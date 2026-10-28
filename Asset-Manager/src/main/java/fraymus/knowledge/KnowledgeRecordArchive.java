package fraymus.knowledge;

import fraymus.core.FraymusJSON;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.*;

/** Immutable per-record metadata and content-addressed UTF-8 payloads, independent of blocks.dat. */
public final class KnowledgeRecordArchive {
    private final Path root;
    public KnowledgeRecordArchive(Path root) { this.root = root; }
    public static String sha(byte[] bytes) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }
        catch (Exception failure) { throw new IllegalStateException(failure); }
    }
    public void save(AkashicRecord.KnowledgeBlock block) throws IOException {
        byte[] content = block.content.getBytes(StandardCharsets.UTF_8);
        String digest = sha(content);
        Path payload = root.resolve("payloads/" + digest + ".gz");
        if (!Files.exists(payload)) {
            Files.createDirectories(payload.getParent());
            Path temp = Files.createTempFile(payload.getParent(), "record-", ".pending");
            try {
                try (var output = new GZIPOutputStream(Files.newOutputStream(temp))) { output.write(content); }
                move(temp, payload);
            } finally { Files.deleteIfExists(temp); }
        } else verifyPayload(payload, digest, content.length);
        Map<String,Object> metadata = Map.of("format", "fraynix.knowledge-record.v1", "hash", block.hash,
            "category", block.category, "timestamp", block.timestamp, "formatted_time",block.formattedTime,"content_sha256", digest, "content_bytes", (long) content.length);
        Path record = root.resolve("records/" + block.hash.substring(2) + ".json");
        if (Files.exists(record)) {
            if (!read(record).equals(metadata)) throw new IOException("Knowledge identity collision: " + block.hash);
        } else atomic(record, FraymusJSON.stringify(metadata));
    }
    public List<Path> records() throws IOException {
        Path directory = root.resolve("records");
        if (!Files.isDirectory(directory)) return List.of();
        try (var files = Files.list(directory)) { return files.filter(p -> p.toString().endsWith(".json")).sorted().toList(); }
    }
    private static void verifyPayload(Path payload, String expected, long expectedBytes) throws IOException {
        try {
            var digest=MessageDigest.getInstance("SHA-256");long bytes=0;
            try(var input=new GZIPInputStream(Files.newInputStream(payload))) {
                byte[] buffer=new byte[1024*1024];int count;
                while((count=input.read(buffer))!=-1) {digest.update(buffer,0,count);bytes+=count;}
            }
            if(bytes!=expectedBytes || !HexFormat.of().formatHex(digest.digest()).equals(expected))throw new IOException("Existing knowledge payload checksum mismatch");
        } catch(java.security.NoSuchAlgorithmException impossible) {throw new IllegalStateException(impossible);}
    }
    @SuppressWarnings("unchecked") public static Map<String,Object> read(Path path) throws IOException {
        Object data = FraymusJSON.parseStrict(Files.readString(path));
        if (!(data instanceof Map)) throw new IOException("Expected record metadata");
        return (Map<String,Object>) data;
    }
    public AkashicRecord.KnowledgeBlock load(Path record) throws IOException {
        Map<String,Object> meta = read(record);
        if (!"fraynix.knowledge-record.v1".equals(meta.get("format"))) throw new IOException("Unknown record archive version");
        String digest = String.valueOf(meta.get("content_sha256"));
        if (!digest.matches("[a-f0-9]{64}")) throw new IOException("Invalid payload identity");
        byte[] content;
        try (var input = new GZIPInputStream(Files.newInputStream(root.resolve("payloads/" + digest + ".gz")))) { content = input.readAllBytes(); }
        if (!sha(content).equals(digest) || content.length != ((Number) meta.get("content_bytes")).longValue()) throw new IOException("Knowledge payload checksum mismatch");
        try { return AkashicRecord.KnowledgeBlock.restore((String) meta.get("category"), new String(content, StandardCharsets.UTF_8),
            ((Number) meta.get("timestamp")).longValue(), (String) meta.get("hash"),(String)meta.get("formatted_time")); }
        catch (IllegalArgumentException invalid) { throw new IOException("Knowledge record hash mismatch", invalid); }
    }
    public static void atomic(Path file, String data) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        Path temp = Files.createTempFile(file.toAbsolutePath().getParent(), "knowledge-", ".pending");
        try { Files.writeString(temp, data, StandardCharsets.UTF_8); move(temp, file); }
        finally { Files.deleteIfExists(temp); }
    }
    private static void move(Path source, Path target) throws IOException {
        try { Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
        catch (AtomicMoveNotSupportedException ignored) { Files.move(source, target, StandardCopyOption.REPLACE_EXISTING); }
    }
    public void purge() throws IOException { deleteTree(root); }
    public static void deleteTree(Path directory)throws IOException {
        Path absolute=directory.toAbsolutePath().normalize();
        if(!Files.exists(absolute))return;
        try(var paths=Files.walk(absolute)) {
            for(Path path:paths.sorted(Comparator.reverseOrder()).toList()) {
                if(!path.toAbsolutePath().normalize().startsWith(absolute))throw new IOException("Archive deletion escaped its root");
                Files.deleteIfExists(path);
            }
        }
    }
}
