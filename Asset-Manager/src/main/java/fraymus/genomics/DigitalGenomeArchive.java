package fraymus.genomics;

import fraymus.core.FraymusJSON;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

/** Lossless, content-addressed software-state capsules. DNA encoding is a storage format. */
public final class DigitalGenomeArchive {
    private DigitalGenomeArchive() {}

    public static String sha(byte[] bytes) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }
        catch (Exception error) { throw new IllegalStateException(error); }
    }

    private static Object canonical(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new TreeMap<>();
            map.forEach((k, v) -> result.put(String.valueOf(k), canonical(v)));
            return result;
        }
        if (value instanceof List<?> list) return list.stream().map(DigitalGenomeArchive::canonical).toList();
        return value;
    }

    public static byte[] jsonBytes(Object value) {
        return FraymusJSON.stringify(canonical(value)).getBytes(StandardCharsets.UTF_8);
    }

    public static String encode(byte[] bytes) {
        StringBuilder sequence = new StringBuilder(bytes.length * 4);
        for (byte value : bytes) for (int shift = 6; shift >= 0; shift -= 2)
            sequence.append("ACGT".charAt((value >>> shift) & 3));
        return sequence.toString();
    }

    public static byte[] decode(String sequence) throws IOException {
        if (sequence.length() % 4 != 0) throw new IOException("Incomplete digital DNA byte");
        byte[] bytes = new byte[sequence.length() / 4];
        for (int i = 0; i < sequence.length(); i++) {
            int value = "ACGT".indexOf(sequence.charAt(i));
            if (value < 0) throw new IOException("Invalid digital DNA symbol");
            bytes[i / 4] |= (byte) (value << (6 - 2 * (i % 4)));
        }
        return bytes;
    }

    public static void atomicWrite(Path path, byte[] bytes) throws IOException {
        Files.createDirectories(path.toAbsolutePath().getParent());
        Path temporary = path.resolveSibling("." + UUID.randomUUID() + ".tmp");
        try {
            Files.write(temporary, bytes, StandardOpenOption.CREATE_NEW);
            try { Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException ignored) { Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temporary); }
    }

    private static void immutableWrite(Path path, byte[] bytes) throws IOException {
        if (Files.exists(path)) {
            if (!Arrays.equals(Files.readAllBytes(path), bytes)) throw new IOException("Archive checksum conflict: " + path);
        } else atomicWrite(path, bytes);
    }

    public static synchronized Map<String, Object> save(Path root, String kind, Map<String, Object> payload) throws IOException {
        root = root.toAbsolutePath().normalize();
        if (!kind.matches("[a-z0-9-]+")) throw new IOException("Invalid genome archive kind");
        Files.createDirectories(root);
        try (var channel = FileChannel.open(root.resolve("archive.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             var lock = channel.lock()) {
            byte[] bytes = jsonBytes(Map.of("schema_version", 1, "kind", kind, "payload", payload));
            String key = sha(bytes);
            Path latest = root.resolve("latest-" + kind + ".json");
            Map<String, Object> previous = Files.isRegularFile(latest) ? readMap(latest) : Map.of();
            if (key.equals(previous.get("payload_sha256"))) {
                readPayload(Path.of((String) previous.get("manifest")));
                return previous;
            }
            Path json = root.resolve("payloads/" + key + ".json");
            Path fasta = root.resolve("payloads/" + key + ".fasta");
            String dna = encode(bytes);
            StringBuilder text = new StringBuilder(">FRAYNIX_DIGITAL_GENOME | software state encoding | sha256=" + key + "\n");
            for (int i = 0; i < dna.length(); i += 80) text.append(dna, i, Math.min(i + 80, dna.length())).append('\n');
            byte[] fastaBytes = text.toString().getBytes(StandardCharsets.US_ASCII);
            immutableWrite(json, bytes); immutableWrite(fasta, fastaBytes);
            Map<String, Object> manifest = new LinkedHashMap<>();
            manifest.put("schema_version", 1); manifest.put("kind", kind);
            manifest.put("created_utc", Instant.now().toString());
            manifest.put("parent_manifest_sha256", previous.getOrDefault("manifest_sha256", ""));
            manifest.put("payload_sha256", key); manifest.put("payload", json.toString());
            manifest.put("fasta", fasta.toString()); manifest.put("fasta_sha256", sha(fastaBytes));
            manifest.put("encoding", "UTF-8 JSON bytes; ACGT two-bit symbols; high bits first; four bases per byte");
            manifest.put("biological_function", "not applicable: lossless software-state storage encoding");
            byte[] manifestBytes = jsonBytes(manifest);
            String manifestKey = sha(manifestBytes);
            Path manifestPath = root.resolve("manifests/" + manifestKey + ".json");
            immutableWrite(manifestPath, manifestBytes);
            Map<String, Object> receipt = Map.of("payload_sha256", key, "manifest_sha256", manifestKey,
                "manifest", manifestPath.toString(), "fasta", fasta.toString());
            atomicWrite(latest, jsonBytes(receipt));
            readPayload(manifestPath);
            return receipt;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readMap(Path path) throws IOException {
        path = resolveStoredPath(path);
        Object object = FraymusJSON.parseStrict(Files.readString(path, StandardCharsets.UTF_8));
        if (!(object instanceof Map<?, ?>)) throw new IOException("Expected JSON object: " + path);
        return (Map<String, Object>) object;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readPayload(Path manifestPath) throws IOException {
        manifestPath = resolveStoredPath(manifestPath);
        byte[] raw = Files.readAllBytes(manifestPath);
        if (!manifestPath.getFileName().toString().equals(sha(raw) + ".json")) throw new IOException("Manifest hash mismatch");
        Map<String, Object> manifest = readMap(manifestPath);
        byte[] payload = Files.readAllBytes(resolveStoredPath(Path.of((String) manifest.get("payload"))));
        byte[] fasta = Files.readAllBytes(resolveStoredPath(Path.of((String) manifest.get("fasta"))));
        if (!sha(payload).equals(manifest.get("payload_sha256")) || !sha(fasta).equals(manifest.get("fasta_sha256")))
            throw new IOException("Genome artifact checksum mismatch");
        String dna = new String(fasta, StandardCharsets.US_ASCII).lines().filter(s -> !s.startsWith(">")).collect(java.util.stream.Collectors.joining());
        if (!Arrays.equals(decode(dna), payload)) throw new IOException("Genome DNA round-trip mismatch");
        Object decoded = FraymusJSON.parseStrict(new String(payload, StandardCharsets.UTF_8));
        if (!(decoded instanceof Map<?, ?> envelope) || !(envelope.get("payload") instanceof Map<?, ?> state))
            throw new IOException("Invalid genome payload");
        return (Map<String, Object>) state;
    }

    /** Relocate archived references without rewriting content-addressed manifests or DNA. */
    public static Path resolveStoredPath(Path path) throws IOException {
        String original = System.getProperty("fraynix.originalWorkspace", "");
        String relocated = System.getProperty("fraynix.portableWorkspace", "");
        if (original.isBlank() || relocated.isBlank() || !path.isAbsolute()) return path;
        Path oldRoot = Path.of(original).toAbsolutePath().normalize();
        Path newRoot = Path.of(relocated).toAbsolutePath().normalize();
        Path absolute = path.toAbsolutePath().normalize();
        if (!absolute.startsWith(oldRoot)) return path;
        Path target = newRoot.resolve(oldRoot.relativize(absolute)).normalize();
        if (!target.startsWith(newRoot)) throw new IOException("Archived path leaves portable workspace");
        // Never fall back to the original machine's state when the portable copy is missing.
        return target;
    }

    /** Keep persisted command identities stable when their workspace moves. */
    public static Path identityPath(Path path) {
        String original = System.getProperty("fraynix.originalWorkspace", "");
        String relocated = System.getProperty("fraynix.portableWorkspace", "");
        Path absolute = path.toAbsolutePath().normalize();
        if (original.isBlank() || relocated.isBlank()) return absolute;
        Path newRoot = Path.of(relocated).toAbsolutePath().normalize();
        return absolute.startsWith(newRoot)
            ? Path.of(original).toAbsolutePath().normalize().resolve(newRoot.relativize(absolute)) : absolute;
    }

    public static Map<String, Object> archiveConcept(Path root, String label, long[] vector, String sequence) throws IOException {
        ByteBuffer bytes = ByteBuffer.allocate(vector.length * Long.BYTES);
        for (long word : vector) bytes.putLong(word);
        Map<String, Object> payload = Map.of("category", "concept_vector", "label", label, "vector_base64", Base64.getEncoder().encodeToString(bytes.array()),
                "vector_byte_order", "big-endian signed long64", "sequence", sequence,
                "sequence_sha256", sha(sequence.getBytes(StandardCharsets.US_ASCII)));
        conceptVector(payload);
        return save(root, "concept-" + sha(label.getBytes(StandardCharsets.UTF_8)).substring(0, 16), payload);
    }

    public static long[] conceptVector(Map<String, Object> payload) throws IOException {
        if (!"concept_vector".equals(payload.get("category"))) throw new IOException("Not a concept-vector genome");
        byte[] bytes = Base64.getDecoder().decode((String) payload.get("vector_base64"));
        if (bytes.length != 256 * 8) throw new IOException("Expected the 16384-bit AEON vector");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long[] vector = new long[256]; StringBuilder sequence = new StringBuilder(8192);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = buffer.getLong();
            for (int bit = 0; bit < 64; bit += 2) sequence.append("ACGT".charAt((int) ((vector[i] >>> bit) & 3)));
        }
        if (!sequence.toString().equals(payload.get("sequence"))
                || !sha(sequence.toString().getBytes(StandardCharsets.US_ASCII)).equals(payload.get("sequence_sha256")))
            throw new IOException("Concept vector and sequence differ");
        return vector;
    }
}
