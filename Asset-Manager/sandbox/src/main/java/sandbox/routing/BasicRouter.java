package sandbox.routing;

import sandbox.core.BackendInterface;
import sandbox.core.QueryAnalyzer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic router for the Hybrid AI Orchestrator.
 * 
 * This router uses static routing rules based on task type, backend capabilities,
 * and simple fallback chains. It serves as the foundation before evolutionary
 * features are added.
 */
public class BasicRouter {
    
    private final QueryAnalyzer queryAnalyzer;
    private final Map<BackendInterface.BackendType, BackendInterface> backends;
    private final Map<BackendInterface.TaskType, List<BackendInterface.BackendType>> routingRules;
    private final Map<BackendInterface.BackendType, BackendPerformanceMetrics> performanceMetrics;
    
    public BasicRouter(QueryAnalyzer queryAnalyzer) {
        this.queryAnalyzer = queryAnalyzer;
        this.backends = new ConcurrentHashMap<>();
        this.routingRules = initializeRoutingRules();
        this.performanceMetrics = new ConcurrentHashMap<>();
    }
    
    /**
     * Initialize static routing rules based on backend capabilities
     */
    private Map<BackendInterface.TaskType, List<BackendInterface.BackendType>> initializeRoutingRules() {
        Map<BackendInterface.TaskType, List<BackendInterface.BackendType>> rules = new HashMap<>();
        
        // Reasoning tasks: claude → ollama → neoClaw
        rules.put(BackendInterface.TaskType.REASONING, Arrays.asList(
            BackendInterface.BackendType.CLAUDE,
            BackendInterface.BackendType.OLLAMA,
            BackendInterface.BackendType.NEOCLAW
        ));
        
        // Generation tasks: gemma3 → openclaw → ollama
        rules.put(BackendInterface.TaskType.GENERATION, Arrays.asList(
            BackendInterface.BackendType.GEMMA3,
            BackendInterface.BackendType.OPENCLAW,
            BackendInterface.BackendType.OLLAMA
        ));
        
        // Knowledge tasks: obsidian → ollama → neoClaw
        rules.put(BackendInterface.TaskType.KNOWLEDGE, Arrays.asList(
            BackendInterface.BackendType.OBSIDIAN,
            BackendInterface.BackendType.OLLAMA,
            BackendInterface.BackendType.NEOCLAW
        ));
        
        // Fast inference: ollama → openclaw → neoClaw
        rules.put(BackendInterface.TaskType.FAST_INFERENCE, Arrays.asList(
            BackendInterface.BackendType.OLLAMA,
            BackendInterface.BackendType.OPENCLAW,
            BackendInterface.BackendType.NEOCLAW
        ));
        
        // Complex analysis: claude → gemma3 → ollama
        rules.put(BackendInterface.TaskType.COMPLEX_ANALYSIS, Arrays.asList(
            BackendInterface.BackendType.CLAUDE,
            BackendInterface.BackendType.GEMMA3,
            BackendInterface.BackendType.OLLAMA
        ));
        
        return rules;
    }
    
    /**
     * Register a backend with the router
     * 
     * @param backend The backend to register
     */
    public void registerBackend(BackendInterface backend) {
        backends.put(backend.getBackendType(), backend);
        performanceMetrics.put(backend.getBackendType(), new BackendPerformanceMetrics());
    }
    
    /**
     * Unregister a backend from the router
     * 
     * @param backendType The backend type to unregister
     */
    public void unregisterBackend(BackendInterface.BackendType backendType) {
        backends.remove(backendType);
        performanceMetrics.remove(backendType);
    }
    
    /**
     * Route a query to the appropriate backend(s)
     * 
     * @param query The query to route
     * @param parameters Additional parameters for the backend
     * @return CompletableFuture containing the query result
     */
    public CompletableFuture<BackendInterface.QueryResult> route(String query, Map<String, Object> parameters) {
        // Analyze the query
        QueryAnalyzer.AnalysisResult analysis = queryAnalyzer.analyze(query);
        
        // Get routing chain for this task type
        List<BackendInterface.BackendType> routingChain = routingRules.getOrDefault(
            analysis.getTaskType(),
            Collections.singletonList(BackendInterface.BackendType.OLLAMA)
        );
        
        // Try each backend in the routing chain
        return tryRoutingChain(query, analysis, parameters, routingChain, 0);
    }
    
    /**
     * Try each backend in the routing chain until one succeeds
     */
    private CompletableFuture<BackendInterface.QueryResult> tryRoutingChain(
            String query,
            QueryAnalyzer.AnalysisResult analysis,
            Map<String, Object> parameters,
            List<BackendInterface.BackendType> routingChain,
            int index) {
        
        if (index >= routingChain.size()) {
            // All backends in chain failed
            return CompletableFuture.completedFuture(
                new BackendInterface.QueryResult(
                    "",
                    0.0,
                    0,
                    false,
                    "All backends in routing chain failed",
                    Map.of("routingChain", routingChain)
                )
            );
        }
        
        BackendInterface.BackendType backendType = routingChain.get(index);
        BackendInterface backend = backends.get(backendType);
        
        if (backend == null || !backend.isHealthy()) {
            // Backend not available, try next
            return tryRoutingChain(query, analysis, parameters, routingChain, index + 1);
        }
        
        // Execute query on this backend
        long startTime = System.currentTimeMillis();
        return backend.executeQuery(query, analysis.getTaskType(), parameters)
            .thenApply(result -> {
                long latency = System.currentTimeMillis() - startTime;
                
                // Update performance metrics
                updateMetrics(backendType, result, latency);
                
                if (result.isSuccess()) {
                    return result;
                } else {
                    // Backend failed, try next in chain
                    return tryRoutingChain(query, analysis, parameters, routingChain, index + 1).join();
                }
            })
            .exceptionally(ex -> {
                // Exception occurred, try next in chain
                updateMetrics(backendType, null, System.currentTimeMillis() - startTime);
                return tryRoutingChain(query, analysis, parameters, routingChain, index + 1).join();
            });
    }
    
    /**
     * Update performance metrics for a backend
     */
    private void updateMetrics(BackendInterface.BackendType backendType, 
                               BackendInterface.QueryResult result, long latency) {
        BackendPerformanceMetrics metrics = performanceMetrics.get(backendType);
        if (metrics != null) {
            metrics.recordRequest(result != null && result.isSuccess(), latency);
        }
    }
    
    /**
     * Get performance metrics for all backends
     * 
     * @return Map of backend type to performance metrics
     */
    public Map<BackendInterface.BackendType, BackendPerformanceMetrics> getPerformanceMetrics() {
        return new HashMap<>(performanceMetrics);
    }
    
    /**
     * Get performance metrics for a specific backend
     * 
     * @param backendType The backend type
     * @return Performance metrics for the backend
     */
    public BackendPerformanceMetrics getPerformanceMetrics(BackendInterface.BackendType backendType) {
        return performanceMetrics.get(backendType);
    }
    
    /**
     * Reset all performance metrics
     */
    public void resetMetrics() {
        performanceMetrics.replaceAll((k, v) -> new BackendPerformanceMetrics());
    }
    
    /**
     * Get the query analyzer (protected access for subclasses)
     */
    protected QueryAnalyzer getQueryAnalyzer() {
        return queryAnalyzer;
    }
    
    /**
     * Get the backends map (protected access for subclasses)
     */
    protected Map<BackendInterface.BackendType, BackendInterface> getBackends() {
        return new HashMap<>(backends);
    }
    
    /**
     * Performance metrics for a backend
     */
    public static class BackendPerformanceMetrics {
        private long totalRequests = 0;
        private long successfulRequests = 0;
        private long totalLatencyMs = 0;
        private long minLatencyMs = Long.MAX_VALUE;
        private long maxLatencyMs = 0;
        
        public synchronized void recordRequest(boolean success, long latencyMs) {
            totalRequests++;
            if (success) successfulRequests++;
            totalLatencyMs += latencyMs;
            minLatencyMs = Math.min(minLatencyMs, latencyMs);
            maxLatencyMs = Math.max(maxLatencyMs, latencyMs);
        }
        
        public synchronized double getSuccessRate() {
            return totalRequests == 0 ? 0.0 : (double) successfulRequests / totalRequests;
        }
        
        public synchronized double getAverageLatencyMs() {
            return totalRequests == 0 ? 0.0 : (double) totalLatencyMs / totalRequests;
        }
        
        public synchronized long getMinLatencyMs() {
            return totalRequests == 0 ? 0 : minLatencyMs;
        }
        
        public synchronized long getMaxLatencyMs() {
            return maxLatencyMs;
        }
        
        public synchronized long getTotalRequests() {
            return totalRequests;
        }
    }
}
