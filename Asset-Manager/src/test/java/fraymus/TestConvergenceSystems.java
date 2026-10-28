package fraymus;

import fraymus.organism.NEXUS_Organism;
import fraymus.swarm.Swarm;
import fraymus.omega.DreamState;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

/**
 * Tests for NEXUS Organism, Swarm, OmniCaster, and DreamState.
 */
public class TestConvergenceSystems {

    // ═══════════════════════════════════════════════════
    // NEXUS ORGANISM
    // ═══════════════════════════════════════════════════

    @Test
    void testNexusCreate() {
        NEXUS_Organism nexus = new NEXUS_Organism();
        assertNotNull(nexus, "NEXUS should instantiate");
        assertFalse(nexus.isConscious(), "Should not be conscious before awaken");
    }

    @Test
    void testNexusAwaken() throws InterruptedException {
        NEXUS_Organism nexus = new NEXUS_Organism();
        nexus.awaken();
        Thread.sleep(2000);
        assertTrue(nexus.isConscious(), "Should be conscious after awaken + wait");
        assertTrue(nexus.getHeartbeat() > 0, "Heartbeat should be positive");
        nexus.terminate();
    }

    @Test
    void testNexusVitalSigns() throws InterruptedException {
        NEXUS_Organism nexus = new NEXUS_Organism();
        nexus.awaken();
        Thread.sleep(1500);
        String vitals = nexus.getVitalSigns();
        assertNotNull(vitals, "Vital signs should not be null");
        assertTrue(nexus.getHeartbeat() > 0, "Heartbeat should be positive");
        nexus.terminate();
    }

    // ═══════════════════════════════════════════════════
    // SWARM
    // ═══════════════════════════════════════════════════

    @Test
    void testSwarmSingleton() {
        Swarm s1 = Swarm.getInstance();
        Swarm s2 = Swarm.getInstance();
        assertSame(s1, s2, "Swarm should be singleton");
    }

    @Test
    void testSwarmNodeCount() {
        Swarm s = Swarm.getInstance();
        assertTrue(s.getNodes().size() > 0, "Swarm should have nodes");
    }

    @Test
    void testSwarmThink() {
        Swarm s = Swarm.getInstance();
        assertDoesNotThrow(() -> s.processThought("QUANTUM_TEST"),
            "processThought should not throw");
    }

    @Test
    void testSwarmStatus() {
        Swarm s = Swarm.getInstance();
        String status = s.getStatus();
        assertNotNull(status, "Status should not be null");
        assertTrue(status.contains("SWARM"), "Status should mention SWARM");
    }

    // ═══════════════════════════════════════════════════
    // DREAM STATE
    // ═══════════════════════════════════════════════════

    @Test
    void testDreamStateCreate() throws IOException {
        DreamState ds = new DreamState();
        assertNotNull(ds, "DreamState should instantiate");
    }

    @Test
    void testDreamStateSeedMemories() throws IOException {
        DreamState ds = new DreamState();
        ds.seedMemories();
        // seedMemories loads 30 concepts into HyperMemory — no blocking
    }

    @Test
    void testDreamStateREMInThread() throws Exception {
        DreamState ds = new DreamState();
        ds.seedMemories();
        Thread dreamer = new Thread(ds::enterREM);
        dreamer.setDaemon(true);
        dreamer.start();
        Thread.sleep(500); // Let it dream for 500ms
        ds.wake();         // Signal stop
        dreamer.join(2000); // Wait for thread to finish
        assertFalse(dreamer.isAlive(), "Dream thread should have stopped after wake()");
    }
}
