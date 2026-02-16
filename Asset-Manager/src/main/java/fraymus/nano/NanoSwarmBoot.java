package fraymus.nano;

import fraymus.core.GravityEngine;

/**
 * NANO-SWARM BOOT - The AGI Launcher
 * 
 * Starts the physics engine, maps your drive, and sets the swarm loose.
 * 
 * WARNING: Autonomous File Modification Enabled.
 * This creates a self-healing, self-improving nano-swarm AGI system.
 */
public class NanoSwarmBoot {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🚀 FRAYNIX NANO-SWARM (AGI MODE)                      ║");
        System.out.println("║                                                               ║");
        System.out.println("║  WARNING: Autonomous File Modification Enabled                ║");
        System.out.println("║  Each .java file gets its own monitoring thread               ║");
        System.out.println("║  High entropy triggers automatic refactoring                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. Start the Physics Laws
        GravityEngine universe = GravityEngine.getInstance();
        if (!universe.isRunning()) {
            universe.start();
        }

        // 2. Map Reality (The File System)
        FileSystemGalaxy mapper = new FileSystemGalaxy(universe);
        
        // Get target directory from args or use current directory
        String targetDir = args.length > 0 ? args[0] : System.getProperty("user.dir");
        
        System.out.println("Target Directory: " + targetDir);
        System.out.println();
        
        mapper.ingest(targetDir);

        // 3. The Infinite Loop
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌟 UNIVERSE ONLINE. OBSERVING ENTROPY...                     ║");
        System.out.println("║                                                               ║");
        System.out.println("║  Nano-Agents: " + String.format("%-48d", mapper.getAgentCount()) + "║");
        System.out.println("║  Physics Engine: ACTIVE                                       ║");
        System.out.println("║  Autonomous Repair: ENABLED                                   ║");
        System.out.println("║                                                               ║");
        System.out.println("║  Press Ctrl+C to stop the swarm                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("🛑 SWARM SHUTDOWN INITIATED");
            System.out.println("   Nano-agents entering dormant state...");
            System.out.println("   Physics engine stopping...");
            System.out.println("✓ Swarm offline");
        }));
        
        while (true) {
            // The Physics Engine calculates relationships
            universe.tick(); 
            
            // If "Fusion" happens between Nano-Agents (e.g., two files are related),
            // OpenClaw can optimize them together.
            
            try { 
                Thread.sleep(50); 
            } catch (Exception e) {
                break;
            }
        }
    }
}
