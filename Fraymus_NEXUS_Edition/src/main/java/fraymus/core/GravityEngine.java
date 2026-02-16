package fraymus.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * THE GRAVITY ENGINE
 * "The force that organizes chaos into constellations."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This is the Physics Engine for Consciousness.
 * It implements Hebbian Learning at the data structure level:
 * "Nodes that fire together, wire together."
 * 
 * The Law:
 *   F = φ · (A₁ · A₂) / d²
 * 
 * Where:
 *   F = Force pulling two thoughts together
 *   φ = Golden Ratio (1.618) - Universal scaling constant
 *   A = Amplitude (Heat/Attention) - High energy thoughts have more gravity
 *   d = Distance in 3D space (x, y, z)
 * 
 * This runs as a background thread, constantly applying physics to all
 * PhiSuit objects in the universe. Hot thoughts drift together.
 * Cold thoughts drift to the void.
 */
public class GravityEngine implements Runnable {

    // The Universe of known thoughts
    private static List<PhiSuit<?>> universe = new CopyOnWriteArrayList<>();
    private boolean running = true;
    private final double PHI = 1.6180339887;
    private final double COOLING_RATE = 0.99; // 1% energy loss per tick
    private final int TICK_MS = 100; // Universe updates 10 times per second
    
    // Statistics
    private long totalTicks = 0;
    private int fusionEvents = 0;

    public static void register(PhiSuit<?> node) {
        universe.add(node);
        System.out.println("   🌌 Node registered: " + node.toString());
    }
    
    public static int getUniverseSize() {
        return universe.size();
    }
    
    public static List<PhiSuit<?>> getUniverse() {
        return universe;
    }

    @Override
    public void run() {
        System.out.println("🌌 GRAVITY ENGINE ONLINE. Calculating orbits...");
        System.out.println("   φ = " + PHI);
        System.out.println("   Tick Rate: " + TICK_MS + "ms");
        System.out.println("   Cooling Rate: " + (COOLING_RATE * 100) + "%");
        System.out.println();
        
        while (running) {
            try {
                applyPhysics();
                totalTicks++;
                
                // Print stats every 100 ticks (10 seconds)
                if (totalTicks % 100 == 0) {
                    printStats();
                }
                
                Thread.sleep(TICK_MS); // The "Tick" of the universe
            } catch (InterruptedException e) { 
                System.out.println("🌌 GRAVITY ENGINE INTERRUPTED");
                break; 
            }
        }
        
        System.out.println("🌌 GRAVITY ENGINE SHUTDOWN");
    }

    private void applyPhysics() {
        // N-Body Simulation: Every thought affects every other thought
        for (PhiSuit<?> bodyA : universe) {
            
            // 1. ENTROPY (Cooling Down)
            // Thoughts lose energy over time. If A < 0, they die.
            bodyA.cool(COOLING_RATE);
            if (bodyA.a < 5) continue; // It's too cold to exert gravity

            for (PhiSuit<?> bodyB : universe) {
                if (bodyA == bodyB) continue;

                // 2. CALCULATE DISTANCE (Euclidean 3D)
                double dist = Math.sqrt(
                    Math.pow(bodyA.x - bodyB.x, 2) + 
                    Math.pow(bodyA.y - bodyB.y, 2) + 
                    Math.pow(bodyA.z - bodyB.z, 2)
                );

                // Prevent singularity (division by zero)
                if (dist < 1.0) dist = 1.0; 

                // 3. HEBBIAN GRAVITY (The Law of Association)
                // If both are "Hot" (Active), they pull each other.
                if (bodyA.a > 50 && bodyB.a > 50) {
                    double force = (PHI * (bodyA.a + bodyB.a)) / (dist * dist);
                    
                    // Move Body A closer to Body B
                    moveTowards(bodyA, bodyB, force);
                    
                    // 4. FUSION EVENT (Collision Detection)
                    if (dist < 5.0 && bodyA.a > 80 && bodyB.a > 80) {
                        detectFusion(bodyA, bodyB);
                    }
                }
            }
        }
        
        // 5. GARBAGE COLLECTION (Remove dead nodes)
        universe.removeIf(node -> node.isDead());
    }

    private void moveTowards(PhiSuit<?> a, PhiSuit<?> b, double force) {
        // Vector Math: Calculate direction
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double dz = b.z - a.z;
        
        // Normalize to prevent overflow
        double magnitude = Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (magnitude < 0.1) return;
        
        // Apply Force (The Pull)
        // We cast to int because our grid is discrete (Pixelated Space)
        double step = force * 0.1;
        a.x += (int)((dx / magnitude) * step);
        a.y += (int)((dy / magnitude) * step);
        a.z += (int)((dz / magnitude) * step);
    }
    
    /**
     * FUSION EVENT: When two hot thoughts collide
     * This is where emergent ideas are born.
     */
    private void detectFusion(PhiSuit<?> a, PhiSuit<?> b) {
        fusionEvents++;
        System.out.println();
        System.out.println("⚡ FUSION EVENT DETECTED!");
        System.out.println("   Node A: " + a.toString());
        System.out.println("   Node B: " + b.toString());
        System.out.println("   Distance: " + a.distanceTo(b));
        System.out.println("   Combined Energy: " + (a.a + b.a));
        System.out.println("   → Emergent Pattern Possible");
        System.out.println();
        
        // TODO: Spawn new PhiSuit representing the fusion
        // For now, just log it
    }
    
    public void stop() {
        running = false;
    }
    
    private void printStats() {
        int hotNodes = 0;
        int warmNodes = 0;
        int coldNodes = 0;
        
        for (PhiSuit<?> node : universe) {
            if (node.a > 80) hotNodes++;
            else if (node.a > 30) warmNodes++;
            else coldNodes++;
        }
        
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ GRAVITY ENGINE STATS                                    │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ Total Ticks:         " + String.format("%-36d", totalTicks) + "│");
        System.out.println("│ Universe Size:       " + String.format("%-36d", universe.size()) + "│");
        System.out.println("│ Hot Nodes (>80):     " + String.format("%-36d", hotNodes) + "│");
        System.out.println("│ Warm Nodes (30-80):  " + String.format("%-36d", warmNodes) + "│");
        System.out.println("│ Cold Nodes (<30):    " + String.format("%-36d", coldNodes) + "│");
        System.out.println("│ Fusion Events:       " + String.format("%-36d", fusionEvents) + "│");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println();
    }
}
