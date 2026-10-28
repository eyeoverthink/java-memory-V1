package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for WorkingMemory
 * 
 * Phase 1.2: Working Memory Layer
 * 
 * RULE UT-1: Every new class must have unit tests
 * RULE UT-3: Tests must be deterministic
 */
public class WorkingMemoryTest {

    private WorkingMemory wm;

    @BeforeEach
    void setUp() {
        wm = new WorkingMemory();
    }

    @Test
    void testConstructorDefault() {
        assertNotNull(wm);
        assertEquals(WorkingMemory.DEFAULT_CAPACITY, wm.getCapacity());
    }

    @Test
    void testConstructorWithCapacity() {
        WorkingMemory wm2 = new WorkingMemory(50);
        assertEquals(50, wm2.getCapacity());
    }

    @Test
    void testConstructorInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new WorkingMemory(0));
        assertThrows(IllegalArgumentException.class, () -> new WorkingMemory(-1));
    }

    @Test
    void testAdd() {
        wm.add("test", 0.5);
        
        assertEquals(1, wm.size());
        assertTrue(wm.contains("test"));
    }

    @Test
    void testAddDefaultImportance() {
        wm.add("test");
        
        assertEquals(1, wm.size());
        assertTrue(wm.contains("test"));
    }

    @Test
    void testAddNullContent() {
        assertThrows(IllegalArgumentException.class, () -> wm.add(null, 0.5));
    }

    @Test
    void testAddEmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> wm.add("", 0.5));
    }

    @Test
    void testAddInvalidImportance() {
        assertThrows(IllegalArgumentException.class, () -> wm.add("test", -0.1));
        assertThrows(IllegalArgumentException.class, () -> wm.add("test", 1.1));
    }

    @Test
    void testAddDuplicate() {
        wm.add("test", 0.5);
        wm.add("test", 0.7);
        
        assertEquals(1, wm.size()); // Should replace, not duplicate
    }

    @Test
    void testGet() {
        wm.add("test", 0.5);
        WorkingMemory.MemoryItem item = wm.get("test");
        
        assertNotNull(item);
        assertEquals("test", item.content);
        // Importance may have decayed slightly due to add() triggering decay
        assertTrue(item.importance > 0.0);
    }

    @Test
    void testGetNotFound() {
        wm.add("test", 0.5);
        WorkingMemory.MemoryItem item = wm.get("notfound");
        
        assertNull(item);
    }

    @Test
    void testGetNull() {
        WorkingMemory.MemoryItem item = wm.get(null);
        assertNull(item);
    }

    @Test
    void testGetUpdatesAccessTime() {
        wm.add("test", 0.5);
        WorkingMemory.MemoryItem item = wm.get("test");
        long firstAccess = item.lastAccessed;
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        wm.get("test");
        long secondAccess = item.lastAccessed;
        
        assertTrue(secondAccess > firstAccess);
    }

    @Test
    void testContains() {
        assertFalse(wm.contains("test"));
        wm.add("test", 0.5);
        assertTrue(wm.contains("test"));
    }

    @Test
    void testGetAll() {
        wm.add("item1", 0.5);
        wm.add("item2", 0.7);
        
        List<WorkingMemory.MemoryItem> items = wm.getAll();
        
        assertEquals(2, items.size());
    }

    @Test
    void testGetByImportance() {
        wm.add("low", 0.3);
        wm.add("high", 0.9);
        wm.add("medium", 0.6);
        
        List<WorkingMemory.MemoryItem> items = wm.getByImportance();
        
        assertEquals(3, items.size());
        assertEquals("high", items.get(0).content);
        assertEquals("medium", items.get(1).content);
        assertEquals("low", items.get(2).content);
    }

    @Test
    void testGetByRecency() {
        wm.add("item1", 0.5);
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        wm.add("item2", 0.5);
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        wm.add("item3", 0.5);
        
        List<WorkingMemory.MemoryItem> items = wm.getByRecency();
        
        assertEquals(3, items.size());
        assertEquals("item3", items.get(0).content);
        assertEquals("item2", items.get(1).content);
        assertEquals("item1", items.get(2).content);
    }

    @Test
    void testRemove() {
        wm.add("test", 0.5);
        assertTrue(wm.contains("test"));
        
        boolean removed = wm.remove("test");
        
        assertTrue(removed);
        assertFalse(wm.contains("test"));
        assertEquals(0, wm.size());
    }

    @Test
    void testRemoveNotFound() {
        wm.add("test", 0.5);
        boolean removed = wm.remove("notfound");
        
        assertFalse(removed);
        assertEquals(1, wm.size());
    }

    @Test
    void testRemoveNull() {
        wm.add("test", 0.5);
        boolean removed = wm.remove(null);
        
        assertFalse(removed);
        assertEquals(1, wm.size());
    }

    @Test
    void testClear() {
        wm.add("item1", 0.5);
        wm.add("item2", 0.5);
        
        assertEquals(2, wm.size());
        
        wm.clear();
        
        assertEquals(0, wm.size());
        assertTrue(wm.isEmpty());
    }

    @Test
    void testSetCapacity() {
        wm.setCapacity(50);
        assertEquals(50, wm.getCapacity());
    }

    @Test
    void testSetCapacityInvalid() {
        assertThrows(IllegalArgumentException.class, () -> wm.setCapacity(0));
        assertThrows(IllegalArgumentException.class, () -> wm.setCapacity(-1));
    }

    @Test
    void testSetCapacityEvictsItems() {
        // First add items with default capacity (100)
        wm.add("item1", 0.5);
        wm.add("item2", 0.5);
        wm.add("item3", 0.5);
        wm.add("item4", 0.5);
        
        assertEquals(4, wm.size());
        
        // Then reduce capacity to 2
        wm.setCapacity(2);
        
        // Should evict items to fit new capacity
        assertTrue(wm.size() <= 2);
    }

    @Test
    void testSetDecayRate() {
        wm.setDecayRate(0.1);
        assertEquals(0.1, wm.getDecayRate(), 0.001);
    }

    @Test
    void testSetDecayRateInvalid() {
        assertThrows(IllegalArgumentException.class, () -> wm.setDecayRate(-0.1));
        assertThrows(IllegalArgumentException.class, () -> wm.setDecayRate(1.1));
    }

    @Test
    void testTriggerDecay() {
        wm.add("test", 0.5);
        wm.triggerDecay();
        
        WorkingMemory.MemoryItem item = wm.get("test");
        assertTrue(item.importance < 0.5);
    }

    @Test
    void testDecayRemovesLowImportance() {
        wm.setDecayRate(0.9); // High decay rate
        wm.add("test", 0.01); // Low importance
        
        wm.triggerDecay();
        
        assertFalse(wm.contains("test"));
    }

    @Test
    void testDecayPreservesMinimum() {
        wm.add("test", 0.1); // Higher importance to avoid removal
        wm.triggerDecay();
        
        WorkingMemory.MemoryItem item = wm.get("test");
        if (item != null) {
            assertTrue(item.importance >= 0.001);
        }
    }

    @Test
    void testSize() {
        assertEquals(0, wm.size());
        wm.add("item1", 0.5);
        assertEquals(1, wm.size());
        wm.add("item2", 0.5);
        assertEquals(2, wm.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(wm.isEmpty());
        wm.add("test", 0.5);
        assertFalse(wm.isEmpty());
    }

    @Test
    void testGetStats() {
        wm.add("item1", 0.5);
        wm.add("item2", 0.7);
        
        String stats = wm.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Capacity"));
        assertTrue(stats.contains("Size"));
        assertTrue(stats.contains("Decay rate"));
        assertTrue(stats.contains("Adds"));
    }

    @Test
    void testMemoryItemAge() {
        wm.add("test", 0.5);
        WorkingMemory.MemoryItem item = wm.get("test");
        
        double age1 = item.age();
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        
        double age2 = item.age();
        
        assertTrue(age2 > age1);
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int numThreads = 10;
        int itemsPerThread = 100;
        
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    String content = "thread_" + threadId + "_item_" + j;
                    wm.add(content, 0.5);
                    wm.get(content);
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Should have items (some may have been evicted due to capacity)
        assertTrue(wm.size() > 0);
        assertTrue(wm.size() <= WorkingMemory.DEFAULT_CAPACITY);
    }
}
