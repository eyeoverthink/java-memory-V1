package fraymus.brain.unified;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import fraymus.core.OllamaBridge;

/**
 * Test suite for Nervous System integration with UnifiedBrainOrchestrator
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnifiedBrainOrchestratorNervousSystemTest {
    
    private static UnifiedBrainOrchestrator orchestrator;
    private static OllamaBridge ollamaBridge;
    
    @BeforeAll
    static void setUp() {
        orchestrator = new UnifiedBrainOrchestrator();
        ollamaBridge = new OllamaBridge("qwen2.5:0.5b");
    }
    
    @AfterAll
    static void tearDown() {
        if (orchestrator != null && orchestrator.isNervousSystemWired()) {
            orchestrator.unwireNervousSystem();
        }
    }
    
    @Test
    @Order(1)
    public void testNervousSystemWiring() {
        assertFalse(orchestrator.isNervousSystemWired());
        
        orchestrator.wireNervousSystem(ollamaBridge);
        
        assertTrue(orchestrator.isNervousSystemWired());
        assertNotNull(orchestrator.getSemanticRouter());
        assertNotNull(orchestrator.getPremotorCortex());
        assertNotNull(orchestrator.getEntericNervousSystem());
        
        // Check that brains are registered
        assertEquals(2, orchestrator.getRegisteredBrainCount());
    }
    
    @Test
    @Order(2)
    public void testNervousSystemUnwiring() {
        orchestrator.wireNervousSystem(ollamaBridge);
        assertTrue(orchestrator.isNervousSystemWired());
        
        orchestrator.unwireNervousSystem();
        
        assertFalse(orchestrator.isNervousSystemWired());
    }
    
    @Test
    @Order(3)
    public void testSemanticRouterIntegration() {
        orchestrator.wireNervousSystem(ollamaBridge);
        
        SemanticRouterBrain router = orchestrator.getSemanticRouter();
        assertNotNull(router);
        assertEquals("SemanticRouter", router.getName());
        assertEquals("SpinalCord", router.getType());
        
        // Check routing stats are accessible
        assertNotNull(router.getRoutingStats());
    }
    
    @Test
    @Order(4)
    public void testPremotorCortexIntegration() {
        orchestrator.wireNervousSystem(ollamaBridge);
        
        PremotorCortexBrain cortex = orchestrator.getPremotorCortex();
        assertNotNull(cortex);
        assertEquals("PremotorCortex", cortex.getName());
        assertEquals("PremotorCortex", cortex.getType());
        
        // Check conversion stats are accessible
        assertNotNull(cortex.getConversionStats());
    }
    
    @Test
    @Order(5)
    public void testEntericNervousSystemIntegration() {
        orchestrator.wireNervousSystem(ollamaBridge);
        
        EntericNervousSystem enteric = orchestrator.getEntericNervousSystem();
        assertNotNull(enteric);
        
        // Check stats are accessible
        assertNotNull(enteric.getStats());
        assertTrue(enteric.getStats().get("running").equals(true));
    }
    
    @Test
    @Order(6)
    public void testDoubleWiringProtection() {
        orchestrator.wireNervousSystem(ollamaBridge);
        int initialCount = orchestrator.getRegisteredBrainCount();
        
        // Try to wire again - should not duplicate
        orchestrator.wireNervousSystem(ollamaBridge);
        
        assertEquals(initialCount, orchestrator.getRegisteredBrainCount());
    }
}
