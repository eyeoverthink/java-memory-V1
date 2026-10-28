package fraymus.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ModelRegistry - Central registry of available models with their descriptors
 * and capability profiles.
 * 
 * This registry enables model selection based on capability contracts rather
 * than brand names, supporting the model-adaptive organism architecture.
 */
public class ModelRegistry {
    
    private final Map<String, ModelDescriptor> descriptors;
    private final Map<String, CapabilityProfile> profiles;
    private final Map<String, ModelAdapter> adapters;
    private final Map<String, String> organAssignments;
    
    public ModelRegistry() {
        this.descriptors = new ConcurrentHashMap<>();
        this.profiles = new ConcurrentHashMap<>();
        this.adapters = new ConcurrentHashMap<>();
        this.organAssignments = new ConcurrentHashMap<>();
    }
    
    /**
     * Register a model with its descriptor
     */
    public void registerModel(ModelDescriptor descriptor) {
        descriptors.put(descriptor.getFullName(), descriptor);
        
        // Auto-generate capability profile from descriptor
        CapabilityProfile profile = new CapabilityProfile.Builder()
            .modelId(descriptor.getFullName())
            .fromDescriptor(descriptor)
            .build();
        profiles.put(descriptor.getFullName(), profile);
    }
    
    /**
     * Register a model with custom capability profile
     */
    public void registerModel(ModelDescriptor descriptor, CapabilityProfile profile) {
        descriptors.put(descriptor.getFullName(), descriptor);
        profiles.put(descriptor.getFullName(), profile);
    }
    
    /**
     * Register an adapter for a model
     */
    public void registerAdapter(String modelId, ModelAdapter adapter) {
        adapters.put(modelId, adapter);
    }
    
    /**
     * Assign a model to an organ
     */
    public void assignToOrgan(String modelId, CapabilityProfile.Organ organ) {
        CapabilityProfile profile = profiles.get(modelId);
        if (profile == null) {
            throw new IllegalArgumentException("No profile found for model: " + modelId);
        }
        
        // Verify model is suitable for organ
        double suitability = profile.computeOrganSuitability(organ);
        if (suitability < 0.5) {
            throw new IllegalArgumentException(
                String.format("Model %s has low suitability %.2f for organ %s", 
                    modelId, suitability, organ));
        }
        
        organAssignments.put(organ.name(), modelId);
    }
    
    /**
     * Get model descriptor
     */
    public ModelDescriptor getDescriptor(String modelId) {
        return descriptors.get(modelId);
    }
    
    /**
     * Get capability profile
     */
    public CapabilityProfile getProfile(String modelId) {
        return profiles.get(modelId);
    }
    
    /**
     * Get adapter for model
     */
    public ModelAdapter getAdapter(String modelId) {
        return adapters.get(modelId);
    }
    
    /**
     * Get model assigned to organ
     */
    public String getAssignedModel(CapabilityProfile.Organ organ) {
        return organAssignments.get(organ.name());
    }
    
    /**
     * Find best model for an organ based on capability profile
     */
    public Optional<String> findBestModelForOrgan(CapabilityProfile.Organ organ) {
        return profiles.entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().computeOrganSuitability(organ)))
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);
    }
    
    /**
     * Find models meeting capability thresholds
     */
    public List<String> findModelsMeetingThreshold(CapabilityProfile.Capability capability, 
                                                    double threshold) {
        return profiles.entrySet().stream()
            .filter(entry -> entry.getValue().meetsThreshold(capability, threshold))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * Find reasoning models (high reasoning score)
     */
    public List<String> findReasoningModels() {
        return profiles.values().stream()
            .filter(CapabilityProfile::isReasoningModel)
            .map(CapabilityProfile::getModelId)
            .collect(Collectors.toList());
    }
    
    /**
     * Find reflex models (high reflex speed)
     */
    public List<String> findReflexModels() {
        return profiles.values().stream()
            .filter(CapabilityProfile::isReflexModel)
            .map(CapabilityProfile::getModelId)
            .collect(Collectors.toList());
    }
    
    /**
     * Find coding models (high coding score)
     */
    public List<String> findCodingModels() {
        return profiles.values().stream()
            .filter(CapabilityProfile::isCodingModel)
            .map(CapabilityProfile::getModelId)
            .collect(Collectors.toList());
    }
    
    /**
     * Find long-context models
     */
    public List<String> findLongContextModels() {
        return profiles.values().stream()
            .filter(CapabilityProfile::isLongContextModel)
            .map(CapabilityProfile::getModelId)
            .collect(Collectors.toList());
    }
    
    /**
     * Find local-capable models
     */
    public List<String> findLocalCapableModels() {
        return descriptors.values().stream()
            .filter(ModelDescriptor::isLocalFirst)
            .map(ModelDescriptor::getFullName)
            .collect(Collectors.toList());
    }
    
    /**
     * Find models by backend support
     */
    public List<String> findModelsByBackend(ModelDescriptor.Backend backend) {
        return descriptors.values().stream()
            .filter(desc -> desc.supportsBackend(backend))
            .map(ModelDescriptor::getFullName)
            .collect(Collectors.toList());
    }
    
    /**
     * Find models by family
     */
    public List<String> findModelsByFamily(String family) {
        return descriptors.values().stream()
            .filter(desc -> desc.getFamily().equalsIgnoreCase(family))
            .map(ModelDescriptor::getFullName)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all registered model IDs
     */
    public Set<String> getAllModelIds() {
        return new HashSet<>(descriptors.keySet());
    }
    
    /**
     * Get all models with descriptors and profiles
     */
    public Map<String, ModelRegistration> getAllRegistrations() {
        Map<String, ModelRegistration> registrations = new HashMap<>();
        
        for (String modelId : descriptors.keySet()) {
            ModelDescriptor desc = descriptors.get(modelId);
            CapabilityProfile profile = profiles.get(modelId);
            ModelAdapter adapter = adapters.get(modelId);
            String assignedOrgan = null;
            
            for (Map.Entry<String, String> entry : organAssignments.entrySet()) {
                if (entry.getValue().equals(modelId)) {
                    assignedOrgan = entry.getKey();
                    break;
                }
            }
            
            registrations.put(modelId, new ModelRegistration(desc, profile, adapter, assignedOrgan));
        }
        
        return registrations;
    }
    
    /**
     * Check if model is registered
     */
    public boolean isRegistered(String modelId) {
        return descriptors.containsKey(modelId);
    }
    
    /**
     * Check if model has adapter
     */
    public boolean hasAdapter(String modelId) {
        return adapters.containsKey(modelId);
    }
    
    /**
     * Remove model from registry
     */
    public void unregisterModel(String modelId) {
        descriptors.remove(modelId);
        profiles.remove(modelId);
        adapters.remove(modelId);
        organAssignments.values().removeIf(modelId::equals);
    }
    
    /**
     * Get registry statistics
     */
    public RegistryStatistics getStatistics() {
        int totalModels = descriptors.size();
        int withAdapters = adapters.size();
        int assigned = organAssignments.size();
        
        int localModels = (int) descriptors.values().stream()
            .filter(ModelDescriptor::isLocalFirst)
            .count();
        
        int longContextModels = (int) descriptors.values().stream()
            .filter(ModelDescriptor::isLongContext)
            .count();
        
        Map<String, Integer> backendCounts = new HashMap<>();
        for (ModelDescriptor desc : descriptors.values()) {
            for (ModelDescriptor.Backend backend : desc.getSupportedBackends()) {
                backendCounts.merge(backend.name(), 1, Integer::sum);
            }
        }
        
        return new RegistryStatistics(totalModels, withAdapters, assigned, 
                                    localModels, longContextModels, backendCounts);
    }
    
    /**
     * Model registration bundle
     */
    public static class ModelRegistration {
        private final ModelDescriptor descriptor;
        private final CapabilityProfile profile;
        private final ModelAdapter adapter;
        private final String assignedOrgan;
        
        public ModelRegistration(ModelDescriptor descriptor, CapabilityProfile profile,
                              ModelAdapter adapter, String assignedOrgan) {
            this.descriptor = descriptor;
            this.profile = profile;
            this.adapter = adapter;
            this.assignedOrgan = assignedOrgan;
        }
        
        public ModelDescriptor getDescriptor() { return descriptor; }
        public CapabilityProfile getProfile() { return profile; }
        public ModelAdapter getAdapter() { return adapter; }
        public String getAssignedOrgan() { return assignedOrgan; }
    }
    
    /**
     * Registry statistics
     */
    public static class RegistryStatistics {
        private final int totalModels;
        private final int withAdapters;
        private final int assigned;
        private final int localModels;
        private final int longContextModels;
        private final Map<String, Integer> backendCounts;
        
        public RegistryStatistics(int totalModels, int withAdapters, int assigned,
                               int localModels, int longContextModels,
                               Map<String, Integer> backendCounts) {
            this.totalModels = totalModels;
            this.withAdapters = withAdapters;
            this.assigned = assigned;
            this.localModels = localModels;
            this.longContextModels = longContextModels;
            this.backendCounts = new HashMap<>(backendCounts);
        }
        
        public int getTotalModels() { return totalModels; }
        public int getWithAdapters() { return withAdapters; }
        public int getAssigned() { return assigned; }
        public int getLocalModels() { return localModels; }
        public int getLongContextModels() { return longContextModels; }
        public Map<String, Integer> getBackendCounts() { return new HashMap<>(backendCounts); }
    }
    
    /**
     * Populate registry with known open models from the leaderboard
     */
    public void populateWithKnownModels() {
        // GLM-5
        registerModel(new ModelDescriptor.Builder()
            .family("glm")
            .variant("5")
            .fullName("glm-5")
            .contextWindow(128000)
            .license("custom")
            .addModality(ModelDescriptor.Modality.TEXT)
            .addModality(ModelDescriptor.Modality.CODE)
            .addModality(ModelDescriptor.Modality.FUNCTION_CALLING)
            .addBackend(ModelDescriptor.Backend.OLLAMA)
            .addBackend(ModelDescriptor.Backend.GGUF_DIRECT)
            .defaultQuantization(ModelDescriptor.Quantization.FP16)
            .speedTier(ModelDescriptor.SpeedTier.REASONING)
            .costTier(ModelDescriptor.CostTier.MEDIUM)
            .parameterCount(70)
            .localFirst(true)
            .build());
        
        // Qwen3.5
        registerModel(new ModelDescriptor.Builder()
            .family("qwen")
            .variant("3.5")
            .fullName("qwen3.5")
            .contextWindow(32768)
            .license("apache-2.0")
            .addModality(ModelDescriptor.Modality.TEXT)
            .addModality(ModelDescriptor.Modality.CODE)
            .addModality(ModelDescriptor.Modality.MULTIMODAL)
            .addBackend(ModelDescriptor.Backend.OLLAMA)
            .addBackend(ModelDescriptor.Backend.GGUF_DIRECT)
            .defaultQuantization(ModelDescriptor.Quantization.FP16)
            .speedTier(ModelDescriptor.SpeedTier.STANDARD)
            .costTier(ModelDescriptor.CostTier.LOW)
            .parameterCount(72)
            .localFirst(true)
            .build());
        
        // Qwen3-Coder
        registerModel(new ModelDescriptor.Builder()
            .family("qwen")
            .variant("3-coder")
            .fullName("qwen3-coder")
            .contextWindow(32768)
            .license("apache-2.0")
            .addModality(ModelDescriptor.Modality.CODE)
            .addModality(ModelDescriptor.Modality.TOOL_USE)
            .addBackend(ModelDescriptor.Backend.OLLAMA)
            .addBackend(ModelDescriptor.Backend.GGUF_DIRECT)
            .defaultQuantization(ModelDescriptor.Quantization.FP16)
            .speedTier(ModelDescriptor.SpeedTier.STANDARD)
            .costTier(ModelDescriptor.CostTier.LOW)
            .parameterCount(14)
            .localFirst(true)
            .build());
        
        // DeepSeek-V3.2
        registerModel(new ModelDescriptor.Builder()
            .family("deepseek")
            .variant("v3.2")
            .fullName("deepseek-v3.2")
            .contextWindow(128000)
            .license("custom")
            .addModality(ModelDescriptor.Modality.TEXT)
            .addModality(ModelDescriptor.Modality.CODE)
            .addModality(ModelDescriptor.Modality.FUNCTION_CALLING)
            .addBackend(ModelDescriptor.Backend.OLLAMA)
            .addBackend(ModelDescriptor.Backend.OPENAI_COMPATIBLE)
            .defaultQuantization(ModelDescriptor.Quantization.FP16)
            .speedTier(ModelDescriptor.SpeedTier.REASONING)
            .costTier(ModelDescriptor.CostTier.MEDIUM)
            .parameterCount(70)
            .localFirst(false)
            .build());

        // DeepSeek-R1
        registerModel(new ModelDescriptor.Builder()
            .family("deepseek")
            .variant("r1")
            .fullName("deepseek-r1")
            .contextWindow(164000)
            .license("open")
            .addModality(ModelDescriptor.Modality.TEXT)
            .addModality(ModelDescriptor.Modality.CODE)
            .addModality(ModelDescriptor.Modality.FUNCTION_CALLING)
            .addBackend(ModelDescriptor.Backend.OLLAMA)
            .addBackend(ModelDescriptor.Backend.OPENAI_COMPATIBLE)
            .defaultQuantization(ModelDescriptor.Quantization.FP16)
            .speedTier(ModelDescriptor.SpeedTier.REASONING)
            .costTier(ModelDescriptor.CostTier.MEDIUM)
            .parameterCount(70)
            .localFirst(false)
            .build());
        
        // Kimi K2.5
        registerModel(new ModelDescriptor.Builder()
            .family("kimi")
            .variant("k2.5")
            .fullName("kimi-k2.5")
            .contextWindow(1000000)
            .license("custom")
            .addModality(ModelDescriptor.Modality.TEXT)
            .addModality(ModelDescriptor.Modality.CODE)
            .addModality(ModelDescriptor.Modality.MULTIMODAL)
            .addBackend(ModelDescriptor.Backend.OPENAI_COMPATIBLE)
            .defaultQuantization(ModelDescriptor.Quantization.FP16)
            .speedTier(ModelDescriptor.SpeedTier.STANDARD)
            .costTier(ModelDescriptor.CostTier.HIGH)
            .parameterCount(70)
            .localFirst(false)
            .build());
        
        // Gemma 4
        registerModel(new ModelDescriptor.Builder()
            .family("gemma")
            .variant("4")
            .fullName("gemma-4")
            .contextWindow(32768)
            .license("apache-2.0")
            .addModality(ModelDescriptor.Modality.TEXT)
            .addModality(ModelDescriptor.Modality.CODE)
            .addBackend(ModelDescriptor.Backend.OLLAMA)
            .addBackend(ModelDescriptor.Backend.GGUF_DIRECT)
            .defaultQuantization(ModelDescriptor.Quantization.FP16)
            .speedTier(ModelDescriptor.SpeedTier.STANDARD)
            .costTier(ModelDescriptor.CostTier.FREE)
            .parameterCount(26)
            .localFirst(true)
            .build());
        
        // MiniMax 1M
        registerModel(new ModelDescriptor.Builder()
            .family("minimax")
            .variant("1m")
            .fullName("minimax-1m")
            .contextWindow(1000000)
            .license("custom")
            .addModality(ModelDescriptor.Modality.TEXT)
            .addModality(ModelDescriptor.Modality.MULTIMODAL)
            .addBackend(ModelDescriptor.Backend.OPENAI_COMPATIBLE)
            .defaultQuantization(ModelDescriptor.Quantization.FP16)
            .speedTier(ModelDescriptor.SpeedTier.STANDARD)
            .costTier(ModelDescriptor.CostTier.MEDIUM)
            .parameterCount(70)
            .localFirst(false)
            .build());
        
        // Step-3.5-Flash
        registerModel(new ModelDescriptor.Builder()
            .family("step")
            .variant("3.5-flash")
            .fullName("step-3.5-flash")
            .contextWindow(32768)
            .license("custom")
            .addModality(ModelDescriptor.Modality.TEXT)
            .addModality(ModelDescriptor.Modality.CODE)
            .addBackend(ModelDescriptor.Backend.OLLAMA)
            .addBackend(ModelDescriptor.Backend.GGUF_DIRECT)
            .defaultQuantization(ModelDescriptor.Quantization.INT4)
            .speedTier(ModelDescriptor.SpeedTier.FLASH)
            .costTier(ModelDescriptor.CostTier.LOW)
            .parameterCount(7)
            .localFirst(true)
            .build());
    }
}
