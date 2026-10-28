package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Context retention and decay tests for Working Memory
 * 
 * Phase 1.2: Working Memory Layer
 * 
 * Success Criteria:
 * - 80% context retention across multiple think() calls
 * - Attention focuses on relevant information
 * - Memory decays appropriately
 * - No memory leaks
 */
public class WorkingMemoryContextTest {

    private SentientCore core;
    private WorkingMemory wm;
    private AttentionMechanism att;

    @BeforeEach
    void setUp() {
        core = new SentientCore();
        wm = new WorkingMemory(50); // Smaller capacity for testing
        att = new AttentionMechanism();
        core.wireWorkingMemory(wm, att);
    }

    @Test
    void testContextRetention() {
        // Learn 10 statements
        for (int i = 0; i < 10; i++) {
            core.learn("statement " + i + " is important");
        }
        
        // Should have all in working memory (capacity is 50)
        assertEquals(10, wm.size());
        
        // Query each one - should maintain context
        for (int i = 0; i < 10; i++) {
            String result = core.think("statement " + i);
            assertNotNull(result);
            assertTrue(result.contains("Working Memory"));
        }
        
        // 80% retention: at least 8 should still be in working memory
        assertTrue(wm.size() >= 8, "Context retention below 80%");
    }

    @Test
    void testAttentionFocusesOnRelevant() {
        // Learn diverse statements
        core.learn("cats have fur");
        core.learn("dogs have fur");
        core.learn("birds have wings");
        core.learn("fish have scales");
        core.learn("cats are animals");
        core.learn("dogs are animals");
        
        // Query about cats - should focus on cat-related items
        String result = core.think("cats");
        
        assertNotNull(result);
        assertTrue(result.contains("Working Memory"));
        
        // Get the focused items
        java.util.List<WorkingMemory.MemoryItem> allItems = wm.getAll();
        java.util.List<WorkingMemory.MemoryItem> focused = att.focusTopK(allItems, "cats", 3);
        
        // At least one should be cat-related
        boolean catRelated = focused.stream()
            .anyMatch(item -> item.content.toLowerCase().contains("cat"));
        
        assertTrue(catRelated, "Attention did not focus on relevant information");
    }

    @Test
    void testMemoryDecay() {
        wm.setDecayRate(0.5); // High decay rate for testing
        
        // Add items with varying importance
        wm.add("high importance", 0.9);
        wm.add("low importance", 0.1);
        
        assertEquals(2, wm.size());
        
        // Trigger decay
        wm.triggerDecay();
        
        // High importance item should still exist (may have decayed but still present)
        WorkingMemory.MemoryItem highItem = wm.get("high importance");
        assertNotNull(highItem, "High importance item was removed");
        // May have decayed but should still be > 0
        assertTrue(highItem.importance > 0.0, "High importance item decayed to zero");
    }

    @Test
    void testMemoryDecayWithCapacity() {
        wm.setCapacity(5);
        wm.setDecayRate(0.0); // Disable decay for this test to focus on capacity
        
        // Add items up to capacity
        for (int i = 0; i < 5; i++) {
            wm.add("item " + i, 0.5);
        }
        
        assertEquals(5, wm.size());
        
        // Reduce capacity - should evict items
        wm.setCapacity(2);
        
        // Should not exceed new capacity
        assertTrue(wm.size() <= 2);
    }

    @Test
    void testNoMemoryLeaks() {
        // Add and remove items repeatedly
        for (int i = 0; i < 100; i++) {
            wm.add("temp item " + i, 0.5);
            wm.get("temp item " + i);
            wm.remove("temp item " + i);
        }
        
        // Memory should be empty
        assertEquals(0, wm.size());
        
        // Stats should reflect operations
        String stats = wm.getStats();
        assertTrue(stats.contains("Adds: 100"));
    }

    @Test
    void testContextAcrossMultipleThinkCalls() {
        // Learn a sequence
        core.learn("first statement");
        core.learn("second statement");
        core.learn("third statement");
        
        // Multiple think calls should maintain context
        String result1 = core.think("first");
        String result2 = core.think("second");
        String result3 = core.think("third");
        
        assertTrue(result1.contains("Working Memory"));
        assertTrue(result2.contains("Working Memory"));
        assertTrue(result3.contains("Working Memory"));
        
        // All should still be in working memory
        assertEquals(3, wm.size());
    }

    @Test
    void testAttentionDepthLimit() {
        att.setAttentionDepth(3);
        
        // Add many items
        for (int i = 0; i < 10; i++) {
            wm.add("item " + i, 0.5);
        }
        
        // Focus should respect depth limit
        java.util.List<WorkingMemory.MemoryItem> allItems = wm.getAll();
        java.util.List<WorkingMemory.MemoryItem> focused = att.focusTopK(allItems, "item", 10);
        
        // Should return at most attention depth
        assertTrue(focused.size() <= 3);
    }

    @Test
    void testRelevanceThresholdFiltering() {
        att.setRelevanceThreshold(0.8);
        
        wm.add("cat has fur", 0.5);
        wm.add("cat is an animal", 0.5);
        wm.add("dog has fur", 0.5);
        wm.add("unrelated statement", 0.5);
        
        java.util.List<WorkingMemory.MemoryItem> allItems = wm.getAll();
        java.util.List<WorkingMemory.MemoryItem> filtered = att.filterByThreshold(allItems, "cat");
        
        // Should only return items relevant to "cat"
        for (WorkingMemory.MemoryItem item : filtered) {
            assertTrue(item.content.toLowerCase().contains("cat") || 
                      item.content.toLowerCase().contains("animal"));
        }
    }

    @Test
    void testCycleDetection() {
        wm.add("item1", 0.5);
        wm.add("item2", 0.5);
        
        java.util.List<WorkingMemory.MemoryItem> items = wm.getAll();
        java.util.List<java.util.List<WorkingMemory.MemoryItem>> history = new java.util.ArrayList<>();
        
        // First attention
        history.add(att.focusTopK(items, "item", 2));
        
        // Same attention again
        boolean detected = att.detectCycle(history, att.focusTopK(items, "item", 2));
        
        assertTrue(detected, "Cycle not detected");
    }

    @Test
    void testWorkingMemoryWithHDCAndAttention() {
        // Test all three together
        HDCSimilarityEngine hdc = new HDCSimilarityEngine();
        core.wireHDC(hdc);
        core.wireWorkingMemory(wm, att);
        
        core.learn("cats have fur");
        core.learn("cats are animals");
        core.learn("dogs have fur");
        
        String result = core.think("cats");
        
        // Should show all three sources
        assertTrue(result.contains("Attributes"));
        assertTrue(result.contains("HDC Similar"));
        assertTrue(result.contains("Working Memory"));
    }

    @Test
    void testWorkingMemoryUnwire() {
        core.wireWorkingMemory(wm, att);
        
        core.learn("test statement");
        assertEquals(1, wm.size());
        
        core.unwireWorkingMemory();
        
        // Should no longer add to working memory
        core.learn("another statement");
        assertEquals(1, wm.size()); // Still 1, not 2
    }
}
