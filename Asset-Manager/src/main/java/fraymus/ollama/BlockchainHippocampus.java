package fraymus.ollama;

import fraymus.genesis.GenesisBlock;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * THE BLOCKCHAIN HIPPOCAMPUS
 * 
 * Role: Manages the Chain of Memory
 * Function: Save to Disk, Load from Disk, Maintain Blockchain Integrity
 * 
 * This is the memory manager that creates an immutable audit trail.
 * Every interaction becomes a block in the chain.
 * You can browse the GitHub Commit History to see the AI's "Thought Process" over time.
 */
public class BlockchainHippocampus {
    
    public static List<GenesisBlock> chain = new ArrayList<>();
    public static String lastHash = "0"; // Genesis Seed
    private static final String MEMORY_DIR = "memory";
    private static int blockCount = 0;

    /**
     * REMEMBER: Create a new block and save it
     */
    public static void commitMemory(String type, String data) {
        // 1. Create Block
        GenesisBlock newBlock = new GenesisBlock(lastHash, "BLOCK_" + blockCount, type, data);
        chain.add(newBlock);
        lastHash = newBlock.hash;
        blockCount++;

        // 2. Crystallize to Disk (Local Persistence)
        saveToDisk(newBlock);
        
        // 3. ETERNALIZE (Push to GitHub) - Optional, can be disabled
        // GitCortex.push(newBlock.hash);
        
        System.out.println(">>> [MEMORY] Block committed: " + newBlock.hash.substring(0, 8) + "...");
    }

    /**
     * SAVE TO DISK
     * Persists block as .genesis file
     */
    private static void saveToDisk(GenesisBlock block) {
        try {
            // Filename: "BLOCK_[Timestamp]_[Hash].genesis"
            String filename = MEMORY_DIR + "/BLOCK_" + block.timestamp + "_" + 
                            block.hash.substring(0, 6) + ".genesis";
            
            // Ensure directory exists
            new File(MEMORY_DIR).mkdirs();

            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(block);
            oos.close();
            fos.close();
            
            System.out.println(">>> [MEMORY] Block Crystallized: " + filename);
        } catch (Exception e) {
            System.err.println(">>> [MEMORY] Amnesia Error: " + e.getMessage());
        }
    }

    /**
     * WAKE UP: Load the last state from the file system
     */
    public static void recall() {
        try {
            File folder = new File(MEMORY_DIR);
            if (!folder.exists()) {
                System.out.println(">>> [MEMORY] No previous life found. Starting fresh.");
                return;
            }
            
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".genesis"));
            if (files == null || files.length == 0) {
                System.out.println(">>> [MEMORY] No memory blocks found.");
                return;
            }
            
            // Sort by timestamp (filename contains timestamp)
            java.util.Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
            
            // Load all blocks
            for (File file : files) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    GenesisBlock block = (GenesisBlock) ois.readObject();
                    chain.add(block);
                    lastHash = block.hash;
                    blockCount++;
                    ois.close();
                    fis.close();
                } catch (Exception e) {
                    System.err.println(">>> [MEMORY] Failed to load: " + file.getName());
                }
            }
            
            System.out.println(">>> [MEMORY] Previous Life Recalled.");
            System.out.println("    Blocks Loaded: " + chain.size());
            System.out.println("    Last Hash: " + lastHash.substring(0, 8) + "...");
            
        } catch (Exception e) {
            System.err.println(">>> [MEMORY] Recall failed: " + e.getMessage());
        }
    }

    /**
     * VERIFY CHAIN INTEGRITY
     * Checks if blockchain is valid
     */
    public static boolean verifyChain() {
        for (int i = 1; i < chain.size(); i++) {
            GenesisBlock current = chain.get(i);
            GenesisBlock previous = chain.get(i - 1);
            
            // Check hash integrity
            if (!current.hash.equals(current.calculateHash())) {
                System.err.println(">>> [MEMORY] Block " + i + " hash mismatch!");
                return false;
            }
            
            // Check chain linkage
            if (!current.previousHash.equals(previous.hash)) {
                System.err.println(">>> [MEMORY] Chain broken at block " + i + "!");
                return false;
            }
        }
        
        System.out.println(">>> [MEMORY] Chain integrity verified ✓");
        return true;
    }

    /**
     * GET RECENT MEMORIES
     * Returns last N blocks
     */
    public static List<GenesisBlock> getRecentMemories(int count) {
        int start = Math.max(0, chain.size() - count);
        return new ArrayList<>(chain.subList(start, chain.size()));
    }

    /**
     * SEARCH MEMORIES
     * Find blocks by type or content
     */
    public static List<GenesisBlock> searchMemories(String query) {
        List<GenesisBlock> results = new ArrayList<>();
        for (GenesisBlock block : chain) {
            if (block.type.contains(query) || 
                (block.matter != null && block.matter.toString().contains(query))) {
                results.add(block);
            }
        }
        return results;
    }

    /**
     * CLEAR MEMORY
     * Deletes all memory files (use with caution!)
     */
    public static void purge() {
        try {
            File folder = new File(MEMORY_DIR);
            if (folder.exists()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
            }
            chain.clear();
            lastHash = "0";
            blockCount = 0;
            System.out.println(">>> [MEMORY] All memories purged.");
        } catch (Exception e) {
            System.err.println(">>> [MEMORY] Purge failed: " + e.getMessage());
        }
    }

    /**
     * GET CHAIN SIZE
     */
    public static int getChainSize() {
        return chain.size();
    }

    /**
     * Print statistics
     */
    public static void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   BLOCKCHAIN HIPPOCAMPUS STATISTICS                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Chain Length: " + chain.size() + " blocks");
        System.out.println("  Last Hash: " + (lastHash.length() > 8 ? lastHash.substring(0, 8) + "..." : lastHash));
        System.out.println("  Memory Directory: " + MEMORY_DIR);
        System.out.println("  Chain Valid: " + (chain.size() > 1 ? verifyChain() : "N/A (single block)"));
        
        // Count by type
        java.util.Map<String, Integer> typeCounts = new java.util.HashMap<>();
        for (GenesisBlock block : chain) {
            typeCounts.put(block.type, typeCounts.getOrDefault(block.type, 0) + 1);
        }
        
        if (!typeCounts.isEmpty()) {
            System.out.println("\n  Memory Types:");
            for (java.util.Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
                System.out.println("    " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
