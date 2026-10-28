package brain_architecture.orchestration.discovery;

import brain_architecture.core.BackendInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Backend Discovery for automatic backend detection and dynamic registration.
 * 
 * This component automatically discovers available backends, assesses their capabilities,
 * and dynamically registers them with the orchestrator. It enables hot-swappable backends
 * without system restart.
 */
public class BackendDiscovery {
    
    private final Map<BackendInterface.BackendType, BackendInfo> discoveredBackends;
    private final Map<String, BackendInterface.BackendType> backendAliases;
    private final DiscoveryConfig config;
    private boolean autoDiscoveryEnabled = true;
    
    public BackendDiscovery() {
        this.discoveredBackends = new ConcurrentHashMap<>();
        this.backendAliases = new ConcurrentHashMap<>();
        this.config = new DiscoveryConfig();
        
        // Initialize with known backend types
        initializeKnownBackends();
    }
    
    /**
     * Initialize with known backend types
     */
    private void initializeKnownBackends() {
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            discoveredBackends.put(type, new BackendInfo(type, BackendStatus.UNKNOWN));
        }
    }
    
    /**
     * Discover backends by attempting to connect to each
     * 
     * @return Map of discovered backends with their status
     */
    public Map<BackendInterface.BackendType, BackendStatus> discoverBackends() {
        Map<BackendInterface.BackendType, BackendStatus> results = new HashMap<>();
        
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            BackendStatus status = probeBackend(type);
            results.put(type, status);
            
            BackendInfo info = discoveredBackends.get(type);
            if (info != null) {
                info.setStatus(status);
                info.setLastChecked(System.currentTimeMillis());
            }
        }
        
        return results;
    }
    
    /**
     * Probe a specific backend to check if it's available
     */
    private BackendStatus probeBackend(BackendInterface.BackendType type) {
        // In a real implementation, this would attempt to connect to the backend
        // For integration, we'll simulate based on backend type
        
        // Simulate: OLLAMA, OBSIDIAN, OPENCLAW are local (likely available)
        // CLAUDE, GEMMA3 are remote (may not be available)
        
        return switch (type) {
            case OLLAMA, OBSIDIAN, OPENCLAW, NEOCLAW, DEEPSEEK, FISH_AUDIO_S2 -> BackendStatus.AVAILABLE;
            case CLAUDE, GEMMA3, KIMI, GEMMA4, LTXVIDEO, SPEECHBRAIN -> BackendStatus.NOT_CONFIGURED; // Remote/specialized backends need configuration
            case LAZARUS, AKASHICRECORD -> BackendStatus.AVAILABLE; // Specialized local backends
        };
    }
    
    /**
     * Register a discovered backend with the orchestrator
     * 
     * @param type The backend type
     * @param capabilities The backend capabilities (optional, will be auto-detected if null)
     * @return true if registration successful
     */
    public boolean registerBackend(BackendInterface.BackendType type, 
                                   BackendInterface.BackendCapabilities capabilities) {
        BackendInfo info = discoveredBackends.get(type);
        if (info == null) {
            info = new BackendInfo(type, BackendStatus.UNKNOWN);
            discoveredBackends.put(type, info);
        }
        
        if (capabilities != null) {
            info.setCapabilities(capabilities);
        }
        
        info.setStatus(BackendStatus.REGISTERED);
        info.setRegisteredAt(System.currentTimeMillis());
        
        return true;
    }
    
    /**
     * Unregister a backend from the orchestrator
     * 
     * @param type The backend type to unregister
     * @return true if unregistration successful
     */
    public boolean unregisterBackend(BackendInterface.BackendType type) {
        BackendInfo info = discoveredBackends.get(type);
        if (info != null) {
            info.setStatus(BackendStatus.UNREGISTERED);
            info.setUnregisteredAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }
    
    /**
     * Hot-swap a backend (replace with new instance)
     * 
     * @param type The backend type to hot-swap
     * @param newBackend The new backend instance
     * @return true if hot-swap successful
     */
    public boolean hotSwapBackend(BackendInterface.BackendType type, BackendInterface newBackend) {
        BackendInfo info = discoveredBackends.get(type);
        if (info != null && info.getStatus() == BackendStatus.REGISTERED) {
            // Unregister old
            unregisterBackend(type);
            
            // Register new
            return registerBackend(type, newBackend.getCapabilities());
        }
        return false;
    }
    
    /**
     * Get information about a discovered backend
     * 
     * @param type The backend type
     * @return BackendInfo or null if not found
     */
    public BackendInfo getBackendInfo(BackendInterface.BackendType type) {
        return discoveredBackends.get(type);
    }
    
    /**
     * Get all discovered backends
     * 
     * @return Map of backend type to backend info
     */
    public Map<BackendInterface.BackendType, BackendInfo> getAllDiscoveredBackends() {
        return new HashMap<>(discoveredBackends);
    }
    
    /**
     * Get available backends (status is AVAILABLE or REGISTERED)
     * 
     * @return List of available backend types
     */
    public List<BackendInterface.BackendType> getAvailableBackends() {
        return discoveredBackends.entrySet().stream()
            .filter(e -> e.getValue().getStatus() == BackendStatus.AVAILABLE || 
                       e.getValue().getStatus() == BackendStatus.REGISTERED)
            .map(Map.Entry::getKey)
            .toList();
    }
    
    /**
     * Add an alias for a backend type
     * 
     * @param alias The alias name
     * @param type The backend type
     */
    public void addAlias(String alias, BackendInterface.BackendType type) {
        backendAliases.put(alias.toLowerCase(), type);
    }
    
    /**
     * Resolve an alias to a backend type
     * 
     * @param alias The alias to resolve
     * @return BackendType or null if not found
     */
    public BackendInterface.BackendType resolveAlias(String alias) {
        return backendAliases.get(alias.toLowerCase());
    }
    
    /**
     * Enable or disable auto-discovery
     * 
     * @param enabled Whether auto-discovery should be enabled
     */
    public void setAutoDiscoveryEnabled(boolean enabled) {
        this.autoDiscoveryEnabled = enabled;
    }
    
    /**
     * Check if auto-discovery is enabled
     * 
     * @return true if auto-discovery is enabled
     */
    public boolean isAutoDiscoveryEnabled() {
        return autoDiscoveryEnabled;
    }
    
    /**
     * Get discovery statistics
     * 
     * @return Map of discovery statistics
     */
    public Map<String, Object> getDiscoveryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = discoveredBackends.size();
        long available = discoveredBackends.values().stream()
            .filter(i -> i.getStatus() == BackendStatus.AVAILABLE || 
                       i.getStatus() == BackendStatus.REGISTERED)
            .count();
        long registered = discoveredBackends.values().stream()
            .filter(i -> i.getStatus() == BackendStatus.REGISTERED)
            .count();
        
        stats.put("totalBackends", total);
        stats.put("availableBackends", available);
        stats.put("registeredBackends", registered);
        stats.put("aliases", backendAliases.size());
        stats.put("autoDiscoveryEnabled", autoDiscoveryEnabled);
        
        return stats;
    }
    
    /**
     * Reset all discovery data
     */
    public void reset() {
        discoveredBackends.clear();
        backendAliases.clear();
        initializeKnownBackends();
    }
    
    /**
     * Backend status
     */
    public enum BackendStatus {
        UNKNOWN,         // Status not yet determined
        AVAILABLE,       // Backend is available but not registered
        NOT_CONFIGURED,  // Backend exists but needs configuration
        REGISTERED,      // Backend is registered and ready
        UNREGISTERED,    // Backend was unregistered
        UNAVAILABLE,     // Backend is not available
        ERROR            // Backend encountered an error
    }
    
    /**
     * Backend information
     */
    public static class BackendInfo {
        private final BackendInterface.BackendType type;
        private BackendStatus status;
        private BackendInterface.BackendCapabilities capabilities;
        private long lastChecked;
        private long registeredAt;
        private long unregisteredAt;
        
        public BackendInfo(BackendInterface.BackendType type, BackendStatus status) {
            this.type = type;
            this.status = status;
        }
        
        public BackendInterface.BackendType getType() { return type; }
        public BackendStatus getStatus() { return status; }
        public void setStatus(BackendStatus status) { this.status = status; }
        
        public BackendInterface.BackendCapabilities getCapabilities() { return capabilities; }
        public void setCapabilities(BackendInterface.BackendCapabilities capabilities) { 
            this.capabilities = capabilities; 
        }
        
        public long getLastChecked() { return lastChecked; }
        public void setLastChecked(long lastChecked) { this.lastChecked = lastChecked; }
        
        public long getRegisteredAt() { return registeredAt; }
        public void setRegisteredAt(long registeredAt) { this.registeredAt = registeredAt; }
        
        public long getUnregisteredAt() { return unregisteredAt; }
        public void setUnregisteredAt(long unregisteredAt) { this.unregisteredAt = unregisteredAt; }
    }
    
    /**
     * Discovery configuration
     */
    public static class DiscoveryConfig {
        private long discoveryIntervalMs = 60000; // Check every 60 seconds
        private int maxRetryAttempts = 3;
        private long retryDelayMs = 5000; // Retry after 5 seconds
        private boolean enableHotSwap = true;
        
        public long getDiscoveryIntervalMs() { return discoveryIntervalMs; }
        public void setDiscoveryIntervalMs(long interval) { this.discoveryIntervalMs = interval; }
        
        public int getMaxRetryAttempts() { return maxRetryAttempts; }
        public void setMaxRetryAttempts(int attempts) { this.maxRetryAttempts = attempts; }
        
        public long getRetryDelayMs() { return retryDelayMs; }
        public void setRetryDelayMs(long delay) { this.retryDelayMs = delay; }
        
        public boolean isEnableHotSwap() { return enableHotSwap; }
        public void setEnableHotSwap(boolean enable) { this.enableHotSwap = enable; }
    }
}
