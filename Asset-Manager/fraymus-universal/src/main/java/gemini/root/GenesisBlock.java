package gemini.root;

import java.io.Serializable;
import java.security.MessageDigest;

/**
 * GENESIS BLOCK: Immutable memory unit.
 * 
 * Features:
 * - SHA-256 hash for integrity
 * - Chain linkage via previousHash
 * - Type classification (CONVERSATION, CODE, FACT)
 */
public class GenesisBlock implements Serializable {
    private static final long serialVersionUID = 1L;

    public String hash;
    public String previousHash;
    public String type;     // "CONVERSATION", "CODE", "FACT", "EVOLUTION"
    public String data;
    public long timestamp;

    public GenesisBlock(String previousHash, String type, String data) {
        this.previousHash = previousHash;
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            String input = previousHash + timestamp + type + "\n" + data;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] h = digest.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
