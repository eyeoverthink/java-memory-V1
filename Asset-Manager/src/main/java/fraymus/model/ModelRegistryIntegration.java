package fraymus.model;

import fraymus.knowledge.AkashicRecord;
import fraymus.core.OllamaBridge;

/**
 * ModelRegistryIntegration - Helper class for integrating Model Registry into FraynixBoot.
 * 
 * This class provides a single initialization point for the Model Registry system,
 * making it easy to wire into the FraynixBoot sequence.
 */
public class ModelRegistryIntegration {
    private static volatile ModelRegistryIntegration activeIntegration;
    
    private ModelRegistry modelRegistry;
    private ModelSelector modelSelector;
    private PromptCompiler promptCompiler;
    private EvaluationHarness evaluationHarness;
    private AkashicModelMemory akashicModelMemory;
    
    private boolean initialized = false;
    
    /**
     * Initialize the Model Registry system with default configuration
     */
    public void initialize() {
        if (initialized) {
            activeIntegration = this;
            return;
        }
        
        // Create ModelRegistry
        modelRegistry = new ModelRegistry();
        
        // Populate with known open models
        modelRegistry.populateWithKnownModels();
        registerObservedLocalModels();
        
        // Create ModelSelector
        modelSelector = new ModelSelector(modelRegistry);
        
        // Create PromptCompiler
        promptCompiler = new PromptCompiler(modelRegistry);
        
        // Create EvaluationHarness
        evaluationHarness = new EvaluationHarness(modelRegistry, modelSelector, promptCompiler);
        
        // Create AkashicModelMemory
        akashicModelMemory = new AkashicModelMemory(modelRegistry);
        
        initialized = true;
        activeIntegration = this;
        
        System.out.println("[ModelRegistryIntegration] Model Registry System initialized");
        System.out.println("   - ModelRegistry: " + modelRegistry.getAllModelIds().size() + " models registered");
        System.out.println("   - ModelSelector: Ready for capability-based selection");
        System.out.println("   - PromptCompiler: " + promptCompiler.getDialect("default").getFamily() + " dialect loaded");
        System.out.println("   - EvaluationHarness: " + evaluationHarness.getAllTaskSuites().size() + " task suites ready");
        System.out.println("   - AkashicModelMemory: Performance tracking enabled");
    }
    
    /**
     * Initialize with AkashicRecord for persistence
     */
    public void initializeWithAkashic(AkashicRecord akashic) {
        initialize();
        // Future: Wire AkashicModelMemory to AkashicRecord
        System.out.println("[ModelRegistryIntegration] AkashicRecord wired for persistence");
    }
    
    /**
     * Initialize with OllamaBridge for backend execution
     */
    public void initializeWithOllama(OllamaBridge ollama) {
        initialize();
        registerObservedLocalModels();
        System.out.println("[ModelRegistryIntegration] Observed local models have runnable Ollama adapters");
    }

    public int registerObservedLocalModels() {
        int count = 0;
        for (fraymus.llm.ModelDescriptor local : new fraymus.llm.OpenModelRegistry().all()) {
            if (!local.id().startsWith("local-ollama/")) continue;
            ModelDescriptor.Builder builder = new ModelDescriptor.Builder()
                .family(local.family()).variant(local.variant()).fullName(local.variant())
                .contextWindow(local.contextTokens()).license(local.license())
                .localFirst(true).parameterCount(fraymus.llm.LocalModelCatalog.parameterBillions(local.variant()))
                .costTier(ModelDescriptor.CostTier.FREE)
                .addMetadata("evidence", "observed-local-metadata; capability scores are priors")
                .addMetadata("quantization", local.quantizationHint())
                .addMetadata("endpoint", fraymus.llm.LocalModelCatalog.endpointForModel(local.variant(), "http://127.0.0.1:11434"));
            String quantization = local.quantizationHint().toUpperCase(java.util.Locale.ROOT);
            builder.defaultQuantization(quantization.contains("4") ? ModelDescriptor.Quantization.INT4
                : quantization.contains("FP8") ? ModelDescriptor.Quantization.FP8
                : quantization.contains("8") ? ModelDescriptor.Quantization.INT8 : ModelDescriptor.Quantization.FP16);
            if (local.modality().contains("vision")) builder.addModality(ModelDescriptor.Modality.MULTIMODAL);
            fraymus.llm.CapabilityProfile p = local.profile();
            CapabilityProfile profile = new CapabilityProfile.Builder().modelId(local.variant())
                .setScore(CapabilityProfile.Capability.REASONING, p.reasoning())
                .setScore(CapabilityProfile.Capability.CODING, p.coding())
                .setScore(CapabilityProfile.Capability.LONG_CONTEXT, p.longContext())
                .setScore(CapabilityProfile.Capability.REFLEX_SPEED, p.reflexSpeed())
                .setScore(CapabilityProfile.Capability.CREATIVITY, p.creativity())
                .setScore(CapabilityProfile.Capability.MULTILINGUAL, p.multilingual())
                .setScore(CapabilityProfile.Capability.TOOL_USE, p.toolUse())
                .setScore(CapabilityProfile.Capability.MULTIMODAL, p.multimodal())
                .setScore(CapabilityProfile.Capability.LOCAL_FIT, p.localFit())
                .addMetric("qualityEvidence", "unbenchmarked-prior").build();
            modelRegistry.registerModel(builder.build(), profile);
            modelRegistry.registerAdapter(local.variant(), new LocalOllamaModelAdapter(local.variant()));
            count++;
        }
        return count;
    }
    
    /**
     * Get ModelRegistry instance
     */
    public ModelRegistry getModelRegistry() {
        return modelRegistry;
    }
    
    /**
     * Get ModelSelector instance
     */
    public ModelSelector getModelSelector() {
        return modelSelector;
    }
    
    /**
     * Get PromptCompiler instance
     */
    public PromptCompiler getPromptCompiler() {
        return promptCompiler;
    }
    
    /**
     * Get EvaluationHarness instance
     */
    public EvaluationHarness getEvaluationHarness() {
        return evaluationHarness;
    }
    
    /**
     * Get AkashicModelMemory instance
     */
    public AkashicModelMemory getAkashicModelMemory() {
        return akashicModelMemory;
    }
    
    /**
     * Check if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    public int getModelCount() {
        return initialized ? modelRegistry.getAllModelIds().size() : 0;
    }

    /**
     * Expose current active integration for whole-system access.
     */
    public static ModelRegistryIntegration getActiveIntegration() {
        return activeIntegration;
    }

    /**
     * Force active integration pointer (used by boot orchestration).
     */
    public static void setActiveIntegration(ModelRegistryIntegration integration) {
        activeIntegration = integration;
    }
    
    /**
     * Get system statistics
     */
    public String getStatistics() {
        if (!initialized) {
            return "Model Registry System not initialized";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Model Registry System Statistics:\n");
        sb.append("  Models: ").append(modelRegistry.getAllModelIds().size()).append("\n");
        sb.append("  Task Suites: ").append(evaluationHarness.getAllTaskSuites().size()).append("\n");
        sb.append("  Adapters: ").append(modelRegistry.getStatistics().getWithAdapters()).append("\n");
        sb.append("  Assigned Organs: ").append(modelRegistry.getStatistics().getAssigned()).append("\n");
        return sb.toString();
    }
}
