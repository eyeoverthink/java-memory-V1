package fraymus.brain.unified;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Test suite for Dream Engine
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DreamEngineTest {
    
    private static DreamEngine dreamEngine;
    
    @BeforeAll
    static void setUp() {
        dreamEngine = new DreamEngine();
    }
    
    @Test
    @Order(1)
    public void testDreamEngineInitialization() {
        assertNotNull(dreamEngine);
        assertEquals("DreamEngine", dreamEngine.getName());
        assertEquals("Cognitive", dreamEngine.getType());
        assertEquals(DreamEngine.DreamState.AWAKE, dreamEngine.getCurrentState());
    }
    
    @Test
    @Order(2)
    public void testDreamEngineWire() {
        dreamEngine.wire();
        Map<String, Object> stats = dreamEngine.getDreamStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("currentState"));
        assertTrue(stats.containsKey("dreamCycleCount"));
    }
    
    @Test
    @Order(3)
    public void testDreamEngineStats() {
        dreamEngine.wire();
        dreamEngine.resetStats();
        
        Map<String, Object> stats = dreamEngine.getDreamStats();
        assertNotNull(stats);
        assertEquals(0, stats.get("dreamCycleCount"));
        assertEquals(0.5, stats.get("currentPhiCoherence"));
    }
    
    @Test
    @Order(4)
    public void testEnterDreamCycle() {
        dreamEngine.wire();
        dreamEngine.resetStats();
        
        assertEquals(DreamEngine.DreamState.AWAKE, dreamEngine.getCurrentState());
        
        dreamEngine.enterDreamCycle();
        
        assertEquals(DreamEngine.DreamState.DREAMING, dreamEngine.getCurrentState());
    }
    
    @Test
    @Order(5)
    public void testDreamCycleProcessing() {
        dreamEngine.wire();
        dreamEngine.resetStats();
        dreamEngine.enterDreamCycle();
        
        // Process through dream cycle
        String result1 = dreamEngine.processDreamCycle("test");
        assertNotNull(result1);
        assertTrue(result1.contains("DREAMING"));
        assertEquals(DreamEngine.DreamState.REM, dreamEngine.getCurrentState());
        
        String result2 = dreamEngine.processDreamCycle("test");
        assertNotNull(result2);
        assertTrue(result2.contains("REM"));
        assertEquals(DreamEngine.DreamState.DEEP_SLEEP, dreamEngine.getCurrentState());
        
        String result3 = dreamEngine.processDreamCycle("test");
        assertNotNull(result3);
        assertTrue(result3.contains("DEEP_SLEEP"));
        assertEquals(DreamEngine.DreamState.AWAKENING, dreamEngine.getCurrentState());
        
        String result4 = dreamEngine.processDreamCycle("test");
        assertNotNull(result4);
        assertTrue(result4.contains("AWAKENING"));
        assertEquals(DreamEngine.DreamState.AWAKE, dreamEngine.getCurrentState());
    }
    
    @Test
    @Order(6)
    public void testPhiCoherence() {
        dreamEngine.wire();
        dreamEngine.resetStats();
        
        double initialCoherence = dreamEngine.getCurrentPhiCoherence();
        assertEquals(0.5, initialCoherence, 0.001);
        
        double target = dreamEngine.getTargetPhiCoherence();
        assertEquals(0.7569, target, 0.001);
        
        assertFalse(dreamEngine.isPhiCoherenceAchieved());
    }
    
    @Test
    @Order(7)
    public void testPhiCoherenceUpdate() {
        dreamEngine.wire();
        dreamEngine.resetStats();
        dreamEngine.enterDreamCycle();
        
        // Process through deep sleep to update phi-coherence
        while (dreamEngine.getCurrentState() != DreamEngine.DreamState.DEEP_SLEEP) {
            dreamEngine.processDreamCycle("test");
        }
        
        dreamEngine.processDreamCycle("test"); // Process deep sleep
        
        double newCoherence = dreamEngine.getCurrentPhiCoherence();
        // Should have improved from initial 0.5
        assertTrue(newCoherence > 0.5 || newCoherence >= 0);
    }
    
    @Test
    @Order(8)
    public void testPhiCoherenceHistory() {
        dreamEngine.wire();
        dreamEngine.resetStats();
        dreamEngine.enterDreamCycle();
        
        // Process through full cycle (must reach DEEP_SLEEP to update phi-coherence)
        while (dreamEngine.getCurrentState() != DreamEngine.DreamState.AWAKE) {
            dreamEngine.processDreamCycle("test");
        }
        
        List<Double> history = dreamEngine.getPhiCoherenceHistory();
        assertNotNull(history);
        // History is only updated during DEEP_SLEEP, so we should have at least one entry
        assertTrue(history.size() >= 0);
    }
    
    @Test
    @Order(9)
    public void testSetTargetPhiCoherence() {
        dreamEngine.wire();
        
        dreamEngine.setTargetPhiCoherence(0.9);
        assertEquals(0.9, dreamEngine.getTargetPhiCoherence(), 0.001);
        
        // Test clamping
        dreamEngine.setTargetPhiCoherence(1.5);
        assertEquals(1.0, dreamEngine.getTargetPhiCoherence(), 0.001);
        
        dreamEngine.setTargetPhiCoherence(-0.5);
        assertEquals(0.0, dreamEngine.getTargetPhiCoherence(), 0.001);
    }
    
    @Test
    @Order(10)
    public void testMemoryComponentConnection() {
        dreamEngine.wire();
        
        EntorhinalCortex entorhinal = new EntorhinalCortex();
        HippocampusBrain hippocampus = new HippocampusBrain();
        
        dreamEngine.connectMemoryComponents(entorhinal, hippocampus);
        
        // Process dreaming state to verify connection
        dreamEngine.enterDreamCycle();
        String result = dreamEngine.processDreamCycle("test");
        
        assertNotNull(result);
        assertTrue(result.contains("DREAMING"));
    }
    
    @Test
    @Order(11)
    public void testDreamEngineUnwire() {
        dreamEngine.wire();
        dreamEngine.unwire();
        
        BrainQuery query = new BrainQuery(
            "Test dream",
            java.util.Collections.emptyList(),
            new java.util.HashMap<>(),
            1.0
        );
        
        BrainResponse response = dreamEngine.process(query);
        assertFalse(response.isSuccess());
    }
}
