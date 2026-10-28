package fraymus.brain.unified;

import fraymus.model.AkashicModelMemory;
import fraymus.model.CapabilityProfile;
import fraymus.model.ModelRegistryIntegration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnifiedBrainOrchestratorModelRegistryIntegrationTest {

    @Test
    void wireModelRegistryMarksSelectorAsWired() {
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        ModelRegistryIntegration integration = new ModelRegistryIntegration();
        integration.initialize();

        orchestrator.wireModelRegistry(integration);

        assertTrue(orchestrator.getWiringStatus().getOrDefault("modelSelector", false));
    }

    @Test
    void processQueryPropagatesModelRoutingAndRecordsUsage() {
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        ModelRegistryIntegration integration = new ModelRegistryIntegration();
        integration.initialize();
        orchestrator.wireModelRegistry(integration);
        orchestrator.wireModelSelector(integration.getModelSelector());

        FakeBrain logicBrain = new FakeBrain(
            "LogicEngine",
            List.of(BrainCapability.REASONING, BrainCapability.MATHEMATICS),
            "deterministic response"
        );
        orchestrator.registerBrain(logicBrain);

        BrainQuery query = new BrainQuery(
            "Solve this architecture problem.",
            List.of(BrainCapability.REASONING),
            new HashMap<>(),
            1.0
        );

        BrainResponse response = orchestrator.processQuery(query);
        assertTrue(response.isSuccess());
        assertNotNull(response.getMetadata());
        assertTrue(response.getMetadata().containsKey("modelRouting"));

        Object modelRouting = response.getMetadata().get("modelRouting");
        assertInstanceOf(List.class, modelRouting);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> routingEntries = (List<Map<String, Object>>) modelRouting;
        assertFalse(routingEntries.isEmpty());

        boolean includesPrefrontal = routingEntries.stream()
            .map(entry -> entry.get("organ"))
            .anyMatch("PREFRONTAL_CORTEX"::equals);
        assertTrue(includesPrefrontal);

        AkashicModelMemory memory = integration.getAkashicModelMemory();
        AkashicModelMemory.SelectionHistory history = memory.getSelectionHistory(CapabilityProfile.Organ.PREFRONTAL_CORTEX);
        assertNotNull(history);
        assertFalse(history.getEvents().isEmpty());

        String selectedModelId = (String) routingEntries.get(0).get("selectedModel");
        assertNotNull(selectedModelId);
        AkashicModelMemory.ModelPerformanceRecord record = memory.getPerformanceRecord(selectedModelId);
        assertNotNull(record);
        assertTrue(record.getTotalTasks() >= 1);
    }

    @Test
    void nonHumorCodingQueryGetsAbstractedAndPackedForRouting() {
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        ModelRegistryIntegration integration = new ModelRegistryIntegration();
        integration.initialize();
        orchestrator.wireModelRegistry(integration);
        orchestrator.wireModelSelector(integration.getModelSelector());

        FakeBrain codingBrain = new FakeBrain(
            "TransformationEngine",
            List.of(BrainCapability.PHYSICS, BrainCapability.REASONING),
            "coding response"
        );
        orchestrator.registerBrain(codingBrain);

        Map<String, Object> context = new HashMap<>();
        context.put(
            "sourceFragments",
            List.of(
                "absorbed-repo: java adapters compile patch verification",
                "documentation: fix class import and compile deterministically"
            )
        );

        BrainQuery query = new BrainQuery(
            "Implement adapter class and debug compile failures with deterministic checks.",
            List.of(BrainCapability.PHYSICS),
            context,
            1.0
        );

        BrainResponse response = orchestrator.processQuery(query);
        assertTrue(response.isSuccess());
        assertNotNull(response.getMetadata());

        Object compiledPack = response.getMetadata().get("compiledContextPack");
        assertInstanceOf(String.class, compiledPack);
        String packed = (String) compiledPack;
        assertFalse(packed.isBlank());
        assertTrue(packed.contains("[Fraynix Context Pack]"));
        assertTrue(packed.contains("abstracted_artifacts"));

        Object modelRouting = response.getMetadata().get("modelRouting");
        assertInstanceOf(List.class, modelRouting);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> routingEntries = (List<Map<String, Object>>) modelRouting;
        assertFalse(routingEntries.isEmpty());

        boolean includesCodingOrgan = routingEntries.stream()
            .map(entry -> entry.get("organ"))
            .anyMatch("CODING_ORGAN"::equals);
        assertTrue(includesCodingOrgan);

        AkashicModelMemory memory = integration.getAkashicModelMemory();
        AkashicModelMemory.SelectionHistory codingHistory = memory.getSelectionHistory(CapabilityProfile.Organ.CODING_ORGAN);
        assertNotNull(codingHistory);
        assertFalse(codingHistory.getEvents().isEmpty());
    }

    private static final class FakeBrain implements UnifiedBrain {
        private final String name;
        private final List<BrainCapability> capabilities;
        private final String responseText;
        private boolean wired;

        private FakeBrain(String name, List<BrainCapability> capabilities, String responseText) {
            this.name = name;
            this.capabilities = new ArrayList<>(capabilities);
            this.responseText = responseText;
            this.wired = false;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getType() {
            return "TestBrain";
        }

        @Override
        public double getConsciousnessLevel() {
            return 0.8;
        }

        @Override
        public List<BrainCapability> getCapabilities() {
            return new ArrayList<>(capabilities);
        }

        @Override
        public BrainResponse process(BrainQuery query) {
            return BrainResponse.success(
                name,
                responseText,
                0.9,
                0.8,
                new HashMap<>(Map.of("queryEcho", query.getQuery())),
                5
            );
        }

        @Override
        public void wire() {
            wired = true;
        }

        @Override
        public void unwire() {
            wired = false;
        }

        @Override
        public boolean isWired() {
            return wired;
        }

        @Override
        public double getHealth() {
            return 1.0;
        }

        @Override
        public BrainStatistics getStatistics() {
            return BrainStatistics.empty();
        }
    }
}
