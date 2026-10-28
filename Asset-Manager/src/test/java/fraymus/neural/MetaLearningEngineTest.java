package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MetaLearningEngine
 * 
 * Phase 3: Self-Improvement
 * 
 * Tests cover:
 * - Constructor validation
 * - Sentience analysis
 * - Architectural adaptation suggestions
 * - Concept drift detection
 * - Forgetting operations
 * - Statistics tracking
 * - Error handling
 */
public class MetaLearningEngineTest {
    
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
    void testConstructorWithValidParameters() {
        assertNotNull(metaEngine);
    }
    
    @Test
    void testConstructorWithNullLLMEngine() {
        assertThrows(IllegalArgumentException.class, () -> new MetaLearningEngine(null, core));
    }
    
    @Test
    void testConstructorWithNullCore() {
        assertThrows(IllegalArgumentException.class, () -> new MetaLearningEngine(llmEngine, null));
    }
    
    @Test
    void testConstructorWithBothNull() {
        assertThrows(IllegalArgumentException.class, () -> new MetaLearningEngine(null, null));
    }
    
    @Test
    void testAnalyzeSentience() {
        core.learn("fire causes burns");
        
        MetaLearningEngine.AnalysisResult result = metaEngine.analyzeSentience();
        
        assertNotNull(result);
        assertNotNull(result.suggestions);
        assertTrue(result.elapsedTimeMs >= 0);
        assertNotNull(result.sentience);
        
        // If connected, should succeed
        if (llmEngine.testConnection()) {
            assertTrue(result.success);
        }
    }
    
    @Test
    void testAnalyzeSentienceWithEmptyCore() {
        MetaLearningEngine.AnalysisResult result = metaEngine.analyzeSentience();
        
        assertNotNull(result);
        assertNotNull(result.suggestions);
        assertEquals(0.0f, result.sentience); // Empty core has 0 sentience
    }
    
    @Test
    void testSuggestAdaptation() {
        core.learn("water causes thirst");
        
        MetaLearningEngine.AdaptationSuggestion suggestion = metaEngine.suggestAdaptation();
        
        assertNotNull(suggestion);
        assertNotNull(suggestion.suggestion);
        assertTrue(suggestion.confidence >= 0.0 && suggestion.confidence <= 1.0);
        assertTrue(suggestion.elapsedTimeMs >= 0);
        
        // If connected, should succeed
        if (llmEngine.testConnection()) {
            assertTrue(suggestion.success);
        }
    }
    
    @Test
    void testSuggestAdaptationWithEmptyCore() {
        MetaLearningEngine.AdaptationSuggestion suggestion = metaEngine.suggestAdaptation();
        
        assertNotNull(suggestion);
        assertNotNull(suggestion.suggestion);
        assertTrue(suggestion.confidence >= 0.0 && suggestion.confidence <= 1.0);
    }
    
    @Test
    void testDetectConceptDrift() {
        core.learn("cats have fur");
        core.learn("dogs have fur");
        
        MetaLearningEngine.DriftDetectionResult result = metaEngine.detectConceptDrift();
        
        assertNotNull(result);
        assertNotNull(result.details);
        assertTrue(result.elapsedTimeMs >= 0);
        assertNotNull(result.driftDetected);
        
        // If connected, should succeed
        if (llmEngine.testConnection()) {
            assertTrue(result.success);
        }
    }
    
    @Test
    void testDetectConceptDriftWithEmptyCore() {
        MetaLearningEngine.DriftDetectionResult result = metaEngine.detectConceptDrift();
        
        assertNotNull(result);
        assertNotNull(result.details);
        assertFalse(result.driftDetected); // Empty core has no drift
    }
    
    @Test
    void testApplyForgetting() {
        int forgotten = metaEngine.applyForgetting();
        
        assertTrue(forgotten >= 0);
    }
    
    @Test
    void testApplyForgettingMultipleTimes() {
        int forgotten1 = metaEngine.applyForgetting();
        int forgotten2 = metaEngine.applyForgetting();
        
        // Should increment operations
        assertTrue(forgotten1 >= 0);
        assertTrue(forgotten2 >= 0);
    }
    
    @Test
    void testGetStats() {
        String stats = metaEngine.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Meta-Learning Statistics"));
        assertTrue(stats.contains("Adaptations: 0"));
        assertTrue(stats.contains("Forgetting operations: 0"));
        assertTrue(stats.contains("Concept drift detections: 0"));
    }
    
    @Test
    void testGetStatsAfterOperations() {
        if (llmEngine.testConnection()) {
            metaEngine.analyzeSentience();
            metaEngine.suggestAdaptation();
            metaEngine.detectConceptDrift();
            metaEngine.applyForgetting();
        }
        
        String stats = metaEngine.getStats();
        assertNotNull(stats);
        
        if (llmEngine.testConnection()) {
            assertTrue(stats.contains("Adaptations: 0")); // Adaptations not auto-applied
            assertTrue(stats.contains("Forgetting operations: 1"));
            assertTrue(stats.contains("Concept drift detections: 1"));
        }
    }
    
    @Test
    void testAnalysisResultFields() {
        MetaLearningEngine.AnalysisResult result = metaEngine.analyzeSentience();
        
        assertNotNull(result.suggestions);
        assertTrue(result.elapsedTimeMs >= 0);
        assertNotNull(result.sentience);
        assertNotNull(result.success);
    }
    
    @Test
    void testAdaptationSuggestionFields() {
        MetaLearningEngine.AdaptationSuggestion suggestion = metaEngine.suggestAdaptation();
        
        assertNotNull(suggestion.suggestion);
        assertTrue(suggestion.confidence >= 0.0 && suggestion.confidence <= 1.0);
        assertTrue(suggestion.elapsedTimeMs >= 0);
        assertNotNull(suggestion.success);
    }
    
    @Test
    void testDriftDetectionResultFields() {
        MetaLearningEngine.DriftDetectionResult result = metaEngine.detectConceptDrift();
        
        assertNotNull(result.driftDetected);
        assertNotNull(result.details);
        assertTrue(result.elapsedTimeMs >= 0);
        assertNotNull(result.success);
    }
    
    @Test
    void testMultipleAnalyses() {
        if (!llmEngine.testConnection()) {
            return; // Skip if not connected
        }
        
        core.learn("test causes result");
        
        metaEngine.analyzeSentience();
        metaEngine.analyzeSentience();
        metaEngine.analyzeSentience();
        
        String stats = metaEngine.getStats();
        assertNotNull(stats);
    }
    
    @Test
    void testConfidenceParsing() {
        // Test that confidence is always in valid range
        MetaLearningEngine.AdaptationSuggestion suggestion = metaEngine.suggestAdaptation();
        
        assertTrue(suggestion.confidence >= 0.0);
        assertTrue(suggestion.confidence <= 1.0);
    }
    
    @Test
    void testWithDifferentLLMModel() {
        LLMReasoningEngine customEngine = new LLMReasoningEngine(true, "deepseek-v3.2");
        MetaLearningEngine customMeta = new MetaLearningEngine(customEngine, core);
        
        assertNotNull(customMeta);
        
        MetaLearningEngine.AnalysisResult result = customMeta.analyzeSentience();
        assertNotNull(result);
    }
    
    @Test
    void testWithLocalLLM() {
        LLMReasoningEngine localEngine = new LLMReasoningEngine(false, "gpt-oss:120b");
        MetaLearningEngine localMeta = new MetaLearningEngine(localEngine, core);
        
        assertNotNull(localMeta);
        
        MetaLearningEngine.AnalysisResult result = localMeta.analyzeSentience();
        assertNotNull(result);
    }
}
