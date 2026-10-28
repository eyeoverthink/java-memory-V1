package sandbox.backends;

import sandbox.core.BackendInterface;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock backend implementation for testing purposes.
 * 
 * This backend simulates the behavior of real backends without requiring
 * actual connections. It can be configured to simulate success, failure,
 * latency, and other characteristics for testing the orchestrator.
 */
public class MockBackend implements BackendInterface {
    
    private final BackendType backendType;
    private final BackendCapabilities capabilities;
    private boolean healthy = true;
    private double failureRate = 0.0; // 0.0 to 1.0
    private int minLatencyMs = 10;
    private int maxLatencyMs = 100;
    private boolean initialized = false;
    
    public MockBackend(BackendType backendType, BackendCapabilities capabilities) {
        this.backendType = backendType;
        this.capabilities = capabilities;
    }
    
    @Override
    public CompletableFuture<QueryResult> executeQuery(String query, TaskType taskType, Map<String, Object> parameters) {
        if (!initialized) {
            return CompletableFuture.completedFuture(
                new QueryResult("", 0.0, 0, false, "Backend not initialized", Map.of())
            );
        }
        
        if (!healthy) {
            return CompletableFuture.completedFuture(
                new QueryResult("", 0.0, 0, false, "Backend unhealthy", Map.of())
            );
        }
        
        // Simulate random failure based on failure rate
        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            return CompletableFuture.completedFuture(
                new QueryResult("", 0.0, 0, false, "Simulated failure", Map.of())
            );
        }
        
        // Simulate latency
        int latency = ThreadLocalRandom.current().nextInt(minLatencyMs, maxLatencyMs + 1);
        
        try {
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generate mock response
        String response = generateMockResponse(query, taskType, backendType);
        
        return CompletableFuture.completedFuture(
            new QueryResult(
                response,
                0.8 + ThreadLocalRandom.current().nextDouble() * 0.2, // 0.8-1.0 confidence
                latency,
                true,
                null,
                Map.of(
                    "backend", backendType.getId(),
                    "taskType", taskType.getId(),
                    "simulated", true
                )
            )
        );
    }
    
    /**
     * Generate a mock response based on query and backend type
     */
    private String generateMockResponse(String query, TaskType taskType, BackendType backendType) {
        return String.format(
            "[MOCK %s] Response to: %s (Task: %s)",
            backendType.getId(),
            query.substring(0, Math.min(query.length(), 50)),
            taskType.getId()
        );
    }
    
    @Override
    public BackendType getBackendType() {
        return backendType;
    }
    
    @Override
    public BackendCapabilities getCapabilities() {
        return capabilities;
    }
    
    @Override
    public boolean isHealthy() {
        return healthy;
    }
    
    @Override
    public Map<String, Object> getHealthStatus() {
        return Map.of(
            "healthy", healthy,
            "initialized", initialized,
            "failureRate", failureRate,
            "minLatencyMs", minLatencyMs,
            "maxLatencyMs", maxLatencyMs
        );
    }
    
    @Override
    public void initialize() throws Exception {
        initialized = true;
    }
    
    @Override
    public void shutdown() {
        initialized = false;
    }
    
    // Configuration methods for testing
    
    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }
    
    public void setFailureRate(double failureRate) {
        this.failureRate = Math.max(0.0, Math.min(1.0, failureRate));
    }
    
    public void setLatencyRange(int minMs, int maxMs) {
        this.minLatencyMs = minMs;
        this.maxLatencyMs = maxMs;
    }
    
    /**
     * Factory method to create mock backends for each type
     */
    public static MockBackend createMockBackend(BackendType type) {
        BackendCapabilities capabilities = switch (type) {
            case OPENCLAW -> new BackendCapabilities(
                type, true, true, false, true, true, 0.8, 0.7
            );
            case NEOCLAW -> new BackendCapabilities(
                type, true, true, true, true, true, 0.9, 0.8
            );
            case OBSIDIAN -> new BackendCapabilities(
                type, false, false, true, true, false, 0.9, 0.9
            );
            case OLLAMA -> new BackendCapabilities(
                type, true, true, true, true, true, 0.6, 0.7
            );
            case GEMMA3 -> new BackendCapabilities(
                type, true, true, true, false, true, 0.3, 0.8
            );
            case CLAUDE -> new BackendCapabilities(
                type, true, true, true, false, true, 0.3, 0.95
            );
        };
        
        return new MockBackend(type, capabilities);
    }
}
