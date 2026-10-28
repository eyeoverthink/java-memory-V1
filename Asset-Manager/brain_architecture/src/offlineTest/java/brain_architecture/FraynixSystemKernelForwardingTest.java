package brain_architecture;

import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.integration.unified.UnifiedTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Verifies FraynixSystem legacy and unified entrypoints share the orchestrator-owned kernel runtime.
 */
public final class FraynixSystemKernelForwardingTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("fraynix-system-kernel-forwarding");
        try {
            FraynixSystem system = new FraynixSystem(workspace);
            system.initialize();
            try {
                assertTrue(system.getOfflineKernel() != null, "Expected orchestrator-owned offline kernel during initialization");

                QuantumOracleIntegration.OracleResponse legacy = system.processQuery(
                    "inspect the local workspace through the legacy fraynix entrypoint and stay offline",
                    QuantumOracleIntegration.OracleQueryType.LOGICAL
                ).join();
                assertTrue(legacy.isSuccess(), "Expected legacy fraynix query to validate");
                assertEquals("kernel-forwarded", legacy.getMetadata().get("path"), "Expected kernel-forwarded metadata path");
                assertTrue(legacy.getContent().contains("Strategy:"), "Expected kernel summary content in legacy response");

                var unified = system.runUnifiedTask(
                    UnifiedTask.builder("inspect the local workspace through the unified gateway and stay offline").build()
                ).join();
                assertTrue(unified.isValidationPassed(), "Expected unified gateway task to validate");
                assertTrue(unified.getStrategy() != null && !unified.getStrategy().isBlank(), "Expected unified strategy");
                assertTrue(unified.getTrace() != null, "Expected unified trace payload");
                assertTrue(unified.getReplayTracePath() != null && !unified.getReplayTracePath().isBlank(), "Expected replay trace path");
                assertTrue(unified.getDiagnostics().getDiagnostics() != null, "Expected structured unified diagnostics list");
                assertTrue(unified.getMemoryHits() != null, "Expected unified memory hit list");
                assertTrue(unified.getEvolutionIterations() != null, "Expected unified evolution iteration list");
                assertTrue(unified.getJournalEntries() != null, "Expected unified journal list");
                assertTrue(unified.getMutationProposals() != null, "Expected unified proposal list");
            } finally {
                system.shutdown();
            }
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("FraynixSystemKernelForwardingTest passed");
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
