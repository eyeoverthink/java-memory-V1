package brain_architecture.kernel;

import java.nio.file.Path;

/**
 * Structured validation diagnostic emitted by a verifier command.
 */
public final class VerifierDiagnostic {
    public enum Severity {
        ERROR,
        WARNING,
        INFO
    }

    private final String toolLabel;
    private final Path file;
    private final Integer line;
    private final Integer column;
    private final Severity severity;
    private final String message;
    private final String raw;

    public VerifierDiagnostic(
            String toolLabel,
            Path file,
            Integer line,
            Integer column,
            Severity severity,
            String message,
            String raw) {
        this.toolLabel = toolLabel == null ? "" : toolLabel;
        this.file = file;
        this.line = line;
        this.column = column;
        this.severity = severity == null ? Severity.INFO : severity;
        this.message = message == null ? "" : message;
        this.raw = raw == null ? "" : raw;
    }

    public String getToolLabel() {
        return toolLabel;
    }

    public Path getFile() {
        return file;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getColumn() {
        return column;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getRaw() {
        return raw;
    }
}
