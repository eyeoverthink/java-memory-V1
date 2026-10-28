package fraymus.brain.unified;

import fraymus.causal.CausalModel;
import fraymus.causal.DAGVerifier;
import fraymus.causal.DoCalculusEngine;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Multi-layer memory system for the unified brain system.
 * Stores query-response pairs with confidence and consciousness scores.
 * 
 * Phase 1.2: Enhanced with DAG-guided do-calculus for causal reasoning
 */
public class MemorySystem {
    private final Map<String, MemoryEntry> episodicMemory;
    private final Map<String, double[]> semanticMemory;
    private final HDCEngine hdcEngine;
    private final CausalModel causalModel;
    private final DAGVerifier causalVerifier;
    private final DoCalculusEngine doCalculus;
    private long totalStored;
    private long totalRetrieved;

    public MemorySystem() {
        this.episodicMemory = new ConcurrentHashMap<>();
        this.semanticMemory = new ConcurrentHashMap<>();
        this.hdcEngine = new HDCEngine();
        this.causalModel = new CausalModel();
        this.causalVerifier = new DAGVerifier(causalModel);
        this.doCalculus = new DoCalculusEngine(causalModel);
        this.totalStored = 0;
        this.totalRetrieved = 0;
    }

    /**
     * Store a query-response pair in memory.
     */
    public void store(BrainQuery query, String response, double confidence, double consciousness) {
        String queryKey = query.getQuery();
        
        // Store in episodic memory (timestamped events)
        MemoryEntry entry = new MemoryEntry(
            response,
            confidence,
            consciousness,
            System.currentTimeMillis(),
            query.getRequiredCapabilities()
        );
        episodicMemory.put(queryKey, entry);
        
        // Store in semantic memory (vectorized for similarity search)
        double[] vector = hdcEngine.vectorize(response);
        semanticMemory.put(queryKey, vector);
        
        totalStored++;
    }

    /**
     * Retrieve a response from episodic memory.
     */
    public MemoryEntry retrieve(String query) {
        totalRetrieved++;
        return episodicMemory.get(query);
    }

    /**
     * Search for similar responses using semantic memory.
     * Enhanced with causal verification to filter out spurious correlations.
     */
    public Map<String, Double> searchSimilar(String query, int topK) {
        double[] queryVector = hdcEngine.vectorize(query);
        
        Map<String, Double> similarities = new HashMap<>();
        for (Map.Entry<String, double[]> entry : semanticMemory.entrySet()) {
            double similarity = hdcEngine.similarity(queryVector, entry.getValue());
            if (similarity > 0.5) { // Threshold for similarity
                // Check for spurious correlations using causal model
                if (causalModel.getNode(query) != null && causalModel.getNode(entry.getKey()) != null) {
                    // If both concepts exist in causal model, verify no spurious correlation
                    boolean hasCausalPath = causalVerifier.verifyCausalPath(query, entry.getKey()) ||
                                          causalVerifier.verifyCausalPath(entry.getKey(), query);
                    if (!hasCausalPath) {
                        // No causal path - might be spurious, reduce similarity
                        similarity *= 0.5; // Penalty for potential spurious correlation
                    }
                }
                similarities.put(entry.getKey(), similarity);
            }
        }
        
        // Return top K
        return similarities.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }
    
    /**
     * Add a causal relationship between two concepts.
     * This enhances semantic memory with causal structure.
     */
    public void addCausalRelationship(String cause, String effect) {
        // Create nodes if they don't exist
        if (causalModel.getNode(cause) == null) {
            causalModel.addNode(cause, cause);
        }
        if (causalModel.getNode(effect) == null) {
            causalModel.addNode(effect, effect);
        }
        
        // Add causal edge
        causalModel.addEdge(cause, effect);
    }
    
    /**
     * Get the causal model for advanced causal reasoning operations.
     */
    public CausalModel getCausalModel() {
        return causalModel;
    }
    
    /**
     * Get the causal verifier for checking causal relationships.
     */
    public DAGVerifier getCausalVerifier() {
        return causalVerifier;
    }
    
    /**
     * Get the do-calculus engine for causal inference.
     */
    public DoCalculusEngine getDoCalculus() {
        return doCalculus;
    }

    /**
     * Get memory statistics.
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("episodicMemorySize", episodicMemory.size());
        stats.put("semanticMemorySize", semanticMemory.size());
        stats.put("totalStored", totalStored);
        stats.put("totalRetrieved", totalRetrieved);
        stats.put("causalNodes", causalModel.getNodes().size());
        stats.put("causalEdges", causalModel.getNodes().values().stream()
            .mapToInt(node -> node.getChildren().size()).sum());
        return stats;
    }

    /**
     * Clear all memory.
     */
    public void clear() {
        episodicMemory.clear();
        semanticMemory.clear();
        causalModel.clear();
    }

    /**
     * Memory entry class.
     */
    public static class MemoryEntry {
        private final String response;
        private final double confidence;
        private final double consciousness;
        private final long timestamp;
        private final java.util.List<BrainCapability> capabilities;

        public MemoryEntry(String response, double confidence, double consciousness, 
                          long timestamp, java.util.List<BrainCapability> capabilities) {
            this.response = response;
            this.confidence = confidence;
            this.consciousness = consciousness;
            this.timestamp = timestamp;
            this.capabilities = capabilities;
        }

        public String getResponse() {
            return response;
        }

        public double getConfidence() {
            return confidence;
        }

        public double getConsciousness() {
            return consciousness;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public java.util.List<BrainCapability> getCapabilities() {
            return capabilities;
        }
    }
}
