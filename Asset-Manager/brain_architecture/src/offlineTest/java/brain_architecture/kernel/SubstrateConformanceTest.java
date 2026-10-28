package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Ensures adapters honor the snapshot/restore contract.
 */
public final class SubstrateConformanceTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("substrate-conformance");
        Files.writeString(workspace.resolve("sample.txt"), "hello substrate", StandardCharsets.UTF_8);

        TaskEnvelope task = TaskEnvelope.builder("Inspect substrate contract.")
            .addArtifact(Path.of("sample.txt"))
            .build();
        List<MemoryHit> hits = List.of();

        UserSpaceSubstrateAdapter userSpace = new UserSpaceSubstrateAdapter(workspace);
        SubstrateSession session = userSpace.loadContext(task, hits);
        SubstrateResult read = userSpace.act(session, SubstrateAction.readFile(Path.of("sample.txt")));
        assertTrue(read.isSuccess(), "User-space adapter should read local files");
        SubstrateSnapshot snapshot = userSpace.snapshot(session);
        SubstrateSession restored = userSpace.restore(snapshot);
        assertTrue(restored.getSessionId().equals(snapshot.getSessionId()), "Restored session id should match snapshot");

        F0RuntimeSubstrateAdapter f0 = new F0RuntimeSubstrateAdapter(workspace);
        SubstrateSession f0Session = f0.loadContext(task, hits);
        SubstrateSnapshot f0Snapshot = f0.snapshot(f0Session);
        SubstrateSession f0Restored = f0.restore(f0Snapshot);
        assertTrue(f0Restored.getWorkspaceRoot().equals(f0Snapshot.getWorkspaceRoot()), "F0 restored workspace should match snapshot");
        SubstrateResult f0Result = f0.act(f0Session, SubstrateAction.runCommand(List.of("cargo", "--version")));
        assertTrue(f0Result.getMetadata().containsKey("adapter"), "F0 adapter should always report adapter metadata");

        System.out.println("SubstrateConformanceTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
