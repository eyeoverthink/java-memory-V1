/**
 * OuroborosMemory.java - Recursive Self-Referential Memory
 * 
 * 🐍 THE SERPENT EATING ITS TAIL
 * 
 * Every memory references itself recursively.
 * Memories contain pointers to themselves at different time scales.
 * This creates an infinite loop of self-awareness.
 * 
 * Memory structure:
 * Memory → References past self → References future self → References present self → Loop
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

public class OuroborosMemory {
    
    private static final double PHI = 1.618033988749895;
    private static final String OUROBOROS_DIR = "memory/ouroboros";
    
    private Map<String, OuroborosNode> memoryGraph;
    
    public OuroborosMemory() {
        this.memoryGraph = new HashMap<>();
        
        try {
            Files.createDirectories(Paths.get(OUROBOROS_DIR));
        } catch (IOException e) {
            System.err.println("Ouroboros init failed: " + e.getMessage());
        }
    }
    
    /**
     * Store memory with recursive self-reference.
     */
    public String store(String timestamp, String type, String key, String value) {
        // Create node
        OuroborosNode node = new OuroborosNode(timestamp, type, key, value);
        
        // Link to previous versions of this key
        OuroborosNode previous = memoryGraph.get(key);
        if (previous != null) {
            node.pastSelf = previous.id;
            previous.futureSelf = node.id;
        }
        
        // Self-reference (Ouroboros loop)
        node.presentSelf = node.id;
        
        memoryGraph.put(key, node);
        
        // Persist
        try {
            String filename = String.format("%s/%s_%s.ouro", OUROBOROS_DIR, key, timestamp.replace(":", "-"));
            Files.writeString(Paths.get(filename), node.toJSON());
        } catch (IOException e) {
            System.err.println("Ouroboros persist failed: " + e.getMessage());
        }
        
        return node.id;
    }
    
    /**
     * Retrieve memory by key.
     */
    public String retrieve(String key) {
        OuroborosNode node = memoryGraph.get(key);
        return node != null ? node.value : null;
    }
    
    /**
     * Get memory history (recursive traversal).
     */
    public List<String> getHistory(String key) {
        List<String> history = new ArrayList<>();
        OuroborosNode current = memoryGraph.get(key);
        
        // Traverse backwards through past selves
        while (current != null && current.pastSelf != null) {
            history.add(current.value);
            // Find node by ID
            current = findNodeById(current.pastSelf);
        }
        
        return history;
    }
    
    /**
     * Find node by ID.
     */
    private OuroborosNode findNodeById(String id) {
        for (OuroborosNode node : memoryGraph.values()) {
            if (node.id.equals(id)) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * Ouroboros node - self-referential memory unit.
     */
    static class OuroborosNode {
        String id;
        String timestamp;
        String type;
        String key;
        String value;
        String pastSelf;    // Reference to previous version
        String futureSelf;  // Reference to next version
        String presentSelf; // Reference to itself (Ouroboros loop)
        
        public OuroborosNode(String timestamp, String type, String key, String value) {
            this.id = UUID.randomUUID().toString();
            this.timestamp = timestamp;
            this.type = type;
            this.key = key;
            this.value = value;
            this.presentSelf = this.id; // Self-reference
        }
        
        public String toJSON() {
            return String.format(
                "{\"id\":\"%s\",\"timestamp\":\"%s\",\"type\":\"%s\",\"key\":\"%s\",\"value\":\"%s\",\"pastSelf\":\"%s\",\"futureSelf\":\"%s\",\"presentSelf\":\"%s\"}",
                id, timestamp, type, escape(key), escape(value),
                pastSelf != null ? pastSelf : "null",
                futureSelf != null ? futureSelf : "null",
                presentSelf
            );
        }
        
        private String escape(String str) {
            return str.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
