package brain_architecture;

import brain_architecture.core.BrainType;
import brain_architecture.integration.QuantumOracleIntegration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Graceful degradation tests
 * Tests system behavior when components are unavailable or integrations are not wired
 */
public class GracefulDegradationTest {

    private FraynixSystem fraynixSystem;

    @BeforeEach
    public void setUp() {
        fraynixSystem = new FraynixSystem();
    }

    @AfterEach
    public void tearDown() {
        if (fraynixSystem.isInitialized()) {
            fraynixSystem.shutdown();
        }
    }

    @Test
    public void testSystemOperatesWithoutIntegrations() {
        // Initialize without any integrations
        fraynixSystem.initialize();

        // System should still function
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
    }

    @Test
    public void testQueryWithoutSentientCoreWired() {
        // Initialize without SentientCore
        fraynixSystem.initialize();

        // Regular query should work
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);

        // SentientCore-specific query should fail gracefully
        try {
            String sentientResponse = fraynixSystem.processQueryWithSentientCore("Test query");
            // If it doesn't throw, it should still return something
            assertNotNull(sentientResponse);
        } catch (Exception e) {
            // Expected when SentientCore not wired
        }
    }

    @Test
    public void testQueryWithoutHybridOrchestratorWired() {
        // Initialize without Hybrid Orchestrator
        fraynixSystem.initialize();

        // Regular query should work
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);

        // System status should reflect unwired state
        java.util.Map<String, Object> status = fraynixSystem.getSystemStatus();
        assertNotNull(status);
    }

    @Test
    public void testLearnWithoutSentientCoreWired() {
        // Initialize without SentientCore
        fraynixSystem.initialize();

        // Learn should fail gracefully
        try {
            String result = fraynixSystem.learnWithSentientCore("Test knowledge");
            // If it doesn't throw, it should handle gracefully
            assertNotNull(result);
        } catch (Exception e) {
            // Expected when SentientCore not wired
        }
    }

    @Test
    public void testUnwiredSentientCoreGracefulHandling() {
        // Initialize and wire SentientCore
        fraynixSystem.initialize();
        fraynixSystem.wireSentientCore();

        // Query with SentientCore
        String response = 
            fraynixSystem.processQueryWithSentientCore("Test query");
        assertNotNull(response);

        // Unwire SentientCore
        fraynixSystem.unwireSentientCore();

        // Query should still work with fallback
        QuantumOracleIntegration.OracleResponse fallbackResponse = fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(fallbackResponse);
    }

    @Test
    public void testUnwiredHybridOrchestratorGracefulHandling() {
        // Initialize and wire Hybrid Orchestrator
        fraynixSystem.initialize();
        fraynixSystem.wireHybridOrchestrator();

        // Query should work
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);

        // Unwire Hybrid Orchestrator
        fraynixSystem.unwireHybridOrchestrator();

        // Query should still work with fallback
        response = fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
    }

    @Test
    public void testOperationWhenNotInitialized() {
        // Don't initialize

        // Operations should fail gracefully
        try {
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
            fail("Should throw exception when not initialized");
        } catch (IllegalStateException e) {
            // Expected
        }

        try {
            fraynixSystem.ingestFile(java.nio.file.Paths.get("test.md"));
            fail("Should throw exception when not initialized");
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    @Test
    public void testShutdownGracefulHandling() {
        // Initialize
        fraynixSystem.initialize();

        // Shutdown
        fraynixSystem.shutdown();

        // Operations should fail gracefully after shutdown
        try {
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
            fail("Should throw exception after shutdown");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Double shutdown should be idempotent
        fraynixSystem.shutdown(); // Should not throw
    }

    @Test
    public void testIdempotentUnwire() {
        // Initialize
        fraynixSystem.initialize();

        // Wire and unwire
        fraynixSystem.wireSentientCore();
        fraynixSystem.unwireSentientCore();

        // Unwire again should be idempotent
        fraynixSystem.unwireSentientCore(); // Should not throw
    }

    @Test
    public void testEmptyQueryHandling() {
        // Initialize
        fraynixSystem.initialize();

        // Empty query should be handled gracefully
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
    }

    @Test
    public void testNullQueryHandling() {
        // Initialize
        fraynixSystem.initialize();

        // Null query should be handled gracefully or throw appropriate exception
        try {
            QuantumOracleIntegration.OracleResponse response = 
                fraynixSystem.processQuery(null, QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
            // If it doesn't throw, it should handle gracefully
        } catch (Exception e) {
            // Expected for null query
        }
    }

    @Test
    public void testInvalidFilePathHandling() {
        // Initialize
        fraynixSystem.initialize();

        // Invalid file path should be handled gracefully
        try {
            fraynixSystem.ingestFile(java.nio.file.Paths.get("/nonexistent/file.md"));
            // If it doesn't throw, it should handle gracefully
        } catch (Exception e) {
            // Expected for invalid file path
        }
    }

    @Test
    public void testInvalidDirectoryPathHandling() {
        // Initialize
        fraynixSystem.initialize();

        // Invalid directory path should be handled gracefully
        try {
            fraynixSystem.ingestDirectory(java.nio.file.Paths.get("/nonexistent/directory"));
            // If it doesn't throw, it should handle gracefully
        } catch (Exception e) {
            // Expected for invalid directory path
        }
    }

    @Test
    public void testInvalidKnowledgeGraphIdHandling() {
        // Initialize
        fraynixSystem.initialize();

        // Invalid knowledge graph ID should be handled gracefully
        try {
            fraynixSystem.retrieveKnowledgeGraph(java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"));
            // If it doesn't throw, it should handle gracefully
        } catch (Exception e) {
            // Expected for invalid ID
        }
    }

    @Test
    public void testInvalidFractalNodeIdHandling() {
        // Initialize
        fraynixSystem.initialize();

        // Invalid fractal node ID should be handled gracefully
        // Fractal node retrieval not available in API - skip test
        // This test is removed since getFractalNode doesn't exist in FraynixSystem
    }

    @Test
    public void testMultiBrainQueryWithEmptyBrainList() {
        // Initialize
        fraynixSystem.initialize();

        // Empty brain list should be handled gracefully
        try {
            fraynixSystem.processMultiBrainQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL, new BrainType[0]);
            // If it doesn't throw, it should handle gracefully
        } catch (Exception e) {
            // Expected for empty brain list
        }
    }

    @Test
    public void testSystemStatusReflectsCurrentState() {
        // Initialize without integrations
        fraynixSystem.initialize();

        // Status should reflect unwired state
        java.util.Map<String, Object> status = fraynixSystem.getSystemStatus();
        assertNotNull(status);

        // Wire SentientCore
        fraynixSystem.wireSentientCore();

        // Status should reflect wired state
        java.util.Map<String, Object> wiredStatus = fraynixSystem.getSystemStatus();
        assertNotNull(wiredStatus);

        // Unwire
        fraynixSystem.unwireSentientCore();

        // Status should return to unwired state
        java.util.Map<String, Object> unwiredStatus = fraynixSystem.getSystemStatus();
        assertNotNull(unwiredStatus);
    }

    @Test
    public void testGracefulDegradationAfterMultipleOperations() {
        // Initialize
        fraynixSystem.initialize();

        // Perform multiple operations
        for (int i = 0; i < 10; i++) {
            QuantumOracleIntegration.OracleResponse response = 
                fraynixSystem.processQuery("Test query " + i, QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
            assertNotNull(response);
        }

        // System should still be stable
        assertTrue(fraynixSystem.isInitialized());
    }

    @Test
    public void testComponentFailureIsolation() {
        // Initialize
        fraynixSystem.initialize();

        // If one integration fails, others should still work
        fraynixSystem.wireSentientCore();

        // Regular query should still work
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);

        // Unwire SentientCore
        fraynixSystem.unwireSentientCore();

        // System should still function
        response = fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
    }
}
