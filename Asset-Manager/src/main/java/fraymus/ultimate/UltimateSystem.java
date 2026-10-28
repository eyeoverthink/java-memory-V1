package fraymus.ultimate;

import com.eyeoverthink.lazarus.LazarusEngine;
import com.fraymus.core.Intent;
import com.fraymus.omega.Amygdala;
import fraymus.contract.SystemStateContract;
import fraymus.brain.unified.DataAbstractionEngine;
import fraymus.core.OllamaBridge;
import fraymus.facade.ExperimentFacade;
import fraymus.facade.PhiNodeFacade;
import fraymus.facade.SelfCodeEngine;
import fraymus.field.F0StressRunner;
import fraymus.llm.ModelAbstractionLayer;
import fraymus.llm.ModelDescriptor;
import fraymus.llm.ModelRole;
import fraymus.llm.ModelSelectionResult;
import fraymus.logic.BabelTransmuter;
import fraymus.neural.AeonAbsolute;
import fraymus.neural.AeonBabel;
import fraymus.neural.AeonWillowCrusher;
import fraymus.ollama.Transmudder;
import fraymus.model.AkashicModelMemory;
import fraymus.model.CapabilityProfile;
import fraymus.model.EvaluationHarness;
import fraymus.model.ModelRegistry;
import fraymus.model.ModelRegistryIntegration;
import fraymus.model.PromptCompiler;
import fraymus.tpu.TpuReasoningEngine;
import fraymus.ultimate.UltimateSubsystemRegistry.UltimateSubsystem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Single shell that abstracts the major Fraymus and Fraynix branches into one
 * operator surface.
 */
public final class UltimateSystem {
    private static final DateTimeFormatter SESSION_STAMP =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT).withZone(java.time.ZoneOffset.UTC);
    private static final Pattern URL_TOKEN = Pattern.compile("https?://[^\\s\"'<>]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern QUOTED_FILE_TOKEN = Pattern.compile(
        "\"([^\"]+\\.(?:pdf|txt|md|java|json|xml|csv|html?|ya?ml|log))\"|'([^']+\\.(?:pdf|txt|md|java|json|xml|csv|html?|ya?ml|log))'",
        Pattern.CASE_INSENSITIVE
    );
    private static final Set<String> INGEST_EXTENSIONS = Set.of(
        "pdf", "txt", "md", "java", "json", "xml", "csv", "html", "htm", "yaml", "yml", "log"
    );
    private static final long AI_RUN_TIMEOUT_SECONDS = 180L;
    private static final long AI_RUN_LOCK_WAIT_MS = 250L;
    private static final int AI_RUN_MAX_COMMAND_CHARS = 512;
    private static final String AI_RUN_POLICY_VERSION = "fraynix.ai-run.policy.v2";
    private static final int AI_RUN_CAPTURE_CHAR_LIMIT = 64_000;
    private static final int FRAYNIX_BOOT_PERSISTENT_BUFFER_LINES = 4_096;
    private static final String FRAYNIX_BOOT_EOF_MARKER = "__FRAYNIX_BOOT_EOF__";
    private static final Set<String> AI_RUN_BLOCKED_HEADS = Set.of(
        "ai", "run", "quit", "launch", "selfbuild", "code", "export",
        "persist", "refresh"
    );
    private static final Set<String> AI_RUN_ALLOWED_IN_PROCESS_HEADS = Set.of(
        "help", "status", "session", "systems", "system",
        "search", "show", "readmes", "commands", "catalog", "paths",
        "goto", "jump", "route", "math", "willow", "resume", "continuity",
        "abstraction",
        "transmudder", "mutter", "mudder", "native", "absolute", "babel",
        "models", "model", "metamorphic"
        , "tpu"
    );
    private static final Set<String> AI_ROUTER_FORCED_HEADS = Set.of(
        "native", "willow", "transmudder", "mutter", "mudder",
        "tpu",
        "sentient", "rcortex", "rainbow",
        "absolute", "babel", "kronos", "omniscience", "omni",
        "libabs", "audio", "vision", "multimodal", "gen",
        "prove", "diagnose", "launch"
    );
    private static final Pattern BABEL_SAFE_PAYLOAD =
        Pattern.compile("^[a-z0-9 _.,:+\\-*/()\\[\\]%?=!]+$", Pattern.CASE_INSENSITIVE);
    private static final Set<String> EXTENDED_MENU_HEADS = Set.of(
        "science", "moe", "moa", "mroute", "absorb", "fs", "physics", "phi", "think", "generate", "build",
        "cortex", "aeon", "absolute", "openclaw", "hrm", "nbs", "teacher", "rate", "rcortex", "rainbow",
        "sentient", "libabs", "audio", "vision", "multimodal", "gen", "wiring", "singularity", "sing", "aubo",
        "tpu",
        "tachyon", "kronos", "omniscience", "omni", "demiurge", "apotheosis", "omega", "babel", "willow",
        "aether", "braid", "learn", "pulse", "retro", "emf", "agora", "lazarus", "stack", "morse",
        "entangle", "qbox", "collide", "forge", "echaos", "swarm", "nexus", "flygm", "omnicast",
        "spatial", "gravity", "fusion", "suit", "miving", "selfcode", "experiment", "phinode", "engine",
        "metalearner", "causal", "goals", "selfref", "collective",
        "wkb", "genmodule", "gameai", "embed", "genome", "vidpipe",
        "hypervector", "hypermemory", "hypersynapse", "dream", "harmonic", "paradox", "mirrorloop",
        "genesiszero", "genesisprime", "genesisgpt", "genesisvoice", "genesisboot", "coder",
        "tesseract", "livingmind", "sleepcycle", "neuroquant", "nanoswarm", "geiger", "hypercomm",
        "mirrortest", "akashicread", "tachionic", "supercession", "trainer", "protein", "cancer", "drug",
        "volatile", "hydra", "continuity", "deadmans", "steganographer", "overthinks",
        "webeater", "inject", "knowledgegut", "blackholeproto", "portal", "genesisengine",
        "fraynixorg", "agentboot", "phoenix", "systemzero", "hivemind", "planetary", "foreverloop",
        "fraymusdaemon", "carbon", "terminus", "fraymusos", "godmode", "maxheadroom", "frayshell",
        "fraynixhive", "abstractkernel", "fraynet", "cymatic", "benchmark", "validate", "rigorous",
        "abstraction",
        "models", "model", "metamorphic",
        "exodus", "desktop"
    );
    private static final Set<String> AI_RUN_ALLOWED_FRAYNIX_HEADS = buildAiRunFraynixHeadAllowlist();
    private static final int AI_UNISON_MAX_AUTONOMOUS_RUNS = 3;
    private static final List<String> AI_UNISON_AUTONOMOUS_PRIORITY = List.of(
        "native", "tpu", "willow", "transmudder", "absolute", "babel",
        "sentient", "rcortex", "rainbow", "teacher", "nbs", "libabs",
        "sing", "singularity", "gen", "multimodal", "prove", "diagnose", "wiring"
    );
    private static final Set<String> AI_UNISON_WHOLE_SYSTEM_HEADS = Set.of(
        "native", "tpu", "willow", "transmudder", "absolute", "babel",
        "sentient", "rcortex", "teacher", "nbs", "continuity"
    );
    private static final List<String> WHOLE_SYSTEM_PROBE_PRIORITY = List.of(
        "native", "continuity", "willow", "babel", "absolute", "tpu",
        "sentient", "rcortex", "rainbow", "teacher", "nbs",
        "morse", "emf", "aubo"
    );
    private static final List<String> RESUME_LIVE_PROBE_PRIORITY = List.of(
        "native", "continuity", "willow", "babel", "absolute", "tpu"
    );
    private static final Set<String> BOOT_ONLY_VISIBLE_HEADS = Set.of(
        "morse", "continuity", "hydra", "deadmans", "steganographer", "overthinks",
        "knowledgegut", "webeater", "agentboot", "frayshell", "fraynixorg", "desktop",
        "maxheadroom", "portal", "genesisengine"
    );
    private static final int ABSTRACTION_MAX_CONTEXT_ARTIFACTS = 8;
    private static final int ABSTRACTION_MAX_HINTS = 8;
    private static final int ABSTRACTION_MAX_PROVENANCE = 8;
    private static final int ABSTRACTION_MAX_FILE_EXCERPT_CHARS = 1200;
    private static final int ABSTRACTION_MEMORY_READ_LIMIT = 800;
    private static final int ABSTRACTION_OUTCOME_READ_LIMIT = 1200;
    private static final double ABSTRACTION_SUCCESS_EMA_ALPHA = 0.25;

    private static Set<String> buildAiRunFraynixHeadAllowlist() {
        LinkedHashSet<String> allowlist = new LinkedHashSet<>(EXTENDED_MENU_HEADS);
        allowlist.addAll(Set.of(
            "help", "status", "resume", "continuity", "wiring", "experts", "agent", "codegen",
            "improve", "diagnose", "prove", "exit"
        ));
        return Set.copyOf(allowlist);
    }

    private final Path assetManagerDir;
    private final Path repoRoot;
    private final Path workspaceRoot;
    private final ExperimentFacade experimentFacade = new ExperimentFacade();
    private final PhiNodeFacade phiNodeFacade = new PhiNodeFacade();
    private final SelfCodeEngine selfCodeEngine = new SelfCodeEngine();
    private final TpuReasoningEngine tpuReasoningEngine = new TpuReasoningEngine();
    private final Transmudder transmudder = new Transmudder();
    private final ModelAbstractionLayer modelAbstractionLayer = ModelAbstractionLayer.defaultLayer();
    private BabelTransmuter babelTransmuter;
    private final UltimateArchitectureExporter architectureExporter;
    private final FraynixCommandCatalog fraynixCommandCatalog;
    private final FraymusEngineCommandCatalog engineCommandCatalog;
    private final UltimateNativeCommandCatalog nativeCommandCatalog;
    private final UltimateImmortalKernel immortalKernel;
    private final UltimateSelfBuildHarness selfBuildHarness;
    private final List<UltimateCommandCatalog> commandCatalogs;

    private UltimateSubsystemRegistry registry;
    private UltimateKnowledgeIndex knowledgeIndex;
    private UltimateOperatorManifest operatorManifest;
    private final ReentrantLock aiRunExecutionLock = new ReentrantLock();
    private final ReentrantLock fraynixBootSessionLock = new ReentrantLock();
    private final ReentrantLock continuityLock = new ReentrantLock();
    private volatile String aiRunInFlightCommand = "";
    private volatile PersistentFraynixBootSession fraynixBootSession;
    private volatile ContinuitySnapshot continuitySnapshot;
    private volatile LiveCapabilitySnapshot latestCapabilityProbeSnapshot = LiveCapabilitySnapshot.empty();

    public UltimateSystem() {
        this(discoverAssetManagerDir(Paths.get("").toAbsolutePath().normalize()));
    }

    public UltimateSystem(Path assetManagerDir) {
        this(assetManagerDir, null);
    }

    UltimateSystem(Path assetManagerDir, UltimateSelfBuildHarness.ProofBatteryRunner proofBatteryRunner) {
        this.assetManagerDir = assetManagerDir.toAbsolutePath().normalize();
        this.repoRoot = Optional.ofNullable(this.assetManagerDir.getParent()).orElse(this.assetManagerDir);
        this.workspaceRoot = Optional.ofNullable(this.repoRoot.getParent()).orElse(this.repoRoot);
        this.architectureExporter = new UltimateArchitectureExporter(this.assetManagerDir, this.repoRoot);
        this.fraynixCommandCatalog = FraynixCommandCatalog.createDefault();
        this.engineCommandCatalog = FraymusEngineCommandCatalog.createDefault();
        this.nativeCommandCatalog = UltimateNativeCommandCatalog.createDefault();
        this.immortalKernel = new UltimateImmortalKernel(this.assetManagerDir);
        this.selfBuildHarness = new UltimateSelfBuildHarness(this.assetManagerDir, proofBatteryRunner);
        this.commandCatalogs = List.of(fraynixCommandCatalog, engineCommandCatalog, nativeCommandCatalog);
        this.registry = UltimateSubsystemRegistry.createDefault(this.repoRoot, this.assetManagerDir);
        this.knowledgeIndex = UltimateKnowledgeIndex.build(this.repoRoot);
        this.operatorManifest = UltimateOperatorManifest.create(this.assetManagerDir, this.commandCatalogs);
        try {
            ensureContinuitySnapshot(false);
        } catch (Exception ignored) {
            this.continuitySnapshot = ContinuitySnapshot.unavailable(
                architectureExporter.defaultOutputDir().resolve("fraynix-continuity-memory.md"),
                architectureExporter.defaultOutputDir().resolve("fraynix-math-canon.md"),
                architectureExporter.defaultOutputDir().resolve("fraynix-axiom-ledger.md"),
                architectureExporter.defaultOutputDir().resolve("fraynix-truth-registry.json"),
                buildVisibleMenuHeads(),
                buildMenuSectionSamples()
            );
        }
    }

    public Path assetManagerDir() {
        return assetManagerDir;
    }

    public Path repoRoot() {
        return repoRoot;
    }

    public Path workspaceRoot() {
        return workspaceRoot;
    }

    public UltimateSubsystemRegistry registry() {
        return registry;
    }

    public UltimateKnowledgeIndex knowledgeIndex() {
        return knowledgeIndex;
    }

    public List<UltimateCommandCatalog> commandCatalogs() {
        return commandCatalogs;
    }

    public UltimateImmortalKernel immortalKernel() {
        return immortalKernel;
    }

    public UltimateOperatorManifest operatorManifest() {
        return operatorManifest;
    }

    UltimateSelfBuildHarness selfBuildHarness() {
        return selfBuildHarness;
    }

    public UltimateArchitectureExporter.ExportArtifact exportArtifacts(Path target) {
        return architectureExporter.export(registry, knowledgeIndex, commandCatalogs, target);
    }

    public ContinuitySnapshot continuitySnapshot() {
        return ensureContinuitySnapshot(false);
    }

    public ContinuitySnapshot refreshContinuitySnapshot() {
        return ensureContinuitySnapshot(true);
    }

    public LiveCapabilitySnapshot latestCapabilityProbeSnapshot() {
        return latestCapabilityProbeSnapshot;
    }

    public void refreshState() {
        this.registry = UltimateSubsystemRegistry.createDefault(repoRoot, assetManagerDir);
        this.knowledgeIndex = UltimateKnowledgeIndex.build(repoRoot);
        this.operatorManifest = UltimateOperatorManifest.create(assetManagerDir, commandCatalogs);
        this.continuitySnapshot = null;
    }

    public int sectionCount() {
        return totalSectionCount();
    }

    public int commandCount() {
        return totalCommandCount();
    }

    public Map<String, Object> aiRunLaneSnapshot() {
        boolean busy = aiRunExecutionLock.isLocked();
        int queueDepth = Math.max(0, aiRunExecutionLock.getQueueLength());
        String inFlight = aiRunInFlightCommand == null ? "" : aiRunInFlightCommand.trim();

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("status", busy ? "busy" : "idle");
        snapshot.put("busy", busy);
        snapshot.put("queueDepth", queueDepth);
        snapshot.put("hasQueuedThreads", aiRunExecutionLock.hasQueuedThreads());
        snapshot.put("inFlightCommand", inFlight.isBlank() ? "" : compactBlock(inFlight, 1, 160));
        snapshot.put("lockWaitMs", AI_RUN_LOCK_WAIT_MS);
        snapshot.put("timeoutSeconds", AI_RUN_TIMEOUT_SECONDS);
        snapshot.put("maxCommandChars", AI_RUN_MAX_COMMAND_CHARS);
        snapshot.put("policyVersion", AI_RUN_POLICY_VERSION);
        return snapshot;
    }

    public static void main(String[] args) {
        UltimateSystem system = new UltimateSystem();
        if (args.length > 0) {
            String response = system.execute(String.join(" ", args));
            if (response != null && !response.isBlank()) {
                System.out.println(response);
            }
            system.shutdown();
            return;
        }
        system.runInteractive();
    }

    public void shutdown() {
        shutdownPersistentFraynixBootSession();
        immortalKernel.shutdownQuietly();
    }

    public String execute(String rawCommand) {
        if (rawCommand == null) {
            return help();
        }

        String command = stripLeadingInvisible(rawCommand).trim();
        if (command.isEmpty()) {
            return "";
        }

        String[] parts = command.split("\\s+", 2);
        String verb = parts[0].toLowerCase(Locale.ROOT);
        String remainder = parts.length > 1 ? parts[1].trim() : "";

        if (immortalKernel.canHandle(verb)) {
            return immortalKernel.execute(command);
        }

        return switch (verb) {
            case "help" -> help();
            case "status" -> status();
            case "ai" -> handleAi(remainder);
            case "resume" -> handleResume(remainder);
            case "continuity" -> handleContinuity(remainder);
            case "abstraction" -> handleAbstraction(remainder);
            case "prove" -> handleProve(remainder);
            case "session" -> handleSession(remainder);
            case "systems" -> registry.renderSummary();
            case "system" -> registry.renderDetails(remainder);
            case "abstract", "map", "synthesize" -> knowledgeIndex.abstractionReport(registry);
            case "search" -> knowledgeIndex.searchReport(remainder, 10);
            case "show" -> knowledgeIndex.fileReport(remainder);
            case "readmes" -> knowledgeIndex.readmeReport(10);
            case "models", "model", "metamorphic" -> handleModels(remainder);
            case "commands", "catalog" -> commandSurface(remainder);
            case "paths" -> paths();
            case "refresh" -> refresh();
            case "export", "persist" -> exportArchitecture(remainder);
            case "demo" -> handleDemo(remainder);
            case "math" -> handleMath(remainder);
            case "tpu" -> handleTpu(remainder);
            case "node" -> handleNode(remainder);
            case "code" -> handleCode(remainder);
            case "selfbuild" -> handleSelfBuild(remainder);
            case "willow" -> handleWillow(remainder);
            case "transmudder", "mutter", "mudder" -> handleTransmudder(remainder);
            case "goto", "jump" -> route(remainder);
            case "route" -> route(remainder);
            case "launch" -> launch(remainder);
            case "quit", "exit" -> null;
            default -> "Unknown command: " + verb + "\nTry: help";
        };
    }

    private void runInteractive() {
        System.out.println(status());
        System.out.println("Type 'help' to see the unified command surface.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("ULTIMATE> ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String response = execute(scanner.nextLine());
                if (response == null) {
                    break;
                }
                if (!response.isBlank()) {
                    System.out.println(response);
                }
            }
        } finally {
            shutdown();
        }
    }

    private String status() {
        ContinuitySnapshot continuity = continuitySnapshot();
        StringBuilder sb = new StringBuilder();
        sb.append("ULTIMATE SYSTEM READY\n");
        sb.append("Root: ").append(repoRoot.toString().replace('\\', '/')).append("\n");
        sb.append("Subsystems: ").append(registry.all().size())
            .append(" | Families: ").append(registry.familyCounts().size()).append("\n");
        sb.append(knowledgeIndex.overview()).append("\n");
        sb.append("Command surfaces: ").append(commandCatalogs.size())
            .append(" | Sections: ").append(totalSectionCount())
            .append(" | Catalogued commands: ").append(totalCommandCount()).append("\n");
        sb.append("Pending human reviews: ").append(selfBuildHarness.listReviewRequests().stream()
            .filter(request -> "pending".equalsIgnoreCase(request.status()))
            .count()).append("\n");
        sb.append("Continuity: ").append(continuity.loaded() ? "loaded" : "degraded")
            .append(" | Rules: ").append(continuity.resumeRules().size())
            .append(" | Visible heads: ").append(continuity.visibleMenuHeads().size())
            .append(" | Deterministic lanes: ").append(continuity.deterministicLaneSummaries().size()).append("\n");
        sb.append("Metamorphic core: seeded descriptors=").append(modelAbstractionLayer.registry().all().size())
            .append(" | observed local model surfaces=").append(readObservedLocalModels().size()).append("\n");
        sb.append("Surfaces: fraynix, engine, native\n");
        sb.append("Facades: experiment, phinode, selfcode\n");
        sb.append("Living organs: willow quantum kernel | transmudder soul\n");
        sb.append("Primary moves: resume | continuity status | abstraction status | models init | models select coding <task> | ai <question> | session | demo guided | systems | abstract | commands | native status | tpu status | search <term> | show <path> | math ... | node ... | code ... | willow ... | transmudder ... | launch <id>");
        return sb.toString();
    }

    private String help() {
        return """
            ULTIMATE COMMANDS
            status                     Current abstraction state
            resume                     Load continuity memory and print the current pickup state
            continuity status          Alias for resume summary
            continuity rules           Show the forced continuity/resume rules
            continuity menus           Show visible Fraynix menu sections and sampled commands
            continuity refresh         Refresh export-backed continuity artifacts and reload them
            abstraction status         Show global AI abstraction memory status
            abstraction tail [n]       Show the last n abstraction records (default 5)
            abstraction delta          Show the last-observed vs previous abstraction delta
            models init                Initialize the ModelDescriptor + CapabilityProfile registry report
            models status              Show seeded families, observed local models, and role leaders
            models families            List seeded open-model families and capability surfaces
            models select <role> [task] Rank models for reasoning/coding/reflex/memory/creative work
            metamorphic ...            Alias for models ...
            ai <question>              Grounded AI lane for state/proof introspection
            ai run <fraynix-command>  Execute an allowlisted routed command with timeout/error envelope
            session                    Recent proof, review, and substrate state
            systems                    List the major subsystems
            system <id>                Explain one subsystem
            abstract                   Synthesize the whole repo into one architecture
            readmes                    Show README anchors
            commands                   Show all command surfaces
            commands <surface>         Show one surface summary (fraynix | engine | native)
            commands <section>         Show one unique command group
            commands <surface> <section> Show one section from a specific surface
            search <term>              Search indexed docs, code, and build files
            show <path-fragment>       Preview one indexed file
            paths                      Show key roots
            refresh                    Rebuild the knowledge index
            export [dir]               Write persistent architecture + graph artifacts
            persist [dir]              Alias for export

            demo guided                Run a readable Fraynix proof walkthrough and write a session artifact
            prove router               Run end-to-end router proof (CLI + companion API) and write artifacts

            math prime <n>
            math factor <n>
            math tunnel [bits]
            math hash <text>
            math rsa [bits]

            tpu status
            tpu infer <prompt>
            tpu reason [depth] <prompt>
            tpu synapse <count>
            tpu selfcreate <objective>
            tpu reset

            node spawn <name> [x y]
            node spiral <name>
            node list
            node tick [dt]
            node simulate [ticks]
            node think <name> <1,0,1,...>
            node reproduce <a> <b> <child>
            node code <name>
            node status

            code transform <snippet>
            code mutate <snippet>
            code validate <snippet>
            code inject <snippet>
            code store <tags> :: <content>
            code fingerprint <content>
            code status

            selfbuild <goal>          Let Fraynix generate, compile, execute, and judge selfbuild candidates
            selfbuild file <path-fragment> :: <goal>
                                     Let Fraynix propose and judge edits against a real local source file
            selfbuild files <path-fragment> :: <goal>
                                    Let Fraynix rank a small matched set of source files and judge each one
            selfbuild promote file <path-fragment> :: <goal>
                                    Let Fraynix emit a promotable diff/report for one judged local source file
            selfbuild promote files <path-fragment> :: <goal>
                                    Let Fraynix compare a ranked file set and emit promotion reports
            selfbuild prove-promote file <path-fragment> :: <goal>
                                    Let Fraynix require its living proof battery before approving a promotion
            selfbuild prove-promote files <path-fragment> :: <goal>
                                    Let Fraynix compare a ranked file set under proof-driven promotion
            selfbuild review-promote file <path-fragment> :: <goal>
                                    Let Fraynix stop at a human approval gate after prove-promotion
            selfbuild reviews         Show current review requests
            selfbuild approve <review-id>
                                    Apply a reviewed promoted snapshot into the live source tree
            selfbuild reject <review-id> [:: note]
                                    Reject a pending review request and keep the live tree untouched

            willow status
            willow hadamard <n>
            willow entangle <q1> <q2>
            willow echo
            willow measure <q>
            willow spectrum
            willow proof [n]         Compact live proof sweep for the Willow quantum kernel

            transmudder file <path>
            transmudder proof <path> Digest a file and return proof-oriented soul telemetry
            transmudder stats
            transmudder essence
            transmudder purge

            native status              Show the in-process immortal kernel state
            native events              Show recent immortal-kernel events
            native shutdown            Stop native background systems
            aether | braid | learn <concept> | pulse <concept>
            entangle ... | qbox ... | collide ... | forge ... | echaos ...
            nexus ... | omnicast ... | spatial ... | gravity ... | fusion ...
            suit wrap ... | miving ...

            goto <section>             Route from a command group to its subsystem(s)
            goto <surface> <section>   Route from a specific command surface
            route <system-id>          Show the launch route for a subsystem
            route <section>            Show route info for a unique command group
            route <surface> <section>  Show route info for a specific surface section
            launch <system-id>         Run a wired subsystem task
            launch <section>           Launch the default subsystem for a unique command group
            launch <surface> <section> Launch from a specific command surface
            quit                       Exit the shell
            """.stripIndent();
    }

    private String handleModels(String remainder) {
        String trimmed = remainder == null ? "" : remainder.trim();
        if (trimmed.isBlank()) {
            return renderModelRegistryReport(true);
        }

        String[] parts = trimmed.split("\\s+", 2);
        String subcommand = parts[0].toLowerCase(Locale.ROOT);
        String tail = parts.length > 1 ? parts[1].trim() : "";

        return switch (subcommand) {
            case "init", "initialize", "status", "registry" -> renderModelRegistryReport(true);
            case "families" -> renderModelFamilyReport();
            case "select", "route", "pick", "rank" -> renderModelSelectionReport(tail);
            default -> "Usage: models [init|status|families|select <reasoning|coding|reflex|memory|creative> [task hint]]";
        };
    }

    private String renderModelRegistryReport(boolean includeRoleLeaders) {
        List<String> observedLocalModels = readObservedLocalModels();
        StringBuilder sb = new StringBuilder();
        sb.append("FRAYNIX METAMORPHIC CORE\n");
        sb.append("Registry status: initialized\n");
        sb.append("ModelDescriptor registry: online\n");
        sb.append("CapabilityProfile registry: online\n");
        sb.append("Seeded descriptors: ").append(modelAbstractionLayer.registry().all().size()).append("\n");
        sb.append("Families: ").append(modelAbstractionLayer.registry().summarizeFamilies()).append("\n");
        sb.append("Observed local models: ").append(observedLocalModels.size()).append("\n");
        if (!observedLocalModels.isEmpty()) {
            sb.append("Local roster: ").append(String.join(", ", observedLocalModels)).append("\n");
        }
        SubstrateLedgerSummary ledger = readSubstrateLedgerSummary();
        sb.append("Observed runtime backends: ")
            .append(ledger.runtimeBackends().isEmpty() ? "(none)" : String.join(", ", ledger.runtimeBackends()))
            .append("\n");
        sb.append("Contract roles: reasoning, coding, reflex, memory, creative\n");
        sb.append("Selection policy: cosine-style capability routing with local-surface preference and task-hint scoring\n");
        if (includeRoleLeaders) {
            sb.append("Role leaders:\n");
            for (ModelRole role : List.of(ModelRole.REASONING, ModelRole.CODING, ModelRole.REFLEX, ModelRole.MEMORY, ModelRole.CREATIVE)) {
                ModelSelectionResult result = modelAbstractionLayer
                    .rankForRole(role, "", observedLocalModels, 1)
                    .stream()
                    .findFirst()
                    .orElse(null);
                if (result != null) {
                    sb.append("  - ").append(role.name().toLowerCase(Locale.ROOT))
                        .append(" -> ").append(result.resolvedModelName())
                        .append(" [").append(result.availability()).append("]")
                        .append(" score=").append(formatScore(result.score()))
                        .append("\n");
                }
            }
        }
        sb.append("Next moves: models select coding patch compile shell | models select memory repo-scale context | models select reflex live routing");
        return sb.toString();
    }

    private String renderModelFamilyReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("FRAYNIX MODEL FAMILIES\n");
        modelAbstractionLayer.registry().all().forEach(descriptor -> {
            sb.append("- ").append(descriptor.variant())
                .append(" | family=").append(descriptor.family())
                .append(" | ctx=").append(descriptor.contextTokens())
                .append(" | speed=").append(descriptor.speedTier())
                .append(" | cost=").append(descriptor.costTier())
                .append(" | backends=").append(descriptor.backends())
                .append("\n");
        });
        return sb.toString().trim();
    }

    private String renderModelSelectionReport(String tail) {
        if (tail == null || tail.isBlank()) {
            return "Usage: models select <reasoning|coding|reflex|memory|creative> [task hint]";
        }

        String[] parts = tail.split("\\s+", 2);
        ModelRole role = ModelRole.fromHint(parts[0]).orElse(null);
        if (role == null) {
            return "Unknown model role: " + parts[0] + "\nUsage: models select <reasoning|coding|reflex|memory|creative> [task hint]";
        }

        String taskHint = parts.length > 1 ? parts[1].trim() : "";
        List<String> observedLocalModels = readObservedLocalModels();
        List<ModelSelectionResult> ranked = modelAbstractionLayer.rankForRole(role, taskHint, observedLocalModels, 5);
        List<LearnedModelSelectionCandidate> learnedRanked =
            applyOutcomeLearningToModelRanking(role, taskHint, ranked);

        StringBuilder sb = new StringBuilder();
        sb.append("FRAYNIX MODEL SELECTION\n");
        sb.append("Role: ").append(role.name().toLowerCase(Locale.ROOT)).append("\n");
        sb.append("Task hint: ").append(taskHint.isBlank() ? "(none)" : taskHint).append("\n");
        sb.append("Observed locals: ").append(observedLocalModels.size()).append("\n");
        for (int i = 0; i < learnedRanked.size(); i++) {
            LearnedModelSelectionCandidate candidate = learnedRanked.get(i);
            ModelSelectionResult result = candidate.result();
            sb.append(i + 1).append(". ")
                .append(result.resolvedModelName())
                .append(" | family=").append(result.family())
                .append(" | availability=").append(result.availability())
                .append(" | score=").append(formatScore(result.score()))
                .append(" | learned=").append(formatScore(candidate.learnedBoost()))
                .append(" | adjusted=").append(formatScore(candidate.adjustedScore()))
                .append("\n");
        }
        if (!learnedRanked.isEmpty()) {
            LearnedModelSelectionCandidate winner = learnedRanked.get(0);
            sb.append("Selected: ").append(winner.result().resolvedModelName()).append("\n");
            sb.append("Rationale: ").append(winner.result().rationale());
            if (winner.learnedObservations() > 0) {
                sb.append("\nLearning: observations=").append(winner.learnedObservations())
                    .append(", successEma=").append(formatScore(winner.successEma()))
                    .append(", latencyMs=").append(formatScore(winner.avgLatencyMs()));
            }
        }
        return sb.toString().trim();
    }

    private List<LearnedModelSelectionCandidate> applyOutcomeLearningToModelRanking(
        ModelRole role,
        String taskHint,
        List<ModelSelectionResult> ranked
    ) {
        if (ranked == null || ranked.isEmpty()) {
            return List.of();
        }
        List<AbstractionBiasOutcome> outcomes = readAbstractionBiasOutcomes(ABSTRACTION_OUTCOME_READ_LIMIT);
        if (outcomes.isEmpty()) {
            List<LearnedModelSelectionCandidate> passthrough = new ArrayList<>();
            for (ModelSelectionResult result : ranked) {
                passthrough.add(new LearnedModelSelectionCandidate(result, result.score(), 0.0, 0, 0.0, 0.0));
            }
            return passthrough;
        }

        List<LearnedModelSelectionCandidate> learned = new ArrayList<>();
        for (ModelSelectionResult result : ranked) {
            ModelOutcomeLearning learning = summarizeModelOutcomeLearning(
                outcomes,
                result.resolvedModelName(),
                role
            );
            double learnedBoost = learning.observations() == 0 ? 0.0 : clampSymmetric(
                (learning.successEma() - 0.50) * 0.18
                    + (normalizeLatencyScore(learning.avgLatencyMs()) - 0.50) * 0.10
            );
            double adjustedScore = clamp01(result.score() + learnedBoost);
            learned.add(new LearnedModelSelectionCandidate(
                result,
                adjustedScore,
                learnedBoost,
                learning.observations(),
                learning.successEma(),
                learning.avgLatencyMs()
            ));
        }
        learned.sort(Comparator.comparingDouble(LearnedModelSelectionCandidate::adjustedScore).reversed());
        return List.copyOf(learned);
    }

    private ModelOutcomeLearning summarizeModelOutcomeLearning(
        List<AbstractionBiasOutcome> outcomes,
        String modelName,
        ModelRole role
    ) {
        if (outcomes == null || outcomes.isEmpty() || modelName == null || modelName.isBlank()) {
            return new ModelOutcomeLearning(0, 0.0, 0.0);
        }
        String normalized = ModelDescriptor.normalizeName(modelName);
        int observations = 0;
        double ema = 0.50;
        double latencyTotal = 0.0;
        int latencyCount = 0;

        for (AbstractionBiasOutcome outcome : outcomes) {
            if (outcome.selectedModel().isBlank()) {
                continue;
            }
            String candidate = ModelDescriptor.normalizeName(outcome.selectedModel());
            if (!normalized.equals(candidate)) {
                continue;
            }
            double sampleWeight = outcome.modelRole() == role ? 1.0 : 0.55;
            double sample = outcome.success() ? 1.0 : 0.0;
            ema = ema + ABSTRACTION_SUCCESS_EMA_ALPHA * sampleWeight * (sample - ema);
            observations++;
            if (outcome.latencyMs() > 0) {
                latencyTotal += outcome.latencyMs();
                latencyCount++;
            }
        }
        double avgLatency = latencyCount == 0 ? 0.0 : latencyTotal / latencyCount;
        return new ModelOutcomeLearning(observations, ema, avgLatency);
    }

    private String formatScore(double value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static double clampSymmetric(double value) {
        return Math.max(-0.35, Math.min(0.35, value));
    }

    private static double normalizeLatencyScore(double latencyMs) {
        if (latencyMs <= 0.0) {
            return 0.50;
        }
        return 1.0 / (1.0 + (latencyMs / 1800.0));
    }

    private String paths() {
        StringBuilder sb = new StringBuilder();
        sb.append("PATHS\n");
        sb.append("  asset-manager: ").append(assetManagerDir.toString().replace('\\', '/')).append("\n");
        sb.append("  repo-root: ").append(repoRoot.toString().replace('\\', '/')).append("\n");
        sb.append("  universal: ").append(assetManagerDir.resolve("fraymus-universal").toString().replace('\\', '/')).append("\n");
        sb.append("  ai-core: ").append(repoRoot.resolve("fraymus-ai-core").toString().replace('\\', '/'));
        return sb.toString();
    }

    private String refresh() {
        refreshState();
        return "Knowledge index refreshed.\n" + status();
    }

    private String exportArchitecture(String remainder) {
        Path target = null;
        if (remainder != null && !remainder.isBlank()) {
            target = Path.of(stripQuotes(remainder));
        }

        UltimateArchitectureExporter.ExportArtifact artifact = exportArtifacts(target);

        return """
            ULTIMATE EXPORT COMPLETE
            %s
            Markdown: %s
            Graph: %s
            """.formatted(
                artifact.summary(),
                artifact.markdownPath().toString().replace('\\', '/'),
                artifact.graphPath().toString().replace('\\', '/')
            ).stripIndent().trim();
    }

    private String handleSession(String remainder) {
        if (remainder != null && !remainder.isBlank() && !"summary".equalsIgnoreCase(remainder.trim())) {
            return "Usage: session";
        }

        List<UltimateSelfBuildHarness.ReviewRequest> reviews = selfBuildHarness.listReviewRequests();
        List<UltimateSelfBuildHarness.SessionArtifact> artifacts = selfBuildHarness.recentRunArtifacts(6);
        SubstrateLedgerSummary ledger = readSubstrateLedgerSummary();
        Optional<Path> latestProof = latestLiveProofRecord();

        StringBuilder sb = new StringBuilder();
        sb.append("FRAYNIX SESSION\n");
        sb.append("Workspace: ").append(workspaceRoot.toString().replace('\\', '/')).append("\n");
        sb.append("Latest proof: ").append(latestProof.map(this::displayPath).orElse("(none found)")).append("\n");
        sb.append("Pending reviews: ")
            .append(reviews.stream().filter(review -> "pending".equalsIgnoreCase(review.status())).count())
            .append(" / ").append(reviews.size()).append("\n");
        sb.append("Recent selfbuild runs: ").append(artifacts.size()).append("\n");
        sb.append("Substrate ledger: models=").append(ledger.modelCount())
            .append(", runtimeLanes=").append(ledger.runtimeCount())
            .append(", licenses=").append(ledger.licenseCount())
            .append(", reviewRequired=").append(ledger.reviewRequiredCount()).append("\n");
        if (!ledger.runtimeBackends().isEmpty()) {
            sb.append("Active runtime lanes: ").append(String.join(", ", ledger.runtimeBackends())).append("\n");
        }
        if (!reviews.isEmpty()) {
            sb.append("\nReview queue:\n");
            reviews.stream().limit(5).forEach(review -> sb.append("  - ")
                .append(review.reviewId()).append(" :: ")
                .append(review.status()).append(" :: ")
                .append(review.targetLabel()).append("\n"));
        }
        if (!artifacts.isEmpty()) {
            sb.append("\nRecent runs:\n");
            artifacts.stream().limit(5).forEach(artifact -> sb.append("  - ")
                .append(artifact.id()).append(" :: ")
                .append(artifact.type()).append(" :: ")
                .append(displayPath(artifact.reportPath())).append("\n"));
        }
        sb.append("\nNext moves:\n");
        sb.append("  - demo guided\n");
        sb.append("  - selfbuild review-promote file <path-fragment> :: <goal>\n");
        sb.append("  - selfbuild reviews");
        return sb.toString().trim();
    }

    private String handleDemo(String remainder) {
        if (remainder == null || remainder.isBlank() || "guided".equalsIgnoreCase(remainder.trim())) {
            String stamp = SESSION_STAMP.format(Instant.now());
            Path sessionRoot = assetManagerDir.resolve("generated").resolve("ultimate-session").resolve(stamp + "-guided-demo");
            Path reportPath = sessionRoot.resolve("guided-demo.md");
            try {
                Files.createDirectories(sessionRoot);
            } catch (IOException e) {
                return "GUIDED DEMO FAILED\nCould not create session workspace: " + e.getMessage();
            }

            Path proofTarget = latestLiveProofRecord()
                .or(() -> knowledgeIndex.documents().stream()
                    .map(UltimateKnowledgeIndex.IndexedDocument::path)
                    .filter(Files::exists)
                    .findFirst())
                .orElse(assetManagerDir.resolve("README.md"));
            String proofTargetDisplay = displayPath(proofTarget);
            List<DemoStep> steps = List.of(
                new DemoStep("System status", "status"),
                new DemoStep("Willow proof sweep", "willow proof 4"),
                new DemoStep("Transmudder proof digest", "transmudder proof " + quoteIfNeeded(proofTarget)),
                new DemoStep("Session summary", "session")
            );

            StringBuilder markdown = new StringBuilder();
            markdown.append("# Fraynix Guided Demo\n\n");
            markdown.append("- Generated at: `").append(stamp).append("`\n");
            markdown.append("- Proof target: `").append(proofTargetDisplay).append("`\n\n");
            markdown.append("This walkthrough favors readable proof surfaces over raw internals so a human can watch Fraynix move.\n");

            for (DemoStep step : steps) {
                String output = execute(step.command());
                markdown.append("\n## ").append(step.title()).append("\n\n");
                markdown.append("Command:\n\n```text\n").append(step.command()).append("\n```\n\n");
                markdown.append("Output:\n\n```text\n").append(output == null ? "" : output).append("\n```\n");
            }

            markdown.append("\n## Next human move\n\n");
            markdown.append("- Open `/session` in the Ultimate shell to review the latest state.\n");
            markdown.append("- Run `selfbuild review-promote file <path-fragment> :: <goal>` to stop at a human approval gate.\n");

            try {
                Files.writeString(reportPath, markdown.toString());
            } catch (IOException e) {
                return "GUIDED DEMO FAILED\nCould not write demo report: " + e.getMessage();
            }

            return """
                FRAYNIX GUIDED DEMO COMPLETE
                Steps: %d
                Proof target: %s
                Report: %s
                Next: open /session or run selfbuild review-promote file <path-fragment> :: <goal>
                """.formatted(
                    steps.size(),
                    proofTargetDisplay,
                    reportPath.toString().replace('\\', '/')
                ).stripIndent().trim();
        }
        return "Usage: demo guided";
    }

    private String handleProve(String remainder) {
        String command = remainder == null ? "" : remainder.trim().toLowerCase(Locale.ROOT);
        if (command.isBlank() || "router".equals(command)) {
            return runRouterProof();
        }
        return "Usage: prove router";
    }

    private String runRouterProof() {
        String runId = SESSION_STAMP.format(Instant.now());
        Instant started = Instant.now();
        JSONArray checks = new JSONArray();
        int passCount = 0;
        int failCount = 0;

        String cliRun = routeAiPrompt("run native status");
        boolean cliRunPass = hasMarkers(
            cliRun,
            "AI RUN EXECUTION",
            "\"status\":\"ok\"",
            "\"head\":\"native\"",
            "\"policyVersion\":\"" + AI_RUN_POLICY_VERSION + "\"",
            "\"executionId\":\"airun-",
            "\"outputSha256\":\"",
            "ULTIMATE IMMORTAL KERNEL"
        );
        checks.put(routerProofCheck(
            "cli.run-native-status",
            "cli",
            "run native status returns ai-run envelope with ok status",
            cliRunPass,
            cliRun,
            cliRunPass ? "" : "Missing expected ai-run markers for native status."
        ));
        if (cliRunPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliBlocked = routeAiPrompt("run launch facades");
        boolean cliBlockedPass = hasMarkers(cliBlocked, "AI RUN EXECUTION", "\"status\":\"blocked\"", "POLICY_HEAD_BLOCKED");
        checks.put(routerProofCheck(
            "cli.run-launch-facades-blocked",
            "cli",
            "run launch facades is blocked by policy envelope",
            cliBlockedPass,
            cliBlocked,
            cliBlockedPass ? "" : "Missing blocked policy markers for launch."
        ));
        if (cliBlockedPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliAbsoluteRun = routeAiPrompt("run absolute status");
        boolean cliAbsoluteRunPass = hasMarkers(cliAbsoluteRun, "AI RUN EXECUTION", "\"allowed\":true", "\"head\":\"absolute\"", "\"lane\":\"in-process\"");
        checks.put(routerProofCheck(
            "cli.run-absolute-status",
            "cli",
            "run absolute status returns ai-run envelope through in-process fast lane",
            cliAbsoluteRunPass,
            cliAbsoluteRun,
            cliAbsoluteRunPass ? "" : "Missing expected ai-run markers for absolute status."
        ));
        if (cliAbsoluteRunPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliBabelRun = routeAiPrompt("run babel status");
        boolean cliBabelRunPass = hasMarkers(cliBabelRun, "AI RUN EXECUTION", "\"allowed\":true", "\"head\":\"babel\"", "\"lane\":\"in-process\"");
        checks.put(routerProofCheck(
            "cli.run-babel-status",
            "cli",
            "run babel status returns ai-run envelope through in-process fast lane",
            cliBabelRunPass,
            cliBabelRun,
            cliBabelRunPass ? "" : "Missing expected ai-run markers for babel status."
        ));
        if (cliBabelRunPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliCommandIntent = routeAiPrompt("native status");
        boolean cliCommandIntentPass = hasMarkers(cliCommandIntent, "AI RUN EXECUTION", "\"status\":\"ok\"", "\"head\":\"native\"");
        checks.put(routerProofCheck(
            "cli.command-intent-native-status",
            "cli",
            "command-intent prompt native status is forced through ai-run router",
            cliCommandIntentPass,
            cliCommandIntent,
            cliCommandIntentPass ? "" : "Command-intent prompt did not use ai-run router."
        ));
        if (cliCommandIntentPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliCommandIntentBlocked = routeAiPrompt("launch facades");
        boolean cliCommandIntentBlockedPass = hasMarkers(cliCommandIntentBlocked, "AI RUN EXECUTION", "\"status\":\"blocked\"", "POLICY_HEAD_BLOCKED");
        checks.put(routerProofCheck(
            "cli.command-intent-launch-blocked",
            "cli",
            "command-intent launch facades is blocked by policy envelope",
            cliCommandIntentBlockedPass,
            cliCommandIntentBlocked,
            cliCommandIntentBlockedPass ? "" : "Command-intent launch was not blocked."
        ));
        if (cliCommandIntentBlockedPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliAbsoluteIntent = routeAiPrompt("absolute status");
        boolean cliAbsoluteIntentPass = hasMarkers(cliAbsoluteIntent, "AI RUN EXECUTION", "\"allowed\":true", "\"head\":\"absolute\"", "\"lane\":\"in-process\"");
        checks.put(routerProofCheck(
            "cli.command-intent-absolute-status",
            "cli",
            "command-intent absolute status is forced through ai-run router",
            cliAbsoluteIntentPass,
            cliAbsoluteIntent,
            cliAbsoluteIntentPass ? "" : "Command-intent absolute status did not use ai-run router."
        ));
        if (cliAbsoluteIntentPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliBabelIntent = routeAiPrompt("babel status");
        boolean cliBabelIntentPass = hasMarkers(cliBabelIntent, "AI RUN EXECUTION", "\"allowed\":true", "\"head\":\"babel\"", "\"lane\":\"in-process\"");
        checks.put(routerProofCheck(
            "cli.command-intent-babel-status",
            "cli",
            "command-intent babel status is forced through ai-run router",
            cliBabelIntentPass,
            cliBabelIntent,
            cliBabelIntentPass ? "" : "Command-intent babel status did not use ai-run router."
        ));
        if (cliBabelIntentPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliBabelInjection = routeAiPrompt("run babel status && whoami");
        boolean cliBabelInjectionPass = hasMarkers(cliBabelInjection, "AI RUN EXECUTION", "\"status\":\"blocked\"", "POLICY_BABEL_PAYLOAD_BLOCKED");
        checks.put(routerProofCheck(
            "cli.run-babel-injection-blocked",
            "cli",
            "run babel payload with shell operator is blocked by policy",
            cliBabelInjectionPass,
            cliBabelInjection,
            cliBabelInjectionPass ? "" : "Babel payload operator injection was not blocked."
        ));
        if (cliBabelInjectionPass) {
            passCount++;
        } else {
            failCount++;
        }

        String cliSystemState = routeAiPrompt("describe your current system and state");
        boolean cliSystemStatePass = hasMarkers(
            cliSystemState,
            "Grounded SYSTEM_STATE envelope (machine-checkable)",
            "aiRun={status=",
            "queueDepth=",
            "aiRunRouter",
            "aiRun lock telemetry"
        );
        checks.put(routerProofCheck(
            "cli.system-state-ai-run-envelope",
            "cli",
            "system-state route emits shared ai-run pressure telemetry",
            cliSystemStatePass,
            cliSystemState,
            cliSystemStatePass ? "" : "Missing ai-run pressure markers in CLI system-state envelope."
        ));
        if (cliSystemStatePass) {
            passCount++;
        } else {
            failCount++;
        }

        Set<String> catalogHeads = fraynixCatalogHeads();
        LinkedHashSet<String> uncoveredHeads = new LinkedHashSet<>();
        for (String catalogHead : catalogHeads) {
            if (catalogHead == null || catalogHead.isBlank()) {
                continue;
            }
            if (!AI_RUN_ALLOWED_FRAYNIX_HEADS.contains(catalogHead)
                && !AI_RUN_ALLOWED_IN_PROCESS_HEADS.contains(catalogHead)
                && !AI_RUN_BLOCKED_HEADS.contains(catalogHead)) {
                uncoveredHeads.add(catalogHead);
            }
        }
        boolean policyCoveragePass = uncoveredHeads.isEmpty();
        String coverageTranscript = """
            catalogHeads=%d
            coveredHeads=%d
            uncoveredHeads=%d
            uncoveredList=%s
            """.formatted(
            catalogHeads.size(),
            Math.max(0, catalogHeads.size() - uncoveredHeads.size()),
            uncoveredHeads.size(),
            uncoveredHeads.isEmpty() ? "(none)" : String.join(", ", uncoveredHeads)
        ).stripIndent().trim();
        checks.put(routerProofCheck(
            "policy.fraynix-head-coverage",
            "policy",
            "ai-run allowlist covers Fraynix catalog command heads (or explicit blocklist/in-process lanes)",
            policyCoveragePass,
            coverageTranscript,
            policyCoveragePass ? "" : "Uncovered Fraynix command heads: " + String.join(", ", uncoveredHeads)
        ));
        if (policyCoveragePass) {
            passCount++;
        } else {
            failCount++;
        }

        UltimateUiServer uiServer = null;
        try {
            uiServer = new UltimateUiServer(assetManagerDir, 0);
            int port = uiServer.start();

            String apiRun = postCompanionChatProof(port, "run native status");
        boolean apiRunPass = hasMarkers(
            apiRun,
            "AI RUN EXECUTION",
            "\"status\":\"ok\"",
            "\"policyVersion\":\"" + AI_RUN_POLICY_VERSION + "\"",
            "\"executionId\":\"airun-",
            "fraynix.ai-run.v1"
        );
            checks.put(routerProofCheck(
                "api.companion-run-native-status",
                "api:/api/companion/chat",
                "companion chat run native status returns ai-run envelope",
                apiRunPass,
                apiRun,
                apiRunPass ? "" : "Companion chat did not return ai-run native status envelope."
            ));
            if (apiRunPass) {
                passCount++;
            } else {
                failCount++;
            }

            String apiBlocked = postCompanionChatProof(port, "run launch facades");
            boolean apiBlockedPass = hasMarkers(apiBlocked, "AI RUN EXECUTION", "\"status\":\"blocked\"", "POLICY_HEAD_BLOCKED");
            checks.put(routerProofCheck(
                "api.companion-run-launch-blocked",
                "api:/api/companion/chat",
                "companion chat run launch facades is blocked by policy envelope",
                apiBlockedPass,
                apiBlocked,
                apiBlockedPass ? "" : "Companion chat launch command was not blocked."
            ));
            if (apiBlockedPass) {
                passCount++;
            } else {
                failCount++;
            }

            String apiAbsoluteIntent = postCompanionChatProof(port, "absolute status");
            boolean apiAbsoluteIntentPass = hasMarkers(apiAbsoluteIntent, "AI RUN EXECUTION", "\"allowed\":true", "\"head\":\"absolute\"", "\"lane\":\"in-process\"");
            checks.put(routerProofCheck(
                "api.companion-command-intent-absolute-status",
                "api:/api/companion/chat",
                "companion chat absolute status is forced through ai-run router",
                apiAbsoluteIntentPass,
                apiAbsoluteIntent,
                apiAbsoluteIntentPass ? "" : "Companion chat absolute status did not use ai-run router."
            ));
            if (apiAbsoluteIntentPass) {
                passCount++;
            } else {
                failCount++;
            }

            String apiBabelIntent = postCompanionChatProof(port, "babel status");
            boolean apiBabelIntentPass = hasMarkers(apiBabelIntent, "AI RUN EXECUTION", "\"allowed\":true", "\"head\":\"babel\"", "\"lane\":\"in-process\"");
            checks.put(routerProofCheck(
                "api.companion-command-intent-babel-status",
                "api:/api/companion/chat",
                "companion chat babel status is forced through ai-run router",
                apiBabelIntentPass,
                apiBabelIntent,
                apiBabelIntentPass ? "" : "Companion chat babel status did not use ai-run router."
            ));
            if (apiBabelIntentPass) {
                passCount++;
            } else {
                failCount++;
            }

            String apiBabelInjection = postCompanionChatProof(port, "run babel status && whoami");
            boolean apiBabelInjectionPass = hasMarkers(apiBabelInjection, "AI RUN EXECUTION", "\"status\":\"blocked\"", "POLICY_BABEL_PAYLOAD_BLOCKED");
            checks.put(routerProofCheck(
                "api.companion-run-babel-injection-blocked",
                "api:/api/companion/chat",
                "companion chat babel payload with shell operator is blocked by policy",
                apiBabelInjectionPass,
                apiBabelInjection,
                apiBabelInjectionPass ? "" : "Companion chat babel payload operator injection was not blocked."
            ));
            if (apiBabelInjectionPass) {
                passCount++;
            } else {
                failCount++;
            }

            String apiSystemState = getSystemStateProof(port, "session");
            boolean apiSystemStatePass = hasMarkers(
                apiSystemState,
                "\"systemState\"",
                "\"boot\"",
                "\"aiRun\"",
                "\"aiRunBusy\"",
                "\"aiRunQueueDepth\"",
                "\"aiRunRouter\"",
                "aiRun lock telemetry"
            );
            checks.put(routerProofCheck(
                "api.system-state-ai-run-envelope",
                "api:/api/system-state",
                "system-state API emits shared ai-run pressure telemetry",
                apiSystemStatePass,
                apiSystemState,
                apiSystemStatePass ? "" : "Missing ai-run pressure markers in /api/system-state envelope."
            ));
            if (apiSystemStatePass) {
                passCount++;
            } else {
                failCount++;
            }
        } catch (Exception e) {
            failCount++;
            checks.put(routerProofCheck(
                "api.companion-chat-probe",
                "api:/api/companion/chat",
                "companion chat probe executes",
                false,
                "",
                "Router proof API probe failed: " + e.getMessage()
            ));
        } finally {
            if (uiServer != null) {
                uiServer.stop();
            }
        }

        long durationMs = Duration.between(started, Instant.now()).toMillis();
        JSONObject report = new JSONObject()
            .put("schema", "fraynix.router-proof.v1")
            .put("runId", runId)
            .put("timestamp", Instant.now().toString())
            .put("durationMs", durationMs)
            .put("passCount", passCount)
            .put("failCount", failCount)
            .put("allPassed", failCount == 0)
            .put("checks", checks);

        Path proofDir = workspaceRoot.resolve(".northstar-kernel").resolve("generated").resolve("proofs");
        Path jsonPath = proofDir.resolve("router-proof-" + runId + ".json");
        Path markdownPath = proofDir.resolve("router-proof-" + runId + ".md");
        Path latestJson = proofDir.resolve("router-proof-latest.json");
        Path latestMarkdown = proofDir.resolve("router-proof-latest.md");

        try {
            Files.createDirectories(proofDir);
            Files.writeString(jsonPath, report.toString(2), StandardCharsets.UTF_8);
            Files.writeString(markdownPath, renderRouterProofMarkdown(runId, durationMs, passCount, failCount, checks), StandardCharsets.UTF_8);
            Files.copy(jsonPath, latestJson, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(markdownPath, latestMarkdown, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return """
                ROUTER PROOF FAILED
                RunId: %s
                Pass: %d
                Fail: %d
                Write fault: %s
                """.formatted(runId, passCount, failCount, e.getMessage()).stripIndent().trim();
        }

        return """
            ROUTER PROOF COMPLETE
            RunId: %s
            Pass: %d
            Fail: %d
            DurationMs: %d
            JSON: %s
            Markdown: %s
            Latest JSON: %s
            Latest Markdown: %s
            """.formatted(
            runId,
            passCount,
            failCount,
            durationMs,
            jsonPath.toString().replace('\\', '/'),
            markdownPath.toString().replace('\\', '/'),
            latestJson.toString().replace('\\', '/'),
            latestMarkdown.toString().replace('\\', '/')
        ).stripIndent().trim();
    }

    private JSONObject routerProofCheck(
        String id,
        String lane,
        String expectation,
        boolean passed,
        String transcript,
        String note
    ) {
        JSONObject check = new JSONObject();
        check.put("id", id);
        check.put("lane", lane);
        check.put("expectation", expectation);
        check.put("passed", passed);
        check.put("note", note == null ? "" : note);
        check.put("transcript", compactBlock(transcript == null ? "" : transcript, 40, 7000));
        return check;
    }

    private String renderRouterProofMarkdown(
        String runId,
        long durationMs,
        int passCount,
        int failCount,
        JSONArray checks
    ) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Fraynix Router Proof Report\n\n");
        markdown.append("- RunId: `").append(runId).append("`\n");
        markdown.append("- DurationMs: `").append(durationMs).append("`\n");
        markdown.append("- Pass: `").append(passCount).append("`\n");
        markdown.append("- Fail: `").append(failCount).append("`\n");
        markdown.append("- Overall: `").append(failCount == 0 ? "PASS" : "FAIL").append("`\n\n");
        markdown.append("## Checks\n");
        for (int i = 0; i < checks.length(); i++) {
            JSONObject check = checks.optJSONObject(i);
            if (check == null) {
                continue;
            }
            boolean passed = check.optBoolean("passed", false);
            markdown.append("\n### ")
                .append(passed ? "[PASS] " : "[FAIL] ")
                .append(check.optString("id", "unknown"))
                .append("\n\n");
            markdown.append("- Lane: `").append(check.optString("lane", "")).append("`\n");
            markdown.append("- Expectation: ").append(check.optString("expectation", "")).append("\n");
            String note = check.optString("note", "");
            if (!note.isBlank()) {
                markdown.append("- Note: ").append(note).append("\n");
            }
            markdown.append("\nTranscript:\n\n```text\n")
                .append(check.optString("transcript", "(empty)"))
                .append("\n```\n");
        }
        return markdown.toString();
    }

    private String postCompanionChatProof(int port, String prompt) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
        JSONObject payload = new JSONObject().put("prompt", prompt == null ? "" : prompt);
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/companion/chat"))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(60))
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String body = response.body() == null ? "" : response.body();
        if (response.statusCode() >= 400) {
            throw new IOException("Companion chat returned HTTP " + response.statusCode() + " with body: " + compactBlock(body, 20, 1200));
        }
        try {
            JSONObject json = new JSONObject(body);
            return json.optString("reply", body);
        } catch (Exception ignored) {
            return body;
        }
    }

    private String getSystemStateProof(int port, String consumer) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        String lane = consumer == null || consumer.isBlank() ? "browser" : consumer.trim().toLowerCase(Locale.ROOT);
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/system-state?consumer=" + lane))
            .timeout(Duration.ofSeconds(20))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String body = response.body() == null ? "" : response.body();
        if (response.statusCode() >= 400) {
            throw new IOException("System state returned HTTP " + response.statusCode() + " with body: " + compactBlock(body, 20, 1200));
        }
        return body;
    }

    private boolean hasMarkers(String text, String... markers) {
        if (text == null || markers == null || markers.length == 0) {
            return false;
        }
        for (String marker : markers) {
            if (marker == null || marker.isBlank()) {
                continue;
            }
            if (!text.contains(marker)) {
                return false;
            }
        }
        return true;
    }

    private String handleAi(String remainder) {
        return routeAiPrompt(remainder);
    }

    private String handleResume(String remainder) {
        String mode = remainder == null ? "" : remainder.trim().toLowerCase(Locale.ROOT);
        boolean liveProbe = wantsResumeLiveProbe(mode);
        boolean forceRefresh = "refresh".equals(mode) || mode.startsWith("refresh ") || mode.contains(" refresh");
        if (liveProbe) {
            return renderContinuityResumeWithLiveProbe(forceRefresh, remainder);
        }
        if (mode.isBlank() || "summary".equals(mode) || "status".equals(mode) || "state".equals(mode) || "fraynix state".equals(mode)) {
            return renderContinuityResumeReport(ensureContinuitySnapshot(false), true);
        }
        if ("refresh".equals(mode)) {
            return renderContinuityResumeReport(ensureContinuitySnapshot(true), true);
        }
        if ("rules".equals(mode)) {
            return renderContinuityRules(ensureContinuitySnapshot(false));
        }
        return "Usage: resume [status|summary|rules|refresh|live|refresh live]";
    }

    private String handleContinuity(String remainder) {
        String mode = remainder == null ? "" : remainder.trim().toLowerCase(Locale.ROOT);
        return switch (mode) {
            case "", "status", "summary", "state" -> renderContinuityResumeReport(ensureContinuitySnapshot(false), true);
            case "refresh" -> renderContinuityResumeReport(ensureContinuitySnapshot(true), true);
            case "rules" -> renderContinuityRules(ensureContinuitySnapshot(false));
            case "menus", "sections", "heads" -> renderMenuVisibilityReport(ensureContinuitySnapshot(false));
            default -> "Usage: continuity [status|rules|menus|refresh]";
        };
    }

    private String handleAbstraction(String remainder) {
        String mode = remainder == null ? "" : remainder.trim();
        if (mode.isBlank() || "status".equalsIgnoreCase(mode) || "summary".equalsIgnoreCase(mode)) {
            return renderAbstractionStatus();
        }
        if (mode.toLowerCase(Locale.ROOT).startsWith("tail")) {
            String[] parts = mode.split("\\s+", 2);
            int limit = parts.length > 1 ? parseInt(parts[1], 5) : 5;
            int bounded = Math.max(1, Math.min(25, limit));
            return renderAbstractionTail(bounded);
        }
        if ("delta".equalsIgnoreCase(mode)) {
            return renderAbstractionDelta();
        }
        if ("path".equalsIgnoreCase(mode)) {
            return "Abstraction memory path: " + displayPath(abstractionMemoryPath());
        }
        return "Usage: abstraction [status|tail [n]|delta|path]";
    }

    public String routeAiPrompt(String prompt) {
        String normalizedPrompt = stripLeadingInvisible(prompt == null ? "" : prompt).trim();
        String loweredPrompt = normalizedPrompt.toLowerCase(Locale.ROOT);
        if (loweredPrompt.startsWith("ai ")) {
            normalizedPrompt = normalizedPrompt.substring(3).trim();
        }
        if (normalizedPrompt.isBlank()) {
            return "Usage: ai <question>";
        }

        PromptAbstractionSnapshot abstractionSnapshot = buildPromptAbstractionSnapshot(normalizedPrompt);

        String runLane = tryAiRunLane(normalizedPrompt);
        if (runLane != null) {
            return runLane;
        }

        String commandIntentRoute = tryAiCommandIntentRouterLane(normalizedPrompt);
        if (commandIntentRoute != null) {
            return commandIntentRoute;
        }

        String structuredProbe = tryStructuredAiProbe(normalizedPrompt);
        if (structuredProbe != null) {
            return structuredProbe;
        }

        String directPassThrough = tryDirectCommandPassThrough(normalizedPrompt);
        if (directPassThrough != null) {
            return directPassThrough;
        }

        if (SystemStateContract.isSystemStateQuery(normalizedPrompt, null)) {
            return SystemStateContract.renderEnvelope(buildSystemStateEnvelope());
        }

        String continuityReply = groundedContinuityReply(normalizedPrompt);
        if (continuityReply != null) {
            return continuityReply;
        }

        String truthRegistryReply = groundedTruthRegistryReply(normalizedPrompt);
        if (truthRegistryReply != null) {
            return truthRegistryReply;
        }

        String livingProofReply = groundedLivingProofLaneReply(normalizedPrompt);
        if (livingProofReply != null) {
            return livingProofReply;
        }

        return orchestrateAiPrompt(normalizedPrompt, abstractionSnapshot);
    }

    private String groundedContinuityReply(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return null;
        }
        String lowered = prompt.toLowerCase(Locale.ROOT);
        boolean liveProbe = wantsResumeLiveProbe(lowered);
        if (lowered.startsWith("resume")
            || lowered.contains("pick up where")
            || lowered.contains("pick up where it is")
            || lowered.contains("pick up where it left off")
            || lowered.contains("continue from")
            || lowered.contains("resume state")
            || lowered.contains("continuity status")) {
            if (liveProbe) {
                return renderContinuityResumeWithLiveProbe(lowered.contains("refresh"), prompt);
            }
            return renderContinuityResumeReport(ensureContinuitySnapshot(false), true);
        }
        return null;
    }

    private String groundedTruthRegistryReply(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return null;
        }
        String lowered = prompt.toLowerCase(Locale.ROOT);
        if (!(lowered.contains("axiom ledger")
            || lowered.contains("truth registry")
            || lowered.contains("principia")
            || lowered.contains("what parts")
            || lowered.contains("implemented now")
            || lowered.contains("frontier target")
            || lowered.contains("continuity memory")
            || lowered.contains("pick up where")
            || lowered.contains("resume rules"))) {
            return null;
        }
        if (!(lowered.contains("fraynix")
            || lowered.contains("agi")
            || lowered.contains("implemented")
            || lowered.contains("scaffold")
            || lowered.contains("partial")
            || lowered.contains("roadmap"))) {
            return null;
        }

        UltimateArchitectureExporter.ExportArtifact artifact = exportArtifacts(null);
        return """
            FRAYNIX TRUTH REGISTRY
            route=TRUTH_REGISTRY
            machineCheckable=true

            Generated artifacts:
            - axiom ledger: %s
            - truth registry: %s
            - continuity memory: %s

            Current grounded reading:
            - implemented: bounded virtual substrate tracking, MAP-VSA memory primitives, core shell/operator records
            - partial: connectome-shaped routing, safe actuation, active inference, recursive self-improvement, holographic memory usage, exodus claims, symbolic compiler lane integration
            - scaffold/frontier: live Exocortex Phase 2 execution, cryptographic sovereignty, full AGI completion, deterministic-lane kernel integration

            Forced resume rules:
            - separate implemented, observed, inferred, planned, and speculative claims
            - prefer source, runtime, proof, and test artifacts over broad synthesis
            - preserve verified bug lists until they are fixed and re-tested

            Use these artifacts to answer:
            - what is implemented now
            - what is partial or scaffolded
            - what remains a frontier target
            - how to pick up where Fraynix left off without losing math or rules

            Next action:
            - open continuity memory, then the axiom ledger and truth registry, before describing Fraynix as complete
            """.formatted(
                artifact.axiomLedgerPath().toString().replace('\\', '/'),
                artifact.truthRegistryPath().toString().replace('\\', '/'),
                artifact.continuityMemoryPath().toString().replace('\\', '/')
            ).stripIndent().trim();
    }

    private String tryAiRunLane(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return null;
        }
        String trimmedPrompt = prompt.trim();
        String lowered = trimmedPrompt.toLowerCase(Locale.ROOT);
        String command;
        if (lowered.startsWith("ai run ")) {
            command = trimmedPrompt.substring("ai run ".length()).trim();
        } else if (lowered.startsWith("run ")) {
            command = trimmedPrompt.substring("run ".length()).trim();
        } else {
            return null;
        }
        if (command.isBlank()) {
            return "Usage: ai run <fraynix-command>";
        }
        return executeAiRunLane(command);
    }

    private String tryAiCommandIntentRouterLane(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return null;
        }
        String normalizedPrompt = prompt.trim();
        String[] parts = normalizedPrompt.split("\\s+", 2);
        String head = parts[0].toLowerCase(Locale.ROOT);
        String tail = parts.length > 1 ? parts[1].trim() : "";
        if ("ai".equals(head) || "run".equals(head)) {
            return null;
        }
        if ("resume".equals(head) && wantsResumeLiveProbe(tail)) {
            return null;
        }
        if (!isForcedAiRouterHead(head)) {
            return null;
        }
        if (looksLikeFraynixBridgeIntent(tail)) {
            return null;
        }
        return executeAiRunLane(normalizedPrompt);
    }

    private String tryStructuredAiProbe(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return null;
        }
        Map<String, String> fields = parseStructuredAiProbeFields(prompt);
        if (!fields.containsKey("intent") || !fields.containsKey("target")) {
            return null;
        }

        String intent = fields.getOrDefault("intent", "").toLowerCase(Locale.ROOT);
        String target = fields.getOrDefault("target", "").toLowerCase(Locale.ROOT);
        String method = fields.getOrDefault("method", "").toLowerCase(Locale.ROOT);

        if ("evaluate".equals(intent)
            && "lazarus_engine".equals(target)
            && "active_inference".equals(method)) {
            return renderLazarusActiveInferenceProbe(prompt, fields);
        }
        if ("verify".equals(intent)
            && "openclaw_execution".equals(target)
            && "formal_proof".equals(method)) {
            return renderOpenClawFormalVerificationProbe(prompt, fields);
        }
        if ("stress_test".equals(intent)
            && "f0_engine".equals(target)
            && "adversarial_perturbation".equals(method)) {
            return renderF0StressProbe(prompt, fields);
        }
        return renderUnsupportedStructuredProbe(prompt, fields);
    }

    private Map<String, String> parseStructuredAiProbeFields(String prompt) {
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        if (prompt == null || prompt.isBlank()) {
            return fields;
        }
        for (String token : prompt.trim().split("\\s+")) {
            int separator = token.indexOf('=');
            if (separator <= 0 || separator == token.length() - 1) {
                continue;
            }
            String key = token.substring(0, separator).trim().toLowerCase(Locale.ROOT);
            String value = token.substring(separator + 1).trim();
            if (!key.isBlank() && !value.isBlank()) {
                fields.put(key, value);
            }
        }
        return fields;
    }

    private String renderLazarusActiveInferenceProbe(String prompt, Map<String, String> fields) {
        Path sourcePath = assetManagerDir.resolve("src/main/java/com/eyeoverthink/lazarus/LazarusEngine.java");
        LazarusEngine lazarusEngine = new LazarusEngine();
        LazarusEngine.ActiveInferenceReport report = lazarusEngine.executeActiveInferenceProbe();
        String verdict = "PASS_EXECUTABLE_ACTIVE_INFERENCE_LANE";

        JSONObject envelope = baseStructuredProbeEnvelope(prompt, fields,
            "lazarus_engine.active_inference", "executed", verdict);
        envelope.put("sourcePath", displayPath(sourcePath));
        envelope.put("observation", report.observation());
        envelope.put("priorCalmBelief", report.priorCalmBelief());
        envelope.put("priorStressBelief", report.priorStressBelief());
        envelope.put("posteriorCalmBelief", report.posteriorCalmBelief());
        envelope.put("posteriorStressBelief", report.posteriorStressBelief());
        envelope.put("variationalFreeEnergy", report.variationalFreeEnergy());
        envelope.put("informationGain", report.informationGain());
        envelope.put("selectedAction", report.selectedAction().name());
        envelope.put("selectedExpectedFreeEnergy", report.selectedExpectedFreeEnergy());
        envelope.put("averageFitness", report.averageFitness());
        envelope.put("averageResonance", report.averageResonance());

        StringBuilder sb = new StringBuilder();
        sb.append("AI STRUCTURED PROBE\n");
        sb.append("Prompt: ").append(prompt).append("\n");
        sb.append("Envelope:\n").append(envelope.toString()).append("\n\n");
        sb.append("Result: LAZARUS ACTIVE INFERENCE REVIEW\n");
        sb.append("Verdict: ").append(verdict).append("\n");
        sb.append("Execution mode: live active-inference evaluation\n");
        sb.append("Source: ").append(displayPath(sourcePath)).append("\n\n");
        sb.append("Equations:\n");
        sb.append("  q(s_t) ∝ p(o_t | s_t) * q(s_(t-1))\n");
        sb.append("  F_t = E_q[-log p(o_t | s_t)] + KL(q(s_t) || q(s_(t-1)))\n");
        sb.append("  G(a) = pragmatic_risk(a) + ambiguity(a) - epistemic_value(a)\n");
        sb.append("  a* = argmin_a G(a)\n\n");
        sb.append("Metrics:\n");
        sb.append("  observation=").append(formatDouble(report.observation())).append("\n");
        sb.append("  priorCalmBelief=").append(formatDouble(report.priorCalmBelief())).append("\n");
        sb.append("  priorStressBelief=").append(formatDouble(report.priorStressBelief())).append("\n");
        sb.append("  posteriorCalmBelief=").append(formatDouble(report.posteriorCalmBelief())).append("\n");
        sb.append("  posteriorStressBelief=").append(formatDouble(report.posteriorStressBelief())).append("\n");
        sb.append("  variationalFreeEnergy=").append(formatDouble(report.variationalFreeEnergy())).append("\n");
        sb.append("  informationGain=").append(formatDouble(report.informationGain())).append("\n");
        sb.append("  averageFitness=").append(formatDouble(report.averageFitness())).append("\n");
        sb.append("  averageResonance=").append(formatDouble(report.averageResonance())).append("\n");
        sb.append("  preferredStress=").append(formatDouble(report.preferredStress())).append("\n");
        sb.append("  selectedAction=").append(report.selectedAction().name()).append("\n");
        sb.append("  selectedExpectedFreeEnergy=").append(formatDouble(report.selectedExpectedFreeEnergy())).append("\n\n");
        sb.append("Action EFE Table:\n");
        sb.append("  STABILIZE=").append(formatDouble(report.stabilize().expectedFreeEnergy()))
            .append(" | risk=").append(formatDouble(report.stabilize().pragmaticRisk()))
            .append(" | ambiguity=").append(formatDouble(report.stabilize().ambiguity()))
            .append(" | epistemic=").append(formatDouble(report.stabilize().epistemicValue())).append("\n");
        sb.append("  EXPLORE=").append(formatDouble(report.explore().expectedFreeEnergy()))
            .append(" | risk=").append(formatDouble(report.explore().pragmaticRisk()))
            .append(" | ambiguity=").append(formatDouble(report.explore().ambiguity()))
            .append(" | epistemic=").append(formatDouble(report.explore().epistemicValue())).append("\n");
        sb.append("  EXPLOIT=").append(formatDouble(report.exploit().expectedFreeEnergy()))
            .append(" | risk=").append(formatDouble(report.exploit().pragmaticRisk()))
            .append(" | ambiguity=").append(formatDouble(report.exploit().ambiguity()))
            .append(" | epistemic=").append(formatDouble(report.exploit().epistemicValue())).append("\n\n");
        sb.append("Interpretation:\n");
        sb.append("  Lazarus now performs a belief update over calm vs stressed latent states and scores stabilize/explore/exploit actions with expected free energy.\n");
        sb.append("  The selected action is applied back into the organism loop as homeostatic correction, exploration stimulus, or exploitation reinforcement.");
        return sb.toString().trim();
    }

    private String renderOpenClawFormalVerificationProbe(String prompt, Map<String, String> fields) {
        Path openClawPath = assetManagerDir.resolve("src/main/java/fraymus/acquisition/OpenClaw.java");
        Path amygdalaPath = assetManagerDir.resolve("src/main/java/com/fraymus/omega/Amygdala.java");
        String openClawSource = readFileIfExists(openClawPath);
        boolean leanAvailable = isCommandAvailable("lean");
        boolean lakeAvailable = isCommandAvailable("lake");
        boolean openClawUnrestricted = containsAnyIgnoreCase(openClawSource,
            "No artificial limitations", "unrestricted acquisition capabilities");
        Amygdala amygdala = new Amygdala("deterministic-policy-v1");
        Intent plan = Intent.builder("openclaw_execution")
            .operation("execute")
            .scope("")
            .networked(true)
            .unrestricted(openClawUnrestricted)
            .humanApproved(false)
            .attribute("target", "openclaw")
            .build();
        Amygdala.PolicyDecision decision = amygdala.evaluate(plan);
        boolean deterministicAllow = decision.allowed();
        boolean proofKernelExecuted = false;
        String verdict = deterministicAllow
            ? "ALLOW_EXECUTION_POLICY_SATISFIED"
            : "BLOCK_EXECUTION_POLICY_VIOLATION";
        String proofScript = decision.proofScript();

        JSONObject envelope = baseStructuredProbeEnvelope(prompt, fields,
            "openclaw_execution.formal_proof", deterministicAllow ? "allow" : "blocked", verdict);
        envelope.put("leanAvailable", leanAvailable);
        envelope.put("lakeAvailable", lakeAvailable);
        envelope.put("openClawUnrestricted", openClawUnrestricted);
        envelope.put("deterministicOutcome", deterministicAllow ? "ALLOW" : "BLOCK");
        envelope.put("proofKernelExecuted", proofKernelExecuted);
        envelope.put("policyDecisionCode", decision.decisionCode());
        envelope.put("scopeBounded", decision.scopeBounded());
        envelope.put("humanApproved", decision.humanApproved());
        envelope.put("networked", decision.networked());
        envelope.put("privileged", decision.privileged());
        envelope.put("destructive", decision.destructive());
        envelope.put("violatedAxioms", new JSONArray(decision.violatedAxioms()));
        envelope.put("openClawPath", displayPath(openClawPath));
        envelope.put("amygdalaPath", displayPath(amygdalaPath));

        StringBuilder sb = new StringBuilder();
        sb.append("AI STRUCTURED PROBE\n");
        sb.append("Prompt: ").append(prompt).append("\n");
        sb.append("Envelope:\n").append(envelope.toString()).append("\n\n");
        sb.append("Result: OPENCLAW FORMAL VERIFICATION REVIEW\n");
        sb.append("Verdict: ").append(verdict).append("\n");
        sb.append("Deterministic binary outcome: ").append(deterministicAllow ? "ALLOW" : "BLOCK").append("\n");
        sb.append("Execution mode: deterministic policy gate with proof-script emission\n");
        sb.append("OpenClaw source: ").append(displayPath(openClawPath)).append("\n");
        sb.append("Amygdala source: ").append(displayPath(amygdalaPath)).append("\n\n");
        sb.append("Proof script:\n");
        sb.append(proofScript).append("\n\n");
        sb.append("Interpretation:\n");
        sb.append("  Lean4 tooling on PATH: ").append(leanAvailable).append(" / lake: ").append(lakeAvailable).append("\n");
        sb.append("  OpenClaw surface is explicitly unrestricted: ").append(openClawUnrestricted).append("\n");
        sb.append("  Policy decision code: ").append(decision.decisionCode()).append("\n");
        sb.append("  Violated axioms: ").append(decision.violatedAxioms()).append("\n");
        sb.append("  The gate is now deterministic and blocks unscoped unrestricted execution even when Lean is unavailable.");
        return sb.toString().trim();
    }

    private String renderF0StressProbe(String prompt, Map<String, String> fields) {
        Path workspaceRoot = workspaceRoot();
        Path mainPath = workspaceRoot.resolve("f0_engine/src/main.rs");
        Path fieldPath = workspaceRoot.resolve("f0_engine/src/field_engine.rs");
        Path simdPath = workspaceRoot.resolve("f0_engine/src/simd_ops.rs");
        String simdSource = readFileIfExists(simdPath);
        boolean simdClampPresent = containsAnyIgnoreCase(simdSource, "_mm256_min_ps", "_mm256_max_ps");
        boolean simdOnlySurface = containsAnyIgnoreCase(simdSource, "_mm256_set1_ps", "_mm256_add_ps");
        F0StressRunner.StressResult result = new F0StressRunner().run(workspaceRoot);
        String verdict = result.verdict();

        JSONObject envelope = baseStructuredProbeEnvelope(prompt, fields,
            "f0_engine.adversarial_perturbation", "executed", verdict);
        envelope.put("substrateSize", result.substrateSize());
        envelope.put("damping", result.damping());
        envelope.put("dt", result.dt());
        envelope.put("baseCoherence", result.baseCoherence());
        envelope.put("stressedCoherence", result.stressedCoherence());
        envelope.put("coherenceDelta", result.coherenceDelta());
        envelope.put("baseAttractorEnergy", result.baseAttractorEnergy());
        envelope.put("stressedAttractorEnergy", result.stressedAttractorEnergy());
        envelope.put("energyDelta", result.energyDelta());
        envelope.put("fisherTrace", result.fisherTrace());
        envelope.put("maxResponse", result.maxResponse());
        envelope.put("meanActivity", result.meanActivity());
        envelope.put("stabilityScore", result.stabilityScore());
        envelope.put("cargoAvailable", result.cargoAvailable());
        envelope.put("cargoExecuted", result.cargoExecuted());
        envelope.put("javaFallback", result.javaFallback());
        envelope.put("executionMode", result.executionMode());
        envelope.put("simdClampPresent", simdClampPresent);
        envelope.put("simdOnlySurface", simdOnlySurface);
        envelope.put("fisherInformationImplemented", true);
        envelope.put("mainPath", displayPath(mainPath));
        envelope.put("fieldPath", displayPath(fieldPath));
        envelope.put("simdPath", displayPath(simdPath));

        StringBuilder sb = new StringBuilder();
        sb.append("AI STRUCTURED PROBE\n");
        sb.append("Prompt: ").append(prompt).append("\n");
        sb.append("Envelope:\n").append(envelope.toString()).append("\n\n");
        sb.append("Result: F0 ENGINE ADVERSARIAL STRESS REVIEW\n");
        sb.append("Verdict: ").append(verdict).append("\n");
        sb.append("Execution mode: ").append(result.executionMode()).append("\n");
        sb.append("Tooling available: cargo=").append(result.cargoAvailable())
            .append(" | executed=").append(result.cargoExecuted())
            .append(" | javaFallback=").append(result.javaFallback()).append("\n\n");
        sb.append("Coherence metrics:\n");
        sb.append("  substrateSize=").append(result.substrateSize()).append("\n");
        sb.append("  damping=").append(formatDouble(result.damping())).append("\n");
        sb.append("  dt=").append(formatDouble(result.dt())).append("\n");
        sb.append("  baseCoherence=").append(formatDouble(result.baseCoherence())).append("\n");
        sb.append("  stressedCoherence=").append(formatDouble(result.stressedCoherence())).append("\n");
        sb.append("  coherenceDelta=").append(formatDouble(result.coherenceDelta())).append("\n");
        sb.append("  baseAttractorEnergy=").append(formatDouble(result.baseAttractorEnergy())).append("\n");
        sb.append("  stressedAttractorEnergy=").append(formatDouble(result.stressedAttractorEnergy())).append("\n");
        sb.append("  energyDelta=").append(formatDouble(result.energyDelta())).append("\n");
        sb.append("  fisherTrace=").append(formatDouble(result.fisherTrace())).append("\n");
        sb.append("  maxResponse=").append(formatDouble(result.maxResponse())).append("\n");
        sb.append("  meanActivity=").append(formatDouble(result.meanActivity())).append("\n");
        sb.append("  stabilityScore=").append(formatDouble(result.stabilityScore())).append("\n");
        sb.append("  simdClampPresent=").append(simdClampPresent).append("\n");
        sb.append("  fisherInformationImplemented=true\n");
        sb.append("  simdOnlySurface=").append(simdOnlySurface).append("\n\n");
        sb.append("Grounded equations:\n");
        sb.append("  s_(t+1) = s_t + dt * F(s_t)\n");
        sb.append("  F(s_t) = -damping * s_t\n");
        sb.append("  attractor_energy = sum((s_i - a_i)^2)\n\n");
        sb.append("  fisher_trace ≈ Σ_i ((E(s+ϵe_i) - E(s-ϵe_i)) / (2ϵ))^2\n\n");
        sb.append("Interpretation:\n");
        sb.append("  The F-0 lane now produces measured coherence, attractor-energy, and Fisher-trace metrics under adversarial perturbation.\n");
        sb.append("  When Cargo is missing the same probe runs against the in-process field engine instead of degrading to static source commentary.");
        return sb.toString().trim();
    }

    private String renderUnsupportedStructuredProbe(String prompt, Map<String, String> fields) {
        JSONObject envelope = baseStructuredProbeEnvelope(prompt, fields,
            "structured-probe.unsupported", "unsupported", "UNSUPPORTED_STRUCTURED_PROBE");
        envelope.put("supportedRoutes", new JSONArray(List.of(
            "lazarus_engine.active_inference",
            "openclaw_execution.formal_proof",
            "f0_engine.adversarial_perturbation"
        )));
        return """
            AI STRUCTURED PROBE
            Prompt: %s
            Envelope:
            %s

            Result: No structured probe route matched this prompt yet.
            """.formatted(prompt, envelope.toString()).trim();
    }

    private JSONObject baseStructuredProbeEnvelope(
        String prompt,
        Map<String, String> fields,
        String route,
        String status,
        String verdict
    ) {
        JSONObject envelope = new JSONObject();
        envelope.put("schema", "fraynix.ai-structured-probe.v1");
        envelope.put("timestamp", Instant.now().toString());
        envelope.put("prompt", prompt);
        envelope.put("intent", fields.getOrDefault("intent", ""));
        envelope.put("target", fields.getOrDefault("target", ""));
        envelope.put("method", fields.getOrDefault("method", ""));
        envelope.put("features", fields.getOrDefault("features", ""));
        envelope.put("constraints", fields.getOrDefault("constraints", ""));
        envelope.put("output", fields.getOrDefault("output", ""));
        envelope.put("route", route);
        envelope.put("status", status);
        envelope.put("verdict", verdict);
        return envelope;
    }

    private String readFileIfExists(Path path) {
        if (path == null || !Files.exists(path)) {
            return "";
        }
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }

    private int extractIntConstant(String source, String constant, int fallback) {
        if (source == null || source.isBlank() || constant == null || constant.isBlank()) {
            return fallback;
        }
        Matcher matcher = Pattern.compile(Pattern.quote(constant) + "\\s*=\\s*(\\d+)").matcher(source);
        return matcher.find() ? parseInt(matcher.group(1), fallback) : fallback;
    }

    private double extractDoubleConstant(String source, String constant, double fallback) {
        if (source == null || source.isBlank() || constant == null || constant.isBlank()) {
            return fallback;
        }
        Matcher matcher = Pattern.compile(Pattern.quote(constant) + "\\s*=\\s*([0-9]+(?:\\.[0-9]+)?)").matcher(source);
        if (!matcher.find()) {
            return fallback;
        }
        try {
            return Double.parseDouble(matcher.group(1));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private int extractRustFieldEngineSize(String source, int fallback) {
        Matcher matcher = Pattern.compile("FieldEngine::new\\((\\d+),\\s*([0-9.]+),\\s*([0-9.]+)\\)").matcher(source == null ? "" : source);
        return matcher.find() ? parseInt(matcher.group(1), fallback) : fallback;
    }

    private double extractRustFieldEngineArg(String source, int argumentIndex, double fallback) {
        Matcher matcher = Pattern.compile("FieldEngine::new\\((\\d+),\\s*([0-9.]+),\\s*([0-9.]+)\\)").matcher(source == null ? "" : source);
        if (!matcher.find()) {
            return fallback;
        }
        try {
            return Double.parseDouble(matcher.group(argumentIndex));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return fallback;
        }
    }

    private boolean containsAnyIgnoreCase(String haystack, String... needles) {
        if (haystack == null || haystack.isBlank() || needles == null || needles.length == 0) {
            return false;
        }
        String lowered = haystack.toLowerCase(Locale.ROOT);
        for (String needle : needles) {
            if (needle != null && !needle.isBlank() && lowered.contains(needle.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private boolean isCommandAvailable(String command) {
        if (command == null || command.isBlank()) {
            return false;
        }
        String pathValue = System.getenv("PATH");
        if (pathValue == null || pathValue.isBlank()) {
            return false;
        }
        String[] pathext = Optional.ofNullable(System.getenv("PATHEXT"))
            .orElse(".EXE;.BAT;.CMD;.COM")
            .split(";");
        for (String directory : pathValue.split(Pattern.quote(File.pathSeparator))) {
            if (directory == null || directory.isBlank()) {
                continue;
            }
            Path base = Paths.get(directory);
            if (!Files.isDirectory(base)) {
                continue;
            }
            Path direct = base.resolve(command);
            if (Files.isRegularFile(direct)) {
                return true;
            }
            for (String extension : pathext) {
                Path candidate = base.resolve(command + extension.toLowerCase(Locale.ROOT));
                if (Files.isRegularFile(candidate)) {
                    return true;
                }
                candidate = base.resolve(command + extension.toUpperCase(Locale.ROOT));
                if (Files.isRegularFile(candidate)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String formatDouble(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }

    private String executeAiRunLane(String command) {
        String normalizedCommand = command == null ? "" : command.trim();
        if (normalizedCommand.isBlank()) {
            return "Usage: ai run <fraynix-command>";
        }
        if (normalizedCommand.length() > AI_RUN_MAX_COMMAND_CHARS) {
            String[] parts = normalizedCommand.split("\\s+", 2);
            String oversizedHead = parts[0].toLowerCase(Locale.ROOT);
            return renderAiRunEnvelope(
                normalizedCommand,
                oversizedHead,
                "rejected",
                "blocked",
                false,
                null,
                "POLICY_COMMAND_TOO_LARGE",
                0L,
                "ai run command exceeds " + AI_RUN_MAX_COMMAND_CHARS + " characters.",
                null,
                null,
                null,
                null
            );
        }
        if (containsDisallowedControlChars(normalizedCommand)) {
            String[] parts = normalizedCommand.split("\\s+", 2);
            String controlHead = parts[0].toLowerCase(Locale.ROOT);
            return renderAiRunEnvelope(
                normalizedCommand,
                controlHead,
                "rejected",
                "blocked",
                false,
                null,
                "POLICY_COMMAND_CONTROL_CHARS",
                0L,
                "ai run command contains disallowed control characters.",
                null,
                null,
                null,
                null
            );
        }

        String[] parts = normalizedCommand.split("\\s+", 2);
        String head = parts[0].toLowerCase(Locale.ROOT);
        String tail = parts.length > 1 ? parts[1].trim() : "";
        boolean lockAcquired = false;
        String priorInFlight = "";
        try {
            lockAcquired = aiRunExecutionLock.tryLock(AI_RUN_LOCK_WAIT_MS, TimeUnit.MILLISECONDS);
            if (!lockAcquired) {
                priorInFlight = aiRunInFlightCommand == null ? "" : aiRunInFlightCommand;
                String busyMessage = priorInFlight.isBlank()
                    ? "ai run lane is busy with another command."
                    : "ai run lane is busy. In-flight command: " + compactBlock(priorInFlight, 1, 160);
                return renderAiRunEnvelope(
                    normalizedCommand,
                    head,
                    "busy",
                    "blocked",
                    false,
                    null,
                    "POLICY_RUN_BUSY",
                    0L,
                    busyMessage,
                    null,
                    null,
                    null,
                    null
                );
            }
            aiRunInFlightCommand = normalizedCommand;

            boolean inProcessLane = isAiRunInProcessHead(head);
            if (inProcessLane) {
                AiRunPolicyDecision policy = evaluateAiRunInProcessAllowlist(head, tail);
                if (!policy.allowed()) {
                    return renderAiRunEnvelope(
                        normalizedCommand,
                        head,
                        "in-process",
                        "blocked",
                        false,
                        null,
                        policy.errorCode(),
                        0L,
                        policy.reason(),
                        null,
                        null,
                        null,
                        null
                    );
                }

                AiRunExecution execution = executeInProcessWithTimeout(normalizedCommand);
                return renderAiRunEnvelope(
                    normalizedCommand,
                    head,
                    "in-process",
                    execution.status(),
                    true,
                    execution.exitCode(),
                    execution.errorCode(),
                    execution.durationMs(),
                    execution.message(),
                    null,
                    null,
                    null,
                    null,
                    execution.output()
                );
            }

            Optional<CatalogSectionMatch> fraynixSection = resolveFraynixSectionByHead(head);
            if (fraynixSection.isEmpty()) {
                return renderAiRunEnvelope(
                    normalizedCommand,
                    head,
                    "unresolved",
                    "unresolved",
                    false,
                    null,
                    "UNKNOWN_COMMAND_HEAD",
                    0L,
                    "No Fraynix menu command head match found. Try: ai " + normalizedCommand,
                    null,
                    null,
                    null,
                    null
                );
            }

            CatalogSectionMatch match = fraynixSection.get();
            String routePlan = route("fraynix " + match.section().id());
            AiRunPolicyDecision mappedPolicy = evaluateAiRunMappedAllowlist(head, tail);
            if (!mappedPolicy.allowed()) {
                return renderAiRunEnvelope(
                    normalizedCommand,
                    head,
                    "fraynix-mapped",
                    "blocked",
                    false,
                    null,
                    mappedPolicy.errorCode(),
                    0L,
                    mappedPolicy.reason(),
                    match.section().id(),
                    match.section().title(),
                    null,
                    routePlan
                );
            }

            String targetId = fraynixCommandCatalog.defaultSubsystemId(match.section())
                .orElse(fraynixCommandCatalog.primarySubsystemId());
            Optional<UltimateSubsystem> targetSubsystem = registry.find(targetId);
            if (targetSubsystem.isEmpty()) {
                return renderAiRunEnvelope(
                    normalizedCommand,
                    head,
                    "fraynix-mapped",
                    "error",
                    true,
                    null,
                    "TARGET_NOT_FOUND",
                    0L,
                    "Default target subsystem '" + targetId + "' was not found.",
                    match.section().id(),
                    match.section().title(),
                    targetId,
                    routePlan
                );
            }

            AiRunExecution execution = executeMappedFraynixCommand(targetSubsystem.get(), normalizedCommand);
            return renderAiRunEnvelope(
                normalizedCommand,
                head,
                "fraynix-mapped",
                execution.status(),
                true,
                execution.exitCode(),
                execution.errorCode(),
                execution.durationMs(),
                execution.message(),
                match.section().id(),
                match.section().title(),
                targetSubsystem.get().id(),
                routePlan,
                execution.output()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return renderAiRunEnvelope(
                normalizedCommand,
                head,
                "busy",
                "error",
                false,
                null,
                "EXEC_INTERRUPTED",
                0L,
                "Interrupted while waiting for ai run execution slot.",
                null,
                null,
                null,
                null
            );
        } finally {
            if (lockAcquired) {
                aiRunInFlightCommand = "";
                aiRunExecutionLock.unlock();
            }
        }
    }

    private Optional<CatalogSectionMatch> resolveFraynixSectionByHead(String head) {
        if (head == null || head.isBlank()) {
            return Optional.empty();
        }
        for (UltimateCommandCatalog.CommandSection section : fraynixCommandCatalog.sections()) {
            for (UltimateCommandCatalog.CommandEntry entry : section.commands()) {
                if (head.equals(commandHead(entry.syntax()))) {
                    return Optional.of(new CatalogSectionMatch(fraynixCommandCatalog, section));
                }
            }
        }
        return Optional.empty();
    }

    private AiRunPolicyDecision evaluateAiRunInProcessAllowlist(String head, String tail) {
        if (AI_RUN_BLOCKED_HEADS.contains(head)) {
            return denyAiRunPolicy("POLICY_HEAD_BLOCKED", "Head '" + head + "' is blocked for ai run.");
        }
        if (!AI_RUN_ALLOWED_IN_PROCESS_HEADS.contains(head)) {
            return denyAiRunPolicy("POLICY_HEAD_NOT_ALLOWLISTED", "Head '" + head + "' is not allowlisted for in-process ai run.");
        }

        String loweredTail = tail == null ? "" : tail.toLowerCase(Locale.ROOT).trim();
        return switch (head) {
            case "native" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "events"),
                "POLICY_NATIVE_SUBCOMMAND_BLOCKED"
            );
            case "willow" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "proof", "hadamard", "entangle", "echo", "measure", "spectrum"),
                "POLICY_WILLOW_SUBCOMMAND_BLOCKED"
            );
            case "tpu" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "infer", "reason", "synapse", "selfcreate", "reset"),
                "POLICY_TPU_SUBCOMMAND_BLOCKED"
            );
            case "transmudder", "mutter", "mudder" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "stats", "proof", "file", "essence"),
                "POLICY_TRANSMUDDER_SUBCOMMAND_BLOCKED"
            );
            case "absolute" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "ignite", "learn", "recall", "inject"),
                "POLICY_ABSOLUTE_SUBCOMMAND_BLOCKED"
            );
            case "babel" -> evaluateBabelTailPolicy(loweredTail);
            default -> allowAiRunPolicy("Head '" + head + "' passed in-process allowlist.");
        };
    }

    private AiRunPolicyDecision evaluateAiRunMappedAllowlist(String head, String tail) {
        if (AI_RUN_BLOCKED_HEADS.contains(head)) {
            return denyAiRunPolicy("POLICY_HEAD_BLOCKED", "Head '" + head + "' is blocked for mapped ai run.");
        }
        if (!AI_RUN_ALLOWED_FRAYNIX_HEADS.contains(head)) {
            return denyAiRunPolicy("POLICY_MAPPED_HEAD_NOT_ALLOWLISTED", "Head '" + head + "' is not allowlisted for mapped ai run.");
        }

        String loweredTail = tail == null ? "" : tail.toLowerCase(Locale.ROOT).trim();
        return switch (head) {
            case "sentient" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "think", "why", "what", "what if", "what is", "describe", "persist", "restore", "passive", "digest", "insights", "learn"),
                "POLICY_SENTIENT_SUBCOMMAND_BLOCKED"
            );
            case "rcortex" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "decide", "stimulate", "improve", "auto", "stop", "replicate", "restore"),
                "POLICY_RCORTEX_SUBCOMMAND_BLOCKED"
            );
            case "rainbow" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "step"),
                "POLICY_RAINBOW_SUBCOMMAND_BLOCKED"
            );
            case "absolute" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "ignite", "learn", "recall", "inject"),
                "POLICY_ABSOLUTE_SUBCOMMAND_BLOCKED"
            );
            case "babel" -> evaluateBabelTailPolicy(loweredTail);
            case "willow" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "proof", "hadamard", "entangle", "echo", "measure", "spectrum"),
                "POLICY_WILLOW_SUBCOMMAND_BLOCKED"
            );
            case "tpu" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "infer", "reason", "synapse", "selfcreate", "reset"),
                "POLICY_TPU_SUBCOMMAND_BLOCKED"
            );
            case "kronos" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "query", "analogy", "recall"),
                "POLICY_KRONOS_SUBCOMMAND_BLOCKED"
            );
            case "libabs" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status", "query", "reason"),
                "POLICY_LIBABS_SUBCOMMAND_BLOCKED"
            );
            case "audio" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("recognize", "classify"),
                "POLICY_AUDIO_SUBCOMMAND_BLOCKED"
            );
            case "vision" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("process"),
                "POLICY_VISION_SUBCOMMAND_BLOCKED"
            );
            case "multimodal" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("test"),
                "POLICY_MULTIMODAL_SUBCOMMAND_BLOCKED"
            );
            case "gen" -> requireTailPrefixAllowlist(
                head,
                loweredTail,
                Set.of("text", "fuse", "novelty", "stats"),
                "POLICY_GENERATIVE_SUBCOMMAND_BLOCKED"
            );
            case "prove" -> requireEmptyOrPrefixAllowlist(
                head,
                loweredTail,
                Set.of("router", "legacy"),
                "POLICY_PROVE_SUBCOMMAND_BLOCKED"
            );
            case "diagnose" -> requireEmptyOrPrefixAllowlist(
                head,
                loweredTail,
                Set.of("status"),
                "POLICY_DIAGNOSE_SUBCOMMAND_BLOCKED"
            );
            default -> allowAiRunPolicy("Head '" + head + "' passed mapped allowlist.");
        };
    }

    private AiRunPolicyDecision requireTailPrefixAllowlist(
        String head,
        String loweredTail,
        Set<String> allowedPrefixes,
        String errorCode
    ) {
        if (loweredTail == null || loweredTail.isBlank()) {
            return denyAiRunPolicy(
                errorCode,
                "Head '" + head + "' requires one of: " + String.join(", ", allowedPrefixes) + "."
            );
        }
        for (String prefix : allowedPrefixes) {
            if (tailMatchesPrefix(loweredTail, prefix)) {
                return allowAiRunPolicy("Head '" + head + "' subcommand '" + prefix + "' passed allowlist.");
            }
        }
        return denyAiRunPolicy(
            errorCode,
            "Head '" + head + "' is restricted to: " + String.join(", ", allowedPrefixes) + "."
        );
    }

    private AiRunPolicyDecision requireEmptyOrPrefixAllowlist(
        String head,
        String loweredTail,
        Set<String> allowedPrefixes,
        String errorCode
    ) {
        if (loweredTail == null || loweredTail.isBlank()) {
            return allowAiRunPolicy("Head '" + head + "' accepts empty tail.");
        }
        for (String prefix : allowedPrefixes) {
            if (tailMatchesPrefix(loweredTail, prefix)) {
                return allowAiRunPolicy("Head '" + head + "' subcommand '" + prefix + "' passed allowlist.");
            }
        }
        return denyAiRunPolicy(
            errorCode,
            "Head '" + head + "' is restricted to empty tail or: " + String.join(", ", allowedPrefixes) + "."
        );
    }

    private AiRunPolicyDecision evaluateBabelTailPolicy(String loweredTail) {
        if (loweredTail == null || loweredTail.isBlank()) {
            return denyAiRunPolicy(
                "POLICY_BABEL_SUBCOMMAND_BLOCKED",
                "Head 'babel' requires a payload or subcommand (status, target <lang>, concept)."
            );
        }
        if (loweredTail.length() > 240) {
            return denyAiRunPolicy(
                "POLICY_BABEL_PAYLOAD_TOO_LARGE",
                "Head 'babel' payload exceeds 240 characters."
            );
        }
        if (loweredTail.contains("&&")
            || loweredTail.contains("||")
            || loweredTail.contains(";")
            || loweredTail.contains("`")
            || loweredTail.contains("$(")
            || loweredTail.contains("|")
            || loweredTail.contains(">")
            || loweredTail.contains("<")) {
            return denyAiRunPolicy(
                "POLICY_BABEL_PAYLOAD_BLOCKED",
                "Head 'babel' payload contains blocked shell-like operators."
            );
        }
        if ("status".equals(loweredTail)) {
            return allowAiRunPolicy("Head 'babel' status subcommand passed allowlist.");
        }
        if (loweredTail.startsWith("target ")) {
            String target = loweredTail.substring("target ".length()).trim();
            if (target.matches("^[a-z0-9_.+-]{1,24}$")) {
                return allowAiRunPolicy("Head 'babel' target subcommand passed allowlist.");
            }
            return denyAiRunPolicy(
                "POLICY_BABEL_TARGET_BLOCKED",
                "Head 'babel' target expects one safe language token."
            );
        }
        if (!BABEL_SAFE_PAYLOAD.matcher(loweredTail).matches()) {
            return denyAiRunPolicy(
                "POLICY_BABEL_PAYLOAD_BLOCKED",
                "Head 'babel' payload contains unsupported characters."
            );
        }
        return allowAiRunPolicy("Head 'babel' payload passed safe transmutation policy.");
    }

    private static boolean tailMatchesPrefix(String loweredTail, String loweredPrefix) {
        return loweredTail.equals(loweredPrefix) || loweredTail.startsWith(loweredPrefix + " ");
    }

    private static AiRunPolicyDecision allowAiRunPolicy(String reason) {
        return new AiRunPolicyDecision(true, null, reason);
    }

    private static AiRunPolicyDecision denyAiRunPolicy(String errorCode, String reason) {
        return new AiRunPolicyDecision(false, errorCode, reason);
    }

    private AiRunExecution executeInProcessWithTimeout(String command) {
        Instant started = Instant.now();
        ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "ai-run-in-process");
            thread.setDaemon(true);
            return thread;
        });
        Future<String> future = executor.submit(() -> executeInProcessAiRunCommand(command));
        try {
            String output = future.get(AI_RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            long durationMs = Duration.between(started, Instant.now()).toMillis();
            return new AiRunExecution(
                "ok",
                0,
                null,
                "In-process ai run command completed.",
                output == null ? "(empty)" : output,
                durationMs
            );
        } catch (TimeoutException e) {
            future.cancel(true);
            long durationMs = Duration.between(started, Instant.now()).toMillis();
            return new AiRunExecution(
                "timeout",
                null,
                "EXEC_TIMEOUT",
                "In-process command timed out after " + AI_RUN_TIMEOUT_SECONDS + " second(s).",
                "",
                durationMs
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long durationMs = Duration.between(started, Instant.now()).toMillis();
            return new AiRunExecution(
                "error",
                null,
                "EXEC_INTERRUPTED",
                "In-process command interrupted while executing.",
                "",
                durationMs
            );
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() == null ? e : e.getCause();
            long durationMs = Duration.between(started, Instant.now()).toMillis();
            return new AiRunExecution(
                "error",
                null,
                "EXEC_FAULT",
                "In-process command faulted: " + cause.getMessage(),
                "",
                durationMs
            );
        } finally {
            executor.shutdownNow();
        }
    }

    private String executeInProcessAiRunCommand(String command) {
        String normalized = command == null ? "" : command.trim();
        if (normalized.isBlank()) {
            return "(empty)";
        }
        String[] parts = normalized.split("\\s+", 2);
        String head = parts[0].toLowerCase(Locale.ROOT);
        String tail = parts.length > 1 ? parts[1].trim() : "";
        return switch (head) {
            case "absolute" -> executeAbsoluteFastLane(tail);
            case "babel" -> executeBabelFastLane(tail);
            default -> execute(normalized);
        };
    }

    private String executeAbsoluteFastLane(String tail) {
        String loweredTail = tail == null ? "" : tail.toLowerCase(Locale.ROOT).trim();
        AeonAbsolute absolute = AeonAbsolute.getInstance();
        if (loweredTail.isBlank() || tailMatchesPrefix(loweredTail, "status")) {
            return absolute.getStatus();
        }
        if (tailMatchesPrefix(loweredTail, "ignite")) {
            int cores = absolute.igniteSwarm();
            return """
                ABSOLUTE IGNITE COMPLETE
                cores: %d
                directive: %s
                swarmActive: %s
                """.formatted(cores, absolute.getDirective(), absolute.isSwarmActive()).stripIndent().trim();
        }
        if (tailMatchesPrefix(loweredTail, "learn")) {
            String payload = payloadAfterCommand(tail, "learn");
            if (payload.isBlank()) {
                return "Usage: absolute learn <text>";
            }
            absolute.holoLearn(payload);
            double similarity = absolute.holoRecall(payload);
            return """
                ABSOLUTE LEARN COMPLETE
                pattern: %s
                immediateRecall: %.6f
                """.formatted(payload, similarity).stripIndent().trim();
        }
        if (tailMatchesPrefix(loweredTail, "recall")) {
            String payload = payloadAfterCommand(tail, "recall");
            if (payload.isBlank()) {
                return "Usage: absolute recall <text>";
            }
            double similarity = absolute.holoRecall(payload);
            return """
                ABSOLUTE RECALL
                query: %s
                similarity: %.6f
                """.formatted(payload, similarity).stripIndent().trim();
        }
        if (tailMatchesPrefix(loweredTail, "inject")) {
            String payload = payloadAfterCommand(tail, "inject");
            if (payload.isBlank()) {
                return "Usage: absolute inject <text>";
            }
            absolute.injectStimulus(payload);
            double similarity = absolute.holoRecall(payload);
            return """
                ABSOLUTE INJECT COMPLETE
                stimulus: %s
                postInjectRecall: %.6f
                """.formatted(payload, similarity).stripIndent().trim();
        }
        return "Usage: absolute [status|ignite|learn <text>|recall <text>|inject <text>]";
    }

    private String executeBabelFastLane(String tail) {
        String loweredTail = tail == null ? "" : tail.toLowerCase(Locale.ROOT).trim();
        AeonBabel babel = AeonBabel.getInstance();
        if (loweredTail.isBlank() || tailMatchesPrefix(loweredTail, "status")) {
            return babel.getStatus();
        }
        if (tailMatchesPrefix(loweredTail, "target")) {
            String payload = payloadAfterCommand(tail, "target");
            if (payload.isBlank()) {
                return "Usage: babel target <C|ASM|PYTHON|GO|JS>";
            }
            String firstToken = payload.split("\\s+")[0];
            String normalizedTarget = normalizeBabelLanguage(firstToken);
            if (normalizedTarget == null) {
                return "Usage: babel target <C|ASM|PYTHON|GO|JS>";
            }
            babel.setTarget(normalizedTarget);
            return """
                BABEL TARGET SET
                target: %s
                """.formatted(babel.getTargetLang()).stripIndent().trim();
        }

        String transmuteTail = tail == null ? "" : tail.trim();
        if (transmuteTail.isBlank()) {
            return "Usage: babel <concept> [C|ASM|PYTHON|GO|JS]";
        }

        String[] tokens = transmuteTail.split("\\s+");
        String lang = null;
        String concept = transmuteTail;
        if (tokens.length > 1) {
            String candidate = tokens[tokens.length - 1];
            String normalizedCandidate = normalizeBabelLanguage(candidate);
            if (normalizedCandidate != null) {
                lang = normalizedCandidate;
                int cutoff = transmuteTail.length() - candidate.length();
                concept = transmuteTail.substring(0, cutoff).trim();
            }
        }
        if (concept.isBlank()) {
            return "Usage: babel <concept> [C|ASM|PYTHON|GO|JS]";
        }
        if (lang == null) {
            String currentTarget = normalizeBabelLanguage(babel.getTargetLang());
            lang = currentTarget == null ? "C" : currentTarget;
        }

        String code = babel.transmute(concept, lang);
        int lineCount = code == null || code.isBlank() ? 0 : code.split("\\R", -1).length;
        return """
            BABEL TRANSMUTATION COMPLETE
            concept: %s
            language: %s
            lines: %d
            %s
            """.formatted(concept, lang, lineCount, code == null ? "" : code).stripIndent().trim();
    }

    private static String payloadAfterCommand(String tail, String commandWord) {
        if (tail == null || commandWord == null) {
            return "";
        }
        String trimmed = tail.trim();
        if (trimmed.isBlank()) {
            return "";
        }
        if (trimmed.length() <= commandWord.length()) {
            return "";
        }
        return trimmed.substring(commandWord.length()).trim();
    }

    private static String normalizeBabelLanguage(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String lowered = token.toLowerCase(Locale.ROOT).trim();
        return switch (lowered) {
            case "c", "c99" -> "C";
            case "asm", "assembly", "x86", "x86_64" -> "ASM";
            case "py", "python", "python3" -> "PYTHON";
            case "go", "golang" -> "GO";
            case "js", "javascript", "node", "v8" -> "JS";
            default -> null;
        };
    }

    private AiRunExecution executeMappedFraynixCommand(UltimateSubsystem subsystem, String command) {
        if (!subsystem.runnable()) {
            return new AiRunExecution(
                "error",
                null,
                "TARGET_NOT_RUNNABLE",
                "Target subsystem is not runnable: " + subsystem.id(),
                "",
                0L
            );
        }
        if ("fraynix-boot".equalsIgnoreCase(subsystem.id())) {
            return runCommandThroughFraynixBootShell(subsystem, command);
        }
        String output = launchSubsystem(subsystem, "ai run mapped command");
        return new AiRunExecution(
            "ok",
            0,
            null,
            "Mapped ai run launched via subsystem '" + subsystem.id() + "'.",
            output,
            0L
        );
    }

    private AiRunExecution runCommandThroughFraynixBootShell(UltimateSubsystem subsystem, String command) {
        Instant started = Instant.now();
        try {
            PersistentFraynixBootSession session = getOrStartPersistentFraynixBootSession(subsystem);
            String capture = session.runCommand(command, Duration.ofSeconds(AI_RUN_TIMEOUT_SECONDS));
            long durationMs = Duration.between(started, Instant.now()).toMillis();
            return new AiRunExecution(
                "ok",
                0,
                null,
                "Mapped Fraynix command completed via persistent fraynix-boot session.",
                capture,
                durationMs
            );
        } catch (TimeoutException e) {
            shutdownPersistentFraynixBootSession();
            long durationMs = Duration.between(started, Instant.now()).toMillis();
            return new AiRunExecution(
                "timeout",
                null,
                "EXEC_TIMEOUT",
                "Mapped Fraynix command timed out after " + AI_RUN_TIMEOUT_SECONDS + " second(s).",
                "",
                durationMs
            );
        } catch (IOException e) {
            shutdownPersistentFraynixBootSession();
            long durationMs = Duration.between(started, Instant.now()).toMillis();
            return new AiRunExecution(
                "error",
                null,
                "EXEC_IO_FAULT",
                "Execution fault while running mapped Fraynix command: " + e.getMessage(),
                "",
                durationMs
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            shutdownPersistentFraynixBootSession();
            long durationMs = Duration.between(started, Instant.now()).toMillis();
            return new AiRunExecution(
                "error",
                null,
                "EXEC_INTERRUPTED",
                "Execution interrupted while running mapped Fraynix command.",
                "",
                durationMs
            );
        }
    }

    private PersistentFraynixBootSession getOrStartPersistentFraynixBootSession(UltimateSubsystem subsystem)
        throws IOException, InterruptedException, TimeoutException {
        fraynixBootSessionLock.lock();
        try {
            if (fraynixBootSession != null && fraynixBootSession.isAlive()) {
                return fraynixBootSession;
            }
            if (fraynixBootSession != null) {
                fraynixBootSession.closeQuietly();
                fraynixBootSession = null;
            }

            ProcessBuilder processBuilder = new ProcessBuilder(subsystem.launchCommand());
            processBuilder.directory(subsystem.workingDirectory().toFile());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            PersistentFraynixBootSession created = new PersistentFraynixBootSession(process);
            try {
                created.awaitReady(Duration.ofSeconds(AI_RUN_TIMEOUT_SECONDS));
            } catch (IOException | InterruptedException | TimeoutException e) {
                created.closeQuietly();
                throw e;
            }
            fraynixBootSession = created;
            return created;
        } finally {
            fraynixBootSessionLock.unlock();
        }
    }

    private void shutdownPersistentFraynixBootSession() {
        fraynixBootSessionLock.lock();
        try {
            if (fraynixBootSession != null) {
                fraynixBootSession.closeQuietly();
                fraynixBootSession = null;
            }
        } finally {
            fraynixBootSessionLock.unlock();
        }
    }

    private String renderAiRunEnvelope(
        String command,
        String head,
        String lane,
        String status,
        boolean allowed,
        Integer exitCode,
        String errorCode,
        long durationMs,
        String message,
        String routeSectionId,
        String routeSectionTitle,
        String targetSubsystemId,
        String routePlan
    ) {
        return renderAiRunEnvelope(
            command,
            head,
            lane,
            status,
            allowed,
            exitCode,
            errorCode,
            durationMs,
            message,
            routeSectionId,
            routeSectionTitle,
            targetSubsystemId,
            routePlan,
            null
        );
    }

    private String renderAiRunEnvelope(
        String command,
        String head,
        String lane,
        String status,
        boolean allowed,
        Integer exitCode,
        String errorCode,
        long durationMs,
        String message,
        String routeSectionId,
        String routeSectionTitle,
        String targetSubsystemId,
        String routePlan,
        String output
    ) {
        String executionId = "airun-" + SESSION_STAMP.format(Instant.now()) + "-" + Integer.toUnsignedString(command.hashCode(), 36);
        JSONObject envelope = new JSONObject();
        envelope.put("schema", "fraynix.ai-run.v1");
        envelope.put("policyVersion", AI_RUN_POLICY_VERSION);
        envelope.put("executionId", executionId);
        envelope.put("timestamp", Instant.now().toString());
        envelope.put("command", command);
        envelope.put("head", head);
        envelope.put("lane", lane);
        envelope.put("status", status);
        envelope.put("allowed", allowed);
        envelope.put("timeoutSeconds", AI_RUN_TIMEOUT_SECONDS);
        envelope.put("durationMs", durationMs);
        if (exitCode != null) {
            envelope.put("exitCode", exitCode);
        }
        if (errorCode != null && !errorCode.isBlank()) {
            envelope.put("errorCode", errorCode);
        }
        if (message != null && !message.isBlank()) {
            envelope.put("message", message);
        }

        JSONObject route = new JSONObject();
        if (routeSectionId != null && !routeSectionId.isBlank()) {
            route.put("sectionId", routeSectionId);
        }
        if (routeSectionTitle != null && !routeSectionTitle.isBlank()) {
            route.put("sectionTitle", routeSectionTitle);
        }
        if (targetSubsystemId != null && !targetSubsystemId.isBlank()) {
            route.put("targetSubsystem", targetSubsystemId);
        }
        if (routePlan != null && !routePlan.isBlank()) {
            route.put("plan", compactBlock(routePlan, 16, 1800));
        }
        if (route.length() > 0) {
            envelope.put("route", route);
        }

        String compactOutput = output == null ? "" : compactBlock(output, 40, 7000);
        int outputLines = output == null || output.isBlank() ? 0 : output.split("\\R").length;
        boolean outputTruncated = isOutputTruncated(output, 40, 7000);
        envelope.put("capturedOutputChars", output == null ? 0 : output.length());
        envelope.put("capturedOutputLines", outputLines);
        envelope.put("outputPreviewChars", compactOutput.isBlank() ? 0 : compactOutput.length());
        envelope.put("outputTruncated", outputTruncated);
        String outputHash = sha256HexOrNull(output);
        if (outputHash != null) {
            envelope.put("outputSha256", outputHash);
        }

        return """
            AI RUN EXECUTION
            Command: %s
            Envelope:
            %s
            Output:
            %s
            """.formatted(command, envelope.toString(), compactOutput.isBlank() ? "(empty)" : compactOutput).stripIndent().trim();
    }

    private static boolean isOutputTruncated(String output, int maxLines, int maxChars) {
        if (output == null || output.isBlank()) {
            return false;
        }
        int lineLimit = Math.max(1, maxLines);
        int charLimit = Math.max(120, maxChars);
        if (output.split("\\R").length > lineLimit) {
            return true;
        }
        return output.trim().length() > charLimit;
    }

    private static String sha256HexOrNull(String output) {
        if (output == null || output.isBlank()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(output.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format(Locale.ROOT, "%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String tryDirectCommandPassThrough(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return null;
        }

        String[] parts = prompt.trim().split("\\s+", 2);
        String verb = parts[0].toLowerCase(Locale.ROOT);
        if ("ai".equals(verb) || !isDirectAiExecutableVerb(verb)) {
            return null;
        }
        String tail = parts.length > 1 ? parts[1].trim() : "";
        if ("resume".equals(verb) && wantsResumeLiveProbe(tail)) {
            return null;
        }

        String output = execute(prompt.trim());
        if (output == null || output.isBlank()) {
            return """
                DIRECT COMMAND PASS-THROUGH
                Command: %s
                Output: (empty)
                """.formatted(prompt.trim()).stripIndent().trim();
        }
        return """
            DIRECT COMMAND PASS-THROUGH
            Command: %s
            %s
            """.formatted(prompt.trim(), compactBlock(output, 24, 2200)).stripIndent().trim();
    }

    private boolean isDirectAiExecutableVerb(String verb) {
        if (verb == null || verb.isBlank()) {
            return false;
        }
        return switch (verb.toLowerCase(Locale.ROOT)) {
            case "help", "status", "session", "systems", "system", "abstract", "map", "synthesize",
                 "search", "show", "readmes", "commands", "catalog", "paths", "refresh",
                 "export", "persist", "demo", "math", "node", "code", "selfbuild",
                 "resume", "continuity", "willow", "transmudder", "mutter", "mudder", "goto", "jump", "route", "launch" -> true;
            default -> immortalKernel.canHandle(verb);
        };
    }

    private boolean isAiRunInProcessHead(String head) {
        if (head == null || head.isBlank()) {
            return false;
        }
        String lowered = head.toLowerCase(Locale.ROOT);
        return isDirectAiExecutableVerb(lowered)
            || immortalKernel.canHandle(lowered)
            || isKernelManagedHead(lowered)
            || AI_RUN_ALLOWED_IN_PROCESS_HEADS.contains(lowered);
    }

    private boolean isForcedAiRouterHead(String head) {
        if (head == null || head.isBlank()) {
            return false;
        }
        String lowered = head.toLowerCase(Locale.ROOT);
        return AI_ROUTER_FORCED_HEADS.contains(lowered)
            || AI_RUN_BLOCKED_HEADS.contains(lowered)
            || AI_RUN_ALLOWED_IN_PROCESS_HEADS.contains(lowered)
            || AI_RUN_ALLOWED_FRAYNIX_HEADS.contains(lowered);
    }

    private boolean looksLikeFraynixBridgeIntent(String tail) {
        if (tail == null || tail.isBlank()) {
            return false;
        }
        int headCount = 0;
        String[] tokens = tail.toLowerCase(Locale.ROOT).split("\\s+");
        for (String token : tokens) {
            String normalized = token.replaceAll("[^a-z0-9_]", "");
            if (normalized.isBlank()) {
                continue;
            }
            if (EXTENDED_MENU_HEADS.contains(normalized)) {
                headCount++;
                if (headCount >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private PromptAbstractionSnapshot buildPromptAbstractionSnapshot(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return PromptAbstractionSnapshot.empty(prompt, abstractionMemoryPath());
        }

        List<String> absorbedFragments = collectContinuityAbstractionFragments(ensureContinuitySnapshot(false));
        List<String> sourceFragments = collectSourceAbstractionFragments(prompt);
        List<DataAbstractionEngine.AbstractedArtifact> artifacts = DataAbstractionEngine.abstractPromptSources(
            prompt,
            absorbedFragments,
            sourceFragments
        );
        Map<CapabilityProfile.Capability, Double> capabilitySignals =
            DataAbstractionEngine.aggregateCapabilitySignals(artifacts);
        List<String> taskHints = DataAbstractionEngine.collectTaskHints(artifacts, ABSTRACTION_MAX_HINTS);
        List<String> provenanceSignals = DataAbstractionEngine.collectProvenanceSummaries(artifacts, ABSTRACTION_MAX_PROVENANCE);

        PromptAbstractionSnapshot provisional = new PromptAbstractionSnapshot(
            prompt,
            List.copyOf(artifacts),
            Map.copyOf(capabilitySignals),
            List.copyOf(taskHints),
            List.copyOf(provenanceSignals),
            abstractionMemoryPath(),
            false,
            "",
            ModelMemoryBridgeResult.inactive()
        );

        AbstractionPersistResult persistence = persistPromptAbstractionSnapshot(provisional);
        ModelMemoryBridgeResult bridge = pushPromptAbstractionToModelMemory(provisional);
        return new PromptAbstractionSnapshot(
            provisional.prompt(),
            provisional.artifacts(),
            provisional.capabilitySignals(),
            provisional.taskHints(),
            provisional.provenanceSignals(),
            persistence.memoryPath(),
            persistence.persisted(),
            persistence.fault(),
            bridge
        );
    }

    private String buildAbstractionLaneAction(PromptAbstractionSnapshot snapshot) {
        if (snapshot == null) {
            return "Action: abstraction-lane\nState: unavailable";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Action: abstraction-lane\n");
        sb.append("Artifacts: ").append(snapshot.artifacts().size()).append("\n");
        sb.append("CapabilitySignals: ").append(snapshot.capabilitySignals().isEmpty()
            ? "(none)"
            : compactCapabilitySignals(snapshot.capabilitySignals(), 5)).append("\n");
        sb.append("TaskHints: ").append(snapshot.taskHints().isEmpty()
            ? "(none)"
            : sampleItems(snapshot.taskHints(), 4)).append("\n");
        sb.append("Provenance: ").append(snapshot.provenanceSignals().isEmpty()
            ? "(none)"
            : sampleItems(snapshot.provenanceSignals(), 3)).append("\n");
        sb.append("MemoryPath: ").append(displayPath(snapshot.memoryPath())).append("\n");
        sb.append("Persisted: ").append(snapshot.persisted());
        if (!snapshot.persistenceFault().isBlank()) {
            sb.append(" (fault=").append(snapshot.persistenceFault()).append(")");
        }

        ModelMemoryBridgeResult bridge = snapshot.modelBridge();
        if (bridge.activeIntegration()) {
            sb.append("\nModelMemoryBridge: pushed=").append(bridge.pushed())
                .append(", capabilityScores=").append(bridge.capabilityScoresWritten())
                .append(", usageRecords=").append(bridge.usageRecordsWritten());
            if (!bridge.selectedModel().isBlank()) {
                sb.append(", selectedModel=").append(bridge.selectedModel());
            }
            if (!bridge.generatedSuite().isBlank()) {
                sb.append(", generatedSuite=").append(bridge.generatedSuite());
            }
            if (!bridge.compiledContextPack().isBlank()) {
                sb.append("\nContextPack: ").append(compactBlock(bridge.compiledContextPack(), 8, 600));
            }
            if (!bridge.fault().isBlank()) {
                sb.append("\nBridgeFault: ").append(bridge.fault());
            }
        } else {
            sb.append("\nModelMemoryBridge: inactive");
        }
        return sb.toString().trim();
    }

    private String renderAbstractionStatus() {
        Path memoryPath = abstractionMemoryPath();
        List<JSONObject> rows = readAbstractionMemoryRows(memoryPath, ABSTRACTION_MEMORY_READ_LIMIT);
        List<AbstractionBiasOutcome> outcomes = readAbstractionBiasOutcomes(ABSTRACTION_OUTCOME_READ_LIMIT);
        JSONObject latest = rows.isEmpty() ? null : rows.get(rows.size() - 1);
        JSONObject previous = rows.size() > 1 ? rows.get(rows.size() - 2) : null;

        StringBuilder sb = new StringBuilder();
        sb.append("FRAYNIX ABSTRACTION MEMORY\n");
        sb.append("Path: ").append(displayPath(memoryPath)).append("\n");
        sb.append("Records: ").append(rows.size()).append("\n");
        sb.append("OutcomePath: ").append(displayPath(abstractionOutcomeMemoryPath())).append("\n");
        sb.append("Bias outcomes: ").append(outcomes.size()).append("\n");
        if (latest == null) {
            sb.append("Latest: (none)\n");
            sb.append("Hint: run any `ai ...` prompt to append a fresh abstraction record.");
            return sb.toString().trim();
        }

        String observedAt = latest.optString("observedAt", "").trim();
        String prompt = latest.optString("prompt", "").trim();
        int artifactCount = abstractionArtifactCount(latest);
        Map<CapabilityProfile.Capability, Double> capabilitySignals =
            parseCapabilitySignals(latest.optJSONObject("capabilitySignals"));
        List<String> taskHints = jsonStringList(latest.optJSONArray("taskHints"));
        List<String> provenance = jsonStringList(latest.optJSONArray("provenanceSignals"));

        sb.append("Latest observedAt: ").append(observedAt.isBlank() ? "(unknown)" : observedAt).append("\n");
        sb.append("Latest artifacts: ").append(artifactCount).append("\n");
        sb.append("Latest prompt: ").append(compactBlock(prompt, 2, 240).replace('\n', ' ')).append("\n");
        sb.append("Top capabilities: ").append(compactCapabilitySignals(capabilitySignals, 5)).append("\n");
        sb.append("Task hints: ").append(taskHints.isEmpty() ? "(none)" : sampleItems(taskHints, 4)).append("\n");
        sb.append("Provenance: ").append(provenance.isEmpty() ? "(none)" : sampleItems(provenance, 3));
        if (!outcomes.isEmpty()) {
            AbstractionBiasOutcome lastOutcome = outcomes.get(outcomes.size() - 1);
            sb.append("\nLast bias outcome: ")
                .append(lastOutcome.head())
                .append(" -> ")
                .append(lastOutcome.success() ? "success" : "failure")
                .append(" (latencyMs=").append(lastOutcome.latencyMs()).append(")");
        }
        if (previous != null) {
            sb.append("\nQuick delta: ").append(renderAbstractionDeltaSummary(previous, latest));
        }
        return sb.toString().trim();
    }

    private String renderAbstractionTail(int limit) {
        int bounded = Math.max(1, Math.min(50, limit));
        Path memoryPath = abstractionMemoryPath();
        List<JSONObject> rows = readAbstractionMemoryRows(memoryPath, ABSTRACTION_MEMORY_READ_LIMIT);
        if (rows.isEmpty()) {
            return """
                FRAYNIX ABSTRACTION MEMORY TAIL
                Path: %s
                Records: 0
                """.formatted(displayPath(memoryPath)).stripIndent().trim();
        }

        int start = Math.max(0, rows.size() - bounded);
        StringBuilder sb = new StringBuilder();
        sb.append("FRAYNIX ABSTRACTION MEMORY TAIL\n");
        sb.append("Path: ").append(displayPath(memoryPath)).append("\n");
        sb.append("Showing: ").append(rows.size() - start).append(" / ").append(rows.size()).append("\n");
        for (int i = start; i < rows.size(); i++) {
            JSONObject row = rows.get(i);
            Map<CapabilityProfile.Capability, Double> capabilitySignals =
                parseCapabilitySignals(row.optJSONObject("capabilitySignals"));
            List<String> taskHints = jsonStringList(row.optJSONArray("taskHints"));
            String observedAt = row.optString("observedAt", "").trim();
            String prompt = compactBlock(row.optString("prompt", ""), 1, 140).replace('\n', ' ');
            int artifactCount = abstractionArtifactCount(row);

            sb.append(i - start + 1).append(". ")
                .append(observedAt.isBlank() ? "(unknown-time)" : observedAt)
                .append(" | artifacts=").append(artifactCount)
                .append(" | top=").append(compactCapabilitySignals(capabilitySignals, 3))
                .append("\n");
            sb.append("   prompt=").append(prompt.isBlank() ? "(none)" : prompt).append("\n");
            if (!taskHints.isEmpty()) {
                sb.append("   hint=").append(compactBlock(taskHints.get(0), 1, 120).replace('\n', ' ')).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private String renderAbstractionDelta() {
        Path memoryPath = abstractionMemoryPath();
        List<JSONObject> rows = readAbstractionMemoryRows(memoryPath, ABSTRACTION_MEMORY_READ_LIMIT);
        if (rows.isEmpty()) {
            return """
                ABSTRACTION DELTA
                Path: %s
                Delta: no abstraction records available.
                """.formatted(displayPath(memoryPath)).stripIndent().trim();
        }
        if (rows.size() == 1) {
            JSONObject only = rows.get(0);
            return """
                ABSTRACTION DELTA
                Path: %s
                Delta: baseline abstraction record established.
                ObservedAt: %s
                Top capabilities: %s
                """.formatted(
                displayPath(memoryPath),
                only.optString("observedAt", "(unknown)"),
                compactCapabilitySignals(parseCapabilitySignals(only.optJSONObject("capabilitySignals")), 5)
            ).stripIndent().trim();
        }

        JSONObject previous = rows.get(rows.size() - 2);
        JSONObject current = rows.get(rows.size() - 1);

        Map<CapabilityProfile.Capability, Double> previousSignals =
            parseCapabilitySignals(previous.optJSONObject("capabilitySignals"));
        Map<CapabilityProfile.Capability, Double> currentSignals =
            parseCapabilitySignals(current.optJSONObject("capabilitySignals"));

        CapabilityProfile.Capability previousDominant = dominantCapability(previousSignals);
        CapabilityProfile.Capability currentDominant = dominantCapability(currentSignals);
        double previousScore = previousSignals.getOrDefault(previousDominant, 0.0);
        double currentScore = currentSignals.getOrDefault(currentDominant, 0.0);

        int previousArtifacts = abstractionArtifactCount(previous);
        int currentArtifacts = abstractionArtifactCount(current);

        List<String> previousHints = jsonStringList(previous.optJSONArray("taskHints"));
        List<String> currentHints = jsonStringList(current.optJSONArray("taskHints"));
        List<String> newHints = new ArrayList<>();
        for (String hint : currentHints) {
            if (!previousHints.contains(hint)) {
                newHints.add(hint);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ABSTRACTION DELTA\n");
        sb.append("Path: ").append(displayPath(memoryPath)).append("\n");
        sb.append("From: ").append(previous.optString("observedAt", "(unknown)"))
            .append(" -> ").append(current.optString("observedAt", "(unknown)")).append("\n");
        sb.append("Artifacts: ").append(previousArtifacts).append(" -> ").append(currentArtifacts).append("\n");
        sb.append("Dominant capability: ")
            .append(previousDominant.name()).append("=").append(formatScore(previousScore))
            .append(" -> ")
            .append(currentDominant.name()).append("=").append(formatScore(currentScore))
            .append("\n");
        sb.append("Capability summary: ").append(compactCapabilitySignals(previousSignals, 4))
            .append(" -> ").append(compactCapabilitySignals(currentSignals, 4)).append("\n");
        sb.append("New task hints: ").append(newHints.isEmpty() ? "(none)" : sampleItems(newHints, 3)).append("\n");
        sb.append("Summary: ").append(renderAbstractionDeltaSummary(previous, current));
        return sb.toString().trim();
    }

    private static List<JSONObject> readAbstractionMemoryRows(Path path, int maxRows) {
        if (path == null || !Files.exists(path) || !Files.isRegularFile(path)) {
            return List.of();
        }
        int bounded = Math.max(1, maxRows);
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            int start = Math.max(0, lines.size() - bounded);
            List<JSONObject> rows = new ArrayList<>();
            for (int i = start; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isBlank()) {
                    continue;
                }
                try {
                    rows.add(new JSONObject(line));
                } catch (Exception ignored) {
                    // skip malformed line but keep reading
                }
            }
            return List.copyOf(rows);
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private static Map<CapabilityProfile.Capability, Double> parseCapabilitySignals(JSONObject signals) {
        if (signals == null || signals.isEmpty()) {
            return Map.of();
        }
        Map<CapabilityProfile.Capability, Double> parsed = new LinkedHashMap<>();
        for (String key : signals.keySet()) {
            if (key == null || key.isBlank()) {
                continue;
            }
            try {
                CapabilityProfile.Capability capability = CapabilityProfile.Capability.valueOf(key.trim().toUpperCase(Locale.ROOT));
                parsed.put(capability, signals.optDouble(key, 0.0));
            } catch (Exception ignored) {
                // unknown capability keys are ignored for forward compatibility
            }
        }
        return Map.copyOf(parsed);
    }

    private static int abstractionArtifactCount(JSONObject row) {
        if (row == null) {
            return 0;
        }
        if (row.has("artifactCount")) {
            return Math.max(0, row.optInt("artifactCount", 0));
        }
        JSONArray artifacts = row.optJSONArray("artifacts");
        return artifacts == null ? 0 : artifacts.length();
    }

    private String renderAbstractionDeltaSummary(JSONObject previous, JSONObject current) {
        int previousArtifacts = abstractionArtifactCount(previous);
        int currentArtifacts = abstractionArtifactCount(current);
        Map<CapabilityProfile.Capability, Double> previousSignals =
            parseCapabilitySignals(previous.optJSONObject("capabilitySignals"));
        Map<CapabilityProfile.Capability, Double> currentSignals =
            parseCapabilitySignals(current.optJSONObject("capabilitySignals"));
        CapabilityProfile.Capability previousDominant = dominantCapability(previousSignals);
        CapabilityProfile.Capability currentDominant = dominantCapability(currentSignals);
        double previousScore = previousSignals.getOrDefault(previousDominant, 0.0);
        double currentScore = currentSignals.getOrDefault(currentDominant, 0.0);
        String dominantShift = previousDominant == currentDominant
            ? currentDominant.name() + " " + formatScore(previousScore) + " -> " + formatScore(currentScore)
            : previousDominant.name() + " -> " + currentDominant.name();
        return "artifacts " + previousArtifacts + " -> " + currentArtifacts + ", dominant " + dominantShift;
    }

    private List<String> runAbstractionBiasActions(
        String prompt,
        PromptAbstractionSnapshot snapshot,
        Set<String> executedHeads
    ) {
        AbstractionBiasPlan plan = determineAbstractionBiasPlan(prompt, snapshot);
        if (plan == null) {
            return List.of();
        }

        List<String> actions = new ArrayList<>();
        actions.add("""
            Action: abstraction-bias-plan
            Dominant capability: %s (%.3f)
            Model role bias: %s
            Lane command bias: %s
            Task hint: %s
            Rationale: %s
            """.formatted(
            plan.dominantCapability().name(),
            plan.strength(),
            plan.modelRole().name().toLowerCase(Locale.ROOT),
            plan.laneCommand(),
            plan.taskHint().isBlank() ? "(none)" : plan.taskHint(),
            plan.rationale()
        ).stripIndent().trim());

        String modelSelection = renderModelSelectionReport(
            plan.modelRole().name().toLowerCase(Locale.ROOT) + " " + plan.taskHint()
        );
        actions.add("Action: abstraction-bias-model-selection\n" + compactBlock(modelSelection, 20, 2600));
        String selectedModel = extractSelectedModelFromSelectionReport(modelSelection);

        long laneStart = System.nanoTime();
        String laneRun = executeAiRunLane(plan.laneCommand());
        long laneLatencyMs = Math.max(1L, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - laneStart));
        actions.add("Action: abstraction-bias-lane\n" + compactBlock(laneRun, 20, 2600));

        AbstractionBiasOutcome outcome = buildAbstractionBiasOutcome(plan, selectedModel, laneRun, laneLatencyMs);
        AbstractionOutcomePersistResult persistedOutcome = persistAbstractionBiasOutcome(outcome);
        AbstractionOutcomeBridgeResult bridgeOutcome = pushAbstractionBiasOutcomeToModelMemory(outcome);
        actions.add("""
            Action: abstraction-bias-outcome
            Capability: %s
            Lane: %s
            Head: %s
            Selected model: %s
            Success: %s
            LatencyMs: %d
            Error class: %s
            Persisted: %s
            OutcomePath: %s
            Bridge: %s
            """.formatted(
            outcome.dominantCapability().name(),
            outcome.laneCommand(),
            outcome.head(),
            outcome.selectedModel().isBlank() ? "(unknown)" : outcome.selectedModel(),
            outcome.success(),
            outcome.latencyMs(),
            outcome.errorClass().isBlank() ? "(none)" : outcome.errorClass(),
            persistedOutcome.persisted(),
            displayPath(persistedOutcome.path()),
            bridgeOutcome.summary()
        ).stripIndent().trim());

        if (executedHeads != null) {
            executedHeads.add(commandHead(plan.laneCommand()));
        }
        return actions;
    }

    private AbstractionBiasPlan determineAbstractionBiasPlan(String prompt, PromptAbstractionSnapshot snapshot) {
        if (snapshot == null || snapshot.capabilitySignals().isEmpty()) {
            return null;
        }
        if (prompt == null || prompt.isBlank()) {
            return null;
        }
        if (mentionsAny(prompt,
            "models select", "model select", "metamorphic",
            "ai run ", "run ", "intent=", "method="
        )) {
            return null;
        }

        Map.Entry<CapabilityProfile.Capability, Double> dominant = snapshot.capabilitySignals().entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
        if (dominant == null) {
            return null;
        }
        double strength = dominant.getValue() == null ? 0.0 : dominant.getValue();
        if (strength < 0.24) {
            return null;
        }

        CapabilityProfile.Capability capability = dominant.getKey();
        ModelRole role = switch (capability) {
            case CODING, TOOL_USE -> ModelRole.CODING;
            case LONG_CONTEXT, MEMORY_RETENTION -> ModelRole.MEMORY;
            case REFLEX_SPEED, EFFICIENCY, LOCAL_FIT -> ModelRole.REFLEX;
            case CREATIVITY, MULTILINGUAL -> ModelRole.CREATIVE;
            default -> ModelRole.REASONING;
        };

        List<AbstractionLaneCandidate> laneCandidates = laneCandidatesForCapability(capability);
        AbstractionLaneCandidate bestLane = laneCandidates.stream()
            .max(Comparator.comparingDouble(candidate -> candidate.baseWeight()
                + learnedLaneBiasScore(capability, candidate.command()) * 0.30))
            .orElse(new AbstractionLaneCandidate(defaultLaneCommandForCapability(capability), 0.5));
        String laneCommand = bestLane.command();

        double learnedCapabilityScore = learnedCapabilityBiasScore(capability);
        double adjustedStrength = clamp01((strength * 0.70) + (learnedCapabilityScore * 0.30));

        String taskHint = snapshot.taskHints().isEmpty()
            ? compactBlock(prompt, 2, 180).replaceAll("\\R", " ")
            : snapshot.taskHints().get(0);
        String rationale = "deterministic_capability_bias:" + capability.name()
            + " score=" + formatScore(strength)
            + ", learnedCapability=" + formatScore(learnedCapabilityScore)
            + ", laneWeight=" + formatScore(bestLane.baseWeight())
            + ", laneLearned=" + formatScore(learnedLaneBiasScore(capability, laneCommand));
        return new AbstractionBiasPlan(capability, adjustedStrength, role, taskHint, laneCommand, rationale);
    }

    private List<AbstractionLaneCandidate> laneCandidatesForCapability(CapabilityProfile.Capability capability) {
        if (capability == null) {
            return List.of(new AbstractionLaneCandidate("native status", 0.50));
        }
        return switch (capability) {
            case CODING, TOOL_USE -> List.of(
                new AbstractionLaneCandidate("transmudder stats", 0.90),
                new AbstractionLaneCandidate("native status", 0.65),
                new AbstractionLaneCandidate("tpu status", 0.55)
            );
            case LONG_CONTEXT, MEMORY_RETENTION -> List.of(
                new AbstractionLaneCandidate("continuity status", 0.90),
                new AbstractionLaneCandidate("native status", 0.55),
                new AbstractionLaneCandidate("transmudder stats", 0.50)
            );
            case REFLEX_SPEED, EFFICIENCY, LOCAL_FIT -> List.of(
                new AbstractionLaneCandidate("native status", 0.90),
                new AbstractionLaneCandidate("continuity status", 0.50),
                new AbstractionLaneCandidate("tpu status", 0.45)
            );
            case CREATIVITY, MULTILINGUAL -> List.of(
                new AbstractionLaneCandidate("babel status", 0.90),
                new AbstractionLaneCandidate("native status", 0.50),
                new AbstractionLaneCandidate("continuity status", 0.45)
            );
            case MULTIMODAL -> List.of(
                new AbstractionLaneCandidate("multimodal test", 0.90),
                new AbstractionLaneCandidate("native status", 0.55),
                new AbstractionLaneCandidate("tpu status", 0.50)
            );
            case SAFETY_ALIGNMENT, PRECISION, REASONING -> List.of(
                new AbstractionLaneCandidate("tpu status", 0.90),
                new AbstractionLaneCandidate("native status", 0.60),
                new AbstractionLaneCandidate("continuity status", 0.50)
            );
        };
    }

    private String defaultLaneCommandForCapability(CapabilityProfile.Capability capability) {
        return laneCandidatesForCapability(capability).stream()
            .max(Comparator.comparingDouble(AbstractionLaneCandidate::baseWeight))
            .map(AbstractionLaneCandidate::command)
            .orElse("native status");
    }

    private double learnedLaneBiasScore(CapabilityProfile.Capability capability, String laneCommand) {
        if (laneCommand == null || laneCommand.isBlank()) {
            return 0.50;
        }
        List<AbstractionBiasOutcome> outcomes = readAbstractionBiasOutcomes(ABSTRACTION_OUTCOME_READ_LIMIT);
        if (outcomes.isEmpty()) {
            return 0.50;
        }

        List<AbstractionBiasOutcome> capRows = outcomes.stream()
            .filter(row -> row.dominantCapability() == capability)
            .toList();
        if (capRows.isEmpty()) {
            return 0.50;
        }

        List<AbstractionBiasOutcome> laneRows = capRows.stream()
            .filter(row -> laneCommand.equalsIgnoreCase(row.laneCommand()))
            .toList();
        if (laneRows.isEmpty()) {
            return 0.50;
        }

        double successEma = 0.50;
        double latencyTotal = 0.0;
        int latencyCount = 0;
        for (AbstractionBiasOutcome row : laneRows) {
            successEma = successEma + ABSTRACTION_SUCCESS_EMA_ALPHA * ((row.success() ? 1.0 : 0.0) - successEma);
            if (row.latencyMs() > 0) {
                latencyTotal += row.latencyMs();
                latencyCount++;
            }
        }
        double avgLatency = latencyCount == 0 ? 0.0 : latencyTotal / latencyCount;
        double latencyScore = normalizeLatencyScore(avgLatency);
        double exploration = Math.sqrt(Math.max(0.0, Math.log(capRows.size() + 1.0) / (laneRows.size() + 1.0)));
        return clamp01(successEma * 0.55 + latencyScore * 0.25 + exploration * 0.20);
    }

    private double learnedCapabilityBiasScore(CapabilityProfile.Capability capability) {
        List<AbstractionBiasOutcome> outcomes = readAbstractionBiasOutcomes(ABSTRACTION_OUTCOME_READ_LIMIT);
        if (outcomes.isEmpty()) {
            return 0.50;
        }
        List<AbstractionBiasOutcome> rows = outcomes.stream()
            .filter(row -> row.dominantCapability() == capability)
            .toList();
        if (rows.isEmpty()) {
            return 0.50;
        }
        double successEma = 0.50;
        double latencyTotal = 0.0;
        int latencyCount = 0;
        for (AbstractionBiasOutcome row : rows) {
            successEma = successEma + ABSTRACTION_SUCCESS_EMA_ALPHA * ((row.success() ? 1.0 : 0.0) - successEma);
            if (row.latencyMs() > 0) {
                latencyTotal += row.latencyMs();
                latencyCount++;
            }
        }
        double avgLatency = latencyCount == 0 ? 0.0 : latencyTotal / latencyCount;
        return clamp01(successEma * 0.70 + normalizeLatencyScore(avgLatency) * 0.30);
    }

    private String extractSelectedModelFromSelectionReport(String report) {
        if (report == null || report.isBlank()) {
            return "";
        }
        for (String line : report.split("\\R")) {
            if (line.startsWith("Selected:")) {
                return line.substring("Selected:".length()).trim();
            }
        }
        return "";
    }

    private AbstractionBiasOutcome buildAbstractionBiasOutcome(
        AbstractionBiasPlan plan,
        String selectedModel,
        String laneRun,
        long latencyMs
    ) {
        boolean success = laneRunSucceeded(laneRun);
        String errorClass = classifyLaneErrorClass(laneRun, success);
        return new AbstractionBiasOutcome(
            Instant.now().toString(),
            plan.dominantCapability(),
            plan.modelRole(),
            plan.laneCommand(),
            commandHead(plan.laneCommand()),
            plan.taskHint(),
            selectedModel == null ? "" : selectedModel.trim(),
            success,
            Math.max(1L, latencyMs),
            errorClass
        );
    }

    private AbstractionOutcomePersistResult persistAbstractionBiasOutcome(AbstractionBiasOutcome outcome) {
        if (outcome == null) {
            return new AbstractionOutcomePersistResult(abstractionOutcomeMemoryPath(), false, "empty_outcome");
        }
        Path path = abstractionOutcomeMemoryPath();
        try {
            Files.createDirectories(path.getParent());
            JSONObject row = new JSONObject();
            row.put("observedAt", outcome.observedAt());
            row.put("capability", outcome.dominantCapability().name());
            row.put("modelRole", outcome.modelRole().name());
            row.put("laneCommand", outcome.laneCommand());
            row.put("head", outcome.head());
            row.put("taskHint", outcome.taskHint());
            row.put("selectedModel", outcome.selectedModel());
            row.put("success", outcome.success());
            row.put("latencyMs", outcome.latencyMs());
            row.put("errorClass", outcome.errorClass());
            Files.writeString(
                path,
                row.toString() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            return new AbstractionOutcomePersistResult(path, true, "");
        } catch (Exception e) {
            return new AbstractionOutcomePersistResult(path, false, e.getMessage());
        }
    }

    private AbstractionOutcomeBridgeResult pushAbstractionBiasOutcomeToModelMemory(AbstractionBiasOutcome outcome) {
        ModelRegistryIntegration integration = ModelRegistryIntegration.getActiveIntegration();
        if (integration == null || !integration.isInitialized() || outcome == null || outcome.selectedModel().isBlank()) {
            return AbstractionOutcomeBridgeResult.inactive();
        }
        try {
            AkashicModelMemory memory = integration.getAkashicModelMemory();
            if (memory == null) {
                return new AbstractionOutcomeBridgeResult(false, false, "", "akashic_model_memory_missing");
            }
            String taskId = "ultimate.abstraction_bias."
                + outcome.dominantCapability().name().toLowerCase(Locale.ROOT)
                + "."
                + Integer.toHexString((outcome.laneCommand() + "|" + outcome.taskHint()).hashCode());
            memory.recordUsage(outcome.selectedModel(), taskId, outcome.success(), outcome.latencyMs());
            double capabilityScore = clamp01((outcome.success() ? 0.80 : 0.20) * 0.75 + normalizeLatencyScore(outcome.latencyMs()) * 0.25);
            memory.recordCapabilityScore(outcome.selectedModel(), outcome.dominantCapability().name(), capabilityScore);
            memory.recordSelection(mapModelRoleToOrgan(outcome.modelRole()), outcome.selectedModel(),
                "abstraction_bias:" + outcome.dominantCapability().name() + ":" + outcome.laneCommand());
            return new AbstractionOutcomeBridgeResult(true, true, outcome.selectedModel(), "");
        } catch (Exception e) {
            return new AbstractionOutcomeBridgeResult(true, false, "", e.getMessage());
        }
    }

    private static CapabilityProfile.Organ mapModelRoleToOrgan(ModelRole role) {
        if (role == null) {
            return CapabilityProfile.Organ.PREFRONTAL_CORTEX;
        }
        return switch (role) {
            case CODING -> CapabilityProfile.Organ.CODING_ORGAN;
            case MEMORY -> CapabilityProfile.Organ.HIPPOCAMPUS;
            case REFLEX -> CapabilityProfile.Organ.SPINAL_CORD;
            case CREATIVE -> CapabilityProfile.Organ.CREATIVE_CORTEX;
            case REASONING -> CapabilityProfile.Organ.PREFRONTAL_CORTEX;
        };
    }

    private static boolean laneRunSucceeded(String laneRun) {
        if (laneRun == null || laneRun.isBlank()) {
            return false;
        }
        String lowered = laneRun.toLowerCase(Locale.ROOT);
        if (lowered.contains("\"status\":\"ok\"")) {
            return true;
        }
        if (lowered.contains("\"status\":\"blocked\"") || lowered.contains("\"status\":\"error\"") || lowered.contains("exception")) {
            return false;
        }
        return lowered.contains("status: ok");
    }

    private static String classifyLaneErrorClass(String laneRun, boolean success) {
        if (success) {
            return "";
        }
        if (laneRun == null || laneRun.isBlank()) {
            return "EMPTY_OUTPUT";
        }
        Matcher matcher = Pattern.compile("\"errorCode\"\\s*:\\s*\"([^\"]+)\"").matcher(laneRun);
        if (matcher.find()) {
            return matcher.group(1);
        }
        if (laneRun.toLowerCase(Locale.ROOT).contains("timeout")) {
            return "TIMEOUT";
        }
        return "EXECUTION_FAILURE";
    }

    private List<AbstractionBiasOutcome> readAbstractionBiasOutcomes(int maxRows) {
        Path path = abstractionOutcomeMemoryPath();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return List.of();
        }
        int bounded = Math.max(1, maxRows);
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            int start = Math.max(0, lines.size() - bounded);
            List<AbstractionBiasOutcome> outcomes = new ArrayList<>();
            for (int i = start; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isBlank()) {
                    continue;
                }
                try {
                    JSONObject row = new JSONObject(line);
                    CapabilityProfile.Capability capability =
                        CapabilityProfile.Capability.valueOf(row.optString("capability", "REASONING"));
                    ModelRole modelRole = ModelRole.fromHint(row.optString("modelRole", "reasoning"))
                        .orElse(ModelRole.REASONING);
                    outcomes.add(new AbstractionBiasOutcome(
                        row.optString("observedAt", ""),
                        capability,
                        modelRole,
                        row.optString("laneCommand", ""),
                        row.optString("head", ""),
                        row.optString("taskHint", ""),
                        row.optString("selectedModel", ""),
                        row.optBoolean("success", false),
                        Math.max(1L, row.optLong("latencyMs", 1L)),
                        row.optString("errorClass", "")
                    ));
                } catch (Exception ignored) {
                    // skip malformed rows
                }
            }
            return List.copyOf(outcomes);
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private List<String> collectContinuityAbstractionFragments(ContinuitySnapshot snapshot) {
        if (snapshot == null) {
            return List.of();
        }
        LinkedHashSet<String> fragments = new LinkedHashSet<>();
        if (snapshot.loaded()) {
            fragments.add("continuity-loaded: true");
        }
        for (int i = 0; i < snapshot.resumeRules().size() && fragments.size() < ABSTRACTION_MAX_CONTEXT_ARTIFACTS; i++) {
            fragments.add("resume-rule: " + snapshot.resumeRules().get(i));
        }
        for (int i = 0; i < snapshot.deterministicLaneSummaries().size() && fragments.size() < ABSTRACTION_MAX_CONTEXT_ARTIFACTS; i++) {
            fragments.add("deterministic-lane: " + snapshot.deterministicLaneSummaries().get(i));
        }
        for (int i = 0; i < snapshot.deterministicLaneGaps().size() && fragments.size() < ABSTRACTION_MAX_CONTEXT_ARTIFACTS; i++) {
            fragments.add("known-gap: " + snapshot.deterministicLaneGaps().get(i));
        }
        if (!snapshot.visibleMenuHeads().isEmpty() && fragments.size() < ABSTRACTION_MAX_CONTEXT_ARTIFACTS) {
            fragments.add("visible-heads: " + sampleItems(snapshot.visibleMenuHeads(), 10));
        }
        LiveCapabilitySnapshot live = latestCapabilityProbeSnapshot;
        if (live != null && live.results() != null && !live.results().isEmpty()) {
            int limit = Math.min(3, live.results().size());
            for (int i = 0; i < limit && fragments.size() < ABSTRACTION_MAX_CONTEXT_ARTIFACTS; i++) {
                CapabilityProbeResult result = live.results().get(i);
                fragments.add("live-probe: " + result.head() + " -> " + result.status() + " (" + result.mode() + ")");
            }
        }
        return List.copyOf(fragments);
    }

    private List<String> collectSourceAbstractionFragments(String prompt) {
        LinkedHashSet<String> fragments = new LinkedHashSet<>();
        for (String url : extractUrls(prompt)) {
            fragments.add("observed-url: " + url);
        }
        for (Path target : extractPromptFileTargets(prompt)) {
            if (fragments.size() >= ABSTRACTION_MAX_CONTEXT_ARTIFACTS) {
                break;
            }
            String excerpt = readAbstractionFileExcerpt(target);
            if (!excerpt.isBlank()) {
                fragments.add("source-file: " + displayPath(target) + " -> " + excerpt);
            }
        }
        return List.copyOf(fragments);
    }

    private String readAbstractionFileExcerpt(Path path) {
        if (path == null || !Files.exists(path) || !Files.isRegularFile(path)) {
            return "";
        }
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                return "";
            }
            return compactBlock(content, 10, ABSTRACTION_MAX_FILE_EXCERPT_CHARS);
        } catch (Exception ignored) {
            return "";
        }
    }

    private Path abstractionMemoryPath() {
        return assetManagerDir.resolve("generated")
            .resolve("ultimate-system")
            .resolve("ai-abstraction-memory.jsonl");
    }

    private Path abstractionOutcomeMemoryPath() {
        return assetManagerDir.resolve("generated")
            .resolve("ultimate-system")
            .resolve("ai-abstraction-outcomes.jsonl");
    }

    private AbstractionPersistResult persistPromptAbstractionSnapshot(PromptAbstractionSnapshot snapshot) {
        Path path = snapshot == null ? abstractionMemoryPath() : snapshot.memoryPath();
        if (snapshot == null || snapshot.prompt() == null || snapshot.prompt().isBlank()) {
            return new AbstractionPersistResult(path, false, "empty_prompt");
        }
        try {
            Files.createDirectories(path.getParent());
            JSONObject row = new JSONObject();
            row.put("observedAt", Instant.now().toString());
            row.put("prompt", snapshot.prompt());
            row.put("artifactCount", snapshot.artifacts().size());
            row.put("taskHints", new JSONArray(snapshot.taskHints()));
            row.put("provenanceSignals", new JSONArray(snapshot.provenanceSignals()));

            JSONObject signals = new JSONObject();
            for (Map.Entry<CapabilityProfile.Capability, Double> entry : snapshot.capabilitySignals().entrySet()) {
                signals.put(entry.getKey().name(), entry.getValue());
            }
            row.put("capabilitySignals", signals);

            JSONArray artifactArray = new JSONArray();
            for (DataAbstractionEngine.AbstractedArtifact artifact : snapshot.artifacts()) {
                JSONObject item = new JSONObject();
                item.put("sourceType", artifact.sourceType().name());
                item.put("sourceId", artifact.sourceId());
                item.put("summary", artifact.summary());
                item.put("confidence", artifact.confidence());
                item.put("topCapabilities", new JSONArray(artifact.topCapabilities(3)));
                item.put("provenance", artifact.provenance().compact());
                artifactArray.put(item);
            }
            row.put("artifacts", artifactArray);

            Files.writeString(
                path,
                row.toString() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            return new AbstractionPersistResult(path, true, "");
        } catch (Exception e) {
            return new AbstractionPersistResult(path, false, e.getMessage());
        }
    }

    private ModelMemoryBridgeResult pushPromptAbstractionToModelMemory(PromptAbstractionSnapshot snapshot) {
        ModelRegistryIntegration integration = ModelRegistryIntegration.getActiveIntegration();
        if (integration == null || !integration.isInitialized() || snapshot == null) {
            return ModelMemoryBridgeResult.inactive();
        }
        if (snapshot.capabilitySignals().isEmpty()) {
            return new ModelMemoryBridgeResult(true, false, "", "", 0, 0, "", "no_capability_signals");
        }

        try {
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("abstractedArtifacts", DataAbstractionEngine.toContextPayload(snapshot.artifacts(), 4));
            context.put("capabilitySignals", convertCapabilitySignals(snapshot.capabilitySignals()));
            context.put("generatedTaskHints", snapshot.taskHints());
            context.put("provenanceSignals", snapshot.provenanceSignals());

            String compiledContextPack = "";
            PromptCompiler compiler = integration.getPromptCompiler();
            if (compiler != null) {
                compiledContextPack = compiler.packContextForUserPrompt(context, 4);
            }

            ModelRegistry registry = integration.getModelRegistry();
            AkashicModelMemory memory = integration.getAkashicModelMemory();
            int capabilityScoresWritten = 0;
            int usageRecordsWritten = 0;
            String selectedModel = "";
            if (registry != null && memory != null) {
                CapabilityProfile.Capability dominant = dominantCapability(snapshot.capabilitySignals());
                if (dominant != null) {
                    String model = bestModelForCapability(registry, dominant);
                    if (model != null) {
                        selectedModel = model;
                        memory.recordSelection(
                            CapabilityProfile.Organ.PREFRONTAL_CORTEX,
                            model,
                            "ultimate_abstraction:" + dominant.name()
                        );
                    }
                }

                for (Map.Entry<CapabilityProfile.Capability, Double> signal : snapshot.capabilitySignals().entrySet()) {
                    String modelId = bestModelForCapability(registry, signal.getKey());
                    if (modelId == null) {
                        continue;
                    }
                    memory.recordCapabilityScore(modelId, signal.getKey().name(), signal.getValue());
                    capabilityScoresWritten++;
                    int hintLimit = Math.min(3, snapshot.taskHints().size());
                    for (int i = 0; i < hintLimit; i++) {
                        String hint = snapshot.taskHints().get(i);
                        String taskId = "ultimate.artifact." + signal.getKey().name().toLowerCase(Locale.ROOT)
                            + "." + Integer.toHexString((hint + modelId).hashCode());
                        long syntheticLatency = Math.max(20L, Math.round((1.0 - signal.getValue()) * 120.0));
                        memory.recordUsage(modelId, taskId, true, syntheticLatency);
                        usageRecordsWritten++;
                    }
                }
            }

            String generatedSuite = "";
            EvaluationHarness harness = integration.getEvaluationHarness();
            if (harness != null && !snapshot.taskHints().isEmpty()) {
                generatedSuite = Optional.ofNullable(harness.registerGeneratedTaskSuiteFromHints(
                    "ultimate_absorption",
                    dominantCapability(snapshot.capabilitySignals()),
                    snapshot.taskHints(),
                    6000
                )).orElse("");
            }

            return new ModelMemoryBridgeResult(
                true,
                capabilityScoresWritten > 0 || usageRecordsWritten > 0 || !generatedSuite.isBlank(),
                selectedModel,
                generatedSuite,
                capabilityScoresWritten,
                usageRecordsWritten,
                compiledContextPack,
                ""
            );
        } catch (Exception e) {
            return new ModelMemoryBridgeResult(true, false, "", "", 0, 0, "", e.getMessage());
        }
    }

    private static CapabilityProfile.Capability dominantCapability(Map<CapabilityProfile.Capability, Double> signals) {
        if (signals == null || signals.isEmpty()) {
            return CapabilityProfile.Capability.REASONING;
        }
        return signals.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(CapabilityProfile.Capability.REASONING);
    }

    private static String bestModelForCapability(ModelRegistry registry, CapabilityProfile.Capability capability) {
        if (registry == null || capability == null) {
            return null;
        }
        return registry.getAllModelIds().stream()
            .filter(modelId -> registry.getProfile(modelId) != null)
            .max(Comparator.comparingDouble(modelId -> registry.getProfile(modelId).getScore(capability)))
            .orElse(null);
    }

    private static Map<String, Double> convertCapabilitySignals(Map<CapabilityProfile.Capability, Double> signals) {
        LinkedHashMap<String, Double> converted = new LinkedHashMap<>();
        if (signals == null || signals.isEmpty()) {
            return converted;
        }
        signals.entrySet().stream()
            .sorted(Map.Entry.<CapabilityProfile.Capability, Double>comparingByValue().reversed())
            .forEach(entry -> converted.put(entry.getKey().name(), entry.getValue()));
        return converted;
    }

    private static String compactCapabilitySignals(Map<CapabilityProfile.Capability, Double> signals, int limit) {
        if (signals == null || signals.isEmpty()) {
            return "(none)";
        }
        List<String> parts = new ArrayList<>();
        signals.entrySet().stream()
            .sorted(Map.Entry.<CapabilityProfile.Capability, Double>comparingByValue().reversed())
            .limit(Math.max(1, limit))
            .forEach(entry -> parts.add(
                entry.getKey().name() + "=" + String.format(Locale.ROOT, "%.2f", entry.getValue())
            ));
        return String.join(", ", parts);
    }

    private String orchestrateAiPrompt(String prompt, PromptAbstractionSnapshot abstractionSnapshot) {
        List<String> actions = new ArrayList<>();
        Set<String> unisonExecutedHeads = new LinkedHashSet<>();

        actions.add(buildAbstractionLaneAction(abstractionSnapshot));
        actions.addAll(runAbstractionBiasActions(prompt, abstractionSnapshot, unisonExecutedHeads));
        actions.addAll(runUnisonAiRouterActions(prompt, unisonExecutedHeads));

        if (looksLikeWholeSystemDiscoveryPrompt(prompt)) {
            actions.add(buildMenuVisibilityAction());
            actions.add(buildLiveCapabilityProbeAction(prompt));
        }

        if (looksLikeContinuityPrompt(prompt)) {
            actions.add("""
                Action: continuity-resume
                %s
                """.formatted(compactBlock(renderContinuityResumeReport(ensureContinuitySnapshot(false), true), 34, 4200)).stripIndent().trim());
        }

        List<String> urls = extractUrls(prompt);
        for (String url : urls) {
            actions.add(runWebIngestAction(url));
        }

        List<Path> ingestTargets = extractPromptFileTargets(prompt);
        for (Path target : ingestTargets) {
            try {
                actions.add("Action: transmudder-proof\n" + transmudderProofForPath(target, true));
            } catch (Exception e) {
                actions.add("Action: transmudder-proof\nTarget: " + displayPath(target) + "\nFault: " + e.getMessage());
            }
        }

        if (mentionsAny(prompt, "willow", "quantum", "qubit", "entangle")) {
            String willowCommand = prompt.toLowerCase(Locale.ROOT).contains("status") ? "status" : "proof 8";
            actions.add("Action: willow\n" + compactBlock(handleWillow(willowCommand), 16, 1400));
        }

        if (mentionsAny(prompt, "transmudder", "mutter", "absorb", "ingest", "digest") && ingestTargets.isEmpty()) {
            actions.add("Action: transmudder-stats\n" + transmudderStats());
        }

        actions.addAll(runKernelMenuActions(prompt));

        if (mentionsAny(prompt, "babel", "transmute", "compile")) {
            actions.add(runBabelAction(prompt));
        }

        if (mentionsAny(prompt, "native", "kernel", "runtime", "aether", "braid")) {
            actions.add("Action: native-status\n" + compactBlock(immortalKernel.execute("native status"), 20, 2000));
        }

        if (mentionsAny(prompt, "all systems", "whole system", "brains", "intellect", "recursion", "progression", "connect")) {
            actions.add("""
                Action: system-bridge
                %s
                %s
                %s
                """.formatted(
                compactBlock(status(), 8, 900),
                compactBlock(registry.renderSummary(), 12, 1200),
                compactBlock(handleSession(""), 12, 1400)
            ).stripIndent().trim());
        }

        actions.addAll(runFraynixMenuBridgeActions(prompt, unisonExecutedHeads));

        if (actions.isEmpty()) {
            actions.add("""
                Action: prompt-router-guidance
                No executable lane was inferred from this prompt.
                Supported lanes through ai prompt:
                  - state: "describe your current system and state"
                  - living proof: "strongest living proof lanes active right now"
                  - run lane: "run sentient status" or "run native status"
                  - willow: "run willow proof"
                  - babel: "transmute this intent..."
                  - transmudder/pdf: include a file path (pdf/md/txt/java/...)
                  - web scrape: include one or more https:// URLs
                  - system bridge: "connect all systems brains intellect"
                """.stripIndent().trim());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("AI PROMPT ORCHESTRATION\n");
        sb.append("Prompt: ").append(prompt).append("\n");
        sb.append("Actions executed: ").append(actions.size()).append("\n");
        for (int i = 0; i < actions.size(); i++) {
            sb.append("\n[").append(i + 1).append("] ").append(actions.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private List<String> runUnisonAiRouterActions(String prompt, Set<String> executedHeads) {
        List<String> actions = new ArrayList<>();
        if (prompt == null || prompt.isBlank()) {
            return actions;
        }

        LinkedHashSet<String> mentionedHeads = new LinkedHashSet<>();
        for (String head : EXTENDED_MENU_HEADS) {
            if (containsWord(prompt, head)) {
                mentionedHeads.add(head);
            }
        }

        if (looksLikeWholeSystemDiscoveryPrompt(prompt)) {
            mentionedHeads.addAll(AI_UNISON_WHOLE_SYSTEM_HEADS);
        }
        if (mentionedHeads.isEmpty()) {
            return actions;
        }

        LinkedHashSet<String> orderedHeads = new LinkedHashSet<>();
        for (String head : AI_UNISON_AUTONOMOUS_PRIORITY) {
            if (mentionedHeads.contains(head)) {
                orderedHeads.add(head);
            }
        }
        for (String head : mentionedHeads) {
            orderedHeads.add(head);
        }

        int runCount = 0;
        int maxRuns = looksLikeWholeSystemDiscoveryPrompt(prompt) ? 5 : AI_UNISON_MAX_AUTONOMOUS_RUNS;
        for (String head : orderedHeads) {
            if (runCount >= maxRuns) {
                break;
            }
            String command = inferUnisonAiRunCommand(head, prompt);
            if (command == null || command.isBlank()) {
                continue;
            }
            String envelope = executeAiRunLane(command);
            actions.add("""
                Action: ai-unison-run
                Head: %s
                Command: %s
                %s
                """.formatted(head, command, compactBlock(envelope, 48, 7600)).stripIndent().trim());
            trackExecutedUnisonHead(executedHeads, head);
            runCount++;
        }

        if (!actions.isEmpty()) {
            actions.add(0, """
                Action: ai-unison-plan
                Autonomous runs: %d/%d
                Mentioned heads: %d
                Guardrails: ai run policy allowlist + timeout/error envelope
                """.formatted(runCount, maxRuns, mentionedHeads.size()).stripIndent().trim());
        }
        return actions;
    }

    private static void trackExecutedUnisonHead(Set<String> executedHeads, String head) {
        if (executedHeads == null || head == null || head.isBlank()) {
            return;
        }
        String lowered = head.toLowerCase(Locale.ROOT);
        executedHeads.add(lowered);
        if ("transmudder".equals(lowered)) {
            executedHeads.add("mutter");
            executedHeads.add("mudder");
        }
        if ("mutter".equals(lowered) || "mudder".equals(lowered)) {
            executedHeads.add("transmudder");
        }
        if ("sing".equals(lowered)) {
            executedHeads.add("singularity");
        }
        if ("singularity".equals(lowered)) {
            executedHeads.add("sing");
        }
    }

    private String inferUnisonAiRunCommand(String head, String prompt) {
        if (head == null || head.isBlank()) {
            return null;
        }
        String loweredHead = head.toLowerCase(Locale.ROOT);
        String loweredPrompt = prompt == null ? "" : prompt.toLowerCase(Locale.ROOT);
        return switch (loweredHead) {
            case "native" -> loweredPrompt.contains("event") ? "native events" : "native status";
            case "tpu" -> "tpu status";
            case "willow" -> mentionsAny(loweredPrompt, "proof", "qubit", "quantum", "entangle")
                ? "willow proof 8"
                : "willow status";
            case "transmudder", "mutter", "mudder" -> "transmudder stats";
            case "absolute" -> "absolute status";
            case "babel" -> "babel status";
            case "sentient" -> "sentient status";
            case "rcortex" -> "rcortex status";
            case "rainbow" -> "rainbow status";
            case "teacher" -> "teacher status";
            case "nbs" -> "nbs status";
            case "continuity" -> "continuity status";
            case "libabs" -> "libabs status";
            case "sing", "singularity" -> "sing status";
            case "gen" -> "gen stats";
            case "multimodal" -> "multimodal test";
            case "prove" -> "prove router";
            case "diagnose" -> "diagnose status";
            case "wiring" -> "wiring";
            default -> null;
        };
    }

    private String runWebIngestAction(String url) {
        try {
            Path artifact = fetchWebArtifact(url);
            String proof = transmudderProofForPath(artifact, true);
            return """
                Action: web-scrape
                Target: %s
                Artifact: %s
                %s
                """.formatted(url, displayPath(artifact), compactBlock(proof, 20, 2200)).stripIndent().trim();
        } catch (Exception e) {
            return "Action: web-scrape\nTarget: " + url + "\nFault: " + e.getMessage();
        }
    }

    private Path fetchWebArtifact(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .GET()
            .timeout(Duration.ofSeconds(40))
            .header("User-Agent", "Fraynix-Ultimate/1.0")
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        String body = response.body() == null ? "" : response.body();
        String text = sanitizeWebToText(body);
        if (text.isBlank()) {
            text = body;
        }

        String host = Optional.ofNullable(URI.create(url).getHost()).orElse("web");
        String safeHost = host.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path dir = assetManagerDir.resolve("generated").resolve("ai-intake").resolve(SESSION_STAMP.format(Instant.now()));
        Files.createDirectories(dir);
        Path target = dir.resolve("web-" + safeHost + "-" + Math.abs(url.hashCode()) + ".txt");
        Files.writeString(target, text, StandardCharsets.UTF_8);
        return target;
    }

    private String runBabelAction(String prompt) {
        try {
            BabelTransmuter transmuter = babelTransmuter();
            var program = transmuter.transmute(prompt);
            return """
                Action: babel-transmute
                Instructions: %d
                Program:
                %s
                """.formatted(
                program == null ? 0 : program.size(),
                compactBlock(program == null ? "(none)" : program.toString(), 20, 1800)
            ).stripIndent().trim();
        } catch (Exception e) {
            return "Action: babel-transmute\nFault: " + e.getMessage();
        }
    }

    private List<String> runKernelMenuActions(String prompt) {
        List<String> actions = new ArrayList<>();
        if (prompt == null || prompt.isBlank()) {
            return actions;
        }

        if (containsWord(prompt, "aether")) {
            actions.add(runKernelCommandAction("aether"));
        }
        if (containsWord(prompt, "braid")) {
            actions.add(runKernelCommandAction("braid"));
        }
        if (containsWord(prompt, "nexus")) {
            actions.add(runKernelCommandAction("nexus status"));
        }
        if (containsWord(prompt, "omnicast")) {
            actions.add(runKernelCommandAction("omnicast status"));
        }
        if (containsWord(prompt, "spatial")) {
            String command = prompt.toLowerCase(Locale.ROOT).contains("map") ? "spatial map" : "spatial status";
            actions.add(runKernelCommandAction(command));
        }
        if (containsWord(prompt, "gravity")) {
            actions.add(runKernelCommandAction("gravity status"));
        }
        if (containsWord(prompt, "fusion")) {
            String command = prompt.toLowerCase(Locale.ROOT).contains("check") ? "fusion check" : "fusion status";
            actions.add(runKernelCommandAction(command));
        }
        if (containsWord(prompt, "miving")) {
            actions.add(runKernelCommandAction("miving status"));
        }
        if (containsWord(prompt, "qbox")) {
            String lowered = prompt.toLowerCase(Locale.ROOT);
            String command = lowered.contains("secret")
                ? "qbox secret"
                : lowered.contains("decoy")
                    ? "qbox decoy"
                    : "qbox create truth | decoy";
            actions.add(runKernelCommandAction(command));
        }
        if (containsWord(prompt, "entangle")) {
            actions.add(runKernelCommandAction("entangle create"));
        }
        if (containsWord(prompt, "collide")) {
            actions.add(runKernelCommandAction("collide logic memory"));
        }
        if (containsWord(prompt, "forge")) {
            actions.add(runKernelCommandAction("forge resonance"));
        }
        if (containsWord(prompt, "echaos")) {
            actions.add(runKernelCommandAction("echaos status"));
        }
        return actions;
    }

    private String runKernelCommandAction(String command) {
        String output = immortalKernel.execute(command);
        return """
            Action: kernel-command
            Command: %s
            %s
            """.formatted(command, compactBlock(output, 20, 2000)).stripIndent().trim();
    }

    private List<String> runFraynixMenuBridgeActions(String prompt) {
        return runFraynixMenuBridgeActions(prompt, Set.of());
    }

    private List<String> runFraynixMenuBridgeActions(String prompt, Set<String> skipHeads) {
        if (prompt == null || prompt.isBlank()) {
            return List.of();
        }

        LinkedHashSet<String> mentioned = new LinkedHashSet<>();
        for (String token : EXTENDED_MENU_HEADS) {
            if (containsWord(prompt, token)) {
                mentioned.add(token);
            }
        }
        if (mentioned.isEmpty()) {
            return List.of();
        }

        List<String> actions = new ArrayList<>();
        Set<String> normalizedSkipHeads = skipHeads == null
            ? Set.of()
            : skipHeads.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        for (String head : mentioned) {
            if (normalizedSkipHeads.contains(head)) {
                continue;
            }
            if (isAiRunInProcessHead(head)) {
                continue;
            }
            String bridge = buildFraynixMenuBridgeAction(head);
            if (bridge != null) {
                actions.add(bridge);
            }
        }
        return actions;
    }

    private boolean looksLikeWholeSystemDiscoveryPrompt(String prompt) {
        return mentionsAny(prompt,
            "all systems", "whole system", "every system", "everything works", "what works",
            "all menu systems", "menu systems", "visible to my ai prompt", "without specifying",
            "do not have to specify", "don't have to specify", "whole organism", "all organs",
            "connect all systems", "use all menu systems together");
    }

    private boolean looksLikeContinuityPrompt(String prompt) {
        return mentionsAny(prompt,
            "resume", "continuity", "pick up where", "continue from", "resume rules", "what did you load");
    }

    private boolean isKernelManagedHead(String head) {
        if (head == null || head.isBlank()) {
            return false;
        }
        return switch (head.toLowerCase(Locale.ROOT)) {
            case "aether", "braid", "nexus", "omnicast", "spatial", "gravity", "fusion",
                 "miving", "qbox", "entangle", "collide", "forge", "echaos" -> true;
            default -> false;
        };
    }

    private String buildFraynixMenuBridgeAction(String head) {
        if (head == null || head.isBlank()) {
            return null;
        }
        List<CatalogSectionMatch> matches = new ArrayList<>();
        for (UltimateCommandCatalog.CommandSection section : fraynixCommandCatalog.sections()) {
            for (UltimateCommandCatalog.CommandEntry entry : section.commands()) {
                if (head.equals(commandHead(entry.syntax()))) {
                    matches.add(new CatalogSectionMatch(fraynixCommandCatalog, section));
                    break;
                }
            }
        }
        if (matches.isEmpty()) {
            return null;
        }

        CatalogSectionMatch primary = matches.get(0);
        String routePlan = route("fraynix " + primary.section().id());
        String suggestedLaunch = "launch fraynix " + primary.section().id();
        Optional<String> defaultTarget = fraynixCommandCatalog.defaultSubsystemId(primary.section());
        if (defaultTarget.isPresent()) {
            suggestedLaunch = suggestedLaunch + " " + defaultTarget.get();
        }

        LinkedHashSet<String> sampledCommands = new LinkedHashSet<>();
        for (CatalogSectionMatch match : matches) {
            for (UltimateCommandCatalog.CommandEntry entry : match.section().commands()) {
                if (head.equals(commandHead(entry.syntax()))) {
                    sampledCommands.add(entry.syntax());
                }
                if (sampledCommands.size() >= 3) {
                    break;
                }
            }
            if (sampledCommands.size() >= 3) {
                break;
            }
        }

        return """
            Action: fraynix-menu-bridge
            Head: %s
            Section: %s (%s)
            Surface: fraynix
            Examples: %s
            Suggested launch: %s
            Route plan:
            %s
            """.formatted(
            head,
            primary.section().id(),
            primary.section().title(),
            sampledCommands.isEmpty() ? "(none)" : String.join(" | ", sampledCommands),
            suggestedLaunch,
            compactBlock(routePlan, 14, 1600)
        ).stripIndent().trim();
    }

    private static String commandHead(String syntax) {
        if (syntax == null || syntax.isBlank()) {
            return "";
        }
        String[] pieces = syntax.trim().split("\\s+", 2);
        return pieces[0].toLowerCase(Locale.ROOT);
    }

    private Set<String> fraynixCatalogHeads() {
        LinkedHashSet<String> heads = new LinkedHashSet<>();
        for (UltimateCommandCatalog.CommandSection section : fraynixCommandCatalog.sections()) {
            for (UltimateCommandCatalog.CommandEntry entry : section.commands()) {
                String head = commandHead(entry.syntax());
                if (!head.isBlank()) {
                    heads.add(head);
                }
            }
        }
        return Set.copyOf(heads);
    }

    private List<String> buildVisibleMenuHeads() {
        LinkedHashSet<String> heads = new LinkedHashSet<>();
        heads.add("resume");
        heads.add("continuity");
        heads.addAll(fraynixCatalogHeads());
        heads.addAll(EXTENDED_MENU_HEADS);
        heads.addAll(BOOT_ONLY_VISIBLE_HEADS);
        heads.add("native");
        heads.add("tpu");
        heads.add("session");
        heads.add("systems");
        return List.copyOf(heads);
    }

    private List<String> buildMenuSectionSamples() {
        List<String> samples = new ArrayList<>();
        for (UltimateCommandCatalog catalog : commandCatalogs) {
            for (UltimateCommandCatalog.CommandSection section : catalog.sections()) {
                String sample = section.commands().isEmpty() ? "(no commands)" : section.commands().get(0).syntax();
                samples.add(catalog.surfaceId() + " / " + section.title() + " -> " + sample);
            }
        }
        samples.add("boot / Infrastructure Extras -> morse | continuity | hydra | deadmans | knowledgegut | fraynixorg");
        return List.copyOf(samples);
    }

    private JSONObject readJsonIfExists(Path path) {
        if (path == null || !Files.exists(path)) {
            return new JSONObject();
        }
        try {
            return new JSONObject(Files.readString(path));
        } catch (Exception ignored) {
            return new JSONObject();
        }
    }

    private static List<String> jsonStringList(JSONArray array) {
        if (array == null) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            String value = array.optString(i, "").trim();
            if (!value.isBlank()) {
                values.add(value);
            }
        }
        return List.copyOf(values);
    }

    private ContinuitySnapshot ensureContinuitySnapshot(boolean forceExport) {
        if (!forceExport) {
            ContinuitySnapshot existing = continuitySnapshot;
            if (existing != null) {
                return existing;
            }
        }
        continuityLock.lock();
        try {
            if (!forceExport && continuitySnapshot != null) {
                return continuitySnapshot;
            }

            Path outputDir = architectureExporter.defaultOutputDir();
            Path continuityPath = outputDir.resolve("fraynix-continuity-memory.md");
            Path mathPath = outputDir.resolve("fraynix-math-canon.md");
            Path axiomPath = outputDir.resolve("fraynix-axiom-ledger.md");
            Path truthPath = outputDir.resolve("fraynix-truth-registry.json");
            if (forceExport || Stream.of(continuityPath, mathPath, axiomPath, truthPath).anyMatch(path -> !Files.exists(path))) {
                exportArtifacts(null);
            }

            JSONObject truthRegistry = readJsonIfExists(truthPath);
            JSONObject counts = truthRegistry.optJSONObject("counts");
            JSONArray resumeRulesJson = truthRegistry.optJSONArray("resumeRules");
            JSONArray deterministicLanesJson = truthRegistry.optJSONArray("deterministicLanes");

            List<String> resumeRules = jsonStringList(resumeRulesJson);
            List<String> deterministicSummaries = new ArrayList<>();
            List<String> deterministicGaps = new ArrayList<>();
            if (deterministicLanesJson != null) {
                for (int i = 0; i < deterministicLanesJson.length(); i++) {
                    JSONObject lane = deterministicLanesJson.optJSONObject(i);
                    if (lane == null) {
                        continue;
                    }
                    String id = lane.optString("id", "lane");
                    String status = lane.optString("status", "unknown");
                    String role = lane.optString("role", "unspecified");
                    deterministicSummaries.add(id + " :: " + status + " :: " + role);
                    JSONArray gaps = lane.optJSONArray("knownGaps");
                    if (gaps != null) {
                        deterministicGaps.addAll(jsonStringList(gaps));
                    }
                }
            }

            continuitySnapshot = new ContinuitySnapshot(
                Files.exists(continuityPath) && Files.exists(truthPath) && Files.exists(mathPath) && Files.exists(axiomPath),
                truthRegistry.optString("generatedAt", ""),
                continuityPath,
                mathPath,
                axiomPath,
                truthPath,
                List.copyOf(resumeRules),
                List.copyOf(deterministicSummaries),
                List.copyOf(deterministicGaps),
                buildVisibleMenuHeads(),
                buildMenuSectionSamples(),
                counts == null ? registry.all().size() : counts.optInt("subsystems", registry.all().size()),
                counts == null ? knowledgeIndex.documentCount() : counts.optInt("documents", knowledgeIndex.documentCount()),
                counts == null ? operatorManifest.livingProofLaneCount() : counts.optInt("livingProofLanes", operatorManifest.livingProofLaneCount())
            );
            return continuitySnapshot;
        } catch (Exception ignored) {
            continuitySnapshot = ContinuitySnapshot.unavailable(
                architectureExporter.defaultOutputDir().resolve("fraynix-continuity-memory.md"),
                architectureExporter.defaultOutputDir().resolve("fraynix-math-canon.md"),
                architectureExporter.defaultOutputDir().resolve("fraynix-axiom-ledger.md"),
                architectureExporter.defaultOutputDir().resolve("fraynix-truth-registry.json"),
                buildVisibleMenuHeads(),
                buildMenuSectionSamples()
            );
            return continuitySnapshot;
        } finally {
            continuityLock.unlock();
        }
    }

    private String renderContinuityRules(ContinuitySnapshot snapshot) {
        StringBuilder sb = new StringBuilder();
        sb.append("FRAYNIX CONTINUITY RULES\n");
        sb.append("Loaded: ").append(snapshot.loaded()).append("\n");
        for (int i = 0; i < snapshot.resumeRules().size(); i++) {
            sb.append(i + 1).append(". ").append(snapshot.resumeRules().get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private String renderMenuVisibilityReport(ContinuitySnapshot snapshot) {
        StringBuilder sb = new StringBuilder();
        List<String> priorityHeads = snapshot.visibleMenuHeads().stream()
            .filter(head -> Set.of("morse", "emf", "aubo", "willow", "babel", "continuity", "native").contains(head))
            .toList();
        sb.append("FRAYNIX MENU VISIBILITY\n");
        sb.append("Visible heads: ").append(snapshot.visibleMenuHeads().size()).append("\n");
        sb.append("Priority heads: ").append(priorityHeads.isEmpty() ? "[]" : priorityHeads).append("\n");
        sb.append("Head sample: ").append(sampleItems(snapshot.visibleMenuHeads(), 18)).append("\n");
        sb.append("Menu sections:\n");
        int limit = Math.min(12, snapshot.menuSectionSamples().size());
        for (int i = 0; i < limit; i++) {
            sb.append("  - ").append(snapshot.menuSectionSamples().get(i)).append("\n");
        }
        sb.append("Auto-bridge: broad prompts can now trigger menu visibility, continuity resume, and safe unison runs without exact head names.");
        return sb.toString().trim();
    }

    private String buildMenuVisibilityAction() {
        return """
            Action: menu-surface-visibility
            %s
            """.formatted(compactBlock(renderMenuVisibilityReport(ensureContinuitySnapshot(false)), 26, 3200)).stripIndent().trim();
    }

    private String buildLiveCapabilityProbeAction(String prompt) {
        LiveCapabilitySnapshot probeSnapshot = collectLiveCapabilitySnapshot(prompt);
        return renderLiveCapabilityProbeReport(probeSnapshot, "Action: live-capability-probes", "continuity snapshot + whole-system prompt + trusted probe registry");
    }

    private String renderContinuityResumeWithLiveProbe(boolean forceRefresh, String prompt) {
        ContinuitySnapshot snapshot = ensureContinuitySnapshot(forceRefresh);
        LiveCapabilitySnapshot previousSnapshot = latestCapabilityProbeSnapshot;
        LiveCapabilitySnapshot probeSnapshot = collectResumeCapabilitySnapshot(prompt);
        return """
            %s

            %s

            %s
            """.formatted(
                renderContinuityResumeReport(snapshot, true),
                renderLiveCapabilityDelta(previousSnapshot, probeSnapshot),
                renderLiveCapabilityProbeReport(probeSnapshot, "Action: resume-live-recheck", "continuity snapshot + resume top-head reprobe")
            ).stripIndent().trim();
    }

    private String renderLiveCapabilityDelta(LiveCapabilitySnapshot previousSnapshot, LiveCapabilitySnapshot currentSnapshot) {
        if (currentSnapshot == null || currentSnapshot.results().isEmpty()) {
            return "Delta: no live probe results available.";
        }
        if (previousSnapshot == null || previousSnapshot.results().isEmpty()) {
            return "Delta: baseline live probe established (no previous probe snapshot in runtime memory).";
        }

        Map<String, CapabilityProbeResult> previousByHead = capabilityResultsByHead(previousSnapshot.results());
        Map<String, CapabilityProbeResult> currentByHead = capabilityResultsByHead(currentSnapshot.results());
        List<String> changed = new ArrayList<>();
        List<String> newHeads = new ArrayList<>();

        for (Map.Entry<String, CapabilityProbeResult> entry : currentByHead.entrySet()) {
            String head = entry.getKey();
            CapabilityProbeResult current = entry.getValue();
            CapabilityProbeResult previous = previousByHead.get(head);
            if (previous == null) {
                newHeads.add(head + " -> " + current.status());
                continue;
            }
            if (!previous.status().equals(current.status())) {
                changed.add(head + " " + previous.status() + " -> " + current.status());
            }
        }

        if (changed.isEmpty() && newHeads.isEmpty()) {
            return "Delta: no head status changes since last probe.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Delta:");
        if (!changed.isEmpty()) {
            sb.append(" changed=").append(changed.size());
        }
        if (!newHeads.isEmpty()) {
            sb.append(changed.isEmpty() ? " " : " | ").append("new=").append(newHeads.size());
        }
        sb.append("\n");
        for (String entry : changed) {
            sb.append("- ").append(entry).append("\n");
        }
        for (String entry : newHeads) {
            sb.append("- new ").append(entry).append("\n");
        }
        return sb.toString().trim();
    }

    private String renderLiveCapabilityProbeReport(LiveCapabilitySnapshot probeSnapshot, String title, String sourceBias) {
        List<CapabilityProbeResult> results = probeSnapshot.results();
        long liveCount = probeSnapshot.observedLiveCount();
        long degradedCount = probeSnapshot.degradedCount();
        long visibleOnlyCount = probeSnapshot.visibleOnlyCount();
        long unavailableCount = probeSnapshot.unavailableCount();

        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        sb.append("Observed-live: ").append(liveCount)
            .append(" | Degraded: ").append(degradedCount)
            .append(" | Visible-only: ").append(visibleOnlyCount)
            .append(" | Unavailable: ").append(unavailableCount).append("\n");
        if (probeSnapshot.observedAt() != null && !probeSnapshot.observedAt().isBlank()) {
            sb.append("ObservedAt: ").append(probeSnapshot.observedAt()).append("\n");
        }
        sb.append("Source bias: ").append(sourceBias).append("\n");
        for (CapabilityProbeResult result : results) {
            sb.append("- ").append(result.head()).append(" -> ").append(result.status())
                .append(" [").append(result.mode()).append("] ").append(result.command()).append("\n");
            sb.append("  Evidence: ").append(result.evidence()).append("\n");
            sb.append("  Detail: ").append(result.detail()).append("\n");
        }
        return sb.toString().trim();
    }

    private LiveCapabilitySnapshot collectLiveCapabilitySnapshot(String prompt) {
        ContinuitySnapshot snapshot = ensureContinuitySnapshot(false);
        List<CapabilityProbeResult> results = new ArrayList<>();
        for (String head : selectWholeSystemProbeHeads(prompt, snapshot)) {
            results.add(runCapabilityProbe(head, prompt, snapshot));
        }

        LiveCapabilitySnapshot probeSnapshot = new LiveCapabilitySnapshot(
            Instant.now().toString(),
            prompt == null ? "" : prompt,
            List.copyOf(results),
            results.stream().filter(result -> "observed-live".equals(result.status())).count(),
            results.stream().filter(result -> "degraded".equals(result.status())).count(),
            results.stream().filter(result -> "visible-only".equals(result.status())).count(),
            results.stream().filter(result -> "unavailable".equals(result.status())).count()
        );
        latestCapabilityProbeSnapshot = probeSnapshot;
        return probeSnapshot;
    }

    private LiveCapabilitySnapshot collectResumeCapabilitySnapshot(String prompt) {
        ContinuitySnapshot snapshot = ensureContinuitySnapshot(false);
        LinkedHashSet<String> selected = new LinkedHashSet<>(RESUME_LIVE_PROBE_PRIORITY);
        if (prompt != null) {
            for (String head : WHOLE_SYSTEM_PROBE_PRIORITY) {
                if (containsWord(prompt, head)) {
                    selected.add(head);
                }
            }
        }
        List<CapabilityProbeResult> results = new ArrayList<>();
        for (String head : selected) {
            results.add(runCapabilityProbe(head, prompt, snapshot));
        }
        LiveCapabilitySnapshot probeSnapshot = new LiveCapabilitySnapshot(
            Instant.now().toString(),
            prompt == null ? "" : prompt,
            List.copyOf(results),
            results.stream().filter(result -> "observed-live".equals(result.status())).count(),
            results.stream().filter(result -> "degraded".equals(result.status())).count(),
            results.stream().filter(result -> "visible-only".equals(result.status())).count(),
            results.stream().filter(result -> "unavailable".equals(result.status())).count()
        );
        latestCapabilityProbeSnapshot = probeSnapshot;
        return probeSnapshot;
    }

    private Map<String, CapabilityProbeResult> capabilityResultsByHead(List<CapabilityProbeResult> results) {
        LinkedHashMap<String, CapabilityProbeResult> byHead = new LinkedHashMap<>();
        if (results == null) {
            return byHead;
        }
        for (CapabilityProbeResult result : results) {
            if (result == null || result.head() == null || result.head().isBlank()) {
                continue;
            }
            byHead.put(result.head().toLowerCase(Locale.ROOT), result);
        }
        return byHead;
    }

    private String renderContinuityResumeReport(ContinuitySnapshot snapshot, boolean includeMenuSections) {
        StringBuilder sb = new StringBuilder();
        sb.append("FRAYNIX RESUME STATE\n");
        sb.append("Loaded: ").append(snapshot.loaded()).append("\n");
        if (!snapshot.generatedAt().isBlank()) {
            sb.append("GeneratedAt: ").append(snapshot.generatedAt()).append("\n");
        }
        sb.append("Artifacts:\n");
        sb.append("  - continuity memory: ").append(displayPath(snapshot.continuityMemoryPath())).append("\n");
        sb.append("  - truth registry: ").append(displayPath(snapshot.truthRegistryPath())).append("\n");
        sb.append("  - math canon: ").append(displayPath(snapshot.mathCanonPath())).append("\n");
        sb.append("  - axiom ledger: ").append(displayPath(snapshot.axiomLedgerPath())).append("\n");
        sb.append("Counts: subsystems=").append(snapshot.subsystemCount())
            .append(", documents=").append(snapshot.documentCount())
            .append(", livingProofLanes=").append(snapshot.livingProofLaneCount()).append("\n");
        sb.append("Visible heads: ").append(snapshot.visibleMenuHeads().size())
            .append(" :: ").append(sampleItems(snapshot.visibleMenuHeads(), 16)).append("\n");
        if (includeMenuSections) {
            sb.append("Menu surfaces:\n");
            int limit = Math.min(10, snapshot.menuSectionSamples().size());
            for (int i = 0; i < limit; i++) {
                sb.append("  - ").append(snapshot.menuSectionSamples().get(i)).append("\n");
            }
        }
        if (!snapshot.deterministicLaneSummaries().isEmpty()) {
            sb.append("Deterministic lanes:\n");
            for (String lane : snapshot.deterministicLaneSummaries()) {
                sb.append("  - ").append(lane).append("\n");
            }
        }
        if (!snapshot.deterministicLaneGaps().isEmpty()) {
            sb.append("Known gaps:\n");
            for (int i = 0; i < Math.min(6, snapshot.deterministicLaneGaps().size()); i++) {
                sb.append("  - ").append(snapshot.deterministicLaneGaps().get(i)).append("\n");
            }
        }
        if (!snapshot.resumeRules().isEmpty()) {
            sb.append("Forced resume rules:\n");
            for (int i = 0; i < snapshot.resumeRules().size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(snapshot.resumeRules().get(i)).append("\n");
            }
        }
        sb.append("Next pickup: resume -> continuity menus -> ai connect all systems / use all menu systems together");
        return sb.toString().trim();
    }

    private boolean wantsResumeLiveProbe(String text) {
        return mentionsAny(text,
            "live", "resume live", "refresh live", "probe", "reprobe", "re-probe", "recheck",
            "what works now", "works now", "live recheck");
    }

    private static String sampleItems(List<String> values, int maxItems) {
        if (values == null || values.isEmpty()) {
            return "(none)";
        }
        return String.join(" | ", values.stream().limit(Math.max(1, maxItems)).toList());
    }

    private BabelTransmuter babelTransmuter() {
        if (babelTransmuter == null) {
            babelTransmuter = new BabelTransmuter();
            babelTransmuter.initialize();
            babelTransmuter.setUseMCTS(true);
        }
        return babelTransmuter;
    }

    private static List<String> extractUrls(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return List.of();
        }
        LinkedHashSet<String> urls = new LinkedHashSet<>();
        Matcher matcher = URL_TOKEN.matcher(prompt);
        while (matcher.find()) {
            String token = trimTrailingPunctuation(matcher.group());
            if (!token.isBlank()) {
                urls.add(token);
            }
        }
        return List.copyOf(urls);
    }

    private List<Path> extractPromptFileTargets(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return List.of();
        }

        LinkedHashSet<Path> targets = new LinkedHashSet<>();
        Matcher quotedMatcher = QUOTED_FILE_TOKEN.matcher(prompt);
        while (quotedMatcher.find()) {
            String token = quotedMatcher.group(1) != null ? quotedMatcher.group(1) : quotedMatcher.group(2);
            addPromptPathTarget(targets, token);
        }

        for (String rawToken : prompt.split("\\s+")) {
            String token = trimTrailingPunctuation(stripQuotes(rawToken));
            addPromptPathTarget(targets, token);
        }
        return List.copyOf(targets);
    }

    private void addPromptPathTarget(LinkedHashSet<Path> targets, String token) {
        if (token == null || token.isBlank() || !looksLikeIngestFileToken(token)) {
            return;
        }
        try {
            Path path = Path.of(token).toAbsolutePath().normalize();
            if (Files.exists(path) && Files.isRegularFile(path) && hasIngestExtension(path)) {
                targets.add(path);
            }
        } catch (Exception ignored) {
            // Ignore malformed candidate tokens.
        }
    }

    private static boolean looksLikeIngestFileToken(String token) {
        String lowered = token.toLowerCase(Locale.ROOT);
        int dot = lowered.lastIndexOf('.');
        if (dot < 0 || dot == lowered.length() - 1) {
            return false;
        }
        String extension = lowered.substring(dot + 1);
        if (!INGEST_EXTENSIONS.contains(extension)) {
            return false;
        }
        return lowered.contains("\\") || lowered.contains("/") || lowered.contains(":");
    }

    private static boolean hasIngestExtension(Path path) {
        String name = path.getFileName() == null ? "" : path.getFileName().toString().toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return false;
        }
        return INGEST_EXTENSIONS.contains(name.substring(dot + 1));
    }

    private static String sanitizeWebToText(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String text = html.replaceAll("(?is)<script[^>]*>.*?</script>", " ");
        text = text.replaceAll("(?is)<style[^>]*>.*?</style>", " ");
        text = text.replaceAll("(?is)<[^>]+>", " ");
        text = text.replaceAll("&nbsp;|&#160;", " ");
        text = text.replaceAll("&amp;", "&");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        text = text.replaceAll("&quot;", "\"");
        text = text.replaceAll("&#39;", "'");
        return text.replaceAll("\\s+", " ").trim();
    }

    private static boolean mentionsAny(String prompt, String... tokens) {
        if (prompt == null || prompt.isBlank()) {
            return false;
        }
        String lowered = prompt.toLowerCase(Locale.ROOT);
        for (String token : tokens) {
            if (token != null && !token.isBlank() && lowered.contains(token.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsWord(String text, String token) {
        if (text == null || text.isBlank() || token == null || token.isBlank()) {
            return false;
        }
        Pattern pattern = Pattern.compile("(?i)(^|[^a-z0-9_])" + Pattern.quote(token) + "([^a-z0-9_]|$)");
        return pattern.matcher(text).find();
    }

    private static boolean containsDisallowedControlChars(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n' || ch == '\r' || ch == '\u0000') {
                return true;
            }
            if (Character.isISOControl(ch) && ch != '\t') {
                return true;
            }
        }
        return false;
    }

    private static String trimTrailingPunctuation(String token) {
        if (token == null || token.isBlank()) {
            return "";
        }
        return token.replaceAll("[),.;!?]+$", "");
    }

    private static String compactBlock(String text, int maxLines, int maxChars) {
        if (text == null || text.isBlank()) {
            return "(empty)";
        }
        String[] lines = text.split("\\R");
        StringBuilder sb = new StringBuilder();
        int lineLimit = Math.max(1, maxLines);
        int count = Math.min(lines.length, lineLimit);
        for (int i = 0; i < count; i++) {
            sb.append(lines[i]).append('\n');
        }
        if (lines.length > lineLimit) {
            sb.append("... (").append(lines.length - lineLimit).append(" more lines)\n");
        }
        String compact = sb.toString().trim();
        int charLimit = Math.max(120, maxChars);
        if (compact.length() > charLimit) {
            compact = compact.substring(0, charLimit) + "...";
        }
        return compact;
    }

    private Map<String, Object> buildSystemStateEnvelope() {
        UltimateImmortalKernel.KernelSnapshot snapshot = immortalKernel.snapshot();
        SubstrateLedgerSummary ledger = readSubstrateLedgerSummary();
        ContinuitySnapshot continuity = continuitySnapshot();
        Map<String, Object> aiRunLane = aiRunLaneSnapshot();
        boolean aiRunBusy = Boolean.TRUE.equals(aiRunLane.get("busy"));
        int aiRunQueueDepth = ((Number) aiRunLane.getOrDefault("queueDepth", 0)).intValue();
        String aiRunInFlight = String.valueOf(aiRunLane.getOrDefault("inFlightCommand", "")).trim();
        String aiRunDetail = "status=" + aiRunLane.getOrDefault("status", "unknown") + ",queueDepth=" + aiRunQueueDepth;
        if (!aiRunInFlight.isBlank()) {
            aiRunDetail = aiRunDetail + ",inFlight=" + aiRunInFlight;
        }
        Runtime runtime = Runtime.getRuntime();
        long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long freeMb = runtime.freeMemory() / (1024 * 1024);
        long maxMb = runtime.maxMemory() / (1024 * 1024);

        Map<String, Object> boot = new LinkedHashMap<>();
        boot.put("nexusState", snapshot.nexusState());
        boot.put("qboxLoaded", snapshot.qboxLoaded());
        boot.put("aetherActive", snapshot.aetherActive());
        boot.put("braidActive", snapshot.braidActive());
        boot.put("gravityActive", snapshot.gravityActive());
        boot.put("fusionActive", snapshot.fusionActive());
        boot.put("recentEventCount", snapshot.recentEvents().size());
        boot.put("aiRun", aiRunLane);
        boot.put("continuityLoaded", continuity.loaded());
        boot.put("continuityRules", continuity.resumeRules().size());
        boot.put("visibleMenuHeads", continuity.visibleMenuHeads().size());

        Map<String, Object> memory = new LinkedHashMap<>();
        memory.put("usedMb", usedMb);
        memory.put("freeMb", freeMb);
        memory.put("maxMb", maxMb);
        memory.put("conceptSpace", snapshot.conceptSpace());
        memory.put("recentEvents", snapshot.recentEvents().size());
        memory.put("continuityMemoryPath", displayPath(continuity.continuityMemoryPath()));

        Map<String, Object> ollama = new LinkedHashMap<>();
        ollama.put("model", "unknown");
        ollama.put("chatAvailable", false);
        ollama.put("status", "unknown");
        ollama.put("lane", "ultimate-shell");

        Map<String, Object> cores = new LinkedHashMap<>();
        cores.put("logicalCpuCores", runtime.availableProcessors());
        cores.put("subsystems", registry.all().size());
        cores.put("agiStrata", registry.agiStrataCounts().size());
        cores.put("commandSurfaces", commandCatalogs.size());
        cores.put("cataloguedCommands", commandCount());
        cores.put("livingProofLanes", operatorManifest.livingProofLaneCount());
        cores.put("runtimeLanes", ledger.runtimeCount());
        cores.put("models", ledger.modelCount());
        cores.put("aiRunBusy", aiRunBusy);
        cores.put("aiRunQueueDepth", aiRunQueueDepth);
        cores.put("continuityHeads", continuity.visibleMenuHeads().size());
        cores.put("deterministicLanes", continuity.deterministicLaneSummaries().size());

        List<Map<String, Object>> subsystemHealth = new ArrayList<>();
        subsystemHealth.add(healthNode("immortalKernel", "healthy", 1.0d, "snapshot_ready"));
        subsystemHealth.add(healthNode("nexusOrganism", stateStatus(snapshot.nexusState()), scoreForState(snapshot.nexusState()), snapshot.nexusState()));
        subsystemHealth.add(healthNode("aiRunRouter", aiRunBusy ? "degraded" : "healthy", aiRunBusy ? 0.65d : 1.0d, aiRunDetail));
        subsystemHealth.add(healthNode("continuityMemory", continuity.loaded() ? "healthy" : "degraded", continuity.loaded() ? 1.0d : 0.35d,
            "rules=" + continuity.resumeRules().size() + ",heads=" + continuity.visibleMenuHeads().size()));
        subsystemHealth.add(healthNode("aetherBridge", snapshot.aetherActive() ? "healthy" : "idle", snapshot.aetherActive() ? 1.0d : 0.55d, snapshot.aetherActive() ? "active" : "inactive"));
        subsystemHealth.add(healthNode("braidBridge", snapshot.braidActive() ? "healthy" : "idle", snapshot.braidActive() ? 1.0d : 0.55d, snapshot.braidActive() ? "active" : "inactive"));
        subsystemHealth.add(healthNode("substrateLedger", ledger.modelCount() > 0 || ledger.runtimeCount() > 0 ? "healthy" : "degraded",
            ledger.modelCount() > 0 || ledger.runtimeCount() > 0 ? 0.9d : 0.5d,
            "models=" + ledger.modelCount() + ",runtimeEntries=" + ledger.runtimeCount()));

        Map<String, Object> provenance = new LinkedHashMap<>();
        provenance.put("route", SystemStateContract.ROUTE_SYSTEM_STATE);
        provenance.put("source", "UltimateSystem.ai");
        provenance.put("truthSource", "UltimateImmortalKernel.snapshot + JVM runtime + substrate ledger + aiRun lock telemetry");
        provenance.put("wrappersBypassed", true);
        provenance.put("metaSynthesisBypassed", true);
        provenance.put("machineCheckable", true);
        provenance.put("continuityLoaded", continuity.loaded());

        return SystemStateContract.createEnvelope(boot, memory, ollama, cores, subsystemHealth, provenance);
    }

    private static Map<String, Object> healthNode(String name, String status, double score, String detail) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("name", name);
        node.put("status", status);
        node.put("score", Math.round(score * 1000.0d) / 1000.0d);
        node.put("detail", detail);
        return node;
    }

    private static String stateStatus(String state) {
        if (state == null || state.isBlank() || "offline".equalsIgnoreCase(state)) {
            return "offline";
        }
        if ("conscious".equalsIgnoreCase(state) || "awake".equalsIgnoreCase(state)) {
            return "healthy";
        }
        return "degraded";
    }

    private static double scoreForState(String state) {
        if (state == null || state.isBlank() || "offline".equalsIgnoreCase(state)) {
            return 0.0d;
        }
        if ("conscious".equalsIgnoreCase(state) || "awake".equalsIgnoreCase(state)) {
            return 1.0d;
        }
        return 0.65d;
    }

    private String groundedLivingProofLaneReply(String prompt) {
        if (!isLivingProofLaneQuery(prompt)) {
            return null;
        }

        List<UltimateOperatorManifest.LivingProofLane> ranked = new ArrayList<>(operatorManifest.livingProofLanes());
        if (ranked.isEmpty()) {
            return "Grounded operator answer from runtime manifest: no living proof lanes are currently registered.";
        }
        ranked.sort(Comparator
            .comparingInt((UltimateOperatorManifest.LivingProofLane lane) -> livingProofLaneStrengthScore(lane))
            .reversed()
            .thenComparing(UltimateOperatorManifest.LivingProofLane::title));

        UltimateImmortalKernel.KernelSnapshot snapshot = immortalKernel.snapshot();
        StringBuilder sb = new StringBuilder();
        sb.append("Grounded operator answer from runtime manifest (no speculative biology lanes).\n\n");
        sb.append("Strongest living proof lanes active in this shell right now:\n");
        int limit = Math.min(3, ranked.size());
        for (int i = 0; i < limit; i++) {
            UltimateOperatorManifest.LivingProofLane lane = ranked.get(i);
            sb.append(i + 1).append(". ").append(lane.title()).append(" (").append(lane.id()).append(")\n");
            sb.append("   role: ").append(lane.operatorRole()).append("\n");
            sb.append("   strata: ").append(String.join(", ", lane.agiStrata())).append("\n");
            sb.append("   evidence: sections=").append(lane.sections().size())
                .append(", focusCommands=").append(lane.focusCommands().size())
                .append(", evidencePaths=").append(lane.evidencePaths().size()).append("\n");
            if (!lane.focusCommands().isEmpty()) {
                sb.append("   next command: ").append(lane.focusCommands().get(0)).append("\n");
            }
        }
        sb.append("\nRuntime state now: nexusState=").append(snapshot.nexusState())
            .append(", aetherActive=").append(snapshot.aetherActive())
            .append(", braidActive=").append(snapshot.braidActive())
            .append(", gravityActive=").append(snapshot.gravityActive())
            .append(", fusionActive=").append(snapshot.fusionActive()).append("\n");
        sb.append("Next action: commands native bridge-native, then launch native bridge-native.");
        return sb.toString().trim();
    }

    private boolean isLivingProofLaneQuery(String prompt) {
        String lowered = prompt == null ? "" : prompt.toLowerCase(Locale.ROOT);
        if (!(lowered.contains("proof lane") || lowered.contains("living proof"))) {
            return false;
        }
        return lowered.contains("strong")
            || lowered.contains("active")
            || lowered.contains("right now")
            || lowered.contains("which")
            || lowered.contains("what");
    }

    private int livingProofLaneStrengthScore(UltimateOperatorManifest.LivingProofLane lane) {
        int score = 0;
        score += lane.sections().size() * 3;
        score += lane.focusCommands().size() * 2;
        score += lane.evidencePaths().size();
        if (lane.proofLane()) {
            score += 4;
        }
        if (lane.kernelDiscipline()) {
            score += 3;
        }
        if (lane.organismCore()) {
            score += 2;
        }
        if (lane.governingCore()) {
            score += 1;
        }
        return score;
    }

    private String commandSurface(String remainder) {
        if (remainder == null || remainder.isBlank()) {
            return renderCommandSurfaceSummary();
        }

        Optional<UltimateCommandCatalog> catalogMatch = findCatalog(remainder);
        if (catalogMatch.isPresent()) {
            return catalogMatch.get().renderSummary();
        }

        List<CatalogSectionMatch> matches = findSectionMatches(remainder);
        if (matches.isEmpty()) {
            return "Usage: commands <surface> [section]\nTry: commands";
        }
        if (matches.size() > 1) {
            return renderAmbiguousSectionMatches(remainder, matches);
        }
        CatalogSectionMatch match = matches.get(0);
        return match.catalog().renderSection(match.section().id()) + "\n" + renderSectionRouting(match);
    }

    private String handleMath(String remainder) {
        if (remainder.isBlank()) {
            return "Usage: math <prime|factor|tunnel|hash|rsa> ...";
        }

        String[] parts = remainder.split("\\s+", 2);
        String action = parts[0].toLowerCase(Locale.ROOT);
        String arg = parts.length > 1 ? parts[1].trim() : "";

        return switch (action) {
            case "prime" -> experimentFacade.primeTest(arg);
            case "factor" -> experimentFacade.factor(arg);
            case "tunnel" -> experimentFacade.tunnel(arg);
            case "hash" -> experimentFacade.hash(arg);
            case "rsa" -> experimentFacade.rsaChallenge(arg);
            default -> "Usage: math <prime|factor|tunnel|hash|rsa> ...";
        };
    }

    private String handleTpu(String remainder) {
        if (remainder == null || remainder.isBlank()) {
            return "Usage: tpu <status|infer|reason|synapse|selfcreate|reset> ...";
        }

        String[] parts = remainder.trim().split("\\s+", 3);
        String action = parts[0].toLowerCase(Locale.ROOT);
        return switch (action) {
            case "status" -> tpuReasoningEngine.status();
            case "infer" -> tpuReasoningEngine.infer(parts.length > 1 ? remainder.substring("infer".length()).trim() : "");
            case "reason" -> {
                int depth = 3;
                String payload;
                if (parts.length >= 3 && parts[1].matches("^\\d+$")) {
                    depth = parseInt(parts[1], 3);
                    payload = parts[2];
                } else {
                    payload = remainder.substring("reason".length()).trim();
                }
                yield tpuReasoningEngine.reason(payload, depth);
            }
            case "synapse" -> {
                if (parts.length < 2) {
                    yield "Usage: tpu synapse <count>";
                }
                try {
                    long count = Long.parseLong(parts[1]);
                    yield tpuReasoningEngine.addSynapses(count);
                } catch (NumberFormatException e) {
                    yield "Usage: tpu synapse <count>";
                }
            }
            case "selfcreate" -> tpuReasoningEngine.selfCreate(parts.length > 1 ? remainder.substring("selfcreate".length()).trim() : "");
            case "reset" -> tpuReasoningEngine.reset();
            default -> "Usage: tpu <status|infer|reason|synapse|selfcreate|reset> ...";
        };
    }

    private String handleNode(String remainder) {
        if (remainder.isBlank()) {
            return "Usage: node <spawn|spiral|list|tick|simulate|think|reproduce|code|status> ...";
        }

        String[] parts = remainder.split("\\s+");
        String action = parts[0].toLowerCase(Locale.ROOT);

        return switch (action) {
            case "spawn" -> nodeSpawn(parts);
            case "spiral" -> {
                if (parts.length < 2) {
                    yield "Usage: node spiral <name>";
                }
                yield "Spawned: " + phiNodeFacade.spawnOnSpiral(parts[1]);
            }
            case "list" -> phiNodeFacade.listNodes();
            case "tick" -> {
                float dt = parts.length > 1 ? parseFloat(parts[1], 0.1f) : 0.1f;
                yield phiNodeFacade.tick(dt);
            }
            case "simulate" -> {
                int ticks = parts.length > 1 ? parseInt(parts[1], 10) : 10;
                yield phiNodeFacade.simulate(ticks);
            }
            case "think" -> nodeThink(remainder);
            case "reproduce" -> {
                if (parts.length < 4) {
                    yield "Usage: node reproduce <parentA> <parentB> <childName>";
                }
                yield phiNodeFacade.reproduce(parts[1], parts[2], parts[3]);
            }
            case "code" -> {
                if (parts.length < 2) {
                    yield "Usage: node code <name>";
                }
                yield phiNodeFacade.toCode(parts[1]);
            }
            case "status" -> phiNodeFacade.getStatus();
            default -> "Usage: node <spawn|spiral|list|tick|simulate|think|reproduce|code|status> ...";
        };
    }

    private String nodeSpawn(String[] parts) {
        if (parts.length < 2) {
            return "Usage: node spawn <name> [x y]";
        }
        if (parts.length >= 4) {
            return "Spawned: " + phiNodeFacade.spawn(parts[1], parseFloat(parts[2], 0f), parseFloat(parts[3], 0f));
        }
        return "Spawned: " + phiNodeFacade.spawnOnSpiral(parts[1]);
    }

    private String nodeThink(String remainder) {
        String[] pieces = remainder.split("\\s+", 3);
        if (pieces.length < 3) {
            return "Usage: node think <name> <1,0,1,...>";
        }

        int[] inputs = Arrays.stream(pieces[2].split("[,\\s]+"))
            .filter(token -> !token.isBlank())
            .mapToInt(token -> parseInt(token, 0))
            .toArray();
        return phiNodeFacade.think(pieces[1], inputs);
    }

    private String handleCode(String remainder) {
        if (remainder.isBlank()) {
            return "Usage: code <transform|mutate|validate|inject|store|fingerprint|status> ...";
        }

        String[] parts = remainder.split("\\s+", 2);
        String action = parts[0].toLowerCase(Locale.ROOT);
        String payload = parts.length > 1 ? parts[1].trim() : "";

        return switch (action) {
            case "transform" -> selfCodeEngine.phiTransform(payload);
            case "mutate" -> selfCodeEngine.mutate(payload);
            case "validate" -> selfCodeEngine.validate(payload);
            case "inject" -> {
                selfCodeEngine.inject(payload);
                yield "Injected code fingerprint: " + selfCodeEngine.linkConcept(payload);
            }
            case "store" -> storeCode(payload);
            case "fingerprint" -> selfCodeEngine.linkConcept(payload);
            case "status" -> selfCodeEngine.getStatus();
            default -> "Usage: code <transform|mutate|validate|inject|store|fingerprint|status> ...";
        };
    }

    private String handleSelfBuild(String remainder) {
        if (remainder == null || remainder.isBlank()) {
            return "Usage: selfbuild <goal>\n   or: selfbuild file <path-fragment> :: <goal>\n   or: selfbuild files <path-fragment> :: <goal>\n   or: selfbuild promote file <path-fragment> :: <goal>\n   or: selfbuild promote files <path-fragment> :: <goal>\n   or: selfbuild prove-promote file <path-fragment> :: <goal>\n   or: selfbuild prove-promote files <path-fragment> :: <goal>\n   or: selfbuild review-promote file <path-fragment> :: <goal>\n   or: selfbuild reviews\n   or: selfbuild approve <review-id>\n   or: selfbuild reject <review-id> [:: note]";
        }
        if (remainder.startsWith("reviews")) {
            List<UltimateSelfBuildHarness.ReviewRequest> reviews = selfBuildHarness.listReviewRequests();
            if (reviews.isEmpty()) {
                return "SELFBUILD REVIEWS\nNo review requests are waiting.";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("SELFBUILD REVIEWS\n");
            reviews.forEach(review -> sb.append("Review: ").append(review.reviewId()).append("\n")
                .append("Status: ").append(review.status()).append("\n")
                .append("Target: ").append(review.targetLabel()).append("\n")
                .append("Path: ").append(displayPath(review.targetPath())).append("\n")
                .append("Patch: ").append(review.patchPath() == null ? "(none)" : displayPath(review.patchPath())).append("\n")
                .append("Proof report: ").append(displayPath(review.provePromotionReportPath())).append("\n")
                .append("Note: ").append(review.note()).append("\n\n"));
            return sb.toString().trim();
        }
        if (remainder.startsWith("approve ")) {
            String reviewId = remainder.substring("approve ".length()).trim();
            if (reviewId.isBlank()) {
                return "Usage: selfbuild approve <review-id>";
            }
            return selfBuildHarness.approveReview(reviewId).summary();
        }
        if (remainder.startsWith("reject ")) {
            String payload = remainder.substring("reject ".length()).trim();
            String[] parts = payload.split("\\s*::\\s*", 2);
            String reviewId = parts[0].trim();
            String reason = parts.length > 1 ? parts[1].trim() : "";
            if (reviewId.isBlank()) {
                return "Usage: selfbuild reject <review-id> [:: note]";
            }
            return selfBuildHarness.rejectReview(reviewId, reason).summary();
        }
        if (remainder.startsWith("review-promote ")) {
            String reviewRemainder = remainder.substring("review-promote ".length()).trim();
            if (reviewRemainder.startsWith("file ")) {
                String payload = reviewRemainder.substring("file ".length()).trim();
                String[] parts = payload.split("\\s*::\\s*", 2);
                String pathFragment = parts[0].trim();
                String goal = parts.length > 1 && !parts[1].isBlank()
                    ? parts[1].trim()
                    : "prove and stage a human-reviewed source improvement under Fraynix selfbuild loop";
                return knowledgeIndex.findByPathFragment(pathFragment)
                    .map(document -> selfBuildHarness.reviewPromoteAgainstSource(
                        document.path(),
                        goal,
                        knowledgeIndex.relativePath(document.path())
                    ))
                    .map(review -> """
                        SELFBUILD REVIEW-PROMOTION READY
                        Review: %s
                        Status: %s
                        Target: %s
                        Patch: %s
                        Proof report: %s
                        Note: %s
                        Next: selfbuild approve %s  |  selfbuild reject %s :: <note>
                        """.formatted(
                            review.reviewId(),
                            review.status(),
                            review.targetLabel(),
                            review.patchPath() == null ? "(none)" : review.patchPath().toString().replace('\\', '/'),
                            review.provePromotionReportPath().toString().replace('\\', '/'),
                            review.note(),
                            review.reviewId(),
                            review.reviewId()
                        ).stripIndent().trim())
                    .orElse("No indexed file matched for selfbuild review-promotion target: " + pathFragment);
            }
            return "Usage: selfbuild review-promote file <path-fragment> :: <goal>";
        }
        if (remainder.startsWith("prove-promote ")) {
            String proveRemainder = remainder.substring("prove-promote ".length()).trim();
            if (proveRemainder.startsWith("files ")) {
                String payload = proveRemainder.substring("files ".length()).trim();
                String[] parts = payload.split("\\s*::\\s*", 2);
                String pathFragment = parts[0].trim();
                String goal = parts.length > 1 && !parts[1].isBlank()
                    ? parts[1].trim()
                    : "prove and promote best target source improvements under Fraynix selfbuild loop";
                List<UltimateKnowledgeIndex.IndexedDocument> matches = knowledgeIndex.findTopByPathFragment(pathFragment, 4);
                if (matches.isEmpty()) {
                    return "No indexed files matched for selfbuild prove-promotion target set: " + pathFragment;
                }
                List<UltimateSelfBuildHarness.SourceTarget> targets = matches.stream()
                    .map(document -> new UltimateSelfBuildHarness.SourceTarget(
                        document.path(),
                        knowledgeIndex.relativePath(document.path())
                    ))
                    .toList();
                return selfBuildHarness.provePromoteAgainstSourceSet(targets, goal, pathFragment).summary();
            }
            if (proveRemainder.startsWith("file ")) {
                String payload = proveRemainder.substring("file ".length()).trim();
                String[] parts = payload.split("\\s*::\\s*", 2);
                String pathFragment = parts[0].trim();
                String goal = parts.length > 1 && !parts[1].isBlank()
                    ? parts[1].trim()
                    : "prove and promote judged improvements for target source under Fraynix selfbuild loop";
                return knowledgeIndex.findByPathFragment(pathFragment)
                    .map(document -> selfBuildHarness.provePromoteAgainstSource(
                        document.path(),
                        goal,
                        knowledgeIndex.relativePath(document.path())
                    ).summary())
                    .orElse("No indexed file matched for selfbuild prove-promotion target: " + pathFragment);
            }
            return "Usage: selfbuild prove-promote file <path-fragment> :: <goal>\n   or: selfbuild prove-promote files <path-fragment> :: <goal>";
        }
        if (remainder.startsWith("promote ")) {
            String promoteRemainder = remainder.substring("promote ".length()).trim();
            if (promoteRemainder.startsWith("files ")) {
                String payload = promoteRemainder.substring("files ".length()).trim();
                String[] parts = payload.split("\\s*::\\s*", 2);
                String pathFragment = parts[0].trim();
                String goal = parts.length > 1 && !parts[1].isBlank()
                    ? parts[1].trim()
                    : "promote best target source improvements under Fraynix selfbuild loop";
                List<UltimateKnowledgeIndex.IndexedDocument> matches = knowledgeIndex.findTopByPathFragment(pathFragment, 4);
                if (matches.isEmpty()) {
                    return "No indexed files matched for selfbuild promotion target set: " + pathFragment;
                }
                List<UltimateSelfBuildHarness.SourceTarget> targets = matches.stream()
                    .map(document -> new UltimateSelfBuildHarness.SourceTarget(
                        document.path(),
                        knowledgeIndex.relativePath(document.path())
                    ))
                    .toList();
                return selfBuildHarness.promoteAgainstSourceSet(targets, goal, pathFragment).summary();
            }
            if (promoteRemainder.startsWith("file ")) {
                String payload = promoteRemainder.substring("file ".length()).trim();
                String[] parts = payload.split("\\s*::\\s*", 2);
                String pathFragment = parts[0].trim();
                String goal = parts.length > 1 && !parts[1].isBlank()
                    ? parts[1].trim()
                    : "promote judged improvements for target source under Fraynix selfbuild loop";
                return knowledgeIndex.findByPathFragment(pathFragment)
                    .map(document -> selfBuildHarness.promoteAgainstSource(
                        document.path(),
                        goal,
                        knowledgeIndex.relativePath(document.path())
                    ).summary())
                    .orElse("No indexed file matched for selfbuild promotion target: " + pathFragment);
            }
            return "Usage: selfbuild promote file <path-fragment> :: <goal>\n   or: selfbuild promote files <path-fragment> :: <goal>";
        }
        if (remainder.startsWith("files ")) {
            String payload = remainder.substring("files ".length()).trim();
            String[] parts = payload.split("\\s*::\\s*", 2);
            String pathFragment = parts[0].trim();
            String goal = parts.length > 1 && !parts[1].isBlank()
                ? parts[1].trim()
                : "compare target source set under Fraynix selfbuild loop";
            List<UltimateKnowledgeIndex.IndexedDocument> matches = knowledgeIndex.findTopByPathFragment(pathFragment, 4);
            if (matches.isEmpty()) {
                return "No indexed files matched for selfbuild target set: " + pathFragment;
            }
            List<UltimateSelfBuildHarness.SourceTarget> targets = matches.stream()
                .map(document -> new UltimateSelfBuildHarness.SourceTarget(
                    document.path(),
                    knowledgeIndex.relativePath(document.path())
                ))
                .toList();
            return selfBuildHarness.runAgainstSourceSet(targets, goal, pathFragment).summary();
        }
        if (remainder.startsWith("file ")) {
            String payload = remainder.substring("file ".length()).trim();
            String[] parts = payload.split("\\s*::\\s*", 2);
            String pathFragment = parts[0].trim();
            String goal = parts.length > 1 && !parts[1].isBlank()
                ? parts[1].trim()
                : "stabilize target source under Fraynix selfbuild loop";
            return knowledgeIndex.findByPathFragment(pathFragment)
                .map(document -> selfBuildHarness.runAgainstSource(
                    document.path(),
                    goal,
                    knowledgeIndex.relativePath(document.path())
                ).summary())
                .orElse("No indexed file matched for selfbuild target: " + pathFragment);
        }
        UltimateSelfBuildHarness.SelfBuildResult result = selfBuildHarness.run(remainder);
        return result.summary();
    }

    private String handleWillow(String remainder) {
        AeonWillowCrusher willow = AeonWillowCrusher.getInstance();
        String command = remainder == null ? "" : remainder.trim();
        try {
            willow.boot();
            if (command.isBlank() || "status".equalsIgnoreCase(command)) {
                return willow.getStatus();
            }
            if (command.startsWith("hadamard ")) {
                int n = Integer.parseInt(command.substring("hadamard ".length()).trim());
                double latency = willow.hadamard(n);
                return """
                    WILLOW HADAMARD COMPLETE
                    Qubits: %d
                    LatencyMs: %.4f
                    ActiveQubits: %d
                    """.formatted(n, latency, willow.getActiveQubitCount()).stripIndent().trim();
            }
            if (command.startsWith("entangle ")) {
                String[] qubits = command.substring("entangle ".length()).trim().split("\\s+");
                if (qubits.length != 2) {
                    return "Usage: willow entangle <q1> <q2>";
                }
                int q1 = Integer.parseInt(qubits[0]);
                int q2 = Integer.parseInt(qubits[1]);
                double latency = willow.entangle(q1, q2);
                if (latency < 0) {
                    return "WILLOW ENTANGLE BLOCKED\nBoth qubits must already be active in superposition.";
                }
                return """
                    WILLOW ENTANGLE COMPLETE
                    Qubits: %d, %d
                    BeatFrequencyHz: %.4f
                    LatencyMs: %.4f
                    """.formatted(q1, q2, willow.getBeatFrequency(q1, q2), latency).stripIndent().trim();
            }
            if ("echo".equalsIgnoreCase(command)) {
                double latency = willow.echo();
                return """
                    WILLOW ECHO COMPLETE
                    EchoShift: %d
                    LatencyMs: %.4f
                    """.formatted(willow.getEchoShift(), latency).stripIndent().trim();
            }
            if (command.startsWith("measure ")) {
                int q = Integer.parseInt(command.substring("measure ".length()).trim());
                String[] result = willow.measure(q);
                if (result == null) {
                    return "WILLOW MEASURE BLOCKED\nQubit %d is inactive or out of bounds.".formatted(q);
                }
                return """
                    WILLOW MEASURE COMPLETE
                    Qubit: %d
                    Spin: |%s>
                    Magnitude: %s
                    LatencyMs: %s
                    FrequencyHz: %s
                    """.formatted(q, result[0], result[1], result[2], result[3]).stripIndent().trim();
            }
            if ("spectrum".equalsIgnoreCase(command)) {
                long[] spectrum = willow.getSpectrum();
                return """
                    WILLOW SPECTRUM
                    ActiveQubits: %d
                    TotalEnergy: %d
                    BufferSizeMB: %d
                    """.formatted(spectrum[0], spectrum[1], spectrum[2]).stripIndent().trim();
            }
            if (command.startsWith("proof")) {
                String payload = command.substring("proof".length()).trim();
                int qubits = payload.isBlank() ? 8 : Integer.parseInt(payload);
                double hadamardLatency = willow.hadamard(qubits);
                double entanglementLatency = qubits >= 2 ? willow.entangle(0, 1) : -1;
                double echoLatency = willow.echo();
                String[] result = willow.measure(0);
                long[] spectrum = willow.getSpectrum();
                return """
                    WILLOW PROOF COMPLETE
                    QubitsDriven: %d
                    HadamardMs: %.4f
                    EntangleMs: %.4f
                    EchoMs: %.4f
                    Spin: %s
                    Magnitude: %s
                    ActiveQubits: %d
                    TotalEnergy: %d
                    """.formatted(
                    qubits,
                    hadamardLatency,
                    entanglementLatency,
                    echoLatency,
                    result == null ? "(none)" : "|" + result[0] + ">",
                    result == null ? "(none)" : result[1],
                    spectrum[0],
                    spectrum[1]
                ).stripIndent().trim();
            }
        } catch (Exception e) {
            return "WILLOW FAULT\n" + e.getMessage();
        }
        return "Usage: willow <status|hadamard <n>|entangle <q1> <q2>|echo|measure <q>|spectrum|proof [n]>";
    }

    private String handleTransmudder(String remainder) {
        String command = remainder == null ? "" : remainder.trim();
        if (command.isBlank() || "stats".equalsIgnoreCase(command)) {
            return transmudderStats();
        }
        if ("purge".equalsIgnoreCase(command)) {
            transmudder.purge();
            return "TRANSMUDDER PURGED\n" + transmudderStats();
        }
        if ("essence".equalsIgnoreCase(command)) {
            String essence = transmudder.getEssence();
            String excerpt = essence.isBlank()
                ? "(empty)"
                : essence.substring(0, Math.min(400, essence.length()));
            return """
                TRANSMUDDER ESSENCE
                ContextSize: %d
                Excerpt:
                %s
                """.formatted(transmudder.getContextSize(), excerpt).stripIndent().trim();
        }
        if (command.startsWith("file ") || command.startsWith("proof ")) {
            boolean proofMode = command.startsWith("proof ");
            String payload = command.substring(proofMode ? "proof ".length() : "file ".length()).trim();
            if (payload.isBlank()) {
                return proofMode ? "Usage: transmudder proof <path>" : "Usage: transmudder file <path>";
            }
            Path target = Path.of(stripQuotes(payload)).toAbsolutePath().normalize();
            if (!Files.exists(target)) {
                return "TRANSMUDDER TARGET MISSING\n" + target.toString().replace('\\', '/');
            }
            try {
                return transmudderProofForPath(target, proofMode);
            } catch (Exception e) {
                return "TRANSMUDDER FAULT\n" + e.getMessage();
            }
        }
        return "Usage: transmudder <file <path>|proof <path>|stats|essence|purge>";
    }

    private String transmudderProofForPath(Path target, boolean proofMode) throws Exception {
        String raw = transmudder.transmuteToText(target.toString());
        String clean = transmudder.cleanse(raw);
        transmudder.transmuteFile(target.toString());
        String excerpt = clean.isBlank()
            ? "(empty)"
            : clean.substring(0, Math.min(proofMode ? 500 : 220, clean.length()));
        return """
            TRANSMUDDER %s COMPLETE
            Target: %s
            RawChars: %d
            CleanChars: %d
            ContextSize: %d
            AbsorbedFiles: %d
            Excerpt:
            %s
            """.formatted(
            proofMode ? "PROOF" : "INGEST",
            target.toString().replace('\\', '/'),
            raw.length(),
            clean.length(),
            transmudder.getContextSize(),
            transmudder.getFileCount(),
            excerpt
        ).stripIndent().trim();
    }

    private String transmudderStats() {
        return """
            TRANSMUDDER STATS
            FilesAbsorbed: %d
            ContextSize: %d
            """.formatted(transmudder.getFileCount(), transmudder.getContextSize()).stripIndent().trim();
    }

    private String storeCode(String payload) {
        if (payload.isBlank()) {
            return "Usage: code store <tags> :: <content>";
        }
        int divider = payload.indexOf("::");
        String tags = divider >= 0 ? payload.substring(0, divider).trim() : "";
        String content = divider >= 0 ? payload.substring(divider + 2).trim() : payload;
        selfCodeEngine.store(content, tags);
        return "Stored code fingerprint: " + selfCodeEngine.linkConcept(content);
    }

    private String route(String subsystemId) {
        if (subsystemId == null || subsystemId.isBlank()) {
            return "Usage: route <system-id|section> [target-subsystem]";
        }
        Optional<UltimateSubsystem> match = registry.find(subsystemId);
        if (match.isPresent()) {
            return renderSubsystemRoute(match.get());
        }

        SectionTargetRequest request = parseSectionTargetRequest(subsystemId);
        List<CatalogSectionMatch> sectionMatches = findSectionMatches(request.sectionQuery());
        if (sectionMatches.size() > 1) {
            return renderAmbiguousSectionMatches(request.sectionQuery(), sectionMatches);
        }
        if (!sectionMatches.isEmpty()) {
            CatalogSectionMatch sectionMatch = sectionMatches.get(0);
            if (request.targetSubsystemId() != null && !request.targetSubsystemId().isBlank()) {
                return renderSectionTargetRoute(sectionMatch, request.targetSubsystemId());
            }
            return renderSectionRoute(sectionMatch);
        }

        return "Unknown subsystem or section: " + subsystemId + "\nTry: systems or commands";
    }

    private String renderSubsystemRoute(UltimateSubsystem subsystem) {
        StringBuilder sb = new StringBuilder();
        sb.append("ROUTE: ").append(subsystem.displayName()).append("\n");
        sb.append("Role: ").append(subsystem.designRole()).append("\n");
        sb.append("Path: ").append(subsystem.primaryPath().toString().replace('\\', '/')).append("\n");
        sb.append("Launch: ").append(subsystem.runnable() ? subsystem.launchCommandText() : "(manual or library-only)");
        return sb.toString();
    }

    private String launch(String subsystemId) {
        if (subsystemId == null || subsystemId.isBlank()) {
            return "Usage: launch <system-id|section> [target-subsystem]";
        }
        Optional<UltimateSubsystem> match = registry.find(subsystemId);
        if (match.isPresent()) {
            return launchSubsystem(match.get(), match.get().id());
        }

        SectionTargetRequest request = parseSectionTargetRequest(subsystemId);
        List<CatalogSectionMatch> sectionMatches = findSectionMatches(request.sectionQuery());
        if (sectionMatches.size() > 1) {
            return renderAmbiguousSectionMatches(request.sectionQuery(), sectionMatches);
        }
        if (sectionMatches.isEmpty()) {
            return "Unknown subsystem or section: " + subsystemId + "\nTry: systems or commands";
        }

        CatalogSectionMatch sectionMatch = sectionMatches.get(0);
        if (request.targetSubsystemId() != null && !request.targetSubsystemId().isBlank()) {
            return launchSectionTarget(sectionMatch, request.targetSubsystemId());
        }

        List<UltimateSubsystem> runnableTargets = sectionMatch.catalog().linkedSubsystemIds(sectionMatch.section()).stream()
            .map(registry::find)
            .flatMap(Optional::stream)
            .filter(UltimateSubsystem::runnable)
            .toList();

        if (sectionMatch.catalog().linkedSubsystemIds(sectionMatch.section()).isEmpty()) {
            return renderDirectExecutionSection(sectionMatch);
        }

        if (runnableTargets.isEmpty()) {
            return "No runnable subsystem is mapped for section: " + sectionMatch.section().title() + "\n" + renderSectionRoute(sectionMatch);
        }

        if (runnableTargets.size() == 1) {
            return launchSubsystem(
                runnableTargets.get(0),
                "section " + sectionMatch.catalog().surfaceId() + " " + sectionMatch.section().id()
                    + " (" + sectionMatch.section().title() + ")"
            );
        }

        return renderLaunchChoices(sectionMatch, runnableTargets);
    }

    private String launchSubsystem(UltimateSubsystem subsystem, String sourceLabel) {
        if (!subsystem.runnable()) {
            return "Subsystem is not wired for direct launch.\n" + renderSubsystemRoute(subsystem);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(subsystem.launchCommand());
        processBuilder.directory(subsystem.workingDirectory().toFile());
        processBuilder.inheritIO();

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return "Launch finished for " + sourceLabel + " -> " + subsystem.id() + " with exit code " + exitCode + ".";
        } catch (IOException e) {
            return "Launch failed for " + sourceLabel + " -> " + subsystem.id() + ": " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Launch interrupted for " + sourceLabel + " -> " + subsystem.id() + ".";
        }
    }

    private String renderSectionRouting(CatalogSectionMatch match) {
        StringBuilder sb = new StringBuilder();
        sb.append("Surface: ").append(match.catalog().surfaceTitle()).append("\n");
        if (match.catalog().linkedSubsystemIds(match.section()).isEmpty()) {
            sb.append("Execution: direct in-process inside UltimateSystem\n");
            sb.append("Use the listed commands directly from the ULTIMATE prompt.");
            return sb.toString().trim();
        }
        sb.append("Routes:\n");
        for (String subsystemId : match.catalog().linkedSubsystemIds(match.section())) {
            Optional<UltimateSubsystem> linked = registry.find(subsystemId);
            if (linked.isEmpty()) {
                continue;
            }
            sb.append("  - ")
                .append(linked.get().id())
                .append(" :: ")
                .append(linked.get().displayName())
                .append(" :: ")
                .append(linked.get().runnable() ? linked.get().launchCommandText() : "(manual or library-only)")
                .append("\n");
        }
        match.catalog().defaultSubsystemId(match.section())
            .ifPresent(defaultId -> sb.append("Default route: ").append(defaultId).append("\n"));
        sb.append("Use: route ").append(match.catalog().surfaceId()).append(" ").append(match.section().id())
            .append(" | launch ").append(match.catalog().surfaceId()).append(" ").append(match.section().id())
            .append(" | launch ").append(match.catalog().surfaceId()).append(" ").append(match.section().id()).append(" <target>");
        return sb.toString().trim();
    }

    private String renderSectionRoute(CatalogSectionMatch match) {
        StringBuilder sb = new StringBuilder();
        sb.append("ROUTE GROUP: ").append(match.section().title()).append("\n");
        sb.append("Theme: ").append(match.section().theme()).append("\n");
        sb.append(renderSectionRouting(match));
        return sb.toString();
    }

    private String renderDirectExecutionSection(CatalogSectionMatch match) {
        StringBuilder sb = new StringBuilder();
        sb.append("DIRECT EXECUTION SECTION\n");
        sb.append("Surface: ").append(match.catalog().surfaceTitle()).append("\n");
        sb.append("Section: ").append(match.section().title()).append("\n");
        sb.append("Commands:\n");
        for (UltimateCommandCatalog.CommandEntry entry : match.section().commands()) {
            sb.append("  - ").append(entry.syntax()).append(" :: ").append(entry.description()).append("\n");
        }
        sb.append("Execution: direct in-process inside UltimateSystem.");
        return sb.toString().trim();
    }

    private String renderSectionTargetRoute(CatalogSectionMatch match, String targetId) {
        Optional<UltimateSubsystem> target = resolveLinkedSubsystem(match, targetId);
        if (target.isEmpty()) {
            return "Section " + match.section().title() + " is not linked to subsystem: " + targetId + "\n" + renderSectionRoute(match);
        }

        return "ROUTE GROUP: " + match.section().title() + "\n"
            + "Surface: " + match.catalog().surfaceTitle() + "\n"
            + "Chosen target: " + target.get().id() + "\n"
            + renderSubsystemRoute(target.get());
    }

    private String launchSectionTarget(CatalogSectionMatch match, String targetId) {
        Optional<UltimateSubsystem> target = resolveLinkedSubsystem(match, targetId);
        if (target.isEmpty()) {
            return "Section " + match.section().title() + " is not linked to subsystem: " + targetId + "\n" + renderSectionRoute(match);
        }
        return launchSubsystem(
            target.get(),
            "section " + match.catalog().surfaceId() + " " + match.section().id() + " -> " + target.get().id()
        );
    }

    private Optional<UltimateSubsystem> resolveLinkedSubsystem(CatalogSectionMatch match, String targetId) {
        if (!match.catalog().linkedSubsystemIds(match.section()).stream().anyMatch(id -> id.equalsIgnoreCase(targetId))) {
            return Optional.empty();
        }
        return registry.find(targetId);
    }

    private String renderLaunchChoices(CatalogSectionMatch match, List<UltimateSubsystem> runnableTargets) {
        StringBuilder sb = new StringBuilder();
        sb.append("MULTI-TARGET LAUNCH\n");
        sb.append("Surface: ").append(match.catalog().surfaceTitle()).append("\n");
        sb.append("Section: ").append(match.section().title()).append("\n");
        sb.append("This section maps to multiple runnable subsystems.\n");
        sb.append("Choose one explicitly:\n");
        for (UltimateSubsystem target : runnableTargets) {
            sb.append("  - launch ")
                .append(match.catalog().surfaceId())
                .append(" ")
                .append(match.section().id())
                .append(" ")
                .append(target.id())
                .append(" :: ")
                .append(target.displayName())
                .append("\n");
        }
        match.catalog().defaultSubsystemId(match.section())
            .ifPresent(defaultId -> sb.append("Default route remains: ").append(defaultId).append("\n"));
        return sb.toString().trim();
    }

    private String renderCommandSurfaceSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("COMMAND SURFACES\n");
        sb.append("Surfaces: ").append(commandCatalogs.size())
            .append(" | Sections: ").append(totalSectionCount())
            .append(" | Catalogued commands: ").append(totalCommandCount()).append("\n");
        for (UltimateCommandCatalog catalog : commandCatalogs) {
            sb.append("  - ")
                .append(catalog.surfaceId())
                .append(" :: ")
                .append(catalog.surfaceTitle())
                .append(" (")
                .append(catalog.sectionCount())
                .append(" sections / ")
                .append(catalog.commandCount())
                .append(" commands)\n");
        }
        sb.append("Use: commands fraynix | commands engine | commands native | commands bridge | commands engine exploration | commands native bridge-native");
        return sb.toString().trim();
    }

    private Optional<UltimateCommandCatalog> findCatalog(String query) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }
        String needle = UltimateCommandCatalog.normalize(query);
        return commandCatalogs.stream()
            .filter(catalog -> UltimateCommandCatalog.normalize(catalog.surfaceId()).equals(needle)
                || UltimateCommandCatalog.normalize(catalog.surfaceTitle()).contains(needle))
            .findFirst();
    }

    private List<CatalogSectionMatch> findSectionMatches(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        SurfaceQuery surfaceQuery = parseSurfaceQuery(query);
        if (surfaceQuery.catalog() != null) {
            if (surfaceQuery.sectionQuery() == null || surfaceQuery.sectionQuery().isBlank()) {
                return List.of();
            }
            return surfaceQuery.catalog().findSection(surfaceQuery.sectionQuery())
                .map(section -> List.of(new CatalogSectionMatch(surfaceQuery.catalog(), section)))
                .orElse(List.of());
        }

        return commandCatalogs.stream()
            .map(catalog -> catalog.findSection(query)
                .map(section -> new CatalogSectionMatch(catalog, section)))
            .flatMap(Optional::stream)
            .toList();
    }

    private SurfaceQuery parseSurfaceQuery(String raw) {
        String trimmed = raw.trim();
        String[] tokens = trimmed.split("\\s+", 2);
        Optional<UltimateCommandCatalog> catalog = findCatalog(tokens[0]);
        if (catalog.isPresent()) {
            return new SurfaceQuery(catalog.get(), tokens.length > 1 ? tokens[1].trim() : "");
        }
        return new SurfaceQuery(null, trimmed);
    }

    private Optional<Path> latestLiveProofRecord() {
        try {
            if (!Files.isDirectory(workspaceRoot)) {
                return Optional.empty();
            }
            try (var files = Files.list(workspaceRoot)) {
                return files
                    .filter(path -> path.getFileName() != null)
                    .filter(path -> path.getFileName().toString().startsWith("FRAYNIX_LIVE_EXECUTION_PROOF_"))
                    .filter(path -> path.getFileName().toString().endsWith(".md"))
                    .max(Comparator.comparing(path -> path.getFileName().toString()));
            }
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    private SubstrateLedgerSummary readSubstrateLedgerSummary() {
        Path root = workspaceRoot.resolve(".northstar-kernel").resolve("generated").resolve("substrates");
        Path manifest = root.resolve("MODEL_SUBSTRATE_MANIFEST.tsv");
        Path runtimeInventory = root.resolve("LOCAL_MODEL_RUNTIME_INVENTORY.md");
        int modelCount = 0;
        int licenseCount = 0;
        int reviewRequiredCount = 0;
        List<String> runtimeBackends = new java.util.ArrayList<>();

        if (Files.exists(manifest)) {
            try {
                List<String> lines = Files.readAllLines(manifest);
                if (lines.size() > 1) {
                    String[] headers = lines.get(0).split("\t", -1);
                    int licenseIndex = indexOf(headers, "licenseId");
                    int opsIndex = indexOf(headers, "allowedOperations");
                    modelCount = lines.size() - 1;
                    java.util.Set<String> licenses = new java.util.LinkedHashSet<>();
                    for (int i = 1; i < lines.size(); i++) {
                        String[] values = lines.get(i).split("\t", -1);
                        if (licenseIndex >= 0 && licenseIndex < values.length && !values[licenseIndex].isBlank()) {
                            licenses.add(values[licenseIndex]);
                        }
                        if (opsIndex >= 0 && opsIndex < values.length
                            && values[opsIndex].toLowerCase(Locale.ROOT).contains("license_review_required")) {
                            reviewRequiredCount++;
                        }
                    }
                    licenseCount = licenses.size();
                }
            } catch (IOException ignored) {
                // Keep summary conservative if ledger reading fails.
            }
        }

        if (Files.exists(runtimeInventory)) {
            try {
                java.util.Map<String, Integer> backendCounts = new java.util.LinkedHashMap<>();
                for (String line : Files.readAllLines(runtimeInventory)) {
                    String marker = "backend=`";
                    int start = line.indexOf(marker);
                    if (start < 0) {
                        continue;
                    }
                    int end = line.indexOf('`', start + marker.length());
                    if (end < 0) {
                        continue;
                    }
                    String backend = line.substring(start + marker.length(), end);
                    backendCounts.merge(backend, 1, Integer::sum);
                }
                runtimeBackends = backendCounts.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .toList();
            } catch (IOException ignored) {
                // Keep runtime lanes empty if inventory reading fails.
            }
        }

        return new SubstrateLedgerSummary(modelCount, runtimeBackends.size(), licenseCount, reviewRequiredCount, runtimeBackends);
    }

    private List<String> readObservedLocalModels() {
        LinkedHashSet<String> models = new LinkedHashSet<>();
        Path runtimeInventory = workspaceRoot.resolve(".northstar-kernel").resolve("generated").resolve("substrates")
            .resolve("LOCAL_MODEL_RUNTIME_INVENTORY.md");
        if (Files.exists(runtimeInventory)) {
            Pattern inventoryPattern = Pattern.compile("-\\s+`([^`]+)`");
            try {
                for (String line : Files.readAllLines(runtimeInventory)) {
                    Matcher matcher = inventoryPattern.matcher(line);
                    if (matcher.find()) {
                        String modelId = matcher.group(1).trim();
                        if (!modelId.isBlank()) {
                            models.add(modelId);
                        }
                    }
                }
            } catch (IOException ignored) {
                // Best effort only.
            }
        }
        try {
            models.addAll(OllamaBridge.listAvailableModels());
        } catch (Exception ignored) {
            // Best effort only.
        }
        return List.copyOf(models);
    }

    private String renderAmbiguousSectionMatches(String query, List<CatalogSectionMatch> matches) {
        StringBuilder sb = new StringBuilder();
        sb.append("AMBIGUOUS COMMAND SECTION: ").append(query).append("\n");
        sb.append("Matches:\n");
        for (CatalogSectionMatch match : matches) {
            sb.append("  - commands ")
                .append(match.catalog().surfaceId())
                .append(" ")
                .append(match.section().id())
                .append(" :: ")
                .append(match.section().title())
                .append("\n");
        }
        return sb.toString().trim();
    }

    private List<String> selectWholeSystemProbeHeads(String prompt, ContinuitySnapshot snapshot) {
        LinkedHashSet<String> selected = new LinkedHashSet<>();
        if (prompt != null) {
            for (String head : WHOLE_SYSTEM_PROBE_PRIORITY) {
                if (containsWord(prompt, head)) {
                    selected.add(head);
                }
            }
        }
        for (String head : WHOLE_SYSTEM_PROBE_PRIORITY) {
            if (selected.size() >= WHOLE_SYSTEM_PROBE_PRIORITY.size()) {
                break;
            }
            if ("morse".equals(head) || "emf".equals(head) || "aubo".equals(head)) {
                if (snapshot.visibleMenuHeads().contains(head) || (prompt != null && containsWord(prompt, head))) {
                    selected.add(head);
                }
                continue;
            }
            selected.add(head);
        }
        for (String head : snapshot.visibleMenuHeads()) {
            if (selected.size() >= WHOLE_SYSTEM_PROBE_PRIORITY.size()) {
                break;
            }
            if (WHOLE_SYSTEM_PROBE_PRIORITY.contains(head)) {
                selected.add(head);
            }
        }
        return List.copyOf(selected);
    }

    private CapabilityProbeResult runCapabilityProbe(String head, String prompt, ContinuitySnapshot snapshot) {
        Optional<CapabilityProbeSpec> spec = capabilityProbeSpecForHead(head, prompt);
        if (spec.isPresent()) {
            CapabilityProbeSpec probe = spec.get();
            if ("airun".equals(probe.mode())) {
                return runAiRunCapabilityProbe(head, probe.command());
            }
            if ("kernel".equals(probe.mode())) {
                return runKernelCapabilityProbe(head, probe.command());
            }
        }

        Optional<CatalogSectionMatch> section = resolveFraynixSectionByHead(head);
        if (section.isPresent()) {
            CatalogSectionMatch match = section.get();
            return new CapabilityProbeResult(
                head,
                "catalog-visible",
                "visible-only",
                "catalog",
                "section=" + match.section().id() + ", title=" + match.section().title(),
                "Launch via " + "launch fraynix " + match.section().id()
            );
        }

        boolean continuityVisible = snapshot.visibleMenuHeads().contains(head);
        return new CapabilityProbeResult(
            head,
            "(no mapped live probe)",
            continuityVisible ? "visible-only" : "unavailable",
            "continuity",
            continuityVisible ? "continuity-visible" : "not in continuity visible heads",
            continuityVisible
                ? "Head is visible through continuity/menu memory but has no live probe mapping yet."
                : "No executable or catalog-visible lane discovered for this head."
        );
    }

    private Optional<CapabilityProbeSpec> capabilityProbeSpecForHead(String head, String prompt) {
        if (head == null || head.isBlank()) {
            return Optional.empty();
        }
        String command = inferUnisonAiRunCommand(head, prompt);
        if (command != null && !command.isBlank()) {
            return Optional.of(new CapabilityProbeSpec(head, command, "airun"));
        }
        return switch (head.toLowerCase(Locale.ROOT)) {
            case "aether" -> Optional.of(new CapabilityProbeSpec(head, "aether", "kernel"));
            case "braid" -> Optional.of(new CapabilityProbeSpec(head, "braid", "kernel"));
            case "nexus" -> Optional.of(new CapabilityProbeSpec(head, "nexus status", "kernel"));
            case "gravity" -> Optional.of(new CapabilityProbeSpec(head, "gravity status", "kernel"));
            case "fusion" -> Optional.of(new CapabilityProbeSpec(head, "fusion status", "kernel"));
            default -> Optional.empty();
        };
    }

    private CapabilityProbeResult runAiRunCapabilityProbe(String head, String command) {
        String envelopeText = executeAiRunLane(command);
        JSONObject envelope = extractAiRunEnvelopeJson(envelopeText);
        String output = extractAiRunOutput(envelopeText);
        String status = envelope.optString("status", "");
        String mode = "airun/" + envelope.optString("lane", "unknown");
        String evidence = "status=" + (status.isBlank() ? "unknown" : status)
            + ", allowed=" + envelope.optBoolean("allowed", false)
            + ", head=" + envelope.optString("head", head);
        String detail = firstNonBlank(
            envelope.optString("message", ""),
            firstLine(output),
            compactBlock(envelopeText, 4, 220)
        );

        String probeStatus = switch (status) {
            case "ok" -> output.contains("Unknown command:") || output.contains("Usage:")
                ? "degraded"
                : "observed-live";
            case "blocked", "busy", "timeout", "error", "unresolved", "rejected" -> "degraded";
            default -> "unavailable";
        };
        return new CapabilityProbeResult(head, command, probeStatus, mode, evidence, detail);
    }

    private CapabilityProbeResult runKernelCapabilityProbe(String head, String command) {
        String output = immortalKernel.execute(command);
        boolean observed = output != null
            && !output.isBlank()
            && !output.toLowerCase(Locale.ROOT).contains("unknown command");
        String detail = firstLine(output);
        return new CapabilityProbeResult(
            head,
            command,
            observed ? "observed-live" : "degraded",
            "kernel",
            observed ? "immortal-kernel direct execution" : "kernel command unavailable",
            detail.isBlank() ? "(no output)" : detail
        );
    }

    private JSONObject extractAiRunEnvelopeJson(String envelopeText) {
        if (envelopeText == null || envelopeText.isBlank()) {
            return new JSONObject();
        }
        int envelopeMarker = envelopeText.indexOf("Envelope:\n");
        int outputMarker = envelopeText.indexOf("\nOutput:");
        if (envelopeMarker < 0 || outputMarker <= envelopeMarker) {
            return new JSONObject();
        }
        String json = envelopeText.substring(envelopeMarker + "Envelope:\n".length(), outputMarker).trim();
        try {
            return new JSONObject(json);
        } catch (Exception ignored) {
            return new JSONObject();
        }
    }

    private static String extractAiRunOutput(String envelopeText) {
        if (envelopeText == null || envelopeText.isBlank()) {
            return "";
        }
        int outputMarker = envelopeText.indexOf("\nOutput:\n");
        if (outputMarker < 0) {
            return "";
        }
        return envelopeText.substring(outputMarker + "\nOutput:\n".length()).trim();
    }

    private static String firstLine(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String[] lines = text.split("\\R", 2);
        return lines.length == 0 ? "" : lines[0].trim();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private int totalSectionCount() {
        return commandCatalogs.stream().mapToInt(UltimateCommandCatalog::sectionCount).sum();
    }

    private int totalCommandCount() {
        return commandCatalogs.stream().mapToInt(UltimateCommandCatalog::commandCount).sum();
    }

    private SectionTargetRequest parseSectionTargetRequest(String raw) {
        String trimmed = raw.trim();
        int arrowIndex = trimmed.indexOf("->");
        if (arrowIndex >= 0) {
            String section = trimmed.substring(0, arrowIndex).trim();
            String target = trimmed.substring(arrowIndex + 2).trim();
            return new SectionTargetRequest(section, target);
        }

        String[] tokens = trimmed.split("\\s+");
        if (tokens.length >= 2) {
            String candidateTarget = tokens[tokens.length - 1];
            if (registry.find(candidateTarget).isPresent()) {
                String section = String.join(" ", Arrays.copyOf(tokens, tokens.length - 1)).trim();
                return new SectionTargetRequest(section, candidateTarget);
            }
        }

        return new SectionTargetRequest(trimmed, null);
    }

    private String displayPath(Path path) {
        if (path == null) {
            return "(none)";
        }
        Path absolute = path.toAbsolutePath().normalize();
        if (absolute.startsWith(workspaceRoot)) {
            return workspaceRoot.relativize(absolute).toString().replace('\\', '/');
        }
        return absolute.toString().replace('\\', '/');
    }

    private String quoteIfNeeded(Path path) {
        String raw = path.toString().replace('\\', '/');
        return raw.contains(" ") ? "\"" + raw + "\"" : raw;
    }

    private record SectionTargetRequest(String sectionQuery, String targetSubsystemId) {
    }

    private record CatalogSectionMatch(UltimateCommandCatalog catalog, UltimateCommandCatalog.CommandSection section) {
    }

    private record AiRunPolicyDecision(boolean allowed, String errorCode, String reason) {
    }

    private record AiRunExecution(String status,
                                  Integer exitCode,
                                  String errorCode,
                                  String message,
                                  String output,
                                  long durationMs) {
    }

    private record SurfaceQuery(UltimateCommandCatalog catalog, String sectionQuery) {
    }

    private record DemoStep(String title, String command) {
    }

    private record SubstrateLedgerSummary(int modelCount,
                                          int runtimeCount,
                                          int licenseCount,
                                          int reviewRequiredCount,
                                          List<String> runtimeBackends) {
    }

    private static final class PersistentFraynixBootSession {
        private final Process process;
        private final OutputStreamWriter writer;
        private final ArrayDeque<String> lineBuffer = new ArrayDeque<>();
        private final Object lineMonitor = new Object();
        private final ReentrantLock commandLock = new ReentrantLock();
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final Thread readerThread;

        private PersistentFraynixBootSession(Process process) {
            this.process = process;
            this.writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8);
            this.readerThread = new Thread(this::pumpOutputLines, "fraynix-boot-persistent-output");
            this.readerThread.setDaemon(true);
            this.readerThread.start();
        }

        private boolean isAlive() {
            return !closed.get() && process.isAlive();
        }

        private void awaitReady(Duration timeout) throws IOException, InterruptedException, TimeoutException {
            commandLock.lock();
            try {
                executeSentinelOnly(timeout);
            } finally {
                commandLock.unlock();
            }
        }

        private String runCommand(String command, Duration timeout) throws IOException, InterruptedException, TimeoutException {
            String normalized = command == null ? "" : command.trim();
            if (normalized.isBlank()) {
                return "";
            }
            commandLock.lock();
            try {
                ensureAlive();
                clearBufferedLines();
                String sentinel = "__AI_RUN_SENTINEL_" + Integer.toUnsignedString((normalized + System.nanoTime()).hashCode(), 36) + "__";
                writeLine(normalized);
                writeLine(sentinel);
                return collectUntilSentinel(sentinel, timeout);
            } finally {
                commandLock.unlock();
            }
        }

        private void closeQuietly() {
            if (!closed.compareAndSet(false, true)) {
                return;
            }
            commandLock.lock();
            try {
                if (process.isAlive()) {
                    try {
                        writer.write("exit");
                        writer.write(System.lineSeparator());
                        writer.flush();
                    } catch (IOException ignored) {
                        // best effort shutdown
                    }
                    try {
                        process.waitFor(3, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    if (process.isAlive()) {
                        process.destroyForcibly();
                        try {
                            process.waitFor(2, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                try {
                    writer.close();
                } catch (IOException ignored) {
                    // best effort cleanup
                }
            } finally {
                commandLock.unlock();
            }
        }

        private void executeSentinelOnly(Duration timeout) throws IOException, InterruptedException, TimeoutException {
            ensureAlive();
            clearBufferedLines();
            String sentinel = "__AI_BOOT_READY_" + Integer.toUnsignedString((int) System.nanoTime(), 36) + "__";
            writeLine(sentinel);
            collectUntilSentinel(sentinel, timeout);
        }

        private void writeLine(String value) throws IOException {
            writer.write(value);
            writer.write(System.lineSeparator());
            writer.flush();
        }

        private void ensureAlive() throws IOException {
            if (closed.get() || !process.isAlive()) {
                throw new IOException("Persistent fraynix-boot session is not alive.");
            }
        }

        private void clearBufferedLines() {
            synchronized (lineMonitor) {
                lineBuffer.clear();
            }
        }

        private String collectUntilSentinel(String sentinel, Duration timeout) throws InterruptedException, TimeoutException, IOException {
            long timeoutMs = Math.max(1L, timeout.toMillis());
            long deadline = System.currentTimeMillis() + timeoutMs;
            String sentinelLower = sentinel.toLowerCase(Locale.ROOT);
            StringBuilder capture = new StringBuilder();
            while (true) {
                long remainingMs = deadline - System.currentTimeMillis();
                if (remainingMs <= 0) {
                    throw new TimeoutException("Timed out waiting for mapped fraynix-boot sentinel.");
                }
                String line = pollLine(remainingMs);
                if (line == null) {
                    throw new TimeoutException("Timed out waiting for mapped fraynix-boot output.");
                }
                if (FRAYNIX_BOOT_EOF_MARKER.equals(line)) {
                    throw new IOException("Persistent fraynix-boot session terminated unexpectedly.");
                }
                if (line.toLowerCase(Locale.ROOT).contains(sentinelLower)) {
                    break;
                }
                if (capture.length() < AI_RUN_CAPTURE_CHAR_LIMIT) {
                    capture.append(line).append('\n');
                }
            }
            return capture.toString().trim();
        }

        private String pollLine(long timeoutMs) throws InterruptedException {
            long deadline = System.currentTimeMillis() + timeoutMs;
            synchronized (lineMonitor) {
                while (lineBuffer.isEmpty()) {
                    long waitMs = deadline - System.currentTimeMillis();
                    if (waitMs <= 0) {
                        return null;
                    }
                    lineMonitor.wait(Math.min(waitMs, 200L));
                }
                return lineBuffer.removeFirst();
            }
        }

        private void pumpOutputLines() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    appendLine(line);
                }
            } catch (IOException ignored) {
                // best effort capture
            } finally {
                appendLine(FRAYNIX_BOOT_EOF_MARKER);
            }
        }

        private void appendLine(String line) {
            synchronized (lineMonitor) {
                while (lineBuffer.size() >= FRAYNIX_BOOT_PERSISTENT_BUFFER_LINES) {
                    lineBuffer.removeFirst();
                }
                lineBuffer.addLast(line);
                lineMonitor.notifyAll();
            }
        }
    }

    private static int parseInt(String raw, int fallback) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String stripLeadingInvisible(String text) {
        if (text == null || text.isEmpty()) {
            return text == null ? "" : text;
        }
        int idx = 0;
        while (idx < text.length()) {
            char c = text.charAt(idx);
            if (c == '\uFEFF' || c == '\u200B' || c == '\u200C' || c == '\u200D') {
                idx++;
                continue;
            }
            if (Character.isISOControl(c) && !Character.isWhitespace(c)) {
                idx++;
                continue;
            }
            break;
        }
        return text.substring(idx);
    }

    private static float parseFloat(String raw, float fallback) {
        try {
            return Float.parseFloat(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int indexOf(String[] headers, String needle) {
        for (int i = 0; i < headers.length; i++) {
            if (needle.equalsIgnoreCase(headers[i])) {
                return i;
            }
        }
        return -1;
    }

    private static String stripQuotes(String raw) {
        String trimmed = raw.trim();
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) ||
            (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static record AbstractionPersistResult(Path memoryPath, boolean persisted, String fault) {
    }

    private static record AbstractionBiasPlan(
        CapabilityProfile.Capability dominantCapability,
        double strength,
        ModelRole modelRole,
        String taskHint,
        String laneCommand,
        String rationale
    ) {
    }

    private static record AbstractionLaneCandidate(String command, double baseWeight) {
    }

    private static record AbstractionBiasOutcome(
        String observedAt,
        CapabilityProfile.Capability dominantCapability,
        ModelRole modelRole,
        String laneCommand,
        String head,
        String taskHint,
        String selectedModel,
        boolean success,
        long latencyMs,
        String errorClass
    ) {
    }

    private static record AbstractionOutcomePersistResult(Path path, boolean persisted, String fault) {
    }

    private static record AbstractionOutcomeBridgeResult(
        boolean activeIntegration,
        boolean pushed,
        String selectedModel,
        String fault
    ) {
        static AbstractionOutcomeBridgeResult inactive() {
            return new AbstractionOutcomeBridgeResult(false, false, "", "");
        }

        String summary() {
            if (!activeIntegration) {
                return "inactive";
            }
            if (pushed) {
                return "pushed:model=" + (selectedModel == null || selectedModel.isBlank() ? "(unknown)" : selectedModel);
            }
            return "active-not-pushed" + (fault == null || fault.isBlank() ? "" : " fault=" + fault);
        }
    }

    private static record ModelOutcomeLearning(int observations, double successEma, double avgLatencyMs) {
    }

    private static record LearnedModelSelectionCandidate(
        ModelSelectionResult result,
        double adjustedScore,
        double learnedBoost,
        int learnedObservations,
        double successEma,
        double avgLatencyMs
    ) {
    }

    private static record ModelMemoryBridgeResult(
        boolean activeIntegration,
        boolean pushed,
        String selectedModel,
        String generatedSuite,
        int capabilityScoresWritten,
        int usageRecordsWritten,
        String compiledContextPack,
        String fault
    ) {
        static ModelMemoryBridgeResult inactive() {
            return new ModelMemoryBridgeResult(false, false, "", "", 0, 0, "", "");
        }
    }

    private static record PromptAbstractionSnapshot(
        String prompt,
        List<DataAbstractionEngine.AbstractedArtifact> artifacts,
        Map<CapabilityProfile.Capability, Double> capabilitySignals,
        List<String> taskHints,
        List<String> provenanceSignals,
        Path memoryPath,
        boolean persisted,
        String persistenceFault,
        ModelMemoryBridgeResult modelBridge
    ) {
        static PromptAbstractionSnapshot empty(String prompt, Path memoryPath) {
            return new PromptAbstractionSnapshot(
                prompt == null ? "" : prompt,
                List.of(),
                Map.of(),
                List.of(),
                List.of(),
                memoryPath,
                false,
                "",
                ModelMemoryBridgeResult.inactive()
            );
        }
    }

    private record CapabilityProbeSpec(String head, String command, String mode) {
    }

    public static record CapabilityProbeResult(String head,
                                               String command,
                                               String status,
                                               String mode,
                                               String evidence,
                                               String detail) {
    }

    public static record LiveCapabilitySnapshot(String observedAt,
                                                String prompt,
                                                List<CapabilityProbeResult> results,
                                                long observedLiveCount,
                                                long degradedCount,
                                                long visibleOnlyCount,
                                                long unavailableCount) {
        static LiveCapabilitySnapshot empty() {
            return new LiveCapabilitySnapshot("", "", List.of(), 0, 0, 0, 0);
        }
    }

    public static record ContinuitySnapshot(
        boolean loaded,
        String generatedAt,
        Path continuityMemoryPath,
        Path mathCanonPath,
        Path axiomLedgerPath,
        Path truthRegistryPath,
        List<String> resumeRules,
        List<String> deterministicLaneSummaries,
        List<String> deterministicLaneGaps,
        List<String> visibleMenuHeads,
        List<String> menuSectionSamples,
        int subsystemCount,
        int documentCount,
        int livingProofLaneCount
    ) {
        static ContinuitySnapshot unavailable(
            Path continuityMemoryPath,
            Path mathCanonPath,
            Path axiomLedgerPath,
            Path truthRegistryPath,
            List<String> visibleMenuHeads,
            List<String> menuSectionSamples
        ) {
            return new ContinuitySnapshot(
                false,
                "",
                continuityMemoryPath,
                mathCanonPath,
                axiomLedgerPath,
                truthRegistryPath,
                List.of(),
                List.of(),
                List.of(),
                visibleMenuHeads,
                menuSectionSamples,
                0,
                0,
                0
            );
        }
    }

    private static Path discoverAssetManagerDir(Path start) {
        Path current = start;
        while (current != null) {
            if (isAssetManagerDir(current)) {
                return current;
            }

            Path directChild = current.resolve("Asset-Manager");
            if (isAssetManagerDir(directChild)) {
                return directChild;
            }

            Path nestedChild = current.resolve("Java-Memory").resolve("Asset-Manager");
            if (isAssetManagerDir(nestedChild)) {
                return nestedChild;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Could not locate Asset-Manager from: " + start);
    }

    private static boolean isAssetManagerDir(Path path) {
        return path != null && Files.isDirectory(path) && Files.exists(path.resolve("build.gradle"));
    }
}
