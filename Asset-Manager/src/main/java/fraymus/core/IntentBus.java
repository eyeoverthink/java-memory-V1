package fraymus.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * INTENT BUS - Core communication backbone
 * 
 * Publish/subscribe + request/response with tracing.
 * Replaces traditional message passing with intent-based routing.
 */
public class IntentBus {
    
    private final Map<Intent.Type, List<Consumer<Intent>>> subscribers = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<Intent>> pendingRequests = new ConcurrentHashMap<>();
    private final BlockingQueue<Intent> intentQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor;
    private final AuditLog auditLog;
    
    private volatile boolean running = false;
    
    public IntentBus(AuditLog auditLog) {
        this.auditLog = auditLog;
        this.executor = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "IntentBus-Worker");
            t.setDaemon(true);
            return t;
        });
    }
    
    /**
     * Start the bus
     */
    public void start() {
        if (running) return;
        running = true;
        
        // Start dispatcher thread
        executor.submit(this::dispatchLoop);
    }
    
    /**
     * Stop the bus
     */
    public void stop() {
        running = false;
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
    
    /**
     * Publish an intent (fire and forget)
     */
    public void publish(Intent intent) {
        auditLog.log("intent_published", intent);
        intentQueue.offer(intent);
    }
    
    /**
     * Request/response pattern
     */
    public CompletableFuture<Intent> request(Intent intent, long timeoutMs) {
        CompletableFuture<Intent> future = new CompletableFuture<>();
        pendingRequests.put(intent.id, future);
        
        auditLog.log("intent_requested", intent);
        intentQueue.offer(intent);
        
        // Timeout handler
        executor.submit(() -> {
            try {
                Thread.sleep(timeoutMs);
                if (pendingRequests.containsKey(intent.id)) {
                    pendingRequests.remove(intent.id);
                    future.completeExceptionally(new TimeoutException("Intent timeout: " + intent.id));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        return future;
    }
    
    /**
     * Respond to a request
     */
    public void respond(String requestId, Intent response) {
        CompletableFuture<Intent> future = pendingRequests.remove(requestId);
        if (future != null) {
            future.complete(response);
            auditLog.log("intent_responded", response);
        }
    }
    
    /**
     * Subscribe to intent type
     */
    public void subscribe(Intent.Type type, Consumer<Intent> handler) {
        subscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(handler);
    }
    
    /**
     * Unsubscribe from intent type
     */
    public void unsubscribe(Intent.Type type, Consumer<Intent> handler) {
        List<Consumer<Intent>> handlers = subscribers.get(type);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }
    
    /**
     * Main dispatch loop
     */
    private void dispatchLoop() {
        while (running) {
            try {
                Intent intent = intentQueue.poll(100, TimeUnit.MILLISECONDS);
                if (intent != null) {
                    dispatch(intent);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Dispatch intent to subscribers
     */
    private void dispatch(Intent intent) {
        List<Consumer<Intent>> handlers = subscribers.get(intent.type);
        if (handlers != null && !handlers.isEmpty()) {
            for (Consumer<Intent> handler : handlers) {
                executor.submit(() -> {
                    try {
                        handler.accept(intent);
                        auditLog.log("intent_handled", intent);
                    } catch (Exception e) {
                        auditLog.log("intent_error", intent, e);
                    }
                });
            }
        } else {
            auditLog.log("intent_unhandled", intent);
        }
    }
    
    /**
     * Get statistics
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("queue_size", intentQueue.size());
        stats.put("pending_requests", pendingRequests.size());
        stats.put("subscriber_types", subscribers.size());
        stats.put("running", running);
        return stats;
    }
}
