package fraymus;

import fraymus.core.GravityEngine;
import fraymus.core.PhiSuit;
import fraymus.limbs.ClawParticle;

/**
 * FRAYNIX HIVE - The Integration
 * 
 * Demonstrates how Fraynix (The Brain) controls OpenClaw (The Body)
 * through physics-based task execution.
 * 
 * Tasks are particles. The Claw is a massive gravity well.
 * When they collide, execution happens automatically.
 */
public class FraynixHive {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🕸️ HIVE MIND ONLINE: Fraynix + OpenClaw               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. Start the Physics Engine
        GravityEngine universe = GravityEngine.getInstance();
        if (!universe.isRunning()) {
            universe.start();
        }

        // 2. Spawn the CLAW (The Body)
        // Positioned at the center of the universe (50, 50, 50)
        ClawParticle claw = new ClawParticle(50, 50, 50); 
        // Note: ClawParticle auto-registers via SpatialNode constructor
        System.out.println("✓ CLAW spawned at center (50, 50, 50)");

        // 3. Inject a Thought (The Intent)
        // "I want to build a deployment script"
        System.out.println();
        System.out.println(">> THOUGHT INJECTION: 'Build Deployment Script'");
        PhiSuit<String> task = new PhiSuit<>("Create a deployment script for Fraynix", 20, 20, 20, "TASK_DEPLOY");
        task.a = 80; // High importance (amplitude)
        // Note: PhiSuit auto-registers via SpatialNode constructor
        System.out.println("✓ Task particle created at (20, 20, 20)");

        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  PHYSICS SIMULATION RUNNING                                   ║");
        System.out.println("║  Gravity will pull task toward claw...                        ║");
        System.out.println("║  Collision will trigger autonomous execution                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // 4. Run Physics Loop
        // The Gravity Engine will naturally pull the 'task' into the 'claw'
        // When close enough, manually trigger execution
        for (int i = 0; i < 100; i++) {
            universe.tick();
            
            // Show progress every 10 ticks
            if (i % 10 == 0) {
                double distance = task.distanceTo(claw);
                System.out.printf("Tick %3d: Distance to claw = %.2f%n", i, distance);
                
                // Check for collision (close enough to execute)
                if (distance < 5.0 && task.a > 50) {
                    claw.executeTask(task);
                    break;
                }
            }
            
            try { 
                Thread.sleep(50); 
            } catch (Exception e) {
                break;
            }
        }

        System.out.println();
        System.out.println("✓ Simulation complete");
        System.out.println();
        System.out.println("What just happened:");
        System.out.println("  1. Task particle created in 3D space");
        System.out.println("  2. Claw particle acts as gravity well");
        System.out.println("  3. Physics engine pulls task toward claw");
        System.out.println("  4. Collision triggers HTTP request to OpenClaw");
        System.out.println("  5. OpenClaw executes task autonomously");
        System.out.println();
        System.out.println("This is Gravity-Driven Code Execution.");
    }
}
