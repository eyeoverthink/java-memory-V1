package fraymus.visual;

/**
 * A single moment of AI cognition, captured and analyzed for visual
 * manifestation. Part of the Max Headroom Protocol.
 *
 * @see ThoughtAnalyzer
 * @see ReactiveVisualAI
 */
public final class ThoughtFrame {

    public final String thought;          // The actual text
    public final double entropy;          // Chaos vs order (0.0 - 1.0)
    public final double confidence;       // Certainty level (0.0 - 1.0)
    public final long timestamp;          // When it occurred (System.currentTimeMillis())
    public final boolean isConclusion;    // Is this the final answer?
    public final String indicator;        // Visual indicator emoji

    public ThoughtFrame(String thought, double entropy, double confidence,
                        long timestamp, boolean isConclusion, String indicator) {
        this.thought = thought;
        this.entropy = entropy;
        this.confidence = confidence;
        this.timestamp = timestamp;
        this.isConclusion = isConclusion;
        this.indicator = indicator;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (entropy=%.2f confidence=%.2f)%s",
            indicator, thought.substring(0, Math.min(50, thought.length())),
            entropy, confidence, isConclusion ? " [CONCLUSION]" : "");
    }
}
