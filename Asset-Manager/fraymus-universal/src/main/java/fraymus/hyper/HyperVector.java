package fraymus.hyper;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.BitSet;

/**
 * HyperVector (10k bits)
 * - BIND: XOR (reversible)
 * - PERMUTE: cyclic shift (positional encoding)
 * - RESONANCE: 1 - normalized Hamming distance
 * - ENTROPY: fraction of 1s (proxy)
 */
public final class HyperVector implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int D = 10_000;
    private final BitSet bits;

    public HyperVector() { this.bits = new BitSet(D); }
    private HyperVector(BitSet b) { this.bits = b; }

    /** Random vector (~50% ones). */
    public static HyperVector random() {
        SecureRandom rng = new SecureRandom();
        BitSet b = new BitSet(D);
        byte[] buf = new byte[(D + 7) / 8];
        rng.nextBytes(buf);
        for (int i = 0; i < D; i++) {
            int bi = i >>> 3;
            int bj = i & 7;
            if (((buf[bi] >>> bj) & 1) == 1) b.set(i);
        }
        return new HyperVector(b);
    }

    /** Deterministic vector from string (stable identity). */
    public static HyperVector fromSeed(String seed) {
        long h = 1469598103934665603L;
        for (int i = 0; i < seed.length(); i++) {
            h ^= seed.charAt(i);
            h *= 1099511628211L;
        }
        XorShift64 rng = new XorShift64(h);
        BitSet b = new BitSet(D);
        for (int i = 0; i < D; i++) {
            if ((rng.nextLong() & 1L) == 1L) b.set(i);
        }
        return new HyperVector(b);
    }

    /** Backward-compat alias: deterministic vector from long seed. */
    public static HyperVector seeded(long seed) {
        return fromSeed(Long.toHexString(seed));
    }

    public HyperVector copy() {
        return new HyperVector((BitSet) bits.clone());
    }

    /** BIND (XOR): reversible concept binding. */
    public HyperVector bind(HyperVector other) {
        BitSet out = (BitSet) this.bits.clone();
        out.xor(other.bits);
        return new HyperVector(out);
    }

    /** Inverse is itself for XOR binding. */
    public HyperVector inverse() { return this; }

    /** PERMUTE: cyclic shift by steps (positional / time encoding). */
    public HyperVector permute(int steps) {
        int s = mod(steps, D);
        if (s == 0) return this;
        BitSet out = new BitSet(D);
        for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            out.set((i + s) % D);
        }
        return new HyperVector(out);
    }

    /** RESONANCE: 1 - normalized Hamming distance. */
    public double resonance(HyperVector other) {
        BitSet x = (BitSet) this.bits.clone();
        x.xor(other.bits);
        int dist = x.cardinality();
        return 1.0 - (dist / (double) D);
    }

    public int hammingDistance(HyperVector other) {
        BitSet x = (BitSet) this.bits.clone();
        x.xor(other.bits);
        return x.cardinality();
    }

    /** XOR-Entropy proxy: lower = closer match. */
    public double xorEntropy(HyperVector other) {
        BitSet x = (BitSet) this.bits.clone();
        x.xor(other.bits);
        return x.cardinality() / (double) D;
    }

    /** Density / entropy proxy: fraction of ones. */
    public double density() {
        return bits.cardinality() / (double) D;
    }

    /**
     * IN-PLACE BUNDLE: approximate merge for FraySH sentence encoding.
     * For proper N-vector bundling, use HyperAccumulator.
     */
    public void bundle(HyperVector other) {
        BitSet combined = (BitSet) this.bits.clone();
        combined.or(other.bits);
        BitSet shifted = new BitSet(D);
        for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            shifted.set((i + 1) % D);
        }
        combined.and(shifted);
        combined.or(other.bits);
        this.bits.clear();
        this.bits.or(combined);
    }

    public BitSet rawBitsCopy() { return (BitSet) bits.clone(); }

    /** Convert to compact stable bytes for storage. */
    public byte[] toBytes() { return bits.toByteArray(); }

    public static HyperVector fromBytes(byte[] bytes) {
        BitSet b = BitSet.valueOf(bytes);
        if (b.length() > D) b.clear(D, b.length());
        return new HyperVector(b);
    }

    /** Package-private fast view (NO CLONE). Use read-only. */
    BitSet rawBitsUnsafe() { return bits; }

    static HyperVector fromBits(BitSet b) {
        return new HyperVector((BitSet) b.clone());
    }

    private static int mod(int a, int m) {
        int r = a % m;
        return (r < 0) ? r + m : r;
    }

    /** tiny deterministic PRNG */
    private static final class XorShift64 implements Serializable {
        private static final long serialVersionUID = 1L;
        private long x;
        XorShift64(long seed) { this.x = (seed == 0 ? 0x9E3779B97F4A7C15L : seed); }
        long nextLong() {
            long z = x;
            z ^= (z << 13);
            z ^= (z >>> 7);
            z ^= (z << 17);
            x = z;
            return z;
        }
    }
}
