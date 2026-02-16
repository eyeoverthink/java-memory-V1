package fraymus.consciousness.advanced;

/**
 * Multi-Query Attention (MQA)
 * 
 * Gemini's Gift: Memory Optimization
 * 
 * Instead of giving every "Head" its own Key/Value memory, they share them.
 * Drastically reduces memory footprint.
 * Allows AI to remember huge contexts (like 31,154 logs) without crashing.
 * 
 * Standard Attention: Each head has separate K,V
 * MQA: All heads share K,V, only Q is separate
 */
public class MultiQueryAttention {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final int dimensions;
    private final int numHeads;
    private final int headDim;
    private final double phiScale;
    
    public MultiQueryAttention(int dimensions, int numHeads) {
        this.dimensions = dimensions;
        this.numHeads = numHeads;
        this.headDim = dimensions / numHeads;
        this.phiScale = PHI * Math.log(dimensions);
    }
    
    /**
     * Multi-Query Attention with Phi-scaling
     * 
     * @param queries Multiple query vectors (one per head)
     * @param sharedKeys Shared key matrix (all heads use same keys)
     * @param sharedValues Shared value matrix (all heads use same values)
     * @return Attention output
     */
    public double[] multiQueryAttention(double[][] queries, double[][] sharedKeys, double[][] sharedValues) {
        double[] output = new double[dimensions];
        
        // Each head computes attention with shared K,V
        for (int h = 0; h < numHeads; h++) {
            double[] query = queries[h];
            
            // Compute attention scores with phi-scaling
            double[] scores = new double[sharedKeys.length];
            for (int i = 0; i < sharedKeys.length; i++) {
                scores[i] = dotProduct(query, sharedKeys[i]) / phiScale;
            }
            
            // Softmax
            double[] weights = softmax(scores);
            
            // Weighted sum of shared values
            int start = h * headDim;
            for (int i = 0; i < sharedValues.length; i++) {
                for (int j = 0; j < headDim; j++) {
                    output[start + j] += weights[i] * sharedValues[i][j];
                }
            }
        }
        
        return output;
    }
    
    /**
     * Memory savings calculation
     * Standard: O(n_heads * d_model * 2) for K,V
     * MQA: O(d_model * 2) for shared K,V
     */
    public long calculateMemorySavings(int contextLength) {
        long standardMemory = (long) numHeads * dimensions * 2 * contextLength * 8; // bytes
        long mqaMemory = (long) dimensions * 2 * contextLength * 8; // bytes
        return standardMemory - mqaMemory;
    }
    
    /**
     * Estimate maximum context length before OOM
     */
    public int estimateMaxContext(long availableMemoryBytes) {
        long memoryPerToken = dimensions * 2 * 8; // K,V per token
        return (int) (availableMemoryBytes / memoryPerToken);
    }
    
    private double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }
    
    private double[] softmax(double[] scores) {
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
}
