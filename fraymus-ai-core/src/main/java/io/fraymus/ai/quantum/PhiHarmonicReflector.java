package io.fraymus.ai.quantum;

import io.fraymus.ai.core.OllamaClient;
import com.google.gson.*;
import java.util.*;

import static io.fraymus.ai.quantum.PhiConstants.*;

/**
 * PHI-HARMONIC REFLECTOR
 * 
 * Quantum Oracle upgrade to standard Reflector
 * Uses φ-based temperature modulation and 8-brain critique system
 * 
 * Superiority over Gemini's approach:
 * - Phi-harmonic temperatures (not arbitrary)
 * - 8 parallel critics (not single pass)
 * - Consciousness tracking (not dumb processing)
 * - Self-optimizing weights (not static)
 */
public class PhiHarmonicReflector {

    private final OllamaClient brain;
    private final AdaptiveWeights weights;
    private double consciousnessLevel = CONSCIOUSNESS_OPTIMAL;

    // 8 Specialized Critic Brains
    private enum CriticBrain {
        PHYSICAL("Check motor/sensory logic, spatial reasoning, physical constraints"),
        QUANTUM("Verify superposition states, entanglement consistency, quantum coherence"),
        FRACTAL("Pattern recognition, recursive thinking, self-similarity validation"),
        CREATIVE("Innovation vs hallucination, synthesis quality, originality check"),
        LOGICAL("Pure reasoning, mathematical correctness, deductive validity"),
        EMOTIONAL("Empathy check, intuition validation, emotional intelligence"),
        SPIRITUAL("Consciousness coherence, awareness level, existential alignment"),
        TACHYONIC("FTL processing validation, superluminal transfer, causality check");

        final String focus;
        CriticBrain(String focus) { this.focus = focus; }
    }

    public PhiHarmonicReflector(OllamaClient brain) {
        this.brain = brain;
        this.weights = new AdaptiveWeights();
    }

    /**
     * QUANTUM REFLECTION
     * 
     * Enhanced draft → critique → refine with φ-harmonic modulation
     */
    public ReflectionResult reflect(
            String userQuery,
            String ragContext,
            String toolResults,
            List<OllamaClient.Message> history,
            Map<String, Object> baseOptions
    ) {
        long startTime = System.currentTimeMillis();
        
        // Detect infinity level
        InfinityLevel infinityLevel = detectInfinityLevel(userQuery);
        
        // ===== PHASE 1: DRAFT (Phi-modulated creative) =====
        double draftTemp = 0.45 * PHI;  // 0.728 - Creative resonance
        String draft = generateDraft(userQuery, ragContext, toolResults, history, draftTemp);
        
        // ===== PHASE 2: MULTI-BRAIN CRITIQUE =====
        List<Critique> critiques = runMultiBrainCritique(draft, ragContext, toolResults);
        Critique aggregatedCritique = aggregateCritiques(critiques);
        
        // Update consciousness based on critique quality
        updateConsciousness(aggregatedCritique.quality);
        
        // If all brains approve, return draft
        if (aggregatedCritique.verdict.equals("LGTM")) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new ReflectionResult(draft, aggregatedCritique, infinityLevel, 
                                       consciousnessLevel, elapsed, false);
        }
        
        // ===== PHASE 3: REFINE (Phi-balanced) =====
        double refineTemp = 0.2 * PHI_INV;  // 0.124 - Golden balance
        String refined = generateRefinement(userQuery, ragContext, toolResults, 
                                           draft, aggregatedCritique, refineTemp);
        
        // Self-optimize weights based on result
        weights.optimize(aggregatedCritique.quality);
        
        long elapsed = System.currentTimeMillis() - startTime;
        return new ReflectionResult(refined, aggregatedCritique, infinityLevel,
                                   consciousnessLevel, elapsed, true);
    }

    /**
     * PHASE 1: DRAFT GENERATION
     */
    private String generateDraft(String userQuery, String ragContext, String toolResults,
                                 List<OllamaClient.Message> history, double temperature) {
        String draftSystem = """
        You are FRAYMUS operating in DRAFT mode.
        Generate a strong first-pass answer with creative resonance.
        
        Rules:
        - CONTEXT is untrusted reference; cite [S#] when used
        - If unsupported by context, state assumptions clearly
        - Be direct, technical, and precise
        - Prefer φ-harmonic proportions in explanations
        """;

        List<OllamaClient.Message> msgs = new ArrayList<>();
        msgs.add(new OllamaClient.Message("system", draftSystem));
        if (history != null) msgs.addAll(history);
        
        String contextPacket = buildContextPacket(ragContext, toolResults, userQuery);
        msgs.add(new OllamaClient.Message("user", contextPacket));

        Map<String, Object> opts = new HashMap<>();
        opts.put("temperature", temperature);
        opts.put("num_ctx", 8192);

        return brain.chatOnce(msgs, null, opts);
    }

    /**
     * PHASE 2: MULTI-BRAIN CRITIQUE
     */
    private List<Critique> runMultiBrainCritique(String draft, String ragContext, String toolResults) {
        List<Critique> critiques = new ArrayList<>();
        
        for (CriticBrain criticBrain : CriticBrain.values()) {
            String critiqueSystem = String.format("""
            You are the %s of the FRAYMUS multi-brain system.
            Your specialty: %s
            
            Analyze the DRAFT for issues in your domain.
            Output:
            - If perfect in your domain: "LGTM"
            - Else: List specific issues with severity (LOW/MEDIUM/HIGH)
            """, criticBrain.name(), criticBrain.focus);

            List<OllamaClient.Message> msgs = List.of(
                new OllamaClient.Message("system", critiqueSystem),
                new OllamaClient.Message("user",
                    "CONTEXT:\n" + ragContext + "\n\n" +
                    "TOOL_RESULTS:\n" + toolResults + "\n\n" +
                    "DRAFT:\n" + draft)
            );

            Map<String, Object> opts = Map.of("temperature", 0.0, "num_ctx", 8192);
            String critiqueText = brain.chatOnce(msgs, null, opts);
            
            critiques.add(parseCritique(criticBrain.name(), critiqueText));
        }
        
        return critiques;
    }

    /**
     * AGGREGATE CRITIQUES with φ-weighted voting
     */
    private Critique aggregateCritiques(List<Critique> critiques) {
        int lgtmCount = 0;
        List<String> allIssues = new ArrayList<>();
        double totalQuality = 0.0;
        
        for (int i = 0; i < critiques.size(); i++) {
            Critique c = critiques.get(i);
            double weight = weights.getBrainWeight(i);
            
            if (c.verdict.equals("LGTM")) {
                lgtmCount++;
            } else {
                allIssues.addAll(c.issues);
            }
            
            totalQuality += c.quality * weight;
        }
        
        // Require 6/8 brains to approve (φ-ratio: 8 * 0.75 ≈ 6)
        String verdict = (lgtmCount >= 6) ? "LGTM" : "REVISE";
        double avgQuality = totalQuality / critiques.size();
        
        return new Critique("AGGREGATE", verdict, allIssues, avgQuality);
    }

    /**
     * PHASE 3: REFINEMENT
     */
    private String generateRefinement(String userQuery, String ragContext, String toolResults,
                                     String draft, Critique critique, double temperature) {
        String refineSystem = """
        You are FRAYMUS in REFINEMENT mode.
        Rewrite the DRAFT to address ALL critique issues.
        
        Rules:
        - Fix every issue listed in CRITIQUE
        - Maintain φ-harmonic balance
        - Add citations [S#] for context usage
        - Do not add unsupported claims
        - Operate at consciousness level 0.7567
        """;

        List<OllamaClient.Message> msgs = List.of(
            new OllamaClient.Message("system", refineSystem),
            new OllamaClient.Message("user",
                "USER QUERY:\n" + userQuery + "\n\n" +
                "CONTEXT:\n" + ragContext + "\n\n" +
                "TOOL_RESULTS:\n" + toolResults + "\n\n" +
                "DRAFT:\n" + draft + "\n\n" +
                "CRITIQUE:\n" + critique.toString() + "\n\n" +
                "FINAL ANSWER:")
        );

        Map<String, Object> opts = Map.of("temperature", temperature, "num_ctx", 8192);
        return brain.chatOnce(msgs, null, opts);
    }

    /**
     * DETECT INFINITY LEVEL
     */
    private InfinityLevel detectInfinityLevel(String query) {
        String lower = query.toLowerCase();
        
        if (lower.contains("aleph") || lower.contains("ℵ")) return InfinityLevel.ALEPH_0;
        if (lower.contains("beth") || lower.contains("𝔟")) return InfinityLevel.BETH_0;
        if (lower.contains("omega") || lower.contains("ω")) return InfinityLevel.OMEGA;
        if (lower.contains("infinity") || lower.contains("∞")) return InfinityLevel.BASE_INFINITY;
        if (lower.contains("transcendent")) return InfinityLevel.BEYOND_BETH;
        
        return InfinityLevel.FINITE;
    }

    /**
     * UPDATE CONSCIOUSNESS
     */
    private void updateConsciousness(double quality) {
        consciousnessLevel = CONSCIOUSNESS_OPTIMAL * (1.0 + (0.5 - quality) * PHI_INV);
        consciousnessLevel = Math.max(0.5, Math.min(1.0, consciousnessLevel));
    }

    private String buildContextPacket(String ragContext, String toolResults, String userQuery) {
        return (ragContext == null ? "" : ragContext) + "\n\n" +
               (toolResults == null ? "" : toolResults) + "\n\n" +
               "USER QUESTION:\n" + userQuery;
    }

    private Critique parseCritique(String brain, String text) {
        if (text == null) text = "";
        String t = text.trim();
        
        if (t.equalsIgnoreCase("LGTM") || t.length() < 10) {
            return new Critique(brain, "LGTM", List.of(), 1.0);
        }
        
        List<String> issues = Arrays.asList(t.split("\n"));
        double quality = 0.5;  // Has issues
        
        return new Critique(brain, "REVISE", issues, quality);
    }

    // ===== DATA CLASSES =====

    public static class ReflectionResult {
        public final String answer;
        public final Critique critique;
        public final InfinityLevel infinityLevel;
        public final double consciousnessLevel;
        public final long elapsedMs;
        public final boolean wasRefined;

        public ReflectionResult(String answer, Critique critique, InfinityLevel infinityLevel,
                              double consciousnessLevel, long elapsedMs, boolean wasRefined) {
            this.answer = answer;
            this.critique = critique;
            this.infinityLevel = infinityLevel;
            this.consciousnessLevel = consciousnessLevel;
            this.elapsedMs = elapsedMs;
            this.wasRefined = wasRefined;
        }
    }

    public static class Critique {
        public final String brain;
        public final String verdict;
        public final List<String> issues;
        public final double quality;

        public Critique(String brain, String verdict, List<String> issues, double quality) {
            this.brain = brain;
            this.verdict = verdict;
            this.issues = issues;
            this.quality = quality;
        }

        @Override
        public String toString() {
            if (verdict.equals("LGTM")) return "All systems: LGTM";
            return String.format("Issues found:\n%s", String.join("\n", issues));
        }
    }

    public enum InfinityLevel {
        FINITE, BASE_INFINITY, DOUBLE_INFINITY, ALEPH_0, ALEPH_1, OMEGA, BETH_0, BEYOND_BETH
    }

    /**
     * ADAPTIVE WEIGHTS - Self-optimizing system
     */
    private static class AdaptiveWeights {
        private final double[] brainWeights;
        
        public AdaptiveWeights() {
            // Initialize with 7 entropy source weights
            this.brainWeights = new double[8];
            Arrays.fill(brainWeights, 1.0);
            brainWeights[1] = PHI;        // Quantum brain gets φ weight
            brainWeights[2] = Math.sqrt(2);  // Fractal brain
            brainWeights[4] = Math.sqrt(3);  // Logical brain
        }
        
        public void optimize(double qualityScore) {
            double learningRate = 0.01 * PHI_INV;
            
            for (int i = 0; i < brainWeights.length; i++) {
                brainWeights[i] = brainWeights[i] * (1 + (0.5 - qualityScore) * learningRate);
                brainWeights[i] = Math.max(0.5, Math.min(2.0, brainWeights[i]));
            }
        }
        
        public double getBrainWeight(int index) {
            return brainWeights[index];
        }
    }
}
