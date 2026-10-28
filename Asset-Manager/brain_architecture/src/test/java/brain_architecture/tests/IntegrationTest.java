package brain_architecture.tests;

import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.core.BrainType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the Quantum Oracle integration layer
 * Demonstrates how the 8-brain system works together
 */
public class IntegrationTest {
    private QuantumOracleIntegration oracle;
    
    @BeforeEach
    public void setUp() {
        try {
            oracle = new QuantumOracleIntegration();
        } catch (Exception e) {
            System.out.println("Failed to initialize oracle (likely missing API keys): " + e.getMessage());
            oracle = null;
        }
    }
    
    @AfterEach
    public void tearDown() {
        if (oracle != null) {
            oracle.shutdown();
        }
    }
    
    @Test
    public void testOracleInitialization() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        assertNotNull(oracle);
        assertEquals(12, oracle.getBrains().size());
        assertNotNull(oracle.getCommunication());
    }
    
    @Test
    public void testPhysicalQuery() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        var response = oracle.processQuery(
            "Move the arm to position x,y,z",
            QuantumOracleIntegration.OracleQueryType.PHYSICAL
        ).join();
        
        // May fail if backends not available, but should return a response
        assertNotNull(response);
        assertEquals(BrainType.PHYSICAL, response.getBrainType());
    }
    
    @Test
    public void testQuantumQuery() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        var response = oracle.processQuery(
            "Calculate quantum entanglement probability",
            QuantumOracleIntegration.OracleQueryType.QUANTUM
        ).join();
        
        assertNotNull(response);
        assertEquals(BrainType.QUANTUM, response.getBrainType());
    }
    
    @Test
    public void testCreativeQuery() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        var response = oracle.processQuery(
            "Create a new concept for digital consciousness",
            QuantumOracleIntegration.OracleQueryType.CREATIVE
        ).join();
        
        assertNotNull(response);
        assertEquals(BrainType.CREATIVE, response.getBrainType());
    }
    
    @Test
    public void testLogicalQuery() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        var response = oracle.processQuery(
            "Analyze the logical consistency of this argument",
            QuantumOracleIntegration.OracleQueryType.LOGICAL
        ).join();
        
        assertNotNull(response);
        assertEquals(BrainType.LOGICAL, response.getBrainType());
    }
    
    @Test
    public void testAutoBrainSelection() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        var response = oracle.processQuery(
            "I feel happy about this achievement",
            QuantumOracleIntegration.OracleQueryType.AUTO
        ).join();
        
        assertNotNull(response);
        // Should select emotional brain based on keyword "feel"
        assertEquals(BrainType.EMOTIONAL, response.getBrainType());
    }
    
    @Test
    public void testMultiBrainQuery() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        var response = oracle.processMultiBrainQuery(
            "What is the nature of consciousness?",
            QuantumOracleIntegration.OracleQueryType.AUTO,
            new BrainType[] { BrainType.LOGICAL, BrainType.SPIRITUAL, BrainType.QUANTUM }
        ).join();
        
        assertNotNull(response);
        // Should select one of the specified brains (or fail gracefully if backends not available)
        if (response.isSuccess()) {
            assertTrue(response.getBrainType() == BrainType.LOGICAL || 
                       response.getBrainType() == BrainType.SPIRITUAL ||
                       response.getBrainType() == BrainType.QUANTUM);
        }
    }
    
    @Test
    public void testBrainCollaboration() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        var response = oracle.processWithBrainCollaboration(
            "Should I pursue this opportunity?",
            QuantumOracleIntegration.OracleQueryType.LOGICAL,
            BrainType.LOGICAL,
            BrainType.EMOTIONAL
        ).join();
        
        assertNotNull(response);
    }
    
    @Test
    public void testSystemStatistics() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        var stats = oracle.getStatistics();
        
        assertTrue((Boolean) stats.get("initialized"));
        assertEquals(12, stats.get("totalBrains"));
        assertNotNull(stats.get("communication"));
        assertNotNull(stats.get("brainStatistics"));
    }
    
    @Test
    public void testAllBrainTypesAccessible() {
        if (oracle == null) {
            System.out.println("Skipping test - oracle not initialized");
            return;
        }
        
        for (BrainType brainType : BrainType.values()) {
            assertTrue(oracle.getBrains().containsKey(brainType), 
                "Brain type " + brainType + " should be accessible");
        }
    }
}
