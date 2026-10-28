package brain_architecture.persistence;

import brain_architecture.synapses.Concept;
import brain_architecture.synapses.Synapse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * KnowledgeGraphDelta - Represents changes to a knowledge graph.
 * Used in blockchain to track modifications over time.
 */
public class KnowledgeGraphDelta {
    private UUID deltaId;
    private UUID graphId;
    private long timestamp;
    private DeltaType type;
    
    // Added concepts and synapses
    private List<Concept> addedConcepts;
    private List<Synapse> addedSynapses;
    
    // Removed concepts and synapses (by ID)
    private List<UUID> removedConceptIds;
    private List<UUID> removedSynapseIds;
    
    // Modified concepts (snapshot after modification)
    private List<Concept> modifiedConcepts;
    private List<Synapse> modifiedSynapses;
    
    public enum DeltaType {
        INITIAL,      // Initial creation of graph
        ADD,          // Adding concepts/synapses
        REMOVE,       // Removing concepts/synapses
        MODIFY,       // Modifying existing concepts/synapses
        MERGE         // Merging multiple graphs
    }
    
    public KnowledgeGraphDelta(UUID graphId, DeltaType type) {
        this.deltaId = UUID.randomUUID();
        this.graphId = graphId;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.addedConcepts = new ArrayList<>();
        this.addedSynapses = new ArrayList<>();
        this.removedConceptIds = new ArrayList<>();
        this.removedSynapseIds = new ArrayList<>();
        this.modifiedConcepts = new ArrayList<>();
        this.modifiedSynapses = new ArrayList<>();
    }
    
    // Add operations
    public void addConcept(Concept concept) {
        addedConcepts.add(concept);
    }
    
    public void addSynapse(Synapse synapse) {
        addedSynapses.add(synapse);
    }
    
    // Remove operations
    public void removeConcept(UUID conceptId) {
        removedConceptIds.add(conceptId);
    }
    
    public void removeSynapse(UUID synapseId) {
        removedSynapseIds.add(synapseId);
    }
    
    // Modify operations
    public void modifyConcept(Concept concept) {
        modifiedConcepts.add(concept);
    }
    
    public void modifySynapse(Synapse synapse) {
        modifiedSynapses.add(synapse);
    }
    
    // Getters
    public UUID getDeltaId() { return deltaId; }
    public UUID getGraphId() { return graphId; }
    public long getTimestamp() { return timestamp; }
    public DeltaType getType() { return type; }
    public List<Concept> getAddedConcepts() { return addedConcepts; }
    public List<Synapse> getAddedSynapses() { return addedSynapses; }
    public List<UUID> getRemovedConceptIds() { return removedConceptIds; }
    public List<UUID> getRemovedSynapseIds() { return removedSynapseIds; }
    public List<Concept> getModifiedConcepts() { return modifiedConcepts; }
    public List<Synapse> getModifiedSynapses() { return modifiedSynapses; }
    
    // Check if delta is empty
    public boolean isEmpty() {
        return addedConcepts.isEmpty() && addedSynapses.isEmpty() &&
               removedConceptIds.isEmpty() && removedSynapseIds.isEmpty() &&
               modifiedConcepts.isEmpty() && modifiedSynapses.isEmpty();
    }
    
    @Override
    public String toString() {
        return "KnowledgeGraphDelta{" +
                "deltaId=" + deltaId +
                ", graphId=" + graphId +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", addedConcepts=" + addedConcepts.size() +
                ", addedSynapses=" + addedSynapses.size() +
                ", removedConcepts=" + removedConceptIds.size() +
                ", removedSynapses=" + removedSynapseIds.size() +
                ", modifiedConcepts=" + modifiedConcepts.size() +
                ", modifiedSynapses=" + modifiedSynapses.size() +
                '}';
    }
}
