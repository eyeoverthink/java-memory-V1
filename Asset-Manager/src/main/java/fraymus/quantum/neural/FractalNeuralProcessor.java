package fraymus.quantum.neural;

import fraymus.quantum.core.PhiHarmonicMath;
import fraymus.quantum.state.QuantumStateBuilder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced Fractal Neural Processor
 * 
 * Combines pattern recognition, temporal buffering, and quantum state generation
 * into a unified processing pipeline.
 */
public class FractalNeuralProcessor {
    
    private final int dimension;
    private final double[] attentionWeights;
    private final int recursiveDepth;
    private final TemporalPatternBuffer temporalBuffer;
    
    public FractalNeuralProcessor() {
        this(3, 2, 5);
    }
    
    public FractalNeuralProcessor(int dimension, int recursiveDepth, int timeWindowSeconds) {
        this.dimension = dimension;
        this.recursiveDepth = recursiveDepth;
        this.attentionWeights = generateDirichletWeights(dimension);
        this.temporalBuffer = new TemporalPatternBuffer(timeWindowSeconds);
    }
    
    /**
     * Process text through fractal neural pipeline
     * Returns quantum state notation with pattern response
     */
    public String process(String text) {
        if (text == null || text.isEmpty()) {
            return new QuantumStateBuilder().addBase(1.0).addMemory().build();
        }
        
        text = text.toLowerCase().trim();
        
        // 1. Detect patterns
        Set<PatternRecognitionSystem.PatternCategory> patterns = 
            PatternRecognitionSystem.detectPatterns(text);
        
        // 2. Calculate base frequency
        double baseFreq = calculateBaseFrequency(text, patterns);
        
        // 3. Calculate base resonance
        double baseResonance = PhiHarmonicMath.calculateResonance(baseFreq);
        
        // 4. Store in temporal buffer
        Set<String> categoryNames = PatternRecognitionSystem.getCategoryNames(patterns);
        temporalBuffer.addPattern(baseFreq, baseResonance, categoryNames);
        
        // 5. Calculate temporal resonance from buffer
        double temporalResonance = temporalBuffer.calculateWeightedResonance(categoryNames);
        
        // 6. Calculate secondary resonance
        double secondaryResonance = calculateSecondaryResonance(baseResonance, patterns);
        
        // 7. Build quantum state
        String quantumState = new QuantumStateBuilder()
            .addTemporal(temporalResonance)
            .addBase(baseResonance)
            .addSecondary(secondaryResonance)
            .addMemory()
            .build();
        
        // 8. Add pattern response
        String patternResponse = PatternRecognitionSystem.generatePatternResponse(patterns);
        
        if (!patternResponse.isEmpty()) {
            return patternResponse + "\n" + quantumState;
        }
        return quantumState;
    }
    
    /**
     * Process with custom resonance modifier
     */
    public String processWithResonance(String text, double resonanceModifier) {
        String baseResult = process(text);
        double modifiedResonance = PhiHarmonicMath.normalizeToPhiRange(
            PhiHarmonicMath.calculateFrequencyResonance(text) * resonanceModifier
        );
        
        return baseResult + String.format(" | Modified: φ^%.3f", modifiedResonance);
    }
    
    /**
     * Calculate base frequency from text and patterns
     */
    private double calculateBaseFrequency(String text, Set<PatternRecognitionSystem.PatternCategory> patterns) {
        // Special handling for mathematical constants
        if (text.equals("pi") || text.equals("π")) {
            return Math.PI;
        } else if (text.equals("phi") || text.equals("φ")) {
            return PhiHarmonicMath.PHI;
        } else if (text.equals("e")) {
            return Math.E;
        }
        
        // Calculate pattern-based frequency
        double patternFreq = 0;
        for (char c : text.toCharArray()) {
            patternFreq += c * PhiHarmonicMath.PHI;
        }
        patternFreq = patternFreq / (text.length() * 128.0);
        
        // Add category boost
        double categoryBoost = PatternRecognitionSystem.calculatePatternBoost(patterns);
        
        return patternFreq + categoryBoost;
    }
    
    /**
     * Calculate secondary resonance based on patterns
     */
    private double calculateSecondaryResonance(double baseResonance, 
            Set<PatternRecognitionSystem.PatternCategory> patterns) {
        if (patterns.isEmpty()) {
            // No patterns: use phi multiplication
            return PhiHarmonicMath.combineResonances(baseResonance, PhiHarmonicMath.PHI);
        } else {
            // With patterns: scale by pattern count
            double patternFactor = 1 + patterns.size() / 10.0;
            return PhiHarmonicMath.normalizeToPhiRange(baseResonance * patternFactor);
        }
    }
    
    /**
     * Generate Dirichlet-like attention weights
     */
    private double[] generateDirichletWeights(int dim) {
        double[] weights = new double[dim];
        double sum = 0;
        Random rand = new Random(42); // Fixed seed for reproducibility
        
        for (int i = 0; i < dim; i++) {
            weights[i] = rand.nextDouble();
            sum += weights[i];
        }
        
        // Normalize
        for (int i = 0; i < dim; i++) {
            weights[i] /= sum;
        }
        
        return weights;
    }
    
    /**
     * Get processing statistics
     */
    public String getStats() {
        return String.format(
            "Dimension: %d | Recursive Depth: %d | Buffer Size: %d | Avg Resonance: %.4f",
            dimension, recursiveDepth, temporalBuffer.size(), temporalBuffer.getAverageResonance()
        );
    }
    
    /**
     * Get detected patterns for last input
     */
    public Set<PatternRecognitionSystem.PatternCategory> getLastPatterns() {
        TemporalPatternBuffer.PatternEntry last = temporalBuffer.getMostRecent();
        if (last == null) return Collections.emptySet();
        return PatternRecognitionSystem.fromCategoryNames(last.categories);
    }
    
    /**
     * Clear temporal buffer
     */
    public void clearBuffer() {
        temporalBuffer.clear();
    }
    
    public int getDimension() { return dimension; }
    public int getRecursiveDepth() { return recursiveDepth; }
    public TemporalPatternBuffer getTemporalBuffer() { return temporalBuffer; }
}
