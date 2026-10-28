package brain_architecture;

import brain_architecture.communication.BrainCommunication;
import brain_architecture.core.BackendEnsemble;
import brain_architecture.core.BrainType;
import brain_architecture.core.QuantumBrain;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.integration.SentientCoreBridge;
import brain_architecture.integration.SystemOrchestrator;
import brain_architecture.integration.unified.UnifiedKernelGateway;
import brain_architecture.integration.unified.UnifiedResult;
import brain_architecture.integration.unified.UnifiedTask;
import brain_architecture.kernel.KernelRunResult;
import brain_architecture.kernel.MemoryBenchmarkReport;
import brain_architecture.kernel.MutationCandidate;
import brain_architecture.kernel.OfflineKernel;
import brain_architecture.kernel.FoundationalSubstrateReport;
import brain_architecture.kernel.PdfDistillationReport;
import brain_architecture.kernel.ShadowMutationEngine;
import brain_architecture.kernel.SubstrateResult;
import brain_architecture.kernel.TaskEnvelope;
import brain_architecture.kernel.ValidationSuite;
import brain_architecture.orchestration.OrchestratorBridge;
import brain_architecture.persistence.FractalNode;
import brain_architecture.persistence.PersistentMemory;
import brain_architecture.synapses.KnowledgeGraph;
import brain_architecture.synapses.KnowledgeScraper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * FraynixSystem - Single Entry Point for the Entire System
 * 
 * This class provides a unified interface for all system components:
 * - Brain Architecture (12 specialized brains with 6 AI backends each)
 * - SentientCore AGI (Bayesian reasoning, HDC, memory systems) - optional
 * - Hybrid Orchestrator (Backend discovery, self-healing, organic growth) - optional
 * - Knowledge Ingestion (Files, directories, URLs, websites, GitHub)
 * - Persistence Layer (Blockchain, Merkle Tree, Fractal Nodes)
 * 
 * All advanced features are optional via wire/unwire pattern.
 * The system operates normally without any optional components.
 * 
 * SAFETY: No breaking changes. All integrations are reversible.
 */
public class FraynixSystem {
    
    private final SystemOrchestrator orchestrator;
    private OfflineKernel offlineKernel;
    private UnifiedKernelGateway unifiedKernelGateway;
    private boolean initialized;
    
    /**
     * Create a new FraynixSystem instance
     * The system is not initialized until initialize() is called
     */
    public FraynixSystem() {
        this(Path.of(".").toAbsolutePath().normalize());
    }

    public FraynixSystem(Path workspaceRoot) {
        this.orchestrator = new SystemOrchestrator(workspaceRoot);
        this.initialized = false;
    }
    
    /**
     * Initialize the FraynixSystem
     * This initializes the Brain Architecture core components
     */
    public void initialize() {
        if (initialized) {
            System.out.println("[FraynixSystem] Already initialized");
            return;
        }
        
        System.out.println("[FraynixSystem] Initializing FraynixSystem...");
        System.out.println("[FraynixSystem] Brain Architecture initialized");
        System.out.println("[FraynixSystem] 12 specialized brains ready");
        System.out.println("[FraynixSystem] 6 AI backends per brain available");
        this.offlineKernel = orchestrator.getOfflineKernel();
        this.unifiedKernelGateway = orchestrator.getUnifiedKernelGateway();
        System.out.println("[FraynixSystem] Offline kernel initialized");
        
        this.initialized = true;
        System.out.println("[FraynixSystem] Initialization complete");
    }
    
    /**
     * Shutdown the FraynixSystem gracefully
     * This unwires all integrations and shuts down core components
     */
    public void shutdown() {
        if (!initialized) {
            System.out.println("[FraynixSystem] Not initialized");
            return;
        }
        
        System.out.println("[FraynixSystem] Shutting down...");
        offlineKernel = null;
        unifiedKernelGateway = null;
        orchestrator.shutdown();
        this.initialized = false;
        System.out.println("[FraynixSystem] Shutdown complete");
    }
    
    /**
     * Check if the system is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    // ========== Unified Query Interface ==========
    
    /**
     * Process a query through the brain architecture
     * This is the primary unified query interface
     * 
     * @param query The query to process
     * @param queryType The type of query (REASONING, CREATIVE, ANALYSIS, etc.)
     * @return CompletableFuture containing the oracle response
     */
    @Deprecated(since = "north-star")
    public CompletableFuture<QuantumOracleIntegration.OracleResponse> processQuery(
            String query, QuantumOracleIntegration.OracleQueryType queryType) {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.processQuery(query, queryType);
    }
    
    /**
     * Process a query with multi-brain collaboration
     * 
     * @param query The query to process
     * @param queryType The type of query
     * @param brainTypes The brains to collaborate
     * @return CompletableFuture containing the oracle response
     */
    @Deprecated(since = "north-star")
    public CompletableFuture<QuantumOracleIntegration.OracleResponse> processMultiBrainQuery(
            String query, QuantumOracleIntegration.OracleQueryType queryType, BrainType[] brainTypes) {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.processMultiBrainQuery(query, queryType, brainTypes);
    }

    // ========== Offline Kernel Path ==========

    /**
     * Run a typed task through the offline kernel pipeline:
     * TaskEnvelope -> Planner -> Executor -> Verifier -> Memory -> Mutation Engine.
     */
    public CompletableFuture<KernelRunResult> runOfflineTask(TaskEnvelope task, ValidationSuite validationSuite) {
        if (!initialized || offlineKernel == null) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.runOfflineTask(task, validationSuite);
    }

    /**
     * Phase A unified forwarding API.
     * This is an additive facade that maps UnifiedTask into the existing typed kernel pipeline.
     */
    public CompletableFuture<UnifiedResult> runUnifiedTask(UnifiedTask task, ValidationSuite validationSuite) {
        if (!initialized || unifiedKernelGateway == null) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.runUnifiedTask(task, validationSuite);
    }

    /**
     * Convenience overload with empty validation suite.
     */
    public CompletableFuture<UnifiedResult> runUnifiedTask(UnifiedTask task) {
        return runUnifiedTask(task, ValidationSuite.empty());
    }

    /**
     * Ingest a local artifact into the hybrid offline memory index.
     */
    public int ingestArtifactToKernel(Path artifactPath) {
        if (!initialized || offlineKernel == null) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        try {
            return offlineKernel.ingestArtifact(artifactPath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to ingest artifact into offline kernel", e);
        }
    }

    /**
     * Apply a mutation candidate through the shadow mutation pipeline.
     */
    public ShadowMutationEngine.MutationReport applyMutationCandidate(
            MutationCandidate candidate,
            ValidationSuite validationSuite) {
        if (!initialized || offlineKernel == null) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        try {
            return offlineKernel.applyMutationCandidate(candidate, validationSuite);
        } catch (IOException e) {
            throw new IllegalStateException("Mutation candidate failed", e);
        }
    }

    /**
     * Compare lexical-only, HDC-only, and hybrid retrieval on local artifacts.
     */
    public MemoryBenchmarkReport benchmarkOfflineMemory(String query, java.util.List<Path> artifactHints, int topK) {
        if (!initialized || offlineKernel == null) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        try {
            return offlineKernel.benchmarkMemory(query, artifactHints, topK);
        } catch (IOException e) {
            throw new IllegalStateException("Offline memory benchmark failed", e);
        }
    }

    /**
     * Distill external PDFs into de-duplicated, labeled offline memory chunks.
     */
    public PdfDistillationReport distillPdfCorpus(java.util.List<Path> pdfPaths) {
        if (!initialized || offlineKernel == null) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        try {
            return offlineKernel.distillPdfCorpus(pdfPaths);
        } catch (IOException e) {
            throw new IllegalStateException("PDF corpus distillation failed", e);
        }
    }

    /**
     * Bootstrap symbolic and local model substrates into kernel-owned memory.
     */
    public FoundationalSubstrateReport bootstrapFoundationalSubstrates(java.util.List<Path> modelRoots) {
        if (!initialized || offlineKernel == null) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        try {
            return offlineKernel.bootstrapFoundationalSubstrates(modelRoots);
        } catch (IOException e) {
            throw new IllegalStateException("Foundational substrate bootstrap failed", e);
        }
    }

    /**
     * Run a direct kernel-owned local model inference without planner/verifier overhead.
     */
    public SubstrateResult inferLocalModel(
            String prompt,
            java.util.List<Path> modelRoots,
            String modelId,
            String modelRunner,
            String modelBackend,
            int maxTokens) {
        if (!initialized || offlineKernel == null) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        try {
            return offlineKernel.inferLocalModel(prompt, modelRoots, modelId, modelRunner, modelBackend, maxTokens);
        } catch (IOException e) {
            throw new IllegalStateException("Local model inference failed", e);
        }
    }

    /**
     * Get the offline kernel instance.
     */
    public OfflineKernel getOfflineKernel() {
        return offlineKernel;
    }
    
    // ========== Knowledge Ingestion ==========
    
    /**
     * Ingest knowledge from a file
     * 
     * @param filePath Path to the file to ingest
     * @return CompletableFuture containing the knowledge graph
     */
    public CompletableFuture<KnowledgeGraph> ingestFile(Path filePath) {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.ingestFile(filePath);
    }
    
    /**
     * Ingest knowledge from a directory
     * 
     * @param directoryPath Path to the directory to ingest
     * @return CompletableFuture containing the knowledge graph
     */
    public CompletableFuture<KnowledgeGraph> ingestDirectory(Path directoryPath) {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.ingestDirectory(directoryPath);
    }
    
    // ========== Persistence ==========
    
    /**
     * Persist a knowledge graph to blockchain storage
     * 
     * @param graph The knowledge graph to persist
     * @return UUID of the persisted graph
     */
    public UUID persistKnowledgeGraph(KnowledgeGraph graph) {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.persistKnowledgeGraph(graph);
    }
    
    /**
     * Retrieve a knowledge graph by ID
     * 
     * @param graphId UUID of the graph to retrieve
     * @return The knowledge graph
     */
    public KnowledgeGraph retrieveKnowledgeGraph(UUID graphId) {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.retrieveKnowledgeGraph(graphId);
    }
    
    /**
     * Create a fractal node for hierarchical storage
     * 
     * @param subgraph The knowledge graph to store as a fractal node
     * @return The created fractal node
     */
    public FractalNode createFractalNode(KnowledgeGraph subgraph) {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.createFractalNode(subgraph);
    }
    
    /**
     * Verify blockchain integrity
     * 
     * @return true if blockchain is valid, false otherwise
     */
    public boolean verifyBlockchainIntegrity() {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        return orchestrator.verifyBlockchainIntegrity();
    }
    
    // ========== SentientCore Integration (Optional) ==========
    
    /**
     * Wire SentientCore AGI integration
     * Enables Bayesian reasoning, HDC, and memory systems
     */
    public void wireSentientCore() {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        orchestrator.wireSentientCore();
    }
    
    /**
     * Unwire SentientCore AGI integration
     * Disables SentientCore - system uses native brain architecture
     */
    public void unwireSentientCore() {
        orchestrator.unwireSentientCore();
    }
    
    /**
     * Process a query using SentientCore (if wired)
     * 
     * @param query The query to process
     * @return Response from SentientCore or "NOT WIRED" message
     */
    public String processQueryWithSentientCore(String query) {
        return orchestrator.processQueryWithSentientCore(query);
    }
    
    /**
     * Learn from a statement using SentientCore (if wired)
     * 
     * @param statement The statement to learn from
     * @return Learning confirmation or "NOT WIRED" message
     */
    public String learnWithSentientCore(String statement) {
        return orchestrator.learnWithSentientCore(statement);
    }
    
    /**
     * Get SentientCore status (if wired)
     * 
     * @return Status string or "NOT WIRED" message
     */
    public String getSentientCoreStatus() {
        return orchestrator.getSentientCoreStatus();
    }
    
    /**
     * Get SentientCore bridge (if wired)
     * 
     * @return SentientCoreBridge instance or null if not wired
     */
    public SentientCoreBridge getSentientCoreBridge() {
        return orchestrator.getSentientCoreBridge();
    }
    
    // ========== Hybrid Orchestrator Integration (Optional) ==========
    
    /**
     * Wire Hybrid AI Orchestrator integration
     * Enables backend discovery, self-healing, and organic growth
     */
    public void wireHybridOrchestrator() {
        if (!initialized) {
            throw new IllegalStateException("FraynixSystem not initialized. Call initialize() first.");
        }
        orchestrator.wireHybridOrchestrator();
    }
    
    /**
     * Unwire Hybrid AI Orchestrator integration
     * Disables orchestration - system uses basic backend routing
     */
    public void unwireHybridOrchestrator() {
        orchestrator.unwireHybridOrchestrator();
    }
    
    /**
     * Get Orchestrator bridge (if wired)
     * 
     * @return OrchestratorBridge instance or null if not wired
     */
    public OrchestratorBridge getOrchestratorBridge() {
        return orchestrator.getOrchestratorBridge();
    }
    
    // ========== System Status and Statistics ==========
    
    /**
     * Get comprehensive system status
     * 
     * @return Map containing system status information
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = orchestrator.getSystemStatus();
        status.put("fraynixSystem", Map.of(
            "initialized", initialized
        ));
        if (offlineKernel != null) {
            status.put("offlineKernel", offlineKernel.getStatus());
        }
        return status;
    }
    
    /**
     * Get all brains from the system
     * 
     * @return Map of brain types to brain instances
     */
    public Map<BrainType, QuantumBrain> getBrains() {
        return orchestrator.getBrains();
    }
    
    /**
     * Get brain communication system
     * 
     * @return BrainCommunication instance
     */
    public BrainCommunication getCommunication() {
        return orchestrator.getCommunication();
    }
    
    /**
     * Get knowledge scraper
     * 
     * @return KnowledgeScraper instance
     */
    public KnowledgeScraper getKnowledgeScraper() {
        return orchestrator.getKnowledgeScraper();
    }
    
    /**
     * Get persistent memory coordinator
     * 
     * @return PersistentMemory instance
     */
    public PersistentMemory getPersistentMemory() {
        return orchestrator.getPersistentMemory();
    }
    
    /**
     * Print system status to console
     */
    public void printSystemStatus() {
        Map<String, Object> status = getSystemStatus();
        System.out.println("=== FraynixSystem Status ===");
        System.out.println("Initialized: " + status.get("fraynixSystem"));
        System.out.println("Quantum Oracle: " + status.get("quantumOracle"));
        System.out.println("Integrations: " + status.get("integrations"));
        System.out.println("==========================");
    }
}
