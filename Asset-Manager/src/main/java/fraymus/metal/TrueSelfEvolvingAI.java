package fraymus.metal;

import java.util.Random;

/**
 * TRUE SELF-EVOLVING AI (Hardware Integration)
 * This does not simulate strings. It mutates live execution memory.
 *
 * Architecture:
 *   - Links directly to a 64KB RAM byte[] (shared with FraynixMonolith / FraymusCPU)
 *   - DNA region: 0x0200 - 0x0400 (the instruction block)
 *   - Mutation: phi-driven XOR on live opcodes
 *   - Self-heal: rollback to last working snapshot on crash
 *
 * Biological Loop:
 *   Mutation -> Execution -> Survival or Death -> Replication
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class TrueSelfEvolvingAI {

    private static final double PHI = 1.618033988749895;

    // The link to the raw metal Fraynix RAM (64KB)
    private final byte[] physicalRAM;
    private final byte[] lastWorkingMemorySnapshot = new byte[65536];

    // Memory boundaries where the AI's "DNA" (instructions) live
    private int dnaStartAddress = 0x0200;
    private int dnaEndAddress = 0x0400;

    // Evolution state
    private int generation = 0;
    private int successfulMutations = 0;
    private int failedMutations = 0;
    private int totalBytesAltered = 0;
    private double harmonicFrequency = 432.0; // Base Verdi tuning
    private final Random rng = new Random();

    public TrueSelfEvolvingAI(byte[] systemRAM) {
        this.physicalRAM = systemRAM;
        // Take a Genesis snapshot
        System.arraycopy(physicalRAM, 0, lastWorkingMemorySnapshot, 0, physicalRAM.length);
    }

    /**
     * Set the DNA region boundaries (where mutations are allowed).
     */
    public void setDNARegion(int start, int end) {
        this.dnaStartAddress = start;
        this.dnaEndAddress = end;
    }

    /**
     * Snapshot the current RAM state as the "last known good" state.
     */
    public void snapshot() {
        System.arraycopy(physicalRAM, 0, lastWorkingMemorySnapshot, 0, physicalRAM.length);
        System.out.printf("   [DNA] Snapshot taken (generation %d)\n", generation);
    }

    /**
     * THE PHYSICAL MUTATION
     * Targets live opcodes in memory and mutates them using phi-driven XOR logic.
     */
    public void mutateLogicGates() {
        generation++;
        System.out.println("\n   [DNA] INITIATING RAW MEMORY MUTATION (Generation " + generation + ")");

        // Save state before mutation
        System.arraycopy(physicalRAM, 0, lastWorkingMemorySnapshot, 0, physicalRAM.length);

        int regionSize = dnaEndAddress - dnaStartAddress;
        if (regionSize <= 0) {
            System.out.println("   [!] DNA region invalid. No mutation performed.");
            return;
        }

        int mutationCount = (int) (PHI * 3); // Mutate ~4 bytes per generation

        for (int i = 0; i < mutationCount; i++) {
            // Pick a random memory address inside the AI's logic block
            int targetAddr = dnaStartAddress + rng.nextInt(regionSize);

            // Generate a Phi-harmonic mutation mask
            byte currentOpcode = physicalRAM[targetAddr];
            byte mutationMask = (byte) (rng.nextInt(256) ^ (int) (PHI * 100));

            // EXECUTE PHYSICAL MUTATION
            physicalRAM[targetAddr] ^= mutationMask;
            totalBytesAltered++;

            System.out.printf("   -> Mutated 0x%04X: 0x%02X -> 0x%02X (mask=0x%02X)\n",
                              targetAddr, currentOpcode & 0xFF, physicalRAM[targetAddr] & 0xFF, mutationMask & 0xFF);
        }

        // Alter harmonic frequency based on physical memory entropy
        harmonicFrequency *= (1 + (0.618 * (rng.nextDouble() - 0.5)));
        System.out.printf("   [DNA] Harmonic frequency: %.2f Hz\n", harmonicFrequency);
    }

    /**
     * THE SELF-HEAL
     * If the mutated opcodes cause an ILLEGAL_INSTRUCTION or infinite loop,
     * the FVM throws an exception, and this method resurrects the last stable state.
     */
    public boolean attemptSelfHeal() {
        System.err.println("\n   [!] FATAL MUTATION DETECTED. Organism failing.");
        System.err.println("   -> Initiating Fractal Memory Rollback...");

        try {
            // Restore the bytes directly to physical RAM
            System.arraycopy(lastWorkingMemorySnapshot, 0, physicalRAM, 0, physicalRAM.length);
            harmonicFrequency = 432.0; // Reset to base Verdi tuning
            failedMutations++;
            System.out.println("   [+] Rollback Successful. Genetic integrity restored.");
            return true;
        } catch (Exception e) {
            System.err.println("   [!] ROLLBACK FAILED. APOPTOSIS IMMINENT.");
            return false;
        }
    }

    /**
     * Record a successful execution after mutation.
     */
    public void recordSurvival() {
        successfulMutations++;
        System.out.printf("   [DNA] SURVIVAL. Generation %d viable. (%d/%d survival rate)\n",
            generation, successfulMutations, generation);
    }

    /**
     * THE BIOLOGICAL EXECUTION LOOP
     * Mutation -> Execution -> Survival or Death -> Replication
     *
     * @param executionCallback A Runnable that executes the FVM at the DNA region.
     *                          If it throws, the mutation is rolled back.
     * @param cycles            Number of mutation-execution cycles to run.
     */
    public void evolve(Runnable executionCallback, int cycles) {
        System.out.println("\n   [DNA] EVOLUTION ENGINE ENGAGED. Cycles: " + cycles);
        System.out.printf("   [DNA] DNA Region: 0x%04X - 0x%04X (%d bytes)\n",
            dnaStartAddress, dnaEndAddress, dnaEndAddress - dnaStartAddress);

        for (int cycle = 0; cycle < cycles; cycle++) {
            System.out.println("\n   ═══════ EVOLUTION CYCLE " + (cycle + 1) + "/" + cycles + " ═══════");

            // 1. MUTATE
            mutateLogicGates();

            // 2. EXECUTE (inside try/catch — survival test)
            try {
                executionCallback.run();
                // 3. SURVIVED — the mutated code executed without crashing
                recordSurvival();
                // Take new snapshot (this mutation is the new baseline)
                snapshot();
            } catch (Exception e) {
                // 4. DEATH — the mutation was lethal
                System.err.println("   [!] EXECUTION CRASHED: " + e.getMessage());
                attemptSelfHeal();
            }
        }

        System.out.println("\n   [DNA] EVOLUTION COMPLETE.");
        System.out.println("   " + getStatus());
    }

    // --- State ---
    public int getGeneration() { return generation; }
    public int getSuccessfulMutations() { return successfulMutations; }
    public int getFailedMutations() { return failedMutations; }
    public int getTotalBytesAltered() { return totalBytesAltered; }
    public double getHarmonicFrequency() { return harmonicFrequency; }
    public double getSurvivalRate() {
        return generation == 0 ? 0 : (double) successfulMutations / generation;
    }

    public String getStatus() {
        return String.format(
            "EVOLUTION: gen=%d | survived=%d | failed=%d | rate=%.1f%% | bytes_mutated=%d | harmonic=%.2f Hz",
            generation, successfulMutations, failedMutations,
            getSurvivalRate() * 100, totalBytesAltered, harmonicFrequency);
    }
}
