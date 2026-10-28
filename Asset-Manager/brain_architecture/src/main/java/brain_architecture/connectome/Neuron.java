package brain_architecture.connectome;

import java.util.UUID;

/**
 * Neuron - Represents a single neuron in the connectome.
 * 
 * Implements leaky integrate-and-fire model with intrinsic descriptors
 * for trainable parameters not captured in the static connectome.
 */
public class Neuron {
    
    private final UUID neuronId;
    private final NeuronType type;
    private Neuropil neuropil;
    private IntrinsicDescriptor intrinsicDescriptor;
    
    // Membrane potential and activation
    private double membranePotential;
    private double threshold;
    private double lastSpikeTime;
    private int spikeCount;
    
    // Leaky integrate-and-fire parameters
    private double leakRate;
    private double resetPotential;
    
    public Neuron(NeuronType type) {
        this.neuronId = UUID.randomUUID();
        this.type = type;
        this.intrinsicDescriptor = new IntrinsicDescriptor();
        this.membranePotential = 0.0;
        this.threshold = 1.0;
        this.lastSpikeTime = -1.0;
        this.spikeCount = 0;
        this.leakRate = 0.1;
        this.resetPotential = 0.0;
    }
    
    public Neuron(NeuronType type, Neuropil neuropil) {
        this(type);
        this.neuropil = neuropil;
    }
    
    public UUID getNeuronId() {
        return neuronId;
    }
    
    public NeuronType getType() {
        return type;
    }
    
    public Neuropil getNeuropil() {
        return neuropil;
    }
    
    public void setNeuropil(Neuropil neuropil) {
        this.neuropil = neuropil;
    }
    
    public IntrinsicDescriptor getIntrinsicDescriptor() {
        return intrinsicDescriptor;
    }
    
    public void setIntrinsicDescriptor(IntrinsicDescriptor intrinsicDescriptor) {
        this.intrinsicDescriptor = intrinsicDescriptor;
    }
    
    public double getMembranePotential() {
        return membranePotential;
    }
    
    public void setMembranePotential(double membranePotential) {
        this.membranePotential = membranePotential;
    }
    
    public double getThreshold() {
        return threshold;
    }
    
    public void setThreshold(double threshold) {
        this.threshold = Math.max(0.1, threshold);
    }
    
    public double getLastSpikeTime() {
        return lastSpikeTime;
    }
    
    public int getSpikeCount() {
        return spikeCount;
    }
    
    public double getLeakRate() {
        return leakRate;
    }
    
    public void setLeakRate(double leakRate) {
        this.leakRate = Math.max(0.0, Math.min(1.0, leakRate));
    }
    
    public double getResetPotential() {
        return resetPotential;
    }
    
    public void setResetPotential(double resetPotential) {
        this.resetPotential = resetPotential;
    }
    
    /**
     * Activate neuron with input (leaky integrate-and-fire).
     * Returns true if neuron fires (spikes).
     */
    public boolean activate(double input, double currentTime) {
        // Apply intrinsic descriptor to input
        double processedInput = intrinsicDescriptor.processInput(input);
        
        // Integrate input into membrane potential
        membranePotential += processedInput;
        
        // Apply leak
        membranePotential *= (1.0 - leakRate);
        
        // Check threshold
        if (membranePotential >= threshold) {
            // Spike!
            spike(currentTime);
            return true;
        }
        
        return false;
    }
    
    /**
     * Spike (fire) the neuron.
     */
    private void spike(double currentTime) {
        this.lastSpikeTime = currentTime;
        this.spikeCount++;
        
        // Apply adaptation
        intrinsicDescriptor.applyAdaptation(1.0);
        
        // Reset membrane potential
        membranePotential = resetPotential;
    }
    
    /**
     * Reset neuron to resting state.
     */
    public void reset() {
        this.membranePotential = 0.0;
        this.lastSpikeTime = -1.0;
        this.spikeCount = 0;
        intrinsicDescriptor.resetAdaptation();
    }
    
    /**
     * Update intrinsic descriptor based on error signal.
     */
    public void updateIntrinsicDescriptor(double delta) {
        intrinsicDescriptor.update(delta);
    }
    
    /**
     * Get firing rate (spikes per unit time).
     */
    public double getFiringRate(double timeWindow) {
        if (timeWindow <= 0 || spikeCount == 0) {
            return 0.0;
        }
        return (double) spikeCount / timeWindow;
    }
    
    @Override
    public String toString() {
        return "Neuron{" +
            "id=" + neuronId.toString().substring(0, 8) +
            ", type=" + type +
            ", neuropil=" + (neuropil != null ? neuropil.getName() : "null") +
            ", potential=" + String.format("%.3f", membranePotential) +
            ", spikes=" + spikeCount +
            '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neuron neuron = (Neuron) o;
        return neuronId.equals(neuron.neuronId);
    }
    
    @Override
    public int hashCode() {
        return neuronId.hashCode();
    }
    
    /**
     * NeuronType - Classification of neurons by function.
     */
    public enum NeuronType {
        EXCITATORY("Excitatory neuron"),
        INHIBITORY("Inhibitory neuron"),
        MODULATORY("Modulatory neuron");
        
        private final String description;
        
        NeuronType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
