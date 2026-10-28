package brain_architecture.integration;

import brain_architecture.core.BackendInterface.BackendType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Test suite for BackendIntegrator
 * Tests integration with existing Fraymus backends instead of duplicate implementations
 */
public class BackendIntegratorTest {
    
    private BackendIntegrator integrator;
    
    // Mock Fraymus components with actual methods for reflection
    private static class MockOllamaSpine {
        public String think(String prompt) {
            return "Mock Ollama response: " + prompt;
        }
    }
    
    private static class MockNeuralTentacle {
        public String think(String prompt) {
            return "Mock NeuralTentacle response: " + prompt;
        }
    }
    
    private static class MockClawConnector {
        public String dispatch(String task, String context) {
            return "Mock ClawConnector executed: " + task;
        }
        
        public String dispatch(String task) {
            return "Mock ClawConnector executed (simple): " + task;
        }
    }
    
    private static class MockVectorVault {
        // Placeholder for VectorVault
    }
    
    @BeforeEach
    public void setUp() {
        integrator = new BackendIntegrator();
    }
    
    @Test
    public void testInitialization() {
        assertFalse(integrator.isInitialized());
        assertNotNull(integrator.getStatus());
    }
    
    @Test
    public void testWireFraymusBackends() {
        // Mock Fraymus backend components with actual methods
        MockOllamaSpine ollamaSpine = new MockOllamaSpine();
        MockNeuralTentacle neuralTentacle = new MockNeuralTentacle();
        MockClawConnector clawConnector = new MockClawConnector();
        MockVectorVault vectorVault = new MockVectorVault();
        
        integrator.wireFraymusBackends(
            ollamaSpine, neuralTentacle, clawConnector, vectorVault);
        
        assertTrue(integrator.isInitialized());
    }
    
    @Test
    public void testUnwireFraymusBackends() {
        // Wire first
        integrator.wireFraymusBackends(
            new MockOllamaSpine(), new MockNeuralTentacle(), 
            new MockClawConnector(), new MockVectorVault());
        
        assertTrue(integrator.isInitialized());
        
        // Unwire
        integrator.unwireFraymusBackends();
        
        assertFalse(integrator.isInitialized());
    }
    
    @Test
    public void testGetFraymusBackend() {
        integrator.wireFraymusBackends(
            new MockOllamaSpine(), new MockNeuralTentacle(), 
            new MockClawConnector(), new MockVectorVault());
        
        // Test that backends can be retrieved
        assertTrue(integrator.getFraymusBackend(BackendType.OLLAMA).isPresent());
        assertTrue(integrator.getFraymusBackend(BackendType.CLAUDE).isPresent());
        assertTrue(integrator.getFraymusBackend(BackendType.GEMMA3).isPresent());
        assertTrue(integrator.getFraymusBackend(BackendType.OPENCLAW).isPresent());
        assertTrue(integrator.getFraymusBackend(BackendType.NEOCLAW).isPresent());
        assertTrue(integrator.getFraymusBackend(BackendType.OBSIDIAN).isPresent());
    }
    
    @Test
    public void testIsWiredToFraymus() {
        integrator.wireFraymusBackends(
            new MockOllamaSpine(), new MockNeuralTentacle(), 
            new MockClawConnector(), new MockVectorVault());
        
        assertTrue(integrator.isWiredToFraymus(BackendType.OLLAMA));
        assertTrue(integrator.isWiredToFraymus(BackendType.CLAUDE));
        assertTrue(integrator.isWiredToFraymus(BackendType.GEMMA3));
        assertTrue(integrator.isWiredToFraymus(BackendType.OPENCLAW));
        assertTrue(integrator.isWiredToFraymus(BackendType.NEOCLAW));
        assertTrue(integrator.isWiredToFraymus(BackendType.OBSIDIAN));
    }
    
    @Test
    public void testGetWiredBackendTypes() {
        integrator.wireFraymusBackends(
            new MockOllamaSpine(), new MockNeuralTentacle(), 
            new MockClawConnector(), new MockVectorVault());
        
        Set<BackendType> wired = integrator.getWiredBackendTypes();
        
        assertEquals(6, wired.size());
        assertTrue(wired.contains(BackendType.OLLAMA));
        assertTrue(wired.contains(BackendType.CLAUDE));
        assertTrue(wired.contains(BackendType.GEMMA3));
        assertTrue(wired.contains(BackendType.OPENCLAW));
        assertTrue(wired.contains(BackendType.NEOCLAW));
        assertTrue(wired.contains(BackendType.OBSIDIAN));
    }
    
    @Test
    public void testGetStatus() {
        // Status before wiring
        Map<String, Object> status = integrator.getStatus();
        assertFalse((Boolean) status.get("initialized"));
        
        // Status after wiring
        integrator.wireFraymusBackends(
            new MockOllamaSpine(), new MockNeuralTentacle(), 
            new MockClawConnector(), new MockVectorVault());
        
        status = integrator.getStatus();
        assertTrue((Boolean) status.get("initialized"));
        assertTrue((Integer) status.get("fraymusBackendsCount") > 0);
        assertNotNull(status.get("wiredBackendTypes"));
        assertNotNull(status.get("wiredStatus"));
    }
    
    @Test
    public void testExecuteQueryOllama() {
        integrator.wireFraymusBackends(
            new MockOllamaSpine(), new MockNeuralTentacle(), 
            new MockClawConnector(), new MockVectorVault());
        
        // Test query execution with real reflection calls
        CompletableFuture<String> result = integrator.executeQuery(
            BackendType.OLLAMA, "test query", new java.util.HashMap<>());
        
        assertNotNull(result);
        String response = result.join();
        assertTrue(response.contains("Mock Ollama response"));
    }
    
    @Test
    public void testExecuteQueryClaude() {
        integrator.wireFraymusBackends(
            new MockOllamaSpine(), new MockNeuralTentacle(), 
            new MockClawConnector(), new MockVectorVault());
        
        CompletableFuture<String> result = integrator.executeQuery(
            BackendType.CLAUDE, "test query", new java.util.HashMap<>());
        
        assertNotNull(result);
        String response = result.join();
        assertTrue(response.contains("Mock NeuralTentacle response"));
    }
    
    @Test
    public void testExecuteQueryOpenClaw() {
        integrator.wireFraymusBackends(
            new MockOllamaSpine(), new MockNeuralTentacle(), 
            new MockClawConnector(), new MockVectorVault());
        
        java.util.Map<String, Object> options = new java.util.HashMap<>();
        options.put("context", "test context");
        
        CompletableFuture<String> result = integrator.executeQuery(
            BackendType.OPENCLAW, "test task", options);
        
        assertNotNull(result);
        String response = result.join();
        assertTrue(response.contains("Mock ClawConnector executed"));
    }
    
    @Test
    public void testExecuteQueryNotWired() {
        // Try to execute query without wiring
        CompletableFuture<String> result = integrator.executeQuery(
            BackendType.OLLAMA, "test query", new java.util.HashMap<>());
        
        assertNotNull(result);
        
        // Should fail because backend is not wired
        CompletionException ex = assertThrows(CompletionException.class, () -> result.join());
        assertTrue(ex.getCause() instanceof IllegalStateException);
    }
}
