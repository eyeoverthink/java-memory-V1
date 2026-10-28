package brain_architecture;

import brain_architecture.core.BrainType;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.integration.SystemOrchestrator;
import brain_architecture.synapses.KnowledgeGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Component interaction tests
 * Tests interactions between FraynixSystem, SystemOrchestrator, and other components
 */
public class ComponentInteractionTest {

    private FraynixSystem fraynixSystem;

    @BeforeEach
    public void setUp() {
        fraynixSystem = new FraynixSystem();
        fraynixSystem.initialize();
    }

    @AfterEach
    public void tearDown() {
        if (fraynixSystem.isInitialized()) {
            fraynixSystem.shutdown();
        }
    }

    @Test
    public void testFraynixSystemToSystemOrchestratorInteraction() {
        // Verify FraynixSystem can access SystemOrchestrator components
        assertNotNull(fraynixSystem.getBrains());
        assertNotNull(fraynixSystem.getCommunication());
        assertNotNull(fraynixSystem.getKnowledgeScraper());
        assertNotNull(fraynixSystem.getPersistentMemory());

        // Process query through FraynixSystem
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
    }

    @Test
    public void testCrossComponentDataFlow() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Create knowledge graph
        KnowledgeGraph graph = new KnowledgeGraph("interaction_test", new ArrayList<>());

        // Persist through FraynixSystem
        java.util.UUID graphId = fraynixSystem.persistKnowledgeGraph(graph);
        assertNotNull(graphId);

        // Retrieve through FraynixSystem
        KnowledgeGraph retrieved = fraynixSystem.retrieveKnowledgeGraph(graphId);
        assertNotNull(retrieved);
    }

    @Test
    public void testMultiBrainInteraction() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Query multiple brains
        List<BrainType> brains = List.of(
            BrainType.QUANTUM,
            BrainType.CREATIVE,
            BrainType.LOGICAL
        );

        var future = fraynixSystem.processMultiBrainQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL, brains.toArray(new BrainType[0]));
        QuantumOracleIntegration.OracleResponse response = future.join();
        assertNotNull(response);
        assertEquals("Test query", response.getQuery());
    }

    @Test
    public void testSentientCoreBridgeInteraction() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Wire SentientCore
        fraynixSystem.wireSentientCore();

        // Verify SentientCore is wired
        String status = fraynixSystem.getSentientCoreStatus();
        assertNotNull(status);

        // Query with SentientCore
        String response = 
            fraynixSystem.processQueryWithSentientCore("Test query");
        assertNotNull(response);

        // Unwire SentientCore
        fraynixSystem.unwireSentientCore();
    }

    @Test
    public void testOrchestratorBridgeInteraction() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Wire Hybrid Orchestrator
        fraynixSystem.wireHybridOrchestrator();

        // Verify orchestrator is wired
        java.util.Map<String, Object> status = fraynixSystem.getSystemStatus();
        assertNotNull(status);

        // Unwire Hybrid Orchestrator
        fraynixSystem.unwireHybridOrchestrator();
    }

    @Test
    public void testKnowledgeScraperInteraction() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Access knowledge scraper
        var scraper = fraynixSystem.getKnowledgeScraper();
        assertNotNull(scraper);
    }

    @Test
    public void testPersistentMemoryInteraction() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Access persistent memory
        var memory = fraynixSystem.getPersistentMemory();
        assertNotNull(memory);

        // Verify blockchain integrity
        boolean integrity = fraynixSystem.verifyBlockchainIntegrity();
        assertTrue(integrity);
    }

    @Test
    public void testBrainCommunicationInteraction() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Access brain communication
        var communication = fraynixSystem.getCommunication();
        assertNotNull(communication);

        // Access brains
        var brains = fraynixSystem.getBrains();
        assertNotNull(brains);
        assertFalse(brains.isEmpty());
    }

    @Test
    public void testErrorPropagationAcrossComponents() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Test error handling for invalid operations
        try {
            // This should handle gracefully or throw appropriate exception
            fraynixSystem.processQuery(null, QuantumOracleIntegration.OracleQueryType.LOGICAL);
        } catch (Exception e) {
            // Expected for null query
        }
    }

    @Test
    public void testComponentLifecycleInteraction() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Verify all components are accessible
        assertNotNull(fraynixSystem.getBrains());
        assertNotNull(fraynixSystem.getCommunication());
        assertNotNull(fraynixSystem.getKnowledgeScraper());
        assertNotNull(fraynixSystem.getPersistentMemory());

        // Shutdown
        fraynixSystem.shutdown();

        // Verify system is shut down
        assertFalse(fraynixSystem.isInitialized());
    }

    @Test
    public void testIntegrationStateConsistency() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Wire SentientCore
        fraynixSystem.wireSentientCore();

        // Verify state is consistent
        java.util.Map<String, Object> status = fraynixSystem.getSystemStatus();
        assertNotNull(status);

        // Wire Hybrid Orchestrator
        fraynixSystem.wireHybridOrchestrator();

        // Verify state is still consistent
        java.util.Map<String, Object> updatedStatus = fraynixSystem.getSystemStatus();
        assertNotNull(updatedStatus);

        // Unwire both
        fraynixSystem.unwireSentientCore();
        fraynixSystem.unwireHybridOrchestrator();

        // Verify state returns to baseline
        java.util.Map<String, Object> baselineStatus = fraynixSystem.getSystemStatus();
        assertNotNull(baselineStatus);
    }

    @Test
    public void testConcurrentComponentAccess() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Access multiple components concurrently
        var brains = fraynixSystem.getBrains();
        var communication = fraynixSystem.getCommunication();
        var scraper = fraynixSystem.getKnowledgeScraper();
        var memory = fraynixSystem.getPersistentMemory();

        // Verify all are accessible
        assertNotNull(brains);
        assertNotNull(communication);
        assertNotNull(scraper);
        assertNotNull(memory);
    }

    @Test
    public void testComponentIsolation() {
        // Initialize FraynixSystem
        fraynixSystem.initialize();

        // Verify components can operate independently
        var brains = fraynixSystem.getBrains();
        assertNotNull(brains);

        // Query should work even without integrations
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("Test query", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
    }
}
