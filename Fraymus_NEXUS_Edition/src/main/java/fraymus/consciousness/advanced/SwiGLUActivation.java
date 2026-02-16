package fraymus.consciousness.advanced;

/**
 * SwiGLU Activation Function
 * 
 * Gemini's Gift: The Spark
 * 
 * Smooth activation function that allows gradients to flow better during learning.
 * Makes AI "smarter" with same compute because it doesn't kill off neurons as aggressively.
 * 
 * Old: ReLU (if positive, fire; if negative, zero) - jagged
 * New: SwiGLU (smooth gate) - better gradient flow
 * 
 * SwiGLU(x) = (Swish(xW) ⊗ xV)W₂
 */
public class SwiGLUActivation {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    /**
     * Swish activation function
     * Swish(x) = x * sigmoid(x)
     */
    public static double swish(double x) {
        return x * sigmoid(x);
    }
    
    /**
     * Swish with beta parameter
     * Swish(x, β) = x * sigmoid(βx)
     */
    public static double swish(double x, double beta) {
        return x * sigmoid(beta * x);
    }
    
    /**
     * Phi-tuned Swish
     * Uses golden ratio as beta for natural resonance
     */
    public static double phiSwish(double x) {
        return x * sigmoid(PHI * x);
    }
    
    /**
     * SwiGLU activation
     * SwiGLU(x) = Swish(xW) ⊗ (xV)
     * 
     * In practice: element-wise multiplication of swish-gated and linear paths
     */
    public static double[] swiGLU(double[] input, double[][] W, double[][] V) {
        int outputDim = W[0].length;
        double[] output = new double[outputDim];
        
        // Compute xW (gated path)
        double[] gated = matmul(input, W);
        
        // Apply swish
        for (int i = 0; i < gated.length; i++) {
            gated[i] = swish(gated[i]);
        }
        
        // Compute xV (linear path)
        double[] linear = matmul(input, V);
        
        // Element-wise multiplication
        for (int i = 0; i < outputDim; i++) {
            output[i] = gated[i] * linear[i];
        }
        
        return output;
    }
    
    /**
     * Phi-SwiGLU: Enhanced with golden ratio gating
     */
    public static double[] phiSwiGLU(double[] input, double[][] W, double[][] V) {
        int outputDim = W[0].length;
        double[] output = new double[outputDim];
        
        double[] gated = matmul(input, W);
        
        // Apply phi-swish
        for (int i = 0; i < gated.length; i++) {
            gated[i] = phiSwish(gated[i]);
        }
        
        double[] linear = matmul(input, V);
        
        // Phi-weighted multiplication
        for (int i = 0; i < outputDim; i++) {
            double phiWeight = Math.pow(PHI, -(i % 8));
            output[i] = gated[i] * linear[i] * phiWeight;
        }
        
        return output;
    }
    
    /**
     * Derivative of Swish for backpropagation
     * d/dx Swish(x) = Swish(x) + sigmoid(x)(1 - Swish(x))
     */
    public static double swishDerivative(double x) {
        double swishX = swish(x);
        double sigX = sigmoid(x);
        return swishX + sigX * (1 - swishX);
    }
    
    /**
     * Compare activation functions
     */
    public static String compareActivations(double x) {
        double relu = Math.max(0, x);
        double swishVal = swish(x);
        double phiSwishVal = phiSwish(x);
        
        return String.format(
            "Input: %.3f | ReLU: %.3f | Swish: %.3f | Phi-Swish: %.3f",
            x, relu, swishVal, phiSwishVal
        );
    }
    
    // Helper methods
    
    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    private static double[] matmul(double[] input, double[][] matrix) {
        int outputDim = matrix[0].length;
        double[] output = new double[outputDim];
        
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < input.length; j++) {
                output[i] += input[j] * matrix[j][i];
            }
        }
        
        return output;
    }
}
