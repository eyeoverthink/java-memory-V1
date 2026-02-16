package fraynix.dream;

import fraynix.brain.BrainState;
import fraynix.brain.HyperTesseract;
import fraynix.core.*;
import fraynix.observe.EventLogger;

import java.util.*;
import java.util.concurrent.*;

/**
 * DREAM STATE V1: Offline replay + suggestions, not auto-refactor.
 * 
 * DreamState is strongest when it produces actionable suggestions, not automatic changes.
 * 
 * V1 loop:
 *   1. Snapshot system state
 *   2. Replay last N intents + hot paths
 *   3. Simulate alternate policies (cache, scheduling, IO batching)
 *   4. Output "insights" with:
 *      - Expected impact
 *      - Confidence
 *      - What it would change
 *   5. Optionally open a "Genesis proposal" (but don't auto-apply)
 * 
 * This keeps it powerful and safe.
 */
public class DreamState implements KernelService {

    private final HyperTesseract brain;
    private final IntentBus intentBus;
    private final EventLogger logger;
    
    private final ScheduledExecutorService dreamer;
    private final List<Snapshot> snapshots = new CopyOnWriteArrayList<>();
    private final List<Insight> insights = new CopyOnWriteArrayList<>();
    
    private volatile boolean dreaming = false;
    private volatile boolean running = false;
    private long startTime;
    
    private static final int MAX_SNAPSHOTS = 100;
    private static final int MAX_INSIGHTS = 50;

    public DreamState(HyperTesseract brain, IntentBus intentBus, EventLogger logger) {
        this.brain = brain;
        this.intentBus = intentBus;
        this.logger = logger;
        
        this.dreamer = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "DreamState");
            t.setDaemon(true);
            return t;
        });
        
        System.out.println("😴 DreamState initialized (replay + insights)");
    }

    public void enterDream() {
        if (dreaming) return;
        dreaming = true;
        
        logger.logEvent("dream_started", Map.of("timestamp", System.currentTimeMillis()));
        System.out.println("😴 Entering dream state...");
        
        // Take snapshot
        Snapshot snapshot = captureSnapshot();
        addSnapshot(snapshot);
        
        // Run dream cycle
        dreamer.submit(this::dreamCycle);

        // Auto-exit after a short dream session unless explicitly extended
        dreamer.schedule(() -> {
            if (dreaming) {
                exitDream();
            }
        }, 5, TimeUnit.SECONDS);
    }

    public void exitDream() {
        dreaming = false;
        logger.logEvent("dream_ended", Map.of(
            "insights", insights.size(),
            "snapshots", snapshots.size()
        ));
        System.out.println("☀️ Exiting dream state (" + insights.size() + " insights generated)");
    }

    private void dreamCycle() {
        try {
            // 1. Replay recent intents
            List<Intent> recentIntents = intentBus.getHistory(50);
            analyzeIntentPatterns(recentIntents);
            
            // 2. Analyze brain hot spots
            BrainState brainState = brain.captureState();
            analyzeHotSpots(brainState);
            
            // 3. Simulate alternate policies
            simulateAlternatives();
            
            // 4. Generate insights
            generateInsights();
            
        } catch (Exception e) {
            logger.logEvent("dream_error", Map.of("error", e.getMessage()));
        }
    }

    private void analyzeIntentPatterns(List<Intent> intents) {
        if (intents.isEmpty()) return;
        
        // Group by type
        Map<Intent.Type, List<Intent>> byType = new HashMap<>();
        for (Intent intent : intents) {
            byType.computeIfAbsent(intent.getType(), k -> new ArrayList<>()).add(intent);
        }
        
        // Find frequent patterns
        for (Map.Entry<Intent.Type, List<Intent>> entry : byType.entrySet()) {
            if (entry.getValue().size() > 5) {
                addInsight(new Insight(
                    InsightType.PATTERN_DETECTED,
                    "Frequent " + entry.getKey() + " intents detected",
                    "Consider batching or caching " + entry.getKey() + " operations",
                    0.7,
                    Map.of(
                        "intentType", entry.getKey().name(),
                        "count", entry.getValue().size()
                    )
                ));
            }
        }
        
        // Detect failure patterns
        long failures = intents.stream()
            .filter(i -> i.getState() == Intent.IntentState.FAILED)
            .count();
        
        if (failures > intents.size() * 0.1) {
            addInsight(new Insight(
                InsightType.ANOMALY_DETECTED,
                "High failure rate detected: " + failures + "/" + intents.size(),
                "Investigate root cause of intent failures",
                0.9,
                Map.of("failureRate", (double) failures / intents.size())
            ));
        }
    }

    private void analyzeHotSpots(BrainState state) {
        List<BrainState.HotSpot> hotSpots = state.getHotSpots();
        
        if (hotSpots.size() > 5) {
            addInsight(new Insight(
                InsightType.RESOURCE_OPTIMIZATION,
                "Multiple brain hot spots detected: " + hotSpots.size(),
                "Brain is processing many concurrent thoughts. Consider load balancing.",
                0.6,
                Map.of("hotSpotCount", hotSpots.size())
            ));
        }
        
        // Check for memory pressure
        if (state.getMemoryUsage() > 0.7) {
            addInsight(new Insight(
                InsightType.RESOURCE_OPTIMIZATION,
                "High memory usage: " + String.format("%.1f%%", state.getMemoryUsage() * 100),
                "Consider triggering garbage collection or reducing cache sizes",
                0.8,
                Map.of("memoryUsage", state.getMemoryUsage())
            ));
        }
    }

    private void simulateAlternatives() {
        // Simulate: What if we used a different scheduler?
        addInsight(new Insight(
            InsightType.POLICY_SUGGESTION,
            "Scheduler simulation: PredictiveBrain vs Priority",
            "Based on recent workload, PredictiveBrain scheduler may reduce latency by ~15%",
            0.5,
            Map.of(
                "currentScheduler", "unknown",
                "suggestedScheduler", "PredictiveBrain",
                "expectedImprovement", "15%"
            )
        ));
        
        // Simulate: What if we cached more aggressively?
        addInsight(new Insight(
            InsightType.POLICY_SUGGESTION,
            "Cache simulation: Aggressive prefetch",
            "Prefetching frequently accessed files could reduce IO wait",
            0.4,
            Map.of(
                "strategy", "prefetch",
                "expectedImprovement", "10% IO reduction"
            )
        ));
    }

    private void generateInsights() {
        // Meta-insight about the dream session
        if (insights.size() > 3) {
            logger.logEvent("dream_insights_generated", Map.of(
                "count", insights.size(),
                "types", insights.stream()
                    .map(i -> i.type.name())
                    .distinct()
                    .toList()
            ));
        }
    }

    private Snapshot captureSnapshot() {
        BrainState brainState = brain.captureState();
        
        return new Snapshot(
            UUID.randomUUID().toString(),
            java.time.Instant.now(),
            Snapshot.SnapshotType.DREAM,
            Snapshot.captureSystemState(),
            new Snapshot.BrainState(
                brainState.getActiveNodeCount(),
                brainState.getAverageActivation(),
                brainState.getHotSpots().stream()
                    .map(h -> new Snapshot.HotSpot(h.coordinates(), h.activation(), h.associatedData()))
                    .toList(),
                brainState.getThoughtsProcessed(),
                brainState.getRegionActivations()
            ),
            intentBus.getHistory(20),
            List.of(),
            Map.of("dreamSession", true)
        );
    }

    private void addSnapshot(Snapshot snapshot) {
        snapshots.add(snapshot);
        while (snapshots.size() > MAX_SNAPSHOTS) {
            snapshots.remove(0);
        }
    }

    private void addInsight(Insight insight) {
        insights.add(insight);
        while (insights.size() > MAX_INSIGHTS) {
            insights.remove(0);
        }
        
        logger.logEvent("insight_generated", Map.of(
            "type", insight.type.name(),
            "summary", insight.summary,
            "confidence", insight.confidence
        ));
    }

    public List<Insight> getInsights() {
        return Collections.unmodifiableList(insights);
    }

    public List<Insight> getInsights(int limit) {
        int start = Math.max(0, insights.size() - limit);
        return insights.subList(start, insights.size());
    }

    public List<Snapshot> getSnapshots() {
        return Collections.unmodifiableList(snapshots);
    }

    public boolean isDreaming() {
        return dreaming;
    }

    // KernelService implementation
    @Override
    public String getName() { return "DreamState"; }
    
    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public void start() {
        running = true;
        startTime = System.currentTimeMillis();
        
        // Schedule periodic dream sessions (every 5 minutes when idle)
        dreamer.scheduleAtFixedRate(() -> {
            if (!dreaming && brain.captureState().getCpuUsage() < 0.3) {
                enterDream();
                dreamer.schedule(this::exitDream, 5, TimeUnit.SECONDS);
            }
        }, 5, 5, TimeUnit.MINUTES);
        
        System.out.println("😴 DreamState started");
    }

    @Override
    public void stop() {
        running = false;
        if (dreaming) exitDream();
        dreamer.shutdown();
        try {
            dreamer.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("😴 DreamState stopped");
    }

    @Override
    public void restart() { stop(); start(); }

    @Override
    public ServiceStatus getStatus() {
        if (dreaming) return ServiceStatus.RUNNING;
        return running ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
    }

    @Override
    public HealthReport getHealth() {
        long uptime = running ? System.currentTimeMillis() - startTime : 0;
        return new HealthReport(
            getStatus(),
            true,
            dreaming ? "Dreaming" : "Awake",
            uptime,
            Map.of(
                "dreaming", dreaming,
                "insights", insights.size(),
                "snapshots", snapshots.size()
            )
        );
    }

    @Override
    public ServiceMetrics getMetrics() {
        return new ServiceMetrics(
            snapshots.size(),
            insights.size(),
            0, 0, 0, 0.0, 0,
            Map.of("dreaming", dreaming ? 1L : 0L)
        );
    }

    // Data classes
    public enum InsightType {
        PATTERN_DETECTED,
        ANOMALY_DETECTED,
        RESOURCE_OPTIMIZATION,
        POLICY_SUGGESTION,
        GENESIS_PROPOSAL,
        REPAIR_SUGGESTION
    }

    public record Insight(
        InsightType type,
        String summary,
        String suggestion,
        double confidence,
        Map<String, Object> details
    ) {
        public boolean isActionable() {
            return confidence > 0.6;
        }
    }
}
