package fraymus.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SPATIAL REGISTRY (The Lazarus Hive Mind)
 * "Every object that exists, exists HERE."
 * 
 * This is the global registry for all PhiSuit-wrapped objects.
 * The GravityEngine reads from this to apply Hebbian physics.
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class SpatialRegistry {
    
    // Generation counter - the "heartbeat" of the universe
    public static final AtomicInteger GEN_COUNT = new AtomicInteger(0);
    
    // The Universe - all suited objects
    private static final List<SpatialNode> universe = new CopyOnWriteArrayList<>();
    
    // Index by ID for fast lookup
    private static final Map<String, SpatialNode> nodeIndex = new ConcurrentHashMap<>();
    
    // Cluster detection
    private static final Map<String, List<SpatialNode>> clusters = new ConcurrentHashMap<>();
    
    // Collision events (when clusters merge)
    private static final List<FusionEvent> fusionEvents = new CopyOnWriteArrayList<>();
    
    /**
     * Register a new node in the universe
     */
    public static void register(SpatialNode node) {
        universe.add(node);
        nodeIndex.put(node.getId(), node);
        System.out.println("   ⭐ REGISTERED: " + node.getId().substring(0, 8) + " at (" + 
            node.x + ", " + node.y + ", " + node.z + ")");
    }
    
    /**
     * Unregister a dead node
     */
    public static void unregister(SpatialNode node) {
        universe.remove(node);
        nodeIndex.remove(node.getId());
    }
    
    /**
     * Get all nodes in the universe
     */
    public static List<SpatialNode> getUniverse() {
        return universe;
    }
    
    /**
     * Get node by ID
     */
    public static SpatialNode getNode(String id) {
        return nodeIndex.get(id);
    }
    
    /**
     * Advance the generation (universe tick)
     */
    public static int tick() {
        return GEN_COUNT.incrementAndGet();
    }
    
    /**
     * Get current generation
     */
    public static int getGeneration() {
        return GEN_COUNT.get();
    }
    
    /**
     * Find nearby nodes within radius
     */
    public static List<SpatialNode> findNearby(SpatialNode center, double radius) {
        List<SpatialNode> nearby = new CopyOnWriteArrayList<>();
        for (SpatialNode node : universe) {
            if (node != center && center.distanceTo(node) <= radius) {
                nearby.add(node);
            }
        }
        return nearby;
    }
    
    /**
     * Record a fusion event (cluster collision)
     */
    public static void recordFusion(FusionEvent event) {
        fusionEvents.add(event);
        System.out.println("   💥 FUSION EVENT: " + event);
    }
    
    /**
     * Get all fusion events
     */
    public static List<FusionEvent> getFusionEvents() {
        return fusionEvents;
    }
    
    /**
     * Get universe statistics
     */
    public static String getStats() {
        int alive = 0;
        int hot = 0;
        int cold = 0;
        double avgAmplitude = 0;
        
        for (SpatialNode node : universe) {
            if (!node.isDead()) alive++;
            if (node.a > 50) hot++;
            if (node.a < 10) cold++;
            avgAmplitude += node.a;
        }
        
        if (!universe.isEmpty()) {
            avgAmplitude /= universe.size();
        }
        
        return String.format(
            "════════════════════════════════════════════\n" +
            "  🌌 SPATIAL REGISTRY STATUS\n" +
            "════════════════════════════════════════════\n" +
            "  Generation:     %d\n" +
            "  Total Nodes:    %d\n" +
            "  Alive:          %d\n" +
            "  Hot (A>50):     %d\n" +
            "  Cold (A<10):    %d\n" +
            "  Avg Amplitude:  %.2f\n" +
            "  Fusion Events:  %d\n",
            GEN_COUNT.get(), universe.size(), alive, hot, cold, avgAmplitude, fusionEvents.size()
        );
    }
    
    /**
     * Render the universe as ASCII map
     */
    public static String renderMap(int width, int height) {
        char[][] map = new char[height][width];
        
        // Initialize with empty space
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = '·';
            }
        }
        
        // Plot nodes
        for (SpatialNode node : universe) {
            int px = Math.max(0, Math.min(width - 1, node.x));
            int py = Math.max(0, Math.min(height - 1, node.y));
            
            // Character based on amplitude
            char c;
            if (node.a > 80) c = '★';
            else if (node.a > 50) c = '●';
            else if (node.a > 20) c = '○';
            else c = '·';
            
            map[py][px] = c;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("┌");
        for (int i = 0; i < width; i++) sb.append("─");
        sb.append("┐\n");
        
        for (int y = 0; y < height; y++) {
            sb.append("│");
            for (int x = 0; x < width; x++) {
                sb.append(map[y][x]);
            }
            sb.append("│\n");
        }
        
        sb.append("└");
        for (int i = 0; i < width; i++) sb.append("─");
        sb.append("┘");
        
        return sb.toString();
    }
    
    /**
     * Fusion Event - when two clusters collide
     */
    public static class FusionEvent {
        public final SpatialNode nodeA;
        public final SpatialNode nodeB;
        public final String suggestion;
        public final long timestamp;
        
        public FusionEvent(SpatialNode a, SpatialNode b, String suggestion) {
            this.nodeA = a;
            this.nodeB = b;
            this.suggestion = suggestion;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return String.format("[%s] + [%s] → %s", 
                nodeA.getId().substring(0, 8), 
                nodeB.getId().substring(0, 8), 
                suggestion);
        }
    }
}
