package gemini.root;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * VECTOR VAULT v3 (Retrieval Memory)
 * 
 * - Stores: {id, path, chunkIndex, text, vec[]}
 * - Retrieves by cosine similarity
 * - Persists to vault/index.jsonl
 * 
 * Ollama's embeddings are L2-normalized (unit length),
 * so cosine similarity = dot product.
 */
public class VectorVault {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static class Entry {
        public String id;
        public String path;
        public int chunkIndex;
        public String text;
        public float[] vec;
    }

    private final List<Entry> entries = new ArrayList<>();
    private final Set<String> seen = new HashSet<>(); // dedupe: path+chunk+hash
    private final Path indexFile = Path.of("vault", "index.jsonl");

    public void load() {
        entries.clear();
        seen.clear();
        if (!Files.exists(indexFile)) return;

        try (BufferedReader br = Files.newBufferedReader(indexFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                JsonObject o = JsonParser.parseString(line).getAsJsonObject();
                Entry e = new Entry();
                e.id = o.get("id").getAsString();
                e.path = o.get("path").getAsString();
                e.chunkIndex = o.get("chunkIndex").getAsInt();
                e.text = o.get("text").getAsString();

                JsonArray v = o.getAsJsonArray("vec");
                float[] arr = new float[v.size()];
                for (int i = 0; i < v.size(); i++) arr[i] = v.get(i).getAsFloat();
                e.vec = arr;

                entries.add(e);
                seen.add(dedupeKey(e.path, e.chunkIndex, e.text));
            }
        } catch (Exception ignored) {}
        System.out.println(">>> [VAULT] Loaded " + entries.size() + " entries.");
    }

    public int size() { return entries.size(); }

    public void addAndPersist(String path, List<String> chunks, List<float[]> vectors) throws Exception {
        Files.createDirectories(indexFile.getParent());

        try (BufferedWriter bw = Files.newBufferedWriter(indexFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            for (int i = 0; i < chunks.size(); i++) {
                String text = chunks.get(i);
                String key = dedupeKey(path, i, text);

                // Skip duplicates
                if (seen.contains(key)) continue;
                seen.add(key);

                Entry e = new Entry();
                e.id = UUID.randomUUID().toString();
                e.path = path;
                e.chunkIndex = i;
                e.text = text;
                e.vec = vectors.get(i);

                entries.add(e);

                JsonObject o = new JsonObject();
                o.addProperty("id", e.id);
                o.addProperty("path", e.path);
                o.addProperty("chunkIndex", e.chunkIndex);
                o.addProperty("text", e.text);

                JsonArray vec = new JsonArray();
                for (float f : e.vec) vec.add(f);
                o.add("vec", vec);

                bw.write(gson.toJson(o));
                bw.newLine();
            }
        }
    }

    private String dedupeKey(String path, int chunkIndex, String text) {
        int hash = text.hashCode();
        return path + "::" + chunkIndex + "::" + hash;
    }

    public List<Entry> topK(float[] queryVec, int k) {
        if (queryVec == null || queryVec.length == 0) return List.of();
        PriorityQueue<Map.Entry<Entry, Float>> pq = new PriorityQueue<>(Comparator.comparing(Map.Entry::getValue));

        for (Entry e : entries) {
            float score = cosine(queryVec, e.vec);
            if (pq.size() < k) pq.offer(new AbstractMap.SimpleEntry<>(e, score));
            else if (score > pq.peek().getValue()) { pq.poll(); pq.offer(new AbstractMap.SimpleEntry<>(e, score)); }
        }

        List<Map.Entry<Entry, Float>> tmp = new ArrayList<>(pq);
        tmp.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));

        List<Entry> out = new ArrayList<>();
        for (var it : tmp) out.add(it.getKey());
        return out;
    }

    private float cosine(float[] a, float[] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0) return 0f;
        int n = Math.min(a.length, b.length);
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < n; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0f;
        return (float)(dot / (Math.sqrt(na) * Math.sqrt(nb)));
    }

}
