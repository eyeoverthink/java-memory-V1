package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies bounded autonomous evolution can promote a generated mutation.
 */
public final class EvolutionLoopTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("evolution-loop-workspace");
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
            TaskEnvelope task = TaskEnvelope.builder("add regression tests for demo feature")
                .addArtifact(Path.of("src/main/java/demo/Feature.java"))
                .mode(TaskMode.MUTATION)
                .build();

            EvolutionReport report = kernel.evolveTask(task, 2);
            assertTrue(!report.getIterations().isEmpty(), "Expected at least one evolution iteration");
            assertTrue(report.hasPromotedMutation(), "Expected an evolution mutation to be promoted");

            Path generatedTest = workspace.resolve(Path.of("src", "offlineTest", "java", "demo", "FeatureGeneratedTest.java"));
            assertTrue(Files.exists(generatedTest), "Expected generated test to exist after evolution");
        }

        System.out.println("EvolutionLoopTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
