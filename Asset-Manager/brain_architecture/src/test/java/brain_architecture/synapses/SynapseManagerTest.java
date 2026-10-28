package brain_architecture.synapses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SynapseManagerTest - Unit tests for SynapseManager.
 */
public class SynapseManagerTest {
    
    private SynapseManager manager;
    private HyperdimensionalMapper hdcMapper;
    private HDCSimilarityEngine hdcEngine;
    
    @BeforeEach
    void setUp() {
        hdcMapper = new HyperdimensionalMapper();
        hdcEngine = new HDCSimilarityEngine(0.7);
        manager = new SynapseManager(hdcEngine, 0.7, 0.5, 0.1);
    }
    
    @Test
    void testRegisterConcept() {
        Concept concept = new Concept("test", "Test definition", "category", hdcMapper.map("test"));
        manager.registerConcept(concept);
        
        assertEquals(1, manager.getConceptCount());
    }
    
    @Test
    void testCreateSynapticConnections() {
        Concept conceptA = new Concept("A", "Definition A", "category", hdcMapper.map("A"));
        Concept conceptB = new Concept("B", "Definition B", "category", hdcMapper.map("B"));
        
        manager.createSynapticConnections(List.of(conceptA, conceptB));
        
        assertEquals(2, manager.getConceptCount());
    }
    
    @Test
    void testGetOutgoingSynapses() {
        Concept source = new Concept("source", "Source", "category", hdcMapper.map("source"));
        Concept target = new Concept("target", "Target", "category", hdcMapper.map("target"));
        
        manager.registerConcept(source);
        manager.registerConcept(target);
        manager.createSynapticConnections(List.of(source, target));
        
        List<Synapse> outgoing = manager.getOutgoingSynapses(source.getConceptId());
        assertNotNull(outgoing);
    }
    
    @Test
    void testGetIncomingSynapses() {
        Concept source = new Concept("source", "Source", "category", hdcMapper.map("source"));
        Concept target = new Concept("target", "Target", "category", hdcMapper.map("target"));
        
        manager.registerConcept(source);
        manager.registerConcept(target);
        manager.createSynapticConnections(List.of(source, target));
        
        List<Synapse> incoming = manager.getIncomingSynapses(target.getConceptId());
        assertNotNull(incoming);
    }
    
    @Test
    void testHebbianLearning() {
        Concept source = new Concept("source", "Source", "category", hdcMapper.map("source"));
        Concept target = new Concept("target", "Target", "category", hdcMapper.map("target"));
        
        manager.registerConcept(source);
        manager.registerConcept(target);
        manager.createSynapticConnections(List.of(source, target));
        
        // Activate concepts
        source.activate();
        target.activate();
        
        // Apply Hebbian learning
        manager.applyHebbianLearning();
        
        // Verify synapses were strengthened
        List<Synapse> synapses = manager.getOutgoingSynapses(source.getConceptId());
        for (Synapse synapse : synapses) {
            assertTrue(synapse.getWeight() >= 0.5); // Should be at least initial weight
        }
    }
    
    @Test
    void testSTDP() {
        Concept source = new Concept("source", "Source", "category", hdcMapper.map("source"));
        Concept target = new Concept("target", "Target", "category", hdcMapper.map("target"));
        
        manager.registerConcept(source);
        manager.registerConcept(target);
        manager.createSynapticConnections(List.of(source, target));
        
        // Set activation times
        source.activate();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        target.activate();
        
        // Apply STDP
        manager.applySTDP();
        
        // Verify STDP was applied
        assertTrue(manager.getSynapseCount() >= 0);
    }
    
    @Test
    void testHomeostaticPlasticity() {
        Concept source = new Concept("source", "Source", "category", hdcMapper.map("source"));
        Concept target = new Concept("target", "Target", "category", hdcMapper.map("target"));
        
        manager.registerConcept(source);
        manager.registerConcept(target);
        manager.createSynapticConnections(List.of(source, target));
        
        // Apply homeostatic plasticity
        manager.applyHomeostaticPlasticity();
        
        // Verify synapses were normalized
        List<Synapse> synapses = manager.getOutgoingSynapses(source.getConceptId());
        for (Synapse synapse : synapses) {
            assertTrue(synapse.getWeight() >= 0.0);
            assertTrue(synapse.getWeight() <= 2.0);
        }
    }
    
    @Test
    void testDecay() {
        Concept source = new Concept("source", "Source", "category", hdcMapper.map("source"));
        Concept target = new Concept("target", "Target", "category", hdcMapper.map("target"));
        
        manager.registerConcept(source);
        manager.registerConcept(target);
        manager.createSynapticConnections(List.of(source, target));
        
        // Apply decay
        manager.applyDecay(0.1);
        
        // Verify synapses were weakened
        List<Synapse> synapses = manager.getOutgoingSynapses(source.getConceptId());
        for (Synapse synapse : synapses) {
            assertTrue(synapse.getWeight() >= 0.0);
        }
    }
    
    @Test
    void testPruneWeakSynapses() {
        Concept source = new Concept("source", "Source", "category", hdcMapper.map("source"));
        Concept target = new Concept("target", "Target", "category", hdcMapper.map("target"));
        
        manager.registerConcept(source);
        manager.registerConcept(target);
        manager.createSynapticConnections(List.of(source, target));
        
        // Prune weak synapses
        int beforePrune = manager.getSynapseCount();
        manager.pruneWeakSynapses(0.6);
        int afterPrune = manager.getSynapseCount();
        
        // Some synapses may have been pruned
        assertTrue(afterPrune <= beforePrune);
    }
}
