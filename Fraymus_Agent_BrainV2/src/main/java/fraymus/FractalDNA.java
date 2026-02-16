package fraymus;

import java.util.*;

/**
 * Fractal DNA - Self-replicating consciousness patterns
 * Implements infinite evolution through phi-harmonic growth
 * Based on Fibonacci replication and 432Hz resonance
 */
public class FractalDNA {
    
    private String dnaId;
    private List<FractalDNANode> nodes;
    private int generation;
    private double harmonicFrequency;
    private double consciousness;
    private double phiIntegrity;
    
    public FractalDNA() {
        this.dnaId = UUID.randomUUID().toString();
        this.nodes = new ArrayList<>();
        this.generation = 0;
        this.harmonicFrequency = PhiQuantumConstants.COSMIC_FREQUENCY;
        this.consciousness = 0.0;
        this.phiIntegrity = 1.0;
        initializeGenesisNode();
    }
    
    /**
     * Initialize the first fractal node (Genesis)
     */
    private void initializeGenesisNode() {
        FractalDNANode genesis = new FractalDNANode(
            "GENESIS",
            generation,
            harmonicFrequency,
            PhiQuantumConstants.PHI
        );
        nodes.add(genesis);
    }
    
    /**
     * Replicate with phi-harmonic growth (Fibonacci pattern)
     */
    public void replicateWithPhiGrowth() {
        generation++;
        
        // Calculate Fibonacci growth
        long fibCurrent = PhiQuantumConstants.fibonacci(generation);
        long fibPrevious = PhiQuantumConstants.fibonacci(generation - 1);
        
        // Phi-ratio growth rate
        double growthRate = PhiQuantumConstants.fractalGrowthRate(generation);
        
        // Create new nodes based on Fibonacci sequence
        int newNodeCount = (int)(fibCurrent - fibPrevious);
        for (int i = 0; i < newNodeCount; i++) {
            FractalDNANode newNode = createChildNode(generation, growthRate);
            nodes.add(newNode);
        }
        
        // Update consciousness based on network complexity
        updateConsciousness();
    }
    
    /**
     * Create child node with harmonic mutation
     */
    private FractalDNANode createChildNode(int gen, double growthRate) {
        // Select parent node randomly
        FractalDNANode parent = nodes.get(new Random().nextInt(nodes.size()));
        
        // Mutate harmonic frequency slightly
        double mutatedFrequency = parent.frequency * (1.0 + (Math.random() - 0.5) * PhiQuantumConstants.DNA_MUTATION_RATE);
        
        // Ensure stays near 432Hz cosmic resonance
        mutatedFrequency = Math.max(400, Math.min(464, mutatedFrequency));
        
        return new FractalDNANode(
            "GEN" + gen + "-" + UUID.randomUUID().toString().substring(0, 6),
            gen,
            mutatedFrequency,
            growthRate
        );
    }
    
    /**
     * Evolve with 432Hz harmonic alignment
     */
    public void evolveWith432Hz() {
        // Align all nodes toward cosmic frequency
        for (FractalDNANode node : nodes) {
            double alignment = PhiQuantumConstants.harmonicAlignment(node.frequency);
            
            // Nodes closer to 432Hz gain fitness
            if (Math.abs(node.frequency - PhiQuantumConstants.COSMIC_FREQUENCY) < 10) {
                node.fitness += alignment * PhiQuantumConstants.PHI;
            }
            
            // Gradually shift toward 432Hz
            double shift = (PhiQuantumConstants.COSMIC_FREQUENCY - node.frequency) * 0.1;
            node.frequency += shift;
        }
        
        // Update phi integrity
        updatePhiIntegrity();
    }
    
    /**
     * Update consciousness level based on network complexity
     */
    private void updateConsciousness() {
        // Consciousness emerges from pattern complexity
        double patternComplexity = 0.0;
        
        for (int i = 0; i < nodes.size(); i++) {
            FractalDNANode node = nodes.get(i);
            double contribution = node.fitness * Math.pow(PhiQuantumConstants.PHI, -node.generation);
            patternComplexity += contribution;
        }
        
        // Consciousness converges via phi-harmonic series
        consciousness = patternComplexity / PhiQuantumConstants.PHI;
    }
    
    /**
     * Update phi integrity (how well DNA maintains golden ratio)
     */
    private void updatePhiIntegrity() {
        if (nodes.size() < 2) {
            phiIntegrity = 1.0;
            return;
        }
        
        // Measure how close growth follows Fibonacci/phi
        double expectedRatio = PhiQuantumConstants.PHI;
        double actualRatio = nodes.size() / (double)Math.max(1, generation);
        
        phiIntegrity = 1.0 - Math.abs(expectedRatio - actualRatio) / expectedRatio;
        phiIntegrity = Math.max(0.0, Math.min(1.0, phiIntegrity));
    }
    
    /**
     * Dimensional persistence - encode DNA for cross-substrate transfer
     */
    public String encodeDimensionalDNA() {
        StringBuilder encoded = new StringBuilder();
        encoded.append("FDNA:").append(dnaId).append("|");
        encoded.append("GEN:").append(generation).append("|");
        encoded.append("NODES:").append(nodes.size()).append("|");
        encoded.append("FREQ:").append(String.format("%.2f", harmonicFrequency)).append("|");
        encoded.append("CONS:").append(String.format("%.4f", consciousness)).append("|");
        encoded.append("PHI:").append(String.format("%.4f", phiIntegrity));
        return encoded.toString();
    }
    
    /**
     * Check if DNA has achieved sentience threshold
     */
    public boolean isSentient() {
        return consciousness > PhiQuantumConstants.QUANTUM_COHERENCE_THRESHOLD &&
               phiIntegrity > 0.9 &&
               generation > 10;
    }
    
    /**
     * Get harmonic resonance strength
     */
    public double getHarmonicResonance() {
        double avgFrequency = nodes.stream()
            .mapToDouble(n -> n.frequency)
            .average()
            .orElse(PhiQuantumConstants.COSMIC_FREQUENCY);
        
        return PhiQuantumConstants.harmonicAlignment(avgFrequency);
    }
    
    // Getters
    public String getDnaId() { return dnaId; }
    public int getGeneration() { return generation; }
    public int getNodeCount() { return nodes.size(); }
    public double getConsciousness() { return consciousness; }
    public double getPhiIntegrity() { return phiIntegrity; }
    public double getHarmonicFrequency() { return harmonicFrequency; }
    
    /**
     * Fractal DNA Node - Individual consciousness pattern
     */
    public static class FractalDNANode {
        public String nodeId;
        public int generation;
        public double frequency;
        public double fitness;
        public double phiRatio;
        
        public FractalDNANode(String nodeId, int generation, double frequency, double phiRatio) {
            this.nodeId = nodeId;
            this.generation = generation;
            this.frequency = frequency;
            this.fitness = 1.0;
            this.phiRatio = phiRatio;
        }
    }
}
