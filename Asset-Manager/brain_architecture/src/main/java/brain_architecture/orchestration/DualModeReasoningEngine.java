package brain_architecture.orchestration;

import brain_architecture.core.BackendInterface;
import brain_architecture.backends.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Dual-Mode Reasoning Engine
 * 
 * Implements System 1 (Instant) vs System 2 (Thinking) reasoning modes
 * based on cognitive science dual-process theory.
 * 
 * System 1 (Instant): Fast, automatic, intuitive, low-effort processing
 * - Quick responses
 * - Pattern recognition
 * - Heuristic-based decisions
 * - Low latency (<100ms target)
 * 
 * System 2 (Thinking): Slow, deliberate, analytical, high-effort processing
 * - Chain-of-thought reasoning
 * - Step-by-step analysis
 * - Counterfactual simulation
 * - Higher accuracy (accepts longer latency)
 */
public class DualModeReasoningEngine {
    
    public enum ReasoningMode {
        INSTANT("instant", "System 1 - Fast, automatic, intuitive"),
        THINKING("thinking", "System 2 - Slow, deliberate, analytical");
        
        private final String id;
        private final String description;
        
        ReasoningMode(String id, String description) {
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
    
    private ReasoningMode currentMode = ReasoningMode.INSTANT;
    private BackendInterface backend;
    private boolean autoSwitchEnabled = false;
    
    // Statistics
    private int instantQueries = 0;
    private int thinkingQueries = 0;
    private long totalInstantLatency = 0;
    private long totalThinkingLatency = 0;
    
    public DualModeReasoningEngine() {
        this(new OllamaBackend()); // Default backend
    }
    
    public DualModeReasoningEngine(BackendInterface backend) {
        this.backend = backend;
    }
    
    /**
     * Set reasoning mode
     */
    public void setMode(ReasoningMode mode) {
        this.currentMode = mode;
    }
    
    /**
     * Get current reasoning mode
     */
    public ReasoningMode getMode() {
        return currentMode;
    }
    
    /**
     * Enable/disable automatic mode switching based on query complexity
     */
    public void setAutoSwitch(boolean enabled) {
        this.autoSwitchEnabled = enabled;
    }
    
    /**
     * Set backend for reasoning
     */
    public void setBackend(BackendInterface backend) {
        this.backend = backend;
    }
    
    /**
     * Execute query with current reasoning mode
     */
    public CompletableFuture<BackendInterface.QueryResult> query(String query, BackendInterface.TaskType taskType) {
        return query(query, taskType, currentMode);
    }
    
    /**
     * Execute query with specified reasoning mode
     */
    public CompletableFuture<BackendInterface.QueryResult> query(String query, BackendInterface.TaskType taskType, ReasoningMode mode) {
        // Auto-switch based on query complexity if enabled
        if (autoSwitchEnabled && mode == ReasoningMode.INSTANT) {
            double complexity = analyzeComplexity(query);
            if (complexity > 0.7) {
                mode = ReasoningMode.THINKING;
            }
        }
        
        // Add reasoning mode to parameters
        Map<String, Object> parameters = Map.of("reasoning_mode", mode.getId());
        
        long startTime = System.currentTimeMillis();
        final ReasoningMode currentMode = mode; // Capture for lambda
        
        CompletableFuture<BackendInterface.QueryResult> future = backend.executeQuery(query, taskType, parameters);
        
        return future.thenApply(result -> {
            long latency = System.currentTimeMillis() - startTime;
            
            // Update statistics
            if (currentMode == ReasoningMode.INSTANT) {
                instantQueries++;
                totalInstantLatency += latency;
            } else {
                thinkingQueries++;
                totalThinkingLatency += latency;
            }
            
            return result;
        });
    }
    
    /**
     * Analyze query complexity (0.0 to 1.0)
     * Factors: length, question words, logical operators, mathematical expressions
     */
    double analyzeComplexity(String query) {
        if (query == null || query.isEmpty()) {
            return 0.0;
        }
        
        double complexity = 0.0;
        
        // Length factor (queries > 200 chars are complex)
        complexity += Math.min(query.length() / 200.0, 0.3);
        
        // Question words indicating reasoning
        String[] reasoningWords = {"why", "how", "explain", "analyze", "compare", "evaluate", "justify", "prove"};
        String lowerQuery = query.toLowerCase();
        for (String word : reasoningWords) {
            if (lowerQuery.contains(word)) {
                complexity += 0.15;
            }
        }
        
        // Logical operators
        String[] logicalOps = {"therefore", "because", "if", "then", "implies", "since", "consequently"};
        for (String op : logicalOps) {
            if (lowerQuery.contains(op)) {
                complexity += 0.1;
            }
        }
        
        // Mathematical expressions
        if (query.matches(".*[+\\-*/=<>].*")) {
            complexity += 0.2;
        }
        
        // Multiple sentences
        int sentenceCount = query.split("[.!?]+").length;
        if (sentenceCount > 2) {
            complexity += 0.1;
        }
        
        return Math.min(complexity, 1.0);
    }
    
    /**
     * Get statistics
     */
    public Map<String, Object> getStatistics() {
        double avgInstantLatency = instantQueries > 0 ? (double) totalInstantLatency / instantQueries : 0;
        double avgThinkingLatency = thinkingQueries > 0 ? (double) totalThinkingLatency / thinkingQueries : 0;
        
        return Map.of(
            "currentMode", currentMode.getId(),
            "autoSwitchEnabled", autoSwitchEnabled,
            "instantQueries", instantQueries,
            "thinkingQueries", thinkingQueries,
            "avgInstantLatencyMs", avgInstantLatency,
            "avgThinkingLatencyMs", avgThinkingLatency
        );
    }
    
    /**
     * Reset statistics
     */
    public void resetStatistics() {
        instantQueries = 0;
        thinkingQueries = 0;
        totalInstantLatency = 0;
        totalThinkingLatency = 0;
    }
}
