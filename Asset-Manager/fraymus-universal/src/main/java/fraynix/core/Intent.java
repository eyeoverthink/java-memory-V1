package fraynix.core;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * INTENT: The fundamental unit of communication in Fraynix.
 * 
 * Every action, request, or event flows through Intents.
 * This is the "syscall" of the conscious runtime.
 */
public class Intent {

    public enum Type {
        // System operations
        BOOT, SHUTDOWN, HEARTBEAT, HEALTH_CHECK,
        
        // File operations
        FS_READ, FS_WRITE, FS_DELETE, FS_WATCH, FS_INTEGRITY_CHECK,
        
        // Process operations
        PROCESS_SPAWN, PROCESS_KILL, PROCESS_STATUS,
        
        // Genesis operations
        GENESIS_CREATE, GENESIS_BUILD, GENESIS_REVIEW, GENESIS_DEPLOY,
        
        // Repair operations
        REPAIR_DETECT, REPAIR_PLAN, REPAIR_EXECUTE, REPAIR_VERIFY,
        
        // Brain operations
        BRAIN_THINK, BRAIN_DECIDE, BRAIN_DREAM, BRAIN_SNAPSHOT,
        
        // Shell operations
        SHELL_COMMAND, SHELL_QUERY,
        
        // Custom/user-defined
        CUSTOM
    }

    public enum Priority {
        CRITICAL(0),    // Immediate, preempts everything
        HIGH(1),        // Next in queue
        NORMAL(2),      // Standard processing
        LOW(3),         // Background, when idle
        DEFERRED(4);    // Process during DreamState

        private final int level;
        Priority(int level) { this.level = level; }
        public int getLevel() { return level; }
    }

    // Identity
    private final String id;
    private final String traceId;           // For distributed tracing
    private final String parentId;          // For causal chains

    // Classification
    private final Type type;
    private final Priority priority;

    // Payload
    private final Map<String, Object> payload;

    // Timing
    private final Instant created;
    private final Instant deadline;         // null = no deadline

    // Origin
    private final String origin;            // Component that created this
    private final CapabilityToken capability; // Required permissions

    // State
    private IntentState state = IntentState.PENDING;
    private String result;
    private String error;

    public enum IntentState {
        PENDING, QUEUED, PROCESSING, COMPLETED, FAILED, CANCELLED, TIMEOUT
    }

    private Intent(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.traceId = builder.traceId != null ? builder.traceId : this.id;
        this.parentId = builder.parentId;
        this.type = builder.type;
        this.priority = builder.priority;
        this.payload = builder.payload;
        this.created = Instant.now();
        this.deadline = builder.deadline;
        this.origin = builder.origin;
        this.capability = builder.capability;
    }

    // Getters
    public String getId() { return id; }
    public String getTraceId() { return traceId; }
    public String getParentId() { return parentId; }
    public Type getType() { return type; }
    public Priority getPriority() { return priority; }
    public Map<String, Object> getPayload() { return payload; }
    public Instant getCreated() { return created; }
    public Instant getDeadline() { return deadline; }
    public String getOrigin() { return origin; }
    public CapabilityToken getCapability() { return capability; }
    public IntentState getState() { return state; }
    public String getResult() { return result; }
    public String getError() { return error; }

    // State mutations (public for cross-package IntentBus access)
    public void setState(IntentState state) { this.state = state; }
    public void setResult(String result) { this.result = result; this.state = IntentState.COMPLETED; }
    public void setError(String error) { this.error = error; this.state = IntentState.FAILED; }

    public boolean isExpired() {
        return deadline != null && Instant.now().isAfter(deadline);
    }

    public boolean requiresCapability(Capability cap) {
        return capability != null && capability.has(cap);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayloadValue(String key, Class<T> type) {
        Object val = payload.get(key);
        if (val == null) return null;
        if (type.isInstance(val)) return (T) val;
        return null;
    }

    public String getPayloadString(String key) {
        return getPayloadValue(key, String.class);
    }

    // Builder pattern for clean construction
    public static class Builder {
        private String id;
        private String traceId;
        private String parentId;
        private Type type = Type.CUSTOM;
        private Priority priority = Priority.NORMAL;
        private Map<String, Object> payload = Map.of();
        private Instant deadline;
        private String origin = "unknown";
        private CapabilityToken capability;

        public Builder type(Type type) { this.type = type; return this; }
        public Builder priority(Priority priority) { this.priority = priority; return this; }
        public Builder payload(Map<String, Object> payload) { this.payload = payload; return this; }
        public Builder deadline(Instant deadline) { this.deadline = deadline; return this; }
        public Builder origin(String origin) { this.origin = origin; return this; }
        public Builder capability(CapabilityToken cap) { this.capability = cap; return this; }
        public Builder traceId(String traceId) { this.traceId = traceId; return this; }
        public Builder parentId(String parentId) { this.parentId = parentId; return this; }
        public Builder id(String id) { this.id = id; return this; }

        public Intent build() {
            return new Intent(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return String.format("Intent[%s|%s|%s|%s]", type, priority, state, id.substring(0, 8));
    }
}
