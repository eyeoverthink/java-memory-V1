package fraynix.core.impl;

import fraynix.core.*;
import fraynix.observe.EventLogger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * DEFAULT INTENT BUS: Production implementation of IntentBus.
 * 
 * Thread-safe, bounded queues, backpressure support.
 */
public class DefaultIntentBus implements IntentBus, KernelService {

    private final String name = "IntentBus";
    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;
    private final BlockingQueue<Intent> queue;
    private final Map<Intent.Type, List<IntentHandler>> handlers;
    private final Map<String, List<SubscriptionImpl>> typeSubscriptions;
    private final List<SubscriptionImpl> globalSubscriptions;
    private final List<SubscriptionImpl> filterSubscriptions;
    private final Deque<Intent> history;
    private final int historyLimit;
    
    private final AtomicLong publishedCount = new AtomicLong();
    private final AtomicLong processedCount = new AtomicLong();
    private final AtomicLong failedCount = new AtomicLong();
    private final AtomicLong totalLatencyMs = new AtomicLong();
    
    private volatile boolean running = false;
    private long startTime;
    private EventLogger logger;

    private Thread pumpThread;

    public DefaultIntentBus() {
        this(100, 1000, 4);
    }

    public DefaultIntentBus(int queueCapacity, int historyLimit, int workerThreads) {
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.history = new ConcurrentLinkedDeque<>();
        this.historyLimit = historyLimit;
        this.handlers = new ConcurrentHashMap<>();
        this.typeSubscriptions = new ConcurrentHashMap<>();
        this.globalSubscriptions = new CopyOnWriteArrayList<>();
        this.filterSubscriptions = new CopyOnWriteArrayList<>();
        
        this.executor = Executors.newFixedThreadPool(workerThreads, r -> {
            Thread t = new Thread(r, "IntentBus-Worker");
            t.setDaemon(true);
            return t;
        });
        
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "IntentBus-Scheduler");
            t.setDaemon(true);
            return t;
        });
    }

    public void setLogger(EventLogger logger) {
        this.logger = logger;
    }

    @Override
    public void publish(Intent intent) {
        if (!running) return;
        
        publishedCount.incrementAndGet();
        intent.setState(Intent.IntentState.QUEUED);
        
        // Log
        if (logger != null) {
            logger.logEvent("intent_received", Map.of(
                "id", intent.getId(),
                "type", intent.getType().name(),
                "priority", intent.getPriority().name(),
                "origin", intent.getOrigin()
            ));
        }
        
        // Try to queue, drop if full (backpressure)
        if (!queue.offer(intent)) {
            intent.setError("Queue full - backpressure applied");
            failedCount.incrementAndGet();
            if (logger != null) {
                logger.logEvent("intent_dropped", Map.of("id", intent.getId(), "reason", "queue_full"));
            }
        }
    }

    @Override
    public CompletableFuture<IntentResult> request(Intent intent) {
        return request(intent, 30000); // 30s default timeout
    }

    @Override
    public CompletableFuture<IntentResult> request(Intent intent, long timeoutMs) {
        CompletableFuture<IntentResult> future = new CompletableFuture<>();
        
        long startTime = System.currentTimeMillis();
        
        executor.submit(() -> {
            try {
                IntentResult result = processIntent(intent);
                long latency = System.currentTimeMillis() - startTime;
                totalLatencyMs.addAndGet(latency);
                future.complete(result);
            } catch (Exception e) {
                long latency = System.currentTimeMillis() - startTime;
                future.complete(IntentResult.failure(intent.getId(), e.getMessage(), latency));
            }
        });
        
        // Timeout handling
        scheduler.schedule(() -> {
            if (!future.isDone()) {
                future.complete(IntentResult.failure(intent.getId(), "Timeout", timeoutMs));
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);
        
        return future;
    }

    @Override
    public Subscription subscribe(Intent.Type type, Consumer<Intent> handler) {
        SubscriptionImpl sub = new SubscriptionImpl(handler);
        typeSubscriptions.computeIfAbsent(type.name(), k -> new CopyOnWriteArrayList<>()).add(sub);
        return sub;
    }

    @Override
    public Subscription subscribe(Predicate<Intent> filter, Consumer<Intent> handler) {
        SubscriptionImpl sub = new SubscriptionImpl(handler, filter);
        filterSubscriptions.add(sub);
        return sub;
    }

    @Override
    public Subscription subscribeAll(Consumer<Intent> handler) {
        SubscriptionImpl sub = new SubscriptionImpl(handler);
        globalSubscriptions.add(sub);
        return sub;
    }

    @Override
    public void registerHandler(Intent.Type type, IntentHandler handler) {
        handlers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public List<Intent> getPendingIntents() {
        return new ArrayList<>(queue);
    }

    @Override
    public List<Intent> getHistory(int limit) {
        List<Intent> result = new ArrayList<>();
        Iterator<Intent> it = history.descendingIterator();
        while (it.hasNext() && result.size() < limit) {
            result.add(it.next());
        }
        return result;
    }

    @Override
    public List<Intent> getByTraceId(String traceId) {
        return history.stream()
            .filter(i -> traceId.equals(i.getTraceId()))
            .toList();
    }

    @Override
    public long getPublishedCount() { return publishedCount.get(); }
    
    @Override
    public long getProcessedCount() { return processedCount.get(); }
    
    @Override
    public long getFailedCount() { return failedCount.get(); }
    
    @Override
    public long getAverageLatencyMs() {
        long processed = processedCount.get();
        return processed == 0 ? 0 : totalLatencyMs.get() / processed;
    }

    @Override
    public void start() {
        if (running) return;
        running = true;
        startTime = System.currentTimeMillis();
        
        // Start queue pump thread (avoid starvation if worker pool is saturated)
        pumpThread = new Thread(this::processQueue, "IntentBus-Pump");
        pumpThread.setDaemon(true);
        pumpThread.start();

        int capacity = queue.size() + queue.remainingCapacity();
        System.out.println("🚌 IntentBus started (queue_capacity=" + capacity + ")");
    }

    @Override
    public void stop() {
        running = false;

        if (pumpThread != null) {
            pumpThread.interrupt();
        }
        executor.shutdown();
        scheduler.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("🚌 IntentBus stopped");
    }

    @Override
    public boolean isRunning() { return running; }

    private void processQueue() {
        while (running) {
            try {
                Intent intent = queue.poll(100, TimeUnit.MILLISECONDS);
                if (intent != null) {
                    executor.submit(() -> processIntent(intent));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private IntentResult processIntent(Intent intent) {
        long start = System.currentTimeMillis();
        intent.setState(Intent.IntentState.PROCESSING);
        
        try {
            // Notify global subscribers
            for (SubscriptionImpl sub : globalSubscriptions) {
                if (sub.isActive()) sub.handler.accept(intent);
            }
            
            // Notify type subscribers
            List<SubscriptionImpl> typeSubs = typeSubscriptions.get(intent.getType().name());
            if (typeSubs != null) {
                for (SubscriptionImpl sub : typeSubs) {
                    if (sub.isActive()) sub.handler.accept(intent);
                }
            }
            
            // Notify filter subscribers
            for (SubscriptionImpl sub : filterSubscriptions) {
                if (sub.isActive() && (sub.filter == null || sub.filter.test(intent))) {
                    sub.handler.accept(intent);
                }
            }
            
            // Process with handlers
            List<IntentHandler> typeHandlers = handlers.get(intent.getType());
            IntentResult result = null;
            
            if (typeHandlers != null && !typeHandlers.isEmpty()) {
                for (IntentHandler handler : typeHandlers) {
                    result = handler.handle(intent);
                    if (result != null && result.success()) break;
                }
            }
            
            long latency = System.currentTimeMillis() - start;
            
            if (result == null) {
                result = IntentResult.success(intent.getId(), null, latency);
            }
            
            // Update state
            if (result.success()) {
                intent.setResult(result.data() != null ? result.data().toString() : "OK");
                processedCount.incrementAndGet();
            } else {
                intent.setError(result.error());
                failedCount.incrementAndGet();
            }
            
            // Add to history
            addToHistory(intent);
            
            // Log
            if (logger != null) {
                logger.logEvent("intent_processed", Map.of(
                    "id", intent.getId(),
                    "type", intent.getType().name(),
                    "success", result.success(),
                    "latency_ms", latency
                ));
            }
            
            return result;
            
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - start;
            intent.setError(e.getMessage());
            failedCount.incrementAndGet();
            addToHistory(intent);
            return IntentResult.failure(intent.getId(), e.getMessage(), latency);
        }
    }

    private void addToHistory(Intent intent) {
        history.addLast(intent);
        while (history.size() > historyLimit) {
            history.removeFirst();
        }
    }

    // KernelService implementation
    @Override
    public String getName() { return name; }
    
    @Override
    public String getVersion() { return "1.0.0"; }
    
    @Override
    public void restart() { stop(); start(); }
    
    @Override
    public ServiceStatus getStatus() {
        return running ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
    }
    
    @Override
    public HealthReport getHealth() {
        long uptime = running ? System.currentTimeMillis() - startTime : 0;
        int queueSize = queue.size();
        int capacity = queueSize + queue.remainingCapacity();
        
        if (!running) {
            return HealthReport.unhealthy(ServiceStatus.STOPPED, "Not running", 0);
        }
        
        if (capacity > 0 && queueSize > capacity * 0.9) {
            return HealthReport.degraded("Queue near capacity", uptime, Map.of("queueSize", queueSize));
        }
        
        return HealthReport.healthy(ServiceStatus.RUNNING, uptime);
    }
    
    @Override
    public ServiceMetrics getMetrics() {
        return new ServiceMetrics(
            publishedCount.get(),
            processedCount.get(),
            failedCount.get(),
            getAverageLatencyMs(),
            0, // p99 would need histogram
            0.0,
            0,
            Map.of("queueSize", (long) queue.size(), "historySize", (long) history.size())
        );
    }

    private static class SubscriptionImpl implements Subscription {
        final Consumer<Intent> handler;
        final Predicate<Intent> filter;
        volatile boolean active = true;

        SubscriptionImpl(Consumer<Intent> handler) {
            this(handler, null);
        }

        SubscriptionImpl(Consumer<Intent> handler, Predicate<Intent> filter) {
            this.handler = handler;
            this.filter = filter;
        }

        @Override
        public void unsubscribe() { active = false; }
        
        @Override
        public boolean isActive() { return active; }
    }
}
