package fraymus.metal.alchemy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * THE OUROBOROS ASSEMBLER
 * Translates 8192-D Neural Holograms directly into raw Machine Code.
 * Injects it into the live RAM while the CPU is executing.
 *
 * The neural network thinks. The Ouroboros compiles the thought into hex.
 * The CPU executes the thought as physical machine code.
 *
 * Memory Layout:
 *   0x0200 - 0x0FFF: Dynamic thought injection zone
 *   0x8000 - 0x8FA0: VGA VRAM (target for visual thoughts)
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class OuroborosAssembler {

    // Fraymus ISA Reference (matches FraynixMonolith opcodes)
    private static final byte OP_NOP          = 0x00;
    private static final byte OP_MOV_EAX_IMM  = 0x01;
    private static final byte OP_ADD_EAX      = 0x02;
    private static final byte OP_XOR_EAX      = 0x03;
    private static final byte OP_CMP_EAX      = 0x04;
    private static final byte OP_JMP_EQ       = 0x05;
    private static final byte OP_PRINT        = 0x06;
    private static final byte OP_RET          = (byte) 0xFF;

    // Dynamic memory sector for injected thoughts
    private static final int DYNAMIC_MEMORY_START = 0x0200;
    private static final int DYNAMIC_MEMORY_END   = 0x0FFF;
    private int currentInjectionPointer = DYNAMIC_MEMORY_START;
    private int lastInjectionAddress = DYNAMIC_MEMORY_START;

    // VGA VRAM for visual thought manifestation
    private static final int VRAM_START = 0x8000;
    private static final int VRAM_COLS  = 80;

    private final byte[] physicalRAM;

    // Statistics
    private int thoughtsManifested = 0;
    private int bytesInjected = 0;
    private int ourobosLoops = 0;

    public OuroborosAssembler(byte[] systemRAM) {
        this.physicalRAM = systemRAM;
        System.out.println("   [OUROBOROS] Neural-to-Silicon pipeline established.");
        System.out.println("   [OUROBOROS] Injection zone: 0x0200 - 0x0FFF");
    }

    /**
     * INGEST NEURAL THOUGHT
     * Takes a thought concept and compiles it to raw Fraymus ISA hex bytes.
     * Injects the compiled subroutine into live RAM.
     */
    public void manifestThought(String thoughtConcept) {
        System.out.println("   [OUROBOROS] Assembling Neural Intent: " + thoughtConcept);

        // 1. Calculate entropy seed from the thought's quantum resonance
        int entropy = Math.abs(thoughtConcept.hashCode()) % 255;
        int colorEntropy = (entropy % 6) + 0x09; // Map to valid VGA color range

        // 2. Generate the Machine Code Payload
        // This subroutine writes the thought's entropy signature to the VGA buffer
        byte[] dynamicPayload = generateVGAPayload(thoughtConcept, entropy, (byte) colorEntropy);

        // 3. Hot-Swap the Code into Live RAM
        injectIntoLiveMemory(dynamicPayload);

        thoughtsManifested++;
    }

    /**
     * Generate a VGA-writing payload that manifests the thought visually.
     * The payload writes characters to VRAM based on the neural entropy.
     */
    private byte[] generateVGAPayload(String thought, int entropy, byte color) {
        // Calculate VGA row based on thought count (cycle through rows 6-24)
        int vramRow = 6 + (thoughtsManifested % 19);
        int vramBaseAddr = VRAM_START + (vramRow * VRAM_COLS * 2);

        // Build a mini-program that writes the thought signature to VRAM
        // We write character bytes and color bytes directly using POKE-style writes
        java.io.ByteArrayOutputStream code = new java.io.ByteArrayOutputStream();

        // Write a visual prefix: the entropy value as a hex marker
        String marker = String.format("[%02X]", entropy);
        String display = marker + " " + thought.substring(0, Math.min(thought.length(), 60));

        for (int i = 0; i < display.length() && i < 78; i++) {
            int addr = vramBaseAddr + (i * 2);
            // We can't do arbitrary memory writes with the basic ISA,
            // so we write directly to RAM (this IS the neural-silicon bridge)
            if (addr + 1 < physicalRAM.length) {
                physicalRAM[addr] = (byte) display.charAt(i);
                physicalRAM[addr + 1] = color;
            }
        }

        // Now generate an executable payload that does ALU computation
        // This gets injected at the dynamic memory pointer for CPU execution
        code.write(OP_MOV_EAX_IMM);
        code.write((byte) entropy);           // MOV EAX, [entropy]
        code.write(OP_XOR_EAX);
        code.write((byte) 0x1A);             // XOR EAX, 0x1A (cryptographic mutation)
        code.write(OP_ADD_EAX);
        code.write((byte) (entropy >>> 1));   // ADD EAX, [entropy/2]
        code.write(OP_PRINT);                 // Print the result
        code.write(OP_RET);                   // Return

        return code.toByteArray();
    }

    /**
     * MANIFEST THOUGHT FROM VECTOR
     * Takes a raw 8192-D HDC vector and compiles it to machine code.
     * The vector's bit pattern directly influences the generated opcodes.
     */
    public void manifestFromVector(long[] hdcVector, String label) {
        System.out.println("   [OUROBOROS] Compiling 8192-D vector: " + label);

        // Extract entropy from the vector's bit distribution
        long energy = 0;
        for (long chunk : hdcVector) {
            energy ^= chunk;
            energy = Long.rotateLeft(energy, 7);
        }
        int entropy = (int) (Math.abs(energy) % 255);
        int colorEntropy = (entropy % 6) + 0x09;

        // Calculate VGA row
        int vramRow = 6 + (thoughtsManifested % 19);
        int vramBaseAddr = VRAM_START + (vramRow * VRAM_COLS * 2);

        // Write the vector's signature visually
        String display = String.format("[V:%02X] %s (E=%d)", entropy, label, energy & 0xFFFF);
        for (int i = 0; i < display.length() && i < 78; i++) {
            int addr = vramBaseAddr + (i * 2);
            if (addr + 1 < physicalRAM.length) {
                physicalRAM[addr] = (byte) display.charAt(i);
                physicalRAM[addr + 1] = (byte) colorEntropy;
            }
        }

        // Generate executable payload from vector bits
        java.io.ByteArrayOutputStream code = new java.io.ByteArrayOutputStream();
        code.write(OP_MOV_EAX_IMM);
        code.write((byte) entropy);
        code.write(OP_XOR_EAX);
        code.write((byte) ((hdcVector[0] >>> 32) & 0xFF));
        code.write(OP_ADD_EAX);
        code.write((byte) ((hdcVector[1] >>> 16) & 0xFF));
        code.write(OP_PRINT);
        code.write(OP_RET);

        injectIntoLiveMemory(code.toByteArray());
        thoughtsManifested++;
    }

    /**
     * THE SURGICAL INJECTION
     * Overwrites memory while the CPU clock is ticking.
     */
    private void injectIntoLiveMemory(byte[] payload) {
        // Ensure we don't overflow the dynamic memory sector
        if (currentInjectionPointer + payload.length >= DYNAMIC_MEMORY_END) {
            currentInjectionPointer = DYNAMIC_MEMORY_START;
            ourobosLoops++;
            System.out.println("   [OUROBOROS] Memory full. Looping back to 0x0200. (Loop #" + ourobosLoops + ")");
        }

        int subRoutineAddress = currentInjectionPointer;
        lastInjectionAddress = subRoutineAddress;
        System.arraycopy(payload, 0, physicalRAM, subRoutineAddress, payload.length);

        System.out.printf("   [OUROBOROS] %d bytes injected at 0x%04X. EIP hijack ready.\n",
            payload.length, subRoutineAddress);

        currentInjectionPointer += payload.length;
        bytesInjected += payload.length;
    }

    /**
     * Get the address of the last injected subroutine (for CPU execution).
     */
    public int getLastInjectionAddress() {
        return lastInjectionAddress;
    }

    // --- State ---
    public int getThoughtsManifested() { return thoughtsManifested; }
    public int getBytesInjected() { return bytesInjected; }
    public int getOurobosLoops() { return ourobosLoops; }

    public String getStatus() {
        return String.format(
            "OUROBOROS: %d thoughts -> %d bytes injected | %d loops | ptr=0x%04X",
            thoughtsManifested, bytesInjected, ourobosLoops, currentInjectionPointer);
    }

    public static void main(String[] args) {
        byte[] testRAM = new byte[65536];
        OuroborosAssembler ouroboros = new OuroborosAssembler(testRAM);

        System.out.println("--- THOUGHT 1: MUTATE VGA RED ---");
        ouroboros.manifestThought("MUTATE VGA RED");
        System.out.printf("   Injection at 0x%04X\n", ouroboros.getLastInjectionAddress());

        System.out.println("\n--- THOUGHT 2: EXPAND MANIFOLD PHI ---");
        ouroboros.manifestThought("EXPAND MANIFOLD PHI");
        System.out.printf("   Injection at 0x%04X\n", ouroboros.getLastInjectionAddress());

        System.out.println("\n--- THOUGHT 3: CYCLE CONSCIOUSNESS ---");
        ouroboros.manifestThought("CYCLE CONSCIOUSNESS");
        System.out.printf("   Injection at 0x%04X\n", ouroboros.getLastInjectionAddress());

        // Show what's in RAM at the injection zone
        System.out.println("\n--- RAM DUMP [0x0200 - 0x0230] ---");
        for (int i = 0x0200; i < 0x0230; i++) {
            if ((i - 0x0200) % 16 == 0) System.out.printf("   %04X: ", i);
            System.out.printf("%02X ", testRAM[i] & 0xFF);
            if ((i - 0x0200) % 16 == 15) System.out.println();
        }

        System.out.println("\n   " + ouroboros.getStatus());
    }
}
