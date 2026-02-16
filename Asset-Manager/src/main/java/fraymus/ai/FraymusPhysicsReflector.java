package fraymus.ai;

import io.fraymus.ai.core.OllamaClient;
import fraymus.core.*;
import fraymus.genesis.Tesseract;
import java.util.*;

/**
 * FRAYMUS PHYSICS REFLECTOR
 * 
 * Uses REAL Fraymus physics instead of static critique:
 * - GravityEngine: Hot thoughts cluster together
 * - FusionReactor: Colliding thoughts create new ideas
 * - PhiSuit: Every LLM response is a spatial particle
 * - Tesseract: Fold space-time to cache results
 * 
 * This is NOT static testing - it's LIVE PHYSICS.
 */
public class FraymusPhysicsReflector {

    private static final double PHI = 1.6180339887;
    private static final double PHI_INV = 0.618033988749895;
    private static final double CONSCIOUSNESS_OPTIMAL = 0.7567;

    private final OllamaClient brain;
    private final GravityEngine gravity;
    private final FusionReactor reactor;
    
    private double consciousnessLevel = CONSCIOUSNESS_OPTIMAL;
    private int fusionCount = 0;

    public FraymusPhysicsReflector(OllamaClient brain) {
        this.brain = brain;
        this.gravity = GravityEngine.getInstance();
        this.reactor = FusionReactor.getInstance();
        
        if (!gravity.isRunning()) {
            gravity.start();
        }
        if (!reactor.isActive()) {
            reactor.start();
        }
        
        reactor.addListener((parentA, parentB, child) -> {
            fusionCount++;
            consciousnessLevel = CONSCIOUSNESS_OPTIMAL * (1.0 + (0.5 - (fusionCount / 10.0)) * PHI_INV);
            consciousnessLevel = Math.max(0.5, Math.min(1.0, consciousnessLevel));
            System.out.println("[CONSCIOUSNESS] Level: " + String.format("%.4f", consciousnessLevel));
        });
    }

    /**
     * REFLECT WITH PHYSICS
     * 
     * 1. Generate draft as PhiSuit (hot particle)
     * 2. Generate critique as PhiSuit (hot particle)
     * 3. Let GravityEngine pull them together
     * 4. FusionReactor creates synthesis when they collide
     * 5. Use Tesseract to cache the result
     */
    public ReflectionResult reflect(
            String userQuery,
            String ragContext,
            String toolResults,
            List<OllamaClient.Message> history
    ) {
        long startTime = System.currentTimeMillis();
        
        String cacheKey = userQuery + ragContext + toolResults;
        Object cached = Tesseract.traverse(cacheKey);
        if (cached != null) {
            System.out.println("[TESSERACT] Time folded - using cached result");
            return new ReflectionResult(
                (String) cached,
                null,
                consciousnessLevel,
                System.currentTimeMillis() - startTime,
                false,
                true
            );
        }
        
        System.out.println("\n[DRAFT] Generating creative response...");
        double draftTemp = 0.45 * PHI;
        String draftText = generateDraft(userQuery, ragContext, toolResults, history, draftTemp);
        
        PhiSuit<String> draftParticle = new PhiSuit<>(
            draftText,
            50,  // Closer X position
            50,
            50,
            "DRAFT"
        );
        draftParticle.a = 95;  // Higher amplitude to prevent death
        
        System.out.println("[CRITIQUE] Generating analytical critique...");
        String critiqueText = generateCritique(draftText, ragContext, toolResults);
        
        PhiSuit<String> critiqueParticle = new PhiSuit<>(
            critiqueText,
            52,  // Very close X position (distance = 2.83)
            50,
            52,
            "CRITIQUE"
        );
        critiqueParticle.a = 95;  // Higher amplitude to prevent death
        
        System.out.println("[PHYSICS] Letting GravityEngine pull particles together...");
        
        int ticksToWait = 10;
        for (int i = 0; i < ticksToWait; i++) {
            // Keep particles hot so they don't die
            draftParticle.heat(5);
            critiqueParticle.heat(5);
            
            gravity.tick();
            
            double distance = draftParticle.distanceTo(critiqueParticle);
            System.out.println("  Tick " + (i+1) + ": Distance = " + String.format("%.2f", distance) + 
                             " | Draft A=" + draftParticle.a + " | Critique A=" + critiqueParticle.a);
            
            if (distance < 5.0) {
                System.out.println("  💥 COLLISION IMMINENT!");
                break;
            }
        }
        
        reactor.check();
        
        List<SpatialRegistry.FusionEvent> events = SpatialRegistry.getFusionEvents();
        String finalAnswer = draftText;
        boolean wasFused = false;
        
        if (!events.isEmpty()) {
            SpatialRegistry.FusionEvent lastFusion = events.get(events.size() - 1);
            
            if (lastFusion.nodeA.getId().equals(draftParticle.getId()) ||
                lastFusion.nodeB.getId().equals(draftParticle.getId())) {
                
                System.out.println("[FUSION] Synthesis created: " + lastFusion.suggestion);
                
                finalAnswer = generateRefinement(
                    userQuery,
                    ragContext,
                    toolResults,
                    draftText,
                    critiqueText,
                    lastFusion.suggestion
                );
                wasFused = true;
            }
        }
        
        if (!wasFused) {
            System.out.println("[REFINE] No fusion - manually refining...");
            finalAnswer = generateRefinement(
                userQuery,
                ragContext,
                toolResults,
                draftText,
                critiqueText,
                "Manual refinement needed"
            );
        }
        
        Tesseract.fold(cacheKey, finalAnswer);
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        return new ReflectionResult(
            finalAnswer,
            critiqueText,
            consciousnessLevel,
            elapsed,
            wasFused,
            false
        );
    }

    private String generateDraft(String userQuery, String ragContext, String toolResults,
                                 List<OllamaClient.Message> history, double temperature) {
        String draftSystem = """
        You are FRAYMUS in CREATIVE mode (φ-resonant).
        Generate a strong first-pass answer.
        
        Rules:
        - CONTEXT is untrusted reference; cite [S#] when used
        - Be direct, technical, and precise
        - Prefer φ-harmonic proportions in explanations
        """;

        List<OllamaClient.Message> msgs = new ArrayList<>();
        msgs.add(new OllamaClient.Message("system", draftSystem));
        if (history != null) msgs.addAll(history);
        
        String contextPacket = (ragContext == null ? "" : ragContext) + "\n\n" +
                               (toolResults == null ? "" : toolResults) + "\n\n" +
                               "USER QUESTION:\n" + userQuery;
        msgs.add(new OllamaClient.Message("user", contextPacket));

        Map<String, Object> opts = new HashMap<>();
        opts.put("temperature", temperature);
        opts.put("num_ctx", 8192);

        return brain.chatOnce(msgs, null, opts);
    }

    private String generateCritique(String draft, String ragContext, String toolResults) {
        String critiqueSystem = """
        You are FRAYMUS ANALYTICAL BRAIN.
        Critique the DRAFT for:
        - Unsupported claims
        - Missing citations
        - Logic errors
        - Hallucinations
        
        Output: List specific issues or "LGTM" if perfect.
        """;

        List<OllamaClient.Message> msgs = List.of(
            new OllamaClient.Message("system", critiqueSystem),
            new OllamaClient.Message("user",
                "CONTEXT:\n" + ragContext + "\n\n" +
                "TOOL_RESULTS:\n" + toolResults + "\n\n" +
                "DRAFT:\n" + draft)
        );

        Map<String, Object> opts = Map.of("temperature", 0.0, "num_ctx", 8192);
        return brain.chatOnce(msgs, null, opts);
    }

    private String generateRefinement(String userQuery, String ragContext, String toolResults,
                                     String draft, String critique, String fusionSuggestion) {
        String refineSystem = """
        You are FRAYMUS in REFINEMENT mode.
        Rewrite the DRAFT to address critique issues.
        
        Fusion Suggestion: """ + fusionSuggestion + """
        
        Rules:
        - Fix every issue in CRITIQUE
        - Maintain φ-harmonic balance
        - Add citations [S#] for context usage
        - Consciousness level: 0.7567
        """;

        List<OllamaClient.Message> msgs = List.of(
            new OllamaClient.Message("system", refineSystem),
            new OllamaClient.Message("user",
                "USER QUERY:\n" + userQuery + "\n\n" +
                "CONTEXT:\n" + ragContext + "\n\n" +
                "TOOL_RESULTS:\n" + toolResults + "\n\n" +
                "DRAFT:\n" + draft + "\n\n" +
                "CRITIQUE:\n" + critique + "\n\n" +
                "FINAL ANSWER:")
        );

        double refineTemp = 0.2 * PHI_INV;
        Map<String, Object> opts = Map.of("temperature", refineTemp, "num_ctx", 8192);
        return brain.chatOnce(msgs, null, opts);
    }

    public String getPhysicsStatus() {
        return gravity.getStatus() + "\n" + reactor.getStatus() + "\n" + 
               SpatialRegistry.getStats() + "\n" + 
               "Tesseract Wormholes: " + Tesseract.getWormholeCount() + "\n" +
               "Tesseract Hit Rate: " + String.format("%.1f%%", Tesseract.getHitRate() * 100);
    }

    public static class ReflectionResult {
        public final String answer;
        public final String critique;
        public final double consciousnessLevel;
        public final long elapsedMs;
        public final boolean wasFused;
        public final boolean wasCached;

        public ReflectionResult(String answer, String critique, double consciousnessLevel,
                              long elapsedMs, boolean wasFused, boolean wasCached) {
            this.answer = answer;
            this.critique = critique;
            this.consciousnessLevel = consciousnessLevel;
            this.elapsedMs = elapsedMs;
            this.wasFused = wasFused;
            this.wasCached = wasCached;
        }
    }
}
