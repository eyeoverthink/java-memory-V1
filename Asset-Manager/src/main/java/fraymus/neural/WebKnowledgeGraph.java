package fraymus.neural;

import java.util.*;
import java.util.concurrent.*;

/**
 * WebKnowledgeGraph - Knowledge Graph for Web/Document Data
 * 
 * Stores:
 * - Nodes: URLs, documents, entities
 * - Edges: citations, references, similarities
 * 
 * Operations:
 * - Add nodes/edges
 * - Find similar nodes
 * - Find cited-by/citing relationships
 */
public class WebKnowledgeGraph {
    
    private final Map<String, WebNode> nodes = new ConcurrentHashMap<>();
    private final Map<String, WebEdge> edges = new ConcurrentHashMap<>();
    
    public WebKnowledgeGraph() {
        System.out.println("[WebKnowledgeGraph] Initialized");
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    // NODE OPERATIONS
    // ═════════════════════════════════════════════════════════════════════════
    
    public void addNode(WebNode node) {
        nodes.put(node.getId(), node);
    }
    
    public WebNode getNode(String id) {
        return nodes.get(id);
    }
    
    public Collection<WebNode> getAllNodes() {
        return nodes.values();
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    // EDGE OPERATIONS
    // ═════════════════════════════════════════════════════════════════════════
    
    public void addEdge(WebEdge edge) {
        edges.put(edge.getId(), edge);
    }
    
    public WebEdge getEdge(String id) {
        return edges.get(id);
    }
    
    public Collection<WebEdge> getAllEdges() {
        return edges.values();
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ═════════════════════════════════════════════════════════════════════════
    
    public List<WebNode> findSimilar(String query) {
        // Simple implementation: find nodes with similar content
        // In full implementation, use semantic similarity
        List<WebNode> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (WebNode node : nodes.values()) {
            if (node.getContent().toLowerCase().contains(lowerQuery) ||
                node.getType().toLowerCase().contains(lowerQuery)) {
                results.add(node);
            }
        }
        
        return results;
    }
    
    public List<WebNode> findCitedBy(String sourceId) {
        // Find all nodes that cite this source
        List<WebNode> results = new ArrayList<>();
        
        for (WebEdge edge : edges.values()) {
            if (edge.getType().equals("citation") && edge.getTargetId().equals(sourceId)) {
                WebNode sourceNode = nodes.get(edge.getSourceId());
                if (sourceNode != null) {
                    results.add(sourceNode);
                }
            }
        }
        
        return results;
    }
    
    public List<WebNode> findCiting(String sourceId) {
        // Find all nodes that this source cites
        List<WebNode> results = new ArrayList<>();
        
        for (WebEdge edge : edges.values()) {
            if (edge.getType().equals("citation") && edge.getSourceId().equals(sourceId)) {
                WebNode targetNode = nodes.get(edge.getTargetId());
                if (targetNode != null) {
                    results.add(targetNode);
                }
            }
        }
        
        return results;
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═════════════════════════════════════════════════════════════════════════
    
    public int getNodeCount() { return nodes.size(); }
    
    public int getEdgeCount() { return edges.size(); }
    
    // ═════════════════════════════════════════════════════════════════════════
    // NESTED CLASSES
    // ═════════════════════════════════════════════════════════════════════════
    
    public static class WebNode {
        private String id;
        private String type; // url, document, entity
        private String url;
        private String content;
        private Map<String, Object> metadata;
        private long createdAt;
        
        public WebNode(String id, String type, String url, String content) {
            this.id = id;
            this.type = type;
            this.url = url;
            this.content = content;
            this.metadata = new HashMap<>();
            this.createdAt = System.currentTimeMillis();
        }
        
        public String getId() { return id; }
        public String getType() { return type; }
        public String getUrl() { return url; }
        public String getContent() { return content; }
        public Map<String, Object> getMetadata() { return metadata; }
        public long getCreatedAt() { return createdAt; }
    }
    
    public static class WebEdge {
        private String id;
        private String sourceId;
        private String targetId;
        private String type; // citation, reference, similarity
        private double weight;
        private long createdAt;
        
        public WebEdge(String sourceId, String targetId, String type) {
            this.id = UUID.randomUUID().toString();
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.type = type;
            this.weight = 1.0;
            this.createdAt = System.currentTimeMillis();
        }
        
        public String getId() { return id; }
        public String getSourceId() { return sourceId; }
        public String getTargetId() { return targetId; }
        public String getType() { return type; }
        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
        public long getCreatedAt() { return createdAt; }
    }
}
