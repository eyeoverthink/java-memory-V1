package fraymus.wetware;

/**
 * DOPAMINE-GATED SYNAPSE (Section XXX — The Molecule of Desire)
 *
 * Connects two IzhikevichNeurons and implements Spike-Timing-Dependent
 * Plasticity (STDP) gated by the global Dopamine concentration.
 *
 * Standard STDP (causal window):
 *   Δw_pre→post(Δt) = A+ · exp(−Δt / τ+)   if Δt > 0  (pre before post → LTP)
 *   Δw_pre→post(Δt) = −A− · exp( Δt / τ−)   if Δt < 0  (post before pre → LTD)
 *
 * Dopamine gating (Section XXX):
 *   dDA/dt = −DA/τ_DA + Reward(t)
 *   Δw = DA(t) × ( c · STDP(Δt) )
 *
 * If DA = 0 the synapse processes but does NOT learn.
 * If DA spikes, the active circuit is permanently etched into the topology.
 * This is the mathematical definition of motivation.
 *
 * Biological hard limits:  w ∈ [0, 10]  (synaptic clipping)
 * Chemical type:
 *   isExcitatory = true  → Glutamate (AMPA)
 *   isExcitatory = false → GABA (inhibitory)
 */
public class WetSynapse {

    // ─── STDP PARAMETERS ─────────────────────────────────────────────────────
    public static final float A_PLUS    =  0.10f;  // LTP amplitude
    public static final float A_MINUS   =  0.12f;  // LTD amplitude
    public static final float TAU_PLUS  = 20.0f;   // ms — causal window
    public static final float TAU_MINUS = 20.0f;   // ms — anti-causal window

    // ─── WEIGHT BOUNDS ────────────────────────────────────────────────────────
    public static final float W_MAX = 10.0f;
    public static final float W_MIN =  0.0f;

    // ─── ENDPOINTS ───────────────────────────────────────────────────────────
    public final IzhikevichNeuron pre;
    public final IzhikevichNeuron post;
    public final boolean          isExcitatory;

    // ─── STATE ───────────────────────────────────────────────────────────────
    public float weight;
    private long  stdpUpdates;
    private double totalWeightDelta;

    public WetSynapse(IzhikevichNeuron pre, IzhikevichNeuron post,
                      float initialWeight, boolean isExcitatory) {
        this.pre          = pre;
        this.post         = post;
        this.weight       = Math.max(W_MIN, Math.min(W_MAX, initialWeight));
        this.isExcitatory = isExcitatory;
    }

    // ─── SPIKE TRANSMISSION ──────────────────────────────────────────────────

    /**
     * Transmit a pre-synaptic spike to the post-synaptic neuron by injecting
     * the appropriate chemical conductance scaled by synaptic weight.
     */
    public void transmit() {
        if (isExcitatory) {
            post.injectChemicals(weight, 0f);   // Glutamate → AMPA
        } else {
            post.injectChemicals(0f, weight);   // GABA → inhibitory
        }
    }

    // ─── DOPAMINE-GATED STDP ─────────────────────────────────────────────────

    /**
     * Apply Dopamine-gated STDP weight update.
     *
     *   Δw = DA(t) × ( A± · exp(−|Δt| / τ±) )
     *
     * @param dopamineLevel   global DA concentration [0, 1]
     * @param timeDifferenceMs  t_pre − t_post in ms (positive → causal LTP)
     */
    public void applyDopamineSTDP(float dopamineLevel, float timeDifferenceMs) {
        if (dopamineLevel <= 0.01f) return;  // No reward → no learning

        float deltaW;
        if (timeDifferenceMs > 0) {
            // Pre fired before post (causal) → Long-Term Potentiation
            deltaW = (float) (A_PLUS  * Math.exp(-timeDifferenceMs / TAU_PLUS));
        } else {
            // Post fired before pre (anti-causal) → Long-Term Depression
            deltaW = (float) (-A_MINUS * Math.exp(timeDifferenceMs / TAU_MINUS));
        }

        float dw = deltaW * dopamineLevel;
        weight += dw;
        weight  = Math.max(W_MIN, Math.min(W_MAX, weight));

        totalWeightDelta += Math.abs(dw);
        stdpUpdates++;
    }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public long   getStdpUpdates()      { return stdpUpdates; }
    public double getTotalWeightDelta() { return totalWeightDelta; }

    @Override
    public String toString() {
        return String.format("WetSynapse[%d→%d %s w=%.3f]",
            pre.id, post.id, isExcitatory ? "EXC" : "INH", weight);
    }
}
