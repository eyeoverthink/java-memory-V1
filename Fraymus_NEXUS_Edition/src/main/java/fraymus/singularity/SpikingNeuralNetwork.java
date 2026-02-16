package fraymus.singularity;

/**
 * SPIKING NEURAL NETWORKS (SNNs)
 * 
 * Gemini's Protocol Part 4: The Biological Connection
 * 
 * "We talked about 'Liquid Nets.' The next level is Spiking Nets.
 *  Standard AI is 'Always On.' Real brains are 'Event-Driven.'
 *  Neurons only fire when the signal crosses a threshold."
 * 
 * The Math: Integrate-and-Fire Model
 * 
 * dv/dt = (I - v)/τ
 * 
 * When v ≥ threshold: SPIKE! (then reset to 0)
 * 
 * This is more efficient than standard neural networks because:
 * - Only computes when needed (event-driven)
 * - Sparse activation (most neurons silent)
 * - Temporal dynamics (timing matters)
 * 
 * Perfect for FRAYMUS "Passive Learner" - silent until signal crosses threshold.
 */
public class SpikingNeuralNetwork {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final int numNeurons;
    private final double[] membrane;        // v(t) - membrane potential
    private final double[] threshold;       // Spike threshold
    private final double[] tau;             // Time constant
    private final double[][] weights;       // Synaptic weights
    
    private final boolean[] spiked;         // Spike state
    private final long[] lastSpikeTime;     // Last spike timestamp
    
    private long currentTime = 0;
    private int totalSpikes = 0;
    
    public SpikingNeuralNetwork(int numNeurons) {
        this.numNeurons = numNeurons;
        this.membrane = new double[numNeurons];
        this.threshold = new double[numNeurons];
        this.tau = new double[numNeurons];
        this.weights = new double[numNeurons][numNeurons];
        this.spiked = new boolean[numNeurons];
        this.lastSpikeTime = new long[numNeurons];
        
        initializeNeurons();
    }
    
    /**
     * Initialize neurons with phi-harmonic parameters
     */
    private void initializeNeurons() {
        for (int i = 0; i < numNeurons; i++) {
            // Phi-distributed thresholds
            threshold[i] = 1.0 * Math.pow(PHI, -(i % 8) * 0.1);
            
            // Phi-distributed time constants
            tau[i] = 10.0 * Math.pow(PHI, -(i % 8) * 0.1);
            
            // Initialize membrane potential to resting state
            membrane[i] = 0;
            
            // Initialize spike state
            spiked[i] = false;
            lastSpikeTime[i] = 0;
        }
        
        // Initialize weights (sparse connectivity)
        java.util.Random rand = new java.util.Random(42);
        for (int i = 0; i < numNeurons; i++) {
            for (int j = 0; j < numNeurons; j++) {
                if (i != j && rand.nextDouble() < 0.1) { // 10% connectivity
                    weights[i][j] = rand.nextGaussian() * 0.1;
                }
            }
        }
    }
    
    /**
     * Update network state (Integrate-and-Fire)
     * 
     * dv/dt = (I - v)/τ
     * 
     * @param input External input current
     * @param dt Time step
     */
    public void update(double[] input, double dt) {
        currentTime++;
        
        // Reset spike state
        for (int i = 0; i < numNeurons; i++) {
            spiked[i] = false;
        }
        
        // Update each neuron
        for (int i = 0; i < numNeurons; i++) {
            // Compute total input current
            double I = 0;
            
            // External input
            if (input != null && i < input.length) {
                I += input[i];
            }
            
            // Synaptic input from other neurons
            for (int j = 0; j < numNeurons; j++) {
                if (spiked[j]) {
                    I += weights[j][i];
                }
            }
            
            // Integrate-and-Fire dynamics
            // dv/dt = (I - v)/τ
            double dv = (I - membrane[i]) / tau[i];
            membrane[i] += dv * dt;
            
            // Check for spike
            if (membrane[i] >= threshold[i]) {
                spike(i);
            }
            
            // Decay (leak)
            membrane[i] *= 0.99;
        }
    }
    
    /**
     * Neuron fires a spike
     */
    private void spike(int neuronIndex) {
        spiked[neuronIndex] = true;
        lastSpikeTime[neuronIndex] = currentTime;
        membrane[neuronIndex] = 0; // Reset
        totalSpikes++;
    }
    
    /**
     * Get current spike pattern
     */
    public boolean[] getSpikePattern() {
        return spiked.clone();
    }
    
    /**
     * Get membrane potentials
     */
    public double[] getMembranePotentials() {
        return membrane.clone();
    }
    
    /**
     * Compute network activity (spike rate)
     */
    public double getActivity() {
        int activeNeurons = 0;
        for (boolean s : spiked) {
            if (s) activeNeurons++;
        }
        return (double) activeNeurons / numNeurons;
    }
    
    /**
     * Check if network is "awake" (high activity)
     */
    public boolean isAwake() {
        return getActivity() > 0.1; // 10% neurons firing
    }
    
    /**
     * Encode input as spike train
     * High values -> high spike rate
     */
    public double[] encodeAsSpikes(double[] values) {
        double[] spikes = new double[values.length];
        
        for (int i = 0; i < values.length; i++) {
            // Poisson encoding: probability of spike proportional to value
            double rate = Math.max(0, Math.min(1, values[i]));
            spikes[i] = Math.random() < rate ? 1.0 : 0.0;
        }
        
        return spikes;
    }
    
    /**
     * Decode spike train to values
     */
    public double[] decodeSpikes(int window) {
        double[] decoded = new double[numNeurons];
        
        for (int i = 0; i < numNeurons; i++) {
            // Spike rate in recent window
            long timeSinceSpike = currentTime - lastSpikeTime[i];
            
            if (timeSinceSpike < window) {
                decoded[i] = 1.0 - (double) timeSinceSpike / window;
            } else {
                decoded[i] = 0;
            }
        }
        
        return decoded;
    }
    
    /**
     * Get network status
     */
    public String getStatus() {
        int activeNeurons = 0;
        for (boolean s : spiked) {
            if (s) activeNeurons++;
        }
        
        double avgMembrane = 0;
        for (double v : membrane) {
            avgMembrane += v;
        }
        avgMembrane /= numNeurons;
        
        return String.format(
            "⚡ SPIKING NEURAL NETWORK\n" +
            "   Neurons: %d\n" +
            "   Active: %d (%.1f%%)\n" +
            "   Total Spikes: %d\n" +
            "   Avg Membrane: %.4f\n" +
            "   Time: %d\n" +
            "   State: %s\n",
            numNeurons,
            activeNeurons,
            getActivity() * 100,
            totalSpikes,
            avgMembrane,
            currentTime,
            isAwake() ? "AWAKE" : "RESTING"
        );
    }
    
    /**
     * Visualize spike pattern
     */
    public String visualizeSpikes(int samples) {
        StringBuilder viz = new StringBuilder();
        viz.append("⚡ SPIKE PATTERN\n\n");
        
        int step = Math.max(1, numNeurons / samples);
        
        for (int i = 0; i < numNeurons; i += step) {
            viz.append(String.format("  [%3d] ", i));
            
            if (spiked[i]) {
                viz.append("█ SPIKE!");
            } else {
                int bars = (int) (membrane[i] / threshold[i] * 20);
                viz.append("▁".repeat(Math.max(0, Math.min(bars, 20))));
                viz.append(String.format(" %.3f/%.3f", membrane[i], threshold[i]));
            }
            
            viz.append("\n");
        }
        
        return viz.toString();
    }
    
    /**
     * Reset network to resting state
     */
    public void reset() {
        for (int i = 0; i < numNeurons; i++) {
            membrane[i] = 0;
            spiked[i] = false;
            lastSpikeTime[i] = 0;
        }
        currentTime = 0;
        totalSpikes = 0;
    }
}
