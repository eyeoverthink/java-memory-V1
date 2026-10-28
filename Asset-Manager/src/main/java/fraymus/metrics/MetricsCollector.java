package fraymus.metrics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MetricsCollector - runtime evidence layer for Fraynix.
 *
 * Tracks boot latency, routing decisions, memory recall,
 * compile success, task completion, and subsystem health.
 */
public class MetricsCollector {

    private static final double PHI = 1.6180339887;

    // Boot metrics
    private final Map<String, Long> phaseStartMs = new LinkedHashMap<>();
    private final Map<String, Long> phaseLatencyMs = new LinkedHashMap<>();
    private long totalBootMs = 0;
    private long bootStartMs = 0;

    // Routing metrics
    private final Map<String, AtomicLong> routingHits = new ConcurrentHashMap<>();
    private long totalQueries = 0;
    private long successfulRoutes = 0;
    private long failedRoutes = 0;

    // Memory recall metrics
    private long totalRecalls = 0;
    private long successfulRecalls = 0;
    private long cacheHits = 0;
    private double avgRecallMs = 0;

    // Compile/build metrics
    private long compilesAttempted = 0;
    private long compilesSucceeded = 0;
    private long compilationErrors = 0;

    // Task completion metrics
    private long tasksStarted = 0;
    private long tasksCompleted = 0;
    private long tasksFailed = 0;

    // Subsystem health
    private final Map<String, SubsystemHealth> subsystems = new ConcurrentHashMap<>();

    // ---------------------------------------------------------------------
    // Boot tracking
    // ---------------------------------------------------------------------

    public void bootStart() {
        bootStartMs = System.currentTimeMillis();
    }

    public void phaseStart(String phase) {
        phaseStartMs.put(phase, System.currentTimeMillis());
    }

    public void phaseEnd(String phase) {
        Long start = phaseStartMs.get(phase);
        if (start != null) {
            long latency = System.currentTimeMillis() - start;
            phaseLatencyMs.put(phase, latency);
        }
    }

    public void bootComplete() {
        totalBootMs = System.currentTimeMillis() - bootStartMs;
    }

    // ---------------------------------------------------------------------
    // Routing tracking
    // ---------------------------------------------------------------------

    public void recordRoutingDecision(String expert, boolean success) {
        totalQueries++;
        routingHits.computeIfAbsent(expert, k -> new AtomicLong()).incrementAndGet();
        if (success) {
            successfulRoutes++;
        } else {
            failedRoutes++;
        }
    }

    public double getRoutingAccuracy() {
        if (totalQueries == 0) {
            return 0.0;
        }
        return (successfulRoutes * 100.0) / totalQueries;
    }

    // ---------------------------------------------------------------------
    // Memory recall tracking
    // ---------------------------------------------------------------------

    public void recordMemoryRecall(boolean hit, long latencyMs, boolean cacheHit) {
        totalRecalls++;
        if (hit) {
            successfulRecalls++;
        }
        if (cacheHit) {
            cacheHits++;
        }
        avgRecallMs = ((avgRecallMs * (totalRecalls - 1)) + latencyMs) / totalRecalls;
    }

    public double getRecallPrecision() {
        if (totalRecalls == 0) {
            return 0.0;
        }
        return (successfulRecalls * 100.0) / totalRecalls;
    }

    // ---------------------------------------------------------------------
    // Compile/build tracking
    // ---------------------------------------------------------------------

    public void recordCompile(boolean success) {
        compilesAttempted++;
        if (success) {
            compilesSucceeded++;
        } else {
            compilationErrors++;
        }
    }

    public double getCompileSuccessRate() {
        if (compilesAttempted == 0) {
            return 0.0;
        }
        return (compilesSucceeded * 100.0) / compilesAttempted;
    }

    // ---------------------------------------------------------------------
    // Task completion tracking
    // ---------------------------------------------------------------------

    public void taskStarted() {
        tasksStarted++;
    }

    public void taskCompleted() {
        tasksCompleted++;
    }

    public void taskFailed() {
        tasksFailed++;
    }

    public double getTaskCompletionRate() {
        if (tasksStarted == 0) {
            return 0.0;
        }
        return (tasksCompleted * 100.0) / tasksStarted;
    }

    // ---------------------------------------------------------------------
    // Subsystem health
    // ---------------------------------------------------------------------

    public void registerSubsystem(String name) {
        subsystems.put(name, new SubsystemHealth(name));
    }

    public void subsystemSuccess(String name, long latencyMs) {
        SubsystemHealth h = subsystems.computeIfAbsent(name, SubsystemHealth::new);
        h.recordSuccess(latencyMs);
    }

    public void subsystemFailure(String name) {
        SubsystemHealth h = subsystems.computeIfAbsent(name, SubsystemHealth::new);
        h.recordFailure();
    }

    // ---------------------------------------------------------------------
    // Report + coherence
    // ---------------------------------------------------------------------

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n===============================================================\n");
        sb.append("FRAYNIX RUNTIME METRICS REPORT\n");
        sb.append("===============================================================\n");

        sb.append("\n-- BOOT -------------------------------------------------------\n");
        sb.append(String.format("   Total boot time: %dms\n", totalBootMs));
        phaseLatencyMs.forEach((phase, ms) ->
            sb.append(String.format("   %-35s %dms\n", phase + ":", ms))
        );

        sb.append("\n-- ROUTING ----------------------------------------------------\n");
        sb.append(String.format("   Total queries:    %d\n", totalQueries));
        sb.append(String.format("   Routing accuracy: %.1f%%\n", getRoutingAccuracy()));
        sb.append(String.format("   Successful:       %d  |  Failed: %d\n", successfulRoutes, failedRoutes));
        if (!routingHits.isEmpty()) {
            sb.append("   Expert hit distribution:\n");
            routingHits.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue().get(), a.getValue().get()))
                .forEach(e -> sb.append(String.format(
                    "     %-28s %d queries\n", e.getKey() + ":", e.getValue().get()
                )));
        }

        sb.append("\n-- MEMORY RECALL ----------------------------------------------\n");
        sb.append(String.format("   Total recalls:    %d\n", totalRecalls));
        sb.append(String.format("   Recall precision: %.1f%%\n", getRecallPrecision()));
        sb.append(String.format(
            "   Cache hit rate:   %.1f%%\n",
            totalRecalls > 0 ? (cacheHits * 100.0 / totalRecalls) : 0.0
        ));
        sb.append(String.format("   Avg latency:      %.1fms\n", avgRecallMs));

        sb.append("\n-- COMPILE / BUILD --------------------------------------------\n");
        sb.append(String.format("   Attempted:        %d\n", compilesAttempted));
        sb.append(String.format("   Success rate:     %.1f%%\n", getCompileSuccessRate()));
        sb.append(String.format("   Errors:           %d\n", compilationErrors));

        sb.append("\n-- TASK COMPLETION --------------------------------------------\n");
        sb.append(String.format("   Tasks started:    %d\n", tasksStarted));
        sb.append(String.format("   Completion rate:  %.1f%%\n", getTaskCompletionRate()));
        sb.append(String.format("   Completed:        %d  |  Failed: %d\n", tasksCompleted, tasksFailed));

        if (!subsystems.isEmpty()) {
            sb.append("\n-- SUBSYSTEM HEALTH -------------------------------------------\n");
            subsystems.forEach((name, h) ->
                sb.append(String.format(
                    "   %-28s %s  (calls: %d, avg: %.1fms)\n",
                    name + ":",
                    h.isHealthy() ? "HEALTHY" : "DEGRADED",
                    h.totalCalls,
                    h.avgLatencyMs
                ))
            );
        }

        double coherence = getCoherenceScore();
        sb.append("\n-- PHI-COHERENCE SCORE ----------------------------------------\n");
        sb.append(String.format("   %.4f  (threshold: 0.7569)\n", coherence));
        if (!hasCoherenceEvidence()) {
            sb.append("   Status: BASELINE (NO WORKLOAD DATA YET)\n");
        } else {
            sb.append(String.format(
                "   Status: %s\n",
                coherence >= 0.7569 ? "ABOVE THRESHOLD" : "BELOW THRESHOLD"
            ));
        }

        sb.append("===============================================================\n");
        return sb.toString();
    }

    public double getCoherenceScore() {
        return computeCoherence();
    }

    public boolean hasCoherenceEvidence() {
        return totalQueries > 0 || totalRecalls > 0 || compilesAttempted > 0 || tasksStarted > 0;
    }

    private double computeCoherence() {
        if (!hasCoherenceEvidence()) {
            // Neutral baseline for fresh boot before any workload.
            return 0.7569;
        }

        double routing = getRoutingAccuracy() / 100.0;
        double recall = getRecallPrecision() / 100.0;
        double compile = getCompileSuccessRate() / 100.0;
        double task = getTaskCompletionRate() / 100.0;

        // Weighted average: routing 30%, recall 30%, compile 20%, task 20%
        double raw = (routing * 0.30) + (recall * 0.30) + (compile * 0.20) + (task * 0.20);

        // Phi-harmonic scaling
        return Math.min(1.0, raw * (1.0 / PHI) + (1.0 - 1.0 / PHI));
    }

    // ---------------------------------------------------------------------
    // SubsystemHealth inner class
    // ---------------------------------------------------------------------

    public static class SubsystemHealth {
        private final String name;
        private long totalCalls = 0;
        private long failedCalls = 0;
        private double avgLatencyMs = 0;

        public SubsystemHealth(String name) {
            this.name = name;
        }

        public void recordSuccess(long latencyMs) {
            totalCalls++;
            avgLatencyMs = ((avgLatencyMs * (totalCalls - 1)) + latencyMs) / totalCalls;
        }

        public void recordFailure() {
            totalCalls++;
            failedCalls++;
        }

        public boolean isHealthy() {
            if (totalCalls == 0) {
                return true;
            }
            return (failedCalls * 100.0 / totalCalls) < 20.0;
        }
    }
}
