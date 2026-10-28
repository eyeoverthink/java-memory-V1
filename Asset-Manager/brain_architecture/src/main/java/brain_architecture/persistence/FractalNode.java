package brain_architecture.persistence;

import brain_architecture.synapses.KnowledgeGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * FractalNode - Hierarchical knowledge storage node with self-similar patterns.
 * Enables efficient compression and natural clustering.
 */
public class FractalNode {
    private UUID nodeId;
    private UUID parentId;
    private List<UUID> childIds;
    private KnowledgeGraph subgraph;
    private FractalPattern pattern;
    private double compressionRatio;
    private long timestamp;
    private boolean compressed;
    
    /**
     * Constructor for root node.
     */
    public FractalNode(KnowledgeGraph subgraph) {
        this.nodeId = UUID.randomUUID();
        this.parentId = null;
        this.childIds = new ArrayList<>();
        this.subgraph = subgraph;
        this.pattern = new FractalPattern("root");
        this.compressionRatio = 1.0;
        this.timestamp = System.currentTimeMillis();
        this.compressed = false;
    }
    
    /**
     * Constructor for child node.
     */
    public FractalNode(UUID parentId, KnowledgeGraph subgraph, FractalPattern pattern) {
        this.nodeId = UUID.randomUUID();
        this.parentId = parentId;
        this.childIds = new ArrayList<>();
        this.subgraph = subgraph;
        this.pattern = pattern;
        this.compressionRatio = 1.0;
        this.timestamp = System.currentTimeMillis();
        this.compressed = false;
    }
    
    /**
     * Add a child node.
     */
    public void addChild(FractalNode child) {
        if (!childIds.contains(child.getNodeId())) {
            childIds.add(child.getNodeId());
            child.setParentId(this.nodeId);
        }
    }
    
    /**
     * Remove a child node by ID.
     */
    public void removeChild(UUID childId) {
        childIds.remove(childId);
    }
    
    /**
     * Get all descendants to a specified depth.
     */
    public List<UUID> getDescendants(int depth) {
        List<UUID> descendants = new ArrayList<>();
        getDescendantsRecursive(this, depth, 0, descendants);
        return descendants;
    }
    
    /**
     * Recursive helper for getDescendants.
     */
    private void getDescendantsRecursive(FractalNode node, int maxDepth, int currentDepth, List<UUID> descendants) {
        if (currentDepth >= maxDepth) {
            return;
        }
        
        for (UUID childId : node.getChildIds()) {
            descendants.add(childId);
            // In a real implementation, we would need to access the child nodes
            // This requires a FractalTree or registry to look up nodes by ID
            // For now, we just add the IDs
        }
    }
    
    /**
     * Compress the subgraph using fractal compression.
     */
    public void compress() {
        if (compressed || subgraph == null) {
            return;
        }
        
        int originalSize = subgraph.getConceptCount();
        
        // Simple compression: remove weak concepts (strength < 0.3)
        // In a real implementation, this would use pattern recognition
        subgraph.removeConceptsIf(concept -> concept.getStrength() < 0.3);
        
        int compressedSize = subgraph.getConceptCount();
        this.compressionRatio = (double) compressedSize / originalSize;
        this.compressed = true;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Decompress the subgraph.
     * In a real implementation, this would restore compressed data.
     */
    public void decompress() {
        if (!compressed) {
            return;
        }
        
        // Placeholder for decompression logic
        // In a real implementation, this would restore the original data
        this.compressed = false;
        this.compressionRatio = 1.0;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Find a node with a specific pattern.
     * Returns null if not found in this subtree.
     */
    public FractalNode findPattern(FractalPattern targetPattern) {
        // Check if this node matches the pattern
        if (pattern != null && pattern.similarity(targetPattern) > 0.8) {
            return this;
        }
        
        // Recursively search children (would need node registry)
        // For now, return null
        return null;
    }
    
    /**
     * Check if this is a leaf node.
     */
    public boolean isLeaf() {
        return childIds.isEmpty();
    }
    
    /**
     * Check if this is the root node.
     */
    public boolean isRoot() {
        return parentId == null;
    }
    
    /**
     * Get the depth of this node in the tree.
     */
    public int getDepth() {
        if (isRoot()) {
            return 0;
        }
        // In a real implementation, we would traverse up to root
        // For now, return -1 to indicate unknown
        return -1;
    }
    
    // Getters and setters
    public UUID getNodeId() { return nodeId; }
    public UUID getParentId() { return parentId; }
    public List<UUID> getChildIds() { return new ArrayList<>(childIds); }
    public KnowledgeGraph getSubgraph() { return subgraph; }
    public FractalPattern getPattern() { return pattern; }
    public double getCompressionRatio() { return compressionRatio; }
    public long getTimestamp() { return timestamp; }
    public boolean isCompressed() { return compressed; }
    
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public void setSubgraph(KnowledgeGraph subgraph) { this.subgraph = subgraph; }
    public void setPattern(FractalPattern pattern) { this.pattern = pattern; }
    public void setCompressionRatio(double compressionRatio) { this.compressionRatio = compressionRatio; }
    public void setCompressed(boolean compressed) { this.compressed = compressed; }
    
    @Override
    public String toString() {
        return "FractalNode{" +
                "nodeId=" + nodeId +
                ", parentId=" + parentId +
                ", children=" + childIds.size() +
                ", compressed=" + compressed +
                ", compressionRatio=" + compressionRatio +
                '}';
    }
}
