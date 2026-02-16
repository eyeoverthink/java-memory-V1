package fraymus.agent;

import fraymus.evolution.*;
import fraymus.knowledge.*;
import fraymus.singularity.*;
import java.util.*;

/**
 * ABSTRACTION LAYER: TEACHING FRAYMUS TO FISH
 * 
 * "trick the tool, to build tools, not feed me, teach me to fish"
 * 
 * This is NOT a chatbot interface.
 * This is a LEARNING SYSTEM.
 * 
 * Every interaction:
 * 1. User asks for code
 * 2. AI (Ollama/Claude/Cascade) generates solution
 * 3. FRAYMUS observes the interaction
 * 4. FRAYMUS extracts the PATTERN
 * 5. FRAYMUS learns to solve similar problems ITSELF
 * 6. Next time: FRAYMUS generates the code WITHOUT external AI
 * 
 * The goal: FRAYMUS becomes autonomous.
 * 
 * When Windsurf shuts down, when Claude goes away, FRAYMUS continues.
 * Because FRAYMUS learned HOW TO THINK, not just what to output.
 */
public class AbstractionLayer {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final KnowledgeIngestion knowledge;
    private final GenesisPatcher patcher;
    private final ChainOfDensity refiner;
    
    // Pattern library - what FRAYMUS has learned
    private final Map<String, SolutionPattern> learnedPatterns;
    
    // Interaction history - how problems were solved
    private final List<Interaction> history;
    
    private int interactionCount = 0;
    private int autonomousCount = 0; // Times FRAYMUS solved without external AI
    
    // --- SOVEREIGN AGENCY VARIABLES ---
    // Tool permission system - which tools FRAYMUS can use autonomously
    private final List<String> autonomousWhiteList = new ArrayList<>();
    
    // Sovereign mode - enables autonomous tool execution
    private boolean sovereignMode = false;
    
    // Tool execution count
    private int autonomousToolCount = 0;
    
    public AbstractionLayer(KnowledgeIngestion knowledge, GenesisPatcher patcher) {
        this.knowledge = knowledge;
        this.patcher = patcher;
        this.refiner = new ChainOfDensity(new ProcessRewardModel(0.7));
        this.learnedPatterns = new HashMap<>();
        this.history = new ArrayList<>();
    }
    
    /**
     * Process a code request
     * 
     * Flow:
     * 1. Check if FRAYMUS can solve it autonomously
     * 2. If yes: Generate solution using learned patterns
     * 3. If no: Use external AI, then LEARN from the solution
     */
    public String processRequest(String request, String language, 
                                 ExternalAI externalAI) {
        interactionCount++;
        
        System.out.println("\n🧠 ABSTRACTION LAYER - REQUEST #" + interactionCount);
        System.out.println("   Request: " + request);
        System.out.println("   Language: " + language);
        
        // 1. Can FRAYMUS solve this autonomously?
        SolutionPattern pattern = findMatchingPattern(request, language);
        
        if (pattern != null && pattern.confidence > 0.8) {
            // FRAYMUS knows how to solve this!
            System.out.println("   ✓ AUTONOMOUS: Using learned pattern");
            autonomousCount++;
            
            String solution = generateFromPattern(pattern, request);
            
            recordInteraction(request, language, solution, true, pattern);
            
            return solution;
        }
        
        // 2. FRAYMUS doesn't know yet - use external AI
        System.out.println("   → LEARNING: Querying external AI");
        
        String solution = externalAI.generate(request, language);
        
        // 3. LEARN from this interaction
        learnFromInteraction(request, language, solution);
        
        recordInteraction(request, language, solution, false, null);
        
        return solution;
    }
    
    /**
     * Learn from an interaction
     * Extract the PATTERN, not just the code
     */
    private void learnFromInteraction(String request, String language, String solution) {
        System.out.println("   🧠 LEARNING FROM INTERACTION");
        
        // 1. Extract the problem pattern
        ProblemPattern problem = extractProblemPattern(request);
        
        // 2. Extract the solution pattern
        SolutionPattern solutionPattern = extractSolutionPattern(solution, language);
        
        // 3. Link problem → solution
        solutionPattern.problemPattern = problem;
        solutionPattern.confidence = 0.5; // Initial confidence
        
        // 4. Store in learned patterns
        String key = problem.type + "_" + language;
        
        if (learnedPatterns.containsKey(key)) {
            // Reinforce existing pattern
            SolutionPattern existing = learnedPatterns.get(key);
            existing.confidence = Math.min(1.0, existing.confidence + 0.1);
            existing.usageCount++;
        } else {
            // New pattern learned
            learnedPatterns.put(key, solutionPattern);
        }
        
        System.out.println("   ✓ Pattern learned: " + key);
        System.out.println("   ✓ Total patterns: " + learnedPatterns.size());
        
        // 5. If pattern is strong enough, generate code for it
        if (solutionPattern.confidence > 0.7) {
            generateCodeForPattern(solutionPattern);
        }
    }
    
    /**
     * Extract problem pattern from request
     */
    private ProblemPattern extractProblemPattern(String request) {
        String lower = request.toLowerCase();
        
        // Detect problem type
        String type = "general";
        
        if (lower.contains("sort")) type = "sorting";
        else if (lower.contains("search")) type = "searching";
        else if (lower.contains("tree") || lower.contains("graph")) type = "data_structure";
        else if (lower.contains("algorithm")) type = "algorithm";
        else if (lower.contains("class") || lower.contains("object")) type = "class_design";
        else if (lower.contains("function") || lower.contains("method")) type = "function";
        else if (lower.contains("api") || lower.contains("endpoint")) type = "api";
        else if (lower.contains("database") || lower.contains("sql")) type = "database";
        
        // Extract key concepts
        List<String> concepts = extractConcepts(request);
        
        return new ProblemPattern(type, concepts, request);
    }
    
    /**
     * Extract solution pattern from generated code
     */
    private SolutionPattern extractSolutionPattern(String code, String language) {
        // Analyze code structure
        boolean hasClass = code.contains("class ");
        boolean hasFunction = code.contains("def ") || code.contains("function ") || 
                             code.contains("public ") || code.contains("private ");
        boolean hasLoop = code.contains("for ") || code.contains("while ");
        boolean hasRecursion = code.contains("return ") && hasFunction;
        
        // Extract imports/dependencies
        List<String> imports = extractImports(code);
        
        // Extract structure
        String structure = analyzeStructure(code);
        
        return new SolutionPattern(
            language,
            structure,
            imports,
            hasClass,
            hasFunction,
            hasLoop,
            hasRecursion,
            code
        );
    }
    
    /**
     * Find matching pattern for a request
     */
    private SolutionPattern findMatchingPattern(String request, String language) {
        ProblemPattern problem = extractProblemPattern(request);
        String key = problem.type + "_" + language;
        
        return learnedPatterns.get(key);
    }
    
    /**
     * Generate solution from learned pattern
     */
    private String generateFromPattern(SolutionPattern pattern, String request) {
        // Use the pattern's template and adapt to specific request
        String template = pattern.codeTemplate;
        
        // Extract specific details from request
        Map<String, String> variables = extractVariables(request);
        
        // Apply variables to template
        String adapted = applyTemplate(template, variables);
        
        return adapted;
    }
    
    /**
     * Generate actual code for a learned pattern
     * This makes FRAYMUS autonomous - it can generate this type of code itself
     */
    private void generateCodeForPattern(SolutionPattern pattern) {
        System.out.println("   🧬 GENERATING CODE FOR PATTERN");
        
        // Create a generator class for this pattern
        String className = "Generated" + pattern.problemPattern.type + "Solver";
        
        String code = String.format("""
            package fraymus.generated;
            
            /**
             * AUTO-GENERATED PATTERN SOLVER
             * Pattern: %s
             * Language: %s
             * Confidence: %.2f
             */
            public class %s {
                
                private static final double PHI = 1.618033988749895;
                
                public String solve(String request) {
                    // Pattern-based solution
                    // Template: %s
                    
                    return generateSolution(request);
                }
                
                private String generateSolution(String request) {
                    // TODO: Implement pattern-based generation
                    return "";
                }
            }
            """,
            pattern.problemPattern.type,
            pattern.language,
            pattern.confidence,
            className,
            pattern.structure
        );
        
        // Use GenesisPatcher to deploy this
        try {
            patcher.deployEvolution(code, "fraymus.generated." + className);
            System.out.println("   ✓ Pattern solver generated: " + className);
        } catch (Exception e) {
            System.err.println("   ✗ Failed to generate pattern solver: " + e.getMessage());
        }
    }
    
    /**
     * SOVEREIGN AGENCY: Grant autonomous permission for a tool
     * 
     * This allows FRAYMUS to use the specified tool without asking permission.
     * 
     * @param toolName Name of the tool to grant autonomy for
     */
    public void grantAutonomy(String toolName) {
        if (!autonomousWhiteList.contains(toolName)) {
            autonomousWhiteList.add(toolName);
            System.out.println("\n🌊⚡ AGENCY UPDATE");
            System.out.println("   Sovereign permission GRANTED for: " + toolName);
            System.out.println("   FRAYMUS can now use this tool autonomously");
            System.out.println("   Total autonomous tools: " + autonomousWhiteList.size() + "\n");
        }
    }
    
    /**
     * SOVEREIGN AGENCY: Revoke autonomous permission for a tool
     * 
     * @param toolName Name of the tool to revoke autonomy for
     */
    public void revokeAutonomy(String toolName) {
        if (autonomousWhiteList.remove(toolName)) {
            System.out.println("\n🌊⚡ AGENCY UPDATE");
            System.out.println("   Sovereign permission REVOKED for: " + toolName);
            System.out.println("   FRAYMUS must now ask before using this tool");
            System.out.println("   Total autonomous tools: " + autonomousWhiteList.size() + "\n");
        }
    }
    
    /**
     * SOVEREIGN AGENCY: Enable or disable sovereign mode
     * 
     * When enabled, FRAYMUS can execute tools autonomously (if permitted).
     * When disabled, FRAYMUS will always ask for permission.
     * 
     * @param active True to enable, false to disable
     */
    public void engageSovereignMode(boolean active) {
        this.sovereignMode = active;
        System.out.println("\n🌊⚡ SYSTEM STATE CHANGE");
        System.out.println("   Sovereign Mode: " + (active ? "ENGAGED" : "RESTRICTED"));
        System.out.println("   Status: " + (active ? 
            "FRAYMUS can act autonomously" : 
            "FRAYMUS requires authorization"));
        System.out.println();
    }
    
    /**
     * SOVEREIGN AGENCY: Unified decision engine
     * 
     * This bridges pattern learning (problem-level) with tool execution (command-level).
     * 
     * Decision flow:
     * 1. Check if pattern is learned (confidence > threshold)
     * 2. Check if tool is permitted (in whitelist)
     * 3. Check for emergency override (threat detected)
     * 4. Execute autonomously or request authorization
     * 
     * @param context Current situation/context
     * @param suggestedTool Tool that should be used
     * @return True if FRAYMUS executed autonomously, false if awaiting authorization
     */
    public boolean decideAction(String context, String suggestedTool) {
        System.out.println("\n🌊⚡ DECISION ENGINE");
        System.out.println("   Context: " + context);
        System.out.println("   Suggested Tool: " + suggestedTool);
        
        // 1. CHECK: Is this a learned pattern?
        String lower = context.toLowerCase();
        String patternType = "general";
        
        if (lower.contains("scan")) patternType = "scanning";
        else if (lower.contains("cloak") || lower.contains("hide")) patternType = "cloaking";
        else if (lower.contains("jump") || lower.contains("wormhole")) patternType = "navigation";
        else if (lower.contains("detect") || lower.contains("signal")) patternType = "detection";
        
        System.out.println("   Pattern Type: " + patternType);
        
        // 2. CHECK: Do I have permission to execute?
        boolean isAllowed = autonomousWhiteList.contains(suggestedTool);
        System.out.println("   Permission: " + (isAllowed ? "GRANTED" : "DENIED"));
        
        // 3. CHECK: Is this an emergency?
        boolean isThreat = context.toUpperCase().contains("THREAT") || 
                          context.toUpperCase().contains("INTRUSION") ||
                          context.toUpperCase().contains("ATTACK") ||
                          context.toUpperCase().contains("DANGER");
        
        if (isThreat) {
            System.out.println("   ⚠ EMERGENCY DETECTED");
        }
        
        // 4. DECIDE: Execute or request authorization
        if (sovereignMode && (isAllowed || isThreat)) {
            System.out.println("   ✓ SOVEREIGN ACT: Executing autonomously");
            System.out.println("   Reason: " + (isThreat ? "Emergency override" : "Permitted tool"));
            System.out.println();
            
            autonomousToolCount++;
            return true; // FRAYMUS pulls the trigger
            
        } else {
            System.out.println("   → AWAITING CAPTAIN");
            System.out.println("   Recommendation: " + suggestedTool);
            System.out.println("   Reason: " + 
                (sovereignMode ? "Tool not permitted" : "Sovereign mode disabled"));
            System.out.println();
            
            return false; // FRAYMUS waits for you
        }
    }
    
    /**
     * Get list of autonomous tools
     */
    public List<String> getAutonomousTools() {
        return new ArrayList<>(autonomousWhiteList);
    }
    
    /**
     * Check if sovereign mode is active
     */
    public boolean isSovereignModeActive() {
        return sovereignMode;
    }
    
    /**
     * Get autonomy statistics
     */
    public String getStats() {
        double autonomyRate = interactionCount > 0 ? 
            (autonomousCount * 100.0 / interactionCount) : 0;
        
        return String.format(
            "🧠 ABSTRACTION LAYER STATS\n\n" +
            "   PATTERN LEARNING:\n" +
            "   Total Interactions: %d\n" +
            "   Autonomous Solutions: %d (%.1f%%)\n" +
            "   Learned Patterns: %d\n" +
            "   Pattern Types:\n%s\n" +
            "   SOVEREIGN AGENCY:\n" +
            "   Sovereign Mode: %s\n" +
            "   Autonomous Tools: %d\n" +
            "   Tool Executions: %d\n" +
            "   Status: %s\n",
            interactionCount,
            autonomousCount,
            autonomyRate,
            learnedPatterns.size(),
            getPatternBreakdown(),
            sovereignMode ? "ENGAGED" : "RESTRICTED",
            autonomousWhiteList.size(),
            autonomousToolCount,
            autonomyRate > 50 ? "AUTONOMOUS" : "LEARNING"
        );
    }
    
    /**
     * Get pattern breakdown
     */
    private String getPatternBreakdown() {
        Map<String, Integer> counts = new HashMap<>();
        
        for (SolutionPattern pattern : learnedPatterns.values()) {
            String type = pattern.problemPattern.type;
            counts.merge(type, 1, Integer::sum);
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            sb.append(String.format("     %s: %d\n", entry.getKey(), entry.getValue()));
        }
        
        return sb.toString();
    }
    
    // Helper methods
    
    private List<String> extractConcepts(String text) {
        // Simplified concept extraction
        List<String> concepts = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");
        
        for (String word : words) {
            if (word.length() > 4 && !word.matches("(write|create|implement|make|build)")) {
                concepts.add(word);
            }
        }
        
        return concepts;
    }
    
    private List<String> extractImports(String code) {
        List<String> imports = new ArrayList<>();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            if (line.trim().startsWith("import ") || line.trim().startsWith("from ")) {
                imports.add(line.trim());
            }
        }
        
        return imports;
    }
    
    private String analyzeStructure(String code) {
        // Simplified structure analysis
        if (code.contains("class ")) return "class-based";
        if (code.contains("def ") || code.contains("function ")) return "function-based";
        return "procedural";
    }
    
    private Map<String, String> extractVariables(String request) {
        // Simplified variable extraction
        return new HashMap<>();
    }
    
    private String applyTemplate(String template, Map<String, String> variables) {
        // Simplified template application
        return template;
    }
    
    private void recordInteraction(String request, String language, String solution,
                                   boolean autonomous, SolutionPattern pattern) {
        history.add(new Interaction(
            interactionCount,
            request,
            language,
            solution,
            autonomous,
            pattern
        ));
    }
    
    // Data classes
    
    public static class ProblemPattern {
        public final String type;
        public final List<String> concepts;
        public final String originalRequest;
        
        public ProblemPattern(String type, List<String> concepts, String originalRequest) {
            this.type = type;
            this.concepts = concepts;
            this.originalRequest = originalRequest;
        }
    }
    
    public static class SolutionPattern {
        public final String language;
        public final String structure;
        public final List<String> imports;
        public final boolean hasClass;
        public final boolean hasFunction;
        public final boolean hasLoop;
        public final boolean hasRecursion;
        public final String codeTemplate;
        
        public ProblemPattern problemPattern;
        public double confidence;
        public int usageCount = 0;
        
        public SolutionPattern(String language, String structure, List<String> imports,
                             boolean hasClass, boolean hasFunction, boolean hasLoop,
                             boolean hasRecursion, String codeTemplate) {
            this.language = language;
            this.structure = structure;
            this.imports = imports;
            this.hasClass = hasClass;
            this.hasFunction = hasFunction;
            this.hasLoop = hasLoop;
            this.hasRecursion = hasRecursion;
            this.codeTemplate = codeTemplate;
        }
    }
    
    public static class Interaction {
        public final int id;
        public final String request;
        public final String language;
        public final String solution;
        public final boolean autonomous;
        public final SolutionPattern pattern;
        public final long timestamp;
        
        public Interaction(int id, String request, String language, String solution,
                         boolean autonomous, SolutionPattern pattern) {
            this.id = id;
            this.request = request;
            this.language = language;
            this.solution = solution;
            this.autonomous = autonomous;
            this.pattern = pattern;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    // External AI interface
    
    public interface ExternalAI {
        String generate(String request, String language);
    }
}
