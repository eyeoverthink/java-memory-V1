package fraymus.brain.unified;

import fraymus.contract.SystemStateContract;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemStateContractRouteTest {

    @Test
    void routesExplicitSystemStateQueriesWithoutBrainSelection() {
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        Map<String, Object> context = new HashMap<>();
        context.put(SystemStateContract.QUERY_ROUTE_KEY, SystemStateContract.ROUTE_SYSTEM_STATE);

        BrainQuery query = new BrainQuery(
            "describe your current system and state",
            Collections.emptyList(),
            context,
            1.0
        );

        BrainResponse response = orchestrator.processQuery(query);
        assertTrue(response.isSuccess());
        assertEquals("Orchestrator.SYSTEM_STATE", response.getBrainName());
        assertNotNull(response.getMetadata());
        assertEquals(SystemStateContract.ROUTE_SYSTEM_STATE, response.getMetadata().get("queryRoute"));
        assertEquals(Boolean.TRUE, response.getMetadata().get("sovereignBypassed"));
        assertEquals(Boolean.TRUE, response.getMetadata().get("metaSynthesisBypassed"));

        @SuppressWarnings("unchecked")
        Map<String, Object> envelope = (Map<String, Object>) response.getMetadata().get(SystemStateContract.ENVELOPE_KEY);
        assertNotNull(envelope);
        assertTrue(envelope.containsKey("timestamp"));
        assertTrue(envelope.containsKey("boot"));
        assertTrue(envelope.containsKey("memory"));
        assertTrue(envelope.containsKey("ollama"));
        assertTrue(envelope.containsKey("cores"));
        assertTrue(envelope.containsKey("subsystemHealth"));
        assertTrue(envelope.containsKey("provenance"));
    }

    @Test
    void doesNotConvertNonStateQueriesIntoSystemStateRoute() {
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        BrainQuery query = new BrainQuery(
            "design an algorithm for sorting",
            Collections.emptyList(),
            new HashMap<>(),
            1.0
        );

        BrainResponse response = orchestrator.processQuery(query);
        assertFalse(response.isSuccess());
        assertEquals("Orchestrator", response.getBrainName());
    }

    @Test
    void routesImplicitIntrospectionPromptToSystemState() {
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        BrainQuery query = new BrainQuery(
            "describe your current system and state",
            Collections.emptyList(),
            new HashMap<>(),
            1.0
        );

        BrainResponse response = orchestrator.processQuery(query);
        assertTrue(response.isSuccess());
        assertEquals("Orchestrator.SYSTEM_STATE", response.getBrainName());
    }
}
