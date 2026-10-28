package brain_architecture.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BlockChainTest - Unit tests for BlockChain class.
 */
public class BlockChainTest {
    
    private BlockChain blockchain;
    
    @BeforeEach
    void setUp() {
        blockchain = new BlockChain(2);
    }
    
    @Test
    void testBlockchainCreation() {
        assertNotNull(blockchain);
        assertEquals(0, blockchain.getSize());
        assertFalse(blockchain.hasGenesisBlock());
        assertEquals(2, blockchain.getDifficulty());
    }
    
    @Test
    void testCreateGenesisBlock() {
        KnowledgeGraphDelta delta = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(delta);
        
        assertTrue(blockchain.hasGenesisBlock());
        assertEquals(1, blockchain.getSize());
        assertNotNull(blockchain.getGenesisHash());
    }
    
    @Test
    void testCannotCreateGenesisBlockTwice() {
        KnowledgeGraphDelta delta1 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        KnowledgeGraphDelta delta2 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        
        blockchain.createGenesisBlock(delta1);
        assertThrows(IllegalStateException.class, () -> blockchain.createGenesisBlock(delta2));
    }
    
    @Test
    void testAddBlock() {
        // Create genesis first
        KnowledgeGraphDelta genesisDelta = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(genesisDelta);
        
        // Add new block
        KnowledgeGraphDelta delta = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.ADD
        );
        blockchain.addBlock(delta);
        
        assertEquals(2, blockchain.getSize());
    }
    
    @Test
    void testCannotAddBlockWithoutGenesis() {
        KnowledgeGraphDelta delta = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.ADD
        );
        assertThrows(IllegalStateException.class, () -> blockchain.addBlock(delta));
    }
    
    @Test
    void testGetLatestBlock() {
        KnowledgeGraphDelta delta1 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(delta1);
        
        Block latest = blockchain.getLatestBlock();
        assertNotNull(latest);
        assertEquals(0, latest.getIndex());
    }
    
    @Test
    void testGetBlockByIndex() {
        KnowledgeGraphDelta delta1 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(delta1);
        
        KnowledgeGraphDelta delta2 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.ADD
        );
        blockchain.addBlock(delta2);
        
        Block block0 = blockchain.getBlock(0);
        Block block1 = blockchain.getBlock(1);
        
        assertNotNull(block0);
        assertNotNull(block1);
        assertEquals(0, block0.getIndex());
        assertEquals(1, block1.getIndex());
    }
    
    @Test
    void testGetBlockInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> blockchain.getBlock(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> blockchain.getBlock(0));
    }
    
    @Test
    void testIsChainValid() {
        KnowledgeGraphDelta delta1 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(delta1);
        
        KnowledgeGraphDelta delta2 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.ADD
        );
        blockchain.addBlock(delta2);
        
        assertTrue(blockchain.isChainValid());
    }
    
    @Test
    void testChainInvalidAfterTampering() {
        KnowledgeGraphDelta delta1 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(delta1);
        
        KnowledgeGraphDelta delta2 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.ADD
        );
        blockchain.addBlock(delta2);
        
        // Tamper with the chain
        Block block = blockchain.getBlock(0);
        block.setHash("tampered");
        
        assertFalse(blockchain.isChainValid());
    }
    
    @Test
    void testCalculateMerkleRoot() {
        KnowledgeGraphDelta delta1 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(delta1);
        
        String merkleRoot = blockchain.calculateMerkleRoot();
        assertNotNull(merkleRoot);
        assertFalse(merkleRoot.isEmpty());
    }
    
    @Test
    void testMerkleRootEmptyChain() {
        String merkleRoot = blockchain.calculateMerkleRoot();
        assertEquals("", merkleRoot);
    }
    
    @Test
    void testGetChain() {
        KnowledgeGraphDelta delta1 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(delta1);
        
        var chain = blockchain.getChain();
        assertNotNull(chain);
        assertEquals(1, chain.size());
    }
    
    @Test
    void testSetDifficulty() {
        blockchain.setDifficulty(5);
        assertEquals(5, blockchain.getDifficulty());
    }
    
    @Test
    void testPreviousHashLink() {
        KnowledgeGraphDelta delta1 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.INITIAL
        );
        blockchain.createGenesisBlock(delta1);
        
        KnowledgeGraphDelta delta2 = new KnowledgeGraphDelta(
            java.util.UUID.randomUUID(), 
            KnowledgeGraphDelta.DeltaType.ADD
        );
        blockchain.addBlock(delta2);
        
        Block block0 = blockchain.getBlock(0);
        Block block1 = blockchain.getBlock(1);
        
        assertEquals(block0.getHash(), block1.getPreviousHash());
    }
}
