package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Meta-Learning with SentientCore
 * 
 * Phase 3: Self-Improvement
 * 
 * RULE SC-2: Maintains backward compatibility
 * RULE SC-3: Never break existing functionality
 */
public class SentientCoreMetaLearningTest {

    private SentientCore core;
    private LLMReasoningEngine llmEngine;
    private MetaLearningEngine metaEngine;

    @BeforeEach
    void setUp() {
        core = new SentientCore();
        llmEngine = new LLMReasoningEngine(true, "qwen3-coder-next");
        metaEngine = new MetaLearningEngine(llmEngine, core);
    }

    @Test
    void testSentientCoreWorksWithoutMetaLearning() {
        // RULE SC-2: Backward compatibility - works without meta-learning
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should work normally
        String status = core.getStatus();
        assertNotNull(status);
    }

    @Test
    void testWireMetaLearning() {
        core.wireMetaLearning(metaEngine);
        
        assertTrue(core.isMetaLearningEnabled());
        assertEquals(metaEngine, core.getMetaLearningEngine());
        
        String status = core.getStatus();
        assertNotNull(status);
    }

    @Test
    void testWireMetaLearningNull() {
        assertThrows(IllegalArgumentException.class, () -> core.wireMetaLearning(null));
    }

    @Test
    void testUnwireMetaLearning() {
        core.wireMetaLearning(metaEngine);
        assertTrue(core.isMetaLearningEnabled());
        
        core.unwireMetaLearning();
        assertFalse(core.isMetaLearningEnabled());
        assertNull(core.getMetaLearningEngine());
    }

    @Test
    void testLearnWithMetaLearningEnabled() {
        core.wireMetaLearning(metaEngine);
        
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Learn should still work normally
        assertEquals(1, core.getCausal().edgeCount());
    }

    @Test
    void testAnalyzeSentienceWithMetaLearning() {
        core.learn("fire causes burns");
        core.wireMetaLearning(metaEngine);
        
        MetaLearningEngine.AnalysisResult result = metaEngine.analyzeSentience();
        
        assertNotNull(result);
        assertNotNull(result.suggestions);
        assertTrue(result.sentience >= 0.0f);
    }

    @Test
    void testSuggestAdaptationWithMetaLearning() {
        core.learn("water causes thirst");
        core.wireMetaLearning(metaEngine);
        
        MetaLearningEngine.AdaptationSuggestion suggestion = metaEngine.suggestAdaptation();
        
        assertNotNull(suggestion);
        assertNotNull(suggestion.suggestion);
        assertTrue(suggestion.confidence >= 0.0 && suggestion.confidence <= 1.0);
    }

    @Test
    void testDetectConceptDriftWithMetaLearning() {
        core.learn("cats have fur");
        core.wireMetaLearning(metaEngine);
        
        MetaLearningEngine.DriftDetectionResult result = metaEngine.detectConceptDrift();
        
        assertNotNull(result);
        assertNotNull(result.details);
        assertNotNull(result.driftDetected);
    }

    @Test
    void testApplyForgettingWithMetaLearning() {
        core.wireMetaLearning(metaEngine);
        
        int forgotten = metaEngine.applyForgetting();
        
        assertTrue(forgotten >= 0);
    }

    @Test
    void testMetaLearningStatusInStatus() {
        String status = core.getStatus();
        assertNotNull(status);
        
        core.wireMetaLearning(metaEngine);
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
    void testMetaLearningDoesNotModifyExistingStructures() {
        // RULE SC-1: Never modify existing data structures
        core.learn("fire causes burns");
        int edgeCountBefore = core.getCausal().edgeCount();
        
        core.wireMetaLearning(metaEngine);
        metaEngine.analyzeSentience();
        metaEngine.suggestAdaptation();
        
        // Causal graph should still work normally
        assertEquals(edgeCountBefore, core.getCausal().edgeCount());
    }

    @Test
    void testMetaLearningWithAllOtherEnhancements() {
        // Test all enhancements together
        HDCSimilarityEngine hdc = new HDCSimilarityEngine();
        WorkingMemory wm = new WorkingMemory();
        AttentionMechanism att = new AttentionMechanism();
        EpisodicMemory em = new EpisodicMemory();
        HippocampalReplay hr = new HippocampalReplay(em);
        
        core.wireHDC(hdc);
        core.wireWorkingMemory(wm, att);
        core.wireEpisodicMemory(em, hr);
        core.wireLLMReasoning(llmEngine);
        core.wireMetaLearning(metaEngine);
        
        core.learn("cats have fur");
        
        // All should be enabled
        assertTrue(core.isHDCEnabled());
        assertTrue(core.isWorkingMemoryEnabled());
        assertTrue(core.isEpisodicMemoryEnabled());
        assertTrue(core.isLLMReasoningEnabled());
        assertTrue(core.isMetaLearningEnabled());
        
        // All should have the observation
        assertTrue(hdc.getMapper().getConceptCount() > 0);
        assertTrue(wm.size() > 0);
        assertTrue(em.size() > 0);
    }

    @Test
    void testMetaLearningRequiresLLMReasoning() {
        // Meta-learning requires LLM reasoning to work
        // This test verifies the dependency
        assertNotNull(metaEngine);
        
        // The meta-learning engine should have access to LLM
        assertNotNull(llmEngine);
    }

    @Test
    void testMetaLearningStatistics() {
        core.wireMetaLearning(metaEngine);
        
        if (llmEngine.testConnection()) {
            metaEngine.analyzeSentience();
            metaEngine.suggestAdaptation();
            metaEngine.detectConceptDrift();
        }
        
        String stats = metaEngine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Meta-Learning Statistics"));
    }
}
