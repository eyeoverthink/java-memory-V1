package brain_architecture.kernel;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Verifies the kernel can route GGUF loading through the dedicated Java runtime module.
 */
public final class JavaGgufRuntimeAdapterTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("java-gguf-runtime-adapter");
        try {
            Path ggufPath = workspace.resolve("models").resolve("demo.gguf");
            Files.createDirectories(ggufPath.getParent());
            writeGgufHeader(ggufPath, 3, 12L, 0L);

            String javaExecutable = Path.of(System.getProperty("java.home"), "bin", isWindows() ? "java.exe" : "java")
                .toAbsolutePath()
                .normalize()
                .toString();

            LocalModelRuntimeAdapter adapter = new LocalModelRuntimeAdapter(KernelConfiguration.defaultForWorkspace(workspace));
            TaskEnvelope task = TaskEnvelope.builder("inspect gguf runtime")
                .strategy("substrate", "local-model")
                .strategy("model.id", "demo")
                .strategy("model.root", "models")
                .strategy("model.runner", javaExecutable)
                .strategy("model.backend", "java_gguf_primitives")
                .strategy("model.maxTokens", "8")
                .build();

            SubstrateSession session = adapter.loadContext(task, List.of());
            SubstrateResult result = adapter.act(
                session,
                SubstrateAction.runInference(task.getGoal(), task.getRequestedModelId(), task.getRequestedModelMaxTokens())
            );

            assertTrue(result.isSuccess(), "Expected Java GGUF runtime to succeed");
            assertTrue("java_gguf_primitives".equals(result.getMetadata().get("backend")), "Expected java_gguf_primitives backend metadata");
            assertTrue(result.getOutput().contains("runtime.mode=gguf_primitives"), "Expected dedicated GGUF runtime summary");
            assertTrue(result.getOutput().contains("generation=primitive_tokens"), "Expected honest lower-level GGUF primitive output");
            assertTrue(result.getOutput().contains("tokens.prompt="), "Expected prompt token trace");
            assertTrue(result.getOutput().contains("tokens.generated="), "Expected generated token trace");
            assertTrue(result.getOutput().contains("generated.text="), "Expected generated text trace");
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("JavaGgufRuntimeAdapterTest passed");
    }

    private static void writeGgufHeader(Path target, int version, long tensorCount, long metadataCount) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(0x46554747);
        header.putInt(version);
        header.putLong(tensorCount);
        header.putLong(metadataCount);
        header.flip();
        try (OutputStream outputStream = Files.newOutputStream(target)) {
            outputStream.write(header.array(), 0, header.remaining());
        }
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

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
