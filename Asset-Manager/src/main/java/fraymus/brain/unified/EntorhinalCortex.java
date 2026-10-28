package fraymus.brain.unified;

import fraymus.core.OllamaBridge;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Entorhinal Cortex Brain - Embedding Generation
 * 
 * Purpose: Generate high-dimensional embeddings for memory encoding
 * Model: nomic-embed-text-v1.5 (384-dim embeddings)
 * Latency: ~50ms | Purpose: Embedding generation
 * 
 * The entorhinal cortex is the gateway to the hippocampus and plays a crucial
 * role in memory formation and spatial navigation. This component generates
 * high-dimensional embeddings for semantic encoding of memories.
 * 
 * NOTE: This is a Java implementation with Ollama integration.
 * For full embedding capabilities, you would need to use nomic-embed-text-v1.5
 * or similar embedding models via Ollama.
 */
public class EntorhinalCortex extends AbstractBrainAdapter {
    private final AtomicBoolean initialized;
    private OllamaBridge ollamaBridge;
    private final OllamaBridge sharedOllamaBridge;
    
    // Embedding generation state tracking
    private int totalEmbeddingsGenerated = 0;
    private int successfulGenerations = 0;
    private Map<String, Integer> embeddingTypeCounts = new HashMap<>();
    
    // Embedding cache for repeated queries
    private final Map<String, double[]> embeddingCache;
    private static final int EMBEDDING_DIMENSION = 384; // nomic-embed-text-v1.5
    
    public EntorhinalCortex() {
        this(null);
    }
    
    public EntorhinalCortex(OllamaBridge sharedOllamaBridge) {
        super("EntorhinalCortex", "Memory", Arrays.asList(
            BrainCapability.MEMORY
        ));
        this.initialized = new AtomicBoolean(false);
        this.sharedOllamaBridge = sharedOllamaBridge;
        this.embeddingCache = new HashMap<>();
    }
    
    @Override
    protected void onWire() {
        if (initialized.compareAndSet(false, true)) {
            ollamaBridge = sharedOllamaBridge != null ? sharedOllamaBridge : new OllamaBridge("nomic-embed-text");
        }
    }
    
    @Override
    protected void onUnwire() {
        initialized.set(false);
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!initialized.get()) {
            return BrainResponse.error(getName(), "EntorhinalCortex not wired");
        }
        
        long startTime = System.currentTimeMillis();
        totalEmbeddingsGenerated++;
        
        try {
            double[] embedding = generateEmbedding(query.getQuery());
            successfulGenerations++;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("embeddingType", detectEmbeddingType(query.getQuery()));
            metadata.put("embeddingDimension", EMBEDDING_DIMENSION);
            metadata.put("processingTimeMs", System.currentTimeMillis() - startTime);
            
            // Convert embedding to string for response
            String embeddingString = Arrays.toString(embedding);
            
            return BrainResponse.success(
                getName(),
                embeddingString,
                0.9,
                0.85,
                metadata,
                0
            );
        } catch (Exception e) {
            return BrainResponse.error(getName(), "Embedding generation failed: " + e.getMessage());
        }
    }
    
    /**
     * Generate embedding for text input
     * In a real implementation, this would:
     * 1. Call nomic-embed-text-v1.5 via Ollama
     * 2. Get 384-dimensional embedding vector
     * 3. Cache the embedding for repeated queries
     * 
     * For now, this is a placeholder that generates pseudo-random embeddings.
     */
    public double[] generateEmbedding(String input) {
        String embeddingType = detectEmbeddingType(input);
        embeddingTypeCounts.put(embeddingType, embeddingTypeCounts.getOrDefault(embeddingType, 0) + 1);
        
        // Check cache
        double[] cached = embeddingCache.get(input);
        if (cached != null) {
            return cached;
        }
        
        // Placeholder implementation - generate deterministic pseudo-random embedding
        // In real usage, this would call nomic-embed-text-v1.5
        double[] embedding = new double[EMBEDDING_DIMENSION];
        int seed = input.hashCode();
        Random random = new Random(seed);
        
        for (int i = 0; i < EMBEDDING_DIMENSION; i++) {
            embedding[i] = random.nextDouble() * 2 - 1; // Range [-1, 1]
        }
        
        // Normalize embedding
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
        
        // Cache the embedding
        embeddingCache.put(input, embedding);
        
        return embedding;
    }
    
    /**
     * Detect the type of embedding generation needed
     */
    private String detectEmbeddingType(String input) {
        String lower = input.toLowerCase();
        if (lower.contains("image") || lower.contains("visual")) {
            return "visual_embedding";
        } else if (lower.contains("audio") || lower.contains("sound")) {
            return "audio_embedding";
        } else if (lower.contains("spatial") || lower.contains("location")) {
            return "spatial_embedding";
        } else {
            return "text_embedding";
        }
    }
    
    /**
     * Calculate cosine similarity between two embeddings
     */
    public double cosineSimilarity(double[] embedding1, double[] embedding2) {
        if (embedding1.length != embedding2.length) {
            throw new IllegalArgumentException("Embedding dimensions must match");
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
     * Get embedding generation statistics
     */
    public Map<String, Object> getEmbeddingStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmbeddingsGenerated", totalEmbeddingsGenerated);
        stats.put("successfulGenerations", successfulGenerations);
        stats.put("generationAccuracy", totalEmbeddingsGenerated > 0 ? 
            (double) successfulGenerations / totalEmbeddingsGenerated : 0.0);
        stats.put("embeddingTypeCounts", new HashMap<>(embeddingTypeCounts));
        stats.put("cacheSize", embeddingCache.size());
        stats.put("embeddingDimension", EMBEDDING_DIMENSION);
        return stats;
    }
    
    /**
     * Reset embedding generation statistics
     */
    public void resetStats() {
        totalEmbeddingsGenerated = 0;
        successfulGenerations = 0;
        embeddingTypeCounts.clear();
    }
    
    /**
     * Clear the embedding cache
     */
    public void clearCache() {
        embeddingCache.clear();
    }
}
