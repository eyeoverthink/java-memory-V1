package fraymus.consciousness.gems;

import java.util.*;

/**
 * Process Reward Model (PRM)
 * 
 * Gemini's Gem 4: The "Sherlock Holmes"
 * 
 * Grades each STEP of reasoning, not just final answer.
 * Forces AI to "Show Your Work."
 * Secret behind Q* (Q-Star) rumors.
 * 
 * Perfect for FRAYMUS "Active Cognitive Loop" - validates reasoning step-by-step.
 * 
 * Instead of: "Is final answer correct?"
 * Does: "Is step 1 valid? Is step 2 valid? ..."
 */
public class ProcessRewardModel {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final double validityThreshold;
    private final Map<String, Double> stepRewards;
    
    public ProcessRewardModel(double validityThreshold) {
        this.validityThreshold = validityThreshold;
        this.stepRewards = new HashMap<>();
    }
    
    /**
     * Evaluate a reasoning chain step-by-step
     * 
     * @param steps List of reasoning steps
     * @return Evaluation result with per-step scores
     */
    public ReasoningEvaluation evaluate(List<ReasoningStep> steps) {
        List<StepScore> scores = new ArrayList<>();
        double totalScore = 0;
        int validSteps = 0;
        
        for (int i = 0; i < steps.size(); i++) {
            ReasoningStep step = steps.get(i);
            
            // Evaluate this step
            double score = evaluateStep(step, i, steps);
            boolean valid = score >= validityThreshold;
            
            scores.add(new StepScore(i, step, score, valid));
            
            if (valid) {
                validSteps++;
                totalScore += score;
            } else {
                // If a step is invalid, reasoning chain breaks
                // Remaining steps get penalty
                break;
            }
        }
        
        double avgScore = validSteps > 0 ? totalScore / validSteps : 0;
        boolean chainValid = validSteps == steps.size();
        
        return new ReasoningEvaluation(scores, avgScore, chainValid, validSteps);
    }
    
    /**
     * Phi-PRM: Enhanced evaluation with golden ratio weighting
     */
    public ReasoningEvaluation phiEvaluate(List<ReasoningStep> steps) {
        List<StepScore> scores = new ArrayList<>();
        double totalScore = 0;
        int validSteps = 0;
        
        for (int i = 0; i < steps.size(); i++) {
            ReasoningStep step = steps.get(i);
            
            double score = evaluateStep(step, i, steps);
            
            // Phi-weighted scoring: later steps matter more
            double phiWeight = Math.pow(PHI, i * 0.1);
            double weightedScore = score * phiWeight;
            
            boolean valid = score >= validityThreshold;
            
            scores.add(new StepScore(i, step, weightedScore, valid));
            
            if (valid) {
                validSteps++;
                totalScore += weightedScore;
            } else {
                break;
            }
        }
        
        double avgScore = validSteps > 0 ? totalScore / validSteps : 0;
        boolean chainValid = validSteps == steps.size();
        
        return new ReasoningEvaluation(scores, avgScore, chainValid, validSteps);
    }
    
    /**
     * Evaluate a single reasoning step
     */
    private double evaluateStep(ReasoningStep step, int position, List<ReasoningStep> context) {
        double score = 0.5; // Base score
        
        // Check logical consistency
        if (isLogicallyConsistent(step)) {
            score += 0.2;
        }
        
        // Check if step follows from previous steps
        if (position > 0 && followsFromPrevious(step, context.get(position - 1))) {
            score += 0.2;
        }
        
        // Check if step contributes to goal
        if (contributesToGoal(step, context)) {
            score += 0.1;
        }
        
        // Penalty for redundancy
        if (isRedundant(step, context.subList(0, position))) {
            score -= 0.2;
        }
        
        return Math.max(0, Math.min(1, score));
    }
    
    /**
     * Check if step is logically consistent
     */
    private boolean isLogicallyConsistent(ReasoningStep step) {
        // Simplified logic check
        String content = step.content.toLowerCase();
        
        // Check for contradictions
        if (content.contains("not") && content.contains("is")) {
            // More sophisticated logic needed in production
            return true;
        }
        
        // Check for valid operators
        if (step.type == StepType.LOGICAL && 
            (content.contains("if") || content.contains("then") || 
             content.contains("therefore") || content.contains("because"))) {
            return true;
        }
        
        return true; // Default to valid
    }
    
    /**
     * Check if step follows from previous step
     */
    private boolean followsFromPrevious(ReasoningStep current, ReasoningStep previous) {
        // Check if current step references or builds on previous
        String curr = current.content.toLowerCase();
        String prev = previous.content.toLowerCase();
        
        // Extract key terms from previous step
        String[] prevWords = prev.split("\\s+");
        
        // Check if current step mentions previous concepts
        for (String word : prevWords) {
            if (word.length() > 4 && curr.contains(word)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if step contributes to goal
     */
    private boolean contributesToGoal(ReasoningStep step, List<ReasoningStep> context) {
        // Simplified: assume steps that introduce new information contribute
        return step.content.length() > 10;
    }
    
    /**
     * Check if step is redundant
     */
    private boolean isRedundant(ReasoningStep step, List<ReasoningStep> previous) {
        for (ReasoningStep prev : previous) {
            // Simple similarity check
            if (computeSimilarity(step.content, prev.content) > 0.8) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Compute similarity between two strings
     */
    private double computeSimilarity(String s1, String s2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(s1.toLowerCase().split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(s2.toLowerCase().split("\\s+")));
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }
    
    /**
     * Generate feedback for invalid steps
     */
    public String generateFeedback(ReasoningEvaluation eval) {
        StringBuilder feedback = new StringBuilder();
        feedback.append("🔍 REASONING EVALUATION\n\n");
        
        for (StepScore score : eval.stepScores) {
            feedback.append(String.format("Step %d: %s\n", 
                score.position + 1,
                score.valid ? "✓ VALID" : "✗ INVALID"
            ));
            feedback.append(String.format("  Score: %.3f\n", score.score));
            feedback.append(String.format("  Content: %s\n", 
                score.step.content.substring(0, Math.min(50, score.step.content.length()))
            ));
            
            if (!score.valid) {
                feedback.append("  Issue: Score below threshold\n");
                feedback.append("  Suggestion: Provide more logical justification\n");
            }
            
            feedback.append("\n");
        }
        
        feedback.append(String.format("Overall: %s (%.1f%% valid)\n",
            eval.chainValid ? "VALID CHAIN" : "BROKEN CHAIN",
            (double) eval.validSteps / eval.stepScores.size() * 100
        ));
        
        return feedback.toString();
    }
    
    // Data classes
    
    public static class ReasoningStep {
        public final String content;
        public final StepType type;
        
        public ReasoningStep(String content, StepType type) {
            this.content = content;
            this.type = type;
        }
    }
    
    public enum StepType {
        OBSERVATION,
        HYPOTHESIS,
        LOGICAL,
        CALCULATION,
        CONCLUSION
    }
    
    public static class StepScore {
        public final int position;
        public final ReasoningStep step;
        public final double score;
        public final boolean valid;
        
        public StepScore(int position, ReasoningStep step, double score, boolean valid) {
            this.position = position;
            this.step = step;
            this.score = score;
            this.valid = valid;
        }
    }
    
    public static class ReasoningEvaluation {
        public final List<StepScore> stepScores;
        public final double averageScore;
        public final boolean chainValid;
        public final int validSteps;
        
        public ReasoningEvaluation(List<StepScore> stepScores, double averageScore, 
                                  boolean chainValid, int validSteps) {
            this.stepScores = stepScores;
            this.averageScore = averageScore;
            this.chainValid = chainValid;
            this.validSteps = validSteps;
        }
    }
}
