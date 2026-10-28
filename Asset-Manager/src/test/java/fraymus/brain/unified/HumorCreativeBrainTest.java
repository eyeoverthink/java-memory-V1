package fraymus.brain.unified;

import fraymus.knowledge.AkashicRecord;
import fraymus.model.AkashicModelMemory;
import fraymus.model.ModelRegistryIntegration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HumorCreativeBrainTest {

    @Test
    void generatesSelectedPunchlineWithRecipeMetadata() {
        HumorCreativeBrain brain = new HumorCreativeBrain();
        brain.wire();

        Map<String, Object> context = new HashMap<>();
        context.put("humorTaste", "dark-system humor with practical punchlines");
        BrainQuery query = new BrainQuery(
            "make Fraynix creative, punchy, mass-relatable, and next level",
            Collections.emptyList(),
            context,
            1.0
        );

        BrainResponse response = brain.process(query);

        assertTrue(response.isSuccess());
        assertEquals("HumorCreative", response.getBrainName());
        assertTrue(response.getResponse().contains("Selected punchline"));
        assertTrue(response.getResponse().contains("Alternate remixes"));
        assertNotNull(response.getMetadata());
        assertEquals("humor_remix_selection", response.getMetadata().get("creativeMode"));
        assertEquals(Boolean.FALSE, response.getMetadata().get("requiresExternalModel"));
        assertTrue(((Number) response.getMetadata().get("candidateCount")).intValue() >= 8);
        assertNotNull(response.getMetadata().get("crowdAnchor"));
        assertNotNull(response.getMetadata().get("selectionSignals"));
        assertTrue(brain.getLastSelectionScore() > 0.0);
    }

    @Test
    void adaptsRecipeBiasFromTasteFeedback() {
        HumorCreativeBrain brain = new HumorCreativeBrain();
        brain.wire();

        Map<String, Object> context = new HashMap<>();
        context.put("humorTaste", "dark weird sharp");
        context.put("likedRecipes", List.of("prestige remix", "crowd anchor"));
        context.put("avoidRecipes", List.of("taste probe"));

        BrainQuery query = new BrainQuery(
            "build a next-level remix brain with crowd hooks and sharp punchlines",
            Collections.emptyList(),
            context,
            1.0
        );

        BrainResponse response = brain.process(query);

        assertTrue(response.isSuccess());
        assertTrue(response.getResponse().contains("Creative ladder"));
        assertTrue(response.getResponse().contains("crowd"));
        assertEquals("dark weird sharp", response.getMetadata().get("tasteSignal"));
        @SuppressWarnings("unchecked")
        Map<String, Object> frame = (Map<String, Object>) response.getMetadata().get("frame");
        assertNotNull(frame);
        assertEquals("dark weird sharp", frame.get("tasteSignal"));
    }

    @Test
    void consumesAkashicFeedAndPersistsTasteTrace() {
        HumorCreativeBrain brain = new HumorCreativeBrain();
        brain.wire();

        AkashicRecord akashic = new AkashicRecord();
        int tasteBefore = akashic.queryCategory("HUMOR_TASTE").size();
        int traceBefore = akashic.queryCategory("HUMOR_CREATIVE_TRACE").size();

        akashic.addBlock("WEBCORTEX", "group chat timing, comment section voltage, remix hook, and cleaner punchline release.");
        brain.wireAkashic(akashic);

        Map<String, Object> context = new HashMap<>();
        context.put("humorTaste", "mass weird sharp");
        context.put("sourceFragments", List.of("observed-url: https://example.com/remix"));

        BrainQuery query = new BrainQuery(
            "make Fraynix funny, remixable, and sharp for the masses",
            Collections.emptyList(),
            context,
            1.0
        );

        BrainResponse response = brain.process(query);

        assertTrue(response.isSuccess());
        assertTrue(response.getResponse().contains("Absorbed source sparks"));
        assertTrue(((Number) response.getMetadata().get("sourceEvidenceCount")).intValue() >= 1);
        assertEquals(Boolean.TRUE, response.getMetadata().get("tasteMemoryPersisted"));
        assertEquals(Boolean.TRUE, response.getMetadata().get("creativeTracePersisted"));
        assertTrue(akashic.queryCategory("HUMOR_TASTE").size() > tasteBefore);
        assertTrue(akashic.queryCategory("HUMOR_CREATIVE_TRACE").size() > traceBefore);
    }

    @Test
    void feederEnrichesCreativeContextFromAkashicAndExocortex() throws IOException {
        AkashicRecord akashic = new AkashicRecord();
        akashic.addBlock("WEBCORTEX", "timeline remix timing and comment section crowd hook");

        Path tempExocortex = Files.createTempDirectory("humor-feeder-exocortex");
        Files.writeString(
            tempExocortex.resolve("memgraph_documentation.txt"),
            "Memgraph graph memory remix for the timeline crowd with cleaner timing."
        );

        String previousRoot = System.getProperty("fraymus.humorCreative.exocortexExtractedRoot");
        System.setProperty("fraymus.humorCreative.exocortexExtractedRoot", tempExocortex.toString());
        try {
            Map<String, Object> context = new HashMap<>();
            HumorCreativeFeeder.enrichContext(
                "make this funny and remix memgraph for the masses https://example.com",
                context,
                akashic
            );

            assertTrue(context.containsKey("absorbedFragments"));
            assertTrue(context.containsKey("sourceFragments"));
            assertTrue(context.containsKey("abstractedArtifacts"));
            assertTrue(context.containsKey("capabilitySignals"));
            assertTrue(context.containsKey("generatedTaskHints"));
            assertEquals("akashi_exocortex_prompt", context.get("creativeFeedMode"));
        } finally {
            if (previousRoot == null) {
                System.clearProperty("fraymus.humorCreative.exocortexExtractedRoot");
            } else {
                System.setProperty("fraymus.humorCreative.exocortexExtractedRoot", previousRoot);
            }
        }
    }

    @Test
    void registersThroughOrchestratorCreativeWirePath() {
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();

        orchestrator.wireHumorCreativeBrain();

        assertTrue(orchestrator.isHumorCreativeWired());
        assertNotNull(orchestrator.getHumorCreativeBrain());
        assertTrue(orchestrator.getBrain("HumorCreative").isPresent());
        assertTrue(orchestrator.getBrain("HumorCreative").get().getCapabilities().contains(BrainCapability.CREATIVITY));
    }

    @Test
    void humorQueriesPreferHumorCreativeLaneInsideOrchestrator() {
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        orchestrator.wireHumorCreativeBrain();
        orchestrator.registerBrain(new DreamEngine());

        Map<String, Object> context = new HashMap<>();
        context.put("humorTaste", "mass clever with weird detail");
        BrainQuery query = new BrainQuery(
            "make this funny, punchy, remixable, and next level for the masses",
            Collections.emptyList(),
            context,
            1.0
        );

        BrainResponse response = orchestrator.processQuery(query);

        assertTrue(response.isSuccess());
        assertFalse(response.getResponse().contains("Mathematical Victor"));
        assertTrue(response.getResponse().contains("HumorCreative") || response.getResponse().contains("punchline"));
    }

    @Test
    void feederScrapesUrlsViaParietalLobeBrain() {
        Map<String, Object> context = new HashMap<>();
        HumorCreativeFeeder.enrichContext(
            "make this funny with https://example.com timing",
            context,
            null
        );

        assertTrue(context.containsKey("sourceFragments"));
        assertTrue(context.containsKey("abstractedArtifacts"));
        assertTrue(context.containsKey("capabilitySignals"));
        @SuppressWarnings("unchecked")
        List<String> fragments = (List<String>) context.get("sourceFragments");
        assertFalse(fragments.isEmpty());
        assertTrue(fragments.stream().anyMatch(f -> f.contains("scraped-url:") && f.contains("https://example.com")));
    }

    @Test
    void feederAbsorbsReposViaExocortexAbsorber() {
        Map<String, Object> context = new HashMap<>();
        HumorCreativeFeeder.enrichContext(
            "make firecracker funny and remixable for the masses",
            context,
            null
        );

        assertTrue(context.containsKey("sourceFragments"));
        @SuppressWarnings("unchecked")
        List<String> fragments = (List<String>) context.get("sourceFragments");
        assertTrue(fragments.stream().anyMatch(f -> f.contains("absorbed-repo: firecracker")));
    }

    @Test
    void feederPushesAbstractionSignalsIntoModelMemoryWhenRegistryIsActive() {
        ModelRegistryIntegration integration = new ModelRegistryIntegration();
        integration.initialize();
        ModelRegistryIntegration.setActiveIntegration(integration);
        try {
            Map<String, Object> context = new HashMap<>();
            HumorCreativeFeeder.enrichContext(
                "make this funny and deterministic with wrapper-level code checks",
                context,
                null
            );

            AkashicModelMemory memory = integration.getAkashicModelMemory();
            assertNotNull(memory);
            assertFalse(memory.getAllPerformanceRecords().isEmpty());
            assertTrue(context.containsKey("compiledContextPack"));
            assertTrue(context.containsKey("generatedEvaluationSuite"));
        } finally {
            ModelRegistryIntegration.setActiveIntegration(null);
        }
    }
}
