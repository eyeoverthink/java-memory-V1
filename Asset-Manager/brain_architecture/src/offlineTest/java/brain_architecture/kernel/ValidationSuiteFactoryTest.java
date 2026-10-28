package brain_architecture.kernel;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies manifest and heuristic validation discovery.
 */
public final class ValidationSuiteFactoryTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("validation-suite-factory");
        Files.writeString(workspace.resolve("offline-toolchain.toml"), """
            [kernel]
            verify = "scripts/verify-offline-kernel.ps1"

            [rust]
            verify = "scripts/verify-f0-engine.ps1"
            """);
        Files.createDirectories(workspace.resolve("scripts"));

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        ValidationSuiteFactory factory = new ValidationSuiteFactory(configuration);
        WorkspaceProfile profile = factory.profileWorkspace();

        assertTrue(profile.hasOfflineToolchainManifest(), "Manifest should be detected");
        assertTrue(profile.getRecommendedValidationSuite().allCommands().stream()
            .anyMatch(command -> command.getLabel().equals("kernel.verify")), "Kernel verify should be discovered");

        System.out.println("ValidationSuiteFactoryTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
