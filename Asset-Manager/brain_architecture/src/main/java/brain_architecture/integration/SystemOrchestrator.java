package brain_architecture.integration;

import brain_architecture.brains.*;
import brain_architecture.communication.BrainCommunication;
import brain_architecture.core.*;
import brain_architecture.integration.unified.UnifiedKernelGateway;
import brain_architecture.integration.unified.UnifiedResult;
import brain_architecture.integration.unified.UnifiedTask;
import brain_architecture.kernel.KernelConfiguration;
import brain_architecture.kernel.KernelRunResult;
import brain_architecture.kernel.MemoryHit;
import brain_architecture.kernel.OfflineKernel;
import brain_architecture.kernel.TaskEnvelope;
import brain_architecture.kernel.TaskMode;
import brain_architecture.kernel.ValidationSuite;
import brain_architecture.orchestration.OrchestratorBridge;
import brain_architecture.persistence.*;
import brain_architecture.synapses.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * System Orchestrator - Unified Coordination Layer
 * 
 * This class provides a unified interface for coordinating all system components:
 * - Brain Architecture (12 specialized brains)
 * - Knowledge Ingestion (KnowledgeScraper with HDC)
 * - Persistence Layer (Blockchain, Merkle Tree, Fractal Nodes)
 * 
 * NEW: Fraymus Integration (wire/unwire pattern):
 * - BrainTypeCoordinator - Coordinates brain types with Fraymus components
 * - BackendIntegrator - Integrates with Fraymus backends (OllamaSpine, NeuralTentacle, etc.)
 * - HDCIntegrator - Integrates with Fraymus HyperFormer (10,000-D) instead of duplicate 16,384-D
 * - SentientCoreIntegrator - Integrates with REAL SentientCore instead of stub
 * 
 * LEGACY (to be removed):
 * - SentientCoreBridge - Stub implementation (replaced by SentientCoreIntegrator)
 * - Hybrid Orchestrator - Backend discovery, self-healing, organic growth
 * 
 * SAFETY: All integrations use wire/unwire pattern. No breaking changes.
 */
public class SystemOrchestrator {
    private volatile QuantumOracleIntegration quantumOracle;
    private final Path workspaceRoot;
    private final OfflineKernel offlineKernel;
    private final UnifiedKernelGateway unifiedKernelGateway;
    
    // NEW: Fraymus Integration Components
    private BrainTypeCoordinator brainTypeCoordinator;
    private BackendIntegrator backendIntegrator;
    private HDCIntegrator hdcIntegrator;
    private SentientCoreIntegrator sentientCoreIntegrator;
    
    // LEGACY: To be removed after migration
    private boolean sentientCoreWired;
    private boolean orchestratorWired;
    private SentientCoreBridge sentientCoreBridge;
    private OrchestratorBridge orchestratorBridge;
    
    // Fraymus component flags
    private boolean fraymusComponentsWired = false;
    
    /**
     * Initialize the System Orchestrator with Brain Architecture
     */
    public SystemOrchestrator() {
        this(Path.of(".").toAbsolutePath().normalize());
    }

    public SystemOrchestrator(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot.toAbsolutePath().normalize();
        this.quantumOracle = null;
        this.sentientCoreWired = false;
        this.orchestratorWired = false;
        try {
            this.offlineKernel = new OfflineKernel(KernelConfiguration.defaultForWorkspace(this.workspaceRoot));
            this.unifiedKernelGateway = new UnifiedKernelGateway(this.offlineKernel);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize offline kernel for SystemOrchestrator", e);
        }
    }
    
    /**
     * Wire SentientCore AGI integration
     * This is optional and reversible (unwireSentientCore)
     */
    public void wireSentientCore() {
        if (sentientCoreWired) {
            System.out.println("[SystemOrchestrator] SentientCore already wired");
            return;
        }
        
        this.sentientCoreBridge = new SentientCoreBridge();
        this.sentientCoreBridge.wireSentientCore();
        this.sentientCoreWired = true;
        System.out.println("[SystemOrchestrator] SentientCore wired - Bayesian reasoning available");
    }
    
    /**
     * Unwire SentientCore AGI integration
     * Reverses wireSentientCore() - no breaking changes
     */
    public void unwireSentientCore() {
        if (!sentientCoreWired) {
            System.out.println("[SystemOrchestrator] SentientCore not wired");
            return;
        }
        
        this.sentientCoreBridge.unwireSentientCore();
        this.sentientCoreBridge = null;
        this.sentientCoreWired = false;
        System.out.println("[SystemOrchestrator] SentientCore unwired - using native brain architecture");
    }
    
    /**
     * Wire Hybrid AI Orchestrator integration
     * This is optional and reversible (unwireHybridOrchestrator)
     */
    public void wireHybridOrchestrator() {
        if (orchestratorWired) {
            System.out.println("[SystemOrchestrator] Hybrid Orchestrator already wired");
            return;
        }
        
        // Get BackendEnsemble from any brain (they all share the same ensemble)
        Map<BrainType, QuantumBrain> brains = requireQuantumOracle().getBrains();
        if (brains.isEmpty()) {
            System.out.println("[SystemOrchestrator] No brains available for orchestration");
            return;
        }
        
        // Get BackendEnsemble from the first brain
        BackendEnsemble ensemble = brains.values().iterator().next().getBackendEnsemble();
        
        // Create and wire OrchestratorBridge
        this.orchestratorBridge = new OrchestratorBridge(ensemble);
        this.orchestratorBridge.wireAll(); // Wire all orchestration features
        this.orchestratorWired = true;
        
        // Set the bridge on the ensemble for integration
        ensemble.setOrchestratorBridge(this.orchestratorBridge);
        
        System.out.println("[SystemOrchestrator] Hybrid Orchestrator wired - Backend discovery, self-healing, organic growth available");
    }
    
    /**
     * Unwire Hybrid AI Orchestrator integration
     * Reverses wireHybridOrchestrator() - no breaking changes
     */
    public void unwireHybridOrchestrator() {
        if (!orchestratorWired) {
            System.out.println("[SystemOrchestrator] Hybrid Orchestrator not wired");
            return;
        }
        
        // Unwire all orchestration features
        if (orchestratorBridge != null) {
            orchestratorBridge.unwireAll();
            
            // Clear the bridge from the ensemble
            Map<BrainType, QuantumBrain> brains = requireQuantumOracle().getBrains();
            if (!brains.isEmpty()) {
                BackendEnsemble ensemble = brains.values().iterator().next().getBackendEnsemble();
                ensemble.setOrchestratorBridge(null);
            }
            
            orchestratorBridge = null;
        }
        
        this.orchestratorWired = false;
        System.out.println("[SystemOrchestrator] Hybrid Orchestrator unwired - using basic backend routing");
    }
    
    // ========== NEW: Fraymus Integration Wire Methods ==========
    
    /**
     * Wire Fraymus components to the system
     * This integrates brain_architecture with existing Fraymus Universal components
     * 
     * @param ollamaSpine Layer 0 - Local LLM connection
     * @param hyperFormer Layer 6 - 10,000-D tensor engine
     * @param holoGraph Layer 8 - O(1) knowledge graph
     * @param godMode Layer 12 - Swarm intelligence aggregator
     * @param phiHarmonicKernel Layer 13 - Sacred geometry scheduler
     * @param fraynixOS Layer 1 - Main runtime
     * @param akashicRecord Layer 0 - Universal persistent memory
     * @param neuralTentacle Layer 12 - HTTP client for Claude/OpenAI
     * @param clawConnector Layer 0 - OpenClaw/NeoClaw connector
     * @param vectorVault Layer 0 - Vector embedding storage
     * @param brainMapper brain_architecture HDC mapper
     * @param brainSimilarityEngine brain_architecture HDC similarity engine
     */
    public void wireFraymusComponents(
            Object ollamaSpine,
            Object hyperFormer,
            Object holoGraph,
            Object godMode,
            Object phiHarmonicKernel,
            Object fraynixOS,
            Object akashicRecord,
            Object neuralTentacle,
            Object clawConnector,
            Object vectorVault,
            HyperdimensionalMapper brainMapper,
            HDCSimilarityEngine brainSimilarityEngine) {
        
        // Initialize integrators
        this.brainTypeCoordinator = new BrainTypeCoordinator();
        this.backendIntegrator = new BackendIntegrator();
        this.hdcIntegrator = new HDCIntegrator();
        this.sentientCoreIntegrator = new SentientCoreIntegrator();
        
        // Wire BrainTypeCoordinator
        brainTypeCoordinator.wireFraymusComponents(
            ollamaSpine, hyperFormer, holoGraph, godMode,
            phiHarmonicKernel, fraynixOS, akashicRecord);
        
        // Wire BackendIntegrator
        backendIntegrator.wireFraymusBackends(
            ollamaSpine, neuralTentacle, clawConnector, vectorVault);
        
        // Wire HDCIntegrator
        hdcIntegrator.wireFraymusHDC(
            hyperFormer, holoGraph, null, brainMapper, brainSimilarityEngine);
        
        this.fraymusComponentsWired = true;
        System.out.println("[SystemOrchestrator] Fraymus components wired - Using existing infrastructure");
    }
    
    /**
     * Wire REAL SentientCore components (from src/main/java/fraymus/neural/SentientCore.java)
     * This replaces the stub SentientCoreBridge with real integration
     * 
     * @param causalGraph CausalGraph
     * @param sufficientStats SufficientStats
     * @param fisherOracle FisherOracle
     * @param analogyEngine AnalogyEngine
     * @param conceptLattice ConceptLattice
     * @param physicsEncoder PhysicsEncoder
     */
    public void wireRealSentientCore(
            Object causalGraph,
            Object sufficientStats,
            Object fisherOracle,
            Object analogyEngine,
            Object conceptLattice,
            Object physicsEncoder) {
        
        if (sentientCoreIntegrator == null) {
            this.sentientCoreIntegrator = new SentientCoreIntegrator();
        }
        
        sentientCoreIntegrator.wireSentientCore(
            causalGraph, sufficientStats, fisherOracle,
            analogyEngine, conceptLattice, physicsEncoder);
        
        System.out.println("[SystemOrchestrator] REAL SentientCore wired - Using Bayesian reasoning engine");
    }
    
    /**
     * Unwire Fraymus components (revert to standalone mode)
     */
    public void unwireFraymusComponents() {
        if (brainTypeCoordinator != null) {
            brainTypeCoordinator.unwireFraymusComponents();
            brainTypeCoordinator = null;
        }
        
        if (backendIntegrator != null) {
            backendIntegrator.unwireFraymusBackends();
            backendIntegrator = null;
        }
        
        if (hdcIntegrator != null) {
            hdcIntegrator.unwireFraymusHDC();
            hdcIntegrator = null;
        }
        
        this.fraymusComponentsWired = false;
        System.out.println("[SystemOrchestrator] Fraymus components unwired - Using standalone brain_architecture");
    }
    
    /**
     * Unwire REAL SentientCore (revert to standalone mode)
     */
    public void unwireRealSentientCore() {
        if (sentientCoreIntegrator != null) {
            sentientCoreIntegrator.unwireSentientCore();
            // Keep the integrator for potential re-wiring
        }
        
        System.out.println("[SystemOrchestrator] REAL SentientCore unwired - Using native brain_architecture");
    }
    
    // ========== Accessor Methods for New Integrators ==========
    
    /**
     * Get BrainTypeCoordinator (if wired)
     */
    public BrainTypeCoordinator getBrainTypeCoordinator() {
        return brainTypeCoordinator;
    }
    
    /**
     * Get BackendIntegrator (if wired)
     */
    public BackendIntegrator getBackendIntegrator() {
        return backendIntegrator;
    }
    
    /**
     * Get HDCIntegrator (if wired)
     */
    public HDCIntegrator getHDCIntegrator() {
        return hdcIntegrator;
    }
    
    /**
     * Get SentientCoreIntegrator (if wired)
     */
    public SentientCoreIntegrator getSentientCoreIntegrator() {
        return sentientCoreIntegrator;
    }
    
    // ========== Delegated Methods from QuantumOracleIntegration ==========
    
    /**
     * Process a query through the brain architecture
     */
    public CompletableFuture<QuantumOracleIntegration.OracleResponse> processQuery(
            String query, QuantumOracleIntegration.OracleQueryType queryType) {
        UnifiedTask task = buildLegacyUnifiedTask(query, queryType, null, "processQuery");
        return runOfflineTask(UnifiedKernelGateway.toEnvelope(task), ValidationSuite.empty())
            .thenApply(runResult -> legacyOracleResponse(query, queryType, selectLegacyBrain(queryType, null), task, runResult, "SystemOrchestrator.processQuery"));
    }
    
    /**
     * Process a query with multi-brain collaboration
     */
    public CompletableFuture<QuantumOracleIntegration.OracleResponse> processMultiBrainQuery(
            String query, QuantumOracleIntegration.OracleQueryType queryType, BrainType[] brainTypes) {
        UnifiedTask task = buildLegacyUnifiedTask(query, queryType, brainTypes, "processMultiBrainQuery");
        return runOfflineTask(UnifiedKernelGateway.toEnvelope(task), ValidationSuite.empty())
            .thenApply(runResult -> legacyOracleResponse(query, queryType, selectLegacyBrain(queryType, brainTypes), task, runResult, "SystemOrchestrator.processMultiBrainQuery"));
    }

    public CompletableFuture<KernelRunResult> runOfflineTask(TaskEnvelope task, ValidationSuite validationSuite) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return offlineKernel.runTask(task, validationSuite == null ? ValidationSuite.empty() : validationSuite);
            } catch (IOException e) {
                throw new IllegalStateException("Offline kernel task failed", e);
            }
        });
    }

    public CompletableFuture<KernelRunResult> runOfflineTask(TaskEnvelope task) {
        return runOfflineTask(task, ValidationSuite.empty());
    }

    public CompletableFuture<UnifiedResult> runUnifiedTask(UnifiedTask task, ValidationSuite validationSuite) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return unifiedKernelGateway.run(task, validationSuite == null ? ValidationSuite.empty() : validationSuite);
            } catch (IOException e) {
                throw new IllegalStateException("Unified kernel task failed", e);
            }
        });
    }

    public CompletableFuture<UnifiedResult> runUnifiedTask(UnifiedTask task) {
        return runUnifiedTask(task, ValidationSuite.empty());
    }
    
    /**
     * Ingest knowledge from a file
     */
    public CompletableFuture<KnowledgeGraph> ingestFile(Path filePath) {
        return requireQuantumOracle().ingestFile(filePath);
    }
    
    /**
     * Ingest knowledge from a directory
     */
    public CompletableFuture<KnowledgeGraph> ingestDirectory(Path directoryPath) {
        return requireQuantumOracle().ingestDirectory(directoryPath);
    }
    
    /**
     * Persist a knowledge graph to blockchain storage
     */
    public UUID persistKnowledgeGraph(KnowledgeGraph graph) {
        return requireQuantumOracle().persistKnowledgeGraph(graph);
    }
    
    /**
     * Retrieve a knowledge graph by ID
     */
    public KnowledgeGraph retrieveKnowledgeGraph(UUID graphId) {
        return requireQuantumOracle().retrieveKnowledgeGraph(graphId);
    }
    
    /**
     * Create a fractal node for hierarchical storage
     */
    public FractalNode createFractalNode(KnowledgeGraph subgraph) {
        return requireQuantumOracle().createFractalNode(subgraph);
    }
    
    /**
     * Verify blockchain integrity
     */
    public boolean verifyBlockchainIntegrity() {
        return requireQuantumOracle().verifyBlockchainIntegrity();
    }
    
    // ========== SentientCore Delegation Methods ==========
    
    /**
     * Process a query using SentientCore (if wired)
     */
    public String processQueryWithSentientCore(String query) {
        if (!sentientCoreWired || sentientCoreBridge == null) {
            return "SentientCore: NOT WIRED";
        }
        return sentientCoreBridge.processQuery(query);
    }
    
    /**
     * Learn from a statement using SentientCore (if wired)
     */
    public String learnWithSentientCore(String statement) {
        if (!sentientCoreWired || sentientCoreBridge == null) {
            return "SentientCore: NOT WIRED";
        }
        return sentientCoreBridge.learn(statement);
    }
    
    /**
     * Get SentientCore status (if wired)
     */
    public String getSentientCoreStatus() {
        if (!sentientCoreWired || sentientCoreBridge == null) {
            return "SentientCore: NOT WIRED";
        }
        return sentientCoreBridge.getStatus();
    }
    
    /**
     * Get SentientCore bridge (if wired)
     */
    public SentientCoreBridge getSentientCoreBridge() {
        return sentientCoreBridge;
    }
    
    /**
     * Get Orchestrator bridge (if wired)
     */
    public OrchestratorBridge getOrchestratorBridge() {
        return orchestratorBridge;
    }
    
    // ========== System Status and Statistics ==========
    
    /**
     * Get comprehensive system status
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        QuantumOracleIntegration oracle = quantumOracle;
        
        // Brain Architecture status
        status.put("quantumOracle", Map.of(
            "initialized", oracle != null,
            "brains", oracle == null ? 0 : oracle.getBrains().size(),
            "statistics", oracle == null ? Map.of() : oracle.getStatistics()
        ));
        
        // NEW: Fraymus integration status
        Map<String, Object> fraymusIntegrationStatus = new HashMap<>();
        fraymusIntegrationStatus.put("fraymusComponentsWired", fraymusComponentsWired);
        if (brainTypeCoordinator != null) {
            fraymusIntegrationStatus.put("brainTypeCoordinator", brainTypeCoordinator.getStatus());
        }
        if (backendIntegrator != null) {
            fraymusIntegrationStatus.put("backendIntegrator", backendIntegrator.getStatus());
        }
        if (hdcIntegrator != null) {
            fraymusIntegrationStatus.put("hdcIntegrator", hdcIntegrator.getStatus());
        }
        if (sentientCoreIntegrator != null) {
            fraymusIntegrationStatus.put("sentientCoreIntegrator", sentientCoreIntegrator.getStatus());
        }
        status.put("fraymusIntegrations", fraymusIntegrationStatus);
        
        // LEGACY: Old integration status (to be removed)
        Map<String, Object> integrationStatus = new HashMap<>();
        integrationStatus.put("sentientCore", sentientCoreWired);
        if (sentientCoreWired && sentientCoreBridge != null) {
            integrationStatus.put("sentientCoreStatus", getSentientCoreStatus());
        }
        integrationStatus.put("hybridOrchestrator", orchestratorWired);
        if (orchestratorWired && orchestratorBridge != null) {
            integrationStatus.put("orchestratorStatus", orchestratorBridge.getStatus());
        }
        status.put("legacyIntegrations", integrationStatus);
        status.put("offlineKernel", offlineKernel.getStatus());
        
        return status;
    }

    public OfflineKernel getOfflineKernel() {
        return offlineKernel;
    }

    public UnifiedKernelGateway getUnifiedKernelGateway() {
        return unifiedKernelGateway;
    }

    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }
    
    /**
     * Get all brains from the system
     */
    public Map<BrainType, QuantumBrain> getBrains() {
        return requireQuantumOracle().getBrains();
    }
    
    /**
     * Get brain communication system
     */
    public BrainCommunication getCommunication() {
        return requireQuantumOracle().getCommunication();
    }
    
    /**
     * Get knowledge scraper
     */
    public KnowledgeScraper getKnowledgeScraper() {
        return requireQuantumOracle().getKnowledgeScraper();
    }
    
    /**
     * Get persistent memory coordinator
     */
    public PersistentMemory getPersistentMemory() {
        return requireQuantumOracle().getPersistentMemory();
    }
    
    // ========== Lifecycle Management ==========
    
    /**
     * Shutdown the system gracefully
     */
    public void shutdown() {
        // Unwire NEW Fraymus integrations first
        if (fraymusComponentsWired) {
            unwireFraymusComponents();
        }
        if (sentientCoreIntegrator != null) {
            unwireRealSentientCore();
        }
        
        // Unwire LEGACY integrations
        if (orchestratorWired) {
            unwireHybridOrchestrator();
        }
        if (sentientCoreWired) {
            unwireSentientCore();
        }
        
        // Shutdown Quantum Oracle only if the legacy subsystem was activated.
        QuantumOracleIntegration oracle = quantumOracle;
        if (oracle != null) {
            oracle.shutdown();
        }
        offlineKernel.close();
    }

    private QuantumOracleIntegration requireQuantumOracle() {
        QuantumOracleIntegration oracle = quantumOracle;
        if (oracle != null) {
            return oracle;
        }
        synchronized (this) {
            if (quantumOracle == null) {
                quantumOracle = new QuantumOracleIntegration();
            }
            return quantumOracle;
        }
    }

    private UnifiedTask buildLegacyUnifiedTask(
            String query,
            QuantumOracleIntegration.OracleQueryType queryType,
            BrainType[] brainTypes,
            String legacyMethod) {
        BrainType preferredBrain = selectLegacyBrain(queryType, brainTypes);
        UnifiedTask.Builder builder = UnifiedTask.builder(query == null ? "" : query)
            .mode(mapLegacyMode(queryType))
            .addConstraint("offline only")
            .addConstraint("legacy compatibility wrapper")
            .addConstraint("kernel-owned execution path")
            .strategy("legacy.entrypoint", legacyMethod)
            .strategy("legacy.queryType", queryType == null ? "auto" : queryType.name().toLowerCase(Locale.ROOT))
            .strategy("preferredBrain", preferredBrain.getId());
        if (brainTypes != null && brainTypes.length > 0) {
            builder.addConstraint("requested brain set: " + Arrays.toString(brainTypes));
            builder.strategy("requestedBrains", joinBrainTypes(brainTypes));
        }
        return builder.build();
    }

    private BrainType selectLegacyBrain(
            QuantumOracleIntegration.OracleQueryType queryType,
            BrainType[] brainTypes) {
        if (brainTypes != null && brainTypes.length > 0 && brainTypes[0] != null) {
            return brainTypes[0];
        }
        if (queryType == null || queryType == QuantumOracleIntegration.OracleQueryType.AUTO) {
            return BrainType.LOGICAL;
        }
        try {
            return BrainType.valueOf(queryType.name());
        } catch (IllegalArgumentException ignored) {
            return BrainType.LOGICAL;
        }
    }

    private TaskMode mapLegacyMode(QuantumOracleIntegration.OracleQueryType queryType) {
        if (queryType == QuantumOracleIntegration.OracleQueryType.EVOLUTIONARY) {
            return TaskMode.AUTOMATED;
        }
        return TaskMode.SUPERVISED;
    }

    private String joinBrainTypes(BrainType[] brainTypes) {
        return Arrays.stream(brainTypes == null ? new BrainType[0] : brainTypes)
            .filter(Objects::nonNull)
            .map(BrainType::getId)
            .reduce((left, right) -> left + "," + right)
            .orElse("");
    }

    private QuantumOracleIntegration.OracleResponse legacyOracleResponse(
            String query,
            QuantumOracleIntegration.OracleQueryType queryType,
            BrainType selectedBrain,
            UnifiedTask task,
            KernelRunResult runResult,
            String entrypoint) {
        long latencyMs = legacyLatency(runResult);
        boolean success = runResult.getVerifierResult() != null && runResult.getVerifierResult().isSuccess();
        String content = buildLegacyContent(runResult);
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("path", "kernel-forwarded");
        metadata.put("legacyEntrypoint", entrypoint);
        metadata.put("workspaceRoot", workspaceRoot.toString());
        metadata.put("taskId", runResult.getTrace() == null ? "unknown" : runResult.getTrace().getTaskId().toString());
        metadata.put("strategy", runResult.getStrategy());
        metadata.put("adapterId", runResult.getAdapterId());
        metadata.put("outcome", runResult.getTrace() == null ? "unknown" : runResult.getTrace().getOutcome());
        metadata.put("validationPassed", success);
        metadata.put("passedChecks", runResult.getVerifierResult() == null ? List.of() : runResult.getVerifierResult().getPassedChecks());
        metadata.put("failedChecks", runResult.getVerifierResult() == null ? List.of() : runResult.getVerifierResult().getFailedChecks());
        metadata.put("warnings", runResult.getVerifierResult() == null ? List.of() : runResult.getVerifierResult().getWarnings());
        metadata.put("requestedBrains", task.getStrategyMetadata().getOrDefault("requestedBrains", selectedBrain.getId()));
        metadata.put("preferredBrain", selectedBrain.getId());
        metadata.put("memoryHits", summarizeMemoryHits(runResult.getMemoryHits()));
        metadata.put("timingsMs", runResult.getVerifierResult() == null ? Map.of() : runResult.getVerifierResult().getTimingsMs());
        return new QuantumOracleIntegration.OracleResponse(
            query,
            queryType == null ? QuantumOracleIntegration.OracleQueryType.AUTO : queryType,
            selectedBrain,
            content,
            legacyConfidence(runResult),
            latencyMs,
            success,
            metadata
        );
    }

    private String buildLegacyContent(KernelRunResult runResult) {
        StringBuilder builder = new StringBuilder();
        builder.append("Kernel outcome: ")
            .append(runResult.getTrace() == null ? "unknown" : runResult.getTrace().getOutcome())
            .append(System.lineSeparator())
            .append("Strategy: ")
            .append(runResult.getStrategy())
            .append(System.lineSeparator())
            .append("Adapter: ")
            .append(runResult.getAdapterId());
        if (runResult.getVerifierResult() != null) {
            builder.append(System.lineSeparator())
                .append("Validation: ")
                .append(runResult.getVerifierResult().isSuccess() ? "passed" : "failed");
            if (!runResult.getVerifierResult().getFailedChecks().isEmpty()) {
                builder.append(System.lineSeparator())
                    .append("Failed checks: ")
                    .append(String.join(", ", runResult.getVerifierResult().getFailedChecks()));
            }
        }
        List<String> hitSummary = summarizeMemoryHits(runResult.getMemoryHits());
        if (!hitSummary.isEmpty()) {
            builder.append(System.lineSeparator())
                .append("Memory hits: ")
                .append(String.join(", ", hitSummary));
        }
        return builder.toString();
    }

    private List<String> summarizeMemoryHits(List<MemoryHit> hits) {
        if (hits == null || hits.isEmpty()) {
            return List.of();
        }
        return hits.stream()
            .limit(3)
            .map(hit -> {
                String sourceId = hit.getDocument() == null ? "unknown" : hit.getDocument().getSourceId();
                return sourceId + "@" + String.format(Locale.ROOT, "%.3f", hit.getTotalScore());
            })
            .toList();
    }

    private long legacyLatency(KernelRunResult runResult) {
        if (runResult.getTrace() != null && runResult.getTrace().getStartedAt() != null && runResult.getTrace().getEndedAt() != null) {
            return Math.max(0L, java.time.Duration.between(runResult.getTrace().getStartedAt(), runResult.getTrace().getEndedAt()).toMillis());
        }
        if (runResult.getVerifierResult() == null || runResult.getVerifierResult().getTimingsMs().isEmpty()) {
            return 0L;
        }
        return runResult.getVerifierResult().getTimingsMs().values().stream()
            .mapToLong(Long::longValue)
            .sum();
    }

    private double legacyConfidence(KernelRunResult runResult) {
        if (runResult.getVerifierResult() == null) {
            return 0.25;
        }
        double confidence = runResult.getVerifierResult().isSuccess() ? 0.75 : 0.35;
        confidence += Math.min(0.15, runResult.getMemoryHits().size() * 0.03);
        if (!runResult.getVerifierResult().getWarnings().isEmpty()) {
            confidence -= Math.min(0.10, runResult.getVerifierResult().getWarnings().size() * 0.03);
        }
        return Math.max(0.0, Math.min(1.0, confidence));
    }
}
