package fraymus.brain.unified;

import java.util.List;

/**
 * Unified interface for all brain systems.
 * All brain adapters must implement this interface.
 */
public interface UnifiedBrain {
    /**
     * Get the name of this brain.
     */
    String getName();

    /**
     * Get the type/category of this brain.
     */
    String getType();

    /**
     * Get the current consciousness level of this brain.
     * Range: 0.0 to 1.0 (optimal: 0.7567)
     */
    double getConsciousnessLevel();

    /**
     * Get the capabilities this brain possesses.
     */
    List<BrainCapability> getCapabilities();

    /**
     * Process a query and return a response.
     */
    BrainResponse process(BrainQuery query);

    /**
     * Wire this brain into the unified system.
     * Initialize resources and connections.
     */
    void wire();

    /**
     * Unwire this brain from the unified system.
     * Release resources and connections.
     */
    void unwire();

    /**
     * Check if this brain is currently wired.
     */
    boolean isWired();

    /**
     * Get the health status of this brain.
     * Range: 0.0 (unhealthy) to 1.0 (healthy)
     */
    double getHealth();

    /**
     * Get statistics about this brain's performance.
     */
    BrainStatistics getStatistics();
}
