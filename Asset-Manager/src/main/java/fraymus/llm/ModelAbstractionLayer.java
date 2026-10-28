package fraymus.llm;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Fraynix model abstraction layer.
 * Organs request capabilities; this layer resolves interchangeable models.
 */
public final class ModelAbstractionLayer {
    private static final ModelAbstractionLayer DEFAULT = new ModelAbstractionLayer(new OpenModelRegistry());

    private final OpenModelRegistry registry;
    private final ModelSelectionPolicy selectionPolicy;

    public ModelAbstractionLayer(OpenModelRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.selectionPolicy = new ModelSelectionPolicy(registry);
    }

    public static ModelAbstractionLayer defaultLayer() {
        return DEFAULT;
    }

    public OpenModelRegistry registry() {
        return registry;
    }

    public Optional<ModelSelectionResult> selectForOrgan(String organOrBrain, String taskHint, Collection<String> availableModels) {
        ModelRole role = ModelRole.fromOrgan(organOrBrain);
        return selectionPolicy.bestCandidate(role, taskHint, availableModels);
    }

    public List<ModelSelectionResult> rankForRole(ModelRole role, String taskHint, Collection<String> availableModels, int limit) {
        return selectionPolicy.rankCandidates(role, taskHint, availableModels, limit);
    }

    public String resolvePreferredModel(String preferredModelOrRole, Collection<String> availableModels) {
        if (availableModels == null || availableModels.isEmpty()) {
            return preferredModelOrRole;
        }

        String preferred = preferredModelOrRole == null ? "" : preferredModelOrRole.trim();
        if (preferred.isBlank()) {
            return selectionPolicy.bestCandidate(ModelRole.REASONING, "", availableModels)
                .map(ModelSelectionResult::resolvedModelName)
                .orElse("");
        }

        String normalizedPreferred = ModelDescriptor.normalizeName(preferred);

        for (String availableModel : availableModels) {
            if (availableModel.equalsIgnoreCase(preferred)) {
                return availableModel;
            }
        }
        for (String availableModel : availableModels) {
            if (ModelDescriptor.normalizeName(availableModel).equals(normalizedPreferred)) {
                return availableModel;
            }
        }

        Optional<ModelRole> role = ModelRole.fromHint(preferred);
        if (role.isPresent()) {
            return selectionPolicy.bestCandidate(role.get(), preferred, availableModels)
                .map(ModelSelectionResult::resolvedModelName)
                .orElse(preferred);
        }

        Optional<ModelDescriptor> explicitDescriptor = registry.findByAlias(preferred);
        if (explicitDescriptor.isPresent()) {
            for (String availableModel : availableModels) {
                if (explicitDescriptor.get().matchesName(availableModel)) {
                    return availableModel;
                }
            }
        }

        String lowerPreferred = preferred.toLowerCase(Locale.ROOT);
        if (lowerPreferred.contains("context") || lowerPreferred.contains("memory")) {
            return selectionPolicy.bestCandidate(ModelRole.MEMORY, preferred, availableModels)
                .map(ModelSelectionResult::resolvedModelName)
                .orElse(preferred);
        }
        if (lowerPreferred.contains("creative") || lowerPreferred.contains("humor")) {
            return selectionPolicy.bestCandidate(ModelRole.CREATIVE, preferred, availableModels)
                .map(ModelSelectionResult::resolvedModelName)
                .orElse(preferred);
        }

        return preferred;
    }
}
