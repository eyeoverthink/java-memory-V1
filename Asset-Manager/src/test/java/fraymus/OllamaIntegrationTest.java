package fraymus;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class OllamaIntegrationTest {
    private HttpServer server;
    private OllamaIntegration integration;
    @TempDir Path temp;
    private String generation = "{\"response\":\"He said \\\"hello\\\".\\nNext line.\",\"done\":true}";
    private String chat = "{\"message\":{\"role\":\"assistant\",\"content\":\"Use \\\"quotes\\\" safely.\"}}";

    @BeforeEach void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/tags", exchange -> {
            byte[] response = ("{\"models\":[{\"name\":\"gpt-oss:120b-cloud\",\"remote_host\":\"https://ollama.com\"},"
                + "{\"name\":\"hidden-remote:latest\",\"remote_model\":\"remote\"},"
                + "{\"name\":\"fraynix:3b\"},{\"name\":\"fraynix:8b\"},{\"name\":\"llama3.2:latest\"}]}")
                .getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            try (var out = exchange.getResponseBody()) { out.write(response); }
            exchange.close();
        });
        for (String route : List.of("generate", "chat")) {
            server.createContext("/api/" + route, exchange -> {
                exchange.getRequestBody().readAllBytes();
                byte[] response = (route.equals("generate") ? generation : chat).getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, response.length);
                try (var out = exchange.getResponseBody()) { out.write(response); }
                exchange.close();
            });
        }
        server.start();
        integration = new OllamaIntegration(false, "http://127.0.0.1:" + server.getAddress().getPort());
    }

    @AfterEach void stop() { server.stop(0); }

    @Test void separatesServiceHealthFromInstalledModelsAndExcludesRemoteProxies() {
        assertTrue(integration.testConnection());
        assertEquals(5, integration.listModels().size());
        assertEquals(List.of("fraynix:3b", "fraynix:8b", "llama3.2:latest"), integration.listLocalModels());
    }
    @Test void missingStartupTagResolvesToInstalledLocalWeights() {
        assertEquals("llama3.2:latest", integration.resolveLocalModel("llama3.2:1b"));
        assertTrue(integration.listLocalModels().contains(integration.resolveLocalModel("gpt-oss:120b")));
    }
    @Test void exactCustomVariantSurvivesSelection() {
        assertEquals("fraynix:8b", integration.resolveLocalModel("fraynix:8b"));
        assertEquals("", OllamaIntegration.selectLocalModel("absent", List.of()));
    }
    @Test void generationPreservesEscapedQuotesAndNewlines() {
        assertEquals("He said \"hello\".\nNext line.", integration.generate("fraynix:3b", "test", false));
    }
    @Test void chatPreservesEscapedQuotes() {
        assertEquals("Use \"quotes\" safely.", integration.chat("fraynix:3b", List.of(Map.of("role", "user", "content", "test")), false));
    }
    @Test void streamedChunksAndDoneFrameAreHandled() {
        generation = "{\"response\":\"Hello \"}\n{\"response\":\"world\"}\n{\"done\":true}\n";
        assertEquals("Hello world", integration.generate("fraynix:3b", "test", true));
    }
    @Test void daemonErrorDoesNotBecomeNullOrAnEmptySuccess() {
        generation = "{\"error\":\"model unavailable\"}";
        assertEquals("ERROR: model unavailable", integration.generate("fraynix:3b", "test", false));
    }

    @Test void registryAdapterAndExistingClientsRouteToAdditionalLocalStore() throws Exception {
        String endpoint = "http://127.0.0.1:" + server.getAddress().getPort();
        Path catalog = temp.resolve("catalog.json");
        Files.writeString(catalog, "{\"stores\":[{\"endpoint\":\"" + endpoint + "\"}],\"models\":[{\"name\":\"fraynix:3b\",\"status\":\"LOCAL_INSTALLED\",\"parameterSize\":\"3.2B\",\"endpoint\":\"" + endpoint + "\"}]}");
        generation = "{\"response\":\"ROUTED_OK\",\"done\":true}";
        String old = System.getProperty("fraynix.localModelCatalog");
        System.setProperty("fraynix.localModelCatalog", catalog.toString());
        try {
            assertEquals("ROUTED_OK", new OllamaIntegration(false).generate("fraynix:3b", "test", false));
            assertEquals("ROUTED_OK", new fraymus.llm.api.OllamaClient().generate("fraynix:3b", "test"));
            assertEquals("ROUTED_OK", new fraymus.brain.OllamaSpine("fraynix:3b").think("test"));
            assertEquals("ROUTED_OK", new fraymus.nexus.OllamaBridge("fraynix:3b").ask("test"));
            assertTrue(java.util.Arrays.asList(new fraymus.nexus.OllamaBridge("fraynix:3b").getAvailableModels()).contains("fraynix:3b"));
            fraymus.model.ModelRegistryIntegration registry = new fraymus.model.ModelRegistryIntegration();
            registry.initialize();
            assertNotNull(registry.getModelRegistry().getAdapter("fraynix:3b"));
            var request = new fraymus.model.ModelAdapter.InferenceRequest("fraynix:3b", "test", Map.of("num_predict", 8), List.of(), List.of());
            var response = registry.getModelRegistry().getAdapter("fraynix:3b").infer(request);
            assertTrue(response.isSuccess(), response.getError());
            assertEquals("ROUTED_OK", response.getContent());
            assertEquals(endpoint, response.getMetadata().get("endpoint"));
            assertEquals(4, registry.getModelRegistry().getDescriptor("fraynix:3b").getParameterCount());
        } finally {
            if (old == null) System.clearProperty("fraynix.localModelCatalog"); else System.setProperty("fraynix.localModelCatalog", old);
        }
    }
}
