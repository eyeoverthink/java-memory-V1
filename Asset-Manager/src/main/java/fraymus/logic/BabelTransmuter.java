package fraymus.logic;

import java.util.*;

/**
 * 🔬 BABEL TRANSMUTER - Natural Language to C-ISA Compiler
 * 
 * Uses LLM as a syntactic parser to compile human intent into Cognitive Instruction Set Architecture.
 * 
 * Philosophy:
 * - LLM is not a thinker, it's a compiler
 * - Language is compressed interface to logic
 * - Compile once, execute deterministically
 * 
 * Pipeline:
 * 1. Human Input (natural language)
 * 2. LLM Parser (extracts intent, generates AST)
 * 3. C-ISA Compiler (generates instruction sequence)
 * 4. Logic Engine (deterministic execution)
 * 
 * ALPHA GO MATH INTEGRATION:
 * When enabled, uses Monte Carlo Tree Search (MCTS) with PUCT to intelligently select
 * C-ISA instruction sequences. Instead of pattern matching, it evaluates 50 potential
 * sequences in a simulated tree and selects the one with the highest Q-value.
 */
public class BabelTransmuter {
    
    // Intent patterns and their C-ISA mappings (LinkedHashMap preserves insertion order)
    private Map<String, IntentPattern> intentPatterns;
    
    // LLM integration (placeholder - would use actual LLM in production)
    private boolean useLLM;
    
    // Alpha Go Math integration for intelligent sequence selection
    private AlphaGoMath alphaGoMath;
    private boolean useMCTS;
    
    /**
     * Initialize the Babel Transmuter
     */
    public void initialize() {
        intentPatterns = new LinkedHashMap<>();
        registerIntentPatterns();
        useLLM = false;  // Start with pattern matching, can enable LLM later
        useMCTS = false; // Start with pattern matching, can enable MCTS later
        alphaGoMath = new AlphaGoMath();

        System.out.println("[BabelTransmuter] Initialized - Natural language to C-ISA compiler ready");
    }
    
    /**
     * Register intent patterns for pattern matching
     * Order matters: more specific patterns should be registered before generic ones
     */
    private void registerIntentPatterns() {
        // Weather check pattern (most specific - check first)
        intentPatterns.put("weather_check", new IntentPattern(
            Arrays.asList("weather", "raining", "check", "find out"),
            this::compileWeatherCheck
        ));
        
        // Conditional action pattern (specific)
        intentPatterns.put("conditional_action", new IntentPattern(
            Arrays.asList("if", "then", "else", "otherwise"),
            this::compileConditional
        ));
        
        // Loop pattern (specific)
        intentPatterns.put("loop", new IntentPattern(
            Arrays.asList("loop", "repeat", "while"),
            this::compileLoop
        ));
        
        // Actuate pattern (specific)
        intentPatterns.put("actuate", new IntentPattern(
            Arrays.asList("actuate", "execute", "run", "trigger"),
            this::compileActuate
        ));
        
        // Compare pattern (specific)
        intentPatterns.put("compare", new IntentPattern(
            Arrays.asList("compare", "check", "equal", "greater", "less"),
            this::compileCompare
        ));
        
        // Store pattern (generic)
        intentPatterns.put("store", new IntentPattern(
            Arrays.asList("store", "save", "remember"),
            this::compileStore
        ));
        
        // Load pattern (generic)
        intentPatterns.put("load", new IntentPattern(
            Arrays.asList("load", "get", "retrieve"),
            this::compileLoad
        ));
        
        // Wait pattern (most generic - check last)
        intentPatterns.put("wait", new IntentPattern(
            Arrays.asList("wait", "pause", "sleep"),
            this::compileWait
        ));
    }
    
    /**
     * Transmute natural language to C-ISA program
     */
    public CognitiveISA.Program transmute(String naturalLanguage) {
        System.out.println("[BabelTransmuter] Transmuting: \"" + naturalLanguage + "\"");
        
        // If MCTS is enabled, use Alpha Go Math
        if (useMCTS) {
            return transmuteWithMCTS(naturalLanguage);
        }
        
        // Detect intent
        String intent = detectIntent(naturalLanguage);
        System.out.println("[BabelTransmuter] Detected intent: " + intent);
        
        // Compile to C-ISA
        IntentPattern pattern = intentPatterns.get(intent);
        if (pattern != null) {
            CognitiveISA.Program program = pattern.compiler.compile(naturalLanguage);
            System.out.println("[BabelTransmuter] Generated C-ISA program:\n" + program);
            return program;
        }
        
        // Fallback: simple direct compilation
        return compileDirect(naturalLanguage);
    }
    
    /**
     * Transmute using MCTS + PUCT (Alpha Go Math)
     * Instead of pattern matching, evaluates 50 potential sequences in a simulated tree
     * and selects the one with the highest Q-value.
     */
    public CognitiveISA.Program transmuteWithMCTS(String naturalLanguage) {
        System.out.println("[BabelTransmuter] Transmuting with MCTS: \"" + naturalLanguage + "\"");
        
        // Create initial state (empty program)
        CognitiveISA.Program initialProgram = new CognitiveISA.Builder().halt().build();
        AlphaGoMath.CISAState initialState = new AlphaGoMath.CISAState(initialProgram);
        
        // Define prior function (LLM would provide this in production)
        java.util.function.Function<Integer, Double> priorFunction = action -> {
            // Simple heuristic: prefer PUSH, STORE, LOAD over control flow
            if (action == CognitiveISA.PUSH || action == CognitiveISA.STORE || action == CognitiveISA.LOAD) {
                return 0.6;
            } else if (action == CognitiveISA.CMP || action == CognitiveISA.BRANCH) {
                return 0.3;
            } else {
                return 0.1;
            }
        };
        
        // Define evaluation function (how good is a terminal state?)
        java.util.function.Function<AlphaGoMath.CISAState, Double> evaluationFunction = state -> {
            // Simple heuristic: longer programs with diverse instructions are better
            int diversity = (int) state.getProgram().instructions.stream()
                .mapToInt(instr -> instr.opcode)
                .distinct()
                .count();
            return (double) diversity;
        };
        
        // Select best action using MCTS
        int bestAction = alphaGoMath.selectBestAction(initialState, priorFunction, evaluationFunction);
        
        System.out.println("[BabelTransmuter] MCTS selected action: " + CognitiveISA.opcodeToString(bestAction));
        
        // Build program with selected action
        CognitiveISA.Builder builder = new CognitiveISA.Builder();
        builder.instruction(bestAction, null, null);
        builder.halt();
        
        CognitiveISA.Program program = builder.build();
        System.out.println("[BabelTransmuter] Generated C-ISA program:\n" + program);
        
        return program;
    }
    
    /**
     * Enable/disable MCTS mode
     */
    public void setUseMCTS(boolean useMCTS) {
        this.useMCTS = useMCTS;
        System.out.println("[BabelTransmuter] MCTS mode: " + (useMCTS ? "ENABLED" : "DISABLED"));
    }
    
    /**
     * Check if MCTS is enabled
     */
    public boolean isUseMCTS() {
        return useMCTS;
    }
    
    /**
     * Detect intent from natural language
     */
    private String detectIntent(String text) {
        String lower = text.toLowerCase();
        String[] words = lower.split("\\s+");

        // Check each intent pattern
        for (Map.Entry<String, IntentPattern> entry : intentPatterns.entrySet()) {
            for (String keyword : entry.getValue().keywords) {
                String keywordLower = keyword.toLowerCase();
                // Check for exact word match or phrase match
                if (lower.equals(keywordLower) ||
                    Arrays.asList(words).contains(keywordLower) ||
                    (keywordLower.contains(" ") && lower.contains(keywordLower))) {
                    return entry.getKey();
                }
            }
        }

        return "direct";
    }
    
    /**
     * Compile weather check intent
     * Example: "Find out if it's raining and tell me, otherwise just wait"
     */
    private CognitiveISA.Program compileWeatherCheck(String text) {
        return new CognitiveISA.Builder()
            .push("Raining")
            .store("weather")
            .label("check_weather")
            .load("weather")
            .push("Raining")
            .cmp()
            .branchIfEq("tell")
            .push("Wait 10 minutes")
            .branch("end")
            .label("tell")
            .push("It is raining")
            .label("end")
            .halt()
            .build();
    }
    
    /**
     * Compile conditional action intent
     * Example: "If x > 5 then push yes else push no"
     */
    private CognitiveISA.Program compileConditional(String text) {
        String lower = text.toLowerCase();
        
        // Extract variable name (simple heuristic)
        String var = extractVariable(text);
        if (var == null) var = "x";
        
        // Extract comparison (simple heuristic)
        String comparison = extractComparison(text);
        String threshold = extractThreshold(text);
        
        return new CognitiveISA.Builder()
            .load(var)
            .push(threshold)
            .cmp()
            .branchIfEq("then_branch")
            .branchIfGt("then_branch")
            .push("no")
            .branch("end")
            .label("then_branch")
            .push("yes")
            .label("end")
            .halt()
            .build();
    }
    
    /**
     * Compile store intent
     * Example: "Store 42 in x"
     */
    private CognitiveISA.Program compileStore(String text) {
        String valueStr = extractValue(text);
        Object value = parseValue(valueStr);
        String var = extractVariable(text);
        if (var == null) var = "default";
        
        return new CognitiveISA.Builder()
            .push(value)
            .store(var)
            .halt()
            .build();
    }
    
    /**
     * Compile load intent
     * Example: "Load x"
     */
    private CognitiveISA.Program compileLoad(String text) {
        String var = extractVariable(text);
        if (var == null) var = "default";
        
        return new CognitiveISA.Builder()
            .load(var)
            .halt()
            .build();
    }
    
    /**
     * Compile compare intent
     * Example: "Compare x with 5"
     */
    private CognitiveISA.Program compileCompare(String text) {
        String var1 = extractVariable(text);
        String var2 = extractValue(text);
        
        return new CognitiveISA.Builder()
            .load(var1 != null ? var1 : "default")
            .push(var2 != null ? var2 : "0")
            .cmp()
            .halt()
            .build();
    }
    
    /**
     * Compile actuate intent
     * Example: "Actuate Weather_API"
     */
    private CognitiveISA.Program compileActuate(String text) {
        String tool = extractTool(text);
        
        return new CognitiveISA.Builder()
            .actuate(tool != null ? tool : "default_tool")
            .halt()
            .build();
    }
    
    /**
     * Compile wait intent
     * Example: "Wait 10 minutes"
     */
    private CognitiveISA.Program compileWait(String text) {
        String duration = extractDuration(text);
        
        return new CognitiveISA.Builder()
            .push("Wait " + duration)
            .wait(duration)
            .halt()
            .build();
    }
    
    /**
     * Compile loop intent
     * Example: "Loop while x < 10"
     */
    private CognitiveISA.Program compileLoop(String text) {
        String var = extractVariable(text);
        String threshold = extractThreshold(text);
        
        return new CognitiveISA.Builder()
            .label("loop_start")
            .load(var != null ? var : "x")
            .push(threshold != null ? threshold : "10")
            .cmp()
            .branchIfGt("loop_end")
            .push("Loop body")
            .jmp("loop_start")
            .label("loop_end")
            .halt()
            .build();
    }
    
    /**
     * Direct compilation (fallback)
     */
    private CognitiveISA.Program compileDirect(String text) {
        return new CognitiveISA.Builder()
            .push(text)
            .halt()
            .build();
    }
    
    /**
     * Extract variable name from text (simple heuristic)
     */
    private String extractVariable(String text) {
        // Look for single-letter variables (x, y, z, etc.)
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.length() == 1 && Character.isLetter(word.charAt(0))) {
                return word;
            }
        }
        return null;
    }
    
    /**
     * Extract comparison operator from text
     */
    private String extractComparison(String text) {
        String lower = text.toLowerCase();
        if (lower.contains(">")) return ">";
        if (lower.contains("greater")) return ">";
        if (lower.contains("<")) return "<";
        if (lower.contains("less")) return "<";
        if (lower.contains("=")) return "=";
        if (lower.contains("equal")) return "=";
        return ">";
    }
    
    /**
     * Extract threshold value from text
     */
    private String extractThreshold(String text) {
        // Look for numbers
        String[] words = text.split("\\s+");
        for (String word : words) {
            try {
                Integer.parseInt(word);
                return word;
            } catch (NumberFormatException e) {
                // Not a number
            }
        }
        return "0";
    }
    
    /**
     * Parse value to appropriate type
     */
    private Object parseValue(String valueStr) {
        if (valueStr == null) return 0;
        
        // Try to parse as integer
        try {
            return Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            // Not an integer
        }
        
        // Try to parse as double
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            // Not a double
        }
        
        // Return as string
        return valueStr;
    }
    
    /**
     * Extract value from text
     */
    private String extractValue(String text) {
        // Look for numbers or quoted strings
        String[] words = text.split("\\s+");
        for (String word : words) {
            try {
                Integer.parseInt(word);
                return word;
            } catch (NumberFormatException e) {
                // Not a number
            }
            if (word.startsWith("\"") && word.endsWith("\"")) {
                return word.substring(1, word.length() - 1);
            }
        }
        return "0";
    }
    
    /**
     * Extract tool name from text
     */
    private String extractTool(String text) {
        // Look for tool names (simple heuristic)
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.contains("API") || word.contains("api")) {
                return word;
            }
            if (word.contains("tool") || word.contains("Tool")) {
                return word;
            }
        }
        return "default_tool";
    }
    
    /**
     * Extract duration from text
     */
    private String extractDuration(String text) {
        // Look for duration patterns
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            try {
                Integer.parseInt(words[i]);
                if (i + 1 < words.length) {
                    return words[i] + " " + words[i + 1];
                }
                return words[i];
            } catch (NumberFormatException e) {
                // Not a number
            }
        }
        return "10 minutes";
    }
    
    /**
     * Enable/disable LLM integration
     */
    public void setUseLLM(boolean useLLM) {
        this.useLLM = useLLM;
    }
    
    /**
     * Get status
     */
    public String getStatus() {
        return String.format(
            "BabelTransmuter: LLM=%b IntentPatterns=%d",
            useLLM, intentPatterns.size()
        );
    }
    
    /**
     * Intent pattern structure
     */
    private static class IntentPattern {
        List<String> keywords;
        Compiler compiler;
        
        IntentPattern(List<String> keywords, Compiler compiler) {
            this.keywords = keywords;
            this.compiler = compiler;
        }
    }
    
    /**
     * Compiler interface for intent patterns
     */
    @FunctionalInterface
    private interface Compiler {
        CognitiveISA.Program compile(String text);
    }
}
