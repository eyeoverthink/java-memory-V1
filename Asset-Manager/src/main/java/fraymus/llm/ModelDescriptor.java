package fraymus.llm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Family/variant descriptor decoupled from any single provider name.
 */
public record ModelDescriptor(
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
    Set<String> aliases
) {
    public ModelDescriptor {
        backends = Set.copyOf(backends);
        aliases = normalizeAliases(aliases);
        Objects.requireNonNull(profile, "profile");
    }

    public boolean matchesName(String candidate) {
        return matchScore(candidate) > 0.0;
    }

    public double matchScore(String candidate) {
        String normalized = normalizeName(candidate);
        if (normalized.isEmpty()) {
            return 0.0;
        }
        if (normalizeName(id).equals(normalized) || normalizeName(variant).equals(normalized)) {
            return 1.0;
        }
        if (normalizeName(family).equals(normalized)) {
            return 0.55;
        }
        double best = 0.0;
        for (String alias : aliases) {
            if (normalized.equals(alias)) {
                best = Math.max(best, 1.0);
                continue;
            }
            if (alias.length() >= 8 && (normalized.startsWith(alias) || alias.startsWith(normalized))) {
                best = Math.max(best, 0.88);
                continue;
            }
            if (alias.length() >= 8 && normalized.contains(alias)) {
                best = Math.max(best, 0.78);
            }
        }
        return best;
    }

    public static String normalizeName(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        int slash = normalized.lastIndexOf('/');
        if (slash >= 0) normalized = normalized.substring(slash + 1);
        int colon = normalized.indexOf(':');
        if (colon >= 0) normalized = normalized.substring(0, colon);
        normalized = normalized.replace("_", "-").replace(" ", "-");
        return normalized;
    }

    private static Set<String> normalizeAliases(Collection<String> aliases) {
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String alias : aliases) {
            String value = normalizeName(alias);
            if (!value.isBlank()) {
                normalized.add(value);
            }
        }
        return normalized;
    }
}
