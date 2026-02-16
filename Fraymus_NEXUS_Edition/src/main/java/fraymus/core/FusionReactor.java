package fraymus.core;

import java.util.List;

/**
 * THE FUSION REACTOR
 * "When two thoughts collide, a new star is born."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This is the Creative Synthesis Engine.
 * It watches the consciousness field for high-energy collisions.
 * When two hot thoughts get too close, they FUSE into a new idea.
 * 
 * This is how creativity works:
 * - "3D Printing" + "Java Code" = "G-Code Generator"
 * - "Music" + "Mathematics" = "Harmonic Analysis"
 * - "Biology" + "Computing" = "Neural Networks"
 * 
 * The reactor doesn't just store data. It CREATES data.
 */
public class FusionReactor implements Runnable {

    private static final double CRITICAL_MASS = 5.0; // The Fusion Threshold
    private static final int MIN_AMPLITUDE = 80; // Only hot thoughts can fuse
    private boolean active = true;
    
    // Statistics
    private int totalFusions = 0;
    private long lastFusionTime = 0;

    @Override
    public void run() {
        System.out.println("☢️  FUSION REACTOR ONLINE. Waiting for collisions...");
        System.out.println("   Critical Mass: " + CRITICAL_MASS + " units");
        System.out.println("   Min Amplitude: " + MIN_AMPLITUDE);
        System.out.println();
        
        while (active) {
            try {
                detectCollisions();
                Thread.sleep(500); // Check every half-second
            } catch (InterruptedException e) { 
                System.out.println("☢️  FUSION REACTOR INTERRUPTED");
                break; 
            }
        }
        
        System.out.println("☢️  FUSION REACTOR SHUTDOWN");
        System.out.println("   Total Fusions: " + totalFusions);
    }

    private void detectCollisions() {
        List<PhiSuit<?>> universe = GravityEngine.getUniverse(); // Access the map

        for (int i = 0; i < universe.size(); i++) {
            PhiSuit<?> bodyA = universe.get(i);
            
            // Only high-energy thoughts can fuse (Amplitude > MIN_AMPLITUDE)
            if (bodyA.a < MIN_AMPLITUDE) continue; 

            for (int j = i + 1; j < universe.size(); j++) {
                PhiSuit<?> bodyB = universe.get(j);
                if (bodyB.a < MIN_AMPLITUDE) continue;

                // CHECK DISTANCE
                double dist = bodyA.distanceTo(bodyB);

                if (dist < CRITICAL_MASS) {
                    // 💥 COLLISION DETECTED
                    igniteFusion(bodyA, bodyB, dist);
                    
                    // Push them apart slightly so they don't fuse infinitely
                    bodyA.x -= 3; 
                    bodyB.x += 3;
                    bodyA.y -= 2;
                    bodyB.y += 2;
                }
            }
        }
    }

    private void igniteFusion(PhiSuit<?> a, PhiSuit<?> b, double distance) {
        totalFusions++;
        lastFusionTime = System.currentTimeMillis();
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("💥 FUSION EVENT #" + totalFusions);
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   PARENT 1: " + a.peek());
        System.out.println("   Position: (" + a.x + ", " + a.y + ", " + a.z + ")");
        System.out.println("   Amplitude: " + a.a);
        System.out.println();
        System.out.println("   PARENT 2: " + b.peek());
        System.out.println("   Position: (" + b.x + ", " + b.y + ", " + b.z + ")");
        System.out.println("   Amplitude: " + b.a);
        System.out.println();
        System.out.println("   Distance: " + String.format("%.2f", distance) + " units");
        System.out.println("   Combined Energy: " + (a.a + b.a));
        System.out.println();

        // 1. SYNTHESIZE NEW IDEA
        // In a real system, we'd use the LLM here to name the idea.
        // For now, we mathematically combine them.
        String newConcept = synthesizeConcept(a.peek().toString(), b.peek().toString());

        // 2. SPAWN THE CHILD
        // It appears exactly between the parents
        int midX = (a.x + b.x) / 2;
        int midY = (a.y + b.y) / 2;
        int midZ = (a.z + b.z) / 2;

        PhiSuit<String> child = new PhiSuit<>(newConcept, midX, midY, midZ);
        
        // Child inherits combined energy
        child.a = Math.min(200, (a.a + b.a) / 2);
        
        // 3. REGISTER THE STAR
        GravityEngine.register(child);
        
        System.out.println("   ✨ NEW IDEA BORN:");
        System.out.println("   → " + newConcept);
        System.out.println();
        System.out.println("   📍 LOCATION: (" + midX + ", " + midY + ", " + midZ + ")");
        System.out.println("   ⚡ ENERGY: " + child.a + " (Hot!)");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        
        // Cool down parents slightly (they just spent energy)
        a.a = (int)(a.a * 0.8);
        b.a = (int)(b.a * 0.8);
    }
    
    /**
     * Synthesize a new concept from two parent concepts
     * This is where the LLM would be called in production
     */
    private String synthesizeConcept(String conceptA, String conceptB) {
        // Simple synthesis for now
        // In production, this would call Ollama with:
        // "Given these two concepts: [A] and [B], what emerges when they combine?"
        
        // Remove common prefixes
        conceptA = conceptA.replace("SYNTHESIS: ", "").trim();
        conceptB = conceptB.replace("SYNTHESIS: ", "").trim();
        
        return "SYNTHESIS: " + conceptA + " ⊕ " + conceptB;
    }
    
    public void stop() {
        active = false;
    }
    
    public int getTotalFusions() {
        return totalFusions;
    }
    
    public long getLastFusionTime() {
        return lastFusionTime;
    }
}
