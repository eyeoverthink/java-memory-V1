package fraymus.physics;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/**
 * ⚛️ HYPER-PHYSICS CONSOLE
 * "The Controller of Reality"
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * REVOLUTIONARY INTERFACE:
 * You don't type commands. You inject CONCEPTS.
 * 
 * If you inject a concept that contradicts the Rigid Body's internal mass,
 * it causes a high-velocity collision in 17-dimensional space.
 * 
 * COMMANDS:
 * - Type any concept → Apply as force
 * - 'dump' → Show tracking data
 * - 'export' → Export mesh as OBJ
 * - 'stats' → Show statistics
 * - 'spawn <concept>' → Create new body
 * - 'gravity' → Toggle gravity simulation
 * - 'reset' → Reset all bodies
 * - 'quit' → Exit
 * 
 * This is Layer 8 of the Fraymus Stack.
 * This is where you control physics with language.
 */
public class PhysicsConsole {
    
    private static List<HyperRigidBody> bodies = new ArrayList<>();
    private static boolean gravityEnabled = false;
    private static boolean running = true;
    private static long startTime;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          ⚛️ HYPER-PHYSICS ENGINE ONLINE                       ║");
        System.out.println("║          Layer 8: Data Becomes Physical                       ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        startTime = System.currentTimeMillis();
        
        // 1. CREATE THE INITIAL BODY
        // We create a Rigid Body made of "PURE_LOGIC". It is heavy.
        System.out.println("⚛️ SPAWNING INITIAL BODY...");
        HyperRigidBody body = new HyperRigidBody("PURE_LOGIC", new Mesh(100));
        bodies.add(body);
        System.out.println();
        
        // 2. START SIMULATION LOOP
        Thread simulationThread = new Thread(() -> {
            while (running) {
                long now = System.currentTimeMillis();
                double dt = 0.016; // 60 FPS tick
                
                // Update all bodies
                for (HyperRigidBody b : bodies) {
                    b.update(dt);
                }
                
                // Apply gravity between bodies if enabled
                if (gravityEnabled && bodies.size() > 1) {
                    for (int i = 0; i < bodies.size(); i++) {
                        for (int j = i + 1; j < bodies.size(); j++) {
                            HyperRigidBody b1 = bodies.get(i);
                            HyperRigidBody b2 = bodies.get(j);
                            
                            b1.applyGravity(b2);
                            b2.applyGravity(b1);
                            
                            // Check for collisions
                            if (b1.isCollidingWith(b2)) {
                                b1.resolveCollision(b2);
                            }
                        }
                    }
                }
                
                // Update meshes
                for (HyperRigidBody b : bodies) {
                    b.geometry.update(now);
                }
                
                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "SimulationThread");
        simulationThread.setDaemon(true);
        simulationThread.start();
        
        System.out.println("✅ SIMULATION RUNNING (60 FPS)");
        System.out.println();
        
        // 3. INTERACTIVE CONSOLE
        printHelp();
        
        Scanner scanner = new Scanner(System.in);
        
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            processCommand(input);
        }
        
        scanner.close();
        System.out.println("\n⚛️ PHYSICS ENGINE TERMINATED");
    }
    
    /**
     * Process user command
     */
    private static void processCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        
        switch (command) {
            case "help":
                printHelp();
                break;
            
            case "dump":
                dumpTracking();
                break;
            
            case "export":
                exportMesh();
                break;
            
            case "stats":
                showStats();
                break;
            
            case "spawn":
                if (parts.length > 1) {
                    spawnBody(parts[1]);
                } else {
                    System.out.println("Usage: spawn <concept>");
                }
                break;
            
            case "gravity":
                toggleGravity();
                break;
            
            case "reset":
                resetBodies();
                break;
            
            case "list":
                listBodies();
                break;
            
            case "select":
                if (parts.length > 1) {
                    selectBody(parts[1]);
                } else {
                    System.out.println("Usage: select <id>");
                }
                break;
            
            case "quit":
            case "exit":
                running = false;
                break;
            
            default:
                // Treat as concept force
                applyConceptForce(input);
                break;
        }
    }
    
    /**
     * Apply concept as force to all bodies
     */
    private static void applyConceptForce(String concept) {
        System.out.println("💥 APPLYING CONCEPT FORCE: '" + concept + "'");
        
        for (HyperRigidBody body : bodies) {
            body.applyDataForce(concept);
        }
        
        System.out.println("   ✓ Force applied to " + bodies.size() + " body(ies)");
    }
    
    /**
     * Dump tracking data
     */
    private static void dumpTracking() {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("📜 TRACKING LOG");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        for (int i = 0; i < bodies.size(); i++) {
            HyperRigidBody body = bodies.get(i);
            
            System.out.println("\nBody " + (i + 1) + " [" + body.id + "] - " + body.concept);
            System.out.println("─────────────────────────────────────────────────────────────");
            
            if (body.collisionLog.isEmpty()) {
                System.out.println("   No collisions recorded");
            } else {
                for (String log : body.collisionLog) {
                    System.out.println("   " + log);
                }
            }
            
            System.out.printf("\n   Current 3D Position: [%.2f, %.2f, %.2f]\n", 
                body.x, body.y, body.z);
            System.out.printf("   Current 3D Speed: %.4f\n", body.get3DSpeed());
            System.out.printf("   Data Mass: %.4f\n", body.dataMass);
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
    }
    
    /**
     * Export mesh
     */
    private static void exportMesh() {
        if (bodies.isEmpty()) {
            System.out.println("No bodies to export");
            return;
        }
        
        System.out.println("\n📦 EXPORTING MESHES...");
        
        for (int i = 0; i < bodies.size(); i++) {
            HyperRigidBody body = bodies.get(i);
            String filename = "mesh_" + body.id + ".obj";
            
            String obj = body.geometry.exportObj();
            
            // In a real implementation, write to file
            System.out.println("\nMesh " + (i + 1) + " [" + body.id + "]:");
            System.out.println("─────────────────────────────────────────────────────────────");
            System.out.println(obj.substring(0, Math.min(500, obj.length())));
            if (obj.length() > 500) {
                System.out.println("... (" + (obj.length() - 500) + " more bytes)");
            }
        }
    }
    
    /**
     * Show statistics
     */
    private static void showStats() {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("📊 PHYSICS ENGINE STATISTICS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        long uptime = System.currentTimeMillis() - startTime;
        
        System.out.println("\nENGINE:");
        System.out.println("   Uptime: " + (uptime / 1000) + "s");
        System.out.println("   Bodies: " + bodies.size());
        System.out.println("   Gravity: " + (gravityEnabled ? "ON" : "OFF"));
        System.out.println("   Dimensions: 17");
        
        System.out.println("\nBODIES:");
        for (int i = 0; i < bodies.size(); i++) {
            HyperRigidBody body = bodies.get(i);
            System.out.println("\n   Body " + (i + 1) + ":");
            System.out.println(body.getStatus());
            System.out.println(body.geometry.getStats());
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
    
    /**
     * Spawn new body
     */
    private static void spawnBody(String concept) {
        System.out.println("\n⚛️ SPAWNING NEW BODY: '" + concept + "'");
        
        HyperRigidBody body = new HyperRigidBody(concept, new Mesh(100));
        bodies.add(body);
        
        System.out.println("   ✓ Body spawned (Total: " + bodies.size() + ")");
    }
    
    /**
     * Toggle gravity
     */
    private static void toggleGravity() {
        gravityEnabled = !gravityEnabled;
        System.out.println("\n🌍 GRAVITY: " + (gravityEnabled ? "ENABLED" : "DISABLED"));
        
        if (gravityEnabled && bodies.size() > 1) {
            System.out.println("   Bodies will attract each other based on semantic similarity");
        }
    }
    
    /**
     * Reset all bodies
     */
    private static void resetBodies() {
        System.out.println("\n🔄 RESETTING ALL BODIES...");
        
        for (HyperRigidBody body : bodies) {
            // Reset positions and velocities
            for (int i = 0; i < 17; i++) {
                body.hyperVelocity[i] = 0;
                body.hyperAcceleration[i] = 0;
            }
            
            // Reset mesh
            body.geometry.reset();
            
            // Clear logs
            body.collisionLog.clear();
        }
        
        System.out.println("   ✓ All bodies reset to initial state");
    }
    
    /**
     * List all bodies
     */
    private static void listBodies() {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("📋 ACTIVE BODIES");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        if (bodies.isEmpty()) {
            System.out.println("   No bodies");
        } else {
            for (int i = 0; i < bodies.size(); i++) {
                HyperRigidBody body = bodies.get(i);
                System.out.printf("\n   %d. [%s] %s\n", i + 1, body.id, body.concept);
                System.out.printf("      Mass: %.4f | Charge: %.2f | Speed: %.4f\n",
                    body.dataMass, body.semanticCharge, body.get3DSpeed());
            }
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
    }
    
    /**
     * Select specific body for detailed view
     */
    private static void selectBody(String id) {
        HyperRigidBody selected = null;
        
        for (HyperRigidBody body : bodies) {
            if (body.id.equals(id)) {
                selected = body;
                break;
            }
        }
        
        if (selected == null) {
            System.out.println("Body not found: " + id);
            return;
        }
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🔍 BODY DETAILS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println(selected.getStatus());
        System.out.println(selected.geometry.getStats());
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
    
    /**
     * Print help
     */
    private static void printHelp() {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("COMMANDS:");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   <concept>        Apply concept as force (e.g., 'CHAOS', 'ORDER')");
        System.out.println("   dump             Show tracking data and collision logs");
        System.out.println("   export           Export meshes as OBJ format");
        System.out.println("   stats            Show detailed statistics");
        System.out.println("   spawn <concept>  Create new hyper-rigid body");
        System.out.println("   gravity          Toggle gravity simulation");
        System.out.println("   reset            Reset all bodies to initial state");
        System.out.println("   list             List all active bodies");
        System.out.println("   select <id>      View detailed info for specific body");
        System.out.println("   help             Show this help");
        System.out.println("   quit             Exit console");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
    }
}
