package fraymus;

import com.sun.net.httpserver.HttpServer;
import fraymus.core.FraymusJSON;
import fraymus.core.OllamaBridge;
import fraymus.llm.LocalModelCatalog;
import fraymus.llm.OpenModelRegistry;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Standalone regression: run on a classpath containing no third-party JARs. */
public final class OllamaJsonStartupRegressionTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        try {
            Class.forName("org.json.JSONObject");
            throw new AssertionError("Regression must run without org.json on the classpath");
        } catch (ClassNotFoundException expected) { checks++; }
        Path fixture = Path.of(args[0]);
        Files.createDirectories(fixture);
        Path catalog = fixture.resolve("catalog.json");
        String old = System.getProperty("fraynix.localModelCatalog");
        System.setProperty("fraynix.localModelCatalog", catalog.toString());
        try {
            checkCatalog(catalog);
            checkProtocol(catalog);
        } finally {
            if (old == null) System.clearProperty("fraynix.localModelCatalog");
            else System.setProperty("fraynix.localModelCatalog", old);
        }
        System.out.println("PASS: " + checks + " Ollama JSON startup/protocol checks; org.json absent");
    }

    private static void checkCatalog(Path catalog) throws Exception {
        String primary = "http://127.0.0.1:11434";
        String archive = "http://127.0.0.1:11435";
        eq(List.of(), LocalModelCatalog.load(catalog), "missing catalog has no installed claims");
        eq(List.of(primary), LocalModelCatalog.endpoints(primary), "missing catalog preserves primary");
        eq(primary, LocalModelCatalog.endpointForModel("absent", primary), "missing catalog fallback");
        eq(Map.of(), LocalModelCatalog.metadataForModel("absent"), "missing metadata");
        eq(Integer.MAX_VALUE, LocalModelCatalog.parameterBillions("absent"), "unknown size");
        Files.writeString(catalog, """
            {"stores":[{"endpoint":"http://127.0.0.1:11435"},
                       {"endpoint":"http://127.0.0.1:11435"},
                       {"endpoint":"https://remote.example"},null],
             "models":[
              {"name":"custom:3b","status":"LOCAL_INSTALLED","family":"phi \u03c6","configuredContext":16384,"contextLength":32768,"parameterSize":"3.2B"},
              {"name":"custom:8b","status":"LOCAL_INSTALLED","contextLength":"65536","capabilities":["tools","vision"],"parameterSize":"8000M"},
              {"name":"archive:latest","status":"LOCAL_INSTALLED","endpoint":"http://127.0.0.1:11435"},
              {"name":"shared:latest","status":"LOCAL_INSTALLED","endpoint":"http://127.0.0.1:11434"},
              {"name":"shared:latest","status":"LOCAL_INSTALLED","endpoint":"http://127.0.0.1:11435"},
              {"name":"remote:latest","status":"LOCAL_INSTALLED","endpoint":"https://remote.example"},
              {"name":"proxy:latest","status":"LOCAL_INSTALLED","remoteHost":"https://remote.example"},
              {"name":"weights.gguf","status":"DISCOVERED_NOT_ACTIVATED"},null,42,{"name":17,"status":"LOCAL_INSTALLED"}]}
            """, StandardCharsets.UTF_8);
        var models = LocalModelCatalog.load(catalog);
        eq(4, models.size(), "installed valid records only, deduplicated");
        eq("custom:3b", models.get(0).variant(), "exact variant retained");
        eq("custom:8b", models.get(1).variant(), "tags remain distinct");
        eq(16384, models.get(0).contextTokens(), "configured context wins");
        eq(65536, models.get(1).contextTokens(), "advertised string context");
        eq("Local/phi \u03c6", models.get(0).family(), "UTF-8 catalog metadata");
        eq("text+vision", models.get(1).modality(), "vision capability");
        eq(0.7, models.get(1).profile().toolUse(), "tools capability");
        eq(4, LocalModelCatalog.parameterBillions("custom:3b"), "fractional billions round up");
        eq(8, LocalModelCatalog.parameterBillions("custom:8b"), "millions convert to billions");
        eq(List.of(primary, archive), LocalModelCatalog.endpoints(primary), "loopback stores, stable deduplication");
        eq(primary, LocalModelCatalog.endpointForModel("shared:latest", archive), "first store wins duplicate tags");
        eq(archive, LocalModelCatalog.endpointForModel("archive:latest", primary), "additional store routed");
        eq(primary, LocalModelCatalog.endpointForModel("remote:latest", primary), "remote endpoint rejected");
        eq("3.2B", LocalModelCatalog.metadataForModel("custom:3b").get("parameterSize"), "ordinary Java metadata");
        var registry = new OpenModelRegistry();
        eq(4L, registry.all().stream().filter(d -> d.id().startsWith("local-ollama/")).count(), "registry uses installed catalog");
        eq("custom:8b", OllamaIntegration.selectLocalModel("custom:8b", List.of("custom:3b", "custom:8b")), "exact model selection");
        eq("", OllamaIntegration.selectLocalModel("missing", List.of()), "no installed models");
        var resolver = OllamaBridge.class.getDeclaredMethod("resolveAvailableModel", String.class, List.class);
        resolver.setAccessible(true);
        eq("qwen3-coder:latest", resolver.invoke(null, "coding", List.of("qwen3-coder:latest", "glm-4.7-flash:latest")), "boot bridge role resolution");
        for (String unsafe : List.of("https://localhost:11435", "http://remote.example:11435",
                "http://user@localhost:11435", "http://127.0.0.1:11435/api", "http://127.0.0.1:11435?q=1",
                "http://127.0.0.1:11435#fragment", "file:///tmp/catalog")) {
            Files.writeString(catalog, FraymusJSON.stringify(Map.of("stores", List.of(Map.of("endpoint", unsafe)))));
            eq(List.of(primary), LocalModelCatalog.endpoints(primary), "unsafe store rejected: " + unsafe);
        }
        for (String malformed : List.of("{", "[]", "null", "{\"models\":[]} trailing", "{\"models\":[],\"models\":[]}")) {
            Files.writeString(catalog, malformed);
            boolean rejected = false;
            try { LocalModelCatalog.load(catalog); } catch (IllegalArgumentException expected) { rejected = true; }
            check(rejected, "malformed catalog rejected");
            eq(List.of(primary), LocalModelCatalog.endpoints(primary), "malformed catalog fallback");
            eq(primary, LocalModelCatalog.endpointForModel("custom:3b", primary), "malformed routing fallback");
            eq(Integer.MAX_VALUE, LocalModelCatalog.parameterBillions("custom:3b"), "malformed size unknown");
        }
        check(registry.all().stream().noneMatch(d -> d.id().startsWith("local-ollama/")), "invalid catalog clears installed claims");
        for (String size : List.of("NaNB", "InfinityB", "-1B", "0B", "unknown")) {
            Files.writeString(catalog, FraymusJSON.stringify(Map.of("models", List.of(Map.of("name", "invalid:latest", "status", "LOCAL_INSTALLED", "parameterSize", size)))));
            eq(Integer.MAX_VALUE, LocalModelCatalog.parameterBillions("invalid:latest"), "invalid size cannot bypass small-model policy");
        }
    }

    private static void checkProtocol(Path catalog) throws Exception {
        HttpServer primary = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        HttpServer archive = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        AtomicReference<String> tags = new AtomicReference<>("""
            {"models":[{"name":"local:3b"},{"name":"local:8b"},
             {"name":"proxy:latest","remote_host":"https://remote.example"},
             {"name":"proxy2:latest","remote_model":"remote"},{"name":"proxy3-cloud"},
             {"name":"local:3b"},null,{"name":123}]}
            """);
        AtomicReference<String> generation = new AtomicReference<>("{\"response\":\"He said \\\"hello\\\".\\nNext line.\",\"done\":true}");
        AtomicReference<String> chat = new AtomicReference<>("{\"message\":{\"content\":\"Use \\\"quotes\\\" safely.\"}}");
        AtomicReference<String> request = new AtomicReference<>();
        AtomicInteger status = new AtomicInteger(200);
        primary.createContext("/api/tags", x -> reply(x, 200, tags.get()));
        primary.createContext("/api/generate", x -> {
            request.set(new String(x.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            reply(x, status.get(), generation.get());
        });
        primary.createContext("/api/chat", x -> {
            request.set(new String(x.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            reply(x, status.get(), chat.get());
        });
        archive.createContext("/api/tags", x -> reply(x, 200, "{\"models\":[{\"name\":\"archive:latest\"}]}"));
        archive.createContext("/api/generate", x -> { x.getRequestBody().readAllBytes(); reply(x, 200, "{\"response\":\"ARCHIVE_OK\"}"); });
        primary.start();
        archive.start();
        try {
            String endpoint = "http://127.0.0.1:" + primary.getAddress().getPort();
            String second = "http://127.0.0.1:" + archive.getAddress().getPort();
            Files.writeString(catalog, FraymusJSON.stringify(Map.of("stores", List.of(Map.of("endpoint", second)),
                "models", List.of(Map.of("name", "archive:latest", "status", "LOCAL_INSTALLED", "endpoint", second)))));
            var api = new OllamaIntegration(false, endpoint);
            check(api.testConnection(), "local daemon health");
            eq(5, api.listModels().size(), "general listing deduplicates model names");
            eq(List.of("local:3b", "local:8b"), api.listLocalModels(), "remote proxies excluded from installed models");
            eq("local:8b", api.resolveLocalModel("local:8b"), "explicit tag survives resolution");
            var include = OllamaIntegration.class.getDeclaredField("includeCatalogEndpoints");
            include.setAccessible(true);
            include.setBoolean(api, true);
            eq(List.of("local:3b", "local:8b", "archive:latest"), api.listLocalModels(), "public discovery visits multiple stores");
            eq("ARCHIVE_OK", new OllamaIntegration(false).generate("archive:latest", "test", false), "public client routes to catalog store");
            String prompt = "quotes \" and \\; newline\n tab\t control\u0001 backspace\b formfeed\f phi \u03c6";
            var options = Map.<String, Object>of("temperature", 0.25, "num_ctx", 8192, "stop", List.of("end\""));
            eq("He said \"hello\".\nNext line.", api.generate("local:3b", prompt, false, options, "json"), "generation decodes escapes");
            Map<?, ?> sent = (Map<?, ?>) FraymusJSON.parseStrict(request.get());
            eq(prompt, sent.get("prompt"), "request preserves control and Unicode characters");
            eq("local:3b", sent.get("model"), "request model");
            eq(Boolean.FALSE, sent.get("stream"), "request stream flag");
            eq("json", sent.get("format"), "structured response format");
            eq(8192L, ((Map<?, ?>) sent.get("options")).get("num_ctx"), "numeric options");
            eq(List.of("end\""), ((Map<?, ?>) sent.get("options")).get("stop"), "array options");
            eq("Use \"quotes\" safely.", api.chat("local:3b", List.of(Map.of("role", "user", "content", prompt)), false, options, "json"), "chat decodes escapes");
            sent = (Map<?, ?>) FraymusJSON.parseStrict(request.get());
            eq(prompt, ((Map<?, ?>) ((List<?>) sent.get("messages")).get(0)).get("content"), "chat request content");
            eq("json", sent.get("format"), "chat response format");
            generation.set("{\"response\":\"Hello \"}\n{\"response\":\"world\"}\n{\"done\":true}\n");
            eq("Hello world", api.generate("local:3b", "stream", true), "streaming generation, including done frame");
            chat.set("{\"message\":{\"content\":\"one \"}}\n{\"message\":{\"content\":\"two\"}}\n{\"done\":true}\n");
            eq("one two", api.chat("local:3b", List.of(Map.of("role", "user", "content", "stream")), true), "streaming chat, including done frame");
            generation.set("{\"error\":\"model unavailable\"}");
            eq("ERROR: model unavailable", api.generate("local:3b", "error", false), "daemon error propagated");
            status.set(503);
            check(api.generate("local:3b", "error", false).startsWith("ERROR 503:"), "HTTP error propagated");
            status.set(200);
            for (String malformed : List.of("[]", "{\"response\":\"one\",\"response\":\"two\"}", "{\"response\":\"ok\"} trailing", "{")) {
                generation.set(malformed);
                check(api.generate("local:3b", "invalid", false).startsWith("ERROR:"), "malformed reply cannot look successful");
            }
            tags.set("{");
            include.setBoolean(api, false);
            eq(List.of(), api.listLocalModels(), "bad tags response degrades without linkage error");
        } finally {
            primary.stop(0);
            archive.stop(0);
        }
    }

    private static void reply(com.sun.net.httpserver.HttpExchange exchange, int status, String text) throws java.io.IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) { output.write(bytes); }
        exchange.close();
    }

    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        checks++;
    }

    private static void eq(Object expected, Object actual, String label) {
        check(Objects.equals(expected, actual), label + ": expected=" + expected + ", actual=" + actual);
    }
}
