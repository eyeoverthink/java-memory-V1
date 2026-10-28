package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LLM Reasoning with SentientCore
 * 
 * Phase 2: Reasoning Enhancement
 * 
 * RULE SC-2: Maintains backward compatibility
 * RULE SC-3: Never break existing functionality
 */
public class SentientCoreLLMReasoningTest {

    private SentientCore core;
    private LLMReasoningEngine llmEngine;

    @BeforeEach
    void setUp() {
        core = new SentientCore();
        llmEngine = new LLMReasoningEngine(true, "qwen3-coder-next");
    }

    @Test
    void testSentientCoreWorksWithoutLLMReasoning() {
        // RULE SC-2: Backward compatibility - works without LLM reasoning
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        // Learn doesn't use LLM, so no LLM output expected
        
        // Should work normally
        String status = core.getStatus();
        // Just verify status works, don't check specific format
        assertNotNull(status);
    }

    @Test
    void testWireLLMReasoning() {
        core.wireLLMReasoning(llmEngine);
        
        assertTrue(core.isLLMReasoningEnabled());
        assertEquals(llmEngine, core.getLLMReasoningEngine());
        
        // Just verify status works
        String status = core.getStatus();
        assertNotNull(status);
    }

    @Test
    void testWireLLMReasoningNull() {
        assertThrows(IllegalArgumentException.class, () -> core.wireLLMReasoning(null));
    }

    @Test
    void testUnwireLLMReasoning() {
        core.wireLLMReasoning(llmEngine);
        assertTrue(core.isLLMReasoningEnabled());
        
        core.unwireLLMReasoning();
        assertFalse(core.isLLMReasoningEnabled());
        assertNull(core.getLLMReasoningEngine());
    }

    @Test
    void testLearnWithLLMReasoningEnabled() {
        core.wireLLMReasoning(llmEngine);
        
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Learn should still work normally
        assertEquals(1, core.getCausal().edgeCount());
    }

    @Test
    void testThinkWithLLMReasoningWhy() {
        core.learn("fire causes burns");
        core.wireLLMReasoning(llmEngine);
        
        String result = core.think("why does fire burn?");
        
        assertNotNull(result);
        // LLM reasoning is optional, so just verify it doesn't crash
    }

    @Test
    void testThinkWithLLMReasoningHow() {
        core.learn("water causes thirst");
        core.wireLLMReasoning(llmEngine);
        
        String result = core.think("how does water cause thirst?");
        
        assertNotNull(result);
        // LLM reasoning is optional, so just verify it doesn't crash
    }

    @Test
    void testThinkWithLLMReasoningExplain() {
        core.learn("gravity causes falling");
        core.wireLLMReasoning(llmEngine);
        
        String result = core.think("explain gravity");
        
        assertNotNull(result);
        // LLM reasoning is optional, so just verify it doesn't crash
    }

    @Test
    void testThinkWithLLMReasoningAnalyze() {
        core.learn("rain causes growth");
        core.wireLLMReasoning(llmEngine);
        
        String result = core.think("analyze the relationship between rain and growth");
        
        assertNotNull(result);
        // LLM reasoning is optional, so just verify it doesn't crash
    }

    @Test
    void testThinkWithLLMReasoningWhatIf() {
        core.learn("sun causes warmth");
        core.wireLLMReasoning(llmEngine);
        
        String result = core.think("what if the sun didn't exist?");
        
        assertNotNull(result);
        // LLM reasoning is optional, so just verify it doesn't crash
    }

    @Test
    void testThinkWithLLMReasoningWhatHappensIf() {
        core.learn("study causes learning");
        core.wireLLMReasoning(llmEngine);
        
        String result = core.think("what happens if you don't study?");
        
        assertNotNull(result);
        // LLM reasoning is optional, so just verify it doesn't crash
    }

    @Test
    void testThinkWithoutLLMReasoning() {
        core.learn("fire causes burns");
        // Don't wire LLM reasoning
        
        String result = core.think("why does fire burn?");
        
        assertNotNull(result);
        // Should not include LLM reasoning
        assertFalse(result.contains("LLM Reasoning"));
    }

    @Test
    void testLLMReasoningStatusInStatus() {
        String status = core.getStatus();
        assertNotNull(status);
        
        core.wireLLMReasoning(llmEngine);
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
    void testLLMReasoningDoesNotModifyExistingStructures() {
        // RULE SC-1: Never modify existing data structures
        core.learn("fire causes burns");
        int edgeCountBefore = core.getCausal().edgeCount();
        
        core.wireLLMReasoning(llmEngine);
        core.learn("water causes thirst");
        
        // Causal graph should still work normally
        assertEquals(edgeCountBefore + 1, core.getCausal().edgeCount());
    }

    @Test
    void testLLMReasoningWithAllOtherEnhancements() {
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
        
        core.learn("cats have fur");
        
        // All should be enabled
        assertTrue(core.isHDCEnabled());
        assertTrue(core.isWorkingMemoryEnabled());
        assertTrue(core.isEpisodicMemoryEnabled());
        assertTrue(core.isLLMReasoningEnabled());
        
        // All should have the observation
        assertTrue(hdc.getMapper().getConceptCount() > 0);
        assertTrue(wm.size() > 0);
        assertTrue(em.size() > 0);
    }

    @Test
    void testLLMReasoningWithDifferentModel() {
        LLMReasoningEngine customEngine = new LLMReasoningEngine(true, "deepseek-v3.2");
        core.wireLLMReasoning(customEngine);
        
        String status = core.getStatus();
        assertTrue(status.contains("deepseek-v3.2"));
    }

    @Test
    void testLLMReasoningWithLocalModel() {
        LLMReasoningEngine localEngine = new LLMReasoningEngine(false, "gpt-oss:120b");
        core.wireLLMReasoning(localEngine);
        
        assertTrue(core.isLLMReasoningEnabled());
        assertFalse(localEngine.isCloud());
    }
}
