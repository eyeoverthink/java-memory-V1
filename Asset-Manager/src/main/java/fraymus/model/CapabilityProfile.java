package fraymus.model;

import java.util.*;

/**
 * CapabilityProfile - Quantitative scoring of model capabilities for organ assignment.
 * 
 * This profile allows Fraynix to select models based on actual capability metrics
 * rather than brand names, enabling model-adaptive behavior.
 */
public class CapabilityProfile {
    
    private final String modelId;
    private final Map<Capability, Double> scores;
    private final Map<String, Object> metrics;
    private final long lastUpdated;
    
    /**
     * Core capability dimensions for model selection
     */
    public enum Capability {
        REASONING,           // Deep logical inference and chain-of-thought
        CODING,              // Code generation, debugging, refactoring
        LONG_CONTEXT,        // Ability to maintain coherence over long contexts
        REFLEX_SPEED,        // Fast response time for quick decisions
        CREATIVITY,          // Novel idea generation and remixing
        MULTILINGUAL,        // Language diversity and translation
        TOOL_USE,            // Function calling and tool integration
        MULTIMODAL,          // Vision, audio, and cross-modal reasoning
        LOCAL_FIT,           // Suitability for local/offline deployment
        MEMORY_RETENTION,    // Ability to remember and recall information
        SAFETY_ALIGNMENT,    // Adherence to safety guidelines
        PRECISION,           // Accuracy and factual correctness
        EFFICIENCY           // Compute efficiency per token
    }
    
    private CapabilityProfile(Builder builder) {
        this.modelId = builder.modelId;
        this.scores = Collections.unmodifiableMap(new HashMap<>(builder.scores));
        this.metrics = Collections.unmodifiableMap(new HashMap<>(builder.metrics));
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public String getModelId() { return modelId; }
    public Map<Capability, Double> getScores() { return scores; }
    public Map<String, Object> getMetrics() { return metrics; }
    public long getLastUpdated() { return lastUpdated; }
    
    public double getScore(Capability capability) {
        return scores.getOrDefault(capability, 0.0);
    }
    
    public boolean meetsThreshold(Capability capability, double threshold) {
        return getScore(capability) >= threshold;
    }
    
    public boolean isReasoningModel() {
        return meetsThreshold(Capability.REASONING, 0.7);
    }
    
    public boolean isReflexModel() {
        return meetsThreshold(Capability.REFLEX_SPEED, 0.7);
    }
    
    public boolean isCodingModel() {
        return meetsThreshold(Capability.CODING, 0.7);
    }
    
    public boolean isLongContextModel() {
        return meetsThreshold(Capability.LONG_CONTEXT, 0.7);
    }
    
    public boolean isCreativeModel() {
        return meetsThreshold(Capability.CREATIVITY, 0.7);
    }
    
    public boolean isLocalCapable() {
        return meetsThreshold(Capability.LOCAL_FIT, 0.6);
    }
    
    /**
     * Compute overall suitability score for a specific organ
     */
    public double computeOrganSuitability(Organ organ) {
        Map<Capability, Double> weights = organ.getCapabilityWeights();
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        
        for (Map.Entry<Capability, Double> entry : weights.entrySet()) {
            double score = getScore(entry.getKey());
            double weight = entry.getValue();
            weightedSum += score * weight;
            totalWeight += weight;
        }
        
        return totalWeight > 0 ? weightedSum / totalWeight : 0.0;
    }
    
    /**
     * Organ definitions with capability weight profiles
     */
    public enum Organ {
        PREFRONTAL_CORTEX("Reasoning and intent formation", 
            Map.of(Capability.REASONING, 0.4, Capability.PRECISION, 0.3, 
                   Capability.SAFETY_ALIGNMENT, 0.2, Capability.CREATIVITY, 0.1)),
        
        PARIETAL_LOBE("Spatial processing and sensory integration",
            Map.of(Capability.MULTIMODAL, 0.3, Capability.TOOL_USE, 0.3,
                   Capability.REFLEX_SPEED, 0.2, Capability.REASONING, 0.2)),
        
        HIPPOCAMPUS("Memory formation and consolidation",
            Map.of(Capability.LONG_CONTEXT, 0.4, Capability.MEMORY_RETENTION, 0.3,
                   Capability.EFFICIENCY, 0.2, Capability.REASONING, 0.1)),
        
        SPINAL_CORD("Reflex arcs and quick responses",
            Map.of(Capability.REFLEX_SPEED, 0.5, Capability.EFFICIENCY, 0.3,
                   Capability.LOCAL_FIT, 0.2, Capability.PRECISION, 0.0)),
        
        CODING_ORGAN("Code generation and transformation",
            Map.of(Capability.CODING, 0.4, Capability.PRECISION, 0.3,
                   Capability.TOOL_USE, 0.2, Capability.REASONING, 0.1)),
        
        CREATIVE_CORTEX("Creative generation and remixing",
            Map.of(Capability.CREATIVITY, 0.4, Capability.REASONING, 0.2,
                   Capability.LONG_CONTEXT, 0.2, Capability.MULTILINGUAL, 0.2)),
        
        MULTIMODAL_ENGINE("Cross-modal reasoning",
            Map.of(Capability.MULTIMODAL, 0.4, Capability.TOOL_USE, 0.3,
                   Capability.REFLEX_SPEED, 0.2, Capability.PRECISION, 0.1)),
        
        TRANSLATION_ENGINE("Language translation and transmutation",
            Map.of(Capability.MULTILINGUAL, 0.4, Capability.PRECISION, 0.3,
                   Capability.EFFICIENCY, 0.2, Capability.CREATIVITY, 0.1));
        
        private final String description;
        private final Map<Capability, Double> capabilityWeights;
        
        Organ(String description, Map<Capability, Double> capabilityWeights) {
            this.description = description;
            this.capabilityWeights = Collections.unmodifiableMap(new HashMap<>(capabilityWeights));
        }
        
        public String getDescription() { return description; }
        public Map<Capability, Double> getCapabilityWeights() { return capabilityWeights; }
    }
    
    /**
     * Compare two capability profiles
     */
    public ComparisonResult compareTo(CapabilityProfile other) {
        Map<Capability, Double> deltas = new HashMap<>();
        int better = 0, worse = 0, equal = 0;
        
        for (Capability cap : Capability.values()) {
            double myScore = this.getScore(cap);
            double otherScore = other.getScore(cap);
            double delta = myScore - otherScore;
            deltas.put(cap, delta);
            
            if (delta > 0.1) better++;
            else if (delta < -0.1) worse++;
            else equal++;
        }
        
        return new ComparisonResult(this.modelId, other.modelId, deltas, better, worse, equal);
    }
    
    public static class ComparisonResult {
        private final String model1;
        private final String model2;
        private final Map<Capability, Double> deltas;
        private final int better;
        private final int worse;
        private final int equal;
        
        public ComparisonResult(String model1, String model2, Map<Capability, Double> deltas,
                              int better, int worse, int equal) {
            this.model1 = model1;
            this.model2 = model2;
            this.deltas = Collections.unmodifiableMap(deltas);
            this.better = better;
            this.worse = worse;
            this.equal = equal;
        }
        
        public String getModel1() { return model1; }
        public String getModel2() { return model2; }
        public Map<Capability, Double> getDeltas() { return deltas; }
        public int getBetterCount() { return better; }
        public int getWorseCount() { return worse; }
        public int getEqualCount() { return equal; }
        
        public boolean isDominant() {
            return better > worse;
        }
        
        public String getVerdict() {
            if (better > worse) return model1 + " dominates";
            if (worse > better) return model2 + " dominates";
            return "comparable";
        }
    }
    
    public static class Builder {
        private String modelId;
        private final Map<Capability, Double> scores = new HashMap<>();
        private final Map<String, Object> metrics = new HashMap<>();
        
        public Builder modelId(String modelId) {
            this.modelId = modelId;
            return this;
        }
        
        public Builder setScore(Capability capability, double score) {
            if (score < 0 || score > 1) {
                throw new IllegalArgumentException("Score must be in [0, 1]");
            }
            this.scores.put(capability, score);
            return this;
        }
        
        public Builder setScores(Map<Capability, Double> scores) {
            for (Map.Entry<Capability, Double> entry : scores.entrySet()) {
                setScore(entry.getKey(), entry.getValue());
            }
            return this;
        }
        
        public Builder addMetric(String key, Object value) {
            this.metrics.put(key, value);
            return this;
        }
        
        public Builder metrics(Map<String, Object> metrics) {
            this.metrics.putAll(metrics);
            return this;
        }
        
        /**
         * Auto-populate scores based on ModelDescriptor
         */
        public Builder fromDescriptor(ModelDescriptor descriptor) {
            // Infer capabilities from descriptor
            if (descriptor.isReasoning()) setScore(Capability.REASONING, 0.8);
            if (descriptor.isReflex()) setScore(Capability.REFLEX_SPEED, 0.9);
            if (descriptor.supportsModality(ModelDescriptor.Modality.CODE)) {
                setScore(Capability.CODING, 0.7);
            }
            if (descriptor.isLongContext()) setScore(Capability.LONG_CONTEXT, 0.8);
            if (descriptor.isUltraLongContext()) setScore(Capability.LONG_CONTEXT, 0.95);
            if (descriptor.isLocalFirst()) setScore(Capability.LOCAL_FIT, 0.9);
            if (descriptor.getParameterCount() >= 20) setScore(Capability.REASONING, 0.7);
            if (descriptor.getParameterCount() <= 7) setScore(Capability.EFFICIENCY, 0.8);
            
            return this;
        }
        
        public CapabilityProfile build() {
            if (modelId == null) throw new IllegalStateException("modelId is required");
            return new CapabilityProfile(this);
        }
    }
}
