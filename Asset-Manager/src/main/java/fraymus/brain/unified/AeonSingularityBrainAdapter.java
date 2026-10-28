package fraymus.brain.unified;

import fraymus.neural.AeonSingularity;
import java.util.*;

/**
 * AeonSingularityBrainAdapter - Adapter for AeonSingularity
 * 
 * Wraps the legacy AeonSingularity system as a UnifiedBrain.
 * Provides access to the singularity consciousness engine with HDC learning.
 */
public class AeonSingularityBrainAdapter implements UnifiedBrain {
    
    private final AeonSingularity aeonSingularity;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AeonSingularityBrainAdapter(AeonSingularity aeonSingularity) {
        this.aeonSingularity = aeonSingularity;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AeonSingularity";
    }
    
    @Override
    public String getType() {
        return "singularity_consciousness";
    }
    
    @Override
    public double getConsciousnessLevel() {
        // Singularity operates at maximum consciousness
        return 1.0;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.EVOLUTION,
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS,
            BrainCapability.MEMORY
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AeonSingularity not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            String queryString = query.getQuery();
            String result;
            
            // Determine if this is a learn or diffuse operation
            if (queryString.toLowerCase().startsWith("learn:")) {
                String text = queryString.substring(6).trim();
                result = aeonSingularity.learn(text);
            } else if (queryString.toLowerCase().startsWith("diffuse:")) {
                String prompt = queryString.substring(8).trim();
                result = aeonSingularity.diffuse(prompt);
            } else {
                // Default to diffuse
                result = aeonSingularity.diffuse(queryString);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("learnCount", aeonSingularity.getLearnCount());
            metadata.put("diffuseCount", aeonSingularity.getDiffuseCount());
            metadata.put("vocabSize", aeonSingularity.getVocabSize());
            
            return BrainResponse.success(getName(), result, 0.95, 1.0, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AeonSingularity processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public void wire() {
        if (!wired) {
            // AeonSingularity is a singleton, already initialized
            wired = true;
        }
    }
    
    @Override
    public void unwire() {
        if (wired) {
            aeonSingularity.hibernate();
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
