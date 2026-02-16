package fraynix.core;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * ARTIFACT: Build outputs from Genesis.
 * 
 * Every generated artifact is tracked, signed, and verifiable.
 * Artifacts can be JARs, scripts, configs, or entire applications.
 */
public record Artifact(
    String id,
    String name,
    ArtifactType type,
    Path path,
    
    // Integrity
    String hash,            // SHA-256
    String signature,       // HMAC signature
    long sizeBytes,
    
    // Provenance
    String blueprintId,     // Genesis blueprint that created this
    String createdBy,       // Component that built it
    Instant createdAt,
    
    // Dependencies
    List<String> dependencies,
    
    // Manifest
    Map<String, String> manifest,
    
    // Status
    ArtifactStatus status
) {
    
    public enum ArtifactType {
        JAR,
        SCRIPT,
        CONFIG,
        SOURCE,
        BINARY,
        MANIFEST,
        BUNDLE,
        TEST,
        DOCS
    }

    public enum ArtifactStatus {
        BUILDING,
        BUILT,
        VERIFIED,
        DEPLOYED,
        FAILED,
        CORRUPTED,
        ARCHIVED
    }

    public boolean isVerified() {
        return status == ArtifactStatus.VERIFIED || status == ArtifactStatus.DEPLOYED;
    }

    public boolean isDeployable() {
        return status == ArtifactStatus.VERIFIED;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = java.util.UUID.randomUUID().toString();
        private String name;
        private ArtifactType type = ArtifactType.SOURCE;
        private Path path;
        private String hash;
        private String signature;
        private long sizeBytes;
        private String blueprintId;
        private String createdBy = "genesis";
        private Instant createdAt = Instant.now();
        private List<String> dependencies = List.of();
        private Map<String, String> manifest = Map.of();
        private ArtifactStatus status = ArtifactStatus.BUILDING;

        public Builder name(String name) { this.name = name; return this; }
        public Builder type(ArtifactType type) { this.type = type; return this; }
        public Builder path(Path path) { this.path = path; return this; }
        public Builder hash(String hash) { this.hash = hash; return this; }
        public Builder signature(String signature) { this.signature = signature; return this; }
        public Builder sizeBytes(long size) { this.sizeBytes = size; return this; }
        public Builder blueprintId(String id) { this.blueprintId = id; return this; }
        public Builder createdBy(String by) { this.createdBy = by; return this; }
        public Builder dependencies(List<String> deps) { this.dependencies = deps; return this; }
        public Builder manifest(Map<String, String> m) { this.manifest = m; return this; }
        public Builder status(ArtifactStatus s) { this.status = s; return this; }

        public Artifact build() {
            return new Artifact(
                id, name, type, path, hash, signature, sizeBytes,
                blueprintId, createdBy, createdAt, dependencies, manifest, status
            );
        }
    }

    public Artifact withStatus(ArtifactStatus newStatus) {
        return new Artifact(
            id, name, type, path, hash, signature, sizeBytes,
            blueprintId, createdBy, createdAt, dependencies, manifest, newStatus
        );
    }

    public Artifact withHash(String newHash) {
        return new Artifact(
            id, name, type, path, newHash, signature, sizeBytes,
            blueprintId, createdBy, createdAt, dependencies, manifest, status
        );
    }

    public Artifact withSignature(String newSig) {
        return new Artifact(
            id, name, type, path, hash, newSig, sizeBytes,
            blueprintId, createdBy, createdAt, dependencies, manifest, status
        );
    }
}
