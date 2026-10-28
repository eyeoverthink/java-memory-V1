package brain_architecture.core;

import java.util.Map;

/**
 * Configuration for a brain's behavior and evolution parameters.
 */
public class BrainConfiguration {
    private final BrainType brainType;
    private final double evolutionCheckInterval;
    private final double degradationThreshold;
    private final int historyRetentionDays;
    private final double successRateWeight;
    private final double latencyWeight;
    private final double stabilityWeight;
    private final Map<String, Object> customParameters;
    
    public BrainConfiguration(
        BrainType brainType,
        double evolutionCheckInterval,
        double degradationThreshold,
        int historyRetentionDays,
        double successRateWeight,
        double latencyWeight,
        double stabilityWeight,
        Map<String, Object> customParameters
    ) {
        this.brainType = brainType;
        this.evolutionCheckInterval = evolutionCheckInterval;
        this.degradationThreshold = degradationThreshold;
        this.historyRetentionDays = historyRetentionDays;
        this.successRateWeight = successRateWeight;
        this.latencyWeight = latencyWeight;
        this.stabilityWeight = stabilityWeight;
        this.customParameters = customParameters;
    }
    
    public BrainType getBrainType() {
        return brainType;
    }
    
    public double getEvolutionCheckInterval() {
        return evolutionCheckInterval;
    }
    
    public double getDegradationThreshold() {
        return degradationThreshold;
    }
    
    public int getHistoryRetentionDays() {
        return historyRetentionDays;
    }
    
    public double getSuccessRateWeight() {
        return successRateWeight;
    }
    
    public double getLatencyWeight() {
        return latencyWeight;
    }
    
    public double getStabilityWeight() {
        return stabilityWeight;
    }
    
    public Map<String, Object> getCustomParameters() {
        return customParameters;
    }
}
