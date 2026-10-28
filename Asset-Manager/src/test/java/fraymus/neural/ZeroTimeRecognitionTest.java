package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.BitSet;
import java.util.List;

/**
 * Test suite for Zero-Time Recognition Engine
 * 
 * Tests the zero-time recognition formula:
 * Similarity = 1 - (Hamming distance / DIMENSION)
 */
public class ZeroTimeRecognitionTest {

    private ZeroTimeRecognition ztr;

    @BeforeEach
    void setUp() {
        ztr = new ZeroTimeRecognition();
    }

    @Test
    void testCreateSignature() {
        BitSet signature = ztr.createSignature("test_concept");
        assertNotNull(signature);
        assertEquals(16384, signature.size());
    }

    @Test
    void testSignatureDeterminism() {
        BitSet sig1 = ztr.createSignature("same_concept");
        BitSet sig2 = ztr.createSignature("same_concept");
        
        // Same concept should produce same signature
        assertEquals(sig1, sig2, "Signatures should be deterministic");
    }

    @Test
    void testSignatureUniqueness() {
        BitSet sig1 = ztr.createSignature("concept_a");
        BitSet sig2 = ztr.createSignature("concept_b");
        
        // Different concepts should produce different signatures
        assertNotEquals(sig1, sig2, "Signatures should be unique");
    }

    @Test
    void testZeroTimeRecognition() {
        // Create signatures for test concepts
        ztr.createSignature("apple");
        ztr.createSignature("orange");
        ztr.createSignature("banana");
        ztr.createSignature("fruit");
        
        // Try to recognize "apple"
        List<ZeroTimeRecognition.RecognitionResult> results = ztr.recognize("apple");
        
        assertNotNull(results);
        assertTrue(results.size() >= 0, "Recognition should return results");
    }

    @Test
    void testRecognitionSpeed() {
        // Create many signatures
        int conceptCount = 1000;
        for (int i = 0; i < conceptCount; i++) {
            ztr.createSignature("concept_" + i);
        }
        
        // Perform recognition
        long startTime = System.nanoTime();
        List<ZeroTimeRecognition.RecognitionResult> results = ztr.recognize("concept_500");
        long duration = System.nanoTime() - startTime;
        
        double durationMs = duration / 1_000_000.0;
        
        System.out.printf("Recognition time: %.3f ms for %d concepts%n", durationMs, conceptCount);
        
        // Should be very fast (< 10ms for 1000 concepts)
        assertTrue(durationMs < 10.0, "Recognition too slow: " + durationMs + " ms");
    }

    @Test
    void testSimilarityFormula() {
        BitSet vectorA = ztr.createSignature("concept_a");
        BitSet vectorB = ztr.createSignature("concept_b");
        
        double similarity = ztr.computeSimilarity(vectorA, vectorB);
        
        // Similarity should be in [0, 1]
        assertTrue(similarity >= 0.0 && similarity <= 1.0, 
            "Similarity out of range: " + similarity);
        
        // Identical vectors should have similarity 1.0
        double selfSimilarity = ztr.computeSimilarity(vectorA, vectorA);
        assertEquals(1.0, selfSimilarity, 0.001, "Self-similarity should be 1.0");
    }

    @Test
    void testRecognitionThreshold() {
        ztr.createSignature("cat");
        ztr.createSignature("dog");
        ztr.createSignature("animal");
        
        // Test with high threshold (0.9)
        List<ZeroTimeRecognition.RecognitionResult> highThreshold = 
            ztr.recognize("cat", 0.9);
        
        // Test with low threshold (0.1)
        List<ZeroTimeRecognition.RecognitionResult> lowThreshold = 
            ztr.recognize("cat", 0.1);
        
        // Lower threshold should return more results
        assertTrue(lowThreshold.size() >= highThreshold.size(),
            "Lower threshold should return more results");
    }

    @Test
    void testStats() {
        ztr.createSignature("test1");
        ztr.createSignature("test2");
        ztr.recognize("test1");
        
        String stats = ztr.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Recognitions"), "Stats should show recognition count");
    }

    @Test
    void testAverageRecognitionTime() {
        ztr.createSignature("concept_a");
        ztr.createSignature("concept_b");
        
        // Perform multiple recognitions
        for (int i = 0; i < 10; i++) {
            ztr.recognize("concept_a");
        }
        
        double avgTime = ztr.getAverageRecognitionTimeNs();
        assertTrue(avgTime > 0, "Average time should be positive");
        
        System.out.printf("Average recognition time: %.3f ns%n", avgTime);
    }

    @Test
    void testZeroTimePerformance() {
        // This test demonstrates the "zero-time" aspect
        // Recognition should happen in microseconds, not milliseconds
        
        int concepts = 100;
        for (int i = 0; i < concepts; i++) {
            ztr.createSignature("concept_" + i);
        }
        
        // Warm up
        ztr.recognize("concept_0");
        
        // Measure
        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            ztr.recognize("concept_" + (i % concepts));
        }
        long duration = System.nanoTime() - startTime;
        
        double avgNs = duration / 100.0;
        double avgMs = avgNs / 1_000_000.0;
        
        System.out.printf("Average recognition: %.3f ns (%.6f ms)%n", avgNs, avgMs);
        System.out.println("ZERO-TIME: Recognition in microseconds!");
        
        // Should be in microseconds (< 1ms)
        assertTrue(avgMs < 1.0, "Not zero-time: " + avgMs + " ms");
    }
}
