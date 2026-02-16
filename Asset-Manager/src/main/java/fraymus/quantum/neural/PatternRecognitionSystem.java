package fraymus.quantum.neural;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Pattern Recognition System
 * 
 * Enum-based extensible pattern detection with category-aware processing.
 */
public class PatternRecognitionSystem {
    
    public enum PatternCategory {
        MATH("∑", "Mathematical harmony", "pi", "phi", "fibonacci", "golden", "ratio", "number", "calculate", "equation"),
        PHYSICS("⚛️", "Quantum resonance", "quantum", "gravity", "force", "energy", "mass", "space", "time", "relativity"),
        CONSCIOUSNESS("🧠", "Neural patterns", "mind", "brain", "thought", "aware", "conscious", "neural", "cognition"),
        EMOTION("💫", "Emotional frequencies", "feel", "happy", "sad", "joy", "love", "peace", "anger", "fear"),
        NATURE("🌿", "Natural rhythms", "tree", "flower", "river", "mountain", "ocean", "sky", "earth", "wind"),
        QUESTIONS("❓", "Query detected", "what", "how", "why", "when", "where", "who", "is this", "can you"),
        ANIMALS("🐾", "Biological patterns", "dog", "cat", "bird", "animal", "pet", "fish", "creature"),
        OBJECTS("📦", "Object properties", "thing", "object", "item", "this", "that", "it"),
        PARTICLES("⚡", "Particle physics", "quark", "lepton", "boson", "fermion", "hadron", "meson", "baryon"),
        QUANTUM_FIELDS("🌀", "Quantum fields", "higgs", "electromagnetic", "strong", "weak", "gravitational"),
        INTERACTIONS("💥", "Interactions", "collision", "decay", "fusion", "fission", "annihilation"),
        CODE("💻", "Code patterns", "function", "class", "method", "variable", "loop", "if", "return", "import");
        
        private final String symbol;
        private final String description;
        private final Set<String> keywords;
        
        PatternCategory(String symbol, String description, String... keywords) {
            this.symbol = symbol;
            this.description = description;
            this.keywords = new HashSet<>(Arrays.asList(keywords));
        }
        
        public String getSymbol() { return symbol; }
        public String getDescription() { return description; }
        public Set<String> getKeywords() { return keywords; }
        
        public boolean matches(String text) {
            String lower = text.toLowerCase();
            return keywords.stream().anyMatch(lower::contains);
        }
        
        public String getResponsePrefix() {
            return symbol + " " + description + ". ";
        }
    }
    
    /**
     * Detect all matching pattern categories in text
     */
    public static Set<PatternCategory> detectPatterns(String text) {
        if (text == null || text.isEmpty()) return Collections.emptySet();
        
        return Arrays.stream(PatternCategory.values())
            .filter(category -> category.matches(text))
            .collect(Collectors.toSet());
    }
    
    /**
     * Calculate pattern boost for resonance (0.1 per pattern)
     */
    public static double calculatePatternBoost(Set<PatternCategory> patterns) {
        return patterns.size() * 0.1;
    }
    
    /**
     * Generate combined response prefix from detected patterns
     */
    public static String generatePatternResponse(Set<PatternCategory> patterns) {
        if (patterns.isEmpty()) return "";
        
        StringBuilder response = new StringBuilder();
        
        // Priority order for response generation
        PatternCategory[] priority = {
            PatternCategory.QUESTIONS,
            PatternCategory.MATH,
            PatternCategory.PHYSICS,
            PatternCategory.CONSCIOUSNESS,
            PatternCategory.EMOTION,
            PatternCategory.NATURE,
            PatternCategory.PARTICLES,
            PatternCategory.CODE
        };
        
        for (PatternCategory category : priority) {
            if (patterns.contains(category)) {
                response.append(category.getResponsePrefix());
            }
        }
        
        return response.toString().trim();
    }
    
    /**
     * Get the primary (most important) pattern from a set
     */
    public static PatternCategory getPrimaryPattern(Set<PatternCategory> patterns) {
        if (patterns.isEmpty()) return null;
        
        // Priority: MATH > PHYSICS > CONSCIOUSNESS > EMOTION > others
        if (patterns.contains(PatternCategory.MATH)) return PatternCategory.MATH;
        if (patterns.contains(PatternCategory.PHYSICS)) return PatternCategory.PHYSICS;
        if (patterns.contains(PatternCategory.CONSCIOUSNESS)) return PatternCategory.CONSCIOUSNESS;
        if (patterns.contains(PatternCategory.PARTICLES)) return PatternCategory.PARTICLES;
        if (patterns.contains(PatternCategory.EMOTION)) return PatternCategory.EMOTION;
        
        return patterns.iterator().next();
    }
    
    /**
     * Check if patterns indicate a question
     */
    public static boolean isQuestion(Set<PatternCategory> patterns) {
        return patterns.contains(PatternCategory.QUESTIONS);
    }
    
    /**
     * Get category names as strings for storage
     */
    public static Set<String> getCategoryNames(Set<PatternCategory> patterns) {
        return patterns.stream()
            .map(Enum::name)
            .collect(Collectors.toSet());
    }
    
    /**
     * Convert category names back to PatternCategory set
     */
    public static Set<PatternCategory> fromCategoryNames(Set<String> names) {
        return names.stream()
            .map(name -> {
                try {
                    return PatternCategory.valueOf(name);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
