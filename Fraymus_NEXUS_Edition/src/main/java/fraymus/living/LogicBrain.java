package fraymus.living;

import java.util.Random;

/**
 * LOGIC BRAIN - Neural Circuit
 * 
 * Collection of 8 logic gates forming a circuit.
 * Supports sexual reproduction via crossover.
 */
public class LogicBrain {
    
    private LogicGate[] gates;
    private Random random = new Random();
    
    public LogicBrain() {
        gates = new LogicGate[8];
        for (int i = 0; i < 8; i++) {
            gates[i] = new LogicGate();
        }
    }
    
    /**
     * Compute circuit output
     */
    public boolean[] compute(boolean[] inputs) {
        boolean[] outputs = new boolean[8];
        
        for (int i = 0; i < 8; i++) {
            outputs[i] = gates[i].compute(inputs);
        }
        
        return outputs;
    }
    
    /**
     * Mutate circuit (10% chance per gate)
     */
    public void mutate() {
        for (LogicGate gate : gates) {
            if (random.nextDouble() < 0.1) {
                gate.mutate();
            }
        }
    }
    
    /**
     * Sexual reproduction via crossover
     */
    public LogicBrain crossover(LogicBrain partner) {
        LogicBrain child = new LogicBrain();
        
        for (int i = 0; i < 8; i++) {
            if (random.nextBoolean()) {
                child.gates[i] = this.gates[i].clone();
            } else {
                child.gates[i] = partner.gates[i].clone();
            }
        }
        
        child.mutate();
        return child;
    }
    
    /**
     * Clone brain
     */
    public LogicBrain clone() {
        LogicBrain copy = new LogicBrain();
        for (int i = 0; i < 8; i++) {
            copy.gates[i] = this.gates[i].clone();
        }
        return copy;
    }
    
    public LogicGate[] getGates() {
        return gates;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Brain[");
        for (int i = 0; i < gates.length; i++) {
            sb.append(gates[i]);
            if (i < gates.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
