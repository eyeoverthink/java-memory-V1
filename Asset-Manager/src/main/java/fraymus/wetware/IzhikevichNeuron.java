package fraymus.wetware;

import fraymus.manifold.AnatomicalRoutingManifold.Lobe;

/**
 * BIOLOGICAL NEURON — IZHIKEVICH MODEL (Section XXIX — Synaptic Conductance)
 *
 * Simulates the exact electrical and chemical state of a single human brain
 * cell using the Izhikevich two-variable model, which reproduces all known
 * types of cortical neuron firing patterns at low computational cost.
 *
 * Membrane dynamics (Izhikevich 2003):
 *   dv/dt = 0.04v² + 5v + 140 − u + I
 *   du/dt = a(bv − u)
 *   if v ≥ 30 mV: v ← c,  u ← u + d  (spike reset)
 *
 * Chemical conductance (Section XXIX — AMPA/GABA kinetics):
 *   g_syn(t) = ḡ_syn · e^{-(t-t0)/τ}
 *   τ_AMPA ≈ 5 ms  (Glutamate, excitatory)
 *   τ_GABA ≈ 10 ms (GABA, inhibitory)
 *
 * Synaptic current:
 *   I_syn = g_AMPA(v − E_AMPA) + g_GABA(v − E_GABA)
 *   E_AMPA = 0 mV,  E_GABA = −80 mV
 *
 * Neuron presets:
 *   Regular Spiking   (RS)  — a=0.02 b=0.20 c=−65 d=8   (80 % of cortex)
 *   Fast Spiking      (FS)  — a=0.10 b=0.20 c=−65 d=2   (20 % inhibitory)
 *   Bursting          (IB)  — a=0.02 b=0.20 c=−55 d=4
 */
public class IzhikevichNeuron {

    // ─── NEURON PRESETS ──────────────────────────────────────────────────────
    public static final float[] REGULAR_SPIKING = { 0.02f,  0.20f, -65f, 8f  };
    public static final float[] FAST_SPIKING    = { 0.10f,  0.20f, -65f, 2f  };
    public static final float[] BURSTING        = { 0.02f,  0.20f, -55f, 4f  };

    // ─── REVERSAL POTENTIALS ─────────────────────────────────────────────────
    public static final float E_AMPA = 0.0f;    // mV — excitatory reversal
    public static final float E_GABA = -80.0f;  // mV — inhibitory reversal

    // ─── DECAY FACTORS (per 1 ms step) ───────────────────────────────────────
    public static final float DECAY_AMPA = 0.80f;  // τ ≈ 5 ms
    public static final float DECAY_GABA = 0.90f;  // τ ≈ 10 ms

    // ─── SPIKE THRESHOLD ─────────────────────────────────────────────────────
    public static final float SPIKE_THRESHOLD = 30.0f; // mV

    // ─── IDENTITY ────────────────────────────────────────────────────────────
    public final Lobe   region;
    public final int    id;
    public final boolean isExcitatory;

    // ─── IZHIKEVICH PARAMETERS ───────────────────────────────────────────────
    private final float a;  // time-scale of recovery u
    private final float b;  // sensitivity of u to v
    private final float c;  // post-spike reset voltage
    private final float d;  // post-spike recovery increment

    // ─── STATE VARIABLES ─────────────────────────────────────────────────────
    private float v;          // membrane voltage (mV)
    private float u;          // recovery variable

    // ─── CHEMICAL CONDUCTANCE ────────────────────────────────────────────────
    private float g_AMPA;     // excitatory conductance (Glutamate)
    private float g_GABA;     // inhibitory conductance (GABA)

    // ─── SPIKE TRACKING ──────────────────────────────────────────────────────
    public  boolean spikedThisStep;
    private long    totalSpikes;
    private float   lastSpikeTimeMs;
    private float   timeMs;

    public IzhikevichNeuron(int id, Lobe region, float a, float b, float c, float d, boolean isExcitatory) {
        this.id           = id;
        this.region       = region;
        this.a            = a;
        this.b            = b;
        this.c            = c;
        this.d            = d;
        this.isExcitatory = isExcitatory;
        this.v            = c;
        this.u            = b * v;
    }

    // ─── CHEMICAL INJECTION ──────────────────────────────────────────────────

    /**
     * Inject conductance from an arriving pre-synaptic spike.
     * @param ampaAmount  excitatory (Glutamate) conductance increment
     * @param gabaAmount  inhibitory (GABA) conductance increment
     */
    public void injectChemicals(float ampaAmount, float gabaAmount) {
        g_AMPA += ampaAmount;
        g_GABA += gabaAmount;
    }

    // ─── PHYSICS STEP (1 ms) ─────────────────────────────────────────────────

    /**
     * Run exactly 1 millisecond of biophysical simulation.
     *
     * 1. Decay chemical conductances (AMPA faster than GABA).
     * 2. Compute synaptic current: I_syn = g_AMPA(v−E_AMPA) + g_GABA(v−E_GABA)
     * 3. Apply Izhikevich differential equations.
     * 4. Check spike threshold; reset if fired.
     *
     * @param externalCurrent  I_ext injected by a BCI or sensory stimulus (pA)
     * @param dopamineLevel    global DA concentration [0,1] — modulates excitability
     */
    public void stepPhysics(float externalCurrent, float dopamineLevel) {
        timeMs += 1.0f;

        // 1. Decay chemical channels
        g_AMPA *= DECAY_AMPA;
        g_GABA *= DECAY_GABA;

        // 2. Synaptic current (Section XXIX)
        float I_syn = -(g_AMPA * (v - E_AMPA)) - (g_GABA * (v - E_GABA));

        // Dopamine modulates excitability: low DA = slightly suppressed baseline
        float daGain = 0.8f + 0.2f * Math.min(1.0f, dopamineLevel);
        float I_total = (externalCurrent + I_syn) * daGain;

        // 3. Izhikevich equations
        float dv = 0.04f * v * v + 5.0f * v + 140.0f - u + I_total;
        float du = a * (b * v - u);
        v += dv;
        u += du;

        // 4. Spike threshold
        if (v >= SPIKE_THRESHOLD) {
            spikedThisStep = true;
            lastSpikeTimeMs = timeMs;
            totalSpikes++;
            v  = c;
            u += d;
        } else {
            spikedThisStep = false;
        }
    }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public float getVoltage()        { return v; }
    public float getRecovery()       { return u; }
    public float getGampa()          { return g_AMPA; }
    public float getGgaba()          { return g_GABA; }
    public long  getTotalSpikes()    { return totalSpikes; }
    public float getLastSpikeTime()  { return lastSpikeTimeMs; }
    public float getTimeMs()         { return timeMs; }

    @Override
    public String toString() {
        return String.format("Neuron[%d %s %s v=%.1f spikes=%d]",
            id, region, isExcitatory ? "EXC" : "INH", v, totalSpikes);
    }
}
