package brain_architecture.orchestration.healing;

import brain_architecture.core.BackendInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Self-Healing mechanism for automatic failure recovery and circuit breaking.
 * 
 * This component implements circuit breaker patterns, health-based routing,
 * automatic failure recovery, and degraded mode operation to ensure system
 * resilience even when backends fail.
 */
public class SelfHealing {
    
    private final Map<BackendInterface.BackendType, CircuitBreakerState> circuitBreakers;
    private final Map<BackendInterface.BackendType, HealthMetrics> healthMetrics;
    private final SelfHealingConfig config;
    private boolean degradedMode = false;
    
    public SelfHealing() {
        this.circuitBreakers = new ConcurrentHashMap<>();
        this.healthMetrics = new ConcurrentHashMap<>();
        this.config = new SelfHealingConfig();
        
        initializeCircuitBreakers();
    }
    
    /**
     * Initialize circuit breakers for all backend types
     */
    private void initializeCircuitBreakers() {
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            circuitBreakers.put(type, new CircuitBreakerState(type));
            healthMetrics.put(type, new HealthMetrics(type));
        }
    }
    
    /**
     * Record a backend request result for health tracking
     * 
     * @param backendType The backend type
     * @param success Whether the request was successful
     * @param latencyMs The request latency in milliseconds
     */
    public void recordRequest(BackendInterface.BackendType backendType, boolean success, long latencyMs) {
        CircuitBreakerState state = circuitBreakers.get(backendType);
        HealthMetrics metrics = healthMetrics.get(backendType);
        
        if (state != null) {
            state.recordRequest(success);
        }
        
        if (metrics != null) {
            metrics.recordRequest(success, latencyMs);
        }
        
        // Check if we should enter degraded mode
        checkDegradedMode();
    }
    
    /**
     * Check if a backend is allowed to handle requests (circuit breaker check)
     * 
     * @param backendType The backend type
     * @return true if requests are allowed
     */
    public boolean isRequestAllowed(BackendInterface.BackendType backendType) {
        CircuitBreakerState state = circuitBreakers.get(backendType);
        if (state == null) return true;
        
        return state.isRequestAllowed();
    }
    
    /**
     * Get the current state of a circuit breaker
     * 
     * @param backendType The backend type
     * @return CircuitBreakerState
     */
    public CircuitBreakerState getCircuitBreakerState(BackendInterface.BackendType backendType) {
        return circuitBreakers.get(backendType);
    }
    
    /**
     * Manually open a circuit breaker for a backend
     * 
     * @param backendType The backend type
     */
    public void openCircuit(BackendInterface.BackendType backendType) {
        CircuitBreakerState state = circuitBreakers.get(backendType);
        if (state != null) {
            state.open();
        }
    }
    
    /**
     * Manually close a circuit breaker for a backend
     * 
     * @param backendType The backend type
     */
    public void closeCircuit(BackendInterface.BackendType backendType) {
        CircuitBreakerState state = circuitBreakers.get(backendType);
        if (state != null) {
            state.close();
        }
    }
    
    /**
     * Manually put a circuit breaker in half-open state for testing
     * 
     * @param backendType The backend type
     */
    public void halfOpenCircuit(BackendInterface.BackendType backendType) {
        CircuitBreakerState state = circuitBreakers.get(backendType);
        if (state != null) {
            state.halfOpen();
        }
    }
    
    /**
     * Get health metrics for a backend
     * 
     * @param backendType The backend type
     * @return HealthMetrics
     */
    public HealthMetrics getHealthMetrics(BackendInterface.BackendType backendType) {
        return healthMetrics.get(backendType);
    }
    
    /**
     * Get all health metrics
     * 
     * @return Map of backend type to health metrics
     */
    public Map<BackendInterface.BackendType, HealthMetrics> getAllHealthMetrics() {
        return new HashMap<>(healthMetrics);
    }
    
    /**
     * Get the health score for a backend (0.0 to 1.0)
     * 
     * @param backendType The backend type
     * @return Health score
     */
    public double getHealthScore(BackendInterface.BackendType backendType) {
        HealthMetrics metrics = healthMetrics.get(backendType);
        if (metrics == null) return 0.5;
        
        return metrics.calculateHealthScore();
    }
    
    /**
     * Check if the system should enter degraded mode
     */
    private void checkDegradedMode() {
        int unhealthyCount = 0;
        int totalCount = circuitBreakers.size();
        
        for (CircuitBreakerState state : circuitBreakers.values()) {
            if (state.getState() == CircuitState.OPEN) {
                unhealthyCount++;
            }
        }
        
        double unhealthyRatio = (double) unhealthyCount / totalCount;
        
        // Enter degraded mode if too many backends are unhealthy
        if (unhealthyRatio >= config.getDegradedModeThreshold()) {
            degradedMode = true;
        } else if (unhealthyRatio < config.getDegradedModeRecoveryThreshold()) {
            degradedMode = false;
        }
    }
    
    /**
     * Check if the system is in degraded mode
     * 
     * @return true if in degraded mode
     */
    public boolean isDegradedMode() {
        return degradedMode;
    }
    
    /**
     * Enable or disable degraded mode manually
     * 
     * @param degraded Whether to enable degraded mode
     */
    public void setDegradedMode(boolean degraded) {
        this.degradedMode = degraded;
    }
    
    /**
     * Get recommended backends based on health
     * 
     * @return List of healthy backends sorted by health score
     */
    public List<BackendInterface.BackendType> getHealthyBackends() {
        return healthMetrics.entrySet().stream()
            .filter(e -> {
                CircuitBreakerState state = circuitBreakers.get(e.getKey());
                return state == null || state.getState() != CircuitState.OPEN;
            })
            .sorted((e1, e2) -> Double.compare(
                e2.getValue().calculateHealthScore(),
                e1.getValue().calculateHealthScore()
            ))
            .map(Map.Entry::getKey)
            .toList();
    }
    
    /**
     * Get self-healing statistics
     * 
     * @return Map of statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long openCircuits = circuitBreakers.values().stream()
            .filter(s -> s.getState() == CircuitState.OPEN)
            .count();
        
        long closedCircuits = circuitBreakers.values().stream()
            .filter(s -> s.getState() == CircuitState.CLOSED)
            .count();
        
        long halfOpenCircuits = circuitBreakers.values().stream()
            .filter(s -> s.getState() == CircuitState.HALF_OPEN)
            .count();
        
        double avgHealthScore = healthMetrics.values().stream()
            .mapToDouble(HealthMetrics::calculateHealthScore)
            .average()
            .orElse(0.5);
        
        stats.put("totalCircuits", circuitBreakers.size());
        stats.put("openCircuits", openCircuits);
        stats.put("closedCircuits", closedCircuits);
        stats.put("halfOpenCircuits", halfOpenCircuits);
        stats.put("degradedMode", degradedMode);
        stats.put("averageHealthScore", avgHealthScore);
        
        return stats;
    }
    
    /**
     * Reset all circuit breakers and health metrics
     */
    public void reset() {
        circuitBreakers.replaceAll((k, v) -> new CircuitBreakerState(k));
        healthMetrics.replaceAll((k, v) -> new HealthMetrics(k));
        degradedMode = false;
    }
    
    /**
     * Circuit breaker state
     */
    public static class CircuitBreakerState {
        private final BackendInterface.BackendType backendType;
        private CircuitState state = CircuitState.CLOSED;
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final AtomicLong lastFailureTime = new AtomicLong(0);
        private final AtomicLong lastStateChange = new AtomicLong(System.currentTimeMillis());
        
        public CircuitBreakerState(BackendInterface.BackendType backendType) {
            this.backendType = backendType;
        }
        
        public void recordRequest(boolean success) {
            if (success) {
                successCount.incrementAndGet();
                failureCount.set(0); // Reset failure count on success
                
                // If in half-open and successful, close the circuit
                if (state == CircuitState.HALF_OPEN && successCount.get() >= 3) {
                    close();
                }
            } else {
                failureCount.incrementAndGet();
                lastFailureTime.set(System.currentTimeMillis());
                
                // Check if we should open the circuit
                if (failureCount.get() >= 5) { // Open after 5 consecutive failures
                    open();
                }
            }
        }
        
        public boolean isRequestAllowed() {
            if (state == CircuitState.OPEN) {
                // Check if we should attempt half-open
                long timeSinceOpen = System.currentTimeMillis() - lastStateChange.get();
                if (timeSinceOpen > 60000) { // 60 seconds cooldown
                    halfOpen();
                    return true;
                }
                return false;
            }
            return true;
        }
        
        public void open() {
            state = CircuitState.OPEN;
            lastStateChange.set(System.currentTimeMillis());
            successCount.set(0);
        }
        
        public void close() {
            state = CircuitState.CLOSED;
            lastStateChange.set(System.currentTimeMillis());
            failureCount.set(0);
        }
        
        public void halfOpen() {
            state = CircuitState.HALF_OPEN;
            lastStateChange.set(System.currentTimeMillis());
            successCount.set(0);
        }
        
        public BackendInterface.BackendType getBackendType() { return backendType; }
        public CircuitState getState() { return state; }
        public int getFailureCount() { return failureCount.get(); }
        public int getSuccessCount() { return successCount.get(); }
        public long getLastFailureTime() { return lastFailureTime.get(); }
    }
    
    /**
     * Circuit state
     */
    public enum CircuitState {
        CLOSED,      // Normal operation, requests allowed
        OPEN,        // Circuit tripped, requests blocked
        HALF_OPEN    // Testing if backend recovered, limited requests allowed
    }
    
    /**
     * Health metrics for a backend
     */
    public static class HealthMetrics {
        private final BackendInterface.BackendType backendType;
        private final AtomicLong totalRequests = new AtomicLong(0);
        private final AtomicLong successfulRequests = new AtomicLong(0);
        private final AtomicLong totalLatencyMs = new AtomicLong(0);
        private final AtomicLong minLatencyMs = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxLatencyMs = new AtomicLong(0);
        private final List<Long> recentLatencies = new ArrayList<>();
        
        public HealthMetrics(BackendInterface.BackendType backendType) {
            this.backendType = backendType;
        }
        
        public void recordRequest(boolean success, long latencyMs) {
            totalRequests.incrementAndGet();
            
            if (success) {
                successfulRequests.incrementAndGet();
                totalLatencyMs.addAndGet(latencyMs);
                
                long currentMin = minLatencyMs.get();
                if (latencyMs < currentMin) {
                    minLatencyMs.set(latencyMs);
                }
                
                long currentMax = maxLatencyMs.get();
                if (latencyMs > currentMax) {
                    maxLatencyMs.set(latencyMs);
                }
                
                synchronized (recentLatencies) {
                    recentLatencies.add(latencyMs);
                    if (recentLatencies.size() > 100) {
                        recentLatencies.remove(0);
                    }
                }
            }
        }
        
        public double calculateHealthScore() {
            long total = totalRequests.get();
            if (total == 0) return 0.5;
            
            double successRate = (double) successfulRequests.get() / total;
            double avgLatency = total > 0 ? (double) totalLatencyMs.get() / successfulRequests.get() : 0;
            
            // Normalize latency (lower is better, max 1000ms)
            double latencyScore = Math.max(0.0, 1.0 - (avgLatency / 1000.0));
            
            // Combined score: 70% success rate, 30% latency
            return (successRate * 0.7) + (latencyScore * 0.3);
        }
        
        public BackendInterface.BackendType getBackendType() { return backendType; }
        public long getTotalRequests() { return totalRequests.get(); }
        public long getSuccessfulRequests() { return successfulRequests.get(); }
        public double getSuccessRate() { 
            long total = totalRequests.get();
            return total == 0 ? 0.0 : (double) successfulRequests.get() / total;
        }
        public double getAverageLatency() {
            long successful = successfulRequests.get();
            return successful == 0 ? 0.0 : (double) totalLatencyMs.get() / successful;
        }
    }
    
    /**
     * Self-healing configuration
     */
    public static class SelfHealingConfig {
        private int failureThreshold = 5; // Open circuit after N failures
        private long cooldownMs = 60000; // 60 seconds cooldown before half-open
        private double degradedModeThreshold = 0.5; // Enter degraded mode if 50% backends unhealthy
        private double degradedModeRecoveryThreshold = 0.3; // Exit degraded mode at 30%
        
        public int getFailureThreshold() { return failureThreshold; }
        public void setFailureThreshold(int threshold) { this.failureThreshold = threshold; }
        
        public long getCooldownMs() { return cooldownMs; }
        public void setCooldownMs(long cooldown) { this.cooldownMs = cooldown; }
        
        public double getDegradedModeThreshold() { return degradedModeThreshold; }
        public void setDegradedModeThreshold(double threshold) { this.degradedModeThreshold = threshold; }
        
        public double getDegradedModeRecoveryThreshold() { return degradedModeRecoveryThreshold; }
        public void setDegradedModeRecoveryThreshold(double threshold) { 
            this.degradedModeRecoveryThreshold = threshold; 
        }
    }
}
