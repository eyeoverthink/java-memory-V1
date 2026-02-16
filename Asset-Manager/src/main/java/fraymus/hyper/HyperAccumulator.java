package fraymus.hyper;

import java.io.Serializable;
import java.util.BitSet;

/**
 * 🔢 HYPER ACCUMULATOR - Majority-Vote Bundling
 * "Proper HDC bundling with integer weights"
 * 
 * Rather than OR-ing bits, we maintain integer weights per dimension
 * and threshold back to binary. This is classic HDC bundling.
 * 
 * This replaces WeightedBundler with a cleaner, more accurate implementation.
 */
public class HyperAccumulator implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int[] weights;
    private int count = 0;

    public HyperAccumulator() {
        this.weights = new int[HyperVector.D];
    }

    /**
     * Add a vector to the accumulator
     */
    public void add(HyperVector v) {
        BitSet bits = v.rawBitsCopy();
        for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            weights[i] += 1;
        }
        count++;
    }

    /**
     * Convert accumulated weights to binary HyperVector using majority threshold
     */
    public HyperVector toVector() {
        BitSet out = new BitSet(HyperVector.D);
        int threshold = count / 2;
        
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] > threshold) {
                out.set(i);
            }
        }
        
        return HyperVector.fromBits(out);
    }

    /**
     * Get number of vectors accumulated
     */
    public int size() {
        return count;
    }

    /**
     * Get total weight (for statistics)
     */
    public int getTotalWeight() {
        int total = 0;
        for (int w : weights) {
            total += Math.abs(w);
        }
        return total;
    }
}
