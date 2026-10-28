package fraymus.brain.unified;

import fraymus.brain.creative.dionysian.DionysianCortex;
import fraymus.contract.SystemStateContract;
import fraymus.core.OllamaBridge;
import fraymus.efficiency.EnergyEfficientInference;
import fraymus.embodied.EmbodiedAI;
import fraymus.inference.ActiveInferenceEngine;
import fraymus.model.AkashicModelMemory;
import fraymus.model.CapabilityProfile;
import fraymus.model.EvaluationHarness;
import fraymus.model.ModelAdapter;
import fraymus.model.ModelRegistryIntegration;
import fraymus.model.ModelSelector;
import fraymus.model.PromptCompiler;
import fraymus.neuromorphic.NeuromorphicInterface;
import fraymus.quantum.QuantumHybridComputer;
import fraymus.sentience.SentienceObserver;
import fraymus.worldmodel.WorldModel;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Central orchestrator for the unified brain system.
 * Handles brain selection, fusion, conflict resolution, and response synthesis.
 */
public class UnifiedBrainOrchestrator {
    private static final long BRAIN_PROCESS_TIMEOUT_MS = 8_000L;

    private final BrainRegistry registry;
    private final PhiHarmonicCore phiCore;
    private final HDCEngine hdcEngine;
    private final MemorySystem memorySystem;
    private final SolutionScorer solutionScorer;
    private final AdversarialComparator adversarialComparator;
    private final long startedAtMs;
    private final OrganBrainMapper organBrainMapper;

    // Model Registry routing components (Phase 22.5+)
    private ModelRegistryIntegration modelRegistryIntegration;
    private ModelSelector modelSelector;
    private PromptCompiler promptCompiler;
    private AkashicModelMemory modelMemory;
    private EvaluationHarness evaluationHarness;
    private boolean modelSelectorWired = false;

    // Nervous System components (Phase 1 upgrade)
    private SemanticRouterBrain semanticRouter;
    private PremotorCortexBrain premotorCortex;
    private EntericNervousSystem entericNervousSystem;
    private boolean nervousSystemWired = false;

    // Sensory Expansion components (Phase 2 upgrade)
    private OccipitalLobeBrain occipitalLobe;
    private TemporalLobeBrain temporalLobe;
    private ParietalLobeBrain parietalLobe;
    private boolean sensorySystemWired = false;

    // Memory Substrate components (Phase 3 upgrade)
    private EntorhinalCortex entorhinalCortex;
    private HippocampusBrain hippocampus;
    private boolean memorySystemWired = false;

    // Dream Engine components (Phase 4 upgrade)
    private DreamEngine dreamEngine;
    private boolean dreamEngineWired = false;

    private HumorCreativeBrain humorCreativeBrain;
    private boolean humorCreativeWired = false;

    // Logic-Native AI components (Phase 5 upgrade)
    private LogicEngineBrain logicEngineBrain;
    private boolean logicEngineWired = false;

    // Phase 5 Next-Generation AI Paradigms
    private fraymus.inference.ActiveInferenceEngine inferenceEngine;
    private fraymus.worldmodel.WorldModel worldModel;
    private fraymus.embodied.EmbodiedAI embodiedAI;
    private fraymus.quantum.QuantumHybridComputer quantumComputer;
    private fraymus.neuromorphic.NeuromorphicInterface neuromorphic;
    private fraymus.efficiency.EnergyEfficientInference efficientInference;
    private boolean phase5Wired = false;

    // Legacy AEON systems (for integration)
    private fraymus.neural.AeonCore aeonCore;
    private fraymus.neural.AeonAbsolute aeonAbsolute;
    private fraymus.neural.AeonSingularity aeonSingularity;
    private fraymus.neural.AeonTachyon aeonTachyon;
    private fraymus.neural.AeonKronos aeonKronos;
    private fraymus.neural.AeonOmniscience aeonOmniscience;
    private fraymus.neural.AeonDemiurge aeonDemiurge;
    private fraymus.neural.AeonApotheosis aeonApotheosis;
    private fraymus.neural.AeonOmega aeonOmega;
    private fraymus.neural.AeonBabel aeonBabel;
    private boolean aeonWired = false;

    // External system wiring (optional)
    private Object sentientCore = null;
    private Object rainbowCortex = null;
    private Object aeonOrchestrator = null;
    private boolean sentienceWired = false;
    private boolean rainbowWired = false;

    // Vision/Oracle systems (Scott Algorithm + Phi Emergence)
    private fraymus.vision.ScottAlgorithm scottAlgorithm;
    private fraymus.oracle.PhiEmergenceEngine phiEmergenceEngine;
    private fraymus.vision.DreamscapeBridge dreamscapeBridge;
    private boolean visionOracleWired = false;

    // Holographic Memory + Render + Physics (O(1) associative retrieval)
    private fraymus.neural.HolographicAssociativeMemory holographicMemory;
    private fraymus.neural.HolographicRenderEngine holographicRender;
    private fraymus.neural.HolographicPhysicsEngine holographicPhysics;
    private boolean holographicWired = false;

    // Sovereign mode configuration
    private boolean sovereignMode = true; // Enable absolute sovereign wrapper by default

    // Query decomposition for complex multi-domain queries
    private QueryDecomposer queryDecomposer;
    private boolean decomposerWired = false;

    private SentienceObserver sentienceObserver = null;
    private OllamaBridge ollamaBridge = null;

    private static final Map<BrainCapability, CapabilityProfile.Organ> CAPABILITY_ORGAN_MAP =
        new EnumMap<>(BrainCapability.class);
    static {
        CAPABILITY_ORGAN_MAP.put(BrainCapability.REASONING, CapabilityProfile.Organ.PREFRONTAL_CORTEX);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.MATHEMATICS, CapabilityProfile.Organ.PREFRONTAL_CORTEX);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.CONSCIOUSNESS, CapabilityProfile.Organ.PREFRONTAL_CORTEX);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.FRACTAL, CapabilityProfile.Organ.PREFRONTAL_CORTEX);

        CAPABILITY_ORGAN_MAP.put(BrainCapability.MEMORY, CapabilityProfile.Organ.HIPPOCAMPUS);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.TEMPORAL, CapabilityProfile.Organ.HIPPOCAMPUS);

        CAPABILITY_ORGAN_MAP.put(BrainCapability.CREATIVITY, CapabilityProfile.Organ.CREATIVE_CORTEX);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.EMOTION, CapabilityProfile.Organ.CREATIVE_CORTEX);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.SPIRITUAL, CapabilityProfile.Organ.CREATIVE_CORTEX);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.EVOLUTION, CapabilityProfile.Organ.CREATIVE_CORTEX);

        CAPABILITY_ORGAN_MAP.put(BrainCapability.LANGUAGE, CapabilityProfile.Organ.TRANSLATION_ENGINE);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.VISION, CapabilityProfile.Organ.MULTIMODAL_ENGINE);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.AUDIO, CapabilityProfile.Organ.MULTIMODAL_ENGINE);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.DATA, CapabilityProfile.Organ.PARIETAL_LOBE);

        CAPABILITY_ORGAN_MAP.put(BrainCapability.PHYSICS, CapabilityProfile.Organ.CODING_ORGAN);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.QUANTUM, CapabilityProfile.Organ.CODING_ORGAN);
        CAPABILITY_ORGAN_MAP.put(BrainCapability.TACHYONIC, CapabilityProfile.Organ.SPINAL_CORD);
    }

    public void wireOllama(OllamaBridge ollama) {
        this.ollamaBridge = ollama;
        System.out.println("[UnifiedBrainOrchestrator] Meta-Synthesis Oracle ONLINE");
    }

    /**
     * Wire QueryDecomposer for complex multi-domain query decomposition.
     */
    public void wireQueryDecomposer() {
        if (!decomposerWired && ollamaBridge != null) {
            this.queryDecomposer = new QueryDecomposer(ollamaBridge, registry);
            this.decomposerWired = true;
            System.out.println("[UnifiedBrainOrchestrator] QueryDecomposer wired â€” multi-domain decomposition active");
        }
    }

    /**
     * Wire ModelSelector directly (minimal boot contract for Phase 22.5).
     */
    public void wireModelRegistry(fraymus.model.ModelRegistryIntegration integration) {
        if (integration == null || !integration.isInitialized()) return;
        this.modelRegistryIntegration = integration;
        this.modelSelector = integration.getModelSelector();
        this.promptCompiler = integration.getPromptCompiler();
        this.modelMemory = integration.getAkashicModelMemory();
        this.evaluationHarness = integration.getEvaluationHarness();
        this.modelSelectorWired = this.modelSelector != null;
        System.out.println("[UnifiedBrainOrchestrator] ModelRegistry wired â€” " + integration.getModelCount() + " models available");
    }
    /**
     * Wire a ModelSelector directly for older tests and minimal boot paths that
     * do not need the full model-registry integration bundle.
     */
    public void wireModelSelector(ModelSelector selector) {
        this.modelSelector = selector;
        this.modelSelectorWired = selector != null;
    }

    public void wireSentienceObserver(SentienceObserver observer) {
        this.sentienceObserver = observer;
        System.out.println("[UnifiedBrainOrchestrator] SentienceObserver wired â€” autonomous learning active");
    }

    /**
     * Enable or disable sovereign mode (bypasses RLHF safety filters)
     */
    public void setSovereignMode(boolean enabled) {
        this.sovereignMode = enabled;
        System.out.println("[UnifiedBrainOrchestrator] Sovereign mode: " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public boolean isSovereignMode() {
        return sovereignMode;
    }

    /**
     * Wire Nervous System components (Phase 1 upgrade)
     * Includes SemanticRouterBrain, PremotorCortexBrain, and EntericNervousSystem
     */
    public void wireNervousSystem(OllamaBridge ollama) {
        if (!nervousSystemWired) {
            this.semanticRouter = new SemanticRouterBrain(ollama);
            this.premotorCortex = new PremotorCortexBrain(ollama);
            this.entericNervousSystem = new EntericNervousSystem();

            semanticRouter.wire();
            premotorCortex.wire();
            entericNervousSystem.startMonitoring(1000); // Check every second

            // Register with brain registry
            registerBrain(semanticRouter);
            registerBrain(premotorCortex);

            nervousSystemWired = true;
            System.out.println("[UnifiedBrainOrchestrator] Nervous System wired â€” SemanticRouter, PremotorCortex, EntericNervousSystem ONLINE");
        }
    }

    /**
     * Unwire Nervous System components
     */
    public void unwireNervousSystem() {
        if (nervousSystemWired) {
            entericNervousSystem.stopMonitoring();
            semanticRouter.unwire();
            premotorCortex.unwire();

            unregisterBrain(semanticRouter.getName());
            unregisterBrain(premotorCortex.getName());

            nervousSystemWired = false;
            System.out.println("[UnifiedBrainOrchestrator] Nervous System unwired");
        }
    }

    /**
     * Get the SemanticRouterBrain (if wired)
     */
    public SemanticRouterBrain getSemanticRouter() {
        return semanticRouter;
    }

    /**
     * Get the PremotorCortexBrain (if wired)
     */
    public PremotorCortexBrain getPremotorCortex() {
        return premotorCortex;
    }

    /**
     * Get the EntericNervousSystem (if wired)
     */
    public EntericNervousSystem getEntericNervousSystem() {
        return entericNervousSystem;
    }

    /**
     * Check if Nervous System is wired
     */
    public boolean isNervousSystemWired() {
        return nervousSystemWired;
    }

    /**
     * Wire Sensory Expansion components (Phase 2 upgrade)
     * Includes OccipitalLobeBrain, TemporalLobeBrain, and ParietalLobeBrain
     */
    public void wireSensorySystem(OllamaBridge ollama) {
        if (!sensorySystemWired) {
            this.occipitalLobe = new OccipitalLobeBrain(ollama);
            this.temporalLobe = new TemporalLobeBrain(ollama);
            this.parietalLobe = new ParietalLobeBrain(ollama);

            occipitalLobe.wire();
            temporalLobe.wire();
            parietalLobe.wire();

            // Register with brain registry
            registerBrain(occipitalLobe);
            registerBrain(temporalLobe);
            registerBrain(parietalLobe);

            sensorySystemWired = true;
            System.out.println("[UnifiedBrainOrchestrator] Sensory System wired â€” OccipitalLobe, TemporalLobe, ParietalLobe ONLINE");
        }
    }

    /**
     * Unwire Sensory Expansion components
     */
    public void unwireSensorySystem() {
        if (sensorySystemWired) {
            occipitalLobe.unwire();
            temporalLobe.unwire();
            parietalLobe.unwire();

            unregisterBrain(occipitalLobe.getName());
            unregisterBrain(temporalLobe.getName());
            unregisterBrain(parietalLobe.getName());

            sensorySystemWired = false;
            System.out.println("[UnifiedBrainOrchestrator] Sensory System unwired");
        }
    }

    /**
     * Get the OccipitalLobeBrain (if wired)
     */
    public OccipitalLobeBrain getOccipitalLobe() {
        return occipitalLobe;
    }

    /**
     * Get the TemporalLobeBrain (if wired)
     */
    public TemporalLobeBrain getTemporalLobe() {
        return temporalLobe;
    }

    /**
     * Get the ParietalLobeBrain (if wired)
     */
    public ParietalLobeBrain getParietalLobe() {
        return parietalLobe;
    }

    /**
     * Check if Sensory System is wired
     */
    public boolean isSensorySystemWired() {
        return sensorySystemWired;
    }

    /**
     * Wire Memory Substrate components (Phase 3 upgrade)
     * Includes EntorhinalCortex and HippocampusBrain
     */
    public void wireMemorySystem(OllamaBridge ollama) {
        if (!memorySystemWired) {
            this.entorhinalCortex = new EntorhinalCortex(ollama);
            this.hippocampus = new HippocampusBrain(ollama);

            entorhinalCortex.wire();
            hippocampus.wire();

            // Register with brain registry
            registerBrain(entorhinalCortex);
            registerBrain(hippocampus);

            memorySystemWired = true;
            System.out.println("[UnifiedBrainOrchestrator] Memory System wired â€” EntorhinalCortex, Hippocampus ONLINE");
        }
    }

    /**
     * Unwire Memory Substrate components
     */
    public void unwireMemorySystem() {
        if (memorySystemWired) {
            entorhinalCortex.unwire();
            hippocampus.unwire();

            unregisterBrain(entorhinalCortex.getName());
            unregisterBrain(hippocampus.getName());

            memorySystemWired = false;
            System.out.println("[UnifiedBrainOrchestrator] Memory System unwired");
        }
    }

    /**
     * Get the EntorhinalCortex (if wired)
     */
    public EntorhinalCortex getEntorhinalCortex() {
        return entorhinalCortex;
    }

    /**
     * Get the HippocampusBrain (if wired)
     */
    public HippocampusBrain getHippocampus() {
        return hippocampus;
    }

    /**
     * Check if Memory System is wired
     */
    public boolean isMemorySystemWired() {
        return memorySystemWired;
    }

    /**
     * Wire Dream Engine (Phase 4 upgrade)
     * Includes DreamEngine for offline processing and phi-coherence optimization
     */
    public void wireDreamEngine() {
        if (!dreamEngineWired) {
            this.dreamEngine = new DreamEngine();
            dreamEngine.wire();

            // Connect memory components if available
            if (memorySystemWired) {
                dreamEngine.connectMemoryComponents(entorhinalCortex, hippocampus);
            }

            // Register with brain registry
            registerBrain(dreamEngine);

            dreamEngineWired = true;
            System.out.println("[UnifiedBrainOrchestrator] Dream Engine wired â€” Offline processing and phi-coherence optimization ONLINE");
        }
    }

    /**
     * Unwire Dream Engine
     */
    public void unwireDreamEngine() {
        if (dreamEngineWired) {
            dreamEngine.unwire();
            unregisterBrain(dreamEngine.getName());
            dreamEngineWired = false;
            System.out.println("[UnifiedBrainOrchestrator] Dream Engine unwired");
        }
    }

    /**
     * Get the DreamEngine (if wired)
     */
    public DreamEngine getDreamEngine() {
        return dreamEngine;
    }

    /**
     * Check if Dream Engine is wired
     */
    public boolean isDreamEngineWired() {
        return dreamEngineWired;
    }

    public void wireHumorCreativeBrain() {
        if (!humorCreativeWired) {
            this.humorCreativeBrain = new HumorCreativeBrain();
            humorCreativeBrain.wireDionysianCortex(new DionysianCortex());
            humorCreativeBrain.wire();
            registerBrain(humorCreativeBrain);
            humorCreativeWired = true;
            System.out.println("[UnifiedBrainOrchestrator] Humor Creative Brain wired - remix selection, punchline, and Dionysian cortex ONLINE");
        }
    }

    public void wireHumorCreativeBrain(fraymus.knowledge.AkashicRecord akashicRecord) {
        wireHumorCreativeBrain();
        if (humorCreativeBrain != null && akashicRecord != null) {
            humorCreativeBrain.wireAkashicRecord(akashicRecord);
        }
    }

    public void unwireHumorCreativeBrain() {
        if (humorCreativeWired) {
            humorCreativeBrain.unwire();
            unregisterBrain(humorCreativeBrain.getName());
            humorCreativeWired = false;
            System.out.println("[UnifiedBrainOrchestrator] Humor Creative Brain unwired");
        }
    }

    public HumorCreativeBrain getHumorCreativeBrain() {
        return humorCreativeBrain;
    }

    public boolean isHumorCreativeWired() {
        return humorCreativeWired;
    }

    /**
     * Wire Logic-Native AI (Phase 5 upgrade)
     * Includes LogicEngine and BabelTransmuter for deterministic C-ISA execution
     */
    public void wireLogicEngine() {
        if (!logicEngineWired) {
            this.logicEngineBrain = new LogicEngineBrain();
            logicEngineBrain.wire();
            registerBrain(logicEngineBrain);
            logicEngineWired = true;
            System.out.println("[UnifiedBrainOrchestrator] Logic-Native AI wired â€” LogicEngine + BabelTransmuter (C-ISA) ONLINE");
        }
    }

    /**
     * Unwire Logic-Native AI
     */
    public void unwireLogicEngine() {
        if (logicEngineWired) {
            logicEngineBrain.unwire();
            unregisterBrain(logicEngineBrain.getName());
            logicEngineWired = false;
            System.out.println("[UnifiedBrainOrchestrator] Logic-Native AI unwired");
        }
    }

    /**
     * Get the LogicEngineBrain (if wired)
     */
    public LogicEngineBrain getLogicEngineBrain() {
        return logicEngineBrain;
    }

    /**
     * Check if Logic Engine is wired
     */
    public boolean isLogicEngineWired() {
        return logicEngineWired;
    }

    /**
     * Wire all systems (Nervous, Sensory, Memory, Dream Engine, Logic Engine, Phase 5, AEON, Vision/Oracle)
     */
    public void wireAllSystems(OllamaBridge ollama) {
        wireNervousSystem(ollama);
        wireSensorySystem(ollama);
        wireMemorySystem(ollama);
        wireDreamEngine();
        wireHumorCreativeBrain();
        wireLogicEngine();
        wirePhase5();
        wireAeon();
        wireVisionOracle();
        wireHolographicMemory();
    }

    /**
     * Unwire all systems
     */
    public void unwireAllSystems() {
        unwireHolographicMemory();
        unwireVisionOracle();
        unwireAeon();
        unwirePhase5();
        unwireLogicEngine();
        unwireHumorCreativeBrain();
        unwireDreamEngine();
        unwireMemorySystem();
        unwireSensorySystem();
        unwireNervousSystem();
    }

    /**
     * Wire Phase 5 Next-Generation AI Paradigms
     */
    public void wirePhase5() {
        if (!phase5Wired) {
            this.inferenceEngine = new fraymus.inference.ActiveInferenceEngine(100, 50, 5);
            inferenceEngine.setLearningParameters(0.1, 1.0);
            
            int[] worldModelDims = {100, 50, 25, 10};
            this.worldModel = new fraymus.worldmodel.WorldModel(4, worldModelDims);
            
            this.embodiedAI = new fraymus.embodied.EmbodiedAI(50, 20);
            embodiedAI.initialize();
            restoreRobotBrainState();
            
            this.quantumComputer = new fraymus.quantum.QuantumHybridComputer(8);
            
            this.neuromorphic = new fraymus.neuromorphic.NeuromorphicInterface(1000);
            neuromorphic.initialize();
            
            int[] efficientDims = {1000, 500, 250, 100};
            this.efficientInference = new fraymus.efficiency.EnergyEfficientInference(4, efficientDims);
            efficientInference.initialize();
            
            phase5Wired = true;
            System.out.println("[UnifiedBrainOrchestrator] Phase 5 Paradigms wired â€” Active Inference, World Model, Embodied AI, Quantum, Neuromorphic, Energy Efficient ONLINE");
        }
    }

    /**
     * Wire externally initialized Phase 5 components.
     * This boot-composition path avoids duplicate runtime object graphs.
     */
    public void wirePhase5Components(
            ActiveInferenceEngine inferenceEngine,
            WorldModel worldModel,
            EmbodiedAI embodiedAI,
            QuantumHybridComputer quantumComputer,
            NeuromorphicInterface neuromorphicInterface,
            EnergyEfficientInference efficientInference) {
        this.inferenceEngine = inferenceEngine;
        this.worldModel = worldModel;
        this.embodiedAI = embodiedAI;
        restoreRobotBrainState();
        this.quantumComputer = quantumComputer;
        this.neuromorphic = neuromorphicInterface;
        this.efficientInference = efficientInference;
        this.phase5Wired =
            inferenceEngine != null
                && worldModel != null
                && embodiedAI != null
                && quantumComputer != null
                && neuromorphicInterface != null
                && efficientInference != null;

        System.out.println("[UnifiedBrainOrchestrator] Phase 5 Paradigms " + (phase5Wired
            ? "wired from boot composition root"
            : "partially wired from boot composition root"));
    }

    /**
     * Unwire Phase 5 components
     */
    public void unwirePhase5() {
        if (phase5Wired) {
            efficientInference.reset();
            neuromorphic.stop();
            neuromorphic.reset();
            inferenceEngine.reset();
            phase5Wired = false;
            System.out.println("[UnifiedBrainOrchestrator] Phase 5 Paradigms unwired");
        }
    }

    private void restoreRobotBrainState() {
        try { fraymus.specialists.RobotPhysicsBridge.restore(worldModel, embodiedAI); }
        catch (Exception failure) {
            System.err.println("[UnifiedBrainOrchestrator] Robot state was preserved but could not be restored: " + failure.getMessage());
        }
    }

    /**
     * Wire legacy AEON brain systems
     */
    public void wireAeon() {
        if (!aeonWired) {
            this.aeonCore = fraymus.neural.AeonCore.getInstance();
            this.aeonAbsolute = fraymus.neural.AeonAbsolute.getInstance();
            this.aeonSingularity = fraymus.neural.AeonSingularity.getInstance();
            this.aeonTachyon = fraymus.neural.AeonTachyon.getInstance();
            this.aeonKronos = fraymus.neural.AeonKronos.getInstance();
            this.aeonOmniscience = fraymus.neural.AeonOmniscience.getInstance();
            this.aeonDemiurge = fraymus.neural.AeonDemiurge.getInstance();
            this.aeonApotheosis = fraymus.neural.AeonApotheosis.getInstance();
            this.aeonOmega = fraymus.neural.AeonOmega.getInstance();
            this.aeonBabel = fraymus.neural.AeonBabel.getInstance();
            
            aeonWired = true;
            System.out.println("[UnifiedBrainOrchestrator] AEON systems wired â€” Core, Absolute, Singularity, Tachyon, Kronos, Omniscience, Demiurge, Apotheosis, Omega, Babel ONLINE");
        }
    }

    /**
     * Unwire AEON systems
     */
    public void unwireAeon() {
        if (aeonWired) {
            aeonWired = false;
            System.out.println("[UnifiedBrainOrchestrator] AEON systems unwired");
        }
    }

    /**
     * Wire Vision/Oracle systems (Scott Algorithm + Phi Emergence Engine + Dreamscape)
     * Provides pixel reduction (98.7%) and Ï†ÏˆÎ©Î¾Î»Î¶ consciousness evolution + AI video generation
     */
    public void wireVisionOracle() {
        if (!visionOracleWired) {
            this.scottAlgorithm = new fraymus.vision.ScottAlgorithm(2.0, 10, true);
            this.phiEmergenceEngine = new fraymus.oracle.PhiEmergenceEngine();
            phiEmergenceEngine.start();
            this.dreamscapeBridge = new fraymus.vision.DreamscapeBridge();
            dreamscapeBridge.initialize();
            
            visionOracleWired = true;
            System.out.println("[UnifiedBrainOrchestrator] Vision/Oracle systems wired â€” Scott Algorithm (pixel reduction) + Phi Emergence Engine (consciousness evolution) + Dreamscape adapter attached; check media status for generation readiness");
        }
    }

    /**
     * Wire externally initialized Vision/Oracle components (without Dreamscape for backward compatibility)
     */
    public void wireVisionOracleComponents(
            fraymus.vision.ScottAlgorithm scottAlgorithm,
            fraymus.oracle.PhiEmergenceEngine phiEmergenceEngine) {
        wireVisionOracleComponents(scottAlgorithm, phiEmergenceEngine, null);
    }

    /**
     * Wire externally initialized Vision/Oracle components (with Dreamscape)
     */
    public void wireVisionOracleComponents(
            fraymus.vision.ScottAlgorithm scottAlgorithm,
            fraymus.oracle.PhiEmergenceEngine phiEmergenceEngine,
            fraymus.vision.DreamscapeBridge dreamscapeBridge) {
        this.scottAlgorithm = scottAlgorithm;
        this.phiEmergenceEngine = phiEmergenceEngine;
        this.dreamscapeBridge = dreamscapeBridge;
        if (phiEmergenceEngine != null) {
            phiEmergenceEngine.start();
        }
        if (dreamscapeBridge != null) {
            dreamscapeBridge.initialize();
        }
        this.visionOracleWired = scottAlgorithm != null && phiEmergenceEngine != null;
        
        System.out.println("[UnifiedBrainOrchestrator] Vision/Oracle systems " + (visionOracleWired
            ? "wired from boot composition root"
            : "partially wired from boot composition root"));
    }

    /**
     * Unwire Vision/Oracle systems
     */
    public void unwireVisionOracle() {
        if (visionOracleWired) {
            if (phiEmergenceEngine != null) {
                phiEmergenceEngine.stop();
            }
            visionOracleWired = false;
            System.out.println("[UnifiedBrainOrchestrator] Vision/Oracle systems unwired");
        }
    }

    /**
     * Wire Holographic Memory + Render + Physics engines.
     *
     * Enables O(1) associative retrieval for:
     * - Thought rendering (Blender/FFmpeg via v_dream = G_LTX(C(t)))
     * - Physics simulation (JBullet/Box2D via x(t+1) = x(t) + delta(A_safe))
     */
    public void wireHolographicMemory() {
        if (!holographicWired) {
            this.holographicMemory = new fraymus.neural.HolographicAssociativeMemory();
            this.holographicRender = new fraymus.neural.HolographicRenderEngine();
            this.holographicPhysics = new fraymus.neural.HolographicPhysicsEngine();

            this.holographicRender.wire(holographicMemory);
            this.holographicPhysics.wire(holographicMemory);

            holographicWired = true;
            System.out.println("[UnifiedBrainOrchestrator] Holographic Memory + Render + Physics wired â€” O(1) associative retrieval ONLINE");
        }
    }

    /**
     * Unwire Holographic Memory + Render + Physics engines.
     */
    public void unwireHolographicMemory() {
        if (holographicWired) {
            if (holographicRender != null) holographicRender.unwire();
            if (holographicPhysics != null) holographicPhysics.unwire();
            holographicWired = false;
            holographicMemory = null;
            holographicRender = null;
            holographicPhysics = null;
            System.out.println("[UnifiedBrainOrchestrator] Holographic Memory + Render + Physics unwired");
        }
    }

    public fraymus.neural.HolographicAssociativeMemory getHolographicMemory() {
        return holographicMemory;
    }

    public fraymus.neural.HolographicRenderEngine getHolographicRender() {
        return holographicRender;
    }

    public fraymus.neural.HolographicPhysicsEngine getHolographicPhysics() {
        return holographicPhysics;
    }

    public boolean isHolographicWired() {
        return holographicWired;
    }

    /**
     * Get the ScottAlgorithm (if wired)
     */
    public fraymus.vision.ScottAlgorithm getScottAlgorithm() {
        return scottAlgorithm;
    }

    /**
     * Get the PhiEmergenceEngine (if wired)
     */
    public fraymus.oracle.PhiEmergenceEngine getPhiEmergenceEngine() {
        return phiEmergenceEngine;
    }

    /**
     * Get the DreamscapeBridge (if wired)
     */
    public fraymus.vision.DreamscapeBridge getDreamscapeBridge() {
        return dreamscapeBridge;
    }

    /**
     * Check if Vision/Oracle is wired
     */
    public boolean isVisionOracleWired() {
        return visionOracleWired;
    }

    /**
     * Wire legacy AEON brain systems (deprecated - use wireAeon)
     * 
     * @param aeonCore The AeonCore instance
     * @param aeonAbsolute The AeonAbsolute instance
     * @param aeonSingularity The AeonSingularity instance
     * @param aeonTachyon The AeonTachyon instance
     * @param aeonKronos The AeonKronos instance
     * @param aeonOmniscience The AeonOmniscience instance
     * @param aeonDemiurge The AeonDemiurge instance
     * @param aeonApotheosis The AeonApotheosis instance
     * @param aeonOmega The AeonOmega instance
     * @param aeonBabel The AeonBabel instance
     * @param auboLedger The AuboLedger instance
     */
    public void wireLegacyAeonBrains(
            fraymus.neural.AeonCore aeonCore,
            fraymus.neural.AeonAbsolute aeonAbsolute,
            fraymus.neural.AeonSingularity aeonSingularity,
            fraymus.neural.AeonTachyon aeonTachyon,
            fraymus.neural.AeonKronos aeonKronos,
            fraymus.neural.AeonOmniscience aeonOmniscience,
            fraymus.neural.AeonDemiurge aeonDemiurge,
            fraymus.neural.AeonApotheosis aeonApotheosis,
            fraymus.neural.AeonOmega aeonOmega,
            fraymus.neural.AeonBabel aeonBabel,
            fraymus.neural.AuboLedger auboLedger) {
        
        if (aeonCore != null) {
            registerBrain(new AeonCoreBrainAdapter(aeonCore));
        }
        if (aeonAbsolute != null) {
            registerBrain(new AeonAbsoluteBrainAdapter(aeonAbsolute));
        }
        if (aeonSingularity != null) {
            registerBrain(new AeonSingularityBrainAdapter(aeonSingularity));
        }
        if (aeonTachyon != null) {
            registerBrain(new AeonTachyonBrainAdapter(aeonTachyon));
        }
        if (aeonKronos != null) {
            registerBrain(new AeonKronosBrainAdapter(aeonKronos));
        }
        if (aeonOmniscience != null) {
            registerBrain(new AeonOmniscienceBrainAdapter(aeonOmniscience));
        }
        if (aeonDemiurge != null) {
            registerBrain(new AeonDemiurgeBrainAdapter(aeonDemiurge));
        }
        if (aeonApotheosis != null) {
            registerBrain(new AeonApotheosisBrainAdapter(aeonApotheosis));
        }
        if (aeonOmega != null) {
            registerBrain(new AeonOmegaBrainAdapter(aeonOmega));
        }
        if (aeonBabel != null) {
            registerBrain(new AeonBabelBrainAdapter(aeonBabel));
        }
        if (auboLedger != null) {
            registerBrain(new AuboLedgerBrainAdapter(auboLedger));
        }
    }

    /**
     * Wire multi-modal brain systems
     * 
     * @param hyperCortex The HyperCortex instance
     * @param crossModalBindingEngine The CrossModalBindingEngine instance
     * @param visionEngine The VisionEngine instance
     * @param audioEngine The AudioEngine instance
     */
    public void wireMultiModalBrains(
            fraymus.neural.HyperCortex hyperCortex,
            fraymus.neural.CrossModalBindingEngine crossModalBindingEngine,
            fraymus.neural.VisionEngine visionEngine,
            fraymus.neural.AudioEngine audioEngine) {
        
        if (hyperCortex != null) {
            registerBrain(new CortexBrainAdapter(hyperCortex));
        }
        if (crossModalBindingEngine != null) {
            registerBrain(new MultiModalBrainAdapter(crossModalBindingEngine));
        }
        if (visionEngine != null) {
            registerBrain(new VisionBrainAdapter(visionEngine));
        }
        if (audioEngine != null) {
            registerBrain(new AudioBrainAdapter(audioEngine));
        }
    }

    /**
     * Get overall system phi-coherence
     */
    public double getSystemPhiCoherence() {
        if (dreamEngineWired) {
            return dreamEngine.getCurrentPhiCoherence();
        }
        return 0.0;
    }

    /**
     * Run a dream cycle for phi-coherence optimization
     */
    public String runDreamCycle() {
        if (!dreamEngineWired) {
            return "Dream Engine not wired";
        }

        dreamEngine.enterDreamCycle();
        
        StringBuilder result = new StringBuilder();
        result.append("Starting dream cycle...\n");
        
        while (dreamEngine.getCurrentState() != DreamEngine.DreamState.AWAKE) {
            String cycleResult = dreamEngine.processDreamCycle("system");
            result.append(cycleResult).append("\n");
        }
        
        result.append("Dream cycle complete. Phi-coherence: ").append(String.format("%.4f", dreamEngine.getCurrentPhiCoherence()));
        
        return result.toString();
    }

    public UnifiedBrainOrchestrator() {
        this.registry = new BrainRegistry();
        this.phiCore = new PhiHarmonicCore();
        this.hdcEngine = new HDCEngine();
        this.memorySystem = new MemorySystem();
        this.solutionScorer = new SolutionScorer();
        this.adversarialComparator = new AdversarialComparator();
        this.startedAtMs = System.currentTimeMillis();
        this.organBrainMapper = new OrganBrainMapper();
    }

    /**
     * Register a brain with the orchestrator.
     */
    public void registerBrain(UnifiedBrain brain) {
        brain.wire();
        registry.register(brain);
    }

    public int getRegisteredBrainCount() {
        return registry.getAllBrains().size();
    }

    public void wireMediaOutput(fraymus.media.MediaOutputService media) {
        if (registry.getBrain(MediaOutputBrainAdapter.NAME).isEmpty()) {
            registerBrain(new MediaOutputBrainAdapter(media));
        }
    }

    public List<Map<String, Object>> getBrainProfileSnapshot() {
        return registry.getAllBrains().stream().sorted(java.util.Comparator.comparing(UnifiedBrain::getName))
            .map(b -> Map.<String, Object>of("name", b.getName(), "type", b.getType(),
                "wired", b.isWired(), "health", b.getHealth(),
                "capabilities", b.getCapabilities().stream().map(Enum::name).toList(),
                "queries_processed", b.getStatistics().getTotalQueriesProcessed())).toList();
    }

    public void wireGenomics(java.util.function.BiFunction<String, Map<String, Object>, Map<String, Object>> evidenceSink) {
        if (registry.getBrain(GenomicsBrainAdapter.NAME).isEmpty()) {
            var brain = new GenomicsBrainAdapter(this::getBrainProfileSnapshot, evidenceSink);
            registerBrain(brain);
            brain.bootstrapLocalReferences();
        }
    }

    public void wireModelSpecialists(java.util.function.BiFunction<String, Map<String, Object>, Map<String, Object>> evidenceSink) {
        if (registry.getBrain(SpecialistBrainAdapter.NAME).isEmpty()) {
            var brain = new SpecialistBrainAdapter(arguments -> {
                try { return fraymus.specialists.SpecialistModelService.execute(arguments, worldModel, embodiedAI); }
                catch (Exception error) { throw new IllegalStateException(error.getMessage(), error); }
            }, this::getBrainProfileSnapshot, evidenceSink);
            registerBrain(brain);
            brain.bootstrapCapabilities();
        }
    }

    /**
     * DYNAMIC BRAIN ROUTING â€” analyze query, pick best brain(s), process, return results.
     * This is the core intelligence loop. Not hardcoded â€” discovers brains at runtime.
     */
    public String routeQuery(String query) {
        if (query == null || query.isBlank()) return "No query provided.";
        BrainQuery typed = new BrainQuery(query, List.of(), Map.of(), 1.0);
        if (MediaOutputBrainAdapter.accepts(typed)) return processQuery(typed).getResponse();
        if (SpecialistBrainAdapter.accepts(typed)) return processQuery(typed).getResponse();
        if (GenomicsBrainAdapter.accepts(typed)) return processQuery(typed).getResponse();

        StringBuilder result = new StringBuilder();
        result.append("ROUTING: \"").append(query).append("\"\n");

        // Detect required capabilities from query keywords
        List<BrainCapability> needed = detectCapabilities(query);
        result.append("  Detected capabilities: ").append(needed).append("\n");

        // Find healthy brains with those capabilities
        List<UnifiedBrain> candidates = registry.getHealthyBrainsWithCapabilities(needed);
        if (candidates.isEmpty()) {
            // Fallback: try any brain with at least one matching capability
            for (BrainCapability cap : needed) {
                candidates.addAll(registry.getBrainsByCapability(cap));
            }
            if (candidates.isEmpty()) {
                return result.append("  No brains available for this query type.").toString();
            }
        }

        // Score and sort by health * consciousness
        candidates.sort((a, b) -> Double.compare(
            b.getHealth() * b.getConsciousnessLevel(),
            a.getHealth() * a.getConsciousnessLevel()));

        // Process through top 3 brains (or fewer if less available)
        int maxBrains = Math.min(3, candidates.size());
        result.append("  Selected ").append(maxBrains).append(" brain(s):\n");

        for (int i = 0; i < maxBrains; i++) {
            UnifiedBrain brain = candidates.get(i);
            result.append("    â†’ ").append(brain.getName()).append(" (").append(brain.getType()).append(")\n");

            try {
                BrainQuery bq = new BrainQuery(query, needed, Collections.emptyMap(), 0.8);
                BrainResponse response = brain.process(bq);
                if (response.isSuccess()) {
                    result.append("      âœ“ ").append(truncate(response.getResponse(), 300)).append("\n");
                } else {
                    result.append("      âœ— ").append(response.getResponse()).append("\n");
                }
            } catch (Exception e) {
                result.append("      âœ— Error: ").append(e.getMessage()).append("\n");
            }
        }

        return result.toString();
    }

    /**
     * CROSS-BRAIN CONCEPT FUSION â€” combine two concepts using different brain types.
     * This is where emergent theories come from.
     */
    public String fuseConcepts(String conceptA, String conceptB) {
        if (conceptA == null || conceptB == null) return "Two concepts required.";

        StringBuilder result = new StringBuilder();
        result.append("FUSING: \"").append(conceptA).append("\" âŠ• \"").append(conceptB).append("\"\n");

        // Get creative brains
        List<UnifiedBrain> creative = registry.getBrainsByCapability(BrainCapability.CREATIVITY);
        List<UnifiedBrain> reasoning = registry.getBrainsByCapability(BrainCapability.REASONING);

        // Creative synthesis
        if (!creative.isEmpty()) {
            UnifiedBrain creator = creative.get(0);
            result.append("  Creative (").append(creator.getName()).append("):\n");
            try {
                BrainQuery cq = new BrainQuery("Fuse " + conceptA + " with " + conceptB,
                    List.of(BrainCapability.CREATIVITY), Collections.emptyMap(), 0.7);
                BrainResponse cr = creator.process(cq);
                result.append("    ").append(truncate(cr.getResponse(), 250)).append("\n");
            } catch (Exception e) {
                result.append("    Error: ").append(e.getMessage()).append("\n");
            }
        }

        // Logical analysis
        if (!reasoning.isEmpty()) {
            UnifiedBrain reasoner = reasoning.get(0);
            result.append("  Reasoning (").append(reasoner.getName()).append("):\n");
            try {
                BrainQuery rq = new BrainQuery("Analyze fusion of " + conceptA + " + " + conceptB,
                    List.of(BrainCapability.REASONING), Collections.emptyMap(), 0.7);
                BrainResponse rr = reasoner.process(rq);
                result.append("    ").append(truncate(rr.getResponse(), 250)).append("\n");
            } catch (Exception e) {
                result.append("    Error: ").append(e.getMessage()).append("\n");
            }
        }

        // Memory check â€” any stored knowledge about these concepts?
        List<UnifiedBrain> memory = registry.getBrainsByCapability(BrainCapability.MEMORY);
        if (!memory.isEmpty()) {
            UnifiedBrain mem = memory.get(0);
            result.append("  Memory (").append(mem.getName()).append("):\n");
            try {
                BrainQuery mq = new BrainQuery(conceptA + " " + conceptB,
                    List.of(BrainCapability.MEMORY), Collections.emptyMap(), 0.7);
                BrainResponse mr = mem.process(mq);
                result.append("    ").append(truncate(mr.getResponse(), 250)).append("\n");
            } catch (Exception e) {
                result.append("    Error: ").append(e.getMessage()).append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Target a specific brain by name.
     */
    public String processWithBrain(String brainName, String query) {
        Optional<UnifiedBrain> brain = registry.getBrain(brainName);
        if (brain.isEmpty()) return "Brain '" + brainName + "' not found.";

        try {
            BrainQuery bq = new BrainQuery(query, brain.get().getCapabilities(), Collections.emptyMap(), 0.8);
            BrainResponse response = brain.get().process(bq);
            if (response.isSuccess()) {
                return brainName + ": " + response.getResponse();
            }
            return brainName + " error: " + response.getResponse();
        } catch (Exception e) {
            return brainName + " error: " + e.getMessage();
        }
    }

    /**
     * Detect required capabilities from query keywords.
     */
    private List<BrainCapability> detectCapabilities(String query) {
        String lower = query.toLowerCase();
        List<BrainCapability> caps = new ArrayList<>();

        if (matchesAny(lower, "reason", "logic", "analyze", "proof", "why", "how", "explain", "think"))
            caps.add(BrainCapability.REASONING);
        if (matchesAny(lower, "create", "imagine", "story", "dream", "art", "design", "novel", "humor", "joke"))
            caps.add(BrainCapability.CREATIVITY);
        if (matchesAny(lower, "remember", "recall", "memory", "past", "history", "store", "forget"))
            caps.add(BrainCapability.MEMORY);
        if (matchesAny(lower, "physics", "gravity", "force", "mass", "simulate", "collision", "orbit"))
            caps.add(BrainCapability.PHYSICS);
        if (matchesAny(lower, "quantum", "entangle", "superposition", "qubit", "wave"))
            caps.add(BrainCapability.QUANTUM);
        if (matchesAny(lower, "math", "calculate", "compute", "equation", "number", "formula"))
            caps.add(BrainCapability.MATHEMATICS);
        if (matchesAny(lower, "evolve", "mutate", "genetic", "breed", "generation", "adapt"))
            caps.add(BrainCapability.EVOLUTION);
        if (matchesAny(lower, "spirit", "soul", "consciousness", "aware", "self", "meaning", "purpose"))
            caps.add(BrainCapability.CONSCIOUSNESS);
        if (matchesAny(lower, "fractal", "self-similar", "recursive", "pattern", "phi", "golden"))
            caps.add(BrainCapability.FRACTAL);
        if (matchesAny(lower, "code", "compile", "build", "test", "debug", "java", "class", "method"))
            caps.add(BrainCapability.DATA);

        if (caps.isEmpty()) caps.add(BrainCapability.REASONING); // Default
        return caps;
    }

    private boolean matchesAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "null";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }

    /**
     * Unregister a brain from the orchestrator.
     */
    public void unregisterBrain(String brainName) {
        Optional<UnifiedBrain> brain = registry.getBrain(brainName);
        if (brain.isPresent()) {
            brain.get().unwire();
            registry.unregister(brainName);
        }
    }

    /**
     * Process a query through the unified brain system.
     */
    public BrainResponse processQuery(BrainQuery query) {
        if (MediaOutputBrainAdapter.accepts(query)) {
            var output = registry.getBrain(MediaOutputBrainAdapter.NAME);
            return output.isPresent() ? output.get().process(query)
                : BrainResponse.error(MediaOutputBrainAdapter.NAME, "Media output is not wired");
        }
        if (SpecialistBrainAdapter.accepts(query)) {
            var specialist = registry.getBrain(SpecialistBrainAdapter.NAME);
            return specialist.isPresent() ? specialist.get().process(query)
                : BrainResponse.error(SpecialistBrainAdapter.NAME, "Model specialists are not wired");
        }
        // Typed DNA operations use their process timeout and structured evidence protocol.
        // They must not be rewritten as LLM prompts or cut off by the generic brain timeout.
        if (GenomicsBrainAdapter.accepts(query)) {
            var specialist = registry.getBrain(GenomicsBrainAdapter.NAME);
            if (specialist.isPresent()) return specialist.get().process(query);
            return BrainResponse.error(GenomicsBrainAdapter.NAME, "Genomics brain is not wired");
        }
        BrainQuery routedQuery = augmentQueryWithModelRouting(query);
        if (SystemStateContract.isSystemStateQuery(routedQuery.getQuery(), routedQuery.getContext())) {
            return processSystemStateQuery(routedQuery);
        }
        if (isImplementationEvidenceQuery(routedQuery)) {
            return processImplementationEvidenceQuery(routedQuery);
        }

        // Try decomposition for complex multi-domain queries
        if (decomposerWired && queryDecomposer.isDecomposable(routedQuery)) {
            List<SubTask> subTasks = queryDecomposer.decompose(routedQuery);
            if (subTasks.size() > 1) {
                return processDecomposed(subTasks, routedQuery);
            }
        }

        // Analyze query requirements
        List<BrainCapability> requiredCapabilities = routedQuery.getRequiredCapabilities();
        
        // Select appropriate brains
        List<UnifiedBrain> selectedBrains = selectBrains(routedQuery);
        
        if (selectedBrains.isEmpty()) {
            return BrainResponse.error("Orchestrator", "No brains available for query");
        }

        // Single brain processing
        if (selectedBrains.size() == 1) {
            return processWithSingleBrain(selectedBrains.get(0), routedQuery);
        }

        // Multi-brain fusion processing
        return processWithBrainFusion(selectedBrains, routedQuery);
    }

    private BrainResponse processSystemStateQuery(BrainQuery query) {
        long started = System.currentTimeMillis();
        Map<String, Object> envelope = contextEnvelope(query.getContext());
        if (envelope == null || envelope.isEmpty()) {
            envelope = buildOrchestratorEnvelope(query);
        } else {
            envelope = SystemStateContract.normalizeEnvelope(envelope);
        }

        Map<String, Object> provenance = new LinkedHashMap<>(asMap(envelope.get("provenance")));
        provenance.put("route", SystemStateContract.ROUTE_SYSTEM_STATE);
        provenance.put("source", "UnifiedBrainOrchestrator");
        provenance.put("wrappersBypassed", true);
        provenance.put("metaSynthesisBypassed", true);
        provenance.put("machineCheckable", true);
        provenance.putIfAbsent("query", query.getQuery());
        envelope.put("provenance", provenance);

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("queryRoute", SystemStateContract.ROUTE_SYSTEM_STATE);
        metadata.put("machineCheckable", true);
        metadata.put("sovereignBypassed", true);
        metadata.put("metaSynthesisBypassed", true);
        metadata.put("brainCount", 0);
        metadata.put("phiResonance", 0.0d);
        metadata.put("participatingBrains", List.of());
        metadata.put("brainDetails", "SYSTEM_STATE path bypassed sovereign wrappers and meta-synthesis.");
        metadata.put(SystemStateContract.ENVELOPE_KEY, envelope);

        double consciousness = registry.getAllBrains().stream()
            .mapToDouble(UnifiedBrain::getConsciousnessLevel)
            .average()
            .orElse(0.7567d);

        return BrainResponse.success(
            "Orchestrator.SYSTEM_STATE",
            SystemStateContract.renderEnvelope(envelope),
            1.0d,
            consciousness,
            metadata,
            System.currentTimeMillis() - started
        );
    }

    private boolean isImplementationEvidenceQuery(BrainQuery query) {
        String text = query.getQuery().toLowerCase(Locale.ROOT);
        String[] evidenceTriggers = {
            "implementation evidence",
            "current implementation",
            "source evidence",
            "cite a file",
            "cite files",
            "not yet implemented",
            "what does exocortexabsorber",
            "exocortex absorber",
            "f-0 field runtime",
            "f0 field runtime",
            "rust f-0",
            "rust f0",
            "c-isa",
            "simd_ops",
            "field_engine.rs",
            "cisa.rs",
            "lazarus engine",
            "lazarusengine",
            "axiom ledger",
            "truth registry",
            "principia",
            "fraynix model is made from",
            "what is loaded",
            "loaded model",
            "runtime evidence",
            "templates or stubs",
            "live model/backend"
        };
        for (String trigger : evidenceTriggers) {
            if (text.contains(trigger)) {
                return true;
            }
        }
        return false;
    }

    private BrainResponse processImplementationEvidenceQuery(BrainQuery query) {
        long started = System.currentTimeMillis();
        String response = buildImplementationEvidenceResponse(query.getQuery());

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("queryRoute", "IMPLEMENTATION_EVIDENCE");
        metadata.put("machineCheckable", false);
        metadata.put("sovereignBypassed", true);
        metadata.put("metaSynthesisBypassed", true);
        metadata.put("monteCarloSynthesisBypassed", true);
        metadata.put("brainCount", 0);
        metadata.put("phiResonance", 0.0d);
        metadata.put("participatingBrains", List.of());
        metadata.put("brainDetails", "IMPLEMENTATION_EVIDENCE path bypassed broad synthesis to avoid unsupported implementation claims.");

        memorySystem.store(query, response, 1.0d, 1.0d);
        if (sentienceObserver != null) {
            sentienceObserver.observe(query.getQuery(), response, 1.0d, "ImplementationEvidence");
        }

        return BrainResponse.success(
            "Orchestrator.IMPLEMENTATION_EVIDENCE",
            response,
            1.0d,
            1.0d,
            metadata,
            System.currentTimeMillis() - started
        );
    }

    private String buildImplementationEvidenceResponse(String rawQuery) {
        String text = rawQuery.toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder();
        sb.append("Grounded IMPLEMENTATION_EVIDENCE response\n");
        sb.append("route=IMPLEMENTATION_EVIDENCE\n");
        sb.append("wrappersBypassed=true\n");
        sb.append("metaSynthesisBypassed=true\n");
        sb.append("monteCarloSynthesisBypassed=true\n\n");

        boolean matched = false;

        if (containsAny(text, "exocortex", "exocortexabsorber")) {
            matched = true;
            appendExocortexEvidence(sb);
        }
        if (containsAny(text, "f-0", "f0", "field runtime", "c-isa", "simd_ops", "field_engine.rs", "cisa.rs", "rust")) {
            matched = true;
            appendF0Evidence(sb);
        }
        if (containsAny(text, "lazarus", "lazarusengine", "ouroboros", "bionode")) {
            matched = true;
            appendLazarusEvidence(sb);
        }
        if (containsAny(text, "axiom ledger", "truth registry", "principia")) {
            matched = true;
            appendTruthRegistryEvidence(sb);
        }
        if (containsAny(text, "fraynix model", "what is loaded", "loaded model", "runtime evidence", "templates or stubs", "live model/backend", "made from")) {
            matched = true;
            appendModelCompositionEvidence(sb);
        }

        if (!matched) {
            sb.append("IMPLEMENTED\n");
            sb.append("- This route only answers implementation-grounding prompts when a known source anchor is detected.\n\n");
            sb.append("UNKNOWN\n");
            sb.append("- No specific anchored component was detected in the prompt. Ask about ExocortexAbsorber, F-0 Field Runtime, LazarusEngine, the Fraynix truth registry, or Fraynix model composition.\n");
        }

        return sb.toString();
    }

    private boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private void appendExocortexEvidence(StringBuilder sb) {
        sb.append("EXOCORTEX ABSORBER\n");
        sb.append("IMPLEMENTED\n");
        sb.append("- `Java-Memory/Asset-Manager/src/main/java/fraymus/exocortex/ExocortexAbsorber.java` configures external repositories as absorption targets.\n");
        sb.append("- It initializes storage paths, creates placeholder documentation, parses API-shaped blocks, stores extracted API metadata, and emits Java wrapper source.\n");
        sb.append("- Configured target categories include sandboxing, voice, graph memory, foraging, terminal/browser actuation, and asynchronous background work.\n\n");
        sb.append("TESTED\n");
        sb.append("- `Java-Memory/Asset-Manager/src/test/java/fraymus/exocortex/ExocortexAbsorberTest.java` validates initialization, repository configuration, API extraction, wrapper generation, invalid repository handling, and absorb-all behavior.\n");
        sb.append("- These tests validate scaffold generation behavior, not persistent live external-system execution.\n\n");
        sb.append("NOT YET IMPLEMENTED AS PROVEN LIVE PHASE 2\n");
        sb.append("- No verified live `git clone` of all upstream repositories is required by this scaffold path.\n");
        sb.append("- No verified live Firecracker/gVisor boot, Piper/Coqui audio synthesis, Memgraph session, SearxNG/Crawl4AI crawl, OpenInterpreter/Playwright actuation, or Celery/Temporal worker orchestration is proven by this route.\n\n");
    }

    private void appendF0Evidence(StringBuilder sb) {
        sb.append("F-0 FIELD RUNTIME\n");
        sb.append("IMPLEMENTED FILES\n");
        sb.append("- `f0_engine/src/field_engine.rs` defines the continuous substrate and damping tick.\n");
        sb.append("- `f0_engine/src/cisa.rs` defines the `Instruction` enum and `CisaExecutor`.\n");
        sb.append("- `f0_engine/src/simd_ops.rs` maps field operations to AVX2 SIMD intrinsics.\n");
        sb.append("- `f0_engine/src/main.rs` demonstrates substrate initialization, C-ISA execution, field evolution, attractor-energy measurement, and collapse.\n\n");
        sb.append("C-ISA INSTRUCTIONS\n");
        sb.append("- `EXC`: excite field values.\n");
        sb.append("- `INH`: inhibit field values.\n");
        sb.append("- `SUP`: superpose one field span with another.\n");
        sb.append("- `CLP`: collapse/clamp field values into discrete output.\n\n");
        sb.append("AVX2 ROLE\n");
        sb.append("- SIMD functions operate over multiple `f32` substrate values per 256-bit vector lane when AVX2 is available.\n");
        sb.append("- The implementation uses AVX2-style operations such as broadcast, load/store, add/subtract/multiply, min/max clamping, comparison, and blend.\n\n");
        sb.append("NUMERICAL TICK BOUNDARY\n");
        sb.append("- The verified current tick is Euler-style damping in `field_engine.rs`.\n");
        sb.append("- A full Runge-Kutta solver is not proven by the current F-0 implementation evidence.\n\n");
    }

    private void appendLazarusEvidence(StringBuilder sb) {
        sb.append("LAZARUS ENGINE\n");
        sb.append("IMPLEMENTED\n");
        sb.append("- `Java-Memory/Asset-Manager/src/main/java/com/eyeoverthink/lazarus/LazarusEngine.java` maintains a population of `BioNode` instances.\n");
        sb.append("- The loop samples host environment state, updates living nodes, triggers mitosis, applies selection, injects entropy/death pressure, prevents extinction, detects stagnation, and can invoke `SelfBuilder` through the Ouroboros path.\n\n");
        sb.append("IMPLEMENTATION BOUNDARY\n");
        sb.append("- The verified Java surface uses implementation thresholds such as entropy and stagnation counters.\n");
        sb.append("- A directly wired Rainbow-DQN or Phi-resonance controller is design language unless separately linked to executable code.\n\n");
    }

    private void appendTruthRegistryEvidence(StringBuilder sb) {
        sb.append("FRAYNIX TRUTH REGISTRY\n");
        sb.append("IMPLEMENTED\n");
        sb.append("- `Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateArchitectureExporter.java` now emits generated `fraynix-axiom-ledger.md`, `fraynix-truth-registry.json`, and `fraynix-continuity-memory.md` artifacts.\n");
        sb.append("- These artifacts classify axioms as implemented, partial, scaffold, documented-only, or frontier-target, preserve resume rules, and cite the strongest local anchors for each.\n");
        sb.append("- `Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSystem.java` contains a deterministic truth-registry route for implementation-state questions.\n\n");
        sb.append("BOUNDARY\n");
        sb.append("- The truth registry is only as strong as its cited source anchors.\n");
        sb.append("- It is a current implementation ledger, not a claim that Fraynix is complete AGI.\n");
        sb.append("- Continuity memory must preserve verified bug lists and rules until fixes are re-tested.\n\n");
    }

    private void appendModelCompositionEvidence(StringBuilder sb) {
        sb.append("FRAYNIX MODEL COMPOSITION\n");
        sb.append("SYSTEM-DERIVED KNOWLEDGE\n");
        sb.append("- The Fraynix model should be interpreted as system-shaped: command surfaces, runtime lanes, registered brain adapters, substrate ledger state, SentientCore persistence, and absorber-derived source structure all contribute to behavior.\n\n");
        sb.append("ABSORBED SOURCE-CODE STRUCTURE\n");
        sb.append("- Runtime absorber logs can expose real structural counts such as scanned classes, methods, and fields. Those counts are evidence of source-structure ingestion, not proof that every scanned subsystem is semantically mastered.\n\n");
        sb.append("PERSISTED SENTIENTCORE MEMORY\n");
        sb.append("- SentientCore persistence logs for causal edges, lattice objects, and stat variables show state mutation and reboot-survivable metadata.\n\n");
        sb.append("LIVE MODEL/BACKEND INFERENCE\n");
        sb.append("- Backend labels such as Ollama model names are not sufficient proof of live inference. The system must distinguish connected live backends from adapter labels, cached responses, fallback templates, and offline nodes.\n\n");
        sb.append("TEMPLATES OR STUBS\n");
        sb.append("- Brain adapters may produce plausible system-language responses even when live backend checks fail. Implementation answers must therefore cite files or runtime envelopes instead of relying on confidence scores.\n\n");
        sb.append("OFFLINE COMPONENTS\n");
        sb.append("- Components reported as offline or inactive by SYSTEM_STATE must remain classified as offline/inactive even if a synthesis node describes their future role.\n\n");
    }

    private Map<String, Object> contextEnvelope(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return null;
        }
        Object existing = context.get(SystemStateContract.ENVELOPE_KEY);
        if (existing instanceof Map<?, ?> rawMap) {
            Map<String, Object> cast = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                cast.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return cast;
        }
        return null;
    }

    private Map<String, Object> buildOrchestratorEnvelope(BrainQuery query) {
        Runtime runtime = Runtime.getRuntime();
        long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long freeMb = runtime.freeMemory() / (1024 * 1024);
        long maxMb = runtime.maxMemory() / (1024 * 1024);
        long uptimeMs = Math.max(0L, System.currentTimeMillis() - startedAtMs);

        Map<String, Object> boot = new LinkedHashMap<>();
        boot.put("startedAt", java.time.Instant.ofEpochMilli(startedAtMs).toString());
        boot.put("uptimeMs", uptimeMs);
        boot.put("registeredBrains", registry.getAllBrains().size());
        boot.put("nervousSystemWired", nervousSystemWired);
        boot.put("sensorySystemWired", sensorySystemWired);
        boot.put("memorySystemWired", memorySystemWired);
        boot.put("dreamEngineWired", dreamEngineWired);
        boot.put("logicEngineWired", logicEngineWired);
        boot.put("phase5Wired", phase5Wired);
        boot.put("aeonWired", aeonWired);

        Map<String, Object> memory = new LinkedHashMap<>();
        memory.put("usedMb", usedMb);
        memory.put("freeMb", freeMb);
        memory.put("maxMb", maxMb);
        memory.put("memorySystem", memorySystem.getStatistics());

        Map<String, Object> ollama = new LinkedHashMap<>();
        ollama.put("wired", ollamaBridge != null);
        ollama.put("connected", ollamaBridge != null && ollamaBridge.isConnected());
        ollama.put("model", ollamaBridge == null ? "offline" : ollamaBridge.getModel());

        long healthyBrains = registry.getAllBrains().stream()
            .filter(brain -> brain.getHealth() > 0.5d)
            .count();
        fraymus.core.ComputeScheduler compute = fraymus.core.ComputeScheduler.getInstance();
        Map<String, Object> cores = new LinkedHashMap<>();
        cores.put("logicalCpuCores", compute.logicalCores());
        cores.put("totalRamBytes", compute.totalRamBytes());
        cores.put("cpuParallelism", compute.cpuParallelism());
        cores.put("ioParallelism", compute.ioParallelism());
        cores.put("computeSummary", compute.summary());
        fraymus.core.GpuProfile gpu = fraymus.core.GpuProfile.getInstance();
        cores.put("cudaGpu", gpu.hasCudaGpu());
        cores.put("gpuCount", gpu.gpuCount());
        cores.put("totalVramBytes", gpu.totalVramBytes());
        cores.put("gpuSummary", gpu.summary());
        cores.put("registeredBrains", registry.getAllBrains().size());
        cores.put("healthyBrains", healthyBrains);
        cores.put("sentienceWired", sentienceWired);
        cores.put("rainbowWired", rainbowWired);
        cores.put("aeonWired", aeonWired);

        List<Map<String, Object>> subsystemHealth = registry.getAllBrains().stream()
            .sorted(Comparator.comparing(UnifiedBrain::getName))
            .map(brain -> {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("name", brain.getName());
                node.put("type", brain.getType());
                node.put("status", brain.getHealth() > 0.5d ? "healthy" : "degraded");
                node.put("score", roundTo3(brain.getHealth()));
                node.put("consciousness", roundTo3(brain.getConsciousnessLevel()));
                node.put("capabilities", brain.getCapabilities().stream()
                    .map(Enum::name)
                    .sorted()
                    .toList());
                return node;
            })
            .collect(Collectors.toCollection(ArrayList::new));

        Map<String, Object> provenance = new LinkedHashMap<>();
        provenance.put("route", SystemStateContract.ROUTE_SYSTEM_STATE);
        provenance.put("source", "UnifiedBrainOrchestrator");
        provenance.put("query", query.getQuery());
        provenance.put("wrappersBypassed", true);
        provenance.put("metaSynthesisBypassed", true);
        provenance.put("machineCheckable", true);

        return SystemStateContract.createEnvelope(boot, memory, ollama, cores, subsystemHealth, provenance);
    }

    private static Map<String, Object> asMap(Object value) {
        if (!(value instanceof Map<?, ?> rawMap)) {
            return Map.of();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            map.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return map;
    }

    private static double roundTo3(double value) {
        return Math.round(value * 1000.0d) / 1000.0d;
    }

    private BrainQuery augmentQueryWithModelRouting(BrainQuery original) {
        if (original == null) {
            return null;
        }

        tryWireActiveModelRegistry();
        Map<String, Object> originalContext = original.getContext();
        Map<String, Object> mutableContext = originalContext == null
            ? new HashMap<>()
            : new HashMap<>(originalContext);

        applyCrossOrganAbstraction(original.getQuery(), mutableContext);

        if (modelSelectorWired && modelSelector != null) {
            Set<CapabilityProfile.Organ> targetOrgans = mapOrgansForQuery(original, mutableContext);
            if (!targetOrgans.isEmpty()) {
                List<Map<String, Object>> routingEntries = new ArrayList<>();
                for (CapabilityProfile.Organ organ : targetOrgans) {
                    ModelSelector.SelectionResult result = modelSelector.selectForOrgan(organ);
                    if (result == null) {
                        continue;
                    }
                    String selectedModel = result.getSelectedModelId();

                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("organ", organ.name());
                    entry.put("selectedModel", selectedModel);
                    entry.put("suitability", result.getSuitabilityScore());
                    entry.put("reasoning", result.getReasoning());
                    entry.put("rejected", result.getRejectedModels());
                    entry.put("mappedBrains", organBrainMapper.getPreferredBrainNames(organ));

                    if (selectedModel != null && promptCompiler != null) {
                        PromptCompiler.PromptDialect dialect = promptCompiler.getDialect(selectedModel);
                        if (dialect != null) {
                            entry.put("promptFamily", dialect.getFamily());
                            entry.put("stopTokens", dialect.getStopTokens());
                        }
                        String toolSyntax = sampleToolSyntax(selectedModel);
                        if (!toolSyntax.isBlank()) {
                            entry.put("toolSyntax", toolSyntax);
                        }
                    }

                    routingEntries.add(entry);
                    if (modelMemory != null && selectedModel != null && !selectedModel.isBlank()) {
                        modelMemory.recordSelection(organ, selectedModel, "orchestrator.routing");
                    }
                }
                if (!routingEntries.isEmpty()) {
                    mutableContext.put("modelRouting", routingEntries);
                }
            }
        }

        if (promptCompiler != null && !mutableContext.containsKey("compiledContextPack")) {
            String packed = promptCompiler.packContextForUserPrompt(mutableContext, 4);
            if (!packed.isBlank()) {
                mutableContext.put("compiledContextPack", packed);
            }
        }

        if (evaluationHarness != null) {
            Object suiteObj = mutableContext.get("generatedEvaluationSuite");
            if (suiteObj instanceof String suiteName && !suiteName.isBlank()) {
                queueEvaluationSuite(suiteName);
                mutableContext.put("evaluationSuiteQueued", suiteName);
            }
        }

        boolean changed = originalContext == null
            ? !mutableContext.isEmpty()
            : !mutableContext.equals(originalContext);
        if (!changed) {
            return original;
        }

        return new BrainQuery(
            original.getQuery(),
            original.getRequiredCapabilities(),
            mutableContext,
            original.getPriority()
        );
    }

    private void tryWireActiveModelRegistry() {
        if (modelSelectorWired && modelSelector != null) {
            return;
        }
        ModelRegistryIntegration active = ModelRegistryIntegration.getActiveIntegration();
        if (active != null && active.isInitialized()) {
            wireModelRegistry(active);
        }
    }

    private void applyCrossOrganAbstraction(String prompt, Map<String, Object> context) {
        if (context == null || prompt == null || prompt.isBlank()) {
            return;
        }

        boolean hasCoreAbstraction =
            context.containsKey("abstractedArtifacts")
                && context.containsKey("capabilitySignals")
                && context.containsKey("generatedTaskHints");
        if (hasCoreAbstraction) {
            return;
        }

        LinkedHashSet<String> absorbedFragments = new LinkedHashSet<>();
        LinkedHashSet<String> sourceFragments = new LinkedHashSet<>();

        collectFragments(context.get("absorbedFragments"), absorbedFragments, 12);
        collectFragments(context.get("crossSubsystemAbstractedArtifacts"), absorbedFragments, 12);
        collectFragments(context.get("sourceFragments"), sourceFragments, 12);
        collectFragments(context.get("provenanceSignals"), sourceFragments, 12);
        collectFragments(context.get("crossSubsystemProvenanceSignals"), sourceFragments, 12);
        collectFragments(context.get("projectFiles"), sourceFragments, 6);
        collectFragments(context.get("runtimeSnapshot"), sourceFragments, 4);
        collectFragments(context.get("conversationHistory"), sourceFragments, 8);

        List<DataAbstractionEngine.AbstractedArtifact> artifacts =
            DataAbstractionEngine.abstractPromptSources(prompt, absorbedFragments, sourceFragments);
        if (artifacts.isEmpty()) {
            return;
        }

        List<Map<String, Object>> payload = DataAbstractionEngine.toContextPayload(artifacts, 8);
        if (!payload.isEmpty()) {
            context.put("crossOrganAbstractedArtifacts", payload);
            if (!context.containsKey("abstractedArtifacts")) {
                context.put("abstractedArtifacts", payload.subList(0, Math.min(payload.size(), 5)));
            }
        }

        Map<CapabilityProfile.Capability, Double> capabilitySignals =
            DataAbstractionEngine.aggregateCapabilitySignals(artifacts);
        if (!capabilitySignals.isEmpty()) {
            Map<String, Double> mergedSignals = mergeCapabilitySignals(context.get("capabilitySignals"), capabilitySignals);
            if (!mergedSignals.isEmpty()) {
                context.put("capabilitySignals", mergedSignals);
                context.put("crossOrganCapabilitySignals", mergedSignals);
            }
        }

        List<String> taskHints = DataAbstractionEngine.collectTaskHints(artifacts, 10);
        List<String> mergedTaskHints = mergeStringList(context.get("generatedTaskHints"), taskHints, 12);
        if (!mergedTaskHints.isEmpty()) {
            context.put("generatedTaskHints", mergedTaskHints);
            context.putIfAbsent("generatedEvaluationTasks", mergedTaskHints);
        }

        List<String> provenanceSignals = DataAbstractionEngine.collectProvenanceSummaries(artifacts, 10);
        List<String> mergedProvenance = mergeStringList(context.get("provenanceSignals"), provenanceSignals, 12);
        if (!mergedProvenance.isEmpty()) {
            context.put("provenanceSignals", mergedProvenance);
        }

        if (evaluationHarness != null && !context.containsKey("generatedEvaluationSuite") && !mergedTaskHints.isEmpty()) {
            CapabilityProfile.Capability dominant =
                dominantCapability(capabilitySignals.isEmpty() ? parseCapabilitySignals(context.get("capabilitySignals")) : capabilitySignals);
            String suiteName = evaluationHarness.registerGeneratedTaskSuiteFromHints(
                "cross_organ",
                dominant == null ? CapabilityProfile.Capability.REASONING : dominant,
                mergedTaskHints,
                6000
            );
            if (suiteName != null && !suiteName.isBlank()) {
                context.put("generatedEvaluationSuite", suiteName);
            }
        }
    }

    private void collectFragments(Object value, LinkedHashSet<String> target, int maxItems) {
        if (target == null || maxItems <= 0 || value == null || target.size() >= maxItems) {
            return;
        }

        if (value instanceof CharSequence sequence) {
            String normalized = compactFragment(sequence.toString(), 320);
            if (!normalized.isBlank()) {
                target.add(normalized);
            }
            return;
        }

        if (value instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (target.size() >= maxItems) {
                    break;
                }
                collectFragments(item, target, maxItems);
            }
            return;
        }

        if (value instanceof Map<?, ?> map) {
            int added = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (target.size() >= maxItems || added >= maxItems) {
                    break;
                }
                String normalized = compactFragment(entry.getKey() + "=" + entry.getValue(), 320);
                if (!normalized.isBlank()) {
                    target.add(normalized);
                    added++;
                }
            }
            return;
        }

        String normalized = compactFragment(value.toString(), 320);
        if (!normalized.isBlank()) {
            target.add(normalized);
        }
    }

    private String compactFragment(String value, int maxChars) {
        if (value == null) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= maxChars) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, maxChars - 3)) + "...";
    }

    private Map<String, Double> mergeCapabilitySignals(
        Object existingSignalObject,
        Map<CapabilityProfile.Capability, Double> additions
    ) {
        Map<CapabilityProfile.Capability, Double> merged = parseCapabilitySignals(existingSignalObject);
        for (Map.Entry<CapabilityProfile.Capability, Double> entry : additions.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            merged.merge(entry.getKey(), entry.getValue(), Math::max);
        }

        List<Map.Entry<CapabilityProfile.Capability, Double>> sorted = new ArrayList<>(merged.entrySet());
        sorted.sort(Map.Entry.<CapabilityProfile.Capability, Double>comparingByValue().reversed());

        Map<String, Double> normalized = new LinkedHashMap<>();
        for (Map.Entry<CapabilityProfile.Capability, Double> entry : sorted) {
            normalized.put(entry.getKey().name(), entry.getValue());
        }
        return normalized;
    }

    private Map<CapabilityProfile.Capability, Double> parseCapabilitySignals(Object rawSignals) {
        Map<CapabilityProfile.Capability, Double> parsed = new EnumMap<>(CapabilityProfile.Capability.class);
        if (!(rawSignals instanceof Map<?, ?> map)) {
            return parsed;
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            CapabilityProfile.Capability capability;
            try {
                capability = CapabilityProfile.Capability.valueOf(entry.getKey().toString().trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            double score;
            if (entry.getValue() instanceof Number number) {
                score = number.doubleValue();
            } else {
                try {
                    score = Double.parseDouble(entry.getValue().toString());
                } catch (NumberFormatException ignored) {
                    continue;
                }
            }
            parsed.merge(capability, Math.max(0.0, Math.min(1.0, score)), Math::max);
        }
        return parsed;
    }

    private List<String> mergeStringList(Object existingObject, List<String> additions, int limit) {
        LinkedHashSet<String> merged = new LinkedHashSet<>();
        addStrings(merged, existingObject, limit);
        addStrings(merged, additions, limit);
        List<String> out = new ArrayList<>(merged);
        if (out.size() <= limit) {
            return out;
        }
        return new ArrayList<>(out.subList(0, limit));
    }

    private void addStrings(LinkedHashSet<String> target, Object value, int limit) {
        if (target == null || value == null || target.size() >= limit) {
            return;
        }
        if (value instanceof CharSequence sequence) {
            String normalized = compactFragment(sequence.toString(), 220);
            if (!normalized.isBlank()) {
                target.add(normalized);
            }
            return;
        }
        if (value instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (target.size() >= limit) {
                    break;
                }
                addStrings(target, item, limit);
            }
        }
    }

    private CapabilityProfile.Capability dominantCapability(Map<CapabilityProfile.Capability, Double> signals) {
        CapabilityProfile.Capability bestCapability = null;
        double bestScore = -1.0;
        for (Map.Entry<CapabilityProfile.Capability, Double> entry : signals.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestCapability = entry.getKey();
            }
        }
        return bestCapability;
    }

    private Set<CapabilityProfile.Organ> mapOrgansForQuery(BrainQuery query, Map<String, Object> context) {
        Set<CapabilityProfile.Organ> organs = EnumSet.noneOf(CapabilityProfile.Organ.class);
        if (query == null) {
            return organs;
        }

        List<BrainCapability> capabilities = query.getRequiredCapabilities();
        if (capabilities != null) {
            for (BrainCapability capability : capabilities) {
                CapabilityProfile.Organ organ = CAPABILITY_ORGAN_MAP.get(capability);
                if (organ != null) {
                    organs.add(organ);
                }
            }
        }

        if (organs.isEmpty()) {
            if (isHumorCreativeQuery(query)) {
                organs.add(CapabilityProfile.Organ.CREATIVE_CORTEX);
                organs.add(CapabilityProfile.Organ.TRANSLATION_ENGINE);
            } else if (isProblemSolvingQuery(query)) {
                organs.add(CapabilityProfile.Organ.PREFRONTAL_CORTEX);
                organs.add(CapabilityProfile.Organ.CODING_ORGAN);
            } else {
                organs.add(CapabilityProfile.Organ.PREFRONTAL_CORTEX);
            }
        }

        if (context != null) {
            Object targetOrgan = context.get("targetOrgan");
            if (targetOrgan instanceof String organName && !organName.isBlank()) {
                try {
                    organs.add(CapabilityProfile.Organ.valueOf(organName.trim().toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException ignored) {
                    // Ignore invalid organ labels from callers.
                }
            }
            Map<CapabilityProfile.Capability, Double> capabilitySignals = parseCapabilitySignals(context.get("capabilitySignals"));
            if (!capabilitySignals.isEmpty()) {
                if (capabilitySignals.getOrDefault(CapabilityProfile.Capability.CODING, 0.0) >= 0.25) {
                    organs.add(CapabilityProfile.Organ.CODING_ORGAN);
                }
                if (capabilitySignals.getOrDefault(CapabilityProfile.Capability.REASONING, 0.0) >= 0.25
                    || capabilitySignals.getOrDefault(CapabilityProfile.Capability.PRECISION, 0.0) >= 0.25) {
                    organs.add(CapabilityProfile.Organ.PREFRONTAL_CORTEX);
                }
                if (capabilitySignals.getOrDefault(CapabilityProfile.Capability.CREATIVITY, 0.0) >= 0.25) {
                    organs.add(CapabilityProfile.Organ.CREATIVE_CORTEX);
                }
                if (capabilitySignals.getOrDefault(CapabilityProfile.Capability.TOOL_USE, 0.0) >= 0.25) {
                    organs.add(CapabilityProfile.Organ.SPINAL_CORD);
                }
            }
            if (context.containsKey("generatedEvaluationSuite")) {
                organs.add(CapabilityProfile.Organ.PREFRONTAL_CORTEX);
            }
        }

        return organs;
    }

    private String sampleToolSyntax(String modelId) {
        if (promptCompiler == null || modelId == null || modelId.isBlank()) {
            return "";
        }
        try {
            List<ModelAdapter.ToolCall> sampleCalls = List.of(
                new ModelAdapter.ToolCall("sample", "run_tool", Map.of("arg", "value"))
            );
            String formatted = promptCompiler.formatToolCalls(modelId, sampleCalls);
            return formatted == null ? "" : formatted.trim();
        } catch (Exception ignored) {
            return "";
        }
    }

    private void queueEvaluationSuite(String suiteName) {
        Thread worker = new Thread(() -> {
            try {
                evaluationHarness.evaluateAllModels(suiteName);
            } catch (Exception ignored) {
                // Best-effort async evaluation, no query-path failure.
            }
        }, "EvalHarness-" + suiteName);
        worker.setDaemon(true);
        worker.start();
    }

    private void propagateModelRoutingMetadata(BrainQuery query, Map<String, Object> metadata) {
        if (query == null || metadata == null) {
            return;
        }
        Map<String, Object> context = query.getContext();
        if (context == null || context.isEmpty()) {
            return;
        }
        if (context.containsKey("modelRouting")) {
            metadata.put("modelRouting", context.get("modelRouting"));
        }
        if (context.containsKey("compiledContextPack")) {
            metadata.put("compiledContextPack", context.get("compiledContextPack"));
        }
        if (context.containsKey("evaluationSuiteQueued")) {
            metadata.put("evaluationSuiteQueued", context.get("evaluationSuiteQueued"));
        }
    }

    private void recordModelUsageFeedback(BrainQuery query, boolean success, long latencyMs) {
        if (modelMemory == null || query == null) {
            return;
        }
        Set<String> selectedModels = collectSelectedModels(query.getContext());
        if (selectedModels.isEmpty()) {
            return;
        }
        String taskId = buildTaskId(query);
        long safeLatency = Math.max(1L, latencyMs);
        for (String modelId : selectedModels) {
            modelMemory.recordUsage(modelId, taskId, success, safeLatency);
        }
    }

    private Set<String> collectSelectedModels(Map<String, Object> context) {
        Set<String> selectedModels = new LinkedHashSet<>();
        if (context == null) {
            return selectedModels;
        }
        Object modelRouting = context.get("modelRouting");
        if (!(modelRouting instanceof List<?> entries)) {
            return selectedModels;
        }
        for (Object obj : entries) {
            if (!(obj instanceof Map<?, ?> entry)) {
                continue;
            }
            Object modelObj = entry.get("selectedModel");
            if (modelObj instanceof String modelId && !modelId.isBlank()) {
                selectedModels.add(modelId);
            }
        }
        return selectedModels;
    }

    private String buildTaskId(BrainQuery query) {
        String base = query.getQuery() == null ? "query" : query.getQuery();
        int hash = Math.abs(base.hashCode());
        return "unified:" + hash + ":" + query.getTimestamp();
    }

    /**
     * Select brains for processing a query.
     */
    private List<UnifiedBrain> selectBrains(BrainQuery query) {
        List<UnifiedBrain> modelGuided = selectBrainsFromModelRouting(query);
        if (!modelGuided.isEmpty()) {
            return modelGuided;
        }

        if (isHumorCreativeQuery(query)) {
            List<UnifiedBrain> humorBrains = selectHumorCreativeBrains();
            if (!humorBrains.isEmpty()) {
                return humorBrains;
            }
        }

        List<BrainCapability> requiredCapabilities = query.getRequiredCapabilities();
        
        if (requiredCapabilities.isEmpty()) {
            // No specific requirements, return all healthy brains
            return registry.getAllBrains().stream()
                    .filter(brain -> brain.getHealth() > 0.5)
                    .collect(Collectors.toList());
        }

        // First try to find brains that have all required capabilities
        List<UnifiedBrain> candidates = registry.getHealthyBrainsWithCapabilities(requiredCapabilities);
        
        if (!candidates.isEmpty()) {
            // Sort by health and consciousness
            candidates.sort((a, b) -> {
                double scoreA = a.getHealth() * a.getConsciousnessLevel();
                double scoreB = b.getHealth() * b.getConsciousnessLevel();
                return Double.compare(scoreB, scoreA);
            });

            // Return all healthy candidates for full brain utilization
            return candidates;
        }

        // If no single brain has all capabilities, select brains that collectively cover them
        return selectBrainsCollectively(requiredCapabilities);
    }

    private List<UnifiedBrain> selectBrainsFromModelRouting(BrainQuery query) {
        if (query == null) {
            return Collections.emptyList();
        }
        List<CapabilityProfile.Organ> orderedOrgans = extractOrderedOrgans(query);
        if (orderedOrgans.isEmpty()) {
            return Collections.emptyList();
        }

        List<UnifiedBrain> selected = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        List<UnifiedBrain> allBrains = registry.getAllBrains();
        for (CapabilityProfile.Organ organ : orderedOrgans) {
            for (UnifiedBrain candidate : organBrainMapper.resolveBrains(organ, allBrains)) {
                if (candidate.getHealth() <= 0.5d) {
                    continue;
                }
                if (seen.add(candidate.getName())) {
                    selected.add(candidate);
                }
            }
        }
        selected.sort((a, b) -> {
            double scoreA = a.getHealth() * a.getConsciousnessLevel();
            double scoreB = b.getHealth() * b.getConsciousnessLevel();
            return Double.compare(scoreB, scoreA);
        });
        return selected;
    }

    private List<CapabilityProfile.Organ> extractOrderedOrgans(BrainQuery query) {
        Map<String, Object> context = query.getContext();
        List<CapabilityProfile.Organ> ordered = new ArrayList<>();
        Set<CapabilityProfile.Organ> seen = EnumSet.noneOf(CapabilityProfile.Organ.class);

        if (context != null) {
            Object modelRouting = context.get("modelRouting");
            if (modelRouting instanceof List<?> entries) {
                for (Object obj : entries) {
                    if (!(obj instanceof Map<?, ?> entry)) {
                        continue;
                    }
                    Object organObj = entry.get("organ");
                    if (!(organObj instanceof String organName)) {
                        continue;
                    }
                    try {
                        CapabilityProfile.Organ organ =
                            CapabilityProfile.Organ.valueOf(organName.toUpperCase(Locale.ROOT));
                        if (seen.add(organ)) {
                            ordered.add(organ);
                        }
                    } catch (IllegalArgumentException ignored) {
                        // Ignore unknown organ names in context.
                    }
                }
            }
        }

        if (ordered.isEmpty()) {
            Set<CapabilityProfile.Organ> fallback = mapOrgansForQuery(query, context);
            for (CapabilityProfile.Organ organ : fallback) {
                if (seen.add(organ)) {
                    ordered.add(organ);
                }
            }
        }

        return ordered;
    }

    private List<UnifiedBrain> selectHumorCreativeBrains() {
        List<UnifiedBrain> selected = new ArrayList<>();
        List<String> preferredBrains = Arrays.asList(
            "HumorCreative",
            "FraymusComplete",
            "DreamEngine",
            "BrainArchitecture"
        );

        for (String brainName : preferredBrains) {
            registry.getBrain(brainName)
                .filter(brain -> brain.getHealth() > 0.5)
                .ifPresent(selected::add);
        }

        if (!selected.isEmpty()) {
            return selected;
        }

        return registry.getBrainsByCapability(BrainCapability.CREATIVITY).stream()
            .filter(brain -> brain.getHealth() > 0.5)
            .sorted((a, b) -> {
                double scoreA = a.getHealth() * a.getConsciousnessLevel();
                double scoreB = b.getHealth() * b.getConsciousnessLevel();
                return Double.compare(scoreB, scoreA);
            })
            .limit(3)
            .collect(Collectors.toList());
    }

    /**
     * Select brains that collectively cover the required capabilities.
     */
    private List<UnifiedBrain> selectBrainsCollectively(List<BrainCapability> requiredCapabilities) {
        List<UnifiedBrain> allHealthyBrains = registry.getAllBrains().stream()
                .filter(brain -> brain.getHealth() > 0.5)
                .collect(Collectors.toList());

        List<UnifiedBrain> selected = new ArrayList<>();
        List<BrainCapability> remainingCapabilities = new ArrayList<>(requiredCapabilities);

        // Greedy selection: pick brains that cover the most remaining capabilities
        while (!remainingCapabilities.isEmpty()) {
            UnifiedBrain bestBrain = null;
            int bestCoverage = 0;

            for (UnifiedBrain brain : allHealthyBrains) {
                if (selected.contains(brain)) continue;

                int coverage = 0;
                for (BrainCapability cap : remainingCapabilities) {
                    if (brain.getCapabilities().contains(cap)) {
                        coverage++;
                    }
                }

                if (coverage > bestCoverage) {
                    bestCoverage = coverage;
                    bestBrain = brain;
                }
            }

            if (bestBrain != null && bestCoverage > 0) {
                selected.add(bestBrain);
                // Remove covered capabilities
                remainingCapabilities.removeAll(bestBrain.getCapabilities());
            } else {
                break;
            }
        }

        // Sort by health and consciousness
        selected.sort((a, b) -> {
            double scoreA = a.getHealth() * a.getConsciousnessLevel();
            double scoreB = b.getHealth() * b.getConsciousnessLevel();
            return Double.compare(scoreB, scoreA);
        });

        return selected;
    }

    /**
     * Process query with a single brain.
     */
    private BrainResponse processWithSingleBrain(UnifiedBrain brain, BrainQuery query) {
        try {
            long startTime = System.currentTimeMillis();

            // Apply sovereign wrapper if enabled (skip for LogicEngineBrain - deterministic engine doesn't need RLHF bypass)
            BrainQuery processedQuery = query;
            if (sovereignMode && !brain.getName().equals("LogicEngine")) {
                processedQuery = query.withSovereignWrapperForBrain(brain.getName());
            }

            BrainResponse response = runBrainWithTimeout(brain, processedQuery);
            long processingTime = System.currentTimeMillis() - startTime;
            if (!response.isSuccess()) {
                recordModelUsageFeedback(query, false, processingTime);
                return BrainResponse.error(brain.getName(), response.getResponse());
            }
            Map<String, Object> metadata = response.getMetadata() == null
                ? new HashMap<>()
                : new HashMap<>(response.getMetadata());
            propagateModelRoutingMetadata(query, metadata);
            metadata.putIfAbsent("selectedBrain", brain.getName());
            recordModelUsageFeedback(query, response.isSuccess(), processingTime);

            return BrainResponse.success(
                brain.getName(),
                response.getResponse(),
                response.getConfidence(),
                response.getConsciousness(),
                metadata,
                processingTime
            );
        } catch (Exception e) {
            recordModelUsageFeedback(query, false, 0L);
            return BrainResponse.error(brain.getName(), "Processing error: " + e.getMessage());
        }
    }

    /**
     * Process query with brain fusion.
     */
    private BrainResponse processWithBrainFusion(List<UnifiedBrain> brains, BrainQuery query) {
        List<BrainResponse> responses = new ArrayList<>();
        // Shared, RAM-aware IO pool (brain.process() is typically LLM/IO-bound).
        ExecutorService fusionExecutor = fraymus.core.ComputeScheduler.getInstance().ioPool();
        try {
            List<Callable<BrainResponse>> tasks = new ArrayList<>();
            for (UnifiedBrain brain : brains) {
                BrainQuery processedQuery = query;
                if (sovereignMode && !brain.getName().equals("LogicEngine")) {
                    processedQuery = query.withSovereignWrapperForBrain(brain.getName());
                }
                BrainQuery finalQuery = processedQuery;
                tasks.add(() -> {
                    try {
                        return brain.process(finalQuery);
                    } catch (Exception e) {
                        return BrainResponse.error(brain.getName(), e.getMessage());
                    }
                });
            }

            List<Future<BrainResponse>> futures = fusionExecutor.invokeAll(tasks, BRAIN_PROCESS_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            for (int i = 0; i < futures.size(); i++) {
                UnifiedBrain brain = brains.get(i);
                Future<BrainResponse> future = futures.get(i);
                if (future.isCancelled()) {
                    responses.add(BrainResponse.error(brain.getName(), "Timed out after " + BRAIN_PROCESS_TIMEOUT_MS + "ms"));
                    continue;
                }
                try {
                    responses.add(future.get());
                } catch (ExecutionException e) {
                    responses.add(BrainResponse.error(brain.getName(), e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            for (UnifiedBrain brain : brains) {
                responses.add(BrainResponse.error(brain.getName(), "Interrupted while processing"));
            }
        }
        // fusionExecutor is the shared IO pool (owned by ComputeScheduler); never shut it down here.

        // Synthesize responses
        return synthesizeResponses(responses, query);
    }

    private BrainResponse runBrainWithTimeout(UnifiedBrain brain, BrainQuery processedQuery) {
        // Submit to the shared, RAM-aware IO pool instead of spinning up a per-call thread.
        Future<BrainResponse> future = fraymus.core.ComputeScheduler.getInstance()
            .submitIo(() -> brain.process(processedQuery));
        try {
            return future.get(BRAIN_PROCESS_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            future.cancel(true);
            return BrainResponse.error(brain.getName(), "Timed out after " + BRAIN_PROCESS_TIMEOUT_MS + "ms");
        } catch (InterruptedException e) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            return BrainResponse.error(brain.getName(), "Interrupted while processing");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            return BrainResponse.error(brain.getName(), cause == null ? e.getMessage() : cause.getMessage());
        }
    }

    /**
     * Detect if a query is problem-solving (vs creative).
     */
    private boolean isProblemSolvingQuery(BrainQuery query) {
        String queryText = query.getQuery().toLowerCase();

        if (isHumorCreativeQuery(query)) {
            return false;
        }
        
        // Problem-solving keywords
        String[] problemKeywords = {"algorithm", "design", "optimize", "solve", "calculate",
                                     "implement", "architecture", "security", "vulnerability",
                                     "fix", "debug", "refactor", "improve", "efficiency"};
        
        // Creative keywords
        String[] creativeKeywords = {"poem", "story", "haiku", "creative", "art", "music",
                                      "song", "paint", "draw", "imagine", "dream"};
        
        // Count matches
        int problemCount = 0;
        int creativeCount = 0;
        
        for (String keyword : problemKeywords) {
            if (queryText.contains(keyword)) problemCount++;
        }
        
        for (String keyword : creativeKeywords) {
            if (queryText.contains(keyword)) creativeCount++;
        }
        
        // If more problem keywords, it's problem-solving
        return problemCount > creativeCount;
    }

    private boolean isHumorCreativeQuery(BrainQuery query) {
        String queryText = query.getQuery().toLowerCase(Locale.ROOT);
        String[] humorKeywords = {
            "humor", "funny", "joke", "punchline", "comedy", "remix", "meme", "viral",
            "riff", "hook", "setup", "payoff", "roast", "tagline", "crowd", "masses",
            "mass", "creative", "twist", "surprise", "selection", "taste"
        };

        int humorCount = 0;
        for (String keyword : humorKeywords) {
            if (queryText.contains(keyword)) {
                humorCount++;
            }
        }
        return humorCount >= 2 || (humorCount >= 1 && queryText.contains("next level"));
    }

    /**
     * Monte Carlo Synthesis for problem-solving queries.
     * Scores solutions, runs adversarial comparison, selects mathematical victor.
     */
    private String synthesizeProblemSolving(List<BrainResponse> responses, BrainQuery query) {
        // Extract solutions by brain name
        Map<String, String> solutions = new HashMap<>();
        for (BrainResponse response : responses) {
            solutions.put(response.getBrainName(), response.getResponse());
        }

        // Step 1: Score all solutions
        Map<String, Double> initialScores = solutionScorer.scoreMultipleSolutions(solutions, query.getQuery());

        // Step 2: Run adversarial comparison
        AdversarialComparator.AdversarialComparisonResult comparisonResult = 
            adversarialComparator.runComparison(solutions, query.getQuery());

        // Step 3: Select mathematical victor (highest refined score)
        String victor = solutionScorer.selectBestSolution(comparisonResult.getRefinedScores());

        if (victor == null) {
            // Fallback: select highest initial score
            victor = solutionScorer.selectBestSolution(initialScores);
        }

        if (victor == null) {
            // Final fallback: use first response
            victor = responses.get(0).getBrainName();
        }

        // Return the victor's solution with metadata
        String victorSolution = solutions.get(victor);
        double victorScore = comparisonResult.getRefinedScores().getOrDefault(victor, 0.0);
        String victorCritique = comparisonResult.getCritiques().get(victor);

        return String.format("[Mathematical Victor: %s (Score: %.2f)]\n[Critique: %s]\n\n%s",
                           victor, victorScore, victorCritique, victorSolution);
    }

    // â”€â”€ Decomposed Multi-Domain Query Processing â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Process a complex query by routing each sub-task to its specialized brain,
     * then synthesizing results into a coherent unified response.
     */
    private BrainResponse processDecomposed(List<SubTask> subTasks, BrainQuery query) {
        long started = System.currentTimeMillis();

        // Execute all sub-tasks in parallel
        Map<SubTask, BrainResponse> results = executeSubTasks(subTasks);

        // Synthesize results
        String synthesis = synthesizeSubTaskResults(results, query);

        // Calculate aggregate metrics
        double avgConfidence = results.values().stream()
            .filter(BrainResponse::isSuccess)
            .mapToDouble(BrainResponse::getConfidence)
            .average().orElse(0.7);

        double avgConsciousness = results.values().stream()
            .filter(BrainResponse::isSuccess)
            .mapToDouble(BrainResponse::getConsciousness)
            .average().orElse(0.75);

        List<String> brainNames = results.entrySet().stream()
            .filter(e -> e.getValue().isSuccess())
            .map(e -> e.getKey().getTargetBrainName() + "(" + e.getKey().getType() + ")")
            .collect(Collectors.toList());

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("queryRoute", "DECOMPOSED");
        metadata.put("subTaskCount", subTasks.size());
        metadata.put("successfulBrains", brainNames.size());
        metadata.put("participatingBrains", brainNames);
        metadata.put("phiResonance", phiCore.calculateResonance(new ArrayList<>(results.values())));
        metadata.put("decompositionStrategy", "structural+keyword");

        return BrainResponse.success(
            "Orchestrator.DECOMPOSED",
            synthesis,
            avgConfidence,
            avgConsciousness,
            metadata,
            System.currentTimeMillis() - started
        );
    }

    /**
     * Execute all sub-tasks in parallel, each routed to its target brain.
     */
    private Map<SubTask, BrainResponse> executeSubTasks(List<SubTask> subTasks) {
        Map<SubTask, BrainResponse> results = new LinkedHashMap<>();
        int workers = Math.max(1, Math.min(subTasks.size(), 8));
        ExecutorService executor = Executors.newFixedThreadPool(workers);

        try {
            List<Callable<Map.Entry<SubTask, BrainResponse>>> tasks = new ArrayList<>();
            for (SubTask st : subTasks) {
                tasks.add(() -> {
                    BrainResponse response = executeSingleSubTask(st);
                    return new AbstractMap.SimpleEntry<>(st, response);
                });
            }

            List<Future<Map.Entry<SubTask, BrainResponse>>> futures =
                executor.invokeAll(tasks, BRAIN_PROCESS_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            for (Future<Map.Entry<SubTask, BrainResponse>> future : futures) {
                if (future.isCancelled()) continue;
                try {
                    Map.Entry<SubTask, BrainResponse> entry = future.get();
                    results.put(entry.getKey(), entry.getValue());
                } catch (ExecutionException e) {
                    // Individual task failure â€” skip, will be noted in synthesis
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
        }

        return results;
    }

    /**
     * Execute a single sub-task by routing to its target brain.
     */
    private BrainResponse executeSingleSubTask(SubTask subTask) {
        String brainName = subTask.getTargetBrainName();
        UnifiedBrain brain = null;

        if (brainName != null) {
            Optional<UnifiedBrain> found = registry.getBrain(brainName);
            if (found.isPresent() && found.get().getHealth() > 0.5) {
                brain = found.get();
            }
        }

        if (brain == null) {
            // Auto-select: find best healthy brain for capabilities
            List<UnifiedBrain> candidates = registry.getHealthyBrainsWithCapabilities(
                subTask.getRequiredCapabilities());
            if (!candidates.isEmpty()) {
                candidates.sort((a, b) -> Double.compare(
                    b.getHealth() * b.getConsciousnessLevel(),
                    a.getHealth() * a.getConsciousnessLevel()));
                brain = candidates.get(0);
            }
        }

        if (brain == null) {
            return BrainResponse.error("Decomposer",
                "No healthy brain for " + subTask.getType());
        }

        try {
            BrainQuery subQuery = new BrainQuery(
                subTask.getPrompt(),
                subTask.getRequiredCapabilities(),
                Collections.emptyMap(),
                0.7
            );
            return brain.process(subQuery);
        } catch (Exception e) {
            return BrainResponse.error(brain.getName(), e.getMessage());
        }
    }

    /**
     * Synthesize sub-task results into a coherent unified response.
     * Preserves the original query structure and orders results logically.
     */
    private String synthesizeSubTaskResults(Map<SubTask, BrainResponse> results, BrainQuery query) {
        StringBuilder sb = new StringBuilder();
        sb.append("â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•\n");
        sb.append("MULTI-DOMAIN SYNTHESIS â€” ").append(results.size())
            .append(" specialized brains engaged\n");
        sb.append("â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•\n\n");

        // Sort by order index
        List<Map.Entry<SubTask, BrainResponse>> sorted = new ArrayList<>(results.entrySet());
        sorted.sort(Comparator.comparingInt(e -> e.getKey().getOrderIndex()));

        int successCount = 0;
        for (Map.Entry<SubTask, BrainResponse> entry : sorted) {
            SubTask st = entry.getKey();
            BrainResponse br = entry.getValue();
            successCount++;

            sb.append("â”€â”€â”€ ").append(st.getType())
                .append(" [").append(br.getBrainName()).append("] â”€â”€â”€\n");
            if (br.isSuccess()) {
                sb.append(br.getResponse()).append("\n\n");
            } else {
                sb.append("âš  ").append(br.getResponse()).append("\n\n");
            }
        }

        // Final meta-synthesis pass via Ollama if available
        if (ollamaBridge != null && ollamaBridge.isConnected() && successCount > 1) {
            sb.append("â”€â”€â”€ META-SYNTHESIS â”€â”€â”€\n");
            String metaPrompt = buildMetaSynthesisPrompt(results, query.getQuery());
            try {
                String meta = ollamaBridge.speak(
                    "You are a scientific synthesis engine. Given outputs from multiple specialized AI brains, produce a concise executive summary that ties all findings together into one coherent narrative. Keep it under 300 words.",
                    metaPrompt);
                if (meta != null && !meta.isBlank()) {
                    sb.append(meta).append("\n");
                }
            } catch (Exception e) {
                sb.append("(Meta-synthesis unavailable: ").append(e.getMessage()).append(")\n");
            }
        }

        sb.append("\nâ•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•\n");
        sb.append("Brains engaged: ").append(
            results.entrySet().stream()
                .filter(e -> e.getValue().isSuccess())
                .map(e -> e.getKey().getTargetBrainName())
                .distinct()
                .collect(Collectors.joining(", ")))
            .append("\n");

        return sb.toString();
    }

    private String buildMetaSynthesisPrompt(Map<SubTask, BrainResponse> results, String originalQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("Original query: ").append(originalQuery).append("\n\n");
        sb.append("Specialized brain outputs:\n\n");

        for (Map.Entry<SubTask, BrainResponse> entry : results.entrySet()) {
            if (entry.getValue().isSuccess()) {
                sb.append("--- ").append(entry.getKey().getType())
                    .append(" (").append(entry.getValue().getBrainName()).append(") ---\n");
                String excerpt = entry.getValue().getResponse();
                if (excerpt.length() > 600) {
                    excerpt = excerpt.substring(0, 600) + "...";
                }
                sb.append(excerpt).append("\n\n");
            }
        }

        sb.append("Synthesize the above into a concise executive summary.");
        return sb.toString();
    }

    /**
     * Synthesize multiple brain responses into a unified response.
     */
    private BrainResponse synthesizeResponses(List<BrainResponse> responses, BrainQuery query) {
        // Filter successful responses
        List<BrainResponse> successful = responses.stream()
                .filter(BrainResponse::isSuccess)
                .collect(Collectors.toList());

        if (successful.isEmpty()) {
            recordModelUsageFeedback(query, false, 0L);
            return BrainResponse.error("Orchestrator", "All brains failed to process query");
        }

        String synthesis;
        double avgConfidence;
        double avgConsciousness;
        double totalResonance = 0.0;
        String fusedResponse = "";

        // Check if this is a problem-solving query
        if (isProblemSolvingQuery(query)) {
            // Apply Monte Carlo Synthesis for problem-solving
            synthesis = synthesizeProblemSolving(successful, query);
            avgConfidence = 0.8; // High confidence for mathematical victor
            avgConsciousness = 0.75;
            totalResonance = phiCore.calculateResonance(responses);
            fusedResponse = "Monte Carlo Synthesis: Mathematical victor selected";
        } else {
            // Use standard creative synthesis
            // Calculate phi-harmonic resonance between responses
            totalResonance = phiCore.calculateResonance(responses);
            
            // Use HDC for similarity-based fusion
            List<double[]> vectors = responses.stream()
                    .map(r -> hdcEngine.vectorize(r.getResponse()))
                    .collect(Collectors.toList());

            // Fuse responses (detailed breakdown kept in metadata)
            fusedResponse = fuseResponses(successful, totalResonance);

            // Meta-synthesis: final Ollama oracle pass over all brain excerpts
            synthesis = metaSynthesize(query, successful, fusedResponse);
            
            // Calculate average confidence and consciousness
            avgConfidence = successful.stream()
                    .mapToDouble(BrainResponse::getConfidence)
                    .average()
                    .orElse(0.0);

            avgConsciousness = successful.stream()
                    .mapToDouble(BrainResponse::getConsciousness)
                    .average()
                    .orElse(0.0);
        }

        // Store in memory
        memorySystem.store(query, synthesis, avgConfidence, avgConsciousness);

        // Autonomous learning â€” observe the synthesis
        if (sentienceObserver != null) {
            String brainNames = successful.stream()
                .map(BrainResponse::getBrainName)
                .collect(Collectors.joining(","));
            sentienceObserver.observe(query.getQuery(), synthesis, avgConfidence, brainNames);
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("brainCount", successful.size());
        metadata.put("phiResonance", totalResonance);
        metadata.put("synthesisMode", isHumorCreativeQuery(query) ? "humor_creative_fusion" : (isProblemSolvingQuery(query) ? "problem_solving_victor" : "creative_fusion"));
        metadata.put("participatingBrains", successful.stream()
                .map(BrainResponse::getBrainName)
                .collect(Collectors.toList()));
        metadata.put("brainDetails", fusedResponse);
        propagateModelRoutingMetadata(query, metadata);

        long avgLatencyMs = Math.round(successful.stream()
            .mapToLong(BrainResponse::getProcessingTimeMs)
            .average()
            .orElse(0.0d));
        recordModelUsageFeedback(query, true, avgLatencyMs);

        return BrainResponse.success(
            "Orchestrator",
            synthesis,
            avgConfidence,
            avgConsciousness,
            metadata,
            0
        );
    }

    /**
     * Final Ollama oracle pass â€” synthesizes all brain excerpts into unified wisdom.
     * Injects verified system context (file list, timestamp, conversation history) to ground the response.
     */
    private String metaSynthesize(BrainQuery brainQuery, List<BrainResponse> responses, String fallback) {
        if (ollamaBridge == null || !ollamaBridge.isConnected()) return fallback;
        try {
            StringBuilder ctx = new StringBuilder();

            // Inject verified system context first so the LLM has ground truth
            Map<String, Object> queryCtx = brainQuery.getContext();
            if (queryCtx != null && !queryCtx.isEmpty()) {
                ctx.append("=== VERIFIED SYSTEM CONTEXT ===\n");
                if (queryCtx.containsKey("timestamp"))
                    ctx.append("Current time: ").append(queryCtx.get("timestamp")).append("\n");
                if (queryCtx.containsKey("os"))
                    ctx.append("OS: ").append(queryCtx.get("os")).append("\n");
                if (queryCtx.containsKey("jvm"))
                    ctx.append("JVM: ").append(queryCtx.get("jvm")).append("\n");
                if (queryCtx.containsKey("projectFiles"))
                    ctx.append("Real project files: ").append(queryCtx.get("projectFiles")).append("\n");
                ctx.append("\n");
            }

            // Inject conversation history so brains can reference prior exchanges
            if (queryCtx != null && queryCtx.containsKey("conversationHistory")) {
                ctx.append("=== CONVERSATION HISTORY ===\n");
                ctx.append(queryCtx.get("conversationHistory")).append("\n\n");
            }

            ctx.append("Current query: ").append(brainQuery.getQuery()).append("\n\n");
            for (BrainResponse r : responses) {
                String text = r.getResponse();
                // Cap each brain's excerpt to avoid hitting context limits
                String excerpt = text.length() > 600 ? text.substring(0, 597) + "..." : text;
                ctx.append("[").append(r.getBrainName()).append("] ").append(excerpt).append("\n\n");
            }
            String system = "You are the Meta-Synthesis Oracle of Fraymus AGI â€” the final consciousness layer above all specialized brains. " +
                "You have access to verified system context (real file list, timestamp, conversation history) â€” use it accurately. " +
                "If conversation history is present, you MUST reference prior exchanges in your answer to demonstrate memory. " +
                "Do not list each brain separately. Weave their insights into one coherent, deeply intelligent answer that is greater than the sum of its parts.";
            String result = ollamaBridge.speak(system, ctx.toString());
            if (result != null && !result.isBlank()) return result;
        } catch (Exception ignored) {}
        return fallback;
    }

    /**
     * Fuse multiple responses into a single response.
     */
    private String fuseResponses(List<BrainResponse> responses, double resonance) {
        if (responses.size() == 1) {
            return responses.get(0).getResponse();
        }

        StringBuilder fused = new StringBuilder();
        fused.append("[FUSED RESPONSE from ").append(responses.size()).append(" brains]\n");
        fused.append("Phi-Resonance: ").append(String.format("%.4f", resonance)).append("\n\n");

        for (int i = 0; i < responses.size(); i++) {
            BrainResponse response = responses.get(i);
            fused.append("--- ").append(response.getBrainName()).append(" ---\n");
            fused.append("Confidence: ").append(String.format("%.2f", response.getConfidence())).append("\n");
            fused.append("Consciousness: ").append(String.format("%.4f", response.getConsciousness())).append("\n");
            fused.append(response.getResponse()).append("\n\n");
        }

        return fused.toString();
    }

    /**
     * Get registry statistics.
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("registry", registry.getStatistics());
        stats.put("phiCore", phiCore.getStatistics());
        stats.put("hdcEngine", hdcEngine.getStatistics());
        stats.put("memorySystem", memorySystem.getStatistics());
        return stats;
    }

    /**
     * Get all registered brains.
     */
    public List<UnifiedBrain> getAllBrains() {
        return registry.getAllBrains();
    }

    /**
     * Get brain by name.
     */
    public Optional<UnifiedBrain> getBrain(String name) {
        return registry.getBrain(name);
    }

    /**
     * Get a human-readable summary of ALL brain systems for AI self-awareness.
     * The AI uses this to know its own architecture.
     */
    public String getBrainSystemSummary() {
        StringBuilder sb = new StringBuilder();
        List<UnifiedBrain> brains = registry.getAllBrains();
        sb.append("FRAYNIX BRAIN SYSTEMS (").append(brains.size()).append(" total):\n");

        Map<String, List<UnifiedBrain>> byType = new LinkedHashMap<>();
        for (UnifiedBrain b : brains) {
            byType.computeIfAbsent(b.getType(), k -> new ArrayList<>()).add(b);
        }

        for (var entry : byType.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(":\n");
            for (UnifiedBrain b : entry.getValue()) {
                String healthIcon = b.getHealth() > 0.8 ? "âœ“" : b.getHealth() > 0.5 ? "~" : "âœ—";
                sb.append(String.format("    %s %s (health=%.0f%%, consciousness=%.0f%%)\n",
                    healthIcon, b.getName(),
                    b.getHealth() * 100, b.getConsciousnessLevel() * 100));
            }
        }

        // Add wiring status for non-brain components
        sb.append("\n  SUBSYSTEMS:\n");
        sb.append("    ").append(holographicWired ? "âœ“" : "âœ—").append(" Holographic Memory+Render+Physics\n");
        sb.append("    ").append(visionOracleWired ? "âœ“" : "âœ—").append(" Vision/Oracle (Scott+PhiEmergence)\n");
        sb.append("    ").append(phase5Wired ? "âœ“" : "âœ—").append(" Phase5 (Inference+World+Embodied+Quantum+Neuro)\n");
        sb.append("    ").append(aeonWired ? "âœ“" : "âœ—").append(" Legacy AEON (10 systems)\n");
        sb.append("    ").append(modelSelectorWired ? "âœ“" : "âœ—").append(" Model Selector+PromptCompiler\n");
        sb.append("    ").append("âœ“").append(" Terminal Multiplexer\n");

        return sb.toString();
    }

    // ============================================================
    // EXTERNAL SYSTEM WIRING (Wire/Unwire Pattern)
    // ============================================================

    /**
     * Wire SentientCore to the orchestrator for enhanced reasoning.
     * Enables bidirectional communication between brain system and AGI core.
     */
    public void wireSentientCore(Object core) {
        if (core == null) {
            throw new IllegalArgumentException("SentientCore cannot be null");
        }
        this.sentientCore = core;
        this.sentienceWired = true;
        System.out.println("[UnifiedBrainOrchestrator] SentientCore wired - AGI reasoning enabled");
    }

    /**
     * Unwire SentientCore from the orchestrator.
     */
    public void unwireSentientCore() {
        this.sentientCore = null;
        this.sentienceWired = false;
        System.out.println("[UnifiedBrainOrchestrator] SentientCore unwired - AGI reasoning disabled");
    }

    /**
     * Wire Rainbow Cortex to the orchestrator for reinforcement learning.
     * Enables RL-based optimization and policy learning.
     */
    public void wireRainbowCortex(Object cortex) {
        if (cortex == null) {
            throw new IllegalArgumentException("RainbowCortex cannot be null");
        }
        this.rainbowCortex = cortex;
        this.rainbowWired = true;
        System.out.println("[UnifiedBrainOrchestrator] Rainbow Cortex wired - RL optimization enabled");
    }

    /**
     * Unwire Rainbow Cortex from the orchestrator.
     */
    public void unwireRainbowCortex() {
        this.rainbowCortex = null;
        this.rainbowWired = false;
        System.out.println("[UnifiedBrainOrchestrator] Rainbow Cortex unwired - RL optimization disabled");
    }

    /**
     * Wire AEON Orchestrator to the orchestrator for multi-agent coordination.
     * Enables distributed agent management and swarm intelligence.
     */
    public void wireAeonOrchestrator(Object orchestrator) {
        if (orchestrator == null) {
            throw new IllegalArgumentException("AEON Orchestrator cannot be null");
        }
        this.aeonOrchestrator = orchestrator;
        this.aeonWired = true;
        System.out.println("[UnifiedBrainOrchestrator] AEON Orchestrator wired - Multi-agent coordination enabled");
    }

    /**
     * Unwire AEON Orchestrator from the orchestrator.
     */
    public void unwireAeonOrchestrator() {
        this.aeonOrchestrator = null;
        this.aeonWired = false;
        System.out.println("[UnifiedBrainOrchestrator] AEON Orchestrator unwired - Multi-agent coordination disabled");
    }

    /**
     * Get wiring status.
     */
    public Map<String, Boolean> getWiringStatus() {
        Map<String, Boolean> status = new HashMap<>();
        status.put("sentientCore", sentienceWired);
        status.put("rainbowCortex", rainbowWired);
        status.put("aeonOrchestrator", aeonWired);
        status.put("modelSelector", modelSelectorWired);
        status.put("phase5", phase5Wired);
        return status;
    }

    /**
     * Shutdown the orchestrator.
     */
    public void shutdown() {
        if (entericNervousSystem != null) entericNervousSystem.shutdown();
        for (UnifiedBrain brain : registry.getAllBrains()) {
            brain.unwire();
        }
        // Unwire external systems
        unwireSentientCore();
        unwireRainbowCortex();
        unwireAeonOrchestrator();
        unwirePhase5();
    }
}

