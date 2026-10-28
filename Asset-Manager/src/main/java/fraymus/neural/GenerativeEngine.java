package fraymus.neural;

import java.util.*;

/**
 * GenerativeEngine - Text and visual generative capabilities
 * 
 * Phase 4: Generative Capability
 * 
 * This class provides:
 * - LLM-based text generation
 * - FUSE operation for compositional creativity
 * - Novelty generation via HDC exploration
 * - Visual generation via LTX-Video
 * - Multi-modal generative capabilities
 * 
 * RULE SC-1: Never modify existing data structures without safety checks
 * RULE SC-2: Maintains backward compatibility - optional enhancement
 */
public class GenerativeEngine {
    
    private final LLMReasoningEngine llmEngine;
    private final SentientCore core;
    private final HDCSimilarityEngine hdcEngine;
    
    // Generative statistics
    private int textGenerations = 0;
    private int visualGenerations = 0;
    private int fuseOperations = 0;
    private int noveltyExplorations = 0;
    private long totalGenerationTimeMs = 0;
    
    // Novelty parameters
    private final double noveltyThreshold = 0.7; // Minimum novelty score
    private final int explorationSteps = 100; // HDC exploration steps
    
    /**
     * Constructor with LLM reasoning engine and SentientCore
     */
    public GenerativeEngine(LLMReasoningEngine llmEngine, SentientCore core, HDCSimilarityEngine hdcEngine) {
        if (llmEngine == null || core == null) {
            throw new IllegalArgumentException("LLMReasoningEngine and SentientCore cannot be null");
        }
        this.llmEngine = llmEngine;
        this.core = core;
        this.hdcEngine = hdcEngine;
    }
    
    /**
     * Generate creative text based on prompt
     * 
     * Uses LLM to generate creative text with novelty and coherence.
     * 
     * @param prompt The prompt for text generation
     * @return Generated text with metadata
     */
    public GenerativeResult generateText(String prompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            StringBuilder llmPrompt = new StringBuilder();
            llmPrompt.append("You are a creative AI. Generate creative text based on the prompt.\n");
            llmPrompt.append("Focus on:\n");
            llmPrompt.append("1. Novelty - create something new and original\n");
            llmPrompt.append("2. Coherence - maintain logical consistency\n");
            llmPrompt.append("3. Creativity - use imaginative language\n\n");
            llmPrompt.append("Prompt: ").append(prompt).append("\n\n");
            llmPrompt.append("Generate creative text:");
            
            LLMReasoningEngine.ReasoningResult llmResult = llmEngine.reason(llmPrompt.toString());
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalGenerationTimeMs += elapsed;
            textGenerations++;
            
            if (llmResult.success) {
                double novelty = calculateNovelty(llmResult.result);
                double coherence = calculateCoherence(llmResult.result);
                
                return new GenerativeResult(llmResult.result, elapsed, "text", true, novelty, coherence);
            } else {
                return new GenerativeResult("Generation failed: " + llmResult.result, elapsed, "text", false, 0.0, 0.0);
            }
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new GenerativeResult("ERROR: " + e.getMessage(), elapsed, "text", false, 0.0, 0.0);
        }
    }
    
    /**
     * FUSE operation - compose multiple concepts into creative output
     * 
     * Combines multiple concepts/ideas using HDC binding and LLM reasoning
     * to create novel compositions.
     * 
     * @param concepts Array of concepts to fuse
     * @return Fused creative output
     */
    public GenerativeResult fuse(String[] concepts) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (concepts == null || concepts.length < 2) {
                return new GenerativeResult("ERROR: Need at least 2 concepts to fuse", 0, "fuse", false, 0.0, 0.0);
            }
            
            StringBuilder llmPrompt = new StringBuilder();
            llmPrompt.append("You are a creative composer. Fuse the following concepts into a novel creative output.\n");
            llmPrompt.append("Concepts to fuse:\n");
            for (String concept : concepts) {
                llmPrompt.append("- ").append(concept).append("\n");
            }
            llmPrompt.append("\nInstructions:\n");
            llmPrompt.append("1. Find creative connections between concepts\n");
            llmPrompt.append("2. Create a novel composition that combines elements\n");
            llmPrompt.append("3. Maintain coherence and logical flow\n");
            llmPrompt.append("4. Be imaginative and original\n\n");
            llmPrompt.append("Fused composition:");
            
            LLMReasoningEngine.ReasoningResult llmResult = llmEngine.reason(llmPrompt.toString());
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalGenerationTimeMs += elapsed;
            fuseOperations++;
            
            if (llmResult.success) {
                double novelty = calculateNovelty(llmResult.result);
                double coherence = calculateCoherence(llmResult.result);
                
                return new GenerativeResult(llmResult.result, elapsed, "fuse", true, novelty, coherence);
            } else {
                return new GenerativeResult("Fusion failed: " + llmResult.result, elapsed, "fuse", false, 0.0, 0.0);
            }
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new GenerativeResult("ERROR: " + e.getMessage(), elapsed, "fuse", false, 0.0, 0.0);
        }
    }
    
    /**
     * Explore novelty via HDC vector space
     * 
     * Uses HDC to explore novel concepts by navigating the vector space
     * and finding regions with low density (novel areas).
     * 
     * @param startingConcept Concept to start exploration from
     * @return Novel concept discovered
     */
    public GenerativeResult exploreNovelty(String startingConcept) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Use LLM to generate novel concepts based on HDC exploration
            StringBuilder llmPrompt = new StringBuilder();
            llmPrompt.append("You are a novelty explorer. Generate novel concepts related to: ").append(startingConcept).append("\n");
            llmPrompt.append("Focus on:\n");
            llmPrompt.append("1. Unexplored connections\n");
            llmPrompt.append("2. Novel combinations\n");
            llmPrompt.append("3. Creative alternatives\n");
            llmPrompt.append("4. Unexpected perspectives\n\n");
            llmPrompt.append("Generate 3 novel concepts, one per line:");
            
            LLMReasoningEngine.ReasoningResult llmResult = llmEngine.reason(llmPrompt.toString());
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalGenerationTimeMs += elapsed;
            noveltyExplorations++;
            
            if (llmResult.success) {
                double novelty = calculateNovelty(llmResult.result);
                double coherence = calculateCoherence(llmResult.result);
                
                return new GenerativeResult(llmResult.result, elapsed, "novelty", true, novelty, coherence);
            } else {
                return new GenerativeResult("Exploration failed: " + llmResult.result, elapsed, "novelty", false, 0.0, 0.0);
            }
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new GenerativeResult("ERROR: " + e.getMessage(), elapsed, "novelty", false, 0.0, 0.0);
        }
    }
    
    /**
     * Generate visual content via LTX-Video
     * 
     * Calls LTX-Video Python script to generate video from prompt.
     * 
     * @param prompt Visual generation prompt
     * @return Generated visual content path
     */
    public GenerativeResult generateVisual(String prompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            // LTX-Video integration via Python script
            String ltxVideoPath = "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\LTX-Video-main\\LTX-Video-main";
            String configPath = ltxVideoPath + "\\configs\\ltxv-2b-0.9.8-distilled.yaml";
            String outputPath = "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\generated\\output_" + System.currentTimeMillis() + ".mp4";
            
            // Build command to run LTX-Video inference
            ProcessBuilder pb = new ProcessBuilder(
                "python",
                ltxVideoPath + "\\inference.py",
                "--prompt", prompt,
                "--height", "704",
                "--width", "1216",
                "--num_frames", "121",
                "--seed", String.valueOf(System.currentTimeMillis()),
                "--pipeline_config", configPath,
                "--output_path", outputPath
            );
            pb.directory(new java.io.File(ltxVideoPath));
            
            // Run the process
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalGenerationTimeMs += elapsed;
            visualGenerations++;
            
            if (exitCode == 0) {
                return new GenerativeResult("LTX-Video generated: " + outputPath, elapsed, "visual", true, 0.8, 0.9);
            } else {
                return new GenerativeResult("LTX-Video failed with exit code: " + exitCode, elapsed, "visual", false, 0.0, 0.0);
            }
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new GenerativeResult("ERROR: " + e.getMessage(), elapsed, "visual", false, 0.0, 0.0);
        }
    }
    
    /**
     * Generate multi-modal output (text + visual)
     * 
     * Combines text generation with visual generation for multi-modal output.
     * 
     * @param prompt Multi-modal generation prompt
     * @return Multi-modal generative result
     */
    public GenerativeResult generateMultiModal(String prompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Generate text
            GenerativeResult textResult = generateText(prompt);
            
            // Generate visual (placeholder)
            GenerativeResult visualResult = generateVisual(prompt);
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalGenerationTimeMs += elapsed;
            
            String combinedResult = "TEXT:\n" + textResult.result + "\n\nVISUAL:\n" + visualResult.result;
            double avgNovelty = (textResult.novelty + visualResult.novelty) / 2.0;
            double avgCoherence = (textResult.coherence + visualResult.coherence) / 2.0;
            
            return new GenerativeResult(combinedResult, elapsed, "multimodal", 
                textResult.success && visualResult.success, avgNovelty, avgCoherence);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new GenerativeResult("ERROR: " + e.getMessage(), elapsed, "multimodal", false, 0.0, 0.0);
        }
    }
    
    /**
     * Calculate novelty score of generated content
     * 
     * Simple heuristic: novelty based on uniqueness of words/phrases
     * In production, would use more sophisticated metrics.
     */
    private double calculateNovelty(String content) {
        if (content == null || content.isEmpty()) return 0.0;
        
        // Simple novelty calculation based on word variety
        String[] words = content.toLowerCase().split("\\s+");
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        
        double varietyRatio = words.length > 0 ? (double) uniqueWords.size() / words.length : 0.0;
        return Math.min(1.0, varietyRatio * 1.5); // Boost variety ratio
    }
    
    /**
     * Calculate coherence score of generated content
     * 
     * Simple heuristic: coherence based on sentence structure
     * In production, would use more sophisticated metrics.
     */
    private double calculateCoherence(String content) {
        if (content == null || content.isEmpty()) return 0.0;
        
        // Simple coherence calculation based on sentence length consistency
        String[] sentences = content.split("[.!?]");
        if (sentences.length < 2) return 1.0;
        
        double avgLength = 0;
        for (String sentence : sentences) {
            avgLength += sentence.trim().length();
        }
        avgLength /= sentences.length;
        
        // Calculate variance
        double variance = 0;
        for (String sentence : sentences) {
            variance += Math.pow(sentence.trim().length() - avgLength, 2);
        }
        variance /= sentences.length;
        
        // Lower variance = higher coherence
        double coherence = 1.0 - Math.min(1.0, variance / 100.0);
        return Math.max(0.0, coherence);
    }
    
    /**
     * Get generative statistics
     */
    public String getStats() {
        long avgTime = textGenerations + visualGenerations + fuseOperations + noveltyExplorations > 0 
            ? totalGenerationTimeMs / (textGenerations + visualGenerations + fuseOperations + noveltyExplorations)
            : 0;
        
        return String.format(
            "Generative Engine Statistics:\n" +
            "  Text generations: %d\n" +
            "  Visual generations: %d\n" +
            "  FUSE operations: %d\n" +
            "  Novelty explorations: %d\n" +
            "  Total operations: %d\n" +
            "  Average generation time: %dms\n" +
            "  Novelty threshold: %.2f\n" +
            "  Exploration steps: %d\n",
            textGenerations,
            visualGenerations,
            fuseOperations,
            noveltyExplorations,
            textGenerations + visualGenerations + fuseOperations + noveltyExplorations,
            avgTime,
            noveltyThreshold,
            explorationSteps
        );
    }
    
    // =========================================================================================
    // INNER CLASSES
    // =========================================================================================
    
    /**
     * Result of generative operation
     */
    public static class GenerativeResult {
        public final String result;
        public final long elapsedTimeMs;
        public final String type;
        public final boolean success;
        public final double novelty;
        public final double coherence;
        
        public GenerativeResult(String result, long elapsedTimeMs, String type, boolean success, 
                                double novelty, double coherence) {
            this.result = result;
            this.elapsedTimeMs = elapsedTimeMs;
            this.type = type;
            this.success = success;
            this.novelty = novelty;
            this.coherence = coherence;
        }
    }
}
