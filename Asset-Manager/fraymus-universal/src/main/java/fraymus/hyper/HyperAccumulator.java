package fraymus.hyper;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Proper BUNDLE (superposition) as a majority vote accumulator.
 * This fixes the "just OR it" bug — OR destroys information and makes everything converge to all-ones.
 */
public final class HyperAccumulator implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int[] votes = new int[HyperVector.D];
    private int count = 0;

    public void add(HyperVector v, int weight) {
        BitSet b = v.rawBitsCopy();
        for (int i = 0; i < HyperVector.D; i++) {
            votes[i] += b.get(i) ? weight : -weight;
        }
        count += weight;
    }

    public void add(HyperVector v) { add(v, 1); }

    /** Quantize to bits by sign (majority). */
    public HyperVector toVector() {
        BitSet out = new BitSet(HyperVector.D);
        for (int i = 0; i < HyperVector.D; i++) {
            if (votes[i] >= 0) out.set(i);
        }
        return HyperVector.fromBytes(out.toByteArray());
    }

    /** Alias for downstream compat (WeightedBundler.build). */
    public HyperVector build() { return toVector(); }

    public int samples() { return count; }

    public void clear() {
        for (int i = 0; i < votes.length; i++) votes[i] = 0;
        count = 0;
    }
}
