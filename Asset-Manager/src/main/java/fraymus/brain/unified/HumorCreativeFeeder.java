package fraymus.brain.unified;

import fraymus.knowledge.AkashicRecord;
import fraymus.model.AkashicModelMemory;
import fraymus.model.CapabilityProfile;
import fraymus.model.EvaluationHarness;
import fraymus.model.ModelRegistry;
import fraymus.model.ModelRegistryIntegration;
import fraymus.model.PromptCompiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class HumorCreativeFeeder {
    private static final List<String> AKASHIC_FEED_CATEGORIES = Arrays.asList(
        "WEBCORTEX",
        "OPENCLAW_ACTION",
        "OPENCLAW_REPAIR"
    );
    private static final String ABSTRACTED_ARTIFACT_CATEGORY = "HUMOR_ABSTRACTED_ARTIFACT";
    private static final String ABSTRACTED_PROVENANCE_CATEGORY = "HUMOR_ABSTRACTED_PROVENANCE";
    private static final String CAPABILITY_SIGNAL_CATEGORY = "HUMOR_CAPABILITY_SIGNAL";
    private static final String TASK_HINT_CATEGORY = "HUMOR_GENERATED_TASK_HINT";
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");

    private HumorCreativeFeeder() {
    }

    public static boolean isHumorCreativePrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return false;
        }
        String lowered = prompt.toLowerCase(Locale.ROOT);
        return lowered.contains("funny")
            || lowered.contains("humor")
            || lowered.contains("creative")
            || lowered.contains("punchline")
            || lowered.contains("remix")
            || lowered.contains("make this hit")
            || lowered.contains("make it land");
    }

    public static void enrichContext(String prompt, Map<String, Object> context, AkashicRecord akashic) {
        if (context == null || !isHumorCreativePrompt(prompt)) {
            return;
        }

        LinkedHashSet<String> absorbedFragments = new LinkedHashSet<>();
        LinkedHashSet<String> sourceFragments = new LinkedHashSet<>();

        collectAkashicFragments(prompt, akashic, absorbedFragments);
        collectExocortexFragments(prompt, sourceFragments);
        collectPromptUrls(prompt, sourceFragments);
        collectRepoAbsorption(prompt, sourceFragments);

        List<DataAbstractionEngine.AbstractedArtifact> abstractedArtifacts =
            DataAbstractionEngine.abstractPromptSources(prompt, absorbedFragments, sourceFragments);
        Map<CapabilityProfile.Capability, Double> capabilitySignals =
            DataAbstractionEngine.aggregateCapabilitySignals(abstractedArtifacts);
        List<String> taskHints = DataAbstractionEngine.collectTaskHints(abstractedArtifacts, 8);
        List<String> provenanceSignals = DataAbstractionEngine.collectProvenanceSummaries(abstractedArtifacts, 8);

        if (!abstractedArtifacts.isEmpty()) {
            context.put("abstractedArtifacts", DataAbstractionEngine.toContextPayload(abstractedArtifacts, 8));
            addAbstractedSummaries(sourceFragments, abstractedArtifacts);
        }
        if (!capabilitySignals.isEmpty()) {
            context.put("capabilitySignals", convertSignals(capabilitySignals));
        }
        if (!taskHints.isEmpty()) {
            context.put("generatedTaskHints", taskHints);
            context.put("generatedEvaluationTasks", taskHints);
        }
        if (!provenanceSignals.isEmpty()) {
            context.put("provenanceSignals", provenanceSignals);
        }

        persistAbstractionEvidence(akashic, abstractedArtifacts, capabilitySignals, taskHints, provenanceSignals);
        pushSignalsIntoModelMemory(context, capabilitySignals, taskHints);

        if (!absorbedFragments.isEmpty()) {
            context.put("absorbedFragments", new ArrayList<>(absorbedFragments));
        }
        if (!sourceFragments.isEmpty()) {
            context.put("sourceFragments", new ArrayList<>(sourceFragments));
        }
        if (!absorbedFragments.isEmpty() || !sourceFragments.isEmpty()) {
            context.put("creativeFeedMode", "akashi_exocortex_prompt");
        }
    }

    private static void addAbstractedSummaries(Set<String> sourceFragments, List<DataAbstractionEngine.AbstractedArtifact> artifacts) {
        for (DataAbstractionEngine.AbstractedArtifact artifact : artifacts) {
            if (sourceFragments.size() >= 8) {
                break;
            }
            String summary = "abstracted: " + artifact.compactSummary();
            List<String> top = artifact.topCapabilities(2);
            if (!top.isEmpty()) {
                summary += " | " + String.join(", ", top);
            }
            sourceFragments.add(summary);
        }
    }

    private static void persistAbstractionEvidence(
        AkashicRecord akashic,
        List<DataAbstractionEngine.AbstractedArtifact> artifacts,
        Map<CapabilityProfile.Capability, Double> capabilitySignals,
        List<String> taskHints,
        List<String> provenanceSignals
    ) {
        if (akashic == null) {
            return;
        }

        for (DataAbstractionEngine.AbstractedArtifact artifact : artifacts) {
            rememberAkashicBlock(akashic, ABSTRACTED_ARTIFACT_CATEGORY, artifact.compactSummary());
        }
        for (String provenance : provenanceSignals) {
            rememberAkashicBlock(akashic, ABSTRACTED_PROVENANCE_CATEGORY, provenance);
        }
        for (Map.Entry<CapabilityProfile.Capability, Double> signal : capabilitySignals.entrySet()) {
            rememberAkashicBlock(
                akashic,
                CAPABILITY_SIGNAL_CATEGORY,
                signal.getKey().name() + "=" + String.format(Locale.ROOT, "%.3f", signal.getValue())
            );
        }
        for (String hint : taskHints) {
            rememberAkashicBlock(akashic, TASK_HINT_CATEGORY, hint);
        }
    }

    private static boolean rememberAkashicBlock(AkashicRecord akashic, String category, String content) {
        if (akashic == null || category == null || category.isBlank() || content == null || content.isBlank()) {
            return false;
        }
        List<AkashicRecord.KnowledgeBlock> existing = akashic.queryCategory(category);
        for (AkashicRecord.KnowledgeBlock block : existing) {
            if (content.equals(block.content)) {
                return false;
            }
        }
        akashic.addBlock(category, content);
        return true;
    }

    private static void pushSignalsIntoModelMemory(
        Map<String, Object> context,
        Map<CapabilityProfile.Capability, Double> capabilitySignals,
        List<String> taskHints
    ) {
        ModelRegistryIntegration integration = ModelRegistryIntegration.getActiveIntegration();
        if (integration == null || !integration.isInitialized()) {
            return;
        }

        PromptCompiler compiler = integration.getPromptCompiler();
        if (compiler != null) {
            String packed = compiler.packContextForUserPrompt(context, 3);
            if (!packed.isBlank()) {
                context.put("compiledContextPack", packed);
            }
        }

        if (capabilitySignals.isEmpty()) {
            return;
        }

        ModelRegistry registry = integration.getModelRegistry();
        AkashicModelMemory memory = integration.getAkashicModelMemory();
        if (registry != null && memory != null) {
            CapabilityProfile.Capability dominant = dominantCapability(capabilitySignals);
            String dominantModel = dominant == null ? null : bestModelForCapability(registry, dominant);

            for (Map.Entry<CapabilityProfile.Capability, Double> signal : capabilitySignals.entrySet()) {
                String modelId = bestModelForCapability(registry, signal.getKey());
                if (modelId == null) {
                    continue;
                }
                memory.recordCapabilityScore(modelId, signal.getKey().name(), signal.getValue());
                if (signal.getValue() >= 0.18 && !taskHints.isEmpty()) {
                    int hintsToRecord = Math.min(3, taskHints.size());
                    for (int i = 0; i < hintsToRecord; i++) {
                        String hint = taskHints.get(i);
                        String taskId = "artifact." + signal.getKey().name().toLowerCase(Locale.ROOT)
                            + "." + Integer.toHexString((hint + modelId).hashCode());
                        long syntheticLatency = Math.max(20L, Math.round((1.0 - signal.getValue()) * 90.0));
                        memory.recordUsage(modelId, taskId, true, syntheticLatency);
                    }
                }
            }

            if (dominantModel != null) {
                memory.recordSelection(
                    CapabilityProfile.Organ.CREATIVE_CORTEX,
                    dominantModel,
                    "abstraction_signals:" + dominant.name()
                );
                context.put("abstractionSelectedModel", dominantModel);
            }
        }

        EvaluationHarness harness = integration.getEvaluationHarness();
        if (harness != null && !taskHints.isEmpty()) {
            CapabilityProfile.Capability dominant = dominantCapability(capabilitySignals);
            String suiteName = harness.registerGeneratedTaskSuiteFromHints(
                "absorption",
                dominant == null ? CapabilityProfile.Capability.REASONING : dominant,
                taskHints,
                6000
            );
            if (suiteName != null) {
                context.put("generatedEvaluationSuite", suiteName);
            }
        }
    }

    private static CapabilityProfile.Capability dominantCapability(Map<CapabilityProfile.Capability, Double> signals) {
        CapabilityProfile.Capability bestCapability = null;
        double bestScore = -1.0;
        for (Map.Entry<CapabilityProfile.Capability, Double> entry : signals.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestCapability = entry.getKey();
            }
        }
        return bestCapability;
    }

    private static String bestModelForCapability(ModelRegistry registry, CapabilityProfile.Capability capability) {
        if (registry == null || capability == null) {
            return null;
        }
        return registry.getAllModelIds().stream()
            .filter(modelId -> registry.getProfile(modelId) != null)
            .max(Comparator.comparingDouble(modelId -> registry.getProfile(modelId).getScore(capability)))
            .orElse(null);
    }

    private static Map<String, Double> convertSignals(Map<CapabilityProfile.Capability, Double> signals) {
        Map<String, Double> converted = new java.util.LinkedHashMap<>();
        signals.entrySet().stream()
            .sorted(Map.Entry.<CapabilityProfile.Capability, Double>comparingByValue().reversed())
            .forEach(entry -> converted.put(entry.getKey().name(), entry.getValue()));
        return converted;
    }

    private static void collectAkashicFragments(String prompt, AkashicRecord akashic, Set<String> absorbedFragments) {
        if (akashic == null) {
            return;
        }

        String lookup = extractLookupTerm(prompt);
        for (String category : AKASHIC_FEED_CATEGORIES) {
            List<AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(category);
            int added = 0;
            for (int i = blocks.size() - 1; i >= 0 && added < 2 && absorbedFragments.size() < 6; i--) {
                AkashicRecord.KnowledgeBlock block = blocks.get(i);
                if (!matchesLookup(block.content, lookup)) {
                    continue;
                }
                if (absorbedFragments.add(extractFragment(block.content, lookup))) {
                    added++;
                }
            }
        }
    }

    private static void collectExocortexFragments(String prompt, Set<String> sourceFragments) {
        Path extractedRoot = resolveExocortexExtractedRoot();
        if (!Files.isDirectory(extractedRoot)) {
            return;
        }

        String lookup = extractLookupTerm(prompt);
        try {
            List<Path> files = Files.list(extractedRoot)
                .filter(Files::isRegularFile)
                .sorted((left, right) -> right.getFileName().toString().compareToIgnoreCase(left.getFileName().toString()))
                .collect(Collectors.toList());

            for (Path file : files) {
                if (sourceFragments.size() >= 4) {
                    break;
                }
                String text = Files.readString(file);
                if (!matchesLookup(text, lookup) && !file.getFileName().toString().toLowerCase(Locale.ROOT).contains(lookup)) {
                    continue;
                }
                sourceFragments.add(file.getFileName() + ": " + extractFragment(text, lookup));
            }
        } catch (IOException ignored) {
        }
    }

    private static void collectPromptUrls(String prompt, Set<String> sourceFragments) {
        if (prompt == null || prompt.isBlank()) {
            return;
        }
        Matcher matcher = URL_PATTERN.matcher(prompt);
        while (matcher.find() && sourceFragments.size() < 6) {
            String url = matcher.group();
            String scraped = fraymus.brain.unified.ParietalLobeBrain.scrapeUrl(url);
            if (!scraped.isEmpty()) {
                sourceFragments.add("scraped-url: " + url + " -> " + scraped);
            } else {
                sourceFragments.add("observed-url: " + url);
            }
        }
    }

    private static void collectRepoAbsorption(String prompt, Set<String> sourceFragments) {
        if (prompt == null || prompt.isBlank()) {
            return;
        }
        // Known repo keys from ExocortexAbsorber
        String[] knownRepos = {"firecracker", "gvisor", "piper", "tts", "memgraph", "searxng", "playwright"};
        String lowered = prompt.toLowerCase(Locale.ROOT);
        
        for (String repo : knownRepos) {
            if (lowered.contains(repo) && sourceFragments.size() < 4) {
                try {
                    fraymus.exocortex.ExocortexAbsorber absorber = new fraymus.exocortex.ExocortexAbsorber();
                    fraymus.exocortex.ExocortexAbsorber.AbsorptionResult result = absorber.absorb(repo);
                    if (result != null && result.success) {
                        int apiCount = result.extractedAPIs != null ? result.extractedAPIs.size() : 0;
                        String summary = String.format("absorbed-repo: %s [%s] - %d APIs extracted", 
                            result.repositoryName, result.status, apiCount);
                        sourceFragments.add(summary);
                    } else {
                        sourceFragments.add("absorbed-repo: " + repo + " [FAILED: " + (result != null ? result.error : "null") + "]");
                    }
                } catch (Exception e) {
                    sourceFragments.add("absorbed-repo: " + repo + " [ERROR: " + e.getMessage() + "]");
                }
            }
        }
    }

    static Path resolveExocortexExtractedRoot() {
        String override = System.getProperty("fraymus.humorCreative.exocortexExtractedRoot");
        if (override != null && !override.isBlank()) {
            return Paths.get(override);
        }
        return Paths.get(".exocortex", "extracted");
    }

    private static String extractLookupTerm(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return "";
        }
        String normalized = prompt.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
        String best = "";
        for (String token : normalized.split("[^a-z0-9]+")) {
            if (token.length() > best.length() && token.length() >= 4 && !"make".equals(token) && !"funny".equals(token)) {
                best = token;
            }
        }
        return best;
    }

    private static boolean matchesLookup(String content, String lookup) {
        if (content == null || content.isBlank()) {
            return false;
        }
        if (lookup == null || lookup.isBlank()) {
            return true;
        }
        String lowered = content.toLowerCase(Locale.ROOT);
        return lowered.contains(lookup)
            || lowered.contains("group chat")
            || lowered.contains("timeline")
            || lowered.contains("comment")
            || lowered.contains("remix")
            || lowered.contains("creative");
    }

    private static String extractFragment(String content, String lookup) {
        String normalized = content == null ? "" : content.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 140) {
            return normalized;
        }

        int index = lookup == null || lookup.isBlank()
            ? -1
            : normalized.toLowerCase(Locale.ROOT).indexOf(lookup.toLowerCase(Locale.ROOT));
        if (index >= 0) {
            int start = Math.max(0, index - 35);
            int end = Math.min(normalized.length(), index + lookup.length() + 85);
            normalized = normalized.substring(start, end);
        }

        if (normalized.length() > 140) {
            normalized = normalized.substring(0, 137) + "...";
        }
        return normalized;
    }
}
