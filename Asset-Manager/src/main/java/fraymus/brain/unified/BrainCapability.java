package fraymus.brain.unified;

/**
 * Capabilities that a brain can possess.
 * Used for brain selection based on query requirements.
 */
public enum BrainCapability {
    REASONING("Logical reasoning and deduction"),
    CREATIVITY("Creative thinking and imagination"),
    EMOTION("Emotional processing and empathy"),
    PHYSICS("Physics simulation and computation"),
    QUANTUM("Quantum mechanics and entanglement"),
    MEMORY("Memory storage and retrieval"),
    TEMPORAL("Temporal reasoning and time perception"),
    CONSCIOUSNESS("Self-awareness and consciousness"),
    MATHEMATICS("Mathematical computation and analysis"),
    LANGUAGE("Natural language processing"),
    VISION("Visual processing and recognition"),
    AUDIO("Audio processing and recognition"),
    EVOLUTION("Evolutionary algorithms and adaptation"),
    SPIRITUAL("Spiritual and metaphysical reasoning"),
    TACHYONIC("Faster-than-light processing and communication"),
    FRACTAL("Fractal pattern recognition and self-similarity"),
    DATA("Data processing and information management");

    private final String description;

    BrainCapability(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
