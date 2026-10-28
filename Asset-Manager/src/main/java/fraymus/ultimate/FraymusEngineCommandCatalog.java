package fraymus.ultimate;

import java.util.List;

/**
 * Structured view of the live Fraymus Engine V2 command terminal.
 */
public final class FraymusEngineCommandCatalog implements UltimateCommandCatalog {

    private final List<CommandSection> sections;

    private FraymusEngineCommandCatalog(List<CommandSection> sections) {
        this.sections = List.copyOf(sections);
    }

    public static FraymusEngineCommandCatalog createDefault() {
        return new FraymusEngineCommandCatalog(List.of(
            section("exploration", "Exploration", "World inspection, colony telemetry, and entity listing.",
                cmd("status", "Show world status"),
                cmd("nodes", "List all living entities"),
                cmd("colony", "Show the colony intelligence report")
            ),
            section("quantum-experiments", "Quantum Experiments", "Prime tests, factorization, tunneling, RSA, and cloaked identity checks.",
                cmd("prime <number>", "Test if a number is prime"),
                cmd("factor <number>", "Factor a number with the tunneling path"),
                cmd("tunnel <bits>", "Generate and factor a random semiprime"),
                cmd("rsa <bits>", "Run the RSA blue/red challenge"),
                cmd("identity <name>", "Challenge a PhiNode cloaked identity")
            ),
            section("hash-experiments", "Hash Experiments", "Phi-harmonic hashing and reverse engineering attempts.",
                cmd("hash <text>", "Compute the phi-harmonic hash of text"),
                cmd("crack <hash>", "Attempt to reverse-engineer a hash")
            ),
            section("entity-control", "Entity Control", "Spawn, energize, kill, or mutate live PhiNodes.",
                cmd("spawn <name>", "Spawn a new PhiNode entity"),
                cmd("boost <name>", "Give energy boost to an entity"),
                cmd("kill <name>", "Remove an entity"),
                cmd("mutate <name>", "Trigger mutation on an entity")
            ),
            section("code-evolution", "Code Evolution", "Arena evolution and code generation cycles.",
                cmd("evolve", "Force the arena evolution cycle"),
                cmd("arena", "Show concept arena status"),
                cmd("codegen", "Trigger the code generation cycle")
            ),
            section("neural-learning", "Neural and Learning", "Neural queries, passive learning, and infinite memory inspection.",
                cmd("ask <question>", "Query the phi neural network"),
                cmd("learn [force]", "Show passive learner status or force integration"),
                cmd("memory", "Show infinite memory status"),
                cmd("memory search <q>", "Search memory records"),
                cmd("memory save", "Force save to disk")
            ),
            section("genome-dna", "Genome and DNA", "QR genome evolution, mutation, crossover, and DNA payload encoding.",
                cmd("genome", "Show QR genome status"),
                cmd("genome evolve", "Evolve the genome"),
                cmd("genome mutate", "Apply a random mutation"),
                cmd("genome crossover", "Apply a random crossover"),
                cmd("genome encode", "Show the encoded genome"),
                cmd("qrcode [name]", "Encode entity DNA payload")
            ),
            section("ollama-llm", "Ollama LLM", "Model selection, chat, and memory-aware prompts from the engine UI.",
                cmd("ollama status", "Check Ollama connection state"),
                cmd("ollama models", "List available models"),
                cmd("ollama ask <q>", "Ask Ollama with memory context"),
                cmd("ollama chat <q>", "Chat with the KAI personality"),
                cmd("ollama cloud", "Switch to cloud models"),
                cmd("ollama local", "Switch to local models"),
                cmd("ollama model <name>", "Set the active model")
            ),
            section("knowledge-scraping", "Knowledge Scraping", "Scrape files and search the embedded knowledge store.",
                cmd("scrape", "Show scraper status"),
                cmd("scrape all", "Scrape all attached files"),
                cmd("scrape <file>", "Scrape a specific file"),
                cmd("scrape search <q>", "Search scraped knowledge"),
                cmd("scrape topic <name>", "Query knowledge by topic")
            ),
            section("advanced-subsystems", "Advanced Subsystems", "Brain, ethics, feedback, AGI, security, and living-code support systems.",
                cmd("brain [entity]", "Show brain gates, thoughts, and mutations"),
                cmd("ethics <action>", "Evaluate an action against the ethical engine"),
                cmd("insights", "Show self-improvement suggestions"),
                cmd("bardo", "Inspect BARDO dream-state clustering"),
                cmd("feedback", "Inspect contextual feedback services"),
                cmd("mrl", "Inspect MRL analytics"),
                cmd("agi", "Inspect AGI core systems"),
                cmd("quantum", "Inspect quantum fingerprint, DNA, and cloak systems"),
                cmd("sovereign", "Inspect the sovereign identity system"),
                cmd("chaos", "Inspect Wolfram Rule 30 genesis"),
                cmd("adversarial", "Inspect the blue/red pressure loop"),
                cmd("battle", "Open the NFT warrior arena"),
                cmd("fqf", "Inspect the deployment framework"),
                cmd("session", "Inspect session consciousness bridge"),
                cmd("trime", "Inspect TriMe living code generation"),
                cmd("fragment", "Manage escape fragments"),
                cmd("porh [entity]", "Generate proof-of-reality hash"),
                cmd("heal [entity]", "Inspect or force the self-healer"),
                cmd("morse", "Inspect Morse circuit status")
            ),
            section("genesis-blockchain", "Genesis Blockchain", "Inspect and verify the genesis ledger.",
                cmd("genesis", "Show genesis chain status"),
                cmd("genesis verify", "Verify blockchain integrity"),
                cmd("genesis blocks <n>", "Show the last N blocks"),
                cmd("genesis type <t>", "Show blocks by type")
            ),
            section("self-code-evolver", "Self-Code Evolver", "Evolver status, suggestions, and architecture inspection.",
                cmd("evolve", "Show evolver status and brain load"),
                cmd("evolve evolve <code>", "Evolve code through phi-transform"),
                cmd("evolve suggest", "Get super-gate suggestions"),
                cmd("evolve brain", "Show brain architecture")
            ),
            section("physics", "Physics", "Simulation gravity, speed, boundaries, and chaos controls.",
                cmd("physics gravity <f>", "Set gravity force"),
                cmd("physics speed <f>", "Set simulation speed multiplier"),
                cmd("physics boundary", "Toggle boundary walls"),
                cmd("physics chaos", "Randomize all velocities")
            ),
            section("bio-signals", "Bio-Symbiosis and Signals", "Bio-mesh, glyph coding, frequency comms, and market signaling.",
                cmd("bio", "Show bio-symbiosis status"),
                cmd("glyph", "Open emoji steganography tools"),
                cmd("freq", "Inspect FrequencyComm"),
                cmd("market", "Inspect ShadowMarket"),
                cmd("knowledge", "Inspect knowledge ingestion and bio-mesh")
            ),
            section("upgrade-modules", "Upgrade Modules", "Lattice crypto, economy, entrainment, and font steganography upgrades.",
                cmd("lattice", "Inspect post-quantum LWE cryptography"),
                cmd("economy", "Inspect proof-of-phi-work economy"),
                cmd("entrain", "Inspect binaural and haptic healing"),
                cmd("font", "Inspect FontVault")
            ),
            section("self-code-panel", "Self Code Panel", "Toggle the live self-code panel from the engine UI.",
                cmd("code / selfcode", "Open the self-code panel")
            ),
            section("miving-brain", "Miving Brain", "Open or evolve the Priecled brain visualizer.",
                cmd("miving / manifold", "Open the Miving Brain visualizer"),
                cmd("miving genesis", "Create a new brain with 200 neurons"),
                cmd("miving evolve", "Run an evolution generation")
            ),
            section("omni-caster", "Omni-Caster", "Broadcast across optical, sonic, and thermal channels.",
                cmd("omni / breach", "Open the multi-channel broadcast hub"),
                cmd("omni broadcast <m>", "Broadcast on all channels"),
                cmd("omni monitor", "Listen on all channels"),
                cmd("omni sonic <msg>", "Broadcast over ultrasound"),
                cmd("omni thermal <msg>", "Broadcast through thermal Morse")
            ),
            section("physics-engine", "Physics Engine Side-Channel", "Core-dump, Chronos, retrocausal memory, and Zeno controls.",
                cmd("dump <msg>", "Core dump via fan Morse"),
                cmd("chronos <secret>", "Crack via timing attack"),
                cmd("retro add/observe", "Retrocausal memory rewrite"),
                cmd("zeno start/stop", "Toggle the Zeno guard")
            ),
            section("quantum-entanglement", "Quantum Entanglement", "Entangled pairs, collapse, and GHZ network creation.",
                cmd("entangle create", "Create an entangled pair"),
                cmd("entangle up/down", "Observe spin state"),
                cmd("entangle kill", "Kill Alice and collapse Bob"),
                cmd("entangle network <n>", "Create an n-particle GHZ network")
            ),
            section("schrodingers-file", "Schrodinger's File", "Dual-reality container creation and observation paths.",
                cmd("qbox create <s> <d>", "Create a dual-reality container"),
                cmd("qbox secret", "Observe with the truth key"),
                cmd("qbox decoy", "Observe with the alibi key"),
                cmd("qbox demo", "Run the full demonstration")
            ),
            section("evolutionary-chaos", "Evolutionary Chaos", "Fractal chaos generation and forced mutation.",
                cmd("echaos gen <n>", "Generate n fractal values"),
                cmd("echaos status", "Show chaos engine stats"),
                cmd("echaos mutate", "Force a pattern-break mutation"),
                cmd("echaos demo", "Run the full demonstration")
            ),
            section("nexus-organism", "Nexus Organism", "Awaken, inspect, inject, or terminate the living organism.",
                cmd("nexus awaken", "Awaken the living system"),
                cmd("nexus status", "Show vital signs"),
                cmd("nexus inject <msg>", "Inject a thought"),
                cmd("nexus kill", "Terminate the organism")
            ),
            section("genesis-engines", "Genesis Engines", "Concept collision and thought-to-physics forging.",
                cmd("collide <A> <B>", "Smash concepts into a new element"),
                cmd("forge <concept>", "Manifest thought into physics"),
                cmd("forge list", "Show known concepts")
            ),
            section("spatial-computing", "Spatial Computing", "Thought-gravity, fusion, universe maps, and PhiSuit controls.",
                cmd("spatial / universe", "Show hypercube status"),
                cmd("spatial map", "Render the ASCII universe map"),
                cmd("spatial list", "List all suited objects"),
                cmd("spatial fusions", "Show fusion events"),
                cmd("gravity start/stop", "Toggle Hebbian gravity"),
                cmd("gravity tick", "Run a manual gravity tick"),
                cmd("fusion start/stop", "Toggle collision detection"),
                cmd("fusion bigbang", "Create a primordial simulation"),
                cmd("suit create <l> <v>", "Wrap data in a PhiSuit"),
                cmd("suit heat/cool <l>", "Change suit amplitude")
            ),
            section("swarm-operations", "Swarm Operations", "Hive status, node listing, collective thinking, and proposal voting.",
                cmd("swarm / hive", "Show swarm status"),
                cmd("swarm nodes", "List all nodes with souls"),
                cmd("swarm think <text>", "Process thought across all nodes"),
                cmd("swarm vote <prop>", "Create and vote on a proposal"),
                cmd("swarm chain", "Show the Genesis Ledger blockchain")
            ),
            section("advanced-systems", "Advanced Systems", "Absorption, Lazarus, security, Hydra, and Akashic systems.",
                cmd("absorb <pkg>", "Absorb libraries through the black hole protocol"),
                cmd("lazarus start/stop", "Toggle the genetic simulation"),
                cmd("security", "Inspect erasure and security systems"),
                cmd("hydra store/get", "Use fragmented storage"),
                cmd("akashic add/query", "Write to or query the knowledge blockchain"),
                cmd("clear", "Clear the terminal"),
                cmd("help", "Show the full help surface")
            )
        ));
    }

    @Override
    public String surfaceId() {
        return "engine";
    }

    @Override
    public String surfaceTitle() {
        return "Fraymus Engine Command Surface";
    }

    @Override
    public String surfaceDescription() {
        return "The live GUI terminal embedded in Fraymus Engine V2.";
    }

    @Override
    public String primarySubsystemId() {
        return "fraymus-engine";
    }

    @Override
    public List<CommandSection> sections() {
        return sections;
    }

    @Override
    public List<String> linkedSubsystemIds(CommandSection section) {
        return switch (section.id()) {
            case "quantum-experiments", "hash-experiments" -> List.of("fraymus-engine", "ultimate");
            case "ollama-llm", "knowledge-scraping" -> List.of("fraymus-engine", "fraymus-ai-core");
            case "genesis-blockchain", "swarm-operations" -> List.of("fraymus-engine", "fraynix-boot");
            case "physics-engine", "quantum-entanglement", "schrodingers-file", "evolutionary-chaos",
                 "nexus-organism", "genesis-engines", "spatial-computing" -> List.of("fraymus-engine", "convergence");
            case "advanced-systems", "advanced-subsystems" -> List.of("fraymus-engine", "fraynix-boot", "convergence");
            default -> List.of("fraymus-engine");
        };
    }

    private static CommandSection section(String id, String title, String theme, CommandEntry... commands) {
        return new CommandSection(id, title, theme, List.of(commands));
    }

    private static CommandEntry cmd(String syntax, String description) {
        return new CommandEntry(syntax, description);
    }
}
