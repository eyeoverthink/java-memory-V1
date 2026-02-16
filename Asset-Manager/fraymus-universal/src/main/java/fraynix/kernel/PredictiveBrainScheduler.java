package fraynix.kernel;

import fraynix.brain.HyperTesseract;
import fraynix.core.Intent;
import fraynix.core.Policy;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PREDICTIVE BRAIN SCHEDULER: The "conscious" mode scheduler.
 * 
 * Uses HyperTesseract brain to make scheduling decisions based on:
 *   - System state (CPU, memory, IO)
 *   - Process characteristics (CPU-bound, IO-bound)
 *   - Historical performance
 *   - Brain predictions
 */
public class PredictiveBrainScheduler implements Scheduler {

    private final HyperTesseract brain;
    private final PriorityBlockingQueue<ScoredProcess> queue;
    private final AtomicLong submitted = new AtomicLong();
    private final AtomicLong completed = new AtomicLong();
    private final AtomicLong failed = new AtomicLong();
    private final AtomicLong totalWaitMs = new AtomicLong();
    private final AtomicLong totalExecMs = new AtomicLong();
    private final AtomicLong brainDecisions = new AtomicLong();

    public PredictiveBrainScheduler(HyperTesseract brain) {
        this.brain = brain;
        this.queue = new PriorityBlockingQueue<>(100,
            Comparator.comparingDouble((ScoredProcess sp) -> sp.score).reversed()
                      .thenComparing(sp -> sp.process.getCreatedAt()));
    }

    @Override
    public String getName() { return "PredictiveBrain"; }

    @Override
    public void submit(FrayProcess process) {
        process.setState(FrayProcess.State.QUEUED);
        
        // Inject thought into brain
        brain.injectThought(
            "SCHEDULE: " + process.getIntent().getType() + " pri=" + process.getPriority(),
            0.3
        );
        
        // Calculate brain-augmented score
        double score = calculateScore(process);
        
        queue.offer(new ScoredProcess(process, score));
        submitted.incrementAndGet();
    }

    private double calculateScore(FrayProcess process) {
        // Base score from priority (0-4, inverted so lower = better)
        double baseScore = 1.0 - (process.getPriority() / 5.0);
        
        // Ask brain for decision
        Policy.Decision<String> decision = brain.decide(process.getIntent());
        brainDecisions.incrementAndGet();
        
        // Adjust score based on brain confidence
        double brainBoost = decision.confidence() * 0.3;
        
        // Consider process characteristics
        double charBoost = 0;
        if (process.isCpuBound() && brain.captureState().getCpuUsage() < 0.5) {
            charBoost = 0.1; // Good time for CPU work
        }
        if (process.isIoBound() && brain.captureState().getCpuUsage() > 0.8) {
            charBoost = 0.15; // IO won't compete for CPU
        }
        
        // Consider wait time (aging)
        double waitBoost = Math.min(0.2, process.getWaitTimeMs() / 10000.0);
        
        return baseScore + brainBoost + charBoost + waitBoost;
    }

    @Override
    public FrayProcess next() {
        ScoredProcess sp = queue.poll();
        return sp != null ? sp.process : null;
    }

    @Override
    public void complete(FrayProcess process) {
        completed.incrementAndGet();
        totalWaitMs.addAndGet(process.getWaitTimeMs());
        totalExecMs.addAndGet(process.getExecutionTimeMs());
        
        // Feedback to brain
        brain.injectThought(
            "COMPLETED: " + process.getIntent().getType() + 
            " exec=" + process.getExecutionTimeMs() + "ms",
            0.1
        );
    }

    @Override
    public void fail(FrayProcess process, String reason) {
        process.setError(reason);
        process.setState(FrayProcess.State.FAILED);
        failed.incrementAndGet();
        
        // Negative feedback to brain
        brain.injectThought(
            "FAILED: " + process.getIntent().getType() + " reason=" + reason,
            0.5
        );
    }

    @Override
    public List<FrayProcess> getPending() {
        List<FrayProcess> result = new ArrayList<>();
        for (ScoredProcess sp : queue) {
            result.add(sp.process);
        }
        return result;
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

    public long getBrainDecisions() {
        return brainDecisions.get();
    }

    private record ScoredProcess(FrayProcess process, double score) {}
}
