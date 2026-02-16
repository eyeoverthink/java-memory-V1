package fraymus.oracle;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * TIMELINE - QUANTUM REALITY STATE
 * 
 * Represents a single timeline in the multiverse.
 * Each timeline has:
 * - Metaphysical metrics (coherence, entropy, complexity)
 * - Physics engine state (CPU registers, qubit state, dimensions)
 * - Memory log of operations
 * 
 * Timelines can be cloned to create parallel realities for simulation.
 */
public class Timeline implements Cloneable {
    
    public double coherence;
    public double entropy;
    public int complexity;

    public Map<String, Integer> cpuRegisters;
    public double[] qubitState;
    public int activeDimensions;
    
    public List<String> log;

    public Timeline(double coh, double ent, int cpx) {
        this.coherence = coh;
        this.entropy = ent;
        this.complexity = cpx;
        this.log = new ArrayList<>();
        
        this.cpuRegisters = new HashMap<>();
        this.cpuRegisters.put("EAX", 0);
        this.cpuRegisters.put("EBX", 0);
        this.cpuRegisters.put("ECX", 10);
        
        this.qubitState = new double[]{1.0, 0.0};
        
        this.activeDimensions = 3; 
    }

    @Override
    public Timeline clone() {
        Timeline t = new Timeline(this.coherence, this.entropy, this.complexity);
        t.cpuRegisters = new HashMap<>(this.cpuRegisters);
        t.qubitState = this.qubitState.clone();
        t.activeDimensions = this.activeDimensions;
        t.log = new ArrayList<>(this.log);
        return t;
    }

    @Override
    public String toString() {
        return String.format("[Coh: %.2f | Ent: %.2f | Dims: %d | EAX: %d]", 
            coherence, entropy, activeDimensions, cpuRegisters.get("EAX"));
    }
}
