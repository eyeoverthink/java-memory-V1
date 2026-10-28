package brain_architecture.orchestration.discovery;

import brain_architecture.core.BackendInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Unit tests for BackendDiscovery
 */
public class BackendDiscoveryTest {
    
    private BackendDiscovery discovery;
    
    @BeforeEach
    public void setUp() {
        discovery = new BackendDiscovery();
    }
    
    @Test
    public void testInitialization() {
        Map<String, Object> stats = discovery.getDiscoveryStatistics();
        
        assertEquals(14L, stats.get("totalBackends")); // 14 backend types in brain_architecture
        assertTrue((Boolean) stats.get("autoDiscoveryEnabled"));
    }
    
    @Test
    public void testDiscoverBackends() {
        Map<BackendInterface.BackendType, BackendDiscovery.BackendStatus> results = 
            discovery.discoverBackends();
        
        assertEquals(14, results.size());
        
        // Local backends should be available
        assertTrue(results.get(BackendInterface.BackendType.OLLAMA) == BackendDiscovery.BackendStatus.AVAILABLE ||
                   results.get(BackendInterface.BackendType.OLLAMA) == BackendDiscovery.BackendStatus.REGISTERED);
    }
    
    @Test
    public void testRegisterBackend() {
        BackendInterface.BackendCapabilities capabilities = new BackendInterface.BackendCapabilities(
            true, true, true, true, true, 0.8, 0.9
        );
        
        boolean registered = discovery.registerBackend(BackendInterface.BackendType.OLLAMA, capabilities);
        
        assertTrue(registered);
        
        BackendDiscovery.BackendInfo info = discovery.getBackendInfo(BackendInterface.BackendType.OLLAMA);
        assertEquals(BackendDiscovery.BackendStatus.REGISTERED, info.getStatus());
        assertNotNull(info.getCapabilities());
    }
    
    @Test
    public void testUnregisterBackend() {
        discovery.registerBackend(BackendInterface.BackendType.OLLAMA, null);
        
        boolean unregistered = discovery.unregisterBackend(BackendInterface.BackendType.OLLAMA);
        
        assertTrue(unregistered);
        
        BackendDiscovery.BackendInfo info = discovery.getBackendInfo(BackendInterface.BackendType.OLLAMA);
        assertEquals(BackendDiscovery.BackendStatus.UNREGISTERED, info.getStatus());
    }
    
    @Test
    public void testGetAvailableBackends() {
        discovery.discoverBackends();
        
        List<BackendInterface.BackendType> available = discovery.getAvailableBackends();
        
        assertFalse(available.isEmpty());
        assertTrue(available.size() <= 10);
    }
    
    @Test
    public void testBackendAliases() {
        discovery.addAlias("ollama_local", BackendInterface.BackendType.OLLAMA);
        
        BackendInterface.BackendType resolved = discovery.resolveAlias("ollama_local");
        
        assertEquals(BackendInterface.BackendType.OLLAMA, resolved);
    }
    
    @Test
    public void testAliasCaseInsensitive() {
        discovery.addAlias("claude_remote", BackendInterface.BackendType.CLAUDE);
        
        BackendInterface.BackendType resolved1 = discovery.resolveAlias("claude_remote");
        BackendInterface.BackendType resolved2 = discovery.resolveAlias("CLAUDE_REMOTE");
        BackendInterface.BackendType resolved3 = discovery.resolveAlias("Claude_Remote");
        
        assertEquals(BackendInterface.BackendType.CLAUDE, resolved1);
        assertEquals(BackendInterface.BackendType.CLAUDE, resolved2);
        assertEquals(BackendInterface.BackendType.CLAUDE, resolved3);
    }
    
    @Test
    public void testAutoDiscoveryToggle() {
        assertTrue(discovery.isAutoDiscoveryEnabled());
        
        discovery.setAutoDiscoveryEnabled(false);
        assertFalse(discovery.isAutoDiscoveryEnabled());
        
        discovery.setAutoDiscoveryEnabled(true);
        assertTrue(discovery.isAutoDiscoveryEnabled());
    }
    
    @Test
    public void testReset() {
        discovery.registerBackend(BackendInterface.BackendType.OLLAMA, null);
        discovery.addAlias("test", BackendInterface.BackendType.CLAUDE);
        
        discovery.reset();
        
        Map<String, Object> stats = discovery.getDiscoveryStatistics();
        assertEquals(0, stats.get("aliases"));
    }
}
