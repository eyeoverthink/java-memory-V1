package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Integration tests for Episodic Memory with SentientCore
 * 
 * Phase 1.3: Episodic Memory
 * 
 * RULE SC-2: Maintains backward compatibility
 * RULE SC-3: Never break existing functionality
 */
public class SentientCoreEpisodicMemoryTest {

    private SentientCore core;
    private EpisodicMemory em;
    private HippocampalReplay hr;

    @BeforeEach
    void setUp() {
        core = new SentientCore();
        em = new EpisodicMemory();
        hr = new HippocampalReplay(em);
    }

    @Test
    void testSentientCoreWorksWithoutEpisodicMemory() {
        // RULE SC-2: Backward compatibility - works without episodic memory
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        assertFalse(result.contains("Episodic"));
        
        // Should work normally
        String status = core.getStatus();
        assertTrue(status.contains("EP: DISABLED"));
    }

    @Test
    void testWireEpisodicMemory() {
        core.wireEpisodicMemory(em, hr);
        
        assertTrue(core.isEpisodicMemoryEnabled());
        assertEquals(em, core.getEpisodicMemory());
        assertEquals(hr, core.getHippocampalReplay());
        
        String status = core.getStatus();
        assertTrue(status.contains("EP: ENABLED"));
    }

    @Test
    void testWireEpisodicMemoryNull() {
        assertThrows(IllegalArgumentException.class, () -> core.wireEpisodicMemory(null, hr));
        assertThrows(IllegalArgumentException.class, () -> core.wireEpisodicMemory(em, null));
    }

    @Test
    void testUnwireEpisodicMemory() {
        core.wireEpisodicMemory(em, hr);
        assertTrue(core.isEpisodicMemoryEnabled());
        
        core.unwireEpisodicMemory();
        assertFalse(core.isEpisodicMemoryEnabled());
        assertNull(core.getEpisodicMemory());
        assertNull(core.getHippocampalReplay());
    }

    @Test
    void testLearnWithEpisodicMemoryEnabled() {
        core.wireEpisodicMemory(em, hr);
        
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should have added to episodic memory
        assertEquals(1, em.size());
    }

    @Test
    void testLearnWithoutEpisodicMemoryEnabled() {
        // Don't wire episodic memory
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should not have added to episodic memory
        assertEquals(0, em.size());
    }

    @Test
    void testAllObservationsTimestamped() {
        core.wireEpisodicMemory(em, hr);
        
        core.learn("observation 1");
        core.learn("observation 2");
        core.learn("observation 3");
        
        // All should have timestamps
        for (EpisodicMemory.Episode episode : em.getAll()) {
            assertTrue(episode.timestamp > 0);
        }
    }

    @Test
    void testReplayConsolidatesMemories() {
        core.wireEpisodicMemory(em, hr);
        hr.setReplayFrequency(3);
        
        core.learn("observation 1");
        core.learn("observation 2");
        core.learn("observation 3"); // This should trigger replay
        
        // Replay should have been triggered
        assertTrue(hr.getStats().contains("Replay operations: 1"));
    }

    @Test
    void testEpisodicMemoryStatusInStatus() {
        String status = core.getStatus();
        assertTrue(status.contains("EP: DISABLED"));
        
        core.wireEpisodicMemory(em, hr);
        status = core.getStatus();
        assertTrue(status.contains("EP: ENABLED"));
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
    void testEpisodicMemoryDoesNotModifyExistingStructures() {
        // RULE SC-1: Never modify existing data structures
        core.learn("fire causes burns");
        int edgeCountBefore = core.getCausal().edgeCount();
        
        core.wireEpisodicMemory(em, hr);
        core.learn("water causes thirst");
        
        // Causal graph should still work normally
        assertEquals(edgeCountBefore + 1, core.getCausal().edgeCount());
    }

    @Test
    void testEpisodicMemoryWithHDCAndWorkingMemory() {
        // Test all three together
        HDCSimilarityEngine hdc = new HDCSimilarityEngine();
        WorkingMemory wm = new WorkingMemory();
        AttentionMechanism att = new AttentionMechanism();
        
        core.wireHDC(hdc);
        core.wireWorkingMemory(wm, att);
        core.wireEpisodicMemory(em, hr);
        
        core.learn("cats have fur");
        
        // All should be enabled
        assertTrue(core.isHDCEnabled());
        assertTrue(core.isWorkingMemoryEnabled());
        assertTrue(core.isEpisodicMemoryEnabled());
        
        // All should have the observation
        assertTrue(hdc.getMapper().getConceptCount() > 0);
        assertTrue(wm.size() > 0);
        assertTrue(em.size() > 0);
    }

    @Test
    void testSequenceLearning() {
        core.wireEpisodicMemory(em, hr);
        
        // Learn a sequence
        core.learn("first step");
        core.learn("second step");
        core.learn("third step");
        
        // Should be able to retrieve in order
        List<EpisodicMemory.Episode> episodes = em.getAll();
        assertEquals(3, episodes.size());
        
        // Verify timeline is sorted by timestamp
        for (int i = 1; i < episodes.size(); i++) {
            assertTrue(episodes.get(i).timestamp >= episodes.get(i-1).timestamp);
        }
    }

    @Test
    void testTemporalRangeQueries() {
        core.wireEpisodicMemory(em, hr);
        
        long start = System.currentTimeMillis();
        core.learn("old observation");
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        core.learn("new observation");
        long end = System.currentTimeMillis();
        
        // Should be able to query by time range
        List<EpisodicMemory.Episode> episodes = em.getByTimeRange(start, end);
        assertTrue(episodes.size() >= 2);
    }
}
