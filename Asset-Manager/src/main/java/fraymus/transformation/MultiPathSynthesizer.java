package fraymus.transformation;

import java.util.*;

/**
 * 🔬 Multi-Path Synthesizer - Ensemble Processing
 * 
 * Synthesizes multiple transformation paths into optimal solution.
 * Uses ensemble methods to combine multiple valid paths.
 */
public class MultiPathSynthesizer {
    
    private boolean initialized;
    
    /**
     * Synthesis methods
     */
    public enum SynthesisMethod {
        BEST_SINGLE,           // Select the single best path
        MAJORITY_VOTE,         // Vote on transformations
        WEIGHTED_AVERAGE,      // Weight by path quality
        CONSENSUS,             // Find common transformations
        ENSEMBLE               // Combine all paths
    }
    
    /**
     * Initialize synthesizer
     */
    public void initialize() {
        initialized = true;
        System.out.println("  [Multi-Path Synthesizer] Initialized");
    }
    
    /**
     * Synthesize multiple paths into single optimal path
     */
    public UnifiedStateTransformationEngine.Path synthesize(
        List<UnifiedStateTransformationEngine.Path> paths,
        SynthesisMethod method
    ) {
        if (!initialized) {
            System.out.println("  [Multi-Path Synthesizer] Not initialized");
            return null;
        }
        
        if (paths == null || paths.isEmpty()) {
            return null;
        }
        
        System.out.println("  [Multi-Path Synthesizer] Synthesizing " + paths.size() + " paths");
        
        switch (method) {
            case BEST_SINGLE:
                return selectBestSingle(paths);
            case MAJORITY_VOTE:
                return majorityVote(paths);
            case WEIGHTED_AVERAGE:
                return weightedAverage(paths);
            case CONSENSUS:
                return findConsensus(paths);
            case ENSEMBLE:
                return ensemble(paths);
            default:
                return selectBestSingle(paths);
        }
    }
    
    /**
     * Select the single best path (lowest cost)
     */
    private UnifiedStateTransformationEngine.Path selectBestSingle(
        List<UnifiedStateTransformationEngine.Path> paths
    ) {
        UnifiedStateTransformationEngine.Path best = paths.get(0);
        
        for (UnifiedStateTransformationEngine.Path path : paths) {
            if (path.cost < best.cost) {
                best = path;
            }
        }
        
        System.out.println("  [Multi-Path Synthesizer] Best single path cost: " + best.cost);
        return best;
    }
    
    /**
     * Majority vote on transformations
     */
    private UnifiedStateTransformationEngine.Path majorityVote(
        List<UnifiedStateTransformationEngine.Path> paths
    ) {
        // Count frequency of each transformation
        Map<String, Integer> transformationCounts = new HashMap<>();
        
        for (UnifiedStateTransformationEngine.Path path : paths) {
            for (UnifiedStateTransformationEngine.Transformation t : path.transformations) {
                transformationCounts.put(t.name, transformationCounts.getOrDefault(t.name, 0) + 1);
            }
        }
        
        // Select transformations that appear in majority of paths
        List<UnifiedStateTransformationEngine.Transformation> consensusTransformations = new ArrayList<>();
        int threshold = paths.size() / 2 + 1;
        
        for (Map.Entry<String, Integer> entry : transformationCounts.entrySet()) {
            if (entry.getValue() >= threshold) {
                // Find the transformation
                for (UnifiedStateTransformationEngine.Path path : paths) {
                    for (UnifiedStateTransformationEngine.Transformation t : path.transformations) {
                        if (t.name.equals(entry.getKey())) {
                            consensusTransformations.add(t);
                            break;
                        }
                    }
                    if (consensusTransformations.size() > 0 && 
                        consensusTransformations.get(consensusTransformations.size() - 1).name.equals(entry.getKey())) {
                        break;
                    }
                }
            }
        }
        
        // Calculate cost
        double cost = consensusTransformations.size();
        
        UnifiedStateTransformationEngine.Path result = new UnifiedStateTransformationEngine.Path(
            consensusTransformations, cost
        );
        
        System.out.println("  [Multi-Path Synthesizer] Majority vote: " + consensusTransformations.size() + " transformations");
        return result;
    }
    
    /**
     * Weighted average by path quality (inverse of cost)
     */
    private UnifiedStateTransformationEngine.Path weightedAverage(
        List<UnifiedStateTransformationEngine.Path> paths
    ) {
        // Calculate weights (inverse of cost)
        double totalWeight = 0;
        Map<UnifiedStateTransformationEngine.Path, Double> weights = new HashMap<>();
        
        for (UnifiedStateTransformationEngine.Path path : paths) {
            double weight = 1.0 / (path.cost + 0.001);  // Avoid division by zero
            weights.put(path, weight);
            totalWeight += weight;
        }
        
        // Select transformations from highest-weighted paths
        List<UnifiedStateTransformationEngine.Transformation> selectedTransformations = new ArrayList<>();
        
        // Sort paths by weight
        List<UnifiedStateTransformationEngine.Path> sortedPaths = new ArrayList<>(paths);
        sortedPaths.sort((a, b) -> Double.compare(weights.get(b), weights.get(a)));
        
        // Take top 50% of transformations from best paths
        int targetSize = (int) (sortedPaths.get(0).transformations.size() * 0.5);
        for (int i = 0; i < Math.min(targetSize, sortedPaths.get(0).transformations.size()); i++) {
            selectedTransformations.add(sortedPaths.get(0).transformations.get(i));
        }
        
        double cost = selectedTransformations.size();
        
        UnifiedStateTransformationEngine.Path result = new UnifiedStateTransformationEngine.Path(
            selectedTransformations, cost
        );
        
        System.out.println("  [Multi-Path Synthesizer] Weighted average: " + selectedTransformations.size() + " transformations");
        return result;
    }
    
    /**
     * Find consensus (common transformations across all paths)
     */
    private UnifiedStateTransformationEngine.Path findConsensus(
        List<UnifiedStateTransformationEngine.Path> paths
    ) {
        if (paths.isEmpty()) {
            return null;
        }
        
        // Find transformations present in ALL paths
        List<UnifiedStateTransformationEngine.Transformation> consensusTransformations = new ArrayList<>();
        
        if (paths.size() == 1) {
            consensusTransformations = new ArrayList<>(paths.get(0).transformations);
        } else {
            // Find intersection of all paths
            Set<String> commonNames = new HashSet<>(paths.get(0).transformations.stream()
                .map(t -> t.name).toList());
            
            for (int i = 1; i < paths.size(); i++) {
                Set<String> currentNames = new HashSet<>(paths.get(i).transformations.stream()
                    .map(t -> t.name).toList());
                commonNames.retainAll(currentNames);
            }
            
            // Build consensus path
            for (UnifiedStateTransformationEngine.Path path : paths) {
                for (UnifiedStateTransformationEngine.Transformation t : path.transformations) {
                    if (commonNames.contains(t.name)) {
                        consensusTransformations.add(t);
                        commonNames.remove(t.name);  // Avoid duplicates
                    }
                }
            }
        }
        
        double cost = consensusTransformations.size();
        
        UnifiedStateTransformationEngine.Path result = new UnifiedStateTransformationEngine.Path(
            consensusTransformations, cost
        );
        
        System.out.println("  [Multi-Path Synthesizer] Consensus: " + consensusTransformations.size() + " transformations");
        return result;
    }
    
    /**
     * Ensemble - combine all paths
     */
    private UnifiedStateTransformationEngine.Path ensemble(
        List<UnifiedStateTransformationEngine.Path> paths
    ) {
        // Combine all transformations, removing duplicates
        Set<String> seen = new HashSet<>();
        List<UnifiedStateTransformationEngine.Transformation> allTransformations = new ArrayList<>();
        
        for (UnifiedStateTransformationEngine.Path path : paths) {
            for (UnifiedStateTransformationEngine.Transformation t : path.transformations) {
                if (!seen.contains(t.name)) {
                    seen.add(t.name);
                    allTransformations.add(t);
                }
            }
        }
        
        double cost = allTransformations.size();
        
        UnifiedStateTransformationEngine.Path result = new UnifiedStateTransformationEngine.Path(
            allTransformations, cost
        );
        
        System.out.println("  [Multi-Path Synthesizer] Ensemble: " + allTransformations.size() + " transformations");
        return result;
    }
    
    /**
     * Calculate similarity between two paths
     */
    public double calculateSimilarity(
        UnifiedStateTransformationEngine.Path path1,
        UnifiedStateTransformationEngine.Path path2
    ) {
        if (path1 == null || path2 == null) {
            return 0.0;
        }
        
        Set<String> set1 = new HashSet<>(path1.transformations.stream()
            .map(t -> t.name).toList());
        Set<String> set2 = new HashSet<>(path2.transformations.stream()
            .map(t -> t.name).toList());
        
        // Jaccard similarity
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        if (union.isEmpty()) {
            return 1.0;  // Both empty
        }
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * Generate cross-validation report
     */
    public String generateCrossValidationReport(
        List<UnifiedStateTransformationEngine.Path> paths
    ) {
        StringBuilder report = new StringBuilder();
        report.append("Cross-Validation Report\n");
        report.append("======================\n");
        report.append("Number of paths: ").append(paths.size()).append("\n\n");
        
        // Calculate pairwise similarities
        double totalSimilarity = 0;
        int comparisons = 0;
        
        for (int i = 0; i < paths.size(); i++) {
            for (int j = i + 1; j < paths.size(); j++) {
                double similarity = calculateSimilarity(paths.get(i), paths.get(j));
                totalSimilarity += similarity;
                comparisons++;
                report.append(String.format("Path %d vs Path %d: %.2f similarity\n", 
                    i, j, similarity));
            }
        }
        
        if (comparisons > 0) {
            double avgSimilarity = totalSimilarity / comparisons;
            report.append(String.format("\nAverage similarity: %.2f\n", avgSimilarity));
        }
        
        return report.toString();
    }
    
    /**
     * Get status
     */
    public String getStatus() {
        return initialized ? "READY" : "NOT INITIALIZED";
    }
    
    /**
     * Shutdown
     */
    public void shutdown() {
        initialized = false;
        System.out.println("  [Multi-Path Synthesizer] Shutdown complete");
    }
}
