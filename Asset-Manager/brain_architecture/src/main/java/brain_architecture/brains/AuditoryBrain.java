package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Auditory Brain - Speech and audio processing
 * Backends: speechbrain (speech processing), ollama (fast transcription), claude (audio understanding)
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class AuditoryBrain extends QuantumBrain {
    
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    private final Map<UUID, UUID> conceptToNeuronMap;
    private UUID auditoryCortexId;
    private UUID speechCenterId;
    private UUID audioProcessingId;

    public AuditoryBrain(BrainConfiguration config) {
        super(BrainType.AUDITORY, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        initializeNeuropils();
    }
    
    private void initializeNeuropils() {
        Neuropil auditoryCortex = new Neuropil("AUDITORY_CORTEX", "Auditory cortex - sound processing", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(auditoryCortex);
        auditoryCortexId = auditoryCortex.getNeuropilId();
        
        Neuropil speechCenter = new Neuropil("SPEECH_CENTER", "Speech center - language", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(speechCenter);
        speechCenterId = speechCenter.getNeuropilId();
        
        Neuropil audioProcessing = new Neuropil("AUDIO_PROCESSING", "Audio processing - frequency analysis", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(audioProcessing);
        audioProcessingId = audioProcessing.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        SpeechBrainBackend speechbrain = new SpeechBrainBackend();
        OllamaBackend ollama = new OllamaBackend();
        ClaudeBackend claude = new ClaudeBackend();

        // Register backends first, then initialize individually
        backendEnsemble.registerBackend(speechbrain);
        backendEnsemble.registerBackend(ollama);
        backendEnsemble.registerBackend(claude);

        // Initialize each backend independently
        try {
            speechbrain.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize SpeechBrain backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.SPEECHBRAIN, false);
        }

        try {
            ollama.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize Ollama backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.OLLAMA, false);
        }

        try {
            claude.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize Claude backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.CLAUDE, false);
        }
    }
    
    @Override
    public CompletableFuture<BrainResponse> processQuery(String query, Map<String, Object> parameters) {
        // Add auditory-specific parameters
        Map<String, Object> auditoryParams = new HashMap<>();
        auditoryParams.put("task", parameters.getOrDefault("task", "asr"));
        auditoryParams.put("input_file", parameters.getOrDefault("input_file", null));
        auditoryParams.put("model", parameters.getOrDefault("model", "speechbrain/asr-conformer-transformerlm-librispeech"));
        
        return super.processQuery(query, auditoryParams);
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
