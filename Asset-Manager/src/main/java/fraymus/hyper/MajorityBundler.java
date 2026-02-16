package fraymus.hyper;

import java.util.BitSet;

/**
 * 🗳️ MAJORITY BUNDLER - Stable Superposition with Integer Accumulators
 * "True bundling without saturation"
 * 
 * Majority vote bundling with integer accumulators.
 * Add vectors → counters; finalize → threshold to bits.
 * 
 * Traditional bundling (OR saturation):
 * - Problem: Bits saturate to 1, loses information
 * - Result: All bundled vectors look the same after a few additions
 * 
 * Majority bundling (integer accumulators):
 * - Solution: Track +1/-1 votes per dimension
 * - Result: Stable superposition that preserves information
 * 
 * This is how biological neurons actually work - vote counting, not saturation.
 */
public class MajorityBundler {

    private final int[] accum; // +1 for bit=1, -1 for bit=0
    private int count;

    public MajorityBundler() {
        this.accum = new int[HyperVector.D];
        this.count = 0;
    }

    /**
     * Add a vector to the bundle
     * 
     * For each dimension:
     * - If bit is set: vote +1
     * - If bit is clear: vote -1
     */
    public void add(HyperVector v) {
        BitSet b = v.rawBits();

        // Efficient two-pass approach:
        // 1) Decrement all dims by 1 (vote for 0)
        // 2) For set bits, add 2 (net +1, vote for 1)
        for (int i = 0; i < accum.length; i++) {
            accum[i] -= 1;
        }

        for (int i = b.nextSetBit(0); i >= 0; i = b.nextSetBit(i + 1)) {
            accum[i] += 2;
        }
        
        count++;
    }

    /**
     * Get number of vectors bundled
     */
    public int getCount() {
        return count;
    }

    /**
     * Finalize to binary vector
     * 
     * Majority rule: bit=1 if accum[i] >= 0 else 0
     */
    public HyperVector build() {
        BitSet out = new BitSet(HyperVector.D);
        for (int i = 0; i < accum.length; i++) {
            if (accum[i] >= 0) {
                out.set(i);
            }
        }
        return HyperVector.fromBits(out);
    }
}
