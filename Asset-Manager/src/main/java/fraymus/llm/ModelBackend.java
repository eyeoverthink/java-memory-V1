package fraymus.llm;

/**
 * Supported substrate backends for a model family.
 */
public enum ModelBackend {
    OLLAMA,
    GGUF_DIRECT,
    OPENAI_COMPATIBLE,
    REMOTE_ONLY
}
