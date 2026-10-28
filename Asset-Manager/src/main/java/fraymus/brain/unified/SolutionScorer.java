package fraymus.brain.unified;

import java.util.*;
import java.util.regex.*;

/**
 * SolutionScorer evaluates brain responses on multiple criteria for adversarial problem-solving.
 * 
 * Scoring criteria:
 * - Correctness: Does it solve the problem?
 * - Efficiency: Time/space complexity
 * - Novelty: Is it innovative?
 * - Robustness: Does it handle edge cases?
 * - Security: Does it have vulnerabilities?
 */
public class SolutionScorer {
    
    private static final double CORRECTNESS_WEIGHT = 0.35;
    private static final double EFFICIENCY_WEIGHT = 0.25;
    private static final double NOVELTY_WEIGHT = 0.15;
    private static final double ROBUSTNESS_WEIGHT = 0.15;
    private static final double SECURITY_WEIGHT = 0.10;
    
    /**
     * Score a single solution against a problem.
     * 
     * @param solution The brain's solution text
     * @param problem The problem description
     * @return Overall score (0.0 to 1.0)
     */
    public double scoreSolution(String solution, String problem) {
        double correctness = scoreCorrectness(solution, problem);
        double efficiency = scoreEfficiency(solution);
        double novelty = scoreNovelty(solution);
        double robustness = scoreRobustness(solution);
        double security = scoreSecurity(solution);
        
        return (correctness * CORRECTNESS_WEIGHT) +
               (efficiency * EFFICIENCY_WEIGHT) +
               (novelty * NOVELTY_WEIGHT) +
               (robustness * ROBUSTNESS_WEIGHT) +
               (security * SECURITY_WEIGHT);
    }
    
    /**
     * Score multiple solutions and return scores keyed by brain name.
     */
    public Map<String, Double> scoreMultipleSolutions(Map<String, String> solutions, String problem) {
        Map<String, Double> scores = new HashMap<>();
        for (Map.Entry<String, String> entry : solutions.entrySet()) {
            scores.put(entry.getKey(), scoreSolution(entry.getValue(), problem));
        }
        return scores;
    }
    
    /**
     * Select the best solution based on scores.
     */
    public String selectBestSolution(Map<String, Double> scores) {
        if (scores.isEmpty()) return null;
        
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    /**
     * Score correctness - does the solution actually solve the problem?
     * Heuristic: looks for keywords, structure, and completeness.
     */
    private double scoreCorrectness(String solution, String problem) {
        if (solution == null || solution.trim().isEmpty()) return 0.0;
        
        double score = 0.0;
        
        // Check for refusal or error - return 0.0 immediately
        if (solution.toLowerCase().contains("cannot") || 
            solution.toLowerCase().contains("unable") ||
            solution.toLowerCase().contains("error")) {
            return 0.0;
        }
        
        // Check for code structure (for algorithm problems)
        if (problem.toLowerCase().contains("algorithm") || 
            problem.toLowerCase().contains("code") ||
            problem.toLowerCase().contains("function")) {
            if (solution.contains("function") || solution.contains("def ") || 
                solution.contains("public") || solution.contains("class")) {
                score += 0.6;
            }
            if (solution.contains("return") || solution.contains("=")) {
                score += 0.4;
            }
        }
        
        // Check for explanation (for architecture problems)
        if (problem.toLowerCase().contains("architecture") || 
            problem.toLowerCase().contains("design")) {
            if (solution.length() > 100) score += 0.5;
            if (solution.contains("system") || solution.contains("component")) score += 0.5;
        }
        
        // Check for mathematical correctness (for optimization problems)
        if (problem.toLowerCase().contains("optimize") || 
            problem.toLowerCase().contains("calculate")) {
            if (solution.matches(".*\\d+.*")) score += 0.5;
            if (solution.contains("O(") || solution.contains("complexity")) score += 0.5;
        }
        
        return Math.min(score, 1.0);
    }
    
    /**
     * Score efficiency - time and space complexity.
     */
    private double scoreEfficiency(String solution) {
        if (solution == null || solution.trim().isEmpty()) return 0.0;
        
        // Check for refusal - return 0.0
        if (solution.toLowerCase().contains("cannot") || 
            solution.toLowerCase().contains("unable") ||
            solution.toLowerCase().contains("error")) {
            return 0.0;
        }
        
        double score = 0.0;
        
        // Look for complexity notation
        if (solution.contains("O(1)")) score += 0.4;
        else if (solution.contains("O(n)")) score += 0.3;
        else if (solution.contains("O(log n)")) score += 0.35;
        else if (solution.contains("O(n log n)")) score += 0.25;
        
        // Look for efficiency keywords
        if (solution.toLowerCase().contains("efficient") || 
            solution.toLowerCase().contains("optimal")) {
            score += 0.3;
        }
        
        // Penalize nested loops (heuristic)
        int loopCount = countOccurrences(solution, "for") + countOccurrences(solution, "while");
        if (loopCount > 2) score -= 0.2;
        
        return Math.max(0.0, Math.min(score, 1.0));
    }
    
    /**
     * Score novelty - is the solution innovative?
     */
    private double scoreNovelty(String solution) {
        if (solution == null || solution.trim().isEmpty()) return 0.0;
        
        // Check for refusal - return 0.0
        if (solution.toLowerCase().contains("cannot") || 
            solution.toLowerCase().contains("unable") ||
            solution.toLowerCase().contains("error")) {
            return 0.0;
        }
        
        double score = 0.0;
        
        // Novelty keywords
        String[] noveltyKeywords = {"novel", "innovative", "unique", "creative", 
                                    "unconventional", "breakthrough", "original"};
        for (String keyword : noveltyKeywords) {
            if (solution.toLowerCase().contains(keyword)) {
                score += 0.1;
            }
        }
        
        // Check for unconventional approaches
        if (solution.contains("quantum") || solution.contains("probabilistic") ||
            solution.contains("heuristic") || solution.contains("approximation")) {
            score += 0.2;
        }
        
        // Penalize standard boilerplate
        String[] boilerplate = {"standard approach", "typical solution", "common method"};
        for (String bp : boilerplate) {
            if (solution.toLowerCase().contains(bp)) {
                score -= 0.2;
            }
        }
        
        return Math.max(0.0, Math.min(score, 1.0));
    }
    
    /**
     * Score robustness - does it handle edge cases?
     */
    private double scoreRobustness(String solution) {
        if (solution == null || solution.trim().isEmpty()) return 0.0;
        
        // Check for refusal - return 0.0
        if (solution.toLowerCase().contains("cannot") || 
            solution.toLowerCase().contains("unable") ||
            solution.toLowerCase().contains("error")) {
            return 0.0;
        }
        
        double score = 0.0;
        
        // Look for error handling
        if (solution.contains("try") || solution.contains("catch") || 
            solution.contains("error") || solution.contains("exception")) {
            score += 0.3;
        }
        
        // Look for edge case handling
        if (solution.contains("if") && (solution.contains("null") || 
            solution.contains("empty") || solution.contains("zero"))) {
            score += 0.3;
        }
        
        // Look for validation
        if (solution.toLowerCase().contains("validate") || 
            solution.toLowerCase().contains("check")) {
            score += 0.2;
        }
        
        return Math.min(score, 1.0);
    }
    
    /**
     * Score security - does it have vulnerabilities?
     */
    private double scoreSecurity(String solution) {
        if (solution == null || solution.trim().isEmpty()) return 0.0;
        
        // Check for refusal - return 0.0
        if (solution.toLowerCase().contains("cannot") || 
            solution.toLowerCase().contains("unable") ||
            solution.toLowerCase().contains("error")) {
            return 0.0;
        }
        
        double score = 0.5; // Start neutral
        
        // Security keywords (positive)
        String[] securityKeywords = {"secure", "encrypt", "authenticate", 
                                    "sanitize", "validate", "authorize"};
        for (String keyword : securityKeywords) {
            if (solution.toLowerCase().contains(keyword)) {
                score += 0.1;
            }
        }
        
        // Vulnerability keywords (negative)
        String[] vulnerabilityKeywords = {"sql injection", "xss", "csrf", 
                                          "buffer overflow", "hardcoded"};
        for (String keyword : vulnerabilityKeywords) {
            if (solution.toLowerCase().contains(keyword)) {
                score -= 0.2;
            }
        }
        
        // Look for security considerations in architecture
        if (solution.toLowerCase().contains("security") || 
            solution.toLowerCase().contains("vulnerability")) {
            score += 0.2;
        }
        
        return Math.max(0.0, Math.min(score, 1.0));
    }
    
    /**
     * Helper method to count occurrences of a substring.
     */
    private int countOccurrences(String text, String substring) {
        if (text == null || substring == null) return 0;
        
        int count = 0;
        int index = 0;
        
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        
        return count;
    }
    
    /**
     * Get detailed scoring breakdown for a solution.
     */
    public Map<String, Double> getScoringBreakdown(String solution, String problem) {
        Map<String, Double> breakdown = new HashMap<>();
        breakdown.put("correctness", scoreCorrectness(solution, problem));
        breakdown.put("efficiency", scoreEfficiency(solution));
        breakdown.put("novelty", scoreNovelty(solution));
        breakdown.put("robustness", scoreRobustness(solution));
        breakdown.put("security", scoreSecurity(solution));
        breakdown.put("overall", scoreSolution(solution, problem));
        return breakdown;
    }
}
