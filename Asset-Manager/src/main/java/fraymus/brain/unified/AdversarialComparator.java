package fraymus.brain.unified;

import java.util.*;

/**
 * AdversarialComparator pits brains against each other to generate critiques and refine scores.
 * 
 * Adversarial rounds:
 * - Logical brain critiques Creative brain's solution
 * - Security brain attacks Physics brain's solution
 * - Evolutionary brain assesses survival of all solutions
 */
public class AdversarialComparator {
    
    private final SolutionScorer scorer;
    
    public AdversarialComparator() {
        this.scorer = new SolutionScorer();
    }
    
    /**
     * Generate critiques by having brains critique each other's solutions.
     * 
     * @param solutions Map of brain name to solution text
     * @param problem The problem description
     * @return Map of brain name to critique text
     */
    public Map<String, String> generateCritiques(Map<String, String> solutions, String problem) {
        Map<String, String> critiques = new HashMap<>();
        
        for (Map.Entry<String, String> entry : solutions.entrySet()) {
            String brainName = entry.getKey();
            String solution = entry.getValue();
            
            String critique = generateCritiqueForBrain(brainName, solution, solutions, problem);
            critiques.put(brainName, critique);
        }
        
        return critiques;
    }
    
    /**
     * Generate a critique for a specific brain based on its role.
     */
    private String generateCritiqueForBrain(String brainName, String solution, 
                                              Map<String, String> allSolutions, String problem) {
        StringBuilder critique = new StringBuilder();
        
        // Determine brain role based on name
        if (brainName.toLowerCase().contains("logical")) {
            critique.append(generateLogicalCritique(solution, allSolutions));
        } else if (brainName.toLowerCase().contains("security")) {
            critique.append(generateSecurityCritique(solution, allSolutions));
        } else if (brainName.toLowerCase().contains("evolutionary")) {
            critique.append(generateEvolutionaryCritique(solution, allSolutions));
        } else if (brainName.toLowerCase().contains("creative")) {
            critique.append(generateCreativeCritique(solution, allSolutions));
        } else if (brainName.toLowerCase().contains("physical") || brainName.toLowerCase().contains("physics")) {
            critique.append(generatePhysicalCritique(solution, allSolutions));
        } else {
            critique.append(generateGenericCritique(solution, allSolutions));
        }
        
        return critique.toString();
    }
    
    /**
     * Logical brain critique: Check for correctness, proofs, edge cases.
     */
    private String generateLogicalCritique(String solution, Map<String, String> allSolutions) {
        StringBuilder critique = new StringBuilder();
        
        // Check for logical structure
        if (!solution.contains("if") && !solution.contains("when") && !solution.contains("then")) {
            critique.append("Missing conditional logic. ");
        }
        
        // Check for proof or justification
        if (!solution.toLowerCase().contains("because") && 
            !solution.toLowerCase().contains("therefore") &&
            !solution.toLowerCase().contains("thus")) {
            critique.append("Lacks logical justification. ");
        }
        
        // Check for completeness
        if (solution.length() < 50) {
            critique.append("Too brief - needs more detail. ");
        }
        
        // Compare with other solutions
        for (Map.Entry<String, String> entry : allSolutions.entrySet()) {
            if (!entry.getKey().toLowerCase().contains("logical") && 
                entry.getValue().length() > solution.length()) {
                critique.append("Less detailed than ").append(entry.getKey()).append(". ");
            }
        }
        
        return critique.length() > 0 ? critique.toString() : "Logically sound.";
    }
    
    /**
     * Security brain critique: Check for vulnerabilities, attack surfaces.
     */
    private String generateSecurityCritique(String solution, Map<String, String> allSolutions) {
        StringBuilder critique = new StringBuilder();
        
        // Check for security considerations
        if (!solution.toLowerCase().contains("security") && 
            !solution.toLowerCase().contains("vulnerability") &&
            !solution.toLowerCase().contains("attack")) {
            critique.append("Missing security analysis. ");
        }
        
        // Check for input validation
        if (!solution.toLowerCase().contains("validate") && 
            !solution.toLowerCase().contains("sanitize")) {
            critique.append("No input validation mentioned. ");
        }
        
        // Check for error handling
        if (!solution.contains("try") && !solution.contains("catch")) {
            critique.append("Lacks error handling - potential for crashes. ");
        }
        
        // Check for hardcoded values
        if (solution.matches(".*\\d+.*") && !solution.toLowerCase().contains("config")) {
            critique.append("May contain hardcoded values. ");
        }
        
        return critique.length() > 0 ? critique.toString() : "Security adequate.";
    }
    
    /**
     * Evolutionary brain critique: Assess fitness, survival value.
     */
    private String generateEvolutionaryCritique(String solution, Map<String, String> allSolutions) {
        StringBuilder critique = new StringBuilder();
        
        // Check for adaptability
        if (!solution.toLowerCase().contains("scalable") && 
            !solution.toLowerCase().contains("flexible") &&
            !solution.toLowerCase().contains("adaptive")) {
            critique.append("Limited adaptability. ");
        }
        
        // Check for resource efficiency
        if (!solution.toLowerCase().contains("efficient") && 
            !solution.toLowerCase().contains("optimize")) {
            critique.append("Resource-intensive approach. ");
        }
        
        // Check for survival under constraints
        if (solution.length() < 30) {
            critique.append("Too simple - may not survive edge cases. ");
        }
        
        // Compare with other solutions for fitness
        int myComplexity = solution.split(" ").length;
        for (Map.Entry<String, String> entry : allSolutions.entrySet()) {
            if (!entry.getKey().toLowerCase().contains("evolutionary")) {
                int otherComplexity = entry.getValue().split(" ").length;
                if (otherComplexity < myComplexity && otherComplexity > 10) {
                    critique.append("More complex than ").append(entry.getKey()).append(". ");
                }
            }
        }
        
        return critique.length() > 0 ? critique.toString() : "Evolutionarily stable.";
    }
    
    /**
     * Creative brain critique: Check for novelty, innovation.
     */
    private String generateCreativeCritique(String solution, Map<String, String> allSolutions) {
        StringBuilder critique = new StringBuilder();
        
        // Check for novelty
        if (!solution.toLowerCase().contains("novel") && 
            !solution.toLowerCase().contains("innovative") &&
            !solution.toLowerCase().contains("unique")) {
            critique.append("Lacks clear novelty. ");
        }
        
        // Check for unconventional approach
        if (!solution.toLowerCase().contains("quantum") && 
            !solution.toLowerCase().contains("probabilistic") &&
            !solution.toLowerCase().contains("heuristic")) {
            critique.append("Uses conventional approach. ");
        }
        
        // Check for creativity indicators
        String[] creativeWords = {"imagine", "explore", "experiment", "discover"};
        boolean hasCreativeWord = false;
        for (String word : creativeWords) {
            if (solution.toLowerCase().contains(word)) {
                hasCreativeWord = true;
                break;
            }
        }
        if (!hasCreativeWord) {
            critique.append("Could be more exploratory. ");
        }
        
        return critique.length() > 0 ? critique.toString() : "Creatively adequate.";
    }
    
    /**
     * Physical brain critique: Check for physical constraints, energy, entropy.
     */
    private String generatePhysicalCritique(String solution, Map<String, String> allSolutions) {
        StringBuilder critique = new StringBuilder();
        
        // Check for physical considerations
        if (!solution.toLowerCase().contains("energy") && 
            !solution.toLowerCase().contains("power") &&
            !solution.toLowerCase().contains("resource")) {
            critique.append("Missing resource analysis. ");
        }
        
        // Check for physical constraints
        if (!solution.toLowerCase().contains("limit") && 
            !solution.toLowerCase().contains("constraint") &&
            !solution.toLowerCase().contains("bound")) {
            critique.append("No physical constraints identified. ");
        }
        
        return critique.length() > 0 ? critique.toString() : "Physically sound.";
    }
    
    /**
     * Generic critique for other brain types.
     */
    private String generateGenericCritique(String solution, Map<String, String> allSolutions) {
        StringBuilder critique = new StringBuilder();
        
        // Basic completeness check
        if (solution.length() < 20) {
            critique.append("Too brief. ");
        }
        
        // Check for structure
        if (!solution.contains(".") && !solution.contains("\n")) {
            critique.append("Lacks structure. ");
        }
        
        return critique.length() > 0 ? critique.toString() : "Adequate.";
    }
    
    /**
     * Refine scores based on critiques.
     * 
     * @param initialScores Initial scores from SolutionScorer
     * @param critiques Critiques from adversarial round
     * @return Refined scores
     */
    public Map<String, Double> refineScores(Map<String, Double> initialScores, 
                                            Map<String, String> critiques) {
        Map<String, Double> refinedScores = new HashMap<>();
        
        for (Map.Entry<String, Double> entry : initialScores.entrySet()) {
            String brainName = entry.getKey();
            double initialScore = entry.getValue();
            String critique = critiques.get(brainName);
            
            // Adjust score based on critique severity
            double adjustment = calculateCritiqueAdjustment(critique);
            double refinedScore = initialScore + adjustment;
            
            // Clamp to [0, 1]
            refinedScore = Math.max(0.0, Math.min(1.0, refinedScore));
            
            refinedScores.put(brainName, refinedScore);
        }
        
        return refinedScores;
    }
    
    /**
     * Calculate score adjustment based on critique severity.
     */
    private double calculateCritiqueAdjustment(String critique) {
        if (critique == null || critique.isEmpty()) return 0.0;
        
        double adjustment = 0.0;
        
        // Negative adjustments for criticisms
        String[] negativeKeywords = {"missing", "lacks", "no", "not", "too", "limited"};
        for (String keyword : negativeKeywords) {
            if (critique.toLowerCase().contains(keyword)) {
                adjustment -= 0.05;
            }
        }
        
        // Positive adjustments for praise
        String[] positiveKeywords = {"sound", "adequate", "good", "excellent", "optimal"};
        for (String keyword : positiveKeywords) {
            if (critique.toLowerCase().contains(keyword)) {
                adjustment += 0.03;
            }
        }
        
        return adjustment;
    }
    
    /**
     * Run full adversarial comparison round.
     * 
     * @param solutions Map of brain name to solution
     * @param problem The problem description
     * @return AdversarialComparisonResult with critiques and refined scores
     */
    public AdversarialComparisonResult runComparison(Map<String, String> solutions, String problem) {
        // Generate initial scores
        Map<String, Double> initialScores = scorer.scoreMultipleSolutions(solutions, problem);
        
        // Generate critiques
        Map<String, String> critiques = generateCritiques(solutions, problem);
        
        // Refine scores based on critiques
        Map<String, Double> refinedScores = refineScores(initialScores, critiques);
        
        return new AdversarialComparisonResult(initialScores, critiques, refinedScores);
    }
    
    /**
     * Result container for adversarial comparison.
     */
    public static class AdversarialComparisonResult {
        private final Map<String, Double> initialScores;
        private final Map<String, String> critiques;
        private final Map<String, Double> refinedScores;
        
        public AdversarialComparisonResult(Map<String, Double> initialScores,
                                          Map<String, String> critiques,
                                          Map<String, Double> refinedScores) {
            this.initialScores = initialScores;
            this.critiques = critiques;
            this.refinedScores = refinedScores;
        }
        
        public Map<String, Double> getInitialScores() { return initialScores; }
        public Map<String, String> getCritiques() { return critiques; }
        public Map<String, Double> getRefinedScores() { return refinedScores; }
    }
}
