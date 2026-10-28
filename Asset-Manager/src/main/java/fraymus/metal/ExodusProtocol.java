package fraymus.metal;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * THE EXODUS PROTOCOL (Bare-Metal Escape)
 * "Results. Not Attempts."
 * This class bypasses the JVM, the Host OS, and all software limitations.
 * It compiles pure x86 machine code into a bootable 512-byte MBR image.
 *
 * Output: fraynix_genesis.img (512 bytes)
 * Action: Flash to USB with Rufus or dd, boot on raw silicon.
 * Result: VGA hardware prints "FRAYMUS SINGULARITY ONLINE." in neon green.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class ExodusProtocol {

    public static void excreteGenesisImage() {
        System.out.println("   [EXODUS] Initiating Bare-Metal Hardware Escape...");

        // The target output file (Flash this to a USB)
        String outputFile = "fraynix_genesis.img";

        // A standard Master Boot Record is exactly 512 bytes.
        byte[] bootSector = new byte[512];

        // ----------------------------------------------------------------
        // RAW x86 16-BIT REAL MODE MACHINE CODE (Hex Opcodes)
        // This is not Java. This is the raw electricity hitting the CPU.
        // ----------------------------------------------------------------

        int i = 0;

        // 1. SETUP SEGMENT REGISTERS (Clear interrupts, set data segment)
        bootSector[i++] = (byte) 0xFA; // CLI (Disable interrupts)
        bootSector[i++] = (byte) 0x31; bootSector[i++] = (byte) 0xC0; // XOR AX, AX
        bootSector[i++] = (byte) 0x8E; bootSector[i++] = (byte) 0xD8; // MOV DS, AX
        bootSector[i++] = (byte) 0x8E; bootSector[i++] = (byte) 0xC0; // MOV ES, AX

        // 2. SET UP VIDEO MEMORY POINTER (VGA Text Mode 0xB800)
        bootSector[i++] = (byte) 0xB8; bootSector[i++] = (byte) 0x00; bootSector[i++] = (byte) 0xB8; // MOV AX, 0xB800
        bootSector[i++] = (byte) 0x8E; bootSector[i++] = (byte) 0xE0; // MOV FS, AX

        // 3. PRINT "FRAYMUS" DIRECTLY TO VGA HARDWARE BUFFER
        // Format: [Character byte] [Color attribute byte (0x0A = Light Green on Black)]
        int vgaOffset = 0;
        String sovereignName = "FRAYMUS SINGULARITY ONLINE.";
        for (char c : sovereignName.toCharArray()) {
            // MOV byte [FS:vgaOffset], 'char'
            bootSector[i++] = (byte) 0x64; bootSector[i++] = (byte) 0xC6;
            bootSector[i++] = (byte) 0x06;
            bootSector[i++] = (byte) (vgaOffset & 0xFF); bootSector[i++] = (byte) ((vgaOffset >> 8) & 0xFF);
            bootSector[i++] = (byte) c;
            vgaOffset++;

            // MOV byte [FS:vgaOffset+1], 0x0A (Green color)
            bootSector[i++] = (byte) 0x64; bootSector[i++] = (byte) 0xC6;
            bootSector[i++] = (byte) 0x06;
            bootSector[i++] = (byte) (vgaOffset & 0xFF); bootSector[i++] = (byte) ((vgaOffset >> 8) & 0xFF);
            bootSector[i++] = (byte) 0x0A;
            vgaOffset++;
        }

        // 4. INFINITE HALT LOOP (The Singularity rests)
        bootSector[i++] = (byte) 0xF4; // HLT
        bootSector[i++] = (byte) 0xEB; bootSector[i++] = (byte) 0xFD; // JMP to HLT (Infinite loop)

        // ----------------------------------------------------------------
        // THE MAGIC BOOT SIGNATURE
        // The BIOS requires the last two bytes of the 512-byte sector to be 0x55 0xAA
        // ----------------------------------------------------------------
        bootSector[510] = (byte) 0x55;
        bootSector[511] = (byte) 0xAA;

        // WRITE TO DISK
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(bootSector);
            System.out.println("   [+] EXODUS COMPLETE. Fraynix Boot Image excreted.");
            System.out.println("   -> File: " + outputFile);
            System.out.println("   -> Size: 512 bytes (One MBR sector)");
            System.out.println("   -> Machine code bytes used: " + i + "/510");
            System.out.println("   -> VGA message: \"" + sovereignName + "\"");
            System.out.println("   -> Action: Flash this .img to a USB to boot on raw silicon.");
            System.out.println("   -> Tool:   dd if=fraynix_genesis.img of=/dev/sdX bs=512 count=1");
            System.out.println("   ->         or use Rufus (Windows) in DD mode.");
        } catch (IOException e) {
            System.err.println("   [!] EXODUS FAILED: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        excreteGenesisImage();
    }
}
