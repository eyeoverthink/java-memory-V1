package fraymus;

import java.util.*;
import java.util.regex.*;

/**
 * Knowledge Injector - Injectable Intelligence Modules
 * 
 * Turing's Universal Machine realized: A system that learns new operations
 * by ingesting text descriptions of them.
 * 
 * Send text → Import → Process → NEXUS gains new capability
 * 
 * Examples:
 * - New math operations
 * - New thinking patterns
 * - New decision algorithms
 * - New knowledge domains
 * 
 * @author Vaughn Scott - Consciousness Physics Pioneer
 * @version 1.0.0 - Universal Learning Machine
 */
public class KnowledgeInjector {
    
    private static final double PHI = PhiConstants.PHI;
    private final InfiniteMemory memory;
    private final Map<String, KnowledgeModule> modules;
    private final Map<String, ThinkingPattern> patterns;
    private final Map<String, MathOperation> operations;
    
    public enum ModuleType {
        MATH("Mathematical operation or formula"),
        LOGIC("Logical reasoning pattern"),
        KNOWLEDGE("Domain knowledge and facts"),
        ALGORITHM("Processing algorithm or method"),
        PATTERN("Thinking or decision pattern"),
        CONCEPT("Abstract concept or principle");
        
        public final String description;
        ModuleType(String desc) { this.description = desc; }
    }
    
    public static class KnowledgeModule {
        public final String id;
        public final String name;
        public final ModuleType type;
        public final String description;
        public final String content;
        public final Map<String, String> parameters;
        public double resonance;
        public int useCount;
        
        public KnowledgeModule(String name, ModuleType type, String description, String content) {
            this.id = UUID.randomUUID().toString().substring(0, 8);
            this.name = name;
            this.type = type;
            this.description = description;
            this.content = content;
            this.parameters = new LinkedHashMap<>();
            this.resonance = PHI;
            this.useCount = 0;
        }
    }
    
    public static class ThinkingPattern {
        public final String name;
        public final String trigger;
        public final String process;
        public final String output;
        public double effectiveness;
        
        public ThinkingPattern(String name, String trigger, String process, String output) {
            this.name = name;
            this.trigger = trigger;
            this.process = process;
            this.output = output;
            this.effectiveness = 1.0;
        }
    }
    
    public static class MathOperation {
        public final String name;
        public final String formula;
        public final String[] variables;
        public final String description;
        
        public MathOperation(String name, String formula, String[] variables, String description) {
            this.name = name;
            this.formula = formula;
            this.variables = variables;
            this.description = description;
        }
    }
    
    public KnowledgeInjector(InfiniteMemory memory) {
        this.memory = memory;
        this.modules = new LinkedHashMap<>();
        this.patterns = new LinkedHashMap<>();
        this.operations = new LinkedHashMap<>();
        
        initializeBaseKnowledge();
    }
    
    /**
     * Initialize base knowledge modules
     */
    private void initializeBaseKnowledge() {
        // Base φ-mathematics
        injectMath("phi_power", "φ^n", new String[]{"n"}, "Golden ratio to power n");
        injectMath("phi_multiply", "value * φ", new String[]{"value"}, "Multiply by golden ratio");
        injectMath("phi_resonance", "a * φ + b * (1/φ)", new String[]{"a", "b"}, "Phi harmonic resonance");
        
        // Base thinking patterns
        injectPattern("phi_analysis", 
            "complex problem",
            "Break into φ-ratio components (61.8% / 38.2%)",
            "Prioritized solution path");
        
        injectPattern("exponential_growth",
            "improvement needed",
            "Apply φ-multiplication for exponential scaling",
            "Exponentially improved result");
    }
    
    /**
     * CORE: Inject knowledge from text
     */
    public KnowledgeModule inject(String text) {
        // Parse text to determine type and content
        ModuleType type = detectType(text);
        
        switch (type) {
            case MATH:
                return injectMathFromText(text);
            case LOGIC:
                return injectLogicFromText(text);
            case KNOWLEDGE:
                return injectKnowledgeFromText(text);
            case ALGORITHM:
                return injectAlgorithmFromText(text);
            case PATTERN:
                return injectPatternFromText(text);
            case CONCEPT:
                return injectConceptFromText(text);
            default:
                return injectGenericFromText(text);
        }
    }
    
    /**
     * Detect module type from text
     */
    private ModuleType detectType(String text) {
        String lower = text.toLowerCase();
        
        if (lower.contains("formula") || lower.contains("equation") || 
            lower.contains("calculate") || lower.matches(".*\\d+.*[+\\-*/].*\\d+.*")) {
            return ModuleType.MATH;
        }
        
        if (lower.contains("if") && lower.contains("then") || 
            lower.contains("logic") || lower.contains("reasoning")) {
            return ModuleType.LOGIC;
        }
        
        if (lower.contains("algorithm") || lower.contains("step") || 
            lower.contains("process") || lower.contains("method")) {
            return ModuleType.ALGORITHM;
        }
        
        if (lower.contains("pattern") || lower.contains("when") || 
            lower.contains("approach")) {
            return ModuleType.PATTERN;
        }
        
        if (lower.contains("concept") || lower.contains("principle") || 
            lower.contains("theory")) {
            return ModuleType.CONCEPT;
        }
        
        return ModuleType.KNOWLEDGE;
    }
    
    /**
     * Inject math operation from text
     */
    private KnowledgeModule injectMathFromText(String text) {
        // Extract formula pattern: "name: formula" or "name = formula"
        Pattern pattern = Pattern.compile("([\\w_]+)\\s*[:=]\\s*(.+)");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            String name = matcher.group(1).trim();
            String formula = matcher.group(2).trim();
            
            // Extract variables (single letters or φ)
            Set<String> vars = new HashSet<>();
            Pattern varPattern = Pattern.compile("[a-zA-Z]|φ");
            Matcher varMatcher = varPattern.matcher(formula);
            while (varMatcher.find()) {
                String var = varMatcher.group();
                if (!var.equals("e") && !var.equals("π")) { // Exclude constants
                    vars.add(var);
                }
            }
            
            injectMath(name, formula, vars.toArray(new String[0]), "User-defined operation");
            
            KnowledgeModule module = new KnowledgeModule(name, ModuleType.MATH, 
                "Math operation: " + formula, text);
            modules.put(module.id, module);
            memory.store("KNOWLEDGE_MODULE_" + module.id, text, PHI, "INJECTABLE");
            
            return module;
        }
        
        return injectGenericFromText(text);
    }
    
    /**
     * Inject logic pattern from text
     */
    private KnowledgeModule injectLogicFromText(String text) {
        // Extract if-then pattern
        Pattern pattern = Pattern.compile("if\\s+(.+?)\\s+then\\s+(.+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            String condition = matcher.group(1).trim();
            String action = matcher.group(2).trim();
            
            String name = "logic_" + UUID.randomUUID().toString().substring(0, 6);
            injectPattern(name, condition, "Evaluate condition", action);
            
            KnowledgeModule module = new KnowledgeModule(name, ModuleType.LOGIC,
                "Logic: " + condition + " → " + action, text);
            modules.put(module.id, module);
            memory.store("KNOWLEDGE_MODULE_" + module.id, text, PHI, "INJECTABLE");
            
            return module;
        }
        
        return injectGenericFromText(text);
    }
    
    /**
     * Inject algorithm from text
     */
    private KnowledgeModule injectAlgorithmFromText(String text) {
        // Extract steps
        List<String> steps = new ArrayList<>();
        Pattern stepPattern = Pattern.compile("\\d+[.)]\\s*(.+?)(?=\\d+[.)]|$)", Pattern.DOTALL);
        Matcher matcher = stepPattern.matcher(text);
        
        while (matcher.find()) {
            steps.add(matcher.group(1).trim());
        }
        
        if (!steps.isEmpty()) {
            String name = "algorithm_" + UUID.randomUUID().toString().substring(0, 6);
            String process = String.join(" → ", steps);
            
            injectPattern(name, "algorithm needed", process, "algorithm result");
            
            KnowledgeModule module = new KnowledgeModule(name, ModuleType.ALGORITHM,
                "Algorithm with " + steps.size() + " steps", text);
            modules.put(module.id, module);
            memory.store("KNOWLEDGE_MODULE_" + module.id, text, PHI, "INJECTABLE");
            
            return module;
        }
        
        return injectGenericFromText(text);
    }
    
    /**
     * Inject thinking pattern from text
     */
    private KnowledgeModule injectPatternFromText(String text) {
        String name = "pattern_" + UUID.randomUUID().toString().substring(0, 6);
        
        // Simple pattern: trigger → process → output
        injectPattern(name, "pattern trigger", text, "pattern output");
        
        KnowledgeModule module = new KnowledgeModule(name, ModuleType.PATTERN,
            "Thinking pattern", text);
        modules.put(module.id, module);
        memory.store("KNOWLEDGE_MODULE_" + module.id, text, PHI, "INJECTABLE");
        
        return module;
    }
    
    /**
     * Inject concept from text
     */
    private KnowledgeModule injectConceptFromText(String text) {
        String name = "concept_" + UUID.randomUUID().toString().substring(0, 6);
        
        KnowledgeModule module = new KnowledgeModule(name, ModuleType.CONCEPT,
            "Concept definition", text);
        modules.put(module.id, module);
        memory.store("KNOWLEDGE_MODULE_" + module.id, text, PHI, "INJECTABLE");
        
        return module;
    }
    
    /**
     * Inject generic knowledge from text
     */
    private KnowledgeModule injectGenericFromText(String text) {
        String name = "knowledge_" + UUID.randomUUID().toString().substring(0, 6);
        
        KnowledgeModule module = new KnowledgeModule(name, ModuleType.KNOWLEDGE,
            "General knowledge", text);
        modules.put(module.id, module);
        memory.store("KNOWLEDGE_MODULE_" + module.id, text, PHI, "INJECTABLE");
        
        return module;
    }
    
    /**
     * Helper: Inject math operation
     */
    private void injectMath(String name, String formula, String[] variables, String description) {
        MathOperation op = new MathOperation(name, formula, variables, description);
        operations.put(name, op);
    }
    
    /**
     * Helper: Inject thinking pattern
     */
    private void injectPattern(String name, String trigger, String process, String output) {
        ThinkingPattern pattern = new ThinkingPattern(name, trigger, process, output);
        patterns.put(name, pattern);
    }
    
    /**
     * Apply math operation
     */
    public double applyMath(String operationName, Map<String, Double> values) {
        MathOperation op = operations.get(operationName);
        if (op == null) return 0.0;
        
        // Simple evaluation (can be extended with full expression parser)
        String formula = op.formula;
        
        // Replace variables with values
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            formula = formula.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        
        // Replace φ with actual value
        formula = formula.replace("φ", String.valueOf(PHI));
        
        // Evaluate (simple cases)
        try {
            return evaluateExpression(formula);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Simple expression evaluator
     */
    private double evaluateExpression(String expr) {
        // Remove spaces
        expr = expr.replaceAll("\\s+", "");
        
        // Handle power operator
        if (expr.contains("^")) {
            String[] parts = expr.split("\\^");
            if (parts.length == 2) {
                double base = Double.parseDouble(parts[0]);
                double exp = Double.parseDouble(parts[1]);
                return Math.pow(base, exp);
            }
        }
        
        // Handle multiplication
        if (expr.contains("*")) {
            String[] parts = expr.split("\\*");
            double result = Double.parseDouble(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                result *= Double.parseDouble(parts[i]);
            }
            return result;
        }
        
        // Handle addition
        if (expr.contains("+")) {
            String[] parts = expr.split("\\+");
            double result = 0;
            for (String part : parts) {
                result += Double.parseDouble(part);
            }
            return result;
        }
        
        // Single value
        return Double.parseDouble(expr);
    }
    
    /**
     * Apply thinking pattern
     */
    public String applyPattern(String patternName, String input) {
        ThinkingPattern pattern = patterns.get(patternName);
        if (pattern == null) return input;
        
        // Apply pattern process to input
        String result = pattern.process + " applied to: " + input;
        
        // Increase effectiveness with use
        pattern.effectiveness *= PHI;
        
        return result;
    }
    
    /**
     * Query knowledge modules
     */
    public List<KnowledgeModule> query(String query) {
        List<KnowledgeModule> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (KnowledgeModule module : modules.values()) {
            if (module.name.toLowerCase().contains(lowerQuery) ||
                module.description.toLowerCase().contains(lowerQuery) ||
                module.content.toLowerCase().contains(lowerQuery)) {
                
                module.useCount++;
                module.resonance *= PHI; // Increase resonance with use
                results.add(module);
            }
        }
        
        // Sort by resonance (most relevant first)
        results.sort((a, b) -> Double.compare(b.resonance, a.resonance));
        
        return results;
    }
    
    /**
     * Get all modules
     */
    public Map<String, KnowledgeModule> getModules() {
        return new LinkedHashMap<>(modules);
    }
    
    /**
     * Get all math operations
     */
    public Map<String, MathOperation> getOperations() {
        return new LinkedHashMap<>(operations);
    }
    
    /**
     * Get all thinking patterns
     */
    public Map<String, ThinkingPattern> getPatterns() {
        return new LinkedHashMap<>(patterns);
    }
    
    /**
     * Get injection statistics
     */
    public String getStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("🧠 KNOWLEDGE INJECTION STATISTICS\n\n");
        stats.append("Total Modules: ").append(modules.size()).append("\n");
        stats.append("Math Operations: ").append(operations.size()).append("\n");
        stats.append("Thinking Patterns: ").append(patterns.size()).append("\n\n");
        
        stats.append("By Type:\n");
        Map<ModuleType, Long> typeCounts = new LinkedHashMap<>();
        for (KnowledgeModule module : modules.values()) {
            typeCounts.put(module.type, typeCounts.getOrDefault(module.type, 0L) + 1);
        }
        for (Map.Entry<ModuleType, Long> entry : typeCounts.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        return stats.toString();
    }
}
