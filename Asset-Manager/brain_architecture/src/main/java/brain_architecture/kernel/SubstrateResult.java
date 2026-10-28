package brain_architecture.kernel;

import java.util.Map;

/**
 * Output from a substrate action or step.
 */
public final class SubstrateResult {
    private final boolean success;
    private final String output;
    private final int exitCode;
    private final Map<String, String> metadata;

    public SubstrateResult(boolean success, String output, int exitCode, Map<String, String> metadata) {
        this.success = success;
        this.output = output;
        this.exitCode = exitCode;
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOutput() {
        return output;
    }

    public int getExitCode() {
        return exitCode;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
