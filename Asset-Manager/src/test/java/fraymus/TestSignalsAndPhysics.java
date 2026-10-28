package fraymus;

import fraymus.signals.*;
import fraymus.physics.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Signal Processing and Physics systems wired into FraynixBoot.
 */
public class TestSignalsAndPhysics {

    // ═══════════════════════════════════════════════════
    // GLYPH CODER (Steganographic Encoding)
    // ═══════════════════════════════════════════════════

    @Test
    void testGlyphCoderRoundTrip() {
        GlyphCoder gc = new GlyphCoder();
        String emoji = "\uD83D\uDD12\uD83D\uDD11\uD83D\uDCE1";
        String secret = "QUANTUM_SECRET";
        String encoded = gc.injectData(emoji, secret);
        assertNotNull(encoded, "Encoded should not be null");
        String decoded = gc.extractData(encoded);
        assertEquals(secret, decoded, "Round-trip should preserve secret");
    }

    @Test
    void testGlyphCoderScrambled() {
        GlyphCoder gc = new GlyphCoder();
        String emoji = "\uD83D\uDE80";
        String secret = "SCRAMBLED_DATA";
        long seed = 42L;
        String encoded = gc.injectDataScrambled(emoji, secret, seed);
        String decoded = gc.extractDataScrambled(encoded, seed);
        assertEquals(secret, decoded, "Scrambled round-trip should preserve secret");
    }

    @Test
    void testGlyphCoderContainsPayload() {
        GlyphCoder gc = new GlyphCoder();
        String clean = "Hello World";
        assertFalse(gc.containsPayload(clean), "Clean text should not contain payload");

        String infected = gc.injectData("\uD83D\uDD11", "HIDDEN");
        assertTrue(gc.containsPayload(infected), "Infected text should contain payload");
    }

    @Test
    void testGlyphCoderSanitize() {
        GlyphCoder gc = new GlyphCoder();
        String infected = gc.injectData("\uD83D\uDD11", "SECRET");
        String sanitized = gc.sanitize(infected);
        assertFalse(gc.containsPayload(sanitized), "Sanitized text should not contain payload");
    }

    @Test
    void testGlyphCoderCreateParseCommand() {
        GlyphCoder gc = new GlyphCoder();
        String emoji = "\uD83D\uDD12";
        String encoded = gc.createCommand(emoji, "SCAN", "192.168.1.1", "ADMIN");
        GlyphCoder.FraymusCommand cmd = gc.parseCommand(encoded);
        assertNotNull(cmd, "Command should parse");
        assertEquals("SCAN", cmd.command, "Command should match");
        assertEquals("192.168.1.1", cmd.target, "Target should match");
        assertEquals("ADMIN", cmd.auth, "Auth should match");
    }

    @Test
    void testGlyphCoderStats() {
        GlyphCoder gc = new GlyphCoder();
        gc.injectData("\uD83D\uDD11", "A");
        String stats = gc.getStats();
        assertTrue(stats.contains("GlyphCoder"), "Stats should mention GlyphCoder");
    }

    // ═══════════════════════════════════════════════════
    // FREQUENCY COMM (EM Communication)
    // ═══════════════════════════════════════════════════

    @Test
    void testFrequencyCommTextToBinaryRoundTrip() {
        FrequencyComm fc = new FrequencyComm();
        String text = "HELLO";
        String binary = fc.textToBinary(text);
        assertNotNull(binary, "Binary should not be null");
        assertTrue(binary.matches("[01]+"), "Should only contain 0s and 1s");
        String decoded = fc.binaryToText(binary);
        assertEquals(text, decoded, "Round-trip should preserve text");
    }

    @Test
    void testFrequencyCommConstants() {
        assertEquals(432.0, FrequencyComm.FREQ_432_HZ, 0.001, "Base frequency should be 432 Hz");
        assertEquals(7.83, FrequencyComm.FREQ_SCHUMANN, 0.001, "Schumann should be 7.83 Hz");
    }

    @Test
    void testFrequencyCommDetectResonance() {
        FrequencyComm fc = new FrequencyComm();
        double sampleRate = 1000.0;
        double targetFreq = 100.0;
        double[] signal = fc.generateTestSignal(sampleRate, 0.1,
            new double[]{targetFreq}, new double[]{1.0});
        double magnitude = fc.detectResonance(signal, targetFreq, sampleRate);
        assertTrue(magnitude > 0, "Should detect target frequency");
    }

    @Test
    void testFrequencyCommFindDominant() {
        FrequencyComm fc = new FrequencyComm();
        double sampleRate = 1000.0;
        double targetFreq = 200.0;
        double[] signal = fc.generateTestSignal(sampleRate, 0.5,
            new double[]{targetFreq}, new double[]{1.0});
        double dominant = fc.findDominantFrequency(signal, sampleRate, 100.0, 400.0);
        assertTrue(Math.abs(dominant - targetFreq) < 10.0,
            "Dominant frequency should be near " + targetFreq + ", got " + dominant);
    }

    // ═══════════════════════════════════════════════════
    // PARADOX ENGINE
    // ═══════════════════════════════════════════════════

    @Test
    void testParadoxEngineRun() {
        ParadoxEngine pe = new ParadoxEngine();
        assertDoesNotThrow(() -> {
            pe.runTimeline();
            pe.runTimeline();
        }, "ParadoxEngine should run timelines without exception");
    }
}
