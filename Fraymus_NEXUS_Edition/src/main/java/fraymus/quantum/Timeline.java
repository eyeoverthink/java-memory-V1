package fraymus.quantum;

import fraymus.hardware.LogicBlock;
import java.util.ArrayList;
import java.util.List;

/**
 * TIMELINE
 * Represents one parallel reality in the quantum evolution system.
 * 
 * Each timeline evolves independently with its own mutation strategy,
 * then they collapse to the one with highest phi-resonance.
 */
public class Timeline {
    
    private String name;
    private List<LogicBlock> circuit;
    private double resonance;
    
    public Timeline(String name) {
        this.name = name;
        this.circuit = new ArrayList<>();
        this.resonance = 0.0;
    }
    
    public String getName() {
        return name;
    }
    
    public List<LogicBlock> getCircuit() {
        return circuit;
    }
    
    public void setCircuit(List<LogicBlock> circuit) {
        this.circuit = circuit;
    }
    
    public double getResonance() {
        return resonance;
    }
    
    public void setResonance(double resonance) {
        this.resonance = resonance;
    }
    
    @Override
    public String toString() {
        return String.format("Timeline[%s] Resonance=%.3f Gates=%d", 
                           name, resonance, circuit.size());
    }
}
