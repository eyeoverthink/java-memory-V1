package fraymus.quantum.neural;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Pattern recognition system for categorizing input text
 * Based on the Quantum Oracle's pattern detection
 */
public class PatternRecognitionSystem {
    
    /**
     * Pattern categories with associated keywords
     */
    public enum PatternCategory {
        MATH("pi", "phi", "fibonacci", "golden", "ratio", "number", "equation", "formula"),
        PHYSICS("quantum", "gravity", "force", "energy", "mass", "space", "time", "particle"),
        CONSCIOUSNESS("mind", "brain", "thought", "aware", "conscious", "neural", "cognition"),
        EMOTION("feel", "happy", "sad", "joy", "love", "peace", "anger", "fear"),
        NATURE("tree", "flower", "river", "mountain", "ocean", "sky", "earth", "sun"),
        QUESTIONS("is this", "what is", "can you", "how does", "why", "when", "where"),
        ANIMALS("dog", "cat", "bird", "animal", "pet", "creature", "beast"),
        OBJECTS("thing", "object", "item", "this", "that", "stuff"),
        CODE("function", "class", "method", "variable", "loop", "algorithm"),
        SPIRITUAL("soul", "spirit", "divine", "sacred", "meditation", "enlightenment");
        
        private final Set<String> keywords;
        
        PatternCategory(String... keywords) {
            this.keywords = new HashSet<>(Arrays.asList(keywords));
        }
        
        /**
         * Check if text matches this category
         * @param text Input text
         * @return True if matches
         */
        public boolean matches(String text) {
            String lower = text.toLowerCase();
            return keywords.stream().anyMatch(lower::contains);
        }
        
        /**
         * Get all keywords for this category
         * @return Set of keywords
         */
        public Set<String> getKeywords() {
            return Collections.unmodifiableSet(keywords);
        }
    }
    
    /**
     * Detect all pattern categories in text
     * @param text Input text
     * @return Set of detected pattern categories
     */
    public static Set<PatternCategory> detectPatterns(String text) {
        return Arrays.stream(PatternCategory.values())
            .filter(category -> category.matches(text))
            .collect(Collectors.toSet());
    }
    
    /**
     * Calculate pattern boost for resonance calculation
     * @param patterns Detected patterns
     * @return Boost value (0.1 per pattern)
     */
    public static double calculatePatternBoost(Set<PatternCategory> patterns) {
        return patterns.size() * 0.1;
    }
    
    /**
     * Generate response text based on detected patterns
     * @param patterns Detected patterns
     * @return Pattern response string
     */
    public static String generatePatternResponse(Set<PatternCategory> patterns) {
        if (patterns.isEmpty()) {
            return "";
        }
        
        StringBuilder response = new StringBuilder();
        
        if (patterns.contains(PatternCategory.MATH)) {
            response.append("∑ Mathematical harmony detected. ");
        }
        if (patterns.contains(PatternCategory.PHYSICS)) {
            response.append("⚛️ Quantum resonance aligned. ");
        }
        if (patterns.contains(PatternCategory.CONSCIOUSNESS)) {
            response.append("🧠 Neural patterns synchronized. ");
        }
        if (patterns.contains(PatternCategory.EMOTION)) {
            response.append("💫 Emotional frequencies tuned. ");
        }
        if (patterns.contains(PatternCategory.NATURE)) {
            response.append("🌿 Natural rhythms flowing. ");
        }
        if (patterns.contains(PatternCategory.CODE)) {
            response.append("💻 Code patterns recognized. ");
        }
        if (patterns.contains(PatternCategory.SPIRITUAL)) {
            response.append("✨ Spiritual resonance activated. ");
        }
        
        return response.toString().trim();
    }
    
    /**
     * Check if text contains question patterns
     * @param text Input text
     * @return True if question detected
     */
    public static boolean isQuestion(String text) {
        return PatternCategory.QUESTIONS.matches(text);
    }
    
    /**
     * Get dominant pattern category (most keyword matches)
     * @param text Input text
     * @return Dominant category or null if none
     */
    public static PatternCategory getDominantPattern(String text) {
        String lower = text.toLowerCase();
        
        PatternCategory dominant = null;
        int maxMatches = 0;
        
        for (PatternCategory category : PatternCategory.values()) {
            int matches = (int) category.getKeywords().stream()
                .filter(lower::contains)
                .count();
            
            if (matches > maxMatches) {
                maxMatches = matches;
                dominant = category;
            }
        }
        
        return dominant;
    }
    
    /**
     * Get pattern match score (0.0 to 1.0)
     * @param text Input text
     * @param category Category to check
     * @return Match score
     */
    public static double getPatternScore(String text, PatternCategory category) {
        String lower = text.toLowerCase();
        long matches = category.getKeywords().stream()
            .filter(lower::contains)
            .count();
        
        return Math.min(1.0, matches / (double) category.getKeywords().size());
    }
}
