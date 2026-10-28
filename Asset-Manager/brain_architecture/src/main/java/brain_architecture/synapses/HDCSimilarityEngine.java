package brain_architecture.synapses;

import java.util.*;

/**
 * HDCSimilarityEngine - High-level interface for HDC similarity operations.
 * 
 * Calculates similarity using Hamming distance formula.
 * Similarity = 1 - (Hamming distance / 16,384)
 */
public class HDCSimilarityEngine {
    
    private static final int DIMENSION = 16384;
    private final double similarityThreshold;
    private final Map<String, Double> cache;
    
    public HDCSimilarityEngine() {
        this(0.7);
    }
    
    public HDCSimilarityEngine(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
        this.cache = new HashMap<>();
    }
    
    /**
     * Calculate similarity between two HDC vectors using Hamming distance.
     */
    public double calculateSimilarity(BitSet vectorA, BitSet vectorB) {
        // Check cache
        String cacheKey = vectorA.hashCode() + "_" + vectorB.hashCode();
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        
        // Calculate Hamming distance
        BitSet xor = (BitSet) vectorA.clone();
        xor.xor(vectorB);
        int hammingDistance = xor.cardinality();
        
        // Calculate similarity
        double similarity = 1.0 - (double) hammingDistance / DIMENSION;
        
        // Cache result
        cache.put(cacheKey, similarity);
        
        return similarity;
    }
    
    /**
     * Find top-K most similar vectors to a query.
     */
    public List<SimilarityResult> findTopK(BitSet query, Map<String, BitSet> vectors, int k) {
        List<SimilarityResult> results = new ArrayList<>();
        
        for (Map.Entry<String, BitSet> entry : vectors.entrySet()) {
            double similarity = calculateSimilarity(query, entry.getValue());
            results.add(new SimilarityResult(entry.getKey(), similarity));
        }
        
        // Sort by similarity (descending)
        results.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));
        
        // Return top-K
        return results.subList(0, Math.min(k, results.size()));
    }
    
    /**
     * Find all vectors above similarity threshold.
     */
    public List<SimilarityResult> findAboveThreshold(BitSet query, Map<String, BitSet> vectors) {
        List<SimilarityResult> results = new ArrayList<>();
        
        for (Map.Entry<String, BitSet> entry : vectors.entrySet()) {
            double similarity = calculateSimilarity(query, entry.getValue());
            if (similarity >= similarityThreshold) {
                results.add(new SimilarityResult(entry.getKey(), similarity));
            }
        }
        
        // Sort by similarity (descending)
        results.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));
        
        return results;
    }
    
    /**
     * Get the similarity threshold.
     */
    public double getSimilarityThreshold() {
        return similarityThreshold;
    }
    
    /**
     * Set the similarity threshold.
     */
    public void setSimilarityThreshold(double threshold) {
        // Clear cache when threshold changes
        cache.clear();
    }
    
    /**
     * Clear the similarity cache.
     */
    public void clearCache() {
        cache.clear();
    }
    
    /**
     * SimilarityResult - Holds a concept name and its similarity score.
     */
    public static class SimilarityResult {
        private final String conceptName;
        private final double similarity;
        
        public SimilarityResult(String conceptName, double similarity) {
            this.conceptName = conceptName;
            this.similarity = similarity;
        }
        
        public String getConceptName() {
            return conceptName;
        }
        
        public double getSimilarity() {
            return similarity;
        }
        
        @Override
        public String toString() {
            return String.format("%s: %.4f", conceptName, similarity);
        }
    }
}
