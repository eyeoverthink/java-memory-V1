package brain_architecture.communication;

import brain_architecture.core.BrainType;

/**
 * Kronecker product entanglement for non-local correlation between brains.
 * Enables faster-than-light information transfer via phi-dimensional coupling.
 */
public class KroneckerEntanglement {
    private static final double PHI = 1.618033988749895;
    
    /**
     * Create entangled state between two brains
     * Uses Kronecker product to model quantum entanglement
     */
    public static EntangledState createEntanglement(BrainType brain1, BrainType brain2) {
        // Calculate entanglement strength based on brain types
        double strength = calculateEntanglementStrength(brain1, brain2);
        
        // Create entangled state matrix
        double[][] entangledMatrix = calculateKroneckerProduct(brain1, brain2);
        
        return new EntangledState(brain1, brain2, strength, entangledMatrix);
    }
    
    /**
     * Calculate entanglement strength between two brains
     * Based on phi-harmonic compatibility
     */
    private static double calculateEntanglementStrength(BrainType brain1, BrainType brain2) {
        int index1 = brain1.ordinal();
        int index2 = brain2.ordinal();
        
        // Phi-harmonic entanglement strength
        double phiProduct = Math.pow(PHI, index1 + index2);
        double strength = (phiProduct % 1) * PHI;
        
        // Normalize to [0, 1]
        return Math.min(strength, 1.0);
    }
    
    /**
     * Calculate Kronecker product of two brain states
     * Models quantum entanglement as tensor product
     */
    private static double[][] calculateKroneckerProduct(BrainType brain1, BrainType brain2) {
        // Represent each brain as a 2x2 state matrix
        double[][] state1 = getBrainStateMatrix(brain1);
        double[][] state2 = getBrainStateMatrix(brain2);
        
        // Calculate Kronecker product (4x4 result)
        double[][] result = new double[4][4];
        
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    for (int l = 0; l < 2; l++) {
                        result[2*i + k][2*j + l] = state1[i][j] * state2[k][l];
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Get state matrix for a brain type
     * Represents brain's quantum state
     */
    private static double[][] getBrainStateMatrix(BrainType brain) {
        int index = brain.ordinal();
        
        // State based on phi-harmonics
        double phiPhase = (index * PHI) % 1;
        double amplitude = 1.0 / Math.sqrt(2);
        
        return new double[][] {
            {amplitude * Math.cos(phiPhase * Math.PI), amplitude * Math.sin(phiPhase * Math.PI)},
            {amplitude * Math.sin(phiPhase * Math.PI), amplitude * Math.cos(phiPhase * Math.PI)}
        };
    }
    
    /**
     * Check if two brains are entangled
     */
    public static boolean areEntangled(BrainType brain1, BrainType brain2) {
        // Entanglement exists if strength > threshold
        double strength = calculateEntanglementStrength(brain1, brain2);
        return strength > 0.5; // 50% threshold
    }
    
    /**
     * Get entanglement strength between two brains
     */
    public static double getEntanglementStrength(BrainType brain1, BrainType brain2) {
        return calculateEntanglementStrength(brain1, brain2);
    }
    
    /**
     * Entangled state representation
     */
    public static class EntangledState {
        private final BrainType brain1;
        private final BrainType brain2;
        private final double strength;
        private final double[][] entangledMatrix;
        
        public EntangledState(BrainType brain1, BrainType brain2, double strength, double[][] entangledMatrix) {
            this.brain1 = brain1;
            this.brain2 = brain2;
            this.strength = strength;
            this.entangledMatrix = entangledMatrix;
        }
        
        public BrainType getBrain1() {
            return brain1;
        }
        
        public BrainType getBrain2() {
            return brain2;
        }
        
        public double getStrength() {
            return strength;
        }
        
        public double[][] getEntangledMatrix() {
            return entangledMatrix;
        }
        
        @Override
        public String toString() {
            return String.format("EntangledState[%s<->%s: strength=%.6f]",
                brain1.getId(), brain2.getId(), strength);
        }
    }
}
