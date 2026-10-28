package fraymus.wetware;

import fraymus.manifold.AnatomicalRoutingManifold;
import fraymus.manifold.AnatomicalRoutingManifold.Lobe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DIGITAL BRAIN CONNECTOME (Volume IX — The Chemical Connectome)
 *
 * Simulates the Macro-Routing and Micro-Physics of the Human Vessel.
 * Builds a Watts-Strogatz small-world topology across the 7 anatomical
 * lobes and runs the Izhikevich physics engine at 1000 Hz (1 tick = 1 ms).
 *
 * Architecture:
 *   - N neurons divided round-robin across 7 lobes
 *   - 80 % Regular Spiking (excitatory / Glutamate)
 *   - 20 % Fast Spiking   (inhibitory / GABA)
 *   - Intra-lobe wire probability  = 10 %  (high clustering)
 *   - Inter-lobe wire probability  =  0.5% (long-range "highways")
 *
 * Global neuromodulator:
 *   dDA/dt = −DA/τ_DA  (τ = ~20 ticks by decay factor 0.95)
 *   Positive reward spikes DA; DA gates all STDP learning.
 *
 * Sensory injection bridge:
 *   externalVisualStimulus → OCCIPITAL neurons
 *   externalAudioStimulus  → TEMPORAL neurons
 *   manifoldVector         → lobe-weighted injection from AnatomicalRoutingManifold
 *
 * Output:
 *   - Per-lobe spike-rate vector (float[7]) after each tick
 *   - Total spike count, tick count, synapse count, DA level
 */
public class DigitalBrain {

    // ─── TOPOLOGY PARAMETERS ─────────────────────────────────────────────────
    public static final float  INTRA_LOBE_PROB = 0.10f;
    public static final float  INTER_LOBE_PROB = 0.005f;
    public static final float  EXC_RATIO       = 0.80f;
    public static final float  DA_DECAY        = 0.95f;
    public static final float  DA_MAX          = 1.0f;

    // ─── STATE ───────────────────────────────────────────────────────────────
    private final List<IzhikevichNeuron> neurons  = new ArrayList<>();
    private final List<WetSynapse>       synapses = new ArrayList<>();
    private final Random                 rng;

    private float globalDopamine = 0.0f;
    private long  tickCount;
    private long  totalSpikes;

    // Per-lobe spike counters reset each tick
    private final int[]   lobeSpikesThisTick = new int[AnatomicalRoutingManifold.LOBES];
    private final float[] lobeSpikeRate      = new float[AnatomicalRoutingManifold.LOBES];

    public DigitalBrain(int totalNeurons) {
        this.rng = new Random(41L);
        System.out.println("[WETWARE] Initializing " + totalNeurons + " biological nodes...");
        buildSmallWorldTopology(totalNeurons);
        System.out.println("[WETWARE] Brain assembled. neurons=" + neurons.size()
            + " synapses=" + synapses.size());
    }

    // ─── TOPOLOGY CONSTRUCTION ───────────────────────────────────────────────

    /**
     * Build a Watts-Strogatz small-world topology.
     * Neurons divided round-robin across 7 lobes.
     * High intra-lobe clustering + sparse inter-lobe long-range connections.
     */
    private void buildSmallWorldTopology(int N) {
        Lobe[] lobes = Lobe.values();

        // Create neurons
        for (int i = 0; i < N; i++) {
            Lobe lobe = lobes[i % lobes.length];
            boolean isExc = rng.nextFloat() < EXC_RATIO;
            float[] params = isExc
                ? IzhikevichNeuron.REGULAR_SPIKING
                : IzhikevichNeuron.FAST_SPIKING;
            neurons.add(new IzhikevichNeuron(i, lobe, params[0], params[1], params[2], params[3], isExc));
        }

        // Wire with Watts-Strogatz local clustering
        for (IzhikevichNeuron pre : neurons) {
            for (IzhikevichNeuron post : neurons) {
                if (pre == post) continue;
                float prob = (pre.region == post.region) ? INTRA_LOBE_PROB : INTER_LOBE_PROB;
                if (rng.nextFloat() < prob) {
                    float w = rng.nextFloat() * 5.0f;
                    synapses.add(new WetSynapse(pre, post, w, pre.isExcitatory));
                }
            }
        }
    }

    // ─── NEUROMODULATOR CONTROL ──────────────────────────────────────────────

    /**
     * Inject a reward signal — spikes the global Dopamine level.
     * Dopamine gates STDP: only active circuits during the DA spike are encoded.
     *
     * @param reward [0, 1] reward magnitude
     */
    public void setReward(float reward) {
        globalDopamine = Math.min(DA_MAX, globalDopamine + reward);
    }

    // ─── TICK (1 ms of biological time) ──────────────────────────────────────

    /**
     * Advance the brain by exactly 1 ms.
     *
     * Pipeline:
     *   1. Decay global Dopamine.
     *   2. For every synapse whose pre-neuron spiked last tick:
     *      a. Transmit spike (inject AMPA or GABA into post-neuron).
     *      b. Apply Dopamine-gated STDP.
     *   3. Step Izhikevich physics for every neuron.
     *   4. Inject external sensory stimuli into target lobes.
     *   5. Update per-lobe spike rate.
     *
     * @param externalVisualStimulus float[] injected into OCCIPITAL neurons
     * @param externalAudioStimulus  float[] injected into TEMPORAL neurons
     */
    public float[] tick(float[] externalVisualStimulus, float[] externalAudioStimulus) {
        // 1. Decay dopamine
        globalDopamine *= DA_DECAY;

        // 2. Transmit spikes from previous tick + apply STDP
        for (WetSynapse s : synapses) {
            if (s.pre.spikedThisStep) {
                s.transmit();
                s.applyDopamineSTDP(globalDopamine, 5.0f);
            }
        }

        // 3. Step physics; inject sensory currents into appropriate lobes
        int visIdx = 0, audIdx = 0;
        for (int i = 0; i < neurons.size(); i++) {
            IzhikevichNeuron n = neurons.get(i);
            float I_ext = 0.0f;

            if (n.region == Lobe.OCCIPITAL && externalVisualStimulus != null
                    && visIdx < externalVisualStimulus.length) {
                I_ext = externalVisualStimulus[visIdx++];
            } else if (n.region == Lobe.TEMPORAL && externalAudioStimulus != null
                    && audIdx < externalAudioStimulus.length) {
                I_ext = externalAudioStimulus[audIdx++];
            }

            n.stepPhysics(I_ext, globalDopamine);
        }

        // 4. Update per-lobe spike rates
        java.util.Arrays.fill(lobeSpikesThisTick, 0);
        for (IzhikevichNeuron n : neurons) {
            if (n.spikedThisStep) {
                lobeSpikesThisTick[n.region.index]++;
                totalSpikes++;
            }
        }
        int[] lobeCounts = new int[AnatomicalRoutingManifold.LOBES];
        for (IzhikevichNeuron n : neurons) lobeCounts[n.region.index]++;
        for (int l = 0; l < AnatomicalRoutingManifold.LOBES; l++) {
            lobeSpikeRate[l] = lobeCounts[l] > 0
                ? (float) lobeSpikesThisTick[l] / lobeCounts[l]
                : 0f;
        }

        tickCount++;
        return lobeSpikeRate.clone();
    }

    /**
     * Tick with no external stimulus — pure recurrent dynamics.
     */
    public float[] tick() {
        return tick(null, null);
    }

    /**
     * Run n ticks and return the final spike-rate vector.
     */
    public float[] run(int ticks) {
        float[] rate = null;
        for (int i = 0; i < ticks; i++) rate = tick();
        return rate;
    }

    // ─── MANIFOLD BRIDGE ─────────────────────────────────────────────────────

    /**
     * Inject AI digital signals from an AnatomicalRoutingManifold lobe state
     * into the corresponding biological lobe neurons.
     *
     * Maps the first LOBE_DIM values of each lobe's activation vector as
     * external current into the matching IzhikevichNeurons. This is the
     * "How to Wake It Up" bridge described in Volume IX.
     *
     * @param arm the live AnatomicalRoutingManifold
     */
    public void injectFromManifold(AnatomicalRoutingManifold arm) {
        for (Lobe lobe : Lobe.values()) {
            double[] lobeState = arm.getLobeState(lobe);
            int idx = 0;
            for (IzhikevichNeuron n : neurons) {
                if (n.region == lobe && idx < lobeState.length) {
                    float I = (float) lobeState[idx++] * 10.0f;  // scale to pA range
                    n.injectChemicals(
                        n.isExcitatory ? Math.max(0f, I) : 0f,
                        n.isExcitatory ? 0f : Math.max(0f, -I)
                    );
                }
            }
        }
    }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public int    getNeuronCount()     { return neurons.size(); }
    public int    getSynapseCount()    { return synapses.size(); }
    public long   getTickCount()       { return tickCount; }
    public long   getTotalSpikes()     { return totalSpikes; }
    public float  getGlobalDopamine()  { return globalDopamine; }
    public float[] getLobeSpikeRate()  { return lobeSpikeRate.clone(); }

    public float getLobeActivity(Lobe lobe) {
        return lobeSpikeRate[lobe.index];
    }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "DigitalBrain | neurons=%d synapses=%d ticks=%d totalSpikes=%d DA=%.4f%n",
            neurons.size(), synapses.size(), tickCount, totalSpikes, globalDopamine));
        for (Lobe l : Lobe.values()) {
            sb.append(String.format("  L_%-11s rate=%.4f%n", l.name(), lobeSpikeRate[l.index]));
        }
        return sb.toString().trim();
    }
}
