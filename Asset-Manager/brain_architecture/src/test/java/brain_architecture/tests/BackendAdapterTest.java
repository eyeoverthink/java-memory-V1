package brain_architecture.tests;

import brain_architecture.backends.*;
import brain_architecture.core.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Test backend adapter implementations
 */
public class BackendAdapterTest {
    
    @Test
    public void testOpenClawBackend() throws Exception {
        OpenClawBackend backend = new OpenClawBackend();
        
        assertEquals(BackendInterface.BackendType.OPENCLAW, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsReasoning());
        assertTrue(backend.getCapabilities().supportsGeneration());
        assertTrue(backend.getCapabilities().supportsKnowledge());
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            backend.executeQuery("test query", BackendInterface.TaskType.REASONING, Map.of());
        
        BackendInterface.QueryResult result = future.join();
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("OPENCLAW"));
        
        backend.shutdown();
    }
    
    @Test
    public void testNeoClawBackend() throws Exception {
        NeoClawBackend backend = new NeoClawBackend();
        
        assertEquals(BackendInterface.BackendType.NEOCLAW, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsComplexAnalysis()); // Enhanced
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            backend.executeQuery("test query", BackendInterface.TaskType.COMPLEX_ANALYSIS, Map.of());
        
        BackendInterface.QueryResult result = future.join();
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("NEOCLAW"));
        
        backend.shutdown();
    }
    
    @Test
    public void testObsidianBackend() throws Exception {
        ObsidianBackend backend = new ObsidianBackend();
        
        assertEquals(BackendInterface.BackendType.OBSIDIAN, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsKnowledge()); // Excellent for knowledge
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            backend.executeQuery("test query", BackendInterface.TaskType.KNOWLEDGE, Map.of());
        
        BackendInterface.QueryResult result = future.join();
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("OBSIDIAN"));
        
        backend.shutdown();
    }
    
    @Test
    public void testOllamaBackend() throws Exception {
        // Ollama requires localhost:11434 to be running
        // If not available, test will fail gracefully
        OllamaBackend backend = new OllamaBackend();
        
        assertEquals(BackendInterface.BackendType.OLLAMA, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsFastInference()); // Local = fast
        
        try {
            backend.initialize();
            assertTrue(backend.isHealthy());
            
            CompletableFuture<BackendInterface.QueryResult> future = 
                backend.executeQuery("test query", BackendInterface.TaskType.FAST_INFERENCE, Map.of());
            
            BackendInterface.QueryResult result = future.join();
            // If Ollama is running, check response
            if (result.isSuccess()) {
                assertTrue(result.getMetadata().containsKey("local"));
                assertEquals(true, result.getMetadata().get("local"));
            }
            
            backend.shutdown();
        } catch (Exception e) {
            // Ollama not running - skip test
            System.out.println("Ollama not running, skipping test: " + e.getMessage());
        }
    }
    
    @Test
    public void testGemma3Backend() throws Exception {
        // Gemma3 requires GOOGLE_API_KEY
        if (System.getenv("GOOGLE_API_KEY") == null) {
            System.out.println("GOOGLE_API_KEY not set, skipping Gemma3 test");
            return;
        }
        
        Gemma3Backend backend = new Gemma3Backend();
        
        assertEquals(BackendInterface.BackendType.GEMMA3, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsGeneration()); // Excellent for generation
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            backend.executeQuery("test query", BackendInterface.TaskType.GENERATION, Map.of());
        
        BackendInterface.QueryResult result = future.join();
        assertTrue(result.isSuccess());
        assertEquals("google", result.getMetadata().get("provider"));
        
        backend.shutdown();
    }
    
    @Test
    public void testClaudeBackend() throws Exception {
        // Claude requires ANTHROPIC_API_KEY
        if (System.getenv("ANTHROPIC_API_KEY") == null) {
            System.out.println("ANTHROPIC_API_KEY not set, skipping Claude test");
            return;
        }
        
        ClaudeBackend backend = new ClaudeBackend();
        
        assertEquals(BackendInterface.BackendType.CLAUDE, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsReasoning()); // Excellent for reasoning
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            backend.executeQuery("test query", BackendInterface.TaskType.REASONING, Map.of());
        
        BackendInterface.QueryResult result = future.join();
        assertTrue(result.isSuccess());
        assertEquals("anthropic", result.getMetadata().get("provider"));
        
        backend.shutdown();
    }
    
    @Test
    public void testLTXVideoBackend() throws Exception {
        // LTX-Video requires Python installation
        LTXVideoBackend backend = new LTXVideoBackend();
        
        assertEquals(BackendInterface.BackendType.LTXVIDEO, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsGeneration()); // Excellent for video generation
        assertFalse(backend.getCapabilities().supportsFastInference()); // Video generation is compute-intensive
        
        try {
            backend.initialize();
            assertTrue(backend.isHealthy());
            
            Map<String, Object> params = Map.of(
                "height", 704,
                "width", 1216,
                "num_frames", 121,
                "output_path", "test_output.mp4"
            );
            
            CompletableFuture<BackendInterface.QueryResult> future = 
                backend.executeQuery("A person walking through a forest", BackendInterface.TaskType.GENERATION, params);
            
            BackendInterface.QueryResult result = future.join();
            assertTrue(result.isSuccess());
            assertTrue(result.getContent().contains("LTX-VIDEO") || result.getMetadata().containsKey("mode"));
            
            backend.shutdown();
        } catch (Exception e) {
            // LTX-Video not available - test mock mode
            System.out.println("LTX-Video not available, testing mock mode: " + e.getMessage());
            
            // Re-initialize for mock mode test
            backend.initialize();
            CompletableFuture<BackendInterface.QueryResult> future = 
                backend.executeQuery("test query", BackendInterface.TaskType.GENERATION, Map.of());
            
            BackendInterface.QueryResult result = future.join();
            assertTrue(result.isSuccess());
            assertTrue(result.getMetadata().containsKey("mode"));
            assertEquals("mock", result.getMetadata().get("mode"));
            
            backend.shutdown();
        }
    }
    
    @Test
    public void testSpeechBrainBackend() throws Exception {
        // SpeechBrain requires Python installation
        SpeechBrainBackend backend = new SpeechBrainBackend();
        
        assertEquals(BackendInterface.BackendType.SPEECHBRAIN, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsFastInference()); // Speech processing can be fast
        assertTrue(backend.getCapabilities().supportsKnowledge()); // Speech content understanding
        
        try {
            backend.initialize();
            assertTrue(backend.isHealthy());
            
            Map<String, Object> params = Map.of(
                "task", "asr",
                "model", "speechbrain/asr-conformer-transformerlm-librispeech"
            );
            
            CompletableFuture<BackendInterface.QueryResult> future = 
                backend.executeQuery("test", BackendInterface.TaskType.FAST_INFERENCE, params);
            
            BackendInterface.QueryResult result = future.join();
            assertTrue(result.isSuccess());
            
            backend.shutdown();
        } catch (Exception e) {
            // SpeechBrain not available - test mock mode
            System.out.println("SpeechBrain not available, testing mock mode: " + e.getMessage());
            
            backend.initialize();
            CompletableFuture<BackendInterface.QueryResult> future = 
                backend.executeQuery("test query", BackendInterface.TaskType.FAST_INFERENCE, Map.of());
            
            BackendInterface.QueryResult result = future.join();
            assertTrue(result.isSuccess());
            assertTrue(result.getMetadata().containsKey("mode"));
            assertEquals("mock", result.getMetadata().get("mode"));
            
            backend.shutdown();
        }
    }
    
    @Test
    public void testLazarusBackend() throws Exception {
        // Lazarus requires Java installation and JAR build
        LazarusBackend backend = new LazarusBackend();
        
        assertEquals(BackendInterface.BackendType.LAZARUS, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsComplexAnalysis()); // Evolution is complex
        assertFalse(backend.getCapabilities().supportsFastInference()); // Evolution takes time
        
        try {
            backend.initialize();
            assertTrue(backend.isHealthy());
            
            Map<String, Object> params = Map.of(
                "task", "evolve",
                "generations", 10
            );
            
            CompletableFuture<BackendInterface.QueryResult> future = 
                backend.executeQuery("Optimize pattern recognition", BackendInterface.TaskType.COMPLEX_ANALYSIS, params);
            
            BackendInterface.QueryResult result = future.join();
            assertTrue(result.isSuccess());
            
            backend.shutdown();
        } catch (Exception e) {
            // Lazarus not available - test mock mode
            System.out.println("Lazarus not available, testing mock mode: " + e.getMessage());
            
            backend.initialize();
            CompletableFuture<BackendInterface.QueryResult> future = 
                backend.executeQuery("test query", BackendInterface.TaskType.COMPLEX_ANALYSIS, Map.of());
            
            BackendInterface.QueryResult result = future.join();
            assertTrue(result.isSuccess());
            assertTrue(result.getMetadata().containsKey("mode"));
            assertEquals("mock", result.getMetadata().get("mode"));
            
            backend.shutdown();
        }
    }
    
    @Test
    public void testAkashicRecordBackend() throws Exception {
        // AkashicRecord requires file system access
        AkashicRecordBackend backend = new AkashicRecordBackend();
        
        assertEquals(BackendInterface.BackendType.AKASHICRECORD, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsKnowledge()); // Excellent for knowledge
        assertTrue(backend.getCapabilities().supportsFastInference()); // Memory lookup is fast
        assertFalse(backend.getCapabilities().supportsGeneration()); // Memory is read-only
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        // Test store operation
        Map<String, Object> storeParams = Map.of(
            "operation", "store",
            "modality", "visual"
        );
        
        CompletableFuture<BackendInterface.QueryResult> storeFuture = 
            backend.executeQuery("Test memory content", BackendInterface.TaskType.KNOWLEDGE, storeParams);
        
        BackendInterface.QueryResult storeResult = storeFuture.join();
        assertTrue(storeResult.isSuccess());
        assertTrue(storeResult.getContent().contains("stored") || storeResult.getMetadata().containsKey("recordId"));
        
        // Test retrieve operation
        Map<String, Object> retrieveParams = Map.of(
            "operation", "retrieve",
            "modality", "visual"
        );
        
        CompletableFuture<BackendInterface.QueryResult> retrieveFuture = 
            backend.executeQuery("Test", BackendInterface.TaskType.KNOWLEDGE, retrieveParams);
        
        BackendInterface.QueryResult retrieveResult = retrieveFuture.join();
        assertTrue(retrieveResult.isSuccess());
        
        backend.shutdown();
    }
    
    @Test
    public void testDeepSeekBackend() throws Exception {
        // DeepSeek requires DEEPSEEK_API_KEY
        DeepSeekBackend backend = new DeepSeekBackend();
        
        assertEquals(BackendInterface.BackendType.DEEPSEEK, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsReasoning()); // Excellent for math reasoning
        assertTrue(backend.getCapabilities().supportsComplexAnalysis()); // 96.0% GSM8K
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            backend.executeQuery("Solve: 2 + 2 = ?", BackendInterface.TaskType.REASONING, Map.of());
        
        BackendInterface.QueryResult result = future.join();
        assertTrue(result.isSuccess());
        assertEquals("deepseek", result.getMetadata().get("provider"));
        
        backend.shutdown();
    }
    
    @Test
    public void testKimiBackend() throws Exception {
        // Kimi requires KIMI_API_KEY
        KimiBackend backend = new KimiBackend();
        
        assertEquals(BackendInterface.BackendType.KIMI, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsReasoning()); // Dual-mode support
        assertTrue(backend.getCapabilities().supportsFastInference()); // Instant mode
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        // Test Instant mode (System 1)
        Map<String, Object> instantParams = Map.of("reasoning_mode", "instant");
        CompletableFuture<BackendInterface.QueryResult> instantFuture = 
            backend.executeQuery("Quick response", BackendInterface.TaskType.FAST_INFERENCE, instantParams);
        
        BackendInterface.QueryResult instantResult = instantFuture.join();
        assertTrue(instantResult.isSuccess());
        assertEquals("instant", instantResult.getMetadata().get("reasoning_mode"));
        
        // Test Thinking mode (System 2)
        Map<String, Object> thinkingParams = Map.of("reasoning_mode", "thinking");
        CompletableFuture<BackendInterface.QueryResult> thinkingFuture = 
            backend.executeQuery("Analyze step by step", BackendInterface.TaskType.COMPLEX_ANALYSIS, thinkingParams);
        
        BackendInterface.QueryResult thinkingResult = thinkingFuture.join();
        assertTrue(thinkingResult.isSuccess());
        assertEquals("thinking", thinkingResult.getMetadata().get("reasoning_mode"));
        
        backend.shutdown();
    }
    
    @Test
    public void testGemma4Backend() throws Exception {
        // Gemma4 requires GOOGLE_API_KEY
        Gemma4Backend backend = new Gemma4Backend();
        
        assertEquals(BackendInterface.BackendType.GEMMA4, backend.getBackendType());
        assertTrue(backend.getCapabilities().supportsReasoning()); // Top-tier reasoning
        assertTrue(backend.getCapabilities().supportsGeneration()); // Excellent generation
        
        backend.initialize();
        assertTrue(backend.isHealthy());
        
        CompletableFuture<BackendInterface.QueryResult> future = 
            backend.executeQuery("Generate a creative story", BackendInterface.TaskType.GENERATION, Map.of());
        
        BackendInterface.QueryResult result = future.join();
        assertTrue(result.isSuccess());
        assertEquals("google", result.getMetadata().get("provider"));
        
        backend.shutdown();
    }
}
