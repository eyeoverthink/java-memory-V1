package brain_architecture.integration;

import brain_architecture.core.BrainType;
import java.util.*;
import java.util.concurrent.*;

/**
 * BrainTypeCoordinator - Coordinates between 12 brain types and existing Fraymus components
 * 
 * This coordinator maps brain_architecture's 12 specialized brain types to the existing
 * Fraymus Universal system components, avoiding duplication and leveraging the existing
 * infrastructure.
 * 
 * Mapping:
 * - Physical Brain → Fraymus Layer 1 (Node/HyperTesseract)
 * - Quantum Brain → Fraymus Layer 6 (HyperFormer)
 * - Fractal Brain → Fraymus Layer 6 (HyperFormer)
 * - Creative Brain → Fraymus Layer 12 (GodMode)
 * - Logical Brain → SentientCore (CausalGraph)
 * - Emotional Brain → SentientCore (FisherOracle)
 * - Spiritual Brain → Fraymus Layer 8 (HoloGraph)
 * - Tachyonic Brain → Fraymus Layer 13 (Phi-Harmonic)
 * - Visual Brain → Fraymus Layer 1 (FrayFS)
 * - Auditory Brain → Fraymus Layer 1 (FrayFS)
 * - Evolutionary Brain → Fraymus Layer 2 (Digital Genetics)
 * - Memory Brain → Fraymus Layer 0 (AkashicRecord/Hippocampus)
 */
public class BrainTypeCoordinator {
    
    private final Map<BrainType, Object> fraymusComponents = new ConcurrentHashMap<>();
    private final Map<BrainType, Object> sentientCoreComponents = new ConcurrentHashMap<>();
    private final Map<BrainType, Boolean> wiredStatus = new ConcurrentHashMap<>();
    
    // Fraymus component interfaces (to be injected)
    private Object ollamaSpine;           // Layer 0
    private Object hyperFormer;          // Layer 6
    private Object holoGraph;            // Layer 8
    private Object godMode;              // Layer 12
    private Object phiHarmonicKernel;    // Layer 13
    private Object fraynixOS;            // Layer 1
    private Object akashicRecord;        // Layer 0
    
    // SentientCore components (to be injected)
    private Object causalGraph;          // SentientCore
    private Object sufficientStats;      // SentientCore
    private Object fisherOracle;         // SentientCore
    private Object analogyEngine;        // SentientCore
    private Object conceptLattice;       // SentientCore
    
    private boolean initialized = false;
    
    /**
     * Wire Fraymus components to the coordinator
     */
    public void wireFraymusComponents(
            Object ollamaSpine,
            Object hyperFormer,
            Object holoGraph,
            Object godMode,
            Object phiHarmonicKernel,
            Object fraynixOS,
            Object akashicRecord) {
        
        this.ollamaSpine = ollamaSpine;
        this.hyperFormer = hyperFormer;
        this.holoGraph = holoGraph;
        this.godMode = godMode;
        this.phiHarmonicKernel = phiHarmonicKernel;
        this.fraynixOS = fraynixOS;
        this.akashicRecord = akashicRecord;
        
        this.initialized = true;
        
        // Map brain types to Fraymus components
        mapFraymusComponents();
    }
    
    /**
     * Wire SentientCore components to the coordinator
     */
    public void wireSentientCore(
            Object causalGraph,
            Object sufficientStats,
            Object fisherOracle,
            Object analogyEngine,
            Object conceptLattice) {
        
        this.causalGraph = causalGraph;
        this.sufficientStats = sufficientStats;
        this.fisherOracle = fisherOracle;
        this.analogyEngine = analogyEngine;
        this.conceptLattice = conceptLattice;
        
        // Map brain types to SentientCore components
        mapSentientCoreComponents();
    }
    
    /**
     * Unwire Fraymus components (revert to standalone mode)
     */
    public void unwireFraymusComponents() {
        this.ollamaSpine = null;
        this.hyperFormer = null;
        this.holoGraph = null;
        this.godMode = null;
        this.phiHarmonicKernel = null;
        this.fraynixOS = null;
        this.akashicRecord = null;
        
        fraymusComponents.clear();
        wiredStatus.clear();
        
        this.initialized = false;
    }
    
    /**
     * Unwire SentientCore components (revert to standalone mode)
     */
    public void unwireSentientCore() {
        this.causalGraph = null;
        this.sufficientStats = null;
        this.fisherOracle = null;
        this.analogyEngine = null;
        this.conceptLattice = null;
        
        sentientCoreComponents.clear();
    }
    
    /**
     * Map brain types to Fraymus components
     */
    private void mapFraymusComponents() {
        if (!initialized) return;
        
        // Physical Brain → Fraymus Layer 1 (Node/HyperTesseract)
        fraymusComponents.put(BrainType.PHYSICAL, fraynixOS);
        
        // Quantum Brain → Fraymus Layer 6 (HyperFormer)
        fraymusComponents.put(BrainType.QUANTUM, hyperFormer);
        
        // Fractal Brain → Fraymus Layer 6 (HyperFormer)
        fraymusComponents.put(BrainType.FRACTAL, hyperFormer);
        
        // Creative Brain → Fraymus Layer 12 (GodMode)
        fraymusComponents.put(BrainType.CREATIVE, godMode);
        
        // Spiritual Brain → Fraymus Layer 8 (HoloGraph)
        fraymusComponents.put(BrainType.SPIRITUAL, holoGraph);
        
        // Tachyonic Brain → Fraymus Layer 13 (Phi-Harmonic)
        fraymusComponents.put(BrainType.TACHYONIC, phiHarmonicKernel);
        
        // Visual Brain → Fraymus Layer 1 (FrayFS)
        fraymusComponents.put(BrainType.VISUAL, fraynixOS);
        
        // Auditory Brain → Fraymus Layer 1 (FrayFS)
        fraymusComponents.put(BrainType.AUDITORY, fraynixOS);
        
        // Evolutionary Brain → Fraymus Layer 2 (Digital Genetics)
        // Note: Not yet wired, would need Digital Genetics component
        // (Not added to map - will be handled as unwired)
        
        // Memory Brain → Fraymus Layer 0 (AkashicRecord/Hippocampus)
        fraymusComponents.put(BrainType.MEMORY, akashicRecord);
        
        // Mark wired status
        for (BrainType type : BrainType.values()) {
            wiredStatus.put(type, fraymusComponents.containsKey(type) && fraymusComponents.get(type) != null);
        }
    }
    
    /**
     * Map brain types to SentientCore components
     */
    private void mapSentientCoreComponents() {
        // Logical Brain → SentientCore (CausalGraph)
        sentientCoreComponents.put(BrainType.LOGICAL, causalGraph);
        
        // Emotional Brain → SentientCore (FisherOracle)
        sentientCoreComponents.put(BrainType.EMOTIONAL, fisherOracle);
    }
    
    /**
     * Get the Fraymus component for a given brain type
     */
    public Optional<Object> getFraymusComponent(BrainType brainType) {
        return Optional.ofNullable(fraymusComponents.get(brainType));
    }
    
    /**
     * Get the SentientCore component for a given brain type
     */
    public Optional<Object> getSentientCoreComponent(BrainType brainType) {
        return Optional.ofNullable(sentientCoreComponents.get(brainType));
    }
    
    /**
     * Check if a brain type is wired to Fraymus
     */
    public boolean isWiredToFraymus(BrainType brainType) {
        return wiredStatus.getOrDefault(brainType, false);
    }
    
    /**
     * Check if a brain type is wired to SentientCore
     */
    public boolean isWiredToSentientCore(BrainType brainType) {
        return sentientCoreComponents.containsKey(brainType) && sentientCoreComponents.get(brainType) != null;
    }
    
    /**
     * Get all wired brain types
     */
    public Set<BrainType> getWiredBrainTypes() {
        Set<BrainType> wired = new HashSet<>();
        for (Map.Entry<BrainType, Boolean> entry : wiredStatus.entrySet()) {
            if (entry.getValue()) {
                wired.add(entry.getKey());
            }
        }
        return wired;
    }
    
    /**
     * Get initialization status
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get status report
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("initialized", initialized);
        status.put("fraymusComponentsCount", fraymusComponents.size());
        status.put("sentientCoreComponentsCount", sentientCoreComponents.size());
        status.put("wiredBrainTypes", getWiredBrainTypes());
        status.put("wiredStatus", new HashMap<>(wiredStatus));
        return status;
    }
}
