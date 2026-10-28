package sandbox.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sandbox.core.BackendInterface;
import sandbox.core.QueryAnalyzer;

/**
 * Unit tests for QueryAnalyzer
 */
public class QueryAnalyzerTest {
    
    private final QueryAnalyzer analyzer = new QueryAnalyzer();
    
    @Test
    public void testReasoningTaskClassification() {
        QueryAnalyzer.AnalysisResult result = analyzer.analyze("Why is the sky blue?");
        
        assertEquals(BackendInterface.TaskType.REASONING, result.getTaskType());
        assertTrue(result.getKeywords().contains("why"));
        assertTrue(result.getComplexityScore() > 0.0);
    }
    
    @Test
    public void testGenerationTaskClassification() {
        QueryAnalyzer.AnalysisResult result = analyzer.analyze("Create a poem about nature");
        
        assertEquals(BackendInterface.TaskType.GENERATION, result.getTaskType());
        assertTrue(result.getKeywords().contains("create"));
    }
    
    @Test
    public void testKnowledgeTaskClassification() {
        QueryAnalyzer.AnalysisResult result = analyzer.analyze("What is the capital of France?");
        
        assertEquals(BackendInterface.TaskType.KNOWLEDGE, result.getTaskType());
        assertTrue(result.getKeywords().contains("what"));
    }
    
    @Test
    public void testFastInferenceTaskClassification() {
        QueryAnalyzer.AnalysisResult result = analyzer.analyze("Quick answer: what is 2+2?");
        
        assertEquals(BackendInterface.TaskType.FAST_INFERENCE, result.getTaskType());
        assertTrue(result.getKeywords().contains("quick"));
    }
    
    @Test
    public void testComplexAnalysisTaskClassification() {
        QueryAnalyzer.AnalysisResult result = analyzer.analyze("Explain and analyze the comprehensive deep economic factors");
        
        assertEquals(BackendInterface.TaskType.COMPLEX_ANALYSIS, result.getTaskType());
        assertTrue(result.getKeywords().contains("comprehensive") || result.getKeywords().contains("deep"));
    }
    
    @Test
    public void testEmptyQuery() {
        QueryAnalyzer.AnalysisResult result = analyzer.analyze("");
        
        assertEquals(BackendInterface.TaskType.REASONING, result.getTaskType()); // Default
        assertEquals(0.0, result.getComplexityScore());
    }
    
    @Test
    public void testNullQuery() {
        QueryAnalyzer.AnalysisResult result = analyzer.analyze(null);
        
        assertEquals(BackendInterface.TaskType.REASONING, result.getTaskType()); // Default
        assertEquals(0.0, result.getComplexityScore());
    }
    
    @Test
    public void testComplexityScoreCalculation() {
        QueryAnalyzer.AnalysisResult simple = analyzer.analyze("Hello");
        QueryAnalyzer.AnalysisResult complex = analyzer.analyze("Provide a comprehensive deep analysis of the complex economic factors affecting the global market, including multiple interconnected variables and nuanced considerations");
        
        assertTrue(complex.getComplexityScore() > simple.getComplexityScore());
    }
    
    @Test
    public void testLatencyRequirementCalculation() {
        QueryAnalyzer.AnalysisResult fast = analyzer.analyze("Quick answer now");
        QueryAnalyzer.AnalysisResult normal = analyzer.analyze("Tell me about history");
        
        assertTrue(fast.getLatencyRequirement() > normal.getLatencyRequirement());
    }
    
    @Test
    public void testAccuracyRequirementCalculation() {
        QueryAnalyzer.AnalysisResult knowledge = analyzer.analyze("What is the capital of France?");
        QueryAnalyzer.AnalysisResult fast = analyzer.analyze("Quick guess what 2+2 is");
        
        assertTrue(knowledge.getAccuracyRequirement() > fast.getAccuracyRequirement());
    }
    
    @Test
    public void testFeatureExtraction() {
        QueryAnalyzer.AnalysisResult result = analyzer.analyze("What is the answer to 42?");
        
        assertTrue(result.getFeatures().containsKey("length"));
        assertTrue(result.getFeatures().containsKey("wordCount"));
        assertTrue(result.getFeatures().containsKey("hasQuestionMark"));
        assertTrue((Boolean) result.getFeatures().get("hasQuestionMark"));
        assertTrue((Boolean) result.getFeatures().get("hasNumbers"));
    }
}
