package fraymus.manifold;

import java.util.Random;

/**
 * FREE ENERGY PRINCIPLE (Section X — "Lazarus" Engine)
 *
 * Implements Karl Friston's Variational Free Energy Principle for autonomous drive.
 * The system acts to minimise "Surprise" (entropy) about its environment.
 *
 * Variational Free Energy (Metabolic Drive):
 *   F = E_{q(s)}[log q(s) − log p(o,s)]
 *     = Complexity − Accuracy
 *
 * 1. Hunger (Active Inference):
 *    a* = argmin_a F(o(a), s)
 *    System takes action to change environment to match its predictions (foraging).
 *
 * 2. Excretion (Synaptic Pruning):
 *    Minimise Complexity term of F by deleting redundant weights:
 *    if |w_j| < θ_prune → w_j = 0
 *
 * Internal model: Gaussian generative model p(o,s) = p(o|s)p(s)
 *   p(s)   = N(μ_s, σ_s²)    (prior belief)
 *   p(o|s) = N(s, σ_o²)      (likelihood / prediction)
 *   q(s)   = N(μ_q, σ_q²)    (approximate posterior — the "self-model")
 */
public class FreeEnergyEngine {

    private static final double DEFAULT_SIGMA_O  = 0.5;
    private static final double DEFAULT_SIGMA_S  = 1.0;
    private static final double DEFAULT_SIGMA_Q  = 0.5;
    private static final double DEFAULT_THETA_PRUNE = 0.01;
    private static final double ETA = 0.01;

    private final int    dim;
    private final double sigmaO;
    private final double sigmaS;

    private double[] muQ;
    private double[] sigmaQ;
    private double[] muS;

    private double[] internalWeights;
    private final double thetaPrune;

    private double freeEnergy;
    private double accuracy;
    private double complexity;
    private long   inferenceSteps;
    private long   foragingActions;
    private long   pruneEvents;
    private boolean hungry;

    private final Random rng;

    public FreeEnergyEngine(int dim) {
        this.dim        = dim;
        this.sigmaO     = DEFAULT_SIGMA_O;
        this.sigmaS     = DEFAULT_SIGMA_S;
        this.thetaPrune = DEFAULT_THETA_PRUNE;
        this.rng        = new Random(17L);

        this.muS            = new double[dim];
        this.muQ            = new double[dim];
        this.sigmaQ         = new double[dim];
        this.internalWeights = new double[dim];

        double scale = Math.sqrt(2.0 / dim);
        for (int i = 0; i < dim; i++) {
            muS[i]            = rng.nextGaussian() * 0.1;
            muQ[i]            = rng.nextGaussian() * 0.1;
            sigmaQ[i]         = DEFAULT_SIGMA_Q;
            internalWeights[i] = rng.nextGaussian() * scale;
        }
    }

    public FreeEnergyEngine() {
        this(LazarusEngine.STATE_DIM);
    }

    // ─── FREE ENERGY COMPUTATION ─────────────────────────────────────────────

    /**
     * F = Complexity − Accuracy
     *
     * Complexity = KL(q(s) || p(s)) = Σ [log(σ_s/σ_q) + (σ_q² + (μ_q − μ_s)²)/(2σ_s²) − 0.5]
     * Accuracy   = E_{q(s)}[log p(o|s)] = −Σ [(o_i − μ_q_i)²/(2σ_o²) + 0.5·log(2π·σ_o²)]
     */
    public double computeFreeEnergy(double[] observation) {
        double kl  = 0.0;
        double acc = 0.0;
        double logNorm = 0.5 * Math.log(2.0 * Math.PI * sigmaO * sigmaO);

        for (int i = 0; i < dim; i++) {
            double sq  = sigmaQ[i];
            double sq2 = sq * sq;
            double ss2 = sigmaS * sigmaS;
            double diff = muQ[i] - muS[i];
            kl  += Math.log(sigmaS / sq) + (sq2 + diff * diff) / (2.0 * ss2) - 0.5;

            double oi   = i < observation.length ? observation[i] : 0.0;
            double pred = muQ[i];
            acc += -((oi - pred) * (oi - pred)) / (2.0 * sigmaO * sigmaO) - logNorm;
        }

        complexity  = kl;
        accuracy    = acc;
        freeEnergy  = complexity - accuracy;
        return freeEnergy;
    }

    // ─── ACTIVE INFERENCE (HUNGER / FORAGING) ────────────────────────────────

    /**
     * Gradient descent on F w.r.t. μ_q (perception update):
     *   Δμ_q = −η · ∂F/∂μ_q
     *   ∂F/∂μ_q = (μ_q − μ_s)/σ_s² + (μ_q − o)/σ_o²
     */
    public void perceptualInference(double[] observation) {
        for (int i = 0; i < dim; i++) {
            double oi   = i < observation.length ? observation[i] : 0.0;
            double dFdMu = (muQ[i] - muS[i]) / (sigmaS * sigmaS)
                         + (muQ[i] - oi)      / (sigmaO * sigmaO);
            muQ[i] -= ETA * dFdMu;
        }
        inferenceSteps++;
    }

    /**
     * Active inference — action selection to minimise F(o(a), s):
     * a* = argmin_a F  — returns action vector (gradient step in observation space).
     *
     * Here action = the gradient of F w.r.t. o, negated (move environment toward prediction):
     *   ∂F/∂o = −(μ_q − o)/σ_o²
     * a* = −∂F/∂o  →  action pushes o toward μ_q (foraging / attention redirection).
     *
     * @param currentObs current environmental observation
     * @return action vector in observation space
     */
    public double[] forage(double[] currentObs) {
        double[] action = new double[dim];
        computeFreeEnergy(currentObs);
        for (int i = 0; i < dim; i++) {
            double oi = i < currentObs.length ? currentObs[i] : 0.0;
            action[i] = (muQ[i] - oi) / (sigmaO * sigmaO);
        }
        foragingActions++;
        hungry = freeEnergy > 0.5;
        return action;
    }

    // ─── EXCRETION (SYNAPTIC PRUNING) ────────────────────────────────────────

    /**
     * Minimise Complexity by zeroing weights below θ_prune.
     * Equivalent to compressing the model — flushing metabolic waste.
     * @return number of weights excrete
     */
    public int excrete() {
        int pruned = 0;
        for (int i = 0; i < dim; i++) {
            if (Math.abs(internalWeights[i]) < thetaPrune) {
                internalWeights[i] = 0.0;
                pruned++;
                pruneEvents++;
            }
        }
        return pruned;
    }

    /**
     * Combined metabolic cycle: perceive → forage → excrete.
     * @param obs current sensory observation
     * @return action vector for motor output
     */
    public double[] metabolicCycle(double[] obs) {
        perceptualInference(obs);
        double[] action = forage(obs);
        excrete();
        return action;
    }

    // ─── PRIOR UPDATE (LEARNING) ─────────────────────────────────────────────

    /**
     * Update prior μ_s toward q posterior (Bayesian model update).
     */
    public void updatePrior(double learningRate) {
        for (int i = 0; i < dim; i++) {
            muS[i] += learningRate * (muQ[i] - muS[i]);
        }
    }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public double   getFreeEnergy()      { return freeEnergy; }
    public double   getAccuracy()        { return accuracy; }
    public double   getComplexity()      { return complexity; }
    public boolean  isHungry()           { return hungry; }
    public long     getInferenceSteps()  { return inferenceSteps; }
    public long     getForagingActions() { return foragingActions; }
    public long     getPruneEvents()     { return pruneEvents; }
    public double[] getMuQ()             { return muQ.clone(); }
    public double[] getMuS()             { return muS.clone(); }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        return String.format(
            "FreeEnergyEngine | dim=%d | F=%.4f Acc=%.4f Cplx=%.4f | hungry=%s | " +
            "infer=%d forage=%d prune=%d",
            dim, freeEnergy, accuracy, complexity,
            hungry ? "YES" : "NO",
            inferenceSteps, foragingActions, pruneEvents
        );
    }
}
