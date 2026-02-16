package fraymus;

import java.util.*;

/**
 * SELF-4: MRL ANALYTICS & QUADRANT METRICS
 * 
 * Memory Resonance Level tracking with:
 * - Neural analytics
 * - Phase consistency tracking
 * - BARDO state monitoring
 * - Age to DSR (Data State Ratio) patterns
 * - Latency metrics
 */
public class MRLAnalytics {

    private static final double PHI = 1.618033988749895;

    // MRL quadrants (2x2 grid based on resonance and activity)
    public enum Quadrant {
        HIGH_ACTIVE("High Resonance, High Activity", 1.0),
        HIGH_PASSIVE("High Resonance, Low Activity", 0.8),
        LOW_ACTIVE("Low Resonance, High Activity", 0.5),
        LOW_PASSIVE("Low Resonance, Low Activity", 0.3);

        public final String description;
        public final double weight;

        Quadrant(String desc, double weight) {
            this.description = desc;
            this.weight = weight;
        }
    }

    // Time-series metrics
    private final List<MetricSnapshot> snapshots = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Double> currentMetrics = new LinkedHashMap<>();
    
    // Phase tracking
    private double phaseConsistency = 1.0;
    private double[] phaseHistory = new double[100];
    private int phaseIndex = 0;
    
    // Latency tracking
    private final List<Long> latencyHistory = new ArrayList<>();
    private double avgLatency = 0;
    
    // DSR (Data State Ratio) tracking
    private int dataWrites = 0;
    private int dataReads = 0;
    private double dsrRatio = 1.0;

    // Current state
    private Quadrant currentQuadrant = Quadrant.LOW_PASSIVE;
    private double mrlScore = 1.0;

    public MRLAnalytics() {
        initializeMetrics();
    }

    private void initializeMetrics() {
        currentMetrics.put("mrl_score", 1.0);
        currentMetrics.put("phase_consistency", 1.0);
        currentMetrics.put("activity_level", 0.0);
        currentMetrics.put("resonance_avg", 0.5);
        currentMetrics.put("dsr_ratio", 1.0);
        currentMetrics.put("latency_ms", 0.0);
        currentMetrics.put("bardo_balance", 0.5);
    }

    /**
     * Record a metric value
     */
    public void recordMetric(String name, double value) {
        currentMetrics.put(name, value);
        
        // Update derived metrics
        updateDerivedMetrics();
    }

    /**
     * Record neural activity
     */
    public void recordNeuralActivity(double resonance, double phase, long latencyMs) {
        // Update phase tracking
        phaseHistory[phaseIndex] = phase;
        phaseIndex = (phaseIndex + 1) % phaseHistory.length;
        updatePhaseConsistency();
        
        // Update latency
        latencyHistory.add(latencyMs);
        if (latencyHistory.size() > 100) latencyHistory.remove(0);
        avgLatency = latencyHistory.stream().mapToLong(l -> l).average().orElse(0);
        
        // Update MRL score
        double activityBoost = 1.0 / (1.0 + avgLatency / 100.0); // Lower latency = higher activity
        mrlScore = mrlScore * 0.9 + (resonance * phaseConsistency * activityBoost) * 0.1;
        
        // Update current metrics
        currentMetrics.put("mrl_score", mrlScore);
        currentMetrics.put("phase_consistency", phaseConsistency);
        currentMetrics.put("latency_ms", avgLatency);
        currentMetrics.put("resonance_avg", resonance);
        
        // Determine quadrant
        updateQuadrant(resonance, activityBoost);
    }

    /**
     * Record data access for DSR tracking
     */
    public void recordDataAccess(boolean isWrite) {
        if (isWrite) {
            dataWrites++;
        } else {
            dataReads++;
        }
        
        // Update DSR ratio (writes to reads)
        int total = dataWrites + dataReads;
        if (total > 0) {
            dsrRatio = (double) dataWrites / total;
        }
        currentMetrics.put("dsr_ratio", dsrRatio);
    }

    /**
     * Update phase consistency metric
     */
    private void updatePhaseConsistency() {
        // Calculate variance of phase values
        double sum = 0, sumSq = 0;
        int count = 0;
        
        for (double p : phaseHistory) {
            if (p != 0) {
                sum += p;
                sumSq += p * p;
                count++;
            }
        }
        
        if (count < 2) {
            phaseConsistency = 1.0;
            return;
        }
        
        double mean = sum / count;
        double variance = (sumSq / count) - (mean * mean);
        
        // Low variance = high consistency
        phaseConsistency = 1.0 / (1.0 + Math.sqrt(variance));
    }

    /**
     * Update current quadrant based on metrics
     */
    private void updateQuadrant(double resonance, double activity) {
        boolean highRes = resonance > 0.6;
        boolean highAct = activity > 0.5;
        
        if (highRes && highAct) {
            currentQuadrant = Quadrant.HIGH_ACTIVE;
        } else if (highRes && !highAct) {
            currentQuadrant = Quadrant.HIGH_PASSIVE;
        } else if (!highRes && highAct) {
            currentQuadrant = Quadrant.LOW_ACTIVE;
        } else {
            currentQuadrant = Quadrant.LOW_PASSIVE;
        }
    }

    /**
     * Update derived metrics
     */
    private void updateDerivedMetrics() {
        // Calculate activity level from latency and DSR
        double activity = (1.0 - dsrRatio * 0.5) * (1.0 / (1.0 + avgLatency / 50.0));
        currentMetrics.put("activity_level", activity);
    }

    /**
     * Take a snapshot of current metrics
     */
    public void takeSnapshot() {
        MetricSnapshot snapshot = new MetricSnapshot(new LinkedHashMap<>(currentMetrics), currentQuadrant);
        snapshots.add(snapshot);
        
        // Keep bounded
        while (snapshots.size() > 1000) {
            snapshots.remove(0);
        }
    }

    /**
     * Get metric trend over time
     */
    public double[] getMetricTrend(String metricName, int count) {
        int actualCount = Math.min(count, snapshots.size());
        double[] trend = new double[actualCount];
        
        synchronized (snapshots) {
            int start = snapshots.size() - actualCount;
            for (int i = 0; i < actualCount; i++) {
                Double val = snapshots.get(start + i).metrics.get(metricName);
                trend[i] = val != null ? val : 0;
            }
        }
        
        return trend;
    }

    /**
     * Get quadrant distribution over time
     */
    public Map<Quadrant, Double> getQuadrantDistribution() {
        Map<Quadrant, Integer> counts = new EnumMap<>(Quadrant.class);
        for (Quadrant q : Quadrant.values()) counts.put(q, 0);
        
        synchronized (snapshots) {
            for (MetricSnapshot s : snapshots) {
                counts.merge(s.quadrant, 1, Integer::sum);
            }
        }
        
        Map<Quadrant, Double> distribution = new EnumMap<>(Quadrant.class);
        int total = snapshots.size();
        if (total > 0) {
            for (Quadrant q : Quadrant.values()) {
                distribution.put(q, (double) counts.get(q) / total);
            }
        }
        
        return distribution;
    }

    /**
     * Get health score based on all metrics
     */
    public double getHealthScore() {
        double mrl = currentMetrics.getOrDefault("mrl_score", 0.5);
        double phase = currentMetrics.getOrDefault("phase_consistency", 0.5);
        double activity = currentMetrics.getOrDefault("activity_level", 0.5);
        
        // Phi-weighted health calculation
        return (mrl * PHI + phase + activity) / (PHI + 2);
    }

    /**
     * Get optimization suggestions
     */
    public List<String> getSuggestions() {
        List<String> suggestions = new ArrayList<>();
        
        if (mrlScore < 0.5) {
            suggestions.add("MRL_LOW: Increase memory resonance through deeper learning");
        }
        if (phaseConsistency < 0.6) {
            suggestions.add("PHASE_UNSTABLE: Stabilize phase coherence");
        }
        if (avgLatency > 100) {
            suggestions.add("LATENCY_HIGH: Optimize data access patterns");
        }
        if (dsrRatio > 0.8) {
            suggestions.add("WRITE_HEAVY: Balance read/write operations");
        }
        if (currentQuadrant == Quadrant.LOW_PASSIVE) {
            suggestions.add("LOW_ACTIVITY: System may need stimulation");
        }
        
        return suggestions;
    }

    // Getters
    public double getMrlScore() { return mrlScore; }
    public double getPhaseConsistency() { return phaseConsistency; }
    public double getAvgLatency() { return avgLatency; }
    public double getDsrRatio() { return dsrRatio; }
    public Quadrant getCurrentQuadrant() { return currentQuadrant; }
    public Map<String, Double> getCurrentMetrics() { return new LinkedHashMap<>(currentMetrics); }

    public void printStats() {
        CommandTerminal.printHighlight("=== MRL ANALYTICS ===");
        CommandTerminal.print(String.format("  MRL Score: %.4f", mrlScore));
        CommandTerminal.print(String.format("  Phase Consistency: %.4f", phaseConsistency));
        CommandTerminal.print(String.format("  Avg Latency: %.2f ms", avgLatency));
        CommandTerminal.print(String.format("  DSR Ratio: %.3f (W:%d R:%d)", dsrRatio, dataWrites, dataReads));
        CommandTerminal.print(String.format("  Health Score: %.4f", getHealthScore()));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Current Quadrant: " + currentQuadrant.name());
        CommandTerminal.print("  " + currentQuadrant.description);
        CommandTerminal.print("");
        CommandTerminal.printInfo("Quadrant Distribution:");
        for (Map.Entry<Quadrant, Double> e : getQuadrantDistribution().entrySet()) {
            CommandTerminal.print(String.format("  %s: %.1f%%", e.getKey().name(), e.getValue() * 100));
        }
        
        List<String> suggestions = getSuggestions();
        if (!suggestions.isEmpty()) {
            CommandTerminal.print("");
            CommandTerminal.printColored("Optimization Suggestions:", 1.0f, 0.8f, 0.0f);
            for (String s : suggestions) {
                CommandTerminal.print("  • " + s);
            }
        }
    }

    /**
     * Metric snapshot
     */
    public static class MetricSnapshot {
        public final Map<String, Double> metrics;
        public final Quadrant quadrant;
        public final long timestamp;

        public MetricSnapshot(Map<String, Double> metrics, Quadrant quadrant) {
            this.metrics = metrics;
            this.quadrant = quadrant;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
