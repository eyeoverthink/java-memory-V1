package brain_architecture.integration;

import brain_architecture.persistence.BlockChain;
import brain_architecture.persistence.FractalNode;
import brain_architecture.persistence.KnowledgeGraphDelta;
import brain_architecture.synapses.KnowledgeGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PersistenceIntegrationTest - Integration tests for persistence layer with QuantumOracleIntegration.
 */
public class PersistenceIntegrationTest {
    
    private QuantumOracleIntegration oracle;
    
    @BeforeEach
    void setUp() {
        oracle = new QuantumOracleIntegration();
        assertTrue(oracle.getBrains().size() == 12, "All 12 brains should be initialized");
    }
    
    @Test
    void testPersistentMemoryInitialization() {
        assertNotNull(oracle.getPersistentMemory());
        assertTrue(oracle.getPersistentMemory().isInitialized());
    }
    
    @Test
    void testPersistKnowledgeGraph() {
        KnowledgeGraph graph = new KnowledgeGraph("test_source", new java.util.ArrayList<>());
        UUID graphId = oracle.persistKnowledgeGraph(graph);
        
        assertNotNull(graphId);
        assertNotNull(oracle.retrieveKnowledgeGraph(graphId));
    }
    
    @Test
    void testRetrieveKnowledgeGraph() {
        KnowledgeGraph graph = new KnowledgeGraph("test_source", new java.util.ArrayList<>());
        UUID graphId = oracle.persistKnowledgeGraph(graph);
        
        KnowledgeGraph retrieved = oracle.retrieveKnowledgeGraph(graphId);
        assertNotNull(retrieved);
        assertEquals(graph.getSource(), retrieved.getSource());
    }
    
    @Test
    void testUpdateKnowledgeGraph() {
        KnowledgeGraph graph = new KnowledgeGraph("test_source", new java.util.ArrayList<>());
        UUID graphId = oracle.persistKnowledgeGraph(graph);
        
        KnowledgeGraph updated = new KnowledgeGraph("updated_source", new java.util.ArrayList<>());
        oracle.updateKnowledgeGraph(graphId, updated);
        
        KnowledgeGraph retrieved = oracle.retrieveKnowledgeGraph(graphId);
        assertEquals("updated_source", retrieved.getSource());
    }
    
    @Test
    void testDeleteKnowledgeGraph() {
        KnowledgeGraph graph = new KnowledgeGraph("test_source", new java.util.ArrayList<>());
        UUID graphId = oracle.persistKnowledgeGraph(graph);
        
        assertNotNull(oracle.retrieveKnowledgeGraph(graphId));
        
        oracle.deleteKnowledgeGraph(graphId);
        assertNull(oracle.retrieveKnowledgeGraph(graphId));
    }
    
    @Test
    void testGetBlockChain() {
        BlockChain blockchain = oracle.getBlockChain();
        assertNotNull(blockchain);
    }
    
    @Test
    void testBlockchainIntegrity() {
        KnowledgeGraph graph = new KnowledgeGraph("test_source", new java.util.ArrayList<>());
        oracle.persistKnowledgeGraph(graph);
        
        assertTrue(oracle.verifyBlockchainIntegrity());
    }
    
    @Test
    void testCreateFractalNode() {
        KnowledgeGraph subgraph = new KnowledgeGraph("fractal_source", new java.util.ArrayList<>());
        FractalNode node = oracle.createFractalNode(subgraph);
        
        assertNotNull(node);
        assertNotNull(node.getNodeId());
    }
    
    @Test
    void testGetFractalNode() {
        KnowledgeGraph subgraph = new KnowledgeGraph("fractal_source", new java.util.ArrayList<>());
        FractalNode created = oracle.createFractalNode(subgraph);
        
        FractalNode retrieved = oracle.getFractalNode(created.getNodeId());
        assertNotNull(retrieved);
        assertEquals(created.getNodeId(), retrieved.getNodeId());
    }
    
    @Test
    void testKnowledgeIngestionWithPersistence() throws Exception {
        // Ingest knowledge and persist it
        KnowledgeGraph graph = new KnowledgeGraph("ingested_source", new java.util.ArrayList<>());
        UUID graphId = oracle.persistKnowledgeGraph(graph);
        
        // Verify it can be retrieved
        KnowledgeGraph retrieved = oracle.retrieveKnowledgeGraph(graphId);
        assertNotNull(retrieved);
        
        // Verify blockchain entry was created
        BlockChain blockchain = oracle.getBlockChain();
        assertTrue(blockchain.hasGenesisBlock());
    }
    
    @Test
    void testMultipleKnowledgeGraphsPersistence() {
        for (int i = 0; i < 5; i++) {
            KnowledgeGraph graph = new KnowledgeGraph("source_" + i, new java.util.ArrayList<>());
            oracle.persistKnowledgeGraph(graph);
        }
        
        BlockChain blockchain = oracle.getBlockChain();
        assertTrue(blockchain.getSize() >= 5);
    }
    
    @Test
    void testShutdownWithPersistentMemory() {
        QuantumOracleIntegration testOracle = new QuantumOracleIntegration();
        assertNotNull(testOracle.getPersistentMemory());
        
        // Should not throw exception
        testOracle.shutdown();
    }
}
