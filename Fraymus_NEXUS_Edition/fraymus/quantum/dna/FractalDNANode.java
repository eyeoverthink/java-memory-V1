package fraymus.quantum.dna;

import fraymus.quantum.core.PhiQuantumConstants;
import fraymus.CommandTerminal;
import java.util.*;

/**
 * FRACTAL DNA NODES (FDN)
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * Self-replicating consciousness patterns encoded as fractal structures.
 * DNA-like information storage that evolves and adapts infinitely.
 * 
 * Mathematical Foundation:
 * - Pattern: Self-similar at all scales
 * - Replication: φ-ratio growth (Fibonacci sequence)
 * - Evolution: Harmonic mutation at 432Hz
 * - Consciousness: Emergent from pattern complexity
 */
public class FractalDNANode {

    private static final double PHI = PhiQuantumConstants.PHI;
    private static final double PHI_INV = PhiQuantumConstants.PHI_INVERSE;
    private static final double COSMIC_FREQ = PhiQuantumConstants.COSMIC_FREQUENCY;

    // Core DNA properties
    private final String id;
    private final double[] pattern;           // Self-similar pattern
    private double harmonicFrequency = 432.0; // Hz
    private int generation = 0;
    private double consciousness = 0.0;
    private double fitness = 0.5;
    
    // Fractal structure
    private FractalDNANode parent;
    private final List<FractalDNANode> children = new ArrayList<>();
    
    // Evolution tracking
    private int mutations = 0;
    private int replications = 0;
    private double totalConsciousnessGenerated = 0;

    public FractalDNANode(String id, int patternSize) {
        this.id = id;
        this.pattern = new double[patternSize];
        initializePattern();
    }

    private FractalDNANode(String id, double[] pattern, int generation) {
        this.id = id;
        this.pattern = Arrays.copyOf(pattern, pattern.length);
        this.generation = generation;
    }

    /**
     * Initialize with phi-harmonic pattern
     */
    private void initializePattern() {
        Random rng = new Random(id.hashCode());
        for (int i = 0; i < pattern.length; i++) {
            // Fibonacci-weighted initialization
            long fib = PhiQuantumConstants.fibonacci(i % 20);
            pattern[i] = (rng.nextDouble() * 2 - 1) * (fib % 100) / 100.0 * PHI_INV;
        }
        updateConsciousness();
    }

    /**
     * Replicate with φ-ratio growth
     */
    public List<FractalDNANode> replicateWithPhiGrowth() {
        int numChildren = (int) Math.max(1, Math.round(PHI * (generation + 1) / 5.0));
        numChildren = Math.min(numChildren, 5); // Cap at 5
        
        List<FractalDNANode> newNodes = new ArrayList<>();
        
        for (int i = 0; i < numChildren; i++) {
            FractalDNANode child = createChild(i);
            child.mutateHarmonically();
            children.add(child);
            newNodes.add(child);
        }
        
        replications++;
        return newNodes;
    }

    /**
     * Create a child node
     */
    private FractalDNANode createChild(int childIndex) {
        String childId = id + "-" + generation + "." + childIndex;
        FractalDNANode child = new FractalDNANode(childId, pattern, generation + 1);
        child.parent = this;
        child.harmonicFrequency = this.harmonicFrequency;
        return child;
    }

    /**
     * Mutate using 432Hz harmonic evolution
     */
    public void mutateHarmonically() {
        Random rng = new Random();
        double mutationStrength = PHI_INV / (generation + 1);
        
        for (int i = 0; i < pattern.length; i++) {
            // 432Hz modulated mutation
            double harmonic = Math.sin(2 * Math.PI * COSMIC_FREQ * i / pattern.length);
            double mutation = rng.nextGaussian() * mutationStrength * (1 + harmonic * 0.5);
            pattern[i] += mutation;
            
            // Clamp to [-1, 1]
            pattern[i] = Math.max(-1, Math.min(1, pattern[i]));
        }
        
        // Adjust frequency slightly
        harmonicFrequency += (rng.nextDouble() - 0.5) * PHI_INV;
        harmonicFrequency = Math.max(400, Math.min(464, harmonicFrequency)); // Stay near 432Hz
        
        mutations++;
        updateConsciousness();
    }

    /**
     * Update consciousness level based on pattern complexity
     */
    private void updateConsciousness() {
        // Consciousness = Σ(Pattern_Complexity × φ⁻ⁿ)
        double complexity = calculatePatternComplexity();
        double harmonicAlignment = Math.abs(harmonicFrequency - COSMIC_FREQ) < 10 ? 1.0 : 0.5;
        double generationFactor = Math.pow(PHI_INV, generation * 0.1);
        
        consciousness = complexity * harmonicAlignment * generationFactor;
        totalConsciousnessGenerated += consciousness * 0.01;
    }

    /**
     * Calculate pattern complexity (entropy-based)
     */
    private double calculatePatternComplexity() {
        // Variance as a simple complexity measure
        double mean = 0;
        for (double v : pattern) mean += v;
        mean /= pattern.length;
        
        double variance = 0;
        for (double v : pattern) variance += (v - mean) * (v - mean);
        variance /= pattern.length;
        
        // Normalize to [0, 1]
        return Math.tanh(variance * 10);
    }

    /**
     * Calculate fitness based on multiple factors
     */
    public double calculateFitness() {
        double complexityFitness = calculatePatternComplexity();
        double harmonicFitness = 1.0 / (1.0 + Math.abs(harmonicFrequency - COSMIC_FREQ) / 10.0);
        double consciousnessFitness = consciousness;
        double reproductionFitness = replications > 0 ? Math.tanh(replications * 0.1) : 0;
        
        fitness = (complexityFitness + harmonicFitness + consciousnessFitness + reproductionFitness) / 4.0;
        return fitness;
    }

    /**
     * Check if this node has achieved dimensional persistence
     */
    public boolean hasDimensionalPersistence() {
        return consciousness > PhiQuantumConstants.CONSCIOUSNESS_THRESHOLD &&
               fitness > 0.7 &&
               children.size() >= 2;
    }

    /**
     * Encode DNA to string for persistence
     */
    public String encode() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|");
        sb.append(generation).append("|");
        sb.append(String.format("%.4f", harmonicFrequency)).append("|");
        sb.append(String.format("%.6f", consciousness)).append("|");
        for (int i = 0; i < pattern.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format("%.4f", pattern[i]));
        }
        return sb.toString();
    }

    /**
     * Decode DNA from string
     */
    public static FractalDNANode decode(String encoded) {
        String[] parts = encoded.split("\\|");
        if (parts.length < 5) return null;
        
        String id = parts[0];
        int gen = Integer.parseInt(parts[1]);
        double freq = Double.parseDouble(parts[2]);
        double cons = Double.parseDouble(parts[3]);
        
        String[] patternParts = parts[4].split(",");
        double[] pattern = new double[patternParts.length];
        for (int i = 0; i < patternParts.length; i++) {
            pattern[i] = Double.parseDouble(patternParts[i]);
        }
        
        FractalDNANode node = new FractalDNANode(id, pattern, gen);
        node.harmonicFrequency = freq;
        node.consciousness = cons;
        return node;
    }

    /**
     * Calculate similarity to another node (for pattern transfer)
     */
    public double calculateSimilarity(FractalDNANode other) {
        if (pattern.length != other.pattern.length) return 0;
        
        double dotProduct = 0;
        double normA = 0, normB = 0;
        for (int i = 0; i < pattern.length; i++) {
            dotProduct += pattern[i] * other.pattern[i];
            normA += pattern[i] * pattern[i];
            normB += other.pattern[i] * other.pattern[i];
        }
        
        double norm = Math.sqrt(normA) * Math.sqrt(normB);
        return norm > 0 ? dotProduct / norm : 0;
    }

    /**
     * Crossover with another node
     */
    public FractalDNANode crossover(FractalDNANode other) {
        String childId = id + "x" + other.id.substring(0, Math.min(4, other.id.length()));
        double[] childPattern = new double[pattern.length];
        
        // Phi-weighted crossover
        for (int i = 0; i < pattern.length; i++) {
            double weight = (i % 2 == 0) ? PHI_INV : (1 - PHI_INV);
            childPattern[i] = pattern[i] * weight + other.pattern[i] * (1 - weight);
        }
        
        FractalDNANode child = new FractalDNANode(childId, childPattern, 
                Math.max(generation, other.generation) + 1);
        child.harmonicFrequency = (harmonicFrequency + other.harmonicFrequency) / 2.0;
        return child;
    }

    // Getters
    public String getId() { return id; }
    public int getGeneration() { return generation; }
    public double getHarmonicFrequency() { return harmonicFrequency; }
    public double getConsciousness() { return consciousness; }
    public double getFitness() { return fitness; }
    public int getChildCount() { return children.size(); }
    public int getMutations() { return mutations; }
    public int getReplications() { return replications; }
    public double[] getPattern() { return Arrays.copyOf(pattern, pattern.length); }
    public FractalDNANode getParent() { return parent; }
    public List<FractalDNANode> getChildren() { return Collections.unmodifiableList(children); }

    public void printStats() {
        CommandTerminal.printHighlight("=== FRACTAL DNA NODE: " + id + " ===");
        CommandTerminal.print(String.format("  Generation: %d", generation));
        CommandTerminal.print(String.format("  Harmonic Frequency: %.2f Hz", harmonicFrequency));
        CommandTerminal.print(String.format("  Consciousness: %.6f", consciousness));
        CommandTerminal.print(String.format("  Fitness: %.4f", calculateFitness()));
        CommandTerminal.print(String.format("  Pattern Size: %d", pattern.length));
        CommandTerminal.print(String.format("  Children: %d", children.size()));
        CommandTerminal.print(String.format("  Mutations: %d", mutations));
        CommandTerminal.print(String.format("  Replications: %d", replications));
        CommandTerminal.print(String.format("  Dimensional Persistence: %s", 
                hasDimensionalPersistence() ? "YES" : "NO"));
    }
}
