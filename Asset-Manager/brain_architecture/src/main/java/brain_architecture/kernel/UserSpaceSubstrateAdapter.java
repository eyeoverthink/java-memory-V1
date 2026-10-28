package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User-space substrate adapter using local filesystem and shell commands.
 */
public final class UserSpaceSubstrateAdapter implements SubstrateAdapter {
    private final Path workspaceRoot;
    private final KernelExecutionBridgeAdapter executionAdapter;

    public UserSpaceSubstrateAdapter(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot.toAbsolutePath().normalize();
        this.executionAdapter = new KernelExecutionBridgeAdapter();
    }

    @Override
    public String getAdapterId() {
        return "user-space";
    }

    @Override
    public SubstrateSession loadContext(TaskEnvelope task, List<MemoryHit> context) {
        SubstrateSession session = new SubstrateSession(workspaceRoot);
        session.getState().put("taskId", task.getId().toString());
        session.getState().put("goal", task.getGoal());
        session.getState().put("contextHits", Integer.toString(context.size()));
        return session;
    }

    @Override
    public SubstrateResult step(SubstrateSession session, String stepInstruction) {
        session.getState().put("lastStep", stepInstruction);
        return new SubstrateResult(true, stepInstruction, 0, Map.of("adapter", getAdapterId()));
    }

    @Override
    public SubstrateResult act(SubstrateSession session, SubstrateAction action) {
        try {
            return switch (action.getType()) {
                case READ_FILE -> readFile(session, action.getTarget());
                case LIST_DIRECTORY -> listDirectory(session, action.getTarget());
                case RUN_COMMAND -> runCommand(session, action.getArguments());
                case RUN_INFERENCE -> new SubstrateResult(false, "User-space adapter does not provide model inference.", -1, Map.of("adapter", getAdapterId()));
            };
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new SubstrateResult(false, e.getMessage(), -1, Map.of("adapter", getAdapterId()));
        }
    }

    @Override
    public SubstrateSnapshot snapshot(SubstrateSession session) {
        return new SubstrateSnapshot(session.getSessionId(), session.getWorkspaceRoot(), session.getState());
    }

    @Override
    public SubstrateSession restore(SubstrateSnapshot snapshot) {
        return new SubstrateSession(snapshot.getSessionId(), snapshot.getWorkspaceRoot(), new HashMap<>(snapshot.getState()));
    }

    private SubstrateResult readFile(SubstrateSession session, Path target) throws IOException {
        Path file = resolveWithinWorkspace(session.getWorkspaceRoot(), target);
        String content = Files.readString(file, StandardCharsets.UTF_8);
        return new SubstrateResult(true, truncate(content), 0, Map.of("path", file.toString()));
    }

    private SubstrateResult listDirectory(SubstrateSession session, Path target) throws IOException {
        Path directory = resolveWithinWorkspace(session.getWorkspaceRoot(), target);
        try (var stream = Files.list(directory)) {
            List<String> entries = stream
                .map(path -> path.getFileName().toString())
                .sorted()
                .limit(100)
                .toList();
            return new SubstrateResult(true, String.join(System.lineSeparator(), entries), 0, Map.of("path", directory.toString()));
        }
    }

    private SubstrateResult runCommand(SubstrateSession session, List<String> command) throws IOException, InterruptedException {
        SubstrateResult result = executionAdapter.runCommand(getAdapterId(), session.getWorkspaceRoot(), command);
        return new SubstrateResult(result.isSuccess(), truncate(result.getOutput()), result.getExitCode(), result.getMetadata());
    }

    private Path resolveWithinWorkspace(Path workspaceRoot, Path target) throws IOException {
        Path resolved = (target == null ? workspaceRoot : workspaceRoot.resolve(target)).toAbsolutePath().normalize();
        if (!resolved.startsWith(workspaceRoot)) {
            throw new IOException("Path escapes workspace: " + target);
        }
        return resolved;
    }

    private String truncate(String content) {
        return content.length() > 4_000 ? content.substring(0, 4_000) : content;
    }
}
