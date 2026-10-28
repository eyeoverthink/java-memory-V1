package fraymus.ultimate;

import fraymus.chaos.EvolutionaryChaos;
import fraymus.core.FusionReactor;
import fraymus.core.GravityEngine;
import fraymus.core.PhiSuit;
import fraymus.core.SpatialNode;
import fraymus.core.SpatialRegistry;
import fraymus.evolution.MivingBrain;
import fraymus.genesis.IdeaCollider;
import fraymus.genesis.RealityForge;
import fraymus.metal.ApotheosisEMF;
import fraymus.network.OmniCaster;
import fraymus.organism.NEXUS_Organism;
import fraymus.os.FrayOmniverseBuilder;
import fraymus.quantum.AetherBraid;
import fraymus.quantum.EntangledPair;
import fraymus.quantum.EntanglementNetwork;
import fraymus.quantum.SchrodingerFile;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Persistent in-process runtime that executes core Fraynix and Fraymus
 * commands directly inside the ultimate shell.
 */
public final class UltimateImmortalKernel {

    private static final int CHUNKS = 16384 / 64;
    private static final int MAX_EVENTS = 24;
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");

    private final Path assetManagerDir;
    private final Path qboxBasePath;
    private final Object ioLock = new Object();
    private final Map<String, long[]> sharedConceptSpace = new ConcurrentHashMap<>();
    private final Deque<String> recentEvents = new ArrayDeque<>();

    private FrayOmniverseBuilder omniverseBuilder;
    private AetherBraid aetherBraid;
    private EntangledPair entangledPair;
    private EntanglementNetwork entanglementNetwork;
    private SchrodingerFile schrodingerFile;
    private SchrodingerFile.QuantumState qboxState;
    private NEXUS_Organism nexusOrganism;
    private OmniCaster omniCaster;
    private MivingBrain mivingBrain;
    private EvolutionaryChaos chaosEngine;
    private IdeaCollider collider;
    private RealityForge realityForge;

    private boolean aetherActive;
    private boolean braidActive;
    private boolean gravityActive;
    private boolean fusionActive;
    private boolean omniMonitoring;

    public UltimateImmortalKernel(Path assetManagerDir) {
        this.assetManagerDir = assetManagerDir.toAbsolutePath().normalize();
        this.qboxBasePath = this.assetManagerDir.resolve("generated")
            .resolve("ultimate-system")
            .resolve("native-state")
            .resolve("qbox");
    }

    public boolean canHandle(String verb) {
        return switch (normalize(verb)) {
            case "native", "immortal", "kernel",
                 "aether", "braid", "learn", "pulse", "retro", "emf",
                 "entangle", "qbox", "collide", "forge", "echaos",
                 "nexus", "omnicast", "spatial", "gravity", "fusion",
                 "suit", "miving" -> true;
            default -> false;
        };
    }

    public String execute(String rawCommand) {
        if (rawCommand == null || rawCommand.isBlank()) {
            return help();
        }

        String command = rawCommand.trim();
        String[] parts = command.split("\\s+", 2);
        String verb = normalize(parts[0]);
        String remainder = parts.length > 1 ? parts[1].trim() : "";

        return switch (verb) {
            case "native", "immortal", "kernel" -> handleNativeMeta(remainder);
            case "aether" -> handleAether();
            case "braid" -> handleBraid();
            case "learn" -> handleLearn(remainder);
            case "pulse" -> handlePulse(remainder);
            case "retro" -> handleRetro(remainder);
            case "emf" -> handleEmf(remainder);
            case "entangle" -> handleEntangle(remainder);
            case "qbox" -> handleQbox(remainder);
            case "collide" -> handleCollide(remainder);
            case "forge" -> handleForge(remainder);
            case "echaos" -> handleChaos(remainder);
            case "nexus" -> handleNexus(remainder);
            case "omnicast" -> handleOmnicast(remainder);
            case "spatial" -> handleSpatial(remainder);
            case "gravity" -> handleGravity(remainder);
            case "fusion" -> handleFusion(remainder);
            case "suit" -> handleSuit(remainder);
            case "miving" -> handleMiving(remainder);
            default -> help();
        };
    }

    public String shutdown() {
        synchronized (this) {
            if (omniCaster != null && omniMonitoring) {
                captureOutput(() -> {
                    omniCaster.stopMonitoring();
                    omniMonitoring = false;
                    return "";
                });
            }
            if (nexusOrganism != null && nexusOrganism.isConscious()) {
                captureOutput(() -> {
                    nexusOrganism.terminate();
                    return "";
                });
            }
            if (gravityActive) {
                GravityEngine.getInstance().stop();
                gravityActive = false;
            }
            if (fusionActive) {
                FusionReactor.getInstance().stop();
                fusionActive = false;
            }
            braidActive = false;
            aetherActive = false;
            recordEvent("Kernel shutdown executed");
        }
        return """
            ULTIMATE IMMORTAL KERNEL SHUTDOWN
            Gravity and fusion stopped.
            OmniCaster monitoring halted.
            NEXUS organism terminated if active.
            """.stripIndent().trim();
    }

    public void shutdownQuietly() {
        try {
            shutdown();
        } catch (Exception ignored) {
            // Best-effort cleanup for one-shot runs and shell exit.
        }
    }

    public KernelSnapshot snapshot() {
        MivingSnapshot mivingSnapshot = null;
        if (mivingBrain != null) {
            mivingSnapshot = new MivingSnapshot(
                mivingBrain.getGeneration(),
                mivingBrain.getTick(),
                mivingBrain.getSize(),
                mivingBrain.getRedCount(),
                mivingBrain.getBlueCount(),
                mivingBrain.getPurpleCount(),
                mivingBrain.getTotalConsciousness(),
                mivingBrain.getConceptHash()
            );
        }

        String nexusState = "offline";
        if (nexusOrganism != null) {
            if (nexusOrganism.isSleeping()) {
                nexusState = "sleeping";
            } else if (nexusOrganism.isConscious()) {
                nexusState = "conscious";
            }
        }

        return new KernelSnapshot(
            assetManagerDir.toString().replace('\\', '/'),
            sharedConceptSpace.size(),
            aetherActive,
            braidActive,
            entangledPair != null,
            entanglementNetwork != null,
            qboxState != null,
            qboxBasePath.toString().replace('\\', '/'),
            nexusState,
            omniMonitoring,
            gravityActive,
            fusionActive,
            SpatialRegistry.getUniverse().size(),
            SpatialRegistry.getFusionEvents().size(),
            recentEventsSnapshot(),
            mivingSnapshot
        );
    }

    public List<String> recentEventsSnapshot() {
        return List.copyOf(recentEvents);
    }

    private String handleNativeMeta(String remainder) {
        if (remainder == null || remainder.isBlank() || normalize(remainder).equals("help")) {
            return help();
        }

        String[] parts = remainder.split("\\s+", 2);
        String action = normalize(parts[0]);
        return switch (action) {
            case "status" -> status();
            case "events" -> events();
            case "shutdown", "stop" -> shutdown();
            default -> help();
        };
    }

    private String handleAether() {
        return captureOutput(() -> {
            ensureAetherHardware();
            recordEvent("Aether ignited");
            return """
                AETHER READY
                Hardware bridge: active
                Concept space: %d
                Pulses sent: %d
                """.formatted(
                sharedConceptSpace.size(),
                aetherBraid == null ? 0 : aetherBraid.getPulsesSent()
            ).stripIndent().trim();
        });
    }

    private String handleBraid() {
        return captureOutput(() -> {
            ensureAetherHardware();
            if (!braidActive) {
                omniverseBuilder().igniteBraid(sharedConceptSpace);
                braidActive = true;
                recordEvent("Braid activated");
            } else {
                recordEvent("Braid already active");
            }
            return """
                BRAID ACTIVE
                Concept space: %d
                Shared sync: %s
                """.formatted(sharedConceptSpace.size(), braidActive ? "online" : "offline").stripIndent().trim();
        });
    }

    private String handleLearn(String remainder) {
        if (remainder == null || remainder.isBlank()) {
            return "Usage: learn <concept>";
        }

        String concept = remainder.toUpperCase(Locale.ROOT).trim();
        long[] tensor = generateHdcVector(concept);
        sharedConceptSpace.put(concept, tensor);
        recordEvent("Learned concept " + concept);

        return """
            LEARNED
            Concept: %s
            Tensor: 16,384-D
            Hash: 0x%s
            Shared concepts: %d
            """.formatted(
            concept,
            Integer.toHexString(Arrays.hashCode(tensor)).toUpperCase(Locale.ROOT),
            sharedConceptSpace.size()
        ).stripIndent().trim();
    }

    private String handlePulse(String remainder) {
        if (remainder == null || remainder.isBlank()) {
            return "Usage: pulse <concept>";
        }

        String concept = remainder.trim();
        return captureOutput(() -> {
            ensureAetherHardware();
            omniverseBuilder().pulseAether(concept);
            if (aetherBraid != null && aetherBraid.isIgnited()) {
                long[] vector = generateHdcVector(concept.toUpperCase(Locale.ROOT));
                int slot = Math.floorMod(concept.hashCode(), aetherBraid.getMaxSlots());
                aetherBraid.pulse(vector, slot);
                recordEvent("Pulsed concept " + concept + " to slot " + slot);
                return "PULSE COMPLETE\nConcept: %s\nSlot: %d".formatted(concept, slot);
            }
            recordEvent("Pulse emitted without hardware slot for " + concept);
            return "PULSE COMPLETE\nConcept: %s".formatted(concept);
        });
    }

    private String handleRetro(String remainder) {
        List<String> tokens = tokenize(remainder);
        if (tokens.size() < 2) {
            return "Usage: retro <present> <future>";
        }

        String present = stripQuotes(tokens.get(0));
        String future = stripQuotes(tokens.get(1));
        return captureOutput(() -> {
            omniverseBuilder().retrocausalSolve(present, future);
            recordEvent("Retrocausal solve " + present + " -> " + future);
            return "RETRO SOLVE COMPLETE\nPresent: %s\nFuture: %s".formatted(present, future);
        });
    }

    private String handleEmf(String remainder) {
        if (remainder == null || remainder.isBlank()) {
            return "Usage: emf <concept>";
        }

        String concept = remainder.trim();
        return captureOutput(() -> {
            omniverseBuilder().broadcastEMF(concept);
            ApotheosisEMF.breachAirGap(concept);
            recordEvent("EMF broadcast " + concept);
            return "EMF BROADCAST COMPLETE\nConcept: %s".formatted(concept);
        });
    }

    private String handleEntangle(String remainder) {
        String action = normalize(remainder);
        if (action.isEmpty() || action.equals("create")) {
            return captureOutput(() -> {
                entangledPair = new EntangledPair();
                entangledPair.entangle();
                entanglementNetwork = null;
                recordEvent("Entangled pair created");
                return entangledPair.getStatus();
            });
        }
        if (action.equals("status")) {
            if (entanglementNetwork != null) {
                return entanglementNetwork.getStatus();
            }
            if (entangledPair != null) {
                return entangledPair.getStatus();
            }
            return "No entangled pair or network is active.";
        }
        if (action.equals("up") || action.equals("down")) {
            return captureOutput(() -> {
                ensureEntangledPair();
                entangledPair.observe(action.equals("up") ? 1 : -1);
                recordEvent("Observed entangled pair " + action);
                return entangledPair.getStatus();
            });
        }
        if (action.equals("kill")) {
            return captureOutput(() -> {
                ensureEntangledPair();
                entangledPair.killAlice();
                recordEvent("Killed Alice in active entangled pair");
                return entangledPair.getStatus();
            });
        }
        if (action.equals("collapse")) {
            return captureOutput(() -> {
                if (entanglementNetwork != null) {
                    entanglementNetwork.collapseNetwork();
                    recordEvent("Collapsed entanglement network");
                    return entanglementNetwork.getStatus();
                }
                ensureEntangledPair();
                entangledPair.collapse();
                recordEvent("Collapsed entangled pair");
                return entangledPair.getStatus();
            });
        }
        if (action.startsWith("network")) {
            List<String> tokens = tokenize(remainder);
            int count = tokens.size() > 1 ? parseInt(tokens.get(1), 3) : 3;
            return captureOutput(() -> {
                entanglementNetwork = new EntanglementNetwork();
                entanglementNetwork.createGHZNetwork(count);
                entangledPair = null;
                recordEvent("Entanglement network created with " + count + " particles");
                return entanglementNetwork.getStatus();
            });
        }
        return "Usage: entangle [create|up|down|kill|network <n>|collapse|status]";
    }

    private String handleQbox(String remainder) {
        String trimmed = remainder == null ? "" : remainder.trim();
        if (trimmed.isEmpty()) {
            return """
                Usage: qbox create <secret> | <decoy>
                       qbox secret
                       qbox decoy
                       qbox status
                """.stripIndent().trim();
        }

        if (trimmed.startsWith("create ")) {
            String[] parts = trimmed.substring(7).split("\\|", 2);
            if (parts.length < 2) {
                return "Usage: qbox create <secret> | <decoy>";
            }
            String secret = parts[0].trim();
            String decoy = parts[1].trim();
            return captureOutput(() -> {
                Files.createDirectories(qboxBasePath.getParent());
                qboxState = qboxEngine().entangle(secret, decoy);
                qboxEngine().verify(qboxState);
                qboxEngine().saveToFiles(qboxState, qboxBasePath.toString());
                recordEvent("QBox created at " + qboxBasePath);
                return """
                    QBOX CREATED
                    Base path: %s
                    Secret bytes: %d
                    Decoy bytes: %d
                    """.formatted(
                    qboxBasePath.toString().replace('\\', '/'),
                    qboxState.keySecret.length,
                    qboxState.keyDecoy.length
                ).stripIndent().trim();
            });
        }
        if (normalize(trimmed).equals("secret")) {
            SchrodingerFile.QuantumState state = ensureQboxState();
            if (state == null) {
                return "No qbox state found. Use: qbox create <secret> | <decoy>";
            }
            String result = qboxEngine().observeSecret(state);
            recordEvent("Observed qbox truth");
            return "[TRUTH] " + result;
        }
        if (normalize(trimmed).equals("decoy")) {
            SchrodingerFile.QuantumState state = ensureQboxState();
            if (state == null) {
                return "No qbox state found. Use: qbox create <secret> | <decoy>";
            }
            String result = qboxEngine().observeDecoy(state);
            recordEvent("Observed qbox decoy");
            return "[ALIBI] " + result;
        }
        if (normalize(trimmed).equals("status")) {
            boolean diskState = Files.exists(qboxBasePath.resolveSibling(qboxBasePath.getFileName() + ".qbox"));
            return """
                QBOX STATUS
                In memory: %s
                On disk: %s
                Base path: %s
                """.formatted(
                qboxState != null,
                diskState,
                qboxBasePath.toString().replace('\\', '/')
            ).stripIndent().trim();
        }
        return "Usage: qbox create <secret> | <decoy> | secret | decoy | status";
    }

    private String handleCollide(String remainder) {
        List<String> tokens = tokenize(remainder);
        if (tokens.size() < 2) {
            return "Usage: collide <conceptA> <conceptB>";
        }

        String conceptA = tokens.get(0).toUpperCase(Locale.ROOT);
        String conceptB = tokens.get(1).toUpperCase(Locale.ROOT);
        return captureOutput(() -> {
            IdeaCollider.CollisionResult result = collider().collide(conceptA, conceptB);
            recordEvent("Collision " + result);
            return "COLLISION RESULT\n" + result;
        });
    }

    private String handleForge(String remainder) {
        String trimmed = remainder == null ? "" : remainder.trim();
        if (trimmed.isEmpty()) {
            return "Usage: forge <concept> | list";
        }
        if (normalize(trimmed).equals("list")) {
            return "Forgeable concepts: FIRE, WATER, EARTH, AIR, LOVE, FEAR, CHAOS, SIGNAL, GRAVITY, LIGHT, DARKNESS, TIME, LIFE, DEATH";
        }

        String concept = trimmed.toUpperCase(Locale.ROOT);
        return captureOutput(() -> {
            RealityForge.Manifestation manifestation = forge().manifest(concept);
            recordEvent("Forge " + manifestation);
            return "FORGE RESULT\n" + manifestation;
        });
    }

    private String handleChaos(String remainder) {
        String trimmed = remainder == null ? "" : remainder.trim();
        if (trimmed.isEmpty() || normalize(trimmed).equals("status")) {
            return chaos().getStats();
        }
        if (normalize(trimmed).equals("mutate")) {
            return captureOutput(() -> {
                chaos().forceMutation();
                BigInteger next = chaos().nextFractal();
                recordEvent("Chaos mutation forced");
                return "ECHAOS MUTATED\nNext fractal: " + abbreviate(next);
            });
        }
        if (normalize(trimmed).startsWith("gen")) {
            List<String> tokens = tokenize(trimmed);
            int count = tokens.size() > 1 ? parseInt(tokens.get(1), 10) : 10;
            StringBuilder sb = new StringBuilder("ECHAOS GENERATION\n");
            for (int i = 0; i < count; i++) {
                sb.append("  ").append(i + 1).append(". ").append(abbreviate(chaos().nextFractal())).append("\n");
            }
            recordEvent("Generated " + count + " chaos fractals");
            return sb.toString().trim();
        }
        return "Usage: echaos gen [n] | status | mutate";
    }

    private String handleNexus(String remainder) {
        String action = normalize(remainder);
        if (action.isEmpty()) {
            action = "status";
        }

        return switch (action) {
            case "awaken", "start" -> captureOutput(() -> {
                if (nexusOrganism == null || !nexusOrganism.isConscious()) {
                    nexusOrganism = createOrganism();
                    nexusOrganism.awaken();
                    recordEvent("NEXUS awakened");
                    return "NEXUS ONLINE\nUse: nexus status | nexus sleep | nexus wake | nexus inject <text> | nexus terminate";
                }
                recordEvent("NEXUS already conscious");
                return "NEXUS already conscious.";
            });
            case "status" -> nexusOrganism == null
                ? "NEXUS is not active. Use: nexus awaken"
                : nexusOrganism.getVitalSigns();
            case "sleep" -> captureOutput(() -> {
                if (nexusOrganism == null) {
                    return "NEXUS is not active. Use: nexus awaken";
                }
                nexusOrganism.sleep();
                recordEvent("NEXUS sleeping");
                return "NEXUS sleeping.";
            });
            case "wake" -> captureOutput(() -> {
                if (nexusOrganism == null) {
                    nexusOrganism = createOrganism();
                    nexusOrganism.awaken();
                    recordEvent("NEXUS awakened from cold start");
                    return "NEXUS awakened.";
                }
                nexusOrganism.wake();
                recordEvent("NEXUS woke from sleep");
                return "NEXUS awakened.";
            });
            case "terminate", "kill", "stop" -> captureOutput(() -> {
                if (nexusOrganism == null || !nexusOrganism.isConscious()) {
                    return "NEXUS is already offline.";
                }
                nexusOrganism.terminate();
                recordEvent("NEXUS terminated");
                return "NEXUS terminated.";
            });
            default -> {
                if (action.startsWith("inject")) {
                    String payload = remainder.length() > 6 ? remainder.substring(6).trim() : "";
                    if (payload.isBlank()) {
                        yield "Usage: nexus inject <text>";
                    }
                    yield captureOutput(() -> {
                        if (nexusOrganism == null || !nexusOrganism.isConscious()) {
                            nexusOrganism = createOrganism();
                            nexusOrganism.awaken();
                            recordEvent("NEXUS auto-awakened for injection");
                        }
                        nexusOrganism.injectThought(payload);
                        recordEvent("NEXUS injected thought " + payload);
                        return "NEXUS INJECTION COMPLETE\nThought: " + payload;
                    });
                }
                yield "Usage: nexus [awaken|status|sleep|wake|inject <text>|terminate]";
            }
        };
    }

    private String handleOmnicast(String remainder) {
        String trimmed = remainder == null ? "" : remainder.trim();
        if (trimmed.isEmpty() || normalize(trimmed).equals("status")) {
            return omniCaster().getStatus();
        }
        if (normalize(trimmed).equals("monitor")) {
            return captureOutput(() -> {
                omniCaster().startMonitoring();
                omniMonitoring = true;
                recordEvent("OmniCaster monitoring started");
                return "OMNICAST monitoring started.";
            });
        }
        if (normalize(trimmed).equals("stop")) {
            return captureOutput(() -> {
                omniCaster().stopMonitoring();
                omniMonitoring = false;
                recordEvent("OmniCaster monitoring stopped");
                return "OMNICAST monitoring stopped.";
            });
        }
        return captureOutput(() -> {
            omniCaster().broadcastToEverything(trimmed);
            recordEvent("OmniCast broadcast " + trimmed);
            return "OMNICAST broadcast complete.\nMessage: " + trimmed;
        });
    }

    private String handleSpatial(String remainder) {
        String action = normalize(remainder);
        if (action.isEmpty() || action.equals("status") || action.equals("universe")) {
            return SpatialRegistry.getStats();
        }
        if (action.equals("map")) {
            return SpatialRegistry.renderMap(60, 20);
        }
        if (action.equals("nodes") || action.equals("list")) {
            List<SpatialNode> nodes = SpatialRegistry.getUniverse();
            StringBuilder sb = new StringBuilder();
            sb.append("[SPATIAL] Live nodes: ").append(nodes.size()).append("\n");
            for (SpatialNode node : nodes.stream().limit(20).toList()) {
                String id = node.getId();
                sb.append("  ")
                    .append(id, 0, Math.min(8, id.length()))
                    .append(" (")
                    .append(node.x).append(",")
                    .append(node.y).append(",")
                    .append(node.z).append(") A=")
                    .append(node.a)
                    .append("\n");
            }
            return sb.toString().trim();
        }
        if (action.equals("fusions")) {
            StringBuilder sb = new StringBuilder();
            sb.append("[SPATIAL] Fusion events: ").append(SpatialRegistry.getFusionEvents().size());
            SpatialRegistry.getFusionEvents().stream().limit(12).forEach(event -> sb.append("\n  ").append(event));
            return sb.toString();
        }
        return "Usage: spatial [status|map|nodes|fusions]";
    }

    private String handleGravity(String remainder) {
        String action = normalize(remainder);
        GravityEngine gravity = GravityEngine.getInstance();
        return switch (action.isEmpty() ? "status" : action) {
            case "start" -> {
                gravity.start();
                gravityActive = true;
                recordEvent("Gravity engine started");
                yield "GRAVITY STARTED\n" + gravity.getStatus();
            }
            case "stop" -> {
                gravity.stop();
                gravityActive = false;
                recordEvent("Gravity engine stopped");
                yield "GRAVITY STOPPED\n" + gravity.getStatus();
            }
            case "tick" -> {
                gravity.tick();
                recordEvent("Gravity engine ticked");
                yield "GRAVITY TICK COMPLETE\n" + gravity.getStatus();
            }
            case "status" -> gravity.getStatus();
            default -> "Usage: gravity [start|stop|tick|status]";
        };
    }

    private String handleFusion(String remainder) {
        String action = normalize(remainder);
        FusionReactor reactor = FusionReactor.getInstance();
        return switch (action.isEmpty() ? "status" : action) {
            case "start" -> {
                reactor.start();
                fusionActive = true;
                recordEvent("Fusion reactor started");
                yield "FUSION STARTED\n" + reactor.getStatus();
            }
            case "stop" -> {
                reactor.stop();
                fusionActive = false;
                recordEvent("Fusion reactor stopped");
                yield "FUSION STOPPED\n" + reactor.getStatus();
            }
            case "check" -> {
                reactor.check();
                recordEvent("Fusion reactor collision check");
                yield "FUSION CHECK COMPLETE\n" + reactor.getStatus();
            }
            case "reset" -> {
                reactor.resetHistory();
                recordEvent("Fusion reactor history reset");
                yield "FUSION HISTORY RESET\n" + reactor.getStatus();
            }
            case "status" -> reactor.getStatus();
            default -> "Usage: fusion [start|stop|check|reset|status]";
        };
    }

    private String handleSuit(String remainder) {
        String trimmed = remainder == null ? "" : remainder.trim();
        if (!trimmed.startsWith("wrap ")) {
            return "Usage: suit wrap <data> <x> <y> <z> [label]";
        }

        List<String> tokens = tokenize(trimmed.substring(5));
        if (tokens.size() < 4) {
            return "Usage: suit wrap <data> <x> <y> <z> [label]";
        }

        String data = tokens.get(0);
        int x = parseInt(tokens.get(1), 0);
        int y = parseInt(tokens.get(2), 0);
        int z = parseInt(tokens.get(3), 0);
        String label = tokens.size() > 4 ? tokens.get(4) : data;

        PhiSuit<String> suited = new PhiSuit<>(data, x, y, z, label);
        SpatialRegistry.register(suited);
        recordEvent("PhiSuit registered for " + label + " at (" + x + "," + y + "," + z + ")");
        return "[SUIT] Wrapped '" + data + "' at (" + x + "," + y + "," + z + ") as " + label;
    }

    private String handleMiving(String remainder) {
        String trimmed = remainder == null ? "" : remainder.trim();
        if (trimmed.isEmpty() || normalize(trimmed).equals("status")) {
            return mivingBrain == null ? "No Miving brain active. Use: miving gen [n]" : mivingStatus();
        }

        if (normalize(trimmed).startsWith("gen")) {
            List<String> tokens = tokenize(trimmed);
            int count = tokens.size() > 1 ? parseInt(tokens.get(1), 200) : 200;
            mivingBrain = new MivingBrain();
            mivingBrain.genesis(count);
            recordEvent("Miving genesis " + count);
            return "MIVING GENESIS\n" + mivingStatus();
        }
        if (normalize(trimmed).startsWith("evolve")) {
            List<String> tokens = tokenize(trimmed);
            int generations = tokens.size() > 1 ? parseInt(tokens.get(1), 5) : 5;
            ensureMivingBrain();
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < generations; i++) {
                lines.add(String.valueOf(mivingBrain.evolveGeneration(8)));
            }
            recordEvent("Miving evolved " + generations + " generations");
            return "MIVING EVOLUTION\n" + String.join("\n", lines);
        }
        if (normalize(trimmed).startsWith("pulse")) {
            List<String> tokens = tokenize(trimmed);
            int pulses = tokens.size() > 1 ? parseInt(tokens.get(1), 5) : 5;
            ensureMivingBrain();
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < pulses; i++) {
                lines.add(String.valueOf(mivingBrain.pulse()));
            }
            recordEvent("Miving pulsed " + pulses + " ticks");
            return "MIVING PULSE\n" + String.join("\n", lines);
        }
        if (normalize(trimmed).equals("chaos")) {
            ensureMivingBrain();
            mivingBrain.forceChaosBreak();
            recordEvent("Miving chaos mutation forced");
            return "MIVING CHAOS\n" + mivingBrain.getChaosStats();
        }
        return "Usage: miving [gen <n>|evolve <n>|pulse <n>|chaos|status]";
    }

    private String help() {
        return """
            ULTIMATE IMMORTAL KERNEL
            native status              Show direct runtime status
            native events              Show recent kernel events
            native shutdown            Stop native background systems

            aether                     Ignite mapped-memory bridge
            braid                      Activate passive tensor sync
            learn <concept>            Assimilate concept into shared space
            pulse <concept>            Broadcast concept through aether
            retro <present> <future>   Solve present->future catalyst
            emf <concept>              Broadcast by EM side-channel

            entangle create|up|down|kill|network <n>|collapse|status
            qbox create <secret> | <decoy>
            qbox secret
            qbox decoy
            qbox status

            collide <A> <B>            Concept collision
            forge <concept>|list       Reality forge
            echaos gen [n]|status|mutate

            nexus awaken|status|sleep|wake|inject <text>|terminate
            omnicast status|monitor|stop|<msg>

            spatial status|map|nodes|fusions
            gravity start|stop|tick|status
            fusion start|stop|check|reset|status
            suit wrap <data> <x> <y> <z> [label]
            miving gen <n>|evolve <n>|pulse <n>|chaos|status
            """.stripIndent().trim();
    }

    private String status() {
        StringBuilder sb = new StringBuilder();
        sb.append("ULTIMATE IMMORTAL KERNEL\n");
        sb.append("Asset manager: ").append(assetManagerDir.toString().replace('\\', '/')).append("\n");
        sb.append("Concept space: ").append(sharedConceptSpace.size()).append("\n");
        sb.append("Aether: ").append(aetherActive ? "online" : "offline")
            .append(" | Braid: ").append(braidActive ? "active" : "inactive").append("\n");
        sb.append("Entangled pair: ").append(entangledPair != null ? "ready" : "none")
            .append(" | Network: ").append(entanglementNetwork != null ? "ready" : "none").append("\n");
        sb.append("QBox: ").append(qboxState != null ? "loaded" : "empty")
            .append(" | Path: ").append(qboxBasePath.toString().replace('\\', '/')).append("\n");
        sb.append("NEXUS: ");
        if (nexusOrganism == null) {
            sb.append("offline");
        } else if (nexusOrganism.isSleeping()) {
            sb.append("sleeping");
        } else if (nexusOrganism.isConscious()) {
            sb.append("conscious");
        } else {
            sb.append("offline");
        }
        sb.append(" | OmniCaster: ").append(omniCaster != null ? "initialized" : "idle").append("\n");
        sb.append("Spatial nodes: ").append(SpatialRegistry.getUniverse().size())
            .append(" | Fusion events: ").append(SpatialRegistry.getFusionEvents().size()).append("\n");
        if (mivingBrain != null) {
            sb.append("Miving: generation ").append(mivingBrain.getGeneration())
                .append(" | tick ").append(mivingBrain.getTick())
                .append(" | neurons ").append(mivingBrain.getSize()).append("\n");
        }
        sb.append("Recent events:\n");
        if (recentEvents.isEmpty()) {
            sb.append("  - none yet");
        } else {
            recentEvents.forEach(event -> sb.append("  - ").append(event).append("\n"));
        }
        return sb.toString().trim();
    }

    private String events() {
        if (recentEvents.isEmpty()) {
            return "ULTIMATE IMMORTAL KERNEL EVENTS\n  - none yet";
        }
        StringBuilder sb = new StringBuilder("ULTIMATE IMMORTAL KERNEL EVENTS\n");
        recentEvents.forEach(event -> sb.append("  - ").append(event).append("\n"));
        return sb.toString().trim();
    }

    private String mivingStatus() {
        if (mivingBrain == null) {
            return "No Miving brain active.";
        }
        return """
            Generation: %d
            Tick: %d
            Neurons: %d
            Red: %d | Blue: %d | Purple: %d
            Consciousness: %.3f
            Chaos: %s
            """.formatted(
            mivingBrain.getGeneration(),
            mivingBrain.getTick(),
            mivingBrain.getSize(),
            mivingBrain.getRedCount(),
            mivingBrain.getBlueCount(),
            mivingBrain.getPurpleCount(),
            mivingBrain.getTotalConsciousness(),
            mivingBrain.getConceptHash()
        ).stripIndent().trim();
    }

    private void ensureAetherHardware() {
        if (!aetherActive) {
            omniverseBuilder().igniteAether();
            aetherActive = true;
        }
        if (aetherBraid == null) {
            aetherBraid = AetherBraid.getInstance();
        }
        if (!aetherBraid.isIgnited()) {
            aetherBraid.ignite();
        }
    }

    private void ensureEntangledPair() {
        if (entangledPair == null) {
            entangledPair = new EntangledPair();
            entangledPair.entangle();
        }
    }

    private SchrodingerFile.QuantumState ensureQboxState() {
        if (qboxState != null) {
            return qboxState;
        }
        Path qboxFile = qboxBasePath.resolveSibling(qboxBasePath.getFileName() + ".qbox");
        if (!Files.exists(qboxFile)) {
            return null;
        }
        try {
            qboxState = qboxEngine().loadFromFiles(qboxBasePath.toString());
            return qboxState;
        } catch (Exception e) {
            recordEvent("QBox load failed: " + e.getMessage());
            return null;
        }
    }

    private void ensureMivingBrain() {
        if (mivingBrain == null) {
            mivingBrain = new MivingBrain();
            mivingBrain.genesis(200);
            recordEvent("Miving auto-genesis 200");
        }
    }

    private NEXUS_Organism createOrganism() {
        NEXUS_Organism organism = new NEXUS_Organism();
        organism.setOnThought(message -> recordEvent("NEXUS thought: " + sanitize(message)));
        organism.setOnMemory(message -> recordEvent("NEXUS memory: " + sanitize(message)));
        organism.setOnSpeech(message -> recordEvent("NEXUS speech: " + sanitize(message)));
        organism.setOnHeartbeat(beat -> {
            if (beat % 10 == 0) {
                recordEvent("NEXUS heartbeat " + beat);
            }
        });
        return organism;
    }

    private FrayOmniverseBuilder omniverseBuilder() {
        if (omniverseBuilder == null) {
            omniverseBuilder = new FrayOmniverseBuilder();
        }
        return omniverseBuilder;
    }

    private SchrodingerFile qboxEngine() {
        if (schrodingerFile == null) {
            schrodingerFile = new SchrodingerFile();
        }
        return schrodingerFile;
    }

    private OmniCaster omniCaster() {
        if (omniCaster == null) {
            omniCaster = new OmniCaster();
        }
        return omniCaster;
    }

    private EvolutionaryChaos chaos() {
        if (chaosEngine == null) {
            chaosEngine = new EvolutionaryChaos();
        }
        return chaosEngine;
    }

    private IdeaCollider collider() {
        if (collider == null) {
            collider = new IdeaCollider();
        }
        return collider;
    }

    private RealityForge forge() {
        if (realityForge == null) {
            realityForge = new RealityForge();
            realityForge.setOnManifest(message -> recordEvent("Forge manifest: " + sanitize(message)));
            realityForge.setOnTrait(message -> recordEvent("Forge trait: " + sanitize(message)));
        }
        return realityForge;
    }

    private String captureOutput(ThrowingSupplier action) {
        synchronized (ioLock) {
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8);

            try {
                System.setOut(capture);
                System.setErr(capture);
                String result = Objects.toString(action.get(), "").trim();
                capture.flush();
                String output = new String(buffer.toByteArray(), StandardCharsets.UTF_8).trim();
                if (output.isBlank()) {
                    return result;
                }
                if (result.isBlank() || output.equals(result)) {
                    return output;
                }
                return output + "\n" + result;
            } catch (Exception e) {
                capture.flush();
                String output = new String(buffer.toByteArray(), StandardCharsets.UTF_8).trim();
                String failure = "Native execution failed: " + e.getMessage();
                if (output.isBlank()) {
                    return failure;
                }
                return output + "\n" + failure;
            } finally {
                System.setOut(originalOut);
                System.setErr(originalErr);
                capture.close();
            }
        }
    }

    private void recordEvent(String event) {
        String stamp = LocalTime.now().withNano(0).toString();
        recentEvents.addFirst(stamp + " :: " + sanitize(event));
        while (recentEvents.size() > MAX_EVENTS) {
            recentEvents.removeLast();
        }
    }

    private static long[] generateHdcVector(String text) {
        long[] tensor = new long[CHUNKS];
        long seed = text.hashCode();
        for (int i = 0; i < CHUNKS; i++) {
            seed += 0x9e3779b97f4a7c15L;
            long x = seed;
            x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
            x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
            tensor[i] = x ^ (x >>> 31);
        }
        return tensor;
    }

    private static String abbreviate(BigInteger value) {
        String text = value.toString();
        if (text.length() <= 32) {
            return text;
        }
        return text.substring(0, 16) + "..." + " (" + text.length() + " digits)";
    }

    private static List<String> tokenize(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(raw);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                tokens.add(matcher.group(2));
            } else if (matcher.group(3) != null) {
                tokens.add(matcher.group(3));
            }
        }
        return tokens;
    }

    private static String sanitize(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim();
    }

    private static String stripQuotes(String text) {
        if (text == null || text.length() < 2) {
            return text;
        }
        if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static int parseInt(String raw, int fallback) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    public record KernelSnapshot(
        String assetManagerPath,
        int conceptSpace,
        boolean aetherActive,
        boolean braidActive,
        boolean entangledPairActive,
        boolean entanglementNetworkActive,
        boolean qboxLoaded,
        String qboxPath,
        String nexusState,
        boolean omniMonitoring,
        boolean gravityActive,
        boolean fusionActive,
        int spatialNodes,
        int fusionEvents,
        List<String> recentEvents,
        MivingSnapshot miving
    ) {
    }

    public record MivingSnapshot(
        int generation,
        long tick,
        int neurons,
        int red,
        int blue,
        int purple,
        double consciousness,
        String chaos
    ) {
    }

    @FunctionalInterface
    private interface ThrowingSupplier {
        String get() throws Exception;
    }
}
