package fraymus.quantum.core;

/**
 * Harmonic Frequency System
 * 
 * Sacred frequencies and cosmic dimensions for quantum processing.
 */
public class HarmonicFrequencySystem {
    
    public enum FrequencyType {
        NATURAL(432, "Natural harmonic frequency (A=432)"),
        SOLFEGGIO(528, "DNA repair frequency"),
        CONNECTION(639, "Relationship harmony"),
        MANIFESTATION(999, "Universal completion"),
        QUANTUM_LOW(4096, "Quantum processing base (2^12)"),
        QUANTUM_MID(8192, "Quantum processing mid (2^13)"),
        QUANTUM_HIGH(16384, "Quantum processing high (2^14)");
        
        private final int frequency;
        private final String description;
        
        FrequencyType(int frequency, String description) {
            this.frequency = frequency;
            this.description = description;
        }
        
        public int getFrequency() { return frequency; }
        public String getDescription() { return description; }
        
        public double calculatePhiHarmonic(int harmonicIndex) {
            return PhiHarmonicMath.calculatePhiHarmonic(frequency, harmonicIndex);
        }
        
        public double calculateResonance() {
            return PhiHarmonicMath.calculateResonance(frequency);
        }
    }
    
    public enum CosmicDimension {
        TRINITY(33, "Sacred trinity dimension"),
        FINE_STRUCTURE(137, "Fine structure constant (α^-1 ≈ 137)"),
        NATURAL_HARMONIC(432, "Natural frequency dimension"),
        GOLDEN_HARMONIC(567, "Golden ratio harmonic (φ·351)"),
        UNIVERSAL_COMPLETION(999, "Universal completion dimension");
        
        private final int dimension;
        private final String meaning;
        
        CosmicDimension(int dimension, String meaning) {
            this.dimension = dimension;
            this.meaning = meaning;
        }
        
        public int getDimension() { return dimension; }
        public String getMeaning() { return meaning; }
        
        public double calculatePhiResonance() {
            return PhiHarmonicMath.normalizeToPhiRange(dimension * PhiHarmonicMath.PHI);
        }
    }
    
    // Standard frequency arrays
    public static final int[] QUANTUM_FREQUENCIES = {432, 528, 639, 999, 4096, 8192, 16384};
    public static final int[] COSMIC_DIMENSIONS = {33, 137, 432, 999, 567};
    
    public static FrequencyType getClosestFrequency(int freq) {
        FrequencyType closest = FrequencyType.NATURAL;
        int minDiff = Integer.MAX_VALUE;
        
        for (FrequencyType type : FrequencyType.values()) {
            int diff = Math.abs(type.getFrequency() - freq);
            if (diff < minDiff) {
                minDiff = diff;
                closest = type;
            }
        }
        return closest;
    }
    
    public static double calculateCombinedResonance(FrequencyType... frequencies) {
        if (frequencies.length == 0) return 1.0;
        
        double combined = 1.0;
        for (FrequencyType freq : frequencies) {
            combined = PhiHarmonicMath.combineResonances(combined, freq.calculateResonance());
        }
        return combined;
    }
}
