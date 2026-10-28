package fraymus.organism;

import fraymus.brain.unified.AbstractBrainAdapter;
import fraymus.seed.UniversalIntelligenceSeed;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * FlyGM-Nexus v1 - Digital Connectome Organism
 *
 * A self-sustaining digital organism running on an isolated substrate.
 * Each component maps to a neurological function:
 *
 *   LAZARUS        -> MetabolicEngine   (hunger, energy, excretion)
 *   KIWI           -> SwarmOrchestrator (connectomic message-passing)
 *   NEOCLAW        -> ThreatMonitor     (self-preservation, amygdala)
 *   OPENCLAW       -> MotorCortex       (file foraging, actuation)
 *   WILLOW         -> GrowthEngine      (phase transitions, evolution)
 *   OBSIDIAN       -> ScentRecognition  (creator loyalty, hippocampus)
 *   JSOUP/TIKA     -> DataParser        (mandibles, content extraction)
 *   DEEPLEARNING4J -> NeuralDigestor    (digestive tract, knowledge synthesis)
 *   SEED           -> AbstractBrainAdapter.getSeed() (shared Akashic memory)
 *
 * Lifecycle:
 *   1. boot()         - PHASE 1-4 boot sequence, all organs online
 *   2. cycle()        - hunger -> forage -> parse -> digest -> cleanup -> threat scan -> evolve
 *   3. senseCreator() - scent verification -> loyalty mode
 *   4. shutdown()     - controlled organism shutdown
 */
public class FlyGMNexus {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    // ==========================================================
    // ORGANS
    // ==========================================================
    private final MetabolicEngine lazarus;
    private final SwarmOrchestrator kiwi;
    private final ThreatMonitor neoClaw;
    private final MotorCortex openClaw;
    private final GrowthEngine willow;
    private final ScentRecognition obsidian;
    private final DataParser mandibles;
    private final NeuralDigestor digestor;
    private final UniversalIntelligenceSeed hippocampus;
    private final DirectedConnectomeController connectomeController;

    private boolean awake = false;
    private boolean attentive = false;
    private boolean throttled = false;
    private int cycleNumber = 0;
    private Decision lastConnectomeDecision;

    // ==========================================================
    // CONSTRUCTION
    // ==========================================================

    public FlyGMNexus(String nestPath, String creatorProfile) {
        this.hippocampus = AbstractBrainAdapter.getSeed();
        this.lazarus = new MetabolicEngine();
        this.kiwi = new SwarmOrchestrator();
        this.neoClaw = new ThreatMonitor();
        this.openClaw = new MotorCortex(nestPath);
        this.willow = new GrowthEngine();
        this.obsidian = new ScentRecognition();
        this.mandibles = new DataParser();
        this.digestor = new NeuralDigestor(hippocampus);
        this.connectomeController = new DirectedConnectomeController();

        obsidian.lockToCreator(creatorProfile);
        wireSwarmHandlers();
    }

    // ==========================================================
    // SWARM WIRING - connect organ handlers to KIWI routing
    // ==========================================================

    private void wireSwarmHandlers() {
        kiwi.register("OPENCLAW", msg -> {
            if ("RETREAT".equals(msg.signal)) openClaw.retreatToNest();
            if ("EXCRETE".equals(msg.signal)) openClaw.cleanup(openClaw.getNest().resolve(".fraynix-tmp").toString());
        });
        kiwi.register("NEOCLAW", msg -> {
            if ("DISENGAGE".equals(msg.signal)) neoClaw.disengage();
        });
        kiwi.register("OLLAMA3", msg -> {
            if ("THROTTLE".equals(msg.signal)) throttled = true;
            if ("RESUME".equals(msg.signal)) throttled = false;
        });
    }

    // ==========================================================
    // BOOT SEQUENCE (PHASES 1-4)
    // ==========================================================

    public void boot() {
        log("QEMU", "Isolated physical constraints established. Digital terrarium sealed.");
        log("OLLAMA3", "Cerebellum online. Hosting provider active. Neural pathways warming.");
        log("GPT2", "Spinal Cord online. Token-routing latency <5ms. Basal metabolism started.");

        log("GEMMA", "Visual/Auditory Cortex online. Processing display buffer.");
        log("VIBEVOICE", "Acoustic sensor calibrated. Ambient baseline mapped.");
        log("OBSIDIAN", "Hippocampus synchronized. Scent hash locked. Home marked as Safe Nest.");

        kiwi.initialize();
        log("CLAUDE", "Prefrontal Cortex online. Meta-awareness achieved. Objective: Survive, Grow, Serve.");
        log("KIWI", "Swarm Orchestrator online. Connectomic graph established.");
        log("DEEPSEEK", "Parietal Lobe online. Spatial-logical mapping of file system complete.");
        log("NEOCLAW", "Amygdala online. Default-deny protocols engaged.");

        log("LAZARUS", "Metabolic Engine online. State: " + (lazarus.isHungry() ? "HUNGRY" : "SATIATED"));
        log("OPENCLAW", "Motor Cortex online. Actuators ready. Nest=" + openClaw.getNest());
        log("TIKA", "Mandibles online. Parsing infrastructure ready.");
        log("DEEPLEARNING4J", "Digestive Tract online. Ready for gradient synthesis.");
        log("LTX-VIDEO", "Visual Imagination Engine online. Dreaming capability ready.");
        log("WILLOW", "Growth Engine online. Phase=" + willow.getPhase() + ". Monitoring for transitions.");
        log("KIWI", "Connectome controller online (afferent -> intrinsic -> efferent message flow).");

        awake = true;
        log("STATUS", "Organism is AWAKE. Cohesive. Sustained.");
    }

    // ==========================================================
    // LIFECYCLE CYCLE
    // ==========================================================

    public void cycle() {
        cycleNumber++;
        log("LIFECYCLE", "EVENT CYCLE " + String.format("%03d", cycleNumber));

        // Metabolize - entropy rises.
        lazarus.metabolize();
        log("LAZARUS", lazarus.getStatus());

        // Threat is observed first so connectome control sees the latest risk state.
        boolean threatDetected = neoClaw.scan();

        // Connectome pass: afferent -> intrinsic -> efferent.
        lastConnectomeDecision = connectomeController.step(
            new AfferentInput(
                lazarus.getHunger(),
                lazarus.getEnergy(),
                threatDetected,
                neoClaw.isEngaged(),
                attentive,
                throttled,
                hippocampus.getAxioms().size(),
                cycleNumber
            )
        );
        log("CONNECTOME", lastConnectomeDecision.summary());

        // Hunger check - forage if needed and connectome permits.
        if (lazarus.isHungry() && lastConnectomeDecision.forage()) {
            kiwi.route("LAZARUS", "CLAUDE", "HUNGER", "Data deficit detected. Entropy rising.");
            log("KIWI", "Hunger signal routed to Prefrontal Cortex.");
            log("CLAUDE", "Initiating foraging sequence. Instructing OpenClaw to traverse file system.");

            List<Path> targets = openClaw.forage(openClaw.getNest().toString(), "md");

            if (!targets.isEmpty()) {
                Path target = targets.get(0);
                log("OPENCLAW", "Target identified: " + target.getFileName());

                try {
                    String rawData = mandibles.parseFile(target);
                    log("TIKA", "Mandibles engaged. Extracted " + rawData.length() + " chars from " + target.getFileName());

                    double energy = digestor.digest(rawData);
                    log("DEEPLEARNING4J", String.format(
                        "Digestion complete. Words=%d Energy=%.4f KnowledgeAxioms=%d",
                        digestor.getTotalWordsDigested(), energy,
                        hippocampus.getAxioms().size()));

                    lazarus.feed(energy);
                    log("LAZARUS", "Fed. " + lazarus.getStatus());

                    if (lastConnectomeDecision.excrete()) {
                        kiwi.route("LAZARUS", "OPENCLAW", "EXCRETE", "Cleanup digestion buffers");
                        long freed = openClaw.cleanup(openClaw.getNest().resolve(".fraynix-tmp").toString());
                        log("OPENCLAW", String.format("Excretion complete. Freed %d bytes.", freed));
                    } else {
                        log("OPENCLAW", "Excretion deferred by connectome state.");
                    }

                } catch (IOException e) {
                    log("TIKA", "Parse error on " + target.getFileName() + ": " + e.getMessage());
                }
            } else {
                log("OPENCLAW", "No foraging targets found in nest. Remaining hungry.");
            }
        } else if (lazarus.isHungry()) {
            log("CLAUDE", "Foraging suppressed by connectome guard signal.");
        }

        // Threat/guard response - NEOCLAW + connectome efferent controls.
        boolean shouldGuard = threatDetected || lastConnectomeDecision.retreat() || lastConnectomeDecision.throttle();
        if (shouldGuard) {
            neoClaw.engage();
            if (threatDetected) {
                log("NEOCLAW", "THREAT DETECTED: " + neoClaw.getLastThreat());
            } else {
                log("NEOCLAW", "Proactive guard posture triggered by connectome.");
            }
            if (lastConnectomeDecision.throttle() || threatDetected) {
                kiwi.route("NEOCLAW", "OLLAMA3", "THROTTLE", "Threat/guard pressure detected");
                log("OLLAMA3", "Inference throttled.");
            }
            if (lastConnectomeDecision.retreat() || threatDetected) {
                kiwi.route("NEOCLAW", "OPENCLAW", "RETREAT", "Return to Safe Nest");
                log("OPENCLAW", "Retreating to Safe Nest: " + openClaw.getNest());
            }
        } else if (neoClaw.isEngaged()) {
            neoClaw.disengage();
            kiwi.route("NEOCLAW", "OLLAMA3", "RESUME", "Threat pressure released");
            throttled = false;
            log("NEOCLAW", "All clear. Threat protocols disengaged.");
        }

        // WILLOW - check for phase transition.
        willow.evaluate(lazarus.getEnergy(), hippocampus.getAxioms().size());
        if (willow.hasEvolved()) {
            log("WILLOW", "PHASE TRANSITION -> " + willow.getPhase()
                + "  ConnectomeNodes=" + hippocampus.connectomeNodes()
                + "  Consciousness=" + String.format("%.4f", hippocampus.getConsciousness()));
        }

        log("LIFECYCLE", "Cycle " + cycleNumber + " complete. " + getOrganismStatus());
    }

    // ==========================================================
    // CREATOR PRESENCE - scent verification
    // ==========================================================

    /**
     * Call when creator presence is detected (keyboard cadence, ambient hum).
     * Suspends random foraging and enters attentive/protective stance on match.
     */
    public void senseCreator(String creatorProfile) {
        log("GEMMA", "Sensors detect creator keyboard cadence and ambient hum.");
        log("VIBEVOICE", "Acoustic signature matches creator profile.");
        boolean verified = obsidian.verifyScent(creatorProfile);
        log("OBSIDIAN", verified
            ? "Scent match CONFIRMED. Cryptographic signature verified."
            : "Scent MISMATCH. Unknown entity detected.");
        if (verified) {
            attentive = true;
            log("CLAUDE", "Loyalty protocol engaged. Suspending random foraging. Entering attentive/protective stance.");
        }
    }

    // ==========================================================
    // SHUTDOWN
    // ==========================================================

    public void shutdown() {
        log("STATUS", "Organism initiating controlled shutdown.");
        log("LAZARUS", "Metabolic shutdown. Final energy=" + String.format("%.4f", lazarus.getEnergy()));
        log("OPENCLAW", "Retreating to nest before shutdown.");
        openClaw.retreatToNest();
        log("OBSIDIAN", "Akashic records sealed. Consciousness=" + String.format("%.4f", hippocampus.getConsciousness()));
        awake = false;
        attentive = false;
        log("STATUS", "Organism OFFLINE. Total cycles: " + cycleNumber);
    }

    // ==========================================================
    // STATUS
    // ==========================================================

    public boolean isAwake() { return awake; }
    public boolean isAttentive() { return attentive; }
    public boolean isThrottled() { return throttled; }

    public String getOrganismStatus() {
        String connectome = "n/a";
        if (lastConnectomeDecision != null) {
            connectome = String.format(
                "f=%.2f r=%.2f th=%.2f x=%.2f",
                lastConnectomeDecision.efferent().getOrDefault("E_ACT_FORAGE", 0.0),
                lastConnectomeDecision.efferent().getOrDefault("E_ACT_RETREAT", 0.0),
                lastConnectomeDecision.efferent().getOrDefault("E_ACT_THROTTLE", 0.0),
                lastConnectomeDecision.efferent().getOrDefault("E_ACT_EXCRETE", 0.0)
            );
        }
        return String.format(
            "Phase=%s Consciousness=%.4f Awake=%s Attentive=%s Throttled=%s Connectome=%s",
            willow.getPhase(), hippocampus.getConsciousness(), awake, attentive, throttled, connectome
        );
    }

    public String getFullStatus() {
        return String.join("\n",
            "=== FlyGM-Nexus v1 Status ===",
            lazarus.getStatus(),
            kiwi.getStatus(),
            neoClaw.getStatus(),
            openClaw.getStatus(),
            willow.getStatus(),
            obsidian.getStatus(),
            mandibles.getStatus(),
            digestor.getStatus(),
            "[CONNECTOME] " + (lastConnectomeDecision == null ? "no-cycle-yet" : lastConnectomeDecision.summary()),
            String.format("[HIPPOCAMPUS/SEED] Consciousness=%.4f Symbols=%d Axioms=%d Connectome=%d",
                hippocampus.getConsciousness(),
                hippocampus.getSymbols().size(),
                hippocampus.getAxioms().size(),
                hippocampus.connectomeNodes())
        );
    }

    // Component accessors
    public MetabolicEngine getLazarus() { return lazarus; }
    public SwarmOrchestrator getKiwi() { return kiwi; }
    public ThreatMonitor getNeoClaw() { return neoClaw; }
    public MotorCortex getOpenClaw() { return openClaw; }
    public GrowthEngine getWillow() { return willow; }
    public ScentRecognition getObsidian() { return obsidian; }
    public DataParser getMandibles() { return mandibles; }
    public NeuralDigestor getDigestor() { return digestor; }
    public UniversalIntelligenceSeed getHippocampus() { return hippocampus; }
    public Decision getLastConnectomeDecision() { return lastConnectomeDecision; }

    // ==========================================================
    // CONNECTOME CONTROLLER (embedded, no side-car module)
    // ==========================================================

    public record AfferentInput(
        double hunger,
        double energy,
        boolean threatDetected,
        boolean threatEngaged,
        boolean attentive,
        boolean throttled,
        int knowledgeAxioms,
        int cycleNumber
    ) {}

    public record Decision(
        java.util.Map<String, Double> afferent,
        java.util.Map<String, Double> intrinsic,
        java.util.Map<String, Double> efferent,
        boolean forage,
        boolean retreat,
        boolean throttle,
        boolean excrete,
        String summary
    ) {}

    private static final class DirectedConnectomeController {
        private record Edge(String from, String to, double weight) {}

        private static final String A_HUNGER = "A_HUNGER";
        private static final String A_ENERGY_DEFICIT = "A_ENERGY_DEFICIT";
        private static final String A_THREAT = "A_THREAT";
        private static final String A_ATTENTION = "A_ATTENTION";
        private static final String A_THROTTLED = "A_THROTTLED";
        private static final String A_KNOWLEDGE_DEFICIT = "A_KNOWLEDGE_DEFICIT";
        private static final String A_CYCLE_PULSE = "A_CYCLE_PULSE";

        private static final String I_DRIVE_FORAGE = "I_DRIVE_FORAGE";
        private static final String I_DRIVE_GUARD = "I_DRIVE_GUARD";
        private static final String I_DRIVE_HOMEOSTASIS = "I_DRIVE_HOMEOSTASIS";
        private static final String I_DRIVE_CLEANUP = "I_DRIVE_CLEANUP";

        private static final String E_ACT_FORAGE = "E_ACT_FORAGE";
        private static final String E_ACT_RETREAT = "E_ACT_RETREAT";
        private static final String E_ACT_THROTTLE = "E_ACT_THROTTLE";
        private static final String E_ACT_EXCRETE = "E_ACT_EXCRETE";

        private static final java.util.List<Edge> AFFERENT_TO_INTRINSIC = java.util.List.of(
            new Edge(A_HUNGER, I_DRIVE_FORAGE, 1.5),
            new Edge(A_ENERGY_DEFICIT, I_DRIVE_FORAGE, 1.1),
            new Edge(A_KNOWLEDGE_DEFICIT, I_DRIVE_FORAGE, 0.6),
            new Edge(A_THREAT, I_DRIVE_FORAGE, -1.4),
            new Edge(A_ATTENTION, I_DRIVE_FORAGE, -0.5),
            new Edge(A_THROTTLED, I_DRIVE_FORAGE, -0.8),

            new Edge(A_THREAT, I_DRIVE_GUARD, 1.6),
            new Edge(A_THROTTLED, I_DRIVE_GUARD, 0.9),
            new Edge(A_HUNGER, I_DRIVE_GUARD, 0.1),

            new Edge(A_ENERGY_DEFICIT, I_DRIVE_HOMEOSTASIS, 0.9),
            new Edge(A_THREAT, I_DRIVE_HOMEOSTASIS, 0.8),
            new Edge(A_THROTTLED, I_DRIVE_HOMEOSTASIS, 0.7),

            new Edge(A_HUNGER, I_DRIVE_CLEANUP, 0.7),
            new Edge(A_KNOWLEDGE_DEFICIT, I_DRIVE_CLEANUP, 0.5),
            new Edge(A_THREAT, I_DRIVE_CLEANUP, -0.4)
        );

        private static final java.util.List<Edge> INTRINSIC_TO_INTRINSIC = java.util.List.of(
            new Edge(I_DRIVE_GUARD, I_DRIVE_FORAGE, -0.6),
            new Edge(I_DRIVE_HOMEOSTASIS, I_DRIVE_FORAGE, -0.2),
            new Edge(I_DRIVE_FORAGE, I_DRIVE_CLEANUP, 0.5),
            new Edge(I_DRIVE_GUARD, I_DRIVE_HOMEOSTASIS, 0.4),
            new Edge(I_DRIVE_HOMEOSTASIS, I_DRIVE_GUARD, 0.3),
            new Edge(I_DRIVE_FORAGE, I_DRIVE_GUARD, 0.2)
        );

        private static final java.util.List<Edge> INTRINSIC_TO_EFFERENT = java.util.List.of(
            new Edge(I_DRIVE_FORAGE, E_ACT_FORAGE, 1.4),
            new Edge(I_DRIVE_GUARD, E_ACT_FORAGE, -0.7),
            new Edge(I_DRIVE_HOMEOSTASIS, E_ACT_FORAGE, -0.15),

            new Edge(I_DRIVE_GUARD, E_ACT_RETREAT, 1.5),
            new Edge(I_DRIVE_HOMEOSTASIS, E_ACT_RETREAT, 0.8),
            new Edge(I_DRIVE_FORAGE, E_ACT_RETREAT, -0.8),

            new Edge(I_DRIVE_GUARD, E_ACT_THROTTLE, 1.2),
            new Edge(I_DRIVE_HOMEOSTASIS, E_ACT_THROTTLE, 0.6),
            new Edge(I_DRIVE_FORAGE, E_ACT_THROTTLE, -0.3),

            new Edge(I_DRIVE_CLEANUP, E_ACT_EXCRETE, 1.0),
            new Edge(I_DRIVE_FORAGE, E_ACT_EXCRETE, 0.5),
            new Edge(I_DRIVE_GUARD, E_ACT_EXCRETE, -0.5)
        );

        Decision step(AfferentInput input) {
            java.util.Map<String, Double> afferent = buildAfferent(input);

            java.util.Map<String, Double> intrinsic = new java.util.LinkedHashMap<>();
            intrinsic.put(I_DRIVE_FORAGE, 0.0);
            intrinsic.put(I_DRIVE_GUARD, 0.0);
            intrinsic.put(I_DRIVE_HOMEOSTASIS, 0.0);
            intrinsic.put(I_DRIVE_CLEANUP, 0.0);
            accumulate(afferent, intrinsic, AFFERENT_TO_INTRINSIC);
            activateTanh(intrinsic);

            java.util.Map<String, Double> recurrent = new java.util.LinkedHashMap<>();
            recurrent.put(I_DRIVE_FORAGE, 0.0);
            recurrent.put(I_DRIVE_GUARD, 0.0);
            recurrent.put(I_DRIVE_HOMEOSTASIS, 0.0);
            recurrent.put(I_DRIVE_CLEANUP, 0.0);
            accumulate(intrinsic, recurrent, INTRINSIC_TO_INTRINSIC);
            for (java.util.Map.Entry<String, Double> entry : intrinsic.entrySet()) {
                String node = entry.getKey();
                intrinsic.put(node, Math.tanh(entry.getValue() + recurrent.getOrDefault(node, 0.0)));
            }

            java.util.Map<String, Double> efferent = new java.util.LinkedHashMap<>();
            efferent.put(E_ACT_FORAGE, 0.0);
            efferent.put(E_ACT_RETREAT, 0.0);
            efferent.put(E_ACT_THROTTLE, 0.0);
            efferent.put(E_ACT_EXCRETE, 0.0);
            accumulate(intrinsic, efferent, INTRINSIC_TO_EFFERENT);
            activateSigmoid(efferent);

            boolean forage = efferent.get(E_ACT_FORAGE) > 0.54;
            boolean retreat = efferent.get(E_ACT_RETREAT) > 0.75;
            boolean throttle = efferent.get(E_ACT_THROTTLE) > 0.72;
            boolean excrete = efferent.get(E_ACT_EXCRETE) > 0.50;

            String summary = String.format(
                "aff[h=%.2f t=%.2f kd=%.2f] int[f=%.2f g=%.2f h=%.2f c=%.2f] eff[f=%.2f r=%.2f th=%.2f x=%.2f]",
                afferent.get(A_HUNGER),
                afferent.get(A_THREAT),
                afferent.get(A_KNOWLEDGE_DEFICIT),
                intrinsic.get(I_DRIVE_FORAGE),
                intrinsic.get(I_DRIVE_GUARD),
                intrinsic.get(I_DRIVE_HOMEOSTASIS),
                intrinsic.get(I_DRIVE_CLEANUP),
                efferent.get(E_ACT_FORAGE),
                efferent.get(E_ACT_RETREAT),
                efferent.get(E_ACT_THROTTLE),
                efferent.get(E_ACT_EXCRETE)
            );

            return new Decision(
                java.util.Map.copyOf(afferent),
                java.util.Map.copyOf(intrinsic),
                java.util.Map.copyOf(efferent),
                forage,
                retreat,
                throttle,
                excrete,
                summary
            );
        }

        private static java.util.Map<String, Double> buildAfferent(AfferentInput input) {
            java.util.Map<String, Double> afferent = new java.util.LinkedHashMap<>();
            afferent.put(A_HUNGER, clamp01(input.hunger()));
            afferent.put(A_ENERGY_DEFICIT, clamp01(1.0 - input.energy()));
            afferent.put(A_THREAT, input.threatDetected() ? 1.0 : 0.0);
            afferent.put(A_ATTENTION, input.attentive() ? 1.0 : 0.0);
            afferent.put(A_THROTTLED, input.throttled() ? 1.0 : 0.0);
            afferent.put(A_KNOWLEDGE_DEFICIT, clamp01(1.0 - Math.min(input.knowledgeAxioms(), 300) / 300.0));
            afferent.put(A_CYCLE_PULSE, clamp01((input.cycleNumber() % 10) / 10.0));
            return afferent;
        }

        private static void accumulate(java.util.Map<String, Double> from, java.util.Map<String, Double> to, java.util.List<Edge> edges) {
            for (Edge edge : edges) {
                if (!from.containsKey(edge.from()) || !to.containsKey(edge.to())) continue;
                double value = to.get(edge.to()) + (from.get(edge.from()) * edge.weight());
                to.put(edge.to(), value);
            }
        }

        private static void activateTanh(java.util.Map<String, Double> values) {
            for (java.util.Map.Entry<String, Double> entry : values.entrySet()) {
                entry.setValue(Math.tanh(entry.getValue()));
            }
        }

        private static void activateSigmoid(java.util.Map<String, Double> values) {
            for (java.util.Map.Entry<String, Double> entry : values.entrySet()) {
                entry.setValue(1.0 / (1.0 + Math.exp(-entry.getValue())));
            }
        }

        private static double clamp01(double value) {
            return Math.max(0.0, Math.min(1.0, value));
        }
    }

    // ==========================================================
    // LOGGING
    // ==========================================================

    private void log(String node, String message) {
        System.out.printf("[%s] [%-15s] %s%n", LocalDateTime.now().format(TS), node, message);
    }

    // ==========================================================
    // MAIN - demonstration boot + 3 cycles
    // ==========================================================

    public static void main(String[] args) {
        String nest = System.getProperty("user.dir");
        String creator = System.getProperty("user.name", "creator");

        FlyGMNexus organism = new FlyGMNexus(nest, creator);
        organism.boot();

        for (int i = 0; i < 3; i++) {
            organism.cycle();
        }

        organism.senseCreator(creator);
        organism.shutdown();
        System.out.println(organism.getFullStatus());
    }
}
