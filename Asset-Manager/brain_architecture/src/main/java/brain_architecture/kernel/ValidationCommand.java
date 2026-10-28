package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A reproducible validation command.
 */
public final class ValidationCommand {
    private final String label;
    private final Path workingDirectory;
    private final List<String> command;

    public ValidationCommand(String label, Path workingDirectory, List<String> command) {
        this.label = label == null ? "unnamed" : label;
        this.workingDirectory = workingDirectory;
        this.command = Collections.unmodifiableList(new ArrayList<>(command == null ? List.of() : command));
    }

    public String getLabel() {
        return label;
    }

    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    public List<String> getCommand() {
        return command;
    }
}
