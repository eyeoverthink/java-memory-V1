package fraymus.core;

/**
 * 🎭 FRAY ORCHESTRATOR - Gen 163
 * The Intent Router
 * 
 * "Make me a snake game" -> FrayOrchestrator -> Code Generation
 * 
 * This class receives natural language intents from the UI and routes
 * them to the appropriate subsystem (code generation, file operations,
 * system commands, etc.)
 */
public class FrayOrchestrator {

    private static FrayOrchestrator instance;
    
    public static FrayOrchestrator getInstance() {
        if (instance == null) {
            instance = new FrayOrchestrator();
        }
        return instance;
    }

    /**
     * Main entry point for intent processing
     * @param intent Natural language command from user
     */
    public void manifestIntent(String intent) {
        System.out.println("🎭 ORCHESTRATOR PROCESSING...");
        System.out.println("   Intent: \"" + intent + "\"");
        
        // Classify intent
        IntentType type = classifyIntent(intent);
        System.out.println("   Type: " + type);
        
        // Route to appropriate handler
        switch (type) {
            case CODE_GENERATION:
                handleCodeGeneration(intent);
                break;
            case FILE_OPERATION:
                handleFileOperation(intent);
                break;
            case SYSTEM_COMMAND:
                handleSystemCommand(intent);
                break;
            case QUERY:
                handleQuery(intent);
                break;
            default:
                handleUnknown(intent);
        }
    }

    private IntentType classifyIntent(String intent) {
        String lower = intent.toLowerCase();
        
        if (lower.contains("make") || lower.contains("create") || lower.contains("build") || 
            lower.contains("generate") || lower.contains("write")) {
            return IntentType.CODE_GENERATION;
        }
        if (lower.contains("open") || lower.contains("save") || lower.contains("delete") ||
            lower.contains("file") || lower.contains("folder")) {
            return IntentType.FILE_OPERATION;
        }
        if (lower.contains("run") || lower.contains("execute") || lower.contains("start") ||
            lower.contains("stop") || lower.contains("kill")) {
            return IntentType.SYSTEM_COMMAND;
        }
        if (lower.contains("what") || lower.contains("how") || lower.contains("why") ||
            lower.contains("show") || lower.contains("list")) {
            return IntentType.QUERY;
        }
        
        return IntentType.UNKNOWN;
    }

    private void handleCodeGeneration(String intent) {
        System.out.println("   📝 Routing to Code Generator...");
        System.out.println("   [Hook to OllamaBridge / Ouroboros Compiler here]");
        // OllamaBridge.generate(intent);
    }

    private void handleFileOperation(String intent) {
        System.out.println("   📁 Routing to File System...");
        System.out.println("   [Hook to FrayFS here]");
    }

    private void handleSystemCommand(String intent) {
        System.out.println("   ⚙️ Routing to System Controller...");
        System.out.println("   [Hook to ProcessBuilder / Runtime here]");
    }

    private void handleQuery(String intent) {
        System.out.println("   🔍 Routing to Knowledge Base...");
        System.out.println("   [Hook to SovereignMind / Vector DB here]");
    }

    private void handleUnknown(String intent) {
        System.out.println("   ❓ Unknown intent type");
        System.out.println("   Attempting general processing...");
    }

    public enum IntentType {
        CODE_GENERATION,
        FILE_OPERATION,
        SYSTEM_COMMAND,
        QUERY,
        UNKNOWN
    }

    public static void main(String[] args) {
        FrayOrchestrator orchestrator = FrayOrchestrator.getInstance();
        
        // Test intents
        orchestrator.manifestIntent("Make me a snake game");
        System.out.println();
        orchestrator.manifestIntent("Open the config file");
        System.out.println();
        orchestrator.manifestIntent("What is my current fitness?");
    }
}
