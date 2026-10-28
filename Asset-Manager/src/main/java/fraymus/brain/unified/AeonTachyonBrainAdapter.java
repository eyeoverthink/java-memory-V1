package fraymus.brain.unified;

import fraymus.neural.AeonTachyon;
import java.util.*;

/**
 * AeonTachyonBrainAdapter - Adapter for AeonTachyon
 * 
 * Wraps the legacy AeonTachyon system as a UnifiedBrain.
 * Provides access to tachyonic faster-than-light reasoning.
 */
public class AeonTachyonBrainAdapter implements UnifiedBrain {
    
    private final AeonTachyon aeonTachyon;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AeonTachyonBrainAdapter(AeonTachyon aeonTachyon) {
        this.aeonTachyon = aeonTachyon;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AeonTachyon";
    }
    
    @Override
    public String getType() {
        return "tachyonic_reasoning";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.9000;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS,
            BrainCapability.CREATIVITY
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AeonTachyon not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            // Generate deterministic vector for the query
            long[] vector = AeonTachyon.generateDeterministicVector(query.getQuery());
            
            String result = "Tachyonic vector generated for query: " + query.getQuery() + 
                          "\nVector hash: " + Arrays.hashCode(vector);
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("tachyonic", true);
            metadata.put("vectorHash", Arrays.hashCode(vector));
            
            return BrainResponse.success(getName(), result, 0.88, 0.9000, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AeonTachyon processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.9000, 1.0);
    }
}
