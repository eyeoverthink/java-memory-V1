package fraymus.biology;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * 🧫 IN VITRO SUBSTRATE (The Petri Dish)
 * Simulates 100,000 Izhikevich Spiking Neurons using Data-Oriented Design.
 * No objects. Pure contiguous float arrays for maximum CPU L1/L2 Cache hits.
 *
 * Izhikevich Model:
 *   dv/dt = 0.04v^2 + 5v + 140 - u + I
 *   du/dt = a(bv - u)
 *   if v >= 30mV: v = c, u = u + d
 *
 * 80% Excitatory (Regular Spiking), 20% Inhibitory (Fast Spiking)
 * Each neuron connects to 100 random others via sparse synaptic wiring.
 */
public class InVitroSubstrate {

    public static final int NEURON_COUNT = 100_000;

    // --- THE BIOLOGICAL ARRAYS (Data-Oriented Design) ---
    // v: Membrane potential (voltage in mV)
    // u: Membrane recovery variable (potassium/sodium ion channel dynamics)
    private final float[] v = new float[NEURON_COUNT];
    private final float[] u = new float[NEURON_COUNT];

    // I: Input current (electrical stimulation)
    private final float[] I = new float[NEURON_COUNT];

    // Tracks which neurons fired in the current millisecond
    private final boolean[] fired = new boolean[NEURON_COUNT];

    // --- IZHIKEVICH CONSTANTS (Cortical Neuron DNA) ---
    // a: Time scale of recovery variable
    // b: Sensitivity of recovery variable
    // c: After-spike reset value of membrane potential
    // d: After-spike reset of recovery variable
    private final float[] a = new float[NEURON_COUNT];
    private final float[] b = new float[NEURON_COUNT];
    private final float[] c = new float[NEURON_COUNT];
    private final float[] d = new float[NEURON_COUNT];

    // --- SYNAPTIC WIRING (Sparse Matrix) ---
    // For the dish, each neuron randomly connects to 100 others
    private final int SYNAPSES_PER_NEURON = 100;
    private final int[] synapseTargets = new int[NEURON_COUNT * SYNAPSES_PER_NEURON];
    private final float[] synapseWeights = new float[NEURON_COUNT * SYNAPSES_PER_NEURON];

    // Statistics
    private long totalSpikes = 0;
    private int lastSpikeCount = 0;

    public InVitroSubstrate() {
        System.out.println("   [🧫] Preparing In Vitro Petri Dish...");
        System.out.println("   [🧫] Allocating 100,000 Spiking Neurons into contiguous RAM...");

        ThreadLocalRandom rng = ThreadLocalRandom.current();

        // 1. GENERATE NEURON DNA (80% Excitatory, 20% Inhibitory)
        for (int i = 0; i < NEURON_COUNT; i++) {
            float r = rng.nextFloat();
            if (i < NEURON_COUNT * 0.8) {
                // Excitatory Neurons (Regular Spiking)
                a[i] = 0.02f;
                b[i] = 0.2f;
                c[i] = -65.0f + (15 * (r * r));
                d[i] = 8.0f - (6 * (r * r));
            } else {
                // Inhibitory Neurons (Fast Spiking)
                a[i] = 0.02f + (0.08f * r);
                b[i] = 0.25f - (0.05f * r);
                c[i] = -65.0f;
                d[i] = 2.0f;
            }
            v[i] = -65.0f; // Resting potential is -65mV
            u[i] = b[i] * v[i];
        }

        // 2. GROW SYNAPSES (Random wiring for the dish)
        System.out.println("   [🧫] Growing 10,000,000 Synaptic connections...");
        for (int i = 0; i < NEURON_COUNT; i++) {
            int offset = i * SYNAPSES_PER_NEURON;
            boolean isExcitatory = (i < NEURON_COUNT * 0.8);

            for (int s = 0; s < SYNAPSES_PER_NEURON; s++) {
                synapseTargets[offset + s] = rng.nextInt(NEURON_COUNT);
                // Excitatory synapses add voltage (+), Inhibitory subtract voltage (-)
                synapseWeights[offset + s] = isExcitatory ? (rng.nextFloat() * 5.0f) : (rng.nextFloat() * -5.0f);
            }
        }
        System.out.println("   [+] Culture Stable. Ready for electrical stimulation.");
    }

    /**
     * THE BIOLOGICAL CLOCK TICK (1 Millisecond of real time)
     * Solves the differential equations for all 100,000 cells simultaneously.
     */
    public void tick() {
        // 1. RESET CURRENTS (Decay ambient electricity)
        Arrays.fill(I, 0.0f);

        // 2. APPLY AMBIENT THALAMIC NOISE (Spontaneous brain activity)
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < NEURON_COUNT; i++) {
            if (rng.nextFloat() < 0.01f) { // 1% chance per ms to receive a random jolt
                I[i] = (i < NEURON_COUNT * 0.8) ? 5.0f * rng.nextFloat() : 2.0f * rng.nextFloat();
            }
        }

        // 3. PROPAGATE SPIKES FROM PREVIOUS MILLISECOND
        for (int i = 0; i < NEURON_COUNT; i++) {
            if (fired[i]) {
                int offset = i * SYNAPSES_PER_NEURON;
                for (int s = 0; s < SYNAPSES_PER_NEURON; s++) {
                    int target = synapseTargets[offset + s];
                    I[target] += synapseWeights[offset + s]; // Inject neurotransmitters
                }
            }
        }

        // 4. SOLVE MEMBRANE PHYSICS (Parallelized)
        lastSpikeCount = 0;
        IntStream.range(0, NEURON_COUNT).parallel().forEach(i -> {
            float vi = v[i];
            float ui = u[i];

            // Izhikevich Differential Equation (Step 1: 0.5 ms)
            vi = vi + 0.5f * (0.04f * vi * vi + 5.0f * vi + 140.0f - ui + I[i]);
            // Izhikevich Differential Equation (Step 2: 0.5 ms)
            vi = vi + 0.5f * (0.04f * vi * vi + 5.0f * vi + 140.0f - ui + I[i]);
            // Recovery variable update
            ui = ui + a[i] * (b[i] * vi - ui);

            // Check for Action Potential (Spike at 30mV)
            if (vi >= 30.0f) {
                fired[i] = true;
                v[i] = c[i];       // Reset voltage
                u[i] = ui + d[i];  // Reset recovery
            } else {
                fired[i] = false;
                v[i] = vi;
                u[i] = ui;
            }
        });

        // Count spikes
        for (boolean b : fired) if (b) lastSpikeCount++;
        totalSpikes += lastSpikeCount;
    }

    /**
     * Inject external current into a specific neuron.
     */
    public void stimulate(int neuronIndex, float current) {
        if (neuronIndex >= 0 && neuronIndex < NEURON_COUNT) {
            I[neuronIndex] += current;
        }
    }

    /**
     * Inject current into a range of neurons (region stimulation).
     */
    public void stimulateRegion(int start, int end, float current) {
        for (int i = Math.max(0, start); i < Math.min(end, NEURON_COUNT); i++) {
            I[i] += current;
        }
    }

    /**
     * Renders a microscopic view of the culture.
     */
    public void observeDish(int msTime) {
        System.out.printf("   [T+%04d ms] Cellular Activity: %5d action potentials fired.\n", msTime, lastSpikeCount);
    }

    /**
     * Returns the fired state array (for external consumers like CerebralManifold).
     */
    public boolean[] getFired() { return fired; }

    /**
     * Returns the voltage array.
     */
    public float[] getVoltages() { return v; }

    /**
     * Returns the current array.
     */
    public float[] getCurrents() { return I; }

    public int getLastSpikeCount() { return lastSpikeCount; }
    public long getTotalSpikes() { return totalSpikes; }

    public static void main(String[] args) {
        InVitroSubstrate dish = new InVitroSubstrate();

        System.out.println("\n[!] INITIATING CORTICAL OBSERVATION...");
        for (int ms = 0; ms < 1000; ms++) { // Simulate 1 second of brain activity
            dish.tick();
            if (ms % 50 == 0) dish.observeDish(ms); // Sample every 50ms
        }
        System.out.println("\n[+] Total spikes in 1 second: " + dish.getTotalSpikes());
    }
}
