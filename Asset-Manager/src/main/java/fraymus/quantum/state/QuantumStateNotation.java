package fraymus.quantum.state;

import fraymus.quantum.core.PhiHarmonicMath;
import java.util.Arrays;

/**
 * Quantum State Notation System
 * 
 * Bra-ket notation: ⟨state|value⟩
 * Tensor product: ⊗ (combines quantum states)
 * Phi exponentiation: φ^x represents resonance level
 */
public class QuantumStateNotation {
    
    public static class QuantumState {
        public final String label;      // τ, ψ_0, ψ_1, M, etc.
        public final double phiPower;   // Exponent of φ
        public final String unit;       // Optional unit
        public final StateType type;    // Category of state
        
        public QuantumState(String label, double phiPower) {
            this(label, phiPower, null, StateType.GENERIC);
        }
        
        public QuantumState(String label, double phiPower, String unit) {
            this(label, phiPower, unit, StateType.GENERIC);
        }
        
        public QuantumState(String label, double phiPower, String unit, StateType type) {
            this.label = label;
            this.phiPower = phiPower;
            this.unit = unit;
            this.type = type;
        }
        
        @Override
        public String toString() {
            if (unit != null) {
                return String.format("⟨%s|φ^%.3f|%s⟩", label, phiPower, unit);
            }
            return String.format("⟨%s|φ^%.3f⟩", label, phiPower);
        }
        
        public String toSymbol() {
            return type.getSymbol();
        }
    }
    
    public enum StateType {
        TEMPORAL("τ", "Temporal state"),
        BASE("ψ_0", "Base quantum state"),
        SECONDARY("ψ_1", "Secondary quantum state"),
        MEMORY("M", "Memory state"),
        PARTICLE("ψ₁", "Particle state"),
        FIELD("ℋ", "Field Hamiltonian"),
        SMATRIX("S", "S-matrix interaction"),
        NEURAL("🧠", "Neural state"),
        COGNITIVE("💡", "Cognitive state"),
        PHYSICS("⚖️", "Physics state"),
        CONSCIOUSNESS("Ω", "Consciousness state"),
        INFINITY("∞", "Infinite state"),
        GENERIC("ψ", "Generic quantum state");
        
        private final String symbol;
        private final String description;
        
        StateType(String symbol, String description) {
            this.symbol = symbol;
            this.description = description;
        }
        
        public String getSymbol() { return symbol; }
        public String getDescription() { return description; }
    }
    
    // Tensor product of multiple states
    public static String tensorProduct(QuantumState... states) {
        if (states.length == 0) return "";
        return String.join(" ⊗ ", Arrays.stream(states)
            .map(QuantumState::toString)
            .toArray(String[]::new));
    }
    
    // Factory methods for common states
    
    public static QuantumState createTemporalState(double resonance) {
        return new QuantumState("τ", resonance, null, StateType.TEMPORAL);
    }
    
    public static QuantumState createBaseState(double resonance) {
        return new QuantumState("ψ_0", resonance, null, StateType.BASE);
    }
    
    public static QuantumState createSecondaryState(double resonance) {
        return new QuantumState("ψ_1", resonance, null, StateType.SECONDARY);
    }
    
    public static QuantumState createMemoryState() {
        return new QuantumState("M", PhiHarmonicMath.PHI, null, StateType.MEMORY);
    }
    
    public static QuantumState createParticleState(String particle, double resonance) {
        return new QuantumState(particle, resonance, null, StateType.PARTICLE);
    }
    
    public static QuantumState createFieldState(String field, double resonance) {
        return new QuantumState(field, resonance, "ℋ", StateType.FIELD);
    }
    
    public static QuantumState createNeuralState(double resonance) {
        return new QuantumState("neural", resonance, "🧠", StateType.NEURAL);
    }
    
    public static QuantumState createConsciousnessState(double resonance) {
        return new QuantumState("Ω", resonance, null, StateType.CONSCIOUSNESS);
    }
    
    // Physics notation helpers
    
    public static String formatPhysicsEquation(String name, double resonance) {
        switch (name.toLowerCase()) {
            case "e=mc2":
            case "e=mc squared":
                return "⟨E|c²|m⟩ ⊗ ℋ_{relativistic} ⊗ exp(-S[φ]/ℏ)";
            case "f=ma":
                return "⟨F|d/dt|p⟩ ⊗ ℋ_{classical}";
            case "schrodinger":
                return "iℏ∂|ψ⟩/∂t = ℋ|ψ⟩";
            case "heisenberg":
                return "⟨Δx|Δp⟩ ≥ ℏ/2";
            case "dirac":
                return "(iγᵘ∂ᵘ - m)|ψ⟩ = 0";
            default:
                return String.format("⟨%s|φ^%.3f⟩", name, resonance);
        }
    }
    
    // Mathematical constants in quantum notation
    
    public static String formatMathConstant(String constant) {
        switch (constant.toLowerCase()) {
            case "pi":
            case "π":
                return "⟨π|3.14159265359|∞⟩";
            case "phi":
            case "φ":
                return String.format("⟨φ|%.15f|∞⟩", PhiHarmonicMath.PHI);
            case "e":
                return "⟨e|2.71828182846|∞⟩";
            case "infinity":
            case "∞":
                return "⟨∞|∞|∞⟩";
            case "planck":
            case "h":
                return "⟨ℏ|1.054571817e-34|J⋅s⟩";
            case "lightspeed":
            case "c":
                return "⟨c|299792458|m/s⟩";
            case "g":
                return "⟨g|9.80665|m/s²⟩";
            default:
                return null;
        }
    }
}
