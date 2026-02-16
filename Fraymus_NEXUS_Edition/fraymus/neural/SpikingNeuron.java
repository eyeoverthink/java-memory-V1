package fraymus.neural;

/**
 * SPIKING NEURAL NETWORK - INTEGRATE-AND-FIRE MODEL
 * The Math: du/dt = -(u - u_rest)/τ + R·I(t)
 *           if u > u_threshold: fire(), u = u_reset
 * 
 * The Secret: Real brains are event-driven. Neurons only fire when threshold crossed.
 * - Energy: 20 watts vs GPU's 400 watts
 * - Temporal Coding: Information is in the TIMING of spikes
 * - Sparse Activation: Most neurons are silent most of the time
 */
public class SpikingNeuron {

    private static final double PHI = 1.618033988749895;
    
    private double potential;           // Membrane potential u
    private double restPotential;       // u_rest
    private double threshold;           // u_threshold
    private double resetPotential;      // u_reset after spike
    private double tau;                 // Time constant τ
    private double resistance;          // Membrane resistance R
    
    private long lastSpikeTime;
    private int spikeCount;
    private double[] spikeHistory;
    private int historyIndex;

    public SpikingNeuron() {
        this(0.0, 1.0, 0.0, 0.1, 1.0);
    }

    public SpikingNeuron(double restPotential, double threshold, double resetPotential, 
                         double tau, double resistance) {
        this.restPotential = restPotential;
        this.threshold = threshold;
        this.resetPotential = resetPotential;
        this.tau = tau;
        this.resistance = resistance;
        this.potential = restPotential;
        this.spikeCount = 0;
        this.spikeHistory = new double[100];
        this.historyIndex = 0;
    }

    /**
     * Update neuron state with input current
     * @param input Input current I(t)
     * @param dt Time step
     * @return true if neuron fired (spike)
     */
    public boolean update(double input, double dt) {
        // Integrate: du/dt = -(u - u_rest)/τ + R·I(t)
        double du = (-(potential - restPotential) / tau + resistance * input) * dt;
        potential += du;
        
        // Fire?
        if (potential > threshold) {
            potential = resetPotential;
            lastSpikeTime = System.nanoTime();
            spikeCount++;
            
            // Record spike time in history
            spikeHistory[historyIndex] = System.currentTimeMillis();
            historyIndex = (historyIndex + 1) % spikeHistory.length;
            
            return true; // Spike!
        }
        
        return false;
    }

    /**
     * φ-enhanced update: threshold adapts based on golden ratio
     */
    public boolean updatePhiAdaptive(double input, double dt) {
        // Adaptive threshold based on recent activity
        double adaptiveThreshold = threshold * (1.0 + (spikeCount % 10) * 0.01 / PHI);
        
        double du = (-(potential - restPotential) / tau + resistance * input) * dt;
        potential += du;
        
        if (potential > adaptiveThreshold) {
            potential = resetPotential;
            lastSpikeTime = System.nanoTime();
            spikeCount++;
            spikeHistory[historyIndex] = System.currentTimeMillis();
            historyIndex = (historyIndex + 1) % spikeHistory.length;
            return true;
        }
        
        return false;
    }

    /**
     * Calculate firing rate (spikes per second)
     */
    public double getFiringRate(double windowMs) {
        long now = System.currentTimeMillis();
        int spikesInWindow = 0;
        
        for (double spikeTime : spikeHistory) {
            if (spikeTime > 0 && (now - spikeTime) < windowMs) {
                spikesInWindow++;
            }
        }
        
        return spikesInWindow / (windowMs / 1000.0);
    }

    public double getPotential() { return potential; }
    public int getSpikeCount() { return spikeCount; }
    public long getLastSpikeTime() { return lastSpikeTime; }

    public void reset() {
        potential = restPotential;
        spikeCount = 0;
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║    SPIKING NEURAL NETWORK TEST            ║");
        System.out.println("║    Integrate-and-Fire Model               ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        
        SpikingNeuron neuron = new SpikingNeuron();
        double dt = 0.01;
        int spikes = 0;
        
        System.out.println("║  Simulating 1000 timesteps...");
        System.out.println("║  Input: sin wave + noise");
        System.out.println("║");
        
        StringBuilder timeline = new StringBuilder();
        for (int t = 0; t < 1000; t++) {
            // Input: sinusoidal + noise
            double input = 0.5 * Math.sin(t * 0.1) + 0.3 + Math.random() * 0.2;
            
            if (neuron.update(input, dt)) {
                spikes++;
                timeline.append("█");
            } else if (t % 20 == 0) {
                timeline.append(neuron.getPotential() > 0.5 ? "▄" : "_");
            }
        }
        
        System.out.println("║  Spike timeline (sampled):");
        System.out.println("║  " + timeline.toString().substring(0, Math.min(50, timeline.length())));
        System.out.println("║");
        System.out.println("║  Total spikes: " + spikes);
        System.out.println("║  Final potential: " + String.format("%.4f", neuron.getPotential()));
        System.out.println("║  φ = " + PHI);
        System.out.println("╚═══════════════════════════════════════════╝");
    }
}
