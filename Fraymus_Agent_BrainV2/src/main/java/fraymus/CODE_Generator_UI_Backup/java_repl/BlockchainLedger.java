/**
 * BlockchainLedger.java - Immutable Memory Blockchain
 * 
 * Genesis blocks for immortal memory storage.
 * Each memory is a block in an immutable chain.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

public class BlockchainLedger {
    
    private static final String BLOCKCHAIN_DIR = "memory/blockchain";
    private List<Block> chain;
    private Map<String, String> keyValueIndex;
    
    public BlockchainLedger() {
        this.chain = new ArrayList<>();
        this.keyValueIndex = new HashMap<>();
        loadChain();
    }
    
    /**
     * Add block to chain.
     */
    public String addBlock(String timestamp, String type, String key, String value) {
        try {
            String previousHash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).hash;
            Block block = new Block(chain.size(), timestamp, type, key, value, previousHash);
            chain.add(block);
            keyValueIndex.put(key, value);
            
            // Persist block
            saveBlock(block);
            
            return block.hash;
            
        } catch (Exception e) {
            System.err.println("Blockchain add failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get value by key.
     */
    public String getValue(String key) {
        return keyValueIndex.get(key);
    }
    
    /**
     * Save block to file.
     */
    private void saveBlock(Block block) throws IOException {
        String filename = String.format("%s/block_%06d.json", BLOCKCHAIN_DIR, block.index);
        Files.writeString(Paths.get(filename), block.toJSON());
    }
    
    /**
     * Load chain from disk.
     */
    private void loadChain() {
        try {
            Files.createDirectories(Paths.get(BLOCKCHAIN_DIR));
            File dir = new File(BLOCKCHAIN_DIR);
            File[] files = dir.listFiles((d, name) -> name.startsWith("block_"));
            
            if (files != null) {
                Arrays.sort(files);
                for (File file : files) {
                    String json = Files.readString(file.toPath());
                    Block block = Block.fromJSON(json);
                    chain.add(block);
                    keyValueIndex.put(block.key, block.value);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Blockchain load failed: " + e.getMessage());
        }
    }
    
    /**
     * Block in the chain.
     */
    static class Block {
        int index;
        String timestamp;
        String type;
        String key;
        String value;
        String previousHash;
        String hash;
        
        public Block(int index, String timestamp, String type, String key, String value, String previousHash) {
            this.index = index;
            this.timestamp = timestamp;
            this.type = type;
            this.key = key;
            this.value = value;
            this.previousHash = previousHash;
            this.hash = calculateHash();
        }
        
        private String calculateHash() {
            try {
                String data = index + timestamp + type + key + value + previousHash;
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(data.getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (Exception e) {
                return "0";
            }
        }
        
        public String toJSON() {
            return String.format(
                "{\"index\":%d,\"timestamp\":\"%s\",\"type\":\"%s\",\"key\":\"%s\",\"value\":\"%s\",\"previousHash\":\"%s\",\"hash\":\"%s\"}",
                index, timestamp, type, escape(key), escape(value), previousHash, hash
            );
        }
        
        public static Block fromJSON(String json) {
            // Simplified parser
            Block block = new Block(0, "", "", "", "", "");
            // Would parse JSON properly in production
            return block;
        }
        
        private String escape(String str) {
            return str.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
