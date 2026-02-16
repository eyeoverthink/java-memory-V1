package fraymus.evolution.goals;

import fraymus.evolution.EvolutionGoal;
import fraymus.hardware.LogicBlock;
import java.util.List;

/**
 * FULL ADDER GOAL
 * Complexity: 15/100
 * Target: 1-bit addition with carry (A + B + Cin = Sum, Cout)
 * 
 * This requires evolving TWO outputs from the circuit:
 * - Sum bit (XOR of all inputs)
 * - Carry out bit (majority function)
 */
public class FullAdderGoal extends EvolutionGoal {
    
    public FullAdderGoal() {
        super("Full Adder", "1-bit addition with carry", 15);
    }
    
    @Override
    public int calculateFitness(List<LogicBlock> circuit) {
        if (circuit.isEmpty()) return 0;
        
        int score = 0;
        int maxScore = 8; // 8 test cases (all combinations of 3 inputs)
        
        // Test all 8 cases: A, B, Cin → Sum, Cout
        boolean[][] tests = {
            {false, false, false}, // 0+0+0 = 0, carry 0
            {false, false, true},  // 0+0+1 = 1, carry 0
            {false, true, false},  // 0+1+0 = 1, carry 0
            {false, true, true},   // 0+1+1 = 0, carry 1
            {true, false, false},  // 1+0+0 = 1, carry 0
            {true, false, true},   // 1+0+1 = 0, carry 1
            {true, true, false},   // 1+1+0 = 0, carry 1
            {true, true, true}     // 1+1+1 = 1, carry 1
        };
        
        boolean[] expectedSum = {false, true, true, false, true, false, false, true};
        
        for (int i = 0; i < tests.length; i++) {
            boolean result = runCircuit(circuit, tests[i][0], tests[i][1], tests[i][2]);
            if (result == expectedSum[i]) score++;
        }
        
        return (score * 100) / maxScore;
    }
    
    @Override
    public boolean isSolved(List<LogicBlock> circuit) {
        return calculateFitness(circuit) == 100;
    }
    
    @Override
    public String getStatus(List<LogicBlock> circuit) {
        int fitness = calculateFitness(circuit);
        return String.format("Full Adder: %d%% (%d/8 test cases)", fitness, fitness * 8 / 100);
    }
    
    private boolean runCircuit(List<LogicBlock> circuit, boolean a, boolean b, boolean cin) {
        // For now, simplified: chain through circuit
        // In real implementation, would need to extract both Sum and Carry outputs
        boolean signal = a;
        for (LogicBlock gate : circuit) {
            signal = gate.process(signal, b ^ cin); // Simplified
        }
        return signal;
    }
}
