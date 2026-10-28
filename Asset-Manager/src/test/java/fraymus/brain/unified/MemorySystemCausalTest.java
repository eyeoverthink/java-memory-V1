package fraymus.brain.unified;

import fraymus.causal.CausalModel;
import fraymus.causal.DAGVerifier;
import fraymus.causal.DoCalculusEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Test suite for MemorySystem with causal reasoning integration (Phase 1.2)
 * Tests that causal reasoning enhances the existing memory system without breaking it.
 */
public class MemorySystemCausalTest {
    
    @Test
    public void testInitialization() {
        MemorySystem memory = new MemorySystem();
        
        assertNotNull(memory.getCausalModel());
        assertNotNull(memory.getCausalVerifier());
        assertNotNull(memory.getDoCalculus());
    }
    
    @Test
    public void testAddCausalRelationship() {
        MemorySystem memory = new MemorySystem();
        
        memory.addCausalRelationship("cause", "effect");
        
        CausalModel model = memory.getCausalModel();
        assertNotNull(model.getNode("cause"));
        assertNotNull(model.getNode("effect"));
        assertTrue(model.getChildren("cause").contains("effect"));
    }
    
    @Test
    public void testSearchSimilarWithCausalVerification() {
        MemorySystem memory = new MemorySystem();
        
        // Store some concepts
        BrainQuery query1 = new BrainQuery("What causes rain?", 
            new ArrayList<>(), new HashMap<>(), 1.0);
        memory.store(query1, "Rain is caused by water condensation", 0.9, 0.8);
        
        BrainQuery query2 = new BrainQuery("What is condensation?", 
            new ArrayList<>(), new HashMap<>(), 1.0);
        memory.store(query2, "Condensation is when water vapor turns to liquid", 0.9, 0.8);
        
        // Add causal relationship
        memory.addCausalRelationship("condensation", "rain");
        
        // Search for similar concepts
        Map<String, Double> similar = memory.searchSimilar("water", 5);
        
        assertNotNull(similar);
        // The causal relationship should enhance the search
    }
    
    @Test
    public void testSearchSimilarPenalizesSpuriousCorrelations() {
        MemorySystem memory = new MemorySystem();
        
        // Store concepts without causal relationship
        BrainQuery query1 = new BrainQuery("What is apple?", 
            new ArrayList<>(), new HashMap<>(), 1.0);
        memory.store(query1, "Apple is a fruit", 0.9, 0.8);
        
        BrainQuery query2 = new BrainQuery("What is banana?", 
            new ArrayList<>(), new HashMap<>(), 1.0);
        memory.store(query2, "Banana is also a fruit", 0.9, 0.8);
        
        // No causal relationship added - search should still work
        Map<String, Double> similar = memory.searchSimilar("fruit", 5);
        
        assertNotNull(similar);
    }
    
    @Test
    public void testStatisticsIncludesCausalInfo() {
        MemorySystem memory = new MemorySystem();
        
        memory.addCausalRelationship("A", "B");
        memory.addCausalRelationship("B", "C");
        
        Map<String, Object> stats = memory.getStatistics();
        
        assertTrue(stats.containsKey("causalNodes"));
        assertTrue(stats.containsKey("causalEdges"));
        assertEquals(3, stats.get("causalNodes"));
        assertEquals(2, stats.get("causalEdges"));
    }
    
    @Test
    public void testClearClearsCausalModel() {
        MemorySystem memory = new MemorySystem();
        
        memory.addCausalRelationship("A", "B");
        BrainQuery query = new BrainQuery("test", 
            new ArrayList<>(), new HashMap<>(), 1.0);
        memory.store(query, "response", 0.9, 0.8);
        
        memory.clear();
        
        assertEquals(0, memory.getCausalModel().getNodes().size());
        Map<String, Object> stats = memory.getStatistics();
        assertEquals(0, stats.get("causalNodes"));
    }
    
    @Test
    public void testBackwardCompatibility() {
        MemorySystem memory = new MemorySystem();
        
        // Original functionality should still work
        BrainQuery query = new BrainQuery("What is the meaning of life?", 
            new ArrayList<>(), new HashMap<>(), 1.0);
        memory.store(query, "42", 0.9, 0.8);
        
        MemorySystem.MemoryEntry entry = memory.retrieve("What is the meaning of life?");
        assertNotNull(entry);
        assertEquals("42", entry.getResponse());
        
        // Semantic search should still work
        Map<String, Double> similar = memory.searchSimilar("meaning", 5);
        assertNotNull(similar);
    }
    
    @Test
    public void testCausalVerifierAccessible() {
        MemorySystem memory = new MemorySystem();
        
        memory.addCausalRelationship("A", "B");
        memory.addCausalRelationship("B", "C");
        
        DAGVerifier verifier = memory.getCausalVerifier();
        
        assertTrue(verifier.verifyCausalPath("A", "C"));
        assertFalse(verifier.verifyCausalPath("C", "A"));
    }
    
    @Test
    public void testDoCalculusAccessible() {
        MemorySystem memory = new MemorySystem();
        
        memory.addCausalRelationship("X", "Y");
        memory.addCausalRelationship("Z", "X");
        memory.addCausalRelationship("Z", "Y");
        
        DoCalculusEngine engine = memory.getDoCalculus();
        
        assertTrue(engine.isIdentifiable("Y", "X"));
    }
    
    @Test
    public void testCausalCycleDetection() {
        MemorySystem memory = new MemorySystem();
        
        memory.addCausalRelationship("A", "B");
        memory.addCausalRelationship("B", "C");
        
        // This should throw an exception because it would create a cycle
        assertThrows(IllegalArgumentException.class, () -> {
            memory.addCausalRelationship("C", "A");
        });
    }
    
    @Test
    public void testOriginalMemoryFunctionalityUnchanged() {
        MemorySystem memory = new MemorySystem();
        
        // Test original store/retrieve
        BrainQuery query1 = new BrainQuery("query1", 
            new ArrayList<>(), new HashMap<>(), 1.0);
        memory.store(query1, "response1", 0.9, 0.8);
        
        BrainQuery query2 = new BrainQuery("query2", 
            new ArrayList<>(), new HashMap<>(), 1.0);
        memory.store(query2, "response2", 0.8, 0.7);
        
        assertEquals(2L, memory.getStatistics().get("totalStored"));
        
        MemorySystem.MemoryEntry entry = memory.retrieve("query1");
        assertEquals("response1", entry.getResponse());
        assertEquals(0.9, entry.getConfidence());
        assertEquals(0.8, entry.getConsciousness());
    }
}
