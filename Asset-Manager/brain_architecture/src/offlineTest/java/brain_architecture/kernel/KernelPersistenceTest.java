package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies that memory and router state survive kernel restarts.
 */
public final class KernelPersistenceTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("kernel-persistence-workspace");
        Files.writeString(workspace.resolve("README.md"), "offline kernel persistence memory", StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        java.util.UUID taskId;

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            kernel.ingestArtifact(Path.of("README.md"));
            TaskEnvelope task = TaskEnvelope.builder("Remember this local workspace and stay offline.")
                .addArtifact(Path.of("README.md"))
                .build();
            KernelRunResult result = kernel.runTask(task, ValidationSuite.empty());
            taskId = result.getTrace().getTaskId();
        }

        try (OfflineKernel reopened = new OfflineKernel(configuration)) {
            assertTrue(reopened.getMemoryStore().getDocumentCount() >= 1, "Memory store should reload persisted documents");
            assertTrue(reopened.getStatus().containsKey("trackedArtifacts"), "Status should expose tracked artifacts");
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> causalStatus = (java.util.Map<String, Object>) reopened.getStatus().get("causal");
            assertTrue(causalStatus != null, "Status should expose causal statistics");
            assertTrue(((Number) causalStatus.get("nodes")).intValue() >= 2, "Causal graph should survive restart");
            String replay = reopened.loadReplay(taskId);
            assertTrue(replay.contains(taskId.toString()), "Replay should survive restart");

            MemoryBenchmarkReport report = reopened.benchmarkMemory("offline kernel persistence", List.of(Path.of("README.md")), 3);
            assertTrue(report.getWinner() != null, "Benchmark should run after restart");
        }

        System.out.println("KernelPersistenceTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
