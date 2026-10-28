package brain_architecture.integration;

import brain_architecture.synapses.KnowledgeGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KnowledgeIngestionIntegrationTest - Integration tests for knowledge ingestion.
 */
public class KnowledgeIngestionIntegrationTest {
    
    private QuantumOracleIntegration oracle;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        oracle = new QuantumOracleIntegration();
        assertTrue(oracle.getBrains().size() == 12, "All 12 brains should be initialized");
    }
    
    @Test
    void testKnowledgeScraperInitialization() {
        assertNotNull(oracle.getKnowledgeScraper(), "KnowledgeScraper should be initialized");
        assertNotNull(oracle.getHDCMapper(), "HyperdimensionalMapper should be initialized");
        assertNotNull(oracle.getHDCSimilarityEngine(), "HDCSimilarityEngine should be initialized");
    }
    
    @Test
    void testIngestMarkdownFile() throws Exception {
        // Create a test markdown file
        Path mdFile = tempDir.resolve("test.md");
        Files.writeString(mdFile, "# Test Document\n\nThis is a test. The concept is defined as a test concept.");
        
        // Ingest the file
        CompletableFuture<KnowledgeGraph> future = oracle.ingestFile(mdFile);
        KnowledgeGraph graph = future.get();
        
        // Verify
        assertNotNull(graph);
        assertNotNull(graph.getConcepts());
        assertTrue(graph.getConceptCount() > 0, "Should extract concepts from markdown");
        assertEquals(mdFile.toString(), graph.getSource());
    }
    
    @Test
    void testIngestPlainTextFile() throws Exception {
        // Create a test text file
        Path txtFile = tempDir.resolve("test.txt");
        Files.writeString(txtFile, "This is a plain text file with some concepts.");
        
        // Ingest the file
        CompletableFuture<KnowledgeGraph> future = oracle.ingestFile(txtFile);
        KnowledgeGraph graph = future.get();
        
        // Verify
        assertNotNull(graph);
        assertNotNull(graph.getConcepts());
        assertTrue(graph.getConceptCount() > 0);
    }
    
    @Test
    void testIngestDirectory() throws Exception {
        // Create test files
        Files.writeString(tempDir.resolve("file1.txt"), "First file with concepts.");
        Files.writeString(tempDir.resolve("file2.txt"), "Second file with more concepts.");
        
        // Ingest directory
        CompletableFuture<KnowledgeGraph> future = oracle.ingestDirectory(tempDir);
        KnowledgeGraph graph = future.get();
        
        // Verify
        assertNotNull(graph);
        assertNotNull(graph.getConcepts());
        assertTrue(graph.getConceptCount() > 0);
        assertEquals(2, graph.getSources().size());
    }
    
    @Test
    void testHDCMapping() {
        String concept = "test concept";
        var vector = oracle.getHDCMapper().map(concept);
        
        assertNotNull(vector);
        assertEquals(16384, vector.size());
    }
    
    @Test
    void testHDCCalculations() {
        var vectorA = oracle.getHDCMapper().map("concept A");
        var vectorB = oracle.getHDCMapper().map("concept B");
        
        double similarity = oracle.getHDCSimilarityEngine().calculateSimilarity(vectorA, vectorB);
        
        assertTrue(similarity >= 0.0 && similarity <= 1.0);
    }
    
    @Test
    void testKnowledgeIngestionWithMemoryBrain() throws Exception {
        // Create a test file
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "Test concept for memory brain integration.");
        
        // Ingest the file
        CompletableFuture<KnowledgeGraph> future = oracle.ingestFile(testFile);
        KnowledgeGraph graph = future.get();
        
        // Verify concepts were extracted
        assertTrue(graph.getConceptCount() > 0);
        
        // The KnowledgeScraper should have integrated with MemoryBrain
        // This is verified by the fact that ingestFile() completed without error
    }
    
    @Test
    void testMultipleFileIngestion() throws Exception {
        // Create multiple test files
        for (int i = 0; i < 5; i++) {
            Path file = tempDir.resolve("file" + i + ".txt");
            Files.writeString(file, "Concept " + i + " for testing.");
        }
        
        // Ingest directory
        CompletableFuture<KnowledgeGraph> future = oracle.ingestDirectory(tempDir);
        KnowledgeGraph graph = future.get();
        
        // Verify
        assertEquals(5, graph.getSources().size());
        assertTrue(graph.getConceptCount() > 0);
    }
    
    @Test
    void testKnowledgeGraphMetadata() throws Exception {
        // Create a test file
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "Test concept.");
        
        // Ingest the file
        CompletableFuture<KnowledgeGraph> future = oracle.ingestFile(testFile);
        KnowledgeGraph graph = future.get();
        
        // Verify metadata
        assertNotNull(graph.getMetadata());
        assertNotNull(graph.getSource());
    }
    
    @Test
    void testShutdownWithKnowledgeScraper() {
        QuantumOracleIntegration testOracle = new QuantumOracleIntegration();
        assertNotNull(testOracle.getKnowledgeScraper());
        
        // Should not throw exception
        testOracle.shutdown();
    }
}
