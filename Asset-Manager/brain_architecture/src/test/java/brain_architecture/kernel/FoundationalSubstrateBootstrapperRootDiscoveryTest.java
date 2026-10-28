package brain_architecture.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoundationalSubstrateBootstrapperRootDiscoveryTest {

    @TempDir
    Path tempDir;

    @Test
    void discoversConfiguredExternalModelRootsWhenNoExplicitRootsProvided() throws Exception {
        Path workspace = tempDir.resolve("workspace");
        Files.createDirectories(workspace);

        Path externalRoot = tempDir.resolve("external-model-root");
        Path bundleRoot = externalRoot.resolve("tiny-bridge");
        Files.createDirectories(bundleRoot);
        Files.writeString(bundleRoot.resolve("config.json"), """
            {
              "model_type": "gpt2",
              "n_layer": 2,
              "n_head": 2,
              "n_embd": 32,
              "vocab_size": 64
            }
            """, StandardCharsets.UTF_8);
        Files.writeString(bundleRoot.resolve("tokenizer.json"), """
            {
              "model": {
                "vocab": {
                  "hello": 0
                }
              }
            }
            """, StandardCharsets.UTF_8);
        Files.write(bundleRoot.resolve("pytorch_model.bin"), new byte[64]);

        String previousRoots = System.getProperty("fraynix.model.roots");
        try {
            System.setProperty("fraynix.model.roots", externalRoot.toString());
            FoundationalSubstrateBootstrapper bootstrapper = new FoundationalSubstrateBootstrapper(
                KernelConfiguration.defaultForWorkspace(workspace)
            );
            LocalModelSubstrateCatalog catalog = bootstrapper.buildCatalog(List.of());

            assertTrue(catalog.roots().contains(externalRoot.toAbsolutePath().normalize()));
            assertNotNull(catalog.descriptorFor("tiny-bridge"));
        } finally {
            if (previousRoots == null) {
                System.clearProperty("fraynix.model.roots");
            } else {
                System.setProperty("fraynix.model.roots", previousRoots);
            }
        }
    }
}

