package fraymus.storage;

import fraymus.bio.NeuroQuant;
import java.io.Serializable;
import java.security.MessageDigest;

/**
 * 🔷 HYPER-BLOCK - Content-Addressable Data
 * "Immutable data wrapped in semantic identity."
 * 
 * This is NOT a traditional file.
 * This is a chunk of data with a 10,000D semantic signature.
 * 
 * Properties:
 * 1. SEMANTIC ID: NeuroQuant vector (find by concept, not filename)
 * 2. IMMUTABLE: SHA-256 hash ensures integrity
 * 3. VERIFIABLE: Can't fake it, can't corrupt it
 * 4. ROUTABLE: Automatically finds storage nodes by semantic similarity
 * 
 * Example:
 * - Traditional: "Find file 'quantum_paper.pdf' on server X"
 * - HyperBlock: "Find data about 'Quantum Entanglement'" → Routes to expert nodes
 * 
 * This enables:
 * - Content-addressable storage
 * - Automatic replication to semantic neighbors
 * - Fault-tolerant retrieval (any similar node can serve)
 */
public class HyperBlock implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public final NeuroQuant signature; // The Semantic ID (10,000D vector)
    public final byte[] payload;       // The Actual Data (can be encrypted)
    public final String hash;          // Integrity Check (SHA-256)
    public final long timestamp;       // Creation time
    public final String creator;       // Node that created this block

    /**
     * Create a HyperBlock
     * 
     * @param concept Semantic concept (e.g., "Quantum Encryption Algorithm")
     * @param data The actual data bytes
     * @param creator Node ID that created this
     */
    public HyperBlock(String concept, byte[] data, String creator) {
        this.signature = new NeuroQuant(concept);
        this.payload = data;
        this.hash = hashData(data);
        this.timestamp = System.currentTimeMillis();
        this.creator = creator;
    }
    
    /**
     * Verify integrity
     */
    public boolean verify() {
        return hash.equals(hashData(payload));
    }

    /**
     * Get size in bytes
     */
    public int getSize() {
        return payload.length;
    }

    /**
     * Calculate SHA-256 hash
     */
    private String hashData(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get summary
     */
    public String getSummary() {
        return String.format(
            "HyperBlock[%s, %d bytes, hash=%s..., creator=%s]",
            signature.id,
            payload.length,
            hash.substring(0, Math.min(8, hash.length())),
            creator
        );
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
