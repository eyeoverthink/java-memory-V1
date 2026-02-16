package fraymus.consciousness.advanced;

/**
 * Rotary Positional Embedding (RoPE)
 * 
 * Gemini's Gift: The GPS
 * 
 * Allows AI to understand "relative distance" perfectly.
 * Knows that "King" is related to "Queen" the same way "Man" is to "Woman,"
 * regardless of where they appear in text.
 * 
 * Critical for Golden Vector Memory - memory needs to rotate in Phi-space
 * to keep relationships valid over time.
 */
public class RotaryPositionalEmbedding {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final int dimensions;
    private final double[][] rotationMatrices;
    
    public RotaryPositionalEmbedding(int dimensions, int maxSequenceLength) {
        this.dimensions = dimensions;
        this.rotationMatrices = precomputeRotations(maxSequenceLength);
    }
    
    /**
     * Apply rotary positional embedding to a vector
     * 
     * f(x, pos) = (cos θ  -sin θ) (x₁)
     *             (sin θ   cos θ) (x₂)
     */
    public double[] applyRoPE(double[] vector, int position) {
        if (position >= rotationMatrices.length) {
            position = rotationMatrices.length - 1;
        }
        
        double[] rotated = new double[vector.length];
        
        // Apply rotation in pairs of dimensions
        for (int i = 0; i < vector.length - 1; i += 2) {
            double theta = rotationMatrices[position][i / 2];
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            
            rotated[i] = vector[i] * cos - vector[i + 1] * sin;
            rotated[i + 1] = vector[i] * sin + vector[i + 1] * cos;
        }
        
        // Handle odd dimension
        if (vector.length % 2 == 1) {
            rotated[vector.length - 1] = vector[vector.length - 1];
        }
        
        return rotated;
    }
    
    /**
     * Apply RoPE with Phi-harmonic scaling
     * Enhances the standard RoPE with golden ratio frequency modulation
     */
    public double[] applyPhiRoPE(double[] vector, int position) {
        double[] rotated = new double[vector.length];
        
        for (int i = 0; i < vector.length - 1; i += 2) {
            // Phi-modulated frequency
            double baseFreq = 1.0 / Math.pow(10000.0, (double) i / dimensions);
            double phiFreq = baseFreq * Math.pow(PHI, i % 8); // Phi-harmonic
            
            double theta = position * phiFreq;
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            
            rotated[i] = vector[i] * cos - vector[i + 1] * sin;
            rotated[i + 1] = vector[i] * sin + vector[i + 1] * cos;
        }
        
        if (vector.length % 2 == 1) {
            rotated[vector.length - 1] = vector[vector.length - 1];
        }
        
        return rotated;
    }
    
    /**
     * Compute relative position encoding between two positions
     * This is the key insight of RoPE - relative distances are preserved
     */
    public double computeRelativeDistance(int pos1, int pos2) {
        int relativePos = Math.abs(pos2 - pos1);
        
        // Phi-scaled distance metric
        return Math.log(1 + relativePos) / Math.log(PHI);
    }
    
    /**
     * Precompute rotation angles for efficiency
     */
    private double[][] precomputeRotations(int maxLength) {
        double[][] rotations = new double[maxLength][dimensions / 2];
        
        for (int pos = 0; pos < maxLength; pos++) {
            for (int i = 0; i < dimensions / 2; i++) {
                double freq = 1.0 / Math.pow(10000.0, (double) (2 * i) / dimensions);
                rotations[pos][i] = pos * freq;
            }
        }
        
        return rotations;
    }
    
    /**
     * Check if two positions are "relatively close" based on phi-threshold
     */
    public boolean areRelativelyClose(int pos1, int pos2, double threshold) {
        double distance = computeRelativeDistance(pos1, pos2);
        return distance < threshold * PHI;
    }
}
