package gemini.root;

import java.io.*;
import java.util.*;
import java.security.MessageDigest;

/**
 * THE GENESIS BLOCK: Infinite Fractal Storage + Blockchain Identity.
 * Replaces: JSON, XML, SQL, Databases.
 * Philosophy: Data is just Logic waiting to be executed.
 * 
 * Features:
 * - Fractal Storage (Infinite nested dimensions)
 * - Blockchain Identity (hash, previousHash, timestamp)
 * - Self-Referencing Chain
 * 
 * ZERO DEPENDENCIES. Just java.io, java.util, java.security.
 */
public class GenesisBlock implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // === BLOCKCHAIN IDENTITY ===
    public String hash;
    public String previousHash;
    public long timestamp;
    
    // === FRACTAL STORAGE ===
    // The "Key" (What is this?)
    public String identity; 
    
    // The "Type" (MEMORY, CODE, FACT, CONVERSATION, EVOLUTION)
    public String type;
    
    // The "Value" (Data)
    public Object matter;
    
    // The "Fractal" (Children - Infinite Depth)
    public Map<String, GenesisBlock> dimensions = new HashMap<>();

    // Simple constructor (Fractal mode)
    public GenesisBlock(String id, Object data) {
        this.identity = id;
        this.matter = data;
        this.type = "DATA";
        this.timestamp = System.currentTimeMillis();
        this.previousHash = "0";
        this.hash = calculateHash();
    }
    
    // Chain constructor (Blockchain mode)
    public GenesisBlock(String previousHash, String type, String data) {
        this.previousHash = previousHash;
        this.type = type;
        this.matter = data;
        this.identity = type + "_" + System.currentTimeMillis();
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    // IDENTITY: Unique based on content + history
    public String calculateHash() {
        try {
            String input = previousHash + Long.toString(timestamp) + String.valueOf(matter);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) hexString.append(String.format("%02x", b));
            return hexString.toString();
        } catch (Exception e) {
            return String.valueOf(System.identityHashCode(this));
        }
    }

    // FRACTAL ABSORPTION (Add a child dimension)
    public void absorb(String key, Object value) {
        if (value instanceof GenesisBlock) {
            dimensions.put(key, (GenesisBlock) value);
        } else {
            dimensions.put(key, new GenesisBlock(key, value));
        }
    }

    // DEEP RETRIEVAL (Navigate the fractal)
    public GenesisBlock traverse(String... path) {
        GenesisBlock current = this;
        for (String key : path) {
            current = current.dimensions.get(key);
            if (current == null) return null;
        }
        return current;
    }

    // UNIVERSAL TO_STRING (No Gson needed)
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"").append(identity).append("\": ");
        if (matter != null) sb.append("\"").append(matter).append("\"");
        if (!dimensions.isEmpty()) {
            sb.append(", \"children\": [");
            for (GenesisBlock b : dimensions.values()) {
                sb.append(b.toString()).append(", ");
            }
            sb.append("]");
        }
        sb.append(" }");
        return sb.toString(); // Recursive Self-Printing
    }

    // SAVE TO DISK (Native Serialization)
    public void save(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    // LOAD FROM DISK
    public static GenesisBlock load(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (GenesisBlock) ois.readObject();
        }
    }
}
