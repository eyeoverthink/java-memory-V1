package fraymus;

import fraymus.api.FraymusAPI;
import fraymus.core.CreativeEngineManager;

import java.io.IOException;

/**
 * FRAYMUS REST API COMMAND HANDLERS
 * Handles terminal commands for the Fraymus REST API
 */
public class CommandTerminalAPI {
    
    private static FraymusAPI api = null;
    private static ExperimentManager experimentManager = null;
    
    public static void setExperimentManager(ExperimentManager mgr) {
        experimentManager = mgr;
        if (api != null) {
            api.setExperimentManager(mgr);
        }
    }
    
    /**
     * Handle API commands (CommandProcessor interface)
     */
    public static void handle(String command, String args) {
        String[] parts = args.trim().split("\\s+", 2);
        String sub = parts.length > 0 ? parts[0].toLowerCase() : "";
        String val = parts.length > 1 ? parts[1] : "";
        
        switch (sub) {
            case "":
            case "status":
                showAPIStatus();
                break;
            case "start":
                startAPI(val);
                break;
            case "stop":
                stopAPI();
                break;
            case "help":
                showAPIHelp();
                break;
            default:
                System.err.println("Unknown api command: " + sub);
                System.out.println("Try: api start | stop | status | help");
                break;
        }
    }
    
    /**
     * Legacy method name for compatibility
     */
    public static void handleAPI(String args) {
        handle("api", args);
    }
    
    private static void showAPIStatus() {
        if (api == null) {
            CommandTerminal.printColored("  Status: NOT INITIALIZED", 1.0f, 0.5f, 0.0f);
            CommandTerminal.print("  Use 'api start' to initialize");
        } else if (api.isRunning()) {
            CommandTerminal.printSuccess("  Status: RUNNING");
            CommandTerminal.print("  Base URL: http://localhost:8080");
            CommandTerminal.print("  Documentation: http://localhost:8080/api/help");
            CommandTerminal.print("");
            CommandTerminal.print("  Quick test:");
            CommandTerminal.print("    curl http://localhost:8080/api/status");
        } else {
            CommandTerminal.printColored("  Status: STOPPED", 1.0f, 0.5f, 0.0f);
            CommandTerminal.print("  Use 'api start' to restart");
        }
    }
    
    private static void startAPI(String portStr) {
        if (api != null && api.isRunning()) {
            CommandTerminal.printColored("API already running", 1.0f, 0.5f, 0.0f);
            return;
        }
        
        int port = 8080;
        if (!portStr.isEmpty()) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                CommandTerminal.printError("Invalid port number: " + portStr);
                return;
            }
        }
        
        try {
            CommandTerminal.printInfo("Starting Fraymus REST API on port " + port + "...");
            api = new FraymusAPI(port);
            
            // Wire up subsystems using singleton manager
            api.setCreativeEngine(CreativeEngineManager.getInstance());
            if (experimentManager != null) {
                api.setExperimentManager(experimentManager);
            }
            
            api.start();
            
            CommandTerminal.printSuccess("Fraymus REST API ONLINE");
            CommandTerminal.print("");
            CommandTerminal.print("Test with:");
            CommandTerminal.print("  curl http://localhost:" + port + "/api/help");
            CommandTerminal.print("");
            CommandTerminal.print("See CURL_COMMANDS.md for complete documentation");
            CommandTerminal.print("");
        } catch (IOException e) {
            CommandTerminal.printError("Failed to start API: " + e.getMessage());
            api = null;
        }
    }
    
    private static void stopAPI() {
        if (api == null || !api.isRunning()) {
            CommandTerminal.printError("API not running");
            return;
        }
        
        CommandTerminal.printInfo("Stopping Fraymus REST API...");
        api.stop();
        CommandTerminal.printSuccess("Fraymus REST API STOPPED");
    }
    
    private static void showAPIHelp() {
        CommandTerminal.printHighlight("=== FRAYMUS REST API HELP ===");
        CommandTerminal.print("");
        CommandTerminal.print("The Fraymus REST API exposes all subsystems via HTTP endpoints.");
        CommandTerminal.print("This enables programmatic access and system analysis via curl.");
        CommandTerminal.print("");
        CommandTerminal.printColored("Terminal Commands:", 0.5f, 0.8f, 1.0f);
        CommandTerminal.print("  api start [port]    Start API server (default port: 8080)");
        CommandTerminal.print("  api stop            Stop API server");
        CommandTerminal.print("  api status          Check API status");
        CommandTerminal.print("  api help            Show this help");
        CommandTerminal.print("");
        CommandTerminal.printColored("Available Endpoints:", 0.5f, 0.8f, 1.0f);
        CommandTerminal.print("  GET  /api/help              Full API documentation");
        CommandTerminal.print("  GET  /api/status            System status");
        CommandTerminal.print("  GET  /api/spatial/status    Spatial computing status");
        CommandTerminal.print("  POST /api/spatial/start     Start Creative Engine");
        CommandTerminal.print("  GET  /api/universe/export   Export all PhiNodes (JSON)");
        CommandTerminal.print("  POST /api/inject            Inject concept");
        CommandTerminal.print("  GET  /api/memory/export     Export all memories");
        CommandTerminal.print("  GET  /api/genome/export     Export genome");
        CommandTerminal.print("  GET  /api/export/all        Export entire system");
        CommandTerminal.print("");
        CommandTerminal.printColored("Example curl commands:", 0.5f, 0.8f, 1.0f);
        CommandTerminal.print("  curl http://localhost:8080/api/status");
        CommandTerminal.print("  curl http://localhost:8080/api/universe/export");
        CommandTerminal.print("  curl -X POST http://localhost:8080/api/spatial/start");
        CommandTerminal.print("  curl -X POST http://localhost:8080/api/inject \\");
        CommandTerminal.print("    -H 'Content-Type: application/json' \\");
        CommandTerminal.print("    -d '{\"data\":\"Java\",\"x\":0,\"y\":0,\"z\":0}'");
        CommandTerminal.print("");
        CommandTerminal.print("See CURL_COMMANDS.md for complete documentation with examples.");
        CommandTerminal.print("");
    }
}
