package brain_architecture.tests;

import brain_architecture.core.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Basic test for brain architecture compilation and basic functionality
 */
public class BrainArchitectureTest {
    
    @Test
    public void testBrainTypeEnum() {
        assertEquals(12, BrainType.values().length);
        assertEquals("physical", BrainType.PHYSICAL.getId());
        assertEquals("quantum", BrainType.QUANTUM.getId());
    }
    
    @Test
    public void testBackendTypeEnum() {
        assertEquals(14, BackendInterface.BackendType.values().length);
        assertEquals("openclaw", BackendInterface.BackendType.OPENCLAW.getId());
        assertEquals("claude", BackendInterface.BackendType.CLAUDE.getId());
    }
    
    @Test
    public void testBrainConfiguration() {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.PHYSICAL,
            60.0,
            0.2,
            30,
            0.6,
            0.3,
            0.1,
            Map.of("custom", "value")
        );
        
        assertEquals(BrainType.PHYSICAL, config.getBrainType());
        assertEquals(60.0, config.getEvolutionCheckInterval());
        assertEquals(0.2, config.getDegradationThreshold());
    }
    
    @Test
    public void testBackendEnsemble() {
        BackendEnsemble ensemble = new BackendEnsemble();
        assertNotNull(ensemble);
        assertEquals(0, ensemble.getBackends().size());
        assertEquals(0, ensemble.getHealthyBackends().size());
    }
    
    @Test
    public void testPhiResonanceMatrix() {
        PhiResonanceMatrix matrix = new PhiResonanceMatrix();
        assertNotNull(matrix);
        
        // Test resonance pattern calculation
        double resonance1 = matrix.calculateResonancePattern(BrainType.PHYSICAL, BrainType.QUANTUM);
        double resonance2 = matrix.calculateResonancePattern(BrainType.PHYSICAL, BrainType.FRACTAL);
        
        // Verify method returns valid values
        assertTrue(resonance1 >= 0.0 && resonance1 <= 1.0);
        assertTrue(resonance2 >= 0.0 && resonance2 <= 1.0);
    }
}
