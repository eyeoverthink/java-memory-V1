package brain_architecture.integration.unified;

import brain_architecture.kernel.MutationProposalPreview;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified summary of one guarded proposal preview.
 */
public final class UnifiedProposalPreview {
    private final boolean riskAccepted;
    private final boolean validationPassed;
    private final boolean validationImproved;
    private final List<String> candidatePaths;
    private final List<String> failedChecks;
    private final List<String> warnings;
    private final List<UnifiedDiagnosticEntry> diagnostics;

    public UnifiedProposalPreview(
            boolean riskAccepted,
            boolean validationPassed,
            boolean validationImproved,
            List<String> candidatePaths,
            List<String> failedChecks,
            List<String> warnings,
            List<UnifiedDiagnosticEntry> diagnostics) {
        this.riskAccepted = riskAccepted;
        this.validationPassed = validationPassed;
        this.validationImproved = validationImproved;
        this.candidatePaths = Collections.unmodifiableList(new ArrayList<>(candidatePaths == null ? List.of() : candidatePaths));
        this.failedChecks = Collections.unmodifiableList(new ArrayList<>(failedChecks == null ? List.of() : failedChecks));
        this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings == null ? List.of() : warnings));
        this.diagnostics = Collections.unmodifiableList(new ArrayList<>(diagnostics == null ? List.of() : diagnostics));
    }

    public static UnifiedProposalPreview fromPreview(MutationProposalPreview preview) {
        if (preview == null) {
            return new UnifiedProposalPreview(false, false, false, List.of(), List.of(), List.of(), List.of());
        }
        return new UnifiedProposalPreview(
            preview.isRiskAccepted(),
            preview.isValidationPassed(),
            preview.isValidationImproved(),
            preview.getCandidatePaths().stream().map(UnifiedProposalPreview::normalizePath).toList(),
            preview.getVerifierResult() == null ? List.of() : preview.getVerifierResult().getFailedChecks(),
            preview.getVerifierResult() == null ? List.of() : preview.getVerifierResult().getWarnings(),
            preview.getVerifierResult() == null
                ? List.of()
                : preview.getVerifierResult().getDiagnostics().stream().map(UnifiedDiagnosticEntry::fromVerifierDiagnostic).toList()
        );
    }

    private static String normalizePath(Path path) {
        return path == null ? null : path.toString().replace('\\', '/');
    }

    public boolean isRiskAccepted() {
        return riskAccepted;
    }

    public boolean isValidationPassed() {
        return validationPassed;
    }

    public boolean isValidationImproved() {
        return validationImproved;
    }

    public List<String> getCandidatePaths() {
        return candidatePaths;
    }

    public List<String> getFailedChecks() {
        return failedChecks;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<UnifiedDiagnosticEntry> getDiagnostics() {
        return diagnostics;
    }
}
