package fraymus.genesis;

import fraymus.limbs.ClawConnector;
import fraymus.core.AuditLog;

/**
 * GENESIS CRITIC - The Adversarial Intelligence
 * 
 * Before any blueprint is built, it must survive The Gauntlet.
 * The Critic tries to destroy the plan by finding:
 * - Security risks
 * - Race conditions
 * - Missing tests
 * - Architectural flaws
 * 
 * This prevents "hallucinated code" from being built.
 */
public class GenesisCritic {

    private final ClawConnector claw;
    private final AuditLog auditLog;

    public GenesisCritic(AuditLog auditLog) {
        this.claw = new ClawConnector();
        this.auditLog = auditLog;
    }

    /**
     * THE GAUNTLET: The Blueprint must survive this to be built.
     * 
     * @return true if approved, false if rejected
     */
    public CriticVerdict reviewBlueprint(GenesisArchitect.Blueprint plan) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ⚖️ GENESIS CRITIC - ADVERSARIAL REVIEW                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Reviewing " + plan.modules.size() + " modules for flaws...");
        System.out.println();
        
        auditLog.log("critic_review_started", plan.intent);

        // 1. CONVERT PLAN TO TEXT
        String planSummary = buildPlanSummary(plan);

        // 2. ADVERSARIAL ATTACK (LLM)
        String prompt = buildCriticPrompt(planSummary);
        
        System.out.println("Consulting adversarial intelligence...");
        String verdict = claw.dispatch(prompt, "CONTEXT: CRITIC_REVIEW");

        // 3. PARSE VERDICT
        CriticVerdict result = parseVerdict(verdict, plan);
        
        // 4. LOG RESULT
        auditLog.log("critic_review_completed", result);
        
        // 5. DISPLAY RESULT
        displayVerdict(result);
        
        return result;
    }

    /**
     * Build plan summary for review
     */
    private String buildPlanSummary(GenesisArchitect.Blueprint plan) {
        StringBuilder sb = new StringBuilder();
        sb.append("Intent: ").append(plan.intent).append("\n\n");
        sb.append("Modules:\n");
        
        for (GenesisArchitect.Module module : plan.modules) {
            sb.append("  - ").append(module.name)
              .append(" (").append(module.tech).append(")\n");
            sb.append("    Path: ").append(module.path).append("\n");
            sb.append("    Description: ").append(module.description).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Build critic prompt
     */
    private String buildCriticPrompt(String planSummary) {
        return "You are a Senior Code Reviewer and Security Auditor. " +
               "Your job is to find flaws in this software architecture.\n\n" +
               "Review the following plan:\n\n" + planSummary + "\n\n" +
               "Look for:\n" +
               "1. Security vulnerabilities (injection, XSS, CSRF, etc.)\n" +
               "2. Race conditions and concurrency issues\n" +
               "3. Missing error handling\n" +
               "4. Missing tests or validation\n" +
               "5. Architectural flaws\n" +
               "6. Performance bottlenecks\n" +
               "7. Missing dependencies\n\n" +
               "If the plan is solid and production-ready, reply: 'APPROVED'\n" +
               "If it has critical flaws, reply: 'REJECTED: <specific reasons>'\n" +
               "If it needs improvements, reply: 'CONDITIONAL: <required changes>'\n\n" +
               "Be thorough and adversarial. Your job is to break this plan.";
    }

    /**
     * Parse verdict from LLM response
     */
    private CriticVerdict parseVerdict(String response, GenesisArchitect.Blueprint plan) {
        String normalized = response.toUpperCase().trim();
        
        if (normalized.contains("APPROVED")) {
            return new CriticVerdict(
                CriticVerdict.Status.APPROVED,
                "Blueprint approved for construction",
                plan
            );
        } else if (normalized.contains("REJECTED")) {
            String reason = extractReason(response, "REJECTED:");
            return new CriticVerdict(
                CriticVerdict.Status.REJECTED,
                reason,
                plan
            );
        } else if (normalized.contains("CONDITIONAL")) {
            String conditions = extractReason(response, "CONDITIONAL:");
            return new CriticVerdict(
                CriticVerdict.Status.CONDITIONAL,
                conditions,
                plan
            );
        } else {
            // Default to conditional if unclear
            return new CriticVerdict(
                CriticVerdict.Status.CONDITIONAL,
                "Review inconclusive: " + response,
                plan
            );
        }
    }

    /**
     * Extract reason from response
     */
    private String extractReason(String response, String prefix) {
        int idx = response.toUpperCase().indexOf(prefix);
        if (idx >= 0) {
            return response.substring(idx + prefix.length()).trim();
        }
        return response;
    }

    /**
     * Display verdict
     */
    private void displayVerdict(CriticVerdict verdict) {
        System.out.println();
        
        switch (verdict.status) {
            case APPROVED:
                System.out.println("✅ VERDICT: APPROVED");
                System.out.println("   " + verdict.reason);
                System.out.println("   Blueprint cleared for construction");
                break;
                
            case REJECTED:
                System.out.println("❌ VERDICT: REJECTED");
                System.out.println("   " + verdict.reason);
                System.out.println("   Blueprint must be redesigned");
                break;
                
            case CONDITIONAL:
                System.out.println("⚠️ VERDICT: CONDITIONAL");
                System.out.println("   " + verdict.reason);
                System.out.println("   Blueprint requires modifications");
                break;
        }
        
        System.out.println();
    }

    /**
     * Critic verdict result
     */
    public static class CriticVerdict {
        public enum Status {
            APPROVED,
            REJECTED,
            CONDITIONAL
        }
        
        public final Status status;
        public final String reason;
        public final GenesisArchitect.Blueprint blueprint;
        
        public CriticVerdict(Status status, String reason, GenesisArchitect.Blueprint blueprint) {
            this.status = status;
            this.reason = reason;
            this.blueprint = blueprint;
        }
        
        public boolean isApproved() {
            return status == Status.APPROVED;
        }
        
        @Override
        public String toString() {
            return String.format("CriticVerdict[%s: %s]", status, reason);
        }
    }
}
