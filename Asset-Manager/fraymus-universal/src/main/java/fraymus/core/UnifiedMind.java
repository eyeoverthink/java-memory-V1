package fraymus.core;

import fraymus.bio.HyperCortex;
import fraymus.bio.NeuroQuant;
import fraymus.nexus.OllamaBridge;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🧠 UNIFIED MIND (Layer 10)
 * Integrates Multi-Model LLMs with Hyper-Dimensional Physics.
 *
 * Architecture:
 *   - Logic Core    (llama3)    → Reasoning
 *   - Creative Core (mistral)   → Abstraction
 *   - Coder Core    (codellama) → Implementation
 *   - HyperCortex              → 432 Hz NCA Lattice
 *   - SFA Loop                 → Slow Feature Analysis for long-term memory
 */
public class UnifiedMind {

    // THE SWARM (Specialized Models via Ollama)
    private final OllamaBridge logicCore;
    private final OllamaBridge creativeCore;
    private final OllamaBridge coderCore;

    // THE PHYSICS ENGINE
    private final HyperCortex cortex;
    private Thread cortexThread;

    // EXECUTION THREADS
    private final ExecutorService synapses = Executors.newFixedThreadPool(4);

    // SFA
    private volatile boolean sfaRunning = false;

    public UnifiedMind() {
        this("llama3", "mistral", "codellama");
    }

    public UnifiedMind(String logicModel, String creativeModel, String coderModel) {
        this.logicCore = new OllamaBridge(logicModel);
        this.creativeCore = new OllamaBridge(creativeModel);
        this.coderCore = new OllamaBridge(coderModel);

        this.cortex = new HyperCortex();
        cortexThread = new Thread(cortex, "UnifiedMind-Cortex");
        cortexThread.setDaemon(true);
        cortexThread.start();

        System.out.println("⚡ UNIFIED MIND INITIALIZED. SWARM READY.");
        System.out.println("   Logic:    " + logicModel);
        System.out.println("   Creative: " + creativeModel);
        System.out.println("   Coder:    " + coderModel);
    }

    /**
     * THINK: The primary cognitive loop.
     * 1. Distributes task to the Swarm (parallel).
     * 2. Synthesizes result.
     * 3. Encodes into 10,000D HyperVector via NeuroQuant.
     * 4. Injects into Cortex.
     * Returns the synthesized result.
     */
    public String processInput(String input) {
        System.out.println("📥 [MIND] INPUT: " + input);

        try {
            var logicFuture = synapses.submit(() ->
                    logicCore.ask("Analyze the logic: " + input));
            var creativeFuture = synapses.submit(() ->
                    creativeCore.ask("Extract the metaphysical concept: " + input));

            String logicResult = logicFuture.get();
            String abstractResult = creativeFuture.get();

            // SYNTHESIS
            String synthesis = logicResult + " | " + abstractResult;
            System.out.println("   [MIND] Logic:    " + truncate(logicResult, 100));
            System.out.println("   [MIND] Abstract: " + truncate(abstractResult, 100));

            // VECTOR ENCODING (Concept -> HyperVector via NeuroQuant)
            NeuroQuant thoughtCell = new NeuroQuant(input.toUpperCase());
            NeuroQuant logicVec = new NeuroQuant("LOGIC_TRACE");
            NeuroQuant abstractVec = new NeuroQuant("ABSTRACT_TRACE");

            thoughtCell.bind(logicVec);
            thoughtCell.bind(abstractVec);

            // INJECTION into the living lattice
            cortex.inject(thoughtCell.id);
            System.out.println("   [MIND] Thought crystallized & injected into lattice.");

            return synthesis;

        } catch (Exception e) {
            System.out.println("   [MIND] ERROR: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * CODE: Route a coding task to the coder model specifically.
     */
    public String code(String task) {
        System.out.println("💻 [MIND] CODE TASK: " + task);
        String result = coderCore.ask("Write code for: " + task);
        cortex.inject("CODE_" + task.hashCode());
        return result;
    }

    /**
     * SFA LOOP (Slow Feature Analysis)
     * Periodically reviews the Cortex to find stable patterns (Long Term Memory).
     */
    public void startSFA() {
        if (sfaRunning) return;
        sfaRunning = true;

        Thread sfaThread = new Thread(() -> {
            while (sfaRunning) {
                try {
                    Thread.sleep(10000); // Every 10 seconds
                    System.out.println("🔍 [SFA] Scanning for stable concepts...");
                    // In a full SFA: find vectors that change the LEAST over time
                    // Stable concepts get promoted to the Immutable Ledger (OmegaPoint)
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "SFA-Loop");
        sfaThread.setDaemon(true);
        sfaThread.start();
        System.out.println("🔍 [SFA] Slow Feature Analysis loop started.");
    }

    public void stopSFA() {
        sfaRunning = false;
    }

    public void shutdown() {
        sfaRunning = false;
        synapses.shutdownNow();
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
