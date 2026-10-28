package fraymus.llm;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Seed registry derived from the open-model families visible in open-llms.pdf
 * and the open_model_AI skill source artifact lane.
 * This is an abstraction surface, not a claim that every family is locally installed.
 */
public final class OpenModelRegistry {
    private final List<ModelDescriptor> descriptors;
    private List<ModelDescriptor> observed = List.of();
    private String catalogSignature = "";

    public OpenModelRegistry() {
        this.descriptors = List.copyOf(seedDescriptors());
    }

    /** Non-chat specialists expose typed contracts and explicit availability. */
    public List<java.util.Map<String, Object>> specialists() throws java.io.IOException {
        return new SpecialistModelRegistry().all();
    }

    public synchronized List<ModelDescriptor> all() {
        java.nio.file.Path path = LocalModelCatalog.defaultPath();
        try {
            String signature = path.toAbsolutePath() + ":" + (java.nio.file.Files.isRegularFile(path)
                ? java.nio.file.Files.getLastModifiedTime(path).toString() + ":" + java.nio.file.Files.size(path)
                : "absent");
            if (!signature.equals(catalogSignature)) {
                observed = LocalModelCatalog.load(path).stream().map(this::withExistingPriors).toList();
                catalogSignature = signature;
            }
        } catch (Exception e) {
            observed = List.of(); // An invalid catalog must not leave stale installed-model claims.
        }
        List<ModelDescriptor> merged = new ArrayList<>(observed);
        merged.addAll(descriptors);
        return List.copyOf(merged);
    }

    public Optional<ModelDescriptor> findByAlias(String alias) {
        // Keep namespaces and tags distinct for observed custom models (3b, 8b, live, etc.).
        Optional<ModelDescriptor> exact = all().stream()
            .filter(descriptor -> descriptor.variant().equalsIgnoreCase(alias == null ? "" : alias.trim()))
            .findFirst();
        if (exact.isPresent()) return exact;
        String normalized = ModelDescriptor.normalizeName(alias);
        if (normalized.isBlank()) {
            return Optional.empty();
        }
        return descriptors.stream()
            .filter(descriptor -> descriptor.matchScore(normalized) > 0.0)
            .max((left, right) -> Double.compare(left.matchScore(normalized), right.matchScore(normalized)));
    }

    public List<ModelDescriptor> forRole(ModelRole role, String hint) {
        List<ModelDescriptor> ranked = new ArrayList<>(all());
        ranked.sort((left, right) -> Double.compare(
            right.profile().scoreFor(role, hint),
            left.profile().scoreFor(role, hint)
        ));
        return ranked;
    }

    public String summarizeFamilies() {
        Set<String> families = new LinkedHashSet<>();
        for (ModelDescriptor descriptor : all()) {
            families.add(descriptor.family());
        }
        return String.join(", ", families);
    }

    private ModelDescriptor withExistingPriors(ModelDescriptor local) {
        Optional<ModelDescriptor> known = descriptors.stream()
            .filter(seed -> seed.matchesName(local.variant()))
            .max((a, b) -> Double.compare(a.matchScore(local.variant()), b.matchScore(local.variant())));
        if (known.isEmpty()) return local;
        // Installed metadata augments existing routing priors; it does not erase them or prove quality.
        CapabilityProfile prior = known.get().profile();
        CapabilityProfile observed = local.profile();
        CapabilityProfile profile = new CapabilityProfile(prior.reasoning(), prior.coding(),
            observed.longContext(), prior.reflexSpeed(), prior.creativity(), prior.multilingual(),
            Math.max(prior.toolUse(), observed.toolUse()), observed.multimodal(), observed.localFit());
        return new ModelDescriptor(local.id(), local.family(), local.variant(), local.contextTokens(),
            local.license(), local.modality(), local.backends(), local.quantizationHint(), local.speedTier(),
            local.costTier(), profile, local.aliases());
    }

    private static List<ModelDescriptor> seedDescriptors() {
        List<ModelDescriptor> seeded = new ArrayList<>();

        seeded.add(descriptor(
            "glm-5", "GLM", "GLM-5", 200_000, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.OPENAI_COMPATIBLE),
            "server-optimized", "medium", "medium",
            new CapabilityProfile(0.90, 0.76, 0.78, 0.55, 0.58, 0.82, 0.74, 0.18, 0.44),
            "glm5", "glm", "reasoning", "thinking"
        ));
        seeded.add(descriptor(
            "kimi-k2.6", "Kimi", "Kimi K2.6", 262_100, "Open", "text",
            EnumSet.of(ModelBackend.OPENAI_COMPATIBLE, ModelBackend.REMOTE_ONLY),
            "provider-managed", "medium", "medium",
            new CapabilityProfile(0.92, 0.78, 0.84, 0.57, 0.59, 0.74, 0.76, 0.14, 0.28),
            "kimi", "kimi-k2.5", "kimi-k2.6", "thinking"
        ));
        seeded.add(descriptor(
            "qwen3.5-397b-a17b", "Qwen", "Qwen3.5-397B-A17B", 262_100, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.GGUF_DIRECT, ModelBackend.OPENAI_COMPATIBLE),
            "quantizable", "medium", "medium",
            new CapabilityProfile(0.91, 0.84, 0.83, 0.60, 0.61, 0.86, 0.82, 0.20, 0.56),
            "qwen3.5", "qwen", "reasoning", "large"
        ));
        seeded.add(descriptor(
            "qwen3-coder", "Qwen", "Qwen3-Coder", 256_000, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.GGUF_DIRECT, ModelBackend.OPENAI_COMPATIBLE),
            "quantizable", "fast", "cheap",
            new CapabilityProfile(0.83, 0.95, 0.72, 0.68, 0.42, 0.80, 0.90, 0.10, 0.72),
            "qwen3-coder", "coder", "code", "qwen-coder"
        ));
        seeded.add(descriptor(
            "deepseek-v3.2", "DeepSeek", "DeepSeek-V3.2", 204_800, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.GGUF_DIRECT, ModelBackend.OPENAI_COMPATIBLE),
            "quantizable", "fast", "cheap",
            new CapabilityProfile(0.90, 0.92, 0.78, 0.67, 0.48, 0.78, 0.86, 0.12, 0.62),
            "deepseek", "deepseek-v3", "deepseek-v3.1", "deepseek-v3.2", "deepseek-v4", "speciale", "exp"
        ));
        seeded.add(descriptor(
            "deepseek-r1", "DeepSeek", "DeepSeek-R1", 164_000, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.GGUF_DIRECT, ModelBackend.OPENAI_COMPATIBLE),
            "quantizable", "medium", "cheap",
            new CapabilityProfile(0.95, 0.88, 0.80, 0.58, 0.52, 0.80, 0.84, 0.12, 0.54),
            "deepseek-r1", "deepseek-r1-lite", "r1", "deepseek-reasoning", "deepseek-v4-pro"
        ));
        seeded.add(descriptor(
            "minimax-m2.5", "MiniMax", "MiniMax M2.5", 1_000_000, "Open", "text",
            EnumSet.of(ModelBackend.OPENAI_COMPATIBLE, ModelBackend.REMOTE_ONLY),
            "provider-managed", "fast", "cheap",
            new CapabilityProfile(0.84, 0.70, 0.98, 0.82, 0.50, 0.70, 0.66, 0.16, 0.26),
            "minimax", "m2.5", "m2", "1m-context", "long-context"
        ));
        seeded.add(descriptor(
            "gemma-4-26b-a4b", "Gemma", "Gemma 4 26B-A4B", 262_100, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.GGUF_DIRECT),
            "local-friendly", "fast", "cheap",
            new CapabilityProfile(0.78, 0.74, 0.71, 0.74, 0.60, 0.74, 0.62, 0.15, 0.90),
            "gemma", "gemma-4", "26b", "local"
        ));
        seeded.add(descriptor(
            "gemma-4-31b", "Gemma", "Gemma 4 31B", 262_100, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.GGUF_DIRECT),
            "local-friendly", "medium", "cheap",
            new CapabilityProfile(0.80, 0.76, 0.72, 0.66, 0.62, 0.74, 0.64, 0.15, 0.88),
            "gemma-4-31b", "31b", "gemma"
        ));
        seeded.add(descriptor(
            "mistral-large-3", "Mistral", "Mistral Large 3", 262_100, "Open", "text",
            EnumSet.of(ModelBackend.OPENAI_COMPATIBLE, ModelBackend.REMOTE_ONLY),
            "provider-managed", "medium", "medium",
            new CapabilityProfile(0.88, 0.82, 0.79, 0.56, 0.58, 0.78, 0.78, 0.18, 0.34),
            "mistral", "mistral-large-3", "675b"
        ));
        seeded.add(descriptor(
            "gpt-oss-20b", "GPT OSS", "GPT OSS 20B", 131_100, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.GGUF_DIRECT),
            "local-friendly", "fast", "cheap",
            new CapabilityProfile(0.76, 0.88, 0.58, 0.75, 0.45, 0.68, 0.80, 0.10, 0.92),
            "gpt-oss", "gpt oss", "20b", "local"
        ));
        seeded.add(descriptor(
            "longcat-flash-thinking", "LongCat", "LongCat-Flash-Thinking", 1_000_000, "Open", "text",
            EnumSet.of(ModelBackend.OPENAI_COMPATIBLE, ModelBackend.REMOTE_ONLY),
            "provider-managed", "fast", "cheap",
            new CapabilityProfile(0.81, 0.68, 0.94, 0.88, 0.50, 0.66, 0.62, 0.10, 0.24),
            "longcat", "flash-thinking", "flash", "long-context"
        ));
        seeded.add(descriptor(
            "longcat-flash-chat", "LongCat", "LongCat-Flash-Chat", 128_000, "Open", "text",
            EnumSet.of(ModelBackend.OPENAI_COMPATIBLE, ModelBackend.REMOTE_ONLY),
            "provider-managed", "fast", "cheap",
            new CapabilityProfile(0.68, 0.58, 0.68, 0.92, 0.54, 0.66, 0.54, 0.10, 0.24),
            "longcat-flash-chat", "flash-chat", "flash"
        ));
        seeded.add(descriptor(
            "mimo-v2-flash", "MiMo", "MiMo-V2-Flash", 256_000, "Open", "text",
            EnumSet.of(ModelBackend.OPENAI_COMPATIBLE, ModelBackend.REMOTE_ONLY),
            "provider-managed", "fast", "cheap",
            new CapabilityProfile(0.70, 0.60, 0.72, 0.93, 0.50, 0.62, 0.52, 0.10, 0.20),
            "mimo", "mimo-v2-flash", "flash"
        ));
        seeded.add(descriptor(
            "step-3.5-flash", "Step", "Step-3.5-Flash", 65_500, "Open", "text",
            EnumSet.of(ModelBackend.OPENAI_COMPATIBLE, ModelBackend.REMOTE_ONLY),
            "provider-managed", "very-fast", "cheap",
            new CapabilityProfile(0.64, 0.56, 0.40, 0.96, 0.42, 0.58, 0.48, 0.08, 0.18),
            "step", "step-3.5-flash", "flash"
        ));
        seeded.add(descriptor(
            "glm-4.7-flash", "GLM", "GLM-4.7-Flash", 128_000, "Open", "text",
            EnumSet.of(ModelBackend.OLLAMA, ModelBackend.OPENAI_COMPATIBLE),
            "quantizable", "very-fast", "cheap",
            new CapabilityProfile(0.68, 0.58, 0.55, 0.94, 0.46, 0.72, 0.56, 0.10, 0.48),
            "glm-4.7-flash", "glm-flash", "flash"
        ));
        seeded.add(descriptor(
            "minimax-m2.7", "MiniMax", "MiniMax M2.7", 262_100, "Open", "text",
            EnumSet.of(ModelBackend.OPENAI_COMPATIBLE, ModelBackend.REMOTE_ONLY),
            "provider-managed", "medium", "cheap",
            new CapabilityProfile(0.82, 0.68, 0.86, 0.80, 0.52, 0.70, 0.62, 0.16, 0.26),
            "minimax-m2.7", "m2.7", "minimax"
        ));

        return seeded;
    }

    private static ModelDescriptor descriptor(
        String id,
        String family,
        String variant,
        int contextTokens,
        String license,
        String modality,
        Set<ModelBackend> backends,
        String quantizationHint,
        String speedTier,
        String costTier,
        CapabilityProfile profile,
        String... aliases
    ) {
        Set<String> aliasSet = new LinkedHashSet<>();
        aliasSet.add(id);
        aliasSet.add(family.toLowerCase(Locale.ROOT));
        aliasSet.add(variant.toLowerCase(Locale.ROOT));
        for (String alias : aliases) {
            aliasSet.add(alias);
        }
        return new ModelDescriptor(
            id,
            family,
            variant,
            contextTokens,
            license,
            modality,
            backends,
            quantizationHint,
            speedTier,
            costTier,
            profile,
            aliasSet
        );
    }
}
