package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AudioEngine
 * Phase 5: Embodiment & Multi-Modal
 */
public class AudioEngineTest {
    
    private SentientCore core;
    private HDCSimilarityEngine hdcEngine;
    private AudioEngine engine;
    
    @BeforeEach
    void setUp() {
        core = new SentientCore();
        hdcEngine = new HDCSimilarityEngine();
        engine = new AudioEngine(core, hdcEngine);
    }
    
    @Test
    void testConstructorWithNullCore() {
        assertThrows(IllegalArgumentException.class, () -> new AudioEngine(null, hdcEngine));
    }
    
    @Test
    void testConstructorWithValidCore() {
        assertNotNull(engine);
    }
    
    @Test
    void testProcessAudio() {
        byte[] audioData = new byte[1000];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = (byte) (i % 256);
        }
        
        AudioEngine.AudioResult result = engine.processAudio("audio1", audioData);
        
        assertTrue(result.success);
        assertEquals("Audio processed", result.message);
        assertTrue(result.featureCount > 0);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testProcessAudioWithNullAudioId() {
        byte[] audioData = new byte[100];
        AudioEngine.AudioResult result = engine.processAudio(null, audioData);
        
        assertFalse(result.success);
        assertEquals("Invalid input", result.message);
    }
    
    @Test
    void testProcessAudioWithNullAudioData() {
        AudioEngine.AudioResult result = engine.processAudio("audio1", null);
        
        assertFalse(result.success);
        assertEquals("Invalid input", result.message);
    }
    
    @Test
    void testRecognizeSpeech() {
        // Test with non-existent file - should return error message
        String speech = engine.recognizeSpeech("nonexistent.wav");
        
        assertNotNull(speech);
        assertTrue(speech.toLowerCase().contains("failed"));
        assertTrue(speech.toLowerCase().contains("audio file not found"));
    }
    
    @Test
    void testRecognizeSpeechById() {
        String speech = engine.recognizeSpeechById("audio1");
        
        assertNotNull(speech);
        assertTrue(speech.contains("not yet implemented"));
    }
    
    @Test
    void testClassifySound() {
        // Test with non-existent file - should return error message
        String classification = engine.classifySound("nonexistent.wav");
        
        assertNotNull(classification);
        assertTrue(classification.toLowerCase().contains("failed"));
        assertTrue(classification.toLowerCase().contains("audio file not found"));
    }
    
    @Test
    void testClassifySoundById() {
        String classification = engine.classifySoundById("audio1");
        
        assertNotNull(classification);
        assertTrue(classification.contains("not yet implemented"));
    }
    
    @Test
    void testGetStats() {
        byte[] audioData = new byte[1000];
        engine.processAudio("audio1", audioData);
        engine.processAudio("audio2", audioData);
        
        String stats = engine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Audio Engine Statistics"));
        assertTrue(stats.contains("Audio segments processed: 2"));
    }
    
    @Test
    void testAudioFeatureFields() {
        AudioEngine.AudioFeature feature = new AudioEngine.AudioFeature("feature1", 0.5);
        
        assertEquals("feature1", feature.id);
        assertEquals(0.5, feature.value);
    }
    
    @Test
    void testAudioResultFields() {
        AudioEngine.AudioResult result = new AudioEngine.AudioResult(true, "success", 10, 100);
        
        assertTrue(result.success);
        assertEquals("success", result.message);
        assertEquals(10, result.featureCount);
        assertEquals(100, result.elapsedTimeMs);
    }
    
    @Test
    void testProcessAudioWithNullHDC() {
        AudioEngine engineNoHDC = new AudioEngine(core, null);
        byte[] audioData = new byte[1000];
        
        AudioEngine.AudioResult result = engineNoHDC.processAudio("audio1", audioData);
        
        assertTrue(result.success);
    }
}
