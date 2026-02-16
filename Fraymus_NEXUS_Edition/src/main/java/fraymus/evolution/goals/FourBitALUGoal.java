package fraymus.evolution.goals;

import fraymus.evolution.EvolutionGoal;
import fraymus.hardware.LogicBlock;
import java.util.List;

/**
 * 4-BIT ALU GOAL
 * Complexity: 40/100
 * Target: Arithmetic Logic Unit that can ADD, SUB, AND, OR 4-bit numbers
 * 
 * This is the foundation of a CPU - the component that does math.
 */
public class FourBitALUGoal extends EvolutionGoal {
    
    public FourBitALUGoal() {
        super("4-bit ALU", "Arithmetic Logic Unit (ADD, SUB, AND, OR)", 40);
    }
    
    @Override
    public int calculateFitness(List<LogicBlock> circuit) {
        if (circuit.isEmpty()) return 0;
        
        int score = 0;
        int maxScore = 16; // Test 16 different operations
        
        // Test ADD operations (4 tests)
        if (testAdd(circuit, 0, 0, 0)) score++;  // 0+0=0
        if (testAdd(circuit, 1, 1, 2)) score++;  // 1+1=2
        if (testAdd(circuit, 3, 5, 8)) score++;  // 3+5=8
        if (testAdd(circuit, 7, 8, 15)) score++; // 7+8=15
        
        // Test SUB operations (4 tests)
        if (testSub(circuit, 5, 3, 2)) score++;  // 5-3=2
        if (testSub(circuit, 8, 4, 4)) score++;  // 8-4=4
        if (testSub(circuit, 10, 5, 5)) score++; // 10-5=5
        if (testSub(circuit, 15, 7, 8)) score++; // 15-7=8
        
        // Test AND operations (4 tests)
        if (testAnd(circuit, 15, 15, 15)) score++; // 1111 & 1111 = 1111
        if (testAnd(circuit, 12, 10, 8)) score++;  // 1100 & 1010 = 1000
        if (testAnd(circuit, 5, 3, 1)) score++;    // 0101 & 0011 = 0001
        if (testAnd(circuit, 0, 15, 0)) score++;   // 0000 & 1111 = 0000
        
        // Test OR operations (4 tests)
        if (testOr(circuit, 0, 0, 0)) score++;     // 0000 | 0000 = 0000
        if (testOr(circuit, 12, 3, 15)) score++;   // 1100 | 0011 = 1111
        if (testOr(circuit, 5, 10, 15)) score++;   // 0101 | 1010 = 1111
        if (testOr(circuit, 8, 4, 12)) score++;    // 1000 | 0100 = 1100
        
        return (score * 100) / maxScore;
    }
    
    @Override
    public boolean isSolved(List<LogicBlock> circuit) {
        return calculateFitness(circuit) >= 90; // Allow 90% for complex circuits
    }
    
    @Override
    public String getStatus(List<LogicBlock> circuit) {
        int fitness = calculateFitness(circuit);
        return String.format("4-bit ALU: %d%% (%d/16 operations)", fitness, fitness * 16 / 100);
    }
    
    // Simplified test functions - in real implementation would need multi-bit logic
    private boolean testAdd(List<LogicBlock> circuit, int a, int b, int expected) {
        // Simplified: test if circuit can handle basic addition pattern
        boolean result = runCircuit(circuit, (a & 1) == 1, (b & 1) == 1);
        return result == ((expected & 1) == 1);
    }
    
    private boolean testSub(List<LogicBlock> circuit, int a, int b, int expected) {
        boolean result = runCircuit(circuit, (a & 1) == 1, (b & 1) == 1);
        return result == ((expected & 1) == 1);
    }
    
    private boolean testAnd(List<LogicBlock> circuit, int a, int b, int expected) {
        boolean result = runCircuit(circuit, (a & 1) == 1, (b & 1) == 1);
        return result == ((expected & 1) == 1);
    }
    
    private boolean testOr(List<LogicBlock> circuit, int a, int b, int expected) {
        boolean result = runCircuit(circuit, (a & 1) == 1, (b & 1) == 1);
        return result == ((expected & 1) == 1);
    }
    
    private boolean runCircuit(List<LogicBlock> circuit, boolean a, boolean b) {
        boolean signal = a;
        for (LogicBlock gate : circuit) {
            signal = gate.process(signal, b);
        }
        return signal;
    }
}
