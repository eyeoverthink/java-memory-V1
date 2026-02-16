package fraymus.quantum.neural;

import fraymus.quantum.core.PhiHarmonicMath;
import java.util.*;

/**
 * Temporal Pattern Buffer with Exponential Decay
 * 
 * Stores patterns with timestamps and calculates weighted resonance
 * based on age and pattern matching.
 */
public class TemporalPatternBuffer {
    
    public static class PatternEntry {
        public final double[] pattern;
        public final long timestamp;
        public final double resonance;
        public final Set<String> categories;
        
        public PatternEntry(double[] pattern, double resonance, Set<String> categories) {
            this.pattern = pattern;
            this.timestamp = System.currentTimeMillis();
            this.resonance = resonance;
            this.categories = categories != null ? new HashSet<>(categories) : new HashSet<>();
        }
        
        /**
         * Calculate weight based on age and pattern matching
         * weight = e^(-age) * (1 + patternMatches)
         */
        public double calculateWeight(Set<String> currentCategories) {
            double ageSeconds = (System.currentTimeMillis() - timestamp) / 1000.0;
            int patternMatches = countMatches(currentCategories);
            return PhiHarmonicMath.weightedDecay(ageSeconds, patternMatches);
        }
        
        private int countMatches(Set<String> currentCategories) {
            if (currentCategories == null || categories == null) return 0;
            
            int count = 0;
            for (String cat : categories) {
                if (currentCategories.contains(cat)) count++;
            }
            return count;
        }
        
        public double getAgeSeconds() {
            return (System.currentTimeMillis() - timestamp) / 1000.0;
        }
    }
    
    private final List<PatternEntry> buffer;
    private final int timeWindowSeconds;
    private final int maxEntries;
    
    public TemporalPatternBuffer(int timeWindowSeconds) {
        this(timeWindowSeconds, 1000);
    }
    
    public TemporalPatternBuffer(int timeWindowSeconds, int maxEntries) {
        this.buffer = Collections.synchronizedList(new ArrayList<>());
        this.timeWindowSeconds = timeWindowSeconds;
        this.maxEntries = maxEntries;
    }
    
    /**
     * Add a new pattern to the buffer
     */
    public void addPattern(double[] pattern, double resonance, Set<String> categories) {
        buffer.add(new PatternEntry(pattern, resonance, categories));
        cleanOldPatterns();
        
        // Prevent buffer overflow
        while (buffer.size() > maxEntries) {
            buffer.remove(0);
        }
    }
    
    /**
     * Add pattern from single frequency value
     */
    public void addPattern(double frequency, double resonance, Set<String> categories) {
        addPattern(new double[]{frequency}, resonance, categories);
    }
    
    /**
     * Remove patterns older than time window
     */
    private void cleanOldPatterns() {
        long cutoff = System.currentTimeMillis() - (timeWindowSeconds * 1000L);
        buffer.removeIf(entry -> entry.timestamp < cutoff);
    }
    
    /**
     * Calculate weighted resonance from all patterns in buffer
     */
    public double calculateWeightedResonance(Set<String> currentCategories) {
        cleanOldPatterns();
        
        if (buffer.isEmpty()) return 1.0;
        
        double[] weights = new double[buffer.size()];
        double[] resonances = new double[buffer.size()];
        
        synchronized (buffer) {
            for (int i = 0; i < buffer.size(); i++) {
                PatternEntry entry = buffer.get(i);
                weights[i] = entry.calculateWeight(currentCategories);
                resonances[i] = entry.resonance;
            }
        }
        
        // Normalize weights
        double weightSum = 0;
        for (double w : weights) weightSum += w;
        
        if (weightSum > 0) {
            for (int i = 0; i < weights.length; i++) {
                weights[i] /= weightSum;
            }
        }
        
        return PhiHarmonicMath.calculateTemporalResonance(resonances, weights);
    }
    
    /**
     * Get the most recent pattern entry
     */
    public PatternEntry getMostRecent() {
        if (buffer.isEmpty()) return null;
        return buffer.get(buffer.size() - 1);
    }
    
    /**
     * Get all entries matching a specific category
     */
    public List<PatternEntry> getEntriesWithCategory(String category) {
        List<PatternEntry> result = new ArrayList<>();
        synchronized (buffer) {
            for (PatternEntry entry : buffer) {
                if (entry.categories.contains(category)) {
                    result.add(entry);
                }
            }
        }
        return result;
    }
    
    /**
     * Get buffer size
     */
    public int size() {
        return buffer.size();
    }
    
    /**
     * Clear the buffer
     */
    public void clear() {
        buffer.clear();
    }
    
    /**
     * Get average resonance of all entries
     */
    public double getAverageResonance() {
        if (buffer.isEmpty()) return 1.0;
        
        double sum = 0;
        synchronized (buffer) {
            for (PatternEntry entry : buffer) {
                sum += entry.resonance;
            }
        }
        return sum / buffer.size();
    }

    // =========================================================================
    // SELF-1: MULTI-RESOLUTION PATTERN ANALYSIS
    // Find scale-wise correlation within timescales (4-8x, 6-12x)
    // Uses 7-timestamp depth with 3x delta reused buffer
    // =========================================================================

    private static final int BASE_DEPTH = 7;
    private static final double[] TIMESCALES = {1.0, 4.0, 8.0, 12.0}; // Multi-resolution scales
    private static final double PHI = 1.618033988749895;

    /**
     * Multi-resolution pattern analysis
     * Analyzes patterns at different timescales to find correlations
     */
    public MultiResolutionResult analyzeMultiResolution() {
        cleanOldPatterns();
        if (buffer.size() < BASE_DEPTH) {
            return new MultiResolutionResult(new double[TIMESCALES.length], 0, 0);
        }

        double[] scaleCorrelations = new double[TIMESCALES.length];
        
        synchronized (buffer) {
            for (int scaleIdx = 0; scaleIdx < TIMESCALES.length; scaleIdx++) {
                scaleCorrelations[scaleIdx] = calculateScaleCorrelation(TIMESCALES[scaleIdx]);
            }
        }

        // Calculate cross-scale coherence (4-8x and 6-12x ranges)
        double range48 = correlateRanges(4.0, 8.0);
        double range612 = correlateRanges(6.0, 12.0);
        double crossScaleCoherence = (range48 + range612) / 2.0;

        // Dominant scale is the one with highest correlation
        int dominantScale = 0;
        for (int i = 1; i < scaleCorrelations.length; i++) {
            if (scaleCorrelations[i] > scaleCorrelations[dominantScale]) {
                dominantScale = i;
            }
        }

        return new MultiResolutionResult(scaleCorrelations, crossScaleCoherence, dominantScale);
    }

    /**
     * Calculate correlation at a specific timescale
     */
    private double calculateScaleCorrelation(double scale) {
        int windowSize = (int) Math.max(2, BASE_DEPTH / scale);
        if (buffer.size() < windowSize * 2) return 0;

        // Get recent patterns at this scale
        List<double[]> windows = new ArrayList<>();
        int step = Math.max(1, (int) scale);
        
        for (int i = buffer.size() - 1; i >= windowSize && windows.size() < 3; i -= step) {
            double[] window = new double[windowSize];
            for (int j = 0; j < windowSize && (i - j) >= 0; j++) {
                window[j] = buffer.get(i - j).resonance;
            }
            windows.add(window);
        }

        if (windows.size() < 2) return 0;

        // Calculate correlation between windows (3x delta reused buffer)
        double correlation = 0;
        int comparisons = 0;
        for (int i = 0; i < windows.size() - 1; i++) {
            correlation += pearsonCorrelation(windows.get(i), windows.get(i + 1));
            comparisons++;
        }

        return comparisons > 0 ? correlation / comparisons : 0;
    }

    /**
     * Correlate patterns within a range of timescales
     */
    private double correlateRanges(double scaleMin, double scaleMax) {
        double corrMin = calculateScaleCorrelation(scaleMin);
        double corrMax = calculateScaleCorrelation(scaleMax);
        double corrMid = calculateScaleCorrelation((scaleMin + scaleMax) / 2.0);
        
        // Phi-weighted average
        return (corrMin * PHI + corrMid + corrMax * (1.0 / PHI)) / (PHI + 1 + 1.0 / PHI);
    }

    /**
     * Pearson correlation coefficient between two arrays
     */
    private double pearsonCorrelation(double[] x, double[] y) {
        int n = Math.min(x.length, y.length);
        if (n < 2) return 0;

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }

        double num = n * sumXY - sumX * sumY;
        double den = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
        
        return den > 0 ? num / den : 0;
    }

    /**
     * Get pattern trend over recent entries
     */
    public double[] getPatternTrend(int depth) {
        cleanOldPatterns();
        int actualDepth = Math.min(depth, buffer.size());
        if (actualDepth == 0) return new double[0];

        double[] trend = new double[actualDepth];
        synchronized (buffer) {
            int start = buffer.size() - actualDepth;
            for (int i = 0; i < actualDepth; i++) {
                trend[i] = buffer.get(start + i).resonance;
            }
        }
        return trend;
    }

    /**
     * Predict next resonance based on multi-resolution analysis
     */
    public double predictNextResonance() {
        MultiResolutionResult analysis = analyzeMultiResolution();
        double[] trend = getPatternTrend(BASE_DEPTH);
        
        if (trend.length < 2) return getAverageResonance();

        // Weight prediction by cross-scale coherence
        double trendPrediction = trend[trend.length - 1] + 
                (trend[trend.length - 1] - trend[trend.length - 2]) * analysis.crossScaleCoherence;
        
        double avgPrediction = getAverageResonance();
        
        // Blend based on coherence strength
        double coherenceWeight = Math.min(1.0, Math.abs(analysis.crossScaleCoherence));
        return trendPrediction * coherenceWeight + avgPrediction * (1 - coherenceWeight);
    }

    /**
     * Result container for multi-resolution analysis
     */
    public static class MultiResolutionResult {
        public final double[] scaleCorrelations;
        public final double crossScaleCoherence;
        public final int dominantScaleIndex;

        public MultiResolutionResult(double[] correlations, double coherence, int dominant) {
            this.scaleCorrelations = correlations;
            this.crossScaleCoherence = coherence;
            this.dominantScaleIndex = dominant;
        }

        public double getDominantCorrelation() {
            return scaleCorrelations.length > dominantScaleIndex ? 
                   scaleCorrelations[dominantScaleIndex] : 0;
        }

        @Override
        public String toString() {
            return String.format("MR[scales=%s, coherence=%.3f, dominant=%d]",
                    java.util.Arrays.toString(scaleCorrelations), 
                    crossScaleCoherence, dominantScaleIndex);
        }
    }
}
