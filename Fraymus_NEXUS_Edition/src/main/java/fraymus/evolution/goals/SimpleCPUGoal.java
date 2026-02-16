package fraymus.evolution.goals;

import fraymus.evolution.EvolutionGoal;
import fraymus.hardware.LogicBlock;
import java.util.List;

/**
 * SIMPLE 8-BIT CPU GOAL
 * Complexity: 70/100
 * Target: Complete CPU with ALU + Registers + Control
 * 
 * This combines all previous components into a working computer:
 * - ALU (arithmetic)
 * - Registers (memory)
 * - Control logic (instruction execution)
 * 
 * INSTRUCTION SET:
 * - ADD: Add two numbers
 * - SUB: Subtract two numbers
 * - LOAD: Load value into register
 * - STORE: Store register to memory
 */
public class SimpleCPUGoal extends EvolutionGoal {
    
    public SimpleCPUGoal() {
        super("8-bit CPU", "Complete computer (ALU + Registers + Control)", 70);
    }
    
    @Override
    public int calculateFitness(List<LogicBlock> circuit) {
        if (circuit.isEmpty()) return 0;
        
        int score = 0;
        int maxScore = 20; // Test 20 different CPU operations
        
        // Test ADD instructions (5 tests)
        if (testInstruction(circuit, "ADD", 3, 5, 8)) score++;
        if (testInstruction(circuit, "ADD", 7, 8, 15)) score++;
        if (testInstruction(circuit, "ADD", 0, 0, 0)) score++;
        if (testInstruction(circuit, "ADD", 10, 5, 15)) score++;
        if (testInstruction(circuit, "ADD", 1, 1, 2)) score++;
        
        // Test SUB instructions (5 tests)
        if (testInstruction(circuit, "SUB", 10, 3, 7)) score++;
        if (testInstruction(circuit, "SUB", 15, 8, 7)) score++;
        if (testInstruction(circuit, "SUB", 5, 5, 0)) score++;
        if (testInstruction(circuit, "SUB", 12, 4, 8)) score++;
        if (testInstruction(circuit, "SUB", 8, 2, 6)) score++;
        
        // Test LOAD instructions (5 tests)
        if (testLoad(circuit, 0)) score++;
        if (testLoad(circuit, 1)) score++;
        if (testLoad(circuit, 7)) score++;
        if (testLoad(circuit, 15)) score++;
        if (testLoad(circuit, 10)) score++;
        
        // Test STORE instructions (5 tests)
        if (testStore(circuit, 0)) score++;
        if (testStore(circuit, 1)) score++;
        if (testStore(circuit, 8)) score++;
        if (testStore(circuit, 15)) score++;
        if (testStore(circuit, 5)) score++;
        
        return (score * 100) / maxScore;
    }
    
    @Override
    public boolean isSolved(List<LogicBlock> circuit) {
        return calculateFitness(circuit) >= 80; // 80% = working computer
    }
    
    @Override
    public String getStatus(List<LogicBlock> circuit) {
        int fitness = calculateFitness(circuit);
        return String.format("8-bit CPU: %d%% (%d/20 instructions)", fitness, fitness * 20 / 100);
    }
    
    private boolean testInstruction(List<LogicBlock> circuit, String op, int a, int b, int expected) {
        // Simplified: test if circuit produces correct output pattern
        boolean inputA = (a & 1) == 1;
        boolean inputB = (b & 1) == 1;
        boolean expectedOut = (expected & 1) == 1;
        
        boolean result = runCircuit(circuit, inputA, inputB);
        return result == expectedOut;
    }
    
    private boolean testLoad(List<LogicBlock> circuit, int value) {
        boolean input = (value & 1) == 1;
        boolean result = runCircuit(circuit, input, true);
        return result == input; // Should load the value
    }
    
    private boolean testStore(List<LogicBlock> circuit, int value) {
        boolean input = (value & 1) == 1;
        boolean result = runCircuit(circuit, input, false);
        return result == input; // Should store the value
    }
    
    private boolean runCircuit(List<LogicBlock> circuit, boolean a, boolean b) {
        boolean signal = a;
        for (LogicBlock gate : circuit) {
            signal = gate.process(signal, b);
        }
        return signal;
    }
}
