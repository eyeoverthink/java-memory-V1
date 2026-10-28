package fraymus.neural;

import java.util.*;

/**
 * DocumentSynapse - Document Structure Synapse
 * 
 * Stores document structure and semantics:
 * - Document type (PDF, HTML, Markdown, etc.)
 * - Structure (sections, headings, paragraphs)
 * - Citations (bibliographic references, in-text citations)
 * - Entities (people, places, organizations, dates)
 */
public class DocumentSynapse {
    
    private String id;
    private String documentId;
    private String documentType;
    private String source;
    private Map<String, Object> structure;
    private List<Citation> citations;
    private Map<String, Entity> entities;
    private long createdAt;
    private long lastModified;
    
    public DocumentSynapse(String documentId, String documentType, String source) {
        this.id = UUID.randomUUID().toString();
        this.documentId = documentId;
        this.documentType = documentType;
        this.source = source;
        this.structure = new HashMap<>();
        this.citations = new ArrayList<>();
        this.entities = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }
    
    public String getId() { return id; }
    
    public String getDocumentId() { return documentId; }
    
    public String getDocumentType() { return documentType; }
    
    public String getSource() { return source; }
    
    public Map<String, Object> getStructure() { return structure; }
    public void addStructureElement(String key, Object value) { this.structure.put(key, value); }
    
    public List<Citation> getCitations() { return citations; }
    public void addCitation(Citation citation) { 
        this.citations.add(citation);
        this.lastModified = System.currentTimeMillis();
    }
    
    public Map<String, Entity> getEntities() { return entities; }
    public void addEntity(Entity entity) {
        this.entities.put(entity.getId(), entity);
        this.lastModified = System.currentTimeMillis();
    }
    
    public long getCreatedAt() { return createdAt; }
    
    public long getLastModified() { return lastModified; }
    
    @Override
    public String toString() {
        return String.format("DocumentSynapse[id=%s, type=%s, source=%s, citations=%d, entities=%d]",
            id, documentType, source, citations.size(), entities.size());
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    // NESTED CLASSES
    // ═════════════════════════════════════════════════════════════════════════
    
    public static class Citation {
        private String id;
        private String source;
        private String type; // bibliographic, in-text, footnote
        private String context;
        private int position;
        
        public Citation(String source, String type, String context) {
            this.id = UUID.randomUUID().toString();
            this.source = source;
            this.type = type;
            this.context = context;
        }
        
        public String getId() { return id; }
        public String getSource() { return source; }
        public String getType() { return type; }
        public String getContext() { return context; }
    }
}
