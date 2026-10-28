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
 * Verifies local model assets can be abstracted into kernel-owned substrate memory.
 */
public final class FoundationalSubstrateBootstrapTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("foundational-substrate-bootstrap");
        try {
            Path modelsRoot = workspace.resolve("models");
            Path gpt2Root = modelsRoot.resolve("gpt2-mini");
            Path ollamaRoot = modelsRoot.resolve("ollama-demo");
            Files.createDirectories(gpt2Root);
            Files.createDirectories(ollamaRoot);

            Files.writeString(gpt2Root.resolve("config.json"), """
                {
                  "model_type": "gpt2",
                  "n_layer": 4,
                  "n_head": 4,
                  "n_embd": 64,
                  "vocab_size": 256
                }
                """, StandardCharsets.UTF_8);
            Files.writeString(gpt2Root.resolve("README.md"), """
                ---
                license: mit
                base_model: openai/gpt2
                derived_from: openai/gpt2
                ---
                Local GPT-2 substrate fixture for Fraynix.
                """, StandardCharsets.UTF_8);
            Files.writeString(gpt2Root.resolve("tokenizer.json"), """
                {
                  "model": {
                    "vocab": {
                      "hello": 0,
                      "<|assistant|>": 1
                    }
                  }
                }
                """, StandardCharsets.UTF_8);
            Files.writeString(gpt2Root.resolve("vocab.json"), "{\"hello\":0,\"world\":1,\"<|assistant|>\":2}", StandardCharsets.UTF_8);
            Files.writeString(gpt2Root.resolve("merges.txt"), "#version: 0.2\nh e\nhe llo\n", StandardCharsets.UTF_8);
            Files.write(gpt2Root.resolve("pytorch_model.bin"), new byte[128]);

            Path gguf = modelsRoot.resolve("demo.gguf");
            Path bareGguf = modelsRoot.resolve("bare.gguf");
            writeGgufWithTokenizerMetadata(gguf);
            writeBareGguf(bareGguf);
            Files.writeString(ollamaRoot.resolve("Modelfile"), """
                FROM ../demo.gguf
                PARAMETER temperature 0.2
                TEMPLATE {{ .Prompt }}
                """, StandardCharsets.UTF_8);

            try (OfflineKernel kernel = new OfflineKernel(KernelConfiguration.defaultForWorkspace(workspace))) {
                FoundationalSubstrateReport report = kernel.bootstrapFoundationalSubstrates(List.of(Path.of("models")));

                assertTrue(report.artifactPaths().size() == 7, "Expected seven generated substrate artifacts");
                assertTrue(report.modelDescriptors().size() >= 3, "Expected gguf, gpt2 bundle, and modelfile descriptors");
                assertTrue(report.tokenizerDigests().size() >= 2, "Expected bundle and GGUF tokenizer digests");
                assertTrue(report.runtimeDescriptors().size() >= 3, "Expected runtime descriptors for discovered model assets");
                assertTrue(report.familyCounts().getOrDefault("gpt2_bundle", 0L) >= 1L, "Expected GPT-2 bundle family");
                assertTrue(report.familyCounts().getOrDefault("gguf_model", 0L) >= 1L, "Expected GGUF family");
                assertTrue(report.familyCounts().getOrDefault("ollama_modelfile", 0L) >= 1L, "Expected Ollama modelfile family");
                assertTrue(report.licenseCounts().getOrDefault("mit", 0L) >= 1L, "Expected MIT license ledger count");
                assertTrue(report.provenanceCounts().getOrDefault("local_transformer_bundle", 0L) >= 1L, "Expected local transformer provenance");
                assertTrue(report.provenanceCounts().getOrDefault("local_gguf_blob", 0L) >= 1L, "Expected local GGUF provenance");
                assertTrue(report.provenanceCounts().getOrDefault("ollama_modelfile", 0L) >= 1L, "Expected Ollama modelfile provenance");
                assertTrue(report.allowedOperationCounts().getOrDefault("incorporate", 0L) >= 1L,
                    "Expected permissive license to unlock incorporate operation");
                assertTrue(report.tokenizerDigests().stream().noneMatch(digest -> "bare".equals(digest.modelId())),
                    "Expected GGUF without tokenizer metadata to be skipped");
                assertTrue(Files.exists(report.languageArtifact()), "Expected language substrate artifact");
                assertTrue(Files.exists(report.mathArtifact()), "Expected math substrate artifact");
                assertTrue(Files.exists(report.modelInventoryArtifact()), "Expected model inventory artifact");
                assertTrue(Files.exists(report.modelManifestArtifact()), "Expected model manifest artifact");
                assertTrue(Files.exists(report.tokenizerInventoryArtifact()), "Expected tokenizer inventory artifact");
                assertTrue(Files.exists(report.tokenizerManifestArtifact()), "Expected tokenizer manifest artifact");
                assertTrue(Files.exists(report.runtimeInventoryArtifact()), "Expected runtime inventory artifact");

                String inventory = Files.readString(report.modelInventoryArtifact(), StandardCharsets.UTF_8);
                assertTrue(inventory.contains("gpt2_bundle"), "Expected gpt2 bundle entry in inventory");
                assertTrue(inventory.contains("gguf_model"), "Expected gguf entry in inventory");
                assertTrue(inventory.contains("ollama_modelfile"), "Expected modelfile entry in inventory");
                assertTrue(inventory.contains("license=`mit`"), "Expected license ledger in inventory");
                assertTrue(inventory.contains("provenance=`local_transformer_bundle`"), "Expected provenance ledger in inventory");
                assertTrue(inventory.contains("allowedOps: inspect, abstract, benchmark, route, tokenize, quantize, distill, fine_tune, incorporate, export_metadata"),
                    "Expected allowed operation ledger in inventory");
                String tokenizerInventory = Files.readString(report.tokenizerInventoryArtifact(), StandardCharsets.UTF_8);
                assertTrue(tokenizerInventory.contains("huggingface-bpe"), "Expected tokenizer digest entry in inventory");
                assertTrue(tokenizerInventory.contains("gguf-ggml"), "Expected GGUF tokenizer digest entry in inventory");
                String runtimeInventory = Files.readString(report.runtimeInventoryArtifact(), StandardCharsets.UTF_8);
                assertTrue(runtimeInventory.contains("ollama_cli"), "Expected Ollama runtime entry in runtime inventory");
                String modelManifest = Files.readString(report.modelManifestArtifact(), StandardCharsets.UTF_8);
                assertTrue(modelManifest.startsWith("modelId\tfamily\tlicenseId\tprovenance\tsourceUri\tbaseModel\tderivedFrom\tallowedOperations"),
                    "Expected expanded model manifest header");
                assertTrue(modelManifest.contains("gpt2-mini\tgpt2_bundle\tmit\tlocal_transformer_bundle"),
                    "Expected expanded ledger row for GPT-2 bundle");

                List<MemoryHit> hits = kernel.getMemoryStore().search("gpt2 tokenizer gguf model substrate", List.of(), 5);
                assertTrue(!hits.isEmpty(), "Expected substrate artifacts to be indexed into memory");
            }
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("FoundationalSubstrateBootstrapTest passed");
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

    private static void writeBareGguf(Path target) throws IOException {
        ByteArrayOutputStream metadata = new ByteArrayOutputStream();
        writeMetadataString(metadata, "general.architecture", "llama");
        writeMetadataInt(metadata, "llama.context_length", 2048);

        ByteBuffer header = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(0x46554747);
        header.putInt(3);
        header.putLong(8L);
        header.putLong(2L);
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
