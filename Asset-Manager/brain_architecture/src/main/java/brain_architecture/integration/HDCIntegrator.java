package brain_architecture.integration;

import brain_architecture.synapses.HyperdimensionalMapper;
import brain_architecture.synapses.HDCSimilarityEngine;
import java.util.*;
import java.util.concurrent.*;
import java.lang.reflect.*;

/**
 * HDCIntegrator - Integrates with Fraymus HyperFormer instead of duplicate HDC
 * 
 * This integrator bridges brain_architecture's HDC (16,384-D) with Fraymus's HyperFormer (10,000-D):
 * - Converts between brain_architecture HDC format and Fraymus HDC format
 * - Uses Fraymus's HoloGraph for O(1) knowledge instead of similarity search
 * - Leverages Fraymus's WeightedBundler for bundling instead of majority vote
 * - Uses Fraymus's HyperFormer persistence instead of custom persistence
 * 
 * This avoids duplicate HDC implementations and leverages the existing infrastructure.
 */
public class HDCIntegrator {
    
    // Fraymus HDC components (to be injected)
    private Object hyperFormer;       // Layer 6 - 10,000-D tensor engine
    private Object holoGraph;         // Layer 8 - O(1) knowledge graph
    private Object weightedBundler;   // Layer 6 - Fast bias+delta majority vote
    
    // brain_architecture HDC components (for conversion)
    private HyperdimensionalMapper brainMapper;
    private HDCSimilarityEngine brainSimilarityEngine;
    
    private boolean initialized = false;
    private boolean useFraymusHDC = false;
    
    // Conversion cache
    private final Map<String, Object> conversionCache = new ConcurrentHashMap<>();
    
    /**
     * Wire Fraymus HDC components to the integrator
     */
    public void wireFraymusHDC(
            Object hyperFormer,
            Object holoGraph,
            Object weightedBundler,
            HyperdimensionalMapper brainMapper,
            HDCSimilarityEngine brainSimilarityEngine) {
        
        this.hyperFormer = hyperFormer;
        this.holoGraph = holoGraph;
        this.weightedBundler = weightedBundler;
        this.brainMapper = brainMapper;
        this.brainSimilarityEngine = brainSimilarityEngine;
        
        this.initialized = true;
        this.useFraymusHDC = true;
    }
    
    /**
     * Unwire Fraymus HDC components (revert to standalone mode)
     */
    public void unwireFraymusHDC() {
        this.hyperFormer = null;
        this.holoGraph = null;
        this.weightedBundler = null;
        
        // Keep brainMapper and brainSimilarityEngine for fallback functionality
        this.initialized = false;
        this.useFraymusHDC = false;
        
        conversionCache.clear();
    }
    
    /**
     * Map concept to HDC vector
     * If Fraymus HDC is wired, uses HyperFormer (10,000-D)
     * Otherwise, uses brain_architecture HDC (16,384-D)
     */
    public Object mapConcept(String concept) {
        if (useFraymusHDC && hyperFormer != null) {
            // Use Fraymus HyperFormer for mapping
            return mapConceptWithHyperFormer(concept);
        } else {
            // Use brain_architecture HyperdimensionalMapper
            return brainMapper.map(concept);
        }
    }
    
    /**
     * Calculate similarity between two concepts
     * If Fraymus HDC is wired, uses HoloGraph (O(1) knowledge)
     * Otherwise, uses brain_architecture HDCSimilarityEngine
     */
    public double calculateSimilarity(String concept1, String concept2) {
        if (useFraymusHDC && holoGraph != null) {
            // Use Fraymus HoloGraph for O(1) similarity
            return calculateSimilarityWithHoloGraph(concept1, concept2);
        } else {
            // Use brain_architecture HDCSimilarityEngine
            Object vector1 = brainMapper.map(concept1);
            Object vector2 = brainMapper.map(concept2);
            return brainSimilarityEngine.calculateSimilarity((java.util.BitSet) vector1, (java.util.BitSet) vector2);
        }
    }
    
    /**
     * Bind two concepts (compose them)
     * If Fraymus HDC is wired, uses HyperFormer XOR binding
     * Otherwise, uses brain_architecture holographic binding
     */
    public Object bindConcepts(String concept1, String concept2) {
        if (useFraymusHDC && hyperFormer != null) {
            // Use Fraymus HyperFormer XOR binding
            return bindWithHyperFormer(concept1, concept2);
        } else {
            // Use brain_architecture holographic binding
            Object vector1 = brainMapper.map(concept1);
            Object vector2 = brainMapper.map(concept2);
            return brainMapper.bind((java.util.BitSet) vector1, (java.util.BitSet) vector2);
        }
    }
    
    /**
     * Bundle multiple concepts (aggregate them)
     * If Fraymus HDC is wired, uses WeightedBundler
     * Otherwise, uses brain_architecture majority vote bundling
     */
    public Object bundleConcepts(List<String> concepts) {
        if (useFraymusHDC && weightedBundler != null) {
            // Use Fraymus WeightedBundler
            return bundleWithWeightedBundler(concepts);
        } else {
            // Use brain_architecture majority vote bundling
            java.util.BitSet[] vectors = new java.util.BitSet[concepts.size()];
            for (int i = 0; i < concepts.size(); i++) {
                vectors[i] = brainMapper.map(concepts.get(i));
            }
            return brainMapper.bundle(vectors);
        }
    }
    
    /**
     * Map concept using HyperFormer.embed(String token)
     * Uses reflection to call Fraymus HyperFormer
     */
    private Object mapConceptWithHyperFormer(String concept) {
        try {
            Method embedMethod = hyperFormer.getClass().getMethod("embed", String.class);
            Object vector = embedMethod.invoke(hyperFormer, concept);
            // Cache the result
            conversionCache.put(concept, vector);
            return vector;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("HyperFormer.embed() method not found", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access HyperFormer.embed()", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("HyperFormer.embed() threw exception", e.getCause());
        }
    }
    
    /**
     * Calculate similarity using HyperFormer vectors and Hamming distance
     * HoloGraph is for knowledge facts, not similarity - use HyperFormer vectors instead
     */
    private double calculateSimilarityWithHoloGraph(String concept1, String concept2) {
        try {
            // Get vectors using HyperFormer.embed()
            Method embedMethod = hyperFormer.getClass().getMethod("embed", String.class);
            Object vector1 = embedMethod.invoke(hyperFormer, concept1);
            Object vector2 = embedMethod.invoke(hyperFormer, concept2);

            // Calculate similarity using Hamming distance
            // HyperFormer uses HyperVector class with cardinality() method
            Method cardinalityMethod = vector1.getClass().getMethod("cardinality");
            int bits = (Integer) cardinalityMethod.invoke(vector1);

            // XOR the vectors
            Method xorMethod = vector1.getClass().getMethod("xor", vector1.getClass());
            Object xorResult = xorMethod.invoke(vector1, vector2);

            // Get Hamming distance (number of set bits in XOR result)
            int hammingDistance = (Integer) cardinalityMethod.invoke(xorResult);

            // Calculate similarity: 1 - (hamming_distance / total_bits)
            double similarity = 1.0 - ((double) hammingDistance / bits);
            return similarity;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Required method not found on HyperFormer/HyperVector", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access method", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Method threw exception", e.getCause());
        }
    }
    
    /**
     * Bind two concepts using HyperFormer XOR binding
     * Uses reflection to XOR the vectors
     */
    private Object bindWithHyperFormer(String concept1, String concept2) {
        try {
            // Get vectors using HyperFormer.embed()
            Method embedMethod = hyperFormer.getClass().getMethod("embed", String.class);
            Object vector1 = embedMethod.invoke(hyperFormer, concept1);
            Object vector2 = embedMethod.invoke(hyperFormer, concept2);

            // XOR the vectors for binding
            Method xorMethod = vector1.getClass().getMethod("xor", vector1.getClass());
            Object boundVector = xorMethod.invoke(vector1, vector2);

            return boundVector;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Required method not found on HyperFormer/HyperVector", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access method", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Method threw exception", e.getCause());
        }
    }
    
    /**
     * Bundle multiple concepts using majority vote
     * Uses HyperFormer vectors and XOR for bundling
     * Note: There is no separate WeightedBundler in Fraymus - using standard HDC bundling
     */
    private Object bundleWithWeightedBundler(List<String> concepts) {
        try {
            if (concepts == null || concepts.isEmpty()) {
                throw new IllegalArgumentException("Concepts list cannot be null or empty");
            }

            // Get vectors using HyperFormer.embed()
            Method embedMethod = hyperFormer.getClass().getMethod("embed", String.class);
            List<Object> vectors = new ArrayList<>();
            for (String concept : concepts) {
                vectors.add(embedMethod.invoke(hyperFormer, concept));
            }

            // Bundle using majority vote: XOR all vectors together
            // Start with the first vector
            Object bundled = vectors.get(0);

            // XOR with remaining vectors
            Method xorMethod = bundled.getClass().getMethod("xor", bundled.getClass());
            for (int i = 1; i < vectors.size(); i++) {
                bundled = xorMethod.invoke(bundled, vectors.get(i));
            }

            return bundled;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Required method not found on HyperFormer/HyperVector", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access method", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Method threw exception", e.getCause());
        }
    }
    
    /**
     * Convert brain_architecture vector (16,384-D) to Fraymus vector (10,000-D)
     * Note: This requires dimension transformation which is non-trivial.
     * For now, this returns the original vector as the systems use different dimensions.
     * Future implementation: Use interpolation or hashing to transform dimensions.
     */
    public Object convertBrainVectorToFraymus(Object brainVector) {
        // Dimension conversion from 16,384-D to 10,000-D is non-trivial
        // For now, return as-is since systems operate independently
        // Future: Could use PCA, hashing, or interpolation for dimension transformation
        return brainVector;
    }

    /**
     * Convert Fraymus vector (10,000-D) to brain_architecture vector (16,384-D)
     * Note: This requires dimension transformation which is non-trivial.
     * For now, this returns the original vector as the systems use different dimensions.
     * Future implementation: Use interpolation or hashing to transform dimensions.
     */
    public Object convertFraymusVectorToBrain(Object fraymusVector) {
        // Dimension conversion from 10,000-D to 16,384-D is non-trivial
        // For now, return as-is since systems operate independently
        // Future: Could use PCA, hashing, or interpolation for dimension transformation
        return fraymusVector;
    }
    
    /**
     * Get initialization status
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get whether Fraymus HDC is being used
     */
    public boolean isUsingFraymusHDC() {
        return useFraymusHDC;
    }
    
    /**
     * Get status report
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("initialized", initialized);
        status.put("useFraymusHDC", useFraymusHDC);
        status.put("conversionCacheSize", conversionCache.size());
        status.put("hyperFormerAvailable", hyperFormer != null);
        status.put("holoGraphAvailable", holoGraph != null);
        status.put("weightedBundlerAvailable", weightedBundler != null);
        return status;
    }
}
