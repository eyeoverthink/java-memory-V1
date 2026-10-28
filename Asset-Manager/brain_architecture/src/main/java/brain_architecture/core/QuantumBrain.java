package brain_architecture.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Base class for all quantum brains in the system.
 * Each brain has its own backend ensemble, routing, self-healing, and evolution capabilities.
 */
public abstract class QuantumBrain {
    protected final BrainType brainType;
    protected final BrainConfiguration config;
    
    // Backend ensemble (Hybrid AI Orchestrator integration)
    protected final BackendEnsemble backendEnsemble;
    
    // Localized routing
    protected BasicRouter router;
    protected EvolutionaryRouter evolutionaryRouter;
    
    // Self-healing
    protected SelfHealing selfHealing;
    
    // Organic growth
    protected OrganicGrowth organicGrowth;
    
    // Backend discovery
    protected BackendDiscovery backendDiscovery;
    
    // Performance tracking
    protected EvolutionEngine evolutionEngine;
    
    // Result synthesis
    protected ResultSynthesizer resultSynthesizer;
    
    // Phi-harmonic resonance (inter-brain communication)
    protected PhiResonanceMatrix phiResonance;
    
    /**
     * Initialize a quantum brain with its configuration
     */
    public QuantumBrain(BrainType brainType, BrainConfiguration config) {
        this.brainType = brainType;
        this.config = config;
        this.backendEnsemble = new BackendEnsemble();
        this.phiResonance = new PhiResonanceMatrix();
        
        // Initialize components
        initializeComponents();
    }
    
    /**
     * Initialize all brain components
     */
    protected void initializeComponents() {
        // Initialize routing
        this.router = new BasicRouter(backendEnsemble);
        this.evolutionaryRouter = new EvolutionaryRouter(backendEnsemble, evolutionEngine);
        
        // Initialize self-healing
        this.selfHealing = new SelfHealing(backendEnsemble, config);
        
        // Initialize organic growth
        this.organicGrowth = new OrganicGrowth();
        
        // Initialize backend discovery
        this.backendDiscovery = new BackendDiscovery(backendEnsemble);
        
        // Initialize evolution engine
        this.evolutionEngine = new EvolutionEngine(config);
        
        // Initialize result synthesizer
        this.resultSynthesizer = new ResultSynthesizer();
    }
    
    /**
     * Register backends for this brain
     * Subclasses should override to configure specific backends
     */
    public abstract void registerBackends();
    
    /**
     * Process a query through this brain
     */
    public CompletableFuture<BrainResponse> processQuery(String query, Map<String, Object> parameters) {
        // Analyze query
        QueryAnalyzer analyzer = new QueryAnalyzer();
        QueryAnalyzer.AnalysisResult analysis = analyzer.analyze(query);
        
        // Route query through evolutionary router
        CompletableFuture<BackendInterface.QueryResult> resultFuture = 
            evolutionaryRouter.route(query, analysis.getTaskType(), parameters);
        
        return resultFuture.thenApply(result -> {
            // Record result for evolution
            for (BackendInterface.BackendType backendType : backendEnsemble.getBackends().stream()
                .map(BackendInterface::getBackendType).toList()) {
                evolutionEngine.recordResult(backendType, analysis.getTaskType(), result);
            }
            
            // Return brain response
            return new BrainResponse(
                brainType,
                result.getContent(),
                result.getConfidence(),
                result.getLatencyMs(),
                result.isSuccess(),
                result.getMetadata()
            );
        });
    }
    
    /**
     * Send a message to another brain via phi-resonance
     */
    public CompletableFuture<BrainResponse> sendToBrain(BrainType targetBrain, String message) {
        return phiResonance.sendToBrain(brainType, targetBrain, message);
    }
    
    /**
     * Receive a message from another brain
     */
    public void receiveFromBrain(BrainType sourceBrain, String message) {
        phiResonance.receiveFromBrain(sourceBrain, brainType, message);
    }
    
    /**
     * Get brain type
     */
    public BrainType getBrainType() {
        return brainType;
    }
    
    /**
     * Get backend ensemble
     */
    public BackendEnsemble getBackendEnsemble() {
        return backendEnsemble;
    }
    
    /**
     * Get configuration
     */
    public BrainConfiguration getConfig() {
        return config;
    }
    
    /**
     * Get evolution statistics
     */
    public Map<String, Object> getEvolutionStatistics() {
        return evolutionEngine.getStatistics();
    }
    
    /**
     * Get self-healing statistics
     */
    public Map<String, Object> getSelfHealingStatistics() {
        return selfHealing.getStatistics();
    }
    
    /**
     * Shutdown the brain
     */
    public void shutdown() {
        // Shutdown all backends
        for (BackendInterface backend : backendEnsemble.getBackends()) {
            backend.shutdown();
        }
        
        // Shutdown components
        backendDiscovery.shutdown();
        organicGrowth.shutdown();
    }
    
    /**
     * Brain response wrapper
     */
    public static class BrainResponse {
        private final BrainType brainType;
        private final String content;
        private final double confidence;
        private final long latencyMs;
        private final boolean success;
        private final Map<String, Object> metadata;
        
        public BrainResponse(BrainType brainType, String content, double confidence,
                           long latencyMs, boolean success, Map<String, Object> metadata) {
            this.brainType = brainType;
            this.content = content;
            this.confidence = confidence;
            this.latencyMs = latencyMs;
            this.success = success;
            this.metadata = metadata;
        }
        
        public BrainType getBrainType() {
            return brainType;
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
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }
}
