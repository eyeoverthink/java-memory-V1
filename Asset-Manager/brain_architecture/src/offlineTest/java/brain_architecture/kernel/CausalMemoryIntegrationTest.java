package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Verifies that causal grounding in hybrid retrieval rewards structural dependencies
 * and penalizes coincidental lexical overlap.
 */
public final class CausalMemoryIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("kernel-causal-memory");
        Path helper = workspace.resolve("src/main/java/demo/support/Helper.java");
        Path consumer = workspace.resolve("src/main/java/demo/UseHelper.java");
        Path coincidence = workspace.resolve("docs/coincidence.md");

        Files.createDirectories(helper.getParent());
        Files.createDirectories(consumer.getParent());
        Files.createDirectories(coincidence.getParent());

        Files.writeString(helper, """
            package demo.support;

            public final class Helper {
                public String value() {
                    return "ok";
                }
            }
            """, StandardCharsets.UTF_8);

        Files.writeString(consumer, """
            package demo;

            import demo.support.Helper;

            public final class UseHelper {
                public String render() {
                    Helper helper = new Helper();
                    return helper.value();
                }
            }
            """, StandardCharsets.UTF_8);

        Files.writeString(coincidence, """
            helper render value coincidence coincidence coincidence
            """, StandardCharsets.UTF_8);

        HybridMemoryStore store = new HybridMemoryStore(workspace, new KernelHypervectorSpace());
        store.ingestPath(helper);
        store.ingestPath(consumer);
        store.ingestPath(coincidence);

        List<MemoryHit> hits = store.search("repair helper import render value", List.of(), 5);
        MemoryHit helperConsumerHit = findHit(hits, "src/main/java/demo/UseHelper.java");
        MemoryHit coincidenceHit = findHit(hits, "docs/coincidence.md");

        assertTrue(helperConsumerHit != null, "Expected UseHelper.java to be retrieved");
        assertTrue(coincidenceHit != null, "Expected coincidence.md to be retrieved");
        assertTrue(helperConsumerHit.isCausallyGrounded(), "Expected UseHelper.java to be causally grounded");
        assertTrue(helperConsumerHit.getCausalScore() > coincidenceHit.getCausalScore(),
            "Expected structural dependency hit to have stronger causal score");
        assertTrue(coincidenceHit.getSpuriousPenalty() > 0.0,
            "Expected coincidence.md to receive a spurious correlation penalty");
        assertTrue(helperConsumerHit.getTotalScore() > coincidenceHit.getTotalScore(),
            "Expected causally grounded hit to outrank coincidental lexical overlap");

        Map<String, Object> causalStats = store.causalStatistics();
        assertTrue(((Number) causalStats.get("nodes")).intValue() >= 4, "Expected causal graph nodes to be created");
        assertTrue(((Number) causalStats.get("edges")).intValue() >= 3, "Expected causal graph edges to be created");

        System.out.println("CausalMemoryIntegrationTest passed");
    }

    private static MemoryHit findHit(List<MemoryHit> hits, String sourceId) {
        for (MemoryHit hit : hits) {
            if (hit.getDocument().getSourceId().equals(sourceId)) {
                return hit;
            }
        }
        return null;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
