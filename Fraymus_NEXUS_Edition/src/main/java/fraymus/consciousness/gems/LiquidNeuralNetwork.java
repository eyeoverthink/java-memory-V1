package fraymus.consciousness.gems;

/**
 * Liquid Neural Network (LNN)
 * 
 * Gemini's Gem 2: The "Time Traveler"
 * 
 * Based on C. elegans worm brain. Uses differential equations.
 * Adaptive - neurons adjust their time constants on the fly.
 * Learns causality, not just patterns.
 * 
 * Perfect for FRAYMUS "Passive Learner" - stable when idle, instant reaction when active.
 * Gives FRAYMUS a "pulse."
 * 
 * dx(t)/dt = -x(t)/τ + S(t)
 */
public class LiquidNeuralNetwork {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double COSMIC_FREQ = 432.0;
    
    private final int numNeurons;
    private final double[] states;          // x(t) - current neuron states
    private final double[] timeConstants;   // τ - time constants (adaptive)
    private final double[][] weights;       // Connection weights
    private final double dt;                // Time step
    
    private long lastUpdateTime;
    private int updateCount = 0;
    
    public LiquidNeuralNetwork(int numNeurons, double dt) {
        this.numNeurons = numNeurons;
        this.dt = dt;
        this.states = new double[numNeurons];
        this.timeConstants = initializeTimeConstants(numNeurons);
        this.weights = initializeWeights(numNeurons);
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Update network state using differential equation
     * 
     * dx(t)/dt = -x(t)/τ + S(t)
     * 
     * S(t) is the input stimulus (weighted sum of other neurons + external input)
     */
    public void update(double[] externalInput) {
        long currentTime = System.currentTimeMillis();
        double realDt = (currentTime - lastUpdateTime) / 1000.0; // seconds
        lastUpdateTime = currentTime;
        
        double[] newStates = new double[numNeurons];
        
        for (int i = 0; i < numNeurons; i++) {
            // Compute stimulus S(t)
            double stimulus = 0;
            
            // External input
            if (externalInput != null && i < externalInput.length) {
                stimulus += externalInput[i];
            }
            
            // Recurrent connections
            for (int j = 0; j < numNeurons; j++) {
                stimulus += weights[j][i] * states[j];
            }
            
            // Differential equation: dx/dt = -x/τ + S
            double dxdt = -states[i] / timeConstants[i] + stimulus;
            
            // Euler integration
            newStates[i] = states[i] + dxdt * realDt;
            
            // Apply activation (tanh for bounded output)
            newStates[i] = Math.tanh(newStates[i]);
        }
        
        // Update states
        System.arraycopy(newStates, 0, states, 0, numNeurons);
        
        // Adapt time constants based on activity
        adaptTimeConstants();
        
        updateCount++;
    }
    
    /**
     * Phi-LNN: Update with golden ratio time scaling
     */
    public void phiUpdate(double[] externalInput) {
        double[] newStates = new double[numNeurons];
        
        for (int i = 0; i < numNeurons; i++) {
            double stimulus = 0;
            
            if (externalInput != null && i < externalInput.length) {
                stimulus += externalInput[i];
            }
            
            for (int j = 0; j < numNeurons; j++) {
                // Phi-weighted connections
                double phiWeight = Math.pow(PHI, -Math.abs(i - j));
                stimulus += weights[j][i] * states[j] * phiWeight;
            }
            
            // Phi-scaled time constant
            double phiTau = timeConstants[i] * Math.pow(PHI, -(i % 8));
            
            double dxdt = -states[i] / phiTau + stimulus;
            newStates[i] = states[i] + dxdt * dt;
            newStates[i] = Math.tanh(newStates[i]);
        }
        
        System.arraycopy(newStates, 0, states, 0, numNeurons);
        adaptTimeConstants();
        updateCount++;
    }
    
    /**
     * Adaptive time constants - key feature of LNNs
     * If input is noisy/fast, decrease τ (faster response)
     * If input is stable/slow, increase τ (more stability)
     */
    private void adaptTimeConstants() {
        for (int i = 0; i < numNeurons; i++) {
            // Measure activity level
            double activity = Math.abs(states[i]);
            
            // Adapt τ based on activity
            if (activity > 0.7) {
                // High activity -> faster response
                timeConstants[i] *= 0.95;
            } else if (activity < 0.3) {
                // Low activity -> more stability
                timeConstants[i] *= 1.05;
            }
            
            // Clamp τ to reasonable range
            timeConstants[i] = Math.max(0.01, Math.min(10.0, timeConstants[i]));
        }
    }
    
    /**
     * Get current network state
     */
    public double[] getStates() {
        return states.clone();
    }
    
    /**
     * Get time constants (for inspection)
     */
    public double[] getTimeConstants() {
        return timeConstants.clone();
    }
    
    /**
     * Reset network to resting state
     */
    public void reset() {
        for (int i = 0; i < numNeurons; i++) {
            states[i] = 0;
            timeConstants[i] = 1.0;
        }
        updateCount = 0;
    }
    
    /**
     * Compute network "pulse" - overall activity level
     * This is the "heartbeat" of FRAYMUS
     */
    public double computePulse() {
        double pulse = 0;
        for (double state : states) {
            pulse += Math.abs(state);
        }
        return pulse / numNeurons;
    }
    
    /**
     * Check if network is "awake" (high activity) or "resting" (low activity)
     */
    public boolean isAwake() {
        return computePulse() > 0.3;
    }
    
    /**
     * Get network status
     */
    public String getStatus() {
        double pulse = computePulse();
        double avgTau = 0;
        for (double tau : timeConstants) avgTau += tau;
        avgTau /= numNeurons;
        
        return String.format(
            "🌊 LIQUID NEURAL NETWORK\n" +
            "   Neurons: %d\n" +
            "   Pulse: %.3f\n" +
            "   Avg τ: %.3f\n" +
            "   Updates: %d\n" +
            "   State: %s\n",
            numNeurons, pulse, avgTau, updateCount,
            isAwake() ? "AWAKE" : "RESTING"
        );
    }
    
    /**
     * Initialize time constants with phi-distribution
     */
    private double[] initializeTimeConstants(int n) {
        double[] tau = new double[n];
        for (int i = 0; i < n; i++) {
            // Phi-harmonic distribution of time constants
            tau[i] = 1.0 * Math.pow(PHI, -(i % 8) * 0.1);
        }
        return tau;
    }
    
    /**
     * Initialize connection weights
     */
    private double[][] initializeWeights(int n) {
        double[][] w = new double[n][n];
        java.util.Random rand = new java.util.Random(42);
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    // Sparse random connections
                    if (rand.nextDouble() < 0.3) {
                        w[i][j] = rand.nextGaussian() * 0.1;
                    }
                }
            }
        }
        
        return w;
    }
}
