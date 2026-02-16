package fraynix;

import fraynix.brain.HyperTesseract;
import fraynix.core.*;
import fraynix.core.impl.DefaultIntentBus;
import fraynix.dream.DreamState;
import fraynix.fs.FrayFS;
import fraynix.genesis.Blueprint;
import fraynix.genesis.GenesisArchitectV1;
import fraynix.kernel.*;
import fraynix.observe.EventLogger;
import fraynix.pulse.BrainPulse;
import fraynix.shell.FrayShell;
import fraynix.swarm.NanoSwarm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * FRAYNIX OS V1: The Conscious Runtime
 * 
 * A user-space "OS-like" substrate with:
 *   - Policy-driven decisions
 *   - Simulation capabilities
 *   - Agent orchestration
 *   - Code generation
 * 
 * V1 wins on:
 *   - Automation
 *   - Self-repair
 *   - Intent execution
 *   - Observability
 *   - Repeatability
 */
public class FraynixOS {

    private final HyperTesseract brain;
    private final DefaultIntentBus intentBus;
    private final EventLogger logger;
    private final FrayAbstractKernel kernel;
    private final BrainPulse pulse;
    private final FrayFS fs;
    private final NanoSwarm swarm;
    private final DreamState dream;
    private final GenesisArchitectV1 genesis;
    private final FrayShell shell;

    private final CapabilityToken shellToken;
    
    private final long seed;

    public FraynixOS() throws IOException {
        this(System.currentTimeMillis());
    }

    public FraynixOS(long seed) throws IOException {
        this.seed = seed;
        
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("   FRAYNIX OS V1 - INITIALIZING");
        System.out.println("   Seed: " + seed + " (deterministic mode)");
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println();
        
        // 1. Initialize logging (observability first!)
        this.logger = new EventLogger();
        
        // 2. Initialize brain (policy engine)
        this.brain = new HyperTesseract(seed);
        brain.setLogger(logger);
        
        // 3. Initialize intent bus (nervous system)
        this.intentBus = new DefaultIntentBus();
        intentBus.setLogger(logger);
        
        // 4. Initialize kernel (execution substrate)
        // Start with PredictiveBrainScheduler for "conscious mode"
        Scheduler scheduler = new PredictiveBrainScheduler(brain);
        this.kernel = new FrayAbstractKernel(brain, intentBus, logger, scheduler, 4);
        
        // 5. Initialize pulse (heartbeat)
        this.pulse = new BrainPulse(brain, intentBus, logger);
        kernel.registerService(pulse);

        // 6. Initialize FS + Swarm + Dream + Genesis
        this.fs = new FrayFS(brain, logger);
        kernel.registerService(fs);

        this.swarm = new NanoSwarm(fs, intentBus, logger);
        kernel.registerService(swarm);

        this.dream = new DreamState(brain, intentBus, logger);
        kernel.registerService(dream);

        this.genesis = new GenesisArchitectV1(fs, logger, Path.of("sys", "apps"));
        kernel.registerService(genesis);

        // Wire intent handlers for the organism
        registerOrganismHandlers();
        
        // 7. Initialize shell (user interface)
        this.shell = new FrayShell(kernel);
        shell.setPulse(pulse);

        // Default shell capability token (interactive user session)
        this.shellToken = CapabilityToken.admin("shell");
        shell.setSessionToken(shellToken);
        
        System.out.println();
        System.out.println("✅ FRAYNIX OS V1 READY");
        System.out.println();
    }

    public void start() {
        // Start all systems (kernel manages lifecycle of registered services)
        kernel.start();
        
        // Run interactive shell
        shell.run();
        
        // Cleanup on exit
        shutdown();
    }

    public void shutdown() {
        System.out.println("\n🛑 FRAYNIX OS SHUTTING DOWN...");

        kernel.stop();
        
        try {
            logger.close();
        } catch (IOException e) {
            System.err.println("Error closing logger: " + e.getMessage());
        }
        
        System.out.println("✅ Shutdown complete. Logs saved to: " + logger.getLogDir());
    }

    // Accessors for programmatic use
    public HyperTesseract getBrain() { return brain; }
    public IntentBus getIntentBus() { return intentBus; }
    public EventLogger getLogger() { return logger; }
    public FrayAbstractKernel getKernel() { return kernel; }
    public BrainPulse getPulse() { return pulse; }
    public FrayFS getFs() { return fs; }
    public NanoSwarm getSwarm() { return swarm; }
    public DreamState getDream() { return dream; }
    public GenesisArchitectV1 getGenesis() { return genesis; }
    public long getSeed() { return seed; }

    private void registerOrganismHandlers() {
        // FS watch (V1: register + log)
        intentBus.registerHandler(Intent.Type.FS_WATCH, intent -> {
            if (intent.getCapability() == null || !intent.getCapability().has(Capability.FS_READ)) {
                return IntentBus.IntentResult.failure(intent.getId(), "Missing capability: FS_READ", 0);
            }
            String path = intent.getPayloadString("path");
            if (path == null || path.isBlank()) {
                return IntentBus.IntentResult.failure(intent.getId(), "Missing path", 0);
            }
            fs.addWatch(path);
            logger.logEvent("action_taken", Map.of(
                "intent_id", intent.getId(),
                "action", "fs_watch",
                "path", path
            ));
            return IntentBus.IntentResult.success(intent.getId(), Map.of("watching", path), 0);
        });

        // Genesis create: schedule as kernel process to avoid blocking IntentBus
        intentBus.registerHandler(Intent.Type.GENESIS_CREATE, intent -> {
            if (intent.getCapability() == null || !intent.getCapability().hasAll(Capability.GENESIS_CREATE, Capability.BUILD)) {
                return IntentBus.IntentResult.failure(intent.getId(), "Missing capabilities: GENESIS_CREATE, BUILD", 0);
            }
            String userIntent = intent.getPayloadString("intent");
            if (userIntent == null || userIntent.isBlank()) {
                return IntentBus.IntentResult.failure(intent.getId(), "Missing intent", 0);
            }

            FrayProcess proc = kernel.spawn(intent, () -> {
                Blueprint bp = genesis.designBlueprint(userIntent);
                List<Artifact> artifacts = genesis.build(bp);
                logger.logEvent("artifact_created", Map.of(
                    "blueprintId", bp.getId(),
                    "artifactCount", artifacts.size()
                ));
            });

            return IntentBus.IntentResult.success(intent.getId(), Map.of(
                "processId", proc.getId(),
                "scheduled", true
            ), 0);
        });

        // Dream: enter dream state
        intentBus.registerHandler(Intent.Type.BRAIN_DREAM, intent -> {
            dream.enterDream();
            return IntentBus.IntentResult.success(intent.getId(), Map.of("dreaming", true), 0);
        });

        // Repair detect: trigger swarm check
        intentBus.registerHandler(Intent.Type.REPAIR_DETECT, intent -> {
            if (intent.getCapability() == null || !intent.getCapability().has(Capability.REPAIR_DETECT)) {
                return IntentBus.IntentResult.failure(intent.getId(), "Missing capability: REPAIR_DETECT", 0);
            }
            swarm.triggerIntegrityCheck();
            return IntentBus.IntentResult.success(intent.getId(), Map.of("triggered", true), 0);
        });
    }

    public static void main(String[] args) {
        try {
            // Parse seed from args for reproducible runs
            long seed = System.currentTimeMillis();
            for (int i = 0; i < args.length; i++) {
                if ("--seed".equals(args[i]) && i + 1 < args.length) {
                    seed = Long.parseLong(args[i + 1]);
                    break;
                }
            }
            
            FraynixOS os = new FraynixOS(seed);
            os.start();
            
        } catch (Exception e) {
            System.err.println("❌ FATAL: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
