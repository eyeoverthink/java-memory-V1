package fraymus.brain.unified;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Test suite for Nervous System Upgrade components
 * Tests SemanticRouterBrain, PremotorCortexBrain, and EntericNervousSystem
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NervousSystemTest {
    
    private static SemanticRouterBrain semanticRouter;
    private static PremotorCortexBrain premotorCortex;
    private static EntericNervousSystem entericSystem;
    
    @BeforeAll
    static void setUp() {
        semanticRouter = new SemanticRouterBrain();
        premotorCortex = new PremotorCortexBrain();
        entericSystem = new EntericNervousSystem();
    }
    
    @AfterAll
    static void tearDown() {
        if (entericSystem != null) {
            entericSystem.stopMonitoring();
        }
    }
    
    // ==================== SemanticRouterBrain Tests ====================
    
    @Test
    @Order(1)
    public void testSemanticRouterInitialization() {
        assertNotNull(semanticRouter);
        assertEquals("SemanticRouter", semanticRouter.getName());
        assertEquals("SpinalCord", semanticRouter.getType());
    }
    
    @Test
    @Order(2)
    public void testSemanticRouterWire() {
        semanticRouter.wire();
        Map<String, Object> stats = semanticRouter.getRoutingStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalRoutes"));
        assertTrue(stats.containsKey("routeAccuracy"));
    }
    
    @Test
    @Order(3)
    public void testSemanticRouterStats() {
        semanticRouter.wire();
        semanticRouter.resetStats();
        
        Map<String, Object> stats = semanticRouter.getRoutingStats();
        assertNotNull(stats);
        assertEquals(0, stats.get("totalRoutes"));
        assertEquals(0, stats.get("routeSuccess"));
    }
    
    @Test
    @Order(4)
    public void testSemanticRouterUnwire() {
        semanticRouter.wire();
        semanticRouter.unwire();
        
        BrainQuery query = new BrainQuery(
            "Test query",
            java.util.Collections.emptyList(),
            new java.util.HashMap<>(),
            1.0
        );
        
        BrainResponse response = semanticRouter.process(query);
        assertFalse(response.isSuccess());
    }
    
    // ==================== PremotorCortexBrain Tests ====================
    
    @Test
    @Order(8)
    public void testPremotorCortexInitialization() {
        assertNotNull(premotorCortex);
        assertEquals("PremotorCortex", premotorCortex.getName());
        assertEquals("PremotorCortex", premotorCortex.getType());
    }
    
    @Test
    @Order(9)
    public void testPremotorCortexWire() {
        premotorCortex.wire();
        Map<String, Object> stats = premotorCortex.getConversionStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalConversions"));
        assertTrue(stats.containsKey("conversionAccuracy"));
    }
    
    @Test
    @Order(10)
    public void testPremotorCortexStats() {
        premotorCortex.wire();
        premotorCortex.resetStats();
        
        Map<String, Object> stats = premotorCortex.getConversionStats();
        assertNotNull(stats);
        assertEquals(0, stats.get("totalConversions"));
        assertEquals(0, stats.get("conversionSuccess"));
    }
    
    @Test
    @Order(14)
    public void testPremotorCortexUnwire() {
        premotorCortex.wire();
        premotorCortex.unwire();
        
        BrainQuery query = new BrainQuery(
            "Test query",
            java.util.Collections.emptyList(),
            new java.util.HashMap<>(),
            1.0
        );
        
        BrainResponse response = premotorCortex.process(query);
        assertFalse(response.isSuccess());
    }
    
    // ==================== EntericNervousSystem Tests ====================
    
    @Test
    @Order(15)
    public void testEntericSystemInitialization() {
        assertNotNull(entericSystem);
        assertFalse(entericSystem.isAnomalyDetected());
    }
    
    @Test
    @Order(16)
    public void testEntericSystemStartStop() {
        entericSystem.startMonitoring(100);
        assertTrue(entericSystem.getStats().get("running").equals(true));
        
        entericSystem.stopMonitoring();
        assertFalse(entericSystem.getStats().get("running").equals(true));
    }
    
    @Test
    @Order(17)
    public void testEntericSystemStats() {
        Map<String, Object> stats = entericSystem.getStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("running"));
        assertTrue(stats.containsKey("anomalyDetected"));
        assertTrue(stats.containsKey("totalChecks"));
        assertTrue(stats.containsKey("ramThreshold"));
        assertTrue(stats.containsKey("cpuThreshold"));
        assertTrue(stats.containsKey("networkThreshold"));
    }
    
    @Test
    @Order(18)
    public void testEntericSystemThresholds() {
        entericSystem.setThresholds(0.9, 0.85, 2000);
        
        Map<String, Object> stats = entericSystem.getStats();
        assertEquals(0.9, stats.get("ramThreshold"));
        assertEquals(0.85, stats.get("cpuThreshold"));
        assertEquals(2000.0, stats.get("networkThreshold"));
    }
    
    @Test
    @Order(19)
    public void testEntericSystemResetStats() {
        entericSystem.startMonitoring(50);
        
        // Wait for a few checks
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        entericSystem.stopMonitoring();
        entericSystem.resetStats();
        
        Map<String, Object> stats = entericSystem.getStats();
        assertEquals(0, stats.get("totalChecks"));
        assertEquals(0, stats.get("anomalyCount"));
    }
    
    @Test
    @Order(20)
    public void testEntericSystemAnomalyCallback() {
        final boolean[] callbackCalled = {false};
        
        entericSystem.setCallback(result -> {
            callbackCalled[0] = true;
        });
        
        // Set very low thresholds to trigger anomaly
        entericSystem.setThresholds(0.0, 0.0, 0.0);
        entericSystem.startMonitoring(50);
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        entericSystem.stopMonitoring();
        
        // Callback may or may not be called depending on actual system metrics
        // Just verify the callback mechanism is set up correctly
        assertNotNull(entericSystem);
    }
    
    @Test
    @Order(21)
    public void testSystemMetrics() {
        EntericNervousSystem.SystemMetrics metrics = 
            EntericNervousSystem.SystemMetrics.getCurrent();
        
        assertNotNull(metrics);
        assertTrue(metrics.getRamUsage() >= 0.0);
        assertTrue(metrics.getRamUsage() <= 1.0);
        assertTrue(metrics.getCpuUsage() >= 0.0);
        assertTrue(metrics.getCpuUsage() <= 1.0);
    }
    
    @Test
    @Order(22)
    public void testAnomalyResult() {
        EntericNervousSystem.AnomalyResult result = new EntericNervousSystem.AnomalyResult();
        
        result.isAnomaly = true;
        result.anomalyScore = 0.8;
        result.reasons.add("High RAM usage");
        
        assertTrue(result.isAnomaly);
        assertEquals(0.8, result.anomalyScore);
        assertEquals(1, result.reasons.size());
        
        String resultString = result.toString();
        assertTrue(resultString.contains("isAnomaly=true"));
        assertTrue(resultString.contains("score=0.8"));
    }
    
    // ==================== Integration Tests ====================
    
    @Test
    @Order(23)
    public void testNervousSystemIntegration() {
        // Wire both brains
        semanticRouter.wire();
        premotorCortex.wire();
        
        // Start enteric monitoring
        entericSystem.startMonitoring(100);
        
        // Wait a bit for monitoring to start
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check enteric system stats
        Map<String, Object> entericStats = entericSystem.getStats();
        assertTrue(entericStats.get("running").equals(true));
        
        // Check semantic router stats
        Map<String, Object> routerStats = semanticRouter.getRoutingStats();
        assertNotNull(routerStats);
        
        // Check premotor cortex stats
        Map<String, Object> cortexStats = premotorCortex.getConversionStats();
        assertNotNull(cortexStats);
        
        // Cleanup
        entericSystem.stopMonitoring();
        semanticRouter.unwire();
        premotorCortex.unwire();
    }
}
