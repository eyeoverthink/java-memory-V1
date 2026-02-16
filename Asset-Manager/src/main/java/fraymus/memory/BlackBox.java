package fraymus.memory;

import fraymus.limbs.ClawConnector;
import fraymus.core.AuditLog;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * BLACK BOX - Vector Memory Persistence
 * 
 * The Hippocampus: Permanent, searchable database of every thought,
 * success, and failure Fraynix has ever had.
 * 
 * Enables continuous learning across reboots.
 */
public class BlackBox {

    private final ClawConnector claw;
    private final AuditLog auditLog;
    private final File memoryFile;
    private final List<Memory> longTermStorage = new ArrayList<>();
    
    private static final int EMBEDDING_DIM = 384; // Standard embedding size

    public BlackBox(AuditLog auditLog) {
        this.claw = new ClawConnector();
        this.auditLog = auditLog;
        this.memoryFile = new File("Fraynix_BlackBox.jsonl");
        loadMemories();
    }

    /**
     * ENCODE: Save an experience forever
     */
    public void remember(String context, String outcome, boolean success) {
        System.out.println("💾 BLACK BOX: Encoding experience...");
        auditLog.log("memory_encoding", context);

        // 1. Get Embedding (Vector) from Ollama
        List<Double> vector = generateEmbedding(context + " " + outcome);

        // 2. Create Memory Object
        Memory mem = new Memory(
            System.currentTimeMillis(),
            context,
            outcome,
            success,
            vector
        );
        longTermStorage.add(mem);

        // 3. Persist to Disk
        persistMemory(mem);
        
        System.out.println("   ✓ Memory encoded and persisted");
        auditLog.log("memory_encoded", mem);
    }

    /**
     * RECALL: Find relevant past experiences
     */
    public String recall(String currentSituation) {
        System.out.println("🧠 BLACK BOX: Searching for precedents...");
        auditLog.log("memory_recall_started", currentSituation);
        
        if (longTermStorage.isEmpty()) {
            return "NO PRECEDENT FOUND. Exploring new territory.";
        }
        
        List<Double> currentVector = generateEmbedding(currentSituation);
        Memory bestMatch = null;
        double bestScore = -1.0;

        for (Memory mem : longTermStorage) {
            double similarity = cosineSimilarity(currentVector, mem.vector);
            if (similarity > bestScore) {
                bestScore = similarity;
                bestMatch = mem;
            }
        }

        if (bestMatch != null && bestScore > 0.7) {
            String insight = String.format(
                "INSIGHT FROM PAST (similarity: %.2f): I previously tried '%s'. " +
                "Outcome: %s (Success: %s)",
                bestScore, bestMatch.context, bestMatch.outcome, bestMatch.success
            );
            auditLog.log("memory_recalled", bestMatch);
            return insight;
        }
        
        return "NO STRONG PRECEDENT FOUND (best similarity: " + 
               String.format("%.2f", bestScore) + "). Proceeding with caution.";
    }

    /**
     * Get all memories
     */
    public List<Memory> getAllMemories() {
        return new ArrayList<>(longTermStorage);
    }

    /**
     * Get memory count
     */
    public int getMemoryCount() {
        return longTermStorage.size();
    }

    /**
     * Clear all memories (use with caution)
     */
    public void clearMemories() {
        longTermStorage.clear();
        memoryFile.delete();
        auditLog.log("memory_cleared", "All memories erased");
    }

    // --- PERSISTENCE ---

    /**
     * Persist memory to disk
     */
    private void persistMemory(Memory mem) {
        try (FileWriter fw = new FileWriter(memoryFile, true)) {
            fw.write(mem.toJson() + "\n");
        } catch (IOException e) {
            System.err.println("❌ MEMORY PERSISTENCE ERROR: " + e.getMessage());
            auditLog.log("memory_persist_error", mem, e);
        }
    }

    /**
     * Load memories from disk
     */
    private void loadMemories() {
        if (!memoryFile.exists()) {
            System.out.println("📚 BLACK BOX: No existing memories found. Starting fresh.");
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(memoryFile))) {
            String line;
            int count = 0;
            
            while ((line = br.readLine()) != null) {
                try {
                    Memory mem = Memory.fromJson(line);
                    longTermStorage.add(mem);
                    count++;
                } catch (Exception e) {
                    System.err.println("⚠️ Skipping corrupted memory: " + e.getMessage());
                }
            }
            
            System.out.println("📚 BLACK BOX LOADED: " + count + " memories restored.");
            auditLog.log("memory_loaded", count);
            
        } catch (IOException e) {
            System.err.println("❌ MEMORY LOAD ERROR: " + e.getMessage());
            auditLog.log("memory_load_error", null, e);
        }
    }

    // --- EMBEDDING GENERATION ---

    /**
     * Generate embedding vector for text
     * In production, this would call Ollama embeddings API
     */
    private List<Double> generateEmbedding(String text) {
        // TODO: Integrate with actual Ollama embeddings
        // For now, use hash-based pseudo-embedding
        return generatePseudoEmbedding(text);
    }

    /**
     * Generate pseudo-embedding based on text hash
     * This is a fallback for when Ollama is not available
     */
    private List<Double> generatePseudoEmbedding(String text) {
        List<Double> vector = new ArrayList<>();
        Random r = new Random(text.hashCode());
        
        for (int i = 0; i < EMBEDDING_DIM; i++) {
            vector.add(r.nextDouble());
        }
        
        // Normalize
        double norm = 0.0;
        for (double v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        
        for (int i = 0; i < vector.size(); i++) {
            vector.set(i, vector.get(i) / norm);
        }
        
        return vector;
    }

    // --- MATH ---

    /**
     * Calculate cosine similarity between two vectors
     */
    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size()) {
            throw new IllegalArgumentException("Vectors must be same size");
        }
        
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            normA += v1.get(i) * v1.get(i);
            normB += v2.get(i) * v2.get(i);
        }
        
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // --- MEMORY CLASS ---

    /**
     * Memory entry
     */
    public static class Memory {
        public final long timestamp;
        public final String context;
        public final String outcome;
        public final boolean success;
        public final List<Double> vector;
        
        public Memory(long timestamp, String context, String outcome, 
                     boolean success, List<Double> vector) {
            this.timestamp = timestamp;
            this.context = context;
            this.outcome = outcome;
            this.success = success;
            this.vector = vector;
        }
        
        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"timestamp\":").append(timestamp).append(",");
            sb.append("\"context\":\"").append(escapeJson(context)).append("\",");
            sb.append("\"outcome\":\"").append(escapeJson(outcome)).append("\",");
            sb.append("\"success\":").append(success).append(",");
            sb.append("\"vector\":[");
            for (int i = 0; i < vector.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(vector.get(i));
            }
            sb.append("]}");
            return sb.toString();
        }
        
        public static Memory fromJson(String json) {
            // Simple JSON parsing (in production, use proper JSON library)
            long timestamp = extractLong(json, "timestamp");
            String context = extractString(json, "context");
            String outcome = extractString(json, "outcome");
            boolean success = extractBoolean(json, "success");
            List<Double> vector = extractVector(json, "vector");
            
            return new Memory(timestamp, context, outcome, success, vector);
        }
        
        private static String escapeJson(String s) {
            return s.replace("\"", "\\\"").replace("\n", "\\n");
        }
        
        private static long extractLong(String json, String key) {
            String pattern = "\"" + key + "\":";
            int start = json.indexOf(pattern) + pattern.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return Long.parseLong(json.substring(start, end).trim());
        }
        
        private static String extractString(String json, String key) {
            String pattern = "\"" + key + "\":\"";
            int start = json.indexOf(pattern) + pattern.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end).replace("\\\"", "\"").replace("\\n", "\n");
        }
        
        private static boolean extractBoolean(String json, String key) {
            String pattern = "\"" + key + "\":";
            int start = json.indexOf(pattern) + pattern.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return Boolean.parseBoolean(json.substring(start, end).trim());
        }
        
        private static List<Double> extractVector(String json, String key) {
            String pattern = "\"" + key + "\":[";
            int start = json.indexOf(pattern) + pattern.length();
            int end = json.indexOf("]", start);
            String vectorStr = json.substring(start, end);
            
            List<Double> vector = new ArrayList<>();
            for (String val : vectorStr.split(",")) {
                vector.add(Double.parseDouble(val.trim()));
            }
            return vector;
        }
    }
}
