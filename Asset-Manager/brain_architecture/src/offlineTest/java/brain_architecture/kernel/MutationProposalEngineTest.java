package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies high-level mutation proposals can be derived from task goals.
 */
public final class MutationProposalEngineTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("mutation-proposal-workspace");
        Path sourceDir = workspace.resolve(Path.of("src", "main", "java", "demo"));
        Files.createDirectories(sourceDir);
        Files.writeString(sourceDir.resolve("Feature.java"), """
            package demo;
            public final class Feature {
                public String run() { return "ok"; }
            }
            """, StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            kernel.ingestArtifact(Path.of("src/main/java/demo/Feature.java"));
            kernel.getMemoryStore().ingestText(
                ".northstar-kernel/distilled/demo/0001-p3-p3-failure.md",
                """
                # Distilled PDF Chunk

                Label: failure

                Empirical validation failure: Feature had no offline regression coverage and no reproducible verification path.
                """
            );
            kernel.getMemoryStore().ingestText(
                ".northstar-kernel/distilled/demo/0002-p4-p4-test-idea.md",
                """
                # Distilled PDF Chunk

                Label: test-idea

                Add a regression harness for Feature and verify class loading in an air-gapped workflow.
                """
            );

            TaskEnvelope task = TaskEnvelope.builder("stabilize feature after empirical validation failure and add regression coverage offline")
                .addArtifact(Path.of("src/main/java/demo/Feature.java"))
                .mode(TaskMode.MUTATION)
                .build();

            List<MutationProposal> proposals = kernel.proposeMutations(task, 5);
            assertTrue(!proposals.isEmpty(), "Expected at least one mutation proposal");
            assertTrue(proposals.stream().anyMatch(proposal ->
                proposal.getTargetPaths().stream().anyMatch(path -> path.toString().contains("FeatureGeneratedTest.java"))
            ), "Expected a generated test proposal");
            long generatedTestProposals = proposals.stream()
                .filter(proposal -> proposal.getTargetPaths().stream()
                    .map(Path::toString)
                    .anyMatch(path -> path.contains("FeatureGeneratedTest.java")))
                .count();
            assertTrue(generatedTestProposals == 1, "Expected generated test proposals to be deduplicated");

            MutationProposal generatedTestProposal = proposals.stream()
                .filter(proposal -> proposal.getTargetPaths().stream()
                    .map(Path::toString)
                    .anyMatch(path -> path.contains("FeatureGeneratedTest.java")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected generated test proposal"));
            assertTrue(generatedTestProposal.getEvidenceTags().contains("corpus.failure"), "Expected failure seed evidence");
            assertTrue(generatedTestProposal.getEvidenceTags().contains("corpus.test-idea"), "Expected test-idea seed evidence");
            assertTrue(generatedTestProposal.getRationale().toLowerCase().contains("distilled corpus"), "Expected rationale to mention distilled corpus");
            String generatedContent = generatedTestProposal.getCandidate().getOperations().get(0).getContent();
            assertTrue(generatedContent.contains("Corpus-guided regression seeds"), "Expected generated test to embed corpus seed comments");
        }

        System.out.println("MutationProposalEngineTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
