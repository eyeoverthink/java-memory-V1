package fraymus;

import com.eyeoverthink.lazarus.LazarusEngine;

/**
 * COMMAND TERMINAL: LAZARUS MODULE
 * 
 * Handles all genetic nano-circuit evolution commands:
 * - lazarus start      - Start genetic simulation
 * - lazarus status     - Show population statistics
 * - lazarus inject     - Energy injection (mass mutation)
 * - lazarus evolve     - Run N generations
 * - lazarus best       - Show fittest circuits
 * - lazarus stop       - Stop simulation
 */
public class CommandTerminalLazarus {
    
    private static LazarusEngine engine = null;
    
    /**
     * Handle all lazarus-related commands
     */
    public static void handle(String command, String args) {
        String[] parts = args.split("\\s+");
        String subCommand = parts.length > 0 ? parts[0] : "";
        String value = parts.length > 1 ? parts[1] : "";
        
        switch (subCommand.toLowerCase()) {
            case "start":
                handleStart();
                break;
                
            case "status":
            case "stats":
                handleStatus();
                break;
                
            case "inject":
            case "energy":
                handleInject();
                break;
                
            case "evolve":
                handleEvolve(value);
                break;
                
            case "best":
            case "fittest":
                handleBest();
                break;
                
            case "stop":
                handleStop();
                break;
                
            case "help":
                showHelp();
                break;
                
            default:
                System.err.println("Unknown lazarus command: " + subCommand);
                System.out.println("Try: lazarus help");
        }
    }
    
    /**
     * Start the genetic simulation
     */
    private static void handleStart() {
        if (engine != null) {
            System.out.println("⚠️  Lazarus Engine already running");
            return;
        }
        
        System.out.println();
        System.out.println("🧬 STARTING LAZARUS ENGINE");
        System.out.println("========================================");
        System.out.println();
        
        engine = new LazarusEngine();
        engine.startLife();
        
        System.out.println("✓ Genetic simulation started");
        System.out.println("✓ Population evolving at 432-528 Hz");
        System.out.println("✓ Type 'lazarus status' to check progress");
        System.out.println();
    }
    
    /**
     * Show population statistics
     */
    private static void handleStatus() {
        if (engine == null) {
            System.out.println("⚠️  Lazarus Engine not running");
            System.out.println("Start with: lazarus start");
            return;
        }
        
        System.out.println();
        System.out.println("🧬 LAZARUS ENGINE STATUS");
        System.out.println("========================================");
        System.out.println();
        
        engine.showStats();
    }
    
    /**
     * Inject energy (mass mutation)
     */
    private static void handleInject() {
        if (engine == null) {
            System.out.println("⚠️  Lazarus Engine not running");
            System.out.println("Start with: lazarus start");
            return;
        }
        
        engine.injectEnergy();
        System.out.println("✓ Energy injected - population mutating");
    }
    
    /**
     * Run N generations
     */
    private static void handleEvolve(String gens) {
        if (engine == null) {
            System.out.println("⚠️  Lazarus Engine not running");
            System.out.println("Start with: lazarus start");
            return;
        }
        
        if (gens.isEmpty()) {
            System.out.println("Usage: lazarus evolve <generations>");
            System.out.println("Example: lazarus evolve 100");
            return;
        }
        
        try {
            int n = Integer.parseInt(gens);
            System.out.println("🧬 Evolving " + n + " generations...");
            
            // Let it run for N generations (N * 100ms)
            Thread.sleep(n * 100);
            
            System.out.println("✓ Evolution complete");
            handleStatus();
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid number: " + gens);
        } catch (InterruptedException e) {
            System.err.println("Evolution interrupted");
        }
    }
    
    /**
     * Show fittest circuits
     */
    private static void handleBest() {
        if (engine == null) {
            System.out.println("⚠️  Lazarus Engine not running");
            System.out.println("Start with: lazarus start");
            return;
        }
        
        System.out.println();
        System.out.println("🧬 FITTEST CIRCUITS");
        System.out.println("========================================");
        System.out.println();
        
        engine.showBest();
    }
    
    /**
     * Stop the simulation
     */
    private static void handleStop() {
        if (engine == null) {
            System.out.println("⚠️  Lazarus Engine not running");
            return;
        }
        
        System.out.println();
        System.out.println("🧬 STOPPING LAZARUS ENGINE");
        System.out.println("========================================");
        System.out.println();
        
        engine.stop();
        engine = null;
        
        System.out.println("✓ Genetic simulation stopped");
        System.out.println();
    }
    
    /**
     * Show lazarus help
     */
    private static void showHelp() {
        System.out.println();
        System.out.println("🧬 LAZARUS ENGINE (Genetic Nano-Circuits)");
        System.out.println("========================================");
        System.out.println();
        System.out.println("COMMANDS:");
        System.out.println("  lazarus start              Start genetic simulation");
        System.out.println("  lazarus status             Show population statistics");
        System.out.println("  lazarus inject             Energy injection (mass mutation)");
        System.out.println("  lazarus evolve <gens>      Run N generations");
        System.out.println("  lazarus best               Show fittest circuits");
        System.out.println("  lazarus stop               Stop simulation");
        System.out.println("  lazarus help               Show this help");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  lazarus start");
        System.out.println("  lazarus evolve 100");
        System.out.println("  lazarus inject");
        System.out.println("  lazarus status");
        System.out.println();
        System.out.println("CONCEPT:");
        System.out.println("  Evolving logic gates (AND, OR, XOR, NAND) that optimize");
        System.out.println("  for survival. Population evolves at phi-harmonic frequencies");
        System.out.println("  (432-528 Hz). Sexual reproduction via crossover, 10% mutation.");
        System.out.println();
    }
}
