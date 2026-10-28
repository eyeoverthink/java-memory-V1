package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CrossModalBindingEngine
 * Phase 5: Embodiment & Multi-Modal
 */
public class CrossModalBindingEngineTest {
    
    private SentientCore core;
    private VisionEngine visionEngine;
    private AudioEngine audioEngine;
    private CrossModalBindingEngine engine;
    
    @BeforeEach
    void setUp() {
        core = new SentientCore();
        HDCSimilarityEngine hdcEngine = new HDCSimilarityEngine();
        visionEngine = new VisionEngine(core, hdcEngine);
        audioEngine = new AudioEngine(core, hdcEngine);
        engine = new CrossModalBindingEngine(core, visionEngine, audioEngine);
    }
    
    @Test
    void testConstructorWithNullCore() {
        assertThrows(IllegalArgumentException.class, 
            () -> new CrossModalBindingEngine(null, visionEngine, audioEngine));
    }
    
    @Test
    void testConstructorWithValidCore() {
        assertNotNull(engine);
    }
    
    @Test
    void testBind() {
        CrossModalBindingEngine.BindingResult result = engine.bind("binding1", "visual1", "audio1");
        
        assertTrue(result.success);
        assertEquals("Binding created", result.message);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testBindWithNullBindingId() {
        CrossModalBindingEngine.BindingResult result = engine.bind(null, "visual1", "audio1");
        
        assertFalse(result.success);
        assertEquals("Invalid inputs", result.message);
    }
    
    @Test
    void testBindWithNullVisualId() {
        CrossModalBindingEngine.BindingResult result = engine.bind("binding1", null, "audio1");
        
        assertFalse(result.success);
        assertEquals("Invalid inputs", result.message);
    }
    
    @Test
    void testBindWithNullAudioId() {
        CrossModalBindingEngine.BindingResult result = engine.bind("binding1", "visual1", null);
        
        assertFalse(result.success);
        assertEquals("Invalid inputs", result.message);
    }
    
    @Test
    void testSynchronize() {
        engine.bind("binding1", "visual1", "audio1");
        CrossModalBindingEngine.SyncResult result = engine.synchronize("binding1", System.currentTimeMillis());
        
        assertTrue(result.success);
        assertEquals("Synchronized", result.message);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testSynchronizeWithNullBindingId() {
        CrossModalBindingEngine.SyncResult result = engine.synchronize(null, 0);
        
        assertFalse(result.success);
        assertEquals("Invalid binding ID", result.message);
    }
    
    @Test
    void testSynchronizeWithNonExistentBinding() {
        CrossModalBindingEngine.SyncResult result = engine.synchronize("nonexistent", 0);
        
        assertFalse(result.success);
        assertEquals("Binding not found", result.message);
    }
    
    @Test
    void testFuse() {
        engine.bind("binding1", "visual1", "audio1");
        CrossModalBindingEngine.FusionResult result = engine.fuse("binding1");
        
        assertTrue(result.success);
        assertEquals("Features fused", result.message);
        assertTrue(result.fusedValue >= 0.0 && result.fusedValue <= 1.0);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testFuseWithNullBindingId() {
        CrossModalBindingEngine.FusionResult result = engine.fuse(null);
        
        assertFalse(result.success);
        assertEquals("Invalid binding ID", result.message);
    }
    
    @Test
    void testFuseWithNonExistentBinding() {
        CrossModalBindingEngine.FusionResult result = engine.fuse("nonexistent");
        
        assertFalse(result.success);
        assertEquals("Binding not found", result.message);
    }
    
    @Test
    void testAttend() {
        engine.bind("binding1", "visual1", "audio1");
        CrossModalBindingEngine.AttentionResult result = engine.attend("binding1");
        
        assertTrue(result.success);
        assertEquals("Attention applied", result.message);
        assertTrue(result.focusedFeatures >= 0);
        assertTrue(result.attentionScore >= 0.0 && result.attentionScore <= 1.0);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testAttendWithNullBindingId() {
        CrossModalBindingEngine.AttentionResult result = engine.attend(null);
        
        assertFalse(result.success);
        assertEquals("Invalid binding ID", result.message);
    }
    
    @Test
    void testAttendWithNonExistentBinding() {
        CrossModalBindingEngine.AttentionResult result = engine.attend("nonexistent");
        
        assertFalse(result.success);
        assertEquals("Binding not found", result.message);
    }
    
    @Test
    void testGetStats() {
        engine.bind("binding1", "visual1", "audio1");
        engine.synchronize("binding1", System.currentTimeMillis());
        engine.fuse("binding1");
        engine.attend("binding1");
        
        String stats = engine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Cross-Modal Binding Engine Statistics"));
        assertTrue(stats.contains("Bindings created: 1"));
        assertTrue(stats.contains("Synchronizations: 1"));
        assertTrue(stats.contains("Fusions: 1"));
    }
    
    @Test
    void testMultiModalBindingFields() {
        CrossModalBindingEngine.MultiModalBinding binding = 
            new CrossModalBindingEngine.MultiModalBinding("binding1", "visual1", "audio1");
        
        assertEquals("binding1", binding.id);
        assertEquals("visual1", binding.visualId);
        assertEquals("audio1", binding.audioId);
        assertTrue(binding.timestamp > 0);
    }
    
    @Test
    void testMultipleOperations() {
        for (int i = 0; i < 5; i++) {
            engine.bind("binding" + i, "visual" + i, "audio" + i);
            engine.fuse("binding" + i);
        }
        
        String stats = engine.getStats();
        assertTrue(stats.contains("Bindings created: 5"));
        assertTrue(stats.contains("Fusions: 5"));
    }
}
