package sandbox.routing;

import sandbox.core.BackendInterface;
import sandbox.core.QueryAnalyzer;
import sandbox.evolution.EvolutionEngine;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Evolutionary Router that combines static routing with adaptive learning.
 * 
 * This router extends the BasicRouter by incorporating the EvolutionEngine
 * for adaptive route optimization based on performance data. It can switch
 * between static and evolutionary routing modes.
 */
public class EvolutionaryRouter extends BasicRouter {
    
    private final EvolutionEngine evolutionEngine;
    private boolean evolutionaryMode = false;
    
    public EvolutionaryRouter(QueryAnalyzer queryAnalyzer) {
        super(queryAnalyzer);
        this.evolutionEngine = new EvolutionEngine(this);
    }
    
    /**
     * Route a query with evolutionary routing enabled
     * 
     * @param query The query to route
     * @param parameters Additional parameters for the backend
     * @return CompletableFuture containing the query result
     */
    @Override
    public CompletableFuture<BackendInterface.QueryResult> route(String query, Map<String, Object> parameters) {
        if (evolutionaryMode) {
            return routeEvolutionary(query, parameters);
        } else {
            return super.route(query, parameters);
        }
    }
    
    /**
     * Route using evolutionary adaptation
     */
    private CompletableFuture<BackendInterface.QueryResult> routeEvolutionary(
            String query, Map<String, Object> parameters) {
        
        // Analyze the query
        QueryAnalyzer.AnalysisResult analysis = getQueryAnalyzer().analyze(query);
        
        // Get recommended backend from evolution engine
        Optional<BackendInterface.BackendType> recommended = 
            evolutionEngine.getRecommendedBackend(analysis.getTaskType());
        
        if (recommended.isPresent()) {
            BackendInterface.BackendType backendType = recommended.get();
            BackendInterface backend = getBackends().get(backendType);
            
            if (backend != null && backend.isHealthy()) {
                // Try recommended backend first
                long startTime = System.currentTimeMillis();
                return backend.executeQuery(query, analysis.getTaskType(), parameters)
                    .thenApply(result -> {
                        long latency = System.currentTimeMillis() - startTime;
                        
                        // Record result for evolution
                        evolutionEngine.recordResult(backendType, analysis.getTaskType(), result);
                        
                        if (result.isSuccess()) {
                            return result;
                        } else {
                            // Recommended backend failed, fallback to static routing
                            return super.route(query, parameters).join();
                        }
                    })
                    .exceptionally(ex -> {
                        // Exception occurred, fallback to static routing
                        return super.route(query, parameters).join();
                    });
            }
        }
        
        // No recommendation or backend unavailable, use static routing
        return super.route(query, parameters);
    }
    
    /**
     * Enable evolutionary routing mode
     */
    public void enableEvolutionaryMode() {
        this.evolutionaryMode = true;
    }
    
    /**
     * Disable evolutionary routing mode (use static routing)
     */
    public void disableEvolutionaryMode() {
        this.evolutionaryMode = false;
    }
    
    /**
     * Check if evolutionary mode is enabled
     */
    public boolean isEvolutionaryMode() {
        return evolutionaryMode;
    }
    
    /**
     * Get the evolution engine
     */
    public EvolutionEngine getEvolutionEngine() {
        return evolutionEngine;
    }
    
    /**
     * Force evolution of routes for a specific task type
     */
    public void forceEvolution(BackendInterface.TaskType taskType) {
        evolutionEngine.evolveRoutes(taskType);
    }
    
    /**
     * Force evolution of routes for all task types
     */
    public void forceEvolutionAll() {
        for (BackendInterface.TaskType taskType : BackendInterface.TaskType.values()) {
            evolutionEngine.evolveRoutes(taskType);
        }
    }
    
    /**
     * Get evolution statistics
     */
    public Map<String, Object> getEvolutionStatistics() {
        return evolutionEngine.getEvolutionStatistics();
    }
    
    /**
     * Reset evolution data
     */
    @Override
    public void resetMetrics() {
        super.resetMetrics();
        evolutionEngine.reset();
    }
}
