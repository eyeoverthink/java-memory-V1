package fraynix.core;

/**
 * CAPABILITY: Security backbone for Fraynix.
 * 
 * Every privileged action requires a capability.
 * This prevents "self-modifying OS" from becoming "self-owning OS."
 */
public enum Capability {
    // Filesystem
    FS_READ("Read files from disk"),
    FS_WRITE("Write files to disk"),
    FS_DELETE("Delete files from disk"),
    FS_EXECUTE("Execute files"),
    
    // Network
    NET_CONNECT("Open outbound connections"),
    NET_LISTEN("Listen on ports"),
    NET_DNS("Resolve DNS"),
    
    // Process
    PROC_SPAWN("Spawn new processes"),
    PROC_KILL("Kill processes"),
    PROC_SIGNAL("Send signals to processes"),
    
    // System
    SYS_INFO("Read system information"),
    SYS_CONFIG("Modify system configuration"),
    SYS_SHUTDOWN("Shutdown the system"),
    
    // Genesis (code generation)
    BUILD("Compile and build artifacts"),
    GENESIS_CREATE("Create new code/blueprints"),
    GENESIS_MUTATE("Modify existing code"),
    GENESIS_DEPLOY("Deploy artifacts to /sys/apps"),
    
    // Repair (self-healing)
    REPAIR_DETECT("Detect corruption/issues"),
    REPAIR_PLAN("Plan repair actions"),
    REPAIR_EXECUTE("Execute repair actions"),
    REPAIR_VERIFY("Verify repair success"),
    
    // Brain (consciousness)
    BRAIN_READ("Read brain state"),
    BRAIN_WRITE("Modify brain state"),
    BRAIN_SNAPSHOT("Create brain snapshots"),
    
    // Admin
    ADMIN("Full administrative access"),
    GRANT_CAPABILITY("Grant capabilities to others");

    private final String description;

    Capability(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrivileged() {
        return this == ADMIN || 
               this == GRANT_CAPABILITY || 
               this == GENESIS_MUTATE ||
               this == REPAIR_EXECUTE ||
               this == SYS_SHUTDOWN ||
               this == BRAIN_WRITE;
    }

    public boolean isDestructive() {
        return this == FS_DELETE ||
               this == PROC_KILL ||
               this == SYS_SHUTDOWN ||
               this == GENESIS_MUTATE ||
               this == REPAIR_EXECUTE;
    }
}
