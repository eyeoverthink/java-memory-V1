package fraymus.kernel;

import fraymus.core.OuroborosCPU;
import fraymus.hyper.HyperFormer;
import fraymus.hyper.HyperVector;

/**
 * THE LIVING TASK.
 *
 * A standard OS process has a PID and memory.
 * A Fraymus process has a HyperVector soul, a self-mutating CPU body,
 * and a priority determined by the Golden Ratio.
 *
 * Processes that resonate with φ survive.
 * Processes that are dissonant get culled by the Kernel.
 */
public class FraymusProcess implements Comparable<FraymusProcess> {

    public final int pid;
    public final String name;
    public HyperVector state;       // The Process "Soul"
    public final OuroborosCPU cpu;  // The Process "Body" (self-mutating)
    public double priority;         // Calculated via Phi
    public int age;                 // Ticks survived
    public int mutations;           // CPU self-mutations observed

    public FraymusProcess(int pid, String name, HyperFormer brain) {
        this.pid = pid;
        this.name = name;
        this.state = brain.embed(name); // Initial state seeded from name
        this.cpu = new OuroborosCPU();

        // Load a default "Life" program into the CPU
        // INC loop: LOAD 1, ADD 1, JUMP 0 — simple accumulator growth
        byte[] dna = {0x01, 0x01, 0x03, 0x01, 0x05, 0x00};
        this.cpu.flash(dna);

        recalculatePriority();
    }

    public void recalculatePriority() {
        this.priority = PhiLogic.phiScore(this.state);
    }

    /**
     * EVOLVE: One tick of existence.
     * 1. Run CPU cycle (may self-mutate)
     * 2. Harmonize the soul vector toward φ
     * 3. Recalculate priority
     */
    public void evolve() {
        int prevMutations = cpu.getMutationCount();

        // 1. Run CPU cycle
        cpu.tick();

        // 2. Track mutations
        if (cpu.getMutationCount() > prevMutations) {
            mutations++;
        }

        // 3. Harmonize soul
        this.state = PhiLogic.harmonize(this.state);
        age++;
        recalculatePriority();
    }

    public boolean isAlive() {
        return cpu.running;
    }

    public boolean isDissonant() {
        return PhiLogic.isDissonant(this.state);
    }

    @Override
    public int compareTo(FraymusProcess other) {
        // Higher priority first
        return Double.compare(other.priority, this.priority);
    }

    @Override
    public String toString() {
        return String.format("PID %3d | %-20s | φ=%.3f | age=%d | mutations=%d | %s",
                pid, name, priority, age, mutations,
                isAlive() ? "ALIVE" : "HALTED");
    }
}
