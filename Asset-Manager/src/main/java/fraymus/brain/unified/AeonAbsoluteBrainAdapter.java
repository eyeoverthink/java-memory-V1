package fraymus.brain.unified;

import fraymus.neural.AeonAbsolute;
import java.util.*;

/**
 * AeonAbsoluteBrainAdapter - Adapter for AeonAbsolute
 * 
 * Wraps the legacy AeonAbsolute system as a UnifiedBrain.
 * Provides access to the hyper-dimensional holographic memory system.
 */
public class AeonAbsoluteBrainAdapter implements UnifiedBrain {
    
    private final AeonAbsolute aeonAbsolute;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AeonAbsoluteBrainAdapter(AeonAbsolute aeonAbsolute) {
        this.aeonAbsolute = aeonAbsolute;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AeonAbsolute";
    }
    
    @Override
    public String getType() {
        return "holographic_memory";
    }
    
    @Override
    public double getConsciousnessLevel() {
        // Holographic memory operates at high consciousness
        return 0.8500;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.MEMORY,
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS,
            BrainCapability.QUANTUM
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AeonAbsolute not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            String queryString = query.getQuery();
            String result;
            
            // Determine if this is a learn or recall operation
            if (queryString.toLowerCase().startsWith("learn:")) {
                String pattern = queryString.substring(6).trim();
                aeonAbsolute.holoLearn(pattern);
                result = "Pattern learned in holographic memory: " + pattern;
            } else if (queryString.toLowerCase().startsWith("recall:")) {
                String recallQuery = queryString.substring(7).trim();
                double similarity = aeonAbsolute.holoRecall(recallQuery);
                result = "Holographic recall similarity: " + similarity;
            } else {
                // Default to recall
                double similarity = aeonAbsolute.holoRecall(queryString);
                result = "Holographic similarity: " + similarity + " for query: " + queryString;
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("holographic", true);
            metadata.put("dims", 16384);
            
            return BrainResponse.success(getName(), result, 0.85, 0.8500, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AeonAbsolute processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public void wire() {
        if (!wired) {
            // Ignite the swarm if not already active
            aeonAbsolute.igniteSwarm();
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.8500, 1.0);
    }
}
