package fraymus.core;

import java.util.UUID;

/**
 * INTENT - Core contract for all system requests
 * 
 * Replaces traditional syscalls with intent-based execution.
 * Every action in Fraynix starts as an Intent.
 */
public class Intent {
    
    public enum Type {
        // Execution
        EXECUTE_PROCESS,
        SCHEDULE_TASK,
        KILL_PROCESS,
        
        // Filesystem
        FS_READ,
        FS_WRITE,
        FS_DELETE,
        FS_WATCH,
        
        // Network
        NET_CONNECT,
        NET_LISTEN,
        NET_SEND,
        
        // Genesis
        CREATE_SOFTWARE,
        BUILD_ARTIFACT,
        DEPLOY_APP,
        
        // Repair
        DETECT_CORRUPTION,
        REPAIR_FILE,
        VERIFY_INTEGRITY,
        
        // System
        OPTIMIZE,
        DREAM,
        SHUTDOWN
    }
    
    public enum Priority {
        CRITICAL(0),
        HIGH(1),
        NORMAL(2),
        LOW(3),
        BACKGROUND(4);
        
        public final int level;
        Priority(int l) { this.level = l; }
    }
    
    // Identity
    public final String id;
    public final Type type;
    public final String origin;
    
    // Content
    public final String payload;
    public final Object data;
    
    // Scheduling
    public final Priority priority;
    public final long deadline;  // Unix timestamp (0 = no deadline)
    
    // Tracing
    public final long createdAt;
    public final String traceId;
    
    private Intent(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.origin = builder.origin;
        this.payload = builder.payload;
        this.data = builder.data;
        this.priority = builder.priority;
        this.deadline = builder.deadline;
        this.createdAt = System.currentTimeMillis();
        this.traceId = builder.traceId != null ? builder.traceId : UUID.randomUUID().toString();
    }
    
    public static Builder builder(Type type) {
        return new Builder(type);
    }
    
    public static class Builder {
        private String id = UUID.randomUUID().toString();
        private final Type type;
        private String origin = "unknown";
        private String payload = "";
        private Object data = null;
        private Priority priority = Priority.NORMAL;
        private long deadline = 0;
        private String traceId = null;
        
        public Builder(Type type) {
            this.type = type;
        }
        
        public Builder id(String id) { this.id = id; return this; }
        public Builder origin(String origin) { this.origin = origin; return this; }
        public Builder payload(String payload) { this.payload = payload; return this; }
        public Builder data(Object data) { this.data = data; return this; }
        public Builder priority(Priority priority) { this.priority = priority; return this; }
        public Builder deadline(long deadline) { this.deadline = deadline; return this; }
        public Builder traceId(String traceId) { this.traceId = traceId; return this; }
        
        public Intent build() {
            return new Intent(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format("Intent[%s, type=%s, priority=%s, origin=%s, trace=%s]",
            id.substring(0, 8), type, priority, origin, traceId.substring(0, 8));
    }
}
