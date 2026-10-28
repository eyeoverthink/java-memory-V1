package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Verifies refresh-memory with canon mode updates dual memory lanes deterministically.
 */
public final class CanonicalRefreshMemoryIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("canonical-refresh-memory");
        seedMemoryDocs(workspace);
        seedEvidenceWorkspace(workspace);

        Path sourceA = workspace.resolve("canon-source-a.txt");
        Path sourceB = workspace.resolve("canon-source-b.txt");
        Path sourcePdf = workspace.resolve("canon-source-c.pdf");
        Files.writeString(sourceA, """
            The offline kernel typed path is TaskEnvelope -> Planner -> Executor -> Verifier -> Memory -> Mutation Engine.
            """, StandardCharsets.UTF_8);
        Files.writeString(sourceB, """
            FrayBuilderRegistry routes all builders.
            BrainFeedbackListener is fully integrated.
            Researching websites...
            Researching websites...
            Researching websites...
            """, StandardCharsets.UTF_8);
        Files.writeString(sourcePdf, "Exocortex wrapper generation exists in local source.", StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            ProjectMemoryArtifactWriter.MemoryArtifacts first = kernel.refreshProjectMemoryArtifacts(true, List.of(sourceA, sourceB, sourcePdf));
            String firstMatrix = Files.readString(workspace.resolve("FRAYNIX_IMPLEMENTATION_AUDIT_MATRIX.tsv"), StandardCharsets.UTF_8);

            ProjectMemoryArtifactWriter.MemoryArtifacts second = kernel.refreshProjectMemoryArtifacts(true, List.of(sourceA, sourceB, sourcePdf));
            String secondMatrix = Files.readString(workspace.resolve("FRAYNIX_IMPLEMENTATION_AUDIT_MATRIX.tsv"), StandardCharsets.UTF_8);

            assertTrue(Files.exists(workspace.resolve("FRAYNIX_TRUE_OUTLINE.md")), "Expected FRAYNIX_TRUE_OUTLINE.md");
            assertTrue(Files.exists(workspace.resolve("FRAYNIX_IMPLEMENTATION_AUDIT_MATRIX.tsv")), "Expected FRAYNIX_IMPLEMENTATION_AUDIT_MATRIX.tsv");
            assertTrue(Files.exists(workspace.resolve("FRAYNIX_CONTINUITY_PROTOCOL.md")), "Expected FRAYNIX_CONTINUITY_PROTOCOL.md");
            assertTrue(Files.exists(first.operationalMemory()), "Expected generated SELF_BUILD_MEMORY.md");
            assertTrue(Files.exists(first.forecastMemory()), "Expected generated SELF_BUILD_FORECAST.md");
            assertTrue(!first.dualCanonDocs().isEmpty(), "Expected dual canon docs to be included in memory artifacts");
            assertTrue(firstMatrix.equals(secondMatrix), "Expected canon audit matrix to be stable across repeated refresh with fixed fixtures");

            Map<String, Object> status = kernel.getStatus();
            int verified = asInt(status.get("truthClaimsVerified"));
            int scaffold = asInt(status.get("truthClaimsScaffold"));
            int speculative = asInt(status.get("truthClaimsSpeculative"));
            assertTrue(verified + scaffold + speculative > 0, "Expected non-zero truth claim totals");
            assertTrue(verified >= 0 && scaffold >= 0 && speculative >= 0, "Expected non-negative truth counters");
            assertTrue(status.get("canonLastRefreshAt") != null, "Expected canon refresh timestamp");

            Map<String, MemoryDocument> documents = kernel.getMemoryStore().snapshotDocuments();
            assertTrue(documents.containsKey("FRAYNIX_TRUE_OUTLINE.md"), "Expected outline to be indexed in memory");
            assertTrue(documents.containsKey("FRAYNIX_IMPLEMENTATION_AUDIT_MATRIX.tsv"), "Expected matrix to be indexed in memory");
            assertTrue(documents.containsKey("FRAYNIX_CONTINUITY_PROTOCOL.md"), "Expected protocol to be indexed in memory");
            assertTrue(documents.containsKey(".northstar-kernel/generated/SELF_BUILD_MEMORY.md"), "Expected generated memory artifact in index");
            assertTrue(documents.containsKey(".northstar-kernel/generated/SELF_BUILD_FORECAST.md"), "Expected generated forecast artifact in index");

            assertTrue(!second.dualCanonDocs().isEmpty(), "Expected dual canon docs on repeated refresh");
        }

        System.out.println("CanonicalRefreshMemoryIntegrationTest passed");
    }

    private static int asInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static void seedMemoryDocs(Path workspace) throws Exception {
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "NORTHSTAR_KERNEL_MEMORY_AND_FORECAST.md"), "# Canonical Memory\n");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "NORTHSTAR_KERNEL_ARCHITECTURE.md"), "# Architecture Memory\n");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "KERNEL_GOALS_FORECASTS.md"), "# Goals Memory\n");
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
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "src", "main", "java", "fraymus", "exocortex", "ExocortexAbsorber.java"), "class ExocortexAbsorber {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "src", "test", "java", "fraymus", "exocortex", "ExocortexAbsorberTest.java"), "class ExocortexAbsorberTest {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "src", "main", "java", "com", "eyeoverthink", "lazarus", "LazarusEngine.java"), "class LazarusEngine {}");
        write(workspace, Path.of("Java-Memory", "Asset-Manager", "src", "main", "java", "com", "fraymus", "builders", "FrayBuilderRegistry.java"), """
            public class FrayBuilderRegistry {
                public String route() { return "FrayShellBuilder"; }
            }
            """);
        write(workspace, Path.of("f0_engine", "src", "field_engine.rs"), "pub fn field_engine() {}");
        write(workspace, Path.of("f0_engine", "src", "simd_ops.rs"), "pub fn simd_ops() {}");
        write(workspace, Path.of("f0_engine", "src", "cisa.rs"), "pub fn cisa() {}");
        write(workspace, Path.of(".northstar-kernel", "repair-memory.tsv"), "sample");
        write(workspace, Path.of(".northstar-kernel", "repair-templates.tsv"), "sample");
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
