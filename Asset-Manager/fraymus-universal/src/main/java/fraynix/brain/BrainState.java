package fraynix.brain;

import java.util.*;

/**
 * BRAIN STATE: Snapshot of the brain's current state.
 * 
 * Used for policy decisions and DreamState replay.
 */
public class BrainState {

    private final int activeNodeCount;
    private final double averageActivation;
    private final List<HotSpot> hotSpots;
    private final long thoughtsProcessed;
    private final Map<String, Double> regionActivations;
    
    // Resource metrics
    private final double cpuUsage;
    private final double memoryUsage;
    private final long queueDepth;
    private final long recentErrors;

    public BrainState(
        int activeNodeCount,
        double averageActivation,
        List<HotSpot> hotSpots,
        long thoughtsProcessed,
        Map<String, Double> regionActivations,
        double cpuUsage,
        double memoryUsage,
        long queueDepth,
        long recentErrors
    ) {
        this.activeNodeCount = activeNodeCount;
        this.averageActivation = averageActivation;
        this.hotSpots = hotSpots != null ? List.copyOf(hotSpots) : List.of();
        this.thoughtsProcessed = thoughtsProcessed;
        this.regionActivations = regionActivations != null ? Map.copyOf(regionActivations) : Map.of();
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.queueDepth = queueDepth;
        this.recentErrors = recentErrors;
    }

    public int getActiveNodeCount() { return activeNodeCount; }
    public double getAverageActivation() { return averageActivation; }
    public List<HotSpot> getHotSpots() { return hotSpots; }
    public long getThoughtsProcessed() { return thoughtsProcessed; }
    public Map<String, Double> getRegionActivations() { return regionActivations; }
    public double getCpuUsage() { return cpuUsage; }
    public double getMemoryUsage() { return memoryUsage; }
    public long getQueueDepth() { return queueDepth; }
    public long getRecentErrors() { return recentErrors; }

    public static Builder builder() {
        return new Builder();
    }

    public record HotSpot(int[] coordinates, double activation, String associatedData) {}

    public static class Builder {
        private int activeNodeCount;
        private double averageActivation;
        private List<HotSpot> hotSpots = new ArrayList<>();
        private long thoughtsProcessed;
        private Map<String, Double> regionActivations = new HashMap<>();
        private double cpuUsage;
        private double memoryUsage;
        private long queueDepth;
        private long recentErrors;

        public Builder activeNodeCount(int count) { this.activeNodeCount = count; return this; }
        public Builder averageActivation(double avg) { this.averageActivation = avg; return this; }
        public Builder hotSpots(List<HotSpot> spots) { this.hotSpots = spots; return this; }
        public Builder addHotSpot(HotSpot spot) { this.hotSpots.add(spot); return this; }
        public Builder thoughtsProcessed(long count) { this.thoughtsProcessed = count; return this; }
        public Builder regionActivations(Map<String, Double> regions) { this.regionActivations = regions; return this; }
        public Builder cpuUsage(double cpu) { this.cpuUsage = cpu; return this; }
        public Builder memoryUsage(double mem) { this.memoryUsage = mem; return this; }
        public Builder queueDepth(long depth) { this.queueDepth = depth; return this; }
        public Builder recentErrors(long errors) { this.recentErrors = errors; return this; }

        public BrainState build() {
            return new BrainState(
                activeNodeCount, averageActivation, hotSpots, thoughtsProcessed,
                regionActivations, cpuUsage, memoryUsage, queueDepth, recentErrors
            );
        }
    }
}
