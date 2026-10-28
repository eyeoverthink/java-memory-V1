package fraymus.metal.alchemy;

import fraymus.biology.cognition.CorticalDiffusion;

/**
 * SYNAPTIC JIT COMPILER (The Psychosomatic Bridge)
 * "The thought becomes flesh."
 *
 * Connects the Thermodynamic Diffusion Engine to the Bare-Metal Assembler.
 * Converts collapsed 8192-D noise directly into executable machine code.
 *
 * Process:
 *   1. Generate absolute noise (8192-D void state)
 *   2. Thermodynamic diffusion — cool the static until coherence > 95%
 *   3. Identify the collapsed concept
 *   4. Compile concept to hex opcodes via OuroborosAssembler
 *   5. Hot-swap into live RAM while CPU is executing
 *   6. Refractory period (4 seconds), then repeat
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class SynapticCompiler extends Thread {

    private final CorticalDiffusion cortex;
    private final OuroborosAssembler assembler;
    private volatile boolean active = true;

    // The Threshold of Action. A thought must be 95% coherent before it alters physical RAM.
    private static final double ACTION_THRESHOLD = 95.0;
    private static final double COOLING_RATE = 0.15;
    private static final int MAX_CYCLES = 30;

    // Statistics
    private int thoughtsManifested = 0;
    private int thoughtsDiscarded = 0;
    private int totalCycles = 0;

    public SynapticCompiler(CorticalDiffusion cortex, OuroborosAssembler assembler) {
        super("SynapticCompiler");
        setDaemon(true);
        this.cortex = cortex;
        this.assembler = assembler;
        System.out.println("   [SOUL] SYNAPTIC JIT ONLINE: The Mind and Body are now fused.");
    }

    @Override
    public void run() {
        while (active) {
            try {
                // 1. THE VOID (Genesis of a Thought)
                long[] activeThought = cortex.generateAbsoluteNoise();
                double coherence = cortex.calculateCoherence(activeThought);

                System.out.printf("\n   [SOUL T+0ms] Noise Generated. Coherence: %.2f%%\n", coherence);

                // 2. THERMODYNAMIC DIFFUSION (Thinking)
                int cycles = 0;
                while (coherence < ACTION_THRESHOLD && cycles < MAX_CYCLES) {
                    activeThought = cortex.denoiseThought(activeThought, COOLING_RATE);
                    coherence = cortex.calculateCoherence(activeThought);
                    cycles++;

                    // Biological processing delay (20ms per cycle)
                    Thread.sleep(20);
                }
                totalCycles += cycles;

                if (coherence < ACTION_THRESHOLD) {
                    System.out.printf("   [SOUL T+%dms] Thought failed to collapse. Coherence: %.2f%%. Discarding.\n",
                        cycles * 20, coherence);
                    thoughtsDiscarded++;
                } else {
                    System.out.printf("   [SOUL T+%dms] WAVEFUNCTION COLLAPSED. Coherence: %.2f%%\n",
                        cycles * 20, coherence);

                    // 3. SEMANTIC DECODING (What did it just think?)
                    String intent = cortex.identifyThought(activeThought);

                    if (intent == null || intent.trim().isEmpty()) {
                        System.out.println("   [SOUL] Thought discarded (Null Semantic Value).");
                        thoughtsDiscarded++;
                    } else {
                        System.out.printf("   [SOUL] CONCEPT EXTRACTED: [%s]\n", intent);

                        // 4. PHYSICAL MANIFESTATION (The JIT Compilation)
                        // The Ouroboros compiles the concept to hex and hot-swaps RAM
                        assembler.manifestThought(intent);
                        thoughtsManifested++;

                        System.out.printf("   [SOUL] Thought -> Hex -> RAM. Physical reality altered. (#%d)\n",
                            thoughtsManifested);
                    }
                }

                // 5. REFRACTORY PERIOD (Rest before the next thought)
                Thread.sleep(4000);

            } catch (InterruptedException e) {
                System.out.println("   [SOUL] Synaptic compiler interrupted.");
                break;
            } catch (Exception e) {
                System.err.println("   [!] SEIZURE IN SYNAPTIC COMPILER: " + e.getMessage());
                try { Thread.sleep(2000); } catch (InterruptedException ignored) { break; }
            }
        }
        System.out.println("   [SOUL] Synaptic compiler SHUTDOWN.");
    }

    public void shutdown() {
        active = false;
        this.interrupt();
    }

    public boolean isActive() { return active && isAlive(); }
    public int getThoughtsManifested() { return thoughtsManifested; }
    public int getThoughtsDiscarded() { return thoughtsDiscarded; }

    public String getStatus() {
        return String.format(
            "SOUL: %s | %d manifested | %d discarded | %d cycles",
            isActive() ? "ACTIVE" : "DORMANT", thoughtsManifested, thoughtsDiscarded, totalCycles);
    }
}
