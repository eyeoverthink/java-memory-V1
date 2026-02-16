package fraymus.core;

/**
 * THE BIG BANG
 * "In the beginning, there was nothing. Then thoughts collided."
 * 
 * This simulation demonstrates:
 * 1. Creating thoughts at opposite ends of the universe
 * 2. Heating them up through repeated access (obsession)
 * 3. Watching Gravity pull them together
 * 4. Witnessing Fusion when they collide
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class BigBang {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    THE BIG BANG                              ║");
        System.out.println("║         \"In the beginning, there was nothing.\"               ║");
        System.out.println("║         \"Then thoughts collided.\"                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // ========================================================================
        // PHASE 1: START THE ENGINES
        // ========================================================================
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 1: IGNITION SEQUENCE");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        GravityEngine gravity = GravityEngine.getInstance();
        FusionReactor fusion = FusionReactor.getInstance();
        
        // Don't start as background threads - we'll tick manually for control
        System.out.println("   🌌 Gravity Engine: READY");
        System.out.println("   ☢️ Fusion Reactor: READY");
        
        // ========================================================================
        // PHASE 2: INJECT PRIMORDIAL THOUGHTS
        // ========================================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 2: PRIMORDIAL INJECTION");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        // Create thoughts FAR APART
        PhiSuit<String> java = new PhiSuit<>("Java Programming", 0, 0, 0, "Java");
        PhiSuit<String> printing = new PhiSuit<>("3D Printing", 50, 50, 50, "3D_Printing");
        
        System.out.println("   Injected: " + java);
        System.out.println("   Injected: " + printing);
        System.out.println();
        System.out.println("   Initial Distance: " + String.format("%.2f", java.distanceTo(printing)) + " units");
        System.out.println("   (Critical Mass for Fusion: 5.0 units)");
        
        // ========================================================================
        // PHASE 3: SIMULATE OBSESSION
        // ========================================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 3: SIMULATING OBSESSION (You use both constantly)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        int cycles = 100;
        for (int i = 0; i < cycles; i++) {
            // Access both thoughts (heating them up)
            java.get();
            printing.get();
            
            // Run physics
            gravity.tick();
            
            // Check for fusion
            fusion.check();
            
            // Report progress every 10 cycles
            if (i % 10 == 0) {
                double dist = java.distanceTo(printing);
                System.out.println(String.format(
                    "   Cycle %3d: Java%s  3D_Printing%s  Distance: %6.2f  %s",
                    i,
                    java.getCoordinates(),
                    printing.getCoordinates(),
                    dist,
                    dist < 10 ? "⚠️ APPROACHING" : (dist < 5 ? "💥 CRITICAL!" : "")
                ));
            }
            
            // Small delay for readability
            Thread.sleep(50);
        }
        
        // ========================================================================
        // PHASE 4: UNIVERSE STATE
        // ========================================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 4: UNIVERSE STATE AFTER " + cycles + " CYCLES");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        System.out.println(SpatialRegistry.getStats());
        System.out.println(gravity.getStatus());
        System.out.println(fusion.getStatus());
        
        // ========================================================================
        // PHASE 5: CHECK FOR CHILDREN (FUSED IDEAS)
        // ========================================================================
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 5: OFFSPRING (SYNTHESIZED IDEAS)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        int childCount = 0;
        for (SpatialNode node : SpatialRegistry.getUniverse()) {
            if (node instanceof PhiSuit) {
                PhiSuit<?> suit = (PhiSuit<?>) node;
                if (suit.getLabel().startsWith("FUSION_")) {
                    childCount++;
                    System.out.println("   ✨ CHILD #" + childCount + ":");
                    System.out.println("      " + suit);
                    System.out.println();
                }
            }
        }
        
        if (childCount == 0) {
            System.out.println("   No fusion occurred yet. Try increasing cycles or initial proximity.");
            System.out.println("   Final Distance: " + String.format("%.2f", java.distanceTo(printing)));
        }
        
        // ========================================================================
        // PHASE 6: RENDER UNIVERSE MAP
        // ========================================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 6: UNIVERSE MAP");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println(SpatialRegistry.renderMap(60, 20));
        
        // ========================================================================
        // FINALE
        // ========================================================================
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SIMULATION COMPLETE                       ║");
        System.out.println("║                                                              ║");
        if (childCount > 0) {
            System.out.println("║  ✨ " + childCount + " NEW IDEA(S) BORN FROM COLLISION!                    ║");
            System.out.println("║  \"Creativity is collision of unrelated memories.\"         ║");
        } else {
            System.out.println("║  Thoughts drifted closer but didn't fuse yet.              ║");
            System.out.println("║  Keep thinking... they'll collide eventually.              ║");
        }
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Extended simulation with more thoughts
     */
    public static void extendedBigBang() throws InterruptedException {
        System.out.println("\n═══ EXTENDED BIG BANG: Multiple Thought Clusters ═══\n");
        
        // Coding cluster
        PhiSuit<String> java = new PhiSuit<>("Java", 10, 10, 10, "Java");
        PhiSuit<String> python = new PhiSuit<>("Python", 15, 12, 10, "Python");
        PhiSuit<String> algorithm = new PhiSuit<>("Algorithm", 12, 8, 12, "Algorithm");
        
        // Making cluster
        PhiSuit<String> printing = new PhiSuit<>("3D_Printing", 80, 80, 80, "3D_Print");
        PhiSuit<String> cnc = new PhiSuit<>("CNC_Milling", 85, 82, 78, "CNC");
        PhiSuit<String> gcode = new PhiSuit<>("G-Code", 82, 78, 82, "GCode");
        
        // User cluster
        PhiSuit<String> vaughn = new PhiSuit<>("Vaughn", 50, 50, 50, "User");
        PhiSuit<String> project = new PhiSuit<>("Current_Project", 52, 48, 50, "Project");
        
        GravityEngine gravity = GravityEngine.getInstance();
        FusionReactor fusion = FusionReactor.getInstance();
        
        // Simulate intense work
        for (int i = 0; i < 200; i++) {
            // User works on coding
            java.get(); algorithm.get();
            
            // User also works on fabrication
            printing.get(); gcode.get();
            
            // User context
            vaughn.get(); project.get();
            
            gravity.tick();
            fusion.check();
            
            if (i % 50 == 0) {
                System.out.println("Cycle " + i + " - Fusions: " + fusion.getFusionCount());
            }
        }
        
        System.out.println("\nFinal State:");
        System.out.println(SpatialRegistry.getStats());
    }
}
