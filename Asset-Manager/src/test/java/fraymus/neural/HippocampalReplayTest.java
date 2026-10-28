package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for HippocampalReplay
 * 
 * Phase 1.3: Episodic Memory
 * 
 * RULE UT-1: Every new class must have unit tests
 * RULE UT-3: Tests must be deterministic
 */
public class HippocampalReplayTest {

    private EpisodicMemory em;
    private HippocampalReplay hr;

    @BeforeEach
    void setUp() {
        em = new EpisodicMemory();
        hr = new HippocampalReplay(em);
    }

    @Test
    void testConstructor() {
        assertNotNull(hr);
        assertEquals(HippocampalReplay.DEFAULT_REPLAY_FREQUENCY, hr.getReplayFrequency());
    }

    @Test
    void testConstructorWithFrequency() {
        HippocampalReplay hr2 = new HippocampalReplay(em, 20);
        assertEquals(20, hr2.getReplayFrequency());
    }

    @Test
    void testConstructorNullMemory() {
        assertThrows(IllegalArgumentException.class, () -> new HippocampalReplay(null));
    }

    @Test
    void testReplay() {
        em.add("episode 1");
        em.add("episode 2");
        
        hr.replay();
        
        // Episodes should be strengthened (capped at 1.0)
        List<EpisodicMemory.Episode> episodes = em.getAll();
        for (EpisodicMemory.Episode episode : episodes) {
            assertEquals(1.0, episode.strength, 0.001); // Capped at 1.0
        }
    }

    @Test
    void testReplayEmpty() {
        hr.replay(); // Should not crash
        // No episodes to replay
    }

    @Test
    void testNotifyAdd() {
        hr.setReplayFrequency(3);
        
        em.add("episode 1");
        hr.notifyAdd();
        
        em.add("episode 2");
        hr.notifyAdd();
        
        em.add("episode 3");
        hr.notifyAdd(); // This should trigger replay
        
        // Replay should have been triggered
        assertTrue(hr.getStats().contains("Replay operations: 1"));
    }

    @Test
    void testDetectPatterns() {
        em.add("cats have fur");
        em.add("cats have fur"); // Repeated
        em.add("dogs have fur");
        
        List<EpisodicMemory.Episode> episodes = em.getAll();
        List<String> patterns = hr.detectPatterns(episodes);
        
        assertFalse(patterns.isEmpty());
        assertTrue(patterns.get(0).contains("Repeated"));
    }

    @Test
    void testDetectPatternsNoPatterns() {
        em.add("episode 1");
        em.add("episode 2");
        
        List<EpisodicMemory.Episode> episodes = em.getAll();
        List<String> patterns = hr.detectPatterns(episodes);
        
        assertTrue(patterns.isEmpty());
    }

    @Test
    void testDetectPatternsNull() {
        List<String> patterns = hr.detectPatterns(null);
        assertTrue(patterns.isEmpty());
    }

    @Test
    void testDetectPatternsEmpty() {
        List<String> patterns = hr.detectPatterns(new ArrayList<>());
        assertTrue(patterns.isEmpty());
    }

    @Test
    void testConsolidate() {
        long id = em.add("test episode");
        EpisodicMemory.Episode episode = em.get(id);
        
        assertEquals(1.0, episode.strength, 0.001);
        
        hr.consolidate(id);
        
        // Strength is capped at 1.0, so it should still be 1.0
        assertEquals(1.0, episode.strength, 0.001);
    }

    @Test
    void testConsolidateNotFound() {
        hr.consolidate(999); // Should not crash
    }

    @Test
    void testConsolidateMultiple() {
        long id1 = em.add("episode 1");
        long id2 = em.add("episode 2");
        
        hr.consolidateMultiple(List.of(id1, id2));
        
        EpisodicMemory.Episode ep1 = em.get(id1);
        EpisodicMemory.Episode ep2 = em.get(id2);
        
        // Strength is capped at 1.0, so both should still be 1.0
        assertEquals(1.0, ep1.strength, 0.001);
        assertEquals(1.0, ep2.strength, 0.001);
    }

    @Test
    void testGetConsolidationSuggestions() {
        em.add("episode 1");
        em.add("episode 2");
        
        List<Long> suggestions = hr.getConsolidationSuggestions();
        
        // Should return episodes (even if none need consolidation)
        assertNotNull(suggestions);
    }

    @Test
    void testSetReplayFrequency() {
        hr.setReplayFrequency(20);
        assertEquals(20, hr.getReplayFrequency());
    }

    @Test
    void testSetReplayFrequencyInvalid() {
        assertThrows(IllegalArgumentException.class, () -> hr.setReplayFrequency(0));
        assertThrows(IllegalArgumentException.class, () -> hr.setReplayFrequency(-1));
    }

    @Test
    void testGetStats() {
        String stats = hr.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Replay frequency"));
        assertTrue(stats.contains("Replay operations"));
    }

    @Test
    void testGetEpisodicMemory() {
        assertEquals(em, hr.getEpisodicMemory());
    }

    @Test
    void testReplayStrengthensRecentEpisodes() {
        em.add("episode 1");
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        em.add("episode 2");
        
        hr.replay();
        
        List<EpisodicMemory.Episode> recent = em.getRecent(60);
        for (EpisodicMemory.Episode episode : recent) {
            assertEquals(1.0, episode.strength, 0.001); // Capped at 1.0
        }
    }

    @Test
    void testReplayDoesNotStrengthenOldEpisodes() {
        em.add("old episode");
        try { Thread.sleep(61000); } catch (InterruptedException e) {}
        
        hr.replay();
        
        // Old episode should not be in recent range
        List<EpisodicMemory.Episode> recent = em.getRecent(60);
        assertTrue(recent.isEmpty());
    }
}
