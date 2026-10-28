package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for EpisodicMemory
 * 
 * Phase 1.3: Episodic Memory
 * 
 * RULE UT-1: Every new class must have unit tests
 * RULE UT-3: Tests must be deterministic
 */
public class EpisodicMemoryTest {

    private EpisodicMemory em;

    @BeforeEach
    void setUp() {
        em = new EpisodicMemory();
    }

    @Test
    void testConstructorDefault() {
        assertNotNull(em);
        assertEquals(EpisodicMemory.DEFAULT_CAPACITY, em.getCapacity());
    }

    @Test
    void testConstructorWithCapacity() {
        EpisodicMemory em2 = new EpisodicMemory(500);
        assertEquals(500, em2.getCapacity());
    }

    @Test
    void testConstructorInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new EpisodicMemory(0));
        assertThrows(IllegalArgumentException.class, () -> new EpisodicMemory(-1));
    }

    @Test
    void testAdd() {
        long id = em.add("test episode");
        
        assertTrue(id > 0);
        assertEquals(1, em.size());
        
        EpisodicMemory.Episode episode = em.get(id);
        assertNotNull(episode);
        assertEquals("test episode", episode.content);
    }

    @Test
    void testAddNullContent() {
        assertThrows(IllegalArgumentException.class, () -> em.add(null));
    }

    @Test
    void testAddEmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> em.add(""));
    }

    @Test
    void testAddMultiple() {
        em.add("episode 1");
        em.add("episode 2");
        em.add("episode 3");
        
        assertEquals(3, em.size());
    }

    @Test
    void testGet() {
        long id = em.add("test episode");
        EpisodicMemory.Episode episode = em.get(id);
        
        assertNotNull(episode);
        assertEquals("test episode", episode.content);
        assertTrue(episode.timestamp > 0);
    }

    @Test
    void testGetNotFound() {
        EpisodicMemory.Episode episode = em.get(999);
        assertNull(episode);
    }

    @Test
    void testGetByTimeRange() {
        long start = System.currentTimeMillis();
        em.add("episode 1");
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        em.add("episode 2");
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        em.add("episode 3");
        long end = System.currentTimeMillis();
        
        List<EpisodicMemory.Episode> episodes = em.getByTimeRange(start, end);
        
        assertEquals(3, episodes.size());
    }

    @Test
    void testGetByTimeRangeEmpty() {
        List<EpisodicMemory.Episode> episodes = em.getByTimeRange(0, System.currentTimeMillis());
        assertTrue(episodes.isEmpty());
    }

    @Test
    void testGetRecent() {
        em.add("episode 1");
        em.add("episode 2");
        
        List<EpisodicMemory.Episode> episodes = em.getRecent(60);
        
        assertEquals(2, episodes.size());
    }

    @Test
    void testGetRecentNone() {
        em.add("old episode");
        // Wait for it to be older than 1 second
        try { Thread.sleep(1100); } catch (InterruptedException e) {}
        
        List<EpisodicMemory.Episode> episodes = em.getRecent(1);
        
        assertTrue(episodes.isEmpty());
    }

    @Test
    void testGetAll() {
        em.add("episode 1");
        em.add("episode 2");
        
        List<EpisodicMemory.Episode> episodes = em.getAll();
        
        assertEquals(2, episodes.size());
    }

    @Test
    void testGetByRecency() {
        em.add("episode 1");
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        em.add("episode 2");
        
        List<EpisodicMemory.Episode> episodes = em.getByRecency();
        
        assertEquals(2, episodes.size());
        assertEquals("episode 2", episodes.get(0).content);
        assertEquals("episode 1", episodes.get(1).content);
    }

    @Test
    void testGetByStrength() {
        long id1 = em.add("episode 1");
        long id2 = em.add("episode 2");
        
        em.strengthen(id1, 0.5);
        
        List<EpisodicMemory.Episode> episodes = em.getByStrength();
        
        assertEquals(2, episodes.size());
        assertEquals("episode 1", episodes.get(0).content);
    }

    @Test
    void testRemove() {
        long id = em.add("test episode");
        assertTrue(em.size() > 0);
        
        boolean removed = em.remove(id);
        
        assertTrue(removed);
        assertEquals(0, em.size());
    }

    @Test
    void testRemoveNotFound() {
        boolean removed = em.remove(999);
        assertFalse(removed);
    }

    @Test
    void testClear() {
        em.add("episode 1");
        em.add("episode 2");
        
        assertEquals(2, em.size());
        
        em.clear();
        
        assertEquals(0, em.size());
        assertTrue(em.isEmpty());
    }

    @Test
    void testStrengthen() {
        long id = em.add("test episode");
        EpisodicMemory.Episode episode = em.get(id);
        
        assertEquals(1.0, episode.strength, 0.001);
        
        em.strengthen(id, 0.5);
        
        assertEquals(1.0, episode.strength, 0.001); // Capped at 1.0
    }

    @Test
    void testStrengthenInvalid() {
        assertThrows(IllegalArgumentException.class, () -> em.strengthen(1, -0.1));
        assertThrows(IllegalArgumentException.class, () -> em.strengthen(1, 1.1));
    }

    @Test
    void testWeaken() {
        long id = em.add("test episode");
        EpisodicMemory.Episode episode = em.get(id);
        
        assertEquals(1.0, episode.strength, 0.001);
        
        em.weaken(id, 0.3);
        
        assertEquals(0.7, episode.strength, 0.001);
    }

    @Test
    void testWeakenRemovesZeroStrength() {
        long id = em.add("test episode");
        
        em.weaken(id, 1.0);
        
        assertNull(em.get(id));
    }

    @Test
    void testWeakenInvalid() {
        assertThrows(IllegalArgumentException.class, () -> em.weaken(1, -0.1));
        assertThrows(IllegalArgumentException.class, () -> em.weaken(1, 1.1));
    }

    @Test
    void testSetCapacity() {
        em.setCapacity(50);
        assertEquals(50, em.getCapacity());
    }

    @Test
    void testSetCapacityInvalid() {
        assertThrows(IllegalArgumentException.class, () -> em.setCapacity(0));
        assertThrows(IllegalArgumentException.class, () -> em.setCapacity(-1));
    }

    @Test
    void testSetCapacityEvictsItems() {
        em.setCapacity(3);
        em.add("episode 1");
        em.add("episode 2");
        em.add("episode 3");
        em.add("episode 4");
        
        assertEquals(3, em.size());
    }

    @Test
    void testSize() {
        assertEquals(0, em.size());
        em.add("episode 1");
        assertEquals(1, em.size());
        em.add("episode 2");
        assertEquals(2, em.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(em.isEmpty());
        em.add("test");
        assertFalse(em.isEmpty());
    }

    @Test
    void testGetStats() {
        em.add("episode 1");
        em.add("episode 2");
        
        String stats = em.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Capacity"));
        assertTrue(stats.contains("Size"));
        assertTrue(stats.contains("Adds"));
    }

    @Test
    void testEpisodeAge() {
        long id = em.add("test episode");
        EpisodicMemory.Episode episode = em.get(id);
        
        double age1 = episode.age();
        
        try {
            Thread.sleep(1100); // Sleep for 1.1 seconds
        } catch (InterruptedException e) {}
        
        double age2 = episode.age();
        
        // Age should have increased by at least 1 second
        assertTrue(age2 >= age1 + 1.0);
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int numThreads = 10;
        int episodesPerThread = 100;
        
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < episodesPerThread; j++) {
                    em.add("thread_" + threadId + "_episode_" + j);
                    em.get(threadId * episodesPerThread + j);
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Should have episodes (some may have been evicted due to capacity)
        assertTrue(em.size() > 0);
        assertTrue(em.size() <= EpisodicMemory.DEFAULT_CAPACITY);
    }

    @Test
    void testTimelineSorted() {
        em.add("episode 1");
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        em.add("episode 2");
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        em.add("episode 3");
        
        List<EpisodicMemory.Episode> timeline = em.getAll();
        
        // Verify sorted by timestamp
        for (int i = 1; i < timeline.size(); i++) {
            assertTrue(timeline.get(i).timestamp >= timeline.get(i-1).timestamp);
        }
    }
}
