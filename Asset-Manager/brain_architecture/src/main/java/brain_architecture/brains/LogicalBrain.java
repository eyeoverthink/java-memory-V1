package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;

/**
 * Logical Brain - Analysis, reasoning, problem solving
 * Backends: claude, gemma3, obsidian
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class LogicalBrain extends QuantumBrain {
    
    // Internal connectome for neural representation
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    
    // Mapping from concept IDs to neuron IDs
    private final Map<UUID, UUID> conceptToNeuronMap;
    
    // Neuropil IDs for quick access
    private UUID prefrontalCortexId;
    private UUID parietalCortexId;
    private UUID temporalCortexId;

    public LogicalBrain(BrainConfiguration config) {
        super(BrainType.LOGICAL, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        
        // Initialize neuropils for logical brain regions
        initializeNeuropils();
    }
    
    /**
     * Initialize neuropils for logical brain regions
     */
    private void initializeNeuropils() {
        // Prefrontal cortex - reasoning and planning
        Neuropil prefrontalCortex = new Neuropil("PREFRONTAL_CORTEX", "Prefrontal cortex - reasoning and planning", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(prefrontalCortex);
        prefrontalCortexId = prefrontalCortex.getNeuropilId();
        
        // Parietal cortex - spatial reasoning
        Neuropil parietalCortex = new Neuropil("PARIETAL_CORTEX", "Parietal cortex - spatial reasoning", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(parietalCortex);
        parietalCortexId = parietalCortex.getNeuropilId();
        
        // Temporal cortex - logical processing
        Neuropil temporalCortex = new Neuropil("TEMPORAL_CORTEX", "Temporal cortex - logical processing", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(temporalCortex);
        temporalCortexId = temporalCortex.getNeuropilId();
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
            System.err.println("Failed to initialize Logical Brain backends: " + e.getMessage());
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
            synapse = new ConnectomeSynapse(neuronId1, neuronId2, 0.1, Neurotransmitter.ACETYLCHOLINE);
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
