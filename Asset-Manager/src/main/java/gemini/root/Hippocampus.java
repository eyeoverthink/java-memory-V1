package gemini.root;

import java.io.*;
import java.util.*;

/**
 * THE HIPPOCAMPUS
 * Role: Manages the Chain of Memory.
 * Function: Save to Disk, Load from Disk, Maintain History.
 * 
 * This is the persistence layer that makes the system immortal.
 * Every thought, every creation, every evolution is recorded.
 */
public class Hippocampus {
    
    public static List<GenesisBlock> chain = new ArrayList<>();
    public static String lastHash = "0"; // Genesis Seed
    private static final String MEMORY_DIR = "memory";

    /**
     * REMEMBER: Create a new block and save it.
     */
    public static GenesisBlock commitMemory(String type, String data) {
        // 1. Create Block
        GenesisBlock newBlock = new GenesisBlock(lastHash, type, data);
        chain.add(newBlock);
        lastHash = newBlock.hash;

        // 2. Crystallize to Disk
        saveToDisk(newBlock);
        
        System.out.println(">>> [HIPPOCAMPUS] Memory committed: " + type + " [" + newBlock.hash.substring(0, 8) + "]");
        
        return newBlock;
    }

    /**
     * REMEMBER AND ETERNALIZE: Commit + Push to Git
     */
    public static GenesisBlock commitAndPush(String type, String data) {
        GenesisBlock block = commitMemory(type, data);
        GitCortex.push(block.hash);
        return block;
    }

    private static void saveToDisk(GenesisBlock block) {
        try {
            // Ensure directory exists
            new File(MEMORY_DIR).mkdirs();
            
            // Filename: "BLOCK_[Timestamp]_[Hash].genesis"
            String filename = MEMORY_DIR + "/BLOCK_" + block.timestamp + "_" + block.hash.substring(0, 8) + ".genesis";

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                oos.writeObject(block);
            }
            
            System.out.println(">>> [HIPPOCAMPUS] Crystallized: " + filename);
        } catch (Exception e) {
            System.err.println(">>> [HIPPOCAMPUS] Save failed: " + e.getMessage());
        }
    }

    /**
     * WAKE UP: Load the last state from the file system.
     */
    public static void recall() {
        File folder = new File(MEMORY_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println(">>> [HIPPOCAMPUS] Fresh memory initialized.");
            return;
        }
        
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".genesis"));
        if (files == null || files.length == 0) {
            System.out.println(">>> [HIPPOCAMPUS] No previous memories found.");
            return;
        }
        
        // Sort by timestamp (newest first)
        Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        
        // Load all memories into chain
        chain.clear();
        for (File f : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                GenesisBlock block = (GenesisBlock) ois.readObject();
                chain.add(block);
            } catch (Exception e) {
                System.err.println(">>> [HIPPOCAMPUS] Failed to load: " + f.getName());
            }
        }
        
        // Set lastHash from most recent block
        if (!chain.isEmpty()) {
            // Sort chain by timestamp
            chain.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));
            lastHash = chain.get(chain.size() - 1).hash;
        }
        
        System.out.println(">>> [HIPPOCAMPUS] Recalled " + chain.size() + " memories. Last hash: " + lastHash.substring(0, 8) + "...");
    }

    /**
     * SEARCH: Find memories by type
     */
    public static List<GenesisBlock> findByType(String type) {
        List<GenesisBlock> results = new ArrayList<>();
        for (GenesisBlock block : chain) {
            if (type.equals(block.type)) {
                results.add(block);
            }
        }
        return results;
    }

    /**
     * SEARCH: Find memories containing text
     */
    public static List<GenesisBlock> search(String query) {
        List<GenesisBlock> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (GenesisBlock block : chain) {
            String data = String.valueOf(block.matter).toLowerCase();
            if (data.contains(q)) {
                results.add(block);
            }
        }
        return results;
    }

    /**
     * GET RECENT: Return last N memories
     */
    public static List<GenesisBlock> getRecent(int count) {
        int start = Math.max(0, chain.size() - count);
        return new ArrayList<>(chain.subList(start, chain.size()));
    }

    /**
     * VERIFY CHAIN: Check integrity
     */
    public static boolean verifyChain() {
        for (int i = 1; i < chain.size(); i++) {
            GenesisBlock current = chain.get(i);
            GenesisBlock previous = chain.get(i - 1);
            
            // Verify hash
            if (!current.hash.equals(current.calculateHash())) {
                System.err.println(">>> [HIPPOCAMPUS] Block " + i + " hash corrupted!");
                return false;
            }
            
            // Verify chain link
            if (!current.previousHash.equals(previous.hash)) {
                System.err.println(">>> [HIPPOCAMPUS] Chain broken at block " + i);
                return false;
            }
        }
        System.out.println(">>> [HIPPOCAMPUS] Chain verified: " + chain.size() + " blocks intact.");
        return true;
    }

    /**
     * GET STATS
     */
    public static String getStats() {
        Map<String, Integer> typeCounts = new HashMap<>();
        for (GenesisBlock block : chain) {
            typeCounts.merge(block.type, 1, Integer::sum);
        }
        return "Memories: " + chain.size() + " | Types: " + typeCounts;
    }
}
