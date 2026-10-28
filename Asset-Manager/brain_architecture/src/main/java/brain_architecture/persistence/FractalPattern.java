package brain_architecture.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * FractalPattern - Represents a self-similar pattern in knowledge.
 * Used for hierarchical organization and compression.
 */
public class FractalPattern {
    private UUID patternId;
    private String name;
    private List<Double> signature; // Pattern signature as vector
    private double scale;
    private double complexity;
    private List<UUID> subPatternIds;
    
    /**
     * Constructor.
     */
    public FractalPattern(String name) {
        this.patternId = UUID.randomUUID();
        this.name = name;
        this.signature = new ArrayList<>();
        this.scale = 1.0;
        this.complexity = 0.0;
        this.subPatternIds = new ArrayList<>();
    }
    
    /**
     * Constructor with signature.
     */
    public FractalPattern(String name, List<Double> signature) {
        this(name);
        this.signature = new ArrayList<>(signature);
        this.complexity = calculateComplexity();
    }
    
    /**
     * Calculate pattern complexity based on signature.
     */
    private double calculateComplexity() {
        if (signature.isEmpty()) {
            return 0.0;
        }
        
        double variance = 0.0;
        double mean = signature.stream().mapToDouble(d -> d).average().orElse(0.0);
        
        for (Double value : signature) {
            variance += Math.pow(value - mean, 2);
        }
        variance /= signature.size();
        
        return Math.sqrt(variance);
    }
    
    /**
     * Add a sub-pattern.
     */
    public void addSubPattern(UUID subPatternId) {
        if (!subPatternIds.contains(subPatternId)) {
            subPatternIds.add(subPatternId);
        }
    }
    
    /**
     * Remove a sub-pattern.
     */
    public void removeSubPattern(UUID subPatternId) {
        subPatternIds.remove(subPatternId);
    }
    
    /**
     * Compare similarity with another pattern.
     * Returns similarity score between 0.0 and 1.0.
     */
    public double similarity(FractalPattern other) {
        if (signature.size() != other.signature.size()) {
            return 0.0;
        }
        
        if (signature.isEmpty()) {
            return 1.0; // Both empty patterns are similar
        }
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < signature.size(); i++) {
            dotProduct += signature.get(i) * other.signature.get(i);
            normA += Math.pow(signature.get(i), 2);
            normB += Math.pow(other.signature.get(i), 2);
        }
        
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    /**
     * Scale the pattern by a factor.
     */
    public void scale(double factor) {
        this.scale *= factor;
        for (int i = 0; i < signature.size(); i++) {
            signature.set(i, signature.get(i) * factor);
        }
    }
    
    /**
     * Get pattern dimension.
     */
    public int getDimension() {
        return signature.size();
    }
    
    // Getters and setters
    public UUID getPatternId() { return patternId; }
    public String getName() { return name; }
    public List<Double> getSignature() { return new ArrayList<>(signature); }
    public double getScale() { return scale; }
    public double getComplexity() { return complexity; }
    public List<UUID> getSubPatternIds() { return new ArrayList<>(subPatternIds); }
    
    public void setName(String name) { this.name = name; }
    public void setSignature(List<Double> signature) { 
        this.signature = new ArrayList<>(signature);
        this.complexity = calculateComplexity();
    }
    public void setScale(double scale) { this.scale = scale; }
    
    @Override
    public String toString() {
        return "FractalPattern{" +
                "name='" + name + '\'' +
                ", dimension=" + signature.size() +
                ", scale=" + scale +
                ", complexity=" + complexity +
                ", subPatterns=" + subPatternIds.size() +
                '}';
    }
}
