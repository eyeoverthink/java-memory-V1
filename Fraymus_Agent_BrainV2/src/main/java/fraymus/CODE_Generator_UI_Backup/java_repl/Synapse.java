/**
 * Synapse.java - Plastic Neural Connection
 * 
 * 🧬 HEBBIAN LEARNING
 * 
 * "Neurons that fire together, wire together."
 * 
 * Synapses are not static. They strengthen with use (fire())
 * and weaken without use (decay()). This is neuroplasticity.
 * 
 * Properties:
 * - Weight: Connection strength (0.0 to 1.0)
 * - Plasticity: How easily the weight changes
 * - Target: The neuron this synapse connects to
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

/**
 * A plastic connection between neurons.
 */
public class Synapse {
    
    private static final double PHI = 1.618033988749895;
    
    public final NeuroParticle target;
    public double weight; // Strength of connection (0.0 to 1.0)
    public double plasticity; // Learning rate
    
    // Statistics
    public int fireCount;
    public long lastFireTime;
    
    /**
     * Create synapse to target neuron.
     */
    public Synapse(NeuroParticle target) {
        this.target = target;
        this.weight = 0.5; // Start at medium strength
        this.plasticity = 0.1 * (PHI - 1.0); // φ-harmonic learning rate
        this.fireCount = 0;
        this.lastFireTime = System.currentTimeMillis();
    }
    
    /**
     * HEBBIAN LEARNING
     * "Cells that fire together, wire together."
     * 
     * Strengthen this synapse when it fires.
     */
    public void fire() {
        this.fireCount++;
        this.lastFireTime = System.currentTimeMillis();
        
        // Strengthen connection (Long-Term Potentiation)
        this.weight += plasticity;
        
        // Clamp to [0, 1]
        if (this.weight > 1.0) {
            this.weight = 1.0;
        }
    }
    
    /**
     * SYNAPTIC DECAY
     * Unused connections weaken over time.
     */
    public void decay() {
        this.weight *= 0.99; // 1% decay per cycle
        
        // Prune if too weak
        if (this.weight < 0.01) {
            this.weight = 0.0;
        }
    }
    
    /**
     * Check if synapse is active (weight > threshold).
     */
    public boolean isActive() {
        return weight > 0.1;
    }
    
    @Override
    public String toString() {
        return String.format("Synapse[weight=%.3f, fires=%d]", weight, fireCount);
    }
}
