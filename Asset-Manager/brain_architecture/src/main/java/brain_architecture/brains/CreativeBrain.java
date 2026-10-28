package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;

/**
 * Creative Brain - Imagination, synthesis, innovation
 * Backends: gemma3, claude, ollama
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class CreativeBrain extends QuantumBrain {
    
    // Internal connectome for neural representation
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    
    // Mapping from concept IDs to neuron IDs
    private final Map<UUID, UUID> conceptToNeuronMap;
    
    // Neuropil IDs for quick access
    private UUID defaultModeNetworkId;
    private UUID imaginationCenterId;
    private UUID synthesisHubId;

    public CreativeBrain(BrainConfiguration config) {
        super(BrainType.CREATIVE, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        
        // Initialize neuropils for creative brain regions
        initializeNeuropils();
    }
    
    /**
     * Initialize neuropils for creative brain regions
     */
    private void initializeNeuropils() {
        // Default mode network - imagination and creativity
        Neuropil defaultModeNetwork = new Neuropil("DEFAULT_MODE_NETWORK", "Default mode network - imagination and creativity", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(defaultModeNetwork);
        defaultModeNetworkId = defaultModeNetwork.getNeuropilId();
        
        // Imagination center - novel idea generation
        Neuropil imaginationCenter = new Neuropil("IMAGINATION_CENTER", "Imagination center - novel idea generation", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(imaginationCenter);
        imaginationCenterId = imaginationCenter.getNeuropilId();
        
        // Synthesis hub - combining concepts
        Neuropil synthesisHub = new Neuropil("SYNTHESIS_HUB", "Synthesis hub - combining concepts", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(synthesisHub);
        synthesisHubId = synthesisHub.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        Gemma3Backend gemma3 = new Gemma3Backend();
        ClaudeBackend claude = new ClaudeBackend();
        OllamaBackend ollama = new OllamaBackend();

        try {
            gemma3.initialize();
            claude.initialize();
            ollama.initialize();

            backendEnsemble.registerBackend(gemma3);
            backendEnsemble.registerBackend(claude);
            backendEnsemble.registerBackend(ollama);
        } catch (Exception e) {
            System.err.println("Failed to initialize Creative Brain backends: " + e.getMessage());
        }
    }
    
    /**
     * Map a concept to a neuron in the connectome
     */
    public UUID mapConceptToNeuron(UUID conceptId, String conceptDescription) {
        if (conceptToNeuronMap.containsKey(conceptId)) {
            return conceptToNeuronMap.get(conceptId);
        }
        
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        connectome.addNeuron(neuron);
        conceptToNeuronMap.put(conceptId, neuron.getNeuronId());
        
        return neuron.getNeuronId();
    }
    
    /**
     * Strengthen the synaptic connection between two concepts
     */
    public void strengthenSynapse(UUID conceptId1, UUID conceptId2, double preActivity, double postActivity) {
        UUID neuronId1 = mapConceptToNeuron(conceptId1, "");
        UUID neuronId2 = mapConceptToNeuron(conceptId2, "");
        
        ConnectomeSynapse synapse = connectome.getOutgoingSynapses(neuronId1).stream()
            .filter(s -> s.getTargetNeuronId().equals(neuronId2))
            .findFirst()
            .orElse(null);
        if (synapse == null) {
            synapse = new ConnectomeSynapse(neuronId1, neuronId2, 0.1, Neurotransmitter.DOPAMINE);
            connectome.addSynapse(synapse);
        }
        synapse.strengthen(preActivity, postActivity);
    }
    
    /**
     * Propagate a signal through the connectome
     */
    public void propagateSignal(UUID startConceptId) {
        UUID startNeuronId = mapConceptToNeuron(startConceptId, "");
        gnn.performMessagePassingStep();
    }
    
    /**
     * Get the connectome for this brain
     */
    public ConnectomeGraph getConnectome() {
        return connectome;
    }
}
