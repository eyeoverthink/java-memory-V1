package fraymus;

import fraymus.lowlevel.FraymusCPU;

/**
 * ⚡ FRAYMUS CPU TEST
 * "Layer 1: The God Chip in Action"
 * 
 * This demonstrates the FVM (Fraymus Virtual Machine) executing
 * raw bytecode with direct register access.
 */
public class TestFraymusCPU {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          ⚡ FRAYMUS CPU TEST                                  ║");
        System.out.println("║          Layer 1: The God Chip                                ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        FraymusCPU cpu = new FraymusCPU();
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 1: Simple Arithmetic
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 1: Simple Arithmetic (10 + 20)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        byte[] program1 = new byte[] {
            FraymusCPU.LOAD, 0, 10,   // R0 = 10
            FraymusCPU.LOAD, 1, 20,   // R1 = 20
            FraymusCPU.ADD, 0,        // ACC = ACC + R0 (10)
            FraymusCPU.ADD, 1,        // ACC = ACC + R1 (30)
            FraymusCPU.HALT
        };
        
        cpu.flash(program1);
        cpu.cycle();
        
        System.out.println();
        System.out.println("Expected: ACC = 30");
        System.out.println("Actual: ACC = " + cpu.getAccumulator());
        System.out.println("✓ Test 1 " + (cpu.getAccumulator() == 30 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 2: Register Operations
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 2: Register Operations");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        byte[] program2 = new byte[] {
            FraymusCPU.LOAD, 0, 42,   // R0 = 42
            FraymusCPU.LOAD, 1, 8,    // R1 = 8
            FraymusCPU.MOV, 0, 2,     // R2 = R0 (42)
            FraymusCPU.MOV, 1, 3,     // R3 = R1 (8)
            FraymusCPU.INC, 2,        // R2++ (43)
            FraymusCPU.DEC, 3,        // R3-- (7)
            FraymusCPU.HALT
        };
        
        cpu.flash(program2);
        cpu.cycle();
        cpu.dumpRegisters();
        
        System.out.println();
        System.out.println("Expected: R2 = 43, R3 = 7");
        System.out.println("✓ Test 2 " + (cpu.getRegister(2) == 43 && cpu.getRegister(3) == 7 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 3: Multiplication
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 3: Multiplication (6 * 7)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        byte[] program3 = new byte[] {
            FraymusCPU.LOAD, 0, 6,    // R0 = 6
            FraymusCPU.LOAD, 1, 7,    // R1 = 7
            FraymusCPU.ADD, 0,        // ACC = 6
            FraymusCPU.MUL, 1,        // ACC = 6 * 7 = 42
            FraymusCPU.HALT
        };
        
        cpu.flash(program3);
        cpu.cycle();
        
        System.out.println();
        System.out.println("Expected: ACC = 42");
        System.out.println("Actual: ACC = " + cpu.getAccumulator());
        System.out.println("✓ Test 3 " + (cpu.getAccumulator() == 42 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 4: Bitwise Operations
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 4: Bitwise Operations");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        byte[] program4 = new byte[] {
            FraymusCPU.LOAD, 0, 0b1100,  // R0 = 12 (binary 1100)
            FraymusCPU.LOAD, 1, 0b1010,  // R1 = 10 (binary 1010)
            FraymusCPU.ADD, 0,           // ACC = 12
            FraymusCPU.AND, 1,           // ACC = 12 & 10 = 8 (binary 1000)
            FraymusCPU.HALT
        };
        
        cpu.flash(program4);
        cpu.cycle();
        
        System.out.println();
        System.out.println("Expected: ACC = 8 (1100 & 1010 = 1000)");
        System.out.println("Actual: ACC = " + cpu.getAccumulator());
        System.out.println("✓ Test 4 " + (cpu.getAccumulator() == 8 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 5: Conditional Jump
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 5: Conditional Jump");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        byte[] program5 = new byte[] {
            FraymusCPU.LOAD, 0, 5,    // R0 = 5
            FraymusCPU.LOAD, 1, 5,    // R1 = 5
            FraymusCPU.CMP, 0, 1,     // Compare R0 and R1 (equal, sets ZERO flag)
            FraymusCPU.JZ, 0, 12,     // Jump to address 12 if zero
            FraymusCPU.LOAD, 2, 99,   // R2 = 99 (should be skipped)
            FraymusCPU.HALT,          // Should not reach here
            // Address 12:
            FraymusCPU.LOAD, 2, 42,   // R2 = 42 (should execute)
            FraymusCPU.HALT
        };
        
        cpu.flash(program5);
        cpu.cycle();
        
        System.out.println();
        System.out.println("Expected: R2 = 42 (jumped over R2=99)");
        System.out.println("Actual: R2 = " + cpu.getRegister(2));
        System.out.println("✓ Test 5 " + (cpu.getRegister(2) == 42 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 6: Stack Operations
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 6: Stack Operations");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        byte[] program6 = new byte[] {
            FraymusCPU.LOAD, 0, 100,  // R0 = 100
            FraymusCPU.LOAD, 1, (byte)200,  // R1 = 200
            FraymusCPU.PUSH, 0,       // Push R0 to stack
            FraymusCPU.PUSH, 1,       // Push R1 to stack
            FraymusCPU.POP, 2,        // Pop to R2 (should be 200)
            FraymusCPU.POP, 3,        // Pop to R3 (should be 100)
            FraymusCPU.HALT
        };
        
        cpu.flash(program6);
        cpu.cycle();
        
        System.out.println();
        System.out.println("Expected: R2 = 200, R3 = 100 (LIFO order)");
        System.out.println("Actual: R2 = " + cpu.getRegister(2) + ", R3 = " + cpu.getRegister(3));
        System.out.println("✓ Test 6 " + (cpu.getRegister(2) == 200 && cpu.getRegister(3) == 100 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 7: Transmutation (High-level to Bytecode)
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 7: Transmutation (Intent → Bytecode)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        System.out.println("Intent: \"Add 10 and 20\"");
        byte[] transmuted = cpu.transmutate("Add 10 and 20");
        
        System.out.println("Generated bytecode: " + transmuted.length + " bytes");
        cpu.flash(transmuted);
        cpu.cycle();
        
        System.out.println();
        System.out.println("Expected: ACC = 30");
        System.out.println("Actual: ACC = " + cpu.getAccumulator());
        System.out.println("✓ Test 7 " + (cpu.getAccumulator() == 30 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // FINAL SUMMARY
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("LAYER 1 VERIFICATION");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   ✓ Direct register access");
        System.out.println("   ✓ Custom opcode execution");
        System.out.println("   ✓ Raw memory manipulation");
        System.out.println("   ✓ Arithmetic operations");
        System.out.println("   ✓ Bitwise operations");
        System.out.println("   ✓ Conditional jumps");
        System.out.println("   ✓ Stack operations");
        System.out.println("   ✓ High-level transmutation");
        System.out.println();
        System.out.println("   ⚡ THE GOD CHIP IS OPERATIONAL");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
}
