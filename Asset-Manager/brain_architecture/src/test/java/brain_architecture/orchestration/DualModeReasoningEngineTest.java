package brain_architecture.orchestration;

import brain_architecture.core.BackendInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Test dual-mode reasoning engine
 */
public class DualModeReasoningEngineTest {

    private DualModeReasoningEngine engine;
    private BackendInterface mockBackend;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a simple mock backend that returns successful responses
        mockBackend = new BackendInterface() {
            @Override
            public BackendType getBackendType() {
                return BackendType.OLLAMA;
            }

            @Override
            public BackendCapabilities getCapabilities() {
                return new BackendCapabilities(true, true, true, true, true, 1000.0, 0.7);
            }

            @Override
            public void initialize() throws Exception {
                // No-op for mock
            }

            @Override
            public CompletableFuture<QueryResult> executeQuery(String query, TaskType taskType, Map<String, Object> parameters) {
                Map<String, Object> metadata = Map.of(
                    "reasoning_mode", parameters.getOrDefault("reasoning_mode", "instant"),
                    "task_type", taskType.toString()
                );
                // Add small delay to simulate real latency
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(5); // 5ms delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return new QueryResult("Mock response: " + query, 1.0, 10, true, null, metadata);
                });
            }

            @Override
            public boolean isHealthy() {
                return true;
            }

            @Override
            public void shutdown() {
                // No-op for mock
            }
        };
        engine = new DualModeReasoningEngine(mockBackend);
        engine.setMode(DualModeReasoningEngine.ReasoningMode.INSTANT);
        engine.resetStatistics();
    }

    @AfterEach
    public void tearDown() {
        if (mockBackend != null) {
            mockBackend.shutdown();
        }
    }
    
    @Test
    public void testInstantMode() throws Exception {
        engine.setMode(DualModeReasoningEngine.ReasoningMode.INSTANT);
        assertEquals(DualModeReasoningEngine.ReasoningMode.INSTANT, engine.getMode());
        
        var future = engine.query("Quick question", BackendInterface.TaskType.FAST_INFERENCE);
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertEquals("instant", result.getMetadata().get("reasoning_mode"));
    }
    
    @Test
    public void testThinkingMode() throws Exception {
        engine.setMode(DualModeReasoningEngine.ReasoningMode.THINKING);
        assertEquals(DualModeReasoningEngine.ReasoningMode.THINKING, engine.getMode());
        
        var future = engine.query("Analyze step by step", BackendInterface.TaskType.COMPLEX_ANALYSIS);
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertEquals("thinking", result.getMetadata().get("reasoning_mode"));
    }
    
    @Test
    public void testModeSwitch() {
        engine.setMode(DualModeReasoningEngine.ReasoningMode.INSTANT);
        assertEquals(DualModeReasoningEngine.ReasoningMode.INSTANT, engine.getMode());
        
        engine.setMode(DualModeReasoningEngine.ReasoningMode.THINKING);
        assertEquals(DualModeReasoningEngine.ReasoningMode.THINKING, engine.getMode());
    }
    
    @Test
    public void testAutoSwitchDisabled() throws Exception {
        engine.setMode(DualModeReasoningEngine.ReasoningMode.INSTANT);
        engine.setAutoSwitch(false);
        
        // Complex query should still use instant mode when auto-switch disabled
        var future = engine.query("Why does this happen? Explain step by step with logical reasoning.", 
                                  BackendInterface.TaskType.REASONING);
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertEquals("instant", result.getMetadata().get("reasoning_mode"));
    }
    
    @Test
    public void testAutoSwitchEnabled() throws Exception {
        engine.setMode(DualModeReasoningEngine.ReasoningMode.INSTANT);
        engine.setAutoSwitch(true);

        // Complex query should auto-switch to thinking mode
        // This query is designed to have complexity > 0.7
        var future = engine.query("Why does this happen? Explain how it works, analyze the implications, compare with alternatives, evaluate the options, and justify your conclusion. Therefore, if X then Y implies Z.",
                                  BackendInterface.TaskType.REASONING);
        var result = future.join();

        assertTrue(result.isSuccess());
        // Should switch to thinking for complex queries
        assertEquals("thinking", result.getMetadata().get("reasoning_mode"));
    }
    
    @Test
    public void testComplexityAnalysis() {
        // Simple query
        double complexity1 = engine.analyzeComplexity("Hello");
        assertTrue(complexity1 < 0.5);
        
        // Complex query with reasoning words
        double complexity2 = engine.analyzeComplexity("Why does this happen? Explain how it works and analyze the implications.");
        assertTrue(complexity2 > 0.5);
        
        // Very complex query
        double complexity3 = engine.analyzeComplexity("Why does this happen? Explain how it works, analyze the implications, and provide a mathematical proof. Therefore, we can conclude that if X then Y.");
        assertTrue(complexity3 > 0.7);
    }
    
    @Test
    public void testStatistics() throws Exception {
        engine.setMode(DualModeReasoningEngine.ReasoningMode.INSTANT);
        
        // Execute instant queries
        for (int i = 0; i < 3; i++) {
            var future = engine.query("Quick query", BackendInterface.TaskType.FAST_INFERENCE);
            future.join();
        }
        
        // Execute thinking queries
        engine.setMode(DualModeReasoningEngine.ReasoningMode.THINKING);
        for (int i = 0; i < 2; i++) {
            var future = engine.query("Complex analysis", BackendInterface.TaskType.COMPLEX_ANALYSIS);
            future.join();
        }
        
        Map<String, Object> stats = engine.getStatistics();
        
        assertEquals(3, stats.get("instantQueries"));
        assertEquals(2, stats.get("thinkingQueries"));
        assertTrue((Double) stats.get("avgInstantLatencyMs") > 0);
        assertTrue((Double) stats.get("avgThinkingLatencyMs") > 0);
    }
    
    @Test
    public void testStatisticsReset() throws Exception {
        engine.setMode(DualModeReasoningEngine.ReasoningMode.INSTANT);
        
        var future = engine.query("Test", BackendInterface.TaskType.FAST_INFERENCE);
        future.join();
        
        Map<String, Object> statsBefore = engine.getStatistics();
        assertEquals(1, statsBefore.get("instantQueries"));
        
        engine.resetStatistics();
        
        Map<String, Object> statsAfter = engine.getStatistics();
        assertEquals(0, statsAfter.get("instantQueries"));
    }
    
    @Test
    public void testSetBackend() throws Exception {
        BackendInterface newBackend = new BackendInterface() {
            @Override
            public BackendType getBackendType() {
                return BackendType.OLLAMA;
            }

            @Override
            public BackendCapabilities getCapabilities() {
                return new BackendCapabilities(true, true, true, true, true, 1000.0, 0.7);
            }

            @Override
            public void initialize() throws Exception {
                // No-op for mock
            }

            @Override
            public CompletableFuture<QueryResult> executeQuery(String query, TaskType taskType, Map<String, Object> parameters) {
                Map<String, Object> metadata = Map.of(
                    "reasoning_mode", parameters.getOrDefault("reasoning_mode", "instant"),
                    "task_type", taskType.toString()
                );
                // Add small delay to simulate real latency
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(5); // 5ms delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return new QueryResult("New backend response: " + query, 1.0, 10, true, null, metadata);
                });
            }

            @Override
            public boolean isHealthy() {
                return true;
            }

            @Override
            public void shutdown() {
                // No-op for mock
            }
        };
        engine.setBackend(newBackend);

        var future = engine.query("Test", BackendInterface.TaskType.FAST_INFERENCE);
        var result = future.join();

        assertTrue(result.isSuccess());
        newBackend.shutdown();
    }
    
    @Test
    public void testModeDescriptions() {
        assertEquals("instant", DualModeReasoningEngine.ReasoningMode.INSTANT.getId());
        assertEquals("thinking", DualModeReasoningEngine.ReasoningMode.THINKING.getId());
        assertTrue(DualModeReasoningEngine.ReasoningMode.INSTANT.getDescription().contains("Fast"));
        assertTrue(DualModeReasoningEngine.ReasoningMode.THINKING.getDescription().contains("Slow"));
    }
}
