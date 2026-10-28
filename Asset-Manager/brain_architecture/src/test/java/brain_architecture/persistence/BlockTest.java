package brain_architecture.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BlockTest - Unit tests for Block class.
 */
public class BlockTest {
    
    private Block block;
    private KnowledgeGraphDelta delta;
    
    @BeforeEach
    void setUp() {
        delta = new KnowledgeGraphDelta(java.util.UUID.randomUUID(), KnowledgeGraphDelta.DeltaType.ADD);
        block = new Block(0, delta, "0");
    }
    
    @Test
    void testBlockCreation() {
        assertEquals(0, block.getIndex());
        assertEquals(delta, block.getData());
        assertEquals("0", block.getPreviousHash());
        assertNotNull(block.getHash());
        assertEquals(0, block.getNonce());
    }
    
    @Test
    void testCalculateHash() {
        String hash1 = block.calculateHash();
        String hash2 = block.calculateHash();
        assertEquals(hash1, hash2); // Deterministic
    }
    
    @Test
    void testHashChangesWithNonce() {
        String hashBefore = block.getHash();
        block.setNonce(1);
        String hashAfter = block.calculateHash();
        assertNotEquals(hashBefore, hashAfter);
    }
    
    @Test
    void testMine() {
        int difficulty = 2;
        block.mine(difficulty);
        
        String hash = block.getHash();
        assertTrue(hash.substring(0, difficulty).equals("00"));
        assertTrue(block.getNonce() > 0);
    }
    
    @Test
    void testMineWithZeroDifficulty() {
        int difficulty = 0;
        block.mine(difficulty);
        assertEquals(0, block.getNonce()); // Should not mine with zero difficulty
    }
    
    @Test
    void testIsValid() {
        block.mine(2);
        assertTrue(block.isValid());
    }
    
    @Test
    void testIsInvalidAfterTampering() {
        block.mine(2);
        String originalHash = block.getHash();
        block.setHash("tampered_hash");
        assertFalse(block.isValid());
    }
    
    @Test
    void testHashConsistency() {
        block.mine(2);
        String hash1 = block.getHash();
        String hash2 = block.calculateHash();
        assertEquals(hash1, hash2);
    }
    
    @Test
    void testSetters() {
        block.setIndex(5);
        block.setNonce(100);
        block.setPreviousHash("previous");
        block.setHash("new_hash");
        
        assertEquals(5, block.getIndex());
        assertEquals(100, block.getNonce());
        assertEquals("previous", block.getPreviousHash());
        assertEquals("new_hash", block.getHash());
    }
    
    @Test
    void testToString() {
        String str = block.toString();
        assertTrue(str.contains("index="));
        assertTrue(str.contains("hash="));
        assertTrue(str.contains("previousHash="));
    }
    
    @Test
    void testBlockWithNullData() {
        Block blockWithNull = new Block(1, null, "hash");
        assertNotNull(blockWithNull);
        assertNull(blockWithNull.getData());
        blockWithNull.calculateHash(); // Should not throw
    }
}
