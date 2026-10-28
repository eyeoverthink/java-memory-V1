package brain_architecture.persistence;

import brain_architecture.synapses.KnowledgeGraph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MerkleTree - Binary hash tree for efficient integrity verification.
 * Allows O(log n) verification of data inclusion.
 */
public class MerkleTree {
    private MerkleNode root;
    private List<MerkleNode> leaves;
    private Map<String, String> hashMap; // data hash -> leaf node hash
    
    /**
     * Constructor.
     */
    public MerkleTree() {
        this.leaves = new ArrayList<>();
        this.hashMap = new HashMap<>();
    }
    
    /**
     * Build Merkle tree from a list of knowledge graphs.
     */
    public void build(List<KnowledgeGraph> graphs) {
        if (graphs == null || graphs.isEmpty()) {
            this.root = null;
            return;
        }
        
        // Create leaf nodes
        this.leaves = new ArrayList<>();
        this.hashMap = new HashMap<>();
        
        for (KnowledgeGraph graph : graphs) {
            String dataHash = hashGraph(graph);
            MerkleNode leaf = new MerkleNode(dataHash);
            leaves.add(leaf);
            hashMap.put(dataHash, leaf.getHash());
        }
        
        // Build tree bottom-up
        List<MerkleNode> currentLevel = new ArrayList<>(leaves);
        
        while (currentLevel.size() > 1) {
            List<MerkleNode> nextLevel = new ArrayList<>();
            
            for (int i = 0; i < currentLevel.size(); i += 2) {
                MerkleNode left = currentLevel.get(i);
                MerkleNode right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left;
                
                String combinedHash = left.getHash() + right.getHash();
                String parentHash = applySha256(combinedHash);
                
                MerkleNode parent = new MerkleNode(parentHash);
                parent.setLeft(left);
                parent.setRight(right);
                
                nextLevel.add(parent);
            }
            
            currentLevel = nextLevel;
        }
        
        this.root = currentLevel.get(0);
    }
    
    /**
     * Get the root hash of the Merkle tree.
     */
    public String getRootHash() {
        return root != null ? root.getHash() : "";
    }
    
    /**
     * Generate a Merkle proof for a knowledge graph.
     * Returns the list of hashes needed to verify inclusion.
     */
    public String generateProof(KnowledgeGraph graph) {
        if (root == null) {
            return "";
        }
        
        String dataHash = hashGraph(graph);
        MerkleNode leaf = findLeafByDataHash(dataHash);
        
        if (leaf == null) {
            return ""; // Graph not in tree
        }
        
        return generateProofForNode(leaf, root, new ArrayList<>());
    }
    
    /**
     * Generate proof for a specific node.
     */
    private String generateProofForNode(MerkleNode target, MerkleNode current, List<String> proof) {
        if (current == target) {
            // Reverse the proof so it goes from leaf to root (verification order)
            Collections.reverse(proof);
            return String.join(",", proof);
        }
        
        if (current.getLeft() != null) {
            if (isDescendant(target, current.getLeft())) {
                // Target is in left subtree, add right sibling with "R" prefix
                proof.add("R:" + current.getRight().getHash());
                return generateProofForNode(target, current.getLeft(), proof);
            }
        }
        
        if (current.getRight() != null) {
            if (isDescendant(target, current.getRight())) {
                // Target is in right subtree, add left sibling with "L" prefix
                proof.add("L:" + current.getLeft().getHash());
                return generateProofForNode(target, current.getRight(), proof);
            }
        }
        
        return "";
    }
    
    /**
     * Check if target is a descendant of current node.
     */
    private boolean isDescendant(MerkleNode target, MerkleNode current) {
        if (current == null) {
            return false;
        }
        if (current == target) {
            return true;
        }
        return isDescendant(target, current.getLeft()) || isDescendant(target, current.getRight());
    }
    
    /**
     * Verify a knowledge graph using its Merkle proof.
     */
    public boolean verify(KnowledgeGraph graph, String proof) {
        if (root == null) {
            return false;
        }
        
        String dataHash = hashGraph(graph);
        String currentHash = dataHash;
        
        if (proof.isEmpty()) {
            return currentHash.equals(root.getHash());
        }
        
        String[] proofHashes = proof.split(",");
        
        for (String proofHash : proofHashes) {
            // Parse position information (L: or R: prefix)
            String[] parts = proofHash.split(":", 2);
            if (parts.length != 2) {
                return false; // Invalid proof format
            }
            
            String position = parts[0];
            String hash = parts[1];
            
            // Use position to determine order
            if (position.equals("L")) {
                // Target is on right, sibling is on left: hash + currentHash
                currentHash = applySha256(hash + currentHash);
            } else if (position.equals("R")) {
                // Target is on left, sibling is on right: currentHash + hash
                currentHash = applySha256(currentHash + hash);
            } else {
                return false; // Invalid position
            }
        }
        
        return currentHash.equals(root.getHash());
    }
    
    /**
     * Find leaf node by data hash.
     */
    private MerkleNode findLeafByDataHash(String dataHash) {
        for (MerkleNode leaf : leaves) {
            if (leaf.getHash().equals(dataHash)) {
                return leaf;
            }
        }
        return null;
    }
    
    /**
     * Hash a knowledge graph.
     */
    private String hashGraph(KnowledgeGraph graph) {
        if (graph == null) {
            return "";
        }
        return applySha256(graph.getSource() + graph.getConceptCount() + graph.getTimestamp());
    }
    
    /**
     * Apply SHA-256 hash function.
     */
    private String applySha256(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get the root node.
     */
    public MerkleNode getRoot() {
        return root;
    }
    
    /**
     * Get all leaf nodes.
     */
    public List<MerkleNode> getLeaves() {
        return new ArrayList<>(leaves);
    }
    
    /**
     * Check if tree is built.
     */
    public boolean isBuilt() {
        return root != null;
    }
}
