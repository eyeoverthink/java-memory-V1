package sandbox.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sandbox.core.BackendInterface;
import sandbox.core.ResultSynthesizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for ResultSynthesizer
 */
public class ResultSynthesizerTest {
    
    private final ResultSynthesizer synthesizer = new ResultSynthesizer();
    
    @Test
    public void testSynthesizeBestSingle() {
        BackendInterface.QueryResult result1 = new BackendInterface.QueryResult(
            "Response 1",
            0.7,
            100,
            true,
            null,
            Map.of("backend", "backend1")
        );
        
        BackendInterface.QueryResult result2 = new BackendInterface.QueryResult(
            "Response 2",
            0.9,
            150,
            true,
            null,
            Map.of("backend", "backend2")
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(result1, result2);
        
        ResultSynthesizer.SynthesizedResult synthesized = 
            synthesizer.synthesize(results, ResultSynthesizer.SynthesisMethod.BEST_SINGLE);
        
        assertTrue(synthesized.isSuccess());
        assertEquals("Response 2", synthesized.getContent()); // Higher confidence
        assertEquals(0.9, synthesized.getConfidence());
    }
    
    @Test
    public void testSynthesizeEmptyResults() {
        ResultSynthesizer.SynthesizedResult synthesized = 
            synthesizer.synthesize(Collections.emptyList(), ResultSynthesizer.SynthesisMethod.BEST_SINGLE);
        
        assertFalse(synthesized.isSuccess());
        assertEquals("No results to synthesize", synthesized.getMessage());
    }
    
    @Test
    public void testSynthesizeAllFailed() {
        BackendInterface.QueryResult failedResult = new BackendInterface.QueryResult(
            "",
            0.0,
            0,
            false,
            "Failed",
            Map.of()
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(failedResult, failedResult);
        
        ResultSynthesizer.SynthesizedResult synthesized = 
            synthesizer.synthesize(results, ResultSynthesizer.SynthesisMethod.BEST_SINGLE);
        
        assertFalse(synthesized.isSuccess());
        assertEquals("All backend results failed", synthesized.getMessage());
    }
    
    @Test
    public void testSynthesizeMajorityVote() {
        BackendInterface.QueryResult result1 = new BackendInterface.QueryResult(
            "Answer A",
            0.8,
            100,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult result2 = new BackendInterface.QueryResult(
            "Answer A",
            0.7,
            120,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult result3 = new BackendInterface.QueryResult(
            "Answer B",
            0.9,
            90,
            true,
            null,
            Map.of()
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(result1, result2, result3);
        
        ResultSynthesizer.SynthesizedResult synthesized = 
            synthesizer.synthesize(results, ResultSynthesizer.SynthesisMethod.MAJORITY_VOTE);
        
        assertTrue(synthesized.isSuccess());
        assertEquals("Answer A", synthesized.getContent()); // Majority
        assertTrue(synthesized.getConfidence() > 0.5); // Should have higher confidence due to agreement
    }
    
    @Test
    public void testSynthesizeWeightedAverage() {
        BackendInterface.QueryResult result1 = new BackendInterface.QueryResult(
            "Short response",
            0.5,
            50,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult result2 = new BackendInterface.QueryResult(
            "Much longer response with more details",
            0.9,
            150,
            true,
            null,
            Map.of()
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(result1, result2);
        
        ResultSynthesizer.SynthesizedResult synthesized = 
            synthesizer.synthesize(results, ResultSynthesizer.SynthesisMethod.WEIGHTED_AVERAGE);
        
        assertTrue(synthesized.isSuccess());
        assertTrue(synthesized.getContent().contains("Much longer")); // Higher confidence first
        assertTrue(synthesized.getContent().length() > result1.getContent().length());
    }
    
    @Test
    public void testSynthesizeConsensusWithHighAgreement() {
        BackendInterface.QueryResult result1 = new BackendInterface.QueryResult(
            "The answer is 42",
            0.8,
            100,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult result2 = new BackendInterface.QueryResult(
            "The answer is 42",
            0.85,
            110,
            true,
            null,
            Map.of()
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(result1, result2);
        
        ResultSynthesizer.SynthesizedResult synthesized = 
            synthesizer.synthesize(results, ResultSynthesizer.SynthesisMethod.CONSENSUS);
        
        assertTrue(synthesized.isSuccess());
        assertEquals("The answer is 42", synthesized.getContent());
        assertTrue(synthesized.getConfidence() > 0.8); // Boosted for consensus
    }
    
    @Test
    public void testSynthesizeConsensusWithLowAgreement() {
        BackendInterface.QueryResult result1 = new BackendInterface.QueryResult(
            "The answer is A",
            0.8,
            100,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult result2 = new BackendInterface.QueryResult(
            "The answer is completely different B",
            0.85,
            110,
            true,
            null,
            Map.of()
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(result1, result2);
        
        ResultSynthesizer.SynthesizedResult synthesized = 
            synthesizer.synthesize(results, ResultSynthesizer.SynthesisMethod.CONSENSUS);
        
        assertTrue(synthesized.isSuccess());
        assertTrue(synthesized.getConfidence() < 0.5); // Low confidence due to lack of consensus
    }
    
    @Test
    public void testSynthesizeEnsemble() {
        BackendInterface.QueryResult result1 = new BackendInterface.QueryResult(
            "Response",
            0.8,
            100,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult result2 = new BackendInterface.QueryResult(
            "Response",
            0.85,
            110,
            true,
            null,
            Map.of()
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(result1, result2);
        
        ResultSynthesizer.SynthesizedResult synthesized = 
            synthesizer.synthesize(results, ResultSynthesizer.SynthesisMethod.ENSEMBLE);
        
        assertTrue(synthesized.isSuccess());
        assertTrue(synthesized.getConfidence() > 0.8); // Should use consensus and boost confidence
    }
    
    @Test
    public void testCrossValidationPass() {
        BackendInterface.QueryResult result1 = new BackendInterface.QueryResult(
            "Response",
            0.9,
            100,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult result2 = new BackendInterface.QueryResult(
            "Response",
            0.85,
            110,
            true,
            null,
            Map.of()
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(result1, result2);
        
        ResultSynthesizer.CrossValidationReport report = synthesizer.crossValidate(results);
        
        assertTrue(report.isPassed());
        assertEquals(2, report.getTotalResults());
        assertEquals(2, report.getValidResults());
        assertEquals(1.0, report.getValidityRate());
    }
    
    @Test
    public void testCrossValidationFail() {
        BackendInterface.QueryResult result1 = new BackendInterface.QueryResult(
            "Response",
            0.9,
            100,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult result2 = new BackendInterface.QueryResult(
            "",
            0.0,
            0,
            false,
            "Failed",
            Map.of()
        );
        
        List<BackendInterface.QueryResult> results = Arrays.asList(result1, result2);
        
        ResultSynthesizer.CrossValidationReport report = synthesizer.crossValidate(results);
        
        assertFalse(report.isPassed());
        assertEquals(2, report.getTotalResults());
        assertEquals(1, report.getValidResults());
        assertEquals(0.5, report.getValidityRate());
    }
    
    @Test
    public void testSimilarityCalculation() {
        // Identical strings
        double similarity1 = synthesizer.calculateSimilarity("test", "test");
        assertEquals(1.0, similarity1);
        
        // Completely different strings
        double similarity2 = synthesizer.calculateSimilarity("abc", "xyz");
        assertTrue(similarity2 < 0.5);
        
        // Similar strings
        double similarity3 = synthesizer.calculateSimilarity("the quick brown fox", "the quick brown dog");
        assertTrue(similarity3 > 0.5);
    }
}
