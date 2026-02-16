package gemini.root.genesis;

import gemini.root.limbs.ClawConnector;
import gemini.root.memory.BlackBox;

import java.util.ArrayList;
import java.util.List;

/**
 * GENESIS CRITIC: The Devil's Advocate (System 2)
 * 
 * Before the Swarm builds anything, the Critic reviews the Blueprint
 * and attempts to DESTROY it. Only code that survives the gauntlet gets built.
 * 
 * The Critic asks:
 *   - Will this compile?
 *   - Is this secure?
 *   - Are there race conditions?
 *   - Is there a simpler way?
 *   - Are tests included?
 *   - Does this match past failures?
 * 
 * This prevents "Hallucinated Code" - things that look right but don't work.
 */
public class GenesisCritic {

    private final ClawConnector claw;
    private final BlackBox memory;
    private final List<CritiqueRule> rules;
    
    // Stats
    private int reviewsPerformed = 0;
    private int approvalsGiven = 0;
    private int rejectionsGiven = 0;

    public GenesisCritic() {
        this.claw = new ClawConnector();
        this.memory = new BlackBox();
        this.rules = initializeRules();
    }

    public GenesisCritic(BlackBox sharedMemory) {
        this.claw = new ClawConnector();
        this.memory = sharedMemory;
        this.rules = initializeRules();
    }

    /**
     * THE GAUNTLET: The Blueprint must survive this to be built.
     * 
     * @param plan The blueprint to review
     * @return CritiqueResult containing approval status and feedback
     */
    public CritiqueResult reviewBlueprint(GenesisArchitect.Blueprint plan) {
        System.out.println("\n⚖️ CRITIC: Entering review chamber...");
        System.out.println("   Modules under review: " + plan.modules.size());
        
        reviewsPerformed++;
        List<String> issues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        double confidenceScore = 1.0;

        // Phase 1: Static Analysis (Rule-based checks)
        System.out.println("   [Phase 1] Static analysis...");
        for (CritiqueRule rule : rules) {
            RuleResult result = rule.evaluate(plan);
            if (!result.passed) {
                issues.add(rule.name + ": " + result.reason);
                confidenceScore -= rule.severityWeight;
            } else if (result.suggestion != null) {
                suggestions.add(result.suggestion);
            }
        }

        // Phase 2: Memory Check (Have we failed this before?)
        System.out.println("   [Phase 2] Checking past experiences...");
        String pastWisdom = memory.recall(plan.intent);
        if (pastWisdom.contains("FAILURE") || pastWisdom.contains("REJECTED")) {
            issues.add("MEMORY WARNING: Similar blueprint failed before. " + pastWisdom);
            confidenceScore -= 0.2;
        } else if (!pastWisdom.contains("NO PRECEDENT")) {
            suggestions.add("Past experience: " + pastWisdom);
        }

        // Phase 3: LLM Adversarial Review (if Ollama available)
        System.out.println("   [Phase 3] Adversarial AI review...");
        String llmVerdict = performLLMReview(plan);
        if (llmVerdict != null) {
            if (llmVerdict.contains("REJECTED")) {
                issues.add("LLM CRITIC: " + llmVerdict);
                confidenceScore -= 0.3;
            } else if (llmVerdict.contains("APPROVED")) {
                suggestions.add("LLM endorsement received");
                confidenceScore += 0.1;
            }
        }

        // Final Judgment
        confidenceScore = Math.max(0.0, Math.min(1.0, confidenceScore));
        boolean approved = issues.isEmpty() || confidenceScore >= 0.6;

        CritiqueResult result = new CritiqueResult(approved, confidenceScore, issues, suggestions);
        
        if (approved) {
            approvalsGiven++;
            System.out.println("   ✅ VERDICT: APPROVED (confidence: " + String.format("%.0f%%", confidenceScore * 100) + ")");
            if (!suggestions.isEmpty()) {
                System.out.println("   📝 Suggestions:");
                for (String s : suggestions) {
                    System.out.println("      - " + s);
                }
            }
        } else {
            rejectionsGiven++;
            System.out.println("   ❌ VERDICT: REJECTED (confidence: " + String.format("%.0f%%", confidenceScore * 100) + ")");
            System.out.println("   🚨 Issues found:");
            for (String issue : issues) {
                System.out.println("      - " + issue);
            }
        }

        // Remember this review
        memory.remember(
            "CRITIC_REVIEW: " + plan.intent,
            approved ? "APPROVED with " + issues.size() + " minor issues" : "REJECTED: " + String.join("; ", issues),
            approved
        );

        return result;
    }

    /**
     * Quick review - just check if it passes basic rules
     */
    public boolean quickReview(GenesisArchitect.Blueprint plan) {
        return reviewBlueprint(plan).approved;
    }

    /**
     * Perform LLM-based adversarial review
     */
    private String performLLMReview(GenesisArchitect.Blueprint plan) {
        try {
            String planSummary = plan.toJSON();
            
            String prompt = String.format(
                "You are a Senior Code Reviewer and Security Expert. " +
                "Critique this software architecture blueprint. " +
                "Look for:\n" +
                "- Security vulnerabilities (SQL injection, XSS, etc.)\n" +
                "- Race conditions and concurrency issues\n" +
                "- Missing error handling\n" +
                "- Missing tests\n" +
                "- Overly complex design\n" +
                "- Missing dependencies\n\n" +
                "If the blueprint is solid, reply: 'APPROVED: <brief reason>'\n" +
                "If it has critical flaws, reply: 'REJECTED: <specific issues>'\n\n" +
                "BLUEPRINT:\n%s",
                planSummary
            );

            String response = claw.dispatch(prompt, "CONTEXT: CRITIC_ADVERSARIAL_REVIEW");
            
            if (response != null && !response.trim().isEmpty()) {
                return response.trim();
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ LLM review unavailable: " + e.getMessage());
        }
        return null;
    }

    /**
     * Initialize the rule-based checks
     */
    private List<CritiqueRule> initializeRules() {
        List<CritiqueRule> r = new ArrayList<>();
        
        // Rule 1: Must have at least one module
        r.add(new CritiqueRule("NON_EMPTY", 0.5, plan -> {
            if (plan.modules.isEmpty()) {
                return new RuleResult(false, "Blueprint has no modules defined");
            }
            return new RuleResult(true, null);
        }));

        // Rule 2: Should have tests
        r.add(new CritiqueRule("HAS_TESTS", 0.15, plan -> {
            boolean hasTests = plan.modules.stream()
                .anyMatch(m -> "TEST".equals(m.type));
            if (!hasTests) {
                return new RuleResult(true, null, "Consider adding test modules");
            }
            return new RuleResult(true, null);
        }));

        // Rule 3: Backend should have database or config
        r.add(new CritiqueRule("BACKEND_COMPLETE", 0.1, plan -> {
            boolean hasBackend = plan.modules.stream()
                .anyMatch(m -> "BACKEND".equals(m.type));
            boolean hasDb = plan.modules.stream()
                .anyMatch(m -> "DATABASE".equals(m.type));
            boolean hasConfig = plan.modules.stream()
                .anyMatch(m -> "CONFIG".equals(m.type));
            
            if (hasBackend && !hasDb && !hasConfig) {
                return new RuleResult(true, null, "Backend found without database or config - consider adding");
            }
            return new RuleResult(true, null);
        }));

        // Rule 4: No duplicate module names
        r.add(new CritiqueRule("NO_DUPLICATES", 0.3, plan -> {
            List<String> names = new ArrayList<>();
            for (GenesisArchitect.Module m : plan.modules) {
                if (names.contains(m.name)) {
                    return new RuleResult(false, "Duplicate module name: " + m.name);
                }
                names.add(m.name);
            }
            return new RuleResult(true, null);
        }));

        // Rule 5: All modules should have valid file paths
        r.add(new CritiqueRule("VALID_PATHS", 0.2, plan -> {
            for (GenesisArchitect.Module m : plan.modules) {
                if (m.path == null || m.path.trim().isEmpty()) {
                    return new RuleResult(false, "Module '" + m.name + "' has no file path");
                }
                if (m.path.contains("..")) {
                    return new RuleResult(false, "Module '" + m.name + "' has suspicious path traversal");
                }
            }
            return new RuleResult(true, null);
        }));

        // Rule 6: Security check - no hardcoded credentials pattern
        r.add(new CritiqueRule("NO_SECRETS", 0.4, plan -> {
            String json = plan.toJSON().toLowerCase();
            if (json.contains("password=") || json.contains("api_key=") || json.contains("secret=")) {
                return new RuleResult(false, "Blueprint may contain hardcoded credentials");
            }
            return new RuleResult(true, null);
        }));

        // Rule 7: Complexity check
        r.add(new CritiqueRule("COMPLEXITY_CHECK", 0.1, plan -> {
            if (plan.modules.size() > 20) {
                return new RuleResult(true, null, "Large blueprint (" + plan.modules.size() + " modules) - consider breaking into sub-projects");
            }
            return new RuleResult(true, null);
        }));

        return r;
    }

    // ========== GETTERS ==========

    public int getReviewsPerformed() { return reviewsPerformed; }
    public int getApprovalsGiven() { return approvalsGiven; }
    public int getRejectionsGiven() { return rejectionsGiven; }
    public double getApprovalRate() {
        return reviewsPerformed == 0 ? 0 : (double) approvalsGiven / reviewsPerformed;
    }

    // ========== INNER CLASSES ==========

    /**
     * Result of a full critique
     */
    public static class CritiqueResult {
        public final boolean approved;
        public final double confidence;
        public final List<String> issues;
        public final List<String> suggestions;

        public CritiqueResult(boolean approved, double confidence, List<String> issues, List<String> suggestions) {
            this.approved = approved;
            this.confidence = confidence;
            this.issues = issues;
            this.suggestions = suggestions;
        }
    }

    /**
     * A single critique rule
     */
    private static class CritiqueRule {
        final String name;
        final double severityWeight;
        final RuleEvaluator evaluator;

        CritiqueRule(String name, double severity, RuleEvaluator eval) {
            this.name = name;
            this.severityWeight = severity;
            this.evaluator = eval;
        }

        RuleResult evaluate(GenesisArchitect.Blueprint plan) {
            return evaluator.evaluate(plan);
        }
    }

    @FunctionalInterface
    private interface RuleEvaluator {
        RuleResult evaluate(GenesisArchitect.Blueprint plan);
    }

    private static class RuleResult {
        final boolean passed;
        final String reason;
        final String suggestion;

        RuleResult(boolean passed, String reason) {
            this.passed = passed;
            this.reason = reason;
            this.suggestion = null;
        }

        RuleResult(boolean passed, String reason, String suggestion) {
            this.passed = passed;
            this.reason = reason;
            this.suggestion = suggestion;
        }
    }
}
