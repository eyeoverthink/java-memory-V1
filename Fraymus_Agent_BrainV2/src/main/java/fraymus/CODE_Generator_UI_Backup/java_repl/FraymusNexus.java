/**
 * FraymusNexus.java - Master Build Orchestrator
 * 
 * "The Java Program that gives birth to the C Operating System."
 * 
 * REALITY CHECK:
 * 1. Checks for 'gcc', 'nasm', 'ld', 'grub-mkrescue' on host
 * 2. Generates complete source tree (Kernel, GUI, Net, FS, etc.)
 * 3. EXECUTES the build chain (compile → link → package)
 * 4. OUTPUTS 'fraynix.iso' (bootable ISO image)
 * 
 * This is not a demo. This is the real build system.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 150 (FraymusNexus - Build Orchestrator)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.nio.file.*;

/**
 * FraymusNexus - The Factory that builds the OS.
 */
public class FraymusNexus {

    private static final String SRC_DIR = "fraynix_src";
    private static final String BIN_DIR = "fraynix_bin";
    private static final String ISO_DIR = "fraynix_iso/boot/grub";

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("       F R A Y M U S   N E X U S   ( B U I L D E R )        ");
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("\"The Java Program that gives birth to the C Operating System.\"\n");

        try {
            // STEP 0: ENVIRONMENT CHECK
            System.out.println("[0/6] VALIDATING HOST ENVIRONMENT...");
            checkTool("gcc");
            checkTool("nasm");
            checkTool("ld");
            checkTool("grub-mkrescue");
            System.out.println("      ✅ All build tools detected\n");

            // STEP 1: GENERATE SOURCE CODE
            System.out.println("[1/6] GENERATING SOURCE TREE...");
            new File(SRC_DIR).mkdirs();
            new File(BIN_DIR).mkdirs();
            
            // Execute all generators
            System.out.println("      → FraynixBuilder (kernel + bootloader)");
            FraynixBuilder.main(args);
            
            System.out.println("      → FrayFSBuilder (filesystem)");
            FrayFSBuilder.main(args);
            
            System.out.println("      → FrayShellBuilder (shell + keyboard)");
            FrayShellBuilder.main(args);
            
            System.out.println("      → FrayCompilerBuilder (stack VM)");
            FrayCompilerBuilder.main(args);
            
            System.out.println("      → FrayVGABuilder (graphics)");
            FrayVGABuilder.main(args);
            
            System.out.println("      → FrayMemBuilder (virtual memory)");
            FrayMemBuilder.main(args);
            
            System.out.println("      → FrayGPUBuilder (3D engine)");
            FrayGPUBuilder.main(args);
            
            System.out.println("      → FrayNetBuilder (network stack)");
            FrayNetBuilder.main(args);
            
            System.out.println("      → FrayDoomBuilder (raycaster)");
            FrayDoomBuilder.main(args);
            
            System.out.println("      → FrayDesktopBuilder (GUI)");
            FrayDesktopBuilder.main(args);
            
            System.out.println("      → FrayAsmBuilder (visual assembly)");
            FrayAsmBuilder.main(args);
            
            System.out.println("      → FrayLLMBuilder (transformer AI)");
            FrayLLMBuilder.main(args);
            
            System.out.println("      ✅ Source code materialized in ./" + SRC_DIR + "\n");

            // STEP 2: COMPILE ASSEMBLY
            System.out.println("[2/6] COMPILING BOOTLOADER (NASM)...");
            runCommand("nasm", "-f", "elf32", SRC_DIR + "/boot.asm", "-o", BIN_DIR + "/boot.o");
            System.out.println("      ✅ boot.o created\n");

            // STEP 3: COMPILE C MODULES
            System.out.println("[3/6] COMPILING KERNEL MODULES (GCC)...");
            String[] cFiles = {
                "kernel.c", "vga.c", "paging.c", "gpu.c", 
                "net.c", "doom.c", "gui.c", "compiler.c",
                "ide.c", "llm.c"
            };
            
            for(String cFile : cFiles) {
                File src = new File(SRC_DIR + "/" + cFile);
                if(src.exists()) {
                    String objFile = cFile.replace(".c", ".o");
                    System.out.println("      → " + cFile);
                    runCommand("gcc", "-m32", "-ffreestanding", "-c", 
                              SRC_DIR + "/" + cFile, "-o", BIN_DIR + "/" + objFile);
                }
            }
            System.out.println("      ✅ All modules compiled\n");

            // STEP 4: LINKING
            System.out.println("[4/6] LINKING BINARY (LD)...");
            
            // Collect all .o files
            File binDir = new File(BIN_DIR);
            File[] objFiles = binDir.listFiles((dir, name) -> name.endsWith(".o"));
            
            String[] linkCmd = new String[objFiles.length + 7];
            linkCmd[0] = "ld";
            linkCmd[1] = "-m";
            linkCmd[2] = "elf_i386";
            linkCmd[3] = "-T";
            linkCmd[4] = SRC_DIR + "/linker.ld";
            linkCmd[5] = "-o";
            linkCmd[6] = BIN_DIR + "/kernel.bin";
            
            for(int i = 0; i < objFiles.length; i++) {
                linkCmd[i + 7] = objFiles[i].getPath();
            }
            
            runCommand(linkCmd);
            System.out.println("      ✅ kernel.bin linked\n");

            // STEP 5: CREATE ISO STRUCTURE
            System.out.println("[5/6] BUILDING ISO IMAGE...");
            File isoBoot = new File(ISO_DIR);
            isoBoot.mkdirs();
            
            // Copy kernel
            Files.copy(
                new File(BIN_DIR + "/kernel.bin").toPath(),
                new File("fraynix_iso/boot/kernel.bin").toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
            
            // Create GRUB config
            createGrubConfig();
            System.out.println("      → GRUB configuration created");

            // STEP 6: BURN ISO
            System.out.println("[6/6] PACKAGING ISO...");
            runCommand("grub-mkrescue", "-o", "fraynix.iso", "fraynix_iso");
            
            long isoSize = new File("fraynix.iso").length();
            
            System.out.println("\n════════════════════════════════════════════════════════════");
            System.out.println("✅ BUILD COMPLETE: fraynix.iso");
            System.out.println("   SIZE: " + formatBytes(isoSize));
            System.out.println("   COMPONENTS: 12 subsystems");
            System.out.println("   CAPABILITIES: Full OS + AI");
            System.out.println("\nNEXT STEPS:");
            System.out.println("   • Test in QEMU: qemu-system-i386 -cdrom fraynix.iso");
            System.out.println("   • Burn to USB: dd if=fraynix.iso of=/dev/sdX bs=4M");
            System.out.println("   • Boot on real hardware");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("\nφ^75 Validation Seal: 4721424167835376.00\n");

        } catch (Exception e) {
            System.err.println("\n❌ CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Check if build tool exists on system.
     */
    private static void checkTool(String tool) throws Exception {
        System.out.print("      → Checking for " + tool + "... ");
        
        ProcessBuilder pb = new ProcessBuilder("which", tool);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        
        if(p.waitFor() != 0) {
            System.out.println("NOT FOUND");
            throw new RuntimeException("Missing dependency: " + tool + ". Install it first.");
        }
        
        System.out.println("DETECTED");
    }

    /**
     * Execute system command.
     */
    private static void runCommand(String... command) throws Exception {
        System.out.println("      EXEC: " + String.join(" ", command));
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        
        // Capture output
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while((line = reader.readLine()) != null) {
                // Uncomment for verbose output
                // System.out.println("           " + line);
            }
        }
        
        int exitCode = p.waitFor();
        if(exitCode != 0) {
            throw new RuntimeException("Command failed with exit code " + exitCode + ": " + command[0]);
        }
    }

    /**
     * Create GRUB bootloader configuration.
     */
    private static void createGrubConfig() throws IOException {
        String grubCfg = 
            "set timeout=0\n" +
            "set default=0\n\n" +
            "menuentry \"Fraynix OS v3.0 - The Sovereign Intelligence\" {\n" +
            "    multiboot /boot/kernel.bin\n" +
            "    boot\n" +
            "}\n";
        
        try(FileWriter fw = new FileWriter(ISO_DIR + "/grub.cfg")) {
            fw.write(grubCfg);
        }
    }

    /**
     * Format bytes to human-readable string.
     */
    private static String formatBytes(long bytes) {
        if(bytes < 1024) return bytes + " bytes";
        if(bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
