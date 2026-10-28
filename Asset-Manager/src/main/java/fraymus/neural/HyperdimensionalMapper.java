package fraymus.neural;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HyperdimensionalMapper - Maps concepts to 16,384-D binary vectors
 * 
 * This class implements Hyperdimensional Computing (HDC) for concept representation.
 * HDC provides:
 * - High-dimensional vector representations (16,384 dimensions)
 * - Holographic binding via XOR (composition)
 * - Bundling via superposition (aggregation)
 * - Similarity via Hamming distance
 * - Noise robustness and graceful degradation
 * 
 * DESIGN PRINCIPLES:
 * - Deterministic: Same concept always maps to same vector
 * - Reproducible: No randomness, uses hash functions
 * - Efficient: O(1) mapping, O(D) similarity where D=16384
 * - Safe: No modification of existing SentientCore structures
 * 
 * PHASE 1.1: HDC Integration with SentientCore
 * Part of the AGI System Blueprint
 */
public class HyperdimensionalMapper {

    // Vector dimension - must be 16,384 (RULE HDC-1)
    public static final int DIMENSION = 16384;
    
    // Similarity threshold (default: 50% match = Hamming distance < 8192)
    private static final int DEFAULT_SIMILARITY_THRESHOLD = DIMENSION / 2;
    
    // Concept → Vector mapping (thread-safe)
    private final Map<String, BitSet> conceptToVector = new ConcurrentHashMap<>();
    
    // Vector → Concept reverse mapping (for similarity search)
    private final Map<String, BitSet> vectorToConcept = new ConcurrentHashMap<>();
    
    // Statistics
    private final AtomicInteger conceptsMapped = new AtomicInteger(0);
    private final AtomicInteger bindingsPerformed = new AtomicInteger(0);
    private final AtomicInteger bundlesPerformed = new AtomicInteger(0);

    /**
     * Map a concept string to a 16,384-D binary vector.
     * 
     * Uses deterministic hashing to ensure the same concept always maps
     * to the same vector. This is crucial for reproducibility.
     * 
     * @param concept The concept string to map
     * @return The 16,384-D binary vector as a BitSet
     */
    public BitSet mapConcept(String concept) {
        if (concept == null || concept.isEmpty()) {
            throw new IllegalArgumentException("Concept cannot be null or empty");
        }
        
        // Check cache
        BitSet cached = conceptToVector.get(concept);
        if (cached != null) {
            return cached;
        }
        
        // Generate deterministic vector using hash
        BitSet vector = generateDeterministicVector(concept);
        
        // Cache mappings
        conceptToVector.put(concept, vector);
        vectorToConcept.put(concept, vector); // Use concept as key for simplicity
        
        conceptsMapped.incrementAndGet();
        
        return vector;
    }

    /**
     * Generate a deterministic 16,384-D binary vector from a concept string.
     * 
     * Uses SHA-256 hash to generate pseudo-random bits deterministically.
     * The same input always produces the same output.
     * 
     * @param concept The concept string
     * @return The binary vector
     */
    private BitSet generateDeterministicVector(String concept) {
        BitSet vector = new BitSet(DIMENSION);
        
        // Use concept hash to seed deterministic bit generation
        int hash = concept.hashCode();
        
        // Simple but deterministic hash-based bit generation
        // For production, could use SHA-256 for better distribution
        Random deterministic = new Random(hash);
        
        for (int i = 0; i < DIMENSION; i++) {
            // Generate bit deterministically
            if (deterministic.nextBoolean()) {
                vector.set(i);
            }
        }
        
        return vector;
    }

    /**
     * Get the vector for a concept (without creating if not exists).
     * 
     * @param concept The concept string
     * @return The vector, or null if not mapped
     */
    public BitSet getVector(String concept) {
        return conceptToVector.get(concept);
    }

    /**
     * Check if a concept has been mapped.
     * 
     * @param concept The concept string
     * @return True if mapped, false otherwise
     */
    public boolean isMapped(String concept) {
        return conceptToVector.containsKey(concept);
    }

    /**
     * Holographic binding: Combine two vectors via XOR.
     * 
     * This is reversible: unbind(A, A⊕B) = B
     * Used for composition: vector("red") ⊕ vector("circle") = vector("red circle")
     * 
     * RULE HDC-3: Binding must be reversible
     * 
     * @param vectorA First vector
     * @param vectorB Second vector
     * @return XOR of the two vectors
     */
    public BitSet bind(BitSet vectorA, BitSet vectorB) {
        if (vectorA == null || vectorB == null) {
            throw new IllegalArgumentException("Vectors cannot be null");
        }
        if (vectorA.size() != DIMENSION || vectorB.size() != DIMENSION) {
            throw new IllegalArgumentException("Vectors must be " + DIMENSION + " dimensions");
        }
        
        BitSet result = (BitSet) vectorA.clone();
        result.xor(vectorB);
        
        bindingsPerformed.incrementAndGet();
        
        return result;
    }

    /**
     * Holographic unbinding: Reverse the binding operation.
     * 
     * unbind(A, A⊕B) = B
     * 
     * @param vectorA The vector that was bound
     * @param boundVector The result of the binding
     * @return The unbound vector
     */
    public BitSet unbind(BitSet vectorA, BitSet boundVector) {
        // XOR is its own inverse, so unbind is the same as bind
        return bind(vectorA, boundVector);
    }

    /**
     * Bundling: Superposition of multiple vectors via majority vote.
     * 
     * Used for aggregation: bundle of {vector("red"), vector("blue")} 
     * represents "colors" in general.
     * 
     * @param vectors The vectors to bundle
     * @return The bundled vector
     */
    public BitSet bundle(Collection<BitSet> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            throw new IllegalArgumentException("Cannot bundle empty collection");
        }
        
        // Count bits at each position
        int[] bitCounts = new int[DIMENSION];
        
        for (BitSet vector : vectors) {
            if (vector == null || vector.size() != DIMENSION) {
                throw new IllegalArgumentException("All vectors must be " + DIMENSION + " dimensions");
            }
            
            for (int i = vector.nextSetBit(0); i >= 0; i = vector.nextSetBit(i + 1)) {
                bitCounts[i]++;
            }
        }
        
        // Majority vote: set bit if > half the vectors have it set
        BitSet result = new BitSet(DIMENSION);
        int threshold = vectors.size() / 2;
        
        for (int i = 0; i < DIMENSION; i++) {
            if (bitCounts[i] > threshold) {
                result.set(i);
            }
        }
        
        bundlesPerformed.incrementAndGet();
        
        return result;
    }

    /**
     * Compute similarity between two vectors using Hamming distance.
     * 
     * Similarity = 1 - (Hamming distance / dimension)
     * Range: [0, 1], where 1 = identical, 0 = completely different
     * 
     * @param vectorA First vector
     * @param vectorB Second vector
     * @return Similarity score [0, 1]
     */
    public double similarity(BitSet vectorA, BitSet vectorB) {
        if (vectorA == null || vectorB == null) {
            throw new IllegalArgumentException("Vectors cannot be null");
        }
        if (vectorA.size() != DIMENSION || vectorB.size() != DIMENSION) {
            throw new IllegalArgumentException("Vectors must be " + DIMENSION + " dimensions");
        }
        
        int hammingDistance = hammingDistance(vectorA, vectorB);
        return 1.0 - ((double) hammingDistance / DIMENSION);
    }

    /**
     * Compute Hamming distance between two vectors.
     * 
     * Hamming distance = number of differing bits
     * 
     * @param vectorA First vector
     * @param vectorB Second vector
     * @return Hamming distance [0, DIMENSION]
     */
    public int hammingDistance(BitSet vectorA, BitSet vectorB) {
        BitSet xor = (BitSet) vectorA.clone();
        xor.xor(vectorB);
        return xor.cardinality();
    }

    /**
     * Find concepts similar to a query concept.
     * 
     * Returns concepts with similarity above the threshold.
     * 
     * @param query The query concept
     * @param threshold Minimum similarity (default: 0.5)
     * @return List of similar concepts with their similarity scores
     */
    public List<SimilarityResult> findSimilar(String query, double threshold) {
        BitSet queryVector = mapConcept(query);
        
        List<SimilarityResult> results = new ArrayList<>();
        
        for (Map.Entry<String, BitSet> entry : conceptToVector.entrySet()) {
            if (entry.getKey().equals(query)) continue; // Skip self
            
            double sim = similarity(queryVector, entry.getValue());
            if (sim >= threshold) {
                results.add(new SimilarityResult(entry.getKey(), sim));
            }
        }
        
        // Sort by similarity (descending)
        results.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        
        return results;
    }

    /**
     * Find concepts similar to a query concept using default threshold.
     * 
     * @param query The query concept
     * @return List of similar concepts with their similarity scores
     */
    public List<SimilarityResult> findSimilar(String query) {
        return findSimilar(query, 0.5);
    }

    /**
     * Result class for similarity search.
     */
    public static class SimilarityResult {
        public final String concept;
        public final double similarity;
        
        public SimilarityResult(String concept, double similarity) {
            this.concept = concept;
            this.similarity = similarity;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%.3f)", concept, similarity);
        }
    }

    /**
     * Get statistics about the mapper.
     */
    public String getStats() {
        return String.format(
            "HyperdimensionalMapper Stats:\n" +
            "  Dimension: %,d\n" +
            "  Concepts mapped: %,d\n" +
            "  Bindings performed: %,d\n" +
            "  Bundles performed: %,d",
            DIMENSION,
            conceptsMapped.get(),
            bindingsPerformed.get(),
            bundlesPerformed.get()
        );
    }

    /**
     * Get the number of concepts mapped.
     */
    public int getConceptCount() {
        return conceptsMapped.get();
    }

    /**
     * Clear all mappings (use with caution).
     * 
     * This is primarily for testing or reset scenarios.
     */
    public void clear() {
        conceptToVector.clear();
        vectorToConcept.clear();
        conceptsMapped.set(0);
        bindingsPerformed.set(0);
        bundlesPerformed.set(0);
    }
}
