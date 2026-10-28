package fraymus.neural;

import java.util.*;

/**
 * WebSynapse - URL/Web Content Pattern Synapse
 * 
 * Stores learned patterns from web content:
 * - URL patterns (domain structure, path patterns)
 * - HTTP semantics (content types, response codes)
 * - HTML structure (heading hierarchy, link patterns)
 * - Relevance scoring
 * - Access patterns
 */
public class WebSynapse {
    
    private String id;
    private String urlPattern;
    private String domain;
    private String contentType;
    private double relevanceScore;
    private List<String> extractedEntities;
    private Map<String, Object> semanticFeatures;
    private long lastAccessed;
    private int accessCount;
    private long createdAt;
    
    public WebSynapse(String url, String contentType) {
        this.id = UUID.randomUUID().toString();
        this.urlPattern = extractPattern(url);
        this.domain = extractDomain(url);
        this.contentType = contentType;
        this.extractedEntities = new ArrayList<>();
        this.semanticFeatures = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.lastAccessed = System.currentTimeMillis();
        this.accessCount = 0;
    }
    
    private String extractPattern(String url) {
        // Extract URL pattern (e.g., "https://example.com/path/*")
        try {
            java.net.URL parsed = new java.net.URL(url);
            String path = parsed.getPath();
            if (path.length() > 50) {
                path = path.substring(0, 50) + "*";
            }
            return parsed.getProtocol() + "://" + parsed.getHost() + path;
        } catch (Exception e) {
            return url;
        }
    }
    
    private String extractDomain(String url) {
        try {
            java.net.URL parsed = new java.net.URL(url);
            return parsed.getHost();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    public String getId() { return id; }
    
    public String getUrlPattern() { return urlPattern; }
    
    public String getDomain() { return domain; }
    
    public String getContentType() { return contentType; }
    
    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double score) { this.relevanceScore = score; }
    
    public List<String> getExtractedEntities() { return extractedEntities; }
    public void addEntity(String entity) { this.extractedEntities.add(entity); }
    
    public Map<String, Object> getSemanticFeatures() { return semanticFeatures; }
    public void addSemanticFeature(String key, Object value) { this.semanticFeatures.put(key, value); }
    
    public long getLastAccessed() { return lastAccessed; }
    public void updateAccess() {
        this.lastAccessed = System.currentTimeMillis();
        this.accessCount++;
    }
    
    public int getAccessCount() { return accessCount; }
    
    public long getCreatedAt() { return createdAt; }
    
    @Override
    public String toString() {
        return String.format("WebSynapse[id=%s, domain=%s, type=%s, relevance=%.2f, entities=%d, accesses=%d]",
            id, domain, contentType, relevanceScore, extractedEntities.size(), accessCount);
    }
}
