package fraymus.brain.unified;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import fraymus.core.OllamaBridge;

/**
 * Test suite for Sensory Expansion integration with UnifiedBrainOrchestrator
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnifiedBrainOrchestratorSensoryTest {

    private static UnifiedBrainOrchestrator orchestrator;
    private static OllamaBridge ollamaBridge;

    @BeforeAll
    static void setUp() {
        orchestrator = new UnifiedBrainOrchestrator();
        ollamaBridge = new OllamaBridge("llama3.2");
    }

    @AfterAll
    static void tearDown() {
        if (orchestrator != null && orchestrator.isSensorySystemWired()) {
            orchestrator.unwireSensorySystem();
        }
    }

    @Test
    @Order(1)
    public void testSensorySystemWiring() {
        assertFalse(orchestrator.isSensorySystemWired());

        orchestrator.wireSensorySystem(ollamaBridge);

        assertTrue(orchestrator.isSensorySystemWired());
        assertNotNull(orchestrator.getOccipitalLobe());
        assertNotNull(orchestrator.getTemporalLobe());
        assertNotNull(orchestrator.getParietalLobe());

        // Check that brains are registered
        assertEquals(3, orchestrator.getRegisteredBrainCount());
    }

    @Test
    @Order(2)
    public void testSensorySystemUnwiring() {
        orchestrator.wireSensorySystem(ollamaBridge);
        assertTrue(orchestrator.isSensorySystemWired());

        orchestrator.unwireSensorySystem();

        assertFalse(orchestrator.isSensorySystemWired());
    }

    @Test
    @Order(3)
    public void testOccipitalLobeIntegration() {
        orchestrator.wireSensorySystem(ollamaBridge);

        OccipitalLobeBrain occipital = orchestrator.getOccipitalLobe();
        assertNotNull(occipital);
        assertEquals("OccipitalLobe", occipital.getName());
        assertEquals("Vision", occipital.getType());

        // Check vision stats are accessible
        assertNotNull(occipital.getVisionStats());
    }

    @Test
    @Order(4)
    public void testTemporalLobeIntegration() {
        orchestrator.wireSensorySystem(ollamaBridge);

        TemporalLobeBrain temporal = orchestrator.getTemporalLobe();
        assertNotNull(temporal);
        assertEquals("TemporalLobe", temporal.getName());
        assertEquals("Temporal", temporal.getType());

        // Check temporal stats are accessible
        assertNotNull(temporal.getTemporalStats());
    }

    @Test
    @Order(5)
    public void testParietalLobeIntegration() {
        orchestrator.wireSensorySystem(ollamaBridge);

        ParietalLobeBrain parietal = orchestrator.getParietalLobe();
        assertNotNull(parietal);
        assertEquals("ParietalLobe", parietal.getName());
        assertEquals("Spatial", parietal.getType());

        // Check scraping stats are accessible
        assertNotNull(parietal.getScrapingStats());
    }

    @Test
    @Order(6)
    public void testDoubleWiringProtection() {
        orchestrator.wireSensorySystem(ollamaBridge);
        int initialCount = orchestrator.getRegisteredBrainCount();

        // Try to wire again - should not duplicate
        orchestrator.wireSensorySystem(ollamaBridge);

        assertEquals(initialCount, orchestrator.getRegisteredBrainCount());
    }
}
