package sandbox.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sandbox.core.BackendInterface;
import sandbox.organic.OrganicGrowth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for OrganicGrowth
 */
public class OrganicGrowthTest {
    
    private OrganicGrowth organicGrowth;
    
    @BeforeEach
    public void setUp() {
        organicGrowth = new OrganicGrowth(null); // Router can be null for testing
    }
    
    @Test
    public void testInitialization() {
        Map<String, Object> stats = organicGrowth.getStatistics();
        
        assertEquals(0, stats.get("totalPlugins"));
        assertEquals(1, stats.get("totalRoutingRules")); // Default rule
        assertTrue((Boolean) stats.get("pluginArchitectureEnabled"));
    }
    
    @Test
    public void testRegisterPlugin() {
        OrganicGrowth.GrowthPlugin plugin = new TestPlugin("test_plugin", "1.0");
        
        boolean registered = organicGrowth.registerPlugin(plugin);
        
        assertTrue(registered);
        assertEquals(plugin, organicGrowth.getPlugin("test_plugin"));
    }
    
    @Test
    public void testUnregisterPlugin() {
        OrganicGrowth.GrowthPlugin plugin = new TestPlugin("test_plugin", "1.0");
        organicGrowth.registerPlugin(plugin);
        
        boolean unregistered = organicGrowth.unregisterPlugin("test_plugin");
        
        assertTrue(unregistered);
        assertNull(organicGrowth.getPlugin("test_plugin"));
    }
    
    @Test
    public void testAddRoutingRule() {
        OrganicGrowth.RoutingRule rule = new OrganicGrowth.RoutingRule(
            "test_rule",
            "Test rule",
            Map.of("key", "value"),
            0.9,
            LocalDateTime.now()
        );
        
        boolean added = organicGrowth.addRoutingRule(rule);
        
        assertTrue(added);
        assertEquals(rule, organicGrowth.getRoutingRule("test_rule"));
    }
    
    @Test
    public void testRemoveRoutingRule() {
        OrganicGrowth.RoutingRule rule = new OrganicGrowth.RoutingRule(
            "test_rule",
            "Test rule",
            Map.of("key", "value"),
            0.9,
            LocalDateTime.now()
        );
        organicGrowth.addRoutingRule(rule);
        
        boolean removed = organicGrowth.removeRoutingRule("test_rule");
        
        assertTrue(removed);
        assertNull(organicGrowth.getRoutingRule("test_rule"));
    }
    
    @Test
    public void testEvolveRoutingRules() {
        // Add a rule
        OrganicGrowth.RoutingRule rule = new OrganicGrowth.RoutingRule(
            "test_rule",
            "Test rule",
            Map.of("key", "value"),
            0.9,
            LocalDateTime.now()
        );
        organicGrowth.addRoutingRule(rule);
        
        // Evolve rules
        List<OrganicGrowth.RoutingRule> evolved = organicGrowth.evolveRoutingRules();
        
        assertFalse(evolved.isEmpty());
        assertTrue(evolved.get(0).getId().contains("_evolved"));
    }
    
    @Test
    public void testSelfModifyRouting() {
        Map<String, Object> patterns = Map.of(
            "avoid_backend_claude", "high_latency",
            "avoid_backend_gemini", "low_accuracy"
        );
        
        organicGrowth.selfModifyRouting(patterns);
        
        // Should have created avoidance rules
        assertNotNull(organicGrowth.getRoutingRule("avoid_claude"));
        assertNotNull(organicGrowth.getRoutingRule("avoid_gemini"));
    }
    
    @Test
    public void testGetApplicableRules() {
        OrganicGrowth.RoutingRule rule = new OrganicGrowth.RoutingRule(
            "fast_rule",
            "Fast inference rule",
            Map.of("taskType", "fast_inference"),
            0.9,
            LocalDateTime.now()
        );
        organicGrowth.addRoutingRule(rule);

        List<OrganicGrowth.RoutingRule> applicable =
            organicGrowth.getApplicableRules(BackendInterface.TaskType.FAST_INFERENCE);

        assertFalse(applicable.isEmpty());
        assertTrue(applicable.stream().anyMatch(r -> r.getId().equals("fast_rule")));
    }
    
    @Test
    public void testEvolutionHistory() {
        organicGrowth.addRoutingRule(new OrganicGrowth.RoutingRule(
            "test", "test", Map.of(), 0.5, LocalDateTime.now()
        ));
        
        List<OrganicGrowth.EvolutionaryEvent> history = organicGrowth.getEvolutionHistory();
        
        assertFalse(history.isEmpty());
    }
    
    @Test
    public void testReset() {
        OrganicGrowth.GrowthPlugin plugin = new TestPlugin("test", "1.0");
        organicGrowth.registerPlugin(plugin);
        organicGrowth.addRoutingRule(new OrganicGrowth.RoutingRule(
            "test", "test", Map.of(), 0.5, LocalDateTime.now()
        ));
        
        organicGrowth.reset();
        
        Map<String, Object> stats = organicGrowth.getStatistics();
        assertEquals(0, stats.get("totalPlugins"));
        assertEquals(1, stats.get("totalRoutingRules")); // Default rule restored
    }
    
    @Test
    public void testPluginExecution() {
        OrganicGrowth.GrowthPlugin plugin = new TestPlugin("test", "1.0");
        organicGrowth.registerPlugin(plugin);
        
        Map<String, Object> result = plugin.execute(Map.of("input", "test"));
        
        assertNotNull(result);
        assertEquals("test", result.get("output"));
    }
    
    /**
     * Test plugin implementation
     */
    private static class TestPlugin implements OrganicGrowth.GrowthPlugin {
        private final String id;
        private final String version;
        private boolean initialized = false;
        
        public TestPlugin(String id, String version) {
            this.id = id;
            this.version = version;
        }
        
        @Override
        public String getId() { return id; }
        
        @Override
        public String getName() { return "Test Plugin"; }
        
        @Override
        public String getVersion() { return version; }
        
        @Override
        public void initialize() { initialized = true; }
        
        @Override
        public void shutdown() { initialized = false; }
        
        @Override
        public Map<String, Object> execute(Map<String, Object> input) {
            return Map.of("output", input.get("input"));
        }
    }
}
