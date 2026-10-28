package brain_architecture.tests;

import brain_architecture.brains.*;
import brain_architecture.core.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Test multimodal brain implementations
 */
public class MultimodalBrainTest {
    
    @Test
    public void testVisualBrain() throws Exception {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.VISUAL,
            60.0,
            0.25,
            30,
            0.4,
            0.4,
            0.2,
            Map.of("priority", "quality")
        );
        VisualBrain brain = new VisualBrain(config);
        
        assertEquals(BrainType.VISUAL, brain.getBrainType());
        assertNotNull(brain.getBackendEnsemble());
        
        brain.registerBackends();
        brain.shutdown();
    }
    
    @Test
    public void testAuditoryBrain() throws Exception {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.AUDITORY,
            60.0,
            0.2,
            30,
            0.5,
            0.4,
            0.1,
            Map.of("priority", "speed")
        );
        AuditoryBrain brain = new AuditoryBrain(config);
        
        assertEquals(BrainType.AUDITORY, brain.getBrainType());
        assertNotNull(brain.getBackendEnsemble());
        
        brain.registerBackends();
        brain.shutdown();
    }
    
    @Test
    public void testEvolutionaryBrain() throws Exception {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.EVOLUTIONARY,
            60.0,
            0.3,
            30,
            0.3,
            0.2,
            0.5,
            Map.of("priority", "evolution")
        );
        EvolutionaryBrain brain = new EvolutionaryBrain(config);
        
        assertEquals(BrainType.EVOLUTIONARY, brain.getBrainType());
        assertNotNull(brain.getBackendEnsemble());
        
        brain.registerBackends();
        brain.shutdown();
    }
    
    @Test
    public void testMemoryBrain() throws Exception {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.MEMORY,
            60.0,
            0.1,
            30,
            0.6,
            0.2,
            0.2,
            Map.of("priority", "accuracy")
        );
        MemoryBrain brain = new MemoryBrain(config);
        
        assertEquals(BrainType.MEMORY, brain.getBrainType());
        assertNotNull(brain.getBackendEnsemble());
        
        brain.registerBackends();
        brain.shutdown();
    }
    
    @Test
    public void testVisualBrainBackendEnsemble() throws Exception {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.VISUAL,
            60.0,
            0.25,
            30,
            0.4,
            0.4,
            0.2,
            Map.of("priority", "quality")
        );
        VisualBrain brain = new VisualBrain(config);
        
        brain.registerBackends();
        
        // Verify backend ensemble exists
        assertNotNull(brain.getBackendEnsemble());
        
        brain.shutdown();
    }
    
    @Test
    public void testAuditoryBrainBackendEnsemble() throws Exception {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.AUDITORY,
            60.0,
            0.2,
            30,
            0.5,
            0.4,
            0.1,
            Map.of("priority", "speed")
        );
        AuditoryBrain brain = new AuditoryBrain(config);
        
        brain.registerBackends();
        
        // Verify backend ensemble exists
        assertNotNull(brain.getBackendEnsemble());
        
        brain.shutdown();
    }
    
    @Test
    public void testEvolutionaryBrainBackendEnsemble() throws Exception {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.EVOLUTIONARY,
            60.0,
            0.3,
            30,
            0.3,
            0.2,
            0.5,
            Map.of("priority", "evolution")
        );
        EvolutionaryBrain brain = new EvolutionaryBrain(config);
        
        brain.registerBackends();
        
        // Verify backend ensemble exists
        assertNotNull(brain.getBackendEnsemble());
        
        brain.shutdown();
    }
    
    @Test
    public void testMemoryBrainBackendEnsemble() throws Exception {
        BrainConfiguration config = new BrainConfiguration(
            BrainType.MEMORY,
            60.0,
            0.1,
            30,
            0.6,
            0.2,
            0.2,
            Map.of("priority", "accuracy")
        );
        MemoryBrain brain = new MemoryBrain(config);
        
        brain.registerBackends();
        
        // Verify backend ensemble exists
        assertNotNull(brain.getBackendEnsemble());
        
        brain.shutdown();
    }
}
