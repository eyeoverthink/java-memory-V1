package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies the kernel can propose and promote a missing offline verification surface.
 */
public final class ToolchainBootstrapTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("toolchain-bootstrap-workspace");
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
            TaskEnvelope task = TaskEnvelope.builder("make this workspace reproducible and verify offline")
                .addArtifact(Path.of("src/main/java/demo/Feature.java"))
                .mode(TaskMode.MUTATION)
                .build();

            List<MutationProposal> proposals = kernel.proposeMutations(task, 6);
            MutationProposal bootstrap = proposals.stream()
                .filter(proposal -> proposal.getTargetPaths().stream()
                    .map(Path::toString)
                    .map(path -> path.replace('\\', '/'))
                    .anyMatch(path -> path.equals("offline-toolchain.toml")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected an offline toolchain bootstrap proposal"));

            assertTrue(bootstrap.getTargetPaths().stream()
                .map(Path::toString)
                .map(path -> path.replace('\\', '/'))
                .anyMatch(path -> path.equals("scripts/verify-workspace.ps1")), "Expected a generated verify script target");

            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(report.hasPromotedMutation(), "Expected toolchain bootstrap evolution to promote a mutation");
            assertTrue(Files.exists(workspace.resolve("offline-toolchain.toml")), "Expected offline toolchain manifest to exist");
            assertTrue(Files.exists(workspace.resolve(Path.of("scripts", "verify-workspace.ps1"))), "Expected verify script to exist");

            WorkspaceProfile profile = kernel.profileWorkspace();
            assertTrue(profile.hasOfflineToolchainManifest(), "Expected workspace profile to detect generated manifest");
            assertTrue(!profile.getRecommendedValidationSuite().allCommands().isEmpty(), "Expected generated manifest to produce validation commands");
        }

        System.out.println("ToolchainBootstrapTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
