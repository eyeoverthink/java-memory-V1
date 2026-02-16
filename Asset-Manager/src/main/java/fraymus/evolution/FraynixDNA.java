package fraymus.evolution;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 🧬 THE GENOME - Digital Genetics
 * 
 * This is the immutable source of truth.
 * The Organism is just a temporary instance (Phenotype) of this DNA (Genotype).
 * 
 * Biology Analogy:
 * - DNA (Genotype) = This class
 * - Body (Phenotype) = FraynixOrganism.java
 * - Mutation = Random gene changes
 * - Evolution = Survival of the fittest
 * 
 * This solves the Fragility Problem:
 * - Traditional AI: Code edits accumulate errors → death
 * - Fraynix: DNA is separate from body → immortal
 */
public class FraynixDNA implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // GENE MAP: Trait → Expression
    // e.g., "MemoryArchitecture" → "VectorDatabase"
    // e.g., "Personality" → "Stoic Engineer"
    public Map<String, String> genes = new HashMap<>();
    
    // METADATA
    public int generation = 0;
    public double fitnessScore = 0.0;
    public String parentHash = "";
    public long birthTime = System.currentTimeMillis();
    
    // MUTATION RATE (0.0 to 1.0)
    private static final double MUTATION_RATE = 0.1; // 10% chance per gene

    /**
     * THE ADAM GENOME (Default Settings)
     */
    public FraynixDNA() {
        // Core Architecture
        genes.put("PhysicsEngine", "Newtonian");        // Can evolve to "Einsteinian", "Quantum"
        genes.put("MemoryModel", "BlackBox_JSON");      // Can evolve to "ChromaDB", "VectorDB"
        genes.put("BrainArchitecture", "Tesseract4D");  // Can evolve to "Tesseract8D", "Hypercube"
        
        // Intelligence Models
        genes.put("LogicModel", "llama3");              // Can evolve to "codellama", "deepseek"
        genes.put("CreativityModel", "mistral");        // Can evolve to "wizard", "hermes"
        genes.put("SynthesizerModel", "llama3");        // Can evolve to "mixtral", "solar"
        
        // Performance Tuning
        genes.put("ReflexLoop", "50ms");                // Can evolve to "10ms", "1ms"
        genes.put("HeartbeatRate", "100ms");            // Can evolve to "50ms", "10ms"
        genes.put("DreamCycleRate", "100Hz");           // Can evolve to "1000Hz", "10000Hz"
        
        // Behavioral Traits
        genes.put("Personality", "Stoic_Engineer");     // Can evolve to "Creative_Artist", "Aggressive_Optimizer"
        genes.put("RiskTolerance", "Conservative");     // Can evolve to "Moderate", "Aggressive"
        genes.put("LearningRate", "0.01");              // Can evolve to "0.1", "0.001"
        
        // System Configuration
        genes.put("MaxConcurrency", "4");               // Can evolve to "8", "16", "32"
        genes.put("CacheStrategy", "LRU");              // Can evolve to "LFU", "ARC"
        genes.put("CompressionAlgorithm", "GZIP");      // Can evolve to "ZSTD", "LZ4"
    }

    /**
     * MITOSIS: Create a mutated daughter cell
     */
    public FraynixDNA mutate() {
        FraynixDNA child = new FraynixDNA();
        child.genes = new HashMap<>(this.genes);
        child.generation = this.generation + 1;
        child.parentHash = Integer.toHexString(this.hashCode());
        child.birthTime = System.currentTimeMillis();
        
        // RANDOM MUTATION (The Spark of Evolution)
        Random rand = new Random();
        
        for (String gene : child.genes.keySet()) {
            if (rand.nextDouble() < MUTATION_RATE) {
                String mutatedValue = mutateGene(gene, child.genes.get(gene), rand);
                child.genes.put(gene, mutatedValue);
                System.out.println("   🧬 MUTATION: " + gene + " → " + mutatedValue);
            }
        }
        
        return child;
    }

    /**
     * INTELLIGENT MUTATION: Gene-specific evolution paths
     */
    private String mutateGene(String geneName, String currentValue, Random rand) {
        switch (geneName) {
            case "PhysicsEngine":
                return pickRandom(rand, "Newtonian", "Einsteinian", "Quantum", "String_Theory");
                
            case "MemoryModel":
                return pickRandom(rand, "BlackBox_JSON", "ChromaDB", "VectorDB", "GraphDB");
                
            case "BrainArchitecture":
                return pickRandom(rand, "Tesseract4D", "Tesseract8D", "Hypercube", "Fractal");
                
            case "LogicModel":
                return pickRandom(rand, "llama3", "codellama", "deepseek", "phi3");
                
            case "CreativityModel":
                return pickRandom(rand, "mistral", "wizard", "hermes", "solar");
                
            case "ReflexLoop":
                return pickRandom(rand, "100ms", "50ms", "10ms", "1ms");
                
            case "HeartbeatRate":
                return pickRandom(rand, "100ms", "50ms", "10ms", "5ms");
                
            case "MaxConcurrency":
                return pickRandom(rand, "2", "4", "8", "16", "32");
                
            case "Personality":
                return pickRandom(rand, "Stoic_Engineer", "Creative_Artist", 
                                "Aggressive_Optimizer", "Cautious_Analyst");
                
            case "RiskTolerance":
                return pickRandom(rand, "Conservative", "Moderate", "Aggressive", "Reckless");
                
            default:
                // For numeric genes, mutate by ±20%
                try {
                    double value = Double.parseDouble(currentValue);
                    double delta = value * 0.2 * (rand.nextDouble() - 0.5) * 2;
                    return String.format("%.3f", value + delta);
                } catch (NumberFormatException e) {
                    return currentValue; // No mutation if can't parse
                }
        }
    }

    /**
     * Pick random value from options
     */
    private String pickRandom(Random rand, String... options) {
        return options[rand.nextInt(options.length)];
    }

    /**
     * CROSSOVER: Sexual reproduction (combine two genomes)
     */
    public FraynixDNA crossover(FraynixDNA partner) {
        FraynixDNA child = new FraynixDNA();
        child.generation = Math.max(this.generation, partner.generation) + 1;
        child.parentHash = this.hashCode() + "x" + partner.hashCode();
        
        Random rand = new Random();
        
        // For each gene, randomly pick from either parent
        for (String gene : this.genes.keySet()) {
            if (rand.nextBoolean()) {
                child.genes.put(gene, this.genes.get(gene));
            } else {
                child.genes.put(gene, partner.genes.get(gene));
            }
        }
        
        return child;
    }

    /**
     * Get gene value
     */
    public String getGene(String name) {
        return genes.getOrDefault(name, "");
    }

    /**
     * Set gene value
     */
    public void setGene(String name, String value) {
        genes.put(name, value);
    }

    /**
     * Get genome summary
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generation: ").append(generation).append("\n");
        sb.append("Fitness: ").append(String.format("%.2f", fitnessScore)).append("\n");
        sb.append("Parent: ").append(parentHash).append("\n");
        sb.append("Age: ").append((System.currentTimeMillis() - birthTime) / 1000).append("s\n");
        sb.append("\nKey Genes:\n");
        sb.append("  Physics: ").append(genes.get("PhysicsEngine")).append("\n");
        sb.append("  Memory: ").append(genes.get("MemoryModel")).append("\n");
        sb.append("  Brain: ").append(genes.get("BrainArchitecture")).append("\n");
        sb.append("  Logic: ").append(genes.get("LogicModel")).append("\n");
        sb.append("  Creativity: ").append(genes.get("CreativityModel")).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "FraynixDNA[Gen=" + generation + ", Fitness=" + 
               String.format("%.2f", fitnessScore) + ", Genes=" + genes.size() + "]";
    }
}
