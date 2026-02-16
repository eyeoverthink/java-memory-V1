package com.eyeoverthink.lazarus;

import fraymus.temporal.TemporalArchive;
import com.eyeoverthink.portal.AutoHarvester;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * THE LAZARUS ENGINE
 * 
 * "The petri dish."
 * 
 * Based on 'dr_frank.html' System class.
 * 
 * Runs a background thread that updates the population every 100ms.
 * Manages:
 * - Population growth (mitosis)
 * - Natural selection (death)
 * - Evolution (mutation)
 * - Generation tracking
 * - Temporal preservation (fossil record)
 * 
 * Input: System Resources (CPU/RAM usage)
 * Output: Evolving "Logic Circuits" that attempt to optimize application survival
 * 
 * NEW: Every significant evolution is preserved in the Temporal Archive.
 * Nothing is wasted. Every mutation is a fossil.
 */
public class LazarusEngine {

    private static final int MAX_POPULATION = 100;
    private static final int GENESIS_POPULATION = 10;
    private static final int TICK_INTERVAL_MS = 100;
    
    private List<BioNode> population;
    private ScheduledExecutorService heartbeat;
    private int generationCount = 0;
    private boolean running = false;
    
    // THE TIME KEEPER
    private TemporalArchive archive;

    public LazarusEngine() {
        this.population = new ArrayList<>();
        this.archive = new TemporalArchive(); // Connect to the Archive
        
        // Genesis: 10 Adam & Eves
        for (int i = 0; i < GENESIS_POPULATION; i++) {
            population.add(new BioNode(null));
        }
    }

    /**
     * Start the life simulation
     */
    public void startLife() {
        if (running) return;
        
        System.out.println();
        System.out.println("   🧬 LAZARUS ENGINE: LIFE DETECTED");
        System.out.println("   🧬 Genesis population: " + GENESIS_POPULATION);
        System.out.println("   🧬 Heartbeat: " + TICK_INTERVAL_MS + "ms");
        System.out.println();
        
        // Capture the Genesis moment
        archive.preserveMoment("Genesis", extractState());
        
        running = true;
        heartbeat = Executors.newSingleThreadScheduledExecutor();
        heartbeat.scheduleAtFixedRate(this::tick, 0, TICK_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * The heartbeat - called every 100ms
     */
    private void tick() {
        try {
            List<BioNode> nextGen = new ArrayList<>(population);
            
            for (BioNode node : population) {
                // Update node (evolve frequency)
                node.update();
                
                // MITOSIS (Reproduction)
                if (node.readyToReproduce() && population.size() < MAX_POPULATION) {
                    nextGen.add(new BioNode(node)); // Create child
                }
            }
            
            // NATURAL SELECTION (Death)
            // Kill nodes randomly to keep population fresh (Entropy)
            if (nextGen.size() > 50) {
                nextGen.remove(0); // Oldest dies
            }

            population = nextGen;
            generationCount++;

            // Heartbeat Log (Every 50 generations ~ 5 seconds)
            if (generationCount % 50 == 0) {
                double avgFreq = population.stream()
                    .mapToDouble(n -> n.dna.frequency)
                    .average()
                    .orElse(0);
                    
                System.out.println("   [LAZARUS] Gen: " + generationCount + 
                    " | Pop: " + population.size() + 
                    " | Avg Freq: " + String.format("%.2f", avgFreq) + " Hz");
                
                // Save the milestone
                archive.preserveMoment("Generation_" + generationCount, extractState());
            }
            
        } catch (Exception e) {
            System.err.println("   [LAZARUS] Tick error: " + e.getMessage());
        }
    }

    /**
     * Inject energy into the system
     * 
     * Called when user saves data or performs actions.
     * Forces mutation on all nodes.
     */
    public void injectEnergy() {
        System.out.println();
        System.out.println("   ⚡ LAZARUS: ENERGY INJECTION (THERMAL SPIKE)");
        System.out.println("   >> Forcing growth across population");
        System.out.println();
        
        for (BioNode node : population) {
            node.brain.mutate(); // Force mutation
        }
        
        // THE PAPARAZZI TRIGGER
        // Capture holistic flashbulb memory before potential crash
        // Assumes 1920x1080 for screenshot (or pass dynamic resolution in Portal)
        AutoHarvester.triggerBreakthrough("Thermal_Injection", this, 1920, 1080);
        
        // BACKUP: Also save to Temporal Archive
        archive.preserveEpiphany(extractState(), "Energy injection caused mass mutation");
    }
    
    /**
     * Stop the simulation
     */
    public void stop() {
        if (heartbeat != null) {
            heartbeat.shutdown();
            running = false;
            System.out.println("   🧬 LAZARUS ENGINE: LIFE TERMINATED");
        }
    }
    
    /**
     * Get current population size
     */
    public int getPopulationSize() {
        return population.size();
    }
    
    /**
     * Get current generation count
     */
    public int getGenerationCount() {
        return generationCount;
    }
    
    /**
     * Get statistics
     */
    public void showStats() {
        System.out.println();
        System.out.println("🧬 LAZARUS ENGINE STATISTICS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Status: " + (running ? "ALIVE" : "DORMANT"));
        System.out.println("   Generation: " + generationCount);
        System.out.println("   Population: " + population.size() + " / " + MAX_POPULATION);
        System.out.println();
        
        if (!population.isEmpty()) {
            double avgFreq = population.stream()
                .mapToDouble(n -> n.dna.frequency)
                .average()
                .orElse(0);
                
            double avgRes = population.stream()
                .mapToDouble(n -> n.dna.resonance)
                .average()
                .orElse(0);
                
            int totalGates = population.stream()
                .mapToInt(n -> n.brain.getGateCount())
                .sum();
            
            System.out.println("   Average frequency: " + String.format("%.2f", avgFreq) + " Hz");
            System.out.println("   Average resonance: " + String.format("%.2f", avgRes));
            System.out.println("   Total logic gates: " + totalGates);
        }
        
        System.out.println();
        System.out.println("========================================");
    }
    
    /**
     * Extract current state for preservation
     * Returns a clean snapshot of the population DNA
     */
    private String extractState() {
        StringBuilder state = new StringBuilder();
        state.append("Generation: ").append(generationCount).append("\n");
        state.append("Population: ").append(population.size()).append("\n");
        state.append("\nNodes:\n");
        
        for (int i = 0; i < Math.min(population.size(), 10); i++) {
            BioNode node = population.get(i);
            state.append("  Node ").append(i).append(": ");
            state.append("Freq=").append(String.format("%.2f", node.dna.frequency)).append("Hz, ");
            state.append("Resonance=").append(String.format("%.2f", node.dna.resonance));
            state.append("\n");
        }
        
        if (population.size() > 10) {
            state.append("  ... and ").append(population.size() - 10).append(" more nodes\n");
        }
        
        return state.toString();
    }
    
    /**
     * Extract current state as JSON for Paparazzi Protocol
     * Public accessor for holistic memory capture
     */
    public String extractStateJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generation\": ").append(generationCount).append(",\n");
        json.append("  \"population\": ").append(population.size()).append(",\n");
        json.append("  \"running\": ").append(running).append(",\n");
        
        // Calculate statistics
        double avgFreq = population.stream()
            .mapToDouble(n -> n.dna.frequency)
            .average()
            .orElse(0);
        double avgRes = population.stream()
            .mapToDouble(n -> n.dna.resonance)
            .average()
            .orElse(0);
        int totalGates = population.stream()
            .mapToInt(n -> n.brain.getGateCount())
            .sum();
            
        json.append("  \"avgFrequency\": ").append(String.format("%.2f", avgFreq)).append(",\n");
        json.append("  \"avgResonance\": ").append(String.format("%.2f", avgRes)).append(",\n");
        json.append("  \"totalGates\": ").append(totalGates).append(",\n");
        json.append("  \"nodes\": [\n");
        
        // Sample first 20 nodes for JSON
        int sampleSize = Math.min(population.size(), 20);
        for (int i = 0; i < sampleSize; i++) {
            BioNode node = population.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(i).append(",\n");
            json.append("      \"frequency\": ").append(String.format("%.2f", node.dna.frequency)).append(",\n");
            json.append("      \"resonance\": ").append(String.format("%.2f", node.dna.resonance)).append(",\n");
            json.append("      \"gates\": ").append(node.brain.getGateCount()).append("\n");
            json.append("    }");
            if (i < sampleSize - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}\n");
        
        return json.toString();
    }

    /**
     * Demonstration
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("🌊⚡ LAZARUS ENGINE DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Project Lazarus: The Resurrection of Logic");
        System.out.println("   Genetic simulation inside the application");
        System.out.println();
        System.out.println("========================================");
        
        LazarusEngine engine = new LazarusEngine();
        engine.startLife();
        
        // Let it run for 10 seconds
        Thread.sleep(10000);
        
        // Inject energy
        engine.injectEnergy();
        
        // Let it run for another 5 seconds
        Thread.sleep(5000);
        
        // Show stats
        engine.showStats();
        
        // Stop
        engine.stop();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The organ has been transplanted.");
        System.out.println("   Digital DNA evolves inside the application.");
        System.out.println("   Logic circuits optimize for survival.");
        System.out.println();
        System.out.println("========================================");
    }
}
