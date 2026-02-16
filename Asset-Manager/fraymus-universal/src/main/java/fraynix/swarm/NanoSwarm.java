package fraynix.swarm;

import fraynix.core.*;
import fraynix.fs.FrayFS;
import fraynix.observe.EventLogger;

import java.util.*;
import java.util.concurrent.*;

/**
 * NANO SWARM V1: Agents as scheduled tasks, not raw threads.
 * 
 * Uses:
 *   - ScheduledExecutorService
 *   - Bounded queues
 *   - Backpressure
 * 
 * Agent responsibilities:
 *   - Watch integrity signals
 *   - Request repair plan (NOT directly patch)
 *   - Verify repair
 */
public class NanoSwarm implements KernelService {

    private final FrayFS fs;
    private final IntentBus intentBus;
    private final EventLogger logger;
    
    private final ScheduledExecutorService executor;
    private final Map<String, RepairAgent> agents = new ConcurrentHashMap<>();
    private final BlockingQueue<Runnable> taskQueue;
    
    private volatile boolean running = false;
    private long startTime;
    private int maxAgents;

    public NanoSwarm(FrayFS fs, IntentBus intentBus, EventLogger logger) {
        this(fs, intentBus, logger, 4, 100);
    }

    public NanoSwarm(FrayFS fs, IntentBus intentBus, EventLogger logger, int maxAgents, int queueCapacity) {
        this.fs = fs;
        this.intentBus = intentBus;
        this.logger = logger;
        this.maxAgents = maxAgents;
        this.taskQueue = new LinkedBlockingQueue<>(queueCapacity);
        
        // Bounded thread pool with backpressure
        this.executor = new ScheduledThreadPoolExecutor(maxAgents, r -> {
            Thread t = new Thread(r, "NanoSwarm-Worker");
            t.setDaemon(true);
            return t;
        }, new ThreadPoolExecutor.CallerRunsPolicy()); // Backpressure: caller runs if full
        
        System.out.println("🐝 NanoSwarm initialized (agents=" + maxAgents + ", queue=" + queueCapacity + ")");
    }

    public RepairAgent spawnAgent(String id) {
        if (agents.size() >= maxAgents) {
            System.out.println("🐝 Cannot spawn agent: max agents reached (" + maxAgents + ")");
            return null;
        }
        
        RepairAgent agent = new RepairAgent(id, fs, intentBus, logger);
        agents.put(id, agent);
        
        // Schedule agent to run periodically
        executor.scheduleAtFixedRate(agent, 0, 5, TimeUnit.SECONDS);
        
        logger.logEvent("swarm_agent_spawned", Map.of("agentId", id));
        System.out.println("🐝 Agent spawned: " + id);
        
        return agent;
    }

    public void terminateAgent(String id) {
        RepairAgent agent = agents.remove(id);
        if (agent != null) {
            agent.stop();
            logger.logEvent("swarm_agent_terminated", Map.of("agentId", id));
            System.out.println("🐝 Agent terminated: " + id);
        }
    }

    public void submitTask(Runnable task) {
        if (!running) return;
        
        // Try to queue, apply backpressure if full
        if (!taskQueue.offer(task)) {
            logger.logEvent("swarm_backpressure", Map.of(
                "queueSize", taskQueue.size(),
                "reason", "queue_full"
            ));
            // CallerRunsPolicy will handle this
        }
        
        executor.submit(() -> {
            try {
                Runnable queued = taskQueue.poll();
                if (queued != null) {
                    queued.run();
                }
            } catch (Exception e) {
                logger.logEvent("swarm_task_error", Map.of("error", e.getMessage()));
            }
        });
    }

    public void triggerIntegrityCheck() {
        submitTask(() -> {
            List<String> corrupted = fs.checkAllIntegrity();
            if (!corrupted.isEmpty()) {
                logger.logEvent("swarm_integrity_issues", Map.of(
                    "count", corrupted.size(),
                    "paths", corrupted.size() > 5 ? 
                        corrupted.subList(0, 5).toString() + "..." : 
                        corrupted.toString()
                ));
                
                // Dispatch to agents
                for (String path : corrupted) {
                    dispatchRepairRequest(path, RepairAgent.IssueType.CHECKSUM_MISMATCH);
                }
            }
        });
    }

    public void dispatchRepairRequest(String path, RepairAgent.IssueType issueType) {
        // Find available agent
        for (RepairAgent agent : agents.values()) {
            RepairAgent.Issue issue = new RepairAgent.Issue(path, issueType, "Dispatched by swarm");
            submitTask(() -> agent.handleIssue(issue));
            return;
        }
        
        // No agents available - spawn one if possible
        if (agents.size() < maxAgents) {
            RepairAgent agent = spawnAgent("repair-" + System.currentTimeMillis());
            if (agent != null) {
                RepairAgent.Issue issue = new RepairAgent.Issue(path, issueType, "Dispatched by swarm");
                submitTask(() -> agent.handleIssue(issue));
            }
        }
    }

    public Map<String, Object> getSwarmStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("running", running);
        stats.put("agentCount", agents.size());
        stats.put("maxAgents", maxAgents);
        stats.put("queueSize", taskQueue.size());
        
        // Aggregate agent stats
        int totalAttempted = 0;
        int totalVerified = 0;
        int totalFailed = 0;
        
        for (RepairAgent agent : agents.values()) {
            Map<String, Object> agentStats = agent.getStats();
            totalAttempted += (int) agentStats.get("repairsAttempted");
            totalVerified += (int) agentStats.get("repairsVerified");
            totalFailed += (int) agentStats.get("repairsFailed");
        }
        
        stats.put("repairsAttempted", totalAttempted);
        stats.put("repairsVerified", totalVerified);
        stats.put("repairsFailed", totalFailed);
        stats.put("successRate", totalAttempted > 0 ? 
            (double) totalVerified / totalAttempted : 0.0);
        
        return stats;
    }

    // KernelService implementation
    @Override
    public String getName() { return "NanoSwarm"; }
    
    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public void start() {
        if (running) return;
        running = true;
        startTime = System.currentTimeMillis();
        
        // Spawn initial repair agent
        spawnAgent("repair-primary");
        
        // Schedule periodic integrity checks
        executor.scheduleAtFixedRate(this::triggerIntegrityCheck, 30, 60, TimeUnit.SECONDS);
        
        System.out.println("🐝 NanoSwarm started");
    }

    @Override
    public void stop() {
        running = false;
        
        // Stop all agents
        for (RepairAgent agent : agents.values()) {
            agent.stop();
        }
        agents.clear();
        
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("🐝 NanoSwarm stopped");
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public ServiceStatus getStatus() {
        return running ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
    }

    @Override
    public HealthReport getHealth() {
        long uptime = running ? System.currentTimeMillis() - startTime : 0;
        
        Map<String, Object> details = getSwarmStats();
        
        if (!running) {
            return HealthReport.unhealthy(ServiceStatus.STOPPED, "Not running", 0);
        }
        
        // Check for high failure rate
        int attempted = (int) details.get("repairsAttempted");
        int failed = (int) details.get("repairsFailed");
        
        if (attempted > 10 && failed > attempted / 2) {
            return HealthReport.degraded("High repair failure rate", uptime, details);
        }
        
        return new HealthReport(ServiceStatus.RUNNING, true, "OK", uptime, details);
    }

    @Override
    public ServiceMetrics getMetrics() {
        Map<String, Object> stats = getSwarmStats();
        return new ServiceMetrics(
            (int) stats.get("repairsAttempted"),
            (int) stats.get("repairsVerified"),
            (int) stats.get("repairsFailed"),
            0, 0, 0.0, 0,
            Map.of(
                "agents", (long) agents.size(),
                "queueSize", (long) taskQueue.size()
            )
        );
    }
}
