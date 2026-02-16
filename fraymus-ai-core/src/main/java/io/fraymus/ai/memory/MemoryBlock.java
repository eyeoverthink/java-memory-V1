package io.fraymus.ai.memory;

import java.io.Serializable;
import java.security.MessageDigest;

/**
 * MEMORY BLOCK - Immutable Memory Unit
 * 
 * Hash-chained block for blockchain-style memory
 */
public class MemoryBlock implements Serializable {
    private static final long serialVersionUID = 1L;

    public String hash;
    public String previousHash;
    public String type;
    public String data;
    public long timestamp;

    public MemoryBlock(String previousHash, String type, String data) {
        this.previousHash = previousHash;
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            String input = previousHash + timestamp + data;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
