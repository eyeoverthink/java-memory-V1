package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * Replayable substrate snapshot.
 */
public final class SubstrateSnapshot {
    private final UUID sessionId;
    private final Path workspaceRoot;
    private final Map<String, String> state;

    public SubstrateSnapshot(UUID sessionId, Path workspaceRoot, Map<String, String> state) {
        this.sessionId = sessionId;
        this.workspaceRoot = workspaceRoot;
        this.state = state == null ? Map.of() : Map.copyOf(state);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }

    public Map<String, String> getState() {
        return state;
    }
}
