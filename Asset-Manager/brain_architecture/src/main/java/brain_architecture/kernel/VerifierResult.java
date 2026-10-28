package brain_architecture.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Validation outcome for a task or mutation.
 */
public final class VerifierResult {
    private final boolean success;
    private final List<String> passedChecks;
    private final List<String> failedChecks;
    private final List<String> warnings;
    private final List<VerifierDiagnostic> diagnostics;
    private final Map<String, Long> timingsMs;

    public VerifierResult(
            boolean success,
            List<String> passedChecks,
            List<String> failedChecks,
            List<String> warnings,
            Map<String, Long> timingsMs) {
        this(success, passedChecks, failedChecks, warnings, List.of(), timingsMs);
    }

    public VerifierResult(
            boolean success,
            List<String> passedChecks,
            List<String> failedChecks,
            List<String> warnings,
            List<VerifierDiagnostic> diagnostics,
            Map<String, Long> timingsMs) {
        this.success = success;
        this.passedChecks = Collections.unmodifiableList(new ArrayList<>(passedChecks == null ? List.of() : passedChecks));
        this.failedChecks = Collections.unmodifiableList(new ArrayList<>(failedChecks == null ? List.of() : failedChecks));
        this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings == null ? List.of() : warnings));
        this.diagnostics = Collections.unmodifiableList(new ArrayList<>(diagnostics == null ? List.of() : diagnostics));
        this.timingsMs = timingsMs == null ? Map.of() : Map.copyOf(timingsMs);
    }

    public boolean isSuccess() {
        return success;
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

    public List<VerifierDiagnostic> getDiagnostics() {
        return diagnostics;
    }

    public List<VerifierDiagnostic> getErrorDiagnostics() {
        return diagnostics.stream()
            .filter(diagnostic -> diagnostic.getSeverity() == VerifierDiagnostic.Severity.ERROR)
            .toList();
    }

    public Map<String, Long> getTimingsMs() {
        return timingsMs;
    }
}
