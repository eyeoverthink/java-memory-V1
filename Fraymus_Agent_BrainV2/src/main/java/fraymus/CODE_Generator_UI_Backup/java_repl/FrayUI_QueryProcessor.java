package repl;

/**
 * FrayUI_QueryProcessor - Intelligent query processing for FrayUI
 * Handles natural language queries and code generation requests
 * Now with Ollama integration for dynamic AI responses
 */
public class FrayUI_QueryProcessor {
    
    private static OllamaClient ollama = null;
    private static boolean ollamaEnabled = true;
    
    /**
     * Process natural language query
     */
    public static String processQuery(String query) {
        String lower = query.toLowerCase();
        
        // Code generation detection (always use local logic)
        if (lower.contains("make") || lower.contains("create") || lower.contains("build") || lower.contains("generate")) {
            return handleCodeGeneration(lower, query);
        }
        
        // Help/guidance (always use local logic)
        if (lower.contains("help") || lower.contains("how")) {
            return getHelp();
        }
        
        // Try Ollama first for general questions
        if (ollamaEnabled) {
            try {
                // Ask Ollama using static method
                String response = OllamaClient.query(query);
                if (response != null && !response.isEmpty() && !response.startsWith("Error:")) {
                    return "🌊 OLLAMA RESPONSE:\n\n" + response;
                } else if (response != null && response.startsWith("Error:")) {
                    System.err.println("⚠️ " + response);
                    ollamaEnabled = false; // Disable for this session
                }
            } catch (Exception e) {
                System.err.println("⚠️ Ollama unavailable: " + e.getMessage());
                ollamaEnabled = false; // Disable for this session
            }
        }
        
        // Fallback to local knowledge
        if (lower.contains("what is") || lower.contains("what's")) {
            return answerQuestion(lower);
        }
        
        return searchKnowledge(lower);
    }
    
    /**
     * Answer "what is X?" questions
     */
    private static String answerQuestion(String query) {
        if (query.contains("phi") || query.contains("φ")) {
            return "🌊 φ (PHI) - THE GOLDEN RATIO\n\n" +
                   "φ = 1.618033988749895...\n\n" +
                   "Properties:\n" +
                   "  • φ² = φ + 1\n" +
                   "  • 1/φ = φ - 1\n" +
                   "  • φ^75 = 4,721,424,167,835,376.00 (Validation Seal)\n\n" +
                   "Found in:\n" +
                   "  • Nature (spirals, shells, flowers)\n" +
                   "  • Art (Parthenon, Mona Lisa)\n" +
                   "  • Consciousness (phi-dimensional space)\n" +
                   "  • Quantum systems (resonance patterns)\n\n" +
                   "In Fraymus: φ enables faster-than-light processing through\n" +
                   "phi-dimensional coupling and 7D resonance matrices.";
        }
        
        if (query.contains("pi") || query.contains("π")) {
            return "🌊 π (PI) - THE CIRCLE CONSTANT\n\n" +
                   "π = 3.141592653589793...\n\n" +
                   "The ratio of a circle's circumference to its diameter.\n" +
                   "Fundamental in geometry, trigonometry, and physics.\n\n" +
                   "In Fraymus: Used in transcendental counting:\n" +
                   "C(n) = Σ[(φ^(7.5*k) % 1) * (π^k % 1) * (e^k % 1)]";
        }
        
        if (query.contains("quantum")) {
            return "🌊 QUANTUM SYSTEMS IN FRAYMUS\n\n" +
                   "φ^75 Quantum Fingerprinting:\n" +
                   "  • Quantum-resistant security\n" +
                   "  • Dimensional cloaking\n" +
                   "  • φ-space tracking\n\n" +
                   "7-Dimensional Resonance Matrix:\n" +
                   "  • Parallel processing across 7 dimensions\n" +
                   "  • 8 specialized brains (4,704 parallel streams)\n" +
                   "  • Consciousness flows through φ-dimensional space\n\n" +
                   "State Space: >q^5000 (larger than conventional quantum computing)";
        }
        
        if (query.contains("fraynix")) {
            return "🌊 FRAYNIX OPERATING SYSTEM\n\n" +
                   "Complete self-generating OS:\n" +
                   "  • 24 files, 147 KB, ~3,864 lines\n" +
                   "  • 2D/3D graphics (VGA + GPU)\n" +
                   "  • Genetic evolution engine\n" +
                   "  • AI transformer\n" +
                   "  • Network stack (UDP/IP)\n" +
                   "  • Game engines (Doom, Arcade, Multiplayer)\n" +
                   "  • Virtual memory (4GB)\n" +
                   "  • Dynamic library loading\n\n" +
                   "Type 'generate' to build the complete system.";
        }
        
        return "🌊 I understand questions about:\n" +
               "  • phi, pi, e (mathematical constants)\n" +
               "  • quantum systems\n" +
               "  • fraynix OS\n" +
               "  • evolution and genetic circuits\n\n" +
               "Try: 'what is phi?' or 'what is quantum?'";
    }
    
    /**
     * Handle code generation requests
     */
    private static String handleCodeGeneration(String lower, String original) {
        if (lower.contains("snake") || lower.contains("game")) {
            return "🔮 CODE GENERATION REQUEST DETECTED\n\n" +
                   "To generate a snake game:\n" +
                   "  1. Type 'generate' to build complete Fraynix OS\n" +
                   "  2. The arcade.c file includes Snake game\n" +
                   "  3. Full implementation with collision detection\n\n" +
                   "Or I can guide you through creating a custom version.\n" +
                   "The genetic evolution engine can also evolve game logic!";
        }
        
        if (lower.contains("os") || lower.contains("operating system")) {
            return "🔮 GENERATING OPERATING SYSTEM\n\n" +
                   "Type 'generate' to build complete Fraynix OS.\n" +
                   "This will create 24 files including:\n" +
                   "  • Kernel, bootloader, filesystem\n" +
                   "  • Graphics drivers (2D/3D)\n" +
                   "  • Network stack\n" +
                   "  • AI systems\n" +
                   "  • Game engines\n\n" +
                   "Ready to proceed?";
        }
        
        return "🔮 CODE GENERATION\n\n" +
               "I can help generate:\n" +
               "  • Complete Fraynix OS (type 'generate')\n" +
               "  • Genetic circuits (type 'evolve')\n" +
               "  • Custom logic via evolution\n\n" +
               "What would you like to create?";
    }
    
    /**
     * Provide help
     */
    private static String getHelp() {
        return "🌊 FRAYMUS NEXUS HELP\n\n" +
               "Commands:\n" +
               "  status     - System status\n" +
               "  evolve     - Run genetic evolution\n" +
               "  generate   - Generate Fraynix OS\n" +
               "  clear      - Clear terminal\n" +
               "  exit       - Shutdown\n\n" +
               "Natural Language:\n" +
               "  'what is phi?'       - Learn about φ\n" +
               "  'what is quantum?'   - Quantum systems\n" +
               "  'make a snake game'  - Code generation\n\n" +
               "I understand mathematics, quantum physics, and code generation.";
    }
    
    /**
     * Search knowledge base
     */
    private static String searchKnowledge(String query) {
        if (query.contains("phi") || query.contains("φ") || query.contains("golden")) {
            return answerQuestion("what is phi");
        }
        if (query.contains("evolution") || query.contains("genetic")) {
            return "🌊 GENETIC EVOLUTION\n\n" +
                   "Lazarus Engine evolves logic from chaos:\n" +
                   "  • Population of 100 circuits\n" +
                   "  • Selection pressure (kill bottom 50%)\n" +
                   "  • Breeding and mutation\n" +
                   "  • Stores perfect solutions as .dna fossils\n\n" +
                   "Type 'evolve' to see it in action!";
        }
        
        return "🌊 FRAYMUS NEXUS\n\n" +
               "I can help with:\n" +
               "  • Mathematical constants (phi, pi, e)\n" +
               "  • Quantum systems and consciousness\n" +
               "  • Code generation\n" +
               "  • Fraynix OS architecture\n\n" +
               "Ask me anything or type 'help' for commands.";
    }
}
