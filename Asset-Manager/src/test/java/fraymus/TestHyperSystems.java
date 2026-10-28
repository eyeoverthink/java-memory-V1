package fraymus;

import fraymus.hyper.*;
import fraymus.cymatics.HarmonicLanguage;
import fraymus.temporal.TachyonRouter;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Hyper Systems, Harmonic Language, and TachyonRouter
 * wired into FraynixBoot batch 2.
 */
public class TestHyperSystems {

    // ═══════════════════════════════════════════════════
    // HYPERVECTOR
    // ═══════════════════════════════════════════════════

    @Test
    void testHyperVectorDimensions() {
        assertEquals(10000, HyperVector.D, "HyperVector should be 10,000-D");
    }

    @Test
    void testHyperVectorLive() {
        HyperVector v = HyperVector.live();
        assertNotNull(v, "live() should return a vector");
        double density = v.density();
        assertTrue(density > 0.3 && density < 0.7, "Random vector density should be ~0.5, got " + density);
    }

    @Test
    void testHyperVectorSelfSimilarity() {
        HyperVector v = HyperVector.live();
        assertEquals(1.0, v.similarity(v), 0.001, "Self-similarity should be 1.0");
    }

    @Test
    void testHyperVectorOrthogonality() {
        HyperVector v1 = HyperVector.live();
        HyperVector v2 = HyperVector.live();
        double sim = v1.similarity(v2);
        assertTrue(sim > 0.4 && sim < 0.6, "Random vectors should be ~0.5 similar, got " + sim);
    }

    @Test
    void testHyperVectorBindUnbind() {
        HyperVector a = HyperVector.live();
        HyperVector b = HyperVector.live();
        HyperVector bound = a.bind(b);
        HyperVector recovered = bound.unbind(a);
        double sim = recovered.similarity(b);
        assertTrue(sim > 0.99, "Unbind should recover original: similarity=" + sim);
    }

    @Test
    void testHyperVectorPermute() {
        HyperVector v = HyperVector.live();
        HyperVector permuted = v.permute();
        double sim = v.similarity(permuted);
        assertTrue(sim > 0.4 && sim < 0.6, "Permuted should be quasi-orthogonal, got " + sim);
    }

    @Test
    void testHyperVectorClone() {
        HyperVector v = HyperVector.live();
        HyperVector clone = v.clone();
        assertEquals(1.0, v.similarity(clone), 0.001, "Clone should be identical");
    }

    @Test
    void testHyperVectorZero() {
        HyperVector z = HyperVector.zero();
        assertEquals(0.0, z.density(), 0.001, "Zero vector density should be 0");
    }

    // ═══════════════════════════════════════════════════
    // HYPERMEMORY
    // ═══════════════════════════════════════════════════

    @Test
    void testHyperMemoryLearn() {
        HyperMemory hm = new HyperMemory();
        hm.learn("DOG");
        hm.learn("CAT");
        assertEquals(2, hm.conceptCount(), "Should have 2 concepts");
    }

    @Test
    void testHyperMemoryKnows() {
        HyperMemory hm = new HyperMemory();
        hm.learn("ALPHA");
        assertTrue(hm.knows("ALPHA"), "Should know learned concept");
        assertFalse(hm.knows("BETA"), "Should not know unlearned concept");
    }

    @Test
    void testHyperMemoryRemember() {
        HyperMemory hm = new HyperMemory();
        hm.remember("DOG", "LOYAL");
        assertEquals(1, hm.factCount(), "Should have 1 fact");
        assertEquals(2, hm.conceptCount(), "Should have 2 concepts (DOG + LOYAL)");
    }

    @Test
    void testHyperMemoryRecall() {
        HyperMemory hm = new HyperMemory();
        HyperVector v = hm.learn("UNIVERSE");
        String recalled = hm.recall(v);
        assertEquals("UNIVERSE", recalled, "Should recall exact concept from its own vector");
    }

    @Test
    void testHyperMemoryExportImport() {
        HyperMemory hm1 = new HyperMemory();
        hm1.remember("SUN", "HOT");
        HyperVector knowledge = hm1.exportKnowledge();
        assertNotNull(knowledge, "Exported knowledge should not be null");

        HyperMemory hm2 = new HyperMemory();
        hm2.importKnowledge(knowledge);
        // After import, global memory should be non-zero
        assertNotNull(hm2.getGlobalMemory(), "Imported memory should not be null");
    }

    // ═══════════════════════════════════════════════════
    // HYPERSYNAPSE
    // ═══════════════════════════════════════════════════

    @Test
    void testHyperSynapseInit() {
        HyperSynapse hs = new HyperSynapse();
        assertTrue(hs.getTotalNodes() >= 1, "Should have root node after init");
    }

    @Test
    void testHyperSynapseGrow() {
        HyperSynapse hs = new HyperSynapse();
        hs.grow("ORIGIN", "THESIS", "ANTITHESIS", "SYNTHESIS");
        assertTrue(hs.getTotalNodes() >= 4, "Should have at least 4 nodes after grow");
    }

    @Test
    void testHyperSynapseWormhole() {
        HyperSynapse hs = new HyperSynapse();
        hs.grow("ORIGIN", "A", "B", "C");
        hs.createWormhole("A", "C");
        assertTrue(hs.getTotalWormholes() >= 1, "Should have at least 1 wormhole");
    }

    @Test
    void testHyperSynapseGetNode() {
        HyperSynapse hs = new HyperSynapse();
        HyperSynapse.LogicNode root = hs.getNode("ORIGIN");
        assertNotNull(root, "Root node should exist");
        assertEquals("ORIGIN", root.id, "Root id should be ORIGIN");
    }

    // ═══════════════════════════════════════════════════
    // HARMONIC LANGUAGE
    // ═══════════════════════════════════════════════════

    @Test
    void testHarmonicLanguageVibration() {
        HarmonicLanguage hl = new HarmonicLanguage();
        double freq = hl.analyzeVibration("LOVE");
        assertTrue(freq > 0, "Vibration frequency should be positive");
    }

    @Test
    void testHarmonicLanguageHarmony() {
        HarmonicLanguage hl = new HarmonicLanguage();
        double harmony = hl.analyzeHarmony("PEACE");
        assertTrue(harmony >= 0 && harmony <= 1.0, "Harmony should be [0,1]");
    }

    @Test
    void testHarmonicLanguageTexture() {
        HarmonicLanguage hl = new HarmonicLanguage();
        String texture = hl.analyzeTexture("FLOW");
        assertNotNull(texture, "Texture should not be null");
    }

    @Test
    void testHarmonicLanguageResonance() {
        HarmonicLanguage hl = new HarmonicLanguage();
        double res = hl.matchResonance("LIGHT", "BRIGHT");
        assertTrue(res >= 0 && res <= 1.0, "Resonance should be [0,1]");
    }

    @Test
    void testHarmonicLanguageSelfResonance() {
        HarmonicLanguage hl = new HarmonicLanguage();
        double res = hl.matchResonance("SAME", "SAME");
        // Identical words: ratio=1.0, but φ-harmonic scoring gives (1.0 + harmonicScore)/2 ≈ 0.618
        assertTrue(res > 0.5, "Self-resonance should be positive, got " + res);
        // Same word should always score higher than random pair
        double randomRes = hl.matchResonance("SAME", "ZZZZQQQQ");
        assertTrue(res >= randomRes, "Self-resonance should be >= random pair resonance");
    }

    // ═══════════════════════════════════════════════════
    // TACHYON ROUTER
    // ═══════════════════════════════════════════════════

    @Test
    void testTachyonRouterInit() {
        TachyonRouter tr = new TachyonRouter();
        assertNotNull(tr, "TachyonRouter should initialize");
        tr.shutdown();
    }

    @Test
    void testTachyonRouterObserveAndHandle() {
        TachyonRouter tr = new TachyonRouter();
        tr.observeCurrentState("LOGIN");
        String result = tr.handleRequest("VIEW_DASHBOARD");
        assertNotNull(result, "Handle result should not be null");
        tr.shutdown();
    }
}
