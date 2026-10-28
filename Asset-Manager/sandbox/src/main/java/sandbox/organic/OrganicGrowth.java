package sandbox.organic;

import sandbox.core.BackendInterface;
import sandbox.routing.BasicRouter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Organic Growth mechanism for plugin architecture and runtime reconfiguration.
 * 
 * This component enables the orchestrator to evolve organically through
 * plugin architecture, runtime reconfiguration, evolutionary algorithms,
 * and self-modifying routing rules.
 */
public class OrganicGrowth {
    
    private final Map<String, GrowthPlugin> plugins;
    private final Map<String, RoutingRule> dynamicRoutingRules;
    private final List<EvolutionaryEvent> evolutionHistory;
    private final OrganicConfig config;
    private final BasicRouter router;
    
    public OrganicGrowth(BasicRouter router) {
        this.router = router;
        this.plugins = new ConcurrentHashMap<>();
        this.dynamicRoutingRules = new ConcurrentHashMap<>();
        this.evolutionHistory = new ArrayList<>();
        this.config = new OrganicConfig();
        
        initializeDefaultRules();
    }
    
    /**
     * Initialize default dynamic routing rules
     */
    private void initializeDefaultRules() {
        // Default rule: prefer local backends for fast inference
        dynamicRoutingRules.put("prefer_local_fast", new RoutingRule(
            "prefer_local_fast",
            "Prefer local backends for fast inference tasks",
            Map.of(
                "taskType", "FAST_INFERENCE",
                "preference", "local"
            ),
            0.8,
            LocalDateTime.now()
        ));
    }
    
    /**
     * Register a growth plugin
     * 
     * @param plugin The plugin to register
     * @return true if registration successful
     */
    public boolean registerPlugin(GrowthPlugin plugin) {
        if (!config.isPluginArchitectureEnabled()) {
            return false;
        }
        
        plugins.put(plugin.getId(), plugin);
        plugin.initialize();
        
        recordEvolutionaryEvent(EvolutionaryEventType.PLUGIN_REGISTERED, 
            "Plugin registered: " + plugin.getId());
        
        return true;
    }
    
    /**
     * Unregister a growth plugin
     * 
     * @param pluginId The plugin ID
     * @return true if unregistration successful
     */
    public boolean unregisterPlugin(String pluginId) {
        GrowthPlugin plugin = plugins.remove(pluginId);
        if (plugin != null) {
            plugin.shutdown();
            recordEvolutionaryEvent(EvolutionaryEventType.PLUGIN_UNREGISTERED, 
                "Plugin unregistered: " + pluginId);
            return true;
        }
        return false;
    }
    
    /**
     * Get a registered plugin
     * 
     * @param pluginId The plugin ID
     * @return GrowthPlugin or null if not found
     */
    public GrowthPlugin getPlugin(String pluginId) {
        return plugins.get(pluginId);
    }
    
    /**
     * Get all registered plugins
     * 
     * @return Map of plugin ID to plugin
     */
    public Map<String, GrowthPlugin> getAllPlugins() {
        return new HashMap<>(plugins);
    }
    
    /**
     * Add a dynamic routing rule
     * 
     * @param rule The routing rule to add
     * @return true if rule added successfully
     */
    public boolean addRoutingRule(RoutingRule rule) {
        if (!config.isRuntimeReconfigurationEnabled()) {
            return false;
        }
        
        dynamicRoutingRules.put(rule.getId(), rule);
        
        recordEvolutionaryEvent(EvolutionaryEventType.RULE_ADDED, 
            "Routing rule added: " + rule.getId());
        
        return true;
    }
    
    /**
     * Remove a dynamic routing rule
     * 
     * @param ruleId The rule ID to remove
     * @return true if rule removed successfully
     */
    public boolean removeRoutingRule(String ruleId) {
        RoutingRule removed = dynamicRoutingRules.remove(ruleId);
        if (removed != null) {
            recordEvolutionaryEvent(EvolutionaryEventType.RULE_REMOVED, 
                "Routing rule removed: " + ruleId);
            return true;
        }
        return false;
    }
    
    /**
     * Get a routing rule
     * 
     * @param ruleId The rule ID
     * @return RoutingRule or null if not found
     */
    public RoutingRule getRoutingRule(String ruleId) {
        return dynamicRoutingRules.get(ruleId);
    }
    
    /**
     * Get all dynamic routing rules
     * 
     * @return Map of rule ID to routing rule
     */
    public Map<String, RoutingRule> getAllRoutingRules() {
        return new HashMap<>(dynamicRoutingRules);
    }
    
    /**
     * Apply evolutionary algorithm to optimize routing rules
     * 
     * @return List of evolved routing rules
     */
    public List<RoutingRule> evolveRoutingRules() {
        if (!config.isEvolutionaryAlgorithmsEnabled()) {
            return new ArrayList<>(dynamicRoutingRules.values());
        }
        
        List<RoutingRule> evolvedRules = new ArrayList<>();
        
        // Simple evolutionary algorithm: mutate rules based on performance
        for (RoutingRule rule : dynamicRoutingRules.values()) {
            // Mutate confidence based on age (older rules get slightly lower confidence)
            double ageFactor = calculateAgeFactor(rule.getCreatedAt());
            double mutatedConfidence = rule.getConfidence() * ageFactor;
            
            RoutingRule mutatedRule = new RoutingRule(
                rule.getId() + "_evolved",
                rule.getDescription() + " (evolved)",
                rule.getParameters(),
                mutatedConfidence,
                LocalDateTime.now()
            );
            
            evolvedRules.add(mutatedRule);
        }
        
        // Replace old rules with evolved ones
        dynamicRoutingRules.clear();
        for (RoutingRule rule : evolvedRules) {
            dynamicRoutingRules.put(rule.getId(), rule);
        }
        
        recordEvolutionaryEvent(EvolutionaryEventType.RULES_EVOLVED, 
            "Routing rules evolved: " + evolvedRules.size() + " rules");
        
        return evolvedRules;
    }
    
    /**
     * Calculate age factor for rule evolution
     * (Rules decay over time unless reinforced)
     */
    private double calculateAgeFactor(LocalDateTime createdAt) {
        long hoursSinceCreation = java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
        
        // Decay factor: 1.0 at creation, 0.9 after 1 hour, 0.8 after 2 hours, etc.
        // Minimum 0.5
        return Math.max(0.5, 1.0 - (hoursSinceCreation * 0.1));
    }
    
    /**
     * Self-modify routing based on learned patterns
     * 
     * @param patterns Map of learned patterns
     */
    public void selfModifyRouting(Map<String, Object> patterns) {
        if (!config.isSelfModifyingRulesEnabled()) {
            return;
        }
        
        // Example: If pattern shows high latency for a backend, add rule to avoid it
        for (Map.Entry<String, Object> entry : patterns.entrySet()) {
            String patternKey = entry.getKey();
            Object patternValue = entry.getValue();
            
            if (patternKey.startsWith("avoid_backend_")) {
                String backendType = patternKey.replace("avoid_backend_", "");
                RoutingRule avoidanceRule = new RoutingRule(
                    "avoid_" + backendType,
                    "Avoid backend due to poor performance",
                    Map.of("avoidBackend", backendType),
                    0.9,
                    LocalDateTime.now()
                );
                addRoutingRule(avoidanceRule);
            }
        }
        
        recordEvolutionaryEvent(EvolutionaryEventType.SELF_MODIFIED, 
            "Routing self-modified based on " + patterns.size() + " patterns");
    }
    
    /**
     * Get applicable routing rules for a task type
     * 
     * @param taskType The task type
     * @return List of applicable rules sorted by confidence
     */
    public List<RoutingRule> getApplicableRules(BackendInterface.TaskType taskType) {
        return dynamicRoutingRules.values().stream()
            .filter(rule -> {
                Object taskTypeParam = rule.getParameters().get("taskType");
                return taskTypeParam == null || 
                       taskTypeParam.toString().equals(taskType.getId());
            })
            .sorted((r1, r2) -> Double.compare(r2.getConfidence(), r1.getConfidence()))
            .toList();
    }
    
    /**
     * Get evolution history
     * 
     * @return List of evolutionary events
     */
    public List<EvolutionaryEvent> getEvolutionHistory() {
        return new ArrayList<>(evolutionHistory);
    }
    
    /**
     * Record an evolutionary event
     */
    private void recordEvolutionaryEvent(EvolutionaryEventType type, String description) {
        EvolutionaryEvent event = new EvolutionaryEvent(
            type,
            description,
            LocalDateTime.now()
        );
        evolutionHistory.add(event);
        
        // Keep only last 1000 events
        if (evolutionHistory.size() > 1000) {
            evolutionHistory.remove(0);
        }
    }
    
    /**
     * Get organic growth statistics
     * 
     * @return Map of statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPlugins", plugins.size());
        stats.put("totalRoutingRules", dynamicRoutingRules.size());
        stats.put("evolutionHistorySize", evolutionHistory.size());
        stats.put("pluginArchitectureEnabled", config.isPluginArchitectureEnabled());
        stats.put("runtimeReconfigurationEnabled", config.isRuntimeReconfigurationEnabled());
        stats.put("evolutionaryAlgorithmsEnabled", config.isEvolutionaryAlgorithmsEnabled());
        stats.put("selfModifyingRulesEnabled", config.isSelfModifyingRulesEnabled());
        
        return stats;
    }
    
    /**
     * Reset all organic growth data
     */
    public void reset() {
        // Shutdown all plugins
        for (GrowthPlugin plugin : plugins.values()) {
            plugin.shutdown();
        }
        plugins.clear();
        dynamicRoutingRules.clear();
        evolutionHistory.clear();
        initializeDefaultRules();
    }
    
    /**
     * Growth plugin interface
     */
    public interface GrowthPlugin {
        String getId();
        String getName();
        String getVersion();
        void initialize();
        void shutdown();
        Map<String, Object> execute(Map<String, Object> input);
    }
    
    /**
     * Routing rule
     */
    public static class RoutingRule {
        private final String id;
        private final String description;
        private final Map<String, Object> parameters;
        private final double confidence;
        private final LocalDateTime createdAt;
        
        public RoutingRule(String id, String description, Map<String, Object> parameters,
                         double confidence, LocalDateTime createdAt) {
            this.id = id;
            this.description = description;
            this.parameters = new HashMap<>(parameters);
            this.confidence = confidence;
            this.createdAt = createdAt;
        }
        
        public String getId() { return id; }
        public String getDescription() { return description; }
        public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
        public double getConfidence() { return confidence; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
    
    /**
     * Evolutionary event
     */
    public static class EvolutionaryEvent {
        private final EvolutionaryEventType type;
        private final String description;
        private final LocalDateTime timestamp;
        
        public EvolutionaryEvent(EvolutionaryEventType type, String description, LocalDateTime timestamp) {
            this.type = type;
            this.description = description;
            this.timestamp = timestamp;
        }
        
        public EvolutionaryEventType getType() { return type; }
        public String getDescription() { return description; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    /**
     * Evolutionary event type
     */
    public enum EvolutionaryEventType {
        PLUGIN_REGISTERED,
        PLUGIN_UNREGISTERED,
        RULE_ADDED,
        RULE_REMOVED,
        RULES_EVOLVED,
        SELF_MODIFIED,
        CONFIGURATION_CHANGED
    }
    
    /**
     * Organic configuration
     */
    public static class OrganicConfig {
        private boolean pluginArchitectureEnabled = true;
        private boolean runtimeReconfigurationEnabled = true;
        private boolean evolutionaryAlgorithmsEnabled = true;
        private boolean selfModifyingRulesEnabled = true;
        private int maxEvolutionHistorySize = 1000;
        
        public boolean isPluginArchitectureEnabled() { return pluginArchitectureEnabled; }
        public void setPluginArchitectureEnabled(boolean enabled) { pluginArchitectureEnabled = enabled; }
        
        public boolean isRuntimeReconfigurationEnabled() { return runtimeReconfigurationEnabled; }
        public void setRuntimeReconfigurationEnabled(boolean enabled) { runtimeReconfigurationEnabled = enabled; }
        
        public boolean isEvolutionaryAlgorithmsEnabled() { return evolutionaryAlgorithmsEnabled; }
        public void setEvolutionaryAlgorithmsEnabled(boolean enabled) { evolutionaryAlgorithmsEnabled = enabled; }
        
        public boolean isSelfModifyingRulesEnabled() { return selfModifyingRulesEnabled; }
        public void setSelfModifyingRulesEnabled(boolean enabled) { selfModifyingRulesEnabled = enabled; }
        
        public int getMaxEvolutionHistorySize() { return maxEvolutionHistorySize; }
        public void setMaxEvolutionHistorySize(int size) { maxEvolutionHistorySize = size; }
    }
}
