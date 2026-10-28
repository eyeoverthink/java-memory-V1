package fraymus.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MathProblemCoherenceGate {
    private MathProblemCoherenceGate() {
    }

    public static boolean isMathProblemRequest(String prompt) {
        if (prompt == null) return false;
        String p = prompt.toLowerCase(Locale.ROOT);
        return (p.contains("math problem") || p.contains("mathematical problem"))
            && (p.contains("create") || p.contains("generate") || p.contains("new") || p.contains("novel"));
    }

    public static String buildSystemPrompt() {
        return String.join("\n",
            "You are FRAYMUS mathematical problem builder.",
            "Generate exactly one original mathematical problem and make it machine-checkable.",
            "Do not leave blanks, placeholders, dangling punctuation, or missing codomains.",
            "Use this contract:",
            "TITLE:",
            "OBJECTS: Define every symbol, domain, codomain, parameter, and interval.",
            "CONDITIONS: Give numbered complete conditions with all constants specified.",
            "QUESTION: State exactly what must be found or proved.",
            "CONSISTENCY CHECK: Explain in 2-4 sentences why the conditions are not contradictory.",
            "SOLUTION SKETCH: Give a brief valid solution path or proof of existence.",
            "Avoid vague labels like fractal unless a precise fractal object is defined."
        );
    }

    public static ValidationResult validate(String response) {
        List<String> issues = new ArrayList<>();
        String r = response == null ? "" : response;
        String lower = r.toLowerCase(Locale.ROOT);

        if (r.isBlank()) issues.add("empty response");
        if (!containsAny(lower, "title:")) issues.add("missing TITLE section");
        if (!containsAny(lower, "objects:")) issues.add("missing OBJECTS section");
        if (!containsAny(lower, "conditions:")) issues.add("missing CONDITIONS section");
        if (!containsAny(lower, "question:")) issues.add("missing QUESTION section");
        if (!containsAny(lower, "consistency check:")) issues.add("missing CONSISTENCY CHECK section");
        if (!containsAny(lower, "solution sketch:")) issues.add("missing SOLUTION SKETCH section");
        if (hasDanglingBlank(r)) issues.add("contains dangling blanks or incomplete clauses");
        if (lower.contains("for any real") && (lower.contains("from [") || lower.contains("on [")) && !lower.contains("extended")) {
            issues.add("possible domain mismatch: bounded definition used with all real inputs");
        }
        if (countOccurrences(lower, "integral") > 0 && !hasIntegralBounds(lower)) {
            issues.add("integral appears without clear bounds or measure");
        }

        return new ValidationResult(issues.isEmpty(), issues);
    }

    public static String buildRepairPrompt(String originalPrompt, String invalidResponse, ValidationResult validation) {
        return String.join("\n\n",
            "The previous mathematical problem failed coherence validation.",
            "Original request: " + originalPrompt,
            "Validation issues: " + String.join("; ", validation.issues()),
            "Previous response:",
            invalidResponse == null ? "" : invalidResponse,
            "Regenerate the problem from scratch using the required contract. Every symbol must be defined, every condition must be complete, and a solution sketch must be included."
        );
    }

    private static boolean containsAny(String text, String needle) {
        return text.contains(needle);
    }

    private static boolean hasDanglingBlank(String text) {
        return text.contains("to .")
            || text.contains("equals .")
            || text.contains("= .")
            || text.contains("to  .")
            || text.matches("(?s).*\\n\\s*\\d+\\.\\s*.*\\b(to|equals|is|are)\\s*\\.\\s*.*");
    }

    private static boolean hasIntegralBounds(String lower) {
        return lower.contains("from")
            || lower.contains("over")
            || lower.contains("∫_")
            || lower.contains("∫[")
            || lower.contains("int_")
            || lower.contains("between");
    }

    private static int countOccurrences(String text, String needle) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }

    public record ValidationResult(boolean valid, List<String> issues) {
    }
}
