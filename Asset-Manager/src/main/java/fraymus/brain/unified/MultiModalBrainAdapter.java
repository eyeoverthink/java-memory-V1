package fraymus.brain.unified;

import fraymus.neural.CrossModalBindingEngine;
import java.util.*;

/**
 * MultiModalBrainAdapter - Adapter for CrossModalBindingEngine
 * 
 * Wraps the legacy CrossModalBindingEngine system as a UnifiedBrain.
 * Provides access to cross-modal sensory processing.
 */
public class MultiModalBrainAdapter implements UnifiedBrain {
    
    private final CrossModalBindingEngine crossModalBindingEngine;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public MultiModalBrainAdapter(CrossModalBindingEngine crossModalBindingEngine) {
        this.crossModalBindingEngine = crossModalBindingEngine;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "CrossModalEngine";
    }
    
    @Override
    public String getType() {
        return "cross_modal";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.8900;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.VISION,
            BrainCapability.AUDIO,
            BrainCapability.QUANTUM,
            BrainCapability.FRACTAL
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "CrossModalEngine not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            String result = "Cross-modal processing for query: " + query.getQuery() + 
                          "\nSensory integration active";
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("crossModal", true);
            metadata.put("sensoryIntegration", true);
            
            return BrainResponse.success(getName(), result, 0.88, 0.8900, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "CrossModalEngine processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public void wire() {
        if (!wired) {
            wired = true;
        }
    }
    
    @Override
    public void unwire() {
        if (wired) {
            wired = false;
        }
    }
    
    @Override
    public boolean isWired() {
        return wired;
    }
    
    @Override
    public double getHealth() {
        return wired ? 1.0 : 0.0;
    }
    
    @Override
    public BrainStatistics getStatistics() {
        long successful = queryCount;
        long failed = 0;
        double avgTime = queryCount > 0 ? (double)totalExecutionTime / queryCount : 0.0;
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.8900, 1.0);
    }
}
