package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LLMReasoningEngine
 * 
 * Phase 2: Reasoning Enhancement
 * 
 * Tests cover:
 * - Constructor variants
 * - Connection testing
 * - Recursive reasoning
 * - Counterfactual simulation
 * - Enhanced analogy
 * - General reasoning
 * - Statistics tracking
 * - Error handling
 */
public class LLMReasoningEngineTest {
    
    private LLMReasoningEngine engine;
    
    @BeforeEach
    void setUp() {
        // Use cloud by default (more reliable)
        engine = new LLMReasoningEngine(true, "qwen3-coder-next");
    }
    
    @Test
    void testConstructorDefault() {
        LLMReasoningEngine defaultEngine = new LLMReasoningEngine();
        assertNotNull(defaultEngine);
        assertEquals("qwen3-coder-next", defaultEngine.getModel());
        assertTrue(defaultEngine.isCloud());
    }
    
    @Test
    void testConstructorWithModel() {
        LLMReasoningEngine customEngine = new LLMReasoningEngine(true, "deepseek-v3.2");
        assertNotNull(customEngine);
        assertEquals("deepseek-v3.2", customEngine.getModel());
        assertTrue(customEngine.isCloud());
    }
    
    @Test
    void testConstructorLocal() {
        LLMReasoningEngine localEngine = new LLMReasoningEngine(false, "gpt-oss:120b");
        assertNotNull(localEngine);
        assertEquals("gpt-oss:120b", localEngine.getModel());
        assertFalse(localEngine.isCloud());
    }
    
    @Test
    void testGetModel() {
        assertEquals("qwen3-coder-next", engine.getModel());
    }
    
    @Test
    void testIsCloud() {
        assertTrue(engine.isCloud());
    }
    
    @Test
    void testTestConnection() {
        // This test may fail if Ollama is not running
        // We'll just verify it doesn't throw
        boolean connected = engine.testConnection();
        // Result depends on Ollama availability
        assertNotNull(connected);
    }
    
    @Test
    void testRecursiveReason() {
        // Test with a simple reasoning question
        LLMReasoningEngine.ReasoningResult result = engine.recursiveReason(
            "If all cats are animals, and Fluffy is a cat, is Fluffy an animal?",
            3
        );
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("recursive", result.reasoningType);
        assertTrue(result.elapsedTimeMs >= 0);
        
        // If connected, should succeed
        if (engine.testConnection()) {
            assertTrue(result.success);
            assertFalse(result.result.contains("ERROR"));
        }
    }
    
    @Test
    void testRecursiveReasonWithMaxHops() {
        LLMReasoningEngine.ReasoningResult result = engine.recursiveReason(
            "What is 2 + 2?",
            5
        );
        
        assertNotNull(result);
        assertEquals("recursive", result.reasoningType);
    }
    
    @Test
    void testCounterfactualSimulate() {
        LLMReasoningEngine.ReasoningResult result = engine.counterfactualSimulate(
            "The sun rises in the east and sets in the west.",
            "The sun rises in the west."
        );
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("counterfactual", result.reasoningType);
        assertTrue(result.elapsedTimeMs >= 0);
        
        // If connected, should succeed
        if (engine.testConnection()) {
            assertTrue(result.success);
        }
    }
    
    @Test
    void testEnhancedAnalogy() {
        LLMReasoningEngine.ReasoningResult result = engine.enhancedAnalogy(
            "hot",
            "cold",
            "big"
        );
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("analogy", result.reasoningType);
        assertTrue(result.elapsedTimeMs >= 0);
        
        // If connected, should succeed
        if (engine.testConnection()) {
            assertTrue(result.success);
        }
    }
    
    @Test
    void testEnhancedAnalogyComplex() {
        LLMReasoningEngine.ReasoningResult result = engine.enhancedAnalogy(
            "doctor",
            "hospital",
            "teacher"
        );
        
        assertNotNull(result);
        assertEquals("analogy", result.reasoningType);
    }
    
    @Test
    void testGeneralReason() {
        LLMReasoningEngine.ReasoningResult result = engine.reason(
            "Explain what a computer is in one sentence."
        );
        
        assertNotNull(result);
        assertNotNull(result.result);
        assertEquals("general", result.reasoningType);
        assertTrue(result.elapsedTimeMs >= 0);
        
        // If connected, should succeed
        if (engine.testConnection()) {
            assertTrue(result.success);
        }
    }
    
    @Test
    void testGetStats() {
        String stats = engine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("LLM Reasoning Statistics"));
        assertTrue(stats.contains("Model:"));
        assertTrue(stats.contains("Mode:"));
        assertTrue(stats.contains("Recursive reasoning: 0"));
        assertTrue(stats.contains("Counterfactual simulations: 0"));
        assertTrue(stats.contains("Enhanced analogies: 0"));
    }
    
    @Test
    void testGetStatsAfterOperations() {
        // Perform some operations
        if (engine.testConnection()) {
            engine.recursiveReason("Test question", 2);
            engine.counterfactualSimulate("Test scenario", "Test alternative");
            engine.enhancedAnalogy("A", "B", "C");
        }
        
        String stats = engine.getStats();
        assertNotNull(stats);
        
        if (engine.testConnection()) {
            assertTrue(stats.contains("Recursive reasoning: 1"));
            assertTrue(stats.contains("Counterfactual simulations: 1"));
            assertTrue(stats.contains("Enhanced analogies: 1"));
        }
    }
    
    @Test
    void testReasoningResultFields() {
        LLMReasoningEngine.ReasoningResult result = engine.recursiveReason("Test", 1);
        
        assertNotNull(result.result);
        assertTrue(result.elapsedTimeMs >= 0);
        assertNotNull(result.reasoningType);
        assertNotNull(result.success);
    }
    
    @Test
    void testMultipleOperations() {
        if (!engine.testConnection()) {
            return; // Skip if not connected
        }
        
        // Perform multiple operations
        engine.recursiveReason("Question 1", 2);
        engine.recursiveReason("Question 2", 2);
        engine.counterfactualSimulate("Scenario 1", "Alternative 1");
        
        String stats = engine.getStats();
        assertTrue(stats.contains("Recursive reasoning: 2"));
        assertTrue(stats.contains("Counterfactual simulations: 1"));
    }
    
    @Test
    void testNullInputs() {
        // Should handle null inputs gracefully
        LLMReasoningEngine.ReasoningResult result1 = engine.recursiveReason(null, 2);
        assertNotNull(result1);
        
        LLMReasoningEngine.ReasoningResult result2 = engine.counterfactualSimulate(null, null);
        assertNotNull(result2);
        
        LLMReasoningEngine.ReasoningResult result3 = engine.enhancedAnalogy(null, null, null);
        assertNotNull(result3);
    }
    
    @Test
    void testEmptyInputs() {
        // Should handle empty inputs gracefully
        LLMReasoningEngine.ReasoningResult result1 = engine.recursiveReason("", 2);
        assertNotNull(result1);
        
        LLMReasoningEngine.ReasoningResult result2 = engine.counterfactualSimulate("", "");
        assertNotNull(result2);
        
        LLMReasoningEngine.ReasoningResult result3 = engine.enhancedAnalogy("", "", "");
        assertNotNull(result3);
    }
    
    @Test
    void testMaxHopsZero() {
        LLMReasoningEngine.ReasoningResult result = engine.recursiveReason("Test", 0);
        assertNotNull(result);
        assertEquals("recursive", result.reasoningType);
    }
    
    @Test
    void testMaxHopsNegative() {
        LLMReasoningEngine.ReasoningResult result = engine.recursiveReason("Test", -1);
        assertNotNull(result);
        assertEquals("recursive", result.reasoningType);
    }
}
