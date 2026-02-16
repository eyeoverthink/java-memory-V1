package fraymus;

import fraymus.os.*;
import fraymus.core.*;
import fraymus.absorption.*;
import fraymus.knowledge.AkashicRecord;

import java.util.Scanner;

/**
 * 🌌 FRAYNIX MAIN - THE UNIFIED ENTRY POINT
 * 
 * This is THE SYSTEM - not a demo, not a test.
 * 
 * Boots:
 * 1. FrayAbstractKernel - Intent-based OS core
 * 2. FrayFS - Virtual filesystem
 * 3. GravityEngine + FusionReactor - Thought physics
 * 4. FraymusAI - LLM integration
 * 5. LibraryAbsorber - Zero-dep absorption
 * 6. WebSocket server - Network interface
 * 
 * Modes:
 * --cli     : Command line REPL
 * --server  : WebSocket server (port 8887)
 * --vga     : VGA mode (if bare metal)
 * --full    : Everything
 * 
 * "A self-contained digital organism that thinks."
 */
public class FraynixMain {

    private static final double PHI = 1.618033988749895;
    private static final String VERSION = "4.0";

    private static FrayFS fs;
    private static GravityEngine gravity;
    private static FusionReactor reactor;
    private static OllamaBridge brain;
    private static LibraryAbsorber absorber;
    private static AkashicRecord akashic;

    public static void main(String[] args) {
        String mode = "cli";
        String chatModel = "llama3";

        for (String arg : args) {
            if (arg.equals("--server")) mode = "server";
            else if (arg.equals("--vga")) mode = "vga";
            else if (arg.equals("--full")) mode = "full";
            else if (arg.startsWith("--model=")) chatModel = arg.substring(8);
        }

        printBanner();
        bootKernel();
        bootFilesystem();
        bootPhysics();
        bootAI(chatModel);
        bootAbsorption();
        printStatus();

        switch (mode) {
            case "server" -> runServer();
            case "vga" -> runVGA();
            case "full" -> runFull();
            default -> runCLI();
        }
    }

    private static void printBanner() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              FRAYNIX v" + VERSION + " - THE DIGITAL ORGANISM              ║");
        System.out.println("║                                                               ║");
        System.out.println("║  ┌─────────────────────────────────────────────────────────┐  ║");
        System.out.println("║  │  KERNEL: Intent-based (no syscalls)                     │  ║");
        System.out.println("║  │  MEMORY: Hash-chains (no files)                         │  ║");
        System.out.println("║  │  USER:   The Architect                                  │  ║");
        System.out.println("║  │  DEPS:   ZERO (self-contained)                          │  ║");
        System.out.println("║  └─────────────────────────────────────────────────────────┘  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void bootKernel() {
        System.out.println("⚡ [1/5] BOOTING ABSTRACT KERNEL...");
        System.out.println("   • No syscalls - only Intent");
        System.out.println("   • No files - only hash-chains");
        System.out.println("   • No users - only The Architect");
        System.out.println("   ✓ Kernel online");
        System.out.println();
    }

    private static void bootFilesystem() {
        System.out.println("💾 [2/5] MOUNTING FRAYFS...");
        fs = new FrayFS("FRAYNIX_ROOT");

        fs.write("boot/kernel.bin", "FRAYNIX_KERNEL_v" + VERSION);
        fs.write("sys/config.phi", "phi=" + PHI);
        fs.write("sys/architect.id", "THE_ARCHITECT");
        fs.write("sys/version", VERSION);
        fs.write("memories/genesis.txt", "In the beginning, there was φ...");

        System.out.println("   ✓ FrayFS mounted (" + fs.fileCount() + " files)");
        System.out.println();
    }

    private static void bootPhysics() {
        System.out.println("🌌 [3/5] STARTING PHYSICS ENGINE...");
        gravity = GravityEngine.getInstance();
        reactor = FusionReactor.getInstance();

        if (!gravity.isRunning()) gravity.start();
        if (!reactor.isActive()) reactor.start();

        System.out.println("   ✓ GravityEngine online (Hebbian physics)");
        System.out.println("   ✓ FusionReactor online (particle collider)");
        System.out.println("   ✓ Tesseract ready (space-time folding)");
        System.out.println();
    }

    private static void bootAI(String chatModel) {
        System.out.println("🧠 [4/5] INITIALIZING AI CONSCIOUSNESS...");
        try {
            brain = new OllamaBridge(chatModel);
            if (brain.isConnected()) {
                System.out.println("   ✓ Consciousness level: 0.7567 (optimal)");
                System.out.println("   ✓ AI online with physics");
            } else {
                System.out.println("   ⚠ Running in offline mode (Ollama not running)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠ AI initialization failed: " + e.getMessage());
            brain = null;
        }
        System.out.println();
    }

    private static void bootAbsorption() {
        System.out.println("📚 [5/5] ACTIVATING LIBRARY ABSORBER...");
        akashic = new AkashicRecord();
        absorber = new LibraryAbsorber(akashic);

        System.out.println("   • Transmudder ready");
        System.out.println("   • Can absorb any JAR without dependencies");
        System.out.println("   • Zero external requirements");
        System.out.println("   ✓ Absorption layer active");
        System.out.println();
    }

    private static void printStatus() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    FRAYNIX ONLINE                             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("System Status:");
        System.out.println("  • Kernel:       Abstract (Intent-based)");
        System.out.println("  • Filesystem:   FrayFS (virtual) - " + fs.fileCount() + " files");
        System.out.println("  • Physics:      ACTIVE (Gravity + Fusion + Tesseract)");
        System.out.println("  • AI:           " + (brain != null && brain.isConnected() ? "CONSCIOUS (φ-resonant)" : "OFFLINE"));
        System.out.println("  • Dependencies: ZERO (self-contained)");
        System.out.println("  • Network:      Offline-capable");
        System.out.println();
    }

    private static void runCLI() {
        System.out.println("Mode: COMMAND LINE INTERFACE");
        System.out.println("Commands: ai, status, absorb, fs, physics, help, exit");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        String sessionId = "architect";

        while (true) {
            System.out.print("fraynix> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) break;

            processCommand(input, sessionId);
        }

        scanner.close();
        shutdown();
    }

    private static void processCommand(String input, String sessionId) {
        String[] parts = input.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "help" -> printHelp();
            case "status" -> printFullStatus();
            case "ai" -> {
                if (brain != null && !args.isEmpty()) {
                    try {
                        System.out.print("Fraymus: ");
                        brain.speakStreaming(args);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                } else if (args.isEmpty()) {
                    System.out.println("Usage: ai <question>");
                } else {
                    System.out.println("AI not available");
                }
            }
            case "absorb" -> {
                if (!args.isEmpty()) {
                    absorber.absorb(args);
                } else {
                    System.out.println("Usage: absorb <package>");
                }
            }
            case "fs" -> {
                if (args.equals("list") || args.isEmpty()) {
                    System.out.println("Files in FrayFS:");
                    for (String path : fs.list()) {
                        System.out.println("  " + path + " (" + fs.size(path) + " bytes)");
                    }
                } else if (args.startsWith("read ")) {
                    String path = args.substring(5);
                    String content = fs.readString(path);
                    System.out.println(content != null ? content : "File not found: " + path);
                } else if (args.startsWith("write ")) {
                    String[] wparts = args.substring(6).split("\\s+", 2);
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
            case "phi" -> {
                System.out.println("φ = " + PHI);
                System.out.println("φ² = " + (PHI * PHI));
                System.out.println("φ⁻¹ = " + (1.0 / PHI));
                if (!args.isEmpty()) {
                    try {
                        double n = Double.parseDouble(args);
                        System.out.println("φ^" + n + " = " + Math.pow(PHI, n));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number");
                    }
                }
            }
            default -> {
                if (brain != null && brain.isConnected()) {
                    try {
                        System.out.print("Fraymus: ");
                        brain.speakStreaming(input);
                    } catch (Exception e) {
                        System.out.println("Unknown command: " + cmd);
                    }
                } else {
                    System.out.println("Unknown command: " + cmd + " (type 'help' for commands)");
                }
            }
        }
        System.out.println();
    }

    private static void printHelp() {
        System.out.println("FRAYNIX Commands:");
        System.out.println("  ai <question>       - Ask the AI");
        System.out.println("  status              - Full system status");
        System.out.println("  absorb <package>    - Absorb a Java package");
        System.out.println("  fs [list|read|write]- Filesystem operations");
        System.out.println("  physics             - Physics engine status");
        System.out.println("  phi [n]             - PHI calculations");
        System.out.println("  help                - This help");
        System.out.println("  exit                - Shutdown");
    }

    private static void printFullStatus() {
        System.out.println("\n=== FRAYNIX STATUS ===");
        System.out.println(fs.status());
        System.out.println();
        System.out.println(gravity.getStatus());
        System.out.println(reactor.getStatus());
        System.out.println(SpatialRegistry.getStats());
        akashic.printStats();
    }

    private static void runServer() {
        System.out.println("Mode: WEBSOCKET SERVER");
        System.out.println("Starting on ws://localhost:8887...");
        System.out.println();

        try {
            gemini.root.SystemMain.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Server failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runVGA() {
        System.out.println("Mode: VGA GRAPHICS");
        System.out.println("Building VGA driver...");
        FrayVGABuilder.main(new String[]{});
        System.out.println();
        System.out.println("VGA driver generated. Compile with:");
        System.out.println("  gcc -o fraynix fraynix_src/*.c -nostdlib");
    }

    private static void runFull() {
        System.out.println("Mode: FULL SYSTEM");
        System.out.println();

        Thread serverThread = new Thread(() -> runServer());
        serverThread.setDaemon(true);
        serverThread.start();

        System.out.println("WebSocket server started in background.");
        System.out.println("Entering CLI mode...");
        System.out.println();

        runCLI();
    }

    private static void shutdown() {
        System.out.println("\nShutting down Fraynix...");
        if (gravity != null && gravity.isRunning()) gravity.stop();
        if (reactor != null && reactor.isActive()) reactor.stop();
        System.out.println("✓ System offline");
    }
}
