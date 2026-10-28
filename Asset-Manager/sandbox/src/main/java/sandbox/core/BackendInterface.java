package sandbox.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Unified interface for all AI backends in the Hybrid Orchestrator.
 * 
 * This interface defines the contract that all backend adapters must implement,
 * enabling the orchestrator to treat different AI systems (openclaw, ollama, 
 * claude, gemma3, obsidian, neoClaw) uniformly while respecting their unique
 * capabilities and limitations.
 */
public interface BackendInterface {
    
    /**
     * Backend types supported by the orchestrator
     */
    enum BackendType {
        OPENCLAW("openclaw", "Custom TypeScript/JavaScript agent system"),
        NEOCLAW("neoClaw", "Enhanced openclaw evolution"),
        OBSIDIAN("obsidian", "Knowledge management system"),
        OLLAMA("ollama", "Local LLM infrastructure"),
        GEMMA3("gemma3", "Google's LLM"),
        CLAUDE("claude", "Anthropic's model (offline)");
        
        private final String id;
        private final String description;
        
        BackendType(String id, String description) {
            this.id = id;
            this.description = description;
        }
        
        public String getId() { return id; }
        public String getDescription() { return description; }
    }
    
    /**
     * Task types for routing decisions
     */
    enum TaskType {
        REASONING("reasoning", "Complex logical analysis and chain-of-thought"),
        GENERATION("generation", "Creative content generation"),
        KNOWLEDGE("knowledge", "Fact retrieval and information lookup"),
        FAST_INFERENCE("fast_inference", "Real-time low-latency processing"),
        COMPLEX_ANALYSIS("complex_analysis", "Multi-step deep reasoning");
        
        private final String id;
        private final String description;
        
        TaskType(String id, String description) {
            this.id = id;
            this.description = description;
        }
        
        public String getId() { return id; }
        public String getDescription() { return description; }
    }
    
    /**
     * Query result from a backend
     */
    class QueryResult {
        private final String content;
        private final double confidence;
        private final long latencyMs;
        private final boolean success;
        private final String errorMessage;
        private final Map<String, Object> metadata;
        
        public QueryResult(String content, double confidence, long latencyMs, 
                          boolean success, String errorMessage, Map<String, Object> metadata) {
            this.content = content;
            this.confidence = confidence;
            this.latencyMs = latencyMs;
            this.success = success;
            this.errorMessage = errorMessage;
            this.metadata = metadata;
        }
        
        public String getContent() { return content; }
        public double getConfidence() { return confidence; }
        public long getLatencyMs() { return latencyMs; }
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public Map<String, Object> getMetadata() { return metadata; }
    }
    
    /**
     * Backend capabilities metadata
     */
    class BackendCapabilities {
        private final BackendType type;
        private final boolean supportsReasoning;
        private final boolean supportsGeneration;
        private final boolean supportsKnowledge;
        private final boolean isLocal;
        private final boolean isCreative;
        private final double speedScore; // 0.0 (slow) to 1.0 (fast)
        private final double accuracyScore; // 0.0 (low) to 1.0 (high)
        
        public BackendCapabilities(BackendType type, boolean supportsReasoning, 
                                   boolean supportsGeneration, boolean supportsKnowledge,
                                   boolean isLocal, boolean isCreative, 
                                   double speedScore, double accuracyScore) {
            this.type = type;
            this.supportsReasoning = supportsReasoning;
            this.supportsGeneration = supportsGeneration;
            this.supportsKnowledge = supportsKnowledge;
            this.isLocal = isLocal;
            this.isCreative = isCreative;
            this.speedScore = speedScore;
            this.accuracyScore = accuracyScore;
        }
        
        public BackendType getType() { return type; }
        public boolean supportsReasoning() { return supportsReasoning; }
        public boolean supportsGeneration() { return supportsGeneration; }
        public boolean supportsKnowledge() { return supportsKnowledge; }
        public boolean isLocal() { return isLocal; }
        public boolean isCreative() { return isCreative; }
        public double getSpeedScore() { return speedScore; }
        public double getAccuracyScore() { return accuracyScore; }
    }
    
    /**
     * Execute a query on this backend
     * 
     * @param query The query to execute
     * @param taskType The type of task (for routing context)
     * @param parameters Additional parameters for the backend
     * @return CompletableFuture containing the query result
     */
    CompletableFuture<QueryResult> executeQuery(String query, TaskType taskType, Map<String, Object> parameters);
    
    /**
     * Get the backend type
     * 
     * @return The backend type
     */
    BackendType getBackendType();
    
    /**
     * Get the backend capabilities
     * 
     * @return The backend capabilities
     */
    BackendCapabilities getCapabilities();
    
    /**
     * Check if the backend is healthy and available
     * 
     * @return true if healthy and available, false otherwise
     */
    boolean isHealthy();
    
    /**
     * Get the current health status details
     * 
     * @return Map containing health status information
     */
    Map<String, Object> getHealthStatus();
    
    /**
     * Initialize the backend (called once during orchestrator startup)
     * 
     * @throws Exception if initialization fails
     */
    void initialize() throws Exception;
    
    /**
     * Shutdown the backend (called once during orchestrator shutdown)
     */
    void shutdown();
}
