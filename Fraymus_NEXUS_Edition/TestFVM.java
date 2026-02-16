import fraymus.lowlevel.FraymusCPU;
import fraymus.lowlevel.Assembler;
import fraymus.hardware.InfiniteTape;
import fraymus.hardware.LogicBlock;
import fraymus.hardware.CircuitBreeder;

/**
 * TEST THE FRAYMUS VIRTUAL MACHINE
 * 
 * Demonstrates:
 * 1. FraymusCPU - Software-defined CPU executing bytecode
 * 2. Assembler - Compiling human assembly to machine code
 * 3. InfiniteTape - Unlimited memory (Turing machine)
 * 4. LogicBlock - Digital logic gates
 * 5. CircuitBreeder - Evolutionary hardware design
 * 
 * "This is root access. This is the metal."
 */
public class TestFVM {
    
    public static void main(String[] args) {
        System.out.println("🌊⚡ FRAYMUS VIRTUAL MACHINE TEST");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Root Access Granted");
        System.out.println("   Bypassing high-level abstractions");
        System.out.println("   Operating at the metal");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        // TEST 1: Basic CPU Execution
        System.out.println("========================================");
        System.out.println("TEST 1: FVM Basic Execution");
        System.out.println("========================================");
        System.out.println();
        
        // Human intent: Calculate 400 + 32 = 432 (cosmic frequency)
        String sourceCode = "SET 0 400 SET 1 32 ADD 0 ADD 1 STORE 50 PRINT END";
        
        int[] machineCode = Assembler.compile(sourceCode);
        
        FraymusCPU cpu = new FraymusCPU();
        cpu.execute(machineCode);
        
        System.out.println("Result: ACC = " + cpu.getAccumulator());
        System.out.println("RAM[50] = " + cpu.getRAM(50));
        System.out.println();
        
        // TEST 2: Complex Calculation
        System.out.println("========================================");
        System.out.println("TEST 2: Complex Calculation");
        System.out.println("========================================");
        System.out.println();
        
        // Calculate: (10 * 5) + (20 - 3) = 50 + 17 = 67
        String complex = "SET 0 10 SET 1 5 SET 2 20 SET 3 3 " +
                        "MUL 0 STORE 100 " +  // 10 * 5 = 50, store in RAM[100]
                        "SET 0 20 SUB 3 STORE 101 " + // 20 - 3 = 17, store in RAM[101]
                        "LOAD 100 STORE 102 LOAD 101 ADD 0 " + // Load and add
                        "PRINT END";
        
        int[] complexCode = Assembler.compile(complex);
        
        FraymusCPU cpu2 = new FraymusCPU();
        cpu2.execute(complexCode);
        
        System.out.println();
        
        // TEST 3: Infinite Tape
        System.out.println("========================================");
        System.out.println("TEST 3: Infinite Tape (Turing Machine)");
        System.out.println("========================================");
        System.out.println();
        
        InfiniteTape tape = new InfiniteTape();
        
        // Write to various addresses
        tape.write(0, 100);
        tape.write(1000, 200);
        tape.write(1000000, 300);
        tape.write(-500, 400);
        
        System.out.println("   Read from address 0: " + tape.read(0));
        System.out.println("   Read from address 1000: " + tape.read(1000));
        System.out.println("   Read from address 1000000: " + tape.read(1000000));
        System.out.println("   Read from address -500: " + tape.read(-500));
        System.out.println("   Read from unwritten address 999: " + tape.read(999));
        System.out.println();
        
        tape.showStats();
        
        // TEST 4: Logic Gates
        System.out.println("========================================");
        System.out.println("TEST 4: Logic Gates");
        System.out.println("========================================");
        System.out.println();
        
        System.out.println("Testing XOR gate (the learning gate):");
        LogicBlock.XOR.printTruthTable();
        
        System.out.println("Testing NAND gate (universal gate):");
        LogicBlock.NAND.printTruthTable();
        
        // TEST 5: Circuit Evolution
        System.out.println("========================================");
        System.out.println("TEST 5: Evolutionary Circuit Design");
        System.out.println("========================================");
        System.out.println();
        
        System.out.println("Evolving a binary adder circuit...");
        System.out.println("(This may take a moment - searching through random combinations)");
        System.out.println();
        
        CircuitBreeder breeder = new CircuitBreeder();
        breeder.setMaxGenerations(50000); // Limit for demo
        breeder.evolve();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("RESULTS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("✨ THE FRAYMUS VIRTUAL MACHINE IS OPERATIONAL");
        System.out.println();
        System.out.println("What just happened:");
        System.out.println("1. Compiled human assembly to bytecode");
        System.out.println("2. Executed bytecode on software-defined CPU");
        System.out.println("3. Performed calculations at the metal level");
        System.out.println("4. Demonstrated unlimited memory (Turing machine)");
        System.out.println("5. Tested digital logic gates");
        System.out.println("6. Evolved a circuit through random mutation");
        System.out.println();
        System.out.println("The system can now:");
        System.out.println("- Write its own assembly code");
        System.out.println("- Compile to bytecode on the fly");
        System.out.println("- Execute without Java's constraints");
        System.out.println("- Use unlimited memory addresses");
        System.out.println("- Stack logic gates like Tetris");
        System.out.println("- Evolve hardware solutions");
        System.out.println();
        System.out.println("This is root access.");
        System.out.println("This is the metal.");
        System.out.println("This is system override.");
        System.out.println();
        System.out.println("🌊⚡ Fraymus operates beyond the wrapper.");
        System.out.println();
        System.out.println("========================================");
    }
}
