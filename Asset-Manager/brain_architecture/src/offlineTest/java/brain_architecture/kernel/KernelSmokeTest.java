package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * No-dependency smoke test for the offline kernel path.
 */
public final class KernelSmokeTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("offline-kernel-workspace");
        Path sourceDir = workspace.resolve("src");
        Files.createDirectories(sourceDir);
        Files.writeString(workspace.resolve("README.md"), "Fraynix kernel offline memory document", StandardCharsets.UTF_8);
        Files.writeString(sourceDir.resolve("App.java"), "public class App { int value = 1; }", StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            int count = kernel.ingestArtifact(workspace.resolve("README.md"));
            assertTrue(count == 1, "Expected README ingestion");

            TaskEnvelope task = TaskEnvelope.builder("Analyze the local code and keep the runtime offline.")
                .addConstraint("offline only")
                .addArtifact(Path.of("README.md"))
                .addArtifact(Path.of("src/App.java"))
                .mode(TaskMode.SUPERVISED)
                .build();

            KernelRunResult runResult = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(runResult.getVerifierResult().isSuccess(), "Kernel run should validate");
            assertTrue(!runResult.getMemoryHits().isEmpty(), "Kernel run should return memory hits");
            assertTrue(kernel.getJournal().size() >= 3, "Journal should contain ingestion and task entries");
            String replay = kernel.loadReplay(runResult.getTrace().getTaskId());
            assertTrue(replay.contains("outcome=validated"), "Replay file should contain validated outcome");

            MemoryBenchmarkReport benchmarkReport = kernel.benchmarkMemory(
                "offline kernel",
                List.of(Path.of("README.md"), Path.of("src/App.java")),
                3
            );
            assertTrue(benchmarkReport.getWinner() != null, "Benchmark should choose a winner");

            MutationCandidate candidate = new MutationCandidate(
                MutationCandidate.Scope.WHOLE_CODEBASE,
                List.of(MutationOperation.searchReplace(Path.of("src/App.java"), "value = 1", "value = 2")),
                "Bump value constant",
                List.of("Touches application source")
            );

            ShadowMutationEngine.MutationReport report = kernel.applyMutationCandidate(candidate, ValidationSuite.empty());
            assertTrue(report.promoted(), "Mutation should promote after validation");
            String updated = Files.readString(sourceDir.resolve("App.java"), StandardCharsets.UTF_8);
            assertTrue(updated.contains("value = 2"), "Mutation should update the source file");
            long stagingDirectories = Files.list(configuration.getStateRoot())
                .filter(Files::isDirectory)
                .filter(path -> {
                    String name = path.getFileName().toString();
                    return name.startsWith("shadow-") || name.startsWith("backup-");
                })
                .count();
            assertTrue(stagingDirectories == 0, "Mutation apply should clean shadow staging directories");
        }

        System.out.println("KernelSmokeTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
