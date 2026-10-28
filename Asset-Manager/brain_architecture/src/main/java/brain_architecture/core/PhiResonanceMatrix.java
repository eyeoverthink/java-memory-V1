package brain_architecture.core;

import brain_architecture.communication.PhiResonancePattern;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Phi-harmonic resonance matrix for inter-brain communication.
 * Implements 7-dimensional resonance pathways with enhanced phi-resonance patterns.
 */
public class PhiResonanceMatrix {
    private static final double PHI = 1.618033988749895;
    
    /**
     * Send a message to another brain via phi-resonance
     */
    public CompletableFuture<QuantumBrain.BrainResponse> sendToBrain(
        BrainType sourceBrain,
        BrainType targetBrain,
        String message
    ) {
        // Create phi-harmonic resonance pattern
        PhiResonancePattern pattern = new PhiResonancePattern(sourceBrain, targetBrain);
        
        // Calculate transfer time based on resonance (higher resonance = faster)
        long transferTimeMs = (long) (100 * (1.0 - pattern.getResonance()));
        
        // Simulate phi-harmonic transfer
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(transferTimeMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Return response with phi-resonance metadata
            return new QuantumBrain.BrainResponse(
                targetBrain,
                "[PHI-RESONANCE] Message received via phi-harmonic coupling",
                pattern.getResonance(),
                transferTimeMs,
                true,
                Map.of(
                    "resonance", pattern.getResonance(),
                    "amplitude", pattern.getAmplitude(),
                    "frequency", pattern.getFrequency(),
                    "phase", pattern.getPhase(),
                    "sourceBrain", sourceBrain.getId(),
                    "phi", PHI,
                    "pattern", pattern.toString()
                )
            );
        });
    }
    
    /**
     * Receive a message from another brain
     */
    public void receiveFromBrain(BrainType sourceBrain, BrainType targetBrain, String message) {
        // Create phi-harmonic resonance pattern
        PhiResonancePattern pattern = new PhiResonancePattern(sourceBrain, targetBrain);
        
        // Process received message
        System.out.println("[PHI-RESONANCE] Received from " + sourceBrain.getId() + 
                          " to " + targetBrain.getId() + 
                          " with resonance: " + pattern.getResonance() +
                          ", amplitude: " + pattern.getAmplitude() +
                          ", frequency: " + pattern.getFrequency() + "Hz");
    }
    
    /**
     * Get phi-resonance pattern between two brains
     */
    public PhiResonancePattern getResonancePattern(BrainType source, BrainType target) {
        return new PhiResonancePattern(source, target);
    }
    
    /**
     * Calculate phi-harmonic resonance pattern between two brains
     * Based on 7-dimensional resonance matrix
     * @deprecated Use getResonancePattern() instead
     */
    @Deprecated
    public double calculateResonancePattern(BrainType source, BrainType target) {
        PhiResonancePattern pattern = new PhiResonancePattern(source, target);
        return pattern.getResonance();
    }
    
    /**
     * Calculate Fibonacci number
     */
    private int fibonacci(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }
}
