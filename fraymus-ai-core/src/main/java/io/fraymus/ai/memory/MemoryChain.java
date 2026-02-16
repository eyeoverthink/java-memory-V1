package io.fraymus.ai.memory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * MEMORY CHAIN - Blockchain-style Persistent Memory
 * 
 * Immutable, hash-chained memory blocks
 */
public class MemoryChain {

    public static List<MemoryBlock> chain = new ArrayList<>();
    public static String lastHash = "0";
    
    private final Path memoryPath;

    public MemoryChain(String memoryPath) {
        this.memoryPath = Path.of(memoryPath);
    }

    /**
     * COMMIT
     * Add new memory block to chain
     */
    public void commit(String type, String data) {
        MemoryBlock b = new MemoryBlock(lastHash, type, data);
        chain.add(b);
        lastHash = b.hash;
        saveToDisk(b);
    }

    private void saveToDisk(MemoryBlock block) {
        try {
            Files.createDirectories(memoryPath);
            String fn = memoryPath.resolve("BLOCK_" + block.timestamp + "_" + block.hash.substring(0, 8) + ".genesis").toString();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fn))) {
                oos.writeObject(block);
            }
        } catch (Exception e) {
            System.err.println(">>> [MEMORY] write failed: " + e.getMessage());
        }
    }

    /**
     * RECALL
     * Load memory chain from disk
     */
    public void recall() {
        chain.clear();
        lastHash = "0";

        File folder = memoryPath.toFile();
        if (!folder.exists()) {
            return;
        }
        
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".genesis"));
        if (files == null || files.length == 0) {
            return;
        }

        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        for (File f : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                MemoryBlock b = (MemoryBlock) ois.readObject();
                chain.add(b);
                lastHash = b.hash;
            } catch (Exception ignored) {}
        }
    }

    /**
     * SEARCH
     * Search memory blocks by text
     */
    public List<MemoryBlock> search(String query, int limit) {
        String q = query.toLowerCase();
        List<MemoryBlock> results = new ArrayList<>();
        
        for (int i = chain.size() - 1; i >= 0 && results.size() < limit; i--) {
            MemoryBlock b = chain.get(i);
            if (b.data != null && b.data.toLowerCase().contains(q)) {
                results.add(b);
            }
        }
        
        return results;
    }

    /**
     * SIZE
     */
    public int size() {
        return chain.size();
    }
}
