package brain_architecture.integration;

import brain_architecture.core.BrainType;
import brain_architecture.core.QuantumBrain;
import brain_architecture.communication.BrainCommunication;
import brain_architecture.persistence.PersistentMemory;
import brain_architecture.synapses.KnowledgeScraper;
import org.junit.jupiter.api.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the System Orchestrator
 * 
 * Tests:
 * 1. Basic initialization
 * 2. Delegation to QuantumOracleIntegration
 * 3. Wire/unwire pattern for integrations
 * 4. System status reporting
 * 5. Lifecycle management
 */
class SystemOrchestratorTest {
    
    private SystemOrchestrator orchestrator;
    
    @BeforeEach
    void setUp() {
        orchestrator = new SystemOrchestrator();
    }
    
    @AfterEach
    void tearDown() {
        if (orchestrator != null) {
            orchestrator.shutdown();
        }
    }
    
    @Test
    void testInitialization() {
        assertNotNull(orchestrator);
        assertFalse(orchestrator.getBrains().isEmpty());
        assertNotNull(orchestrator.getCommunication());
        assertNotNull(orchestrator.getKnowledgeScraper());
        assertNotNull(orchestrator.getPersistentMemory());
    }
    
    @Test
    void testWireSentientCore() {
        orchestrator.wireSentientCore();
        
        Map<String, Object> status = orchestrator.getSystemStatus();
        @SuppressWarnings("unchecked")
        Map<String, Object> integrations = (Map<String, Object>) status.get("legacyIntegrations");
        assertTrue((Boolean) integrations.get("sentientCore"));
        assertTrue(integrations.containsKey("sentientCoreStatus"));
        assertFalse((Boolean) integrations.get("hybridOrchestrator"));
    }
    
    @Test
    void testUnwireSentientCore() {
        orchestrator.wireSentientCore();
        assertTrue((Boolean) ((Map<String, Object>) orchestrator.getSystemStatus()
            .get("legacyIntegrations")).get("sentientCore"));
        
        orchestrator.unwireSentientCore();
        assertFalse((Boolean) ((Map<String, Object>) orchestrator.getSystemStatus()
            .get("legacyIntegrations")).get("sentientCore"));
    }
    
    @Test
    void testWireHybridOrchestrator() {
        orchestrator.wireHybridOrchestrator();
        
        Map<String, Object> status = orchestrator.getSystemStatus();
        @SuppressWarnings("unchecked")
        Map<String, Object> integrations = (Map<String, Object>) status.get("legacyIntegrations");
        assertFalse((Boolean) integrations.get("sentientCore"));
        assertTrue((Boolean) integrations.get("hybridOrchestrator"));
    }
    
    @Test
    void testUnwireHybridOrchestrator() {
        orchestrator.wireHybridOrchestrator();
        assertTrue((Boolean) ((Map<String, Object>) orchestrator.getSystemStatus()
            .get("legacyIntegrations")).get("hybridOrchestrator"));
        
        orchestrator.unwireHybridOrchestrator();
        assertFalse((Boolean) ((Map<String, Object>) orchestrator.getSystemStatus()
            .get("legacyIntegrations")).get("hybridOrchestrator"));
    }
    
    @Test
    void testGetBrains() {
        Map<BrainType, QuantumBrain> brains = orchestrator.getBrains();
        assertNotNull(brains);
        assertFalse(brains.isEmpty());
        // Should have 12 brains
        assertEquals(12, brains.size());
    }
    
    @Test
    void testGetCommunication() {
        BrainCommunication communication = orchestrator.getCommunication();
        assertNotNull(communication);
    }
    
    @Test
    void testGetKnowledgeScraper() {
        KnowledgeScraper scraper = orchestrator.getKnowledgeScraper();
        assertNotNull(scraper);
    }
    
    @Test
    void testGetPersistentMemory() {
        PersistentMemory persistentMemory = orchestrator.getPersistentMemory();
        assertNotNull(persistentMemory);
    }
    
    @Test
    void testSystemStatus() {
        Map<String, Object> status = orchestrator.getSystemStatus();
        assertNotNull(status);
        assertTrue(status.containsKey("quantumOracle"));
        assertTrue(status.containsKey("legacyIntegrations"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> quantumStatus = (Map<String, Object>) status.get("quantumOracle");
        assertTrue(quantumStatus.containsKey("initialized"));
        assertTrue(quantumStatus.containsKey("brains"));
        assertTrue(quantumStatus.containsKey("statistics"));
    }
    
    @Test
    void testProcessQuery() throws Exception {
        var future = orchestrator.processQuery(
            "test query",
            QuantumOracleIntegration.OracleQueryType.AUTO
        );
        
        assertNotNull(future);
        QuantumOracleIntegration.OracleResponse response = future.get();
        assertNotNull(response);
        assertNotNull(response.getContent());
    }
    
    @Test
    void testVerifyBlockchainIntegrity() {
        boolean valid = orchestrator.verifyBlockchainIntegrity();
        // Should be valid (only has genesis block initially)
        assertTrue(valid);
    }
    
    @Test
    void testShutdown() {
        orchestrator.wireSentientCore();
        orchestrator.wireHybridOrchestrator();
        
        assertDoesNotThrow(() -> orchestrator.shutdown());
        
        // After shutdown, integrations should be unwired
        Map<String, Object> status = orchestrator.getSystemStatus();
        @SuppressWarnings("unchecked")
        Map<String, Object> integrations = (Map<String, Object>) status.get("legacyIntegrations");
        assertFalse((Boolean) integrations.get("sentientCore"));
        assertFalse((Boolean) integrations.get("hybridOrchestrator"));
    }
    
    @Test
    void testProcessQueryWithSentientCoreWhenWired() {
        orchestrator.wireSentientCore();
        String response = orchestrator.processQueryWithSentientCore("What is 2+2?");
        assertNotNull(response);
        assertFalse(response.equals("SentientCore: NOT WIRED"));
    }
    
    @Test
    void testProcessQueryWithSentientCoreWhenNotWired() {
        String response = orchestrator.processQueryWithSentientCore("What is 2+2?");
        assertEquals("SentientCore: NOT WIRED", response);
    }
    
    @Test
    void testLearnWithSentientCoreWhenWired() {
        orchestrator.wireSentientCore();
        String response = orchestrator.learnWithSentientCore("The sky is blue.");
        assertNotNull(response);
        assertFalse(response.equals("SentientCore: NOT WIRED"));
    }
    
    @Test
    void testLearnWithSentientCoreWhenNotWired() {
        String response = orchestrator.learnWithSentientCore("The sky is blue.");
        assertEquals("SentientCore: NOT WIRED", response);
    }
    
    @Test
    void testGetSentientCoreStatusWhenWired() {
        orchestrator.wireSentientCore();
        String status = orchestrator.getSentientCoreStatus();
        assertNotNull(status);
        assertFalse(status.equals("SentientCore: NOT WIRED"));
    }
    
    @Test
    void testGetSentientCoreStatusWhenNotWired() {
        String status = orchestrator.getSentientCoreStatus();
        assertEquals("SentientCore: NOT WIRED", status);
    }
    
    @Test
    void testGetSentientCoreBridgeWhenWired() {
        orchestrator.wireSentientCore();
        SentientCoreBridge bridge = orchestrator.getSentientCoreBridge();
        assertNotNull(bridge);
        assertTrue(bridge.isWired());
    }
    
    @Test
    void testGetSentientCoreBridgeWhenNotWired() {
        SentientCoreBridge bridge = orchestrator.getSentientCoreBridge();
        assertNull(bridge);
    }
    
    // ========== NEW: Fraymus Integration Tests ==========
    
    @Test
    void testWireFraymusComponents() {
        brain_architecture.synapses.HyperdimensionalMapper mapper = 
            new brain_architecture.synapses.HyperdimensionalMapper();
        brain_architecture.synapses.HDCSimilarityEngine engine = 
            new brain_architecture.synapses.HDCSimilarityEngine();
        
        orchestrator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), mapper, engine);
        
        Map<String, Object> status = orchestrator.getSystemStatus();
        @SuppressWarnings("unchecked")
        Map<String, Object> fraymusIntegrations = (Map<String, Object>) status.get("fraymusIntegrations");
        assertTrue((Boolean) fraymusIntegrations.get("fraymusComponentsWired"));
    }
    
    @Test
    void testUnwireFraymusComponents() {
        brain_architecture.synapses.HyperdimensionalMapper mapper = 
            new brain_architecture.synapses.HyperdimensionalMapper();
        brain_architecture.synapses.HDCSimilarityEngine engine = 
            new brain_architecture.synapses.HDCSimilarityEngine();
        
        orchestrator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), mapper, engine);
        
        assertTrue((Boolean) ((Map<String, Object>) orchestrator.getSystemStatus()
            .get("fraymusIntegrations")).get("fraymusComponentsWired"));
        
        orchestrator.unwireFraymusComponents();
        assertFalse((Boolean) ((Map<String, Object>) orchestrator.getSystemStatus()
            .get("fraymusIntegrations")).get("fraymusComponentsWired"));
    }
    
    @Test
    void testWireRealSentientCore() {
        orchestrator.wireRealSentientCore(
            new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        assertNotNull(orchestrator.getSentientCoreIntegrator());
        assertTrue(orchestrator.getSentientCoreIntegrator().isInitialized());
    }
    
    @Test
    void testUnwireRealSentientCore() {
        orchestrator.wireRealSentientCore(
            new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        assertTrue(orchestrator.getSentientCoreIntegrator().isInitialized());
        
        orchestrator.unwireRealSentientCore();
        assertFalse(orchestrator.getSentientCoreIntegrator().isInitialized());
    }
    
    @Test
    void testGetBrainTypeCoordinator() {
        brain_architecture.synapses.HyperdimensionalMapper mapper = 
            new brain_architecture.synapses.HyperdimensionalMapper();
        brain_architecture.synapses.HDCSimilarityEngine engine = 
            new brain_architecture.synapses.HDCSimilarityEngine();
        
        orchestrator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), mapper, engine);
        
        assertNotNull(orchestrator.getBrainTypeCoordinator());
        assertTrue(orchestrator.getBrainTypeCoordinator().isInitialized());
    }
    
    @Test
    void testGetBackendIntegrator() {
        brain_architecture.synapses.HyperdimensionalMapper mapper = 
            new brain_architecture.synapses.HyperdimensionalMapper();
        brain_architecture.synapses.HDCSimilarityEngine engine = 
            new brain_architecture.synapses.HDCSimilarityEngine();
        
        orchestrator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), mapper, engine);
        
        assertNotNull(orchestrator.getBackendIntegrator());
        assertTrue(orchestrator.getBackendIntegrator().isInitialized());
    }
    
    @Test
    void testGetHDCIntegrator() {
        brain_architecture.synapses.HyperdimensionalMapper mapper = 
            new brain_architecture.synapses.HyperdimensionalMapper();
        brain_architecture.synapses.HDCSimilarityEngine engine = 
            new brain_architecture.synapses.HDCSimilarityEngine();
        
        orchestrator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), mapper, engine);
        
        assertNotNull(orchestrator.getHDCIntegrator());
        assertTrue(orchestrator.getHDCIntegrator().isInitialized());
    }
    
    @Test
    void testGetSentientCoreIntegrator() {
        orchestrator.wireRealSentientCore(
            new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        assertNotNull(orchestrator.getSentientCoreIntegrator());
        assertTrue(orchestrator.getSentientCoreIntegrator().isInitialized());
    }
    
    @Test
    void testSystemStatusWithFraymusIntegrations() {
        brain_architecture.synapses.HyperdimensionalMapper mapper = 
            new brain_architecture.synapses.HyperdimensionalMapper();
        brain_architecture.synapses.HDCSimilarityEngine engine = 
            new brain_architecture.synapses.HDCSimilarityEngine();
        
        orchestrator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), mapper, engine);
        
        Map<String, Object> status = orchestrator.getSystemStatus();
        assertTrue(status.containsKey("fraymusIntegrations"));
        assertTrue(status.containsKey("legacyIntegrations"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> fraymusStatus = (Map<String, Object>) status.get("fraymusIntegrations");
        assertTrue((Boolean) fraymusStatus.get("fraymusComponentsWired"));
        assertTrue(fraymusStatus.containsKey("brainTypeCoordinator"));
        assertTrue(fraymusStatus.containsKey("backendIntegrator"));
        assertTrue(fraymusStatus.containsKey("hdcIntegrator"));
        assertTrue(fraymusStatus.containsKey("sentientCoreIntegrator"));
    }
    
    @Test
    void testShutdownWithFraymusIntegrations() {
        brain_architecture.synapses.HyperdimensionalMapper mapper = 
            new brain_architecture.synapses.HyperdimensionalMapper();
        brain_architecture.synapses.HDCSimilarityEngine engine = 
            new brain_architecture.synapses.HDCSimilarityEngine();
        
        orchestrator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), mapper, engine);
        orchestrator.wireRealSentientCore(
            new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        assertDoesNotThrow(() -> orchestrator.shutdown());
        
        // After shutdown, Fraymus integrations should be unwired
        Map<String, Object> status = orchestrator.getSystemStatus();
        @SuppressWarnings("unchecked")
        Map<String, Object> fraymusStatus = (Map<String, Object>) status.get("fraymusIntegrations");
        assertFalse((Boolean) fraymusStatus.get("fraymusComponentsWired"));
    }
}
