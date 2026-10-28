package fraymus.brain.unified;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Test suite for Memory Substrate components
 * Tests EntorhinalCortex and HippocampusBrain
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemorySubstrateTest {
    
    private static EntorhinalCortex entorhinalCortex;
    private static HippocampusBrain hippocampus;
    
    @BeforeAll
    static void setUp() {
        entorhinalCortex = new EntorhinalCortex();
        hippocampus = new HippocampusBrain();
    }
    
    // ==================== EntorhinalCortex Tests ====================
    
    @Test
    @Order(1)
    public void testEntorhinalCortexInitialization() {
        assertNotNull(entorhinalCortex);
        assertEquals("EntorhinalCortex", entorhinalCortex.getName());
        assertEquals("Memory", entorhinalCortex.getType());
    }
    
    @Test
    @Order(2)
    public void testEntorhinalCortexWire() {
        entorhinalCortex.wire();
        Map<String, Object> stats = entorhinalCortex.getEmbeddingStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalEmbeddingsGenerated"));
        assertTrue(stats.containsKey("generationAccuracy"));
    }
    
    @Test
    @Order(3)
    public void testEntorhinalCortexStats() {
        entorhinalCortex.wire();
        entorhinalCortex.resetStats();
        
        Map<String, Object> stats = entorhinalCortex.getEmbeddingStats();
        assertNotNull(stats);
        assertEquals(0, stats.get("totalEmbeddingsGenerated"));
        assertEquals(0, stats.get("successfulGenerations"));
    }
    
    @Test
    @Order(4)
    public void testEntorhinalCortexEmbeddingGeneration() {
        entorhinalCortex.wire();
        
        double[] embedding1 = entorhinalCortex.generateEmbedding("test input");
        double[] embedding2 = entorhinalCortex.generateEmbedding("test input");
        
        assertNotNull(embedding1);
        assertEquals(384, embedding1.length);
        
        // Same input should produce same embedding (deterministic)
        assertArrayEquals(embedding1, embedding2, 0.001);
    }
    
    @Test
    @Order(5)
    public void testEntorhinalCortexCosineSimilarity() {
        entorhinalCortex.wire();
        
        double[] embedding1 = entorhinalCortex.generateEmbedding("apple");
        double[] embedding2 = entorhinalCortex.generateEmbedding("orange");
        double[] embedding3 = entorhinalCortex.generateEmbedding("apple");
        
        double similarity1 = entorhinalCortex.cosineSimilarity(embedding1, embedding3);
        double similarity2 = entorhinalCortex.cosineSimilarity(embedding1, embedding2);
        
        // Same embeddings should have similarity 1.0
        assertEquals(1.0, similarity1, 0.001);
        
        // Different embeddings should have lower similarity
        assertTrue(similarity2 < 1.0);
    }
    
    @Test
    @Order(6)
    public void testEntorhinalCortexCache() {
        entorhinalCortex.wire();
        entorhinalCortex.clearCache();
        
        Map<String, Object> stats = entorhinalCortex.getEmbeddingStats();
        assertEquals(0, stats.get("cacheSize"));
        
        // Generate embedding
        entorhinalCortex.generateEmbedding("test");
        
        // Should be cached
        stats = entorhinalCortex.getEmbeddingStats();
        assertEquals(1, stats.get("cacheSize"));
    }
    
    @Test
    @Order(7)
    public void testEntorhinalCortexUnwire() {
        entorhinalCortex.wire();
        entorhinalCortex.unwire();
        
        BrainQuery query = new BrainQuery(
            "Test embedding",
            java.util.Collections.emptyList(),
            new java.util.HashMap<>(),
            1.0
        );
        
        BrainResponse response = entorhinalCortex.process(query);
        assertFalse(response.isSuccess());
    }
    
    // ==================== HippocampusBrain Tests ====================
    
    @Test
    @Order(8)
    public void testHippocampusInitialization() {
        assertNotNull(hippocampus);
        assertEquals("Hippocampus", hippocampus.getName());
        assertEquals("Memory", hippocampus.getType());
    }
    
    @Test
    @Order(9)
    public void testHippocampusWire() {
        hippocampus.wire();
        Map<String, Object> stats = hippocampus.getConsolidationStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalMemoriesConsolidated"));
        assertTrue(stats.containsKey("consolidationAccuracy"));
    }
    
    @Test
    @Order(10)
    public void testHippocampusStats() {
        hippocampus.wire();
        hippocampus.resetStats();
        
        Map<String, Object> stats = hippocampus.getConsolidationStats();
        assertNotNull(stats);
        assertEquals(0, stats.get("totalMemoriesConsolidated"));
        assertEquals(0, stats.get("successfulConsolidations"));
    }
    
    @Test
    @Order(11)
    public void testHippocampusConsolidation() {
        hippocampus.wire();
        
        String result = hippocampus.consolidateMemory("test memory");
        assertNotNull(result);
        assertTrue(result.contains("Memory consolidated"));
        
        Map<String, Object> stats = hippocampus.getConsolidationStats();
        assertEquals(1, stats.get("totalMemoriesConsolidated"));
        assertEquals(1, stats.get("memoryStoreSize"));
    }
    
    @Test
    @Order(12)
    public void testHippocampusConsolidationMultiple() {
        hippocampus.wire();
        hippocampus.resetStats();
        hippocampus.clearMemoryStore();
        
        // Consolidate multiple memories
        hippocampus.consolidateMemory("apple fruit red");
        hippocampus.consolidateMemory("banana fruit yellow");
        hippocampus.consolidateMemory("orange fruit orange");
        
        Map<String, Object> stats = hippocampus.getConsolidationStats();
        assertEquals(3, stats.get("totalMemoriesConsolidated"));
        assertEquals(3, stats.get("memoryStoreSize"));
    }
    
    @Test
    @Order(13)
    public void testHippocampusMemoryStoreLimit() {
        hippocampus.wire();
        hippocampus.clearMemoryStore();
        
        // Consolidate many memories
        for (int i = 0; i < 1100; i++) {
            hippocampus.consolidateMemory("memory " + i);
        }
        
        Map<String, Object> stats = hippocampus.getConsolidationStats();
        // Should not exceed max memories
        assertTrue((Integer) stats.get("memoryStoreSize") <= 1000);
    }
    
    @Test
    @Order(14)
    public void testHippocampusUnwire() {
        hippocampus.wire();
        hippocampus.unwire();
        
        BrainQuery query = new BrainQuery(
            "Test memory",
            java.util.Collections.emptyList(),
            new java.util.HashMap<>(),
            1.0
        );
        
        BrainResponse response = hippocampus.process(query);
        assertFalse(response.isSuccess());
    }
    
    // ==================== Integration Tests ====================
    
    @Test
    @Order(15)
    public void testMemorySubstrateIntegration() {
        // Wire both components
        entorhinalCortex.wire();
        hippocampus.wire();
        
        // Reset stats
        entorhinalCortex.resetStats();
        hippocampus.resetStats();
        hippocampus.clearMemoryStore();
        
        // Check both are wired
        assertNotNull(entorhinalCortex.getEmbeddingStats());
        assertNotNull(hippocampus.getConsolidationStats());
        
        // Generate embedding
        double[] embedding = entorhinalCortex.generateEmbedding("test memory");
        
        // Consolidate memory
        hippocampus.consolidateMemory("test memory");
        
        // Verify consolidation
        Map<String, Object> hippocampusStats = hippocampus.getConsolidationStats();
        assertEquals(1, hippocampusStats.get("totalMemoriesConsolidated"));
        
        // Cleanup
        entorhinalCortex.unwire();
        hippocampus.unwire();
    }
}
