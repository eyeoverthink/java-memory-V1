package brain_architecture.persistence;

import brain_architecture.synapses.Concept;
import brain_architecture.synapses.KnowledgeGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FractalNodeTest - Unit tests for FractalNode class.
 */
public class FractalNodeTest {
    
    private FractalNode rootNode;
    private KnowledgeGraph graph;
    
    @BeforeEach
    void setUp() {
        graph = new KnowledgeGraph("test_source", new ArrayList<>());
        rootNode = new FractalNode(graph);
    }
    
    @Test
    void testFractalNodeCreation() {
        assertNotNull(rootNode);
        assertNotNull(rootNode.getNodeId());
        assertNull(rootNode.getParentId());
        assertTrue(rootNode.isRoot());
        assertTrue(rootNode.isLeaf());
        assertFalse(rootNode.isCompressed());
        assertEquals(1.0, rootNode.getCompressionRatio());
    }
    
    @Test
    void testChildNodeCreation() {
        FractalPattern pattern = new FractalPattern("child_pattern");
        KnowledgeGraph childGraph = new KnowledgeGraph("child_source", new ArrayList<>());
        FractalNode childNode = new FractalNode(rootNode.getNodeId(), childGraph, pattern);
        
        assertNotNull(childNode);
        assertEquals(rootNode.getNodeId(), childNode.getParentId());
        assertFalse(childNode.isRoot());
        assertTrue(childNode.isLeaf());
    }
    
    @Test
    void testAddChild() {
        FractalPattern pattern = new FractalPattern("child_pattern");
        KnowledgeGraph childGraph = new KnowledgeGraph("child_source", new ArrayList<>());
        FractalNode childNode = new FractalNode(rootNode.getNodeId(), childGraph, pattern);
        
        rootNode.addChild(childNode);
        
        assertEquals(1, rootNode.getChildIds().size());
        assertTrue(rootNode.getChildIds().contains(childNode.getNodeId()));
    }
    
    @Test
    void testAddDuplicateChild() {
        FractalPattern pattern = new FractalPattern("child_pattern");
        KnowledgeGraph childGraph = new KnowledgeGraph("child_source", new ArrayList<>());
        FractalNode childNode = new FractalNode(rootNode.getNodeId(), childGraph, pattern);
        
        rootNode.addChild(childNode);
        rootNode.addChild(childNode); // Try to add again
        
        assertEquals(1, rootNode.getChildIds().size()); // Should not add duplicate
    }
    
    @Test
    void testRemoveChild() {
        FractalPattern pattern = new FractalPattern("child_pattern");
        KnowledgeGraph childGraph = new KnowledgeGraph("child_source", new ArrayList<>());
        FractalNode childNode = new FractalNode(rootNode.getNodeId(), childGraph, pattern);
        
        rootNode.addChild(childNode);
        assertEquals(1, rootNode.getChildIds().size());
        
        rootNode.removeChild(childNode.getNodeId());
        assertEquals(0, rootNode.getChildIds().size());
    }
    
    @Test
    void testGetDescendants() {
        FractalPattern pattern = new FractalPattern("child_pattern");
        KnowledgeGraph childGraph = new KnowledgeGraph("child_source", new ArrayList<>());
        FractalNode childNode = new FractalNode(rootNode.getNodeId(), childGraph, pattern);
        
        rootNode.addChild(childNode);
        
        List<UUID> descendants = rootNode.getDescendants(1);
        assertEquals(1, descendants.size());
        assertTrue(descendants.contains(childNode.getNodeId()));
    }
    
    @Test
    void testGetDescendantsDepthZero() {
        FractalPattern pattern = new FractalPattern("child_pattern");
        KnowledgeGraph childGraph = new KnowledgeGraph("child_source", new ArrayList<>());
        FractalNode childNode = new FractalNode(rootNode.getNodeId(), childGraph, pattern);
        
        rootNode.addChild(childNode);
        
        List<UUID> descendants = rootNode.getDescendants(0);
        assertEquals(0, descendants.size());
    }
    
    @Test
    void testCompress() {
        // Add some concepts to the graph
        List<Concept> concepts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BitSet vector = new BitSet(16384);
            Concept concept = new Concept("concept_" + i, "definition", "category", vector);
            concept.setStrength(i < 5 ? 0.5 : 0.2); // Some weak concepts
            concepts.add(concept);
        }
        KnowledgeGraph graphWithConcepts = new KnowledgeGraph("test_source", concepts);
        FractalNode node = new FractalNode(graphWithConcepts);
        
        node.compress();
        
        assertTrue(node.isCompressed());
        assertTrue(node.getCompressionRatio() < 1.0);
        assertTrue(node.getCompressionRatio() > 0.0);
    }
    
    @Test
    void testCompressAlreadyCompressed() {
        rootNode.compress();
        assertTrue(rootNode.isCompressed());
        
        double ratioBefore = rootNode.getCompressionRatio();
        rootNode.compress(); // Try to compress again
        assertEquals(ratioBefore, rootNode.getCompressionRatio());
    }
    
    @Test
    void testDecompress() {
        List<Concept> concepts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BitSet vector = new BitSet(16384);
            Concept concept = new Concept("concept_" + i, "definition", "category", vector);
            concept.setStrength(0.2);
            concepts.add(concept);
        }
        KnowledgeGraph graphWithConcepts = new KnowledgeGraph("test_source", concepts);
        FractalNode node = new FractalNode(graphWithConcepts);
        
        node.compress();
        assertTrue(node.isCompressed());
        
        node.decompress();
        assertFalse(node.isCompressed());
        assertEquals(1.0, node.getCompressionRatio());
    }
    
    @Test
    void testDecompressNotCompressed() {
        assertFalse(rootNode.isCompressed());
        rootNode.decompress();
        assertFalse(rootNode.isCompressed());
        assertEquals(1.0, rootNode.getCompressionRatio());
    }
    
    @Test
    void testFindPattern() {
        FractalPattern pattern = new FractalPattern("target");
        List<Double> signature = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            signature.add(0.5);
        }
        pattern.setSignature(signature);
        
        // Create node with similar pattern
        FractalPattern nodePattern = new FractalPattern("node_pattern");
        nodePattern.setSignature(signature);
        KnowledgeGraph nodeGraph = new KnowledgeGraph("node_source", new ArrayList<>());
        FractalNode node = new FractalNode(null, nodeGraph, nodePattern);
        
        FractalNode found = node.findPattern(pattern);
        assertNotNull(found);
        assertEquals(node, found);
    }
    
    @Test
    void testFindPatternNotSimilar() {
        FractalPattern pattern = new FractalPattern("target");
        List<Double> signature1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            signature1.add(0.5);
        }
        pattern.setSignature(signature1);
        
        // Create node with different pattern (orthogonal vectors for low similarity)
        FractalPattern nodePattern = new FractalPattern("node_pattern");
        List<Double> signature2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            signature2.add(i % 2 == 0 ? 1.0 : -1.0); // Orthogonal pattern
        }
        nodePattern.setSignature(signature2);
        KnowledgeGraph nodeGraph = new KnowledgeGraph("node_source", new ArrayList<>());
        FractalNode node = new FractalNode(null, nodeGraph, nodePattern);
        
        FractalNode found = node.findPattern(pattern);
        assertNull(found);
    }
    
    @Test
    void testSetters() {
        UUID newParentId = UUID.randomUUID();
        rootNode.setParentId(newParentId);
        assertEquals(newParentId, rootNode.getParentId());
        
        KnowledgeGraph newGraph = new KnowledgeGraph("new_source", new ArrayList<>());
        rootNode.setSubgraph(newGraph);
        assertEquals(newGraph, rootNode.getSubgraph());
        
        FractalPattern newPattern = new FractalPattern("new_pattern");
        rootNode.setPattern(newPattern);
        assertEquals(newPattern, rootNode.getPattern());
    }
    
    @Test
    void testToString() {
        String str = rootNode.toString();
        assertNotNull(str);
        assertTrue(str.contains("nodeId="));
        assertTrue(str.contains("children="));
    }
}
