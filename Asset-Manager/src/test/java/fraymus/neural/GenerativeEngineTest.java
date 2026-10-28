package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GenerativeEngine
 * 
 * Phase 4: Generative Capability
 * 
 * Tests cover:
 * - Constructor validation
 * - Text generation
 * - FUSE operation
 * - Novelty exploration
 * - Visual generation (placeholder)
 * - Multi-modal generation
 * - Statistics tracking
 * - Error handling
 */
public class GenerativeEngineTest {
    
    private SentientCore core;
    private LLMReasoningEngine llmEngine;
    private HDCSimilarityEngine hdcEngine;
    private GenerativeEngine genEngine;
    
    @BeforeEach
    void setUp() {
        core = new SentientCore();
        llmEngine = new LLMReasoningEngine(true, "qwen3-coder-next");
        hdcEngine = new HDCSimilarityEngine();
        genEngine = new GenerativeEngine(llmEngine, core, hdcEngine);
    }
    
    @Test
    void testConstructorWithValidParameters() {
        assertNotNull(genEngine);
    }
    
    @Test
    void testConstructorWithNullLLMEngine() {
        assertThrows(IllegalArgumentException.class, () -> new GenerativeEngine(null, core, hdcEngine));
    }
    
    @Test
    void testConstructorWithNullCore() {
        assertThrows(IllegalArgumentException.class, () -> new GenerativeEngine(llmEngine, null, hdcEngine));
    }
    
    @Test
    void testConstructorWithNullHDC() {
        // HDC engine is optional for GenerativeEngine
        GenerativeEngine engine = new GenerativeEngine(llmEngine, core, null);
        assertNotNull(engine);
    }
    
    @Test
    void testGenerateText() {
        GenerativeEngine.GenerativeResult result = genEngine.generateText("Write a short poem about AI");
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertTrue(result.elapsedTimeMs >= 0);
        assertEquals("text", result.type);
        assertTrue(result.novelty >= 0.0 && result.novelty <= 1.0);
        assertTrue(result.coherence >= 0.0 && result.coherence <= 1.0);
    }
    
    @Test
    void testGenerateTextWithEmptyPrompt() {
        GenerativeEngine.GenerativeResult result = genEngine.generateText("");
        
        assertNotNull(result);
        assertNotNull(result.result);
    }
    
    @Test
    void testGenerateTextWithNullPrompt() {
        GenerativeEngine.GenerativeResult result = genEngine.generateText(null);
        
        assertNotNull(result);
        assertNotNull(result.result);
    }
    
    @Test
    void testFuse() {
        String[] concepts = {"fire", "ice", "transformation"};
        
        GenerativeEngine.GenerativeResult result = genEngine.fuse(concepts);
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("fuse", result.type);
        assertTrue(result.novelty >= 0.0 && result.novelty <= 1.0);
        assertTrue(result.coherence >= 0.0 && result.coherence <= 1.0);
    }
    
    @Test
    void testFuseWithSingleConcept() {
        String[] concepts = {"fire"};
        
        GenerativeEngine.GenerativeResult result = genEngine.fuse(concepts);
        
        assertNotNull(result);
        assertFalse(result.success);
        assertTrue(result.result.contains("ERROR"));
    }
    
    @Test
    void testFuseWithNullConcepts() {
        GenerativeEngine.GenerativeResult result = genEngine.fuse(null);
        
        assertNotNull(result);
        assertFalse(result.success);
        assertTrue(result.result.contains("ERROR"));
    }
    
    @Test
    void testFuseWithEmptyConcepts() {
        String[] concepts = {};
        
        GenerativeEngine.GenerativeResult result = genEngine.fuse(concepts);
        
        assertNotNull(result);
        assertFalse(result.success);
        assertTrue(result.result.contains("ERROR"));
    }
    
    @Test
    void testExploreNovelty() {
        GenerativeEngine.GenerativeResult result = genEngine.exploreNovelty("artificial intelligence");
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("novelty", result.type);
        assertTrue(result.novelty >= 0.0 && result.novelty <= 1.0);
        assertTrue(result.coherence >= 0.0 && result.coherence <= 1.0);
    }
    
    @Test
    void testExploreNoveltyWithEmptyConcept() {
        GenerativeEngine.GenerativeResult result = genEngine.exploreNovelty("");
        
        assertNotNull(result);
        assertNotNull(result.result);
    }
    
    @Test
    void testGenerateVisual() {
        GenerativeEngine.GenerativeResult result = genEngine.generateVisual("A sunset over mountains");
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("visual", result.type);
        assertTrue(result.result.contains("LTX-Video"));
        // Success depends on LTX-Video being installed and working
        // Don't assert success, just check it runs
    }
    
    @Test
    void testGenerateMultiModal() {
        GenerativeEngine.GenerativeResult result = genEngine.generateMultiModal("A futuristic city");
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("multimodal", result.type);
        assertTrue(result.result.contains("TEXT"));
        assertTrue(result.result.contains("VISUAL"));
        assertTrue(result.novelty >= 0.0 && result.novelty <= 1.0);
        assertTrue(result.coherence >= 0.0 && result.coherence <= 1.0);
    }
    
    @Test
    void testGetStats() {
        String stats = genEngine.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Generative Engine Statistics"));
        assertTrue(stats.contains("Text generations: 0"));
        assertTrue(stats.contains("Visual generations: 0"));
        assertTrue(stats.contains("FUSE operations: 0"));
        assertTrue(stats.contains("Novelty explorations: 0"));
    }
    
    @Test
    void testGetStatsAfterOperations() {
        genEngine.generateText("test");
        genEngine.generateVisual("test");
        genEngine.fuse(new String[]{"a", "b"});
        genEngine.exploreNovelty("test");
        
        String stats = genEngine.getStats();
        assertNotNull(stats);
        
        assertTrue(stats.contains("Text generations: 1"));
        assertTrue(stats.contains("Visual generations: 1"));
        assertTrue(stats.contains("FUSE operations: 1"));
        assertTrue(stats.contains("Novelty explorations: 1"));
    }
    
    @Test
    void testGenerativeResultFields() {
        GenerativeEngine.GenerativeResult result = genEngine.generateText("test");
        
        assertNotNull(result.result);
        assertTrue(result.elapsedTimeMs >= 0);
        assertNotNull(result.type);
        assertNotNull(result.success);
        assertTrue(result.novelty >= 0.0 && result.novelty <= 1.0);
        assertTrue(result.coherence >= 0.0 && result.coherence <= 1.0);
    }
    
    @Test
    void testNoveltyCalculation() {
        // Test with repetitive content (low novelty)
        GenerativeEngine.GenerativeResult result1 = genEngine.generateText("test test test test");
        assertTrue(result1.novelty >= 0.0 && result1.novelty <= 1.0);
        
        // Test with varied content
        GenerativeEngine.GenerativeResult result2 = genEngine.generateText("Write a creative story");
        assertTrue(result2.novelty >= 0.0 && result2.novelty <= 1.0);
    }
    
    @Test
    void testCoherenceCalculation() {
        GenerativeEngine.GenerativeResult result = genEngine.generateText("test");
        
        assertNotNull(result.coherence);
        assertTrue(result.coherence >= 0.0);
        assertTrue(result.coherence <= 1.0);
    }
    
    @Test
    void testMultipleGenerations() {
        if (!llmEngine.testConnection()) {
            return; // Skip if not connected
        }
        
        genEngine.generateText("test");
        genEngine.generateText("test");
        genEngine.generateText("test");
        
        String stats = genEngine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Text generations: 3"));
    }
    
    @Test
    void testWithDifferentLLMModel() {
        LLMReasoningEngine customEngine = new LLMReasoningEngine(true, "deepseek-v3.2");
        GenerativeEngine customGen = new GenerativeEngine(customEngine, core, hdcEngine);
        
        assertNotNull(customGen);
        
        GenerativeEngine.GenerativeResult result = customGen.generateText("test");
        assertNotNull(result);
    }
}
