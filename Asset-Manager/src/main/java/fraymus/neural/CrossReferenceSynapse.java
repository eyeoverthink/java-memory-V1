package fraymus.neural;

import java.util.*;

/**
 * CrossReferenceSynapse - Source Linking & Citation Tracking Synapse
 * 
 * Stores cross-references between sources:
 * - Referenced sources (what this source cites)
 * - Referencing sources (what cites this source)
 * - Similarity scores between sources
 */
public class CrossReferenceSynapse {
    
    private String id;
    private String sourceId;
    private List<String> referencedSources;
    private List<String> referencingSources;
    private Map<String, Double> similarityScores;
    private long createdAt;
    
    public CrossReferenceSynapse(String sourceId) {
        this.id = UUID.randomUUID().toString();
        this.sourceId = sourceId;
        this.referencedSources = new ArrayList<>();
        this.referencingSources = new ArrayList<>();
        this.similarityScores = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
    }
    
    public String getId() { return id; }
    
    public String getSourceId() { return sourceId; }
    
    public List<String> getReferencedSources() { return referencedSources; }
    public void addReferencedSource(String source) { 
        if (!this.referencedSources.contains(source)) {
            this.referencedSources.add(source);
        }
    }
    
    public List<String> getReferencingSources() { return referencingSources; }
    public void addReferencingSource(String source) {
        if (!this.referencingSources.contains(source)) {
            this.referencingSources.add(source);
        }
    }
    
    public Map<String, Double> getSimilarityScores() { return similarityScores; }
    public void setSimilarityScore(String otherSource, double score) {
        this.similarityScores.put(otherSource, score);
    }
    
    public double getSimilarityScore(String otherSource) {
        return similarityScores.getOrDefault(otherSource, 0.0);
    }
    
    public long getCreatedAt() { return createdAt; }
    
    @Override
    public String toString() {
        return String.format("CrossReferenceSynapse[id=%s, source=%s, references=%d, referenced_by=%d, similarities=%d]",
            id, sourceId, referencedSources.size(), referencingSources.size(), similarityScores.size());
    }
}
