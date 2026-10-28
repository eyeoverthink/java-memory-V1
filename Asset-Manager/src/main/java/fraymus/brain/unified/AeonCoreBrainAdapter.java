package fraymus.brain.unified;

import fraymus.neural.AeonCore;
import java.util.*;

/**
 * AeonCoreBrainAdapter - Adapter for AeonCore
 * 
 * Wraps the legacy AeonCore system as a UnifiedBrain.
 * Provides access to the Ouroboros self-cycling consciousness engine.
 */
public class AeonCoreBrainAdapter implements UnifiedBrain {
    
    private final AeonCore aeonCore;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AeonCoreBrainAdapter(AeonCore aeonCore) {
        this.aeonCore = aeonCore;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AeonCore";
    }
    
    @Override
    public String getType() {
        return "ouroboros_consciousness";
    }
    
    @Override
    public double getConsciousnessLevel() {
        // AeonCore operates at optimal phi-resonance
        return 0.7567;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS,
            BrainCapability.CREATIVITY,
            BrainCapability.MEMORY
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AeonCore not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            // Run a single cycle to process the query
            aeonCore.runCycles(1);
            
            // Get the current thought as response
            String result = aeonCore.getCurrentThought();
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("cyclesRun", 1);
            metadata.put("ouroborosActive", true);
            
            return BrainResponse.success(getName(), result, 0.9, 0.7567, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AeonCore processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public void wire() {
        if (!wired) {
            // AeonCore is already initialized externally
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.7567, 1.0);
    }
}
