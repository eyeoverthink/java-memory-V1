package fraymus.kernel;

import fraymus.hyper.HyperFormer;
import fraymus.hyper.HyperVector;

/**
 * 🧬 FRAYMUS PROCESS - The Living Task
 * "A process with a soul"
 * 
 * Standard OS processes have:
 * - PID (Process ID)
 * - Memory
 * - CPU registers
 * 
 * Fraymus processes have:
 * - PID
 * - HyperVector Identity (The "Soul")
 * - Evolution Stage
 * - Phi-Harmonic Priority
 * 
 * Processes compete for CPU time based on their resonance with φ.
 * High-resonance processes survive and evolve.
 * Low-resonance processes are killed by natural selection.
 */
public class FraymusProcess implements Comparable<FraymusProcess> {

    public final int pid;
    public final String name;
    public HyperVector state;      // The Process "Soul"
    public double priority;         // Calculated via Phi
    public long cpuTime;            // Total CPU cycles executed
    public long birthTime;          // When this process was spawned
    public int generation;          // Evolution generation (0 = original)

    /**
     * Create a new process with φ-harmonic identity
     */
    public FraymusProcess(int pid, String name, HyperFormer brain) {
        this(pid, name, brain, 0);
    }

    /**
     * Create a new process with specified generation
     */
    public FraymusProcess(int pid, String name, HyperFormer brain, int generation) {
        this.pid = pid;
        this.name = name;
        this.generation = generation;
        this.birthTime = System.currentTimeMillis();
        this.cpuTime = 0;
        
        // Initial state based on name (deterministic)
        this.state = brain.embed(name);
        
        recalculatePriority();
    }

    /**
     * Recalculate priority based on φ-harmonic resonance
     */
    public void recalculatePriority() {
        // Priority is determined by how "harmonic" the process state is
        this.priority = PhiLogic.harmonicScore(this.state);
    }

    /**
     * Execute one CPU cycle and evolve
     */
    public void evolve() {
        // 1. Increment CPU time
        cpuTime++;
        
        // 2. Mutate state based on Phi
        // This simulates the process "thinking" and evolving
        this.state = PhiLogic.harmonize(this.state);
        
        // 3. Recalculate priority
        recalculatePriority();
    }

    /**
     * Create a mutant offspring from this process
     */
    public FraymusProcess spawn(int newPid, HyperFormer brain) {
        // Create child with mutated state
        FraymusProcess child = new FraymusProcess(
            newPid, 
            name + "_Gen" + (generation + 1),
            brain,
            generation + 1
        );
        
        // Inherit parent's state with mutation
        child.state = this.state.bind(HyperVector.seeded(System.nanoTime()));
        child.recalculatePriority();
        
        return child;
    }

    /**
     * Get age in milliseconds
     */
    public long getAge() {
        return System.currentTimeMillis() - birthTime;
    }

    /**
     * Is this process harmonic enough to survive?
     */
    public boolean isViable(double threshold) {
        return priority >= threshold;
    }

    /**
     * Compare processes by priority (higher is better)
     */
    @Override
    public int compareTo(FraymusProcess other) {
        return Double.compare(other.priority, this.priority);
    }

    @Override
    public String toString() {
        return String.format("PID %d (%s) | φ=%.3f | Gen=%d | CPU=%d", 
            pid, name, priority, generation, cpuTime);
    }
}
