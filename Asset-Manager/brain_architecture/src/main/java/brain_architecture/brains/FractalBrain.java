package brain_architecture.brains;

import brain_architecture.backends.*;
import brain_architecture.connectome.*;
import brain_architecture.core.*;
import brain_architecture.synapses.*;
import java.util.*;

/**
 * Fractal Brain - Pattern recognition, recursive thinking
 * Backends: obsidian, neoClaw, openclaw
 * 
 * Enhanced with ConnectomeGraph for internal neural representation
 */
public class FractalBrain extends QuantumBrain {
    
    private final ConnectomeGraph connectome;
    private final ConnectomeGNN gnn;
    private final Map<UUID, UUID> conceptToNeuronMap;
    private UUID patternRecognizerId;
    private UUID recursiveEngineId;
    private UUID fractalGeneratorId;

    public FractalBrain(BrainConfiguration config) {
        super(BrainType.FRACTAL, config);
        this.connectome = new ConnectomeGraph();
        this.gnn = new ConnectomeGNN(connectome);
        this.conceptToNeuronMap = new HashMap<>();
        initializeNeuropils();
    }
    
    private void initializeNeuropils() {
        Neuropil patternRecognizer = new Neuropil("PATTERN_RECOGNIZER", "Pattern recognizer - self-similar patterns", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(patternRecognizer);
        patternRecognizerId = patternRecognizer.getNeuropilId();
        
        Neuropil recursiveEngine = new Neuropil("RECURSIVE_ENGINE", "Recursive engine - nested processing", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(recursiveEngine);
        recursiveEngineId = recursiveEngine.getNeuropilId();
        
        Neuropil fractalGenerator = new Neuropil("FRACTAL_GENERATOR", "Fractal generator - scale invariance", Neuropil.NeuropilType.ASSOCIATION);
        connectome.addNeuropil(fractalGenerator);
        fractalGeneratorId = fractalGenerator.getNeuropilId();
    }

    @Override
    public void registerBackends() {
        ObsidianBackend obsidian = new ObsidianBackend();
        NeoClawBackend neoClaw = new NeoClawBackend();
        OpenClawBackend openclaw = new OpenClawBackend();

        try {
            obsidian.initialize();
            neoClaw.initialize();
            openclaw.initialize();

            backendEnsemble.registerBackend(obsidian);
            backendEnsemble.registerBackend(neoClaw);
            backendEnsemble.registerBackend(openclaw);
        } catch (Exception e) {
            System.err.println("Failed to initialize Fractal Brain backends: " + e.getMessage());
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
