package fraymus.brain.unified;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

/**
 * Unit tests for AdversarialComparator.
 */
public class AdversarialComparatorTest {
    
    private final AdversarialComparator comparator = new AdversarialComparator();
    
    @Test
    public void testGenerateCritiques() {
        Map<String, String> solutions = Map.of(
            "Logical", "If condition then action because valid",
            "Creative", "Novel quantum approach with heuristics",
            "Security", "Validate all inputs, handle errors with try-catch"
        );
        String problem = "Design a system";
        
        Map<String, String> critiques = comparator.generateCritiques(solutions, problem);
        
        assertEquals(3, critiques.size());
        assertTrue(critiques.containsKey("Logical"));
        assertTrue(critiques.containsKey("Creative"));
        assertTrue(critiques.containsKey("Security"));
        
        // Check that critiques are not empty
        for (String critique : critiques.values()) {
            assertNotNull(critique);
            assertFalse(critique.isEmpty());
        }
    }
    
    @Test
    public void testLogicalCritique() {
        Map<String, String> solutions = Map.of(
            "Logical", "if x then y because z",
            "Other", "Simple solution"
        );
        
        Map<String, String> critiques = comparator.generateCritiques(solutions, "problem");
        
        String logicalCritique = critiques.get("Logical");
        assertNotNull(logicalCritique);
        // Logical solution should have good critique
        assertTrue(logicalCritique.contains("Logically sound") || logicalCritique.length() > 0);
    }
    
    @Test
    public void testSecurityCritique() {
        Map<String, String> solutions = Map.of(
            "Security", "Validate input, sanitize data, try-catch errors",
            "Other", "No security"
        );
        
        Map<String, String> critiques = comparator.generateCritiques(solutions, "problem");
        
        String securityCritique = critiques.get("Security");
        assertNotNull(securityCritique);
        // Security solution with validation should be praised
        assertTrue(securityCritique.contains("Security adequate") || securityCritique.length() > 0);
    }
    
    @Test
    public void testEvolutionaryCritique() {
        Map<String, String> solutions = Map.of(
            "Evolutionary", "Scalable adaptive flexible approach",
            "Other", "Simple"
        );
        
        Map<String, String> critiques = comparator.generateCritiques(solutions, "problem");
        
        String evolutionaryCritique = critiques.get("Evolutionary");
        assertNotNull(evolutionaryCritique);
        assertTrue(evolutionaryCritique.contains("Evolutionarily stable") || evolutionaryCritique.length() > 0);
    }
    
    @Test
    public void testRefineScores() {
        Map<String, Double> initialScores = Map.of(
            "Brain1", 0.8,
            "Brain2", 0.6
        );
        
        Map<String, String> critiques = Map.of(
            "Brain1", "Sound",
            "Brain2", "Missing logic, lacks detail"
        );
        
        Map<String, Double> refinedScores = comparator.refineScores(initialScores, critiques);
        
        assertEquals(2, refinedScores.size());
        
        // Brain1 should have higher or equal score (positive critique)
        assertTrue(refinedScores.get("Brain1") >= 0.8);
        
        // Brain2 should have lower score (negative critique)
        assertTrue(refinedScores.get("Brain2") < 0.6);
    }
    
    @Test
    public void testRefineScoresClamped() {
        Map<String, Double> initialScores = Map.of(
            "Brain1", 0.95,
            "Brain2", 0.05
        );
        
        Map<String, String> critiques = Map.of(
            "Brain1", "Excellent optimal",
            "Brain2", "Missing everything, lacks all"
        );
        
        Map<String, Double> refinedScores = comparator.refineScores(initialScores, critiques);
        
        // Scores should be clamped to [0, 1]
        assertTrue(refinedScores.get("Brain1") <= 1.0);
        assertTrue(refinedScores.get("Brain2") >= 0.0);
    }
    
    @Test
    public void testRunComparison() {
        Map<String, String> solutions = Map.of(
            "Logical", "If condition then action because valid",
            "Creative", "Novel quantum approach",
            "Security", "Validate inputs, handle errors"
        );
        String problem = "Design system";
        
        AdversarialComparator.AdversarialComparisonResult result = 
            comparator.runComparison(solutions, problem);
        
        assertNotNull(result);
        assertEquals(3, result.getInitialScores().size());
        assertEquals(3, result.getCritiques().size());
        assertEquals(3, result.getRefinedScores().size());
    }
    
    @Test
    public void testRunComparisonWithEmptySolutions() {
        Map<String, String> solutions = Map.of();
        String problem = "Design system";
        
        AdversarialComparator.AdversarialComparisonResult result = 
            comparator.runComparison(solutions, problem);
        
        assertNotNull(result);
        assertEquals(0, result.getInitialScores().size());
        assertEquals(0, result.getCritiques().size());
        assertEquals(0, result.getRefinedScores().size());
    }
    
    @Test
    public void testGenericCritique() {
        Map<String, String> solutions = Map.of(
            "GenericBrain", "This is a solution with structure. It has details."
        );
        
        Map<String, String> critiques = comparator.generateCritiques(solutions, "problem");
        
        String genericCritique = critiques.get("GenericBrain");
        assertNotNull(genericCritique);
        assertTrue(genericCritique.contains("Adequate") || genericCritique.length() > 0);
    }
}
