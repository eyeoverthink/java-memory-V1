package fraymus.ollama;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * VECTOR VAULT - Semantic RAG Memory
 * 
 * This is the "Soul" with real retrieval, not "dump everything into prompt"
 * 
 * Features:
 * - Chunks + embeds every file you transmute
 * - Embeds user queries
 * - Retrieves top-K chunks by cosine similarity
 * - Injects only the best context into /api/chat
 * 
 * Ollama's /api/embed returns L2-normalized embeddings (unit length)
 * Cosine similarity = dot product for normalized vectors
 */
public class VectorVault {
    private static final ObjectMapper M = new ObjectMapper();

    public static class VecChunk {
        public String id;
        public String path;
        public int index;
        public String text;
        public double[] vec;

        public VecChunk() {}
        
        public VecChunk(String id, String path, int index, String text, double[] vec) {
            this.id = id;
            this.path = path;
            this.index = index;
            this.text = text;
            this.vec = vec;
        }
    }
    
    // Alias for compatibility
    public static class Entry extends VecChunk {
        public int chunkIndex;
        
        public Entry() {
            super();
        }
        
        public Entry(String id, String path, int index, String text, double[] vec) {
            super(id, path, index, text, vec);
            this.chunkIndex = index;
        }
    }

    private final List<VecChunk> store = new ArrayList<>();
    private final Path diskFile = Path.of("memory", "vectors.jsonl");

    /**
     * LOAD FROM DISK
     * Resurrects previous embeddings
     */
    public void loadFromDisk() {
        try {
            if (!Files.exists(diskFile)) {
                System.out.println(">>> [VAULT] No previous vectors found");
                return;
            }
            
            try (BufferedReader r = Files.newBufferedReader(diskFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.isBlank()) continue;
                    VecChunk c = M.readValue(line, VecChunk.class);
                    if (c != null && c.vec != null) {
                        store.add(c);
                    }
                }
            }
            System.out.println(">>> [VAULT] Loaded " + store.size() + " vector chunks from disk");
        } catch (Exception e) {
            System.err.println(">>> [VAULT] Load failed: " + e.getMessage());
        }
    }

    /**
     * GET SIZE
     */
    public int size() {
        return store.size();
    }

    /**
     * ADD AND PERSIST
     * Adds chunks with vectors and persists to disk
     */
    public void addAndPersist(String path, List<String> chunks, List<double[]> vectors) throws Exception {
        Files.createDirectories(diskFile.getParent());

        try (BufferedWriter bw = Files.newBufferedWriter(diskFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            for (int i = 0; i < chunks.size(); i++) {
                VecChunk e = new VecChunk();
                e.id = UUID.randomUUID().toString();
                e.path = path;
                e.index = i;
                e.text = chunks.get(i);
                e.vec = vectors.get(i);

                store.add(e);

                ObjectNode o = M.createObjectNode();
                o.put("id", e.id);
                o.put("path", e.path);
                o.put("chunkIndex", e.index);
                o.put("text", e.text);

                ArrayNode vec = o.putArray("vec");
                for (double d : e.vec) vec.add(d);

                bw.write(M.writeValueAsString(o));
                bw.newLine();
            }
        }
    }

    /**
     * TOP K
     * Returns top K chunks by cosine similarity
     */
    public List<VecChunk> topK(double[] queryVec, int k) {
        if (queryVec == null || queryVec.length == 0) return List.of();
        PriorityQueue<Map.Entry<VecChunk, Double>> pq = new PriorityQueue<>(Comparator.comparing(Map.Entry::getValue));

        for (VecChunk e : store) {
            double score = cosine(queryVec, e.vec);
            if (pq.size() < k) pq.offer(new AbstractMap.SimpleEntry<>(e, score));
            else if (score > pq.peek().getValue()) { pq.poll(); pq.offer(new AbstractMap.SimpleEntry<>(e, score)); }
        }

        List<Map.Entry<VecChunk, Double>> tmp = new ArrayList<>(pq);
        tmp.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<VecChunk> out = new ArrayList<>();
        for (var it : tmp) out.add(it.getKey());
        return out;
    }


    /**
     * APPEND TO DISK
     * Persists vector chunk as JSONL
     */
    private void appendToDisk(VecChunk vc) {
        try (BufferedWriter w = Files.newBufferedWriter(diskFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(M.writeValueAsString(vc));
            w.newLine();
        } catch (Exception e) {
            System.err.println(">>> [VAULT] Persist failed: " + e.getMessage());
        }
    }

    /**
     * COSINE SIMILARITY
     */
    private double cosine(double[] a, double[] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0) return 0.0;
        int n = Math.min(a.length, b.length);
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < n; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0.0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }


    /**
     * CLEAR VAULT
     * Removes all vectors (use with caution)
     */
    public void clear() {
        store.clear();
        try {
            if (Files.exists(diskFile)) {
                Files.delete(diskFile);
            }
            System.out.println(">>> [VAULT] Cleared all vectors");
        } catch (Exception e) {
            System.err.println(">>> [VAULT] Clear failed: " + e.getMessage());
        }
    }

    /**
     * Print statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   VECTOR VAULT STATISTICS                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Total Chunks: " + store.size());
        System.out.println("  Disk File: " + diskFile);
        System.out.println("  Exists: " + Files.exists(diskFile));
        
        if (!store.isEmpty()) {
            System.out.println("  Vector Dimension: " + store.get(0).vec.length);
            
            // Count by file
            Map<String, Integer> fileCounts = new HashMap<>();
            for (VecChunk c : store) {
                fileCounts.put(c.path, fileCounts.getOrDefault(c.path, 0) + 1);
            }
            
            System.out.println("\n  Files Indexed:");
            for (Map.Entry<String, Integer> entry : fileCounts.entrySet()) {
                System.out.println("    " + entry.getKey() + ": " + entry.getValue() + " chunks");
            }
        }
    }
}
