package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Visual Brain - Visual generation and understanding
 * Backends: ltxvideo (video generation), gemma3 (frame descriptions), claude (visual analysis)
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class VisualBrain extends QuantumBrain {
    
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    private final Map<UUID, UUID> conceptToNeuronMap;
    private UUID visualCortexId;
    private UUID objectRecognitionId;
    private UUID spatialProcessingId;

    public VisualBrain(BrainConfiguration config) {
        super(BrainType.VISUAL, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        initializeNeuropils();
    }
    
    private void initializeNeuropils() {
        Neuropil visualCortex = new Neuropil("VISUAL_CORTEX", "Visual cortex - image processing", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(visualCortex);
        visualCortexId = visualCortex.getNeuropilId();
        
        Neuropil objectRecognition = new Neuropil("OBJECT_RECOGNITION", "Object recognition - pattern matching", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(objectRecognition);
        objectRecognitionId = objectRecognition.getNeuropilId();
        
        Neuropil spatialProcessing = new Neuropil("SPATIAL_PROCESSING", "Spatial processing - 3D mapping", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(spatialProcessing);
        spatialProcessingId = spatialProcessing.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        LTXVideoBackend ltxvideo = new LTXVideoBackend();
        Gemma3Backend gemma3 = new Gemma3Backend();
        ClaudeBackend claude = new ClaudeBackend();

        // Register backends first, then initialize individually
        backendEnsemble.registerBackend(ltxvideo);
        backendEnsemble.registerBackend(gemma3);
        backendEnsemble.registerBackend(claude);

        // Initialize each backend independently
        try {
            ltxvideo.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize LTXVideo backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.LTXVIDEO, false);
        }

        try {
            gemma3.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize Gemma3 backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.GEMMA3, false);
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
        // Add visual-specific parameters
        Map<String, Object> visualParams = Map.of(
            "height", parameters.getOrDefault("height", 704),
            "width", parameters.getOrDefault("width", 1216),
            "num_frames", parameters.getOrDefault("num_frames", 121),
            "output_path", parameters.getOrDefault("output_path", "visual_output.mp4")
        );
        
        return super.processQuery(query, visualParams);
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
