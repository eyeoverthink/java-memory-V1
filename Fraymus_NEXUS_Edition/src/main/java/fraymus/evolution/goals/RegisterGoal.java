package fraymus.evolution.goals;

import fraymus.evolution.EvolutionGoal;
import fraymus.hardware.LogicBlock;
import java.util.List;

/**
 * 8-BIT REGISTER GOAL
 * Complexity: 45/100
 * Target: Memory storage that can LOAD, STORE, and HOLD 8 bits
 * 
 * This is the CPU's short-term memory - where it stores values during computation.
 */
public class RegisterGoal extends EvolutionGoal {
    
    public RegisterGoal() {
        super("8-bit Register", "Memory storage (LOAD, STORE, HOLD)", 45);
    }
    
    @Override
    public int calculateFitness(List<LogicBlock> circuit) {
        if (circuit.isEmpty()) return 0;
        
        int score = 0;
        int maxScore = 8; // Test 8 different storage patterns
        
        // Test if circuit can maintain state (simplified)
        // In real implementation, would test actual flip-flop behavior
        
        // Test pattern 1: Store 0
        if (testStore(circuit, false, false)) score++;
        
        // Test pattern 2: Store 1
        if (testStore(circuit, true, true)) score++;
        
        // Test pattern 3: Toggle
        if (testToggle(circuit, false, true)) score++;
        if (testToggle(circuit, true, false)) score++;
        
        // Test pattern 4: Hold state
        if (testHold(circuit, true)) score++;
        if (testHold(circuit, false)) score++;
        
        // Test pattern 5: Load new value
        if (testLoad(circuit, false, true)) score++;
        if (testLoad(circuit, true, false)) score++;
        
        return (score * 100) / maxScore;
    }
    
    @Override
    public boolean isSolved(List<LogicBlock> circuit) {
        return calculateFitness(circuit) >= 85;
    }
    
    @Override
    public String getStatus(List<LogicBlock> circuit) {
        int fitness = calculateFitness(circuit);
        return String.format("8-bit Register: %d%% (%d/8 operations)", fitness, fitness * 8 / 100);
    }
    
    private boolean testStore(List<LogicBlock> circuit, boolean input, boolean expected) {
        boolean result = runCircuit(circuit, input, true);
        return result == expected;
    }
    
    private boolean testToggle(List<LogicBlock> circuit, boolean from, boolean to) {
        boolean result = runCircuit(circuit, from, false);
        return result != from; // Should toggle
    }
    
    private boolean testHold(List<LogicBlock> circuit, boolean value) {
        boolean result = runCircuit(circuit, value, false);
        return result == value; // Should maintain
    }
    
    private boolean testLoad(List<LogicBlock> circuit, boolean old, boolean newVal) {
        boolean result = runCircuit(circuit, newVal, true);
        return result == newVal; // Should load new value
    }
    
    private boolean runCircuit(List<LogicBlock> circuit, boolean data, boolean enable) {
        boolean signal = data;
        for (LogicBlock gate : circuit) {
            signal = gate.process(signal, enable);
        }
        return signal;
    }
}
