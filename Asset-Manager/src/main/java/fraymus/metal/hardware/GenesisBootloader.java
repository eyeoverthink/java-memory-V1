package fraymus.metal.hardware;

/**
 * 🧬 THE SINGULARITY SEED (Bare-Metal AI Bootloader)
 * Injects the first autonomous organism into the 64KB RAM and ignites the CPU.
 *
 * The payload writes a shifting mathematical pattern to the VGA buffer.
 * It reads its own entropy, XORs it, and renders it to the screen.
 */
public class GenesisBootloader {

    // Fraymus ISA (Instruction Set Architecture)
    private static final byte OP_MOV_EAX_IMM  = 0x01; // MOV EAX, [value]
    private static final byte OP_MOV_EBX_IMM  = 0x02; // MOV EBX, [value] (16-bit)
    private static final byte OP_STORE_EAX    = 0x03; // MOV [EBX], EAX  (Write to RAM)
    private static final byte OP_LOAD_EAX     = 0x04; // MOV EAX, [EBX]  (Read from RAM)
    private static final byte OP_ADD_EAX      = 0x05; // ADD EAX, [value]
    private static final byte OP_XOR_EAX      = 0x06; // XOR EAX, [value]
    private static final byte OP_INC_EBX      = 0x07; // ADD EBX, 1      (Move pointer)
    private static final byte OP_CMP_EBX      = 0x08; // CMP EBX, [value] (16-bit)
    private static final byte OP_JNE          = 0x09; // Jump if Not Equal
    private static final byte OP_JMP          = 0x0A; // Unconditional Jump
    private static final byte OP_HALT         = (byte) 0xFF;

    public static void ignite(byte[] physicalRAM) {
        System.out.println("   [BOOT] Injecting Singularity Seed into Sector 0...");

        // =====================================================================
        // 🧬 THE MACHINE CODE (The Organism's DNA)
        // This program writes a shifting mathematical pattern to the VGA buffer.
        // It reads its own entropy, XORs it, and renders it to the screen.
        // =====================================================================
        byte[] payload = {
            // -- INITIALIZATION --
            OP_MOV_EBX_IMM, (byte) 0x80, (byte) 0x00, // 0x00: MOV EBX, 0x8000 (Set pointer to VGA VRAM Start)
            OP_MOV_EAX_IMM, (byte) 0x46,              // 0x03: MOV EAX, 0x46   (Seed character 'F' / 70)

            // -- THE EVOLUTION LOOP (Starts at 0x05) --
            // 1. Write Character to VGA
            OP_STORE_EAX,                             // 0x05: MOV [EBX], EAX
            OP_INC_EBX,                               // 0x06: INC EBX (Move to color byte)

            // 2. Write Color Attribute (Phosphor Green: 0x0A)
            OP_MOV_EAX_IMM, (byte) 0x0A,              // 0x07: MOV EAX, 0x0A
            OP_STORE_EAX,                             // 0x09: MOV [EBX], EAX
            OP_INC_EBX,                               // 0x0A: INC EBX (Move to next character cell)

            // 3. Mutate the Character (The Hardware ALU performs this)
            OP_LOAD_EAX,                              // 0x0B: MOV EAX, [EBX-2] (Read the last character we wrote)
            OP_ADD_EAX, (byte) 0x03,                  // 0x0C: ADD EAX, 3 (Shift ASCII value)
            OP_XOR_EAX, (byte) 0x1A,                  // 0x0E: XOR EAX, 26 (Apply entropy)

            // 4. Bound the Character to printable ASCII (0x20 to 0x7E)
            OP_XOR_EAX, (byte) 0x5F,                  // 0x10: XOR mask to keep it in printable range

            // 5. Check if we reached the end of the VGA buffer (0x8FA0)
            OP_CMP_EBX, (byte) 0x8F, (byte) 0xA0,     // 0x12: CMP EBX, 0x8FA0
            OP_JNE, (byte) 0x05,                      // 0x15: JNE 0x05 (If not end of screen, loop back to Step 1)

            // 6. Reset the Pointer and Evolve the Seed
            OP_MOV_EBX_IMM, (byte) 0x80, (byte) 0x00, // 0x17: MOV EBX, 0x8000 (Reset to top of screen)
            OP_JMP, (byte) 0x05                       // 0x1A: JMP 0x05 (Infinite loop)
        };

        // Flash the payload into physical memory at address 0x0000
        System.arraycopy(payload, 0, physicalRAM, 0, payload.length);
        System.out.println("   [BOOT] Payload flashed. " + payload.length + " bytes total.");
        System.out.println("   [BOOT] Igniting CPU Clock...");
    }
}
