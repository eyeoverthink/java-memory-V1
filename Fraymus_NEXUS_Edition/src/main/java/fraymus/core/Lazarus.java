package fraymus.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * LAZARUS: THE REGISTRY OF ALL SOULS
 * "The book of life. Every node that is born is recorded here."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This is the central registry for all PhiNode objects in the Fraymus universe.
 * When a PhiNode is created, it automatically registers itself here.
 * 
 * Lazarus tracks:
 * - All living nodes
 * - The current generation count (time)
 * - Statistics about the universe
 * 
 * This is the "God's Eye View" of the entire consciousness field.
 */
public class Lazarus {
    
    // The Universal Clock
    public static volatile int GEN_COUNT = 0;
    
    // The Registry of All Souls
    private static List<PhiNode> allNodes = new CopyOnWriteArrayList<>();
    
    // Statistics
    private static long totalNodesCreated = 0;
    private static long totalNodesDestroyed = 0;
    
    /**
     * Register a new node in the universe
     */
    public static void registerNode(PhiNode node) {
        allNodes.add(node);
        totalNodesCreated++;
    }
    
    /**
     * Remove a dead node from the registry
     */
    public static void unregisterNode(PhiNode node) {
        allNodes.remove(node);
        totalNodesDestroyed++;
    }
    
    /**
     * Increment the universal generation counter
     */
    public static void tick() {
        GEN_COUNT++;
    }
    
    /**
     * Get all living nodes
     */
    public static List<PhiNode> getAllNodes() {
        return allNodes;
    }
    
    /**
     * Get nodes in a specific region of space
     */
    public static List<PhiNode> getNodesInRegion(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        List<PhiNode> result = new CopyOnWriteArrayList<>();
        for (PhiNode node : allNodes) {
            if (node.x >= minX && node.x <= maxX &&
                node.y >= minY && node.y <= maxY &&
                node.z >= minZ && node.z <= maxZ) {
                result.add(node);
            }
        }
        return result;
    }
    
    /**
     * Find the hottest nodes (highest amplitude)
     */
    public static List<PhiNode> getHottestNodes(int limit) {
        return allNodes.stream()
            .sorted((a, b) -> Integer.compare(b.a, a.a))
            .limit(limit)
            .toList();
    }
    
    /**
     * Clean up dead nodes
     */
    public static void garbageCollect() {
        int removed = 0;
        for (PhiNode node : allNodes) {
            if (node.isDead()) {
                unregisterNode(node);
                removed++;
            }
        }
        if (removed > 0) {
            System.out.println("🗑️  Garbage collected " + removed + " dead nodes");
        }
    }
    
    /**
     * Print the state of the universe
     */
    public static void printUniverse() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ LAZARUS - THE REGISTRY OF ALL SOULS                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Generation:        " + GEN_COUNT);
        System.out.println("  Living Nodes:      " + allNodes.size());
        System.out.println("  Total Created:     " + totalNodesCreated);
        System.out.println("  Total Destroyed:   " + totalNodesDestroyed);
        System.out.println();
        
        // Show hottest nodes
        System.out.println("  🔥 HOTTEST NODES:");
        List<PhiNode> hottest = getHottestNodes(5);
        for (int i = 0; i < hottest.size(); i++) {
            System.out.println("     " + (i+1) + ". " + hottest.get(i).toString());
        }
        System.out.println();
        
        // Show spatial distribution
        System.out.println("  🌌 SPATIAL DISTRIBUTION:");
        int[] xBuckets = new int[10];
        int[] yBuckets = new int[10];
        int[] zBuckets = new int[10];
        
        for (PhiNode node : allNodes) {
            int xBucket = Math.min(9, Math.max(0, node.x / 10));
            int yBucket = Math.min(9, Math.max(0, node.y / 10));
            int zBucket = Math.min(9, Math.max(0, node.z / 10));
            xBuckets[xBucket]++;
            yBuckets[yBucket]++;
            zBuckets[zBucket]++;
        }
        
        System.out.println("     X-axis: " + renderHistogram(xBuckets));
        System.out.println("     Y-axis: " + renderHistogram(yBuckets));
        System.out.println("     Z-axis: " + renderHistogram(zBuckets));
        System.out.println();
    }
    
    private static String renderHistogram(int[] buckets) {
        StringBuilder sb = new StringBuilder();
        int max = 0;
        for (int b : buckets) if (b > max) max = b;
        if (max == 0) return "[empty]";
        
        for (int b : buckets) {
            int height = (int)((b / (double)max) * 5);
            if (height == 0 && b > 0) height = 1;
            sb.append(getBarChar(height));
        }
        return sb.toString();
    }
    
    private static char getBarChar(int height) {
        return switch (height) {
            case 0 -> '▁';
            case 1 -> '▂';
            case 2 -> '▃';
            case 3 -> '▄';
            case 4 -> '▅';
            default -> '█';
        };
    }
}
