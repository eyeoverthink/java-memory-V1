package brain_architecture.kernel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verifies PDF distillation de-duplicates repeated content and labels useful chunks.
 */
public final class PdfDistillationEngineTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("pdf-distillation-workspace");
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        HybridMemoryStore memoryStore = new HybridMemoryStore(workspace, new KernelHypervectorSpace());
        PdfDistillationEngine engine = new PdfDistillationEngine(configuration, memoryStore);

        String sharedCodeIdea = """
            [[PDF_PAGE_1]]
            package com.fraymus.core;

            public interface BrainFeedbackListener {
                void onThermalInjection(String sourceBrainId, double resonanceGoldenRatio);
                void onEpiphanySynthesized(String sourceBrainId, Object epiphanyVector);
                void onManifestationRequested(String targetBuilderId, Object intent);
            }

            This interface is the contract for the synaptic bridge and should remain air-gapped while the orchestrator routes local manifestation requests through a typed boundary.
            """;

        String failureChunk = """
            [[PDF_PAGE_3]]
            Analysis Updated with Empirical Validation

            Test Result: the system produced incomplete pseudo-code snippets, no actual working Rust SIMD implementation, no attractor detection code, and no integration with the existing runtime. Diagnosis confidence remained high because the failure was empirical, reproducible, and directly tied to missing implementation rather than vision.
            """;

        String researchChunk = """
            [[PDF_PAGE_5]]
            Researching websites and cross-referencing research on cache modulation, archive.org manuals, and community posts. This research pass is exploratory and should not be promoted into runtime logic without local verification and a typed test plan.
            """;

        Map<Path, String> sources = new LinkedHashMap<>();
        sources.put(Path.of("fraynix-brain-matter-2.pdf"), sharedCodeIdea + System.lineSeparator() + System.lineSeparator() + failureChunk);
        sources.put(Path.of("fraynix-brain-matters.pdf"), sharedCodeIdea + System.lineSeparator() + System.lineSeparator() + researchChunk);

        PdfDistillationReport report = engine.distillTextSources(sources);
        assertTrue(report.getRawChunkCount() > report.getUniqueChunkCount(), "Expected duplicate chunks to be removed");
        assertTrue(report.getDuplicateChunkCount() >= 1, "Expected at least one duplicate chunk to be counted");
        assertTrue(report.getLabelCounts().getOrDefault(PdfDistillationChunk.Label.CODE_IDEA, 0) >= 1, "Expected code idea chunks");
        assertTrue(report.getLabelCounts().getOrDefault(PdfDistillationChunk.Label.FAILURE, 0) >= 1, "Expected failure chunks");
        assertTrue(report.getLabelCounts().getOrDefault(PdfDistillationChunk.Label.RESEARCH, 0) >= 1, "Expected research chunks");
        assertTrue(report.getChunks().stream().allMatch(chunk -> Files.exists(workspace.resolve(chunk.getOutputPath()))), "Expected distilled chunk files to be written");

        List<MemoryHit> hits = memoryStore.search("empirical validation failure", List.of(), 3);
        assertTrue(!hits.isEmpty(), "Expected distilled chunks to be searchable in memory");
        assertTrue(hits.stream().anyMatch(hit -> hit.getDocument().getSourceId().contains("failure")), "Expected failure chunk in memory search results");

        System.out.println("PdfDistillationEngineTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
