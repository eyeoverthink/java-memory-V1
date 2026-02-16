package fraynix.kernel;

import fraynix.core.Intent;
import java.util.List;

/**
 * SCHEDULER: Contract for process/intent scheduling.
 * 
 * Three implementations:
 *   - RoundRobinScheduler (baseline)
 *   - PriorityScheduler (baseline)
 *   - PredictiveBrainScheduler (conscious mode)
 */
public interface Scheduler {

    String getName();

    void submit(FrayProcess process);

    FrayProcess next();

    void complete(FrayProcess process);

    void fail(FrayProcess process, String reason);

    List<FrayProcess> getPending();

    int getQueueSize();

    SchedulerMetrics getMetrics();

    record SchedulerMetrics(
        long submitted,
        long completed,
        long failed,
        long avgWaitMs,
        long avgExecutionMs,
        int currentQueueSize
    ) {}
}
