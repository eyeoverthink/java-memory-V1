package brain_architecture.synapses;

import java.util.BitSet;
import java.util.Random;

/**
 * HyperdimensionalMapper - Maps concepts to 16,384-D binary vectors.
 * 
 * Simplified implementation for the brain architecture.
 * Uses deterministic hash-based mapping for consistency.
 */
public class HyperdimensionalMapper {
    
    private static final int DIMENSION = 16384;
    private final Random random;
    
    public HyperdimensionalMapper() {
        this.random = new Random(42); // Fixed seed for determinism
    }
    
    /**
     * Map a concept to a 16,384-D binary vector.
     * Deterministic: same concept always maps to same vector.
     */
    public BitSet map(String concept) {
        BitSet vector = new BitSet(DIMENSION);
        
        // Use concept hash for deterministic mapping
        int hash = concept.hashCode();
        random.setSeed(hash);
        
        // Set approximately 50% of bits
        for (int i = 0; i < DIMENSION; i++) {
            if (random.nextBoolean()) {
                vector.set(i);
            }
        }
        
        return vector;
    }
    
    /**
     * Get the dimension of the HDC vectors.
     */
    public int getDimension() {
        return DIMENSION;
    }
    
    /**
     * Bind two vectors via XOR (holographic binding).
     */
    public BitSet bind(BitSet vectorA, BitSet vectorB) {
        BitSet result = (BitSet) vectorA.clone();
        result.xor(vectorB);
        return result;
    }
    
    /**
     * Bundle multiple vectors via majority vote.
     */
    public BitSet bundle(BitSet... vectors) {
        BitSet result = new BitSet(DIMENSION);
        int[] counts = new int[DIMENSION];
        
        for (BitSet vector : vectors) {
            for (int i = 0; i < DIMENSION; i++) {
                if (vector.get(i)) {
                    counts[i]++;
                } else {
                    counts[i]--;
                }
            }
        }
        
        // Majority vote
        for (int i = 0; i < DIMENSION; i++) {
            if (counts[i] > 0) {
                result.set(i);
            }
        }
        
        return result;
    }
}
