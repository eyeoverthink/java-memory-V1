package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Multi-Modal Capability with SentientCore
 * Phase 5: Embodiment & Multi-Modal
 * 
 * RULE SC-2: Maintains backward compatibility
 * RULE SC-3: Never break existing functionality
 */
public class SentientCoreMultiModalTest {
    
    private SentientCore core;
    private HDCSimilarityEngine hdcEngine;
    private SensorimotorEngine sensorimotorEngine;
    private VisionEngine visionEngine;
    private AudioEngine audioEngine;
    private CrossModalBindingEngine crossModalEngine;
    
    @BeforeEach
    void setUp() {
        core = new SentientCore();
        hdcEngine = new HDCSimilarityEngine();
        sensorimotorEngine = new SensorimotorEngine(core);
        visionEngine = new VisionEngine(core, hdcEngine);
        audioEngine = new AudioEngine(core, hdcEngine);
        crossModalEngine = new CrossModalBindingEngine(core, visionEngine, audioEngine);
    }
    
    @Test
    void testSentientCoreWorksWithoutMultiModal() {
        // RULE SC-2: Backward compatibility - works without multi-modal
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Should work normally
        String status = core.getStatus();
        assertNotNull(status);
    }
    
    @Test
    void testWireMultiModal() {
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        
        assertTrue(core.isMultiModalEnabled());
        assertEquals(sensorimotorEngine, core.getSensorimotorEngine());
        assertEquals(visionEngine, core.getVisionEngine());
        assertEquals(audioEngine, core.getAudioEngine());
        assertEquals(crossModalEngine, core.getCrossModalEngine());
        
        String status = core.getStatus();
        assertNotNull(status);
    }
    
    @Test
    void testWireMultiModalWithNull() {
        assertThrows(IllegalArgumentException.class, 
            () -> core.wireMultiModal(null, visionEngine, audioEngine, crossModalEngine));
        assertThrows(IllegalArgumentException.class, 
            () -> core.wireMultiModal(sensorimotorEngine, null, audioEngine, crossModalEngine));
        assertThrows(IllegalArgumentException.class, 
            () -> core.wireMultiModal(sensorimotorEngine, visionEngine, null, crossModalEngine));
        assertThrows(IllegalArgumentException.class, 
            () -> core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, null));
    }
    
    @Test
    void testUnwireMultiModal() {
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        assertTrue(core.isMultiModalEnabled());
        
        core.unwireMultiModal();
        assertFalse(core.isMultiModalEnabled());
        assertNull(core.getSensorimotorEngine());
        assertNull(core.getVisionEngine());
        assertNull(core.getAudioEngine());
        assertNull(core.getCrossModalEngine());
    }
    
    @Test
    void testLearnWithMultiModalEnabled() {
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        
        String result = core.learn("fire causes burns");
        
        assertNotNull(result);
        assertTrue(result.contains("Causal"));
        
        // Learn should still work normally
        assertEquals(1, core.getCausal().edgeCount());
    }
    
    @Test
    void testSensorimotorWithMultiModal() {
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        
        SensorimotorEngine.MotorCommand command = new SensorimotorEngine.MotorCommand(
            "cmd1", "move", "arm", 0.5);
        SensorimotorEngine.MotorResult result = sensorimotorEngine.processMotorCommand(command);
        
        assertTrue(result.success);
    }
    
    @Test
    void testVisionWithMultiModal() {
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        
        byte[] imageData = new byte[1000];
        VisionEngine.VisionResult result = visionEngine.processImage("image1", imageData);
        
        assertTrue(result.success);
    }
    
    @Test
    void testAudioWithMultiModal() {
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        
        byte[] audioData = new byte[1000];
        AudioEngine.AudioResult result = audioEngine.processAudio("audio1", audioData);
        
        assertTrue(result.success);
    }
    
    @Test
    void testCrossModalWithMultiModal() {
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        
        CrossModalBindingEngine.BindingResult result = crossModalEngine.bind("binding1", "visual1", "audio1");
        
        assertTrue(result.success);
    }
    
    @Test
    void testMultiModalStatusInStatus() {
        String status = core.getStatus();
        assertNotNull(status);
        
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        status = core.getStatus();
        assertNotNull(status);
    }
    
    @Test
    void testBackwardCompatibilityOfLearn() {
        // Test that learn() signature hasn't changed
        core.learn("test causes result");
        
        // Should still work exactly as before
        assertEquals(1, core.getCausal().edgeCount());
    }
    
    @Test
    void testBackwardCompatibilityOfThink() {
        // Test that think() signature hasn't changed
        core.learn("test causes result");
        String result = core.think("why result");
        
        // Should still work exactly as before
        assertNotNull(result);
    }
    
    @Test
    void testBackwardCompatibilityOfAccessors() {
        // Test that all accessors still work
        assertNotNull(core.getCausal());
        assertNotNull(core.getStats());
        assertNotNull(core.getLattice());
        assertNotNull(core.getAnalogy());
        assertNotNull(core.getFisher());
        assertNotNull(core.getSentience());
    }
    
    @Test
    void testMultiModalDoesNotModifyExistingStructures() {
        // RULE SC-1: Never modify existing data structures
        core.learn("fire causes burns");
        int edgeCountBefore = core.getCausal().edgeCount();
        
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        sensorimotorEngine.processMotorCommand(new SensorimotorEngine.MotorCommand("cmd1", "move", "arm", 0.5));
        visionEngine.processImage("image1", new byte[1000]);
        audioEngine.processAudio("audio1", new byte[1000]);
        
        // Causal graph should still work normally
        assertEquals(edgeCountBefore, core.getCausal().edgeCount());
    }
    
    @Test
    void testMultiModalWithAllOtherEnhancements() {
        // Test all enhancements together
        WorkingMemory wm = new WorkingMemory();
        AttentionMechanism att = new AttentionMechanism();
        EpisodicMemory em = new EpisodicMemory();
        HippocampalReplay hr = new HippocampalReplay(em);
        LLMReasoningEngine llmEngine = new LLMReasoningEngine(true, "qwen3-coder-next");
        MetaLearningEngine metaEngine = new MetaLearningEngine(llmEngine, core);
        GenerativeEngine genEngine = new GenerativeEngine(llmEngine, core, hdcEngine);
        
        core.wireHDC(hdcEngine);
        core.wireWorkingMemory(wm, att);
        core.wireEpisodicMemory(em, hr);
        core.wireLLMReasoning(llmEngine);
        core.wireMetaLearning(metaEngine);
        core.wireGenerative(genEngine);
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        
        core.learn("cats have fur");
        
        // All should be enabled
        assertTrue(core.isHDCEnabled());
        assertTrue(core.isWorkingMemoryEnabled());
        assertTrue(core.isEpisodicMemoryEnabled());
        assertTrue(core.isLLMReasoningEnabled());
        assertTrue(core.isMetaLearningEnabled());
        assertTrue(core.isGenerativeEnabled());
        assertTrue(core.isMultiModalEnabled());
        
        // All should have the observation
        assertTrue(hdcEngine.getMapper().getConceptCount() > 0);
        assertTrue(wm.size() > 0);
        assertTrue(em.size() > 0);
    }
    
    @Test
    void testMultiModalStatistics() {
        core.wireMultiModal(sensorimotorEngine, visionEngine, audioEngine, crossModalEngine);
        
        sensorimotorEngine.processMotorCommand(new SensorimotorEngine.MotorCommand("cmd1", "move", "arm", 0.5));
        visionEngine.processImage("image1", new byte[1000]);
        audioEngine.processAudio("audio1", new byte[1000]);
        
        String sensorimotorStats = sensorimotorEngine.getStats();
        String visionStats = visionEngine.getStats();
        String audioStats = audioEngine.getStats();
        
        assertNotNull(sensorimotorStats);
        assertNotNull(visionStats);
        assertNotNull(audioStats);
    }
}
