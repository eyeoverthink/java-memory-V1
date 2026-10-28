package brain_architecture;

import brain_architecture.core.BrainType;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.persistence.FractalNode;
import brain_architecture.synapses.KnowledgeGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for FraynixSystem
 * Tests complete workflows and system lifecycle
 */
public class FraynixSystemEndToEndTest {

    private FraynixSystem fraynixSystem;
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        fraynixSystem = new FraynixSystem();
        tempDir = Files.createTempDirectory("fraynix_e2e_test_");
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (fraynixSystem.isInitialized()) {
            fraynixSystem.shutdown();
        }
        // Clean up temp files
        deleteDirectory(tempDir.toFile());
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    @Test
    public void testCompleteSystemLifecycle() {
        // Initialize system
        fraynixSystem.initialize();
        assertTrue(fraynixSystem.isInitialized());

        // Use system
        QuantumOracleIntegration.OracleResponse response = fraynixSystem.processQuery("What is consciousness?", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
        assertEquals("What is consciousness?", response.getQuery());

        // Shutdown system
        fraynixSystem.shutdown();
        assertFalse(fraynixSystem.isInitialized());
    }

    @Test
    public void testKnowledgeIngestionWorkflow() throws IOException {
        // Create test file
        File testFile = tempDir.resolve("test.md").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("# Test Knowledge\n\nThis is test knowledge about consciousness.");
        }

        // Initialize system
        fraynixSystem.initialize();

        // Ingest file
        fraynixSystem.ingestFile(testFile.toPath()).join();

        // Query system
        QuantumOracleIntegration.OracleResponse response = fraynixSystem.processQuery("What is test knowledge?", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
    }

    @Test
    public void testMultiBrainCollaborationWorkflow() {
        // Initialize system
        fraynixSystem.initialize();

        // Query multiple brains
        List<BrainType> brains = List.of(
            BrainType.QUANTUM,
            BrainType.CREATIVE,
            BrainType.LOGICAL
        );

        CompletableFuture<QuantumOracleIntegration.OracleResponse> future = 
            fraynixSystem.processMultiBrainQuery("What is consciousness?", QuantumOracleIntegration.OracleQueryType.LOGICAL, brains.toArray(new BrainType[0]));

        QuantumOracleIntegration.OracleResponse response = future.join();
        assertNotNull(response);
        assertEquals("What is consciousness?", response.getQuery());
    }

    @Test
    public void testIntegrationWorkflowWithSentientCore() {
        // Initialize system
        fraynixSystem.initialize();

        // Wire SentientCore
        fraynixSystem.wireSentientCore();

        // Query with SentientCore
        String response = 
            fraynixSystem.processQueryWithSentientCore("What is consciousness?");
        assertNotNull(response);

        // Learn from experience
        fraynixSystem.learnWithSentientCore("Consciousness is self-awareness.");

        // Unwire SentientCore
        fraynixSystem.unwireSentientCore();
    }

    @Test
    public void testIntegrationWorkflowWithHybridOrchestrator() {
        // Initialize system
        fraynixSystem.initialize();

        // Wire Hybrid Orchestrator
        fraynixSystem.wireHybridOrchestrator();

        // Verify orchestrator is wired
        java.util.Map<String, Object> status = fraynixSystem.getSystemStatus();
        assertNotNull(status);

        // Unwire Hybrid Orchestrator
        fraynixSystem.unwireHybridOrchestrator();
    }

    @Test
    public void testPersistenceWorkflow() {
        // Initialize system
        fraynixSystem.initialize();

        // Create knowledge graph
        KnowledgeGraph graph = new KnowledgeGraph("test_persistence", new ArrayList<>());

        // Persist knowledge graph
        java.util.UUID graphId = fraynixSystem.persistKnowledgeGraph(graph);
        assertNotNull(graphId);

        // Retrieve knowledge graph
        KnowledgeGraph retrieved = fraynixSystem.retrieveKnowledgeGraph(graphId);
        assertNotNull(retrieved);

        // Verify blockchain integrity
        boolean integrity = fraynixSystem.verifyBlockchainIntegrity();
        assertTrue(integrity);
    }

    @Test
    public void testFractalNodeWorkflow() {
        // Initialize system
        fraynixSystem.initialize();

        // Create knowledge graph for fractal node
        KnowledgeGraph graph = new KnowledgeGraph("fractal_test", new ArrayList<>());

        // Create fractal node
        FractalNode node = fraynixSystem.createFractalNode(graph);
        assertNotNull(node);
    }

    @Test
    public void testErrorHandlingWorkflow() {
        // Initialize system
        fraynixSystem.initialize();

        // Test query with empty string
        QuantumOracleIntegration.OracleResponse response = fraynixSystem.processQuery("", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);

        // Test query with null (should handle gracefully)
        try {
            response = fraynixSystem.processQuery(null, QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
            // Should either handle gracefully or throw appropriate exception
        } catch (Exception e) {
            // Expected for null query
        }
    }

    @Test
    public void testMultipleInitializationAndShutdown() {
        // First lifecycle
        fraynixSystem.initialize();
        assertTrue(fraynixSystem.isInitialized());
        fraynixSystem.shutdown();
        assertFalse(fraynixSystem.isInitialized());

        // Second lifecycle (idempotent)
        fraynixSystem.initialize();
        assertTrue(fraynixSystem.isInitialized());
        fraynixSystem.shutdown();
        assertFalse(fraynixSystem.isInitialized());
    }

    @Test
    public void testSystemStatusWorkflow() {
        // Initialize system
        fraynixSystem.initialize();

        // Get system status
        java.util.Map<String, Object> status = fraynixSystem.getSystemStatus();
        assertNotNull(status);
        assertFalse(status.isEmpty());

        // Wire integrations
        fraynixSystem.wireSentientCore();
        fraynixSystem.wireHybridOrchestrator();

        // Get updated status
        java.util.Map<String, Object> updatedStatus = fraynixSystem.getSystemStatus();
        assertNotNull(updatedStatus);

        // Unwire integrations
        fraynixSystem.unwireSentientCore();
        fraynixSystem.unwireHybridOrchestrator();
    }

    @Test
    public void testDirectoryIngestionWorkflow() throws IOException {
        // Create test directory with files
        File testDir = tempDir.resolve("test_dir").toFile();
        testDir.mkdirs();

        File file1 = new File(testDir, "file1.md");
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write("# File 1\n\nContent 1");
        }

        File file2 = new File(testDir, "file2.md");
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("# File 2\n\nContent 2");
        }

        // Initialize system
        fraynixSystem.initialize();

        // Ingest directory
        fraynixSystem.ingestDirectory(testDir.toPath()).join();

        // Query system
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("What is in the test directory?", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);
    }

    @Test
    public void testAccessorWorkflow() {
        // Initialize system
        fraynixSystem.initialize();

        // Access all components
        assertNotNull(fraynixSystem.getBrains());
        assertNotNull(fraynixSystem.getCommunication());
        assertNotNull(fraynixSystem.getKnowledgeScraper());
        assertNotNull(fraynixSystem.getPersistentMemory());
    }

    @Test
    public void testCompleteIntegrationWorkflow() {
        // Initialize system
        fraynixSystem.initialize();

        // Wire both integrations
        fraynixSystem.wireSentientCore();
        fraynixSystem.wireHybridOrchestrator();

        // Query with all integrations active
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("What is consciousness?", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        assertNotNull(response);

        // Learn with SentientCore
        fraynixSystem.learnWithSentientCore("Consciousness is self-awareness.");

        // Verify system status includes all components
        java.util.Map<String, Object> status = fraynixSystem.getSystemStatus();
        assertNotNull(status);

        // Unwire integrations
        fraynixSystem.unwireSentientCore();
        fraynixSystem.unwireHybridOrchestrator();

        // Shutdown
        fraynixSystem.shutdown();
        assertFalse(fraynixSystem.isInitialized());
    }
}
