package brain_architecture.integration.unified;

import brain_architecture.kernel.VerifierDiagnostic;

import java.nio.file.Path;

/**
 * Structured unified view of one verifier diagnostic.
 */
public final class UnifiedDiagnosticEntry {
    private final String toolLabel;
    private final String filePath;
    private final Integer line;
    private final Integer column;
    private final String severity;
    private final String message;
    private final String raw;

    public UnifiedDiagnosticEntry(
            String toolLabel,
            String filePath,
            Integer line,
            Integer column,
            String severity,
            String message,
            String raw) {
        this.toolLabel = toolLabel == null ? "" : toolLabel;
        this.filePath = filePath;
        this.line = line;
        this.column = column;
        this.severity = severity == null ? "INFO" : severity;
        this.message = message == null ? "" : message;
        this.raw = raw == null ? "" : raw;
    }

    public static UnifiedDiagnosticEntry fromVerifierDiagnostic(VerifierDiagnostic diagnostic) {
        if (diagnostic == null) {
            return new UnifiedDiagnosticEntry("", null, null, null, "INFO", "", "");
        }
        Path file = diagnostic.getFile();
        return new UnifiedDiagnosticEntry(
            diagnostic.getToolLabel(),
            file == null ? null : file.toString().replace('\\', '/'),
            diagnostic.getLine(),
            diagnostic.getColumn(),
            diagnostic.getSeverity().name(),
            diagnostic.getMessage(),
            diagnostic.getRaw()
        );
    }

    public String getToolLabel() {
        return toolLabel;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getColumn() {
        return column;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getRaw() {
        return raw;
    }
}
