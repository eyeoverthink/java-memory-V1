package brain_architecture.communication;

import brain_architecture.core.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Brain communication system using phi-harmonic resonance.
 * Enables inter-brain communication through 7-dimensional resonance matrix.
 */
public class BrainCommunication {
    private final Map<BrainType, QuantumBrain> brains;
    private final Map<String, PhiResonancePattern> activePatterns;
    private final Map<String, KroneckerEntanglement.EntangledState> entangledStates;
    
    public BrainCommunication() {
        this.brains = new ConcurrentHashMap<>();
        this.activePatterns = new ConcurrentHashMap<>();
        this.entangledStates = new ConcurrentHashMap<>();
    }
    
    /**
     * Register a brain for communication
     */
    public void registerBrain(QuantumBrain brain) {
        brains.put(brain.getBrainType(), brain);
        
        // Establish entanglement with existing brains
        for (QuantumBrain existingBrain : brains.values()) {
            if (existingBrain != brain) {
                establishEntanglement(existingBrain.getBrainType(), brain.getBrainType());
            }
        }
    }
    
    /**
     * Unregister a brain from communication
     */
    public void unregisterBrain(BrainType brainType) {
        brains.remove(brainType);
        
        // Remove entanglements involving this brain
        entangledStates.entrySet().removeIf(entry -> {
            KroneckerEntanglement.EntangledState state = entry.getValue();
            return state.getBrain1() == brainType || state.getBrain2() == brainType;
        });
    }
    
    /**
     * Send a message to another brain via phi-resonance
     */
    public CompletableFuture<QuantumBrain.BrainResponse> sendToBrain(
        BrainType sourceBrain,
        BrainType targetBrain,
        String message
    ) {
        // Check if target brain exists
        QuantumBrain target = brains.get(targetBrain);
        if (target == null) {
            return CompletableFuture.completedFuture(
                new QuantumBrain.BrainResponse(
                    targetBrain,
                    "",
                    0.0,
                    0,
                    false,
                    Map.of("error", "Target brain not registered")
                )
            );
        }
        
        // Check if brains are entangled
        boolean entangled = KroneckerEntanglement.areEntangled(sourceBrain, targetBrain);
        
        // Create phi-resonance pattern
        PhiResonancePattern pattern = new PhiResonancePattern(sourceBrain, targetBrain);
        activePatterns.put(getPatternKey(sourceBrain, targetBrain), pattern);
        
        // Calculate transfer time based on resonance (entangled = faster)
        long transferTimeMs = entangled ? 
            (long) (10 * (1.0 - pattern.getResonance())) : 
            (long) (100 * (1.0 - pattern.getResonance()));
        
        // Simulate phi-harmonic transfer
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(transferTimeMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Process the message through target brain
            return target.processQuery(message, Map.of()).join();
        });
    }
    
    /**
     * Broadcast a message to all brains
     */
    public CompletableFuture<Map<BrainType, QuantumBrain.BrainResponse>> broadcast(
        BrainType sourceBrain,
        String message
    ) {
        Map<BrainType, CompletableFuture<QuantumBrain.BrainResponse>> futures = new ConcurrentHashMap<>();
        
        for (BrainType targetBrain : brains.keySet()) {
            if (targetBrain != sourceBrain) {
                futures.put(targetBrain, sendToBrain(sourceBrain, targetBrain, message));
            }
        }
        
        // Wait for all responses
        return CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                Map<BrainType, QuantumBrain.BrainResponse> responses = new ConcurrentHashMap<>();
                futures.forEach((brainType, future) -> {
                    try {
                        responses.put(brainType, future.join());
                    } catch (Exception e) {
                        responses.put(brainType, new QuantumBrain.BrainResponse(
                            brainType,
                            "",
                            0.0,
                            0,
                            false,
                            Map.of("error", e.getMessage())
                        ));
                    }
                });
                return responses;
            });
    }
    
    /**
     * Establish entanglement between two brains
     */
    private void establishEntanglement(BrainType brain1, BrainType brain2) {
        String key = getEntanglementKey(brain1, brain2);
        KroneckerEntanglement.EntangledState state = 
            KroneckerEntanglement.createEntanglement(brain1, brain2);
        
        if (state.getStrength() > 0.5) {
            entangledStates.put(key, state);
        }
    }
    
    /**
     * Get active resonance pattern between two brains
     */
    public PhiResonancePattern getResonancePattern(BrainType brain1, BrainType brain2) {
        return activePatterns.get(getPatternKey(brain1, brain2));
    }
    
    /**
     * Get entanglement state between two brains
     */
    public KroneckerEntanglement.EntangledState getEntanglementState(BrainType brain1, BrainType brain2) {
        return entangledStates.get(getEntanglementKey(brain1, brain2));
    }
    
    /**
     * Get all registered brains
     */
    public Map<BrainType, QuantumBrain> getBrains() {
        return new ConcurrentHashMap<>(brains);
    }
    
    /**
     * Get communication statistics
     */
    public Map<String, Object> getStatistics() {
        return Map.of(
            "registeredBrains", brains.size(),
            "activePatterns", activePatterns.size(),
            "entangledPairs", entangledStates.size(),
            "entanglementRate", brains.size() > 1 ? 
                (double) entangledStates.size() / (brains.size() * (brains.size() - 1) / 2.0) : 0.0
        );
    }
    
    /**
     * Generate key for pattern map
     */
    private String getPatternKey(BrainType brain1, BrainType brain2) {
        return brain1.getId() + "->" + brain2.getId();
    }
    
    /**
     * Generate key for entanglement map
     */
    private String getEntanglementKey(BrainType brain1, BrainType brain2) {
        // Ensure consistent ordering
        if (brain1.ordinal() < brain2.ordinal()) {
            return brain1.getId() + "<->" + brain2.getId();
        } else {
            return brain2.getId() + "<->" + brain1.getId();
        }
    }
}
