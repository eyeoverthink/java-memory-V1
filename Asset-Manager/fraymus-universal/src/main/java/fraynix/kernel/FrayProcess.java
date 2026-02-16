package fraynix.kernel;

import fraynix.core.Intent;
import fraynix.core.CapabilityToken;

import java.time.Instant;
import java.util.UUID;

/**
 * FRAY PROCESS: A schedulable unit of work.
 * 
 * Wraps an Intent with execution metadata.
 */
public class FrayProcess {

    public enum State {
        CREATED, QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED
    }

    private final String id;
    private final Intent intent;
    private final CapabilityToken capabilities;
    private final Runnable task;
    
    private State state = State.CREATED;
    private final Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;
    private String result;
    private String error;
    
    // Scheduling hints
    private int priority;
    private long estimatedDurationMs;
    private boolean cpuBound;
    private boolean ioBound;

    public FrayProcess(Intent intent, Runnable task) {
        this(intent, task, null);
    }

    public FrayProcess(Intent intent, Runnable task, CapabilityToken capabilities) {
        this.id = UUID.randomUUID().toString();
        this.intent = intent;
        this.task = task;
        this.capabilities = capabilities;
        this.createdAt = Instant.now();
        this.priority = intent.getPriority().getLevel();
    }

    public String getId() { return id; }
    public Intent getIntent() { return intent; }
    public CapabilityToken getCapabilities() { return capabilities; }
    public Runnable getTask() { return task; }
    public State getState() { return state; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getResult() { return result; }
    public String getError() { return error; }
    public int getPriority() { return priority; }

    public void setState(State state) { this.state = state; }
    public void setStartedAt(Instant time) { this.startedAt = time; }
    public void setCompletedAt(Instant time) { this.completedAt = time; }
    public void setResult(String result) { this.result = result; }
    public void setError(String error) { this.error = error; }
    public void setPriority(int priority) { this.priority = priority; }

    public void setEstimatedDurationMs(long ms) { this.estimatedDurationMs = ms; }
    public long getEstimatedDurationMs() { return estimatedDurationMs; }

    public void setCpuBound(boolean cpuBound) { this.cpuBound = cpuBound; }
    public boolean isCpuBound() { return cpuBound; }

    public void setIoBound(boolean ioBound) { this.ioBound = ioBound; }
    public boolean isIoBound() { return ioBound; }

    public long getWaitTimeMs() {
        if (startedAt == null) {
            return java.time.Duration.between(createdAt, Instant.now()).toMillis();
        }
        return java.time.Duration.between(createdAt, startedAt).toMillis();
    }

    public long getExecutionTimeMs() {
        if (startedAt == null) return 0;
        Instant end = completedAt != null ? completedAt : Instant.now();
        return java.time.Duration.between(startedAt, end).toMillis();
    }

    public void run() {
        state = State.RUNNING;
        startedAt = Instant.now();
        try {
            task.run();
            state = State.COMPLETED;
            completedAt = Instant.now();
        } catch (Exception e) {
            state = State.FAILED;
            error = e.getMessage();
            completedAt = Instant.now();
            throw e;
        }
    }

    @Override
    public String toString() {
        return String.format("Process[%s|%s|%s|pri=%d]", 
            id.substring(0, 8), intent.getType(), state, priority);
    }
}
