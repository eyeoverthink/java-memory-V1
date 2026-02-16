package fraymus;

import fraymus.evolution.*;
import fraymus.evolution.goals.*;
import fraymus.hardware.LogicBlock;
import java.util.List;

/**
 * COMMAND TERMINAL: EVOLUTION MODULE
 * 
 * Handles all circuit evolution commands:
 * - evolve <goal>           - Evolve specific circuit (xor, adder, alu, cpu)
 * - evolve build <target>   - Build complete system (computer, 16bit, 32bit)
 * - library show            - Show all evolved circuits
 * - library search <term>   - Search circuit library
 * - library stats           - Library statistics
 * - generate <lang> <desc>  - Generate living code from evolved circuits
 * - prove <circuit>         - Prove circuit is real (execute it)
 */
public class CommandTerminalEvolution {
    
    private static GoalDrivenEvolution evolution = null;
    private static CircuitLibrary library = null;
    private static LivingCodeGenerator generator = null;
    
    /**
     * Handle all evolution-related commands
     */
    public static void handle(String command, String args) {
        // Lazy initialization
        if (evolution == null) {
            System.out.println("🧠 Initializing Evolution Engine...");
            evolution = new GoalDrivenEvolution();
            library = new CircuitLibrary();
            generator = new LivingCodeGenerator();
        }
        
        String[] parts = args.split("\\s+", 2);
        String subCommand = parts.length > 0 ? parts[0] : "";
        String value = parts.length > 1 ? parts[1] : "";
        
        switch (command) {
            case "evolve":
                handleEvolve(subCommand, value);
                break;
                
            case "library":
                handleLibrary(subCommand, value);
                break;
                
            case "generate":
                handleGenerate(subCommand, value);
                break;
                
            case "prove":
                handleProve(args);
                break;
                
            default:
                System.err.println("❌ Unknown evolution command: " + command);
        }
    }
    
    /**
     * Handle evolve commands
     */
    private static void handleEvolve(String target, String extra) {
        System.out.println("\n🧬 EVOLUTION ENGINE");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        
        switch (target.toLowerCase()) {
            case "xor":
                System.out.println("🧬 Evolving XOR gate...");
                evolution.evolve(new XORGoal());
                break;
                
            case "adder":
            case "fulladder":
                System.out.println("🧬 Evolving Full Adder...");
                evolution.evolve(new FullAdderGoal());
                break;
                
            case "alu":
            case "4bit":
                System.out.println("🧬 Evolving 4-bit ALU...");
                evolution.evolve(new FourBitALUGoal());
                break;
                
            case "register":
            case "8bit":
                System.out.println("🧬 Evolving 8-bit Register...");
                evolution.evolve(new RegisterGoal());
                break;
                
            case "cpu":
                System.out.println("🧬 Evolving 8-bit CPU...");
                evolution.evolve(new SimpleCPUGoal());
                break;
                
            case "build":
                handleBuild(extra);
                break;
                
            default:
                System.err.println("❌ Unknown evolution target: " + target);
                System.out.println("Available: xor, adder, alu, register, cpu, build");
        }
    }
    
    /**
     * Handle build commands (complete systems)
     */
    private static void handleBuild(String target) {
        switch (target.toLowerCase()) {
            case "computer":
            case "8bit":
                System.out.println("🧬 Building complete 8-bit computer...");
                BuildComputer.main(new String[]{});
                break;
                
            case "16bit":
                System.out.println("🧬 Building 16-bit computer...");
                System.out.println("⚠️  16-bit evolution not yet implemented");
                System.out.println("This would evolve: 16-bit ALU, 16-bit registers, 16-bit CPU");
                break;
                
            case "32bit":
                System.out.println("🧬 Building 32-bit computer...");
                System.out.println("⚠️  32-bit evolution not yet implemented");
                System.out.println("This would evolve: 32-bit ALU, 32-bit registers, 32-bit CPU");
                break;
                
            default:
                System.err.println("❌ Unknown build target: " + target);
                System.out.println("Available: computer (8bit), 16bit, 32bit");
        }
    }
    
    /**
     * Handle library commands
     */
    private static void handleLibrary(String subCommand, String value) {
        switch (subCommand.toLowerCase()) {
            case "show":
            case "list":
                System.out.println("\n📚 CIRCUIT LIBRARY");
                System.out.println("═══════════════════════════════════════════════════════════");
                library.listAll();
                break;
                
            case "search":
                if (value.isEmpty()) {
                    System.err.println("❌ Usage: library search <goal>");
                    return;
                }
                System.out.println("🔍 Searching for: " + value);
                List<CircuitLibrary.CircuitRecord> results = library.search(value, 0);
                if (results.isEmpty()) {
                    System.out.println("No circuits found matching: " + value);
                } else {
                    System.out.println("Found " + results.size() + " circuits:");
                    for (CircuitLibrary.CircuitRecord record : results) {
                        System.out.println("  • " + record.id + " - Fitness: " + record.fitness + "% - Gates: " + record.gateCount);
                    }
                }
                break;
                
            case "stats":
                System.out.println("\n📊 LIBRARY STATISTICS");
                System.out.println("═══════════════════════════════════════════════════════════");
                System.out.println("Total circuits: " + library.search("", 0).size());
                System.out.println("Storage: evolution_db/");
                break;
                
            default:
                System.err.println("❌ Unknown library command: " + subCommand);
                System.out.println("Available: show, search, stats");
        }
    }
    
    /**
     * Handle generate commands (living code generation)
     */
    private static void handleGenerate(String language, String description) {
        if (language.isEmpty()) {
            System.err.println("❌ Usage: generate <language> <description>");
            System.out.println("Languages: java, python, cpp, processing");
            return;
        }
        
        if (description.isEmpty()) {
            description = "Generated code from evolved circuits";
        }
        
        System.out.println("\n🔧 LIVING CODE GENERATOR");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("Language: " + language);
        System.out.println("Description: " + description);
        System.out.println();
        
        String code = generator.generateLivingCode(description, language);
        
        // Save to file
        String filename = "Generated_" + language + "_" + System.currentTimeMillis() + "." + getExtension(language);
        try {
            java.io.FileWriter writer = new java.io.FileWriter(filename);
            writer.write(code);
            writer.close();
            
            System.out.println("✅ Generated: " + filename);
            System.out.println("Size: " + code.length() + " bytes");
            System.out.println();
            
            // Show preview
            System.out.println("Preview (first 20 lines):");
            System.out.println("─────────────────────────────────────────────────────────────");
            String[] lines = code.split("\n");
            for (int i = 0; i < Math.min(20, lines.length); i++) {
                System.out.println(lines[i]);
            }
            if (lines.length > 20) {
                System.out.println("... (" + (lines.length - 20) + " more lines)");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to save: " + e.getMessage());
        }
    }
    
    /**
     * Handle prove commands (demonstrate real circuit execution)
     */
    private static void handleProve(String circuitName) {
        if (circuitName.isEmpty()) {
            System.err.println("❌ Usage: prove <circuit>");
            System.out.println("Example: prove xor");
            return;
        }
        
        System.out.println("\n🔬 PROVING REAL CIRCUIT EXECUTION");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        
        try {
            // Run the real circuit demo
            RealCircuitDemo.main(new String[]{});
        } catch (Exception e) {
            System.err.println("❌ Failed to prove circuit: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get file extension for language
     */
    private static String getExtension(String language) {
        switch (language.toLowerCase()) {
            case "java": return "java";
            case "python": return "py";
            case "cpp": case "c++": return "cpp";
            case "processing": return "pde";
            default: return "txt";
        }
    }
}
