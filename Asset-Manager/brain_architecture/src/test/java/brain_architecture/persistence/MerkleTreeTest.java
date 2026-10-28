package brain_architecture.persistence;

import brain_architecture.synapses.Concept;
import brain_architecture.synapses.KnowledgeGraph;
import brain_architecture.synapses.Synapse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MerkleTreeTest - Unit tests for MerkleTree class.
 */
public class MerkleTreeTest {
    
    private MerkleTree merkleTree;
    private List<KnowledgeGraph> graphs;
    
    @BeforeEach
    void setUp() {
        merkleTree = new MerkleTree();
        graphs = new ArrayList<>();
        
        // Create test knowledge graphs
        for (int i = 0; i < 4; i++) {
            KnowledgeGraph graph = new KnowledgeGraph("source_" + i, new ArrayList<>());
            graphs.add(graph);
        }
    }
    
    @Test
    void testMerkleTreeCreation() {
        assertNotNull(merkleTree);
        assertFalse(merkleTree.isBuilt());
        assertEquals("", merkleTree.getRootHash());
    }
    
    @Test
    void testBuildTree() {
        merkleTree.build(graphs);
        
        assertTrue(merkleTree.isBuilt());
        assertNotNull(merkleTree.getRootHash());
        assertFalse(merkleTree.getRootHash().isEmpty());
    }
    
    @Test
    void testBuildEmptyList() {
        merkleTree.build(new ArrayList<>());
        
        assertFalse(merkleTree.isBuilt());
        assertNull(merkleTree.getRoot());
    }
    
    @Test
    void testBuildNullList() {
        merkleTree.build(null);
        
        assertFalse(merkleTree.isBuilt());
        assertNull(merkleTree.getRoot());
    }
    
    @Test
    void testBuildSingleGraph() {
        List<KnowledgeGraph> singleGraph = new ArrayList<>();
        singleGraph.add(graphs.get(0));
        
        merkleTree.build(singleGraph);
        
        assertTrue(merkleTree.isBuilt());
        assertNotNull(merkleTree.getRootHash());
    }
    
    @Test
    void testGetRootHash() {
        merkleTree.build(graphs);
        String rootHash = merkleTree.getRootHash();
        
        assertNotNull(rootHash);
        assertEquals(64, rootHash.length()); // SHA-256 produces 64 hex characters
    }
    
    @Test
    void testRootHashDeterministic() {
        merkleTree.build(graphs);
        String hash1 = merkleTree.getRootHash();
        
        MerkleTree tree2 = new MerkleTree();
        tree2.build(graphs);
        String hash2 = tree2.getRootHash();
        
        assertEquals(hash1, hash2);
    }
    
    @Test
    void testGenerateProof() {
        merkleTree.build(graphs);
        
        String proof = merkleTree.generateProof(graphs.get(0));
        assertNotNull(proof);
    }
    
    @Test
    void testGenerateProofForNonExistentGraph() {
        merkleTree.build(graphs);
        
        KnowledgeGraph newGraph = new KnowledgeGraph("new_source", new ArrayList<>());
        String proof = merkleTree.generateProof(newGraph);
        
        assertEquals("", proof);
    }
    
    @Test
    void testVerifyValidProof() {
        merkleTree.build(graphs);
        
        KnowledgeGraph graph = graphs.get(0);
        String proof = merkleTree.generateProof(graph);
        
        assertTrue(merkleTree.verify(graph, proof));
    }
    
    @Test
    void testVerifyInvalidProof() {
        merkleTree.build(graphs);
        
        KnowledgeGraph graph = graphs.get(0);
        String invalidProof = "invalid_proof";
        
        assertFalse(merkleTree.verify(graph, invalidProof));
    }
    
    @Test
    void testVerifyWithEmptyTree() {
        KnowledgeGraph graph = graphs.get(0);
        
        assertFalse(merkleTree.verify(graph, ""));
    }
    
    @Test
    void testGetLeaves() {
        merkleTree.build(graphs);
        
        List<MerkleNode> leaves = merkleTree.getLeaves();
        assertNotNull(leaves);
        assertEquals(4, leaves.size());
    }
    
    @Test
    void testAllLeavesAreLeafNodes() {
        merkleTree.build(graphs);
        
        List<MerkleNode> leaves = merkleTree.getLeaves();
        for (MerkleNode leaf : leaves) {
            assertTrue(leaf.isLeaf());
        }
    }
    
    @Test
    void testGetRoot() {
        merkleTree.build(graphs);
        
        MerkleNode root = merkleTree.getRoot();
        assertNotNull(root);
        assertFalse(root.isLeaf()); // Root should not be a leaf (unless single element)
    }
    
    @Test
    void testRootIsLeafForSingleElement() {
        List<KnowledgeGraph> singleGraph = new ArrayList<>();
        singleGraph.add(graphs.get(0));
        
        merkleTree.build(singleGraph);
        
        MerkleNode root = merkleTree.getRoot();
        assertNotNull(root);
        assertTrue(root.isLeaf()); // Single element tree has root as leaf
    }
}
