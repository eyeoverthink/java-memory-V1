package brain_architecture.integration;

import brain_architecture.core.BrainType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Test suite for BrainTypeCoordinator
 * Tests coordination between 12 brain types and existing Fraymus components
 */
public class BrainTypeCoordinatorTest {
    
    private BrainTypeCoordinator coordinator;
    
    @BeforeEach
    public void setUp() {
        coordinator = new BrainTypeCoordinator();
    }
    
    @Test
    public void testInitialization() {
        assertFalse(coordinator.isInitialized());
        assertNotNull(coordinator.getStatus());
    }
    
    @Test
    public void testWireFraymusComponents() {
        // Mock Fraymus components
        Object ollamaSpine = new Object();
        Object hyperFormer = new Object();
        Object holoGraph = new Object();
        Object godMode = new Object();
        Object phiHarmonicKernel = new Object();
        Object fraynixOS = new Object();
        Object akashicRecord = new Object();
        
        coordinator.wireFraymusComponents(
            ollamaSpine, hyperFormer, holoGraph, godMode,
            phiHarmonicKernel, fraynixOS, akashicRecord);
        
        assertTrue(coordinator.isInitialized());
    }
    
    @Test
    public void testUnwireFraymusComponents() {
        // Wire first
        coordinator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        assertTrue(coordinator.isInitialized());
        
        // Unwire
        coordinator.unwireFraymusComponents();
        
        assertFalse(coordinator.isInitialized());
    }
    
    @Test
    public void testGetFraymusComponent() {
        coordinator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        // Test that components can be retrieved
        assertTrue(coordinator.getFraymusComponent(BrainType.PHYSICAL).isPresent());
        assertTrue(coordinator.getFraymusComponent(BrainType.QUANTUM).isPresent());
        assertTrue(coordinator.getFraymusComponent(BrainType.FRACTAL).isPresent());
        assertTrue(coordinator.getFraymusComponent(BrainType.CREATIVE).isPresent());
        assertTrue(coordinator.getFraymusComponent(BrainType.SPIRITUAL).isPresent());
        assertTrue(coordinator.getFraymusComponent(BrainType.TACHYONIC).isPresent());
        assertTrue(coordinator.getFraymusComponent(BrainType.VISUAL).isPresent());
        assertTrue(coordinator.getFraymusComponent(BrainType.AUDITORY).isPresent());
        assertTrue(coordinator.getFraymusComponent(BrainType.MEMORY).isPresent());
        
        // Evolutionary Brain is not wired (no Digital Genetics component)
        assertFalse(coordinator.getFraymusComponent(BrainType.EVOLUTIONARY).isPresent());
    }
    
    @Test
    public void testIsWiredToFraymus() {
        coordinator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        assertTrue(coordinator.isWiredToFraymus(BrainType.PHYSICAL));
        assertTrue(coordinator.isWiredToFraymus(BrainType.QUANTUM));
        assertFalse(coordinator.isWiredToFraymus(BrainType.LOGICAL)); // Not mapped to Fraymus
        assertFalse(coordinator.isWiredToFraymus(BrainType.EMOTIONAL)); // Not mapped to Fraymus
    }
    
    @Test
    public void testGetWiredBrainTypes() {
        coordinator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        Set<BrainType> wired = coordinator.getWiredBrainTypes();
        
        assertTrue(wired.contains(BrainType.PHYSICAL));
        assertTrue(wired.contains(BrainType.QUANTUM));
        assertTrue(wired.contains(BrainType.FRACTAL));
        assertTrue(wired.contains(BrainType.CREATIVE));
        assertTrue(wired.contains(BrainType.SPIRITUAL));
        assertTrue(wired.contains(BrainType.TACHYONIC));
        assertTrue(wired.contains(BrainType.VISUAL));
        assertTrue(wired.contains(BrainType.AUDITORY));
        assertTrue(wired.contains(BrainType.MEMORY));
        
        assertFalse(wired.contains(BrainType.LOGICAL));
        assertFalse(wired.contains(BrainType.EMOTIONAL));
        assertFalse(wired.contains(BrainType.EVOLUTIONARY));
    }
    
    @Test
    public void testGetStatus() {
        // Status before wiring
        Map<String, Object> status = coordinator.getStatus();
        assertFalse((Boolean) status.get("initialized"));
        
        // Status after wiring
        coordinator.wireFraymusComponents(
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object());
        
        status = coordinator.getStatus();
        assertTrue((Boolean) status.get("initialized"));
        assertTrue((Integer) status.get("fraymusComponentsCount") > 0);
        assertNotNull(status.get("wiredBrainTypes"));
        assertNotNull(status.get("wiredStatus"));
    }
    
    @Test
    public void testWireSentientCore() {
        coordinator.wireSentientCore(
            new Object(), new Object(), new Object(),
            new Object(), new Object());
        
        // Test that SentientCore components can be retrieved
        assertTrue(coordinator.getSentientCoreComponent(BrainType.LOGICAL).isPresent());
        assertTrue(coordinator.getSentientCoreComponent(BrainType.EMOTIONAL).isPresent());
    }
    
    @Test
    public void testIsWiredToSentientCore() {
        coordinator.wireSentientCore(
            new Object(), new Object(), new Object(),
            new Object(), new Object());
        
        assertTrue(coordinator.isWiredToSentientCore(BrainType.LOGICAL));
        assertTrue(coordinator.isWiredToSentientCore(BrainType.EMOTIONAL));
        assertFalse(coordinator.isWiredToSentientCore(BrainType.PHYSICAL));
    }
    
    @Test
    public void testUnwireSentientCore() {
        // Wire first
        coordinator.wireSentientCore(
            new Object(), new Object(), new Object(),
            new Object(), new Object());
        
        assertTrue(coordinator.isWiredToSentientCore(BrainType.LOGICAL));
        
        // Unwire
        coordinator.unwireSentientCore();
        
        assertFalse(coordinator.isWiredToSentientCore(BrainType.LOGICAL));
    }
}
