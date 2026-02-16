package fraynix.core;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * SNAPSHOT: State capture for DreamState replay.
 * 
 * Captures the complete system state at a point in time.
 * Used for replay, debugging, and "what-if" simulations.
 */
public record Snapshot(
    String id,
    Instant timestamp,
    SnapshotType type,
    
    // System state
    SystemState system,
    
    // Brain state
    BrainState brain,
    
    // Recent intents
    List<Intent> recentIntents,
    
    // Recent decisions
    List<DecisionRecord> recentDecisions,
    
    // Custom data
    Map<String, Object> metadata
) {
    
    public enum SnapshotType {
        PERIODIC,       // Regular interval snapshot
        PRE_ACTION,     // Before a significant action
        POST_ACTION,    // After a significant action
        ERROR,          // After an error
        MANUAL,         // User-requested
        DREAM           // During dream state
    }

    public record SystemState(
        long uptimeMs,
        double cpuUsage,
        double memoryUsage,
        long threadCount,
        long activeProcesses,
        Map<String, KernelService.ServiceStatus> serviceStatuses
    ) {}

    public record BrainState(
        int activeNodes,
        double averageActivation,
        List<HotSpot> hotSpots,
        long thoughtsProcessed,
        Map<String, Double> regionActivations
    ) {}

    public record HotSpot(
        int[] coordinates,
        double activation,
        String associatedData
    ) {}

    public record DecisionRecord(
        Instant timestamp,
        String policyName,
        String intentId,
        String chosenAction,
        double confidence,
        String reason
    ) {}

    public static Snapshot create(SnapshotType type) {
        return new Snapshot(
            java.util.UUID.randomUUID().toString(),
            Instant.now(),
            type,
            captureSystemState(),
            null, // Brain state filled by brain
            List.of(),
            List.of(),
            Map.of()
        );
    }

    public static SystemState captureSystemState() {
        Runtime rt = Runtime.getRuntime();
        return new SystemState(
            java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime(),
            0.0,
            1.0 - ((double) rt.freeMemory() / rt.maxMemory()),
            Thread.activeCount(),
            0,
            Map.of()
        );
    }

    public Snapshot withBrainState(BrainState brain) {
        return new Snapshot(id, timestamp, type, system, brain, recentIntents, recentDecisions, metadata);
    }

    public Snapshot withIntents(List<Intent> intents) {
        return new Snapshot(id, timestamp, type, system, brain, intents, recentDecisions, metadata);
    }

    public Snapshot withDecisions(List<DecisionRecord> decisions) {
        return new Snapshot(id, timestamp, type, system, brain, recentIntents, decisions, metadata);
    }
}
