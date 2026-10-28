package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;

/**
 * Quantum Specialized Brain - Entanglement, superposition, coherence
 * Backends: claude, gemma3, ollama
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class QuantumSpecializedBrain extends QuantumBrain {
    
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    private final Map<UUID, UUID> conceptToNeuronMap;
    private UUID entanglementMatrixId;
    private UUID superpositionFieldId;
    private UUID coherenceEngineId;

    public QuantumSpecializedBrain(BrainConfiguration config) {
        super(BrainType.QUANTUM, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        initializeNeuropils();
    }
    
    private void initializeNeuropils() {
        Neuropil entanglementMatrix = new Neuropil("ENTANGLEMENT_MATRIX", "Entanglement matrix - non-local correlation", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(entanglementMatrix);
        entanglementMatrixId = entanglementMatrix.getNeuropilId();
        
        Neuropil superpositionField = new Neuropil("SUPERPOSITION_FIELD", "Superposition field - multiple states", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(superpositionField);
        superpositionFieldId = superpositionField.getNeuropilId();
        
        Neuropil coherenceEngine = new Neuropil("COHERENCE_ENGINE", "Coherence engine - phase alignment", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(coherenceEngine);
        coherenceEngineId = coherenceEngine.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        ClaudeBackend claude = new ClaudeBackend();
        Gemma3Backend gemma3 = new Gemma3Backend();
        OllamaBackend ollama = new OllamaBackend();

        try {
            claude.initialize();
            gemma3.initialize();
            ollama.initialize();

            backendEnsemble.registerBackend(claude);
            backendEnsemble.registerBackend(gemma3);
            backendEnsemble.registerBackend(ollama);
        } catch (Exception e) {
            System.err.println("Failed to initialize Quantum Specialized Brain backends: " + e.getMessage());
        }
    }
    
    public UUID mapConceptToNeuron(UUID conceptId, String conceptDescription) {
        if (conceptToNeuronMap.containsKey(conceptId)) {
            return conceptToNeuronMap.get(conceptId);
        }
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        connectome.addNeuron(neuron);
        conceptToNeuronMap.put(conceptId, neuron.getNeuronId());
        return neuron.getNeuronId();
    }
    
    public void strengthenSynapse(UUID conceptId1, UUID conceptId2, double preActivity, double postActivity) {
        UUID neuronId1 = mapConceptToNeuron(conceptId1, "");
        UUID neuronId2 = mapConceptToNeuron(conceptId2, "");
        ConnectomeSynapse synapse = connectome.getOutgoingSynapses(neuronId1).stream()
            .filter(s -> s.getTargetNeuronId().equals(neuronId2))
            .findFirst()
            .orElse(null);
        if (synapse == null) {
            synapse = new ConnectomeSynapse(neuronId1, neuronId2, 0.1, Neurotransmitter.ACETYLCHOLINE);
            connectome.addSynapse(synapse);
        }
        synapse.strengthen(preActivity, postActivity);
    }
    
    public void propagateSignal(UUID startConceptId) {
        mapConceptToNeuron(startConceptId, "");
        gnn.performMessagePassingStep();
    }
    
    public ConnectomeGraph getConnectome() {
        return connectome;
    }
}
