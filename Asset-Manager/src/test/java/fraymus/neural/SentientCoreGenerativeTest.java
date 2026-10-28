package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Generative Capability with SentientCore
 * 
 * Phase 4: Generative Capability
 * 
 * RULE SC-2: Maintains backward compatibility
 * RULE SC-3: Never break existing functionality
 */
public class SentientCoreGenerativeTest {

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
    void testSentientCoreWorksWithoutGenerative() {
        // RULE SC-2: Backward compatibility - works without generative
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should work normally
        String status = core.getStatus();
        assertNotNull(status);
    }

    @Test
    void testWireGenerative() {
        core.wireGenerative(genEngine);
        
        assertTrue(core.isGenerativeEnabled());
        assertEquals(genEngine, core.getGenerativeEngine());
        
        String status = core.getStatus();
        assertNotNull(status);
    }

    @Test
    void testWireGenerativeNull() {
        assertThrows(IllegalArgumentException.class, () -> core.wireGenerative(null));
    }

    @Test
    void testUnwireGenerative() {
        core.wireGenerative(genEngine);
        assertTrue(core.isGenerativeEnabled());
        
        core.unwireGenerative();
        assertFalse(core.isGenerativeEnabled());
        assertNull(core.getGenerativeEngine());
    }

    @Test
    void testLearnWithGenerativeEnabled() {
        core.wireGenerative(genEngine);
        
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Learn should still work normally
        assertEquals(1, core.getCausal().edgeCount());
    }

    @Test
    void testGenerateTextWithGenerative() {
        core.wireGenerative(genEngine);
        
        GenerativeEngine.GenerativeResult result = genEngine.generateText("Write a poem");
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("text", result.type);
    }

    @Test
    void testFuseWithGenerative() {
        core.wireGenerative(genEngine);
        
        GenerativeEngine.GenerativeResult result = genEngine.fuse(new String[]{"fire", "ice"});
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("fuse", result.type);
    }

    @Test
    void testExploreNoveltyWithGenerative() {
        core.wireGenerative(genEngine);
        
        GenerativeEngine.GenerativeResult result = genEngine.exploreNovelty("AI");
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("novelty", result.type);
    }

    @Test
    void testGenerateVisualWithGenerative() {
        core.wireGenerative(genEngine);
        
        GenerativeEngine.GenerativeResult result = genEngine.generateVisual("A sunset");
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("visual", result.type);
        // Success depends on LTX-Video being installed and working
        // Don't assert success, just check it runs
    }

    @Test
    void testGenerateMultiModalWithGenerative() {
        core.wireGenerative(genEngine);
        
        GenerativeEngine.GenerativeResult result = genEngine.generateMultiModal("A futuristic city");
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("multimodal", result.type);
    }

    @Test
    void testGenerativeStatusInStatus() {
        String status = core.getStatus();
        assertNotNull(status);
        
        core.wireGenerative(genEngine);
        status = core.getStatus();
        assertNotNull(status);
    }

    @Test
    void testBackwardCompatibilityOfLearn() {
        // Test that learn() signature hasn't changed
        core.learn("test causes result");
        
        // Should still work exactly as before
        assertEquals(1, core.getCausal().edgeCount());
    }

    @Test
    void testBackwardCompatibilityOfThink() {
        // Test that think() signature hasn't changed
        core.learn("test causes result");
        String result = core.think("why result");
        
        // Should still work exactly as before
        assertNotNull(result);
    }

    @Test
    void testBackwardCompatibilityOfAccessors() {
        // Test that all accessors still work
        assertNotNull(core.getCausal());
        assertNotNull(core.getStats());
        assertNotNull(core.getLattice());
        assertNotNull(core.getAnalogy());
        assertNotNull(core.getFisher());
        assertNotNull(core.getSentience());
    }

    @Test
    void testGenerativeDoesNotModifyExistingStructures() {
        // RULE SC-1: Never modify existing data structures
        core.learn("fire causes burns");
        int edgeCountBefore = core.getCausal().edgeCount();
        
        core.wireGenerative(genEngine);
        genEngine.generateText("test");
        genEngine.fuse(new String[]{"a", "b"});
        
        // Causal graph should still work normally
        assertEquals(edgeCountBefore, core.getCausal().edgeCount());
    }

    @Test
    void testGenerativeWithAllOtherEnhancements() {
        // Test all enhancements together
        HDCSimilarityEngine hdc = new HDCSimilarityEngine();
        WorkingMemory wm = new WorkingMemory();
        AttentionMechanism att = new AttentionMechanism();
        EpisodicMemory em = new EpisodicMemory();
        HippocampalReplay hr = new HippocampalReplay(em);
        MetaLearningEngine meta = new MetaLearningEngine(llmEngine, core);
        
        core.wireHDC(hdc);
        core.wireWorkingMemory(wm, att);
        core.wireEpisodicMemory(em, hr);
        core.wireLLMReasoning(llmEngine);
        core.wireMetaLearning(meta);
        core.wireGenerative(genEngine);
        
        core.learn("cats have fur");
        
        // All should be enabled
        assertTrue(core.isHDCEnabled());
        assertTrue(core.isWorkingMemoryEnabled());
        assertTrue(core.isEpisodicMemoryEnabled());
        assertTrue(core.isLLMReasoningEnabled());
        assertTrue(core.isMetaLearningEnabled());
        assertTrue(core.isGenerativeEnabled());
        
        // All should have the observation
        assertTrue(hdc.getMapper().getConceptCount() > 0);
        assertTrue(wm.size() > 0);
        assertTrue(em.size() > 0);
    }

    @Test
    void testGenerativeStatistics() {
        core.wireGenerative(genEngine);
        
        genEngine.generateText("test");
        genEngine.fuse(new String[]{"a", "b"});
        
        String stats = genEngine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Generative Engine Statistics"));
    }

    @Test
    void testGenerativeResultFields() {
        core.wireGenerative(genEngine);
        
        GenerativeEngine.GenerativeResult result = genEngine.generateText("test");
        
        assertNotNull(result.result);
        assertTrue(result.elapsedTimeMs >= 0);
        assertNotNull(result.type);
        assertNotNull(result.success);
        assertTrue(result.novelty >= 0.0 && result.novelty <= 1.0);
        assertTrue(result.coherence >= 0.0 && result.coherence <= 1.0);
    }

    @Test
    void testGenerativeWithNullHDC() {
        // HDC is optional for GenerativeEngine
        GenerativeEngine genEngineNoHDC = new GenerativeEngine(llmEngine, core, null);
        core.wireGenerative(genEngineNoHDC);
        
        assertTrue(core.isGenerativeEnabled());
        
        GenerativeEngine.GenerativeResult result = genEngineNoHDC.generateText("test");
        assertNotNull(result);
    }
}
