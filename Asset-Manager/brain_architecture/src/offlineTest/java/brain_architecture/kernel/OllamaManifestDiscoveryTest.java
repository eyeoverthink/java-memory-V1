package brain_architecture.kernel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Verifies local Ollama manifests can be resolved into real GGUF descriptors.
 */
public final class OllamaManifestDiscoveryTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("ollama-manifest-discovery");
        try {
            Path manifestsRoot = workspace.resolve(".ollama").resolve("models").resolve("manifests")
                .resolve("registry.ollama.ai").resolve("library").resolve("llama3.2");
            Path blobsRoot = workspace.resolve(".ollama").resolve("models").resolve("blobs");
            Files.createDirectories(manifestsRoot);
            Files.createDirectories(blobsRoot);

            String digest = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
            Path ggufPath = blobsRoot.resolve("sha256-" + digest);
            writeGgufWithTokenizerMetadata(ggufPath);
            Files.writeString(manifestsRoot.resolve("latest"), """
                {"schemaVersion":2,"layers":[{"mediaType":"application/vnd.ollama.image.model","digest":"sha256:%s","size":24}]}
                """.formatted(digest), StandardCharsets.UTF_8);

            FoundationalSubstrateBootstrapper bootstrapper = new FoundationalSubstrateBootstrapper(KernelConfiguration.defaultForWorkspace(workspace));
            LocalModelSubstrateCatalog catalog = bootstrapper.buildCatalog(List.of(workspace.resolve(".ollama")));

            LocalModelDescriptor descriptor = catalog.descriptorFor("llama3.2:latest");
            assertTrue(descriptor != null, "Expected manifest-backed model descriptor");
            assertTrue("ollama_gguf_model".equals(descriptor.family()), "Expected ollama_gguf_model family");
            assertTrue(descriptor.sourcePath().equals(ggufPath.toAbsolutePath().normalize()), "Expected descriptor to resolve blob path");
            TokenizerDigest tokenizerDigest = catalog.tokenizerFor("llama3.2:latest");
            assertTrue(tokenizerDigest != null, "Expected manifest-backed GGUF tokenizer digest");
            assertTrue("gguf-ggml".equals(tokenizerDigest.format()), "Expected GGUF tokenizer format");
            assertTrue(tokenizerDigest.vocabSize() == 4, "Expected GGUF vocab size from tokenizer metadata");
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("OllamaManifestDiscoveryTest passed");
    }

    private static void writeGgufWithTokenizerMetadata(Path target) throws IOException {
        ByteArrayOutputStream metadata = new ByteArrayOutputStream();
        writeMetadataString(metadata, "general.architecture", "llama");
        writeMetadataInt(metadata, "llama.context_length", 4096);
        writeMetadataInt(metadata, "tokenizer.ggml.vocab_size", 4);
        writeMetadataStringArray(metadata, "tokenizer.ggml.tokens", List.of("<s>", "hello", "world", "!"));

        ByteBuffer header = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(0x46554747);
        header.putInt(3);
        header.putLong(12L);
        header.putLong(4L);
        header.flip();
        try (OutputStream outputStream = Files.newOutputStream(target)) {
            outputStream.write(header.array(), 0, header.remaining());
            outputStream.write(metadata.toByteArray());
        }
    }

    private static void writeMetadataString(OutputStream outputStream, String key, String value) throws IOException {
        writeGgufString(outputStream, key);
        writeInt(outputStream, 8);
        writeGgufString(outputStream, value);
    }

    private static void writeMetadataInt(OutputStream outputStream, String key, int value) throws IOException {
        writeGgufString(outputStream, key);
        writeInt(outputStream, 5);
        writeInt(outputStream, value);
    }

    private static void writeMetadataStringArray(OutputStream outputStream, String key, List<String> values) throws IOException {
        writeGgufString(outputStream, key);
        writeInt(outputStream, 9);
        writeInt(outputStream, 8);
        writeLong(outputStream, values.size());
        for (String value : values) {
            writeGgufString(outputStream, value);
        }
    }

    private static void writeGgufString(OutputStream outputStream, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeLong(outputStream, bytes.length);
        outputStream.write(bytes);
    }

    private static void writeInt(OutputStream outputStream, int value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        outputStream.write(buffer.array());
    }

    private static void writeLong(OutputStream outputStream, long value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(value);
        outputStream.write(buffer.array());
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
