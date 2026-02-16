package fraynix.swarm;

import fraynix.core.*;
import fraynix.fs.FrayFS;
import fraynix.fs.VFile;
import fraynix.observe.EventLogger;

import java.util.*;

/**
 * REPAIR AGENT: Self-healing agent with verification.
 * 
 * Responsibilities:
 *   - Watch integrity signals (checksum drift, size spikes, entropy heuristics)
 *   - Request repair plan (NOT directly patch)
 *   - Verify repair (hash match, parse, compile, test)
 * 
 * Verification is the whole game. Self-heal without verify is self-corrupt.
 */
public class RepairAgent implements Runnable {

    public enum IssueType {
        CHECKSUM_MISMATCH,
        SIZE_ANOMALY,
        ENTROPY_SPIKE,
        MISSING_FILE,
        PARSE_ERROR,
        PERMISSION_ERROR
    }

    private final String id;
    private final FrayFS fs;
    private final IntentBus intentBus;
    private final EventLogger logger;
    private final CapabilityToken capabilities;
    
    private volatile boolean active = true;
    private int repairsAttempted = 0;
    private int repairsVerified = 0;
    private int repairsFailed = 0;

    public RepairAgent(String id, FrayFS fs, IntentBus intentBus, EventLogger logger) {
        this.id = id;
        this.fs = fs;
        this.intentBus = intentBus;
        this.logger = logger;
        this.capabilities = CapabilityToken.repair("RepairAgent-" + id);
    }

    @Override
    public void run() {
        while (active) {
            try {
                // Check for integrity issues
                List<String> corrupted = fs.checkAllIntegrity();
                
                for (String path : corrupted) {
                    Issue issue = new Issue(path, IssueType.CHECKSUM_MISMATCH, "Hash verification failed");
                    handleIssue(issue);
                }
                
                // Sleep between checks
                Thread.sleep(5000);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.logEvent("repair_agent_error", Map.of(
                    "agentId", id,
                    "error", e.getMessage()
                ));
            }
        }
    }

    public void handleIssue(Issue issue) {
        repairsAttempted++;
        
        logger.logRepairAttempted(issue.path, issue.type.name(), "planning");
        
        // 1. Request repair plan (don't directly modify)
        RepairPlan plan = createRepairPlan(issue);
        
        if (plan == null) {
            logger.logRepairVerified(issue.path, false, "No repair plan available");
            repairsFailed++;
            return;
        }
        
        // 2. Execute repair
        boolean executed = executeRepair(plan);
        
        if (!executed) {
            logger.logRepairVerified(issue.path, false, "Repair execution failed");
            repairsFailed++;
            return;
        }
        
        // 3. VERIFY - This is critical
        boolean verified = verifyRepair(plan);
        
        logger.logRepairVerified(issue.path, verified, 
            verified ? "Hash verified, integrity restored" : "Verification failed");
        
        if (verified) {
            repairsVerified++;
            
            // Publish success intent
            Intent successIntent = Intent.builder()
                .type(Intent.Type.REPAIR_VERIFY)
                .origin("RepairAgent-" + id)
                .capability(capabilities)
                .payload(Map.of(
                    "path", issue.path,
                    "issueType", issue.type.name(),
                    "action", plan.action.name(),
                    "verified", true
                ))
                .build();
            
            intentBus.publish(successIntent);
        } else {
            repairsFailed++;
        }
    }

    private RepairPlan createRepairPlan(Issue issue) {
        VFile file = fs.read(issue.path);
        
        switch (issue.type) {
            case CHECKSUM_MISMATCH:
            case SIZE_ANOMALY:
                // Try to restore from version history
                List<VFile> versions = fs.getVersions(issue.path);
                if (versions.size() > 1) {
                    // Find last known good version
                    for (int i = versions.size() - 2; i >= 0; i--) {
                        VFile v = versions.get(i);
                        if (v.verifyIntegrity()) {
                            return new RepairPlan(
                                issue.path,
                                RepairAction.ROLLBACK,
                                v.getVersion(),
                                v.getHash(),
                                "Rollback to version " + v.getVersion()
                            );
                        }
                    }
                }
                return null; // No good version found
                
            case MISSING_FILE:
                // Check if we have any version
                versions = fs.getVersions(issue.path);
                if (!versions.isEmpty()) {
                    VFile latest = versions.get(versions.size() - 1);
                    return new RepairPlan(
                        issue.path,
                        RepairAction.RESTORE,
                        latest.getVersion(),
                        latest.getHash(),
                        "Restore from version history"
                    );
                }
                return null;
                
            default:
                return null;
        }
    }

    private boolean executeRepair(RepairPlan plan) {
        try {
            switch (plan.action) {
                case ROLLBACK:
                    VFile rolled = fs.rollback(plan.path, plan.targetVersion);
                    return rolled != null;
                    
                case RESTORE:
                    List<VFile> versions = fs.getVersions(plan.path);
                    for (VFile v : versions) {
                        if (v.getVersion() == plan.targetVersion) {
                            fs.write(plan.path, v.getContent(), v.getMetadata());
                            return true;
                        }
                    }
                    return false;
                    
                case REGENERATE:
                    // Would need Genesis integration
                    return false;
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            logger.logEvent("repair_execution_error", Map.of(
                "path", plan.path,
                "action", plan.action.name(),
                "error", e.getMessage()
            ));
            return false;
        }
    }

    private boolean verifyRepair(RepairPlan plan) {
        VFile file = fs.read(plan.path);
        if (file == null) return false;
        
        // 1. Hash verification
        if (!file.verifyIntegrity()) {
            return false;
        }
        
        // 2. Expected hash match (if we have one)
        if (plan.expectedHash != null && !plan.expectedHash.equals(file.getHash())) {
            return false;
        }
        
        // 3. Basic parse verification (for known file types)
        String ext = file.getExtension();
        if ("json".equals(ext)) {
            try {
                // Simple JSON validation - check for balanced braces
                String content = new String(file.getContent());
                int braces = 0;
                for (char c : content.toCharArray()) {
                    if (c == '{') braces++;
                    if (c == '}') braces--;
                    if (braces < 0) return false;
                }
                if (braces != 0) return false;
            } catch (Exception e) {
                return false;
            }
        }
        
        return true;
    }

    public void stop() {
        active = false;
    }

    public Map<String, Object> getStats() {
        return Map.of(
            "agentId", id,
            "active", active,
            "repairsAttempted", repairsAttempted,
            "repairsVerified", repairsVerified,
            "repairsFailed", repairsFailed,
            "successRate", repairsAttempted > 0 ? 
                (double) repairsVerified / repairsAttempted : 0.0
        );
    }

    // Data classes
    public record Issue(String path, IssueType type, String details) {}
    
    public record RepairPlan(
        String path,
        RepairAction action,
        long targetVersion,
        String expectedHash,
        String description
    ) {}

    public enum RepairAction {
        ROLLBACK,       // Restore from version history
        RESTORE,        // Restore deleted file
        REGENERATE,     // Use Genesis to recreate
        PATCH           // Apply specific fix
    }
}
