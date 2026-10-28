package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;

/**
 * Tachyonic Brain - FTL processing, superluminal transfer
 * Backends: ollama, neoClaw, gemma3
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class TachyonicBrain extends QuantumBrain {
    
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    private final Map<UUID, UUID> conceptToNeuronMap;
    private UUID ftlProcessorId;
    private UUID superluminalTransferId;
    private UUID tachyonFieldId;

    public TachyonicBrain(BrainConfiguration config) {
        super(BrainType.TACHYONIC, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        initializeNeuropils();
    }
    
    private void initializeNeuropils() {
        Neuropil ftlProcessor = new Neuropil("FTL_PROCESSOR", "FTL processor - faster-than-light", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(ftlProcessor);
        ftlProcessorId = ftlProcessor.getNeuropilId();
        
        Neuropil superluminalTransfer = new Neuropil("SUPERLUMINAL_TRANSFER", "Superluminal transfer - instant", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(superluminalTransfer);
        superluminalTransferId = superluminalTransfer.getNeuropilId();
        
        Neuropil tachyonField = new Neuropil("TACHYON_FIELD", "Tachyon field - non-local", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(tachyonField);
        tachyonFieldId = tachyonField.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        OllamaBackend ollama = new OllamaBackend();
        NeoClawBackend neoClaw = new NeoClawBackend();
        Gemma3Backend gemma3 = new Gemma3Backend();

        try {
            ollama.initialize();
            neoClaw.initialize();
            gemma3.initialize();

            backendEnsemble.registerBackend(ollama);
            backendEnsemble.registerBackend(neoClaw);
            backendEnsemble.registerBackend(gemma3);
        } catch (Exception e) {
            System.err.println("Failed to initialize Tachyonic Brain backends: " + e.getMessage());
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
