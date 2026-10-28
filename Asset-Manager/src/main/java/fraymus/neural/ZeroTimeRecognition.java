package fraymus.neural;

import java.util.BitSet;
import java.util.ArrayList;
import java.util.List;

/**
 * ZERO-TIME RECOGNITION ENGINE
 * 
 * The "Sign Maker" - Creates vector signatures for instant recognition
 * 
 * FORMULA:
 * Similarity = 1 - (Hamming distance / DIMENSION)
 * Hamming distance = XOR(vectorA, vectorB).cardinality()
 * 
 * ZERO-TIME because:
 * - No training required
 * - Recognition = XOR + bit-count (microseconds)
 * - O(D) where D = 16,384 dimensions
 * 
 * This is the mathematical particle collider for instant pattern recognition.
 */
public class ZeroTimeRecognition {

    private static final int DIMENSION = 16384;
    private final HyperdimensionalMapper mapper;
    
    // Recognition statistics
    private long recognitionsPerformed = 0;
    private long totalRecognitionTimeNs = 0;

    public ZeroTimeRecognition() {
        this.mapper = new HyperdimensionalMapper();
    }

    public ZeroTimeRecognition(HyperdimensionalMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Create a signature (vector) for a concept - the "sign maker"
     */
    public BitSet createSignature(String concept) {
        return mapper.mapConcept(concept);
    }

    /**
     * ZERO-TIME RECOGNITION
     * 
     * Recognize a concept by comparing its signature to stored signatures
     * using the Hamming distance formula.
     * 
     * @param queryConcept The concept to recognize
     * @param threshold Minimum similarity [0, 1]
     * @return List of recognized concepts with similarity scores
     */
    public List<RecognitionResult> recognize(String queryConcept, double threshold) {
        long startTime = System.nanoTime();
        
        BitSet queryVector = mapper.mapConcept(queryConcept);
        List<RecognitionResult> results = new ArrayList<>();
        
        // Compare against all stored concepts using mapper's findSimilar
        List<HyperdimensionalMapper.SimilarityResult> similarConcepts = 
            mapper.findSimilar(queryConcept, threshold);
        
        for (HyperdimensionalMapper.SimilarityResult simResult : similarConcepts) {
            results.add(new RecognitionResult(simResult.concept, simResult.similarity));
        }
        
        long duration = System.nanoTime() - startTime;
        recognitionsPerformed++;
        totalRecognitionTimeNs += duration;
        
        return results;
    }

    /**
     * ZERO-TIME RECOGNITION with default threshold (0.5)
     */
    public List<RecognitionResult> recognize(String queryConcept) {
        return recognize(queryConcept, 0.5);
    }

    /**
     * THE ZERO-TIME FORMULA
     * 
     * Similarity = 1 - (Hamming distance / DIMENSION)
     * 
     * This is the core formula that enables instant recognition without training.
     */
    public double computeSimilarity(BitSet vectorA, BitSet vectorB) {
        // Hamming distance = number of differing bits
        BitSet xor = (BitSet) vectorA.clone();
        xor.xor(vectorB);
        int hammingDistance = xor.cardinality();
        
        // Similarity formula
        return 1.0 - ((double) hammingDistance / DIMENSION);
    }

    /**
     * Get average recognition time
     */
    public double getAverageRecognitionTimeNs() {
        return recognitionsPerformed > 0 ? (double) totalRecognitionTimeNs / recognitionsPerformed : 0;
    }

    /**
     * Get recognition statistics
     */
    public String getStats() {
        double avgTimeNs = getAverageRecognitionTimeNs();
        return String.format(
            "Zero-Time Recognition Stats:\n" +
            "  Recognitions: %,d\n" +
            "  Avg time: %.3f ns (%.6f ms)\n" +
            "  Total time: %.3f ms\n" +
            "  Concepts stored: %,d",
            recognitionsPerformed,
            avgTimeNs,
            avgTimeNs / 1_000_000.0,
            totalRecognitionTimeNs / 1_000_000.0,
            mapper.getConceptCount()
        );
    }

    /**
     * Result class for recognition
     */
    public static class RecognitionResult {
        public final String concept;
        public final double similarity;
        
        public RecognitionResult(String concept, double similarity) {
            this.concept = concept;
            this.similarity = similarity;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%.3f)", concept, similarity);
        }
    }

    /**
     * Get the underlying mapper
     */
    public HyperdimensionalMapper getMapper() {
        return mapper;
    }
}
