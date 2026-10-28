package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for VisionEngine
 * Phase 5: Embodiment & Multi-Modal
 */
public class VisionEngineTest {
    
    private SentientCore core;
    private HDCSimilarityEngine hdcEngine;
    private VisionEngine engine;
    
    @BeforeEach
    void setUp() {
        core = new SentientCore();
        hdcEngine = new HDCSimilarityEngine();
        engine = new VisionEngine(core, hdcEngine);
    }
    
    @Test
    void testConstructorWithNullCore() {
        assertThrows(IllegalArgumentException.class, () -> new VisionEngine(null, hdcEngine));
    }
    
    @Test
    void testConstructorWithValidCore() {
        assertNotNull(engine);
    }
    
    @Test
    void testProcessImage() {
        byte[] imageData = new byte[1000];
        for (int i = 0; i < imageData.length; i++) {
            imageData[i] = (byte) (i % 256);
        }
        
        VisionEngine.VisionResult result = engine.processImage("image1", imageData);
        
        assertTrue(result.success);
        assertEquals("Image processed", result.message);
        assertTrue(result.featureCount > 0);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testProcessImageWithNullImageId() {
        byte[] imageData = new byte[100];
        VisionEngine.VisionResult result = engine.processImage(null, imageData);
        
        assertFalse(result.success);
        assertEquals("Invalid input", result.message);
    }
    
    @Test
    void testProcessImageWithNullImageData() {
        VisionEngine.VisionResult result = engine.processImage("image1", null);
        
        assertFalse(result.success);
        assertEquals("Invalid input", result.message);
    }
    
    @Test
    void testRecognizeObjects() {
        List<String> objects = engine.recognizeObjects("image1");
        
        assertNotNull(objects);
        // Placeholder returns empty list
        assertEquals(0, objects.size());
    }
    
    @Test
    void testUnderstandScene() {
        String scene = engine.understandScene("image1");
        
        assertNotNull(scene);
        assertTrue(scene.contains("not yet implemented"));
    }
    
    @Test
    void testGetStats() {
        byte[] imageData = new byte[1000];
        engine.processImage("image1", imageData);
        engine.processImage("image2", imageData);
        
        String stats = engine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Vision Engine Statistics"));
        assertTrue(stats.contains("Images processed: 2"));
    }
    
    @Test
    void testVisualFeatureFields() {
        VisionEngine.VisualFeature feature = new VisionEngine.VisualFeature("feature1", 0.5);
        
        assertEquals("feature1", feature.id);
        assertEquals(0.5, feature.value);
    }
    
    @Test
    void testVisionResultFields() {
        VisionEngine.VisionResult result = new VisionEngine.VisionResult(true, "success", 10, 100);
        
        assertTrue(result.success);
        assertEquals("success", result.message);
        assertEquals(10, result.featureCount);
        assertEquals(100, result.elapsedTimeMs);
    }
    
    @Test
    void testProcessImageWithNullHDC() {
        VisionEngine engineNoHDC = new VisionEngine(core, null);
        byte[] imageData = new byte[1000];
        
        VisionEngine.VisionResult result = engineNoHDC.processImage("image1", imageData);
        
        assertTrue(result.success);
    }
}
