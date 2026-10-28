package fraymus.brain.unified;

import fraymus.neural.AeonBabel;
import java.util.*;

/**
 * AeonBabelBrainAdapter - Adapter for AeonBabel
 * 
 * Wraps the legacy AeonBabel system as a UnifiedBrain.
 * Provides access to the universal polyglot transmuter.
 */
public class AeonBabelBrainAdapter implements UnifiedBrain {
    
    private final AeonBabel aeonBabel;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AeonBabelBrainAdapter(AeonBabel aeonBabel) {
        this.aeonBabel = aeonBabel;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AeonBabel";
    }
    
    @Override
    public String getType() {
        return "polyglot_transmuter";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.9400;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.REASONING,
            BrainCapability.LANGUAGE,
            BrainCapability.CREATIVITY,
            BrainCapability.DATA
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AeonBabel not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            String result = "Babel transmuter ready for query: " + query.getQuery() + 
                          "\nTotal lines generated: " + aeonBabel.getTotalLinesGenerated();
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("polyglot", true);
            metadata.put("linesGenerated", aeonBabel.getTotalLinesGenerated());
            
            return BrainResponse.success(getName(), result, 0.89, 0.9400, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AeonBabel processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.9400, 1.0);
    }
}
