package fraymus.neural;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HDCSimilarityEngine - High-level similarity search using Hyperdimensional Computing
 * 
 * This engine provides a convenient interface for similarity search operations
 * that will be used by SentientCore for hybrid symbolic/vector reasoning.
 * 
 * CAPABILITIES:
 * - Find similar concepts by vector similarity
 * - Compose concepts via holographic binding
 * - Aggregate concepts via bundling
 * - Configurable similarity thresholds (RULE HDC-2)
 * 
 * DESIGN PRINCIPLES:
 * - Safe: Does not modify existing SentientCore structures
 * - Efficient: O(N) search where N = number of concepts
 * - Configurable: Thresholds can be adjusted per use
 * - Deterministic: No randomness
 * 
 * PHASE 1.1: HDC Integration with SentientCore
 * Part of the AGI System Blueprint
 */
public class HDCSimilarityEngine {

    private final HyperdimensionalMapper mapper;
    
    // Configurable similarity threshold (RULE HDC-2)
    private volatile double similarityThreshold = 0.5;
    
    // Cache for similarity results (optional, for performance)
    private final Map<String, List<HyperdimensionalMapper.SimilarityResult>> similarityCache = 
        new ConcurrentHashMap<>();
    
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

    /**
     * Create a new HDCSimilarityEngine.
     * 
     * @param mapper The HyperdimensionalMapper to use
     */
    public HDCSimilarityEngine(HyperdimensionalMapper mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null");
        }
        this.mapper = mapper;
    }

    /**
     * Create a new HDCSimilarityEngine with a new mapper.
     */
    public HDCSimilarityEngine() {
        this(new HyperdimensionalMapper());
    }

    /**
     * Find concepts similar to a query concept.
     * 
     * Uses the configured similarity threshold.
     * 
     * @param query The query concept
     * @return List of similar concepts with similarity scores
     */
    public List<HyperdimensionalMapper.SimilarityResult> findSimilar(String query) {
        return findSimilar(query, similarityThreshold);
    }

    /**
     * Find concepts similar to a query concept with a custom threshold.
     * 
     * @param query The query concept
     * @param threshold Minimum similarity [0, 1]
     * @return List of similar concepts with similarity scores
     */
    public List<HyperdimensionalMapper.SimilarityResult> findSimilar(String query, double threshold) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }
        if (threshold < 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be in [0, 1]");
        }
        
        // Check cache
        String cacheKey = query + ":" + threshold;
        List<HyperdimensionalMapper.SimilarityResult> cached = similarityCache.get(cacheKey);
        if (cached != null) {
            cacheHits.incrementAndGet();
            return cached;
        }
        
        cacheMisses.incrementAndGet();
        
        // Perform search
        List<HyperdimensionalMapper.SimilarityResult> results = mapper.findSimilar(query, threshold);
        
        // Cache results
        similarityCache.put(cacheKey, results);
        
        return results;
    }

    /**
     * Find the top K most similar concepts.
     * 
     * @param query The query concept
     * @param k Number of results to return
     * @return Top K similar concepts
     */
    public List<HyperdimensionalMapper.SimilarityResult> findTopK(String query, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("K must be positive");
        }
        
        List<HyperdimensionalMapper.SimilarityResult> all = findSimilar(query, 0.0); // Get all
        
        // Return top K
        return all.subList(0, Math.min(k, all.size()));
    }

    /**
     * Compose two concepts via holographic binding.
     * 
     * This creates a new concept that represents the composition of the two.
     * For example: bind("red", "circle") → concept for "red circle"
     * 
     * @param conceptA First concept
     * @param conceptB Second concept
     * @return The composed concept's vector
     */
    public BitSet compose(String conceptA, String conceptB) {
        BitSet vectorA = mapper.mapConcept(conceptA);
        BitSet vectorB = mapper.mapConcept(conceptB);
        return mapper.bind(vectorA, vectorB);
    }

    /**
     * Compose multiple concepts via sequential binding.
     * 
     * @param concepts The concepts to compose
     * @return The composed vector
     */
    public BitSet composeMultiple(String... concepts) {
        if (concepts == null || concepts.length == 0) {
            throw new IllegalArgumentException("Concepts cannot be null or empty");
        }
        
        BitSet result = mapper.mapConcept(concepts[0]);
        
        for (int i = 1; i < concepts.length; i++) {
            BitSet next = mapper.mapConcept(concepts[i]);
            result = mapper.bind(result, next);
        }
        
        return result;
    }

    /**
     * Aggregate concepts via bundling.
     * 
     * This creates a vector that represents the "essence" of multiple concepts.
     * For example: bundle(["red", "blue", "green"]) → concept for "colors"
     * 
     * @param concepts The concepts to bundle
     * @return The bundled vector
     */
    public BitSet aggregate(String... concepts) {
        if (concepts == null || concepts.length == 0) {
            throw new IllegalArgumentException("Concepts cannot be null or empty");
        }
        
        List<BitSet> vectors = new ArrayList<>();
        for (String concept : concepts) {
            vectors.add(mapper.mapConcept(concept));
        }
        
        return mapper.bundle(vectors);
    }

    /**
     * Check if two concepts are similar.
     * 
     * @param conceptA First concept
     * @param conceptB Second concept
     * @return True if similarity >= threshold
     */
    public boolean areSimilar(String conceptA, String conceptB) {
        BitSet vectorA = mapper.mapConcept(conceptA);
        BitSet vectorB = mapper.mapConcept(conceptB);
        double sim = mapper.similarity(vectorA, vectorB);
        return sim >= similarityThreshold;
    }

    /**
     * Get the similarity between two concepts.
     * 
     * @param conceptA First concept
     * @param conceptB Second concept
     * @return Similarity score [0, 1]
     */
    public double getSimilarity(String conceptA, String conceptB) {
        BitSet vectorA = mapper.mapConcept(conceptA);
        BitSet vectorB = mapper.mapConcept(conceptB);
        return mapper.similarity(vectorA, vectorB);
    }

    /**
     * Set the similarity threshold.
     * 
     * RULE HDC-2: Similarity thresholds must be configurable
     * 
     * @param threshold The new threshold [0, 1]
     */
    public void setSimilarityThreshold(double threshold) {
        if (threshold < 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be in [0, 1]");
        }
        this.similarityThreshold = threshold;
        // Clear cache when threshold changes
        similarityCache.clear();
    }

    /**
     * Get the current similarity threshold.
     * 
     * @return The current threshold
     */
    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    /**
     * Clear the similarity cache.
     */
    public void clearCache() {
        similarityCache.clear();
        cacheHits.set(0);
        cacheMisses.set(0);
    }

    /**
     * Get cache statistics.
     * 
     * @return Cache hit rate and sizes
     */
    public String getCacheStats() {
        int total = cacheHits.get() + cacheMisses.get();
        double hitRate = total > 0 ? (double) cacheHits.get() / total : 0.0;
        
        return String.format(
            "Cache Stats:\n" +
            "  Hits: %,d\n" +
            "  Misses: %,d\n" +
            "  Hit rate: %.2f%%\n" +
            "  Cache size: %,d",
            cacheHits.get(),
            cacheMisses.get(),
            hitRate * 100,
            similarityCache.size()
        );
    }

    /**
     * Get the underlying mapper.
     * 
     * @return The HyperdimensionalMapper
     */
    public HyperdimensionalMapper getMapper() {
        return mapper;
    }

    /**
     * Get overall statistics.
     * 
     * @return Combined mapper and engine statistics
     */
    public String getStats() {
        return mapper.getStats() + "\n" + getCacheStats();
    }
}
