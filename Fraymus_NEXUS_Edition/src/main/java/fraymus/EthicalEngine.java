package fraymus;

import java.util.*;

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
    }

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
            reasoning = String.format("Action resonance %.4f within ethical bounds (threshold %.4f)",
                    resonanceScore, ethicalThreshold);
        } else {
            totalBlocked++;
            reasoning = String.format("Action violates '%s' (score %.4f > threshold %.4f)",
                    worstCategory, worstScore, ethicalThreshold);
        }

        return new EthicalResult(action, approved, resonanceScore, worstCategory, worstScore, reasoning);
    }

    public static EthicalResult evaluateForEntity(PhiNode node, String action) {
        EthicalResult base = evaluate(action);

        double consciousnessModifier = node.consciousness.getCoherence();
        double adjustedScore = base.categoryScore * (1.0 - consciousnessModifier * 0.3);

        boolean approved = adjustedScore < PhiConstants.PHI_INVERSE;

        String reasoning = base.reasoning;
        if (approved && !base.approved) {
            reasoning = String.format("Entity %s consciousness coherence (%.3f) overrides base ethical block",
                    node.name, consciousnessModifier);
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
                double keywordResonance = 1.0 / (1.0 + Math.abs(keyword.length() * PhiConstants.PHI_INVERSE - action.length() * 0.1));
                totalScore += keywordResonance;
            }
        }

        if (matches == 0) return 0;

        double proximity = (double) matches / keywords.length;
        return totalScore * proximity * PhiConstants.PHI;
    }

    public static int getTotalEvaluations() { return totalEvaluations; }
    public static int getTotalBlocked() { return totalBlocked; }
    public static int getTotalApproved() { return totalApproved; }
}
