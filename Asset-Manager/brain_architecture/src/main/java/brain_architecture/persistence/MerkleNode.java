package brain_architecture.persistence;

/**
 * MerkleNode - A node in the Merkle tree.
 * Contains hash and references to left and right children.
 */
public class MerkleNode {
    private String hash;
    private MerkleNode left;
    private MerkleNode right;
    
    /**
     * Constructor for leaf node.
     */
    public MerkleNode(String hash) {
        this.hash = hash;
        this.left = null;
        this.right = null;
    }
    
    /**
     * Constructor for internal node.
     */
    public MerkleNode(String hash, MerkleNode left, MerkleNode right) {
        this.hash = hash;
        this.left = left;
        this.right = right;
    }
    
    /**
     * Check if this is a leaf node.
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }
    
    // Getters and setters
    public String getHash() { return hash; }
    public MerkleNode getLeft() { return left; }
    public MerkleNode getRight() { return right; }
    
    public void setHash(String hash) { this.hash = hash; }
    public void setLeft(MerkleNode left) { this.left = left; }
    public void setRight(MerkleNode right) { this.right = right; }
    
    @Override
    public String toString() {
        return "MerkleNode{" +
                "hash='" + hash.substring(0, 8) + "...'" +
                ", isLeaf=" + isLeaf() +
                '}';
    }
}
