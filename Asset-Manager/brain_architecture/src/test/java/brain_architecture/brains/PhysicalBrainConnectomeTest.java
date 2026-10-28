package brain_architecture.brains;

import brain_architecture.connectome.*;
import brain_architecture.core.*;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for PhysicalBrain connectome integration
 */
public class PhysicalBrainConnectomeTest {
    
    private PhysicalBrain physicalBrain;
    private BrainConfiguration config;
    
    @BeforeEach
    public void setUp() {
        Map<String, Object> configParams = new HashMap<>();
        configParams.put("backend_timeout", 5000);
        configParams.put("max_retries", 3);
        config = new BrainConfiguration(
            BrainType.PHYSICAL,
            1.0,  // evolutionCheckInterval
            0.3,  // degradationThreshold
            7,    // historyRetentionDays
            0.6,  // successRateWeight
            0.3,  // latencyWeight
            0.1,  // stabilityWeight
            configParams
        );
        physicalBrain = new PhysicalBrain(config);
    }
    
    @Test
    public void testConnectomeInitialization() {
        ConnectomeGraph connectome = physicalBrain.getConnectome();
        assertNotNull(connectome, "Connectome should be initialized");
        assertEquals(0, connectome.getAllNeurons().size(), "Connectome should start with no neurons");
    }
    
    @Test
    public void testGNNInitialization() {
        ConnectomeGNN gnn = physicalBrain.getGNN();
        assertNotNull(gnn, "GNN should be initialized");
        assertEquals(4, gnn.getNumHops(), "Default hop count should be 4");
    }
    
    @Test
    public void testNeuropilInitialization() {
        ConnectomeGraph connectome = physicalBrain.getConnectome();
        Collection<Neuropil> neuropils = connectome.getAllNeuropils();
        
        assertEquals(3, neuropils.size(), "Should have 3 neuropils");
        
        boolean hasMotorCortex = false;
        boolean hasSensoryCortex = false;
        boolean hasAssociationArea = false;
        
        for (Neuropil neuropil : neuropils) {
            if (neuropil.getName().equals("MOTOR_CORTEX")) {
                hasMotorCortex = true;
                assertEquals(Neuropil.NeuropilType.MOTOR, neuropil.getType());
            }
            if (neuropil.getName().equals("SENSORY_CORTEX")) {
                hasSensoryCortex = true;
                assertEquals(Neuropil.NeuropilType.SENSORY, neuropil.getType());
            }
            if (neuropil.getName().equals("ASSOCIATION_AREA")) {
                hasAssociationArea = true;
                assertEquals(Neuropil.NeuropilType.ASSOCIATION, neuropil.getType());
            }
        }
        
        assertTrue(hasMotorCortex, "Should have motor cortex");
        assertTrue(hasSensoryCortex, "Should have sensory cortex");
        assertTrue(hasAssociationArea, "Should have association area");
    }
    
    @Test
    public void testMapConceptToNeuron() {
        UUID conceptId1 = UUID.randomUUID();
        UUID conceptId2 = UUID.randomUUID();
        
        UUID neuronId1 = physicalBrain.mapConceptToNeuron(conceptId1, "concept1");
        UUID neuronId2 = physicalBrain.mapConceptToNeuron(conceptId2, "concept2");
        
        assertNotNull(neuronId1, "First neuron should be created");
        assertNotNull(neuronId2, "Second neuron should be created");
        assertNotEquals(neuronId1, neuronId2, "Neurons should be different");
        
        // Mapping same concept should return same neuron
        UUID neuronId1Again = physicalBrain.mapConceptToNeuron(conceptId1, "concept1");
        assertEquals(neuronId1, neuronId1Again, "Same concept should map to same neuron");
        
        // Verify neurons are in connectome
        ConnectomeGraph connectome = physicalBrain.getConnectome();
        assertEquals(2, connectome.getAllNeurons().size(), "Connectome should have 2 neurons");
    }
    
    @Test
    public void testStrengthenConnection() {
        UUID conceptId1 = UUID.randomUUID();
        UUID conceptId2 = UUID.randomUUID();
        
        physicalBrain.mapConceptToNeuron(conceptId1, "concept1");
        physicalBrain.mapConceptToNeuron(conceptId2, "concept2");
        
        // Strengthen connection
        physicalBrain.strengthenConnection(conceptId1, conceptId2);
        
        // Verify synapse was created
        ConnectomeGraph connectome = physicalBrain.getConnectome();
        UUID neuronId1 = physicalBrain.mapConceptToNeuron(conceptId1, "concept1");
        List<ConnectomeSynapse> synapses = connectome.getSynapses(neuronId1);
        
        assertEquals(1, synapses.size(), "Should have 1 synapse");
        assertTrue(synapses.get(0).getWeight() > 0.5, "Weight should be strengthened");
    }
    
    @Test
    public void testStrengthenConnectionMultipleTimes() {
        UUID conceptId1 = UUID.randomUUID();
        UUID conceptId2 = UUID.randomUUID();
        
        physicalBrain.mapConceptToNeuron(conceptId1, "concept1");
        physicalBrain.mapConceptToNeuron(conceptId2, "concept2");
        
        // Strengthen connection multiple times
        physicalBrain.strengthenConnection(conceptId1, conceptId2);
        physicalBrain.strengthenConnection(conceptId1, conceptId2);
        physicalBrain.strengthenConnection(conceptId1, conceptId2);
        
        // Verify weight increased
        ConnectomeGraph connectome = physicalBrain.getConnectome();
        UUID neuronId1 = physicalBrain.mapConceptToNeuron(conceptId1, "concept1");
        List<ConnectomeSynapse> synapses = connectome.getSynapses(neuronId1);
        
        assertTrue(synapses.get(0).getWeight() > 0.5, "Weight should be strengthened");
    }
    
    @Test
    public void testPropagateSignals() {
        UUID conceptId1 = UUID.randomUUID();
        UUID conceptId2 = UUID.randomUUID();
        UUID conceptId3 = UUID.randomUUID();
        
        physicalBrain.mapConceptToNeuron(conceptId1, "concept1");
        physicalBrain.mapConceptToNeuron(conceptId2, "concept2");
        physicalBrain.mapConceptToNeuron(conceptId3, "concept3");
        
        // Create connections
        physicalBrain.strengthenConnection(conceptId1, conceptId2);
        physicalBrain.strengthenConnection(conceptId2, conceptId3);
        
        // Propagate signals
        physicalBrain.propagateSignals();
        
        // Verify no errors occurred
        ConnectomeGraph connectome = physicalBrain.getConnectome();
        assertEquals(3, connectome.getAllNeurons().size(), "Should still have 3 neurons");
    }
    
    @Test
    public void testBackendRegistrationPreserved() {
        // Verify that backend registration still works
        physicalBrain.registerBackends();
        
        // Backend registration should complete without errors
        // (can't directly access backendEnsemble as it's protected)
        assertNotNull(physicalBrain.getConnectome(), "Connectome should still be accessible");
    }
    
    @Test
    public void testNeuronAssignmentToMotorCortex() {
        UUID conceptId = UUID.randomUUID();
        UUID neuronId = physicalBrain.mapConceptToNeuron(conceptId, "test");
        
        ConnectomeGraph connectome = physicalBrain.getConnectome();
        Neuron neuron = connectome.getNeuron(neuronId);
        
        assertNotNull(neuron.getNeuropil(), "Neuron should be assigned to a neuropil");
        assertEquals("MOTOR_CORTEX", neuron.getNeuropil().getName(), "Neuron should be in motor cortex");
    }
    
    @Test
    public void testIntrinsicDescriptorInitialization() {
        UUID conceptId = UUID.randomUUID();
        UUID neuronId = physicalBrain.mapConceptToNeuron(conceptId, "test");
        
        ConnectomeGraph connectome = physicalBrain.getConnectome();
        Neuron neuron = connectome.getNeuron(neuronId);
        
        assertNotNull(neuron.getIntrinsicDescriptor(), "Neuron should have intrinsic descriptor");
        assertEquals(Neuron.NeuronType.EXCITATORY, neuron.getType(), "Neuron should be excitatory");
    }
}
