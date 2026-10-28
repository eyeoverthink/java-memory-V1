package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Verifies project memory artifacts are written and ingested into kernel retrieval memory.
 */
public final class ProjectMemoryArtifactsTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("project-memory-artifacts");
        Path canonicalDoc = workspace.resolve(Path.of(
            "Java-Memory",
            "Asset-Manager",
            "brain_architecture",
            "NORTHSTAR_KERNEL_MEMORY_AND_FORECAST.md"
        ));
        Path architectureDoc = workspace.resolve(Path.of(
            "Java-Memory",
            "Asset-Manager",
            "brain_architecture",
            "NORTHSTAR_KERNEL_ARCHITECTURE.md"
        ));
        Path goalsDoc = workspace.resolve(Path.of(
            "Java-Memory",
            "Asset-Manager",
            "brain_architecture",
            "KERNEL_GOALS_FORECASTS.md"
        ));
        Files.createDirectories(canonicalDoc.getParent());
        Files.writeString(canonicalDoc, """
            # Canonical Memory

            This workspace is building an offline self-evolving kernel.
            """, StandardCharsets.UTF_8);
        Files.writeString(architectureDoc, """
            # Architecture Memory

            TaskEnvelope -> Planner -> Verifier -> Mutation Engine.
            """, StandardCharsets.UTF_8);
        Files.writeString(goalsDoc, """
            # Goals Memory

            Build an offline self-evolving kernel.
            """, StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            ProjectMemoryArtifactWriter.MemoryArtifacts artifacts = kernel.refreshProjectMemoryArtifacts();

            assertTrue(Files.exists(artifacts.operationalMemory()), "Expected operational memory artifact to exist");
            assertTrue(Files.exists(artifacts.forecastMemory()), "Expected forecast memory artifact to exist");
            assertTrue(Files.exists(artifacts.canonicalDoc()), "Expected canonical doc to exist");
            assertTrue(Files.exists(artifacts.architectureDoc()), "Expected architecture doc to exist");
            assertTrue(Files.exists(artifacts.goalsDoc()), "Expected goals doc to exist");

            Map<String, MemoryDocument> documents = kernel.getMemoryStore().snapshotDocuments();
            String canonicalSource = workspace.relativize(artifacts.canonicalDoc()).toString().replace('\\', '/');
            String architectureSource = workspace.relativize(artifacts.architectureDoc()).toString().replace('\\', '/');
            String goalsSource = workspace.relativize(artifacts.goalsDoc()).toString().replace('\\', '/');
            String operationalSource = workspace.relativize(artifacts.operationalMemory()).toString().replace('\\', '/');
            String forecastSource = workspace.relativize(artifacts.forecastMemory()).toString().replace('\\', '/');

            assertTrue(documents.containsKey(canonicalSource), "Expected canonical doc to be ingested into memory");
            assertTrue(documents.containsKey(architectureSource), "Expected architecture doc to be ingested into memory");
            assertTrue(documents.containsKey(goalsSource), "Expected goals doc to be ingested into memory");
            assertTrue(documents.containsKey(operationalSource), "Expected operational memory to be ingested into memory");
            assertTrue(documents.containsKey(forecastSource), "Expected forecast memory to be ingested into memory");

            List<MemoryHit> hits = kernel.getMemoryStore().search("self-build forecast memory", List.of(), 5);
            assertTrue(hits.stream().anyMatch(hit -> hit.getDocument().getSourceId().equals(forecastSource)),
                "Expected forecast artifact to be retrievable from memory");
        }

        System.out.println("ProjectMemoryArtifactsTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
