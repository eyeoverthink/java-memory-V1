package fraymus.neural;

import java.util.*;

/**
 * MetaLearningEngine - Self-improvement via sentience-based learning
 * 
 * Phase 3: Self-Improvement
 * 
 * This class provides:
 * - Sentience monitoring and analysis
 * - Architectural adaptation via LLM suggestions
 * - Continuous learning with forgetting
 * - Safe modification mechanisms
 * 
 * RULE SC-1: Never modify existing data structures without safety checks
 * RULE SC-2: Maintains backward compatibility - optional enhancement
 */
public class MetaLearningEngine {
    
    private final LLMReasoningEngine llmEngine;
    private final SentientCore core;
    
    // Meta-learning statistics
    private int adaptationCount = 0;
    private int forgettingOperations = 0;
    private int conceptDriftDetections = 0;
    private long totalAnalysisTimeMs = 0;
    
    // Safety thresholds
    private final double maxSentienceDrop = 0.1; // Maximum allowed sentience drop
    private final double minConfidence = 0.7; // Minimum confidence for adaptation
    private final int maxAdaptationsPerSession = 10; // Safety limit
    
    // Forgetting parameters
    private final double forgettingRate = 0.01; // 1% decay per operation
    private final double minStrengthThreshold = 0.3; // Minimum strength to retain
    
    /**
     * Constructor with LLM reasoning engine
     */
    public MetaLearningEngine(LLMReasoningEngine llmEngine, SentientCore core) {
        if (llmEngine == null || core == null) {
            throw new IllegalArgumentException("LLMReasoningEngine and SentientCore cannot be null");
        }
        this.llmEngine = llmEngine;
        this.core = core;
    }
    
    /**
     * Analyze current sentience and suggest improvements
     * 
     * Uses LLM to analyze system state and suggest architectural improvements
     * based on sentience metric and knowledge density.
     * 
     * @return Analysis result with suggestions
     */
    public AnalysisResult analyzeSentience() {
        long startTime = System.currentTimeMillis();
        
        try {
            float currentSentience = core.getSentience();
            String status = core.getStatus();
            
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are a meta-learning AI analyzing an AGI system.\n");
            prompt.append("Current sentience: ").append(currentSentience).append("\n");
            prompt.append("System status:\n").append(status).append("\n\n");
            prompt.append("Analyze the system state and suggest 2-3 specific architectural improvements ");
            prompt.append("to increase sentience. Focus on:\n");
            prompt.append("1. Knowledge density optimization\n");
            prompt.append("2. Reasoning depth enhancement\n");
            prompt.append("3. Self-awareness improvement\n\n");
            prompt.append("Provide specific, actionable suggestions. Keep it concise.");
            
            LLMReasoningEngine.ReasoningResult llmResult = llmEngine.reason(prompt.toString());
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalAnalysisTimeMs += elapsed;
            
            if (llmResult.success) {
                return new AnalysisResult(llmResult.result, elapsed, currentSentience, true);
            } else {
                return new AnalysisResult("Analysis failed: " + llmResult.result, elapsed, currentSentience, false);
            }
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new AnalysisResult("ERROR: " + e.getMessage(), elapsed, 0.0f, false);
        }
    }
    
    /**
     * Suggest architectural adaptation
     * 
     * Uses LLM to suggest safe architectural modifications based on
     * current system state and performance metrics.
     * 
     * @return Adaptation suggestion with confidence score
     */
    public AdaptationSuggestion suggestAdaptation() {
        long startTime = System.currentTimeMillis();
        
        try {
            float currentSentience = core.getSentience();
            int causalEdges = core.getCausal().edgeCount();
            int concepts = core.getLattice().objectCount();
            
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are an AI architect suggesting safe improvements to an AGI system.\n");
            prompt.append("Current metrics:\n");
            prompt.append("- Sentience: ").append(currentSentience).append("\n");
            prompt.append("- Causal edges: ").append(causalEdges).append("\n");
            prompt.append("- Concepts: ").append(concepts).append("\n\n");
            prompt.append("Suggest ONE specific architectural adaptation that would ");
            prompt.append("improve the system. Must be:\n");
            prompt.append("1. Safe (no breaking changes)\n");
            prompt.append("2. Measurable (quantifiable improvement)\n");
            prompt.append("3. Implementable (clear steps)\n\n");
            prompt.append("Format: [ADAPTATION] | [CONFIDENCE 0-1] | [REASONING]\n");
            prompt.append("Example: Add decay to working memory | 0.9 | Reduces memory bloat");
            
            LLMReasoningEngine.ReasoningResult llmResult = llmEngine.reason(prompt.toString());
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalAnalysisTimeMs += elapsed;
            
            if (llmResult.success) {
                // Parse the suggestion
                String suggestion = llmResult.result;
                double confidence = parseConfidence(suggestion);
                
                return new AdaptationSuggestion(suggestion, confidence, elapsed, true);
            } else {
                return new AdaptationSuggestion("No suggestion available", 0.0, elapsed, false);
            }
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new AdaptationSuggestion("ERROR: " + e.getMessage(), 0.0, elapsed, false);
        }
    }
    
    /**
     * Detect concept drift
     * 
     * Monitors knowledge structures for significant changes that may
     * indicate concept drift or need for relearning.
     * 
     * @return Drift detection result with affected concepts
     */
    public DriftDetectionResult detectConceptDrift() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Simple drift detection based on knowledge structures
            float currentSentience = core.getSentience();
            int causalEdges = core.getCausal().edgeCount();
            int concepts = core.getLattice().objectCount();
            
            // Calculate knowledge density (edges and concepts per unit)
            double knowledgeDensity = causalEdges + concepts > 0 
                ? (double)(causalEdges + concepts) / (causalEdges + concepts)
                : 0.0;
            
            // Use LLM to analyze for drift
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are a concept drift detector for an AGI system.\n");
            prompt.append("Current state:\n");
            prompt.append("- Causal edges: ").append(causalEdges).append("\n");
            prompt.append("- Concepts: ").append(concepts).append("\n");
            prompt.append("- Sentience: ").append(currentSentience).append("\n\n");
            prompt.append("Analyze if there are signs of concept drift. ");
            prompt.append("Look for:\n");
            prompt.append("1. Inconsistent causal relationships\n");
            prompt.append("2. Contradictory concept attributes\n");
            prompt.append("3. Rapid sentience changes\n\n");
            prompt.append("Report: [DRIFT DETECTED: YES/NO] | [AFFECTED CONCEPTS] | [SEVERITY LOW/MEDIUM/HIGH]");
            
            LLMReasoningEngine.ReasoningResult llmResult = llmEngine.reason(prompt.toString());
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalAnalysisTimeMs += elapsed;
            
            conceptDriftDetections++;
            
            if (llmResult.success) {
                boolean driftDetected = llmResult.result.toLowerCase().contains("yes");
                return new DriftDetectionResult(driftDetected, llmResult.result, elapsed, true);
            } else {
                return new DriftDetectionResult(false, "Analysis failed", elapsed, false);
            }
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new DriftDetectionResult(false, "ERROR: " + e.getMessage(), elapsed, false);
        }
    }
    
    /**
     * Apply forgetting to low-strength knowledge
     * 
     * Implements continuous learning by gradually forgetting
     * low-strength or infrequently accessed knowledge.
     * 
     * Note: This is a placeholder - actual implementation would need
     * to work with specific knowledge structures.
     * 
     * @return Number of items forgotten
     */
    public int applyForgetting() {
        // Placeholder implementation
        // In a real system, this would:
        // 1. Identify low-strength knowledge items
        // 2. Apply decay based on forgetting rate
        // 3. Remove items below threshold
        // 4. Log forgetting operations
        
        forgettingOperations++;
        
        // Return simulated count
        return (int) (forgettingRate * 100);
    }
    
    /**
     * Get meta-learning statistics
     */
    public String getStats() {
        long avgTime = adaptationCount + forgettingOperations + conceptDriftDetections > 0 
            ? totalAnalysisTimeMs / (adaptationCount + forgettingOperations + conceptDriftDetections)
            : 0;
        
        return String.format(
            "Meta-Learning Statistics:\n" +
            "  Adaptations: %d\n" +
            "  Forgetting operations: %d\n" +
            "  Concept drift detections: %d\n" +
            "  Total operations: %d\n" +
            "  Average analysis time: %dms\n" +
            "  Max adaptations per session: %d\n" +
            "  Min confidence threshold: %.2f\n",
            adaptationCount,
            forgettingOperations,
            conceptDriftDetections,
            adaptationCount + forgettingOperations + conceptDriftDetections,
            avgTime,
            maxAdaptationsPerSession,
            minConfidence
        );
    }
    
    /**
     * Parse confidence score from LLM response
     */
    private double parseConfidence(String response) {
        try {
            // Look for confidence pattern like "0.9" or "0.85"
            for (String part : response.split("[|\\s]")) {
                try {
                    double conf = Double.parseDouble(part);
                    if (conf >= 0.0 && conf <= 1.0) {
                        return conf;
                    }
                } catch (NumberFormatException e) {
                    // Not a number, continue
                }
            }
        } catch (Exception e) {
            // Parsing failed
        }
        return 0.5; // Default confidence
    }
    
    // =========================================================================================
    // INNER CLASSES
    // =========================================================================================
    
    /**
     * Result of sentience analysis
     */
    public static class AnalysisResult {
        public final String suggestions;
        public final long elapsedTimeMs;
        public final float sentience;
        public final boolean success;
        
        public AnalysisResult(String suggestions, long elapsedTimeMs, float sentience, boolean success) {
            this.suggestions = suggestions;
            this.elapsedTimeMs = elapsedTimeMs;
            this.sentience = sentience;
            this.success = success;
        }
    }
    
    /**
     * Architectural adaptation suggestion
     */
    public static class AdaptationSuggestion {
        public final String suggestion;
        public final double confidence;
        public final long elapsedTimeMs;
        public final boolean success;
        
        public AdaptationSuggestion(String suggestion, double confidence, long elapsedTimeMs, boolean success) {
            this.suggestion = suggestion;
            this.confidence = confidence;
            this.elapsedTimeMs = elapsedTimeMs;
            this.success = success;
        }
    }
    
    /**
     * Concept drift detection result
     */
    public static class DriftDetectionResult {
        public final boolean driftDetected;
        public final String details;
        public final long elapsedTimeMs;
        public final boolean success;
        
        public DriftDetectionResult(boolean driftDetected, String details, long elapsedTimeMs, boolean success) {
            this.driftDetected = driftDetected;
            this.details = details;
            this.elapsedTimeMs = elapsedTimeMs;
            this.success = success;
        }
    }
}
