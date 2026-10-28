package fraymus.llm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Task and organ aware ranking over the open-model abstraction layer.
 */
public final class ModelSelectionPolicy {
    private final OpenModelRegistry registry;

    public ModelSelectionPolicy(OpenModelRegistry registry) {
        this.registry = registry;
    }

    public List<ModelSelectionResult> rankCandidates(ModelRole role, String taskHint, Collection<String> availableModels, int limit) {
        List<ModelSelectionResult> ranked = new ArrayList<>();

        if (availableModels != null && !availableModels.isEmpty()) {
            for (String availableModel : availableModels) {
                Optional<ModelDescriptor> descriptor = registry.findByAlias(availableModel);
                if (descriptor.isPresent()) {
                    ModelDescriptor match = descriptor.get();
                    double score = match.profile().scoreFor(role, taskHint);
                    ranked.add(new ModelSelectionResult(
                        role,
                        taskHint,
                        availableModel,
                        match.id(),
                        match.family(),
                        "observed-local",
                        score,
                        rationale(match, role, taskHint, "local-match")
                    ));
                } else {
                    ranked.add(new ModelSelectionResult(
                        role,
                        taskHint,
                        availableModel,
                        "unclassified-local",
                        "UnknownLocal",
                        "observed-local",
                        heuristicUnknownScore(availableModel, role, taskHint),
                        "Local model is available but not yet classified in the registry."
                    ));
                }
            }
        } else {
            for (ModelDescriptor descriptor : registry.forRole(role, taskHint)) {
                ranked.add(new ModelSelectionResult(
                    role,
                    taskHint,
                    descriptor.variant(),
                    descriptor.id(),
                    descriptor.family(),
                    "registry-planned",
                    descriptor.profile().scoreFor(role, taskHint),
                    rationale(descriptor, role, taskHint, "registry-seed")
                ));
            }
        }

        ranked.sort(Comparator.comparingDouble(ModelSelectionResult::score).reversed());
        if (limit > 0 && ranked.size() > limit) {
            return new ArrayList<>(ranked.subList(0, limit));
        }
        return ranked;
    }

    public Optional<ModelSelectionResult> bestCandidate(ModelRole role, String taskHint, Collection<String> availableModels) {
        return rankCandidates(role, taskHint, availableModels, 1).stream().findFirst();
    }

    private static double heuristicUnknownScore(String modelName, ModelRole role, String taskHint) {
        String normalized = ModelDescriptor.normalizeName(modelName);
        double score = 0.40;
        if (normalized.contains("deepseek")) {
            score += role == ModelRole.REASONING ? 0.36 : 0.12;
            score += role == ModelRole.CODING ? 0.28 : 0.08;
        }
        if (normalized.contains("qwen")) {
            score += role == ModelRole.CODING ? 0.30 : 0.10;
            score += role == ModelRole.REASONING ? 0.18 : 0.06;
        }
        if (normalized.contains("coder")) score += role == ModelRole.CODING ? 0.40 : 0.12;
        if (normalized.contains("flash")) score += role == ModelRole.REFLEX ? 0.38 : 0.08;
        if (normalized.contains("think")) score += role == ModelRole.REASONING ? 0.30 : 0.04;
        if (normalized.contains("long") || normalized.contains("1m")) score += role == ModelRole.MEMORY ? 0.34 : 0.05;
        if (normalized.contains("gemma") || normalized.contains("gpt-oss")) score += role == ModelRole.CREATIVE ? 0.10 : 0.06;
        if (taskHint != null && !taskHint.isBlank()) {
            CapabilityProfile generic = new CapabilityProfile(0.65, 0.65, 0.65, 0.65, 0.55, 0.55, 0.60, 0.10, 0.70);
            score += generic.scoreFor(role, taskHint) * 0.10;
        }
        return Math.max(0.0, Math.min(0.99, score));
    }

    private static String rationale(ModelDescriptor descriptor, ModelRole role, String taskHint, String source) {
        return "Selected via " + source
            + " for role=" + role
            + " using family=" + descriptor.family()
            + ", variant=" + descriptor.variant()
            + ", context=" + descriptor.contextTokens()
            + ", speedTier=" + descriptor.speedTier()
            + ", costTier=" + descriptor.costTier()
            + (taskHint == null || taskHint.isBlank() ? "" : ", taskHint=" + taskHint);
    }
}
