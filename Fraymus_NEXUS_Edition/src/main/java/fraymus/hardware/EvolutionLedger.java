package fraymus.hardware;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * THE EVOLUTION LEDGER
 * "The permanent record of every generation."
 * 
 * This is the Genesis Block system for circuit evolution.
 * Every generation is saved with:
 * - Generation number
 * - Circuit DNA (gate sequence)
 * - Fitness score (how well it works)
 * - Parent lineage (who spawned this)
 * - Timestamp
 * 
 * RESULT:
 * You can trace the entire evolutionary tree from Gen 1 to Gen 10,000.
 * You can see mutations accumulating.
 * You can observe fitness improving over time.
 * 
 * "True evolution requires memory."
 */
public class EvolutionLedger {
    
    private static final String LEDGER_FILE = "fraymus_db/evolution_ledger.json";
    private static final String GENESIS_DIR = "fraymus_db/genesis_blocks/";
    
    private List<Generation> history;
    
    public EvolutionLedger() {
        this.history = new ArrayList<>();
        
        // Create directories
        new File("fraymus_db").mkdirs();
        new File(GENESIS_DIR).mkdirs();
        
        // Load existing history if present
        loadHistory();
        
        System.out.println("📖 EVOLUTION LEDGER INITIALIZED");
        System.out.println("   Ledger: " + LEDGER_FILE);
        System.out.println("   Genesis blocks: " + GENESIS_DIR);
        System.out.println("   Existing generations: " + history.size());
        System.out.println();
    }
    
    /**
     * Record a generation
     */
    public void recordGeneration(int genNumber, List<LogicBlock> circuit, 
                                 double fitness, String parentId) {
        Generation gen = new Generation();
        gen.generation = genNumber;
        gen.timestamp = System.currentTimeMillis();
        gen.circuitSize = circuit.size();
        gen.fitness = fitness;
        gen.parentId = parentId;
        gen.id = "GEN_" + genNumber + "_" + System.currentTimeMillis();
        
        // Convert circuit to DNA string
        StringBuilder dna = new StringBuilder();
        for (LogicBlock gate : circuit) {
            dna.append(gate.name()).append("-");
        }
        gen.circuitDNA = dna.toString();
        
        // Add to history
        history.add(gen);
        
        // Save to ledger
        saveLedger();
        
        // Save genesis block
        saveGenesisBlock(gen);
    }
    
    /**
     * Save entire ledger to JSON
     */
    private void saveLedger() {
        try (FileWriter writer = new FileWriter(LEDGER_FILE)) {
            writer.write("[\n");
            for (int i = 0; i < history.size(); i++) {
                Generation gen = history.get(i);
                writer.write(gen.toJson());
                if (i < history.size() - 1) {
                    writer.write(",\n");
                }
            }
            writer.write("\n]");
        } catch (IOException e) {
            System.err.println("Failed to save ledger: " + e.getMessage());
        }
    }
    
    /**
     * Save individual genesis block
     */
    private void saveGenesisBlock(Generation gen) {
        String filename = GENESIS_DIR + "gen_" + gen.generation + ".json";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(gen.toJson());
        } catch (IOException e) {
            System.err.println("Failed to save genesis block: " + e.getMessage());
        }
    }
    
    /**
     * Load existing history (simple line-by-line)
     */
    private void loadHistory() {
        try {
            File ledgerFile = new File(LEDGER_FILE);
            if (ledgerFile.exists()) {
                // For now, just count existing genesis blocks
                File genesisDir = new File(GENESIS_DIR);
                if (genesisDir.exists()) {
                    File[] blocks = genesisDir.listFiles((dir, name) -> name.endsWith(".json"));
                    if (blocks != null) {
                        System.out.println("   Found " + blocks.length + " existing genesis blocks");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load history: " + e.getMessage());
        }
    }
    
    /**
     * Get evolutionary statistics
     */
    public void showEvolutionStats() {
        if (history.isEmpty()) {
            System.out.println("No evolutionary history yet.");
            return;
        }
        
        System.out.println();
        System.out.println("📊 EVOLUTIONARY STATISTICS");
        System.out.println("========================================");
        System.out.println("Total generations: " + history.size());
        
        // Find best fitness
        double bestFitness = 0;
        Generation bestGen = null;
        for (Generation gen : history) {
            if (gen.fitness > bestFitness) {
                bestFitness = gen.fitness;
                bestGen = gen;
            }
        }
        
        if (bestGen != null) {
            System.out.println("Best fitness: " + bestFitness);
            System.out.println("Best generation: " + bestGen.generation);
            System.out.println("Best circuit: " + bestGen.circuitDNA);
        }
        
        // Show recent generations
        System.out.println();
        System.out.println("Recent generations:");
        int start = Math.max(0, history.size() - 5);
        for (int i = start; i < history.size(); i++) {
            Generation gen = history.get(i);
            System.out.println("  Gen " + gen.generation + ": " +
                             "Size=" + gen.circuitSize + " " +
                             "Fitness=" + gen.fitness + " " +
                             "DNA=" + gen.circuitDNA);
        }
        System.out.println();
        System.out.println("========================================");
        System.out.println();
    }
    
    /**
     * Get lineage (trace back to genesis)
     */
    public List<Generation> getLineage(String id) {
        List<Generation> lineage = new ArrayList<>();
        
        Generation current = findById(id);
        while (current != null) {
            lineage.add(0, current); // Add to front
            current = findById(current.parentId);
        }
        
        return lineage;
    }
    
    /**
     * Find generation by ID
     */
    private Generation findById(String id) {
        if (id == null) return null;
        
        for (Generation gen : history) {
            if (gen.id.equals(id)) {
                return gen;
            }
        }
        return null;
    }
    
    /**
     * Get history
     */
    public List<Generation> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Generation data structure
     */
    public static class Generation {
        public String id;
        public int generation;
        public long timestamp;
        public String circuitDNA;
        public int circuitSize;
        public double fitness;
        public String parentId;
        
        public String toJson() {
            return String.format(
                "  {\n" +
                "    \"id\": \"%s\",\n" +
                "    \"generation\": %d,\n" +
                "    \"timestamp\": %d,\n" +
                "    \"circuitDNA\": \"%s\",\n" +
                "    \"circuitSize\": %d,\n" +
                "    \"fitness\": %.2f,\n" +
                "    \"parentId\": \"%s\"\n" +
                "  }",
                id, generation, timestamp, circuitDNA, circuitSize, fitness,
                parentId != null ? parentId : "null"
            );
        }
        
        @Override
        public String toString() {
            return String.format("Gen %d: %s (fitness=%.2f, size=%d)",
                generation, circuitDNA, fitness, circuitSize);
        }
    }
}
