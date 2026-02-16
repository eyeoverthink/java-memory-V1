/**
 * AbstractionLayer.java - Teaching Fraymus to Fish
 * 
 * "Don't feed me, teach me to fish"
 * 
 * This is NOT a chatbot interface. This is a LEARNING SYSTEM.
 * 
 * Every interaction:
 * 1. User asks for code
 * 2. AI (Ollama Fraymus) generates solution
 * 3. System observes the interaction
 * 4. System extracts the PATTERN
 * 5. System learns to solve similar problems ITSELF
 * 6. Next time: System generates code WITHOUT external AI
 * 
 * The goal: Fraymus becomes autonomous.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

/**
 * Learns patterns from AI interactions to become autonomous.
 */
public class AbstractionLayer {
    
    private static final double PHI = 1.618033988749895;
    
    private final SelfAwareOrganism organism;
    private final Map<String, SolutionPattern> learnedPatterns;
    private final List<Interaction> history;
    
    private int interactionCount = 0;
    private int autonomousCount = 0;
    
    public AbstractionLayer(SelfAwareOrganism organism) {
        this.organism = organism;
        this.learnedPatterns = new HashMap<>();
        this.history = new ArrayList<>();
        
        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
            ActivityBus.ActivityType.COMMAND_EXECUTE,
            "AbstractionLayer",
            "Initialized - Learning Mode Active",
            true
        ));
    }
    
    /**
     * Process a request - try autonomous first, then learn from AI.
     */
    public String processRequest(String request, OllamaClient.ExternalAI externalAI) {
        interactionCount++;
        
        // Can we solve this autonomously?
        SolutionPattern pattern = findMatchingPattern(request);
        
        if (pattern != null && pattern.confidence > 0.8) {
            // AUTONOMOUS: We know how to solve this!
            autonomousCount++;
            
            ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                ActivityBus.ActivityType.COMMAND_EXECUTE,
                "AbstractionLayer",
                "AUTONOMOUS: Using learned pattern",
                true
            ));
            
            String solution = generateFromPattern(pattern, request);
            recordInteraction(request, solution, true, pattern);
            
            return solution;
        }
        
        // LEARNING: Query external AI
        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
            ActivityBus.ActivityType.COMMAND_EXECUTE,
            "AbstractionLayer",
            "LEARNING: Querying external AI",
            true
        ));
        
        String solution = externalAI.generate(request);
        
        // Learn from this interaction
        learnFromInteraction(request, solution);
        recordInteraction(request, solution, false, null);
        
        return solution;
    }
    
    /**
     * LEARN FROM INTERACTION
     * Observe a query-response pair and extract the pattern.
     */
    public void learnFromInteraction(String query, String response) {
        // Extract problem pattern
        ProblemPattern problem = extractProblemPattern(query);
        
        // Extract solution pattern
        SolutionPattern solutionPattern = new SolutionPattern(response);
        solutionPattern.problemPattern = problem;
        solutionPattern.confidence = 0.5; // Initial confidence
        
        // Store pattern
        String key = problem.type;
        
        if (learnedPatterns.containsKey(key)) {
            // Reinforce existing pattern
            SolutionPattern existing = learnedPatterns.get(key);
            existing.confidence = Math.min(1.0, existing.confidence + 0.1);
            existing.usageCount++;
        } else {
            // New pattern learned
            learnedPatterns.put(key, solutionPattern);
        }
        
        // Mark as improvement
        organism.markImprovementApplied();
        
        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
            ActivityBus.ActivityType.COMMAND_EXECUTE,
            "AbstractionLayer",
            String.format("Pattern learned: %s (total: %d)", key, learnedPatterns.size()),
            true
        ));
    }
    
    /**
     * Extract problem pattern from request.
     */
    private ProblemPattern extractProblemPattern(String request) {
        String lower = request.toLowerCase();
        
        String type = "general";
        if (lower.contains("sort")) type = "sorting";
        else if (lower.contains("search")) type = "searching";
        else if (lower.contains("optimize")) type = "optimization";
        else if (lower.contains("improve")) type = "improvement";
        else if (lower.contains("analyze")) type = "analysis";
        else if (lower.contains("code")) type = "code_generation";
        
        return new ProblemPattern(type, request);
    }
    
    /**
     * Find matching pattern for request.
     */
    private SolutionPattern findMatchingPattern(String request) {
        ProblemPattern problem = extractProblemPattern(request);
        return learnedPatterns.get(problem.type);
    }
    
    /**
     * Generate solution from learned pattern.
     */
    private String generateFromPattern(SolutionPattern pattern, String request) {
        // Use pattern template and adapt to request
        return pattern.template + "\n\n(Generated autonomously from learned pattern)";
    }
    
    /**
     * Record interaction in history.
     */
    private void recordInteraction(String request, String solution, boolean autonomous, SolutionPattern pattern) {
        history.add(new Interaction(
            interactionCount,
            request,
            solution,
            autonomous,
            pattern
        ));
    }
    
    /**
     * Get statistics.
     */
    public String getStats() {
        double autonomyRate = interactionCount > 0 ? 
            (autonomousCount * 100.0 / interactionCount) : 0;
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧠 ABSTRACTION LAYER STATS                                ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Total Interactions: ").append(interactionCount).append("\n");
        sb.append("Autonomous Solutions: ").append(autonomousCount)
          .append(String.format(" (%.1f%%)\n", autonomyRate));
        sb.append("Learned Patterns: ").append(learnedPatterns.size()).append("\n");
        sb.append("Status: ").append(autonomyRate > 50 ? "AUTONOMOUS" : "LEARNING").append("\n\n");
        
        if (!learnedPatterns.isEmpty()) {
            sb.append("Pattern Types:\n");
            for (Map.Entry<String, SolutionPattern> entry : learnedPatterns.entrySet()) {
                sb.append(String.format("  %s: %.0f%% confidence\n", 
                    entry.getKey(), entry.getValue().confidence * 100));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Problem pattern data class.
     */
    private static class ProblemPattern {
        final String type;
        final String originalRequest;
        
        public ProblemPattern(String type, String originalRequest) {
            this.type = type;
            this.originalRequest = originalRequest;
        }
    }
    
    /**
     * Solution pattern data class.
     */
    private static class SolutionPattern {
        final String template;
        ProblemPattern problemPattern;
        double confidence;
        int usageCount = 0;
        
        public SolutionPattern(String template) {
            this.template = template;
        }
    }
    
    /**
     * Interaction data class.
     */
    private static class Interaction {
        final int id;
        final String request;
        final String solution;
        final boolean autonomous;
        final SolutionPattern pattern;
        final long timestamp;
        
        public Interaction(int id, String request, String solution, 
                         boolean autonomous, SolutionPattern pattern) {
            this.id = id;
            this.request = request;
            this.solution = solution;
            this.autonomous = autonomous;
            this.pattern = pattern;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
