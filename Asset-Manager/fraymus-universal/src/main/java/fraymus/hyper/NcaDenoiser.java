package fraymus.hyper;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Feed-Forward Replacement:
 * A tiny Neural Cellular Automata "cleanup" pass over the bit lattice.
 *
 * Rule used: 150 (new = L XOR C XOR R)
 * - fast
 * - purely XOR / bit logic
 * - helps reduce random speckle after heavy bundling
 */
public final class NcaDenoiser implements Serializable {
    private static final long serialVersionUID = 1L;

    public HyperVector denoise(HyperVector v, int steps) {
        BitSet cur = v.rawBitsCopy();
        int D = HyperVector.D;

        for (int s = 0; s < steps; s++) {
            BitSet next = new BitSet(D);

            for (int i = 0; i < D; i++) {
                boolean L = cur.get((i - 1 + D) % D);
                boolean C = cur.get(i);
                boolean R = cur.get((i + 1) % D);

                boolean out = L ^ C ^ R; // Rule 150
                if (out) next.set(i);
            }
            cur = next;
        }
        return HyperVector.fromBytes(cur.toByteArray());
    }

    /** Backward-compat alias for Layer 2 callers. */
    public HyperVector denoiseMajority(HyperVector v, int steps) {
        return denoise(v, steps);
    }
}
