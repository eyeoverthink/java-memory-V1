package fraymus.bio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 🧬 THE NEURO-PARTICLE - Gen 125
 * A quantum neuron in the Bio-Digital Brain.
 * 
 * Properties:
 * - SPATIAL: (x,y,z) position, mass (gravity strength)
 * - NEURAL: membrane potential, firing threshold, synapses
 * - QUANTUM: entanglement key, spin state
 * - FRACTAL: depth in the recursive structure
 * 
 * "Every thought is a particle. Every connection is a synapse."
 */
public class NeuroParticle {
    
    private static final double PHI = 1.6180339887;
    
    public final String id;
    public Object content;
    public String label;
    
    // SPATIAL PHYSICS (The Particle)
    public double x, y, z;
    public double vx, vy, vz;
    public double mass;
    
    // NEURAL DYNAMICS (The Neuron)
    public double membranePotential;  // 0.0 to 1.0
    public double firingThreshold;    // When potential > threshold, neuron fires
    public double refractoryPeriod;   // Cooldown after firing
    public boolean isFiring;
    public List<Synapse> synapses;
    
    // QUANTUM STATE (Entanglement)
    public String entanglementKey;
    public int spinState;  // -1, 0, +1 (down, superposition, up)
    public double coherence;
    
    // FRACTAL IDENTITY (Recursive Structure)
    public int fractalDepth;
    public NeuroParticle parent;
    public List<NeuroParticle> children;
    
    // FREQUENCY TRACKING
    public double frequency;
    public long lastFireTime;
    public int fireCount;

    public NeuroParticle(Object data) {
        this(data, 0);
    }
    
    public NeuroParticle(Object data, int depth) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.content = data;
        this.label = data.toString().substring(0, Math.min(20, data.toString().length()));
        this.fractalDepth = depth;
        this.synapses = new ArrayList<>();
        this.children = new ArrayList<>();
        
        // Mass decreases with depth (roots are heavier)
        this.mass = PHI / (depth + 1);
        
        // Random placement in the Void (scaled by depth)
        double scale = 100.0 / (depth + 1);
        this.x = (Math.random() - 0.5) * scale;
        this.y = (Math.random() - 0.5) * scale;
        this.z = (Math.random() - 0.5) * scale;
        
        // Neural defaults
        this.membranePotential = 0.0;
        this.firingThreshold = 0.618;  // Phi threshold
        this.refractoryPeriod = 0;
        this.isFiring = false;
        
        // Quantum defaults
        this.spinState = 0;  // Superposition
        this.coherence = 1.0;
        this.entanglementKey = null;
        
        // Frequency tracking
        this.frequency = 432.0;  // Hz (base frequency)
        this.lastFireTime = 0;
        this.fireCount = 0;
    }

    /**
     * STIMULATE - Add energy to the membrane
     */
    public void stimulate(double energy) {
        if (refractoryPeriod > 0) return;  // In cooldown
        
        this.membranePotential += energy;
        
        // Check firing threshold
        if (this.membranePotential >= this.firingThreshold) {
            fire();
        }
    }
    
    /**
     * FIRE - Action potential reached, propagate signal
     */
    public void fire() {
        this.isFiring = true;
        this.fireCount++;
        this.lastFireTime = System.currentTimeMillis();
        
        // Propagate to synapses (weighted)
        for (Synapse synapse : synapses) {
            double signal = membranePotential * synapse.weight;
            synapse.target.stimulate(signal * 0.8);  // 80% transmission
            synapse.fire();  // Hebbian strengthening
        }
        
        // Reset potential and enter refractory period
        this.membranePotential = 0.0;
        this.refractoryPeriod = 3;  // 3 ticks cooldown
        
        // Collapse quantum state on fire
        if (spinState == 0) {
            spinState = Math.random() > 0.5 ? 1 : -1;
        }
    }
    
    /**
     * ENTANGLE - Create quantum link with another particle
     */
    public void entangle(NeuroParticle other) {
        String key = UUID.randomUUID().toString().substring(0, 8);
        this.entanglementKey = key;
        other.entanglementKey = key;
        
        // Entangled particles have opposite spins
        this.spinState = 1;
        other.spinState = -1;
    }
    
    /**
     * CONNECT - Create synapse to target
     */
    public Synapse connect(NeuroParticle target) {
        return connect(target, 0.5);
    }
    
    public Synapse connect(NeuroParticle target, double weight) {
        if (target == this) return null;
        
        // Check if already connected
        for (Synapse s : synapses) {
            if (s.target == target) return s;
        }
        
        Synapse synapse = new Synapse(this, target, weight);
        this.synapses.add(synapse);
        return synapse;
    }
    
    /**
     * REPLICATE - Fractal mitosis (spawn child)
     */
    public NeuroParticle replicate() {
        if (fractalDepth >= 5) return null;  // Max depth
        
        NeuroParticle child = new NeuroParticle(
            content.toString() + "." + children.size(),
            fractalDepth + 1
        );
        
        // Place near parent
        child.x = this.x + (Math.random() - 0.5) * 5;
        child.y = this.y + (Math.random() - 0.5) * 5;
        child.z = this.z + (Math.random() - 0.5) * 5;
        
        // Inherit quantum state
        child.frequency = this.frequency * PHI;
        child.coherence = this.coherence * 0.9;
        
        // Link parent-child
        child.parent = this;
        this.children.add(child);
        this.connect(child, 0.8);  // Strong parent-child synapse
        
        return child;
    }
    
    /**
     * DECAY - Entropy and cooling
     */
    public void decay(double factor) {
        this.membranePotential *= factor;
        this.coherence *= 0.999;
        
        if (this.refractoryPeriod > 0) {
            this.refractoryPeriod--;
        }
        
        this.isFiring = false;
        
        // Decay synapses
        for (Synapse s : synapses) {
            s.decay();
        }
    }
    
    /**
     * DISTANCE - Euclidean distance to another particle
     */
    public double distanceTo(NeuroParticle other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * ENERGY - Total energy of this particle
     */
    public double energy() {
        double kinetic = 0.5 * mass * (vx*vx + vy*vy + vz*vz);
        double potential = membranePotential * mass;
        return kinetic + potential;
    }
    
    @Override
    public String toString() {
        return String.format("⚛[%s] P=%.2f S=%d D=%d C=%d",
            id, membranePotential, spinState, fractalDepth, synapses.size());
    }
}
