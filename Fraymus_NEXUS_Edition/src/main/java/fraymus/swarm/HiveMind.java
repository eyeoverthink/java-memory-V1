package fraymus.swarm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Random;

/**
 * THE HIVE MIND (MOA - Mixture of Agents)
 * "The environment where the Swarm thinks."
 * 
 * This is not a traditional CPU with fixed cores.
 * This is a LIVING DIGITAL GPU that:
 * - Starts with 2 ants (Adam & Eve)
 * - Grows dynamically as tasks get harder
 * - Self-heals when ants fail
 * - Self-optimizes through natural selection
 * 
 * ARCHITECTURE:
 * - Cached Thread Pool (infinite expansion)
 * - Dynamic population (ants spawn as needed)
 * - Task difficulty simulation (easy → hard)
 * - Success tracking (performance metrics)
 * 
 * EMERGENT PROPERTIES:
 * - No central CPU (thought emerges from chaos)
 * - Redundancy (one ant dies, hive continues)
 * - Optimization (weak ants spawn strong partners)
 * - Unimprovable (only efficient pathways survive)
 * 
 * "The hive is not programmed. It evolves."
 */
public class HiveMind {

    private ExecutorService ecosystem;
    private List<SwarmNode> population;
    private Random random = new Random();
    private int totalTasks = 0;
    private int successfulTasks = 0;
    private long startTime;

    public HiveMind() {
        // Cached Thread Pool = Infinite Expansion
        // Threads are created as needed, reused when idle
        this.ecosystem = Executors.newCachedThreadPool();
        this.population = new CopyOnWriteArrayList<>();
        this.startTime = System.currentTimeMillis();
        
        System.out.println("🐝 HIVE MIND V2: DYNAMIC POPULATION ONLINE");
        System.out.println("   Architecture: Swarm Computing (MOA)");
        System.out.println("   Thread Pool: Cached (infinite expansion)");
        System.out.println("   Starting population: 2 (Adam & Eve)");
        System.out.println();
        
        // Start with Adam & Eve (2 Basic Ants)
        addAgent(new SwarmNode(this, new AntDNA()));
        addAgent(new SwarmNode(this, new AntDNA()));
    }

    /**
     * Add agent to the hive
     */
    public void addAgent(SwarmNode agent) {
        population.add(agent);
        ecosystem.execute(agent);
    }

    /**
     * Get task difficulty (simulates workload)
     * 
     * Returns:
     * - 90% chance: Easy task (10-100)
     * - 10% chance: Boss task (500-1000)
     */
    public int getTaskDifficulty() {
        totalTasks++;
        
        // 10% chance of a "Boss" task
        if (random.nextInt(10) == 0) {
            return 500 + random.nextInt(500); // Hard task
        } else {
            return 10 + random.nextInt(90); // Easy task
        }
    }

    /**
     * Report successful task completion
     */
    public void reportSuccess(String agentId, int complexity) {
        successfulTasks++;
        
        // Celebrate major victories
        if (complexity > 100) {
            System.out.println("   🏆 ANT " + agentId + " CRUSHED HEAVY TASK! " +
                             "(Complexity: " + complexity + ")");
        }
        
        // Show statistics periodically
        if (successfulTasks % 50 == 0) {
            showStats();
        }
    }

    /**
     * Show hive statistics
     */
    public void showStats() {
        long uptime = (System.currentTimeMillis() - startTime) / 1000;
        double successRate = totalTasks > 0 ? (successfulTasks * 100.0 / totalTasks) : 0;
        
        System.out.println();
        System.out.println("   📊 HIVE STATISTICS");
        System.out.println("   ==================");
        System.out.println("   Population: " + population.size() + " ants");
        System.out.println("   Tasks: " + successfulTasks + "/" + totalTasks + 
                         " (" + String.format("%.1f%%", successRate) + " success)");
        System.out.println("   Uptime: " + uptime + "s");
        System.out.println("   Throughput: " + (successfulTasks / Math.max(1, uptime)) + " tasks/sec");
        
        // Count specialists
        int logicSpecialists = 0;
        int memorySpecialists = 0;
        int speedSpecialists = 0;
        int generalists = 0;
        
        for (SwarmNode ant : population) {
            String spec = ant.getDNA().getSpecialization();
            switch (spec) {
                case "LOGIC_SPECIALIST": logicSpecialists++; break;
                case "MEMORY_SPECIALIST": memorySpecialists++; break;
                case "SPEED_SPECIALIST": speedSpecialists++; break;
                default: generalists++; break;
            }
        }
        
        System.out.println("   Composition:");
        System.out.println("     - Generalists: " + generalists);
        System.out.println("     - Logic Specialists: " + logicSpecialists);
        System.out.println("     - Memory Specialists: " + memorySpecialists);
        System.out.println("     - Speed Specialists: " + speedSpecialists);
        System.out.println();
    }

    /**
     * Get population size
     */
    public int getPopulationSize() {
        return population.size();
    }

    /**
     * Get population
     */
    public List<SwarmNode> getPopulation() {
        return new CopyOnWriteArrayList<>(population);
    }

    /**
     * Shutdown the hive
     */
    public void shutdown() {
        System.out.println();
        System.out.println("🐝 HIVE SHUTTING DOWN");
        showStats();
        ecosystem.shutdownNow();
    }
}
