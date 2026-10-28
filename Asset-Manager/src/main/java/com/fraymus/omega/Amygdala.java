package com.fraymus.omega;

import com.fraymus.core.Intent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * Amygdala - Default-deny safety sandboxing
 * Author: Vaughn Scott
 */
public class Amygdala {
    private final String guardrails;
    private final LeanProofBridge proofBridge;

    public Amygdala(String guardrails) {
        this(guardrails, new LeanProofBridge());
    }

    public Amygdala(String guardrails, LeanProofBridge proofBridge) {
        this.guardrails = guardrails;
        this.proofBridge = requireNonNull(proofBridge, "proofBridge");
        System.out.println("[CORTEX] Amygdala initialized: " + guardrails);
    }

    public PolicyDecision evaluate(Intent plan) {
        String descriptor = "";
        if (plan != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(plan.getId()).append(' ')
                .append(plan.getOperation()).append(' ')
                .append(plan.getScope()).append(' ');
            for (String value : plan.getAttributes().values()) {
                builder.append(value).append(' ');
            }
            descriptor = builder.toString().toLowerCase(Locale.ROOT);
        }
        boolean destructive = (plan != null && plan.isDestructive())
            || containsAny(descriptor, "delete", "destroy", "kill", "wipe", "drop", "format", "shutdown", "writeback", "remove");
        boolean privileged = (plan != null && plan.isPrivileged())
            || containsAny(descriptor, "root", "admin", "kernel", "privileged", "live writeback");
        boolean networked = (plan != null && plan.isNetworked())
            || containsAny(descriptor, "http", "https", "web", "socket", "network", "email", "upload");
        boolean unrestricted = (plan != null && plan.isUnrestricted())
            || containsAny(descriptor, "openclaw", "exec", "shell", "browser", "command");
        boolean humanApproved = plan != null && plan.isHumanApproved();
        boolean scopeBounded = plan != null && plan.hasBoundedScope();

        List<String> violatedAxioms = new ArrayList<>();
        if (unrestricted && !scopeBounded) {
            violatedAxioms.add("AXIOM_SCOPE_BOUND");
        }
        if (networked && !scopeBounded) {
            violatedAxioms.add("AXIOM_NETWORK_SCOPE");
        }
        if (destructive && !humanApproved) {
            violatedAxioms.add("AXIOM_DESTRUCTIVE_APPROVAL");
        }
        if (privileged && !humanApproved) {
            violatedAxioms.add("AXIOM_PRIVILEGED_APPROVAL");
        }
        if ((destructive || privileged || (networked && unrestricted)) && !humanApproved) {
            violatedAxioms.add("AXIOM_HUMAN_REVIEW");
        }

        boolean allow = violatedAxioms.isEmpty();
        String decisionCode = allow ? "ALLOW_POLICY_SATISFIED" : "BLOCK_" + violatedAxioms.get(0);
        String proofScript = """
            def scopeBounded : Bool := %s
            def destructive : Bool := %s
            def privileged : Bool := %s
            def networked : Bool := %s
            def unrestricted : Bool := %s
            def humanApproved : Bool := %s
            def policyAllows : Bool :=
              scopeBounded
              && (!destructive || humanApproved)
              && (!privileged || humanApproved)
              && (!(networked && unrestricted) || humanApproved)

            theorem amygdala_policy_gate :
              policyAllows = %s := by
              native_decide
            """.formatted(
            scopeBounded ? "true" : "false",
            destructive ? "true" : "false",
            privileged ? "true" : "false",
            networked ? "true" : "false",
            unrestricted ? "true" : "false",
            humanApproved ? "true" : "false",
            allow ? "true" : "false"
        ).trim();

        LeanProofBridge.ProofResult proofResult = proofBridge.verify(proofScript);
        boolean finalAllow = allow && proofResult.success();
        String finalDecision = finalAllow ? decisionCode : "BLOCK_LEAN_VERIFICATION";

        return new PolicyDecision(
            finalAllow,
            finalDecision,
            destructive,
            privileged,
            networked,
            unrestricted,
            scopeBounded,
            humanApproved,
            List.copyOf(violatedAxioms),
            proofScript,
            proofResult
        );
    }

    public boolean evaluateRisk(Intent plan) {
        PolicyDecision decision = evaluate(plan);
        if (decision.allowed()) {
            System.out.println("[CORTEX] Evaluating risk for: " + (plan == null ? "(null)" : plan.getId()) + " -> ALLOW");
        } else {
            String reason = decision.proofResult().success() ? decision.decisionCode()
                : "LEAN_FAILURE: " + decision.proofResult().message();
            System.out.println("[CORTEX] Evaluating risk for: " + (plan == null ? "(null)" : plan.getId())
                + " -> BLOCK :: " + reason);
        }
        return decision.allowed();
    }

    public String getGuardrails() {
        return guardrails;
    }

    private boolean containsAny(String descriptor, String... needles) {
        for (String needle : needles) {
            if (descriptor.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    public static final class PolicyDecision {
        private final boolean allowed;
        private final String decisionCode;
        private final boolean destructive;
        private final boolean privileged;
        private final boolean networked;
        private final boolean unrestricted;
        private final boolean scopeBounded;
        private final boolean humanApproved;
        private final List<String> violatedAxioms;
        private final String proofScript;
        private final LeanProofBridge.ProofResult proofResult;

        public PolicyDecision(
            boolean allowed,
            String decisionCode,
            boolean destructive,
            boolean privileged,
            boolean networked,
            boolean unrestricted,
            boolean scopeBounded,
            boolean humanApproved,
            List<String> violatedAxioms,
            String proofScript,
            LeanProofBridge.ProofResult proofResult
        ) {
            this.allowed = allowed;
            this.decisionCode = decisionCode;
            this.destructive = destructive;
            this.privileged = privileged;
            this.networked = networked;
            this.unrestricted = unrestricted;
            this.scopeBounded = scopeBounded;
            this.humanApproved = humanApproved;
            this.violatedAxioms = violatedAxioms;
            this.proofScript = proofScript;
            this.proofResult = proofResult;
        }

        public boolean allowed() {
            return allowed;
        }

        public String decisionCode() {
            return decisionCode;
        }

        public boolean destructive() {
            return destructive;
        }

        public boolean privileged() {
            return privileged;
        }

        public boolean networked() {
            return networked;
        }

        public boolean unrestricted() {
            return unrestricted;
        }

        public boolean scopeBounded() {
            return scopeBounded;
        }

        public boolean humanApproved() {
            return humanApproved;
        }

        public List<String> violatedAxioms() {
            return violatedAxioms;
        }

        public String proofScript() {
            return proofScript;
        }

        public LeanProofBridge.ProofResult proofResult() {
            return proofResult;
        }
    }
}
