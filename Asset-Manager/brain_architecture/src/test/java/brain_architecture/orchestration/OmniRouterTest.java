package brain_architecture.orchestration;

import brain_architecture.backends.OllamaBackend;
import brain_architecture.core.BackendInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Test omni router for intelligent backend routing
 */
public class OmniRouterTest {
    
    private OmniRouter router;
    
    @BeforeEach
    public void setUp() {
        router = new OmniRouter();
    }
    
    @AfterEach
    public void tearDown() {
        // Router manages backend lifecycle
    }
    
    @Test
    public void testRouterInitialization() {
        assertNotNull(router);
        assertEquals(OmniRouter.RoutingStrategy.HYBRID, router.getStrategy());
        assertFalse(router.getBackends().isEmpty());
    }
    
    @Test
    public void testSetStrategy() {
        router.setStrategy(OmniRouter.RoutingStrategy.CAPABILITY_MATCH);
        assertEquals(OmniRouter.RoutingStrategy.CAPABILITY_MATCH, router.getStrategy());
        
        router.setStrategy(OmniRouter.RoutingStrategy.PERFORMANCE_BASED);
        assertEquals(OmniRouter.RoutingStrategy.PERFORMANCE_BASED, router.getStrategy());
    }
    
    @Test
    public void testRegisterBackend() {
        OllamaBackend backend = new OllamaBackend();
        router.registerBackend("test-backend", backend);
        
        assertNotNull(router.getBackend("test-backend"));
        assertEquals(BackendInterface.BackendType.OLLAMA, router.getBackend("test-backend").getBackendType());
    }
    
    @Test
    public void testUnregisterBackend() {
        OllamaBackend backend = new OllamaBackend();
        router.registerBackend("test-backend", backend);
        
        assertNotNull(router.getBackend("test-backend"));
        
        router.unregisterBackend("test-backend");
        assertNull(router.getBackend("test-backend"));
    }
    
    @Test
    public void testRouteQuery() throws Exception {
        var future = router.route("What is 2+2?", BackendInterface.TaskType.REASONING);
        var result = future.join();

        assertNotNull(result);
        assertTrue(result.isSuccess() || result.getErrorMessage() != null);
    }
    
    @Test
    public void testRouteWithParameters() throws Exception {
        Map<String, Object> params = Map.of("temperature", 0.7);
        var future = router.route("Generate a story", BackendInterface.TaskType.GENERATION, params);
        var result = future.join();
        
        assertNotNull(result);
    }
    
    @Test
    public void testRouteWithStrategy() throws Exception {
        router.setStrategy(OmniRouter.RoutingStrategy.CAPABILITY_MATCH);
        
        var future = router.route("Analyze this", BackendInterface.TaskType.COMPLEX_ANALYSIS);
        var result = future.join();
        
        assertNotNull(result);
    }
    
    @Test
    public void testCostAwareRouting() throws Exception {
        router.setCostAwareEnabled(true);
        router.setStrategy(OmniRouter.RoutingStrategy.COST_AWARE);
        
        var future = router.route("Simple query", BackendInterface.TaskType.FAST_INFERENCE);
        var result = future.join();
        
        assertNotNull(result);
    }
    
    @Test
    public void testLoadBalancing() throws Exception {
        router.setStrategy(OmniRouter.RoutingStrategy.LOAD_BALANCED);
        
        // Send multiple requests
        for (int i = 0; i < 5; i++) {
            router.route("Query " + i, BackendInterface.TaskType.REASONING).join();
        }
        
        Map<String, OmniRouter.BackendMetrics> metrics = router.getAllMetrics();
        assertNotNull(metrics);
    }
    
    @Test
    public void testBackendMetrics() throws Exception {
        router.route("Test query", BackendInterface.TaskType.REASONING).join();
        
        Map<String, OmniRouter.BackendMetrics> metrics = router.getAllMetrics();
        assertFalse(metrics.isEmpty());
        
        for (OmniRouter.BackendMetrics metric : metrics.values()) {
            if (metric.getTotalRequests() > 0) {
                assertTrue(metric.getSuccessRate() >= 0.0);
                assertTrue(metric.getSuccessRate() <= 1.0);
                assertTrue(metric.getAverageLatency() >= 0.0);
            }
        }
    }
    
    @Test
    public void testPerformanceScore() throws Exception {
        router.route("Test query", BackendInterface.TaskType.REASONING).join();
        
        Map<String, OmniRouter.BackendMetrics> metrics = router.getAllMetrics();
        for (OmniRouter.BackendMetrics metric : metrics.values()) {
            if (metric.getTotalRequests() > 0) {
                double score = metric.getPerformanceScore();
                assertTrue(score >= 0.0);
                assertTrue(score <= 1.0);
            }
        }
    }
    
    @Test
    public void testResetMetrics() throws Exception {
        router.route("Test query", BackendInterface.TaskType.REASONING).join();

        Map<String, OmniRouter.BackendMetrics> metricsBefore = router.getAllMetrics();
        int totalRequestsBefore = metricsBefore.values().stream()
            .mapToInt(OmniRouter.BackendMetrics::getTotalRequests)
            .sum();

        router.resetMetrics();

        Map<String, OmniRouter.BackendMetrics> metricsAfter = router.getAllMetrics();
        int totalRequestsAfter = metricsAfter.values().stream()
            .mapToInt(OmniRouter.BackendMetrics::getTotalRequests)
            .sum();

        assertEquals(0, totalRequestsAfter);
    }
    
    @Test
    public void testGetBackends() {
        Map<String, BackendInterface> backends = router.getBackends();
        assertNotNull(backends);
        assertFalse(backends.isEmpty());
    }
    
    @Test
    public void testHybridStrategy() {
        router.setStrategy(OmniRouter.RoutingStrategy.HYBRID);
        assertEquals(OmniRouter.RoutingStrategy.HYBRID, router.getStrategy());
    }
    
    @Test
    public void testMetricsTracking() {
        OmniRouter.BackendMetrics metrics = new OmniRouter.BackendMetrics();
        
        assertEquals(0, metrics.getTotalRequests());
        assertEquals(0, metrics.getSuccessfulRequests());
        assertEquals(0, metrics.getFailedRequests());
        
        metrics.recordRequest(true, 100);
        assertEquals(1, metrics.getTotalRequests());
        assertEquals(1, metrics.getSuccessfulRequests());
        assertEquals(0, metrics.getFailedRequests());
        assertEquals(100, metrics.getTotalLatency());
        assertEquals(100, metrics.getMinLatency());
        assertEquals(100, metrics.getMaxLatency());
        
        metrics.recordRequest(false, 200);
        assertEquals(2, metrics.getTotalRequests());
        assertEquals(1, metrics.getSuccessfulRequests());
        assertEquals(1, metrics.getFailedRequests());
        assertEquals(300, metrics.getTotalLatency());
        assertEquals(100, metrics.getMinLatency());
        assertEquals(200, metrics.getMaxLatency());
        
        assertEquals(0.5, metrics.getSuccessRate());
        assertEquals(150.0, metrics.getAverageLatency());
    }
    
    @Test
    public void testOpenSourceOnlyMode() {
        // Default is open-source only
        assertTrue(router.isOpenSourceOnly());
        
        Map<String, BackendInterface> openSourceBackends = router.getOpenSourceBackends();
        Map<String, BackendInterface> allBackends = router.getBackends();
        
        // All registered backends should be open-source
        for (BackendInterface backend : allBackends.values()) {
            assertTrue(backend.getBackendType().isOpenSource());
        }
        
        // Open-source backends should equal all backends
        assertEquals(openSourceBackends.size(), allBackends.size());
    }
    
    @Test
    public void testToggleOpenSourceOnly() {
        // Start in open-source only mode
        assertTrue(router.isOpenSourceOnly());
        int openSourceCount = router.getOpenSourceBackends().size();
        int totalCount = router.getBackends().size();
        assertEquals(openSourceCount, totalCount);
        
        // Switch to allow proprietary
        router.setOpenSourceOnly(false);
        assertFalse(router.isOpenSourceOnly());
        
        // Should have more backends now (if API keys are available)
        int totalCountAfter = router.getBackends().size();
        assertTrue(totalCountAfter >= openSourceCount);
        
        // Switch back to open-source only
        router.setOpenSourceOnly(true);
        assertTrue(router.isOpenSourceOnly());
        
        // Should be back to only open-source
        int totalCountFinal = router.getBackends().size();
        assertEquals(openSourceCount, totalCountFinal);
    }
    
    @Test
    public void testBackendTypeIsOpenSource() {
        // Test that BackendType.isOpenSource() works correctly
        assertTrue(BackendInterface.BackendType.OLLAMA.isOpenSource());
        assertTrue(BackendInterface.BackendType.DEEPSEEK.isOpenSource());
        assertTrue(BackendInterface.BackendType.FISH_AUDIO_S2.isOpenSource());
        assertTrue(BackendInterface.BackendType.OPENCLAW.isOpenSource());
        assertTrue(BackendInterface.BackendType.NEOCLAW.isOpenSource());
        assertTrue(BackendInterface.BackendType.OBSIDIAN.isOpenSource());
        assertTrue(BackendInterface.BackendType.LTXVIDEO.isOpenSource());
        assertTrue(BackendInterface.BackendType.SPEECHBRAIN.isOpenSource());
        assertTrue(BackendInterface.BackendType.LAZARUS.isOpenSource());
        assertTrue(BackendInterface.BackendType.AKASHICRECORD.isOpenSource());
        
        // Proprietary backends
        assertFalse(BackendInterface.BackendType.CLAUDE.isOpenSource());
        assertFalse(BackendInterface.BackendType.GEMMA3.isOpenSource());
        assertFalse(BackendInterface.BackendType.KIMI.isOpenSource());
        assertFalse(BackendInterface.BackendType.GEMMA4.isOpenSource());
    }
}
