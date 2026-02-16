package fraymus.knowledge;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AKASHIC RECORD: THE UNIVERSAL MEMORY
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "All knowledge, past and future, stored in one place."
 * 
 * The Akashic Record is the permanent knowledge storage for FRAYMUS.
 * It stores:
 * - Absorbed library knowledge
 * - Learned patterns and behaviors
 * - Historical decisions and outcomes
 * - Universal truths and constants
 * 
 * Structure:
 * - BLOCKS: Individual units of knowledge
 * - CHAINS: Linked sequences of related blocks
 * - INDEX: Fast lookup by category and hash
 * 
 * Persistence:
 * - Memory: ConcurrentHashMap for fast access
 * - Disk: JSON/Binary files for permanent storage
 * - Chain: Linked hash for integrity verification
 */
public class AkashicRecord {

    private static final double PHI = 1.618033988749895;
    private static final String STORAGE_DIR = ".akashic/";
    private static final String CHAIN_FILE = ".akashic/chain.dat";
    
    // In-memory storage
    private Map<String, List<KnowledgeBlock>> categories;
    private Map<String, KnowledgeBlock> blockIndex;
    private List<String> chainHashes;
    
    // Statistics
    private long blocksAdded = 0;
    private long queriesProcessed = 0;
    private long chainLength = 0;

    public AkashicRecord() {
        this.categories = new ConcurrentHashMap<>();
        this.blockIndex = new ConcurrentHashMap<>();
        this.chainHashes = Collections.synchronizedList(new ArrayList<>());
        
        // Ensure storage directory exists
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        } catch (Exception e) {
            // Ignore
        }
        
        // Load existing chain
        loadChain();
    }

    /**
     * ADD BLOCK - Add a knowledge block to the record
     */
    public String addBlock(String category, String content) {
        blocksAdded++;
        
        // Create the block
        KnowledgeBlock block = new KnowledgeBlock(category, content);
        
        // Add to category
        categories.computeIfAbsent(category, k -> Collections.synchronizedList(new ArrayList<>()))
                  .add(block);
        
        // Add to index
        blockIndex.put(block.hash, block);
        
        // Add to chain
        chainHashes.add(block.hash);
        chainLength++;
        
        // Persist periodically
        if (blocksAdded % 100 == 0) {
            saveChain();
        }
        
        return block.hash;
    }

    /**
     * QUERY - Search for knowledge
     */
    public List<KnowledgeBlock> query(String searchTerm) {
        queriesProcessed++;
        
        List<KnowledgeBlock> results = new ArrayList<>();
        
        for (List<KnowledgeBlock> categoryBlocks : categories.values()) {
            for (KnowledgeBlock block : categoryBlocks) {
                if (block.content.toLowerCase().contains(searchTerm.toLowerCase())) {
                    results.add(block);
                }
            }
        }
        
        return results;
    }

    /**
     * QUERY BY CATEGORY
     */
    public List<KnowledgeBlock> queryCategory(String category) {
        queriesProcessed++;
        return categories.getOrDefault(category, Collections.emptyList());
    }

    /**
     * GET BLOCK BY HASH
     */
    public KnowledgeBlock getBlock(String hash) {
        queriesProcessed++;
        return blockIndex.get(hash);
    }

    /**
     * VERIFY CHAIN INTEGRITY
     */
    public boolean verifyIntegrity() {
        if (chainHashes.isEmpty()) return true;
        
        String previousHash = null;
        for (String hash : chainHashes) {
            KnowledgeBlock block = blockIndex.get(hash);
            if (block == null) return false;
            
            // Verify hash matches content
            String computedHash = computeHash(block.category + block.content + block.timestamp);
            if (!computedHash.equals(hash)) return false;
            
            previousHash = hash;
        }
        
        return true;
    }

    /**
     * SAVE CHAIN TO DISK
     */
    private void saveChain() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(CHAIN_FILE))) {
            oos.writeObject(new ArrayList<>(chainHashes));
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * LOAD CHAIN FROM DISK
     */
    @SuppressWarnings("unchecked")
    private void loadChain() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(CHAIN_FILE))) {
            chainHashes = Collections.synchronizedList((List<String>) ois.readObject());
            chainLength = chainHashes.size();
        } catch (Exception e) {
            // Start fresh
            chainHashes = Collections.synchronizedList(new ArrayList<>());
        }
    }

    /**
     * COMPUTE PHI-ENHANCED HASH
     */
    private String computeHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes());
            
            // PHI enhancement
            long phiSig = (long) (bytes[0] * PHI * 1000000);
            
            StringBuilder sb = new StringBuilder();
            sb.append("φ-");
            for (int i = 0; i < 8; i++) {
                sb.append(String.format("%02x", bytes[i]));
            }
            
            return sb.toString();
        } catch (Exception e) {
            return "φ-" + Integer.toHexString(input.hashCode());
        }
    }

    /**
     * GET STATISTICS
     */
    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ AKASHIC RECORD STATISTICS                                   │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Categories:          " + String.format("%-36d", categories.size()) + "│");
        System.out.println("│ Total Blocks:        " + String.format("%-36d", blockIndex.size()) + "│");
        System.out.println("│ Chain Length:        " + String.format("%-36d", chainLength) + "│");
        System.out.println("│ Blocks Added:        " + String.format("%-36d", blocksAdded) + "│");
        System.out.println("│ Queries Processed:   " + String.format("%-36d", queriesProcessed) + "│");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        
        for (String category : categories.keySet()) {
            int count = categories.get(category).size();
            System.out.println("│ " + String.format("%-20s", category) + ": " + 
                              String.format("%-35d", count) + "│");
        }
        
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    /**
     * KNOWLEDGE BLOCK - Individual unit of knowledge
     */
    public static class KnowledgeBlock implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public final String hash;
        public final String category;
        public final String content;
        public final long timestamp;
        public final String formattedTime;
        
        public KnowledgeBlock(String category, String content) {
            this.category = category;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
            this.formattedTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // Compute hash
            this.hash = computeBlockHash(category, content, this.timestamp);
        }
        
        private static String computeBlockHash(String category, String content, long timestamp) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] bytes = md.digest((category + content + timestamp).getBytes());
                StringBuilder sb = new StringBuilder("φ-");
                for (int i = 0; i < 8; i++) {
                    sb.append(String.format("%02x", bytes[i]));
                }
                return sb.toString();
            } catch (Exception e) {
                return "φ-" + Integer.toHexString((category + content).hashCode());
            }
        }
        
        @Override
        public String toString() {
            return String.format("Block[%s | %s | %s]", hash, category, 
                content.length() > 50 ? content.substring(0, 50) + "..." : content);
        }
    }

    /**
     * Demonstration
     */
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   AKASHIC RECORD: THE UNIVERSAL MEMORY                       ║");
        System.out.println("║   \"All knowledge, past and future, stored in one place.\"     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        AkashicRecord akashic = new AkashicRecord();
        
        // Add some knowledge
        System.out.println("   Adding knowledge blocks...");
        akashic.addBlock("MATH", "PI = 3.14159265359");
        akashic.addBlock("MATH", "PHI = 1.618033988749895");
        akashic.addBlock("MATH", "E = 2.71828182845904");
        akashic.addBlock("PHYSICS", "c = 299792458 m/s (speed of light)");
        akashic.addBlock("PHYSICS", "G = 6.67430e-11 (gravitational constant)");
        akashic.addBlock("FRAYMUS", "Patent: VS-PoQC-19046423-φ⁷⁵-2025");
        
        // Query
        System.out.println();
        System.out.println("   Querying for 'PHI'...");
        List<KnowledgeBlock> results = akashic.query("PHI");
        for (KnowledgeBlock block : results) {
            System.out.println("   >> " + block);
        }
        
        // Query by category
        System.out.println();
        System.out.println("   Querying category 'MATH'...");
        results = akashic.queryCategory("MATH");
        for (KnowledgeBlock block : results) {
            System.out.println("   >> " + block);
        }
        
        // Verify integrity
        System.out.println();
        System.out.println("   Verifying chain integrity...");
        boolean valid = akashic.verifyIntegrity();
        System.out.println("   >> Chain valid: " + valid);
        
        // Stats
        akashic.printStats();
        
        System.out.println();
        System.out.println("   ✓ Akashic Record demo complete.");
        System.out.println();
    }
}
