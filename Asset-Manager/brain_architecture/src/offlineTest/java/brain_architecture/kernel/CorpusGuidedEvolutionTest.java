package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies evolution scoring prefers corpus-backed regression proposals.
 */
public final class CorpusGuidedEvolutionTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("corpus-guided-evolution-workspace");
        Path sourceDir = workspace.resolve(Path.of("src", "main", "java", "demo"));
        Files.createDirectories(sourceDir);
        Files.writeString(sourceDir.resolve("Feature.java"), """
            package demo;
            public final class Feature {
                public String run() { return "ok"; }
            }
            """, StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            kernel.ingestArtifact(Path.of("src/main/java/demo/Feature.java"));
            kernel.getMemoryStore().ingestText(
                ".northstar-kernel/distilled/demo/0001-p3-p3-failure.md",
                """
                # Distilled PDF Chunk

                Label: failure

                Empirical validation failure: Feature produced no working regression path and lacked offline verification coverage.
                """
            );
            kernel.getMemoryStore().ingestText(
                ".northstar-kernel/distilled/demo/0002-p4-p4-test-idea.md",
                """
                # Distilled PDF Chunk

                Label: test-idea

                Test idea: add a Feature regression harness that validates class loading offline and protects future mutations.
                """
            );

            TaskEnvelope task = TaskEnvelope.builder("stabilize Feature after empirical validation failure")
                .addArtifact(Path.of("src/main/java/demo/Feature.java"))
                .mode(TaskMode.MUTATION)
                .build();

            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(!report.getIterations().isEmpty(), "Expected one evolution iteration");
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.proposal() != null, "Expected a selected proposal");
            assertTrue(iteration.proposal().getTitle().contains("Generated test scaffold"), "Expected corpus-backed test proposal to win");
            assertTrue(iteration.proposal().getEvidenceTags().contains("corpus.failure"), "Expected failure evidence on selected proposal");
            assertTrue(iteration.promoted(), "Expected selected proposal to be promoted");
            assertTrue(iteration.evaluations().stream().anyMatch(evaluation ->
                evaluation.notes().contains("corpus.failure.seed")
            ), "Expected evaluation notes to include corpus failure scoring");
        }

        System.out.println("CorpusGuidedEvolutionTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
