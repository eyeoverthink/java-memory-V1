package fraymus.manifold;

import java.util.Arrays;
import java.util.Random;

/**
 * MULTI-HEADED SENSORY ATTENTION (Section VII — "Gemma" Architecture)
 *
 * Implements Rotary Position Embedding (RoPE) attention over a sensory sequence
 * X ∈ R^{L×d}. Projects X → Q, K, V then applies rotary matrices R_{Θ,m} and
 * computes scaled dot-product attention:
 *
 *   Q = X·W_Q,  K = X·W_K,  V = X·W_V
 *   q̃_m = R_{Θ,m}·q_m,  k̃_n = R_{Θ,n}·k_n
 *   Attention(Q̃,K̃,V) = softmax( Q̃K̃ᵀ / √d_k ) · V
 *
 * Multi-head variant: H independent heads of dimension d_k = d / H.
 * Single-layer implementation; extendable to stacked depth.
 */
public class RoPEAttention {

    public static final int DEFAULT_DIM   = 64;
    public static final int DEFAULT_HEADS = 8;
    public static final int DEFAULT_SEQ   = 32;

    private final int d;
    private final int numHeads;
    private final int dk;

    private final double[][] wQ;
    private final double[][] wK;
    private final double[][] wV;
    private final double[][] wO;

    private long forwardCount;
    private boolean ready;

    private final Random rng;

    public RoPEAttention(int d, int numHeads) {
        if (d % numHeads != 0)
            throw new IllegalArgumentException("d must be divisible by numHeads");
        this.d        = d;
        this.numHeads = numHeads;
        this.dk       = d / numHeads;
        this.rng      = new Random(42L);

        this.wQ = initWeight(d, d);
        this.wK = initWeight(d, d);
        this.wV = initWeight(d, d);
        this.wO = initWeight(d, d);
        this.ready = true;
    }

    public RoPEAttention() {
        this(DEFAULT_DIM, DEFAULT_HEADS);
    }

    // ─── WEIGHT INIT ─────────────────────────────────────────────────────────

    private double[][] initWeight(int rows, int cols) {
        double scale = Math.sqrt(2.0 / cols);
        double[][] w = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                w[i][j] = rng.nextGaussian() * scale;
        return w;
    }

    // ─── ROPE ROTATION ───────────────────────────────────────────────────────

    /**
     * Apply R_{Θ,pos} to a d_k-dimensional vector via paired rotation.
     * θ_i = pos / 10000^{2i/d_k}
     * [x_{2i}, x_{2i+1}] → [x_{2i}·cos(θ) − x_{2i+1}·sin(θ),
     *                         x_{2i}·sin(θ) + x_{2i+1}·cos(θ)]
     */
    private double[] applyRoPE(double[] vec, int pos) {
        double[] out = Arrays.copyOf(vec, vec.length);
        int len = vec.length;
        for (int i = 0; i < len - 1; i += 2) {
            double theta = pos / Math.pow(10000.0, (2.0 * i) / len);
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            out[i]     = vec[i] * cos - vec[i + 1] * sin;
            out[i + 1] = vec[i] * sin + vec[i + 1] * cos;
        }
        return out;
    }

    // ─── LINEAR PROJECTION ───────────────────────────────────────────────────

    private double[] project(double[] x, double[][] w, int outStart, int outLen) {
        double[] out = new double[outLen];
        for (int o = 0; o < outLen; o++) {
            int row = outStart + o;
            double sum = 0.0;
            for (int j = 0; j < x.length; j++) sum += w[row][j] * x[j];
            out[o] = sum;
        }
        return out;
    }

    private double[][] projectAll(double[][] X, double[][] W) {
        int L = X.length;
        double[][] out = new double[L][d];
        for (int t = 0; t < L; t++) {
            for (int o = 0; o < d; o++) {
                double sum = 0.0;
                for (int j = 0; j < X[t].length; j++) sum += W[o][j] * X[t][j];
                out[t][o] = sum;
            }
        }
        return out;
    }

    // ─── SOFTMAX ─────────────────────────────────────────────────────────────

    private double[] softmax(double[] z) {
        double max = Arrays.stream(z).max().getAsDouble();
        double sum = 0.0;
        double[] out = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            out[i] = Math.exp(z[i] - max);
            sum += out[i];
        }
        for (int i = 0; i < z.length; i++) out[i] /= sum;
        return out;
    }

    // ─── FORWARD PASS ────────────────────────────────────────────────────────

    /**
     * Full multi-head RoPE attention forward pass.
     * @param X  input sequence [L × d]
     * @return   attended output [L × d]
     */
    public double[][] forward(double[][] X) {
        int L = X.length;
        double[][] Q = projectAll(X, wQ);
        double[][] K = projectAll(X, wK);
        double[][] V = projectAll(X, wV);

        double[][] output = new double[L][d];

        for (int h = 0; h < numHeads; h++) {
            int hStart = h * dk;

            double[][] Qh = new double[L][dk];
            double[][] Kh = new double[L][dk];
            double[][] Vh = new double[L][dk];

            for (int t = 0; t < L; t++) {
                double[] qSlice = Arrays.copyOfRange(Q[t], hStart, hStart + dk);
                double[] kSlice = Arrays.copyOfRange(K[t], hStart, hStart + dk);
                Qh[t] = applyRoPE(qSlice, t);
                Kh[t] = applyRoPE(kSlice, t);
                Vh[t] = Arrays.copyOfRange(V[t], hStart, hStart + dk);
            }

            double scale = 1.0 / Math.sqrt(dk);
            double[][] attn = new double[L][L];
            for (int q = 0; q < L; q++) {
                for (int k = 0; k < L; k++) {
                    double dot = 0.0;
                    for (int i = 0; i < dk; i++) dot += Qh[q][i] * Kh[k][i];
                    attn[q][k] = dot * scale;
                }
                attn[q] = softmax(attn[q]);
            }

            for (int q = 0; q < L; q++) {
                for (int v = 0; v < dk; v++) {
                    double sum = 0.0;
                    for (int k = 0; k < L; k++) sum += attn[q][k] * Vh[k][v];
                    output[q][hStart + v] += sum;
                }
            }
        }

        double[][] finalOut = new double[L][d];
        for (int t = 0; t < L; t++) {
            for (int o = 0; o < d; o++) {
                double sum = 0.0;
                for (int j = 0; j < d; j++) sum += wO[o][j] * output[t][j];
                finalOut[t][o] = sum;
            }
        }

        forwardCount++;
        return finalOut;
    }

    /**
     * Convenience: attend over a flat 1-D sensory vector by reshaping into L tokens of dk each.
     * Pads or truncates to fit L = d (one token per dimension).
     */
    public double[] attendVector(double[] sensoryVec) {
        int L = Math.min(DEFAULT_SEQ, sensoryVec.length / dk + 1);
        double[][] X = new double[L][d];
        for (int t = 0; t < L; t++) {
            for (int i = 0; i < d && (t * d + i) < sensoryVec.length; i++) {
                X[t][i] = sensoryVec[t * d + i];
            }
        }
        double[][] out = forward(X);
        double[] flat = new double[d];
        for (int i = 0; i < d; i++) flat[i] = out[0][i];
        return flat;
    }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public boolean isReady()        { return ready; }
    public long    getForwardCount(){ return forwardCount; }
    public int     getDim()         { return d; }
    public int     getNumHeads()    { return numHeads; }
    public int     getDk()          { return dk; }

    public String getStatus() {
        return String.format(
            "RoPEAttention | d=%d H=%d dk=%d | forwards=%d | ready=%s",
            d, numHeads, dk, forwardCount, ready ? "YES" : "NO"
        );
    }
}
