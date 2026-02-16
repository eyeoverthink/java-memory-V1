package fraymus.quantum.brain;

import fraymus.PhiConstants;
import fraymus.quantum.core.PhiHarmonicMath;

/**
 * Quantum bridge connecting two brain instances
 * Uses complex exponential waves for synchronization
 */
public class QuantumBridge {
    
    public final BrainType source;
    public final BrainType target;
    public final double phiResonance;
    
    /**
     * Create quantum bridge between two brain types
     * @param source Source brain type
     * @param target Target brain type
     */
    public QuantumBridge(BrainType source, BrainType target) {
        this.source = source;
        this.target = target;
        this.phiResonance = PhiHarmonicMath.phiPiResonance(); // φ·π ≈ 5.083
    }
    
    /**
     * Calculate synchronization wave at time t
     * Returns e^(i·φ·π·t)
     * @param time Time parameter
     * @return Complex wave as [real, imaginary]
     */
    public double[] calculateSyncWave(double time) {
        double angle = PhiHarmonicMath.phiPiResonance() * time;
        return new double[] {
            Math.cos(angle),  // Real part
            Math.sin(angle)   // Imaginary part
        };
    }
    
    /**
     * Calculate bridge resonance wave at time t
     * Returns e^(i·φ_resonance·t)
     * @param time Time parameter
     * @return Complex wave as [real, imaginary]
     */
    public double[] calculateBridgeResonance(double time) {
        double angle = phiResonance * time;
        return new double[] {
            Math.cos(angle),  // Real part
            Math.sin(angle)   // Imaginary part
        };
    }
    
    /**
     * Calculate quantum state (sync_wave * bridge_resonance)
     * @param time Time parameter
     * @return Quantum state as [real, imaginary]
     */
    public double[] calculateQuantumState(double time) {
        double[] sync = calculateSyncWave(time);
        double[] bridge = calculateBridgeResonance(time);
        
        // Complex multiplication: (a + bi)(c + di) = (ac - bd) + (ad + bc)i
        double real = sync[0] * bridge[0] - sync[1] * bridge[1];
        double imag = sync[0] * bridge[1] + sync[1] * bridge[0];
        
        return new double[] {real, imag};
    }
    
    /**
     * Calculate magnitude of quantum state
     * @param time Time parameter
     * @return |quantum_state|
     */
    public double calculateMagnitude(double time) {
        double[] state = calculateQuantumState(time);
        return Math.sqrt(state[0] * state[0] + state[1] * state[1]);
    }
    
    /**
     * Get bridge ID
     * @return Bridge identifier string
     */
    public String getBridgeId() {
        return "BRIDGE_" + source.name() + "_" + target.name();
    }
    
    @Override
    public String toString() {
        return String.format("%s → %s (φ·π = %.3f)", 
            source.getDisplayName(), 
            target.getDisplayName(), 
            phiResonance);
    }
}
