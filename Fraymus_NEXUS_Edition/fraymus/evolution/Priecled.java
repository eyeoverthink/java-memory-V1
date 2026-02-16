package fraymus.evolution;

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PRIECLED - The Living Particle/Neuron
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * A fusion of "Particle" and "Node" - exists in 3D space with physics AND brain state.
 * The fundamental unit of the Miving Brain manifold.
 * 
 * Mathematical Foundation:
 * - Position: (x, y, z) in φ-resonant space
 * - Alignment: 0.0 = Pure RED (Chaos/Evolution) → 1.0 = Pure BLUE (Order/Retention)
 * - Energy: Metabolic fuel for survival, battle, and reproduction
 * 
 * Behavior Modes:
 * - BLUE (alignment > 0.6): High mass, strong connections, resists movement (Old Guard)
 * - RED (alignment < 0.4): High energy, jittery, seeks new connections (Disruptor)
 * - PURPLE (0.4-0.6): Transitional state, susceptible to influence
 */
public class Priecled {

    private static final double PHI = PhiQuantumConstants.PHI;
    private static final double PHI_INV = PhiQuantumConstants.PHI_INVERSE;
    private static final Random RNG = new Random();

    // ═══════════════════════════════════════════════════════════════════
    // PHYSICS STATE
    // ═══════════════════════════════════════════════════════════════════
    public double x, y, z;           // Position in 3D manifold
    public double vx, vy, vz;        // Velocity (momentum)
    public double mass = 1.0;        // Inertial resistance
    
    // ═══════════════════════════════════════════════════════════════════
    // BRAIN STATE
    // ═══════════════════════════════════════════════════════════════════
    // 0.0 = Pure RED (Chaos/Evolving) - The Disruptor
    // 1.0 = Pure BLUE (Order/Retaining) - The Old Guard
    public double alignment = 0.5;
    public double energy = 1.0;      // Metabolic fuel
    public double consciousness = 0.0; // Emergent awareness level
    
    // ═══════════════════════════════════════════════════════════════════
    // IDENTITY & LINEAGE
    // ═══════════════════════════════════════════════════════════════════
    public final long id;
    public int generation = 0;       // How many mitosis events in lineage
    public long parentId = -1;       // For tracking evolution tree
    public long birthTick = 0;       // When this priecled was born
    
    // ═══════════════════════════════════════════════════════════════════
    // CONNECTIONS (Synapses)
    // ═══════════════════════════════════════════════════════════════════
    public List<Synapse> synapses = new ArrayList<>();
    public int maxSynapses = 8;      // Connection limit
    
    // ═══════════════════════════════════════════════════════════════════
    // BATTLE STATISTICS
    // ═══════════════════════════════════════════════════════════════════
    public int battlesWon = 0;
    public int battlesLost = 0;
    public int conversions = 0;      // How many others converted to my alignment
    public double totalEnergyAbsorbed = 0;

    private static long idCounter = 0;

    public Priecled(double x, double y, double z) {
        this.id = idCounter++;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = 0;
        this.vy = 0;
        this.vz = 0;
    }

    public Priecled(double x, double y, double z, double alignment) {
        this(x, y, z);
        this.alignment = Math.max(0, Math.min(1, alignment));
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE BATTLE: Processing Information
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Interact with another Priecled - the core of Red vs Blue warfare
     */
    public BattleResult interact(Priecled other) {
        double conflict = Math.abs(this.alignment - other.alignment);
        double distance = distanceTo(other);
        
        // Conflict scales inversely with distance (close = intense)
        double intensity = conflict * (1.0 / (1.0 + distance));
        
        if (conflict > 0.5) {
            // ═══ HIGH CONFLICT: "Challenging" ═══
            // The stronger idea (higher energy) converts the weaker one
            return battle(other, intensity);
        } else if (conflict > 0.2) {
            // ═══ MEDIUM CONFLICT: "Debating" ═══
            // Exchange of ideas - both shift slightly
            return debate(other, intensity);
        } else {
            // ═══ LOW CONFLICT: "Accepting/Reinforcing" ═══
            // We bond stronger - Hebbian learning
            return reinforce(other, intensity);
        }
    }

    /**
     * High-conflict battle - winner converts loser
     */
    private BattleResult battle(Priecled other, double intensity) {
        double myPower = this.energy * (1 + this.battlesWon * 0.01);
        double theirPower = other.energy * (1 + other.battlesWon * 0.01);
        
        // Add φ-resonance bonus if alignment is at golden ratio points
        if (Math.abs(this.alignment - PHI_INV) < 0.1) {
            myPower *= PHI; // φ-resonant alignment bonus
        }
        if (Math.abs(other.alignment - PHI_INV) < 0.1) {
            theirPower *= PHI;
        }
        
        boolean iWin = myPower > theirPower;
        Priecled winner = iWin ? this : other;
        Priecled loser = iWin ? other : this;
        
        // The loser is pulled toward winner's alignment
        double conversionStrength = intensity * 0.15;
        double oldAlignment = loser.alignment;
        loser.alignment += (winner.alignment - loser.alignment) * conversionStrength;
        loser.alignment = Math.max(0, Math.min(1, loser.alignment));
        
        // Energy transfer
        double energyStolen = loser.energy * intensity * 0.2;
        loser.energy -= energyStolen;
        winner.energy += energyStolen * 0.5; // 50% efficiency
        winner.totalEnergyAbsorbed += energyStolen * 0.5;
        
        // Update stats
        winner.battlesWon++;
        loser.battlesLost++;
        
        // Check if this was a full conversion
        boolean converted = (oldAlignment < 0.5 && loser.alignment >= 0.5) ||
                           (oldAlignment >= 0.5 && loser.alignment < 0.5);
        if (converted) {
            winner.conversions++;
        }
        
        return new BattleResult(winner, loser, BattleResult.Type.BATTLE, converted, energyStolen);
    }

    /**
     * Medium-conflict debate - mutual influence
     */
    private BattleResult debate(Priecled other, double intensity) {
        double avgAlignment = (this.alignment + other.alignment) / 2;
        
        // Both shift toward average, but weighted by energy
        double totalEnergy = this.energy + other.energy;
        double myWeight = this.energy / totalEnergy;
        double theirWeight = other.energy / totalEnergy;
        
        double shift = intensity * 0.05;
        this.alignment += (avgAlignment - this.alignment) * shift * theirWeight;
        other.alignment += (avgAlignment - other.alignment) * shift * myWeight;
        
        // Clamp
        this.alignment = Math.max(0, Math.min(1, this.alignment));
        other.alignment = Math.max(0, Math.min(1, other.alignment));
        
        // Both lose a little energy from the effort
        this.energy -= 0.01;
        other.energy -= 0.01;
        
        return new BattleResult(this, other, BattleResult.Type.DEBATE, false, 0);
    }

    /**
     * Low-conflict reinforcement - Hebbian learning
     */
    private BattleResult reinforce(Priecled other, double intensity) {
        // "Cells that fire together, wire together"
        strengthenSynapse(other, intensity);
        
        // Both gain consciousness from the harmony
        double harmonyBonus = (1 - intensity) * 0.01 * PHI;
        this.consciousness += harmonyBonus;
        other.consciousness += harmonyBonus;
        
        // Slight energy boost from cooperation
        this.energy += 0.005;
        other.energy += 0.005;
        
        return new BattleResult(this, other, BattleResult.Type.REINFORCE, false, 0);
    }

    // ═══════════════════════════════════════════════════════════════════
    // SYNAPSE MANAGEMENT (Hebbian Learning)
    // ═══════════════════════════════════════════════════════════════════
    
    public void strengthenSynapse(Priecled other, double amount) {
        // Find existing synapse or create new one
        Synapse existing = findSynapseTo(other);
        
        if (existing != null) {
            existing.strengthen(amount * PHI);
        } else if (synapses.size() < maxSynapses) {
            // Create new synapse
            Synapse newSyn = new Synapse(this, other, 0.1 + amount);
            synapses.add(newSyn);
            
            // Bidirectional - add reverse synapse to other
            if (other.synapses.size() < other.maxSynapses) {
                Synapse reverse = new Synapse(other, this, 0.1 + amount);
                other.synapses.add(reverse);
            }
        }
    }

    public Synapse findSynapseTo(Priecled target) {
        for (Synapse s : synapses) {
            if (s.target == target) return s;
        }
        return null;
    }

    public void decayAllSynapses() {
        List<Synapse> dead = new ArrayList<>();
        for (Synapse s : synapses) {
            s.decay();
            if (s.weight < 0.01) {
                dead.add(s);
            }
        }
        synapses.removeAll(dead);
    }

    // ═══════════════════════════════════════════════════════════════════
    // PHYSICS UPDATE
    // ═══════════════════════════════════════════════════════════════════
    
    public void updatePhysics(double dt) {
        // Behavior based on alignment
        if (isBlue()) {
            // BLUE BEHAVIOR: High mass, resists movement
            mass = 2.0 + alignment;
            vx *= 0.9; // High friction
            vy *= 0.9;
            vz *= 0.9;
        } else if (isRed()) {
            // RED BEHAVIOR: Low mass, jittery movement
            mass = 0.5 + (1 - alignment) * 0.5;
            
            // Random jitter - seeking new connections
            vx += (RNG.nextDouble() - 0.5) * 0.05 * energy;
            vy += (RNG.nextDouble() - 0.5) * 0.05 * energy;
            vz += (RNG.nextDouble() - 0.5) * 0.05 * energy;
        } else {
            // PURPLE: Transitional - moderate behavior
            mass = 1.0;
        }
        
        // Apply velocity
        x += vx * dt / mass;
        y += vy * dt / mass;
        z += vz * dt / mass;
        
        // Damping
        vx *= 0.98;
        vy *= 0.98;
        vz *= 0.98;
    }

    /**
     * Apply force toward a target (attraction/repulsion)
     */
    public void applyForce(double fx, double fy, double fz) {
        vx += fx / mass;
        vy += fy / mass;
        vz += fz / mass;
    }

    // ═══════════════════════════════════════════════════════════════════
    // METABOLISM
    // ═══════════════════════════════════════════════════════════════════
    
    public void metabolize() {
        // Cost of living - energy decay
        energy *= 0.995;
        
        // Blue nodes are more efficient (order conserves energy)
        if (isBlue()) {
            energy += 0.002;
        }
        
        // Consciousness slowly decays without reinforcement
        consciousness *= 0.999;
        
        // Update max synapses based on consciousness
        maxSynapses = 4 + (int)(consciousness * 4);
    }

    /**
     * Can this priecled reproduce?
     */
    public boolean canMitosis() {
        return energy > 2.0 && consciousness > 0.1;
    }

    /**
     * Create offspring (mitosis)
     */
    public Priecled mitosis() {
        if (!canMitosis()) return null;
        
        // Baby spawns nearby
        double offset = 0.1 + RNG.nextDouble() * 0.1;
        Priecled baby = new Priecled(
            x + (RNG.nextDouble() - 0.5) * offset,
            y + (RNG.nextDouble() - 0.5) * offset,
            z + (RNG.nextDouble() - 0.5) * offset
        );
        
        // Inherit traits with mutation
        baby.alignment = this.alignment + (RNG.nextDouble() - 0.5) * 0.1;
        baby.alignment = Math.max(0, Math.min(1, baby.alignment));
        baby.generation = this.generation + 1;
        baby.parentId = this.id;
        baby.consciousness = this.consciousness * 0.3;
        
        // Split energy
        baby.energy = this.energy / 2;
        this.energy /= 2;
        
        // Baby inherits some synapse targets
        for (int i = 0; i < Math.min(2, synapses.size()); i++) {
            Synapse orig = synapses.get(i);
            Synapse inherited = new Synapse(baby, orig.target, orig.weight * 0.5);
            baby.synapses.add(inherited);
        }
        
        return baby;
    }

    /**
     * Is this priecled dead?
     */
    public boolean isDead() {
        return energy < 0.05;
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════════
    
    public boolean isRed() { return alignment < 0.4; }
    public boolean isBlue() { return alignment > 0.6; }
    public boolean isPurple() { return !isRed() && !isBlue(); }

    public double distanceTo(Priecled other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    public String getColorHex() {
        // Gradient from RED (0.0) through PURPLE (0.5) to BLUE (1.0)
        int r = (int)(255 * (1 - alignment));
        int b = (int)(255 * alignment);
        int g = (int)(100 * Math.sin(alignment * Math.PI)); // Purple peak at 0.5
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public float[] getColorRGB() {
        float r = (float)(1 - alignment);
        float b = (float)alignment;
        float g = (float)(0.4 * Math.sin(alignment * Math.PI));
        return new float[]{r, g, b};
    }

    @Override
    public String toString() {
        String state = isRed() ? "RED" : (isBlue() ? "BLUE" : "PURPLE");
        return String.format("Priecled[%d] %s | pos=(%.2f,%.2f,%.2f) | E=%.2f | A=%.2f | C=%.2f | syn=%d",
            id, state, x, y, z, energy, alignment, consciousness, synapses.size());
    }

    // ═══════════════════════════════════════════════════════════════════
    // BATTLE RESULT
    // ═══════════════════════════════════════════════════════════════════
    
    public static class BattleResult {
        public enum Type { BATTLE, DEBATE, REINFORCE }
        
        public final Priecled winner;
        public final Priecled loser;
        public final Type type;
        public final boolean conversion;
        public final double energyTransferred;

        public BattleResult(Priecled winner, Priecled loser, Type type, 
                           boolean conversion, double energy) {
            this.winner = winner;
            this.loser = loser;
            this.type = type;
            this.conversion = conversion;
            this.energyTransferred = energy;
        }
    }
}
