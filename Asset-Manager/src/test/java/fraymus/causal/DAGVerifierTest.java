package fraymus.causal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Test suite for DAGVerifier
 * Phase 1.2: DAG-guided Do-Calculus Integration
 */
public class DAGVerifierTest {
    
    @Test
    public void testVerifyCausalPath() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        model.addEdge("A", "B");
        model.addEdge("B", "C");
        
        DAGVerifier verifier = new DAGVerifier(model);
        
        assertTrue(verifier.verifyCausalPath("A", "C"));
        assertFalse(verifier.verifyCausalPath("C", "A"));
    }
    
    @Test
    public void testFindBackdoorPaths() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        model.addNode("Z", "Z");
        
        model.addEdge("Z", "A");
        model.addEdge("Z", "B");
        model.addEdge("A", "B");
        
        DAGVerifier verifier = new DAGVerifier(model);
        
        List<List<String>> backdoorPaths = verifier.findBackdoorPaths("A", "B");
        
        // Should find path A <- Z -> B as backdoor
        assertFalse(backdoorPaths.isEmpty());
    }
    
    @Test
    public void testDSeparation() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("X", "Z");
        model.addEdge("Z", "Y");
        
        DAGVerifier verifier = new DAGVerifier(model);
        
        // X and Y should be d-separated given Z
        assertTrue(verifier.isDSeparated("X", "Y", new HashSet<>(Arrays.asList("Z"))));
        
        // X and Y should NOT be d-separated without Z
        assertFalse(verifier.isDSeparated("X", "Y", new HashSet<>()));
    }
    
    @Test
    public void testDetectSpuriousCorrelations() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        
        // A and B both caused by C (spurious correlation between A and B)
        model.addEdge("C", "A");
        model.addEdge("C", "B");
        
        DAGVerifier verifier = new DAGVerifier(model);
        
        List<DAGVerifier.CausalViolation> violations = verifier.detectSpuriousCorrelations();
        
        assertFalse(violations.isEmpty());
        assertEquals("SPURIOUS_CORRELATION", violations.get(0).getType());
    }
    
    @Test
    public void testVerifyBackdoorCriterion() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("Z", "X");
        model.addEdge("Z", "Y");
        model.addEdge("X", "Y");
        
        DAGVerifier verifier = new DAGVerifier(model);
        
        // Backdoor criterion should be satisfied by adjusting for Z
        assertTrue(verifier.verifyBackdoorCriterion("X", "Y", new HashSet<>(Arrays.asList("Z"))));
        
        // Not satisfied without adjustment
        assertFalse(verifier.verifyBackdoorCriterion("X", "Y", new HashSet<>()));
    }
    
    @Test
    public void testComputeViolationEnergy() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        
        model.addEdge("C", "A");
        model.addEdge("C", "B");
        
        DAGVerifier verifier = new DAGVerifier(model);
        
        double energy = verifier.computeViolationEnergy(1.0);
        
        // Should have positive energy due to spurious correlation
        assertTrue(energy > 0);
    }
    
    @Test
    public void testGetAllViolations() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        
        model.addEdge("C", "A");
        model.addEdge("C", "B");
        
        DAGVerifier verifier = new DAGVerifier(model);
        
        List<DAGVerifier.CausalViolation> violations = verifier.getAllViolations();
        
        assertFalse(violations.isEmpty());
    }
    
    @Test
    public void testCausalViolationStructure() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addEdge("A", "B");
        
        DAGVerifier verifier = new DAGVerifier(model);
        
        // Create a manual violation for testing
        DAGVerifier.CausalViolation violation = new DAGVerifier.CausalViolation(
            "TEST",
            "Test violation",
            Arrays.asList("A", "B"),
            0.5
        );
        
        assertEquals("TEST", violation.getType());
        assertEquals("Test violation", violation.getDescription());
        assertEquals(2, violation.getInvolvedNodes().size());
        assertEquals(0.5, violation.getSeverity());
    }
}
