package fraymus.evolution;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MAP-Elites (Multi-dimensional Archive of Phenotypic Elites) Mutator
 * 
 * Maintains a persistent archive of high-performing, structurally diverse code variants.
 * Ensures exploration of wide evolutionary landscape while preserving best performers.
 * 
 * Phase 2.2: MAP-Elites Mutator Implementation
 * 
 * Mathematical foundation:
 * - Archive A maps from behavior space B to performance space P
 * - A: B → P where b ∈ B is behavior descriptor, p ∈ P is performance
 * - Mutation: m(g) → g' where g is genome, g' is mutated genome
 * - Selection: Keep g' if P(g') > P(A(b)) for behavior b
 * 
 * Key capabilities:
 * - Multi-dimensional behavior space characterization
 * - Diversity preservation through niche coverage
 * - Performance tracking per niche
 * - Mutation operators for C-ISA instructions
 */
public class MAPElitesMutator {
    
    // Behavior space dimensions
    private final int behaviorDimensions;
    
    // Archive: maps behavior descriptor to elite genome
    private final Map<String, EliteGenome> archive;
    
    // Performance metrics
    private final Map<String, Double> performanceHistory;
    
    // Mutation operators
    private final List<MutationOperator> mutationOperators;
    
    // Archive statistics
    private int totalMutations;
    private int successfulMutations;
    private int archiveUpdates;
    
    public MAPElitesMutator(int behaviorDimensions) {
        this.behaviorDimensions = behaviorDimensions;
        this.archive = new ConcurrentHashMap<>();
        this.performanceHistory = new ConcurrentHashMap<>();
        this.mutationOperators = new ArrayList<>();
        this.totalMutations = 0;
        this.successfulMutations = 0;
        this.archiveUpdates = 0;
        
        // Initialize default mutation operators
        initializeDefaultOperators();
    }
    
    /**
     * Initialize default mutation operators for C-ISA programs
     */
    private void initializeDefaultOperators() {
        mutationOperators.add(new InstructionMutation());
        mutationOperators.add(new ParameterMutation());
        mutationOperators.add(new InsertMutation());
        mutationOperators.add(new DeleteMutation());
        mutationOperators.add(new SwapMutation());
        mutationOperators.add(new BlockMutation());
    }
    
    /**
     * Mutate a genome and attempt to add to archive.
     * 
     * @param genome Original genome (C-ISA program)
     * @param performance Performance score
     * @return True if added to archive (elite)
     */
    public boolean mutateAndArchive(Object genome, double performance) {
        totalMutations++;
        
        // Compute behavior descriptor
        String behaviorDescriptor = computeBehaviorDescriptor(genome);
        
        // Check if this niche exists and if new performance is better
        EliteGenome existingElite = archive.get(behaviorDescriptor);
        
        if (existingElite == null || performance > existingElite.performance) {
            // Apply mutations
            Object mutatedGenome = applyMutations(genome);
            
            // Create elite genome entry
            EliteGenome elite = new EliteGenome(
                mutatedGenome,
                performance,
                behaviorDescriptor,
                System.currentTimeMillis()
            );
            
            archive.put(behaviorDescriptor, elite);
            performanceHistory.put(behaviorDescriptor, performance);
            
            archiveUpdates++;
            successfulMutations++;
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Compute behavior descriptor from genome.
     * Multi-dimensional characterization of program structure.
     */
    private String computeBehaviorDescriptor(Object genome) {
        // Simplified: use hash of genome structure
        // In full implementation, would use actual behavior space metrics
        return String.valueOf(genome.hashCode());
    }
    
    /**
     * Apply mutations to genome.
     */
    private Object applyMutations(Object genome) {
        Object mutated = genome;
        
        // Apply random mutation operator
        MutationOperator operator = mutationOperators.get(
            (int) (Math.random() * mutationOperators.size())
        );
        
        mutated = operator.mutate(mutated);
        
        return mutated;
    }
    
    /**
     * Get elite from specific behavior niche.
     */
    public EliteGenome getElite(String behaviorDescriptor) {
        return archive.get(behaviorDescriptor);
    }
    
    /**
     * Get all elites from archive.
     */
    public Collection<EliteGenome> getAllElites() {
        return archive.values();
    }
    
    /**
     * Get archive coverage (number of filled niches).
     */
    public int getArchiveCoverage() {
        return archive.size();
    }
    
    /**
     * Get average performance across archive.
     */
    public double getAveragePerformance() {
        if (archive.isEmpty()) return 0.0;
        
        double sum = 0.0;
        for (EliteGenome elite : archive.values()) {
            sum += elite.performance;
        }
        return sum / archive.size();
    }
    
    /**
     * Get best performing elite.
     */
    public EliteGenome getBestElite() {
        EliteGenome best = null;
        double bestPerf = Double.NEGATIVE_INFINITY;
        
        for (EliteGenome elite : archive.values()) {
            if (elite.performance > bestPerf) {
                bestPerf = elite.performance;
                best = elite;
            }
        }
        
        return best;
    }
    
    /**
     * Get diversity metrics.
     */
    public DiversityMetrics getDiversityMetrics() {
        if (archive.size() < 2) {
            return new DiversityMetrics(archive.size(), 0.0, 0.0);
        }
        
        // Compute pairwise distance between behavior descriptors
        double totalDistance = 0.0;
        int comparisons = 0;
        
        List<String> descriptors = new ArrayList<>(archive.keySet());
        
        for (int i = 0; i < descriptors.size(); i++) {
            for (int j = i + 1; j < descriptors.size(); j++) {
                totalDistance += hammingDistance(descriptors.get(i), descriptors.get(j));
                comparisons++;
            }
        }
        
        double avgDistance = comparisons > 0 ? totalDistance / comparisons : 0.0;
        
        return new DiversityMetrics(
            archive.size(),
            avgDistance,
            (double) archive.size() / (behaviorDimensions * 10) // Normalized coverage
        );
    }
    
    /**
     * Clear archive.
     */
    public void clearArchive() {
        archive.clear();
        performanceHistory.clear();
        totalMutations = 0;
        successfulMutations = 0;
        archiveUpdates = 0;
    }
    
    /**
     * Get statistics.
     */
    public String getStatistics() {
        double successRate = totalMutations > 0 ? 
            (double) successfulMutations / totalMutations : 0.0;
        
        DiversityMetrics diversity = getDiversityMetrics();
        
        return String.format(
            "MAP-Elites Mutator Stats:\n" +
            "  Total mutations: %d\n" +
            "  Successful mutations: %d (%.1f%%)\n" +
            "  Archive updates: %d\n" +
            "  Archive coverage: %d niches\n" +
            "  Average performance: %.3f\n" +
            "  Diversity score: %.3f\n" +
            "  Coverage ratio: %.3f",
            totalMutations,
            successfulMutations,
            successRate * 100,
            archiveUpdates,
            archive.size(),
            getAveragePerformance(),
            diversity.averageDistance,
            diversity.coverageRatio
        );
    }
    
    /**
     * Add custom mutation operator.
     */
    public void addMutationOperator(MutationOperator operator) {
        mutationOperators.add(operator);
    }
    
    /**
     * Hamming distance between strings.
     */
    private int hammingDistance(String a, String b) {
        int distance = 0;
        int minLen = Math.min(a.length(), b.length());
        
        for (int i = 0; i < minLen; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                distance++;
            }
        }
        
        distance += Math.abs(a.length() - b.length());
        return distance;
    }
    
    /**
     * Elite Genome class
     */
    public static class EliteGenome {
        public final Object genome;
        public final double performance;
        public final String behaviorDescriptor;
        public final long createdAt;
        
        public EliteGenome(Object genome, double performance, String behaviorDescriptor, long createdAt) {
            this.genome = genome;
            this.performance = performance;
            this.behaviorDescriptor = behaviorDescriptor;
            this.createdAt = createdAt;
        }
    }
    
    /**
     * Diversity Metrics class
     */
    public static class DiversityMetrics {
        public final int nicheCount;
        public final double averageDistance;
        public final double coverageRatio;
        
        public DiversityMetrics(int nicheCount, double averageDistance, double coverageRatio) {
            this.nicheCount = nicheCount;
            this.averageDistance = averageDistance;
            this.coverageRatio = coverageRatio;
        }
    }
    
    /**
     * Mutation Operator interface
     */
    public interface MutationOperator {
        Object mutate(Object genome);
    }
    
    /**
     * Instruction Mutation: Change instruction opcode
     */
    private static class InstructionMutation implements MutationOperator {
        @Override
        public Object mutate(Object genome) {
            // Simplified: return modified genome
            // In full implementation, would modify C-ISA instructions
            return genome; // Placeholder
        }
    }
    
    /**
     * Parameter Mutation: Change instruction parameters
     */
    private static class ParameterMutation implements MutationOperator {
        @Override
        public Object mutate(Object genome) {
            return genome; // Placeholder
        }
    }
    
    /**
     * Insert Mutation: Add new instruction
     */
    private static class InsertMutation implements MutationOperator {
        @Override
        public Object mutate(Object genome) {
            return genome; // Placeholder
        }
    }
    
    /**
     * Delete Mutation: Remove instruction
     */
    private static class DeleteMutation implements MutationOperator {
        @Override
        public Object mutate(Object genome) {
            return genome; // Placeholder
        }
    }
    
    /**
     * Swap Mutation: Swap two instructions
     */
    private static class SwapMutation implements MutationOperator {
        @Override
        public Object mutate(Object genome) {
            return genome; // Placeholder
        }
    }
    
    /**
     * Block Mutation: Mutate instruction block
     */
    private static class BlockMutation implements MutationOperator {
        @Override
        public Object mutate(Object genome) {
            return genome; // Placeholder
        }
    }
}
