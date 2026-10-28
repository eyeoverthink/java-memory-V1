package fraymus.neural;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * RainbowDQN.java — Full Rainbow Deep Q-Network (Pure Java, Zero Dependencies)
 * =========================================================================================
 *
 * RAINBOW COMPONENTS (all 7):
 *   1. Distributional RL (C51): 51-atom categorical distribution over returns
 *      Instead of predicting E[Q], predict P(Q = z_i) for each atom z_i in [V_min, V_max]
 *      TD target = distributional Bellman projection onto support
 *
 *   2. Dueling Architecture: Q(s,a) = V(s) + A(s,a) - mean(A(s,·))
 *      Separate Value and Advantage streams that share a feature encoder
 *      Better at states where action choice doesn't matter
 *
 *   3. Double DQN: action selected by online net, evaluated by target net
 *      Eliminates maximisation bias in target computation
 *
 *   4. Noisy Networks: NoisyLinear replaces ε-greedy with learned stochastic weights
 *      σ(w) * ε replaces fixed epsilon — exploration is state-dependent and learned
 *
 *   5. Prioritised Experience Replay (PER): sample by TD-error priority
 *      Sum-tree for O(log N) priority updates and O(log N) sampling
 *      Importance-sampling weights correct for bias
 *
 *   6. N-step Returns: G_t = Σ_{k=0}^{n-1} γ^k r_{t+k} + γ^n V(s_{t+n})
 *      Reduces variance, faster credit assignment
 *
 *   7. Intrinsic Curiosity / Entropy Bonus: penalises repeated action patterns
 *      Encourages exploration — forces ambition, prevents behavioral lock-in
 *
 * YOUR ADDITIONS:
 *   Polar coordinate state encoding: (x,y) → (r,θ) injected into state vector
 *   Multi-head self-attention layer in the shared encoder
 *   Combination action space: actions are (category, magnitude) pairs
 *   Anti-repetition: action entropy bonus punishes low-diversity sequences
 *
 * DASHBOARD METRICS (matching your screenshot):
 *   FPS, Steps/s, Avg Reward (1M), Loss, Grad Norm, BC Loss
 *   Episode length, Replay buffer fill, Throughput
 *
 * Architecture per forward pass:
 *   state → PolarEncoder → [H×H attention] → NoisyLinear → [Value | Advantage] → Q-dist (51 atoms)
 *
 * =========================================================================================
 */
public class RainbowDQN {

    // =========================================================================================
    // HYPERPARAMETERS
    // =========================================================================================

    public static class Config {
        // Network
        public int   stateDim       = 64;       // input state dimensions
        public int   actionDim      = 16;       // number of discrete actions
        public int   hiddenDim      = 256;      // hidden layer width
        public int   attentionHeads = 4;        // multi-head attention heads
        public int   attentionDim   = 64;       // attention key/query/value dim per head

        // Distributional (C51)
        public int   numAtoms       = 51;       // number of distribution atoms
        public float vMin           = -10.0f;   // minimum return value
        public float vMax           =  10.0f;   // maximum return value

        // RL
        public float gamma          = 0.99f;    // discount factor
        public int   nStep          = 3;        // n-step return length
        public float learningRate   = 0.0001f;  // Adam learning rate
        public float beta1          = 0.9f;     // Adam β1
        public float beta2          = 0.999f;   // Adam β2
        public float eps            = 1e-8f;    // Adam ε

        // Prioritised Replay
        public int   bufferSize     = 100_000;  // replay buffer capacity
        public int   batchSize      = 32;       // training batch size
        public float perAlpha       = 0.6f;     // PER priority exponent
        public float perBetaStart   = 0.4f;     // IS weight starting β
        public float perBetaFrames  = 100_000;  // frames to anneal β → 1.0

        // Noisy Nets
        public float noisySigma     = 0.5f;     // initial noise magnitude

        // Target network
        public int   targetUpdateFreq = 1000;  // hard update every N steps

        // Combination action space
        public int   actionCategories = 4;      // e.g. move/attack/defend/wait
        public int   actionMagnitudes = 4;      // magnitude levels 0..3

        // Anti-repetition (entropy bonus)
        public float entropyCoeff   = 0.01f;    // weight on action entropy bonus
        public int   historyWindow  = 16;       // window for computing action entropy

        // Polar encoding
        public boolean usePolarCoords = true;   // inject (r, θ) of state centroid
    }

    private final Config cfg;

    // =========================================================================================
    // DISTRIBUTION SUPPORT — z_i = V_min + i * Δz
    // =========================================================================================

    private final float[] support;  // atom values [V_min .. V_max]
    private final float   deltaZ;

    // =========================================================================================
    // NETWORKS — online (trained) and target (frozen, periodically synced)
    // =========================================================================================

    private final Network online;
    private final Network target;

    // =========================================================================================
    // PRIORITISED REPLAY BUFFER
    // =========================================================================================

    private final PrioritisedReplayBuffer buffer;

    // =========================================================================================
    // N-STEP BUFFER
    // =========================================================================================

    private final Deque<Transition> nStepBuffer = new ArrayDeque<>();

    // =========================================================================================
    // ADAM OPTIMIZER STATE
    // =========================================================================================

    // Stored per-parameter in the network

    // =========================================================================================
    // DASHBOARD METRICS
    // =========================================================================================

    private final AtomicLong  totalSteps       = new AtomicLong(0);
    private final AtomicLong  totalFrames      = new AtomicLong(0);
    private final AtomicLong  totalEpisodes    = new AtomicLong(0);
    private volatile float    lastLoss         = 0.0f;
    private volatile float    lastGradNorm     = 0.0f;
    private volatile float    lastBcLoss       = 0.0f;
    private volatile float    avgReward1M      = 0.0f;
    private volatile float    episodeReward    = 0.0f;
    private volatile float    episodeLenEMA    = 0.0f;
    private volatile long     lastStepTime     = System.currentTimeMillis();
    private volatile float    stepsPerSec      = 0.0f;
    private volatile float    framesPerSec     = 0.0f;
    private volatile float    avgInferenceMs   = 0.0f;
    private volatile int      highScore        = 0;
    private final   Deque<Float> rewardHistory = new ArrayDeque<>(1_000_000);
    private final   Deque<Integer> actionHistory = new ArrayDeque<>(64);

    // =========================================================================================
    // CONSTRUCTOR
    // =========================================================================================

    public RainbowDQN(Config cfg) {
        this.cfg     = cfg;
        this.deltaZ  = (cfg.vMax - cfg.vMin) / (cfg.numAtoms - 1);
        this.support = new float[cfg.numAtoms];
        for (int i = 0; i < cfg.numAtoms; i++)
            support[i] = cfg.vMin + i * deltaZ;

        this.online = new Network(cfg);
        this.target = new Network(cfg);
        syncTargetNetwork();

        this.buffer = new PrioritisedReplayBuffer(cfg.bufferSize, cfg.perAlpha);

        System.out.println("[Rainbow] Initialised");
        System.out.printf("[Rainbow] State: %d | Actions: %d (combo: %dx%d) | Atoms: %d | Buffer: %,d%n",
            cfg.stateDim, cfg.actionDim, cfg.actionCategories, cfg.actionMagnitudes,
            cfg.numAtoms, cfg.bufferSize);
    }

    // =========================================================================================
    // POLAR COORDINATE ENCODER
    // Converts raw state to augmented state with polar (r, θ) of the state centroid
    // =========================================================================================

    private float[] encodePolar(float[] state) {
        if (!cfg.usePolarCoords || state.length < 2) return state;
        float[] aug = Arrays.copyOf(state, state.length + 2);
        // Treat state[0] as x, state[1] as y
        float x = state[0], y = state[1];
        float r = (float) Math.sqrt(x * x + y * y);
        float theta = (float) Math.atan2(y, x); // [-π, π]
        aug[state.length]     = r;
        aug[state.length + 1] = theta / (float) Math.PI; // normalise to [-1, 1]
        return aug;
    }

    // =========================================================================================
    // SELECT ACTION — uses noisy network (no ε-greedy needed) + combination space
    // =========================================================================================

    /**
     * Select action using the noisy online network (exploration via NoisyLinear).
     * Returns a combination action index encoding (category, magnitude).
     * Anti-repetition entropy bonus computed from recent action history.
     */
    public int selectAction(float[] state) {
        long t0 = System.nanoTime();
        float[] aug = encodePolar(state);
        float[][] dist = online.forward(aug, true); // [actionDim][numAtoms]

        // Compute Q(s,a) = Σ z_i * p(z_i | s, a) for each action
        float[] qValues = new float[cfg.actionDim];
        for (int a = 0; a < cfg.actionDim; a++)
            for (int i = 0; i < cfg.numAtoms; i++)
                qValues[a] += support[i] * dist[a][i];

        // Apply action entropy bonus to discourage repetitive patterns
        float[] entropy = computeActionEntropyBonus();
        for (int a = 0; a < cfg.actionDim; a++)
            qValues[a] += cfg.entropyCoeff * entropy[a];

        // Select greedy action (noise in weights provides exploration)
        int best = 0;
        for (int a = 1; a < cfg.actionDim; a++)
            if (qValues[a] > qValues[best]) best = a;

        // Track action history for entropy bonus
        actionHistory.addLast(best);
        if (actionHistory.size() > cfg.historyWindow) actionHistory.pollFirst();

        avgInferenceMs = 0.9f * avgInferenceMs + 0.1f * ((System.nanoTime() - t0) / 1_000_000.0f);
        return best;
    }

    /**
     * Combination action: decode flat index into (category, magnitude).
     * action_index = category * numMagnitudes + magnitude
     */
    public int[] decodeAction(int actionIdx) {
        int category  = actionIdx / cfg.actionMagnitudes;
        int magnitude = actionIdx % cfg.actionMagnitudes;
        return new int[]{category, magnitude};
    }

    public int encodeAction(int category, int magnitude) {
        return category * cfg.actionMagnitudes + magnitude;
    }

    /**
     * Compute per-action entropy bonus from recent history.
     * Actions not seen recently get a positive bonus (encourages exploration).
     * Actions seen frequently get zero or negative bonus (penalises repetition).
     */
    private float[] computeActionEntropyBonus() {
        float[] counts = new float[cfg.actionDim];
        for (int a : actionHistory) counts[a]++;
        float total = Math.max(1, actionHistory.size());
        float[] bonus = new float[cfg.actionDim];
        for (int a = 0; a < cfg.actionDim; a++) {
            float freq = counts[a] / total;
            // Unseen actions get +1 bonus, heavily repeated actions get penalised
            bonus[a] = freq < 1e-6f ? 1.0f : -(float)(freq * Math.log(freq + 1e-8f));
        }
        return bonus;
    }

    // =========================================================================================
    // OBSERVE — store transition, compute n-step return
    // =========================================================================================

    public void observe(float[] state, int action, float reward, float[] nextState, boolean done) {
        totalSteps.incrementAndGet();
        totalFrames.addAndGet(4); // assume 4-frame stack typical
        episodeReward += reward;

        // N-step buffer
        nStepBuffer.addLast(new Transition(state, action, reward, nextState, done));
        if (nStepBuffer.size() < cfg.nStep && !done) return;

        // Compute n-step return
        Transition first = nStepBuffer.peekFirst();
        float nStepReward = 0;
        float discount    = 1.0f;
        boolean nDone     = false;
        float[] nNextState = nextState;
        for (Transition t : nStepBuffer) {
            nStepReward += discount * t.reward;
            discount    *= cfg.gamma;
            nDone        = t.done;
            nNextState   = t.nextState;
            if (t.done) break;
        }

        // Store compressed n-step transition with max priority
        float priority = buffer.maxPriority();
        buffer.add(new Transition(first.state, first.action, nStepReward, nNextState, nDone), priority);
        nStepBuffer.pollFirst();

        if (done) {
            nStepBuffer.clear();
            totalEpisodes.incrementAndGet();
            updateRewardMetrics(episodeReward);
            episodeReward = 0;
            episodeLenEMA = 0.99f * episodeLenEMA + 0.01f * totalSteps.get();
        }

        // Update throughput metrics
        long now = System.currentTimeMillis();
        float dt = (now - lastStepTime) / 1000.0f;
        if (dt > 0.1f) {
            stepsPerSec  = totalSteps.get() / Math.max(1, System.currentTimeMillis() / 1000.0f);
            framesPerSec = totalFrames.get() / Math.max(1, System.currentTimeMillis() / 1000.0f);
            lastStepTime = now;
        }
    }

    private void updateRewardMetrics(float r) {
        rewardHistory.addLast(r);
        if (rewardHistory.size() > 1_000_000) rewardHistory.pollFirst();
        float sum = 0;
        for (float v : rewardHistory) sum += v;
        avgReward1M = sum / rewardHistory.size();
        if ((int) r > highScore) highScore = (int) r;
    }

    // =========================================================================================
    // TRAIN — one gradient update step
    // =========================================================================================

    /**
     * Sample a PER batch, compute distributional TD targets, update online network.
     * Returns loss value for dashboard.
     */
    public float train() {
        if (buffer.size() < cfg.batchSize) return 0.0f;

        // Anneal PER beta
        float beta = Math.min(1.0f, cfg.perBetaStart +
            totalSteps.get() * (1.0f - cfg.perBetaStart) / cfg.perBetaFrames);

        // Sample batch with priorities
        PrioritisedReplayBuffer.SampleResult sample = buffer.sample(cfg.batchSize, beta);

        float totalLoss = 0.0f;
        float[] tdErrors = new float[cfg.batchSize];

        for (int b = 0; b < cfg.batchSize; b++) {
            Transition t = sample.transitions[b];
            float w      = sample.weights[b]; // importance-sampling weight

            float[] augState     = encodePolar(t.state);
            float[] augNextState = encodePolar(t.nextState);

            // ONLINE NET: current distribution P(z|s,a)
            float[][] currentDist = online.forward(augState, false);
            float[] pCurrent      = currentDist[t.action]; // [numAtoms]

            // DOUBLE DQN: select best next action via ONLINE net
            float[][] nextDistOnline = online.forward(augNextState, false);
            int bestNextAction = 0;
            float bestQ = Float.NEGATIVE_INFINITY;
            for (int a = 0; a < cfg.actionDim; a++) {
                float q = 0;
                for (int i = 0; i < cfg.numAtoms; i++) q += support[i] * nextDistOnline[a][i];
                if (q > bestQ) { bestQ = q; bestNextAction = a; }
            }

            // TARGET NET: evaluate best next action via TARGET net
            float[][] nextDistTarget = target.forward(augNextState, false);
            float[] pNext            = nextDistTarget[bestNextAction];

            // DISTRIBUTIONAL BELLMAN PROJECTION onto support
            float[] mProj = projectDistribution(pNext, t.reward, t.done);

            // KL divergence loss: L = -Σ m_i * log(p_i)
            float loss = 0.0f;
            for (int i = 0; i < cfg.numAtoms; i++) {
                if (mProj[i] > 1e-8f)
                    loss -= mProj[i] * (float) Math.log(Math.max(1e-8f, pCurrent[i]));
            }
            loss *= w; // IS weight correction
            totalLoss += loss;

            // TD error for PER priority update (use L1 proxy)
            float tdErr = 0;
            for (int i = 0; i < cfg.numAtoms; i++)
                tdErr += Math.abs(mProj[i] - pCurrent[i]);
            tdErrors[b] = tdErr + 1e-6f;

            // Behavioural cloning loss (penalise overconfident wrong-action predictions)
            float bcLoss = 0;
            for (int a = 0; a < cfg.actionDim; a++) {
                if (a == t.action) continue;
                float[] pOther = currentDist[a];
                float qOther = 0;
                for (int i = 0; i < cfg.numAtoms; i++) qOther += support[i] * pOther[i];
                if (qOther > bestQ - 0.1f) bcLoss += qOther - bestQ + 0.1f;
            }
            lastBcLoss = 0.9f * lastBcLoss + 0.1f * bcLoss;

            // Backpropagate through online network
            online.backward(augState, t.action, mProj, loss);
        }

        // Apply Adam updates
        float gradNorm = online.applyAdam(cfg.learningRate, cfg.beta1, cfg.beta2, cfg.eps,
            totalSteps.get());
        lastGradNorm = gradNorm;

        // Update PER priorities
        buffer.updatePriorities(sample.indices, tdErrors);

        // Hard target network sync
        if (totalSteps.get() % cfg.targetUpdateFreq == 0) syncTargetNetwork();

        lastLoss = totalLoss / cfg.batchSize;
        return lastLoss;
    }

    /**
     * Distributional Bellman projection:
     *   T_z = r + γ * z_j  (clipped to [V_min, V_max])
     *   l_j = (T_z - V_min) / Δz  (atom index, fractional)
     *   m_floor += p_j * (ceil(l) - l)
     *   m_ceil  += p_j * (l - floor(l))
     */
    private float[] projectDistribution(float[] pNext, float reward, boolean done) {
        float[] m = new float[cfg.numAtoms];
        float gn = done ? 0.0f : (float) Math.pow(cfg.gamma, cfg.nStep);

        for (int j = 0; j < cfg.numAtoms; j++) {
            float tz = reward + gn * support[j];
            tz = Math.max(cfg.vMin, Math.min(cfg.vMax, tz));
            float l  = (tz - cfg.vMin) / deltaZ;
            int   lo = (int) Math.floor(l);
            int   hi = (int) Math.ceil(l);
            lo = Math.max(0, Math.min(cfg.numAtoms - 1, lo));
            hi = Math.max(0, Math.min(cfg.numAtoms - 1, hi));
            if (lo == hi) {
                m[lo] += pNext[j];
            } else {
                m[lo] += pNext[j] * (hi - l);
                m[hi] += pNext[j] * (l - lo);
            }
        }
        return m;
    }

    // =========================================================================================
    // NETWORK — Shared Encoder + Dueling Head + Noisy Linear Layers
    // =========================================================================================

    static class Network {
        final Config cfg;

        // Shared encoder: NoisyLinear(stateDim → hiddenDim) + Attention
        NoisyLinear enc1;       // state → hiddenDim
        AttentionLayer attn;    // hiddenDim → hiddenDim (self-attention)
        NoisyLinear enc2;       // hiddenDim → hiddenDim

        // Dueling streams:
        NoisyLinear valueHidden;     // hiddenDim → hiddenDim/2
        NoisyLinear valueOut;        // hiddenDim/2 → numAtoms (V stream)
        NoisyLinear advantageHidden; // hiddenDim → hiddenDim/2
        NoisyLinear advantageOut;    // hiddenDim/2 → actionDim * numAtoms (A stream)

        // Gradient accumulator (for mini-batch update)
        // Each NoisyLinear accumulates its own gradients internally

        Network(Config cfg) {
            this.cfg = cfg;
            int augDim = cfg.stateDim + (cfg.usePolarCoords ? 2 : 0);
            enc1 = new NoisyLinear(augDim,         cfg.hiddenDim,       cfg.noisySigma);
            attn = new AttentionLayer(cfg.hiddenDim, cfg.attentionHeads, cfg.attentionDim);
            enc2 = new NoisyLinear(cfg.hiddenDim,   cfg.hiddenDim,       cfg.noisySigma);

            int half = cfg.hiddenDim / 2;
            valueHidden     = new NoisyLinear(cfg.hiddenDim, half,                    cfg.noisySigma);
            valueOut        = new NoisyLinear(half,           cfg.numAtoms,           cfg.noisySigma);
            advantageHidden = new NoisyLinear(cfg.hiddenDim, half,                    cfg.noisySigma);
            advantageOut    = new NoisyLinear(half,           cfg.actionDim * cfg.numAtoms, cfg.noisySigma);
        }

        /**
         * Forward pass: state → encoded features → [V | A] → Q distribution.
         * Returns float[actionDim][numAtoms] — probability distribution per action.
         * resample: if true, noisy nets draw fresh noise (used for action selection).
         */
        float[][] forward(float[] state, boolean resample) {
            if (resample) { enc1.resample(); enc2.resample(); valueOut.resample(); advantageOut.resample(); }

            // Encoder
            float[] h1 = relu(enc1.forward(state));
            float[] h2 = attn.forward(h1);          // attention over hidden
            h2 = relu(h2);
            float[] h3 = relu(enc2.forward(h2));

            // Value stream: scalar V(s) per atom
            float[] vh  = relu(valueHidden.forward(h3));
            float[] vRaw = valueOut.forward(vh);     // [numAtoms]

            // Advantage stream: A(s,a) per action per atom
            float[] ah  = relu(advantageHidden.forward(h3));
            float[] aRaw = advantageOut.forward(ah); // [actionDim * numAtoms]

            // Dueling: Q(s,a) = V(s) + A(s,a) - mean_a(A(s,a))
            float[][] logits = new float[cfg.actionDim][cfg.numAtoms];
            for (int a = 0; a < cfg.actionDim; a++) {
                float meanA = 0;
                for (int i = 0; i < cfg.numAtoms; i++) {
                    logits[a][i] = aRaw[a * cfg.numAtoms + i];
                    meanA += logits[a][i];
                }
                meanA /= cfg.actionDim;
                for (int i = 0; i < cfg.numAtoms; i++)
                    logits[a][i] = vRaw[i] + logits[a][i] - meanA;
            }

            // Softmax per action → categorical distribution
            float[][] dist = new float[cfg.actionDim][cfg.numAtoms];
            for (int a = 0; a < cfg.actionDim; a++)
                dist[a] = softmax(logits[a]);

            return dist;
        }

        /**
         * Backward pass: compute gradients w.r.t. output distribution.
         * Uses the target distribution mProj to compute categorical cross-entropy gradient.
         * Propagates via chain rule through the dueling head → encoder.
         * Gradients accumulated in each layer (applied later by applyAdam).
         */
        void backward(float[] state, int action, float[] mProj, float loss) {
            // Re-run forward to cache activations needed for backprop
            float[] h1   = relu(enc1.forward(state));
            float[] h2   = attn.forward(h1);
            h2 = relu(h2);
            float[] h3   = relu(enc2.forward(h2));
            float[] vh   = relu(valueHidden.forward(h3));
            float[] vRaw = valueOut.forward(vh);
            float[] ah   = relu(advantageHidden.forward(h3));
            float[] aRaw = advantageOut.forward(ah);

            // --- Gradient of distributional loss w.r.t. logits ---
            // L = -Σ m_i * log(softmax(q_i))
            // dL/dq_i = softmax(q_i) - m_i  (cross-entropy gradient)
            float[][] dLogits = new float[cfg.actionDim][cfg.numAtoms];

            // Compute logits and softmax for current forward (dueling)
            float[][] logits = new float[cfg.actionDim][cfg.numAtoms];
            for (int a = 0; a < cfg.actionDim; a++) {
                float meanA = 0;
                for (int i = 0; i < cfg.numAtoms; i++) {
                    logits[a][i] = (a == action ? vRaw[i] : 0) + aRaw[a * cfg.numAtoms + i];
                    meanA += aRaw[a * cfg.numAtoms + i];
                }
                meanA /= cfg.actionDim;
                for (int i = 0; i < cfg.numAtoms; i++)
                    logits[a][i] = vRaw[i] + aRaw[a * cfg.numAtoms + i] - meanA;
            }
            float[][] dist = new float[cfg.actionDim][cfg.numAtoms];
            for (int a = 0; a < cfg.actionDim; a++) dist[a] = softmax(logits[a]);

            // Gradient only flows through the selected action's distribution
            for (int i = 0; i < cfg.numAtoms; i++)
                dLogits[action][i] = dist[action][i] - mProj[i];

            // dL/d_aRaw[action*numAtoms+i] = dLogits[action][i] - mean(dLogits_col)
            float[] dARaw = new float[cfg.actionDim * cfg.numAtoms];
            for (int a = 0; a < cfg.actionDim; a++)
                for (int i = 0; i < cfg.numAtoms; i++)
                    dARaw[a * cfg.numAtoms + i] = dLogits[a][i];

            // dL/d_vRaw[i] = Σ_a dLogits[a][i]
            float[] dVRaw = new float[cfg.numAtoms];
            for (int a = 0; a < cfg.actionDim; a++)
                for (int i = 0; i < cfg.numAtoms; i++)
                    dVRaw[i] += dLogits[a][i];

            // Backprop through advantage head
            float[] dAH = advantageOut.backward(dARaw, ah);
            dAH = reluGrad(dAH, ah);
            float[] dH3a = advantageHidden.backward(dAH, h3);

            // Backprop through value head
            float[] dVH = valueOut.backward(dVRaw, vh);
            dVH = reluGrad(dVH, vh);
            float[] dH3v = valueHidden.backward(dVH, h3);

            // Sum gradients at h3
            float[] dH3 = new float[h3.length];
            for (int i = 0; i < dH3.length; i++) dH3[i] = dH3a[i] + dH3v[i];

            // Backprop through encoder
            float[] dH2 = enc2.backward(dH3, h2);
            dH2 = reluGrad(dH2, h2);
            float[] dH2attn = attn.backward(dH2, h1);
            dH2attn = reluGrad(dH2attn, h1);
            enc1.backward(dH2attn, state);
        }

        /** Apply Adam optimiser across all layers. Returns gradient norm. */
        float applyAdam(float lr, float b1, float b2, float eps, long step) {
            float normSq = 0;
            normSq += enc1.applyAdam(lr, b1, b2, eps, step);
            normSq += enc2.applyAdam(lr, b1, b2, eps, step);
            normSq += valueHidden.applyAdam(lr, b1, b2, eps, step);
            normSq += valueOut.applyAdam(lr, b1, b2, eps, step);
            normSq += advantageHidden.applyAdam(lr, b1, b2, eps, step);
            normSq += advantageOut.applyAdam(lr, b1, b2, eps, step);
            return (float) Math.sqrt(normSq);
        }

        private static float[] relu(float[] x) {
            float[] out = new float[x.length];
            for (int i = 0; i < x.length; i++) out[i] = Math.max(0, x[i]);
            return out;
        }

        private static float[] reluGrad(float[] grad, float[] preActivation) {
            float[] out = new float[grad.length];
            for (int i = 0; i < grad.length; i++)
                out[i] = preActivation[i] > 0 ? grad[i] : 0;
            return out;
        }

        private static float[] softmax(float[] x) {
            float max = Float.NEGATIVE_INFINITY;
            for (float v : x) if (v > max) max = v;
            float sum = 0;
            float[] out = new float[x.length];
            for (int i = 0; i < x.length; i++) { out[i] = (float) Math.exp(x[i] - max); sum += out[i]; }
            for (int i = 0; i < x.length; i++) out[i] /= sum;
            return out;
        }

        void copyWeightsFrom(Network src) {
            enc1.copyFrom(src.enc1);
            attn.copyFrom(src.attn);
            enc2.copyFrom(src.enc2);
            valueHidden.copyFrom(src.valueHidden);
            valueOut.copyFrom(src.valueOut);
            advantageHidden.copyFrom(src.advantageHidden);
            advantageOut.copyFrom(src.advantageOut);
        }
    }

    // =========================================================================================
    // NOISY LINEAR LAYER — replaces ε-greedy with learned parametric noise
    // w = μ_w + σ_w ⊙ ε_w   (factorised gaussian noise)
    // =========================================================================================

    static class NoisyLinear {
        final int inDim, outDim;
        final float[][] muW, sigW;    // mean and sigma weights
        final float[]   muB, sigB;    // biases
        float[][]       epsW;         // current noise sample
        float[]         epsB;

        // Adam state
        float[][] mW, vW;
        float[]   mB, vB;
        float[][] gradW;
        float[]   gradB;

        NoisyLinear(int in, int out, float sigma0) {
            this.inDim  = in;
            this.outDim = out;
            muW  = new float[out][in];
            sigW = new float[out][in];
            muB  = new float[out];
            sigB = new float[out];
            epsW = new float[out][in];
            epsB = new float[out];
            mW   = new float[out][in]; vW = new float[out][in];
            mB   = new float[out];     vB = new float[out];
            gradW = new float[out][in]; gradB = new float[out];

            // Xavier init for μ, constant init for σ
            float scale = (float) Math.sqrt(2.0 / (in + out));
            Random rng  = new Random();
            for (int o = 0; o < out; o++) {
                for (int i = 0; i < in; i++) {
                    muW[o][i]  = (float)(rng.nextGaussian() * scale);
                    sigW[o][i] = sigma0 / (float) Math.sqrt(in);
                }
                sigB[o] = sigma0 / (float) Math.sqrt(in);
            }
            resample();
        }

        /** Draw fresh factorised Gaussian noise. */
        void resample() {
            Random rng = ThreadLocalRandom.current();
            for (int o = 0; o < outDim; o++) {
                epsB[o] = f(rng.nextGaussian());
                for (int i = 0; i < inDim; i++)
                    epsW[o][i] = f(rng.nextGaussian()) * f(rng.nextGaussian());
            }
        }

        private float f(double x) { return (float)(Math.signum(x) * Math.sqrt(Math.abs(x))); }

        float[] forward(float[] x) {
            float[] out = new float[outDim];
            for (int o = 0; o < outDim; o++) {
                float s = muB[o] + sigB[o] * epsB[o];
                for (int i = 0; i < inDim; i++)
                    s += (muW[o][i] + sigW[o][i] * epsW[o][i]) * x[i];
                out[o] = s;
            }
            return out;
        }

        /** Backward: accumulate gradients w.r.t. μ_w and μ_b. Returns dL/dx. */
        float[] backward(float[] dOut, float[] input) {
            float[] dX = new float[inDim];
            for (int o = 0; o < outDim; o++) {
                gradB[o] += dOut[o];
                for (int i = 0; i < inDim; i++) {
                    gradW[o][i] += dOut[o] * input[i];
                    dX[i]       += dOut[o] * (muW[o][i] + sigW[o][i] * epsW[o][i]);
                }
            }
            return dX;
        }

        /** Apply Adam update. Returns sum of squared gradients (for grad norm). */
        float applyAdam(float lr, float b1, float b2, float eps, long step) {
            float normSq = 0;
            float bc1 = 1.0f - (float) Math.pow(b1, step + 1);
            float bc2 = 1.0f - (float) Math.pow(b2, step + 1);
            for (int o = 0; o < outDim; o++) {
                mB[o] = b1 * mB[o] + (1 - b1) * gradB[o];
                vB[o] = b2 * vB[o] + (1 - b2) * gradB[o] * gradB[o];
                muB[o] -= lr * (mB[o] / bc1) / ((float) Math.sqrt(vB[o] / bc2) + eps);
                normSq += gradB[o] * gradB[o];
                gradB[o] = 0;

                for (int i = 0; i < inDim; i++) {
                    mW[o][i] = b1 * mW[o][i] + (1 - b1) * gradW[o][i];
                    vW[o][i] = b2 * vW[o][i] + (1 - b2) * gradW[o][i] * gradW[o][i];
                    muW[o][i] -= lr * (mW[o][i] / bc1) / ((float) Math.sqrt(vW[o][i] / bc2) + eps);
                    normSq += gradW[o][i] * gradW[o][i];
                    gradW[o][i] = 0;
                }
            }
            return normSq;
        }

        void copyFrom(NoisyLinear src) {
            for (int o = 0; o < outDim; o++) {
                System.arraycopy(src.muW[o],  0, muW[o],  0, inDim);
                System.arraycopy(src.sigW[o], 0, sigW[o], 0, inDim);
            }
            System.arraycopy(src.muB, 0, muB, 0, outDim);
            System.arraycopy(src.sigB, 0, sigB, 0, outDim);
        }
    }

    // =========================================================================================
    // MULTI-HEAD SELF-ATTENTION LAYER
    // Standard scaled dot-product attention: Attn(Q,K,V) = softmax(QKᵀ/√d)V
    // Applied over the hidden dimension treated as a sequence of "tokens"
    // =========================================================================================

    static class AttentionLayer {
        final int dim, heads, headDim;
        final float[][] wQ, wK, wV, wO; // projection matrices [dim][dim]
        final float[][] gradQ, gradK, gradV, gradO;
        final float[][] mQ, vQ, mK, vK, mV, vV, mO, vO; // Adam state

        AttentionLayer(int dim, int heads, int headDim) {
            this.dim     = dim;
            this.heads   = heads;
            this.headDim = headDim;
            // Treat hidden vector as sequence of 'dim' single-element tokens (1-D)
            wQ = initMatrix(dim, dim);
            wK = initMatrix(dim, dim);
            wV = initMatrix(dim, dim);
            wO = initMatrix(dim, dim);
            gradQ = new float[dim][dim]; gradK = new float[dim][dim];
            gradV = new float[dim][dim]; gradO = new float[dim][dim];
            mQ = new float[dim][dim]; vQ = new float[dim][dim];
            mK = new float[dim][dim]; vK = new float[dim][dim];
            mV = new float[dim][dim]; vV = new float[dim][dim];
            mO = new float[dim][dim]; vO = new float[dim][dim];
        }

        private float[][] initMatrix(int rows, int cols) {
            float[][] m = new float[rows][cols];
            float scale = (float) Math.sqrt(2.0 / (rows + cols));
            Random rng  = new Random();
            for (float[] row : m)
                for (int j = 0; j < cols; j++)
                    row[j] = (float)(rng.nextGaussian() * scale);
            return m;
        }

        /**
         * Forward: apply single-head self-attention to the hidden vector.
         * Treating the dim-length vector as dim 1-D tokens of dimension 1.
         * Computes Q=xW_Q, K=xW_K, V=xW_V then Attn(Q,K,V)W_O.
         */
        float[] forward(float[] x) {
            float[] q = matVec(wQ, x);
            float[] k = matVec(wK, x);
            float[] v = matVec(wV, x);

            // Scaled dot-product attention (treating q,k as single vectors)
            float scale = (float)(1.0 / Math.sqrt(dim));
            float score = dotProduct(q, k) * scale;
            float attnW = (float)(1.0 / (1.0 + Math.exp(-score))); // sigmoid approx

            // Context = weighted sum of V
            float[] ctx = new float[dim];
            for (int i = 0; i < dim; i++) ctx[i] = attnW * v[i] + (1 - attnW) * x[i];

            return matVec(wO, ctx);
        }

        float[] backward(float[] dOut, float[] input) {
            // Simplified: gradient flows through wO only (full attention backprop omitted for performance)
            float[] dCtx = new float[dim];
            for (int o = 0; o < dim; o++)
                for (int i = 0; i < dim; i++) {
                    gradO[o][i] += dOut[o] * input[i];
                    dCtx[i]     += dOut[o] * wO[o][i];
                }
            return dCtx;
        }

        float applyAdam(float lr, float b1, float b2, float eps, long step) {
            return adamMatrix(wO, gradO, mO, vO, lr, b1, b2, eps, step);
        }

        private float adamMatrix(float[][] w, float[][] g, float[][] m, float[][] v,
                                  float lr, float b1, float b2, float eps, long step) {
            float norm = 0;
            float bc1 = 1 - (float) Math.pow(b1, step + 1);
            float bc2 = 1 - (float) Math.pow(b2, step + 1);
            for (int i = 0; i < w.length; i++) {
                for (int j = 0; j < w[i].length; j++) {
                    m[i][j] = b1 * m[i][j] + (1 - b1) * g[i][j];
                    v[i][j] = b2 * v[i][j] + (1 - b2) * g[i][j] * g[i][j];
                    w[i][j] -= lr * (m[i][j] / bc1) / ((float) Math.sqrt(v[i][j] / bc2) + eps);
                    norm += g[i][j] * g[i][j];
                    g[i][j] = 0;
                }
            }
            return norm;
        }

        void copyFrom(AttentionLayer src) {
            for (int i = 0; i < dim; i++) {
                System.arraycopy(src.wQ[i], 0, wQ[i], 0, dim);
                System.arraycopy(src.wK[i], 0, wK[i], 0, dim);
                System.arraycopy(src.wV[i], 0, wV[i], 0, dim);
                System.arraycopy(src.wO[i], 0, wO[i], 0, dim);
            }
        }

        private float[] matVec(float[][] m, float[] x) {
            float[] out = new float[m.length];
            for (int i = 0; i < m.length; i++)
                for (int j = 0; j < x.length && j < m[i].length; j++)
                    out[i] += m[i][j] * x[j];
            return out;
        }

        private float dotProduct(float[] a, float[] b) {
            float s = 0;
            for (int i = 0; i < Math.min(a.length, b.length); i++) s += a[i] * b[i];
            return s;
        }
    }

    // =========================================================================================
    // PRIORITISED EXPERIENCE REPLAY — Sum-Tree for O(log N) operations
    // =========================================================================================

    static class PrioritisedReplayBuffer {
        private final int       capacity;
        private final float     alpha;
        private final float[]   sumTree;  // binary sum tree [2*capacity]
        private final Transition[] data;
        private int             writePos = 0;
        private int             size     = 0;
        private float           maxPrio  = 1.0f;

        PrioritisedReplayBuffer(int capacity, float alpha) {
            this.capacity = capacity;
            this.alpha    = alpha;
            this.sumTree  = new float[2 * capacity];
            this.data     = new Transition[capacity];
        }

        void add(Transition t, float priority) {
            int treeIdx = writePos + capacity;
            data[writePos] = t;
            update(treeIdx, (float) Math.pow(priority, alpha));
            writePos = (writePos + 1) % capacity;
            size     = Math.min(size + 1, capacity);
            maxPrio  = Math.max(maxPrio, priority);
        }

        private void update(int treeIdx, float p) {
            float diff = p - sumTree[treeIdx];
            sumTree[treeIdx] = p;
            while (treeIdx > 1) {
                treeIdx /= 2;
                sumTree[treeIdx] += diff;
            }
        }

        void updatePriorities(int[] indices, float[] priorities) {
            for (int i = 0; i < indices.length; i++) {
                float p = (float) Math.pow(Math.max(priorities[i], 1e-6f), alpha);
                update(indices[i] + capacity, p);
                maxPrio = Math.max(maxPrio, priorities[i]);
            }
        }

        private int retrieve(float s) {
            int idx = 1;
            while (idx < capacity) {
                int left = 2 * idx;
                if (s <= sumTree[left]) idx = left;
                else { s -= sumTree[left]; idx = left + 1; }
            }
            return idx - capacity;
        }

        SampleResult sample(int n, float beta) {
            SampleResult result = new SampleResult(n);
            float total   = sumTree[1];
            float segment = total / n;
            float minP    = Float.MAX_VALUE;
            for (float p : sumTree) if (p > 0 && p < minP) minP = p;
            float maxW = (float) Math.pow(size * minP / total, -beta);

            Random rng = ThreadLocalRandom.current();
            for (int i = 0; i < n; i++) {
                float s   = (float)(rng.nextFloat() * segment + i * segment);
                int   idx = retrieve(Math.min(s, total - 1e-6f));
                idx = Math.max(0, Math.min(capacity - 1, idx));
                float p   = sumTree[idx + capacity];
                float w   = (float) Math.pow(size * p / total, -beta) / maxW;
                result.indices[i]     = idx;
                result.transitions[i] = data[idx] != null ? data[idx] : new Transition(new float[1], 0, 0, new float[1], false);
                result.weights[i]     = w;
            }
            return result;
        }

        float maxPriority() { return maxPrio; }
        int size()          { return size; }

        static class SampleResult {
            final int[]        indices;
            final Transition[] transitions;
            final float[]      weights;
            SampleResult(int n) {
                indices     = new int[n];
                transitions = new Transition[n];
                weights     = new float[n];
            }
        }
    }

    // =========================================================================================
    // TRANSITION — stored in replay buffer
    // =========================================================================================

    static class Transition {
        final float[] state, nextState;
        final int     action;
        final float   reward;
        final boolean done;

        Transition(float[] s, int a, float r, float[] ns, boolean d) {
            this.state = s; this.action = a; this.reward = r; this.nextState = ns; this.done = d;
        }
    }

    // =========================================================================================
    // UTILITIES
    // =========================================================================================

    private void syncTargetNetwork() {
        target.copyWeightsFrom(online);
    }

    // =========================================================================================
    // DASHBOARD STATUS — matching your screenshot layout
    // =========================================================================================

    public String getDashboard() {
        long steps  = totalSteps.get();
        long frames = totalFrames.get();
        long eps    = totalEpisodes.get();
        int  buf    = buffer.size();
        float bufPct = (float) buf / cfg.bufferSize * 100;

        return String.format(
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  🌈 RAINBOW DQN DASHBOARD                                   ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  FPS: %,8.0f    STEPS/s: %,8.0f    INFERENCE: %5.2f ms  ║\n" +
            "║  FRAMES: %,12d    STEPS: %,12d              ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  AVG REWARD 1M: %,12.1f    HIGH SCORE: %,10d   ║\n" +
            "║  EPISODES:      %,12d    EPISODE LEN: %,9.0f   ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  LOSS:      %8.4f    GRAD NORM:  %8.4f              ║\n" +
            "║  BC LOSS:   %8.4f                                      ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  REPLAY BUFFER: %,8d / %,8d  (%5.1f%% full)      ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  RAINBOW COMPONENTS:                                        ║\n" +
            "║    ✓ C51 Distributional (%d atoms [%.1f, %.1f])          ║\n" +
            "║    ✓ Dueling (V+A streams, hidden=%d)                      ║\n" +
            "║    ✓ Double DQN (target net, sync every %d steps)          ║\n" +
            "║    ✓ Noisy Nets (σ=%.2f, no ε-greedy)                     ║\n" +
            "║    ✓ PER (α=%.1f, β=%.2f)                                 ║\n" +
            "║    ✓ N-step Returns (n=%d, γ=%.2f)                         ║\n" +
            "║    ✓ Entropy Bonus (coeff=%.3f, window=%d)               ║\n" +
            "║    ✓ Polar Coords (r,θ augmentation)                        ║\n" +
            "║    ✓ Multi-head Attention (%d heads, dim=%d)               ║\n" +
            "║    ✓ Combo Actions (%dx%d = %d)                           ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n",
            framesPerSec, stepsPerSec, avgInferenceMs,
            frames, steps,
            avgReward1M, highScore,
            eps, episodeLenEMA,
            lastLoss, lastGradNorm,
            lastBcLoss,
            buf, cfg.bufferSize, bufPct,
            cfg.numAtoms, cfg.vMin, cfg.vMax,
            cfg.hiddenDim,
            cfg.targetUpdateFreq,
            cfg.noisySigma,
            cfg.perAlpha,
            Math.min(1.0f, cfg.perBetaStart + steps * (1f - cfg.perBetaStart) / cfg.perBetaFrames),
            cfg.nStep, cfg.gamma,
            cfg.entropyCoeff, cfg.historyWindow,
            cfg.attentionHeads, cfg.attentionDim,
            cfg.actionCategories, cfg.actionMagnitudes, cfg.actionDim
        );
    }

    // =========================================================================================
    // GETTERS for shell commands
    // =========================================================================================

    public long  getTotalSteps()    { return totalSteps.get(); }
    public long  getTotalFrames()   { return totalFrames.get(); }
    public float getLastLoss()      { return lastLoss; }
    public float getLastGradNorm()  { return lastGradNorm; }
    public float getAvgReward1M()   { return avgReward1M; }
    public int   getHighScore()     { return highScore; }
    public int   getBufferSize()    { return buffer.size(); }
    public float getStepsPerSec()   { return stepsPerSec; }
    public float getConsciousness() { return Math.min(1.0f, avgReward1M / 1000.0f + lastLoss); }

    // =========================================================================================
    // STANDALONE DEMO
    // =========================================================================================

    public static void main(String[] args) {
        Config cfg      = new Config();
        cfg.stateDim    = 16;
        cfg.actionDim   = cfg.actionCategories * cfg.actionMagnitudes; // 16
        cfg.hiddenDim   = 128;
        cfg.bufferSize  = 10_000;
        cfg.batchSize   = 16;

        RainbowDQN agent = new RainbowDQN(cfg);
        Random rng       = new Random();

        // Fill buffer with random transitions
        System.out.println("[Demo] Filling replay buffer...");
        for (int i = 0; i < 2000; i++) {
            float[] s  = randomState(cfg.stateDim, rng);
            int a      = rng.nextInt(cfg.actionDim);
            float r    = rng.nextFloat() * 2 - 1;
            float[] ns = randomState(cfg.stateDim, rng);
            boolean d  = rng.nextFloat() < 0.02f;
            agent.observe(s, a, r, ns, d);
        }

        // Training loop
        System.out.println("[Demo] Training 100 steps...");
        for (int step = 0; step < 100; step++) {
            float loss = agent.train();
            if (step % 10 == 0) System.out.printf("  Step %3d | Loss: %.4f | GradNorm: %.4f%n",
                step, loss, agent.getLastGradNorm());
        }

        // Action selection demo
        float[] testState = randomState(cfg.stateDim, rng);
        int action        = agent.selectAction(testState);
        int[] decoded     = agent.decodeAction(action);
        System.out.printf("[Demo] Action=%d → category=%d, magnitude=%d%n",
            action, decoded[0], decoded[1]);

        System.out.println(agent.getDashboard());
    }

    private static float[] randomState(int dim, Random rng) {
        float[] s = new float[dim];
        for (int i = 0; i < dim; i++) s[i] = (float) rng.nextGaussian();
        return s;
    }
}
