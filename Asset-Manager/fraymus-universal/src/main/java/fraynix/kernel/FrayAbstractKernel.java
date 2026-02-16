package fraynix.kernel;

import fraynix.brain.HyperTesseract;
import fraynix.core.*;
import fraynix.core.impl.DefaultIntentBus;
import fraynix.observe.EventLogger;

import java.util.*;
import java.util.concurrent.*;

/**
 * FRAY ABSTRACT KERNEL: The execution substrate.
 * 
 * Orchestrates:
 *   - Process model
 *   - Scheduler
 *   - Lifecycle
 *   - Intent routing
 * 
 * Uses DI - no static globals.
 */
public class FrayAbstractKernel implements KernelService {

    private final HyperTesseract brain;
    private final IntentBus intentBus;
    private final EventLogger logger;
    private final Map<String, KernelService> services;
    
    private Scheduler scheduler;
    private final ExecutorService workers;
    private final ScheduledExecutorService lifecycle;
    
    private volatile boolean running = false;
    private long startTime;
    
    // Process tracking
    private final Map<String, FrayProcess> processes = new ConcurrentHashMap<>();
    private final int maxConcurrentProcesses;

    public FrayAbstractKernel(HyperTesseract brain, IntentBus intentBus, EventLogger logger) {
        this(brain, intentBus, logger, new PriorityScheduler(), 4);
    }

    public FrayAbstractKernel(
        HyperTesseract brain,
        IntentBus intentBus,
        EventLogger logger,
        Scheduler scheduler,
        int workerThreads
    ) {
        this.brain = brain;
        this.intentBus = intentBus;
        this.logger = logger;
        this.scheduler = scheduler;
        this.services = new ConcurrentHashMap<>();
        this.maxConcurrentProcesses = workerThreads * 2;
        
        this.workers = Executors.newFixedThreadPool(workerThreads, r -> {
            Thread t = new Thread(r, "Kernel-Worker");
            t.setDaemon(true);
            return t;
        });
        
        this.lifecycle = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Kernel-Lifecycle");
            t.setDaemon(true);
            return t;
        });
        
        // Register core services
        registerService(brain);
        if (intentBus instanceof KernelService) {
            registerService((KernelService) intentBus);
        }
        
        // Set up intent handlers
        setupIntentHandlers();
        
        System.out.println("🔧 FrayAbstractKernel initialized (scheduler=" + scheduler.getName() + 
                          ", workers=" + workerThreads + ")");
    }

    private void setupIntentHandlers() {
        // Process operations
        intentBus.registerHandler(Intent.Type.PROCESS_SPAWN, this::handleProcessSpawn);
        intentBus.registerHandler(Intent.Type.PROCESS_STATUS, this::handleProcessStatus);
        
        // Brain operations
        intentBus.registerHandler(Intent.Type.BRAIN_THINK, this::handleBrainThink);
        intentBus.registerHandler(Intent.Type.BRAIN_DECIDE, this::handleBrainDecide);
        
        // Health check
        intentBus.registerHandler(Intent.Type.HEALTH_CHECK, this::handleHealthCheck);
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        System.out.println("🔧 Scheduler changed to: " + scheduler.getName());
    }

    public void registerService(KernelService service) {
        services.put(service.getName(), service);
    }

    public FrayProcess spawn(Intent intent, Runnable task) {
        return spawn(intent, task, null);
    }

    public FrayProcess spawn(Intent intent, Runnable task, CapabilityToken capabilities) {
        FrayProcess process = new FrayProcess(intent, task, capabilities);
        processes.put(process.getId(), process);
        scheduler.submit(process);
        
        logger.logEvent("process_spawned", Map.of(
            "processId", process.getId(),
            "intentType", intent.getType().name(),
            "priority", process.getPriority()
        ));
        
        return process;
    }

    private void processLoop() {
        while (running) {
            try {
                // Check if we can run more processes
                long runningCount = processes.values().stream()
                    .filter(p -> p.getState() == FrayProcess.State.RUNNING)
                    .count();
                
                if (runningCount >= maxConcurrentProcesses) {
                    Thread.sleep(10);
                    continue;
                }
                
                // Get next process
                FrayProcess process = scheduler.next();
                if (process == null) {
                    Thread.sleep(10);
                    continue;
                }
                
                // Execute
                workers.submit(() -> executeProcess(process));
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void executeProcess(FrayProcess process) {
        long start = System.currentTimeMillis();
        
        try {
            process.run();
            scheduler.complete(process);
            
            long duration = System.currentTimeMillis() - start;
            logger.logActionTaken(
                process.getIntent().getId(),
                "process_executed",
                true,
                duration
            );
            
        } catch (Exception e) {
            scheduler.fail(process, e.getMessage());
            
            long duration = System.currentTimeMillis() - start;
            logger.logActionTaken(
                process.getIntent().getId(),
                "process_executed",
                false,
                duration
            );
        }
    }

    // Intent handlers
    private IntentBus.IntentResult handleProcessSpawn(Intent intent) {
        String taskName = intent.getPayloadString("task");
        Runnable task = () -> System.out.println("Task: " + taskName);
        FrayProcess proc = spawn(intent, task);
        return IntentBus.IntentResult.success(intent.getId(), proc.getId(), 0);
    }

    private IntentBus.IntentResult handleProcessStatus(Intent intent) {
        String processId = intent.getPayloadString("processId");
        FrayProcess proc = processes.get(processId);
        if (proc == null) {
            return IntentBus.IntentResult.failure(intent.getId(), "Process not found", 0);
        }
        return IntentBus.IntentResult.success(intent.getId(), proc.getState().name(), 0);
    }

    private IntentBus.IntentResult handleBrainThink(Intent intent) {
        String thought = intent.getPayloadString("thought");
        double intensity = 0.5;
        Object intensityObj = intent.getPayload().get("intensity");
        if (intensityObj instanceof Number) {
            intensity = ((Number) intensityObj).doubleValue();
        }
        brain.injectThought(thought, intensity);
        return IntentBus.IntentResult.success(intent.getId(), "thought_injected", 0);
    }

    private IntentBus.IntentResult handleBrainDecide(Intent intent) {
        Policy.Decision<String> decision = brain.decide(intent);
        Map<String, Object> result = Map.of(
            "choice", decision.getBestChoice() != null ? decision.getBestChoice() : "NONE",
            "confidence", decision.confidence(),
            "reason", decision.reason()
        );
        return IntentBus.IntentResult.success(intent.getId(), result, 0);
    }

    private IntentBus.IntentResult handleHealthCheck(Intent intent) {
        Map<String, Object> health = new HashMap<>();
        for (Map.Entry<String, KernelService> entry : services.entrySet()) {
            health.put(entry.getKey(), entry.getValue().getHealth());
        }
        return IntentBus.IntentResult.success(intent.getId(), health, 0);
    }

    // KernelService implementation
    @Override
    public String getName() { return "FrayAbstractKernel"; }
    
    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public void start() {
        if (running) return;
        running = true;
        startTime = System.currentTimeMillis();
        
        // Start all services
        for (KernelService service : services.values()) {
            try {
                service.start();
            } catch (Exception e) {
                System.err.println("Failed to start service " + service.getName() + ": " + e.getMessage());
            }
        }
        
        // Start process loop
        lifecycle.submit(this::processLoop);
        
        logger.logEvent("kernel_started", Map.of(
            "scheduler", scheduler.getName(),
            "services", services.size()
        ));
        
        System.out.println("🔧 FrayAbstractKernel started");
    }

    @Override
    public void stop() {
        running = false;
        
        // Stop all services
        for (KernelService service : services.values()) {
            try {
                service.stop();
            } catch (Exception e) {
                System.err.println("Failed to stop service " + service.getName() + ": " + e.getMessage());
            }
        }
        
        workers.shutdown();
        lifecycle.shutdown();
        
        try {
            workers.awaitTermination(5, TimeUnit.SECONDS);
            lifecycle.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.logEvent("kernel_stopped", Map.of(
            "uptimeMs", System.currentTimeMillis() - startTime
        ));
        
        System.out.println("🔧 FrayAbstractKernel stopped");
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
        
        // Check all services
        boolean allHealthy = true;
        for (KernelService service : services.values()) {
            if (!service.getHealth().healthy()) {
                allHealthy = false;
                break;
            }
        }
        
        Map<String, Object> details = Map.of(
            "services", services.size(),
            "scheduler", scheduler.getName(),
            "queueSize", scheduler.getQueueSize(),
            "processes", processes.size()
        );
        
        if (!running) {
            return HealthReport.unhealthy(ServiceStatus.STOPPED, "Not running", 0);
        }
        
        if (!allHealthy) {
            return HealthReport.degraded("Some services unhealthy", uptime, details);
        }
        
        return new HealthReport(ServiceStatus.RUNNING, true, "OK", uptime, details);
    }

    @Override
    public ServiceMetrics getMetrics() {
        Scheduler.SchedulerMetrics sm = scheduler.getMetrics();
        return new ServiceMetrics(
            sm.submitted(),
            sm.completed(),
            sm.failed(),
            sm.avgWaitMs(),
            sm.avgExecutionMs(),
            0.0,
            0,
            Map.of(
                "queueSize", (long) sm.currentQueueSize(),
                "processes", (long) processes.size()
            )
        );
    }

    // Accessors
    public HyperTesseract getBrain() { return brain; }
    public IntentBus getIntentBus() { return intentBus; }
    public EventLogger getLogger() { return logger; }
    public Scheduler getScheduler() { return scheduler; }
    public Map<String, KernelService> getServices() { return Collections.unmodifiableMap(services); }
}
