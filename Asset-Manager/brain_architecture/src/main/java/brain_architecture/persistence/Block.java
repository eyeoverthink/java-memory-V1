package brain_architecture.persistence;

import brain_architecture.synapses.KnowledgeGraph;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Block - Represents a single block in the blockchain.
 * Each block contains knowledge graph deltas and cryptographic proof.
 */
public class Block {
    private long index;
    private long timestamp;
    private KnowledgeGraphDelta data;
    private String previousHash;
    private String hash;
    private int nonce;
    
    /**
     * Constructor for creating a new block.
     */
    public Block(long index, KnowledgeGraphDelta data, String previousHash) {
        this.index = index;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();
        this.nonce = 0;
    }
    
    /**
     * Calculate the hash of this block.
     */
    public String calculateHash() {
        String input = index + timestamp + (data != null ? data.toString() : "") + previousHash + nonce;
        return applySha256(input);
    }
    
    /**
     * Apply SHA-256 hash function.
     */
    private String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
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
     * Mine the block with specified difficulty.
     * Finds a nonce such that the hash starts with the required number of zeros.
     */
    public void mine(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
    }
    
    /**
     * Validate this block.
     */
    public boolean isValid() {
        String calculatedHash = calculateHash();
        return hash.equals(calculatedHash);
    }
    
    // Getters and setters
    public long getIndex() { return index; }
    public long getTimestamp() { return timestamp; }
    public KnowledgeGraphDelta getData() { return data; }
    public String getPreviousHash() { return previousHash; }
    public String getHash() { return hash; }
    public int getNonce() { return nonce; }
    
    public void setIndex(long index) { this.index = index; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setData(KnowledgeGraphDelta data) { this.data = data; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }
    public void setHash(String hash) { this.hash = hash; }
    public void setNonce(int nonce) { this.nonce = nonce; }
    
    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", previousHash='" + previousHash + '\'' +
                ", hash='" + hash + '\'' +
                ", nonce=" + nonce +
                '}';
    }
}
