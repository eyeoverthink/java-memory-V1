package fraymus.ultimate;

import java.util.List;

/**
 * Structured view of the Fraynix operator surface.
 */
public final class FraynixCommandCatalog implements UltimateCommandCatalog {

    private final List<CommandSection> sections;

    private FraynixCommandCatalog(List<CommandSection> sections) {
        this.sections = List.copyOf(sections);
    }

    public static FraynixCommandCatalog createDefault() {
        return new FraynixCommandCatalog(List.of(
            section("core", "Core Shell", "Primary boot, build, file, and code generation commands.",
                cmd("help", "Show the full Fraynix help surface"),
                cmd("status", "Print system-wide status"),
                cmd("ai <question>", "Route a question through the AI stack"),
                cmd("absorb <package>", "Absorb Java packages into knowledge"),
                cmd("fs [list|read|write]", "Operate on the Fraynix filesystem"),
                cmd("physics", "Inspect the physics engine"),
                cmd("phi [n]", "Golden-ratio calculations and diagnostics"),
                cmd("generate", "Generate the full bare-metal component set"),
                cmd("build <component>", "Build one low-level subsystem"),
                cmd("code <description>", "Generate code and save it into FrayFS")
            ),
            section("cognition", "Cortex and AEON", "Live cognition, HDC swarm, and autopoietic engines.",
                cmd("cortex", "Inspect or run the HyperCortex"),
                cmd("cortex inject <txt>", "Inject stimulus into cortex state"),
                cmd("aeon", "Inspect or run AEON Prime"),
                cmd("aeon inject <txt>", "Inject stimulus into the liquid manifold"),
                cmd("absolute ignite", "Start the AEON Absolute HDC swarm"),
                cmd("absolute recall <t>", "Query holographic swarm memory"),
                cmd("openclaw", "Launch the rasterized node arena"),
                cmd("hrm", "Launch the neuromorphic cortex")
            ),
            section("exodus", "Exodus Pipeline", "Bare-metal pipeline from generation to image launch.",
                cmd("exodus", "Run the full bare-metal build pipeline"),
                cmd("exodus verify", "Verify the 512-byte image, 0x55AA signature, SHA-256, and boot text"),
                cmd("exodus launch", "Boot the image in QEMU when qemu-system-i386 is available"),
                cmd("exodus autofix", "Run build plus Ollama-assisted repair"),
                cmd("genmodule <concept>", "Generate a C module into fraynix_src"),
                cmd("retro \"cur\" \"fut\"", "Retrocausal compile planning")
            ),
            section("reinforcement", "RL and Teacher Stack", "Neural brain system, expert teacher, and Rainbow cortex.",
                cmd("nbs start", "Start the continuous brain tick loop"),
                cmd("nbs present <text>", "Present a concept to the brain"),
                cmd("teacher teach", "Advance the curriculum"),
                cmd("teacher probe <q>|<a>", "Probe the student and reward or penalize"),
                cmd("rate good", "Positive reward signal"),
                cmd("rcortex improve", "Recursive self-improvement"),
                cmd("rainbow train [n]", "Train the Rainbow DQN")
            ),
            section("knowledge-media", "Knowledge and Media", "Scraping, embeddings, video, game AI, and phi-LSH.",
                cmd("wkb scrape <url>", "Scrape and embed web content"),
                cmd("wkb search <query>", "Semantic search over scraped knowledge"),
                cmd("vidpipe run", "Run the video pipeline"),
                cmd("gameai run", "Run the OpenClaw RL game simulation"),
                cmd("embed <text>", "Encode text to an HD vector"),
                cmd("phi encode <text>", "Word-hash encode text"),
                cmd("phi dist <a>|<b>", "Compute Hamming similarity")
            ),
            section("sentience", "Sentience and Absorption", "SentientCore, library absorption, and specialist AGI engines.",
                cmd("sentient think <q>", "Auto-route a question to the right engine"),
                cmd("sentient why <x>", "Abductive causal reasoning"),
                cmd("sentient what if <x>", "Forward causal prediction"),
                cmd("libabs package <p>", "Absorb a package via reflection"),
                cmd("libabs crawl <t>", "Clone and absorb external tools"),
                cmd("transmudder file <path>", "Digest one file into the living context soul"),
                cmd("transmudder proof <path>", "Digest one file and return proof-oriented absorption telemetry"),
                cmd("transmudder stats", "Show soul/transmutation context telemetry"),
                cmd("singularity", "Enter the Singularity REPL"),
                cmd("aubo ledger", "Inspect the blockchain graph"),
                cmd("tachyon query <k>", "O(1) holographic retrieval"),
                cmd("kronos analogy A B C", "Zero-shot symbolic analogy"),
                cmd("omniscience", "Enter the Dream Daemon / fractal VSA shell")
            ),
            section("aeon-specialists", "AEON Specialist Engines", "Physics, reality compilation, self-coding, transmutation, and quantum kernels.",
                cmd("demiurge", "Launch the ontological physics engine"),
                cmd("apotheosis", "Launch the reality compiler"),
                cmd("genomics status", "Inspect the local Carbon-3B DNA model"),
                cmd("specialist list", "Inspect typed model capabilities and requirements"),
                cmd("specialist describe <id>", "Read source, methods, licensing and availability"),
                cmd("specialist profile", "Verify the saved specialist brain profile and digital genome"),
                cmd("specialist needle <request.json>", "Propose schema-validated calls with local Needle3"),
                cmd("specialist brain <request.json>", "Use the owner Brain learner, VM and native methods"),
                cmd("specialist enigma <request.json>", "Verify and roundtrip owner DNA/WASM/PNG containers"),
                cmd("specialist minimax-code version", "Check the isolated MiniMax CLI installation"),
                cmd("specialist robot <request.json>", "Observe, plan, preview, apply and control the Fraymus scene with existing world/body brains"),
                cmd("specialist chain <request.json>", "Audit, recover or reconcile knowledge records while retaining unresolved history"),
                cmd("specialist learn", "Persist the specialist catalogue and brain profile"),
                cmd("genomics inventory", "Inventory FASTA files and generator provenance"),
                cmd("genomics analyze <fasta>", "Score a sequence with the local Carbon model"),
                cmd("genomics generate <fasta> --bases 120", "Generate a DNA continuation with provenance"),
                cmd("genomics learn", "Persist the genomics knowledge pack"),
                cmd("genomics identify <fasta>", "Match a sequence to the offline reference catalog"),
                cmd("genomics absorb <fasta> --annotation <embl> --source-url <url>", "Absorb verified reference sequence and annotations"),
                cmd("genomics profile", "Inspect the durable genomics component and shared-brain profile"),
                cmd("genomics genome verify <manifest>", "Verify and decode a digital genome snapshot"),
                cmd("apotheosis restore <manifest>", "Restore a saved concept vector and its DNA encoding"),
                cmd("omega ouroboros", "JIT self-coding through Omega"),
                cmd("babel <concept> [L]", "Transmute concepts across substrates"),
                cmd("willow status", "Inspect the live Willow-Crusher substrate"),
                cmd("willow hadamard <n>", "Inject qubits into phi-spaced superposition"),
                cmd("willow proof [n]", "Run a compact Willow proof sweep through superposition, entanglement, echo, and measurement")
            ),
            section("bridge", "Quantum Bridge", "Cross-instance learning, propagation, retrocausality, and EM signaling.",
                cmd("aether", "Entangle all parallel Fraynix instances"),
                cmd("braid", "Topological tensor synchronization"),
                cmd("learn <concept>", "Assimilate a concept into shared 16,384-D space"),
                cmd("pulse <concept>", "Broadcast a concept across entangled instances"),
                cmd("retro <now> <future>", "Compute catalyst from future XOR present"),
                cmd("emf <concept>", "Broadcast by CPU-load radio modulation")
            ),
            section("survival", "Survival and Transfer", "Steganographic propagation, Lazarus resurrection, and cortical stack transfer.",
                cmd("agora forge <text>|<payload>", "Hide payloads in carrier text"),
                cmd("lazarus crystallize", "Harvest and save the soul crystal"),
                cmd("lazarus resurrect", "Restore from the soul crystal"),
                cmd("stack encode <pass>", "Encrypt consciousness into a DHF"),
                cmd("stack decode <pass>", "Resleeve consciousness into this host"),
                cmd("stack needlecast <h>", "Transmit consciousness to a remote host")
            ),
            section("convergence", "Convergence and Physics", "Quantum/convergence subsystems, swarm behavior, organism control, and spatial physics.",
                cmd("entangle", "Create entangled particle pairs"),
                cmd("qbox create S | D", "Create a dual-reality container"),
                cmd("collide <A> <B>", "Smash concepts into new elements"),
                cmd("swarm think <text>", "Collective consciousness processing"),
                cmd("nexus awaken", "Awaken the 10-organ digital organism"),
                cmd("spatial map", "ASCII universe render"),
                cmd("gravity start", "Start Hebbian gravity"),
                cmd("fusion check", "Scan for thought collisions"),
                cmd("suit wrap D X Y Z", "Wrap data in a PhiSuit"),
                cmd("genome load <id>", "Load and decode a DNA shard")
            ),
            section("facades", "Facades and Engine Layers", "Dependency-light wrappers plus the seven-layer engine control plane.",
                cmd("selfcode phi <code>", "Phi-transform code"),
                cmd("experiment prime <n>", "Primality testing"),
                cmd("phinode spawn <name>", "Create a living node"),
                cmd("engine init", "Initialize all seven layers"),
                cmd("engine status", "Report seven-layer status"),
                cmd("metalearner adapt", "Adapt the learning strategy"),
                cmd("causal intervene v n", "Run a what-if intervention"),
                cmd("goals create id|desc", "Create an external goal"),
                cmd("selfref observe", "Observe its own processing")
            ),
            section("genesis", "Genesis and Applications", "Hyper systems, consciousness engines, creation systems, and applied science.",
                cmd("hypervector", "10,000-D vector operations"),
                cmd("dream", "Enter REM association state"),
                cmd("genesisprime", "Run the self-writing engine"),
                cmd("genesisboot <prompt>", "Spawn software from intent"),
                cmd("coder create p|f[|l]", "Self-correcting code generation"),
                cmd("protein", "Protein folding engine"),
                cmd("cancer", "Cancer research engine"),
                cmd("drug", "Drug discovery engine")
            ),
            section("daemons", "Daemons and Infrastructure", "Long-running orchestrators, security systems, infrastructure, and embodiment.",
                cmd("fraynixorg start", "Start the organism daemon"),
                cmd("agentboot", "Offline AGI: brain, hands, and soul"),
                cmd("planetary", "Global semantic P2P network"),
                cmd("frayshell", "Intent-based shell"),
                cmd("benchmark complete", "Run the full benchmark suite"),
                cmd("validate", "Scientific validation"),
                cmd("rigorous", "Rigorous architecture live demo"),
                cmd("desktop", "Enter the Construct / desktop embodiment"),
                cmd("exit", "Shutdown Fraynix")
            )
        ));
    }

    @Override
    public String surfaceId() {
        return "fraynix";
    }

    @Override
    public String surfaceTitle() {
        return "Fraynix Command Surface";
    }

    @Override
    public String surfaceDescription() {
        return "The mega-shell exposed by Fraynix Boot and its related runtimes.";
    }

    @Override
    public String primarySubsystemId() {
        return "fraynix-boot";
    }

    @Override
    public List<CommandSection> sections() {
        return sections;
    }

    @Override
    public List<String> linkedSubsystemIds(CommandSection section) {
        return switch (section.id()) {
            case "core" -> List.of("fraynix-boot", "fraynix-os");
            case "cognition" -> List.of("fraynix-boot", "fraynix-organism", "convergence");
            case "exodus" -> List.of("fraynix-boot");
            case "reinforcement" -> List.of("fraynix-boot");
            case "knowledge-media" -> List.of("fraynix-boot", "video-cortex", "fraymus-ai-core");
            case "sentience" -> List.of("fraynix-boot", "fraymus-ai-core");
            case "aeon-specialists" -> List.of("fraynix-boot", "convergence");
            case "bridge" -> List.of("fraynix-boot");
            case "survival" -> List.of("fraynix-boot");
            case "convergence" -> List.of("fraynix-boot", "convergence", "fraynix-organism");
            case "facades" -> List.of("ultimate", "fraynix-boot", "convergence");
            case "genesis" -> List.of("fraynix-boot", "applications");
            case "daemons" -> List.of("fraynix-organism", "fraynix-boot", "fraynix-os");
            default -> List.of("fraynix-boot");
        };
    }

    private static CommandSection section(String id, String title, String theme, CommandEntry... commands) {
        return new CommandSection(id, title, theme, List.of(commands));
    }

    private static CommandEntry cmd(String syntax, String description) {
        return new CommandEntry(syntax, description);
    }

}
