package fraymus.causal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test suite for CausalModel
 * Phase 1.2: DAG-guided Do-Calculus Integration
 */
public class CausalModelTest {
    
    @Test
    public void testAddNode() {
        CausalModel model = new CausalModel();
        CausalModel.CausalNode node = model.addNode("A", "Variable A");
        
        assertNotNull(node);
        assertEquals("A", node.getId());
        assertEquals("Variable A", node.getVariable());
        assertEquals(1, model.getNodes().size());
    }
    
    @Test
    public void testAddEdge() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        
        model.addEdge("A", "B");
        
        assertTrue(model.getChildren("A").contains("B"));
        assertTrue(model.getParents("B").contains("A"));
    }
    
    @Test
    public void testCycleDetection() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        
        model.addEdge("A", "B");
        model.addEdge("B", "C");
        
        // Adding C -> A should create a cycle
        assertThrows(IllegalArgumentException.class, () -> {
            model.addEdge("C", "A");
        });
    }
    
    @Test
    public void testDoIntervention() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addEdge("A", "B");
        
        model.doIntervention("B", "value");
        
        CausalModel.CausalNode node = model.getNode("B");
        assertEquals("value", node.getValue());
        assertTrue(model.getInterventions().containsKey("B"));
        assertTrue(model.getParents("B").isEmpty()); // Parents broken
    }
    
    @Test
    public void testResetIntervention() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addEdge("A", "B");
        
        model.doIntervention("B", "value");
        model.resetIntervention("B");
        
        assertFalse(model.getInterventions().containsKey("B"));
    }
    
    @Test
    public void testCausalPath() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        
        model.addEdge("A", "B");
        model.addEdge("B", "C");
        
        List<String> path = model.getCausalPath("A", "C");
        
        assertEquals(3, path.size());
        assertEquals("A", path.get(0));
        assertEquals("B", path.get(1));
        assertEquals("C", path.get(2));
    }
    
    @Test
    public void testNoCausalPath() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        
        model.addEdge("A", "B");
        // C is disconnected
        
        List<String> path = model.getCausalPath("A", "C");
        assertTrue(path.isEmpty());
    }
    
    @Test
    public void testValidDAG() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addNode("C", "C");
        
        model.addEdge("A", "B");
        model.addEdge("B", "C");
        
        assertTrue(model.isValidDAG());
    }
    
    @Test
    public void testClear() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addEdge("A", "B");
        model.doIntervention("B", "value");
        
        model.clear();
        
        assertTrue(model.getNodes().isEmpty());
        assertTrue(model.getInterventions().isEmpty());
    }
    
    @Test
    public void testStatistics() {
        CausalModel model = new CausalModel();
        model.addNode("A", "A");
        model.addNode("B", "B");
        model.addEdge("A", "B");
        
        String stats = model.getStatistics();
        
        assertTrue(stats.contains("Nodes: 2"));
        assertTrue(stats.contains("Edges: 1"));
    }
}
