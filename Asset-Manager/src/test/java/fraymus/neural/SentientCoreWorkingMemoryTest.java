package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Working Memory with SentientCore
 * 
 * Phase 1.2: Working Memory Layer
 * 
 * RULE SC-2: Maintains backward compatibility
 * RULE SC-3: Never break existing functionality
 */
public class SentientCoreWorkingMemoryTest {

    private SentientCore core;
    private WorkingMemory wm;
    private AttentionMechanism att;

    @BeforeEach
    void setUp() {
        core = new SentientCore();
        wm = new WorkingMemory();
        att = new AttentionMechanism();
    }

    @Test
    void testSentientCoreWorksWithoutWorkingMemory() {
        // RULE SC-2: Backward compatibility - works without working memory
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        assertFalse(result.contains("Working Memory"));
        
        // Should work normally
        String status = core.getStatus();
        assertTrue(status.contains("WM: DISABLED"));
    }

    @Test
    void testWireWorkingMemory() {
        core.wireWorkingMemory(wm, att);
        
        assertTrue(core.isWorkingMemoryEnabled());
        assertEquals(wm, core.getWorkingMemory());
        assertEquals(att, core.getAttentionMechanism());
        
        String status = core.getStatus();
        assertTrue(status.contains("WM: ENABLED"));
    }

    @Test
    void testWireWorkingMemoryNull() {
        assertThrows(IllegalArgumentException.class, () -> core.wireWorkingMemory(null, att));
        assertThrows(IllegalArgumentException.class, () -> core.wireWorkingMemory(wm, null));
    }

    @Test
    void testUnwireWorkingMemory() {
        core.wireWorkingMemory(wm, att);
        assertTrue(core.isWorkingMemoryEnabled());
        
        core.unwireWorkingMemory();
        assertFalse(core.isWorkingMemoryEnabled());
        assertNull(core.getWorkingMemory());
        assertNull(core.getAttentionMechanism());
    }

    @Test
    void testLearnWithWorkingMemoryEnabled() {
        core.wireWorkingMemory(wm, att);
        
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should have added to working memory
        assertEquals(1, wm.size());
    }

    @Test
    void testLearnWithoutWorkingMemoryEnabled() {
        // Don't wire working memory
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should not have added to working memory
        assertEquals(0, wm.size());
    }

    @Test
    void testThinkWithWorkingMemoryEnabled() {
        core.wireWorkingMemory(wm, att);
        
        // Learn some concepts first
        core.learn("cats have fur");
        core.learn("dogs have fur");
        core.learn("cats are animals");
        
        // Now query
        String result = core.think("cats");
        
        assertNotNull(result);
        assertTrue(result.contains("Attributes"));
        
        // Should include working memory context
        assertTrue(result.contains("Working Memory"));
    }

    @Test
    void testThinkWithoutWorkingMemoryEnabled() {
        // Don't wire working memory
        core.learn("cats have fur");
        
        String result = core.think("cats");
        
        assertNotNull(result);
        // Should not include working memory context
        assertFalse(result.contains("Working Memory"));
    }

    @Test
    void testWorkingMemoryStatusInStatus() {
        String status = core.getStatus();
        assertTrue(status.contains("WM: DISABLED"));
        
        core.wireWorkingMemory(wm, att);
        status = core.getStatus();
        assertTrue(status.contains("WM: ENABLED"));
    }

    @Test
    void testBackwardCompatibilityOfLearn() {
        // Test that learn() signature hasn't changed
        core.learn("test causes result");
        
        // Should still work exactly as before
        assertEquals(1, core.getCausal().edgeCount());
    }

    @Test
    void testBackwardCompatibilityOfThink() {
        // Test that think() signature hasn't changed
        core.learn("test causes result");
        String result = core.think("why result");
        
        // Should still work exactly as before
        assertNotNull(result);
    }

    @Test
    void testBackwardCompatibilityOfAccessors() {
        // Test that all accessors still work
        assertNotNull(core.getCausal());
        assertNotNull(core.getStats());
        assertNotNull(core.getLattice());
        assertNotNull(core.getAnalogy());
        assertNotNull(core.getFisher());
        assertNotNull(core.getSentience());
    }

    @Test
    void testWorkingMemoryDoesNotModifyExistingStructures() {
        // RULE SC-1: Never modify existing data structures
        core.learn("fire causes burns");
        int edgeCountBefore = core.getCausal().edgeCount();
        
        core.wireWorkingMemory(wm, att);
        core.learn("water causes thirst");
        
        // Causal graph should still work normally
        assertEquals(edgeCountBefore + 1, core.getCausal().edgeCount());
    }

    @Test
    void testWorkingMemoryContextRetention() {
        core.wireWorkingMemory(wm, att);
        
        // Learn multiple statements
        core.learn("cats have fur");
        core.learn("dogs have fur");
        core.learn("cats are animals");
        
        // Should have all in working memory
        assertEquals(3, wm.size());
        
        // Query should show relevant context
        String result = core.think("cats");
        assertTrue(result.contains("Working Memory"));
    }

    @Test
    void testWorkingMemoryWithHDC() {
        // Test that working memory works alongside HDC
        HDCSimilarityEngine hdc = new HDCSimilarityEngine();
        core.wireHDC(hdc);
        core.wireWorkingMemory(wm, att);
        
        core.learn("cats have fur");
        
        // Both should be enabled
        assertTrue(core.isHDCEnabled());
        assertTrue(core.isWorkingMemoryEnabled());
        
        String result = core.think("cats");
        assertTrue(result.contains("HDC Similar"));
        assertTrue(result.contains("Working Memory"));
    }
}
