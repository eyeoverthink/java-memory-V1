package brain_architecture.core;

import brain_architecture.orchestration.OrchestratorBridge;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Backend ensemble for a brain - manages multiple backends with their capabilities, metrics, and health.
 * 
 * Supports optional orchestration features via OrchestratorBridge for advanced backend management
 * including discovery, self-healing, evolution, organic growth, and result synthesis.
 */
public class BackendEnsemble {
    private final List<BackendInterface> backends;
    private final Map<BackendInterface.BackendType, BackendInterface.BackendCapabilities> capabilities;
    private final Map<BackendInterface.BackendType, BackendPerformanceMetrics> metrics;
    private final Map<BackendInterface.BackendType, Boolean> healthStatus;
    private final Map<BackendInterface.BackendType, CircuitBreakerState> circuitBreakers;
    
    private OrchestratorBridge orchestratorBridge;
    
    public BackendEnsemble() {
        this.backends = new ArrayList<>();
        this.capabilities = new HashMap<>();
        this.metrics = new HashMap<>();
        this.healthStatus = new HashMap<>();
        this.circuitBreakers = new HashMap<>();
    }
    
    /**
     * Register a backend with this ensemble
     */
    public void registerBackend(BackendInterface backend) {
        backends.add(backend);
        capabilities.put(backend.getBackendType(), backend.getCapabilities());
        metrics.put(backend.getBackendType(), new BackendPerformanceMetrics());
        healthStatus.put(backend.getBackendType(), true);
        circuitBreakers.put(backend.getBackendType(), CircuitBreakerState.CLOSED);
    }
    
    /**
     * Unregister a backend from this ensemble
     */
    public void unregisterBackend(BackendInterface.BackendType backendType) {
        backends.removeIf(b -> b.getBackendType() == backendType);
        capabilities.remove(backendType);
        metrics.remove(backendType);
        healthStatus.remove(backendType);
        circuitBreakers.remove(backendType);
    }
    
    /**
     * Get all registered backends
     */
    public List<BackendInterface> getBackends() {
        return new ArrayList<>(backends);
    }
    
    /**
     * Get healthy backends
     */
    public List<BackendInterface> getHealthyBackends() {
        return backends.stream()
            .filter(b -> healthStatus.getOrDefault(b.getBackendType(), false))
            .filter(b -> circuitBreakers.getOrDefault(b.getBackendType(), CircuitBreakerState.CLOSED) != CircuitBreakerState.OPEN)
            .toList();
    }
    
    /**
     * Get backend capabilities
     */
    public BackendInterface.BackendCapabilities getCapabilities(BackendInterface.BackendType backendType) {
        return capabilities.get(backendType);
    }
    
    /**
     * Get backend performance metrics
     */
    public BackendPerformanceMetrics getMetrics(BackendInterface.BackendType backendType) {
        return metrics.get(backendType);
    }
    
    /**
     * Get backend health status
     */
    public Boolean getHealthStatus(BackendInterface.BackendType backendType) {
        return healthStatus.get(backendType);
    }
    
    /**
     * Set backend health status
     */
    public void setHealthStatus(BackendInterface.BackendType backendType, boolean healthy) {
        healthStatus.put(backendType, healthy);
    }
    
    /**
     * Get circuit breaker state
     */
    public CircuitBreakerState getCircuitBreakerState(BackendInterface.BackendType backendType) {
        return circuitBreakers.getOrDefault(backendType, CircuitBreakerState.CLOSED);
    }
    
    /**
     * Set circuit breaker state
     */
    public void setCircuitBreakerState(BackendInterface.BackendType backendType, CircuitBreakerState state) {
        circuitBreakers.put(backendType, state);
    }
    
    /**
     * Record a query result for metrics tracking
     * Also notifies orchestration components if wired
     */
    public void recordResult(BackendInterface.BackendType backendType, BackendInterface.QueryResult result) {
        BackendPerformanceMetrics metrics = this.metrics.get(backendType);
        if (metrics != null) {
            metrics.recordResult(result);
        }
        
        // Notify orchestration components if wired
        if (orchestratorBridge != null) {
            // Self-healing: record request for circuit breaker
            if (orchestratorBridge.getSelfHealing() != null) {
                orchestratorBridge.getSelfHealing().recordRequest(
                    backendType, 
                    result.isSuccess(), 
                    result.getLatencyMs()
                );
            }
            
            // Evolution: record result for performance tracking
            if (orchestratorBridge.getEvolutionEngine() != null) {
                // Note: TaskType would need to be passed in a more complete implementation
                // For now, we use a default task type
                orchestratorBridge.getEvolutionEngine().recordResult(
                    backendType,
                    BackendInterface.TaskType.REASONING,
                    result
                );
            }
        }
    }
    
    /**
     * Circuit breaker states
     */
    public enum CircuitBreakerState {
        CLOSED,    // Normal operation
        OPEN,      // Circuit broken, blocking requests
        HALF_OPEN  // Testing if backend recovered
    }
    
    /**
     * Backend performance metrics
     */
    public static class BackendPerformanceMetrics {
        private long totalRequests = 0;
        private long successfulRequests = 0;
        private long totalLatencyMs = 0;
        private int consecutiveFailures = 0;
        
        public void recordResult(BackendInterface.QueryResult result) {
            totalRequests++;
            if (result.isSuccess()) {
                successfulRequests++;
                totalLatencyMs += result.getLatencyMs();
                consecutiveFailures = 0;
            } else {
                consecutiveFailures++;
            }
        }
        
        public double getSuccessRate() {
            return totalRequests == 0 ? 0.0 : (double) successfulRequests / totalRequests;
        }
        
        public double getAverageLatency() {
            return successfulRequests == 0 ? 0.0 : (double) totalLatencyMs / successfulRequests;
        }
        
        public long getTotalRequests() {
            return totalRequests;
        }
        
        public int getConsecutiveFailures() {
            return consecutiveFailures;
        }
        
        public void reset() {
            totalRequests = 0;
            successfulRequests = 0;
            totalLatencyMs = 0;
            consecutiveFailures = 0;
        }
    }
    
    /**
     * Set the orchestrator bridge for advanced orchestration features
     * This enables optional discovery, self-healing, evolution, organic growth, and synthesis
     */
    public void setOrchestratorBridge(OrchestratorBridge orchestratorBridge) {
        this.orchestratorBridge = orchestratorBridge;
    }
    
    /**
     * Get the orchestrator bridge
     */
    public OrchestratorBridge getOrchestratorBridge() {
        return orchestratorBridge;
    }
}
