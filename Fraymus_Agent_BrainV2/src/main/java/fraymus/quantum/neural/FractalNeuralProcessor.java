package fraymus.quantum.neural;

import fraymus.PhiConstants;
import fraymus.quantum.core.PhiResonanceCalculator;
import fraymus.quantum.state.QuantumStateBuilder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fractal neural processor - integrates pattern recognition, temporal buffering,
 * and quantum state generation
 */
public class FractalNeuralProcessor {
    
    private final int dimension;
    private final int recursiveDepth;
    private final TemporalPatternBuffer temporalBuffer;
    
    /**
     * Create fractal neural processor
     * @param dimension Processing dimension (typically 3-11)
     * @param recursiveDepth Recursive processing depth (typically 2)
     * @param timeWindowSeconds Temporal buffer window (typically 5)
     */
    public FractalNeuralProcessor(int dimension, int recursiveDepth, int timeWindowSeconds) {
        this.dimension = dimension;
        this.recursiveDepth = recursiveDepth;
        this.temporalBuffer = new TemporalPatternBuffer(timeWindowSeconds);
    }
    
    /**
     * Create with default parameters
     */
    public FractalNeuralProcessor() {
        this(3, 2, 5);
    }
    
    /**
     * Process text input through fractal neural system
     * @param text Input text
     * @return Quantum state string with pattern response
     */
    public String process(String text) {
        // 1. Detect patterns
        Set<PatternRecognitionSystem.PatternCategory> patterns = 
            PatternRecognitionSystem.detectPatterns(text);
        
        // 2. Calculate base frequency
        double baseFreq = calculateBaseFrequency(text, patterns);
        
        // 3. Calculate base resonance
        double baseResonance = PhiResonanceCalculator.calculateBaseResonance(baseFreq);
        
        // 4. Store in temporal buffer
        Set<String> categoryNames = patterns.stream()
            .map(Enum::name)
            .collect(Collectors.toSet());
        
        temporalBuffer.addPattern(
            new double[]{baseFreq}, 
            baseResonance, 
            categoryNames
        );
        
        // 5. Calculate temporal resonance
        double temporalResonance = temporalBuffer.calculateWeightedResonance(categoryNames);
        
        // 6. Calculate secondary resonance
        double secondaryResonance = patterns.isEmpty() 
            ? PhiResonanceCalculator.calculateSecondaryResonance(baseResonance)
            : PhiResonanceCalculator.calculateSecondaryResonanceWithPatterns(baseResonance, patterns.size());
        
        // 7. Build quantum state
        String quantumState = QuantumStateBuilder.createStandard(
            temporalResonance,
            baseResonance,
            secondaryResonance
        );
        
        // 8. Add pattern response
        String patternResponse = PatternRecognitionSystem.generatePatternResponse(patterns);
        
        return patternResponse.isEmpty() ? quantumState : patternResponse + "\n" + quantumState;
    }
    
    /**
     * Calculate base frequency from text and patterns
     * @param text Input text
     * @param patterns Detected patterns
     * @return Base frequency
     */
    private double calculateBaseFrequency(String text, Set<PatternRecognitionSystem.PatternCategory> patterns) {
        // Special handling for mathematical constants
        String lower = text.toLowerCase().trim();
        
        if (lower.equals("pi") || lower.equals("π")) {
            return Math.PI;
        } else if (lower.equals("phi") || lower.equals("φ")) {
            return PhiConstants.PHI;
        } else if (lower.equals("e")) {
            return Math.E;
        }
        
        // Calculate from text characters
        int charSum = 0;
        for (char c : text.toCharArray()) {
            charSum += c;
        }
        
        double patternFreq = text.isEmpty() ? 0 : charSum / (double)(text.length() * 128);
        double categoryBoost = PatternRecognitionSystem.calculatePatternBoost(patterns);
        
        return patternFreq + categoryBoost;
    }
    
    /**
     * Process with simple output (just quantum state, no pattern response)
     * @param text Input text
     * @return Quantum state string
     */
    public String processSimple(String text) {
        Set<PatternRecognitionSystem.PatternCategory> patterns = 
            PatternRecognitionSystem.detectPatterns(text);
        
        double baseFreq = calculateBaseFrequency(text, patterns);
        double baseResonance = PhiResonanceCalculator.calculateBaseResonance(baseFreq);
        
        return QuantumStateBuilder.createSimple(baseResonance);
    }
    
    /**
     * Get temporal buffer
     * @return The temporal pattern buffer
     */
    public TemporalPatternBuffer getTemporalBuffer() {
        return temporalBuffer;
    }
    
    /**
     * Get dimension
     * @return Processing dimension
     */
    public int getDimension() {
        return dimension;
    }
    
    /**
     * Get recursive depth
     * @return Recursive processing depth
     */
    public int getRecursiveDepth() {
        return recursiveDepth;
    }
}
