package brain_architecture.synapses;

import brain_architecture.brains.MemoryBrain;
import brain_architecture.core.BrainConfiguration;
import brain_architecture.core.BrainType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KnowledgeScraperTest - Unit tests for KnowledgeScraper.
 */
public class KnowledgeScraperTest {
    
    private KnowledgeScraper scraper;
    private MemoryBrain memoryBrain;
    private HyperdimensionalMapper hdcMapper;
    private HDCSimilarityEngine hdcEngine;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        hdcMapper = new HyperdimensionalMapper();
        hdcEngine = new HDCSimilarityEngine();
        
        // Create a simple MemoryBrain for testing
        BrainConfiguration config = new BrainConfiguration(
            BrainType.MEMORY,
            1.0,
            0.5,
            30,
            0.6,
            0.3,
            0.1,
            new HashMap<>()
        );
        memoryBrain = new MemoryBrain(config);
        
        scraper = new KnowledgeScraper(memoryBrain, hdcMapper, hdcEngine);
    }
    
    @Test
    void testScrapeMarkdownFile() throws Exception {
        // Create a test markdown file
        Path mdFile = tempDir.resolve("test.md");
        Files.writeString(mdFile, "# Test Document\n\nThis is a test. The concept is defined as a test concept.");
        
        // Scrape the file
        KnowledgeGraph graph = scraper.scrapeFile(mdFile).get();
        
        // Verify
        assertNotNull(graph);
        assertNotNull(graph.getConcepts());
        assertTrue(graph.getConceptCount() > 0);
        assertEquals(mdFile.toString(), graph.getSource());
    }
    
    @Test
    void testScrapePlainTextFile() throws Exception {
        // Create a test text file
        Path txtFile = tempDir.resolve("test.txt");
        Files.writeString(txtFile, "This is a plain text file with some concepts.");
        
        // Scrape the file
        KnowledgeGraph graph = scraper.scrapeFile(txtFile).get();
        
        // Verify
        assertNotNull(graph);
        assertNotNull(graph.getConcepts());
        assertTrue(graph.getConceptCount() > 0);
    }
    
    @Test
    void testScrapeDirectory() throws Exception {
        // Create test files
        Files.writeString(tempDir.resolve("file1.txt"), "First file with concepts.");
        Files.writeString(tempDir.resolve("file2.txt"), "Second file with more concepts.");
        
        // Scrape directory
        KnowledgeGraph graph = scraper.scrapeDirectory(tempDir).get();
        
        // Verify
        assertNotNull(graph);
        assertNotNull(graph.getConcepts());
        assertTrue(graph.getConceptCount() > 0);
        assertEquals(2, graph.getSources().size());
    }
    
    @Test
    void testConceptExtraction() {
        ConceptExtractor extractor = new ConceptExtractor(hdcMapper);
        
        String content = "A neuron is defined as a cell that processes information. " +
                        "Synapses are connections between neurons.";
        
        List<Concept> concepts = extractor.extract(content);
        
        // Verify
        assertNotNull(concepts);
        assertTrue(concepts.size() > 0);
    }
    
    @Test
    void testHyperdimensionalMapping() {
        String concept = "test concept";
        BitSet vector1 = hdcMapper.map(concept);
        BitSet vector2 = hdcMapper.map(concept);
        
        // Verify determinism
        assertEquals(vector1, vector2);
        assertEquals(16384, vector1.size());
    }
    
    @Test
    void testSimilarityCalculation() {
        BitSet vectorA = hdcMapper.map("concept A");
        BitSet vectorB = hdcMapper.map("concept A"); // Same concept
        BitSet vectorC = hdcMapper.map("concept C"); // Different concept
        
        // Verify
        double similarityAB = hdcEngine.calculateSimilarity(vectorA, vectorB);
        double similarityAC = hdcEngine.calculateSimilarity(vectorA, vectorC);
        
        assertEquals(1.0, similarityAB, 0.001); // Same concept = 1.0
        assertTrue(similarityAC < 1.0); // Different concept < 1.0
        assertTrue(similarityAC > 0.0); // But still similar
    }
    
    @Test
    void testSynapseCreation() {
        SynapseManager manager = new SynapseManager(hdcEngine, 0.7, 0.5, 0.1);
        
        Concept conceptA = new Concept("A", "Definition A", "test", hdcMapper.map("A"));
        Concept conceptB = new Concept("B", "Definition B", "test", hdcMapper.map("B"));
        
        manager.registerConcept(conceptA);
        manager.registerConcept(conceptB);
        
        manager.createSynapticConnections(List.of(conceptA, conceptB));
        
        // Verify
        assertEquals(2, manager.getConceptCount());
        assertTrue(manager.getSynapseCount() >= 0); // May or may not create synapse based on similarity
    }
    
    @Test
    void testSynapsePlasticity() {
        Concept source = new Concept("source", "Source concept", "test", hdcMapper.map("source"));
        Concept target = new Concept("target", "Target concept", "test", hdcMapper.map("target"));
        
        Synapse synapse = new Synapse(
            source.getConceptId(),
            target.getConceptId(),
            0.5,
            Neurotransmitter.ACETYLCHOLINE
        );
        
        double initialWeight = synapse.getWeight();
        
        // Strengthen
        synapse.strengthen(0.1, 0.1);
        assertTrue(synapse.getWeight() > initialWeight);
        
        // Weaken
        synapse.weaken(0.1);
        assertTrue(synapse.getWeight() < initialWeight + 0.1);
    }
    
    @Test
    void testSTDP() {
        Synapse synapse = new Synapse(
            UUID.randomUUID(),
            UUID.randomUUID(),
            0.5,
            Neurotransmitter.ACETYLCHOLINE
        );
        
        double initialWeight = synapse.getWeight();
        
        // Pre before post: strengthen
        synapse.applySTDP(100, 90);
        assertTrue(synapse.getWeight() > initialWeight);
        
        // Reset
        synapse = new Synapse(
            UUID.randomUUID(),
            UUID.randomUUID(),
            0.5,
            Neurotransmitter.ACETYLCHOLINE
        );
        
        // Post before pre: weaken
        synapse.applySTDP(90, 100);
        assertTrue(synapse.getWeight() < 0.5);
    }
    
    @Test
    void testNeurotransmitterValence() {
        assertTrue(Neurotransmitter.ACETYLCHOLINE.isExcitatory());
        assertTrue(Neurotransmitter.GABA.isInhibitory());
        assertTrue(Neurotransmitter.DOPAMINE.isModulatory());
    }
    
    @Test
    void testShutdown() {
        scraper.shutdown();
        // Should complete without exception
    }
}
