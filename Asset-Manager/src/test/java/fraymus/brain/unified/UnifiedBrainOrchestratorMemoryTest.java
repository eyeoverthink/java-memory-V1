package fraymus.brain.unified;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import fraymus.core.OllamaBridge;

/**
 * Test suite for Memory Substrate integration with UnifiedBrainOrchestrator
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnifiedBrainOrchestratorMemoryTest {

    private static UnifiedBrainOrchestrator orchestrator;
    private static OllamaBridge ollamaBridge;

    @BeforeAll
    static void setUp() {
        orchestrator = new UnifiedBrainOrchestrator();
        ollamaBridge = new OllamaBridge("llama3.2");
    }

    @AfterAll
    static void tearDown() {
        if (orchestrator != null && orchestrator.isMemorySystemWired()) {
            orchestrator.unwireMemorySystem();
        }
    }

    @Test
    @Order(1)
    public void testMemorySystemWiring() {
        assertFalse(orchestrator.isMemorySystemWired());

        orchestrator.wireMemorySystem(ollamaBridge);

        assertTrue(orchestrator.isMemorySystemWired());
        assertNotNull(orchestrator.getEntorhinalCortex());
        assertNotNull(orchestrator.getHippocampus());

        // Check that brains are registered
        assertEquals(2, orchestrator.getRegisteredBrainCount());
    }

    @Test
    @Order(2)
    public void testMemorySystemUnwiring() {
        orchestrator.wireMemorySystem(ollamaBridge);
        assertTrue(orchestrator.isMemorySystemWired());

        orchestrator.unwireMemorySystem();

        assertFalse(orchestrator.isMemorySystemWired());
    }

    @Test
    @Order(3)
    public void testEntorhinalCortexIntegration() {
        orchestrator.wireMemorySystem(ollamaBridge);

        EntorhinalCortex entorhinal = orchestrator.getEntorhinalCortex();
        assertNotNull(entorhinal);
        assertEquals("EntorhinalCortex", entorhinal.getName());
        assertEquals("Memory", entorhinal.getType());

        // Check embedding stats are accessible
        assertNotNull(entorhinal.getEmbeddingStats());
    }

    @Test
    @Order(4)
    public void testHippocampusIntegration() {
        orchestrator.wireMemorySystem(ollamaBridge);

        HippocampusBrain hippocampus = orchestrator.getHippocampus();
        assertNotNull(hippocampus);
        assertEquals("Hippocampus", hippocampus.getName());
        assertEquals("Memory", hippocampus.getType());

        // Check consolidation stats are accessible
        assertNotNull(hippocampus.getConsolidationStats());
    }

    @Test
    @Order(5)
    public void testMemorySystemIntegration() {
        orchestrator.wireMemorySystem(ollamaBridge);

        EntorhinalCortex entorhinal = orchestrator.getEntorhinalCortex();
        HippocampusBrain hippocampus = orchestrator.getHippocampus();

        // Generate embedding
        double[] embedding = entorhinal.generateEmbedding("test memory");
        assertNotNull(embedding);
        assertEquals(384, embedding.length);

        // Consolidate memory
        String result = hippocampus.consolidateMemory("test memory");
        assertNotNull(result);
        assertTrue(result.contains("Memory consolidated"));

        // Verify consolidation stats
        assertNotNull(hippocampus.getConsolidationStats());
    }

    @Test
    @Order(6)
    public void testDoubleWiringProtection() {
        orchestrator.wireMemorySystem(ollamaBridge);
        int initialCount = orchestrator.getRegisteredBrainCount();

        // Try to wire again - should not duplicate
        orchestrator.wireMemorySystem(ollamaBridge);

        assertEquals(initialCount, orchestrator.getRegisteredBrainCount());
    }
}
