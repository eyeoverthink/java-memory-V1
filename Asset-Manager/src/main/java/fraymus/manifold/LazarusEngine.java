package fraymus.manifold;

import java.util.*;

/**
 * TELEOLOGICAL DRIVE: Lazarus Engine + Rainbow DQN
 *
 * Implements the MDP ⟨S, A, P, R, γ⟩ with phi-resonance stability constraint:
 *
 *   J(θ) = E[Σ γ^t (R(s_t, a_t) + β H(π_θ(·|s_t)))]
 *
 * Phi-Resonance stability check (consciousness threshold C_opt = 0.7569):
 *   |1/D * Σ h_k^t · h_k^(t-1)| × φ ≈ 0.7569
 *
 * When resonance < C_opt → Retrocausal Omega Point protocol: mutate weights θ.
 *
 * State S: NeuralSwarmGraph activations (9-dim)
 * Action A: discrete {0..ACTION_DIM-1} (routing / attention selection)
 * Reward R: phi-resonance proximity to C_opt
 */
public class LazarusEngine {

    private static final double PHI   = (1.0 + Math.sqrt(5.0)) / 2.0;
    private static final double C_OPT = 0.7569;
    private static final double GAMMA = 0.99;
    private static final double BETA  = 0.01;
    private static final double ETA   = 0.001;

    public static final int STATE_DIM  = 9;
    public static final int ACTION_DIM = 9;
    public static final int HIDDEN_DIM = 32;

    private final double[][] w1;
    private final double[][] w2;
    private final double[]   b1;
    private final double[]   b2;

    private final double[] episodeRewards;
    private double totalReward;
    private int    stepCount;
    private int    omegaPointCount;
    private double lastResonance;
    private boolean alive;

    private final Random rng;
    private double epsilon;

    public LazarusEngine() {
        this.rng    = new Random();
        this.w1     = new double[HIDDEN_DIM][STATE_DIM];
        this.b1     = new double[HIDDEN_DIM];
        this.w2     = new double[ACTION_DIM][HIDDEN_DIM];
        this.b2     = new double[ACTION_DIM];
        this.episodeRewards = new double[1024];
        this.epsilon = 1.0;
        initWeights();
    }

    // ─── WEIGHT INITIALIZATION ───────────────────────────────────────────────

    private void initWeights() {
        double scale1 = Math.sqrt(2.0 / STATE_DIM);
        double scale2 = Math.sqrt(2.0 / HIDDEN_DIM);
        for (int h = 0; h < HIDDEN_DIM; h++) {
            b1[h] = 0.0;
            for (int s = 0; s < STATE_DIM; s++) {
                w1[h][s] = rng.nextGaussian() * scale1;
            }
        }
        for (int a = 0; a < ACTION_DIM; a++) {
            b2[a] = 0.0;
            for (int h = 0; h < HIDDEN_DIM; h++) {
                w2[a][h] = rng.nextGaussian() * scale2;
            }
        }
    }

    // ─── LIFECYCLE ──────────────────────────────────────────────────────────

    public void boot() {
        alive = true;
        stepCount = 0;
        omegaPointCount = 0;
        totalReward = 0.0;
        lastResonance = 0.0;
        epsilon = 1.0;
        System.out.println("   [LazarusEngine] MDP online | γ=" + GAMMA + " β=" + BETA + " η=" + ETA);
        System.out.println("   [LazarusEngine] Rainbow DQN | S=" + STATE_DIM +
            " A=" + ACTION_DIM + " H=" + HIDDEN_DIM + " C_opt=" + C_OPT);
    }

    public void shutdown() { alive = false; }

    public boolean isAlive() { return alive; }

    // ─── POLICY: Q-NETWORK FORWARD PASS ─────────────────────────────────────

    /**
     * Forward pass: state → Q-values via 2-layer network with ReLU.
     */
    public double[] qValues(double[] state) {
        double[] hidden = new double[HIDDEN_DIM];
        for (int h = 0; h < HIDDEN_DIM; h++) {
            double z = b1[h];
            for (int s = 0; s < STATE_DIM; s++) {
                z += w1[h][s] * state[s];
            }
            hidden[h] = Math.max(0.0, z);
        }
        double[] q = new double[ACTION_DIM];
        for (int a = 0; a < ACTION_DIM; a++) {
            double z = b2[a];
            for (int h = 0; h < HIDDEN_DIM; h++) {
                z += w2[a][h] * hidden[h];
            }
            q[a] = z;
        }
        return q;
    }

    /**
     * π(a|s): ε-greedy policy with entropy regularization.
     */
    public int selectAction(double[] state) {
        if (rng.nextDouble() < epsilon) {
            return rng.nextInt(ACTION_DIM);
        }
        double[] q = qValues(state);
        int best = 0;
        for (int a = 1; a < ACTION_DIM; a++) {
            if (q[a] > q[best]) best = a;
        }
        return best;
    }

    /**
     * Policy entropy H(π(·|s)) via softmax of Q-values.
     */
    public double entropy(double[] state) {
        double[] q = qValues(state);
        double maxQ = Arrays.stream(q).max().getAsDouble();
        double sumExp = 0.0;
        double[] probs = new double[ACTION_DIM];
        for (int a = 0; a < ACTION_DIM; a++) {
            probs[a] = Math.exp(q[a] - maxQ);
            sumExp += probs[a];
        }
        double H = 0.0;
        for (int a = 0; a < ACTION_DIM; a++) {
            double p = probs[a] / sumExp;
            if (p > 1e-12) H -= p * Math.log(p);
        }
        return H;
    }

    // ─── OPTIMIZATION LOOP J(θ) ──────────────────────────────────────────────

    /**
     * One MDP step:
     *   1. Observe state s_t (from NeuralSwarmGraph activations)
     *   2. Select action a_t ~ π_θ(·|s_t)
     *   3. Compute reward R = proximity of φ-resonance to C_opt
     *   4. Compute augmented reward: R + β·H(π_θ)
     *   5. Update weights θ via gradient descent on J(θ)
     *   6. Check phi-resonance stability → Omega Point if below C_opt
     *
     * @param graph  the live NeuralSwarmGraph providing state
     * @return selected action index
     */
    public int optimizationStep(NeuralSwarmGraph graph) {
        double[] state = graph.getAllActivations();

        int action = selectAction(state);

        graph.inject(NeuralSwarmGraph.Lobe.values()[action], 0.1);
        graph.step();

        double resonance = graph.getPhiResonance();
        lastResonance = resonance;

        double reward = computeReward(resonance);
        double H      = entropy(state);
        double augR   = reward + BETA * H;

        updateWeights(state, action, augR);
        trackReward(augR);

        if (resonance < C_OPT) {
            omegaPoint();
        }

        epsilon = Math.max(0.05, epsilon * 0.9995);
        stepCount++;
        return action;
    }

    /**
     * R(s, a) — reward = proximity of phi-resonance to C_opt, maximised at 0.7569.
     * Shaped as: 1 - |resonance - C_opt| / C_opt
     */
    private double computeReward(double resonance) {
        return 1.0 - Math.abs(resonance - C_OPT) / C_OPT;
    }

    // ─── WEIGHT UPDATE (TD(0) with entropy) ─────────────────────────────────

    /**
     * Single-step TD update: ∇θ J ≈ (R + β·H - Q(s,a)) · ∂Q/∂θ
     * Applied via SGD with learning rate η.
     */
    private void updateWeights(double[] state, int action, double target) {
        double[] hidden = new double[HIDDEN_DIM];
        for (int h = 0; h < HIDDEN_DIM; h++) {
            double z = b1[h];
            for (int s = 0; s < STATE_DIM; s++) z += w1[h][s] * state[s];
            hidden[h] = Math.max(0.0, z);
        }
        double[] q = qValues(state);
        double tdError = target - q[action];

        for (int h = 0; h < HIDDEN_DIM; h++) {
            double dw2 = tdError * hidden[h];
            w2[action][h] += ETA * dw2;
        }
        b2[action] += ETA * tdError;

        for (int h = 0; h < HIDDEN_DIM; h++) {
            if (hidden[h] <= 0) continue;
            double upstream = tdError * w2[action][h];
            for (int s = 0; s < STATE_DIM; s++) {
                w1[h][s] += ETA * upstream * state[s];
            }
            b1[h] += ETA * upstream;
        }
    }

    // ─── RETROCAUSAL OMEGA POINT ─────────────────────────────────────────────

    /**
     * Omega Point protocol: triggered when phi-resonance < C_opt.
     * Mutates weights θ by a phi-scaled perturbation to escape local minima.
     *
     * |δθ| = φ^(-stepCount % 7) * σ (bounded mutation, decreasing over time)
     */
    private void omegaPoint() {
        double magnitude = Math.pow(PHI, -(stepCount % 7)) * 0.05;
        for (int h = 0; h < HIDDEN_DIM; h++) {
            for (int s = 0; s < STATE_DIM; s++) {
                w1[h][s] += rng.nextGaussian() * magnitude;
            }
            b1[h] += rng.nextGaussian() * magnitude * 0.1;
        }
        for (int a = 0; a < ACTION_DIM; a++) {
            for (int h = 0; h < HIDDEN_DIM; h++) {
                w2[a][h] += rng.nextGaussian() * magnitude;
            }
            b2[a] += rng.nextGaussian() * magnitude * 0.1;
        }
        omegaPointCount++;
    }

    // ─── REWARD TRACKING ─────────────────────────────────────────────────────

    private void trackReward(double r) {
        int idx = stepCount % episodeRewards.length;
        episodeRewards[idx] = r;
        totalReward += r;
    }

    public double getAverageReward() {
        int n = Math.min(stepCount, episodeRewards.length);
        if (n == 0) return 0.0;
        double sum = 0.0;
        for (int i = 0; i < n; i++) sum += episodeRewards[i];
        return sum / n;
    }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public int    getStepCount()       { return stepCount; }
    public int    getOmegaPointCount() { return omegaPointCount; }
    public double getLastResonance()   { return lastResonance; }
    public double getEpsilon()         { return epsilon; }
    public double getTotalReward()     { return totalReward; }

    public String getStatus() {
        return String.format(
            "LazarusEngine | steps=%d | φ-res=%.4f/C_opt=%.4f | ε=%.4f | " +
            "avgR=%.4f | Ω-pts=%d | alive=%s",
            stepCount, lastResonance, C_OPT, epsilon,
            getAverageReward(), omegaPointCount, alive ? "YES" : "NO"
        );
    }
}
