package brain_architecture.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Unified interface for all AI backends in the brain architecture.
 * Each backend adapter implements this interface to provide consistent access.
 */
public interface BackendInterface {
    
    /**
     * Backend types supported by the system
     */
    enum BackendType {
        OPENCLAW("openclaw", "Custom TypeScript/JavaScript agent system", true),
        NEOCLAW("neoClaw", "Enhanced openclaw evolution", true),
        OBSIDIAN("obsidian", "Knowledge management system", true),
        OLLAMA("ollama", "Local LLM infrastructure", true),
        GEMMA3("gemma3", "Google's LLM", false),
        CLAUDE("claude", "Anthropic's model", false),
        DEEPSEEK("deepseek", "DeepSeek v3.2 - Mixture-of-Experts reasoning model", true),
        KIMI("kimi", "Kimi K2.5 - Multimodal coding and agent swarms", false),
        GEMMA4("gemma4", "Gemma 4 - Top-tier reasoning on single GPU", false),
        LTXVIDEO("ltxvideo", "Real-time video generation", true),
        SPEECHBRAIN("speechbrain", "Speech and audio processing", true),
        LAZARUS("lazarus", "Self-evolving biological engine", true),
        AKASHICRECORD("akashicrecord", "Persistent cross-modal knowledge", true),
        FISH_AUDIO_S2("fish_audio_s2", "Fish Audio S2 - Emotion-controlled TTS and audio synthesis", true);
        
        private final String id;
        private final String description;
        private final boolean isOpenSource;
        
        BackendType(String id, String description, boolean isOpenSource) {
            this.id = id;
            this.description = description;
            this.isOpenSource = isOpenSource;
        }
        
        public String getId() {
            return id;
        }
        
        public String getDescription() {
            return description;
        }
        
        public boolean isOpenSource() {
            return isOpenSource;
        }
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
        
        public String getId() {
            return id;
        }
        
        public String getDescription() {
            return description;
        }
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
        
        public String getContent() {
            return content;
        }
        
        public double getConfidence() {
            return confidence;
        }
        
        public long getLatencyMs() {
            return latencyMs;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }
    
    /**
     * Backend capabilities metadata
     */
    class BackendCapabilities {
        private final boolean supportsReasoning;
        private final boolean supportsGeneration;
        private final boolean supportsKnowledge;
        private final boolean supportsFastInference;
        private final boolean supportsComplexAnalysis;
        private final double maxLatencyMs;
        private final double minConfidence;
        
        public BackendCapabilities(boolean supportsReasoning, boolean supportsGeneration,
                                   boolean supportsKnowledge, boolean supportsFastInference,
                                   boolean supportsComplexAnalysis, double maxLatencyMs,
                                   double minConfidence) {
            this.supportsReasoning = supportsReasoning;
            this.supportsGeneration = supportsGeneration;
            this.supportsKnowledge = supportsKnowledge;
            this.supportsFastInference = supportsFastInference;
            this.supportsComplexAnalysis = supportsComplexAnalysis;
            this.maxLatencyMs = maxLatencyMs;
            this.minConfidence = minConfidence;
        }
        
        public boolean supportsReasoning() {
            return supportsReasoning;
        }
        
        public boolean supportsGeneration() {
            return supportsGeneration;
        }
        
        public boolean supportsKnowledge() {
            return supportsKnowledge;
        }
        
        public boolean supportsFastInference() {
            return supportsFastInference;
        }
        
        public boolean supportsComplexAnalysis() {
            return supportsComplexAnalysis;
        }
        
        public double getMaxLatencyMs() {
            return maxLatencyMs;
        }
        
        public double getMinConfidence() {
            return minConfidence;
        }
        
        public double getReasoningScore() {
            return supportsReasoning ? 1.0 : 0.0;
        }
        
        public double getGenerationScore() {
            return supportsGeneration ? 1.0 : 0.0;
        }
        
        public double getKnowledgeScore() {
            return supportsKnowledge ? 1.0 : 0.0;
        }
        
        public double getFastInferenceScore() {
            return supportsFastInference ? 1.0 : 0.0;
        }
        
        public double getComplexAnalysisScore() {
            return supportsComplexAnalysis ? 1.0 : 0.0;
        }
    }
    
    /**
     * Get the backend type
     */
    BackendType getBackendType();
    
    /**
     * Get backend capabilities
     */
    BackendCapabilities getCapabilities();
    
    /**
     * Initialize the backend
     */
    void initialize() throws Exception;
    
    /**
     * Shutdown the backend
     */
    void shutdown();
    
    /**
     * Check if backend is healthy
     */
    boolean isHealthy();
    
    /**
     * Execute a query asynchronously
     */
    CompletableFuture<QueryResult> executeQuery(String query, TaskType taskType, Map<String, Object> parameters);
}
