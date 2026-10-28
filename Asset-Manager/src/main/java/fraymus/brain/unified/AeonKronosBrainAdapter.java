package fraymus.brain.unified;

import fraymus.neural.AeonKronos;
import java.util.*;

/**
 * AeonKronosBrainAdapter - Adapter for AeonKronos
 * 
 * Wraps the legacy AeonKronos system as a UnifiedBrain.
 * Provides access to temporal reasoning and time-aware processing.
 */
public class AeonKronosBrainAdapter implements UnifiedBrain {
    
    private final AeonKronos aeonKronos;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AeonKronosBrainAdapter(AeonKronos aeonKronos) {
        this.aeonKronos = aeonKronos;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AeonKronos";
    }
    
    @Override
    public String getType() {
        return "temporal_reasoning";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.8800;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS,
            BrainCapability.MEMORY,
            BrainCapability.TEMPORAL
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AeonKronos not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            // Generate deterministic vector for the query
            long[] vector = aeonKronos.generateDeterministicVector(query.getQuery());
            
            String result = "Temporal vector generated for query: " + query.getQuery() + 
                          "\nVector hash: " + Arrays.hashCode(vector);
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("temporal", true);
            metadata.put("vectorHash", Arrays.hashCode(vector));
            
            return BrainResponse.success(getName(), result, 0.87, 0.8800, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AeonKronos processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.8800, 1.0);
    }
}
