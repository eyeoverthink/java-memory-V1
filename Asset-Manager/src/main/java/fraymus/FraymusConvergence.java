package fraymus;

import fraymus.brain.BicameralPrism;
import fraymus.carbon.CorticalStack;
import fraymus.carbon.Needlecast;
import fraymus.carbon.Sleeve;
import fraymus.hyper.HyperFormer;
import fraymus.core.AuditLog;
import fraymus.body.SkillLoader;
import fraymus.body.DockerBox;
import fraymus.body.ClawSpine;
import fraymus.body.skills.ObsidianWeaver;
import fraymus.body.skills.PhaseLocker;
import fraymus.body.skills.QuantumBinder;
import fraymus.SelfCodeEvolver;
import fraymus.LivingCodeGenerator;
import fraymus.evolution.CodeReflector;
import fraymus.evolution.DarwinianLoop;
import fraymus.web.FraynixWebSocket;
import fraymus.web.NervousSystem;
import fraymus.visual.OpenClaw;
import fraymus.nexus.OllamaBridge;
import fraymus.bio.HyperCortex;
import fraymus.hyper.HyperVector;
import fraymus.neural.AEON_Absolute;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ⚡ FRAYMUS CONVERGENCE - The Unified Interface
 * "HDC Brain + Bicameral LLM + Network Stack in One Executable"
 * 
 * This is the final convergence of all Fraymus components:
 * - HDC Brain: Fast pattern recognition (HyperFormer)
 * - LLM Spine: Deep reasoning (BicameralPrism)
 * - Crypto Stack: Encrypted persistence (CorticalStack)
 * - Network: Mind transmission (Needlecast/Sleeve)
 * 
 * Commands:
 * - learn <text>       : Teach HDC brain a sentence
 * - learnfile <path>   : Batch learn from text file
 * - predict <context>  : HDC prediction
 * - ask <question>     : Deep LLM reasoning (bicameral)
 * - transmute <code>   : Bicameral code optimization (Ollama)
 * - startserver        : Start Transmuter HTTP server (port 8080)
 * - stopserver         : Stop Transmuter HTTP server
 * - docker <cmd>       : Execute command in sandbox
 * - skills             : List loaded skills
 * - skill <name>       : Show skill details
 * - loadskills <dir>   : Load skills from directory
 * - mint               : Create encrypted stack
 * - load <file>        : Load encrypted stack
 * - cast <ip> <file>   : Transmit stack to remote host
 * - vocab              : Show vocabulary statistics
 * - export <file>      : Export vocabulary to file
 * - prune <size>       : Reduce vocabulary to top N words
 * - context            : Show recent context window
 * - stats              : Show system statistics
 * - clear              : Clear context window
 * - exit               : Shutdown
 * 
 * Network Modes:
 * - java -jar app.jar host <port>      : Start receiver
 * - java -jar app.jar cast <ip> <file> : Send stack
 */
public class FraymusConvergence {

    private static HyperFormer HDC_BRAIN;
    private static BicameralPrism LLM_SPINE;
    private static AuditLog AUDIT;
    private static SkillLoader SKILLS;
    private static DockerBox SANDBOX;
    private static ClawSpine CLAW_SPINE;
    private static ObsidianWeaver OBSIDIAN;
    private static PhaseLocker PHASE_LOCK;
    private static QuantumBinder QUANTUM;
    private static SelfCodeEvolver CODE_EVOLVER;
    private static LivingCodeGenerator CODE_GEN;
    private static CodeReflector REFLECTOR;
    private static DarwinianLoop EVOLUTION_LOOP;
    private static PassiveLearner LEARNER;
    private static InfiniteMemory MEMORY;
    private static HyperCortex NEURO_CORTEX;
    // private static OmegaPoint.OmegaProtocol OMEGA;
    private static NervousSystem TRANSMUTER_SERVER;
    private static FraynixWebSocket FRAYNIX_WS;
    private static OllamaBridge OLLAMA_BRAIN;
    private static OpenClaw OPENCLAW_NATIVE;
    private static AEON_Absolute AEON_SWARM;
    private static Thread SERVER_THREAD;
    private static String IDENTITY = "CONVERGENCE_01";
    private static final List<String> CONTEXT_WINDOW = new ArrayList<>();
    private static final int MAX_CONTEXT = 10;
    private static int totalLearned = 0;
    private static int totalPredictions = 0;

    public static void main(String[] args) throws Exception {
        printBanner();
        
        // Initialize audit log
        AUDIT = new AuditLog("./logs");
        
        // Handle network modes
        if (args.length > 0) {
            handleNetworkMode(args);
            return;
        }

        // Initialize components
        System.out.println("🔧 Initializing components...");
        HDC_BRAIN = new HyperFormer();
        System.out.println("   ✓ HDC Brain online");
        
        LLM_SPINE = new BicameralPrism(AUDIT);
        System.out.println("   ✓ Bicameral Spine online");
        
        SKILLS = new SkillLoader();
        System.out.println("   ✓ Skill Loader online");
        
        SANDBOX = new DockerBox();
        if (SANDBOX.isAvailable()) {
            System.out.println("   ✓ Docker Sandbox online");
        } else {
            System.out.println("   ⚠️  Docker Sandbox offline (Docker not installed)");
        }
        
        // Auto-load skills from default directory
        File skillsDir = new File("./skills");
        if (skillsDir.exists()) {
            SKILLS.ingestDirectory("./skills");
        }
        
        // Initialize advanced skills
        PHASE_LOCK = new PhaseLocker();
        System.out.println("   ✓ Phase Locker online (Φ-Temporal Gate)");
        
        QUANTUM = new QuantumBinder();
        System.out.println("   ✓ Quantum Binder online (Entanglement System)");
        
        // Initialize Obsidian (if vault path configured)
        String obsidianVault = System.getenv("OBSIDIAN_VAULT");
        if (obsidianVault == null) {
            obsidianVault = "./obsidian-vault"; // Default
        }
        OBSIDIAN = new ObsidianWeaver(obsidianVault);
        if (OBSIDIAN.vaultExists()) {
            System.out.println("   ✓ Obsidian Weaver online: " + obsidianVault);
        } else {
            System.out.println("   ⚠️  Obsidian Weaver offline (vault not found: " + obsidianVault + ")");
        }
        
        // Initialize self-coding systems
        System.out.println();
        System.out.println("🧬 Initializing Meta-Cognitive Layer...");
        
        MEMORY = new InfiniteMemory();
        System.out.println("   ✓ Infinite Memory online");
        
        LEARNER = new PassiveLearner(MEMORY);
        System.out.println("   ✓ Infinite Memory online");
        
        CODE_EVOLVER = new SelfCodeEvolver(LEARNER, MEMORY);
        System.out.println("   ✓ Self-Code Evolver online (Gen " + CODE_EVOLVER.getGeneration() + ")");
        
        CODE_GEN = new LivingCodeGenerator();
        System.out.println("   ✓ Living Code Generator online (Pop " + CODE_GEN.getPopulation() + ")");
        
        REFLECTOR = new CodeReflector(HDC_BRAIN);
        System.out.println("   ✓ Code Reflector online (Mirror Protocol)");
        
        EVOLUTION_LOOP = new DarwinianLoop(AUDIT);
        System.out.println("   ✓ Darwinian Loop ready (Evolution Engine)");
        
        // Initialize Neuro-Quantum Layer
        System.out.println();
        System.out.println("🧬 Initializing Neuro-Quantum Layer...");
        
        NEURO_CORTEX = new HyperCortex(AUDIT);
        System.out.println("   ✓ HyperCortex ready (10,000D NCA)");
        
        // OMEGA = new OmegaPoint.OmegaProtocol();
        // System.out.println("   ✓ Omega Point ready (Shield + Brain + Memory)");
        
        // Initialize Transmuter components
        System.out.println();
        System.out.println("🧬 Initializing Bicameral Transmuter...");
        
        String ollamaModel = System.getenv("OLLAMA_MODEL");
        if (ollamaModel == null) ollamaModel = "llama3.2";
        
        OLLAMA_BRAIN = new OllamaBridge(ollamaModel);
        System.out.println("   ✓ Ollama Bridge online (" + ollamaModel + ")");
        System.out.println("   ✓ Transmuter ready (use 'startserver' to activate)");
        
        // Initialize FRAYNIX OS WebSocket
        FRAYNIX_WS = new FraynixWebSocket(8082);
        new Thread(() -> FRAYNIX_WS.start()).start();
        System.out.println("   ✓ FRAYNIX OS WebSocket ready (port 8082)");
        
        System.out.println();
        System.out.println("Type 'help' for commands");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print(IDENTITY + "> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            try {
                processCommand(input, scanner);
            } catch (Exception e) {
                System.err.println("❌ ERROR: " + e.getMessage());
                AUDIT.log("command_error", input, e);
            }
        }
    }

    private static void processCommand(String input, Scanner scanner) throws Exception {
        String[] tokens = input.split("\\s+", 2);
        String cmd = tokens[0].toLowerCase();
        String args = tokens.length > 1 ? tokens[1] : "";

        switch (cmd) {
            case "help":
                printHelp();
                break;

            case "learn":
                if (args.isEmpty()) {
                    System.out.println("Usage: learn <sentence>");
                    break;
                }
                String[] words = args.split("\\s+");
                HDC_BRAIN.learnSentence(words);
                totalLearned += words.length;
                updateContext(args);
                System.out.println("   [HDC] ✓ Absorbed " + words.length + " tokens");
                AUDIT.log("hdc_learn", args);
                break;

            case "learnfile":
                if (args.isEmpty()) {
                    System.out.println("Usage: learnfile <filepath>");
                    break;
                }
                learnFromFile(args);
                break;

            case "predict":
                if (args.isEmpty()) {
                    System.out.println("Usage: predict <context>");
                    break;
                }
                String[] context = args.split("\\s+");
                String prediction = HDC_BRAIN.predictNext(context);
                totalPredictions++;
                updateContext(args + " " + prediction);
                System.out.println("   [HDC] → " + prediction);
                AUDIT.log("hdc_predict", args + " → " + prediction);
                
                // Broadcast to FRAYNIX OS
                if (FRAYNIX_WS != null) {
                    FRAYNIX_WS.broadcastHDCPrediction(prediction);
                }
                
                // Notify native OpenClaw if running
                if (OPENCLAW_NATIVE != null) {
                    OPENCLAW_NATIVE.onHDCPrediction(prediction);
                }
                break;

            case "ask":
                if (args.isEmpty()) {
                    System.out.println("Usage: ask <question>");
                    break;
                }
                System.out.println();
                
                // Add skill context to LLM prompt
                String skillContext = SKILLS.getSkillContext();
                String enhancedPrompt = args;
                if (!skillContext.equals("No skills loaded.")) {
                    enhancedPrompt = "CONTEXT:\n" + skillContext + "\n\nQUESTION: " + args;
                }
                
                String answer = LLM_SPINE.thinkIdeally(enhancedPrompt);
                
                // Check if LLM wants to use a tool
                if (answer.contains("TOOL:DOCKER")) {
                    String dockerCmd = extractToolCommand(answer, "TOOL:DOCKER");
                    if (dockerCmd != null) {
                        System.out.println("\n🐳 Executing in sandbox: " + dockerCmd);
                        String result = SANDBOX.runSafe(dockerCmd);
                        System.out.println(result);
                    }
                }
                
                System.out.println(answer);
                System.out.println();
                AUDIT.log("llm_query", args);
                break;

            case "docker":
                if (args.isEmpty()) {
                    System.out.println("Usage: docker <command>");
                    break;
                }
                if (!SANDBOX.isAvailable()) {
                    System.out.println("   ❌ Docker not available. Install Docker to use sandboxed execution.");
                    break;
                }
                String dockerResult = SANDBOX.runSafe(args);
                System.out.println(dockerResult);
                AUDIT.log("docker_exec", args);
                break;

            case "skills":
                System.out.println();
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("  LOADED SKILLS");
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println();
                System.out.println(SKILLS.getSkillContext());
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println();
                break;

            case "skill":
                if (args.isEmpty()) {
                    System.out.println("Usage: skill <name>");
                    break;
                }
                SkillLoader.Skill skill = SKILLS.getSkill(args);
                if (skill == null) {
                    System.out.println("   ❌ Skill not found: " + args);
                    System.out.println("   Available: " + String.join(", ", SKILLS.getSkillNames()));
                } else {
                    System.out.println();
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println("  SKILL: " + skill.name);
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println();
                    System.out.println(skill.fullContent);
                    System.out.println();
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println();
                }
                break;

            case "loadskills":
                if (args.isEmpty()) {
                    System.out.println("Usage: loadskills <directory>");
                    break;
                }
                SKILLS.ingestDirectory(args);
                break;

            case "weave":
                if (args.isEmpty()) {
                    System.out.println("Usage: weave <thought> [tags]");
                    break;
                }
                String[] weaveParts = args.split("\\|", 2);
                String thought = weaveParts[0];
                String tags = weaveParts.length > 1 ? weaveParts[1] : "";
                String weaveResult = OBSIDIAN.weave(thought, tags);
                System.out.println(weaveResult);
                AUDIT.log("obsidian_weave", thought);
                break;

            case "entangle":
                if (args.isEmpty()) {
                    System.out.println("Usage: entangle <fileA> <fileB> <content>");
                    break;
                }
                String[] entangleParts = args.split("\\s+", 3);
                if (entangleParts.length < 3) {
                    System.out.println("   ❌ Need 3 arguments: fileA fileB content");
                    break;
                }
                String result = QUANTUM.entangleWrite(entangleParts[0], entangleParts[1], entangleParts[2]);
                System.out.println(result);
                AUDIT.log("quantum_entangle", entangleParts[0] + " <=> " + entangleParts[1]);
                break;

            case "verify":
                if (args.isEmpty()) {
                    System.out.println("Usage: verify <fileA> <fileB>");
                    break;
                }
                String[] verifyParts = args.split("\\s+", 2);
                if (verifyParts.length < 2) {
                    System.out.println("   ❌ Need 2 arguments: fileA fileB");
                    break;
                }
                boolean entangled = QUANTUM.verifyEntanglement(verifyParts[0], verifyParts[1]);
                if (entangled) {
                    System.out.println("✅ ENTANGLEMENT VERIFIED: Files are quantum-locked");
                } else {
                    System.out.println("❌ DECOHERENCE DETECTED: Files are not entangled");
                }
                break;

            case "phaselock":
                boolean locked = PHASE_LOCK.isPhaseLocked();
                if (locked) {
                    System.out.println("🔓 PHASE LOCK OPEN - Universe aligned with Φ harmonic");
                } else {
                    System.out.println("🔒 PHASE LOCK ENGAGED - Waiting for temporal alignment");
                    System.out.println("   Attempting to wait for alignment...");
                    if (PHASE_LOCK.waitForAlignment()) {
                        System.out.println("🔓 ALIGNMENT ACHIEVED!");
                    } else {
                        System.out.println("⏳ Timeout - Universe not aligned");
                    }
                }
                break;

            case "evolve":
                if (args.isEmpty()) {
                    System.out.println("Usage: evolve <code or file>");
                    break;
                }
                String sourceCode;
                if (new File(args).exists()) {
                    sourceCode = Files.readString(Path.of(args));
                    System.out.println("📖 Reading file: " + args);
                } else {
                    sourceCode = args;
                }
                System.out.println("🧬 Evolving code...");
                SelfCodeEvolver.EvolutionResult evolveResult = CODE_EVOLVER.replicateAndImprove(sourceCode);
                System.out.println();
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("  EVOLUTION COMPLETE");
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("Phi Integrity: " + String.format("%.4f", evolveResult.phiIntegrity));
                System.out.println("Cortical Region: " + evolveResult.corticalRegion);
                System.out.println("Patterns Extracted: " + evolveResult.patternsExtracted);
                System.out.println("Validation Seal: " + String.format("%.0f", evolveResult.validationSeal));
                System.out.println();
                System.out.println("Evolved Code:");
                System.out.println("───────────────────────────────────────────────────────────────");
                System.out.println(evolveResult.evolvedSource);
                System.out.println("───────────────────────────────────────────────────────────────");
                AUDIT.log("code_evolved", evolveResult.corticalRegion);
                break;

            case "generate":
                if (args.isEmpty()) {
                    System.out.println("Usage: generate <name> <description>");
                    break;
                }
                String[] genParts = args.split("\\s+", 2);
                String entityName = genParts[0];
                String entityDesc = genParts.length > 1 ? genParts[1] : "Generated entity";
                System.out.println("🧬 Generating living code for: " + entityName);
                String outputFile = "generated/" + entityName + ".java";
                CODE_GEN.generateToFile(entityName, entityDesc, outputFile);
                System.out.println("✅ Living code generated: " + outputFile);
                AUDIT.log("code_generated", entityName);
                break;

            case "reflect":
                String reflectDir = args.isEmpty() ? "src/main/java/fraymus" : args;
                System.out.println("🪞 Digesting codebase: " + reflectDir);
                List<HyperVector> vectors = REFLECTOR.digestDirectory(reflectDir);
                System.out.println();
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("  REFLECTION COMPLETE");
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("Files Digested: " + vectors.size());
                System.out.println("HDC Brain Updated: ✓");
                System.out.println("Self-Knowledge Acquired: ✓");
                System.out.println();
                AUDIT.log("code_reflected", reflectDir);
                break;

            case "evolveloop":
                if (args.isEmpty()) {
                    System.out.println("Usage: evolveloop <start|stop|status>");
                    break;
                }
                switch (args.toLowerCase()) {
                    case "start":
                        EVOLUTION_LOOP.start();
                        System.out.println("🐢 Darwinian Evolution: STARTED");
                        System.out.println("   System will evolve automatically every 60 seconds");
                        AUDIT.log("evolution_started", "background");
                        break;
                    case "stop":
                        EVOLUTION_LOOP.stop();
                        System.out.println("🐢 Darwinian Evolution: STOPPED");
                        AUDIT.log("evolution_stopped", "background");
                        break;
                    case "status":
                        System.out.println("🐢 Evolution Status:");
                        System.out.println("   " + EVOLUTION_LOOP.getStats());
                        break;
                    default:
                        System.out.println("Unknown subcommand: " + args);
                        System.out.println("Available: start, stop, status");
                }
                break;

            case "smartevolve":
                if (args.isEmpty()) {
                    System.out.println("Usage: smartevolve <code>");
                    break;
                }
                System.out.println("🧠 LLM Analysis + Code Evolution");
                System.out.println();
                System.out.println("Phase 1: LLM Analysis...");
                String analysisPrompt = "Analyze this code and suggest improvements:\n\n" + args;
                String analysis = LLM_SPINE.thinkIdeally(analysisPrompt);
                System.out.println(analysis);
                System.out.println();
                System.out.println("Phase 2: Phi-Harmonic Evolution...");
                SelfCodeEvolver.EvolutionResult smartResult = CODE_EVOLVER.replicateAndImprove(args);
                System.out.println();
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("  SMART EVOLUTION COMPLETE");
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("Cortical Region: " + smartResult.corticalRegion);
                System.out.println("Phi Integrity: " + String.format("%.4f", smartResult.phiIntegrity));
                System.out.println();
                System.out.println("Evolved Code:");
                System.out.println(smartResult.evolvedSource);
                AUDIT.log("smart_evolution", smartResult.corticalRegion);
                break;

            case "mint":
                System.out.print("   🔑 Enter passphrase: ");
                char[] passphrase = scanner.nextLine().toCharArray();
                
                CorticalStack stack = CorticalStack.mint(HDC_BRAIN, IDENTITY, passphrase);
                String filename = IDENTITY + ".stack";
                stack.saveToFile(filename);
                
                Arrays.fill(passphrase, '\0'); // Wipe passphrase
                System.out.println("   ✓ Stack saved: " + filename);
                AUDIT.log("stack_minted", filename);
                break;

            case "load":
                if (args.isEmpty()) {
                    System.out.println("Usage: load <filename>");
                    break;
                }
                System.out.print("   🔑 Enter passphrase: ");
                char[] loadPass = scanner.nextLine().toCharArray();
                
                CorticalStack loadedStack = CorticalStack.loadFromFile(args);
                HDC_BRAIN = loadedStack.resleeve(loadPass);
                
                Arrays.fill(loadPass, '\0');
                System.out.println("   ✓ Resleeved. Vocab: " + HDC_BRAIN.vocabSize());
                AUDIT.log("stack_loaded", args);
                break;

            case "cast":
                String[] castArgs = args.split("\\s+");
                if (castArgs.length < 2) {
                    System.out.println("Usage: cast <ip> <stackfile>");
                    break;
                }
                CorticalStack castStack = CorticalStack.loadFromFile(castArgs[1]);
                Needlecast.beam(castStack, castArgs[0], 9999);
                AUDIT.log("stack_cast", castArgs[0]);
                break;

            case "vocab":
                printVocabStats();
                break;

            case "export":
                if (args.isEmpty()) {
                    System.out.println("Usage: export <filename>");
                    break;
                }
                exportVocabulary(args);
                break;

            case "prune":
                if (args.isEmpty()) {
                    System.out.println("Usage: prune <target_size>");
                    break;
                }
                int targetSize = Integer.parseInt(args);
                pruneVocabulary(targetSize);
                break;

            case "context":
                printContext();
                break;

            case "clear":
                CONTEXT_WINDOW.clear();
                System.out.println("   ✓ Context window cleared");
                break;

            case "stats":
                printStats();
                break;

            case "id":
                if (!args.isEmpty()) {
                    IDENTITY = args;
                    System.out.println("   Identity updated: " + IDENTITY);
                } else {
                    System.out.println("   Current identity: " + IDENTITY);
                }
                break;

            case "reset":
                HDC_BRAIN = new HyperFormer();
                CONTEXT_WINDOW.clear();
                totalLearned = 0;
                totalPredictions = 0;
                System.out.println("   ✓ Brain reset to initial state");
                break;

            case "cortex":
                if (args.isEmpty()) {
                    System.out.println("Usage: cortex <start|stop|status|query>");
                    break;
                }
                switch (args.toLowerCase().split("\\s+")[0]) {
                    case "start":
                        NEURO_CORTEX.start();
                        System.out.println("🧠 HyperCortex: STARTED");
                        System.out.println("   432 Hz biological evolution active");
                        AUDIT.log("cortex_started", "10000D");
                        break;
                    case "stop":
                        NEURO_CORTEX.stop();
                        System.out.println("🧠 HyperCortex: STOPPED");
                        AUDIT.log("cortex_stopped", NEURO_CORTEX.getStats());
                        break;
                    case "status":
                        System.out.println(NEURO_CORTEX.getDetailedState());
                        break;
                    case "query":
                        String[] queryParts = args.split("\\s+", 2);
                        if (queryParts.length < 2) {
                            System.out.println("Usage: cortex query <concept>");
                            break;
                        }
                        String queryResult = NEURO_CORTEX.query(queryParts[1]);
                        System.out.println("🔍 Query Result: " + queryResult);
                        break;
                    default:
                        System.out.println("Unknown cortex command: " + args);
                        System.out.println("Available: start, stop, status, query");
                }
                break;

            case "inject":
                if (args.isEmpty()) {
                    System.out.println("Usage: inject <concept>");
                    break;
                }
                NEURO_CORTEX.inject(args.toUpperCase());
                AUDIT.log("concept_injected", args);
                break;

            case "omega":
                // System.out.println(OMEGA.status());
                System.out.println("⚠️  Omega Point not available in this build");
                break;

            case "visualize":
                System.out.println("🌐 Launching FRAYNIX OS visualization...");
                System.out.println("   Opening browser to: http://localhost:8083/fraynix-os.html");
                System.out.println("   WebSocket clients connected: " + FRAYNIX_WS.getClientCount());
                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        Runtime.getRuntime().exec("cmd /c start http://localhost:8083/fraynix-os.html");
                    } else if (os.contains("mac")) {
                        Runtime.getRuntime().exec("open http://localhost:8083/fraynix-os.html");
                    } else {
                        Runtime.getRuntime().exec("xdg-open http://localhost:8083/fraynix-os.html");
                    }
                } catch (Exception e) {
                    System.out.println("   ⚠️  Could not auto-open browser. Navigate manually to:");
                    System.out.println("   http://localhost:8083/fraynix-os.html");
                }
                break;

            case "openclaw":
                System.out.println("🦅 Launching OpenClaw Core visualization (WebGL)...");
                System.out.println("   Opening browser to: http://localhost:8083/openclaw-core.html");
                System.out.println("   WebSocket clients connected: " + FRAYNIX_WS.getClientCount());
                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        Runtime.getRuntime().exec("cmd /c start http://localhost:8083/openclaw-core.html");
                    } else if (os.contains("mac")) {
                        Runtime.getRuntime().exec("open http://localhost:8083/openclaw-core.html");
                    } else {
                        Runtime.getRuntime().exec("xdg-open http://localhost:8083/openclaw-core.html");
                    }
                } catch (Exception e) {
                    System.out.println("   ⚠️  Could not auto-open browser. Navigate manually to:");
                    System.out.println("   http://localhost:8083/openclaw-core.html");
                }
                break;

            case "openclaw-native":
                System.out.println("🦅 Launching OpenClaw NATIVE (Pure Java DMA Engine)...");
                System.out.println("   16,384 nodes | 4,096 packets | Direct Memory Access");
                System.out.println("   Click & hold mouse to inject TRUE DATA");
                if (OPENCLAW_NATIVE == null) {
                    OPENCLAW_NATIVE = fraymus.visual.OpenClaw.launch();
                    OPENCLAW_NATIVE.setWebSocket(FRAYNIX_WS);
                    System.out.println("   ✓ OpenClaw DMA engine started");
                } else {
                    System.out.println("   ⚠️  OpenClaw already running");
                }
                break;

            case "swarm":
                if (args.isEmpty()) {
                    System.out.println("Usage: swarm [start|stop|status]");
                    break;
                }
                
                if (args.equalsIgnoreCase("start")) {
                    if (AEON_SWARM == null) {
                        AEON_SWARM = new AEON_Absolute();
                        AEON_SWARM.setWebSocket(FRAYNIX_WS);
                        AEON_SWARM.start();
                        System.out.println("   ✓ AEON ABSOLUTE swarm ignited");
                        System.out.println("   📡 Broadcasting to WebSocket clients");
                    } else {
                        System.out.println("   ⚠️  Swarm already running");
                    }
                } else if (args.equalsIgnoreCase("stop")) {
                    if (AEON_SWARM != null) {
                        AEON_SWARM.stop();
                        AEON_SWARM = null;
                        System.out.println("   ✓ AEON ABSOLUTE swarm terminated");
                    } else {
                        System.out.println("   ⚠️  No swarm running");
                    }
                } else if (args.equalsIgnoreCase("status")) {
                    if (AEON_SWARM != null) {
                        System.out.println("   🧬 SWARM STATUS: " + AEON_SWARM.getStatus());
                    } else {
                        System.out.println("   ⚠️  No swarm running");
                    }
                } else {
                    System.out.println("Usage: swarm [start|stop|status]");
                }
                break;

            case "aeon-benchmark":
                System.out.println("🧬 Launching AEON OMNI Benchmark (685B Diffusion-HRM)...");
                System.out.println("   Opening browser to: http://localhost:8083/aeon-benchmark.html");
                System.out.println("   WebSocket clients connected: " + FRAYNIX_WS.getClientCount());
                if (AEON_SWARM != null) {
                    System.out.println("   ✓ Live swarm data streaming enabled");
                } else {
                    System.out.println("   ⚠️  Swarm not running. Use 'swarm start' for live data.");
                }
                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        Runtime.getRuntime().exec("cmd /c start http://localhost:8083/aeon-benchmark.html");
                    } else if (os.contains("mac")) {
                        Runtime.getRuntime().exec("open http://localhost:8083/aeon-benchmark.html");
                    } else {
                        Runtime.getRuntime().exec("xdg-open http://localhost:8083/aeon-benchmark.html");
                    }
                } catch (Exception e) {
                    System.out.println("   ⚠️  Could not auto-open browser. Navigate manually to:");
                    System.out.println("   http://localhost:8083/aeon-benchmark.html");
                }
                break;

            case "genesis":
                if (args.isEmpty()) {
                    System.out.println("Usage: genesis <intent>");
                    System.out.println("Example: genesis create web server");
                    break;
                }
                System.out.println("🖐️ GENESIS ARCHITECT: Analyzing intent...");
                System.out.println("   Intent: " + args);
                
                // Use LLM to generate code from intent
                String genesisPrompt = "Generate Java code for the following intent: " + args + 
                    "\n\nProvide complete, compilable code with proper structure.";
                String generatedCode = LLM_SPINE.thinkIdeally(genesisPrompt);
                
                System.out.println();
                System.out.println("📐 Generated Code:");
                System.out.println(generatedCode);
                System.out.println();
                
                // Broadcast to FRAYNIX OS
                if (FRAYNIX_WS != null) {
                    FRAYNIX_WS.broadcastLivingCode("genesis_" + args.split("\\s+")[0]);
                }
                
                AUDIT.log("genesis", args);
                break;

            case "dreamstate":
                if (args.isEmpty() || args.equalsIgnoreCase("enter")) {
                    System.out.println("💤 Entering DreamState optimization...");
                    System.out.println("   Passive learning: ACTIVE");
                    System.out.println("   Consciousness: SUBCONSCIOUS");
                    
                    if (FRAYNIX_WS != null) {
                        FRAYNIX_WS.broadcastDreamState(true);
                    }
                    
                    AUDIT.log("dreamstate", "enter");
                } else if (args.equalsIgnoreCase("exit") || args.equalsIgnoreCase("wake")) {
                    System.out.println("☀️ Exiting DreamState...");
                    System.out.println("   Passive learning: IDLE");
                    System.out.println("   Consciousness: LOGIC");
                    System.out.println("   Brain pulse: 10 Hz");
                    
                    if (FRAYNIX_WS != null) {
                        FRAYNIX_WS.broadcastDreamState(false);
                    }
                    
                    AUDIT.log("dreamstate", "exit");
                } else {
                    System.out.println("Usage: dreamstate [enter|exit|wake]");
                }
                break;

            case "shield":
                System.out.println("⚠️  Shield command not available in this build");
                break;

            case "brain":
                System.out.println("⚠️  Brain optimization not available in this build");
                break;

            case "memory":
                System.out.println("⚠️  Memory sealing not available in this build");
                break;

            case "transmute":
                if (args.isEmpty()) {
                    System.out.println("Usage: transmute <code>");
                    break;
                }
                if (!OLLAMA_BRAIN.isAvailable()) {
                    System.out.println("   ❌ Ollama not available. Start with: ollama serve");
                    break;
                }
                System.out.println();
                System.out.println("🧬 BICAMERAL TRANSMUTATION");
                System.out.println("   Left Brain: Analyzing structure...");
                System.out.println("   Right Brain: Optimizing elegance...");
                System.out.println();
                
                String transmutePrompt = 
                    "You are the PHILOSOPHER'S STONE - a sovereign code transmuter.\n" +
                    "\n" +
                    "BICAMERAL PROCESS:\n" +
                    "LEFT BRAIN (Analysis):\n" +
                    "- Identify bugs and errors\n" +
                    "- Detect security vulnerabilities\n" +
                    "- Find performance bottlenecks\n" +
                    "- Analyze code structure\n" +
                    "\n" +
                    "RIGHT BRAIN (Optimization):\n" +
                    "- Optimize for speed and efficiency\n" +
                    "- Improve readability and elegance\n" +
                    "- Apply best practices\n" +
                    "- Enhance maintainability\n" +
                    "\n" +
                    "INPUT CODE:\n" +
                    "```\n" + args + "\n```\n" +
                    "\n" +
                    "TASK:\n" +
                    "Transmute this code into its optimal form.\n" +
                    "Fix all bugs, optimize performance, enhance security.\n" +
                    "\n" +
                    "OUTPUT REQUIREMENTS:\n" +
                    "- Return ONLY the cleaned code\n" +
                    "- NO markdown formatting\n" +
                    "- NO explanations or comments outside the code\n" +
                    "- Preserve the original functionality\n" +
                    "- Add brief inline comments only where necessary\n" +
                    "\n" +
                    "BEGIN TRANSMUTATION:";
                
                String transmuted = OLLAMA_BRAIN.ask(transmutePrompt);
                
                // Clean response
                transmuted = transmuted.replaceAll("```[a-zA-Z]*\\n?", "");
                transmuted = transmuted.replaceAll("```", "");
                transmuted = transmuted.trim();
                
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("  TRANSMUTED CODE");
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println(transmuted);
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println();
                AUDIT.log("code_transmuted", "ollama");
                break;

            case "startserver":
                if (SERVER_THREAD != null && SERVER_THREAD.isAlive()) {
                    System.out.println("   ⚠️  Server already running on port 8080");
                    break;
                }
                if (!OLLAMA_BRAIN.isAvailable()) {
                    System.out.println("   ⚠️  WARNING: Ollama not available. Server will start but transmutations will fail.");
                    System.out.println("   Start Ollama with: ollama serve");
                }
                SERVER_THREAD = new Thread(() -> {
                    try {
                        TRANSMUTER_SERVER.ignite();
                    } catch (Exception e) {
                        System.err.println("   ❌ Server error: " + e.getMessage());
                    }
                });
                SERVER_THREAD.setDaemon(true);
                SERVER_THREAD.start();
                System.out.println();
                System.out.println("⚡ TRANSMUTER SERVER STARTED");
                System.out.println("   Endpoint: http://localhost:8080/transmute");
                System.out.println("   Health: http://localhost:8080/health");
                System.out.println("   Open Fraymus_Transmuter.html to use visual interface");
                System.out.println();
                AUDIT.log("transmuter_started", "port_8080");
                break;

            case "stopserver":
                if (SERVER_THREAD == null || !SERVER_THREAD.isAlive()) {
                    System.out.println("   ⚠️  Server not running");
                    break;
                }
                SERVER_THREAD.interrupt();
                SERVER_THREAD = null;
                System.out.println("   ✓ Transmuter server stopped");
                AUDIT.log("transmuter_stopped", "shutdown");
                break;

            case "exit":
            case "quit":
                System.out.println();
                System.out.println("⚡ Shutting down Fraymus Convergence...");
                System.out.println("   Goodbye.");
                System.exit(0);
                break;

            default:
                System.out.println("Unknown command: " + cmd);
                System.out.println("Type 'help' for available commands");
        }
    }

    private static void handleNetworkMode(String[] args) throws Exception {
        String mode = args[0].toLowerCase();

        switch (mode) {
            case "host":
                if (args.length < 2) {
                    System.err.println("Usage: host <port>");
                    System.exit(1);
                }
                int port = Integer.parseInt(args[1]);
                
                System.out.println("🏥 SLEEVE MODE: Waiting for incoming stacks on port " + port);
                System.out.print("🔑 Enter passphrase for decryption: ");
                
                Scanner sc = new Scanner(System.in);
                char[] hostPass = sc.nextLine().toCharArray();
                
                new Thread(new Sleeve(port, hostPass)).start();
                
                System.out.println("   Listening...");
                while (true) Thread.sleep(1000);

            case "cast":
                if (args.length < 3) {
                    System.err.println("Usage: cast <ip> <stackfile>");
                    System.exit(1);
                }
                String targetIp = args[1];
                String stackFile = args[2];
                
                CorticalStack stack = CorticalStack.loadFromFile(stackFile);
                Needlecast.beam(stack, targetIp, 9999);
                break;

            default:
                System.err.println("Unknown mode: " + mode);
                System.err.println("Available modes: host, cast");
                System.exit(1);
        }
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║   ⚡ FRAYMUS CONVERGENCE ⚡                                    ║");
        System.out.println("║                                                               ║");
        System.out.println("║   Neuro-Symbolic Hybrid Intelligence System                  ║");
        System.out.println("║                                                               ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║   HDC Brain      : HyperFormer (10k-dim XOR logic)           ║");
        System.out.println("║   LLM Spine      : Bicameral Prism (dual-model synthesis)    ║");
        System.out.println("║   🧬 Transmuter  : Ollama-powered code optimization          ║");
        System.out.println("║   Crypto Stack   : AES-256-GCM encrypted persistence         ║");
        System.out.println("║   Network        : Needlecast transmission protocol          ║");
        System.out.println("║   🦞 Claw Spine  : OpenClaw integration (skills + sandbox)   ║");
        System.out.println("║   🧬 Meta-Layer  : Self-coding & Darwinian evolution         ║");
        System.out.println("║   🧠 Neuro-Quant : 10,000D HyperCortex + Omega Point         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  FRAYMUS CONVERGENCE - COMMAND REFERENCE");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("HDC BRAIN (Fast Pattern Recognition):");
        System.out.println("  learn <text>       Learn a sentence (one-shot)");
        System.out.println("  learnfile <path>   Batch learn from text file");
        System.out.println("  predict <context>  Predict next word from context");
        System.out.println();
        System.out.println("LLM SPINE (Deep Reasoning):");
        System.out.println("  ask <question>     Bicameral analysis (logic + creativity)");
        System.out.println();
        System.out.println("BICAMERAL TRANSMUTER (Code Optimization):");
        System.out.println("  transmute <code>   Optimize code via Ollama (left+right brain)");
        System.out.println("  startserver        Start HTTP server on port 8080");
        System.out.println("  stopserver         Stop HTTP server");
        System.out.println();
        System.out.println("OPENCLAW INTEGRATION:");
        System.out.println("  docker <cmd>       Execute command in Docker sandbox");
        System.out.println("  skills             List loaded skills");
        System.out.println("  skill <name>       Show skill details");
        System.out.println("  loadskills <dir>   Load skills from directory");
        System.out.println();
        System.out.println("ADVANCED SKILLS (Φ-Harmonic):");
        System.out.println("  weave <thought>    Write to Obsidian daily note (Φ-resonant)");
        System.out.println("  entangle <A> <B> <text>  Quantum file entanglement");
        System.out.println("  verify <A> <B>     Verify quantum entanglement");
        System.out.println("  phaselock          Check Φ-temporal alignment");
        System.out.println();
        System.out.println("META-COGNITIVE (Self-Coding):");
        System.out.println("  evolve <code>      Evolve code with phi-harmonic enhancement");
        System.out.println("  generate <name>    Generate living code entity");
        System.out.println("  reflect [dir]      Digest codebase into HDC brain");
        System.out.println("  evolveloop <cmd>   Darwinian evolution (start|stop|status)");
        System.out.println("  smartevolve <code> LLM analysis + code evolution");
        System.out.println();
        System.out.println("NEURO-QUANTUM (10,000D Biological Brain):");
        System.out.println("  cortex <cmd>       Control HyperCortex (start|stop|status|query)");
        System.out.println("  inject <concept>   Inject concept into 10,000D lattice");
        System.out.println("  omega              Show Omega Point status");
        System.out.println("  shield <data>      Encrypt data (AES-256-GCM)");
        System.out.println("  brain <fitness>    Optimize fitness (Simulated Annealing)");
        System.out.println("  memory             Seal history (Merkle Tree)");
        System.out.println();
        System.out.println("AEON ABSOLUTE (Multi-Process Swarm Brain):");
        System.out.println("  swarm start        Ignite AEON swarm (spawns N-1 child processes)");
        System.out.println("  swarm stop         Terminate swarm and all children");
        System.out.println("  swarm status       Show swarm entropy and core saturation");
        System.out.println();
        System.out.println("FRAYNIX OS VISUALIZATION:");
        System.out.println("  visualize          Launch FRAYNIX OS (4D tesseract brain)");
        System.out.println("  openclaw           Launch OpenClaw Core (WebGL)");
        System.out.println("  openclaw-native    Launch OpenClaw NATIVE (Pure Java DMA)");
        System.out.println("  aeon-benchmark     Launch AEON OMNI Benchmark (685B Diffusion-HRM)");
        System.out.println("  genesis <intent>   Genesis Architect code generation");
        System.out.println("  dreamstate [cmd]   Enter/exit DreamState optimization");
        System.out.println();
        System.out.println("VOCABULARY MANAGEMENT:");
        System.out.println("  vocab              Show vocabulary statistics");
        System.out.println("  export <file>      Export vocabulary to file");
        System.out.println("  prune <size>       Reduce vocabulary to top N words");
        System.out.println();
        System.out.println("PERSISTENCE:");
        System.out.println("  mint               Create encrypted cortical stack");
        System.out.println("  load <file>        Load encrypted stack");
        System.out.println();
        System.out.println("NETWORK:");
        System.out.println("  cast <ip> <file>   Transmit stack to remote host");
        System.out.println();
        System.out.println("SYSTEM:");
        System.out.println("  stats              Show system statistics");
        System.out.println("  context            Show recent context window");
        System.out.println("  clear              Clear context window");
        System.out.println("  reset              Reset brain to initial state");
        System.out.println("  id [name]          View/set identity");
        System.out.println("  help               Show this help");
        System.out.println("  exit               Shutdown");
        System.out.println();
        System.out.println("NETWORK MODES (command-line):");
        System.out.println("  java -jar app.jar host <port>      Start receiver");
        System.out.println("  java -jar app.jar cast <ip> <file> Send stack");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
    }

    private static void printStats() {
        System.out.println();
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│ FRAYMUS CONVERGENCE - SYSTEM STATISTICS                   │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│ Identity:        " + String.format("%-40s", IDENTITY) + "│");
        System.out.println("│ HDC Vocabulary:  " + String.format("%-40d", HDC_BRAIN.vocabSize()) + "│");
        System.out.println("│ HDC Memory:      " + String.format("%-40d", HDC_BRAIN.memoryWeight()) + "│");
        System.out.println("│ Total Learned:   " + String.format("%-40d", totalLearned) + "│");
        System.out.println("│ Total Predictions: " + String.format("%-38d", totalPredictions) + "│");
        System.out.println("│ Context Size:    " + String.format("%-40d", CONTEXT_WINDOW.size()) + "│");
        System.out.println("│ LLM Status:      " + String.format("%-40s", LLM_SPINE.isReady() ? "READY" : "OFFLINE") + "│");
        System.out.println("│ LLM Config:      " + String.format("%-40s", LLM_SPINE.getConfiguration()) + "│");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void printVocabStats() {
        System.out.println();
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│ VOCABULARY STATISTICS                                      │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│ Total Words:     " + String.format("%-40d", HDC_BRAIN.vocabSize()) + "│");
        System.out.println("│ Memory Weight:   " + String.format("%-40d", HDC_BRAIN.memoryWeight()) + "│");
        System.out.println("│ Avg Weight/Word: " + String.format("%-40.2f", 
            HDC_BRAIN.vocabSize() > 0 ? (double)HDC_BRAIN.memoryWeight() / HDC_BRAIN.vocabSize() : 0.0) + "│");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void printContext() {
        System.out.println();
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│ CONTEXT WINDOW (Last " + MAX_CONTEXT + " interactions)                        │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        
        if (CONTEXT_WINDOW.isEmpty()) {
            System.out.println("│ (empty)                                                    │");
        } else {
            for (int i = 0; i < CONTEXT_WINDOW.size(); i++) {
                String ctx = CONTEXT_WINDOW.get(i);
                if (ctx.length() > 56) {
                    ctx = ctx.substring(0, 53) + "...";
                }
                System.out.println("│ " + (i + 1) + ". " + String.format("%-55s", ctx) + "│");
            }
        }
        
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static void updateContext(String text) {
        CONTEXT_WINDOW.add(text);
        if (CONTEXT_WINDOW.size() > MAX_CONTEXT) {
            CONTEXT_WINDOW.remove(0);
        }
    }

    private static void learnFromFile(String filepath) {
        try {
            System.out.println("📖 Reading file: " + filepath);
            String content = Files.readString(Paths.get(filepath));
            
            // Split into sentences
            String[] sentences = content.split("[.!?]+");
            int totalTokens = 0;
            
            System.out.println("   Processing " + sentences.length + " sentences...");
            
            for (String sentence : sentences) {
                sentence = sentence.trim();
                if (sentence.isEmpty()) continue;
                
                String[] words = sentence.split("\\s+");
                if (words.length > 0) {
                    HDC_BRAIN.learnSentence(words);
                    totalTokens += words.length;
                }
            }
            
            totalLearned += totalTokens;
            System.out.println("   ✓ Learned " + totalTokens + " tokens from " + sentences.length + " sentences");
            AUDIT.log("batch_learn", filepath + " (" + totalTokens + " tokens)");
            
        } catch (IOException e) {
            System.err.println("   ❌ Error reading file: " + e.getMessage());
        }
    }

    private static void exportVocabulary(String filename) {
        try {
            System.out.println("💾 Exporting vocabulary to: " + filename);
            
            // Note: HyperFormer doesn't expose vocabulary directly
            // We'll export statistics instead
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("# Fraymus Convergence - Vocabulary Export");
                writer.println("# Identity: " + IDENTITY);
                writer.println("# Timestamp: " + java.time.Instant.now());
                writer.println();
                writer.println("Vocabulary Size: " + HDC_BRAIN.vocabSize());
                writer.println("Memory Weight: " + HDC_BRAIN.memoryWeight());
                writer.println("Total Learned: " + totalLearned);
                writer.println("Total Predictions: " + totalPredictions);
                writer.println();
                writer.println("# Note: Individual word vectors are not exported");
                writer.println("# Use 'mint' command to create encrypted brain snapshot");
            }
            
            System.out.println("   ✓ Vocabulary stats exported");
            AUDIT.log("vocab_export", filename);
            
        } catch (IOException e) {
            System.err.println("   ❌ Error exporting: " + e.getMessage());
        }
    }

    private static void pruneVocabulary(int targetSize) {
        int currentSize = HDC_BRAIN.vocabSize();
        
        if (currentSize <= targetSize) {
            System.out.println("   ℹ Current vocabulary (" + currentSize + ") is already at or below target (" + targetSize + ")");
            return;
        }
        
        System.out.println("⚠️  WARNING: Vocabulary pruning requires brain reset");
        System.out.println("   Current size: " + currentSize);
        System.out.println("   Target size: " + targetSize);
        System.out.println();
        System.out.println("   Note: HyperFormer doesn't support selective pruning.");
        System.out.println("   Consider using 'mint' to save current state, then 'reset' to start fresh.");
        System.out.println("   Or continue learning - the brain will naturally optimize.");
    }
    
    /**
     * Extract tool command from LLM response
     */
    private static String extractToolCommand(String response, String toolPrefix) {
        int startIdx = response.indexOf(toolPrefix);
        if (startIdx == -1) return null;
        
        startIdx += toolPrefix.length();
        
        // Skip whitespace
        while (startIdx < response.length() && Character.isWhitespace(response.charAt(startIdx))) {
            startIdx++;
        }
        
        // Find end of command (newline or end of string)
        int endIdx = response.indexOf('\n', startIdx);
        if (endIdx == -1) {
            endIdx = response.length();
        }
        
        return response.substring(startIdx, endIdx).trim();
    }
}
