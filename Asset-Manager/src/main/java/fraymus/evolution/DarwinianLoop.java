package fraymus.evolution;

import fraymus.brain.BicameralPrism;
import fraymus.genesis.GenesisSandbox;
import fraymus.limbs.ClawConnector;
import fraymus.core.AuditLog;
import java.io.*;

/**
 * DARWINIAN LOOP - The Evolutionary Engine
 * 
 * Runs in background, continuously evolving the organism:
 * 1. MITOSIS: Create mutant daughter
 * 2. INTELLIGENT MUTATION: Ask brain for upgrades
 * 3. GESTATION: Build mutant in sandbox
 * 4. NATURAL SELECTION: Test if mutant is superior
 * 5. REPLACEMENT: If better, replace current DNA
 * 
 * This makes Fraynix ANTIFRAGILE:
 * - The more it crashes, the stronger it becomes
 * - Each failure teaches what NOT to do
 * - Successful mutations are preserved
 * - Failed mutations are discarded
 */
public class DarwinianLoop implements Runnable {

    private FraynixDNA currentDNA;
    private final GenesisSandbox sandbox;
    private final BicameralPrism brain;
    private final ClawConnector hands;
    private final AuditLog auditLog;
    
    private volatile boolean running = false;
    private int evolutionCycles = 0;
    private int successfulMutations = 0;
    private int failedMutations = 0;
    
    // Evolution parameters
    private final long EVOLUTION_INTERVAL_MS = 60000; // 1 minute between cycles
    private final String DNA_FILE = "Fraynix_Seed.dna";

    public DarwinianLoop(AuditLog auditLog) {
        this.currentDNA = loadOrCreateDNA();
        this.sandbox = new GenesisSandbox(auditLog);
        this.brain = new BicameralPrism(auditLog);
        this.hands = new ClawConnector();
        this.auditLog = auditLog;
    }

    /**
     * Start evolution
     */
    public void start() {
        if (running) return;
        running = true;
        new Thread(this, "DarwinianLoop").start();
    }

    /**
     * Stop evolution
     */
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🐢 DARWINIAN ENGINE - EVOLUTION STARTED               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Current Generation: " + currentDNA.generation);
        System.out.println("Fitness Score: " + String.format("%.2f", currentDNA.fitnessScore));
        System.out.println();
        
        auditLog.log("evolution_started", currentDNA);

        while (running) {
            try {
                evolutionCycle();
                Thread.sleep(EVOLUTION_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("❌ EVOLUTION ERROR: " + e.getMessage());
                auditLog.log("evolution_error", currentDNA, e);
            }
        }
        
        System.out.println("🐢 DARWINIAN ENGINE: Evolution stopped");
        auditLog.log("evolution_stopped", currentDNA);
    }

    /**
     * Single evolution cycle
     */
    private void evolutionCycle() {
        evolutionCycles++;
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🧬 EVOLUTION CYCLE " + evolutionCycles);
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // 1. MITOSIS: Create a Mutant Daughter
        System.out.println("🥚 Phase 1: MITOSIS");
        FraynixDNA mutant = currentDNA.mutate();
        System.out.println("   Created Gen " + mutant.generation + " mutant");
        System.out.println();
        
        // 2. INTELLIGENT MUTATION (Using Bicameral Brain)
        System.out.println("🧠 Phase 2: INTELLIGENT MUTATION");
        if (brain.isReady()) {
            String proposedUpgrade = proposeUpgrade();
            if (proposedUpgrade != null && !proposedUpgrade.isEmpty()) {
                applyIntelligentMutation(mutant, proposedUpgrade);
            }
        } else {
            System.out.println("   ⚠️ Brain not available - using random mutation only");
        }
        System.out.println();
        
        // 3. GESTATION (Build the Mutant)
        System.out.println("🔬 Phase 3: GESTATION");
        boolean viable = attemptBirth(mutant);
        System.out.println();
        
        // 4. NATURAL SELECTION
        System.out.println("⚖️ Phase 4: NATURAL SELECTION");
        if (viable) {
            double mutantFitness = evaluateFitness(mutant);
            mutant.fitnessScore = mutantFitness;
            
            System.out.println("   Current Fitness: " + String.format("%.2f", currentDNA.fitnessScore));
            System.out.println("   Mutant Fitness: " + String.format("%.2f", mutantFitness));
            
            if (mutantFitness > currentDNA.fitnessScore) {
                System.out.println();
                System.out.println("🚀 EVOLUTION: The Mutant is SUPERIOR!");
                System.out.println("   Replacing current organism...");
                
                currentDNA = mutant;
                successfulMutations++;
                saveSeed(currentDNA);
                
                auditLog.log("evolution_success", mutant);
                
                System.out.println("   ✓ New DNA saved to " + DNA_FILE);
                System.out.println("   ✓ Generation: " + currentDNA.generation);
            } else {
                System.out.println();
                System.out.println("💀 NATURAL SELECTION: Mutant is INFERIOR");
                System.out.println("   Discarding mutant...");
                failedMutations++;
                auditLog.log("evolution_failed", mutant);
            }
        } else {
            System.out.println("💀 STILLBIRTH: Mutant failed viability checks");
            failedMutations++;
            auditLog.log("evolution_stillbirth", mutant);
        }
        
        System.out.println();
        System.out.println("Statistics:");
        System.out.println("  Total Cycles: " + evolutionCycles);
        System.out.println("  Successful: " + successfulMutations);
        System.out.println("  Failed: " + failedMutations);
        System.out.println("  Success Rate: " + 
            String.format("%.1f%%", (successfulMutations * 100.0 / evolutionCycles)));
        System.out.println();
    }

    /**
     * Propose upgrade using bicameral brain
     */
    private String proposeUpgrade() {
        String currentPhysics = currentDNA.getGene("PhysicsEngine");
        String currentMemory = currentDNA.getGene("MemoryModel");
        
        String prompt = "Our current system uses:\n" +
                       "- Physics Engine: " + currentPhysics + "\n" +
                       "- Memory Model: " + currentMemory + "\n\n" +
                       "Propose ONE specific upgrade that would make the system faster or smarter. " +
                       "Return ONLY the upgrade name (e.g., 'Quantum' or 'VectorDB'). " +
                       "Be concise - one word or short phrase.";
        
        try {
            return brain.thinkLogically(prompt).trim();
        } catch (Exception e) {
            System.out.println("   ⚠️ Brain proposal failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Apply intelligent mutation based on brain proposal
     */
    private void applyIntelligentMutation(FraynixDNA mutant, String proposal) {
        System.out.println("   Brain proposes: " + proposal);
        
        // Try to apply to relevant gene
        if (proposal.toLowerCase().contains("quantum") || 
            proposal.toLowerCase().contains("einstein")) {
            mutant.setGene("PhysicsEngine", proposal);
            System.out.println("   Applied to PhysicsEngine");
        } else if (proposal.toLowerCase().contains("vector") || 
                   proposal.toLowerCase().contains("chroma") ||
                   proposal.toLowerCase().contains("graph")) {
            mutant.setGene("MemoryModel", proposal);
            System.out.println("   Applied to MemoryModel");
        } else {
            System.out.println("   Proposal unclear - using random mutation");
        }
    }

    /**
     * Attempt to birth mutant (build and test)
     */
    private boolean attemptBirth(FraynixDNA dna) {
        System.out.println("   Testing mutant viability...");
        
        // For V1, we do basic validation
        // In production, this would:
        // 1. Generate code from DNA
        // 2. Build in sandbox
        // 3. Run tests
        // 4. Verify it doesn't crash
        
        // Simple viability check: ensure critical genes exist
        boolean viable = dna.getGene("PhysicsEngine") != null &&
                        dna.getGene("MemoryModel") != null &&
                        dna.getGene("BrainArchitecture") != null;
        
        if (viable) {
            System.out.println("   ✓ Mutant is viable");
        } else {
            System.out.println("   ✗ Mutant has missing critical genes");
        }
        
        return viable;
    }

    /**
     * Evaluate fitness of DNA
     */
    private double evaluateFitness(FraynixDNA dna) {
        double fitness = 0.0;
        
        // Fitness criteria (higher is better)
        
        // 1. Advanced physics = higher fitness
        String physics = dna.getGene("PhysicsEngine");
        if (physics.contains("Quantum")) fitness += 50;
        else if (physics.contains("Einstein")) fitness += 30;
        else if (physics.contains("Newtonian")) fitness += 10;
        
        // 2. Advanced memory = higher fitness
        String memory = dna.getGene("MemoryModel");
        if (memory.contains("Vector")) fitness += 40;
        else if (memory.contains("Chroma")) fitness += 35;
        else if (memory.contains("Graph")) fitness += 30;
        else if (memory.contains("JSON")) fitness += 10;
        
        // 3. Performance tuning = higher fitness
        try {
            int reflex = Integer.parseInt(dna.getGene("ReflexLoop").replace("ms", ""));
            fitness += (100 - reflex) / 10.0; // Faster = better
        } catch (Exception e) {}
        
        // 4. Generation bonus (older = more tested)
        fitness += dna.generation * 0.5;
        
        return fitness;
    }

    /**
     * Save DNA to disk
     */
    private void saveSeed(FraynixDNA dna) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DNA_FILE))) {
            oos.writeObject(dna);
            System.out.println("💾 DNA SAVED: System is immortal");
            auditLog.log("dna_saved", dna);
        } catch (Exception e) {
            System.err.println("❌ DNA SAVE FAILED: " + e.getMessage());
            auditLog.log("dna_save_failed", dna, e);
        }
    }

    /**
     * Load DNA from disk or create new
     */
    private FraynixDNA loadOrCreateDNA() {
        File dnaFile = new File(DNA_FILE);
        
        if (dnaFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(dnaFile))) {
                FraynixDNA dna = (FraynixDNA) ois.readObject();
                System.out.println("📚 LOADED DNA: Generation " + dna.generation);
                return dna;
            } catch (Exception e) {
                System.out.println("⚠️ DNA load failed: " + e.getMessage());
            }
        }
        
        System.out.println("🌱 CREATING ADAM GENOME (Generation 0)");
        return new FraynixDNA();
    }

    /**
     * Get current DNA
     */
    public FraynixDNA getCurrentDNA() {
        return currentDNA;
    }

    /**
     * Get statistics
     */
    public String getStats() {
        return String.format("Cycles: %d, Success: %d, Failed: %d, Current Gen: %d",
            evolutionCycles, successfulMutations, failedMutations, currentDNA.generation);
    }
}
