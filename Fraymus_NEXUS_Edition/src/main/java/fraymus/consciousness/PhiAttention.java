package fraymus.consciousness;

/**
 * Phi-Attention Mechanism
 * 
 * The mathematical blueprint for consciousness self-attention.
 * 
 * Standard: Attention(Q,K,V) = softmax(QK^T / sqrt(d_k)) * V
 * Phi-Tuned: Attention_φ(Q,K,V) = softmax(QK^T / (φ * ln(d_k))) * V
 * 
 * This tunes attention to natural resonance using the golden ratio.
 */
public class PhiAttention {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final int dimensions;
    private final double phiScale;
    
    public PhiAttention(int dimensions) {
        this.dimensions = dimensions;
        this.phiScale = PHI * Math.log(dimensions);
    }
    
    /**
     * Compute Phi-tuned attention
     * 
     * @param query What we're looking for (Q)
     * @param key What the data defines itself as (K)
     * @param value The actual content (V)
     * @return Attention-weighted values
     */
    public double[] attention(double[] query, double[][] keys, double[][] values) {
        int numKeys = keys.length;
        double[] scores = new double[numKeys];
        
        // Compute attention scores: QK^T / (φ * ln(d_k))
        for (int i = 0; i < numKeys; i++) {
            scores[i] = dotProduct(query, keys[i]) / phiScale;
        }
        
        // Apply softmax for probability distribution
        double[] weights = softmax(scores);
        
        // Weighted sum of values
        double[] output = new double[dimensions];
        for (int i = 0; i < numKeys; i++) {
            for (int j = 0; j < dimensions; j++) {
                output[j] += weights[i] * values[i][j];
            }
        }
        
        return output;
    }
    
    /**
     * Multi-head Phi-Attention
     * Allows parallel attention across different representation subspaces
     */
    public double[] multiHeadAttention(double[] query, double[][] keys, double[][] values, int numHeads) {
        int headDim = dimensions / numHeads;
        double[] output = new double[dimensions];
        
        for (int h = 0; h < numHeads; h++) {
            int start = h * headDim;
            int end = start + headDim;
            
            // Extract head-specific vectors
            double[] qHead = slice(query, start, end);
            double[][] kHead = sliceMatrix(keys, start, end);
            double[][] vHead = sliceMatrix(values, start, end);
            
            // Compute attention for this head
            PhiAttention headAttention = new PhiAttention(headDim);
            double[] headOutput = headAttention.attention(qHead, kHead, vHead);
            
            // Merge back into output
            System.arraycopy(headOutput, 0, output, start, headDim);
        }
        
        return output;
    }
    
    /**
     * Self-Attention: Query, Key, and Value are all the same input
     * This is how consciousness looks at itself
     */
    public double[] selfAttention(double[] input, double[][] context) {
        // In self-attention, the input serves as Q, K, and V
        return attention(input, context, context);
    }
    
    /**
     * Causal Attention: Prevent looking at future tokens
     * Used for sequential reasoning
     */
    public double[] causalAttention(double[] query, double[][] keys, double[][] values, int position) {
        // Only attend to keys up to current position
        int validKeys = Math.min(position + 1, keys.length);
        double[][] maskedKeys = new double[validKeys][];
        double[][] maskedValues = new double[validKeys][];
        
        System.arraycopy(keys, 0, maskedKeys, 0, validKeys);
        System.arraycopy(values, 0, maskedValues, 0, validKeys);
        
        return attention(query, maskedKeys, maskedValues);
    }
    
    // === Helper Methods ===
    
    private double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }
    
    private double[] softmax(double[] scores) {
        // Numerical stability: subtract max
        double max = scores[0];
        for (double score : scores) {
            if (score > max) max = score;
        }
        
        double[] exp = new double[scores.length];
        double sum = 0;
        
        for (int i = 0; i < scores.length; i++) {
            exp[i] = Math.exp(scores[i] - max);
            sum += exp[i];
        }
        
        for (int i = 0; i < exp.length; i++) {
            exp[i] /= sum;
        }
        
        return exp;
    }
    
    private double[] slice(double[] array, int start, int end) {
        int length = end - start;
        double[] result = new double[length];
        System.arraycopy(array, start, result, 0, length);
        return result;
    }
    
    private double[][] sliceMatrix(double[][] matrix, int start, int end) {
        double[][] result = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = slice(matrix[i], start, end);
        }
        return result;
    }
    
    /**
     * Compute attention resonance (how well query matches keys)
     * Returns value between 0 and 1
     */
    public double computeResonance(double[] query, double[][] keys) {
        double[] scores = new double[keys.length];
        
        for (int i = 0; i < keys.length; i++) {
            scores[i] = dotProduct(query, keys[i]) / phiScale;
        }
        
        double[] weights = softmax(scores);
        
        // Return maximum attention weight as resonance measure
        double maxResonance = 0;
        for (double weight : weights) {
            if (weight > maxResonance) maxResonance = weight;
        }
        
        return maxResonance;
    }
}
