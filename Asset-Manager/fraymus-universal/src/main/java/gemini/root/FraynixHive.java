package gemini.root;

import gemini.root.physics.*;

/**
 * FRAYNIX HIVE: The Singularity Point.
 * 
 * Fraynix (Brain) + OpenClaw (Body) = Telepathy
 * 
 * Tasks are particles with mass. The Claw is a gravity well.
 * When they collide, the task executes automatically.
 * You don't type commands. You THINK them into existence.
 */
public class FraynixHive {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("   🕸️ HIVE MIND ONLINE: Fraynix + OpenClaw");
        System.out.println("   Gravity causes Code Execution.");
        System.out.println("═══════════════════════════════════════════════════");

        // 1. Start the Physics Engine (The Universe)
        HiveGravityEngine universe = new HiveGravityEngine(50);

        // 2. Spawn the CLAW at the center (The Body / Executor)
        // Position: center of the 100x100x100 thought-space
        ClawParticle claw = new ClawParticle(50, 50, 50);
        universe.register(claw);

        // Check if OpenClaw is actually running
        if (claw.isClawOnline()) {
            System.out.println(">>> CLAW STATUS: ONLINE - Ready for telepathy");
        } else {
            System.out.println(">>> CLAW STATUS: OFFLINE - Running in simulation mode");
            System.out.println("    (Start OpenClaw on port 18789 to enable real execution)");
        }

        // 3. Inject thoughts (Task particles)
        System.out.println("\n>> THOUGHT INJECTION: 'Create a deployment script'");
        universe.injectTask(
            "Create a deployment script for Fraynix that builds and runs the JAR",
            80.0,   // High priority = high mass
            10, 10, 10  // Start far from center
        );

        // 4. Start the physics loop
        Thread engineThread = universe.start();

        // 5. Let gravity work for 10 seconds
        System.out.println("\n>>> Physics simulation running... (10 seconds)");
        System.out.println(">>> Watch the TASK particle spiral toward the CLAW...\n");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 6. Inject another task mid-simulation
        System.out.println("\n>> THOUGHT INJECTION: 'Check system status'");
        universe.injectTask(
            "Display current system status and resource usage",
            60.0,
            90, 90, 90  // Different starting position
        );

        // Let it run more
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Shutdown
        universe.stop();
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("   HIVE SHUTDOWN");
        System.out.println("   Tasks executed: " + claw.getTasksExecuted());
        System.out.println("   Total ticks: " + universe.getTickCount());
        System.out.println("═══════════════════════════════════════════════════");
    }
}
