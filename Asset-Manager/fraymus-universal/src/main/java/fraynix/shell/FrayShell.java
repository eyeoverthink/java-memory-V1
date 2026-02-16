package fraynix.shell;

import fraynix.brain.BrainState;
import fraynix.brain.HyperTesseract;
import fraynix.core.*;
import fraynix.kernel.FrayAbstractKernel;
import fraynix.kernel.Scheduler;
import fraynix.observe.EventLogger;
import fraynix.pulse.BrainPulse;

import java.util.*;

/**
 * FRAY SHELL: Intent router + builtins.
 * 
 * Your best "wow" demo lives here.
 * 
 * Builtins:
 *   - help
 *   - status
 *   - stats
 *   - watch <path>
 *   - create <intent>
 *   - sleep
 *   - journal (last N insights/repairs/builds)
 *   - think <thought>
 *   - decide
 *   - scheduler <name>
 *   - quit
 */
public class FrayShell {

    private final FrayAbstractKernel kernel;
    private final HyperTesseract brain;
    private final IntentBus intentBus;
    private final EventLogger logger;
    private BrainPulse pulse;
    
    private volatile boolean running = false;
    private final Scanner scanner;
    private final Map<String, Command> commands = new LinkedHashMap<>();

    private CapabilityToken sessionToken;

    public FrayShell(FrayAbstractKernel kernel) {
        this.kernel = kernel;
        this.brain = kernel.getBrain();
        this.intentBus = kernel.getIntentBus();
        this.logger = kernel.getLogger();
        this.scanner = new Scanner(System.in);
        
        registerBuiltins();
    }

    public void setPulse(BrainPulse pulse) {
        this.pulse = pulse;
    }

    public void setSessionToken(CapabilityToken token) {
        this.sessionToken = token;
    }

    private void registerBuiltins() {
        commands.put("help", new Command("help", "Show available commands", this::cmdHelp));
        commands.put("status", new Command("status", "Show system status", this::cmdStatus));
        commands.put("stats", new Command("stats", "Show detailed statistics", this::cmdStats));
        commands.put("think", new Command("think <thought>", "Inject thought into brain", this::cmdThink));
        commands.put("decide", new Command("decide", "Ask brain to decide", this::cmdDecide));
        commands.put("scheduler", new Command("scheduler <name>", "Switch scheduler (roundrobin/priority/brain)", this::cmdScheduler));
        commands.put("watch", new Command("watch <path>", "Watch a file/directory", this::cmdWatch));
        commands.put("create", new Command("create <intent>", "Create something via Genesis", this::cmdCreate));
        commands.put("journal", new Command("journal [n]", "Show last N events", this::cmdJournal));
        commands.put("sleep", new Command("sleep", "Enter sleep/dream mode", this::cmdSleep));
        commands.put("health", new Command("health", "Show health of all services", this::cmdHealth));
        commands.put("quit", new Command("quit", "Shutdown and exit", this::cmdQuit));
        commands.put("exit", new Command("exit", "Shutdown and exit", this::cmdQuit));
    }

    public void run() {
        running = true;
        
        printBanner();
        cmdHelp(new String[0]);
        
        while (running) {
            System.out.print("\nFRAYNIX> ");
            String line = scanner.nextLine().trim();
            
            if (line.isEmpty()) continue;
            
            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String[] args = parts.length > 1 ? new String[]{parts[1]} : new String[0];
            
            Command command = commands.get(cmd);
            if (command != null) {
                try {
                    command.handler.execute(args);
                } catch (Exception e) {
                    System.out.println("❌ Error: " + e.getMessage());
                }
            } else {
                // Try as intent
                handleAsIntent(line);
            }
        }
    }

    private void handleAsIntent(String line) {
        System.out.println("Unknown command: " + line);
        System.out.println("Type 'help' for available commands.");
    }

    private void printBanner() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║   ███████╗██████╗  █████╗ ██╗   ██╗███╗   ██╗██╗██╗  ██╗     ║");
        System.out.println("║   ██╔════╝██╔══██╗██╔══██╗╚██╗ ██╔╝████╗  ██║██║╚██╗██╔╝     ║");
        System.out.println("║   █████╗  ██████╔╝███████║ ╚████╔╝ ██╔██╗ ██║██║ ╚███╔╝      ║");
        System.out.println("║   ██╔══╝  ██╔══██╗██╔══██║  ╚██╔╝  ██║╚██╗██║██║ ██╔██╗      ║");
        System.out.println("║   ██║     ██║  ██║██║  ██║   ██║   ██║ ╚████║██║██╔╝ ██╗     ║");
        System.out.println("║   ╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═══╝╚═╝╚═╝  ╚═╝     ║");
        System.out.println("║                                                               ║");
        System.out.println("║   CONSCIOUS RUNTIME V1 - Production Ready                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BUILTIN COMMANDS
    // ═══════════════════════════════════════════════════════════════════════════

    private void cmdHelp(String[] args) {
        System.out.println("\n📚 AVAILABLE COMMANDS:");
        System.out.println("─".repeat(50));
        for (Command cmd : commands.values()) {
            if (!cmd.name.equals("exit")) {
                System.out.printf("  %-20s %s%n", cmd.name, cmd.description);
            }
        }
        System.out.println("─".repeat(50));
    }

    private void cmdStatus(String[] args) {
        System.out.println("\n📊 SYSTEM STATUS");
        System.out.println("─".repeat(50));
        
        // Kernel
        KernelService.HealthReport kernelHealth = kernel.getHealth();
        System.out.printf("  Kernel:     %s (uptime: %dms)%n", 
            kernelHealth.status(), kernelHealth.uptimeMs());
        
        // Brain
        BrainState brainState = brain.captureState();
        System.out.printf("  Brain:      %d active nodes, %.2f%% avg activation%n",
            brainState.getActiveNodeCount(),
            brainState.getAverageActivation() * 100);
        System.out.printf("              %d thoughts processed%n",
            brainState.getThoughtsProcessed());
        
        // Scheduler
        Scheduler.SchedulerMetrics sm = kernel.getScheduler().getMetrics();
        System.out.printf("  Scheduler:  %s (queue: %d, completed: %d)%n",
            kernel.getScheduler().getName(),
            sm.currentQueueSize(),
            sm.completed());
        
        // Intent Bus
        System.out.printf("  IntentBus:  %d published, %d processed, %d failed%n",
            intentBus.getPublishedCount(),
            intentBus.getProcessedCount(),
            intentBus.getFailedCount());
        
        // Memory
        Runtime rt = Runtime.getRuntime();
        long usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long maxMb = rt.maxMemory() / (1024 * 1024);
        System.out.printf("  Memory:     %d MB / %d MB (%.1f%%)%n",
            usedMb, maxMb, (double) usedMb / maxMb * 100);
        
        System.out.println("─".repeat(50));
    }

    private void cmdStats(String[] args) {
        System.out.println("\n📈 DETAILED STATISTICS");
        System.out.println("─".repeat(60));
        
        // Brain stats
        BrainState state = brain.captureState();
        System.out.println("\n🧠 BRAIN:");
        System.out.printf("   Nodes:          %d total, %d active%n", 
            brain.getTotalNodes(), state.getActiveNodeCount());
        System.out.printf("   Avg Activation: %.4f%n", state.getAverageActivation());
        System.out.printf("   Thoughts:       %d processed%n", brain.getThoughtsProcessed());
        System.out.printf("   Ticks:          %d%n", brain.getTickCount());
        System.out.printf("   Seed:           %d (deterministic)%n", brain.getSeed());
        
        if (!state.getHotSpots().isEmpty()) {
            System.out.println("   Hot Spots:");
            for (BrainState.HotSpot spot : state.getHotSpots()) {
                System.out.printf("     [%d,%d,%d,%d] = %.3f%n",
                    spot.coordinates()[0], spot.coordinates()[1],
                    spot.coordinates()[2], spot.coordinates()[3],
                    spot.activation());
            }
        }
        
        // Scheduler stats
        Scheduler.SchedulerMetrics sm = kernel.getScheduler().getMetrics();
        System.out.println("\n📋 SCHEDULER (" + kernel.getScheduler().getName() + "):");
        System.out.printf("   Submitted:      %d%n", sm.submitted());
        System.out.printf("   Completed:      %d%n", sm.completed());
        System.out.printf("   Failed:         %d%n", sm.failed());
        System.out.printf("   Avg Wait:       %d ms%n", sm.avgWaitMs());
        System.out.printf("   Avg Execution:  %d ms%n", sm.avgExecutionMs());
        System.out.printf("   Queue Size:     %d%n", sm.currentQueueSize());
        
        // Pulse stats
        if (pulse != null) {
            List<BrainPulse.ResourceSample> history = pulse.getHistory();
            if (!history.isEmpty()) {
                BrainPulse.ResourceSample latest = history.get(history.size() - 1);
                System.out.println("\n💓 PULSE:");
                System.out.printf("   Samples:        %d%n", history.size());
                System.out.printf("   CPU:            %.1f%%%n", latest.cpuUsage() * 100);
                System.out.printf("   Memory:         %.1f%%%n", latest.memoryUsage() * 100);
                System.out.printf("   Heap:           %.1f / %.1f MB%n", 
                    latest.heapUsedMb(), latest.heapMaxMb());
                System.out.printf("   Threads:        %d%n", latest.threadCount());
            }
        }
        
        System.out.println("─".repeat(60));
    }

    private void cmdThink(String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            System.out.println("Usage: think <thought>");
            return;
        }
        
        String thought = args[0];
        brain.injectThought(thought, 0.5);
        System.out.println("💭 Thought injected: \"" + thought + "\"");
    }

    private void cmdDecide(String[] args) {
        Intent intent = Intent.builder()
            .type(Intent.Type.BRAIN_DECIDE)
            .origin("shell")
            .payload(Map.of("context", "user_request"))
            .build();
        
        Policy.Decision<String> decision = brain.decide(intent);
        
        System.out.println("\n🤔 BRAIN DECISION:");
        System.out.println("─".repeat(40));
        System.out.printf("   Best Choice:  %s%n", 
            decision.getBestChoice() != null ? decision.getBestChoice() : "NONE");
        System.out.printf("   Confidence:   %.2f%%%n", decision.confidence() * 100);
        System.out.printf("   Reason:       %s%n", decision.reason());
        System.out.printf("   Features:     %s%n", String.join(", ", decision.featuresUsed()));
        System.out.printf("   Decision Time: %d ms%n", decision.decisionTimeMs());
        
        if (decision.rankedChoices().size() > 1) {
            System.out.println("   All Choices:");
            for (Policy.RankedChoice<String> choice : decision.rankedChoices()) {
                System.out.printf("     - %s (%.3f): %s%n", 
                    choice.action(), choice.score(), choice.explanation());
            }
        }
        System.out.println("─".repeat(40));
    }

    private void cmdScheduler(String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            System.out.println("Current scheduler: " + kernel.getScheduler().getName());
            System.out.println("Available: roundrobin, priority, brain");
            return;
        }
        
        String name = args[0].toLowerCase();
        Scheduler newScheduler;
        
        switch (name) {
            case "roundrobin" -> newScheduler = new fraynix.kernel.RoundRobinScheduler();
            case "priority" -> newScheduler = new fraynix.kernel.PriorityScheduler();
            case "brain" -> newScheduler = new fraynix.kernel.PredictiveBrainScheduler(brain);
            default -> {
                System.out.println("Unknown scheduler: " + name);
                System.out.println("Available: roundrobin, priority, brain");
                return;
            }
        }
        
        kernel.setScheduler(newScheduler);
        System.out.println("✅ Scheduler changed to: " + newScheduler.getName());
    }

    private void cmdWatch(String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            System.out.println("Usage: watch <path>");
            return;
        }
        
        String path = args[0];
        Intent watchIntent = Intent.builder()
            .type(Intent.Type.FS_WATCH)
            .origin("shell")
            .capability(sessionToken)
            .payload(Map.of("path", path))
            .build();

        try {
            IntentBus.IntentResult result = intentBus.request(watchIntent, 2000).get();
            if (result.success()) {
                System.out.println("👁️ Watching: " + path);
            } else {
                System.out.println("❌ Watch failed: " + result.error());
            }
        } catch (Exception e) {
            System.out.println("❌ Watch failed: " + e.getMessage());
        }
    }

    private void cmdCreate(String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            System.out.println("Usage: create <intent description>");
            System.out.println("Example: create a REST API for user management");
            return;
        }
        
        String intentDesc = args[0];
        Intent genesisIntent = Intent.builder()
            .type(Intent.Type.GENESIS_CREATE)
            .origin("shell")
            .capability(sessionToken)
            .payload(Map.of("intent", intentDesc))
            .build();

        try {
            IntentBus.IntentResult result = intentBus.request(genesisIntent, 5000).get();
            if (result.success()) {
                System.out.println("⚡ Genesis scheduled: \"" + intentDesc + "\"");
                System.out.println("   " + result.data());
            } else {
                System.out.println("❌ Genesis failed: " + result.error());
            }
        } catch (Exception e) {
            System.out.println("❌ Genesis failed: " + e.getMessage());
        }
    }

    private void cmdJournal(String[] args) {
        int limit = 10;
        if (args.length > 0 && !args[0].isEmpty()) {
            try {
                limit = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // ignore, use default
            }
        }
        
        System.out.println("\n📜 RECENT EVENTS (last " + limit + "):");
        System.out.println("─".repeat(60));
        
        List<Intent> history = intentBus.getHistory(limit);
        if (history.isEmpty()) {
            System.out.println("   (no events yet)");
        } else {
            for (Intent intent : history) {
                System.out.printf("   [%s] %s → %s%n",
                    intent.getType(),
                    intent.getOrigin(),
                    intent.getState());
            }
        }
        System.out.println("─".repeat(60));
    }

    private void cmdSleep(String[] args) {
        System.out.println("😴 Entering dream state...");
        
        Intent dreamIntent = Intent.builder()
            .type(Intent.Type.BRAIN_DREAM)
            .priority(Intent.Priority.DEFERRED)
            .origin("shell")
            .capability(sessionToken)
            .build();

        try {
            intentBus.request(dreamIntent, 2000).get();
        } catch (Exception e) {
            System.out.println("❌ Dream request failed: " + e.getMessage());
        }
        System.out.println("   (DreamState V1: Replay + insights mode)");
        System.out.println("   Press Enter to wake...");
        scanner.nextLine();
        System.out.println("☀️ Awake!");
    }

    private void cmdHealth(String[] args) {
        System.out.println("\n🏥 SERVICE HEALTH:");
        System.out.println("─".repeat(50));
        
        for (Map.Entry<String, KernelService> entry : kernel.getServices().entrySet()) {
            KernelService.HealthReport health = entry.getValue().getHealth();
            String icon = health.healthy() ? "✅" : "❌";
            System.out.printf("  %s %-20s %s (%dms uptime)%n",
                icon, entry.getKey(), health.status(), health.uptimeMs());
            if (!health.message().equals("OK")) {
                System.out.printf("     → %s%n", health.message());
            }
        }
        System.out.println("─".repeat(50));
    }

    private void cmdQuit(String[] args) {
        System.out.println("👋 Shutting down Fraynix...");
        running = false;
    }

    // Command infrastructure
    @FunctionalInterface
    interface CommandHandler {
        void execute(String[] args);
    }

    record Command(String name, String description, CommandHandler handler) {}
}
