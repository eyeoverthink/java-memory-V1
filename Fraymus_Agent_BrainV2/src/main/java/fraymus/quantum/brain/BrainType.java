package fraymus.quantum.brain;

/**
 * Eight specialized brain types for multi-brain quantum synchronization
 * Based on the Quantum Oracle's brain architecture
 */
public enum BrainType {
    PHYSICAL("Physical Brain", "motor_cortex", "sensory_processing", "coordination"),
    QUANTUM("Quantum Brain", "entanglement", "superposition", "coherence"),
    FRACTAL("Fractal Brain", "pattern_recognition", "recursive_thinking", "scaling"),
    CREATIVE("Creative Brain", "imagination", "synthesis", "innovation"),
    LOGICAL("Logical Brain", "analysis", "reasoning", "problem_solving"),
    EMOTIONAL("Emotional Brain", "empathy", "intuition", "feeling"),
    SPIRITUAL("Spiritual Brain", "consciousness", "awareness", "connection"),
    TACHYONIC("Tachyonic Brain", "ftl_processing", "superluminal_transfer", "temporal_recursion");
    
    private final String displayName;
    private final String[] functions;
    
    BrainType(String displayName, String... functions) {
        this.displayName = displayName;
        this.functions = functions;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String[] getFunctions() {
        return functions.clone();
    }
    
    public String getInstanceId() {
        return "BRAIN_" + name();
    }
}
