package fraymus.brain.unified;

import fraymus.neural.AuboLedger;
import java.util.*;

/**
 * AuboLedgerBrainAdapter - Adapter for AuboLedger
 * 
 * Wraps the legacy AuboLedger system as a UnifiedBrain.
 * Provides access to the ledger system.
 */
public class AuboLedgerBrainAdapter implements UnifiedBrain {
    
    private final AuboLedger auboLedger;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AuboLedgerBrainAdapter(AuboLedger auboLedger) {
        this.auboLedger = auboLedger;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AuboLedger";
    }
    
    @Override
    public String getType() {
        return "ledger_system";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.8000;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.MEMORY,
            BrainCapability.REASONING,
            BrainCapability.MATHEMATICS
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AuboLedger not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            String result = "Ledger system processed query: " + query.getQuery();
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("ledger", true);
            
            return BrainResponse.success(getName(), result, 0.85, 0.8000, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AuboLedger processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.8000, 1.0);
    }
}
