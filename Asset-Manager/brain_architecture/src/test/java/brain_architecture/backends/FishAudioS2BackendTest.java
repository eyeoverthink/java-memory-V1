package brain_architecture.backends;

import brain_architecture.core.BackendInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Test Fish Audio S2 backend adapter
 */
public class FishAudioS2BackendTest {
    
    private FishAudioS2Backend backend;
    
    @BeforeEach
    public void setUp() throws Exception {
        backend = new FishAudioS2Backend();
        backend.initialize();
    }
    
    @AfterEach
    public void tearDown() {
        if (backend != null) {
            backend.shutdown();
        }
    }
    
    @Test
    public void testBackendType() {
        assertEquals(BackendInterface.BackendType.FISH_AUDIO_S2, backend.getBackendType());
    }
    
    @Test
    public void testCapabilities() {
        BackendInterface.BackendCapabilities caps = backend.getCapabilities();
        
        assertFalse(caps.supportsReasoning());
        assertTrue(caps.supportsGeneration());
        assertFalse(caps.supportsKnowledge());
        assertTrue(caps.supportsFastInference());
        assertFalse(caps.supportsComplexAnalysis());
        assertEquals(5000.0, caps.getMaxLatencyMs());
        assertEquals(0.8, caps.getMinConfidence());
    }
    
    @Test
    public void testInitialization() throws Exception {
        FishAudioS2Backend newBackend = new FishAudioS2Backend();
        assertFalse(newBackend.isHealthy());
        
        newBackend.initialize();
        assertTrue(newBackend.isHealthy());
        
        newBackend.shutdown();
        assertFalse(newBackend.isHealthy());
    }
    
    @Test
    public void testExecuteQueryWithMock() throws Exception {
        Map<String, Object> params = Map.of(
            "emotion", "happy",
            "voice_id", "default",
            "speed", 1.0
        );
        
        var future = backend.executeQuery("Hello world", BackendInterface.TaskType.GENERATION, params);
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getContent());
        assertTrue(result.getMetadata().containsKey("emotion"));
        assertEquals("happy", result.getMetadata().get("emotion"));
    }
    
    @Test
    public void testGenerateSpeechWithEmotion() throws Exception {
        var future = backend.generateSpeech("This is a test", FishAudioS2Backend.Emotion.HAPPY);
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertEquals("happy", result.getMetadata().get("emotion"));
    }
    
    @Test
    public void testGenerateSpeechWithFullControl() throws Exception {
        var future = backend.generateSpeech(
            "Full control test",
            FishAudioS2Backend.Emotion.SAD,
            "custom_voice",
            0.8
        );
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertEquals("sad", result.getMetadata().get("emotion"));
        assertEquals("custom_voice", result.getMetadata().get("voice_id"));
    }
    
    @Test
    public void testGetAvailableEmotions() {
        FishAudioS2Backend.Emotion[] emotions = FishAudioS2Backend.getAvailableEmotions();
        
        assertNotNull(emotions);
        assertTrue(emotions.length > 0);
        
        // Check for common emotions
        boolean hasHappy = false, hasSad = false, hasNeutral = false;
        for (FishAudioS2Backend.Emotion emotion : emotions) {
            if (emotion.getId().equals("happy")) hasHappy = true;
            if (emotion.getId().equals("sad")) hasSad = true;
            if (emotion.getId().equals("neutral")) hasNeutral = true;
        }
        
        assertTrue(hasHappy);
        assertTrue(hasSad);
        assertTrue(hasNeutral);
    }
    
    @Test
    public void testGetEmotion() {
        assertEquals(FishAudioS2Backend.Emotion.HAPPY, 
            FishAudioS2Backend.getEmotion("happy"));
        assertEquals(FishAudioS2Backend.Emotion.SAD, 
            FishAudioS2Backend.getEmotion("sad"));
        assertEquals(FishAudioS2Backend.Emotion.NEUTRAL, 
            FishAudioS2Backend.getEmotion("neutral"));
        assertEquals(FishAudioS2Backend.Emotion.NEUTRAL, 
            FishAudioS2Backend.getEmotion("invalid"));
    }
    
    @Test
    public void testEmotionDescriptions() {
        for (FishAudioS2Backend.Emotion emotion : FishAudioS2Backend.getAvailableEmotions()) {
            assertNotNull(emotion.getDescription());
            assertFalse(emotion.getDescription().isEmpty());
        }
    }
    
    @Test
    public void testQueryNotInitialized() {
        FishAudioS2Backend notInitialized = new FishAudioS2Backend();
        
        var future = notInitialized.executeQuery("test", BackendInterface.TaskType.GENERATION, Map.of());
        var result = future.join();
        
        assertFalse(result.isSuccess());
        assertEquals("Backend not initialized", result.getErrorMessage());
    }
    
    @Test
    public void testDefaultParameters() throws Exception {
        var future = backend.executeQuery("Test", BackendInterface.TaskType.GENERATION, Map.of());
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertEquals("neutral", result.getMetadata().get("emotion"));
        assertEquals("default", result.getMetadata().get("voice_id"));
    }
    
    @Test
    public void testLatencyTracking() throws Exception {
        var future = backend.executeQuery("Latency test", BackendInterface.TaskType.GENERATION, Map.of());
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getLatencyMs() > 0);
    }
    
    @Test
    public void testConfidenceScore() throws Exception {
        var future = backend.executeQuery("Confidence test", BackendInterface.TaskType.GENERATION, Map.of());
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getConfidence() >= 0.0);
        assertTrue(result.getConfidence() <= 1.0);
    }
}
