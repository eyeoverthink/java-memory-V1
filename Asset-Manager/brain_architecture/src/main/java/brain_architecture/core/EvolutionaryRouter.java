package brain_architecture.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Evolutionary router with adaptive routing based on performance.
 * TODO: Integrate from sandbox or implement fresh
 */
public class EvolutionaryRouter {
    private final BackendEnsemble backendEnsemble;
    private final EvolutionEngine evolutionEngine;
    
    public EvolutionaryRouter(BackendEnsemble backendEnsemble, EvolutionEngine evolutionEngine) {
        this.backendEnsemble = backendEnsemble;
        this.evolutionEngine = evolutionEngine;
    }
    
    public CompletableFuture<BackendInterface.QueryResult> route(
        String query, 
        BackendInterface.TaskType taskType, 
        Map<String, Object> parameters
    ) {
        // For now, delegate to basic routing
        // TODO: Implement evolutionary routing based on performance scores
        var healthyBackends = backendEnsemble.getHealthyBackends();
        var allBackends = backendEnsemble.getBackends();
        
        if (healthyBackends.isEmpty()) {
            if (allBackends.isEmpty()) {
                return CompletableFuture.completedFuture(
                    new BackendInterface.QueryResult(
                        "",
                        0.0,
                        0,
                        false,
                        "No backends registered",
                        Map.of()
                    )
                );
            }
            // Fall back to first available backend even if unhealthy
            System.out.println("No healthy backends, falling back to first available backend");
            return allBackends.get(0).executeQuery(query, taskType, parameters);
        }
        
        return healthyBackends.get(0).executeQuery(query, taskType, parameters);
    }
}
