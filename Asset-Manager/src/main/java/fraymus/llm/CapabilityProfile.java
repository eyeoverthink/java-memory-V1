package fraymus.llm;

import java.util.Locale;

/**
 * Quantized capability contract for task-driven model selection.
 * Values are on a 0..1 scale.
 */
public record CapabilityProfile(
    double reasoning,
    double coding,
    double longContext,
    double reflexSpeed,
    double creativity,
    double multilingual,
    double toolUse,
    double multimodal,
    double localFit
) {
    public double scoreFor(ModelRole role, String taskHint) {
        String hint = taskHint == null ? "" : taskHint.toLowerCase(Locale.ROOT);

        double base = switch (role) {
            case REASONING -> reasoning * 0.45 + toolUse * 0.15 + longContext * 0.15 + multilingual * 0.05 + localFit * 0.10 + creativity * 0.10;
            case CODING -> coding * 0.45 + reasoning * 0.15 + toolUse * 0.20 + longContext * 0.10 + localFit * 0.10;
            case REFLEX -> reflexSpeed * 0.50 + localFit * 0.20 + toolUse * 0.10 + coding * 0.10 + longContext * 0.10;
            case MEMORY -> longContext * 0.50 + reasoning * 0.15 + toolUse * 0.10 + localFit * 0.10 + multilingual * 0.05 + multimodal * 0.10;
            case CREATIVE -> creativity * 0.40 + reasoning * 0.10 + multilingual * 0.10 + longContext * 0.10 + localFit * 0.05 + reflexSpeed * 0.05 + multimodal * 0.20;
        };

        double hintBonus = 0.0;
        if (containsAny(hint, "repo", "repository", "whole codebase", "long context", "pdf", "document", "memory")) {
            hintBonus += longContext * 0.10;
        }
        if (containsAny(hint, "code", "coder", "compile", "debug", "patch", "proof", "shell")) {
            hintBonus += coding * 0.10 + toolUse * 0.05;
        }
        if (containsAny(hint, "flash", "fast", "instant", "reflex", "live")) {
            hintBonus += reflexSpeed * 0.10;
        }
        if (containsAny(hint, "creative", "humor", "remix", "joke", "style", "taste")) {
            hintBonus += creativity * 0.10;
        }
        if (containsAny(hint, "reason", "math", "logic", "thinking", "plan", "synthesis")) {
            hintBonus += reasoning * 0.10;
        }

        return clamp(base + hintBonus);
    }

    private static boolean containsAny(String haystack, String... needles) {
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
