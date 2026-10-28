package brain_architecture.kernel;

import fraymus.execution.ExecutionBridge;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;
import java.util.List;
import java.util.Map;

/**
 * Kernel-owned adapter that routes command execution through ExecutionBridge.
 */
public final class KernelExecutionBridgeAdapter {
    private static final java.util.Set<String> WINDOWS_SHELL_BUILTINS = java.util.Set.of(
        "echo", "cd", "dir", "set", "cls", "type", "copy", "move", "del",
        "erase", "ren", "mkdir", "rmdir", "pushd", "popd"
    );

    private final ExecutionBridge executionBridge;

    public KernelExecutionBridgeAdapter() {
        this.executionBridge = new ExecutionBridge();
    }

    public SubstrateResult runCommand(String adapterId, Path workingDirectory, List<String> commandArgs) {
        if (commandArgs == null || commandArgs.isEmpty()) {
            return new SubstrateResult(false, "No command provided", -1, metadata(adapterId, "", false));
        }

        if (shouldUseDirectProcess(commandArgs)) {
            return runDirectProcess(adapterId, workingDirectory, commandArgs);
        }

        String command = normalizeCommand(commandArgs);
        if (command.isBlank()) {
            return new SubstrateResult(false, "No executable command provided", -1, metadata(adapterId, "", false));
        }

        String scopedCommand = scopeToWorkingDirectory(workingDirectory, command);
        ExecutionBridge.ExecutionResult result = executionBridge.executeShell(scopedCommand);
        String output = (result.getStdout() == null || result.getStdout().isBlank())
            ? result.getStderr()
            : result.getStdout();

        return new SubstrateResult(
            result.isSuccess(),
            output == null ? "" : output,
            result.getExitCode(),
            metadata(adapterId, scopedCommand, true)
        );
    }

    private SubstrateResult runDirectProcess(String adapterId, Path workingDirectory, List<String> commandArgs) {
        try {
            ProcessBuilder builder = new ProcessBuilder(commandArgs);
            if (workingDirectory != null) {
                builder.directory(workingDirectory.toFile());
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();
            String output = readOutput(process);
            int exitCode = process.waitFor();
            return new SubstrateResult(
                exitCode == 0,
                output,
                exitCode,
                metadata(adapterId, String.join(" ", commandArgs), false)
            );
        } catch (IOException e) {
            if (isExecutableNotFound(e)) {
                return runThroughExecutionBridge(adapterId, workingDirectory, commandArgs);
            }
            return new SubstrateResult(false, e.getMessage(), -1, metadata(adapterId, String.join(" ", commandArgs), false));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new SubstrateResult(false, e.getMessage(), -1, metadata(adapterId, String.join(" ", commandArgs), false));
        }
    }

    private SubstrateResult runThroughExecutionBridge(String adapterId, Path workingDirectory, List<String> commandArgs) {
        String command = normalizeCommand(commandArgs);
        if (command.isBlank()) {
            return new SubstrateResult(false, "No executable command provided", -1, metadata(adapterId, "", false));
        }
        String scopedCommand = scopeToWorkingDirectory(workingDirectory, command);
        ExecutionBridge.ExecutionResult result = executionBridge.executeShell(scopedCommand);
        String output = (result.getStdout() == null || result.getStdout().isBlank())
            ? result.getStderr()
            : result.getStdout();

        return new SubstrateResult(
            result.isSuccess(),
            output == null ? "" : output,
            result.getExitCode(),
            metadata(adapterId, scopedCommand, true)
        );
    }

    private boolean shouldUseDirectProcess(List<String> commandArgs) {
        if (commandArgs == null || commandArgs.isEmpty()) {
            return false;
        }
        if (commandArgs.size() >= 3 && isCmdShell(commandArgs.get(0)) && "/c".equalsIgnoreCase(commandArgs.get(1))) {
            return false;
        }
        if (commandArgs.size() >= 3 && isPosixShell(commandArgs.get(0)) && "-c".equals(commandArgs.get(1))) {
            return false;
        }
        if (isWindows() && isWindowsShellBuiltin(commandArgs.get(0))) {
            // Route shell built-ins through cmd/sh bridge instead of ProcessBuilder direct exec.
            return false;
        }
        return true;
    }

    private String readOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }
        return output.toString().trim();
    }

    private String normalizeCommand(List<String> commandArgs) {
        if (commandArgs.isEmpty()) {
            return "";
        }
        if (commandArgs.size() >= 3 && isCmdShell(commandArgs.get(0)) && "/c".equalsIgnoreCase(commandArgs.get(1))) {
            return String.join(" ", commandArgs.subList(2, commandArgs.size())).trim();
        }
        if (commandArgs.size() >= 3 && isPosixShell(commandArgs.get(0)) && "-c".equals(commandArgs.get(1))) {
            return String.join(" ", commandArgs.subList(2, commandArgs.size())).trim();
        }
        return commandArgs.stream()
            .map(this::quoteArgument)
            .reduce((left, right) -> left + " " + right)
            .orElse("")
            .trim();
    }

    private String scopeToWorkingDirectory(Path workingDirectory, String command) {
        if (workingDirectory == null) {
            return command;
        }
        String escapedPath = workingDirectory.toAbsolutePath().normalize().toString().replace("\"", "\"\"");
        if (isWindows()) {
            return "cd /d \"" + escapedPath + "\" && " + command;
        }
        return "cd \"" + escapedPath + "\" && " + command;
    }

    private boolean isCmdShell(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        return "cmd".equals(normalized) || "cmd.exe".equals(normalized);
    }

    private boolean isPosixShell(String value) {
        return "sh".equals(value) || "bash".equals(value);
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private boolean isWindowsShellBuiltin(String value) {
        if (value == null) {
            return false;
        }
        return WINDOWS_SHELL_BUILTINS.contains(value.toLowerCase(Locale.ROOT));
    }

    private boolean isExecutableNotFound(IOException error) {
        String message = error == null ? "" : String.valueOf(error.getMessage()).toLowerCase(Locale.ROOT);
        return message.contains("createprocess error=2")
            || message.contains("no such file")
            || message.contains("not recognized");
    }

    private String quoteArgument(String argument) {
        if (argument == null) {
            return "\"\"";
        }
        if (argument.isBlank()) {
            return "\"\"";
        }
        boolean needsQuotes = argument.chars().anyMatch(character ->
            Character.isWhitespace(character)
                || character == '"'
                || character == '&'
                || character == '|'
                || character == '('
                || character == ')'
                || character == '^'
                || character == '<'
                || character == '>'
        );
        if (!needsQuotes) {
            return argument;
        }
        return "\"" + argument.replace("\"", "\"\"") + "\"";
    }

    private Map<String, String> metadata(String adapterId, String command, boolean bridgeUsed) {
        return Map.of(
            "adapter", adapterId == null ? "unknown" : adapterId,
            "executor", bridgeUsed ? "execution-bridge" : "process-builder",
            "command", command == null ? "" : command
        );
    }
}
