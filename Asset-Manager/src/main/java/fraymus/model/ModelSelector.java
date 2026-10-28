package fraymus.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ModelSelector - Selection policy for choosing models by organ + task + hardware.
 * 
 * This implements the adaptive selection logic that allows Fraynix to choose
 * the best model for a given task based on capability profiles, hardware constraints,
 * and current system state.
 */
public class ModelSelector {
    
    private final ModelRegistry registry;
    private final Map<String, SelectionPolicy> customPolicies;
    private SelectionConstraints globalConstraints;
    
    public ModelSelector(ModelRegistry registry) {
        this.registry = registry;
        this.customPolicies = new ConcurrentHashMap<>();
        this.globalConstraints = new SelectionConstraints();
    }
    
    /**
     * Selection constraints for model selection
     */
    public static class SelectionConstraints {
        private boolean requireLocal = false;
        private boolean requireLongContext = false;
        private int maxContextTokens = Integer.MAX_VALUE;
        private int maxParameters = Integer.MAX_VALUE;
        private ModelDescriptor.SpeedTier minSpeedTier = ModelDescriptor.SpeedTier.STANDARD;
        private ModelDescriptor.CostTier maxCostTier = ModelDescriptor.CostTier.PREMIUM;
        private Set<ModelDescriptor.Backend> requiredBackends = new HashSet<>();
        
        public boolean isRequireLocal() { return requireLocal; }
        public boolean isRequireLongContext() { return requireLongContext; }
        public int getMaxContextTokens() { return maxContextTokens; }
        public int getMaxParameters() { return maxParameters; }
        public ModelDescriptor.SpeedTier getMinSpeedTier() { return minSpeedTier; }
        public ModelDescriptor.CostTier getMaxCostTier() { return maxCostTier; }
        public Set<ModelDescriptor.Backend> getRequiredBackends() { return new HashSet<>(requiredBackends); }
        
        public SelectionConstraints requireLocal(boolean requireLocal) {
            this.requireLocal = requireLocal;
            return this;
        }
        
        public SelectionConstraints requireLongContext(boolean requireLongContext) {
            this.requireLongContext = requireLongContext;
            return this;
        }
        
        public SelectionConstraints maxContextTokens(int maxContextTokens) {
            this.maxContextTokens = maxContextTokens;
            return this;
        }
        
        public SelectionConstraints maxParameters(int maxParameters) {
            this.maxParameters = maxParameters;
            return this;
        }
        
        public SelectionConstraints minSpeedTier(ModelDescriptor.SpeedTier minSpeedTier) {
            this.minSpeedTier = minSpeedTier;
            return this;
        }
        
        public SelectionConstraints maxCostTier(ModelDescriptor.CostTier maxCostTier) {
            this.maxCostTier = maxCostTier;
            return this;
        }
        
        public SelectionConstraints requireBackend(ModelDescriptor.Backend backend) {
            this.requiredBackends.add(backend);
            return this;
        }
        
        public SelectionConstraints requireBackends(Set<ModelDescriptor.Backend> backends) {
            this.requiredBackends.addAll(backends);
            return this;
        }
    }
    
    /**
     * Selection result with reasoning
     */
    public static class SelectionResult {
        private final String selectedModelId;
        private final String reasoning;
        private final double suitabilityScore;
        private final List<String> rejectedModels;
        private final SelectionConstraints appliedConstraints;
        
        public SelectionResult(String selectedModelId, String reasoning, 
                              double suitabilityScore, List<String> rejectedModels,
                              SelectionConstraints appliedConstraints) {
            this.selectedModelId = selectedModelId;
            this.reasoning = reasoning;
            this.suitabilityScore = suitabilityScore;
            this.rejectedModels = new ArrayList<>(rejectedModels);
            this.appliedConstraints = appliedConstraints;
        }
        
        public String getSelectedModelId() { return selectedModelId; }
        public String getReasoning() { return reasoning; }
        public double getSuitabilityScore() { return suitabilityScore; }
        public List<String> getRejectedModels() { return new ArrayList<>(rejectedModels); }
        public SelectionConstraints getAppliedConstraints() { return appliedConstraints; }
    }
    
    /**
     * Select model for organ based on capability profile
     */
    public SelectionResult selectForOrgan(CapabilityProfile.Organ organ) {
        return selectForOrgan(organ, globalConstraints);
    }
    
    /**
     * Select model for organ with custom constraints
     */
    public SelectionResult selectForOrgan(CapabilityProfile.Organ organ, SelectionConstraints constraints) {
        List<String> candidateModels = registry.getAllModelIds().stream()
            .filter(modelId -> meetsConstraints(modelId, constraints))
            .collect(Collectors.toList());
        
        if (candidateModels.isEmpty()) {
            return new SelectionResult(null, 
                "No models meet constraints: " + constraints.toString(),
                0.0, Collections.emptyList(), constraints);
        }
        
        // Score each candidate for the organ
        Map<String, Double> scores = new HashMap<>();
        for (String modelId : candidateModels) {
            CapabilityProfile profile = registry.getProfile(modelId);
            if (profile != null) {
                scores.put(modelId, profile.computeOrganSuitability(organ));
            }
        }
        
        // Select best
        String bestModel = scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        double bestScore = scores.getOrDefault(bestModel, 0.0);
        
        // Build reasoning
        StringBuilder reasoning = new StringBuilder();
        reasoning.append("Selected ").append(bestModel)
            .append(" for organ ").append(organ.name())
            .append(" with suitability score ").append(String.format("%.2f", bestScore));
        
        // Check if has adapter
        if (!registry.hasAdapter(bestModel)) {
            reasoning.append(" (WARNING: no adapter registered)");
        }
        
        List<String> rejected = candidateModels.stream()
            .filter(id -> !id.equals(bestModel))
            .collect(Collectors.toList());
        
        return new SelectionResult(bestModel, reasoning.toString(), bestScore, rejected, constraints);
    }
    
    /**
     * Select model for specific task with capability requirements
     */
    public SelectionResult selectForTask(Map<CapabilityProfile.Capability, Double> requiredCapabilities,
                                       SelectionConstraints constraints) {
        List<String> candidateModels = registry.getAllModelIds().stream()
            .filter(modelId -> meetsConstraints(modelId, constraints))
            .collect(Collectors.toList());
        
        if (candidateModels.isEmpty()) {
            return new SelectionResult(null,
                "No models meet constraints: " + constraints.toString(),
                0.0, Collections.emptyList(), constraints);
        }
        
        // Score each candidate based on required capabilities
        Map<String, Double> scores = new HashMap<>();
        for (String modelId : candidateModels) {
            CapabilityProfile profile = registry.getProfile(modelId);
            if (profile != null) {
                double score = 0.0;
                double totalWeight = 0.0;
                for (Map.Entry<CapabilityProfile.Capability, Double> req : requiredCapabilities.entrySet()) {
                    double modelScore = profile.getScore(req.getKey());
                    double weight = req.getValue();
                    score += modelScore * weight;
                    totalWeight += weight;
                }
                scores.put(modelId, totalWeight > 0 ? score / totalWeight : 0.0);
            }
        }
        
        String bestModel = scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        double bestScore = scores.getOrDefault(bestModel, 0.0);
        
        String reasoning = String.format("Selected %s with task score %.2f", bestModel, bestScore);
        List<String> rejected = candidateModels.stream()
            .filter(id -> !id.equals(bestModel))
            .collect(Collectors.toList());
        
        return new SelectionResult(bestModel, reasoning, bestScore, rejected, constraints);
    }
    
    /**
     * Select best reasoning model
     */
    public SelectionResult selectReasoningModel() {
        SelectionConstraints constraints = new SelectionConstraints()
            .minSpeedTier(ModelDescriptor.SpeedTier.STANDARD);
        
        List<String> candidates = registry.findReasoningModels();
        if (candidates.isEmpty()) {
            return new SelectionResult(null, "No reasoning models available", 0.0,
                Collections.emptyList(), constraints);
        }
        
        String best = candidates.stream()
            .max(Comparator.comparing(id -> registry.getProfile(id).getScore(
                CapabilityProfile.Capability.REASONING)))
            .orElse(null);
        
        double score = registry.getProfile(best).getScore(CapabilityProfile.Capability.REASONING);
        String reasoning = String.format("Selected %s as reasoning model (score: %.2f)", best, score);
        
        return new SelectionResult(best, reasoning, score, 
            candidates.stream().filter(id -> !id.equals(best)).collect(Collectors.toList()),
            constraints);
    }
    
    /**
     * Select best reflex/flash model
     */
    public SelectionResult selectReflexModel() {
        SelectionConstraints constraints = new SelectionConstraints()
            .minSpeedTier(ModelDescriptor.SpeedTier.FLASH)
            .requireLocal(true);
        
        List<String> candidates = registry.findReflexModels().stream()
            .filter(id -> registry.getDescriptor(id).isLocalFirst())
            .collect(Collectors.toList());
        
        if (candidates.isEmpty()) {
            return new SelectionResult(null, "No local reflex models available", 0.0,
                Collections.emptyList(), constraints);
        }
        
        String best = candidates.stream()
            .max(Comparator.comparing(id -> registry.getProfile(id).getScore(
                CapabilityProfile.Capability.REFLEX_SPEED)))
            .orElse(null);
        
        double score = registry.getProfile(best).getScore(CapabilityProfile.Capability.REFLEX_SPEED);
        String reasoning = String.format("Selected %s as reflex model (score: %.2f)", best, score);
        
        return new SelectionResult(best, reasoning, score,
            candidates.stream().filter(id -> !id.equals(best)).collect(Collectors.toList()),
            constraints);
    }
    
    /**
     * Select best coding model
     */
    public SelectionResult selectCodingModel() {
        SelectionConstraints constraints = new SelectionConstraints();
        
        List<String> candidates = registry.findCodingModels();
        if (candidates.isEmpty()) {
            return new SelectionResult(null, "No coding models available", 0.0,
                Collections.emptyList(), constraints);
        }
        
        String best = candidates.stream()
            .max(Comparator.comparing(id -> registry.getProfile(id).getScore(
                CapabilityProfile.Capability.CODING)))
            .orElse(null);
        
        double score = registry.getProfile(best).getScore(CapabilityProfile.Capability.CODING);
        String reasoning = String.format("Selected %s as coding model (score: %.2f)", best, score);
        
        return new SelectionResult(best, reasoning, score,
            candidates.stream().filter(id -> !id.equals(best)).collect(Collectors.toList()),
            constraints);
    }
    
    /**
     * Select best long-context model for memory tasks
     */
    public SelectionResult selectLongContextModel(int requiredContext) {
        SelectionConstraints constraints = new SelectionConstraints()
            .maxContextTokens(requiredContext)
            .requireLongContext(true);
        
        List<String> candidates = registry.findLongContextModels().stream()
            .filter(id -> registry.getDescriptor(id).getContextWindow() >= requiredContext)
            .collect(Collectors.toList());
        
        if (candidates.isEmpty()) {
            return new SelectionResult(null,
                String.format("No models with %d context available", requiredContext),
                0.0, Collections.emptyList(), constraints);
        }
        
        // Prefer local models, then smallest sufficient context
        String best = candidates.stream()
            .sorted(Comparator.comparing((String id) -> {
                ModelDescriptor desc = registry.getDescriptor(id);
                int localPenalty = desc.isLocalFirst() ? 0 : 1000;
                int contextPenalty = desc.getContextWindow();
                return localPenalty + contextPenalty;
            }))
            .findFirst()
            .orElse(null);
        
        int context = registry.getDescriptor(best).getContextWindow();
        String reasoning = String.format("Selected %s with %d context (required: %d)", 
            best, context, requiredContext);
        
        return new SelectionResult(best, reasoning, 1.0,
            candidates.stream().filter(id -> !id.equals(best)).collect(Collectors.toList()),
            constraints);
    }
    
    /**
     * Check if model meets selection constraints
     */
    private boolean meetsConstraints(String modelId, SelectionConstraints constraints) {
        ModelDescriptor desc = registry.getDescriptor(modelId);
        if (desc == null) return false;
        
        if (constraints.isRequireLocal() && !desc.isLocalFirst()) return false;
        if (constraints.isRequireLongContext() && !desc.isLongContext()) return false;
        if (desc.getContextWindow() > constraints.getMaxContextTokens()) return false;
        if (desc.getParameterCount() > constraints.getMaxParameters()) return false;
        
        // Speed tier check (FLASH < DELIBERATE < REASONING < STANDARD)
        if (compareSpeedTier(desc.getSpeedTier(), constraints.getMinSpeedTier()) < 0) return false;
        
        // Cost tier check (FREE < LOW < MEDIUM < HIGH < PREMIUM)
        if (compareCostTier(desc.getCostTier(), constraints.getMaxCostTier()) > 0) return false;
        
        // Backend requirements
        for (ModelDescriptor.Backend requiredBackend : constraints.getRequiredBackends()) {
            if (!desc.supportsBackend(requiredBackend)) return false;
        }
        
        return true;
    }
    
    private int compareSpeedTier(ModelDescriptor.SpeedTier a, ModelDescriptor.SpeedTier b) {
        ModelDescriptor.SpeedTier[] order = {
            ModelDescriptor.SpeedTier.FLASH,
            ModelDescriptor.SpeedTier.DELIBERATE,
            ModelDescriptor.SpeedTier.REASONING,
            ModelDescriptor.SpeedTier.STANDARD
        };
        int aIdx = Arrays.asList(order).indexOf(a);
        int bIdx = Arrays.asList(order).indexOf(b);
        return Integer.compare(aIdx, bIdx);
    }
    
    private int compareCostTier(ModelDescriptor.CostTier a, ModelDescriptor.CostTier b) {
        ModelDescriptor.CostTier[] order = {
            ModelDescriptor.CostTier.FREE,
            ModelDescriptor.CostTier.LOW,
            ModelDescriptor.CostTier.MEDIUM,
            ModelDescriptor.CostTier.HIGH,
            ModelDescriptor.CostTier.PREMIUM
        };
        int aIdx = Arrays.asList(order).indexOf(a);
        int bIdx = Arrays.asList(order).indexOf(b);
        return Integer.compare(aIdx, bIdx);
    }
    
    /**
     * Set global selection constraints
     */
    public void setGlobalConstraints(SelectionConstraints constraints) {
        this.globalConstraints = constraints;
    }
    
    /**
     * Get global constraints
     */
    public SelectionConstraints getGlobalConstraints() {
        return globalConstraints;
    }
    
    /**
     * Register custom selection policy for an organ
     */
    public void registerCustomPolicy(CapabilityProfile.Organ organ, SelectionPolicy policy) {
        customPolicies.put(organ.name(), policy);
    }
    
    /**
     * Custom selection policy interface
     */
    public interface SelectionPolicy {
        String selectModel(CapabilityProfile.Organ organ, ModelRegistry registry, 
                          SelectionConstraints constraints);
    }
}
