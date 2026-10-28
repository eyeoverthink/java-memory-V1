package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Verifies the kernel can route local-model inference through the substrate adapter boundary.
 */
public final class LocalModelRuntimeAdapterTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("local-model-runtime-adapter");
        try {
            Path modelsRoot = workspace.resolve("models").resolve("ollama-demo");
            Files.createDirectories(modelsRoot);
            Files.writeString(modelsRoot.resolve("Modelfile"), """
                FROM local-test-model:latest
                TEMPLATE {{ .Prompt }}
                """, StandardCharsets.UTF_8);

            Path runner = workspace.resolve("tools").resolve("fake-ollama.cmd");
            Files.createDirectories(runner.getParent());
            Files.writeString(runner, """
                @echo off
                echo %*
                """, StandardCharsets.UTF_8);

            LocalModelRuntimeAdapter adapter = new LocalModelRuntimeAdapter(KernelConfiguration.defaultForWorkspace(workspace));
            TaskEnvelope task = TaskEnvelope.builder("hello local model")
                .strategy("substrate", "local-model")
                .strategy("model.id", "ollama-demo")
                .strategy("model.root", "models")
                .strategy("model.runner", runner.toString())
                .strategy("model.backend", "ollama_cli")
                .strategy("model.maxTokens", "16")
                .build();

            SubstrateSession session = adapter.loadContext(task, List.of());
            SubstrateResult result = adapter.act(session, SubstrateAction.runInference(task.getGoal(), task.getRequestedModelId(), task.getRequestedModelMaxTokens()));

            assertTrue(result.isSuccess(), "Expected fake local model runtime to succeed");
            assertTrue(result.getOutput().contains("run"), "Expected runtime command to execute through fake runner");
            assertTrue(result.getOutput().contains("local-test-model:latest"), "Expected modelfile FROM target to be passed to runtime");
            assertTrue(result.getOutput().contains("hello"), "Expected prompt token to reach fake runtime");
            assertTrue(result.getOutput().contains("local"), "Expected prompt words to reach fake runtime");
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("LocalModelRuntimeAdapterTest passed");
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

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
