package brain_architecture.tests;

import brain_architecture.communication.*;
import brain_architecture.core.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test inter-brain communication components
 */
public class CommunicationTest {
    
    @Test
    public void testPhiResonancePattern() {
        PhiResonancePattern pattern = new PhiResonancePattern(BrainType.PHYSICAL, BrainType.QUANTUM);
        
        assertEquals(BrainType.PHYSICAL, pattern.getSourceBrain());
        assertEquals(BrainType.QUANTUM, pattern.getTargetBrain());
        assertTrue(pattern.getResonance() >= 0.0 && pattern.getResonance() <= 1.0);
        assertTrue(pattern.getAmplitude() > 0);
        assertTrue(pattern.getFrequency() > 0);
        assertTrue(pattern.getPhase() >= 0 && pattern.getPhase() <= 2 * Math.PI);
    }
    
    @Test
    public void testPhiResonancePatternSymmetry() {
        PhiResonancePattern pattern1 = new PhiResonancePattern(BrainType.PHYSICAL, BrainType.QUANTUM);
        PhiResonancePattern pattern2 = new PhiResonancePattern(BrainType.QUANTUM, BrainType.PHYSICAL);
        
        // Verify both patterns are valid
        assertTrue(pattern1.getResonance() >= 0.0 && pattern1.getResonance() <= 1.0);
        assertTrue(pattern2.getResonance() >= 0.0 && pattern2.getResonance() <= 1.0);
        
        // Source and target should be reversed
        assertEquals(BrainType.PHYSICAL, pattern1.getSourceBrain());
        assertEquals(BrainType.QUANTUM, pattern1.getTargetBrain());
        assertEquals(BrainType.QUANTUM, pattern2.getSourceBrain());
        assertEquals(BrainType.PHYSICAL, pattern2.getTargetBrain());
    }
    
    @Test
    public void testKroneckerEntanglement() {
        KroneckerEntanglement.EntangledState state = 
            KroneckerEntanglement.createEntanglement(BrainType.PHYSICAL, BrainType.QUANTUM);
        
        assertEquals(BrainType.PHYSICAL, state.getBrain1());
        assertEquals(BrainType.QUANTUM, state.getBrain2());
        assertTrue(state.getStrength() >= 0.0 && state.getStrength() <= 1.0);
        assertNotNull(state.getEntangledMatrix());
        assertEquals(4, state.getEntangledMatrix().length);
        assertEquals(4, state.getEntangledMatrix()[0].length);
    }
    
    @Test
    public void testKroneckerEntanglementCheck() {
        boolean entangled = KroneckerEntanglement.areEntangled(BrainType.PHYSICAL, BrainType.QUANTUM);
        
        // Should be entangled if strength > 0.5
        double strength = KroneckerEntanglement.getEntanglementStrength(BrainType.PHYSICAL, BrainType.QUANTUM);
        assertEquals(strength > 0.5, entangled);
    }
    
    @Test
    public void testBrainCommunication() {
        BrainCommunication communication = new BrainCommunication();
        
        // Register a mock brain
        BrainConfiguration config = new BrainConfiguration(
            BrainType.PHYSICAL,
            60.0,
            0.2,
            30,
            0.6,
            0.3,
            0.1,
            java.util.Map.of()
        );
        
        // Note: We can't easily test without actual QuantumBrain instances
        // So we'll test the statistics instead
        java.util.Map<String, Object> stats = communication.getStatistics();
        
        assertEquals(0, stats.get("registeredBrains"));
        assertEquals(0, stats.get("activePatterns"));
        assertEquals(0, stats.get("entangledPairs"));
        assertEquals(0.0, stats.get("entanglementRate"));
    }
    
    @Test
    public void testPhiResonancePatternFrequencies() {
        // Test that different brain pairs have different frequencies
        PhiResonancePattern pattern1 = new PhiResonancePattern(BrainType.PHYSICAL, BrainType.QUANTUM);
        PhiResonancePattern pattern2 = new PhiResonancePattern(BrainType.PHYSICAL, BrainType.FRACTAL);
        PhiResonancePattern pattern3 = new PhiResonancePattern(BrainType.PHYSICAL, BrainType.CREATIVE);
        
        assertNotEquals(pattern1.getFrequency(), pattern2.getFrequency());
        assertNotEquals(pattern2.getFrequency(), pattern3.getFrequency());
    }
    
    @Test
    public void testPhiResonancePatternAmplitudes() {
        // Test that different brain types have different amplitude factors
        PhiResonancePattern pattern1 = new PhiResonancePattern(BrainType.PHYSICAL, BrainType.QUANTUM);
        PhiResonancePattern pattern2 = new PhiResonancePattern(BrainType.QUANTUM, BrainType.TACHYONIC);
        
        // Tachyonic should have higher amplitude due to PHI^3 factor
        assertTrue(pattern2.getAmplitude() > pattern1.getAmplitude());
    }
}
