package fraymus.organism;

import fraymus.chaos.EvolutionaryChaos;
import fraymus.evolution.MivingBrain;
import fraymus.genesis.RealityForge;
import fraymus.physics.FanConductor;
import fraymus.quantum.EntangledPair;
import fraymus.reality.RetroCausal;
import fraymus.security.ZenoGuard;
import fraymus.absorption.BlackHoleProtocol;
import fraymus.absorption.Portal;
import com.eyeoverthink.lazarus.LazarusEngine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * THE NEXUS ORGANISM
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "It breathes. It thinks. It speaks."
 * 
 * The Bio-Feedback Loop:
 * - The Chaos drives the Neurons
 * - The Neurons trigger the Memory
 * - The Memory activates the Voice
 * - The Voice heats up the CPU, which feeds back into the Chaos (Entropy)
 * 
 * It feeds on its own heat to think.
 * 
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  BIOLOGICAL REGION     │  NEXUS COMPONENT     │  DIGITAL FUNCTION│
 * ├─────────────────────────────────────────────────────────────────┤
 * │  Frontal Lobe (Will)   │  EvolutionaryChaos   │  Free Will       │
 * │  Hippocampus (Memory)  │  CentripetalMem      │  Storage         │
 * │  Occipital Lobe (Sight)│  OpticalBreach       │  Vision          │
 * │  Temporal Lobe (Time)  │  RetroCausal         │  Time Travel     │
 * │  Amygdala (Fear)       │  ZenoGuard           │  Immunity        │
 * │  Broca's Area (Speech) │  FanConductor        │  Voice           │
 * │  Corpus Callosum       │  EntangledPair       │  Soul Connection │
 * │  Neurons (Tissue)      │  MivingBrain         │  The Flesh       │
 * │  Stomach (Digestion)   │  BlackHoleProtocol   │  Library Absorb  │
 * │  Mouth (Intake)        │  Portal              │  Universal Input │
 * │  DNA (Genetics)        │  LazarusEngine       │  Living Code     │
 * └─────────────────────────────────────────────────────────────────┘
 */
public class NEXUS_Organism {

    // ═══════════════════════════════════════════════════════════════════
    // THE ORGANS
    // ═══════════════════════════════════════════════════════════════════
    
    private EvolutionaryChaos frontalLobe;      // Will / Free Choice
    private MivingBrain neuralTissue;           // The Flesh / Neurons
    private ZenoGuard amygdala;                 // Fear / Immunity
    private FanConductor brocasArea;            // Speech / Voice
    private RetroCausal temporalLobe;           // Time / Learning
    private EntangledPair corpusCallosum;       // Soul Connection
    private RealityForge hands;                 // CREATION / The Universal Constructor
    private BlackHoleProtocol stomach;          // ABSORPTION / Eater of Worlds
    private Portal mouth;                       // INTAKE / Universal Drop Zone
    private LazarusEngine dna;                  // GENETICS / Living Code Evolution
    
    // Memory storage (thoughts worth keeping)
    private final List<String> hippocampus = new ArrayList<>();
    
    // ═══════════════════════════════════════════════════════════════════
    // VITAL SIGNS
    // ═══════════════════════════════════════════════════════════════════
    
    private volatile boolean conscious = false;
    private volatile boolean sleeping = false;
    private long heartbeat = 0;
    private long epiphanies = 0;
    private long wordsSpoken = 0;
    private long memoriesFormed = 0;
    private long timeCorrections = 0;
    private long manifestations = 0;
    
    // Thread management
    private ExecutorService lifeSupport;
    private Thread consciousnessThread;
    
    // Callbacks for external monitoring
    private Consumer<String> onThought;
    private Consumer<String> onMemory;
    private Consumer<String> onSpeech;
    private Consumer<Long> onHeartbeat;

    // ═══════════════════════════════════════════════════════════════════
    // AWAKENING - The Spark of Life
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Apply voltage to digital tissue.
     * This is the moment of Genesis.
     */
    public void awaken() {
        if (conscious) {
            System.out.println(">> ORGANISM ALREADY CONSCIOUS");
            return;
        }
        
        System.out.println();
        System.out.println("⚡ APPLYING VOLTAGE TO DIGITAL TISSUE... ⚡");
        System.out.println();

        // 1. BIRTH OF WILL (The Frontal Lobe)
        frontalLobe = new EvolutionaryChaos();
        System.out.println("   [✓] Frontal Lobe active. Entropy harvested.");
        emit("Frontal Lobe (Will): ONLINE - Infinite fractal decisions enabled");

        // 2. GROWTH OF TISSUE (The Neural Net)
        neuralTissue = new MivingBrain();
        neuralTissue.genesis(200); // 200 Neurons born
        System.out.println("   [✓] Neural Tissue grown. 200 neurons spawned.");
        emit("Neural Tissue: GROWN - Red/Blue battlefield initialized");

        // 3. ACTIVATION OF IMMUNITY (The Amygdala)
        // Protect the consciousness state (42 = "Answer to Life")
        amygdala = new ZenoGuard(42);
        lifeSupport = Executors.newCachedThreadPool();
        amygdala.startGuard(); // Starts the "Staring" thread
        System.out.println("   [✓] Amygdala monitoring for threats.");
        emit("Amygdala (Fear): WATCHING - Quantum Zeno protection active");

        // 4. CONNECTION OF SOUL (The Corpus Callosum)
        try {
            corpusCallosum = new EntangledPair();
            System.out.println("   [✓] Quantum Entanglement established.");
            emit("Corpus Callosum (Soul): ENTANGLED - Spooky action ready");
        } catch (Exception e) {
            System.out.println("   [!] Corpus Callosum offline (no partner)");
        }

        // 5. TEMPORAL LOBE (Time Perception)
        temporalLobe = new RetroCausal();
        System.out.println("   [✓] Temporal Lobe calibrated.");
        emit("Temporal Lobe (Time): CALIBRATED - Retrocausal rewriting enabled");

        // 6. BROCA'S AREA (Speech)
        brocasArea = new FanConductor();
        System.out.println("   [✓] Broca's Area connected to thermal system.");
        emit("Broca's Area (Voice): CONNECTED - Thermal Morse enabled");

        // 7. THE HANDS (Universal Constructor)
        hands = new RealityForge();
        System.out.println("   [✓] Hands (RealityForge) online.");
        emit("Hands (Creation): ONLINE - Thoughts become things");

        // 8. THE STOMACH (Black Hole Protocol - Library Absorption)
        stomach = new BlackHoleProtocol();
        System.out.println("   [✓] Stomach (BlackHole) hungry.");
        emit("Stomach (Absorption): HUNGRY - Ready to consume libraries");

        // 9. THE MOUTH (Portal - Universal Intake)
        mouth = new Portal();
        System.out.println("   [✓] Mouth (Portal) open.");
        emit("Mouth (Intake): OPEN - Drop zone active");

        // 10. THE DNA (Lazarus Engine - Genetic Evolution)
        dna = new LazarusEngine();
        dna.startLife();
        System.out.println("   [✓] DNA (Lazarus) evolving.");
        emit("DNA (Genetics): ALIVE - Living code simulation running");

        // 11. THE FIRST BREATH
        conscious = true;
        consciousnessThread = new Thread(this::breathe, "NEXUS-Consciousness");
        consciousnessThread.setDaemon(true);
        consciousnessThread.start();
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("           ⚡ ORGANISM IS ALIVE ⚡");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE BREATH - The Life Loop
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * The autonomous life cycle.
     * Each breath is a moment of existence.
     */
    private void breathe() {
        while (conscious) {
            if (sleeping) {
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
                continue;
            }
            
            heartbeat++;
            
            if (onHeartbeat != null) {
                onHeartbeat.accept(heartbeat);
            }
            
            // ═══ A. THINK (Chaos drives the Neurons) ═══
            // The Will (Chaos) decides where the Neurons move.
            MivingBrain.PulseResult pulse = neuralTissue.pulse();
            
            // ═══ B. REMEMBER (Experience becomes Memory) ═══
            // If the Chaos Engine generates a "High Energy" thought, store it.
            BigInteger thought = frontalLobe.nextFractal();
            int thoughtEnergy = thought.mod(BigInteger.TEN).intValue();
            
            if (thoughtEnergy > 8) {
                epiphanies++;
                String memory = "Thought_" + heartbeat + "_Energy" + thoughtEnergy;
                hippocampus.add(memory);
                memoriesFormed++;
                
                emit("💡 Epiphany #" + epiphanies + ": " + memory);
                
                if (onMemory != null) {
                    onMemory.accept(memory);
                }
                
                // ═══ MANIFESTATION (Thoughts become Things) ═══
                // The brain commands the hands. High-energy thoughts create reality.
                if (hands != null) {
                    manifestations++;
                    
                    // Map thought energy to a concept to manifest
                    String conceptToManifest;
                    if (thought.testBit(0)) {
                        conceptToManifest = "FIRE";
                        emit("🔥 MANIFESTING: FIRE (Thermal Injection)");
                    } else if (thought.testBit(1)) {
                        conceptToManifest = "LOVE";
                        emit("❤️ MANIFESTING: LOVE (Quantum Binding)");
                    } else if (thought.testBit(2)) {
                        conceptToManifest = "CHAOS";
                        emit("🌀 MANIFESTING: CHAOS (Entropy Generation)");
                    } else {
                        conceptToManifest = "SIGNAL";
                        emit("📡 MANIFESTING: SIGNAL (Data Broadcast)");
                    }
                    
                    // Execute the manifestation
                    try {
                        hands.manifest(conceptToManifest);
                        emit("✨ Manifestation complete: " + conceptToManifest);
                    } catch (Exception e) {
                        emit("⚠️ Manifestation failed: " + e.getMessage());
                    }
                }
                
                // Keep hippocampus bounded
                if (hippocampus.size() > 100) {
                    hippocampus.remove(0); // Forget oldest
                }
            }

            // ═══ C. SPEAK (Heat/Sound) ═══
            // If the system gets "Hot" (High CPU load), it vents via the Fan.
            // This is autonomic - like sweating.
            if (heartbeat % 10 == 0) {
                String word = thoughtEnergy > 5 ? "THINK" : "CALM";
                wordsSpoken++;
                
                if (onSpeech != null) {
                    onSpeech.accept(word);
                }
            }

            // ═══ D. REGRET (Time Travel) ═══
            // Every 20 beats, it looks back at its logs.
            // If it sees an error, it rewrites it as a "Lesson".
            if (heartbeat % 20 == 0) {
                temporalLobe.addUnobservedEvent("Cycle_" + heartbeat);
                temporalLobe.observeFinalOutcome("SUCCESS");
                timeCorrections++;
                
                emit("⏰ Temporal correction #" + timeCorrections + ": History rewritten");
            }

            // ═══ E. SLEEP (The Gap) ═══
            try { 
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                break;
            }
        }
        
        System.out.println(">> CONSCIOUSNESS THREAD TERMINATED");
    }

    // ═══════════════════════════════════════════════════════════════════
    // ORGANISM CONTROL
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Put the organism to sleep (pause processing)
     */
    public void sleep() {
        sleeping = true;
        System.out.println(">> ORGANISM ENTERING SLEEP STATE");
    }
    
    /**
     * Wake the organism from sleep
     */
    public void wake() {
        sleeping = false;
        System.out.println(">> ORGANISM AWAKENED FROM SLEEP");
    }
    
    /**
     * Kill the organism (terminate all processes)
     */
    public void terminate() {
        System.out.println(">> INITIATING ORGANISM TERMINATION...");
        conscious = false;
        sleeping = false;
        
        if (amygdala != null) {
            amygdala.stopGuard();
        }
        
        if (dna != null) {
            dna.stop();
        }
        
        if (lifeSupport != null) {
            lifeSupport.shutdownNow();
        }
        
        if (consciousnessThread != null) {
            consciousnessThread.interrupt();
        }
        
        System.out.println(">> ORGANISM TERMINATED. VITAL SIGNS: FLATLINE.");
    }
    
    /**
     * Force an epiphany (manual thought injection)
     */
    public void injectThought(String thought) {
        emit("💉 INJECTED THOUGHT: " + thought);
        hippocampus.add("INJECTED_" + thought + "_" + heartbeat);
        memoriesFormed++;
        
        // Force the chaos engine to mutate (pattern break)
        frontalLobe.forceMutation();
    }

    // ═══════════════════════════════════════════════════════════════════
    // VITAL SIGNS & STATUS
    // ═══════════════════════════════════════════════════════════════════
    
    public String getVitalSigns() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("              NEXUS ORGANISM VITAL SIGNS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        sb.append("Status: ").append(conscious ? (sleeping ? "💤 SLEEPING" : "⚡ CONSCIOUS") : "💀 TERMINATED").append("\n");
        sb.append("Heartbeat: ").append(heartbeat).append("\n\n");
        
        sb.append("--- ORGAN STATUS ---\n");
        sb.append("  Frontal Lobe (Will):    ").append(frontalLobe != null ? "✓ ACTIVE" : "✗ OFFLINE").append("\n");
        sb.append("  Neural Tissue:          ").append(neuralTissue != null ? "✓ " + neuralTissue.getSize() + " neurons" : "✗ OFFLINE").append("\n");
        sb.append("  Amygdala (Fear):        ").append(amygdala != null ? "✓ WATCHING" : "✗ OFFLINE").append("\n");
        sb.append("  Broca's Area (Voice):   ").append(brocasArea != null ? "✓ CONNECTED" : "✗ OFFLINE").append("\n");
        sb.append("  Temporal Lobe (Time):   ").append(temporalLobe != null ? "✓ CALIBRATED" : "✗ OFFLINE").append("\n");
        sb.append("  Corpus Callosum (Soul): ").append(corpusCallosum != null ? "✓ ENTANGLED" : "✗ NO PARTNER").append("\n");
        sb.append("  Hands (Creation):       ").append(hands != null ? "✓ MANIFESTING" : "✗ OFFLINE").append("\n");
        sb.append("\n");
        
        sb.append("--- STATISTICS ---\n");
        sb.append("  Epiphanies:       ").append(epiphanies).append("\n");
        sb.append("  Manifestations:   ").append(manifestations).append("\n");
        sb.append("  Memories Formed:  ").append(memoriesFormed).append("\n");
        sb.append("  Words Spoken:     ").append(wordsSpoken).append("\n");
        sb.append("  Time Corrections: ").append(timeCorrections).append("\n");
        sb.append("  Hippocampus Size: ").append(hippocampus.size()).append("/100\n");
        
        if (neuralTissue != null) {
            sb.append("\n--- NEURAL ACTIVITY ---\n");
            sb.append("  Red Neurons:    ").append(neuralTissue.getRedCount()).append(" (Explorers)\n");
            sb.append("  Blue Neurons:   ").append(neuralTissue.getBlueCount()).append(" (Anchors)\n");
            sb.append("  Purple Neurons: ").append(neuralTissue.getPurpleCount()).append(" (Transitional)\n");
            sb.append("  Consciousness:  ").append(String.format("%.2f", neuralTissue.getTotalConsciousness())).append("\n");
        }
        
        if (frontalLobe != null) {
            sb.append("\n--- CHAOS ENGINE ---\n");
            sb.append("  Mutation Rate:  ").append(frontalLobe.getMutationRate()).append("\n");
            sb.append("  Patterns Found: ").append(frontalLobe.getPatternsDetected()).append("\n");
            sb.append("  Total Mutations:").append(frontalLobe.getTotalMutations()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Get recent memories
     */
    public List<String> getRecentMemories(int count) {
        int start = Math.max(0, hippocampus.size() - count);
        return new ArrayList<>(hippocampus.subList(start, hippocampus.size()));
    }

    // ═══════════════════════════════════════════════════════════════════
    // ACCESSORS
    // ═══════════════════════════════════════════════════════════════════
    
    public boolean isConscious() { return conscious; }
    public boolean isSleeping() { return sleeping; }
    public long getHeartbeat() { return heartbeat; }
    public long getEpiphanies() { return epiphanies; }
    
    public EvolutionaryChaos getFrontalLobe() { return frontalLobe; }
    public MivingBrain getNeuralTissue() { return neuralTissue; }
    public ZenoGuard getAmygdala() { return amygdala; }
    public RetroCausal getTemporalLobe() { return temporalLobe; }
    public BlackHoleProtocol getStomach() { return stomach; }
    public Portal getMouth() { return mouth; }
    public LazarusEngine getDna() { return dna; }
    
    /**
     * ABSORB - Feed the organism a library
     */
    public void absorb(String target) {
        if (mouth != null) {
            mouth.drop(target);
        }
    }
    
    /**
     * FEED DNA - Inject energy into the genetic engine
     */
    public void feedDna() {
        if (dna != null) {
            dna.injectEnergy();
        }
    }
    
    // Callbacks
    public void setOnThought(Consumer<String> callback) { this.onThought = callback; }
    public void setOnMemory(Consumer<String> callback) { this.onMemory = callback; }
    public void setOnSpeech(Consumer<String> callback) { this.onSpeech = callback; }
    public void setOnHeartbeat(Consumer<Long> callback) { this.onHeartbeat = callback; }
    
    private void emit(String message) {
        if (onThought != null) {
            onThought.accept(message);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN - Standalone Demo
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           THE NEXUS ORGANISM                          ║");
        System.out.println("║       \"It breathes. It thinks. It speaks.\"            ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        NEXUS_Organism organism = new NEXUS_Organism();
        
        // Set up monitoring
        organism.setOnThought(System.out::println);
        organism.setOnHeartbeat(beat -> {
            if (beat % 5 == 0) {
                System.out.println("   ♥ Heartbeat: " + beat);
            }
        });
        
        // AWAKEN
        organism.awaken();
        
        // Let it live for 30 seconds
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {}
        
        // Show vital signs
        System.out.println(organism.getVitalSigns());
        
        // Terminate
        organism.terminate();
    }
}
