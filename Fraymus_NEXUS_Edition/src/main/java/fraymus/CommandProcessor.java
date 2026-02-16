package fraymus;

import fraymus.absorption.LibraryAbsorber;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * COMMAND PROCESSOR - Central Nervous System
 * 
 * Routes all commands to appropriate handlers.
 * Works in both GUI and CLI modes.
 * 
 * Registered Handlers:
 * - Absorption (LibraryAbsorber, Black Hole Protocol)
 * - Lazarus (Genetic nano-circuits)
 * - Oracle (Quantum timelines)
 * - Living (Living code generator)
 * - Vault (Biometric storage)
 * - Security (BlackHole, RootScrambler)
 * - Ollama (AI agent integration)
 * 
 * Usage:
 *   CommandProcessor.process("absorb package java.util");
 *   CommandProcessor.process("lazarus start");
 *   CommandProcessor.process("oracle init");
 */
public class CommandProcessor {
    
    private static Map<String, BiConsumer<String, String>> handlers = new HashMap<>();
    private static boolean initialized = false;
    
    /**
     * Initialize command processor and register all handlers
     */
    public static void initialize() {
        if (initialized) return;
        
        System.out.println("🧠 Initializing Command Processor...");
        
        // Register absorption commands
        registerHandler("absorb", CommandTerminalAbsorption::handle);
        registerHandler("query", CommandTerminalAbsorption::handle);
        registerHandler("execute", CommandTerminalAbsorption::handle);
        registerHandler("absorber", CommandTerminalAbsorption::handle);
        
        // Register security commands
        registerHandler("blackhole", CommandTerminalSecurity::handle);
        registerHandler("scramble", CommandTerminalSecurity::handle);
        registerHandler("deadman", CommandTerminalSecurity::handle);
        registerHandler("ghostcode", CommandTerminalSecurity::handle);
        registerHandler("secureinfo", CommandTerminalSecurity::handle);
        
        // Register lazarus commands
        registerHandler("lazarus", CommandTerminalLazarus::handle);
        
        // Register oracle commands
        registerHandler("oracle", CommandTerminalOracle::handle);
        
        // Register vault commands
        registerHandler("vault", CommandTerminalVault::handle);
        
        // Register living code commands
        registerHandler("living", CommandTerminalLiving::handle);
        
        // Register spatial computing commands
        registerHandler("spatial", CommandTerminalSpatial::handle);
        registerHandler("gravity", CommandTerminalSpatial::handleGravityEngine);
        registerHandler("fusion", CommandTerminalSpatial::handleFusionReactor);
        registerHandler("universe", CommandTerminalSpatial::handleUniverse);
        registerHandler("inject", CommandTerminalSpatial::handleInjectConcept);
        
        // Register API commands
        registerHandler("api", CommandTerminalAPI::handle);
        
        // Register evolution commands
        registerHandler("evolve", CommandTerminalEvolution::handle);
        registerHandler("library", CommandTerminalEvolution::handle);
        registerHandler("generate", CommandTerminalEvolution::handle);
        registerHandler("prove", CommandTerminalEvolution::handle);
        
        initialized = true;
        System.out.println("   ✓ Command Processor initialized");
        System.out.println();
    }
    
    /**
     * Register a command handler
     */
    public static void registerHandler(String command, BiConsumer<String, String> handler) {
        handlers.put(command.toLowerCase(), handler);
    }
    
    /**
     * Process a command string
     * 
     * @param input Full command string (e.g., "absorb package java.util")
     * @return true if command was handled, false if unknown
     */
    public static boolean process(String input) {
        if (!initialized) {
            initialize();
        }
        
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        input = input.trim();
        
        // Handle special commands
        if (input.equalsIgnoreCase("help")) {
            showHelp();
            return true;
        }
        
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
            System.out.println("Exiting...");
            System.exit(0);
            return true;
        }
        
        // Parse command and args
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";
        
        // Route to handler
        BiConsumer<String, String> handler = handlers.get(command);
        if (handler != null) {
            try {
                handler.accept(command, args);
                return true;
            } catch (Exception e) {
                System.err.println("❌ Command error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        // Unknown command
        System.err.println("❌ Unknown command: " + command);
        System.out.println("Type 'help' for available commands");
        return false;
    }
    
    /**
     * Show comprehensive help
     */
    public static void showHelp() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ FRAYMUS NEXUS v2.0 - COMMAND REFERENCE                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("🕳️ ABSORPTION (Black Hole Protocol)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  absorb package <name>       Absorb Java package");
        System.out.println("  absorb jar <path>           Absorb entire JAR file");
        System.out.println("  absorb url <url>            Absorb web content");
        System.out.println("  query <term>                Search absorbed knowledge");
        System.out.println("  execute <skill> [args]      Execute absorbed method");
        System.out.println("  absorber stats              Show absorption statistics");
        System.out.println();
        
        System.out.println("🧬 EVOLUTION (Lazarus Engine)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  lazarus start               Start genetic simulation");
        System.out.println("  lazarus status              Population statistics");
        System.out.println("  lazarus inject              Energy injection");
        System.out.println("  lazarus evolve <gens>       Run N generations");
        System.out.println("  lazarus best                Show fittest circuits");
        System.out.println("  lazarus stop                Stop simulation");
        System.out.println();
        
        System.out.println("🔮 ORACLE (Quantum Timelines)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  oracle init                 Initialize 3 timelines");
        System.out.println("  oracle run <cycles>         Run evolution cycles");
        System.out.println("  oracle status               Check reality state");
        System.out.println("  oracle consult              Single consultation");
        System.out.println("  oracle collapse             Force timeline collapse");
        System.out.println();
        
        System.out.println("🔐 VAULT (Biometric Storage)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  vault deposit <id> <data> <bio>  Store with biometric");
        System.out.println("  vault withdraw <id> <bio>        Retrieve with biometric");
        System.out.println("  vault stats                      Show vault statistics");
        System.out.println("  vault clear                      Clear vault");
        System.out.println();
        
        System.out.println("🧬 CIRCUIT EVOLUTION (Hardware Evolution)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  evolve xor                      Evolve XOR gate");
        System.out.println("  evolve adder                    Evolve Full Adder");
        System.out.println("  evolve alu                      Evolve 4-bit ALU");
        System.out.println("  evolve cpu                      Evolve 8-bit CPU");
        System.out.println("  evolve build computer           Build complete 8-bit computer");
        System.out.println("  library show                    Show all evolved circuits");
        System.out.println("  library search <term>           Search circuit library");
        System.out.println("  library stats                   Library statistics");
        System.out.println("  generate <lang> <desc>          Generate living code");
        System.out.println("  prove <circuit>                 Prove circuit is real");
        System.out.println();
        
        System.out.println("🧬 LIVING CODE (Biological Evolution)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  living create <intent>          Generate living code");
        System.out.println("  living evolve <cycles>          Evolve population");
        System.out.println("  living population               Show population info");
        System.out.println("  living best                     Show best circuits");
        System.out.println("  living stats                    Show statistics");
        System.out.println();
        
        System.out.println("🌌 SPATIAL COMPUTING (Creative Engine)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  spatial start                   Start Creative Engine");
        System.out.println("  spatial stop                    Stop Creative Engine");
        System.out.println("  spatial status                  Show spatial status");
        System.out.println("  gravity status                  Gravity engine status");
        System.out.println("  fusion status                   Fusion reactor status");
        System.out.println("  universe                        Show universe map");
        System.out.println("  universe hot                    Show hottest nodes");
        System.out.println("  inject <data> <x> <y> <z>       Inject concept");
        System.out.println();
        
        System.out.println("🌐 REST API (HTTP Interface)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  api start [port]                Start REST API (default 8080)");
        System.out.println("  api stop                        Stop REST API");
        System.out.println("  api status                      Show API status");
        System.out.println("  api help                        Show API documentation");
        System.out.println();
        
        System.out.println("🔒 SECURITY (Military Grade)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  blackhole <file>            7-pass entropy overwrite");
        System.out.println("  blackhole demo              Safe demonstration");
        System.out.println("  scramble                    DoD 5220.22-M erasure");
        System.out.println("  deadman status              Dead Man's Switch status");
        System.out.println("  ghostcode                   Ghost Code Protocol info");
        System.out.println("  secureinfo                  Security overview");
        System.out.println();
        
        System.out.println("💡 GENERAL");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("  help                        Show this help");
        System.out.println("  exit / quit                 Exit the system");
        System.out.println();
        
        System.out.println("📚 EXAMPLES:");
        System.out.println("  absorb package java.util");
        System.out.println("  lazarus start");
        System.out.println("  oracle init");
        System.out.println("  living create \"fibonacci in java\"");
        System.out.println("  spatial start");
        System.out.println("  inject \"Java\" 0 0 0");
        System.out.println("  api start");
        System.out.println();
    }
    
    /**
     * Get list of registered commands
     */
    public static Set<String> getRegisteredCommands() {
        if (!initialized) {
            initialize();
        }
        return handlers.keySet();
    }
}
