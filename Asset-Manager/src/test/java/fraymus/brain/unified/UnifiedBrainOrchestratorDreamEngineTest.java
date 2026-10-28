package fraymus.brain.unified;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import fraymus.core.OllamaBridge;
import java.util.Map;

/**
 * Test suite for Dream Engine integration with UnifiedBrainOrchestrator
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnifiedBrainOrchestratorDreamEngineTest {

    private static UnifiedBrainOrchestrator orchestrator;
    private static OllamaBridge ollamaBridge;

    @BeforeAll
    static void setUp() {
        orchestrator = new UnifiedBrainOrchestrator();
        ollamaBridge = new OllamaBridge("llama3.2");
    }

    @AfterAll
    static void tearDown() {
        if (orchestrator != null && orchestrator.isDreamEngineWired()) {
            orchestrator.unwireDreamEngine();
        }
    }

    @Test
    @Order(1)
    public void testDreamEngineWiring() {
        assertFalse(orchestrator.isDreamEngineWired());

        orchestrator.wireDreamEngine();

        assertTrue(orchestrator.isDreamEngineWired());
        assertNotNull(orchestrator.getDreamEngine());

        // Check that brain is registered
        assertEquals(1, orchestrator.getRegisteredBrainCount());
    }

    @Test
    @Order(2)
    public void testDreamEngineUnwiring() {
        orchestrator.wireDreamEngine();
        assertTrue(orchestrator.isDreamEngineWired());

        orchestrator.unwireDreamEngine();

        assertFalse(orchestrator.isDreamEngineWired());
    }

    @Test
    @Order(3)
    public void testDreamEngineIntegration() {
        orchestrator.wireDreamEngine();

        DreamEngine dreamEngine = orchestrator.getDreamEngine();
        assertNotNull(dreamEngine);
        assertEquals("DreamEngine", dreamEngine.getName());
        assertEquals("Cognitive", dreamEngine.getType());

        // Check dream stats are accessible
        assertNotNull(dreamEngine.getDreamStats());
    }

    @Test
    @Order(4)
    public void testDreamEngineWithMemoryComponents() {
        // Wire memory system first
        orchestrator.wireMemorySystem(ollamaBridge);
        orchestrator.wireDreamEngine();

        DreamEngine dreamEngine = orchestrator.getDreamEngine();
        assertNotNull(dreamEngine);

        // Dream engine should be connected to memory components
        Map<String, Object> stats = dreamEngine.getDreamStats();
        assertNotNull(stats);
    }

    @Test
    @Order(5)
    public void testRunDreamCycle() {
        orchestrator.wireDreamEngine();

        String result = orchestrator.runDreamCycle();
        assertNotNull(result);
        assertTrue(result.contains("Starting dream cycle"));
        assertTrue(result.contains("Dream cycle complete"));
        assertTrue(result.contains("Phi-coherence"));
    }

    @Test
    @Order(6)
    public void testSystemPhiCoherence() {
        orchestrator.wireDreamEngine();

        double coherence = orchestrator.getSystemPhiCoherence();
        assertTrue(coherence >= 0.0);
        assertTrue(coherence <= 1.0);
    }

    @Test
    @Order(7)
    public void testWireAllSystems() {
        // Unwire any existing systems first
        orchestrator.unwireAllSystems();
        
        assertFalse(orchestrator.isNervousSystemWired());
        assertFalse(orchestrator.isSensorySystemWired());
        assertFalse(orchestrator.isMemorySystemWired());
        assertFalse(orchestrator.isDreamEngineWired());

        orchestrator.wireAllSystems(ollamaBridge);

        assertTrue(orchestrator.isNervousSystemWired());
        assertTrue(orchestrator.isSensorySystemWired());
        assertTrue(orchestrator.isMemorySystemWired());
        assertTrue(orchestrator.isDreamEngineWired());
    }

    @Test
    @Order(8)
    public void testUnwireAllSystems() {
        orchestrator.wireAllSystems(ollamaBridge);
        assertTrue(orchestrator.isDreamEngineWired());

        orchestrator.unwireAllSystems();

        assertFalse(orchestrator.isNervousSystemWired());
        assertFalse(orchestrator.isSensorySystemWired());
        assertFalse(orchestrator.isMemorySystemWired());
        assertFalse(orchestrator.isDreamEngineWired());
    }

    @Test
    @Order(9)
    public void testDoubleWiringProtection() {
        orchestrator.wireDreamEngine();
        int initialCount = orchestrator.getRegisteredBrainCount();

        // Try to wire again - should not duplicate
        orchestrator.wireDreamEngine();

        assertEquals(initialCount, orchestrator.getRegisteredBrainCount());
    }
}
