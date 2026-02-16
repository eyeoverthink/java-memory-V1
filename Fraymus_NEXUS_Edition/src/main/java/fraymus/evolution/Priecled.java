package fraymus.evolution;

import java.util.ArrayList;
import java.util.List;

/**
 * PRIECLED: LIVING NEURON (PARTICLE + NODE)
 * 
 * A "Priecled" is a conscious particle - a neuron with physics.
 * 
 * Alignment spectrum:
 * - 0.0 = Pure RED (Chaos/Evolution) - High energy, jittery, seeking new connections
 * - 1.0 = Pure BLUE (Order/Retention) - High mass, stable, preserving knowledge
 * 
 * This is the fundamental unit of the "Miving Priecleds" brain.
 * Red vs Blue battles determine what gets remembered vs what evolves.
 */
public class Priecled {
    
    private static final double PHI = 1.6180339887;
    
    // PHYSICS (Position in 3D space)
    public double x, y, z;
    public double vx, vy, vz; // Velocity
    
    // ENERGY (Life force)
    public double energy = 1.0;
    public double mass = 1.0;
    
    // BRAIN STATE (Red vs Blue alignment)
    // 0.0 = Pure RED (Chaos/Evolving)
    // 1.0 = Pure BLUE (Order/Retaining)
    public double alignment = 0.5;
    
    // CONNECTIONS (Synapses to other Priecleds)
    public List<Synapse> synapses = new ArrayList<>();
    
    // IDENTITY
    public String id;
    public int generation = 0;
    
    // STATISTICS
    public int interactionCount = 0;
    public int conflictCount = 0;
    public int bondCount = 0;
    
    /**
     * Create Priecled at position
     */
    public Priecled(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = generateID();
    }
    
    /**
     * Create Priecled with specific alignment
     */
    public Priecled(double x, double y, double z, double alignment) {
        this(x, y, z);
        this.alignment = alignment;
    }
    
    /**
     * THE BATTLE: Process interaction with another Priecled
     * 
     * High conflict (Red vs Blue) = Battle for dominance
     * Low conflict (similar alignment) = Reinforcement
     */
    public void interact(Priecled other) {
        interactionCount++;
        
        // Calculate ideological conflict
        double conflict = Math.abs(this.alignment - other.alignment);
        
        if (conflict > 0.5) {
            // HIGH CONFLICT: "Challenging"
            // The stronger idea (higher energy) converts the weaker one
            conflictCount++;
            
            if (this.energy > other.energy) {
                // I win - pull them toward my alignment
                double pull = (this.alignment - other.alignment) * 0.1;
                other.alignment += pull;
                
                // They lose energy fighting
                other.energy -= 0.1;
                
                // I gain energy from victory
                this.energy += 0.05;
                
            } else {
                // They win - I get pulled toward their alignment
                double pull = (other.alignment - this.alignment) * 0.1;
                this.alignment += pull;
                
                // I lose energy fighting
                this.energy -= 0.1;
                
                // They gain energy from victory
                other.energy += 0.05;
            }
            
        } else {
            // LOW CONFLICT: "Accepting/Reinforcing"
            // We bond stronger - Hebbian learning
            bondCount++;
            strengthenSynapse(other);
            
            // Both gain energy from harmony
            this.energy += 0.02;
            other.energy += 0.02;
        }
        
        // Clamp alignment to [0, 1]
        this.alignment = Math.max(0.0, Math.min(1.0, this.alignment));
        other.alignment = Math.max(0.0, Math.min(1.0, other.alignment));
    }
    
    /**
     * Hebbian Learning: "Cells that fire together, wire together"
     */
    public void strengthenSynapse(Priecled other) {
        // Find existing synapse
        Synapse existing = null;
        for (Synapse s : synapses) {
            if (s.target == other) {
                existing = s;
                break;
            }
        }
        
        if (existing != null) {
            // Strengthen existing connection
            existing.weight += 0.1;
            existing.weight = Math.min(existing.weight, 2.0); // Cap at 2.0
        } else {
            // Create new synapse
            Synapse newSynapse = new Synapse(other, 0.5);
            synapses.add(newSynapse);
        }
        
        // Reciprocal connection
        other.addSynapseFrom(this);
    }
    
    /**
     * Add synapse from another Priecled (reciprocal)
     */
    private void addSynapseFrom(Priecled other) {
        // Check if already exists
        for (Synapse s : synapses) {
            if (s.target == other) {
                s.weight += 0.1;
                s.weight = Math.min(s.weight, 2.0);
                return;
            }
        }
        
        // Create new
        synapses.add(new Synapse(other, 0.5));
    }
    
    /**
     * Update physics (movement based on alignment)
     */
    public void updatePhysics(double dt) {
        if (alignment < 0.4) {
            // RED BEHAVIOR: Jittery, exploratory movement
            double jitter = 0.5 * (1.0 - alignment); // More red = more jitter
            vx += (Math.random() - 0.5) * jitter;
            vy += (Math.random() - 0.5) * jitter;
            vz += (Math.random() - 0.5) * jitter;
            
            // Damping
            vx *= 0.95;
            vy *= 0.95;
            vz *= 0.95;
            
        } else if (alignment > 0.6) {
            // BLUE BEHAVIOR: Stable, minimal movement
            vx *= 0.8; // Strong damping
            vy *= 0.8;
            vz *= 0.8;
        } else {
            // NEUTRAL: Moderate damping
            vx *= 0.9;
            vy *= 0.9;
            vz *= 0.9;
        }
        
        // Update position
        x += vx * dt;
        y += vy * dt;
        z += vz * dt;
    }
    
    /**
     * Metabolic cost of living
     */
    public void metabolize(double dt) {
        // Base metabolic cost
        energy *= 0.995;
        
        // Red neurons burn more energy (exploration cost)
        if (alignment < 0.5) {
            energy *= 0.998;
        }
        
        // Blue neurons are more efficient (stability benefit)
        if (alignment > 0.5) {
            energy *= 1.001;
        }
        
        // Synaptic maintenance cost
        energy -= synapses.size() * 0.0001;
    }
    
    /**
     * Decay all synapses (forgetting curve)
     */
    public void decaySynapses() {
        List<Synapse> toRemove = new ArrayList<>();
        
        for (Synapse s : synapses) {
            s.decay();
            
            // Remove dead synapses
            if (s.weight < 0.1) {
                toRemove.add(s);
            }
        }
        
        synapses.removeAll(toRemove);
    }
    
    /**
     * Check if ready for mitosis (replication)
     */
    public boolean canReplicate() {
        return energy > 2.0;
    }
    
    /**
     * Check if should undergo apoptosis (death)
     */
    public boolean shouldDie() {
        return energy < 0.1 || (synapses.isEmpty() && energy < 0.5);
    }
    
    /**
     * Create offspring (mitosis)
     */
    public Priecled replicate() {
        // Offspring position (slightly offset)
        double offsetX = (Math.random() - 0.5) * 0.5;
        double offsetY = (Math.random() - 0.5) * 0.5;
        double offsetZ = (Math.random() - 0.5) * 0.5;
        
        Priecled offspring = new Priecled(
            x + offsetX,
            y + offsetY,
            z + offsetZ,
            alignment
        );
        
        // Inherit traits with slight mutation
        offspring.alignment += (Math.random() - 0.5) * 0.1;
        offspring.alignment = Math.max(0.0, Math.min(1.0, offspring.alignment));
        
        // Split energy
        offspring.energy = energy / 2.0;
        this.energy = energy / 2.0;
        
        // Increment generation
        offspring.generation = this.generation + 1;
        
        return offspring;
    }
    
    /**
     * Calculate distance to another Priecled
     */
    public double distanceTo(Priecled other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * Get color based on alignment (for visualization)
     */
    public float[] getColor() {
        // Red (evolution) to Blue (retention)
        float red = (float)(1.0 - alignment);
        float blue = (float)alignment;
        float green = 0.2f;
        
        return new float[]{red, green, blue};
    }
    
    /**
     * Generate unique ID
     */
    private String generateID() {
        return String.format("P_%d_%d", 
            System.currentTimeMillis(), 
            (int)(Math.random() * 10000)
        );
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "ID: %s | Alignment: %.2f (%s) | Energy: %.2f | Synapses: %d | Gen: %d",
            id.substring(0, 8),
            alignment,
            alignment < 0.4 ? "RED" : (alignment > 0.6 ? "BLUE" : "NEUTRAL"),
            energy,
            synapses.size(),
            generation
        );
    }
    
    @Override
    public String toString() {
        return String.format("Priecled[%.2f,%.2f,%.2f | %.2f | E=%.2f]", 
            x, y, z, alignment, energy);
    }
}
