package fraymus.neural;

/**
 * SwiGLU ACTIVATION FUNCTION
 * The Math: SwiGLU(x) = (Swish(xW) ⊗ xV) W₂
 * 
 * The Secret: Smooth gate. Gradients flow better during learning.
 * Makes AI "smarter" with same compute - doesn't kill neurons aggressively like ReLU.
 */
public class SwiGLU {

    private static final double PHI = 1.618033988749895;
    
    private double[][] W;   // First weight matrix
    private double[][] V;   // Gate weight matrix  
    private double[][] W2;  // Output weight matrix
    private int inputDim;
    private int hiddenDim;
    private int outputDim;

    public SwiGLU(int inputDim, int hiddenDim, int outputDim) {
        this.inputDim = inputDim;
        this.hiddenDim = hiddenDim;
        this.outputDim = outputDim;
        
        // Initialize weights with φ-scaled Xavier initialization
        this.W = initWeights(inputDim, hiddenDim);
        this.V = initWeights(inputDim, hiddenDim);
        this.W2 = initWeights(hiddenDim, outputDim);
    }

    private double[][] initWeights(int rows, int cols) {
        double[][] weights = new double[rows][cols];
        double scale = Math.sqrt(2.0 / (rows + cols)) * PHI;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                weights[i][j] = (Math.random() - 0.5) * scale;
            }
        }
        return weights;
    }

    /**
     * Swish activation: x * sigmoid(x)
     */
    public double swish(double x) {
        return x * sigmoid(x);
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * Forward pass: SwiGLU(x) = (Swish(xW) ⊗ xV) W₂
     */
    public double[] forward(double[] input) {
        // Step 1: xW and xV
        double[] xW = matmul(input, W);
        double[] xV = matmul(input, V);
        
        // Step 2: Swish(xW)
        double[] swishXW = new double[hiddenDim];
        for (int i = 0; i < hiddenDim; i++) {
            swishXW[i] = swish(xW[i]);
        }
        
        // Step 3: Element-wise multiply (Swish(xW) ⊗ xV)
        double[] gated = new double[hiddenDim];
        for (int i = 0; i < hiddenDim; i++) {
            gated[i] = swishXW[i] * xV[i];
        }
        
        // Step 4: Final projection gated @ W2
        return matmul(gated, W2);
    }

    private double[] matmul(double[] vec, double[][] mat) {
        double[] result = new double[mat[0].length];
        for (int j = 0; j < mat[0].length; j++) {
            double sum = 0;
            for (int i = 0; i < vec.length; i++) {
                sum += vec[i] * mat[i][j];
            }
            result[j] = sum;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║         SwiGLU ACTIVATION TEST            ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        
        SwiGLU layer = new SwiGLU(8, 16, 8);
        double[] input = {1.0, 0.5, -0.5, 0.0, 1.0, -1.0, 0.25, 0.75};
        double[] output = layer.forward(input);
        
        System.out.print("║  Input:  [");
        for (int i = 0; i < input.length; i++) {
            System.out.printf("%.2f", input[i]);
            if (i < input.length - 1) System.out.print(", ");
        }
        System.out.println("]");
        
        System.out.print("║  Output: [");
        for (int i = 0; i < output.length; i++) {
            System.out.printf("%.4f", output[i]);
            if (i < output.length - 1) System.out.print(", ");
        }
        System.out.println("]");
        
        System.out.println("║  φ = " + PHI);
        System.out.println("╚═══════════════════════════════════════════╝");
    }
}
