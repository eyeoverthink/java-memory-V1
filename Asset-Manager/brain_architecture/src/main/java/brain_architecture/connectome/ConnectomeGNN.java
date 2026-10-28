package brain_architecture.connectome;

import java.util.*;

/**
 * ConnectomeGNN - Graph Neural Network for connectome message passing.
 * 
 * Implements message passing across the connectome graph to propagate
 * information through synaptic connections. Supports multi-hop message
 * passing for small-world property verification (4 hops to any neuron).
 */
public class ConnectomeGNN {
    
    private ConnectomeGraph graph;
    private int numHops;
    private double currentTime;
    private int stepCount;
    
    public ConnectomeGNN(ConnectomeGraph graph) {
        this.graph = graph;
        this.numHops = 4; // Default: 4 hops for small-world property
        this.currentTime = 0.0;
        this.stepCount = 0;
    }
    
    public ConnectomeGNN(ConnectomeGraph graph, int numHops) {
        this(graph);
        this.numHops = Math.max(1, numHops);
    }
    
    public ConnectomeGraph getGraph() {
        return graph;
    }
    
    public int getNumHops() {
        return numHops;
    }
    
    public void setNumHops(int numHops) {
        this.numHops = Math.max(1, numHops);
    }
    
    public double getCurrentTime() {
        return currentTime;
    }
    
    public int getStepCount() {
        return stepCount;
    }
    
    /**
     * Perform message passing across the graph for specified number of hops.
     * Each hop propagates information through synaptic connections.
     */
    public void messagePassing() {
        for (int hop = 0; hop < numHops; hop++) {
            performMessagePassingStep();
        }
    }
    
    /**
     * Perform a single message passing step (one hop).
     */
    public void performMessagePassingStep() {
        // Store old membrane potentials for synchronous update
        Map<UUID, Double> oldPotentials = new HashMap<>();
        for (var neuron : graph.getAllNeurons()) {
            oldPotentials.put(neuron.getNeuronId(), neuron.getMembranePotential());
        }
        
        // Aggregate messages and update neurons
        for (var neuron : graph.getAllNeurons()) {
            UUID neuronId = neuron.getNeuronId();
            
            // Aggregate messages from incoming neighbors
            double aggregated = aggregateMessages(neuronId, oldPotentials);
            
            // Update neuron state
            boolean fired = neuron.activate(aggregated, currentTime);
            
            // If fired, activate outgoing synapses
            if (fired) {
                for (var synapse : graph.getOutgoingSynapses(neuronId)) {
                    synapse.activate();
                }
            }
        }
        
        // Advance time
        currentTime += 1.0;
        stepCount++;
    }
    
    /**
     * Aggregate messages from incoming neighbors.
     * Weighted sum of neighbor membrane potentials multiplied by synaptic weights.
     */
    private double aggregateMessages(UUID neuronId, Map<UUID, Double> oldPotentials) {
        double sum = 0.0;
        
        for (var synapse : graph.getIncomingSynapses(neuronId)) {
            UUID sourceId = synapse.getSourceNeuronId();
            Double sourcePotential = oldPotentials.get(sourceId);
            
            if (sourcePotential != null) {
                // Message = neighbor potential * synaptic weight
                double message = sourcePotential * synapse.getEffectiveWeight();
                sum += message;
            }
        }
        
        return sum;
    }
    
    /**
     * Reset simulation state.
     */
    public void reset() {
        graph.resetAllNeurons();
        this.currentTime = 0.0;
        this.stepCount = 0;
    }
    
    /**
     * Verify small-world property: check if most neurons are reachable within numHops.
     * Returns the percentage of neuron pairs that are connected within numHops.
     */
    public double verifySmallWorldProperty() {
        if (graph.getNeuronCount() < 2) {
            return 1.0;
        }
        
        int totalPairs = 0;
        int connectedPairs = 0;
        
        List<UUID> neuronIds = new ArrayList<>(graph.getAllNeurons().stream()
            .map(n -> n.getNeuronId())
            .toList());
        
        // Sample pairs (to avoid O(N^2) for large graphs)
        int sampleSize = Math.min(1000, neuronIds.size() * neuronIds.size() / 2);
        Random random = new Random(42);
        
        for (int i = 0; i < sampleSize; i++) {
            UUID source = neuronIds.get(random.nextInt(neuronIds.size()));
            UUID target = neuronIds.get(random.nextInt(neuronIds.size()));
            
            if (!source.equals(target)) {
                totalPairs++;
                int distance = graph.getShortestPath(source, target);
                if (distance >= 0 && distance <= numHops) {
                    connectedPairs++;
                }
            }
        }
        
        return totalPairs > 0 ? (double) connectedPairs / totalPairs : 0.0;
    }
    
    /**
     * Get average path length across the graph.
     */
    public double getAveragePathLength() {
        if (graph.getNeuronCount() < 2) {
            return 0.0;
        }
        
        List<UUID> neuronIds = new ArrayList<>(graph.getAllNeurons().stream()
            .map(n -> n.getNeuronId())
            .toList());
        
        int totalDistance = 0;
        int pairCount = 0;
        
        // Sample pairs
        int sampleSize = Math.min(500, neuronIds.size() * neuronIds.size() / 2);
        Random random = new Random(42);
        
        for (int i = 0; i < sampleSize; i++) {
            UUID source = neuronIds.get(random.nextInt(neuronIds.size()));
            UUID target = neuronIds.get(random.nextInt(neuronIds.size()));
            
            if (!source.equals(target)) {
                int distance = graph.getShortestPath(source, target);
                if (distance >= 0) {
                    totalDistance += distance;
                    pairCount++;
                }
            }
        }
        
        return pairCount > 0 ? (double) totalDistance / pairCount : 0.0;
    }
    
    /**
     * Get clustering coefficient (local clustering).
     */
    public double getClusteringCoefficient() {
        double totalClustering = 0.0;
        int neuronCount = 0;
        
        for (var neuron : graph.getAllNeurons()) {
            UUID neuronId = neuron.getNeuronId();
            List<Neuron> neighbors = graph.getNeighbors(neuronId);
            
            if (neighbors.size() < 2) {
                continue;
            }
            
            int possibleEdges = neighbors.size() * (neighbors.size() - 1) / 2;
            int actualEdges = 0;
            
            // Count edges between neighbors
            for (int i = 0; i < neighbors.size(); i++) {
                for (int j = i + 1; j < neighbors.size(); j++) {
                    UUID id1 = neighbors.get(i).getNeuronId();
                    UUID id2 = neighbors.get(j).getNeuronId();
                    
                    // Check if connected (either direction)
                    if (graph.getShortestPath(id1, id2) == 1) {
                        actualEdges++;
                    }
                }
            }
            
            totalClustering += (double) actualEdges / possibleEdges;
            neuronCount++;
        }
        
        return neuronCount > 0 ? totalClustering / neuronCount : 0.0;
    }
    
    @Override
    public String toString() {
        return "ConnectomeGNN{" +
            "numHops=" + numHops +
            ", steps=" + stepCount +
            ", time=" + String.format("%.1f", currentTime) +
            '}';
    }
}
