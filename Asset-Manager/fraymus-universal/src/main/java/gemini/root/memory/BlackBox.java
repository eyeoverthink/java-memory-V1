package gemini.root.memory;

import gemini.root.limbs.ClawConnector;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BLACK BOX: The Hippocampus of Fraynix (Vector Persistence)
 * 
 * This is a permanent, searchable database of every thought, every success,
 * and every failure Fraynix has ever had. It provides IMMORTALITY.
 * 
 * The Learning Cycle:
 *   1. Experience: Fraynix attempts a task
 *   2. Outcome: Success or failure is recorded
 *   3. Encoding: Experience converted to vector embedding
 *   4. Recall: Future similar tasks search for precedents
 * 
 * When Fraynix boots, it reloads its "Life Experience" instantly.
 * It remembers that GravityEngine.java was buggy last Tuesday.
 */
public class BlackBox {

    private static final String MEMORY_FILE = "Fraynix_BlackBox.jsonl";
    private static final String MEMORY_DIR = ".fraynix";
    private static final int EMBEDDING_DIM = 64;  // Vector dimensions
    private static final double SIMILARITY_THRESHOLD = 0.75;

    private final ClawConnector claw;
    private final File memoryFile;
    private final List<Memory> longTermStorage;
    private final Map<String, List<Double>> embeddingCache;
    
    // Stats
    private int memoriesLoaded = 0;
    private int memoriesCreated = 0;
    private int recallsPerformed = 0;

    public BlackBox() {
        this.claw = new ClawConnector();
        this.longTermStorage = new ArrayList<>();
        this.embeddingCache = new HashMap<>();
        
        // Create memory directory if needed
        File memDir = new File(MEMORY_DIR);
        if (!memDir.exists()) {
            memDir.mkdirs();
        }
        this.memoryFile = new File(memDir, MEMORY_FILE);
        
        loadMemories();
    }

    public BlackBox(String customPath) {
        this.claw = new ClawConnector();
        this.longTermStorage = new ArrayList<>();
        this.embeddingCache = new HashMap<>();
        this.memoryFile = new File(customPath);
        
        loadMemories();
    }

    /**
     * ENCODE: Save an experience forever.
     * 
     * @param context What was being attempted
     * @param outcome What happened
     * @param success Whether it worked
     */
    public void remember(String context, String outcome, boolean success) {
        System.out.println("💾 BLACK BOX: Encoding experience...");

        // 1. Get Embedding (Vector)
        List<Double> vector = generateEmbedding(context + " " + outcome);

        // 2. Create Memory Object
        Memory mem = new Memory(
            UUID.randomUUID().toString(),
            context,
            outcome,
            success,
            vector,
            Instant.now().toEpochMilli(),
            1  // Initial recall count
        );
        
        longTermStorage.add(mem);
        memoriesCreated++;

        // 3. Persist to Disk
        persistMemory(mem);
        
        System.out.println("   ✅ Memory encoded: " + truncate(context, 50));
    }

    /**
     * RECALL: Find relevant past experiences.
     * 
     * @param currentSituation The current context to search for
     * @return Wisdom from past experiences
     */
    public String recall(String currentSituation) {
        recallsPerformed++;
        
        if (longTermStorage.isEmpty()) {
            return "NO PRECEDENT FOUND. Exploring new territory.";
        }

        System.out.println("🧠 BLACK BOX: Searching " + longTermStorage.size() + " memories...");
        
        List<Double> currentVector = generateEmbedding(currentSituation);
        
        // Find best matches
        List<MemoryMatch> matches = new ArrayList<>();
        for (Memory mem : longTermStorage) {
            double similarity = cosineSimilarity(currentVector, mem.vector);
            if (similarity > SIMILARITY_THRESHOLD) {
                matches.add(new MemoryMatch(mem, similarity));
            }
        }

        if (matches.isEmpty()) {
            return "NO PRECEDENT FOUND. Exploring new territory.";
        }

        // Sort by similarity (descending)
        matches.sort((a, b) -> Double.compare(b.similarity, a.similarity));

        // Return top 3 insights
        StringBuilder wisdom = new StringBuilder();
        int count = 0;
        for (MemoryMatch match : matches) {
            if (count >= 3) break;
            
            // Update recall count
            match.memory.recallCount++;
            
            String status = match.memory.success ? "SUCCESS" : "FAILURE";
            wisdom.append(String.format(
                "[%s %.0f%%] %s -> %s\n",
                status,
                match.similarity * 100,
                truncate(match.memory.context, 40),
                truncate(match.memory.outcome, 60)
            ));
            count++;
        }

        return "PAST WISDOM FOUND:\n" + wisdom.toString().trim();
    }

    /**
     * Search memories by keyword
     */
    public List<Memory> search(String keyword) {
        String lower = keyword.toLowerCase();
        return longTermStorage.stream()
            .filter(m -> m.context.toLowerCase().contains(lower) || 
                        m.outcome.toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    /**
     * Get memories by success status
     */
    public List<Memory> getSuccesses() {
        return longTermStorage.stream()
            .filter(m -> m.success)
            .collect(Collectors.toList());
    }

    public List<Memory> getFailures() {
        return longTermStorage.stream()
            .filter(m -> !m.success)
            .collect(Collectors.toList());
    }

    /**
     * Get most frequently recalled memories
     */
    public List<Memory> getMostRecalled(int limit) {
        return longTermStorage.stream()
            .sorted((a, b) -> Integer.compare(b.recallCount, a.recallCount))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * Clear all memories (use with caution!)
     */
    public void wipe() {
        System.out.println("⚠️ BLACK BOX: Wiping all memories...");
        longTermStorage.clear();
        embeddingCache.clear();
        if (memoryFile.exists()) {
            memoryFile.delete();
        }
        memoriesLoaded = 0;
        memoriesCreated = 0;
    }

    // ========== PERSISTENCE ==========

    private void loadMemories() {
        if (!memoryFile.exists()) {
            System.out.println("📚 BLACK BOX: No previous memories found. Starting fresh.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(memoryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                Memory mem = parseMemoryJson(line);
                if (mem != null) {
                    longTermStorage.add(mem);
                    memoriesLoaded++;
                }
            }
            System.out.println("📚 BLACK BOX LOADED: " + memoriesLoaded + " memories from disk.");
        } catch (Exception e) {
            System.err.println("⚠️ BLACK BOX: Error loading memories: " + e.getMessage());
        }
    }

    private void persistMemory(Memory mem) {
        try (FileWriter fw = new FileWriter(memoryFile, true)) {
            fw.write(memoryToJson(mem) + "\n");
        } catch (IOException e) {
            System.err.println("❌ BLACK BOX: Failed to persist memory: " + e.getMessage());
        }
    }

    private String memoryToJson(Memory mem) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":\"").append(escapeJson(mem.id)).append("\",");
        sb.append("\"context\":\"").append(escapeJson(mem.context)).append("\",");
        sb.append("\"outcome\":\"").append(escapeJson(mem.outcome)).append("\",");
        sb.append("\"success\":").append(mem.success).append(",");
        sb.append("\"timestamp\":").append(mem.timestamp).append(",");
        sb.append("\"recallCount\":").append(mem.recallCount).append(",");
        sb.append("\"vector\":[");
        for (int i = 0; i < mem.vector.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format("%.6f", mem.vector.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    private Memory parseMemoryJson(String json) {
        try {
            // Simple JSON parsing (avoiding external dependencies)
            String id = extractJsonString(json, "id");
            String context = extractJsonString(json, "context");
            String outcome = extractJsonString(json, "outcome");
            boolean success = json.contains("\"success\":true");
            long timestamp = extractJsonLong(json, "timestamp");
            int recallCount = (int) extractJsonLong(json, "recallCount");
            List<Double> vector = extractJsonArray(json, "vector");

            return new Memory(id, context, outcome, success, vector, timestamp, recallCount);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to parse memory: " + e.getMessage());
            return null;
        }
    }

    // ========== EMBEDDING ==========

    /**
     * Generate embedding vector for text.
     * Uses Ollama nomic-embed-text if available, otherwise uses hash-based fallback.
     */
    private List<Double> generateEmbedding(String text) {
        // Check cache
        if (embeddingCache.containsKey(text)) {
            return embeddingCache.get(text);
        }

        List<Double> vector;
        
        // Try Ollama embeddings
        try {
            String response = claw.dispatch(
                "ollama embeddings --model nomic-embed-text --text \"" + escapeJson(text) + "\"",
                "CONTEXT: EMBEDDING"
            );
            
            if (response != null && response.contains("[")) {
                vector = parseEmbeddingResponse(response);
                if (vector != null && !vector.isEmpty()) {
                    embeddingCache.put(text, vector);
                    return vector;
                }
            }
        } catch (Exception e) {
            // Fallback to hash-based
        }

        // Fallback: Hash-based pseudo-embedding
        vector = hashBasedEmbedding(text);
        embeddingCache.put(text, vector);
        return vector;
    }

    private List<Double> hashBasedEmbedding(String text) {
        List<Double> v = new ArrayList<>();
        
        // Use multiple hash seeds for diversity
        String normalized = text.toLowerCase().trim();
        int baseHash = normalized.hashCode();
        
        Random r = new Random(baseHash);
        for (int i = 0; i < EMBEDDING_DIM; i++) {
            v.add(r.nextGaussian() * 0.5);
        }
        
        // Normalize vector
        double norm = Math.sqrt(v.stream().mapToDouble(x -> x * x).sum());
        if (norm > 0) {
            for (int i = 0; i < v.size(); i++) {
                v.set(i, v.get(i) / norm);
            }
        }
        
        return v;
    }

    private List<Double> parseEmbeddingResponse(String response) {
        List<Double> v = new ArrayList<>();
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');
        if (start >= 0 && end > start) {
            String arrayPart = response.substring(start + 1, end);
            String[] parts = arrayPart.split(",");
            for (String p : parts) {
                try {
                    v.add(Double.parseDouble(p.trim()));
                } catch (NumberFormatException e) {
                    // skip
                }
            }
        }
        return v;
    }

    // ========== MATH ==========

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size() || v1.isEmpty()) {
            return 0.0;
        }
        
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            normA += v1.get(i) * v1.get(i);
            normB += v2.get(i) * v2.get(i);
        }
        
        double denom = Math.sqrt(normA) * Math.sqrt(normB);
        return denom > 0 ? dot / denom : 0.0;
    }

    // ========== UTILS ==========

    private String extractJsonString(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) return "";
        start += pattern.length();
        
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                sb.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private long extractJsonLong(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) return 0;
        start += pattern.length();
        
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (Character.isDigit(c) || c == '-') {
                sb.append(c);
            } else if (sb.length() > 0) {
                break;
            }
        }
        try {
            return Long.parseLong(sb.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private List<Double> extractJsonArray(String json, String key) {
        List<Double> result = new ArrayList<>();
        String pattern = "\"" + key + "\":[";
        int start = json.indexOf(pattern);
        if (start < 0) return result;
        start += pattern.length();
        
        int end = json.indexOf(']', start);
        if (end < 0) return result;
        
        String arrayContent = json.substring(start, end);
        for (String part : arrayContent.split(",")) {
            try {
                result.add(Double.parseDouble(part.trim()));
            } catch (NumberFormatException e) {
                // skip
            }
        }
        return result;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    // ========== GETTERS ==========

    public int getMemoryCount() { return longTermStorage.size(); }
    public int getMemoriesLoaded() { return memoriesLoaded; }
    public int getMemoriesCreated() { return memoriesCreated; }
    public int getRecallsPerformed() { return recallsPerformed; }

    // ========== INNER CLASSES ==========

    /**
     * A single memory unit
     */
    public static class Memory {
        public final String id;
        public final String context;
        public final String outcome;
        public final boolean success;
        public final List<Double> vector;
        public final long timestamp;
        public int recallCount;

        public Memory(String id, String context, String outcome, boolean success, 
                     List<Double> vector, long timestamp, int recallCount) {
            this.id = id;
            this.context = context;
            this.outcome = outcome;
            this.success = success;
            this.vector = vector;
            this.timestamp = timestamp;
            this.recallCount = recallCount;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s -> %s", success ? "✓" : "✗", context, outcome);
        }
    }

    /**
     * Memory with similarity score
     */
    private static class MemoryMatch {
        final Memory memory;
        final double similarity;

        MemoryMatch(Memory m, double s) {
            this.memory = m;
            this.similarity = s;
        }
    }
}
