package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Evolutionary Brain - Self-evolving logic circuits
 * Backends: lazarus (evolution engine), neoClaw (enhanced evolution), claude (evolution analysis)
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class EvolutionaryBrain extends QuantumBrain {
    
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    private final Map<UUID, UUID> conceptToNeuronMap;
    private UUID evolutionEngineId;
    private UUID mutationCenterId;
    private UUID selectionHubId;

    public EvolutionaryBrain(BrainConfiguration config) {
        super(BrainType.EVOLUTIONARY, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        initializeNeuropils();
    }
    
    private void initializeNeuropils() {
        Neuropil evolutionEngine = new Neuropil("EVOLUTION_ENGINE", "Evolution engine - genetic algorithms", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(evolutionEngine);
        evolutionEngineId = evolutionEngine.getNeuropilId();
        
        Neuropil mutationCenter = new Neuropil("MUTATION_CENTER", "Mutation center - variation", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(mutationCenter);
        mutationCenterId = mutationCenter.getNeuropilId();
        
        Neuropil selectionHub = new Neuropil("SELECTION_HUB", "Selection hub - fitness evaluation", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(selectionHub);
        selectionHubId = selectionHub.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        LazarusBackend lazarus = new LazarusBackend();
        NeoClawBackend neoClaw = new NeoClawBackend();
        ClaudeBackend claude = new ClaudeBackend();

        // Register backends first, then initialize individually
        backendEnsemble.registerBackend(lazarus);
        backendEnsemble.registerBackend(neoClaw);
        backendEnsemble.registerBackend(claude);

        // Initialize each backend independently
        try {
            lazarus.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize Lazarus backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.LAZARUS, false);
        }

        try {
            neoClaw.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize NeoClaw backend: " + e.getMessage());
            backendEnsemble.setHealthStatus(BackendInterface.BackendType.NEOCLAW, false);
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
        // Add evolutionary-specific parameters
        Map<String, Object> evolutionaryParams = Map.of(
            "task", parameters.getOrDefault("task", "evolve"),
            "generations", parameters.getOrDefault("generations", 10),
            "mutation_rate", parameters.getOrDefault("mutation_rate", 0.05)
        );
        
        return super.processQuery(query, evolutionaryParams);
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
