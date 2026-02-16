package fraynix.fs;

import java.time.Instant;
import java.util.*;

/**
 * VFILE: Virtual file in FrayFS.
 * 
 * Immutable content with versioning and integrity tracking.
 */
public class VFile {

    private final String id;
    private final String path;
    private final byte[] content;
    private final String hash;
    private final long version;
    private final Instant created;
    private final Instant modified;
    private final Map<String, String> metadata;
    
    // Brain mapping
    private final int[] brainCoordinates;

    public VFile(String path, byte[] content) {
        this(path, content, 1, Instant.now(), Map.of());
    }

    public VFile(String path, byte[] content, long version, Instant modified, Map<String, String> metadata) {
        this.id = UUID.randomUUID().toString();
        this.path = normalizePath(path);
        this.content = content != null ? content.clone() : new byte[0];
        this.hash = computeHash(this.content);
        this.version = version;
        this.created = Instant.now();
        this.modified = modified;
        this.metadata = Map.copyOf(metadata);
        this.brainCoordinates = computeBrainCoordinates(this.path);
    }

    private VFile(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.path = normalizePath(builder.path);
        this.content = builder.content != null ? builder.content.clone() : new byte[0];
        this.hash = computeHash(this.content);
        this.version = builder.version;
        this.created = builder.created;
        this.modified = builder.modified;
        this.metadata = Map.copyOf(builder.metadata);
        this.brainCoordinates = computeBrainCoordinates(this.path);
    }

    public String getId() { return id; }
    public String getPath() { return path; }
    public byte[] getContent() { return content.clone(); }
    public String getHash() { return hash; }
    public long getVersion() { return version; }
    public Instant getCreated() { return created; }
    public Instant getModified() { return modified; }
    public Map<String, String> getMetadata() { return metadata; }
    public int[] getBrainCoordinates() { return brainCoordinates.clone(); }
    public long getSize() { return content.length; }

    public String getName() {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    public String getParentPath() {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash > 0 ? path.substring(0, lastSlash) : "/";
    }

    public String getExtension() {
        String name = getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot >= 0 ? name.substring(lastDot + 1) : "";
    }

    public boolean verifyIntegrity() {
        return hash.equals(computeHash(content));
    }

    public VFile withContent(byte[] newContent) {
        return new Builder()
            .id(id)
            .path(path)
            .content(newContent)
            .version(version + 1)
            .created(created)
            .modified(Instant.now())
            .metadata(metadata)
            .build();
    }

    public VFile withMetadata(String key, String value) {
        Map<String, String> newMeta = new HashMap<>(metadata);
        newMeta.put(key, value);
        return new Builder()
            .id(id)
            .path(path)
            .content(content)
            .version(version)
            .created(created)
            .modified(modified)
            .metadata(newMeta)
            .build();
    }

    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) return "/";
        if (!path.startsWith("/")) path = "/" + path;
        return path.replaceAll("/+", "/");
    }

    private static String computeHash(byte[] data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "error";
        }
    }

    private static int[] computeBrainCoordinates(String path) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(path.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return new int[]{
                (digest[0] & 0xFF) % 8,
                (digest[1] & 0xFF) % 8,
                (digest[2] & 0xFF) % 8,
                (digest[3] & 0xFF) % 8
            };
        } catch (Exception e) {
            int hash = path.hashCode();
            return new int[]{
                Math.abs(hash) % 8,
                Math.abs(hash >> 8) % 8,
                Math.abs(hash >> 16) % 8,
                Math.abs(hash >> 24) % 8
            };
        }
    }

    @Override
    public String toString() {
        return String.format("VFile[%s|v%d|%d bytes|%s]", 
            path, version, content.length, hash.substring(0, 8));
    }

    public static class Builder {
        private String id;
        private String path;
        private byte[] content;
        private long version = 1;
        private Instant created = Instant.now();
        private Instant modified = Instant.now();
        private Map<String, String> metadata = new HashMap<>();

        public Builder id(String id) { this.id = id; return this; }
        public Builder path(String path) { this.path = path; return this; }
        public Builder content(byte[] content) { this.content = content; return this; }
        public Builder version(long version) { this.version = version; return this; }
        public Builder created(Instant created) { this.created = created; return this; }
        public Builder modified(Instant modified) { this.modified = modified; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = new HashMap<>(metadata); return this; }

        public VFile build() {
            return new VFile(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
