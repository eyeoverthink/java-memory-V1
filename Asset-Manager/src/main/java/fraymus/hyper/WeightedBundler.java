package fraymus.hyper;

import java.util.BitSet;

/**
 * Majority Vote Bundling.
 * Combines multiple vectors into one without losing information.
 */
public final class WeightedBundler implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final int[] accum = new int[HyperVector.D];

    public void add(HyperVector v, int weight) {
        if (weight <= 0) return;
        BitSet b = v.rawBitsCopy();
        
        for (int i = 0; i < HyperVector.D; i++) {
            // Bipolar mapping: 0 -> -1, 1 -> +1
            int val = b.get(i) ? 1 : -1;
            accum[i] += (val * weight);
        }
    }

    public HyperVector build() {
        BitSet out = new BitSet(HyperVector.D);
        for (int i = 0; i < HyperVector.D; i++) {
            if (accum[i] > 0) out.set(i); // Majority Rule
            else if (accum[i] == 0) out.set(i, Math.random() > 0.5); // Break ties randomly
        }
        return HyperVector.fromBits(out);
    }
    
    /**
     * Get total weight (sum of absolute values).
     */
    public int getTotalWeight() {
        int total = 0;
        for (int i = 0; i < accum.length; i++) {
            total += Math.abs(accum[i]);
        }
        return total;
    }
    
    /**
     * Export snapshot for secure serialization.
     */
    public State snapshot() {
        int[] copy = new int[accum.length];
        System.arraycopy(accum, 0, copy, 0, accum.length);
        return new State(copy);
    }
    
    /**
     * Restore from snapshot.
     */
    public static WeightedBundler fromSnapshot(State s) {
        WeightedBundler b = new WeightedBundler();
        System.arraycopy(s.accum(), 0, b.accum, 0, b.accum.length);
        return b;
    }
    
    /**
     * Immutable state container for serialization.
     */
    public record State(
        int[] accum
    ) implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
    }
}
