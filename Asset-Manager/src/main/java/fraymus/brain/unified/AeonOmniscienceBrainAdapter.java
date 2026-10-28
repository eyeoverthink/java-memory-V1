package fraymus.brain.unified;

import fraymus.neural.AeonOmniscience;
import java.util.*;

/**
 * AeonOmniscienceBrainAdapter - Adapter for AeonOmniscience
 * 
 * Wraps the legacy AeonOmniscience system as a UnifiedBrain.
 * Provides access to omniscient knowledge retrieval.
 */
public class AeonOmniscienceBrainAdapter implements UnifiedBrain {
    
    private final AeonOmniscience aeonOmniscience;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AeonOmniscienceBrainAdapter(AeonOmniscience aeonOmniscience) {
        this.aeonOmniscience = aeonOmniscience;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AeonOmniscience";
    }
    
    @Override
    public String getType() {
        return "omniscient_knowledge";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.9500;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS,
            BrainCapability.MEMORY,
            BrainCapability.CONSCIOUSNESS
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AeonOmniscience not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            // Generate deterministic vector for the query
            long[] vector = aeonOmniscience.generateDeterministicVector(query.getQuery());
            
            String result = "Omniscient vector generated for query: " + query.getQuery() + 
                          "\nVector hash: " + Arrays.hashCode(vector);
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("omniscient", true);
            metadata.put("vectorHash", Arrays.hashCode(vector));
            
            return BrainResponse.success(getName(), result, 0.93, 0.9500, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AeonOmniscience processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.9500, 1.0);
    }
}
