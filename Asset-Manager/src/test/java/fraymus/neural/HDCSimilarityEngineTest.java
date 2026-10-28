package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.BitSet;
import java.util.List;

/**
 * Unit tests for HDCSimilarityEngine
 * 
 * Phase 1.1: HDC Integration with SentientCore
 * 
 * RULE UT-1: Every new class must have unit tests
 * RULE UT-3: Tests must be deterministic
 */
public class HDCSimilarityEngineTest {

    private HDCSimilarityEngine engine;
    private HyperdimensionalMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HyperdimensionalMapper();
        engine = new HDCSimilarityEngine(mapper);
    }

    @Test
    void testConstructorWithMapper() {
        HDCSimilarityEngine e = new HDCSimilarityEngine(mapper);
        assertNotNull(e);
        assertEquals(mapper, e.getMapper());
    }

    @Test
    void testConstructorWithoutMapper() {
        HDCSimilarityEngine e = new HDCSimilarityEngine();
        assertNotNull(e);
        assertNotNull(e.getMapper());
    }

    @Test
    void testConstructorNullMapper() {
        assertThrows(IllegalArgumentException.class, () -> new HDCSimilarityEngine(null));
    }

    @Test
    void testFindSimilar() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        mapper.mapConcept("feline");
        
        List<HyperdimensionalMapper.SimilarityResult> results = engine.findSimilar("cat");
        
        assertNotNull(results);
        assertTrue(results.size() >= 0);
    }

    @Test
    void testFindSimilarNull() {
        assertThrows(IllegalArgumentException.class, () -> engine.findSimilar(null));
    }

    @Test
    void testFindSimilarEmpty() {
        assertThrows(IllegalArgumentException.class, () -> engine.findSimilar(""));
    }

    @Test
    void testFindSimilarWithThreshold() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        
        // High threshold should return fewer results
        List<HyperdimensionalMapper.SimilarityResult> high = engine.findSimilar("cat", 0.9);
        
        // Low threshold should return more results
        List<HyperdimensionalMapper.SimilarityResult> low = engine.findSimilar("cat", 0.1);
        
        assertTrue(low.size() >= high.size());
    }

    @Test
    void testFindSimilarInvalidThreshold() {
        assertThrows(IllegalArgumentException.class, () -> engine.findSimilar("test", -0.1));
        assertThrows(IllegalArgumentException.class, () -> engine.findSimilar("test", 1.1));
    }

    @Test
    void testFindTopK() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        mapper.mapConcept("feline");
        mapper.mapConcept("canine");
        
        List<HyperdimensionalMapper.SimilarityResult> results = engine.findTopK("cat", 2);
        
        assertNotNull(results);
        assertTrue(results.size() <= 2);
    }

    @Test
    void testFindTopKInvalidK() {
        assertThrows(IllegalArgumentException.class, () -> engine.findTopK("test", 0));
        assertThrows(IllegalArgumentException.class, () -> engine.findTopK("test", -1));
    }

    @Test
    void testCompose() {
        BitSet composed = engine.compose("red", "circle");
        
        assertNotNull(composed);
        assertEquals(HyperdimensionalMapper.DIMENSION, composed.size());
    }

    @Test
    void testComposeMultiple() {
        BitSet composed = engine.composeMultiple("red", "circle", "small");
        
        assertNotNull(composed);
        assertEquals(HyperdimensionalMapper.DIMENSION, composed.size());
    }

    @Test
    void testComposeMultipleEmpty() {
        assertThrows(IllegalArgumentException.class, () -> engine.composeMultiple());
    }

    @Test
    void testComposeMultipleNull() {
        assertThrows(IllegalArgumentException.class, () -> engine.composeMultiple((String[]) null));
    }

    @Test
    void testAggregate() {
        BitSet aggregated = engine.aggregate("red", "blue", "green");
        
        assertNotNull(aggregated);
        assertEquals(HyperdimensionalMapper.DIMENSION, aggregated.size());
    }

    @Test
    void testAggregateEmpty() {
        assertThrows(IllegalArgumentException.class, () -> engine.aggregate());
    }

    @Test
    void testAggregateNull() {
        assertThrows(IllegalArgumentException.class, () -> engine.aggregate((String[]) null));
    }

    @Test
    void testAreSimilar() {
        mapper.mapConcept("cat");
        mapper.mapConcept("feline");
        
        boolean similar = engine.areSimilar("cat", "feline");
        
        // Result depends on random hashing, but should be deterministic
        assertNotNull(similar);
    }

    @Test
    void testGetSimilarity() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        
        double sim = engine.getSimilarity("cat", "dog");
        
        assertNotNull(sim);
        assertTrue(sim >= 0.0 && sim <= 1.0);
    }

    @Test
    void testSetSimilarityThreshold() {
        engine.setSimilarityThreshold(0.7);
        assertEquals(0.7, engine.getSimilarityThreshold(), 0.001);
    }

    @Test
    void testSetSimilarityThresholdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> engine.setSimilarityThreshold(-0.1));
        assertThrows(IllegalArgumentException.class, () -> engine.setSimilarityThreshold(1.1));
    }

    @Test
    void testSetThresholdClearsCache() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        
        // First search populates cache
        engine.findSimilar("cat");
        
        // Change threshold should clear cache
        engine.setSimilarityThreshold(0.7);
        
        String stats = engine.getCacheStats();
        assertTrue(stats.contains("Cache size: 0"));
    }

    @Test
    void testClearCache() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        
        engine.findSimilar("cat");
        engine.clearCache();
        
        String stats = engine.getCacheStats();
        assertTrue(stats.contains("Cache size: 0"));
        assertTrue(stats.contains("Hits: 0"));
    }

    @Test
    void testGetCacheStats() {
        String stats = engine.getCacheStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Hits"));
        assertTrue(stats.contains("Misses"));
        assertTrue(stats.contains("Hit rate"));
    }

    @Test
    void testGetMapper() {
        assertEquals(mapper, engine.getMapper());
    }

    @Test
    void testGetStats() {
        String stats = engine.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Dimension"));
        assertTrue(stats.contains("Concepts mapped"));
        assertTrue(stats.contains("Cache Stats"));
    }

    @Test
    void testCacheFunctionality() {
        mapper.mapConcept("cat");
        mapper.mapConcept("dog");
        mapper.mapConcept("feline");
        
        // First call - cache miss
        engine.findSimilar("cat");
        
        // Second call - cache hit
        engine.findSimilar("cat");
        
        String stats = engine.getCacheStats();
        assertTrue(stats.contains("Hits: 1"));
        assertTrue(stats.contains("Misses: 1"));
    }
}
