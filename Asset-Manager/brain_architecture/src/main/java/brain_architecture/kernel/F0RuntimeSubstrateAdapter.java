package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for the Rust F0 runtime. It degrades gracefully when cargo is unavailable.
 */
public final class F0RuntimeSubstrateAdapter implements SubstrateAdapter {
    private final Path f0Root;
    private final KernelExecutionBridgeAdapter executionAdapter;

    public F0RuntimeSubstrateAdapter(Path workspaceRoot) {
        this.f0Root = workspaceRoot.resolve("f0_engine").toAbsolutePath().normalize();
        this.executionAdapter = new KernelExecutionBridgeAdapter();
    }

    @Override
    public String getAdapterId() {
        return "f0-runtime";
    }

    @Override
    public SubstrateSession loadContext(TaskEnvelope task, List<MemoryHit> context) {
        SubstrateSession session = new SubstrateSession(f0Root);
        session.getState().put("taskId", task.getId().toString());
        session.getState().put("goal", task.getGoal());
        session.getState().put("available", Boolean.toString(isCargoAvailable()));
        session.getState().put("contextHits", Integer.toString(context.size()));
        return session;
    }

    @Override
    public SubstrateResult step(SubstrateSession session, String stepInstruction) {
        return new SubstrateResult(true, "[f0-step] " + stepInstruction, 0, Map.of("adapter", getAdapterId()));
    }

    @Override
    public SubstrateResult act(SubstrateSession session, SubstrateAction action) {
        if (!isCargoAvailable()) {
            return new SubstrateResult(false, "cargo not available in PATH", -1, Map.of("adapter", getAdapterId()));
        }
        if (action.getType() == SubstrateAction.Type.RUN_INFERENCE) {
            return new SubstrateResult(false, "F0 runtime adapter does not provide model inference.", -1, Map.of("adapter", getAdapterId()));
        }
        if (action.getType() == SubstrateAction.Type.RUN_COMMAND && !action.getArguments().isEmpty()) {
            return runCommand(action.getArguments(), session.getWorkspaceRoot());
        }
        return runCommand(List.of("cargo", "run", "--quiet"), session.getWorkspaceRoot());
    }

    @Override
    public SubstrateSnapshot snapshot(SubstrateSession session) {
        return new SubstrateSnapshot(session.getSessionId(), session.getWorkspaceRoot(), session.getState());
    }

    @Override
    public SubstrateSession restore(SubstrateSnapshot snapshot) {
        return new SubstrateSession(snapshot.getSessionId(), snapshot.getWorkspaceRoot(), new HashMap<>(snapshot.getState()));
    }

    public boolean isCargoAvailable() {
        try {
            Process process = new ProcessBuilder("cargo", "--version")
                .redirectErrorStream(true)
                .start();
            int exit = process.waitFor();
            return exit == 0;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }

    private SubstrateResult runCommand(List<String> command, Path workingDirectory) {
        if (!Files.exists(workingDirectory)) {
            return new SubstrateResult(false, "f0_engine directory missing", -1, Map.of("adapter", getAdapterId()));
        }
        SubstrateResult result = executionAdapter.runCommand(getAdapterId(), workingDirectory, command);
        return new SubstrateResult(result.isSuccess(), truncate(result.getOutput()), result.getExitCode(), result.getMetadata());
    }

    private String truncate(String output) {
        return output.length() > 4_000 ? output.substring(0, 4_000) : output;
    }
}
