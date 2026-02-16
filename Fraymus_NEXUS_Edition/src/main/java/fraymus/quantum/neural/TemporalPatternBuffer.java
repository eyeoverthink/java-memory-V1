package fraymus.quantum.neural;

import fraymus.quantum.core.PhiHarmonicMath;
import fraymus.quantum.core.PhiResonanceCalculator;

import java.util.*;

/**
 * Temporal pattern buffer with exponential decay weighting
 * Stores recent patterns and calculates weighted resonance
 */
public class TemporalPatternBuffer {
    
    /**
     * Single pattern entry in the buffer
     */
    public static class PatternEntry {
        public final double[] pattern;
        public final long timestamp;
        public final double resonance;
        public final Set<String> categories;
        
        public PatternEntry(double[] pattern, double resonance, Set<String> categories) {
            this.pattern = pattern;
            this.timestamp = System.currentTimeMillis();
            this.resonance = resonance;
            this.categories = new HashSet<>(categories);
        }
        
        /**
         * Calculate weight for this entry based on age and pattern matching
         * @param currentCategories Current pattern categories
         * @return Weight value (exponential decay * pattern match bonus)
         */
        public double calculateWeight(Set<String> currentCategories) {
            double age = (System.currentTimeMillis() - timestamp) / 1000.0; // seconds
            
            // Calculate pattern match count
            Set<String> intersection = new HashSet<>(categories);
            intersection.retainAll(currentCategories);
            int patternMatch = intersection.size();
            
            return PhiHarmonicMath.weightedDecay(age, patternMatch);
        }
    }
    
    private final List<PatternEntry> buffer = new ArrayList<>();
    private final int timeWindowSeconds;
    
    /**
     * Create temporal pattern buffer
     * @param timeWindowSeconds Time window for keeping patterns (typically 5 seconds)
     */
    public TemporalPatternBuffer(int timeWindowSeconds) {
        this.timeWindowSeconds = timeWindowSeconds;
    }
    
    /**
     * Add a pattern to the buffer
     * @param pattern Pattern array
     * @param resonance Resonance value
     * @param categories Pattern categories
     */
    public void addPattern(double[] pattern, double resonance, Set<String> categories) {
        buffer.add(new PatternEntry(pattern, resonance, categories));
        cleanOldPatterns();
    }
    
    /**
     * Remove patterns older than the time window
     */
    private void cleanOldPatterns() {
        long cutoff = System.currentTimeMillis() - (timeWindowSeconds * 1000L);
        buffer.removeIf(entry -> entry.timestamp < cutoff);
    }
    
    /**
     * Calculate weighted resonance based on current categories
     * @param currentCategories Current pattern categories
     * @return Weighted temporal resonance
     */
    public double calculateWeightedResonance(Set<String> currentCategories) {
        if (buffer.isEmpty()) {
            return 1.0;
        }
        
        double[] weights = new double[buffer.size()];
        double[] resonances = new double[buffer.size()];
        
        for (int i = 0; i < buffer.size(); i++) {
            PatternEntry entry = buffer.get(i);
            weights[i] = entry.calculateWeight(currentCategories);
            resonances[i] = entry.resonance;
        }
        
        return PhiResonanceCalculator.calculateTemporalResonance(resonances, weights);
    }
    
    /**
     * Get buffer size
     * @return Number of patterns in buffer
     */
    public int size() {
        return buffer.size();
    }
    
    /**
     * Clear all patterns
     */
    public void clear() {
        buffer.clear();
    }
    
    /**
     * Get all pattern entries
     * @return Unmodifiable list of entries
     */
    public List<PatternEntry> getEntries() {
        return Collections.unmodifiableList(buffer);
    }
    
    /**
     * Get average resonance of all patterns
     * @return Average resonance
     */
    public double getAverageResonance() {
        if (buffer.isEmpty()) {
            return 1.0;
        }
        
        double sum = 0;
        for (PatternEntry entry : buffer) {
            sum += entry.resonance;
        }
        
        return sum / buffer.size();
    }
}
