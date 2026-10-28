package brain_architecture.orchestration;

import brain_architecture.core.BackendInterface;
import brain_architecture.backends.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OmniRouter - Intelligent Backend Routing
 * 
 * Dynamically routes queries to the most appropriate backend based on:
 * - Query characteristics (task type, complexity, latency requirements)
 * - Backend capabilities (reasoning, generation, knowledge, etc.)
 * - Backend health and availability
 * - Historical performance metrics
 * - Cost considerations
 * 
 * Features:
 * - Multi-factor routing decisions
 * - Load balancing across healthy backends
 * - Automatic failover on backend failures
 * - Performance-based backend ranking
 * - Cost-aware routing (optional)
 */
public class OmniRouter {
    
    /**
     * Routing strategy
     */
    public enum RoutingStrategy {
        CAPABILITY_MATCH,      // Route based on backend capabilities
        PERFORMANCE_BASED,     // Route based on historical performance
        LOAD_BALANCED,         // Distribute load evenly
        COST_AWARE,            // Consider cost factors
        HYBRID                 // Combine multiple strategies
    }
    
    private final Map<String, BackendInterface> backends;
    private RoutingStrategy strategy;
    private final Map<String, BackendMetrics> backendMetrics;
    private final Map<String, Integer> requestCounts;
    private boolean costAwareEnabled;
    private boolean openSourceOnly;
    
    public OmniRouter() {
        this(RoutingStrategy.HYBRID, true);
    }
    
    public OmniRouter(RoutingStrategy strategy) {
        this(strategy, true);
    }
    
    public OmniRouter(RoutingStrategy strategy, boolean openSourceOnly) {
        this.backends = new ConcurrentHashMap<>();
        this.strategy = strategy;
        this.backendMetrics = new ConcurrentHashMap<>();
        this.requestCounts = new ConcurrentHashMap<>();
        this.costAwareEnabled = false;
        this.openSourceOnly = openSourceOnly;
        
        // Register default backends
        registerDefaultBackends();
    }
    
    /**
     * Register default backends (only open-source by default)
     */
    private void registerDefaultBackends() {
        try {
            // Open-source / Local backends only
            registerBackend("ollama", new OllamaBackend());
            registerBackend("deepseek", new DeepSeekBackend());
            registerBackend("fish_audio_s2", new FishAudioS2Backend());
            
            // Optional: Register proprietary backends if openSourceOnly is false
            if (!openSourceOnly) {
                registerBackend("claude", new ClaudeBackend());
                registerBackend("gemma3", new Gemma3Backend());
                registerBackend("kimi", new KimiBackend());
                registerBackend("gemma4", new Gemma4Backend());
            }
        } catch (Exception e) {
            // Some backends may fail initialization (missing API keys)
            // This is expected behavior
        }
    }
    
    /**
     * Register a backend
     */
    public void registerBackend(String name, BackendInterface backend) {
        backends.put(name, backend);
        backendMetrics.put(name, new BackendMetrics());
        requestCounts.put(name, 0);
    }
    
    /**
     * Unregister a backend
     */
    public void unregisterBackend(String name) {
        BackendInterface backend = backends.remove(name);
        if (backend != null) {
            backend.shutdown();
        }
        backendMetrics.remove(name);
        requestCounts.remove(name);
    }
    
    /**
     * Get a backend by name
     */
    public BackendInterface getBackend(String name) {
        return backends.get(name);
    }
    
    /**
     * Get all registered backends
     */
    public Map<String, BackendInterface> getBackends() {
        return new HashMap<>(backends);
    }
    
    /**
     * Get only open-source backends
     */
    public Map<String, BackendInterface> getOpenSourceBackends() {
        Map<String, BackendInterface> openSource = new HashMap<>();
        for (Map.Entry<String, BackendInterface> entry : backends.entrySet()) {
            if (entry.getValue().getBackendType().isOpenSource()) {
                openSource.put(entry.getKey(), entry.getValue());
            }
        }
        return openSource;
    }
    
    /**
     * Check if router is in open-source only mode
     */
    public boolean isOpenSourceOnly() {
        return openSourceOnly;
    }
    
    /**
     * Toggle open-source only mode
     * If set to true, proprietary backends will be unregistered
     * If set to false, proprietary backends will be registered if available
     */
    public void setOpenSourceOnly(boolean openSourceOnly) {
        if (this.openSourceOnly == openSourceOnly) {
            return; // No change needed
        }
        
        this.openSourceOnly = openSourceOnly;
        
        if (openSourceOnly) {
            // Remove proprietary backends
            unregisterProprietaryBackends();
        } else {
            // Register proprietary backends
            registerProprietaryBackends();
        }
    }
    
    /**
     * Unregister all proprietary backends
     */
    private void unregisterProprietaryBackends() {
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, BackendInterface> entry : backends.entrySet()) {
            if (!entry.getValue().getBackendType().isOpenSource()) {
                toRemove.add(entry.getKey());
            }
        }
        for (String name : toRemove) {
            unregisterBackend(name);
        }
    }
    
    /**
     * Register proprietary backends
     */
    private void registerProprietaryBackends() {
        try {
            if (!backends.containsKey("claude")) {
                registerBackend("claude", new ClaudeBackend());
            }
            if (!backends.containsKey("gemma3")) {
                registerBackend("gemma3", new Gemma3Backend());
            }
            if (!backends.containsKey("kimi")) {
                registerBackend("kimi", new KimiBackend());
            }
            if (!backends.containsKey("gemma4")) {
                registerBackend("gemma4", new Gemma4Backend());
            }
        } catch (Exception e) {
            // Some backends may fail initialization (missing API keys)
        }
    }
    
    /**
     * Set routing strategy
     */
    public void setStrategy(RoutingStrategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Get current routing strategy
     */
    public RoutingStrategy getStrategy() {
        return strategy;
    }
    
    /**
     * Enable/disable cost-aware routing
     */
    public void setCostAwareEnabled(boolean enabled) {
        this.costAwareEnabled = enabled;
    }
    
    /**
     * Route a query to the best backend
     */
    public CompletableFuture<BackendInterface.QueryResult> route(
            String query, BackendInterface.TaskType taskType) {
        return route(query, taskType, Map.of());
    }
    
    /**
     * Route a query to the best backend with parameters
     */
    public CompletableFuture<BackendInterface.QueryResult> route(
            String query, BackendInterface.TaskType taskType, Map<String, Object> parameters) {
        
        String selectedBackend = selectBackend(query, taskType);
        
        if (selectedBackend == null) {
            return CompletableFuture.completedFuture(
                new BackendInterface.QueryResult(
                    "", 0.0, 0, false, "No suitable backend available", null
                )
            );
        }
        
        long startTime = System.currentTimeMillis();
        requestCounts.put(selectedBackend, requestCounts.getOrDefault(selectedBackend, 0) + 1);
        
        BackendInterface backend = backends.get(selectedBackend);
        return backend.executeQuery(query, taskType, parameters)
            .thenApply(result -> {
                long latency = System.currentTimeMillis() - startTime;
                updateMetrics(selectedBackend, result.isSuccess(), latency);
                return result;
            })
            .exceptionally(ex -> {
                long latency = System.currentTimeMillis() - startTime;
                updateMetrics(selectedBackend, false, latency);
                // Try fallback
                return routeWithFallback(query, taskType, parameters, selectedBackend).join();
            });
    }
    
    /**
     * Select the best backend for a query
     */
    private String selectBackend(String query, BackendInterface.TaskType taskType) {
        List<String> candidates = new ArrayList<>();
        
        for (Map.Entry<String, BackendInterface> entry : backends.entrySet()) {
            String name = entry.getKey();
            BackendInterface backend = entry.getValue();
            
            // Filter by open-source if in open-source only mode
            if (openSourceOnly && !backend.getBackendType().isOpenSource()) {
                continue;
            }
            
            // Check if backend is healthy and has required capabilities
            if (backend.isHealthy() && hasRequiredCapability(backend, taskType)) {
                candidates.add(name);
            }
        }
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        // Apply routing strategy
        switch (strategy) {
            case CAPABILITY_MATCH:
                return selectByCapabilityMatch(candidates, taskType);
            case PERFORMANCE_BASED:
                return selectByPerformance(candidates);
            case LOAD_BALANCED:
                return selectByLoadBalance(candidates);
            case COST_AWARE:
                return selectByCost(candidates);
            case HYBRID:
            default:
                return selectHybrid(candidates, query, taskType);
        }
    }
    
    /**
     * Check if backend has required capability
     */
    private boolean hasRequiredCapability(BackendInterface backend, BackendInterface.TaskType taskType) {
        BackendInterface.BackendCapabilities caps = backend.getCapabilities();
        
        switch (taskType) {
            case REASONING:
                return caps.supportsReasoning();
            case GENERATION:
                return caps.supportsGeneration();
            case KNOWLEDGE:
                return caps.supportsKnowledge();
            case FAST_INFERENCE:
                return caps.supportsFastInference();
            case COMPLEX_ANALYSIS:
                return caps.supportsComplexAnalysis();
            default:
                return true;
        }
    }
    
    /**
     * Select backend by capability match
     */
    private String selectByCapabilityMatch(List<String> candidates, BackendInterface.TaskType taskType) {
        String best = null;
        double bestScore = 0.0;
        
        for (String name : candidates) {
            BackendInterface backend = backends.get(name);
            BackendInterface.BackendCapabilities caps = backend.getCapabilities();
            
            double score = 0.0;
            switch (taskType) {
                case REASONING:
                    score = caps.getReasoningScore();
                    break;
                case GENERATION:
                    score = caps.getGenerationScore();
                    break;
                case KNOWLEDGE:
                    score = caps.getKnowledgeScore();
                    break;
                case FAST_INFERENCE:
                    score = caps.getFastInferenceScore();
                    break;
                case COMPLEX_ANALYSIS:
                    score = caps.getComplexAnalysisScore();
                    break;
            }
            
            if (score > bestScore) {
                bestScore = score;
                best = name;
            }
        }
        
        return best;
    }
    
    /**
     * Select backend by performance metrics
     */
    private String selectByPerformance(List<String> candidates) {
        String best = null;
        double bestScore = 0.0;
        
        for (String name : candidates) {
            BackendMetrics metrics = backendMetrics.get(name);
            double score = metrics.getPerformanceScore();
            
            if (score > bestScore) {
                bestScore = score;
                best = name;
            }
        }
        
        return best;
    }
    
    /**
     * Select backend by load balancing
     */
    private String selectByLoadBalance(List<String> candidates) {
        String best = null;
        int minCount = Integer.MAX_VALUE;
        
        for (String name : candidates) {
            int count = requestCounts.getOrDefault(name, 0);
            if (count < minCount) {
                minCount = count;
                best = name;
            }
        }
        
        return best;
    }
    
    /**
     * Select backend by cost (simplified - assumes certain backends are cheaper)
     */
    private String selectByCost(List<String> candidates) {
        // Cost ranking (lower is cheaper)
        Map<String, Integer> costRanking = Map.of(
            "ollama", 1,
            "gemma3", 2,
            "deepseek", 3,
            "kimi", 4,
            "gemma4", 5,
            "claude", 6
        );
        
        String best = null;
        int minCost = Integer.MAX_VALUE;
        
        for (String name : candidates) {
            int cost = costRanking.getOrDefault(name, 10);
            if (cost < minCost) {
                minCost = cost;
                best = name;
            }
        }
        
        return best;
    }
    
    /**
     * Hybrid selection combining multiple factors
     */
    private String selectHybrid(List<String> candidates, String query, BackendInterface.TaskType taskType) {
        // Weighted scoring: 40% capability, 30% performance, 20% load, 10% cost
        Map<String, Double> scores = new HashMap<>();
        
        for (String name : candidates) {
            BackendInterface backend = backends.get(name);
            BackendInterface.BackendCapabilities caps = backend.getCapabilities();
            BackendMetrics metrics = backendMetrics.get(name);
            int count = requestCounts.getOrDefault(name, 0);
            
            // Capability score
            double capScore = 0.0;
            switch (taskType) {
                case REASONING: capScore = caps.getReasoningScore(); break;
                case GENERATION: capScore = caps.getGenerationScore(); break;
                case KNOWLEDGE: capScore = caps.getKnowledgeScore(); break;
                case FAST_INFERENCE: capScore = caps.getFastInferenceScore(); break;
                case COMPLEX_ANALYSIS: capScore = caps.getComplexAnalysisScore(); break;
            }
            
            // Performance score
            double perfScore = metrics.getPerformanceScore();
            
            // Load score (inverse of request count)
            double loadScore = 1.0 / (1.0 + count);
            
            // Cost score (inverse of cost ranking)
            Map<String, Integer> costRanking = Map.of(
                "ollama", 1, "gemma3", 2, "deepseek", 3, "kimi", 4, "gemma4", 5, "claude", 6
            );
            int cost = costRanking.getOrDefault(name, 10);
            double costScore = 1.0 / cost;
            double queryAffinity = queryAffinityBonus(name, query, taskType);
            
            // Combined score
            double combinedScore = 0.38 * capScore + 0.28 * perfScore + 0.18 * loadScore + 0.08 * costScore + 0.08 * queryAffinity;
            scores.put(name, combinedScore);
        }
        
        // Return backend with highest score
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    private double queryAffinityBonus(String backendName, String query, BackendInterface.TaskType taskType) {
        String name = backendName == null ? "" : backendName.toLowerCase(Locale.ROOT);
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT);

        if (name.contains("deepseek")) {
            if (taskType == BackendInterface.TaskType.REASONING || taskType == BackendInterface.TaskType.COMPLEX_ANALYSIS) {
                return 0.95;
            }
            if (containsAny(q, "reason", "proof", "math", "derive", "algorithm", "compiler", "patch", "refactor")) {
                return 0.92;
            }
            return 0.72;
        }

        if (name.contains("ollama")) {
            if (taskType == BackendInterface.TaskType.FAST_INFERENCE || containsAny(q, "quick", "fast", "summary", "short")) {
                return 0.82;
            }
            return 0.64;
        }

        return 0.60;
    }

    private boolean containsAny(String text, String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Route with fallback to other backends
     */
    private CompletableFuture<BackendInterface.QueryResult> routeWithFallback(
            String query, BackendInterface.TaskType taskType, Map<String, Object> parameters,
            String failedBackend) {
        
        List<String> candidates = new ArrayList<>(backends.keySet());
        candidates.remove(failedBackend);
        
        for (String name : candidates) {
            BackendInterface backend = backends.get(name);
            if (backend.isHealthy() && hasRequiredCapability(backend, taskType)) {
                try {
                    CompletableFuture<BackendInterface.QueryResult> future = 
                        backend.executeQuery(query, taskType, parameters);
                    BackendInterface.QueryResult result = future.join();
                    if (result.isSuccess()) {
                        return CompletableFuture.completedFuture(result);
                    }
                } catch (Exception e) {
                    // Try next backend
                }
            }
        }
        
        return CompletableFuture.completedFuture(
            new BackendInterface.QueryResult(
                "", 0.0, 0, false, "All backends failed", null
            )
        );
    }
    
    /**
     * Update backend metrics
     */
    private void updateMetrics(String backendName, boolean success, long latency) {
        BackendMetrics metrics = backendMetrics.get(backendName);
        if (metrics != null) {
            metrics.recordRequest(success, latency);
        }
    }
    
    /**
     * Get backend metrics
     */
    public BackendMetrics getBackendMetrics(String backendName) {
        return backendMetrics.get(backendName);
    }
    
    /**
     * Get all backend metrics
     */
    public Map<String, BackendMetrics> getAllMetrics() {
        return new HashMap<>(backendMetrics);
    }
    
    /**
     * Reset all metrics
     */
    public void resetMetrics() {
        for (BackendMetrics metrics : backendMetrics.values()) {
            metrics.reset();
        }
        requestCounts.replaceAll((k, v) -> 0);
    }
    
    /**
     * Backend metrics tracking
     */
    public static class BackendMetrics {
        private int totalRequests;
        private int successfulRequests;
        private int failedRequests;
        private long totalLatency;
        private long minLatency = Long.MAX_VALUE;
        private long maxLatency = 0;
        
        public void recordRequest(boolean success, long latency) {
            totalRequests++;
            if (success) {
                successfulRequests++;
            } else {
                failedRequests++;
            }
            totalLatency += latency;
            minLatency = Math.min(minLatency, latency);
            maxLatency = Math.max(maxLatency, latency);
        }
        
        public double getSuccessRate() {
            return totalRequests > 0 ? (double) successfulRequests / totalRequests : 0.0;
        }
        
        public double getAverageLatency() {
            return totalRequests > 0 ? (double) totalLatency / totalRequests : 0.0;
        }
        
        public double getPerformanceScore() {
            // Performance score: 70% success rate, 30% inverse latency
            double successScore = getSuccessRate();
            double latencyScore = getAverageLatency() > 0 ? 
                1000.0 / (1000.0 + getAverageLatency()) : 0.0;
            return 0.7 * successScore + 0.3 * latencyScore;
        }
        
        public void reset() {
            totalRequests = 0;
            successfulRequests = 0;
            failedRequests = 0;
            totalLatency = 0;
            minLatency = Long.MAX_VALUE;
            maxLatency = 0;
        }
        
        public int getTotalRequests() { return totalRequests; }
        public int getSuccessfulRequests() { return successfulRequests; }
        public int getFailedRequests() { return failedRequests; }
        public long getTotalLatency() { return totalLatency; }
        public long getMinLatency() { return minLatency; }
        public long getMaxLatency() { return maxLatency; }
    }
}
