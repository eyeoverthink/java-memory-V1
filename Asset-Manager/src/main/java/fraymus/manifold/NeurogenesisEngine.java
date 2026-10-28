package fraymus.manifold;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CELLULAR DIVISION & NEUROGENESIS (Section IX — "Willow" Engine)
 *
 * A static matrix cannot evolve. This engine monitors gradient variance per neuron
 * and triggers mitosis when a neuron is overwhelmed by complexity.
 *
 * Gradient variance per neuron i over window τ:
 *   V_i = E[(∇_{w_i} L)²] − (E[∇_{w_i} L])²
 *
 * Cellular Division Trigger: if V_i > θ_split, split node v_i into v_{i,1} and v_{i,2}:
 *   W_{i,1} = W_i · (1 + ε),  W_{i,2} = W_i · (1 − ε),  ε ~ N(0, σ²)
 *
 * Matrix dimension D permanently increases by 1 per split.
 * Synaptic Pruning: if |w_j| < θ_prune → w_j = 0 (Free Energy complexity minimization).
 */
public class NeurogenesisEngine {

    public static final double DEFAULT_THETA_SPLIT = 0.15;
    public static final double DEFAULT_THETA_PRUNE = 0.005;
    public static final double DEFAULT_SIGMA       = 0.01;
    public static final int    DEFAULT_WINDOW      = 64;

    private final double thetaSplit;
    private final double thetaPrune;
    private final double sigma;
    private final int    window;

    private int    dimension;
    private double[] weights;

    private final List<double[]> gradientHistory;

    private int    splitCount;
    private int    pruneCount;
    private int    stepCount;

    private final Random rng;

    public NeurogenesisEngine(int initialDimension, double thetaSplit, double thetaPrune, double sigma, int window) {
        this.dimension   = initialDimension;
        this.thetaSplit  = thetaSplit;
        this.thetaPrune  = thetaPrune;
        this.sigma       = sigma;
        this.window      = window;
        this.rng         = new Random(13L);
        this.weights     = initWeights(initialDimension);
        this.gradientHistory = new ArrayList<>();
    }

    public NeurogenesisEngine(int initialDimension) {
        this(initialDimension, DEFAULT_THETA_SPLIT, DEFAULT_THETA_PRUNE, DEFAULT_SIGMA, DEFAULT_WINDOW);
    }

    public NeurogenesisEngine() {
        this(LazarusEngine.HIDDEN_DIM);
    }

    private double[] initWeights(int dim) {
        double scale = Math.sqrt(2.0 / dim);
        double[] w = new double[dim];
        for (int i = 0; i < dim; i++) w[i] = rng.nextGaussian() * scale;
        return w;
    }

    // ─── GRADIENT TRACKING ──────────────────────────────────────────────────

    /**
     * Record one gradient observation for the current timestep.
     * Maintains a rolling window of size τ.
     * @param grad  gradient vector of length == dimension
     */
    public void recordGradient(double[] grad) {
        if (grad.length != dimension) return;
        double[] copy = grad.clone();
        gradientHistory.add(copy);
        if (gradientHistory.size() > window) gradientHistory.remove(0);
        stepCount++;
    }

    // ─── VARIANCE COMPUTATION ────────────────────────────────────────────────

    /**
     * V_i = E[(∇_{w_i})²] − (E[∇_{w_i}])²
     */
    private double[] computeVariance() {
        int n = gradientHistory.size();
        if (n < 2) return new double[dimension];

        double[] mean   = new double[dimension];
        double[] mean2  = new double[dimension];

        for (double[] g : gradientHistory) {
            for (int i = 0; i < dimension; i++) {
                mean[i]  += g[i];
                mean2[i] += g[i] * g[i];
            }
        }
        double[] variance = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            mean[i]  /= n;
            mean2[i] /= n;
            variance[i] = mean2[i] - mean[i] * mean[i];
        }
        return variance;
    }

    // ─── CELLULAR DIVISION ──────────────────────────────────────────────────

    /**
     * Check each neuron's variance; if V_i > θ_split, perform mitosis.
     * Returns number of splits performed this cycle.
     */
    public int checkAndSplit() {
        double[] variance = computeVariance();
        int splits = 0;

        for (int i = 0; i < dimension; i++) {
            if (variance[i] > thetaSplit) {
                splitNeuron(i);
                splits++;
                splitCount++;
                i++;
            }
        }
        return splits;
    }

    /**
     * Neuron mitosis: w_i → [w_i·(1+ε), w_i·(1−ε)].
     * Dimension D permanently increases by 1.
     */
    private void splitNeuron(int idx) {
        double eps = rng.nextGaussian() * sigma;
        double wi  = weights[idx];

        double[] newWeights = new double[dimension + 1];
        System.arraycopy(weights, 0, newWeights, 0, idx);
        newWeights[idx]     = wi * (1.0 + eps);
        newWeights[idx + 1] = wi * (1.0 - eps);
        System.arraycopy(weights, idx + 1, newWeights, idx + 2, dimension - idx - 1);

        weights   = newWeights;
        dimension = dimension + 1;

        for (double[] g : gradientHistory) {
            double[] ng = new double[dimension];
            System.arraycopy(g, 0, ng, 0, idx);
            ng[idx]     = g[idx < g.length ? idx : g.length - 1];
            ng[idx + 1] = g[idx < g.length ? idx : g.length - 1];
            if (idx + 1 < g.length)
                System.arraycopy(g, idx + 1, ng, idx + 2, g.length - idx - 1);
        }
    }

    // ─── SYNAPTIC PRUNING (FREE ENERGY COMPLEXITY TERM) ─────────────────────

    /**
     * Set weights below θ_prune to zero.
     * Minimizes the Complexity term of variational free energy.
     * @return number of weights pruned
     */
    public int prune() {
        int pruned = 0;
        for (int i = 0; i < dimension; i++) {
            if (Math.abs(weights[i]) < thetaPrune) {
                weights[i] = 0.0;
                pruned++;
                pruneCount++;
            }
        }
        return pruned;
    }

    /**
     * Combined plasticity step: record gradient → check splits → prune.
     * @param grad current loss gradient
     * @return summary string
     */
    public String plasticityStep(double[] grad) {
        recordGradient(grad);
        int splits = checkAndSplit();
        int pruned = prune();
        return String.format("plasticityStep | splits=%d pruned=%d D=%d", splits, pruned, dimension);
    }

    // ─── WEIGHT ACCESS ───────────────────────────────────────────────────────

    public double[] getWeights()     { return weights.clone(); }
    public int      getDimension()   { return dimension; }
    public int      getSplitCount()  { return splitCount; }
    public int      getPruneCount()  { return pruneCount; }
    public int      getStepCount()   { return stepCount; }

    public double getVarianceAt(int idx) {
        double[] v = computeVariance();
        if (idx < 0 || idx >= v.length) return 0.0;
        return v[idx];
    }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        double[] var = computeVariance();
        double maxVar = 0.0;
        for (double v : var) if (v > maxVar) maxVar = v;
        return String.format(
            "NeurogenesisEngine | D=%d θ_split=%.4f θ_prune=%.4f | splits=%d pruned=%d steps=%d maxVar=%.4f",
            dimension, thetaSplit, thetaPrune, splitCount, pruneCount, stepCount, maxVar
        );
    }
}
