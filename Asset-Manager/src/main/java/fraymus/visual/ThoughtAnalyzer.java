package fraymus.visual;

import java.util.List;
import java.util.Locale;

/**
 * THOUGHT ANALYZER
 * Parses raw text into {@link ThoughtFrame}s and calculates entropy
 * (chaos vs order) and confidence (certainty) for visual manifestation.
 *
 * <p>Entropy scale:
 * <ul>
 *   <li>0.7 - 1.0 : Chaotic thinking (turbulent vortex visuals)</li>
 *   <li>0.3 - 0.7 : Balanced thinking (flowing patterns)</li>
 *   <li>0.0 - 0.3 : Crystalline thinking (perfect geometry)</li>
 * </ul>
 *
 * <p>Confidence scale:
 * <ul>
 *   <li>0.8 - 1.0 : High certainty (radiant light)</li>
 *   <li>0.4 - 0.7 : Medium certainty (soft ambient glow)</li>
 *   <li>0.0 - 0.4 : Low certainty (dim twilight)</li>
 * </ul>
 */
public final class ThoughtAnalyzer {

    private static final List<String> UNCERTAINTY_MARKERS = List.of(
        "maybe", "perhaps", "might", "uncertain", "not sure", "possibly",
        "could be", "i think", "i believe", "i guess", "probably",
        "seems", "appears", "likely", "unlikely", "doubt", "wonder",
        "what if", "suppose", "assume", "presumably"
    );

    private static final List<String> CERTAINTY_MARKERS = List.of(
        "definitely", "clearly", "obviously", "certainly", "must be",
        "is", "are", "therefore", "conclusion", "proven", "fact",
        "undeniable", "absolutely", "without doubt", "conclusive",
        "established", "verified", "confirmed", "indeed", "exactly"
    );

    private static final List<String> CONCLUSION_MARKERS = List.of(
        "therefore", "conclusion", "thus", "hence", "in conclusion",
        "to conclude", "finally", "overall", "in summary", "so",
        "as a result", "consequently", "in short"
    );

    private ThoughtAnalyzer() {}

    /**
     * Parse a multi-line response into individual thought frames.
     * Each non-empty line becomes a frame with calculated entropy and confidence.
     */
    public static List<ThoughtFrame> parseThoughts(String response) {
        List<ThoughtFrame> frames = new java.util.ArrayList<>();
        if (response == null || response.isBlank()) {
            return frames;
        }
        String[] lines = response.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.length() < 5) {
                continue; // skip empty/very short lines
            }
            frames.add(analyze(trimmed));
        }
        return frames;
    }

    /**
     * Analyze a single thought line into a {@link ThoughtFrame}.
     */
    public static ThoughtFrame analyze(String thought) {
        double entropy = calculateEntropy(thought);
        double confidence = calculateConfidence(thought);
        boolean isConclusion = detectConclusion(thought);
        String indicator = selectIndicator(entropy, confidence, isConclusion);
        return new ThoughtFrame(thought, entropy, confidence,
            System.currentTimeMillis(), isConclusion, indicator);
    }

    /**
     * Entropy measures how chaotic or ordered a thought is.
     *
     * <pre>
     * entropy = lengthFactor * 0.3
     *         + uncertaintyFactor * 0.5
     *         - certaintyFactor * 0.3
     *         + 0.3
     * </pre>
     *
     * Clamped to [0.0, 1.0].
     */
    public static double calculateEntropy(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        int wordCount = countWords(text);

        // Length factor: longer thoughts → higher entropy, capped
        double lengthFactor = Math.min(1.0, wordCount / 30.0); // 30+ words = max

        // Uncertainty markers
        double uncertaintyFactor = countMarkers(lower, UNCERTAINTY_MARKERS) * 0.15;

        // Certainty markers
        double certaintyFactor = countMarkers(lower, CERTAINTY_MARKERS) * 0.15;

        double entropy = lengthFactor * 0.3
                       + uncertaintyFactor * 0.5
                       - certaintyFactor * 0.3
                       + 0.3;

        return clamp(entropy, 0.0, 1.0);
    }

    /**
     * Confidence measures how certain the AI is.
     *
     * <pre>
     * confidence = 0.5 (baseline)
     *            + certaintyMarkers * 0.2
     *            - uncertaintyMarkers * 0.2
     * </pre>
     *
     * Clamped to [0.0, 1.0].
     */
    public static double calculateConfidence(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        double certaintyCount = countMarkers(lower, CERTAINTY_MARKERS);
        double uncertaintyCount = countMarkers(lower, UNCERTAINTY_MARKERS);

        double confidence = 0.5
                          + certaintyCount * 0.2
                          - uncertaintyCount * 0.2;

        return clamp(confidence, 0.0, 1.0);
    }

    /**
     * Detect if a thought is a conclusion/final statement.
     */
    public static boolean detectConclusion(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        for (String marker : CONCLUSION_MARKERS) {
            if (lower.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Select a visual indicator based on entropy and confidence.
     */
    public static String selectIndicator(double entropy, double confidence, boolean isConclusion) {
        if (isConclusion && entropy < 0.3 && confidence > 0.7) {
            return "\uD83D\uDC8E"; // Crystalline certainty
        }
        if (entropy < 0.3 && confidence > 0.6) {
            return "\u2728"; // Ordered clarity
        }
        if (entropy < 0.3 && confidence < 0.4) {
            return "\u2753"; // Uncertain questioning (ordered but unsure)
        }
        if (entropy > 0.7) {
            return "\uD83D\uDCAC"; // Chaotic exploration
        }
        if (confidence < 0.4) {
            return "\u2753"; // Uncertain questioning
        }
        return "\uD83D\uDCAD"; // Normal thought
    }

    // =====================================================================
    // Internals
    // =====================================================================

    private static int countWords(String text) {
        int count = 0;
        boolean inWord = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                if (!inWord) {
                    inWord = true;
                    count++;
                }
            } else {
                inWord = false;
            }
        }
        return count;
    }

    private static double countMarkers(String text, List<String> markers) {
        double count = 0;
        for (String marker : markers) {
            int idx = 0;
            while ((idx = text.indexOf(marker, idx)) != -1) {
                count++;
                idx += marker.length();
            }
        }
        return count;
    }

    private static double clamp(double v, double min, double max) {
        return v < min ? min : (v > max ? max : v);
    }
}
