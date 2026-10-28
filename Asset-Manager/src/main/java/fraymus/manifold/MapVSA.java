package fraymus.manifold;

import java.util.Arrays;
import java.util.Random;

/**
 * MAP-VSA: Hyperdimensional Substrate with Continuous Normalizing Flows
 * Implements the Boolean hypercube H = {-1, 1}^D where D = 16384.
 *
 * Three primitive operations for zero-shot logical deduction:
 *   BINDING     c = a ⊗ b  →  c_i = a_i · b_i   (element-wise product)
 *   BUNDLING    s = sign(Σ v_k)                   (majority vote / superposition)
 *   PERMUTATION ρ(v) = (v_D, v_1, ..., v_{D-1})   (circular left-shift by 1)
 *
 * Sign thresholding S(x): 1 if x > 0, -1 if x ≤ 0
 *
 * CONTINUOUS NORMALIZING FLOWS (CNF):
 * In advanced architectures (like Neural ODEs), the network is treated as a
 * continuous fluid flowing over a lattice (the AkashicRecord graph).
 *
 * Instead of discrete steps, the change in the hidden state h over time t is
 * modeled as a differential equation:
 *   dh(t)/dt = f(h(t), t, θ)
 *
 * To find the final output, we integrate over the flow:
 *   h(T) = h(0) + ∫₀^T f(h(t), t, θ) dt
 *
 * Pre-verse Engineering Application:
 * When Fraynix binds concepts in its 16384-D VSA, the concepts don't just "snap"
 * together. They flow into each other continuously. This prevents the sudden
 * "hallucination spikes" that plague standard LLMs.
 */
public class MapVSA {

    public static final int D = 16384;
    private static final double PHI = (1.0 + Math.sqrt(5.0)) / 2.0;

    // ODE integration parameters
    private static final double ODE_STEP_SIZE = 0.1;
    private static final int ODE_STEPS = 10;

    private final Random rng;
    private long bindCount;
    private long bundleCount;
    private long permuteCount;
    private long recognitionCount;
    private long continuousBindCount;
    private long continuousBundleCount;

    public MapVSA() {
        this.rng = new Random();
    }

    // ─── VECTOR CREATION ────────────────────────────────────────────────────

    /** Generate a random bipolar vector in {-1,1}^D */
    public byte[] random() {
        byte[] v = new byte[D];
        for (int i = 0; i < D; i++) {
            v[i] = rng.nextBoolean() ? (byte) 1 : (byte) -1;
        }
        return v;
    }

    /** Deterministic seed vector from a String key */
    public byte[] seed(String key) {
        long h = 0xcbf29ce484222325L;
        for (char c : key.toCharArray()) {
            h ^= c;
            h *= 0x00000100000001B3L;
        }
        Random seeded = new Random(h);
        byte[] v = new byte[D];
        for (int i = 0; i < D; i++) {
            v[i] = seeded.nextBoolean() ? (byte) 1 : (byte) -1;
        }
        return v;
    }

    // ─── PRIMITIVE OPERATIONS ────────────────────────────────────────────────

    /**
     * BINDING: c = a ⊗ b  →  c_i = a_i · b_i
     * Encodes relationships between concepts. XOR-equivalent in {-1,1} space.
     */
    public byte[] bind(byte[] a, byte[] b) {
        assert a.length == D && b.length == D : "Dimension mismatch in bind()";
        byte[] c = new byte[D];
        for (int i = 0; i < D; i++) {
            c[i] = (byte) (a[i] * b[i]);
        }
        bindCount++;
        return c;
    }

    /**
     * BUNDLING: s = sign(Σ v_k) where sign(x) = 1 if x > 0, -1 if x ≤ 0
     * Combines multiple concepts into a single superposition state.
     */
    public byte[] bundle(byte[]... vectors) {
        assert vectors.length > 0 : "bundle() requires at least one vector";
        int[] sum = new int[D];
        for (byte[] v : vectors) {
            assert v.length == D : "Dimension mismatch in bundle()";
            for (int i = 0; i < D; i++) {
                sum[i] += v[i];
            }
        }
        byte[] s = new byte[D];
        for (int i = 0; i < D; i++) {
            s[i] = (byte) (sum[i] > 0 ? 1 : -1);
        }
        bundleCount++;
        return s;
    }

    /**
     * PERMUTATION: ρ(v) = (v_D, v_1, v_2, ..., v_{D-1})
     * Circular left-shift by 1, encodes sequence and directed causality.
     */
    public byte[] permute(byte[] v) {
        assert v.length == D : "Dimension mismatch in permute()";
        byte[] p = new byte[D];
        p[0] = v[D - 1];
        System.arraycopy(v, 0, p, 1, D - 1);
        permuteCount++;
        return p;
    }

    /**
     * Inverse PERMUTATION: right-shift by 1 (recovers original from permuted).
     */
    public byte[] unPermute(byte[] v) {
        assert v.length == D : "Dimension mismatch in unPermute()";
        byte[] p = new byte[D];
        p[D - 1] = v[0];
        System.arraycopy(v, 1, p, 0, D - 1);
        return p;
    }

    // ─── SIMILARITY ──────────────────────────────────────────────────────────

    /**
     * Cosine similarity in {-1,1}^D space via dot product / D.
     * Returns value in [-1, 1]. Orthogonal → 0, identical → 1, opposite → -1.
     */
    public double similarity(byte[] a, byte[] b) {
        assert a.length == D && b.length == D : "Dimension mismatch in similarity()";
        long dot = 0;
        for (int i = 0; i < D; i++) {
            dot += (long) a[i] * b[i];
        }
        recognitionCount++;
        return (double) dot / D;
    }

    /**
     * Hamming-distance-based similarity (mirrors HyperdimensionalMapper convention).
     * Returns value in [0, 1]. Identical → 1.
     */
    public double hammingSimilarity(byte[] a, byte[] b) {
        int diffCount = 0;
        for (int i = 0; i < D; i++) {
            if (a[i] != b[i]) diffCount++;
        }
        return 1.0 - ((double) diffCount / D);
    }

    // ─── COMPOSITION HELPERS ─────────────────────────────────────────────────

    /** Sequence encoding: ρ^k(base) ⊗ item, for position k in a sequence. */
    public byte[] encodeSequence(byte[] base, byte[] item, int position) {
        byte[] shifted = base;
        for (int k = 0; k < position; k++) {
            shifted = permute(shifted);
        }
        return bind(shifted, item);
    }

    /** Unbind: recovers b given c = bind(a, b), since a ⊗ (a ⊗ b) = b in {-1,1}^D. */
    public byte[] unbind(byte[] a, byte[] c) {
        return bind(a, c);
    }

    // ─── CONTINUOUS NORMALIZING FLOWS (ODE SOLVERS) ───────────────────────────

    /**
     * Differential equation for continuous binding flow:
     * dh(t)/dt = f(h(t), t, θ) where f models the gradual binding of two vectors
     * 
     * The flow interpolates between the initial state and the bound state:
     * h(0) = a (first vector)
     * h(T) = a ⊗ b (bound result)
     */
    private double[] bindingFlow(double[] h, byte[] a, byte[] b, double t) {
        double[] dhdt = new double[D];
        
        // Target state: a ⊗ b
        for (int i = 0; i < D; i++) {
            double target = a[i] * b[i];
            // Flow toward target with damping (prevents oscillation)
            dhdt[i] = (target - h[i]) * 2.0;
        }
        
        return dhdt;
    }

    /**
     * Differential equation for continuous bundling flow:
     * Models the gradual superposition of multiple vectors
     */
    private double[] bundlingFlow(double[] h, byte[][] vectors, double t) {
        double[] dhdt = new double[D];
        
        // Target state: sign(Σ v_k)
        int[] sum = new int[D];
        for (byte[] v : vectors) {
            for (int i = 0; i < D; i++) {
                sum[i] += v[i];
            }
        }
        
        for (int i = 0; i < D; i++) {
            double target = sum[i] > 0 ? 1.0 : -1.0;
            dhdt[i] = (target - h[i]) * 2.0;
        }
        
        return dhdt;
    }

    /**
     * Runge-Kutta 4th order ODE solver
     * Solves: h(T) = h(0) + ∫₀^T f(h(t), t, θ) dt
     */
    private double[] rungeKutta4(java.util.function.Function<Double, double[]> flow, 
                                   double[] h0, double dt, int steps) {
        double[] h = Arrays.copyOf(h0, h0.length);
        double t = 0.0;
        
        for (int step = 0; step < steps; step++) {
            // k1 = f(t, h)
            double[] k1 = flow.apply(t);
            
            // k2 = f(t + dt/2, h + dt*k1/2)
            double[] h2 = new double[D];
            for (int i = 0; i < D; i++) h2[i] = h[i] + dt * k1[i] / 2.0;
            double[] k2 = flow.apply(t + dt / 2.0);
            
            // k3 = f(t + dt/2, h + dt*k2/2)
            double[] h3 = new double[D];
            for (int i = 0; i < D; i++) h3[i] = h[i] + dt * k2[i] / 2.0;
            double[] k3 = flow.apply(t + dt / 2.0);
            
            // k4 = f(t + dt, h + dt*k3)
            double[] h4 = new double[D];
            for (int i = 0; i < D; i++) h4[i] = h[i] + dt * k3[i];
            double[] k4 = flow.apply(t + dt);
            
            // h(t+dt) = h(t) + dt/6 * (k1 + 2*k2 + 2*k3 + k4)
            for (int i = 0; i < D; i++) {
                h[i] += dt / 6.0 * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
            }
            
            t += dt;
        }
        
        return h;
    }

    /**
     * CONTINUOUS BINDING using ODE integration
     * Instead of instant binding a ⊗ b, concepts flow into each other continuously.
     * This prevents sudden "hallucination spikes" in the cognitive manifold.
     */
    public byte[] continuousBind(byte[] a, byte[] b) {
        assert a.length == D && b.length == D : "Dimension mismatch in continuousBind()";
        
        // Initial state: a
        double[] h0 = new double[D];
        for (int i = 0; i < D; i++) h0[i] = a[i];
        
        // Define flow function
        java.util.function.Function<Double, double[]> flow = t -> 
            bindingFlow(h0, a, b, t);
        
        // Integrate using Runge-Kutta
        double[] hFinal = rungeKutta4(flow, h0, ODE_STEP_SIZE, ODE_STEPS);
        
        // Threshold to bipolar {-1, 1}
        byte[] result = new byte[D];
        for (int i = 0; i < D; i++) {
            result[i] = hFinal[i] > 0 ? (byte) 1 : (byte) -1;
        }
        
        continuousBindCount++;
        return result;
    }

    /**
     * CONTINUOUS BUNDLING using ODE integration
     * Multiple concepts flow into a superposition state continuously.
     */
    public byte[] continuousBundle(byte[]... vectors) {
        assert vectors.length > 0 : "continuousBundle() requires at least one vector";
        for (byte[] v : vectors) {
            assert v.length == D : "Dimension mismatch in continuousBundle()";
        }
        
        // Initial state: first vector
        double[] h0 = new double[D];
        for (int i = 0; i < D; i++) h0[i] = vectors[0][i];
        
        // Define flow function
        java.util.function.Function<Double, double[]> flow = t -> 
            bundlingFlow(h0, vectors, t);
        
        // Integrate using Runge-Kutta
        double[] hFinal = rungeKutta4(flow, h0, ODE_STEP_SIZE, ODE_STEPS);
        
        // Threshold to bipolar {-1, 1}
        byte[] result = new byte[D];
        for (int i = 0; i < D; i++) {
            result[i] = hFinal[i] > 0 ? (byte) 1 : (byte) -1;
        }
        
        continuousBundleCount++;
        return result;
    }

    /**
     * Get gradient of the flow at a given time step
     * Useful for understanding the "direction" of thought flow
     */
    public double[] getBindingGradient(byte[] a, byte[] b, double t) {
        double[] h0 = new double[D];
        for (int i = 0; i < D; i++) h0[i] = a[i];
        return bindingFlow(h0, a, b, t);
    }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        return String.format(
            "MapVSA | D=%d | φ=%.5f | binds=%d bundles=%d permutes=%d recognitions=%d | continuousBinds=%d continuousBundles=%d",
            D, PHI, bindCount, bundleCount, permuteCount, recognitionCount, continuousBindCount, continuousBundleCount
        );
    }

    public long getBindCount()        { return bindCount; }
    public long getBundleCount()      { return bundleCount; }
    public long getPermuteCount()     { return permuteCount; }
    public long getRecognitionCount() { return recognitionCount; }
    public long getContinuousBindCount() { return continuousBindCount; }
    public long getContinuousBundleCount() { return continuousBundleCount; }
}
