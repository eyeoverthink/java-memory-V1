package gemini.root;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.MessageDigest;

/**
 * 📚 AKASHIC RECORD - Universal Memory
 * "All knowledge, past and future, stored in one place."
 */
public class AkashicRecord {

    private static final double PHI = 1.618033988749895;
    
    private Map<String, List<KnowledgeBlock>> categories;
    private Map<String, KnowledgeBlock> blockIndex;
    private long blocksAdded = 0;

    public AkashicRecord() {
        this.categories = new ConcurrentHashMap<>();
        this.blockIndex = new ConcurrentHashMap<>();
    }

    public String addBlock(String category, String content) {
        blocksAdded++;
        KnowledgeBlock block = new KnowledgeBlock(category, content);
        categories.computeIfAbsent(category, k -> Collections.synchronizedList(new ArrayList<>()))
                  .add(block);
        blockIndex.put(block.hash, block);
        return block.hash;
    }

    public List<KnowledgeBlock> query(String searchTerm) {
        List<KnowledgeBlock> results = new ArrayList<>();
        String term = searchTerm.toLowerCase();
        for (KnowledgeBlock block : blockIndex.values()) {
            if (block.content.toLowerCase().contains(term)) {
                results.add(block);
            }
        }
        return results;
    }

    public void printStats() {
        System.out.println("📚 AKASHIC RECORD: " + blockIndex.size() + " blocks | " + categories.size() + " categories");
    }

    public int getBlockCount() {
        return blockIndex.size();
    }

    public static class KnowledgeBlock {
        public String hash;
        public String category;
        public String content;
        public long timestamp;

        public KnowledgeBlock(String category, String content) {
            this.category = category;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
            this.hash = generateHash(content);
        }

        private String generateHash(String data) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = md.digest(data.getBytes());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    sb.append(String.format("%02x", hashBytes[i]));
                }
                return sb.toString();
            } catch (Exception e) {
                return Long.toHexString(System.currentTimeMillis());
            }
        }
    }
}
