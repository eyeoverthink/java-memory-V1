package fraymus.evolution;

import fraymus.quantum.core.PhiQuantumConstants;

/**
 * SYNAPSE - The Living Wire
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * A connection between two Priecleds that:
 * - Decays if unused (forgetting curve)
 * - Strengthens if reinforced (Hebbian learning)
 * - Carries information bidirectionally
 * 
 * Mathematical Foundation:
 * - Weight: Connection strength (0.0 = dead, 1.0+ = strong memory)
 * - Decay: Exponential forgetting (weight *= 0.99 per tick)
 * - Strengthen: φ-scaled reinforcement
 * - Transmission: Signal strength = weight × source.energy
 */
public class Synapse {

    private static final double PHI = PhiQuantumConstants.PHI;
    private static final double PHI_INV = PhiQuantumConstants.PHI_INVERSE;

    // ═══════════════════════════════════════════════════════════════════
    // CORE CONNECTION
    // ═══════════════════════════════════════════════════════════════════
    public final Priecled source;
    public final Priecled target;
    public double weight;           // Strength of memory (0.0 - unbounded)
    
    // ═══════════════════════════════════════════════════════════════════
    // DYNAMICS
    // ═══════════════════════════════════════════════════════════════════
    public double lastSignal = 0;   // Last transmitted value
    public long lastFired = 0;      // Tick when last used
    public int fireCount = 0;       // How many times this synapse fired
    
    // ═══════════════════════════════════════════════════════════════════
    // PLASTICITY PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    public double decayRate = 0.995;        // Per-tick decay (forgetting)
    public double strengthenRate = 0.1;     // Per-reinforce boost
    public double maxWeight = 5.0;          // Cap to prevent runaway
    
    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════
    public double totalEnergyTransmitted = 0;
    public double peakWeight = 0;

    public Synapse(Priecled source, Priecled target, double initialWeight) {
        this.source = source;
        this.target = target;
        this.weight = Math.max(0.01, initialWeight);
        this.peakWeight = this.weight;
    }

    // ═══════════════════════════════════════════════════════════════════
    // DECAY (The Forgetting Curve)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Apply exponential decay - memories fade without reinforcement
     */
    public void decay() {
        weight *= decayRate;
        
        // Apply φ-resonance: connections at golden ratio distances decay slower
        double distance = source.distanceTo(target);
        double phiDistance = Math.abs(distance - PHI) + Math.abs(distance - PHI * PHI);
        if (phiDistance < 0.5) {
            weight *= 1.001; // φ-resonant connections resist forgetting
        }
    }

    /**
     * Check if this synapse is effectively dead
     */
    public boolean isDead() {
        return weight < 0.01;
    }

    // ═══════════════════════════════════════════════════════════════════
    // STRENGTHEN (Hebbian Learning)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Strengthen this connection - "Cells that fire together, wire together"
     */
    public void strengthen(double amount) {
        weight += amount * strengthenRate * PHI;
        weight = Math.min(weight, maxWeight);
        
        if (weight > peakWeight) {
            peakWeight = weight;
        }
    }

    /**
     * Long-Term Potentiation (LTP) - major strengthening event
     */
    public void potentiate() {
        weight *= PHI;  // Golden ratio boost
        weight = Math.min(weight, maxWeight);
        
        if (weight > peakWeight) {
            peakWeight = weight;
        }
    }

    /**
     * Long-Term Depression (LTD) - major weakening event
     */
    public void depress() {
        weight *= PHI_INV;  // Inverse golden ratio reduction
    }

    // ═══════════════════════════════════════════════════════════════════
    // SIGNAL TRANSMISSION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Fire this synapse - transmit signal from source to target
     * Returns the signal strength transmitted
     */
    public double fire(long currentTick) {
        // Signal strength based on source energy and synapse weight
        double signal = source.energy * weight;
        
        // Apply distance attenuation
        double distance = source.distanceTo(target);
        double attenuation = 1.0 / (1.0 + distance * 0.1);
        signal *= attenuation;
        
        // Record
        lastSignal = signal;
        lastFired = currentTick;
        fireCount++;
        totalEnergyTransmitted += signal;
        
        // Small cost to source
        source.energy -= 0.001 * weight;
        
        // Boost to target
        target.energy += signal * 0.01;
        
        // Activity-dependent plasticity: firing strengthens
        strengthen(0.01);
        
        return signal;
    }

    /**
     * Transmit alignment influence (idea propagation)
     */
    public void transmitAlignment(double influence) {
        // Source's alignment influences target, weighted by synapse strength
        double effectiveInfluence = influence * weight * 0.01;
        target.alignment += (source.alignment - target.alignment) * effectiveInfluence;
        target.alignment = Math.max(0, Math.min(1, target.alignment));
    }

    /**
     * Transmit consciousness (awareness propagation)
     */
    public void transmitConsciousness() {
        double transmission = source.consciousness * weight * 0.001;
        target.consciousness += transmission;
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════════
    
    public double getDistance() {
        return source.distanceTo(target);
    }

    public boolean isActive() {
        return weight > 0.1 && fireCount > 0;
    }

    public boolean isStrong() {
        return weight > 1.0;
    }

    public boolean isWeak() {
        return weight < 0.3;
    }

    /**
     * Get age in ticks since last fired
     */
    public long getIdleTime(long currentTick) {
        return currentTick - lastFired;
    }

    /**
     * Get color for visualization (brighter = stronger)
     */
    public float[] getColorRGB() {
        float intensity = (float) Math.min(1.0, weight / 2.0);
        
        // Blend source and target colors
        float[] srcColor = source.getColorRGB();
        float[] tgtColor = target.getColorRGB();
        
        return new float[]{
            (srcColor[0] + tgtColor[0]) / 2 * intensity,
            (srcColor[1] + tgtColor[1]) / 2 * intensity,
            (srcColor[2] + tgtColor[2]) / 2 * intensity
        };
    }

    @Override
    public String toString() {
        return String.format("Synapse[%d→%d] w=%.3f | fired=%d | dist=%.2f",
            source.id, target.id, weight, fireCount, getDistance());
    }

    /**
     * Create a reverse synapse (bidirectional connection)
     */
    public Synapse createReverse() {
        return new Synapse(target, source, weight * PHI_INV);
    }
}
