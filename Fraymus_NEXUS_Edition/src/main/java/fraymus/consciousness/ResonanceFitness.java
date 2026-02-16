package fraymus.consciousness;

/**
 * Resonance Fitness Function
 * 
 * Determines which thoughts survive and which are pruned.
 * This is the "Active Loop" - the Will.
 * 
 * Survival Function: P(survival) = 1 / (1 + e^(-φ(R_φ - threshold)))
 * 
 * If resonance exceeds threshold → survives → writes to long-term memory
 * If resonance fails → dies → pruned from consciousness
 */
public class ResonanceFitness {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final double threshold;
    private final double phiScale;
    
    // Statistics
    private int thoughtsGenerated = 0;
    private int thoughtsSurvived = 0;
    private int thoughtsPruned = 0;
    
    public ResonanceFitness(double threshold) {
        this.threshold = threshold;
        this.phiScale = PHI;
    }
    
    /**
     * Evaluate if a thought survives based on its resonance
     * 
     * @param resonance The resonance value (0 to 1)
     * @return true if thought survives, false if pruned
     */
    public boolean evaluateSurvival(double resonance) {
        thoughtsGenerated++;
        
        double survivalProbability = computeSurvivalProbability(resonance);
        boolean survives = Math.random() < survivalProbability;
        
        if (survives) {
            thoughtsSurvived++;
        } else {
            thoughtsPruned++;
        }
        
        return survives;
    }
    
    /**
     * Compute survival probability using phi-scaled sigmoid
     * 
     * P(survival) = 1 / (1 + e^(-φ(R_φ - threshold)))
     */
    public double computeSurvivalProbability(double resonance) {
        double exponent = -phiScale * (resonance - threshold);
        return 1.0 / (1.0 + Math.exp(exponent));
    }
    
    /**
     * Compute fitness score (how "fit" is this thought?)
     * Higher score = more likely to survive and replicate
     */
    public double computeFitness(double resonance) {
        // Fitness is survival probability scaled by phi
        double baseFitness = computeSurvivalProbability(resonance);
        
        // Apply phi-harmonic boost for high resonance
        if (resonance > threshold * PHI) {
            baseFitness *= PHI;
        }
        
        return Math.min(1.0, baseFitness);
    }
    
    /**
     * Determine if thought should replicate (spawn related thoughts)
     */
    public boolean shouldReplicate(double resonance) {
        // Only highly resonant thoughts replicate
        double replicationThreshold = threshold * PHI;
        return resonance > replicationThreshold;
    }
    
    /**
     * Compute replication factor (how many offspring thoughts)
     */
    public int computeReplicationFactor(double resonance) {
        if (!shouldReplicate(resonance)) return 0;
        
        // Higher resonance = more offspring
        double factor = (resonance - threshold * PHI) / (1.0 - threshold * PHI);
        return (int) Math.ceil(factor * PHI * 3); // Max ~5 offspring
    }
    
    /**
     * Evolutionary pressure: adjust threshold based on survival rate
     * If too many thoughts survive, raise threshold (increase selection pressure)
     * If too few survive, lower threshold (decrease selection pressure)
     */
    public void adaptThreshold() {
        if (thoughtsGenerated < 100) return; // Need sufficient data
        
        double survivalRate = (double) thoughtsSurvived / thoughtsGenerated;
        
        // Target survival rate: 1/φ ≈ 0.618 (golden ratio)
        double targetRate = 1.0 / PHI;
        double delta = survivalRate - targetRate;
        
        // Adjust threshold (small steps)
        double adjustment = delta * 0.01;
        
        // Don't let threshold go below 0.1 or above 0.9
        double newThreshold = Math.max(0.1, Math.min(0.9, threshold + adjustment));
        
        // Update (would need to make threshold non-final for this)
        // For now, this is informational
    }
    
    /**
     * Compute selection pressure (how harsh is the environment?)
     */
    public double computeSelectionPressure() {
        if (thoughtsGenerated == 0) return 0;
        
        double survivalRate = (double) thoughtsSurvived / thoughtsGenerated;
        
        // Pressure is inverse of survival rate, scaled by phi
        return PHI * (1.0 - survivalRate);
    }
    
    /**
     * Get survival statistics
     */
    public SurvivalStats getStats() {
        return new SurvivalStats(
            thoughtsGenerated,
            thoughtsSurvived,
            thoughtsPruned,
            thoughtsGenerated > 0 ? (double) thoughtsSurvived / thoughtsGenerated : 0,
            computeSelectionPressure()
        );
    }
    
    /**
     * Reset statistics
     */
    public void resetStats() {
        thoughtsGenerated = 0;
        thoughtsSurvived = 0;
        thoughtsPruned = 0;
    }
    
    /**
     * Survival statistics container
     */
    public static class SurvivalStats {
        public final int generated;
        public final int survived;
        public final int pruned;
        public final double survivalRate;
        public final double selectionPressure;
        
        public SurvivalStats(int generated, int survived, int pruned, 
                           double survivalRate, double selectionPressure) {
            this.generated = generated;
            this.survived = survived;
            this.pruned = pruned;
            this.survivalRate = survivalRate;
            this.selectionPressure = selectionPressure;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Generated: %d | Survived: %d (%.1f%%) | Pruned: %d | Pressure: %.3f",
                generated, survived, survivalRate * 100, pruned, selectionPressure
            );
        }
    }
}
