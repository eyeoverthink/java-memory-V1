package brain_architecture.synapses;

import java.util.UUID;

/**
 * Synapse - Represents a synaptic connection between two concepts.
 * 
 * Implements Hebbian learning: "fire together, wire together"
 * Supports STDP (Spike-Timing-Dependent Plasticity)
 * Supports homeostatic plasticity
 */
public class Synapse {
    
    private final UUID synapseId;
    private final UUID sourceConceptId;
    private final UUID targetConceptId;
    private double weight; // Synaptic strength
    private final Neurotransmitter neurotransmitter;
    private final long createdAt;
    
    private long lastActivated;
    private int activationCount;
    private double plasticityRate; // Learning rate
    
    public Synapse(UUID sourceConceptId, UUID targetConceptId, double weight, Neurotransmitter neurotransmitter) {
        this.synapseId = UUID.randomUUID();
        this.sourceConceptId = sourceConceptId;
        this.targetConceptId = targetConceptId;
        this.weight = weight;
        this.neurotransmitter = neurotransmitter;
        this.createdAt = System.currentTimeMillis();
        this.lastActivated = 0;
        this.activationCount = 0;
        this.plasticityRate = 0.1; // Default learning rate
    }
    
    public UUID getSynapseId() {
        return synapseId;
    }
    
    public UUID getSourceConceptId() {
        return sourceConceptId;
    }
    
    public UUID getTargetConceptId() {
        return targetConceptId;
    }
    
    public double getWeight() {
        return weight;
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
        this.plasticityRate = plasticityRate;
    }
    
    /**
     * Record activation of this synapse.
     */
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
        double A_plus = 0.1;
        double A_minus = 0.12;
        double tau_plus = 20.0;
        double tau_minus = 20.0;
        
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
     * Prevents runaway excitation or depression.
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
    
    @Override
    public String toString() {
        return "Synapse{" +
            "source=" + sourceConceptId +
            ", target=" + targetConceptId +
            ", weight=" + weight +
            ", neurotransmitter=" + neurotransmitter +
            ", activationCount=" + activationCount +
            '}';
    }
}
