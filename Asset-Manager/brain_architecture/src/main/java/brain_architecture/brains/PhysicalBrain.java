package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;

/**
 * Physical Brain - Motor cortex, sensory processing, coordination
 * Backends: openclaw, neoClaw, obsidian
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class PhysicalBrain extends QuantumBrain {
    
    // Internal connectome for neural representation
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    
    // Mapping from concept IDs to neuron IDs
    private final Map<UUID, UUID> conceptToNeuronMap;
    
    // Neuropil IDs for quick access
    private UUID motorCortexId;
    private UUID sensoryCortexId;
    private UUID associationAreaId;

    public PhysicalBrain(BrainConfiguration config) {
        super(BrainType.PHYSICAL, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        
        // Initialize neuropils for physical brain regions
        initializeNeuropils();
    }
    
    /**
     * Initialize neuropils for physical brain regions
     */
    private void initializeNeuropils() {
        // Motor cortex
        Neuropil motorCortex = new Neuropil("MOTOR_CORTEX", "Motor cortex - movement control", 
            Neuropil.NeuropilType.MOTOR);
        connectome.addNeuropil(motorCortex);
        motorCortexId = motorCortex.getNeuropilId();
        
        // Sensory cortex
        Neuropil sensoryCortex = new Neuropil("SENSORY_CORTEX", "Sensory cortex - sensory processing", 
            Neuropil.NeuropilType.SENSORY);
        connectome.addNeuropil(sensoryCortex);
        sensoryCortexId = sensoryCortex.getNeuropilId();
        
        // Association area
        Neuropil associationArea = new Neuropil("ASSOCIATION_AREA", "Association area - coordination", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(associationArea);
        associationAreaId = associationArea.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        OpenClawBackend openclaw = new OpenClawBackend();
        NeoClawBackend neoClaw = new NeoClawBackend();
        ObsidianBackend obsidian = new ObsidianBackend();

        try {
            openclaw.initialize();
            neoClaw.initialize();
            obsidian.initialize();

            backendEnsemble.registerBackend(openclaw);
            backendEnsemble.registerBackend(neoClaw);
            backendEnsemble.registerBackend(obsidian);
        } catch (Exception e) {
            System.err.println("Failed to initialize Physical Brain backends: " + e.getMessage());
        }
    }
    
    /**
     * Get the internal connectome graph
     */
    public ConnectomeGraph getConnectome() {
        return connectome;
    }
    
    /**
     * Get the GNN for message passing
     */
    public ConnectomeGNN getGNN() {
        return gnn;
    }
    
    /**
     * Map a concept to a neuron in the connectome
     */
    public UUID mapConceptToNeuron(UUID conceptId, String conceptName) {
        if (conceptToNeuronMap.containsKey(conceptId)) {
            return conceptToNeuronMap.get(conceptId);
        }
        
        // Create a new neuron for this concept
        IntrinsicDescriptor intrinsic = new IntrinsicDescriptor(0.5, 1.0, 0.1, 0.0);
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setIntrinsicDescriptor(intrinsic);
        
        // Assign to motor cortex for physical brain
        Neuropil motorCortex = connectome.getNeuropil(motorCortexId);
        if (motorCortex != null) {
            neuron.setNeuropil(motorCortex);
        }
        
        connectome.addNeuron(neuron);
        conceptToNeuronMap.put(conceptId, neuron.getNeuronId());
        
        return neuron.getNeuronId();
    }
    
    /**
     * Apply synaptic plasticity based on concept co-activation
     */
    public void strengthenConnection(UUID conceptId1, UUID conceptId2) {
        UUID neuronId1 = conceptToNeuronMap.get(conceptId1);
        UUID neuronId2 = conceptToNeuronMap.get(conceptId2);
        
        if (neuronId1 != null && neuronId2 != null) {
            // Check if synapse exists
            List<ConnectomeSynapse> synapses = connectome.getSynapses(neuronId1);
            ConnectomeSynapse synapse = null;
            
            for (ConnectomeSynapse s : synapses) {
                if (s.getTargetNeuronId().equals(neuronId2)) {
                    synapse = s;
                    break;
                }
            }
            
            if (synapse == null) {
                // Create new synapse
                synapse = new ConnectomeSynapse(neuronId1, neuronId2, 0.5, Neurotransmitter.ACETYLCHOLINE);
                connectome.addSynapse(synapse);
            }
            
            // Strengthen the connection (Hebbian learning)
            // Requires pre-synaptic and post-synaptic activity
            synapse.strengthen(0.8, 0.8);
        }
    }
    
    /**
     * Run GNN message passing
     */
    public void propagateSignals() {
        gnn.messagePassing();
    }
}
