package brain_architecture.integration;

import brain_architecture.brains.*;
import brain_architecture.communication.BrainCommunication;
import brain_architecture.core.*;
import brain_architecture.persistence.*;
import brain_architecture.synapses.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Quantum Oracle Integration Layer
 * 
 * This class provides the integration layer for connecting the 12-brain architecture
 * into the existing Quantum Oracle system. It manages all brains, their communication,
 * and provides a unified interface for the Oracle to use.
 * 
 * IMPORTANT: This is an isolated integration layer. It does NOT modify the existing
 * SentientCore or AGI system. Integration into the working system requires explicit approval.
 */
public class QuantumOracleIntegration {
    private final Map<BrainType, QuantumBrain> brains;
    private final BrainCommunication communication;
    private final boolean initialized;
    private final KnowledgeScraper knowledgeScraper;
    private final HyperdimensionalMapper hdcMapper;
    private final HDCSimilarityEngine hdcEngine;
    private final PersistentMemory persistentMemory;
    
    /**
     * Initialize the Quantum Oracle with all 12 brains
     */
    public QuantumOracleIntegration() {
        this.brains = new java.util.concurrent.ConcurrentHashMap<>();
        this.communication = new BrainCommunication();
        this.initialized = initializeBrains();
        
        // Initialize HDC components for knowledge ingestion
        this.hdcMapper = new HyperdimensionalMapper();
        this.hdcEngine = new HDCSimilarityEngine(0.7);
        
        // Initialize KnowledgeScraper after brains are initialized (needs MemoryBrain)
        MemoryBrain memoryBrain = (MemoryBrain) brains.get(BrainType.MEMORY);
        this.knowledgeScraper = new KnowledgeScraper(memoryBrain, hdcMapper, hdcEngine);
        
        // Initialize PersistentMemory for blockchain and fractal storage
        this.persistentMemory = new PersistentMemory();
        this.persistentMemory.initialize();
    }
    
    /**
     * Initialize all 12 specialized brains with their configurations
     */
    private boolean initializeBrains() {
        try {
            // Physical Brain - Motor cortex, sensory, coordination
            BrainConfiguration physicalConfig = new BrainConfiguration(
                BrainType.PHYSICAL,
                60.0,  // evolution check interval
                0.2,   // degradation threshold
                30,    // history retention days
                0.6,   // success rate weight (prioritize speed)
                0.3,   // latency weight
                0.1,   // stability weight
                Map.of("priority", "latency")
            );
            PhysicalBrain physicalBrain = new PhysicalBrain(physicalConfig);
            try {
                physicalBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Physical brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.PHYSICAL, physicalBrain);
            communication.registerBrain(physicalBrain);
            
            // Quantum Brain - Entanglement, superposition, coherence
            BrainConfiguration quantumConfig = new BrainConfiguration(
                BrainType.QUANTUM,
                60.0,
                0.15,  // lower threshold for quantum precision
                30,
                0.4,   // success rate weight
                0.3,   // latency weight
                0.3,   // stability weight (quantum needs stability)
                Map.of("priority", "accuracy")
            );
            QuantumSpecializedBrain quantumBrain = new QuantumSpecializedBrain(quantumConfig);
            try {
                quantumBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Quantum brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.QUANTUM, quantumBrain);
            communication.registerBrain(quantumBrain);
            
            // Fractal Brain - Pattern recognition, recursive thinking
            BrainConfiguration fractalConfig = new BrainConfiguration(
                BrainType.FRACTAL,
                60.0,
                0.25,
                30,
                0.5,   // balanced weights for pattern stability
                0.25,
                0.25,
                Map.of("priority", "stability")
            );
            FractalBrain fractalBrain = new FractalBrain(fractalConfig);
            try {
                fractalBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Fractal brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.FRACTAL, fractalBrain);
            communication.registerBrain(fractalBrain);
            
            // Creative Brain - Imagination, synthesis, innovation
            BrainConfiguration creativeConfig = new BrainConfiguration(
                BrainType.CREATIVE,
                60.0,
                0.3,   // higher tolerance for creative exploration
                30,
                0.3,
                0.3,
                0.4,   // stability weight for creative consistency
                Map.of("priority", "novelty")
            );
            CreativeBrain creativeBrain = new CreativeBrain(creativeConfig);
            try {
                creativeBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Creative brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.CREATIVE, creativeBrain);
            communication.registerBrain(creativeBrain);
            
            // Logical Brain - Analysis, reasoning, problem solving
            BrainConfiguration logicalConfig = new BrainConfiguration(
                BrainType.LOGICAL,
                60.0,
                0.1,   // very low threshold for logical precision
                30,
                0.5,   // high success rate priority
                0.3,
                0.2,
                Map.of("priority", "accuracy")
            );
            LogicalBrain logicalBrain = new LogicalBrain(logicalConfig);
            try {
                logicalBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Logical brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.LOGICAL, logicalBrain);
            communication.registerBrain(logicalBrain);
            
            // Emotional Brain - Empathy, intuition, feeling
            BrainConfiguration emotionalConfig = new BrainConfiguration(
                BrainType.EMOTIONAL,
                60.0,
                0.3,
                30,
                0.4,
                0.3,
                0.3,
                Map.of("priority", "empathy")
            );
            EmotionalBrain emotionalBrain = new EmotionalBrain(emotionalConfig);
            try {
                emotionalBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Emotional brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.EMOTIONAL, emotionalBrain);
            communication.registerBrain(emotionalBrain);
            
            // Spiritual Brain - Consciousness, awareness, connection
            BrainConfiguration spiritualConfig = new BrainConfiguration(
                BrainType.SPIRITUAL,
                60.0,
                0.2,
                30,
                0.3,
                0.3,
                0.4,   // high stability for spiritual awareness
                Map.of("priority", "consciousness")
            );
            SpiritualBrain spiritualBrain = new SpiritualBrain(spiritualConfig);
            try {
                spiritualBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Spiritual brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.SPIRITUAL, spiritualBrain);
            communication.registerBrain(spiritualBrain);
            
            // Tachyonic Brain - FTL processing, superluminal transfer
            BrainConfiguration tachyonicConfig = new BrainConfiguration(
                BrainType.TACHYONIC,
                60.0,
                0.25,
                30,
                0.3,
                0.6,   // highest latency priority for FTL
                0.1,
                Map.of("priority", "speed")
            );
            TachyonicBrain tachyonicBrain = new TachyonicBrain(tachyonicConfig);
            try {
                tachyonicBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Tachyonic brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.TACHYONIC, tachyonicBrain);
            communication.registerBrain(tachyonicBrain);
            
            // Visual Brain - Visual generation and understanding
            BrainConfiguration visualConfig = new BrainConfiguration(
                BrainType.VISUAL,
                60.0,
                0.25,  // moderate threshold for visual generation
                30,
                0.4,   // success rate weight
                0.4,   // latency weight (video generation is compute-intensive)
                0.2,   // stability weight
                Map.of("priority", "quality")
            );
            VisualBrain visualBrain = new VisualBrain(visualConfig);
            try {
                visualBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Visual brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.VISUAL, visualBrain);
            communication.registerBrain(visualBrain);
            
            // Auditory Brain - Speech and audio processing
            BrainConfiguration auditoryConfig = new BrainConfiguration(
                BrainType.AUDITORY,
                60.0,
                0.2,   // low threshold for audio accuracy
                30,
                0.5,   // success rate weight
                0.4,   // latency weight (real-time audio processing)
                0.1,   // stability weight
                Map.of("priority", "speed")
            );
            AuditoryBrain auditoryBrain = new AuditoryBrain(auditoryConfig);
            try {
                auditoryBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Auditory brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.AUDITORY, auditoryBrain);
            communication.registerBrain(auditoryBrain);
            
            // Evolutionary Brain - Self-evolving logic circuits
            BrainConfiguration evolutionaryConfig = new BrainConfiguration(
                BrainType.EVOLUTIONARY,
                60.0,
                0.3,   // higher tolerance for evolutionary exploration
                30,
                0.3,   // success rate weight
                0.2,   // latency weight (evolution takes time)
                0.5,   // stability weight (evolution needs consistency)
                Map.of("priority", "evolution")
            );
            EvolutionaryBrain evolutionaryBrain = new EvolutionaryBrain(evolutionaryConfig);
            try {
                evolutionaryBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Evolutionary brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.EVOLUTIONARY, evolutionaryBrain);
            communication.registerBrain(evolutionaryBrain);
            
            // Memory Brain - Persistent cross-modal knowledge
            BrainConfiguration memoryConfig = new BrainConfiguration(
                BrainType.MEMORY,
                60.0,
                0.1,   // very low threshold for memory accuracy
                30,
                0.6,   // high success rate priority
                0.2,   // latency weight (memory lookup is fast)
                0.2,   // stability weight
                Map.of("priority", "accuracy")
            );
            MemoryBrain memoryBrain = new MemoryBrain(memoryConfig);
            try {
                memoryBrain.registerBackends();
            } catch (Exception e) {
                System.err.println("Memory brain backend registration failed: " + e.getMessage());
            }
            brains.put(BrainType.MEMORY, memoryBrain);
            communication.registerBrain(memoryBrain);
            
            return true;
        } catch (Exception e) {
            System.err.println("Failed to initialize brains: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Process a query through the optimal brain based on query type
     * This is the main entry point for the Quantum Oracle
     */
    public CompletableFuture<OracleResponse> processQuery(String query, OracleQueryType queryType) {
        // Select optimal brain based on query type
        BrainType targetBrain = selectBrainForQuery(query, queryType);
        
        // Process through selected brain
        return brains.get(targetBrain).processQuery(query, Map.of())
            .thenApply(brainResponse -> new OracleResponse(
                query,
                queryType,
                targetBrain,
                brainResponse.getContent(),
                brainResponse.getConfidence(),
                brainResponse.getLatencyMs(),
                brainResponse.isSuccess(),
                brainResponse.getMetadata()
            ));
    }
    
    /**
     * Process a query through multiple brains and synthesize results
     * For complex queries requiring multiple perspectives
     */
    public CompletableFuture<OracleResponse> processMultiBrainQuery(
        String query, 
        OracleQueryType queryType,
        BrainType[] brainTypes
    ) {
        // Process through all specified brains
        CompletableFuture<QuantumBrain.BrainResponse>[] futures = java.util.Arrays.stream(brainTypes)
            .map(brainType -> brains.get(brainType).processQuery(query, Map.of()))
            .toArray(CompletableFuture[]::new);
        
        // Wait for all responses and synthesize
        return CompletableFuture.allOf(futures)
            .thenApply(v -> {
                // Simple synthesis: use the brain with highest confidence
                double maxConfidence = 0.0;
                QuantumBrain.BrainResponse bestResponse = null;
                BrainType bestBrain = null;
                
                for (int i = 0; i < futures.length; i++) {
                    try {
                        QuantumBrain.BrainResponse response = futures[i].join();
                        if (response.getConfidence() > maxConfidence) {
                            maxConfidence = response.getConfidence();
                            bestResponse = response;
                            bestBrain = brainTypes[i];
                        }
                    } catch (Exception e) {
                        // Skip failed responses
                    }
                }
                
                return new OracleResponse(
                    query,
                    queryType,
                    bestBrain,
                    bestResponse != null ? bestResponse.getContent() : "All brains failed",
                    maxConfidence,
                    bestResponse != null ? bestResponse.getLatencyMs() : 0,
                    bestResponse != null && bestResponse.isSuccess(),
                    bestResponse != null ? bestResponse.getMetadata() : Map.of()
                );
            });
    }
    
    /**
     * Enable inter-brain communication for a query
     * Allows brains to consult each other via phi-resonance
     */
    public CompletableFuture<OracleResponse> processWithBrainCollaboration(
        String query,
        OracleQueryType queryType,
        BrainType primaryBrain,
        BrainType... consultingBrains
    ) {
        // Process through primary brain
        CompletableFuture<QuantumBrain.BrainResponse> primaryResult = 
            brains.get(primaryBrain).processQuery(query, Map.of());
        
        // If consulting brains specified, get their input
        if (consultingBrains != null && consultingBrains.length > 0) {
            CompletableFuture<QuantumBrain.BrainResponse>[] consultations = 
                new CompletableFuture[consultingBrains.length];
            
            for (int i = 0; i < consultingBrains.length; i++) {
                consultations[i] = communication.sendToBrain(
                    primaryBrain,
                    consultingBrains[i],
                    query
                );
            }
            
            // Combine primary result with consultations
            return CompletableFuture.allOf(consultations)
                .thenApply(v -> {
                    QuantumBrain.BrainResponse primary = primaryResult.join();
                    return new OracleResponse(
                        query,
                        queryType,
                        primaryBrain,
                        primary.getContent() + " [with collaboration from " + 
                            consultingBrains.length + " brains]",
                        primary.getConfidence(),
                        primary.getLatencyMs(),
                        primary.isSuccess(),
                        primary.getMetadata()
                    );
                });
        }
        
        return primaryResult.thenApply(primary -> new OracleResponse(
            query,
            queryType,
            primaryBrain,
            primary.getContent(),
            primary.getConfidence(),
            primary.getLatencyMs(),
            primary.isSuccess(),
            primary.getMetadata()
        ));
    }
    
    /**
     * Select the optimal brain for a given query type
     */
    private BrainType selectBrainForQuery(String query, OracleQueryType queryType) {
        return switch (queryType) {
            case PHYSICAL -> BrainType.PHYSICAL;
            case QUANTUM -> BrainType.QUANTUM;
            case FRACTAL -> BrainType.FRACTAL;
            case CREATIVE -> BrainType.CREATIVE;
            case LOGICAL -> BrainType.LOGICAL;
            case EMOTIONAL -> BrainType.EMOTIONAL;
            case SPIRITUAL -> BrainType.SPIRITUAL;
            case TACHYONIC -> BrainType.TACHYONIC;
            case VISUAL -> BrainType.VISUAL;
            case AUDITORY -> BrainType.AUDITORY;
            case EVOLUTIONARY -> BrainType.EVOLUTIONARY;
            case MEMORY -> BrainType.MEMORY;
            case AUTO -> analyzeQueryAndSelectBrain(query);
        };
    }
    
    /**
     * Analyze query and automatically select best brain
     */
    private BrainType analyzeQueryAndSelectBrain(String query) {
        // Simple keyword analysis
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("feel") || lowerQuery.contains("emotion") || lowerQuery.contains("empathy")) {
            return BrainType.EMOTIONAL;
        }
        if (lowerQuery.contains("create") || lowerQuery.contains("imagine") || lowerQuery.contains("innovate")) {
            return BrainType.CREATIVE;
        }
        if (lowerQuery.contains("logic") || lowerQuery.contains("reason") || lowerQuery.contains("analyze")) {
            return BrainType.LOGICAL;
        }
        if (lowerQuery.contains("quantum") || lowerQuery.contains("entangle") || lowerQuery.contains("superposition")) {
            return BrainType.QUANTUM;
        }
        if (lowerQuery.contains("pattern") || lowerQuery.contains("recursive") || lowerQuery.contains("fractal")) {
            return BrainType.FRACTAL;
        }
        if (lowerQuery.contains("conscious") || lowerQuery.contains("aware") || lowerQuery.contains("spirit")) {
            return BrainType.SPIRITUAL;
        }
        if (lowerQuery.contains("fast") || lowerQuery.contains("speed") || lowerQuery.contains("instant")) {
            return BrainType.TACHYONIC;
        }
        if (lowerQuery.contains("video") || lowerQuery.contains("visual") || lowerQuery.contains("image") || lowerQuery.contains("see") || lowerQuery.contains("watch")) {
            return BrainType.VISUAL;
        }
        if (lowerQuery.contains("speech") || lowerQuery.contains("audio") || lowerQuery.contains("sound") || lowerQuery.contains("hear") || lowerQuery.contains("listen") || lowerQuery.contains("voice")) {
            return BrainType.AUDITORY;
        }
        if (lowerQuery.contains("evolve") || lowerQuery.contains("optimize") || lowerQuery.contains("improve") || lowerQuery.contains("adapt") || lowerQuery.contains("learn")) {
            return BrainType.EVOLUTIONARY;
        }
        if (lowerQuery.contains("remember") || lowerQuery.contains("memory") || lowerQuery.contains("recall") || lowerQuery.contains("store") || lowerQuery.contains("retrieve")) {
            return BrainType.MEMORY;
        }
        
        // Default to logical brain
        return BrainType.LOGICAL;
    }
    
    /**
     * Get all brains
     */
    public Map<BrainType, QuantumBrain> getBrains() {
        return new java.util.concurrent.ConcurrentHashMap<>(brains);
    }
    
    /**
     * Get communication system
     */
    public BrainCommunication getCommunication() {
        return communication;
    }
    
    /**
     * Get system statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new java.util.concurrent.ConcurrentHashMap<>();
        stats.put("initialized", initialized);
        stats.put("totalBrains", brains.size());
        stats.put("communication", communication.getStatistics());
        
        // Add per-brain statistics
        Map<String, Object> brainStats = new java.util.concurrent.ConcurrentHashMap<>();
        brains.forEach((type, brain) -> {
            brainStats.put(type.getId(), Map.of(
                "evolution", brain.getEvolutionStatistics(),
                "selfHealing", brain.getSelfHealingStatistics()
            ));
        });
        stats.put("brainStatistics", brainStats);
        
        return stats;
    }
    
    /**
     * Shutdown the Quantum Oracle integration
     */
    public void shutdown() {
        brains.values().forEach(QuantumBrain::shutdown);
        brains.clear();
        if (knowledgeScraper != null) {
            knowledgeScraper.shutdown();
        }
        if (persistentMemory != null) {
            persistentMemory.shutdown();
        }
    }
    
    // ==================== KNOWLEDGE INGESTION METHODS ====================
    // Phase 1: Synaptic Knowledge Ingestion
    
    /**
     * Scrape a single file and extract knowledge into the brain.
     * Creates synaptic connections between concepts and stores in MemoryBrain.
     */
    public CompletableFuture<KnowledgeGraph> ingestFile(Path filePath) {
        if (knowledgeScraper == null) {
            return CompletableFuture.completedFuture(
                new KnowledgeGraph(filePath.toString(), java.util.Collections.emptyList())
            );
        }
        return knowledgeScraper.scrapeFile(filePath);
    }
    
    /**
     * Scrape a directory recursively and extract knowledge.
     */
    public CompletableFuture<KnowledgeGraph> ingestDirectory(Path directoryPath) {
        if (knowledgeScraper == null) {
            return CompletableFuture.completedFuture(
                new KnowledgeGraph(directoryPath.toString(), java.util.Collections.emptyList())
            );
        }
        return knowledgeScraper.scrapeDirectory(directoryPath);
    }
    
    /**
     * Scrape a URL and extract knowledge.
     */
    public CompletableFuture<KnowledgeGraph> ingestURL(String url) {
        if (knowledgeScraper == null) {
            return CompletableFuture.completedFuture(
                new KnowledgeGraph(url, java.util.Collections.emptyList())
            );
        }
        return knowledgeScraper.scrapeURL(url);
    }
    
    /**
     * Scrape a website recursively to a specified depth.
     */
    public CompletableFuture<KnowledgeGraph> ingestWebsite(String baseUrl, int depth) {
        if (knowledgeScraper == null) {
            return CompletableFuture.completedFuture(
                new KnowledgeGraph(baseUrl, java.util.Collections.emptyList())
            );
        }
        return knowledgeScraper.scrapeWebsite(baseUrl, depth);
    }
    
    /**
     * Scrape a GitHub repository.
     */
    public CompletableFuture<KnowledgeGraph> ingestGitHubRepository(String owner, String repo) {
        if (knowledgeScraper == null) {
            return CompletableFuture.completedFuture(
                new KnowledgeGraph("github:" + owner + "/" + repo, java.util.Collections.emptyList())
            );
        }
        return knowledgeScraper.scrapeGitHubRepository(owner, repo);
    }
    
    /**
     * Scrape a GitHub gist.
     */
    public CompletableFuture<KnowledgeGraph> ingestGitHubGist(String gistId) {
        if (knowledgeScraper == null) {
            return CompletableFuture.completedFuture(
                new KnowledgeGraph("gist:" + gistId, java.util.Collections.emptyList())
            );
        }
        return knowledgeScraper.scrapeGitHubGist(gistId);
    }
    
    /**
     * Get the KnowledgeScraper instance for advanced operations.
     */
    public KnowledgeScraper getKnowledgeScraper() {
        return knowledgeScraper;
    }
    
    /**
     * Get the HyperdimensionalMapper instance.
     */
    public HyperdimensionalMapper getHDCMapper() {
        return hdcMapper;
    }
    
    /**
     * Get the HDCSimilarityEngine instance.
     */
    public HDCSimilarityEngine getHDCSimilarityEngine() {
        return hdcEngine;
    }
    
    // ==================== PERSISTENCE METHODS ====================
    // Phase 2: Persistent Memory Layer
    
    /**
     * Store a knowledge graph in persistent memory.
     * Creates blockchain entry and Merkle tree update.
     */
    public UUID persistKnowledgeGraph(KnowledgeGraph graph) {
        if (persistentMemory == null) {
            return null;
        }
        return persistentMemory.store(graph);
    }
    
    /**
     * Retrieve a knowledge graph from persistent memory by ID.
     */
    public KnowledgeGraph retrieveKnowledgeGraph(UUID graphId) {
        if (persistentMemory == null) {
            return null;
        }
        return persistentMemory.retrieve(graphId);
    }
    
    /**
     * Update an existing knowledge graph in persistent memory.
     */
    public void updateKnowledgeGraph(UUID graphId, KnowledgeGraph graph) {
        if (persistentMemory != null) {
            persistentMemory.update(graphId, graph);
        }
    }
    
    /**
     * Delete a knowledge graph from persistent memory.
     */
    public void deleteKnowledgeGraph(UUID graphId) {
        if (persistentMemory != null) {
            persistentMemory.delete(graphId);
        }
    }
    
    /**
     * Get the blockchain for audit trail.
     */
    public BlockChain getBlockChain() {
        if (persistentMemory == null) {
            return null;
        }
        return persistentMemory.getBlockChain();
    }
    
    /**
     * Verify blockchain integrity.
     */
    public boolean verifyBlockchainIntegrity() {
        if (persistentMemory == null) {
            return false;
        }
        return persistentMemory.verifyIntegrity();
    }
    
    /**
     * Create a fractal node from a knowledge graph.
     */
    public FractalNode createFractalNode(KnowledgeGraph subgraph) {
        if (persistentMemory == null) {
            return null;
        }
        return persistentMemory.createFractalNode(subgraph);
    }
    
    /**
     * Get a fractal node by ID.
     */
    public FractalNode getFractalNode(UUID nodeId) {
        if (persistentMemory == null) {
            return null;
        }
        return persistentMemory.getFractalNode(nodeId);
    }
    
    /**
     * Export all knowledge to JSON file.
     */
    public void exportToJSON(Path outputPath) throws Exception {
        if (persistentMemory != null) {
            persistentMemory.exportToJSON(outputPath);
        }
    }
    
    /**
     * Get the PersistentMemory instance for advanced operations.
     */
    public PersistentMemory getPersistentMemory() {
        return persistentMemory;
    }
    
    /**
     * Oracle query types
     */
    public enum OracleQueryType {
        PHYSICAL,     // Physical world interaction
        QUANTUM,      // Quantum phenomena
        FRACTAL,      // Pattern recognition
        CREATIVE,     // Creative generation
        LOGICAL,      // Logical reasoning
        EMOTIONAL,    // Emotional intelligence
        SPIRITUAL,    // Consciousness and awareness
        TACHYONIC,   // FTL processing
        VISUAL,       // Visual generation and understanding
        AUDITORY,     // Speech and audio processing
        EVOLUTIONARY, // Self-evolving logic circuits
        MEMORY,       // Persistent cross-modal knowledge
        AUTO          // Automatic brain selection
    }
    
    /**
     * Oracle response wrapper
     */
    public static class OracleResponse {
        private final String query;
        private final OracleQueryType queryType;
        private final BrainType brainType;
        private final String content;
        private final double confidence;
        private final long latencyMs;
        private final boolean success;
        private final Map<String, Object> metadata;
        
        public OracleResponse(String query, OracleQueryType queryType, BrainType brainType,
                            String content, double confidence, long latencyMs,
                            boolean success, Map<String, Object> metadata) {
            this.query = query;
            this.queryType = queryType;
            this.brainType = brainType;
            this.content = content;
            this.confidence = confidence;
            this.latencyMs = latencyMs;
            this.success = success;
            this.metadata = metadata;
        }
        
        public String getQuery() {
            return query;
        }
        
        public OracleQueryType getQueryType() {
            return queryType;
        }
        
        public BrainType getBrainType() {
            return brainType;
        }
        
        public String getContent() {
            return content;
        }
        
        public double getConfidence() {
            return confidence;
        }
        
        public long getLatencyMs() {
            return latencyMs;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }
}
