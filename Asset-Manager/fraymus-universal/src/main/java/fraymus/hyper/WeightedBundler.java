package fraymus.hyper;

import java.io.Serializable;
import java.util.BitSet;

public final class WeightedBundler implements Serializable {

    private static final long serialVersionUID = 1L;

    /** bias applies to all dimensions */
    private int bias = 0;

    /** delta[i] stores per-dimension adjustments (bias+delta[i] is the accumulator) */
    private final int[] delta = new int[HyperVector.D];

    private int totalWeight = 0;

    public void add(HyperVector v) { add(v, 1); }

    public void add(HyperVector v, int weight) {
        if (weight <= 0) return;

        bias -= weight;

        BitSet b = v.rawBitsUnsafe(); // NO CLONE
        int twoW = weight << 1;
        for (int i = b.nextSetBit(0); i >= 0; i = b.nextSetBit(i + 1)) {
            delta[i] += twoW;
        }

        totalWeight += weight;
    }

    public int getTotalWeight() { return totalWeight; }

    /** Right-shift aging: divides all accumulators by 2^k (fast "forgetting"). */
    public void ageShift(int k) {
        if (k <= 0) return;
        bias >>= k;
        for (int i = 0; i < delta.length; i++) {
            delta[i] >>= k;
        }
        totalWeight >>= k;
        if (totalWeight < 0) totalWeight = 0;
    }

    /** Build binary HyperVector by thresholding >=0 */
    public HyperVector build() {
        BitSet out = new BitSet(HyperVector.D);
        for (int i = 0; i < delta.length; i++) {
            if (bias + delta[i] >= 0) out.set(i);
        }
        return HyperVector.fromBits(out);
    }

    /** Snapshot for persistence */
    public State snapshot() {
        int[] d = new int[delta.length];
        System.arraycopy(delta, 0, d, 0, delta.length);
        return new State(bias, d, totalWeight);
    }

    /** Restore from snapshot */
    public static WeightedBundler fromSnapshot(State s) {
        WeightedBundler b = new WeightedBundler();
        b.bias = s.bias();
        System.arraycopy(s.delta(), 0, b.delta, 0, b.delta.length);
        b.totalWeight = s.totalWeight();
        return b;
    }

    public record State(int bias, int[] delta, int totalWeight) implements Serializable {}
}
