package brain_architecture.connectome;

import brain_architecture.synapses.Neurotransmitter;
import java.util.UUID;

/**
 * ConnectomeSynapse - Synaptic connection between neurons in the connectome.
 * 
 * Extends the synaptic plasticity model from Phase 1 with connectome-specific
 * features for graph neural network operations.
 */
public class ConnectomeSynapse {
    
    private final UUID synapseId;
    private final UUID sourceNeuronId;
    private final UUID targetNeuronId;
    private double weight;
    private final Neurotransmitter neurotransmitter;
    private final long createdAt;
    
    private long lastActivated;
    private int activationCount;
    private double plasticityRate;
    
    // STDP parameters
    private double A_plus;
    private double A_minus;
    private double tau_plus;
    private double tau_minus;
    
    public ConnectomeSynapse(UUID sourceNeuronId, UUID targetNeuronId, 
                           double weight, Neurotransmitter neurotransmitter) {
        this.synapseId = UUID.randomUUID();
        this.sourceNeuronId = sourceNeuronId;
        this.targetNeuronId = targetNeuronId;
        this.weight = weight;
        this.neurotransmitter = neurotransmitter;
        this.createdAt = System.currentTimeMillis();
        this.lastActivated = 0;
        this.activationCount = 0;
        this.plasticityRate = 0.1;
        
        // Default STDP parameters
        this.A_plus = 0.1;
        this.A_minus = 0.12;
        this.tau_plus = 20.0;
        this.tau_minus = 20.0;
    }
    
    public UUID getSynapseId() {
        return synapseId;
    }
    
    public UUID getSourceNeuronId() {
        return sourceNeuronId;
    }
    
    public UUID getTargetNeuronId() {
        return targetNeuronId;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = Math.max(0.0, Math.min(2.0, weight));
    }
    
    public Neurotransmitter getNeurotransmitter() {
        return neurotransmitter;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public long getLastActivated() {
        return lastActivated;
    }
    
    public int getActivationCount() {
        return activationCount;
    }
    
    public double getPlasticityRate() {
        return plasticityRate;
    }
    
    public void setPlasticityRate(double plasticityRate) {
        this.plasticityRate = Math.max(0.0, Math.min(1.0, plasticityRate));
    }
    
    public void activate() {
        this.activationCount++;
        this.lastActivated = System.currentTimeMillis();
    }
    
    /**
     * Hebbian learning: strengthen when pre- and post-synaptic neurons fire together.
     * Δw = η * pre * post
     */
    public void strengthen(double preActivity, double postActivity) {
        double deltaWeight = plasticityRate * preActivity * postActivity;
        this.weight = Math.min(2.0, this.weight + deltaWeight);
    }
    
    /**
     * Weaken synapse (forgetting).
     */
    public void weaken(double amount) {
        this.weight = Math.max(0.0, this.weight - amount);
    }
    
    /**
     * Apply STDP (Spike-Timing-Dependent Plasticity).
     * 
     * If pre-synaptic spike before post-synaptic: strengthen (causal)
     * If post-synaptic spike before pre-synaptic: weaken (anti-causal)
     * 
     * Δw = A+ * exp(-Δt/τ+) if Δt > 0
     * Δw = -A- * exp(Δt/τ-) if Δt < 0
     */
    public void applySTDP(double preSpikeTime, double postSpikeTime) {
        double deltaTime = preSpikeTime - postSpikeTime;
        
        if (deltaTime > 0) {
            // Pre before post: strengthen
            double deltaWeight = A_plus * Math.exp(-deltaTime / tau_plus);
            this.weight = Math.min(2.0, this.weight + deltaWeight);
        } else {
            // Post before pre: weaken
            double deltaWeight = -A_minus * Math.exp(deltaTime / tau_minus);
            this.weight = Math.max(0.0, this.weight + deltaWeight);
        }
    }
    
    /**
     * Homeostatic plasticity: normalize to maintain stability.
     */
    public void normalize(double targetWeight, double normalizationRate) {
        double difference = targetWeight - this.weight;
        this.weight += difference * normalizationRate;
        this.weight = Math.max(0.0, Math.min(2.0, this.weight));
    }
    
    /**
     * Apply decay to weight over time.
     */
    public void applyDecay(double decayRate) {
        this.weight = Math.max(0.0, this.weight * (1.0 - decayRate));
    }
    
    /**
     * Get effective weight considering neurotransmitter valence.
     */
    public double getEffectiveWeight() {
        if (neurotransmitter.isInhibitory()) {
            return -weight;
        }
        return weight;
    }
    
    @Override
    public String toString() {
        return "ConnectomeSynapse{" +
            "source=" + sourceNeuronId.toString().substring(0, 8) +
            ", target=" + targetNeuronId.toString().substring(0, 8) +
            ", weight=" + String.format("%.3f", weight) +
            ", neurotransmitter=" + neurotransmitter +
            ", activations=" + activationCount +
            '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectomeSynapse that = (ConnectomeSynapse) o;
        return synapseId.equals(that.synapseId);
    }
    
    @Override
    public int hashCode() {
        return synapseId.hashCode();
    }
}
