package fraymus.neural;

import java.util.*;

/**
 * ExtractionSynapse - Knowledge Extraction Synapse
 * 
 * Stores extracted knowledge from content:
 * - Facts (subject-predicate-object triples)
 * - Relationships (entity connections)
 * - Confidence scores
 */
public class ExtractionSynapse {
    
    private String id;
    private String sourceId;
    private List<Fact> extractedFacts;
    private List<Relationship> relationships;
    private double confidence;
    private long createdAt;
    
    public ExtractionSynapse(String sourceId) {
        this.id = UUID.randomUUID().toString();
        this.sourceId = sourceId;
        this.extractedFacts = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.confidence = 0.0;
        this.createdAt = System.currentTimeMillis();
    }
    
    public String getId() { return id; }
    
    public String getSourceId() { return sourceId; }
    
    public List<Fact> getExtractedFacts() { return extractedFacts; }
    public void addFact(Fact fact) { this.extractedFacts.add(fact); }
    
    public List<Relationship> getRelationships() { return relationships; }
    public void addRelationship(Relationship rel) { this.relationships.add(rel); }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public long getCreatedAt() { return createdAt; }
    
    @Override
    public String toString() {
        return String.format("ExtractionSynapse[id=%s, source=%s, facts=%d, relationships=%d, confidence=%.2f]",
            id, sourceId, extractedFacts.size(), relationships.size(), confidence);
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    // NESTED CLASSES
    // ═════════════════════════════════════════════════════════════════════════
    
    public static class Fact {
        private String id;
        private String subject;
        private String predicate;
        private String object;
        private String context;
        private double confidence;
        
        public Fact(String subject, String predicate, String object) {
            this.id = UUID.randomUUID().toString();
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            this.confidence = 0.5; // default confidence
        }
        
        public String getId() { return id; }
        public String getSubject() { return subject; }
        public String getPredicate() { return predicate; }
        public String getObject() { return object; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        @Override
        public String toString() {
            return String.format("%s %s %s (confidence: %.2f)", subject, predicate, object, confidence);
        }
    }
    
    public static class Relationship {
        private String id;
        private String sourceEntity;
        private String targetEntity;
        private String relationType;
        private double strength;
        
        public Relationship(String source, String target, String type) {
            this.id = UUID.randomUUID().toString();
            this.sourceEntity = source;
            this.targetEntity = target;
            this.relationType = type;
            this.strength = 0.5; // default strength
        }
        
        public String getId() { return id; }
        public String getSourceEntity() { return sourceEntity; }
        public String getTargetEntity() { return targetEntity; }
        public String getRelationType() { return relationType; }
        public double getStrength() { return strength; }
        public void setStrength(double strength) { this.strength = strength; }
    }
}
