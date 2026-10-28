package brain_architecture.orchestration;

import brain_architecture.core.BackendEnsemble;
import brain_architecture.core.BackendInterface;
import brain_architecture.orchestration.discovery.BackendDiscovery;
import brain_architecture.orchestration.evolution.EvolutionEngine;
import brain_architecture.orchestration.healing.SelfHealing;
import brain_architecture.orchestration.organic.OrganicGrowth;
import brain_architecture.orchestration.synthesis.ResultSynthesizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Integration tests for OrchestratorBridge
 * Tests wire/unwire pattern for orchestration features
 */
public class OrchestratorBridgeTest {
    
    private BackendEnsemble backendEnsemble;
    private OrchestratorBridge bridge;
    
    @BeforeEach
    public void setUp() {
        backendEnsemble = new BackendEnsemble();
        bridge = new OrchestratorBridge(backendEnsemble);
    }
    
    @Test
    public void testInitialization() {
        assertNotNull(bridge);
        assertNotNull(bridge.getBackendEnsemble());
        assertEquals(backendEnsemble, bridge.getBackendEnsemble());
    }
    
    @Test
    public void testWireBackendDiscovery() {
        bridge.wireBackendDiscovery();
        
        Map<String, Object> status = bridge.getStatus();
        assertTrue((Boolean) status.get("discoveryWired"));
        assertNotNull(bridge.getBackendDiscovery());
    }
    
    @Test
    public void testUnwireBackendDiscovery() {
        bridge.wireBackendDiscovery();
        bridge.unwireBackendDiscovery();
        
        Map<String, Object> status = bridge.getStatus();
        assertFalse((Boolean) status.get("discoveryWired"));
        assertNull(bridge.getBackendDiscovery());
    }
    
    @Test
    public void testWireSelfHealing() {
        bridge.wireSelfHealing();
        
        Map<String, Object> status = bridge.getStatus();
        assertTrue((Boolean) status.get("healingWired"));
        assertNotNull(bridge.getSelfHealing());
    }
    
    @Test
    public void testUnwireSelfHealing() {
        bridge.wireSelfHealing();
        bridge.unwireSelfHealing();
        
        Map<String, Object> status = bridge.getStatus();
        assertFalse((Boolean) status.get("healingWired"));
        assertNull(bridge.getSelfHealing());
    }
    
    @Test
    public void testWireEvolutionEngine() {
        bridge.wireEvolutionEngine();
        
        Map<String, Object> status = bridge.getStatus();
        assertTrue((Boolean) status.get("evolutionWired"));
        assertNotNull(bridge.getEvolutionEngine());
    }
    
    @Test
    public void testUnwireEvolutionEngine() {
        bridge.wireEvolutionEngine();
        bridge.unwireEvolutionEngine();
        
        Map<String, Object> status = bridge.getStatus();
        assertFalse((Boolean) status.get("evolutionWired"));
        assertNull(bridge.getEvolutionEngine());
    }
    
    @Test
    public void testWireOrganicGrowth() {
        bridge.wireOrganicGrowth();
        
        Map<String, Object> status = bridge.getStatus();
        assertTrue((Boolean) status.get("organicWired"));
        assertNotNull(bridge.getOrganicGrowth());
    }
    
    @Test
    public void testUnwireOrganicGrowth() {
        bridge.wireOrganicGrowth();
        bridge.unwireOrganicGrowth();
        
        Map<String, Object> status = bridge.getStatus();
        assertFalse((Boolean) status.get("organicWired"));
        assertNull(bridge.getOrganicGrowth());
    }
    
    @Test
    public void testWireResultSynthesizer() {
        bridge.wireResultSynthesizer();
        
        Map<String, Object> status = bridge.getStatus();
        assertTrue((Boolean) status.get("synthesisWired"));
        assertNotNull(bridge.getResultSynthesizer());
    }
    
    @Test
    public void testUnwireResultSynthesizer() {
        bridge.wireResultSynthesizer();
        bridge.unwireResultSynthesizer();
        
        Map<String, Object> status = bridge.getStatus();
        assertFalse((Boolean) status.get("synthesisWired"));
        assertNull(bridge.getResultSynthesizer());
    }
    
    @Test
    public void testWireAll() {
        bridge.wireAll();
        
        Map<String, Object> status = bridge.getStatus();
        assertTrue((Boolean) status.get("discoveryWired"));
        assertTrue((Boolean) status.get("healingWired"));
        assertTrue((Boolean) status.get("evolutionWired"));
        assertTrue((Boolean) status.get("organicWired"));
        assertTrue((Boolean) status.get("synthesisWired"));
        assertTrue((Boolean) status.get("anyWired"));
    }
    
    @Test
    public void testUnwireAll() {
        bridge.wireAll();
        bridge.unwireAll();
        
        Map<String, Object> status = bridge.getStatus();
        assertFalse((Boolean) status.get("discoveryWired"));
        assertFalse((Boolean) status.get("healingWired"));
        assertFalse((Boolean) status.get("evolutionWired"));
        assertFalse((Boolean) status.get("organicWired"));
        assertFalse((Boolean) status.get("synthesisWired"));
        assertFalse((Boolean) status.get("anyWired"));
    }
    
    @Test
    public void testBackendEnsembleIntegration() {
        bridge.wireAll();
        
        // Set bridge on backend ensemble
        backendEnsemble.setOrchestratorBridge(bridge);
        
        // Verify bridge is set
        assertEquals(bridge, backendEnsemble.getOrchestratorBridge());
    }
    
    @Test
    public void testIdempotentWire() {
        bridge.wireBackendDiscovery();
        bridge.wireBackendDiscovery(); // Wire again
        
        Map<String, Object> status = bridge.getStatus();
        assertTrue((Boolean) status.get("discoveryWired"));
        assertNotNull(bridge.getBackendDiscovery());
    }
    
    @Test
    public void testIdempotentUnwire() {
        bridge.unwireBackendDiscovery(); // Unwire without wiring first
        
        Map<String, Object> status = bridge.getStatus();
        assertFalse((Boolean) status.get("discoveryWired"));
    }
}
