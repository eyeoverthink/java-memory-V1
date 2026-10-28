package fraymus.neural;

import fraymus.acquisition.OpenClaw;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * WebSynapseLearner - Learn URL Patterns and Web Content Features
 * 
 * Learns from web content:
 * - URL patterns (domain structure, path patterns)
 * - Content features (word count, link count, entity density)
 * - Relevance scoring based on extracted features
 * - Access pattern tracking
 */
public class WebSynapseLearner {
    
    private WebCortex webCortex;
    private OpenClaw openClaw;
    
    // Learning statistics
    private final AtomicInteger patternsLearned = new AtomicInteger(0);
    private final AtomicInteger featuresExtracted = new AtomicInteger(0);
    
    public WebSynapseLearner(WebCortex webCortex) {
        this.webCortex = webCortex;
        System.out.println("[WebSynapseLearner] Initialized");
    }
    
    public void setOpenClaw(OpenClaw openClaw) {
        this.openClaw = openClaw;
    }
    
    /**
     * Learn from a URL by acquiring content and creating a WebSynapse
     */
    public WebSynapse learnFromUrl(String url) {
        if (openClaw == null) {
            System.err.println("[WebSynapseLearner] OpenClaw not wired");
            return null;
        }
        
        System.out.println("[WebSynapseLearner] Learning from: " + url);
        
        // Acquire content
        OpenClaw.AcquisitionResult acquisition = openClaw.acquireWeb(url);
        
        if (!acquisition.success) {
            System.err.println("[WebSynapseLearner] Failed to acquire: " + acquisition.error);
            return null;
        }
        
        // Create synapse
        WebSynapse synapse = new WebSynapse(url, acquisition.contentType);
        
        // Extract features
        Map<String, Object> features = extractFeatures(acquisition.content);
        for (Map.Entry<String, Object> entry : features.entrySet()) {
            synapse.addSemanticFeature(entry.getKey(), entry.getValue());
        }
        
        // Calculate relevance
        double relevance = calculateRelevance(acquisition.content, features);
        synapse.setRelevanceScore(relevance);
        
        // Extract entities (basic implementation)
        List<String> entities = extractBasicEntities(acquisition.content);
        for (String entity : entities) {
            synapse.addEntity(entity);
        }
        
        // Add to WebCortex
        webCortex.addWebSynapse(synapse);
        webCortex.incrementUrlsScraped();
        for (int i = 0; i < entities.size(); i++) {
            webCortex.incrementEntitiesExtracted();
        }
        
        patternsLearned.incrementAndGet();
        featuresExtracted.incrementAndGet();
        
        System.out.println("[WebSynapseLearner] Learned pattern: " + synapse.getUrlPattern());
        System.out.println("[WebSynapseLearner] Relevance: " + relevance);
        System.out.println("[WebSynapseLearner] Entities: " + entities.size());
        
        return synapse;
    }
    
    /**
     * Extract features from content
     */
    private Map<String, Object> extractFeatures(String content) {
        Map<String, Object> features = new HashMap<>();
        
        // Basic text features
        features.put("word_count", content.split("\\s+").length);
        features.put("char_count", content.length());
        features.put("line_count", content.split("\n").length);
        
        // HTML-specific features
        if (content.contains("<html") || content.contains("<HTML")) {
            features.put("is_html", true);
            features.put("heading_count", countOccurrences(content, "<h"));
            features.put("link_count", countOccurrences(content, "<a"));
            features.put("paragraph_count", countOccurrences(content, "<p"));
        } else {
            features.put("is_html", false);
        }
        
        // Content density
        features.put("avg_word_length", calculateAvgWordLength(content));
        
        return features;
    }
    
    /**
     * Calculate relevance score based on content features
     */
    private double calculateRelevance(String content, Map<String, Object> features) {
        double relevance = 0.0;
        
        // Content length factor
        int charCount = (Integer) features.getOrDefault("char_count", 0);
        if (charCount > 100) {
            relevance += 0.3;
        }
        
        // HTML structure factor
        Boolean isHtml = (Boolean) features.getOrDefault("is_html", false);
        if (isHtml) {
            relevance += 0.2;
            Integer headingCount = (Integer) features.getOrDefault("heading_count", 0);
            relevance += Math.min(0.3, headingCount * 0.1);
        }
        
        // Entity density factor (will be improved in Phase 2.3)
        // For now, use basic keyword density
        
        // Normalize to 0-1 range
        relevance = Math.min(1.0, relevance);
        
        return relevance;
    }
    
    /**
     * Extract basic entities (simple keyword-based)
     * Full NER implementation in Phase 2.3
     */
    private List<String> extractBasicEntities(String content) {
        List<String> entities = new ArrayList<>();
        
        // Extract capitalized words (simple heuristic)
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && Character.isUpperCase(word.charAt(0)) && 
                word.matches("[A-Z][a-z]+")) {
                entities.add(word);
            }
        }
        
        // Remove duplicates
        Set<String> uniqueEntities = new LinkedHashSet<>(entities);
        
        return new ArrayList<>(uniqueEntities);
    }
    
    /**
     * Predict content type for a URL based on learned patterns
     */
    public String predictContentType(String url) {
        // Simple prediction based on URL pattern
        // Full implementation will use learned synapses
        
        if (url.endsWith(".pdf")) {
            return "application/pdf";
        } else if (url.endsWith(".html") || url.endsWith(".htm")) {
            return "text/html";
        } else if (url.contains(".json")) {
            return "application/json";
        } else if (url.contains(".xml")) {
            return "application/xml";
        }
        
        return "text/html"; // default
    }
    
    // Helper methods
    
    private int countOccurrences(String content, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = content.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }
    
    private double calculateAvgWordLength(String content) {
        String[] words = content.split("\\s+");
        if (words.length == 0) return 0.0;
        
        int totalLength = 0;
        for (String word : words) {
            totalLength += word.length();
        }
        
        return (double) totalLength / words.length;
    }
    
    /**
     * Get learning statistics
     */
    public String getStats() {
        return String.format("[WebSynapseLearner] Patterns learned: %d, Features extracted: %d",
            patternsLearned.get(), featuresExtracted.get());
    }
}
