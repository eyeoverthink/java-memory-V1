package brain_architecture;

import brain_architecture.core.BrainType;
import brain_architecture.integration.SentientCoreBridge;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.orchestration.OrchestratorBridge;
import brain_architecture.synapses.KnowledgeGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for FraynixSystem - the unified entry point for the entire system
 */
public class FraynixSystemTest {
    
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
    public void testInitialization() {
        assertFalse(fraynixSystem.isInitialized());
        
        fraynixSystem.initialize();
        
        assertTrue(fraynixSystem.isInitialized());
    }
    
    @Test
    public void testIdempotentInitialization() {
        fraynixSystem.initialize();
        assertTrue(fraynixSystem.isInitialized());
        
        // Second initialize should be idempotent
        fraynixSystem.initialize();
        assertTrue(fraynixSystem.isInitialized());
    }
    
    @Test
    public void testShutdown() {
        fraynixSystem.initialize();
        assertTrue(fraynixSystem.isInitialized());
        
        fraynixSystem.shutdown();
        assertFalse(fraynixSystem.isInitialized());
    }
    
    @Test
    public void testShutdownWhenNotInitialized() {
        // Should not throw exception
        assertDoesNotThrow(() -> fraynixSystem.shutdown());
    }
    
    @Test
    public void testGetSystemStatus() {
        fraynixSystem.initialize();
        
        Map<String, Object> status = fraynixSystem.getSystemStatus();
        
        assertNotNull(status);
        assertTrue(status.containsKey("fraynixSystem"));
        assertTrue(status.containsKey("quantumOracle"));
        assertTrue(status.containsKey("legacyIntegrations"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> fraynixStatus = (Map<String, Object>) status.get("fraynixSystem");
        assertTrue((Boolean) fraynixStatus.get("initialized"));
    }
    
    @Test
    public void testGetBrains() {
        fraynixSystem.initialize();
        
        Map<BrainType, brain_architecture.core.QuantumBrain> brains = fraynixSystem.getBrains();
        
        assertNotNull(brains);
        assertFalse(brains.isEmpty());
        assertEquals(12, brains.size()); // 12 specialized brains
    }
    
    @Test
    public void testGetCommunication() {
        fraynixSystem.initialize();
        
        assertNotNull(fraynixSystem.getCommunication());
    }
    
    @Test
    public void testGetKnowledgeScraper() {
        fraynixSystem.initialize();
        
        assertNotNull(fraynixSystem.getKnowledgeScraper());
    }
    
    @Test
    public void testGetPersistentMemory() {
        fraynixSystem.initialize();
        
        assertNotNull(fraynixSystem.getPersistentMemory());
    }
    
    @Test
    public void testProcessQueryWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> {
            fraynixSystem.processQuery("test", QuantumOracleIntegration.OracleQueryType.LOGICAL);
        });
    }
    
    @Test
    public void testProcessQueryWhenInitialized() throws ExecutionException, InterruptedException {
        fraynixSystem.initialize();
        
        CompletableFuture<QuantumOracleIntegration.OracleResponse> future = 
            fraynixSystem.processQuery("test query", QuantumOracleIntegration.OracleQueryType.LOGICAL);
        
        assertNotNull(future);
        QuantumOracleIntegration.OracleResponse response = future.get();
        assertNotNull(response);
    }
    
    @Test
    public void testProcessMultiBrainQueryWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> {
            fraynixSystem.processMultiBrainQuery(
                "test", 
                QuantumOracleIntegration.OracleQueryType.LOGICAL, 
                new BrainType[]{BrainType.PHYSICAL}
            );
        });
    }
    
    @Test
    public void testProcessMultiBrainQueryWhenInitialized() throws ExecutionException, InterruptedException {
        fraynixSystem.initialize();
        
        CompletableFuture<QuantumOracleIntegration.OracleResponse> future = 
            fraynixSystem.processMultiBrainQuery(
                "test query", 
                QuantumOracleIntegration.OracleQueryType.LOGICAL,
                new BrainType[]{BrainType.PHYSICAL, BrainType.QUANTUM}
            );
        
        assertNotNull(future);
        QuantumOracleIntegration.OracleResponse response = future.get();
        assertNotNull(response);
    }
    
    @Test
    public void testIngestFileWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> {
            fraynixSystem.ingestFile(Paths.get("test.txt"));
        });
    }
    
    @Test
    public void testWireSentientCoreWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> {
            fraynixSystem.wireSentientCore();
        });
    }
    
    @Test
    public void testWireSentientCoreWhenInitialized() {
        fraynixSystem.initialize();
        
        assertDoesNotThrow(() -> fraynixSystem.wireSentientCore());
        
        SentientCoreBridge bridge = fraynixSystem.getSentientCoreBridge();
        assertNotNull(bridge);
    }
    
    @Test
    public void testUnwireSentientCore() {
        fraynixSystem.initialize();
        fraynixSystem.wireSentientCore();
        
        assertNotNull(fraynixSystem.getSentientCoreBridge());
        
        fraynixSystem.unwireSentientCore();
        
        assertNull(fraynixSystem.getSentientCoreBridge());
    }
    
    @Test
    public void testProcessQueryWithSentientCoreWhenNotWired() {
        fraynixSystem.initialize();
        
        String response = fraynixSystem.processQueryWithSentientCore("test");
        
        assertEquals("SentientCore: NOT WIRED", response);
    }
    
    @Test
    public void testProcessQueryWithSentientCoreWhenWired() {
        fraynixSystem.initialize();
        fraynixSystem.wireSentientCore();
        
        String response = fraynixSystem.processQueryWithSentientCore("test");
        
        assertNotNull(response);
        assertNotEquals("SentientCore: NOT WIRED", response);
    }
    
    @Test
    public void testLearnWithSentientCoreWhenNotWired() {
        fraynixSystem.initialize();
        
        String response = fraynixSystem.learnWithSentientCore("test statement");
        
        assertEquals("SentientCore: NOT WIRED", response);
    }
    
    @Test
    public void testLearnWithSentientCoreWhenWired() {
        fraynixSystem.initialize();
        fraynixSystem.wireSentientCore();
        
        String response = fraynixSystem.learnWithSentientCore("test statement");
        
        assertNotNull(response);
        assertNotEquals("SentientCore: NOT WIRED", response);
    }
    
    @Test
    public void testGetSentientCoreStatusWhenNotWired() {
        fraynixSystem.initialize();
        
        String status = fraynixSystem.getSentientCoreStatus();
        
        assertEquals("SentientCore: NOT WIRED", status);
    }
    
    @Test
    public void testGetSentientCoreStatusWhenWired() {
        fraynixSystem.initialize();
        fraynixSystem.wireSentientCore();
        
        String status = fraynixSystem.getSentientCoreStatus();
        
        assertNotNull(status);
        assertNotEquals("SentientCore: NOT WIRED", status);
    }
    
    @Test
    public void testWireHybridOrchestratorWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> {
            fraynixSystem.wireHybridOrchestrator();
        });
    }
    
    @Test
    public void testWireHybridOrchestratorWhenInitialized() {
        fraynixSystem.initialize();
        
        assertDoesNotThrow(() -> fraynixSystem.wireHybridOrchestrator());
        
        OrchestratorBridge bridge = fraynixSystem.getOrchestratorBridge();
        assertNotNull(bridge);
    }
    
    @Test
    public void testUnwireHybridOrchestrator() {
        fraynixSystem.initialize();
        fraynixSystem.wireHybridOrchestrator();
        
        assertNotNull(fraynixSystem.getOrchestratorBridge());
        
        fraynixSystem.unwireHybridOrchestrator();
        
        assertNull(fraynixSystem.getOrchestratorBridge());
    }
    
    @Test
    public void testSystemStatusWithIntegrationsWired() {
        fraynixSystem.initialize();
        fraynixSystem.wireSentientCore();
        fraynixSystem.wireHybridOrchestrator();
        
        Map<String, Object> status = fraynixSystem.getSystemStatus();
        
        assertNotNull(status);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> integrationStatus = (Map<String, Object>) status.get("legacyIntegrations");
        assertTrue((Boolean) integrationStatus.get("sentientCore"));
        assertTrue((Boolean) integrationStatus.get("hybridOrchestrator"));
    }
    
    @Test
    public void testPersistKnowledgeGraphWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> {
            fraynixSystem.persistKnowledgeGraph(new KnowledgeGraph("test", new java.util.ArrayList<>()));
        });
    }
    
    @Test
    public void testPersistAndRetrieveKnowledgeGraph() {
        fraynixSystem.initialize();
        
        KnowledgeGraph graph = new KnowledgeGraph("test", new java.util.ArrayList<>());
        UUID graphId = fraynixSystem.persistKnowledgeGraph(graph);
        
        assertNotNull(graphId);
        
        KnowledgeGraph retrieved = fraynixSystem.retrieveKnowledgeGraph(graphId);
        assertNotNull(retrieved);
    }
    
    @Test
    public void testVerifyBlockchainIntegrityWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> {
            fraynixSystem.verifyBlockchainIntegrity();
        });
    }
    
    @Test
    public void testVerifyBlockchainIntegrityWhenInitialized() {
        fraynixSystem.initialize();
        
        boolean valid = fraynixSystem.verifyBlockchainIntegrity();
        
        assertTrue(valid);
    }
    
    @Test
    public void testPrintSystemStatus() {
        fraynixSystem.initialize();
        
        // Should not throw exception
        assertDoesNotThrow(() -> fraynixSystem.printSystemStatus());
    }
    
    @Test
    public void testCreateFractalNodeWhenNotInitialized() {
        assertThrows(IllegalStateException.class, () -> {
            fraynixSystem.createFractalNode(new KnowledgeGraph("test", new java.util.ArrayList<>()));
        });
    }
    
    @Test
    public void testCreateFractalNodeWhenInitialized() {
        fraynixSystem.initialize();
        
        KnowledgeGraph subgraph = new KnowledgeGraph("test", new java.util.ArrayList<>());
        brain_architecture.persistence.FractalNode node = fraynixSystem.createFractalNode(subgraph);
        
        assertNotNull(node);
    }
}
