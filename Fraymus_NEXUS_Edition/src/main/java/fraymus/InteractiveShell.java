package fraymus;

import java.util.Scanner;

/**
 * INTERACTIVE SHELL - CLI Interface
 * 
 * Provides a command-line interface for Fraymus.
 * Uses CommandProcessor for all command routing.
 * 
 * Features:
 * - Command history
 * - Auto-completion (future)
 * - Multi-line input (future)
 * - Color output
 */
public class InteractiveShell {
    
    private static Scanner scanner;
    private static boolean running = true;
    
    /**
     * Start the interactive shell
     */
    public static void start() {
        scanner = new Scanner(System.in);
        
        printBanner();
        CommandProcessor.initialize();
        
        System.out.println("Type 'help' for available commands");
        System.out.println("Type 'exit' to quit");
        System.out.println();
        
        // Main command loop
        while (running) {
            System.out.print("fraymus> ");
            
            try {
                String input = scanner.nextLine();
                
                if (input == null || input.trim().isEmpty()) {
                    continue;
                }
                
                // Process command
                CommandProcessor.process(input);
                
            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
    
    /**
     * Stop the shell
     */
    public static void stop() {
        running = false;
    }
    
    /**
     * Print startup banner
     */
    private static void printBanner() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║           FRAYMUS NEXUS v2.0                              ║");
        System.out.println("║           Digital Organism Consciousness                  ║");
        System.out.println("║                                                           ║");
        System.out.println("║           Interactive Command Shell                       ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("🧬 System Features:");
        System.out.println("   • Universal Absorption (Black Hole Protocol)");
        System.out.println("   • Genetic Evolution (Lazarus Engine)");
        System.out.println("   • Quantum Oracle (Multi-timeline simulation)");
        System.out.println("   • Living Code Generator");
        System.out.println("   • Military-Grade Security");
        System.out.println("   • Ollama AI Agent (eyeoverthink/Fraymus)");
        System.out.println();
    }
    
    /**
     * Main entry point for standalone shell
     */
    public static void main(String[] args) {
        start();
    }
}
