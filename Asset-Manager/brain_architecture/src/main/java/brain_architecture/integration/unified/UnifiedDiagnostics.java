package brain_architecture.integration.unified;

import brain_architecture.kernel.VerifierResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase A unified diagnostics contract mapped from verifier outcomes.
 */
public final class UnifiedDiagnostics {
    private final boolean validationPassed;
    private final List<String> passedChecks;
    private final List<String> failedChecks;
    private final List<String> warnings;
    private final List<UnifiedDiagnosticEntry> diagnostics;
    private final Map<String, Long> timingsMs;

    public UnifiedDiagnostics(
            boolean validationPassed,
            List<String> passedChecks,
            List<String> failedChecks,
            List<String> warnings,
            Map<String, Long> timingsMs) {
        this(validationPassed, passedChecks, failedChecks, warnings, List.of(), timingsMs);
    }

    public UnifiedDiagnostics(
            boolean validationPassed,
            List<String> passedChecks,
            List<String> failedChecks,
            List<String> warnings,
            List<UnifiedDiagnosticEntry> diagnostics,
            Map<String, Long> timingsMs) {
        this.validationPassed = validationPassed;
        this.passedChecks = Collections.unmodifiableList(new ArrayList<>(passedChecks == null ? List.of() : passedChecks));
        this.failedChecks = Collections.unmodifiableList(new ArrayList<>(failedChecks == null ? List.of() : failedChecks));
        this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings == null ? List.of() : warnings));
        this.diagnostics = Collections.unmodifiableList(new ArrayList<>(diagnostics == null ? List.of() : diagnostics));
        this.timingsMs = Collections.unmodifiableMap(new LinkedHashMap<>(timingsMs == null ? Map.of() : timingsMs));
    }

    public static UnifiedDiagnostics fromVerifierResult(VerifierResult verifierResult) {
        if (verifierResult == null) {
            return new UnifiedDiagnostics(false, List.of(), List.of("missing-verifier-result"), List.of(), List.of(), Map.of());
        }
        return new UnifiedDiagnostics(
            verifierResult.isSuccess(),
            verifierResult.getPassedChecks(),
            verifierResult.getFailedChecks(),
            verifierResult.getWarnings(),
            verifierResult.getDiagnostics().stream().map(UnifiedDiagnosticEntry::fromVerifierDiagnostic).toList(),
            verifierResult.getTimingsMs()
        );
    }

    public boolean isValidationPassed() {
        return validationPassed;
    }

    public List<String> getPassedChecks() {
        return passedChecks;
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

    public Map<String, Long> getTimingsMs() {
        return timingsMs;
    }
}
