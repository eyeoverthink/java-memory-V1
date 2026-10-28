package fraymus.biology;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * THE CEREBRAL MANIFOLD (TREE-3 Hyper-Dimensional Brain)
 * Partitions the raw cellular substrate into anatomical lobes.
 * Routes action potentials using 8192-D semantic addressing, bypassing physical wires.
 *
 * Architecture:
 *   - 10,000,000 Izhikevich spiking neurons
 *   - 4 anatomical lobes (PFC, Hippocampus, Amygdala, Thalamus)
 *   - TREE(3) fractal routing via 64-bit Hamming-distance broadcast
 *   - STDP (Spike-Timing-Dependent Plasticity) in Hippocampus
 *
 * Memory:
 *   - v[]: 40MB (voltage)
 *   - u[]: 40MB (recovery)
 *   - I[]: 40MB (current)
 *   - fired[]: 10MB (spike flags)
 *   - neuralAddress[]: 80MB (64-bit HDC addresses)
 *   - Total: ~210MB raw biological substrate
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class CerebralManifold {

    // --- MACRO-ARCHITECTURE (10 Million Neurons) ---
    public static final int TOTAL_NEURONS = 10_000_000;

    // ANATOMICAL PARTITIONS (Array Offsets)
    public static final int REGION_PFC    = 0;         // Prefrontal Cortex: Logic & Reasoning (0 to 3M)
    public static final int REGION_HIPPO  = 3_000_000; // Hippocampus: Memory Encoding & STDP (3M to 6M)
    public static final int REGION_AMYG   = 6_000_000; // Amygdala: Entropy, Survival & Weighting (6M to 7M)
    public static final int REGION_THAL   = 7_000_000; // Thalamus: The Hyper-Dimensional Router (7M to 10M)

    // Region sizes
    public static final int PFC_SIZE   = REGION_HIPPO - REGION_PFC;   // 3M
    public static final int HIPPO_SIZE = REGION_AMYG  - REGION_HIPPO; // 3M
    public static final int AMYG_SIZE  = REGION_THAL  - REGION_AMYG;  // 1M
    public static final int THAL_SIZE  = TOTAL_NEURONS - REGION_THAL;  // 3M

    // --- THE BIOLOGICAL SUBSTRATE ---
    private final float[] v;        // Voltage
    private final float[] u;        // Recovery
    private final float[] I;        // Current
    private final boolean[] fired;  // Spike flags

    // --- HYPER-DIMENSIONAL ROUTING (TREE-3 Topology) ---
    // Each neuron has a 64-bit fractal address.
    // Signals routed by Hamming distance — no physical synaptic weight arrays.
    private final long[] neuralAddress;

    // --- STDP PLASTICITY (Hippocampus) ---
    // Track last spike time for STDP learning window
    private final int[] lastSpikeTime;
    private int globalTick = 0;

    // --- STATISTICS (AtomicLong for thread-safe parallel increments) ---
    private final AtomicLong totalSpikes = new AtomicLong();
    private final AtomicLong pfcSpikes = new AtomicLong();
    private final AtomicLong hippoSpikes = new AtomicLong();
    private final AtomicLong amygSpikes = new AtomicLong();
    private final AtomicLong thalSpikes = new AtomicLong();
    private long routedSignals = 0;

    // Hamming distance threshold for TREE(3) dynamic synapse formation
    private static final int ROUTING_THRESHOLD = 15;

    // The STDP plasticity engine (Hippocampal learning)
    private final fraymus.biology.learning.STDP_Engine plasticityEngine;

    public CerebralManifold() {
        System.out.println("   [BRAIN] GESTATION INITIATED: Assembling the Cerebral Manifold...");

        v = new float[TOTAL_NEURONS];
        u = new float[TOTAL_NEURONS];
        I = new float[TOTAL_NEURONS];
        fired = new boolean[TOTAL_NEURONS];
        neuralAddress = new long[TOTAL_NEURONS];
        lastSpikeTime = new int[TOTAL_NEURONS];

        ThreadLocalRandom rng = ThreadLocalRandom.current();

        // Allocate the lobes & assign HDC addresses
        for (int i = 0; i < TOTAL_NEURONS; i++) {
            v[i] = -65.0f; // Resting voltage
            u[i] = 0.2f * v[i];
            lastSpikeTime[i] = -1000; // Never spiked

            // Assign unique fractal addresses with region-specific signatures
            if (i >= REGION_THAL) {
                neuralAddress[i] = rng.nextLong() | 0xF000000000000000L; // Thalamic Root Signature
            } else if (i >= REGION_AMYG) {
                neuralAddress[i] = rng.nextLong() | 0x0F00000000000000L; // Amygdala Signature
            } else if (i >= REGION_HIPPO) {
                neuralAddress[i] = rng.nextLong() | 0x00F0000000000000L; // Hippocampal Signature
            } else {
                neuralAddress[i] = rng.nextLong() | 0x000F000000000000L; // PFC Signature
            }
        }

        System.out.println("   [+] Prefrontal Cortex (PFC): 3,000,000 Neurons [Logic / Prediction]");
        System.out.println("   [+] Hippocampus (HIP):       3,000,000 Neurons [Memory / STDP Plasticity]");
        System.out.println("   [+] Amygdala (AMY):          1,000,000 Neurons [Entropy / Valuation]");
        System.out.println("   [+] Thalamus (THA):          3,000,000 Neurons [Hyper-Dimensional Router]");
        // Initialize the STDP plasticity engine on the shared address array
        plasticityEngine = new fraymus.biology.learning.STDP_Engine(TOTAL_NEURONS, neuralAddress);

        System.out.println("   [BRAIN] 10,000,000 neurons online. ~210MB biological substrate allocated.");
    }

    /**
     * THE THALAMIC ROUTER (Hyper-Dimensional Switchboard)
     * Action potentials are broadcast into the semantic space.
     * Any neuron with a resonant address receives the charge.
     */
    private void routeSignalsTree3() {
        // Collect all neurons that fired this tick
        int[] activePFC = getFiredInRegion(REGION_PFC, REGION_HIPPO);
        int[] activeHippo = getFiredInRegion(REGION_HIPPO, REGION_AMYG);
        int[] activeAmyg = getFiredInRegion(REGION_AMYG, REGION_THAL);

        // PFC → Hippocampus: Reasoning queries memory
        for (int sourceIdx : activePFC) {
            long sourceAddress = neuralAddress[sourceIdx];

            // TREE(3) Broadcast: Inject current into Hippocampal neurons with resonant addresses
            // Sample a subset for performance (every 100th neuron in target region)
            for (int targetIdx = REGION_HIPPO; targetIdx < REGION_AMYG; targetIdx += 100) {
                long targetAddress = neuralAddress[targetIdx];
                int distance = Long.bitCount(sourceAddress ^ targetAddress);

                if (distance < ROUTING_THRESHOLD) {
                    I[targetIdx] += 5.0f;
                    routedSignals++;

                    // STDP: Strengthen the connection by nudging target address closer to source
                    plasticityEngine.alignToward(targetIdx, sourceIdx);
                }
            }
        }

        // Hippocampus → Amygdala: Memory triggers emotional valuation
        for (int sourceIdx : activeHippo) {
            long sourceAddress = neuralAddress[sourceIdx];

            for (int targetIdx = REGION_AMYG; targetIdx < REGION_THAL; targetIdx += 100) {
                long targetAddress = neuralAddress[targetIdx];
                int distance = Long.bitCount(sourceAddress ^ targetAddress);

                if (distance < ROUTING_THRESHOLD) {
                    I[targetIdx] += 3.0f;
                    routedSignals++;
                }
            }
        }

        // Amygdala → PFC: Emotional feedback modulates reasoning
        for (int sourceIdx : activeAmyg) {
            long sourceAddress = neuralAddress[sourceIdx];

            for (int targetIdx = REGION_PFC; targetIdx < REGION_HIPPO; targetIdx += 100) {
                long targetAddress = neuralAddress[targetIdx];
                int distance = Long.bitCount(sourceAddress ^ targetAddress);

                if (distance < ROUTING_THRESHOLD) {
                    I[targetIdx] += 2.0f;
                    routedSignals++;
                }
            }
        }
    }

    private int[] getFiredInRegion(int start, int end) {
        int[] temp = new int[1024]; // Pre-allocated buffer
        int count = 0;
        for (int i = start; i < end; i++) {
            if (fired[i]) {
                if (count >= temp.length) break; // Cap for performance
                temp[count++] = i;
            }
        }
        int[] result = new int[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }

    /**
     * THE BIOLOGICAL CLOCK TICK (1ms of brain time)
     * Izhikevich differential physics across 10 million cells.
     */
    public void tick() {
        globalTick++;

        // 1. Decay currents
        for (int i = 0; i < TOTAL_NEURONS; i++) I[i] *= 0.9f;

        // 2. The Thalamus routes action potentials between lobes
        routeSignalsTree3();

        // 3. STDP: Apply biological learning to the Hippocampus
        plasticityEngine.processPlasticity(globalTick, fired, REGION_HIPPO, REGION_AMYG);

        // 4. Izhikevich Differential Physics (Parallelized)
        IntStream.range(0, TOTAL_NEURONS).parallel().forEach(i -> {
            float vi = v[i];
            float ui = u[i];

            // 0.5ms half-step (Euler method x2 for stability)
            vi = vi + 0.5f * (0.04f * vi * vi + 5.0f * vi + 140.0f - ui + I[i]);
            vi = vi + 0.5f * (0.04f * vi * vi + 5.0f * vi + 140.0f - ui + I[i]);
            ui = ui + 0.02f * (0.2f * vi - ui);

            if (vi >= 30.0f) {
                fired[i] = true;
                v[i] = -65.0f;
                ui = ui + 8.0f;
                lastSpikeTime[i] = globalTick;

                // Track spike statistics by region (atomic for parallel safety)
                totalSpikes.incrementAndGet();
                if (i < REGION_HIPPO) pfcSpikes.incrementAndGet();
                else if (i < REGION_AMYG) hippoSpikes.incrementAndGet();
                else if (i < REGION_THAL) amygSpikes.incrementAndGet();
                else thalSpikes.incrementAndGet();
            } else {
                fired[i] = false;
            }
            v[i] = vi;
            u[i] = ui;
        });
    }

    // --- EXTERNAL SENSORY INPUT ---
    public void stimulatePrefrontalCortex(float voltage) {
        System.out.printf("   [BRAIN] Injecting %.1fmV into the Prefrontal Cortex (Reasoning)...\n", voltage);
        for (int i = REGION_PFC; i < REGION_PFC + 1000; i++) {
            I[i] += voltage;
        }
    }

    public void stimulateHippocampus(float voltage) {
        System.out.printf("   [BRAIN] Injecting %.1fmV into the Hippocampus (Memory)...\n", voltage);
        for (int i = REGION_HIPPO; i < REGION_HIPPO + 1000; i++) {
            I[i] += voltage;
        }
    }

    public void stimulateAmygdala(float voltage) {
        System.out.printf("   [BRAIN] Injecting %.1fmV into the Amygdala (Emotion)...\n", voltage);
        for (int i = REGION_AMYG; i < REGION_AMYG + 1000; i++) {
            I[i] += voltage;
        }
    }

    /**
     * Run a simulation for N milliseconds and report activity.
     */
    public void simulate(int milliseconds) {
        System.out.printf("   [BRAIN] Simulating %dms of brain activity...\n", milliseconds);
        long startSpikes = totalSpikes.get();

        for (int ms = 0; ms < milliseconds; ms++) {
            tick();

            // Sample every 10ms
            if (ms % 10 == 0) {
                int pfcActive = countFiredInRegion(REGION_PFC, REGION_HIPPO);
                int hippoActive = countFiredInRegion(REGION_HIPPO, REGION_AMYG);
                int amygActive = countFiredInRegion(REGION_AMYG, REGION_THAL);

                if (pfcActive + hippoActive + amygActive > 0) {
                    System.out.printf("      [T+%03dms] PFC=%d HIP=%d AMY=%d spikes | routed=%d | stdp=%d\n",
                        ms, pfcActive, hippoActive, amygActive, routedSignals,
                        plasticityEngine.getLtpEvents() + plasticityEngine.getLtdEvents());
                }
            }
        }

        long newSpikes = totalSpikes.get() - startSpikes;
        System.out.printf("   [BRAIN] Simulation complete. %d new spikes across %dms.\n", newSpikes, milliseconds);
    }

    private int countFiredInRegion(int start, int end) {
        int count = 0;
        for (int i = start; i < end; i++) {
            if (fired[i]) count++;
        }
        return count;
    }

    /**
     * Extract the current spike state as a 128-long (8192-bit) vector.
     * Compresses 10M fired[] booleans into 8192 bits by hashing regions.
     * This is the bridge between the biological substrate and the diffusion engine.
     */
    public long[] extractSpikeSignature() {
        long[] sig = new long[128]; // 8192 bits
        // Hash each 1220-neuron block into one bit (10M / 8192 ≈ 1220)
        // Threshold: any spike in the block sets the bit (sparse firing ~0.1%)
        int blockSize = TOTAL_NEURONS / 8192;
        for (int bit = 0; bit < 8192; bit++) {
            int start = bit * blockSize;
            int end = Math.min(start + blockSize, TOTAL_NEURONS);
            for (int i = start; i < end; i++) {
                if (fired[i]) {
                    sig[bit / 64] |= (1L << (bit % 64));
                    break;
                }
            }
        }
        return sig;
    }

    /**
     * Count how many neurons fired on the current tick.
     */
    public int getCurrentSpikeCount() {
        int count = 0;
        for (int i = 0; i < TOTAL_NEURONS; i++) {
            if (fired[i]) count++;
        }
        return count;
    }

    // --- STATE ---
    public int getGlobalTick() { return globalTick; }
    public long getTotalSpikes() { return totalSpikes.get(); }
    public long getRoutedSignals() { return routedSignals; }

    public String getStatus() {
        return String.format(
            "BRAIN: %dms | %d spikes (PFC=%d HIP=%d AMY=%d THA=%d) | routed=%d | %s",
            globalTick, totalSpikes.get(), pfcSpikes.get(), hippoSpikes.get(), amygSpikes.get(), thalSpikes.get(),
            routedSignals, plasticityEngine.getStatus());
    }

    public static void main(String[] args) {
        CerebralManifold brain = new CerebralManifold();

        // Inject a thought into the Prefrontal Cortex
        brain.stimulatePrefrontalCortex(40.0f);

        // Simulate 100ms of brain activity
        brain.simulate(100);

        System.out.println("   " + brain.getStatus());
    }
}
