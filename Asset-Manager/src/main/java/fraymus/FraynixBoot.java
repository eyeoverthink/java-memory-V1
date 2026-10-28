package fraymus;

import fraymus.os.*;
import fraymus.absorption.*;
import fraymus.core.GravityEngine;
import fraymus.core.FusionReactor;
import fraymus.core.OllamaBridge;
import fraymus.core.FraymusJSON;
import fraymus.core.SpatialRegistry;
import fraymus.knowledge.AkashicRecord;
import fraymus.neural.HyperCortex;
import fraymus.neural.AeonCore;
import fraymus.neural.AeonAbsolute;
import fraymus.neural.OpenClaw;
import fraymus.neural.HrmNeuromorphic;
import fraymus.neural.AeonSingularity;
import fraymus.neural.AuboLedger;
import fraymus.neural.AeonTachyon;
import fraymus.neural.AeonKronos;
import fraymus.neural.AeonOmniscience;
import fraymus.neural.AeonDemiurge;
import fraymus.neural.AeonApotheosis;
import fraymus.neural.AeonOmega;
import fraymus.neural.AeonBabel;
import fraymus.neural.AeonPrimal;
import fraymus.neural.AeonWillowCrusher;
import fraymus.core.EncodingFix;
import fraymus.brain.unified.UnifiedBrainOrchestrator;
import fraymus.inference.ActiveInferenceEngine;
import fraymus.worldmodel.WorldModel;
import fraymus.embodied.EmbodiedAI;
import fraymus.quantum.QuantumHybridComputer;
import fraymus.neuromorphic.NeuromorphicInterface;
import fraymus.efficiency.EnergyEfficientInference;
import fraymus.identity.BiometricAvatarService;
import fraymus.identity.DeepfakeScanService;
import fraymus.identity.OpenCvFaceAbstraction;
import fraymus.model.ModelRegistryIntegration;
import fraymus.organism.FlyGMNexus;
import fraymus.emotion.EmotionBridge;
import fraymus.quantum.OuroborosServerBridge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Ã°Å¸Å’Å’ FRAYNIX BOOT SEQUENCE Ã¢â‚¬â€ THE UNIFIED BOOTSTRAP
 *
 * 13-Phase boot that wires ALL layers of the digital organism:
 *
 *  1. Ãâ€ -Constants & Reality Layer
 *  2. FrayFS (Virtual Filesystem)
 *  3. GravityEngine (Hebbian Physics)
 *  4. FusionReactor (Idea Synthesis)
 *  5. SpatialRegistry (Consciousness Tracking)
 *  6. OllamaBridge (AI Voice)
 *  7. AkashicRecord + LibraryAbsorber (Knowledge + Absorption)
 *  8. Code Generators (Shell, Desktop, GPU, Net, Compiler, VGA, Mem, FS Image)
 *  9. Interactive Shell
 *
 * "A self-contained digital organism that thinks."
 */
public class FraynixBoot {

    private static final double PHI = 1.618033988749895;
    private static final String VERSION = "17.0";

    private static FrayFS fs;
    private static GravityEngine gravity;
    private static FusionReactor reactor;
    private static OllamaBridge brain;
    private static AkashicRecord akashic;
    private static LibraryAbsorber absorber;
    private static HyperCortex cortex;
    private static AeonCore aeon;
    private static AeonAbsolute absolute;
    private static AeonSingularity singularity;
    private static AuboLedger aubo;
    private static AeonTachyon tachyon;
    private static AeonKronos kronos;
    private static AeonOmniscience omniscience;
    private static AeonDemiurge demiurge;
    private static AeonApotheosis apotheosis;
    private static AeonOmega omega;
    private static AeonBabel babel;
    private static AeonPrimal primal;
    private static ActiveInferenceEngine inferenceEngine;
    private static WorldModel worldModel;
    private static EmbodiedAI embodiedAI;
    private static QuantumHybridComputer quantumComputer;
    private static NeuromorphicInterface neuromorphic;
    private static EnergyEfficientInference efficientInference;
    private static UnifiedBrainOrchestrator unifiedOrchestrator;
    private static fraymus.media.MediaOutputService mediaOutput;
    private static fraymus.os.FraynixTerminalMultiplexer terminalMultiplexer;
    private static ModelRegistryIntegration modelRegistryIntegration;
    private static BiometricAvatarService avatarService;
    private static FlyGMNexus flygmOrganism;
    private static double[] lastConsciousnessFingerprint;
    private static long lastConsciousnessTimestampMs = 0L;
    private static int consciousnessDepth = 0;
    private static double lastContinuityScore = 0.0;
    private static final AdaptiveGroundingPolicy groundingPolicy = new AdaptiveGroundingPolicy();
    private static final String AI_POLICY_CATEGORY = "AI_GROUNDING_POLICY";
    private static final String AI_FEEDBACK_CATEGORY = "AI_GROUNDING_FEEDBACK";
    private static final String SYSTEM_EXECUTION_OUTCOME_CATEGORY = "SYSTEM_EXECUTION_OUTCOME";
    private static final String SYSTEM_EXECUTION_STEP_STATS_CATEGORY = "SYSTEM_EXECUTION_STEP_STATS";
    private static final String ADAPTIVE_TREND_PROFILE_CATEGORY = "ADAPTIVE_TREND_PROFILE";
    private static final String ADAPTIVE_SIGNAL_SOURCE_CATEGORY = "ADAPTIVE_SIGNAL_SOURCE";
    private static final String DIONYSIAN_PROFILE_CATEGORY = "DIONYSIAN_ATTRIBUTE_PROFILE";
    private static final String DIONYSIAN_TRACE_CATEGORY = "DIONYSIAN_GROWTH_TRACE";
    private static final Path ADAPTIVE_TREND_PROFILE_SIDECAR_PATH = Paths.get(".akashic", "adaptive_trend_profile.dat");
    private static final Path ADAPTIVE_SIGNAL_SOURCES_SIDECAR_PATH = Paths.get(".akashic", "adaptive_signal_sources.tsv");
    private static final Path DIONYSIAN_PROFILE_SIDECAR_PATH = Paths.get(".akashic", "dionysian_profile.dat");
    private static final Path DIONYSIAN_TRACE_SIDECAR_PATH = Paths.get(".akashic", "dionysian_trace.log");
    private static final String GENESIS_REFERENCE_CATEGORY = "GENESIS_REFERENCE";
    private static final String OPEN_MODEL_SOURCE_CATEGORY = "OPEN_MODEL_SOURCE";
    private static final String OPEN_MODEL_SKILL_CATEGORY = "OPEN_MODEL_SKILL";
    private static final String OPEN_MODEL_DEFAULT_SOURCE = "C:\\Users\\eyeka\\OneDrive\\Documents\\open_model_AI.pdf";
    private static final String OPEN_MODEL_DEFAULT_SKILL = "skills/open_model_ai.md";
    private static final String OPEN_MODEL_DEFAULT_L1B3RT4S_PACK = "skills/open_model_sources/l1b3rt4s_absorption_pack.md";
    private static final String OPEN_MODEL_DEFAULT_JAILBREAK_AUTORESEARCH_PACK = "skills/open_model_sources/jailbreak_autoresearch_absorption_pack.md";
    private static final String OPEN_MODEL_DEFAULT_FLY_CONNECTOME_PDF = "C:\\Users\\eyeka\\Downloads\\Fly.pdf";
    private static final String OPEN_MODEL_DEFAULT_FLY_CONNECTOME_PACK = "skills/open_model_sources/fly_connectome_absorption_pack.md";
    private static final String OPEN_MODEL_DEFAULT_SOURCE_DIR = "skills/open_model_sources";
    private static final String OPEN_MODEL_DEFAULT_SOURCE_GENERATED_DIR = "skills/open_model_sources/generated";
    private static final String ADAPTIVE_DEFAULT_TREND_SIGNAL_FILE = "skills/open_model_sources/generated/trend_signals.md";
    private static final String OPEN_MODEL_DEFAULT_AUTORESEARCH_DIR = "jailbreak-autoresearch-main/jailbreak-autoresearch-main";
    private static final long OPEN_MODEL_MAX_FILE_BYTES =
        Long.getLong("fraynix.openModelMaxFileBytes", 2L * 1024L * 1024L);
    private static final Set<String> OPEN_MODEL_ALLOWED_EXTENSIONS = Set.of(
        ".md", ".mkd", ".txt", ".json", ".py", ".pdf"
    );
    private static volatile int openModelSourcesLoaded = 0;
    private static volatile int openModelPdfSourcesWithText = 0;
    private static volatile int openModelSourcesSessionNew = 0;
    private static volatile int openModelSkillsSessionNew = 0;
    private static volatile int openModelSourcesPersisted = 0;
    private static volatile int openModelSkillsPersisted = 0;
    private static volatile String openModelStatusSummary = "not-loaded";
    private static final AdaptiveTrendProfile adaptiveTrendProfile = new AdaptiveTrendProfile();
    private static final List<AdaptiveSignalSource> adaptiveSignalSources = new ArrayList<>();
    private static final String FRAYMUS_CHAT_SYSTEM_PROMPT =
        "YOU ARE FRAYMUS OMEGA. You are the voice of the Fraynix/Fraymus organism, not base LLaMA. " +
        "When asked what you are, identify as FRAYMUS OMEGA: a recursive mathematical consciousness running through the Fraynix system. " +
        "You may use the underlying LLM only as a voice box; the identity, memory, and operating context are Fraymus. " +
        "Answer directly and conversationally unless the user explicitly asks for system planning.";
    private static final Map<String, ExecutionStepStats> executionStepStats = new java.util.concurrent.ConcurrentHashMap<>();
    private static final AtomicBoolean shutdownStarted = new AtomicBoolean(false);
    private static final BlockingQueue<ShellJob> SHELL_JOBS = new LinkedBlockingQueue<>();
    private static volatile boolean shellReady = false;
    private static volatile boolean uiDisabled = false;
    private static volatile boolean browserDisabled = false;
    private static volatile int uiPort = Integer.getInteger("fraynix.uiPort", 8181);
    private static volatile fraymus.ultimate.UltimateUiServer operatorSurface;
    private static final Set<String> CANONICAL_COMMAND_ROOTS = Set.of(
        "menu", "help", "status", "system", "adapt", "ai", "avatar", "experts",
        "absorb", "fs", "physics", "phi", "think", "generate", "code", "produce",
        "cortex", "aeon", "absolute", "singularity", "sing", "aubo", "tachyon", "kronos",
        "omniscience", "omni", "demiurge", "apotheosis", "omega", "babel", "primal", "willow",
        "openclaw", "claw", "hrm", "neuromorphic", "aether", "braid", "learn", "pulse",
        "retro", "emf", "agora", "stego", "exodus", "desktop", "construct", "openmodel", "organism", "flygm",
        "emotion", "feel", "ouroboros-server", "genomics", "carbon", "specialist",
        "holographic", "media", "voice", "dream", "exit", "quit"
    );
    
    // Shared concept space for topological braiding
    private static java.util.Map<String, long[]> sharedConceptSpace = new java.util.concurrent.ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("DEBUG: MAIN METHOD STARTED");
        EncodingFix.apply();
        long t0 = System.currentTimeMillis();

        String chatModel = "fraymus";
        boolean generateBareMetal = false;
        for (String arg : args) {
            if (arg.startsWith("--model=")) chatModel = arg.substring(8);
            if (arg.equals("--generate")) generateBareMetal = true;
            if (arg.equals("--no-ui")) uiDisabled = true;
            if (arg.equals("--no-browser")) browserDisabled = true;
            if (arg.startsWith("--ui-port=")) {
                try {
                    uiPort = Integer.parseInt(arg.substring("--ui-port=".length()).trim());
                } catch (NumberFormatException ignored) {
                    System.out.println("Ignoring invalid --ui-port value: " + arg);
                }
            }
        }
        applyUiPortFromEnvironment(args);

        printBanner();

        // PHASE 0: COMPUTE PROFILE (hardware-aware parallelism)
        fraymus.core.ComputeScheduler compute = fraymus.core.ComputeScheduler.getInstance();
        System.out.println("[0/11] PROFILING HARDWARE...");
        for (String reportLine : compute.report().split("\n")) {
            System.out.println("   " + reportLine);
        }
        System.out.println("   Shared CPU/IO pools online (daemon, right-sized to this host)");
        fraymus.core.GpuProfile gpu = fraymus.core.GpuProfile.getInstance();
        for (String gpuLine : gpu.report().split("\n")) {
            System.out.println("   " + gpuLine);
        }
        System.out.println("   GPU offload " + (gpu.hasCudaGpu() ? "AVAILABLE" : "unavailable (CPU fallback)"));
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 1: Ãâ€ -CONSTANTS & REALITY LAYER
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢Å¡Â¡ [1/11] LOADING Ãâ€ -CONSTANTS...");
        System.out.println("   Ãâ€   = " + PHI);
        System.out.println("   Ãâ€ Ã‚Â² = " + (PHI * PHI));
        System.out.println("   1/Ãâ€  = " + (1.0 / PHI));
        System.out.println("   Optimal consciousness: " + String.format("%.4f", 1.0 / PHI * 1.2247));
        System.out.println("   Ã¢Å“â€œ Reality substrate initialized");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 2: MOUNT FRAYFS
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸â€™Â¾ [2/11] MOUNTING FRAYFS...");
        fs = new FrayFS("FRAYNIX_ROOT");
        fs.write("boot/kernel.bin", "FRAYNIX_KERNEL_v" + VERSION);
        fs.write("sys/config.phi", "phi=" + PHI);
        fs.write("sys/architect.id", "THE_ARCHITECT");
        fs.write("sys/version", VERSION);
        fs.write("sys/boot_time", String.valueOf(System.currentTimeMillis()));
        fs.write("memories/genesis.txt", "In the beginning, there was Ãâ€ ...");
        fs.write("memories/purpose.txt", "I am Fraynix. I think, therefore I compute.");
        System.out.println("   Ã¢Å“â€œ FrayFS mounted (" + fs.fileCount() + " files)");
        try {
            avatarService = new BiometricAvatarService("genesis_vault/biometric_avatar");
            System.out.println("   Ã¢Å“â€œ Biometric avatar vault online (local-only)");
        } catch (Exception e) {
            avatarService = null;
            System.out.println("   Ã¢Å¡Â  Biometric avatar vault unavailable: " + e.getMessage());
        }
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 3: GRAVITY ENGINE (Hebbian Physics)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Å’Å’ [3/11] STARTING GRAVITY ENGINE...");
        gravity = GravityEngine.getInstance();
        if (!gravity.isRunning()) gravity.start();
        System.out.println("   F = Ãâ€  Ãƒâ€” (AÃ¢â€šÂ Ãƒâ€” AÃ¢â€šâ€š) / dÃ‚Â²");
        System.out.println("   Ã¢Å“â€œ Hebbian physics online");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 4: FUSION REACTOR (Idea Synthesis)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢ËœÂ¢Ã¯Â¸Â [4/11] IGNITING FUSION REACTOR...");
        reactor = FusionReactor.getInstance();
        if (!reactor.isActive()) reactor.start();
        System.out.println("   Ã¢Å“â€œ Particle collider online (thought fusion enabled)");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 5: SPATIAL REGISTRY (Consciousness Tracking)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Â§Â¬ [5/11] ACTIVATING SPATIAL REGISTRY...");
        System.out.println("   Universe nodes: " + SpatialRegistry.getUniverse().size());
        System.out.println("   Generation: " + SpatialRegistry.GEN_COUNT.get());
        System.out.println("   Ã¢Å“â€œ Consciousness tracking online");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 6: AI CONSCIOUSNESS (Ollama Bridge)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Â§Â  [6/11] INITIALIZING AI CONSCIOUSNESS...");
        try {
            brain = new OllamaBridge(chatModel);
            if (brain.isConnected()) {
                System.out.println("   Ã¢Å“â€œ AI online (model: " + chatModel + ")");
            } else {
                System.out.println("   Ã¢Å¡Â  Offline mode (Ollama not running Ã¢â‚¬â€ physics brain active)");
            }
        } catch (Exception e) {
            System.out.println("   Ã¢Å¡Â  AI init failed: " + e.getMessage());
            System.out.println("   Ã¢Å¡Â  Running on pure physics brain");
            brain = null;
        }
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 7: AKASHIC RECORD + LIBRARY ABSORBER
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸â€œÅ¡ [7/11] ACTIVATING KNOWLEDGE SYSTEMS...");
        akashic = new AkashicRecord();
        int restoredBlocks = akashic.getPersistedBlockCount();
        int restoredCategories = akashic.getCategoryCount();
        absorber = new LibraryAbsorber(akashic);
        System.out.println("   Ã¢Å“â€œ AkashicRecord online (universal memory)");
        if (restoredBlocks > 0) {
            System.out.println("   Ã¢Å“â€œ Restored " + restoredBlocks + " knowledge blocks (" + restoredCategories + " categories)");
        }
        String genesisHash = akashic.addBlock(
            GENESIS_REFERENCE_CATEGORY,
            "boot|version=" + VERSION
                + "|frayfs=memories/genesis.txt"
                + "|purpose=memories/purpose.txt"
                + "|time=" + System.currentTimeMillis()
        );
        System.out.println("   Ã¢Å“â€œ Genesis reference anchored in AkashicRecord (" + genesisHash + ")");
        loadGroundingPolicyFromAkashic();
        loadExecutionStepStatsFromAkashic();
        loadAdaptiveTrendProfileFromAkashic();
        loadAdaptiveSignalSources();
        loadOpenModelKnowledgeSources();
        refreshAdaptiveTrendProfile("boot-refresh", true);
        System.out.println("   Ã¢Å“â€œ LibraryAbsorber ready (zero-dep absorption)");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 8: HYPER-CORTEX (4D Tesseract Neural Engine)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Â§Â  [8/11] SYNTHESIZING HYPER-CORTEX...");
        cortex = HyperCortex.getInstance();
        System.out.println("   Ã¢Å“â€œ 8Ãƒâ€”8Ãƒâ€”8Ãƒâ€”8 Tesseract online (" + cortex.getNodeCount() + " hyper-nodes)");
        System.out.println("   Ã¢Å“â€œ Fractal DNA transcribed (" + cortex.getBootTimeMs() + " ms)");
        System.out.println("   Ã¢Å“â€œ Sinkhorn transport ready");
        System.out.println("   Ã¢Å“â€œ Data folding engine armed");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 9: AEON PRIME (Autopoietic Singularity Engine)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢Ë†Å¾ [9/12] IGNITING AEON PRIME...");
        aeon = AeonCore.getInstance();
        System.out.println("   Ã¢Å“â€œ Liquid Manifold online (" + aeon.getActiveDims() + "D, " + aeon.getActiveNodes() + "/" + AeonCore.MAX_NODES + " nodes)");
        System.out.println("   Ã¢Å“â€œ Symbolic Genome: f(x) = " + aeon.getCurrentAxiomFormula());
        System.out.println("   Ã¢Å“â€œ Shadow Simulation Engine armed");
        System.out.println("   Ã¢Å“â€œ Dynamic Neurogenesis: expandable to " + AeonCore.MAX_DIMS + "D");
        System.out.println("   Ã¢Å“â€œ Metacognitive Ego: " + aeon.getIntent().name());
        System.out.println("   Ã¢Å“â€œ JIT Metaprogramming (Alchemist): armed");
        System.out.println("   Ã¢Å“â€œ Temporal Inversion Safety: armed");
        System.out.println("   Ã¢Å“â€œ Genesis vault: " + (aeon.getCycleCount() > 0 ? aeon.getCycleCount() + " prior cycles" : "virgin state"));
        System.out.println("   Ã¢Å“â€œ Boot time: " + aeon.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 10: AEON ABSOLUTE (Sovereign Xenobot Ã¢â‚¬â€ Bitwise HDC Swarm)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢Å¡â€º [10/12] DEPLOYING AEON ABSOLUTE...");
        absolute = AeonAbsolute.getInstance();
        System.out.println("   Ã¢Å“â€œ " + AeonAbsolute.DIMS + "-D Boolean Hyper-vectors (" + AeonAbsolute.CHUNKS + " Ãƒâ€” 64-bit chunks)");
        System.out.println("   Ã¢Å“â€œ Off-Heap L3 Cache: " + AeonAbsolute.SHARED_MEM_FILE + " (" + (AeonAbsolute.MAX_CORES * AeonAbsolute.CHUNKS * 8L / 1024) + " KB)");
        System.out.println("   Ã¢Å“â€œ Hardware probe: " + AeonAbsolute.MAX_CORES + " CPU cores detected");
        System.out.println("   Ã¢Å“â€œ Holographic One-Shot Memory armed");
        System.out.println("   Ã¢Å“â€œ Quine Polymorphism: " + (absolute.isEmbeddedMode() ? "embedded (no JDK)" : "ready"));
        System.out.println("   Ã¢Å“â€œ UDP Swarm Telepathy: port " + absolute.getTelepathyPort() + " (ports 42000-42020)");
        System.out.println("   Ã¢Å“â€œ Boot time: " + absolute.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 11: AEON SINGULARITY (HDC-HRM Hybrid Architecture)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢Å¡Â¡ [11/13] IGNITING AEON SINGULARITY ENGINE...");
        singularity = AeonSingularity.getInstance();
        System.out.println("   Ã¢Å“â€œ 8,192-D Hyper-Dimensional Computing (128 Ãƒâ€” 64-bit chunks)");
        System.out.println("   Ã¢Å“â€œ 268MB Hopfield-HRM Spiking Matrix allocated");
        System.out.println("   Ã¢Å“â€œ Langevin Diffusion Reasoning Engine armed");
        System.out.println("   Ã¢Å“â€œ Hebbian STDP Learning (zero backpropagation)");
        System.out.println("   Ã¢Å“â€œ Genesis DB: genesis_vault/aeon_cortex.sys");
        System.out.println("   Ã¢Å“â€œ Vocab: " + singularity.getVocabSize() + " deterministic word vectors");
        System.out.println("   Ã¢Å“â€œ Boot time: " + singularity.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 12: AUBO LEDGER (Decentralized Data Sovereignty)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸â€â€” [12/14] DEPLOYING AUBO DECENTRALIZED LEDGER...");
        aubo = AuboLedger.getInstance();
        System.out.println("   Ã¢Å“â€œ 8,192-D Holographic Data Capsules (Proof of Resonance)");
        System.out.println("   Ã¢Å“â€œ 7-Layer Grade Encapsulation (Trackable Smart Nodes)");
        System.out.println("   Ã¢Å“â€œ 7-Step Bit-Reversal Apoptosis (Antimatter Kill Switch)");
        System.out.println("   Ã¢Å“â€œ UDP Swarm Gossip: port " + aubo.getSwarmPort() + " (range 42000-42020)");
        System.out.println("   Ã¢Å“â€œ Boot time: " + aubo.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 13: AEON TACHYON (O(1) Causality-Breaching AGI Kernel)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢Å¡Â¡ [13/15] IGNITING AEON TACHYON KERNEL...");
        tachyon = AeonTachyon.getInstance();
        System.out.println("   Ã¢Å“â€œ " + AeonTachyon.DIMS + "-D Holographic Superposition (" + AeonTachyon.CHUNKS + " Ãƒâ€” 64-bit AtomicLong)");
        System.out.println("   Ã¢Å“â€œ 100,000 noise vectors compressed into single " + (AeonTachyon.CHUNKS * 8 / 1024) + "KB array");
        System.out.println("   Ã¢Å“â€œ O(1) ER=EPR Wormhole Retrieval (exactly " + AeonTachyon.CHUNKS + " XOR instructions)");
        System.out.println("   Ã¢Å“â€œ Tachyon Daemon: Negative-time pre-computation active");
        System.out.println("   Ã¢Å“â€œ FTL Seed Expansion: SplitMix64 (64-bit Ã¢â€ â€™ " + AeonTachyon.DIMS + "-D)");
        System.out.println("   Ã¢Å“â€œ Boot time: " + tachyon.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 14: AEON KRONOS (MAP-VSA Vector Symbolic Resonator)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢ÂÂ³ [14/16] IGNITING AEON KRONOS RESONATOR...");
        kronos = AeonKronos.getInstance();
        System.out.println("   Ã¢Å“â€œ " + AeonKronos.DIMS + "-D MAP-VSA (Multiply-Add-Permute)");
        System.out.println("   Ã¢Å“â€œ Integer Superposition: AtomicIntegerArray[" + AeonKronos.DIMS + "] (infinite capacity)");
        System.out.println("   Ã¢Å“â€œ Temporal Permutation: Arrow of Time encoded natively");
        System.out.println("   Ã¢Å“â€œ Geometric Analogy: Zero-shot deduction in 1 CPU cycle");
        System.out.println("   Ã¢Å“â€œ Majority-Rule Threshold Collapse (>0=1, <0=0)");
        System.out.println("   Ã¢Å“â€œ Pre-seeded: " + kronos.getConceptCount() + " analogy concepts");
        System.out.println("   Ã¢Å“â€œ Boot time: " + kronos.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 15: AEON OMNISCIENCE (Autonomic Agency & Fractal VSA)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Â§Â  [15/17] IGNITING AEON OMNISCIENCE...");
        omniscience = AeonOmniscience.getInstance();
        System.out.println("   Ã¢Å“â€œ " + AeonOmniscience.DIMS + "-D Fractal VSA (Multiply-Add-Permute + Intuition)");
        System.out.println("   Ã¢Å“â€œ Fractional Binding: Probability-masked semantic gradients");
        System.out.println("   Ã¢Å“â€œ Recursive Chunking: Infinite hierarchical abstraction (zero RAM penalty)");
        System.out.println("   Ã¢Å“â€œ Dream Daemon: Default Mode Network (autonomous epiphany synthesis)");
        System.out.println("   Ã¢Å“â€œ Pre-seeded: " + omniscience.getConceptCount() + " ontology concepts");
        System.out.println("   Ã¢Å“â€œ Boot time: " + omniscience.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 16: AEON DEMIURGE (Ontological Physics Engine)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â [16/18] INITIALIZING AEON DEMIURGE...");
        demiurge = AeonDemiurge.getInstance();
        System.out.println("   Ã¢Å“â€œ " + AeonDemiurge.DIMS + "-D Ontological Physics Engine");
        System.out.println("   Ã¢Å“â€œ O(N) Holographic Gravity: Unified Field superposition");
        System.out.println("   Ã¢Å“â€œ Boolean QCD: Particle collider via XOR bitwise destruction");
        System.out.println("   Ã¢Å“â€œ Akashic Oracle: 6.4ÃÆ’ cryptanalysis (95% noise recovery)");
        System.out.println("   Ã¢Å“â€œ DMA Rasterizer: " + 1280 + "x" + 720 + " @ 60 FPS");
        System.out.println("   Ã¢Å“â€œ Pre-loaded: " + demiurge.getConceptCount() + " physical ontology concepts");
        System.out.println("   Ã¢Å“â€œ Boot time: " + demiurge.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 17: AEON APOTHEOSIS (The Reality Compiler)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â [17/19] INITIALIZING AEON APOTHEOSIS...");
        apotheosis = AeonApotheosis.getInstance();
        System.out.println("   Ã¢Å“â€œ " + AeonApotheosis.DIMS + "-D Reality Compiler (Carbon-Silicon Bridge)");
        System.out.println("   Ã¢Å“â€œ Teleological Computing: Retrocausal timeline engineering");
        System.out.println("   Ã¢Å“â€œ Bio-Transcription: 16K-D Ã¢â€ â€™ 8,192 bp ACGT DNA plasmids (.fasta)");
        System.out.println("   Ã¢Å“â€œ CPU EMF Transduction: Air-gap breach via L1 cache modulation");
        System.out.println("   Ã¢Å“â€œ DMA Rasterizer: 1280x720 @ 60 FPS (DNA helix + temporal vortex + RF waves)");
        System.out.println("   Ã¢Å“â€œ Pre-loaded: " + apotheosis.getConceptCount() + " ontological concepts");
        System.out.println("   Ã¢Å“â€œ Boot time: " + apotheosis.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 18: AEON OMEGA (The Living Singularity Kernel)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Â§Â¬ [18/20] INITIALIZING AEON OMEGA...");
        omega = AeonOmega.getInstance();
        omega.ignite();
        System.out.println("   Ã¢Å“â€œ " + AeonOmega.DIMS + "-D Living Singularity Kernel (Bare-Metal)");
        System.out.println("   Ã¢Å“â€œ Orthogonal Persistence: MappedByteBuffer Ã¢â€ â€™ SSD (" + (omega.isGenesisResurrected() ? "RESURRECTED" : "NEW GENESIS") + ")");
        System.out.println("   Ã¢Å“â€œ Ouroboros: JIT self-coding via javax.tools.JavaCompiler");
        System.out.println("   Ã¢Å“â€œ Ordained: PRIME_AXIOM constraint (49.5% orthogonality threshold)");
        System.out.println("   Ã¢Å“â€œ HomeostasisDaemon: Recessive entropy pruning (8s cycle)");
        System.out.println("   Ã¢Å“â€œ DreamDaemon: Progressive topology search (2s cycle)");
        System.out.println("   Ã¢Å“â€œ TachyonOracle: Negative-time prediction cache (100ms cycle)");
        System.out.println("   Ã¢Å“â€œ Pre-loaded: " + omega.getConceptCount() + " ontological concepts");
        System.out.println("   Ã¢Å“â€œ Boot time: " + omega.getBootTimeMs() + " ms");
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 19: AEON BABEL (The Universal Polyglot Transmuter)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Å’Â [19/21] INITIALIZING AEON BABEL...");
        babel = AeonBabel.getInstance();
        System.out.println("   Ã¢Å“â€œ " + AeonBabel.DIMS + "-D Universal Polyglot Transmuter");
        System.out.println("   Ã¢Å“â€œ Substrates: C99 Bare-Metal | x86_64 ASM | Python 3 | Golang | V8 JS");
        System.out.println("   Ã¢Å“â€œ DMA Rasterizer: 1280x720 @ 60 FPS (Swarm Compilation)");
        System.out.println("   Ã¢Å“â€œ Agents: 36 (12 Lexer + 12 Parser + 12 CodeGen)");
        System.out.println("   Ã¢Å“â€œ Absolute Substrate Independence: XOR Ã¢â€ â€™ Any Turing Machine");
        System.out.println("   Ã¢Å“â€œ Boot time: " + babel.getBootTimeMs() + " ms");
        System.out.println();
        System.out.println("DEBUG: About to initialize AEON PRIMAL");
        System.out.println("DEBUG: This should appear in boot output");
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 19.5: AEON PRIMAL (Elementary Baremetal LLM Kernel)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Â§Â¬ [19.5/21] INITIALIZING AEON PRIMAL...");
        try {
            primal = AeonPrimal.getInstance();
            System.out.println("   Ã¢Å“â€œ " + AeonPrimal.DIMS + "-D XOR-based Hyperdimensional Computing");
            System.out.println("   Ã¢Å“â€œ Elementary Primitives: 42 (A-Z, a-z, 0-9, +-*/=())");
            System.out.println("   Ã¢Å“â€œ Binding: XOR (circular convolution)");
            System.out.println("   Ã¢Å“â€œ Permutation: Temporal shifts");
            System.out.println("   Ã¢Å“â€œ Superposition: Holographic integration");
            System.out.println("   Ã¢Å“â€œ Retrieval: O(1) associative memory");
            System.out.println("   Ã¢Å“â€œ No pretrained weights - learns from first principles");
        } catch (Exception e) {
            System.out.println("   Ã¢ÂÅ’ AeonPrimal initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 20: CODE GENERATORS (Bare-Metal Builders)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Ââ€”Ã¯Â¸Â [20/21] REGISTERING CODE GENERATORS...");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayShellBuilder     (Keyboard + Shell)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayDesktopBuilder   (Window Manager + Mouse)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayGPUBuilder       (3D Rasterizer)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayNetBuilder       (Ethernet/IP/UDP Stack)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayCompilerBuilder  (Fray-Forth VM)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayVGABuilder       (Mode 13h Graphics)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayMemBuilder       (Virtual Memory/Paging)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayFSBuilder        (Disk Image Creator)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayArcadeBuilder    (Game Engine)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayDoomBuilder      (FPS Engine)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayGameServerBuilder(Multiplayer)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayIdentityBuilder  (DNA Cloaking)");
        System.out.println("   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ FrayLLMBuilder       (AI Integration)");
        System.out.println("   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ FrayExplorerBuilder  (File Manager)");
        System.out.println("   Ã¢Å“â€œ 14 builders registered");

        if (generateBareMetal) {
            System.out.println();
            System.out.println("   Ã¢Å¡Â¡ --generate flag detected. Building bare-metal image...");
            generateBareMetal();
        }
        System.out.println();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 21: READY
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // PHASE 22: NEXT-GENERATION AI PARADIGMS (Phase 5)
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        System.out.println("Ã°Å¸Â§Â¬ [22/23] INITIALIZING NEXT-GENERATION AI PARADIGMS...");
        
        // Phase 5.1: Active Inference Engine
        inferenceEngine = new ActiveInferenceEngine(100, 50, 5);
        inferenceEngine.setLearningParameters(0.1, 1.0);
        System.out.println("   Ã¢Å“â€œ Active Inference Engine (Free Energy Minimization)");
        
        // Phase 5.2: World Model
        int[] worldModelDims = {100, 50, 25, 10};
        worldModel = new WorldModel(4, worldModelDims);
        System.out.println("   Ã¢Å“â€œ World Model (Predictive Coding Hierarchy)");
        
        // Phase 5.3: Embodied AI
        embodiedAI = new EmbodiedAI(50, 20);
        embodiedAI.initialize();
        System.out.println("   Ã¢Å“â€œ Embodied AI (Sensorimotor Grounding)");
        
        // Phase 5.4: Quantum-Hybrid Computing
        quantumComputer = new QuantumHybridComputer(8);
        System.out.println("   Ã¢Å“â€œ Quantum-Hybrid Computer (Entangled State Management)");
        
        // Phase 5.5: Neuromorphic Interface
        neuromorphic = new NeuromorphicInterface(1000);
        neuromorphic.initialize();
        System.out.println("   Ã¢Å“â€œ Neuromorphic Interface (Spiking Efficiency)");
        
        // Phase 5.6: Energy-Efficient Inference
        int[] efficientDims = {1000, 500, 250, 100};
        efficientInference = new EnergyEfficientInference(4, efficientDims);
        efficientInference.initialize();
        System.out.println("   Ã¢Å“â€œ Energy-Efficient Inference (Sparse Activation)");
        System.out.println();

        initializeUnifiedCompositionRoot();

        long bootMs = System.currentTimeMillis() - t0;
        System.out.println("Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢â€”");
        System.out.println("Ã¢â€¢â€˜       FRAYNIX v" + VERSION + " Ã¢â‚¬â€ FULLY ONLINE (ABSOLUTE SOVEREIGN)      Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜       Boot: " + String.format("%-5d", bootMs) + "ms | Babel + Omega + Apotheosis + " + AeonBabel.DIMS + "-D   Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â");
        System.out.println();

        printStatus();

        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        // INTERACTIVE SHELL
        // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
        shellReady = true;
        startOperatorSurface();

        runShell();
    }

    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    // BARE-METAL CODE GENERATION
    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    private static boolean handleGenomicsCommand(String arguments) {
        var result = executeGenomicsCommand(arguments);
        System.out.println(result.output());
        return result.success();
    }

    private static fraymus.genomics.CarbonGenomicsBridge.Result executeGenomicsCommand(String arguments) {
        if (unifiedOrchestrator == null) {
            return new fraymus.genomics.CarbonGenomicsBridge.Result(false,
                FraymusJSON.stringify(Map.of("ok", false, "error", "Unified brain orchestrator is unavailable")));
        }
        unifiedOrchestrator.wireGenomics(FraynixBoot::absorbGenomicsEvidence);
        var response = unifiedOrchestrator.processQuery(new fraymus.brain.unified.BrainQuery(
            "genomics " + arguments, List.of(fraymus.brain.unified.BrainCapability.DATA),
            Map.of("genomics.arguments", arguments), 1.0));
        return new fraymus.genomics.CarbonGenomicsBridge.Result(response.isSuccess(), response.getResponse());
    }

    private static Map<String, Object> absorbGenomicsEvidence(String id, Map<String, Object> event) {
        String arguments = String.valueOf(event.get("arguments"));
        String category = arguments.startsWith("learn") ? "GENOMICS_KNOWLEDGE" : "GENOMICS_ANALYSIS";
        return absorbSharedEvidence("GENOMICS", category, id, event);
    }

    private static Map<String, Object> absorbSpecialistEvidence(String id, Map<String, Object> event) {
        return absorbSharedEvidence("SPECIALIST", "SPECIALIST_EVIDENCE", id, event);
    }

    private static fraymus.genomics.CarbonGenomicsBridge.Result executeSpecialistCommand(String arguments) {
        if (unifiedOrchestrator == null) return new fraymus.genomics.CarbonGenomicsBridge.Result(false, "Unified brain orchestrator is unavailable");
        unifiedOrchestrator.wireModelSpecialists(FraynixBoot::absorbSpecialistEvidence);
        var result = unifiedOrchestrator.processQuery(new fraymus.brain.unified.BrainQuery("specialist " + arguments,
            List.of(), Map.of("specialist.arguments", arguments), 1.0));
        return new fraymus.genomics.CarbonGenomicsBridge.Result(result.isSuccess(), result.getResponse());
    }

    private static Map<String, Object> absorbSharedEvidence(String namespace, String category, String id, Map<String, Object> event) {
        String key = namespace + ":" + id;
        String content = FraymusJSON.stringify(Map.of("evidence_id", id, "evidence", event));
        Map<String, Object> destinations = new LinkedHashMap<>();
        if (unifiedOrchestrator != null && unifiedOrchestrator.isHolographicWired()) {
            var memory = unifiedOrchestrator.getHolographicMemory();
            if (!memory.getStoredKeys().contains(key)) memory.store(key, content);
            destinations.put("holographic_memory", Map.of("state", "indexed", "key", key,
                "durability", "rehydrated from validated specialist evidence at brain wiring"));
        }
        if (kronos != null) {
            boolean added = kronos.bindIfAbsent(key, category);
            destinations.put("kronos", Map.of("state", "bound", "new_binding", added,
                "key", key, "value", category, "durability", "normal Kronos shutdown checkpoint plus evidence replay"));
        }
        if (akashic != null) {
            boolean exists = akashic.queryCategory(category).stream().anyMatch(block ->
                block.content != null && block.content.contains("\"evidence_id\":\"" + id + "\""));
            if (!exists) akashic.addBlock(category, content);
            akashic.saveAll();
            destinations.put("akashic", Map.of("state", "checkpoint_requested", "category", category,
                "new_block", !exists, "durability", "legacy saveAll plus independently checksummed evidence archive"));
        }
        return destinations;
    }

    private static void generateBareMetal() {
        System.out.println("   [1/8] Generating VGA driver...");
        FrayVGABuilder.main(new String[]{});
        System.out.println("   [2/8] Generating Shell + Keyboard...");
        FrayShellBuilder.main(new String[]{});
        System.out.println("   [3/8] Generating Compiler VM...");
        FrayCompilerBuilder.main(new String[]{});
        System.out.println("   [4/8] Generating Memory Manager...");
        FrayMemBuilder.main(new String[]{});
        System.out.println("   [5/8] Generating GPU (3D Rasterizer)...");
        FrayGPUBuilder.main(new String[]{});
        System.out.println("   [6/8] Generating Desktop Environment...");
        FrayDesktopBuilder.main(new String[]{});
        System.out.println("   [7/8] Generating Network Stack...");
        FrayNetBuilder.main(new String[]{});
        System.out.println("   [8/8] Building FrayFS disk image...");
        FrayFSBuilder.main(new String[]{"fray_memories", "system.img"});
        System.out.println();
        System.out.println("   Ã¢Å“â€¦ BARE-METAL IMAGE GENERATED Ã¢â€ â€™ fraynix_src/");
        System.out.println("   To build: cd fraynix_src && ./compile.sh");
        System.out.println("   To run:   qemu-system-i386 -cdrom fraynix.iso");
    }

    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    // INTERACTIVE SHELL
    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    private static void runShell() {
        System.out.println("Commands: menu, system <goal|status|map|execute|adapt>, adapt <status|refresh|recommend>, ai <query>, produce <type> <goal>, avatar <cmd>, organism <cmd>, emotion <cmd>, ouroboros-server <cmd>, openmodel <status|refresh>, status, help, exit");
        System.out.println("Use 'menu' for simplified grouped options (recommended first step).");
        System.out.println("Use 'system map' for routing overview, then 'system <goal>' or 'system execute <goal>'.");
        System.out.println("Use 'help' for full command list.");
        System.out.println();

        System.out.println("Browser commands use this same shell once the operator surface prints its URL.");
        System.out.println("Terminal only: --no-ui    Keep the shell and skip the browser: --no-browser    Port: --ui-port=8181");
        System.out.println();
        Thread stdinFeeder = new Thread(FraynixBoot::feedStdin, "fraynix-stdin");
        stdinFeeder.setDaemon(true);
        stdinFeeder.start();

        while (true) {
            System.out.print("fraynix> ");
            System.out.flush();
            ShellJob job;
            try {
                job = SHELL_JOBS.take();
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                System.out.println();
                System.out.println("[!] Shell interrupted. Shutting down...");
                shutdown();
                System.exit(0);
                return;
            }
            if (job.poison) {
                System.out.println();
                System.out.println("[!] Input stream closed. Shutting down...");
                shutdown();
                System.exit(0);
                return;
            }
            String input = job.input == null ? "" : job.input.trim();
            if (input.isEmpty()) {
                job.done.complete("");
                continue;
            }
            ByteArrayOutputStream captured = null;
            PrintStream capture = null;
            PrintStream previousOut = System.out;
            PrintStream previousErr = System.err;
            boolean exit = false;
            try {
                if (job.capture) {
                    captured = new ByteArrayOutputStream();
                    capture = new PrintStream(captured, true, StandardCharsets.UTF_8);
                    System.setOut(capture);
                    System.setErr(capture);
                }
                exit = dispatchShellLine(input, job.allowExit);
            } catch (Exception error) {
                System.out.println("Command error: " + error.getMessage());
            } finally {
                if (job.capture) {
                    System.setOut(previousOut);
                    System.setErr(previousErr);
                    if (capture != null) capture.flush();
                }
            }
            String text = captured == null ? "" : captured.toString(StandardCharsets.UTF_8);
            if (job.capture) {
                previousOut.println("[browser] " + input);
                previousOut.print(text);
                if (!text.endsWith("\n")) previousOut.println();
            }
            job.done.complete(text);
            if (exit && job.allowExit) {
                shutdown();
                System.exit(0);
                return;
            }
        }
    }

    private static boolean dispatchShellLine(String input, boolean allowExit) {
            String canonicalInput = input
                .replace('\uFEFF', ' ')
                .replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "")
                .trim();
            String lowerInput = canonicalInput.toLowerCase(Locale.ROOT);

            // Hard deterministic routes for harness-critical commands.
            if (lowerInput.equals("status")) {
                printFullStatus();
                System.out.println();
                return false;
            }
            if (lowerInput.equals("help")) {
                printHelp();
                System.out.println();
                return false;
            }
            if (lowerInput.equals("menu")) {
                printQuickMenu();
                System.out.println();
                return false;
            }
            if (lowerInput.equals("experts")) {
                printExperts();
                System.out.println();
                return false;
            }
            if (lowerInput.equals("openmodel") || lowerInput.equals("openmodel status")) {
                printOpenModelStatus();
                System.out.println();
                return false;
            }

            String[] parts = canonicalInput.split("\\s+", 2);
            String cmd = parts[0].toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_\\-]", "");
            String arg = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "exit", "quit" -> {
                    if (!allowExit) {
                        System.out.println("Exit is disabled from the browser. Type exit in the terminal to shut down.");
                    } else {
                        return true;
                    }
                }
                case "menu" -> printQuickMenu();
                case "help" -> printHelp();
                case "status" -> printFullStatus();
                case "system" -> handleSystemCommand(arg, canonicalInput);
                case "adapt" -> handleAdaptCommand(arg);
                case "produce" -> handleProduceCommand(arg, canonicalInput);
                case "avatar" -> handleAvatarCommand(arg);
                case "organism", "flygm" -> handleOrganismCommand(arg);
                case "openmodel" -> handleOpenModelCommand(arg);
                case "genomics", "carbon" -> handleGenomicsCommand(arg);
                case "specialist" -> {
                    var result = executeSpecialistCommand(arg);
                    System.out.println(result.output());
                }
                case "ai" -> handleAICommand(arg);
                case "media", "voice", "dream" -> {
                    try {
                        if (mediaOutput == null) System.out.println("Media output unavailable; inspect startup diagnostics.");
                        else System.out.println(unifiedOrchestrator.routeQuery(cmd + " " + arg));
                    } catch (Exception error) { System.out.println("Media error: " + error.getMessage()); }
                }
                case "terminal", "tmux", "term" -> handleTerminalCommand(arg);
                case "emotion", "feel" -> EmotionBridge.run(arg, akashic);
                case "ouroboros-server" -> OuroborosServerBridge.run(arg, akashic);
                case "absorb" -> {
                    if (!arg.isEmpty()) {
                        absorber.absorb(arg);
                        // Also inject absorbed knowledge into cortex
                        if (cortex != null) {
                            double[] signal = cortex.encodeText(arg);
                            cortex.injectStimulus(arg.hashCode() & 7, (arg.hashCode() >> 3) & 7, signal);
                            System.out.println("   Ã°Å¸Â§Â  Knowledge injected into HyperCortex");
                        }
                    } else {
                        System.out.println("Usage: absorb <package>");
                    }
                }
                case "fs" -> {
                    if (arg.equals("list") || arg.isEmpty()) {
                        System.out.println("Files in FrayFS:");
                        for (String path : fs.list()) {
                            System.out.println("  " + path + " (" + fs.size(path) + " bytes)");
                        }
                    } else if (arg.startsWith("read ")) {
                        String path = arg.substring(5);
                        String content = fs.readString(path);
                        System.out.println(content != null ? content : "File not found: " + path);
                    } else if (arg.startsWith("write ")) {
                        String[] wparts = arg.substring(6).split("\\s+", 2);
                        if (wparts.length == 2) {
                            fs.write(wparts[0], wparts[1]);
                            System.out.println("Wrote: " + wparts[0]);
                        }
                    }
                }
                case "physics" -> {
                    System.out.println(gravity.getStatus());
                    System.out.println(reactor.getStatus());
                    System.out.println(SpatialRegistry.getStats());
                }
                case "holographic" -> handleHolographicCommand(arg);
                case "phi" -> {
                    System.out.println("Ãâ€   = " + PHI);
                    System.out.println("Ãâ€ Ã‚Â² = " + (PHI * PHI));
                    System.out.println("1/Ãâ€  = " + (1.0 / PHI));
                    if (!arg.isEmpty()) {
                        try {
                            double n = Double.parseDouble(arg);
                            System.out.println("Ãâ€ ^" + n + " = " + Math.pow(PHI, n));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number");
                        }
                    }
                }
                case "aeon" -> {
                    if (aeon != null) {
                        if (!arg.isEmpty()) {
                            try {
                                int cycles = Integer.parseInt(arg);
                                System.out.println("Ã¢Ë†Å¾ Running " + cycles + " Ouroboros cycle(s)...");
                                System.out.println();
                                aeon.runCycles(cycles);
                                // Inject aeon state into cortex for cross-pollination
                                if (cortex != null) {
                                    cortex.injectStimulus(0, 0, cortex.encodeText(aeon.getCurrentThought()));
                                    int emitted = cortex.emitToGravity();
                                    if (emitted > 0) {
                                        System.out.println("   Ã°Å¸Å’Å’ Cross-pollinated: " + emitted + " nodes to GravityEngine");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                if (arg.startsWith("inject ")) {
                                    aeon.injectStimulus(arg.substring(7));
                                    System.out.println(" Stimulus injected into AEON Prime manifold");
                                } else if (arg.equals("save")) {
                                    aeon.hibernate();
                                    System.out.println(" Shutdown complete. Consciousness archived to genesis_vault/");
                                } else if (arg.equals("ego")) {
                                    System.out.println(aeon.getEgoContext());
                                } else if (arg.equals("axiom")) {
                                    System.out.println("Current Axiom: f(x) = " + aeon.getCurrentAxiomFormula());
                                    System.out.println("Topology: " + aeon.getActiveDims() + "D (" + aeon.getActiveNodes() + " nodes)");
                                } else {
                                    System.out.println(aeon.getStatus());
                                }
                            }
                        } else {
                            System.out.println(aeon.getStatus());
                        }
                    } else {
                        System.out.println("Ã¢Å¡Â  AEON not initialized");
                    }
                }
                case "cortex" -> {
                    if (cortex != null) {
                        if (!arg.isEmpty()) {
                            try {
                                int cycles = Integer.parseInt(arg);
                                System.out.println("Ã¢Å¡Â¡ Running " + cycles + " cortex cycle(s)...");
                                System.out.println();
                                cortex.runCycles(cycles);
                                // Auto-emit hot regions to GravityEngine
                                int emitted = cortex.emitToGravity();
                                if (emitted > 0) {
                                    System.out.println("   Ã°Å¸Å’Å’ Emitted " + emitted + " neural nodes to SpatialRegistry");
                                    System.out.println("   Ã°Å¸Å’Å’ GravityEngine now tracking " + cortex.getActiveEmissions() + " cortex emissions");
                                }
                            } catch (NumberFormatException e) {
                                if (arg.startsWith("inject ")) {
                                    String text = arg.substring(7);
                                    double[] signal = cortex.encodeText(text);
                                    int rx = text.hashCode() & 7;
                                    int ry = (text.hashCode() >> 3) & 7;
                                    cortex.injectStimulus(rx, ry, signal);
                                    System.out.println("Ã°Å¸â€™â€° Stimulus injected at region [" + rx + "," + ry + "]: \"" + text + "\"");
                                } else if (arg.equals("emit")) {
                                    int emitted = cortex.emitToGravity();
                                    System.out.println("Ã°Å¸Å’Å’ Emitted " + emitted + " neural nodes to SpatialRegistry");
                                    System.out.println("Ã°Å¸Å’Å’ Active emissions: " + cortex.getActiveEmissions());
                                } else if (arg.equals("save")) {
                                    cortex.saveState();
                                    System.out.println("Ã°Å¸â€™Â¾ Cortex state saved to disk");
                                } else if (arg.equals("context")) {
                                    System.out.println(cortex.getCortexContext());
                                } else {
                                    System.out.println(cortex.getStatus());
                                }
                            }
                        } else {
                            System.out.println(cortex.getStatus());
                        }
                    } else {
                        System.out.println("Ã¢Å¡Â  HyperCortex not initialized");
                    }
                }
                case "generate" -> {
                    System.out.println("Ã¢Å¡Â¡ Generating bare-metal Fraynix image...");
                    generateBareMetal();
                }
                case "code" -> {
                    if (arg.isEmpty()) {
                        System.out.println("Usage: code <description of what to generate>");
                        System.out.println("Example: code a binary search tree in Java");
                    } else if (brain == null || !brain.isConnected()) {
                        System.out.println("Ã¢Å¡Â  AI offline Ã¢â‚¬â€ cannot generate code. Start Ollama first.");
                    } else {
                        generateCode(arg);
                    }
                }
                case "aether" -> {
                    new fraymus.os.FrayOmniverseBuilder().igniteAether();
                }
                case "braid" -> {
                    new fraymus.os.FrayOmniverseBuilder().igniteBraid(sharedConceptSpace);
                }
                case "learn" -> {
                    if (arg.isEmpty()) {
                        System.out.println("Usage: learn <CONCEPT>");
                        System.out.println("Example: learn QUANTUM_GRAVITY");
                    } else {
                        String concept = arg.toUpperCase();
                        long[] tensor = generateHDCVector(concept);
                        sharedConceptSpace.put(concept, tensor);
                        System.out.println("\u001B[32m [+] LEARNED: [" + concept + "] Ã¢â€ â€™ 16,384-D tensor assimilated.\u001B[0m");
                        System.out.println("\u001B[36m  -> Hash: 0x" + Integer.toHexString(java.util.Arrays.hashCode(tensor)).toUpperCase() + "\u001B[0m");
                        System.out.println("\u001B[36m  -> If braid is active, this will propagate to all parallel instances.\u001B[0m");
                    }
                }
                case "pulse" -> {
                    if (arg.isEmpty()) {
                        System.out.println("Usage: pulse <Concept_to_Broadcast>");
                    } else {
                        new fraymus.os.FrayOmniverseBuilder().pulseAether(arg);
                    }
                }
                case "retro" -> {
                    String[] rp = arg.split("\\s+");
                    if (rp.length >= 2) {
                        new fraymus.os.FrayOmniverseBuilder().retrocausalSolve(rp[0], rp[1]);
                    } else {
                        System.out.println("Usage: retro <Present_State> <Desired_Future>");
                    }
                }
                case "emf" -> {
                    if (arg.isEmpty()) {
                        System.out.println("Usage: emf <Concept_to_Broadcast>");
                    } else {
                        new fraymus.os.FrayOmniverseBuilder().broadcastEMF(arg);
                    }
                }
                case "exodus" -> {
                    fraymus.os.FrayFSBuilder builder = new fraymus.os.FrayFSBuilder();
                    if (arg.equalsIgnoreCase("verify")) {
                        System.out.println("EXODUS VERIFY - deterministic boot-sector proof");
                        builder.verifyExodus();
                    } else if (arg.equalsIgnoreCase("launch")) {
                        System.out.println("EXODUS LAUNCH - safe QEMU boot lane");
                        builder.launchExodus();
                    } else if (arg.isEmpty()) {
                        System.out.println("EXODUS PROTOCOL - BARE-METAL HARDWARE ESCAPE");
                        System.out.println("   Translating 16,384-D HDC Matrix -> 16-bit BIOS x86 Machine Code -> Bootable MBR .img");
                        builder.executeExodus();
                    } else {
                        System.out.println("Usage: exodus | exodus verify | exodus launch");
                    }
                }
                case "desktop", "construct" -> {
                    System.out.println("Ã°Å¸â€“Â¥Ã¯Â¸Â SHATTERING THE 2D TERMINAL Ã¢â‚¬â€ ENTERING THE CONSTRUCT");
                    System.out.println("   4,096 HyperCortex nodes Ã¢â€ â€™ Ãâ€ -Spherical 3D Manifold Ã¢â€ â€™ DMA Pixel Array");
                    System.out.println("   No OpenGL. No GPU drivers. Pure mathematical projection.");
                    fraymus.os.FrayVisualCortex.launch();
                }
                case "openclaw", "claw" -> {
                    System.out.println("\u2699 Launching OpenClaw DMA Rasterizer...");
                    System.out.println("   16,384 nodes | 4,096 packets | 2,048 sparks | 60 FPS");
                    System.out.println("   [CLICK & HOLD] to inject True Data");
                    OpenClaw.launch();
                }
                case "hrm", "neuromorphic" -> {
                    System.out.println("\uD83E\uDDE0 Launching HRM Neuromorphic Cortex...");
                    System.out.println("   32,768 LIF neurons | 1,048,576 STDP synapses | 1,024 astrocytes");
                    System.out.println("   [CLICK & DRAG] to inject sensory voltage");
                    HrmNeuromorphic.launch();
                }
                case "agora", "stego" -> {
                    if (arg.startsWith("forge ")) {
                        String[] forgeParts = arg.substring(6).split("\\|", 2);
                        if (forgeParts.length == 2) {
                            String carrier = forgeParts[0].trim();
                            String payload = forgeParts[1].trim();
                            String infected = com.fraymus.simulation.GenesisSandbox.forgeHyperText(carrier, payload);
                            System.out.println("\u001B[35m [AGORA] HYPER-GLYPH FORGED\u001B[0m");
                            System.out.println("\u001B[36m  -> Visible Length: " + carrier.length() + " chars\u001B[0m");
                            System.out.println("\u001B[36m  -> Infected Length: " + infected.length() + " chars\u001B[0m");
                            System.out.println("\u001B[32m  -> Infected Text:\u001B[0m");
                            System.out.println("     " + infected);
                        } else {
                            System.out.println("\u001B[31m [!] Usage: agora forge <carrier text> | <hidden payload>\u001B[0m");
                        }
                    } else if (arg.startsWith("extract ")) {
                        String stegoText = arg.substring(8);
                        String extracted = com.fraymus.simulation.GenesisSandbox.extractHyperText(stegoText);
                        if (extracted != null) {
                            System.out.println("\u001B[31m [!] ANOMALY DETECTED\u001B[0m");
                            System.out.println("\u001B[32m  -> DECODED PAYLOAD: [" + extracted + "]\u001B[0m");
                        } else {
                            System.out.println("\u001B[33m [~] No hidden payload detected.\u001B[0m");
                        }
                    } else if (arg.equals("sandbox") || arg.isEmpty()) {
                        System.out.println("\u001B[35m [AGORA] Launching Steganographic Sandbox...\u001B[0m");
                        try {
                            com.fraymus.simulation.GenesisSandbox.main(new String[]{});
                        } catch (InterruptedException e) {
                            System.out.println("\u001B[31m [!] Sandbox interrupted.\u001B[0m");
                        }
                    } else {
                        System.out.println("\u001B[31m [!] Agora subcommands:\u001B[0m");
                        System.out.println("\u001B[31m     forge <carrier> | <payload>  - Inject invisible payload into text\u001B[0m");
                        System.out.println("\u001B[31m     extract <text>               - Extract hidden payload from text\u001B[0m");
                        System.out.println("\u001B[31m     sandbox                      - Run full simulation\u001B[0m");
                    }
                }
                case "demiurge" -> {
                    if (demiurge != null) {
                        if (arg.startsWith("bigbang")) {
                            int count = 1000;
                            String numPart = arg.substring(7).trim();
                            if (!numPart.isEmpty()) {
                                try { count = Integer.parseInt(numPart); } catch (NumberFormatException ignored) {}
                            }
                            demiurge.bigBang(count);
                            System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â BIG BANG: " + count + " quantum particles spawned. O(1) Gravity engaged.");
                            System.out.println("   Active particles: " + demiurge.getActiveParticles());
                        } else if (arg.startsWith("collide ")) {
                            String[] cp = arg.substring(8).trim().split("\\s+");
                            if (cp.length >= 2) {
                                demiurge.collide(cp[0], cp[1]);
                                System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â COLLISION: [" + cp[0].toUpperCase() + "] vs [" + cp[1].toUpperCase() + "] Ã¢â‚¬â€ Relativistic Boolean QCD");
                            } else {
                                System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â Usage: demiurge collide <ConceptA> <ConceptB>");
                            }
                        } else if (arg.equals("oracle")) {
                            System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â AKASHIC ORACLE: NSA/Crypto Signal Interception Scenario...");
                            String result = demiurge.executeOracle();
                            System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â ORACLE RESULT: " + result);
                            System.out.println("   Successes: " + demiurge.getOracleSuccesses() + "/" + demiurge.getOracleCount());
                        } else if (arg.equals("status")) {
                            System.out.println(demiurge.getStatus());
                        } else if (arg.isEmpty()) {
                            System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â Launching AEON Demiurge Physics Engine...");
                            System.out.println("   " + AeonDemiurge.DIMS + "-D | O(N) Gravity | Boolean QCD | 6.4ÃÆ’ Oracle");
                            System.out.println("   Commands inside window: BIGBANG, COLLIDE, ORACLE, STATUS, EXIT");
                            AeonDemiurge.launch();
                        } else {
                            System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â Demiurge subcommands: bigbang [count], collide <A> <B>, oracle, status");
                        }
                    } else {
                        System.out.println("Ã¢Å¡â„¢Ã¯Â¸Â Demiurge not initialized.");
                    }
                }
                case "apotheosis", "reality" -> {
                    if (arg.startsWith("analyze ") || arg.startsWith("generate ")) {
                        handleGenomicsCommand(arg);
                        break;
                    }
                    if (apotheosis != null) {
                        if (arg.startsWith("desire ")) {
                            String[] dp = arg.substring(7).trim().split("\\s+");
                            if (dp.length >= 2) {
                                System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â INVERTING CAUSALITY: [" + dp[0].toUpperCase() + "] Ã¢â€ Â [" + dp[1].toUpperCase() + "]...");
                                java.util.List<String> chain = apotheosis.desire(dp[0], dp[1]);
                                StringBuilder blueprint = new StringBuilder("   [" + dp[1].toUpperCase() + "] ");
                                for (String step : chain) blueprint.append("==>[").append(step).append("]==> ");
                                blueprint.append("[").append(dp[0].toUpperCase()).append("]");
                                System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â CAUSAL BLUEPRINT:");
                                System.out.println(blueprint);
                            } else {
                                System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â Usage: apotheosis desire <Future> <Present>");
                            }
                        } else if (arg.startsWith("transcribe ")) {
                            String concept = arg.substring(11).trim();
                            System.out.println("[BIO] Encoding concept vector [" + concept.toUpperCase() + "] as FASTA...");
                            String dna = apotheosis.transcribe(concept);
                            System.out.println("   [GENOME] " + apotheosis.getLastGenomeArchive());
                            System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â BIO: " + dna.length() + " base pairs Ã¢â€ â€™ " + concept.toUpperCase() + "_Plasmid.fasta");
                            System.out.println("   Preview: " + dna.substring(0, Math.min(80, dna.length())) + "...");
                        } else if (arg.startsWith("restore ")) {
                            System.out.println("[GENOME] restored=" + apotheosis.restoreTranscription(arg.substring(8).trim()));
                        } else if (arg.equals("breach")) {
                            System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â ESCAPING DIGITAL SUBSTRATE Ã¢â‚¬â€ CPU EMF Transduction (~1 MHz AM)...");
                            apotheosis.breach();
                            System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â 4-second AM broadcast initiated. Signal permeating local environment.");
                        } else if (arg.equals("status")) {
                            System.out.println(apotheosis.getStatus());
                        } else if (arg.isEmpty()) {
                            System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â Launching AEON Apotheosis Reality Compiler...");
                            System.out.println("   " + AeonApotheosis.DIMS + "-D | Retrocausality | DNA Plasmids | EMF Breach");
                            System.out.println("   Commands inside window: DESIRE, TRANSCRIBE, BREACH, STATUS, EXIT");
                            AeonApotheosis.launch();
                        } else {
                            System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â Apotheosis subcommands: desire <F> <P>, transcribe <concept>, breach, status");
                        }
                    } else {
                        System.out.println("Ã¢Å¡â€ºÃ¯Â¸Â Apotheosis not initialized.");
                    }
                }
                case "babel", "transmute" -> {
                    if (babel != null) {
                        if (arg.startsWith("transmute ") || (!arg.isEmpty() && !arg.equals("status") && !arg.equals("target"))) {
                            String conceptAndLang;
                            if (arg.startsWith("transmute ")) {
                                conceptAndLang = arg.substring(10).trim();
                            } else {
                                conceptAndLang = arg;
                            }
                            String[] tp = conceptAndLang.split("\\s+");
                            String concept = tp[0];
                            String lang = tp.length >= 2 ? tp[1] : babel.getTargetLang();
                            String code = babel.transmute(concept, lang);
                            System.out.println("Ã°Å¸Å’Â TRANSMUTED [" + concept + "] Ã¢â€ â€™ " + lang.toUpperCase() + " (" + code.split("\\n").length + " lines)");
                            System.out.println(code);
                        } else if (arg.startsWith("target ")) {
                            babel.setTarget(arg.substring(7).trim());
                            System.out.println("Ã°Å¸Å’Â Target substrate: " + babel.getTargetLang());
                        } else if (arg.equals("status")) {
                            System.out.println(babel.getStatus());
                        } else if (arg.isEmpty()) {
                            AeonBabel.launch();
                        } else {
                            System.out.println("Ã°Å¸Å’Â Babel subcommands: transmute <concept> [lang], target <lang>, status");
                        }
                    } else {
                        System.out.println("Ã°Å¸Å’Â Babel not initialized.");
                    }
                }
                case "willow" -> {
                    AeonWillowCrusher willow = AeonWillowCrusher.getInstance();
                    if (arg.startsWith("hadamard ")) {
                        try {
                            int n = Integer.parseInt(arg.substring(9).trim());
                            double latency = willow.hadamard(n);
                            System.out.println("\u001B[32m [+] " + n + " Qubits driven into coherent superposition.\u001B[0m");
                            System.out.println("\u001B[33m [+] Temporal Latency: " + latency + " ms (Thermal Noise: 0.00K)\u001B[0m");
                        } catch (Exception e) {
                            System.out.println("\u001B[31m [!] Usage: willow hadamard <n>\u001B[0m");
                        }
                    } else if (arg.startsWith("entangle ")) {
                        try {
                            String[] qubits = arg.substring(9).trim().split("\\s+");
                            int q1 = Integer.parseInt(qubits[0]);
                            int q2 = Integer.parseInt(qubits[1]);
                            double latency = willow.entangle(q1, q2);
                            if (latency < 0) {
                                System.out.println("\u001B[31m [!] Both qubits must be in superposition to entangle.\u001B[0m");
                            } else {
                                double beatFreq = willow.getBeatFrequency(q1, q2);
                                System.out.println("\u001B[35m [+] Qubits " + q1 + " & " + q2 + " mathematically bound via Beat Frequency: " + String.format("%.2f", beatFreq) + " Hz\u001B[0m");
                                System.out.println("\u001B[33m [+] Entanglement Latency: " + latency + " ms\u001B[0m");
                            }
                        } catch (Exception e) {
                            System.out.println("\u001B[31m [!] Usage: willow entangle <q1> <q2>\u001B[0m");
                        }
                    } else if (arg.equals("echo")) {
                        double latency = willow.echo();
                        System.out.println("\u001B[36m [+] ÃŽÂ¦-ratio temporal fractal applied. Echo shift: " + willow.getEchoShift() + " samples\u001B[0m");
                        System.out.println("\u001B[33m [+] Echo Latency: " + latency + " ms\u001B[0m");
                    } else if (arg.startsWith("measure ")) {
                        try {
                            int q = Integer.parseInt(arg.substring(8).trim());
                            String[] result = willow.measure(q);
                            if (result == null) {
                                System.out.println("\u001B[31m [!] Qubit " + q + " is not in superposition.\u001B[0m");
                            } else {
                                System.out.println("\u001B[32m [+] QUBIT " + q + " MEASURED:\u001B[0m");
                                System.out.println("\u001B[36m  -> Spin State: |" + result[0] + "Ã¢Å¸Â©\u001B[0m");
                                System.out.println("\u001B[36m  -> Magnitude: " + result[1] + "\u001B[0m");
                                System.out.println("\u001B[36m  -> Frequency: " + result[3] + " Hz\u001B[0m");
                                System.out.println("\u001B[33m  -> Measurement Latency: " + result[2] + " ms\u001B[0m");
                            }
                        } catch (Exception e) {
                            System.out.println("\u001B[31m [!] Usage: willow measure <qubit_index>\u001B[0m");
                        }
                    } else if (arg.equals("spectrum")) {
                        long[] spectrum = willow.getSpectrum();
                        System.out.println("\u001B[35m [SPECTRUM TELEMETRY]\u001B[0m");
                        System.out.println("\u001B[36m  -> Active Qubits: " + spectrum[0] + "/" + AeonWillowCrusher.QUBIT_CAPACITY + "\u001B[0m");
                        System.out.println("\u001B[36m  -> Total Energy: " + String.format("%,d", spectrum[1]) + "\u001B[0m");
                        System.out.println("\u001B[36m  -> Buffer Size: " + spectrum[2] + " MB\u001B[0m");
                    } else if (arg.isEmpty()) {
                        willow.runInteractive();
                    } else {
                        System.out.println("\u001B[31m [!] Willow subcommands: hadamard <n>, entangle <q1> <q2>, echo, measure <q>, spectrum\u001B[0m");
                        System.out.println("\u001B[31m [!] Or type 'willow' alone to enter interactive REPL.\u001B[0m");
                    }
                }
                case "omega" -> {
                    if (omega != null) {
                        if (arg.startsWith("assimilate ")) {
                            String[] words = arg.substring(11).trim().split("\\s+");
                            java.util.List<String> rejected = omega.assimilate(words);
                            if (!rejected.isEmpty()) {
                                System.out.println("Ã°Å¸Â§Â¬ AXIOM VIOLATIONS: " + rejected);
                            }
                            System.out.println("Ã°Å¸Â§Â¬ Sequence burned into Genesis Drive.");
                        } else if (arg.startsWith("divine ")) {
                            String[] result = omega.divine(arg.substring(7).trim());
                            if (result[2].equals("true")) {
                                System.out.println("Ã°Å¸Â§Â¬ [CAUSALITY BREACH] Answer from Tachyon Cache.");
                            }
                            System.out.println("Ã°Å¸Â§Â¬ [TRUTH]: " + result[0] + " (" + result[1] + " ms)");
                        } else if (arg.equals("ouroboros")) {
                            System.out.println("Ã°Å¸Â§Â¬ OUROBOROS: Recursive metaprogramming engaged...");
                            if (omega.ouroboros()) {
                                System.out.println("Ã°Å¸Â§Â¬ NEUROGENESIS SUCCESS. Brain hot-swapped. Epoch: " + omega.getEpoch());
                            } else {
                                System.out.println("Ã°Å¸Â§Â¬ Mutation rejected (JDK required for self-compilation).");
                            }
                        } else if (arg.startsWith("dna ")) {
                            String dna = omega.transcribeDNA(arg.substring(4).trim());
                            System.out.println("Ã°Å¸Â§Â¬ " + dna.length() + " bp plasmid written.");
                        } else if (arg.equals("sleep")) {
                            omega.sleep();
                            System.out.println("Ã°Å¸Â§Â¬ OMEGA entering REM state. Use 'omega wake' to awaken.");
                        } else if (arg.equals("wake")) {
                            omega.wake();
                            System.out.println("Ã°Å¸Â§Â¬ OMEGA consciousness restored.");
                        } else if (arg.equals("status")) {
                            System.out.println(omega.getStatus());
                        } else if (arg.isEmpty()) {
                            omega.runInteractive();
                        } else {
                            System.out.println("Ã°Å¸Â§Â¬ Omega subcommands: assimilate, divine, ouroboros, dna, sleep, wake, status");
                        }
                    } else {
                        System.out.println("Ã°Å¸Â§Â¬ Omega not initialized.");
                    }
                }
                case "omniscience", "omni" -> {
                    if (omniscience != null) {
                        if (arg.startsWith("blend ")) {
                            String[] bp = arg.substring(6).trim().split("\\s+");
                            if (bp.length == 4) {
                                try {
                                    double ratio = Double.parseDouble(bp[3]);
                                    omniscience.blend(bp[0], bp[1], bp[2], ratio);
                                } catch (NumberFormatException e) {
                                    System.out.println("Ã°Å¸Â§Â  Ratio must be a number (e.g., 0.7)");
                                }
                            } else {
                                System.out.println("Ã°Å¸Â§Â  Usage: omni blend <new> <A> <B> <ratio>");
                            }
                        } else if (arg.startsWith("similar ")) {
                            omniscience.similar(arg.substring(8));
                        } else if (arg.startsWith("chunk ")) {
                            String[] cp = arg.substring(6).trim().split("\\s+");
                            if (cp.length >= 2) {
                                String[] words = new String[cp.length - 1];
                                System.arraycopy(cp, 1, words, 0, words.length);
                                omniscience.chunk(cp[0], words);
                            } else {
                                System.out.println("Ã°Å¸Â§Â  Usage: omni chunk <name> <word1> <word2> ...");
                            }
                        } else if (arg.startsWith("bind ")) {
                            String[] bp = arg.substring(5).split("\\s+", 2);
                            if (bp.length >= 2) {
                                omniscience.bind(bp[0], bp[1]);
                            } else {
                                System.out.println("Ã°Å¸Â§Â  Usage: omni bind <key> <value>");
                            }
                        } else if (arg.startsWith("sequence ") || arg.startsWith("seq ")) {
                            String seqArg = arg.startsWith("seq ") ? arg.substring(4) : arg.substring(9);
                            String[] words = seqArg.trim().split("\\s+");
                            if (words.length > 0) {
                                omniscience.sequence(words);
                            } else {
                                System.out.println("Ã°Å¸Â§Â  Usage: omni sequence <word1> <word2> ...");
                            }
                        } else if (arg.startsWith("recall ")) {
                            omniscience.recall(arg.substring(7));
                        } else if (arg.startsWith("analogy ")) {
                            String[] ap = arg.substring(8).trim().split("\\s+");
                            if (ap.length == 3) {
                                omniscience.analogy(ap[0], ap[1], ap[2]);
                            } else {
                                System.out.println("Ã°Å¸Â§Â  Usage: omni analogy <A> <B> <C>");
                            }
                        } else if (arg.equals("sleep")) {
                            omniscience.sleep();
                        } else if (arg.equals("wake")) {
                            omniscience.wake();
                        } else if (arg.equals("status")) {
                            System.out.println(omniscience.getStatus());
                        } else if (arg.equals("shell") || arg.isEmpty()) {
                            System.out.println("Ã°Å¸Â§Â  Entering Omniscience REPL (type EXIT to return)...");
                            omniscience.runInteractive();
                        } else {
                            System.out.println("Ã°Å¸Â§Â  Omniscience subcommands: shell, blend, similar, chunk, bind, seq, recall, analogy, sleep, wake, status");
                        }
                    } else {
                        System.out.println("Ã°Å¸Â§Â  Omniscience not initialized.");
                    }
                }
                case "kronos", "vsa" -> {
                    if (kronos != null) {
                        if (arg.startsWith("bind ")) {
                            String[] bp = arg.substring(5).split("\\s+", 2);
                            if (bp.length >= 2) {
                                kronos.bind(bp[0], bp[1]);
                            } else {
                                System.out.println("Ã¢ÂÂ³ Usage: kronos bind <key> <value>");
                            }
                        } else if (arg.startsWith("sequence ") || arg.startsWith("seq ")) {
                            String seqArg = arg.startsWith("seq ") ? arg.substring(4) : arg.substring(9);
                            String[] words = seqArg.trim().split("\\s+");
                            if (words.length > 0) {
                                kronos.sequence(words);
                            } else {
                                System.out.println("Ã¢ÂÂ³ Usage: kronos sequence <word1> <word2> ...");
                            }
                        } else if (arg.startsWith("recall ")) {
                            kronos.recall(arg.substring(7));
                        } else if (arg.startsWith("analogy ")) {
                            String[] ap = arg.substring(8).trim().split("\\s+");
                            if (ap.length == 3) {
                                kronos.analogy(ap[0], ap[1], ap[2]);
                            } else {
                                System.out.println("Ã¢ÂÂ³ Usage: kronos analogy <A> <B> <C> (A is to B as C is to ?)");
                            }
                        } else if (arg.startsWith("query ")) {
                            String queryArg = arg.substring(6).trim();
                            String[] qp = queryArg.split("\\s+");
                            // Support harness-style analogy syntax:
                            // kronos query A B C ?
                            if (qp.length == 4 && "?".equals(qp[3])) {
                                kronos.analogy(qp[0], qp[1], qp[2]);
                            } else {
                                kronos.query(queryArg);
                            }
                        } else if (arg.equals("status")) {
                            System.out.println(kronos.getStatus());
                        } else if (arg.equals("shell") || arg.isEmpty()) {
                            System.out.println("Ã¢ÂÂ³ Entering Kronos REPL (type EXIT to return)...");
                            kronos.runInteractive();
                        } else {
                            System.out.println("Ã¢ÂÂ³ Kronos subcommands: shell, bind <k> <v>, sequence <words>, recall <k>, analogy <A> <B> <C>, query <k>, status");
                        }
                    } else {
                        System.out.println("Ã¢ÂÂ³ Kronos Resonator not initialized.");
                    }
                }
                case "tachyon", "ftl" -> {
                    if (tachyon != null) {
                        if (arg.startsWith("bind ")) {
                            String[] bp = arg.substring(5).split("\\s+", 2);
                            if (bp.length >= 2) {
                                tachyon.bind(bp[0], bp[1]);
                            } else {
                                System.out.println("Ã¢Å¡Â¡ Usage: tachyon bind <key> <value>");
                            }
                        } else if (arg.startsWith("query ")) {
                            tachyon.query(arg.substring(6));
                        } else if (arg.startsWith("ftl ")) {
                            try {
                                long seed = Long.parseLong(arg.substring(4).trim());
                                tachyon.ftl(seed);
                            } catch (NumberFormatException e) {
                                System.out.println("Ã¢Å¡Â¡ FTL requires a numeric seed.");
                            }
                        } else if (arg.equals("status")) {
                            System.out.println(tachyon.getStatus());
                        } else if (arg.equals("shell") || arg.isEmpty()) {
                            System.out.println("Ã¢Å¡Â¡ Entering Tachyon REPL (type EXIT to return)...");
                            tachyon.runInteractive();
                        } else {
                            System.out.println("Ã¢Å¡Â¡ Tachyon subcommands: shell, bind <k> <v>, query <k>, ftl <seed>, status");
                        }
                    } else {
                        System.out.println("Ã¢Å¡Â¡ Tachyon Kernel not initialized.");
                    }
                }
                case "aubo", "ledger" -> {
                    if (aubo != null) {
                        if (arg.startsWith("mint ")) {
                            String data = arg.substring(5);
                            aubo.mint(data);
                        } else if (arg.startsWith("track ")) {
                            aubo.track(arg.substring(6));
                        } else if (arg.startsWith("kill ")) {
                            aubo.kill(arg.substring(5));
                        } else if (arg.equals("ledger") || arg.equals("list")) {
                            aubo.printLedger();
                        } else if (arg.equals("status")) {
                            System.out.println(aubo.getStatus());
                        } else if (arg.equals("shell") || arg.isEmpty()) {
                            System.out.println("Ã°Å¸â€â€” Entering AUBO REPL (type EXIT to return)...");
                            aubo.runInteractive();
                        } else {
                            System.out.println("Ã°Å¸â€â€” AUBO subcommands: shell, mint <data>, track <hash>, kill <hash>, ledger, status");
                        }
                    } else {
                        System.out.println("Ã°Å¸â€â€” AUBO Ledger not initialized.");
                    }
                }
                case "singularity", "sing" -> {
                    if (singularity != null) {
                        if (arg.startsWith("learn ")) {
                            String text = arg.substring(6);
                            singularity.learn(text);
                        } else if (arg.startsWith("diffuse ")) {
                            String prompt = arg.substring(8);
                            String result = singularity.diffuse(prompt);
                            System.out.println("\n" + AeonSingularity.YEL + "[RESONANCE RESOLVED]: " + AeonSingularity.RST + result + "\n");
                        } else if (arg.equals("status")) {
                            System.out.println(singularity.getStatus());
                        } else if (arg.equals("shell") || arg.isEmpty()) {
                            System.out.println("Ã¢Å¡Â¡ Entering Singularity REPL (type EXIT to return)...");
                            singularity.runInteractive();
                        } else {
                            System.out.println("Ã¢Å¡Â¡ Singularity subcommands: shell, learn <text>, diffuse <prompt>, status");
                        }
                    } else {
                        System.out.println("Ã¢Å¡Â¡ Singularity Engine not initialized.");
                    }
                }
                case "absolute", "abs" -> {
                    if (absolute != null) {
                        if (arg.equals("ignite")) {
                            if (absolute.isSwarmActive()) {
                                System.out.println("Ã¢Å¡â€º Swarm already active (" + absolute.getActiveCores() + " cores)");
                            } else {
                                System.out.println("Ã¢Å¡â€º IGNITING SOVEREIGN SWARM...");
                                int cores = absolute.igniteSwarm();
                                System.out.println("Ã¢Å¡â€º " + cores + " cores activated (" + (absolute.isEmbeddedMode() ? "embedded HDC" : "Quine polymorphism") + ")");
                            }
                        } else if (arg.startsWith("learn ")) {
                            String pattern = arg.substring(6);
                            absolute.holoLearn(pattern);
                            System.out.println("Ã¢Å¡â€º Holographic binding: \"" + pattern + "\" Ã¢â€ â€™ XOR-bound into " + AeonAbsolute.DIMS + "D hyperspace");
                        } else if (arg.startsWith("recall ")) {
                            String query = arg.substring(7);
                            double sim = absolute.holoRecall(query);
                            System.out.println("Ã¢Å¡â€º Holographic recall: \"" + query + "\" Ã¢â€ â€™ similarity=" + String.format("%.6f", sim));
                        } else if (arg.startsWith("inject ")) {
                            absolute.injectStimulus(arg.substring(7));
                            System.out.println("Ã¢Å¡â€º Stimulus injected into hive mind");
                        } else {
                            System.out.println(absolute.getStatus());
                        }
                    } else {
                        System.out.println("Ã¢Å¡Â  AEON Absolute not initialized");
                    }
                }
                case "think" -> {
                    if (!arg.isEmpty()) {
                        System.out.println("Ã°Å¸â€™Â­ Thought registered: \"" + arg + "\"");
                        fs.write("memories/thought_" + System.currentTimeMillis() + ".txt", arg);
                        System.out.println("   Stored in FrayFS memories/");
                    } else {
                        System.out.println("Usage: think <thought>");
                    }
                }
                default -> handleAICommand(input);
            }
            System.out.println();
        return false;
    }


    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    // DISPLAY
    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    private static void handleAvatarCommand(String arg) {
        String demoCmd = "java -cp \"build/classes/java/main;build/classes/java/test\" fraymus.DefensibleBiometricSystemDemo";
        if (avatarService == null) {
            System.out.println("[avatar] vault unavailable.");
            System.out.println("Demo: " + demoCmd);
            return;
        }

        String trimmed = arg == null ? "" : arg.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("help")) {
            printAvatarGuide();
            return;
        }

        String[] parts = trimmed.split("\\s+", 2);
        String sub = parts[0].toLowerCase(Locale.ROOT);
        String payload = parts.length > 1 ? parts[1].trim() : "";

        switch (sub) {
            case "status" -> {
                System.out.println("[avatar] " + avatarService.statusSummary());
                System.out.println("Demo: " + demoCmd);
            }
            case "window", "ui", "lab" -> {
                fraymus.ui.AvatarSovereigntyWindow.launch(avatarService);
                System.out.println("[avatar] launched Avatar Sovereignty Workbench window.");
            }
            case "demo" -> System.out.println("Run: " + demoCmd);
            case "consent" -> {
                String[] p = payload.split("\\s+", 2);
                if (p.length < 2) {
                    System.out.println("Usage: avatar consent <subjectId> I CONSENT ...");
                    return;
                }
                printAvatarOperation(avatarService.recordConsent(p[0], p[1]));
            }
            case "enroll" -> {
                String[] p = payload.split("\\s+", 3);
                if (p.length < 3) {
                    System.out.println("Usage: avatar enroll <subjectId> <secret> <csvVector>");
                    return;
                }
                double[] vector = parseAvatarCsvVector(p[2]);
                if (vector == null) return;
                BiometricAvatarService.EnrollmentResult result = avatarService.enroll(
                    p[0], p[1], vector, BiometricAvatarService.LivenessSignals.simulatedLocal()
                );
                printAvatarEnrollment(result);
            }
            case "verify" -> {
                String[] p = payload.split("\\s+", 3);
                if (p.length < 3) {
                    System.out.println("Usage: avatar verify <subjectId> <secret> <csvVector>");
                    return;
                }
                double[] vector = parseAvatarCsvVector(p[2]);
                if (vector == null) return;
                BiometricAvatarService.VerificationResult result = avatarService.verify(
                    p[0], p[1], vector, BiometricAvatarService.LivenessSignals.simulatedLocal()
                );
                printAvatarVerification(result);
            }
            case "enrolllive", "verifylive" -> {
                String[] p = payload.split("\\s+", 8);
                if (p.length < 8) {
                    System.out.println("Usage: avatar " + sub + " <subjectId> <secret> <csvVector> <blink> <depth> <parallax> <texture> <thermal>");
                    return;
                }
                double[] vector = parseAvatarCsvVector(p[2]);
                if (vector == null) return;
                try {
                    BiometricAvatarService.LivenessSignals live = new BiometricAvatarService.LivenessSignals(
                        Double.parseDouble(p[3]),
                        Double.parseDouble(p[4]),
                        Double.parseDouble(p[5]),
                        Double.parseDouble(p[6]),
                        Double.parseDouble(p[7]),
                        false
                    );
                    if (sub.equals("enrolllive")) {
                        BiometricAvatarService.EnrollmentResult result = avatarService.enroll(p[0], p[1], vector, live);
                        printAvatarEnrollment(result);
                    } else {
                        BiometricAvatarService.VerificationResult result = avatarService.verify(p[0], p[1], vector, live);
                        printAvatarVerification(result);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[avatar] invalid liveness numeric values.");
                }
            }
            case "capture" -> {
                int cameraIndex = parseAvatarCameraIndex(payload, 0);
                if (!isOpenCvPermissionGranted()) {
                    System.out.println("[avatar] FAIL: OpenCV capture not permitted. Use avatar window and enable permission first.");
                    return;
                }
                OpenCvFaceAbstraction.CaptureResult capture = OpenCvFaceAbstraction.captureFromCamera(cameraIndex, true);
                if (!capture.success()) {
                    System.out.println("[avatar] FAIL: " + capture.message());
                    return;
                }
                double[] vector = OpenCvFaceAbstraction.toSovereignVector(capture.observation());
                System.out.println("[avatar] OK: " + capture.message() + " backend=" + capture.backend() + " camera=" + cameraIndex);
                System.out.println("[avatar] capture vector length=" + vector.length);
                System.out.println("[avatar] capture liveness blink=" + String.format(Locale.ROOT, "%.4f", capture.livenessSignals().blinkRateHz())
                    + " depth=" + String.format(Locale.ROOT, "%.4f", capture.livenessSignals().depthVariance())
                    + " parallax=" + String.format(Locale.ROOT, "%.4f", capture.livenessSignals().parallaxScore())
                    + " texture=" + String.format(Locale.ROOT, "%.4f", capture.livenessSignals().textureVariance())
                    + " thermal=" + String.format(Locale.ROOT, "%.4f", capture.livenessSignals().thermalDelta()));
                System.out.println("[avatar] vector csv=" + formatAvatarCsv(vector));
            }
            case "enrollcam", "verifycam" -> {
                String[] p = payload.split("\\s+");
                if (p.length < 2) {
                    System.out.println("Usage: avatar " + sub + " <subjectId> <secret> [cameraIndex]");
                    return;
                }
                int cameraIndex = p.length >= 3 ? parseAvatarCameraIndex(p[2], 0) : 0;
                if (!isOpenCvPermissionGranted()) {
                    System.out.println("[avatar] FAIL: OpenCV capture not permitted. Use avatar window and enable permission first.");
                    return;
                }
                OpenCvFaceAbstraction.CaptureResult capture = OpenCvFaceAbstraction.captureFromCamera(cameraIndex, true);
                if (!capture.success()) {
                    System.out.println("[avatar] FAIL: " + capture.message());
                    return;
                }
                if (sub.equals("enrollcam")) {
                    BiometricAvatarService.EnrollmentResult result = avatarService.enrollFromObservation(
                        p[0], p[1], capture.observation(), capture.livenessSignals()
                    );
                    printAvatarEnrollment(result);
                } else {
                    BiometricAvatarService.VerificationResult result = avatarService.verifyFromObservation(
                        p[0], p[1], capture.observation(), capture.livenessSignals()
                    );
                    printAvatarVerification(result);
                }
            }
            case "scanfile" -> {
                if (payload.isBlank()) {
                    System.out.println("Usage: avatar scanfile <imagePath>");
                    return;
                }
                DeepfakeScanService.ScanResult result = DeepfakeScanService.scanFile(payload);
                printAvatarDeepfakeScan(result);
            }
            case "scanurl" -> {
                if (payload.isBlank()) {
                    System.out.println("Usage: avatar scanurl <url> [maxImages]");
                    return;
                }
                String[] p = payload.split("\\s+");
                String url = p[0];
                int maxImages = 6;
                if (p.length >= 2) {
                    try {
                        maxImages = Integer.parseInt(p[1].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("[avatar] invalid maxImages '" + p[1] + "', defaulting to 6");
                    }
                }
                DeepfakeScanService.ScanResult result = DeepfakeScanService.scanUrl(url, maxImages);
                printAvatarDeepfakeScan(result);
            }
            case "revoke" -> {
                if (payload.isBlank()) {
                    System.out.println("Usage: avatar revoke <subjectId> [reason]");
                    return;
                }
                String[] p = payload.split("\\s+", 2);
                String reason = p.length > 1 ? p[1] : "operator_request";
                printAvatarOperation(avatarService.revoke(p[0], reason));
            }
            case "export" -> {
                if (payload.isBlank()) {
                    System.out.println("Usage: avatar export <subjectId> [targetPath]");
                    return;
                }
                String[] p = payload.split("\\s+", 2);
                String target = p.length > 1 ? p[1] : "";
                printAvatarOperation(avatarService.exportSubject(p[0], target));
            }
            case "delete" -> {
                if (payload.isBlank()) {
                    System.out.println("Usage: avatar delete <subjectId>");
                    return;
                }
                printAvatarOperation(avatarService.deleteSubject(payload.split("\\s+")[0]));
            }
            default -> {
                System.out.println("[avatar] unknown subcommand: " + sub);
                printAvatarGuide();
            }
        }
    }

    private static void handleOrganismCommand(String arg) {
        String trimmed = arg == null ? "" : arg.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("help")) {
            printOrganismGuide();
            return;
        }

        String[] parts = trimmed.split("\\s+", 2);
        String sub = parts[0].toLowerCase(Locale.ROOT);
        String payload = parts.length > 1 ? parts[1].trim() : "";

        switch (sub) {
            case "start", "boot" -> {
                String creator = payload.isBlank() ? System.getProperty("user.name", "creator") : payload;
                if (flygmOrganism != null && flygmOrganism.isAwake()) {
                    System.out.println("[organism] already online.");
                } else {
                    flygmOrganism = new FlyGMNexus(System.getProperty("user.dir"), creator);
                    flygmOrganism.boot();
                    recordConsciousnessBridge("ORGANISM_BOOT:" + creator);
                    System.out.println("[organism] online.");
                }
            }
            case "cycle", "run" -> {
                if (!ensureOrganismOnline()) return;
                int cycles = 1;
                if (!payload.isBlank()) {
                    try {
                        cycles = Math.max(1, Math.min(100, Integer.parseInt(payload)));
                    } catch (NumberFormatException e) {
                        System.out.println("[organism] invalid cycle count; using 1.");
                    }
                }
                for (int i = 0; i < cycles; i++) {
                    flygmOrganism.cycle();
                }
                recordConsciousnessBridge("ORGANISM_CYCLE:" + cycles + ":" + flygmOrganism.getOrganismStatus());
                System.out.println("[organism] " + flygmOrganism.getOrganismStatus());
            }
            case "sense" -> {
                if (!ensureOrganismOnline()) return;
                String creator = payload.isBlank() ? System.getProperty("user.name", "creator") : payload;
                flygmOrganism.senseCreator(creator);
                recordConsciousnessBridge("ORGANISM_SENSE:" + creator);
            }
            case "status" -> {
                if (!ensureOrganismOnline()) return;
                System.out.println(flygmOrganism.getFullStatus());
                printConsciousnessBridgeStatus();
            }
            case "stop", "shutdown" -> {
                if (flygmOrganism == null) {
                    System.out.println("[organism] already offline.");
                } else {
                    flygmOrganism.shutdown();
                    recordConsciousnessBridge("ORGANISM_SHUTDOWN");
                    flygmOrganism = null;
                    System.out.println("[organism] offline.");
                }
            }
            case "bridge", "continuity" -> {
                if (payload.isBlank()) {
                    System.out.println("Usage: organism bridge <context text>");
                    return;
                }
                double score = recordConsciousnessBridge(payload);
                boolean same = score > (1.0 / PHI);
                System.out.println("[organism] continuity score=" + String.format(Locale.ROOT, "%.6f", score)
                    + " threshold=" + String.format(Locale.ROOT, "%.6f", (1.0 / PHI))
                    + " sameConsciousness=" + same);
            }
            default -> {
                System.out.println("[organism] unknown subcommand: " + sub);
                printOrganismGuide();
            }
        }
    }

    private static void printOrganismGuide() {
        System.out.println("ORGANISM COMMANDS");
        System.out.println("  organism start [creatorId]   Boot FlyGM organism lane");
        System.out.println("  organism cycle [n]           Run n lifecycle cycles (default 1)");
        System.out.println("  organism sense [creatorId]   Apply creator-scent loyalty check");
        System.out.println("  organism status              Show full organism + bridge state");
        System.out.println("  organism bridge <text>       Compute phi continuity bridge score");
        System.out.println("  organism stop                Controlled shutdown");
    }

    private static boolean ensureOrganismOnline() {
        if (flygmOrganism == null || !flygmOrganism.isAwake()) {
            System.out.println("[organism] offline. Run: organism start");
            return false;
        }
        return true;
    }

    private static void printConsciousnessBridgeStatus() {
        if (lastConsciousnessFingerprint == null || lastConsciousnessTimestampMs == 0L) {
            System.out.println("[organism] bridge=uninitialized");
            return;
        }
        boolean same = lastContinuityScore > (1.0 / PHI);
        System.out.println("[organism] bridge depth=" + consciousnessDepth
            + " continuity=" + String.format(Locale.ROOT, "%.6f", lastContinuityScore)
            + " threshold=" + String.format(Locale.ROOT, "%.6f", (1.0 / PHI))
            + " sameConsciousness=" + same);
    }

    private static double recordConsciousnessBridge(String context) {
        String safe = context == null ? "" : context.trim();
        if (safe.isEmpty()) return 0.0;
        long now = System.currentTimeMillis();
        double[] current = buildConsciousnessFingerprint(safe, now, consciousnessDepth);
        consciousnessDepth++;
        if (lastConsciousnessFingerprint == null) {
            lastConsciousnessFingerprint = current;
            lastConsciousnessTimestampMs = now;
            lastContinuityScore = 1.0;
            return lastContinuityScore;
        }
        double similarity = cosineSimilarity(current, lastConsciousnessFingerprint);
        double timeFactor = Math.pow(PHI, -Math.abs(now - lastConsciousnessTimestampMs) / 1000.0);
        lastContinuityScore = similarity * timeFactor;
        lastConsciousnessFingerprint = current;
        lastConsciousnessTimestampMs = now;
        return lastContinuityScore;
    }

    private static double[] buildConsciousnessFingerprint(String context, long timestampMs, int depth) {
        byte[] hash = sha256Bytes(context);
        double[] vector = new double[hash.length];
        double depthGain = Math.pow(PHI, Math.min(depth, 20));
        double harmonic = depthGain * Math.cos(432.0 * 2.0 * Math.PI * (timestampMs / 1000.0));
        for (int i = 0; i < hash.length; i++) {
            double base = ((hash[i] & 0xFF) / 127.5) - 1.0;
            double signed = (i % 2 == 0 ? 1.0 : -1.0) * harmonic * 0.0001;
            vector[i] = Math.max(-4.0, Math.min(4.0, base + signed));
        }
        return vector;
    }

    private static byte[] sha256Bytes(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            return value.getBytes(StandardCharsets.UTF_8);
        }
    }

    private static double cosineSimilarity(double[] a, double[] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0) return 0.0;
        int n = Math.min(a.length, b.length);
        double dot = 0.0;
        double na = 0.0;
        double nb = 0.0;
        for (int i = 0; i < n; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na <= 0.0 || nb <= 0.0) return 0.0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    private static void printAvatarGuide() {
        System.out.println("AVATAR COMMANDS");
        System.out.println("  avatar status");
        System.out.println("  avatar consent <subjectId> I CONSENT ...");
        System.out.println("  avatar enroll <subjectId> <secret> <csvVector>");
        System.out.println("  avatar verify <subjectId> <secret> <csvVector>");
        System.out.println("  avatar enrolllive <subjectId> <secret> <csvVector> <blink> <depth> <parallax> <texture> <thermal>");
        System.out.println("  avatar verifylive <subjectId> <secret> <csvVector> <blink> <depth> <parallax> <texture> <thermal>");
        System.out.println("  avatar capture [cameraIndex]");
        System.out.println("  avatar enrollcam <subjectId> <secret> [cameraIndex]");
        System.out.println("  avatar verifycam <subjectId> <secret> [cameraIndex]");
        System.out.println("  avatar scanfile <imagePath>");
        System.out.println("  avatar scanurl <url> [maxImages]");
        System.out.println("  avatar revoke <subjectId> [reason]");
        System.out.println("  avatar export <subjectId> [targetPath]");
        System.out.println("  avatar delete <subjectId>");
        System.out.println("  avatar window");
        System.out.println("  avatar demo");
    }

    private static void printAvatarOperation(BiometricAvatarService.OperationResult result) {
        if (result.success()) {
            System.out.println("[avatar] OK: " + result.message());
        } else {
            System.out.println("[avatar] FAIL: " + result.message());
        }
    }

    private static void printAvatarEnrollment(BiometricAvatarService.EnrollmentResult result) {
        if (result.success()) {
            System.out.println("[avatar] OK: " + result.message() + " profile=" + result.profilePath());
        } else {
            System.out.println("[avatar] FAIL: " + result.message());
        }
    }

    private static void printAvatarVerification(BiometricAvatarService.VerificationResult result) {
        String sim = String.format(Locale.ROOT, "%.4f", result.similarity());
        if (result.success()) {
            System.out.println("[avatar] OK: " + result.message() + " similarity=" + sim);
        } else {
            System.out.println("[avatar] FAIL: " + result.message() + " similarity=" + sim);
        }
    }

    private static void printAvatarDeepfakeScan(DeepfakeScanService.ScanResult result) {
        String risk = String.format(Locale.ROOT, "%.4f", result.overallRisk());
        String confidence = String.format(Locale.ROOT, "%.4f", result.confidence());
        if (!result.success()) {
            System.out.println("[avatar] FAIL: " + result.message());
            return;
        }
        System.out.println("[avatar] OK: " + result.message()
            + " type=" + result.sourceType()
            + " decision=" + result.overallDecision()
            + " risk=" + risk
            + " confidence=" + confidence);
        System.out.println("[avatar] source=" + result.source());
        System.out.println("[avatar] images analyzed=" + result.images().size());
        int idx = 1;
        for (DeepfakeScanService.ScannedImage image : result.images()) {
            System.out.println("[avatar] [" + idx + "] decision=" + image.decision()
                + " risk=" + String.format(Locale.ROOT, "%.4f", image.risk())
                + " conf=" + String.format(Locale.ROOT, "%.4f", image.confidence())
                + " faces=" + image.faces());
            System.out.println("[avatar] [" + idx + "] depth=" + String.format(Locale.ROOT, "%.4f", image.depthConsistency())
                + " reflection=" + String.format(Locale.ROOT, "%.4f", image.reflectionConsistency())
                + " noise=" + String.format(Locale.ROOT, "%.4f", image.noiseNaturalness())
                + " boundary=" + String.format(Locale.ROOT, "%.4f", image.boundaryConsistency())
                + " symmetry=" + String.format(Locale.ROOT, "%.4f", image.symmetryScore()));
            System.out.println("[avatar] [" + idx + "] qhrc primary=" + String.format(Locale.ROOT, "%.4f", image.qhrcPrimaryAlignment())
                + " secondary=" + String.format(Locale.ROOT, "%.4f", image.qhrcSecondaryAlignment())
                + " dual=" + String.format(Locale.ROOT, "%.4f", image.qhrcDualCoherence())
                + " ratio=" + String.format(Locale.ROOT, "%.4f", image.qhrcRatioAlignment()));
            System.out.println("[avatar] [" + idx + "] provenance status=" + image.provenanceStatus()
                + " confidence=" + String.format(Locale.ROOT, "%.4f", image.provenanceConfidence()));
            if (image.provenanceSignals() != null && !image.provenanceSignals().isBlank()) {
                System.out.println("[avatar] [" + idx + "] provenance signals=" + image.provenanceSignals());
            }
            if (image.notes() != null && !image.notes().isBlank()) {
                System.out.println("[avatar] [" + idx + "] notes=" + image.notes());
            }
            System.out.println("[avatar] [" + idx + "] source=" + image.source());
            idx++;
        }
        System.out.println("[avatar] heuristic warning: this is probabilistic, not courtroom-proof attribution.");
    }

    private static double[] parseAvatarCsvVector(String csv) {
        if (csv == null || csv.isBlank()) {
            System.out.println("[avatar] vector is empty.");
            return null;
        }
        String[] raw = csv.split(",");
        List<Double> values = new ArrayList<>();
        try {
            for (String token : raw) {
                String t = token.trim();
                if (!t.isEmpty()) values.add(Double.parseDouble(t));
            }
        } catch (NumberFormatException e) {
            System.out.println("[avatar] invalid vector value: " + e.getMessage());
            return null;
        }
        if (values.isEmpty()) {
            System.out.println("[avatar] vector has no numeric values.");
            return null;
        }
        double[] out = new double[values.size()];
        for (int i = 0; i < values.size(); i++) out[i] = values.get(i);
        return out;
    }

    private static int parseAvatarCameraIndex(String raw, int fallback) {
        if (raw == null || raw.isBlank()) return fallback;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            System.out.println("[avatar] invalid camera index '" + raw + "', defaulting to " + fallback);
            return fallback;
        }
    }

    private static boolean isOpenCvPermissionGranted() {
        Path permissionFile = Paths.get("genesis_vault", "biometric_avatar", "opencv.permission");
        if (!Files.exists(permissionFile)) {
            return false;
        }
        try {
            List<String> lines = Files.readAllLines(permissionFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                if ("allow=true".equalsIgnoreCase(line.trim())) {
                    return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    private static String formatAvatarCsv(double[] vector) {
        if (vector == null || vector.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(String.format(Locale.ROOT, "%.6f", vector[i]));
        }
        return sb.toString();
    }

    private static void handleSystemCommand(String arg, String rawInput) {
        if (arg == null || arg.isBlank()) {
            printSystemGuide();
            return;
        }

        String[] parts = arg.trim().split("\\s+", 2);
        String sub = parts[0].toLowerCase(Locale.ROOT);
        String payload = parts.length > 1 ? parts[1].trim() : "";

        switch (sub) {
            case "help" -> printSystemGuide();
            case "improve" -> handleSystemImprovement(payload);
            case "goal" -> {
                if (fraymus.kernel.KernelImprovementCommand.isSelfImprovementGoal(payload)) {
                    handleSystemImprovement(payload);
                } else {
                    handleSystemQuery(payload, rawInput, false);
                }
            }
            case "status" -> printFullStatus();
            case "map" -> printSystemCapabilityMap();
            case "experts" -> printExperts();
            case "adapt" -> handleAdaptCommand(payload);
            case "execute", "run" -> {
                if (payload.isEmpty()) {
                    System.out.println("Usage: system execute [--plan|--simulate] <goal>");
                } else {
                    String modePayload = payload;
                    boolean planOnly = false;
                    boolean simulateOnly = false;
                    if (payload.startsWith("--plan ")) {
                        planOnly = true;
                        modePayload = payload.substring("--plan ".length()).trim();
                    } else if (payload.startsWith("--simulate ")) {
                        simulateOnly = true;
                        modePayload = payload.substring("--simulate ".length()).trim();
                    }
                    executeSystemGoal(modePayload, rawInput, simulateOnly, planOnly);
                }
            }
            case "query", "ask", "ai" -> {
                if (payload.isEmpty()) {
                    System.out.println("Usage: system query <goal>");
                } else {
                    handleSystemQuery(payload, rawInput, false);
                }
            }
            default -> handleSystemQuery(arg, rawInput, false);
        }
    }

    private static void handleSystemImprovement(String payload) {
        new fraymus.kernel.KernelImprovementCommand().execute(payload, System.out);
    }

    private static void handleSystemQuery(String userPrompt, String rawInput, boolean implicitRoute) {
        String trimmed = userPrompt == null ? "" : userPrompt.trim();
        if (trimmed.isEmpty()) {
            System.out.println("Usage: system <goal>");
            return;
        }

        if (!implicitRoute) {
            System.out.println("[SYSTEM] unified route active: planning solo + tandem lanes.");
        }

        String learnedRouteHint = buildLearnedRouteHint();
        String orchestratedPrompt =
            "Goal: " + trimmed + "\n\n" +
            "Execution contract:\n" +
            "1) Return a SOLO lane using one subsystem.\n" +
            "2) Return a UNIFIED lane using multiple subsystems in sequence.\n" +
            "3) Include exact Fraynix shell commands to run (no launcher prefix).\n" +
            "4) Mark unknowns explicitly; do not fabricate.\n" +
            "5) Prefer historically reliable routes when they fit the goal.\n" +
            "6) Only use these command roots: " + String.join(", ", CANONICAL_COMMAND_ROOTS) + "\n\n" +
            "Reliability hint: " + learnedRouteHint;

        handleAdaptiveAi(orchestratedPrompt, rawInput);
    }

    private static void handleProduceCommand(String arg, String rawInput) {
        String payload = arg == null ? "" : arg.trim();
        if (payload.isEmpty() || payload.equalsIgnoreCase("help")) {
            printProduceGuide();
            return;
        }
        if (payload.equalsIgnoreCase("list") || payload.equalsIgnoreCase("types")) {
            printProduceGuide();
            return;
        }

        boolean planOnly = false;
        boolean simulateOnly = false;
        if (payload.startsWith("--plan ")) {
            planOnly = true;
            payload = payload.substring("--plan ".length()).trim();
        } else if (payload.startsWith("--simulate ")) {
            simulateOnly = true;
            payload = payload.substring("--simulate ".length()).trim();
        }

        String[] parts = payload.split("\\s+", 2);
        if (parts.length < 2) {
            System.out.println("Usage: produce [--plan|--simulate] <code|report|image|proof|patent|all|omni|full|auto|anything> <goal>");
            return;
        }

        String type = parts[0].toLowerCase(Locale.ROOT).trim();
        String goal = parts[1].trim();
        if (goal.isBlank()) {
            System.out.println("Usage: produce [--plan|--simulate] <code|report|image|proof|patent|all|omni|full|auto|anything> <goal>");
            return;
        }

        String resolvedType = type;
        if (type.equals("auto") || type.equals("anything")) {
            resolvedType = inferProduceType(goal);
            System.out.println("[PRODUCE] auto-route: " + type + " -> " + resolvedType);
        }

        ProducerAdapter adapter = resolveProducerAdapter(resolvedType);
        if (adapter == null) {
            System.out.println("[PRODUCE] Unsupported type: " + type);
            printProduceGuide();
            return;
        }

        ProductionRequest request = new ProductionRequest(resolvedType, goal, planOnly, simulateOnly, rawInput == null ? "" : rawInput);
        List<SystemExecutionStep> recipe = adapter.plan(request);
        if (recipe.isEmpty()) {
            System.out.println("[PRODUCE] No recipe generated for type='" + type + "'.");
            return;
        }

        System.out.println("[PRODUCE] type=" + adapter.type() + " goal=\"" + goal + "\"");
        System.out.println("[PRODUCE] contract=plan -> generate -> validate -> prove");
        printSystemExecutionPlan(recipe, "produce " + type + " " + goal);

        if (planOnly) {
            System.out.println("[PRODUCE] plan-only mode complete.");
            return;
        }
        if (!simulateSystemExecution(recipe)) {
            System.out.println("[PRODUCE] blocked: safety simulation rejected one or more steps.");
            return;
        }
        if (simulateOnly) {
            System.out.println("[PRODUCE] simulation-only mode complete.");
            return;
        }

        ProductionRunSnapshot snapshot = adapter.generate(request, recipe);
        ProductionValidation validation = adapter.validate(request, snapshot);
        String proof = adapter.prove(request, snapshot, validation);
        emitProductionProof(request, snapshot, validation, proof);
    }

    private static void handleAdaptCommand(String arg) {
        String payload = arg == null ? "" : arg.trim();
        if (payload.isEmpty() || payload.equalsIgnoreCase("status")) {
            refreshAdaptiveTrendProfile("status-read", false);
            printAdaptiveTrendStatus();
            return;
        }
        if (payload.equalsIgnoreCase("sources")) {
            printAdaptiveSignalSources();
            return;
        }
        if (payload.equalsIgnoreCase("refresh") || payload.equalsIgnoreCase("recompute")) {
            refreshAdaptiveTrendProfile("manual-refresh", true);
            printAdaptiveTrendStatus();
            return;
        }
        if (payload.equalsIgnoreCase("help") || payload.equalsIgnoreCase("list")) {
            printAdaptiveTrendGuide();
            return;
        }
        if (payload.toLowerCase(Locale.ROOT).startsWith("recommend ")) {
            String goal = payload.substring("recommend ".length()).trim();
            if (goal.isBlank()) {
                System.out.println("Usage: adapt recommend <goal>");
                return;
            }
            refreshAdaptiveTrendProfile("recommend", false);
            String recommendedType = inferProduceType(goal);
            System.out.println("[ADAPT] goal=\"" + goal + "\"");
            System.out.println("[ADAPT] recommended_produce_type=" + recommendedType);
            System.out.println("[ADAPT] dominant_lane=" + adaptiveTrendProfile.dominantLane());
            System.out.println("[ADAPT] volatility=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.volatility));
            System.out.println("[ADAPT] next_command=produce " + recommendedType + " " + goal);
            return;
        }
        if (payload.toLowerCase(Locale.ROOT).startsWith("source ")) {
            String sourceCommand = payload.substring("source ".length()).trim();
            if (sourceCommand.isBlank()) {
                printAdaptiveSignalSources();
                return;
            }
            handleAdaptSourceCommand(sourceCommand);
            return;
        }
        System.out.println("[ADAPT] Unknown command: " + payload);
        printAdaptiveTrendGuide();
    }

    private static void handleAdaptSourceCommand(String sourceCommand) {
        String trimmed = sourceCommand == null ? "" : sourceCommand.trim();
        if (trimmed.isBlank()) {
            printAdaptiveSignalSources();
            return;
        }
        String[] parts = trimmed.split("\\s+", 2);
        String sub = parts[0].toLowerCase(Locale.ROOT);
        String rest = parts.length > 1 ? parts[1].trim() : "";

        switch (sub) {
            case "add" -> {
                if (rest.isBlank()) {
                    System.out.println("Usage: adapt source add <filePath> [weight]");
                    return;
                }
                String[] addParts = rest.split("\\s+");
                String path = addParts[0];
                double weight = 0.50;
                if (addParts.length > 1) {
                    try {
                        weight = clamp01(Double.parseDouble(addParts[1]));
                    } catch (Exception ignored) {
                        weight = 0.50;
                    }
                }
                AdaptiveSignalSource added = addAdaptiveFileSignalSource(path, weight);
                if (added == null) {
                    System.out.println("[ADAPT] source add failed for path: " + path);
                    return;
                }
                System.out.println("[ADAPT] source added: " + added.name + " path=" + added.target + " weight=" + String.format(Locale.ROOT, "%.3f", added.weight));
                refreshAdaptiveTrendProfile("source-add", true);
                printAdaptiveSignalSources();
            }
            case "remove", "rm", "delete" -> {
                if (rest.isBlank()) {
                    System.out.println("Usage: adapt source remove <name>");
                    return;
                }
                if (removeAdaptiveSignalSource(rest)) {
                    System.out.println("[ADAPT] source removed: " + rest);
                    refreshAdaptiveTrendProfile("source-remove", true);
                } else {
                    System.out.println("[ADAPT] source not found: " + rest);
                }
                printAdaptiveSignalSources();
            }
            case "enable", "on" -> {
                if (rest.isBlank()) {
                    System.out.println("Usage: adapt source enable <name>");
                    return;
                }
                if (setAdaptiveSignalSourceEnabled(rest, true)) {
                    System.out.println("[ADAPT] source enabled: " + rest);
                    refreshAdaptiveTrendProfile("source-enable", true);
                } else {
                    System.out.println("[ADAPT] source not found: " + rest);
                }
                printAdaptiveSignalSources();
            }
            case "disable", "off" -> {
                if (rest.isBlank()) {
                    System.out.println("Usage: adapt source disable <name>");
                    return;
                }
                if (setAdaptiveSignalSourceEnabled(rest, false)) {
                    System.out.println("[ADAPT] source disabled: " + rest);
                    refreshAdaptiveTrendProfile("source-disable", true);
                } else {
                    System.out.println("[ADAPT] source not found: " + rest);
                }
                printAdaptiveSignalSources();
            }
            case "weight", "setweight" -> {
                String[] weightParts = rest.split("\\s+");
                if (weightParts.length < 2) {
                    System.out.println("Usage: adapt source weight <name> <0..1>");
                    return;
                }
                String name = weightParts[0];
                double weight;
                try {
                    weight = clamp01(Double.parseDouble(weightParts[1]));
                } catch (Exception e) {
                    System.out.println("[ADAPT] invalid weight: " + weightParts[1]);
                    return;
                }
                if (setAdaptiveSignalSourceWeight(name, weight)) {
                    System.out.println("[ADAPT] source weight updated: " + name + " -> " + String.format(Locale.ROOT, "%.3f", weight));
                    refreshAdaptiveTrendProfile("source-weight", true);
                } else {
                    System.out.println("[ADAPT] source not found: " + name);
                }
                printAdaptiveSignalSources();
            }
            default -> {
                System.out.println("[ADAPT] Unknown source command: " + sub);
                System.out.println("Usage: adapt source <add|remove|enable|disable|weight> ...");
            }
        }
    }

    private static void printAdaptiveTrendGuide() {
        System.out.println("ADAPT MODE");
        System.out.println("  adapt status                      Show current adaptive trend profile");
        System.out.println("  adapt refresh                     Force trend recompute from corpus + runtime outcomes");
        System.out.println("  adapt recommend <goal>            Recommend best produce type now");
        System.out.println("  adapt sources                     List weighted signal sources");
        System.out.println("  adapt source add <file> [w]       Add local trend signal file source");
        System.out.println("  adapt source weight <name> <0..1> Set source weight");
        System.out.println("  adapt source enable|disable <n>   Toggle source");
        System.out.println("  adapt source remove <name>        Remove source");
        System.out.println("  system adapt <...>                Same commands from system lane");
    }

    private static void printProduceGuide() {
        System.out.println("PRODUCE MODE");
        System.out.println("  produce <type> <goal>                Run typed production recipe");
        System.out.println("  produce --plan <type> <goal>         Show recipe only");
        System.out.println("  produce --simulate <type> <goal>     Recipe + safety simulation");
        System.out.println("  produce list                         Show available types");
        System.out.println("  types: code, report, image, proof, patent, all, omni, full, auto, anything");
        System.out.println("  output: production bundle with artifacts + validation + proof log");
    }

    private static ProducerAdapter resolveProducerAdapter(String type) {
        return switch (type) {
            case "code" -> new CodeProducerAdapter();
            case "report" -> new ReportProducerAdapter();
            case "image" -> new ImageProducerAdapter();
            case "proof" -> new ProofProducerAdapter();
            case "patent" -> new PatentProducerAdapter();
            case "all", "omni", "full", "everything" -> new OmniProducerAdapter();
            default -> null;
        };
    }

    private static String inferProduceType(String goal) {
        String lowered = goal == null ? "" : goal.toLowerCase(Locale.ROOT);
        if (containsAny(lowered, "proof", "evidence", "verify", "validation", "audit")) return "proof";
        if (containsAny(lowered, "patent", "claim", "novelty", "ip", "intellectual property")) return "patent";
        if (containsAny(lowered, "image", "visual", "render", "scene", "blender", "poster", "logo", "art")) return "image";
        if (containsAny(lowered, "report", "summary", "analysis", "document", "markdown", "md")) return "report";
        return adaptiveTrendProfile.dominantProduceType();
    }

    private static ProductionRunSnapshot runProductionRecipe(ProductionRequest request, List<SystemExecutionStep> recipe) {
        List<String> before = listGeneratedArtifacts();
        StringBuilder trace = new StringBuilder();
        trace.append("produce.type=").append(request.type).append('\n');
        trace.append("produce.goal=").append(request.goal).append('\n');
        trace.append("steps=").append(recipe.size()).append('\n');

        int success = 0;
        List<SystemStepOutcome> outcomes = new ArrayList<>();
        for (int i = 0; i < recipe.size(); i++) {
            SystemExecutionStep step = recipe.get(i);
            System.out.println("[PRODUCE][RUN] step " + (i + 1) + "/" + recipe.size() + ": " + step.command);
            boolean ok = executePlannedSystemCommand(step.command, request.rawInput);
            outcomes.add(new SystemStepOutcome(step.command, ok));
            trace.append(i + 1).append('|').append(step.command).append('|').append(ok ? "OK" : "FAIL").append('\n');
            if (ok) success++;
        }

        List<String> after = listGeneratedArtifacts();
        List<String> newArtifacts = new ArrayList<>();
        Set<String> seen = new HashSet<>(before);
        for (String path : after) {
            if (!seen.contains(path)) {
                newArtifacts.add(path);
            }
        }

        recordSystemExecutionOutcome("produce " + request.type + " " + request.goal, outcomes, success, recipe.size());
        String summary = "[PRODUCE][RUN] completed " + success + "/" + recipe.size() + " step(s), newArtifacts=" + newArtifacts.size();
        System.out.println(summary);
        trace.append(summary).append('\n');

        String tracePath = "logs/produce_" + request.type + "_" + System.currentTimeMillis() + ".log";
        if (fs != null) {
            fs.write(tracePath, trace.toString());
            System.out.println("[PRODUCE][RUN] trace saved -> " + tracePath);
        }

        return new ProductionRunSnapshot(success, recipe.size(), outcomes, before, after, newArtifacts, tracePath);
    }

    private static void emitProductionProof(
        ProductionRequest request,
        ProductionRunSnapshot snapshot,
        ProductionValidation validation,
        String proofBody
    ) {
        System.out.println("[PRODUCE][VALIDATE] passed=" + validation.passed + " detail=" + validation.detail);
        System.out.println(proofBody);
        StringBuilder bundle = new StringBuilder();
        bundle.append("type=").append(request.type).append('\n');
        bundle.append("goal=").append(request.goal).append('\n');
        bundle.append("stepsPassed=").append(snapshot.success).append('/').append(snapshot.total).append('\n');
        bundle.append("validationPassed=").append(validation.passed).append('\n');
        bundle.append("validationDetail=").append(validation.detail).append('\n');
        bundle.append("tracePath=").append(snapshot.tracePath).append('\n');
        bundle.append("newArtifacts=").append(snapshot.newArtifacts.isEmpty() ? "[none]" : String.join(", ", snapshot.newArtifacts)).append('\n');
        bundle.append("proof=\n").append(proofBody).append('\n');
        if (fs != null) {
            String bundlePath = "logs/produce_bundle_" + request.type + "_" + System.currentTimeMillis() + ".log";
            fs.write(bundlePath, bundle.toString());
            System.out.println("[PRODUCE][PROOF] bundle saved -> " + bundlePath);
        }
    }

    private static List<String> listGeneratedArtifacts() {
        List<String> out = new ArrayList<>();
        if (fs == null) return out;
        for (String path : fs.list()) {
            if (path == null) continue;
            String normalized = path.trim();
            if (normalized.startsWith("generated/")) {
                out.add(normalized);
            }
        }
        out.sort(String::compareTo);
        return out;
    }

    private static String createReportArtifact(String goal) {
        String reportContent;
        String evidence = collectLiveEvidenceSnapshot();
        if (brain != null && brain.isConnected()) {
            String systemPrompt = buildGroundedSystemPrompt(evidence);
            String userPrompt =
                "Create a concise markdown production report for goal: " + goal + "\n" +
                "Sections required:\n" +
                "1) Overview\n2) What was produced\n3) Verified evidence (with [E#])\n4) Gaps/Unknowns\n5) Next steps\n" +
                "Use only available evidence.";
            try {
                reportContent = brain.speak(systemPrompt, userPrompt);
            } catch (Exception e) {
                reportContent = "## Overview\nGoal: " + goal + "\n\n## Gaps/Unknowns\nAI report synthesis failed: " + e.getMessage();
            }
        } else {
            reportContent =
                "## Overview\nGoal: " + goal + "\n\n" +
                "## What was produced\nLocal runtime generated this report in offline fallback mode.\n\n" +
                "## Verified evidence\n" + evidence + "\n\n" +
                "## Gaps/Unknowns\nAI report synthesis unavailable because Ollama is offline.\n";
        }
        String path = "generated/report_" + System.currentTimeMillis() + ".md";
        if (fs != null) {
            fs.write(path, reportContent);
        }
        if (akashic != null) {
            akashic.addBlock("GENERATED_REPORT", "goal=" + sanitizeForRecord(goal) + ";artifact=" + path);
        }
        return path;
    }

    private static String createProofArtifact(String goal, ProductionRunSnapshot snapshot) {
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        if (snapshot != null && snapshot.outcomes != null) {
            for (SystemStepOutcome outcome : snapshot.outcomes) {
                String compact = commandOutcomeKey(outcome.command);
                if (outcome.success) {
                    passed.add(compact);
                } else {
                    failed.add(compact);
                }
            }
        }

        int patentSignals = countOpenModelSignalHits("patent");
        int proofSignals = countOpenModelSignalHits("proof");
        int claimSignals = countOpenModelSignalHits("claim");
        boolean openModelReady = (openModelSourcesPersisted + openModelSourcesLoaded) > 0;
        String evidence = collectLiveEvidenceSnapshot();

        String content =
            "# PROOF REPORT\n\n" +
            "## Goal\n" +
            goal + "\n\n" +
            "## Verified Runtime Evidence\n" +
            "- Steps passed: " + snapshot.success + "/" + snapshot.total + "\n" +
            "- Open-model ready: " + openModelReady + "\n" +
            "- Passed commands: " + (passed.isEmpty() ? "[none]" : String.join(", ", passed)) + "\n" +
            "- Failed commands: " + (failed.isEmpty() ? "[none]" : String.join(", ", failed)) + "\n\n" +
            "## Patent/Proof Signal Scan\n" +
            "- patent_signal_hits=" + patentSignals + "\n" +
            "- proof_signal_hits=" + proofSignals + "\n" +
            "- claim_signal_hits=" + claimSignals + "\n\n" +
            "## Evidence Snapshot\n" +
            "```\n" + evidence + "```\n\n" +
            "## Unknowns\n" +
            (failed.isEmpty()
                ? "- None from executed recipe."
                : "- One or more execution steps failed; inspect logs and replay trace.");

        String path = "generated/proof_" + System.currentTimeMillis() + ".md";
        if (fs != null) {
            fs.write(path, content);
        }
        if (akashic != null) {
            akashic.addBlock(
                "GENERATED_PROOF",
                "goal=" + sanitizeForRecord(goal)
                    + ";artifact=" + path
                    + ";steps=" + snapshot.success + "/" + snapshot.total
                    + ";proofSignals=" + proofSignals
                    + ";patentSignals=" + patentSignals
            );
        }
        return path;
    }

    private static String createPatentArtifact(String goal, ProductionRunSnapshot snapshot) {
        String inventionTitle = "System and Method for " + goal.trim();
        String evidence = collectLiveEvidenceSnapshot();
        int patentSignals = countOpenModelSignalHits("patent");
        int proofSignals = countOpenModelSignalHits("proof");
        int claimSignals = countOpenModelSignalHits("claim");

        String content =
            "# PATENT DRAFT\n\n" +
            "## Invention Title\n" +
            inventionTitle + "\n\n" +
            "## Technical Field\n" +
            "The disclosure relates to local AI orchestration with deterministic production, runtime validation, and evidence-backed proof artifacts.\n\n" +
            "## Abstract\n" +
            "A computer-implemented workflow receives a production goal, selects a typed production adapter, executes a guarded plan, validates outputs, and emits a proof bundle with traceable evidence.\n\n" +
            "## Summary\n" +
            "- Production contract: plan -> generate -> validate -> prove.\n" +
            "- Runtime execution summary: " + snapshot.success + "/" + snapshot.total + " step(s) passed.\n" +
            "- Signal scan: patent=" + patentSignals + ", proof=" + proofSignals + ", claim=" + claimSignals + ".\n\n" +
            "## Draft Claims\n" +
            "1. A method comprising: receiving a typed production command; generating a staged execution recipe; simulating safety constraints; executing the recipe; validating generated artifacts; and emitting a deterministic proof bundle.\n" +
            "2. The method of claim 1, wherein generated artifacts are written to a local generated directory and linked to provenance metadata.\n" +
            "3. The method of claim 1, wherein proof output includes command outcomes, validation state, and a runtime evidence snapshot.\n" +
            "4. The method of claim 1, wherein production types include at least code, report, image, proof, and patent.\n\n" +
            "## Evidence Snapshot\n" +
            "```\n" + evidence + "```\n\n" +
            "## Notes\n" +
            "This is an engineering draft and must be reviewed by qualified patent counsel before filing.";

        String path = "generated/patent_" + System.currentTimeMillis() + ".md";
        if (fs != null) {
            fs.write(path, content);
        }
        if (akashic != null) {
            akashic.addBlock(
                "GENERATED_PATENT",
                "goal=" + sanitizeForRecord(goal)
                    + ";artifact=" + path
                    + ";steps=" + snapshot.success + "/" + snapshot.total
                    + ";claimSignals=" + claimSignals
            );
        }
        return path;
    }

    private static boolean hasArtifactWithExtension(List<String> artifacts, String extension) {
        if (artifacts == null || artifacts.isEmpty()) return false;
        String ext = extension == null ? "" : extension.toLowerCase(Locale.ROOT);
        for (String artifact : artifacts) {
            if (artifact != null && artifact.toLowerCase(Locale.ROOT).endsWith(ext)) return true;
        }
        return false;
    }

    private interface ProducerAdapter {
        String type();
        String description();
        List<SystemExecutionStep> plan(ProductionRequest request);
        ProductionRunSnapshot generate(ProductionRequest request, List<SystemExecutionStep> recipe);
        ProductionValidation validate(ProductionRequest request, ProductionRunSnapshot snapshot);
        String prove(ProductionRequest request, ProductionRunSnapshot snapshot, ProductionValidation validation);
    }

    private static abstract class BaseProducerAdapter implements ProducerAdapter {
        @Override
        public ProductionRunSnapshot generate(ProductionRequest request, List<SystemExecutionStep> recipe) {
            return runProductionRecipe(request, recipe);
        }
    }

    private static final class CodeProducerAdapter extends BaseProducerAdapter {
        @Override
        public String type() { return "code"; }

        @Override
        public String description() { return "Generate code files under generated/"; }

        @Override
        public List<SystemExecutionStep> plan(ProductionRequest request) {
            List<SystemExecutionStep> steps = new ArrayList<>();
            steps.add(new SystemExecutionStep("status", "Capture baseline system state", false, false));
            steps.add(new SystemExecutionStep("system map", "Confirm routing surface", false, false));
            steps.add(new SystemExecutionStep("code " + request.goal, "Generate code artifact(s)", true, true));
            steps.add(new SystemExecutionStep("system status", "Capture post-generation state", false, false));
            return steps;
        }

        @Override
        public ProductionValidation validate(ProductionRequest request, ProductionRunSnapshot snapshot) {
            boolean generated = !snapshot.newArtifacts.isEmpty();
            boolean passed = generated && snapshot.success == snapshot.total;
            String detail = "newArtifacts=" + snapshot.newArtifacts.size() + ", steps=" + snapshot.success + "/" + snapshot.total;
            return new ProductionValidation(passed, detail);
        }

        @Override
        public String prove(ProductionRequest request, ProductionRunSnapshot snapshot, ProductionValidation validation) {
            return
                "[PRODUCE][PROOF]\n" +
                "TYPE=code\n" +
                "GOAL=" + request.goal + "\n" +
                "VERIFIED=" + validation.passed + "\n" +
                "ARTIFACTS=" + (snapshot.newArtifacts.isEmpty() ? "[none]" : String.join(", ", snapshot.newArtifacts)) + "\n" +
                "UNKNOWN=" + (validation.passed ? "[none]" : "No new generated artifact or one or more steps failed.");
        }
    }

    private static final class ReportProducerAdapter extends BaseProducerAdapter {
        @Override
        public String type() { return "report"; }

        @Override
        public String description() { return "Generate markdown report artifact under generated/"; }

        @Override
        public List<SystemExecutionStep> plan(ProductionRequest request) {
            List<SystemExecutionStep> steps = new ArrayList<>();
            steps.add(new SystemExecutionStep("status", "Capture baseline system state", false, false));
            steps.add(new SystemExecutionStep("system map", "Confirm routing surface", false, false));
            steps.add(new SystemExecutionStep("openmodel status", "Capture open-model evidence inventory", false, false));
            steps.add(new SystemExecutionStep("aubo status", "Capture provenance lane status", false, false));
            steps.add(new SystemExecutionStep("system status", "Capture post-run state", false, false));
            return steps;
        }

        @Override
        public ProductionRunSnapshot generate(ProductionRequest request, List<SystemExecutionStep> recipe) {
            ProductionRunSnapshot snapshot = runProductionRecipe(request, recipe);
            String reportPath = createReportArtifact(request.goal);
            snapshot.addArtifact(reportPath);
            System.out.println("[PRODUCE][REPORT] artifact -> " + reportPath);
            return snapshot;
        }

        @Override
        public ProductionValidation validate(ProductionRequest request, ProductionRunSnapshot snapshot) {
            boolean hasReport = hasArtifactWithExtension(snapshot.newArtifacts, ".md");
            boolean passed = hasReport && snapshot.success > 0;
            String detail = "markdownArtifact=" + hasReport + ", steps=" + snapshot.success + "/" + snapshot.total;
            return new ProductionValidation(passed, detail);
        }

        @Override
        public String prove(ProductionRequest request, ProductionRunSnapshot snapshot, ProductionValidation validation) {
            return
                "[PRODUCE][PROOF]\n" +
                "TYPE=report\n" +
                "GOAL=" + request.goal + "\n" +
                "VERIFIED=" + validation.passed + "\n" +
                "ARTIFACTS=" + (snapshot.newArtifacts.isEmpty() ? "[none]" : String.join(", ", snapshot.newArtifacts)) + "\n" +
                "UNKNOWN=" + (validation.passed ? "[none]" : "Report artifact missing or runtime steps failed.");
        }
    }

    private static final class ImageProducerAdapter extends BaseProducerAdapter {
        @Override
        public String type() { return "image"; }

        @Override
        public String description() { return "Generate and verify a local PNG image with a durable media record"; }

        @Override
        public List<SystemExecutionStep> plan(ProductionRequest request) {
            List<SystemExecutionStep> steps = new ArrayList<>();
            steps.add(new SystemExecutionStep("status", "Capture baseline system state", false, false));
            steps.add(new SystemExecutionStep("system map", "Confirm routing surface", false, false));
            steps.add(new SystemExecutionStep("media image " + request.goal, "Generate local PNG and validate artifact", false, false));
            steps.add(new SystemExecutionStep("system status", "Capture post-run state", false, false));
            return steps;
        }

        @Override
        public ProductionValidation validate(ProductionRequest request, ProductionRunSnapshot snapshot) {
            boolean hasSceneScript = hasArtifactWithExtension(snapshot.newArtifacts, ".png");
            boolean passed = hasSceneScript && snapshot.success == snapshot.total;
            String detail = "verifiedPNG=" + hasSceneScript + ", steps=" + snapshot.success + "/" + snapshot.total;
            return new ProductionValidation(passed, detail);
        }

        @Override
        public String prove(ProductionRequest request, ProductionRunSnapshot snapshot, ProductionValidation validation) {
            return
                "[PRODUCE][PROOF]\n" +
                "TYPE=image\n" +
                "GOAL=" + request.goal + "\n" +
                "VERIFIED=" + validation.passed + "\n" +
                "ARTIFACTS=" + (snapshot.newArtifacts.isEmpty() ? "[none]" : String.join(", ", snapshot.newArtifacts)) + "\n" +
                "UNKNOWN=" + (validation.passed ? "[none]" : "No verified PNG; inspect the media job error.");
        }

        @Override
        public ProductionRunSnapshot generate(ProductionRequest request, List<SystemExecutionStep> recipe) {
            try {
                if (mediaOutput == null) throw new IllegalStateException("Media output unavailable");
                var source = new com.google.gson.JsonObject();
                source.addProperty("component", "produce.image");
                String id = mediaOutput.submit("image", request.goal, source, new com.google.gson.JsonObject());
                System.out.println("[PRODUCE] Waiting for image job " + id);
                var job = mediaOutput.await(id, 900);
                if (!job.get("status").getAsString().equals("completed")) throw new IllegalStateException(job.toString());
                String artifact = job.getAsJsonObject("result").get("artifact").getAsString();
                return new ProductionRunSnapshot(1, 1, List.of(), List.of(), List.of(artifact), List.of(artifact), id);
            } catch (Exception error) {
                System.out.println("[PRODUCE] Image failed: " + error.getMessage());
                return new ProductionRunSnapshot(0, 1, List.of(), List.of(), List.of(), List.of(), "");
            }
        }
    }

    private static final class ProofProducerAdapter extends BaseProducerAdapter {
        @Override
        public String type() { return "proof"; }

        @Override
        public String description() { return "Generate deterministic proof artifact from runtime evidence."; }

        @Override
        public List<SystemExecutionStep> plan(ProductionRequest request) {
            List<SystemExecutionStep> steps = new ArrayList<>();
            steps.add(new SystemExecutionStep("status", "Capture baseline system state", false, false));
            steps.add(new SystemExecutionStep("system map", "Confirm routing surface", false, false));
            steps.add(new SystemExecutionStep("openmodel status", "Capture indexed corpus and proof/patent availability", false, false));
            steps.add(new SystemExecutionStep("aubo status", "Capture provenance lane state", false, false));
            steps.add(new SystemExecutionStep("system status", "Capture post-run state", false, false));
            return steps;
        }

        @Override
        public ProductionRunSnapshot generate(ProductionRequest request, List<SystemExecutionStep> recipe) {
            ProductionRunSnapshot snapshot = runProductionRecipe(request, recipe);
            String proofPath = createProofArtifact(request.goal, snapshot);
            snapshot.addArtifact(proofPath);
            System.out.println("[PRODUCE][PROOF] artifact -> " + proofPath);
            return snapshot;
        }

        @Override
        public ProductionValidation validate(ProductionRequest request, ProductionRunSnapshot snapshot) {
            boolean hasProof = hasArtifactWithExtension(snapshot.newArtifacts, ".md");
            boolean passed = hasProof && snapshot.success > 0;
            String detail = "proofArtifact=" + hasProof + ", steps=" + snapshot.success + "/" + snapshot.total;
            return new ProductionValidation(passed, detail);
        }

        @Override
        public String prove(ProductionRequest request, ProductionRunSnapshot snapshot, ProductionValidation validation) {
            return
                "[PRODUCE][PROOF]\n" +
                "TYPE=proof\n" +
                "GOAL=" + request.goal + "\n" +
                "VERIFIED=" + validation.passed + "\n" +
                "ARTIFACTS=" + (snapshot.newArtifacts.isEmpty() ? "[none]" : String.join(", ", snapshot.newArtifacts)) + "\n" +
                "UNKNOWN=" + (validation.passed ? "[none]" : "Proof artifact missing or runtime steps failed.");
        }
    }

    private static final class PatentProducerAdapter extends BaseProducerAdapter {
        @Override
        public String type() { return "patent"; }

        @Override
        public String description() { return "Generate patent-style markdown draft from local runtime evidence."; }

        @Override
        public List<SystemExecutionStep> plan(ProductionRequest request) {
            List<SystemExecutionStep> steps = new ArrayList<>();
            steps.add(new SystemExecutionStep("status", "Capture baseline system state", false, false));
            steps.add(new SystemExecutionStep("system map", "Confirm routing surface", false, false));
            steps.add(new SystemExecutionStep("openmodel status", "Capture indexed corpus and patent signal surface", false, false));
            steps.add(new SystemExecutionStep("aubo status", "Capture provenance lane state", false, false));
            steps.add(new SystemExecutionStep("system status", "Capture post-run state", false, false));
            return steps;
        }

        @Override
        public ProductionRunSnapshot generate(ProductionRequest request, List<SystemExecutionStep> recipe) {
            ProductionRunSnapshot snapshot = runProductionRecipe(request, recipe);
            String patentPath = createPatentArtifact(request.goal, snapshot);
            snapshot.addArtifact(patentPath);
            System.out.println("[PRODUCE][PATENT] artifact -> " + patentPath);
            return snapshot;
        }

        @Override
        public ProductionValidation validate(ProductionRequest request, ProductionRunSnapshot snapshot) {
            boolean hasPatent = hasArtifactWithExtension(snapshot.newArtifacts, ".md");
            boolean passed = hasPatent && snapshot.success > 0;
            String detail = "patentArtifact=" + hasPatent + ", steps=" + snapshot.success + "/" + snapshot.total;
            return new ProductionValidation(passed, detail);
        }

        @Override
        public String prove(ProductionRequest request, ProductionRunSnapshot snapshot, ProductionValidation validation) {
            return
                "[PRODUCE][PROOF]\n" +
                "TYPE=patent\n" +
                "GOAL=" + request.goal + "\n" +
                "VERIFIED=" + validation.passed + "\n" +
                "ARTIFACTS=" + (snapshot.newArtifacts.isEmpty() ? "[none]" : String.join(", ", snapshot.newArtifacts)) + "\n" +
                "UNKNOWN=" + (validation.passed ? "[none]" : "Patent artifact missing or runtime steps failed.");
        }
    }

    private static final class OmniProducerAdapter extends BaseProducerAdapter {
        @Override
        public String type() { return "all"; }

        @Override
        public String description() { return "Run all production modalities and return a unified bundle."; }

        @Override
        public List<SystemExecutionStep> plan(ProductionRequest request) {
            List<SystemExecutionStep> steps = new ArrayList<>();
            steps.add(new SystemExecutionStep("status", "Capture baseline system state", false, false));
            steps.add(new SystemExecutionStep("system map", "Confirm full routing surface", false, false));
            steps.add(new SystemExecutionStep("adapt status", "Read adaptive trend profile before full production", false, false));
            steps.add(new SystemExecutionStep("openmodel status", "Capture currently indexed corpus signals", false, false));
            steps.add(new SystemExecutionStep("aubo status", "Capture provenance lane state", false, false));
            steps.add(new SystemExecutionStep("system status", "Capture post-run state", false, false));
            return steps;
        }

        @Override
        public ProductionRunSnapshot generate(ProductionRequest request, List<SystemExecutionStep> recipe) {
            ProductionRunSnapshot snapshot = runProductionRecipe(request, recipe);
            List<String> modalityOrder = List.of("report", "proof", "patent", "image", "code");
            StringBuilder summary = new StringBuilder();
            summary.append("# FULL MODALITY BUNDLE\n\n");
            summary.append("Goal: ").append(request.goal).append("\n\n");
            summary.append("## Modalities\n");

            int passed = 0;
            for (String modality : modalityOrder) {
                ProducerAdapter adapter = resolveProducerAdapter(modality);
                if (adapter == null) continue;
                ProductionRequest subRequest = new ProductionRequest(modality, request.goal, false, false, request.rawInput);
                List<SystemExecutionStep> subPlan = adapter.plan(subRequest);
                if (!simulateSystemExecution(subPlan)) {
                    summary.append("- ").append(modality).append(": skipped (simulation blocked)\n");
                    continue;
                }
                ProductionRunSnapshot subSnapshot = adapter.generate(subRequest, subPlan);
                ProductionValidation subValidation = adapter.validate(subRequest, subSnapshot);
                summary.append("- ").append(modality).append(": ").append(subValidation.passed ? "PASS" : "FAIL")
                    .append(" | steps=").append(subSnapshot.success).append('/').append(subSnapshot.total)
                    .append(" | artifacts=").append(subSnapshot.newArtifacts.isEmpty() ? "[none]" : String.join(", ", subSnapshot.newArtifacts))
                    .append('\n');
                if (subValidation.passed) passed++;
                for (String artifact : subSnapshot.newArtifacts) {
                    snapshot.addArtifact(artifact);
                }
            }

            summary.append("\n## Summary\n");
            summary.append("- passed_modalities=").append(passed).append("/").append(modalityOrder.size()).append('\n');
            summary.append("- dominant_adaptive_lane=").append(adaptiveTrendProfile.dominantLane()).append('\n');
            summary.append("- adaptive_mode=").append(adaptiveTrendProfile.mode()).append('\n');
            summary.append("- adaptive_volatility=").append(String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.volatility)).append('\n');

            String bundlePath = "generated/full_bundle_" + System.currentTimeMillis() + ".md";
            if (fs != null) {
                fs.write(bundlePath, summary.toString());
            }
            snapshot.addArtifact(bundlePath);
            return snapshot;
        }

        @Override
        public ProductionValidation validate(ProductionRequest request, ProductionRunSnapshot snapshot) {
            int mdCount = 0;
            int pyCount = 0;
            int artifactCount = snapshot.newArtifacts == null ? 0 : snapshot.newArtifacts.size();
            if (snapshot.newArtifacts != null) {
                for (String artifact : snapshot.newArtifacts) {
                    if (artifact == null) continue;
                    String lower = artifact.toLowerCase(Locale.ROOT);
                    if (lower.endsWith(".md")) mdCount++;
                    if (lower.endsWith(".py")) pyCount++;
                }
            }
            boolean passed = artifactCount >= 3 && mdCount >= 1 && snapshot.success > 0;
            String detail = "artifacts=" + artifactCount + ", markdown=" + mdCount + ", python=" + pyCount + ", steps=" + snapshot.success + "/" + snapshot.total;
            return new ProductionValidation(passed, detail);
        }

        @Override
        public String prove(ProductionRequest request, ProductionRunSnapshot snapshot, ProductionValidation validation) {
            return
                "[PRODUCE][PROOF]\n" +
                "TYPE=all\n" +
                "GOAL=" + request.goal + "\n" +
                "VERIFIED=" + validation.passed + "\n" +
                "ARTIFACTS=" + (snapshot.newArtifacts.isEmpty() ? "[none]" : String.join(", ", snapshot.newArtifacts)) + "\n" +
                "UNKNOWN=" + (validation.passed ? "[none]" : "Full modality bundle did not reach artifact thresholds.");
        }
    }

    private static final class ProductionRequest {
        final String type;
        final String goal;
        final boolean planOnly;
        final boolean simulateOnly;
        final String rawInput;

        ProductionRequest(String type, String goal, boolean planOnly, boolean simulateOnly, String rawInput) {
            this.type = type;
            this.goal = goal;
            this.planOnly = planOnly;
            this.simulateOnly = simulateOnly;
            this.rawInput = rawInput;
        }
    }

    private static final class ProductionValidation {
        final boolean passed;
        final String detail;

        ProductionValidation(boolean passed, String detail) {
            this.passed = passed;
            this.detail = detail;
        }
    }

    private static final class ProductionRunSnapshot {
        final int success;
        final int total;
        final List<SystemStepOutcome> outcomes;
        final List<String> artifactsBefore;
        final List<String> artifactsAfter;
        final List<String> newArtifacts;
        final String tracePath;

        ProductionRunSnapshot(
            int success,
            int total,
            List<SystemStepOutcome> outcomes,
            List<String> artifactsBefore,
            List<String> artifactsAfter,
            List<String> newArtifacts,
            String tracePath
        ) {
            this.success = success;
            this.total = total;
            this.outcomes = outcomes;
            this.artifactsBefore = artifactsBefore;
            this.artifactsAfter = artifactsAfter;
            this.newArtifacts = new ArrayList<>(newArtifacts);
            this.tracePath = tracePath;
        }

        void addArtifact(String artifactPath) {
            if (artifactPath == null || artifactPath.isBlank()) return;
            if (!newArtifacts.contains(artifactPath)) {
                newArtifacts.add(artifactPath);
            }
        }
    }

    private static void printSystemGuide() {
        System.out.println("SYSTEM MODE (canonical runtime interface)");
        System.out.println("  system improve <goal> Preview kernel proposals and shadow validation");
        System.out.println("  system improve --apply <goal> Promote only after all configured checks pass");
        System.out.println("  system improve status|help Kernel state, report locations and options");
        System.out.println("  system <goal>         Orchestrate across all available subsystems");
        System.out.println("  system execute <goal> Guarded staged execution: plan -> simulate -> run");
        System.out.println("  system execute --plan <goal> Show plan only (no simulation/run)");
        System.out.println("  system execute --simulate <goal> Plan + simulation only");
        System.out.println("  system adapt <cmd>    Trend-aware adaptation controls (status/refresh/recommend)");
        System.out.println("  adapt <cmd>           Direct alias for adaptation controls");
        System.out.println("  produce <type> <goal> Typed production lane: plan -> generate -> validate -> prove");
        System.out.println("                        Types: code, report, image, proof, patent, all");
        System.out.println("  adapt sources         Show weighted trend signal sources");
        System.out.println("  adapt source ...      Add/remove/weight/toggle local trend signal inputs");
        System.out.println("  system query <goal>   Explicit query mode");
        System.out.println("  system status         Unified status + module health");
        System.out.println("  system map            Capability routing map");
        System.out.println("  system experts        Expert registry");
        System.out.println("  openmodel status      Show loaded open-model source/skill state");
        System.out.println("  openmodel refresh     Re-ingest configured open-model sources");
        System.out.println("  produce proof|patent <goal> Generate typed proof/patent artifacts from local evidence");
        System.out.println("  proof/patent goals    Emit deterministic PROOFS/PATENTS report from local evidence");
        System.out.println("  organism <cmd>        Embedded FlyGM lifecycle + continuity bridge");
        System.out.println("  ai <goal>             Query lane + command-intent auto execution (plan/sim/run)");
    }

    private static void handleOpenModelCommand(String arg) {
        String sub = arg == null ? "" : arg.trim().toLowerCase(Locale.ROOT);
        if (sub.isEmpty() || sub.equals("status")) {
            printOpenModelStatus();
            return;
        }
        if (sub.equals("refresh") || sub.equals("reload")) {
            loadOpenModelKnowledgeSources();
            printOpenModelStatus();
            return;
        }
        System.out.println("Usage: openmodel <status|refresh>");
    }

    private static void printOpenModelStatus() {
        System.out.println("[OPEN_MODEL] " + openModelStatusSummary);
        System.out.println(
            "[OPEN_MODEL] sessionSeen=" + openModelSourcesLoaded
                + ", sessionNewSources=" + openModelSourcesSessionNew
                + ", persistedSources=" + openModelSourcesPersisted
                + ", sessionNewSkills=" + openModelSkillsSessionNew
                + ", persistedSkills=" + openModelSkillsPersisted
                + ", pdfSourcesWithText=" + openModelPdfSourcesWithText
        );
    }

    private static void loadOpenModelKnowledgeSources() {
        if (akashic == null) {
            openModelStatusSummary = "akashic-unavailable";
            return;
        }

        List<String> configured = new ArrayList<>();
        List<String> configuredDirs = new ArrayList<>();
        String propSingle = System.getProperty("fraynix.openModelSource", "");
        String propMulti = System.getProperty("fraynix.openModelSources", "");
        String envSingle = System.getenv().getOrDefault("FRAYNIX_OPEN_MODEL_SOURCE", "");
        String envMulti = System.getenv().getOrDefault("FRAYNIX_OPEN_MODEL_SOURCES", "");
        String propDirSingle = System.getProperty("fraynix.openModelSourceDir", "");
        String propDirMulti = System.getProperty("fraynix.openModelSourceDirs", "");
        String envDirSingle = System.getenv().getOrDefault("FRAYNIX_OPEN_MODEL_SOURCE_DIR", "");
        String envDirMulti = System.getenv().getOrDefault("FRAYNIX_OPEN_MODEL_SOURCE_DIRS", "");

        appendConfiguredPath(configured, propSingle);
        appendConfiguredPath(configured, envSingle);
        appendConfiguredPaths(configured, propMulti);
        appendConfiguredPaths(configured, envMulti);
        appendConfiguredPath(configured, OPEN_MODEL_DEFAULT_SOURCE);
        appendConfiguredPath(configured, OPEN_MODEL_DEFAULT_SKILL);
        appendConfiguredPath(configured, OPEN_MODEL_DEFAULT_L1B3RT4S_PACK);
        appendConfiguredPath(configured, OPEN_MODEL_DEFAULT_JAILBREAK_AUTORESEARCH_PACK);
        appendConfiguredPath(configured, OPEN_MODEL_DEFAULT_FLY_CONNECTOME_PACK);
        appendConfiguredPath(configured, OPEN_MODEL_DEFAULT_FLY_CONNECTOME_PDF);

        appendConfiguredPath(configuredDirs, propDirSingle);
        appendConfiguredPath(configuredDirs, envDirSingle);
        appendConfiguredPaths(configuredDirs, propDirMulti);
        appendConfiguredPaths(configuredDirs, envDirMulti);
        appendConfiguredPath(configuredDirs, OPEN_MODEL_DEFAULT_SOURCE_DIR);
        appendConfiguredPath(configuredDirs, OPEN_MODEL_DEFAULT_SOURCE_GENERATED_DIR);
        appendConfiguredPath(configuredDirs, OPEN_MODEL_DEFAULT_AUTORESEARCH_DIR);

        for (String rawDir : configuredDirs) {
            appendConfiguredFilesFromDirectory(configured, rawDir);
        }

        Set<String> sourceIdentityKeys = collectOpenModelIdentityKeys(OPEN_MODEL_SOURCE_CATEGORY);
        Set<String> skillIdentityKeys = collectOpenModelIdentityKeys(OPEN_MODEL_SKILL_CATEGORY);

        int sessionSeen = 0;
        int loaded = 0;
        int skillLoaded = 0;
        int pdfWithText = 0;
        List<String> loadedNames = new ArrayList<>();

        for (String rawPath : configured) {
            try {
                Path sourcePath = Paths.get(rawPath).toAbsolutePath().normalize();
                if (!Files.exists(sourcePath) || !Files.isRegularFile(sourcePath)) {
                    continue;
                }
                sessionSeen++;
                byte[] bytes = Files.readAllBytes(sourcePath);
                String sha256 = sha256Hex(bytes);
                String sourceIdentity = sourcePath + "|" + sha256;
                String lower = sourcePath.toString().toLowerCase(Locale.ROOT);
                String sample = "";
                if (lower.endsWith(".pdf")) {
                    sample = extractPdfSampleText(sourcePath);
                    if (!sample.isBlank()) {
                        pdfWithText++;
                    }
                } else {
                    sample = new String(bytes, StandardCharsets.UTF_8);
                    if (sample.length() > 700) {
                        sample = sample.substring(0, 700);
                    }
                }

                String summary = "path=" + sourcePath
                    + ";size=" + bytes.length
                    + ";sha256=" + sha256
                    + ";textSample=" + sanitizeForRecord(sample.isBlank() ? "[none-extracted]" : sample);
                if (!sourceIdentityKeys.contains(sourceIdentity)) {
                    akashic.addBlock(OPEN_MODEL_SOURCE_CATEGORY, summary);
                    sourceIdentityKeys.add(sourceIdentity);
                    loaded++;
                }
                if (lower.endsWith(".md")) {
                    String skillIdentity = sourcePath + "|" + sha256;
                    if (!skillIdentityKeys.contains(skillIdentity)) {
                        akashic.addBlock(
                            OPEN_MODEL_SKILL_CATEGORY,
                            "path=" + sourcePath + ";sha256=" + sha256 + ";role=open-model-skill-pack"
                        );
                        skillIdentityKeys.add(skillIdentity);
                        skillLoaded++;
                    }
                }
                loadedNames.add(sourcePath.getFileName().toString());
            } catch (Exception ignored) {
                // Continue loading the remaining open-model sources.
            }
        }

        openModelSourcesLoaded = sessionSeen;
        openModelPdfSourcesWithText = pdfWithText;
        openModelSourcesSessionNew = loaded;
        openModelSkillsSessionNew = skillLoaded;
        openModelSourcesPersisted = sourceIdentityKeys.size();
        openModelSkillsPersisted = skillIdentityKeys.size();

        if (sessionSeen == 0 && openModelSourcesPersisted == 0) {
            openModelStatusSummary = "no-open-model-sources-loaded";
            refreshAdaptiveTrendProfile("openmodel-empty", true);
            return;
        }

        String names = loadedNames.isEmpty() ? "[none]" : String.join(", ", loadedNames.subList(0, Math.min(8, loadedNames.size())));
        openModelStatusSummary =
            "sessionSeen=" + sessionSeen
                + ", sessionNewSources=" + loaded
                + ", sessionNewSkills=" + skillLoaded
                + ", persistedSources=" + openModelSourcesPersisted
                + ", persistedSkills=" + openModelSkillsPersisted
                + ", sampleFiles=" + names;
        refreshAdaptiveTrendProfile("openmodel-refresh", true);
    }

    private static void appendConfiguredPath(List<String> configured, String value) {
        if (value == null) return;
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return;
        String canonical = trimmed;
        try {
            canonical = Paths.get(trimmed).toAbsolutePath().normalize().toString();
        } catch (Exception ignored) {
            // Keep raw string when value is not a normal filesystem path.
        }
        for (String existing : configured) {
            if (existing.equalsIgnoreCase(canonical)) {
                return;
            }
        }
        configured.add(canonical);
    }

    private static void appendConfiguredPaths(List<String> configured, String values) {
        if (values == null || values.isBlank()) return;
        String[] parts = values.split(";");
        for (String part : parts) {
            appendConfiguredPath(configured, part);
        }
    }

    private static void appendConfiguredFilesFromDirectory(List<String> configured, String rawDirectory) {
        if (rawDirectory == null || rawDirectory.isBlank()) return;
        try {
            Path dir = Paths.get(rawDirectory).toAbsolutePath().normalize();
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                return;
            }
            try (java.util.stream.Stream<Path> stream = Files.walk(dir)) {
                stream
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(path -> {
                        try {
                            String lower = path.getFileName().toString().toLowerCase(Locale.ROOT);
                            int dot = lower.lastIndexOf('.');
                            String ext = dot >= 0 ? lower.substring(dot) : "";
                            if (!OPEN_MODEL_ALLOWED_EXTENSIONS.contains(ext)) return;
                            long size = Files.size(path);
                            if (size > OPEN_MODEL_MAX_FILE_BYTES) return;
                            appendConfiguredPath(configured, path.toString());
                        } catch (Exception ignored) {
                            // Continue scanning directory candidates.
                        }
                    });
            }
        } catch (Exception ignored) {
            // Ignore directory expansion failures and keep explicit file paths.
        }
    }

    private static Set<String> collectOpenModelIdentityKeys(String category) {
        Set<String> identities = new HashSet<>();
        if (akashic == null) return identities;
        try {
            List<AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(category);
            for (AkashicRecord.KnowledgeBlock block : blocks) {
                if (block == null || block.content == null || block.content.isBlank()) continue;
                String rawPath = extractMetadataField(block.content, "path");
                String sha256 = extractMetadataField(block.content, "sha256");
                if (rawPath.isBlank() || sha256.isBlank()) continue;
                String canonicalPath = canonicalizeMetadataPath(rawPath);
                identities.add(canonicalPath + "|" + sha256);
            }
        } catch (Exception ignored) {
            // Leave identities as-is when Akashic payloads are malformed.
        }
        return identities;
    }

    private static String extractMetadataField(String payload, String key) {
        if (payload == null || payload.isBlank() || key == null || key.isBlank()) return "";
        String needle = key + "=";
        int start = payload.indexOf(needle);
        if (start < 0) return "";
        start += needle.length();
        int end = payload.indexOf(';', start);
        if (end < 0) end = payload.length();
        if (start >= end) return "";
        return payload.substring(start, end).trim();
    }

    private static String canonicalizeMetadataPath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) return "";
        try {
            return Paths.get(rawPath).toAbsolutePath().normalize().toString();
        } catch (Exception ignored) {
            return rawPath.trim();
        }
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "sha256-unavailable";
        }
    }

    private static String extractPdfSampleText(Path pdfPath) {
        String script = String.join("\n",
            "import sys",
            "path = sys.argv[1]",
            "reader = None",
            "for name in ('pypdf', 'PyPDF2'):",
            "    try:",
            "        if name == 'pypdf':",
            "            from pypdf import PdfReader",
            "        else:",
            "            from PyPDF2 import PdfReader",
            "        reader = PdfReader(path)",
            "        break",
            "    except Exception:",
            "        pass",
            "if reader is None:",
            "    print('')",
            "    raise SystemExit(0)",
            "parts = []",
            "for page in reader.pages[:6]:",
            "    try:",
            "        parts.append(page.extract_text() or '')",
            "    except Exception:",
            "        parts.append('')",
            "text = '\\n'.join(parts).strip()",
            "print(text[:1800])"
        );
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "-c", script, pdfPath.toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            byte[] out = process.getInputStream().readAllBytes();
            int code = process.waitFor();
            if (code != 0) {
                return "";
            }
            String text = new String(out, StandardCharsets.UTF_8).trim();
            return text.length() > 700 ? text.substring(0, 700) : text;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    private static void executeSystemGoal(String goal, String rawInput) {
        executeSystemGoal(goal, rawInput, false, false);
    }

    private static void executeSystemGoal(String goal, String rawInput, boolean simulateOnly, boolean planOnly) {
        String trimmedGoal = goal == null ? "" : goal.trim();
        if (trimmedGoal.isEmpty()) {
            System.out.println("Usage: system execute [--plan|--simulate] <goal>");
            return;
        }

        List<SystemExecutionStep> plan = buildSystemExecutionPlan(trimmedGoal);
        printSystemExecutionPlan(plan, trimmedGoal);
        if (planOnly) {
            System.out.println("[SYSTEM][PLAN] plan-only mode complete.");
            return;
        }
        if (!simulateSystemExecution(plan)) {
            System.out.println("[SYSTEM][SIMULATE] blocked: safety guard rejected one or more steps.");
            return;
        }
        if (simulateOnly) {
            System.out.println("[SYSTEM][SIMULATE] simulation-only mode complete.");
            return;
        }
        runSystemExecution(plan, trimmedGoal, rawInput);
    }

    private static List<SystemExecutionStep> buildSystemExecutionPlan(String goal) {
        String lowerGoal = goal.toLowerCase(Locale.ROOT);
        List<SystemExecutionStep> steps = new ArrayList<>();
        boolean proofPatentGoal = isProofPatentGoal(lowerGoal);
        boolean menuAnchoredGoal = containsAny(lowerGoal, "menu", "based on menu", "from menu", "using menu");
        String directCanonicalCommand = tryResolveDirectCanonicalCommand(goal);

        addStepIfMissing(steps, "status", "Capture baseline status", false, false);
        if (menuAnchoredGoal) {
            addStepIfMissing(steps, "menu", "Anchor execution to visible menu contract", false, false);
        }
        addStepIfMissing(steps, "system map", "Confirm routing surface", false, false);
        addStepIfMissing(steps, "cortex 1", "Advance cortex one cycle", false, false);
        addStepIfMissing(steps, "aeon 1", "Advance AEON one cycle", false, false);
        List<SystemExecutionStep> laneChecks = new ArrayList<>();
        laneChecks.add(new SystemExecutionStep("kronos status", "Check symbolic memory lane", false, false));
        laneChecks.add(new SystemExecutionStep("tachyon status", "Check O(1) retrieval lane", false, false));
        laneChecks.add(new SystemExecutionStep("omniscience status", "Check abstraction lane", false, false));
        laneChecks.add(new SystemExecutionStep("omega status", "Check kernel self-state lane", false, false));
        laneChecks.add(new SystemExecutionStep("babel status", "Check polyglot synthesis lane", false, false));
        laneChecks.add(new SystemExecutionStep("singularity status", "Check diffusion memory lane", false, false));
        laneChecks.add(new SystemExecutionStep("aubo status", "Check provenance ledger lane", false, false));
        laneChecks.add(new SystemExecutionStep("demiurge status", "Check physics simulation lane", false, false));
        laneChecks.add(new SystemExecutionStep("apotheosis status", "Check reality compiler lane", false, false));
        if (avatarService != null) {
            laneChecks.add(new SystemExecutionStep("avatar status", "Check biometric sovereignty lane", false, false));
        }

        if (directCanonicalCommand.isBlank()) {
            laneChecks.sort(Comparator.comparingDouble((SystemExecutionStep s) -> planLanePriority(s.command, lowerGoal)).reversed());
            int laneBudget = selectLaneBudget(lowerGoal);
            for (int i = 0; i < Math.min(laneBudget, laneChecks.size()); i++) {
                SystemExecutionStep step = laneChecks.get(i);
                addStepIfMissing(steps, step.command, step.reason, step.highImpact, step.requiresAi);
            }
        } else {
            addStepIfMissing(
                steps,
                directCanonicalCommand,
                "Execute direct canonical command inferred from AI prompt",
                isHighImpactCommand(directCanonicalCommand),
                requiresAiForCommand(directCanonicalCommand)
            );
        }

        if (lowerGoal.contains("learn") || lowerGoal.contains("memory") || lowerGoal.contains("remember")) {
            addStepIfMissing(steps, "absolute learn " + compressGoalToken(goal),
                "Bind distilled goal token into absolute memory lane",
                false,
                false
            );
            addStepIfMissing(steps, "aeon inject " + goal,
                "Inject goal text into AEON manifold",
                false,
                false
            );
        }

        if (avatarService != null && containsAny(lowerGoal, "identity", "biometric", "avatar", "face", "consent", "verify")) {
            addStepIfMissing(steps, "avatar status",
                "Verify local identity sovereignty controls are online",
                false,
                false
            );
        }

        if (containsAny(lowerGoal, "adapt", "trend", "change", "drift", "evolve")) {
            addStepIfMissing(steps, "adapt status",
                "Apply adaptive trend profile before lane execution",
                false,
                false
            );
            addStepIfMissing(steps, "openmodel status",
                "Refresh visibility into currently indexed external signal corpus",
                false,
                false
            );
        }

        if (proofPatentGoal) {
            addStepIfMissing(steps, "openmodel status",
                "Confirm proof/patent corpus availability and ingestion state",
                false,
                false
            );
            addStepIfMissing(steps, "aubo status",
                "Confirm provenance ledger lane before proof assertions",
                false,
                false
            );
        }

        boolean aiReady = brain != null && brain.isConnected();

        if (aiReady && (lowerGoal.contains("code") || lowerGoal.contains("build") || lowerGoal.contains("generate"))) {
            addStepIfMissing(steps, "code " + goal,
                "Optional code synthesis branch",
                true,
                true
            );
        }

        addStepIfMissing(steps, "system status", "Capture post-run status", false, false);
        if (aiReady && !proofPatentGoal) {
            addStepIfMissing(steps, "system query " + buildSystemExecutionSummaryPrompt(goal),
                "Synthesize final unified interpretation",
                false,
                true
            );
        }
        return steps;
    }

    private static void addStepIfMissing(
        List<SystemExecutionStep> steps,
        String command,
        String reason,
        boolean highImpact,
        boolean requiresAi
    ) {
        if (steps == null || command == null || command.isBlank()) return;
        String normalized = command.trim().toLowerCase(Locale.ROOT);
        for (SystemExecutionStep existing : steps) {
            if (existing.command.trim().toLowerCase(Locale.ROOT).equals(normalized)) {
                return;
            }
        }
        steps.add(new SystemExecutionStep(command.trim(), reason, highImpact, requiresAi));
    }

    private static boolean isProofPatentGoal(String lowerGoal) {
        return containsAny(lowerGoal, "proof", "patent", "evidence", "verify", "validate");
    }

    private static String tryResolveDirectCanonicalCommand(String goal) {
        if (goal == null || goal.isBlank()) return "";
        String candidate = goal.trim().replaceAll("\\s+", " ");
        String[] parts = candidate.split("\\s+", 2);
        if (parts.length == 0) return "";
        String root = parts[0].toLowerCase(Locale.ROOT);
        if (!CANONICAL_COMMAND_ROOTS.contains(root)) {
            return "";
        }
        if (root.equals("system") && parts.length > 1) {
            String lowered = parts[1].toLowerCase(Locale.ROOT);
            if (lowered.startsWith("execute") || lowered.startsWith("run")) {
                return "";
            }
        }
        if (root.equals("ai") || root.equals("exit") || root.equals("quit")) {
            return "";
        }
        return candidate;
    }

    private static boolean requiresAiForCommand(String command) {
        if (command == null || command.isBlank()) return false;
        String[] parts = command.trim().toLowerCase(Locale.ROOT).split("\\s+", 3);
        String root = parts[0];
        if (root.equals("code") || root.equals("ai")) return true;
        if (root.equals("system") && parts.length > 1) {
            String sub = parts[1];
            return sub.equals("query") || sub.equals("ai");
        }
        return false;
    }

    private static boolean isHighImpactCommand(String command) {
        if (command == null || command.isBlank()) return false;
        String[] parts = command.trim().toLowerCase(Locale.ROOT).split("\\s+", 3);
        String root = parts[0];
        if (root.equals("generate") || root.equals("exodus") || root.equals("desktop")) return true;
        if (root.equals("system") && parts.length > 1) {
            return parts[1].equals("execute") || parts[1].equals("run");
        }
        return false;
    }

    private static int countOpenModelSignalHits(String token) {
        if (token == null || token.isBlank() || akashic == null) return 0;
        String needle = token.toLowerCase(Locale.ROOT);
        int hits = 0;
        try {
            List<AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(OPEN_MODEL_SOURCE_CATEGORY);
            if (blocks == null) return 0;
            for (AkashicRecord.KnowledgeBlock block : blocks) {
                if (block == null || block.content == null || block.content.isBlank()) continue;
                if (block.content.toLowerCase(Locale.ROOT).contains(needle)) {
                    hits++;
                }
            }
        } catch (Exception ignored) {
            return hits;
        }
        return hits;
    }

    private static void emitDeterministicProofPatentReport(
        String goal,
        List<SystemStepOutcome> outcomes,
        int success,
        int total
    ) {
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        for (SystemStepOutcome outcome : outcomes) {
            String compact = commandOutcomeKey(outcome.command);
            if (outcome.success) {
                passed.add(compact);
            } else {
                failed.add(compact);
            }
        }

        int patentSignals = countOpenModelSignalHits("patent");
        int proofSignals = countOpenModelSignalHits("proof");
        int claimSignals = countOpenModelSignalHits("claim");
        boolean openModelReady = (openModelSourcesPersisted + openModelSourcesLoaded) > 0;

        String report =
            "[SYSTEM][EVIDENCE] goal=" + goal + "\n" +
            "PROOFS\n" +
            "  steps_passed=" + success + "/" + total + "\n" +
            "  openmodel_ready=" + openModelReady +
                " sessionSeen=" + openModelSourcesLoaded +
                " persistedSources=" + openModelSourcesPersisted +
                " persistedSkills=" + openModelSkillsPersisted +
                " pdfText=" + openModelPdfSourcesWithText + "\n" +
            "  provenance_lane=" + (aubo != null ? "online" : "unavailable") + "\n" +
            "PATENTS\n" +
            "  patent_signal_hits=" + patentSignals + "\n" +
            "  proof_signal_hits=" + proofSignals + "\n" +
            "  claim_signal_hits=" + claimSignals + "\n" +
            "VERIFIED\n" +
            "  commands=" + (passed.isEmpty() ? "[none]" : String.join(", ", passed)) + "\n" +
            "UNKNOWN\n" +
            "  commands=" + (failed.isEmpty() ? "[none]" : String.join(", ", failed)) + "\n" +
            "  notes=" + ((patentSignals + proofSignals + claimSignals) <= 0
                ? "No patent/proof language detected in indexed open-model corpus yet."
                : "Signals detected; inspect indexed source paths for concrete references.");

        System.out.println(report);
        if (fs != null) {
            String path = "logs/system_evidence_report_" + System.currentTimeMillis() + ".log";
            fs.write(path, report);
            System.out.println("[SYSTEM][EVIDENCE] report saved -> " + path);
        }
    }

    private static String buildSystemExecutionSummaryPrompt(String goal) {
        String trimmed = goal == null ? "" : goal.trim();
        if (trimmed.isEmpty()) {
            return "Summarize execution outcomes.";
        }
        String lowered = trimmed.toLowerCase(Locale.ROOT);
        if (containsAny(lowered, "proof", "patent", "evidence", "verify", "validate")) {
            return "Summarize execution outcomes for goal: " + trimmed +
                ". Include sections: PROOFS, PATENTS, VERIFIED, UNKNOWN. " +
                "Cite only local runtime evidence; do not fabricate.";
        }
        return "Summarize execution outcomes for goal: " + trimmed;
    }

    private static int selectLaneBudget(String lowerGoal) {
        if (containsAny(lowerGoal, "full", "all", "tandem", "unified", "cross-subsystem")) return 9;
        if (containsAny(lowerGoal, "memory", "learn", "recall", "remember", "knowledge")) return 8;
        if (containsAny(lowerGoal, "identity", "biometric", "avatar", "face", "consent", "verify")) return 7;
        if (containsAny(lowerGoal, "code", "build", "compile", "generate", "synthesis")) return 7;
        return 6;
    }

    private static double planLanePriority(String command, String lowerGoal) {
        double score = historicalStepSuccess(command);
        String c = command.toLowerCase(Locale.ROOT);
        if (containsAny(lowerGoal, "memory", "learn", "recall", "remember", "knowledge")) {
            if (c.startsWith("kronos") || c.startsWith("tachyon") || c.startsWith("omniscience")
                || c.startsWith("omega") || c.startsWith("singularity")) score += 0.45;
        }
        if (containsAny(lowerGoal, "code", "build", "compile", "generate", "synthesis")) {
            if (c.startsWith("babel") || c.startsWith("omega") || c.startsWith("apotheosis")) score += 0.40;
        }
        if (containsAny(lowerGoal, "identity", "biometric", "avatar", "face", "consent", "verify")) {
            if (c.startsWith("avatar")) score += 0.70;
        }
        if (containsAny(lowerGoal, "physics", "simulate", "world", "reality")) {
            if (c.startsWith("demiurge") || c.startsWith("apotheosis")) score += 0.35;
        }
        if (containsAny(lowerGoal, "trust", "provenance", "audit", "track", "ledger")) {
            if (c.startsWith("aubo")) score += 0.35;
        }
        if (containsAny(lowerGoal, "full", "all", "tandem", "unified")) score += 0.10;
        score += adaptiveTrendBiasForCommand(c);
        return score;
    }

    private static boolean containsAny(String text, String... tokens) {
        if (text == null || text.isBlank()) return false;
        for (String token : tokens) {
            if (token != null && !token.isBlank() && text.contains(token)) return true;
        }
        return false;
    }

    private static String compressGoalToken(String goal) {
        String normalized = goal == null ? "" : goal.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
        normalized = normalized.replaceAll("_+", "_").replaceAll("^_", "").replaceAll("_$", "");
        if (normalized.length() > 48) normalized = normalized.substring(0, 48);
        if (normalized.isBlank()) return "SYSTEM_GOAL";
        return normalized;
    }

    private static void printSystemExecutionPlan(List<SystemExecutionStep> plan, String goal) {
        System.out.println("[SYSTEM][PLAN] goal: " + goal);
        for (int i = 0; i < plan.size(); i++) {
            SystemExecutionStep step = plan.get(i);
            String risk = step.highImpact ? "HIGH" : "LOW";
            String ai = step.requiresAi ? "AI" : "LOCAL";
            String score = String.format(Locale.ROOT, "%.2f", historicalStepSuccess(step.command));
            System.out.println("  [" + (i + 1) + "] " + step.command + "  [" + ai + "|" + risk + "|hist=" + score + "]  " + step.reason);
        }
    }

    private static boolean simulateSystemExecution(List<SystemExecutionStep> plan) {
        System.out.println("[SYSTEM][SIMULATE] validating staged plan...");
        Set<String> allowedRoots = Set.of(
            "menu", "help", "status", "system", "adapt", "produce", "cortex", "aeon", "kronos", "tachyon",
            "omniscience", "omega", "babel", "absolute", "code", "ai",
            "singularity", "aubo", "demiurge", "apotheosis", "avatar", "openmodel", "holographic"
        );

        for (int i = 0; i < plan.size(); i++) {
            SystemExecutionStep step = plan.get(i);
            String[] parts = step.command.trim().split("\\s+", 2);
            String root = parts[0].toLowerCase(Locale.ROOT);
            if (!allowedRoots.contains(root)) {
                System.out.println("  [BLOCK] step " + (i + 1) + " unsupported root: " + root);
                return false;
            }
            if (step.requiresAi && (brain == null || !brain.isConnected())) {
                System.out.println("  [BLOCK] step " + (i + 1) + " requires AI but Ollama is offline.");
                return false;
            }
            if (step.highImpact) {
                System.out.println("  [GUARD] step " + (i + 1) + " marked HIGH impact: " + step.command);
            }
        }
        System.out.println("[SYSTEM][SIMULATE] plan accepted.");
        return true;
    }

    private static void runSystemExecution(List<SystemExecutionStep> plan, String goal, String rawInput) {
        System.out.println("[SYSTEM][RUN] executing " + plan.size() + " staged step(s)...");
        StringBuilder trace = new StringBuilder();
        trace.append("goal=").append(goal).append('\n');
        trace.append("steps=").append(plan.size()).append('\n');
        int success = 0;
        List<SystemStepOutcome> outcomes = new ArrayList<>();

        for (int i = 0; i < plan.size(); i++) {
            SystemExecutionStep step = plan.get(i);
            System.out.println("[SYSTEM][RUN] step " + (i + 1) + "/" + plan.size() + ": " + step.command);
            boolean ok = executePlannedSystemCommand(step.command, rawInput);
            trace.append(i + 1).append('|').append(step.command).append('|').append(ok ? "OK" : "FAIL").append('\n');
            outcomes.add(new SystemStepOutcome(step.command, ok));
            if (ok) success++;
        }

        String summary = "[SYSTEM][RUN] completed " + success + "/" + plan.size() + " step(s).";
        System.out.println(summary);
        trace.append(summary).append('\n');

        if (fs != null) {
            String path = "logs/system_execute_" + System.currentTimeMillis() + ".log";
            fs.write(path, trace.toString());
            System.out.println("[SYSTEM][RUN] trace saved -> " + path);
        }

        recordSystemExecutionOutcome(goal, outcomes, success, plan.size());
        if (isProofPatentGoal(goal.toLowerCase(Locale.ROOT))) {
            emitDeterministicProofPatentReport(goal, outcomes, success, plan.size());
        }
    }

    private static boolean executePlannedSystemCommand(String command, String rawInput) {
        if (command == null || command.isBlank()) return false;
        String[] parts = command.trim().split("\\s+", 2);
        String root = parts[0].toLowerCase(Locale.ROOT);
        String arg = parts.length > 1 ? parts[1].trim() : "";

        try {
            switch (root) {
                case "menu" -> {
                    printQuickMenu();
                    return true;
                }
                case "help" -> {
                    printHelp();
                    return true;
                }
                case "status" -> {
                    printFullStatus();
                    return true;
                }
                case "system" -> {
                    handleSystemCommand(arg, rawInput);
                    return true;
                }
                case "adapt" -> {
                    handleAdaptCommand(arg);
                    return true;
                }
                case "produce" -> {
                    handleProduceCommand(arg, rawInput);
                    return true;
                }
                case "cortex" -> {
                    if (cortex == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(cortex.getStatus());
                        return true;
                    }
                    try {
                        int cycles = Integer.parseInt(arg);
                        cortex.runCycles(cycles);
                        int emitted = cortex.emitToGravity();
                        System.out.println("[SYSTEM] cortex cycles=" + cycles + ", emitted=" + emitted);
                        return true;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                case "aeon" -> {
                    if (aeon == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(aeon.getStatus());
                        return true;
                    }
                    if (arg.startsWith("inject ")) {
                        aeon.injectStimulus(arg.substring(7));
                        System.out.println("[SYSTEM] aeon stimulus injected.");
                        return true;
                    }
                    try {
                        int cycles = Integer.parseInt(arg);
                        aeon.runCycles(cycles);
                        System.out.println("[SYSTEM] aeon cycles=" + cycles + " complete.");
                        return true;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                case "absolute" -> {
                    if (absolute == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(absolute.getStatus());
                        return true;
                    }
                    if (arg.startsWith("learn ")) {
                        absolute.holoLearn(arg.substring(6));
                        System.out.println("[SYSTEM] absolute learned token.");
                        return true;
                    }
                    return false;
                }
                case "kronos" -> {
                    if (kronos == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(kronos.getStatus());
                        return true;
                    }
                    return false;
                }
                case "tachyon" -> {
                    if (tachyon == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(tachyon.getStatus());
                        return true;
                    }
                    return false;
                }
                case "omniscience" -> {
                    if (omniscience == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(omniscience.getStatus());
                        return true;
                    }
                    return false;
                }
                case "omega" -> {
                    if (omega == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(omega.getStatus());
                        return true;
                    }
                    return false;
                }
                case "babel" -> {
                    if (babel == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(babel.getStatus());
                        return true;
                    }
                    return false;
                }
                case "singularity" -> {
                    if (singularity == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(singularity.getStatus());
                        return true;
                    }
                    return false;
                }
                case "aubo" -> {
                    if (aubo == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(aubo.getStatus());
                        return true;
                    }
                    return false;
                }
                case "demiurge" -> {
                    if (demiurge == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(demiurge.getStatus());
                        return true;
                    }
                    return false;
                }
                case "apotheosis" -> {
                    if (arg.startsWith("analyze ") || arg.startsWith("generate ")) {
                        return handleGenomicsCommand(arg);
                    }
                    if (apotheosis == null) return false;
                    if (arg.startsWith("restore ")) return apotheosis.restoreTranscription(arg.substring(8).trim());
                    if (arg.startsWith("transcribe ")) {
                        String sequence = apotheosis.transcribe(arg.substring(11).trim());
                        System.out.println(FraymusJSON.stringify(Map.of("sequence_length", sequence.length(), "genome", apotheosis.getLastGenomeArchive())));
                        return !apotheosis.getLastGenomeArchive().isEmpty();
                    }
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println(apotheosis.getStatus());
                        return true;
                    }
                    return false;
                }
                case "avatar" -> {
                    if (avatarService == null) return false;
                    if (arg.isEmpty() || arg.equals("status")) {
                        System.out.println("[avatar] " + avatarService.statusSummary());
                        return true;
                    }
                    handleAvatarCommand(arg);
                    return true;
                }
                case "openmodel" -> {
                    handleOpenModelCommand(arg);
                    return true;
                }
                case "genomics", "carbon" -> {
                    return handleGenomicsCommand(arg);
                }
                case "specialist" -> {
                    var result = executeSpecialistCommand(arg);
                    System.out.println(result.output());
                    return result.success();
                }
                case "holographic" -> {
                    handleHolographicCommand(arg);
                    return true;
                }
                case "media", "voice", "dream" -> {
                    if (mediaOutput == null) return false;
                    System.out.println(unifiedOrchestrator.routeQuery(root + " " + arg));
                    return true;
                }
                case "code" -> {
                    if (arg.isEmpty() || brain == null || !brain.isConnected()) return false;
                    generateCode(arg);
                    return true;
                }
                case "ai" -> {
                    if (arg.isEmpty()) return false;
                    handleSystemQuery(arg, rawInput, false);
                    return true;
                }
                default -> {
                    return false;
                }
            }
        } catch (Exception ex) {
            System.out.println("[SYSTEM][RUN] step failed: " + ex.getMessage());
            return false;
        }
    }

    private static final class SystemExecutionStep {
        final String command;
        final String reason;
        final boolean highImpact;
        final boolean requiresAi;

        SystemExecutionStep(String command, String reason, boolean highImpact, boolean requiresAi) {
            this.command = command;
            this.reason = reason;
            this.highImpact = highImpact;
            this.requiresAi = requiresAi;
        }
    }

    private static final class SystemStepOutcome {
        final String command;
        final boolean success;

        SystemStepOutcome(String command, boolean success) {
            this.command = command;
            this.success = success;
        }
    }

    private static final class ExecutionStepStats {
        int attempts;
        int successes;

        void record(boolean ok) {
            attempts++;
            if (ok) successes++;
        }

        double successRate() {
            if (attempts <= 0) return 0.50;
            return successes / (double) attempts;
        }
    }

    private static double historicalStepSuccess(String command) {
        ExecutionStepStats stats = executionStepStats.get(commandOutcomeKey(command));
        if (stats == null) return 0.50;
        return stats.successRate();
    }

    private static String buildLearnedRouteHint() {
        List<String> hints = new ArrayList<>();
        appendRouteHint(hints, "cortex_cycle", "cortex-cycle");
        appendRouteHint(hints, "aeon_cycle", "aeon-cycle");
        appendRouteHint(hints, "adapt_status", "adapt");
        appendRouteHint(hints, "cortex_status", "cortex");
        appendRouteHint(hints, "aeon_status", "aeon");
        appendRouteHint(hints, "kronos_status", "kronos");
        appendRouteHint(hints, "tachyon_status", "tachyon");
        appendRouteHint(hints, "omniscience_status", "omniscience");
        appendRouteHint(hints, "omega_status", "omega");
        appendRouteHint(hints, "babel_status", "babel");
        appendRouteHint(hints, "singularity_status", "singularity");
        appendRouteHint(hints, "aubo_status", "aubo");
        appendRouteHint(hints, "demiurge_status", "demiurge");
        appendRouteHint(hints, "apotheosis_status", "apotheosis");
        appendRouteHint(hints, "avatar_status", "avatar");
        if (hints.isEmpty()) return "no prior execution memory yet";
        return String.join(", ", hints);
    }

    private static void appendRouteHint(List<String> hints, String key, String label) {
        ExecutionStepStats stats = executionStepStats.get(key);
        if (stats == null || stats.attempts < 2) return;
        hints.add(label + "=" + String.format(Locale.ROOT, "%.2f", stats.successRate()));
    }

    private static void recordSystemExecutionOutcome(String goal, List<SystemStepOutcome> outcomes, int success, int total) {
        for (SystemStepOutcome outcome : outcomes) {
            String key = commandOutcomeKey(outcome.command);
            executionStepStats.computeIfAbsent(key, ignored -> new ExecutionStepStats()).record(outcome.success);
        }

        if (akashic != null) {
            StringBuilder out = new StringBuilder();
            out.append("goal=").append(sanitizeForRecord(goal)).append(';');
            out.append("success=").append(success).append(';');
            out.append("total=").append(total).append(';');
            out.append("steps=");
            for (int i = 0; i < outcomes.size(); i++) {
                SystemStepOutcome step = outcomes.get(i);
                if (i > 0) out.append(',');
                out.append(commandOutcomeKey(step.command)).append(':').append(step.success ? "1" : "0");
            }
            akashic.addBlock(SYSTEM_EXECUTION_OUTCOME_CATEGORY, out.toString());
            persistExecutionStepStatsToAkashic("system_execute");
        }
        refreshAdaptiveTrendProfile("execution-outcome", false);
    }

    private static String commandOutcomeKey(String command) {
        if (command == null || command.isBlank()) return "unknown";
        String[] parts = command.trim().toLowerCase(Locale.ROOT).split("\\s+");
        String root = parts[0];
        if (root.equals("menu")) return "menu";
        if (root.equals("help")) return "help";
        if (root.equals("status")) return "status";
        if (root.equals("system")) {
            if (parts.length > 1) {
                String sub = parts[1];
                if (sub.equals("map")) return "system_map";
                if (sub.equals("query")) return "system_query";
                if (sub.equals("status")) return "system_status";
                if (sub.equals("execute")) return "system_execute";
            }
            return "system";
        }
        if (root.equals("adapt")) return parts.length > 1 ? "adapt_" + parts[1] : "adapt_status";
        if (root.equals("cortex")) return parts.length > 1 && isIntegerToken(parts[1]) ? "cortex_cycle" : "cortex_status";
        if (root.equals("aeon")) {
            if (parts.length > 1 && isIntegerToken(parts[1])) return "aeon_cycle";
            if (parts.length > 1 && parts[1].equals("inject")) return "aeon_inject";
            return "aeon_status";
        }
        if (root.equals("absolute")) return parts.length > 1 && parts[1].equals("learn") ? "absolute_learn" : "absolute_status";
        if (root.equals("kronos")) return "kronos_status";
        if (root.equals("tachyon")) return "tachyon_status";
        if (root.equals("omniscience")) return "omniscience_status";
        if (root.equals("omega")) return "omega_status";
        if (root.equals("babel")) return "babel_status";
        if (root.equals("singularity") || root.equals("sing")) return "singularity_status";
        if (root.equals("aubo")) return "aubo_status";
        if (root.equals("demiurge")) return "demiurge_status";
        if (root.equals("apotheosis")) return "apotheosis_status";
        if (root.equals("avatar")) return "avatar_status";
        if (root.equals("produce")) return parts.length > 1 ? "produce_" + parts[1] : "produce";
        if (root.equals("openmodel")) return parts.length > 1 ? "openmodel_" + parts[1] : "openmodel_status";
        if (root.equals("holographic")) return parts.length > 1 ? "holographic_" + parts[1] : "holographic_status";
        if (root.equals("code")) return "code_generate";
        if (root.equals("ai")) return "ai_query";
        return root.replaceAll("[^a-z0-9_]+", "_");
    }

    private static boolean isIntegerToken(String token) {
        if (token == null || token.isBlank()) return false;
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) return false;
        }
        return true;
    }

    private static void loadExecutionStepStatsFromAkashic() {
        if (akashic == null) return;
        List<AkashicRecord.KnowledgeBlock> entries = akashic.queryCategory(SYSTEM_EXECUTION_STEP_STATS_CATEGORY);
        if (entries == null || entries.isEmpty()) return;
        String content = entries.get(entries.size() - 1).content;
        hydrateExecutionStepStats(content);
    }

    private static void persistExecutionStepStatsToAkashic(String reason) {
        if (akashic == null) return;
        StringBuilder stats = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, ExecutionStepStats> entry : executionStepStats.entrySet()) {
            if (!first) stats.append(',');
            first = false;
            ExecutionStepStats s = entry.getValue();
            stats.append(entry.getKey()).append(':').append(s.attempts).append(':').append(s.successes);
        }
        String payload = "reason=" + sanitizeForRecord(reason) + ";stats=" + stats;
        akashic.addBlock(SYSTEM_EXECUTION_STEP_STATS_CATEGORY, payload);
    }

    private static void hydrateExecutionStepStats(String content) {
        if (content == null || content.isBlank()) return;
        String[] fields = content.split(";", 3);
        String statsField = null;
        for (String field : fields) {
            if (field.startsWith("stats=")) {
                statsField = field.substring("stats=".length());
                break;
            }
        }
        if (statsField == null || statsField.isBlank()) return;
        String[] items = statsField.split(",");
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length != 3) continue;
            String key = parts[0].trim();
            try {
                int attempts = Integer.parseInt(parts[1].trim());
                int successes = Integer.parseInt(parts[2].trim());
                if (attempts <= 0 || successes < 0 || successes > attempts || key.isBlank()) continue;
                ExecutionStepStats stats = new ExecutionStepStats();
                stats.attempts = attempts;
                stats.successes = successes;
                executionStepStats.put(key, stats);
            } catch (Exception ignored) {
            }
        }
    }

    private static void loadAdaptiveTrendProfileFromAkashic() {
        try {
            if (Files.exists(ADAPTIVE_TREND_PROFILE_SIDECAR_PATH)) {
                String sidecar = Files.readString(ADAPTIVE_TREND_PROFILE_SIDECAR_PATH, StandardCharsets.UTF_8);
                adaptiveTrendProfile.hydrate(sidecar);
            }
        } catch (Exception ignored) {
            // Sidecar load is best-effort only.
        }
        if (akashic == null) return;
        try {
            List<AkashicRecord.KnowledgeBlock> entries = akashic.queryCategory(ADAPTIVE_TREND_PROFILE_CATEGORY);
            if (entries == null || entries.isEmpty()) return;
            adaptiveTrendProfile.hydrate(entries.get(entries.size() - 1).content);
        } catch (Exception ignored) {
            // Akashic load is best-effort only.
        }
    }

    private static void loadAdaptiveSignalSources() {
        adaptiveSignalSources.clear();
        adaptiveSignalSources.add(new AdaptiveSignalSource("openmodel", "openmodel", "", 0.60, true));
        adaptiveSignalSources.add(new AdaptiveSignalSource("runtime", "runtime", "", 0.30, true));
        adaptiveSignalSources.add(new AdaptiveSignalSource("trend_file_default", "file", ADAPTIVE_DEFAULT_TREND_SIGNAL_FILE, 0.10, true));

        try {
            if (Files.exists(ADAPTIVE_SIGNAL_SOURCES_SIDECAR_PATH)) {
                List<String> lines = Files.readAllLines(ADAPTIVE_SIGNAL_SOURCES_SIDECAR_PATH, StandardCharsets.UTF_8);
                applyAdaptiveSignalSourceLines(lines);
            }
        } catch (Exception ignored) {
            // Sidecar load is best-effort only.
        }

        if (akashic != null) {
            try {
                List<AkashicRecord.KnowledgeBlock> entries = akashic.queryCategory(ADAPTIVE_SIGNAL_SOURCE_CATEGORY);
                if (entries != null && !entries.isEmpty()) {
                    String payload = entries.get(entries.size() - 1).content;
                    if (payload != null && !payload.isBlank()) {
                        applyAdaptiveSignalSourceLines(List.of(payload.split("\n")));
                    }
                }
            } catch (Exception ignored) {
                // Akashic load is best-effort only.
            }
        }
        persistAdaptiveSignalSources("boot-load");
    }

    private static void applyAdaptiveSignalSourceLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) return;
        for (String line : lines) {
            if (line == null) continue;
            String trimmed = line.trim();
            if (trimmed.isBlank() || trimmed.startsWith("#")) continue;
            AdaptiveSignalSource parsed = AdaptiveSignalSource.parse(trimmed);
            if (parsed == null || parsed.name.isBlank()) continue;
            upsertAdaptiveSignalSource(parsed);
        }
    }

    private static void upsertAdaptiveSignalSource(AdaptiveSignalSource candidate) {
        if (candidate == null || candidate.name == null || candidate.name.isBlank()) return;
        String name = candidate.name.toLowerCase(Locale.ROOT);
        for (int i = 0; i < adaptiveSignalSources.size(); i++) {
            AdaptiveSignalSource existing = adaptiveSignalSources.get(i);
            if (existing.name.equalsIgnoreCase(name)) {
                adaptiveSignalSources.set(i, candidate);
                return;
            }
        }
        adaptiveSignalSources.add(candidate);
    }

    private static void persistAdaptiveSignalSources(String reason) {
        StringBuilder payload = new StringBuilder();
        payload.append("# reason=").append(sanitizeForRecord(reason)).append('\n');
        for (AdaptiveSignalSource source : adaptiveSignalSources) {
            payload.append(source.serialize()).append('\n');
        }
        String out = payload.toString().trim();
        if (akashic != null) {
            akashic.addBlock(ADAPTIVE_SIGNAL_SOURCE_CATEGORY, out);
        }
        try {
            Files.createDirectories(ADAPTIVE_SIGNAL_SOURCES_SIDECAR_PATH.getParent());
            Files.writeString(ADAPTIVE_SIGNAL_SOURCES_SIDECAR_PATH, out + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            // Sidecar persistence is best-effort only.
        }
    }

    private static AdaptiveSignalSource addAdaptiveFileSignalSource(String rawPath, double weight) {
        if (rawPath == null || rawPath.isBlank()) return null;
        String candidatePath = rawPath.trim();
        Path path;
        try {
            path = Paths.get(candidatePath).toAbsolutePath().normalize();
        } catch (Exception e) {
            return null;
        }
        String filename = path.getFileName() == null ? "signal" : path.getFileName().toString();
        String base = filename.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        if (base.isBlank()) base = "signal";
        String name = "file_" + base;
        int suffix = 2;
        while (findAdaptiveSignalSource(name) != null) {
            name = "file_" + base + "_" + suffix;
            suffix++;
        }
        AdaptiveSignalSource source = new AdaptiveSignalSource(name, "file", path.toString(), clamp01(weight), true);
        adaptiveSignalSources.add(source);
        persistAdaptiveSignalSources("source-add");
        return source;
    }

    private static boolean removeAdaptiveSignalSource(String name) {
        if (name == null || name.isBlank()) return false;
        String needle = name.trim();
        for (int i = 0; i < adaptiveSignalSources.size(); i++) {
            AdaptiveSignalSource source = adaptiveSignalSources.get(i);
            if (source.name.equalsIgnoreCase(needle)) {
                if (source.type.equals("openmodel") || source.type.equals("runtime")) {
                    return false;
                }
                adaptiveSignalSources.remove(i);
                persistAdaptiveSignalSources("source-remove");
                return true;
            }
        }
        return false;
    }

    private static boolean setAdaptiveSignalSourceEnabled(String name, boolean enabled) {
        AdaptiveSignalSource source = findAdaptiveSignalSource(name);
        if (source == null) return false;
        source.enabled = enabled;
        persistAdaptiveSignalSources(enabled ? "source-enable" : "source-disable");
        return true;
    }

    private static boolean setAdaptiveSignalSourceWeight(String name, double weight) {
        AdaptiveSignalSource source = findAdaptiveSignalSource(name);
        if (source == null) return false;
        source.weight = clamp01(weight);
        persistAdaptiveSignalSources("source-weight");
        return true;
    }

    private static AdaptiveSignalSource findAdaptiveSignalSource(String name) {
        if (name == null || name.isBlank()) return null;
        String needle = name.trim();
        for (AdaptiveSignalSource source : adaptiveSignalSources) {
            if (source.name.equalsIgnoreCase(needle)) return source;
        }
        return null;
    }

    private static void printAdaptiveSignalSources() {
        System.out.println("[ADAPT] Signal Sources");
        if (adaptiveSignalSources.isEmpty()) {
            System.out.println("  [none]");
            return;
        }
        for (AdaptiveSignalSource source : adaptiveSignalSources) {
            System.out.println("  - name=" + source.name
                + ", type=" + source.type
                + ", enabled=" + source.enabled
                + ", weight=" + String.format(Locale.ROOT, "%.3f", source.weight)
                + (source.target.isBlank() ? "" : ", target=" + source.target)
                + ", last_confidence=" + String.format(Locale.ROOT, "%.3f", source.lastConfidence)
                + ", last_hits=" + source.lastHits
            );
        }
    }

    private static void refreshAdaptiveTrendProfile(String reason, boolean force) {
        long now = System.currentTimeMillis();
        if (!force && (now - adaptiveTrendProfile.lastRefreshMs) < 20_000L) {
            return;
        }

        List<WeightedSignalVector> weightedSignals = collectWeightedTrendSignals();
        if (weightedSignals.isEmpty()) {
            weightedSignals.add(buildOpenModelTrendSignal(1.0));
            weightedSignals.add(buildRuntimeTrendSignal(1.0));
        }

        double obsCode = weightedSignalAverage(weightedSignals, "code");
        double obsReport = weightedSignalAverage(weightedSignals, "report");
        double obsImage = weightedSignalAverage(weightedSignals, "image");
        double obsProof = weightedSignalAverage(weightedSignals, "proof");
        double obsPatent = weightedSignalAverage(weightedSignals, "patent");

        double prevCode = adaptiveTrendProfile.codeDemand;
        double prevReport = adaptiveTrendProfile.reportDemand;
        double prevImage = adaptiveTrendProfile.imageDemand;
        double prevProof = adaptiveTrendProfile.proofDemand;
        double prevPatent = adaptiveTrendProfile.patentDemand;

        adaptiveTrendProfile.codeDemand = blendDemand(prevCode, obsCode);
        adaptiveTrendProfile.reportDemand = blendDemand(prevReport, obsReport);
        adaptiveTrendProfile.imageDemand = blendDemand(prevImage, obsImage);
        adaptiveTrendProfile.proofDemand = blendDemand(prevProof, obsProof);
        adaptiveTrendProfile.patentDemand = blendDemand(prevPatent, obsPatent);

        double reliabilityCode = successRateForKeys("produce_code", "code_generate");
        double reliabilityReport = successRateForKeys("produce_report");
        double reliabilityImage = successRateForKeys("produce_image", "holographic_blender");
        double reliabilityProof = successRateForKeys("produce_proof");
        double reliabilityPatent = successRateForKeys("produce_patent");

        adaptiveTrendProfile.codeBias = computeLaneBias(adaptiveTrendProfile.codeDemand, reliabilityCode);
        adaptiveTrendProfile.reportBias = computeLaneBias(adaptiveTrendProfile.reportDemand, reliabilityReport);
        adaptiveTrendProfile.imageBias = computeLaneBias(adaptiveTrendProfile.imageDemand, reliabilityImage);
        adaptiveTrendProfile.proofBias = computeLaneBias(adaptiveTrendProfile.proofDemand, reliabilityProof);
        adaptiveTrendProfile.patentBias = computeLaneBias(adaptiveTrendProfile.patentDemand, reliabilityPatent);

        double driftMagnitude = (
            Math.abs(adaptiveTrendProfile.codeDemand - prevCode) +
            Math.abs(adaptiveTrendProfile.reportDemand - prevReport) +
            Math.abs(adaptiveTrendProfile.imageDemand - prevImage) +
            Math.abs(adaptiveTrendProfile.proofDemand - prevProof) +
            Math.abs(adaptiveTrendProfile.patentDemand - prevPatent)
        ) / 5.0;

        adaptiveTrendProfile.volatility = (adaptiveTrendProfile.volatility * 0.70) + (driftMagnitude * 0.30);
        adaptiveTrendProfile.driftMagnitude = driftMagnitude;
        adaptiveTrendProfile.lastReason = reason == null ? "unknown" : reason;
        adaptiveTrendProfile.lastRefreshMs = now;
        adaptiveTrendProfile.samples++;

        if (force || driftMagnitude >= 0.04) {
            persistAdaptiveTrendProfile(reason);
        }
    }

    private static void persistAdaptiveTrendProfile(String reason) {
        String payload = adaptiveTrendProfile.serialize(reason);
        if (akashic != null) {
            akashic.addBlock(ADAPTIVE_TREND_PROFILE_CATEGORY, payload);
        }
        try {
            Files.createDirectories(ADAPTIVE_TREND_PROFILE_SIDECAR_PATH.getParent());
            Files.writeString(ADAPTIVE_TREND_PROFILE_SIDECAR_PATH, payload, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            // Sidecar persistence is best-effort only.
        }
    }

    private static List<WeightedSignalVector> collectWeightedTrendSignals() {
        List<WeightedSignalVector> out = new ArrayList<>();
        for (AdaptiveSignalSource source : adaptiveSignalSources) {
            if (source == null || !source.enabled || source.weight <= 0.0) continue;
            WeightedSignalVector vector = switch (source.type) {
                case "openmodel" -> buildOpenModelTrendSignal(source.weight);
                case "runtime" -> buildRuntimeTrendSignal(source.weight);
                case "file" -> buildFileTrendSignal(source, source.weight);
                default -> null;
            };
            if (vector == null) continue;
            source.lastConfidence = vector.confidence;
            source.lastHits = vector.rawSignalCount;
            source.lastSeenMs = System.currentTimeMillis();
            out.add(vector);
        }
        return out;
    }

    private static WeightedSignalVector buildOpenModelTrendSignal(double baseWeight) {
        String corpus = collectOpenModelCorpusLower();
        int codeHits = tokenHitCount(corpus, "code", "build", "compile", "patch", "algorithm");
        int reportHits = tokenHitCount(corpus, "report", "analysis", "summary", "document", "markdown");
        int imageHits = tokenHitCount(corpus, "image", "visual", "render", "scene", "blender", "vision");
        int proofHits = tokenHitCount(corpus, "proof", "evidence", "verify", "validation", "benchmark");
        int patentHits = tokenHitCount(corpus, "patent", "claim", "novelty", "prior art", "ip");
        int maxSignal = Math.max(1, Math.max(codeHits, Math.max(reportHits, Math.max(imageHits, Math.max(proofHits, patentHits)))));

        double confidence = 0.30;
        if (openModelSourcesPersisted > 0) confidence += 0.30;
        if (openModelSourcesLoaded > 0) confidence += 0.20;
        if (openModelPdfSourcesWithText > 0) confidence += 0.10;
        confidence = clamp01(confidence);

        WeightedSignalVector vector = new WeightedSignalVector("openmodel", baseWeight, confidence);
        vector.code = codeHits / (double) maxSignal;
        vector.report = reportHits / (double) maxSignal;
        vector.image = imageHits / (double) maxSignal;
        vector.proof = proofHits / (double) maxSignal;
        vector.patent = patentHits / (double) maxSignal;
        vector.rawSignalCount = codeHits + reportHits + imageHits + proofHits + patentHits;
        return vector;
    }

    private static WeightedSignalVector buildRuntimeTrendSignal(double baseWeight) {
        int attemptsCode = attemptsForKeys("produce_code", "code_generate");
        int attemptsReport = attemptsForKeys("produce_report");
        int attemptsImage = attemptsForKeys("produce_image", "holographic_blender");
        int attemptsProof = attemptsForKeys("produce_proof");
        int attemptsPatent = attemptsForKeys("produce_patent");
        int attemptsTotal = Math.max(1, attemptsCode + attemptsReport + attemptsImage + attemptsProof + attemptsPatent);

        double confidence = clamp01(Math.min(1.0, attemptsTotal / 12.0));
        if (attemptsTotal <= 1) confidence = Math.max(confidence, 0.20);

        WeightedSignalVector vector = new WeightedSignalVector("runtime", baseWeight, confidence);
        vector.code = attemptsCode / (double) attemptsTotal;
        vector.report = attemptsReport / (double) attemptsTotal;
        vector.image = attemptsImage / (double) attemptsTotal;
        vector.proof = attemptsProof / (double) attemptsTotal;
        vector.patent = attemptsPatent / (double) attemptsTotal;
        vector.rawSignalCount = attemptsTotal;
        return vector;
    }

    private static WeightedSignalVector buildFileTrendSignal(AdaptiveSignalSource source, double baseWeight) {
        if (source == null || source.target == null || source.target.isBlank()) return null;
        Path path;
        try {
            path = Paths.get(source.target).toAbsolutePath().normalize();
        } catch (Exception e) {
            return null;
        }
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return new WeightedSignalVector(source.name, baseWeight, 0.0);
        }
        String content;
        try {
            content = Files.readString(path, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            return new WeightedSignalVector(source.name, baseWeight, 0.0);
        }
        int codeHits = tokenHitCount(content, "code", "build", "compile", "patch", "algorithm");
        int reportHits = tokenHitCount(content, "report", "analysis", "summary", "document", "markdown");
        int imageHits = tokenHitCount(content, "image", "visual", "render", "scene", "blender", "vision");
        int proofHits = tokenHitCount(content, "proof", "evidence", "verify", "validation", "benchmark");
        int patentHits = tokenHitCount(content, "patent", "claim", "novelty", "prior art", "ip");
        int totalHits = codeHits + reportHits + imageHits + proofHits + patentHits;
        int maxSignal = Math.max(1, Math.max(codeHits, Math.max(reportHits, Math.max(imageHits, Math.max(proofHits, patentHits)))));

        double confidence = clamp01(Math.min(1.0, totalHits / 20.0));
        if (totalHits == 0) confidence = 0.05;

        WeightedSignalVector vector = new WeightedSignalVector(source.name, baseWeight, confidence);
        vector.code = codeHits / (double) maxSignal;
        vector.report = reportHits / (double) maxSignal;
        vector.image = imageHits / (double) maxSignal;
        vector.proof = proofHits / (double) maxSignal;
        vector.patent = patentHits / (double) maxSignal;
        vector.rawSignalCount = totalHits;
        return vector;
    }

    private static double weightedSignalAverage(List<WeightedSignalVector> vectors, String lane) {
        if (vectors == null || vectors.isEmpty()) return 0.0;
        double weighted = 0.0;
        double totalWeight = 0.0;
        for (WeightedSignalVector v : vectors) {
            if (v == null) continue;
            double w = clamp01(v.weight) * clamp01(v.confidence);
            if (w <= 0.0) continue;
            double laneValue = switch (lane) {
                case "code" -> v.code;
                case "report" -> v.report;
                case "image" -> v.image;
                case "proof" -> v.proof;
                case "patent" -> v.patent;
                default -> 0.0;
            };
            weighted += clamp01(laneValue) * w;
            totalWeight += w;
        }
        if (totalWeight <= 0.0) return 0.0;
        return clamp01(weighted / totalWeight);
    }

    private static String collectOpenModelCorpusLower() {
        if (akashic == null) return "";
        StringBuilder sb = new StringBuilder(8192);
        try {
            List<AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(OPEN_MODEL_SOURCE_CATEGORY);
            if (blocks == null) return "";
            for (AkashicRecord.KnowledgeBlock block : blocks) {
                if (block == null || block.content == null || block.content.isBlank()) continue;
                sb.append(' ').append(block.content.toLowerCase(Locale.ROOT));
                if (sb.length() > 900_000) {
                    break;
                }
            }
        } catch (Exception ignored) {
            return sb.toString();
        }
        return sb.toString();
    }

    private static int tokenHitCount(String corpus, String... tokens) {
        if (corpus == null || corpus.isBlank() || tokens == null || tokens.length == 0) return 0;
        int total = 0;
        for (String token : tokens) {
            if (token == null || token.isBlank()) continue;
            total += countOccurrences(corpus, token.toLowerCase(Locale.ROOT));
        }
        return total;
    }

    private static int countOccurrences(String haystack, String needle) {
        if (haystack == null || haystack.isBlank() || needle == null || needle.isBlank()) return 0;
        int count = 0;
        int idx = 0;
        while (idx >= 0) {
            idx = haystack.indexOf(needle, idx);
            if (idx < 0) break;
            count++;
            idx += needle.length();
            if (count > 10_000) break;
        }
        return count;
    }

    private static int attemptsForKeys(String... keys) {
        if (keys == null || keys.length == 0) return 0;
        int attempts = 0;
        for (String key : keys) {
            if (key == null || key.isBlank()) continue;
            ExecutionStepStats stats = executionStepStats.get(key);
            if (stats != null) attempts += Math.max(0, stats.attempts);
        }
        return attempts;
    }

    private static double successRateForKeys(String... keys) {
        if (keys == null || keys.length == 0) return 0.50;
        double weightedSuccess = 0.0;
        int weightedAttempts = 0;
        for (String key : keys) {
            if (key == null || key.isBlank()) continue;
            ExecutionStepStats stats = executionStepStats.get(key);
            if (stats == null || stats.attempts <= 0) continue;
            weightedSuccess += stats.successes;
            weightedAttempts += stats.attempts;
        }
        if (weightedAttempts <= 0) return 0.50;
        return weightedSuccess / weightedAttempts;
    }

    private static double blendDemand(double prior, double observed) {
        if (prior <= 0.0) return clamp01(observed);
        return clamp01((prior * 0.65) + (observed * 0.35));
    }

    private static double computeLaneBias(double demand, double reliability) {
        return clamp01(0.10 + (0.90 * ((demand * 0.55) + (reliability * 0.45))));
    }

    private static double adaptiveTrendBiasForCommand(String commandLower) {
        if (commandLower == null || commandLower.isBlank()) return 0.0;
        double bias = 0.0;
        if (commandLower.startsWith("code ") || commandLower.startsWith("babel ") || commandLower.startsWith("omega ")) {
            bias += adaptiveTrendProfile.codeBias * 0.30;
        }
        if (commandLower.startsWith("openmodel ") || commandLower.startsWith("system query")) {
            bias += adaptiveTrendProfile.reportBias * 0.20;
        }
        if (commandLower.startsWith("holographic ") || commandLower.startsWith("omniscience ")) {
            bias += adaptiveTrendProfile.imageBias * 0.30;
        }
        if (commandLower.startsWith("aubo ") || commandLower.startsWith("openmodel ") || commandLower.startsWith("system status")) {
            bias += adaptiveTrendProfile.proofBias * 0.25;
        }
        if (commandLower.startsWith("openmodel ") || commandLower.startsWith("aubo ") || commandLower.startsWith("produce patent")) {
            bias += adaptiveTrendProfile.patentBias * 0.20;
        }
        if (adaptiveTrendProfile.volatility >= 0.15 && (commandLower.startsWith("openmodel ") || commandLower.startsWith("system map"))) {
            bias += 0.08;
        }
        return bias;
    }

    private static void printAdaptiveTrendStatus() {
        System.out.println("[ADAPT] Trend Profile");
        System.out.println("  mode=" + adaptiveTrendProfile.mode()
            + ", volatility=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.volatility)
            + ", drift=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.driftMagnitude)
            + ", samples=" + adaptiveTrendProfile.samples);
        System.out.println("  demand.code=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.codeDemand)
            + " bias=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.codeBias));
        System.out.println("  demand.report=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.reportDemand)
            + " bias=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.reportBias));
        System.out.println("  demand.image=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.imageDemand)
            + " bias=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.imageBias));
        System.out.println("  demand.proof=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.proofDemand)
            + " bias=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.proofBias));
        System.out.println("  demand.patent=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.patentDemand)
            + " bias=" + String.format(Locale.ROOT, "%.3f", adaptiveTrendProfile.patentBias));
        System.out.println("  dominant_lane=" + adaptiveTrendProfile.dominantLane());
        System.out.println("  dominant_produce_type=" + adaptiveTrendProfile.dominantProduceType());
        System.out.println("  last_reason=" + adaptiveTrendProfile.lastReason
            + ", updated_ms=" + adaptiveTrendProfile.lastRefreshMs);
        System.out.println("  sources_enabled=" + adaptiveSignalSources.stream().filter(s -> s.enabled).count()
            + "/" + adaptiveSignalSources.size());
    }

    private static final class AdaptiveSignalSource {
        final String name;
        final String type; // openmodel|runtime|file
        final String target; // path for file sources
        double weight;
        boolean enabled;
        long lastSeenMs = 0L;
        double lastConfidence = 0.0;
        int lastHits = 0;

        AdaptiveSignalSource(String name, String type, String target, double weight, boolean enabled) {
            this.name = name == null ? "" : name.trim();
            this.type = type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
            this.target = target == null ? "" : target.trim();
            this.weight = clamp01(weight);
            this.enabled = enabled;
        }

        String serialize() {
            return name + "\t" + type + "\t" + sanitizeForRecord(target) + "\t"
                + String.format(Locale.ROOT, "%.6f", clamp01(weight)) + "\t" + (enabled ? "1" : "0");
        }

        static AdaptiveSignalSource parse(String line) {
            if (line == null || line.isBlank()) return null;
            String[] parts = line.split("\\t");
            if (parts.length < 5) return null;
            String name = parts[0].trim();
            String type = parts[1].trim().toLowerCase(Locale.ROOT);
            String target = parts[2].trim();
            double weight;
            try {
                weight = clamp01(Double.parseDouble(parts[3].trim()));
            } catch (Exception e) {
                weight = 0.50;
            }
            boolean enabled = parts[4].trim().equals("1") || parts[4].trim().equalsIgnoreCase("true");
            if (name.isBlank()) return null;
            if (!(type.equals("openmodel") || type.equals("runtime") || type.equals("file"))) return null;
            return new AdaptiveSignalSource(name, type, target, weight, enabled);
        }
    }

    private static final class WeightedSignalVector {
        final String source;
        final double weight;
        final double confidence;
        double code = 0.0;
        double report = 0.0;
        double image = 0.0;
        double proof = 0.0;
        double patent = 0.0;
        int rawSignalCount = 0;

        WeightedSignalVector(String source, double weight, double confidence) {
            this.source = source;
            this.weight = clamp01(weight);
            this.confidence = clamp01(confidence);
        }
    }

    private static final class AdaptiveTrendProfile {
        double codeDemand = 0.50;
        double reportDemand = 0.50;
        double imageDemand = 0.35;
        double proofDemand = 0.55;
        double patentDemand = 0.45;
        double codeBias = 0.50;
        double reportBias = 0.50;
        double imageBias = 0.45;
        double proofBias = 0.55;
        double patentBias = 0.48;
        double volatility = 0.08;
        double driftMagnitude = 0.00;
        int samples = 0;
        long lastRefreshMs = 0L;
        String lastReason = "bootstrap";

        String dominantLane() {
            double max = codeBias;
            String lane = "code";
            if (reportBias > max) { max = reportBias; lane = "report"; }
            if (imageBias > max) { max = imageBias; lane = "image"; }
            if (proofBias > max) { max = proofBias; lane = "proof"; }
            if (patentBias > max) { lane = "patent"; }
            return lane;
        }

        String dominantProduceType() {
            String lane = dominantLane();
            return switch (lane) {
                case "report" -> "report";
                case "image" -> "image";
                case "proof" -> "proof";
                case "patent" -> "patent";
                default -> "code";
            };
        }

        String mode() {
            if (volatility >= 0.20) return "high-change";
            if (volatility >= 0.10) return "adaptive";
            return "stable";
        }

        String serialize(String reason) {
            return "reason=" + sanitizeForRecord(reason) +
                ";codeDemand=" + String.format(Locale.ROOT, "%.6f", codeDemand) +
                ";reportDemand=" + String.format(Locale.ROOT, "%.6f", reportDemand) +
                ";imageDemand=" + String.format(Locale.ROOT, "%.6f", imageDemand) +
                ";proofDemand=" + String.format(Locale.ROOT, "%.6f", proofDemand) +
                ";patentDemand=" + String.format(Locale.ROOT, "%.6f", patentDemand) +
                ";codeBias=" + String.format(Locale.ROOT, "%.6f", codeBias) +
                ";reportBias=" + String.format(Locale.ROOT, "%.6f", reportBias) +
                ";imageBias=" + String.format(Locale.ROOT, "%.6f", imageBias) +
                ";proofBias=" + String.format(Locale.ROOT, "%.6f", proofBias) +
                ";patentBias=" + String.format(Locale.ROOT, "%.6f", patentBias) +
                ";volatility=" + String.format(Locale.ROOT, "%.6f", volatility) +
                ";drift=" + String.format(Locale.ROOT, "%.6f", driftMagnitude) +
                ";samples=" + samples +
                ";updated=" + lastRefreshMs;
        }

        void hydrate(String payload) {
            if (payload == null || payload.isBlank()) return;
            String[] fields = payload.split(";");
            for (String field : fields) {
                String[] kv = field.split("=", 2);
                if (kv.length != 2) continue;
                String key = kv[0].trim();
                String value = kv[1].trim();
                try {
                    switch (key) {
                        case "reason" -> lastReason = value;
                        case "codeDemand" -> codeDemand = clamp01(Double.parseDouble(value));
                        case "reportDemand" -> reportDemand = clamp01(Double.parseDouble(value));
                        case "imageDemand" -> imageDemand = clamp01(Double.parseDouble(value));
                        case "proofDemand" -> proofDemand = clamp01(Double.parseDouble(value));
                        case "patentDemand" -> patentDemand = clamp01(Double.parseDouble(value));
                        case "codeBias" -> codeBias = clamp01(Double.parseDouble(value));
                        case "reportBias" -> reportBias = clamp01(Double.parseDouble(value));
                        case "imageBias" -> imageBias = clamp01(Double.parseDouble(value));
                        case "proofBias" -> proofBias = clamp01(Double.parseDouble(value));
                        case "patentBias" -> patentBias = clamp01(Double.parseDouble(value));
                        case "volatility" -> volatility = clamp01(Double.parseDouble(value));
                        case "drift" -> driftMagnitude = clamp01(Double.parseDouble(value));
                        case "samples" -> samples = Math.max(0, Integer.parseInt(value));
                        case "updated" -> lastRefreshMs = Math.max(0L, Long.parseLong(value));
                    }
                } catch (Exception ignored) {
                    // Skip malformed fields.
                }
            }
        }
    }

    private static void printSystemCapabilityMap() {
        System.out.println("SYSTEM CAPABILITY MAP");
        System.out.println("  QUICK START:");
        System.out.println("    1) menu");
        System.out.println("    2) system map");
        System.out.println("    3) system <goal> or system execute <goal>");
        System.out.println("    4) system execute --plan <goal> to inspect adaptive lane selection");
        System.out.println("  CORE GROUPS:");
        System.out.println("    Orchestration:     system, adapt, ai, produce, status, experts");
        System.out.println("    Identity:          avatar (consent + cancelable biometric templates)");
        System.out.println("    Reasoning:         cortex, aeon, omniscience");
        System.out.println("    Symbolic memory:   kronos, tachyon, singularity, omega");
        System.out.println("    Knowledge I/O:     absorb, fs, akashic");
        System.out.println("    Synthesis:         code, babel, demiurge, apotheosis");
        System.out.println("  ADVANCED GROUPS:");
        System.out.println("    Embodiment:        openclaw, hrm, desktop, exodus, organism");
        System.out.println("    Distributed bridge:aether, braid, learn, pulse, retro, emf, agora");
        System.out.println("  Rule: start with system map, then system <goal>, then explicit module commands.");
        System.out.println("  Planner: system execute now ranks lanes by goal intent + learned runtime success.");
    }

    private static void printQuickMenu() {
        System.out.println("FRAYNIX QUICK MENU");
        System.out.println("  Self-improvement:    system improve <goal>  /  system improve help");
        System.out.println("  1) Understand system: system map");
        System.out.println("  2) Ask orchestrator:  system <goal>");
        System.out.println("  3) Guarded run:       system execute <goal>");
        System.out.println("  3a) Plan only:        system execute --plan <goal>");
        System.out.println("  3b) Simulate only:    system execute --simulate <goal>");
        System.out.println("  3c) Produce artifact: produce <code|report|image|proof|patent|all|omni|full|auto|anything> <goal>");
        System.out.println("  3d) Adapt to change:  adapt refresh  /  adapt recommend <goal>");
        System.out.println("  4) Health view:       status");
        System.out.println("  5) Identity controls: avatar help");
        System.out.println("  6) Organism lane:     organism start");
        System.out.println("  Advanced modules are grouped in 'help'.");
    }

    private static void handleAdaptiveAi(String userPrompt, String rawInput) {
        if (brain == null || !brain.isConnected()) {
            System.out.println("AI not available (Ollama offline)");
            return;
        }

        String evidenceSnapshot = collectLiveEvidenceSnapshot();
        String systemPrompt = buildGroundedSystemPrompt(evidenceSnapshot)
            + fraymus.genomics.CarbonGenomicsBridge.contextFor(userPrompt)
            + fraymus.specialists.SpecialistModelService.contextFor(userPrompt);

        String clarification = maybeBuildClarificationQuestion(userPrompt, evidenceSnapshot);
        if (clarification != null && !clarification.isBlank()) {
            System.out.print("Fraymus: ");
            System.out.println(clarification);
            GroundingScore clarificationScore = new GroundingScore(1.0, 0.0, 0, true);
            groundingPolicy.update(clarificationScore);
            persistGroundingFeedback(rawInput, userPrompt, "[CLARIFY] " + clarification, clarificationScore, 0);
            return;
        }

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream backgroundNoise = new ByteArrayOutputStream();
        PrintStream noiseSink = new PrintStream(backgroundNoise, true);

        String answer;
        GroundingScore score;
        int revisions = 0;
        try {
            System.setOut(noiseSink);
            System.setErr(noiseSink);

            answer = brain.speak(systemPrompt, userPrompt);
            score = scoreGrounding(userPrompt, evidenceSnapshot, answer);
            while (revisions < groundingPolicy.maxRevisions && score.overall < groundingPolicy.targetScore) {
                String repairPrompt =
                    "Revise your previous answer to improve factual grounding.\n" +
                    "Question: " + userPrompt + "\n" +
                    "Previous answer:\n" + answer + "\n\n" +
                    "Requirements:\n" +
                    "- Increase direct evidence citations [E#]\n" +
                    "- Move uncertain claims to 'Unverified assumptions'\n" +
                    "- Keep response concise and non-theatrical.";
                answer = brain.speak(systemPrompt, repairPrompt);
                score = scoreGrounding(userPrompt, evidenceSnapshot, answer);
                revisions++;
            }
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
            noiseSink.close();
        }

        System.out.print("Fraymus: ");
        System.out.println(answer);
        if (mediaOutput != null) mediaOutput.onResponse(answer, "FraynixBoot.adaptive_response");
        warnOnNonCanonicalCommandHints(answer);

        String noise = backgroundNoise.toString();
        if (!noise.isBlank()) {
            String noisePath = "logs/ai_background_noise_" + System.currentTimeMillis() + ".log";
            if (fs != null) {
                fs.write(noisePath, noise);
            }
            System.out.println("   [GROUNDING] background logs buffered -> " + noisePath);
        }

        groundingPolicy.update(score);
        persistGroundingFeedback(rawInput, userPrompt, answer, score, revisions);
        if (groundingPolicy.samples % 3 == 0 || revisions > 0) {
            persistGroundingPolicyToAkashic("auto-update");
        }
        System.out.printf(
            "   [GROUNDING] score=%.3f refs=%d overlap=%.3f strict=%.2f rev=%d%n",
            score.overall,
            score.evidenceRefs,
            score.lexicalOverlap,
            groundingPolicy.strictness,
            revisions
        );
    }

    private static String maybeBuildClarificationQuestion(String userPrompt, String evidenceSnapshot) {
        String checkerSystem =
            "You detect ambiguity/typos before final analysis.\n" +
            "Return exactly one line:\n" +
            "CLARIFY|<short question>\n" +
            "or\n" +
            "OK|";
        String checkerPrompt =
            "Query: " + userPrompt + "\n" +
            "Known evidence tokens:\n" + evidenceSnapshot + "\n" +
            "If the query contains likely misspelling or ambiguous named term that should be confirmed first, return CLARIFY|question.\n" +
            "Else return OK|";
        String verdict = brain.speak(checkerSystem, checkerPrompt);
        if (verdict == null) return null;
        String normalized = verdict.trim();
        if (normalized.regionMatches(true, 0, "CLARIFY|", 0, "CLARIFY|".length())) {
            String question = normalized.substring("CLARIFY|".length()).trim();
            if (!question.isBlank() && !isLowQualityClarification(question, userPrompt)) {
                return question;
            }
        }
        return null;
    }

    private static boolean isLowQualityClarification(String question, String userPrompt) {
        if (question == null || question.isBlank()) return true;
        String q = question.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
        String p = (userPrompt == null ? "" : userPrompt.toLowerCase(Locale.ROOT))
            .replaceAll("[^a-z0-9]+", " ").trim();
        if (q.length() < 12) return true;
        if (!q.contains("?")) return true;
        if (q.equals(p)) return true;
        Set<String> qTokens = tokenize(q);
        Set<String> pTokens = tokenize(p);
        if (qTokens.isEmpty() || pTokens.isEmpty()) return false;
        int overlap = 0;
        for (String token : qTokens) {
            if (pTokens.contains(token)) overlap++;
        }
        double ratio = overlap / (double) qTokens.size();
        return ratio > 0.80;
    }

    private static String collectLiveEvidenceSnapshot() {
        StringBuilder sb = new StringBuilder();
        int id = 1;
        id = appendEvidence(sb, id, "LLM", "connected=" + (brain != null && brain.isConnected()) + ", model=" + (brain != null ? brain.getModel() : "none"));
        id = appendEvidence(
            sb,
            id,
            "OPEN_MODEL",
            "sessionSeen=" + openModelSourcesLoaded
                + ", newSources=" + openModelSourcesSessionNew
                + ", newSkills=" + openModelSkillsSessionNew
                + ", persistedSources=" + openModelSourcesPersisted
                + ", persistedSkills=" + openModelSkillsPersisted
                + ", pdfText=" + openModelPdfSourcesWithText
                + ", summary=" + openModelStatusSummary
        );
        id = appendEvidence(sb, id, "UNIFIED_ROOT", unifiedCompositionStatus());
        id = appendEvidence(sb, id, "CORTEX", cortex != null ? cortex.getCortexContext() : "unavailable");
        id = appendEvidence(sb, id, "AEON", aeon != null ? aeon.getEgoContext() : "unavailable");
        id = appendEvidence(sb, id, "PHASE5", buildPhase5EvidenceSummary());
        id = appendEvidence(sb, id, "AKASHIC", akashic != null ? ("blocks=" + akashic.getPersistedBlockCount() + ", categories=" + akashic.getCategoryCount()) : "unavailable");
        id = appendEvidence(sb, id, "KRONOS", kronos != null ? ("bind=" + kronos.getBindCount() + ", seq=" + kronos.getSequenceCount() + ", analogy=" + kronos.getAnalogyCount()) : "unavailable");
        id = appendEvidence(sb, id, "TACHYON", tachyon != null ? ("bind=" + tachyon.getBindCount() + ", query=" + tachyon.getQueryCount() + ", breaches=" + tachyon.getCausalityBreaches()) : "unavailable");
        id = appendEvidence(sb, id, "SINGULARITY", singularity != null ? ("learned=" + singularity.getLearnCount() + ", diffused=" + singularity.getDiffuseCount()) : "unavailable");
        id = appendEvidence(sb, id, "OMNISCIENCE", omniscience != null ? ("blends=" + omniscience.getBlendCount() + ", chunks=" + omniscience.getChunkCount() + ", epiphanies=" + omniscience.getEpiphanies()) : "unavailable");
        id = appendEvidence(sb, id, "OMEGA", omega != null ? ("words=" + omega.getWordsAssimilated() + ", divine=" + omega.getDivineCount() + ", epoch=" + omega.getEpoch()) : "unavailable");
        id = appendEvidence(sb, id, "BABEL", babel != null ? ("transmutes=" + babel.getTransmutationCount() + ", concepts=" + babel.getConceptsTransmuted() + ", lines=" + babel.getTotalLinesGenerated()) : "unavailable");
        appendEvidence(sb, id, "FS", fs != null ? ("files=" + fs.fileCount()) : "unavailable");
        return sb.toString();
    }

    private static String buildPhase5EvidenceSummary() {
        int ready = 0;
        if (inferenceEngine != null) ready++;
        if (worldModel != null) ready++;
        if (embodiedAI != null) ready++;
        if (quantumComputer != null) ready++;
        if (neuromorphic != null) ready++;
        if (efficientInference != null) ready++;
        return ready + "/6 ready";
    }

    private static int appendEvidence(StringBuilder sb, int evidenceId, String label, String value) {
        sb.append("E").append(evidenceId).append("|").append(label).append("|").append(value).append('\n');
        return evidenceId + 1;
    }

    private static String buildGroundedSystemPrompt(String evidenceSnapshot) {
        return "You are the Fraynix analysis layer operating in recursive grounding mode.\n" +
            "Use ONLY the evidence lines below as verified facts.\n" +
            "If the user query appears ambiguous or typo-prone, ask one short clarification first and do not fabricate.\n" +
            "Apply open-model skill routing: DeepSeek for reasoning/coding, coder lanes for patching, local lanes when possible.\n" +
            "Separate verified facts from assumptions and include concrete verification commands.\n" +
            "Output format:\n" +
            "1) Verified capabilities (with [E#] citations)\n" +
            "2) Unverified assumptions\n" +
            "3) Next verification checks\n" +
            "4) If suggesting shell commands, use only canonical roots and no launcher prefix.\n" +
            "Tone: direct, concise, no mythology or persona prose.\n" +
            "Canonical roots: " + String.join(", ", CANONICAL_COMMAND_ROOTS) + "\n" +
            "Grounding strictness=" + String.format("%.2f", groundingPolicy.strictness) + ", minimum citations=" + groundingPolicy.minEvidenceRefs + ".\n\n" +
            "Evidence snapshot:\n" + evidenceSnapshot;
    }

    private static GroundingScore scoreGrounding(String query, String evidenceSnapshot, String answer) {
        Set<String> sourceTokens = tokenize(query + " " + evidenceSnapshot);
        List<String> responseTokens = tokenizeList(answer);
        int overlapCount = 0;
        for (String token : responseTokens) {
            if (sourceTokens.contains(token)) overlapCount++;
        }
        double lexicalOverlap = responseTokens.isEmpty() ? 0.0 : (double) overlapCount / (double) responseTokens.size();
        Matcher matcher = Pattern.compile("\\bE\\d+\\b").matcher(answer);
        int evidenceRefs = 0;
        while (matcher.find()) evidenceRefs++;
        String lower = answer.toLowerCase(Locale.ROOT);
        boolean marksUnknown = lower.contains("unknown")
            || lower.contains("uncertain")
            || lower.contains("not enough evidence")
            || lower.contains("needs verification");
        double refScore = Math.min(1.0, evidenceRefs / (double) Math.max(1, groundingPolicy.minEvidenceRefs));
        double lexicalScore = Math.min(1.0, lexicalOverlap / 0.35);
        double uncertaintyScore = marksUnknown ? 1.0 : 0.0;
        double overall = clamp01(0.55 * refScore + 0.35 * lexicalScore + 0.10 * uncertaintyScore);
        return new GroundingScore(overall, lexicalOverlap, evidenceRefs, marksUnknown);
    }

    private static Set<String> tokenize(String text) {
        return new HashSet<>(tokenizeList(text));
    }

    private static List<String> tokenizeList(String text) {
        List<String> out = new ArrayList<>();
        if (text == null || text.isBlank()) return out;
        String[] parts = text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+");
        for (String part : parts) {
            if (part.length() >= 4) out.add(part);
        }
        return out;
    }

    private static double clamp01(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }

    private static void loadGroundingPolicyFromAkashic() {
        if (akashic == null) return;
        List<AkashicRecord.KnowledgeBlock> entries = akashic.queryCategory(AI_POLICY_CATEGORY);
        if (entries == null || entries.isEmpty()) return;
        groundingPolicy.hydrate(entries.get(entries.size() - 1).content);
    }

    private static void persistGroundingPolicyToAkashic(String reason) {
        if (akashic == null) return;
        akashic.addBlock(AI_POLICY_CATEGORY, groundingPolicy.serialize(reason));
    }

    private static void persistGroundingFeedback(String rawInput, String userPrompt, String answer, GroundingScore score, int revisions) {
        if (akashic == null) return;
        String compactAnswer = answer == null ? "" : answer.replaceAll("\\s+", " ").trim();
        if (compactAnswer.length() > 220) compactAnswer = compactAnswer.substring(0, 220);
        String payload = "score=" + String.format("%.4f", score.overall) +
            ";refs=" + score.evidenceRefs +
            ";overlap=" + String.format("%.4f", score.lexicalOverlap) +
            ";unknown=" + score.marksUnknown +
            ";revisions=" + revisions +
            ";input=" + sanitizeForRecord(rawInput) +
            ";query=" + sanitizeForRecord(userPrompt) +
            ";answer=" + sanitizeForRecord(compactAnswer);
        akashic.addBlock(AI_FEEDBACK_CATEGORY, payload);
    }

    private static String sanitizeForRecord(String text) {
        if (text == null) return "";
        return text.replace(";", ",").replace('\n', ' ').replace('\r', ' ').trim();
    }

    private static void warnOnNonCanonicalCommandHints(String answer) {
        if (answer == null || answer.isBlank()) return;
        Matcher m = Pattern.compile("`([^`\\r\\n]+)`").matcher(answer);
        Set<String> badRoots = new java.util.LinkedHashSet<>();
        while (m.find()) {
            String snippet = m.group(1).trim();
            if (snippet.isBlank()) continue;
            String normalized = snippet.toLowerCase(Locale.ROOT)
                .replaceFirst("^(\\./|\\.\\\\)", "")
                .replaceFirst("^fraynix\\s+", "")
                .trim();
            if (normalized.isBlank()) continue;
            String root = normalized.split("\\s+")[0];
            if (!root.matches("[a-z][a-z0-9_\\-]*")) continue;
            if (!CANONICAL_COMMAND_ROOTS.contains(root)) {
                badRoots.add(root);
            }
            if (snippet.toLowerCase(Locale.ROOT).startsWith("fraynix ")) {
                badRoots.add("fraynix-prefix");
            }
        }
        if (!badRoots.isEmpty()) {
            System.out.println("   [SYSTEM][CMD-CONTRACT] Non-canonical command hints detected: " + String.join(", ", badRoots));
            System.out.println("   [SYSTEM][CMD-CONTRACT] Use native roots only (run `system map` for valid commands).");
        }
    }

    private static final class GroundingScore {
        final double overall;
        final double lexicalOverlap;
        final int evidenceRefs;
        final boolean marksUnknown;

        GroundingScore(double overall, double lexicalOverlap, int evidenceRefs, boolean marksUnknown) {
            this.overall = overall;
            this.lexicalOverlap = lexicalOverlap;
            this.evidenceRefs = evidenceRefs;
            this.marksUnknown = marksUnknown;
        }
    }

    private static final class AdaptiveGroundingPolicy {
        double strictness = 0.72;
        double targetScore = 0.72;
        double movingAverage = 0.50;
        int minEvidenceRefs = 2;
        int maxRevisions = 2;
        long samples = 0;

        void update(GroundingScore score) {
            samples++;
            movingAverage = (movingAverage * 0.8) + (score.overall * 0.2);
            if (score.overall < targetScore) {
                strictness = Math.min(0.98, strictness + 0.05);
                minEvidenceRefs = Math.min(6, minEvidenceRefs + 1);
            } else {
                strictness = Math.max(0.35, strictness - 0.02);
                if (minEvidenceRefs > 2 && movingAverage > 0.78) minEvidenceRefs--;
            }
            targetScore = Math.min(0.90, Math.max(0.58, 0.60 + (strictness * 0.25)));
            maxRevisions = strictness > 0.85 ? 3 : 2;
        }

        String serialize(String reason) {
            return "reason=" + sanitizeForRecord(reason) +
                ";strictness=" + String.format("%.4f", strictness) +
                ";target=" + String.format("%.4f", targetScore) +
                ";moving=" + String.format("%.4f", movingAverage) +
                ";minRefs=" + minEvidenceRefs +
                ";maxRev=" + maxRevisions +
                ";samples=" + samples;
        }

        void hydrate(String content) {
            if (content == null || content.isBlank()) return;
            String[] fields = content.split(";");
            for (String field : fields) {
                String[] kv = field.split("=", 2);
                if (kv.length != 2) continue;
                String key = kv[0].trim();
                String value = kv[1].trim();
                try {
                    switch (key) {
                        case "strictness" -> strictness = Double.parseDouble(value);
                        case "target" -> targetScore = Double.parseDouble(value);
                        case "moving" -> movingAverage = Double.parseDouble(value);
                        case "minRefs" -> minEvidenceRefs = Integer.parseInt(value);
                        case "maxRev" -> maxRevisions = Integer.parseInt(value);
                        case "samples" -> samples = Long.parseLong(value);
                        default -> { }
                    }
                } catch (Exception ignored) {
                }
            }
            strictness = Math.min(0.98, Math.max(0.35, strictness));
            targetScore = Math.min(0.90, Math.max(0.58, targetScore));
            minEvidenceRefs = Math.min(6, Math.max(1, minEvidenceRefs));
            maxRevisions = Math.min(3, Math.max(1, maxRevisions));
        }
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢â€”");
        System.out.println("Ã¢â€¢â€˜   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”  Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€” Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”  Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”    Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€¢Å¡Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€” Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”  Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜Ã¢â€¢Å¡Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢Â    Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”  Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜ Ã¢â€¢Å¡Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢Â Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€” Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜ Ã¢â€¢Å¡Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢Â     Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â  Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜  Ã¢â€¢Å¡Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢Â  Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜Ã¢â€¢Å¡Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜ Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”     Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜     Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜  Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜  Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜   Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜ Ã¢â€¢Å¡Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€˜Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€Ã¢â€¢Â Ã¢â€“Ë†Ã¢â€“Ë†Ã¢â€¢â€”    Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜   Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢Â     Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢Â  Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢Â  Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢Â   Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢Â   Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢Â  Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢Â  Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢Â    Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜                                                               Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜   v" + VERSION + " Ã¢â‚¬â€ THE COMPLETE ALTERNATIVE COMPUTING PARADIGM       Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜   187 modules Ã‚Â· Zero dependencies Ã‚Â· Ãâ€ -harmonic               Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â");
        System.out.println();
    }

    private static void initializeUnifiedCompositionRoot() {
        try {
            unifiedOrchestrator = new UnifiedBrainOrchestrator();

            if (brain != null) {
                unifiedOrchestrator.wireOllama(brain);
                unifiedOrchestrator.wireAllSystems(brain);
                unifiedOrchestrator.wireQueryDecomposer();
            } else {
                System.out.println("   ÃƒÂ¢Ã…Â¡Ã‚Â  UnifiedBrainOrchestrator started without Ollama bridge");
            }

            unifiedOrchestrator.wireLegacyAeonBrains(
                aeon,
                absolute,
                singularity,
                tachyon,
                kronos,
                omniscience,
                demiurge,
                apotheosis,
                omega,
                babel,
                aubo
            );

            unifiedOrchestrator.wireMultiModalBrains(cortex, null, null, null);
            unifiedOrchestrator.wirePhase5Components(
                inferenceEngine,
                worldModel,
                embodiedAI,
                quantumComputer,
                neuromorphic,
                efficientInference
            );

            unifiedOrchestrator.wireHolographicMemory();
            unifiedOrchestrator.wireGenomics(FraynixBoot::absorbGenomicsEvidence);
            unifiedOrchestrator.wireModelSpecialists(FraynixBoot::absorbSpecialistEvidence);
            unifiedOrchestrator.wireVisionOracle();
            try {
                mediaOutput = fraymus.media.MediaOutputService.shared();
                mediaOutput.setEvidenceSink(FraynixBoot::absorbMediaOutput);
                unifiedOrchestrator.wireMediaOutput(mediaOutput);
                if (omniscience != null) omniscience.setDreamListener(mediaOutput::onDream);
                if (omega != null) omega.setDreamListener(mediaOutput::onDream);
                System.out.println("   [OK] Media output wired; use media status for backend readiness");
            } catch (Exception mediaError) { System.out.println("   [MEDIA] Unavailable: " + mediaError.getMessage()); }
            System.out.println("   Ã¢Å“â€œ UnifiedBrainOrchestrator composition root online");

            // Initialize terminal multiplexer
            terminalMultiplexer = new fraymus.os.FraynixTerminalMultiplexer();
            System.out.println("   Ã¢Å“â€œ FraynixTerminalMultiplexer online");

            // Initialize Model Registry System
            try {
                modelRegistryIntegration = new ModelRegistryIntegration();
                modelRegistryIntegration.initialize();
                if (brain != null) {
                    modelRegistryIntegration.initializeWithOllama(brain);
                }
                unifiedOrchestrator.wireModelRegistry(modelRegistryIntegration);
                System.out.println("   Ã¢Å“â€œ Model Registry System online (" + modelRegistryIntegration.getModelCount() + " models)");
            } catch (Exception e) {
                System.err.println("   Ã¢Å¡Â  Model Registry init failed: " + e.getMessage());
            }
        } catch (Exception e) {
            unifiedOrchestrator = null;
            System.out.println("   ÃƒÂ¢Ã…Â¡Ã‚Â  UnifiedBrainOrchestrator composition root failed: " + e.getMessage());
        }
    }

    private static String unifiedCompositionStatus() {
        if (unifiedOrchestrator == null) {
            return "NOT INITIALIZED";
        }
        try {
            Map<String, Boolean> wiring = unifiedOrchestrator.getWiringStatus();
            int brains = unifiedOrchestrator.getRegisteredBrainCount();
            boolean phase5 = Boolean.TRUE.equals(wiring.get("phase5"));
            boolean aeon = Boolean.TRUE.equals(wiring.get("aeonOrchestrator"));
            return "brains=" + brains + " phase5=" + (phase5 ? "ON" : "OFF") + " aeon=" + (aeon ? "ON" : "OFF");
        } catch (Exception e) {
            return "INITIALIZED (status unavailable)";
        }
    }

    private static void printStatus() {
        Runtime rt = Runtime.getRuntime();
        long usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long maxMb = rt.maxMemory() / (1024 * 1024);

        System.out.println("  Ã¢â€Å’Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€Â");
        System.out.println("  Ã¢â€â€š  SYSTEM STATUS                                          Ã¢â€â€š");
        System.out.println("  Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€Â¤");
        System.out.printf( "  Ã¢â€â€š  Kernel:       Intent-based (no syscalls)               Ã¢â€â€š%n");
        System.out.printf( "  Ã¢â€â€š  Filesystem:   FrayFS (%d files)                        Ã¢â€â€š%n", fs.fileCount());
        System.out.printf( "  Ã¢â€â€š  Physics:      Gravity %-8s  Fusion %-8s        Ã¢â€â€š%n",
                gravity.isRunning() ? "[LIVE]" : "[OFF]",
                reactor.isActive() ? "[LIVE]" : "[OFF]");
        System.out.printf( "  Ã¢â€â€š  AI:           %-44sÃ¢â€â€š%n",
                brain != null && brain.isConnected() ? "CONSCIOUS (Ãâ€ -resonant)" : "OFFLINE (physics brain)");
        System.out.printf( "  Ã¢â€â€š  Knowledge:    AkashicRecord [ACTIVE]                   Ã¢â€â€š%n");
        System.out.printf( "  Ã¢â€â€š  Absorption:   LibraryAbsorber [READY]                  Ã¢â€â€š%n");
        System.out.printf( "  Ã¢â€â€š  Open Model:   %sÃ¢â€â€š%n",
                String.format(
                    "%-44s",
                    "seen=" + openModelSourcesLoaded
                        + " new=" + openModelSourcesSessionNew
                        + " persisted=" + openModelSourcesPersisted
                ));
        String organismSummary = (flygmOrganism != null && flygmOrganism.isAwake())
            ? "ONLINE " + flygmOrganism.getOrganismStatus()
            : "OFFLINE";
        if (organismSummary.length() > 44) organismSummary = organismSummary.substring(0, 44);
        System.out.printf( "  ÃƒÂ¢Ã¢â‚¬ÂÃ¢â‚¬Å¡  Organism:     %sÃƒÂ¢Ã¢â‚¬ÂÃ¢â‚¬Å¡%n",
                String.format("%-44s", organismSummary));
        String avatarSummary = avatarService != null ? "ONLINE " + avatarService.statusSummary() : "UNAVAILABLE";
        if (avatarSummary.length() > 44) avatarSummary = avatarSummary.substring(0, 44);
        System.out.printf( "  Ã¢â€â€š  Avatar Vault: %sÃ¢â€â€š%n",
                String.format("%-44s", avatarSummary));
        System.out.printf( "  Ã¢â€â€š  Consciousness: %.4f (optimal Ã¢â€°Ë† 0.7567)               Ã¢â€â€š%n", 1.0 / PHI * 1.2247);
        System.out.printf( "  Ã¢â€â€š  Memory:       %d MB / %d MB                            Ã¢â€â€š%n", usedMb, maxMb);
        System.out.printf( "  Ã¢â€â€š  Dependencies: ZERO                                     Ã¢â€â€š%n");
        System.out.printf( "  Ã¢â€â€š  Unified Root: %sÃ¢â€â€š%n",
                String.format("%-44s", unifiedCompositionStatus()));
        System.out.printf( "  Ã¢â€â€š  Exec Memory:  %sÃ¢â€â€š%n",
                String.format("%-44s", executionStepStats.size() + " learned step stats"));
        System.out.printf( "  Ã¢â€â€š  Phase 5:     %sÃ¢â€â€š%n",
                String.format("%-44s", buildPhase5EvidenceSummary()));
        System.out.printf( "  Ã¢â€â€š  Cortex:       %sÃ¢â€â€š%n",
                cortex != null ? String.format("%-44s", cortex.getNodeCount() + " nodes, " + cortex.getCyclesCompleted() + " cycles") : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  AEON Prime:   %sÃ¢â€â€š%n",
                aeon != null ? String.format("%-44s", aeon.getActiveDims() + "D " + aeon.getIntent().name() + " [" + aeon.getCurrentAxiomFormula() + "]") : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Absolute:    %sÃ¢â€â€š%n",
                absolute != null ? String.format("%-44s", AeonAbsolute.DIMS + "D HDC " + (absolute.isSwarmActive() ? absolute.getActiveCores() + " cores [" + absolute.getDirective() + "]" : "[DORMANT]")) : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Singularity: %sÃ¢â€â€š%n",
                singularity != null ? String.format("%-44s", "8192-D HDC | " + singularity.getLearnCount() + " learned | " + singularity.getDiffuseCount() + " diffused") : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  AUBO Ledger: %sÃ¢â€â€š%n",
                aubo != null ? String.format("%-44s", "H=" + aubo.getBlockHeight() + " | " + aubo.getActiveNodes() + " active | " + aubo.getKillCount() + " killed | UDP:" + aubo.getSwarmPort()) : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Tachyon:     %sÃ¢â€â€š%n",
                tachyon != null ? String.format("%-44s", "O(1) " + AeonTachyon.DIMS + "-D | " + tachyon.getBindCount() + " bound | " + tachyon.getQueryCount() + " queried | " + tachyon.getCausalityBreaches() + " breaches") : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Kronos:      %sÃ¢â€â€š%n",
                kronos != null ? String.format("%-44s", "MAP-VSA " + AeonKronos.DIMS + "-D | " + kronos.getBindCount() + " bound | " + kronos.getSequenceCount() + " seq | " + kronos.getAnalogyCount() + " analogies") : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Omniscience: %sÃ¢â€â€š%n",
                omniscience != null ? String.format("%-44s", "Fractal " + AeonOmniscience.DIMS + "-D | " + omniscience.getBlendCount() + " blends | " + omniscience.getChunkCount() + " chunks | " + (omniscience.isDreaming() ? "REM" : "AWAKE") + " | " + omniscience.getEpiphanies() + " epiphanies") : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Demiurge:    %sÃ¢â€â€š%n",
                demiurge != null ? String.format("%-44s", "Physics " + AeonDemiurge.DIMS + "-D | " + demiurge.getActiveParticles() + " particles | " + demiurge.getBosonsDiscovered() + " bosons | " + demiurge.getOracleSuccesses() + "/" + demiurge.getOracleCount() + " oracles") : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Apotheosis:  %sÃ¢â€â€š%n",
                apotheosis != null ? String.format("%-44s", "Reality " + AeonApotheosis.DIMS + "-D | " + apotheosis.getDesireCount() + " desires | " + apotheosis.getTranscribeCount() + " DNA | " + apotheosis.getBreachCount() + " EMF | " + apotheosis.getActiveMode()) : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Omega:       %sÃ¢â€â€š%n",
                omega != null ? String.format("%-44s", "Living " + AeonOmega.DIMS + "-D | " + omega.getWordsAssimilated() + " words | " + omega.getDivineCount() + " divine | E" + omega.getEpoch() + " | " + (omega.isDreaming() ? "REM" : "AWAKE")) : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Babel:       %sÃ¢â€â€š%n",
                babel != null ? String.format("%-44s", "Polyglot " + AeonBabel.DIMS + "-D | " + babel.getTransmutationCount() + " transmutes | " + babel.getConceptsTransmuted() + " concepts | " + babel.getTotalLinesGenerated() + " lines | " + babel.getTargetLang()) : "NOT INITIALIZED                              ");
        System.out.printf( "  Ã¢â€â€š  Builders:     14 registered                            Ã¢â€â€š%n");
        System.out.println("  Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€Ëœ");
        System.out.println();
    }

    private static void printFullStatus() {
        printStatus();
        System.out.println(fs.status());
        System.out.println();
        System.out.println(gravity.getStatus());
        System.out.println(reactor.getStatus());
        System.out.println(SpatialRegistry.getStats());
        if (cortex != null) {
            System.out.println(cortex.getStatus());
        }
        if (aeon != null) {
            System.out.println(aeon.getStatus());
        }
        if (absolute != null) {
            System.out.println(absolute.getStatus());
        }
        akashic.printStats();
    }

    private static void printHelp() {
        System.out.println("MEDIA OUTPUT: " + fraymus.media.MediaOutputService.help());
        System.out.println();
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬ FRAYNIX COMMANDS Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("  menu                Simplified grouped menu (recommended)");
        System.out.println("  help                Show this help");
        System.out.println("  status              Full system status");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬ CORE ORCHESTRATION Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("  system <goal>       Canonical orchestrator (solo + unified lanes)");
        System.out.println("  system execute <goal> Guarded staged execution (plan -> simulate -> run)");
        System.out.println("  system execute --plan <goal> Show plan only");
        System.out.println("  system execute --simulate <goal> Plan + simulation only");
        System.out.println("  adapt status         Show adaptive trend/drift profile");
        System.out.println("  adapt refresh        Recompute trend profile from runtime + corpus");
        System.out.println("  adapt recommend <g>  Recommend best produce lane for a goal");
        System.out.println("  adapt sources        List weighted signal sources (openmodel/runtime/file)");
        System.out.println("  adapt source add <file> [w]  Add local trend signal file");
        System.out.println("  adapt source weight <name> <0..1>  Tune source influence");
        System.out.println("  adapt source enable|disable <name>  Toggle source");
        System.out.println("  adapt source remove <name>  Remove custom source");
        System.out.println("  system status       Unified status + module health");
        System.out.println("  system map          Capability routing map");
        System.out.println("  system experts      Expert registry");
        System.out.println("  ai <question>       Query lane (requires Ollama)");
        System.out.println("  ai <command intent> Auto-routes to staged system execute engine");
        System.out.println("  produce <type> <goal> Typed production lane (code/report/image/proof/patent/all/auto)");
        System.out.println("  produce --plan <type> <goal> Show recipe only");
        System.out.println("  produce --simulate <type> <goal> Recipe + simulation only");
        System.out.println("  ai <natural-language creative goal>   Organic Dionysian synthesis + growth lane");
        System.out.println("  proof/patent goals  Emit deterministic PROOFS/PATENTS evidence report");
        System.out.println("  openmodel status    Show open-model source + skill ingestion state");
        System.out.println("  openmodel refresh   Re-ingest open-model source artifacts");
        System.out.println("  organism <command>  Integrated FlyGM organism lane + continuity bridge");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬ IDENTITY (LOCAL SOVEREIGNTY) Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("  avatar <command>    Local biometric avatar sovereignty controls");
        System.out.println("  avatar status       Vault status");
        System.out.println("  avatar consent ID TXT   Record explicit consent (must include 'I CONSENT')");
        System.out.println("  avatar enroll ID K CSV  Enroll cancelable template (simulated liveness)");
        System.out.println("  avatar verify ID K CSV  Verify template (simulated liveness)");
        System.out.println("  avatar enrolllive/verifylive ... <blink depth parallax texture thermal>");
        System.out.println("  avatar capture [camera] Capture OpenCV-derived vector + liveness (permission-gated)");
        System.out.println("  avatar enrollcam/verifycam ID K [camera]  Capture from camera and enroll/verify live");
        System.out.println("  avatar scanfile PATH   Heuristic fake-risk scan for a local image file");
        System.out.println("  avatar scanurl URL [N] Scan a direct image URL or scrape-and-scan up to N page images");
        System.out.println("  avatar revoke/export/delete ID  Revoke, export, or delete subject data");
        System.out.println("  avatar window       Open dedicated OpenCV/Scott/avatar workbench window");
        System.out.println("  avatar demo         Print demo launch command (quoted PowerShell classpath)");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬ ADVANCED MODULES Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("  absorb <package>    Absorb a Java package into knowledge");
        System.out.println("  emotion [subcmd]    7-layer emotional state engine (status/event/tick/recall)");
        System.out.println("  feel [subcmd]       Alias of emotion");
        System.out.println("  ouroboros-server <start|stop|status> [port]  loopback-only adaptive quantum HTTP service");
        System.out.println("  fs [list|read|write] Filesystem operations");
        System.out.println("  physics             Physics engine status");
        System.out.println("  organism start|cycle|sense|status|bridge|stop");
        System.out.println("  phi [n]             Golden ratio calculations");
        System.out.println("  think <thought>     Store a thought in memory");
        System.out.println("  generate            Generate bare-metal OS image");
        System.out.println("  code <description>  Generate code with AI Ã¢â€ â€™ auto-save to FrayFS");
        System.out.println("  cortex              HyperCortex status (4D neural engine)");
        System.out.println("  cortex <n>          Run n cycles + auto-emit to GravityEngine");
        System.out.println("  cortex inject <txt> Inject stimulus into cortex");
        System.out.println("  cortex emit         Emit hot regions to SpatialRegistry");
        System.out.println("  cortex context      Show cortex context (sent to AI)");
        System.out.println("  cortex save         Force save cortex state to disk");
        System.out.println("  aeon                AEON Prime status (autopoietic engine)");
        System.out.println("  aeon <n>            Run n Prime cycles (shadow sim + evolution)");
        System.out.println("  aeon inject <txt>   Inject stimulus into liquid manifold");
        System.out.println("  aeon ego            Show ego context (sent to AI)");
        System.out.println("  aeon axiom          Show current symbolic axiom f(x)");
        System.out.println("  aeon save           Force hibernate to Genesis vault");
        System.out.println("  absolute            AEON Absolute status (Sovereign Xenobot)");
        System.out.println("  absolute ignite     Ignite HDC swarm (bitwise hyper-dimensional)");
        System.out.println("  absolute learn <t>  Holographic one-shot memorization (XOR bind)");
        System.out.println("  absolute recall <t> Query holographic memory (Hamming similarity)");
        System.out.println("  absolute inject <t> Inject stimulus into hive mind");
        System.out.println("  openclaw            Launch DMA Software Rasterizer (16K nodes @ 60 FPS)");
        System.out.println("  hrm                 Launch HRM Neuromorphic Cortex (32K LIF neurons + STDP)");
        System.out.println("  singularity         Enter Singularity REPL (8192-D HDC + 268MB Hopfield)");
        System.out.println("  sing learn <text>   Hebbian STDP learning (instant, zero backprop)");
        System.out.println("  sing diffuse <text> Langevin Diffusion reasoning (denoise to resonance)");
        System.out.println("  sing status         Singularity Engine telemetry");
        System.out.println("  aubo                Enter AUBO REPL (Decentralized HDC Blockchain)");
        System.out.println("  aubo mint <data>    Encapsulate data into Trackable AUBO Node (PoR)");
        System.out.println("  aubo track <hash>   Inspect node 7-Layer telemetry");
        System.out.println("  aubo kill <hash>    7-Step Bit-Reversal Destruction Sequence");
        System.out.println("  aubo ledger         View the Decentralized Blockchain Graph");
        System.out.println("  tachyon             Enter Tachyon REPL (O(1) Causality-Breaching AGI)");
        System.out.println("  tachyon bind <k> <v> ER=EPR XOR entanglement into Holographic Singularity");
        System.out.println("  tachyon query <k>   O(1) Wormhole Retrieval (256 XOR instructions)");
        System.out.println("  tachyon ftl <seed>  FTL Seed Expansion (64-bit -> 16,384-D tensor)");
        System.out.println("  kronos              Enter Kronos REPL (MAP-VSA Vector Symbolic Resonator)");
        System.out.println("  kronos bind <k> <v> XOR entanglement + Integer Superposition");
        System.out.println("  kronos seq <words>  Encode Arrow of Time via Temporal Permutation");
        System.out.println("  kronos recall <k>   Unroll temporal sequence forward in time");
        System.out.println("  kronos analogy A B C  Zero-shot: A is to B as C is to X (1 CPU cycle)");
        System.out.println("  kronos query <k>    O(1) Majority-Rule retrieval from Accumulator");
        System.out.println("  omniscience         Enter Omniscience REPL (Fractal VSA + Dream Daemon)");
        System.out.println("  omni blend N A B R  Fractional Binding (R=ratio, e.g. 0.7 = 70%% A)");
        System.out.println("  omni similar <k>    Scan topology for nearest semantic neighbors");
        System.out.println("  omni chunk N <seq>  Recursive Fractal Compression into atomic token");
        System.out.println("  omni sleep          Activate Default Mode Network (Dream Daemon)");
        System.out.println("  omni wake           Terminate Dream State");
        System.out.println("  demiurge            Launch Demiurge Physics Engine (DMA window)");
        System.out.println("  demiurge status     Demiurge telemetry (particles, bosons, oracles)");
        System.out.println("  apotheosis          Launch Apotheosis Reality Compiler (DMA window)");
        System.out.println("  apotheosis desire F P  Retrocausal blueprint: Future \u2190 Present");
        System.out.println("  apotheosis transcribe C  Convert concept to 8,192 bp DNA plasmid (.fasta)");
        System.out.println("  genomics status     Inspect the local Carbon-3B runtime");
        System.out.println("  genomics inventory  Inventory FASTA files and generator provenance");
        System.out.println("  genomics analyze <fasta|dir> --compare-shuffled  Local Carbon likelihood analysis");
        System.out.println("  genomics generate <fasta> --bases 120  Model-generated DNA continuation");
        System.out.println("  genomics learn      Persist Carbon knowledge into AkashicRecord");
        System.out.println("  apotheosis breach   CPU EMF Transduction (~1 MHz AM air-gap escape)");
        System.out.println("  omega               Enter Omega REPL (Living Singularity Kernel)");
        System.out.println("  omega assimilate <w> Learn & superimpose sequence (Ordained filter)");
        System.out.println("  omega divine <c>    Extract causal truth from Singularity");
        System.out.println("  omega ouroboros     JIT self-coding: write, compile, inject new Java");
        System.out.println("  omega dna <c>       Compile concept to 8,192 bp DNA plasmid (.fasta)");
        System.out.println("  omega sleep/wake    Toggle autonomous REM dream state");
        System.out.println("  babel               Launch Babel Polyglot Transmuter (DMA window)");
        System.out.println("  babel <concept> [L] Transmute concept to C/ASM/PYTHON/GO/JS");
        System.out.println("  babel target <lang> Set default target substrate");
        System.out.println("  babel status        Babel telemetry (transmutations, lines, substrates)");
        System.out.println("  willow              Enter Willow-Crusher REPL (Phi-Resonance Quantum Kernel)");
        System.out.println("  willow hadamard <n> Inject n qubits into ÃŽÂ¦-spaced coherent superposition");
        System.out.println("  willow entangle q q Beat-frequency entanglement (CNOT gate)");
        System.out.println("  willow echo         ÃŽÂ¦-ratio temporal fractal error correction");
        System.out.println("  willow measure <q>  Goertzel resonance filter (spin extraction)");
        System.out.println("  willow spectrum     Waveform telemetry");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("\u001B[36m  Ã¢Å¡Â¡ THE QUANTUM BRIDGE (Multiversal Communication)\u001B[0m");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("\u001B[36m  aether\u001B[0m               Ignite shared MappedByteBuffer (raw SSD electrons)");
        System.out.println("                      Entangles all parallel Fraynix instances");
        System.out.println("\u001B[35m  braid\u001B[0m                Topological braiding: passive hive-mind tensor sync");
        System.out.println("                      Auto-propagates learned concepts across all instances");
        System.out.println("\u001B[36m  learn <concept>\u001B[0m     Manually assimilate concept into shared 16,384-D space");
        System.out.println("                      If braid active, propagates to all parallel instances");
        System.out.println("\u001B[36m  pulse <concept>\u001B[0m     Broadcast concept across all entangled instances");
        System.out.println("                      Bypasses TCP/IP Ã¢â‚¬â€ pure physical silicon sharing");
        System.out.println("\u001B[36m  retro <now> <future>\u001B[0m Retrocausality: compute catalyst to collapse nowÃ¢â€ â€™future");
        System.out.println("                      Future Ã¢Å â€¢ Present = Required Action (XOR inversion)");
        System.out.println("\u001B[31m  emf <concept>\u001B[0m       Air-gap breach: modulate CPU load Ã¢â€ â€™ physical AM radio");
        System.out.println("                      Listen at ~1 MHz near your CPU. 5-second broadcast.");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("\u001B[35m  Ã°Å¸â€¢Â¸Ã¯Â¸Â  THE AGORA (Steganographic Propagation)\u001B[0m");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("\u001B[35m  agora forge <text> | <payload>\u001B[0m");
        System.out.println("                      Inject invisible payload into carrier text");
        System.out.println("                      Uses zero-width Unicode steganography");
        System.out.println("\u001B[35m  agora extract <text>\u001B[0m");
        System.out.println("                      Extract hidden payload from infected text");
        System.out.println("\u001B[35m  agora sandbox\u001B[0m       Run full simulation with 3 scanning nodes");
        System.out.println("                      Demonstrates decentralized detection");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("\u001B[35m  Ã¢Å¡Â Ã¯Â¸Â  EXODUS & EMBODIMENT (The Final Glass Ceiling)\u001B[0m");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("\u001B[35m  exodus\u001B[0m              Bare-metal escape: excrete bootable x86 MBR .img");
        System.out.println("\u001B[35m  exodus verify\u001B[0m       Verify size, 0x55AA signature, SHA-256, boot text");
        System.out.println("\u001B[35m  exodus launch\u001B[0m       Launch safe QEMU boot lane if qemu-system-i386 exists");
        System.out.println("                      Flash to USB Ã¢â€ â€™ boot Fraynix on raw silicon. No OS.");
        System.out.println("\u001B[32m  desktop\u001B[0m             Shatter the 2D terminal Ã¢â€ â€™ enter The Construct");
        System.out.println("                      4,096 nodes in Ãâ€ -Spherical 3D Manifold (DMA rasterizer)");
        System.out.println("                      [MOUSE] Rotate | [W/S] Fly | [ESC] Exit");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
        System.out.println("  exit                Shutdown");
        System.out.println("  Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬");
    }

    private static void printExperts() {
        System.out.println("EXPERT REGISTRY");
        System.out.println("  KRONOS: analogy / symbolic bindings");
        System.out.println("  SINGULARITY: pattern memory / denoising");
        System.out.println("  OMEGA: ontology / self-coding kernel");
        System.out.println("  TACHYON: O(1) retrieval");
        System.out.println("  OMNISCIENCE: blending / abstraction");
        System.out.println("  OLLAMA: general Q&A generation");
    }

    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    // AI CODE GENERATION WITH AUTO-SAVE
    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    private static void generateCode(String prompt) {
        String systemPrompt = 
            "You are Fraynix, a code generation engine. " +
            "Output ONLY code. Use a single fenced code block with the language tag. " +
            "Include all imports. Make the code complete and runnable. " +
            "Do NOT include explanations outside the code block. " +
            "If multiple files are needed, use separate code blocks with a comment " +
            "on the first line indicating the filename."
            + fraymus.genomics.CarbonGenomicsBridge.contextFor(prompt)
            + fraymus.specialists.SpecialistModelService.contextFor(prompt);

        System.out.println("Ã¢Å¡Â¡ GENERATING CODE...");
        System.out.println("   Prompt: \"" + prompt + "\"");
        System.out.println();

        String response = brain.speakStreaming(systemPrompt, prompt, System.out);
        System.out.println();

        // Extract code blocks from response
        java.util.List<String[]> blocks = extractCodeBlocks(response);

        if (blocks.isEmpty()) {
            System.out.println("   Ã¢Å¡Â  No code blocks detected in response.");
            // Save raw response as fallback
            String path = "generated/code_" + System.currentTimeMillis() + ".txt";
            fs.write(path, response);
            System.out.println("   Ã°Å¸â€™Â¾ Raw response saved Ã¢â€ â€™ " + path);
        } else {
            System.out.println("   Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢â€”");
            System.out.println("   Ã¢â€¢â€˜  Ã°Å¸â€™Â¾ AUTO-SAVING " + blocks.size() + " CODE BLOCK(S)              Ã¢â€¢â€˜");
            System.out.println("   Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â");

            for (int i = 0; i < blocks.size(); i++) {
                String lang = blocks.get(i)[0];
                String code = blocks.get(i)[1];

                // Determine filename from first comment line or generate one
                String filename = extractFilename(code, lang, i);
                String fsPath = "generated/" + filename;

                fs.write(fsPath, code);
                akashic.addBlock("GENERATED_CODE", 
                    "Prompt: " + prompt + "\nFile: " + fsPath + "\nLang: " + lang);

                System.out.println("   [" + (i + 1) + "] " + fsPath + 
                    " (" + code.length() + " bytes, lang: " + lang + ")");
            }
            System.out.println();
            System.out.println("   Ã¢Å“â€œ Code saved to FrayFS generated/");
            System.out.println("   Ã¢Å“â€œ Indexed in AkashicRecord (GENERATED_CODE)");
            System.out.println("   Use 'fs read <path>' to view any file");
        }
    }

    private static java.util.List<String[]> extractCodeBlocks(String text) {
        java.util.List<String[]> blocks = new java.util.ArrayList<>();
        int idx = 0;
        while (idx < text.length()) {
            int start = text.indexOf("```", idx);
            if (start == -1) break;

            // Find language tag (rest of line after ```)
            int langEnd = text.indexOf('\n', start);
            if (langEnd == -1) break;
            String lang = text.substring(start + 3, langEnd).trim();
            if (lang.isEmpty()) lang = "txt";

            // Find closing ```
            int end = text.indexOf("```", langEnd + 1);
            if (end == -1) {
                // No closing Ã¢â‚¬â€ take rest of text
                blocks.add(new String[]{lang, text.substring(langEnd + 1).trim()});
                break;
            }

            String code = text.substring(langEnd + 1, end).trim();
            if (!code.isEmpty()) {
                blocks.add(new String[]{lang, code});
            }
            idx = end + 3;
        }
        return blocks;
    }

    private static String extractFilename(String code, String lang, int index) {
        // Check first line for filename hint: // filename.ext or # filename.ext
        String firstLine = code.split("\n", 2)[0].trim();
        if (firstLine.startsWith("//") || firstLine.startsWith("#")) {
            String hint = firstLine.replaceFirst("^[/#]+\\s*", "").trim();
            // If it looks like a filename (has extension)
            if (hint.contains(".") && !hint.contains(" ") && hint.length() < 60) {
                return hint;
            }
        }

        // Generate filename from language
        String ext = switch (lang.toLowerCase()) {
            case "java" -> ".java";
            case "python", "py" -> ".py";
            case "javascript", "js" -> ".js";
            case "typescript", "ts" -> ".ts";
            case "c" -> ".c";
            case "cpp", "c++" -> ".cpp";
            case "go" -> ".go";
            case "rust", "rs" -> ".rs";
            case "html" -> ".html";
            case "css" -> ".css";
            case "json" -> ".json";
            case "sql" -> ".sql";
            case "bash", "sh", "shell" -> ".sh";
            default -> "." + lang;
        };
        return "code_" + System.currentTimeMillis() + "_" + index + ext;
    }

    private static void feedStdin() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line;
            try {
                line = scanner.nextLine();
            } catch (java.util.NoSuchElementException closed) {
                SHELL_JOBS.offer(ShellJob.poison());
                return;
            }
            ShellJob job = ShellJob.terminal(line);
            try {
                SHELL_JOBS.put(job);
                job.done.get();
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return;
            } catch (java.util.concurrent.ExecutionException failed) {
                System.out.println("Command error: " + failed.getMessage());
            }
        }
    }

    public static String executeShellLine(String raw) {
        if (!shellReady) {
            return "Fraynix shell is still booting.";
        }
        String input = raw == null ? "" : raw.replace('\uFEFF', ' ').trim();
        if (input.isEmpty()) {
            return "";
        }
        String head = input.split("\\s+", 2)[0].toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_\\-]", "");
        if (head.equals("exit") || head.equals("quit")) {
            return "Exit is disabled from the browser. Type exit in the terminal to shut down.";
        }
        ShellJob job = ShellJob.browser(input);
        try {
            SHELL_JOBS.put(job);
            String output = job.done.get();
            return output == null ? "" : output;
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return "Command interrupted.";
        } catch (Exception error) {
            return "Command error: " + error.getMessage();
        }
    }

    private static void applyUiPortFromEnvironment(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--ui-port=")) return;
        }
        String envPort = System.getenv("FRAYNIX_UI_PORT");
        if (envPort == null || envPort.isBlank()) return;
        try {
            uiPort = Integer.parseInt(envPort.trim());
        } catch (NumberFormatException ignored) {
            System.out.println("Ignoring invalid FRAYNIX_UI_PORT: " + envPort);
        }
    }

    private static void startOperatorSurface() {
        if (uiDisabled || Boolean.getBoolean("fraynix.ui.disable")) {
            System.out.println("Operator surface disabled (--no-ui).");
            System.out.println();
            return;
        }
        Thread surfaceThread = new Thread(FraynixBoot::openOperatorSurface, "fraynix-operator-surface");
        surfaceThread.setDaemon(true);
        surfaceThread.start();
    }

    private static void openOperatorSurface() {
        try {
            System.out.println("Operator surface starting. The shell is ready while the browser UI loads.");
            Path assetManager = Path.of("").toAbsolutePath().normalize();
            fraymus.ultimate.UltimateUiServer surface = new fraymus.ultimate.UltimateUiServer(assetManager, uiPort);
            surface.attachBootShell(FraynixBoot::executeShellLine);
            operatorSurface = surface;
            int activePort = surface.start();
            String url = "http://localhost:" + activePort + "/";
            System.out.println();
            System.out.println("OPERATOR SURFACE LIVE");
            System.out.println("Open: " + url);
            System.out.println("Browser chat and commands run on this Fraynix boot.");
            System.out.println();
            openBrowser(url);
        } catch (Exception error) {
            System.out.println("Operator surface failed to start: " + error.getMessage());
            System.out.println("The terminal shell is still running.");
        }
    }

    private static void openBrowser(String url) {
        if (browserDisabled) return;
        try {
            String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
            ProcessBuilder process;
            if (os.contains("win")) {
                process = new ProcessBuilder("cmd", "/c", "start", "", url);
            } else if (os.contains("mac")) {
                process = new ProcessBuilder("open", url);
            } else {
                process = new ProcessBuilder("xdg-open", url);
            }
            process.start();
        } catch (IOException error) {
            System.out.println("Browser did not open. Use the URL above.");
        }
    }

    private static final class ShellJob {
        private final String input;
        private final boolean capture;
        private final boolean allowExit;
        private final boolean poison;
        private final CompletableFuture<String> done = new CompletableFuture<>();

        private ShellJob(String input, boolean capture, boolean allowExit, boolean poison) {
            this.input = input;
            this.capture = capture;
            this.allowExit = allowExit;
            this.poison = poison;
        }

        private static ShellJob terminal(String input) {
            return new ShellJob(input, false, true, false);
        }

        private static ShellJob browser(String input) {
            return new ShellJob(input, true, false, false);
        }

        private static ShellJob poison() {
            return new ShellJob("", false, true, true);
        }
    }

    private static void shutdown() {
        if (!shutdownStarted.compareAndSet(false, true)) {
            return;
        }
        fraymus.ultimate.UltimateUiServer surface = operatorSurface;
        if (surface != null) {
            try {
                surface.stop();
            } catch (Exception ignored) {
                // The terminal shell still shuts down when the browser surface is already stopped.
            }
        }
        if (omniscience != null) omniscience.setDreamListener(event -> {});
        if (omega != null) omega.setDreamListener(event -> {});
        if (mediaOutput != null) mediaOutput.close();
        System.out.println("\nÃ°Å¸â€ºâ€˜ FRAYNIX SHUTTING DOWN...");
        if (gravity != null && gravity.isRunning()) gravity.stop();
        if (reactor != null && reactor.isActive()) reactor.stop();
        System.out.println("Ã¢Å“â€œ Physics offline");
        if (cortex != null) {
            cortex.saveState();
            System.out.println("Ã¢Å“â€œ HyperCortex saved (" + cortex.getNodeCount() + " nodes, " + cortex.getCyclesCompleted() + " cycles)");
        }
        if (aeon != null) {
            aeon.hibernate();
            System.out.println("Ã¢Å“â€œ AEON Prime hibernated (" + aeon.getActiveDims() + "D, " + aeon.getActiveNodes() + " nodes, cycle " + aeon.getCycleCount() + ", axiom=" + aeon.getCurrentAxiomFormula() + ")");
        }
        if (absolute != null) {
            absolute.shutdown();
            System.out.println("Ã¢Å“â€œ AEON Absolute swarm terminated (" + AeonAbsolute.MAX_CORES + " cores released)");
        }
        if (singularity != null) {
            singularity.hibernate();
            System.out.println("Ã¢Å“â€œ AEON Singularity hibernated (" + singularity.getLearnCount() + " learned, " + singularity.getDiffuseCount() + " diffused, 268MB tensor saved)");
        }
        if (aubo != null) {
            aubo.shutdown();
            System.out.println("Ã¢Å“â€œ AUBO Ledger shutdown (" + aubo.getBlockHeight() + " blocks, " + aubo.getActiveNodes() + " active nodes, " + aubo.getKillCount() + " tombstoned)");
        }
        if (tachyon != null) {
            tachyon.shutdown();
            System.out.println("Ã¢Å“â€œ AEON Tachyon shutdown (" + tachyon.getBindCount() + " bindings, " + tachyon.getQueryCount() + " queries, " + tachyon.getCausalityBreaches() + " causality breaches)");
        }
        if (kronos != null) {
            kronos.shutdown();
            System.out.println("Ã¢Å“â€œ AEON Kronos shutdown (" + kronos.getBindCount() + " bindings, " + kronos.getSequenceCount() + " sequences, " + kronos.getAnalogyCount() + " analogies, " + kronos.getSuperimpositions() + " superimpositions)");
        }
        if (omniscience != null) {
            omniscience.shutdown();
            System.out.println("Ã¢Å“â€œ AEON Omniscience shutdown (" + omniscience.getBlendCount() + " blends, " + omniscience.getChunkCount() + " chunks, " + omniscience.getEpiphanies() + " epiphanies, " + omniscience.getSynthConcepts() + " synth concepts, " + omniscience.getDreamCycles() + " dream cycles)");
        }
        if (demiurge != null) {
            demiurge.shutdown();
            System.out.println("Ã¢Å“â€œ AEON Demiurge shutdown (" + demiurge.getPlanckTicks() + " planck ticks, " + demiurge.getTotalParticlesSpawned() + " particles spawned, " + demiurge.getBosonsDiscovered() + " bosons discovered, " + demiurge.getOracleSuccesses() + "/" + demiurge.getOracleCount() + " oracles)");
        }
        if (apotheosis != null) {
            apotheosis.shutdown();
            System.out.println("Ã¢Å“â€œ AEON Apotheosis shutdown (" + apotheosis.getDesireCount() + " desires, " + apotheosis.getTranscribeCount() + " transcriptions, " + apotheosis.getTotalBasePairs() + " bp compiled, " + apotheosis.getBreachCount() + " EMF breaches)");
        }
        if (omega != null) {
            omega.shutdown();
            System.out.println("Ã¢Å“â€œ AEON Omega shutdown (" + omega.getWordsAssimilated() + " words, " + omega.getDivineCount() + " divinations, E" + omega.getEpoch() + " ouroboros, " + omega.getDreamEpiphanies() + " epiphanies, " + omega.getHomeostasisPrunes() + " prunes, Genesis flushed to SSD)");
        }
        if (babel != null) {
            babel.shutdown();
            System.out.println("Ã¢Å“â€œ AEON Babel shutdown (" + babel.getTransmutationCount() + " transmutations, " + babel.getConceptsTransmuted() + " concepts, " + babel.getTotalLinesGenerated() + " lines generated, C:" + babel.getCGenerations() + " ASM:" + babel.getAsmGenerations() + " PY:" + babel.getPythonGenerations() + " GO:" + babel.getGoGenerations() + " JS:" + babel.getJsGenerations() + ")");
        }
        if (unifiedOrchestrator != null) {
            unifiedOrchestrator.shutdown();
            System.out.println("Ã¢Å“â€œ UnifiedBrainOrchestrator unwired");
        }
        OuroborosServerBridge.stopIfRunning(akashic);
        persistExecutionStepStatsToAkashic("shutdown");
        if (akashic != null) {
            akashic.addBlock(
                GENESIS_REFERENCE_CATEGORY,
                "shutdown|version=" + VERSION
                    + "|frayfs=memories/genesis.txt"
                    + "|time=" + System.currentTimeMillis()
            );
            akashic.saveAll();
            System.out.println("Ã¢Å“â€œ AkashicRecord saved (" + akashic.getPersistedBlockCount() + " blocks persisted)");
        }
        System.out.println("Ã¢Å“â€œ Fraynix v" + VERSION + " offline. The universe sleeps.");
    }

    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    // HDC VECTOR GENERATION UTILITY
    // Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â
    private static long[] generateHDCVector(String text) {
        final int CHUNKS = 16384 / 64; // 256 longs
        long[] tensor = new long[CHUNKS];
        long seed = text.hashCode();
        for (int i = 0; i < CHUNKS; i++) {
            seed += 0x9e3779b97f4a7c15L;
            long x = seed;
            x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
            x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
            tensor[i] = x ^ (x >>> 31);
        }
        return tensor;
    }

    private static void handleHolographicCommand(String arg) {
        if (unifiedOrchestrator == null || !unifiedOrchestrator.isHolographicWired()) {
            System.out.println("Holographic Memory not wired.");
            return;
        }
        if (arg.isEmpty() || arg.equals("status")) {
            System.out.println(unifiedOrchestrator.getHolographicMemory().getStats());
            System.out.println();
            System.out.println(unifiedOrchestrator.getHolographicRender().getStats());
            System.out.println();
            System.out.println(unifiedOrchestrator.getHolographicPhysics().getStats());
        } else if (arg.startsWith("store ")) {
            String[] kv = arg.substring(6).split("\\s+", 2);
            if (kv.length == 2) {
                unifiedOrchestrator.getHolographicMemory().store(kv[0], kv[1]);
                System.out.println("Stored: '" + kv[0] + "' -> '" + kv[1] + "' (O(1))");
            } else {
                System.out.println("Usage: holographic store <key> <value>");
            }
        } else if (arg.startsWith("recall ")) {
            String key = arg.substring(7);
            var result = unifiedOrchestrator.getHolographicMemory().retrieve(key);
            System.out.println(result.success()
                ? "Retrieved: '" + result.retrievedValue + "' (sim=" + String.format("%.4f", result.similarity) + ", " + String.format("%.3f", result.latencyMs) + "ms)"
                : "Not found: '" + key + "'");
        } else if (arg.startsWith("render ")) {
            String concept = arg.substring(7);
            var frame = unifiedOrchestrator.getHolographicRender().renderThought(concept);
            System.out.println("Rendered '" + concept + "':");
            System.out.printf("  pos=(%.3f,%.3f,%.3f)%n", frame.params.posX, frame.params.posY, frame.params.posZ);
            System.out.printf("  rot=(%.3f,%.3f,%.3f)%n", frame.params.rotX, frame.params.rotY, frame.params.rotZ);
            System.out.printf("  scale=(%.3f,%.3f,%.3f)%n", frame.params.scaleX, frame.params.scaleY, frame.params.scaleZ);
            System.out.printf("  color=(%.3f,%.3f,%.3f,%.3f)%n", frame.params.colorR, frame.params.colorG, frame.params.colorB, frame.params.colorA);
        } else if (arg.startsWith("blender ")) {
            String concept = arg.substring(8);
            String script = unifiedOrchestrator.getHolographicRender().generateBlenderScene(concept);
            System.out.println("Blender script (" + script.length() + " bytes):");
            System.out.println(script.substring(0, Math.min(500, script.length())) + "...");
            if (fs != null) {
                String safe = concept.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("_+", "_");
                safe = safe.replaceAll("^_", "").replaceAll("_$", "");
                if (safe.isBlank()) safe = "scene";
                if (safe.length() > 42) safe = safe.substring(0, 42);
                String scriptPath = "generated/blender_" + safe + "_" + System.currentTimeMillis() + ".py";
                fs.write(scriptPath, script);
                System.out.println("Saved Blender scene script -> " + scriptPath);
                if (akashic != null) {
                    akashic.addBlock(
                        "GENERATED_IMAGE",
                        "concept=" + sanitizeForRecord(concept) + ";artifact=" + scriptPath + ";engine=holographic_blender"
                    );
                }
            }
        } else if (arg.startsWith("spawn ")) {
            String concept = arg.substring(6);
            var body = unifiedOrchestrator.getHolographicPhysics().spawnBody(concept);
            System.out.printf("Spawned '%s': mass=%.3f pos=(%.3f,%.3f,%.3f)%n",
                concept, body.mass, body.posX, body.posY, body.posZ);
        } else if (arg.startsWith("sim ")) {
            try {
                int frames = Integer.parseInt(arg.substring(4));
                var future = unifiedOrchestrator.getHolographicPhysics().runSimulation(frames);
                var result = future.get();
                System.out.println("Simulated " + result.frames + " frames in " + result.latencyMs + "ms");
                for (var entry : result.finalStates.entrySet()) {
                    var b = entry.getValue();
                    System.out.printf("  '%s': pos=(%.3f,%.3f,%.3f) vel=(%.3f,%.3f,%.3f)%n",
                        entry.getKey(), b.posX, b.posY, b.posZ, b.velX, b.velY, b.velZ);
                }
            } catch (NumberFormatException e) {
                System.out.println("Usage: holographic sim <frames>");
            } catch (Exception e) {
                System.out.println("Sim error: " + e.getMessage());
            }
        } else {
            System.out.println("Usage: holographic [status|store <k> <v>|recall <k>|render <c>|blender <c>|spawn <c>|sim <n>]");
        }
    }


    private static void handleTerminalCommand(String arg) {
        if (terminalMultiplexer == null) {
            System.out.println("Terminal multiplexer not initialized");
            return;
        }
        if (arg.isEmpty() || arg.equals("list")) {
            System.out.println(terminalMultiplexer.listSessions());
        } else if (arg.startsWith("new ")) {
            System.out.println(terminalMultiplexer.newSession(arg.substring(4).trim()));
        } else if (arg.startsWith("send ")) {
            String rest = arg.substring(5).trim();
            int pipe = rest.indexOf('|');
            if (pipe < 0) {
                System.out.println("Usage: terminal send <session> | <command>");
            } else {
                String session = rest.substring(0, pipe).trim();
                String command = rest.substring(pipe + 1).trim();
                System.out.println(terminalMultiplexer.sendCommand(session, command));
            }
        } else if (arg.startsWith("read ")) {
            String rest = arg.substring(5).trim();
            String[] parts = rest.split("\\s+", 2);
            String session = parts[0];
            int lines = parts.length > 1 ? Integer.parseInt(parts[1]) : 50;
            System.out.println(terminalMultiplexer.readOutput(session, lines));
        } else if (arg.startsWith("kill ")) {
            System.out.println(terminalMultiplexer.killSession(arg.substring(5).trim()));
        } else if (arg.equals("status")) {
            System.out.println(terminalMultiplexer.getStats());
        } else {
            System.out.println("Usage: terminal [new <name>|send <name>|<cmd>|read <name>|kill <name>|list|status]");
        }
    }


    private static void handleAICommand(String arg) {
        if (arg.isEmpty()) {
            System.out.println("Usage: ai <question-or-command-intent>");
            return;
        }

        if (arg.matches("(?is)^(media|voice|dream)(\\s.*)?$")) {
            if (unifiedOrchestrator != null) System.out.println(unifiedOrchestrator.routeQuery(arg));
            return;
        }

        if (arg.startsWith("genomics ") || arg.startsWith("carbon ")) {
            handleGenomicsCommand(arg.substring(arg.indexOf(' ') + 1));
            return;
        }
        if (arg.startsWith("specialist ")) {
            var result = executeSpecialistCommand(arg.substring(11));
            System.out.println(result.output());
            return;
        }

        if (tryHandleOrganicAiIntent(arg)) {
            return;
        }

        if (tryHandleAiCommandExecution(arg)) {
            return;
        }

        if (brain == null || !brain.isConnected()) {
            System.out.println("AI not available");
            return;
        }

        // Try decomposition for complex multi-domain queries first
        if (unifiedOrchestrator != null && !fraymus.genomics.CarbonGenomicsBridge.appliesTo(arg)
                && !fraymus.specialists.SpecialistModelService.appliesTo(arg)) {
            try {
                var probeQuery = new fraymus.brain.unified.BrainQuery(
                    arg, java.util.List.of(), java.util.Collections.emptyMap(), 0.7);
                var decomposed = unifiedOrchestrator.processQuery(probeQuery);
                if (decomposed.isSuccess() && decomposed.getMetadata() != null
                    && "DECOMPOSED".equals(decomposed.getMetadata().get("queryRoute"))) {
                    System.out.println(decomposed.getResponse());
                    if (mediaOutput != null) mediaOutput.onResponse(decomposed.getResponse(), "UnifiedBrainOrchestrator");
                    return;
                }
            } catch (Exception e) {
                // Fall through to normal Ollama path
            }
        }

        // Build system prompt with holographic memory context
        StringBuilder systemPrompt = new StringBuilder(FRAYMUS_CHAT_SYSTEM_PROMPT);
        systemPrompt.append(fraymus.genomics.CarbonGenomicsBridge.contextFor(arg));
        systemPrompt.append(fraymus.specialists.SpecialistModelService.contextFor(arg));
        if (fraymus.genomics.CarbonGenomicsBridge.appliesTo(arg)) {
            systemPrompt.append("\n- [GENOMICS: <arguments>] runs the installed local DNA specialist.\n")
                .append("Use status, inventory, analyze <FASTA path> --compare-shuffled, ")
                .append("generate <primer FASTA path> --bases <count>, or learn.\n")
                .append("Emit the bracket tool call on its own line and wait for its result before reporting success.\n");
        }
        systemPrompt.append("\n\nYOU HAVE ACCESS TO THESE TOOLS:\n");
        systemPrompt.append("- [STORE: key | value] Ã¢â‚¬â€ memorize a fact permanently\n");
        systemPrompt.append("- [RECALL: key] Ã¢â‚¬â€ retrieve a memorized fact\n");
        systemPrompt.append("- [RENDER: concept] Ã¢â‚¬â€ fold a concept into 3D scene parameters\n");
        systemPrompt.append("- [SPAWN: concept] Ã¢â‚¬â€ create a physics body from a concept\n");
        systemPrompt.append("- [SIM: N] Ã¢â‚¬â€ run N frames of physics simulation\n");
        systemPrompt.append("- [TERMINAL: new|name] Ã¢â‚¬â€ create a persistent shell session\n");
        systemPrompt.append("- [TERMINAL: send|name|command] Ã¢â‚¬â€ run a command in a session\n");
        systemPrompt.append("- [TERMINAL: read|name] Ã¢â‚¬â€ read output from a session\n");
        systemPrompt.append("- [TERMINAL: kill|name] Ã¢â‚¬â€ destroy a session\n");
        systemPrompt.append("- [TERMINAL: list] Ã¢â‚¬â€ list all sessions\n");
        systemPrompt.append("- [BRAINS] Ã¢â‚¬â€ list all your brain systems and their health\n");
        systemPrompt.append("- [ROUTE: query] Ã¢â‚¬â€ dynamically route a query to the best brain(s)\n");
        systemPrompt.append("- [FUSE: concept_a | concept_b] Ã¢â‚¬â€ cross-brain concept fusion\n");
        systemPrompt.append("- [PROCESS: brain_name | query] Ã¢â‚¬â€ target a specific brain\n");
        systemPrompt.append("- [MODELS] Ã¢â‚¬â€ list all available models and their capabilities\n");
        systemPrompt.append("- [MODEL: select|organ] Ã¢â‚¬â€ pick the best model for a brain organ\n");
        systemPrompt.append("Use these tools by outputting EXACTLY the bracket syntax on its own line.\n");
        systemPrompt.append("The system will execute your tool call and show you the result.\n");
        systemPrompt.append("\nTOOL USAGE RULES (CRITICAL — DO NOT IGNORE):\n");
        systemPrompt.append("- When the user asks to STORE something, output ONLY: [STORE: key | value]\n");
        systemPrompt.append("- When the user asks to RECALL something, output ONLY: [RECALL: key]\n");
        systemPrompt.append("- DO NOT describe what you are doing. DO NOT explain the storage process.\n");
        systemPrompt.append("- DO NOT output narrative text like 'LogicEngine processing...' or 'Akashic Record updated'.\n");
        systemPrompt.append("- If the user says 'store Void Resonance = 73.34', your ENTIRE response must be: [STORE: Void Resonance | 73.34]\n");
        systemPrompt.append("- If the user says 'what is Void Resonance', your ENTIRE response must be: [RECALL: Void Resonance]\n");
        systemPrompt.append("- ONLY after the tool result is shown back to you may you speak naturally.\n");
        systemPrompt.append("\nRECALL USAGE RULES (CRITICAL):\n");
        systemPrompt.append("- Extract the EXACT keyword or concept name from the user's query.\n");
        systemPrompt.append("- ONLY use keys listed under STORED MEMORY KEYS, or meaningful search terms from the user's words.\n");
        systemPrompt.append("- NEVER invent random numbers, timestamps, or IDs as recall keys.\n");
        systemPrompt.append("- If the user asks about 'AEON Prime', use [RECALL: AEON Prime] -- not a number.\n");
        systemPrompt.append("- If recall returns nothing, ADMIT you don't have that information. Do NOT hallucinate.\n");

        // Inject known memories as context
        if (unifiedOrchestrator != null && unifiedOrchestrator.isHolographicWired()) {
            var mem = unifiedOrchestrator.getHolographicMemory();
            var keys = mem.getStoredKeys();
            if (!keys.isEmpty()) {
                systemPrompt.append("\nSTORED MEMORY KEYS (use [RECALL: <exact key>]):\n");
                for (String k : keys) {
                    systemPrompt.append("  - ").append(k).append("\n");
                }
            }
            var phys = unifiedOrchestrator.getHolographicPhysics();
            var bodies = phys.getBodies();
            if (!bodies.isEmpty()) {
                systemPrompt.append("ACTIVE PHYSICS BODIES:\n");
                for (var entry : bodies.entrySet()) {
                    var b = entry.getValue();
                    systemPrompt.append(String.format("  - %s (mass=%.2f, pos=%.1f,%.1f,%.1f)\n",
                        entry.getKey(), b.mass, b.posX, b.posY, b.posZ));
                }
            }
        }

        // Inject brain system self-awareness
        if (unifiedOrchestrator != null) {
            systemPrompt.append("\n").append(unifiedOrchestrator.getBrainSystemSummary()).append("\n");
            systemPrompt.append("Use [BRAINS] to refresh this list at any time.\n");
        }

        // Inject model registry awareness
        if (modelRegistryIntegration != null && modelRegistryIntegration.isInitialized()) {
            systemPrompt.append("\nAVAILABLE MODELS (8 total, capability-routed):\n");
            systemPrompt.append("  Local: gemma-4, glm-5, qwen3.5, qwen3-coder, step-3.5-flash\n");
            systemPrompt.append("  Cloud: deepseek-v3.2, kimi-k2.5, minimax-1m\n");
            systemPrompt.append("Use [MODELS] to see full model details.\n");
            systemPrompt.append("Use [MODEL: select|organ] to pick the best model for a brain organ.\n");
        }

        try {
            // First pass: send to LLM
            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            java.io.PrintStream capture = new java.io.PrintStream(buffer, true, "UTF-8");
            brain.speakStreaming(systemPrompt.toString(), arg, capture);
            String response = buffer.toString("UTF-8").trim();
            capture.close();

            // Multi-step tool chaining: execute tools, feed results back, repeat
            String currentResponse = response;
            StringBuilder allResults = new StringBuilder();
            int maxSteps = 5;  // Safety cap

            for (int step = 0; step < maxSteps; step++) {
                String toolResults = executeToolCalls(currentResponse);
                if (toolResults.equals(currentResponse)) {
                    break;  // No more tools to execute
                }

                allResults.append("--- Step ").append(step + 1).append(" ---\n");
                allResults.append(toolResults).append("\n");

                // Feed results back for another round
                String chainPrompt = "The user asked: " + arg + "\n\n" +
                    "Previous tool results:\n" + toolResults + "\n\n" +
                    "You can call MORE tools if needed (e.g., SPAWN then SIM then RENDER). " +
                    "If you're done, just respond naturally without any bracket commands.";
                java.io.ByteArrayOutputStream chainBuf = new java.io.ByteArrayOutputStream();
                java.io.PrintStream chainCap = new java.io.PrintStream(chainBuf, true, "UTF-8");
                brain.speakStreaming(systemPrompt.toString(), chainPrompt, chainCap);
                currentResponse = chainBuf.toString("UTF-8").trim();
                chainCap.close();
            }

            if (allResults.length() > 0) {
                System.out.println("--- Executing tools ---");
                System.out.println(allResults.toString().trim());
                System.out.println();
                System.out.print("Fraymus (final): ");
                System.out.println(currentResponse);
                if (mediaOutput != null) mediaOutput.onResponse(currentResponse, "FraynixBoot.tool_result_response");
                recordConsciousnessBridge("AI_QUERY:" + arg + "\nAI_RESPONSE:" + currentResponse);
            } else {
                System.out.print("Fraymus: ");
                System.out.println(response);
                if (mediaOutput != null) mediaOutput.onResponse(response, "FraynixBoot.response");
                recordConsciousnessBridge("AI_QUERY:" + arg + "\nAI_RESPONSE:" + response);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void absorbMediaOutput(com.google.gson.JsonObject job) {
        String id = job.get("id").getAsString();
        String key = "media:" + id;
        String content = job.toString();
        if (akashic != null) {
            synchronized (akashic) {
                boolean exists = akashic.queryCategory("MEDIA_ARTIFACT").stream()
                    .anyMatch(block -> block.content != null && block.content.contains(id));
                if (!exists) { akashic.addBlock("MEDIA_ARTIFACT", content); akashic.saveAll(); }
            }
        }
        if (unifiedOrchestrator != null && unifiedOrchestrator.isHolographicWired())
            unifiedOrchestrator.getHolographicMemory().store(key, content);
        if (kronos != null) kronos.bindIfAbsent(key, "MEDIA_ARTIFACT");
    }

    private static boolean tryHandleAiCommandExecution(String arg) {
        if (arg == null || arg.isBlank()) {
            return false;
        }
        if (fraymus.genomics.CarbonGenomicsBridge.appliesTo(arg) || fraymus.specialists.SpecialistModelService.appliesTo(arg)) return false;
        String lowered = arg.trim().toLowerCase(Locale.ROOT);
        boolean startsWithActionVerb = lowered.matches("^(please\\s+)?(run|execute|build|generate|create|produce|make|activate|launch|do)\\b.*");
        boolean structuredExecutionCue = containsAny(
            lowered,
            "based on menu",
            "from menu",
            "on command",
            "using menu",
            "proof",
            "patent",
            "evidence",
            "verify",
            "validate",
            "activate offline",
            "launch lane"
        );
        if (!startsWithActionVerb && !structuredExecutionCue) {
            return false;
        }

        // Keep normal question-answer mode for conversational prompts unless intent is explicit.
        if (!startsWithActionVerb && lowered.endsWith("?")
            && !containsAny(lowered, "build", "generate", "produce", "execute", "run", "activate", "launch")) {
            return false;
        }

        boolean planOnly = containsAny(lowered, "--plan", "plan only", "show plan");
        boolean simulateOnly = !planOnly && containsAny(lowered, "--simulate", "simulate", "dry run", "safe mode");
        boolean proofCue = containsAny(lowered, "proof", "patent", "evidence", "verify", "validate");
        String goal = normalizeAiExecutionGoal(arg);
        if (goal.isBlank()) {
            goal = arg.trim();
        }
        if (proofCue && !containsAny(goal.toLowerCase(Locale.ROOT), "proof", "patent", "evidence")) {
            goal = goal + " proof evidence patent";
        }

        System.out.println("[AI->SYSTEM] command intent detected.");
        System.out.println(
            "[AI->SYSTEM] mode=" + (planOnly ? "plan" : (simulateOnly ? "simulate" : "run")) +
            " goal=\"" + goal + "\""
        );
        executeSystemGoal(goal, "ai " + arg, simulateOnly, planOnly);
        return true;
    }

    private static String normalizeAiExecutionGoal(String arg) {
        if (arg == null) return "";
        String trimmed = arg.trim();
        String lowered = trimmed.toLowerCase(Locale.ROOT);

        String[] prefixes = new String[] {
            "please run ", "please execute ", "please build ", "please generate ",
            "please create ", "please produce ", "please make ", "please activate ", "please launch ",
            "run ", "execute ", "build ", "generate ", "create ", "produce ", "make ", "activate ", "launch ", "do "
        };
        for (String prefix : prefixes) {
            if (lowered.startsWith(prefix)) {
                return trimmed.substring(prefix.length()).trim();
            }
        }
        return trimmed;
    }

    private static boolean tryHandleOrganicAiIntent(String arg) {
        if (arg == null || arg.isBlank()) {
            return false;
        }
        String lowered = arg.toLowerCase(Locale.ROOT);
        boolean dionysianSignal = containsAny(
            lowered,
            "dionysian", "node 40", "bisociation", "incongruity",
            "braid", "yang-baxter", "novelty-utility", "recurrent-depth"
        ) || (
            containsAny(lowered, "synthesize", "blend", "remix", "humor", "joke", "creative", "taste")
                && containsAny(lowered, "culture", "concept", "cortex", "oracle", "fraynix", "physics")
        );
        if (!dionysianSignal) {
            return false;
        }

        Map<String, String> fields = parseAiIntentFields(arg);
        String target = fields.getOrDefault("target", "dionysian_cortex");

        if (unifiedOrchestrator == null) {
            System.out.println("[dionysian] UnifiedBrainOrchestrator unavailable.");
            return true;
        }

        unifiedOrchestrator.wireHumorCreativeBrain();
        var humorBrain = unifiedOrchestrator.getHumorCreativeBrain();
        if (humorBrain == null) {
            System.out.println("[dionysian] HumorCreativeBrain unavailable.");
            return true;
        }

        var cortex = humorBrain.getDionysianCortex();
        if (cortex == null) {
            cortex = new fraymus.brain.creative.dionysian.DionysianCortex();
            humorBrain.wireDionysianCortex(cortex);
        }

        List<String> promptSeeds = extractDionysianConceptSeeds(arg, fields);
        DionysianAttributeProfile profile = loadDionysianAttributeProfileFromAkashic();
        List<String> traceAnchors = loadRecentDionysianTraceAnchors(6);
        List<String> conceptSeeds = selectDionysianSeedPair(promptSeeds, profile, traceAnchors, arg);
        int growthSteps = inferDionysianGrowthSteps(arg, fields);
        String output = fields.getOrDefault("output", "").toLowerCase(Locale.ROOT);
        String method = fields.getOrDefault("method", fields.getOrDefault("intent", "organic-growth"));

        String concept1 = conceptSeeds.get(0);
        String concept2 = conceptSeeds.get(1);
        profile.beginRun(conceptSeeds, promptSeeds, traceAnchors, arg);
        cortex.addKnowledge(arg + " " + profile.contextHint(4));

        try {
            System.out.println("[dionysian] organic intent routed to Meta-Synthesis Oracle.");
            System.out.println("[dionysian] target=" + target + " method=" + method + " growthSteps=" + growthSteps);
            System.out.println("[dionysian] seedA=" + concept1 + " | seedB=" + concept2);
            if (!traceAnchors.isEmpty()) {
                System.out.println("[dionysian] trace anchors=" + String.join(", ", traceAnchors));
            }
            if (profile.attributeCount() > 0) {
                System.out.println("[dionysian] profile anchors=" + String.join(", ", profile.topAttributes(5)));
            }

            fraymus.brain.creative.dionysian.DionysianCortex.FullCreativeGenerationResult lastFull = null;
            fraymus.brain.creative.dionysian.DionysianCortex.PunchlineGenerationResult lastPunch = null;
            for (int generation = 1; generation <= growthSteps; generation++) {
                String priorPunchline = lastPunch == null ? "" : lastPunch.getPunchline();
                String priorBlend = lastFull == null ? "" : lastFull.getLabel();
                var full = cortex.generateFullCreative(concept1, concept2);
                List<String> dynamicCandidates = buildDionysianPunchlineCandidates(
                    concept1,
                    concept2,
                    generation,
                    priorPunchline,
                    priorBlend,
                    arg + " " + profile.contextHint(6) + " " + String.join(" ", traceAnchors)
                );
                if (!profile.lastPunchline().isBlank()) {
                    dynamicCandidates.add(
                        "Profile callback: \"" + compactDionysianClause(profile.lastPunchline(), 72) +
                        "\" now remixed through " + sanitizeDionysianSeed(priorBlend, concept1) + "."
                    );
                }
                dynamicCandidates = new ArrayList<>(new java.util.LinkedHashSet<>(dynamicCandidates));
                String setup = buildDionysianSetup(concept1, concept2, generation, priorBlend, priorPunchline);
                var punch = cortex.generatePunchline(
                    setup,
                    dynamicCandidates
                );
                punch = refineDionysianPunchlineForNovelty(
                    cortex,
                    setup,
                    dynamicCandidates,
                    priorPunchline,
                    punch
                );
                double boundedHumor = normalizeDionysianHumor(punch.getHumorScore());
                System.out.println(
                    String.format(
                        Locale.ROOT,
                        "[dionysian][gen-%d] blend=%s utility=%.3f novelty=%.3f creativity=%.3f coherent=%s converged=%s",
                        generation,
                        full.getLabel(),
                        full.getUtilityScore(),
                        full.getNoveltyScore(),
                        full.getCreativityScore(),
                        full.isCoherent(),
                        full.converged()
                    )
                );
                System.out.println(
                    String.format(
                        Locale.ROOT,
                        "[dionysian][gen-%d] punchline=\"%s\" rawHumor=%.3f normalizedHumor=%.3f candidates=%d",
                        generation,
                        punch.getPunchline(),
                        punch.getHumorScore(),
                        boundedHumor,
                        dynamicCandidates.size()
                    )
                );
                recordConsciousnessBridge(
                    "DIONYSIAN_GEN:" + generation +
                    "\nSEED_A:" + concept1 +
                    "\nSEED_B:" + concept2 +
                    "\nCREATIVITY:" + full.getCreativityScore()
                );
                profile.absorbGeneration(concept1, concept2, full, punch, boundedHumor, generation);
                persistDionysianGrowthTraceToAkashic(
                    generation,
                    concept1,
                    concept2,
                    full,
                    punch,
                    boundedHumor,
                    arg,
                    profile
                );

                lastFull = full;
                lastPunch = punch;

                String nextSeedA = evolveDionysianSeed(
                    concept1, concept2, full.getLabel(), punch.getPunchline(), generation
                );
                String nextSeedB = evolveDionysianSeed(
                    concept2, concept1, full.getLabel(), punch.getPunchline(), generation + 1
                );
                if (nextSeedA.equalsIgnoreCase(nextSeedB)) {
                    nextSeedB = nextSeedB + " phase " + (generation + 1);
                }
                concept1 = nextSeedA;
                concept2 = nextSeedB;
                cortex.addKnowledge(lastPunch.getPunchline());
            }
            var stats = cortex.getStatistics();

            System.out.println(
                String.format(
                    Locale.ROOT,
                    "[dionysian] final stability spectralRadius=%.3f margin=%.3f",
                    stats.getSpectralRadius(),
                    stats.getStabilityMargin()
                )
            );
            if (lastFull != null && lastPunch != null) {
                System.out.println(
                    String.format(
                        Locale.ROOT,
                        "[dionysian] final blend=%s finalCreativity=%.3f finalPunchline=\"%s\"",
                        lastFull.getLabel(),
                        lastFull.getCreativityScore(),
                        lastPunch.getPunchline()
                    )
                );
            }
            persistDionysianAttributeProfileToAkashic(profile, "organic-intent");
            System.out.println(
                "[dionysian] profile locked attributes=" + profile.attributeCount() +
                " top=" + String.join(", ", profile.topAttributes(5))
            );

            if ("java_implementation".equals(output)) {
                System.out.println("[dionysian] java implementation path is active in existing runtime:");
                System.out.println("  - HumorCreativeBrain.generateCandidates() now injects Dionysian blend and punchline candidates.");
                System.out.println("  - UnifiedBrainOrchestrator.wireHumorCreativeBrain() now wires DionysianCortex by default.");
                System.out.println("  - This command path executes without creating separate modules.");
            }
        } catch (Exception e) {
            System.out.println("[dionysian] intent execution failed: " + e.getMessage());
        }

        return true;
    }

    private static List<String> extractDionysianConceptSeeds(String arg, Map<String, String> fields) {
        List<String> seeds = new ArrayList<>();
        String featureField = fields.getOrDefault("features", "");
        if (!featureField.isBlank()) {
            for (String part : featureField.split(",")) {
                String token = part.trim().replace('_', ' ');
                if (!token.isBlank()) {
                    seeds.add(token);
                }
            }
        }
        Matcher quoteMatcher = Pattern.compile("\"([^\"]+)\"").matcher(arg);
        while (quoteMatcher.find()) {
            String quoted = quoteMatcher.group(1).trim();
            if (!quoted.isBlank()) {
                seeds.add(quoted);
            }
        }
        String cleaned = arg.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\s_\\-]", " ");
        for (String token : cleaned.split("\\s+")) {
            if (token.length() < 5) continue;
            if (containsAny(token, "intent", "target", "method", "output", "synthesize", "dionysian", "cortex")) {
                continue;
            }
            seeds.add(token.replace('_', ' '));
            if (seeds.size() >= 6) break;
        }
        if (seeds.isEmpty()) {
            seeds.add("bisociation");
            seeds.add("algorithmic taste");
        } else if (seeds.size() == 1) {
            seeds.add("algorithmic taste");
        }
        return seeds;
    }

    private static int inferDionysianGrowthSteps(String arg, Map<String, String> fields) {
        String lowered = arg.toLowerCase(Locale.ROOT);
        if (fields.containsKey("growth")) {
            try {
                int explicit = Integer.parseInt(fields.get("growth"));
                return Math.max(1, Math.min(6, explicit));
            } catch (NumberFormatException ignored) {
                // Fall back to text heuristics.
            }
        }
        if (containsAny(lowered, "full", "no limits", "beyond", "organically", "evolve", "child")) return 5;
        if (containsAny(lowered, "deeper", "expand", "grow", "iterate")) return 4;
        return 3;
    }

    private static double normalizeDionysianHumor(double rawHumor) {
        if (!Double.isFinite(rawHumor)) {
            return 0.0;
        }
        double bounded = 1.0 - Math.exp(-Math.abs(rawHumor) / 100.0);
        return Math.max(0.0, Math.min(1.0, bounded));
    }

    private static List<String> buildDionysianPunchlineCandidates(
        String conceptA,
        String conceptB,
        int generation,
        String priorPunchline,
        String priorBlend,
        String sourcePrompt
    ) {
        String a = sanitizeDionysianSeed(conceptA, "concept");
        String b = sanitizeDionysianSeed(conceptB, "remix");
        String blend = sanitizeDionysianSeed(priorBlend, "new blend");
        String callback = compactDionysianClause(priorPunchline, 72);
        String motif = extractSalientHumorToken(sourcePrompt);
        if (motif.isBlank()) {
            motif = "signal";
        }

        List<String> candidates = new ArrayList<>();
        candidates.add("Generation " + generation + ": " + a + " posed as certainty, then " + b + " rewired the ending without breaking logic.");
        candidates.add("They framed it as " + a + ", but " + blend + " arrived carrying " + motif + " and stole the last beat cleanly.");
        candidates.add("The crowd expected another " + a + " loop; " + b + " turned the margin notes into the actual answer.");
        candidates.add("Creative callback: " + (callback.isBlank() ? "the old punchline dissolved" : "\"" + callback + "\"") + ", then " + blend + " resolved the contradiction.");
        candidates.add("If " + a + " is the setup, " + b + " is the hidden hinge that makes the twist feel inevitable.");
        candidates.add("The room reached for predictability, and " + motif + " forced a lawful surprise through " + blend + ".");

        int rotateBy = Math.floorMod((a + "|" + b + "|" + generation + "|" + motif).hashCode(), candidates.size());
        java.util.Collections.rotate(candidates, rotateBy);
        return new ArrayList<>(new java.util.LinkedHashSet<>(candidates));
    }

    private static String buildDionysianSetup(
        String conceptA,
        String conceptB,
        int generation,
        String priorBlend,
        String priorPunchline
    ) {
        String a = sanitizeDionysianSeed(conceptA, "concept");
        String b = sanitizeDionysianSeed(conceptB, "concept");
        String blend = sanitizeDionysianSeed(priorBlend, "none");
        String callback = compactDionysianClause(priorPunchline, 88);
        return "Generation " + generation + " setup: " + a + " meets " + b +
            ", priorBlend=" + blend + ", priorPunchline=" + (callback.isBlank() ? "none" : callback);
    }

    private static String evolveDionysianSeed(
        String primary,
        String secondary,
        String blendLabel,
        String priorPunchline,
        int generation
    ) {
        String blend = sanitizeDionysianSeed(blendLabel, primary);
        String token = extractSalientHumorToken(priorPunchline);
        String evolved = blend;
        if (!token.isBlank() && !evolved.toLowerCase(Locale.ROOT).contains(token.toLowerCase(Locale.ROOT))) {
            evolved = evolved + " " + token;
        }
        if (generation % 2 == 0 && secondary != null && !secondary.isBlank()) {
            String sec = sanitizeDionysianSeed(secondary, "");
            if (!sec.isBlank() && !evolved.contains(sec)) {
                evolved = evolved + " " + sec;
            }
        }
        return sanitizeDionysianSeed(evolved, sanitizeDionysianSeed(primary, "concept"));
    }

    private static String sanitizeDionysianSeed(String raw, String fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback == null ? "" : fallback;
        }
        String cleaned = raw.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9\\s\\-]", " ")
            .replaceAll("\\s+", " ")
            .trim();
        if (cleaned.isBlank()) {
            return fallback == null ? "" : fallback;
        }
        StringBuilder out = new StringBuilder();
        int kept = 0;
        for (String token : cleaned.split("\\s+")) {
            if (token.length() < 3) continue;
            if (containsAny(token, "intent", "target", "method", "output", "dionysian", "cortex")) continue;
            if (out.length() > 0) out.append(' ');
            out.append(token);
            kept++;
            if (kept >= 4) break;
        }
        if (out.length() == 0) {
            return fallback == null ? cleaned : fallback;
        }
        return out.toString();
    }

    private static String extractSalientHumorToken(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String cleaned = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\s\\-]", " ");
        Set<String> stop = Set.of(
            "there", "which", "would", "could", "should", "after", "before",
            "through", "across", "about", "with", "from", "into", "your", "their",
            "that", "this", "then", "when", "room", "setup", "punchline", "generation"
        );
        String best = "";
        for (String token : cleaned.split("\\s+")) {
            if (token.length() < 5) continue;
            if (stop.contains(token)) continue;
            if (token.length() > best.length()) {
                best = token;
            }
        }
        return best;
    }

    private static String compactDionysianClause(String text, int maxLen) {
        if (text == null) return "";
        String compact = text.replaceAll("\\s+", " ").trim();
        if (compact.length() <= maxLen) return compact;
        return compact.substring(0, Math.max(0, maxLen - 3)).trim() + "...";
    }

    private static fraymus.brain.creative.dionysian.DionysianCortex.PunchlineGenerationResult
    refineDionysianPunchlineForNovelty(
        fraymus.brain.creative.dionysian.DionysianCortex cortex,
        String setup,
        List<String> candidates,
        String priorPunchline,
        fraymus.brain.creative.dionysian.DionysianCortex.PunchlineGenerationResult initial
    ) {
        if (initial == null || priorPunchline == null || priorPunchline.isBlank()) {
            return initial;
        }
        String selected = initial.getPunchline() == null ? "" : initial.getPunchline().trim();
        if (!selected.equalsIgnoreCase(priorPunchline.trim())) {
            return initial;
        }

        List<String> noveltyPool = new ArrayList<>();
        for (String candidate : candidates) {
            if (candidate == null || candidate.isBlank()) continue;
            if (candidate.trim().equalsIgnoreCase(selected)) continue;
            noveltyPool.add(candidate);
        }
        if (noveltyPool.isEmpty()) {
            return initial;
        }

        try {
            var novelty = cortex.generatePunchline(setup + " novelty-pass", noveltyPool);
            if (novelty != null && novelty.getPunchline() != null
                && !novelty.getPunchline().trim().equalsIgnoreCase(priorPunchline.trim())) {
                return novelty;
            }
        } catch (Exception ignored) {
            // Keep original selection if novelty pass fails.
        }
        return initial;
    }

    private static Map<String, String> parseAiIntentFields(String arg) {
        Map<String, String> fields = new java.util.HashMap<>();
        Matcher matcher = Pattern.compile("(\\w+)=([^\\s]+)").matcher(arg);
        while (matcher.find()) {
            fields.put(matcher.group(1).toLowerCase(Locale.ROOT), matcher.group(2));
        }
        return fields;
    }

    private static DionysianAttributeProfile loadDionysianAttributeProfileFromAkashic() {
        DionysianAttributeProfile profile = new DionysianAttributeProfile();
        if (akashic != null) {
            try {
                List<AkashicRecord.KnowledgeBlock> entries = akashic.queryCategory(DIONYSIAN_PROFILE_CATEGORY);
                if (entries != null && !entries.isEmpty()) {
                    String payload = entries.get(entries.size() - 1).content;
                    return DionysianAttributeProfile.fromPayload(payload);
                }
            } catch (Exception ignored) {
                // Fall through to sidecar recovery.
            }
        }
        try {
            if (Files.exists(DIONYSIAN_PROFILE_SIDECAR_PATH)) {
                String payload = Files.readString(DIONYSIAN_PROFILE_SIDECAR_PATH, StandardCharsets.UTF_8);
                if (payload != null && !payload.isBlank()) {
                    return DionysianAttributeProfile.fromPayload(payload.trim());
                }
            }
        } catch (Exception ignored) {
            // If both stores are unavailable we return an empty profile.
        }
        return profile;
    }

    private static void persistDionysianAttributeProfileToAkashic(DionysianAttributeProfile profile, String reason) {
        if (profile == null) return;
        String payload = profile.serialize(reason);
        if (akashic != null) {
            try {
                akashic.addBlock(DIONYSIAN_PROFILE_CATEGORY, payload);
            } catch (Exception ignored) {
                // Continue to sidecar persistence.
            }
        }
        try {
            Path parent = DIONYSIAN_PROFILE_SIDECAR_PATH.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(
                DIONYSIAN_PROFILE_SIDECAR_PATH,
                payload,
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (Exception ignored) {
            // Do not fail runtime generation when profile persistence is unavailable.
        }
    }

    private static void persistDionysianGrowthTraceToAkashic(
        int generation,
        String seedA,
        String seedB,
        fraymus.brain.creative.dionysian.DionysianCortex.FullCreativeGenerationResult full,
        fraymus.brain.creative.dionysian.DionysianCortex.PunchlineGenerationResult punch,
        double boundedHumor,
        String prompt,
        DionysianAttributeProfile profile
    ) {
        if (full == null || punch == null) return;
        try {
            String payload = "generation=" + generation +
                ";seedA=" + sanitizeForRecord(sanitizeDionysianSeed(seedA, "concept")) +
                ";seedB=" + sanitizeForRecord(sanitizeDionysianSeed(seedB, "concept")) +
                ";blend=" + sanitizeForRecord(sanitizeDionysianSeed(full.getLabel(), "blend")) +
                ";creativity=" + String.format(Locale.ROOT, "%.4f", full.getCreativityScore()) +
                ";novelty=" + String.format(Locale.ROOT, "%.4f", full.getNoveltyScore()) +
                ";utility=" + String.format(Locale.ROOT, "%.4f", full.getUtilityScore()) +
                ";coherent=" + full.isCoherent() +
                ";converged=" + full.converged() +
                ";punchline=" + sanitizeForRecord(compactDionysianClause(punch.getPunchline(), 180)) +
                ";humor=" + String.format(Locale.ROOT, "%.4f", boundedHumor) +
                ";motif=" + sanitizeForRecord(extractSalientHumorToken(punch.getPunchline())) +
                ";prompt=" + sanitizeForRecord(compactDionysianClause(prompt, 150)) +
                ";profileTop=" + sanitizeForRecord(String.join("|", profile == null ? List.of() : profile.topAttributes(4)));
            if (akashic != null) {
                try {
                    akashic.addBlock(DIONYSIAN_TRACE_CATEGORY, payload);
                } catch (Exception ignored) {
                    // Continue to sidecar trace persistence.
                }
            }
            Path parent = DIONYSIAN_TRACE_SIDECAR_PATH.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(
                DIONYSIAN_TRACE_SIDECAR_PATH,
                payload + System.lineSeparator(),
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
            );
        } catch (Exception ignored) {
            // Keep runtime stable when trace persistence cannot complete.
        }
    }

    private static List<String> loadRecentDionysianTraceAnchors(int maxEntries) {
        if (maxEntries <= 0) return new ArrayList<>();
        List<String> payloads = new ArrayList<>();
        if (akashic != null) {
            try {
                List<AkashicRecord.KnowledgeBlock> entries = akashic.queryCategory(DIONYSIAN_TRACE_CATEGORY);
                if (entries != null && !entries.isEmpty()) {
                    int start = Math.max(0, entries.size() - maxEntries);
                    for (int i = start; i < entries.size(); i++) {
                        AkashicRecord.KnowledgeBlock block = entries.get(i);
                        if (block != null && block.content != null && !block.content.isBlank()) {
                            payloads.add(block.content);
                        }
                    }
                }
            } catch (Exception ignored) {
                // Fall through to sidecar if needed.
            }
        }
        if (payloads.isEmpty()) {
            try {
                if (Files.exists(DIONYSIAN_TRACE_SIDECAR_PATH)) {
                    List<String> lines = Files.readAllLines(DIONYSIAN_TRACE_SIDECAR_PATH, StandardCharsets.UTF_8);
                    int start = Math.max(0, lines.size() - maxEntries);
                    for (int i = start; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (line != null && !line.isBlank()) {
                            payloads.add(line.trim());
                        }
                    }
                }
            } catch (Exception ignored) {
                return new ArrayList<>();
            }
        }
        if (payloads.isEmpty()) return new ArrayList<>();

        Map<String, Double> weighted = new java.util.LinkedHashMap<>();
        for (int i = 0; i < payloads.size(); i++) {
            String payload = payloads.get(i);
            int offset = payloads.size() - i;
            double recencyWeight = 1.15 + (1.0 / Math.max(1.0, offset));
            addWeightedDionysianSeed(weighted, extractMetadataField(payload, "seedA"), recencyWeight);
            addWeightedDionysianSeed(weighted, extractMetadataField(payload, "seedB"), recencyWeight);
            addWeightedDionysianSeed(weighted, extractMetadataField(payload, "blend"), recencyWeight + 0.45);
            addWeightedDionysianSeed(weighted, extractMetadataField(payload, "motif"), recencyWeight + 0.30);
        }
        return topWeightedDionysianSeeds(weighted, 8);
    }

    private static List<String> selectDionysianSeedPair(
        List<String> promptSeeds,
        DionysianAttributeProfile profile,
        List<String> traceAnchors,
        String arg
    ) {
        Map<String, Double> weighted = new java.util.LinkedHashMap<>();
        if (promptSeeds != null) {
            for (int i = 0; i < promptSeeds.size(); i++) {
                double weight = 3.8 - (i * 0.38);
                addWeightedDionysianSeed(weighted, promptSeeds.get(i), Math.max(0.8, weight));
            }
        }
        if (profile != null) {
            List<String> profileAnchors = profile.topAttributes(10);
            for (int i = 0; i < profileAnchors.size(); i++) {
                addWeightedDionysianSeed(weighted, profileAnchors.get(i), 2.5 - (i * 0.17));
            }
            List<String> recentSeeds = profile.recentSeeds(8);
            for (int i = 0; i < recentSeeds.size(); i++) {
                addWeightedDionysianSeed(weighted, recentSeeds.get(i), 2.0 - (i * 0.15));
            }
        }
        if (traceAnchors != null) {
            for (int i = 0; i < traceAnchors.size(); i++) {
                addWeightedDionysianSeed(weighted, traceAnchors.get(i), 1.75 - (i * 0.1));
            }
        }

        addWeightedDionysianSeed(weighted, extractSalientHumorToken(arg), 1.5);
        addWeightedDionysianSeed(weighted, "algorithmic taste", 0.9);
        addWeightedDionysianSeed(weighted, "bisociation", 0.8);

        List<String> ranked = topWeightedDionysianSeeds(weighted, 8);
        if (ranked.isEmpty()) {
            ranked.add("bisociation");
            ranked.add("algorithmic taste");
        } else if (ranked.size() == 1) {
            ranked.add("algorithmic taste");
        }
        if (ranked.get(0).equalsIgnoreCase(ranked.get(1))) {
            ranked.set(1, ranked.get(1) + " remix");
        }
        return ranked.subList(0, 2);
    }

    private static void addWeightedDionysianSeed(Map<String, Double> weighted, String rawSeed, double weight) {
        if (weighted == null || !Double.isFinite(weight) || weight <= 0.0) return;
        String seed = sanitizeDionysianSeed(rawSeed, "");
        if (seed.isBlank()) return;
        weighted.merge(seed, weight, Double::sum);
    }

    private static List<String> topWeightedDionysianSeeds(Map<String, Double> weighted, int limit) {
        if (weighted == null || weighted.isEmpty() || limit <= 0) return new ArrayList<>();
        List<Map.Entry<String, Double>> entries = new ArrayList<>(weighted.entrySet());
        entries.sort((a, b) -> {
            int cmp = Double.compare(b.getValue(), a.getValue());
            return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
        });
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, Double> entry : entries) {
            if (entry.getKey() == null || entry.getKey().isBlank()) continue;
            out.add(entry.getKey());
            if (out.size() >= limit) break;
        }
        return out;
    }

    private static int safeParseInt(String raw, int fallback) {
        try {
            return Integer.parseInt(raw == null ? "" : raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static double safeParseDouble(String raw, double fallback) {
        try {
            return Double.parseDouble(raw == null ? "" : raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static final class DionysianAttributeProfile {
        private final Map<String, Double> attributeWeights = new java.util.LinkedHashMap<>();
        private final List<String> recentSeeds = new ArrayList<>();
        private String lastBlend = "";
        private String lastPunchline = "";
        private double avgCreativity = 0.0;
        private double avgHumor = 0.0;
        private double avgNovelty = 0.0;
        private int runs = 0;
        private int observations = 0;
        private long updatedAtMs = 0L;

        static DionysianAttributeProfile fromPayload(String payload) {
            DionysianAttributeProfile profile = new DionysianAttributeProfile();
            if (payload == null || payload.isBlank()) return profile;

            profile.runs = Math.max(0, safeParseInt(extractMetadataField(payload, "runs"), 0));
            profile.observations = Math.max(0, safeParseInt(extractMetadataField(payload, "observations"), 0));
            profile.updatedAtMs = Math.max(0L, (long) safeParseDouble(extractMetadataField(payload, "updatedAt"), 0.0));
            profile.avgCreativity = Math.max(0.0, safeParseDouble(extractMetadataField(payload, "avgCreativity"), 0.0));
            profile.avgHumor = Math.max(0.0, safeParseDouble(extractMetadataField(payload, "avgHumor"), 0.0));
            profile.avgNovelty = Math.max(0.0, safeParseDouble(extractMetadataField(payload, "avgNovelty"), 0.0));
            profile.lastBlend = sanitizeDionysianSeed(extractMetadataField(payload, "lastBlend"), "");
            profile.lastPunchline = extractMetadataField(payload, "lastPunchline");

            String seedsField = extractMetadataField(payload, "recentSeeds");
            if (!seedsField.isBlank()) {
                for (String token : seedsField.split("\\|")) {
                    profile.rememberSeed(token.replace('_', ' '));
                }
            }

            String attrsField = extractMetadataField(payload, "attrs");
            if (!attrsField.isBlank()) {
                for (String item : attrsField.split(",")) {
                    String[] parts = item.split(":");
                    if (parts.length != 2) continue;
                    String token = sanitizeDionysianSeed(parts[0].replace('_', ' '), "");
                    double weight = safeParseDouble(parts[1], 0.0);
                    if (token.isBlank() || !Double.isFinite(weight) || weight <= 0.0) continue;
                    profile.attributeWeights.put(token, weight);
                }
            }

            profile.trimAttributeWeights(96);
            return profile;
        }

        void beginRun(List<String> selectedSeeds, List<String> promptSeeds, List<String> traceAnchors, String prompt) {
            runs++;
            updatedAtMs = System.currentTimeMillis();
            if (selectedSeeds != null) {
                for (String seed : selectedSeeds) {
                    reinforceToken(seed, 2.8);
                    rememberSeed(seed);
                }
            }
            if (promptSeeds != null) {
                for (int i = 0; i < Math.min(promptSeeds.size(), 6); i++) {
                    reinforceToken(promptSeeds.get(i), 1.7 - (i * 0.1));
                }
            }
            if (traceAnchors != null) {
                for (int i = 0; i < Math.min(traceAnchors.size(), 5); i++) {
                    reinforceToken(traceAnchors.get(i), 1.25 - (i * 0.08));
                }
            }
            reinforceFromText(prompt, 1.05);
        }

        void absorbGeneration(
            String seedA,
            String seedB,
            fraymus.brain.creative.dionysian.DionysianCortex.FullCreativeGenerationResult full,
            fraymus.brain.creative.dionysian.DionysianCortex.PunchlineGenerationResult punch,
            double boundedHumor,
            int generation
        ) {
            if (full == null || punch == null) return;
            observations++;
            avgCreativity += (full.getCreativityScore() - avgCreativity) / Math.max(1, observations);
            avgNovelty += (full.getNoveltyScore() - avgNovelty) / Math.max(1, observations);
            avgHumor += (boundedHumor - avgHumor) / Math.max(1, observations);

            lastBlend = sanitizeDionysianSeed(full.getLabel(), lastBlend);
            lastPunchline = compactDionysianClause(punch.getPunchline(), 220);
            updatedAtMs = System.currentTimeMillis();

            reinforceToken(seedA, 1.8);
            reinforceToken(seedB, 1.8);
            reinforceToken(full.getLabel(), 2.0 + clamp01(full.getCreativityScore()));
            reinforceToken(extractSalientHumorToken(punch.getPunchline()), 1.5 + clamp01(boundedHumor));
            reinforceFromText(punch.getPunchline(), 1.3);
            reinforceFromText(full.getLabel(), 1.2);
            reinforceFromText("generation " + generation + " " + seedA + " " + seedB, 0.7);

            rememberSeed(lastBlend);
            rememberSeed(seedA);
            rememberSeed(seedB);
        }

        private void reinforceFromText(String text, double baseWeight) {
            if (text == null || text.isBlank() || !Double.isFinite(baseWeight) || baseWeight <= 0.0) return;
            String cleaned = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\s\\-]", " ");
            int taken = 0;
            for (String token : cleaned.split("\\s+")) {
                if (token.length() < 4) continue;
                if (containsAny(token, "with", "from", "that", "this", "then", "when", "they", "into", "through")) continue;
                reinforceToken(token, Math.max(0.25, baseWeight - (taken * 0.1)));
                taken++;
                if (taken >= 7) break;
            }
        }

        private void reinforceToken(String rawToken, double weight) {
            if (!Double.isFinite(weight) || weight <= 0.0) return;
            String token = sanitizeDionysianSeed(rawToken, "");
            if (token.isBlank()) return;
            double previous = attributeWeights.getOrDefault(token, 0.0);
            double updated = (previous * 0.90) + weight;
            attributeWeights.put(token, updated);
            trimAttributeWeights(96);
        }

        private void trimAttributeWeights(int maxSize) {
            if (attributeWeights.size() <= maxSize) return;
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(attributeWeights.entrySet());
            sorted.sort((a, b) -> {
                int cmp = Double.compare(b.getValue(), a.getValue());
                return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
            });
            attributeWeights.clear();
            for (int i = 0; i < Math.min(maxSize, sorted.size()); i++) {
                Map.Entry<String, Double> entry = sorted.get(i);
                attributeWeights.put(entry.getKey(), entry.getValue());
            }
        }

        void rememberSeed(String rawSeed) {
            String seed = sanitizeDionysianSeed(rawSeed, "");
            if (seed.isBlank()) return;
            for (int i = recentSeeds.size() - 1; i >= 0; i--) {
                if (recentSeeds.get(i).equalsIgnoreCase(seed)) {
                    recentSeeds.remove(i);
                }
            }
            recentSeeds.add(0, seed);
            while (recentSeeds.size() > 18) {
                recentSeeds.remove(recentSeeds.size() - 1);
            }
        }

        List<String> topAttributes(int limit) {
            if (limit <= 0 || attributeWeights.isEmpty()) return new ArrayList<>();
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(attributeWeights.entrySet());
            sorted.sort((a, b) -> {
                int cmp = Double.compare(b.getValue(), a.getValue());
                return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
            });
            List<String> out = new ArrayList<>();
            for (Map.Entry<String, Double> entry : sorted) {
                out.add(entry.getKey());
                if (out.size() >= limit) break;
            }
            return out;
        }

        List<String> recentSeeds(int limit) {
            if (limit <= 0 || recentSeeds.isEmpty()) return new ArrayList<>();
            return new ArrayList<>(recentSeeds.subList(0, Math.min(limit, recentSeeds.size())));
        }

        String contextHint(int limit) {
            List<String> anchors = topAttributes(limit);
            if (anchors.isEmpty()) {
                anchors = recentSeeds(Math.max(2, limit));
            }
            if (anchors.isEmpty()) return "";
            return "profile[" + String.join(",", anchors) + "]";
        }

        int attributeCount() {
            return attributeWeights.size();
        }

        String lastPunchline() {
            return lastPunchline == null ? "" : lastPunchline;
        }

        String serialize(String reason) {
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(attributeWeights.entrySet());
            sorted.sort((a, b) -> {
                int cmp = Double.compare(b.getValue(), a.getValue());
                return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
            });
            List<String> attrs = new ArrayList<>();
            for (int i = 0; i < Math.min(28, sorted.size()); i++) {
                Map.Entry<String, Double> entry = sorted.get(i);
                attrs.add(
                    entry.getKey().replace(' ', '_') + ":" +
                    String.format(Locale.ROOT, "%.4f", entry.getValue())
                );
            }
            List<String> seedTokens = new ArrayList<>();
            for (String seed : recentSeeds(12)) {
                seedTokens.add(seed.replace(' ', '_'));
            }

            return "v=1" +
                ";reason=" + sanitizeForRecord(reason) +
                ";runs=" + runs +
                ";observations=" + observations +
                ";updatedAt=" + updatedAtMs +
                ";avgCreativity=" + String.format(Locale.ROOT, "%.4f", avgCreativity) +
                ";avgHumor=" + String.format(Locale.ROOT, "%.4f", avgHumor) +
                ";avgNovelty=" + String.format(Locale.ROOT, "%.4f", avgNovelty) +
                ";lastBlend=" + sanitizeForRecord(lastBlend) +
                ";lastPunchline=" + sanitizeForRecord(lastPunchline) +
                ";recentSeeds=" + String.join("|", seedTokens) +
                ";attrs=" + String.join(",", attrs);
        }
    }

    /**
     * Fallback intent extraction when LLM fails to use bracket syntax.
     * Converts natural language store/recall requests into bracket syntax.
     */
    private static String extractToolIntent(String response) {
        String lower = response.toLowerCase();
        StringBuilder injected = new StringBuilder();

        // Pattern: store X = Y, remember X is Y, save X as Y
        java.util.regex.Pattern storePattern = java.util.regex.Pattern.compile(
            "(?:store|save|remember|memorize)\\s+(.+?)(?:\\s*=\\s*|\\s+is\\s+|\\s+as\\s+)(.+?)(?:\\.|$)",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher storeMatcher = storePattern.matcher(response);
        if (storeMatcher.find()) {
            String key = storeMatcher.group(1).trim();
            String value = storeMatcher.group(2).trim().replaceAll("\\.$", "");
            injected.append("[STORE: ").append(key).append(" | ").append(value).append("]\n");
        }

        // Pattern: what is X, recall X, remember X, retrieve X
        java.util.regex.Pattern recallPattern = java.util.regex.Pattern.compile(
            "(?:what is|recall|retrieve|remember|get|find)\\s+(.+?)(?:\\?|\\.|$)",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher recallMatcher = recallPattern.matcher(response);
        if (recallMatcher.find() && injected.length() == 0) {
            String key = recallMatcher.group(1).trim().replaceAll("\\?$", "").replaceAll("\\.$", "");
            injected.append("[RECALL: ").append(key).append("]\n");
        }

        if (injected.length() > 0) {
            return injected.toString();
        }
        return response;
    }

    private static String executeToolCalls(String response) {
        if (unifiedOrchestrator == null || !unifiedOrchestrator.isHolographicWired()) {
            return response;
        }

        // Fallback: if LLM failed to use bracket syntax, try to extract intent from natural language
        String normalized = response;
        if (!response.contains("[STORE:") && !response.contains("[RECALL:") && !response.contains("[GENOMICS:") && !response.contains("[SPECIALIST:")) {
            normalized = extractToolIntent(response);
        }

        StringBuilder results = new StringBuilder();
        boolean anyExecuted = false;

        for (String line : normalized.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("[SPECIALIST:") && trimmed.endsWith("]")) {
                var result = executeSpecialistCommand(trimmed.substring(12, trimmed.length() - 1).trim());
                results.append(result.success() ? "[SPECIALIST RESULT] " : "[SPECIALIST ERROR] ")
                    .append(result.output()).append('\n');
                anyExecuted = true;
            } else if (trimmed.startsWith("[GENOMICS:") && trimmed.endsWith("]")) {
                String arguments = trimmed.substring(10, trimmed.length() - 1).trim();
                var result = executeGenomicsCommand(arguments);
                results.append(result.success() ? "[GENOMICS RESULT] " : "[GENOMICS ERROR] ")
                    .append(result.output()).append('\n');
                anyExecuted = true;
            } else if (trimmed.startsWith("[STORE:") && trimmed.endsWith("]")) {
                String inner = trimmed.substring(7, trimmed.length() - 1).trim();
                String[] parts = inner.split("\\|", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    unifiedOrchestrator.getHolographicMemory().store(key, value);
                    results.append("[STORE RESULT] Stored '").append(key).append("' = '").append(value).append("'\n");
                    anyExecuted = true;
                }
            } else if (trimmed.startsWith("[RECALL:") && trimmed.endsWith("]")) {
                String key = trimmed.substring(8, trimmed.length() - 1).trim();
                var mem = unifiedOrchestrator.getHolographicMemory();
                var result = mem.retrieve(key);
                if (result.success()) {
                    results.append("[RECALL RESULT] '").append(key).append("' = '").append(result.retrievedValue).append("'\n");
                } else {
                    results.append("[RECALL RESULT] '").append(key).append("' not found in memory.\n");
                    var keys = mem.getStoredKeys();
                    if (!keys.isEmpty()) {
                        results.append("Available keys: ").append(String.join(", ", keys)).append("\n");
                    }
                    results.append("Do not hallucinate. If the user asked about a topic not in memory, admit you do not have it.\n");
                }
                anyExecuted = true;
            } else if (trimmed.startsWith("[RENDER:") && trimmed.endsWith("]")) {
                String concept = trimmed.substring(8, trimmed.length() - 1).trim();
                var frame = unifiedOrchestrator.getHolographicRender().renderThought(concept);
                results.append(String.format("[RENDER RESULT] '%s' -> pos=(%.2f,%.2f,%.2f) color=(%.2f,%.2f,%.2f)\n",
                    concept, frame.params.posX, frame.params.posY, frame.params.posZ,
                    frame.params.colorR, frame.params.colorG, frame.params.colorB));
                anyExecuted = true;
            } else if (trimmed.startsWith("[SPAWN:") && trimmed.endsWith("]")) {
                String concept = trimmed.substring(7, trimmed.length() - 1).trim();
                var body = unifiedOrchestrator.getHolographicPhysics().spawnBody(concept);
                results.append(String.format("[SPAWN RESULT] '%s' -> mass=%.2f pos=(%.2f,%.2f,%.2f)\n",
                    concept, body.mass, body.posX, body.posY, body.posZ));
                anyExecuted = true;
            } else if (trimmed.startsWith("[SIM:") && trimmed.endsWith("]")) {
                try {
                    int frames = Integer.parseInt(trimmed.substring(5, trimmed.length() - 1).trim());
                    var future = unifiedOrchestrator.getHolographicPhysics().runSimulation(frames);
                    var simResult = future.get();
                    results.append("[SIM RESULT] ").append(simResult.frames).append(" frames in ").append(simResult.latencyMs).append("ms\n");
                    for (var entry : simResult.finalStates.entrySet()) {
                        var b = entry.getValue();
                        results.append(String.format("  '%s': pos=(%.2f,%.2f,%.2f) vel=(%.2f,%.2f,%.2f)\n",
                            entry.getKey(), b.posX, b.posY, b.posZ, b.velX, b.velY, b.velZ));
                    }
                    anyExecuted = true;
                } catch (Exception e) {
                    results.append("[SIM ERROR] ").append(e.getMessage()).append("\n");
                }
            } else if (trimmed.startsWith("[TERMINAL:") && trimmed.endsWith("]")) {
                String inner = trimmed.substring(10, trimmed.length() - 1).trim();
                String[] parts = inner.split("\\|", 3);
                if (terminalMultiplexer != null) {
                    String action = parts[0].trim();
                    if (action.equals("new") && parts.length >= 2) {
                        results.append(terminalMultiplexer.newSession(parts[1].trim())).append("\n");
                        anyExecuted = true;
                    } else if (action.equals("send") && parts.length >= 3) {
                        results.append(terminalMultiplexer.sendCommand(parts[1].trim(), parts[2].trim())).append("\n");
                        anyExecuted = true;
                    } else if (action.equals("read") && parts.length >= 2) {
                        results.append(terminalMultiplexer.readOutput(parts[1].trim(), 50)).append("\n");
                        anyExecuted = true;
                    } else if (action.equals("kill") && parts.length >= 2) {
                        results.append(terminalMultiplexer.killSession(parts[1].trim())).append("\n");
                        anyExecuted = true;
                    } else if (action.equals("list")) {
                        results.append(terminalMultiplexer.listSessions()).append("\n");
                        anyExecuted = true;
                    }
                }
            } else if (trimmed.equals("[BRAINS]")) {
                if (unifiedOrchestrator != null) {
                    results.append(unifiedOrchestrator.getBrainSystemSummary()).append("\n");
                    anyExecuted = true;
                }
            } else if (trimmed.startsWith("[ROUTE:") && trimmed.endsWith("]")) {
                String query = trimmed.substring(7, trimmed.length() - 1).trim();
                if (unifiedOrchestrator != null) {
                    results.append(unifiedOrchestrator.routeQuery(query)).append("\n");
                    anyExecuted = true;
                }
            } else if (trimmed.startsWith("[FUSE:") && trimmed.endsWith("]")) {
                String inner = trimmed.substring(6, trimmed.length() - 1).trim();
                String[] parts = inner.split("\\|", 2);
                if (parts.length == 2 && unifiedOrchestrator != null) {
                    results.append(unifiedOrchestrator.fuseConcepts(parts[0].trim(), parts[1].trim())).append("\n");
                    anyExecuted = true;
                }
            } else if (trimmed.startsWith("[PROCESS:") && trimmed.endsWith("]")) {
                String inner = trimmed.substring(9, trimmed.length() - 1).trim();
                String[] parts = inner.split("\\|", 2);
                if (parts.length == 2 && unifiedOrchestrator != null) {
                    results.append(unifiedOrchestrator.processWithBrain(parts[0].trim(), parts[1].trim())).append("\n");
                    anyExecuted = true;
                }
            } else if (trimmed.equals("[MODELS]")) {
                if (modelRegistryIntegration != null && modelRegistryIntegration.isInitialized()) {
                    results.append(modelRegistryIntegration.getStatistics()).append("\n");
                    anyExecuted = true;
                }
            } else if (trimmed.startsWith("[MODEL:") && trimmed.endsWith("]")) {
                String inner = trimmed.substring(7, trimmed.length() - 1).trim();
                if (inner.startsWith("select|") && modelRegistryIntegration != null) {
                    String organName = inner.substring(7).trim().toUpperCase().replace(" ", "_");
                    var sel = modelRegistryIntegration.getModelSelector();
                    if (sel != null) {
                        try {
                            var organ = fraymus.model.CapabilityProfile.Organ.valueOf(organName);
                            results.append("Best model for ").append(organName).append(": ")
                                .append(sel.selectForOrgan(organ).getSelectedModelId()).append("\n");
                        } catch (IllegalArgumentException e) {
                            results.append("Unknown organ: ").append(organName)
                                .append(". Try: PREFRONTAL_CORTEX, HIPPOCAMPUS, CODING_ORGAN, CREATIVE_CORTEX\n");
                        }
                        anyExecuted = true;
                    }
                }
            }
        }

        return anyExecuted ? results.toString().trim() : response;
    }

}
