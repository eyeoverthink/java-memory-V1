package brain_architecture.kernel;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KernelConfigurationCausalPolicyTest {

    @Test
    void defaultConfigurationEnablesCausalPolicy() {
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(Path.of("."));
        assertTrue(configuration.isCausalPolicyEnabled());
    }

    @Test
    void constructorSupportsDisablingCausalPolicy() {
        Path workspace = Path.of(".").toAbsolutePath().normalize();
        KernelConfiguration configuration = new KernelConfiguration(
            workspace,
            workspace.resolve(".northstar-kernel"),
            123L,
            true,
            true,
            false,
            Set.of(".git")
        );

        assertFalse(configuration.isCausalPolicyEnabled());
        assertFalse(configuration.withWorkspaceRoot(workspace).isCausalPolicyEnabled());
    }
}
