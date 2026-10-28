package fraymus.biology.learning;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * HIPPOCAMPAL STDP ENGINE (Spike-Timing-Dependent Plasticity)
 * Real-time biological learning. Alters the 64-bit semantic addresses of neurons
 * based on the temporal proximity of their action potentials.
 *
 * LTP (Long-Term Potentiation): "Cells that fire together, wire together."
 *   - If Neuron A fires within 20ms before Neuron B, their addresses are pulled closer.
 *
 * LTD (Long-Term Depression): "Cells that lose sync, lose their link."
 *   - If a cell goes idle at the plasticity boundary, its address drifts away.
 *
 * This is Semantic Plasticity — no physical synaptic weights.
 * The 64-bit neuralAddress IS the synapse. STDP mutates the DNA of the cell.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class STDP_Engine {

    // The maximum time window (in milliseconds) for synapses to strengthen or weaken
    private static final int PLASTICITY_WINDOW_MS = 20;

    // Tracks the exact millisecond each neuron last fired
    private final int[] lastSpikeTime;

    // Reference to the brain's hyper-dimensional routing addresses (shared array)
    private final long[] neuralAddresses;

    // Statistics (AtomicLong for thread-safe parallel increments)
    private final AtomicLong ltpEvents = new AtomicLong();  // Long-Term Potentiation (strengthening)
    private final AtomicLong ltdEvents = new AtomicLong();  // Long-Term Depression (weakening)
    private long totalPlasticityTicks = 0;

    public STDP_Engine(int totalNeurons, long[] manifoldAddresses) {
        System.out.println("   [STDP] Hippocampal plasticity engine ONLINE.");
        System.out.println("   [STDP] Window: " + PLASTICITY_WINDOW_MS + "ms | Mode: Semantic Address Mutation");
        this.lastSpikeTime = new int[totalNeurons];
        this.neuralAddresses = manifoldAddresses;

        // Initialize all to deeply negative time to prevent false associations at boot
        for (int i = 0; i < totalNeurons; i++) lastSpikeTime[i] = -1000;
    }

    /**
     * THE LEARNING TICK
     * Called every millisecond. Evaluates causality between connected lobes.
     *
     * @param currentTime   The current tick of the biological clock
     * @param firedThisTick Array of booleans indicating which neurons spiked
     * @param regionStart   Start index of the learning region (e.g., Hippocampus)
     * @param regionEnd     End index of the learning region
     */
    public void processPlasticity(int currentTime, boolean[] firedThisTick, int regionStart, int regionEnd) {
        totalPlasticityTicks++;

        IntStream.range(regionStart, regionEnd).parallel().forEach(i -> {
            if (firedThisTick[i]) {

                // STDP EVALUATION: When this cell fires, look backward in time
                // to see who "caused" it to fire
                int timeSinceLastSpike = currentTime - lastSpikeTime[i];

                if (timeSinceLastSpike < PLASTICITY_WINDOW_MS && timeSinceLastSpike > 0) {
                    // CAUSALITY DETECTED: A recent spike contributed to this one.
                    // LTP: Mutate the cell's address to cluster with its causal neighbors.
                    alignAddress(i);
                    ltpEvents.incrementAndGet();
                }

                // Mark current firing time
                lastSpikeTime[i] = currentTime;

            } else {
                // LONG-TERM DEPRESSION (LTD)
                // If a cell fired recently but isn't firing now while the network is active,
                // drift its address away to forget useless noise.
                int idleTime = currentTime - lastSpikeTime[i];
                if (idleTime == PLASTICITY_WINDOW_MS) {
                    divergeAddress(i);
                    ltdEvents.incrementAndGet();
                }
            }
        });
    }

    /**
     * LONG-TERM POTENTIATION (LTP) — "Wiring Together"
     * Zeros out a random entropy bit in the 64-bit address.
     * This reduces Hamming distance to nearby neurons, creating denser regional clusters.
     * The neuron's address converges toward its causal neighborhood.
     */
    private void alignAddress(int neuronIndex) {
        long address = neuralAddresses[neuronIndex];

        // Pick a random bit (0-63) and clear it to reduce entropy
        int bitToFlip = ThreadLocalRandom.current().nextInt(64);
        long mask = ~(1L << bitToFlip);
        neuralAddresses[neuronIndex] = address & mask;
    }

    /**
     * LONG-TERM DEPRESSION (LTD) — "Losing the Link"
     * XOR flips a random bit, increasing Hamming distance.
     * The neuron drifts away in the semantic space (forgetting).
     */
    private void divergeAddress(int neuronIndex) {
        long address = neuralAddresses[neuronIndex];

        // XOR flips the bit, increasing entropy
        int bitToFlip = ThreadLocalRandom.current().nextInt(64);
        long mask = (1L << bitToFlip);
        neuralAddresses[neuronIndex] = address ^ mask;
    }

    /**
     * TARGETED LTP: Align a post-synaptic neuron's address toward a specific pre-synaptic source.
     * Flips one bit of the post address to match the pre address (directional convergence).
     */
    public void alignToward(int postIdx, int preIdx) {
        long diff = neuralAddresses[preIdx] ^ neuralAddresses[postIdx];
        if (diff != 0) {
            int bitToFlip = Long.numberOfTrailingZeros(diff);
            neuralAddresses[postIdx] ^= (1L << bitToFlip);
            ltpEvents.incrementAndGet();
        }
    }

    /**
     * TARGETED LTD: Push a post-synaptic neuron's address away from a specific pre-synaptic source.
     * Flips one bit of the post address to diverge from the pre address (anti-causal weakening).
     */
    public void divergeFrom(int postIdx, int preIdx) {
        long same = ~(neuralAddresses[preIdx] ^ neuralAddresses[postIdx]);
        if (same != 0) {
            int bitToFlip = Long.numberOfTrailingZeros(same);
            neuralAddresses[postIdx] ^= (1L << bitToFlip);
            ltdEvents.incrementAndGet();
        }
    }

    // --- State ---
    public long getLtpEvents() { return ltpEvents.get(); }
    public long getLtdEvents() { return ltdEvents.get(); }
    public long getTotalPlasticityTicks() { return totalPlasticityTicks; }

    public String getStatus() {
        return String.format(
            "STDP: %d ticks | LTP=%d (strengthened) | LTD=%d (weakened) | ratio=%.3f",
            totalPlasticityTicks, ltpEvents.get(), ltdEvents.get(),
            ltpEvents.get() + ltdEvents.get() > 0 ? (double) ltpEvents.get() / (ltpEvents.get() + ltdEvents.get()) : 0.0);
    }
}
