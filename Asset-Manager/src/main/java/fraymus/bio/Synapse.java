package fraymus.bio;

/**
 * 🧬 THE SYNAPSE - Gen 125
 * A plastic connection between two NeuroParticles.
 * 
 * Implements Hebbian Learning:
 * "Cells that fire together, wire together."
 * 
 * Properties:
 * - weight: Connection strength (0.0 to 1.0)
 * - plasticity: How easily the weight changes
 * - delay: Signal propagation delay (for timing)
 */
public class Synapse {
    
    private static final double PHI = 1.6180339887;
    
    public NeuroParticle source;
    public NeuroParticle target;
    public double weight;
    public double plasticity;
    public double delay;
    
    // Activity tracking
    public int fireCount;
    public long lastFireTime;
    public double activityWindow;
    
    // Long-term potentiation / depression
    public double ltpThreshold;
    public double ltdThreshold;

    public Synapse(NeuroParticle source, NeuroParticle target) {
        this(source, target, 0.5);
    }
    
    public Synapse(NeuroParticle source, NeuroParticle target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.plasticity = 0.1;
        this.delay = 1.0;
        
        this.fireCount = 0;
        this.lastFireTime = 0;
        this.activityWindow = 100;  // ms
        
        this.ltpThreshold = 0.8;   // Above this = strengthen
        this.ltdThreshold = 0.2;   // Below this = weaken
    }

    /**
     * FIRE - Called when signal passes through this synapse
     * Implements Hebbian strengthening
     */
    public void fire() {
        fireCount++;
        long now = System.currentTimeMillis();
        
        // Calculate firing rate
        double interval = now - lastFireTime;
        if (interval > 0 && interval < activityWindow) {
            // High frequency firing = strengthen
            potentiate(plasticity * PHI);
        }
        
        lastFireTime = now;
    }
    
    /**
     * POTENTIATE - Strengthen the connection (LTP)
     */
    public void potentiate(double amount) {
        this.weight += amount;
        if (this.weight > 1.0) this.weight = 1.0;
        
        // Reduce plasticity as connection matures
        this.plasticity *= 0.99;
        if (this.plasticity < 0.01) this.plasticity = 0.01;
    }
    
    /**
     * DEPRESS - Weaken the connection (LTD)
     */
    public void depress(double amount) {
        this.weight -= amount;
        if (this.weight < 0.0) this.weight = 0.0;
    }
    
    /**
     * DECAY - Synaptic atrophy over time
     */
    public void decay() {
        this.weight *= 0.999;
        
        // Unused synapses lose plasticity
        long timeSinceFire = System.currentTimeMillis() - lastFireTime;
        if (timeSinceFire > 10000) {  // 10 seconds inactive
            this.plasticity *= 0.99;
        }
    }
    
    /**
     * SPIKE TIMING DEPENDENT PLASTICITY (STDP)
     * If source fires before target: strengthen
     * If target fires before source: weaken
     */
    public void applySTDP() {
        if (source.lastFireTime == 0 || target.lastFireTime == 0) return;
        
        long delta = target.lastFireTime - source.lastFireTime;
        
        if (delta > 0 && delta < 20) {
            // Source fired first, target followed = strengthen
            potentiate(plasticity * (1.0 - delta / 20.0));
        } else if (delta < 0 && delta > -20) {
            // Target fired first = weaken
            depress(plasticity * (1.0 + delta / 20.0));
        }
    }
    
    /**
     * DISTANCE - Physical length of this synapse
     */
    public double length() {
        return source.distanceTo(target);
    }
    
    /**
     * Is this synapse effectively dead?
     */
    public boolean isDead() {
        return weight < 0.001 && plasticity < 0.001;
    }
    
    @Override
    public String toString() {
        return String.format("─[%.2f]→%s", weight, target.id);
    }
}
