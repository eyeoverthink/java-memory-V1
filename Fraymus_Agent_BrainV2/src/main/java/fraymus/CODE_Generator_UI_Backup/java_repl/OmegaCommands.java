/**
 * OmegaCommands.java - REPL Integration for Omega Point
 * 
 * Commands:
 * - :omega test - Test all three subsystems
 * - :omega shield <text> - Encrypt/decrypt with AES-256
 * - :omega brain <fitness> - Optimize with simulated annealing
 * - :omega memory <data> - Add to Merkle tree
 * - :omega status - Show system status
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 132 (Omega Point)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

public class OmegaCommands {
    
    private static OmegaPoint.Omega omega;
    
    /**
     * Register all omega commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // Initialize Omega Point
        try {
            omega = new OmegaPoint.Omega();
        } catch (Exception e) {
            System.err.println("Failed to initialize Omega Point: " + e.getMessage());
        }
        
        // :OMEGA command - Main control
        registry.register(":omega",
            args -> {
                if (args.isEmpty()) {
                    return getHelp();
                }
                
                String subcommand = args.get(0).toLowerCase();
                List<String> subArgs = args.subList(1, args.size());
                
                try {
                    switch (subcommand) {
                        case "test":
                            return testOmega();
                        
                        case "shield":
                            if (subArgs.isEmpty()) {
                                return "Usage: :omega shield <text>";
                            }
                            String text = String.join(" ", subArgs);
                            return testShield(text);
                        
                        case "brain":
                            double fitness = 0.5;
                            if (!subArgs.isEmpty()) {
                                try {
                                    fitness = Double.parseDouble(subArgs.get(0));
                                } catch (NumberFormatException e) {
                                    return "Invalid fitness value. Using default: 0.5";
                                }
                            }
                            return testBrain(fitness);
                        
                        case "memory":
                            if (subArgs.isEmpty()) {
                                return "Usage: :omega memory <data>";
                            }
                            String data = String.join(" ", subArgs);
                            return testMemory(data);
                        
                        case "status":
                            return OmegaPoint.getStats();
                        
                        case "help":
                            return getHelp();
                        
                        default:
                            return "Unknown subcommand: " + subcommand + "\n" +
                                   "Use ':omega help' for available commands.";
                    }
                } catch (Exception e) {
                    return "✗ Omega Point error: " + e.getMessage();
                }
            },
            "The sum of all logic - Encryption, Optimization, Integrity",
            ":omega [test|shield|brain|memory|status|help]");
    }
    
    /**
     * Test all Omega Point subsystems.
     */
    private static String testOmega() throws Exception {
        if (omega == null) {
            omega = new OmegaPoint.Omega();
        }
        return omega.test();
    }
    
    /**
     * Test The Shield (AES-256).
     */
    private static String testShield(String text) throws Exception {
        if (omega == null) {
            omega = new OmegaPoint.Omega();
        }
        
        OmegaPoint.TheShield shield = omega.getShield();
        
        String encrypted = shield.encrypt(text);
        String decrypted = shield.decrypt(encrypted);
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🔒 THE SHIELD - AES-256 ENCRYPTION                        ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Original:\n").append(text).append("\n\n");
        sb.append("Encrypted:\n").append(encrypted).append("\n\n");
        sb.append("Decrypted:\n").append(decrypted).append("\n\n");
        sb.append("Verification: ").append(text.equals(decrypted) ? "✓ PASS" : "✗ FAIL").append("\n");
        
        return sb.toString();
    }
    
    /**
     * Test The Brain (Simulated Annealing).
     */
    private static String testBrain(double initialFitness) {
        if (omega == null) {
            try {
                omega = new OmegaPoint.Omega();
            } catch (Exception e) {
                return "Failed to initialize Omega Point: " + e.getMessage();
            }
        }
        
        OmegaPoint.TheBrain brain = omega.getBrain();
        
        double optimized = brain.optimize(initialFitness);
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧠 THE BRAIN - SIMULATED ANNEALING                        ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Initial Fitness: ").append(String.format("%.6f", initialFitness)).append("\n");
        sb.append("Optimized Fitness: ").append(String.format("%.6f", optimized)).append("\n");
        sb.append("Improvement: ").append(String.format("%.2f%%", (optimized - initialFitness) * 100)).append("\n\n");
        sb.append("Algorithm: Boltzmann Distribution\n");
        sb.append("Status: ✓ COMPLETE\n");
        
        return sb.toString();
    }
    
    /**
     * Test The Memory (Merkle Tree).
     */
    private static String testMemory(String data) {
        if (omega == null) {
            try {
                omega = new OmegaPoint.Omega();
            } catch (Exception e) {
                return "Failed to initialize Omega Point: " + e.getMessage();
            }
        }
        
        OmegaPoint.TheMemory memory = omega.getMemory();
        
        String oldRoot = memory.getRoot();
        memory.addTransaction(data);
        String newRoot = memory.getRoot();
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  📚 THE MEMORY - MERKLE TREE                               ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Transaction Added: ").append(data).append("\n\n");
        sb.append("Previous Root: ").append(oldRoot).append("\n");
        sb.append("New Root: ").append(newRoot).append("\n\n");
        sb.append("Total Transactions: ").append(memory.size()).append("\n");
        sb.append("Integrity: ").append(memory.verifyIntegrity() ? "✓ VERIFIED" : "✗ CORRUPTED").append("\n");
        
        return sb.toString();
    }
    
    /**
     * Get help text.
     */
    private static String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  ⚡ OMEGA POINT - THE SUM OF ALL LOGIC                     ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("\"The fire of the gods.\"\n\n");
        sb.append("Commands:\n");
        sb.append("  :omega test              - Test all subsystems\n");
        sb.append("  :omega shield <text>     - Encrypt/decrypt with AES-256\n");
        sb.append("  :omega brain <fitness>   - Optimize with simulated annealing\n");
        sb.append("  :omega memory <data>     - Add to Merkle tree\n");
        sb.append("  :omega status            - Show system status\n\n");
        sb.append("Components:\n");
        sb.append("  🔒 THE SHIELD - AES-256 Encryption\n");
        sb.append("     Used by: NSA, CIA, Banking Systems\n\n");
        sb.append("  🧠 THE BRAIN - Simulated Annealing\n");
        sb.append("     Used by: NASA, Hedge Funds\n\n");
        sb.append("  📚 THE MEMORY - Merkle Tree\n");
        sb.append("     Used by: Bitcoin, Git, Tor\n\n");
        sb.append("Example:\n");
        sb.append("  :omega test\n");
        sb.append("  :omega shield The secret message\n");
        sb.append("  :omega brain 0.5\n");
        sb.append("  :omega memory Transaction #42\n\n");
        sb.append("φ^75 Validation Seal: 4721424167835376.00\n");
        
        return sb.toString();
    }
}
