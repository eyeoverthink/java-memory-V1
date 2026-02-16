package fraymus;

import fraymus.core.*;

/**
 * SPATIAL COMPUTING COMMAND HANDLERS
 * Handles all commands related to the Spatial Computing system:
 * - PhiNode, PhiSuit, Lazarus, GravityEngine, FusionReactor, CreativeEngine
 */
public class CommandTerminalSpatial {
    
    // Use singleton manager for shared CreativeEngine instance
    // (No local instance needed)
    
    /**
     * Handle spatial computing commands (CommandProcessor interface)
     */
    public static void handle(String command, String args) {
        String[] parts = args.trim().split("\\s+", 2);
        String sub = parts.length > 0 ? parts[0].toLowerCase() : "";
        String val = parts.length > 1 ? parts[1] : "";
        
        switch (sub) {
            case "":
            case "status":
                showSpatialStatus();
                break;
            case "start":
                startCreativeEngine();
                break;
            case "stop":
                stopCreativeEngine();
                break;
            default:
                System.err.println("Unknown spatial command: " + sub);
                System.out.println("Try: spatial start | stop | status");
                break;
        }
    }
    
    /**
     * Legacy method name for compatibility
     */
    public static void handleSpatial(String args) {
        handle("spatial", args);
    }
    
    /**
     * Handle gravity engine commands
     */
    public static void handleGravityEngine(String args) {
        if (!CreativeEngineManager.isRunning()) {
            CommandTerminal.printError("Creative Engine not running. Use 'spatial start' first.");
            return;
        }
        
        String[] parts = args.trim().split("\\s+", 2);
        String sub = parts.length > 0 ? parts[0].toLowerCase() : "";
        
        switch (sub) {
            case "":
            case "status":
                CreativeEngineManager.printStats();
                break;
            default:
                CommandTerminal.printError("Unknown gravity command: " + sub);
                CommandTerminal.print("Try: gravity status");
                break;
        }
    }
    
    /**
     * Handle fusion reactor commands
     */
    public static void handleFusionReactor(String args) {
        if (!CreativeEngineManager.isRunning()) {
            CommandTerminal.printError("Creative Engine not running. Use 'spatial start' first.");
            return;
        }
        
        String[] parts = args.trim().split("\\s+", 2);
        String sub = parts.length > 0 ? parts[0].toLowerCase() : "";
        
        switch (sub) {
            case "":
            case "status":
                FusionReactor reactor = CreativeEngineManager.getInstance().getFusionReactor();
                CommandTerminal.printHighlight("=== FUSION REACTOR STATUS ===");
                CommandTerminal.print("");
                CommandTerminal.print("  Total Fusions: " + reactor.getTotalFusions());
                CommandTerminal.print("  Last Fusion: " + 
                    (reactor.getLastFusionTime() > 0 ? 
                        ((System.currentTimeMillis() - reactor.getLastFusionTime()) / 1000) + "s ago" : 
                        "Never"));
                CommandTerminal.print("");
                break;
            default:
                CommandTerminal.printError("Unknown fusion command: " + sub);
                CommandTerminal.print("Try: fusion status");
                break;
        }
    }
    
    /**
     * Handle universe visualization commands
     */
    public static void handleUniverse(String args) {
        String[] parts = args.trim().split("\\s+", 2);
        String sub = parts.length > 0 ? parts[0].toLowerCase() : "";
        String val = parts.length > 1 ? parts[1] : "";
        
        switch (sub) {
            case "":
                Lazarus.printUniverse();
                break;
            case "hot":
                showHotNodes();
                break;
            case "region":
                showRegion(val);
                break;
            default:
                CommandTerminal.printError("Unknown universe command: " + sub);
                CommandTerminal.print("Try: universe | universe hot | universe region <x1> <x2> <y1> <y2> <z1> <z2>");
                break;
        }
    }
    
    /**
     * Handle concept injection
     */
    public static void handleInjectConcept(String args) {
        if (!CreativeEngineManager.isRunning()) {
            CommandTerminal.printError("Creative Engine not running. Use 'spatial start' first.");
            return;
        }
        
        String[] parts = args.trim().split("\\s+");
        if (parts.length < 4) {
            CommandTerminal.printError("Usage: inject <data> <x> <y> <z>");
            return;
        }
        
        try {
            String data = parts[0];
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            
            PhiSuit<String> concept = CreativeEngineManager.getInstance().inject(data, x, y, z);
            CommandTerminal.printSuccess("Injected concept: " + concept.toString());
        } catch (NumberFormatException e) {
            CommandTerminal.printError("Invalid coordinates. Use integers for x, y, z");
        }
    }
    
    // ========================================================================
    // PRIVATE HELPERS
    // ========================================================================
    
    private static void showSpatialStatus() {
        CommandTerminal.printHighlight("=== SPATIAL COMPUTING STATUS ===");
        CommandTerminal.print("");
        
        if (CreativeEngineManager.isRunning()) {
            CommandTerminal.printSuccess("  Status: RUNNING");
            CommandTerminal.print("");
            CreativeEngineManager.printStats();
        } else {
            CommandTerminal.printColored("  Status: STOPPED", 1.0f, 0.5f, 0.0f);
            CommandTerminal.print("  Use 'spatial start' to initialize");
        }
        
        CommandTerminal.print("");
        CommandTerminal.print("Components:");
        CommandTerminal.print("  ✓ PhiNode - Base particle with 5D coordinates");
        CommandTerminal.print("  ✓ PhiSuit - Data exoskeleton wrapper");
        CommandTerminal.print("  ✓ Lazarus - Registry of all souls");
        CommandTerminal.print("  ✓ GravityEngine - Hebbian physics (F = φ · A₁·A₂ / d²)");
        CommandTerminal.print("  ✓ FusionReactor - Creative synthesis engine");
        CommandTerminal.print("");
    }
    
    private static void startCreativeEngine() {
        if (CreativeEngineManager.isRunning()) {
            CommandTerminal.printColored("Creative Engine already running", 1.0f, 0.5f, 0.0f);
            return;
        }
        
        CommandTerminal.printInfo("Starting Creative Engine...");
        CreativeEngineManager.start();
        
        CommandTerminal.printSuccess("Creative Engine ONLINE");
        CommandTerminal.print("");
        CommandTerminal.print("The Particle Collider for Thoughts is now active.");
        CommandTerminal.print("Use 'inject <data> <x> <y> <z>' to add concepts.");
        CommandTerminal.print("Watch them drift together through gravity and fuse into new ideas.");
        CommandTerminal.print("");
    }
    
    private static void stopCreativeEngine() {
        if (!CreativeEngineManager.isRunning()) {
            CommandTerminal.printError("Creative Engine not running");
            return;
        }
        
        CommandTerminal.printInfo("Stopping Creative Engine...");
        CreativeEngineManager.stop();
        CommandTerminal.printSuccess("Creative Engine STOPPED");
    }
    
    private static void showHotNodes() {
        CommandTerminal.printHighlight("=== HOTTEST NODES (Most Accessed) ===");
        CommandTerminal.print("");
        
        java.util.List<PhiNode> hottest = Lazarus.getHottestNodes(10);
        if (hottest.isEmpty()) {
            CommandTerminal.print("  No nodes in universe");
        } else {
            for (int i = 0; i < hottest.size(); i++) {
                PhiNode node = hottest.get(i);
                CommandTerminal.printColored(
                    String.format("  %d. %s", i+1, node.toString()),
                    1.0f, 0.84f, 0.0f
                );
            }
        }
        CommandTerminal.print("");
    }
    
    private static void showRegion(String args) {
        String[] parts = args.trim().split("\\s+");
        if (parts.length < 6) {
            CommandTerminal.printError("Usage: universe region <x1> <x2> <y1> <y2> <z1> <z2>");
            return;
        }
        
        try {
            int x1 = Integer.parseInt(parts[0]);
            int x2 = Integer.parseInt(parts[1]);
            int y1 = Integer.parseInt(parts[2]);
            int y2 = Integer.parseInt(parts[3]);
            int z1 = Integer.parseInt(parts[4]);
            int z2 = Integer.parseInt(parts[5]);
            
            java.util.List<PhiNode> nodes = Lazarus.getNodesInRegion(x1, x2, y1, y2, z1, z2);
            
            CommandTerminal.printHighlight(String.format(
                "=== NODES IN REGION (%d,%d,%d) to (%d,%d,%d) ===", 
                x1, y1, z1, x2, y2, z2
            ));
            CommandTerminal.print("");
            
            if (nodes.isEmpty()) {
                CommandTerminal.print("  No nodes in this region");
            } else {
                CommandTerminal.print("  Found " + nodes.size() + " nodes:");
                for (PhiNode node : nodes) {
                    CommandTerminal.print("  " + node.toString());
                }
            }
            CommandTerminal.print("");
        } catch (NumberFormatException e) {
            CommandTerminal.printError("Invalid coordinates. Use integers.");
        }
    }
}
