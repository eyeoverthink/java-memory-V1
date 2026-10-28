package fraymus.brain.unified;

import fraymus.core.OllamaBridge;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hippocampus Brain - Memory Consolidation
 *
 * Purpose: Consolidate short-term memories into long-term storage
 * Storage: In-memory with LRU eviction
 * Latency: ~100ms | Purpose: Memory consolidation and retrieval
 *
 * The hippocampus is critical for memory formation and consolidation.
 * This component manages the transfer of memories from short-term to
 * long-term storage, using vector similarity for retrieval.
 *
 * NOTE: This implementation uses in-memory storage with vector similarity.
 * Future versions may integrate with external vector databases (Qdrant, Milvus).
 */
public class HippocampusBrain extends AbstractBrainAdapter {
    private final AtomicBoolean initialized;
    private OllamaBridge ollamaBridge;
    private final OllamaBridge sharedOllamaBridge;
    
    // Memory consolidation state tracking
    private int totalMemoriesConsolidated = 0;
    private int successfulConsolidations = 0;
    private Map<String, Integer> consolidationTypeCounts = new HashMap<>();

    // Memory storage configuration
    private static final int EMBEDDING_DIMENSION = 384;
    private final Map<String, MemoryEntry> memoryStore;
    private static final int MAX_MEMORIES = 1000;
    
    // Inner class for memory entries
    public static class MemoryEntry {
        String id;
        String content;
        double[] embedding;
        long timestamp;
        double importance;
        
        MemoryEntry(String id, String content, double[] embedding, double importance) {
            this.id = id;
            this.content = content;
            this.embedding = embedding;
            this.timestamp = System.currentTimeMillis();
            this.importance = importance;
        }
    }
    
    public HippocampusBrain() {
        this(null);
    }
    
    public HippocampusBrain(OllamaBridge sharedOllamaBridge) {
        super("Hippocampus", "Memory", Arrays.asList(
            BrainCapability.MEMORY
        ));
        this.initialized = new AtomicBoolean(false);
        this.sharedOllamaBridge = sharedOllamaBridge;
        this.memoryStore = new LinkedHashMap<>(MAX_MEMORIES, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, MemoryEntry> eldest) {
                return size() > MAX_MEMORIES;
            }
        };
    }
    
    @Override
    protected void onWire() {
        if (initialized.compareAndSet(false, true)) {
            ollamaBridge = sharedOllamaBridge != null ? sharedOllamaBridge : new OllamaBridge("llama3.2");
            System.out.println("[HippocampusBrain] Wired with in-memory storage");
        }
    }
    
    @Override
    protected void onUnwire() {
        if (initialized.compareAndSet(true, false)) {
            ollamaBridge = null;
            System.out.println("[HippocampusBrain] Unwired");
        }
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!initialized.get()) {
            return BrainResponse.error(getName(), "Hippocampus not wired");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            String result = consolidateMemory(query.getQuery());
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("consolidationType", detectConsolidationType(query.getQuery()));
            metadata.put("processingTimeMs", System.currentTimeMillis() - startTime);
            metadata.put("memoryStoreSize", memoryStore.size());
            
            return BrainResponse.success(
                getName(),
                result,
                0.85,
                0.8,
                metadata,
                0
            );
        } catch (Exception e) {
            return BrainResponse.error(getName(), "Memory consolidation failed: " + e.getMessage());
        }
    }
    
    /**
     * Consolidate memory into long-term storage
     * Uses in-memory storage with vector similarity
     */
    public String consolidateMemory(String input) {
        String consolidationType = detectConsolidationType(input);
        consolidationTypeCounts.put(consolidationType, consolidationTypeCounts.getOrDefault(consolidationType, 0) + 1);

        // Generate memory ID
        String memoryId = "mem_" + UUID.randomUUID().toString().substring(0, 8);

        // Placeholder embedding (in real usage, would call EntorhinalCortex)
        double[] embedding = generatePlaceholderEmbedding(input);

        // Calculate importance (placeholder logic)
        double importance = calculateImportance(input);

        // Store memory in-memory
        MemoryEntry entry = new MemoryEntry(memoryId, input, embedding, importance);
        memoryStore.put(memoryId, entry);
        System.out.println("[HippocampusBrain] Storing in-memory: " + memoryId);

        totalMemoriesConsolidated++;
        successfulConsolidations++;

        return "Memory consolidated: ID=" + memoryId + ", importance=" + importance + " [in-memory]";
    }
    
    /**
     * Retrieve similar memories based on query
     * Uses in-memory vector similarity search
     */
    public List<MemoryEntry> retrieveSimilarMemories(String query, int topK) {
        double[] queryEmbedding = generatePlaceholderEmbedding(query);
        return inMemorySearch(queryEmbedding, topK);
    }
    
    /**
     * In-memory search fallback
     */
    private List<MemoryEntry> inMemorySearch(double[] queryEmbedding, int topK) {
        List<Map.Entry<String, Double>> similarities = new ArrayList<>();
        
        for (Map.Entry<String, MemoryEntry> entry : memoryStore.entrySet()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getValue().embedding);
            similarities.add(new AbstractMap.SimpleEntry<>(entry.getKey(), similarity));
        }
        
        // Sort by similarity (descending)
        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        // Return top K
        List<MemoryEntry> results = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, similarities.size()); i++) {
            results.add(memoryStore.get(similarities.get(i).getKey()));
        }
        
        return results;
    }
    
    /**
     * Generate placeholder embedding
     * In real usage, this would call EntorhinalCortex
     */
    private double[] generatePlaceholderEmbedding(String input) {
        double[] embedding = new double[EMBEDDING_DIMENSION];
        int seed = input.hashCode();
        Random random = new Random(seed);
        
        for (int i = 0; i < EMBEDDING_DIMENSION; i++) {
            embedding[i] = random.nextDouble() * 2 - 1;
        }
        
        // Normalize
        double norm = 0.0;
        for (double v : embedding) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        
        if (norm > 0) {
            for (int i = 0; i < EMBEDDING_DIMENSION; i++) {
                embedding[i] /= norm;
            }
        }
        
        return embedding;
    }
    
    /**
     * Calculate cosine similarity
     */
    private double cosineSimilarity(double[] embedding1, double[] embedding2) {
        if (embedding1.length != embedding2.length) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += embedding1[i] * embedding1[i];
            norm2 += embedding2[i] * embedding2[i];
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * Calculate importance score for memory
     */
    private double calculateImportance(String input) {
        // Placeholder importance calculation
        // In real usage, would consider recency, frequency, emotional salience
        double importance = 0.5;
        
        // Longer content = higher importance
        importance += Math.min(input.length() / 1000.0, 0.3);
        
        // Keywords increase importance
        String lower = input.toLowerCase();
        if (lower.contains("important") || lower.contains("critical")) {
            importance += 0.2;
        }
        
        return Math.min(importance, 1.0);
    }
    
    /**
     * Detect the type of consolidation needed
     */
    private String detectConsolidationType(String input) {
        String lower = input.toLowerCase();
        if (lower.contains("episodic") || lower.contains("event")) {
            return "episodic_consolidation";
        } else if (lower.contains("semantic") || lower.contains("concept")) {
            return "semantic_consolidation";
        } else if (lower.contains("spatial") || lower.contains("location")) {
            return "spatial_consolidation";
        } else {
            return "general_consolidation";
        }
    }
    
    /**
     * Get memory consolidation statistics
     */
    public Map<String, Object> getConsolidationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMemoriesConsolidated", totalMemoriesConsolidated);
        stats.put("successfulConsolidations", successfulConsolidations);
        stats.put("consolidationAccuracy", totalMemoriesConsolidated > 0 ?
            (double) successfulConsolidations / totalMemoriesConsolidated : 0.0);
        stats.put("consolidationTypeCounts", new HashMap<>(consolidationTypeCounts));
        stats.put("memoryStoreSize", memoryStore.size());
        stats.put("maxMemories", MAX_MEMORIES);
        stats.put("storageBackend", "in-memory");
        return stats;
    }
    
    /**
     * Reset memory consolidation statistics
     */
    public void resetStats() {
        totalMemoriesConsolidated = 0;
        successfulConsolidations = 0;
        consolidationTypeCounts.clear();
    }
    
    /**
     * Clear the memory store
     */
    public void clearMemoryStore() {
        memoryStore.clear();
    }
}
