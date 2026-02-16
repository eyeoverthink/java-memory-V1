package fraymus.evolution;

/**
 * SYNAPSE: LIVING CONNECTION
 * 
 * A synapse is not permanent - it decays if unused (forgetting)
 * and grows if reinforced (memory formation).
 * 
 * This implements the forgetting curve and Hebbian learning.
 */
public class Synapse {
    
    // Target Priecled
    public Priecled target;
    
    // Connection strength (memory weight)
    public double weight;
    
    // Decay rate (forgetting speed)
    public double decayRate = 0.99;
    
    // Last activation time
    public long lastActivation;
    
    /**
     * Create synapse to target with initial weight
     */
    public Synapse(Priecled target, double weight) {
        this.target = target;
        this.weight = weight;
        this.lastActivation = System.currentTimeMillis();
    }
    
    /**
     * Decay synapse (forgetting curve)
     * 
     * Unused connections weaken over time
     */
    public void decay() {
        weight *= decayRate;
        
        // Faster decay if not recently activated
        long timeSinceActivation = System.currentTimeMillis() - lastActivation;
        if (timeSinceActivation > 10000) { // 10 seconds
            weight *= 0.95; // Extra decay
        }
    }
    
    /**
     * Strengthen synapse (Hebbian learning)
     * 
     * "Cells that fire together, wire together"
     */
    public void strengthen(double amount) {
        weight += amount;
        weight = Math.min(weight, 2.0); // Cap at 2.0
        lastActivation = System.currentTimeMillis();
    }
    
    /**
     * Activate synapse (signal transmission)
     */
    public void activate() {
        lastActivation = System.currentTimeMillis();
    }
    
    /**
     * Check if synapse is dead (should be pruned)
     */
    public boolean isDead() {
        return weight < 0.1;
    }
    
    @Override
    public String toString() {
        return String.format("Synapse[→%s | w=%.2f]", 
            target.id.substring(0, 8), weight);
    }
}
