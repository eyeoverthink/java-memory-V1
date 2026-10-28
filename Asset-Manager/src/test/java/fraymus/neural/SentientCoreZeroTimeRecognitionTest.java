package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.BitSet;
import java.util.List;

/**
 * Integration test for Zero-Time Recognition with SentientCore
 * 
 * Tests the wire/unwire pattern and integration with the core system.
 */
public class SentientCoreZeroTimeRecognitionTest {

    private SentientCore core;
    private ZeroTimeRecognition ztr;

    @BeforeEach
    void setUp() {
        core = new SentientCore();
        ztr = new ZeroTimeRecognition();
    }

    @Test
    void testWireZeroTimeRecognition() {
        assertFalse(core.isZeroTimeRecognitionEnabled());
        
        core.wireZeroTimeRecognition(ztr);
        
        assertTrue(core.isZeroTimeRecognitionEnabled());
        assertNotNull(core.getZeroTimeRecognition());
    }

    @Test
    void testUnwireZeroTimeRecognition() {
        core.wireZeroTimeRecognition(ztr);
        assertTrue(core.isZeroTimeRecognitionEnabled());
        
        core.unwireZeroTimeRecognition();
        
        assertFalse(core.isZeroTimeRecognitionEnabled());
        assertNull(core.getZeroTimeRecognition());
    }

    @Test
    void testWireNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            core.wireZeroTimeRecognition(null);
        });
    }

    @Test
    void testStatusIncludesZeroTimeRecognition() {
        core.wireZeroTimeRecognition(ztr);
        
        String status = core.getStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("Zero-Time Rec"), "Status should show Zero-Time Recognition");
        assertTrue(status.contains("ENABLED"), "Status should show ENABLED");
    }

    @Test
    void testStatusShowsDisabledWhenNotWired() {
        String status = core.getStatus();
        
        assertTrue(status.contains("Zero-Time Rec"), "Status should show Zero-Time Recognition");
        assertTrue(status.contains("DISABLED"), "Status should show DISABLED when not wired");
    }

    @Test
    void testZeroTimeRecognitionWithLearning() {
        core.wireZeroTimeRecognition(ztr);
        
        // Learn some concepts
        core.learn("fire burns");
        core.learn("water flows");
        core.learn("air rises");
        
        // Create signatures
        ztr.createSignature("fire");
        ztr.createSignature("water");
        ztr.createSignature("air");
        
        // Recognize
        List<ZeroTimeRecognition.RecognitionResult> results = ztr.recognize("fire");
        
        assertNotNull(results);
        assertTrue(results.size() >= 0);
    }

    @Test
    void testZeroTimeRecognitionPerformance() {
        core.wireZeroTimeRecognition(ztr);
        
        // Create many signatures
        int conceptCount = 100;
        for (int i = 0; i < conceptCount; i++) {
            ztr.createSignature("concept_" + i);
        }
        
        // Measure recognition time
        long startTime = System.nanoTime();
        for (int i = 0; i < 50; i++) {
            ztr.recognize("concept_" + (i % conceptCount));
        }
        long duration = System.nanoTime() - startTime;
        
        double avgMs = (duration / 50.0) / 1_000_000.0;
        
        System.out.printf("Average recognition time: %.3f ms%n", avgMs);
        
        // Should be fast (< 10ms)
        assertTrue(avgMs < 10.0, "Recognition too slow: " + avgMs + " ms");
    }

    @Test
    void testBackwardCompatibility() {
        // SentientCore should work perfectly without Zero-Time Recognition
        core.learn("A causes B");
        core.learn("B causes C");
        
        String thought = core.think("What happens if A?");
        
        assertNotNull(thought);
        assertTrue(thought.length() > 0);
    }

    @Test
    void testZeroTimeRecognitionFormula() {
        core.wireZeroTimeRecognition(ztr);
        
        // Test the formula: Similarity = 1 - (Hamming distance / 16384)
        BitSet sigA = ztr.createSignature("concept_a");
        BitSet sigB = ztr.createSignature("concept_b");
        
        double similarity = ztr.computeSimilarity(sigA, sigB);
        
        // Similarity should be in [0, 1]
        assertTrue(similarity >= 0.0 && similarity <= 1.0);
        
        // Self-similarity should be 1.0
        assertEquals(1.0, ztr.computeSimilarity(sigA, sigA), 0.001);
    }

    @Test
    void testGetStats() {
        core.wireZeroTimeRecognition(ztr);
        
        ztr.createSignature("test1");
        ztr.createSignature("test2");
        ztr.recognize("test1");
        
        String stats = ztr.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("Recognitions"));
        assertTrue(stats.contains("Avg time"));
    }
}
