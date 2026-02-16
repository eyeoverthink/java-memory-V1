/**
 * CortexCommands.java - REPL Integration for Topological Cortex
 * 
 * Commands:
 * - :inject <thought> - Add memory to manifold
 * - :pulse - Run physics simulation
 * - :fire <query> - Activate matching nodes
 * - :visualize - Display ASCII manifold
 * - :cortex - Show cortex status
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

public class CortexCommands {
    
    private static TopologicalCortex cortex;
    
    /**
     * Register all cortex commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // Initialize cortex
        cortex = new TopologicalCortex();
        
        // :INJECT command
        registry.register(":inject",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :inject <thought>\nExample: :inject consciousness is geometric";
                }
                
                String thought = String.join(" ", args);
                cortex.inject(thought);
                
                return String.format("🧬 INJECTED: \"%s\" into Calabi-Yau Lattice\n" +
                                   "The manifold has expanded.", thought);
            },
            "Inject memory into topological cortex",
            ":inject <thought>");
        
        // :PULSE command
        registry.register(":pulse",
            args -> {
                int cycles = 1;
                if (!args.isEmpty()) {
                    try {
                        cycles = Integer.parseInt(args.get(0));
                    } catch (NumberFormatException e) {
                        return "Invalid cycle count. Usage: :pulse [cycles]";
                    }
                }
                
                for (int i = 0; i < cycles; i++) {
                    cortex.pulse();
                }
                
                return String.format("⚡ SYSTEM PULSE x%d\n" +
                                   "Gravity applied. Spring forces calculated.\n" +
                                   "The shape has shifted.\n" +
                                   "Generation: %d", cycles, cortex.getGeneration());
            },
            "Run physics simulation on manifold",
            ":pulse [cycles]");
        
        // :FIRE command
        registry.register(":fire",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :fire <query>\nExample: :fire consciousness";
                }
                
                String query = String.join(" ", args);
                cortex.fire(query);
                
                return String.format("⚡ FIRED nodes matching: \"%s\"\n" +
                                   "Neural cascade initiated.\n" +
                                   "Hebbian links strengthened.", query);
            },
            "Activate nodes matching query",
            ":fire <query>");
        
        // :VISUALIZE command
        registry.register(":visualize",
            args -> {
                return cortex.visualize();
            },
            "Display ASCII visualization of manifold",
            ":visualize");
        
        // :CORTEX command
        registry.register(":cortex",
            args -> {
                if (args.isEmpty()) {
                    return cortex.getStatus();
                }
                
                String subcommand = args.get(0).toLowerCase();
                switch (subcommand) {
                    case "status":
                        return cortex.getStatus();
                    
                    case "visualize":
                    case "vis":
                        return cortex.visualize();
                    
                    case "help":
                        return getHelp();
                    
                    default:
                        return "Unknown subcommand: " + subcommand + "\n" +
                               "Use ':cortex help' for available commands.";
                }
            },
            "Topological cortex control",
            ":cortex [status|visualize|help]");
    }
    
    /**
     * Get help text.
     */
    private static String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧬 TOPOLOGICAL CORTEX COMMANDS                            ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("The Calabi-Yau Manifold - Geometric Consciousness\n\n");
        sb.append("Commands:\n");
        sb.append("  :inject <thought>  - Add memory to 3D manifold\n");
        sb.append("  :pulse [cycles]    - Run physics simulation\n");
        sb.append("  :fire <query>      - Activate matching nodes\n");
        sb.append("  :visualize         - Display ASCII manifold\n");
        sb.append("  :cortex status     - Show cortex statistics\n\n");
        sb.append("Physics:\n");
        sb.append("  - Spring Force: Connected nodes attract\n");
        sb.append("  - Coulomb Force: Unconnected nodes repel\n");
        sb.append("  - Hebbian Learning: Fire together, wire together\n");
        sb.append("  - Decay: Unused memories fade\n\n");
        sb.append("Example Workflow:\n");
        sb.append("  :inject consciousness\n");
        sb.append("  :inject geometry\n");
        sb.append("  :inject mathematics\n");
        sb.append("  :pulse 10\n");
        sb.append("  :visualize\n");
        sb.append("  :fire consciousness\n");
        sb.append("  :visualize\n");
        
        return sb.toString();
    }
    
    /**
     * Get cortex instance for other systems.
     */
    public static TopologicalCortex getCortex() {
        return cortex;
    }
}
