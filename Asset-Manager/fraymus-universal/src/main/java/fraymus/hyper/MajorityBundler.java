package fraymus.hyper;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Weighted Majority Vote Bundling.
 * Combines multiple vectors into one without losing information.
 * Supersedes MajorityBundler with weighted add support.
 */
public final class MajorityBundler implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int[] accum = new int[HyperVector.D];

    public void add(HyperVector v, int weight) {
        if (weight <= 0) return;
        BitSet b = v.rawBitsCopy();

        for (int i = 0; i < HyperVector.D; i++) {
            int val = b.get(i) ? 1 : -1;
            accum[i] += (val * weight);
        }
    }

    public HyperVector build() {
        BitSet out = new BitSet(HyperVector.D);
        for (int i = 0; i < HyperVector.D; i++) {
            if (accum[i] > 0) out.set(i);
            else if (accum[i] == 0) out.set(i, Math.random() > 0.5);
        }
        return HyperVector.fromBits(out);
    }
}
