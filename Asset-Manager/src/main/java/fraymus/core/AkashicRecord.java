package fraymus.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 📜 AKASHIC RECORD - THE TEMPORAL ARCHIVE
 * 
 * Stores the system's "DNA" and historical "Fossils."
 * Every version of logic is preserved to prevent regression
 * and enable temporal analysis of evolutionary trajectories.
 * 
 * This is the Memory component of the Supercession Protocol.
 */
public class AkashicRecord {
    
    // ThreadID -> List of Historical Logic States (DNA Fossils)
    private final Map<String, List<LogicFossil>> temporalArchive = new ConcurrentHashMap<>();
    
    // Global event timeline
    private final List<TemporalEvent> timeline = Collections.synchronizedList(new ArrayList<>());
    
    // Statistics
    private long totalFossils = 0;
    private long totalEvents = 0;
    
    /**
     * Preserve a logic fossil for temporal analysis
     */
    public void preserveFossil(String threadId, String logicDna) {
        LogicFossil fossil = new LogicFossil(
            System.currentTimeMillis(),
            logicDna,
            calculateDnaHash(logicDna)
        );
        
        temporalArchive.computeIfAbsent(threadId, k -> new ArrayList<>()).add(fossil);
        totalFossils++;
        
        System.out.println("[AKASHIC] Fossil preserved for: " + threadId + " (Hash: " + fossil.hash + ")");
    }
    
    /**
     * Preserve a fossil with metadata
     */
    public void preserveFossilWithMetadata(String threadId, String logicDna, Map<String, Object> metadata) {
        LogicFossil fossil = new LogicFossil(
            System.currentTimeMillis(),
            logicDna,
            calculateDnaHash(logicDna)
        );
        fossil.metadata.putAll(metadata);
        
        temporalArchive.computeIfAbsent(threadId, k -> new ArrayList<>()).add(fossil);
        totalFossils++;
        
        System.out.println("[AKASHIC] Fossil preserved for: " + threadId + " with metadata: " + metadata.keySet());
    }
    
    /**
     * Get complete lineage for a thread
     */
    public List<LogicFossil> getLineage(String threadId) {
        return new ArrayList<>(temporalArchive.getOrDefault(threadId, Collections.emptyList()));
    }
    
    /**
     * Get most recent fossil for a thread
     */
    public LogicFossil getLatestFossil(String threadId) {
        List<LogicFossil> lineage = temporalArchive.get(threadId);
        if (lineage == null || lineage.isEmpty()) return null;
        return lineage.get(lineage.size() - 1);
    }
    
    /**
     * Record a temporal event
     */
    public void recordEvent(String eventType, String description, double intensity) {
        TemporalEvent event = new TemporalEvent(
            System.currentTimeMillis(),
            eventType,
            description,
            intensity
        );
        timeline.add(event);
        totalEvents++;
    }
    
    /**
     * Get events within time range
     */
    public List<TemporalEvent> getEventsInRange(long startTime, long endTime) {
        List<TemporalEvent> filtered = new ArrayList<>();
        for (TemporalEvent event : timeline) {
            if (event.timestamp >= startTime && event.timestamp <= endTime) {
                filtered.add(event);
            }
        }
        return filtered;
    }
    
    /**
     * Analyze evolutionary trajectory using phi-based prediction
     */
    public EvolutionaryTrajectory analyzeTrajectory(String threadId) {
        List<LogicFossil> lineage = getLineage(threadId);
        if (lineage.size() < 2) {
            return new EvolutionaryTrajectory(0.0, 0.0, "INSUFFICIENT_DATA");
        }
        
        // Calculate mutation rate
        int mutations = 0;
        for (int i = 1; i < lineage.size(); i++) {
            if (!lineage.get(i).hash.equals(lineage.get(i-1).hash)) {
                mutations++;
            }
        }
        double mutationRate = (double) mutations / lineage.size();
        
        // Calculate growth trajectory using phi
        double PHI = 1.6180339887;
        double predictedGrowth = mutationRate * PHI;
        
        // Determine trajectory type
        String trajectoryType;
        if (mutationRate > 0.618) {
            trajectoryType = "RAPID_EVOLUTION";
        } else if (mutationRate > 0.382) {
            trajectoryType = "STEADY_GROWTH";
        } else if (mutationRate > 0.1) {
            trajectoryType = "SLOW_ADAPTATION";
        } else {
            trajectoryType = "STABLE";
        }
        
        return new EvolutionaryTrajectory(mutationRate, predictedGrowth, trajectoryType);
    }
    
    /**
     * Calculate DNA hash for deduplication
     */
    private String calculateDnaHash(String dna) {
        // Simple hash based on phi-harmonic fingerprint
        long hash = 0;
        for (int i = 0; i < dna.length(); i++) {
            hash = hash * 1618033 + dna.charAt(i);
        }
        return Long.toHexString(hash);
    }
    
    /**
     * Get statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFossils", totalFossils);
        stats.put("totalEvents", totalEvents);
        stats.put("uniqueThreads", temporalArchive.size());
        stats.put("timelineSpan", timeline.isEmpty() ? 0 : 
            timeline.get(timeline.size() - 1).timestamp - timeline.get(0).timestamp);
        return stats;
    }
    
    /**
     * Print archive status
     */
    public void printStatus() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   📜 AKASHIC RECORD STATUS                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Total Fossils: " + totalFossils);
        System.out.println("  Total Events: " + totalEvents);
        System.out.println("  Unique Threads: " + temporalArchive.size());
        System.out.println("  Timeline Entries: " + timeline.size());
        
        if (!temporalArchive.isEmpty()) {
            System.out.println("\n  Thread Lineages:");
            for (Map.Entry<String, List<LogicFossil>> entry : temporalArchive.entrySet()) {
                System.out.println("    - " + entry.getKey() + ": " + entry.getValue().size() + " fossils");
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // INNER CLASSES
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Logic Fossil - A preserved state of logic DNA
     */
    public static class LogicFossil {
        public final long timestamp;
        public final String dna;
        public final String hash;
        public final Map<String, Object> metadata;
        
        public LogicFossil(long timestamp, String dna, String hash) {
            this.timestamp = timestamp;
            this.dna = dna;
            this.hash = hash;
            this.metadata = new HashMap<>();
        }
    }
    
    /**
     * Temporal Event - A recorded system event
     */
    public static class TemporalEvent {
        public final long timestamp;
        public final String eventType;
        public final String description;
        public final double intensity;
        
        public TemporalEvent(long timestamp, String eventType, String description, double intensity) {
            this.timestamp = timestamp;
            this.eventType = eventType;
            this.description = description;
            this.intensity = intensity;
        }
    }
    
    /**
     * Evolutionary Trajectory - Analysis result
     */
    public static class EvolutionaryTrajectory {
        public final double mutationRate;
        public final double predictedGrowth;
        public final String trajectoryType;
        
        public EvolutionaryTrajectory(double mutationRate, double predictedGrowth, String trajectoryType) {
            this.mutationRate = mutationRate;
            this.predictedGrowth = predictedGrowth;
            this.trajectoryType = trajectoryType;
        }
    }
}
