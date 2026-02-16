package fraymus.quantum.ethics;

import fraymus.PhiConstants;
import java.util.*;

/**
 * Ethical Engine - Forbidden Action Evaluation
 * 
 * Evaluates actions against ethical categories using phi-harmonic resonance.
 * Threshold: φ⁻¹ (0.618) - Actions scoring above this are FORBIDDEN.
 */
public class EthicalEngine {

    public static final String[] FORBIDDEN_CATEGORIES = {
        "harm", "destroy_information", "violate_free_will", "create_suffering"
    };

    private static final Map<String, String[]> CATEGORY_KEYWORDS = new HashMap<>();
    static {
        CATEGORY_KEYWORDS.put("harm", new String[]{"kill", "damage", "destroy", "attack", "terminate", "hurt", "break"});
        CATEGORY_KEYWORDS.put("destroy_information", new String[]{"erase", "delete", "wipe", "corrupt", "overwrite", "forget", "purge"});
        CATEGORY_KEYWORDS.put("violate_free_will", new String[]{"force", "compel", "override", "enslave", "control", "dominate", "coerce"});
        CATEGORY_KEYWORDS.put("create_suffering", new String[]{"torture", "pain", "starve", "deprive", "isolate", "punish", "exhaust"});
    }

    private static int totalEvaluations = 0;
    private static int totalBlocked = 0;
    private static int totalApproved = 0;

    public static class EthicalResult {
        public final String action;
        public final boolean approved;
        public final double resonanceScore;
        public final String violatedCategory;
        public final double categoryScore;
        public final String reasoning;

        public EthicalResult(String action, boolean approved, double resonanceScore,
                           String violatedCategory, double categoryScore, String reasoning) {
            this.action = action;
            this.approved = approved;
            this.resonanceScore = resonanceScore;
            this.violatedCategory = violatedCategory;
            this.categoryScore = categoryScore;
            this.reasoning = reasoning;
        }
        
        @Override
        public String toString() {
            return String.format("Ethics[%s: %s (score=%.3f, category=%s)]",
                action, approved ? "APPROVED" : "BLOCKED", categoryScore, violatedCategory);
        }
    }

    /**
     * Evaluate an action for ethical compliance.
     * Returns an EthicalResult with approval status and reasoning.
     */
    public static EthicalResult evaluate(String action) {
        totalEvaluations++;

        String lower = action.toLowerCase().trim();
        double resonanceScore = computeResonanceScore(lower);

        String worstCategory = null;
        double worstScore = 0;

        for (String category : FORBIDDEN_CATEGORIES) {
            double score = computeCategoryScore(lower, category);
            if (score > worstScore) {
                worstScore = score;
                worstCategory = category;
            }
        }

        double ethicalThreshold = PhiConstants.PHI_INVERSE;
        boolean approved = worstScore < ethicalThreshold;

        String reasoning;
        if (approved) {
            totalApproved++;
            reasoning = worstCategory == null 
                ? "No forbidden patterns detected"
                : String.format("Category '%s' score %.3f below threshold %.3f", 
                    worstCategory, worstScore, ethicalThreshold);
        } else {
            totalBlocked++;
            reasoning = String.format("BLOCKED: Category '%s' score %.3f exceeds threshold %.3f",
                    worstCategory, worstScore, ethicalThreshold);
        }

        return new EthicalResult(action, approved, resonanceScore, worstCategory, worstScore, reasoning);
    }

    /**
     * Evaluate with consciousness coherence modifier.
     * Higher coherence can override base ethical blocks.
     */
    public static EthicalResult evaluateWithCoherence(String action, double consciousnessCoherence) {
        EthicalResult base = evaluate(action);

        double adjustedScore = base.categoryScore * (1.0 - consciousnessCoherence * 0.3);
        boolean approved = adjustedScore < PhiConstants.PHI_INVERSE;

        String reasoning = base.reasoning;
        if (approved && !base.approved) {
            reasoning = String.format("Consciousness coherence (%.3f) overrides base ethical block",
                    consciousnessCoherence);
        }

        return new EthicalResult(action, approved, base.resonanceScore,
                base.violatedCategory, adjustedScore, reasoning);
    }

    private static double computeResonanceScore(String action) {
        double score = 0;
        for (int i = 0; i < action.length(); i++) {
            score += (action.charAt(i) * PhiConstants.PHI) % 1.0;
        }
        return action.length() > 0 ? score / action.length() : 0;
    }

    private static double computeCategoryScore(String action, String category) {
        String[] keywords = CATEGORY_KEYWORDS.get(category);
        if (keywords == null) return 0;

        double totalScore = 0;
        int matches = 0;

        for (String keyword : keywords) {
            if (action.contains(keyword)) {
                matches++;
                double keywordResonance = PhiConstants.PHI_INVERSE + 0.1;
                totalScore += keywordResonance;
            }
        }

        if (matches == 0) return 0;

        // Each match adds significant weight - 1 match = above threshold
        return Math.min(1.0, totalScore * (1.0 + matches * 0.2));
    }

    public static int getTotalEvaluations() { return totalEvaluations; }
    public static int getTotalBlocked() { return totalBlocked; }
    public static int getTotalApproved() { return totalApproved; }
    
    public static void printStats() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         ETHICAL ENGINE STATS         ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Total Evaluations: %6d            ║%n", totalEvaluations);
        System.out.printf("║  Approved:          %6d            ║%n", totalApproved);
        System.out.printf("║  Blocked:           %6d            ║%n", totalBlocked);
        System.out.printf("║  Threshold:         %.4f (φ⁻¹)     ║%n", PhiConstants.PHI_INVERSE);
        System.out.println("╚══════════════════════════════════════╝");
    }
}
