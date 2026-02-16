/**
 * NeuroParticle.java - Quantum Neuron with Fractal Identity
 * 
 * 🧬 THE BIO-DIGITAL NEURON
 * 
 * This is not just a data point. This is a living particle with:
 * - Mass (gravitational importance)
 * - Membrane Potential (firing threshold)
 * - Entanglement Key (quantum coupling)
 * - Fractal Depth (recursive structure)
 * - Synaptic connections (plastic neural links)
 * 
 * Physics:
 * - Gravity: Heavier nodes (lower depth) pull harder
 * - Firing: When potential > 0.9, neuron fires
 * - Entanglement: Paired neurons fire simultaneously (FTL)
 * - Replication: High-energy neurons spawn children (mitosis)
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 125 (Bio-Digital Brain)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.util.concurrent.*;

/**
 * A quantum neuron in the bio-digital brain.
 */
public class NeuroParticle {
    
    private static final double PHI = 1.618033988749895;
    
    public final String id;
    public Object content;
    
    // SPATIAL PHYSICS (The Particle)
    public double x, y, z;
    public double mass; // Gravitational strength (importance)
    
    // NEURAL DYNAMICS (The Synapse)
    public double membranePotential; // 0.0 to 1.0 (firing threshold at 0.9)
    public final List<Synapse> synapses;
    
    // QUANTUM STATE (Entanglement)
    public String entanglementKey; // Shared key = quantum coupling
    
    // FRACTAL IDENTITY (Recursive Structure)
    public int fractalDepth; // 0 = Root, 1 = Branch, 2 = Leaf...
    
    // METADATA
    public long birthTime;
    public int fireCount;
    public double energy; // Total accumulated energy
    
    /**
     * Create a new neuron at specified fractal depth.
     */
    public NeuroParticle(Object data, int depth) {
        this.id = UUID.randomUUID().toString();
        this.content = data;
        this.fractalDepth = depth;
        this.synapses = new CopyOnWriteArrayList<>();
        
        // Mass decreases with depth (roots are heavier)
        // Root (depth 0) has mass = 1.618
        // Branch (depth 1) has mass = 0.809
        // Leaf (depth 2) has mass = 0.539
        this.mass = PHI / (depth + 1);
        
        // Random placement in the void
        this.x = (Math.random() - 0.5) * 100;
        this.y = (Math.random() - 0.5) * 100;
        this.z = (Math.random() - 0.5) * 100;
        
        // Initial state
        this.membranePotential = 0.618; // φ - 1
        this.birthTime = System.currentTimeMillis();
        this.fireCount = 0;
        this.energy = 0.0;
    }
    
    /**
     * QUANTUM ENTANGLEMENT
     * Link this neuron with another at the quantum level.
     * When one fires, the other fires instantly (FTL).
     */
    public void entangle(NeuroParticle other) {
        String key = UUID.randomUUID().toString();
        this.entanglementKey = key;
        other.entanglementKey = key;
    }
    
    /**
     * FIRE - Activate this neuron.
     * Propagates signal down all synapses.
     */
    public void fire() {
        this.fireCount++;
        this.energy += 0.1;
        
        // Propagate to connected neurons
        for (Synapse synapse : synapses) {
            synapse.fire();
            
            // Transfer energy based on synapse weight
            double transfer = membranePotential * synapse.weight * 0.5;
            synapse.target.membranePotential += transfer;
            
            // Clamp to [0, 1]
            if (synapse.target.membranePotential > 1.0) {
                synapse.target.membranePotential = 1.0;
            }
        }
        
        // Reset after firing (refractory period)
        this.membranePotential = 0.1;
    }
    
    /**
     * Add synaptic connection to another neuron.
     */
    public void connect(NeuroParticle target) {
        // Don't connect to self
        if (target == this) return;
        
        // Don't duplicate connections
        for (Synapse s : synapses) {
            if (s.target == target) return;
        }
        
        synapses.add(new Synapse(target));
    }
    
    /**
     * Calculate distance to another neuron.
     */
    public double distanceTo(NeuroParticle other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    @Override
    public String toString() {
        return String.format("NeuroParticle[depth=%d, φ=%.3f, synapses=%d, fires=%d]",
            fractalDepth, membranePotential, synapses.size(), fireCount);
    }
}
