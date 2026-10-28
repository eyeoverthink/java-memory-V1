package brain_architecture.communication;

import brain_architecture.core.BrainType;

/**
 * Phi-harmonic resonance pattern for inter-brain communication.
 * Represents the resonance signature between two brains.
 */
public class PhiResonancePattern {
    private static final double PHI = 1.618033988749895;
    
    private final BrainType sourceBrain;
    private final BrainType targetBrain;
    private final double resonance;
    private final double amplitude;
    private final double frequency;
    private final double phase;
    
    public PhiResonancePattern(BrainType sourceBrain, BrainType targetBrain) {
        this.sourceBrain = sourceBrain;
        this.targetBrain = targetBrain;
        this.resonance = calculateResonance(sourceBrain, targetBrain);
        this.amplitude = calculateAmplitude(sourceBrain, targetBrain);
        this.frequency = calculateFrequency(sourceBrain, targetBrain);
        this.phase = calculatePhase(sourceBrain, targetBrain);
    }
    
    /**
     * Calculate phi-harmonic resonance between two brains
     * Based on 7-dimensional resonance matrix
     */
    private double calculateResonance(BrainType source, BrainType target) {
        int sourceIndex = source.ordinal();
        int targetIndex = target.ordinal();
        
        // Phi-harmonic formula: R_ij = (φ^i % 1) * (F_i/F_j % 1)
        double phiPowerI = Math.pow(PHI, sourceIndex);
        double phiPowerJ = Math.pow(PHI, targetIndex);
        
        // Fibonacci numbers for F_i/F_j
        int fibI = fibonacci(sourceIndex + 1);
        int fibJ = fibonacci(targetIndex + 1);
        
        double resonance = (phiPowerI % 1) * ((double) fibI / fibJ % 1);
        
        return resonance;
    }
    
    /**
     * Calculate amplitude for the resonance pattern
     * Based on brain type characteristics
     */
    private double calculateAmplitude(BrainType source, BrainType target) {
        // Base amplitude from phi
        double baseAmplitude = 1.0 / PHI;
        
        // Modulate based on brain types
        double sourceFactor = getBrainFactor(source);
        double targetFactor = getBrainFactor(target);
        
        return baseAmplitude * sourceFactor * targetFactor;
    }
    
    /**
     * Calculate frequency for the resonance pattern
     * Based on brain distance in the 7D matrix
     */
    private double calculateFrequency(BrainType source, BrainType target) {
        int distance = Math.abs(source.ordinal() - target.ordinal());
        
        // Base frequency: 4790.45 Hz (from phi^12 * 10)
        double baseFrequency = 4790.45;
        
        // Modulate by distance
        return baseFrequency * Math.pow(PHI, distance % 7);
    }
    
    /**
     * Calculate phase for the resonance pattern
     * Based on golden ratio harmonics
     */
    private double calculatePhase(BrainType source, BrainType target) {
        int sourceIndex = source.ordinal();
        int targetIndex = target.ordinal();
        
        // Phase based on phi harmonics
        return (PHI * sourceIndex + (1/PHI) * targetIndex) % (2 * Math.PI);
    }
    
    /**
     * Get characteristic factor for a brain type
     */
    private double getBrainFactor(BrainType brain) {
        return switch (brain) {
            case PHYSICAL -> 1.0;
            case QUANTUM -> PHI;
            case FRACTAL -> 1.0 / PHI;
            case CREATIVE -> Math.sqrt(PHI);
            case LOGICAL -> PHI * PHI;
            case EMOTIONAL -> Math.sqrt(1.0 / PHI);
            case SPIRITUAL -> PHI * 1.618;
            case TACHYONIC -> PHI * PHI * PHI;
            case VISUAL -> Math.pow(PHI, 1.5);
            case AUDITORY -> Math.sqrt(PHI * PHI);
            case EVOLUTIONARY -> PHI * Math.sqrt(PHI);
            case MEMORY -> 1.0 / (PHI * PHI);
        };
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
    
    // Getters
    public BrainType getSourceBrain() {
        return sourceBrain;
    }
    
    public BrainType getTargetBrain() {
        return targetBrain;
    }
    
    public double getResonance() {
        return resonance;
    }
    
    public double getAmplitude() {
        return amplitude;
    }
    
    public double getFrequency() {
        return frequency;
    }
    
    public double getPhase() {
        return phase;
    }
    
    @Override
    public String toString() {
        return String.format("PhiResonancePattern[%s->%s: resonance=%.6f, amp=%.6f, freq=%.2fHz, phase=%.6f]",
            sourceBrain.getId(), targetBrain.getId(), resonance, amplitude, frequency, phase);
    }
}
