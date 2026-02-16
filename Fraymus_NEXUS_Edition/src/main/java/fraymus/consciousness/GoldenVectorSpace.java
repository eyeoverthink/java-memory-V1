package fraymus.consciousness;

import java.util.*;

/**
 * Golden Vector Space
 * 
 * Concept memory distributed using Golden Angle (137.5°) separation.
 * Prevents concept crowding and ensures perfect memory efficiency.
 * 
 * Like seeds in a sunflower - optimal packing using φ.
 */
public class GoldenVectorSpace {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double GOLDEN_ANGLE = 137.5077640500378; // degrees
    private static final double GOLDEN_ANGLE_RAD = Math.toRadians(GOLDEN_ANGLE);
    
    private final int dimensions;
    private final Map<String, double[]> concepts;
    private final List<double[]> vectorSpace;
    
    public GoldenVectorSpace(int dimensions) {
        this.dimensions = dimensions;
        this.concepts = new HashMap<>();
        this.vectorSpace = new ArrayList<>();
    }
    
    /**
     * Add a concept to the vector space using golden angle placement
     */
    public void addConcept(String name, double[] embedding) {
        if (embedding.length != dimensions) {
            throw new IllegalArgumentException("Embedding dimension mismatch");
        }
        
        // Normalize embedding
        double[] normalized = normalize(embedding);
        
        // Apply golden angle rotation for optimal placement
        double[] positioned = applyGoldenPlacement(normalized, vectorSpace.size());
        
        concepts.put(name, positioned);
        vectorSpace.add(positioned);
    }
    
    /**
     * Find most similar concept using golden angle cosine similarity
     */
    public String findSimilar(double[] query) {
        double[] normalizedQuery = normalize(query);
        
        double maxSimilarity = -1;
        String mostSimilar = null;
        
        for (Map.Entry<String, double[]> entry : concepts.entrySet()) {
            double similarity = goldenCosineSimilarity(normalizedQuery, entry.getValue());
            
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilar = entry.getKey();
            }
        }
        
        return mostSimilar;
    }
    
    /**
     * Find all concepts within a resonance threshold
     */
    public List<String> findResonant(double[] query, double threshold) {
        double[] normalizedQuery = normalize(query);
        List<String> resonant = new ArrayList<>();
        
        for (Map.Entry<String, double[]> entry : concepts.entrySet()) {
            double similarity = goldenCosineSimilarity(normalizedQuery, entry.getValue());
            
            if (similarity >= threshold) {
                resonant.add(entry.getKey());
            }
        }
        
        return resonant;
    }
    
    /**
     * Golden Cosine Similarity
     * Standard: cos(θ) = A·B / (||A|| ||B||)
     * Golden: Weighted by φ-harmonic distance
     */
    private double goldenCosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        
        for (int i = 0; i < dimensions; i++) {
            // Apply φ-weighting to each dimension
            double phiWeight = Math.pow(PHI, -(i % 8)); // Cycle every 8 dimensions
            
            dotProduct += a[i] * b[i] * phiWeight;
            normA += a[i] * a[i] * phiWeight;
            normB += b[i] * b[i] * phiWeight;
        }
        
        return dotProduct / (Math.sqrt(normA * normB) + 1e-10);
    }
    
    /**
     * Apply golden angle placement to prevent concept crowding
     */
    private double[] applyGoldenPlacement(double[] vector, int index) {
        double[] placed = new double[dimensions];
        
        // Calculate golden angle rotation for this index
        double angle = index * GOLDEN_ANGLE_RAD;
        
        // Apply rotation in pairs of dimensions (2D rotations in high-D space)
        for (int i = 0; i < dimensions - 1; i += 2) {
            double cos = Math.cos(angle * (i / 2.0));
            double sin = Math.sin(angle * (i / 2.0));
            
            placed[i] = vector[i] * cos - vector[i + 1] * sin;
            placed[i + 1] = vector[i] * sin + vector[i + 1] * cos;
        }
        
        // Handle odd dimension
        if (dimensions % 2 == 1) {
            placed[dimensions - 1] = vector[dimensions - 1];
        }
        
        return normalize(placed);
    }
    
    /**
     * Map concepts onto a sphere using Fibonacci lattice
     * This is the "sunflower seed" pattern in 3D
     */
    public double[] fibonacciSphere(int index, int total) {
        double goldenRatio = (1 + Math.sqrt(5)) / 2;
        double theta = 2 * Math.PI * index / goldenRatio;
        double phi = Math.acos(1 - 2 * (index + 0.5) / total);
        
        return new double[]{
            Math.cos(theta) * Math.sin(phi),
            Math.sin(theta) * Math.sin(phi),
            Math.cos(phi)
        };
    }
    
    /**
     * Compute concept density (how crowded the space is)
     */
    public double computeDensity() {
        if (vectorSpace.size() < 2) return 0;
        
        double totalDistance = 0;
        int comparisons = 0;
        
        for (int i = 0; i < vectorSpace.size(); i++) {
            for (int j = i + 1; j < vectorSpace.size(); j++) {
                double distance = euclideanDistance(vectorSpace.get(i), vectorSpace.get(j));
                totalDistance += distance;
                comparisons++;
            }
        }
        
        return totalDistance / comparisons;
    }
    
    /**
     * Get concept embedding
     */
    public double[] getConcept(String name) {
        return concepts.get(name);
    }
    
    /**
     * Check if concept exists
     */
    public boolean hasConcept(String name) {
        return concepts.containsKey(name);
    }
    
    /**
     * Get all concept names
     */
    public Set<String> getConceptNames() {
        return concepts.keySet();
    }
    
    /**
     * Get number of concepts
     */
    public int size() {
        return concepts.size();
    }
    
    // === Helper Methods ===
    
    private double[] normalize(double[] vector) {
        double norm = 0;
        for (double v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        
        if (norm < 1e-10) return vector;
        
        double[] normalized = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = vector[i] / norm;
        }
        return normalized;
    }
    
    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}
