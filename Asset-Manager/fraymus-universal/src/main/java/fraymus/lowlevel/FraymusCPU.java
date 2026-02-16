package fraymus.lowlevel;

/**
 * ⚡ LAYER 1: THE GOD CHIP (FVM)
 * "Bypassing the wrapper. Direct register control."
 * Source: fraymus-os.pdf
 */
public class FraymusCPU {

    // REGISTERS (Fast Storage)
    private int[] R = new int[16]; // R0 - R15
    private int ACC = 0;           // Accumulator
    private int PC = 0;            // Program Counter
    
    // MEMORY (The Tape)
    private byte[] RAM = new byte[65536]; // 64KB Raw Memory

    // OPCODES (The Language of God)
    public static final byte LOAD = 0x01;  // Load Value to Reg
    public static final byte ADD  = 0x02;  // Add Reg to ACC
    public static final byte SUB  = 0x03;  // Sub Reg from ACC
    public static final byte MOV  = 0x04;  // Move Reg to Reg
    public static final byte JMP  = 0x05;  // Jump to Address
    public static final byte JZ   = 0x06;  // Jump if ACC == 0
    public static final byte STORE = 0x07; // Store ACC to RAM[addr]
    public static final byte FETCH = 0x08; // Fetch RAM[addr] to ACC
    public static final byte HALT = (byte)0xFF;  // Stop

    public void flash(byte[] program) {
        System.arraycopy(program, 0, RAM, 0, program.length);
        PC = 0;
        ACC = 0;
        for (int i = 0; i < R.length; i++) R[i] = 0;
        System.out.println("⚡ FVM FLASHED. BINARY SIZE: " + program.length + " BYTES");
    }

    public void cycle() {
        boolean running = true;
        long cycles = 0;
        while(running && PC < RAM.length) {
            byte instruction = RAM[PC++];
            cycles++;
            
            switch(instruction) {
                case LOAD: {
                    int reg = RAM[PC++] & 0x0F;
                    int val = RAM[PC++] & 0xFF;
                    R[reg] = val;
                    break;
                }
                case ADD: {
                    int rA = RAM[PC++] & 0x0F;
                    ACC += R[rA];
                    break;
                }
                case SUB: {
                    int rA = RAM[PC++] & 0x0F;
                    ACC -= R[rA];
                    break;
                }
                case MOV: {
                    int src = RAM[PC++] & 0x0F;
                    int dst = RAM[PC++] & 0x0F;
                    R[dst] = R[src];
                    break;
                }
                case JMP: {
                    int addr = (RAM[PC++] & 0xFF) << 8 | (RAM[PC++] & 0xFF);
                    PC = addr;
                    break;
                }
                case JZ: {
                    int addr = (RAM[PC++] & 0xFF) << 8 | (RAM[PC++] & 0xFF);
                    if (ACC == 0) PC = addr;
                    break;
                }
                case STORE: {
                    int addr = (RAM[PC++] & 0xFF) << 8 | (RAM[PC++] & 0xFF);
                    RAM[addr] = (byte)(ACC & 0xFF);
                    break;
                }
                case FETCH: {
                    int addr = (RAM[PC++] & 0xFF) << 8 | (RAM[PC++] & 0xFF);
                    ACC = RAM[addr] & 0xFF;
                    break;
                }
                case HALT:
                    running = false;
                    break;

                case 0x00: // NOP
                    break;

                default:
                    System.out.println("⚠️ FVM: UNKNOWN OPCODE 0x" + 
                        String.format("%02X", instruction & 0xFF) + " at PC=" + (PC - 1));
                    running = false;
                    break;
            }
        }
        System.out.println("⚡ CPU HALT. ACC=" + ACC + " CYCLES=" + cycles);
    }

    // THE "TRANSMUTER" HOOK
    // Allows high-level Java to inject raw OpCodes
    public byte[] transmutate(String highLevelIntent) {
        // Manually assembling Bytecode for "Add 10 and 20"
        return new byte[] {
            LOAD, 0, 10,   // R0 = 10
            LOAD, 1, 20,   // R1 = 20
            ADD, 0,        // ACC = ACC + R0 (10)
            ADD, 1,        // ACC = ACC + R1 (30)
            HALT
        };
    }

    // DIAGNOSTICS
    public int getACC() { return ACC; }
    public int getPC() { return PC; }
    public int getReg(int r) { return R[r & 0x0F]; }
    public byte[] getRAM() { return RAM; }

    public String dumpRegisters() {
        StringBuilder sb = new StringBuilder();
        sb.append("ACC=").append(ACC).append(" PC=").append(PC).append("\n");
        for (int i = 0; i < 16; i++) {
            sb.append(String.format("R%d=%d ", i, R[i]));
            if (i % 8 == 7) sb.append("\n");
        }
        return sb.toString().trim();
    }
}
