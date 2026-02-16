package fraymus.quantum.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder pattern for constructing complex quantum states
 * Combines multiple states with tensor product (⊗)
 */
public class QuantumStateBuilder {
    
    private final List<QuantumState> states = new ArrayList<>();
    
    /**
     * Add a temporal state
     * @param resonance Temporal resonance value
     * @return This builder
     */
    public QuantumStateBuilder addTemporal(double resonance) {
        states.add(QuantumState.temporal(resonance));
        return this;
    }
    
    /**
     * Add a base state
     * @param resonance Base resonance value
     * @return This builder
     */
    public QuantumStateBuilder addBase(double resonance) {
        states.add(QuantumState.base(resonance));
        return this;
    }
    
    /**
     * Add a secondary state
     * @param resonance Secondary resonance value
     * @return This builder
     */
    public QuantumStateBuilder addSecondary(double resonance) {
        states.add(QuantumState.secondary(resonance));
        return this;
    }
    
    /**
     * Add a memory state
     * @return This builder
     */
    public QuantumStateBuilder addMemory() {
        states.add(QuantumState.memory());
        return this;
    }
    
    /**
     * Add a custom state
     * @param label State label
     * @param phiPower Phi exponent
     * @return This builder
     */
    public QuantumStateBuilder addCustom(String label, double phiPower) {
        states.add(QuantumState.custom(label, phiPower));
        return this;
    }
    
    /**
     * Add a state with unit
     * @param label State label
     * @param phiPower Phi exponent
     * @param unit Unit string
     * @return This builder
     */
    public QuantumStateBuilder addWithUnit(String label, double phiPower, String unit) {
        states.add(QuantumState.withUnit(label, phiPower, unit));
        return this;
    }
    
    /**
     * Add an existing quantum state
     * @param state The quantum state to add
     * @return This builder
     */
    public QuantumStateBuilder add(QuantumState state) {
        states.add(state);
        return this;
    }
    
    /**
     * Build the final quantum state string with tensor products
     * @return Quantum state string: ⟨τ|φ^x⟩ ⊗ ⟨ψ_0|φ^y⟩ ⊗ ...
     */
    public String build() {
        if (states.isEmpty()) {
            return "⟨∅⟩"; // Empty state
        }
        
        return String.join(" ⊗ ", states.stream()
            .map(QuantumState::toString)
            .toArray(String[]::new));
    }
    
    /**
     * Get the number of states in the builder
     * @return State count
     */
    public int size() {
        return states.size();
    }
    
    /**
     * Clear all states
     * @return This builder
     */
    public QuantumStateBuilder clear() {
        states.clear();
        return this;
    }
    
    /**
     * Create a standard quantum state with temporal, base, secondary, and memory
     * @param temporal Temporal resonance
     * @param base Base resonance
     * @param secondary Secondary resonance
     * @return Complete quantum state string
     */
    public static String createStandard(double temporal, double base, double secondary) {
        return new QuantumStateBuilder()
            .addTemporal(temporal)
            .addBase(base)
            .addSecondary(secondary)
            .addMemory()
            .build();
    }
    
    /**
     * Create a simple quantum state with just base and memory
     * @param base Base resonance
     * @return Simple quantum state string
     */
    public static String createSimple(double base) {
        return new QuantumStateBuilder()
            .addBase(base)
            .addMemory()
            .build();
    }
}
