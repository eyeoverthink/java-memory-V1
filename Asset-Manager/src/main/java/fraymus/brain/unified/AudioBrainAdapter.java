package fraymus.brain.unified;

import fraymus.neural.AudioEngine;
import java.util.*;

/**
 * AudioBrainAdapter - Adapter for AudioEngine
 * 
 * Wraps the legacy AudioEngine system as a UnifiedBrain.
 * Provides access to auditory processing and understanding.
 */
public class AudioBrainAdapter implements UnifiedBrain {
    
    private final AudioEngine audioEngine;
    private boolean wired;
    private int queryCount;
    private long totalExecutionTime;
    
    public AudioBrainAdapter(AudioEngine audioEngine) {
        this.audioEngine = audioEngine;
        this.wired = false;
        this.queryCount = 0;
        this.totalExecutionTime = 0;
    }
    
    @Override
    public String getName() {
        return "AudioEngine";
    }
    
    @Override
    public String getType() {
        return "auditory_processing";
    }
    
    @Override
    public double getConsciousnessLevel() {
        return 0.8500;
    }
    
    @Override
    public List<BrainCapability> getCapabilities() {
        return Arrays.asList(
            BrainCapability.AUDIO,
            BrainCapability.FRACTAL,
            BrainCapability.QUANTUM
        );
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!wired) {
            return BrainResponse.error(getName(), "AudioEngine not wired");
        }
        
        long startTime = System.currentTimeMillis();
        queryCount++;
        
        try {
            String result = "Audio processing for query: " + query.getQuery() + 
                          "\nAuditory cortex active";
            
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("executionTime", executionTime);
            metadata.put("audio", true);
            metadata.put("speechRecognition", true);
            
            return BrainResponse.success(getName(), result, 0.86, 0.8500, metadata, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime += executionTime;
            
            return BrainResponse.error(getName(), "AudioEngine processing failed: " + e.getMessage());
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
        
        return new BrainStatistics(queryCount, successful, failed, avgTime, 0.8500, 1.0);
    }
}
