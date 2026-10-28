package fraymus.causal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Test suite for DoCalculusEngine
 * Phase 1.2: DAG-guided Do-Calculus Integration
 */
public class DoCalculusEngineTest {
    
    @Test
    public void testApplyRule1() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("X", "Y");
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        DoCalculusEngine.CausalQueryResult result = engine.applyRule1("Y", "X", 
            new HashSet<>(Arrays.asList("Z")), new HashSet<>());
        
        assertNotNull(result);
        assertNotNull(result.getExplanation());
    }
    
    @Test
    public void testApplyRule2() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("X", "Y");
        model.addEdge("X", "Z");
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        DoCalculusEngine.CausalQueryResult result = engine.applyRule2("Y", "X", "Z", new HashSet<>());
        
        assertNotNull(result);
        assertNotNull(result.getExplanation());
    }
    
    @Test
    public void testApplyRule3() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("Z", "X");
        model.addEdge("X", "Y");
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        DoCalculusEngine.CausalQueryResult result = engine.applyRule3("Y", "X", "Z", new HashSet<>());
        
        assertNotNull(result);
        assertNotNull(result.getExplanation());
    }
    
    @Test
    public void testEstimateCausalEffect() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("Z", "X");
        model.addEdge("Z", "Y");
        model.addEdge("X", "Y");
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        DoCalculusEngine.CausalQueryResult result = engine.estimateCausalEffect("Y", "X", 
            new HashSet<>(Arrays.asList("Z")));
        
        assertNotNull(result);
        assertTrue(result.isIdentifiable());
    }
    
    @Test
    public void testFindMinimalAdjustmentSet() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("Z", "X");
        model.addEdge("Z", "Y");
        model.addEdge("X", "Y");
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        Set<String> adjustmentSet = engine.findMinimalAdjustmentSet("X", "Y");
        
        assertNotNull(adjustmentSet);
        assertTrue(adjustmentSet.contains("Z"));
    }
    
    @Test
    public void testCausalChainOfThought() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addEdge("X", "Y");
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        List<String> chain = engine.causalChainOfThought("What is the effect of X on Y?");
        
        assertNotNull(chain);
        assertFalse(chain.isEmpty());
        assertTrue(chain.get(0).contains("Causal Chain of Thought"));
    }
    
    @Test
    public void testIsIdentifiable() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("Z", "X");
        model.addEdge("Z", "Y");
        model.addEdge("X", "Y");
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        assertTrue(engine.isIdentifiable("Y", "X"));
    }
    
    @Test
    public void testCausalQueryResult() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addEdge("X", "Y");
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        DoCalculusEngine.CausalQueryResult result = engine.estimateCausalEffect("Y", "X", new HashSet<>());
        
        assertEquals("P(Y | do(X))", result.getQuery());
        assertNotNull(result.getMethod());
        assertNotNull(result.getExplanation());
    }
    
    @Test
    public void testRule1NotApplicable() {
        CausalModel model = new CausalModel();
        model.addNode("X", "X");
        model.addNode("Y", "Y");
        model.addNode("Z", "Z");
        
        model.addEdge("X", "Y");
        model.addEdge("Z", "Y"); // Z affects Y directly
        
        DoCalculusEngine engine = new DoCalculusEngine(model);
        
        DoCalculusEngine.CausalQueryResult result = engine.applyRule1("Y", "X", 
            new HashSet<>(Arrays.asList("Z")), new HashSet<>());
        
        // Should not be identifiable because Z is not d-separated
        assertFalse(result.isIdentifiable());
    }
}
