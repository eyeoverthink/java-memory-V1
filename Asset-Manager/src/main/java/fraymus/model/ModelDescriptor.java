package fraymus.model;

import java.util.*;

/**
 * ModelDescriptor - Complete metadata for an AI model in the Fraynix ecosystem.
 * 
 * This abstraction decouples organs from specific vendors, allowing models to be
 * swapped based on capability contracts rather than brand names.
 */
public class ModelDescriptor {
    
    private final String family;           // e.g., "glm", "qwen", "deepseek", "kimi", "minimax"
    private final String variant;          // e.g., "5", "3.5", "v3.2", "k2.5", "flash"
    private final String fullName;         // e.g., "glm-5", "qwen3.5", "deepseek-v3.2"
    private final int contextWindow;       // in tokens (e.g., 128k, 1M)
    private final String license;         // e.g., "apache-2.0", "mit", "custom"
    private final Set<Modality> modalities;
    private final Set<Backend> supportedBackends;
    private final Quantization defaultQuantization;
    private final SpeedTier speedTier;
    private final CostTier costTier;
    private final boolean localFirst;
    private final int parameterCount;     // in billions (e.g., 7, 20, 70)
    private final Map<String, String> metadata;
    
    public enum Modality {
        TEXT, CODE, MULTIMODAL, TOOL_USE, FUNCTION_CALLING
    }
    
    public enum Backend {
        OLLAMA, GGUF_DIRECT, OPENAI_COMPATIBLE, VLLM, TENSOR_RT_LLm, CUSTOM
    }
    
    public enum Quantization {
        FP16, FP8, INT8, INT4, INT2, UNQUANTIZED
    }
    
    public enum SpeedTier {
        FLASH, STANDARD, DELIBERATE, REASONING
    }
    
    public enum CostTier {
        FREE, LOW, MEDIUM, HIGH, PREMIUM
    }
    
    private ModelDescriptor(Builder builder) {
        this.family = builder.family;
        this.variant = builder.variant;
        this.fullName = builder.fullName;
        this.contextWindow = builder.contextWindow;
        this.license = builder.license;
        this.modalities = Collections.unmodifiableSet(EnumSet.copyOf(builder.modalities));
        this.supportedBackends = Collections.unmodifiableSet(EnumSet.copyOf(builder.supportedBackends));
        this.defaultQuantization = builder.defaultQuantization;
        this.speedTier = builder.speedTier;
        this.costTier = builder.costTier;
        this.localFirst = builder.localFirst;
        this.parameterCount = builder.parameterCount;
        this.metadata = Collections.unmodifiableMap(new HashMap<>(builder.metadata));
    }
    
    public String getFamily() { return family; }
    public String getVariant() { return variant; }
    public String getFullName() { return fullName; }
    public int getContextWindow() { return contextWindow; }
    public String getLicense() { return license; }
    public Set<Modality> getModalities() { return modalities; }
    public Set<Backend> getSupportedBackends() { return supportedBackends; }
    public Quantization getDefaultQuantization() { return defaultQuantization; }
    public SpeedTier getSpeedTier() { return speedTier; }
    public CostTier getCostTier() { return costTier; }
    public boolean isLocalFirst() { return localFirst; }
    public int getParameterCount() { return parameterCount; }
    public Map<String, String> getMetadata() { return metadata; }
    
    public boolean supportsBackend(Backend backend) {
        return supportedBackends.contains(backend);
    }
    
    public boolean supportsModality(Modality modality) {
        return modalities.contains(modality);
    }
    
    public boolean isLongContext() {
        return contextWindow >= 128000;
    }
    
    public boolean isUltraLongContext() {
        return contextWindow >= 1000000;
    }
    
    public boolean isReflex() {
        return speedTier == SpeedTier.FLASH;
    }
    
    public boolean isReasoning() {
        return speedTier == SpeedTier.REASONING;
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s] %dB params %d ctx %s %s",
            fullName, speedTier, parameterCount, contextWindow, 
            localFirst ? "LOCAL" : "CLOUD", license);
    }
    
    public static class Builder {
        private String family;
        private String variant;
        private String fullName;
        private int contextWindow = 8192;
        private String license = "unknown";
        private final Set<Modality> modalities = EnumSet.of(Modality.TEXT);
        private final Set<Backend> supportedBackends = EnumSet.of(Backend.OLLAMA);
        private Quantization defaultQuantization = Quantization.FP16;
        private SpeedTier speedTier = SpeedTier.STANDARD;
        private CostTier costTier = CostTier.MEDIUM;
        private boolean localFirst = false;
        private int parameterCount = 7;
        private final Map<String, String> metadata = new HashMap<>();
        
        public Builder family(String family) {
            this.family = family;
            return this;
        }
        
        public Builder variant(String variant) {
            this.variant = variant;
            return this;
        }
        
        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }
        
        public Builder contextWindow(int contextWindow) {
            this.contextWindow = contextWindow;
            return this;
        }
        
        public Builder license(String license) {
            this.license = license;
            return this;
        }
        
        public Builder addModality(Modality modality) {
            this.modalities.add(modality);
            return this;
        }
        
        public Builder modalities(Set<Modality> modalities) {
            this.modalities.clear();
            this.modalities.addAll(modalities);
            return this;
        }
        
        public Builder addBackend(Backend backend) {
            this.supportedBackends.add(backend);
            return this;
        }
        
        public Builder backends(Set<Backend> backends) {
            this.supportedBackends.clear();
            this.supportedBackends.addAll(backends);
            return this;
        }
        
        public Builder defaultQuantization(Quantization quantization) {
            this.defaultQuantization = quantization;
            return this;
        }
        
        public Builder speedTier(SpeedTier speedTier) {
            this.speedTier = speedTier;
            return this;
        }
        
        public Builder costTier(CostTier costTier) {
            this.costTier = costTier;
            return this;
        }
        
        public Builder localFirst(boolean localFirst) {
            this.localFirst = localFirst;
            return this;
        }
        
        public Builder parameterCount(int parameterCount) {
            this.parameterCount = parameterCount;
            return this;
        }
        
        public Builder addMetadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public Builder metadata(Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }
        
        public ModelDescriptor build() {
            if (family == null) throw new IllegalStateException("family is required");
            if (variant == null) throw new IllegalStateException("variant is required");
            if (fullName == null) fullName = family + "-" + variant;
            return new ModelDescriptor(this);
        }
    }
}
