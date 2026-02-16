package gemini.root;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * HIPPOCAMPUS: Memory chain manager.
 * 
 * - Saves blocks to disk (memory/*.genesis)
 * - Recalls previous state on startup
 * - Safe-by-default: Git push requires explicit env var
 */
public class Hippocampus {
    public static final List<GenesisBlock> chain = new ArrayList<>();
    public static String lastHash = "0";
    private static final String MEMORY_DIR = "memory";

    /**
     * RECALL: Load previous memories from disk.
     */
    public static void recall() {
        try {
            Path dir = Path.of(MEMORY_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                System.out.println(">>> [MEMORY] Fresh memory initialized.");
                return;
            }

            List<Path> files = Files.list(dir)
                .filter(p -> p.getFileName().toString().endsWith(".genesis"))
                .sorted(Comparator.comparingLong(p -> p.toFile().lastModified()))
                .toList();

            for (Path p : files) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
                    GenesisBlock b = (GenesisBlock) ois.readObject();
                    chain.add(b);
                    lastHash = b.hash;
                } catch (Exception e) {
                    System.err.println(">>> [MEMORY] Failed to load: " + p.getFileName());
                }
            }

            if (!files.isEmpty()) {
                System.out.println(">>> [MEMORY] Recalled " + files.size() + " blocks. lastHash=" + lastHash.substring(0, 8));
            }
        } catch (Exception e) {
            System.err.println(">>> [MEMORY] Recall failed: " + e.getMessage());
        }
    }

    /**
     * COMMIT: Create and persist a new memory block.
     */
    public static GenesisBlock commitMemory(String type, String data) {
        GenesisBlock b = new GenesisBlock(lastHash, type, data);
        chain.add(b);
        lastHash = b.hash;
        saveToDisk(b);

        // Safe-by-default: only push if env var enabled
        if ("1".equals(System.getenv("FRAYMUS_GIT_PUSH"))) {
            GitCortex.push("GENESIS: " + b.hash.substring(0, 8));
        }

        return b;
    }

    private static void saveToDisk(GenesisBlock b) {
        try {
            Files.createDirectories(Path.of(MEMORY_DIR));
            String filename = MEMORY_DIR + "/BLOCK_" + b.timestamp + "_" + b.hash.substring(0, 8) + ".genesis";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                oos.writeObject(b);
            }
            System.out.println(">>> [MEMORY] Block crystallized: " + filename);
        } catch (Exception e) {
            System.err.println(">>> [MEMORY] Save failed: " + e.getMessage());
        }
    }

    /**
     * GET STATS
     */
    public static String getStats() {
        Map<String, Integer> typeCounts = new HashMap<>();
        for (GenesisBlock b : chain) {
            typeCounts.merge(b.type, 1, Integer::sum);
        }
        return "Blocks: " + chain.size() + " | Types: " + typeCounts;
    }

    /**
     * SEARCH: Find memories containing text.
     */
    public static List<GenesisBlock> search(String query) {
        String q = query.toLowerCase();
        List<GenesisBlock> results = new ArrayList<>();
        for (GenesisBlock b : chain) {
            if (b.data != null && b.data.toLowerCase().contains(q)) {
                results.add(b);
            }
        }
        return results;
    }
}
