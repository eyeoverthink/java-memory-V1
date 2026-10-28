package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Verifies transformer bundle inference can route through a Python-backed local runtime binding.
 */
public final class PythonTransformersRuntimeAdapterTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("python-transformers-runtime-adapter");
        try {
            Path bundleRoot = workspace.resolve("models").resolve("gpt2-mini");
            Files.createDirectories(bundleRoot);
            Files.writeString(bundleRoot.resolve("config.json"), """
                {
                  "model_type": "gpt2",
                  "n_layer": 2,
                  "n_head": 2,
                  "n_embd": 32,
                  "vocab_size": 64
                }
                """, StandardCharsets.UTF_8);
            Files.writeString(bundleRoot.resolve("tokenizer.json"), """
                {
                  "model": {
                    "vocab": {
                      "hello": 0,
                      "world": 1
                    }
                  }
                }
                """, StandardCharsets.UTF_8);
            Files.writeString(bundleRoot.resolve("vocab.json"), "{\"hello\":0,\"world\":1}", StandardCharsets.UTF_8);
            Files.write(bundleRoot.resolve("pytorch_model.bin"), new byte[64]);

            Path runner = workspace.resolve("tools").resolve("fake-python.cmd");
            Files.createDirectories(runner.getParent());
            Files.writeString(runner, """
                @echo off
                echo %*
                """, StandardCharsets.UTF_8);

            LocalModelRuntimeAdapter adapter = new LocalModelRuntimeAdapter(KernelConfiguration.defaultForWorkspace(workspace));
            TaskEnvelope task = TaskEnvelope.builder("say hello from bundle runtime")
                .strategy("substrate", "local-model")
                .strategy("model.id", "gpt2-mini")
                .strategy("model.root", "models")
                .strategy("model.runner", runner.toString())
                .strategy("model.backend", "python_transformers")
                .strategy("model.maxTokens", "12")
                .build();

            SubstrateSession session = adapter.loadContext(task, List.of());
            SubstrateResult result = adapter.act(
                session,
                SubstrateAction.runInference(task.getGoal(), task.getRequestedModelId(), task.getRequestedModelMaxTokens())
            );

            assertTrue(result.isSuccess(), "Expected fake Python transformers runtime to succeed");
            assertTrue("python_transformers".equals(result.getMetadata().get("backend")), "Expected python_transformers backend metadata");
            String recordedCommand = result.getMetadata().getOrDefault("command", "");
            assertTrue(recordedCommand.contains("local_transformers_runtime.py"), "Expected dedicated Python runtime module to execute through runner");
            assertTrue(bundleRoot.toString().equals(result.getMetadata().get("targetReference")), "Expected bundle root target reference in metadata");
            assertTrue(recordedCommand.contains("hello"), "Expected prompt token to reach fake runtime");
            assertTrue(recordedCommand.contains("bundle"), "Expected prompt words to reach fake runtime");
            assertTrue(recordedCommand.contains("12"), "Expected max token budget to reach fake runtime");
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("PythonTransformersRuntimeAdapterTest passed");
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
