package fraymus.llm;

import java.util.Locale;
import java.util.Optional;

/**
 * Fraynix-native capability roles.
 * Roles map organs and tasks to interchangeable model substrates.
 */
public enum ModelRole {
    REASONING,
    CODING,
    REFLEX,
    MEMORY,
    CREATIVE;

    public static Optional<ModelRole> fromHint(String hint) {
        String normalized = normalize(hint);
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        if (containsAny(normalized, "coder", "coding", "code", "debug", "compile", "patch", "repair", "babel", "openclaw")) {
            return Optional.of(CODING);
        }
        if (containsAny(normalized, "reflex", "fast", "flash", "realtime", "instant", "spinal")) {
            return Optional.of(REFLEX);
        }
        if (containsAny(normalized, "memory", "hippocampus", "long context", "context", "archive", "repo scale", "repository", "million token")) {
            return Optional.of(MEMORY);
        }
        if (containsAny(normalized, "creative", "humor", "remix", "joke", "style", "punchline", "taste", "dionysian")) {
            return Optional.of(CREATIVE);
        }
        if (containsAny(normalized, "reason", "thinking", "think", "prove", "math", "plan", "logic", "parietal", "prefrontal")) {
            return Optional.of(REASONING);
        }
        return Optional.empty();
    }

    public static ModelRole fromOrgan(String organOrBrain) {
        return fromHint(organOrBrain).orElse(REASONING);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean containsAny(String haystack, String... needles) {
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
