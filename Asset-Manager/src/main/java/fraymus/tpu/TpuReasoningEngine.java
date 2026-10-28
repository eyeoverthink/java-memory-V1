package fraymus.tpu;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TPU Reasoning Engine
 *
 * Digital, inference-focused reasoning lane with lightweight recursive and
 * counterfactual branching primitives. This is a local software simulation
 * that exposes a bare-metal style operator surface for orchestration.
 */
public final class TpuReasoningEngine {
    private static final long BASE_SYNAPSES = 1_500_000L;
    private static final int MAX_DEPTH = 12;

    private final AtomicLong synapses = new AtomicLong(BASE_SYNAPSES);
    private final AtomicLong inferences = new AtomicLong();
    private final AtomicLong recursiveRuns = new AtomicLong();
    private final AtomicLong selfCreations = new AtomicLong();
    private volatile Instant lastMutationAt = Instant.now();

    public String status() {
        return """
            TPU REASONING ENGINE
            mode: digital bare-metal inference lane
            profile: post-training recursive reasoner
            synapses: %d
            inferences: %d
            recursiveRuns: %d
            selfCreations: %d
            counterfactualBranches: active
            lastMutationAt: %s
            """.formatted(
            synapses.get(),
            inferences.get(),
            recursiveRuns.get(),
            selfCreations.get(),
            lastMutationAt
        ).stripIndent().trim();
    }

    public String infer(String prompt) {
        String normalized = normalize(prompt);
        if (normalized.isBlank()) {
            return "Usage: tpu infer <prompt>";
        }
        long run = inferences.incrementAndGet();
        long synapseGain = Math.max(1, Math.min(64, normalized.length() / 8));
        long totalSynapses = synapses.addAndGet(synapseGain);
        lastMutationAt = Instant.now();

        return """
            TPU INFERENCE COMPLETE
            run: %d
            prompt: %s
            digest: %s
            counterfactual: %s
            synapseGain: +%d
            synapses: %d
            """.formatted(
            run,
            normalized,
            shortDigest(normalized),
            counterfactualHint(normalized),
            synapseGain,
            totalSynapses
        ).stripIndent().trim();
    }

    public String reason(String prompt, int depth) {
        String normalized = normalize(prompt);
        if (normalized.isBlank()) {
            return "Usage: tpu reason [depth] <prompt>";
        }
        int boundedDepth = Math.max(1, Math.min(MAX_DEPTH, depth));
        long run = recursiveRuns.incrementAndGet();

        String state = normalized;
        StringBuilder chain = new StringBuilder();
        for (int i = 1; i <= boundedDepth; i++) {
            state = shortDigest(state + "::" + i + "::" + normalized);
            chain.append("  ").append(i).append(". ").append(state).append('\n');
        }

        long synapseGain = 4L * boundedDepth;
        long totalSynapses = synapses.addAndGet(synapseGain);
        lastMutationAt = Instant.now();

        return """
            TPU RECURSIVE REASONING COMPLETE
            run: %d
            depth: %d
            prompt: %s
            chain:
            %s
            synapseGain: +%d
            synapses: %d
            """.formatted(
            run,
            boundedDepth,
            normalized,
            chain.toString().trim(),
            synapseGain,
            totalSynapses
        ).stripIndent().trim();
    }

    public String addSynapses(long count) {
        if (count <= 0) {
            return "Usage: tpu synapse <positive-count>";
        }
        long updated = synapses.addAndGet(count);
        lastMutationAt = Instant.now();
        return """
            TPU SYNAPSE EXPANSION
            added: %d
            synapses: %d
            """.formatted(count, updated).stripIndent().trim();
    }

    public String selfCreate(String objective) {
        String normalized = normalize(objective);
        if (normalized.isBlank()) {
            return "Usage: tpu selfcreate <objective>";
        }
        long generation = selfCreations.incrementAndGet();
        long gain = 128L + Math.abs(normalized.hashCode() % 1024L);
        long updated = synapses.addAndGet(gain);
        lastMutationAt = Instant.now();

        return """
            TPU SELF-CREATION COMPLETE
            generation: %d
            objective: %s
            blueprint: %s
            synapseGain: +%d
            synapses: %d
            """.formatted(
            generation,
            normalized,
            shortDigest("selfcreate::" + normalized + "::" + generation),
            gain,
            updated
        ).stripIndent().trim();
    }

    public String reset() {
        inferences.set(0);
        recursiveRuns.set(0);
        selfCreations.set(0);
        synapses.set(BASE_SYNAPSES);
        lastMutationAt = Instant.now();
        return "TPU RESET COMPLETE\nsynapses: " + BASE_SYNAPSES;
    }

    private static String counterfactualHint(String prompt) {
        String lowered = prompt.toLowerCase(Locale.ROOT);
        if (lowered.contains("safety") || lowered.contains("secure")) {
            return "safety-first branch favored";
        }
        if (lowered.contains("speed") || lowered.contains("latency")) {
            return "latency-first branch favored";
        }
        return "balanced branch favored";
    }

    private static String normalize(String text) {
        return text == null ? "" : text.trim().replaceAll("\\s+", " ");
    }

    private static String shortDigest(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(String.format(Locale.ROOT, "%02x", b));
            }
            return hex.substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toUnsignedString(value.hashCode(), 16);
        }
    }
}

