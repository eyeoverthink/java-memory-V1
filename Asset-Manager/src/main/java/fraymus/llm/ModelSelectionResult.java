package fraymus.llm;

/**
 * Ranked decision from the abstraction layer.
 */
public record ModelSelectionResult(
    ModelRole role,
    String requestedHint,
    String resolvedModelName,
    String descriptorId,
    String family,
    String availability,
    double score,
    String rationale
) {
}
