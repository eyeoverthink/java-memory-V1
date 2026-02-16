package fraymus.kernel;

import fraymus.hyper.HyperFormer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 🌌 FRAYMUS KERNEL - The Phi-Harmonic Scheduler
 * "Natural selection as an operating system"
 * 
 * Traditional OS Schedulers:
 * - Round Robin: Each process gets equal time
 * - Priority: Fixed priorities assigned by user
 * - Fair: Complex algorithms to balance fairness
 * 
 * Fraymus Scheduler:
 * - Phi-Harmonic: Processes compete via Golden Ratio resonance
 * - Darwinian: Weak processes die, strong processes survive
 * - Evolutionary: New processes spawn from successful ones
 * 
 * This is not a scheduler. This is an ecosystem.
 */
public class FraymusKernel {

    private final List<FraymusProcess> processTable = new ArrayList<>();
    private final HyperFormer brain;
    private int pidCounter = 1;
    private final Random rng = new Random();
    
    // Kernel parameters
    private static final int MAX_PROCESSES = 10;
    private static final int ELITE_COUNT = 3;
    private static final double SURVIVAL_THRESHOLD = 0.3;
    private static final double MUTATION_RATE = 0.2;
    
    private long tickCount = 0;
    private long totalProcessesSpawned = 0;
    private long totalProcessesKilled = 0;

    public FraymusKernel(HyperFormer brain) {
        this.brain = brain;
    }

    /**
     * Spawn a new process
     */
    public void spawn(String name) {
        FraymusProcess p = new FraymusProcess(pidCounter++, name, brain);
        processTable.add(p);
        totalProcessesSpawned++;
        
        System.out.println("   [SPAWN] PID " + p.pid + ": " + name + 
                         " | φ=" + String.format("%.3f", p.priority));
    }

    /**
     * Main scheduler tick
     * 
     * 1. Sort processes by φ-harmonic resonance
     * 2. Execute elite processes (top N)
     * 3. Kill dissonant processes (bottom 1)
     * 4. Spawn mutations from elite (evolution)
     */
    public void tick() {
        tickCount++;
        
        if (processTable.isEmpty()) {
            System.out.println("\n⚠️  NO PROCESSES. KERNEL IDLE.");
            return;
        }

        // 1. SORT BY PHI (Survival of the Fittest)
        Collections.sort(processTable);

        // 2. DISPLAY KERNEL STATE
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  ⚙️  KERNEL TICK #" + tickCount + 
                         " | Processes: " + processTable.size() + 
                         " | Spawned: " + totalProcessesSpawned +
                         " | Killed: " + totalProcessesKilled);
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // 3. EXECUTE ELITE
        int eliteCount = Math.min(ELITE_COUNT, processTable.size());
        
        System.out.println("🌟 ELITE PROCESSES (Executing):");
        for (int i = 0; i < eliteCount; i++) {
            FraymusProcess p = processTable.get(i);
            System.out.println("   " + (i+1) + ". " + p);
            p.evolve();
        }
        System.out.println();

        // 4. DISPLAY DORMANT
        if (processTable.size() > eliteCount) {
            System.out.println("💤 DORMANT PROCESSES (Waiting):");
            for (int i = eliteCount; i < Math.min(eliteCount + 3, processTable.size()); i++) {
                FraymusProcess p = processTable.get(i);
                System.out.println("   " + (i+1) + ". " + p);
            }
            if (processTable.size() > eliteCount + 3) {
                System.out.println("   ... and " + (processTable.size() - eliteCount - 3) + " more");
            }
            System.out.println();
        }

        // 5. CULL THE WEAK (Natural Selection)
        if (processTable.size() > MAX_PROCESSES / 2) {
            FraymusProcess weak = processTable.get(processTable.size() - 1);
            
            // Only kill if below survival threshold
            if (weak.priority < SURVIVAL_THRESHOLD) {
                processTable.remove(processTable.size() - 1);
                totalProcessesKilled++;
                System.out.println("💀 KILLED: " + weak + " (Too Dissonant)");
                System.out.println();
            }
        }

        // 6. EVOLUTION (Spontaneous Genesis)
        // Spawn mutations from elite processes
        if (processTable.size() < MAX_PROCESSES && rng.nextDouble() < MUTATION_RATE) {
            // Pick a random elite process to mutate
            int parentIdx = rng.nextInt(Math.min(eliteCount, processTable.size()));
            FraymusProcess parent = processTable.get(parentIdx);
            
            FraymusProcess mutant = parent.spawn(pidCounter++, brain);
            processTable.add(mutant);
            totalProcessesSpawned++;
            
            System.out.println("🧬 MUTATION: " + mutant + " (Parent: PID " + parent.pid + ")");
            System.out.println();
        }

        // 7. STATISTICS
        if (tickCount % 5 == 0) {
            displayStatistics();
        }
    }

    /**
     * Display kernel statistics
     */
    private void displayStatistics() {
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("📊 KERNEL STATISTICS");
        System.out.println("─────────────────────────────────────────────────────────────────");
        
        if (!processTable.isEmpty()) {
            double avgPriority = processTable.stream()
                .mapToDouble(p -> p.priority)
                .average()
                .orElse(0.0);
            
            double maxPriority = processTable.stream()
                .mapToDouble(p -> p.priority)
                .max()
                .orElse(0.0);
            
            double minPriority = processTable.stream()
                .mapToDouble(p -> p.priority)
                .min()
                .orElse(0.0);
            
            System.out.println("   Average φ-Resonance: " + String.format("%.3f", avgPriority));
            System.out.println("   Max φ-Resonance:     " + String.format("%.3f", maxPriority));
            System.out.println("   Min φ-Resonance:     " + String.format("%.3f", minPriority));
            System.out.println("   Total CPU Cycles:    " + 
                processTable.stream().mapToLong(p -> p.cpuTime).sum());
        }
        
        System.out.println("   Total Spawned:       " + totalProcessesSpawned);
        System.out.println("   Total Killed:        " + totalProcessesKilled);
        System.out.println("   Survival Rate:       " + 
            String.format("%.1f%%", 100.0 * (totalProcessesSpawned - totalProcessesKilled) / 
                Math.max(1, totalProcessesSpawned)));
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println();
    }

    /**
     * Get process count
     */
    public int getProcessCount() {
        return processTable.size();
    }

    /**
     * Get tick count
     */
    public long getTickCount() {
        return tickCount;
    }
}
