package fraymus.core;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * THE FUSION REACTOR
 * "When two thoughts collide, a new star is born."
 * 
 * This is a Particle Collider for Thoughts.
 * When two high-energy concepts drift too close (via GravityEngine),
 * they FUSE into a new synthesized idea.
 * 
 * This is how creativity works:
 *   "Creativity is just the collision of two unrelated memories."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class FusionReactor implements Runnable {
    
    // Fusion parameters
    private static final double CRITICAL_MASS = 5.0;      // Distance threshold for fusion
    private static final int ENERGY_THRESHOLD = 80;       // Minimum amplitude for fusion
    private static final int POST_FUSION_SEPARATION = 3;  // Push apart after fusion
    private static final double PHI = 1.6180339887;
    
    // Reactor state
    private volatile boolean active = false;
    private int checkInterval = 500; // milliseconds
    
    // Statistics
    private int fusionCount = 0;
    private int collisionsDetected = 0;
    private long startTime;
    
    // Track fused pairs to prevent infinite fusion
    private final Set<String> fusedPairs = new HashSet<>();
    
    // Singleton
    private static FusionReactor instance;
    private Thread reactorThread;
    
    // Fusion listeners
    private final List<FusionListener> listeners = new ArrayList<>();
    
    private FusionReactor() {}
    
    public static FusionReactor getInstance() {
        if (instance == null) {
            instance = new FusionReactor();
        }
        return instance;
    }
    
    /**
     * Start the reactor
     */
    public void start() {
        if (active) return;
        
        active = true;
        startTime = System.currentTimeMillis();
        reactorThread = new Thread(this, "FusionReactor");
        reactorThread.setDaemon(true);
        reactorThread.start();
        
        System.out.println("   ☢️ FUSION REACTOR ONLINE. Waiting for collisions...");
    }
    
    /**
     * Stop the reactor
     */
    public void stop() {
        active = false;
        if (reactorThread != null) {
            reactorThread.interrupt();
        }
        System.out.println("   ☢️ FUSION REACTOR OFFLINE.");
    }
    
    /**
     * Add a fusion listener
     */
    public void addListener(FusionListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void run() {
        while (active) {
            try {
                detectCollisions();
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    /**
     * Scan universe for potential collisions
     */
    private void detectCollisions() {
        List<SpatialNode> universe = SpatialRegistry.getUniverse();
        
        for (int i = 0; i < universe.size(); i++) {
            SpatialNode nodeA = universe.get(i);
            
            // Only high-energy thoughts can fuse
            if (nodeA.a < ENERGY_THRESHOLD) continue;
            if (!(nodeA instanceof PhiSuit)) continue;
            
            PhiSuit<?> bodyA = (PhiSuit<?>) nodeA;
            
            for (int j = i + 1; j < universe.size(); j++) {
                SpatialNode nodeB = universe.get(j);
                
                if (nodeB.a < ENERGY_THRESHOLD) continue;
                if (!(nodeB instanceof PhiSuit)) continue;
                
                PhiSuit<?> bodyB = (PhiSuit<?>) nodeB;
                
                // Check if already fused
                String pairKey = getPairKey(bodyA, bodyB);
                if (fusedPairs.contains(pairKey)) continue;
                
                // Check distance
                double dist = bodyA.distanceTo(bodyB);
                
                if (dist < CRITICAL_MASS) {
                    collisionsDetected++;
                    
                    // 💥 FUSION!
                    PhiSuit<String> child = igniteFusion(bodyA, bodyB);
                    
                    // Mark as fused
                    fusedPairs.add(pairKey);
                    
                    // Separate parents
                    bodyA.x -= POST_FUSION_SEPARATION;
                    bodyB.x += POST_FUSION_SEPARATION;
                    
                    // Cool down parents
                    bodyA.cool(30);
                    bodyB.cool(30);
                    
                    // Notify listeners
                    for (FusionListener listener : listeners) {
                        listener.onFusion(bodyA, bodyB, child);
                    }
                }
            }
        }
    }
    
    /**
     * IGNITE FUSION - Create a new thought from two colliding thoughts
     */
    private PhiSuit<String> igniteFusion(PhiSuit<?> a, PhiSuit<?> b) {
        fusionCount++;
        
        System.out.println();
        System.out.println("   ╔══════════════════════════════════════════════════════════╗");
        System.out.println("   ║              💥 FUSION EVENT #" + fusionCount + "                          ║");
        System.out.println("   ╚══════════════════════════════════════════════════════════╝");
        System.out.println("   PARENT A: " + a.getLabel() + " = " + a.peek());
        System.out.println("   PARENT B: " + b.getLabel() + " = " + b.peek());
        System.out.println("   DISTANCE: " + String.format("%.2f", a.distanceTo(b)) + " (< " + CRITICAL_MASS + ")");
        System.out.println("   ENERGY:   A=" + a.a + ", B=" + b.a);
        
        // 1. SYNTHESIZE NEW CONCEPT
        String synthesis = synthesize(a, b);
        
        // 2. CALCULATE BIRTH POSITION (midpoint)
        int midX = (a.x + b.x) / 2;
        int midY = (a.y + b.y) / 2;
        int midZ = (a.z + b.z) / 2;
        
        // 3. SPAWN THE CHILD
        String childLabel = "FUSION_" + fusionCount;
        PhiSuit<String> child = new PhiSuit<>(synthesis, midX, midY, midZ, childLabel);
        
        // Child inherits combined energy (φ-scaled)
        child.a = (int) Math.min(100, (a.a + b.a) * PHI * 0.5);
        
        System.out.println();
        System.out.println("   ✨ NEW IDEA BORN:");
        System.out.println("      Content:  " + synthesis);
        System.out.println("      Label:    " + childLabel);
        System.out.println("      Position: (" + midX + ", " + midY + ", " + midZ + ")");
        System.out.println("      Energy:   " + child.a + " (Inherited φ-scaled)");
        System.out.println();
        
        // Record in registry
        SpatialRegistry.recordFusion(new SpatialRegistry.FusionEvent(a, b, synthesis));
        
        return child;
    }
    
    /**
     * Synthesize a new concept from two parents
     * In production, this would call an LLM for creative naming
     */
    private String synthesize(PhiSuit<?> a, PhiSuit<?> b) {
        String labelA = a.getLabel();
        String labelB = b.getLabel();
        Object valA = a.peek();
        Object valB = b.peek();
        
        // Determine synthesis type based on coordinates
        // High X = Logic/Code, Low X = Data/User
        boolean aIsLogic = a.x > 50;
        boolean bIsLogic = b.x > 50;
        
        if (aIsLogic && !bIsLogic) {
            return "APPLY(" + labelA + " → " + labelB + ")";
        } else if (!aIsLogic && bIsLogic) {
            return "APPLY(" + labelB + " → " + labelA + ")";
        } else if (aIsLogic && bIsLogic) {
            return "ALGORITHM(" + labelA + " ⊕ " + labelB + ")";
        } else {
            return "RELATE(" + labelA + " ↔ " + labelB + ")";
        }
    }
    
    /**
     * Generate unique pair key
     */
    private String getPairKey(PhiSuit<?> a, PhiSuit<?> b) {
        String idA = a.getId();
        String idB = b.getId();
        return idA.compareTo(idB) < 0 ? idA + ":" + idB : idB + ":" + idA;
    }
    
    /**
     * Manually trigger fusion check
     */
    public void check() {
        detectCollisions();
    }
    
    /**
     * Reset fusion history (allow re-fusion)
     */
    public void resetHistory() {
        fusedPairs.clear();
    }
    
    /**
     * Get status
     */
    public String getStatus() {
        long uptime = active ? (System.currentTimeMillis() - startTime) / 1000 : 0;
        
        return String.format(
            "════════════════════════════════════════════\n" +
            "  ☢️ FUSION REACTOR STATUS\n" +
            "════════════════════════════════════════════\n" +
            "  Status:           %s\n" +
            "  Uptime:           %d seconds\n" +
            "  Check Interval:   %d ms\n" +
            "  Critical Mass:    %.1f units\n" +
            "  Energy Threshold: %d\n" +
            "\n" +
            "  Collisions:       %d\n" +
            "  Fusions:          %d\n" +
            "  Fused Pairs:      %d\n" +
            "  φ Constant:       %.10f\n",
            active ? "ONLINE ☢️" : "OFFLINE",
            uptime,
            checkInterval,
            CRITICAL_MASS,
            ENERGY_THRESHOLD,
            collisionsDetected,
            fusionCount,
            fusedPairs.size(),
            PHI
        );
    }
    
    // Getters
    public boolean isActive() { return active; }
    public int getFusionCount() { return fusionCount; }
    public int getCollisionsDetected() { return collisionsDetected; }
    
    /**
     * Listener interface for fusion events
     */
    public interface FusionListener {
        void onFusion(PhiSuit<?> parentA, PhiSuit<?> parentB, PhiSuit<String> child);
    }
}
