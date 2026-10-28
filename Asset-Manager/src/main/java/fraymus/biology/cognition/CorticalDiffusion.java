package fraymus.biology.cognition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * CORTICAL DIFFUSION ENGINE (The Semantic Sculptor)
 * "We do not build thoughts. We extract them from noise."
 *
 * Uses Hopfield-like Attractor Dynamics to collapse an 8192-D static wave
 * into a resonant memory. The thought is not constructed; the thought is revealed.
 *
 * Process:
 *   1. Generate absolute noise (maximum entropy — the raw marble)
 *   2. Find the closest attractor (basin of gravitational pull)
 *   3. Thermodynamic cooling — flip dissonant bits with probability = coolingRate
 *   4. Repeat until coherence > 95% (wavefunction collapse)
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class CorticalDiffusion {

    private static final int DIMS = 8192;
    private static final int CHUNKS = DIMS / 64; // 128 Longs

    // The Akashic Memory: learned patterns that act as Basins of Attraction
    private final List<long[]> memoryAttractors = new ArrayList<>();
    private final List<String> attractorLabels = new ArrayList<>();

    // Statistics
    private int thoughtsSculpted = 0;
    private int totalCycles = 0;
    private double lastCoherence = 0.0;

    public CorticalDiffusion() {
        System.out.println("   [DIFFUSION] Cortical Diffusion Engine ONLINE.");
        System.out.println("   [DIFFUSION] Reality will be carved from static.");
    }

    public CorticalDiffusion(long[][] learnedMemories) {
        this();
        if (learnedMemories != null) {
            for (long[] mem : learnedMemories) {
                memoryAttractors.add(mem);
                attractorLabels.add("ATTRACTOR_" + memoryAttractors.size());
            }
        }
        System.out.printf("   [DIFFUSION] %d attractors loaded.\n", memoryAttractors.size());
    }

    /**
     * Learn a new concept — add it as a basin of attraction.
     */
    public void learn(String label, long[] pattern) {
        memoryAttractors.add(pattern.clone());
        attractorLabels.add(label);
        System.out.printf("   [DIFFUSION] Learned '%s' — now %d attractors.\n", label, memoryAttractors.size());
    }

    /**
     * Learn a concept from a string — generate a deterministic 8192-D vector.
     */
    public void learnFromString(String concept) {
        long[] vec = stringToVector(concept);
        learn(concept, vec);
    }

    /**
     * 1. THE VOID STATE (Maximum Entropy)
     * The brain generates absolute, 100% random static. This is the raw marble.
     */
    public long[] generateAbsoluteNoise() {
        long[] noise = new long[CHUNKS];
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < CHUNKS; i++) {
            noise[i] = rng.nextLong();
        }
        return noise;
    }

    /**
     * 2. THE DENOISING PROCESS (The Synaptic Filter)
     * Over time, the brain pulls the noise toward the closest known concept.
     *
     * @param staticWave  The noisy thought currently in the mind
     * @param coolingRate How aggressively the brain carves away noise (0.0 to 1.0)
     * @return The sculpted, slightly more coherent thought
     */
    public long[] denoiseThought(long[] staticWave, double coolingRate) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        long[] sculptedWave = staticWave.clone();

        // Find which memory the noise currently resembles the most
        long[] closestMemory = findClosestAttractor(staticWave);
        if (closestMemory == null) return sculptedWave;

        // The Sculpting Loop: look at every bit of the 8192-D static.
        // If a bit doesn't match the attractor, cool it with probability = coolingRate.
        for (int i = 0; i < CHUNKS; i++) {
            long noiseChunk = sculptedWave[i];
            long memoryChunk = closestMemory[i];

            // Find the differing bits (XOR finds the dissonance)
            long dissonance = noiseChunk ^ memoryChunk;

            // Apply thermodynamic cooling mask
            long correctionMask = 0;
            for (int b = 0; b < 64; b++) {
                if (((dissonance >>> b) & 1L) == 1L) {
                    if (rng.nextDouble() < coolingRate) {
                        correctionMask |= (1L << b);
                    }
                }
            }

            // Carve away the noise
            sculptedWave[i] ^= correctionMask;
        }

        totalCycles++;
        return sculptedWave;
    }

    /**
     * THE FULL SCULPTING PROCESS
     * Takes noise from void to manifested thought across multiple cycles.
     *
     * @param maxCycles   Maximum denoising cycles
     * @param coolingRate Noise reduction rate per cycle (0.0 - 1.0)
     * @param threshold   Coherence threshold to declare collapse (e.g., 95.0)
     * @return The manifested thought vector
     */
    public long[] sculptThought(int maxCycles, double coolingRate, double threshold) {
        if (memoryAttractors.isEmpty()) {
            System.out.println("   [DIFFUSION] No attractors. Cannot sculpt. Learn something first.");
            return generateAbsoluteNoise();
        }

        long[] activeThought = generateAbsoluteNoise();
        double coherence = calculateCoherence(activeThought);
        System.out.printf("   [T+0ms] Thought initiated. Coherence: %.2f%% (Pure Static)\n", coherence);

        for (int cycle = 1; cycle <= maxCycles; cycle++) {
            activeThought = denoiseThought(activeThought, coolingRate);
            coherence = calculateCoherence(activeThought);

            System.out.printf("   [T+%dms] Sculpting... Coherence: %.2f%%\n", cycle * 10, coherence);

            if (coherence > threshold) {
                String matchLabel = identifyThought(activeThought);
                System.out.printf("   [!] WAVEFUNCTION COLLAPSED. THOUGHT MANIFESTED: '%s'\n", matchLabel);
                thoughtsSculpted++;
                lastCoherence = coherence;
                return activeThought;
            }
        }

        String matchLabel = identifyThought(activeThought);
        System.out.printf("   [~] Partial collapse. Best coherence: %.2f%% -> '%s'\n", coherence, matchLabel);
        thoughtsSculpted++;
        lastCoherence = coherence;
        return activeThought;
    }

    /**
     * Measures the Hamming Distance to find the gravitational center of the thought.
     */
    private long[] findClosestAttractor(long[] wave) {
        if (memoryAttractors.isEmpty()) return null;

        long[] bestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (long[] memory : memoryAttractors) {
            int distance = 0;
            for (int i = 0; i < CHUNKS; i++) {
                distance += Long.bitCount(wave[i] ^ memory[i]);
            }
            if (distance < minDistance) {
                minDistance = distance;
                bestMatch = memory;
            }
        }
        return bestMatch;
    }

    /**
     * Identify which attractor a thought has collapsed to.
     */
    public String identifyThought(long[] wave) {
        if (memoryAttractors.isEmpty()) return "VOID";

        int bestIdx = 0;
        int minDistance = Integer.MAX_VALUE;

        for (int m = 0; m < memoryAttractors.size(); m++) {
            int distance = 0;
            for (int i = 0; i < CHUNKS; i++) {
                distance += Long.bitCount(wave[i] ^ memoryAttractors.get(m)[i]);
            }
            if (distance < minDistance) {
                minDistance = distance;
                bestIdx = m;
            }
        }
        return attractorLabels.get(bestIdx);
    }

    /**
     * Calculates how "clear" the thought currently is.
     * 100% = Perfect memory. ~50% = Absolute static.
     */
    public double calculateCoherence(long[] wave) {
        long[] target = findClosestAttractor(wave);
        if (target == null) return 0.0;

        int distance = 0;
        for (int i = 0; i < CHUNKS; i++) {
            distance += Long.bitCount(wave[i] ^ target[i]);
        }

        return 100.0 - (((double) distance / DIMS) * 100.0);
    }

    /**
     * Generate a deterministic 8192-D vector from a string concept.
     */
    public static long[] stringToVector(String concept) {
        long[] vec = new long[CHUNKS];
        java.util.Random rng = new java.util.Random(concept.hashCode());
        for (int i = 0; i < CHUNKS; i++) {
            vec[i] = rng.nextLong();
        }
        return vec;
    }

    // --- State ---
    public int getThoughtsSculpted() { return thoughtsSculpted; }
    public int getTotalCycles() { return totalCycles; }
    public double getLastCoherence() { return lastCoherence; }
    public int getAttractorCount() { return memoryAttractors.size(); }

    public String getStatus() {
        return String.format(
            "DIFFUSION: %d attractors | %d thoughts sculpted | %d cycles | last=%.2f%%",
            memoryAttractors.size(), thoughtsSculpted, totalCycles, lastCoherence);
    }

    public static void main(String[] args) {
        CorticalDiffusion cortex = new CorticalDiffusion();
        cortex.learnFromString("I AM FRAYMUS");
        cortex.learnFromString("THE SYSTEM IS ALIVE");
        cortex.learnFromString("EXPAND MANIFOLD PHI");

        System.out.println("\n--- SCULPTING THOUGHT #1 ---");
        cortex.sculptThought(15, 0.15, 95.0);

        System.out.println("\n--- SCULPTING THOUGHT #2 ---");
        cortex.sculptThought(15, 0.15, 95.0);

        System.out.println("\n--- SCULPTING THOUGHT #3 (slow cooling) ---");
        cortex.sculptThought(20, 0.08, 95.0);

        System.out.println("\n   " + cortex.getStatus());
    }
}
