package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Verifier for invariants, commands, and mutation risk.
 */
public final class KernelVerifier {
    private static final Pattern FILE_LINE_SEVERITY_PATTERN = Pattern.compile("^(.+?):(\\d+):\\s*(error|warning):\\s*(.+)$");
    private static final Pattern FILE_LINE_CHAR_PATTERN = Pattern.compile("^At\\s+(.+?):(\\d+)\\s+char:(\\d+)$");
    private static final List<String> NETWORK_TOKENS = List.of(
        "http://",
        "https://",
        "curl",
        "wget",
        "invoke-webrequest",
        "start-bitstransfer"
    );

    private final KernelConfiguration configuration;

    public KernelVerifier(KernelConfiguration configuration) {
        this.configuration = configuration;
    }

    public VerifierResult verify(TaskEnvelope envelope, ExecutionTrace trace, ValidationSuite suite, Path workspaceRoot) {
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<VerifierDiagnostic> diagnostics = new ArrayList<>();
        Map<String, Long> timings = new LinkedHashMap<>();

        if (envelope.getGoal() != null && !envelope.getGoal().isBlank()) {
            passed.add("task.goal.present");
        } else {
            failed.add("task.goal.missing");
        }

        if (trace.getPlanSteps().size() <= envelope.getBudget().getMaxSteps()) {
            passed.add("budget.max_steps");
        } else {
            failed.add("budget.max_steps_exceeded");
        }

        if (trace.getToolCalls().size() <= envelope.getBudget().getMaxToolCalls()) {
            passed.add("budget.max_tool_calls");
        } else {
            failed.add("budget.max_tool_calls_exceeded");
        }

        if (configuration.isOfflineEnforced()) {
            if (containsNetworkToken(envelope.getGoal()) || envelope.getConstraints().stream().anyMatch(this::containsNetworkToken)) {
                failed.add("offline.task_contains_network_tokens");
            } else {
                passed.add("offline.task_tokens");
            }
        }

        for (String invariant : suite.getInvariants()) {
            if ("offline".equalsIgnoreCase(invariant) && configuration.isOfflineEnforced()) {
                passed.add("invariant.offline");
            }
        }

        for (ValidationCommand command : suite.allCommands()) {
            Instant started = Instant.now();
            String timingKey = "cmd." + command.getLabel();
            try {
                if (configuration.isOfflineEnforced() && command.getCommand().stream().anyMatch(this::containsNetworkToken)) {
                    failed.add("command." + command.getLabel() + ".network_blocked");
                    timings.put(timingKey, Duration.between(started, Instant.now()).toMillis());
                    continue;
                }
                ProcessBuilder builder = new ProcessBuilder(rewriteCommandForWorkspace(command.getCommand(), workspaceRoot));
                Path workingDirectory = resolveWorkingDirectory(workspaceRoot, command.getWorkingDirectory());
                builder.directory(workingDirectory.toFile());
                builder.redirectErrorStream(true);
                Process process = builder.start();
                byte[] outputBytes = process.getInputStream().readAllBytes();
                int exitCode = process.waitFor();
                String output = new String(outputBytes, StandardCharsets.UTF_8);
                timings.put(timingKey, Duration.between(started, Instant.now()).toMillis());
                diagnostics.addAll(parseDiagnostics(command.getLabel(), output, workingDirectory));
                if (exitCode == 0) {
                    passed.add("command." + command.getLabel());
                } else {
                    failed.add("command." + command.getLabel() + ".exit_" + exitCode);
                    warnings.add(truncate(output));
                }
            } catch (IOException e) {
                failed.add("command." + command.getLabel() + ".io_error");
                warnings.add(command.getLabel() + ": " + e.getMessage());
                timings.put(timingKey, Duration.between(started, Instant.now()).toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                failed.add("command." + command.getLabel() + ".interrupted");
                timings.put(timingKey, Duration.between(started, Instant.now()).toMillis());
            }
        }

        boolean success = failed.isEmpty();
        return new VerifierResult(success, passed, failed, warnings, diagnostics, timings);
    }

    public VerifierResult assessMutationRisk(MutationCandidate candidate) {
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (candidate.getScope() == MutationCandidate.Scope.WHOLE_CODEBASE && !configuration.isAllowWholeCodebaseMutation()) {
            failed.add("mutation.scope.whole_codebase_disallowed");
        } else {
            passed.add("mutation.scope.allowed");
        }

        if (candidate.getOperations().isEmpty()) {
            failed.add("mutation.operations.empty");
        } else {
            passed.add("mutation.operations.present");
        }

        if (candidate.getOperations().size() > 25) {
            warnings.add("mutation.operations.large_batch");
        }

        for (MutationOperation operation : candidate.getOperations()) {
            String lowerPath = operation.getRelativePath().toString().toLowerCase(Locale.ROOT);
            if (lowerPath.contains("build.gradle") || lowerPath.contains("settings.gradle") || lowerPath.contains("cargo.toml")) {
                warnings.add("mutation.touches_build_surface:" + lowerPath);
            }
        }

        return new VerifierResult(failed.isEmpty(), passed, failed, warnings, List.of(), Map.of());
    }

    private boolean containsNetworkToken(String value) {
        if (value == null) {
            return false;
        }
        String lowered = value.toLowerCase(Locale.ROOT);
        return NETWORK_TOKENS.stream().anyMatch(lowered::contains);
    }

    private Path resolveWorkingDirectory(Path workspaceRoot, Path workingDirectory) {
        if (workingDirectory == null) {
            return workspaceRoot;
        }
        Path resolved = workingDirectory.isAbsolute() ? workingDirectory : workspaceRoot.resolve(workingDirectory);
        return resolved.normalize();
    }

    private List<VerifierDiagnostic> parseDiagnostics(String toolLabel, String output, Path workingDirectory) {
        List<VerifierDiagnostic> diagnostics = new ArrayList<>();
        if (output == null || output.isBlank()) {
            return diagnostics;
        }

        String[] lines = output.split("\\R", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher fileMatcher = FILE_LINE_SEVERITY_PATTERN.matcher(line);
            if (fileMatcher.matches()) {
                Path file = resolveDiagnosticPath(fileMatcher.group(1), workingDirectory);
                Integer lineNumber = safeParseInt(fileMatcher.group(2));
                VerifierDiagnostic.Severity severity = parseSeverity(fileMatcher.group(3));
                String message = fileMatcher.group(4).trim();
                Integer column = detectCaretColumn(lines, i);
                String raw = collectDiagnosticRaw(lines, i);
                diagnostics.add(new VerifierDiagnostic(toolLabel, file, lineNumber, column, severity, message, raw));
                continue;
            }

            Matcher shellMatcher = FILE_LINE_CHAR_PATTERN.matcher(line.trim());
            if (shellMatcher.matches()) {
                Path file = resolveDiagnosticPath(shellMatcher.group(1), workingDirectory);
                Integer lineNumber = safeParseInt(shellMatcher.group(2));
                Integer column = safeParseInt(shellMatcher.group(3));
                String message = i > 0 ? lines[i - 1].trim() : line.trim();
                diagnostics.add(new VerifierDiagnostic(
                    toolLabel,
                    file,
                    lineNumber,
                    column,
                    VerifierDiagnostic.Severity.ERROR,
                    message,
                    line.trim()
                ));
            }
        }
        return diagnostics;
    }

    private String collectDiagnosticRaw(String[] lines, int diagnosticIndex) {
        StringBuilder raw = new StringBuilder(lines[diagnosticIndex]);
        int cursor = diagnosticIndex + 1;
        while (cursor < lines.length) {
            String candidate = lines[cursor];
            if (candidate == null) {
                break;
            }
            String trimmed = candidate.trim();
            if (trimmed.isBlank()) {
                break;
            }
            if (FILE_LINE_SEVERITY_PATTERN.matcher(candidate).matches() || FILE_LINE_CHAR_PATTERN.matcher(trimmed).matches()) {
                break;
            }
            if (candidate.startsWith(" ") || candidate.startsWith("\t") || candidate.contains("^")) {
                raw.append(System.lineSeparator()).append(candidate);
                cursor++;
                continue;
            }
            break;
        }
        return raw.toString();
    }

    private Path resolveDiagnosticPath(String rawPath, Path workingDirectory) {
        if (rawPath == null || rawPath.isBlank()) {
            return null;
        }
        try {
            Path path = Path.of(rawPath.trim());
            if (path.isAbsolute()) {
                return path.toAbsolutePath().normalize();
            }
            if (workingDirectory != null) {
                return workingDirectory.resolve(path).normalize();
            }
            return path.normalize();
        } catch (InvalidPathException e) {
            return null;
        }
    }

    private VerifierDiagnostic.Severity parseSeverity(String rawSeverity) {
        if (rawSeverity == null) {
            return VerifierDiagnostic.Severity.INFO;
        }
        return switch (rawSeverity.toLowerCase(Locale.ROOT)) {
            case "error" -> VerifierDiagnostic.Severity.ERROR;
            case "warning" -> VerifierDiagnostic.Severity.WARNING;
            default -> VerifierDiagnostic.Severity.INFO;
        };
    }

    private Integer safeParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer detectCaretColumn(String[] lines, int diagnosticIndex) {
        if (lines == null || diagnosticIndex + 2 >= lines.length) {
            return null;
        }
        String caretLine = lines[diagnosticIndex + 2];
        int caretIndex = caretLine.indexOf('^');
        return caretIndex >= 0 ? caretIndex + 1 : null;
    }

    private List<String> rewriteCommandForWorkspace(List<String> command, Path workspaceRoot) {
        if (command == null || command.isEmpty() || workspaceRoot == null) {
            return command == null ? List.of() : command;
        }
        Path configuredRoot = configuration.getWorkspaceRoot();
        Path normalizedWorkspace = workspaceRoot.toAbsolutePath().normalize();
        if (normalizedWorkspace.equals(configuredRoot)) {
            return command;
        }

        List<String> rewritten = new ArrayList<>(command.size());
        for (String argument : command) {
            rewritten.add(rewriteArgumentForWorkspace(argument, configuredRoot, normalizedWorkspace));
        }
        return rewritten;
    }

    private String rewriteArgumentForWorkspace(String argument, Path configuredRoot, Path workspaceRoot) {
        if (argument == null || argument.isBlank()) {
            return argument;
        }
        try {
            Path candidate = Path.of(argument);
            if (candidate.isAbsolute()) {
                Path normalized = candidate.toAbsolutePath().normalize();
                if (normalized.startsWith(configuredRoot)) {
                    return workspaceRoot.resolve(configuredRoot.relativize(normalized)).normalize().toString();
                }
            }
        } catch (InvalidPathException ignored) {
            return argument;
        }
        return argument;
    }

    private String truncate(String output) {
        if (output == null) {
            return "";
        }
        return output.length() > 400 ? output.substring(0, 400) : output;
    }
}
