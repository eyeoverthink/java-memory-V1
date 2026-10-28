package fraymus.brain.unified;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for managing all unified brains.
 * Provides dynamic discovery, registration, and lookup.
 */
public class BrainRegistry {
    private final Map<String, UnifiedBrain> brains;
    private final Map<BrainCapability, List<UnifiedBrain>> capabilityIndex;
    private final Map<String, List<UnifiedBrain>> typeIndex;

    public BrainRegistry() {
        this.brains = new ConcurrentHashMap<>();
        this.capabilityIndex = new ConcurrentHashMap<>();
        this.typeIndex = new ConcurrentHashMap<>();
    }

    /**
     * Register a brain with the registry.
     */
    public synchronized void register(UnifiedBrain brain) {
        String name = brain.getName();
        brains.put(name, brain);

        // Index by capability
        for (BrainCapability capability : brain.getCapabilities()) {
            capabilityIndex.computeIfAbsent(capability, k -> new ArrayList<>()).add(brain);
        }

        // Index by type
        String type = brain.getType();
        typeIndex.computeIfAbsent(type, k -> new ArrayList<>()).add(brain);
    }

    /**
     * Unregister a brain from the registry.
     */
    public synchronized void unregister(String brainName) {
        UnifiedBrain brain = brains.remove(brainName);
        if (brain != null) {
            // Remove from capability index
            for (BrainCapability capability : brain.getCapabilities()) {
                List<UnifiedBrain> brainList = capabilityIndex.get(capability);
                if (brainList != null) {
                    brainList.remove(brain);
                    if (brainList.isEmpty()) {
                        capabilityIndex.remove(capability);
                    }
                }
            }

            // Remove from type index
            List<UnifiedBrain> brainList = typeIndex.get(brain.getType());
            if (brainList != null) {
                brainList.remove(brain);
                if (brainList.isEmpty()) {
                    typeIndex.remove(brain.getType());
                }
            }
        }
    }

    /**
     * Get a brain by name.
     */
    public Optional<UnifiedBrain> getBrain(String name) {
        return Optional.ofNullable(brains.get(name));
    }

    /**
     * Get all brains.
     */
    public List<UnifiedBrain> getAllBrains() {
        return new ArrayList<>(brains.values());
    }

    /**
     * Get brains that have a specific capability.
     */
    public List<UnifiedBrain> getBrainsByCapability(BrainCapability capability) {
        return capabilityIndex.getOrDefault(capability, Collections.emptyList())
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * Get brains of a specific type.
     */
    public List<UnifiedBrain> getBrainsByType(String type) {
        return typeIndex.getOrDefault(type, Collections.emptyList())
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * Get healthy brains that have all required capabilities.
     */
    public List<UnifiedBrain> getHealthyBrainsWithCapabilities(List<BrainCapability> requiredCapabilities) {
        return brains.values().stream()
                .filter(brain -> brain.getHealth() > 0.5)
                .filter(brain -> brain.getCapabilities().containsAll(requiredCapabilities))
                .collect(Collectors.toList());
    }

    /**
     * Get the best brain for a specific capability based on health and consciousness.
     */
    public Optional<UnifiedBrain> getBestBrainForCapability(BrainCapability capability) {
        List<UnifiedBrain> candidates = getBrainsByCapability(capability);
        return candidates.stream()
                .filter(brain -> brain.getHealth() > 0.5)
                .max(Comparator.comparingDouble(brain -> 
                    brain.getHealth() * brain.getConsciousnessLevel()));
    }

    /**
     * Get total number of registered brains.
     */
    public int getBrainCount() {
        return brains.size();
    }

    /**
     * Get number of brains with a specific capability.
     */
    public int getBrainCountForCapability(BrainCapability capability) {
        return capabilityIndex.getOrDefault(capability, Collections.emptyList()).size();
    }

    /**
     * Check if a brain is registered.
     */
    public boolean isRegistered(String brainName) {
        return brains.containsKey(brainName);
    }

    /**
     * Get all brain types.
     */
    public Set<String> getBrainTypes() {
        return new HashSet<>(typeIndex.keySet());
    }

    /**
     * Get registry statistics.
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBrains", brains.size());
        stats.put("totalTypes", typeIndex.size());
        stats.put("totalCapabilities", capabilityIndex.size());
        
        Map<String, Integer> capabilityCounts = new HashMap<>();
        for (BrainCapability cap : capabilityIndex.keySet()) {
            capabilityCounts.put(cap.name(), capabilityIndex.get(cap).size());
        }
        stats.put("capabilityCounts", capabilityCounts);
        
        Map<String, Integer> typeCounts = new HashMap<>();
        for (String type : typeIndex.keySet()) {
            typeCounts.put(type, typeIndex.get(type).size());
        }
        stats.put("typeCounts", typeCounts);
        
        return stats;
    }
}
