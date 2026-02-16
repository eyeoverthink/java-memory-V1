package fraynix.kernel;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ROUND ROBIN SCHEDULER: Baseline fair scheduler.
 * 
 * FIFO queue - simple and predictable.
 */
public class RoundRobinScheduler implements Scheduler {

    private final Queue<FrayProcess> queue = new ConcurrentLinkedQueue<>();
    private final AtomicLong submitted = new AtomicLong();
    private final AtomicLong completed = new AtomicLong();
    private final AtomicLong failed = new AtomicLong();
    private final AtomicLong totalWaitMs = new AtomicLong();
    private final AtomicLong totalExecMs = new AtomicLong();

    @Override
    public String getName() { return "RoundRobin"; }

    @Override
    public void submit(FrayProcess process) {
        process.setState(FrayProcess.State.QUEUED);
        queue.offer(process);
        submitted.incrementAndGet();
    }

    @Override
    public FrayProcess next() {
        return queue.poll();
    }

    @Override
    public void complete(FrayProcess process) {
        completed.incrementAndGet();
        totalWaitMs.addAndGet(process.getWaitTimeMs());
        totalExecMs.addAndGet(process.getExecutionTimeMs());
    }

    @Override
    public void fail(FrayProcess process, String reason) {
        process.setError(reason);
        process.setState(FrayProcess.State.FAILED);
        failed.incrementAndGet();
    }

    @Override
    public List<FrayProcess> getPending() {
        return new ArrayList<>(queue);
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    @Override
    public SchedulerMetrics getMetrics() {
        long comp = completed.get();
        return new SchedulerMetrics(
            submitted.get(),
            comp,
            failed.get(),
            comp > 0 ? totalWaitMs.get() / comp : 0,
            comp > 0 ? totalExecMs.get() / comp : 0,
            queue.size()
        );
    }
}
