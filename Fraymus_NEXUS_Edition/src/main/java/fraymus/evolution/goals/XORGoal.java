package fraymus.evolution.goals;

import fraymus.evolution.EvolutionGoal;
import fraymus.hardware.LogicBlock;
import java.util.List;

/**
 * XOR GATE GOAL
 * Complexity: 5/100
 * Target: Binary addition (half-adder sum bit)
 */
public class XORGoal extends EvolutionGoal {
    
    public XORGoal() {
        super("XOR Gate", "Binary addition (0⊕1=1, 1⊕1=0)", 5);
    }
    
    @Override
    public int calculateFitness(List<LogicBlock> circuit) {
        if (circuit.isEmpty()) return 0;
        
        int score = 0;
        int maxScore = 4;
        
        // Test all 4 cases
        if (runCircuit(circuit, false, false) == false) score++;
        if (runCircuit(circuit, false, true) == true) score++;
        if (runCircuit(circuit, true, false) == true) score++;
        if (runCircuit(circuit, true, true) == false) score++;
        
        return (score * 100) / maxScore;
    }
    
    @Override
    public boolean isSolved(List<LogicBlock> circuit) {
        return calculateFitness(circuit) == 100;
    }
    
    @Override
    public String getStatus(List<LogicBlock> circuit) {
        int fitness = calculateFitness(circuit);
        return String.format("XOR: %d%% (%d/4 test cases)", fitness, fitness * 4 / 100);
    }
    
    private boolean runCircuit(List<LogicBlock> circuit, boolean a, boolean b) {
        boolean signal = a;
        for (LogicBlock gate : circuit) {
            signal = gate.process(signal, b);
        }
        return signal;
    }
}
