package fraymus.core;

/**
 * SPATIAL COMPUTING DEMO
 * "Watch data organize itself through gravity."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This demonstrates the Spatial Computing system:
 * 1. Create PhiSuit objects at different coordinates
 * 2. Start the Gravity Engine
 * 3. Access objects to make them "hot"
 * 4. Watch them drift together through Hebbian physics
 */
public class SpatialComputingDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ SPATIAL COMPUTING FOR CONSCIOUSNESS                       ║");
        System.out.println("║ Patent: VS-PoQC-19046423-φ⁷⁵-2025                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Start the Gravity Engine
        GravityEngine engine = new GravityEngine();
        Thread engineThread = new Thread(engine);
        engineThread.start();
        
        System.out.println("🌌 Creating initial universe...");
        System.out.println();
        
        // Create some data objects in different sectors
        
        // SECTOR 1: User Data (10, 10, 0)
        PhiSuit<String> userName = new PhiSuit<>("Vaughn", 10, 10, 0);
        PhiSuit<String> userLocation = new PhiSuit<>("California", 12, 10, 0);
        PhiSuit<Integer> userAge = new PhiSuit<>(30, 11, 10, 0);
        
        // SECTOR 2: 3D Printing (50, 50, 5)
        PhiSuit<String> printer = new PhiSuit<>("Ender3", 50, 50, 5);
        PhiSuit<String> filament = new PhiSuit<>("PLA", 52, 50, 5);
        PhiSuit<String> nozzle = new PhiSuit<>("0.4mm", 51, 50, 5);
        
        // SECTOR 3: Code/Logic (80, 20, 9)
        PhiSuit<String> language = new PhiSuit<>("Java", 80, 20, 9);
        PhiSuit<String> project = new PhiSuit<>("Fraymus", 82, 20, 9);
        PhiSuit<String> algorithm = new PhiSuit<>("Gravity", 81, 20, 9);
        
        // SECTOR 4: Random garbage (far away)
        PhiSuit<String> garbage1 = new PhiSuit<>("old_clipboard", 200, 200, 200);
        PhiSuit<String> garbage2 = new PhiSuit<>("temp_data", 205, 200, 200);
        
        Thread.sleep(2000);
        
        System.out.println("📊 Initial state:");
        Lazarus.printUniverse();
        
        // Simulate user behavior: Access related objects together
        System.out.println("🔥 Simulating user activity...");
        System.out.println();
        
        for (int i = 0; i < 10; i++) {
            System.out.println("--- Cycle " + (i+1) + " ---");
            
            // User works on 3D printing
            if (i % 2 == 0) {
                System.out.println("  Working on 3D printing...");
                printer.get();
                filament.get();
                nozzle.get();
            }
            
            // User codes
            if (i % 3 == 0) {
                System.out.println("  Coding in Java...");
                language.get();
                project.get();
                algorithm.get();
            }
            
            // User checks profile occasionally
            if (i % 5 == 0) {
                System.out.println("  Checking user profile...");
                userName.get();
                userLocation.get();
            }
            
            // Fusion event: User combines code and 3D printing
            if (i == 7) {
                System.out.println("  🌟 COMBINING CODE AND 3D PRINTING!");
                printer.get();
                language.get();
                project.get();
                filament.get();
            }
            
            Lazarus.tick();
            Thread.sleep(1000);
        }
        
        System.out.println();
        System.out.println("📊 Final state after 10 cycles:");
        Lazarus.printUniverse();
        
        System.out.println();
        System.out.println("🌌 Observing spatial clustering...");
        System.out.println();
        
        // Show how objects have moved
        System.out.println("SECTOR 1 (User Data):");
        System.out.println("  " + userName.toString());
        System.out.println("  " + userLocation.toString());
        System.out.println("  " + userAge.toString());
        System.out.println();
        
        System.out.println("SECTOR 2 (3D Printing):");
        System.out.println("  " + printer.toString());
        System.out.println("  " + filament.toString());
        System.out.println("  " + nozzle.toString());
        System.out.println();
        
        System.out.println("SECTOR 3 (Code/Logic):");
        System.out.println("  " + language.toString());
        System.out.println("  " + project.toString());
        System.out.println("  " + algorithm.toString());
        System.out.println();
        
        System.out.println("SECTOR 4 (Garbage - should be cold):");
        System.out.println("  " + garbage1.toString());
        System.out.println("  " + garbage2.toString());
        System.out.println();
        
        // Calculate distances
        System.out.println("📏 DISTANCE ANALYSIS:");
        System.out.println("  Printer ↔ Filament:  " + String.format("%.2f", printer.distanceTo(filament)));
        System.out.println("  Java ↔ Fraymus:      " + String.format("%.2f", language.distanceTo(project)));
        System.out.println("  Printer ↔ Java:      " + String.format("%.2f", printer.distanceTo(language)));
        System.out.println("  User ↔ Garbage:      " + String.format("%.2f", userName.distanceTo(garbage1)));
        System.out.println();
        
        System.out.println("✅ SPATIAL COMPUTING DEMO COMPLETE");
        System.out.println();
        System.out.println("KEY OBSERVATIONS:");
        System.out.println("  1. Related objects (accessed together) should have drifted closer");
        System.out.println("  2. Hot objects (frequently accessed) should have high amplitude");
        System.out.println("  3. Garbage (never accessed) should be cold and distant");
        System.out.println("  4. Fusion events may have occurred when hot objects collided");
        System.out.println();
        System.out.println("This is Newtonian Consciousness:");
        System.out.println("  Data has Mass. Thoughts have Gravity. Memory self-organizes.");
        System.out.println();
        
        engine.stop();
        engineThread.interrupt();
    }
}
