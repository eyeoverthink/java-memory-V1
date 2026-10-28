package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.Neurotransmitter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Memory Brain - Persistent cross-modal knowledge
 * Enhanced with ConnectomeGraph for neural memory representation
 * Backends: akashicrecord (persistent memory), obsidian (structured knowledge), gemma3 (memory understanding)
 */
public class MemoryBrain extends QuantumBrain {
    
    // Internal connectome for neural memory representation
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    
    // Mapping from memory IDs to neuron IDs
    private final Map<UUID, UUID> memoryToNeuronMap;
    
    // Neuropil IDs for quick access
    private UUID hippocampusId;
    private UUID cortexId;
    private UUID associationAreaId;

    public MemoryBrain(BrainConfiguration config) {
        super(BrainType.MEMORY, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.memoryToNeuronMap = new HashMap<>();
        
        // Initialize neuropils for memory brain regions
        initializeNeuropils();
    }
    
    /**
     * Initialize neuropils for memory brain regions
     */
    private void initializeNeuropils() {
        // Hippocampus (memory formation)
        Neuropil hippocampus = new Neuropil("HIPPOCAMPUS", "Hippocampus - memory formation and consolidation", 
            Neuropil.NeuropilType.MEMORY);
        connectome.addNeuropil(hippocampus);
        hippocampusId = hippocampus.getNeuropilId();
        
        // Cortex (long-term memory storage)
        Neuropil cortex = new Neuropil("CORTEX", "Cortex - long-term memory storage", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(cortex);
        cortexId = cortex.getNeuropilId();
        
        // Association area (cross-modal integration)
        Neuropil associationArea = new Neuropil("ASSOCIATION_AREA", "Association area - cross-modal memory integration", 
            Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(associationArea);
        associationAreaId = associationArea.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        AkashicRecordBackend akashicrecord = new AkashicRecordBackend();
        ObsidianBackend obsidian = new ObsidianBackend();
        Gemma3Backend gemma3 = new Gemma3Backend();

        // Register backends first, then initialize individually
        backendEnsemble.registerBackend(akashicrecord);
        backendEnsemble.registerBackend(obsidian);
        backendEnsemble.registerBackend(gemma3);

        // Initialize each backend independently
        try {
            akashicrecord.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize AkashicRecord backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.AKASHICRECORD, false);
        }

        try {
            obsidian.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize Obsidian backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.OBSIDIAN, false);
        }

        try {
            gemma3.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize Gemma3 backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.GEMMA3, false);
        }
    }
    
    @Override
    public CompletableFuture<BrainResponse> processQuery(String query, Map<String, Object> parameters) {
        // Add memory-specific parameters
        Map<String, Object> memoryParams = Map.of(
            "operation", parameters.getOrDefault("operation", "retrieve"),
            "modality", parameters.getOrDefault("modality", "text"),
            "cross_modal", parameters.getOrDefault("cross_modal", true)
        );
        
        return super.processQuery(query, memoryParams);
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
     * Map a memory to a neuron in the connectome
     */
    public UUID mapMemoryToNeuron(UUID memoryId, String memoryContent) {
        if (memoryToNeuronMap.containsKey(memoryId)) {
            return memoryToNeuronMap.get(memoryId);
        }
        
        // Create a new neuron for this memory
        IntrinsicDescriptor intrinsic = new IntrinsicDescriptor(0.5, 1.0, 0.1, 0.0);
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setIntrinsicDescriptor(intrinsic);
        
        // Assign to hippocampus for memory formation
        Neuropil hippocampus = connectome.getNeuropil(hippocampusId);
        if (hippocampus != null) {
            neuron.setNeuropil(hippocampus);
        }
        
        connectome.addNeuron(neuron);
        memoryToNeuronMap.put(memoryId, neuron.getNeuronId());
        
        return neuron.getNeuronId();
    }
    
    /**
     * Apply synaptic plasticity based on memory co-activation
     */
    public void strengthenMemoryAssociation(UUID memoryId1, UUID memoryId2) {
        UUID neuronId1 = memoryToNeuronMap.get(memoryId1);
        UUID neuronId2 = memoryToNeuronMap.get(memoryId2);
        
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
            synapse.strengthen(0.8, 0.8);
        }
    }
    
    /**
     * Run GNN message passing for memory consolidation
     */
    public void consolidateMemories() {
        gnn.messagePassing();
    }
}
