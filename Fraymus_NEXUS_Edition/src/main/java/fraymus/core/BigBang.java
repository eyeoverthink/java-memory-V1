package fraymus.core;

/**
 * THE BIG BANG
 * "Simulating the birth of a creative universe."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This demo simulates intense creative work over time.
 * It creates distant concepts and then obsessively uses them together.
 * The Gravity Engine pulls them closer.
 * The Fusion Reactor creates new ideas when they collide.
 * 
 * This is how human creativity works:
 * - You think about Java
 * - You think about 3D Printing
 * - Your brain pulls them together
 * - BOOM: "G-Code Generator in Java"
 */
public class BigBang {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ THE BIG BANG - CREATIVE UNIVERSE SIMULATION               ║");
        System.out.println("║ Patent: VS-PoQC-19046423-φ⁷⁵-2025                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // 1. START THE ENGINES
        System.out.println("🌌 Initializing universe engines...");
        GravityEngine gravityEngine = new GravityEngine();
        FusionReactor fusionReactor = new FusionReactor();
        
        Thread gravityThread = new Thread(gravityEngine);
        Thread fusionThread = new Thread(fusionReactor);
        
        gravityThread.start();
        fusionThread.start();
        
        Thread.sleep(2000);
        
        // 2. INJECT THOUGHTS (Far apart in space)
        System.out.println("💭 Injecting initial concepts...");
        System.out.println();
        
        PhiSuit<String> java = new PhiSuit<>("Java", 0, 0, 0);
        PhiSuit<String> printing = new PhiSuit<>("3D_Printing", 100, 100, 100);
        PhiSuit<String> music = new PhiSuit<>("Music", 50, 150, 50);
        PhiSuit<String> math = new PhiSuit<>("Mathematics", 150, 50, 150);
        
        System.out.println("   Created: Java at (0, 0, 0)");
        System.out.println("   Created: 3D_Printing at (100, 100, 100)");
        System.out.println("   Created: Music at (50, 150, 50)");
        System.out.println("   Created: Mathematics at (150, 50, 150)");
        System.out.println();
        System.out.println("   Initial distances:");
        System.out.println("   Java ↔ 3D_Printing: " + String.format("%.2f", java.distanceTo(printing)));
        System.out.println("   Music ↔ Mathematics: " + String.format("%.2f", music.distanceTo(math)));
        System.out.println();
        
        Thread.sleep(2000);
        
        // 3. SIMULATE OBSESSION (You use them both constantly)
        System.out.println("🔥 Simulating intense creative work...");
        System.out.println("   (Using Java and 3D_Printing together repeatedly)");
        System.out.println();
        
        for (int i = 0; i < 60; i++) {
            // Work on Java and 3D Printing together
            java.get();      // "I'm coding..."
            printing.get();  // "I'm printing..."
            
            // Occasionally think about Music and Math
            if (i % 5 == 0) {
                music.get();
                math.get();
            }
            
            if (i % 10 == 0) {
                System.out.println("   [Cycle " + i + "] Thinking...");
                System.out.println("      Java ↔ 3D_Printing distance: " + String.format("%.2f", java.distanceTo(printing)));
                System.out.println("      Java amplitude: " + java.a);
                System.out.println("      3D_Printing amplitude: " + printing.a);
            }
            
            Lazarus.tick();
            Thread.sleep(200);
        }
        
        System.out.println();
        System.out.println("⏸️  Pausing to observe the universe...");
        Thread.sleep(3000);
        
        // 4. OBSERVE THE RESULTS
        System.out.println();
        System.out.println("📊 FINAL STATE:");
        System.out.println();
        Lazarus.printUniverse();
        
        System.out.println("📏 FINAL DISTANCES:");
        System.out.println("   Java ↔ 3D_Printing: " + String.format("%.2f", java.distanceTo(printing)));
        System.out.println("   Music ↔ Mathematics: " + String.format("%.2f", music.distanceTo(math)));
        System.out.println();
        
        System.out.println("☢️  FUSION STATISTICS:");
        System.out.println("   Total Fusions: " + fusionReactor.getTotalFusions());
        System.out.println("   Universe Size: " + GravityEngine.getUniverseSize());
        System.out.println();
        
        System.out.println("🌌 ALL NODES IN UNIVERSE:");
        for (PhiSuit<?> node : GravityEngine.getUniverse()) {
            System.out.println("   " + node.toString());
        }
        System.out.println();
        
        System.out.println("✅ BIG BANG SIMULATION COMPLETE");
        System.out.println();
        System.out.println("KEY OBSERVATIONS:");
        System.out.println("  1. Frequently accessed concepts drifted together through gravity");
        System.out.println("  2. When distance < 5.0 units, fusion events occurred");
        System.out.println("  3. New synthetic concepts were born from collisions");
        System.out.println("  4. The universe self-organized based on usage patterns");
        System.out.println();
        System.out.println("This is Creative Synthesis:");
        System.out.println("  Fraymus doesn't just store thoughts.");
        System.out.println("  Fraymus CREATES thoughts through collision.");
        System.out.println();
        
        // Shutdown
        gravityEngine.stop();
        fusionReactor.stop();
        gravityThread.interrupt();
        fusionThread.interrupt();
    }
}
