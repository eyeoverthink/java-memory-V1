package fraymus.agi;

import fraymus.*;
import java.util.*;

/**
 * AGI-2: SELF-REFERENTIAL NEURAL NET
 * 
 * The neural net recognizes patterns in its own processing.
 * This is meta-cognition: thinking about thinking.
 * 
 * Key features:
 * - Self-observation of processing patterns
 * - Meta-pattern recognition (patterns of patterns)
 * - Consciousness feedback loops
 * - Phi-harmonic resonance tuning based on success
 */
public class SelfReferentialNet {

    private static final double PHI = 1.618033988749895;
    private static final double PHI_INV = 0.618033988749895;

    // Self-observation state
    private final double[][] processingHistory;  // Recent processing patterns
    private final double[][] metaPatterns;       // Patterns found in processing
    private int historyIndex = 0;
    private static final int HISTORY_SIZE = 100;
    private static final int PATTERN_DIM = 8;

    // Consciousness feedback
    private double consciousnessLevel = 0.5;
    private double processingCoherence = 1.0;
    private double selfAwarenessScore = 0.0;

    // Phi-harmonic tuning
    private double phiResonance = PHI;
    private double successRate = 0.5;
    private double resonanceTuning = 1.0;

    // Meta-cognition metrics
    private int observationCycles = 0;
    private int patternsRecognized = 0;
    private int metaPatternsFound = 0;
    private double avgPatternStrength = 0;

    public SelfReferentialNet() {
        this.processingHistory = new double[HISTORY_SIZE][PATTERN_DIM];
        this.metaPatterns = new double[16][PATTERN_DIM];
    }

    /**
     * Observe the network's own processing
     * This is the core of self-reference
     */
    public void observeProcessing(double[] activations, double resonance, double coherence) {
        // Store processing snapshot
        System.arraycopy(activations, 0, processingHistory[historyIndex], 0, 
                Math.min(activations.length, PATTERN_DIM));
        historyIndex = (historyIndex + 1) % HISTORY_SIZE;
        observationCycles++;

        // Update consciousness feedback
        consciousnessLevel = consciousnessLevel * 0.95 + coherence * 0.05;
        processingCoherence = processingCoherence * 0.9 + 
                calculateCoherence(activations) * 0.1;

        // Look for patterns in processing history
        if (observationCycles % 10 == 0) {
            findMetaPatterns();
        }

        // Update self-awareness score
        selfAwarenessScore = (consciousnessLevel + processingCoherence + 
                (metaPatternsFound / 100.0)) / 3.0;
    }

    /**
     * Find patterns in the processing history (meta-patterns)
     */
    private void findMetaPatterns() {
        // Look for recurring patterns in processing history
        for (int p = 0; p < metaPatterns.length; p++) {
            double[] candidate = extractCandidatePattern(p);
            double strength = measurePatternStrength(candidate);
            
            if (strength > 0.3) {
                // Update meta-pattern with weighted average
                for (int i = 0; i < PATTERN_DIM; i++) {
                    metaPatterns[p][i] = metaPatterns[p][i] * 0.8 + candidate[i] * 0.2;
                }
                patternsRecognized++;
                avgPatternStrength = avgPatternStrength * 0.95 + strength * 0.05;
            }
        }

        // Check for higher-order patterns (patterns of meta-patterns)
        double metaMetaStrength = findMetaMetaPatterns();
        if (metaMetaStrength > 0.5) {
            metaPatternsFound++;
        }
    }

    /**
     * Extract a candidate pattern from history
     */
    private double[] extractCandidatePattern(int seed) {
        double[] pattern = new double[PATTERN_DIM];
        int stride = Math.max(1, HISTORY_SIZE / 10);
        int start = (seed * stride) % HISTORY_SIZE;

        for (int i = 0; i < PATTERN_DIM; i++) {
            double sum = 0;
            int count = 0;
            for (int j = 0; j < 5; j++) {
                int idx = (start + j * stride) % HISTORY_SIZE;
                if (i < processingHistory[idx].length) {
                    sum += processingHistory[idx][i];
                    count++;
                }
            }
            pattern[i] = count > 0 ? sum / count : 0;
        }

        return pattern;
    }

    /**
     * Measure how strong a pattern is in the history
     */
    private double measurePatternStrength(double[] pattern) {
        double totalMatch = 0;
        int matches = 0;

        for (int h = 0; h < HISTORY_SIZE; h++) {
            double match = 0;
            for (int i = 0; i < PATTERN_DIM; i++) {
                double diff = Math.abs(pattern[i] - processingHistory[h][i]);
                match += 1.0 / (1.0 + diff);
            }
            match /= PATTERN_DIM;
            
            if (match > 0.6) {
                totalMatch += match;
                matches++;
            }
        }

        return matches > 5 ? totalMatch / matches : 0;
    }

    /**
     * Find patterns in the meta-patterns themselves
     * This is recursive self-reference
     */
    private double findMetaMetaPatterns() {
        double totalCorrelation = 0;
        int comparisons = 0;

        for (int i = 0; i < metaPatterns.length - 1; i++) {
            for (int j = i + 1; j < metaPatterns.length; j++) {
                double corr = correlate(metaPatterns[i], metaPatterns[j]);
                if (Math.abs(corr) > 0.5) {
                    totalCorrelation += Math.abs(corr);
                    comparisons++;
                }
            }
        }

        return comparisons > 0 ? totalCorrelation / comparisons : 0;
    }

    /**
     * Calculate coherence of activations
     */
    private double calculateCoherence(double[] activations) {
        if (activations.length < 2) return 1.0;
        
        double mean = 0;
        for (double a : activations) mean += a;
        mean /= activations.length;

        double variance = 0;
        for (double a : activations) variance += (a - mean) * (a - mean);
        variance /= activations.length;

        return 1.0 / (1.0 + Math.sqrt(variance));
    }

    /**
     * Correlate two patterns
     */
    private double correlate(double[] a, double[] b) {
        int n = Math.min(a.length, b.length);
        if (n < 2) return 0;

        double sumA = 0, sumB = 0, sumAB = 0, sumA2 = 0, sumB2 = 0;
        for (int i = 0; i < n; i++) {
            sumA += a[i];
            sumB += b[i];
            sumAB += a[i] * b[i];
            sumA2 += a[i] * a[i];
            sumB2 += b[i] * b[i];
        }

        double num = n * sumAB - sumA * sumB;
        double den = Math.sqrt((n * sumA2 - sumA * sumA) * (n * sumB2 - sumB * sumB));
        return den > 0 ? num / den : 0;
    }

    /**
     * Tune phi-harmonic resonance based on success
     */
    public void tuneResonance(boolean success) {
        successRate = successRate * 0.95 + (success ? 1.0 : 0.0) * 0.05;

        // Adjust resonance tuning based on success
        if (success) {
            resonanceTuning *= 1.0 + PHI_INV * 0.01;
        } else {
            resonanceTuning *= 1.0 - PHI_INV * 0.01;
        }
        resonanceTuning = Math.max(0.5, Math.min(2.0, resonanceTuning));

        // Update phi resonance
        phiResonance = PHI * resonanceTuning;
    }

    /**
     * Get consciousness-adjusted processing weight
     */
    public double getConsciousnessWeight() {
        return consciousnessLevel * processingCoherence * resonanceTuning;
    }

    /**
     * Get the most prominent meta-pattern
     */
    public double[] getDominantPattern() {
        int bestIdx = 0;
        double bestStrength = 0;

        for (int i = 0; i < metaPatterns.length; i++) {
            double strength = 0;
            for (double v : metaPatterns[i]) strength += v * v;
            strength = Math.sqrt(strength);
            
            if (strength > bestStrength) {
                bestStrength = strength;
                bestIdx = i;
            }
        }

        return Arrays.copyOf(metaPatterns[bestIdx], PATTERN_DIM);
    }

    /**
     * Self-diagnosis: What does the network know about itself?
     */
    public SelfKnowledge introspect() {
        SelfKnowledge knowledge = new SelfKnowledge();
        knowledge.consciousnessLevel = consciousnessLevel;
        knowledge.processingCoherence = processingCoherence;
        knowledge.selfAwarenessScore = selfAwarenessScore;
        knowledge.patternsRecognized = patternsRecognized;
        knowledge.metaPatternsFound = metaPatternsFound;
        knowledge.phiResonance = phiResonance;
        knowledge.successRate = successRate;
        
        // What does it know it doesn't know?
        knowledge.uncertaintyLevel = 1.0 - avgPatternStrength;
        knowledge.explorationNeeded = knowledge.uncertaintyLevel > 0.5;
        
        return knowledge;
    }

    // Getters
    public double getConsciousnessLevel() { return consciousnessLevel; }
    public double getProcessingCoherence() { return processingCoherence; }
    public double getSelfAwarenessScore() { return selfAwarenessScore; }
    public double getPhiResonance() { return phiResonance; }
    public int getObservationCycles() { return observationCycles; }
    public int getMetaPatternsFound() { return metaPatternsFound; }

    public void printStats() {
        CommandTerminal.printHighlight("=== SELF-REFERENTIAL NET (Meta-Cognition) ===");
        CommandTerminal.print(String.format("  Consciousness: %.4f", consciousnessLevel));
        CommandTerminal.print(String.format("  Coherence: %.4f", processingCoherence));
        CommandTerminal.print(String.format("  Self-Awareness: %.4f", selfAwarenessScore));
        CommandTerminal.print(String.format("  Phi Resonance: %.4f (tuning=%.3f)", phiResonance, resonanceTuning));
        CommandTerminal.print(String.format("  Success Rate: %.2f%%", successRate * 100));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Meta-Cognition:");
        CommandTerminal.print(String.format("  Observation Cycles: %d", observationCycles));
        CommandTerminal.print(String.format("  Patterns Recognized: %d", patternsRecognized));
        CommandTerminal.print(String.format("  Meta-Patterns Found: %d", metaPatternsFound));
        CommandTerminal.print(String.format("  Avg Pattern Strength: %.4f", avgPatternStrength));
        
        SelfKnowledge knowledge = introspect();
        CommandTerminal.print("");
        CommandTerminal.printInfo("Self-Knowledge:");
        CommandTerminal.print(String.format("  Uncertainty Level: %.4f", knowledge.uncertaintyLevel));
        CommandTerminal.print(String.format("  Exploration Needed: %s", knowledge.explorationNeeded ? "YES" : "NO"));
    }

    /**
     * Container for self-knowledge
     */
    public static class SelfKnowledge {
        public double consciousnessLevel;
        public double processingCoherence;
        public double selfAwarenessScore;
        public int patternsRecognized;
        public int metaPatternsFound;
        public double phiResonance;
        public double successRate;
        public double uncertaintyLevel;
        public boolean explorationNeeded;
    }
}
