package fraymus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SELF-2: BARDO MEMORY PATTERNS
 * 
 * Enhanced memory with BARDO (Between-state Awareness Resonance Data Organization)
 * - Clusters memory by resonance patterns
 * - Tracks "dream" states (dormant memory clusters)
 * - Visualizes memory density as phi-weighted resonance map
 */
public class BardoMemoryPatterns {

    private static final double PHI = 1.618033988749895;
    private static final double PHI_INV = 0.618033988749895;

    // BARDO states (intermediate consciousness levels)
    public enum BardoState {
        CHIKHAI(0, "Clear Light", 1.0),           // Pure awareness
        CHONYID(1, "Reality", 0.8),               // Pattern recognition
        SIDPA(2, "Seeking", 0.6),                 // Active search
        DORMANT(3, "Dream", 0.3);                 // Inactive patterns

        public final int level;
        public final String description;
        public final double resonanceThreshold;

        BardoState(int level, String desc, double threshold) {
            this.level = level;
            this.description = desc;
            this.resonanceThreshold = threshold;
        }
    }

    // Memory clusters organized by BARDO state
    private final Map<BardoState, List<MemoryCluster>> bardoClusters = new ConcurrentHashMap<>();
    
    // Dream array - dormant memories that may resurface
    private final List<DreamFragment> dreamArray = Collections.synchronizedList(new ArrayList<>());
    
    // Resonance density map (phi-weighted)
    private final double[][] resonanceMap = new double[8][8]; // 8x8 grid
    
    private final InfiniteMemory memory;
    private int totalClusters = 0;
    private double avgResonance = PHI_INV;

    public BardoMemoryPatterns(InfiniteMemory memory) {
        this.memory = memory;
        
        // Initialize BARDO clusters
        for (BardoState state : BardoState.values()) {
            bardoClusters.put(state, Collections.synchronizedList(new ArrayList<>()));
        }
    }

    /**
     * Cluster a memory record into appropriate BARDO state
     */
    public void clusterMemory(InfiniteMemory.MemoryRecord record) {
        BardoState state = determineBardoState(record.phiResonance);
        
        // Find or create cluster
        MemoryCluster cluster = findOrCreateCluster(state, record.category);
        cluster.addRecord(record);
        
        // Update resonance map
        updateResonanceMap(record);
        
        // Check for dream transition
        if (cluster.getAge() > 300000 && cluster.getAccessCount() < 3) { // 5 min inactive
            transitionToDream(cluster);
        }
        
        avgResonance = avgResonance * 0.95 + record.phiResonance * 0.05;
    }

    /**
     * Determine BARDO state based on resonance level
     */
    private BardoState determineBardoState(double resonance) {
        for (BardoState state : BardoState.values()) {
            if (resonance >= state.resonanceThreshold) {
                return state;
            }
        }
        return BardoState.DORMANT;
    }

    /**
     * Find existing cluster or create new one
     */
    private MemoryCluster findOrCreateCluster(BardoState state, String category) {
        List<MemoryCluster> clusters = bardoClusters.get(state);
        
        synchronized (clusters) {
            for (MemoryCluster cluster : clusters) {
                if (cluster.category.equals(category)) {
                    return cluster;
                }
            }
            
            // Create new cluster
            MemoryCluster newCluster = new MemoryCluster(category, state);
            clusters.add(newCluster);
            totalClusters++;
            return newCluster;
        }
    }

    /**
     * Transition inactive cluster to dream state
     */
    private void transitionToDream(MemoryCluster cluster) {
        // Remove from active clusters
        List<MemoryCluster> clusters = bardoClusters.get(cluster.state);
        clusters.remove(cluster);
        
        // Create dream fragment
        DreamFragment dream = new DreamFragment(cluster);
        dreamArray.add(dream);
        
        // Keep dream array bounded
        while (dreamArray.size() > 100) {
            dreamArray.remove(0);
        }
    }

    /**
     * Attempt to recall from dream state
     */
    public MemoryCluster recallFromDream(String category) {
        synchronized (dreamArray) {
            for (int i = dreamArray.size() - 1; i >= 0; i--) {
                DreamFragment dream = dreamArray.get(i);
                if (dream.category.equals(category)) {
                    // Resurrect cluster
                    MemoryCluster resurrected = dream.resurrect();
                    dreamArray.remove(i);
                    
                    // Add back to appropriate BARDO state
                    List<MemoryCluster> clusters = bardoClusters.get(resurrected.state);
                    clusters.add(resurrected);
                    
                    return resurrected;
                }
            }
        }
        return null;
    }

    /**
     * Update phi-weighted resonance density map
     */
    private void updateResonanceMap(InfiniteMemory.MemoryRecord record) {
        // Map category hash to grid position
        int hash = Math.abs(record.category.hashCode());
        int x = hash % 8;
        int y = (hash / 8) % 8;
        
        // Phi-weighted update
        resonanceMap[x][y] = resonanceMap[x][y] * PHI_INV + record.phiResonance * (1 - PHI_INV);
        
        // Spread to neighbors (diffusion)
        double spread = record.phiResonance * 0.1;
        if (x > 0) resonanceMap[x-1][y] += spread;
        if (x < 7) resonanceMap[x+1][y] += spread;
        if (y > 0) resonanceMap[x][y-1] += spread;
        if (y < 7) resonanceMap[x][y+1] += spread;
    }

    /**
     * Get resonance at map position
     */
    public double getResonanceAt(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return 0;
        return resonanceMap[x][y];
    }

    /**
     * Get hottest regions in resonance map
     */
    public List<int[]> getHotspots(int count) {
        List<int[]> spots = new ArrayList<>();
        
        // Create list of all positions with resonance
        List<double[]> all = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                all.add(new double[]{x, y, resonanceMap[x][y]});
            }
        }
        
        // Sort by resonance descending
        all.sort((a, b) -> Double.compare(b[2], a[2]));
        
        // Return top positions
        for (int i = 0; i < Math.min(count, all.size()); i++) {
            spots.add(new int[]{(int)all.get(i)[0], (int)all.get(i)[1]});
        }
        
        return spots;
    }

    /**
     * Query memories by BARDO state
     */
    public List<InfiniteMemory.MemoryRecord> queryByState(BardoState state) {
        List<InfiniteMemory.MemoryRecord> result = new ArrayList<>();
        List<MemoryCluster> clusters = bardoClusters.get(state);
        
        synchronized (clusters) {
            for (MemoryCluster cluster : clusters) {
                result.addAll(cluster.records);
            }
        }
        
        return result;
    }

    /**
     * Get cluster stats
     */
    public Map<BardoState, Integer> getClusterCounts() {
        Map<BardoState, Integer> counts = new LinkedHashMap<>();
        for (BardoState state : BardoState.values()) {
            counts.put(state, bardoClusters.get(state).size());
        }
        return counts;
    }

    public int getTotalClusters() { return totalClusters; }
    public int getDreamCount() { return dreamArray.size(); }
    public double getAvgResonance() { return avgResonance; }

    public void printStats() {
        CommandTerminal.printHighlight("=== BARDO MEMORY PATTERNS ===");
        CommandTerminal.print(String.format("  Total Clusters: %d", totalClusters));
        CommandTerminal.print(String.format("  Dream Fragments: %d", dreamArray.size()));
        CommandTerminal.print(String.format("  Avg Resonance: %.4f", avgResonance));
        CommandTerminal.print("");
        CommandTerminal.printInfo("BARDO States:");
        for (BardoState state : BardoState.values()) {
            int count = bardoClusters.get(state).size();
            CommandTerminal.print(String.format("  %s (%s): %d clusters", 
                    state.name(), state.description, count));
        }
        CommandTerminal.print("");
        CommandTerminal.printInfo("Resonance Hotspots:");
        for (int[] spot : getHotspots(3)) {
            CommandTerminal.print(String.format("  [%d,%d] = %.3f", 
                    spot[0], spot[1], resonanceMap[spot[0]][spot[1]]));
        }
    }

    /**
     * Memory cluster container
     */
    public static class MemoryCluster {
        public final String category;
        public final BardoState state;
        public final List<InfiniteMemory.MemoryRecord> records;
        private final long createdAt;
        private long lastAccess;
        private int accessCount;

        public MemoryCluster(String category, BardoState state) {
            this.category = category;
            this.state = state;
            this.records = Collections.synchronizedList(new ArrayList<>());
            this.createdAt = System.currentTimeMillis();
            this.lastAccess = createdAt;
            this.accessCount = 0;
        }

        public void addRecord(InfiniteMemory.MemoryRecord record) {
            records.add(record);
            lastAccess = System.currentTimeMillis();
            accessCount++;
        }

        public long getAge() {
            return System.currentTimeMillis() - lastAccess;
        }

        public int getAccessCount() { return accessCount; }
        public int size() { return records.size(); }
    }

    /**
     * Dream fragment - dormant memory cluster
     */
    public static class DreamFragment {
        public final String category;
        public final BardoState originalState;
        public final int recordCount;
        public final double avgResonance;
        private final List<String> recordHashes;
        private final long dormantSince;

        public DreamFragment(MemoryCluster cluster) {
            this.category = cluster.category;
            this.originalState = cluster.state;
            this.recordCount = cluster.records.size();
            this.dormantSince = System.currentTimeMillis();
            
            // Store hashes for potential resurrection
            this.recordHashes = new ArrayList<>();
            double sum = 0;
            for (InfiniteMemory.MemoryRecord r : cluster.records) {
                recordHashes.add(r.hash);
                sum += r.phiResonance;
            }
            this.avgResonance = recordCount > 0 ? sum / recordCount : 0;
        }

        public MemoryCluster resurrect() {
            MemoryCluster cluster = new MemoryCluster(category, originalState);
            // Note: Full resurrection would need to reload records from storage
            return cluster;
        }

        public long getDormantDuration() {
            return System.currentTimeMillis() - dormantSince;
        }
    }
}
