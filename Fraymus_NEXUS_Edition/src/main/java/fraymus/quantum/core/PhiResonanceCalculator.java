package fraymus.quantum.core;

import fraymus.PhiConstants;

/**
 * Specialized calculator for phi-resonance operations
 * Handles the core resonance calculations used throughout the quantum system
 */
public class PhiResonanceCalculator {
    
    /**
     * Calculate base resonance from frequency
     * @param frequency Input frequency
     * @return Resonance in [1.0, φ) range
     */
    public static double calculateBaseResonance(double frequency) {
        return PhiHarmonicMath.calculateResonance(frequency);
    }
    
    /**
     * Calculate secondary resonance from base resonance
     * @param baseResonance The base resonance value
     * @return Secondary resonance in [1.0, φ) range
     */
    public static double calculateSecondaryResonance(double baseResonance) {
        return PhiHarmonicMath.combineResonances(baseResonance, PhiConstants.PHI);
    }
    
    /**
     * Calculate secondary resonance with pattern boost
     * @param baseResonance The base resonance value
     * @param patternCount Number of detected patterns
     * @return Secondary resonance with pattern influence
     */
    public static double calculateSecondaryResonanceWithPatterns(double baseResonance, int patternCount) {
        double boost = 1.0 + (patternCount / 10.0);
        return PhiHarmonicMath.normalizeToPhiRange(baseResonance * boost);
    }
    
    /**
     * Calculate temporal resonance from weighted history
     * @param resonances Array of historical resonance values
     * @param weights Array of weights (typically from exponential decay)
     * @return Temporal resonance in [1.0, φ) range
     */
    public static double calculateTemporalResonance(double[] resonances, double[] weights) {
        double weighted = PhiHarmonicMath.weightedAverage(resonances, weights);
        return PhiHarmonicMath.normalizeToPhiRange(weighted);
    }
    
    /**
     * Calculate resonance from text content
     * Uses character sum divided by normalized length
     * @param text Input text
     * @return Base resonance value
     */
    public static double calculateTextResonance(String text) {
        if (text == null || text.isEmpty()) {
            return 1.0;
        }
        
        int charSum = 0;
        for (char c : text.toCharArray()) {
            charSum += c;
        }
        
        double patternFreq = charSum / (double)(text.length() * 128);
        return calculateBaseResonance(patternFreq);
    }
    
    /**
     * Calculate resonance with pattern category boost
     * @param text Input text
     * @param patternCount Number of detected pattern categories
     * @return Boosted resonance value
     */
    public static double calculateTextResonanceWithPatterns(String text, int patternCount) {
        double baseFreq = 0;
        
        if (text == null || text.isEmpty()) {
            baseFreq = 0;
        } else {
            int charSum = 0;
            for (char c : text.toCharArray()) {
                charSum += c;
            }
            baseFreq = charSum / (double)(text.length() * 128);
        }
        
        double categoryBoost = patternCount * 0.1;
        return calculateBaseResonance(baseFreq + categoryBoost);
    }
    
    /**
     * Calculate resonance for mathematical constants
     * @param constant The constant name (pi, phi, e)
     * @return Resonance value for the constant
     */
    public static double calculateConstantResonance(String constant) {
        switch (constant.toLowerCase()) {
            case "pi":
            case "π":
                return calculateBaseResonance(Math.PI);
            case "phi":
            case "φ":
                return calculateBaseResonance(PhiConstants.PHI);
            case "e":
                return calculateBaseResonance(Math.E);
            default:
                return 1.0;
        }
    }
}
