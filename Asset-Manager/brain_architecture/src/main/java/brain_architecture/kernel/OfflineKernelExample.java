package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.List;

/**
 * Standalone example for the offline kernel path.
 */
public final class OfflineKernelExample {
    public static void main(String[] args) throws Exception {
        Path workspace = Path.of(args.length > 0 ? args[0] : ".").toAbsolutePath().normalize();
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            if (args.length > 1) {
                kernel.ingestArtifact(Path.of(args[1]));
            }

            TaskEnvelope task = TaskEnvelope.builder("Inspect the workspace and prepare a reproducible offline trace.")
                .addConstraint("offline only")
                .addArtifact(Path.of("src"))
                .mode(TaskMode.SUPERVISED)
                .strategy("profile", "logical")
                .build();

            ValidationSuite suite = new ValidationSuite(
                List.of(),
                List.of(),
                List.of(),
                List.of("offline")
            );

            KernelRunResult result = kernel.runTask(task, suite);
            System.out.println("Strategy: " + result.getStrategy());
            System.out.println("Adapter: " + result.getAdapterId());
            System.out.println("Outcome: " + result.getTrace().getOutcome());
            System.out.println("Verifier passed: " + result.getVerifierResult().isSuccess());
            System.out.println("Replay trace files: " + kernel.getTraceReplayStore().listTraceFiles());
            System.out.println("Kernel status: " + kernel.getStatus());
        }
    }
}
