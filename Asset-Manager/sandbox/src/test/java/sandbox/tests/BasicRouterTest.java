package sandbox.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sandbox.backends.MockBackend;
import sandbox.core.BackendInterface;
import sandbox.core.QueryAnalyzer;
import sandbox.routing.BasicRouter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Unit tests for BasicRouter
 */
public class BasicRouterTest {
    
    private BasicRouter router;
    private QueryAnalyzer queryAnalyzer;
    
    @BeforeEach
    public void setUp() throws Exception {
        queryAnalyzer = new QueryAnalyzer();
        router = new BasicRouter(queryAnalyzer);
        
        // Register mock backends
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            MockBackend backend = MockBackend.createMockBackend(type);
            backend.initialize();
            router.registerBackend(backend);
        }
    }
    
    @AfterEach
    public void tearDown() {
        // Unregister all backends
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            router.unregisterBackend(type);
        }
    }
    
    @Test
    public void testReasoningTaskRouting() {
        CompletableFuture<BackendInterface.QueryResult> future =
            router.route("Why is the sky blue?", Map.of());

        BackendInterface.QueryResult result = future.join();

        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("claude") ||
                   result.getContent().contains("ollama") ||
                   result.getContent().contains("neoClaw"));
    }
    
    @Test
    public void testGenerationTaskRouting() {
        CompletableFuture<BackendInterface.QueryResult> future = 
            router.route("Create a poem about nature", Map.of());
        
        BackendInterface.QueryResult result = future.join();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("gemma3") || 
                   result.getContent().contains("openclaw") ||
                   result.getContent().contains("ollama"));
    }
    
    @Test
    public void testKnowledgeTaskRouting() {
        CompletableFuture<BackendInterface.QueryResult> future = 
            router.route("What is the capital of France?", Map.of());
        
        BackendInterface.QueryResult result = future.join();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("obsidian") || 
                   result.getContent().contains("ollama") ||
                   result.getContent().contains("neoClaw"));
    }
    
    @Test
    public void testFastInferenceTaskRouting() {
        CompletableFuture<BackendInterface.QueryResult> future = 
            router.route("Quick answer: what is 2+2?", Map.of());
        
        BackendInterface.QueryResult result = future.join();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("ollama") || 
                   result.getContent().contains("openclaw") ||
                   result.getContent().contains("neoClaw"));
    }
    
    @Test
    public void testFallbackChain() throws Exception {
        // Re-register claude with high failure rate
        router.unregisterBackend(BackendInterface.BackendType.CLAUDE);
        MockBackend failingClaude = MockBackend.createMockBackend(BackendInterface.BackendType.CLAUDE);
        failingClaude.setFailureRate(1.0); // Always fail
        failingClaude.initialize();
        router.registerBackend(failingClaude);
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            router.route("Why is the sky blue?", Map.of());
        
        BackendInterface.QueryResult result = future.join();
        
        // Should fallback to ollama or neoClaw
        assertTrue(result.isSuccess());
        assertFalse(result.getContent().contains("claude"));
    }
    
    @Test
    public void testPerformanceMetricsTracking() {
        // Execute several queries
        for (int i = 0; i < 10; i++) {
            router.route("Test query " + i, Map.of()).join();
        }
        
        Map<BackendInterface.BackendType, BasicRouter.BackendPerformanceMetrics> metrics = 
            router.getPerformanceMetrics();
        
        // At least one backend should have metrics
        assertTrue(metrics.values().stream().anyMatch(m -> m.getTotalRequests() > 0));
    }
    
    @Test
    public void testUnhealthyBackendSkipped() throws Exception {
        // Set ollama to unhealthy
        router.unregisterBackend(BackendInterface.BackendType.OLLAMA);
        MockBackend unhealthyOllama = MockBackend.createMockBackend(BackendInterface.BackendType.OLLAMA);
        unhealthyOllama.setHealthy(false);
        unhealthyOllama.initialize();
        router.registerBackend(unhealthyOllama);
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            router.route("Quick answer", Map.of());
        
        BackendInterface.QueryResult result = future.join();
        
        // Should not use ollama
        assertTrue(result.isSuccess());
        assertFalse(result.getContent().contains("ollama"));
    }
    
    @Test
    public void testMetricsReset() {
        // Execute queries
        router.route("Test", Map.of()).join();
        router.route("Test", Map.of()).join();
        
        // Reset metrics
        router.resetMetrics();
        
        // Metrics should be zero
        Map<BackendInterface.BackendType, BasicRouter.BackendPerformanceMetrics> metrics = 
            router.getPerformanceMetrics();
        
        assertTrue(metrics.values().stream().allMatch(m -> m.getTotalRequests() == 0));
    }
    
    @Test
    public void testAllBackendsFailure() throws Exception {
        // Set all backends to fail
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            router.unregisterBackend(type);
            MockBackend failing = MockBackend.createMockBackend(type);
            failing.setFailureRate(1.0);
            failing.initialize();
            router.registerBackend(failing);
        }
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            router.route("Test query", Map.of());
        
        BackendInterface.QueryResult result = future.join();
        
        // Should fail with error message
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("All backends in routing chain failed"));
    }
}
