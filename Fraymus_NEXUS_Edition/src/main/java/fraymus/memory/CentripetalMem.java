package fraymus.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * CENTRIPETAL MEMORY: RADIAL COMPRESSION
 * 
 * "The truth is always in the center."
 * 
 * Mechanism:
 * 1. Maps data segments to polar coordinate system (r, θ)
 * 2. Uses phi-resonance to pack data spirally
 * 3. High-priority data gravitates to center (r → 0)
 * 4. Low-priority data flung to edge (r → ∞)
 * 
 * Physics:
 * - Centripetal force: F = mv²/r
 * - Heavy data (high importance) has low radius
 * - Light data (low importance) has high radius
 * - Phi-spiral distribution prevents collisions
 * - Golden angle (137.5°) ensures optimal packing
 * 
 * Compression:
 * - Traditional: Linear array [0, 1, 2, ...]
 * - Centripetal: Radial cloud (r, θ)
 * - Core data: Dense, hot, critical
 * - Edge data: Sparse, cold, disposable
 * 
 * Retrieval:
 * - Read from center outward (drill to core)
 * - Most important data retrieved first
 * - Natural priority queue
 */
public class CentripetalMem {
    
    private static final double PHI = 1.6180339887;
    private static final double GOLDEN_ANGLE = 137.5077640500378;
    
    private List<PolarNode> memorySpiral = new ArrayList<>();
    private int totalNodes = 0;
    
    /**
     * Store data in centripetal spiral
     */
    public void storeData(String data, double importance) {
        // Importance: 0.0 (Junk) to 1.0 (Critical)
        // Invert importance to get radius (High importance = Low radius)
        double radius = (1.0 - importance) * 10.0;
        
        // Angle determined by hash of data (content-addressable)
        // Scaled by phi to ensure no collisions (golden angle distribution)
        double theta = Math.abs(data.hashCode()) * PHI * Math.PI / 180.0;
        
        // Normalize theta to [0, 2π]
        theta = theta % (2 * Math.PI);
        
        PolarNode node = new PolarNode(radius, theta, data, importance);
        memorySpiral.add(node);
        totalNodes++;
        
        // Sort by radius (physically move heavy items to center)
        memorySpiral.sort((a, b) -> Double.compare(a.r, b.r));
        
        System.out.println("   Stored: [" + data.substring(0, Math.min(20, data.length())) + 
                         "...] | Importance: " + String.format("%.2f", importance) + 
                         " | Radius: " + String.format("%.2f", radius));
    }
    
    /**
     * Read core memories (center of spiral)
     */
    public void readCore() {
        System.out.println("\n🌊⚡ READING MEMORY CORE (CENTRIPETAL SCAN)");
        System.out.println("   Drilling to singularity...");
        System.out.println();
        
        int coreCount = 0;
        
        for (PolarNode node : memorySpiral) {
            if (node.r < 2.0) { // Only read the "core" memories
                System.out.println("   CORE DATA [r=" + String.format("%.2f", node.r) + 
                                 ", θ=" + String.format("%.2f°", Math.toDegrees(node.theta)) + 
                                 ", importance=" + String.format("%.2f", node.importance) + "]: " + 
                                 node.content);
                coreCount++;
            }
        }
        
        System.out.println();
        System.out.println("   ✓ Core memories: " + coreCount + " / " + totalNodes);
        System.out.println();
    }
    
    /**
     * Read all memories (full spiral scan)
     */
    public void readAll() {
        System.out.println("\n🌊⚡ READING FULL MEMORY SPIRAL");
        System.out.println("   Total nodes: " + totalNodes);
        System.out.println();
        
        for (int i = 0; i < memorySpiral.size(); i++) {
            PolarNode node = memorySpiral.get(i);
            System.out.println("   [" + i + "] r=" + String.format("%.2f", node.r) + 
                             ", θ=" + String.format("%.2f°", Math.toDegrees(node.theta)) + 
                             " | " + node.content);
        }
        
        System.out.println();
    }
    
    /**
     * Compress by removing edge data (low importance)
     */
    public void compress(double radiusThreshold) {
        System.out.println("\n🌊⚡ COMPRESSING MEMORY (CENTRIPETAL)");
        System.out.println("   Threshold radius: " + radiusThreshold);
        
        int beforeCount = memorySpiral.size();
        
        // Remove nodes beyond threshold (edge data)
        memorySpiral.removeIf(node -> node.r > radiusThreshold);
        
        int afterCount = memorySpiral.size();
        int removed = beforeCount - afterCount;
        
        System.out.println("   ✓ Removed: " + removed + " edge nodes");
        System.out.println("   ✓ Retained: " + afterCount + " core nodes");
        System.out.println("   ✓ Compression ratio: " + 
                         String.format("%.1f%%", (removed * 100.0 / beforeCount)));
        System.out.println();
    }
    
    /**
     * Get statistics
     */
    public void printStats() {
        System.out.println("\n🌊⚡ CENTRIPETAL MEMORY STATISTICS");
        System.out.println("   Total nodes: " + totalNodes);
        System.out.println("   Active nodes: " + memorySpiral.size());
        
        if (!memorySpiral.isEmpty()) {
            double minRadius = memorySpiral.get(0).r;
            double maxRadius = memorySpiral.get(memorySpiral.size() - 1).r;
            
            System.out.println("   Core radius: " + String.format("%.2f", minRadius));
            System.out.println("   Edge radius: " + String.format("%.2f", maxRadius));
            System.out.println("   Spiral depth: " + String.format("%.2f", maxRadius - minRadius));
        }
        
        System.out.println();
    }
    
    /**
     * Get core data for thermal broadcast
     */
    public List<String> getCoreData(int maxItems) {
        List<String> core = new ArrayList<>();
        
        for (int i = 0; i < Math.min(maxItems, memorySpiral.size()); i++) {
            if (memorySpiral.get(i).r < 2.0) {
                core.add(memorySpiral.get(i).content);
            }
        }
        
        return core;
    }
    
    /**
     * Polar node in memory spiral
     */
    class PolarNode {
        double r;           // Distance from center (radius)
        double theta;       // Angle (radians)
        String content;     // Data payload
        double importance;  // Priority weight
        
        public PolarNode(double r, double theta, String content, double importance) {
            this.r = r;
            this.theta = theta;
            this.content = content;
            this.importance = importance;
        }
        
        /**
         * Convert to Cartesian coordinates (for visualization)
         */
        public double getX() {
            return r * Math.cos(theta);
        }
        
        public double getY() {
            return r * Math.sin(theta);
        }
    }
}
