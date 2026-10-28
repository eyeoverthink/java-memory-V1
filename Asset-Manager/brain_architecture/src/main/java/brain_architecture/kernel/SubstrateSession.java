package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Live substrate session state.
 */
public final class SubstrateSession {
    private final UUID sessionId;
    private final Path workspaceRoot;
    private final Map<String, String> state;

    public SubstrateSession(Path workspaceRoot) {
        this(UUID.randomUUID(), workspaceRoot, new HashMap<>());
    }

    public SubstrateSession(UUID sessionId, Path workspaceRoot, Map<String, String> state) {
        this.sessionId = sessionId;
        this.workspaceRoot = workspaceRoot;
        this.state = new HashMap<>(state);
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
