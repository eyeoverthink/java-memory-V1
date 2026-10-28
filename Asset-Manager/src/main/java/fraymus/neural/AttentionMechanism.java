package fraymus.neural;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AttentionMechanism - Focuses attention on relevant information
 * 
 * This class implements an attention mechanism that scores and filters
 * information based on relevance to a query or context. It helps the system
 * focus on the most relevant information in working memory.
 * 
 * CAPABILITIES:
 * - Attention scoring based on relevance
 * - Context focusing (filter to top-K relevant items)
 * - Relevance filtering (threshold-based)
 * - Cycle detection (RULE WM-2)
 * - Configurable attention depth
 * 
 * DESIGN PRINCIPLES:
 * - Safe: No infinite loops (RULE WM-2)
 * - Efficient: O(n) scoring, O(n log n) sorting for top-K
 * - Configurable: Attention depth and thresholds adjustable
 * - Deterministic: No randomness
 * 
 * PHASE 1.2: Working Memory Layer
 * Part of the AGI System Blueprint
 */
public class AttentionMechanism {

    // Default attention depth (max items to consider)
    public static final int DEFAULT_ATTENTION_DEPTH = 10;
    
    // Default relevance threshold
    public static final double DEFAULT_RELEVANCE_THRESHOLD = 0.3;
    
    // Maximum attention depth to prevent infinite loops (RULE WM-2)
    public static final int MAX_ATTENTION_DEPTH = 100;
    
    // Configuration
    private volatile int attentionDepth = DEFAULT_ATTENTION_DEPTH;
    private volatile double relevanceThreshold = DEFAULT_RELEVANCE_THRESHOLD;
    
    // Statistics
    private final AtomicInteger scoringOperations = new AtomicInteger(0);
    private final AtomicInteger filteringOperations = new AtomicInteger(0);
    private final AtomicInteger cycleDetections = new AtomicInteger(0);

    /**
     * Create a new AttentionMechanism with default settings.
     */
    public AttentionMechanism() {
        // Default constructor
    }

    /**
     * Create a new AttentionMechanism with specified attention depth.
     * 
     * @param attentionDepth Maximum items to consider
     */
    public AttentionMechanism(int attentionDepth) {
        setAttentionDepth(attentionDepth);
    }

    /**
     * Score items based on relevance to a query.
     * 
     * Uses a simple keyword matching algorithm for relevance scoring.
     * In a more advanced implementation, this could use semantic similarity
     * with HDC vectors or other techniques.
     * 
     * @param items The items to score
     * @param query The query to score against
     * @return List of scored items sorted by relevance
     */
    public List<ScoredItem> score(List<WorkingMemory.MemoryItem> items, String query) {
        if (items == null || items.isEmpty() || query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        
        scoringOperations.incrementAndGet();
        
        List<ScoredItem> scored = new ArrayList<>();
        String[] queryWords = query.toLowerCase().split("\\s+");
        
        for (WorkingMemory.MemoryItem item : items) {
            double score = computeRelevance(item.content, queryWords);
            scored.add(new ScoredItem(item, score));
        }
        
        // Sort by score (descending)
        scored.sort((a, b) -> Double.compare(b.score, a.score));
        
        return scored;
    }

    /**
     * Compute relevance score between item content and query words.
     * 
     * @param content The item content
     * @param queryWords The query words
     * @return Relevance score [0, 1]
     */
    private double computeRelevance(String content, String[] queryWords) {
        String contentLower = content.toLowerCase();
        int matches = 0;
        
        for (String word : queryWords) {
            if (contentLower.contains(word)) {
                matches++;
            }
        }
        
        // Score based on proportion of query words matched
        double score = (double) matches / queryWords.length;
        
        // Boost score if content is very similar to query
        if (contentLower.contains(queryWords[0])) {
            score = Math.min(1.0, score + 0.2);
        }
        
        return Math.min(1.0, Math.max(0.0, score));
    }

    /**
     * Focus attention on top-K most relevant items.
     * 
     * @param items The items to focus on
     * @param query The query to focus on
     * @param k Number of items to return
     * @return Top-K most relevant items
     */
    public List<WorkingMemory.MemoryItem> focusTopK(List<WorkingMemory.MemoryItem> items, String query, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("K must be positive");
        }
        if (k > attentionDepth) {
            k = attentionDepth; // Enforce attention depth limit
        }
        
        List<ScoredItem> scored = score(items, query);
        
        List<WorkingMemory.MemoryItem> result = new ArrayList<>();
        for (int i = 0; i < Math.min(k, scored.size()); i++) {
            result.add(scored.get(i).item);
        }
        
        filteringOperations.incrementAndGet();
        
        return result;
    }

    /**
     * Focus attention on top-K most relevant items using default depth.
     * 
     * @param items The items to focus on
     * @param query The query to focus on
     * @return Top-K most relevant items
     */
    public List<WorkingMemory.MemoryItem> focusTopK(List<WorkingMemory.MemoryItem> items, String query) {
        return focusTopK(items, query, attentionDepth);
    }

    /**
     * Filter items by relevance threshold.
     * 
     * @param items The items to filter
     * @param query The query to filter by
     * @param threshold Minimum relevance score
     * @return Items above threshold
     */
    public List<WorkingMemory.MemoryItem> filterByThreshold(List<WorkingMemory.MemoryItem> items, String query, double threshold) {
        if (threshold < 0.0 || threshold > 1.0) {
            throw new IllegalArgumentException("Threshold must be in [0, 1]");
        }
        
        List<ScoredItem> scored = score(items, query);
        
        List<WorkingMemory.MemoryItem> result = new ArrayList<>();
        for (ScoredItem scoredItem : scored) {
            if (scoredItem.score >= threshold) {
                result.add(scoredItem.item);
            }
        }
        
        filteringOperations.incrementAndGet();
        
        return result;
    }

    /**
     * Filter items by relevance threshold using default threshold.
     * 
     * @param items The items to filter
     * @param query The query to filter by
     * @return Items above threshold
     */
    public List<WorkingMemory.MemoryItem> filterByThreshold(List<WorkingMemory.MemoryItem> items, String query) {
        return filterByThreshold(items, query, relevanceThreshold);
    }

    /**
     * Detect cycles in attention pattern.
     * 
     * RULE WM-2: Attention must not create infinite loops
     * This method detects if the same items are being attended to repeatedly.
     * 
     * @param recentAttentions Recent attention history
     * @param currentItems Current items being attended to
     * @return True if cycle detected, false otherwise
     */
    public boolean detectCycle(List<List<WorkingMemory.MemoryItem>> recentAttentions, List<WorkingMemory.MemoryItem> currentItems) {
        if (recentAttentions == null || recentAttentions.isEmpty() || currentItems == null) {
            return false;
        }
        
        // Check if current items match any recent attention pattern
        for (List<WorkingMemory.MemoryItem> pastAttention : recentAttentions) {
            if (itemsMatch(pastAttention, currentItems)) {
                cycleDetections.incrementAndGet();
                return true;
            }
        }
        
        return false;
    }

    /**
     * Check if two lists of items match (by content).
     */
    private boolean itemsMatch(List<WorkingMemory.MemoryItem> a, List<WorkingMemory.MemoryItem> b) {
        if (a.size() != b.size()) {
            return false;
        }
        
        Set<String> aContents = new HashSet<>();
        for (WorkingMemory.MemoryItem item : a) {
            aContents.add(item.content);
        }
        
        Set<String> bContents = new HashSet<>();
        for (WorkingMemory.MemoryItem item : b) {
            bContents.add(item.content);
        }
        
        return aContents.equals(bContents);
    }

    /**
     * Set the attention depth.
     * 
     * RULE WM-2: Attention depth must be limited to prevent infinite loops
     * 
     * @param depth New attention depth
     */
    public void setAttentionDepth(int depth) {
        if (depth <= 0) {
            throw new IllegalArgumentException("Attention depth must be positive");
        }
        if (depth > MAX_ATTENTION_DEPTH) {
            throw new IllegalArgumentException("Attention depth cannot exceed " + MAX_ATTENTION_DEPTH);
        }
        this.attentionDepth = depth;
    }

    /**
     * Get the current attention depth.
     * 
     * @return Current attention depth
     */
    public int getAttentionDepth() {
        return attentionDepth;
    }

    /**
     * Set the relevance threshold.
     * 
     * @param threshold New threshold [0, 1]
     */
    public void setRelevanceThreshold(double threshold) {
        if (threshold < 0.0 || threshold > 1.0) {
            throw new IllegalArgumentException("Threshold must be in [0, 1]");
        }
        this.relevanceThreshold = threshold;
    }

    /**
     * Get the current relevance threshold.
     * 
     * @return Current threshold
     */
    public double getRelevanceThreshold() {
        return relevanceThreshold;
    }

    /**
     * Get statistics about attention operations.
     * 
     * @return Statistics string
     */
    public String getStats() {
        return String.format(
            "AttentionMechanism Stats:\n" +
            "  Attention depth: %,d\n" +
            "  Relevance threshold: %.3f\n" +
            "  Scoring operations: %,d\n" +
            "  Filtering operations: %,d\n" +
            "  Cycle detections: %,d",
            attentionDepth,
            relevanceThreshold,
            scoringOperations.get(),
            filteringOperations.get(),
            cycleDetections.get()
        );
    }

    /**
     * Scored item wrapper.
     */
    public static class ScoredItem {
        public final WorkingMemory.MemoryItem item;
        public final double score;
        
        public ScoredItem(WorkingMemory.MemoryItem item, double score) {
            this.item = item;
            this.score = score;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%.3f)", item.content, score);
        }
    }
}
