package fraymus;

import fraymus.contract.SystemStateContract;
import fraymus.os.FrayFS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KernelProofHarnessTest {

    @TempDir
    Path tempDir;

    @Test
    void inspectsModelManifestWithFamilyAndProvenanceCounts() throws Exception {
        Path manifest = tempDir.resolve("MODEL_SUBSTRATE_MANIFEST.tsv");
        Files.writeString(manifest, String.join("\n",
            "modelId\tfamily\tlicenseId\tprovenance\tsourceUri\tbaseModel\tderivedFrom\tallowedOperations\tsourcePath\tsizeBytes\tattributes",
            "m1\tollama_gguf_model\tunknown\tollama_manifest_blob\tollama://m1\tm1\tm1\tinspect\t/one\t100\tx=1",
            "m2\ttransformer_bundle\tunknown\thuggingface_cache_bundle\thuggingface://m2\tm2\tm2\tinspect\t/two\t200\tx=2",
            ""
        ), StandardCharsets.UTF_8);

        KernelProofHarness.ManifestSummary summary = KernelProofHarness.inspectModelManifest(manifest);
        assertTrue(summary.exists());
        assertEquals(2, summary.modelCount());
        assertEquals(1, summary.familyCounts().get("ollama_gguf_model"));
        assertEquals(1, summary.familyCounts().get("transformer_bundle"));
        assertEquals(1, summary.provenanceCounts().get("ollama_manifest_blob"));
        assertEquals(1, summary.provenanceCounts().get("huggingface_cache_bundle"));
    }

    @Test
    void emitsProofArtifactsEvenWhenSomeLiveLanesAreMissing() throws Exception {
        Path workspace = tempDir.resolve("workspace");
        Path substrateDir = workspace.resolve(".northstar-kernel").resolve("generated").resolve("substrates");
        Files.createDirectories(substrateDir);

        Files.writeString(
            substrateDir.resolve("MODEL_SUBSTRATE_MANIFEST.tsv"),
            String.join("\n",
                "modelId\tfamily\tlicenseId\tprovenance\tsourceUri\tbaseModel\tderivedFrom\tallowedOperations\tsourcePath\tsizeBytes\tattributes",
                "m1\tollama_gguf_model\tunknown\tollama_manifest_blob\tollama://m1\tm1\tm1\tinspect\t/one\t100\tx=1"
            ),
            StandardCharsets.UTF_8
        );

        Files.writeString(
            substrateDir.resolve("LOCAL_MODEL_RUNTIME_INVENTORY.md"),
            String.join("\n",
                "# Local Model Runtime Inventory",
                "- `m1` backend=`docker_llama_server` available=`true` target=`/one`"
            ),
            StandardCharsets.UTF_8
        );

        Map<String, Object> envelope = SystemStateContract.createEnvelope(
            Map.of("version", "test"),
            Map.of("usedMb", 1),
            Map.of("status", "offline"),
            Map.of("logicalCpuCores", 1),
            List.of(Map.of("name", "gravity", "status", "healthy", "score", 1.0)),
            Map.of("route", "SYSTEM_STATE")
        );

        KernelProofHarness.KernelProofContext context = new KernelProofHarness.KernelProofContext(
            workspace,
            () -> envelope,
            null,
            null,
            new FrayFS("TEST"),
            "test"
        );

        KernelProofHarness.KernelProofReport report = KernelProofHarness.run(context);
        assertEquals("FAIL", report.status());
        assertTrue(report.failCount() >= 2);
        assertFalse(report.checks().isEmpty());

        String jsonPath = String.valueOf(report.artifacts().get("jsonReportPath"));
        String markdownPath = String.valueOf(report.artifacts().get("markdownReportPath"));
        assertTrue(Files.exists(Path.of(jsonPath)));
        assertTrue(Files.exists(Path.of(markdownPath)));

        String markdown = Files.readString(Path.of(markdownPath), StandardCharsets.UTF_8);
        assertTrue(markdown.contains("Kernel Proof Report"));
    }
}
