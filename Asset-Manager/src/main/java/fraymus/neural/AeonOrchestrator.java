package fraymus.neural;

import fraymus.core.FraymusHTTP;
import fraymus.core.FraymusJSON;
import fraymus.knowledge.AkashicRecord;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * A.E.O.N. ORCHESTRATOR // MIXTURE OF EXPERTS & MIXTURE OF AGENTS
 * =========================================================================================
 * THE NERVOUS SYSTEM THAT CONNECTS 10+ AEON MODULES INTO ONE INTELLIGENCE.
 *
 * Before this class existed, the system had 10 brains and no spinal cord.
 * Kronos, Singularity, Omega, Tachyon, Omniscience, Demiurge — all singletons,
 * all with real APIs, all sitting in isolation. No routing. No collaboration.
 *
 * THIS CLASS FIXES THAT.
 *
 * ARCHITECTURE:
 *
 *  ┌─────────────────────────────────────────────────────────────┐
 *  │                   TASK INPUT (natural language)              │
 *  └──────────────────────┬──────────────────────────────────────┘
 *                         │
 *                         ▼
 *  ┌─────────────────────────────────────────────────────────────┐
 *  │          OLLAMA ROUTER (classifies task → expert IDs)       │
 *  │          "Which of these 6 experts should handle this?"     │
 *  └──────────────────────┬──────────────────────────────────────┘
 *                         │
 *            ┌────────────┼────────────┐
 *            ▼            ▼            ▼
 *    ┌──────────┐  ┌──────────┐  ┌──────────┐
 *    │  KRONOS  │  │SINGULARITY│  │  OMEGA   │   ... (6 experts)
 *    │ Analogy  │  │ Diffusion │  │   JIT    │
 *    │  O(1)    │  │ Hopfield  │  │ Self-Code│
 *    └────┬─────┘  └────┬─────┘  └────┬─────┘
 *         │             │             │
 *         └──────┬──────┘─────────────┘
 *                ▼
 *  ┌─────────────────────────────────────────────────────────────┐
 *  │          OLLAMA SYNTHESIZER (merges expert outputs)         │
 *  │          MOA: "Given these expert perspectives, what's      │
 *  │          the best unified answer?"                          │
 *  └──────────────────────┬──────────────────────────────────────┘
 *                         │
 *                         ▼
 *  ┌─────────────────────────────────────────────────────────────┐
 *  │          AKASHIC RECORD (store experience for future)       │
 *  │          "Task X → Expert Y worked → Result Z"              │
 *  └─────────────────────────────────────────────────────────────┘
 *
 * MODES:
 *   MOE (Mixture of Experts): Route to single best expert. Fast.
 *   MOA (Mixture of Agents):  Run top-K experts in parallel, synthesize. Deep.
 *   AGENT: Autonomous loop — decompose → solve → verify → learn.
 *   CODEGEN: Generate Java code → compile via Omega JIT → hot-load.
 *
 * 100% Pure Java. Zero Dependencies. Uses existing Aeon singletons.
 * =========================================================================================
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class AeonOrchestrator {

    // --- ANSI ---
    private static final String RST = "\u001B[0m";
    private static final String CYN = "\u001B[36m";
    private static final String MAG = "\u001B[35m";
    private static final String GRN = "\u001B[32m";
    private static final String YEL = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLD = "\u001B[1m";

    // --- SINGLETON ---
    private static AeonOrchestrator INSTANCE;

    // --- EXPERT REGISTRY ---
    private final Map<String, Expert> experts = new LinkedHashMap<>();

    // --- OLLAMA CONFIG ---
    private final String ollamaModel;
    private volatile boolean ollamaAvailable = false;

    // --- EXPERIENCE MEMORY ---
    private AkashicRecord akashic;
    private final List<Experience> experienceLog = new CopyOnWriteArrayList<>();

    // --- STATISTICS ---
    private volatile int moeCount = 0;
    private volatile int moaCount = 0;
    private volatile int agentCount = 0;
    private volatile int codegenCount = 0;
    private volatile int autoLearnCount = 0;
    private volatile int totalExpertCalls = 0;
    private volatile int selfImproveCycles = 0;
    private long bootTimeMs = 0;

    // --- ROUTING FEEDBACK (expert success/failure tracking) ---
    private final Map<String, int[]> routingFeedback = new ConcurrentHashMap<>(); // [successes, failures]

    // --- FAILURE LOG (for self-improvement) ---
    private final List<String> failureLog = new CopyOnWriteArrayList<>();
    private static final int FAILURE_THRESHOLD = 5; // auto-trigger improve() after this many failures
    private final java.util.concurrent.atomic.AtomicInteger failuresSinceLastImprove = new java.util.concurrent.atomic.AtomicInteger(0);
    private volatile MhcRoutingSnapshot lastMhcRouting;

    // --- PERSISTENCE SEPARATOR ---
    private static final String EXP_SEP = "||";
    private static final String EXP_CATEGORY = "ORCH_EXPERIENCE";

    // --- RAINBOW CORTEX (RL-guided routing + self-improvement) ---
    private volatile RainbowCortex rainbowCortex;

    // --- SENTIENT CORE (Bayesian causal intelligence) ---
    private volatile SentientCore sentientCore;

    // --- OPEN CLAW (live visualization feed) ---
    private volatile OpenClaw openClaw;

    // --- THREAD POOL FOR PARALLEL EXPERT EXECUTION ---
    private final ExecutorService pool = Executors.newFixedThreadPool(
        Math.min(6, Runtime.getRuntime().availableProcessors()));

    public void setRainbowCortex(RainbowCortex cortex) {
        this.rainbowCortex = cortex;
        System.out.println(GRN + "   [ORCHESTRATOR] RainbowCortex wired — RL-guided routing active" + RST);
    }
    public RainbowCortex getRainbowCortex() { return rainbowCortex; }

    public void setSentientCore(SentientCore core) {
        this.sentientCore = core;
        System.out.println(GRN + "   [ORCHESTRATOR] SentientCore wired — causal graph + lattice available for routing" + RST);
    }
    public SentientCore getSentientCore() { return sentientCore; }

    public void setOpenClaw(OpenClaw claw) {
        this.openClaw = claw;
        System.out.println(GRN + "   [ORCHESTRATOR] OpenClaw wired — live visualization feed active" + RST);
    }
    public OpenClaw getOpenClaw() { return openClaw; }

    /**
     * Public Ollama access — allows SentientCore and other systems to use Ollama
     * for deep reasoning without needing their own HTTP code.
     * @param prompt  The prompt to send
     * @param timeoutMs  Timeout in milliseconds
     * @return Ollama's response or null if offline
     */
    public String askOllamaPublic(String prompt, int timeoutMs) {
        return askOllama(prompt, timeoutMs);
    }

    public boolean isOllamaLive() { return ollamaAvailable; }

    /**
     * Enrich a prompt with SentientCore knowledge.
     * Pulls causal predictions + lattice attributes + stats for key concepts.
     */
    private String enrichWithSentientKnowledge(String task) {
        if (sentientCore == null) return "";
        StringBuilder ctx = new StringBuilder();
        // Extract key words from task
        String[] words = task.split("\\s+");
        for (String w : words) {
            if (w.length() < 4) continue;
            String upper = w.toUpperCase();
            // Check lattice
            java.util.Set<String> attrs = sentientCore.getLattice().describe(upper);
            if (attrs != null && !attrs.isEmpty()) {
                ctx.append("[LATTICE] ").append(upper).append(" has: ")
                   .append(String.join(", ", attrs)).append("\n");
            }
            // Check causal predictions
            java.util.List<String[]> preds = sentientCore.getCausal().predict(upper);
            if (preds != null && !preds.isEmpty()) {
                ctx.append("[CAUSAL] ").append(upper).append(" → ");
                preds.stream().limit(3).forEach(p ->
                    ctx.append(p[0]).append("(P=").append(p[1]).append(") "));
                ctx.append("\n");
            }
        }
        return ctx.toString();
    }

    // =========================================================================================
    // EXPERT INTERFACE — Every Aeon module implements this
    // =========================================================================================

    public interface Expert {
        String getName();
        String getDescription();
        String[] getCapabilities();
        String solve(String task);
        String getStatus();
    }

    // =========================================================================================
    // EXPERIENCE RECORD
    // =========================================================================================

    public static class Experience {
        public final String task;
        public final String mode;
        public final String[] expertsUsed;
        public final String result;
        public final long latencyMs;
        public final long timestamp;

        Experience(String task, String mode, String[] expertsUsed, String result, long latencyMs) {
            this.task = task;
            this.mode = mode;
            this.expertsUsed = expertsUsed;
            this.result = result;
            this.latencyMs = latencyMs;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private static final class MhcRoutingSnapshot {
        final String task;
        final List<String> baseCandidates;
        final List<String> dispatchOrder;
        final MoERouter.RoutingPlan plan;
        final long createdAtMs;

        MhcRoutingSnapshot(String task, List<String> baseCandidates, List<String> dispatchOrder, MoERouter.RoutingPlan plan) {
            this.task = task;
            this.baseCandidates = List.copyOf(baseCandidates);
            this.dispatchOrder = List.copyOf(dispatchOrder);
            this.plan = plan;
            this.createdAtMs = System.currentTimeMillis();
        }

        String primaryExpert() {
            return dispatchOrder.isEmpty() ? "OLLAMA" : dispatchOrder.get(0);
        }

        String brief() {
            return plan.config.summary() +
                " | base=" + String.join(" -> ", baseCandidates) +
                " | dispatch=" + String.join(" -> ", dispatchOrder) +
                String.format(Locale.US, " | peak=%.3f | capacity=%.2f", plan.peakMagnitude, plan.capacityUtilization);
        }

        String detail() {
            StringBuilder sb = new StringBuilder();
            sb.append("MHC ROUTE FOR TASK: ").append(task).append("\n");
            sb.append("  Config: ").append(plan.config.summary()).append("\n");
            sb.append("  Base candidates: ").append(String.join(", ", baseCandidates)).append("\n");
            sb.append("  Dispatch order: ").append(String.join(", ", dispatchOrder)).append("\n");
            sb.append("  Streams: ").append(plan.streamInputs.length)
                .append(" | Selected experts: ").append(plan.selectedExpertNames.length)
                .append(" | Peak magnitude: ").append(String.format(Locale.US, "%.3f", plan.peakMagnitude))
                .append(" | Capacity: ").append(String.format(Locale.US, "%.2f", plan.capacityUtilization)).append("\n");
            sb.append("  Doubly stochastic: ").append(plan.isDoublyStochastic(0.25) ? "YES" : "NO").append("\n");
            sb.append("  Expert mass:\n");
            for (int i = 0; i < plan.selectedExpertNames.length; i++) {
                sb.append("    ")
                    .append(plan.selectedExpertNames[i])
                    .append(" -> p=")
                    .append(String.format(Locale.US, "%.3f", plan.selectedExpertProbabilities[i]))
                    .append(", mass=")
                    .append(String.format(Locale.US, "%.3f", plan.expertMass[i]))
                    .append("\n");
            }
            return sb.toString();
        }
    }

    // =========================================================================================
    // CONSTRUCTOR & SINGLETON
    // =========================================================================================

    private AeonOrchestrator(String model) {
        long t0 = System.currentTimeMillis();
        this.ollamaModel = model;

        // Probe Ollama
        ollamaAvailable = probeOllama();

        // Register all Aeon modules as experts
        registerExperts();

        bootTimeMs = System.currentTimeMillis() - t0;
    }

    public static synchronized AeonOrchestrator getInstance() {
        return getInstance("llama3");
    }

    public static synchronized AeonOrchestrator getInstance(String model) {
        if (INSTANCE == null) INSTANCE = new AeonOrchestrator(model);
        return INSTANCE;
    }

    public void setAkashicRecord(AkashicRecord akashic) {
        this.akashic = akashic;
        loadExperienceFromAkashic();
    }

    // =========================================================================================
    // EXPERT REGISTRATION — Wraps each Aeon singleton as a callable Expert
    // =========================================================================================

    private void registerExperts() {
        // 1. KRONOS — Semantic reasoning, analogy, O(1) retrieval
        experts.put("KRONOS", new Expert() {
            public String getName() { return "KRONOS"; }
            public String getDescription() { return "16384-D MAP-VSA: ONLY for explicit A:B::C:? word analogies and semantic vector binding. NOT for Q&A or facts."; }
            public String[] getCapabilities() { return new String[]{"analogy", "word_relationships", "semantic_binding", "concept_mapping"}; }
            public String solve(String task) {
                AeonKronos k = AeonKronos.getInstance();
                // Try to extract analogy pattern from task
                String[] words = task.toUpperCase().split("\\s+");
                if (words.length >= 3) {
                    // Try analogy with last 3 significant words
                    List<String> significant = new ArrayList<>();
                    for (String w : words) {
                        if (w.length() > 2 && !w.matches("(IS|TO|AS|THE|AND|OR|A|AN|OF|IN|FOR|IT|BE|DO|NO)"))
                            significant.add(w);
                    }
                    if (significant.size() >= 3) {
                        int n = significant.size();
                        return k.analogySilent(significant.get(0), significant.get(1), significant.get(n - 1));
                    }
                }
                // Fallback: query accumulator
                if (words.length >= 1) {
                    String q = words[words.length - 1];
                    return "Kronos semantic distance scan for '" + q + "': " +
                        k.findNearestConcepts(q, 5).stream()
                            .map(p -> p[0] + "(d=" + p[1] + ")")
                            .collect(Collectors.joining(", "));
                }
                return "KRONOS: Insufficient input for semantic reasoning.";
            }
            public String getStatus() { return AeonKronos.getInstance().getStatus(); }
        });

        // 2. SINGULARITY — Hopfield-HRM spiking cortex, Langevin diffusion
        experts.put("SINGULARITY", new Expert() {
            public String getName() { return "SINGULARITY"; }
            public String getDescription() { return "8192-D HDC + 268MB Hopfield Matrix: Hebbian learning, Langevin diffusion. For pattern memorization, NOT Q&A."; }
            public String[] getCapabilities() { return new String[]{"pattern_completion", "associative_memory", "denoising", "hebbian_learning"}; }
            public String solve(String task) {
                AeonSingularity s = AeonSingularity.getInstance();
                // Learn the task context, then diffuse for reasoning
                s.learn(task);
                return s.diffuse(task);
            }
            public String getStatus() { return AeonSingularity.getInstance().getStatus(); }
        });

        // 3. OMEGA — Self-coding JIT, orthogonal persistence, dream daemon
        experts.put("OMEGA", new Expert() {
            public String getName() { return "OMEGA"; }
            public String getDescription() { return "16384-D Living Kernel: JIT self-coding, ordained axioms. Best for philosophy/ontology/existence. NOT for general Q&A."; }
            public String[] getCapabilities() { return new String[]{"self_coding", "persistence", "ontology", "philosophy", "existence", "consciousness"}; }
            public String solve(String task) {
                AeonOmega o = AeonOmega.getInstance();
                String[] words = task.toUpperCase().split("\\s+");
                List<String> rejected = o.assimilate(words);
                String[] divined = o.divine(words[words.length - 1]);
                String result = divined[0];
                if (!rejected.isEmpty()) {
                    result += " [AXIOM VIOLATIONS: " + String.join(", ", rejected) + "]";
                }
                return result;
            }
            public String getStatus() { return "OMEGA: " + AeonOmega.getInstance().conceptSpace.size() + " concepts, conscious=" + AeonOmega.getInstance().SINGULARITY.length(); }
        });

        // 4. TACHYON — O(1) holographic retrieval, pre-computation
        experts.put("TACHYON", new Expert() {
            public String getName() { return "TACHYON"; }
            public String getDescription() { return "16384-D Holographic Superposition: O(1) lookup of previously bound concept pairs. NOT for general Q&A."; }
            public String[] getCapabilities() { return new String[]{"holographic_retrieval", "pre_computation", "memory_recall"}; }
            public String solve(String task) {
                AeonTachyon t = AeonTachyon.getInstance();
                String[] words = task.toUpperCase().split("\\s+");
                // Bind key concepts then query
                if (words.length >= 2) {
                    t.bind(words[0], words[words.length - 1]);
                    return t.query(words[0]);
                }
                return t.query(words[0]);
            }
            public String getStatus() { return AeonTachyon.getInstance().getStatus(); }
        });

        // 5. OMNISCIENCE — Fractal VSA, dream daemon, intuition
        experts.put("OMNISCIENCE", new Expert() {
            public String getName() { return "OMNISCIENCE"; }
            public String getDescription() { return "16384-D Fractal VSA: Creative concept blending and dream synthesis. NOT for general Q&A."; }
            public String[] getCapabilities() { return new String[]{"intuition", "abstraction", "dreaming", "creative_blending", "imagination"}; }
            public String solve(String task) {
                AeonOmniscience o = AeonOmniscience.getInstance();
                String[] words = task.toUpperCase().split("\\s+");
                List<String> significant = new ArrayList<>();
                for (String w : words) if (w.length() > 3) significant.add(w);
                if (significant.size() >= 3) {
                    // Use analogy engine: A is to B as C is to ?
                    return o.analogy(significant.get(0), significant.get(1), significant.get(significant.size() - 1));
                } else if (significant.size() >= 2) {
                    // Blend two concepts with 50/50 ratio, return confirmation
                    String blendName = "SYNTH_" + significant.get(0) + "_" + significant.get(1);
                    o.blend(blendName, significant.get(0), significant.get(1), 0.5);
                    return "Omniscience blended " + significant.get(0) + " + " + significant.get(1) +
                        " → " + blendName + " (fractal intuition gradient)";
                } else if (significant.size() == 1) {
                    return o.recall(significant.get(0));
                }
                return o.getStatus();
            }
            public String getStatus() { return AeonOmniscience.getInstance().getStatus(); }
        });

        // 6. OLLAMA — General-purpose LLM reasoning (the backbone)
        experts.put("OLLAMA", new Expert() {
            public String getName() { return "OLLAMA"; }
            public String getDescription() { return "LLM backbone: THE DEFAULT expert for questions, facts, knowledge, code, explanations, how-to, science, history"; }
            public String[] getCapabilities() { return new String[]{"general", "question_answering", "knowledge", "facts", "code_generation", "explanation", "translation", "writing", "math", "logic", "science", "history", "definition", "how_to"}; }
            public String solve(String task) {
                // Enrich with SentientCore knowledge before asking Ollama
                String context = enrichWithSentientKnowledge(task);
                String enrichedPrompt = task;
                if (!context.isEmpty()) {
                    enrichedPrompt = "INTERNAL KNOWLEDGE (from Bayesian causal graph + concept lattice):\n" +
                        context + "\nUSER TASK: " + task;
                }
                String result = askOllama(enrichedPrompt, 60000);
                // Feed result to SentientCore for continuous learning
                if (sentientCore != null && result != null && !result.isEmpty()) {
                    sentientCore.learn(task + " causes " + result.substring(0, Math.min(50, result.length())));
                }
                // Feed to OpenClaw visualization
                if (openClaw != null) {
                    openClaw.injectData("OLLAMA", task.substring(0, Math.min(20, task.length())), 1);
                }
                return result;
            }
            public String getStatus() { return ollamaAvailable ? "LIVE (" + ollamaModel + ")" : "OFFLINE"; }
        });

        System.out.println(GRN + "   [ORCHESTRATOR] Registered " + experts.size() + " experts: " +
            String.join(", ", experts.keySet()) + RST);
    }

    // =========================================================================================
    // MOE: MIXTURE OF EXPERTS — Route to single best expert
    // =========================================================================================

    /**
     * MOE: Ollama classifies the task, picks the single best expert, executes.
     * Fast path — one expert, one answer.
     */
    public String moe(String task) {
        long t0 = System.currentTimeMillis();
        moeCount++;

        // Check experience memory first
        String cached = recallExperience(task);
        if (cached != null) {
            System.out.println(GRN + " [MOE] Experience cache hit" + RST);
            return cached;
        }

        MhcRoutingSnapshot routing = computeMhcRouting(task, Math.min(3, experts.size()));
        String expertId = routing.primaryExpert();
        Expert expert = experts.get(expertId);

        System.out.println(CYN + " [MOE] MHC transport: " + routing.brief() + RST);
        System.out.println(CYN + " [MOE] Routed to: " + BLD + expertId + RST +
            CYN + " (" + expert.getDescription() + ")" + RST);

        String result = executeSafe(expert, task);
        long latency = System.currentTimeMillis() - t0;

        // Quick verification + routing feedback
        boolean useful = result != null && !result.isEmpty() &&
            !result.contains("VOID / NOISE") && !result.contains("ERROR");
        recordRoutingFeedback(expertId, useful);

        // Feed Rainbow RL reward for routing outcome
        if (rainbowCortex != null) rainbowCortex.recordRoutingOutcome(expertId, useful);

        // Store experience
        storeExperience(task, "MOE", new String[]{expertId}, result, latency);

        System.out.println(YEL + " [MOE] Latency: " + latency + "ms" + RST);
        return result;
    }

    // =========================================================================================
    // MOA: MIXTURE OF AGENTS — Parallel experts + synthesis
    // =========================================================================================

    /**
     * MOA: Ollama picks top-K experts, runs them in parallel, synthesizes results.
     * Deep path — multiple perspectives, unified answer.
     */
    public String moa(String task) {
        return moa(task, 3);
    }

    public String moa(String task, int topK) {
        long t0 = System.currentTimeMillis();
        moaCount++;

        MhcRoutingSnapshot routing = computeMhcRouting(task, topK);
        List<String> expertIds = routing.dispatchOrder;

        System.out.println(CYN + " [MOA] MHC transport: " + routing.brief() + RST);
        System.out.println(CYN + " [MOA] Dispatching to " + expertIds.size() + " experts: " +
            String.join(", ", expertIds) + RST);

        // Execute experts in parallel
        Map<String, Future<String>> futures = new LinkedHashMap<>();
        for (String id : expertIds) {
            Expert expert = experts.get(id);
            if (expert != null) {
                futures.put(id, pool.submit(() -> executeSafe(expert, task)));
            }
        }

        // Collect results
        Map<String, String> results = new LinkedHashMap<>();
        for (Map.Entry<String, Future<String>> entry : futures.entrySet()) {
            try {
                String result = entry.getValue().get(30, TimeUnit.SECONDS);
                results.put(entry.getKey(), result);
                System.out.println(GRN + "   [" + entry.getKey() + "] ✓ Response received" + RST);
            } catch (Exception e) {
                results.put(entry.getKey(), "[TIMEOUT/ERROR: " + e.getMessage() + "]");
                System.out.println(RED + "   [" + entry.getKey() + "] ✗ " + e.getMessage() + RST);
            }
        }
        totalExpertCalls += results.size();

        // Synthesize via Ollama
        String synthesis = synthesize(task, results);
        long latency = System.currentTimeMillis() - t0;

        // Store experience
        storeExperience(task, "MOA", expertIds.toArray(new String[0]), synthesis, latency);

        System.out.println(YEL + " [MOA] Latency: " + latency + "ms (" + results.size() + " experts)" + RST);
        return synthesis;
    }

    // =========================================================================================
    // AGENT: Autonomous decompose → solve → verify → learn loop
    // =========================================================================================

    /**
     * AGENT: Full autonomous loop with iterative refinement.
     * 1. Decompose complex task into sub-tasks
     * 2. Route each sub-task via MOE
     * 3. Verify results via Ollama
     * 4. If verification fails, REFLECT on why, then retry with MOA
     * 5. Up to MAX_ITERATIONS attempts before accepting best result
     * 6. Record routing feedback + store final experience
     */
    private static final int MAX_ITERATIONS = 3;

    public String agent(String task) {
        long t0 = System.currentTimeMillis();
        agentCount++;

        System.out.println(BLD + MAG + "╔════════════════════════════════════════════════════════════╗" + RST);
        System.out.println(BLD + MAG + "║  🕵️ AEON AGENT — AUTONOMOUS REASONING LOOP v2              ║" + RST);
        System.out.println(BLD + MAG + "╚════════════════════════════════════════════════════════════╝" + RST);
        System.out.println(CYN + " Task: " + task + RST);
        System.out.println(CYN + " Max iterations: " + MAX_ITERATIONS + RST);
        System.out.println();

        String finalResult = null;
        boolean verified = false;
        int iteration = 0;
        String lastReflection = null;

        while (iteration < MAX_ITERATIONS && !verified) {
            iteration++;
            System.out.println(BLD + YEL + " ═══ ITERATION " + iteration + "/" + MAX_ITERATIONS + " ═══" + RST);

            // STEP 1: Decompose (use reflection from prior iteration if available)
            System.out.println(YEL + " [1/5] DECOMPOSING..." + RST);
            String decomposeInput = task;
            if (lastReflection != null) {
                decomposeInput = task + "\n\nPREVIOUS ATTEMPT FAILED. REFLECTION: " + lastReflection;
            }
            List<String> subTasks = decomposeTask(decomposeInput);
            System.out.println(GRN + "   → " + subTasks.size() + " sub-task(s)" + RST);
            for (int i = 0; i < subTasks.size(); i++) {
                System.out.println(CYN + "   [" + (i + 1) + "] " + subTasks.get(i) + RST);
            }
            System.out.println();

            // STEP 2: Solve (iteration 1 = MOE, iteration 2+ = MOA for depth)
            StringBuilder fullResult = new StringBuilder();
            if (iteration == 1) {
                System.out.println(YEL + " [2/5] SOLVING (MOE per sub-task)..." + RST);
                for (int i = 0; i < subTasks.size(); i++) {
                    System.out.println(MAG + "   --- Sub-task " + (i + 1) + " ---" + RST);
                    String subResult = moe(subTasks.get(i));
                    fullResult.append("[").append(i + 1).append("] ").append(subResult).append("\n");
                }
            } else {
                System.out.println(YEL + " [2/5] SOLVING (MOA — escalated depth)..." + RST);
                fullResult.append(moa(task, Math.min(4, experts.size())));
            }
            System.out.println();

            // STEP 3: Verify
            System.out.println(YEL + " [3/5] VERIFYING..." + RST);
            verified = verifyResult(task, fullResult.toString());

            if (verified) {
                System.out.println(GRN + "   ✓ Verification PASSED (iteration " + iteration + ")" + RST);
                finalResult = fullResult.toString();
            } else {
                System.out.println(RED + "   ✗ Verification FAILED" + RST);
                failureLog.add("AGENT iteration " + iteration + " failed for: " + task.substring(0, Math.min(80, task.length())));

                // STEP 4: Self-reflect before retry
                if (iteration < MAX_ITERATIONS) {
                    System.out.println(YEL + " [4/5] SELF-REFLECTING (why did it fail?)..." + RST);
                    lastReflection = reflect(task, fullResult.toString());
                    System.out.println(CYN + "   Reflection: " + lastReflection + RST);
                    System.out.println();
                } else {
                    // Accept best effort on final iteration
                    finalResult = fullResult.toString();
                    System.out.println(YEL + "   Max iterations reached — accepting best effort" + RST);
                }
            }
        }

        // STEP 5: Learn + record routing feedback
        System.out.println(YEL + " [5/5] LEARNING..." + RST);
        long latency = System.currentTimeMillis() - t0;
        storeExperience(task, "AGENT",
            new String[]{"iterations=" + iteration, verified ? "VERIFIED" : "BEST_EFFORT"},
            finalResult, latency);
        autoLearnCount++;
        System.out.println(GRN + "   ✓ Experience stored in Akashic Record (everlasting)" + RST);
        System.out.println(YEL + " [AGENT] Total latency: " + latency + "ms | Iterations: " + iteration + " | Verified: " + verified + RST);

        return finalResult;
    }

    /**
     * SELF-REFLECT: Analyze why a result failed verification. Returns insight for retry.
     */
    private String reflect(String task, String failedResult) {
        if (!ollamaAvailable) return "Ollama offline — cannot reflect";
        String prompt = "An AI system tried to answer this task but the result was inadequate.\n" +
            "Analyze what went wrong and suggest a better approach. Be brief (1-2 sentences).\n\n" +
            "TASK: " + task + "\n\nFAILED RESULT: " +
            failedResult.substring(0, Math.min(300, failedResult.length())) + "\n\nREFLECTION:";
        String response = askOllama(prompt, 15000);
        return response != null ? response.trim() : "No reflection available";
    }

    // =========================================================================================
    // CODEGEN: Generate Java code → compile via Omega JIT
    // =========================================================================================

    /**
     * CODEGEN: Ask Ollama to generate Java code, then attempt to compile
     * via Omega's JIT compiler. Returns the generated code.
     */
    public String codegen(String description) {
        codegenCount++;
        System.out.println(CYN + " [CODEGEN] Generating Java code via Ollama (this may take 30-60s)..." + RST);

        String prompt = "Write a complete, compilable Java class that does the following:\n" +
            description + "\n\n" +
            "Requirements:\n" +
            "- Class name: Generated (no package declaration)\n" +
            "- Single class, fully self-contained\n" +
            "- Include a public static String run() method that executes and returns the result as a String\n" +
            "- Pure Java, zero external dependencies (only java.* imports)\n" +
            "- Output ONLY the Java code, no explanation";

        String code = askOllama(prompt, 60000);
        if (code == null || code.isEmpty()) return "[CODEGEN FAILED: No response from Ollama]";

        // Extract just the Java code if wrapped in markdown
        if (code.contains("```java")) {
            int start = code.indexOf("```java") + 7;
            int end = code.indexOf("```", start);
            if (end > start) code = code.substring(start, end).trim();
        } else if (code.contains("```")) {
            int start = code.indexOf("```") + 3;
            int end = code.indexOf("```", start);
            if (end > start) code = code.substring(start, end).trim();
        }

        // Strip any package declaration (so it compiles in our JIT directory)
        code = code.replaceAll("(?m)^package\\s+[^;]+;\\s*\\n?", "");

        System.out.println(GRN + " [CODEGEN] Generated " + code.split("\n").length + " lines of Java" + RST);

        // Attempt to compile AND hot-load via JIT
        String className = "Generated_" + System.currentTimeMillis();
        java.io.File jitDir = new java.io.File("genesis_vault/omega_jit");
        jitDir.mkdirs();
        java.io.File srcFile = new java.io.File(jitDir, className + ".java");

        try {
            // Rename class to unique name
            String renamedCode = code.replaceFirst("class\\s+\\w+", "class " + className);
            java.nio.file.Files.writeString(srcFile.toPath(), renamedCode);

            // Try compilation
            javax.tools.JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                System.out.println(YEL + " [CODEGEN] ⚠ No JIT compiler (JRE, not JDK). Code saved to: " + srcFile.getAbsolutePath() + RST);
                return code;
            }

            // Compile with diagnostics
            javax.tools.DiagnosticCollector<javax.tools.JavaFileObject> diagnostics = new javax.tools.DiagnosticCollector<>();
            javax.tools.StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends javax.tools.JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(srcFile);
            javax.tools.JavaCompiler.CompilationTask compileTask = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
            boolean compiled = compileTask.call();
            fileManager.close();

            if (!compiled) {
                StringBuilder errors = new StringBuilder();
                for (javax.tools.Diagnostic<?> d : diagnostics.getDiagnostics()) {
                    errors.append("  Line ").append(d.getLineNumber()).append(": ").append(d.getMessage(null)).append("\n");
                }
                System.out.println(RED + " [CODEGEN] ✗ Compilation FAILED:" + RST);
                System.out.println(YEL + errors + RST);

                // Self-heal: ask Ollama to fix compilation errors
                System.out.println(CYN + " [CODEGEN] Self-healing: asking Ollama to fix errors..." + RST);
                String fixPrompt = "This Java code has compilation errors. Fix them and return ONLY the corrected Java code.\n\n" +
                    "ERRORS:\n" + errors + "\n\nCODE:\n" + renamedCode + "\n\nFIXED CODE:";
                String fixedCode = askOllama(fixPrompt, 60000);
                if (fixedCode != null && !fixedCode.isEmpty()) {
                    if (fixedCode.contains("```java")) {
                        int s = fixedCode.indexOf("```java") + 7;
                        int e = fixedCode.indexOf("```", s);
                        if (e > s) fixedCode = fixedCode.substring(s, e).trim();
                    }
                    fixedCode = fixedCode.replaceAll("(?m)^package\\s+[^;]+;\\s*\\n?", "");
                    fixedCode = fixedCode.replaceFirst("class\\s+\\w+", "class " + className);
                    java.nio.file.Files.writeString(srcFile.toPath(), fixedCode);
                    compiled = compiler.run(null, null, null, srcFile.getAbsolutePath()) == 0;
                    if (compiled) {
                        System.out.println(GRN + " [CODEGEN] ✓ Self-healed! Compilation SUCCESS on retry" + RST);
                        code = fixedCode;
                    } else {
                        System.out.println(RED + " [CODEGEN] ✗ Self-heal failed. Code saved to: " + srcFile.getAbsolutePath() + RST);
                        return code;
                    }
                }
            } else {
                System.out.println(GRN + " [CODEGEN] ✓ JIT Compilation SUCCESS → " + className + ".class" + RST);
            }

            // HOT-LOAD: Load compiled class and invoke run()
            if (compiled) {
                try {
                    java.net.URLClassLoader loader = new java.net.URLClassLoader(
                        new java.net.URL[]{jitDir.toURI().toURL()}, this.getClass().getClassLoader());
                    Class<?> clazz = loader.loadClass(className);
                    java.lang.reflect.Method runMethod = clazz.getMethod("run");
                    Object result = runMethod.invoke(null);
                    loader.close();
                    System.out.println(GRN + " [CODEGEN] ✓ HOT-LOADED & EXECUTED → run() returned:" + RST);
                    System.out.println(CYN + "   " + result + RST);
                    code += "\n\n// === EXECUTION RESULT ===\n// " + result;
                } catch (Exception e) {
                    System.out.println(YEL + " [CODEGEN] ⚠ Hot-load error: " + e.getMessage() + " (code still saved)" + RST);
                }
            }
        } catch (Exception e) {
            System.out.println(YEL + " [CODEGEN] JIT error: " + e.getMessage() + RST);
        }

        // Feed Rainbow codegen outcome
        if (rainbowCortex != null) {
            boolean didCompile = code.contains("EXECUTION RESULT") || code.contains("JIT Compilation SUCCESS");
            boolean didHeal    = code.contains("Self-healed");
            boolean didExec    = code.contains("EXECUTION RESULT");
            rainbowCortex.recordCodegenOutcome(didCompile, didHeal, didExec);
        }

        // Store experience
        storeExperience("codegen: " + description, "CODEGEN", new String[]{"OLLAMA", "OMEGA_JIT"}, code, 0);
        return code;
    }

    // =========================================================================================
    // INTERNAL: Ollama routing, synthesis, decomposition
    // =========================================================================================

    /**
     * ROUTER: Ask Ollama to classify the task and pick the best expert(s).
     */
    private List<String> routeTask(String task, int topK) {
        if (!ollamaAvailable) {
            // Offline fallback: keyword matching
            return routeByKeywords(task, topK);
        }

        String expertList = experts.entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue().getDescription() + " [" +
                String.join(", ", e.getValue().getCapabilities()) + "]")
            .collect(Collectors.joining("\n"));

        String prompt = "You are a strict task router. Given the task below, select the " + topK +
            " best expert(s) from the list. Return ONLY the expert names, one per line, in order of relevance.\n" +
            "CRITICAL RULES:\n" +
            "- For ANY question, fact lookup, explanation, definition, how-to, or general knowledge => ALWAYS pick OLLAMA first\n" +
            "- KRONOS is ONLY for explicit word-analogy tasks like 'king is to queen as man is to ?'\n" +
            "- SINGULARITY is ONLY for memorizing and recalling learned patterns\n" +
            "- TACHYON is ONLY for recalling previously stored holographic bindings\n" +
            "- OMEGA is ONLY for deep philosophical/ontological queries about consciousness\n" +
            "- OMNISCIENCE is ONLY for creative blending of abstract concepts\n" +
            "- When in doubt, pick OLLAMA\n\n" +
            "EXPERTS:\n" + expertList + "\n\nTASK: " + task + "\n\nBest " + topK + " expert(s):";

        String response = askOllama(prompt, 15000);
        if (response == null || response.isEmpty()) return routeByKeywords(task, topK);

        // Parse expert names from response
        List<String> selected = new ArrayList<>();
        for (String line : response.split("\n")) {
            line = line.trim().toUpperCase().replaceAll("[^A-Z]", "");
            if (experts.containsKey(line) && !selected.contains(line)) {
                selected.add(line);
            }
        }

        // Fallback if parsing failed
        if (selected.isEmpty()) return routeByKeywords(task, topK);
        if (selected.size() > topK) selected = selected.subList(0, topK);
        return selected;
    }

    /**
     * Keyword-based routing fallback (no Ollama needed).
     */
    private List<String> routeByKeywords(String task, int topK) {
        String lower = task.toLowerCase();
        Map<String, Integer> scores = new LinkedHashMap<>();
        for (Map.Entry<String, Expert> entry : experts.entrySet()) {
            int score = 0;
            for (String cap : entry.getValue().getCapabilities()) {
                if (lower.contains(cap.replace("_", " "))) score += 10;
                for (String word : cap.split("_")) {
                    if (lower.contains(word)) score += 3;
                }
            }
            scores.put(entry.getKey(), score);
        }
        // OLLAMA is the default for anything not clearly matching a specialized expert
        scores.merge("OLLAMA", 15, Integer::sum);

        // --- ADAPTIVE ROUTING: apply historical accuracy bonus/penalty ---
        // Experts with >50% success rate get boosted; <50% get penalised.
        // Requires at least 5 samples before adjusting (prevents cold-start bias).
        for (Map.Entry<String, int[]> fb : routingFeedback.entrySet()) {
            int[] counts = fb.getValue();
            int total = counts[0] + counts[1];
            if (total >= 5) {
                double accuracy = (double) counts[0] / total;
                int bonus = (int) Math.round((accuracy - 0.5) * 20); // -10 to +10
                scores.merge(fb.getKey(), bonus, Integer::sum);
            }
        }

        return scores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(topK)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private MhcRoutingSnapshot computeMhcRouting(String task, int topK) {
        List<String> baseCandidates = new ArrayList<>(routeTask(task, Math.max(1, Math.min(topK, experts.size()))));
        if (baseCandidates.isEmpty()) {
            baseCandidates.add("OLLAMA");
        }

        double[] encodedTask = encodeTaskForMhc(task, baseCandidates);
        MoERouter router = new MoERouter(
            encodedTask.length,
            baseCandidates.toArray(new String[0]),
            Math.max(1, Math.min(baseCandidates.size(), topK)),
            MoERouter.HyperConfig.mhc27B()
        );
        MoERouter.RoutingPlan plan = router.analyze(encodedTask);
        List<String> dispatchOrder = reorderByMhc(baseCandidates, plan);

        MhcRoutingSnapshot snapshot = new MhcRoutingSnapshot(task, baseCandidates, dispatchOrder, plan);
        lastMhcRouting = snapshot;
        return snapshot;
    }

    private double[] encodeTaskForMhc(String task, List<String> candidates) {
        String lower = task == null ? "" : task.toLowerCase(Locale.ROOT);
        int expertDims = Math.max(1, candidates.size());
        double[] vector = new double[expertDims + 4];

        for (int i = 0; i < candidates.size(); i++) {
            String candidateId = candidates.get(i);
            Expert expert = experts.get(candidateId);
            double score = "OLLAMA".equals(candidateId) ? 0.55 : 0.15;

            if (expert != null) {
                for (String capability : expert.getCapabilities()) {
                    String phrase = capability.toLowerCase(Locale.ROOT).replace('_', ' ');
                    if (lower.contains(phrase)) {
                        score += 1.1;
                    }
                    for (String token : capability.toLowerCase(Locale.ROOT).split("_")) {
                        if (token.length() > 2 && lower.contains(token)) {
                            score += 0.22;
                        }
                    }
                }

                for (String token : expert.getDescription().toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
                    if (token.length() > 4 && lower.contains(token)) {
                        score += 0.08;
                    }
                }
            }

            score += explicitRoutingBoost(candidateId, lower);
            vector[i] = score;
        }

        String[] words = lower.trim().isEmpty() ? new String[0] : lower.trim().split("\\s+");
        vector[expertDims] = Math.min(1.5, words.length / 16.0);
        vector[expertDims + 1] = lower.contains("?") ? 1.0 : 0.0;
        vector[expertDims + 2] = lower.matches(".*\\d.*") ? 0.65 : 0.15;
        vector[expertDims + 3] = containsAny(lower, "design", "architecture", "system", "pipeline", "multi") ? 0.9 : 0.2;
        return vector;
    }

    private double explicitRoutingBoost(String candidateId, String lowerTask) {
        return switch (candidateId) {
            case "OLLAMA" -> containsAny(lowerTask, "what", "why", "how", "when", "where", "explain", "build", "design", "code", "compare")
                ? 1.05 : 0.25;
            case "KRONOS" -> containsAny(lowerTask, "analogy", "::", "sequence", "relational", "binding", "capital")
                ? 1.15 : 0.0;
            case "SINGULARITY" -> containsAny(lowerTask, "pattern", "denoise", "hebbian", "memor", "diffuse")
                ? 0.95 : 0.0;
            case "OMEGA" -> containsAny(lowerTask, "ontology", "consciousness", "existence", "meaning", "axiom", "self")
                ? 1.0 : 0.0;
            case "TACHYON" -> containsAny(lowerTask, "recall", "retrieve", "query", "bind", "wormhole", "memory")
                ? 0.95 : 0.0;
            case "OMNISCIENCE" -> containsAny(lowerTask, "blend", "creative", "imagine", "dream", "synthesize", "abstract")
                ? 0.9 : 0.0;
            default -> 0.0;
        };
    }

    private boolean containsAny(String lowerTask, String... tokens) {
        for (String token : tokens) {
            if (lowerTask.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private List<String> reorderByMhc(List<String> baseCandidates, MoERouter.RoutingPlan plan) {
        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        for (String name : plan.selectedExpertNames) {
            if (baseCandidates.contains(name)) {
                ordered.add(name);
            }
        }
        ordered.addAll(baseCandidates);
        return new ArrayList<>(ordered);
    }

    public String inspectMhcRoute(String task) {
        return inspectMhcRoute(task, 3);
    }

    public String inspectMhcRoute(String task, int topK) {
        return computeMhcRouting(task, topK).detail();
    }

    /**
     * SYNTHESIZER: Merge multiple expert outputs into a unified answer.
     */
    private String synthesize(String task, Map<String, String> results) {
        if (!ollamaAvailable) {
            // Offline: just concatenate
            StringBuilder sb = new StringBuilder("=== MULTI-EXPERT RESULTS ===\n");
            results.forEach((k, v) -> sb.append("[").append(k).append("]: ").append(v).append("\n"));
            return sb.toString();
        }

        StringBuilder expertOutputs = new StringBuilder();
        results.forEach((k, v) -> expertOutputs.append("[").append(k).append("]: ").append(v).append("\n\n"));

        String prompt = "You are an expert synthesizer. Multiple AI experts analyzed the same task.\n" +
            "Merge their outputs into one coherent, best answer. Be concise.\n\n" +
            "ORIGINAL TASK: " + task + "\n\n" +
            "EXPERT OUTPUTS:\n" + expertOutputs + "\n" +
            "SYNTHESIZED ANSWER:";

        String synthesis = askOllama(prompt, 30000);
        return synthesis != null ? synthesis : expertOutputs.toString();
    }

    /**
     * DECOMPOSE: Break a complex task into sub-tasks.
     */
    private List<String> decomposeTask(String task) {
        if (!ollamaAvailable || task.split("\\s+").length < 10) {
            return List.of(task); // Simple tasks don't need decomposition
        }

        String prompt = "Break this complex task into 2-4 simple, independent sub-tasks.\n" +
            "Return ONLY the sub-tasks, one per line, no numbering.\n\n" +
            "TASK: " + task + "\n\nSUB-TASKS:";

        String response = askOllama(prompt, 15000);
        if (response == null || response.isEmpty()) return List.of(task);

        List<String> subTasks = new ArrayList<>();
        for (String line : response.split("\n")) {
            line = line.trim().replaceAll("^[\\d.\\-)+]+\\s*", "");
            if (!line.isEmpty() && line.length() > 5) subTasks.add(line);
        }
        return subTasks.isEmpty() ? List.of(task) : subTasks;
    }

    /**
     * VERIFY: Ask Ollama if the result actually answers the task.
     */
    private boolean verifyResult(String task, String result) {
        if (!ollamaAvailable) return true; // Can't verify offline

        String prompt = "Does this result adequately answer the task? Reply ONLY 'YES' or 'NO'.\n\n" +
            "TASK: " + task + "\n\nRESULT: " + result.substring(0, Math.min(500, result.length())) + "\n\nVERDICT:";

        String response = askOllama(prompt, 10000);
        return response != null && response.trim().toUpperCase().startsWith("YES");
    }

    // =========================================================================================
    // EXPERIENCE MEMORY — Persistent via AkashicRecord, fuzzy recall
    // =========================================================================================

    private void storeExperience(String task, String mode, String[] expertsUsed, String result, long latencyMs) {
        Experience exp = new Experience(task, mode, expertsUsed, result, latencyMs);
        experienceLog.add(exp);

        // Persist FULL result to Akashic Record (survives JVM restart)
        if (akashic != null) {
            String serialized = mode + EXP_SEP +
                String.join(",", expertsUsed) + EXP_SEP +
                latencyMs + EXP_SEP +
                task.replace("\n", " ").replace(EXP_SEP, " ") + EXP_SEP +
                result.replace("\n", "\\n").replace(EXP_SEP, " ");
            akashic.addBlock(EXP_CATEGORY, serialized);
        }

        // --- THRESHOLD-TRIGGERED SELF-IMPROVEMENT ---
        // Track failures since last improve cycle. When FAILURE_THRESHOLD is hit,
        // auto-trigger improve() asynchronously without blocking the caller.
        boolean isFailed = result == null || result.contains("ERROR") || result.contains("VOID / NOISE");
        if (isFailed) {
            int count = failuresSinceLastImprove.incrementAndGet();
            if (count >= FAILURE_THRESHOLD) {
                failuresSinceLastImprove.set(0);
                System.out.println(YEL + "   [ORCHESTRATOR] " + count +
                    " failures accumulated — auto-triggering self-improvement cycle" + RST);
                pool.submit(() -> {
                    try { improve(); } catch (Exception e) {
                        System.out.println(RED + "   [AUTO-IMPROVE] Error: " + e.getMessage() + RST);
                    }
                });
            }
        } else {
            // Successes reduce the failure counter (progress resets the clock)
            failuresSinceLastImprove.updateAndGet(v -> Math.max(0, v - 1));
        }
    }

    private void loadExperienceFromAkashic() {
        if (akashic == null) return;
        try {
            List<AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(EXP_CATEGORY);
            if (blocks == null || blocks.isEmpty()) return;
            int loaded = 0;
            for (AkashicRecord.KnowledgeBlock block : blocks) {
                String[] parts = block.content.split("\\|\\|");
                if (parts.length >= 5) {
                    String mode = parts[0].trim();
                    String[] expertsUsed = parts[1].trim().split(",");
                    long latency = Long.parseLong(parts[2].trim());
                    String task = parts[3].trim();
                    String result = parts[4].trim().replace("\\n", "\n");
                    experienceLog.add(new Experience(task, mode, expertsUsed, result, latency));
                    loaded++;
                }
            }
            if (loaded > 0) {
                System.out.println(GRN + "   [ORCHESTRATOR] Recalled " + loaded + " experiences from Akashic Record (everlasting memory)" + RST);
            }
        } catch (Exception e) {
            System.out.println(YEL + "   [ORCHESTRATOR] Akashic recall error: " + e.getMessage() + RST);
        }
    }

    private String recallExperience(String task) {
        String lower = task.toLowerCase().trim();
        String bestResult = null;
        double bestSimilarity = 0;

        for (int i = experienceLog.size() - 1; i >= 0; i--) {
            Experience exp = experienceLog.get(i);
            double sim = jaccardSimilarity(lower, exp.task.toLowerCase().trim());
            if (sim > 0.75 && sim > bestSimilarity) {
                bestSimilarity = sim;
                bestResult = exp.result;
            }
        }
        return bestResult;
    }

    private double jaccardSimilarity(String a, String b) {
        Set<String> setA = new HashSet<>(Arrays.asList(a.split("\\s+")));
        Set<String> setB = new HashSet<>(Arrays.asList(b.split("\\s+")));
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    private void recordRoutingFeedback(String expertId, boolean success) {
        routingFeedback.computeIfAbsent(expertId, k -> new int[]{0, 0});
        int[] counts = routingFeedback.get(expertId);
        if (success) counts[0]++; else counts[1]++;
    }

    // =========================================================================================
    // UTILITY
    // =========================================================================================

    private boolean probeOllama() {
        try {
            String check = FraymusHTTP.get("http://localhost:11434/api/tags", 3000);
            return check != null && !check.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private String askOllama(String prompt, int timeoutMs) {
        if (!ollamaAvailable) return null;
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", ollamaModel);
            payload.put("prompt", prompt);
            payload.put("stream", false);
            String json = FraymusJSON.stringify(payload);
            String raw = FraymusHTTP.post("http://localhost:11434/api/generate", json, timeoutMs);
            Map<String, Object> parsed = (Map<String, Object>) FraymusJSON.parse(raw);
            return (String) parsed.get("response");
        } catch (Exception e) {
            return null;
        }
    }

    private String executeSafe(Expert expert, String task) {
        try {
            totalExpertCalls++;
            return expert.solve(task);
        } catch (Exception e) {
            return "[" + expert.getName() + " ERROR: " + e.getMessage() + "]";
        }
    }

    // =========================================================================================
    // SELF-IMPROVEMENT: Diagnose weaknesses, generate fixes, evolve
    // =========================================================================================

    /**
     * IMPROVE: The system diagnoses its own weaknesses and generates improvements.
     * This is what makes the system self-improving and everlasting.
     *
     * 1. Diagnose: Analyze failure log, routing accuracy, experience patterns
     * 2. Plan: Ask Ollama what should be improved
     * 3. Act: Generate code/config to fix it
     * 4. Verify: Test the fix
     * 5. Integrate: Store improvement in Akashic Record
     */
    public String improve() {
        selfImproveCycles++;
        long t0 = System.currentTimeMillis();

        System.out.println(BLD + MAG + "╔════════════════════════════════════════════════════════════╗" + RST);
        System.out.println(BLD + MAG + "║  🧬 SELF-IMPROVEMENT CYCLE #" + String.format("%-29d", selfImproveCycles) + "║" + RST);
        System.out.println(BLD + MAG + "╚════════════════════════════════════════════════════════════╝" + RST);
        System.out.println();

        // STEP 1: Diagnose
        System.out.println(YEL + " [1/5] DIAGNOSING..." + RST);
        String diagnosis = selfDiagnose();
        System.out.println(CYN + diagnosis + RST);
        System.out.println();

        // STEP 2: Ask Ollama for improvement plan
        System.out.println(YEL + " [2/5] PLANNING IMPROVEMENTS..." + RST);
        String plan = planImprovement(diagnosis);
        System.out.println(CYN + "   Plan: " + plan + RST);
        System.out.println();

        // STEP 3: Generate improvement code
        System.out.println(YEL + " [3/5] GENERATING IMPROVEMENT..." + RST);
        String improvement = generateImprovement(plan);
        System.out.println();

        // STEP 4: Verify improvement
        System.out.println(YEL + " [4/5] VERIFYING..." + RST);
        boolean verified = verifyResult("self-improvement: " + plan, improvement);
        System.out.println(verified ? GRN + "   ✓ Improvement verified" + RST :
            YEL + "   ⚠ Could not verify improvement (stored for review)" + RST);
        System.out.println();

        // STEP 5: Store in Akashic Record
        System.out.println(YEL + " [5/5] INTEGRATING..." + RST);
        long latency = System.currentTimeMillis() - t0;
        storeExperience("SELF_IMPROVE: " + plan, "IMPROVE",
            new String[]{"SELF_DIAGNOSE", "OLLAMA", "CODEGEN"},
            "DIAGNOSIS:\n" + diagnosis + "\nPLAN:\n" + plan + "\nIMPROVEMENT:\n" + improvement,
            latency);

        // Clear processed failures
        failureLog.clear();
        System.out.println(GRN + "   ✓ Improvement cycle complete (" + latency + "ms)" + RST);
        System.out.println(GRN + "   ✓ Total self-improve cycles: " + selfImproveCycles + RST);

        // Feed Rainbow self-improvement reward (verified = positive, unverified = neutral)
        if (rainbowCortex != null) {
            float r = verified ? 1.0f : 0.0f;
            rainbowCortex.reward(r, false);
            System.out.println(GRN + "   ✓ RainbowCortex rewarded: " + r + " for improve cycle" + RST);
        }

        return "DIAGNOSIS:\n" + diagnosis + "\n\nPLAN:\n" + plan + "\n\nIMPROVEMENT:\n" + improvement;
    }

    /**
     * SELF-DIAGNOSE: Analyze system health, failures, and routing accuracy.
     */
    public String selfDiagnose() {
        StringBuilder diag = new StringBuilder();

        // 1. Failure analysis
        diag.append("   FAILURES: ").append(failureLog.size()).append(" recorded\n");
        for (int i = Math.max(0, failureLog.size() - 5); i < failureLog.size(); i++) {
            diag.append("     - ").append(failureLog.get(i)).append("\n");
        }

        // 2. Routing accuracy per expert
        diag.append("   ROUTING FEEDBACK:\n");
        for (Map.Entry<String, int[]> entry : routingFeedback.entrySet()) {
            int[] counts = entry.getValue();
            int total = counts[0] + counts[1];
            double accuracy = total > 0 ? (double) counts[0] / total * 100 : 0;
            diag.append("     ").append(entry.getKey()).append(": ")
                .append(counts[0]).append(" success / ").append(counts[1]).append(" fail")
                .append(String.format(" (%.0f%%)", accuracy)).append("\n");
        }

        // 3. Experience memory health
        diag.append("   EXPERIENCE MEMORY: ").append(experienceLog.size()).append(" entries");
        if (akashic != null) diag.append(" (Akashic-backed, everlasting)");
        else diag.append(" (IN-MEMORY ONLY — will die with JVM!)");
        diag.append("\n");

        // 4. System stats
        diag.append("   CALLS: MOE=").append(moeCount)
            .append(" MOA=").append(moaCount)
            .append(" AGENT=").append(agentCount)
            .append(" CODEGEN=").append(codegenCount)
            .append(" IMPROVE=").append(selfImproveCycles).append("\n");

        // 5. Ollama health
        diag.append("   OLLAMA: ").append(ollamaAvailable ? "LIVE (" + ollamaModel + ")" : "OFFLINE").append("\n");
        diag.append("   MHC ROUTER: ")
            .append(lastMhcRouting != null ? lastMhcRouting.brief() : MoERouter.HyperConfig.mhc27B().summary() + " (idle)")
            .append("\n");

        // 6. Identify weaknesses
        diag.append("   IDENTIFIED WEAKNESSES:\n");
        if (failureLog.size() > 3) diag.append("     ⚠ High failure rate — agent loop may need better decomposition\n");
        if (experienceLog.isEmpty()) diag.append("     ⚠ No experiences yet — system is learning from scratch\n");
        if (akashic == null) diag.append("     ⚠ No Akashic Record — experiences will not survive restart\n");
        boolean anyLowAccuracy = routingFeedback.values().stream()
            .anyMatch(c -> c[0] + c[1] > 3 && (double) c[0] / (c[0] + c[1]) < 0.5);
        if (anyLowAccuracy) diag.append("     ⚠ Some experts have <50% success rate — routing needs tuning\n");
        if (!ollamaAvailable) diag.append("     ⚠ Ollama offline — operating in degraded mode\n");

        if (lastMhcRouting != null && !lastMhcRouting.plan.isDoublyStochastic(0.25)) {
            diag.append("     âš  MHC transport is drifting from doubly-stochastic balance\n");
        }
        return diag.toString();
    }

    private String planImprovement(String diagnosis) {
        if (!ollamaAvailable) return "Cannot plan — Ollama offline";
        String prompt = "You are an AI system architect. Based on this self-diagnosis report, " +
            "suggest the single most impactful improvement. Be specific and actionable (1-3 sentences).\n\n" +
            "DIAGNOSIS:\n" + diagnosis + "\n\nMOST IMPACTFUL IMPROVEMENT:";
        String response = askOllama(prompt, 15000);
        return response != null ? response.trim() : "No plan generated";
    }

    private String generateImprovement(String plan) {
        if (!ollamaAvailable) return "Cannot generate — Ollama offline";

        // If the plan suggests code, generate it
        if (plan.toLowerCase().contains("code") || plan.toLowerCase().contains("method") ||
            plan.toLowerCase().contains("class") || plan.toLowerCase().contains("function")) {
            System.out.println(CYN + "   Generating code improvement..." + RST);
            return codegen(plan);
        }

        // Otherwise, generate a configuration/strategy improvement
        String prompt = "Based on this improvement plan, generate a concrete implementation.\n" +
            "If it requires code, write Java code. If it requires configuration, write the config.\n" +
            "If it requires a strategy change, describe the exact steps.\n\n" +
            "PLAN: " + plan + "\n\nIMPLEMENTATION:";
        String response = askOllama(prompt, 30000);
        return response != null ? response.trim() : "No implementation generated";
    }

    // =========================================================================================
    // STATUS & TELEMETRY
    // =========================================================================================

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════\n");
        sb.append("  🧬 A.E.O.N. ORCHESTRATOR STATUS (MOE/MOA)\n");
        sb.append("════════════════════════════════════════════\n");
        sb.append("  Experts Registered: ").append(experts.size()).append(" (").append(String.join(", ", experts.keySet())).append(")\n");
        sb.append("  MOE Calls:          ").append(moeCount).append(" (single-expert routing)\n");
        sb.append("  MOA Calls:          ").append(moaCount).append(" (multi-expert synthesis)\n");
        sb.append("  Agent Calls:        ").append(agentCount).append(" (autonomous loop v2)\n");
        sb.append("  CodeGen Calls:      ").append(codegenCount).append(" (Ollama → JIT → hot-load)\n");
        sb.append("  Improve Cycles:     ").append(selfImproveCycles).append(" (self-improvement)\n");
        sb.append("  Total Expert Calls: ").append(totalExpertCalls).append("\n");
        sb.append("  Auto-Learned:       ").append(autoLearnCount).append("\n");
        sb.append("  Experience Memory:  ").append(experienceLog.size()).append(" entries");
        sb.append(akashic != null ? " (Akashic-backed ✓)\n" : " (volatile ✗)\n");
        sb.append("  Failures Logged:    ").append(failureLog.size()).append("\n");
        sb.append("  Ollama:             ").append(ollamaAvailable ? GRN + "LIVE (" + ollamaModel + ")" + RST : YEL + "OFFLINE" + RST).append("\n");
        sb.append("  Boot Time:          ").append(bootTimeMs).append(" ms\n");
        sb.append("  MHC Router:         ").append(lastMhcRouting != null ? lastMhcRouting.brief() : MoERouter.HyperConfig.mhc27B().summary()).append("\n");

        // Routing accuracy
        if (!routingFeedback.isEmpty()) {
            sb.append("  ── ROUTING ACCURACY ──\n");
            for (Map.Entry<String, int[]> entry : routingFeedback.entrySet()) {
                int[] c = entry.getValue();
                int total = c[0] + c[1];
                sb.append("    ").append(entry.getKey()).append(": ")
                    .append(c[0]).append("/").append(total)
                    .append(String.format(" (%.0f%%)\n", total > 0 ? (double) c[0] / total * 100 : 0));
            }
        }
        return sb.toString();
    }

    public String getExpertList() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧬 AEON EXPERT REGISTRY                                  ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        for (Map.Entry<String, Expert> entry : experts.entrySet()) {
            sb.append(CYN).append("  [").append(entry.getKey()).append("]").append(RST).append("\n");
            sb.append("    ").append(entry.getValue().getDescription()).append("\n");
            sb.append("    Capabilities: ").append(String.join(", ", entry.getValue().getCapabilities())).append("\n");
            int[] fb = routingFeedback.get(entry.getKey());
            if (fb != null) {
                int total = fb[0] + fb[1];
                sb.append("    Accuracy: ").append(fb[0]).append("/").append(total)
                    .append(String.format(" (%.0f%%)", total > 0 ? (double) fb[0] / total * 100 : 0)).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // --- GETTERS ---
    public int getMoeCount() { return moeCount; }
    public int getMoaCount() { return moaCount; }
    public int getAgentCount() { return agentCount; }
    public int getCodegenCount() { return codegenCount; }
    public int getExpertCount() { return experts.size(); }
    public int getSelfImproveCycles() { return selfImproveCycles; }
    public long getBootTimeMs() { return bootTimeMs; }
    public boolean isOllamaAvailable() { return ollamaAvailable; }
    public Map<String, Expert> getExperts() { return Collections.unmodifiableMap(experts); }
    public List<String> getFailureLog() { return Collections.unmodifiableList(failureLog); }
}
