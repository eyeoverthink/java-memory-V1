package brain_architecture.persistence;

import brain_architecture.synapses.KnowledgeGraph;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PersistentMemory - Multi-tier storage system for knowledge graphs.
 * Coordinates JSON, blockchain, and fractal node storage.
 */
public class PersistentMemory {
    private Map<UUID, KnowledgeGraph> knowledgeStore;
    private BlockChain blockchain;
    private Map<UUID, FractalNode> fractalNodes;
    private MerkleTree merkleTree;
    private Path storagePath;
    private boolean initialized;
    
    /**
     * Constructor with default storage path.
     */
    public PersistentMemory() {
        this(null);
    }
    
    /**
     * Constructor with custom storage path.
     */
    public PersistentMemory(Path storagePath) {
        this.knowledgeStore = new ConcurrentHashMap<>();
        this.blockchain = new BlockChain(3);
        this.fractalNodes = new ConcurrentHashMap<>();
        this.merkleTree = new MerkleTree();
        this.storagePath = storagePath != null ? storagePath : Path.of(".brain_memory");
        this.initialized = false;
    }
    
    /**
     * Initialize the persistence layer.
     */
    public void initialize() {
        try {
            // Create storage directory if it doesn't exist
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }
            
            // Load existing data if available
            loadFromStorage();
            
            this.initialized = true;
        } catch (IOException e) {
            System.err.println("Failed to initialize PersistentMemory: " + e.getMessage());
            this.initialized = false;
        }
    }
    
    /**
     * Store a knowledge graph.
     */
    public UUID store(KnowledgeGraph graph) {
        if (!initialized) {
            throw new IllegalStateException("PersistentMemory not initialized");
        }
        
        UUID graphId = UUID.randomUUID();
        knowledgeStore.put(graphId, graph);
        
        // Create delta for blockchain
        KnowledgeGraphDelta delta = new KnowledgeGraphDelta(graphId, KnowledgeGraphDelta.DeltaType.ADD);
        for (var concept : graph.getConcepts()) {
            delta.addConcept(concept);
        }
        
        // Append to blockchain
        if (blockchain.hasGenesisBlock()) {
            blockchain.addBlock(delta);
        } else {
            blockchain.createGenesisBlock(delta);
        }
        
        // Update Merkle tree
        rebuildMerkleTree();
        
        // Persist to JSON
        try {
            exportToJSON(storagePath.resolve(graphId + ".json"));
        } catch (IOException e) {
            System.err.println("Failed to export to JSON: " + e.getMessage());
        }
        
        return graphId;
    }
    
    /**
     * Retrieve a knowledge graph by ID.
     */
    public KnowledgeGraph retrieve(UUID graphId) {
        if (!initialized) {
            throw new IllegalStateException("PersistentMemory not initialized");
        }
        
        return knowledgeStore.get(graphId);
    }
    
    /**
     * Update an existing knowledge graph.
     */
    public void update(UUID graphId, KnowledgeGraph graph) {
        if (!initialized) {
            throw new IllegalStateException("PersistentMemory not initialized");
        }
        
        if (!knowledgeStore.containsKey(graphId)) {
            throw new IllegalArgumentException("Graph ID not found: " + graphId);
        }
        
        // Calculate delta
        KnowledgeGraph oldGraph = knowledgeStore.get(graphId);
        KnowledgeGraphDelta delta = new KnowledgeGraphDelta(graphId, KnowledgeGraphDelta.DeltaType.MODIFY);
        
        // Find added/modified concepts
        for (var concept : graph.getConcepts()) {
            delta.modifyConcept(concept);
        }
        
        knowledgeStore.put(graphId, graph);
        
        // Append to blockchain
        blockchain.addBlock(delta);
        
        // Update Merkle tree
        rebuildMerkleTree();
        
        // Persist to JSON
        try {
            exportToJSON(storagePath.resolve(graphId + ".json"));
        } catch (IOException e) {
            System.err.println("Failed to export to JSON: " + e.getMessage());
        }
    }
    
    /**
     * Delete a knowledge graph.
     */
    public void delete(UUID graphId) {
        if (!initialized) {
            throw new IllegalStateException("PersistentMemory not initialized");
        }
        
        KnowledgeGraph removed = knowledgeStore.remove(graphId);
        
        if (removed != null) {
            // Create delta for blockchain
            KnowledgeGraphDelta delta = new KnowledgeGraphDelta(graphId, KnowledgeGraphDelta.DeltaType.REMOVE);
            for (var concept : removed.getConcepts()) {
                delta.removeConcept(concept.getConceptId());
            }
            
            // Append to blockchain
            blockchain.addBlock(delta);
            
            // Update Merkle tree
            rebuildMerkleTree();
            
            // Delete JSON file
            try {
                Files.deleteIfExists(storagePath.resolve(graphId + ".json"));
            } catch (IOException e) {
                System.err.println("Failed to delete JSON file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Create genesis block with initial knowledge graph.
     */
    public void createGenesisBlock(KnowledgeGraph initialGraph) {
        if (!initialized) {
            throw new IllegalStateException("PersistentMemory not initialized");
        }
        
        if (blockchain.hasGenesisBlock()) {
            throw new IllegalStateException("Genesis block already exists");
        }
        
        UUID graphId = store(initialGraph);
        // The store method already creates the genesis block
    }
    
    /**
     * Append a delta to the blockchain.
     */
    public void appendBlock(KnowledgeGraphDelta delta) {
        if (!initialized) {
            throw new IllegalStateException("PersistentMemory not initialized");
        }
        
        if (!blockchain.hasGenesisBlock()) {
            throw new IllegalStateException("Genesis block not created");
        }
        
        blockchain.addBlock(delta);
    }
    
    /**
     * Get the blockchain.
     */
    public BlockChain getBlockChain() {
        return blockchain;
    }
    
    /**
     * Verify blockchain integrity.
     */
    public boolean verifyIntegrity() {
        return blockchain.isChainValid();
    }
    
    /**
     * Create a fractal node from a knowledge graph.
     */
    public FractalNode createFractalNode(KnowledgeGraph subgraph) {
        if (!initialized) {
            throw new IllegalStateException("PersistentMemory not initialized");
        }
        
        FractalPattern pattern = extractPattern(subgraph);
        FractalNode node = new FractalNode(null, subgraph, pattern);
        fractalNodes.put(node.getNodeId(), node);
        
        return node;
    }
    
    /**
     * Get a fractal node by ID.
     */
    public FractalNode getFractalNode(UUID nodeId) {
        return fractalNodes.get(nodeId);
    }
    
    /**
     * Get children of a fractal node.
     */
    public List<FractalNode> getChildren(UUID parentNodeId) {
        FractalNode parent = fractalNodes.get(parentNodeId);
        if (parent == null) {
            return Collections.emptyList();
        }
        
        List<FractalNode> children = new ArrayList<>();
        for (UUID childId : parent.getChildIds()) {
            FractalNode child = fractalNodes.get(childId);
            if (child != null) {
                children.add(child);
            }
        }
        
        return children;
    }
    
    /**
     * Sync with remote storage (placeholder).
     */
    public void syncWithRemote(String remoteUrl) {
        // Placeholder for remote sync implementation
        System.out.println("Sync with remote: " + remoteUrl + " (not implemented)");
    }
    
    /**
     * Export all knowledge graphs to JSON.
     */
    public void exportToJSON(Path outputPath) throws IOException {
        // Simple JSON export (in real implementation, use a JSON library)
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"graphs\": [\n");
        
        int index = 0;
        for (Map.Entry<UUID, KnowledgeGraph> entry : knowledgeStore.entrySet()) {
            if (index > 0) json.append(",\n");
            json.append("    {\n");
            json.append("      \"id\": \"").append(entry.getKey()).append("\",\n");
            json.append("      \"source\": \"").append(entry.getValue().getSource()).append("\",\n");
            json.append("      \"conceptCount\": ").append(entry.getValue().getConceptCount()).append(",\n");
            json.append("      \"timestamp\": ").append(entry.getValue().getTimestamp()).append("\n");
            json.append("    }");
            index++;
        }
        
        json.append("\n  ],\n");
        json.append("  \"blockchainSize\": ").append(blockchain.getSize()).append(",\n");
        json.append("  \"merkleRoot\": \"").append(merkleTree.getRootHash()).append("\"\n");
        json.append("}\n");
        
        Files.writeString(outputPath, json.toString());
    }
    
    /**
     * Import knowledge graphs from JSON (placeholder).
     */
    public void importFromJSON(Path inputPath) throws IOException {
        // Placeholder for JSON import implementation
        System.out.println("Import from JSON: " + inputPath + " (not implemented)");
    }
    
    /**
     * Query knowledge graphs by text (simple search).
     */
    public List<KnowledgeGraph> query(String query) {
        List<KnowledgeGraph> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (KnowledgeGraph graph : knowledgeStore.values()) {
            if (graph.getSource().toLowerCase().contains(lowerQuery)) {
                results.add(graph);
            }
        }
        
        return results;
    }
    
    /**
     * Query knowledge graphs by timestamp range.
     */
    public KnowledgeGraph queryByTimestamp(long timestamp) {
        // Find the graph closest to the timestamp
        KnowledgeGraph closest = null;
        long minDiff = Long.MAX_VALUE;
        
        for (KnowledgeGraph graph : knowledgeStore.values()) {
            long diff = Math.abs(graph.getTimestamp() - timestamp);
            if (diff < minDiff) {
                minDiff = diff;
                closest = graph;
            }
        }
        
        return closest;
    }
    
    /**
     * Rebuild the Merkle tree from current knowledge store.
     */
    private void rebuildMerkleTree() {
        List<KnowledgeGraph> graphs = new ArrayList<>(knowledgeStore.values());
        merkleTree.build(graphs);
    }
    
    /**
     * Extract a fractal pattern from a knowledge graph.
     */
    private FractalPattern extractPattern(KnowledgeGraph graph) {
        // Simple pattern extraction based on concept strengths
        List<Double> signature = new ArrayList<>();
        
        for (var concept : graph.getConcepts()) {
            signature.add(concept.getStrength());
        }
        
        return new FractalPattern("extracted", signature);
    }
    
    /**
     * Load data from storage on initialization.
     */
    private void loadFromStorage() throws IOException {
        // Load JSON files from storage path
        if (Files.exists(storagePath)) {
            try (var stream = Files.list(storagePath)) {
                stream.filter(path -> path.toString().endsWith(".json"))
                      .forEach(this::loadJSONFile);
            }
        }
    }
    
    /**
     * Load a single JSON file (placeholder).
     */
    private void loadJSONFile(Path jsonPath) {
        // Placeholder for JSON file loading
        System.out.println("Loading JSON file: " + jsonPath + " (not implemented)");
    }
    
    /**
     * Shutdown the persistence layer.
     */
    public void shutdown() {
        try {
            // Export all data to JSON
            exportToJSON(storagePath.resolve("full_export.json"));
            System.out.println("PersistentMemory shutdown complete");
        } catch (IOException e) {
            System.err.println("Failed to export on shutdown: " + e.getMessage());
        }
    }
    
    /**
     * Get statistics.
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("initialized", initialized);
        stats.put("knowledgeGraphCount", knowledgeStore.size());
        stats.put("blockchainSize", blockchain.getSize());
        stats.put("fractalNodeCount", fractalNodes.size());
        stats.put("merkleTreeBuilt", merkleTree.isBuilt());
        stats.put("merkleRoot", merkleTree.getRootHash());
        stats.put("storagePath", storagePath.toString());
        return stats;
    }
    
    /**
     * Check if initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }
}
