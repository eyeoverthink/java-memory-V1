package fraymus.factory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/** Versioned, checksummed checkpoints. A valid previous generation remains recoverable. */
public final class WorldCheckpointStore {
    private static final int MAGIC = 0x4657524C;
    private static final int MAX_BYTES = 256 * 1024 * 1024;
    private static final int MAX_RECORDS = 1_000_000;
    private final Path file;
    public record Loaded(WorldSnapshot snapshot, boolean recoveredPrevious) {}

    public WorldCheckpointStore(Path file) { this.file = file.toAbsolutePath().normalize(); }
    public Path path() { return file; }
    private Path previous() { return file.resolveSibling(file.getFileName() + ".previous"); }

    public void save(WorldSnapshot snapshot, SchemaRegistry schemas) throws IOException {
        FactoryWorld.validate(snapshot, schemas);
        byte[] data = encode(snapshot);
        Files.createDirectories(file.getParent());
        Path lock = file.resolveSibling(file.getFileName() + ".lock");
        try (FileChannel channel = FileChannel.open(lock, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             var held = channel.lock()) {
            if (Files.isRegularFile(file)) {
                boolean valid = false;
                try {
                    read(file, schemas);
                    valid = true;
                } catch (IOException | IllegalArgumentException corrupt) {
                    // Preserve an existing valid previous generation when the current one is corrupt.
                }
                if (valid) writeAtomic(previous(), Files.readAllBytes(file));
            }
            writeAtomic(file, data);
        }
    }

    public Loaded load(SchemaRegistry schemas) throws IOException {
        try { return new Loaded(read(file, schemas), false); }
        catch (IOException | IllegalArgumentException current) {
            try { return new Loaded(read(previous(), schemas), true); }
            catch (IOException | IllegalArgumentException old) {
                IOException error = new IOException("No valid checkpoint: " + file, current);
                error.addSuppressed(old);
                throw error;
            }
        }
    }

    private static byte[] encode(WorldSnapshot snapshot) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(body)) {
            out.writeLong(snapshot.revision()); out.writeInt(snapshot.entities().size());
            for (EntityRecord entity : snapshot.entities()) {
                string(out, entity.id().toString()); string(out, entity.name()); out.writeInt(entity.components().size());
                for (ComponentRecord component : entity.components()) {
                    string(out, component.id().toString()); string(out, component.type()); out.writeInt(component.version());
                    out.writeInt(component.values().size());
                    for (var field : new TreeMap<>(component.values()).entrySet()) { string(out, field.getKey()); string(out, field.getValue()); }
                }
            }
        }
        byte[] payload = body.toByteArray();
        if (payload.length > MAX_BYTES - 44) throw new IOException("Checkpoint exceeds supported size");
        ByteArrayOutputStream envelope = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(envelope)) {
            out.writeInt(MAGIC); out.writeInt(1); out.writeInt(payload.length); out.write(digest(payload)); out.write(payload);
        }
        return envelope.toByteArray();
    }

    private static WorldSnapshot read(Path path, SchemaRegistry schemas) throws IOException {
        long size = Files.size(path);
        if (size < 44 || size > MAX_BYTES) throw new IOException("Invalid checkpoint size");
        try (DataInputStream in = new DataInputStream(Files.newInputStream(path))) {
            if (in.readInt() != MAGIC || in.readInt() != 1) throw new IOException("Unsupported checkpoint format");
            int length = in.readInt();
            if (length != size - 44) throw new IOException("Truncated checkpoint");
            byte[] expected = in.readNBytes(32), payload = in.readNBytes(length);
            if (!MessageDigest.isEqual(expected, digest(payload))) throw new IOException("Checkpoint checksum mismatch");
            try (DataInputStream body = new DataInputStream(new ByteArrayInputStream(payload))) {
                long revision = body.readLong(); int n = count(body);
                List<EntityRecord> entities = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    UUID id = UUID.fromString(string(body)); String name = string(body); int cn = count(body);
                    List<ComponentRecord> components = new ArrayList<>();
                    for (int j = 0; j < cn; j++) {
                        UUID cid = UUID.fromString(string(body)); String type = string(body); int version = body.readInt();
                        int fn = count(body); Map<String, String> values = new LinkedHashMap<>();
                        for (int k = 0; k < fn; k++) {
                            if (values.put(string(body), string(body)) != null) throw new IOException("Duplicate field");
                        }
                        components.add(new ComponentRecord(cid, type, version, values));
                    }
                    entities.add(new EntityRecord(id, name, components));
                }
                if (body.available() != 0) throw new IOException("Trailing checkpoint data");
                WorldSnapshot snapshot = new WorldSnapshot(revision, entities);
                FactoryWorld.validate(snapshot, schemas);
                return snapshot;
            }
        }
    }
    private static int count(DataInputStream in) throws IOException {
        int value = in.readInt(); if (value < 0 || value > MAX_RECORDS) throw new IOException("Invalid record count"); return value;
    }
    private static void string(DataOutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8); out.writeInt(bytes.length); out.write(bytes);
    }
    private static String string(DataInputStream in) throws IOException {
        int size = in.readInt();
        if (size < 0 || size > in.available()) throw new IOException("Invalid string length");
        return new String(in.readNBytes(size), StandardCharsets.UTF_8);
    }
    private static byte[] digest(byte[] payload) {
        try { return MessageDigest.getInstance("SHA-256").digest(payload); }
        catch (NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }
    private static void writeAtomic(Path target, byte[] bytes) throws IOException {
        Path temp = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".pending");
        try {
            try (FileChannel out = FileChannel.open(temp, StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()) out.write(buffer);
                out.force(true);
            }
            try { Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException unsupported) { throw new IOException("Atomic replacement required on this filesystem", unsupported); }
        } finally { Files.deleteIfExists(temp); }
    }
}
