package brain_architecture.orchestration.synthesis;

import brain_architecture.core.BackendInterface;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Result Synthesizer for ensemble processing and consensus building.
 * 
 * This component combines results from multiple backends to improve accuracy,
 * confidence, and reliability. It implements ensemble methods, cross-validation,
 * and consensus building algorithms.
 */
public class ResultSynthesizer {
    
    private final SynthesisConfig config;
    
    public ResultSynthesizer() {
        this.config = new SynthesisConfig();
    }
    
    /**
     * Synthesize results from multiple backends into a single result
     * 
     * @param results List of query results from different backends
     * @param synthesisMethod The synthesis method to use
     * @return Synthesized result
     */
    public SynthesizedResult synthesize(List<BackendInterface.QueryResult> results, SynthesisMethod synthesisMethod) {
        if (results == null || results.isEmpty()) {
            return new SynthesizedResult(
                "",
                0.0,
                0,
                false,
                "No results to synthesize",
                Collections.emptyList(),
                Map.of()
            );
        }
        
        // Filter out failed results
        List<BackendInterface.QueryResult> successfulResults = results.stream()
            .filter(BackendInterface.QueryResult::isSuccess)
            .collect(Collectors.toList());
        
        if (successfulResults.isEmpty()) {
            return new SynthesizedResult(
                "",
                0.0,
                0,
                false,
                "All backend results failed",
                results,
                Map.of()
            );
        }
        
        // Apply synthesis method
        return switch (synthesisMethod) {
            case BEST_SINGLE -> synthesizeBestSingle(successfulResults);
            case MAJORITY_VOTE -> synthesizeMajorityVote(successfulResults);
            case WEIGHTED_AVERAGE -> synthesizeWeightedAverage(successfulResults);
            case CONSENSUS -> synthesizeConsensus(successfulResults);
            case ENSEMBLE -> synthesizeEnsemble(successfulResults);
        };
    }
    
    /**
     * Synthesize by selecting the best single result (highest confidence)
     */
    private SynthesizedResult synthesizeBestSingle(List<BackendInterface.QueryResult> results) {
        BackendInterface.QueryResult best = results.stream()
            .max(Comparator.comparingDouble(BackendInterface.QueryResult::getConfidence))
            .orElse(results.get(0));
        
        return new SynthesizedResult(
            best.getContent(),
            best.getConfidence(),
            best.getLatencyMs(),
            true,
            "Best single result selected",
            results,
            Map.of("method", "best_single", "selected_backend", best.getMetadata().get("backend"))
        );
    }
    
    /**
     * Synthesize by majority vote (for categorical/discrete outputs)
     */
    private SynthesizedResult synthesizeMajorityVote(List<BackendInterface.QueryResult> results) {
        // Group similar responses
        Map<String, Long> responseCounts = results.stream()
            .collect(Collectors.groupingBy(
                BackendInterface.QueryResult::getContent,
                Collectors.counting()
            ));
        
        // Find most common response
        String majorityResponse = responseCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(results.get(0).getContent());
        
        // Calculate confidence based on agreement
        long count = responseCounts.getOrDefault(majorityResponse, 0L);
        double agreement = (double) count / results.size();
        double confidence = 0.5 + (agreement * 0.5);
        
        // Calculate average latency
        double avgLatency = results.stream()
            .mapToLong(BackendInterface.QueryResult::getLatencyMs)
            .average()
            .orElse(0.0);
        
        return new SynthesizedResult(
            majorityResponse,
            confidence,
            (long) avgLatency,
            true,
            "Majority vote synthesis",
            results,
            Map.of(
                "method", "majority_vote",
                "agreement", agreement,
                "vote_count", count
            )
        );
    }
    
    /**
     * Synthesize by weighted average (for numerical/continuous outputs)
     */
    private SynthesizedResult synthesizeWeightedAverage(List<BackendInterface.QueryResult> results) {
        // For text, this is a weighted concatenation based on confidence
        StringBuilder weightedContent = new StringBuilder();
        double totalWeight = results.stream().mapToDouble(BackendInterface.QueryResult::getConfidence).sum();
        
        // Sort by confidence
        List<BackendInterface.QueryResult> sorted = results.stream()
            .sorted(Comparator.comparingDouble(BackendInterface.QueryResult::getConfidence).reversed())
            .collect(Collectors.toList());
        
        // Include top results weighted by confidence
        for (BackendInterface.QueryResult result : sorted) {
            if (weightedContent.length() > 0) {
                weightedContent.append("\n\n");
            }
            weightedContent.append(result.getContent());
            
            // Stop if we've included enough content
            if (weightedContent.length() > 2000) break;
        }
        
        // Calculate weighted confidence
        double weightedConfidence = results.stream()
            .mapToDouble(r -> r.getConfidence() * (r.getConfidence() / totalWeight))
            .sum();
        
        // Calculate average latency
        double avgLatency = results.stream()
            .mapToLong(BackendInterface.QueryResult::getLatencyMs)
            .average()
            .orElse(0.0);
        
        return new SynthesizedResult(
            weightedContent.toString(),
            weightedConfidence,
            (long) avgLatency,
            true,
            "Weighted average synthesis",
            results,
            Map.of(
                "method", "weighted_average",
                "total_weight", totalWeight
            )
        );
    }
    
    /**
     * Synthesize by consensus (requires high agreement)
     */
    private SynthesizedResult synthesizeConsensus(List<BackendInterface.QueryResult> results) {
        // Calculate similarity between all results
        double avgSimilarity = calculateAverageSimilarity(results);
        
        // If similarity is below threshold, no consensus
        if (avgSimilarity < config.getConsensusThreshold()) {
            return new SynthesizedResult(
                results.get(0).getContent(),
                0.3, // Low confidence due to lack of consensus
                results.stream().mapToLong(BackendInterface.QueryResult::getLatencyMs).max().orElse(0),
                true,
                "Low consensus - using best result",
                results,
                Map.of(
                    "method", "consensus",
                    "avg_similarity", avgSimilarity,
                    "consensus_reached", false
                )
            );
        }
        
        // Consensus reached - use majority vote
        SynthesizedResult majorityResult = synthesizeMajorityVote(results);
        
        return new SynthesizedResult(
            majorityResult.getContent(),
            Math.min(majorityResult.getConfidence() + 0.2, 1.0), // Boost confidence for consensus
            majorityResult.getLatencyMs(),
            true,
            "Consensus synthesis",
            results,
            Map.of(
                "method", "consensus",
                "avg_similarity", avgSimilarity,
                "consensus_reached", true
            )
        );
    }
    
    /**
     * Synthesize by ensemble (combines multiple methods)
     */
    private SynthesizedResult synthesizeEnsemble(List<BackendInterface.QueryResult> results) {
        // If only one result, return it
        if (results.size() == 1) {
            BackendInterface.QueryResult result = results.get(0);
            return new SynthesizedResult(
                result.getContent(),
                result.getConfidence(),
                result.getLatencyMs(),
                true,
                "Single result - no ensemble needed",
                results,
                Map.of("method", "ensemble", "ensemble_size", 1)
            );
        }
        
        // If high agreement, use consensus
        double avgSimilarity = calculateAverageSimilarity(results);
        if (avgSimilarity >= config.getConsensusThreshold()) {
            return synthesizeConsensus(results);
        }
        
        // Otherwise, use weighted average
        return synthesizeWeightedAverage(results);
    }
    
    /**
     * Calculate average similarity between results
     * (Simple implementation based on content length similarity)
     */
    private double calculateAverageSimilarity(List<BackendInterface.QueryResult> results) {
        if (results.size() < 2) return 1.0;
        
        double totalSimilarity = 0.0;
        int comparisons = 0;
        
        for (int i = 0; i < results.size(); i++) {
            for (int j = i + 1; j < results.size(); j++) {
                double similarity = calculateSimilarity(
                    results.get(i).getContent(),
                    results.get(j).getContent()
                );
                totalSimilarity += similarity;
                comparisons++;
            }
        }
        
        return comparisons == 0 ? 1.0 : totalSimilarity / comparisons;
    }
    
    /**
     * Calculate similarity between two strings
     * (Simple implementation based on length ratio and common words)
     */
    public double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        
        // Length similarity
        double len1 = s1.length();
        double len2 = s2.length();
        double lengthSimilarity = 1.0 - Math.abs(len1 - len2) / Math.max(len1, len2);
        
        // Word overlap similarity
        Set<String> words1 = new HashSet<>(Arrays.asList(s1.toLowerCase().split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(s2.toLowerCase().split("\\s+")));
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        double wordSimilarity = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
        
        // Combined similarity
        return (lengthSimilarity * 0.3) + (wordSimilarity * 0.7);
    }
    
    /**
     * Cross-validate results across backends
     * 
     * @param results List of query results
     * @return Cross-validation report
     */
    public CrossValidationReport crossValidate(List<BackendInterface.QueryResult> results) {
        List<BackendInterface.QueryResult> successful = results.stream()
            .filter(BackendInterface.QueryResult::isSuccess)
            .collect(Collectors.toList());
        
        if (successful.isEmpty()) {
            return new CrossValidationReport(
                0,
                0,
                0.0,
                false,
                "No successful results to validate",
                Map.of()
            );
        }
        
        int total = results.size();
        int valid = successful.size();
        double validityRate = (double) valid / total;
        
        double avgConfidence = successful.stream()
            .mapToDouble(BackendInterface.QueryResult::getConfidence)
            .average()
            .orElse(0.0);
        
        double avgLatency = successful.stream()
            .mapToLong(BackendInterface.QueryResult::getLatencyMs)
            .average()
            .orElse(0.0);
        
        double avgSimilarity = calculateAverageSimilarity(successful);
        
        boolean validationPassed = validityRate >= config.getValidationThreshold() &&
                                  avgConfidence >= config.getMinConfidence() &&
                                  avgSimilarity >= config.getMinSimilarity();
        
        return new CrossValidationReport(
            total,
            valid,
            validityRate,
            validationPassed,
            validationPassed ? "Cross-validation passed" : "Cross-validation failed",
            Map.of(
                "avg_confidence", avgConfidence,
                "avg_latency", avgLatency,
                "avg_similarity", avgSimilarity
            )
        );
    }
    
    /**
     * Synthesis configuration
     */
    public static class SynthesisConfig {
        private double consensusThreshold = 0.7; // Minimum similarity for consensus
        private double validationThreshold = 0.8; // Minimum validity rate
        private double minConfidence = 0.6; // Minimum average confidence
        private double minSimilarity = 0.5; // Minimum average similarity
        
        public double getConsensusThreshold() { return consensusThreshold; }
        public void setConsensusThreshold(double threshold) { this.consensusThreshold = threshold; }
        
        public double getValidationThreshold() { return validationThreshold; }
        public void setValidationThreshold(double threshold) { this.validationThreshold = threshold; }
        
        public double getMinConfidence() { return minConfidence; }
        public void setMinConfidence(double confidence) { this.minConfidence = confidence; }
        
        public double getMinSimilarity() { return minSimilarity; }
        public void setMinSimilarity(double similarity) { this.minSimilarity = similarity; }
    }
    
    /**
     * Synthesis methods
     */
    public enum SynthesisMethod {
        BEST_SINGLE,       // Select best single result
        MAJORITY_VOTE,     // Majority vote for categorical outputs
        WEIGHTED_AVERAGE,  // Weighted average for continuous outputs
        CONSENSUS,         // Requires high agreement
        ENSEMBLE           // Combines multiple methods
    }
    
    /**
     * Synthesized result
     */
    public static class SynthesizedResult {
        private final String content;
        private final double confidence;
        private final long latencyMs;
        private final boolean success;
        private final String message;
        private final List<BackendInterface.QueryResult> sourceResults;
        private final Map<String, Object> metadata;
        
        public SynthesizedResult(String content, double confidence, long latencyMs,
                                boolean success, String message,
                                List<BackendInterface.QueryResult> sourceResults,
                                Map<String, Object> metadata) {
            this.content = content;
            this.confidence = confidence;
            this.latencyMs = latencyMs;
            this.success = success;
            this.message = message;
            this.sourceResults = sourceResults;
            this.metadata = metadata;
        }
        
        public String getContent() { return content; }
        public double getConfidence() { return confidence; }
        public long getLatencyMs() { return latencyMs; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<BackendInterface.QueryResult> getSourceResults() { return sourceResults; }
        public Map<String, Object> getMetadata() { return metadata; }
    }
    
    /**
     * Cross-validation report
     */
    public static class CrossValidationReport {
        private final int totalResults;
        private final int validResults;
        private final double validityRate;
        private final boolean passed;
        private final String message;
        private final Map<String, Object> metrics;
        
        public CrossValidationReport(int totalResults, int validResults, double validityRate,
                                   boolean passed, String message, Map<String, Object> metrics) {
            this.totalResults = totalResults;
            this.validResults = validResults;
            this.validityRate = validityRate;
            this.passed = passed;
            this.message = message;
            this.metrics = metrics;
        }
        
        public int getTotalResults() { return totalResults; }
        public int getValidResults() { return validResults; }
        public double getValidityRate() { return validityRate; }
        public boolean isPassed() { return passed; }
        public String getMessage() { return message; }
        public Map<String, Object> getMetrics() { return metrics; }
    }
}
