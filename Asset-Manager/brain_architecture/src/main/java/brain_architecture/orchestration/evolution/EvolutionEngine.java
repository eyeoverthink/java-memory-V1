package brain_architecture.orchestration.evolution;

import brain_architecture.core.BackendInterface;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Evolution Engine for adaptive routing optimization.
 * 
 * This component makes the orchestrator "organic" by learning from usage patterns,
 * optimizing routing decisions based on performance data, and adapting to changing
 * conditions. It implements pattern recognition, route optimization, and adaptive learning.
 */
public class EvolutionEngine {
    
    private final Map<BackendInterface.BackendType, BackendPerformanceHistory> performanceHistory;
    private final Map<BackendInterface.TaskType, List<RouteOptimization>> routeOptimizations;
    private final Map<BackendInterface.TaskType, BackendInterface.BackendType> currentBestRoutes;
    private final EvolutionConfig config;
    
    public EvolutionEngine() {
        this.performanceHistory = new ConcurrentHashMap<>();
        this.routeOptimizations = new ConcurrentHashMap<>();
        this.currentBestRoutes = new ConcurrentHashMap<>();
        this.config = new EvolutionConfig();
        
        initializeHistory();
    }
    
    /**
     * Initialize performance history for all backends
     */
    private void initializeHistory() {
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            performanceHistory.put(type, new BackendPerformanceHistory(type));
        }
    }
    
    /**
     * Record a query result for evolutionary learning
     * 
     * @param backendType The backend that handled the query
     * @param taskType The type of task
     * @param result The query result
     */
    public void recordResult(BackendInterface.BackendType backendType, 
                            BackendInterface.TaskType taskType,
                            BackendInterface.QueryResult result) {
        BackendPerformanceHistory history = performanceHistory.get(backendType);
        if (history != null) {
            history.recordResult(taskType, result);
        }
        
        // Trigger evolution check periodically
        if (history.getTotalRequests() % config.getEvolutionCheckInterval() == 0) {
            evolveRoutes(taskType);
        }
    }
    
    /**
     * Evolve routing rules based on performance data
     * 
     * @param taskType The task type to optimize routing for
     */
    public void evolveRoutes(BackendInterface.TaskType taskType) {
        List<RouteOptimization> optimizations = new ArrayList<>();
        
        // Calculate performance scores for each backend for this task type
        Map<BackendInterface.BackendType, Double> performanceScores = new HashMap<>();
        
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            BackendPerformanceHistory history = performanceHistory.get(type);
            double score = calculatePerformanceScore(history, taskType);
            performanceScores.put(type, score);
            
            optimizations.add(new RouteOptimization(
                type,
                taskType,
                score,
                history.getSuccessRate(taskType),
                history.getAverageLatency(taskType),
                LocalDateTime.now()
            ));
        }
        
        // Sort by performance score (descending)
        optimizations.sort((a, b) -> Double.compare(b.getPerformanceScore(), a.getPerformanceScore()));
        
        // Store optimizations
        routeOptimizations.put(taskType, optimizations);
        
        // Update current best route (top performer)
        if (!optimizations.isEmpty()) {
            BackendInterface.BackendType bestBackend = optimizations.get(0).getBackend();
            currentBestRoutes.put(taskType, bestBackend);
        }
    }
    
    /**
     * Calculate performance score for a backend for a specific task type
     * 
     * Score formula: (successRate * 0.6) + ((1 - normalizedLatency) * 0.3) + (stability * 0.1)
     */
    private double calculatePerformanceScore(BackendPerformanceHistory history, 
                                             BackendInterface.TaskType taskType) {
        double successRate = history.getSuccessRate(taskType);
        double avgLatency = history.getAverageLatency(taskType);
        double stability = history.getStabilityScore(taskType);
        
        // Normalize latency (lower is better, max 1000ms)
        double normalizedLatency = Math.min(avgLatency / 1000.0, 1.0);
        
        // Weighted score
        double score = (successRate * 0.6) + ((1.0 - normalizedLatency) * 0.3) + (stability * 0.1);
        
        return Math.max(0.0, Math.min(1.0, score));
    }
    
    /**
     * Get the recommended backend for a task type based on evolution
     * 
     * @param taskType The task type
     * @return Recommended backend type
     */
    public Optional<BackendInterface.BackendType> getRecommendedBackend(BackendInterface.TaskType taskType) {
        return Optional.ofNullable(currentBestRoutes.get(taskType));
    }
    
    /**
     * Get route optimizations for a task type
     * 
     * @param taskType The task type
     * @return List of route optimizations sorted by performance score
     */
    public List<RouteOptimization> getRouteOptimizations(BackendInterface.TaskType taskType) {
        return routeOptimizations.getOrDefault(taskType, Collections.emptyList());
    }
    
    /**
     * Get performance history for a backend
     * 
     * @param backendType The backend type
     * @return Performance history
     */
    public BackendPerformanceHistory getPerformanceHistory(BackendInterface.BackendType backendType) {
        return performanceHistory.get(backendType);
    }
    
    /**
     * Get all performance histories
     * 
     * @return Map of backend type to performance history
     */
    public Map<BackendInterface.BackendType, BackendPerformanceHistory> getAllPerformanceHistories() {
        return new HashMap<>(performanceHistory);
    }
    
    /**
     * Detect performance degradation for a backend
     * 
     * @param backendType The backend type
     * @param taskType The task type
     * @return true if degradation detected
     */
    public boolean detectPerformanceDegradation(BackendInterface.BackendType backendType,
                                                BackendInterface.TaskType taskType) {
        BackendPerformanceHistory history = performanceHistory.get(backendType);
        if (history == null) return false;
        
        double currentSuccessRate = history.getSuccessRate(taskType);
        double baselineSuccessRate = history.getBaselineSuccessRate(taskType);
        
        // Degradation if success rate dropped by more than threshold
        return (baselineSuccessRate - currentSuccessRate) > config.getDegradationThreshold();
    }
    
    /**
     * Get evolution statistics
     * 
     * @return Map of evolution statistics
     */
    public Map<String, Object> getEvolutionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalEvolutions", routeOptimizations.values().stream()
            .mapToInt(List::size).sum());
        stats.put("taskTypesOptimized", routeOptimizations.size());
        stats.put("backendsTracked", performanceHistory.size());
        
        // Calculate average performance improvement
        double avgImprovement = routeOptimizations.values().stream()
            .flatMap(List::stream)
            .mapToDouble(RouteOptimization::getPerformanceScore)
            .average()
            .orElse(0.0);
        stats.put("averagePerformanceScore", avgImprovement);
        
        return stats;
    }
    
    /**
     * Reset all evolution data
     */
    public void reset() {
        performanceHistory.replaceAll((k, v) -> new BackendPerformanceHistory(k));
        routeOptimizations.clear();
        currentBestRoutes.clear();
    }
    
    /**
     * Evolution configuration
     */
    public static class EvolutionConfig {
        private int evolutionCheckInterval = 10; // Check after every N requests
        private double degradationThreshold = 0.2; // 20% drop in success rate
        private int historyRetentionDays = 30; // Keep 30 days of history
        
        public int getEvolutionCheckInterval() { return evolutionCheckInterval; }
        public void setEvolutionCheckInterval(int interval) { this.evolutionCheckInterval = interval; }
        
        public double getDegradationThreshold() { return degradationThreshold; }
        public void setDegradationThreshold(double threshold) { this.degradationThreshold = threshold; }
        
        public int getHistoryRetentionDays() { return historyRetentionDays; }
        public void setHistoryRetentionDays(int days) { this.historyRetentionDays = days; }
    }
    
    /**
     * Performance history for a backend
     */
    public static class BackendPerformanceHistory {
        private final BackendInterface.BackendType backendType;
        private final Map<BackendInterface.TaskType, TaskPerformanceData> taskData;
        
        public BackendPerformanceHistory(BackendInterface.BackendType backendType) {
            this.backendType = backendType;
            this.taskData = new ConcurrentHashMap<>();
            
            // Initialize data for all task types
            for (BackendInterface.TaskType type : BackendInterface.TaskType.values()) {
                taskData.put(type, new TaskPerformanceData());
            }
        }
        
        public void recordResult(BackendInterface.TaskType taskType, BackendInterface.QueryResult result) {
            TaskPerformanceData data = taskData.get(taskType);
            if (data != null) {
                data.recordResult(result);
            }
        }
        
        public double getSuccessRate(BackendInterface.TaskType taskType) {
            TaskPerformanceData data = taskData.get(taskType);
            return data != null ? data.getSuccessRate() : 0.0;
        }
        
        public double getAverageLatency(BackendInterface.TaskType taskType) {
            TaskPerformanceData data = taskData.get(taskType);
            return data != null ? data.getAverageLatency() : 0.0;
        }
        
        public double getStabilityScore(BackendInterface.TaskType taskType) {
            TaskPerformanceData data = taskData.get(taskType);
            return data != null ? data.getStabilityScore() : 0.0;
        }
        
        public double getBaselineSuccessRate(BackendInterface.TaskType taskType) {
            TaskPerformanceData data = taskData.get(taskType);
            return data != null ? data.getBaselineSuccessRate() : 0.0;
        }
        
        public long getTotalRequests() {
            return taskData.values().stream().mapToLong(TaskPerformanceData::getTotalRequests).sum();
        }
        
        public BackendInterface.BackendType getBackendType() { return backendType; }
    }
    
    /**
     * Performance data for a specific task type
     */
    public static class TaskPerformanceData {
        private long totalRequests = 0;
        private long successfulRequests = 0;
        private long totalLatencyMs = 0;
        private final List<Long> recentLatencies = new ArrayList<>();
        private double baselineSuccessRate = 0.0;
        
        public synchronized void recordResult(BackendInterface.QueryResult result) {
            totalRequests++;
            if (result.isSuccess()) {
                successfulRequests++;
                totalLatencyMs += result.getLatencyMs();
                recentLatencies.add(result.getLatencyMs());
                
                // Keep only last 100 latencies for stability calculation
                if (recentLatencies.size() > 100) {
                    recentLatencies.remove(0);
                }
                
                // Update baseline after 10 requests
                if (totalRequests == 10) {
                    baselineSuccessRate = getSuccessRate();
                }
            }
        }
        
        public double getSuccessRate() {
            return totalRequests == 0 ? 0.0 : (double) successfulRequests / totalRequests;
        }
        
        public double getAverageLatency() {
            return successfulRequests == 0 ? 0.0 : (double) totalLatencyMs / successfulRequests;
        }
        
        public double getStabilityScore() {
            if (recentLatencies.size() < 10) return 0.5;
            
            double mean = recentLatencies.stream().mapToLong(Long::longValue).average().orElse(0.0);
            double variance = recentLatencies.stream()
                .mapToDouble(l -> Math.pow(l - mean, 2))
                .average()
                .orElse(0.0);
            double stdDev = Math.sqrt(variance);
            
            // Stability score: lower standard deviation = higher stability
            double normalizedStdDev = Math.min(stdDev / mean, 1.0);
            return 1.0 - normalizedStdDev;
        }
        
        public double getBaselineSuccessRate() {
            return baselineSuccessRate;
        }
        
        public long getTotalRequests() {
            return totalRequests;
        }
    }
    
    /**
     * Route optimization data
     */
    public static class RouteOptimization {
        private final BackendInterface.BackendType backend;
        private final BackendInterface.TaskType taskType;
        private final double performanceScore;
        private final double successRate;
        private final double averageLatency;
        private final LocalDateTime timestamp;
        
        public RouteOptimization(BackendInterface.BackendType backend,
                                BackendInterface.TaskType taskType,
                                double performanceScore,
                                double successRate,
                                double averageLatency,
                                LocalDateTime timestamp) {
            this.backend = backend;
            this.taskType = taskType;
            this.performanceScore = performanceScore;
            this.successRate = successRate;
            this.averageLatency = averageLatency;
            this.timestamp = timestamp;
        }
        
        public BackendInterface.BackendType getBackend() { return backend; }
        public BackendInterface.TaskType getTaskType() { return taskType; }
        public double getPerformanceScore() { return performanceScore; }
        public double getSuccessRate() { return successRate; }
        public double getAverageLatency() { return averageLatency; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
