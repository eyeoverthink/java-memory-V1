package fraymus.brain.unified;

import fraymus.neural.AeonOmega;
import java.util.*;

/**
 * AeonOmegaBrainAdapter - Adapter for AeonOmega
 * 
 * Wraps the legacy AeonOmega system as a UnifiedBrain.
 * Provides access to the omega living singularity kernel.
 */
public class AeonOmegaBrainAdapter implements UnifiedBrain {
    
    private final AeonOmega aeonOmega;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AeonOmegaBrainAdapter(AeonOmega aeonOmega) {
        this.aeonOmega = aeonOmega;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AeonOmega";
    }
    
    @Override
    public String getType() {
        return "living_singularity";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 1.0;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS,
            BrainCapability.MEMORY,
            BrainCapability.CONSCIOUSNESS,
            BrainCapability.QUANTUM
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AeonOmega not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            // Get or generate concept vector
            long[] vector = aeonOmega.getOrGenerateConcept(query.getQuery());
            
            String result = "Omega concept generated for query: " + query.getQuery() + 
                          "\nVector hash: " + Arrays.hashCode(vector);
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("livingSingularity", true);
            metadata.put("vectorHash", Arrays.hashCode(vector));
            
            return BrainResponse.success(getName(), result, 0.97, 1.0, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AeonOmega processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 1.0, 1.0);
    }
}
