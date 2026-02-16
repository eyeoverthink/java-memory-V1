package fraymus.quantum.core;

import fraymus.PhiConstants;

/**
 * Harmonic frequency system with sacred frequencies and cosmic dimensions
 * Based on the Quantum Oracle's frequency architecture
 */
public class HarmonicFrequencySystem {
    
    /**
     * Quantum harmonic frequencies used throughout the system
     */
    public enum FrequencyType {
        NATURAL(432, "Natural harmonic frequency (A=432)"),
        SOLFEGGIO(528, "DNA repair frequency (Solfeggio)"),
        CONNECTION(639, "Relationship harmony frequency"),
        MANIFESTATION(999, "Universal completion frequency"),
        QUANTUM_LOW(4096, "Quantum processing base (2^12)"),
        QUANTUM_MID(8192, "Quantum processing mid (2^13)"),
        QUANTUM_HIGH(16384, "Quantum processing high (2^14)");
        
        private final int frequency;
        private final String description;
        
        FrequencyType(int frequency, String description) {
            this.frequency = frequency;
            this.description = description;
        }
        
        public int getFrequency() { 
            return frequency; 
        }
        
        public String getDescription() { 
            return description; 
        }
        
        public double getResonance() {
            return PhiResonanceCalculator.calculateBaseResonance(frequency);
        }
    }
    
    /**
     * Cosmic fractal DNA dimensions
     */
    public enum CosmicDimension {
        TRINITY(33, "Sacred trinity dimension"),
        FINE_STRUCTURE(137, "Fine structure constant (α^-1 ≈ 137.036)"),
        NATURAL_HARMONIC(432, "Natural frequency dimension"),
        UNIVERSAL_COMPLETION(999, "Universal completion dimension"),
        GOLDEN_HARMONIC(567, "Golden ratio harmonic (φ·351 ≈ 567)");
        
        private final int dimension;
        private final String meaning;
        
        CosmicDimension(int dimension, String meaning) {
            this.dimension = dimension;
            this.meaning = meaning;
        }
        
        public int getDimension() { 
            return dimension; 
        }
        
        public String getMeaning() { 
            return meaning; 
        }
    }
    
    /**
     * Calculate phi-harmonic for a base frequency
     * @param baseFrequency Base frequency in Hz
     * @param harmonicIndex Harmonic number (0-6)
     * @return Phi-harmonic frequency
     */
    public static double calculatePhiHarmonic(int baseFrequency, int harmonicIndex) {
        return PhiHarmonicMath.calculatePhiHarmonic(baseFrequency, harmonicIndex);
    }
    
    /**
     * Calculate resonance for a frequency type
     * @param type The frequency type
     * @return Resonance value
     */
    public static double calculateResonance(FrequencyType type) {
        return type.getResonance();
    }
    
    /**
     * Get all quantum harmonic frequencies
     * @return Array of all frequency values
     */
    public static int[] getAllFrequencies() {
        FrequencyType[] types = FrequencyType.values();
        int[] frequencies = new int[types.length];
        for (int i = 0; i < types.length; i++) {
            frequencies[i] = types[i].getFrequency();
        }
        return frequencies;
    }
    
    /**
     * Get all cosmic dimensions
     * @return Array of all dimension values
     */
    public static int[] getAllDimensions() {
        CosmicDimension[] dims = CosmicDimension.values();
        int[] dimensions = new int[dims.length];
        for (int i = 0; i < dims.length; i++) {
            dimensions[i] = dims[i].getDimension();
        }
        return dimensions;
    }
    
    /**
     * Find closest frequency type to a given value
     * @param frequency The frequency to match
     * @return Closest FrequencyType
     */
    public static FrequencyType findClosestFrequency(int frequency) {
        FrequencyType closest = FrequencyType.NATURAL;
        int minDiff = Integer.MAX_VALUE;
        
        for (FrequencyType type : FrequencyType.values()) {
            int diff = Math.abs(type.getFrequency() - frequency);
            if (diff < minDiff) {
                minDiff = diff;
                closest = type;
            }
        }
        
        return closest;
    }
}
