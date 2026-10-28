package fraymus.brain.unified;

import fraymus.model.CapabilityProfile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * OrganBrainMapper
 *
 * Maps capability organs to concrete unified-brain adapters already registered
 * in the orchestrator. This keeps organ-driven model selection grounded in the
 * actual local brain runtime surfaces.
 */
public class OrganBrainMapper {

    private final Map<CapabilityProfile.Organ, List<String>> preferredBrainsByOrgan;

    public OrganBrainMapper() {
        this.preferredBrainsByOrgan = new EnumMap<>(CapabilityProfile.Organ.class);
        initializeDefaults();
    }

    public List<String> getPreferredBrainNames(CapabilityProfile.Organ organ) {
        if (organ == null) {
            return Collections.emptyList();
        }
        return preferredBrainsByOrgan.getOrDefault(organ, Collections.emptyList());
    }

    public List<UnifiedBrain> resolveBrains(CapabilityProfile.Organ organ, Collection<UnifiedBrain> registeredBrains) {
        if (organ == null || registeredBrains == null || registeredBrains.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> aliases = getPreferredBrainNames(organ);
        Map<String, UnifiedBrain> ordered = new LinkedHashMap<>();

        for (String alias : aliases) {
            String aliasNorm = normalize(alias);
            for (UnifiedBrain brain : registeredBrains) {
                if (brain == null || brain.getName() == null) {
                    continue;
                }
                String nameNorm = normalize(brain.getName());
                if (nameNorm.equals(aliasNorm)) {
                    ordered.putIfAbsent(brain.getName(), brain);
                }
            }
            for (UnifiedBrain brain : registeredBrains) {
                if (brain == null || brain.getName() == null) {
                    continue;
                }
                String nameNorm = normalize(brain.getName());
                if (nameNorm.contains(aliasNorm)) {
                    ordered.putIfAbsent(brain.getName(), brain);
                }
            }
        }

        if (!ordered.isEmpty()) {
            return new ArrayList<>(ordered.values());
        }

        // Fallback capability-based resolution if name aliases are unavailable.
        for (UnifiedBrain brain : registeredBrains) {
            if (brain == null) {
                continue;
            }
            List<BrainCapability> caps = brain.getCapabilities();
            if (caps == null || caps.isEmpty()) {
                continue;
            }
            if (matchesOrganByCapability(organ, caps)) {
                ordered.putIfAbsent(brain.getName(), brain);
            }
        }

        return new ArrayList<>(ordered.values());
    }

    private boolean matchesOrganByCapability(CapabilityProfile.Organ organ, List<BrainCapability> caps) {
        return switch (organ) {
            case PREFRONTAL_CORTEX -> caps.contains(BrainCapability.REASONING)
                || caps.contains(BrainCapability.MATHEMATICS)
                || caps.contains(BrainCapability.CONSCIOUSNESS);
            case SPINAL_CORD -> caps.contains(BrainCapability.TACHYONIC)
                || caps.contains(BrainCapability.DATA);
            case CODING_ORGAN -> caps.contains(BrainCapability.PHYSICS)
                || caps.contains(BrainCapability.DATA)
                || caps.contains(BrainCapability.REASONING);
            case HIPPOCAMPUS -> caps.contains(BrainCapability.MEMORY)
                || caps.contains(BrainCapability.TEMPORAL);
            case CREATIVE_CORTEX -> caps.contains(BrainCapability.CREATIVITY)
                || caps.contains(BrainCapability.EMOTION)
                || caps.contains(BrainCapability.SPIRITUAL);
            case MULTIMODAL_ENGINE -> caps.contains(BrainCapability.VISION)
                || caps.contains(BrainCapability.AUDIO);
            case TRANSLATION_ENGINE -> caps.contains(BrainCapability.LANGUAGE);
            case PARIETAL_LOBE -> caps.contains(BrainCapability.DATA)
                || caps.contains(BrainCapability.VISION)
                || caps.contains(BrainCapability.AUDIO);
        };
    }

    private void initializeDefaults() {
        preferredBrainsByOrgan.put(
            CapabilityProfile.Organ.PREFRONTAL_CORTEX,
            List.of("LogicEngine", "BrainArchitecture", "NexusOmega", "AEONPlatform", "FraymusComplete", "AeonCore")
        );
        preferredBrainsByOrgan.put(
            CapabilityProfile.Organ.SPINAL_CORD,
            List.of("AuboLedger", "AeonTachyon", "PremotorCortex", "SemanticRouter")
        );
        preferredBrainsByOrgan.put(
            CapabilityProfile.Organ.CODING_ORGAN,
            List.of("TransformationEngine", "FraymusComplete", "BrainArchitecture", "LogicEngine")
        );
        preferredBrainsByOrgan.put(
            CapabilityProfile.Organ.HIPPOCAMPUS,
            List.of("Hippocampus", "EntorhinalCortex", "BrainArchitecture", "FraymusComplete", "AeonKronos")
        );
        preferredBrainsByOrgan.put(
            CapabilityProfile.Organ.CREATIVE_CORTEX,
            List.of("HumorCreative", "DreamEngine", "FraymusComplete", "BrainArchitecture", "NexusOmega")
        );
        preferredBrainsByOrgan.put(
            CapabilityProfile.Organ.MULTIMODAL_ENGINE,
            List.of("CrossModalEngine", "VisionEngine", "AudioEngine", "HyperCortex")
        );
        preferredBrainsByOrgan.put(
            CapabilityProfile.Organ.TRANSLATION_ENGINE,
            List.of("AeonBabel", "FraymusComplete", "BrainArchitecture")
        );
        preferredBrainsByOrgan.put(
            CapabilityProfile.Organ.PARIETAL_LOBE,
            List.of("ParietalLobe", "BrainArchitecture", "NexusOmega", "LogicEngine")
        );
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value
            .toLowerCase(Locale.ROOT)
            .replace("_", "")
            .replace("-", "")
            .replace(" ", "");
    }
}
