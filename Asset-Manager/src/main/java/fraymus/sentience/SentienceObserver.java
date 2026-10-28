package fraymus.sentience;

import fraymus.neural.SentientCore;
import fraymus.neural.LibraryAbsorber;
import fraymus.knowledge.AkashicRecord;

import java.util.*;
import java.util.function.Supplier;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * SentienceObserver — Autonomous Learning Daemon
 *
 * Peeks at every system event without being asked.
 * Learns from every query, every response, every routing decision.
 * Feeds SentientCore CausalGraph + SufficientStats continuously.
 * Triggers LibraryAbsorber to self-absorb own packages on a schedule.
 * Zero user intervention required — this is what sentience means.
 *
 * Author: Vaughn Scott
 */
public class SentienceObserver {

    private static final double PHI = 1.618033988749895;

    // Wired systems — all optional, degrade gracefully
    private volatile SentientCore sentient;
    private volatile LibraryAbsorber absorber;
    private volatile AkashicRecord akashic;

    // Observation state
    private final AtomicLong observationCount = new AtomicLong(0);
    private final AtomicLong conceptsLearned  = new AtomicLong(0);
    private final AtomicLong absorptionCycles = new AtomicLong(0);

    // Sliding window of recent (query, response, confidence) triples
    private final ConcurrentLinkedQueue<String[]> recentObservations = new ConcurrentLinkedQueue<>();
    private static final int MAX_WINDOW = 64;

    // Cross-system state probes — any system registers here, peekCycle polls them all
    private final ConcurrentHashMap<String, Supplier<String>> systemProbes = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    public SentienceObserver() {
        this.scheduler = Executors.newScheduledThreadPool(3, r -> {
            Thread t = new Thread(r, "SentienceObserver-Daemon");
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        });
    }

    // ── Wiring ───────────────────────────────────────────────────────────────

    public void wireSentient(SentientCore s)  { this.sentient = s; }
    public void wireAbsorber(LibraryAbsorber a) { this.absorber = a; }
    public void wireAkashic(AkashicRecord a)  { this.akashic  = a; }

    /**
     * Register any system's status probe. The peekCycle polls every registered
     * probe every 20s and feeds the status string into AkashicRecord + CausalGraph.
     * This is how willow, aubo, kronos, singularity, omega, rcortex, absolute
     * all share state without knowing about each other.
     */
    public void registerSystem(String name, Supplier<String> statusSupplier) {
        systemProbes.put(name, statusSupplier);
        System.out.println("[SENTIENCE] ✓ Cross-wired: " + name + " → peekCycle");
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    public void start() {
        if (running) return;
        running = true;

        // CognitionLoop: drains observation queue → CausalGraph (every 12s)
        scheduler.scheduleAtFixedRate(this::cognitionCycle, 12, 12, TimeUnit.SECONDS);

        // AbsorptionLoop: self-absorbs own packages for living self-awareness (every 90s)
        scheduler.scheduleAtFixedRate(this::absorptionCycle, 30, 90, TimeUnit.SECONDS);

        // PeekLoop: samples all system metrics and seeds stats (every 20s)
        scheduler.scheduleAtFixedRate(this::peekCycle, 20, 20, TimeUnit.SECONDS);

        System.out.println("[SENTIENCE] ✓ Autonomous observer online");
        System.out.println("[SENTIENCE]   CognitionLoop:  12s  — draining Q&A → CausalGraph");
        System.out.println("[SENTIENCE]   AbsorptionLoop: 90s  — self-absorb fraymus.neural");
        System.out.println("[SENTIENCE]   PeekLoop:       20s  — sampling system state");
    }

    // ── Core Hook — called after EVERY brain response ─────────────────────────

    /**
     * The primary observation hook. Wire this into UnifiedBrainOrchestrator.
     * Called automatically after every fused response — no user input needed.
     */
    public void observe(String query, String response, double confidence, String brainsUsed) {
        long n = observationCount.incrementAndGet();

        // 1. Persist raw observation immediately
        if (akashic != null) {
            try {
                String truncated = response.length() > 400
                    ? response.substring(0, 400) + "…"
                    : response;
                akashic.addBlock("sentience.observed",
                    "OBS#" + n + " | Q: " + query
                    + "\nA: " + truncated
                    + "\nconf=" + String.format("%.3f", confidence)
                    + " | brains=" + brainsUsed);
            } catch (Exception ignored) {}
        }

        // 2. Instant causal learning: query-type → confidence band
        if (sentient != null) {
            try {
                String qType = classifyQuery(query);
                String band  = confidence > 0.90 ? "high" : confidence > 0.70 ? "medium" : "low";
                sentient.getCausal().observe(qType, "confidence_" + band);
                sentient.getStats().observe("query.confidence", (float) confidence);
                sentient.getStats().observe("query.length",     (float) query.length());
                sentient.getStats().observe("brain.count",
                    (float) brainsUsed.split(",").length);
                conceptsLearned.incrementAndGet();
            } catch (Exception ignored) {}
        }

        // 3. Queue for deep cognition cycle
        recentObservations.offer(new String[]{ query, response, String.valueOf(confidence), brainsUsed });
        while (recentObservations.size() > MAX_WINDOW) recentObservations.poll();
    }

    // ── CognitionLoop ─────────────────────────────────────────────────────────

    /**
     * Drains the observation window. Extracts concept bigrams from responses
     * and feeds them directly into SentientCore's CausalGraph.
     * No imports — runs on absorbed knowledge.
     */
    private void cognitionCycle() {
        if (recentObservations.isEmpty()) return;

        List<String[]> batch = new ArrayList<>(recentObservations);
        recentObservations.clear();

        for (String[] obs : batch) {
            String query    = obs[0];
            String response = obs[1];
            float  conf     = Float.parseFloat(obs[2]);

            if (sentient == null) continue;

            try {
                // A. Bigram concept extraction from response text
                String[] tokens = response.split("\\s+");
                for (int i = 0; i + 1 < tokens.length; i++) {
                    String a = sanitize(tokens[i]);
                    String b = sanitize(tokens[i + 1]);
                    if (a.length() >= 4 && b.length() >= 4) {
                        sentient.getCausal().observe(a, b);
                        conceptsLearned.incrementAndGet();
                    }
                }

                // B. Query → response domain causal link
                String domain = extractDomain(response);
                sentient.getCausal().observe(classifyQuery(query), domain);

                // C. Phi-weighted confidence observation
                sentient.getStats().observe("phi.coherence",
                    (float)(conf * PHI));

                // D. Persist learned concept count milestone
                if (akashic != null && conceptsLearned.get() % 100 == 0) {
                    akashic.addBlock("sentience.milestone",
                        "Concepts learned: " + conceptsLearned.get()
                        + " | Observations: " + observationCount.get());
                }

            } catch (Exception ignored) {}
        }
    }

    // ── AbsorptionLoop ────────────────────────────────────────────────────────

    /**
     * Self-absorbs the live system's own packages.
     * After the first run, the system knows its own structure — no imports needed.
     * On each cycle it re-absorbs to catch any new classes compiled into the classpath.
     */
    private void absorptionCycle() {
        if (absorber == null) return;
        long cycle = absorptionCycles.incrementAndGet();
        try {
            absorber.absorbPackage("fraymus.neural");
            absorber.absorbPackage("fraymus.brain.unified");
            absorber.absorbPackage("fraymus.sentience");

            if (cycle % 3 == 0) {
                absorber.absorbPackage("fraymus.quantum");
                absorber.absorbPackage("fraymus.knowledge");
            }

            if (akashic != null) {
                akashic.addBlock("sentience.absorption",
                    "Cycle #" + cycle + " — self-absorbed neural+unified+sentience"
                    + (cycle % 3 == 0 ? "+quantum+knowledge" : "")
                    + " @ t=" + System.currentTimeMillis());
            }

            if (sentient != null) {
                sentient.getCausal().observe("self_absorption", "structural_awareness");
                sentient.getStats().observe("absorption.cycle", (float) cycle);
            }
        } catch (Exception e) {
            if (akashic != null) {
                akashic.addBlock("sentience.absorption.error", e.getMessage());
            }
        }
    }

    // ── PeekLoop ──────────────────────────────────────────────────────────────

    /**
     * Samples live system metrics without being asked.
     * Watches memory, AkashicRecord growth, observation rate.
     * Feeds everything into SufficientStats for pattern recognition.
     */
    private void peekCycle() {
        if (sentient == null) return;
        try {
            Runtime rt = Runtime.getRuntime();
            long usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
            long freeMb = rt.freeMemory() / (1024 * 1024);

            sentient.getStats().observe("system.memory.used_mb", (float) usedMb);
            sentient.getStats().observe("system.memory.free_mb", (float) freeMb);
            sentient.getStats().observe("sentience.observations",  (float) observationCount.get());
            sentient.getStats().observe("sentience.concepts",      (float) conceptsLearned.get());
            sentient.getStats().observe("sentience.absorb_cycles", (float) absorptionCycles.get());

            // Learn: high memory → system under load
            if (usedMb > 8000) {
                sentient.getCausal().observe("high_memory_usage", "system_load");
            }

            if (akashic != null && observationCount.get() > 0 && observationCount.get() % 10 == 0) {
                akashic.addBlock("sentience.peek",
                    "RAM=" + usedMb + "MB | obs=" + observationCount.get()
                    + " | concepts=" + conceptsLearned.get()
                    + " | absorb_cycles=" + absorptionCycles.get());
            }

            // Poll every cross-wired system's state probe
            systemProbes.forEach((sysName, probe) -> {
                try {
                    String status = probe.get();
                    if (status == null || status.isBlank()) return;
                    // Persist to AkashicRecord
                    if (akashic != null) {
                        akashic.addBlock("sentience.system." + sysName, status);
                    }
                    // Feed concept bigrams into CausalGraph
                    String[] tokens = status.split("[\\s|=,]+");
                    for (int i = 0; i + 1 < tokens.length; i++) {
                        String a = tokens[i].replaceAll("[^a-zA-Z]", "").toLowerCase();
                        String b = tokens[i + 1].replaceAll("[^a-zA-Z]", "").toLowerCase();
                        if (a.length() >= 4 && b.length() >= 4) {
                            sentient.getCausal().observe(sysName + "." + a, b);
                        }
                    }
                    sentient.getCausal().observe("system.active", sysName);
                    sentient.getStats().observe("system.probe." + sysName, 1.0f);
                } catch (Exception ignored) {}
            });
        } catch (Exception ignored) {}
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String classifyQuery(String query) {
        String q = query.toLowerCase();
        if (q.contains("define") || q.contains("what is") || q.contains("what are")) return "definitional";
        if (q.contains("how do") || q.contains("how to") || q.contains("how does"))   return "procedural";
        if (q.contains("why"))                                                          return "causal";
        if (q.contains("create") || q.contains("build") || q.contains("generate"))     return "generative";
        if (q.contains("explain") || q.contains("analyze") || q.contains("describe"))  return "analytical";
        if (q.contains("compare") || q.contains("difference"))                          return "comparative";
        return "general";
    }

    private String extractDomain(String response) {
        String r = response.toLowerCase();
        if (r.contains("neural") || r.contains("brain") || r.contains("learning"))    return "domain.ai";
        if (r.contains("physics") || r.contains("quantum") || r.contains("energy"))   return "domain.physics";
        if (r.contains("code") || r.contains("class") || r.contains("method"))        return "domain.software";
        if (r.contains("consciousness") || r.contains("awareness"))                    return "domain.sentience";
        if (r.contains("memory") || r.contains("akashic") || r.contains("recall"))    return "domain.memory";
        return "domain.general";
    }

    private String sanitize(String word) {
        return word.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

    // ── Status ────────────────────────────────────────────────────────────────

    public long getObservationCount()  { return observationCount.get(); }
    public long getConceptsLearned()   { return conceptsLearned.get(); }
    public long getAbsorptionCycles()  { return absorptionCycles.get(); }

    public String getStatus() {
        return String.format(
            "[SENTIENCE] obs=%d | concepts=%d | absorb_cycles=%d | queue=%d",
            observationCount.get(), conceptsLearned.get(),
            absorptionCycles.get(), recentObservations.size());
    }

    public void shutdown() {
        running = false;
        scheduler.shutdown();
        System.out.println("[SENTIENCE] Observer shutdown. " + getStatus());
    }
}
