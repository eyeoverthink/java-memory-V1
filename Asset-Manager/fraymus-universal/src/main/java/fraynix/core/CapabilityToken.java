package fraynix.core;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * CAPABILITY TOKEN: A verified set of permissions.
 * 
 * Tokens are issued by the kernel and required for privileged actions.
 * They can expire and be revoked.
 */
public class CapabilityToken {

    private final String id;
    private final String issuedTo;           // Component/agent name
    private final Set<Capability> capabilities;
    private final Instant issuedAt;
    private final Instant expiresAt;         // null = never expires
    private final String issuedBy;           // Issuing authority
    private boolean revoked = false;

    private CapabilityToken(Builder builder) {
        this.id = UUID.randomUUID().toString();
        this.issuedTo = builder.issuedTo;
        this.capabilities = Collections.unmodifiableSet(builder.capabilities);
        this.issuedAt = Instant.now();
        this.expiresAt = builder.expiresAt;
        this.issuedBy = builder.issuedBy;
    }

    public String getId() { return id; }
    public String getIssuedTo() { return issuedTo; }
    public Set<Capability> getCapabilities() { return capabilities; }
    public Instant getIssuedAt() { return issuedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public String getIssuedBy() { return issuedBy; }
    public boolean isRevoked() { return revoked; }

    public boolean has(Capability cap) {
        if (revoked || isExpired()) return false;
        return capabilities.contains(cap) || capabilities.contains(Capability.ADMIN);
    }

    public boolean hasAll(Capability... caps) {
        for (Capability cap : caps) {
            if (!has(cap)) return false;
        }
        return true;
    }

    public boolean hasAny(Capability... caps) {
        for (Capability cap : caps) {
            if (has(cap)) return true;
        }
        return false;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    void revoke() {
        this.revoked = true;
    }

    public static class Builder {
        private String issuedTo = "anonymous";
        private Set<Capability> capabilities = EnumSet.noneOf(Capability.class);
        private Instant expiresAt;
        private String issuedBy = "kernel";

        public Builder issuedTo(String issuedTo) { 
            this.issuedTo = issuedTo; 
            return this; 
        }
        
        public Builder capability(Capability cap) { 
            this.capabilities.add(cap); 
            return this; 
        }
        
        public Builder capabilities(Set<Capability> caps) { 
            this.capabilities.addAll(caps); 
            return this; 
        }
        
        public Builder expiresAt(Instant expiresAt) { 
            this.expiresAt = expiresAt; 
            return this; 
        }
        
        public Builder expiresIn(java.time.Duration duration) {
            this.expiresAt = Instant.now().plus(duration);
            return this;
        }
        
        public Builder issuedBy(String issuedBy) { 
            this.issuedBy = issuedBy; 
            return this; 
        }

        public CapabilityToken build() {
            return new CapabilityToken(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Convenience factory methods
    public static CapabilityToken readOnly(String issuedTo) {
        return builder()
            .issuedTo(issuedTo)
            .capability(Capability.FS_READ)
            .capability(Capability.SYS_INFO)
            .capability(Capability.BRAIN_READ)
            .build();
    }

    public static CapabilityToken genesis(String issuedTo) {
        return builder()
            .issuedTo(issuedTo)
            .capability(Capability.FS_READ)
            .capability(Capability.FS_WRITE)
            .capability(Capability.BUILD)
            .capability(Capability.GENESIS_CREATE)
            .capability(Capability.GENESIS_DEPLOY)
            .build();
    }

    public static CapabilityToken repair(String issuedTo) {
        return builder()
            .issuedTo(issuedTo)
            .capability(Capability.FS_READ)
            .capability(Capability.FS_WRITE)
            .capability(Capability.REPAIR_DETECT)
            .capability(Capability.REPAIR_PLAN)
            .capability(Capability.REPAIR_EXECUTE)
            .capability(Capability.REPAIR_VERIFY)
            .build();
    }

    public static CapabilityToken admin(String issuedTo) {
        return builder()
            .issuedTo(issuedTo)
            .capability(Capability.ADMIN)
            .issuedBy("root")
            .build();
    }

    @Override
    public String toString() {
        return String.format("Token[%s→%s|caps=%d|%s]", 
            issuedBy, issuedTo, capabilities.size(),
            isValid() ? "valid" : (revoked ? "revoked" : "expired"));
    }
}
