package gemini.root;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CONTEXT VAULT: RAG (Retrieval-Augmented Generation)
 * 
 * Stores chunks of ingested documents.
 * Retrieves relevant chunks based on query overlap.
 * 
 * This is what makes the system "Gemini-grade":
 * - Context is SELECTED, not dumped
 * - Prevents prompt overflow
 * - Enables citation of sources
 */
public class ContextVault {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final List<Chunk> chunks = new ArrayList<>();

    public static class Chunk {
        public String id;
        public String path;
        public int index;
        public String text;

        public Chunk() {}
        public Chunk(String id, String path, int index, String text) {
            this.id = id;
            this.path = path;
            this.index = index;
            this.text = text;
        }
    }

    /**
     * INGEST: Chunk and store document text.
     */
    public void ingest(String filePath, String cleanText) throws Exception {
        List<String> sliced = chunk(cleanText, 1200, 200);

        for (int i = 0; i < sliced.size(); i++) {
            String id = UUID.randomUUID().toString();
            chunks.add(new Chunk(id, filePath, i, sliced.get(i)));
        }

        persistAppend(filePath, sliced);
        System.out.println(">>> [VAULT] Ingested " + sliced.size() + " chunks from: " + filePath);
    }

    /**
     * RETRIEVE: Find relevant chunks for a query.
     * Uses simple lexical overlap (fast, no embedding model required).
     */
    public String retrieveForQuery(String query, int maxChars) {
        if (chunks.isEmpty()) return "";

        Set<String> q = tokenize(query);
        var scored = new ArrayList<Map.Entry<Chunk, Integer>>();

        for (Chunk c : chunks) {
            int score = overlapScore(q, tokenize(c.text));
            if (score > 0) scored.add(Map.entry(c, score));
        }

        // Sort by score descending
        scored.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        // Build context string up to maxChars
        StringBuilder out = new StringBuilder();
        for (var e : scored) {
            String block = "\n[FILE: " + e.getKey().path + " | CHUNK: " + e.getKey().index + "]\n" 
                         + e.getKey().text + "\n";
            if (out.length() + block.length() > maxChars) break;
            out.append(block);
        }
        return out.toString();
    }

    /**
     * RETRIEVE ALL: Get all chunks (for small datasets).
     */
    public String retrieveAll(int maxChars) {
        StringBuilder out = new StringBuilder();
        for (Chunk c : chunks) {
            String block = "\n[FILE: " + c.path + " | CHUNK: " + c.index + "]\n" + c.text + "\n";
            if (out.length() + block.length() > maxChars) break;
            out.append(block);
        }
        return out.toString();
    }

    /**
     * GET STATS
     */
    public String getStats() {
        Set<String> files = chunks.stream().map(c -> c.path).collect(Collectors.toSet());
        return String.format("Chunks: %d | Files: %d", chunks.size(), files.size());
    }

    /**
     * CLEAR: Remove all chunks.
     */
    public void clear() {
        chunks.clear();
        System.out.println(">>> [VAULT] Cleared all chunks.");
    }

    // === Private helpers ===

    private int overlapScore(Set<String> q, Set<String> t) {
        int s = 0;
        for (String w : q) if (t.contains(w)) s++;
        return s;
    }

    private Set<String> tokenize(String s) {
        if (s == null) return Set.of();
        String[] parts = s.toLowerCase().split("[^a-z0-9_]+");
        return Arrays.stream(parts)
            .filter(p -> p.length() >= 3)
            .collect(Collectors.toSet());
    }

    private List<String> chunk(String text, int size, int overlap) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            int end = Math.min(text.length(), i + size);
            out.add(text.substring(i, end));
            if (end == text.length()) break;
            i = Math.max(0, end - overlap);
        }
        return out;
    }

    private void persistAppend(String filePath, List<String> sliced) {
        try {
            Files.createDirectories(Path.of("memory"));
            Path vault = Path.of("memory", "vault.jsonl");
            try (BufferedWriter w = Files.newBufferedWriter(vault, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                for (int i = 0; i < sliced.size(); i++) {
                    var row = Map.of("path", filePath, "index", i, "text", sliced.get(i));
                    w.write(gson.toJson(row));
                    w.newLine();
                }
            }
        } catch (Exception e) {
            System.err.println(">>> [VAULT] Persist failed: " + e.getMessage());
        }
    }
}
