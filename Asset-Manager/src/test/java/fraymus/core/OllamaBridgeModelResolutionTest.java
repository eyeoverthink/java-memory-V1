package fraymus.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OllamaBridgeModelResolutionTest {

    @Test
    void resolvesRoleAliasToBestObservedModel() {
        String resolved = OllamaBridge.resolveAvailableModel("coding", List.of(
            "glm-4.7-flash:latest",
            "qwen3-coder:latest",
            "gemma-4-31b:latest"
        ));

        assertEquals("qwen3-coder:latest", resolved);
    }

    @Test
    void resolvesReflexAliasToFlashFamily() {
        String resolved = OllamaBridge.resolveAvailableModel("reflex", List.of(
            "qwen3-coder:latest",
            "glm-4.7-flash:latest"
        ));

        assertEquals("glm-4.7-flash:latest", resolved);
    }
}
