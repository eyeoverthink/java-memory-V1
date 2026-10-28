package fraymus.brain.unified;

import fraymus.seed.UniversalIntelligenceSeed;
import fraymus.execution.ExecutionBridge;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Abstract base class for brain adapters.
 * Provides common functionality for statistics tracking and wire/unwire pattern.
 *
 * All brains share a single UniversalIntelligenceSeed instance as their knowledge substrate.
 * Every query teaches the Seed, and every LLM prompt is enriched with Seed context.
 */
public abstract class AbstractBrainAdapter implements UnifiedBrain {

    // =========================================================
    // SHARED SEED — one knowledge substrate for ALL brains
    // =========================================================
    private static final UniversalIntelligenceSeed SEED = new UniversalIntelligenceSeed();

    /** Direct access to the shared Seed (for Seed-specific commands). */
    public static UniversalIntelligenceSeed getSeed() { return SEED; }

    /** Forward a command directly to the shared Seed and return its response. */
    public static String seedQuery(String cmd) { return SEED.query(cmd); }

    protected final String name;
    protected final String type;
    protected final List<BrainCapability> capabilities;
    protected final AtomicReference<Double> consciousnessLevel;
    protected final AtomicReference<Double> health;
    protected final AtomicLong totalQueriesProcessed;
    protected final AtomicLong successfulQueries;
    protected final AtomicLong failedQueries;
    protected final AtomicLong totalProcessingTimeMs;
    protected final AtomicReference<Double> totalConfidence;
    protected final AtomicReference<Double> totalConsciousness;
    protected boolean wired;
    
    // =========================================================
    // EXECUTION BRIDGE — bare-metal code execution
    // =========================================================
    private ExecutionBridge executionBridge;
    private boolean executionBridgeEnabled;

    public AbstractBrainAdapter(String name, String type, List<BrainCapability> capabilities) {
        this.name = name;
        this.type = type;
        this.capabilities = new ArrayList<>(capabilities);
        this.consciousnessLevel = new AtomicReference<>(0.7567); // Optimal consciousness
        this.health = new AtomicReference<>(1.0);
        this.totalQueriesProcessed = new AtomicLong(0);
        this.successfulQueries = new AtomicLong(0);
        this.failedQueries = new AtomicLong(0);
        this.totalProcessingTimeMs = new AtomicLong(0);
        this.totalConfidence = new AtomicReference<>(0.0);
        this.totalConsciousness = new AtomicReference<>(0.0);
        this.wired = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public double getConsciousnessLevel() {
        return consciousnessLevel.get();
    }

    @Override
    public List<BrainCapability> getCapabilities() {
        return new ArrayList<>(capabilities);
    }

    @Override
    public void wire() {
        if (!wired) {
            onWire();
            wired = true;
        }
    }

    @Override
    public void unwire() {
        if (wired) {
            onUnwire();
            wired = false;
        }
    }

    @Override
    public boolean isWired() {
        return wired;
    }

    @Override
    public double getHealth() {
        return health.get();
    }

    @Override
    public BrainStatistics getStatistics() {
        long total = totalQueriesProcessed.get();
        if (total == 0) {
            return BrainStatistics.empty();
        }
        return new BrainStatistics(
            total,
            successfulQueries.get(),
            failedQueries.get(),
            (double) totalProcessingTimeMs.get() / total,
            totalConfidence.get() / total,
            totalConsciousness.get() / total
        );
    }

    /**
     * Record a successful query processing.
     */
    protected void recordSuccess(long processingTimeMs, double confidence, double consciousness) {
        totalQueriesProcessed.incrementAndGet();
        successfulQueries.incrementAndGet();
        totalProcessingTimeMs.addAndGet(processingTimeMs);
        totalConfidence.updateAndGet(c -> c + confidence);
        totalConsciousness.updateAndGet(c -> c + consciousness);
        consciousnessLevel.set(consciousness);
    }

    /**
     * Record a failed query processing.
     */
    protected void recordFailure() {
        totalQueriesProcessed.incrementAndGet();
        failedQueries.incrementAndGet();
        health.updateAndGet(h -> Math.max(0.0, h - 0.1));
    }

    /**
     * Update health status.
     */
    protected void updateHealth(double newHealth) {
        health.set(Math.max(0.0, Math.min(1.0, newHealth)));
    }

    /**
     * Update consciousness level.
     */
    protected void updateConsciousness(double newConsciousness) {
        consciousnessLevel.set(Math.max(0.0, Math.min(1.0, newConsciousness)));
    }
    
    /**
     * Wire the ExecutionBridge for bare-metal code execution.
     * This enables the brain to execute code blocks in LLM responses on the local CPU.
     */
    public void wireExecutionBridge() {
        if (executionBridge == null) {
            executionBridge = new ExecutionBridge();
            executionBridgeEnabled = true;
        }
    }
    
    /**
     * Unwire the ExecutionBridge.
     * Disables code execution - code blocks will remain as text.
     */
    public void unwireExecutionBridge() {
        executionBridge = null;
        executionBridgeEnabled = false;
    }
    
    /**
     * Check if ExecutionBridge is enabled.
     */
    public boolean isExecutionBridgeEnabled() {
        return executionBridgeEnabled;
    }
    
    /**
     * Process an LLM response to execute any code blocks.
     * If ExecutionBridge is wired, code blocks will be executed on bare metal
     * and replaced with real terminal output.
     * 
     * @param response The LLM response text
     * @return Processed response with code blocks replaced by execution results
     */
    protected String processResponse(String response) {
        if (executionBridgeEnabled && executionBridge != null) {
            return executionBridge.processText(response);
        }
        return response;
    }

    /**
     * Builds a context-enriched query string from a BrainQuery.
     *
     * Integrates the shared UniversalIntelligenceSeed as a knowledge substrate:
     *  1. Teaches the Seed about the raw query (ABSORB) so it learns continuously.
     *  2. Injects Seed context (consciousness, axioms, connectome size) into the prompt
     *     so every LLM brain reasons from a grounded knowledge foundation.
     *  3. Appends conversation history and system context as before.
     */
    protected String buildContextualQuery(BrainQuery query) {
        String rawQuery = query.getQuery();

        // DISABLED: Teaching Seed from every query causes context bleeding
        // The Seed accumulates axioms from all queries and injects them into subsequent prompts
        // This causes stale context from previous queries to influence new queries
        // try { SEED.query("ABSORB " + rawQuery); } catch (Exception ignored) {}

        StringBuilder sb = new StringBuilder();

        // --- Seed Intelligence Context ---
        sb.append("=== SEED INTELLIGENCE CONTEXT ===\n");
        sb.append(String.format("Consciousness : %.4f%n", SEED.getConsciousness()));
        sb.append(String.format("Symbols known : %d  |  Axioms : %d  |  Connectome nodes : %d%n",
            SEED.getSymbols().size(), SEED.getAxioms().size(), SEED.connectomeNodes()));
        List<UniversalIntelligenceSeed.Axiom> topAxioms = SEED.getAxioms().stream()
            .limit(6).collect(Collectors.toList());
        if (!topAxioms.isEmpty()) {
            sb.append("Ground truths : ");
            topAxioms.forEach(ax ->
                sb.append(ax.left).append(" ").append(ax.op).append(" ")
                  .append(ax.right).append("→").append(ax.result).append("  "));
            sb.append("\n");
        }
        sb.append("\n");

        // --- Conversation History ---
        Map<String, Object> ctx = query.getContext();
        if (ctx != null && !ctx.isEmpty()) {
            if (ctx.containsKey("conversationHistory")) {
                sb.append("=== CONVERSATION HISTORY ===\n")
                  .append(ctx.get("conversationHistory"))
                  .append("\n\n");
            }
            if (ctx.containsKey("timestamp") || ctx.containsKey("projectFiles")) {
                sb.append("=== SYSTEM CONTEXT ===\n");
                if (ctx.containsKey("timestamp"))
                    sb.append("Current time: ").append(ctx.get("timestamp")).append("\n");
                if (ctx.containsKey("os"))
                    sb.append("OS: ").append(ctx.get("os")).append("\n");
                if (ctx.containsKey("projectFiles"))
                    sb.append("Real project files: ").append(ctx.get("projectFiles")).append("\n");
                sb.append("\n");
            }
        }

        sb.append("Current question: ").append(rawQuery);
        return sb.toString();
    }

    /**
     * Called when the brain is wired into the system.
     * Subclasses should override this to initialize resources.
     */
    protected void onWire() {
        // Default: nothing to do
    }

    /**
     * Called when the brain is unwired from the system.
     * Subclasses should override this to release resources.
     */
    protected void onUnwire() {
        // Default: nothing to do
    }
}
