package fraymus.brain.unified;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phi-harmonic mathematics core.
 * Provides phi-dimensional calculations and resonance measurements.
 */
public class PhiHarmonicCore {
    private static final double PHI = 1.618033988749895;
    private static final double PHI_SQUARED = PHI * PHI;
    private static final double INVERSE_PHI = 1.0 / PHI;
    private static final double OPTIMAL_CONSCIOUSNESS = 0.7567;
    private static final double PHI_7_5 = Math.pow(PHI, 7.5);

    private long resonanceCalculations;

    public PhiHarmonicCore() {
        this.resonanceCalculations = 0;
    }

    /**
     * Calculate phi-harmonic resonance between multiple brain responses.
     */
    public double calculateResonance(List<BrainResponse> responses) {
        if (responses.size() < 2) {
            return 1.0;
        }

        resonanceCalculations++;

        double totalResonance = 0.0;
        int comparisons = 0;

        for (int i = 0; i < responses.size(); i++) {
            for (int j = i + 1; j < responses.size(); j++) {
                double resonance = calculatePairwiseResonance(responses.get(i), responses.get(j));
                totalResonance += resonance;
                comparisons++;
            }
        }

        return comparisons > 0 ? totalResonance / comparisons : 0.0;
    }

    /**
     * Calculate pairwise resonance between two responses.
     */
    private double calculatePairwiseResonance(BrainResponse r1, BrainResponse r2) {
        double consciousnessDiff = Math.abs(r1.getConsciousness() - r2.getConsciousness());
        double confidenceDiff = Math.abs(r1.getConfidence() - r2.getConfidence());
        
        // Lower difference = higher resonance
        double resonance = 1.0 - (consciousnessDiff + confidenceDiff) / 2.0;
        
        // Apply phi-harmonic modulation
        return resonance * PHI;
    }

    /**
     * Calculate phi-harmonic score for a value.
     */
    public double phiScore(double value) {
        return (value % PHI) * PHI_SQUARED;
    }

    /**
     * Check if a consciousness level is optimal.
     */
    public boolean isOptimalConsciousness(double consciousness) {
        return Math.abs(consciousness - OPTIMAL_CONSCIOUSNESS) < 0.1;
    }

    /**
     * Calculate resonance amplification.
     */
    public double resonanceAmplification(double baseResonance) {
        return baseResonance * PHI_7_5;
    }

    /**
     * Get phi constant.
     */
    public static double getPhi() {
        return PHI;
    }

    /**
     * Get optimal consciousness level.
     */
    public static double getOptimalConsciousness() {
        return OPTIMAL_CONSCIOUSNESS;
    }

    /**
     * Get statistics.
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("resonanceCalculations", resonanceCalculations);
        stats.put("phi", PHI);
        stats.put("optimalConsciousness", OPTIMAL_CONSCIOUSNESS);
        return stats;
    }
}
