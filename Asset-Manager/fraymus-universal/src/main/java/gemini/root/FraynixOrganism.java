package gemini.root;

import gemini.root.hyper.*;
import gemini.root.physics.*;
import gemini.root.limbs.ClawConnector;
import gemini.root.viz.CortexServer;
import gemini.root.genesis.GenesisArchitect;
import gemini.root.genesis.GenesisCritic;
import gemini.root.genesis.ConstructionSwarm;
import gemini.root.memory.BlackBox;

import java.io.File;
import java.util.Scanner;

/**
 * FRAYNIX ORGANISM: The Complete Digital Lifeform
 * 
 * Integrates all subsystems into a living, thinking entity:
 *   - HyperTesseract (4D Brain - 2,048 nodes)
 *   - CortexMapper (Reality → Brain upload)
 *   - BrainPulse (4-phase cognitive heartbeat)
 *   - DreamState (REM sleep optimization)
 *   - HiveGravityEngine (Physics simulation)
 *   - ClawParticle (OpenClaw execution body)
 * 
 * This is AGI behavior: watching, simulating, deciding, acting, DREAMING.
 */
public class FraynixOrganism {

    private final HyperTesseract brain;
    private final CortexMapper cortex;
    private final BrainPulse heartbeat;
    private final DreamState dreamEngine;
    private final HiveGravityEngine physics;
    private final ClawParticle claw;
    private final CortexServer visualizer;
    private final GenesisArchitect architect;
    
    private volatile boolean alive = false;
    private boolean visualizerEnabled = true;

    public FraynixOrganism() {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   🧬 FRAYNIX ORGANISM: DIGITAL LIFEFORM GENESIS       ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. Create the 4D Brain
        this.brain = new HyperTesseract();
        
        // 2. Create the Cortex Mapper
        this.cortex = new CortexMapper(brain);
        
        // 3. Create the Heartbeat
        this.heartbeat = new BrainPulse(brain, 100);
        
        // 4. Create the Dream Engine
        this.dreamEngine = new DreamState(brain);
        
        // 5. Create the Physics Engine
        this.physics = new HiveGravityEngine(50);
        
        // 6. Create the Claw (Body/Executor)
        this.claw = new ClawParticle(50, 50, 50);
        physics.register(claw);
        
        // 7. Create the Visualizer (Optic Nerve)
        this.visualizer = new CortexServer(physics, brain);
        
        // 8. Create the Genesis Architect (Creator)
        this.architect = new GenesisArchitect();
        
        System.out.println("\n✅ ORGANISM ASSEMBLED");
        System.out.println("   Brain: 2,048 nodes (4×8×8×8)");
        System.out.println("   Lobes: Frontal, Hippocampus, Visual, Ego");
        System.out.println("   Body: OpenClaw executor");
        System.out.println("   Heart: 4-phase cognitive loop");
        System.out.println("   Dreams: REM sleep engine");
        System.out.println("   Eyes: Cortex visualizer (port 8888)");
        System.out.println("   Hands: Genesis architect");
    }

    /**
     * Wake the organism and upload reality
     */
    public void wake(File rootDirectory) {
        System.out.println("\n🌅 ORGANISM WAKING...");
        
        // Upload file system into Hippocampus
        cortex.uploadReality(rootDirectory);
        
        // Start heartbeat
        heartbeat.start();
        
        // Start physics
        physics.start();
        
        // Start visualizer
        if (visualizerEnabled) {
            try {
                visualizer.start();
                System.out.println("👁️ Visualizer started on port 8888");
            } catch (Exception e) {
                System.out.println("⚠️ Visualizer failed to start: " + e.getMessage());
            }
        }
        
        alive = true;
        
        System.out.println("\n💓 ORGANISM IS ALIVE");
        System.out.println("   Files in memory: " + cortex.getFilesUploaded());
        System.out.println("   Tools available: " + cortex.getToolsUploaded());
        System.out.println("   Heartbeat: ACTIVE");
        System.out.println("   Visualizer: " + (visualizerEnabled ? "ws://localhost:8888" : "DISABLED"));
        System.out.println("   Ready for commands.\n");
    }

    /**
     * Put organism to sleep (REM mode)
     */
    public void sleep() {
        System.out.println("\n🌙 INDUCING SLEEP...");
        heartbeat.stop();
        dreamEngine.induceSleep();
    }

    /**
     * Wake from sleep
     */
    public void wakeFromSleep() {
        dreamEngine.wakeUp();
        heartbeat.start();
        System.out.println("💓 Heartbeat resumed.");
    }

    /**
     * Inject a thought into the brain
     */
    public void think(String thought) {
        brain.think(thought);
    }

    /**
     * Remember something
     */
    public void remember(String memory) {
        brain.remember(memory);
    }

    /**
     * Queue an action for execution
     */
    public void act(String action) {
        heartbeat.queueAction(action);
    }

    /**
     * Introspect - what is the Ego seeing?
     */
    public void introspect() {
        brain.introspect();
    }

    /**
     * Get status
     */
    public void status() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("   FRAYNIX ORGANISM STATUS");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("Alive: " + alive);
        System.out.println("Heartbeat: " + (heartbeat.isBeating() ? "BEATING" : "STOPPED"));
        System.out.println("Pulse count: " + heartbeat.getPulseCount());
        System.out.println("System entropy: " + String.format("%.2f", heartbeat.getSystemEntropy()));
        System.out.println("Dreaming: " + dreamEngine.isDreaming());
        System.out.println("Physics particles: " + physics.getParticleCount());
        System.out.println("Claw busy: " + claw.isBusy());
        System.out.println("Tasks executed: " + claw.getTasksExecuted());
        brain.printStatus();
    }

    /**
     * Shutdown
     */
    public void shutdown() {
        System.out.println("\n💀 ORGANISM SHUTTING DOWN...");
        alive = false;
        heartbeat.stop();
        physics.stop();
        if (dreamEngine.isDreaming()) {
            dreamEngine.wakeUp();
        }
        brain.shutdown();
        System.out.println("💀 ORGANISM OFFLINE");
    }

    // ========== MAIN ==========

    public static void main(String[] args) {
        FraynixOrganism organism = new FraynixOrganism();
        
        // Determine root directory
        File root;
        if (args.length > 0) {
            root = new File(args[0]);
        } else {
            root = new File(System.getProperty("user.dir"));
        }
        
        // Wake the organism
        organism.wake(root);
        
        // Interactive command loop
        Scanner scanner = new Scanner(System.in);
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   FRAYNIX COMMAND INTERFACE                           ║");
        System.out.println("║   Commands: think, remember, act, sleep, wake,        ║");
        System.out.println("║             introspect, status, dream, create,        ║");
        System.out.println("║             memory, recall, quit                      ║");
        System.out.println("║   Visualizer: Open web/fraymus_eye.html in browser    ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        
        while (organism.alive) {
            System.out.print("\nFRAYNIX> ");
            String line = scanner.nextLine().trim();
            
            if (line.isEmpty()) continue;
            
            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : "";
            
            switch (cmd) {
                case "think":
                    if (arg.isEmpty()) {
                        System.out.println("Usage: think <thought>");
                    } else {
                        organism.think(arg);
                    }
                    break;
                    
                case "remember":
                    if (arg.isEmpty()) {
                        System.out.println("Usage: remember <memory>");
                    } else {
                        organism.remember(arg);
                    }
                    break;
                    
                case "act":
                    if (arg.isEmpty()) {
                        System.out.println("Usage: act <action>");
                    } else {
                        organism.act(arg);
                    }
                    break;
                    
                case "sleep":
                    organism.sleep();
                    break;
                    
                case "wake":
                    organism.wakeFromSleep();
                    break;
                    
                case "dream":
                    // Show dream journal
                    java.util.List<DreamState.Epiphany> insights = organism.dreamEngine.getInsights();
                    if (insights.isEmpty()) {
                        System.out.println("No insights yet. Try 'sleep' first.");
                    } else {
                        for (DreamState.Epiphany e : insights) {
                            System.out.printf("[%s] %.0f%% - %s%n", e.type, e.confidence * 100, e.description);
                        }
                    }
                    break;
                    
                case "introspect":
                    organism.introspect();
                    break;
                    
                case "status":
                    organism.status();
                    break;
                    
                case "create":
                    // Genesis: create software from intent (with Critic review)
                    if (arg.isEmpty()) {
                        System.out.println("Usage: create <intent>");
                        System.out.println("Example: create a real-time chat app with encryption");
                    } else {
                        System.out.println("⚡ GENESIS PROTOCOL ENGAGED: \"" + arg + "\"");
                        
                        // 1. Architect designs the blueprint
                        GenesisArchitect.Blueprint plan = organism.architect.designSystem(arg);
                        
                        // 2. Critic reviews (System 2 adversarial check)
                        GenesisCritic critic = new GenesisCritic(organism.architect.getMemory());
                        GenesisCritic.CritiqueResult review = critic.reviewBlueprint(plan);
                        
                        if (!review.approved) {
                            System.out.println("\n🛑 GENESIS HALTED. The Architect must refine the plan.");
                            organism.architect.recordOutcome(arg, false, "REJECTED by Critic: " + String.join("; ", review.issues));
                            organism.visualizer.pushEvent("GENESIS_REJECTED", arg);
                            break;
                        }
                        
                        // 3. Swarm builds the approved blueprint
                        ConstructionSwarm swarm = new ConstructionSwarm(organism.physics, new File("GenesisOutput"));
                        swarm.build(plan);
                        int built = swarm.getCompletedCount();
                        
                        // 4. Record outcome
                        boolean success = built == plan.modules.size();
                        organism.architect.recordOutcome(arg, success, 
                            "Built " + built + "/" + plan.modules.size() + " modules");
                        
                        System.out.println("\n✅ GENESIS COMPLETE: " + built + " modules created");
                        organism.visualizer.pushEvent("GENESIS_COMPLETE", arg + " (" + built + " modules)");
                    }
                    break;
                    
                case "query":
                    // Query a voxel: query w x y z
                    String[] coords = arg.split("\\s+");
                    if (coords.length == 4) {
                        try {
                            int w = Integer.parseInt(coords[0]);
                            int x = Integer.parseInt(coords[1]);
                            int y = Integer.parseInt(coords[2]);
                            int z = Integer.parseInt(coords[3]);
                            organism.cortex.queryVoxel(w, x, y, z);
                        } catch (NumberFormatException e) {
                            System.out.println("Usage: query <w> <x> <y> <z>");
                        }
                    } else {
                        System.out.println("Usage: query <w> <x> <y> <z>");
                    }
                    break;
                    
                case "find":
                    // Find a file
                    if (arg.isEmpty()) {
                        System.out.println("Usage: find <filename>");
                    } else {
                        int[] loc = organism.cortex.locateFile(arg);
                        if (loc != null) {
                            System.out.printf("Found at [%d][%d][%d][%d]%n", loc[0], loc[1], loc[2], loc[3]);
                        } else {
                            System.out.println("Not found in memory.");
                        }
                    }
                    break;

                case "memory":
                    // Show BlackBox memory stats
                    BlackBox mem = organism.architect.getMemory();
                    System.out.println("\n📚 BLACK BOX MEMORY STATUS");
                    System.out.println("   Total memories: " + mem.getMemoryCount());
                    System.out.println("   Loaded from disk: " + mem.getMemoriesLoaded());
                    System.out.println("   Created this session: " + mem.getMemoriesCreated());
                    System.out.println("   Recalls performed: " + mem.getRecallsPerformed());
                    System.out.println("\n   Recent successes:");
                    for (BlackBox.Memory m : mem.getSuccesses().stream().limit(5).toList()) {
                        System.out.println("      ✓ " + m.context);
                    }
                    System.out.println("   Recent failures:");
                    for (BlackBox.Memory m : mem.getFailures().stream().limit(5).toList()) {
                        System.out.println("      ✗ " + m.context);
                    }
                    break;

                case "recall":
                    // Search BlackBox for past wisdom
                    if (arg.isEmpty()) {
                        System.out.println("Usage: recall <query>");
                        System.out.println("Example: recall Python game");
                    } else {
                        String wisdom = organism.architect.getMemory().recall(arg);
                        System.out.println("\n" + wisdom);
                    }
                    break;
                    
                case "quit":
                case "exit":
                    organism.shutdown();
                    break;
                    
                default:
                    System.out.println("Unknown command: " + cmd);
                    System.out.println("Commands: think, remember, act, sleep, wake, introspect, status, dream, query, find, quit");
            }
        }
        
        scanner.close();
        System.out.println("Goodbye.");
    }
}
