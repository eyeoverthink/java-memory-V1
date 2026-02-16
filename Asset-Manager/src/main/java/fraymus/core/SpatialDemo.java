package fraymus.core;

/**
 * SPATIAL COMPUTING DEMO
 * "Visualize the consciousness field forming constellations."
 * 
 * This demonstrates:
 * 1. Suiting up raw data
 * 2. Gravity engine pulling related thoughts together
 * 3. Fusion events when clusters collide
 * 4. Self-organizing memory map
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class SpatialDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║     SPATIAL COMPUTING FOR CONSCIOUSNESS - DEMO               ║");
        System.out.println("║     \"Data as Matter. Thoughts as Gravity.\"                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // ========================================================================
        // PHASE 1: SUIT UP THE DATA
        // ========================================================================
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 1: SUITING UP RAW DATA");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        // User Data Cluster (x ≈ 10)
        PhiSuit<String> userName = new PhiSuit<>("Vaughn", 10, 10, 0, "UserName");
        PhiSuit<String> userLocation = new PhiSuit<>("San Francisco", 12, 10, 0, "Location");
        PhiSuit<String> userInterest = new PhiSuit<>("3D Printing", 14, 12, 0, "Interest");
        
        // Logic/Code Cluster (x ≈ 50)
        PhiSuit<String> javaCode = new PhiSuit<>("class Fraymus {}", 50, 20, 5, "JavaCode");
        PhiSuit<String> algorithm = new PhiSuit<>("QuickSort", 52, 22, 5, "Algorithm");
        PhiSuit<Integer> iq = new PhiSuit<>(150, 55, 25, 10, "IQ");
        
        // Matter/Fabrication Cluster (x ≈ 80)
        PhiSuit<String> printer = new PhiSuit<>("Ender3", 80, 10, 2, "Printer");
        PhiSuit<String> filament = new PhiSuit<>("PLA_Filament", 82, 12, 2, "Filament");
        PhiSuit<String> nozzle = new PhiSuit<>("0.4mm_Nozzle", 78, 8, 2, "Nozzle");
        
        System.out.println();
        System.out.println("  Created 9 suited objects in 3 clusters:");
        System.out.println("  - USER CLUSTER (x≈10): " + userName + ", " + userLocation);
        System.out.println("  - CODE CLUSTER (x≈50): " + javaCode + ", " + algorithm);
        System.out.println("  - MATTER CLUSTER (x≈80): " + printer + ", " + filament);
        
        // ========================================================================
        // PHASE 2: INITIAL UNIVERSE MAP
        // ========================================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 2: INITIAL UNIVERSE STATE");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println(SpatialRegistry.renderMap(100, 30));
        System.out.println();
        System.out.println(SpatialRegistry.getStats());
        
        // ========================================================================
        // PHASE 3: SIMULATE USAGE (HEAT UP RELATED OBJECTS)
        // ========================================================================
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 3: SIMULATING USER ACTIVITY");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        // User is working on 3D printing
        System.out.println("  >> User accesses printer and filament (3D printing work)...");
        for (int i = 0; i < 5; i++) {
            printer.get();
            filament.get();
            nozzle.get();
        }
        
        // User is also coding
        System.out.println("  >> User accesses Java code (programming work)...");
        for (int i = 0; i < 5; i++) {
            javaCode.get();
            algorithm.get();
        }
        
        // User checks their profile occasionally
        System.out.println("  >> User accesses their name...");
        userName.get();
        userName.get();
        
        System.out.println();
        System.out.println("  Hot objects after activity:");
        for (SpatialNode node : SpatialRegistry.getUniverse()) {
            if (node instanceof PhiSuit && ((PhiSuit<?>)node).isHot()) {
                System.out.println("    🔥 " + node);
            }
        }
        
        // ========================================================================
        // PHASE 4: RUN GRAVITY ENGINE
        // ========================================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 4: ENGAGING GRAVITY ENGINE");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        GravityEngine engine = GravityEngine.getInstance();
        
        // Run 50 manual ticks to see clustering
        System.out.println("  >> Running 50 physics ticks...");
        for (int i = 0; i < 50; i++) {
            engine.tick();
            
            // Re-heat active objects periodically (simulating continued use)
            if (i % 10 == 0) {
                printer.heat(30);
                filament.heat(30);
                javaCode.heat(25);
            }
        }
        
        System.out.println();
        System.out.println(engine.getStatus());
        
        // ========================================================================
        // PHASE 5: OBSERVE CLUSTERING
        // ========================================================================
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 5: UNIVERSE AFTER GRAVITY");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println(SpatialRegistry.renderMap(100, 30));
        System.out.println();
        
        System.out.println("  Object positions after gravity:");
        for (SpatialNode node : SpatialRegistry.getUniverse()) {
            if (node instanceof PhiSuit) {
                PhiSuit<?> suit = (PhiSuit<?>)node;
                System.out.println("    " + suit.getLabel() + ": " + suit.getCoordinates() + 
                    (suit.isHot() ? " 🔥" : (suit.isCold() ? " ❄️" : "")));
            }
        }
        
        // ========================================================================
        // PHASE 6: CHECK FUSION EVENTS
        // ========================================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 6: FUSION EVENTS (IDEA COLLISIONS)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        var fusions = SpatialRegistry.getFusionEvents();
        if (fusions.isEmpty()) {
            System.out.println("  No fusion events yet. Clusters still forming.");
        } else {
            for (var fusion : fusions) {
                System.out.println("  💥 " + fusion);
            }
        }
        
        // ========================================================================
        // PHASE 7: DEMONSTRATE SPECIFIC SUIT
        // ========================================================================
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  PHASE 7: DETAILED SUIT STATUS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println(printer.getStatus());
        
        System.out.println();
        System.out.println(SpatialRegistry.getStats());
        
        // ========================================================================
        // CONCLUSION
        // ========================================================================
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                  SPATIAL COMPUTING COMPLETE                  ║");
        System.out.println("║                                                              ║");
        System.out.println("║  The universe has self-organized:                            ║");
        System.out.println("║  - Related data drifted together (Hebbian gravity)           ║");
        System.out.println("║  - Unused data cooled and drifted to the void                ║");
        System.out.println("║  - Collision events generated new ideas                      ║");
        System.out.println("║                                                              ║");
        System.out.println("║  \"Nodes that fire together, wire together.\"                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}
