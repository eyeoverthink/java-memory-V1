package brain_architecture.connectome;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ConnectomeGraph - Graph structure representing the neural connectome.
 * 
 * Manages neurons, synapses, and neuropils in a graph structure inspired
 * by the Drosophila connectome. Supports graph operations, queries,
 * and rich club detection.
 */
public class ConnectomeGraph {
    
    private Map<UUID, Neuron> neurons;
    private Map<UUID, ConnectomeSynapse> synapses;
    private Map<UUID, Neuropil> neuropils;
    private RichClub richClub;
    
    public ConnectomeGraph() {
        this.neurons = new HashMap<>();
        this.synapses = new HashMap<>();
        this.neuropils = new HashMap<>();
        this.richClub = new RichClub();
    }
    
    // ========== Graph Operations ==========
    
    public void addNeuron(Neuron neuron) {
        neurons.put(neuron.getNeuronId(), neuron);
    }
    
    public void addSynapse(ConnectomeSynapse synapse) {
        synapses.put(synapse.getSynapseId(), synapse);
    }
    
    public void removeNeuron(UUID neuronId) {
        // Remove neuron
        neurons.remove(neuronId);
        
        // Remove associated synapses
        synapses.entrySet().removeIf(entry -> 
            entry.getValue().getSourceNeuronId().equals(neuronId) ||
            entry.getValue().getTargetNeuronId().equals(neuronId)
        );
    }
    
    public void removeSynapse(UUID synapseId) {
        synapses.remove(synapseId);
    }
    
    public Neuron getNeuron(UUID neuronId) {
        return neurons.get(neuronId);
    }
    
    public ConnectomeSynapse getSynapse(UUID synapseId) {
        return synapses.get(synapseId);
    }
    
    public Neuropil getNeuropil(UUID neuropilId) {
        return neuropils.get(neuropilId);
    }
    
    public Collection<Neuron> getAllNeurons() {
        return neurons.values();
    }
    
    public Collection<ConnectomeSynapse> getAllSynapses() {
        return synapses.values();
    }
    
    public Collection<Neuropil> getAllNeuropils() {
        return neuropils.values();
    }
    
    public int getNeuronCount() {
        return neurons.size();
    }
    
    public int getSynapseCount() {
        return synapses.size();
    }
    
    public int getNeuropilCount() {
        return neuropils.size();
    }
    
    public void addNeuropil(Neuropil neuropil) {
        neuropils.put(neuropil.getNeuropilId(), neuropil);
    }
    
    // ========== Query Operations ==========
    
    /**
     * Get neighbors of a neuron (connected via outgoing synapses).
     */
    public List<Neuron> getNeighbors(UUID neuronId) {
        return synapses.values().stream()
            .filter(s -> s.getSourceNeuronId().equals(neuronId))
            .map(s -> neurons.get(s.getTargetNeuronId()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    /**
     * Get incoming neighbors (neurons that connect to this neuron).
     */
    public List<Neuron> getIncomingNeighbors(UUID neuronId) {
        return synapses.values().stream()
            .filter(s -> s.getTargetNeuronId().equals(neuronId))
            .map(s -> neurons.get(s.getSourceNeuronId()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all synapses for a neuron (both incoming and outgoing).
     */
    public List<ConnectomeSynapse> getSynapses(UUID neuronId) {
        return synapses.values().stream()
            .filter(s -> s.getSourceNeuronId().equals(neuronId) || 
                       s.getTargetNeuronId().equals(neuronId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get outgoing synapses for a neuron.
     */
    public List<ConnectomeSynapse> getOutgoingSynapses(UUID neuronId) {
        return synapses.values().stream()
            .filter(s -> s.getSourceNeuronId().equals(neuronId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get incoming synapses for a neuron.
     */
    public List<ConnectomeSynapse> getIncomingSynapses(UUID neuronId) {
        return synapses.values().stream()
            .filter(s -> s.getTargetNeuronId().equals(neuronId))
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate shortest path between two neurons (BFS).
     * Returns number of hops, or -1 if no path exists.
     */
    public int getShortestPath(UUID source, UUID target) {
        if (!neurons.containsKey(source) || !neurons.containsKey(target)) {
            return -1;
        }
        
        if (source.equals(target)) {
            return 0;
        }
        
        // BFS
        Queue<UUID> queue = new LinkedList<>();
        Set<UUID> visited = new HashSet<>();
        Map<UUID, Integer> distance = new HashMap<>();
        
        queue.add(source);
        visited.add(source);
        distance.put(source, 0);
        
        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            int currentDist = distance.get(current);
            
            if (current.equals(target)) {
                return currentDist;
            }
            
            for (Neuron neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor.getNeuronId())) {
                    visited.add(neighbor.getNeuronId());
                    distance.put(neighbor.getNeuronId(), currentDist + 1);
                    queue.add(neighbor.getNeuronId());
                }
            }
        }
        
        return -1; // No path found
    }
    
    /**
     * Get degree of a neuron (number of connections).
     */
    public int getDegree(UUID neuronId) {
        return getSynapses(neuronId).size();
    }
    
    /**
     * Get average degree of all neurons.
     */
    public double getAverageDegree() {
        if (neurons.isEmpty()) {
            return 0.0;
        }
        
        int totalDegree = neurons.values().stream()
            .mapToInt(n -> getDegree(n.getNeuronId()))
            .sum();
        
        return (double) totalDegree / neurons.size();
    }
    
    /**
     * Get neurons in a specific neuropil.
     */
    public List<Neuron> getNeuronsInNeuropil(UUID neuropilId) {
        return neurons.values().stream()
            .filter(n -> n.getNeuropil() != null && 
                       n.getNeuropil().getNeuropilId().equals(neuropilId))
            .collect(Collectors.toList());
    }
    
    // ========== Rich Club ==========
    
    public void detectRichClub() {
        // Build adjacency list
        Map<UUID, List<UUID>> adjacencyList = new HashMap<>();
        for (Neuron neuron : neurons.values()) {
            UUID neuronId = neuron.getNeuronId();
            List<UUID> neighbors = getNeighbors(neuronId).stream()
                .map(Neuron::getNeuronId)
                .collect(Collectors.toList());
            adjacencyList.put(neuronId, neighbors);
        }
        
        richClub.detectRichClub(adjacencyList);
    }
    
    public List<Neuron> getRichClubMembers() {
        return richClub.getMembers().stream()
            .map(neurons::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    public RichClub getRichClub() {
        return richClub;
    }
    
    public boolean isRichClubMember(UUID neuronId) {
        return richClub.isMember(neuronId);
    }
    
    // ========== Simulation ==========
    
    /**
     * Reset all neurons to resting state.
     */
    public void resetAllNeurons() {
        neurons.values().forEach(Neuron::reset);
    }
    
    /**
     * Clear all synapses and neurons.
     */
    public void clear() {
        neurons.clear();
        synapses.clear();
        neuropils.clear();
        richClub.clear();
    }
    
    @Override
    public String toString() {
        return "ConnectomeGraph{" +
            "neurons=" + neurons.size() +
            ", synapses=" + synapses.size() +
            ", neuropils=" + neuropils.size() +
            ", richClub=" + richClub +
            '}';
    }
}
