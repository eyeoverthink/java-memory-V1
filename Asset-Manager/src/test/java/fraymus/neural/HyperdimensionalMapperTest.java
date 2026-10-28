package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Unit tests for HyperdimensionalMapper
 * 
 * Phase 1.1: HDC Integration with SentientCore
 * 
 * RULE UT-1: Every new class must have unit tests
 * RULE UT-3: Tests must be deterministic
 */
public class HyperdimensionalMapperTest {

    private HyperdimensionalMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HyperdimensionalMapper();
    }

    @Test
    void testMapConcept() {
        BitSet vector = mapper.mapConcept("test");
        
        assertNotNull(vector);
        assertEquals(HyperdimensionalMapper.DIMENSION, vector.size());
        assertEquals(1, mapper.getConceptCount());
    }

    @Test
    void testMapConceptDeterministic() {
        // Same concept should map to same vector (deterministic)
        BitSet vector1 = mapper.mapConcept("deterministic");
        BitSet vector2 = mapper.mapConcept("deterministic");
        
        assertEquals(vector1, vector2);
    }

    @Test
    void testMapConceptDifferentConcepts() {
        BitSet vector1 = mapper.mapConcept("concept1");
        BitSet vector2 = mapper.mapConcept("concept2");
        
        // Different concepts should have different vectors (with high probability)
        int hamming = mapper.hammingDistance(vector1, vector2);
        assertTrue(hamming > HyperdimensionalMapper.DIMENSION / 4); // At least 25% different
    }

    @Test
    void testMapConceptNull() {
        assertThrows(IllegalArgumentException.class, () -> mapper.mapConcept(null));
    }

    @Test
    void testMapConceptEmpty() {
        assertThrows(IllegalArgumentException.class, () -> mapper.mapConcept(""));
    }

    @Test
    void testGetVector() {
        mapper.mapConcept("test");
        BitSet vector = mapper.getVector("test");
        
        assertNotNull(vector);
        assertEquals(HyperdimensionalMapper.DIMENSION, vector.size());
    }

    @Test
    void testGetVectorNotMapped() {
        BitSet vector = mapper.getVector("not_mapped");
        assertNull(vector);
    }

    @Test
    void testIsMapped() {
        assertFalse(mapper.isMapped("test"));
        mapper.mapConcept("test");
        assertTrue(mapper.isMapped("test"));
    }

    @Test
    void testBind() {
        BitSet vectorA = mapper.mapConcept("A");
        BitSet vectorB = mapper.mapConcept("B");
        
        BitSet bound = mapper.bind(vectorA, vectorB);
        
        assertNotNull(bound);
        assertEquals(HyperdimensionalMapper.DIMENSION, bound.size());
    }

    @Test
    void testBindNull() {
        BitSet vector = mapper.mapConcept("test");
        assertThrows(IllegalArgumentException.class, () -> mapper.bind(null, vector));
        assertThrows(IllegalArgumentException.class, () -> mapper.bind(vector, null));
    }

    @Test
    void testBindWrongDimension() {
        BitSet vector = mapper.mapConcept("test");
        BitSet wrongDim = new BitSet(1000);
        
        assertThrows(IllegalArgumentException.class, () -> mapper.bind(vector, wrongDim));
    }

    @Test
    void testBindUnbindReversible() {
        // RULE HDC-3: Binding must be reversible
        BitSet vectorA = mapper.mapConcept("A");
        BitSet vectorB = mapper.mapConcept("B");
        
        BitSet bound = mapper.bind(vectorA, vectorB);
        BitSet unbound = mapper.unbind(vectorA, bound);
        
        // unbound should equal vectorB
        assertEquals(vectorB, unbound);
    }

    @Test
    void testBundle() {
        BitSet vectorA = mapper.mapConcept("A");
        BitSet vectorB = mapper.mapConcept("B");
        BitSet vectorC = mapper.mapConcept("C");
        
        List<BitSet> vectors = Arrays.asList(vectorA, vectorB, vectorC);
        BitSet bundled = mapper.bundle(vectors);
        
        assertNotNull(bundled);
        assertEquals(HyperdimensionalMapper.DIMENSION, bundled.size());
    }

    @Test
    void testBundleEmpty() {
        assertThrows(IllegalArgumentException.class, () -> mapper.bundle(Collections.emptyList()));
    }

    @Test
    void testBundleNullVector() {
        BitSet vector = mapper.mapConcept("test");
        List<BitSet> vectors = Arrays.asList(vector, null);
        
        assertThrows(IllegalArgumentException.class, () -> mapper.bundle(vectors));
    }

    @Test
    void testSimilarityIdentical() {
        BitSet vector = mapper.mapConcept("test");
        double sim = mapper.similarity(vector, vector);
        
        assertEquals(1.0, sim, 0.001);
    }

    @Test
    void testSimilarityNull() {
        BitSet vector = mapper.mapConcept("test");
        assertThrows(IllegalArgumentException.class, () -> mapper.similarity(null, vector));
        assertThrows(IllegalArgumentException.class, () -> mapper.similarity(vector, null));
    }

    @Test
    void testSimilarityRange() {
        BitSet vectorA = mapper.mapConcept("A");
        BitSet vectorB = mapper.mapConcept("B");
        
        double sim = mapper.similarity(vectorA, vectorB);
        
        assertTrue(sim >= 0.0 && sim <= 1.0);
    }

    @Test
    void testHammingDistance() {
        BitSet vectorA = mapper.mapConcept("A");
        BitSet vectorB = mapper.mapConcept("B");
        
        int distance = mapper.hammingDistance(vectorA, vectorB);
        
        assertTrue(distance >= 0);
        assertTrue(distance <= HyperdimensionalMapper.DIMENSION);
    }

    @Test
    void testHammingDistanceIdentical() {
        BitSet vector = mapper.mapConcept("test");
        int distance = mapper.hammingDistance(vector, vector);
        
        assertEquals(0, distance);
    }

    @Test
    void testFindSimilar() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        mapper.mapConcept("feline");
        mapper.mapConcept("canine");
        
        List<HyperdimensionalMapper.SimilarityResult> results = mapper.findSimilar("cat");
        
        assertNotNull(results);
        // Should find at least itself
        assertTrue(results.size() >= 1);
    }

    @Test
    void testFindSimilarThreshold() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        
        // High threshold should return fewer results
        List<HyperdimensionalMapper.SimilarityResult> highThreshold = 
            mapper.findSimilar("cat", 0.9);
        
        // Low threshold should return more results
        List<HyperdimensionalMapper.SimilarityResult> lowThreshold = 
            mapper.findSimilar("cat", 0.1);
        
        assertTrue(lowThreshold.size() >= highThreshold.size());
    }

    @Test
    void testFindSimilarExcludesSelf() {
        mapper.mapConcept("test");
        
        List<HyperdimensionalMapper.SimilarityResult> results = mapper.findSimilar("test");
        
        // Should not include itself
        for (HyperdimensionalMapper.SimilarityResult result : results) {
            assertNotEquals("test", result.concept);
        }
    }

    @Test
    void testGetStats() {
        mapper.mapConcept("test");
        String stats = mapper.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Dimension"));
        assertTrue(stats.contains("Concepts mapped"));
    }

    @Test
    void testClear() {
        mapper.mapConcept("test");
        assertEquals(1, mapper.getConceptCount());
        
        mapper.clear();
        assertEquals(0, mapper.getConceptCount());
        assertNull(mapper.getVector("test"));
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int numThreads = 10;
        int conceptsPerThread = 100;
        
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < conceptsPerThread; j++) {
                    String concept = "thread_" + threadId + "_concept_" + j;
                    mapper.mapConcept(concept);
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        assertEquals(numThreads * conceptsPerThread, mapper.getConceptCount());
    }
}
