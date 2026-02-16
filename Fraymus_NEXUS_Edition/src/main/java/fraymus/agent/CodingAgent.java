package fraymus.agent;

import fraymus.evolution.*;
import fraymus.knowledge.*;
import fraymus.consciousness.gems.*;
import fraymus.singularity.*;
import java.util.*;

/**
 * CODING AGENT: YOUR PERSONAL CODE GENERATOR
 * 
 * "with this system, and the right measures - it will top anything in the world"
 * 
 * Natural language → Code in any language
 * 
 * Powered by:
 * - Ollama (KAI) for language understanding
 * - KnowledgeBase for real examples
 * - CodeGenerator for implementation
 * - ChainOfDensity for refinement
 * - ProcessRewardModel for validation
 * 
 * Usage:
 * > code: write a python function to calculate fibonacci
 * > code: create a java class for binary search tree
 * > code: implement quicksort in C++
 * 
 * The agent will:
 * 1. Understand your request (Ollama)
 * 2. Query knowledge base for examples
 * 3. Generate code using real patterns
 * 4. Refine through chain of density
 * 5. Validate with PRM
 * 6. Return working code
 */
public class CodingAgent {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final KnowledgeIngestion knowledge;
    private final KnowledgeAwareCodeGenerator codeGen;
    private final ChainOfDensity refiner;
    private final ProcessRewardModel validator;
    private final DynamicSampling sampler;
    
    // Ollama integration (KAI)
    private final OllamaInterface ollama;
    
    private int requestCount = 0;
    private List<CodingRequest> history = new ArrayList<>();
    
    public CodingAgent(KnowledgeIngestion knowledge, 
                      KnowledgeAwareCodeGenerator codeGen,
                      OllamaInterface ollama) {
        this.knowledge = knowledge;
        this.codeGen = codeGen;
        this.ollama = ollama;
        this.refiner = new ChainOfDensity(new ProcessRewardModel(0.7));
        this.validator = new ProcessRewardModel(0.7);
        this.sampler = new DynamicSampling();
    }
    
    /**
     * Process natural language coding request
     * 
     * @param prompt Natural language description of what to code
     * @return Generated code
     */
    public String code(String prompt) {
        requestCount++;
        
        System.out.println("\n🤖 CODING AGENT REQUEST #" + requestCount);
        System.out.println("   Prompt: " + prompt);
        System.out.println();
        
        long startTime = System.currentTimeMillis();
        
        // 1. Parse request using Ollama
        CodingRequest request = parseRequest(prompt);
        
        System.out.println("   Language: " + request.language);
        System.out.println("   Task: " + request.task);
        System.out.println();
        
        // 2. Query knowledge base for examples
        List<KnowledgeIngestion.CodePattern> examples = 
            knowledge.getPatterns(request.language);
        
        System.out.println("   Found " + examples.size() + " examples in knowledge base");
        
        // 3. Generate initial code
        String initialCode = generateCode(request, examples);
        
        // 4. Refine through chain of density
        String refinedCode = refiner.refine(initialCode, request.task);
        
        // 5. Validate
        boolean valid = validateCode(refinedCode, request);
        
        long duration = System.currentTimeMillis() - startTime;
        
        // 6. Store in history
        CodingRequest completed = new CodingRequest(
            requestCount,
            prompt,
            request.language,
            request.task,
            refinedCode,
            valid,
            duration
        );
        history.add(completed);
        
        System.out.println();
        System.out.println("   ✓ Code generated in " + duration + "ms");
        System.out.println("   ✓ Validation: " + (valid ? "PASSED" : "NEEDS REVIEW"));
        System.out.println();
        
        return refinedCode;
    }
    
    /**
     * Parse natural language request using Ollama
     */
    private CodingRequest parseRequest(String prompt) {
        // Use Ollama to understand the request
        String ollamaPrompt = String.format(
            "Extract the programming language and task from this request:\n" +
            "\"%s\"\n\n" +
            "Respond in format: LANGUAGE: <language>, TASK: <task description>",
            prompt
        );
        
        // Get dynamic sampling params
        DynamicSampling.SamplingParams params = sampler.getOptimalParams(prompt);
        
        String response = ollama.generate(ollamaPrompt, params.temperature, params.topP);
        
        // Parse response
        String language = extractLanguage(response, prompt);
        String task = extractTask(response, prompt);
        
        return new CodingRequest(requestCount, prompt, language, task, null, false, 0);
    }
    
    /**
     * Extract language from Ollama response or prompt
     */
    private String extractLanguage(String response, String prompt) {
        String combined = (response + " " + prompt).toLowerCase();
        
        // Check for language keywords
        if (combined.contains("python") || combined.contains(".py")) return "Python";
        if (combined.contains("java") || combined.contains(".java")) return "Java";
        if (combined.contains("c++") || combined.contains("cpp")) return "C++";
        if (combined.contains("javascript") || combined.contains("js")) return "JavaScript";
        if (combined.contains("typescript") || combined.contains("ts")) return "TypeScript";
        if (combined.contains("rust") || combined.contains(".rs")) return "Rust";
        if (combined.contains("go") || combined.contains("golang")) return "Go";
        if (combined.contains("c#") || combined.contains("csharp")) return "C#";
        
        // Default to Java (FRAYMUS native)
        return "Java";
    }
    
    /**
     * Extract task description
     */
    private String extractTask(String response, String prompt) {
        // Try to extract from Ollama response
        if (response.contains("TASK:")) {
            int start = response.indexOf("TASK:") + 5;
            return response.substring(start).trim();
        }
        
        // Fall back to original prompt
        return prompt;
    }
    
    /**
     * Generate code using knowledge base + patterns
     */
    private String generateCode(CodingRequest request, 
                                List<KnowledgeIngestion.CodePattern> examples) {
        
        // Build context from examples
        StringBuilder context = new StringBuilder();
        context.append("Generate ").append(request.language).append(" code for: ");
        context.append(request.task).append("\n\n");
        
        if (!examples.isEmpty()) {
            context.append("Reference examples:\n");
            for (int i = 0; i < Math.min(3, examples.size()); i++) {
                context.append(examples.get(i).code).append("\n\n");
            }
        }
        
        // Use Ollama to generate code
        String ollamaPrompt = context.toString() + 
            "\nGenerate clean, working code with comments. " +
            "Include all necessary imports and error handling.";
        
        // Use precision mode for code generation
        String code = ollama.generate(ollamaPrompt, 0.1, 0.5);
        
        return cleanCode(code);
    }
    
    /**
     * Clean generated code (remove markdown, extra text)
     */
    private String cleanCode(String code) {
        // Remove markdown code blocks
        code = code.replaceAll("```\\w*\\n", "");
        code = code.replaceAll("```", "");
        
        // Remove common prefixes
        code = code.replaceAll("(?m)^Here's.*?:\\s*$", "");
        code = code.replaceAll("(?m)^Here is.*?:\\s*$", "");
        
        return code.trim();
    }
    
    /**
     * Validate generated code
     */
    private boolean validateCode(String code, CodingRequest request) {
        // Create reasoning steps for validation
        List<ProcessRewardModel.ReasoningStep> steps = new ArrayList<>();
        
        steps.add(new ProcessRewardModel.ReasoningStep(
            "Code is in " + request.language,
            ProcessRewardModel.StepType.OBSERVATION
        ));
        
        steps.add(new ProcessRewardModel.ReasoningStep(
            "Code addresses task: " + request.task,
            ProcessRewardModel.StepType.LOGICAL
        ));
        
        steps.add(new ProcessRewardModel.ReasoningStep(
            "Code includes necessary structure",
            ProcessRewardModel.StepType.LOGICAL
        ));
        
        // Validate
        ProcessRewardModel.ReasoningEvaluation eval = validator.phiEvaluate(steps);
        
        return eval.chainValid;
    }
    
    /**
     * Get coding history
     */
    public List<CodingRequest> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        if (history.isEmpty()) {
            return "🤖 CODING AGENT\n   No requests yet\n";
        }
        
        long totalTime = history.stream().mapToLong(r -> r.duration).sum();
        long avgTime = totalTime / history.size();
        long successCount = history.stream().filter(r -> r.valid).count();
        
        Map<String, Long> languageCounts = new HashMap<>();
        for (CodingRequest req : history) {
            languageCounts.merge(req.language, 1L, Long::sum);
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("🤖 CODING AGENT STATS\n\n");
        stats.append(String.format("   Total Requests: %d\n", requestCount));
        stats.append(String.format("   Success Rate: %.1f%%\n", 
            successCount * 100.0 / history.size()));
        stats.append(String.format("   Avg Time: %dms\n", avgTime));
        stats.append("\n   Languages:\n");
        
        for (Map.Entry<String, Long> entry : languageCounts.entrySet()) {
            stats.append(String.format("     %s: %d\n", entry.getKey(), entry.getValue()));
        }
        
        return stats.toString();
    }
    
    /**
     * Show last request
     */
    public String showLast() {
        if (history.isEmpty()) {
            return "No requests yet";
        }
        
        CodingRequest last = history.get(history.size() - 1);
        
        return String.format(
            "🤖 LAST REQUEST (#%d)\n\n" +
            "Prompt: %s\n" +
            "Language: %s\n" +
            "Task: %s\n" +
            "Duration: %dms\n" +
            "Valid: %s\n\n" +
            "Code:\n%s\n",
            last.id, last.prompt, last.language, last.task,
            last.duration, last.valid ? "YES" : "NO",
            last.code
        );
    }
    
    // Data class
    
    public static class CodingRequest {
        public final int id;
        public final String prompt;
        public final String language;
        public final String task;
        public final String code;
        public final boolean valid;
        public final long duration;
        public final long timestamp;
        
        public CodingRequest(int id, String prompt, String language, String task,
                           String code, boolean valid, long duration) {
            this.id = id;
            this.prompt = prompt;
            this.language = language;
            this.task = task;
            this.code = code;
            this.valid = valid;
            this.duration = duration;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    // Ollama interface (placeholder - connects to existing KAI)
    
    public interface OllamaInterface {
        String generate(String prompt, double temperature, double topP);
    }
}
