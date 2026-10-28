package fraymus.llm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelAbstractionLayerTest {

    @Test
    void codingOrganPrefersCoderFamily() {
        ModelAbstractionLayer layer = ModelAbstractionLayer.defaultLayer();

        ModelSelectionResult result = layer
            .selectForOrgan("OpenClaw", "compile, patch, and debug this repository", List.of(
                "glm-4.7-flash:latest",
                "qwen3-coder:latest",
                "gemma-4-31b:latest"
            ))
            .orElseThrow();

        assertEquals("qwen3-coder:latest", result.resolvedModelName());
        assertEquals(ModelRole.CODING, result.role());
    }

    @Test
    void memoryOrganPrefersLongContextFamily() {
        ModelAbstractionLayer layer = ModelAbstractionLayer.defaultLayer();

        ModelSelectionResult result = layer
            .selectForOrgan("Hippocampus", "hold the whole repo, docs, and pdf memory in context", List.of(
                "qwen3-coder:latest",
                "minimax-m2.5:latest",
                "longcat-flash-chat:latest"
            ))
            .orElseThrow();

        assertEquals("minimax-m2.5:latest", result.resolvedModelName());
        assertEquals(ModelRole.MEMORY, result.role());
    }

    @Test
    void registryContainsVisibleOpenFamilies() {
        OpenModelRegistry registry = new OpenModelRegistry();

        assertTrue(registry.findByAlias("glm-5").isPresent());
        assertTrue(registry.findByAlias("qwen3-coder").isPresent());
        assertTrue(registry.findByAlias("gpt oss 20b").isPresent());
    }
}
