package fraymus.quantum.core;

/**
 * Core Phi-Harmonic Mathematics
 * 
 * All resonances normalized to golden ratio range [1.0, φ)
 * Uses modulo (φ - 1.0) = 0.618 (phi inverse)
 */
public class PhiHarmonicMath {
    
    public static final double PHI = 1.618033988749895;
    public static final double PHI_INVERSE = 0.6180339887498949;
    public static final double PHI_SQUARED = 2.618033988749895;
    public static final double PHI_PI = PHI * Math.PI;  // ≈ 5.083
    
    // Core phi operations
    
    public static double phiPower(double exponent) {
        return Math.pow(PHI, exponent);
    }
    
    public static double phiModulo(double value) {
        return value % PHI_INVERSE;
    }
    
    /**
     * Normalize value to golden ratio range [1.0, φ)
     * This is the fundamental normalization used throughout the system.
     */
    public static double normalizeToPhiRange(double value) {
        return 1.0 + phiModulo(Math.abs(value));
    }
    
    // Resonance calculations
    
    public static double calculateResonance(double frequency) {
        return normalizeToPhiRange(frequency * PHI);
    }
    
    public static double combineResonances(double r1, double r2) {
        return normalizeToPhiRange(r1 * r2);
    }
    
    public static double calculateSecondaryResonance(double baseResonance) {
        return normalizeToPhiRange(baseResonance * PHI);
    }
    
    public static double calculateTemporalResonance(double[] resonances, double[] weights) {
        if (resonances.length == 0) return 1.0;
        
        double weighted = 0;
        for (int i = 0; i < resonances.length; i++) {
            weighted += resonances[i] * weights[i];
        }
        return normalizeToPhiRange(weighted);
    }
    
    // Temporal decay functions
    
    public static double exponentialDecay(double ageSeconds) {
        return Math.exp(-ageSeconds);
    }
    
    public static double weightedDecay(double ageSeconds, int patternMatches) {
        return exponentialDecay(ageSeconds) * (1.0 + patternMatches);
    }
    
    // Frequency calculations
    
    public static double calculatePhiHarmonic(int baseFrequency, int harmonicIndex) {
        return baseFrequency * phiPower(harmonicIndex % 7);
    }
    
    public static double calculateFrequencyResonance(String text) {
        if (text == null || text.isEmpty()) return 1.0;
        double sum = 0;
        for (char c : text.toCharArray()) {
            sum += c * PHI;
        }
        return normalizeToPhiRange(sum / (text.length() * 128.0));
    }
    
    // Complex wave calculations for quantum synchronization
    
    public static double[] calculateSyncWave(double time) {
        double angle = PHI_PI * time;
        return new double[]{Math.cos(angle), Math.sin(angle)};
    }
    
    public static double calculateWaveAmplitude(double[] complexWave) {
        return Math.sqrt(complexWave[0] * complexWave[0] + complexWave[1] * complexWave[1]);
    }
    
    public static double[] multiplyComplexWaves(double[] wave1, double[] wave2) {
        // (a + bi)(c + di) = (ac - bd) + (ad + bc)i
        return new double[]{
            wave1[0] * wave2[0] - wave1[1] * wave2[1],
            wave1[0] * wave2[1] + wave1[1] * wave2[0]
        };
    }
}
