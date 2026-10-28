package brain_architecture.kernel;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Verifies the adapter can launch a llama.cpp-compatible server process and read a completion.
 */
public final class DockerLlamaServerRuntimeAdapterTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("docker-llama-runtime-adapter");
        try {
            Path ggufPath = workspace.resolve("models").resolve("demo.gguf");
            Files.createDirectories(ggufPath.getParent());
            writeGgufHeader(ggufPath, 3, 12L, 0L);

            Path wrapper = workspace.resolve("tools").resolve("fake-docker-llama.cmd");
            Files.createDirectories(wrapper.getParent());
            String javaExecutable = Path.of(System.getProperty("java.home"), "bin", isWindows() ? "java.exe" : "java")
                .toAbsolutePath()
                .normalize()
                .toString();
            String classPath = System.getProperty("java.class.path");
            Files.writeString(wrapper, """
                @echo off
                "%s" -cp "%s" brain_architecture.kernel.FakeDockerLlamaServerMain %%*
                """.formatted(javaExecutable, classPath.replace("\"", "\"\"")), StandardCharsets.UTF_8);

            LocalModelRuntimeAdapter adapter = new LocalModelRuntimeAdapter(KernelConfiguration.defaultForWorkspace(workspace));
            TaskEnvelope task = TaskEnvelope.builder("native docker llama test")
                .strategy("substrate", "local-model")
                .strategy("model.id", "demo")
                .strategy("model.root", "models")
                .strategy("model.runner", wrapper.toString())
                .strategy("model.backend", "docker_llama_server")
                .strategy("model.maxTokens", "8")
                .build();

            SubstrateSession session = adapter.loadContext(task, List.of());
            SubstrateResult result = adapter.act(
                session,
                SubstrateAction.runInference(task.getGoal(), task.getRequestedModelId(), task.getRequestedModelMaxTokens())
            );

            assertTrue(result.isSuccess(), "Expected docker llama server backend to succeed");
            assertTrue("docker_llama_server".equals(result.getMetadata().get("backend")), "Expected docker_llama_server metadata");
            assertTrue(result.getOutput().contains("docker fake native output"), "Expected fake native output content");
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("DockerLlamaServerRuntimeAdapterTest passed");
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

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
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
