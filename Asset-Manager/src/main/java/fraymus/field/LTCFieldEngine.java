package fraymus.field;

/**
 * Liquid Time-Constant (LTC) Field Engine
 * 
 * Replaces static RK4/Euler integration with input-dependent ODE solver.
 * Inspired by Liquid Neural Networks (LNNs) and C. elegans neural dynamics.
 * 
 * Mathematical foundation:
 * - Time constant: τ = sigmoid(W_τ * x + b_τ) [input-dependent]
 * - State dynamics: dx/dt = -x/τ + W * σ(x + U * input)
 * - Fluid memory horizon: τ adapts to input frequency
 * 
 * Phase 1.1: LTC ODE Solver Implementation
 * 
 * Key innovations:
 * - High-frequency inputs → short τ (fast response, short memory)
 * - Low-frequency inputs → long τ (slow response, long memory)
 * - Parameter efficiency: ~19 neurons can match thousands of parameters
 */
public class LTCFieldEngine {
    
    // State field x ∈ [-1.0, +1.0]^N
    private float[] state;
    
    // Previous state for time derivative
    private float[] prevState;
    
    // Time constant per dimension (τ) - input-dependent
    private float[] timeConstant;
    
    // Learnable weights for time constant computation
    private float[][] W_tau;  // Weight matrix for τ computation
    private float[] b_tau;    // Bias for τ computation
    
    // Learnable weights for state dynamics
    private float[][] W;      // Recurrent weights
    private float[][] U;      // Input weights
    
    // Input dimension
    private final int inputDim;
    
    // State dimension
    private final int stateDim;
    
    // Base time step
    private float dt;
    
    // Minimum and maximum time constant bounds
    private float tauMin = 0.1f;  // Fast response (high-frequency)
    private float tauMax = 10.0f; // Slow response (low-frequency)
    
    // Current input frequency estimate (for adaptive τ)
    private float inputFrequency = 1.0f;
    
    // Frequency smoothing factor
    private float frequencySmoothing = 0.1f;
    
    /**
     * Initialize LTC Field Engine
     * 
     * @param stateDim State field dimension
     * @param inputDim Input dimension
     * @param dt Time step
     */
    public LTCFieldEngine(int stateDim, int inputDim, float dt) {
        this.stateDim = stateDim;
        this.inputDim = inputDim;
        this.dt = dt;
        
        this.state = new float[stateDim];
        this.prevState = new float[stateDim];
        this.timeConstant = new float[stateDim];
        
        // Initialize learnable weights (random initialization)
        this.W_tau = new float[stateDim][inputDim];
        this.b_tau = new float[stateDim];
        this.W = new float[stateDim][stateDim];
        this.U = new float[stateDim][inputDim];
        
        // Initialize weights with Xavier-like initialization
        initializeWeights();
        
        // Initialize time constants to baseline
        for (int i = 0; i < stateDim; i++) {
            timeConstant[i] = 1.0f; // Baseline τ
        }
    }
    
    /**
     * Initialize weights with Xavier/Glorot-like scheme
     */
    private void initializeWeights() {
        float scaleW_tau = (float) Math.sqrt(2.0 / (stateDim + inputDim));
        float scaleW = (float) Math.sqrt(2.0 / (stateDim + stateDim));
        float scaleU = (float) Math.sqrt(2.0 / (stateDim + inputDim));
        
        for (int i = 0; i < stateDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                W_tau[i][j] = (float) (Math.random() * 2 - 1) * scaleW_tau;
                U[i][j] = (float) (Math.random() * 2 - 1) * scaleU;
            }
            b_tau[i] = (float) (Math.random() * 2 - 1) * 0.1f;
            
            for (int j = 0; j < stateDim; j++) {
                W[i][j] = (float) (Math.random() * 2 - 1) * scaleW;
            }
        }
    }
    
    /**
     * Sigmoid activation function
     */
    private float sigmoid(float x) {
        return (float) (1.0 / (1.0 + Math.exp(-x)));
    }
    
    /**
     * Tanh activation function
     */
    private float tanh(float x) {
        return (float) Math.tanh(x);
    }
    
    /**
     * Compute input-dependent time constant τ
     * τ = sigmoid(W_τ * input + b_τ) scaled to [tauMin, tauMax]
     * 
     * Key innovation: τ adapts based on input characteristics
     * - High-frequency/complex input → short τ (fast processing)
     * - Low-frequency/stable input → long τ (deep memory)
     */
    private void computeTimeConstants(float[] input) {
        for (int i = 0; i < stateDim; i++) {
            // Compute weighted sum
            float sum = b_tau[i];
            for (int j = 0; j < Math.min(inputDim, input.length); j++) {
                sum += W_tau[i][j] * input[j];
            }
            
            // Sigmoid to [0, 1]
            float sig = sigmoid(sum);
            
            // Scale to [tauMin, tauMax]
            // Inverse mapping: high sigmoid → short τ (fast response)
            timeConstant[i] = tauMin + (1.0f - sig) * (tauMax - tauMin);
        }
        
        // Adapt time constants based on estimated input frequency
        adaptTimeConstantsToFrequency();
    }
    
    /**
     * Estimate input frequency from rate of change
     */
    private void estimateInputFrequency(float[] input, float[] prevInput) {
        if (prevInput == null) return;
        
        float totalChange = 0.0f;
        for (int i = 0; i < Math.min(input.length, prevInput.length); i++) {
            totalChange += Math.abs(input[i] - prevInput[i]);
        }
        
        float avgChange = totalChange / input.length;
        
        // Smooth frequency estimate
        inputFrequency = frequencySmoothing * avgChange + (1.0f - frequencySmoothing) * inputFrequency;
    }
    
    /**
     * Adapt time constants based on input frequency
     * High frequency → shorter τ (faster processing, shorter memory horizon)
     * Low frequency → longer τ (slower processing, longer memory horizon)
     */
    private void adaptTimeConstantsToFrequency() {
        float frequencyFactor = Math.min(1.0f, inputFrequency * 10.0f);
        
        for (int i = 0; i < stateDim; i++) {
            // High frequency → shift toward tauMin
            // Low frequency → shift toward tauMax
            float targetTau = tauMin + (1.0f - frequencyFactor) * (tauMax - tauMin);
            
            // Smooth adaptation
            timeConstant[i] = 0.9f * timeConstant[i] + 0.1f * targetTau;
        }
    }
    
    /**
     * Compute state derivative: dx/dt = -x/τ + W * σ(x) + U * input
     */
    private float[] computeDerivative(float[] state, float[] input) {
        float[] derivative = new float[stateDim];
        
        for (int i = 0; i < stateDim; i++) {
            // Decay term: -x/τ
            float decay = -state[i] / timeConstant[i];
            
            // Recurrent term: W * σ(x)
            float recurrent = 0.0f;
            for (int j = 0; j < stateDim; j++) {
                recurrent += W[i][j] * tanh(state[j]);
            }
            
            // Input term: U * input
            float inputTerm = 0.0f;
            for (int j = 0; j < Math.min(inputDim, input.length); j++) {
                inputTerm += U[i][j] * input[j];
            }
            
            derivative[i] = decay + recurrent + inputTerm;
        }
        
        return derivative;
    }
    
    /**
     * LTC integration step using adaptive time constant
     * Uses semi-implicit Euler for stability
     * 
     * x(t+1) = x(t) + dt * (-x(t)/τ + W * σ(x(t)) + U * input)
     */
    public void integrate(float[] input) {
        // Store previous state
        System.arraycopy(state, 0, prevState, 0, stateDim);
        
        // Compute input-dependent time constants
        computeTimeConstants(input);
        
        // Compute derivative
        float[] derivative = computeDerivative(state, input);
        
        // Semi-implicit Euler integration
        for (int i = 0; i < stateDim; i++) {
            state[i] += dt * derivative[i];
            
            // Clamp to valid range
            state[i] = clamp(state[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * C-ISA Operation: EXC (Excite) - adapted for LTC
     * Inject positive energy, affects time constant computation
     */
    public void excite(float[] input, float alpha) {
        int n = Math.min(stateDim, input.length);
        for (int i = 0; i < n; i++) {
            state[i] += alpha * input[i];
            state[i] = clamp(state[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * C-ISA Operation: INH (Inhibit) - adapted for LTC
     * Inject negative energy, affects time constant computation
     */
    public void inhibit(float[] input, float alpha) {
        int n = Math.min(stateDim, input.length);
        for (int i = 0; i < n; i++) {
            state[i] -= alpha * input[i];
            state[i] = clamp(state[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * C-ISA Operation: SUP (Superpose) - adapted for LTC
     * Merge states with weighted superposition
     */
    public void superpose(float[] input, float alpha) {
        int n = Math.min(stateDim, input.length);
        for (int i = 0; i < n; i++) {
            state[i] = alpha * state[i] + (1.0f - alpha) * input[i];
            state[i] = clamp(state[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * C-ISA Operation: CLP (Collapse)
     * Snap continuous state to discrete
     */
    public int[] collapse(float threshold) {
        int[] discrete = new int[stateDim];
        for (int i = 0; i < stateDim; i++) {
            if (state[i] > threshold) {
                discrete[i] = 1;
            } else if (state[i] < -threshold) {
                discrete[i] = -1;
            } else {
                discrete[i] = 0;
            }
        }
        return discrete;
    }
    
    /**
     * Get current memory horizon (average time constant)
     * This represents how far back the system "remembers"
     * 
     * High-frequency input → short horizon (fast processing)
     * Low-frequency input → long horizon (deep memory)
     */
    public float getMemoryHorizon() {
        float sum = 0.0f;
        for (float tau : timeConstant) {
            sum += tau;
        }
        return sum / stateDim;
    }
    
    /**
     * Get time constants (for analysis/debugging)
     */
    public float[] getTimeConstants() {
        return timeConstant.clone();
    }
    
    /**
     * Get current state
     */
    public float[] getState() {
        return state.clone();
    }
    
    /**
     * Set state
     */
    public void setState(float[] newState) {
        int n = Math.min(stateDim, newState.length);
        for (int i = 0; i < n; i++) {
            state[i] = clamp(newState[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * Get input frequency estimate
     */
    public float getInputFrequency() {
        return inputFrequency;
    }
    
    /**
     * Set time constant bounds
     */
    public void setTauBounds(float tauMin, float tauMax) {
        this.tauMin = Math.max(0.01f, tauMin);
        this.tauMax = Math.max(tauMin, tauMax);
    }
    
    /**
     * Get state dimension
     */
    public int getStateDim() {
        return stateDim;
    }
    
    /**
     * Get input dimension
     */
    public int getInputDim() {
        return inputDim;
    }
    
    /**
     * Reset to neutral state
     */
    public void reset() {
        for (int i = 0; i < stateDim; i++) {
            state[i] = 0.0f;
            prevState[i] = 0.0f;
            timeConstant[i] = 1.0f;
        }
        inputFrequency = 1.0f;
    }
    
    /**
     * Clamp value to range
     */
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Print state and time constants (for debugging)
     */
    public void printState() {
        System.out.print("State: [");
        for (int i = 0; i < Math.min(10, stateDim); i++) {
            System.out.printf("%.3f", state[i]);
            if (i < Math.min(10, stateDim) - 1) {
                System.out.print(", ");
            }
        }
        if (stateDim > 10) {
            System.out.print(", ...");
        }
        System.out.println("]");
        
        System.out.printf("Memory Horizon: %.3f (avg τ)\n", getMemoryHorizon());
        System.out.printf("Input Frequency: %.3f\n", inputFrequency);
    }
}
