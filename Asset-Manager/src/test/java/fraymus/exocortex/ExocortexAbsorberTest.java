package fraymus.exocortex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ExocortexAbsorber
 */
public class ExocortexAbsorberTest {
    
    private ExocortexAbsorber absorber;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUp() {
        absorber = new ExocortexAbsorber(tempDir.toString());
    }
    
    @Test
    public void testInitialization() {
        assertNotNull(absorber);
        assertNotNull(ExocortexAbsorber.getRepositories());
        assertFalse(ExocortexAbsorber.getRepositories().isEmpty());
    }
    
    @Test
    public void testRepositoryConfigurations() {
        Map<String, ExocortexAbsorber.RepositoryConfig> repos = ExocortexAbsorber.getRepositories();
        
        // Test Blood-Brain Barrier repositories
        assertTrue(repos.containsKey("firecracker"));
        assertTrue(repos.containsKey("gvisor"));
        
        // Test Vocal Cords repositories
        assertTrue(repos.containsKey("piper"));
        assertTrue(repos.containsKey("tts"));
        
        // Test Synaptic Web repository
        assertTrue(repos.containsKey("memgraph"));
        
        // Test Foraging Engine repositories
        assertTrue(repos.containsKey("searxng"));
        assertTrue(repos.containsKey("crawl4ai"));
        
        // Test Digital Nervous System repositories
        assertTrue(repos.containsKey("open-interpreter"));
        assertTrue(repos.containsKey("playwright"));
        
        // Test Subconscious repositories
        assertTrue(repos.containsKey("celery"));
        assertTrue(repos.containsKey("temporal"));
    }
    
    @Test
    public void testFirecrackerConfiguration() {
        ExocortexAbsorber.RepositoryConfig config = ExocortexAbsorber.getRepository("firecracker");
        
        assertNotNull(config);
        assertEquals("firecracker-microvm/firecracker", config.githubPath);
        assertEquals("Blood-Brain Barrier", config.name);
        assertEquals("Immune System", config.biologicalMap);
        assertTrue(config.description.contains("microVM"));
        assertFalse(config.documentationFiles.isEmpty());
    }
    
    @Test
    public void testPiperConfiguration() {
        ExocortexAbsorber.RepositoryConfig config = ExocortexAbsorber.getRepository("piper");
        
        assertNotNull(config);
        assertEquals("rhasspy/piper", config.githubPath);
        assertEquals("Vocal Cords", config.name);
        assertEquals("Broca's Area / Vocal Tract", config.biologicalMap);
        assertTrue(config.description.contains("text-to-speech"));
        assertFalse(config.documentationFiles.isEmpty());
    }
    
    @Test
    public void testMemgraphConfiguration() {
        ExocortexAbsorber.RepositoryConfig config = ExocortexAbsorber.getRepository("memgraph");
        
        assertNotNull(config);
        assertEquals("memgraph/memgraph", config.githubPath);
        assertEquals("Synaptic Web", config.name);
        assertEquals("Connectome Cartographer", config.biologicalMap);
        assertTrue(config.description.contains("graph database"));
        assertFalse(config.documentationFiles.isEmpty());
    }
    
    @Test
    public void testAbsorbFirecracker() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("firecracker");
        
        assertNotNull(result);
        assertEquals("firecracker", result.repositoryName);
        assertTrue(result.success);
        assertEquals("COMPLETE", result.status);
        assertNull(result.error);
        assertTrue(result.extractedAPIs.containsKey("count"));
        assertTrue(result.extractedAPIs.containsKey("apis"));
        assertTrue(result.wrapperPaths.containsKey("main"));
    }
    
    @Test
    public void testAbsorbPiper() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("piper");
        
        assertNotNull(result);
        assertEquals("piper", result.repositoryName);
        assertTrue(result.success);
        assertEquals("COMPLETE", result.status);
        assertNull(result.error);
        assertTrue(result.extractedAPIs.containsKey("count"));
    }
    
    @Test
    public void testAbsorbMemgraph() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("memgraph");
        
        assertNotNull(result);
        assertEquals("memgraph", result.repositoryName);
        assertTrue(result.success);
        assertEquals("COMPLETE", result.status);
        assertNull(result.error);
    }
    
    @Test
    public void testAbsorbSearxNG() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("searxng");
        
        assertNotNull(result);
        assertEquals("searxng", result.repositoryName);
        assertTrue(result.success);
    }
    
    @Test
    public void testAbsorbOpenInterpreter() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("open-interpreter");
        
        assertNotNull(result);
        assertEquals("open-interpreter", result.repositoryName);
        assertTrue(result.success);
    }
    
    @Test
    public void testAbsorbCelery() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("celery");
        
        assertNotNull(result);
        assertEquals("celery", result.repositoryName);
        assertTrue(result.success);
    }
    
    @Test
    public void testAbsorbInvalidRepository() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("invalid-repo");
        
        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("FAILED", result.status);
        assertNotNull(result.error);
        assertTrue(result.error.contains("not found"));
    }
    
    @Test
    public void testAbsorbAll() {
        Map<String, ExocortexAbsorber.AbsorptionResult> results = absorber.absorbAll();
        
        assertNotNull(results);
        assertEquals(11, results.size()); // 11 repositories configured
        
        // Verify all succeeded
        for (ExocortexAbsorber.AbsorptionResult result : results.values()) {
            assertTrue(result.success, "Failed for " + result.repositoryName + ": " + result.error);
            assertEquals("COMPLETE", result.status);
        }
    }
    
    @Test
    public void testAPIExtraction() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("firecracker");
        
        @SuppressWarnings("unchecked")
        java.util.List<ExocortexAbsorber.ExtractedAPI> apis = 
            (java.util.List<ExocortexAbsorber.ExtractedAPI>) result.extractedAPIs.get("apis");
        
        assertNotNull(apis);
        assertFalse(apis.isEmpty());
        
        // Verify API structure
        for (ExocortexAbsorber.ExtractedAPI api : apis) {
            assertNotNull(api.name);
            assertNotNull(api.signature);
            assertNotNull(api.description);
            assertFalse(api.examples.isEmpty());
        }
    }
    
    @Test
    public void testWrapperGeneration() {
        ExocortexAbsorber.AbsorptionResult result = absorber.absorb("piper");
        
        String wrapperPath = result.wrapperPaths.get("main");
        assertNotNull(wrapperPath);
        assertTrue(wrapperPath.contains("PiperWrapper.java"));
    }
}
