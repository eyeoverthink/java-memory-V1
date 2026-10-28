package fraymus;

import fraymus.quantum.EntangledPair;
import fraymus.quantum.SchrodingerFile;
import fraymus.genesis.IdeaCollider;
import fraymus.genesis.RealityForge;
import fraymus.chaos.EvolutionaryChaos;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

/**
 * Tests for Quantum, Genesis, and Chaos systems.
 */
public class TestQuantumSystems {

    // ═══════════════════════════════════════════════════
    // ENTANGLED PAIR
    // ═══════════════════════════════════════════════════

    @Test
    void testEntangledPairCreate() {
        EntangledPair ep = new EntangledPair();
        assertNotNull(ep, "EntangledPair should instantiate");
        assertFalse(ep.isCollapsed(), "Should not be collapsed initially");
    }

    @Test
    void testEntangledPairEntangle() {
        EntangledPair ep = new EntangledPair();
        ep.entangle();
        assertNotNull(ep.getAlice(), "Alice should exist after entangle");
        assertNotNull(ep.getBob(), "Bob should exist after entangle");
        assertTrue(ep.isAlive(), "Pair should be alive after entangle");
    }

    @Test
    void testEntangledPairObserve() throws InterruptedException {
        EntangledPair ep = new EntangledPair();
        ep.entangle();
        Thread.sleep(200); // Let threads start
        ep.observe(1); // Force UP spin
        Thread.sleep(200);
        assertEquals(1, ep.getSpinState(), "Spin state should be UP after observing UP");
    }

    @Test
    void testEntangledPairCollapse() throws InterruptedException {
        EntangledPair ep = new EntangledPair();
        ep.entangle();
        Thread.sleep(200);
        ep.collapse();
        Thread.sleep(200);
        assertTrue(ep.isCollapsed(), "Should be collapsed after collapse()");
    }

    @Test
    void testEntangledPairStatus() {
        EntangledPair ep = new EntangledPair();
        String status = ep.getStatus();
        assertNotNull(status, "Status should not be null");
        assertTrue(status.contains("ENTANGLED"), "Status should mention entanglement");
    }

    @Test
    void testEntangledPairDecohere() {
        EntangledPair ep = new EntangledPair();
        ep.decohere(0.5);
        // Should not throw
    }

    // ═══════════════════════════════════════════════════
    // SCHRÖDINGER FILE
    // ═══════════════════════════════════════════════════

    @Test
    void testSchrodingerFileEntangle() {
        SchrodingerFile sf = new SchrodingerFile();
        SchrodingerFile.QuantumState state = sf.entangle(
            "TOP SECRET: Launch codes 12345",
            "Shopping list: milk, eggs, bread"
        );
        assertNotNull(state, "QuantumState should not be null");
        assertNotNull(state.container, "Container should not be null");
        assertNotNull(state.keySecret, "Secret key should not be null");
        assertNotNull(state.keyDecoy, "Decoy key should not be null");
    }

    @Test
    void testSchrodingerFileObserveSecret() {
        SchrodingerFile sf = new SchrodingerFile();
        String secret = "TOP SECRET DATA";
        String decoy = "Grocery list item";
        SchrodingerFile.QuantumState state = sf.entangle(secret, decoy);
        String observed = sf.observeSecret(state);
        assertTrue(observed.contains(secret), "Secret key should reveal secret: got '" + observed + "'");
    }

    @Test
    void testSchrodingerFileObserveDecoy() {
        SchrodingerFile sf = new SchrodingerFile();
        String secret = "CLASSIFIED INFO";
        String decoy = "Hello World Nice";
        SchrodingerFile.QuantumState state = sf.entangle(secret, decoy);
        String observed = sf.observeDecoy(state);
        assertTrue(observed.contains(decoy), "Decoy key should reveal decoy: got '" + observed + "'");
    }

    @Test
    void testSchrodingerFileDualReality() {
        SchrodingerFile sf = new SchrodingerFile();
        String secret = "NUCLEAR CODES";
        String decoy = "GROCERY LISTS";
        SchrodingerFile.QuantumState state = sf.entangle(secret, decoy);

        String revealedSecret = sf.observeSecret(state);
        String revealedDecoy = sf.observeDecoy(state);

        assertNotEquals(revealedSecret, revealedDecoy,
            "Secret and decoy observations should differ");
    }

    @Test
    void testSchrodingerFileVerify() {
        SchrodingerFile sf = new SchrodingerFile();
        SchrodingerFile.QuantumState state = sf.entangle("SECRET", "DECOY!");
        boolean valid = sf.verify(state);
        assertTrue(valid, "Verification should pass for valid state");
    }

    @Test
    void testSchrodingerFilePortable() {
        SchrodingerFile sf = new SchrodingerFile();
        SchrodingerFile.QuantumState state = sf.entangle("ALPHA", "BRAVO");
        String portable = sf.exportPortable(state);
        assertNotNull(portable, "Portable export should not be null");
        assertTrue(portable.startsWith("QBOX:"), "Portable should start with QBOX:");

        SchrodingerFile.QuantumState restored = sf.importPortable(portable);
        assertNotNull(restored, "Restored state should not be null");
        assertArrayEquals(state.container, restored.container, "Container should survive round-trip");
    }

    // ═══════════════════════════════════════════════════
    // IDEA COLLIDER
    // ═══════════════════════════════════════════════════

    @Test
    void testIdeaColliderCreate() {
        IdeaCollider ic = new IdeaCollider();
        assertNotNull(ic, "IdeaCollider should instantiate");
        assertEquals(0, ic.getTotalCollisions(), "Should start with 0 collisions");
    }

    @Test
    void testIdeaColliderCollide() {
        IdeaCollider ic = new IdeaCollider();
        IdeaCollider.CollisionResult result = ic.collide("SECURITY", "BIOLOGY");
        assertNotNull(result, "Collision result should not be null");
        assertNotNull(result.primaryElement, "Primary element should not be null");
        assertNotNull(result.secondaryElement, "Secondary element should not be null");
        assertTrue(result.energyReleased > 0, "Energy should be positive");
        assertTrue(result.stability >= 0 && result.stability <= 100, "Stability should be [0,100]");
        assertEquals(1, ic.getTotalCollisions(), "Should have 1 collision");
    }

    @Test
    void testIdeaColliderMultipleCollisions() {
        IdeaCollider ic = new IdeaCollider();
        ic.collide("FIRE", "WATER");
        ic.collide("EARTH", "AIR");
        ic.collide("LIGHT", "DARK");
        assertEquals(3, ic.getTotalCollisions(), "Should have 3 collisions");
        assertEquals(3, ic.getHistory().size(), "History should have 3 entries");
    }

    @Test
    void testIdeaColliderLastCollision() {
        IdeaCollider ic = new IdeaCollider();
        assertNull(ic.getLastCollision(), "No last collision initially");
        ic.collide("A", "B");
        IdeaCollider.CollisionResult last = ic.getLastCollision();
        assertNotNull(last, "Should have last collision after collide");
        assertEquals("A", last.conceptA, "conceptA should be A");
        assertEquals("B", last.conceptB, "conceptB should be B");
    }

    @Test
    void testIdeaColliderParticleShower() {
        IdeaCollider ic = new IdeaCollider();
        IdeaCollider.CollisionResult result = ic.collide("QUANTUM", "GRAVITY");
        assertNotNull(result.particleShower, "Particle shower should not be null");
        assertTrue(result.particleShower.size() >= 0, "Particle shower should be a list");
    }

    // ═══════════════════════════════════════════════════
    // REALITY FORGE
    // ═══════════════════════════════════════════════════

    @Test
    void testRealityForgeCreate() {
        RealityForge rf = new RealityForge();
        assertNotNull(rf, "RealityForge should instantiate");
        assertEquals(0, rf.getTotalManifestations(), "Should start with 0 manifestations");
    }

    @Test
    void testRealityForgeManifestKnown() {
        RealityForge rf = new RealityForge();
        RealityForge.Manifestation m = rf.manifest("FIRE");
        assertNotNull(m, "Manifestation should not be null");
        assertEquals("FIRE", m.concept, "Concept should be FIRE");
        assertTrue(m.wasKnown, "FIRE should be a known concept");
        assertTrue(m.appliedTraits.size() > 0, "Should have traits");
        assertEquals(1, rf.getTotalManifestations(), "Should have 1 manifestation");
    }

    @Test
    void testRealityForgeManifestUnknown() {
        RealityForge rf = new RealityForge();
        RealityForge.Manifestation m = rf.manifest("XYZNONEXISTENT");
        assertNotNull(m, "Manifestation should not be null for unknown concept");
        assertFalse(m.wasKnown, "Unknown concept should not be marked as known");
    }

    @Test
    void testRealityForgeKnownConcepts() {
        RealityForge rf = new RealityForge();
        var concepts = rf.getKnownConcepts();
        assertNotNull(concepts, "Known concepts should not be null");
        assertTrue(concepts.size() > 5, "Should have several known concepts");
        assertTrue(concepts.contains("FIRE"), "Should know FIRE");
        assertTrue(concepts.contains("WATER"), "Should know WATER");
    }

    // ═══════════════════════════════════════════════════
    // EVOLUTIONARY CHAOS
    // ═══════════════════════════════════════════════════

    @Test
    void testEvolutionaryChaosCreate() {
        EvolutionaryChaos ec = new EvolutionaryChaos();
        assertNotNull(ec, "EvolutionaryChaos should instantiate");
    }

    @Test
    void testEvolutionaryChaosNextFractal() {
        EvolutionaryChaos ec = new EvolutionaryChaos();
        BigInteger v1 = ec.nextFractal();
        BigInteger v2 = ec.nextFractal();
        assertNotNull(v1, "First fractal should not be null");
        assertNotNull(v2, "Second fractal should not be null");
        assertNotEquals(v1, v2, "Consecutive fractals should differ");
    }

    @Test
    void testEvolutionaryChaosNextInt() {
        EvolutionaryChaos ec = new EvolutionaryChaos();
        for (int i = 0; i < 20; i++) {
            int val = ec.nextInt(100);
            assertTrue(val >= 0 && val < 100, "nextInt(100) should be [0,100), got " + val);
        }
    }

    @Test
    void testEvolutionaryChaosNextDouble() {
        EvolutionaryChaos ec = new EvolutionaryChaos();
        for (int i = 0; i < 20; i++) {
            double val = ec.nextDouble();
            assertTrue(val >= 0.0 && val <= 1.0, "nextDouble should be [0,1], got " + val);
        }
    }

    @Test
    void testEvolutionaryChaosGeneration() {
        EvolutionaryChaos ec = new EvolutionaryChaos();
        BigInteger gen0 = ec.getGeneration();
        ec.nextFractal();
        BigInteger gen1 = ec.getGeneration();
        assertTrue(gen1.compareTo(gen0) > 0, "Generation should increase");
    }

    @Test
    void testEvolutionaryChaosState() {
        EvolutionaryChaos ec = new EvolutionaryChaos();
        BigInteger state = ec.getState();
        assertNotNull(state, "State should not be null");
        assertTrue(state.bitLength() > 0, "State should have positive bit length");
    }

    @Test
    void testEvolutionaryChaosFork() {
        EvolutionaryChaos parent = new EvolutionaryChaos();
        parent.nextFractal();
        EvolutionaryChaos child = parent.fork();
        assertNotNull(child, "Fork should create child");
        BigInteger parentVal = parent.nextFractal();
        BigInteger childVal = child.nextFractal();
        assertNotEquals(parentVal, childVal, "Parent and child should diverge");
    }

    @Test
    void testEvolutionaryChaosForceMutation() {
        EvolutionaryChaos ec = new EvolutionaryChaos();
        long mutations0 = ec.getTotalMutations();
        ec.forceMutation();
        assertTrue(ec.getTotalMutations() > mutations0, "Mutations should increase after force");
    }

    @Test
    void testEvolutionaryChaosStats() {
        EvolutionaryChaos ec = new EvolutionaryChaos();
        ec.nextFractal();
        String stats = ec.getStats();
        assertNotNull(stats, "Stats should not be null");
        assertTrue(stats.contains("EVOLUTIONARY"), "Stats should identify itself");
    }

    @Test
    void testEvolutionaryChaosCustomSeed() {
        EvolutionaryChaos ec1 = new EvolutionaryChaos("SEED_A");
        EvolutionaryChaos ec2 = new EvolutionaryChaos("SEED_B");
        BigInteger v1 = ec1.nextFractal();
        BigInteger v2 = ec2.nextFractal();
        // Different seeds should produce different outputs (high probability)
        assertNotEquals(v1, v2, "Different seeds should produce different initial values");
    }
}
