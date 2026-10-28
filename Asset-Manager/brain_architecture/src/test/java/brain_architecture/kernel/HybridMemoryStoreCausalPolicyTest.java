package brain_architecture.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HybridMemoryStoreCausalPolicyTest {

    @TempDir
    Path tempDir;

    @Test
    void searchDisablesCausalSignalsWhenPolicyIsOff() {
        KernelHypervectorSpace hypervectorSpace = new KernelHypervectorSpace();
        HybridMemoryStore store = new HybridMemoryStore(tempDir, hypervectorSpace, false);
        store.ingestText("src/main/java/example/UseMultiStepChain.java", "class UseMultiStepChain { void repair() {} }");

        List<MemoryHit> hits = store.search(
            "repair import parser chain",
            List.of(Path.of("src/main/java/example/UseMultiStepChain.java")),
            5
        );

        assertFalse(hits.isEmpty());
        MemoryHit top = hits.get(0);
        assertEquals(0.0, top.getCausalScore(), 1e-9);
        assertEquals(0.0, top.getSpuriousPenalty(), 1e-9);
        assertFalse(top.isCausallyGrounded());
        assertTrue(top.getCausalNotes().contains("causal.disabled"));

        Map<String, Object> stats = store.causalStatistics();
        assertEquals(Boolean.FALSE, stats.get("enabled"));
        assertEquals(0, stats.get("nodes"));
        assertEquals(0, stats.get("edges"));
    }

    @Test
    void statisticsExposeEnabledFlagWhenPolicyIsOn() {
        KernelHypervectorSpace hypervectorSpace = new KernelHypervectorSpace();
        HybridMemoryStore store = new HybridMemoryStore(tempDir, hypervectorSpace, true);
        store.ingestText("src/main/java/example/A.java", "class A { void run() {} }");

        Map<String, Object> stats = store.causalStatistics();
        assertNotNull(stats.get("enabled"));
        assertEquals(Boolean.TRUE, stats.get("enabled"));
    }
}
