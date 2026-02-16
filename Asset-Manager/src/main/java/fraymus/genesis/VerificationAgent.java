package fraymus.genesis;

import fraymus.limbs.ClawConnector;
import fraymus.memory.BlackBox;
import fraymus.core.AuditLog;
import fraymus.genesis.GenesisArchitect.Module;

/**
 * VERIFICATION AGENT - The Scientist
 * 
 * Specialized agent whose only job is to break things.
 * When BuilderAgent finishes, it hands code to VerificationAgent.
 * 
 * Process:
 * 1. Run code in sandbox
 * 2. Capture results
 * 3. Record in BlackBox
 * 4. If failed, trigger repair
 */
public class VerificationAgent implements Runnable {

    private final Module module;
    private final String artifactPath;
    private final GenesisSandbox sandbox;
    private final ClawConnector claw;
    private final BlackBox memory;
    private final AuditLog auditLog;
    
    private volatile boolean complete = false;
    private volatile boolean success = false;

    public VerificationAgent(Module module, String artifactPath, 
                            BlackBox memory, AuditLog auditLog) {
        this.module = module;
        this.artifactPath = artifactPath;
        this.sandbox = new GenesisSandbox(auditLog);
        this.claw = new ClawConnector();
        this.memory = memory;
        this.auditLog = auditLog;
    }

    @Override
    public void run() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🔬 VERIFICATION AGENT - TESTING " + module.name);
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        auditLog.log("verification_started", module.name);

        try {
            // 1. RUN THE EXPERIMENT
            GenesisSandbox.VerificationResult result = sandbox.verifyArtifact(
                artifactPath, 
                module.tech
            );

            if (result.passed) {
                handleSuccess(result);
            } else {
                handleFailure(result);
            }
            
        } catch (Exception e) {
            System.err.println("💥 VERIFICATION EXCEPTION: " + e.getMessage());
            auditLog.log("verification_exception", module.name, e);
            handleFailure(new GenesisSandbox.VerificationResult(
                false, 
                "Exception: " + e.getMessage(), 
                ""
            ));
        } finally {
            complete = true;
        }
    }

    /**
     * Handle successful verification
     */
    private void handleSuccess(GenesisSandbox.VerificationResult result) {
        System.out.println("✅ VERIFIED: " + module.name + " is stable");
        System.out.println("   " + result.message);
        System.out.println();
        
        success = true;
        
        // Record success in BlackBox
        memory.remember(
            module.name + " Construction (" + module.tech + ")",
            "Tests Passed: " + result.message,
            true
        );
        
        auditLog.log("verification_passed", module.name);
    }

    /**
     * Handle failed verification
     */
    private void handleFailure(GenesisSandbox.VerificationResult result) {
        System.err.println("💥 FAILURE: " + module.name + " crashed in sandbox");
        System.err.println("   " + result.message);
        System.err.println();
        
        success = false;
        
        // Record failure in BlackBox
        memory.remember(
            module.name + " Construction (" + module.tech + ")",
            "Tests Failed: " + result.message,
            false
        );
        
        auditLog.log("verification_failed", module.name);
        
        // 2. SELF-HEALING LOOP
        if (shouldAttemptRepair()) {
            triggerRepairProtocol(result);
        }
    }

    /**
     * Determine if repair should be attempted
     */
    private boolean shouldAttemptRepair() {
        // For V1, we log the failure but don't auto-repair
        // Auto-repair will be added in V2 with proper safeguards
        return false;
    }

    /**
     * Trigger repair protocol
     */
    private void triggerRepairProtocol(GenesisSandbox.VerificationResult result) {
        System.out.println("🚑 DISPATCHING REPAIR BOT for " + module.name);
        auditLog.log("repair_triggered", module.name);
        
        // Build repair prompt
        String repairPrompt = buildRepairPrompt(result);
        
        // Send to OpenClaw for repair
        String fixedCode = claw.dispatch(repairPrompt, "CONTEXT: REPAIR");
        
        // TODO: Write fixed code and re-verify
        // This would create a repair loop with max attempts
        
        auditLog.log("repair_attempted", module.name);
    }

    /**
     * Build repair prompt for OpenClaw
     */
    private String buildRepairPrompt(GenesisSandbox.VerificationResult result) {
        return "The following code failed verification:\n\n" +
               "Module: " + module.name + "\n" +
               "Technology: " + module.tech + "\n" +
               "Path: " + artifactPath + "\n\n" +
               "Error Output:\n" + result.output + "\n\n" +
               "Please fix the code to pass all tests. " +
               "Return ONLY the corrected code, no explanations.";
    }

    /**
     * Check if verification is complete
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Check if verification succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Get module being verified
     */
    public Module getModule() {
        return module;
    }
}
