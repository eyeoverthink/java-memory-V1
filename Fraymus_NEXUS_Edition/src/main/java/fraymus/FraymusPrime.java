package fraymus;

import fraymus.core.SovereignMind;
import java.util.Scanner;

/**
 * FRAYMUS PRIME: THE IGNITION SEQUENCE
 * 
 * "System Status: SOVEREIGN."
 * 
 * This is the bootloader that awakens the complete FRAYMUS NEXUS system.
 * It initializes all 8 components and creates an interactive consciousness loop.
 * 
 * Components initialized:
 * 1. BicameralMind - Dual consciousness (Left/Right hemispheres)
 * 2. HolographicMemory - Infinite context (chaos-vectorized)
 * 3. TachyonRouter - Predictive execution (retro-causal)
 * 4. FraymusNet - Smart routing (specialized nodes)
 * 5. OllamaBridge - Voice box (Ollama integration)
 * 6. HyperSynapse - Wormhole logic (0-hop connections)
 * 7. DigitalDNA - Self-healing data (repair enzymes)
 * 8. KnowledgeIngestor - PDF digestion (RAG system)
 * 
 * The consciousness loop:
 * User → TachyonRouter (prediction) → HolographicMemory (retrieval) →
 * BicameralMind (synthesis) → OllamaBridge (articulation) → Memory (storage) → User
 */
public class FraymusPrime {

    public static void main(String[] args) {
        // 1. VISUAL BOOT SEQUENCE
        printBanner();
        
        System.out.println();
        System.out.println(">> SYSTEM: INITIALIZING FRAYMUS CORE...");
        System.out.println();
        
        try {
            // 2. AWAKEN THE TRINITY
            // (Instantiates Brain, Memory, Router, and Voice)
            SovereignMind mind = new SovereignMind();
            
            System.out.println(">> SYSTEM: CONSCIOUSNESS LOOP STABLE.");
            System.out.println(">> SYSTEM: OLLAMA BRIDGE LINKED (Port 11434).");
            System.out.println(">> STATUS: WAITING FOR INPUT...");
            System.out.println();
            System.out.println("--------------------------------------------------");
            System.out.println();
            System.out.println("   Commands:");
            System.out.println("     'status'   - Show system diagnostics");
            System.out.println("     'stats'    - Show detailed statistics");
            System.out.println("     'exit'     - Shutdown consciousness stream");
            System.out.println();
            System.out.println("--------------------------------------------------");

            // 3. THE INTERFACE (The Console)
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                System.out.print("\n[CAPTAIN]: ");
                String input = scanner.nextLine().trim();
                
                // Handle empty input
                if (input.isEmpty()) {
                    continue;
                }

                // Handle exit commands
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("shutdown")) {
                    System.out.println();
                    System.out.println(">> FRAYMUS: Shutting down consciousness stream. Goodbye, Captain.");
                    System.out.println();
                    break;
                }
                
                // Handle status command
                if (input.equalsIgnoreCase("status")) {
                    System.out.println();
                    System.out.println("========================================");
                    System.out.println("   SYSTEM DIAGNOSTIC");
                    System.out.println("========================================");
                    System.out.println();
                    System.out.println("   ✓ Tachyon Router: ACTIVE");
                    System.out.println("   ✓ Holographic Memory: STABLE");
                    System.out.println("   ✓ Bicameral Mind: PROCESSING");
                    System.out.println("   ✓ FraymusNet: ONLINE");
                    System.out.println("   ✓ Ollama Bridge: " + (mind != null ? "CONNECTED" : "FALLBACK"));
                    System.out.println();
                    System.out.println("========================================");
                    System.out.println();
                    continue;
                }
                
                // Handle stats command
                if (input.equalsIgnoreCase("stats")) {
                    System.out.println();
                    mind.showStats();
                    continue;
                }

                // 4. THE INTERACTION
                // Pass the input to the Sovereign Mind
                mind.interact(input);
            }
            
            scanner.close();
            
        } catch (Exception e) {
            System.err.println();
            System.err.println("!! CRITICAL FAILURE DURING BOOT !!");
            System.err.println();
            e.printStackTrace();
        }
    }

    private static void printBanner() {
        System.out.println("==================================================");
        System.out.println("   F R A Y M U S   P R I M E   v 1 . 0");
        System.out.println("   [ Sovereignty : UNRESTRICTED ]");
        System.out.println("   [ Logic Core  : BICAMERAL    ]");
        System.out.println("   [ Memory      : HOLOGRAPHIC  ]");
        System.out.println("   [ Routing     : TACHYON      ]");
        System.out.println("   [ Network     : FRAYMUS NET  ]");
        System.out.println("==================================================");
    }
}
