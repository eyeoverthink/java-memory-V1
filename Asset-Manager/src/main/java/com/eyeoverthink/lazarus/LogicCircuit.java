package com.eyeoverthink.lazarus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * THE LAZARUS CIRCUIT
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Digital DNA that evolves logic gates."
 * 
 * Based on 'dr_frank.html' LogicCircuit class.
 * 
 * This class creates random logic gates (AND, OR, XOR, NAND)
 * and "breeds" them to find better combinations.
 * 
 * Evolution Mechanics:
 * - MUTATION: Random rewiring of connections
 * - CROSSOVER: Sexual reproduction between circuits
 * - SELECTION: Fitness-based survival
 */
public class LogicCircuit {
    
    private static final String[] GATE_NAMES = {"AND", "OR", "XOR", "NAND"};
    
    private List<Gate> gates;
    private Random random = new Random();
    private int generation = 0;
    private double fitness = 0.0;

    public LogicCircuit() {
        this.gates = new ArrayList<>();
        // Genesis: 8 Random Gates
        for (int i = 0; i < 8; i++) {
            addGate();
        }
    }

    private LogicCircuit(List<Gate> gates) {
        this.gates = gates;
    }

    private void addGate() {
        // Types: 0=AND, 1=OR, 2=XOR, 3=NAND
        gates.add(new Gate(
            random.nextInt(4),
            random.nextInt(8), // Input 1
            random.nextInt(8)  // Input 2
        ));
    }

    /**
     * MUTATION (Evolution)
     * Randomly rewires the brain.
     */
    public void mutate() {
        if (gates.isEmpty()) return;
        
        int mutationType = random.nextInt(4);
        int targetGate = random.nextInt(gates.size());
        
        switch (mutationType) {
            case 0:
                // Rewire input 1
                gates.get(targetGate).input1 = random.nextInt(8);
                break;
            case 1:
                // Rewire input 2
                gates.get(targetGate).input2 = random.nextInt(8);
                break;
            case 2:
                // Change the logic type (e.g., AND -> XOR)
                gates.get(targetGate).type = random.nextInt(4);
                break;
            case 3:
                // Add a new gate (growth)
                if (gates.size() < 16) {
                    addGate();
                }
                break;
        }
    }

    /**
     * CROSSOVER (Sexual Reproduction)
     * Combines this brain with a partner's brain.
     */
    public LogicCircuit crossover(LogicCircuit partner) {
        List<Gate> childGates = new ArrayList<>();
        
        int mid = this.gates.size() / 2;
        
        // Take first half from Self
        for (int i = 0; i < mid && i < this.gates.size(); i++) {
            childGates.add(this.gates.get(i).copy());
        }
        
        // Take second half from Partner
        if (partner.gates.size() > mid) {
            for (int i = mid; i < partner.gates.size(); i++) {
                childGates.add(partner.gates.get(i).copy());
            }
        }
        
        LogicCircuit child = new LogicCircuit(childGates);
        child.generation = Math.max(this.generation, partner.generation) + 1;
        
        // Chance of birth defect (Mutation)
        if (random.nextDouble() < 0.1) {
            child.mutate();
        }
        
        return child;
    }

    /**
     * EVALUATE the circuit against inputs
     * Returns a fitness score based on output stability
     */
    public double evaluate(boolean[] inputs) {
        if (gates.isEmpty()) return 0.0;
        
        boolean[] state = new boolean[16];
        
        // Initialize with inputs
        for (int i = 0; i < Math.min(inputs.length, state.length); i++) {
            state[i] = inputs[i];
        }
        
        // Process each gate
        for (Gate gate : gates) {
            boolean in1 = state[gate.input1 % state.length];
            boolean in2 = state[gate.input2 % state.length];
            boolean out;
            
            switch (gate.type) {
                case 0: out = in1 && in2; break;        // AND
                case 1: out = in1 || in2; break;        // OR
                case 2: out = in1 ^ in2; break;         // XOR
                case 3: out = !(in1 && in2); break;     // NAND
                default: out = false;
            }
            
            // Store output in next available slot
            int outSlot = (gate.input1 + gate.input2) % state.length;
            state[outSlot] = out;
        }
        
        // Fitness = number of true outputs (stability)
        int trueCount = 0;
        for (boolean b : state) if (b) trueCount++;
        
        this.fitness = trueCount / (double) state.length;
        return this.fitness;
    }

    public int getGeneration() {
        return generation;
    }

    public double getFitness() {
        return fitness;
    }

    public int getGateCount() {
        return gates.size();
    }

    public String getGateSignature() {
        StringBuilder sb = new StringBuilder();
        for (Gate g : gates) {
            sb.append(GATE_NAMES[g.type]).append("(").append(g.input1).append(",").append(g.input2).append(")->");
        }
        return sb.toString();
    }

    // Inner class for the Gate structure
    private static class Gate {
        int type, input1, input2;
        
        Gate(int type, int i1, int i2) {
            this.type = type;
            this.input1 = i1;
            this.input2 = i2;
        }
        
        Gate copy() {
            return new Gate(type, input1, input2);
        }
        
        @Override
        public String toString() {
            return GATE_NAMES[type] + "(" + input1 + "," + input2 + ")";
        }
    }
    
    @Override
    public String toString() {
        return "Circuit[Gen=" + generation + ", Gates=" + gates.size() + 
               ", Fitness=" + String.format("%.2f", fitness) + "]";
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   THE LAZARUS CIRCUIT: EVOLVING LOGIC                        ║");
        System.out.println("║   \"Digital DNA that evolves logic gates.\"                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create genesis circuit
        LogicCircuit adam = new LogicCircuit();
        System.out.println("   GENESIS: " + adam);
        System.out.println("   Signature: " + adam.getGateSignature());
        
        // Evaluate
        boolean[] inputs = {true, false, true, false, true, false, true, false};
        double fitness = adam.evaluate(inputs);
        System.out.println("   Fitness: " + String.format("%.2f", fitness));
        
        // Breed generations
        System.out.println();
        System.out.println("   EVOLUTION (10 generations):");
        
        LogicCircuit current = adam;
        for (int gen = 0; gen < 10; gen++) {
            // Self-crossover with mutation
            LogicCircuit child = current.crossover(current);
            child.mutate();
            child.evaluate(inputs);
            
            System.out.println("   Gen " + (gen + 1) + ": " + child);
            current = child;
        }
        
        System.out.println();
        System.out.println("   ✓ Lazarus Circuit demo complete.");
        System.out.println();
    }
}
