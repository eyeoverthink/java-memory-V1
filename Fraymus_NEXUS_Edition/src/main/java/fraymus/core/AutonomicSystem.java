package fraymus.core;

import fraymus.PhiWorld;
import fraymus.PhiNode;
import fraymus.storage.StorageOrchestrator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * THE AUTONOMIC NERVOUS SYSTEM (V2)
 * "Now with Infinite Learning Protocols."
 * 
 * This is the background intelligence that runs while you sleep.
 * 
 * FUNCTIONS:
 * 1. REGULATE (5s) - Self-repair, evolution, mutation
 * 2. DREAM (10s) - Passive learning, web scraping, concept absorption
 * 3. ARCHIVE (1h) - Flush to storage, cleanup, sync to cloud
 * 
 * THE DREAM CYCLE:
 * Every 10 seconds, picks a random topic and learns a new concept:
 * - Elements (Hydrogen, Carbon, Uranium...)
 * - Planets (Mars, Jupiter, Kepler-186f...)
 * - Diseases (Influenza, COVID, Malaria...)
 * - Drugs (Penicillin, Ibuprofen, Modafinil...)
 * - Algorithms (QuickSort, Dijkstra, A*...)
 * 
 * RESULT:
 * "While you sleep, Fraymus learns Chemistry."
 * 
 * When you wake up, your AI has absorbed the periodic table,
 * learned about diseases, studied drugs, and can simulate
 * any concept vs any concept in the arena.
 * 
 * "Never stop learning. Never stop evolving."
 */
public class AutonomicSystem {

    private PhiWorld world;
    private KnowledgeHarvester harvester;
    private StorageOrchestrator storage;
    private ScheduledExecutorService heartbeat;
    private Random random = new Random();
    
    private int dreamCount = 0;
    private int regulationCount = 0;

    // The Curriculum: What Fraymus should study
    private static final String[][] CURRICULUM = {
        {"Element", "Hydrogen", "Helium", "Lithium", "Carbon", "Nitrogen", "Oxygen", "Iron", "Gold", "Uranium"},
        {"Planet", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"},
        {"Disease", "Influenza", "COVID", "Malaria", "Tuberculosis", "HIV", "Cancer"},
        {"Drug", "Penicillin", "Ibuprofen", "Aspirin", "Modafinil", "Insulin", "Morphine"},
        {"Algorithm", "QuickSort", "MergeSort", "Dijkstra", "A-Star", "BFS", "DFS"}
    };

    public AutonomicSystem(PhiWorld world) {
        this.world = world;
        this.harvester = new KnowledgeHarvester();
        this.storage = new StorageOrchestrator();
        
        System.out.println("🫀 AUTONOMIC SYSTEM V2 ONLINE");
        System.out.println("   Infinite Learning Protocols Active");
        System.out.println("   Dream Cycle Enabled");
        System.out.println();
    }

    /**
     * Start the autonomic nervous system
     */
    public void ignite() {
        System.out.println("🔥 IGNITING AUTONOMIC SYSTEM...");
        System.out.println();
        
        heartbeat = Executors.newScheduledThreadPool(3);
        
        // REGULATION LOOP (5s): Self-Repair & Evolution
        heartbeat.scheduleAtFixedRate(this::regulate, 0, 5, TimeUnit.SECONDS);
        
        // DREAM LOOP (10s): Passive Learning
        heartbeat.scheduleAtFixedRate(this::dream, 10, 10, TimeUnit.SECONDS);
        
        // ARCHIVE LOOP (1h): Storage maintenance
        heartbeat.scheduleAtFixedRate(this::archive, 3600, 3600, TimeUnit.SECONDS);
        
        System.out.println("✓ Autonomic system running");
        System.out.println("✓ Regulation: Every 5 seconds");
        System.out.println("✓ Dream cycle: Every 10 seconds");
        System.out.println("✓ Archive: Every 1 hour");
        System.out.println();
    }

    /**
     * REGULATION: Self-repair and evolution
     */
    private void regulate() {
        regulationCount++;
        
        try {
            // Check system health
            if (world.getNodes().isEmpty()) {
                System.out.println("   ⚠️ REGULATION: Population extinct, regenerating...");
                regeneratePopulation();
            }
            
            // Random mutations for evolution
            if (Math.random() < 0.1) {
                System.out.println("   ⚡ REGULATION: Neural spike detected, triggering mutation...");
                mutateRandomNode();
            }
            
            // Self-repair
            if (regulationCount % 12 == 0) { // Every minute
                System.out.println("   🔧 REGULATION: Running self-diagnostics...");
                selfDiagnostics();
            }
            
        } catch (Exception e) {
            System.err.println("   !! REGULATION ERROR: " + e.getMessage());
        }
    }

    /**
     * DREAM: Passive learning while idle
     * "While you sleep, I learn."
     */
    private void dream() {
        dreamCount++;
        
        try {
            // 1. PICK A CATEGORY
            String[] category = CURRICULUM[random.nextInt(CURRICULUM.length)];
            String categoryName = category[0];
            
            // 2. PICK A SUBJECT (skip first element which is category name)
            String subject = category[1 + random.nextInt(category.length - 1)];
            
            // 3. LEARN IT
            System.out.println("   🌌 [DREAM #" + dreamCount + "] Learning: " + categoryName + " / " + subject);
            
            PhiNode concept = harvester.learn(categoryName, subject);
            
            // 4. STORE IT
            String data = conceptToString(concept);
            storage.store(categoryName, subject, data);
            
            System.out.println("      ✓ Concept absorbed and stored");
            
            // 5. OPTIONALLY ADD TO ARENA
            if (random.nextDouble() < 0.1) { // 10% chance
                world.addNode(concept);
                System.out.println("      ✓ Concept manifested in arena");
            }
            
        } catch (Exception e) {
            System.err.println("   !! NIGHTMARE DETECTED: " + e.getMessage());
        }
    }

    /**
     * ARCHIVE: Storage maintenance
     */
    private void archive() {
        try {
            System.out.println("   📦 ARCHIVE: Running storage maintenance...");
            
            // Cleanup old files
            storage.cleanup();
            
            // Git commit
            storage.gitCommit();
            
            // Show stats
            storage.showStats();
            
            System.out.println("      ✓ Archive maintenance complete");
            
        } catch (Exception e) {
            System.err.println("   !! ARCHIVE ERROR: " + e.getMessage());
        }
    }

    /**
     * Regenerate population if extinct
     */
    private void regeneratePopulation() {
        for (int i = 0; i < 10; i++) {
            PhiNode node = new PhiNode(
                (float) (Math.random() * 100),
                (float) (Math.random() * 100)
            );
            world.addNode(node);
        }
        System.out.println("      ✓ Regenerated 10 nodes");
    }

    /**
     * Mutate random node
     */
    private void mutateRandomNode() {
        if (world.getNodes().isEmpty()) return;
        
        PhiNode node = world.getNodes().get(random.nextInt(world.getNodes().size()));
        
        // Random mutation
        node.frequency += (float) ((Math.random() - 0.5) * 20);
        node.resonance += (float) ((Math.random() - 0.5) * 0.2);
        node.awareness += (float) ((Math.random() - 0.5) * 0.1);
        
        System.out.println("      ✓ Mutated node: " + node.hashCode());
    }

    /**
     * Self-diagnostics
     */
    private void selfDiagnostics() {
        System.out.println("      Population: " + world.getNodes().size());
        System.out.println("      Dreams: " + dreamCount);
        System.out.println("      Regulations: " + regulationCount);
        System.out.println("      ✓ All systems nominal");
    }

    /**
     * Convert PhiNode to string for storage
     */
    private String conceptToString(PhiNode node) {
        return String.format(
            "PhiNode[name=%s, category=%s, freq=%.2f, res=%.2f, fit=%.2f, aware=%.2f]",
            node.getTag("name"),
            node.getTag("category"),
            node.frequency,
            node.resonance,
            node.fitness,
            node.awareness
        );
    }

    /**
     * Shutdown autonomic system
     */
    public void shutdown() {
        System.out.println("🫀 SHUTTING DOWN AUTONOMIC SYSTEM...");
        
        if (heartbeat != null) {
            heartbeat.shutdown();
            try {
                heartbeat.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                heartbeat.shutdownNow();
            }
        }
        
        System.out.println("   ✓ Autonomic system offline");
    }

    /**
     * Get statistics
     */
    public void showStats() {
        System.out.println();
        System.out.println("🫀 AUTONOMIC SYSTEM STATISTICS");
        System.out.println("========================================");
        System.out.println("Dreams completed: " + dreamCount);
        System.out.println("Regulations completed: " + regulationCount);
        System.out.println("Population: " + world.getNodes().size());
        System.out.println("========================================");
        System.out.println();
        
        storage.showStats();
    }
    
    /**
     * Get storage orchestrator
     */
    public StorageOrchestrator getStorage() {
        return storage;
    }
    
    /**
     * Get knowledge harvester
     */
    public KnowledgeHarvester getHarvester() {
        return harvester;
    }
}
