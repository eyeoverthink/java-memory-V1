package fraymus.factory.models;

import fraymus.llm.LocalModelCatalog;
import fraymus.model.LocalOllamaModelAdapter;
import fraymus.model.ModelAdapter;
import org.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

/** Observes live local services; inference uses the existing Fraynix model adapter. */
public final class FactoryModelService {
    private final String primary;
    public FactoryModelService() { this("http://127.0.0.1:11434"); }
    public FactoryModelService(String primary) {
        if (!loopback(primary)) throw new IllegalArgumentException("Only local endpoints are supported");
        this.primary = primary;
    }
    public record Store(String endpoint, boolean online, String status, int advertised, int loaded) {}
    public record Model(String name, String endpoint, String family, String parameters, String quantization,
                        long bytes, long vramBytes, int context, boolean available, boolean loaded, String source) {}
    public record Snapshot(Instant observedAt, List<Store> stores, List<Model> models, String warning) {
        public Snapshot { stores = List.copyOf(stores); models = List.copyOf(models); }
    }
    public record Reply(String model, String endpoint, String text, boolean success, long millis, Instant at) {}

    public Snapshot observe() {
        var metadata = new LinkedHashMap<String, JSONObject>();
        var endpoints = new LinkedHashSet<>(LocalModelCatalog.endpoints(primary));
        String warning = "";
        try {
            if (Files.isRegularFile(LocalModelCatalog.defaultPath())) {
                var array = new JSONObject(Files.readString(LocalModelCatalog.defaultPath())).optJSONArray("models");
                if (array != null) for (int i = 0; i < array.length(); i++) {
                    var row = array.getJSONObject(i);
                    if ("LOCAL_INSTALLED".equals(row.optString("status")) && !proxy(row)) {
                        String endpoint = row.optString("endpoint", "http://127.0.0.1:11434");
                        if (loopback(endpoint)) { metadata.putIfAbsent(row.optString("name"), row); endpoints.add(endpoint); }
                    }
                }
            }
        } catch (Exception error) { warning = "Catalog: " + error.getMessage(); }
        var stores = new ArrayList<Store>();
        var live = new LinkedHashMap<String, JSONObject>();
        var loaded = new HashMap<String, JSONObject>();
        for (String endpoint : endpoints) {
            try {
                JSONArray tags = get(endpoint, "/api/tags").getJSONArray("models");
                int local = 0;
                for (int i = 0; i < tags.length(); i++) {
                    var tag = tags.getJSONObject(i);
                    if (proxy(tag)) continue;
                    String name = tag.optString("name"); local++;
                    live.put(endpoint + "\n" + name, tag);
                    // Unknown tags on a secondary store need a catalog mapping before the adapter can route them.
                    if (LocalModelCatalog.endpointForModel(name, "http://127.0.0.1:11434").equals(endpoint)) {
                        metadata.putIfAbsent(name, new JSONObject().put("name", name).put("endpoint", endpoint));
                    }
                }
                int resident = 0;
                String state = "Online";
                try {
                    JSONArray ps = get(endpoint, "/api/ps").getJSONArray("models"); resident = ps.length();
                    for (int i = 0; i < ps.length(); i++) {
                        var item = ps.getJSONObject(i); loaded.put(endpoint + "\n" + item.optString("name"), item);
                    }
                } catch (Exception error) { state = "Online; residency unavailable: " + error.getMessage(); }
                stores.add(new Store(endpoint, true, state, local, resident));
            } catch (Exception error) { stores.add(new Store(endpoint, false, error.getMessage(), 0, 0)); }
        }
        var models = new ArrayList<Model>();
        for (var entry : metadata.entrySet()) {
            var row = entry.getValue(); String name = entry.getKey();
            String endpoint = LocalModelCatalog.endpointForModel(name, row.optString("endpoint", "http://127.0.0.1:11434"));
            String key = endpoint + "\n" + name;
            JSONObject tag = live.get(key), ps = loaded.get(key);
            JSONObject details = tag == null ? new JSONObject() : tag.optJSONObject("details");
            if (details == null) details = new JSONObject();
            models.add(new Model(name, endpoint, row.optString("family", details.optString("family", "unknown")),
                row.optString("parameterSize", details.optString("parameter_size", "unmeasured")),
                row.optString("quantization", details.optString("quantization_level", "unknown")),
                tag == null ? row.optLong("sizeBytes") : tag.optLong("size"), ps == null ? 0 : ps.optLong("size_vram"),
                ps == null ? row.optInt("configuredContext", row.optInt("contextLength")) : ps.optInt("context_length"),
                tag != null, ps != null, row.optString("sourcePath", "Advertised by " + endpoint)));
        }
        models.sort(Comparator.comparing(Model::name, String.CASE_INSENSITIVE_ORDER));
        return new Snapshot(Instant.now(), stores, models, warning);
    }

    public Reply chat(Model model, String prompt, List<ModelAdapter.Message> history) {
        return chat(model, prompt, history, Map.of());
    }
    public Reply chat(Model model, String prompt, List<ModelAdapter.Message> history, Map<String, Object> parameters) {
        if (!model.available() || !loopback(model.endpoint())) throw new IllegalArgumentException("Select an available local model");
        String routed = LocalModelCatalog.endpointForModel(model.name(), "http://127.0.0.1:11434");
        if (!routed.equals(model.endpoint())) throw new IllegalStateException("Model routing changed; refresh the model list");
        long start = System.nanoTime();
        var response = new LocalOllamaModelAdapter(model.name()).infer(new ModelAdapter.InferenceRequest(
            model.name(), prompt, parameters, List.of(), history));
        return new Reply(model.name(), model.endpoint(), response.isSuccess() ? response.getContent() : response.getError(),
            response.isSuccess(), (System.nanoTime() - start) / 1_000_000, Instant.now());
    }

    private static JSONObject get(String endpoint, String path) throws IOException {
        if (!loopback(endpoint)) throw new IOException("Only local endpoints are supported");
        HttpURLConnection connection = (HttpURLConnection) URI.create(endpoint + path).toURL().openConnection();
        connection.setConnectTimeout(1500); connection.setReadTimeout(2500);
        try {
            if (connection.getResponseCode() != 200) throw new IOException("HTTP " + connection.getResponseCode());
            try (var input = connection.getInputStream()) { return new JSONObject(new String(input.readAllBytes(), StandardCharsets.UTF_8)); }
        } finally { connection.disconnect(); }
    }
    private static boolean loopback(String endpoint) {
        try {
            var uri = URI.create(endpoint);
            return "http".equals(uri.getScheme()) && uri.getUserInfo() == null && uri.getQuery() == null && uri.getFragment() == null
                && (uri.getPath() == null || uri.getPath().isEmpty()) && Set.of("localhost", "127.0.0.1", "[::1]", "::1").contains(uri.getHost());
        } catch (Exception error) { return false; }
    }
    private static boolean proxy(JSONObject model) {
        return !model.optString("remote_host", model.optString("remoteHost", "")).isBlank()
            || "REMOTE_PROXY".equals(model.optString("status")) || model.optString("name").toLowerCase(Locale.ROOT).contains("cloud");
    }
}
