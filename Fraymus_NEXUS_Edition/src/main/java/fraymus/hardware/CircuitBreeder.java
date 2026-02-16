package fraymus.hardware;

import fraymus.LivingDNA;
import java.util.ArrayList;
import java.util.List;

/**
 * THE CIRCUIT BREEDER
 * "Hardware evolution through random stacking."
 * 
 * This is evolutionary circuit design - "Tetris for transistors."
 * 
 * PROCESS:
 * 1. Randomly stack logic gates (AND, OR, XOR, NAND, NOT)
 * 2. Test if the circuit solves the problem
 * 3. If yes, keep it. If no, mutate and try again.
 * 
 * EVOLUTION:
 * Generation 1: Random chaos
 * Generation 500: Partial solutions emerge
 * Generation 842: EUREKA! Circuit solves the problem
 * 
 * EXAMPLE PROBLEM:
 * Build a circuit that adds two bits (binary addition)
 * Target: 1 + 1 = 0 (with carry 1)
 * Solution: XOR gate (for sum) + AND gate (for carry)
 * 
 * "We don't design circuits. We grow them."
 */
public class CircuitBreeder {

    private InfiniteTape memory;
    private List<LogicBlock> circuit;
    private LivingDNA quantumDNA;
    private int generation = 0;
    private int maxGenerations = 100000;
    private EvolutionLedger ledger;
    private String currentId = "GENESIS";

    public CircuitBreeder() {
        this.memory = new InfiniteTape();
        this.circuit = new ArrayList<>();
        this.quantumDNA = new LivingDNA(); // Harmonic randomness
        this.ledger = new EvolutionLedger();
        
        System.out.println("🔌 CIRCUIT BREEDER ONLINE");
        System.out.println("   Evolutionary hardware design active");
        System.out.println("   Quantum randomness: " + quantumDNA);
        System.out.println("   Target: Build a binary adder");
        System.out.println();
    }

    /**
     * EVOLVE: The "Tetris" loop
     * Try to build a circuit that solves: A + B = Sum
     * 
     * Binary addition truth table (SUM only, not carry):
     * 0 + 0 = 0
     * 0 + 1 = 1
     * 1 + 0 = 1
     * 1 + 1 = 0 (carry 1, but we test sum)
     */
    public void evolve() {
        boolean solved = false;
        generation = 0;

        System.out.println("🔌 CIRCUIT BREEDER ONLINE. TARGET: [FULL ADDER]");
        System.out.println("   Searching for solution...");
        System.out.print("   Progress: ");

        while (!solved && generation < maxGenerations) {
            generation++;
            
            // MUTATE: Add or remove gates
            String parentId = currentId;
            mutate();
            
            // Calculate fitness (how many test cases pass)
            double fitness = calculateFitness();
            
            // RECORD THIS GENERATION
            currentId = "GEN_" + generation;
            ledger.recordGeneration(generation, circuit, fitness, parentId);

            // TEST: Can this circuit add bits?
            if (testCircuit(false, false, false) && 
                testCircuit(true, false, true) && 
                testCircuit(false, true, true) &&
                testCircuit(true, true, false)) {
                
                System.out.println();
                System.out.println();
                System.out.println("💡 EUREKA! EVOLUTION COMPLETE.");
                System.out.println("   Generations: " + generation);
                System.out.println("   Circuit Size: " + circuit.size());
                System.out.println();
                printCircuit();
                
                // Show evolutionary history
                ledger.showEvolutionStats();
                
                solved = true;
            }
            
            // Progress heartbeat
            if (generation % 1000 == 0) {
                System.out.print(".");
            }
        }
        
        if (!solved) {
            System.out.println();
            System.out.println();
            System.out.println("   ❌ FAILED TO EVOLVE after " + maxGenerations + " generations");
            System.out.println("   Try running again...");
        }
    }

    /**
     * Mutate the circuit (add or remove gates)
     * Uses harmonic pulse from LivingDNA for quantum mutation
     */
    private void mutate() {
        // Use harmonic pulse to decide: add or remove?
        double pulse = quantumDNA.pulse(System.nanoTime());
        
        // Evolve DNA for next decision
        quantumDNA.evolve();
        
        // Pulse > 0 = add gate, pulse < 0 = remove gate
        if (pulse > 0 || circuit.isEmpty()) {
            circuit.add(LogicBlock.spawn());
        } else {
            // Use harmonic frequency to select which gate to remove
            double freq = quantumDNA.getHarmonicFrequency();
            int index = (int)(freq % circuit.size());
            circuit.remove(index);
        }
    }
    
    /**
     * Calculate fitness (0.0 to 1.0)
     * How many test cases does this circuit pass?
     */
    private double calculateFitness() {
        int passed = 0;
        int total = 4;
        
        if (testCircuit(false, false, false)) passed++;
        if (testCircuit(true, false, true)) passed++;
        if (testCircuit(false, true, true)) passed++;
        if (testCircuit(true, true, false)) passed++;
        
        return (double) passed / total;
    }

    /**
     * Test if circuit produces expected output for given inputs
     */
    private boolean testCircuit(boolean inA, boolean inB, boolean expectedOut) {
        memory.clear();
        boolean signal = inA; // Start with Input A
        
        // Run the signal through the "Tetris Stack" of gates
        for (LogicBlock gate : circuit) {
            // Each gate combines the current signal with Input B
            signal = gate.process(signal, inB);
        }
        
        return signal == expectedOut;
    }

    /**
     * Run inputs through the circuit
     */
    private boolean runCircuit(boolean inputA, boolean inputB) {
        memory.clear();
        boolean signal = inputA;
        
        for (LogicBlock gate : circuit) {
            signal = gate.process(signal, inputB);
        }
        
        return signal;
    }

    /**
     * Print the evolved circuit
     */
    private void printCircuit() {
        System.out.print("   [MAP]: INPUT ");
        for (LogicBlock gate : circuit) {
            System.out.print("-> " + gate + " ");
        }
        System.out.println("-> OUTPUT");
        System.out.println();
        
        // Show truth table verification
        System.out.println("   📊 VERIFICATION:");
        System.out.println("      A | B | OUT");
        System.out.println("      --|---|----");
        
        boolean[][] testCases = {
            {false, false},
            {false, true},
            {true, false},
            {true, true}
        };
        
        for (boolean[] test : testCases) {
            boolean output = runCircuit(test[0], test[1]);
            System.out.println("      " + (test[0] ? "1" : "0") + " | " + 
                             (test[1] ? "1" : "0") + " | " + 
                             (output ? "1" : "0"));
        }
        System.out.println();
    }

    /**
     * Evolve with custom fitness function
     */
    public void evolveCustom(FitnessFunction fitness) {
        boolean solved = false;
        generation = 0;

        while (!solved && generation < maxGenerations) {
            generation++;
            
            // Mutate
            if (random.nextDouble() < 0.6) {
                circuit.add(LogicBlock.spawn());
            } else if (!circuit.isEmpty()) {
                circuit.remove(random.nextInt(circuit.size()));
            }

            // Test with custom fitness
            if (fitness.test(circuit)) {
                System.out.println("   💡 SOLUTION FOUND at generation " + generation);
                printCircuit();
                solved = true;
            }
            
            if (generation % 10000 == 0) {
                System.out.println("   Gen " + generation + ": Searching...");
            }
        }
    }

    /**
     * Set max generations
     */
    public void setMaxGenerations(int max) {
        this.maxGenerations = max;
    }

    /**
     * Get current generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Get circuit
     */
    public List<LogicBlock> getCircuit() {
        return new ArrayList<>(circuit);
    }

    /**
     * Fitness function interface for custom evolution
     */
    public interface FitnessFunction {
        boolean test(List<LogicBlock> circuit);
    }
}
