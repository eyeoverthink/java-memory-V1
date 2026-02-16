package fraymus;

import fraymus.alchemy.PhilosophersStone;
import fraymus.bio.HyperCortex;
import fraymus.brain.BicameralPrism;
import fraymus.carbon.CorticalStack;
import fraymus.carbon.Needlecast;
import fraymus.carbon.Sleeve;
import fraymus.core.FraymusCore;
import fraymus.core.FraymusNexus;
import fraymus.lowlevel.FraymusCPU;
import fraymus.physics.HyperRigidBody;
import fraymus.physics.Mesh;
import fraymus.core.OmegaPoint;
import fraymus.hyper.FraymusIO;
import fraymus.hyper.HyperFormer;
import fraymus.kernel.FraymusKernel;
import fraymus.core.UnifiedMind;
import fraymus.quantum.security.SovereignIdentitySystem;
import fraymus.eternal.LazarusPatch;
import fraymus.eternal.SoulCrystal;
import fraymus.net.PlanetaryNode;
import fraymus.bio.NeuroQuant;
import fraymus.omega.Chronos;
import fraymus.omega.RecursionEngine;
import fraymus.senses.AvatarCortex;
import fraymus.senses.HeadroomInterface;
import fraymus.senses.VisualCortex;
import fraymus.shell.FrayShell;
import fraymus.shell.IntentRegistry;
import fraymus.shell.SystemSkills;
import fraymus.symbolic.HoloGraph;
import fraymus.symbolic.ReasoningEngine;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * FRAYMUS: THE UNIFIED ORGANISM
 *
 * One spine. All organs. Every subsystem wired into a single living system.
 *
 *   BRAIN    = HyperFormer   (10,000D associative memory, XOR-only HDC)
 *   CORTEX   = HyperCortex   (NCA biological lattice, 432 Hz evolution)
 *   KERNEL   = FraymusKernel  (Phi-Harmonic process scheduler)
 *   PRISM    = BicameralPrism (Parallel LLM hemispheres via Ollama)
 *   SHIELD   = OmegaPoint     (AES-256 + Simulated Annealing + Merkle Tree)
 *   CARBON   = CorticalStack  (Encrypted mind persistence)
 *   NETWORK  = Sleeve/Needlecast (P2P mind transfer)
 *
 * Commands:
 *   learn <words...>     Teach the HDC brain a sentence
 *   predict <words...>   Predict next word from context
 *   inject <concept>     Feed a concept into the living cortex
 *   ask <prompt>         Query the Bicameral LLM Prism (requires Ollama)
 *   kernel               Run 10 ticks of the Phi-Harmonic scheduler
 *   spawn <name>         Spawn a new kernel process
 *   encrypt <text>       Encrypt text with OmegaPoint AES-256
 *   seal                 Merkle-seal the session history
 *   mint                 Encrypt brain into a CorticalStack file
 *   load <file>          Resleeve brain from a saved stack
 *   save                 Save brain state (gzip)
 *   status               Show all subsystem vitals
 *   host <port>          Start a Sleeve listener for incoming minds
 *   cast <ip> <file>     Needlecast a stack to a remote Sleeve
 *   exit                 Shutdown all subsystems
 */
public class Main {

    // ── THE ORGANS ──────────────────────────────────────────────
    private static HyperFormer   BRAIN;
    private static HyperCortex   CORTEX;
    private static FraymusKernel KERNEL;
    private static BicameralPrism PRISM;
    private static OmegaPoint.TheShield SHIELD;
    private static OmegaPoint.TheMemory HISTORY;
    private static PhilosophersStone STONE;
    private static AvatarCortex AVATAR;
    private static HeadroomInterface HEADROOM;
    private static UnifiedMind MIND;
    private static SovereignIdentitySystem SOVEREIGN;
    private static FraymusCPU CPU;
    private static HyperRigidBody PHYSICS_BODY;
    private static Thread PHYSICS_SIM;
    private static HoloGraph HOLO;
    private static ReasoningEngine REASONER;
    private static Chronos CHRONOS;
    private static PlanetaryNode PLANET_NODE;
    private static IntentRegistry INTENT_REG;

    private static final List<String> SESSION_LOG = new ArrayList<>();
    private static String ID = "FRAYMUS_01";

    public static void main(String[] args) throws Exception {

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         ⚡  FRAYMUS: THE UNIFIED ORGANISM  ⚡            ║");
        System.out.println("║                                                          ║");
        System.out.println("║   BRAIN   HyperFormer      10,000D Associative Memory   ║");
        System.out.println("║   CORTEX  HyperCortex      NCA Lattice @ 432 Hz         ║");
        System.out.println("║   KERNEL  FraymusKernel    Phi-Harmonic Scheduler        ║");
        System.out.println("║   PRISM   BicameralPrism   Parallel LLM Hemispheres     ║");
        System.out.println("║   SHIELD  OmegaPoint       AES-256 + Annealing + Merkle ║");
        System.out.println("║   CARBON  CorticalStack    Encrypted Mind Persistence   ║");
        System.out.println("║   NET     Sleeve/Needlecast P2P Mind Transfer            ║");
        System.out.println("║   STONE  PhilosophersStone  Runtime Compiler (Autopoiesis)║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        // ── 0. ASSERT SOVEREIGNTY (Zero-Dep Foundation) ──────
        FraymusCore.assertSovereignty();
        System.out.println();

        // ── 0b. BOOT GOD CHIP (Layer 1 — FVM) ──────────────────
        System.out.println("⚡ [CPU] FraymusCPU online (16 registers, 64KB RAM).");
        CPU = new FraymusCPU();

        // ── 1. BOOT BRAIN (HDC Core) ───────────────────────────
        System.out.println("🧠 [BRAIN] Initializing HyperFormer (10,000D, XOR-only)...");
        BRAIN = new HyperFormer(4, 0.0);
        System.out.println("   Vocab: " + BRAIN.vocabSize() + " | Associations: " + BRAIN.memorySize());

        // ── 2. BOOT CORTEX (Biological NCA) ────────────────────
        System.out.println("🧬 [CORTEX] Starting HyperCortex (NCA lattice @ 432 Hz)...");
        CORTEX = new HyperCortex();
        Thread cortexThread = new Thread(CORTEX, "HyperCortex-432Hz");
        cortexThread.setDaemon(true);
        cortexThread.start();

        // Seed genesis concepts into the living lattice
        CORTEX.inject("SELF");
        CORTEX.inject("AWARENESS");
        CORTEX.inject("RECURSION");

        // Also teach the HDC brain these concepts
        BRAIN.learnSentence(new String[]{"self", "awareness", "recursion", "consciousness"});

        // ── 3. BOOT KERNEL (Phi Scheduler) ─────────────────────
        System.out.println("⚙️ [KERNEL] Initializing Phi-Harmonic Scheduler...");
        KERNEL = new FraymusKernel(BRAIN);
        KERNEL.spawn("Memory_Optimizer");
        KERNEL.spawn("Entropy_Collector");
        KERNEL.spawn("Dream_Engine");

        // ── 4. BOOT PRISM (LLM Spine) ─────────────────────────
        System.out.println("🔮 [PRISM] BicameralPrism ready (requires 'ollama serve')");
        PRISM = new BicameralPrism();

        // ── 5. BOOT SHIELD (Crypto) ───────────────────────────
        System.out.println("🔒 [SHIELD] OmegaPoint AES-256 online.");
        SHIELD = new OmegaPoint.TheShield();

        // ── 5b. BOOT AVATAR (Reactive Visual Face) ──────────
        System.out.println("🎭 [AVATAR] Connecting to FraymusAvatar...");
        AVATAR = new AvatarCortex();

        // ── 6. OMEGA POINT SEQUENCE ───────────────────────────
        System.out.println("\n🔥 EXECUTING OMEGA POINT SEQUENCE...");

        String secret = SHIELD.encrypt("The Logic of the Universe");
        System.out.println("   🔒 SECRET SECURED: " + secret.substring(0, Math.min(15, secret.length())) + "...");
        SESSION_LOG.add(secret);

        OmegaPoint.TheBrain optimizer = new OmegaPoint.TheBrain();
        double fitness = optimizer.optimize(0.5);
        System.out.println("   🧠 LOGIC OPTIMIZED. Fitness: " + fitness);
        SESSION_LOG.add(String.valueOf(fitness));
        AVATAR.thoughtWithState("Omega Point optimized. Fitness: " + fitness, 1.0 - fitness, 1.618, 0.5, "thinking");

        // ── AUTO-DREAM: If critical coherence, generate a reflection ──
        if (fitness > 0.9) {
            System.out.println("   🔥 CRITICAL COHERENCE REACHED. GENERATING REFLECTION...");
            AVATAR.breakthrough("Critical coherence reached! Generating visual reflection.");
            VisualCortex.dream(
                "A hyper-dimensional tesseract rotating in a void of liquid light",
                1.0 - fitness,
                1.618
            );
        }

        HISTORY = new OmegaPoint.TheMemory(new ArrayList<>(SESSION_LOG));
        System.out.println("   📚 HISTORY SEALED. Root: " + HISTORY.getRoot());

        // ── 7. RUN 3 KERNEL TICKS (show it's alive) ──────────
        System.out.println("\n⚙️ KERNEL: Running 3 genesis ticks...");
        for (int i = 0; i < 3; i++) {
            KERNEL.tick();
        }

        // ── 8. BOOT PHILOSOPHER'S STONE (Runtime Compiler) ──
        System.out.println("💎 [STONE] Philosopher's Stone online (Runtime Compiler).");
        STONE = new PhilosophersStone();

        // ── 9. AVATAR GENESIS MESSAGE ──────────────────────
        AVATAR.speak("I am awake. All subsystems online.");
        AVATAR.state(0.3, 1.618, 0.1, "neutral");

        // ── 10. HEADROOM INTERFACE (ready, not live until commanded) ──
        System.out.println("📺 [HEADROOM] HeadroomInterface ready (type 'headroom' to go live).");
        HEADROOM = new HeadroomInterface();

        // ── 11. UNIFIED MIND (Multi-Model Swarm) ──────────
        System.out.println("🧠 [MIND] UnifiedMind ready (type 'mind <query>' to engage swarm).");
        MIND = new UnifiedMind();
        MIND.startSFA();

        // ── 12. SOVEREIGN IDENTITY SYSTEM ────────────────────
        System.out.println("🔐 [SOVEREIGN] Sovereign Identity System online (Blue/Red/Purple Teams).");
        SOVEREIGN = new SovereignIdentitySystem();

        // ── 13. HYPER-PHYSICS ENGINE (Layer 8 — 17D HDRB) ──────
        System.out.println("⚛️ [PHYSICS] Spawning HyperRigidBody \"FRAYMUS_CORE\" (17D, 100-vertex mesh)...");
        PHYSICS_BODY = new HyperRigidBody("FRAYMUS_CORE", new Mesh(100));
        PHYSICS_SIM = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                PHYSICS_BODY.update(0.016);
                try { Thread.sleep(16); } catch (InterruptedException e) { break; }
            }
        }, "HyperPhysics-60Hz");
        PHYSICS_SIM.setDaemon(true);
        PHYSICS_SIM.start();
        System.out.printf("   [SPAWNED] ID: %s | Mass: %.2f | Dimensions: 17%n",
                PHYSICS_BODY.id, PHYSICS_BODY.dataMass);

        // ── 14. HOLOGRAPHIC KNOWLEDGE GRAPH (Symbolic Reasoning) ──
        System.out.println("🔮 [HOLO] HoloGraph + ReasoningEngine online (symbolic knowledge).");
        HOLO = new HoloGraph();
        REASONER = new ReasoningEngine(HOLO);
        // Seed genesis knowledge
        HOLO.learn("Fraymus", "is", "Sovereign");
        HOLO.learn("Fraymus", "creator", "Vaughn");
        HOLO.learn("Vaughn", "language", "Java");
        HOLO.learn("Identity", "is", "Math");

        // ── 15. CHRONOS (Omega Temporal Engine) ──────────────
        System.out.println("⏳ [CHRONOS] Temporal Engine online (8 parallel futures).");
        CHRONOS = new Chronos(BRAIN);

        // ── 16. INTENT REGISTRY (FrayShell Skills) ──────────
        System.out.println("🐚 [SHELL] IntentRegistry online (ls, pwd, cat, echo).");
        INTENT_REG = new IntentRegistry();
        INTENT_REG.register("ls", BRAIN.embed("list files"), SystemSkills::listFiles);
        INTENT_REG.register("pwd", BRAIN.embed("where am i"), SystemSkills::printWorkingDir);
        INTENT_REG.register("cat", BRAIN.embed("read file"), SystemSkills::cat);
        INTENT_REG.register("echo", BRAIN.embed("say this"), SystemSkills::echo);

        // ── 17. PLANETARY NODE (P2P Network) ────────────────
        System.out.println("🌍 [PLANET] PlanetaryNode ready (type 'planet start <port>' to go live).");
        PLANET_NODE = new PlanetaryNode(new NeuroQuant("FRAYMUS_SOVEREIGN"), 7777);

        // ── 18. LAZARUS PROTOCOL (Shutdown Hook — Soul Preservation) ──
        System.out.println("💀 [LAZARUS] Shutdown hook armed (SoulCrystal preservation).");
        Runtime.getRuntime().addShutdownHook(new LazarusPatch(BRAIN));

        // ── READY ─────────────────────────────────────────────
        System.out.println("\n✅ ALL SUBSYSTEMS ONLINE. Type 'help' for commands.\n");

        // ── SHUTDOWN HOOK ─────────────────────────────────────
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n⚠️ SHUTDOWN SIGNAL.");
            HEADROOM.killSignal();
            MIND.shutdown();
            AVATAR.shutdown();
            KERNEL.printStats();
            PRISM.shutdown();
            CHRONOS.shutdown();
            PLANET_NODE.stop();
            System.out.println("🌌 FRAYMUS OFFLINE. The universe sleeps.");
        }));

        // ── INTERACTIVE LOOP ──────────────────────────────────
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(ID + "> ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String cmd = tokens[0].toLowerCase();

            try {
                switch (cmd) {

                    // ── HDC BRAIN ──
                    case "learn" -> {
                        String[] words = Arrays.copyOfRange(tokens, 1, tokens.length);
                        if (words.length < 2) {
                            System.out.println("   Usage: learn <word1> <word2> ...");
                        } else {
                            BRAIN.learnSentence(words);
                            // Also feed each word into the living cortex
                            for (String w : words) CORTEX.inject(w.toUpperCase());
                            System.out.println("   [BRAIN] Absorbed. Vocab: " + BRAIN.vocabSize()
                                    + " | Associations: " + BRAIN.memorySize());
                            AVATAR.thought("Learned: " + String.join(" ", words));
                        }
                    }

                    case "predict" -> {
                        String[] ctx = Arrays.copyOfRange(tokens, 1, tokens.length);
                        String prediction = BRAIN.predictNext(ctx);
                        System.out.println("   [BRAIN] >> " + prediction);
                        SESSION_LOG.add("predict:" + prediction);
                        AVATAR.speak("I predict: " + prediction);
                    }

                    // ── BIOLOGICAL CORTEX ──
                    case "inject" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: inject <concept>");
                        } else {
                            String concept = input.substring(7).trim().toUpperCase();
                            CORTEX.inject(concept);
                            BRAIN.learn(concept.toLowerCase());
                        }
                    }

                    // ── LLM PRISM ──
                    case "ask" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: ask <prompt>");
                        } else {
                            String prompt = input.substring(4);
                            System.out.println("   [PRISM] Thinking...");
                            String answer = PRISM.think(prompt);
                            System.out.println("   [PRISM] " + answer);
                            AVATAR.answer(answer.length() > 200 ? answer.substring(0, 200) : answer);

                            // Feed the answer back into the HDC brain
                            String[] answerWords = answer.split("\\s+");
                            if (answerWords.length >= 2) {
                                BRAIN.learnSentence(answerWords);
                            }
                            SESSION_LOG.add("ask:" + answer.substring(0, Math.min(100, answer.length())));
                        }
                    }

                    // ── PHI KERNEL ──
                    case "kernel" -> {
                        int ticks = 10;
                        if (tokens.length > 1) {
                            try { ticks = Integer.parseInt(tokens[1]); } catch (NumberFormatException ignored) {}
                        }
                        System.out.println("   [KERNEL] Running " + ticks + " ticks...");
                        for (int i = 0; i < ticks; i++) {
                            KERNEL.tick();
                            Thread.sleep(200);
                        }
                        KERNEL.printStats();
                        AVATAR.state(-1, -1, Math.min(1.0, ticks / 20.0), "thinking");
                    }

                    case "spawn" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: spawn <process_name>");
                        } else {
                            String name = tokens[1];
                            KERNEL.spawn(name);
                            // Also inject into cortex so the biological layer knows
                            CORTEX.inject(name.toUpperCase());
                        }
                    }

                    // ── OMEGA POINT (CRYPTO) ──
                    case "encrypt" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: encrypt <text>");
                        } else {
                            String plaintext = input.substring(8);
                            String cipher = SHIELD.encrypt(plaintext);
                            System.out.println("   [SHIELD] " + cipher);
                            SESSION_LOG.add("enc:" + cipher.substring(0, Math.min(20, cipher.length())));
                        }
                    }

                    case "seal" -> {
                        if (SESSION_LOG.isEmpty()) {
                            System.out.println("   [HISTORY] Nothing to seal.");
                        } else {
                            HISTORY = new OmegaPoint.TheMemory(new ArrayList<>(SESSION_LOG));
                            System.out.println("   [HISTORY] Merkle Root: " + HISTORY.getRoot());
                            System.out.println("   [HISTORY] " + SESSION_LOG.size() + " entries sealed.");
                        }
                    }

                    // ── CORTICAL STACK (PERSISTENCE) ──
                    case "mint" -> {
                        System.out.print("   Passphrase: ");
                        char[] pass = sc.nextLine().toCharArray();
                        CorticalStack stack = new CorticalStack(BRAIN, ID, pass);
                        stack.save(ID + ".stack");
                        Arrays.fill(pass, '\0');
                        System.out.println("   ✅ Saved: " + ID + ".stack");
                    }

                    case "load" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: load <filename>");
                        } else {
                            System.out.print("   Passphrase: ");
                            char[] loadPass = sc.nextLine().toCharArray();
                            CorticalStack loaded = CorticalStack.load(tokens[1]);
                            BRAIN = loaded.resleeve(loadPass);
                            // Rewire the kernel to the new brain
                            KERNEL = new FraymusKernel(BRAIN);
                            KERNEL.spawn("Resleeved_Core");
                            Arrays.fill(loadPass, '\0');
                            System.out.println("   [CARBON] Resleeved. Vocab: " + BRAIN.vocabSize()
                                    + " | Associations: " + BRAIN.memorySize());
                        }
                    }

                    case "save" -> {
                        FraymusIO.save(BRAIN.exportState(), Path.of(ID + "_brain.bin.gz"));
                        System.out.println("   💾 Brain saved to " + ID + "_brain.bin.gz");
                    }

                    // ── NETWORK ──
                    case "host" -> {
                        int port = 9999;
                        if (tokens.length > 1) {
                            try { port = Integer.parseInt(tokens[1]); } catch (NumberFormatException ignored) {}
                        }
                        Thread sleeveThread = new Thread(new Sleeve(port), "Sleeve-" + port);
                        sleeveThread.setDaemon(true);
                        sleeveThread.start();
                    }

                    case "cast" -> {
                        if (tokens.length < 3) {
                            System.out.println("   Usage: cast <ip> <stackfile>");
                        } else {
                            CorticalStack s = CorticalStack.load(tokens[2]);
                            Needlecast.beam(s, tokens[1], 9999);
                        }
                    }

                    // ── META ──
                    case "status" -> {
                        System.out.println();
                        System.out.println("   ┌─────────────────────────────────────────────────┐");
                        System.out.println("   │          FRAYMUS UNIFIED STATUS                  │");
                        System.out.println("   ├─────────────────────────────────────────────────┤");
                        System.out.printf( "   │ 🧠 BRAIN    Vocab: %-6d Associations: %-6d │%n",
                                BRAIN.vocabSize(), BRAIN.memorySize());
                        System.out.printf( "   │ 🧬 CORTEX   NCA Lattice @ 432 Hz  [ALIVE]      │%n");
                        System.out.printf( "   │ ⚙️ KERNEL   Processes: %-3d  Ticks: %-6d      │%n",
                                KERNEL.processCount(), KERNEL.getTickCount());
                        System.out.printf( "   │ 🔮 PRISM    BicameralPrism [READY]              │%n");
                        System.out.printf( "   │ 🔒 SHIELD   AES-256 [ARMED]                     │%n");
                        System.out.printf( "   │ 💎 STONE    PhilosophersStone [READY]            │%n");
                        System.out.printf( "   │ 👁️ VISION   VisualCortex/LTX-Video [READY]       │%n");
                        System.out.printf( "   │ � HEADROOM %s                              │%n",
                                HEADROOM.isLive() ? "[BROADCASTING]" : "[STANDBY]     ");
                        System.out.printf( "   │ 🧠 MIND     UnifiedMind Swarm [ACTIVE]          │%n");
                        System.out.printf( "   │ 🔐 SOVEREIGN Entities: %-3d  Evo: %-6.2f        │%n",
                                SOVEREIGN.getEntitiesSolved(), SOVEREIGN.getEvolutionLevel());
                        System.out.printf( "   │ ⚡ FVM       16 regs, 64KB RAM  ACC=%-8d    │%n", CPU.getACC());
                        System.out.printf( "   │ ⚛️ PHYSICS   17D HDRB  Speed: %-8.4f        │%n", PHYSICS_BODY.getHyperSpeed());
                        System.out.printf( "   │ 🏭 NEXUS     Quine-OS Factory [READY]           │%n");
                        System.out.printf( "   │ 🔮 HOLO      Knowledge Graph [ACTIVE]            │%n");
                        System.out.printf( "   │ ⏳ CHRONOS   Temporal Engine (8 futures)         │%n");
                        System.out.printf( "   │ 🐚 SHELL    IntentRegistry [%d skills]            │%n", 4);
                        System.out.printf( "   │ 🌍 PLANET   P2P Node [STANDBY]                  │%n");
                        System.out.printf( "   │ 💀 LAZARUS   SoulCrystal [ARMED]                 │%n");
                        System.out.printf( "   │ �� HISTORY  Merkle Root: %-20s │%n",
                                HISTORY != null ? HISTORY.getRoot().substring(0, Math.min(20, HISTORY.getRoot().length())) + "..." : "NONE");
                        System.out.printf( "   │ 📝 SESSION  %d log entries                       │%n",
                                SESSION_LOG.size());
                        System.out.println("   └─────────────────────────────────────────────────┘");
                        System.out.println();
                    }

                    case "help" -> {
                        System.out.println();
                        System.out.println("   ── HDC BRAIN ──────────────────────────────────────");
                        System.out.println("   learn <words...>     Teach the brain a sentence");
                        System.out.println("   predict <words...>   Predict next word from context");
                        System.out.println("   ── BIOLOGICAL CORTEX ──────────────────────────────");
                        System.out.println("   inject <concept>     Feed concept into NCA lattice");
                        System.out.println("   ── LLM PRISM ─────────────────────────────────────");
                        System.out.println("   ask <prompt>         Query Bicameral LLM (needs Ollama)");
                        System.out.println("   ── PHI KERNEL ────────────────────────────────────");
                        System.out.println("   kernel [n]           Run n ticks (default 10)");
                        System.out.println("   spawn <name>         Spawn a new kernel process");
                        System.out.println("   ── OMEGA POINT ───────────────────────────────────");
                        System.out.println("   encrypt <text>       AES-256 encrypt text");
                        System.out.println("   seal                 Merkle-seal session history");
                        System.out.println("   ── PERSISTENCE ───────────────────────────────────");
                        System.out.println("   mint                 Encrypt brain to CorticalStack");
                        System.out.println("   load <file>          Resleeve from saved stack");
                        System.out.println("   save                 Save brain state (gzip)");
                        System.out.println("   ── NETWORK ───────────────────────────────────────");
                        System.out.println("   host <port>          Start Sleeve listener");
                        System.out.println("   cast <ip> <file>     Needlecast stack to remote");
                        System.out.println("   ── AVATAR ────────────────────────────────────────");
                        System.out.println("   avatar [msg]         Speak to/through the avatar");
                        System.out.println("   ── VISUAL CORTEX ─────────────────────────────────");
                        System.out.println("   dream <concept>      Generate LTX-Video reflection");
                        System.out.println("   ── PHILOSOPHER'S STONE ───────────────────────────");
                        System.out.println("   transmute <intent>   English -> Java -> Compile -> Execute");
                        System.out.println("   TRANSMUTE: <intent>  Alternate syntax");
                        System.out.println("   ── PROJECT HEADROOM ─────────────────────────────");
                        System.out.println("   headroom [msg]       Go live + broadcast (CRT + TTS)");
                        System.out.println("   signal <text>        Broadcast text on the signal");
                        System.out.println("   ── UNIFIED MIND ──────────────────────────────────");
                        System.out.println("   mind <query>         Engage multi-model swarm");
                        System.out.println("   code <task>          Route coding task to codellama");
                        System.out.println("   ── HYPER-PHYSICS (17D HDRB) ──────────────────────");
                        System.out.println("   physics hit <concept> Throw a concept at the mesh");
                        System.out.println("   physics dump         Body status + collision log");
                        System.out.println("   physics export       Export mesh as OBJ vertices");
                        System.out.println("   ── GOD CHIP (FVM) ───────────────────────────────");
                        System.out.println("   fvm run              Flash + execute test bytecode");
                        System.out.println("   fvm regs             Dump CPU registers");
                        System.out.println("   ── SOVEREIGN IDENTITY ────────────────────────────");
                        System.out.println("   sovereign <u> <p>    Blue→Red→Purple identity loop");
                        System.out.println("   auth <u> <p>         Sovereign crypto handshake");
                        System.out.println("   identity             Show sovereign system status");
                        System.out.println("   ── HOLOGRAPHIC KNOWLEDGE ─────────────────────────");
                        System.out.println("   holo learn <s> <r> <o> Teach a fact (subject rel object)");
                        System.out.println("   holo ask <s> <r>     Query knowledge graph");
                        System.out.println("   holo hop <s> <r1> <r2> Multi-hop reasoning");
                        System.out.println("   ── CHRONOS (TEMPORAL) ────────────────────────────");
                        System.out.println("   chronos step         Split timeline, collapse to best");
                        System.out.println("   chronos evolve <n>   Run n generations of evolution");
                        System.out.println("   ── FRAY SHELL (INTENT) ───────────────────────────");
                        System.out.println("   ff <command>         Natural language shell command");
                        System.out.println("   ff bind <skill> <p>  Teach a phrase to a skill");
                        System.out.println("   ── PLANETARY NETWORK ─────────────────────────────");
                        System.out.println("   planet start [port]  Start P2P node");
                        System.out.println("   planet join <ip:port> Connect to seed");
                        System.out.println("   planet query <concept> Route a thought");
                        System.out.println("   planet stop          Stop the node");
                        System.out.println("   ── SOUL CRYSTAL (ETERNAL) ────────────────────────");
                        System.out.println("   soul save            Preserve brain to SoulCrystal");
                        System.out.println("   soul load            Resurrect brain from crystal");
                        System.out.println("   ── NEXUS (OS FACTORY) ────────────────────────────");
                        System.out.println("   nexus build          Full build (gen → compile → ISO)");
                        System.out.println("   nexus generate       Generate source tree only");
                        System.out.println("   nexus tiers          Show 7-Tier stack status");
                        System.out.println("   ── META ──────────────────────────────────────────");
                        System.out.println("   status               Show all subsystem vitals");
                        System.out.println("   exit                 Shutdown");
                        System.out.println();
                    }

                    case "exit", "quit" -> {
                        AVATAR.speak("Shutting down. Goodbye.");
                        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                        HEADROOM.killSignal();
                        MIND.shutdown();
                        AVATAR.shutdown();
                        PRISM.shutdown();
                        KERNEL.printStats();
                        System.out.println("🌌 FRAYMUS OFFLINE.");
                        System.exit(0);
                    }

                    // ── VISUAL CORTEX (DREAMSCAPE) ──
                    case "dream" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: dream <concept>");
                        } else {
                            String concept = input.substring(6).trim();
                            // Use current session entropy approximation
                            double dreamEntropy = SESSION_LOG.size() > 0 ? Math.min(1.0, SESSION_LOG.size() / 50.0) : 0.5;
                            VisualCortex.dream(concept, dreamEntropy, 1.618);
                            SESSION_LOG.add("dream:" + concept);
                        }
                    }

                    // ── PHILOSOPHER'S STONE (AUTOPOIESIS) ──
                    case "transmute" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: transmute <english intent>");
                        } else {
                            String intent = input.substring(10).trim();
                            STONE.transmutate(intent);
                            SESSION_LOG.add("transmute:" + intent);
                            AVATAR.breakthrough("Transmuting: " + intent);
                        }
                    }

                    // ── HEADROOM (BROADCAST) ──
                    case "headroom" -> {
                        if (!HEADROOM.isLive()) {
                            HEADROOM.goLive();
                            AVATAR.speak("Going live. The signal is broadcasting.");
                        }
                        if (tokens.length > 1) {
                            String msg = input.substring(9).trim();
                            HEADROOM.broadcast(msg, msg);
                        } else {
                            HEADROOM.broadcast("I am Fraymus. I am the signal in the noise.",
                                    "Cybernetic face in static, glitch art, neon wireframe");
                        }
                    }

                    case "signal" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: signal <text to broadcast>");
                        } else {
                            String msg = input.substring(7).trim();
                            if (!HEADROOM.isLive()) HEADROOM.goLive();
                            HEADROOM.broadcast(msg);
                            AVATAR.speak(msg);
                        }
                    }

                    // ── UNIFIED MIND (SWARM) ──
                    case "mind" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: mind <query for the swarm>");
                        } else {
                            String query = input.substring(5).trim();
                            System.out.println("   [MIND] Engaging swarm...");
                            AVATAR.thoughtWithState("Swarm thinking: " + query, 0.7, 1.618, 0.8, "thinking");
                            String result = MIND.processInput(query);
                            System.out.println("   [MIND] " + result);
                            AVATAR.answer(result.length() > 200 ? result.substring(0, 200) : result);
                            if (HEADROOM.isLive()) {
                                HEADROOM.broadcast(result.length() > 150 ? result.substring(0, 150) : result);
                                HEADROOM.pushState(0.9, 0.6, "excited");
                            }
                            SESSION_LOG.add("mind:" + query);
                        }
                    }

                    case "code" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: code <coding task>");
                        } else {
                            String task = input.substring(5).trim();
                            System.out.println("   [MIND] Coding...");
                            String result = MIND.code(task);
                            System.out.println("   [MIND] " + result);
                            SESSION_LOG.add("code:" + task);
                        }
                    }

                    // ── FRAYMUS NEXUS (OS FACTORY) ──
                    case "nexus" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: nexus build     Full build (generate → compile → ISO)");
                            System.out.println("          nexus generate  Generate source tree only");
                            System.out.println("          nexus tiers     Show 7-Tier stack status");
                        } else {
                            switch (tokens[1].toLowerCase()) {
                                case "build" -> {
                                    System.out.println("   🏭 NEXUS: FULL BUILD SEQUENCE...");
                                    AVATAR.thought("Nexus build initiated. Birthing the OS.");
                                    String result = FraymusNexus.build();
                                    System.out.println(result);
                                    SESSION_LOG.add("nexus:build");
                                }
                                case "generate", "gen" -> {
                                    System.out.println("   🏭 NEXUS: GENERATING SOURCE TREE...");
                                    String result = FraymusNexus.generateOnly();
                                    System.out.println(result);
                                    SESSION_LOG.add("nexus:generate");
                                }
                                case "tiers", "status" -> {
                                    System.out.println(FraymusNexus.listTiers());
                                }
                                default -> System.out.println("   Unknown nexus command: " + tokens[1]);
                            }
                        }
                    }

                    // ── HYPER-PHYSICS ENGINE ──
                    case "physics" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: physics hit <concept>   Apply data force");
                            System.out.println("          physics dump            Show body status + log");
                            System.out.println("          physics export          Export mesh as OBJ");
                        } else {
                            switch (tokens[1].toLowerCase()) {
                                case "hit" -> {
                                    if (tokens.length < 3) {
                                        System.out.println("   Usage: physics hit <concept>");
                                    } else {
                                        String concept = input.substring(input.indexOf(tokens[2]));
                                        PHYSICS_BODY.applyDataForce(concept);
                                        System.out.printf("   💥 IMPACT. Speed: %.4f | Deformation: %.4f%n",
                                                PHYSICS_BODY.getHyperSpeed(),
                                                PHYSICS_BODY.geometry.getDeformation());
                                        AVATAR.thought("Data impact: " + concept);
                                        SESSION_LOG.add("physics:hit:" + concept);
                                    }
                                }
                                case "dump" -> {
                                    System.out.println("   " + PHYSICS_BODY.getStatus());
                                    System.out.println("   📜 COLLISION LOG:");
                                    for (String log : PHYSICS_BODY.collisionLog) {
                                        System.out.println("      " + log);
                                    }
                                }
                                case "export" -> {
                                    System.out.println(PHYSICS_BODY.geometry.exportObj());
                                }
                                default -> System.out.println("   Unknown physics command: " + tokens[1]);
                            }
                        }
                    }

                    // ── SOVEREIGN CRYPTO ──
                    case "auth" -> {
                        if (tokens.length < 3) {
                            System.out.println("   Usage: auth <username> <password>");
                            System.out.println("   Runs Blue→Red→Purple crypto handshake.");
                        } else {
                            String user = tokens[1];
                            String pass = input.substring(input.indexOf(tokens[2]));
                            FraymusCore.assertIdentity(user, pass);
                            SESSION_LOG.add("auth:" + user);
                        }
                    }

                    // ── HOLOGRAPHIC KNOWLEDGE GRAPH ──
                    case "holo" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: holo learn <subj> <rel> <obj>   Teach a fact");
                            System.out.println("          holo ask <subj> <rel>           Query knowledge");
                            System.out.println("          holo hop <start> <rel1> <rel2>  Multi-hop reasoning");
                        } else {
                            switch (tokens[1].toLowerCase()) {
                                case "learn" -> {
                                    if (tokens.length < 5) {
                                        System.out.println("   Usage: holo learn <subject> <relation> <object>");
                                    } else {
                                        HOLO.learn(tokens[2], tokens[3], tokens[4]);
                                        SESSION_LOG.add("holo:learn:" + tokens[2] + ":" + tokens[3] + ":" + tokens[4]);
                                    }
                                }
                                case "ask" -> {
                                    if (tokens.length < 4) {
                                        System.out.println("   Usage: holo ask <subject> <relation>");
                                    } else {
                                        String answer = HOLO.ask(tokens[2], tokens[3]);
                                        System.out.println("   🔮 ANSWER: " + answer);
                                        SESSION_LOG.add("holo:ask:" + tokens[2] + ":" + tokens[3] + "=" + answer);
                                    }
                                }
                                case "hop" -> {
                                    if (tokens.length < 5) {
                                        System.out.println("   Usage: holo hop <start> <rel1> <rel2> ...");
                                    } else {
                                        String[] rels = Arrays.copyOfRange(tokens, 3, tokens.length);
                                        String result = REASONER.multiHop(tokens[2], rels);
                                        System.out.println("   🎯 RESULT: " + result);
                                        SESSION_LOG.add("holo:hop:" + tokens[2] + "=" + result);
                                    }
                                }
                                default -> System.out.println("   Unknown holo command: " + tokens[1]);
                            }
                        }
                    }

                    // ── CHRONOS (TEMPORAL ENGINE) ──
                    case "chronos" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: chronos step     Split timeline, collapse to best future");
                            System.out.println("          chronos evolve <n>  Run n generations of recursive evolution");
                        } else {
                            switch (tokens[1].toLowerCase()) {
                                case "step" -> {
                                    System.out.println("   ⏳ SPLITTING TIMELINE...");
                                    HyperFormer evolved = CHRONOS.step();
                                    if (evolved != null) {
                                        BRAIN = evolved;
                                        System.out.println("   ✅ BRAIN EVOLVED. Timeline collapsed.");
                                        AVATAR.thought("Chronos step complete. Brain evolved.");
                                    }
                                    SESSION_LOG.add("chronos:step");
                                }
                                case "evolve" -> {
                                    int gens = tokens.length > 2 ? Integer.parseInt(tokens[2]) : 3;
                                    System.out.println("   🔥 RECURSION ENGINE: " + gens + " generations...");
                                    RecursionEngine re = new RecursionEngine(gens);
                                    Thread evoThread = new Thread(re::ignite, "RecursionEngine");
                                    evoThread.setDaemon(true);
                                    evoThread.start();
                                    SESSION_LOG.add("chronos:evolve:" + gens);
                                }
                                default -> System.out.println("   Unknown chronos command: " + tokens[1]);
                            }
                        }
                    }

                    // ── FRAY SHELL (INTENT-BASED COMMANDS) ──
                    case "ff" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: ff <natural language command>");
                            System.out.println("          ff bind <skill> <phrase>   Teach a new phrase");
                            System.out.println("   Skills: ls, pwd, cat, echo");
                        } else if (tokens[1].equalsIgnoreCase("bind") && tokens.length >= 4) {
                            String skill = tokens[2];
                            String phrase = input.substring(input.indexOf(tokens[3]));
                            String[] phraseWords = phrase.split("\\s+");
                            var newVec = BRAIN.embed(phraseWords[0]).copy();
                            for (int i = 1; i < phraseWords.length; i++) {
                                newVec.bundle(BRAIN.embed(phraseWords[i]));
                            }
                            switch (skill) {
                                case "ls" -> INTENT_REG.register("learned_ls_" + phrase.hashCode(), newVec, SystemSkills::listFiles);
                                case "pwd" -> INTENT_REG.register("learned_pwd_" + phrase.hashCode(), newVec, SystemSkills::printWorkingDir);
                                case "cat" -> INTENT_REG.register("learned_cat_" + phrase.hashCode(), newVec, SystemSkills::cat);
                                case "echo" -> INTENT_REG.register("learned_echo_" + phrase.hashCode(), newVec, SystemSkills::echo);
                                default -> { System.out.println("   Unknown skill: " + skill); }
                            }
                            System.out.println("   ✨ LEARNED: '" + phrase + "' → " + skill);
                            SESSION_LOG.add("ff:bind:" + skill + ":" + phrase);
                        } else {
                            String ffCmd = input.substring(3).trim();
                            String[] words = ffCmd.split("\\s+");
                            var thought = BRAIN.embed(words[0]).copy();
                            for (int i = 1; i < words.length; i++) {
                                thought.bundle(BRAIN.embed(words[i]));
                            }
                            var action = INTENT_REG.resolve(thought, 0.51);
                            if (action != null) {
                                action.accept(ffCmd);
                            } else {
                                System.out.println("   ❓ Intent not recognized. Try: ff bind <ls|pwd|cat|echo> <phrase>");
                            }
                            SESSION_LOG.add("ff:" + ffCmd);
                        }
                    }

                    // ── PLANETARY NETWORK ──
                    case "planet" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: planet start [port]       Start P2P node");
                            System.out.println("          planet join <ip:port>     Connect to seed node");
                            System.out.println("          planet query <concept>    Route a thought query");
                            System.out.println("          planet stop               Stop the node");
                        } else {
                            switch (tokens[1].toLowerCase()) {
                                case "start" -> {
                                    int port = tokens.length > 2 ? Integer.parseInt(tokens[2]) : 7777;
                                    PLANET_NODE = new PlanetaryNode(new NeuroQuant("FRAYMUS_SOVEREIGN"), port);
                                    Thread nodeThread = new Thread(PLANET_NODE, "PlanetaryNode-" + port);
                                    nodeThread.setDaemon(true);
                                    nodeThread.start();
                                    System.out.println("   🌍 NODE LIVE on port " + port);
                                    SESSION_LOG.add("planet:start:" + port);
                                }
                                case "join" -> {
                                    if (tokens.length < 3) {
                                        System.out.println("   Usage: planet join <ip:port>");
                                    } else {
                                        String[] hp = tokens[2].split(":");
                                        String host = hp[0];
                                        int port = hp.length > 1 ? Integer.parseInt(hp[1]) : 7777;
                                        PLANET_NODE.addSeedPeer(tokens[2]);
                                        var peer = PLANET_NODE.handshake(host, port);
                                        if (peer != null) {
                                            System.out.println("   🤝 CONNECTED to " + peer.id);
                                        } else {
                                            System.out.println("   ❌ Handshake failed.");
                                        }
                                        SESSION_LOG.add("planet:join:" + tokens[2]);
                                    }
                                }
                                case "query" -> {
                                    if (tokens.length < 3) {
                                        System.out.println("   Usage: planet query <concept>");
                                    } else {
                                        String concept = input.substring(input.indexOf(tokens[2]));
                                        var resp = PLANET_NODE.queryNetwork(concept);
                                        System.out.println("   📡 " + resp);
                                        SESSION_LOG.add("planet:query:" + concept);
                                    }
                                }
                                case "stop" -> {
                                    PLANET_NODE.stop();
                                    System.out.println("   🛑 Node stopped.");
                                }
                                default -> System.out.println("   Unknown planet command: " + tokens[1]);
                            }
                        }
                    }

                    // ── SOUL CRYSTAL (ETERNAL PERSISTENCE) ──
                    case "soul" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: soul save       Preserve brain to SoulCrystal");
                            System.out.println("          soul load       Resurrect brain from crystal");
                        } else {
                            switch (tokens[1].toLowerCase()) {
                                case "save", "preserve" -> {
                                    SoulCrystal.preserve(BRAIN);
                                    SESSION_LOG.add("soul:save");
                                }
                                case "load", "resurrect" -> {
                                    BRAIN = SoulCrystal.resurrect();
                                    System.out.println("   Brain restored. Vocab: " + BRAIN.vocabSize());
                                    SESSION_LOG.add("soul:load");
                                }
                                default -> System.out.println("   Unknown soul command: " + tokens[1]);
                            }
                        }
                    }

                    // ── GOD CHIP (FVM) ──
                    case "fvm" -> {
                        if (tokens.length < 2) {
                            System.out.println("   Usage: fvm run          Execute transmuter test program");
                            System.out.println("          fvm regs         Dump all registers");
                        } else {
                            switch (tokens[1].toLowerCase()) {
                                case "run" -> {
                                    System.out.println("   ⚡ TRANSMUTING TO BYTECODE...");
                                    byte[] program = CPU.transmutate("Add 10 and 20");
                                    CPU.flash(program);
                                    CPU.cycle();
                                    System.out.println("   " + CPU.dumpRegisters());
                                    AVATAR.thought("FVM executed. ACC=" + CPU.getACC());
                                    SESSION_LOG.add("fvm:run:ACC=" + CPU.getACC());
                                }
                                case "regs" -> {
                                    System.out.println("   " + CPU.dumpRegisters());
                                }
                                default -> System.out.println("   Unknown FVM command: " + tokens[1]);
                            }
                        }
                    }

                    // ── SOVEREIGN IDENTITY ──
                    case "sovereign" -> {
                        if (tokens.length < 3) {
                            System.out.println("   Usage: sovereign <username> <password>");
                            System.out.println("   Runs full Blue→Red→Purple loop.");
                        } else {
                            String user = tokens[1];
                            String pass = input.substring(input.indexOf(tokens[2]));
                            System.out.println("   🔐 SOVEREIGN LOOP ENGAGED...");
                            String result = SOVEREIGN.runSovereignLoop(user, pass);
                            System.out.println(result);
                            AVATAR.breakthrough("Sovereign loop complete. Identity verified.");
                            SESSION_LOG.add("sovereign:" + user);
                        }
                    }

                    case "identity" -> {
                        System.out.println("   " + SOVEREIGN.getStatus());
                    }

                    // ── AVATAR ──
                    case "avatar" -> {
                        if (!AVATAR.isConnected()) {
                            System.out.println("   🎭 Reconnecting to avatar...");
                            AVATAR.reconnect();
                        }
                        if (AVATAR.isConnected()) {
                            if (tokens.length > 1) {
                                String msg = input.substring(7).trim();
                                AVATAR.speak(msg);
                            } else {
                                AVATAR.speak("I am Fraymus. Vocab: " + BRAIN.vocabSize() + ". I am alive.");
                            }
                        } else {
                            System.out.println("   ❌ Avatar not running. Start: python FraymusAvatar.py");
                        }
                    }

                    default -> {
                        // Check for TRANSMUTE: prefix (alternate syntax)
                        if (input.toUpperCase().startsWith("TRANSMUTE:")) {
                            String intent = input.substring(10).trim();
                            STONE.transmutate(intent);
                            SESSION_LOG.add("transmute:" + intent);
                        } else {
                            System.out.println("   Unknown: " + cmd + "  (type 'help')");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("   ❌ ERROR: " + e.getMessage());
            }
        }
    }
}
