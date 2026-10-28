package fraymus.brain.unified;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Enteric Nervous System - The "Gut" Instinct
 * 
 * Purpose: Anomaly detection on raw system metrics (RAM, CPU, network)
 * Model: XGBoost or Autoencoder (Python/Scikit-learn)
 * Latency: <1ms | Purpose: Trigger Amygdala on anomalies
 * 
 * The gut doesn't think in words; it thinks in raw data spikes.
 * This system monitors RAM, CPU, and network traffic in real-time,
 * and triggers the Amygdala to wake up the rest of the system
 * when anomalies are detected.
 * 
 * NOTE: This is a Java implementation. For full XGBoost integration,
 * you would need to call Python scripts via ProcessBuilder or use
 * a Java XGBoost library like xgboost4j.
 */
public class EntericNervousSystem {
    private final AtomicBoolean running;
    private final AtomicBoolean anomalyDetected;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;
    
    // Anomaly detection thresholds
    private double ramThreshold = 0.85;      // 85% RAM usage
    private double cpuThreshold = 0.80;      // 80% CPU usage
    private double networkThreshold = 1000;  // 1000 KB/s network spike
    
    // Historical data for trend analysis
    private final Queue<Double> ramHistory;
    private final Queue<Double> cpuHistory;
    private final Queue<Double> networkHistory;
    private static final int HISTORY_SIZE = 100;
    
    // Statistics
    private int totalChecks = 0;
    private int anomalyCount = 0;
    private long lastAnomalyTime = 0;
    
    // Callback for anomaly detection
    private AnomalyCallback callback;
    
    public EntericNervousSystem() {
        this.running = new AtomicBoolean(false);
        this.anomalyDetected = new AtomicBoolean(false);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "Fraynix-Enteric-Monitor");
            thread.setDaemon(true);
            return thread;
        });
        this.ramHistory = new LinkedList<>();
        this.cpuHistory = new LinkedList<>();
        this.networkHistory = new LinkedList<>();
    }
    
    /**
     * Start monitoring system metrics
     * @param intervalMs Check interval in milliseconds (default: 1000ms)
     */
    public void startMonitoring(long intervalMs) {
        if (running.compareAndSet(false, true)) {
            scheduledTask = scheduler.scheduleAtFixedRate(this::checkMetrics, 0, intervalMs, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * Stop monitoring
     */
    public void stopMonitoring() {
        if (running.compareAndSet(true, false)) {
            if (scheduledTask != null) {
                scheduledTask.cancel(false);
            }
        }
    }

    /** Permanently release the monitor when its owning orchestrator shuts down. */
    public void shutdown() {
        stopMonitoring();
        scheduler.shutdownNow();
    }
    
    /**
     * Set callback for anomaly detection
     */
    public void setCallback(AnomalyCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Check system metrics for anomalies
     */
    private void checkMetrics() {
        totalChecks++;
        
        try {
            // Get current system metrics
            SystemMetrics metrics = SystemMetrics.getCurrent();
            
            // Update history
            updateHistory(metrics);
            
            // Detect anomalies
            AnomalyResult result = detectAnomalies(metrics);
            
            if (result.isAnomaly) {
                anomalyCount++;
                lastAnomalyTime = System.currentTimeMillis();
                anomalyDetected.set(true);
                
                // Trigger callback if set
                if (callback != null) {
                    callback.onAnomaly(result);
                }
            } else {
                anomalyDetected.set(false);
            }
            
        } catch (Exception e) {
            // Log error but don't stop monitoring
            System.err.println("Enteric Nervous System error: " + e.getMessage());
        }
    }
    
    /**
     * Update historical data
     */
    private void updateHistory(SystemMetrics metrics) {
        ramHistory.add(metrics.getRamUsage());
        cpuHistory.add(metrics.getCpuUsage());
        networkHistory.add(metrics.getNetworkTraffic());
        
        // Keep history bounded
        if (ramHistory.size() > HISTORY_SIZE) ramHistory.poll();
        if (cpuHistory.size() > HISTORY_SIZE) cpuHistory.poll();
        if (networkHistory.size() > HISTORY_SIZE) networkHistory.poll();
    }
    
    /**
     * Detect anomalies in system metrics
     */
    private AnomalyResult detectAnomalies(SystemMetrics metrics) {
        AnomalyResult result = new AnomalyResult();
        result.timestamp = System.currentTimeMillis();
        result.ramUsage = metrics.getRamUsage();
        result.cpuUsage = metrics.getCpuUsage();
        result.networkTraffic = metrics.getNetworkTraffic();
        
        List<String> reasons = new ArrayList<>();
        double anomalyScore = 0.0;
        
        // Check RAM usage
        if (metrics.getRamUsage() > ramThreshold) {
            reasons.add(String.format("RAM usage %.1f%% exceeds threshold %.1f%%", 
                metrics.getRamUsage() * 100, ramThreshold * 100));
            anomalyScore += 0.4;
        }
        
        // Check CPU usage
        if (metrics.getCpuUsage() > cpuThreshold) {
            reasons.add(String.format("CPU usage %.1f%% exceeds threshold %.1f%%", 
                metrics.getCpuUsage() * 100, cpuThreshold * 100));
            anomalyScore += 0.4;
        }
        
        // Check network spike
        if (metrics.getNetworkTraffic() > networkThreshold) {
            reasons.add(String.format("Network traffic %.1f KB/s exceeds threshold %.1f KB/s", 
                metrics.getNetworkTraffic(), networkThreshold));
            anomalyScore += 0.2;
        }
        
        // Check for sudden spikes (statistical anomaly)
        if (ramHistory.size() >= 10) {
            double ramMean = calculateMean(ramHistory);
            double ramStd = calculateStdDev(ramHistory, ramMean);
            if (metrics.getRamUsage() > ramMean + 3 * ramStd) {
                reasons.add("RAM usage sudden spike (3σ deviation)");
                anomalyScore += 0.3;
            }
        }
        
        if (cpuHistory.size() >= 10) {
            double cpuMean = calculateMean(cpuHistory);
            double cpuStd = calculateStdDev(cpuHistory, cpuMean);
            if (metrics.getCpuUsage() > cpuMean + 3 * cpuStd) {
                reasons.add("CPU usage sudden spike (3σ deviation)");
                anomalyScore += 0.3;
            }
        }
        
        result.isAnomaly = anomalyScore > 0.5;
        result.anomalyScore = Math.min(anomalyScore, 1.0);
        result.reasons = reasons;
        
        return result;
    }
    
    /**
     * Calculate mean of values
     */
    private double calculateMean(Queue<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    /**
     * Calculate standard deviation
     */
    private double calculateStdDev(Queue<Double> values, double mean) {
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0.0);
        return Math.sqrt(variance);
    }
    
    /**
     * Get monitoring statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("running", running.get());
        stats.put("anomalyDetected", anomalyDetected.get());
        stats.put("totalChecks", totalChecks);
        stats.put("anomalyCount", anomalyCount);
        stats.put("anomalyRate", totalChecks > 0 ? (double) anomalyCount / totalChecks : 0.0);
        stats.put("lastAnomalyTime", lastAnomalyTime);
        stats.put("ramThreshold", ramThreshold);
        stats.put("cpuThreshold", cpuThreshold);
        stats.put("networkThreshold", networkThreshold);
        stats.put("currentRam", ramHistory.isEmpty() ? 0.0 : ramHistory.peek());
        stats.put("currentCpu", cpuHistory.isEmpty() ? 0.0 : cpuHistory.peek());
        stats.put("currentNetwork", networkHistory.isEmpty() ? 0.0 : networkHistory.peek());
        return stats;
    }
    
    /**
     * Set anomaly thresholds
     */
    public void setThresholds(double ramThreshold, double cpuThreshold, double networkThreshold) {
        this.ramThreshold = ramThreshold;
        this.cpuThreshold = cpuThreshold;
        this.networkThreshold = networkThreshold;
    }
    
    /**
     * Reset statistics
     */
    public void resetStats() {
        totalChecks = 0;
        anomalyCount = 0;
        lastAnomalyTime = 0;
        ramHistory.clear();
        cpuHistory.clear();
        networkHistory.clear();
    }
    
    /**
     * Check if currently in anomaly state
     */
    public boolean isAnomalyDetected() {
        return anomalyDetected.get();
    }
    
    /**
     * Callback interface for anomaly detection
     */
    public interface AnomalyCallback {
        void onAnomaly(AnomalyResult result);
    }
    
    /**
     * Anomaly detection result
     */
    public static class AnomalyResult {
        public long timestamp;
        public double ramUsage;
        public double cpuUsage;
        public double networkTraffic;
        public boolean isAnomaly;
        public double anomalyScore;
        public List<String> reasons;
        
        public AnomalyResult() {
            this.reasons = new ArrayList<>();
        }
        
        @Override
        public String toString() {
            return String.format(
                "AnomalyResult{timestamp=%d, ram=%.2f, cpu=%.2f, network=%.2f, isAnomaly=%b, score=%.2f, reasons=%s}",
                timestamp, ramUsage, cpuUsage, networkTraffic, isAnomaly, anomalyScore, reasons
            );
        }
    }
    
    /**
     * System metrics snapshot
     */
    public static class SystemMetrics {
        private final double ramUsage;
        private final double cpuUsage;
        private final double networkTraffic;
        
        public SystemMetrics(double ramUsage, double cpuUsage, double networkTraffic) {
            this.ramUsage = ramUsage;
            this.cpuUsage = cpuUsage;
            this.networkTraffic = networkTraffic;
        }
        
        public double getRamUsage() {
            return ramUsage;
        }
        
        public double getCpuUsage() {
            return cpuUsage;
        }
        
        public double getNetworkTraffic() {
            return networkTraffic;
        }
        
        /**
         * Get current system metrics
         */
        public static SystemMetrics getCurrent() {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            double ramUsage = maxMemory > 0 ? (double) usedMemory / maxMemory : 0.0;
            
            // CPU usage is harder to get accurately in Java without native libraries
            // Using a simple approximation based on thread count
            double cpuUsage = Math.min(1.0, (double) runtime.availableProcessors() / 64.0);
            
            // Network traffic would require native libraries or OS-specific calls
            // Using a placeholder value
            double networkTraffic = 0.0;
            
            return new SystemMetrics(ramUsage, cpuUsage, networkTraffic);
        }
    }
}
