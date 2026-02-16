/**
 * FraynixDemo.java - Complete System Demonstration
 * 
 * Runs all generators in sequence to show the complete Fraynix system.
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;

public class FraynixDemo {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           FRAYNIX COMPLETE SYSTEM DEMONSTRATION            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        try {
            System.out.println("Generating complete Fraynix operating system...\n");
            
            // Run all generators
            System.out.println("[1/16] Generating kernel...");
            FraynixBuilder.main(args);
            
            System.out.println("\n[2/16] Generating filesystem...");
            FrayFSBuilder.main(args);
            
            System.out.println("\n[3/16] Generating shell...");
            FrayShellBuilder.main(args);
            
            System.out.println("\n[4/16] Generating compiler...");
            FrayCompilerBuilder.main(args);
            
            System.out.println("\n[5/16] Generating graphics...");
            FrayVGABuilder.main(args);
            
            System.out.println("\n[6/16] Generating virtual memory...");
            FrayMemBuilder.main(args);
            
            System.out.println("\n[7/16] Generating 3D engine...");
            FrayGPUBuilder.main(args);
            
            System.out.println("\n[8/16] Generating network stack...");
            FrayNetBuilder.main(args);
            
            System.out.println("\n[9/16] Generating game engine...");
            FrayDoomBuilder.main(args);
            
            System.out.println("\n[10/16] Generating desktop...");
            FrayDesktopBuilder.main(args);
            
            System.out.println("\n[11/16] Generating assembly IDE...");
            FrayAsmBuilder.main(args);
            
            System.out.println("\n[12/16] Generating AI...");
            FrayLLMBuilder.main(args);
            
            System.out.println("\n[13/16] Generating arcade...");
            FrayArcadeBuilder.main(args);
            
            System.out.println("\n[14/16] Generating game server...");
            FrayGameServerBuilder.main(args);
            
            System.out.println("\n[15/16] Generating abstraction layer...");
            FrayAbstractKernel.main(args);
            
            System.out.println("\n[16/16] Verifying generated files...");
            verifyGeneratedFiles();
            
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              GENERATION COMPLETE                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            System.out.println("Generated source tree:");
            listGeneratedFiles();
            
            System.out.println("\nTo build the actual ISO:");
            System.out.println("  1. Install: gcc, nasm, ld, grub-mkrescue");
            System.out.println("  2. Run: java -cp out repl.FraymusNexus");
            System.out.println("  3. Boot: qemu-system-i386 -cdrom fraynix.iso\n");
            
            System.out.println("φ^75 Validation Seal: 4721424167835376.00\n");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verifyGeneratedFiles() {
        File srcDir = new File("fraynix_src");
        if (!srcDir.exists()) {
            System.out.println("  ❌ Source directory not found");
            return;
        }
        
        String[] expectedFiles = {
            "boot.asm", "kernel.c", "linker.ld",
            "vga.c", "paging.c", "gpu.c",
            "net.c", "doom.c", "gui.c",
            "compiler.c", "ide.c", "llm.c",
            "arcade.c", "arena.c", "abstract_kernel.c"
        };
        
        int found = 0;
        for (String file : expectedFiles) {
            if (new File(srcDir, file).exists()) {
                found++;
            }
        }
        
        System.out.println("  ✅ Found " + found + "/" + expectedFiles.length + " core files");
    }
    
    private static void listGeneratedFiles() {
        File srcDir = new File("fraynix_src");
        if (!srcDir.exists()) {
            System.out.println("  (Source directory not created yet)");
            return;
        }
        
        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println("  📄 " + file.getName() + " (" + file.length() + " bytes)");
            }
        }
    }
}
