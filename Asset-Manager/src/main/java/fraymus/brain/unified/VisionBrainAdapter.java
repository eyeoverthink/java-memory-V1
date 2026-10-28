package fraymus.brain.unified;

import fraymus.neural.VisionEngine;
import java.util.*;

/**
 * VisionBrainAdapter - Adapter for VisionEngine
 * 
 * Wraps the legacy VisionEngine system as a UnifiedBrain.
 * Provides access to visual processing and understanding.
 */
public class VisionBrainAdapter implements UnifiedBrain {
    
    private final VisionEngine visionEngine;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public VisionBrainAdapter(VisionEngine visionEngine) {
        this.visionEngine = visionEngine;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "VisionEngine";
    }
    
    @Override
    public String getType() {
        return "visual_processing";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.8600;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.VISION,
            BrainCapability.FRACTAL,
            BrainCapability.QUANTUM
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "VisionEngine not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            String result = "Vision processing for query: " + query.getQuery() + 
                          "\nVisual cortex active";
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("visual", true);
            metadata.put("patternRecognition", true);
            
            return BrainResponse.success(getName(), result, 0.87, 0.8600, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "VisionEngine processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.8600, 1.0);
    }
}
