package brain_architecture.synapses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HDCTest - Unit tests for HyperdimensionalMapper and HDCSimilarityEngine.
 */
public class HDCTest {
    
    private HyperdimensionalMapper mapper;
    private HDCSimilarityEngine engine;
    
    @BeforeEach
    void setUp() {
        mapper = new HyperdimensionalMapper();
        engine = new HDCSimilarityEngine(0.7);
    }
    
    @Test
    void testMapConcept() {
        String concept = "test concept";
        BitSet vector = mapper.map(concept);
        
        assertNotNull(vector);
        assertEquals(16384, vector.size());
    }
    
    @Test
    void testDeterministicMapping() {
        String concept = "test concept";
        BitSet vector1 = mapper.map(concept);
        BitSet vector2 = mapper.map(concept);
        
        assertEquals(vector1, vector2);
    }
    
    @Test
    void testDifferentConceptsDifferentVectors() {
        BitSet vectorA = mapper.map("concept A");
        BitSet vectorB = mapper.map("concept B");
        
        assertNotEquals(vectorA, vectorB);
    }
    
    @Test
    void testBind() {
        BitSet vectorA = mapper.map("A");
        BitSet vectorB = mapper.map("B");
        BitSet bound = mapper.bind(vectorA, vectorB);
        
        assertNotNull(bound);
        assertEquals(16384, bound.size());
    }
    
    @Test
    void testBindReversible() {
        BitSet vectorA = mapper.map("A");
        BitSet vectorB = mapper.map("B");
        BitSet bound = mapper.bind(vectorA, vectorB);
        BitSet unbound = mapper.bind(bound, vectorB);
        
        // XOR is reversible: (A XOR B) XOR B = A
        assertEquals(vectorA, unbound);
    }
    
    @Test
    void testBundle() {
        BitSet vectorA = mapper.map("A");
        BitSet vectorB = mapper.map("B");
        BitSet vectorC = mapper.map("C");
        
        BitSet bundled = mapper.bundle(vectorA, vectorB, vectorC);
        
        assertNotNull(bundled);
        assertEquals(16384, bundled.size());
    }
    
    @Test
    void testCalculateSimilarity() {
        BitSet vectorA = mapper.map("concept A");
        BitSet vectorB = mapper.map("concept A"); // Same
        BitSet vectorC = mapper.map("concept C"); // Different
        
        double similarityAB = engine.calculateSimilarity(vectorA, vectorB);
        double similarityAC = engine.calculateSimilarity(vectorA, vectorC);
        
        assertEquals(1.0, similarityAB, 0.001);
        assertTrue(similarityAC < 1.0);
        assertTrue(similarityAC > 0.0);
    }
    
    @Test
    void testFindTopK() {
        Map<String, BitSet> vectors = new HashMap<>();
        vectors.put("A", mapper.map("A"));
        vectors.put("B", mapper.map("B"));
        vectors.put("C", mapper.map("C"));
        vectors.put("D", mapper.map("D"));
        vectors.put("E", mapper.map("E"));
        
        BitSet query = mapper.map("A");
        List<HDCSimilarityEngine.SimilarityResult> topK = engine.findTopK(query, vectors, 3);
        
        assertNotNull(topK);
        assertEquals(3, topK.size());
        assertTrue(topK.get(0).getSimilarity() >= topK.get(1).getSimilarity());
    }
    
    @Test
    void testFindAboveThreshold() {
        Map<String, BitSet> vectors = new HashMap<>();
        vectors.put("A", mapper.map("A"));
        vectors.put("B", mapper.map("B"));
        vectors.put("C", mapper.map("C"));
        
        BitSet query = mapper.map("A");
        List<HDCSimilarityEngine.SimilarityResult> results = engine.findAboveThreshold(query, vectors);
        
        assertNotNull(results);
        // "A" should be in results since it's the same concept (similarity = 1.0)
        assertTrue(results.stream().anyMatch(r -> r.getConceptName().equals("A")));
    }
    
    @Test
    void testSimilarityCache() {
        BitSet vectorA = mapper.map("A");
        BitSet vectorB = mapper.map("B");
        
        // First calculation
        double similarity1 = engine.calculateSimilarity(vectorA, vectorB);
        
        // Second calculation (should use cache)
        double similarity2 = engine.calculateSimilarity(vectorA, vectorB);
        
        assertEquals(similarity1, similarity2);
    }
    
    @Test
    void testClearCache() {
        BitSet vectorA = mapper.map("A");
        BitSet vectorB = mapper.map("B");
        
        engine.calculateSimilarity(vectorA, vectorB);
        engine.clearCache();
        
        // Should still work after cache clear
        double similarity = engine.calculateSimilarity(vectorA, vectorB);
        assertTrue(similarity >= 0.0 && similarity <= 1.0);
    }
    
    @Test
    void testSimilarityResult() {
        HDCSimilarityEngine.SimilarityResult result = new HDCSimilarityEngine.SimilarityResult("test", 0.85);
        
        assertEquals("test", result.getConceptName());
        assertEquals(0.85, result.getSimilarity(), 0.001);
    }
}
