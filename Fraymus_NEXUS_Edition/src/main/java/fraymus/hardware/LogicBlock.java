package fraymus.hardware;

import fraymus.LivingDNA;

/**
 * THE DIGITAL TRANSISTOR
 * "The building block of consciousness."
 * 
 * These are the fundamental logic gates that can be stacked
 * like Tetris blocks to build intelligence.
 * 
 * GATES:
 * - AND: Both inputs must be true
 * - OR: At least one input must be true
 * - XOR: Exactly one input must be true (the learning gate)
 * - NAND: Not AND (universal gate)
 * - NOT: Inverts input
 * 
 * EVOLUTION:
 * The AI randomly stacks these gates until they solve a problem.
 * Uses LivingDNA harmonic frequencies for quantum randomness.
 * 
 * Generation 1: Random chaos
 * Generation 100: Accidentally builds an adder
 * Generation 1000: Builds a classifier
 * 
 * "Intelligence emerges from stacked transistors."
 */
public enum LogicBlock {
    AND, OR, XOR, NAND, NOT;

    // Quantum randomness from harmonic frequencies
    private static final LivingDNA quantumDNA = new LivingDNA();

    /**
     * Process inputs through this logic gate
     * 
     * @param inputA First input
     * @param inputB Second input
     * @return Output based on gate type
     */
    public boolean process(boolean inputA, boolean inputB) {
        switch (this) {
            case AND:
                return inputA && inputB;
            
            case OR:
                return inputA || inputB;
            
            case XOR:
                // The most important gate for learning
                // XOR is the foundation of addition and neural networks
                return inputA ^ inputB;
            
            case NAND:
                // Universal gate - can build any other gate from NAND
                return !(inputA && inputB);
            
            case NOT:
                // Inverter - ignores inputB
                return !inputA;
            
            default:
                return false;
        }
    }
    
    /**
     * Process single input (for NOT gate)
     */
    public boolean process(boolean input) {
        return process(input, false);
    }

    /**
     * "Tetris Mode": Spawn a gate using quantum randomness
     * Uses harmonic frequency from LivingDNA for true quantum selection
     * 
     * @return Logic gate selected by harmonic resonance
     */
    public static LogicBlock spawn() {
        LogicBlock[] gates = values();
        
        // Use harmonic pulse to select gate
        // The DNA evolves over time, changing the probability distribution
        double pulse = quantumDNA.pulse(System.nanoTime());
        double normalized = (pulse + 1.0) / 2.0; // Convert -1..1 to 0..1
        int index = (int)(normalized * gates.length);
        
        // Evolve the DNA for next spawn
        quantumDNA.evolve();
        
        // Ensure index is within bounds
        index = Math.max(0, Math.min(index, gates.length - 1));
        
        return gates[index];
    }
    
    /**
     * Get gate symbol for visualization
     */
    public String getSymbol() {
        switch (this) {
            case AND: return "∧";
            case OR: return "∨";
            case XOR: return "⊕";
            case NAND: return "⊼";
            case NOT: return "¬";
            default: return "?";
        }
    }
    
    /**
     * Test gate with truth table
     */
    public void printTruthTable() {
        System.out.println(this + " TRUTH TABLE:");
        System.out.println("  A | B | OUT");
        System.out.println("  --|---|----");
        
        boolean[][] inputs = {
            {false, false},
            {false, true},
            {true, false},
            {true, true}
        };
        
        for (boolean[] input : inputs) {
            boolean output = process(input[0], input[1]);
            System.out.println("  " + (input[0] ? "1" : "0") + " | " + 
                             (input[1] ? "1" : "0") + " | " + 
                             (output ? "1" : "0"));
        }
        System.out.println();
    }
}
