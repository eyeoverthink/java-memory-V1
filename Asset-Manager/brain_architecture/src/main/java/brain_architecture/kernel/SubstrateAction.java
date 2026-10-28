package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Action to be performed by a substrate adapter.
 */
public final class SubstrateAction {
    public enum Type {
        READ_FILE,
        LIST_DIRECTORY,
        RUN_COMMAND,
        RUN_INFERENCE
    }

    private final Type type;
    private final Path target;
    private final List<String> arguments;
    private final String prompt;
    private final String modelReference;
    private final int maxTokens;

    private SubstrateAction(Type type, Path target, List<String> arguments, String prompt, String modelReference, int maxTokens) {
        this.type = type;
        this.target = target;
        this.arguments = Collections.unmodifiableList(new ArrayList<>(arguments == null ? List.of() : arguments));
        this.prompt = prompt;
        this.modelReference = modelReference;
        this.maxTokens = maxTokens;
    }

    public static SubstrateAction readFile(Path target) {
        return new SubstrateAction(Type.READ_FILE, target, List.of(), null, null, 0);
    }

    public static SubstrateAction listDirectory(Path target) {
        return new SubstrateAction(Type.LIST_DIRECTORY, target, List.of(), null, null, 0);
    }

    public static SubstrateAction runCommand(List<String> command) {
        return new SubstrateAction(Type.RUN_COMMAND, null, command, null, null, 0);
    }

    public static SubstrateAction runInference(String prompt, String modelReference, int maxTokens) {
        return new SubstrateAction(Type.RUN_INFERENCE, null, List.of(), prompt, modelReference, maxTokens);
    }

    public Type getType() {
        return type;
    }

    public Path getTarget() {
        return target;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getModelReference() {
        return modelReference;
    }

    public int getMaxTokens() {
        return maxTokens;
    }
}
