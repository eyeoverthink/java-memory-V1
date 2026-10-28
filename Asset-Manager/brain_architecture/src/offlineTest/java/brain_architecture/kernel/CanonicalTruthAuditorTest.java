package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies strict claim-to-evidence auditing and status demotion behavior.
 */
public final class CanonicalTruthAuditorTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("canonical-truth-auditor");
        seedEvidenceWorkspace(workspace);

        Path sourceA = workspace.resolve("artifact-a.txt");
        Path sourceB = workspace.resolve("artifact-b.txt");
        Path sourcePdf = workspace.resolve("artifact-c.pdf");

        Files.writeString(sourceA, """
            The offline kernel typed path is TaskEnvelope -> Planner -> Executor -> Verifier -> Memory -> Mutation Engine.

            FrayBuilderRegistry routes all builders correctly and is not a stub.
            """, StandardCharsets.UTF_8);
        Files.writeString(sourceB, """
            BrainFeedbackListener is fully wired across the runtime.
            Researching websites...
            Researching websites...
            Researching websites...
            """, StandardCharsets.UTF_8);
        Files.writeString(sourcePdf, """
            Exocortex is integrated and maps external repositories.
            """, StandardCharsets.UTF_8);

        CanonicalTruthAuditor auditor = new CanonicalTruthAuditor(KernelConfiguration.defaultForWorkspace(workspace));
        CanonicalTruthAuditor.CanonicalAuditReport report = auditor.audit(List.of(sourceA, sourceB, sourcePdf));

        assertTrue(Files.exists(report.outlinePath()), "Expected canonical outline to be written");
        assertTrue(Files.exists(report.matrixPath()), "Expected audit matrix to be written");
        assertTrue(Files.exists(report.protocolPath()), "Expected continuity protocol to be written");
        assertTrue(report.verifiedClaims() > 0, "Expected at least one verified claim");
        assertTrue(report.scaffoldClaims() > 0, "Expected at least one scaffold claim");
        assertTrue(report.speculativeClaims() > 0, "Expected at least one speculative claim");

        ClaimRecord pipelineClaim = findClaim(report.claims(), "TaskEnvelope -> Planner -> Executor -> Verifier");
        assertTrue(pipelineClaim.status() == ClaimStatus.VERIFIED, "Pipeline claim should be VERIFIED");

        ClaimRecord builderClaim = findClaim(report.claims(), "FrayBuilderRegistry routes all builders");
        assertTrue(builderClaim.status() == ClaimStatus.SCAFFOLD, "Builder claim should be SCAFFOLD because registry is stubbed");

        ClaimRecord bridgeClaim = findClaim(report.claims(), "BrainFeedbackListener is fully wired");
        assertTrue(bridgeClaim.status() == ClaimStatus.SPECULATIVE, "Missing BrainFeedbackListener should be SPECULATIVE");

        String matrix = Files.readString(report.matrixPath(), StandardCharsets.UTF_8);
        assertTrue(matrix.contains("low-authority context"), "Expected low-authority drift note in matrix");

        CanonicalTruthAuditor.CanonicalAuditStatus status = auditor.loadLatestStatus();
        assertTrue(status.verifiedClaims() == report.verifiedClaims(), "Status should persist verified count");
        assertTrue(status.scaffoldClaims() == report.scaffoldClaims(), "Status should persist scaffold count");
        assertTrue(status.speculativeClaims() == report.speculativeClaims(), "Status should persist speculative count");

        System.out.println("CanonicalTruthAuditorTest passed");
    }

    private static void seedEvidenceWorkspace(Path workspace) throws Exception {
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "OfflineKernel.java"), "class OfflineKernel {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "TaskEnvelope.java"), "class TaskEnvelope {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "TaskPlanner.java"), "class TaskPlanner {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "KernelVerifier.java"), "class KernelVerifier {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "HybridMemoryStore.java"), "class HybridMemoryStore {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "ShadowMutationEngine.java"), "class ShadowMutationEngine {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "MutationProposalEngine.java"), "class MutationProposalEngine {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "VerifierDiagnostic.java"), "class VerifierDiagnostic {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "RepairMemoryStore.java"), "class RepairMemoryStore {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "src", "main", "java", "com", "eyeoverthink", "lazarus", "LazarusEngine.java"), "class LazarusEngine {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "src", "main", "java", "fraymus", "exocortex", "ExocortexAbsorber.java"), "class ExocortexAbsorber {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "src", "test", "java", "fraymus", "exocortex", "ExocortexAbsorberTest.java"), "class ExocortexAbsorberTest {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "src", "main", "java", "com", "fraymus", "builders", "FrayBuilderRegistry.java"), """
            public class FrayBuilderRegistry {
                public String route() {
                    return "FrayShellBuilder"; // Default for stub
                }
            }
            """);
        write(workspace, Path.of("f0_engine", "src", "field_engine.rs"), "pub fn field_engine() {}");
        write(workspace, Path.of("f0_engine", "src", "simd_ops.rs"), "pub fn simd_ops() {}");
        write(workspace, Path.of("f0_engine", "src", "cisa.rs"), "pub fn cisa() {}");
    }

    private static ClaimRecord findClaim(List<ClaimRecord> claims, String needle) {
        return claims.stream()
            .filter(claim -> claim.claimText().contains(needle))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Claim not found: " + needle));
    }

    private static void write(Path workspace, Path relative, String content) throws Exception {
        Path target = workspace.resolve(relative);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content, StandardCharsets.UTF_8);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
