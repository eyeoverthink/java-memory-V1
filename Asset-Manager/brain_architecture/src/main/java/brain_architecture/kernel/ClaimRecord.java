package brain_architecture.kernel;

import java.time.Instant;
import java.util.List;

/**
 * Typed strict-evidence claim record used for canonical truth auditing.
 */
public record ClaimRecord(
        String id,
        String sourceDoc,
        String claimText,
        ClaimStatus status,
        List<String> evidenceRefs,
        double confidence,
        Instant lastValidatedAt) {

    public ClaimRecord {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Claim id must not be blank");
        }
        sourceDoc = sourceDoc == null ? "" : sourceDoc;
        claimText = claimText == null ? "" : claimText;
        status = status == null ? ClaimStatus.SPECULATIVE : status;
        evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
        if (Double.isNaN(confidence) || Double.isInfinite(confidence)) {
            confidence = 0.0;
        }
        confidence = Math.max(0.0, Math.min(1.0, confidence));
        lastValidatedAt = lastValidatedAt == null ? Instant.EPOCH : lastValidatedAt;
    }
}
