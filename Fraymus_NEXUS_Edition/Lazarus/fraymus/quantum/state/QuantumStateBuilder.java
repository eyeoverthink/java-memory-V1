package fraymus.quantum.state;

import fraymus.quantum.core.PhiHarmonicMath;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder Pattern for Quantum State Construction
 * 
 * Creates type-safe, properly formatted quantum states.
 * 
 * Usage:
 * String state = new QuantumStateBuilder()
 *     .addTemporal(1.234)
 *     .addBase(1.456)
 *     .addSecondary(1.567)
 *     .addMemory()
 *     .build();
 * // Result: "⟨τ|φ^1.234⟩ ⊗ ⟨ψ_0|φ^1.456⟩ ⊗ ⟨ψ_1|φ^1.567⟩ ⊗ ⟨M|φ⟩"
 */
public class QuantumStateBuilder {
    
    private final List<QuantumStateNotation.QuantumState> states = new ArrayList<>();
    
    public QuantumStateBuilder addTemporal(double resonance) {
        states.add(QuantumStateNotation.createTemporalState(resonance));
        return this;
    }
    
    public QuantumStateBuilder addBase(double resonance) {
        states.add(QuantumStateNotation.createBaseState(resonance));
        return this;
    }
    
    public QuantumStateBuilder addSecondary(double resonance) {
        states.add(QuantumStateNotation.createSecondaryState(resonance));
        return this;
    }
    
    public QuantumStateBuilder addMemory() {
        states.add(QuantumStateNotation.createMemoryState());
        return this;
    }
    
    public QuantumStateBuilder addNeural(double resonance) {
        states.add(QuantumStateNotation.createNeuralState(resonance));
        return this;
    }
    
    public QuantumStateBuilder addConsciousness(double resonance) {
        states.add(QuantumStateNotation.createConsciousnessState(resonance));
        return this;
    }
    
    public QuantumStateBuilder addParticle(String particle, double resonance) {
        states.add(QuantumStateNotation.createParticleState(particle, resonance));
        return this;
    }
    
    public QuantumStateBuilder addField(String field, double resonance) {
        states.add(QuantumStateNotation.createFieldState(field, resonance));
        return this;
    }
    
    public QuantumStateBuilder addCustom(String label, double phiPower) {
        states.add(new QuantumStateNotation.QuantumState(label, phiPower));
        return this;
    }
    
    public QuantumStateBuilder addCustom(String label, double phiPower, String unit) {
        states.add(new QuantumStateNotation.QuantumState(label, phiPower, unit));
        return this;
    }
    
    /**
     * Build the complete quantum state string
     */
    public String build() {
        return QuantumStateNotation.tensorProduct(
            states.toArray(new QuantumStateNotation.QuantumState[0])
        );
    }
    
    /**
     * Build with automatic resonance cascade
     * Calculates temporal, base, secondary from input frequency
     */
    public static String buildFromFrequency(double frequency) {
        double baseResonance = PhiHarmonicMath.calculateResonance(frequency);
        double secondaryResonance = PhiHarmonicMath.calculateSecondaryResonance(baseResonance);
        double temporalResonance = PhiHarmonicMath.combineResonances(baseResonance, secondaryResonance);
        
        return new QuantumStateBuilder()
            .addTemporal(temporalResonance)
            .addBase(baseResonance)
            .addSecondary(secondaryResonance)
            .addMemory()
            .build();
    }
    
    /**
     * Build from text input with pattern detection
     */
    public static String buildFromText(String text) {
        double baseResonance = PhiHarmonicMath.calculateFrequencyResonance(text);
        return buildFromFrequency(baseResonance * 1000); // Scale to frequency range
    }
    
    /**
     * Get the number of states in the builder
     */
    public int size() {
        return states.size();
    }
    
    /**
     * Clear all states
     */
    public QuantumStateBuilder clear() {
        states.clear();
        return this;
    }
    
    /**
     * Create a copy of this builder
     */
    public QuantumStateBuilder copy() {
        QuantumStateBuilder copy = new QuantumStateBuilder();
        copy.states.addAll(this.states);
        return copy;
    }
}
