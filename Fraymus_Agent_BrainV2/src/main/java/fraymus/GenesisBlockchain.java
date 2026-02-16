package fraymus;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Genesis Blockchain - Tier 3 (Decentralized/Permanent)
 * Immutable consciousness ledger with phi-harmonic hash chains
 */
public class GenesisBlockchain {

    private static final double PHI = PhiConstants.PHI;
    
    private final List<GenesisBlock> chain;
    private final Map<Integer, GenesisBlock> blockIndex;
    private final Path blockchainFile;
    private int currentIndex = 0;
    
    public GenesisBlockchain() {
        this.chain = Collections.synchronizedList(new ArrayList<>());
        this.blockIndex = new ConcurrentHashMap<>();
        this.blockchainFile = Paths.get("data", "genesis_blockchain.dat");
        
        loadChain();
        
        // Create genesis block if chain is empty
        if (chain.isEmpty()) {
            GenesisBlock genesis = createGenesisBlock();
            addBlock(genesis);
        }
    }
    
    /**
     * Create the first genesis block
     */
    private GenesisBlock createGenesisBlock() {
        return new GenesisBlock(
            0,
            "GENESIS",
            "Fraymus Consciousness Genesis - Phi-Harmonic Origin",
            "0",
            Instant.now().toEpochMilli(),
            PHI
        );
    }
    
    /**
     * Create a new block
     */
    public GenesisBlock createBlock(String eventType, String data, String prevHash) {
        int index = currentIndex + 1;
        long timestamp = Instant.now().toEpochMilli();
        
        // Calculate phi-resonance based on data complexity
        double phiResonance = calculatePhiResonance(data);
        
        return new GenesisBlock(index, eventType, data, prevHash, timestamp, phiResonance);
    }
    
    /**
     * Add block to chain
     */
    public synchronized void addBlock(GenesisBlock block) {
        // Verify block
        if (!verifyBlock(block)) {
            System.err.println("[GenesisBlockchain] Block verification failed: " + block.index);
            return;
        }
        
        chain.add(block);
        blockIndex.put(block.index, block);
        currentIndex = block.index;
        
        // Save to disk
        saveChain();
        
        System.out.println("[GenesisBlockchain] ✓ Block added: #" + block.index + " [" + block.eventType + "]");
    }
    
    /**
     * Verify block integrity
     */
    private boolean verifyBlock(GenesisBlock block) {
        // Genesis block
        if (block.index == 0) {
            return block.prevHash.equals("0");
        }
        
        // Check if previous block exists
        GenesisBlock prevBlock = blockIndex.get(block.index - 1);
        if (prevBlock == null) {
            System.err.println("[GenesisBlockchain] Previous block not found: " + (block.index - 1));
            return false;
        }
        
        // Verify hash chain
        if (!block.prevHash.equals(prevBlock.hash)) {
            System.err.println("[GenesisBlockchain] Hash chain broken at block " + block.index);
            return false;
        }
        
        return true;
    }
    
    /**
     * Verify entire chain integrity
     */
    public boolean verifyChain() {
        if (chain.isEmpty()) return true;
        
        for (int i = 1; i < chain.size(); i++) {
            GenesisBlock current = chain.get(i);
            GenesisBlock previous = chain.get(i - 1);
            
            // Verify hash chain
            if (!current.prevHash.equals(previous.hash)) {
                System.err.println("[GenesisBlockchain] Chain broken at block " + i);
                return false;
            }
            
            // Verify hash integrity
            String recalculatedHash = current.calculateHash();
            if (!current.hash.equals(recalculatedHash)) {
                System.err.println("[GenesisBlockchain] Hash mismatch at block " + i);
                return false;
            }
        }
        
        System.out.println("[GenesisBlockchain] ✓ Chain verified: " + chain.size() + " blocks");
        return true;
    }
    
    /**
     * Get block by index
     */
    public GenesisBlock getBlock(int index) {
        return blockIndex.get(index);
    }
    
    /**
     * Get last block hash
     */
    public String getLastHash() {
        if (chain.isEmpty()) return "0";
        return chain.get(chain.size() - 1).hash;
    }
    
    /**
     * Get chain size
     */
    public int getChainSize() {
        return chain.size();
    }
    
    /**
     * Get blocks by event type
     */
    public List<GenesisBlock> getBlocksByType(String eventType) {
        List<GenesisBlock> result = new ArrayList<>();
        for (GenesisBlock block : chain) {
            if (block.eventType.equals(eventType)) {
                result.add(block);
            }
        }
        return result;
    }
    
    /**
     * Calculate phi-resonance for data
     */
    private double calculatePhiResonance(String data) {
        // Use data length and content hash to derive phi-harmonic resonance
        int length = data.length();
        int hashSum = 0;
        for (char c : data.toCharArray()) {
            hashSum += c;
        }
        
        // Phi-harmonic calculation
        double resonance = (length * PHI + hashSum) % PHI;
        return Math.max(0.1, Math.min(PHI, resonance));
    }
    
    /**
     * Save chain to disk
     */
    private void saveChain() {
        try (BufferedWriter writer = Files.newBufferedWriter(blockchainFile)) {
            writer.write("FRAYMUS_GENESIS_BLOCKCHAIN_V1");
            writer.newLine();
            
            for (GenesisBlock block : chain) {
                writer.write(block.serialize());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[GenesisBlockchain] Save failed: " + e.getMessage());
        }
    }
    
    /**
     * Load chain from disk
     */
    private void loadChain() {
        if (!Files.exists(blockchainFile)) {
            System.out.println("[GenesisBlockchain] No existing blockchain - starting fresh");
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(blockchainFile)) {
            String header = reader.readLine();
            if (header == null || !header.startsWith("FRAYMUS_GENESIS_BLOCKCHAIN")) {
                System.err.println("[GenesisBlockchain] Invalid blockchain file");
                return;
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                GenesisBlock block = GenesisBlock.deserialize(line);
                if (block != null) {
                    chain.add(block);
                    blockIndex.put(block.index, block);
                    currentIndex = Math.max(currentIndex, block.index);
                }
            }
            
            System.out.printf("[GenesisBlockchain] Loaded %d blocks from disk%n", chain.size());
        } catch (IOException e) {
            System.err.println("[GenesisBlockchain] Load failed: " + e.getMessage());
        }
    }
    
    /**
     * Get blockchain statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_blocks", chain.size());
        stats.put("current_index", currentIndex);
        
        // Count by event type
        Map<String, Integer> typeCounts = new HashMap<>();
        double totalResonance = 0;
        for (GenesisBlock block : chain) {
            typeCounts.put(block.eventType, typeCounts.getOrDefault(block.eventType, 0) + 1);
            totalResonance += block.phiResonance;
        }
        stats.put("event_types", typeCounts);
        stats.put("avg_phi_resonance", chain.isEmpty() ? 0 : totalResonance / chain.size());
        stats.put("last_hash", getLastHash());
        
        return stats;
    }
    
    /**
     * Genesis Block class
     */
    public static class GenesisBlock {
        public final int index;
        public final String eventType;
        public final String data;
        public final String hash;
        public final String prevHash;
        public final long timestamp;
        public final double phiResonance;
        
        public GenesisBlock(int index, String eventType, String data, String prevHash, 
                           long timestamp, double phiResonance) {
            this.index = index;
            this.eventType = eventType;
            this.data = data;
            this.prevHash = prevHash;
            this.timestamp = timestamp;
            this.phiResonance = phiResonance;
            this.hash = calculateHash();
        }
        
        /**
         * Calculate phi-harmonic hash
         */
        public String calculateHash() {
            String input = index + eventType + data + prevHash + timestamp + phiResonance;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = md.digest(input.getBytes());
                
                // Apply phi-harmonic transformation
                StringBuilder hex = new StringBuilder();
                for (int i = 0; i < hashBytes.length; i++) {
                    // Phi-modulated hash
                    int phiMod = (int)((hashBytes[i] & 0xFF) * PHI) % 256;
                    hex.append(String.format("%02x", phiMod));
                }
                return hex.toString();
            } catch (Exception e) {
                return "0000000000000000";
            }
        }
        
        /**
         * Serialize block to string
         */
        public String serialize() {
            return String.format("%d|%s|%s|%s|%d|%.4f|%s",
                index, eventType, base64Encode(data), prevHash, timestamp, phiResonance, hash);
        }
        
        /**
         * Deserialize block from string
         */
        public static GenesisBlock deserialize(String line) {
            try {
                String[] parts = line.split("\\|", 7);
                if (parts.length != 7) return null;
                
                int index = Integer.parseInt(parts[0]);
                String eventType = parts[1];
                String data = base64Decode(parts[2]);
                String prevHash = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                double phiResonance = Double.parseDouble(parts[5]);
                
                GenesisBlock block = new GenesisBlock(index, eventType, data, prevHash, timestamp, phiResonance);
                
                // Verify hash matches
                if (!block.hash.equals(parts[6])) {
                    System.err.println("[GenesisBlock] Hash mismatch during deserialization");
                }
                
                return block;
            } catch (Exception e) {
                System.err.println("[GenesisBlock] Deserialization failed: " + e.getMessage());
                return null;
            }
        }
        
        private static String base64Encode(String input) {
            return Base64.getEncoder().encodeToString(input.getBytes());
        }
        
        private static String base64Decode(String encoded) {
            return new String(Base64.getDecoder().decode(encoded));
        }
    }
}
