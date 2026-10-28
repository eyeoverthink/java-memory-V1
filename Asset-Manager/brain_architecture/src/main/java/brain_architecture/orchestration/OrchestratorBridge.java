package brain_architecture.orchestration;

import brain_architecture.core.BackendEnsemble;
import brain_architecture.core.BackendInterface;
import brain_architecture.orchestration.discovery.BackendDiscovery;
import brain_architecture.orchestration.evolution.EvolutionEngine;
import brain_architecture.orchestration.healing.SelfHealing;
import brain_architecture.orchestration.organic.OrganicGrowth;
import brain_architecture.orchestration.synthesis.ResultSynthesizer;

import java.util.Map;

/**
 * OrchestratorBridge for wiring/unwiring orchestration features to BackendEnsemble.
 * 
 * This bridge enables the Hybrid Orchestrator components to be optionally integrated
 * with the BackendEnsemble using the wire/unwire pattern. This maintains backward
 * compatibility and allows the orchestration features to be disabled if not needed.
 */
public class OrchestratorBridge {
    
    private final BackendEnsemble backendEnsemble;
    private BackendDiscovery backendDiscovery;
    private SelfHealing selfHealing;
    private EvolutionEngine evolutionEngine;
    private OrganicGrowth organicGrowth;
    private ResultSynthesizer resultSynthesizer;
    
    private boolean discoveryWired = false;
    private boolean healingWired = false;
    private boolean evolutionWired = false;
    private boolean organicWired = false;
    private boolean synthesisWired = false;
    
    public OrchestratorBridge(BackendEnsemble backendEnsemble) {
        this.backendEnsemble = backendEnsemble;
    }
    
    /**
     * Wire backend discovery to BackendEnsemble
     * Enables automatic backend detection and dynamic registration
     */
    public void wireBackendDiscovery() {
        if (discoveryWired) return;
        
        backendDiscovery = new BackendDiscovery();
        
        // Discover available backends
        Map<BackendInterface.BackendType, BackendDiscovery.BackendStatus> discovered = 
            backendDiscovery.discoverBackends();
        
        // Register discovered backends with BackendEnsemble
        // (This is a simplified integration - in practice, you'd have backend instances)
        
        discoveryWired = true;
    }
    
    /**
     * Unwire backend discovery from BackendEnsemble
     */
    public void unwireBackendDiscovery() {
        if (!discoveryWired) return;
        
        if (backendDiscovery != null) {
            backendDiscovery.reset();
            backendDiscovery = null;
        }
        
        discoveryWired = false;
    }
    
    /**
     * Wire self-healing to BackendEnsemble
     * Enables circuit breaker patterns and automatic failure recovery
     */
    public void wireSelfHealing() {
        if (healingWired) return;
        
        selfHealing = new SelfHealing();
        
        // Self-healing will be called when recording results
        // Integration point: enhance BackendEnsemble.recordResult() to call selfHealing
        
        healingWired = true;
    }
    
    /**
     * Unwire self-healing from BackendEnsemble
     */
    public void unwireSelfHealing() {
        if (!healingWired) return;
        
        if (selfHealing != null) {
            selfHealing.reset();
            selfHealing = null;
        }
        
        healingWired = false;
    }
    
    /**
     * Wire evolution engine to BackendEnsemble
     * Enables adaptive routing optimization based on performance data
     */
    public void wireEvolutionEngine() {
        if (evolutionWired) return;
        
        evolutionEngine = new EvolutionEngine();
        
        // Evolution engine will track performance and recommend optimal routes
        // Integration point: enhance BackendEnsemble to call evolutionEngine.recordResult()
        
        evolutionWired = true;
    }
    
    /**
     * Unwire evolution engine from BackendEnsemble
     */
    public void unwireEvolutionEngine() {
        if (!evolutionWired) return;
        
        if (evolutionEngine != null) {
            evolutionEngine.reset();
            evolutionEngine = null;
        }
        
        evolutionWired = false;
    }
    
    /**
     * Wire organic growth to BackendEnsemble
     * Enables plugin architecture and runtime reconfiguration
     */
    public void wireOrganicGrowth() {
        if (organicWired) return;
        
        organicGrowth = new OrganicGrowth();
        
        // Organic growth enables dynamic routing rules and plugins
        // Integration point: BackendEnsemble can query organicGrowth for routing rules
        
        organicWired = true;
    }
    
    /**
     * Unwire organic growth from BackendEnsemble
     */
    public void unwireOrganicGrowth() {
        if (!organicWired) return;
        
        if (organicGrowth != null) {
            organicGrowth.reset();
            organicGrowth = null;
        }
        
        organicWired = false;
    }
    
    /**
     * Wire result synthesizer to BackendEnsemble
     * Enables ensemble processing and consensus building
     */
    public void wireResultSynthesizer() {
        if (synthesisWired) return;
        
        resultSynthesizer = new ResultSynthesizer();
        
        // Result synthesizer can combine results from multiple backends
        // Integration point: BackendEnsemble can call resultSynthesizer.synthesize()
        
        synthesisWired = true;
    }
    
    /**
     * Unwire result synthesizer from BackendEnsemble
     */
    public void unwireResultSynthesizer() {
        if (!synthesisWired) return;
        
        resultSynthesizer = null;
        synthesisWired = false;
    }
    
    /**
     * Wire all orchestration features
     */
    public void wireAll() {
        wireBackendDiscovery();
        wireSelfHealing();
        wireEvolutionEngine();
        wireOrganicGrowth();
        wireResultSynthesizer();
    }
    
    /**
     * Unwire all orchestration features
     */
    public void unwireAll() {
        unwireBackendDiscovery();
        unwireSelfHealing();
        unwireEvolutionEngine();
        unwireOrganicGrowth();
        unwireResultSynthesizer();
    }
    
    /**
     * Get orchestration status
     */
    public Map<String, Object> getStatus() {
        return Map.of(
            "discoveryWired", discoveryWired,
            "healingWired", healingWired,
            "evolutionWired", evolutionWired,
            "organicWired", organicWired,
            "synthesisWired", synthesisWired,
            "anyWired", discoveryWired || healingWired || evolutionWired || organicWired || synthesisWired
        );
    }
    
    // Accessors for orchestration components
    
    public BackendDiscovery getBackendDiscovery() {
        return backendDiscovery;
    }
    
    public SelfHealing getSelfHealing() {
        return selfHealing;
    }
    
    public EvolutionEngine getEvolutionEngine() {
        return evolutionEngine;
    }
    
    public OrganicGrowth getOrganicGrowth() {
        return organicGrowth;
    }
    
    public ResultSynthesizer getResultSynthesizer() {
        return resultSynthesizer;
    }
    
    public BackendEnsemble getBackendEnsemble() {
        return backendEnsemble;
    }
}
