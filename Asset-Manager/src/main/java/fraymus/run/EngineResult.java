package fraymus.run;

import java.util.*;

/**
 * Standardized result from an engine run
 * Allows apples-to-apples comparison
 */
public class EngineResult {
    public final String engineName;
    public final long seed;
    public final double fitnessScore;
    public final int iterations;
    public final long runtimeMs;
    public final int noveltyCount;
    public final Map<String, Object> metrics;
    
    private EngineResult(Builder builder) {
        this.engineName = builder.engineName;
        this.seed = builder.seed;
        this.fitnessScore = builder.fitnessScore;
        this.iterations = builder.iterations;
        this.runtimeMs = builder.runtimeMs;
        this.noveltyCount = builder.noveltyCount;
        this.metrics = new LinkedHashMap<>(builder.metrics);
    }
    
    public static Builder builder(String engineName) {
        return new Builder(engineName);
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("engine", engineName);
        map.put("seed", seed);
        map.put("fitness_score", fitnessScore);
        map.put("iterations", iterations);
        map.put("runtime_ms", runtimeMs);
        map.put("novelty_count", noveltyCount);
        map.put("metrics", new LinkedHashMap<>(metrics));
        return map;
    }
    
    public static class Builder {
        private final String engineName;
        private long seed;
        private double fitnessScore;
        private int iterations;
        private long runtimeMs;
        private int noveltyCount;
        private final Map<String, Object> metrics = new LinkedHashMap<>();
        
        public Builder(String engineName) {
            this.engineName = engineName;
        }
        
        public Builder seed(long seed) { this.seed = seed; return this; }
        public Builder fitnessScore(double score) { this.fitnessScore = score; return this; }
        public Builder iterations(int iter) { this.iterations = iter; return this; }
        public Builder runtimeMs(long ms) { this.runtimeMs = ms; return this; }
        public Builder noveltyCount(int count) { this.noveltyCount = count; return this; }
        public Builder metric(String name, Object value) { 
            this.metrics.put(name, value); 
            return this; 
        }
        
        public EngineResult build() {
            return new EngineResult(this);
        }
    }
}
