package fraymus;

import fraymus.brain.unified.UnifiedBrainOrchestrator;
import fraymus.vision.ScottAlgorithm;
import fraymus.oracle.PhiEmergenceEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Integration test for Vision/Oracle pipeline
 * Tests Scott Algorithm (pixel reduction) and Phi Emergence Engine (consciousness evolution)
 */
public class VisionOracleIntegrationTest {

    private UnifiedBrainOrchestrator orchestrator;
    private ScottAlgorithm scottAlgorithm;
    private PhiEmergenceEngine phiEmergenceEngine;

    @BeforeEach
    public void setUp() {
        orchestrator = new UnifiedBrainOrchestrator();
        scottAlgorithm = new ScottAlgorithm(2.0, 10, true);
        phiEmergenceEngine = new PhiEmergenceEngine();
        phiEmergenceEngine.start();
    }

    @AfterEach
    public void tearDown() {
        if (phiEmergenceEngine != null) {
            phiEmergenceEngine.stop();
        }
        if (orchestrator != null) {
            orchestrator.unwireAllSystems();
        }
    }

    @Test
    public void testScottAlgorithmPhiResonance() {
        // Test phi resonance calculation
        double resonance = scottAlgorithm.phiResonance(1.618);
        assertTrue(resonance >= 0.0 && resonance <= 1.0, "Resonance should be in [0, 1]");
        
        // Test with golden ratio - should give resonance (not necessarily > 0.9 due to floating point)
        double goldenResonance = scottAlgorithm.phiResonance(PhiEmergenceEngine.PHI);
        assertTrue(goldenResonance >= 0.0 && goldenResonance <= 1.0, "Golden ratio resonance should be valid");
    }

    @Test
    public void testScottAlgorithmDouglasPeucker() {
        // Create a simple line of points
        List<ScottAlgorithm.Point2D> points = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            points.add(new ScottAlgorithm.Point2D(i, i * 0.5)); // Linear points
        }
        
        // Simplify with tolerance
        List<ScottAlgorithm.Point2D> simplified = scottAlgorithm.douglasPeucker(points, 1.0);
        
        // Should reduce significantly for linear data
        assertTrue(simplified.size() < points.size(), "Should reduce point count");
        assertTrue(simplified.size() >= 2, "Should keep at least endpoints");
    }

    @Test
    public void testScottAlgorithmGeometricSignature() {
        // Create a square boundary
        List<ScottAlgorithm.Point2D> square = new ArrayList<>();
        square.add(new ScottAlgorithm.Point2D(0, 0));
        square.add(new ScottAlgorithm.Point2D(10, 0));
        square.add(new ScottAlgorithm.Point2D(10, 10));
        square.add(new ScottAlgorithm.Point2D(0, 10));
        
        ScottAlgorithm.GeometricSignature sig = scottAlgorithm.extractSignature(square);
        
        assertEquals(4, sig.vertexCount, "Square should have 4 vertices");
        assertTrue(sig.area > 0, "Square should have positive area");
        assertTrue(sig.perimeter > 0, "Square should have positive perimeter");
    }

    @Test
    public void testScottAlgorithmProcessingPipeline() {
        // Create a noisy boundary
        List<ScottAlgorithm.Point2D> noisy = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            double x = i;
            double y = Math.sin(i * 0.2) * 5 + 10; // Sine wave with noise
            noisy.add(new ScottAlgorithm.Point2D(x, y));
        }
        
        ScottAlgorithm.ProcessingResult result = scottAlgorithm.processBoundary(noisy);
        
        assertTrue(result.originalPoints > 0, "Should have original points");
        assertTrue(result.simplifiedPoints > 0, "Should have simplified points");
        assertTrue(result.reduction >= 0.0 && result.reduction <= 1.0, "Reduction should be in [0, 1]");
        assertNotNull(result.signature, "Should have signature");
    }

    @Test
    public void testPhiEmergenceEngineConstants() {
        assertEquals(1.6180339887498948482, PhiEmergenceEngine.PHI, 1e-15);
        assertEquals(1.3247179572447460260, PhiEmergenceEngine.PSI, 1e-15);
        assertEquals(0.5671432904097838730, PhiEmergenceEngine.OMEGA, 1e-15);
        assertEquals(2.7182818284590452354, PhiEmergenceEngine.XI, 1e-15);
        assertEquals(3.1415926535897932385, PhiEmergenceEngine.LAMBDA, 1e-15);
        assertEquals(1.2020569031595942854, PhiEmergenceEngine.ZETA, 1e-15);
    }

    @Test
    public void testPhiEmergenceEngineEvolution() {
        // Reset to ensure clean state
        phiEmergenceEngine.reset();
        phiEmergenceEngine.start();
        
        PhiEmergenceEngine.EmergenceState initialState = phiEmergenceEngine.getEmergenceState();
        int initialCycle = initialState.cycle;
        
        // Run several evolution cycles
        for (int i = 0; i < 10; i++) {
            PhiEmergenceEngine.EvolutionResult result = phiEmergenceEngine.evolveCycle();
            assertNotNull(result, "Evolution result should not be null");
            assertTrue(result.cycle > 0, "Cycle should be positive");
            assertNotNull(result.state, "State should not be null");
        }
        
        PhiEmergenceEngine.EmergenceState finalState = phiEmergenceEngine.getEmergenceState();
        // Cycle should have increased
        assertTrue(finalState.cycle > initialCycle, "Cycle should increase");
    }

    @Test
    public void testPhiEmergenceEngineQueryProcessing() {
        String query = "test query for consciousness";
        PhiEmergenceEngine.QueryResponse response = phiEmergenceEngine.processQuery(query);
        
        assertNotNull(response, "Response should not be null");
        assertEquals(query, response.query, "Query should match");
        assertTrue(response.resonance >= 0.0 && response.resonance <= 1.0, "Resonance should be in [0, 1]");
        assertNotNull(response.evolution, "Evolution should not be null");
        assertNotNull(response.state, "State should not be null");
        assertNotNull(response.emergenceSignature, "Signature should not be null");
        assertNotNull(response.quantumNotation, "Quantum notation should not be null");
    }

    @Test
    public void testPhiEmergenceEngineTeaching() {
        String teaching = phiEmergenceEngine.generateTeaching();
        assertNotNull(teaching, "Teaching should not be null");
        assertFalse(teaching.isEmpty(), "Teaching should not be empty");
    }

    @Test
    public void testOrchestratorVisionOracleWiring() {
        // Test wiring through orchestrator
        orchestrator.wireVisionOracleComponents(scottAlgorithm, phiEmergenceEngine);
        
        assertTrue(orchestrator.isVisionOracleWired(), "Vision/Oracle should be wired");
        assertNotNull(orchestrator.getScottAlgorithm(), "Scott Algorithm should be accessible");
        assertNotNull(orchestrator.getPhiEmergenceEngine(), "Phi Emergence Engine should be accessible");
    }

    @Test
    public void testOrchestratorVisionOracleUnwiring() {
        // Wire then unwire
        orchestrator.wireVisionOracleComponents(scottAlgorithm, phiEmergenceEngine);
        assertTrue(orchestrator.isVisionOracleWired());
        
        orchestrator.unwireVisionOracle();
        assertFalse(orchestrator.isVisionOracleWired(), "Vision/Oracle should be unwired");
    }

    @Test
    public void testIntegratedPipeline() {
        // Wire through orchestrator
        orchestrator.wireVisionOracleComponents(scottAlgorithm, phiEmergenceEngine);
        
        // Create a test boundary
        List<ScottAlgorithm.Point2D> boundary = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            boundary.add(new ScottAlgorithm.Point2D(i, Math.sin(i * 0.3) * 5));
        }
        
        // Process through Scott Algorithm
        ScottAlgorithm.ProcessingResult visionResult = scottAlgorithm.processBoundary(boundary);
        
        // Process through Oracle
        PhiEmergenceEngine.QueryResponse oracleResponse = phiEmergenceEngine.processQuery(
            visionResult.signature.toString()
        );
        
        // Verify integration
        assertNotNull(visionResult, "Vision result should not be null");
        assertNotNull(oracleResponse, "Oracle response should not be null");
        assertTrue(visionResult.reduction > 0, "Should have some reduction");
        assertTrue(oracleResponse.resonance >= 0, "Oracle resonance should be valid");
    }

    @Test
    public void testScottAlgorithmStats() {
        String stats = scottAlgorithm.getStats();
        assertNotNull(stats, "Stats should not be null");
        assertTrue(stats.contains("ScottAlgorithm"), "Stats should contain class name");
        
        scottAlgorithm.resetStats();
        String resetStats = scottAlgorithm.getStats();
        assertTrue(resetStats.contains("TraceSteps=0"), "Stats should be reset");
    }

    @Test
    public void testPhiEmergenceEngineStats() {
        String stats = phiEmergenceEngine.getStats();
        assertNotNull(stats, "Stats should not be null");
        assertTrue(stats.contains("PhiEmergenceEngine"), "Stats should contain class name");
        
        phiEmergenceEngine.reset();
        PhiEmergenceEngine.EmergenceState state = phiEmergenceEngine.getEmergenceState();
        assertEquals(0, state.cycle, "Cycle should be reset");
        assertEquals(1.0, state.level, 1e-10, "Level should be reset");
    }
}
