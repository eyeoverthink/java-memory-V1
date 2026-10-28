package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;

/**
 * Emotional Brain - Empathy, intuition, feeling
 * Backends: claude, ollama, neoClaw
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class EmotionalBrain extends QuantumBrain {
    
    // Internal connectome for neural representation
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    
    // Mapping from concept IDs to neuron IDs
    private final Map<UUID, UUID> conceptToNeuronMap;
    
    // Neuropil IDs for quick access
    private UUID amygdalaId;
    private UUID insulaId;
    private UUID anteriorCingulateId;

    public EmotionalBrain(BrainConfiguration config) {
        super(BrainType.EMOTIONAL, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        
        // Initialize neuropils for emotional brain regions
        initializeNeuropils();
    }
    
    /**
     * Initialize neuropils for emotional brain regions
     */
    private void initializeNeuropils() {
        // Amygdala - emotion processing
        Neuropil amygdala = new Neuropil("AMYGDALA", "Amygdala - emotion processing", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(amygdala);
        amygdalaId = amygdala.getNeuropilId();
        
        // Insula - empathy and intuition
        Neuropil insula = new Neuropil("INSULA", "Insula - empathy and intuition", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(insula);
        insulaId = insula.getNeuropilId();
        
        // Anterior cingulate - emotional regulation
        Neuropil anteriorCingulate = new Neuropil("ANTERIOR_CINGULATE", "Anterior cingulate - emotional regulation", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(anteriorCingulate);
        anteriorCingulateId = anteriorCingulate.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        ClaudeBackend claude = new ClaudeBackend();
        OllamaBackend ollama = new OllamaBackend();
        NeoClawBackend neoClaw = new NeoClawBackend();

        try {
            claude.initialize();
            ollama.initialize();
            neoClaw.initialize();

            backendEnsemble.registerBackend(claude);
            backendEnsemble.registerBackend(ollama);
            backendEnsemble.registerBackend(neoClaw);
        } catch (Exception e) {
            System.err.println("Failed to initialize Emotional Brain backends: " + e.getMessage());
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
            synapse = new ConnectomeSynapse(neuronId1, neuronId2, 0.1, Neurotransmitter.SEROTONIN);
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
