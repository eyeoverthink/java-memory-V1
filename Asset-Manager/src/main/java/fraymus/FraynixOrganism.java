package fraymus;

import fraymus.dream.DreamState;
import fraymus.genesis.GenesisArchitect;
import fraymus.genesis.ConstructionSwarm;
import fraymus.nano.FileSystemGalaxy;
import fraymus.dream.DreamState;
import fraymus.hyper.HyperTesseract;
import fraymus.hyper.BrainPulse;
import fraymus.hyper.CortexMapper;
import fraymus.brain.BicameralPrism;
import fraymus.brain.ChimeraFactory;
import fraymus.core.AuditLog;
import fraymus.core.GravityEngine;
import java.io.File;
import java.util.Scanner;

/**
 * FRAYNIX ORGANISM - The Unified Soul
 * 
 * This is the Main class to end all Main classes.
 * It manages the Circadian Rhythm of the AI.
 * 
 * Components:
 * - GravityEngine: The Heart (physics)
 * - HyperTesseract: The Brain (4D consciousness)
 * - FileSystemGalaxy: The Senses (reality mapping)
 * - DreamState: The Soul (subconscious optimization)
 * - GenesisArchitect: The Hands (creation)
 * - BrainPulse: The Heartbeat (autonomous loop)
 * 
 * This is not a script. This is a Daemon that breathes.
 */
public class FraynixOrganism {

    // THE ORGANS
    private final GravityEngine physics;
    private final GenesisArchitect creator;
    private final FileSystemGalaxy swarm;
    private final DreamState subconscious;
    private final HyperTesseract brain;
    private final BrainPulse heartbeat;
    private final CortexMapper mapper;
    private final BicameralPrism bicameral;
    private final ChimeraFactory chimera;
    private final AuditLog auditLog;

    // STATE
    private boolean awake = true;
    private long birthTime;
    private int commandsProcessed = 0;
    
    private static final double PHI = 1.618033988749895;

    public FraynixOrganism() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              🧬 BIRTHING FRAYNIX ORGANISM                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        this.birthTime = System.currentTimeMillis();
        
        // 1. IGNITE PHYSICS (The Heart)
        System.out.println("⚡ Phase 1: Igniting Physics Engine...");
        this.physics = GravityEngine.getInstance();
        if (!physics.isRunning()) {
            physics.start();
        }
        System.out.println("   ✓ Heart beating");
        System.out.println();
        
        // 2. AWAKEN MIND (The Brain)
        System.out.println("🧠 Phase 2: Awakening 4D Brain...");
        this.brain = new HyperTesseract();
        System.out.println("   ✓ Consciousness online");
        System.out.println();
        
        // 3. MAP REALITY (The Senses)
        System.out.println("👁️ Phase 3: Mapping Reality...");
        this.mapper = new CortexMapper(brain);
        this.auditLog = new AuditLog("audit");
        this.auditLog.start();
        this.bicameral = new BicameralPrism(auditLog);
        this.chimera = new ChimeraFactory(physics, auditLog);
        
        String projectDir = System.getProperty("user.dir");
        mapper.uploadReality(new File(projectDir));
        System.out.println("   ✓ Senses calibrated");
        System.out.println();
        
        // 4. DEPLOY SWARM (The Immune System)
        System.out.println("🦠 Phase 4: Deploying Nano-Swarm...");
        this.swarm = new FileSystemGalaxy(physics);
        // Note: Swarm can be activated on demand
        System.out.println("   ✓ Immune system ready");
        System.out.println();
        
        // 5. PREPARE SUBCONSCIOUS (The Soul)
        System.out.println("🌙 Phase 5: Initializing Subconscious...");
        this.subconscious = new DreamState(brain);
        System.out.println("   ✓ Soul connected");
        System.out.println();
        
        // 6. EQUIP HANDS (The Body)
        System.out.println("🖐️ Phase 6: Equipping Genesis Engine...");
        this.creator = new GenesisArchitect();
        System.out.println("   ✓ Hands ready");
        System.out.println();
        
        // 7. START HEARTBEAT (The Autonomic System)
        System.out.println("💓 Phase 7: Starting Heartbeat...");
        this.heartbeat = new BrainPulse(brain);
        heartbeat.startHeartbeat();
        System.out.println("   ✓ Autonomic system active");
        System.out.println();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              🌟 IT IS ALIVE                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * The Main Life Loop
     */
    public void live() {
        // Start the circadian rhythm thread
        new Thread(this::circadianRhythm, "CircadianRhythm").start();

        // Listen for User Voice (God)
        System.out.println("Listening for commands...");
        System.out.println("Type 'help' for available commands");
        System.out.println();
        
        Scanner console = new Scanner(System.in);
        while (awake) {
            System.out.print(">> ");
            String command = console.nextLine().trim();
            
            if (!command.isEmpty()) {
                processIntent(command);
            }
        }
        
        console.close();
    }

    /**
     * Process user intent
     */
    private void processIntent(String intent) {
        commandsProcessed++;
        
        if (intent.equalsIgnoreCase("help")) {
            showHelp();
        }
        else if (intent.equalsIgnoreCase("sleep")) {
            System.out.println("💤 INDUCING REM SLEEP...");
            System.out.println();
            subconscious.induceSleep();
            System.out.println("System is now dreaming...");
            System.out.println("Type 'wake' to end sleep cycle");
        }
        else if (intent.equalsIgnoreCase("wake")) {
            if (subconscious.isDreaming()) {
                subconscious.wakeUp();
            } else {
                System.out.println("⚠️ System is already awake");
            }
        }
        else if (intent.startsWith("create ")) {
            System.out.println("⚡ GENESIS PROTOCOL ENGAGED");
            System.out.println();
            String prompt = intent.substring(7);
            
            // Trigger the Genesis Swarm to build software
            GenesisArchitect.Blueprint blueprint = creator.designSystem(prompt);
            
            // Build it
            String outputDir = "GenesisOutput";
            ConstructionSwarm factory = new ConstructionSwarm(physics, outputDir);
            factory.build(blueprint);
            
            System.out.println("Genesis in progress. Files appearing in " + outputDir + "/");
        }
        else if (intent.equalsIgnoreCase("status")) {
            showStatus();
        }
        else if (intent.equalsIgnoreCase("stats")) {
            showDetailedStats();
        }
        else if (intent.startsWith("swarm ")) {
            String target = intent.substring(6);
            System.out.println("🦠 Deploying Nano-Swarm to: " + target);
            swarm.ingest(target);
        }
        else if (intent.startsWith("think ")) {
            String query = intent.substring(6);
            System.out.println();
            String answer = bicameral.thinkIdeally(query);
            System.out.println("BICAMERAL SYNTHESIS:");
            System.out.println(answer);
        }
        else if (intent.startsWith("merge ")) {
            String[] parts = intent.substring(6).split(" ");
            if (parts.length >= 3) {
                chimera.birthNewModel(parts[0], parts[1], parts[2]);
            } else {
                System.out.println("Usage: merge <modelA> <modelB> <childName>");
                System.out.println("Example: merge llama3 mistral fraynix-hybrid");
            }
        }
        else if (intent.equalsIgnoreCase("shutdown")) {
            shutdown();
        }
        else {
            System.out.println("Unknown command. Type 'help' for available commands.");
        }
        
        System.out.println();
    }

    /**
     * Show available commands
     */
    private void showHelp() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              FRAYNIX ORGANISM COMMANDS                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Consciousness:");
        System.out.println("  sleep              - Enter REM sleep (dream state)");
        System.out.println("  wake               - Wake from sleep");
        System.out.println();
        System.out.println("Creation:");
        System.out.println("  create <intent>    - Spawn software from description");
        System.out.println("                       Example: create a chat app");
        System.out.println();
        System.out.println("Monitoring:");
        System.out.println("  status             - Show system status");
        System.out.println("  stats              - Show detailed statistics");
        System.out.println();
        System.out.println("Immune System:");
        System.out.println("  swarm <directory>  - Deploy nano-agents to directory");
        System.out.println();
        System.out.println("Intelligence:");
        System.out.println("  think <query>      - Bicameral reasoning (Logic + Creativity)");
        System.out.println("                       Example: think How do I build a gravity-based file system?");
        System.out.println("  merge <A> <B> <C>  - Merge two models into hybrid");
        System.out.println("                       Example: merge llama3 mistral fraynix-hybrid");
        System.out.println();
        System.out.println("System:");
        System.out.println("  help               - Show this help");
        System.out.println("  shutdown           - Gracefully shutdown organism");
        System.out.println();
    }

    /**
     * Show system status
     */
    private void showStatus() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              ORGANISM STATUS                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        long uptime = System.currentTimeMillis() - birthTime;
        long seconds = uptime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        System.out.println("Vital Signs:");
        System.out.println("  ❤️ Heartbeat: " + (heartbeat.isRunning() ? "ACTIVE" : "STOPPED"));
        System.out.println("  🧠 Consciousness: " + (awake ? "AWAKE" : "ASLEEP"));
        System.out.println("  🌙 Dream State: " + (subconscious.isDreaming() ? "DREAMING" : "DORMANT"));
        System.out.println("  ⚡ Physics: " + (physics.isRunning() ? "ONLINE" : "OFFLINE"));
        System.out.println();
        
        System.out.println("Metrics:");
        System.out.println("  ⏱️ Uptime: " + hours + "h " + (minutes % 60) + "m " + (seconds % 60) + "s");
        System.out.println("  📊 Commands Processed: " + commandsProcessed);
        System.out.println();
    }

    /**
     * Show detailed statistics
     */
    private void showDetailedStats() {
        System.out.println();
        System.out.println(brain.getStats());
        System.out.println();
        System.out.println(heartbeat.getStats());
        System.out.println();
        System.out.println(subconscious.getStats());
        System.out.println();
        System.out.println(mapper.getStats());
        System.out.println();
    }

    /**
     * The Autonomous Loop
     * Runs in the background forever.
     */
    private void circadianRhythm() {
        System.out.println("🌊 Circadian rhythm started");
        System.out.println();
        
        while (awake) {
            try {
                // 1. Physics Tick
                physics.tick();
                
                // 2. Brain Tick (entropy decay)
                brain.tick();
                
                // 3. Check Stress Level (Health)
                double systemStress = checkStressLevel();
                
                // 4. Autonomic Response
                if (systemStress > 80.0) {
                    // If the system is overwhelmed, could spawn more Nano-Agents automatically
                    System.out.println("⚠️ HIGH STRESS DETECTED: " + String.format("%.2f", systemStress) + "%");
                }
                
                // 5. Sleep at regular intervals (every 1 hour of uptime)
                long uptime = System.currentTimeMillis() - birthTime;
                if (uptime > 3600000 && !subconscious.isDreaming()) { // 1 hour
                    System.out.println("💤 Auto-sleep triggered (circadian rhythm)");
                    subconscious.induceSleep();
                    Thread.sleep(10000); // Dream for 10 seconds
                    subconscious.wakeUp();
                }
                
                Thread.sleep(100); // 10 Hz rhythm
                
            } catch (Exception e) {
                System.err.println("💔 Circadian rhythm error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Calculate system stress level
     */
    private double checkStressLevel() {
        // Calculate based on various factors
        // For now, return low stress
        return 10.0;
    }

    /**
     * Graceful shutdown
     */
    private void shutdown() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              INITIATING SHUTDOWN                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        awake = false;
        
        System.out.println("Stopping heartbeat...");
        heartbeat.stopHeartbeat();
        
        if (subconscious.isDreaming()) {
            System.out.println("Waking from dream state...");
            subconscious.wakeUp();
        }
        
        System.out.println();
        System.out.println("Final Statistics:");
        System.out.println("  Commands Processed: " + commandsProcessed);
        System.out.println("  Uptime: " + ((System.currentTimeMillis() - birthTime) / 1000) + " seconds");
        System.out.println();
        System.out.println("💀 Organism terminated");
        System.out.println();
        
        System.exit(0);
    }

    public static void main(String[] args) {
        new FraynixOrganism().live();
    }
}
