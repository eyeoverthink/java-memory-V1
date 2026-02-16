package fraymus.kernel;

import fraymus.hyper.HyperFormer;
import fraymus.hyper.HyperVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * THE PHI-HARMONIC SCHEDULER.
 *
 * Replaces Round-Robin with Sacred Geometry.
 * Every tick:
 *   1. Sort processes by φ-resonance (Golden Ratio alignment)
 *   2. Execute the Elite (top 3)
 *   3. Cull the Dissonant (bottom process if too weak)
 *   4. Spontaneous Genesis (random mutation-spawn from elite DNA)
 *
 * This is an operating system based on natural selection
 * where the fitness function is the Golden Ratio.
 */
public class FraymusKernel {

    private final List<FraymusProcess> processTable = new ArrayList<>();
    private final HyperFormer brain;
    private int pidCounter = 1;
    private int tickCount = 0;
    private int totalKills = 0;
    private int totalSpawns = 0;
    private final Random rng = new Random();

    public FraymusKernel(HyperFormer brain) {
        this.brain = brain;
    }

    public FraymusProcess spawn(String name) {
        FraymusProcess p = new FraymusProcess(pidCounter++, name, brain);
        processTable.add(p);
        totalSpawns++;
        System.out.println("   [KERNEL] SPAWN PID " + p.pid + ": " + name +
                " (φ=" + String.format("%.3f", p.priority) + ")");
        return p;
    }

    public int processCount() { return processTable.size(); }
    public int getTickCount() { return tickCount; }

    /**
     * ONE TICK OF THE UNIVERSE.
     */
    public void tick() {
        if (processTable.isEmpty()) return;
        tickCount++;

        // Remove halted processes
        processTable.removeIf(p -> !p.isAlive());

        if (processTable.isEmpty()) {
            System.out.println("\n   [KERNEL] ALL PROCESSES HALTED. Universe is empty.");
            return;
        }

        // 1. SORT BY PHI (Survival of the Fittest)
        Collections.sort(processTable);

        // 2. DISPLAY STATE
        System.out.println("\n⚙️ KERNEL TICK #" + tickCount + " (" + processTable.size() + " processes)");
        System.out.println("   ┌──────────────────────────────────────────────────────────────┐");
        for (FraymusProcess p : processTable) {
            String bar = phiBar(p.priority);
            System.out.printf("   │ PID %3d %-18s φ=%s %s │%n",
                    p.pid, p.name, String.format("%.3f", p.priority), bar);
        }
        System.out.println("   └──────────────────────────────────────────────────────────────┘");

        // 3. EXECUTE ELITE (top 3)
        int eliteCount = Math.min(3, processTable.size());
        for (int i = 0; i < eliteCount; i++) {
            FraymusProcess p = processTable.get(i);
            p.evolve();
        }

        // 4. CULL THE DISSONANT
        if (processTable.size() > 4) {
            FraymusProcess weakest = processTable.get(processTable.size() - 1);
            if (weakest.isDissonant() || weakest.priority < 0.3) {
                processTable.remove(processTable.size() - 1);
                totalKills++;
                System.out.println("   💀 CULL: PID " + weakest.pid + " (" + weakest.name +
                        ") — too dissonant (φ=" + String.format("%.3f", weakest.priority) + ")");
            }
        }

        // 5. SPONTANEOUS GENESIS (20% chance per tick)
        if (rng.nextDouble() < 0.2 && processTable.size() < 10) {
            // Derive name from the elite's soul
            FraymusProcess elite = processTable.get(0);
            String mutantName = "Φ_Mutant_" + (tickCount * 7 % 1000);
            FraymusProcess mutant = spawn(mutantName);
            // Cross-pollinate: bind the mutant's soul with the elite's
            mutant.state = mutant.state.bind(elite.state);
            mutant.recalculatePriority();
            System.out.println("   🧬 GENESIS: " + mutantName + " born from " + elite.name +
                    " (φ=" + String.format("%.3f", mutant.priority) + ")");
        }
    }

    public void printStats() {
        System.out.println("\n📊 KERNEL STATS:");
        System.out.println("   Ticks:   " + tickCount);
        System.out.println("   Spawns:  " + totalSpawns);
        System.out.println("   Kills:   " + totalKills);
        System.out.println("   Alive:   " + processTable.size());
        if (!processTable.isEmpty()) {
            FraymusProcess best = processTable.get(0);
            System.out.println("   Best:    PID " + best.pid + " " + best.name +
                    " (φ=" + String.format("%.3f", best.priority) + ", age=" + best.age + ")");
        }
    }

    private static String phiBar(double score) {
        int len = (int) (score * 20);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 20; i++) {
            sb.append(i < len ? "█" : "░");
        }
        sb.append("]");
        return sb.toString();
    }
}
