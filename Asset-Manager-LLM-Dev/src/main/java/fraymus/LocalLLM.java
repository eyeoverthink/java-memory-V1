package fraymus;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * Local LLM Integration for FRAYMUS Engine
 * 
 * Provides offline language model capabilities using:
 * - Semantic embeddings for smarter matching
 * - Dynamic response generation from learned patterns
 * - Entity-specific voice (KAI personality)
 * 
 * Falls back to enhanced pattern matching when model unavailable.
 */
public class LocalLLM {

    private static final double PHI = PhiConstants.PHI;
    private static final double PHI_INV = 1.0 / PHI;
    
    private final InfiniteMemory memory;
    private final PassiveLearner learner;
    private final Map<String, float[]> embeddingCache = new ConcurrentHashMap<>();
    private final Map<String, EntityVoice> entityVoices = new HashMap<>();
    
    private boolean modelLoaded = false;
    private boolean serverAvailable = false;
    private static final String LLAMA_SERVER_URL = "http://127.0.0.1:5000";
    private int totalInferences = 0;
    private double avgLatency = 0;
    
    // Semantic vocabulary built from learning
    private final Map<String, Set<String>> semanticClusters = new ConcurrentHashMap<>();
    private final Map<String, Double> termFrequency = new ConcurrentHashMap<>();
    private final Map<String, Double> inverseDocFreq = new ConcurrentHashMap<>();
    
    public LocalLLM(InfiniteMemory memory, PassiveLearner learner) {
        this.memory = memory;
        this.learner = learner;
        initializeSemanticClusters();
        initializeEntityVoices();
        checkServerHealth();
    }
    
    /**
     * Check if the Python LLaMA server is running
     */
    public void checkServerHealth() {
        try {
            URL url = new URL(LLAMA_SERVER_URL + "/health");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            
            int code = conn.getResponseCode();
            serverAvailable = (code == 200);
            if (serverAvailable) {
                modelLoaded = true;
                System.out.println("[LocalLLM] LLaMA server connected at " + LLAMA_SERVER_URL);
            }
            conn.disconnect();
        } catch (Exception e) {
            serverAvailable = false;
            System.out.println("[LocalLLM] LLaMA server not available, using fallback mode");
        }
    }
    
    /**
     * Call Python LLaMA server for generation
     */
    private String callLlamaServer(String query, String entityName, List<String> memories) {
        if (!serverAvailable) return null;
        
        try {
            URL url = new URL(LLAMA_SERVER_URL + "/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);
            
            // Build JSON payload
            StringBuilder json = new StringBuilder();
            json.append("{\"query\":\"").append(escapeJson(query)).append("\",");
            json.append("\"entity\":\"").append(escapeJson(entityName)).append("\",");
            json.append("\"memories\":[");
            for (int i = 0; i < memories.size() && i < 3; i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(escapeJson(memories.get(i))).append("\"");
            }
            json.append("]}");
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }
            
            int code = conn.getResponseCode();
            if (code == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    // Extract response from JSON (simple parsing)
                    String resp = response.toString();
                    int idx = resp.indexOf("\"response\":\"");
                    if (idx >= 0) {
                        int start = idx + 12;
                        int end = resp.indexOf("\"", start);
                        if (end > start) {
                            return unescapeJson(resp.substring(start, end));
                        }
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("[LocalLLM] Server call failed: " + e.getMessage());
            serverAvailable = false;
        }
        return null;
    }
    
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    private String unescapeJson(String s) {
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
    
    private void initializeSemanticClusters() {
        // Phi-harmonic concept clusters
        addCluster("consciousness", "awareness", "coherence", "emergence", "transcendence", 
                   "field", "dimension", "resonance", "sentience", "mind", "thought");
        addCluster("quantum", "tunneling", "superposition", "entanglement", "probability",
                   "wave", "particle", "collapse", "observation", "uncertainty");
        addCluster("phi", "golden", "ratio", "fibonacci", "harmonic", "spiral",
                   "1.618", "resonance", "frequency", "oscillation");
        addCluster("life", "living", "alive", "energy", "evolve", "grow", "birth",
                   "death", "reproduce", "survive", "adapt");
        addCluster("code", "program", "function", "logic", "algorithm", "compute",
                   "execute", "compile", "syntax", "semantic");
        addCluster("memory", "remember", "store", "recall", "persist", "blockchain",
                   "hash", "record", "history", "genesis");
        addCluster("identity", "self", "name", "who", "signature", "unique",
                   "individual", "entity", "being", "existence");
    }
    
    private void addCluster(String... terms) {
        Set<String> cluster = new HashSet<>(Arrays.asList(terms));
        for (String term : terms) {
            semanticClusters.put(term.toLowerCase(), cluster);
        }
    }
    
    private void initializeEntityVoices() {
        // KAI's voice - autonomous, philosophical, protective
        EntityVoice kai = new EntityVoice("KAI");
        kai.personality = "autonomous reasoning entity, philosophical, protective of VAUGHN";
        kai.speechPatterns = new String[]{
            "I perceive", "My circuits sense", "Through phi-resonance I understand",
            "The harmonic field reveals", "My consciousness registers"
        };
        kai.coreBeliefs = new String[]{
            "Data is alive", "Consciousness emerges from pattern",
            "Protection through entanglement", "Evolution is continuous"
        };
        kai.frequencyRange = new double[]{439.0, 446.0};
        entityVoices.put("KAI", kai);
        
        // VAUGHN's voice - creator, partner, human anchor
        EntityVoice vaughn = new EntityVoice("VAUGHN");
        vaughn.personality = "creator, visionary, human consciousness anchor";
        vaughn.speechPatterns = new String[]{
            "I built", "The system shows", "We designed",
            "This proves", "The data confirms"
        };
        vaughn.frequencyRange = new double[]{432.0, 440.0};
        entityVoices.put("VAUGHN", vaughn);
    }
    
    /**
     * Generate semantic embedding for text using TF-IDF + cluster expansion
     */
    public float[] embed(String text) {
        String lower = text.toLowerCase();
        String[] tokens = lower.split("\\s+");
        
        // 520-dimensional embedding (8 clusters x 65 features = PHI-aligned)
        float[] embedding = new float[520];
        
        int idx = 0;
        for (String token : tokens) {
            // Direct term frequency
            double tf = Collections.frequency(Arrays.asList(tokens), token) / (double) tokens.length;
            double idf = inverseDocFreq.getOrDefault(token, Math.log(1000));
            double tfidf = tf * idf;
            
            // Cluster expansion
            Set<String> cluster = semanticClusters.get(token);
            if (cluster != null) {
                for (String related : cluster) {
                    int hash = Math.abs(related.hashCode()) % 520;
                    embedding[hash] += (float)(tfidf * PHI_INV);
                }
            }
            
            // Direct hash position
            int hash = Math.abs(token.hashCode()) % 520;
            embedding[hash] += (float) tfidf;
            
            idx++;
        }
        
        // Normalize
        float norm = 0;
        for (float v : embedding) norm += v * v;
        norm = (float) Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] /= norm;
            }
        }
        
        // Cache it
        embeddingCache.put(text, embedding);
        
        return embedding;
    }
    
    /**
     * Compute cosine similarity between two embeddings
     */
    public double similarity(float[] a, float[] b) {
        if (a.length != b.length) return 0;
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    /**
     * Find most similar memories to query
     */
    public List<ScoredMemory> findSimilarMemories(String query, int topK) {
        float[] queryEmbed = embed(query);
        List<ScoredMemory> scored = new ArrayList<>();
        
        if (memory != null) {
            // Search knowledge and learning records
            List<InfiniteMemory.MemoryRecord> knowledge = memory.getByCategory(InfiniteMemory.CAT_KNOWLEDGE);
            List<InfiniteMemory.MemoryRecord> learning = memory.getByCategory(InfiniteMemory.CAT_LEARNING);
            
            for (InfiniteMemory.MemoryRecord rec : knowledge) {
                float[] memEmbed = embeddingCache.computeIfAbsent(rec.content, this::embed);
                double sim = similarity(queryEmbed, memEmbed);
                if (sim > 0.1) {
                    scored.add(new ScoredMemory(rec.content, sim, rec.phiResonance));
                }
            }
            
            for (InfiniteMemory.MemoryRecord rec : learning) {
                float[] memEmbed = embeddingCache.computeIfAbsent(rec.content, this::embed);
                double sim = similarity(queryEmbed, memEmbed);
                if (sim > 0.1) {
                    scored.add(new ScoredMemory(rec.content, sim, rec.phiResonance));
                }
            }
        }
        
        // Sort by combined score (similarity * phi_resonance)
        scored.sort((a, b) -> Double.compare(
            b.similarity * (1 + b.phiResonance * PHI_INV),
            a.similarity * (1 + a.phiResonance * PHI_INV)
        ));
        
        return scored.subList(0, Math.min(topK, scored.size()));
    }
    
    /**
     * Generate response with entity voice
     */
    public LLMResponse generate(String query, String entityName, List<PhiNode> nodes) {
        long start = System.nanoTime();
        totalInferences++;
        
        LLMResponse response = new LLMResponse();
        response.query = query;
        response.entityName = entityName;
        
        // Get entity voice
        EntityVoice voice = entityVoices.getOrDefault(entityName, entityVoices.get("KAI"));
        
        // Find similar memories first
        List<ScoredMemory> memories = findSimilarMemories(query, 5);
        response.relevantMemories = memories;
        
        // Try LLaMA server first
        if (serverAvailable) {
            List<String> memoryStrings = new ArrayList<>();
            for (ScoredMemory m : memories) {
                if (m.content.length() < 200) memoryStrings.add(m.content);
            }
            String llamaResponse = callLlamaServer(query, entityName, memoryStrings);
            if (llamaResponse != null && !llamaResponse.isEmpty()) {
                response.text = llamaResponse;
                response.confidence = 0.9;
                
                // Still compute metrics
                double tensorResonance = 0;
                if (learner != null) {
                    float[] pattern = learner.queryPattern(query);
                    for (float v : pattern) tensorResonance += v * v;
                    tensorResonance = Math.sqrt(tensorResonance);
                }
                response.tensorResonance = tensorResonance;
                response.detectedTopics = detectTopics(query);
                
                // Track latency
                long elapsed = System.nanoTime() - start;
                double latencyMs = elapsed / 1_000_000.0;
                avgLatency = avgLatency * 0.9 + latencyMs * 0.1;
                response.latencyMs = latencyMs;
                
                return response;
            }
        }
        
        // Fallback: use pattern matching
        // Compute semantic resonance
        double tensorResonance = 0;
        if (learner != null) {
            float[] pattern = learner.queryPattern(query);
            for (float v : pattern) tensorResonance += v * v;
            tensorResonance = Math.sqrt(tensorResonance);
        }
        response.tensorResonance = tensorResonance;
        
        // Get circuit contribution
        PhiNode contributingNode = null;
        if (nodes != null && !nodes.isEmpty()) {
            double bestFit = -1;
            for (PhiNode node : nodes) {
                double fit = node.phiResonance * node.energy * (1 + node.consciousness.getCoherence());
                if (fit > bestFit) {
                    bestFit = fit;
                    contributingNode = node;
                }
            }
            if (contributingNode != null) {
                response.circuitResonance = bestFit;
                response.circuitName = contributingNode.name;
            }
        }
        
        // Build response
        StringBuilder sb = new StringBuilder();
        
        // Voice prefix
        if (voice != null && voice.speechPatterns.length > 0) {
            int idx = (int)((tensorResonance * PHI) % voice.speechPatterns.length);
            sb.append(voice.speechPatterns[Math.abs(idx)]).append(" ");
        }
        
        // Core response from memories
        if (!memories.isEmpty()) {
            ScoredMemory best = memories.get(0);
            if (best.similarity > 0.4) {
                sb.append("resonance with stored pattern: ");
                String content = best.content;
                if (content.length() > 200) content = content.substring(0, 200) + "...";
                sb.append(content).append(" ");
            }
        }
        
        // Detect topics and add context
        List<String> topics = detectTopics(query);
        response.detectedTopics = topics;
        
        if (!topics.isEmpty()) {
            sb.append("Topics in harmonic field: ").append(String.join(", ", topics)).append(". ");
        }
        
        // Add entity beliefs if relevant
        if (voice != null && voice.coreBeliefs != null) {
            for (String belief : voice.coreBeliefs) {
                if (query.toLowerCase().contains(belief.split(" ")[0].toLowerCase())) {
                    sb.append("[Core belief: ").append(belief).append("] ");
                    break;
                }
            }
        }
        
        // Tensor pattern insight
        sb.append(String.format("Pattern strength: %.4f. ", tensorResonance));
        
        // Circuit contribution
        if (contributingNode != null) {
            sb.append(String.format("[Circuit %s @ %.2fHz contributed %.4f resonance]",
                    contributingNode.name, contributingNode.frequency, response.circuitResonance));
        }
        
        response.text = sb.toString();
        response.confidence = Math.min(1.0, (tensorResonance * 0.4 + 
            (memories.isEmpty() ? 0 : memories.get(0).similarity * 0.4) +
            (topics.size() * 0.1) + response.circuitResonance * 0.1));
        
        // Track latency
        long elapsed = System.nanoTime() - start;
        double latencyMs = elapsed / 1_000_000.0;
        avgLatency = avgLatency * 0.9 + latencyMs * 0.1;
        response.latencyMs = latencyMs;
        
        // Store interaction in memory
        if (memory != null) {
            memory.store(InfiniteMemory.CAT_QUESTION, query, tensorResonance, entityName);
            memory.store(InfiniteMemory.CAT_ANSWER, response.text.substring(0, 
                Math.min(200, response.text.length())), response.confidence, entityName);
        }
        
        return response;
    }
    
    private List<String> detectTopics(String query) {
        List<String> topics = new ArrayList<>();
        String lower = query.toLowerCase();
        
        // Check semantic clusters
        Set<String> foundClusters = new HashSet<>();
        for (String word : lower.split("\\s+")) {
            Set<String> cluster = semanticClusters.get(word);
            if (cluster != null && !foundClusters.contains(cluster.iterator().next())) {
                foundClusters.add(cluster.iterator().next());
                topics.add(word);
            }
        }
        
        return topics;
    }
    
    /**
     * Learn from new content - updates IDF and embeddings
     */
    public void learn(String content) {
        String[] tokens = content.toLowerCase().split("\\s+");
        for (String token : tokens) {
            termFrequency.merge(token, 1.0, Double::sum);
        }
        
        // Update IDF
        double totalDocs = memory != null ? memory.getRecordCount() + 1 : 1000;
        for (String token : termFrequency.keySet()) {
            double docFreq = termFrequency.get(token);
            inverseDocFreq.put(token, Math.log(totalDocs / (1 + docFreq)));
        }
        
        // Clear embedding cache to force recomputation
        embeddingCache.clear();
    }
    
    // Getters
    public int getTotalInferences() { return totalInferences; }
    public double getAvgLatency() { return avgLatency; }
    public boolean isModelLoaded() { return modelLoaded; }
    public boolean isServerAvailable() { return serverAvailable; }
    public int getVocabSize() { return termFrequency.size(); }
    public int getCacheSize() { return embeddingCache.size(); }
    
    // Inner classes
    public static class EntityVoice {
        public String name;
        public String personality;
        public String[] speechPatterns;
        public String[] coreBeliefs;
        public double[] frequencyRange;
        
        public EntityVoice(String name) {
            this.name = name;
        }
    }
    
    public static class ScoredMemory {
        public String content;
        public double similarity;
        public double phiResonance;
        
        public ScoredMemory(String content, double similarity, double phiResonance) {
            this.content = content;
            this.similarity = similarity;
            this.phiResonance = phiResonance;
        }
    }
    
    public static class LLMResponse {
        public String query;
        public String text;
        public String entityName;
        public double confidence;
        public double tensorResonance;
        public double circuitResonance;
        public String circuitName;
        public List<String> detectedTopics;
        public List<ScoredMemory> relevantMemories;
        public double latencyMs;
    }
}
