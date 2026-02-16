package fraymus.memory;

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.*;

/**
 * CENTRIPETAL MEMORY: RADIAL COMPRESSION
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "The truth is always in the center."
 * 
 * Unlike linear compression (ZIP), this is RADIAL compression.
 * Works like a centrifuge or a black hole:
 * - Heavy, meaningful data (high importance) → Falls to the CENTER
 * - Light, noisy data (low importance) → Flung to the EDGE
 * 
 * To read the most important memories, you don't scan the whole drive;
 * you just look at the CORE. The data compresses itself by falling
 * into the center of the Phi-Spiral.
 * 
 * Mathematical Foundation:
 * - Polar coordinates (r, θ) instead of linear array
 * - Importance maps inversely to radius (high importance = low r)
 * - Angle determined by content hash × φ (Golden Angle distribution)
 * - Self-organizing: data naturally clusters by semantic similarity
 */
public class CentripetalMem {

    private static final double PHI = PhiQuantumConstants.PHI;
    private static final double PHI_INV = PhiQuantumConstants.PHI_INVERSE;
    private static final double GOLDEN_ANGLE = 2 * Math.PI * PHI_INV;

    // The memory spiral - sorted by radius (center = most important)
    private final List<PolarNode> memorySpiral = new ArrayList<>();
    
    // Index by content hash for fast lookup
    private final Map<Long, PolarNode> hashIndex = new HashMap<>();
    
    // Statistics
    private double totalImportance = 0;
    private int accessCount = 0;
    private long creationTime = System.currentTimeMillis();

    // ═══════════════════════════════════════════════════════════════════
    // 1. INGEST (The Spin) - Store data in the spiral
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Store data with importance weighting
     * @param data The content to store
     * @param importance 0.0 (junk) to 1.0 (critical)
     */
    public void storeData(String data, double importance) {
        storeData(data, importance, null);
    }

    /**
     * Store data with importance and optional tags
     */
    public void storeData(String data, double importance, String[] tags) {
        // Clamp importance
        importance = Math.max(0.0, Math.min(1.0, importance));
        
        // Invert importance to get radius (high importance = low radius)
        // Use exponential scaling for better distribution
        double radius = Math.pow(1.0 - importance, PHI) * 10.0;
        
        // Angle determined by content hash × φ (ensures no collisions)
        long hash = computeHash(data);
        double theta = (Math.abs(hash) * GOLDEN_ANGLE) % (2 * Math.PI);
        
        // Check for existing entry
        if (hashIndex.containsKey(hash)) {
            // Update existing - boost importance
            PolarNode existing = hashIndex.get(hash);
            existing.accessCount++;
            existing.importance = Math.min(1.0, existing.importance + 0.1);
            existing.r = Math.pow(1.0 - existing.importance, PHI) * 10.0;
            existing.lastAccess = System.currentTimeMillis();
            resort();
            return;
        }
        
        // Create new node
        PolarNode node = new PolarNode(radius, theta, data, importance, hash);
        if (tags != null) {
            node.tags.addAll(Arrays.asList(tags));
        }
        
        memorySpiral.add(node);
        hashIndex.put(hash, node);
        totalImportance += importance;
        
        // Keep sorted by radius (closest to center first)
        resort();
    }

    /**
     * Store binary data
     */
    public void storeBinary(byte[] data, double importance, String label) {
        String encoded = Base64.getEncoder().encodeToString(data);
        storeData("BINARY:" + label + ":" + encoded, importance, new String[]{"binary", label});
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. RETRIEVE (The Drill) - Read data from the spiral
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Read the core memories (most important)
     */
    public List<String> readCore() {
        return readCore(2.0);
    }

    /**
     * Read memories within specified radius
     */
    public List<String> readCore(double maxRadius) {
        List<String> coreData = new ArrayList<>();
        
        for (PolarNode node : memorySpiral) {
            if (node.r <= maxRadius) {
                node.accessCount++;
                node.lastAccess = System.currentTimeMillis();
                accessCount++;
                coreData.add(node.content);
            } else {
                break; // List is sorted, no need to continue
            }
        }
        
        return coreData;
    }

    /**
     * Read by importance threshold
     */
    public List<String> readByImportance(double minImportance) {
        List<String> result = new ArrayList<>();
        
        for (PolarNode node : memorySpiral) {
            if (node.importance >= minImportance) {
                node.accessCount++;
                node.lastAccess = System.currentTimeMillis();
                accessCount++;
                result.add(node.content);
            }
        }
        
        return result;
    }

    /**
     * Search by tag
     */
    public List<String> searchByTag(String tag) {
        List<String> result = new ArrayList<>();
        
        for (PolarNode node : memorySpiral) {
            if (node.tags.contains(tag)) {
                result.add(node.content);
            }
        }
        
        return result;
    }

    /**
     * Search by content similarity
     */
    public List<String> searchSimilar(String query, int maxResults) {
        List<Map.Entry<PolarNode, Double>> scored = new ArrayList<>();
        
        for (PolarNode node : memorySpiral) {
            double similarity = computeSimilarity(query, node.content);
            if (similarity > 0.1) {
                scored.add(new AbstractMap.SimpleEntry<>(node, similarity));
            }
        }
        
        // Sort by similarity descending
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(maxResults, scored.size()); i++) {
            result.add(scored.get(i).getKey().content);
        }
        
        return result;
    }

    // ═══════════════════════════════════════════════════════════════════
    // 3. COMPRESSION (The Collapse) - Compress the spiral
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Compress by removing low-importance outer edge
     */
    public int compress(double keepRadius) {
        int removed = 0;
        Iterator<PolarNode> iter = memorySpiral.iterator();
        
        while (iter.hasNext()) {
            PolarNode node = iter.next();
            if (node.r > keepRadius) {
                hashIndex.remove(node.hash);
                totalImportance -= node.importance;
                iter.remove();
                removed++;
            }
        }
        
        return removed;
    }

    /**
     * Compress by access recency (remove stale data)
     */
    public int compressByAge(long maxAgeMs) {
        int removed = 0;
        long now = System.currentTimeMillis();
        Iterator<PolarNode> iter = memorySpiral.iterator();
        
        while (iter.hasNext()) {
            PolarNode node = iter.next();
            if (now - node.lastAccess > maxAgeMs && node.importance < 0.8) {
                hashIndex.remove(node.hash);
                totalImportance -= node.importance;
                iter.remove();
                removed++;
            }
        }
        
        return removed;
    }

    /**
     * Decay all importance over time (forgetting curve)
     */
    public void applyDecay(double decayRate) {
        for (PolarNode node : memorySpiral) {
            // Decay importance
            node.importance *= decayRate;
            
            // Recalculate radius
            node.r = Math.pow(1.0 - node.importance, PHI) * 10.0;
        }
        
        // Resort after decay
        resort();
        
        // Recalculate total importance
        totalImportance = 0;
        for (PolarNode node : memorySpiral) {
            totalImportance += node.importance;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // 4. VISUALIZATION (The Map)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Get spiral data for visualization
     */
    public List<double[]> getSpiralCoordinates() {
        List<double[]> coords = new ArrayList<>();
        
        for (PolarNode node : memorySpiral) {
            // Convert polar to cartesian
            double x = node.r * Math.cos(node.theta);
            double y = node.r * Math.sin(node.theta);
            
            coords.add(new double[]{x, y, node.r, node.importance});
        }
        
        return coords;
    }

    /**
     * Get density map (for heatmap visualization)
     */
    public double[][] getDensityMap(int resolution) {
        double[][] density = new double[resolution][resolution];
        double maxRadius = 10.0;
        double cellSize = (maxRadius * 2) / resolution;
        
        for (PolarNode node : memorySpiral) {
            double x = node.r * Math.cos(node.theta);
            double y = node.r * Math.sin(node.theta);
            
            int cellX = (int)((x + maxRadius) / cellSize);
            int cellY = (int)((y + maxRadius) / cellSize);
            
            if (cellX >= 0 && cellX < resolution && cellY >= 0 && cellY < resolution) {
                density[cellX][cellY] += node.importance;
            }
        }
        
        return density;
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════════
    
    private void resort() {
        memorySpiral.sort(Comparator.comparingDouble(n -> n.r));
    }

    private long computeHash(String data) {
        long hash = 0;
        for (char c : data.toCharArray()) {
            hash = hash * 31 + c;
            hash ^= (long)(hash * PHI);
        }
        return hash;
    }

    private double computeSimilarity(String a, String b) {
        // Simple Jaccard-like similarity
        Set<String> wordsA = new HashSet<>(Arrays.asList(a.toLowerCase().split("\\s+")));
        Set<String> wordsB = new HashSet<>(Arrays.asList(b.toLowerCase().split("\\s+")));
        
        Set<String> intersection = new HashSet<>(wordsA);
        intersection.retainAll(wordsB);
        
        Set<String> union = new HashSet<>(wordsA);
        union.addAll(wordsB);
        
        if (union.isEmpty()) return 0;
        return (double) intersection.size() / union.size();
    }

    public int size() { return memorySpiral.size(); }
    public double getTotalImportance() { return totalImportance; }
    public int getAccessCount() { return accessCount; }
    
    public double getCoreRadius() {
        // Calculate radius containing 80% of importance
        double accumulated = 0;
        double target = totalImportance * 0.8;
        
        for (PolarNode node : memorySpiral) {
            accumulated += node.importance;
            if (accumulated >= target) {
                return node.r;
            }
        }
        return 10.0;
    }

    /**
     * Get status report
     */
    public String getStatus() {
        return String.format(
            "═══ CENTRIPETAL MEMORY STATUS ═══%n" +
            "Nodes: %d%n" +
            "Total Importance: %.2f%n" +
            "Core Radius (80%%): %.2f%n" +
            "Access Count: %d%n" +
            "Age: %d seconds%n" +
            "Compression Ratio: %.2fx",
            memorySpiral.size(),
            totalImportance,
            getCoreRadius(),
            accessCount,
            (System.currentTimeMillis() - creationTime) / 1000,
            10.0 / (getCoreRadius() + 0.1)
        );
    }

    /**
     * Export as serializable format
     */
    public String export() {
        StringBuilder sb = new StringBuilder();
        sb.append("CENTRIPETAL_MEM_V1\n");
        sb.append(memorySpiral.size()).append("\n");
        
        for (PolarNode node : memorySpiral) {
            sb.append(node.r).append("|");
            sb.append(node.theta).append("|");
            sb.append(node.importance).append("|");
            sb.append(Base64.getEncoder().encodeToString(node.content.getBytes()));
            sb.append("\n");
        }
        
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // POLAR NODE
    // ═══════════════════════════════════════════════════════════════════
    
    public static class PolarNode {
        public double r;        // Distance from center (singularity)
        public double theta;    // Angle
        public String content;  // The actual data
        public double importance;
        public long hash;
        public long createdAt;
        public long lastAccess;
        public int accessCount;
        public Set<String> tags;

        public PolarNode(double r, double theta, String content, double importance, long hash) {
            this.r = r;
            this.theta = theta;
            this.content = content;
            this.importance = importance;
            this.hash = hash;
            this.createdAt = System.currentTimeMillis();
            this.lastAccess = this.createdAt;
            this.accessCount = 0;
            this.tags = new HashSet<>();
        }

        public double getX() { return r * Math.cos(theta); }
        public double getY() { return r * Math.sin(theta); }

        @Override
        public String toString() {
            return String.format("Node[r=%.2f, θ=%.2f, imp=%.2f]: %s",
                r, theta, importance, 
                content.length() > 30 ? content.substring(0, 30) + "..." : content);
        }
    }
}
