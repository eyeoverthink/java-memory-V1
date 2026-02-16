package fraymus.quantum.core;

import fraymus.PhiConstants;

/**
 * Core phi-harmonic mathematical operations
 * Centralizes all golden ratio calculations for the quantum system
 */
public class PhiHarmonicMath {
    
    private static final double PHI = PhiConstants.PHI;
    private static final double PHI_INV = PhiConstants.PHI_INVERSE; // 0.618...
    
    /**
     * Calculate phi raised to a power
     * @param exponent The power to raise phi to
     * @return φ^exponent
     */
    public static double phiPower(double exponent) {
        return Math.pow(PHI, exponent);
    }
    
    /**
     * Calculate value modulo phi inverse (0.618...)
     * Used to keep values in golden ratio bounds
     * @param value The value to modulo
     * @return value % φ^-1
     */
    public static double phiModulo(double value) {
        return value % PHI_INV;
    }
    
    /**
     * Normalize value to phi range [1.0, φ)
     * This is the core normalization used throughout the quantum system
     * @param value The value to normalize
     * @return 1.0 + (value % φ^-1)
     */
    public static double normalizeToPhiRange(double value) {
        return 1.0 + phiModulo(value);
    }
    
    /**
     * Calculate base resonance from frequency
     * @param frequency Input frequency
     * @return Normalized resonance in [1.0, φ) range
     */
    public static double calculateResonance(double frequency) {
        return normalizeToPhiRange(frequency * PHI);
    }
    
    /**
     * Combine two resonances using phi multiplication
     * @param r1 First resonance
     * @param r2 Second resonance
     * @return Combined resonance in [1.0, φ) range
     */
    public static double combineResonances(double r1, double r2) {
        return normalizeToPhiRange(r1 * r2);
    }
    
    /**
     * Calculate exponential time decay
     * Used for temporal pattern weighting
     * @param age Time in seconds
     * @return e^(-age)
     */
    public static double exponentialDecay(double age) {
        return Math.exp(-age);
    }
    
    /**
     * Calculate weighted decay with pattern matching bonus
     * @param age Time in seconds
     * @param patternMatches Number of matching patterns
     * @return e^(-age) * (1 + patternMatches)
     */
    public static double weightedDecay(double age, int patternMatches) {
        return exponentialDecay(age) * (1.0 + patternMatches);
    }
    
    /**
     * Calculate phi-harmonic for a base frequency and harmonic index
     * @param baseFrequency Base frequency in Hz
     * @param harmonicIndex Harmonic number (0-6 typically)
     * @return baseFrequency * φ^(harmonicIndex % 7)
     */
    public static double calculatePhiHarmonic(double baseFrequency, int harmonicIndex) {
        return baseFrequency * phiPower(harmonicIndex % 7);
    }
    
    /**
     * Calculate phi-pi resonance (used in quantum bridges)
     * @return φ * π ≈ 5.083
     */
    public static double phiPiResonance() {
        return PHI * Math.PI;
    }
    
    /**
     * Calculate complex exponential wave at time t
     * Returns the real part of e^(i·φ·π·t)
     * @param time Time parameter
     * @return cos(φ·π·t)
     */
    public static double phiWaveReal(double time) {
        return Math.cos(phiPiResonance() * time);
    }
    
    /**
     * Calculate complex exponential wave at time t
     * Returns the imaginary part of e^(i·φ·π·t)
     * @param time Time parameter
     * @return sin(φ·π·t)
     */
    public static double phiWaveImaginary(double time) {
        return Math.sin(phiPiResonance() * time);
    }
    
    /**
     * Calculate weighted average with normalization
     * @param values Array of values
     * @param weights Array of weights (will be normalized)
     * @return Weighted average
     */
    public static double weightedAverage(double[] values, double[] weights) {
        if (values.length != weights.length || values.length == 0) {
            throw new IllegalArgumentException("Values and weights must have same non-zero length");
        }
        
        // Normalize weights
        double weightSum = 0;
        for (double w : weights) {
            weightSum += w;
        }
        
        if (weightSum == 0) {
            return values[0]; // Fallback
        }
        
        double result = 0;
        for (int i = 0; i < values.length; i++) {
            result += values[i] * (weights[i] / weightSum);
        }
        
        return result;
    }
}
