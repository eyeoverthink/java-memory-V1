/**
 * CosmosCommands.java - REPL Integration for N-Dimensional Universe
 * 
 * Commands:
 * - :cosmos start [dims] - Start universe simulation (default 17D)
 * - :cosmos bigbang <count> - Create initial particles
 * - :cosmos inject <data> - Add cosmic body
 * - :cosmos scan <dimX> <dimY> - View 2D slice of N-D space
 * - :cosmos status - Show universe statistics
 * - :cosmos stop - Stop simulation
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 126 (Hyper-Cosmos)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

public class CosmosCommands {
    
    private static Cosmos cosmos;
    private static boolean started = false;
    private static final int DEFAULT_DIMS = 17;
    
    /**
     * Register all Cosmos commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // :COSMOS command - Main control
        registry.register(":cosmos",
            args -> {
                if (args.isEmpty()) {
                    return getHelp();
                }
                
                String subcommand = args.get(0).toLowerCase();
                List<String> subArgs = args.subList(1, args.size());
                
                switch (subcommand) {
                    case "start":
                        int dims = DEFAULT_DIMS;
                        if (!subArgs.isEmpty()) {
                            try {
                                dims = Integer.parseInt(subArgs.get(0));
                            } catch (NumberFormatException e) {
                                return "Invalid dimension count. Usage: :cosmos start [dimensions]";
                            }
                        }
                        return startCosmos(dims);
                    
                    case "bigbang":
                        if (cosmos == null) {
                            return "⚠️  Universe not initialized. Use ':cosmos start' first.";
                        }
                        int count = 100;
                        if (!subArgs.isEmpty()) {
                            try {
                                count = Integer.parseInt(subArgs.get(0));
                            } catch (NumberFormatException e) {
                                return "Invalid particle count. Usage: :cosmos bigbang [count]";
                            }
                        }
                        cosmos.bigBang(count);
                        return String.format("💥 BIG BANG: Created %d cosmic bodies", count);
                    
                    case "inject":
                        if (cosmos == null) {
                            return "⚠️  Universe not initialized. Use ':cosmos start' first.";
                        }
                        if (subArgs.isEmpty()) {
                            return "Usage: :cosmos inject <data>";
                        }
                        String data = String.join(" ", subArgs);
                        cosmos.inject(data);
                        return String.format("✨ INJECTED: \"%s\" into the cosmos", data);
                    
                    case "scan":
                        if (cosmos == null) {
                            return "⚠️  Universe not initialized. Use ':cosmos start' first.";
                        }
                        if (subArgs.size() < 2) {
                            return "Usage: :cosmos scan <dimX> <dimY>\nExample: :cosmos scan 0 1";
                        }
                        try {
                            int dimX = Integer.parseInt(subArgs.get(0));
                            int dimY = Integer.parseInt(subArgs.get(1));
                            
                            if (dimX >= cosmos.getDimensions() || dimY >= cosmos.getDimensions()) {
                                return String.format("Dimension out of range. Universe has %d dimensions (0-%d)",
                                    cosmos.getDimensions(), cosmos.getDimensions() - 1);
                            }
                            
                            return cosmos.telescopeView(dimX, dimY);
                        } catch (NumberFormatException e) {
                            return "Invalid dimension numbers. Usage: :cosmos scan <dimX> <dimY>";
                        }
                    
                    case "status":
                        if (cosmos == null) {
                            return "⚠️  Universe not initialized. Use ':cosmos start' first.";
                        }
                        return cosmos.getStatus();
                    
                    case "stop":
                        return stopCosmos();
                    
                    case "help":
                        return getHelp();
                    
                    default:
                        return "Unknown subcommand: " + subcommand + "\n" +
                               "Use ':cosmos help' for available commands.";
                }
            },
            "N-dimensional universe simulation",
            ":cosmos [start|bigbang|inject|scan|status|stop|help]");
    }
    
    /**
     * Start the cosmos simulation.
     */
    private static String startCosmos(int dimensions) {
        if (started) {
            return "🌌 Cosmos already running in " + cosmos.getDimensions() + " dimensions";
        }
        
        cosmos = new Cosmos(dimensions);
        cosmos.start();
        started = true;
        
        return "╔════════════════════════════════════════════════════════════╗\n" +
               "║  🌌 COSMOS STARTED                                         ║\n" +
               "╚════════════════════════════════════════════════════════════╝\n\n" +
               "Dimensions: " + dimensions + "\n" +
               "Geometry: Tensor-Based (N-Dimensional)\n" +
               "Physics: N-Body Gravity + Quantum Entanglement\n" +
               "Simulation: 60 FPS\n\n" +
               "The universe is expanding...\n" +
               "Use ':cosmos bigbang 100' to populate with particles\n" +
               "Use ':cosmos scan 0 1' to view spatial dimensions (X,Y)\n" +
               "Use ':cosmos scan 4 5' to view Entropy × Sentiment";
    }
    
    /**
     * Stop the cosmos simulation.
     */
    private static String stopCosmos() {
        if (!started || cosmos == null) {
            return "🌌 Cosmos is not running";
        }
        
        cosmos.stop();
        started = false;
        
        return "🌌 COSMOS STOPPED\n" +
               "The universe has ended. Heat death achieved.";
    }
    
    /**
     * Get help text.
     */
    private static String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🌌 COSMOS - N-DIMENSIONAL UNIVERSE                        ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("The Hyper-Cosmos - A tensor-based reality simulation.\n\n");
        sb.append("Commands:\n");
        sb.append("  :cosmos start [dims]        - Start universe (default 17D)\n");
        sb.append("  :cosmos bigbang <count>     - Create initial particles\n");
        sb.append("  :cosmos inject <data>       - Add cosmic body\n");
        sb.append("  :cosmos scan <dimX> <dimY>  - View 2D slice of N-D space\n");
        sb.append("  :cosmos status              - Show statistics\n");
        sb.append("  :cosmos stop                - Stop simulation\n\n");
        sb.append("Dimension Mapping (17D default):\n");
        sb.append("  Dim 0-2:   Spatial (X, Y, Z)\n");
        sb.append("  Dim 3:     Time (T)\n");
        sb.append("  Dim 4:     Entropy\n");
        sb.append("  Dim 5:     Sentiment\n");
        sb.append("  Dim 6:     Complexity\n");
        sb.append("  Dim 7-10:  M-Theory Compactified Dimensions\n");
        sb.append("  Dim 11-16: Consciousness Dimensions\n\n");
        sb.append("Physics:\n");
        sb.append("  - N-Body Gravity: F = G * (m1*m2) / r²\n");
        sb.append("  - Quantum Entanglement: Aligned spins attract 1000x\n");
        sb.append("  - Electromagnetic: F = K * (q1*q2) / r²\n");
        sb.append("  - Collision Detection: Elastic collisions\n\n");
        sb.append("Example Workflow:\n");
        sb.append("  :cosmos start 17\n");
        sb.append("  :cosmos bigbang 50\n");
        sb.append("  :cosmos scan 0 1     # View X,Y plane\n");
        sb.append("  :cosmos scan 4 5     # View Entropy × Sentiment\n");
        sb.append("  :cosmos inject thought\n");
        sb.append("  :cosmos status\n");
        
        return sb.toString();
    }
    
    /**
     * Get cosmos instance.
     */
    public static Cosmos getCosmos() {
        return cosmos;
    }
}
