package fraymus.agent;

import fraymus.brain.OllamaSpine;
import fraymus.limbs.ClawConnector;
import fraymus.core.AuditLog;
import fraymus.memory.BlackBox;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * AGENT SWARM - Autonomous Code Maintenance Fleet
 * 
 * Deploys multiple FraynixAgents across a codebase for:
 * - Continuous code quality monitoring
 * - Autonomous bug fixing
 * - Self-healing architecture
 * - Entropy reduction
 * 
 * Uses executor-based threading (not thread-per-file) for scalability.
 */
public class AgentSwarm {

    private final OllamaSpine brain;
    private final ClawConnector hands;
    private final BlackBox memory;
    private final AuditLog auditLog;
    private final ExecutorService executor;
    
    private final List<FraynixAgent> activeAgents = new CopyOnWriteArrayList<>();
    private final Map<String, AgentResult> results = new ConcurrentHashMap<>();

    /**
     * Create swarm with default configuration
     */
    public AgentSwarm(BlackBox memory, AuditLog auditLog) {
        this(new OllamaSpine("llama3"), new ClawConnector(), memory, auditLog, 4);
    }

    /**
     * Create swarm with custom configuration
     */
    public AgentSwarm(OllamaSpine brain, ClawConnector hands, 
                     BlackBox memory, AuditLog auditLog, int maxConcurrent) {
        this.brain = brain;
        this.hands = hands;
        this.memory = memory;
        this.auditLog = auditLog;
        this.executor = Executors.newFixedThreadPool(maxConcurrent, r -> {
            Thread t = new Thread(r, "AgentSwarm-Worker");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Deploy agents across a directory
     */
    public void deployToDirectory(File directory, String filePattern) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🦠 AGENT SWARM - DEPLOYING TO CODEBASE               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Target Directory: " + directory.getAbsolutePath());
        System.out.println("File Pattern: " + filePattern);
        System.out.println();
        
        auditLog.log("swarm_deployment_started", directory.getAbsolutePath());

        List<File> targetFiles = findFiles(directory, filePattern);
        System.out.println("Found " + targetFiles.size() + " files to analyze");
        System.out.println();

        for (File file : targetFiles) {
            deployAgent(file);
        }

        System.out.println("✓ Deployed " + activeAgents.size() + " agents");
        System.out.println();
    }

    /**
     * Deploy single agent to file
     */
    public void deployAgent(File file) {
        FraynixAgent agent = new FraynixAgent(file, brain, hands, memory, auditLog);
        activeAgents.add(agent);
        
        executor.submit(() -> {
            agent.run();
            recordResult(agent);
        });
        
        System.out.println("   🕵️ Agent deployed to: " + file.getName());
    }

    /**
     * Wait for all agents to complete
     */
    public void waitForCompletion(long timeoutMinutes) {
        System.out.println("⏳ Waiting for agents to complete (timeout: " + timeoutMinutes + " minutes)...");
        System.out.println();
        
        long deadline = System.currentTimeMillis() + (timeoutMinutes * 60 * 1000);
        
        while (System.currentTimeMillis() < deadline) {
            boolean allComplete = activeAgents.stream().allMatch(FraynixAgent::isComplete);
            
            if (allComplete) {
                System.out.println("✅ All agents completed");
                break;
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        printSummary();
    }

    /**
     * Record agent result
     */
    private void recordResult(FraynixAgent agent) {
        AgentResult result = new AgentResult(
            agent.getTargetFile().getName(),
            agent.isSuccess(),
            agent.isComplete()
        );
        results.put(agent.getTargetFile().getAbsolutePath(), result);
    }

    /**
     * Print swarm summary
     */
    private void printSummary() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         📊 SWARM SUMMARY                                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        long total = results.size();
        long successful = results.values().stream().filter(r -> r.success).count();
        long failed = results.values().stream().filter(r -> !r.success && r.complete).count();
        long incomplete = results.values().stream().filter(r -> !r.complete).count();
        
        System.out.println("Total Files Analyzed: " + total);
        System.out.println("Successful: " + successful);
        System.out.println("Failed: " + failed);
        System.out.println("Incomplete: " + incomplete);
        System.out.println();
        
        if (successful > 0) {
            System.out.println("✅ Success Rate: " + (successful * 100 / total) + "%");
        }
        
        auditLog.log("swarm_summary", results);
    }

    /**
     * Find files matching pattern
     */
    private List<File> findFiles(File directory, String pattern) {
        List<File> files = new ArrayList<>();
        findFilesRecursive(directory, pattern, files);
        return files;
    }

    /**
     * Recursive file finder
     */
    private void findFilesRecursive(File directory, String pattern, List<File> results) {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findFilesRecursive(file, pattern, results);
            } else if (file.getName().matches(pattern)) {
                results.add(file);
            }
        }
    }

    /**
     * Shutdown swarm
     */
    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    /**
     * Agent result
     */
    private static class AgentResult {
        final String fileName;
        final boolean success;
        final boolean complete;

        AgentResult(String fileName, boolean success, boolean complete) {
            this.fileName = fileName;
            this.success = success;
            this.complete = complete;
        }
    }
}
