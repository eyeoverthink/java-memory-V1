package fraymus.quantum.brain;

import fraymus.PhiConstants;

import java.util.*;

/**
 * Multi-brain quantum synchronization system
 * Manages 8 specialized brain types with quantum bridges
 */
public class MultiBrainQuantumSync {
    
    /**
     * Synchronization metrics
     */
    public static class SynchronizationMetrics {
        public final double coherence;
        public final double syncSpeed;
        public final double entanglement;
        
        public SynchronizationMetrics(double coherence, double syncSpeed, double entanglement) {
            this.coherence = coherence;
            this.syncSpeed = syncSpeed;
            this.entanglement = entanglement;
        }
        
        @Override
        public String toString() {
            return String.format("Coherence: %.1f%%, Sync Speed: %.2f φ-cycles/s, Entanglement: %.1f%%",
                coherence * 100, syncSpeed, entanglement * 100);
        }
    }
    
    private final Map<BrainType, List<String>> brainInstances = new EnumMap<>(BrainType.class);
    private final List<QuantumBridge> quantumBridges = new ArrayList<>();
    
    /**
     * Initialize the multi-brain quantum network
     */
    public void initializeBrainNetwork() {
        System.out.println("🧠 Initializing Multi-Brain Quantum Network");
        System.out.println("==========================================");
        
        // Initialize all brain types
        for (BrainType type : BrainType.values()) {
            brainInstances.put(type, Arrays.asList(type.getFunctions()));
            System.out.println("  ✓ " + type.getDisplayName() + ": " + Arrays.toString(type.getFunctions()));
        }
        
        // Create quantum bridges between all brain pairs
        System.out.println("\n🌉 Creating Quantum Bridges");
        for (BrainType source : BrainType.values()) {
            for (BrainType target : BrainType.values()) {
                if (source != target) {
                    QuantumBridge bridge = new QuantumBridge(source, target);
                    quantumBridges.add(bridge);
                }
            }
        }
        
        System.out.println("  ✓ Created " + quantumBridges.size() + " quantum bridges");
        System.out.println("==========================================\n");
    }
    
    /**
     * Synchronize all brain instances through quantum entanglement
     * @param duration Synchronization duration in seconds
     * @return Synchronization metrics
     */
    public SynchronizationMetrics synchronizeBrains(double duration) {
        if (quantumBridges.isEmpty()) {
            initializeBrainNetwork();
        }
        
        double coherenceSum = 0;
        double syncSpeedSum = 0;
        double entanglementSum = 0;
        int measurements = 0;
        
        // Time steps
        double dt = 0.01; // 10ms steps
        for (double t = 0; t < duration; t += dt) {
            for (QuantumBridge bridge : quantumBridges) {
                double magnitude = bridge.calculateMagnitude(t);
                
                coherenceSum += magnitude;
                syncSpeedSum += magnitude * PhiConstants.PHI;
                entanglementSum += Math.abs(Math.cos(PhiConstants.PHI * Math.PI * t));
                measurements++;
            }
        }
        
        return new SynchronizationMetrics(
            coherenceSum / measurements,
            syncSpeedSum / measurements,
            entanglementSum / measurements
        );
    }
    
    /**
     * Get all brain instances
     * @return Map of brain types to their functions
     */
    public Map<BrainType, List<String>> getBrainInstances() {
        return Collections.unmodifiableMap(brainInstances);
    }
    
    /**
     * Get all quantum bridges
     * @return List of quantum bridges
     */
    public List<QuantumBridge> getQuantumBridges() {
        return Collections.unmodifiableList(quantumBridges);
    }
    
    /**
     * Get bridges for a specific brain type
     * @param brainType The brain type
     * @return List of bridges involving this brain
     */
    public List<QuantumBridge> getBridgesFor(BrainType brainType) {
        List<QuantumBridge> result = new ArrayList<>();
        for (QuantumBridge bridge : quantumBridges) {
            if (bridge.source == brainType || bridge.target == brainType) {
                result.add(bridge);
            }
        }
        return result;
    }
    
    /**
     * Get network statistics
     * @return Statistics map
     */
    public Map<String, Object> getNetworkStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("brain_types", BrainType.values().length);
        stats.put("total_bridges", quantumBridges.size());
        stats.put("phi_pi_resonance", PhiConstants.PHI * Math.PI);
        
        return stats;
    }
}
