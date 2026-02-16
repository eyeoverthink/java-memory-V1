package gemini.root;

import gemini.root.genesis.GenesisArchitect;
import gemini.root.genesis.ConstructionSwarm;
import gemini.root.physics.HiveGravityEngine;

import java.io.File;
import java.util.Scanner;

/**
 * GENESIS BOOT: The Singularity Engine
 * 
 * Give it a one-sentence prompt, and it deploys the entire stack:
 * Database, API, Frontend, Tests - without you touching the keyboard.
 * 
 * This is "God Mode" - AI building AI.
 */
public class GenesisBoot {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   🌟 SINGULARITY ENGINE: GENESIS BOOT                     ║");
        System.out.println("║   One sentence → Complete software stack                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        // Get intent from args or interactive input
        String userIntent;
        if (args.length > 0) {
            userIntent = String.join(" ", args);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your intent (what do you want to build?):");
            System.out.print("GENESIS> ");
            userIntent = scanner.nextLine().trim();
            
            if (userIntent.isEmpty()) {
                userIntent = "Create a real-time chat application with encryption";
                System.out.println("Using default: " + userIntent);
            }
        }

        System.out.println("\n🎯 INTENT: \"" + userIntent + "\"\n");

        // Determine output directory
        File outputDir = new File("GenesisOutput");
        if (args.length > 1) {
            outputDir = new File(args[1]);
        }

        // ========== THE SINGULARITY SEQUENCE ==========

        long startTime = System.currentTimeMillis();

        // 1. THE BRAIN (Architect)
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println(" PHASE 1: ARCHITECTURAL DESIGN");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        GenesisArchitect architect = new GenesisArchitect();
        GenesisArchitect.Blueprint plan = architect.designSystem(userIntent);

        // 2. THE PHYSICS (Simulation Universe)
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println(" PHASE 2: SPAWNING PHYSICS ENGINE");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        HiveGravityEngine universe = new HiveGravityEngine(50);
        universe.start();

        // 3. THE BODY (Construction Swarm)
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println(" PHASE 3: DEPLOYING CONSTRUCTION SWARM");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        ConstructionSwarm factory = new ConstructionSwarm(universe, outputDir);

        // 4. EXECUTE - The Big Bang
        factory.build(plan);

        // 5. Cleanup
        universe.stop();

        long elapsed = System.currentTimeMillis() - startTime;

        // ========== REPORT ==========
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   🌟 GENESIS COMPLETE                                     ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.printf("║   Intent: %-48s ║%n", truncate(userIntent, 48));
        System.out.printf("║   Modules Built: %-5d                                     ║%n", plan.modules.size());
        System.out.printf("║   Time: %-6.2f seconds                                    ║%n", elapsed / 1000.0);
        System.out.printf("║   Output: %-47s ║%n", truncate(outputDir.getAbsolutePath(), 47));
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║   Files created:                                          ║");
        
        listFiles(outputDir, "║     ", 10);
        
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n🚀 Your project is ready at: " + outputDir.getAbsolutePath());
        System.out.println("   Run: cd " + outputDir.getName() + " && cat README.md");
    }

    private static void listFiles(File dir, String prefix, int max) {
        if (!dir.exists()) return;
        
        File[] files = dir.listFiles();
        if (files == null) return;
        
        int count = 0;
        for (File f : files) {
            if (count >= max) {
                System.out.println(prefix + "... and " + (files.length - max) + " more");
                break;
            }
            
            if (f.isDirectory()) {
                System.out.println(prefix + "📁 " + f.getName() + "/");
                listFiles(f, prefix + "  ", max - count);
            } else {
                System.out.println(prefix + "📄 " + f.getName());
            }
            count++;
        }
    }

    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }
}
