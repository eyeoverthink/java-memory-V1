package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for HDC with SentientCore
 * 
 * Phase 1.1: HDC Integration with SentientCore
 * 
 * RULE SC-2: Maintains backward compatibility
 * RULE SC-3: Never break existing functionality
 */
public class SentientCoreHDCTest {

    private SentientCore core;
    private HDCSimilarityEngine hdcEngine;

    @BeforeEach
    void setUp() {
        core = new SentientCore();
        hdcEngine = new HDCSimilarityEngine();
    }

    @Test
    void testSentientCoreWorksWithoutHDC() {
        // RULE SC-2: Backward compatibility - works without HDC
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        assertFalse(result.contains("HDC"));
        
        // Should work normally
        String status = core.getStatus();
        assertTrue(status.contains("HDC: DISABLED"));
    }

    @Test
    void testWireHDC() {
        core.wireHDC(hdcEngine);
        
        assertTrue(core.isHDCEnabled());
        assertEquals(hdcEngine, core.getHDCEngine());
        
        String status = core.getStatus();
        assertTrue(status.contains("HDC: ENABLED"));
    }

    @Test
    void testWireHDCNull() {
        assertThrows(IllegalArgumentException.class, () -> core.wireHDC(null));
    }

    @Test
    void testUnwireHDC() {
        core.wireHDC(hdcEngine);
        assertTrue(core.isHDCEnabled());
        
        core.unwireHDC();
        assertFalse(core.isHDCEnabled());
        assertNull(core.getHDCEngine());
    }

    @Test
    void testLearnWithHDCEnabled() {
        core.wireHDC(hdcEngine);
        
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should have mapped concepts to HDC (at least FIRE and BURNS)
        assertTrue(hdcEngine.getMapper().getConceptCount() >= 2);
        assertTrue(hdcEngine.getMapper().isMapped("FIRE"));
        assertTrue(hdcEngine.getMapper().isMapped("BURNS"));
    }

    @Test
    void testLearnWithoutHDCEnabled() {
        // Don't wire HDC
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should not have mapped concepts
        assertEquals(0, hdcEngine.getMapper().getConceptCount());
    }

    @Test
    void testThinkWithHDCEnabled() {
        core.wireHDC(hdcEngine);
        
        // Learn some concepts first
        core.learn("cats have fur");
        core.learn("dogs have fur");
        core.learn("cats are animals");
        
        // Now query
        String result = core.think("cats");
        
        assertNotNull(result);
        assertTrue(result.contains("Attributes"));
        
        // Should include HDC similarity
        assertTrue(result.contains("HDC Similar"));
    }

    @Test
    void testThinkWithoutHDCEnabled() {
        // Don't wire HDC
        core.learn("cats have fur");
        
        String result = core.think("cats");
        
        assertNotNull(result);
        // Should not include HDC similarity
        assertFalse(result.contains("HDC Similar"));
    }

    @Test
    void testHDCStatusInStatus() {
        String status = core.getStatus();
        assertTrue(status.contains("HDC: DISABLED"));
        
        core.wireHDC(hdcEngine);
        status = core.getStatus();
        assertTrue(status.contains("HDC: ENABLED"));
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
    void testHDCDoesNotModifyExistingStructures() {
        // RULE SC-1: Never modify existing data structures
        core.learn("fire causes burns");
        int edgeCountBefore = core.getCausal().edgeCount();
        
        core.wireHDC(hdcEngine);
        core.learn("water causes thirst");
        
        // Causal graph should still work normally
        assertEquals(edgeCountBefore + 1, core.getCausal().edgeCount());
    }

    @Test
    void testHDCConceptMapping() {
        core.wireHDC(hdcEngine);
        
        core.learn("red circle");
        core.learn("blue square");
        
        // Should have mapped words to HDC
        assertTrue(hdcEngine.getMapper().isMapped("RED"));
        assertTrue(hdcEngine.getMapper().isMapped("CIRCLE"));
        assertTrue(hdcEngine.getMapper().isMapped("BLUE"));
        assertTrue(hdcEngine.getMapper().isMapped("SQUARE"));
    }

    @Test
    void testHDCSimilaritySearch() {
        core.wireHDC(hdcEngine);
        
        core.learn("cats are felines");
        core.learn("dogs are canines");
        
        String result = core.think("cats");
        
        // Should show similar concepts
        assertTrue(result.contains("HDC Similar"));
    }
}
