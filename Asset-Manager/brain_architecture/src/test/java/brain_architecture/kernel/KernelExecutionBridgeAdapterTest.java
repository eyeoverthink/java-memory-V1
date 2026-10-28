package brain_architecture.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KernelExecutionBridgeAdapterTest {

    @TempDir
    Path tempDir;

    @Test
    void executesCommandThroughExecutionBridge() {
        KernelExecutionBridgeAdapter adapter = new KernelExecutionBridgeAdapter();

        SubstrateResult result = adapter.runCommand(
            "user-space",
            tempDir,
            List.of("echo", "phase_d_exec")
        );

        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().toLowerCase().contains("phase_d_exec"));
        assertEquals("execution-bridge", result.getMetadata().get("executor"));
        assertEquals("user-space", result.getMetadata().get("adapter"));
    }
}
