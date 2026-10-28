package brain_architecture.kernel;

/**
 * Resource limits for a task run.
 */
public final class TaskBudget {
    private final int maxSteps;
    private final int maxToolCalls;
    private final int maxFilesToRead;
    private final long maxRuntimeMs;

    public TaskBudget(int maxSteps, int maxToolCalls, int maxFilesToRead, long maxRuntimeMs) {
        this.maxSteps = Math.max(1, maxSteps);
        this.maxToolCalls = Math.max(1, maxToolCalls);
        this.maxFilesToRead = Math.max(1, maxFilesToRead);
        this.maxRuntimeMs = Math.max(1L, maxRuntimeMs);
    }

    public static TaskBudget standard() {
        return new TaskBudget(8, 12, 6, 60_000L);
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public int getMaxToolCalls() {
        return maxToolCalls;
    }

    public int getMaxFilesToRead() {
        return maxFilesToRead;
    }

    public long getMaxRuntimeMs() {
        return maxRuntimeMs;
    }
}
