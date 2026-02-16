package fraymus.quantum.state;

import fraymus.PhiConstants;

/**
 * Represents a quantum state in bra-ket notation
 * Format: ⟨label|φ^power⟩ or ⟨label|φ^power|unit⟩
 */
public class QuantumState {
    
    public final String label;      // State label (τ, ψ_0, ψ_1, M, etc.)
    public final double phiPower;   // Exponent of φ
    public final String unit;       // Optional unit (J⋅s, m/s, etc.)
    
    public QuantumState(String label, double phiPower) {
        this(label, phiPower, null);
    }
    
    public QuantumState(String label, double phiPower, String unit) {
        this.label = label;
        this.phiPower = phiPower;
        this.unit = unit;
    }
    
    /**
     * Convert to bra-ket notation string
     * @return Quantum state string
     */
    @Override
    public String toString() {
        String base = String.format("⟨%s|φ^%.3f⟩", label, phiPower);
        return unit != null ? String.format("⟨%s|φ^%.3f|%s⟩", label, phiPower, unit) : base;
    }
    
    /**
     * Create a temporal state
     * @param resonance Temporal resonance value
     * @return Temporal quantum state
     */
    public static QuantumState temporal(double resonance) {
        return new QuantumState("τ", resonance);
    }
    
    /**
     * Create a base state (ψ_0)
     * @param resonance Base resonance value
     * @return Base quantum state
     */
    public static QuantumState base(double resonance) {
        return new QuantumState("ψ_0", resonance);
    }
    
    /**
     * Create a secondary state (ψ_1)
     * @param resonance Secondary resonance value
     * @return Secondary quantum state
     */
    public static QuantumState secondary(double resonance) {
        return new QuantumState("ψ_1", resonance);
    }
    
    /**
     * Create a memory state (M|φ)
     * @return Memory quantum state
     */
    public static QuantumState memory() {
        return new QuantumState("M", PhiConstants.PHI);
    }
    
    /**
     * Create a custom state
     * @param label State label
     * @param phiPower Phi exponent
     * @return Custom quantum state
     */
    public static QuantumState custom(String label, double phiPower) {
        return new QuantumState(label, phiPower);
    }
    
    /**
     * Create a state with unit
     * @param label State label
     * @param phiPower Phi exponent
     * @param unit Unit string
     * @return Quantum state with unit
     */
    public static QuantumState withUnit(String label, double phiPower, String unit) {
        return new QuantumState(label, phiPower, unit);
    }
}
