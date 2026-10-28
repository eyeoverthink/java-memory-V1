package fraymus.llm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.LinkedHashSet;
import java.net.URI;
import fraymus.core.FraymusJSON;

/** Persistent observations of installed models, independent of seed-family predictions. */
public final class LocalModelCatalog {
    private LocalModelCatalog() {}

    public static Path defaultPath() {
        String configured = System.getProperty("fraynix.localModelCatalog",
            System.getenv().getOrDefault("FRAYNIX_LOCAL_MODEL_CATALOG", ""));
        return configured.isBlank()
            ? Path.of(System.getProperty("user.home"), ".fraymus", "models", "local-model-catalog.json")
            : Path.of(configured);
    }

    public static List<ModelDescriptor> load(Path path) throws IOException {
        if (!Files.isRegularFile(path)) return List.of();
        List<?> models = array(readCatalog(path).get("models"));
        List<ModelDescriptor> observed = new ArrayList<>();
        Set<String> names = new LinkedHashSet<>();
        for (Object value : models) {
            Map<String, Object> model = object(value);
            if (!"LOCAL_INSTALLED".equals(text(model, "status", ""))) continue;
            String name = text(model, "name", "");
            if (name.isBlank() || !text(model, "remoteHost", "").isBlank()) continue;
            if (model.containsKey("endpoint") && !isLoopbackEndpoint(text(model, "endpoint", ""))) continue;
            if (!names.add(name)) continue;
            int configuredContext = integer(model.get("configuredContext"));
            int advertisedContext = integer(model.get("contextLength"));
            int context = configuredContext > 0 ? configuredContext : advertisedContext;
            List<?> capabilities = array(model.get("capabilities"));
            boolean vision = contains(capabilities, "vision");
            boolean tools = contains(capabilities, "tools");
            // Neutral priors: model names and packaging do not prove reasoning/coding quality or speed.
            CapabilityProfile profile = new CapabilityProfile(0.5, 0.5,
                Math.min(1.0, Math.max(0, context) / 131072.0), 0.5, 0.5, 0.5,
                tools ? 0.7 : 0.3, vision ? 0.9 : 0.0, 0.8);
            observed.add(new ModelDescriptor("local-ollama/" + name,
                "Local/" + text(model, "family", "unknown"), name, Math.max(0, context),
                "Local artifact; consult its source license", vision ? "text+vision" : "text",
                Set.of(ModelBackend.OLLAMA), text(model, "quantization", "unknown"),
                "unmeasured", "local", profile, Set.of(name)));
        }
        return List.copyOf(observed);
    }

    /** Exact model names route to the first observed store; the primary store wins tag conflicts. */
    public static String endpointForModel(String name, String fallback) {
        try {
            for (Object value : array(readCatalog(defaultPath()).get("models"))) {
                Map<String, Object> model = object(value);
                if (name.equals(text(model, "name", ""))
                        && "LOCAL_INSTALLED".equals(text(model, "status", ""))) {
                    String endpoint = text(model, "endpoint", fallback);
                    if (isLoopbackEndpoint(endpoint)) return endpoint;
                }
            }
        } catch (Exception ignored) { }
        return fallback;
    }

    public static List<String> endpoints(String primary) {
        Set<String> endpoints = new LinkedHashSet<>();
        endpoints.add(primary);
        try {
            for (Object value : array(readCatalog(defaultPath()).get("stores"))) {
                String endpoint = text(object(value), "endpoint", "");
                if (isLoopbackEndpoint(endpoint)) endpoints.add(endpoint);
            }
        } catch (Exception ignored) { }
        return List.copyOf(endpoints);
    }

    /** Metadata uses ordinary Java collections so discovery needs no JSON library. */
    public static Map<String, Object> metadataForModel(String name) {
        try {
            for (Object value : array(readCatalog(defaultPath()).get("models"))) {
                Map<String, Object> model = object(value);
                if (name.equals(text(model, "name", ""))
                        && "LOCAL_INSTALLED".equals(text(model, "status", ""))) {
                    return Collections.unmodifiableMap(model);
                }
            }
        } catch (Exception ignored) { }
        return Map.of();
    }

    public static int parameterBillions(String name) {
        String size = text(metadataForModel(name), "parameterSize", "").trim().toUpperCase(java.util.Locale.ROOT);
        try {
            if (size.endsWith("B") || size.endsWith("M")) {
                double billions = Double.parseDouble(size.substring(0, size.length() - 1))
                    / (size.endsWith("M") ? 1000.0 : 1.0);
                if (Double.isFinite(billions) && billions > 0) return Math.max(1, (int) Math.ceil(billions));
            }
        } catch (NumberFormatException ignored) { }
        return Integer.MAX_VALUE; // Unknown sizes must not pass small-model constraints by pretending to be zero.
    }

    private static boolean isLoopbackEndpoint(String value) {
        try {
            URI uri = URI.create(value);
            String host = uri.getHost();
            return "http".equals(uri.getScheme()) && uri.getUserInfo() == null
                && uri.getQuery() == null && uri.getFragment() == null
                && (uri.getPath() == null || uri.getPath().isEmpty())
                && Set.of("127.0.0.1", "localhost", "[::1]", "::1").contains(host);
        } catch (Exception ignored) { return false; }
    }

    private static Map<String, Object> readCatalog(Path path) throws IOException {
        Object parsed = FraymusJSON.parseStrict(Files.readString(path, StandardCharsets.UTF_8));
        if (!(parsed instanceof Map<?, ?>)) throw new IllegalArgumentException("Model catalog must be a JSON object");
        return object(parsed);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> object(Object value) {
        // parseStrict creates JSON objects with string keys.
        return value instanceof Map<?, ?> ? (Map<String, Object>) value : Map.of();
    }

    private static List<?> array(Object value) {
        return value instanceof List<?> list ? list : List.of();
    }

    private static String text(Map<String, Object> object, String key, String fallback) {
        return object.get(key) instanceof String value ? value : fallback;
    }

    private static int integer(Object value) {
        if (value instanceof Number number) return number.intValue();
        try { return value instanceof String text ? Integer.parseInt(text) : 0; }
        catch (NumberFormatException ignored) { return 0; }
    }

    private static boolean contains(List<?> values, String expected) {
        return values.contains(expected);
    }
}
