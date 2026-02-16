package fraymus.lowlevel;

import java.util.Arrays;

/**
 * THE FRAYMUS VIRTUAL MACHINE (FVM)
 * "We don't run code. We push bits."
 * 
 * This is a software-defined CPU that executes raw bytecode.
 * It bypasses Java's high-level abstractions and operates at the metal.
 * 
 * ARCHITECTURE:
 * - 4 General Purpose Registers (R0-R3)
 * - 1 Accumulator (ACC) for math results
 * - 1 Program Counter (PC) for instruction pointer
 * - 256 bytes of RAM
 * - Custom instruction set (opcodes)
 * 
 * INSTRUCTION SET:
 * 0x10 MOV  - Move value to register
 * 0x20 ADD  - Add register to accumulator
 * 0x21 SUB  - Subtract register from accumulator
 * 0x30 STR  - Store accumulator to RAM
 * 0x31 LDR  - Load RAM to accumulator
 * 0xFF PRT  - Print accumulator (output)
 * 0x00 HLT  - Halt execution
 * 
 * CYCLE:
 * FETCH → DECODE → EXECUTE
 * 
 * "This is the metal. This is root access."
 */
public class FraymusCPU {

    // --- REGISTERS (The Hands) ---
    private int[] registers = new int[4]; // R0, R1, R2, R3
    private int ACC = 0; // Accumulator (The Math Result)
    private int PC = 0;  // Program Counter (Where are we?)
    private boolean running = false;

    // --- MEMORY (The Canvas) ---
    private int[] RAM = new int[256]; // 256 Bytes of pure storage

    // --- INSTRUCTION SET (The Language) ---
    public static final int OP_MOV  = 0x10; // MOV R[x], [Value]
    public static final int OP_ADD  = 0x20; // ADD R[x] to ACC
    public static final int OP_SUB  = 0x21; // SUB R[x] from ACC
    public static final int OP_MUL  = 0x22; // MUL R[x] with ACC
    public static final int OP_DIV  = 0x23; // DIV ACC by R[x]
    public static final int OP_STR  = 0x30; // STORE ACC -> RAM[Address]
    public static final int OP_LDR  = 0x31; // LOAD RAM[Address] -> ACC
    public static final int OP_CMP  = 0x40; // Compare ACC with R[x]
    public static final int OP_JMP  = 0x50; // Jump to address
    public static final int OP_JZ   = 0x51; // Jump if zero
    public static final int OP_PRT  = 0xFF; // PRINT ACC (Output)
    public static final int OP_HLT  = 0x00; // HALT

    /**
     * THE CYCLE
     * Fetch -> Decode -> Execute
     * 
     * @param program Bytecode array to execute
     */
    public void execute(int[] program) {
        System.out.println("⚡ FVM POWER ON");
        System.out.println("   Loading program into RAM...");
        
        // Load Program into RAM (Von Neumann Architecture)
        System.arraycopy(program, 0, RAM, 0, Math.min(program.length, RAM.length));
        
        System.out.println("   Program size: " + program.length + " bytes");
        System.out.println("   Starting execution...");
        System.out.println();
        
        PC = 0;
        running = true;
        int cycleCount = 0;

        while (running && PC < 256) {
            cycleCount++;
            
            // FETCH
            int instruction = RAM[PC];
            PC++;

            // DECODE & EXECUTE
            switch (instruction) {
                
                case OP_MOV: // MOV R[x], [Value]
                    int regIndex = RAM[PC++];
                    int value = RAM[PC++];
                    if (regIndex >= 0 && regIndex < 4) {
                        registers[regIndex] = value;
                        System.out.println("   [" + cycleCount + "] MOV R" + regIndex + " <- " + value);
                    }
                    break;

                case OP_ADD: // ADD R[x]
                    int rIdxAdd = RAM[PC++];
                    if (rIdxAdd >= 0 && rIdxAdd < 4) {
                        ACC += registers[rIdxAdd];
                        System.out.println("   [" + cycleCount + "] ADD R" + rIdxAdd + " (ACC = " + ACC + ")");
                    }
                    break;
                
                case OP_SUB: // SUB R[x]
                    int rIdxSub = RAM[PC++];
                    if (rIdxSub >= 0 && rIdxSub < 4) {
                        ACC -= registers[rIdxSub];
                        System.out.println("   [" + cycleCount + "] SUB R" + rIdxSub + " (ACC = " + ACC + ")");
                    }
                    break;
                
                case OP_MUL: // MUL R[x]
                    int rIdxMul = RAM[PC++];
                    if (rIdxMul >= 0 && rIdxMul < 4) {
                        ACC *= registers[rIdxMul];
                        System.out.println("   [" + cycleCount + "] MUL R" + rIdxMul + " (ACC = " + ACC + ")");
                    }
                    break;
                
                case OP_DIV: // DIV R[x]
                    int rIdxDiv = RAM[PC++];
                    if (rIdxDiv >= 0 && rIdxDiv < 4 && registers[rIdxDiv] != 0) {
                        ACC /= registers[rIdxDiv];
                        System.out.println("   [" + cycleCount + "] DIV R" + rIdxDiv + " (ACC = " + ACC + ")");
                    }
                    break;

                case OP_STR: // STORE ACC -> RAM[Address]
                    int addr = RAM[PC++];
                    if (addr >= 0 && addr < 256) {
                        RAM[addr] = ACC;
                        System.out.println("   [" + cycleCount + "] STR ACC -> RAM[" + addr + "]");
                    }
                    break;
                
                case OP_LDR: // LOAD RAM[Address] -> ACC
                    int loadAddr = RAM[PC++];
                    if (loadAddr >= 0 && loadAddr < 256) {
                        ACC = RAM[loadAddr];
                        System.out.println("   [" + cycleCount + "] LDR RAM[" + loadAddr + "] -> ACC (" + ACC + ")");
                    }
                    break;

                case OP_PRT: // PRINT
                    System.out.println("   [" + cycleCount + "] 🖥️  OUTPUT: " + ACC);
                    break;

                case OP_HLT: // HALT
                    running = false;
                    System.out.println("   [" + cycleCount + "] 🛑 HALT");
                    break;
                    
                default:
                    if (instruction != 0) {
                        System.out.println("   [" + cycleCount + "] ⚠️  UNKNOWN OPCODE: 0x" + 
                                         String.format("%02X", instruction));
                    }
                    running = false;
            }
            
            // Safety limit
            if (cycleCount > 1000) {
                System.out.println("   ⚠️  CYCLE LIMIT REACHED (infinite loop protection)");
                running = false;
            }
        }
        
        System.out.println();
        System.out.println("⚡ FVM POWER OFF");
        System.out.println("   Total cycles: " + cycleCount);
        System.out.println("   Final ACC: " + ACC);
        System.out.println();
    }
    
    /**
     * Get register value
     */
    public int getRegister(int index) {
        if (index >= 0 && index < 4) {
            return registers[index];
        }
        return 0;
    }
    
    /**
     * Get accumulator value
     */
    public int getAccumulator() {
        return ACC;
    }
    
    /**
     * Get RAM value
     */
    public int getRAM(int address) {
        if (address >= 0 && address < 256) {
            return RAM[address];
        }
        return 0;
    }
    
    /**
     * Dump CPU state
     */
    public void dumpState() {
        System.out.println("CPU STATE:");
        System.out.println("  Registers: R0=" + registers[0] + " R1=" + registers[1] + 
                         " R2=" + registers[2] + " R3=" + registers[3]);
        System.out.println("  ACC: " + ACC);
        System.out.println("  PC: " + PC);
        System.out.println("  Running: " + running);
    }
}
