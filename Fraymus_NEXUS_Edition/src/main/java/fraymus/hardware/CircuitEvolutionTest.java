package fraymus.hardware;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ISOLATED SUBSYSTEM TEST
 * "Can we evolve a working digital component from pure chaos?"
 * 
 * This test proves the Circuit Breeder can create TRUE working logic
 * without human design - just random mutation and natural selection.
 * 
 * TARGET: Evolve an XOR gate (binary addition)
 * - 0 XOR 0 = 0
 * - 0 XOR 1 = 1
 * - 1 XOR 0 = 1
 * - 1 XOR 1 = 0
 * 
 * METHOD: "Tetris" stacking
 * 1. Randomly add/remove gates
 * 2. Test if circuit behaves like XOR
 * 3. Keep mutations that improve fitness
 * 4. Repeat until perfect
 */
public class CircuitEvolutionTest {

    private List<LogicBlock> circuit = new ArrayList<>();
    private Random random = new Random();
    private int generation = 0;
    private int bestFitness = 0;

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     CIRCUIT EVOLUTION TEST - ISOLATED SUBSYSTEM           ║");
        System.out.println("║     Can chaos create a working digital component?         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        CircuitEvolutionTest test = new CircuitEvolutionTest();
        test.evolve();
    }

    /**
     * THE EVOLUTION LOOP
     * Randomly mutate circuit until it solves XOR
     */
    public void evolve() {
        System.out.println("🧬 STARTING EVOLUTION...");
        System.out.println("   Target: XOR gate (binary addition)");
        System.out.println("   Method: Random mutation + Natural selection");
        System.out.println();
        
        boolean solved = false;
        
        while (!solved && generation < 100000) {
            generation++;
            
            // MUTATE: Randomly modify circuit
            mutate();
            
            // TEST: Calculate fitness (how close to XOR?)
            int fitness = calculateFitness();
            
            // SELECTION: Keep if better
            if (fitness > bestFitness) {
                bestFitness = fitness;
                
                if (generation % 100 == 0 || fitness == 4) {
                    System.out.println("   Gen " + generation + ": Fitness " + fitness + "/4 - " + getCircuitString());
                }
            } else {
                // Revert mutation (it made things worse)
                revertMutation();
            }
            
            // SUCCESS: Perfect XOR behavior
            if (fitness == 4) {
                solved = true;
                printSuccess();
            }
            
            // SAFETY: Reset if stuck
            if (generation % 10000 == 0) {
                System.out.println("   ⚠️  Gen " + generation + ": Resetting circuit (stuck)");
                circuit.clear();
                bestFitness = 0;
            }
        }
        
        if (!solved) {
            System.out.println("\n❌ FAILED: Could not evolve XOR in 100,000 generations");
        }
    }

    /**
     * MUTATION: Randomly add or remove a gate
     */
    private LogicBlock lastMutation = null;
    private int lastMutationIndex = -1;
    
    private void mutate() {
        if (circuit.isEmpty() || random.nextBoolean()) {
            // ADD a random gate
            lastMutation = LogicBlock.spawn();
            lastMutationIndex = circuit.size();
            circuit.add(lastMutation);
        } else {
            // REMOVE a random gate
            lastMutationIndex = random.nextInt(circuit.size());
            lastMutation = circuit.remove(lastMutationIndex);
        }
    }
    
    private void revertMutation() {
        if (lastMutation != null && lastMutationIndex >= 0) {
            if (lastMutationIndex < circuit.size()) {
                // Was a removal - add it back
                circuit.add(lastMutationIndex, lastMutation);
            } else if (lastMutationIndex == circuit.size() && !circuit.isEmpty()) {
                // Was an addition - remove it
                circuit.remove(circuit.size() - 1);
            }
        }
    }

    /**
     * FITNESS: How many XOR test cases does the circuit pass?
     * Perfect score = 4/4
     */
    private int calculateFitness() {
        if (circuit.isEmpty()) return 0;
        
        int score = 0;
        
        // Test case 1: 0 XOR 0 = 0
        if (runCircuit(false, false) == false) score++;
        
        // Test case 2: 0 XOR 1 = 1
        if (runCircuit(false, true) == true) score++;
        
        // Test case 3: 1 XOR 0 = 1
        if (runCircuit(true, false) == true) score++;
        
        // Test case 4: 1 XOR 1 = 0
        if (runCircuit(true, true) == false) score++;
        
        return score;
    }

    /**
     * RUN CIRCUIT: Pass inputs through all gates in sequence
     */
    private boolean runCircuit(boolean inputA, boolean inputB) {
        boolean signal = inputA;
        
        for (LogicBlock gate : circuit) {
            // Chain the gates: output of one becomes input to next
            signal = gate.process(signal, inputB);
        }
        
        return signal;
    }

    /**
     * VISUALIZATION: Show the evolved circuit
     */
    private String getCircuitString() {
        if (circuit.isEmpty()) return "[EMPTY]";
        
        StringBuilder sb = new StringBuilder();
        sb.append("INPUT → ");
        for (LogicBlock gate : circuit) {
            sb.append(gate.getSymbol()).append(" → ");
        }
        sb.append("OUTPUT");
        return sb.toString();
    }

    /**
     * SUCCESS: Print the evolved circuit
     */
    private void printSuccess() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    💡 EUREKA!                             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("   ✅ WORKING XOR GATE EVOLVED IN " + generation + " GENERATIONS");
        System.out.println();
        System.out.println("   CIRCUIT TOPOLOGY:");
        System.out.println("   " + getCircuitString());
        System.out.println();
        System.out.println("   GATE BREAKDOWN:");
        for (int i = 0; i < circuit.size(); i++) {
            System.out.println("      Stage " + (i+1) + ": " + circuit.get(i) + " " + circuit.get(i).getSymbol());
        }
        System.out.println();
        System.out.println("   VERIFICATION (Truth Table):");
        System.out.println("      A | B | OUT | Expected");
        System.out.println("      --|---|-----|----------");
        System.out.println("      0 | 0 | " + (runCircuit(false, false) ? "1" : "0") + "   | 0  " + (runCircuit(false, false) == false ? "✓" : "✗"));
        System.out.println("      0 | 1 | " + (runCircuit(false, true) ? "1" : "0") + "   | 1  " + (runCircuit(false, true) == true ? "✓" : "✗"));
        System.out.println("      1 | 0 | " + (runCircuit(true, false) ? "1" : "0") + "   | 1  " + (runCircuit(true, false) == true ? "✓" : "✗"));
        System.out.println("      1 | 1 | " + (runCircuit(true, true) ? "1" : "0") + "   | 0  " + (runCircuit(true, true) == false ? "✓" : "✗"));
        System.out.println();
        System.out.println("   🎯 PROOF: This circuit was NOT designed by a human.");
        System.out.println("             It emerged from random mutation and selection.");
        System.out.println("             This is TRUE hardware evolution.");
        System.out.println();
    }
}
