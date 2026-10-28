package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies that repair-oriented goals do not promote generic housekeeping mutations
 * when the verifier already reports a clean workspace.
 */
public final class ValidatedRepairGoalNoOpTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("validated-repair-noop");
        Path helper = workspace.resolve("src/main/java/training/support/MultiStepChainHelper.java");
        Path consumer = workspace.resolve("src/main/java/training/multistep/UseMultiStepChain.java");

        Files.createDirectories(helper.getParent());
        Files.createDirectories(consumer.getParent());

        Files.writeString(helper, """
            package training.support;

            public final class MultiStepChainHelper {
                public String value() {
                    return "ready";
                }
            }
            """, StandardCharsets.UTF_8);

        Files.writeString(consumer, """
            package training.multistep;

            import training.support.MultiStepChainHelper;

            public final class UseMultiStepChain {
                public String render() {
                    MultiStepChainHelper helper = new MultiStepChainHelper();
                    return helper.value();
                }
            }
            """, StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair chained parser and import failures in live training while keeping the build offline")
                .addArtifact(Path.of("src/main/java/training/multistep/UseMultiStepChain.java"))
                .mode(TaskMode.MUTATION)
                .build();

            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(report.getBaselineRun().getVerifierResult().isSuccess(), "Baseline workspace should validate");
            assertTrue(report.getIterations().isEmpty(), "Validated repair goal should not promote generic mutations");
            assertTrue(!report.hasPromotedMutation(), "Validated repair goal should remain a no-op");
        }

        System.out.println("ValidatedRepairGoalNoOpTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
