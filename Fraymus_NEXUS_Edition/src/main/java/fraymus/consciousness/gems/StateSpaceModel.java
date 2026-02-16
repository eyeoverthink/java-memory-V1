package fraymus.consciousness.gems;

/**
 * State Space Model (SSM) / Mamba
 * 
 * Gemini's Gem 3: The "Infinite Tape"
 * 
 * Compresses entire history into fixed-size state h(t).
 * O(N) linear speed - allows infinite memory without slowing down.
 * 
 * Perfect for FRAYMUS "Concept Memory" - remember all 31,154 logs without deletion.
 * 
 * h'(t) = Ah(t) + Bx(t)
 * y(t) = Ch(t)
 */
public class StateSpaceModel {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final int stateDim;      // Dimension of hidden state h(t)
    private final int inputDim;      // Dimension of input x(t)
    private final int outputDim;     // Dimension of output y(t)
    
    private final double[][] A;      // State transition matrix
    private final double[][] B;      // Input projection matrix
    private final double[][] C;      // Output projection matrix
    
    private double[] hiddenState;    // Current h(t)
    private long sequenceLength = 0;
    
    public StateSpaceModel(int stateDim, int inputDim, int outputDim) {
        this.stateDim = stateDim;
        this.inputDim = inputDim;
        this.outputDim = outputDim;
        
        this.A = initializeA(stateDim);
        this.B = initializeB(stateDim, inputDim);
        this.C = initializeC(outputDim, stateDim);
        
        this.hiddenState = new double[stateDim];
    }
    
    /**
     * Process single input and update state
     * 
     * h'(t) = Ah(t) + Bx(t)
     * y(t) = Ch(t)
     * 
     * This is O(1) per token - constant time regardless of history length
     */
    public double[] process(double[] input) {
        // Update hidden state: h' = Ah + Bx
        double[] newState = new double[stateDim];
        
        // Ah term
        for (int i = 0; i < stateDim; i++) {
            for (int j = 0; j < stateDim; j++) {
                newState[i] += A[i][j] * hiddenState[j];
            }
        }
        
        // Bx term
        for (int i = 0; i < stateDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                if (j < input.length) {
                    newState[i] += B[i][j] * input[j];
                }
            }
        }
        
        hiddenState = newState;
        sequenceLength++;
        
        // Compute output: y = Ch
        double[] output = new double[outputDim];
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < stateDim; j++) {
                output[i] += C[i][j] * hiddenState[j];
            }
        }
        
        return output;
    }
    
    /**
     * Phi-SSM: Enhanced with golden ratio state dynamics
     */
    public double[] phiProcess(double[] input) {
        double[] newState = new double[stateDim];
        
        // Phi-modulated state transition
        for (int i = 0; i < stateDim; i++) {
            for (int j = 0; j < stateDim; j++) {
                double phiWeight = Math.pow(PHI, -Math.abs(i - j) * 0.1);
                newState[i] += A[i][j] * hiddenState[j] * phiWeight;
            }
        }
        
        // Phi-weighted input projection
        for (int i = 0; i < stateDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                if (j < input.length) {
                    double phiWeight = Math.pow(PHI, -(j % 8));
                    newState[i] += B[i][j] * input[j] * phiWeight;
                }
            }
        }
        
        hiddenState = newState;
        sequenceLength++;
        
        double[] output = new double[outputDim];
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < stateDim; j++) {
                output[i] += C[i][j] * hiddenState[j];
            }
        }
        
        return output;
    }
    
    /**
     * Process entire sequence efficiently
     * Still O(N) - linear in sequence length
     */
    public double[][] processSequence(double[][] sequence) {
        double[][] outputs = new double[sequence.length][];
        
        for (int t = 0; t < sequence.length; t++) {
            outputs[t] = process(sequence[t]);
        }
        
        return outputs;
    }
    
    /**
     * Get current hidden state (the compressed history)
     */
    public double[] getHiddenState() {
        return hiddenState.clone();
    }
    
    /**
     * Set hidden state (for loading saved state)
     */
    public void setHiddenState(double[] state) {
        if (state.length == stateDim) {
            this.hiddenState = state.clone();
        }
    }
    
    /**
     * Reset state to zero
     */
    public void reset() {
        hiddenState = new double[stateDim];
        sequenceLength = 0;
    }
    
    /**
     * Compute state norm (measure of how much history is compressed)
     */
    public double computeStateNorm() {
        double norm = 0;
        for (double h : hiddenState) {
            norm += h * h;
        }
        return Math.sqrt(norm);
    }
    
    /**
     * Estimate memory capacity
     * How many tokens worth of information is stored in the state?
     */
    public long estimateMemoryCapacity() {
        // State can theoretically store infinite history
        // But effective capacity depends on state norm and dynamics
        double stateNorm = computeStateNorm();
        
        // Heuristic: capacity scales with state dimension and norm
        return (long) (stateDim * stateNorm * PHI);
    }
    
    /**
     * Get status
     */
    public String getStatus() {
        return String.format(
            "🌊 STATE SPACE MODEL (Mamba)\n" +
            "   State Dim: %d\n" +
            "   Input Dim: %d\n" +
            "   Output Dim: %d\n" +
            "   Sequence Length: %d\n" +
            "   State Norm: %.3f\n" +
            "   Est. Capacity: %d tokens\n" +
            "   Complexity: O(N) - Linear\n",
            stateDim, inputDim, outputDim, sequenceLength,
            computeStateNorm(), estimateMemoryCapacity()
        );
    }
    
    /**
     * Compare with Transformer complexity
     */
    public String compareComplexity(int contextLength) {
        long ssmOps = contextLength * stateDim * stateDim;
        long transformerOps = (long) contextLength * contextLength * stateDim;
        
        return String.format(
            "Complexity Comparison (context=%d):\n" +
            "  SSM (Mamba): O(N) = %,d ops\n" +
            "  Transformer: O(N²) = %,d ops\n" +
            "  Speedup: %.1fx faster\n",
            contextLength, ssmOps, transformerOps,
            (double) transformerOps / ssmOps
        );
    }
    
    /**
     * Initialize A matrix with phi-harmonic eigenvalues
     * This ensures stable long-term memory
     */
    private double[][] initializeA(int n) {
        double[][] A = new double[n][n];
        java.util.Random rand = new java.util.Random(42);
        
        for (int i = 0; i < n; i++) {
            // Diagonal: decay rates based on phi
            A[i][i] = 0.9 * Math.pow(PHI, -(i % 8) * 0.1);
            
            // Off-diagonal: small random connections
            for (int j = 0; j < n; j++) {
                if (i != j && rand.nextDouble() < 0.1) {
                    A[i][j] = rand.nextGaussian() * 0.01;
                }
            }
        }
        
        return A;
    }
    
    /**
     * Initialize B matrix (input projection)
     */
    private double[][] initializeB(int stateDim, int inputDim) {
        double[][] B = new double[stateDim][inputDim];
        java.util.Random rand = new java.util.Random(43);
        
        for (int i = 0; i < stateDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                B[i][j] = rand.nextGaussian() * Math.sqrt(2.0 / inputDim);
            }
        }
        
        return B;
    }
    
    /**
     * Initialize C matrix (output projection)
     */
    private double[][] initializeC(int outputDim, int stateDim) {
        double[][] C = new double[outputDim][stateDim];
        java.util.Random rand = new java.util.Random(44);
        
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < stateDim; j++) {
                C[i][j] = rand.nextGaussian() * Math.sqrt(2.0 / stateDim);
            }
        }
        
        return C;
    }
}
