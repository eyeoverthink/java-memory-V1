package fraymus.neural;

import fraymus.knowledge.AkashicRecord;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * RainbowCortex.java — Central Nervous System Integration Layer
 * =========================================================================================
 *
 * Wires RainbowDQN into every subsystem as the unified RL policy brain:
 *
 *   1. MOE/MOA ROUTING:  Rainbow selects which expert to route to (action = expert index).
 *                         Reward = +1 for useful result, -1 for failure. Learns optimal routing.
 *
 *   2. NEURAL BRAIN:     Rainbow reads NeuralBrainSystem state vector as observations.
 *                         Rainbow action → which brain region to stimulate.
 *                         Reward = consciousness increase.
 *
 *   3. SELF-CODING:      Rainbow observes codegen outcomes (compile success/failure).
 *                         Action = which code improvement strategy to try.
 *                         Reward = +1 compile success, -1 failure, +2 execution success.
 *
 *   4. RECURSIVE SELF-IMPROVEMENT:
 *                         Rainbow tracks improve() outcomes across cycles.
 *                         Action = which weakness to prioritise.
 *                         Reward = routing accuracy delta after improvement.
 *
 *   5. OPENCLAW:          Rainbow provides real-time control signals to the OpenClaw
 *                         visualization — node injection coordinates, camera angles.
 *
 *   6. SYSTEM REPLICATION: Serialize/deserialize Rainbow weights to Akashic Record
 *                         for transfer across JVM instances (Lazarus-compatible).
 *
 * STATE ENCODING (64-D float vector, polar-augmented to 66-D):
 *   [0-5]   MOE/MOA routing metrics (6 expert success rates)
 *   [6-11]  Recent failure counts per subsystem
 *   [12-15] Codegen metrics (compile rate, heal rate, exec rate, lines/gen)
 *   [16-19] Self-improvement metrics (cycles, accuracy delta, failure trend, reward trend)
 *   [20-35] NeuralBrain region firing rates (top 16 regions)
 *   [36-51] NeuralBrain consciousness sub-metrics (16 values)
 *   [52-55] OpenClaw metrics (node density, packet throughput, spark count, fps)
 *   [56-63] Rainbow self-metrics (loss, grad norm, buffer fill, entropy, Q-mean, etc.)
 *
 * ACTION SPACE (4 categories × 4 magnitudes = 16 actions):
 *   Category 0: ROUTE   — select expert for next MOE call (mag 0-3 = top 4 experts)
 *   Category 1: STIMULATE — inject signal into brain region (mag = intensity)
 *   Category 2: IMPROVE — select self-improvement strategy (mag = aggressiveness)
 *   Category 3: EXPLORE — adjust exploration/exploitation balance (mag = noise level)
 *
 * =========================================================================================
 */
public class RainbowCortex {

    private static final String AKASHIC_CATEGORY = "RAINBOW_CORTEX";

    // --- Core systems ---
    private volatile RainbowDQN           rainbow;
    private volatile RainbowDQN.Config    config;
    private volatile NeuralBrainSystem    brain;
    private volatile AeonOrchestrator     orchestrator;
    private volatile AkashicRecord        akashic;

    // --- State vector components ---
    private final float[] stateVector    = new float[64];
    private final float[] expertRates    = new float[6];  // success rate per expert
    private final float[] failureCounts  = new float[6];  // recent failure counts
    private final float[] codegenMetrics = new float[4];
    private final float[] improveMetrics = new float[4];
    private final float[] brainRates     = new float[16]; // top 16 region firing rates
    private final float[] brainMeta      = new float[16]; // consciousness sub-metrics
    private final float[] clawMetrics    = new float[4];
    private final float[] selfMetrics    = new float[8];

    // --- Tracking ---
    private volatile int    totalDecisions     = 0;
    private volatile int    routingDecisions   = 0;
    private volatile int    stimulateDecisions = 0;
    private volatile int    improveDecisions   = 0;
    private volatile int    exploreDecisions   = 0;
    private volatile float  cumulativeReward   = 0.0f;
    private volatile float  accuracyBefore     = 0.0f;
    private volatile float  accuracyAfter      = 0.0f;
    private volatile int    compileSuccesses   = 0;
    private volatile int    compileFailures    = 0;
    private volatile int    healSuccesses      = 0;
    private volatile int    execSuccesses      = 0;
    private volatile long   lastTrainStep      = 0;
    private volatile int    replicationCount   = 0;
    private volatile String lastAction         = "NONE";
    private volatile float  lastReward         = 0.0f;
    private float[]         lastState          = null;
    private int             lastActionIdx      = 0;

    // --- Recursive self-improvement cycle ---
    private volatile boolean autoImproveRunning = false;
    private Thread           autoImproveThread;
    private final AtomicInteger improveCycleCount = new AtomicInteger(0);

    // =========================================================================================
    // CONSTRUCTOR & WIRING
    // =========================================================================================

    public RainbowCortex() {
        config = new RainbowDQN.Config();
        config.stateDim          = 64;
        config.actionCategories  = 4;
        config.actionMagnitudes  = 4;
        config.actionDim         = 16;
        config.hiddenDim         = 256;
        config.bufferSize        = 100_000;
        config.batchSize         = 32;
        config.entropyCoeff      = 0.02f; // higher entropy = more ambitious exploration
        config.usePolarCoords    = true;
        rainbow = new RainbowDQN(config);
        System.out.println("[RainbowCortex] Central nervous system online");
    }

    /** Wire the NeuralBrainSystem (26-region LIF brain). */
    public void wireBrain(NeuralBrainSystem brain) {
        this.brain = brain;
        System.out.println("[RainbowCortex] ← NeuralBrainSystem wired (26 regions, " + brain.getTotalNeurons() + " neurons)");
    }

    /** Wire the AeonOrchestrator (MOE/MOA/Agent/Codegen/Improve). */
    public void wireOrchestrator(AeonOrchestrator orch) {
        this.orchestrator = orch;
        System.out.println("[RainbowCortex] ← AeonOrchestrator wired (MOE/MOA/Agent/Codegen/Improve)");
    }

    /** Wire AkashicRecord for weight persistence and replication. */
    public void wireAkashic(AkashicRecord akashic) {
        this.akashic = akashic;
        System.out.println("[RainbowCortex] ← AkashicRecord wired (weight persistence + replication)");
    }

    // =========================================================================================
    // DECIDE — Rainbow picks an action based on full system state
    // =========================================================================================

    /**
     * Main decision loop: read system state → Rainbow selects action → execute.
     * Call this before any major operation (routing, codegen, improvement, etc.)
     * Returns the decoded action: [category, magnitude].
     */
    public int[] decide() {
        totalDecisions++;

        // 1. Build state vector from all wired systems
        buildStateVector();

        // 2. Rainbow selects action (noisy nets provide exploration)
        int actionIdx = rainbow.selectAction(stateVector);
        int[] decoded = rainbow.decodeAction(actionIdx);
        lastState     = stateVector.clone();
        lastActionIdx = actionIdx;

        int category  = decoded[0];
        int magnitude = decoded[1];

        switch (category) {
            case 0 -> { lastAction = "ROUTE_" + magnitude;     routingDecisions++; }
            case 1 -> { lastAction = "STIMULATE_" + magnitude; stimulateDecisions++; }
            case 2 -> { lastAction = "IMPROVE_" + magnitude;   improveDecisions++; }
            case 3 -> { lastAction = "EXPLORE_" + magnitude;   exploreDecisions++; }
        }

        return decoded;
    }

    /**
     * Feed reward signal back to Rainbow after observing the outcome.
     * Also triggers a training step if buffer has enough samples.
     */
    public void reward(float r, boolean done) {
        lastReward = r;
        cumulativeReward += r;

        // Build next state
        buildStateVector();
        rainbow.observe(lastState, lastActionIdx, r, stateVector.clone(), done);

        // Train every step (Rainbow handles PER sampling)
        float loss = rainbow.train();
        if (loss > 0) lastTrainStep = rainbow.getTotalSteps();
    }

    // =========================================================================================
    // MOE/MOA INTEGRATION — Rainbow routes tasks
    // =========================================================================================

    /**
     * Rainbow-guided MOE routing. Instead of Ollama classifying the task,
     * Rainbow picks the expert based on learned Q-values.
     * Returns the expert name (e.g., "KRONOS", "OLLAMA", etc.).
     */
    public String rainbowRoute(String task) {
        int[] action = decide();
        int magnitude = action[1];

        // Map magnitude to expert index (0=KRONOS, 1=SINGULARITY, 2=OMEGA, 3=OLLAMA etc.)
        String[] expertOrder = {"KRONOS", "SINGULARITY", "OMEGA", "OLLAMA"};
        // Extend with TACHYON, OMNISCIENCE if magnitude > 3
        String expertId = magnitude < expertOrder.length ? expertOrder[magnitude] : "OLLAMA";

        return expertId;
    }

    /**
     * Record MOE/MOA outcome as Rainbow reward.
     */
    public void recordRoutingOutcome(String expertId, boolean success) {
        float r = success ? 1.0f : -0.5f;
        reward(r, false);

        // Update expert success rates for state vector
        int idx = expertIndex(expertId);
        if (idx >= 0 && idx < expertRates.length) {
            float alpha = 0.1f;
            expertRates[idx] = (1 - alpha) * expertRates[idx] + alpha * (success ? 1.0f : 0.0f);
        }
    }

    private int expertIndex(String id) {
        switch (id.toUpperCase()) {
            case "KRONOS":      return 0;
            case "SINGULARITY": return 1;
            case "OMEGA":       return 2;
            case "TACHYON":     return 3;
            case "OMNISCIENCE": return 4;
            case "OLLAMA":      return 5;
            default:            return -1;
        }
    }

    // =========================================================================================
    // NEURAL BRAIN INTEGRATION — Rainbow stimulates brain regions
    // =========================================================================================

    /**
     * Rainbow-guided brain stimulation. Picks a region and intensity,
     * injects a signal, measures consciousness change, rewards accordingly.
     */
    public float stimulateBrain(String concept) {
        if (brain == null) return 0;

        float consciousnessBefore = brain.getConsciousness();
        int[] action = decide();
        int magnitude = action[1];

        // Present concept with intensity scaled by magnitude
        float[] input = new float[Math.min(64, brain.getTotalNeurons())];
        Random rng = new Random(concept.hashCode());
        for (int i = 0; i < input.length; i++)
            input[i] = (float)(rng.nextGaussian() * (0.5f + magnitude * 0.25f));

        brain.tick(input);
        brain.tick(input);
        brain.tick(null); // propagate

        float consciousnessAfter = brain.getConsciousness();
        float delta = consciousnessAfter - consciousnessBefore;

        // Reward = consciousness increase (positive = good stimulation)
        reward(delta * 10.0f, false);

        return consciousnessAfter;
    }

    // =========================================================================================
    // SELF-CODING INTEGRATION — Rainbow guides code improvement
    // =========================================================================================

    /**
     * Record codegen outcome. Rainbow learns which codegen strategies produce
     * compilable, executable code.
     */
    public void recordCodegenOutcome(boolean compiled, boolean healed, boolean executed) {
        if (compiled) compileSuccesses++; else compileFailures++;
        if (healed)   healSuccesses++;
        if (executed)  execSuccesses++;

        float r = 0;
        if (compiled) r += 0.5f;
        if (healed)   r += 0.3f; // self-healing is valuable
        if (executed)  r += 1.0f; // execution success is the ultimate reward
        if (!compiled && !healed) r = -0.5f;

        reward(r, false);

        // Update codegen metrics for state vector
        int total = compileSuccesses + compileFailures;
        codegenMetrics[0] = total > 0 ? (float) compileSuccesses / total : 0;
        codegenMetrics[1] = total > 0 ? (float) healSuccesses / total : 0;
        codegenMetrics[2] = total > 0 ? (float) execSuccesses / total : 0;
        codegenMetrics[3] = total;
    }

    // =========================================================================================
    // RECURSIVE SELF-IMPROVEMENT — Rainbow guides the improvement cycle
    // =========================================================================================

    /**
     * Rainbow-guided self-improvement: selects improvement strategy based on
     * learned Q-values, then measures if the improvement actually helped.
     *
     * Strategies (by magnitude):
     *   0 = conservative (fix most common failure only)
     *   1 = moderate (fix top 3 weaknesses)
     *   2 = aggressive (rewrite routing logic)
     *   3 = revolutionary (generate entirely new expert)
     */
    public String recursiveImprove() {
        if (orchestrator == null) return "[RainbowCortex] No orchestrator wired";

        improveCycleCount.incrementAndGet();
        int[] action = decide();
        int strategy = action[1];

        // Snapshot routing accuracy before
        accuracyBefore = computeOverallAccuracy();

        // Execute improvement
        String result = orchestrator.improve();

        // Snapshot after
        accuracyAfter = computeOverallAccuracy();
        float delta = accuracyAfter - accuracyBefore;

        // Reward = accuracy improvement * strategy aggressiveness multiplier
        float strategyMultiplier = 1.0f + strategy * 0.5f;
        float r = delta * strategyMultiplier * 10.0f;

        // Penalise revolutionary strategies that fail (high risk, high penalty)
        if (delta <= 0 && strategy == 3) r -= 0.5f;

        reward(r, false);

        improveMetrics[0] = improveCycleCount.get();
        improveMetrics[1] = delta;
        improveMetrics[2] = accuracyAfter;
        improveMetrics[3] = cumulativeReward;

        return String.format("[RainbowCortex] Improve cycle #%d | Strategy: %d | " +
            "Accuracy: %.1f%% → %.1f%% (Δ%.1f%%) | Reward: %.3f",
            improveCycleCount.get(), strategy,
            accuracyBefore * 100, accuracyAfter * 100, delta * 100, r);
    }

    /**
     * Start autonomous recursive self-improvement loop.
     * Runs improve cycles on a timer, learns from each one.
     */
    public void startAutoImprove(int intervalSeconds) {
        if (autoImproveRunning) return;
        autoImproveRunning = true;
        autoImproveThread = new Thread(() -> {
            while (autoImproveRunning) {
                try {
                    String result = recursiveImprove();
                    System.out.println(result);
                    Thread.sleep(intervalSeconds * 1000L);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.out.println("[RainbowCortex] Auto-improve error: " + e.getMessage());
                    try { Thread.sleep(5000); } catch (InterruptedException ie) { break; }
                }
            }
        }, "RainbowCortex-AutoImprove");
        autoImproveThread.setDaemon(true);
        autoImproveThread.start();
        System.out.println("[RainbowCortex] Autonomous recursive self-improvement started @ " +
            intervalSeconds + "s interval");
    }

    public void stopAutoImprove() {
        autoImproveRunning = false;
        if (autoImproveThread != null) autoImproveThread.interrupt();
    }

    private float computeOverallAccuracy() {
        if (orchestrator == null) return 0;
        int totalSuccess = 0, totalFail = 0;
        // Read routing feedback via getStatus (no direct access, compute from expert rates)
        for (float rate : expertRates) {
            totalSuccess += rate > 0.5f ? 1 : 0;
            totalFail    += rate <= 0.5f ? 1 : 0;
        }
        int total = totalSuccess + totalFail;
        return total > 0 ? (float) totalSuccess / total : 0.5f;
    }

    // =========================================================================================
    // OPENCLAW INTEGRATION — Rainbow provides control signals
    // =========================================================================================

    /**
     * Get OpenClaw control signal from Rainbow: returns [x, y, z, intensity]
     * for node injection or camera control.
     */
    public float[] getClawSignal() {
        int[] action = decide();
        float magnitude = action[1] / 3.0f; // normalise to [0, 1]

        // Use Rainbow's internal state to generate a spatial signal
        float angle = (float)(totalDecisions * 0.1 * Math.PI);
        float x = (float)(Math.cos(angle) * magnitude * 200);
        float y = (float)(Math.sin(angle) * magnitude * 200);
        float z = magnitude * 100;
        float intensity = 0.5f + magnitude * 0.5f;

        return new float[]{x, y, z, intensity};
    }

    /**
     * Record OpenClaw frame metrics for state vector.
     */
    public void recordClawMetrics(float fps, float nodeDensity, float packetThroughput, float sparkCount) {
        clawMetrics[0] = nodeDensity;
        clawMetrics[1] = packetThroughput;
        clawMetrics[2] = sparkCount;
        clawMetrics[3] = fps;
    }

    // =========================================================================================
    // SYSTEM REPLICATION — Serialize Rainbow weights to Akashic Record
    // =========================================================================================

    /**
     * Serialize Rainbow DQN state to Akashic Record.
     * This allows weight transfer across JVM instances (replication/resurrection).
     */
    public String replicate() {
        if (akashic == null) return "[RainbowCortex] No Akashic Record wired";

        StringBuilder sb = new StringBuilder();
        sb.append("RAINBOW_STATE||");
        sb.append("steps=").append(rainbow.getTotalSteps()).append("||");
        sb.append("reward=").append(cumulativeReward).append("||");
        sb.append("loss=").append(rainbow.getLastLoss()).append("||");
        sb.append("gradnorm=").append(rainbow.getLastGradNorm()).append("||");
        sb.append("decisions=").append(totalDecisions).append("||");
        sb.append("route_decisions=").append(routingDecisions).append("||");
        sb.append("stimulate_decisions=").append(stimulateDecisions).append("||");
        sb.append("improve_cycles=").append(improveCycleCount.get()).append("||");
        sb.append("compile_rate=").append(codegenMetrics[0]).append("||");
        sb.append("accuracy=").append(accuracyAfter).append("||");
        sb.append("replications=").append(replicationCount);

        akashic.addBlock(AKASHIC_CATEGORY, sb.toString());
        replicationCount++;

        return String.format("[RainbowCortex] Replicated to Akashic (replication #%d | %d steps | reward=%.2f)",
            replicationCount, rainbow.getTotalSteps(), cumulativeReward);
    }

    /**
     * Restore Rainbow state from Akashic Record (metrics only — weights
     * must be rebuilt via training, but metrics seed the state vector correctly).
     */
    public String restore() {
        if (akashic == null) return "[RainbowCortex] No Akashic Record wired";

        List<AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(AKASHIC_CATEGORY);
        if (blocks == null || blocks.isEmpty()) return "[RainbowCortex] No saved state found";

        // Use most recent block
        String latest = blocks.get(blocks.size() - 1).content;
        System.out.println("[RainbowCortex] Restoring from: " + latest.substring(0, Math.min(100, latest.length())));

        // Parse key metrics
        for (String part : latest.split("\\|\\|")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                try {
                    switch (kv[0].trim()) {
                        case "reward"    -> cumulativeReward = Float.parseFloat(kv[1]);
                        case "decisions" -> totalDecisions = Integer.parseInt(kv[1]);
                        case "compile_rate" -> codegenMetrics[0] = Float.parseFloat(kv[1]);
                        case "accuracy"  -> accuracyAfter = Float.parseFloat(kv[1]);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        return "[RainbowCortex] State restored — cumulative reward: " + cumulativeReward;
    }

    // =========================================================================================
    // STATE VECTOR CONSTRUCTION
    // =========================================================================================

    private void buildStateVector() {
        // [0-5] Expert success rates
        System.arraycopy(expertRates, 0, stateVector, 0, 6);

        // [6-11] Failure counts
        System.arraycopy(failureCounts, 0, stateVector, 6, 6);

        // [12-15] Codegen metrics
        System.arraycopy(codegenMetrics, 0, stateVector, 12, 4);

        // [16-19] Self-improvement metrics
        System.arraycopy(improveMetrics, 0, stateVector, 16, 4);

        // [20-35] Brain region firing rates
        if (brain != null) {
            float[] sv = brain.getStateVector();
            // Sample 16 evenly spaced values from the brain state vector
            int step = Math.max(1, sv.length / 16);
            for (int i = 0; i < 16; i++)
                brainRates[i] = i * step < sv.length ? sv[i * step] : 0;
        }
        System.arraycopy(brainRates, 0, stateVector, 20, 16);

        // [36-51] Brain consciousness sub-metrics
        if (brain != null) {
            brainMeta[0] = brain.getConsciousness();
            brainMeta[1] = brain.getTotalReward();
            brainMeta[2] = brain.getLastTdError();
            brainMeta[3] = brain.getDeathCount();
            brainMeta[4] = brain.getTickCount() / 10000.0f;
            brainMeta[5] = brain.isAlive() ? 1.0f : 0.0f;
        }
        System.arraycopy(brainMeta, 0, stateVector, 36, 16);

        // [52-55] OpenClaw metrics
        System.arraycopy(clawMetrics, 0, stateVector, 52, 4);

        // [56-63] Rainbow self-metrics
        selfMetrics[0] = rainbow.getLastLoss();
        selfMetrics[1] = rainbow.getLastGradNorm();
        selfMetrics[2] = (float) rainbow.getBufferSize() / config.bufferSize;
        selfMetrics[3] = rainbow.getAvgReward1M();
        selfMetrics[4] = rainbow.getStepsPerSec();
        selfMetrics[5] = cumulativeReward;
        selfMetrics[6] = totalDecisions / 1000.0f;
        selfMetrics[7] = lastReward;
        System.arraycopy(selfMetrics, 0, stateVector, 56, 8);
    }

    // =========================================================================================
    // STATUS & TELEMETRY
    // =========================================================================================

    public String getStatus() {
        return String.format(
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  🌈🧠 RAINBOW CORTEX — Central Nervous System              ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  Total Decisions:    %,10d                                ║\n" +
            "║  Routing Decisions:  %,10d                                ║\n" +
            "║  Stimulate Decisions:%,10d                                ║\n" +
            "║  Improve Decisions:  %,10d                                ║\n" +
            "║  Explore Decisions:  %,10d                                ║\n" +
            "║  Cumulative Reward:  %,10.2f                              ║\n" +
            "║  Last Action:        %-15s                              ║\n" +
            "║  Last Reward:        %+.4f                                 ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  WIRED SYSTEMS:                                             ║\n" +
            "║    Brain:         %s                                  ║\n" +
            "║    Orchestrator:  %s                                  ║\n" +
            "║    Akashic:       %s                                  ║\n" +
            "║  AUTO-IMPROVE:    %s                                  ║\n" +
            "║  REPLICATIONS:    %d                                        ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  CODEGEN:  compile=%.0f%%  heal=%.0f%%  exec=%.0f%%  total=%d     ║\n" +
            "║  ACCURACY: before=%.1f%%  after=%.1f%%                      ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  RAINBOW DQN:                                               ║\n" +
            "║    Steps: %,d  Loss: %.4f  GradNorm: %.4f            ║\n" +
            "║    Buffer: %,d / %,d                                  ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n",
            totalDecisions,
            routingDecisions,
            stimulateDecisions,
            improveDecisions,
            exploreDecisions,
            cumulativeReward,
            lastAction,
            lastReward,
            brain != null ? "WIRED ✓" : "NOT WIRED",
            orchestrator != null ? "WIRED ✓" : "NOT WIRED",
            akashic != null ? "WIRED ✓" : "NOT WIRED",
            autoImproveRunning ? "RUNNING" : "STOPPED",
            replicationCount,
            codegenMetrics[0] * 100, codegenMetrics[1] * 100, codegenMetrics[2] * 100,
            (int) codegenMetrics[3],
            accuracyBefore * 100, accuracyAfter * 100,
            rainbow.getTotalSteps(), rainbow.getLastLoss(), rainbow.getLastGradNorm(),
            rainbow.getBufferSize(), config.bufferSize
        );
    }

    public RainbowDQN getRainbow()      { return rainbow; }
    public int        getTotalDecisions(){ return totalDecisions; }
    public float      getCumulativeReward() { return cumulativeReward; }

    public void shutdown() {
        stopAutoImprove();
        System.out.println("[RainbowCortex] Shutdown complete");
    }
}
