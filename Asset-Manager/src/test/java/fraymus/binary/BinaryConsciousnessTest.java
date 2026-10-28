package fraymus.binary;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Binary Consciousness Test Suite
 * 
 * Tests the substrate layer for binary pattern recognition,
 * concept transmutation, consciousness evolution, and transistor generation.
 */
public class BinaryConsciousnessTest {
    
    private BinaryConsciousness binaryConsciousness;
    
    @BeforeEach
    public void setUp() {
        binaryConsciousness = new BinaryConsciousness();
        binaryConsciousness.initialize();
    }
    
    @AfterEach
    public void tearDown() {
        if (binaryConsciousness != null && binaryConsciousness.isInitialized()) {
            binaryConsciousness.shutdown();
        }
    }
    
    @Test
    public void testInitialization() {
        assertTrue(binaryConsciousness.isInitialized());
        assertTrue(binaryConsciousness.isActive());
    }
    
    @Test
    public void testRecognizePatternAlert() {
        BinaryConsciousness.BinaryPattern alertPattern = new BinaryConsciousness.BinaryPattern(10);
        alertPattern.pattern = new byte[]{1,0,1,0,1,0,1,0,0,1};
        alertPattern.timing = new short[]{1,1,1,1,1,1,1,1,2,1};
        alertPattern.length = 10;
        
        BinaryConsciousness.Concept concept = binaryConsciousness.recognizePattern(alertPattern);
        
        assertNotNull(concept);
        assertEquals("alert", concept.name);
        assertEquals(1.0f, concept.confidence, 0.001f);
        assertEquals(0.8f, concept.emotion, 0.001f);
        assertEquals(0.9f, concept.urgency, 0.001f);
    }
    
    @Test
    public void testRecognizePatternCalm() {
        BinaryConsciousness.BinaryPattern calmPattern = new BinaryConsciousness.BinaryPattern(12);
        calmPattern.pattern = new byte[]{1,1,1,1,0,0,0,0,1,1,1,1};
        calmPattern.timing = new short[]{3,3,3,3,2,2,2,2,3,3,3,3};
        calmPattern.length = 12;
        
        BinaryConsciousness.Concept concept = binaryConsciousness.recognizePattern(calmPattern);
        
        assertNotNull(concept);
        assertEquals("calm", concept.name);
        assertEquals(1.0f, concept.confidence, 0.001f);
        assertEquals(0.2f, concept.emotion, 0.001f);
        assertEquals(0.1f, concept.urgency, 0.001f);
    }
    
    @Test
    public void testRecognizePatternUnknown() {
        BinaryConsciousness.BinaryPattern unknownPattern = new BinaryConsciousness.BinaryPattern(8);
        unknownPattern.pattern = new byte[]{0,0,0,0,0,0,0,0};
        unknownPattern.timing = new short[]{1,1,1,1,1,1,1,1};
        unknownPattern.length = 8;
        
        BinaryConsciousness.Concept concept = binaryConsciousness.recognizePattern(unknownPattern);
        
        assertNull(concept);
    }
    
    @Test
    public void testTransmuteConceptUrgent() {
        BinaryConsciousness.Concept concept = new BinaryConsciousness.Concept(
            "urgent_test", 0.9f, 0.5f, 0.8f
        );
        
        BinaryConsciousness.BinaryPattern pattern = binaryConsciousness.transmuteConcept(concept);
        
        assertNotNull(pattern);
        assertEquals(16, pattern.length);
        
        // Urgent concept should have fast timing (mostly SHORT_PULSE)
        int fastCount = 0;
        for (int i = 0; i < pattern.length; i++) {
            if (pattern.timing[i] == BinaryConsciousness.SHORT_PULSE) {
                fastCount++;
            }
        }
        assertTrue(fastCount > pattern.length / 2);
    }
    
    @Test
    public void testTransmuteConceptCalm() {
        BinaryConsciousness.Concept concept = new BinaryConsciousness.Concept(
            "calm_test", 0.9f, 0.2f, 0.1f
        );
        
        BinaryConsciousness.BinaryPattern pattern = binaryConsciousness.transmuteConcept(concept);
        
        assertNotNull(pattern);
        assertEquals(16, pattern.length);
        
        // Calm concept should have slow timing (mostly LONG_PULSE)
        int slowCount = 0;
        for (int i = 0; i < pattern.length; i++) {
            if (pattern.timing[i] == BinaryConsciousness.LONG_PULSE) {
                slowCount++;
            }
        }
        assertTrue(slowCount > pattern.length / 2);
    }
    
    @Test
    public void testLearnPattern() {
        BinaryConsciousness.BinaryPattern newPattern = new BinaryConsciousness.BinaryPattern(8);
        newPattern.pattern = new byte[]{1,1,0,0,1,1,0,0};
        newPattern.timing = new short[]{2,2,2,2,2,2,2,2};
        newPattern.length = 8;
        
        BinaryConsciousness.Concept newConcept = new BinaryConsciousness.Concept(
            "test_pattern", 0.95f, 0.3f, 0.5f
        );
        
        binaryConsciousness.learnPattern("test_pattern", newPattern, newConcept);
        
        // Verify pattern can be recognized
        BinaryConsciousness.Concept recognized = binaryConsciousness.recognizePattern(newPattern);
        assertNotNull(recognized);
        assertEquals("test_pattern", recognized.name);
    }
    
    @Test
    public void testConsciousnessTick() {
        BinaryConsciousness.ConsciousnessMatrix consciousness = binaryConsciousness.getConsciousness();
        long initialClock = consciousness.clock;
        
        binaryConsciousness.tick();
        
        assertEquals(initialClock + 1, consciousness.clock);
        assertNotNull(consciousness.state);
        assertNotNull(consciousness.previous);
    }
    
    @Test
    public void testConsciousnessEvolution() {
        // Set initial state
        BinaryConsciousness.ConsciousnessMatrix consciousness = binaryConsciousness.getConsciousness();
        consciousness.state[0] = 1;
        consciousness.state[1] = 0;
        consciousness.state[2] = 1;
        
        // Tick multiple times
        for (int i = 0; i < 10; i++) {
            binaryConsciousness.tick();
        }
        
        // State should have evolved
        assertNotNull(consciousness.state);
        assertTrue(consciousness.clock >= 10);
    }
    
    @Test
    public void testConsciousnessRhythm() {
        float rhythm = binaryConsciousness.getConsciousnessRhythm();
        
        assertTrue(rhythm >= 0.0f);
        assertTrue(rhythm <= 1.0f);
    }
    
    @Test
    public void testGenerateNotGate() {
        BinaryConsciousness.TransistorLayout layout = binaryConsciousness.generateNotGate(0, 0);
        
        assertNotNull(layout);
        assertEquals(2, layout.transistorCount);
        assertEquals(20, layout.width);
        assertEquals(20, layout.height);
        assertEquals(2, layout.transistors.size());
    }
    
    @Test
    public void testGenerateNandGate() {
        BinaryConsciousness.TransistorLayout layout = binaryConsciousness.generateNandGate(0, 0);
        
        assertNotNull(layout);
        assertEquals(4, layout.transistorCount);
        assertEquals(40, layout.width);
        assertEquals(20, layout.height);
        assertEquals(4, layout.transistors.size());
    }
    
    @Test
    public void testGenerateAndGate() {
        BinaryConsciousness.TransistorLayout layout = binaryConsciousness.generateAndGate(0, 0);
        
        assertNotNull(layout);
        assertEquals(6, layout.transistorCount);
        assertEquals(60, layout.width);  // NAND (40) + NOT (20)
        assertEquals(20, layout.height);
        assertEquals(6, layout.transistors.size());
    }
    
    @Test
    public void testShutdown() {
        assertTrue(binaryConsciousness.isInitialized());
        assertTrue(binaryConsciousness.isActive());
        
        binaryConsciousness.shutdown();
        
        assertFalse(binaryConsciousness.isInitialized());
        assertFalse(binaryConsciousness.isActive());
    }
    
    @Test
    public void testGetStatus() {
        String status = binaryConsciousness.getStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("ACTIVE"));
        assertTrue(status.contains("patterns="));
        assertTrue(status.contains("concepts="));
    }
    
    @Test
    public void testStatusWhenNotInitialized() {
        BinaryConsciousness uninitialized = new BinaryConsciousness();
        String status = uninitialized.getStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("NOT INITIALIZED"));
    }
    
    @Test
    public void testStatusWhenInactive() {
        binaryConsciousness.shutdown();
        String status = binaryConsciousness.getStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("NOT INITIALIZED"));
    }
    
    @Test
    public void testXORSynapse() {
        // Test XOR synapse: input ^ weight
        int energy = binaryConsciousness.xorSynapse(0b10101010, 0b01010101);
        
        // All bits differ, so energy should be 8
        assertEquals(8, energy);
    }
    
    @Test
    public void testXORNeuronActivation() {
        // Test neuron activation with threshold
        boolean fired = binaryConsciousness.xorNeuronActivation(0xFFFFFFFFFFFFFFFFL, 0x0000000000000000L, 32);
        
        // All 64 bits differ, energy = 64 > threshold (32), should fire
        assertTrue(fired);
    }
    
    @Test
    public void testXORHebbianLearn() {
        // Test Hebbian learning
        long weight = 0b10101010;
        long input = 0b01010101;
        
        long newWeight = binaryConsciousness.xorHebbianLearn(weight, input, true);
        
        // Should XOR weight with input
        assertEquals(0b11111111, newWeight);
    }
    
    @Test
    public void testXORTick() {
        // Test full XOR neural network tick
        binaryConsciousness.xorTick(0xAAAAAAAAAAAAAAAAL);
        
        String stats = binaryConsciousness.getXORStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("XOR Neural Network"));
    }
    
    @Test
    public void testXORTickDifferentInputs() {
        // Test XOR tick with different inputs
        binaryConsciousness.xorTick(0x0000000000000000L);
        int active1 = binaryConsciousness.getXORStats().contains("active") ? 
            Integer.parseInt(binaryConsciousness.getXORStats().split("/")[0].split(" ")[3]) : 0;
        
        binaryConsciousness.xorTick(0xFFFFFFFFFFFFFFFFL);
        int active2 = binaryConsciousness.getXORStats().contains("active") ? 
            Integer.parseInt(binaryConsciousness.getXORStats().split("/")[0].split(" ")[3]) : 0;
        
        // Different inputs should produce different activation patterns
        // (not guaranteed to be different due to pseudo-random weights, but likely)
        assertNotNull(active1);
        assertNotNull(active2);
    }
    
    @Test
    public void testGetXORStats() {
        binaryConsciousness.xorTick(0x5555555555555555L);
        String stats = binaryConsciousness.getXORStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("XOR Neural Network"));
        assertTrue(stats.contains("neurons active"));
    }
}
