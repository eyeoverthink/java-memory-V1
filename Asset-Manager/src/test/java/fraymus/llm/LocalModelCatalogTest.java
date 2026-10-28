package fraymus.llm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class LocalModelCatalogTest {
    @TempDir Path temp;

    @Test void mergesInstalledCustomModelsAndKeepsTagsDistinct() throws Exception {
        Path catalog = temp.resolve("models.json");
        Files.writeString(catalog, """
            {"models":[
              {"name":"eyeoverthink/Fraynix:3b","status":"LOCAL_INSTALLED","family":"llama","configuredContext":16384},
              {"name":"eyeoverthink/Fraynix:8b","status":"LOCAL_INSTALLED","family":"llama","configuredContext":65536,"capabilities":["tools"]},
              {"name":"remote:cloud","status":"REMOTE_PROXY"},
              {"name":"loose-weights.gguf","status":"DISCOVERED_NOT_ACTIVATED"}]}
            """);
        String old = System.getProperty("fraynix.localModelCatalog");
        System.setProperty("fraynix.localModelCatalog", catalog.toString());
        try {
            OpenModelRegistry registry = new OpenModelRegistry();
            assertEquals(2, registry.all().stream().filter(d -> d.id().startsWith("local-ollama/")).count());
            assertEquals(16384, registry.findByAlias("eyeoverthink/Fraynix:3b").orElseThrow().contextTokens());
            assertEquals(65536, registry.findByAlias("eyeoverthink/Fraynix:8b").orElseThrow().contextTokens());
            ModelSelectionResult selected = new ModelSelectionPolicy(registry).bestCandidate(ModelRole.CODING, "code", List.of("eyeoverthink/Fraynix:8b")).orElseThrow();
            assertEquals("local-ollama/eyeoverthink/Fraynix:8b", selected.descriptorId());
            Files.writeString(catalog, "{\"models\":[]}");
            assertFalse(registry.all().stream().anyMatch(d -> d.id().startsWith("local-ollama/")));
        } finally {
            if (old == null) System.clearProperty("fraynix.localModelCatalog"); else System.setProperty("fraynix.localModelCatalog", old);
        }
    }

    @Test void missingCatalogHasNoInstalledClaims() throws Exception {
        assertTrue(LocalModelCatalog.load(temp.resolve("missing.json")).isEmpty());
    }

    @Test void routesAdditionalDrivesLocallyAndKeepsPrimaryForDuplicateTags() throws Exception {
        Path catalog = temp.resolve("stores.json");
        Files.writeString(catalog, """
            {"stores":[{"endpoint":"http://127.0.0.1:11435"},{"endpoint":"https://remote.example"}],
             "models":[
              {"name":"shared:latest","status":"LOCAL_INSTALLED","endpoint":"http://127.0.0.1:11434"},
              {"name":"shared:latest","status":"LOCAL_INSTALLED","endpoint":"http://127.0.0.1:11435"},
              {"name":"archive:latest","status":"LOCAL_INSTALLED","endpoint":"http://127.0.0.1:11435"},
              {"name":"remote:latest","status":"LOCAL_INSTALLED","endpoint":"https://remote.example"}]}
            """);
        String old = System.getProperty("fraynix.localModelCatalog");
        System.setProperty("fraynix.localModelCatalog", catalog.toString());
        try {
            assertEquals("http://127.0.0.1:11435", LocalModelCatalog.endpointForModel("archive:latest", "http://127.0.0.1:11434"));
            assertEquals("http://127.0.0.1:11434", LocalModelCatalog.endpointForModel("shared:latest", "http://127.0.0.1:11434"));
            assertEquals("http://127.0.0.1:11434", LocalModelCatalog.endpointForModel("remote:latest", "http://127.0.0.1:11434"));
            assertEquals(List.of("http://127.0.0.1:11434", "http://127.0.0.1:11435"), LocalModelCatalog.endpoints("http://127.0.0.1:11434"));
        } finally {
            if (old == null) System.clearProperty("fraynix.localModelCatalog"); else System.setProperty("fraynix.localModelCatalog", old);
        }
    }
}
