package fraymus.neural;

import java.util.*;
import fraymus.OllamaIntegration;

/**
 * LLMReasoningEngine - Advanced reasoning using Ollama LLMs
 * 
 * Phase 2: Reasoning Enhancement
 * 
 * This class wraps OllamaIntegration to provide:
 * - Recursive reasoning (multi-hop chain-of-thought)
 * - Counterfactual simulation (what-if analysis)
 * - Enhanced analogy (semantic similarity + structural)
 * 
 * RULE SC-2: Maintains backward compatibility - optional enhancement
 */
public class LLMReasoningEngine {
    
    private final OllamaIntegration ollama;
    private final String model;
    private final boolean useCloud;
    
    // Reasoning statistics
    private int recursiveReasoningCount = 0;
    private int counterfactualSimulationCount = 0;
    private int enhancedAnalogyCount = 0;
    private long totalReasoningTimeMs = 0;
    
    /**
     * Constructor with cloud Ollama (recommended)
     */
    public LLMReasoningEngine() {
        this(true, "qwen3-coder-next");
    }
    
    /**
     * Constructor with model selection
     */
    public LLMReasoningEngine(boolean useCloud, String model) {
        this.useCloud = useCloud;
        this.model = model;
        this.ollama = new OllamaIntegration(useCloud);
    }
    
    /**
     * Test connection to Ollama
     */
    public boolean testConnection() {
        return ollama.testConnection();
    }
    
    /**
     * Recursive reasoning using chain-of-thought
     * 
     * Performs multi-hop reasoning by asking the LLM to think step-by-step
     * and explore intermediate conclusions before reaching the final answer.
     * 
     * @param question The question to reason about
     * @param maxHops Maximum reasoning hops (default: 5)
     * @return Reasoning result with chain of thought
     */
    public ReasoningResult recursiveReason(String question, int maxHops) {
        long startTime = System.currentTimeMillis();
        
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are a reasoning engine. Think step-by-step to answer the question.\n");
            prompt.append("Explore intermediate conclusions and show your reasoning chain.\n");
            prompt.append("Keep your reasoning concise and logical.\n\n");
            prompt.append("Question: ").append(question).append("\n\n");
            prompt.append("Think step-by-step:");
            
            String response = ollama.generate(model, prompt.toString(), false);
            
            recursiveReasoningCount++;
            long elapsed = System.currentTimeMillis() - startTime;
            totalReasoningTimeMs += elapsed;
            
            return new ReasoningResult(response, elapsed, "recursive", true);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new ReasoningResult("ERROR: " + e.getMessage(), elapsed, "recursive", false);
        }
    }
    
    /**
     * Counterfactual simulation (what-if analysis)
     * 
     * Asks the LLM to simulate alternative scenarios by changing premises
     * and exploring what would happen under different conditions.
     * 
     * @param scenario The base scenario
     * @param counterfactual The alternative premise to explore
     * @return Simulation result with analysis
     */
    public ReasoningResult counterfactualSimulate(String scenario, String counterfactual) {
        long startTime = System.currentTimeMillis();
        
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are a counterfactual reasoning engine. Analyze what would happen ");
            prompt.append("if we change a premise in the scenario.\n\n");
            prompt.append("Base scenario: ").append(scenario).append("\n");
            prompt.append("Alternative premise: ").append(counterfactual).append("\n\n");
            prompt.append("Analyze the consequences of this change. What would be different?\n");
            prompt.append("Be specific and logical.");
            
            String response = ollama.generate(model, prompt.toString(), false);
            
            counterfactualSimulationCount++;
            long elapsed = System.currentTimeMillis() - startTime;
            totalReasoningTimeMs += elapsed;
            
            return new ReasoningResult(response, elapsed, "counterfactual", true);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new ReasoningResult("ERROR: " + e.getMessage(), elapsed, "counterfactual", false);
        }
    }
    
    /**
     * Enhanced analogy using semantic similarity
     * 
     * Uses the LLM to find analogical relationships by understanding
     * the semantic structure and meaning of concepts, not just surface patterns.
     * 
     * @param sourceA First element of source analogy
     * @param sourceB Second element of source analogy
     * @param targetA First element of target analogy
     * @return Analogy completion with explanation
     */
    public ReasoningResult enhancedAnalogy(String sourceA, String sourceB, String targetA) {
        long startTime = System.currentTimeMillis();
        
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are an analogical reasoning engine. Complete the analogy ");
            prompt.append("by understanding the semantic relationship.\n\n");
            prompt.append("Source analogy: ").append(sourceA).append(" is to ").append(sourceB).append("\n");
            prompt.append("Target: ").append(targetA).append(" is to ?\n\n");
            prompt.append("Provide the completion and explain the relationship.\n");
            prompt.append("Keep it concise.");
            
            String response = ollama.generate(model, prompt.toString(), false);
            
            enhancedAnalogyCount++;
            long elapsed = System.currentTimeMillis() - startTime;
            totalReasoningTimeMs += elapsed;
            
            return new ReasoningResult(response, elapsed, "analogy", true);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new ReasoningResult("ERROR: " + e.getMessage(), elapsed, "analogy", false);
        }
    }
    
    /**
     * General reasoning query
     * 
     * Allows custom reasoning tasks with flexible prompting.
     * 
     * @param prompt The reasoning prompt
     * @return Reasoning result
     */
    public ReasoningResult reason(String prompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            String response = ollama.generate(model, prompt, false);
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalReasoningTimeMs += elapsed;
            
            return new ReasoningResult(response, elapsed, "general", true);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new ReasoningResult("ERROR: " + e.getMessage(), elapsed, "general", false);
        }
    }
    
    /**
     * Get reasoning statistics
     */
    public String getStats() {
        long avgTime = recursiveReasoningCount + counterfactualSimulationCount + enhancedAnalogyCount > 0 
            ? totalReasoningTimeMs / (recursiveReasoningCount + counterfactualSimulationCount + enhancedAnalogyCount)
            : 0;
        
        return String.format(
            "LLM Reasoning Statistics:\n" +
            "  Model: %s\n" +
            "  Mode: %s\n" +
            "  Recursive reasoning: %d\n" +
            "  Counterfactual simulations: %d\n" +
            "  Enhanced analogies: %d\n" +
            "  Total operations: %d\n" +
            "  Average time: %dms\n",
            model,
            useCloud ? "CLOUD" : "LOCAL",
            recursiveReasoningCount,
            counterfactualSimulationCount,
            enhancedAnalogyCount,
            recursiveReasoningCount + counterfactualSimulationCount + enhancedAnalogyCount,
            avgTime
        );
    }
    
    /**
     * Get the model name
     */
    public String getModel() {
        return model;
    }
    
    /**
     * Check if using cloud
     */
    public boolean isCloud() {
        return useCloud;
    }
    
    /**
     * Inner class representing a reasoning result
     */
    public static class ReasoningResult {
        public final String result;
        public final long elapsedTimeMs;
        public final String reasoningType;
        public final boolean success;
        
        public ReasoningResult(String result, long elapsedTimeMs, String reasoningType, boolean success) {
            this.result = result;
            this.elapsedTimeMs = elapsedTimeMs;
            this.reasoningType = reasoningType;
            this.success = success;
        }
    }
}
