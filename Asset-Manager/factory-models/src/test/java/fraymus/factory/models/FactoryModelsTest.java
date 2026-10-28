package fraymus.factory.models;

import com.sun.net.httpserver.HttpServer;
import fraymus.model.ModelAdapter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/** Exercises actual HTTP parsing, exact-tag adapter routing, failure state and restart persistence. */
public final class FactoryModelsTest {
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("factory-model-test-");
        String previous = System.getProperty("fraynix.localModelCatalog");
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        var request = new AtomicReference<String>();
        var fail = new java.util.concurrent.atomic.AtomicBoolean();
        String endpoint = "http://127.0.0.1:" + server.getAddress().getPort();
        server.createContext("/api/tags", exchange -> {
            byte[] bytes = (fail.get() ? "unavailable" : "{\"models\":[{\"name\":\"fraynix-test:3b\",\"size\":4096},{\"name\":\"remote:cloud\",\"remote_host\":\"https://remote.invalid\"}]}").getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(fail.get() ? 503 : 200, bytes.length); exchange.getResponseBody().write(bytes); exchange.close();
        });
        server.createContext("/api/ps", exchange -> {
            byte[] bytes = "{\"models\":[{\"name\":\"fraynix-test:3b\",\"size_vram\":2048,\"context_length\":4096}]}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length); exchange.getResponseBody().write(bytes); exchange.close();
        });
        server.createContext("/api/chat", exchange -> {
            request.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = "{\"message\":{\"content\":\"Local reply: café \\\"quoted\\\"\"}}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length); exchange.getResponseBody().write(bytes); exchange.close();
        });
        server.start();
        try {
            Path catalog = root.resolve("models.json");
            Files.writeString(catalog, "{\"stores\":[],\"models\":[{\"name\":\"fraynix-test:3b\",\"status\":\"LOCAL_INSTALLED\",\"endpoint\":\"" + endpoint
                + "\",\"family\":\"test\",\"parameterSize\":\"3B\",\"sourcePath\":\"F:/models/test\"}]}");
            System.setProperty("fraynix.localModelCatalog", catalog.toString());
            var service = new FactoryModelService(endpoint);
            var snapshot = service.observe();
            if (snapshot.models().size() != 1 || !snapshot.stores().get(0).online()) throw new AssertionError("Catalog or proxy filtering wrong");
            var model = snapshot.models().get(0);
            if (!model.available() || !model.loaded() || model.context() != 4096 || model.vramBytes() != 2048) throw new AssertionError("Live residency wrong");
            var reply = service.chat(model, "Check café \"quote\"\nnew line", List.of(ModelAdapter.Message.system("Be accurate")), Map.of("response_format", "json", "num_ctx", 8192));
            if (!reply.success() || !reply.text().equals("Local reply: café \"quoted\"") || !request.get().contains("fraynix-test:3b")) throw new AssertionError("Real Fraynix adapter did not route/parse chat");
            var body = new org.json.JSONObject(request.get());
            if (!body.getString("format").equals("json") || body.getJSONObject("options").getInt("num_ctx") != 8192 || body.getJSONObject("options").has("response_format")) throw new AssertionError("Structured output settings were not routed to the correct request fields");
            var schema=Map.of("type","object","properties",Map.of("observationId",Map.of("enum",List.of("exact-id"))));
            var structured=service.chat(model,"Preserve the exact identity",List.of(ModelAdapter.Message.system("Use the supplied schema")),Map.of("response_format",schema));
            var schemaBody=new org.json.JSONObject(request.get());
            if(!structured.success() || !schemaBody.getJSONObject("format").getJSONObject("properties").getJSONObject("observationId").getJSONArray("enum").getString(0).equals("exact-id")
                || schemaBody.getJSONObject("options").has("response_format"))throw new AssertionError("Schema output contract was not passed through the actual Ollama adapter");
            var exchange = new ConversationStore.Exchange(reply.model(), "question\nline two", reply.text(), reply.endpoint(), true, reply.millis(), Instant.now());
            var store = new ConversationStore(root); store.append(exchange);
            if (!new ConversationStore(root).load().equals(List.of(exchange))) throw new AssertionError("Chat lost on restart");
            fail.set(true);
            snapshot = service.observe();
            if (snapshot.stores().get(0).online() || snapshot.models().get(0).available() || snapshot.models().get(0).loaded()) throw new AssertionError("Offline server left stale online data");
            Files.writeString(store.path(), "broken\n", StandardOpenOption.APPEND);
            try { store.load(); throw new AssertionError("Malformed journal silently accepted"); } catch (java.io.IOException expected) { }
            if (!Files.readString(store.path()).endsWith("broken\n")) throw new AssertionError("Evidence was overwritten");
            try { new FactoryModelService("https://remote.invalid"); throw new AssertionError("Remote accepted"); } catch (IllegalArgumentException expected) { }
            System.out.println("FactoryModelsTest passed: live health, residency, proxy exclusion, real adapter routing, Unicode chat, persisted restart and offline transition");
        } finally {
            server.stop(0);
            if (previous == null) System.clearProperty("fraynix.localModelCatalog"); else System.setProperty("fraynix.localModelCatalog", previous);
        }
    }
}
