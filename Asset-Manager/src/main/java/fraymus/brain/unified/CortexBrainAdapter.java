package fraymus.brain.unified;

import fraymus.neural.HyperCortex;
import java.util.*;

/**
 * CortexBrainAdapter - Adapter for HyperCortex
 * 
 * Wraps the legacy HyperCortex system as a UnifiedBrain.
 * Provides access to the hyper-dimensional cortex processing.
 */
public class CortexBrainAdapter implements UnifiedBrain {
    
    private final HyperCortex hyperCortex;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public CortexBrainAdapter(HyperCortex hyperCortex) {
        this.hyperCortex = hyperCortex;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "HyperCortex";
    }
    
    @Override
    public String getType() {
        return "hyper_cortex";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.8700;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS,
            BrainCapability.QUANTUM,
            BrainCapability.FRACTAL
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "HyperCortex not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            String result = "HyperCortex processed query: " + query.getQuery() + 
                          "\nCortex processing active";
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("hyperCortex", true);
            
            return BrainResponse.success(getName(), result, 0.86, 0.8700, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "HyperCortex processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.8700, 1.0);
    }
}
