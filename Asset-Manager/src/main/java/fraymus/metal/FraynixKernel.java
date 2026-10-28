package fraymus.metal;

import fraymus.metal.hardware.HardwareVGA;
import fraymus.metal.hardware.GenesisBootloader;
import fraymus.metal.silicon.HardwareALU;

/**
 * 👑 FRAYNIX BARE-METAL KERNEL
 * The apex of the architecture. Connects RAM, CPU, ALU, and VGA into a living circuit.
 *
 * Memory Map:
 *   0x0000 - 0x7FFF: Program + Data (32KB)
 *   0x8000 - 0x8FA0: VGA Video Buffer (80x25 chars * 2 bytes = 4000 bytes)
 *   0x8FA0 - 0xFFFF: Free RAM
 *
 * ISA (Instruction Set Architecture):
 *   0x01: MOV EAX, imm8
 *   0x02: MOV EBX, imm16
 *   0x03: STORE [EBX], EAX
 *   0x04: LOAD EAX, [EBX]
 *   0x05: ADD EAX, imm8
 *   0x06: XOR EAX, imm8
 *   0x07: INC EBX
 *   0x08: CMP EBX, imm16
 *   0x09: JNE target8
 *   0x0A: JMP target8
 *   0xFF: HALT
 */
public class FraynixKernel {

    // The Absolute Universe: 64 Kilobytes of physical RAM
    private static final byte[] RAM = new byte[65536];

    // CPU Registers
    private static int EAX = 0;
    private static int EBX = 0;
    private static int EIP = 0; // Instruction Pointer
    private static boolean ZF = false; // Zero Flag

    // CPU state
    private static volatile boolean running = false;
    private static long cycleCount = 0;

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              FRAYNIX BARE-METAL KERNEL                        ║");
        System.out.println("║              64KB RAM | Custom ISA | VGA CRT                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        // 1. Power on the CRT Monitor and wire it to the RAM
        HardwareVGA.powerOn(RAM);

        // 2. Flash the AI Organism into Sector 0
        GenesisBootloader.ignite(RAM);

        // 3. START THE HARDWARE CLOCK (The infinite CPU loop)
        running = true;
        runCPU();
    }

    /**
     * Launch the kernel without VGA (headless mode for CLI testing).
     */
    public static void launchHeadless() {
        System.out.println("   [KERNEL] Headless mode — no VGA display");
        GenesisBootloader.ignite(RAM);
        running = true;

        // Run for a limited number of cycles in headless mode
        int maxCycles = 10000;
        while (running && cycleCount < maxCycles) {
            executeInstruction();
        }
        System.out.println("   [KERNEL] Executed " + cycleCount + " cycles. EAX=" + EAX + " EBX=0x" + Integer.toHexString(EBX));
    }

    /**
     * POKE: Write a byte directly to physical RAM.
     */
    public static void poke(int address, int value) {
        if (address >= 0 && address < RAM.length) {
            RAM[address] = (byte) (value & 0xFF);
        }
    }

    /**
     * PEEK: Read a byte from physical RAM.
     */
    public static int peek(int address) {
        if (address >= 0 && address < RAM.length) {
            return RAM[address] & 0xFF;
        }
        return 0;
    }

    /**
     * Get the raw RAM array (for VGA wiring).
     */
    public static byte[] getRAM() { return RAM; }

    /**
     * Stop the CPU.
     */
    public static void halt() { running = false; }

    public static long getCycleCount() { return cycleCount; }

    private static void runCPU() {
        while (running) {
            executeInstruction();

            // Artificial clock delay so you can watch the organism "think" on the VGA screen
            try { Thread.sleep(1); } catch (Exception e) { break; }
        }
    }

    private static void executeInstruction() {
        if (EIP < 0 || EIP >= RAM.length) {
            System.err.printf("   [!] KERNEL PANIC: EIP OUT OF BOUNDS at 0x%04X\n", EIP);
            running = false;
            return;
        }

        byte opcode = RAM[EIP++];
        cycleCount++;

        // The Fraymus Microcode Interpreter
        switch (opcode) {
            case 0x01: // MOV EAX, imm8
                EAX = RAM[EIP++] & 0xFF;
                break;
            case 0x02: // MOV EBX, imm16
                int high = (RAM[EIP++] & 0xFF) << 8;
                int low = (RAM[EIP++] & 0xFF);
                EBX = high | low;
                break;
            case 0x03: // STORE EAX -> [EBX]
                if (EBX >= 0 && EBX < RAM.length) {
                    RAM[EBX] = (byte) (EAX & 0xFF);
                }
                break;
            case 0x04: // LOAD [EBX] -> EAX
                if (EBX >= 0 && EBX < RAM.length) {
                    EAX = RAM[EBX] & 0xFF;
                }
                break;
            case 0x05: // ADD EAX, imm8 (USING THE HARDWARE ALU)
                byte addVal = RAM[EIP++];
                EAX = HardwareALU.executeAdd(EAX, addVal & 0xFF);
                break;
            case 0x06: // XOR EAX, imm8
                byte xorVal = RAM[EIP++];
                EAX = EAX ^ (xorVal & 0xFF);
                break;
            case 0x07: // INC EBX (USING THE HARDWARE ALU)
                EBX = HardwareALU.executeAdd(EBX, 1);
                break;
            case 0x08: // CMP EBX, imm16
                int cmpHigh = (RAM[EIP++] & 0xFF) << 8;
                int cmpLow = (RAM[EIP++] & 0xFF);
                int target = cmpHigh | cmpLow;
                ZF = (EBX == target);
                break;
            case 0x09: // JNE target8
                byte jneTarget = RAM[EIP++];
                if (!ZF) EIP = jneTarget & 0xFF;
                break;
            case 0x0A: // JMP target8
                EIP = RAM[EIP] & 0xFF;
                break;
            case (byte) 0xFF: // HALT
                System.out.println("   [CPU] HALTED after " + cycleCount + " cycles.");
                running = false;
                break;
            case 0x00: // NOP (empty memory = no-op, prevents crash on uninitialized RAM)
                break;
            default:
                System.err.printf("   [!] KERNEL PANIC: ILLEGAL OPCODE 0x%02X at 0x%04X\n", opcode, EIP - 1);
                running = false;
                break;
        }
    }
}
