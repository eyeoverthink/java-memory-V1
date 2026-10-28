package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for AttentionMechanism
 * 
 * Phase 1.2: Working Memory Layer
 * 
 * RULE UT-1: Every new class must have unit tests
 * RULE UT-3: Tests must be deterministic
 */
public class AttentionMechanismTest {

    private AttentionMechanism attention;
    private WorkingMemory wm;

    @BeforeEach
    void setUp() {
        attention = new AttentionMechanism();
        wm = new WorkingMemory();
    }

    @Test
    void testConstructorDefault() {
        assertNotNull(attention);
        assertEquals(AttentionMechanism.DEFAULT_ATTENTION_DEPTH, attention.getAttentionDepth());
    }

    @Test
    void testConstructorWithDepth() {
        AttentionMechanism att = new AttentionMechanism(20);
        assertEquals(20, att.getAttentionDepth());
    }

    @Test
    void testConstructorInvalidDepth() {
        assertThrows(IllegalArgumentException.class, () -> new AttentionMechanism(0));
        assertThrows(IllegalArgumentException.class, () -> new AttentionMechanism(-1));
        assertThrows(IllegalArgumentException.class, () -> new AttentionMechanism(101));
    }

    @Test
    void testScore() {
        wm.add("cat has fur", 0.5);
        wm.add("dog has fur", 0.5);
        wm.add("bird has wings", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<AttentionMechanism.ScoredItem> scored = attention.score(items, "cat");
        
        assertNotNull(scored);
        assertEquals(3, scored.size());
        // First item should be highest score (contains "cat")
        assertTrue(scored.get(0).score > scored.get(1).score);
    }

    @Test
    void testScoreEmptyItems() {
        List<AttentionMechanism.ScoredItem> scored = attention.score(new ArrayList<>(), "test");
        assertNotNull(scored);
        assertTrue(scored.isEmpty());
    }

    @Test
    void testScoreNullQuery() {
        wm.add("test", 0.5);
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<AttentionMechanism.ScoredItem> scored = attention.score(items, null);
        assertNotNull(scored);
        assertTrue(scored.isEmpty());
    }

    @Test
    void testScoreEmptyQuery() {
        wm.add("test", 0.5);
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<AttentionMechanism.ScoredItem> scored = attention.score(items, "");
        assertNotNull(scored);
        assertTrue(scored.isEmpty());
    }

    @Test
    void testFocusTopK() {
        wm.add("cat has fur", 0.5);
        wm.add("dog has fur", 0.5);
        wm.add("bird has wings", 0.5);
        wm.add("fish has scales", 0.5);
        wm.add("snake has scales", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<WorkingMemory.MemoryItem> focused = attention.focusTopK(items, "cat", 3);
        
        assertNotNull(focused);
        assertEquals(3, focused.size());
    }

    @Test
    void testFocusTopKInvalidK() {
        assertThrows(IllegalArgumentException.class, () -> attention.focusTopK(new ArrayList<>(), "test", 0));
        assertThrows(IllegalArgumentException.class, () -> attention.focusTopK(new ArrayList<>(), "test", -1));
    }

    @Test
    void testFocusTopKDefaultDepth() {
        wm.add("item1", 0.5);
        wm.add("item2", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<WorkingMemory.MemoryItem> focused = attention.focusTopK(items, "item");
        
        assertNotNull(focused);
        assertTrue(focused.size() <= AttentionMechanism.DEFAULT_ATTENTION_DEPTH);
    }

    @Test
    void testFocusTopKEnforcesDepthLimit() {
        attention.setAttentionDepth(2);
        
        wm.add("item1", 0.5);
        wm.add("item2", 0.5);
        wm.add("item3", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<WorkingMemory.MemoryItem> focused = attention.focusTopK(items, "item", 10);
        
        assertEquals(2, focused.size());
    }

    @Test
    void testFilterByThreshold() {
        wm.add("cat has fur", 0.5);
        wm.add("dog has fur", 0.5);
        wm.add("bird has wings", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<WorkingMemory.MemoryItem> filtered = attention.filterByThreshold(items, "cat", 0.5);
        
        assertNotNull(filtered);
        // Should only include items with relevance >= 0.5
        for (WorkingMemory.MemoryItem item : filtered) {
            // Item should contain "cat" or be highly relevant
            assertTrue(item.content.toLowerCase().contains("cat"));
        }
    }

    @Test
    void testFilterByThresholdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> attention.filterByThreshold(new ArrayList<>(), "test", -0.1));
        assertThrows(IllegalArgumentException.class, () -> attention.filterByThreshold(new ArrayList<>(), "test", 1.1));
    }

    @Test
    void testFilterByThresholdDefault() {
        wm.add("cat has fur", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<WorkingMemory.MemoryItem> filtered = attention.filterByThreshold(items, "cat");
        
        assertNotNull(filtered);
    }

    @Test
    void testDetectCycle() {
        wm.add("item1", 0.5);
        wm.add("item2", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<List<WorkingMemory.MemoryItem>> history = new ArrayList<>();
        history.add(items);
        
        boolean detected = attention.detectCycle(history, items);
        
        assertTrue(detected);
    }

    @Test
    void testDetectCycleNoMatch() {
        wm.add("item1", 0.5);
        wm.add("item2", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        List<List<WorkingMemory.MemoryItem>> history = new ArrayList<>();
        
        wm.add("item3", 0.5);
        List<WorkingMemory.MemoryItem> differentItems = wm.getAll();
        
        boolean detected = attention.detectCycle(history, differentItems);
        
        assertFalse(detected);
    }

    @Test
    void testDetectCycleEmptyHistory() {
        wm.add("item1", 0.5);
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        
        boolean detected = attention.detectCycle(new ArrayList<>(), items);
        
        assertFalse(detected);
    }

    @Test
    void testDetectCycleNull() {
        boolean detected = attention.detectCycle(null, null);
        assertFalse(detected);
    }

    @Test
    void testSetAttentionDepth() {
        attention.setAttentionDepth(20);
        assertEquals(20, attention.getAttentionDepth());
    }

    @Test
    void testSetAttentionDepthInvalid() {
        assertThrows(IllegalArgumentException.class, () -> attention.setAttentionDepth(0));
        assertThrows(IllegalArgumentException.class, () -> attention.setAttentionDepth(-1));
        assertThrows(IllegalArgumentException.class, () -> attention.setAttentionDepth(101));
    }

    @Test
    void testSetRelevanceThreshold() {
        attention.setRelevanceThreshold(0.7);
        assertEquals(0.7, attention.getRelevanceThreshold(), 0.001);
    }

    @Test
    void testSetRelevanceThresholdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> attention.setRelevanceThreshold(-0.1));
        assertThrows(IllegalArgumentException.class, () -> attention.setRelevanceThreshold(1.1));
    }

    @Test
    void testGetStats() {
        String stats = attention.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Attention depth"));
        assertTrue(stats.contains("Relevance threshold"));
        assertTrue(stats.contains("Scoring operations"));
    }

    @Test
    void testScoredItemToString() {
        wm.add("test", 0.5);
        WorkingMemory.MemoryItem item = wm.get("test");
        
        AttentionMechanism.ScoredItem scored = new AttentionMechanism.ScoredItem(item, 0.75);
        String str = scored.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("test"));
        assertTrue(str.contains("0.75"));
    }
}
