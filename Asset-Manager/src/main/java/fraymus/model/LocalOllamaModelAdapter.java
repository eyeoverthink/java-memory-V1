package fraymus.model;

import fraymus.OllamaIntegration;
import fraymus.llm.LocalModelCatalog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;

/** Runnable adapter for an exact observed model tag, including additional local drives. */
public final class LocalOllamaModelAdapter implements ModelAdapter {
    private final String model;
    private final OllamaIntegration integration = new OllamaIntegration(false);
    private final LongAdder calls = new LongAdder();
    private final LongAdder successes = new LongAdder();
    private volatile boolean enabled = true;

    public LocalOllamaModelAdapter(String model) { this.model = model; }

    @Override public InferenceResponse infer(InferenceRequest request) {
        if (!enabled) return InferenceResponse.error(model, "Adapter stopped");
        if (!model.equals(request.getModelId())) return InferenceResponse.error(model, "Model tag does not match this adapter");
        if (!request.getTools().isEmpty()) return InferenceResponse.error(model, "Structured tool calls are not implemented by this text adapter");
        calls.increment();
        long start = System.nanoTime();
        Map<String, Object> options = request.getParameters();
        Object format = options.remove("response_format");
        if (format != null && !"json".equals(format) && !(format instanceof Map<?,?>)) return InferenceResponse.error(model, "Supported response_format: json or a JSON schema object");
        Object responseFormat = format;
        String text;
        if (request.getHistory().isEmpty()) {
            text = integration.generateWithFormat(model, request.getPrompt(), false, options, responseFormat);
        } else {
            List<Map<String, String>> messages = new ArrayList<>();
            for (Message message : request.getHistory()) {
                messages.add(Map.of("role", message.getRole().name().toLowerCase(Locale.ROOT), "content", message.getContent()));
            }
            messages.add(Map.of("role", "user", "content", request.getPrompt()));
            text = integration.chatWithFormat(model, messages, false, options, responseFormat);
        }
        long millis = (System.nanoTime() - start) / 1_000_000;
        if (text == null || text.isBlank() || text.startsWith("ERROR")) return InferenceResponse.error(model, text == null ? "No response" : text);
        successes.increment();
        return new InferenceResponse(model, text, List.of(), Map.of("endpoint", endpoint(), "backend", "ollama-local"), millis, true, null);
    }

    @Override public CompletableFuture<InferenceResponse> inferAsync(InferenceRequest request) {
        return CompletableFuture.supplyAsync(() -> infer(request));
    }
    @Override public BackendType getBackendType() { return BackendType.OLLAMA; }
    @Override public boolean isAvailable() { return enabled && integration.listLocalModels().contains(model); }
    @Override public BackendHealth getHealth() {
        boolean available = isAvailable();
        return new BackendHealth(available, 0, calls.sum() == 0 ? 0 : (double) successes.sum() / calls.sum(),
            available ? "Model advertised by a local store; latency is unmeasured until inference" : "Local model unavailable",
            Map.of("endpoint", endpoint(), "calls", calls.sum()));
    }
    @Override public void initialize(Map<String, Object> config) { enabled = true; }
    @Override public void shutdown() { enabled = false; }
    private String endpoint() { return LocalModelCatalog.endpointForModel(model, "http://127.0.0.1:11434"); }
}
