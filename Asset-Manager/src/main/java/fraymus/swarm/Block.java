package fraymus.swarm;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Block - Single unit in the Genesis Ledger chain.
 */
public class Block {
    private final int index;
    private final long timestamp;
    private final String type;
    private final String data;
    private final String prevHash;
    private final String hash;
    private final long nonce;
    
    public Block(int index, String type, String data, String prevHash) {
        this.index = index;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.data = data;
        this.prevHash = prevHash;
        this.nonce = (long)(Math.random() * Long.MAX_VALUE);
        this.hash = calculateHash();
    }
    
    private String calculateHash() {
        String input = index + "" + timestamp + type + data + prevHash + nonce;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString().substring(0, 16);
        } catch (Exception e) {
            return Long.toHexString(input.hashCode());
        }
    }
    
    public String getHash() { return hash; }
    public String getPrevHash() { return prevHash; }
    public int getIndex() { return index; }
    public String getType() { return type; }
    public String getData() { return data; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("Block #%d [%s] %s: %s", 
            index, hash.substring(0, 8), type, 
            data.length() > 40 ? data.substring(0, 37) + "..." : data);
    }
}
