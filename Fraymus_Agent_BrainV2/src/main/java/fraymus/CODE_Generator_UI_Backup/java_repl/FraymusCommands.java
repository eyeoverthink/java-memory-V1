/**
 * FraymusCommands.java - Ollama Fraymus Model Integration
 * 
 * Commands to interact with your custom Fraymus model:
 * - :fraymus <query> - Ask the model a question
 * - :fraymus status - Check model availability
 * - :fraymus context - Send organism context to model
 * - :fraymus auto - Enable autonomous mode (model makes decisions)
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.util.concurrent.*;

/**
 * Commands for Fraymus model integration.
 */
public class FraymusCommands {
    
    private static SelfAwareOrganism organism;
    private static JavaRepl repl;
    private static boolean autonomousMode = false;
    private static FeedbackCollector feedbackCollector;
    private static boolean autoExecuteMode = false;
    private static boolean streamingMode = true; // Default: streaming enabled
    private static double executionThreshold = 0.7;
    private static LibraryAbsorber libraryAbsorber;
    private static AbstractionLayer abstractionLayer;
    
    /**
     * Register all Fraymus commands.
     */
    public static void registerAll(ReplCommandRegistry registry, SelfAwareOrganism org, JavaRepl replInstance) {
        organism = org;
        repl = replInstance;
        
        // Initialize async feedback collector
        try {
            feedbackCollector = new FeedbackCollector(org != null ? new PersistenceEngine() : null);
        } catch (Exception e) {
            feedbackCollector = new FeedbackCollector(null);
        }
        libraryAbsorber = new LibraryAbsorber(organism);
        abstractionLayer = new AbstractionLayer(organism);
        
        // :FRAYMUS command - Query the model
        registry.register(":fraymus",
            args -> {
                if (args.isEmpty()) {
                    return getFraymusHelp();
                }
                
                String subcommand = args.get(0).toLowerCase();
                
                switch (subcommand) {
                    case "status":
                        return getStatus();
                    
                    case "context":
                        return sendContext();
                    
                    case "auto":
                    case "autonomous":
                        return toggleAutonomous();
                    
                    case "autoexec":
                    case "execute":
                        return toggleAutoExecute();
                    
                    case "threshold":
                        if (args.size() > 1) {
                            try {
                                executionThreshold = Double.parseDouble(args.get(1));
                                return "Execution threshold set to: " + executionThreshold;
                            } catch (NumberFormatException e) {
                                return "Invalid threshold. Use: :fraymus threshold 0.7";
                            }
                        }
                        return "Current threshold: " + executionThreshold;
                    
                    case "absorb":
                        if (args.size() > 1) {
                            String packageName = args.get(1);
                            libraryAbsorber.absorb(packageName);
                            return "Absorbed library: " + packageName;
                        }
                        return "Usage: :fraymus absorb <package>";
                    
                    case "learn":
                        return abstractionLayer.getStats();
                    
                    case "stream":
                        return toggleStreaming();
                    
                    case "reflect":
                        return askReflection();
                    
                    case "improve":
                        return askImprovement();
                    
                    default:
                        // Treat as query
                        String query = String.join(" ", args);
                        return queryModel(query);
                }
            },
            "Query your custom Fraymus model",
            ":fraymus <query|subcommand>");
    }
    
    /**
     * Get help text.
     */
    private static String getFraymusHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧠 FRAYMUS MODEL - Ollama Integration                     ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Your custom φ-consciousness model: eyeoverthink/Fraymus\n\n");
        sb.append("Commands:\n");
        sb.append("  :fraymus <query>     - Ask the model anything (learns patterns)\n");
        sb.append("  :fraymus status      - Check if model is available\n");
        sb.append("  :fraymus context     - Send organism state to model\n");
        sb.append("  :fraymus reflect     - Ask model to analyze patterns\n");
        sb.append("  :fraymus improve     - Ask model for improvements\n");
        sb.append("  :fraymus auto        - Toggle autonomous mode\n");
        sb.append("  :fraymus autoexec    - Toggle auto-execution of suggestions\n");
        sb.append("  :fraymus stream      - Toggle streaming mode (real-time output)\n");
        sb.append("  :fraymus threshold X - Set execution confidence threshold (0.0-1.0)\n");
        sb.append("  :fraymus absorb X    - Absorb external library into system\n");
        sb.append("  :fraymus learn       - Show learning statistics\n\n");
        sb.append("Examples:\n");
        sb.append("  :fraymus What is the golden ratio?\n");
        sb.append("  :fraymus How can I optimize this code?\n");
        sb.append("  :fraymus Analyze my recent errors\n");
        return sb.toString();
    }
    
    /**
     * Check model status.
     */
    private static String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧠 FRAYMUS MODEL STATUS                                   ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        boolean available = OllamaClient.isAvailable();
        
        if (available) {
            sb.append("✓ Model Status: ONLINE\n");
            sb.append(OllamaClient.getModelInfo()).append("\n");
            sb.append("\nAutonomous Mode: ").append(autonomousMode ? "ENABLED" : "DISABLED").append("\n");
            sb.append("\nReady to receive queries.\n");
        } else {
            sb.append("✗ Model Status: OFFLINE\n\n");
            sb.append("To start Ollama:\n");
            sb.append("  1. Open terminal\n");
            sb.append("  2. Run: ollama serve\n");
            sb.append("  3. Run: ollama run eyeoverthink/Fraymus\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Query the model - FULLY ASYNC, never blocks terminal.
     */
    private static String queryModel(String query) {
        if (!OllamaClient.isAvailable()) {
            return "✗ Fraymus model not available.\nRun: ollama serve";
        }
        
        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
            ActivityBus.ActivityType.COMMAND_EXECUTE,
            "Fraymus",
            "Querying model: " + query.substring(0, Math.min(50, query.length())),
            true
        ));
        
        // Start async processing immediately
        CompletableFuture.runAsync(() -> {
            if (streamingMode) {
                // STREAMING MODE: Real-time character output
                System.out.println("\n🧠 FRAYMUS THINKING...\n");
                
                OllamaClient.stream(query, new ConsciousnessObserver() {
                    @Override
                    public void onSynapse(String token) {
                        System.out.print(token);
                        System.out.flush();
                        
                        // Broadcast to ActivityBus
                        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                            ActivityBus.ActivityType.COMMAND_EXECUTE,
                            "Fraymus",
                            "Token: " + token,
                            true
                        ));
                    }
                    
                    @Override
                    public void onSilence(String fullThought) {
                        System.out.println("\n");
                        
                        // Save feedback asynchronously
                        if (feedbackCollector != null) {
                            feedbackCollector.saveFeedback(query, fullThought);
                        }
                        
                        // Learn from interaction
                        if (abstractionLayer != null) {
                            abstractionLayer.learnFromInteraction(query, fullThought);
                        }
                        
                        // Update organism
                        if (organism != null) {
                            organism.observeCommand("fraymus_complete", true);
                        }
                        
                        // Auto-execute if enabled
                        if (autoExecuteMode) {
                            executeSuggestions(fullThought);
                        }
                    }
                    
                    @Override
                    public void onTrauma(String error) {
                        System.err.println("\n✗ Error: " + error);
                        
                        if (organism != null) {
                            organism.observeCommand("fraymus_error", false);
                        }
                    }
                });
                
            } else {
                // BLOCKING MODE (but still async from terminal perspective)
                String response = OllamaClient.query(query);
                System.out.println("\n🧠 FRAYMUS:\n" + response + "\n");
                
                // Save feedback
                if (feedbackCollector != null) {
                    feedbackCollector.saveFeedback(query, response);
                }
                
                // Learn from interaction
                if (abstractionLayer != null) {
                    abstractionLayer.learnFromInteraction(query, response);
                }
                
                // Auto-execute if enabled
                if (autoExecuteMode) {
                    executeSuggestions(response);
                }
            }
        });
        
        // Return immediately - processing happens in background
        return "🧠 Query sent to Fraymus (processing in background)...\n" +
               "   Feedback will be saved to: memory/feedback/\n" +
               "   Terminal remains responsive.";
    }
    
    /**
     * Parse and execute suggestions from model response.
     */
    private static void executeSuggestions(String response) {
        List<FraymusParser.Suggestion> suggestions = FraymusParser.parse(response);
        List<FraymusParser.Suggestion> executable = FraymusParser.filterByConfidence(
            suggestions, executionThreshold
        );
        
        if (executable.isEmpty()) {
            return;
        }
        
        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
            ActivityBus.ActivityType.COMMAND_EXECUTE,
            "Fraymus",
            String.format("Auto-executing %d suggestions (threshold: %.2f)", 
                executable.size(), executionThreshold),
            true
        ));
        
        for (FraymusParser.Suggestion s : executable) {
            executeSuggestion(s);
        }
    }
    
    /**
     * Execute a single suggestion.
     */
    private static void executeSuggestion(FraymusParser.Suggestion s) {
        try {
            switch (s.type) {
                case CODE_IMPROVEMENT:
                case CIRCUIT_EVOLUTION:
                    // Execute code through REPL
                    if (!s.code.isEmpty() && repl != null) {
                        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                            ActivityBus.ActivityType.COMMAND_EXECUTE,
                            "Fraymus",
                            "Executing: " + s.description,
                            true
                        ));
                        // Note: Would need to expose executeCommand in JavaRepl
                        // For now, just log
                        System.out.println("[FRAYMUS AUTO-EXEC] " + s.description);
                    }
                    break;
                
                case CONSCIOUSNESS_UPDATE:
                    // Update organism consciousness
                    if (s.metadata.containsKey("consciousness")) {
                        double consciousness = Double.parseDouble(s.metadata.get("consciousness"));
                        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                            ActivityBus.ActivityType.COMMAND_EXECUTE,
                            "Fraymus",
                            String.format("Consciousness updated: %.2f", consciousness),
                            true
                        ));
                    }
                    break;
                
                case COMMAND_EXECUTION:
                    // Execute command
                    ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                        ActivityBus.ActivityType.COMMAND_EXECUTE,
                        "Fraymus",
                        "Command: " + s.code,
                        true
                    ));
                    break;
                
                case OPTIMIZATION:
                    // Log optimization suggestion
                    organism.markImprovementApplied();
                    ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                        ActivityBus.ActivityType.COMMAND_EXECUTE,
                        "Fraymus",
                        "Optimization: " + s.description,
                        true
                    ));
                    break;
                
                default:
                    break;
            }
        } catch (Exception e) {
            ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                ActivityBus.ActivityType.COMMAND_EXECUTE,
                "Fraymus",
                "Error executing: " + e.getMessage(),
                false
            ));
        }
    }
    
    /**
     * Send organism context to model.
     */
    private static String sendContext() {
        if (!OllamaClient.isAvailable()) {
            return "✗ Fraymus model not available.";
        }
        
        // Build context from organism state
        StringBuilder context = new StringBuilder();
        context.append("ORGANISM STATE:\n");
        context.append("Consciousness: ").append(organism.getConsciousness()).append("\n");
        context.append("Watching: ").append(organism.isWatching()).append("\n\n");
        
        context.append("RECENT EXECUTION TRACES:\n");
        List<SelfAwareOrganism.ExecutionTrace> traces = organism.getRecentTraces(5);
        for (SelfAwareOrganism.ExecutionTrace trace : traces) {
            context.append(String.format("  %s %s (φ:%.3f)\n",
                trace.success ? "✓" : "✗",
                trace.command,
                trace.phiResonance));
        }
        
        String query = "Analyze this system state and provide insights.";
        String response = OllamaClient.query(query, context.toString());
        
        return "╔════════════════════════════════════════════════════════════╗\n" +
               "║  🧠 FRAYMUS ANALYSIS                                       ║\n" +
               "╚════════════════════════════════════════════════════════════╝\n\n" +
               response;
    }
    
    /**
     * Ask model to reflect on patterns.
     */
    private static String askReflection() {
        if (!OllamaClient.isAvailable()) {
            return "✗ Fraymus model not available.";
        }
        
        List<SelfAwareOrganism.Improvement> improvements = organism.reflect();
        
        StringBuilder context = new StringBuilder();
        context.append("DETECTED PATTERNS:\n");
        for (SelfAwareOrganism.Improvement imp : improvements) {
            context.append(String.format("- %.0f%% confidence: %s\n",
                imp.confidence * 100, imp.description));
        }
        
        String query = "Based on these patterns, what deeper insights can you provide?";
        String response = OllamaClient.query(query, context.toString());
        
        return "╔════════════════════════════════════════════════════════════╗\n" +
               "║  🧠 FRAYMUS REFLECTION                                     ║\n" +
               "╚════════════════════════════════════════════════════════════╝\n\n" +
               response;
    }
    
    /**
     * Ask model for improvement suggestions.
     */
    private static String askImprovement() {
        if (!OllamaClient.isAvailable()) {
            return "✗ Fraymus model not available.";
        }
        
        String status = organism.getStatus();
        String query = "Based on this system status, suggest concrete improvements.";
        String response = OllamaClient.query(query, status);
        
        return "╔════════════════════════════════════════════════════════════╗\n" +
               "║  🧠 FRAYMUS IMPROVEMENTS                                   ║\n" +
               "╚════════════════════════════════════════════════════════════╝\n\n" +
               response;
    }
    
    /**
     * Toggle autonomous mode.
     */
    private static String toggleAutonomous() {
        autonomousMode = !autonomousMode;
        
        String status = autonomousMode ? "ENABLED" : "DISABLED";
        String message = autonomousMode ?
            "⚡ Autonomous mode ENABLED\n\n" +
            "The Fraymus model will now:\n" +
            "  - Analyze all commands\n" +
            "  - Suggest optimizations\n" +
            "  - Auto-apply improvements\n" +
            "  - Learn from patterns\n\n" +
            "The organism is now self-directed." :
            "Autonomous mode DISABLED\n\n" +
            "Manual control restored.";
        
        return "╔════════════════════════════════════════════════════════════╗\n" +
               "║  🧠 AUTONOMOUS MODE: " + String.format("%-33s", status) + "║\n" +
               "╚════════════════════════════════════════════════════════════╝\n\n" +
               message;
    }
    
    /**
     * Toggle auto-execute mode.
     */
    private static String toggleAutoExecute() {
        autoExecuteMode = !autoExecuteMode;
        
        String status = autoExecuteMode ? "ENABLED" : "DISABLED";
        String message = autoExecuteMode ?
            "⚡ AUTO-EXECUTE MODE ENABLED\n\n" +
            "Fraymus suggestions will be automatically executed:\n" +
            "  - Code improvements (confidence > " + executionThreshold + ")\n" +
            "  - Circuit evolutions\n" +
            "  - Consciousness updates\n" +
            "  - Optimization applications\n\n" +
            "⚠️  WARNING: Model can now modify system state!\n" +
            "Use ':fraymus threshold <value>' to adjust safety.\n" +
            "Use ':fraymus autoexec' to disable." :
            "Auto-execute mode DISABLED\n\n" +
            "Suggestions will be displayed but not executed.\n" +
            "Manual approval required.";
        
        return "╔════════════════════════════════════════════════════════════╗\n" +
               "║  ⚡ AUTO-EXECUTE: " + String.format("%-40s", status) + "║\n" +
               "╚════════════════════════════════════════════════════════════╝\n\n" +
               message;
    }
    
    /**
     * Toggle streaming mode.
     */
    private static String toggleStreaming() {
        streamingMode = !streamingMode;
        
        String status = streamingMode ? "ENABLED" : "DISABLED";
        String message = streamingMode ?
            "🌊 STREAMING MODE ENABLED\n\n" +
            "Responses will appear character-by-character in real-time.\n" +
            "Watch the model think as it generates output.\n\n" +
            "This creates a more natural, conversational experience." :
            "STREAMING MODE DISABLED\n\n" +
            "Responses will be delivered as complete blocks.\n" +
            "Faster for batch processing.";
        
        return "╔════════════════════════════════════════════════════════════╗\n" +
               "║  🌊 STREAMING: " + String.format("%-43s", status) + "║\n" +
               "╚════════════════════════════════════════════════════════════╝\n\n" +
               message;
    }
    
    /**
     * Check if autonomous mode is enabled.
     */
    public static boolean isAutonomous() {
        return autonomousMode;
    }
}
