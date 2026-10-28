package brain_architecture.core;

/**
 * Brain types for the Quantum Oracle system.
 * Each brain is a specialized subsystem with its own backend ensemble.
 */
public enum BrainType {
    PHYSICAL("physical", "Motor cortex, sensory, coordination"),
    QUANTUM("quantum", "Entanglement, superposition, coherence"),
    FRACTAL("fractal", "Pattern recognition, recursive thinking"),
    CREATIVE("creative", "Imagination, synthesis, innovation"),
    LOGICAL("logical", "Analysis, reasoning, problem solving"),
    EMOTIONAL("emotional", "Empathy, intuition, feeling"),
    SPIRITUAL("spiritual", "Consciousness, awareness, connection"),
    TACHYONIC("tachyonic", "FTL processing, superluminal transfer"),
    VISUAL("visual", "Visual generation and understanding"),
    AUDITORY("auditory", "Speech and audio processing"),
    EVOLUTIONARY("evolutionary", "Self-evolving logic circuits"),
    MEMORY("memory", "Persistent cross-modal knowledge");
    
    private final String id;
    private final String description;
    
    BrainType(String id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
}
