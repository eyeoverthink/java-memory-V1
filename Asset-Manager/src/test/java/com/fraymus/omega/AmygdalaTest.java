package com.fraymus.omega;

import com.fraymus.core.Intent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmygdalaTest {

    @Test
    void blocksUnscopedUnapprovedOpenClawExecution() {
        Amygdala amygdala = new Amygdala("test-policy");
        Intent plan = Intent.builder("openclaw_execution")
            .operation("execute")
            .networked(true)
            .unrestricted(true)
            .humanApproved(false)
            .build();

        Amygdala.PolicyDecision decision = amygdala.evaluate(plan);

        assertFalse(decision.allowed());
        assertTrue(decision.violatedAxioms().contains("AXIOM_SCOPE_BOUND"));
        assertTrue(decision.proofScript().contains("policyAllows = false"));
    }

    @Test
    void allowsScopedNonDestructiveReadOnlyIntent() {
        Amygdala amygdala = new Amygdala("test-policy");
        Intent plan = Intent.builder("workspace_review")
            .operation("read")
            .scope("workspace/docs")
            .humanApproved(false)
            .build();

        Amygdala.PolicyDecision decision = amygdala.evaluate(plan);

        assertTrue(decision.allowed());
        assertTrue(decision.violatedAxioms().isEmpty());
    }
}
