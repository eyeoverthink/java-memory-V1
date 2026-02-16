package fraynix.pulse;

import fraynix.brain.BrainState;
import fraynix.brain.HyperTesseract;
import fraynix.core.*;
import fraynix.observe.EventLogger;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.*;
import java.util.concurrent.*;

/**
 * BRAIN PULSE: Heartbeat that actually helps.
 * 
 * Sense → Predict → Act with guardrails.
 * 
 * V1 actions allowed:
 *   - trigger GC hints
 *   - reduce concurrency / throttle
 *   - reschedule heavy tasks
 *   - prefetch / cache warmup
 * 
 * Non-negotiable safety:
 *   - never "kill random processes"
 *   - never "rewrite core modules"
 *   - never "change policy without logging + rollback"
 */
public class BrainPulse implements KernelService {

    private final HyperTesseract brain;
    private final IntentBus intentBus;
    private final EventLogger logger;
    private final ScheduledExecutorService pulse;
    
    private final int pulseIntervalMs;
    private volatile boolean running = false;
    private long startTime;
    
    // Thresholds
    private double memoryWarningThreshold = 0.75;
    private double memoryCriticalThreshold = 0.90;
    private double cpuWarningThreshold = 0.80;
    private double cpuCriticalThreshold = 0.95;
    
    // Action tracking (to prevent spam)
    private long lastGcHint = 0;
    private long lastThrottle = 0;
    private static final long GC_COOLDOWN_MS = 30000;
    private static final long THROTTLE_COOLDOWN_MS = 10000;
    
    // Metrics
    private final List<ResourceSample> history = new CopyOnWriteArrayList<>();
    private static final int HISTORY_LIMIT = 100;

    public BrainPulse(HyperTesseract brain, IntentBus intentBus, EventLogger logger) {
        this(brain, intentBus, logger, 1000); // 1 Hz default
    }

    public BrainPulse(HyperTesseract brain, IntentBus intentBus, EventLogger logger, int pulseIntervalMs) {
        this.brain = brain;
        this.intentBus = intentBus;
        this.logger = logger;
        this.pulseIntervalMs = pulseIntervalMs;
        
        this.pulse = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "BrainPulse");
            t.setDaemon(true);
            return t;
        });
        
        System.out.println("💓 BrainPulse initialized (interval=" + pulseIntervalMs + "ms)");
    }

    private void heartbeat() {
        // 1. SENSE - gather resource state
        ResourceSample sample = sense();
        addToHistory(sample);
        
        // Log metrics
        logger.logMetric("cpu_usage", sample.cpuUsage);
        logger.logMetric("memory_usage", sample.memoryUsage);
        logger.logMetric("heap_used_mb", sample.heapUsedMb);
        logger.logMetric("thread_count", sample.threadCount);
        
        // 2. PREDICT - analyze trends
        Prediction prediction = predict(sample);
        
        // 3. ACT - take safe actions with guardrails
        act(sample, prediction);
        
        // 4. INFORM BRAIN
        brain.injectThought(
            "PULSE: cpu=" + String.format("%.1f%%", sample.cpuUsage * 100) +
            " mem=" + String.format("%.1f%%", sample.memoryUsage * 100),
            sample.memoryUsage > memoryWarningThreshold ? 0.4 : 0.1
        );
    }

    private ResourceSample sense() {
        Runtime rt = Runtime.getRuntime();
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        long heapUsed = memBean.getHeapMemoryUsage().getUsed();
        long heapMax = memBean.getHeapMemoryUsage().getMax();
        double memUsage = (double) heapUsed / heapMax;
        
        double cpuUsage = 0.0;
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOs) {
            cpuUsage = sunOs.getProcessCpuLoad();
            if (cpuUsage < 0) cpuUsage = 0;
        }
        
        return new ResourceSample(
            System.currentTimeMillis(),
            cpuUsage,
            memUsage,
            heapUsed / (1024.0 * 1024.0),
            heapMax / (1024.0 * 1024.0),
            Thread.activeCount(),
            rt.availableProcessors()
        );
    }

    private Prediction predict(ResourceSample current) {
        if (history.size() < 5) {
            return new Prediction(Trend.STABLE, Trend.STABLE, Risk.LOW, "Insufficient history");
        }
        
        // Calculate trends from last 5 samples
        List<ResourceSample> recent = history.subList(
            Math.max(0, history.size() - 5), history.size()
        );
        
        double cpuDelta = current.cpuUsage - recent.get(0).cpuUsage;
        double memDelta = current.memoryUsage - recent.get(0).memoryUsage;
        
        Trend cpuTrend = cpuDelta > 0.1 ? Trend.RISING : (cpuDelta < -0.1 ? Trend.FALLING : Trend.STABLE);
        Trend memTrend = memDelta > 0.05 ? Trend.RISING : (memDelta < -0.05 ? Trend.FALLING : Trend.STABLE);
        
        Risk risk = Risk.LOW;
        String reason = "Normal operation";
        
        if (current.memoryUsage > memoryCriticalThreshold) {
            risk = Risk.CRITICAL;
            reason = "Memory critical";
        } else if (current.memoryUsage > memoryWarningThreshold || 
                   current.cpuUsage > cpuCriticalThreshold) {
            risk = Risk.HIGH;
            reason = "Resource pressure";
        } else if (memTrend == Trend.RISING && current.memoryUsage > 0.6) {
            risk = Risk.MEDIUM;
            reason = "Memory trending up";
        }
        
        return new Prediction(cpuTrend, memTrend, risk, reason);
    }

    private void act(ResourceSample sample, Prediction prediction) {
        long now = System.currentTimeMillis();
        
        // GC Hint (safe action)
        if (sample.memoryUsage > memoryWarningThreshold && 
            now - lastGcHint > GC_COOLDOWN_MS) {
            
            logger.logEvent("pulse_action", Map.of(
                "action", "GC_HINT",
                "reason", "Memory at " + String.format("%.1f%%", sample.memoryUsage * 100),
                "safe", true
            ));
            
            System.gc();
            lastGcHint = now;
            
            brain.injectThought("ACTION: Suggested garbage collection", 0.2);
        }
        
        // Throttle suggestion (via intent, not direct action)
        if (prediction.risk == Risk.CRITICAL && 
            now - lastThrottle > THROTTLE_COOLDOWN_MS) {
            
            logger.logEvent("pulse_action", Map.of(
                "action", "THROTTLE_SUGGEST",
                "reason", prediction.reason,
                "safe", true
            ));
            
            // Publish throttle intent (let kernel decide)
            Intent throttleIntent = Intent.builder()
                .type(Intent.Type.HEARTBEAT)
                .priority(Intent.Priority.HIGH)
                .origin("BrainPulse")
                .payload(Map.of(
                    "action", "THROTTLE",
                    "reason", prediction.reason,
                    "memoryUsage", sample.memoryUsage,
                    "cpuUsage", sample.cpuUsage
                ))
                .build();
            
            intentBus.publish(throttleIntent);
            lastThrottle = now;
            
            brain.injectThought("ACTION: Requested throttle - " + prediction.reason, 0.5);
        }
        
        // Prefetch suggestion (when idle)
        if (sample.cpuUsage < 0.2 && sample.memoryUsage < 0.5 && 
            prediction.risk == Risk.LOW) {
            
            // Good time for background work - just log, don't spam
            if (history.size() % 30 == 0) { // Every 30 beats
                logger.logEvent("pulse_opportunity", Map.of(
                    "type", "PREFETCH_WINDOW",
                    "cpuUsage", sample.cpuUsage,
                    "memoryUsage", sample.memoryUsage
                ));
            }
        }
    }

    private void addToHistory(ResourceSample sample) {
        history.add(sample);
        while (history.size() > HISTORY_LIMIT) {
            history.remove(0);
        }
    }

    // Configuration
    public void setMemoryWarningThreshold(double threshold) {
        this.memoryWarningThreshold = threshold;
    }

    public void setMemoryCriticalThreshold(double threshold) {
        this.memoryCriticalThreshold = threshold;
    }

    public void setCpuWarningThreshold(double threshold) {
        this.cpuWarningThreshold = threshold;
    }

    public void setCpuCriticalThreshold(double threshold) {
        this.cpuCriticalThreshold = threshold;
    }

    public List<ResourceSample> getHistory() {
        return Collections.unmodifiableList(history);
    }

    // KernelService implementation
    @Override
    public String getName() { return "BrainPulse"; }
    
    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public void start() {
        if (running) return;
        running = true;
        startTime = System.currentTimeMillis();
        
        pulse.scheduleAtFixedRate(this::heartbeat, 0, pulseIntervalMs, TimeUnit.MILLISECONDS);
        
        System.out.println("💓 BrainPulse started (" + (1000 / pulseIntervalMs) + " Hz)");
    }

    @Override
    public void stop() {
        running = false;
        pulse.shutdown();
        try {
            pulse.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("💓 BrainPulse stopped");
    }

    @Override
    public void restart() {
        // Can't restart a shutdown executor - would need to recreate
        System.out.println("💓 BrainPulse restart not supported");
    }

    @Override
    public ServiceStatus getStatus() {
        return running ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
    }

    @Override
    public HealthReport getHealth() {
        long uptime = running ? System.currentTimeMillis() - startTime : 0;
        
        if (!running) {
            return HealthReport.unhealthy(ServiceStatus.STOPPED, "Not running", 0);
        }
        
        ResourceSample latest = history.isEmpty() ? null : history.get(history.size() - 1);
        Map<String, Object> details = Map.of(
            "sampleCount", history.size(),
            "latestMemory", latest != null ? latest.memoryUsage : 0.0,
            "latestCpu", latest != null ? latest.cpuUsage : 0.0
        );
        
        return new HealthReport(ServiceStatus.RUNNING, true, "OK", uptime, details);
    }

    @Override
    public ServiceMetrics getMetrics() {
        return ServiceMetrics.empty();
    }

    // Data classes
    public record ResourceSample(
        long timestamp,
        double cpuUsage,
        double memoryUsage,
        double heapUsedMb,
        double heapMaxMb,
        int threadCount,
        int availableProcessors
    ) {}

    public record Prediction(
        Trend cpuTrend,
        Trend memTrend,
        Risk risk,
        String reason
    ) {}

    public enum Trend { RISING, STABLE, FALLING }
    public enum Risk { LOW, MEDIUM, HIGH, CRITICAL }
}
