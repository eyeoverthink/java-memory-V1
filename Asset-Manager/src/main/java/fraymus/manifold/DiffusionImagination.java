package fraymus.manifold;

import java.util.Random;

/**
 * DENOISING DIFFUSION IMAGINATION (Section VIII — "LTX-Video" Engine)
 *
 * Implements stochastic differential equation (SDE) based generation.
 * Generation = reversing the flow of entropy.
 *
 * Forward Process (Destroying Information / Sleep):
 *   dx = f(x,t)dt + g(t)dw          [Ornstein-Uhlenbeck: f(x,t) = -αx]
 *
 * Reverse Process (Dreaming / Generation):
 *   dx = [f(x,t) - g(t)² · s_θ(x,t)]dt + g(t)dw̄
 *
 * Score function s_θ(x,t) is approximated by a lightweight MLP that learns
 * the gradient of the log-probability: ∇_x log p_t(x).
 *
 * Discrete Euler-Maruyama integration used for both directions.
 */
public class DiffusionImagination {

    public static final int DEFAULT_DIM   = 64;
    public static final int DEFAULT_STEPS = 50;

    private final int    dim;
    private final int    numSteps;
    private final double alpha;
    private final double dt;

    private final double[][] scoreW1;
    private final double[]   scoreB1;
    private final double[][] scoreW2;
    private final double[]   scoreB2;
    private final int        hiddenDim;

    private long dreamCount;
    private long sleepCount;
    private boolean awake;

    private final Random rng;

    public DiffusionImagination(int dim, int numSteps) {
        this.dim      = dim;
        this.numSteps = numSteps;
        this.alpha    = 0.5;
        this.dt       = 1.0 / numSteps;
        this.hiddenDim = dim * 2;
        this.rng      = new Random(7L);

        double scale1 = Math.sqrt(2.0 / (dim + 1));
        double scale2 = Math.sqrt(2.0 / hiddenDim);
        this.scoreW1 = initWeight(hiddenDim, dim + 1, scale1);
        this.scoreB1 = new double[hiddenDim];
        this.scoreW2 = initWeight(dim, hiddenDim, scale2);
        this.scoreB2 = new double[dim];
    }

    public DiffusionImagination() {
        this(DEFAULT_DIM, DEFAULT_STEPS);
    }

    private double[][] initWeight(int rows, int cols, double scale) {
        double[][] w = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                w[i][j] = rng.nextGaussian() * scale;
        return w;
    }

    // ─── DIFFUSION SCHEDULE ──────────────────────────────────────────────────

    /** f(x,t) = -alpha * x  (mean-reverting drift toward zero) */
    private double[] drift(double[] x) {
        double[] f = new double[dim];
        for (int i = 0; i < dim; i++) f[i] = -alpha * x[i];
        return f;
    }

    /** g(t) — diffusion coefficient: sigma schedule g(t) = sqrt(2*alpha) so variance stays bounded */
    private double diffusionCoeff(double t) {
        return Math.sqrt(2.0 * alpha * (1.0 - Math.exp(-2.0 * alpha * t)));
    }

    // ─── SCORE NETWORK s_θ(x, t) ─────────────────────────────────────────────

    /**
     * Two-layer MLP: input = [x | t] (dim+1), hidden = 2*dim, output = dim.
     * Approximates ∇_x log p_t(x).
     */
    private double[] scoreNetwork(double[] x, double t) {
        double[] input = new double[dim + 1];
        System.arraycopy(x, 0, input, 0, dim);
        input[dim] = t;

        double[] h = new double[hiddenDim];
        for (int j = 0; j < hiddenDim; j++) {
            double z = scoreB1[j];
            for (int i = 0; i < dim + 1; i++) z += scoreW1[j][i] * input[i];
            h[j] = Math.max(0.0, z);
        }

        double[] score = new double[dim];
        for (int j = 0; j < dim; j++) {
            double z = scoreB2[j];
            for (int i = 0; i < hiddenDim; i++) z += scoreW2[j][i] * h[i];
            score[j] = z;
        }
        return score;
    }

    // ─── FORWARD PROCESS (SLEEP / NOISE INJECTION) ───────────────────────────

    /**
     * Forward SDE: dx = f(x,t)dt + g(t)dw
     * Injects Gaussian noise over numSteps, destroying structure.
     * @param x0  pure signal in R^dim
     * @return    noisy state at t=T
     */
    public double[] forwardProcess(double[] x0) {
        assert x0.length == dim;
        double[] x = x0.clone();
        for (int step = 0; step < numSteps; step++) {
            double t = step * dt;
            double g = diffusionCoeff(t);
            double[] f = drift(x);
            double sqrtDt = Math.sqrt(dt);
            for (int i = 0; i < dim; i++) {
                double dw = rng.nextGaussian() * sqrtDt;
                x[i] += f[i] * dt + g * dw;
            }
        }
        sleepCount++;
        return x;
    }

    /**
     * Partial forward: noise to a specific timestep fraction in [0,1].
     */
    public double[] noiseToLevel(double[] x0, double tFrac) {
        assert x0.length == dim;
        int steps = (int) (tFrac * numSteps);
        double[] x = x0.clone();
        for (int step = 0; step < steps; step++) {
            double t = step * dt;
            double g = diffusionCoeff(t);
            double[] f = drift(x);
            double sqrtDt = Math.sqrt(dt);
            for (int i = 0; i < dim; i++) {
                x[i] += f[i] * dt + g * rng.nextGaussian() * sqrtDt;
            }
        }
        return x;
    }

    // ─── REVERSE PROCESS (DREAM / GENERATION) ────────────────────────────────

    /**
     * Reverse SDE (Euler-Maruyama, time-reversed):
     *   dx = [f(x,t) - g(t)² · s_θ(x,t)]dt + g(t)dw̄
     * Starts from pure noise and denoises toward a coherent signal.
     * @return  generated signal in R^dim
     */
    public double[] dream() {
        double[] x = new double[dim];
        for (int i = 0; i < dim; i++) x[i] = rng.nextGaussian();

        for (int step = numSteps - 1; step >= 0; step--) {
            double t = step * dt;
            double g = diffusionCoeff(t);
            double g2 = g * g;
            double[] f    = drift(x);
            double[] score = scoreNetwork(x, t);
            double sqrtDt = Math.sqrt(dt);

            for (int i = 0; i < dim; i++) {
                double dw = rng.nextGaussian() * sqrtDt;
                x[i] += (f[i] - g2 * score[i]) * dt + g * dw;
            }
        }
        dreamCount++;
        return x;
    }

    /**
     * Conditional dream: start from a partially-noised real signal x0.
     * Combines the forward diffusion with reverse recovery.
     */
    public double[] conditionalDream(double[] x0, double noiseFrac) {
        double[] noisy = noiseToLevel(x0, noiseFrac);
        int startStep = (int) ((1.0 - noiseFrac) * numSteps);
        double[] x = noisy;

        for (int step = startStep; step >= 0; step--) {
            double t = step * dt;
            double g = diffusionCoeff(t);
            double g2 = g * g;
            double[] f     = drift(x);
            double[] score = scoreNetwork(x, t);
            double sqrtDt  = Math.sqrt(dt);

            for (int i = 0; i < dim; i++) {
                x[i] += (f[i] - g2 * score[i]) * dt + g * rng.nextGaussian() * sqrtDt;
            }
        }
        dreamCount++;
        return x;
    }

    // ─── SCORE LEARNING (SINGLE STEP) ────────────────────────────────────────

    /**
     * One SGD step of denoising score matching:
     *   L = ||s_θ(x_t, t) + (x_t - x_0)/σ_t²||²
     * @param x0  original clean signal
     * @param eta learning rate
     */
    public void learnStep(double[] x0, double eta) {
        double t = rng.nextDouble();
        double sigma = diffusionCoeff(t);
        if (sigma < 1e-8) return;

        double[] noise = new double[dim];
        double[] xt    = new double[dim];
        for (int i = 0; i < dim; i++) {
            noise[i] = rng.nextGaussian();
            xt[i]    = x0[i] + sigma * noise[i];
        }

        double[] score   = scoreNetwork(xt, t);
        double[] target  = new double[dim];
        double sigma2inv = 1.0 / (sigma * sigma);
        for (int i = 0; i < dim; i++) target[i] = -noise[i] / sigma;

        double[] err = new double[dim];
        for (int i = 0; i < dim; i++) err[i] = score[i] - target[i];

        double[] h = new double[hiddenDim];
        for (int j = 0; j < hiddenDim; j++) {
            double z = scoreB1[j];
            double[] input = new double[dim + 1];
            System.arraycopy(xt, 0, input, 0, dim);
            input[dim] = t;
            for (int i = 0; i < dim + 1; i++) z += scoreW1[j][i] * input[i];
            h[j] = Math.max(0.0, z);
        }

        for (int j = 0; j < dim; j++) {
            for (int i = 0; i < hiddenDim; i++) {
                scoreW2[j][i] -= eta * err[j] * h[i];
            }
            scoreB2[j] -= eta * err[j];
        }
    }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public void   setAwake(boolean v) { awake = v; }
    public boolean isAwake()          { return awake; }
    public long   getDreamCount()     { return dreamCount; }
    public long   getSleepCount()     { return sleepCount; }
    public int    getDim()            { return dim; }
    public int    getNumSteps()       { return numSteps; }

    public String getStatus() {
        return String.format(
            "DiffusionImagination | dim=%d steps=%d α=%.2f | dreams=%d sleeps=%d | awake=%s",
            dim, numSteps, alpha, dreamCount, sleepCount, awake ? "YES" : "NO"
        );
    }
}
