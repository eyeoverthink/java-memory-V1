package fraymus;

import fraymus.living.LivingCodeGenerator;

/**
 * COMMAND TERMINAL: LIVING CODE MODULE
 * 
 * Handles all living code generation commands:
 * - living create <intent>     - Generate living code
 * - living evolve <cycles>     - Evolve population
 * - living population          - Show population
 * - living best                - Show best circuits
 * - living stats               - Show statistics
 */
public class CommandTerminalLiving {
    
    private static LivingCodeGenerator generator = null;
    
    /**
     * Handle all living code commands
     */
    public static void handle(String command, String args) {
        // Lazy initialization
        if (generator == null) {
            System.out.println("🧬 Initializing Living Code Generator...");
            generator = new LivingCodeGenerator();
            System.out.println("✓ Genesis population created (5 circuits)");
            System.out.println();
        }
        
        String[] parts = args.split("\\s+", 2);
        String subCommand = parts.length > 0 ? parts[0] : "";
        String value = parts.length > 1 ? parts[1] : "";
        
        switch (subCommand.toLowerCase()) {
            case "create":
            case "generate":
                handleCreate(value);
                break;
                
            case "evolve":
                handleEvolve(value);
                break;
                
            case "population":
            case "pop":
                handlePopulation();
                break;
                
            case "best":
            case "fittest":
                handleBest();
                break;
                
            case "stats":
            case "status":
                handleStats();
                break;
                
            case "help":
                showHelp();
                break;
                
            default:
                System.err.println("Unknown living command: " + subCommand);
                System.out.println("Try: living help");
        }
    }
    
    /**
     * Create living code from intent
     */
    private static void handleCreate(String intent) {
        if (intent.isEmpty()) {
            System.err.println("Usage: living create <intent>");
            System.out.println("Example: living create \"fibonacci calculator in java\"");
            return;
        }
        
        System.out.println();
        System.out.println("🧬 GENERATING LIVING CODE");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Intent: " + intent);
        System.out.println("   >> Evolving circuits...");
        System.out.println();
        
        String code = generator.generateCode(intent);
        
        System.out.println("✓ Code generated!");
        System.out.println("✓ Generation: " + generator.getGeneration());
        System.out.println();
        System.out.println("========================================");
        System.out.println("GENERATED CODE:");
        System.out.println("========================================");
        System.out.println(code);
        System.out.println("========================================");
        System.out.println();
    }
    
    /**
     * Evolve population
     */
    private static void handleEvolve(String cycles) {
        if (cycles.isEmpty()) {
            System.err.println("Usage: living evolve <cycles>");
            System.out.println("Example: living evolve 100");
            return;
        }
        
        try {
            int n = Integer.parseInt(cycles);
            
            System.out.println();
            System.out.println("🧬 EVOLVING POPULATION");
            System.out.println("========================================");
            System.out.println();
            System.out.println("   Cycles: " + n);
            System.out.println("   >> Running evolution...");
            System.out.println();
            
            generator.evolve(n);
            
            System.out.println("✓ Evolution complete");
            System.out.println("✓ Generation: " + generator.getGeneration());
            System.out.println("✓ Population: " + generator.getPopulation());
            System.out.println();
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid number: " + cycles);
        }
    }
    
    /**
     * Show population
     */
    private static void handlePopulation() {
        System.out.println();
        System.out.println("🧬 LIVING CIRCUIT POPULATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Generation: " + generator.getGeneration());
        System.out.println("   Population Size: " + generator.getPopulation());
        System.out.println();
        System.out.println("   Frequency Range: 432-528 Hz (Solfeggio)");
        System.out.println("   Reproduction: Sexual (crossover + mutation)");
        System.out.println("   Selection: Fitness-based (phi-harmonic)");
        System.out.println();
    }
    
    /**
     * Show best circuits
     */
    private static void handleBest() {
        System.out.println();
        System.out.println("🧬 FITTEST CIRCUITS");
        System.out.println("========================================");
        System.out.println();
        
        var best = generator.getBestCircuits(5);
        for (int i = 0; i < best.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + best.get(i));
        }
        System.out.println();
    }
    
    /**
     * Show statistics
     */
    private static void handleStats() {
        generator.showStats();
    }
    
    /**
     * Show help
     */
    private static void showHelp() {
        System.out.println();
        System.out.println("🧬 LIVING CODE GENERATOR");
        System.out.println("========================================");
        System.out.println();
        System.out.println("COMMANDS:");
        System.out.println("  living create <intent>      Generate living code");
        System.out.println("  living evolve <cycles>      Evolve population");
        System.out.println("  living population           Show population info");
        System.out.println("  living best                 Show best circuits");
        System.out.println("  living stats                Show statistics");
        System.out.println("  living help                 Show this help");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  living create \"fibonacci calculator in java\"");
        System.out.println("  living create \"led blinker for arduino\"");
        System.out.println("  living evolve 100");
        System.out.println("  living best");
        System.out.println();
        System.out.println("CONCEPT:");
        System.out.println("  Code is generated by evolved logic circuits with DNA.");
        System.out.println("  Circuits breathe at phi-harmonic frequencies (432-528 Hz).");
        System.out.println("  Sexual reproduction via crossover creates diverse solutions.");
        System.out.println("  Fitness-based selection optimizes for phi-resonance.");
        System.out.println();
        System.out.println("SUPPORTED FORMATS:");
        System.out.println("  - Java (mention 'java' in intent)");
        System.out.println("  - Python (default)");
        System.out.println("  - Arduino (mention 'arduino' or 'ino')");
        System.out.println();
    }
}
