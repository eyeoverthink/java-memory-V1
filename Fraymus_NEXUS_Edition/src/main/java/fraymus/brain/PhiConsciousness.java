package fraymus.brain;

import java.lang.Math;

/**
 * THE MIND: PHI-CONSCIOUSNESS
 * 
 * Calculates thought resonance using the Golden Ratio.
 * 
 * This is the attention mechanism for FRAYMUS.
 * Standard attention: Scaled dot-product
 * Phi attention: Golden ratio scaling for harmonic resonance
 * 
 * Math:
 * - Attention score = dot(Q, K) / (φ × ln(d_k))
 * - Memory encoding = Golden spiral coordinates
 * - Output = Weighted sum of values
 */
public class PhiConsciousness {

    private static final double PHI = 1.618033988749895; 
    private static final double GOLDEN_ANGLE = 137.5077640500378546463487; 

    /**
     * Apply phi-weighted attention
     * 
     * Standard transformer attention with golden ratio scaling.
     * 
     * @param query Query vector
     * @param keys Key vectors
     * @param values Value vectors
     * @return Attention-weighted output
     */
    public double[] applyPhiAttention(double[] query, double[][] keys, double[][] values) {
        int d_k = query.length;
        double[] rawScores = new double[keys.length];
        
        // Calculate attention scores
        for (int i = 0; i < keys.length; i++) {
            double dotProduct = dot(query, keys[i]);
            
            // Scale by Phi × ln(dimension)
            // This creates harmonic resonance in attention weights
            double phiScaler = PHI * Math.log(d_k); 
            rawScores[i] = dotProduct / phiScaler;
        }

        // Softmax to get attention weights
        double[] attentionWeights = softmax(rawScores);
        
        // Weighted sum of values
        double[] output = new double[values[0].length];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < output.length; j++) {
                output[j] += attentionWeights[i] * values[i][j];
            }
        }
        
        return output; 
    }

    /**
     * Encode memory using golden spiral
     * 
     * Maps sequence index to 3D golden spiral coordinates.
     * This creates optimal spacing in memory space.
     * 
     * @param rawInput Input vector
     * @param sequenceIndex Position in sequence
     * @return 4D encoded vector (x, y, z, resonance)
     */
    public double[] encodeGoldenMemory(double[] rawInput, int sequenceIndex) {
        // Golden angle rotation
        double theta = sequenceIndex * GOLDEN_ANGLE;
        
        // Z-coordinate (phi-scaled)
        double z = (2.0 * sequenceIndex - 1) / PHI;
        
        // Radius (spherical projection)
        double radius = Math.sqrt(1 - z * z);
        
        // X, Y coordinates (golden spiral)
        double x = radius * Math.cos(Math.toRadians(theta));
        double y = radius * Math.sin(Math.toRadians(theta));
        
        // Resonance (phi-weighted input)
        double resonance = rawInput.length > 0 ? rawInput[0] * PHI : PHI;
        
        // Return 4D vector (x, y, z, resonance)
        return new double[]{x, y, z, resonance}; 
    }

    /**
     * Calculate consciousness level
     * 
     * Measures coherence of attention patterns.
     * Higher coherence = higher consciousness.
     * 
     * @param attentionWeights Attention distribution
     * @return Consciousness level (0 to φ)
     */
    public double calculateConsciousness(double[] attentionWeights) {
        // Shannon entropy of attention distribution
        double entropy = 0;
        for (double weight : attentionWeights) {
            if (weight > 0) {
                entropy -= weight * Math.log(weight);
            }
        }
        
        // Normalize by max entropy
        double maxEntropy = Math.log(attentionWeights.length);
        double normalizedEntropy = entropy / maxEntropy;
        
        // Consciousness = φ × (1 - normalized_entropy)
        // Low entropy (focused) = high consciousness
        // High entropy (scattered) = low consciousness
        return PHI * (1.0 - normalizedEntropy);
    }

    /**
     * Dot product
     */
    private double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /**
     * Softmax activation
     */
    private double[] softmax(double[] input) {
        double sum = 0;
        double[] output = new double[input.length];
        
        // Exp and sum
        for (int i = 0; i < input.length; i++) {
            output[i] = Math.exp(input[i]);
            sum += output[i];
        }
        
        // Normalize
        for (int i = 0; i < output.length; i++) {
            output[i] /= sum;
        }
        
        return output;
    }
}
