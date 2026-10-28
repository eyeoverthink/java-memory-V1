package brain_architecture.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Basic router for static routing with fallback chains.
 * TODO: Integrate from sandbox or implement fresh
 */
public class BasicRouter {
    private final BackendEnsemble backendEnsemble;
    
    public BasicRouter(BackendEnsemble backendEnsemble) {
        this.backendEnsemble = backendEnsemble;
    }
    
    public CompletableFuture<BackendInterface.QueryResult> route(
        String query, 
        BackendInterface.TaskType taskType, 
        Map<String, Object> parameters
    ) {
        // Simple routing: use first healthy backend
        var healthyBackends = backendEnsemble.getHealthyBackends();
        if (healthyBackends.isEmpty()) {
            return CompletableFuture.completedFuture(
                new BackendInterface.QueryResult(
                    "",
                    0.0,
                    0,
                    false,
                    "No healthy backends available",
                    Map.of()
                )
            );
        }
        
        return healthyBackends.get(0).executeQuery(query, taskType, parameters);
    }
}
