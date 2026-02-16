package io.fraymus.ai.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * VECTOR STORE - Semantic Search
 * 
 * Stores text chunks with embeddings for similarity search
 */
public class VectorStore {

    public static class Entry {
        public String id;
        public String path;
        public int chunkIndex;
        public String text;
        public double[] vec;
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final List<Entry> entries = new ArrayList<>();
    private final Path indexFile;

    public VectorStore(String storePath) {
        this.indexFile = Path.of(storePath, "index.jsonl");
    }

    /**
     * LOAD
     * Load vectors from disk
     */
    public void load() {
        entries.clear();
        if (!Files.exists(indexFile)) return;

        try (BufferedReader br = Files.newBufferedReader(indexFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                JsonNode o = mapper.readTree(line);
                Entry e = new Entry();
                e.id = o.get("id").asText();
                e.path = o.get("path").asText();
                e.chunkIndex = o.get("chunkIndex").asInt();
                e.text = o.get("text").asText();

                JsonNode v = o.get("vec");
                double[] arr = new double[v.size()];
                for (int i = 0; i < v.size(); i++) arr[i] = v.get(i).asDouble();
                e.vec = arr;

                entries.add(e);
            }
        } catch (Exception ignored) {}
    }

    /**
     * SIZE
     */
    public int size() {
        return entries.size();
    }

    /**
     * ADD AND PERSIST
     * Add chunks with vectors and save to disk
     */
    public void addAndPersist(String path, List<String> chunks, List<double[]> vectors) throws Exception {
        Files.createDirectories(indexFile.getParent());

        try (BufferedWriter bw = Files.newBufferedWriter(indexFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            for (int i = 0; i < chunks.size(); i++) {
                Entry e = new Entry();
                e.id = UUID.randomUUID().toString();
                e.path = path;
                e.chunkIndex = i;
                e.text = chunks.get(i);
                e.vec = vectors.get(i);

                entries.add(e);

                ObjectNode o = mapper.createObjectNode();
                o.put("id", e.id);
                o.put("path", e.path);
                o.put("chunkIndex", e.chunkIndex);
                o.put("text", e.text);

                ArrayNode vec = o.putArray("vec");
                for (double d : e.vec) vec.add(d);

                bw.write(mapper.writeValueAsString(o));
                bw.newLine();
            }
        }
    }

    /**
     * TOP K
     * Find top K most similar entries by cosine similarity
     */
    public List<Entry> topK(double[] queryVec, int k) {
        if (queryVec == null || queryVec.length == 0) return List.of();
        PriorityQueue<Map.Entry<Entry, Double>> pq = new PriorityQueue<>(Comparator.comparing(Map.Entry::getValue));

        for (Entry e : entries) {
            double score = cosine(queryVec, e.vec);
            if (pq.size() < k) pq.offer(new AbstractMap.SimpleEntry<>(e, score));
            else if (score > pq.peek().getValue()) { pq.poll(); pq.offer(new AbstractMap.SimpleEntry<>(e, score)); }
        }

        List<Map.Entry<Entry, Double>> tmp = new ArrayList<>(pq);
        tmp.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Entry> out = new ArrayList<>();
        for (var it : tmp) out.add(it.getKey());
        return out;
    }

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
}
