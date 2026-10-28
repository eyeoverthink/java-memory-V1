package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;

/**
 * Spiritual Brain - Consciousness, awareness, connection
 * Backends: claude, gemma3, obsidian
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class SpiritualBrain extends QuantumBrain {
    
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    private final Map<UUID, UUID> conceptToNeuronMap;
    private UUID consciousnessFieldId;
    private UUID awarenessCenterId;
    private UUID connectionHubId;

    public SpiritualBrain(BrainConfiguration config) {
        super(BrainType.SPIRITUAL, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        initializeNeuropils();
    }
    
    private void initializeNeuropils() {
        Neuropil consciousnessField = new Neuropil("CONSCIOUSNESS_FIELD", "Consciousness field - awareness", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(consciousnessField);
        consciousnessFieldId = consciousnessField.getNeuropilId();
        
        Neuropil awarenessCenter = new Neuropil("AWARENESS_CENTER", "Awareness center - presence", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(awarenessCenter);
        awarenessCenterId = awarenessCenter.getNeuropilId();
        
        Neuropil connectionHub = new Neuropil("CONNECTION_HUB", "Connection hub - interconnection", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(connectionHub);
        connectionHubId = connectionHub.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        ClaudeBackend claude = new ClaudeBackend();
        Gemma3Backend gemma3 = new Gemma3Backend();
        ObsidianBackend obsidian = new ObsidianBackend();

        try {
            claude.initialize();
            gemma3.initialize();
            obsidian.initialize();

            backendEnsemble.registerBackend(claude);
            backendEnsemble.registerBackend(gemma3);
            backendEnsemble.registerBackend(obsidian);
        } catch (Exception e) {
            System.err.println("Failed to initialize Spiritual Brain backends: " + e.getMessage());
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
            synapse = new ConnectomeSynapse(neuronId1, neuronId2, 0.1, Neurotransmitter.DOPAMINE);
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
