package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Append-only audit and replay journal with a rolling Merkle root.
 */
public final class MerkleAppendOnlyJournal {
    private final Path logFile;
    private final List<JournalEntry> entries;

    public MerkleAppendOnlyJournal(Path stateRoot) throws IOException {
        Files.createDirectories(stateRoot);
        this.logFile = stateRoot.resolve("kernel-journal.log");
        this.entries = new ArrayList<>();
        if (Files.exists(logFile)) {
            loadExistingEntries();
        }
    }

    public synchronized JournalEntry append(String type, String message, Map<String, String> metadata) throws IOException {
        long timestamp = System.currentTimeMillis();
        int index = entries.size();
        String previousHash = entries.isEmpty() ? "GENESIS" : entries.get(entries.size() - 1).entryHash();
        String metadataText = serializeMetadata(metadata == null ? Map.of() : metadata);
        String payloadHash = sha256(type + "|" + message + "|" + metadataText);
        String entryHash = sha256(index + "|" + timestamp + "|" + payloadHash + "|" + previousHash);
        List<String> hashes = new ArrayList<>();
        for (JournalEntry entry : entries) {
            hashes.add(entry.entryHash());
        }
        hashes.add(entryHash);
        String merkleRoot = computeMerkleRoot(hashes);
        JournalEntry entry = new JournalEntry(index, timestamp, type, message, metadata == null ? Map.of() : metadata, previousHash, payloadHash, entryHash, merkleRoot);
        entries.add(entry);
        Files.writeString(logFile, encode(entry) + System.lineSeparator(), StandardCharsets.UTF_8,
            Files.exists(logFile) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
        return entry;
    }

    public synchronized List<JournalEntry> getEntries() {
        return List.copyOf(entries);
    }

    public synchronized int size() {
        return entries.size();
    }

    public synchronized String getLastMerkleRoot() {
        return entries.isEmpty() ? "" : entries.get(entries.size() - 1).merkleRoot();
    }

    private void loadExistingEntries() throws IOException {
        for (String line : Files.readAllLines(logFile, StandardCharsets.UTF_8)) {
            if (!line.isBlank()) {
                entries.add(decode(line));
            }
        }
    }

    private String encode(JournalEntry entry) {
        return entry.index()
            + "\t" + entry.timestamp()
            + "\t" + base64(entry.type())
            + "\t" + base64(entry.message())
            + "\t" + base64(serializeMetadata(entry.metadata()))
            + "\t" + entry.previousHash()
            + "\t" + entry.payloadHash()
            + "\t" + entry.entryHash()
            + "\t" + entry.merkleRoot();
    }

    private JournalEntry decode(String line) {
        String[] parts = line.split("\t", 9);
        Map<String, String> metadata = deserializeMetadata(fromBase64(parts[4]));
        return new JournalEntry(
            Integer.parseInt(parts[0]),
            Long.parseLong(parts[1]),
            fromBase64(parts[2]),
            fromBase64(parts[3]),
            metadata,
            parts[5],
            parts[6],
            parts[7],
            parts[8]
        );
    }

    private String serializeMetadata(Map<String, String> metadata) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : new TreeMap<>(metadata).entrySet()) {
            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(entry.getKey()).append('=').append(base64(entry.getValue()));
        }
        return builder.toString();
    }

    private Map<String, String> deserializeMetadata(String serialized) {
        Map<String, String> metadata = new HashMap<>();
        if (serialized == null || serialized.isBlank()) {
            return metadata;
        }
        String[] parts = serialized.split(";");
        for (String part : parts) {
            int separator = part.indexOf('=');
            if (separator > 0) {
                metadata.put(part.substring(0, separator), fromBase64(part.substring(separator + 1)));
            }
        }
        return metadata;
    }

    private String computeMerkleRoot(List<String> hashes) {
        if (hashes.isEmpty()) {
            return "";
        }
        List<String> current = new ArrayList<>(hashes);
        while (current.size() > 1) {
            List<String> next = new ArrayList<>();
            for (int i = 0; i < current.size(); i += 2) {
                String left = current.get(i);
                String right = (i + 1 < current.size()) ? current.get(i + 1) : left;
                next.add(sha256(left + right));
            }
            current = next;
        }
        return current.get(0);
    }

    private String base64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String fromBase64(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash journal entry", e);
        }
    }

    public record JournalEntry(
            int index,
            long timestamp,
            String type,
            String message,
            Map<String, String> metadata,
            String previousHash,
            String payloadHash,
            String entryHash,
            String merkleRoot) {
    }
}
