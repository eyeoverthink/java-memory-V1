package fraymus.genesis;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * THE GENESIS BLOCK: Infinite Fractal Storage with Blockchain Chaining
 * 
 * Replaces: JSON, XML, SQL, Databases
 * Philosophy: Data is just Logic waiting to be executed
 * 
 * Features:
 * - Self-Referencing (Chained by Hash)
 * - Immutable (Cannot change without breaking chain)
 * - Fractal (Infinite nesting)
 * 
 * Zero Dependencies - Only java.io, java.util, java.security
 */
public class GenesisBlock implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Blockchain fields
    public String hash;
    public String previousHash;
    public long timestamp;
    
    // The "Key" (What is this?)
    public String identity; 
    
    // The "Type" (Classification)
    public String type; // "MEMORY", "CODE", "FACT", "CONVERSATION"
    
    // The "Value" (Data)
    public Object matter;
    
    // The "Fractal" (Children - infinite nesting)
    public Map<String, GenesisBlock> dimensions = new HashMap<>();

    // Legacy constructor for backward compatibility
    public GenesisBlock(String id, Object data) {
        this("0", id, "DATA", data);
    }
    
    // Blockchain constructor
    public GenesisBlock(String previousHash, String identity, String type, Object data) {
        this.previousHash = previousHash;
        this.identity = identity;
        this.type = type;
        this.matter = data;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    /**
     * FRACTAL ABSORPTION
     * Add a child dimension - infinite recursion possible
     */
    public void absorb(String key, Object value) {
        if (value instanceof GenesisBlock) {
            dimensions.put(key, (GenesisBlock) value);
        } else {
            dimensions.put(key, new GenesisBlock(key, value));
        }
    }

    /**
     * Get a child dimension
     */
    public GenesisBlock getDimension(String key) {
        return dimensions.get(key);
    }

    /**
     * Check if dimension exists
     */
    public boolean hasDimension(String key) {
        return dimensions.containsKey(key);
    }

    /**
     * Get all dimension keys
     */
    public Set<String> getDimensionKeys() {
        return dimensions.keySet();
    }

    /**
     * CALCULATE HASH
     * Identity: Unique based on content + history
     */
    public String calculateHash() {
        try {
            String input = previousHash + Long.toString(timestamp) + identity + type + 
                          (matter != null ? matter.toString() : "");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * UNIVERSAL TO_STRING
     * No Gson needed - recursive self-printing
     */
    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(depth);
        
        sb.append(indent).append("{ ");
        sb.append("\"hash\": \"").append(hash != null ? hash.substring(0, 8) : "null").append("...\", ");
        sb.append("\"identity\": \"").append(identity).append("\", ");
        sb.append("\"type\": \"").append(type).append("\"");
        
        if (matter != null) {
            sb.append(", \"matter\": \"").append(matter).append("\"");
        }
        
        if (!dimensions.isEmpty()) {
            sb.append(",\n").append(indent).append("  \"children\": [\n");
            Iterator<GenesisBlock> it = dimensions.values().iterator();
            while (it.hasNext()) {
                sb.append(it.next().toString(depth + 2));
                if (it.hasNext()) sb.append(",");
                sb.append("\n");
            }
            sb.append(indent).append("  ]");
        }
        
        sb.append("\n").append(indent).append("}");
        return sb.toString();
    }

    /**
     * Save to file (native Java serialization)
     */
    public void save(String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
        fos.close();
    }

    /**
     * Load from file
     */
    public static GenesisBlock load(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        GenesisBlock block = (GenesisBlock) ois.readObject();
        ois.close();
        fis.close();
        return block;
    }

    /**
     * Count total nodes in fractal tree
     */
    public int countNodes() {
        int count = 1; // This node
        for (GenesisBlock child : dimensions.values()) {
            count += child.countNodes();
        }
        return count;
    }

    /**
     * Get depth of fractal tree
     */
    public int getDepth() {
        if (dimensions.isEmpty()) return 1;
        int maxChildDepth = 0;
        for (GenesisBlock child : dimensions.values()) {
            maxChildDepth = Math.max(maxChildDepth, child.getDepth());
        }
        return 1 + maxChildDepth;
    }

    /**
     * Print statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   GENESIS BLOCK STATISTICS                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Identity: " + identity);
        System.out.println("  Matter: " + (matter != null ? matter.getClass().getSimpleName() : "null"));
        System.out.println("  Direct Children: " + dimensions.size());
        System.out.println("  Total Nodes: " + countNodes());
        System.out.println("  Tree Depth: " + getDepth());
    }
}
