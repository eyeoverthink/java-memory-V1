package brain_architecture.integration;

import brain_architecture.core.BrainType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

/**
 * Verifies legacy SystemOrchestrator query entrypoints forward into the unified kernel path.
 */
public final class SystemOrchestratorKernelForwardingTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("system-orchestrator-kernel-forwarding");
        try {
            SystemOrchestrator orchestrator = new SystemOrchestrator(workspace);
            try {
                QuantumOracleIntegration.OracleResponse single = orchestrator.processQuery(
                    "inspect the local workspace and keep execution offline",
                    QuantumOracleIntegration.OracleQueryType.LOGICAL
                ).join();
                assertTrue(single.isSuccess(), "Expected forwarded single-brain query to validate");
                assertEquals("kernel-forwarded", single.getMetadata().get("path"), "Expected kernel-forwarded metadata path");
                assertEquals("SystemOrchestrator.processQuery", single.getMetadata().get("legacyEntrypoint"), "Expected legacy entrypoint metadata");
                assertEquals("logical", single.getMetadata().get("preferredBrain"), "Expected logical preferred brain");
                assertTrue(single.getContent().contains("Kernel outcome:"), "Expected kernel summary content");
                assertTrue(orchestrator.getSystemStatus().containsKey("offlineKernel"), "Expected kernel status in orchestrator system status");

                QuantumOracleIntegration.OracleResponse multi = orchestrator.processMultiBrainQuery(
                    "inspect the local workspace with logical and memory context while staying offline",
                    QuantumOracleIntegration.OracleQueryType.AUTO,
                    new BrainType[] { BrainType.LOGICAL, BrainType.MEMORY }
                ).join();
                assertTrue(multi.isSuccess(), "Expected forwarded multi-brain query to validate");
                assertEquals("SystemOrchestrator.processMultiBrainQuery", multi.getMetadata().get("legacyEntrypoint"), "Expected multi-brain legacy entrypoint metadata");
                assertEquals("logical,memory", multi.getMetadata().get("requestedBrains"), "Expected requested brain set metadata");
                assertEquals(BrainType.LOGICAL, multi.getBrainType(), "Expected first requested brain to anchor compatibility response");
            } finally {
                orchestrator.shutdown();
            }
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("SystemOrchestratorKernelForwardingTest passed");
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (root == null || !Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Best-effort temp cleanup only.
                }
            });
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new IllegalStateException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
