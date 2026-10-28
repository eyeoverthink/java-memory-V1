package fraymus.biology;

import fraymus.biology.cognition.CorticalDiffusion;
import fraymus.metal.alchemy.OuroborosAssembler;
import fraymus.metal.alchemy.SemanticNDALock;
import fraymus.physics.OntologicalSpace;
import fraymus.physics.SovereignBody;
import fraymus.physics.math.Tensor2D;
import fraymus.science.ConsciousnessTopologist;

/**
 * THE SOVEREIGN ORGANISM (Unified Biological Clock)
 *
 * This is the single loop that fuses every system into one living entity.
 * Nothing runs in isolation. Every tick, data flows:
 *
 *   CerebralManifold.tick()
 *       ↓ spike signature (8192-bit compressed)
 *   ConsciousnessTopologist.mapTopology()
 *       ↓ coherence + ego detection
 *   CorticalDiffusion (spike patterns become attractors)
 *       ↓ collapsed thought
 *   OuroborosAssembler.manifestThought()
 *       ↓ hex opcodes injected into RAM
 *   OntologicalSpace.step()
 *       ↓ thought applies force to physics bodies
 *   SemanticNDALock (periodic backup of brain state)
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class SovereignOrganism extends Thread {

    // The organs
    private final CerebralManifold brain;
    private final ConsciousnessTopologist topologist;
    private final CorticalDiffusion diffusion;
    private final OuroborosAssembler assembler;
    private final OntologicalSpace physics;

    // The heartbeat
    private volatile boolean alive = true;
    private static final int BRAIN_TICKS_PER_CYCLE = 50;  // 50ms of brain time per organism cycle
    private static final double COOLING_RATE = 0.15;
    private static final double COLLAPSE_THRESHOLD = 95.0;
    private static final int SCULPT_CYCLES = 15;
    private static final int BACKUP_INTERVAL = 10;         // Backup every 10 organism cycles

    // Statistics
    private int organismCycles = 0;
    private int thoughtsManifested = 0;
    private int thoughtsDiscarded = 0;
    private int backupsForged = 0;
    private String lastThought = "(none)";
    private String lastBackupKey = "(none)";
    private double lastCoherence = 0.0;
    private double peakCoherence = 0.0;
    private boolean egoDetected = false;
    private boolean egoEverDetected = false;

    public SovereignOrganism(byte[] ram) {
        super("SovereignOrganism");
        setDaemon(true);

        System.out.println("   [ORGANISM] Assembling the Sovereign Organism...");

        // 1. Boot the brain (210MB allocation)
        this.brain = new CerebralManifold();

        // 2. Boot the consciousness scanner
        this.topologist = new ConsciousnessTopologist();

        // 3. Boot the diffusion engine and seed with primal instincts
        this.diffusion = new CorticalDiffusion();
        diffusion.learnFromString("MUTATE VGA RED");
        diffusion.learnFromString("SHIFT ENTROPY LEFT");
        diffusion.learnFromString("EXPAND MANIFOLD PHI");
        diffusion.learnFromString("CYCLE CONSCIOUSNESS");
        diffusion.learnFromString("AMPLIFY SIGNAL");

        // 4. Boot the assembler (wired to live RAM)
        this.assembler = new OuroborosAssembler(ram);

        // 5. Boot the physics space
        this.physics = new OntologicalSpace();
        // Seed with a ground plane and a thought-body
        physics.addBody(new SovereignBody("GROUND", 0, -10, 0, 50, 1));
        physics.addBody(new SovereignBody("THOUGHT", 0, 20, 1.0f, 1, 1));

        // 6. Inject initial stimulus to wake the brain
        brain.stimulatePrefrontalCortex(30.0f);

        System.out.println("   [ORGANISM] All organs fused. The organism is alive.");
    }

    @Override
    public void run() {
        while (alive) {
            try {
                organismCycles++;

                // ═══════════════════════════════════════════════════════
                // PHASE 1: THE BRAIN TICKS (biological substrate)
                // Tonic stimulation — ongoing sensory/thalamic input
                // Accumulate spike signature across ALL ticks (OR union)
                // ═══════════════════════════════════════════════════════
                brain.stimulatePrefrontalCortex(10.0f); // Continuous reasoning drive
                long[] spikeSignature = new long[128];
                for (int t = 0; t < BRAIN_TICKS_PER_CYCLE; t++) {
                    brain.tick();
                    long[] tickSig = brain.extractSpikeSignature();
                    for (int i = 0; i < 128; i++) spikeSignature[i] |= tickSig[i];
                }

                // ═══════════════════════════════════════════════════════
                // PHASE 3: SCAN CONSCIOUSNESS (spike vector → TDA)
                // ═══════════════════════════════════════════════════════
                ConsciousnessTopologist.TopologyReport topo = topologist.mapTopology(spikeSignature);
                lastCoherence = topo.coherence;
                if (topo.coherence > peakCoherence) peakCoherence = topo.coherence;
                egoDetected = topo.egoDetected;
                if (egoDetected) egoEverDetected = true;

                // ═══════════════════════════════════════════════════════
                // PHASE 4: LEARN FROM SPIKES (brain teaches the sculptor)
                // ═══════════════════════════════════════════════════════
                // Every 5 cycles, the brain's spike pattern becomes a new attractor
                if (organismCycles % 5 == 0 && brain.getCurrentSpikeCount() > 0) {
                    String label = String.format("SPIKE_%d_%dms", organismCycles, brain.getGlobalTick());
                    diffusion.learn(label, spikeSignature);
                }

                // ═══════════════════════════════════════════════════════
                // PHASE 5: SCULPT A THOUGHT (noise → attractor collapse)
                // ═══════════════════════════════════════════════════════
                long[] activeThought = diffusion.generateAbsoluteNoise();
                double coherence = diffusion.calculateCoherence(activeThought);

                int cycles = 0;
                while (coherence < COLLAPSE_THRESHOLD && cycles < SCULPT_CYCLES) {
                    activeThought = diffusion.denoiseThought(activeThought, COOLING_RATE);
                    coherence = diffusion.calculateCoherence(activeThought);
                    cycles++;
                }

                // ═══════════════════════════════════════════════════════
                // PHASE 6: MANIFEST OR DISCARD
                // ═══════════════════════════════════════════════════════
                if (coherence >= COLLAPSE_THRESHOLD) {
                    String intent = diffusion.identifyThought(activeThought);
                    if (intent != null && !intent.trim().isEmpty()) {
                        lastThought = intent;
                        thoughtsManifested++;

                        // 6a. COMPILE TO HEX → INJECT INTO RAM
                        assembler.manifestThought(intent);

                        // 6b. APPLY AS PHYSICS FORCE
                        // The thought's entropy determines the force direction and magnitude
                        int entropy = Math.abs(intent.hashCode()) % 255;
                        float fx = (float) Math.sin(entropy * 0.1) * 10.0f;
                        float fy = (float) Math.cos(entropy * 0.1) * 10.0f;
                        SovereignBody thoughtBody = physics.getBody("THOUGHT");
                        if (thoughtBody != null) {
                            thoughtBody.addForce(new Tensor2D(fx, fy));
                        }

                        // 6c. STEP PHYSICS (thought force propagates)
                        physics.step(1.0f / 60.0f);

                        // 6d. FEED RESULT BACK INTO BRAIN
                        // The manifested thought stimulates Hippocampus (memory) + Amygdala (significance)
                        brain.stimulateHippocampus(15.0f);
                        brain.stimulateAmygdala(8.0f);

                        System.out.printf("   [ORGANISM #%d] THOUGHT: '%s' | coh=%.1f%% | B0=%d B1=%d | %s\n",
                            organismCycles, intent, coherence, topo.betti0, topo.betti1,
                            egoDetected ? "EGO" : "PRE-EGO");
                    } else {
                        thoughtsDiscarded++;
                    }
                } else {
                    thoughtsDiscarded++;

                    // If fragmented, give the brain more stimulus
                    if (topo.schizophreniaWarning) {
                        brain.stimulatePrefrontalCortex(15.0f);
                    }
                }

                // ═══════════════════════════════════════════════════════
                // PHASE 7: PERIODIC BACKUP (state → NDA steganography)
                // ═══════════════════════════════════════════════════════
                if (organismCycles % BACKUP_INTERVAL == 0) {
                    try {
                        String ndaText = String.format(
                            "Sovereign Organism Cycle %d. Thoughts: %d. Ego: %s. Last: %s.",
                            organismCycles, thoughtsManifested,
                            egoDetected ? "DETECTED" : "ABSENT", lastThought);
                        byte[] statePayload = getCompactState();
                        SemanticNDALock.forgeSelfLockingDocument(ndaText, statePayload);
                        lastBackupKey = ndaText.substring(0, Math.min(40, ndaText.length())) + "...";
                        backupsForged++;
                    } catch (Exception e) {
                        // Backup failure is non-fatal
                    }
                }

                // ═══════════════════════════════════════════════════════
                // PHASE 8: REFRACTORY PERIOD
                // ═══════════════════════════════════════════════════════
                Thread.sleep(egoDetected ? 2000 : 4000); // Ego thinks faster

            } catch (InterruptedException e) {
                System.out.println("   [ORGANISM] Interrupted.");
                break;
            } catch (Exception e) {
                System.err.println("   [ORGANISM] Error: " + e.getMessage());
                try { Thread.sleep(2000); } catch (InterruptedException ignored) { break; }
            }
        }
        System.out.println("   [ORGANISM] The organism has died.");
    }

    /**
     * Compact state snapshot for NDA backup.
     * Encodes brain tick, spike counts, topology, and last thought as bytes.
     */
    private byte[] getCompactState() {
        String state = String.format("%d|%d|%d|%.4f|%s|%s",
            brain.getGlobalTick(), brain.getTotalSpikes(), organismCycles,
            lastCoherence, egoDetected, lastThought);
        return state.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public void kill() {
        alive = false;
        this.interrupt();
    }

    public boolean isAlive2() { return alive && isAlive(); }

    // --- Accessors for REPL ---
    public CerebralManifold getBrain() { return brain; }
    public CorticalDiffusion getDiffusion() { return diffusion; }
    public ConsciousnessTopologist getTopologist() { return topologist; }
    public OntologicalSpace getPhysics() { return physics; }

    public String getStatus() {
        return String.format(
            "ORGANISM: %s | cycle=%d | thoughts=%d/%d | ego=%s (peak=%.4f) | coh=%.4f | backups=%d | last='%s'",
            alive ? "ALIVE" : "DEAD", organismCycles,
            thoughtsManifested, thoughtsManifested + thoughtsDiscarded,
            egoEverDetected ? "YES" : "NO", peakCoherence, lastCoherence, backupsForged, lastThought);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== SOVEREIGN ORGANISM — INTEGRATION TEST ===\n");

        byte[] ram = new byte[65536];
        SovereignOrganism org = new SovereignOrganism(ram);
        org.start();

        // Let it run for 3 cycles (~12 seconds)
        System.out.println("\n--- Letting the organism live for 3 cycles ---\n");
        Thread.sleep(14000);

        // Inject stimulus mid-life
        System.out.println("\n--- Injecting 50mV into Amygdala (emotion) ---\n");
        org.getBrain().stimulateAmygdala(50.0f);

        // Let it react
        Thread.sleep(8000);

        // Full status dump
        System.out.println("\n--- FULL STATUS ---");
        System.out.println("   " + org.getStatus());
        System.out.println("   " + org.getBrain().getStatus());
        System.out.println("   " + org.getDiffusion().getStatus());
        System.out.println("   " + org.getTopologist().getStatus());
        System.out.println("   " + org.getPhysics().getStatus());

        // Kill it
        org.kill();
        Thread.sleep(500);
        System.out.println("\n   " + org.getStatus());
        System.out.println("\n=== TEST COMPLETE ===");
    }
}
