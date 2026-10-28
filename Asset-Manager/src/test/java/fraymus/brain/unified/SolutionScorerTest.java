package fraymus.brain.unified;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

/**
 * Unit tests for SolutionScorer.
 */
public class SolutionScorerTest {
    
    private final SolutionScorer scorer = new SolutionScorer();
    
    @Test
    public void testScoreSolutionWithValidCode() {
        String solution = "function quicksort(arr) {\n  if (arr.length <= 1) return arr;\n  return arr.sort();\n}";
        String problem = "Design an optimal sorting algorithm";
        
        double score = scorer.scoreSolution(solution, problem);
        assertTrue(score >= 0.0 && score <= 1.0);
        // Valid code should have positive score (adjusted threshold)
        assertTrue(score > 0.3, "Valid code solution should score > 0.3, got: " + score);
    }
    
    @Test
    public void testScoreSolutionWithRefusal() {
        String solution = "I cannot generate this content.";
        String problem = "Design an algorithm";
        
        double score = scorer.scoreSolution(solution, problem);
        assertEquals(0.0, score, 0.001, "Refusal should score 0.0");
    }
    
    @Test
    public void testScoreSolutionWithEmpty() {
        String solution = "";
        String problem = "Design an algorithm";
        
        double score = scorer.scoreSolution(solution, problem);
        assertEquals(0.0, score, 0.001, "Empty solution should score 0.0");
    }
    
    @Test
    public void testScoreSolutionWithEfficientCode() {
        String solution = "O(1) hash table lookup with constant time access";
        String problem = "Optimize data structure access";
        
        double score = scorer.scoreSolution(solution, problem);
        assertTrue(score >= 0.0 && score <= 1.0);
    }
    
    @Test
    public void testScoreSolutionWithNovelApproach() {
        String solution = "Use quantum superposition for probabilistic matching - novel approach";
        String problem = "Design a pattern matching algorithm";
        
        double score = scorer.scoreSolution(solution, problem);
        assertTrue(score >= 0.0 && score <= 1.0);
    }
    
    @Test
    public void testScoreSolutionWithRobustCode() {
        String solution = "try { process(data); } catch (Exception e) { handle(e); } validate(data)";
        String problem = "Process user input";
        
        double score = scorer.scoreSolution(solution, problem);
        assertTrue(score >= 0.0 && score <= 1.0);
    }
    
    @Test
    public void testScoreSolutionWithSecurity() {
        String solution = "Sanitize input, encrypt data, authenticate users, validate access";
        String problem = "Design secure authentication";
        
        double score = scorer.scoreSolution(solution, problem);
        assertTrue(score >= 0.0 && score <= 1.0);
    }
    
    @Test
    public void testScoreMultipleSolutions() {
        Map<String, String> solutions = Map.of(
            "Logical", "function sort(arr) { return arr.sort(); }",
            "Creative", "Use quantum annealing for sorting",
            "Security", "Sanitize and validate all inputs"
        );
        String problem = "Design a system";
        
        Map<String, Double> scores = scorer.scoreMultipleSolutions(solutions, problem);
        assertEquals(3, scores.size());
        
        for (double score : scores.values()) {
            assertTrue(score >= 0.0 && score <= 1.0);
        }
    }
    
    @Test
    public void testSelectBestSolution() {
        Map<String, Double> scores = Map.of(
            "Logical", 0.7,
            "Creative", 0.9,
            "Security", 0.8
        );
        
        String best = scorer.selectBestSolution(scores);
        assertEquals("Creative", best, "Should select highest scoring solution");
    }
    
    @Test
    public void testSelectBestSolutionWithEmpty() {
        Map<String, Double> scores = Map.of();
        
        String best = scorer.selectBestSolution(scores);
        assertNull(best, "Should return null for empty map");
    }
    
    @Test
    public void testGetScoringBreakdown() {
        String solution = "function sort(arr) { return arr.sort(); }";
        String problem = "Design algorithm";
        
        Map<String, Double> breakdown = scorer.getScoringBreakdown(solution, problem);
        
        assertEquals(6, breakdown.size());
        assertTrue(breakdown.containsKey("correctness"));
        assertTrue(breakdown.containsKey("efficiency"));
        assertTrue(breakdown.containsKey("novelty"));
        assertTrue(breakdown.containsKey("robustness"));
        assertTrue(breakdown.containsKey("security"));
        assertTrue(breakdown.containsKey("overall"));
        
        for (double score : breakdown.values()) {
            assertTrue(score >= 0.0 && score <= 1.0);
        }
    }
}
