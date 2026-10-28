package brain_architecture.synapses;

import java.util.*;

/**
 * KnowledgeGraph - Represents a graph of concepts extracted from a source.
 */
public class KnowledgeGraph {
    
    private final String source;
    private final List<Concept> concepts;
    private final List<Synapse> synapses;
    private final Map<String, Object> metadata;
    private List<String> sources; // For directory scraping
    private final long timestamp;
    
    public KnowledgeGraph(String source, List<Concept> concepts) {
        this.source = source;
        this.concepts = new ArrayList<>(concepts);
        this.synapses = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.sources = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getSource() {
        return source;
    }
    
    public List<Concept> getConcepts() {
        return Collections.unmodifiableList(concepts);
    }
    
    public List<Synapse> getSynapses() {
        return Collections.unmodifiableList(synapses);
    }
    
    public void addSynapse(Synapse synapse) {
        this.synapses.add(synapse);
    }
    
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
    
    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    public List<String> getSources() {
        return Collections.unmodifiableList(sources);
    }
    
    public void setSources(List<String> sources) {
        this.sources = new ArrayList<>(sources);
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public int getConceptCount() {
        return concepts.size();
    }
    
    public int getSynapseCount() {
        return synapses.size();
    }
    
    /**
     * Remove concepts that match the given predicate.
     * This allows compression and other operations that need to filter concepts.
     */
    public void removeConceptsIf(java.util.function.Predicate<Concept> predicate) {
        concepts.removeIf(predicate);
    }
    
    /**
     * Find concept by name.
     */
    public Optional<Concept> findConcept(String name) {
        return concepts.stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst();
    }
    
    /**
     * Find concepts by category.
     */
    public List<Concept> findConceptsByCategory(String category) {
        return concepts.stream()
            .filter(c -> c.getCategory().equalsIgnoreCase(category))
            .toList();
    }
}
