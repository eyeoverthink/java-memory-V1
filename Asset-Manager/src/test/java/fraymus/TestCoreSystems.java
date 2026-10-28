package fraymus;

import fraymus.core.*;
import fraymus.os.FrayFS;
import fraymus.knowledge.AkashicRecord;
import fraymus.symbolic.HoloGraph;
import fraymus.neural.AeonKronos;
import fraymus.hyper.HyperVector;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for core systems: GravityEngine, FusionReactor, SpatialRegistry,
 * FrayFS, AkashicRecord, HoloGraph, AeonKronos.
 */
public class TestCoreSystems {

    // ═══════════════════════════════════════════════════
    // GRAVITY ENGINE
    // ═══════════════════════════════════════════════════

    @Test
    void testGravityEngineSingleton() {
        GravityEngine g1 = GravityEngine.getInstance();
        GravityEngine g2 = GravityEngine.getInstance();
        assertSame(g1, g2, "GravityEngine should be singleton");
    }

    @Test
    void testGravityEngineStartStop() {
        GravityEngine g = GravityEngine.getInstance();
        g.start();
        assertTrue(g.isRunning(), "Should be running after start");
        g.stop();
        assertFalse(g.isRunning(), "Should not be running after stop");
    }

    @Test
    void testGravityEngineStatus() {
        GravityEngine g = GravityEngine.getInstance();
        String status = g.getStatus();
        assertNotNull(status, "Status should not be null");
    }

    // ═══════════════════════════════════════════════════
    // FUSION REACTOR
    // ═══════════════════════════════════════════════════

    @Test
    void testFusionReactorSingleton() {
        FusionReactor f1 = FusionReactor.getInstance();
        FusionReactor f2 = FusionReactor.getInstance();
        assertSame(f1, f2, "FusionReactor should be singleton");
    }

    @Test
    void testFusionReactorStartStop() {
        FusionReactor f = FusionReactor.getInstance();
        f.start();
        assertTrue(f.isActive(), "Should be active after start");
        f.stop();
        assertFalse(f.isActive(), "Should not be active after stop");
    }

    @Test
    void testFusionReactorStatus() {
        FusionReactor f = FusionReactor.getInstance();
        String status = f.getStatus();
        assertNotNull(status, "Status should not be null");
    }

    // ═══════════════════════════════════════════════════
    // SPATIAL REGISTRY
    // ═══════════════════════════════════════════════════

    @Test
    void testSpatialRegistryStats() {
        String stats = SpatialRegistry.getStats();
        assertNotNull(stats, "SpatialRegistry stats should not be null");
    }

    // ═══════════════════════════════════════════════════
    // FRAYFS
    // ═══════════════════════════════════════════════════

    @Test
    void testFrayFSCreate() {
        FrayFS fs = new FrayFS("TEST-VOL");
        assertNotNull(fs, "FrayFS should instantiate");
        assertEquals(0, fs.fileCount(), "New FS should have 0 files");
    }

    @Test
    void testFrayFSWriteRead() {
        FrayFS fs = new FrayFS("TEST-VOL");
        fs.write("test.txt", "Hello Fraymus");
        String content = fs.readString("test.txt");
        assertEquals("Hello Fraymus", content, "Read should return what was written");
        assertEquals(1, fs.fileCount(), "Should have 1 file");
    }

    @Test
    void testFrayFSOverwrite() {
        FrayFS fs = new FrayFS("TEST-VOL");
        fs.write("data.txt", "Version 1");
        fs.write("data.txt", "Version 2");
        assertEquals("Version 2", fs.readString("data.txt"), "Should return latest version");
        assertEquals(1, fs.fileCount(), "Overwrite should not increase file count");
    }

    @Test
    void testFrayFSDelete() {
        FrayFS fs = new FrayFS("TEST-VOL");
        fs.write("temp.txt", "temporary");
        assertTrue(fs.exists("temp.txt"), "File should exist");
        fs.delete("temp.txt");
        assertFalse(fs.exists("temp.txt"), "File should not exist after delete");
        assertEquals(0, fs.fileCount(), "Should have 0 files after delete");
    }

    @Test
    void testFrayFSList() {
        FrayFS fs = new FrayFS("TEST-VOL");
        fs.write("a.txt", "A");
        fs.write("b.txt", "B");
        fs.write("c.txt", "C");
        var files = fs.list();
        assertTrue(files.size() >= 3, "Should list at least 3 files");
    }

    @Test
    void testFrayFSListDirectory() {
        FrayFS fs = new FrayFS("TEST-VOL");
        fs.write("memories/thought1.txt", "Quantum entanglement is real");
        fs.write("memories/thought2.txt", "Phi resonance detected");
        var results = fs.list("memories");
        assertTrue(results.size() >= 2, "Should list at least 2 files in memories/");
    }

    @Test
    void testFrayFSStatus() {
        FrayFS fs = new FrayFS("TEST-VOL");
        String status = fs.status();
        assertNotNull(status, "Status should not be null");
    }

    // ═══════════════════════════════════════════════════
    // AKASHIC RECORD
    // ═══════════════════════════════════════════════════

    @Test
    void testAkashicRecordCreate() {
        AkashicRecord ar = new AkashicRecord();
        assertNotNull(ar, "AkashicRecord should instantiate");
    }

    @Test
    void testAkashicRecordAddBlock() {
        AkashicRecord ar = new AkashicRecord();
        String hash = ar.addBlock("PHYSICS", "Newton's laws govern motion");
        assertNotNull(hash, "Hash should not be null after addBlock");
        assertTrue(ar.getPersistedBlockCount() > 0, "Should have blocks after addBlock");
    }

    @Test
    void testAkashicRecordQuery() {
        AkashicRecord ar = new AkashicRecord();
        ar.addBlock("MATH", "Euler's formula connects e, i, pi");
        var results = ar.query("euler");
        assertNotNull(results, "Query results should not be null");
        assertTrue(results.size() >= 1, "Should find at least 1 result for 'euler'");
    }

    // ═══════════════════════════════════════════════════
    // HOLOGRAPH
    // ═══════════════════════════════════════════════════

    @Test
    void testHoloGraphCreate() {
        HoloGraph hg = new HoloGraph();
        assertNotNull(hg, "HoloGraph should instantiate");
    }

    @Test
    void testHoloGraphLearnAsk() {
        HoloGraph hg = new HoloGraph();
        hg.learn("PARIS", "CAPITAL_OF", "FRANCE");
        hg.learn("BERLIN", "CAPITAL_OF", "GERMANY");
        String result = hg.ask("PARIS", "CAPITAL_OF");
        assertNotNull(result, "Ask result should not be null");
        assertTrue(hg.getFactCount() >= 2, "Should have at least 2 facts");
        assertTrue(hg.getConceptCount() >= 4, "Should have at least 4 concepts");
    }

    // ═══════════════════════════════════════════════════
    // AEON KRONOS
    // ═══════════════════════════════════════════════════

    @Test
    void testAeonKronosSingleton() {
        AeonKronos k1 = AeonKronos.getInstance();
        AeonKronos k2 = AeonKronos.getInstance();
        assertSame(k1, k2, "AeonKronos should be singleton");
    }

    @Test
    void testAeonKronosBind() {
        AeonKronos k = AeonKronos.getInstance();
        int bindsBefore = k.getBindCount();
        k.bind("TEST_A", "TEST_B");
        assertTrue(k.getBindCount() > bindsBefore, "Bind count should increase");
    }

    @Test
    void testAeonKronosSequence() {
        AeonKronos k = AeonKronos.getInstance();
        int seqBefore = k.getSequenceCount();
        k.sequence(new String[]{"STEP1", "STEP2", "STEP3"});
        assertTrue(k.getSequenceCount() > seqBefore, "Sequence count should increase");
    }

    @Test
    void testAeonKronosQuery() {
        AeonKronos k = AeonKronos.getInstance();
        k.bind("SUN", "STAR");
        String result = k.query("SUN");
        assertNotNull(result, "Query should return something");
    }

    @Test
    void testAeonKronosAnalogy() {
        AeonKronos k = AeonKronos.getInstance();
        k.bind("KING", "ROYALTY");
        k.bind("QUEEN", "ROYALTY");
        k.bind("MAN", "MALE");
        k.bind("WOMAN", "FEMALE");
        int analogyBefore = k.getAnalogyCount();
        String result = k.analogy("KING", "MAN", "WOMAN");
        assertNotNull(result, "Analogy should return a result");
        assertTrue(k.getAnalogyCount() > analogyBefore, "Analogy count should increase");
    }

    @Test
    void testAeonKronosDimensions() {
        assertEquals(16384, AeonKronos.DIMS, "Kronos should be 16,384-D");
    }

    @Test
    void testAeonKronosConceptCount() {
        AeonKronos k = AeonKronos.getInstance();
        assertTrue(k.getConceptCount() > 0, "Should have concepts after binding");
    }

    @Test
    void testAeonKronosInjectVector() {
        AeonKronos k = AeonKronos.getInstance();
        long[] vector = new long[AeonKronos.DIMS / 64];
        vector[0] = 0xDEADBEEFL;
        k.injectVector("INJECTED_TEST", vector);
        assertNotNull(k.getConceptSpace().get("INJECTED_TEST"), "Injected vector should be in concept space");
    }
}
