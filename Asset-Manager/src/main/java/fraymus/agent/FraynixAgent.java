package fraymus.agent;

import fraymus.brain.OllamaSpine;
import fraymus.limbs.ClawConnector;
import fraymus.core.AuditLog;
import fraymus.memory.BlackBox;
import java.io.File;
import java.nio.file.Files;

/**
 * FRAYNIX AGENT - The Autonomous Self-Correction Loop
 * 
 * Combines Brain (Ollama) + Hands (OpenClaw) + Soul (Fraynix Physics)
 * to create a self-healing, self-improving codebase.
 * 
 * Process:
 * 1. READ: Load file from disk
 * 2. THINK: Ask Ollama "Is this code bad?"
 * 3. ACT: If bad, tell OpenClaw "Fix it"
 * 4. VERIFY: Check if fix worked
 * 5. REMEMBER: Store experience in BlackBox
 * 
 * The Trinity:
 * - Brain (Ollama): Raw intelligence, reasoning, code generation
 * - Hands (OpenClaw): Execution layer, file system, OS manipulation
 * - Soul (Fraynix): Orchestrator, physics-driven decisions
 */
public class FraynixAgent implements Runnable {

    private final OllamaSpine brain;
    private final ClawConnector hands;
    private final BlackBox memory;
    private final AuditLog auditLog;
    private final File targetFile;
    
    private volatile boolean complete = false;
    private volatile boolean success = false;

    /**
     * Create agent with default brain and hands
     */
    public FraynixAgent(File file, BlackBox memory, AuditLog auditLog) {
        this(file, new OllamaSpine("llama3"), new ClawConnector(), memory, auditLog);
    }

    /**
     * Create agent with custom brain and hands
     */
    public FraynixAgent(File file, OllamaSpine brain, ClawConnector hands, 
                       BlackBox memory, AuditLog auditLog) {
        this.targetFile = file;
        this.brain = brain;
        this.hands = hands;
        this.memory = memory;
        this.auditLog = auditLog;
    }

    @Override
    public void run() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🕵️ FRAYNIX AGENT - AUTONOMOUS ANALYSIS                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Target: " + targetFile.getAbsolutePath());
        System.out.println();
        
        auditLog.log("agent_analysis_started", targetFile.getName());

        try {
            // 1. READ
            if (!targetFile.exists()) {
                System.err.println("❌ FILE NOT FOUND: " + targetFile.getAbsolutePath());
                auditLog.log("agent_file_not_found", targetFile.getName());
                complete = true;
                return;
            }

            System.out.println("📖 Reading file...");
            String content = Files.readString(targetFile.toPath());
            System.out.println("   ✓ Loaded " + content.length() + " characters");
            System.out.println();

            // 2. THINK (Entropy Check)
            System.out.println("🧠 Analyzing code quality...");
            String verdict = brain.analyzeCode(content);
            System.out.println();

            if (verdict.toUpperCase().contains("DIRTY")) {
                handleCorruption(verdict, content);
            } else if (verdict.toUpperCase().contains("CLEAN")) {
                handleClean();
            } else {
                handleUnclear(verdict);
            }

        } catch (Exception e) {
            System.err.println("❌ AGENT FAILURE: " + e.getMessage());
            auditLog.log("agent_exception", targetFile.getName(), e);
            complete = true;
        }
    }

    /**
     * Handle corrupted/dirty code
     */
    private void handleCorruption(String verdict, String content) {
        System.out.println("⚠️ CORRUPTION DETECTED");
        System.out.println("   Verdict: " + verdict);
        System.out.println();
        
        auditLog.log("agent_corruption_detected", verdict);

        // Check if we've seen this before
        String wisdom = memory.recall("Fix " + targetFile.getName());
        if (wisdom.contains("INSIGHT FROM PAST")) {
            System.out.println("💡 " + wisdom);
            System.out.println();
        }

        // 3. ACT (OpenClaw Intervention)
        System.out.println("⚡ ACTIVATING OPENCLAW...");
        String task = "Read the file " + targetFile.getAbsolutePath() + ". " +
                     "Refactor it to fix the following issue: " + verdict + ". " +
                     "Save the file when done. Be careful to preserve functionality.";
        
        String result = hands.dispatch(task, "CONTEXT: AUTONOMOUS_REPAIR");
        System.out.println();
        
        // 4. VERIFY
        boolean fixed = !result.contains("ERROR") && !result.contains("SEVERED");
        
        if (fixed) {
            System.out.println("✅ REPAIR COMPLETE");
            System.out.println("   " + result);
            success = true;
            
            // 5. REMEMBER
            memory.remember(
                "Fix " + targetFile.getName(),
                "Successfully repaired: " + verdict,
                true
            );
            auditLog.log("agent_repair_success", targetFile.getName());
        } else {
            System.out.println("❌ REPAIR FAILED");
            System.out.println("   " + result);
            success = false;
            
            memory.remember(
                "Fix " + targetFile.getName(),
                "Failed to repair: " + result,
                false
            );
            auditLog.log("agent_repair_failed", targetFile.getName());
        }
        
        complete = true;
    }

    /**
     * Handle clean code
     */
    private void handleClean() {
        System.out.println("✨ FILE IS CLEAN");
        System.out.println("   No action required");
        System.out.println();
        
        success = true;
        complete = true;
        
        memory.remember(
            "Analyze " + targetFile.getName(),
            "Code is clean and well-structured",
            true
        );
        auditLog.log("agent_file_clean", targetFile.getName());
    }

    /**
     * Handle unclear verdict
     */
    private void handleUnclear(String verdict) {
        System.out.println("⚠️ UNCLEAR VERDICT");
        System.out.println("   Response: " + verdict);
        System.out.println("   Skipping intervention");
        System.out.println();
        
        success = false;
        complete = true;
        
        auditLog.log("agent_unclear_verdict", targetFile.getName());
    }

    /**
     * Check if analysis is complete
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Check if analysis succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Get target file
     */
    public File getTargetFile() {
        return targetFile;
    }
}
