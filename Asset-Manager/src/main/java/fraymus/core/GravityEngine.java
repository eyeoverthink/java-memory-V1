package fraymus.core;

import java.util.List;
import java.util.ArrayList;

/**
 * THE GRAVITY ENGINE
 * "The force that organizes chaos into constellations."
 * 
 * This is the Hebbian Physics Engine. It runs as a background thread and:
 * 1. Applies ENTROPY - Thoughts lose energy over time
 * 2. Applies GRAVITY - Hot thoughts pull each other closer
 * 3. Detects COLLISIONS - When clusters merge, trigger fusion events
 * 4. Performs CLEANUP - Dead nodes are removed
 * 
 * The Law of Association (Hebbian Gravity):
 *   F = φ × (A₁ × A₂) / d²
 * 
 * Where:
 *   F = Force pulling two thoughts together
 *   φ = Golden Ratio (1.618)
 *   A = Amplitude (Heat/Attention)
 *   d = Distance in 3D space
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class GravityEngine implements Runnable {
    
    // Physics constants
    private static final double PHI = 1.6180339887;
    private static final double ENTROPY_RATE = 0.99;      // 1% decay per tick
    private static final double GRAVITY_SCALE = 0.1;      // Force multiplier
    private static final double COLLISION_THRESHOLD = 5.0; // Distance for fusion
    private static final int HOT_THRESHOLD = 50;          // Amplitude for gravity
    private static final int COLD_THRESHOLD = 5;          // Below this = no gravity
    
    // Engine state
    private volatile boolean running = false;
    private int tickRate = 100; // milliseconds between ticks
    private long tickCount = 0;
    
    // Statistics
    private int gravitationalPulls = 0;
    private int fusionEvents = 0;
    private int nodesRemoved = 0;
    
    // Singleton instance
    private static GravityEngine instance;
    private Thread engineThread;
    
    private GravityEngine() {}
    
    public static GravityEngine getInstance() {
        if (instance == null) {
            instance = new GravityEngine();
        }
        return instance;
    }
    
    /**
     * Start the engine
     */
    public void start() {
        if (running) return;
        
        running = true;
        engineThread = new Thread(this, "GravityEngine");
        engineThread.setDaemon(true);
        engineThread.start();
        
        System.out.println("   🌌 GRAVITY ENGINE ONLINE. Calculating orbits...");
    }
    
    /**
     * Stop the engine
     */
    public void stop() {
        running = false;
        if (engineThread != null) {
            engineThread.interrupt();
        }
        System.out.println("   🌌 GRAVITY ENGINE OFFLINE.");
    }
    
    /**
     * Set tick rate (ms between physics updates)
     */
    public void setTickRate(int ms) {
        this.tickRate = Math.max(10, ms);
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                applyPhysics();
                SpatialRegistry.tick(); // Advance universe time
                tickCount++;
                Thread.sleep(tickRate);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    /**
     * Apply physics to all nodes in the universe
     */
    private void applyPhysics() {
        List<SpatialNode> universe = SpatialRegistry.getUniverse();
        List<SpatialNode> toRemove = new ArrayList<>();
        
        for (SpatialNode nodeA : universe) {
            // 1. ENTROPY - Everything cools down
            nodeA.entropy(ENTROPY_RATE);
            
            // 2. Check for death
            if (nodeA.isDead()) {
                toRemove.add(nodeA);
                continue;
            }
            
            // 3. Skip cold nodes (no gravity)
            if (nodeA.a < COLD_THRESHOLD) continue;
            
            // 4. N-BODY GRAVITY
            for (SpatialNode nodeB : universe) {
                if (nodeA == nodeB) continue;
                if (nodeB.a < COLD_THRESHOLD) continue;
                
                // Calculate distance
                double dist = nodeA.distanceTo(nodeB);
                if (dist < 1.0) dist = 1.0; // Prevent singularity
                
                // Check for collision (fusion)
                if (dist < COLLISION_THRESHOLD && nodeA.a > HOT_THRESHOLD && nodeB.a > HOT_THRESHOLD) {
                    triggerFusion(nodeA, nodeB);
                }
                
                // HEBBIAN GRAVITY - Hot nodes pull each other
                if (nodeA.a > HOT_THRESHOLD && nodeB.a > HOT_THRESHOLD) {
                    double force = (PHI * nodeA.a * nodeB.a) / (dist * dist);
                    force *= GRAVITY_SCALE;
                    
                    nodeA.moveTowards(nodeB, force);
                    gravitationalPulls++;
                }
            }
        }
        
        // Cleanup dead nodes
        for (SpatialNode dead : toRemove) {
            SpatialRegistry.unregister(dead);
            nodesRemoved++;
        }
    }
    
    /**
     * Trigger a fusion event when two hot nodes collide
     */
    private void triggerFusion(SpatialNode nodeA, SpatialNode nodeB) {
        // Generate fusion suggestion based on node types
        String suggestion = generateFusionSuggestion(nodeA, nodeB);
        
        SpatialRegistry.FusionEvent event = new SpatialRegistry.FusionEvent(nodeA, nodeB, suggestion);
        SpatialRegistry.recordFusion(event);
        
        // Both nodes cool down after fusion
        nodeA.cool(20);
        nodeB.cool(20);
        
        fusionEvents++;
    }
    
    /**
     * Generate a suggestion when two thoughts collide
     */
    private String generateFusionSuggestion(SpatialNode a, SpatialNode b) {
        String labelA = "unknown";
        String labelB = "unknown";
        
        if (a instanceof PhiSuit) {
            labelA = ((PhiSuit<?>)a).getLabel();
        }
        if (b instanceof PhiSuit) {
            labelB = ((PhiSuit<?>)b).getLabel();
        }
        
        // Determine collision type based on coordinates
        // x > 50 = Logic/Math, x < 30 = Data/User
        boolean aIsLogic = a.x > 50;
        boolean bIsLogic = b.x > 50;
        
        if (aIsLogic && !bIsLogic) {
            return "APPLY " + labelA + " to " + labelB;
        } else if (!aIsLogic && bIsLogic) {
            return "APPLY " + labelB + " to " + labelA;
        } else if (aIsLogic && bIsLogic) {
            return "COMBINE algorithms: " + labelA + " + " + labelB;
        } else {
            return "RELATE data: " + labelA + " ↔ " + labelB;
        }
    }
    
    /**
     * Run one physics tick manually (for testing)
     */
    public void tick() {
        applyPhysics();
        SpatialRegistry.tick();
        tickCount++;
    }
    
    /**
     * Get engine status
     */
    public String getStatus() {
        return String.format(
            "════════════════════════════════════════════\n" +
            "  🌌 GRAVITY ENGINE STATUS\n" +
            "════════════════════════════════════════════\n" +
            "  Status:         %s\n" +
            "  Tick Rate:      %d ms\n" +
            "  Total Ticks:    %d\n" +
            "  Gravity Pulls:  %d\n" +
            "  Fusion Events:  %d\n" +
            "  Nodes Removed:  %d\n" +
            "  φ Constant:     %.10f\n",
            running ? "ONLINE 🟢" : "OFFLINE 🔴",
            tickRate,
            tickCount,
            gravitationalPulls,
            fusionEvents,
            nodesRemoved,
            PHI
        );
    }
    
    // Getters
    public boolean isRunning() { return running; }
    public long getTickCount() { return tickCount; }
    public int getGravitationalPulls() { return gravitationalPulls; }
    public int getFusionEvents() { return fusionEvents; }
}
