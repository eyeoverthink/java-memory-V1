package fraymus.brain.unified;

import fraymus.hdc.SharedHypervectorCore;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Hyperdimensional Computing Engine.
 * Provides 16,384-D vector operations for similarity search and binding.
 */
public class HDCEngine {
    private static final int DIMENSION = SharedHypervectorCore.DIMENSION;
    private static final Random random = new Random();
    private static final SharedHypervectorCore SHARED_CORE = new SharedHypervectorCore();

    private long vectorizations;
    private long similarityCalculations;
    private long bindingOperations;

    public HDCEngine() {
        this.vectorizations = 0;
        this.similarityCalculations = 0;
        this.bindingOperations = 0;
    }

    /**
     * Convert text to a 16,384-D binary vector.
     */
    public double[] vectorize(String text) {
        vectorizations++;
        BitSet encoded = SHARED_CORE.encodeText(text);
        return SHARED_CORE.toSignedArray(encoded);
    }

    /**
     * Calculate similarity between two vectors using Hamming distance.
     * Returns value between 0.0 (no similarity) and 1.0 (identical).
     */
    public double similarity(double[] vectorA, double[] vectorB) {
        similarityCalculations++;
        
        if (vectorA == null || vectorB == null || vectorA.length != vectorB.length) {
            return 0.0;
        }
        BitSet left = SHARED_CORE.fromSignedArray(vectorA);
        BitSet right = SHARED_CORE.fromSignedArray(vectorB);
        return SHARED_CORE.hammingSimilarity(left, right);
    }

    /**
     * Bind two vectors using XOR (holographic binding).
     */
    public double[] bind(double[] vectorA, double[] vectorB) {
        bindingOperations++;
        
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vector dimensions must match");
        }
        BitSet left = SHARED_CORE.fromSignedArray(vectorA);
        BitSet right = SHARED_CORE.fromSignedArray(vectorB);
        return SHARED_CORE.toSignedArray(SHARED_CORE.bind(left, right));
    }

    /**
     * Bundle multiple vectors using majority vote.
     */
    public double[] bundle(double[]... vectors) {
        if (vectors.length == 0) {
            return new double[DIMENSION];
        }
        java.util.List<BitSet> bitVectors = new java.util.ArrayList<>();
        for (double[] vector : vectors) {
            if (vector != null && vector.length == DIMENSION) {
                bitVectors.add(SHARED_CORE.fromSignedArray(vector));
            }
        }
        if (bitVectors.isEmpty()) {
            return new double[DIMENSION];
        }
        return SHARED_CORE.toSignedArray(SHARED_CORE.bundle(bitVectors));
    }

    /**
     * Unbind a vector (reverse of binding).
     */
    public double[] unbind(double[] boundVector, double[] keyVector) {
        return bind(boundVector, keyVector); // XOR is its own inverse
    }

    /**
     * Generate a random 16,384-D vector.
     */
    public double[] randomVector() {
        double[] vector = new double[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            vector[i] = random.nextBoolean() ? 1.0 : -1.0;
        }
        return vector;
    }

    /**
     * Get dimension size.
     */
    public static int getDimension() {
        return DIMENSION;
    }

    /**
     * Get statistics.
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("dimension", DIMENSION);
        stats.put("vectorizations", vectorizations);
        stats.put("similarityCalculations", similarityCalculations);
        stats.put("bindingOperations", bindingOperations);
        return stats;
    }
}
