package fraynix.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * INTENT BUS: The nervous system of Fraynix.
 * 
 * All components communicate through the IntentBus.
 * Supports publish/subscribe, request/response, and filtering.
 */
public interface IntentBus {

    /**
     * Publish an intent without waiting for response.
     * Fire-and-forget pattern.
     */
    void publish(Intent intent);

    /**
     * Request/response pattern.
     * Returns a future that completes when the intent is processed.
     */
    CompletableFuture<IntentResult> request(Intent intent);

    /**
     * Request with timeout.
     */
    CompletableFuture<IntentResult> request(Intent intent, long timeoutMs);

    /**
     * Subscribe to intents of a specific type.
     */
    Subscription subscribe(Intent.Type type, Consumer<Intent> handler);

    /**
     * Subscribe with a filter predicate.
     */
    Subscription subscribe(Predicate<Intent> filter, Consumer<Intent> handler);

    /**
     * Subscribe to all intents (for logging/monitoring).
     */
    Subscription subscribeAll(Consumer<Intent> handler);

    /**
     * Register a handler that can respond to intents.
     */
    void registerHandler(Intent.Type type, IntentHandler handler);

    /**
     * Get pending intents in the queue.
     */
    List<Intent> getPendingIntents();

    /**
     * Get recent intent history (for replay/debugging).
     */
    List<Intent> getHistory(int limit);

    /**
     * Get intents by trace ID (for distributed tracing).
     */
    List<Intent> getByTraceId(String traceId);

    /**
     * Metrics
     */
    long getPublishedCount();
    long getProcessedCount();
    long getFailedCount();
    long getAverageLatencyMs();

    /**
     * Lifecycle
     */
    void start();
    void stop();
    boolean isRunning();

    /**
     * Result of an intent request.
     */
    record IntentResult(
        String intentId,
        boolean success,
        Object data,
        String error,
        long latencyMs
    ) {
        public static IntentResult success(String intentId, Object data, long latencyMs) {
            return new IntentResult(intentId, true, data, null, latencyMs);
        }
        
        public static IntentResult failure(String intentId, String error, long latencyMs) {
            return new IntentResult(intentId, false, null, error, latencyMs);
        }
    }

    /**
     * Handler interface for processing intents.
     */
    @FunctionalInterface
    interface IntentHandler {
        IntentResult handle(Intent intent);
    }

    /**
     * Subscription handle for unsubscribing.
     */
    interface Subscription {
        void unsubscribe();
        boolean isActive();
    }
}
