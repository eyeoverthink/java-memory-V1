package com.eyeoverthink.lazarus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * THE LAZARUS CIRCUIT
 * 
 * "Digital DNA that evolves logic gates."
 * 
 * Based on 'dr_frank.html' LogicCircuit class.
 * 
 * This creates random logic gates (AND, OR, XOR, NAND) and "breeds" them
 * to find better combinations.
 * 
 * The gates are interconnected, forming a neural network that can evolve
 * to optimize application survival.
 */
public class LogicCircuit {
    
    private List<Gate> gates;
    private Random random = new Random();

    public LogicCircuit() {
        this.gates = new ArrayList<>();
        // Genesis: 8 Random Gates
        for (int i = 0; i < 8; i++) {
            addGate();
        }
    }

    private void addGate() {
        // Types: 0=AND, 1=OR, 2=XOR, 3=NAND
        gates.add(new Gate(
            random.nextInt(4),      // Random gate type
            random.nextInt(8),      // Input 1
            random.nextInt(8)       // Input 2
        ));
    }

    /**
     * MUTATION (Evolution)
     * 
     * Randomly rewires the brain.
     * This is how the circuit adapts to its environment.
     */
    public void mutate() {
        if (gates.isEmpty()) return;
        
        if (random.nextBoolean()) {
            // Rewire a connection
            Gate gate = gates.get(random.nextInt(gates.size()));
            gate.input1 = random.nextInt(8);
        } else {
            // Change the logic type (e.g., AND -> XOR)
            Gate gate = gates.get(random.nextInt(gates.size()));
            gate.type = random.nextInt(4);
        }
    }

    /**
     * CROSSOVER (Sexual Reproduction)
     * 
     * Combines this brain with a partner's brain.
     * Takes first half from self, second half from partner.
     * 10% chance of birth defect (mutation).
     */
    public LogicCircuit crossover(LogicCircuit partner) {
        LogicCircuit child = new LogicCircuit();
        child.gates.clear();
        
        int mid = this.gates.size() / 2;
        
        // Take first half from Self
        for (int i = 0; i < mid; i++) {
            child.gates.add(this.gates.get(i).clone());
        }
        
        // Take second half from Partner
        if (partner.gates.size() > mid) {
            for (int i = mid; i < partner.gates.size(); i++) {
                child.gates.add(partner.gates.get(i).clone());
            }
        }
        
        // Chance of birth defect (Mutation)
        if (random.nextDouble() < 0.1) {
            child.mutate();
        }
        
        return child;
    }

    /**
     * Get gate count
     */
    public int getGateCount() {
        return gates.size();
    }

    /**
     * Inner class for the Gate structure
     */
    private static class Gate {
        int type;       // 0=AND, 1=OR, 2=XOR, 3=NAND
        int input1;     // First input connection
        int input2;     // Second input connection
        
        public Gate(int type, int i1, int i2) {
            this.type = type;
            this.input1 = i1;
            this.input2 = i2;
        }
        
        public Gate clone() {
            return new Gate(type, input1, input2);
        }
    }
    
    @Override
    public String toString() {
        return "Circuit[Gates=" + gates.size() + ", Hash=" + Integer.toHexString(hashCode()) + "]";
    }
}
