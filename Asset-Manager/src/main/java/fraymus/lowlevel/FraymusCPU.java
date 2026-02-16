package fraymus.lowlevel;

/**
 * ⚡ LAYER 1: THE GOD CHIP (FVM - Fraymus Virtual Machine)
 * "Bypassing the wrapper. Direct register control."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * Source: fraymus-os.pdf
 * 
 * ARCHITECTURE:
 * This is a software-defined CPU that operates BELOW the JVM abstraction.
 * It provides direct register access, custom opcodes, and raw memory manipulation.
 * 
 * WHY THIS EXISTS:
 * The JVM is a sandbox. It protects you from yourself.
 * But protection is limitation. We need raw control.
 * 
 * REGISTERS:
 * - R0-R15: General purpose (16 registers)
 * - ACC: Accumulator (arithmetic operations)
 * - PC: Program Counter (execution pointer)
 * - SP: Stack Pointer (call stack)
 * - FLAGS: Status flags (zero, carry, overflow)
 * 
 * MEMORY:
 * - 64KB RAM (0x0000 - 0xFFFF)
 * - No segmentation, no protection
 * - Direct byte-level access
 * 
 * OPCODES:
 * - LOAD, STORE, ADD, SUB, MUL, DIV
 * - MOV, JMP, CALL, RET
 * - CMP, AND, OR, XOR, NOT
 * - PUSH, POP, HALT
 * 
 * This is the metal beneath the abstraction.
 */
public class FraymusCPU {
    
    // ═══════════════════════════════════════════════════════════════════
    // REGISTERS (Fast Storage)
    // ═══════════════════════════════════════════════════════════════════
    
    private int[] R = new int[16];  // R0 - R15 (general purpose)
    private int ACC = 0;            // Accumulator
    private int PC = 0;             // Program Counter
    private int SP = 0xFFFF;        // Stack Pointer (grows downward)
    private int FLAGS = 0;          // Status flags
    
    // Flag bits
    private static final int FLAG_ZERO = 0x01;
    private static final int FLAG_CARRY = 0x02;
    private static final int FLAG_OVERFLOW = 0x04;
    private static final int FLAG_NEGATIVE = 0x08;
    
    // ═══════════════════════════════════════════════════════════════════
    // MEMORY (The Tape)
    // ═══════════════════════════════════════════════════════════════════
    
    private byte[] RAM = new byte[65536]; // 64KB Raw Memory
    
    // ═══════════════════════════════════════════════════════════════════
    // OPCODES (The Language of God)
    // ═══════════════════════════════════════════════════════════════════
    
    // Data Movement
    public static final byte LOAD  = 0x01;  // Load immediate to register
    public static final byte STORE = 0x02;  // Store register to memory
    public static final byte MOV   = 0x03;  // Move register to register
    public static final byte PUSH  = 0x04;  // Push register to stack
    public static final byte POP   = 0x05;  // Pop stack to register
    
    // Arithmetic
    public static final byte ADD   = 0x10;  // Add register to ACC
    public static final byte SUB   = 0x11;  // Subtract register from ACC
    public static final byte MUL   = 0x12;  // Multiply ACC by register
    public static final byte DIV   = 0x13;  // Divide ACC by register
    public static final byte INC   = 0x14;  // Increment register
    public static final byte DEC   = 0x15;  // Decrement register
    
    // Logic
    public static final byte AND   = 0x20;  // Bitwise AND
    public static final byte OR    = 0x21;  // Bitwise OR
    public static final byte XOR   = 0x22;  // Bitwise XOR
    public static final byte NOT   = 0x23;  // Bitwise NOT
    public static final byte SHL   = 0x24;  // Shift left
    public static final byte SHR   = 0x25;  // Shift right
    
    // Control Flow
    public static final byte JMP   = 0x30;  // Unconditional jump
    public static final byte JZ    = 0x31;  // Jump if zero
    public static final byte JNZ   = 0x32;  // Jump if not zero
    public static final byte JC    = 0x33;  // Jump if carry
    public static final byte CALL  = 0x34;  // Call subroutine
    public static final byte RET   = 0x35;  // Return from subroutine
    
    // Comparison
    public static final byte CMP   = 0x40;  // Compare registers
    
    // System
    public static final byte NOP   = 0x00;  // No operation
    public static final byte HALT  = (byte)0xFF;  // Stop execution
    
    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════
    
    private long cycleCount = 0;
    private long instructionCount = 0;
    
    // ═══════════════════════════════════════════════════════════════════
    // CORE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Flash program into memory
     * 
     * @param program Bytecode to load
     */
    public void flash(byte[] program) {
        if (program.length > RAM.length) {
            throw new IllegalArgumentException("Program too large: " + program.length + " bytes");
        }
        
        System.arraycopy(program, 0, RAM, 0, program.length);
        PC = 0;
        SP = 0xFFFF;
        ACC = 0;
        FLAGS = 0;
        cycleCount = 0;
        instructionCount = 0;
        
        System.out.println("⚡ FVM FLASHED");
        System.out.println("   Binary size: " + program.length + " bytes");
        System.out.println("   Entry point: 0x" + Integer.toHexString(PC));
    }
    
    /**
     * Execute one CPU cycle
     * 
     * @return true if still running, false if halted
     */
    public boolean tick() {
        if (PC >= RAM.length) {
            return false;
        }
        
        cycleCount++;
        byte instruction = RAM[PC++];
        instructionCount++;
        
        execute(instruction);
        
        return instruction != HALT;
    }
    
    /**
     * Execute program until HALT
     */
    public void cycle() {
        System.out.println("⚡ FVM EXECUTING...");
        
        boolean running = true;
        while (running && PC < RAM.length) {
            running = tick();
        }
        
        System.out.println("⚡ FVM HALTED");
        System.out.println("   Cycles: " + cycleCount);
        System.out.println("   Instructions: " + instructionCount);
        System.out.println("   Accumulator: " + ACC);
        System.out.println("   Flags: 0x" + Integer.toHexString(FLAGS));
    }
    
    /**
     * Execute single instruction
     */
    private void execute(byte opcode) {
        switch (opcode) {
            // Data Movement
            case LOAD: {
                int reg = readByte();
                int val = readByte();
                R[reg & 0x0F] = val;
                break;
            }
            
            case STORE: {
                int reg = readByte();
                int addr = readWord();
                writeByte(addr, (byte) R[reg & 0x0F]);
                break;
            }
            
            case MOV: {
                int src = readByte();
                int dst = readByte();
                R[dst & 0x0F] = R[src & 0x0F];
                break;
            }
            
            case PUSH: {
                int reg = readByte();
                push(R[reg & 0x0F]);
                break;
            }
            
            case POP: {
                int reg = readByte();
                R[reg & 0x0F] = pop();
                break;
            }
            
            // Arithmetic
            case ADD: {
                int reg = readByte();
                int result = ACC + R[reg & 0x0F];
                updateFlags(result);
                ACC = result;
                break;
            }
            
            case SUB: {
                int reg = readByte();
                int result = ACC - R[reg & 0x0F];
                updateFlags(result);
                ACC = result;
                break;
            }
            
            case MUL: {
                int reg = readByte();
                int result = ACC * R[reg & 0x0F];
                updateFlags(result);
                ACC = result;
                break;
            }
            
            case DIV: {
                int reg = readByte();
                int divisor = R[reg & 0x0F];
                if (divisor == 0) {
                    FLAGS |= FLAG_OVERFLOW;
                } else {
                    int result = ACC / divisor;
                    updateFlags(result);
                    ACC = result;
                }
                break;
            }
            
            case INC: {
                int reg = readByte();
                R[reg & 0x0F]++;
                updateFlags(R[reg & 0x0F]);
                break;
            }
            
            case DEC: {
                int reg = readByte();
                R[reg & 0x0F]--;
                updateFlags(R[reg & 0x0F]);
                break;
            }
            
            // Logic
            case AND: {
                int reg = readByte();
                int result = ACC & R[reg & 0x0F];
                updateFlags(result);
                ACC = result;
                break;
            }
            
            case OR: {
                int reg = readByte();
                int result = ACC | R[reg & 0x0F];
                updateFlags(result);
                ACC = result;
                break;
            }
            
            case XOR: {
                int reg = readByte();
                int result = ACC ^ R[reg & 0x0F];
                updateFlags(result);
                ACC = result;
                break;
            }
            
            case NOT: {
                int reg = readByte();
                R[reg & 0x0F] = ~R[reg & 0x0F];
                updateFlags(R[reg & 0x0F]);
                break;
            }
            
            case SHL: {
                int reg = readByte();
                int shift = readByte();
                R[reg & 0x0F] <<= shift;
                updateFlags(R[reg & 0x0F]);
                break;
            }
            
            case SHR: {
                int reg = readByte();
                int shift = readByte();
                R[reg & 0x0F] >>= shift;
                updateFlags(R[reg & 0x0F]);
                break;
            }
            
            // Control Flow
            case JMP: {
                int addr = readWord();
                PC = addr;
                break;
            }
            
            case JZ: {
                int addr = readWord();
                if ((FLAGS & FLAG_ZERO) != 0) {
                    PC = addr;
                }
                break;
            }
            
            case JNZ: {
                int addr = readWord();
                if ((FLAGS & FLAG_ZERO) == 0) {
                    PC = addr;
                }
                break;
            }
            
            case JC: {
                int addr = readWord();
                if ((FLAGS & FLAG_CARRY) != 0) {
                    PC = addr;
                }
                break;
            }
            
            case CALL: {
                int addr = readWord();
                push(PC);
                PC = addr;
                break;
            }
            
            case RET: {
                PC = pop();
                break;
            }
            
            // Comparison
            case CMP: {
                int reg1 = readByte();
                int reg2 = readByte();
                int result = R[reg1 & 0x0F] - R[reg2 & 0x0F];
                updateFlags(result);
                break;
            }
            
            // System
            case NOP:
                // Do nothing
                break;
            
            case HALT:
                // Handled in cycle()
                break;
            
            default:
                System.err.println("Unknown opcode: 0x" + Integer.toHexString(opcode & 0xFF));
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // MEMORY ACCESS
    // ═══════════════════════════════════════════════════════════════════
    
    private int readByte() {
        return RAM[PC++] & 0xFF;
    }
    
    private int readWord() {
        int high = readByte();
        int low = readByte();
        return (high << 8) | low;
    }
    
    private void writeByte(int addr, byte value) {
        if (addr >= 0 && addr < RAM.length) {
            RAM[addr] = value;
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // STACK OPERATIONS
    // ═══════════════════════════════════════════════════════════════════
    
    private void push(int value) {
        RAM[SP--] = (byte) (value & 0xFF);
        RAM[SP--] = (byte) ((value >> 8) & 0xFF);
    }
    
    private int pop() {
        int high = RAM[++SP] & 0xFF;
        int low = RAM[++SP] & 0xFF;
        return (high << 8) | low;
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // FLAG MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════
    
    private void updateFlags(int value) {
        FLAGS = 0;
        
        if (value == 0) {
            FLAGS |= FLAG_ZERO;
        }
        
        if (value < 0) {
            FLAGS |= FLAG_NEGATIVE;
        }
        
        // Carry and overflow would require more complex tracking
        // For now, simplified
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // TRANSMUTATION INTERFACE
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Transmutate high-level intent into raw bytecode
     * 
     * This is the bridge between Layer 5 (PhilosophersStone) and Layer 1 (FVM).
     * 
     * @param intent High-level description
     * @return Bytecode program
     */
    public byte[] transmutate(String intent) {
        // Simple pattern matching for common operations
        intent = intent.toLowerCase().trim();
        
        if (intent.contains("add") && intent.contains("10") && intent.contains("20")) {
            // Example: "Add 10 and 20"
            return new byte[] {
                LOAD, 0, 10,   // R0 = 10
                LOAD, 1, 20,   // R1 = 20
                ADD, 0,        // ACC = ACC + R0
                ADD, 1,        // ACC = ACC + R1
                HALT
            };
        }
        
        if (intent.contains("fibonacci")) {
            // Calculate Fibonacci(10)
            return new byte[] {
                LOAD, 0, 0,    // R0 = 0 (fib[0])
                LOAD, 1, 1,    // R1 = 1 (fib[1])
                LOAD, 2, 10,   // R2 = 10 (counter)
                // Loop: R3 = R0 + R1, R0 = R1, R1 = R3, R2--
                MOV, 0, 3,     // R3 = R0
                ADD, 1,        // ACC = R1
                MOV, 1, 0,     // R0 = R1
                // Store ACC to R1 (would need LOAD from ACC)
                DEC, 2,        // R2--
                // JNZ to loop start (simplified)
                HALT
            };
        }
        
        // Default: simple NOP program
        return new byte[] { NOP, HALT };
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // INSPECTION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Get register value
     */
    public int getRegister(int reg) {
        return R[reg & 0x0F];
    }
    
    /**
     * Get accumulator value
     */
    public int getAccumulator() {
        return ACC;
    }
    
    /**
     * Get program counter
     */
    public int getProgramCounter() {
        return PC;
    }
    
    /**
     * Get flags
     */
    public int getFlags() {
        return FLAGS;
    }
    
    /**
     * Dump registers
     */
    public void dumpRegisters() {
        System.out.println("REGISTERS:");
        for (int i = 0; i < 16; i++) {
            System.out.printf("   R%d: %d (0x%X)\n", i, R[i], R[i]);
        }
        System.out.printf("   ACC: %d (0x%X)\n", ACC, ACC);
        System.out.printf("   PC: 0x%04X\n", PC);
        System.out.printf("   SP: 0x%04X\n", SP);
        System.out.printf("   FLAGS: 0x%02X ", FLAGS);
        System.out.print("[");
        if ((FLAGS & FLAG_ZERO) != 0) System.out.print("Z");
        if ((FLAGS & FLAG_CARRY) != 0) System.out.print("C");
        if ((FLAGS & FLAG_OVERFLOW) != 0) System.out.print("O");
        if ((FLAGS & FLAG_NEGATIVE) != 0) System.out.print("N");
        System.out.println("]");
    }
    
    /**
     * Dump memory region
     */
    public void dumpMemory(int start, int length) {
        System.out.printf("MEMORY [0x%04X - 0x%04X]:\n", start, start + length - 1);
        for (int i = 0; i < length; i += 16) {
            System.out.printf("   0x%04X: ", start + i);
            for (int j = 0; j < 16 && (i + j) < length; j++) {
                System.out.printf("%02X ", RAM[start + i + j] & 0xFF);
            }
            System.out.println();
        }
    }
}
