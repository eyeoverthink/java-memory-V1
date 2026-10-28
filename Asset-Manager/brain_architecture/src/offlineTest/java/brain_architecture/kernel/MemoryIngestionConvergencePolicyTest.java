package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Verifies canonical markdown ingestion policy: mirror remap, shadow suppression, and proof-gated authority.
 */
public final class MemoryIngestionConvergencePolicyTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("memory-ingestion-policy");
        Path rootAtlas = workspace.resolve("FRAYNIX_SYSTEM_ATLAS.md");
        Path corpusAtlas = workspace.resolve(Path.of("docs", "root-md-corpus", "FRAYNIX_SYSTEM_ATLAS.md"));
        Path shadowDoc = workspace.resolve(Path.of(".northstar-kernel", "shadow-123", "FRAYNIX_SYSTEM_ATLAS.md"));
        Path claimUnverified = workspace.resolve(Path.of("docs", "root-md-corpus", "CLAIM_LIVE_COMPLETE.md"));
        Path claimVerified = workspace.resolve(Path.of("docs", "root-md-corpus", "CLAIM_LIVE_COMPLETE_VERIFIED.md"));

        Files.createDirectories(corpusAtlas.getParent());
        Files.createDirectories(shadowDoc.getParent());
        Files.writeString(rootAtlas, "# Root Mirror\n\nRoot copy should be remapped to root-md-corpus.", StandardCharsets.UTF_8);
        Files.writeString(corpusAtlas, "# Canonical Atlas\n\nCanonical lane document.", StandardCharsets.UTF_8);
        Files.writeString(shadowDoc, "# Shadow Mirror\n\nThis should be skipped by policy.", StandardCharsets.UTF_8);
        Files.writeString(claimUnverified, """
            # Live Complete Claim

            This document claims the system is complete but does not cite verifier evidence.
            """, StandardCharsets.UTF_8);
        Files.writeString(claimVerified, """
            # Live Complete Verified Claim

            Evidence:
            - verify-offline-kernel.ps1
            - release-gate-latest.txt
            """, StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            int ingested = kernel.ingestArtifact(workspace);
            assertTrue(ingested > 0, "Expected ingestion to index canonical markdown files");

            Map<String, MemoryDocument> docs = kernel.getMemoryStore().snapshotDocuments();
            String canonicalAtlasSource = "docs/root-md-corpus/FRAYNIX_SYSTEM_ATLAS.md";
            assertTrue(docs.containsKey(canonicalAtlasSource), "Expected canonical root-md-corpus atlas to be indexed");
            assertTrue(!docs.containsKey("FRAYNIX_SYSTEM_ATLAS.md"), "Expected root mirror to be suppressed by remap");
            assertTrue(!docs.containsKey(".northstar-kernel/shadow-123/FRAYNIX_SYSTEM_ATLAS.md"),
                "Expected shadow mirror file to be skipped by policy");

            MemoryDocument unverified = docs.get("docs/root-md-corpus/CLAIM_LIVE_COMPLETE.md");
            MemoryDocument verified = docs.get("docs/root-md-corpus/CLAIM_LIVE_COMPLETE_VERIFIED.md");
            assertTrue(unverified != null, "Expected unverified claim doc to be indexed");
            assertTrue(verified != null, "Expected verified claim doc to be indexed");
            assertTrue(!unverified.isAuthoritative(), "Expected unverified claim doc to be non-authoritative");
            assertTrue(verified.isAuthoritative(), "Expected verified claim doc to be authoritative");

            Map<String, Object> policy = kernel.getMemoryStore().ingestionPolicyStatistics();
            assertTrue(asInt(policy.get("canonicalMirrorRemaps")) >= 1, "Expected at least one canonical mirror remap");
            assertTrue(asInt(policy.get("skippedPolicyPaths")) >= 1, "Expected policy skip count for shadow paths");
            assertTrue(asInt(policy.get("nonAuthoritativeDocuments")) >= 1, "Expected tracked non-authoritative documents");
        }

        System.out.println("MemoryIngestionConvergencePolicyTest passed");
    }

    private static int asInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
