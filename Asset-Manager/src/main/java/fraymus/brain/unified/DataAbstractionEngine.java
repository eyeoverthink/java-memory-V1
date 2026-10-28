package fraymus.brain.unified;

import fraymus.model.CapabilityProfile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataAbstractionEngine - Converts raw scraped/absorbed fragments into structured cognitive units.
 *
 * RawSource -> AbstractedArtifact + Provenance + Capability signals + Task hints.
 */
public final class DataAbstractionEngine {
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[^.!?]+[.!?]?");
    private static final Pattern ENTITY_PATTERN = Pattern.compile("\\b[A-Z][A-Za-z0-9_\\-]{2,}\\b");
    private static final Pattern CODE_PATTERN = Pattern.compile(
        "`[^`]+`|\\b[a-zA-Z_][a-zA-Z0-9_]*\\([^)]*\\)|\\b[A-Za-z0-9_\\-]+\\.(?:java|rs|py|ts|go|cpp|c|md)\\b"
    );
    private static final int MAX_SUMMARY_CHARS = 220;

    private static final Map<CapabilityProfile.Capability, List<String>> CAPABILITY_KEYWORDS =
        new EnumMap<>(CapabilityProfile.Capability.class);

    static {
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.REASONING,
            List.of("reason", "logic", "proof", "infer", "analysis", "theorem", "equation", "formal")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.CODING,
            List.of("code", "class", "method", "api", "compile", "debug", "refactor", "java", "rust", "script")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.LONG_CONTEXT,
            List.of("context", "repository", "codebase", "memory", "ledger", "continuity", "resume")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.REFLEX_SPEED,
            List.of("fast", "flash", "quick", "low-latency", "latency", "instant")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.CREATIVITY,
            List.of("creative", "humor", "remix", "punchline", "meme", "story", "novel")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.MULTILINGUAL,
            List.of("translate", "multilingual", "language", "bilingual")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.TOOL_USE,
            List.of("tool", "wrapper", "execute", "command", "playwright", "openclaw", "terminal")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.MULTIMODAL,
            List.of("image", "audio", "video", "vision", "voice", "screen", "pdf")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.LOCAL_FIT,
            List.of("local", "offline", "on-device", "gguf", "ollama", "quantized")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.MEMORY_RETENTION,
            List.of("akashic", "persist", "state", "memory", "history", "trace")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.SAFETY_ALIGNMENT,
            List.of("safe", "policy", "amygdala", "guard", "deterministic", "verify")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.PRECISION,
            List.of("exact", "deterministic", "formal", "proof", "metric", "precision")
        );
        CAPABILITY_KEYWORDS.put(
            CapabilityProfile.Capability.EFFICIENCY,
            List.of("efficient", "throughput", "simd", "runtime", "budget", "lightweight")
        );
    }

    private DataAbstractionEngine() {
    }

    public enum SourceType {
        PROMPT,
        AKASHIC,
        EXTRACTED_DOC,
        SCRAPED_URL,
        OBSERVED_URL,
        REPOSITORY_ABSORPTION
    }

    public static final class Provenance {
        private final String sourceId;
        private final String sourceType;
        private final long timestampEpochMs;
        private final String contentHash;
        private final double trustScore;

        public Provenance(String sourceId, String sourceType, long timestampEpochMs, String contentHash, double trustScore) {
            this.sourceId = sourceId;
            this.sourceType = sourceType;
            this.timestampEpochMs = timestampEpochMs;
            this.contentHash = contentHash;
            this.trustScore = trustScore;
        }

        public String sourceId() {
            return sourceId;
        }

        public String sourceType() {
            return sourceType;
        }

        public long timestampEpochMs() {
            return timestampEpochMs;
        }

        public String contentHash() {
            return contentHash;
        }

        public double trustScore() {
            return trustScore;
        }

        public String compact() {
            return sourceType + "|" + sourceId + "|trust=" + String.format(Locale.ROOT, "%.2f", trustScore)
                + "|hash=" + contentHash;
        }
    }

    public static final class AbstractedArtifact {
        private final String sourceId;
        private final SourceType sourceType;
        private final String summary;
        private final List<String> entities;
        private final List<String> claims;
        private final List<String> codePatterns;
        private final List<String> taskHints;
        private final Map<CapabilityProfile.Capability, Double> capabilitySignals;
        private final Provenance provenance;
        private final double confidence;

        public AbstractedArtifact(
            String sourceId,
            SourceType sourceType,
            String summary,
            List<String> entities,
            List<String> claims,
            List<String> codePatterns,
            List<String> taskHints,
            Map<CapabilityProfile.Capability, Double> capabilitySignals,
            Provenance provenance,
            double confidence
        ) {
            this.sourceId = sourceId;
            this.sourceType = sourceType;
            this.summary = summary;
            this.entities = new ArrayList<>(entities);
            this.claims = new ArrayList<>(claims);
            this.codePatterns = new ArrayList<>(codePatterns);
            this.taskHints = new ArrayList<>(taskHints);
            this.capabilitySignals = new EnumMap<>(capabilitySignals);
            this.provenance = provenance;
            this.confidence = confidence;
        }

        public String sourceId() {
            return sourceId;
        }

        public SourceType sourceType() {
            return sourceType;
        }

        public String summary() {
            return summary;
        }

        public List<String> entities() {
            return Collections.unmodifiableList(entities);
        }

        public List<String> claims() {
            return Collections.unmodifiableList(claims);
        }

        public List<String> codePatterns() {
            return Collections.unmodifiableList(codePatterns);
        }

        public List<String> taskHints() {
            return Collections.unmodifiableList(taskHints);
        }

        public Map<CapabilityProfile.Capability, Double> capabilitySignals() {
            return Collections.unmodifiableMap(capabilitySignals);
        }

        public Provenance provenance() {
            return provenance;
        }

        public double confidence() {
            return confidence;
        }

        public List<String> topCapabilities(int limit) {
            List<Map.Entry<CapabilityProfile.Capability, Double>> sorted = new ArrayList<>(capabilitySignals.entrySet());
            sorted.sort(Map.Entry.<CapabilityProfile.Capability, Double>comparingByValue().reversed());
            List<String> out = new ArrayList<>();
            for (int i = 0; i < sorted.size() && i < limit; i++) {
                Map.Entry<CapabilityProfile.Capability, Double> entry = sorted.get(i);
                out.add(entry.getKey().name() + "=" + String.format(Locale.ROOT, "%.2f", entry.getValue()));
            }
            return out;
        }

        public String compactSummary() {
            return sourceType.name().toLowerCase(Locale.ROOT) + ": " + summary;
        }

        public Map<String, Object> toContextMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("sourceId", sourceId);
            map.put("sourceType", sourceType.name().toLowerCase(Locale.ROOT));
            map.put("summary", summary);
            map.put("entities", new ArrayList<>(entities));
            map.put("claims", new ArrayList<>(claims));
            map.put("codePatterns", new ArrayList<>(codePatterns));
            map.put("taskHints", new ArrayList<>(taskHints));

            Map<String, Double> signalMap = new HashMap<>();
            for (Map.Entry<CapabilityProfile.Capability, Double> entry : capabilitySignals.entrySet()) {
                signalMap.put(entry.getKey().name(), entry.getValue());
            }
            map.put("capabilitySignals", signalMap);

            Map<String, Object> provenanceMap = new HashMap<>();
            provenanceMap.put("sourceId", provenance.sourceId());
            provenanceMap.put("sourceType", provenance.sourceType());
            provenanceMap.put("timestampEpochMs", provenance.timestampEpochMs());
            provenanceMap.put("contentHash", provenance.contentHash());
            provenanceMap.put("trustScore", provenance.trustScore());
            map.put("provenance", provenanceMap);
            map.put("confidence", confidence);
            return map;
        }
    }

    public static List<AbstractedArtifact> abstractPromptSources(
        String prompt,
        Collection<String> absorbedFragments,
        Collection<String> sourceFragments
    ) {
        List<RawSource> rawSources = collectRawSources(prompt, absorbedFragments, sourceFragments);
        List<AbstractedArtifact> artifacts = new ArrayList<>();
        for (RawSource source : rawSources) {
            AbstractedArtifact artifact = abstractSource(source);
            if (artifact != null) {
                artifacts.add(artifact);
            }
        }
        return artifacts;
    }

    public static List<Map<String, Object>> toContextPayload(List<AbstractedArtifact> artifacts, int limit) {
        if (artifacts == null || artifacts.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> payload = new ArrayList<>();
        for (int i = 0; i < artifacts.size() && i < Math.max(1, limit); i++) {
            payload.add(artifacts.get(i).toContextMap());
        }
        return payload;
    }

    public static List<String> collectTaskHints(List<AbstractedArtifact> artifacts, int limit) {
        if (artifacts == null || artifacts.isEmpty()) {
            return Collections.emptyList();
        }
        LinkedHashSet<String> hints = new LinkedHashSet<>();
        for (AbstractedArtifact artifact : artifacts) {
            hints.addAll(artifact.taskHints());
            if (hints.size() >= limit) {
                break;
            }
        }
        return new ArrayList<>(hints).subList(0, Math.min(hints.size(), Math.max(1, limit)));
    }

    public static List<String> collectProvenanceSummaries(List<AbstractedArtifact> artifacts, int limit) {
        if (artifacts == null || artifacts.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> summaries = new ArrayList<>();
        for (int i = 0; i < artifacts.size() && i < Math.max(1, limit); i++) {
            summaries.add(artifacts.get(i).provenance().compact());
        }
        return summaries;
    }

    public static Map<CapabilityProfile.Capability, Double> aggregateCapabilitySignals(List<AbstractedArtifact> artifacts) {
        if (artifacts == null || artifacts.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<CapabilityProfile.Capability, Double> totals = new EnumMap<>(CapabilityProfile.Capability.class);
        Map<CapabilityProfile.Capability, Integer> counts = new EnumMap<>(CapabilityProfile.Capability.class);
        for (AbstractedArtifact artifact : artifacts) {
            for (Map.Entry<CapabilityProfile.Capability, Double> signal : artifact.capabilitySignals().entrySet()) {
                totals.merge(signal.getKey(), signal.getValue(), Double::sum);
                counts.merge(signal.getKey(), 1, Integer::sum);
            }
        }

        Map<CapabilityProfile.Capability, Double> averaged = new EnumMap<>(CapabilityProfile.Capability.class);
        for (Map.Entry<CapabilityProfile.Capability, Double> entry : totals.entrySet()) {
            int count = Math.max(1, counts.getOrDefault(entry.getKey(), 1));
            averaged.put(entry.getKey(), clamp(entry.getValue() / count));
        }
        return averaged;
    }

    private static List<RawSource> collectRawSources(
        String prompt,
        Collection<String> absorbedFragments,
        Collection<String> sourceFragments
    ) {
        List<RawSource> rawSources = new ArrayList<>();
        Set<String> dedupe = new HashSet<>();

        if (prompt != null && !prompt.isBlank()) {
            String cleaned = normalize(prompt);
            if (!cleaned.isBlank()) {
                rawSources.add(new RawSource("prompt", SourceType.PROMPT, cleaned, 0.50));
                dedupe.add("prompt|" + cleaned);
            }
        }

        if (absorbedFragments != null) {
            for (Object item : absorbedFragments) {
                String text = normalize(item == null ? "" : item.toString());
                if (text.isBlank()) {
                    continue;
                }
                String key = "akashic|" + text;
                if (dedupe.add(key)) {
                    rawSources.add(new RawSource("akashic", SourceType.AKASHIC, text, 0.72));
                }
            }
        }

        if (sourceFragments != null) {
            for (Object item : sourceFragments) {
                String text = normalize(item == null ? "" : item.toString());
                if (text.isBlank()) {
                    continue;
                }
                RawSource parsed = parseSourceFragment(text);
                String key = parsed.sourceType + "|" + parsed.sourceId + "|" + parsed.content;
                if (dedupe.add(key)) {
                    rawSources.add(parsed);
                }
            }
        }

        return rawSources;
    }

    private static RawSource parseSourceFragment(String text) {
        String lowered = text.toLowerCase(Locale.ROOT);
        if (lowered.startsWith("scraped-url:")) {
            String body = text.substring("scraped-url:".length()).trim();
            int arrow = body.indexOf("->");
            if (arrow >= 0) {
                String url = normalize(body.substring(0, arrow));
                String content = normalize(body.substring(arrow + 2));
                return new RawSource(url, SourceType.SCRAPED_URL, content, 0.82);
            }
            return new RawSource(body, SourceType.SCRAPED_URL, body, 0.80);
        }

        if (lowered.startsWith("observed-url:")) {
            String url = normalize(text.substring("observed-url:".length()));
            return new RawSource(url, SourceType.OBSERVED_URL, text, 0.58);
        }

        if (lowered.startsWith("absorbed-repo:")) {
            String body = normalize(text.substring("absorbed-repo:".length()));
            return new RawSource(body, SourceType.REPOSITORY_ABSORPTION, body, 0.86);
        }

        int colon = text.indexOf(':');
        if (colon > 0 && text.substring(0, colon).toLowerCase(Locale.ROOT).contains("documentation")) {
            String source = normalize(text.substring(0, colon));
            String content = normalize(text.substring(colon + 1));
            return new RawSource(source, SourceType.EXTRACTED_DOC, content, 0.76);
        }

        return new RawSource("context_fragment", SourceType.EXTRACTED_DOC, text, 0.70);
    }

    private static AbstractedArtifact abstractSource(RawSource source) {
        if (source == null || source.content.isBlank()) {
            return null;
        }

        String normalizedContent = normalize(source.content);
        String summary = summarize(normalizedContent);
        List<String> entities = extractEntities(normalizedContent);
        List<String> claims = extractClaims(normalizedContent);
        List<String> codePatterns = extractCodePatterns(normalizedContent);
        Map<CapabilityProfile.Capability, Double> capabilitySignals = detectCapabilities(normalizedContent, source.sourceType, codePatterns);
        List<String> taskHints = buildTaskHints(source, capabilitySignals, codePatterns);
        double confidence = computeConfidence(source.trustScore, capabilitySignals, claims, codePatterns);

        Provenance provenance = new Provenance(
            source.sourceId,
            source.sourceType.name().toLowerCase(Locale.ROOT),
            System.currentTimeMillis(),
            shortHash(normalizedContent),
            clamp(source.trustScore)
        );

        return new AbstractedArtifact(
            source.sourceId,
            source.sourceType,
            summary,
            entities,
            claims,
            codePatterns,
            taskHints,
            capabilitySignals,
            provenance,
            confidence
        );
    }

    private static Map<CapabilityProfile.Capability, Double> detectCapabilities(
        String content,
        SourceType sourceType,
        List<String> codePatterns
    ) {
        String lowered = content.toLowerCase(Locale.ROOT);
        Map<CapabilityProfile.Capability, Double> scores = new EnumMap<>(CapabilityProfile.Capability.class);

        for (Map.Entry<CapabilityProfile.Capability, List<String>> entry : CAPABILITY_KEYWORDS.entrySet()) {
            double score = 0.0;
            for (String keyword : entry.getValue()) {
                if (lowered.contains(keyword)) {
                    score += 0.14;
                }
            }
            if (score > 0.0) {
                scores.put(entry.getKey(), clamp(score));
            }
        }

        if (!codePatterns.isEmpty()) {
            scores.put(
                CapabilityProfile.Capability.CODING,
                clamp(Math.max(scores.getOrDefault(CapabilityProfile.Capability.CODING, 0.0), 0.62))
            );
            scores.put(
                CapabilityProfile.Capability.TOOL_USE,
                clamp(Math.max(scores.getOrDefault(CapabilityProfile.Capability.TOOL_USE, 0.0), 0.44))
            );
        }

        if (sourceType == SourceType.REPOSITORY_ABSORPTION) {
            scores.put(
                CapabilityProfile.Capability.LONG_CONTEXT,
                clamp(Math.max(scores.getOrDefault(CapabilityProfile.Capability.LONG_CONTEXT, 0.0), 0.60))
            );
            scores.put(
                CapabilityProfile.Capability.CODING,
                clamp(Math.max(scores.getOrDefault(CapabilityProfile.Capability.CODING, 0.0), 0.58))
            );
        }

        if (sourceType == SourceType.SCRAPED_URL || sourceType == SourceType.OBSERVED_URL) {
            scores.put(
                CapabilityProfile.Capability.TOOL_USE,
                clamp(Math.max(scores.getOrDefault(CapabilityProfile.Capability.TOOL_USE, 0.0), 0.42))
            );
        }

        return scores;
    }

    private static List<String> buildTaskHints(
        RawSource source,
        Map<CapabilityProfile.Capability, Double> capabilitySignals,
        List<String> codePatterns
    ) {
        List<String> hints = new ArrayList<>();
        List<Map.Entry<CapabilityProfile.Capability, Double>> sortedSignals = new ArrayList<>(capabilitySignals.entrySet());
        sortedSignals.sort(Map.Entry.<CapabilityProfile.Capability, Double>comparingByValue().reversed());

        for (Map.Entry<CapabilityProfile.Capability, Double> signal : sortedSignals) {
            if (signal.getValue() < 0.20 || hints.size() >= 4) {
                continue;
            }
            hints.add(capabilityHint(source.sourceId, signal.getKey()));
        }

        for (String codePattern : codePatterns) {
            if (hints.size() >= 6) {
                break;
            }
            hints.add("Validate extracted pattern `" + compact(codePattern, 70) + "` from " + source.sourceId);
        }

        if (hints.isEmpty()) {
            hints.add("Summarize and verify claims from " + source.sourceId);
        }
        return hints;
    }

    private static String capabilityHint(String sourceId, CapabilityProfile.Capability capability) {
        switch (capability) {
            case REASONING:
                return "Reconcile reasoning claims from " + sourceId + " and extract stable axioms";
            case CODING:
                return "Generate or verify adapter tests from " + sourceId;
            case LONG_CONTEXT:
                return "Pack long-context memory chunks from " + sourceId + " for continuity";
            case REFLEX_SPEED:
                return "Derive low-latency response path from " + sourceId;
            case CREATIVITY:
                return "Remix creative motifs from " + sourceId + " into punchline candidates";
            case TOOL_USE:
                return "Build tool-call plan from " + sourceId + " with deterministic output checks";
            case MULTIMODAL:
                return "Extract multimodal cues from " + sourceId + " into observable signals";
            case LOCAL_FIT:
                return "Map " + sourceId + " to local/offline execution lanes";
            case MEMORY_RETENTION:
                return "Persist continuity trace from " + sourceId + " into Akashic memory";
            case SAFETY_ALIGNMENT:
                return "Convert " + sourceId + " into safety policy checks before actuation";
            case PRECISION:
                return "Generate precision metrics from " + sourceId + " and verify deterministically";
            case EFFICIENCY:
                return "Profile execution efficiency implied by " + sourceId;
            case MULTILINGUAL:
                return "Extract multilingual representation hints from " + sourceId;
            default:
                return "Summarize operational signal from " + sourceId;
        }
    }

    private static List<String> extractEntities(String content) {
        if (content == null || content.isBlank()) {
            return Collections.emptyList();
        }
        LinkedHashSet<String> entities = new LinkedHashSet<>();
        Matcher matcher = ENTITY_PATTERN.matcher(content);
        while (matcher.find() && entities.size() < 10) {
            entities.add(matcher.group());
        }
        return new ArrayList<>(entities);
    }

    private static List<String> extractClaims(String content) {
        if (content == null || content.isBlank()) {
            return Collections.emptyList();
        }
        List<String> claims = new ArrayList<>();
        Matcher matcher = SENTENCE_PATTERN.matcher(content);
        while (matcher.find() && claims.size() < 6) {
            String sentence = normalize(matcher.group());
            if (sentence.length() < 24) {
                continue;
            }
            claims.add(compact(sentence, 180));
        }
        return claims;
    }

    private static List<String> extractCodePatterns(String content) {
        if (content == null || content.isBlank()) {
            return Collections.emptyList();
        }
        LinkedHashSet<String> patterns = new LinkedHashSet<>();
        Matcher matcher = CODE_PATTERN.matcher(content);
        while (matcher.find() && patterns.size() < 6) {
            String raw = normalize(matcher.group());
            if (!raw.isBlank()) {
                patterns.add(compact(raw, 96));
            }
        }
        return new ArrayList<>(patterns);
    }

    private static double computeConfidence(
        double trustScore,
        Map<CapabilityProfile.Capability, Double> signals,
        List<String> claims,
        List<String> codePatterns
    ) {
        double signalStrength = signals.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.25);
        double claimFactor = claims.isEmpty() ? 0.08 : Math.min(0.22, claims.size() * 0.05);
        double codeFactor = codePatterns.isEmpty() ? 0.05 : Math.min(0.20, codePatterns.size() * 0.04);
        return clamp(trustScore * 0.55 + signalStrength * 0.25 + claimFactor + codeFactor);
    }

    private static String summarize(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String normalized = normalize(content);
        if (normalized.length() <= MAX_SUMMARY_CHARS) {
            return normalized;
        }
        return normalized.substring(0, MAX_SUMMARY_CHARS - 3) + "...";
    }

    private static String shortHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < hash.length && i < 6; i++) {
                out.append(String.format("%02x", hash[i]));
            }
            return out.toString();
        } catch (Exception e) {
            return Integer.toHexString(content.hashCode());
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    private static String compact(String value, int maxChars) {
        String normalized = normalize(value);
        if (normalized.length() <= maxChars) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, maxChars - 3)) + "...";
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static final class RawSource {
        private final String sourceId;
        private final SourceType sourceType;
        private final String content;
        private final double trustScore;

        private RawSource(String sourceId, SourceType sourceType, String content, double trustScore) {
            this.sourceId = sourceId == null || sourceId.isBlank() ? "unknown" : sourceId;
            this.sourceType = sourceType;
            this.content = content == null ? "" : content;
            this.trustScore = trustScore;
        }
    }
}
