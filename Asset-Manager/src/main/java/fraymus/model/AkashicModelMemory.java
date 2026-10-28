package fraymus.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AkashicModelMemory - Persistence of what worked for model selection learning.
 * 
 * This integrates with the existing AkashicRecord to store model performance metrics,
 * enabling the system to learn which models work best for specific tasks over time.
 */
public class AkashicModelMemory {
    
    private final ModelRegistry registry;
    private final Map<String, ModelPerformanceRecord> performanceRecords;
    private final Map<String, TaskPerformance> taskPerformance;
    private final Map<String, SelectionHistory> selectionHistory;
    
    public AkashicModelMemory(ModelRegistry registry) {
        this.registry = registry;
        this.performanceRecords = new ConcurrentHashMap<>();
        this.taskPerformance = new ConcurrentHashMap<>();
        this.selectionHistory = new ConcurrentHashMap<>();
    }
    
    /**
     * Performance record for a model
     */
    public static class ModelPerformanceRecord {
        private final String modelId;
        private final int totalTasks;
        private final int successfulTasks;
        private final double successRate;
        private final long totalLatencyMs;
        private final double avgLatencyMs;
        private final long lastUsed;
        private final Map<String, CapabilityScore> capabilityScores;
        
        public ModelPerformanceRecord(String modelId, int totalTasks, int successfulTasks,
                                    long totalLatencyMs, long lastUsed,
                                    Map<String, CapabilityScore> capabilityScores) {
            this.modelId = modelId;
            this.totalTasks = totalTasks;
            this.successfulTasks = successfulTasks;
            this.successRate = totalTasks > 0 ? (double) successfulTasks / totalTasks : 0.0;
            this.totalLatencyMs = totalLatencyMs;
            this.avgLatencyMs = totalTasks > 0 ? (double) totalLatencyMs / totalTasks : 0.0;
            this.lastUsed = lastUsed;
            this.capabilityScores = new HashMap<>(capabilityScores);
        }
        
        public String getModelId() { return modelId; }
        public int getTotalTasks() { return totalTasks; }
        public int getSuccessfulTasks() { return successfulTasks; }
        public double getSuccessRate() { return successRate; }
        public long getTotalLatencyMs() { return totalLatencyMs; }
        public double getAvgLatencyMs() { return avgLatencyMs; }
        public long getLastUsed() { return lastUsed; }
        public Map<String, CapabilityScore> getCapabilityScores() { return new HashMap<>(capabilityScores); }
    }
    
    /**
     * Score for a specific capability
     */
    public static class CapabilityScore {
        private final String capability;
        private final double score;
        private final int sampleCount;
        private final long lastUpdated;
        
        public CapabilityScore(String capability, double score, int sampleCount, long lastUpdated) {
            this.capability = capability;
            this.score = score;
            this.sampleCount = sampleCount;
            this.lastUpdated = lastUpdated;
        }
        
        public String getCapability() { return capability; }
        public double getScore() { return score; }
        public int getSampleCount() { return sampleCount; }
        public long getLastUpdated() { return lastUpdated; }
    }
    
    /**
     * Performance for a specific task
     */
    public static class TaskPerformance {
        private final String taskId;
        private final Map<String, TaskResult> modelResults;
        private final String bestModel;
        private final long lastUpdated;
        
        public TaskPerformance(String taskId, Map<String, TaskResult> modelResults,
                           String bestModel, long lastUpdated) {
            this.taskId = taskId;
            this.modelResults = new HashMap<>(modelResults);
            this.bestModel = bestModel;
            this.lastUpdated = lastUpdated;
        }
        
        public String getTaskId() { return taskId; }
        public Map<String, TaskResult> getModelResults() { return new HashMap<>(modelResults); }
        public String getBestModel() { return bestModel; }
        public long getLastUpdated() { return lastUpdated; }
    }
    
    /**
     * Result for a task
     */
    public static class TaskResult {
        private final String modelId;
        private final boolean success;
        private final long latencyMs;
        private final String error;
        private final long timestamp;
        
        public TaskResult(String modelId, boolean success, long latencyMs,
                        String error, long timestamp) {
            this.modelId = modelId;
            this.success = success;
            this.latencyMs = latencyMs;
            this.error = error;
            this.timestamp = timestamp;
        }
        
        public String getModelId() { return modelId; }
        public boolean isSuccess() { return success; }
        public long getLatencyMs() { return latencyMs; }
        public String getError() { return error; }
        public long getTimestamp() { return timestamp; }
    }
    
    /**
     * Selection history for an organ
     */
    public static class SelectionHistory {
        private final String organ;
        private final List<SelectionEvent> events;
        private final Map<String, Integer> selectionCounts;
        
        public SelectionHistory(String organ, List<SelectionEvent> events,
                             Map<String, Integer> selectionCounts) {
            this.organ = organ;
            this.events = new ArrayList<>(events);
            this.selectionCounts = new HashMap<>(selectionCounts);
        }
        
        public String getOrgan() { return organ; }
        public List<SelectionEvent> getEvents() { return new ArrayList<>(events); }
        public Map<String, Integer> getSelectionCounts() { return new HashMap<>(selectionCounts); }
    }
    
    /**
     * Single selection event
     */
    public static class SelectionEvent {
        private final String modelId;
        private final String reasoning;
        private final long timestamp;
        
        public SelectionEvent(String modelId, String reasoning, long timestamp) {
            this.modelId = modelId;
            this.reasoning = reasoning;
            this.timestamp = timestamp;
        }
        
        public String getModelId() { return modelId; }
        public String getReasoning() { return reasoning; }
        public long getTimestamp() { return timestamp; }
    }
    
    /**
     * Record model usage and performance
     */
    public void recordUsage(String modelId, String taskId, boolean success, long latencyMs) {
        long now = System.currentTimeMillis();
        
        // Update performance record
        ModelPerformanceRecord record = performanceRecords.get(modelId);
        if (record == null) {
            record = new ModelPerformanceRecord(modelId, 0, 0, 0, now, new HashMap<>());
        }
        
        int newTotalTasks = record.getTotalTasks() + 1;
        int newSuccessfulTasks = record.getSuccessfulTasks() + (success ? 1 : 0);
        long newTotalLatencyMs = record.getTotalLatencyMs() + latencyMs;
        
        performanceRecords.put(modelId, new ModelPerformanceRecord(
            modelId, newTotalTasks, newSuccessfulTasks, newTotalLatencyMs, now,
            record.getCapabilityScores()
        ));
        
        // Update task performance
        TaskPerformance taskPerf = taskPerformance.get(taskId);
        Map<String, TaskResult> modelResults;
        
        if (taskPerf == null) {
            modelResults = new HashMap<>();
        } else {
            modelResults = new HashMap<>(taskPerf.getModelResults());
        }
        
        modelResults.put(modelId, new TaskResult(modelId, success, latencyMs, null, now));
        
        String bestModel = findBestModelForTask(modelResults);
        taskPerformance.put(taskId, new TaskPerformance(taskId, modelResults, bestModel, now));
    }
    
    /**
     * Record capability score for a model
     */
    public void recordCapabilityScore(String modelId, String capability, double score) {
        long now = System.currentTimeMillis();
        
        ModelPerformanceRecord record = performanceRecords.get(modelId);
        Map<String, CapabilityScore> capabilityScores;
        
        if (record == null) {
            capabilityScores = new HashMap<>();
        } else {
            capabilityScores = new HashMap<>(record.getCapabilityScores());
        }
        
        CapabilityScore existing = capabilityScores.get(capability);
        int newSampleCount = (existing != null) ? existing.getSampleCount() + 1 : 1;
        double newScore = (existing != null) 
            ? (existing.getScore() * existing.getSampleCount() + score) / newSampleCount
            : score;
        
        capabilityScores.put(capability, new CapabilityScore(capability, newScore, newSampleCount, now));
        
        performanceRecords.put(modelId, new ModelPerformanceRecord(
            modelId, record != null ? record.getTotalTasks() : 0,
            record != null ? record.getSuccessfulTasks() : 0,
            record != null ? record.getTotalLatencyMs() : 0, now, capabilityScores
        ));
    }
    
    /**
     * Record selection for an organ
     */
    public void recordSelection(CapabilityProfile.Organ organ, String modelId, String reasoning) {
        String organKey = organ.name();
        
        SelectionHistory history = selectionHistory.get(organKey);
        List<SelectionEvent> events;
        Map<String, Integer> selectionCounts;
        
        if (history == null) {
            events = new ArrayList<>();
            selectionCounts = new HashMap<>();
        } else {
            events = new ArrayList<>(history.getEvents());
            selectionCounts = new HashMap<>(history.getSelectionCounts());
        }
        
        events.add(new SelectionEvent(modelId, reasoning, System.currentTimeMillis()));
        selectionCounts.merge(modelId, 1, Integer::sum);
        
        selectionHistory.put(organKey, new SelectionHistory(organKey, events, selectionCounts));
    }
    
    /**
     * Get performance record for a model
     */
    public ModelPerformanceRecord getPerformanceRecord(String modelId) {
        return performanceRecords.get(modelId);
    }
    
    /**
     * Get task performance
     */
    public TaskPerformance getTaskPerformance(String taskId) {
        return taskPerformance.get(taskId);
    }
    
    /**
     * Get selection history for an organ
     */
    public SelectionHistory getSelectionHistory(CapabilityProfile.Organ organ) {
        return selectionHistory.get(organ.name());
    }
    
    /**
     * Get all performance records
     */
    public Map<String, ModelPerformanceRecord> getAllPerformanceRecords() {
        return new HashMap<>(performanceRecords);
    }
    
    /**
     * Get best model for a task
     */
    public String getBestModelForTask(String taskId) {
        TaskPerformance taskPerf = taskPerformance.get(taskId);
        return taskPerf != null ? taskPerf.getBestModel() : null;
    }
    
    /**
     * Get best model for an organ
     */
    public String getBestModelForOrgan(CapabilityProfile.Organ organ) {
        SelectionHistory history = selectionHistory.get(organ.name());
        if (history == null) {
            return null;
        }
        
        return history.getSelectionCounts().entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * Get recommended model based on performance history
     */
    public String getRecommendedModel(CapabilityProfile.Capability capability) {
        Map<String, Double> scores = new HashMap<>();
        
        for (ModelPerformanceRecord record : performanceRecords.values()) {
            CapabilityScore capScore = record.getCapabilityScores().get(capability.name());
            if (capScore != null && capScore.getSampleCount() >= 3) {
                scores.put(record.getModelId(), capScore.getScore());
            }
        }
        
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * Find best model for task based on results
     */
    private String findBestModelForTask(Map<String, TaskResult> modelResults) {
        return modelResults.entrySet().stream()
            .filter(e -> e.getValue().isSuccess())
            .min(Comparator.comparingLong(e -> e.getValue().getLatencyMs()))
            .map(Map.Entry::getKey)
            .orElse(modelResults.keySet().stream().findFirst().orElse(null));
    }
    
    /**
     * Clear all records
     */
    public void clearAll() {
        performanceRecords.clear();
        taskPerformance.clear();
        selectionHistory.clear();
    }
    
    /**
     * Clear records for a specific model
     */
    public void clearModel(String modelId) {
        performanceRecords.remove(modelId);
    }
    
    /**
     * Get statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalModels", performanceRecords.size());
        stats.put("totalTasks", taskPerformance.size());
        stats.put("totalSelections", selectionHistory.size());
        
        int totalUsage = performanceRecords.values().stream()
            .mapToInt(ModelPerformanceRecord::getTotalTasks)
            .sum();
        
        int totalSuccess = performanceRecords.values().stream()
            .mapToInt(ModelPerformanceRecord::getSuccessfulTasks)
            .sum();
        
        stats.put("totalUsage", totalUsage);
        stats.put("totalSuccess", totalSuccess);
        stats.put("overallSuccessRate", totalUsage > 0 ? (double) totalSuccess / totalUsage : 0.0);
        
        return stats;
    }
}
