package fraymus.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 🏭 FRAYMUS NEXUS (THE FACTORY)
 * "The Java Program that gives birth to the C Operating System."
 *
 * REALITY CHECK:
 * 1. Checks for 'gcc', 'nasm', 'ld' on your host.
 * 2. GENERATES the Source Tree (Kernel, GUI, Net, FS).
 * 3. EXECUTES the build chain.
 * 4. OUTPUTS 'fraynix.iso'.
 *
 * THE 7-TIER MILITARY STACK:
 *   Tier 1: FraymusNexus     (THIS CLASS — The Factory)
 *   Tier 2: FraynixBuilder   (Kernel — Ring 0, 0xb8000 video)
 *   Tier 3: FrayMemBuilder   (Memory — 4GB paging)
 *   Tier 4: FrayFSBuilder    (Storage — Linked-list FS)
 *   Tier 5: FrayVGABuilder   (Vision — Mode 13h VGA)
 *   Tier 6: FrayCompilerBuilder (Compiler — FrayC bytecode)
 *   Tier 7: FrayDoomBuilder  (Simulation — Raycasting engine)
 */
public class FraymusNexus {

    private static final String SRC_DIR = "fraynix_src";
    private static final String BIN_DIR = "fraynix_bin";
    private static final String ISO_DIR = "fraynix_iso/boot/grub";

    // The 7-Tier builder classes (in fraymus.os package)
    private static final String[] TIER_BUILDERS = {
        "fraymus.os.FraynixBuilder",       // Tier 2: Kernel
        "fraymus.os.FrayMemBuilder",        // Tier 3: Memory
        "fraymus.os.FrayFSBuilder",         // Tier 4: Filesystem
        "fraymus.os.FrayVGABuilder",        // Tier 5: Graphics
        "fraymus.os.FrayCompilerBuilder",   // Tier 6: Self-Compiler
        "fraymus.os.FrayDoomBuilder",       // Tier 7: Simulation
        "fraymus.os.FrayShellBuilder",      // Shell
        "fraymus.os.FrayNetBuilder",        // Networking
        "fraymus.os.FrayDesktopBuilder",    // Desktop/Window Manager
    };

    /**
     * Run the full Nexus build: generate → compile → link → ISO.
     * Returns a status string for integration with Main.java.
     */
    public static String build() {
        StringBuilder log = new StringBuilder();
        log.append("════════════════════════════════════════════════════════════\n");
        log.append("   F R A Y M U S   N E X U S   ( B U I L D E R )\n");
        log.append("════════════════════════════════════════════════════════════\n\n");

        try {
            // STEP 1: VALIDATE HOST ENVIRONMENT
            log.append("[PREFLIGHT] Checking host toolchain...\n");
            boolean hasGcc = checkTool("gcc", log);
            boolean hasNasm = checkTool("nasm", log);
            boolean hasLd = checkTool("ld", log);
            boolean hasGrub = checkTool("grub-mkrescue", log);

            if (!hasGcc || !hasNasm || !hasLd) {
                log.append("\n⚠️ MISSING CRITICAL TOOLS. Source generation will proceed.\n");
                log.append("   Install: sudo apt install build-essential nasm xorriso grub-pc-bin\n\n");
            }

            // STEP 2: CREATE DIRECTORIES
            new File(SRC_DIR).mkdirs();
            new File(BIN_DIR).mkdirs();

            // STEP 3: GENERATE SOURCE CODE (The DNA Injection)
            log.append("\n[1/7] GENERATING SOURCE TREE...\n");
            int generated = 0;
            for (String builderClass : TIER_BUILDERS) {
                try {
                    Class<?> clazz = Class.forName(builderClass);
                    java.lang.reflect.Method main = clazz.getMethod("main", String[].class);
                    main.invoke(null, (Object) new String[]{});
                    log.append("   ✅ ").append(builderClass).append("\n");
                    generated++;
                } catch (ClassNotFoundException e) {
                    log.append("   ⚠️ ").append(builderClass).append(" (not on classpath)\n");
                } catch (Exception e) {
                    log.append("   ❌ ").append(builderClass).append(": ").append(e.getMessage()).append("\n");
                }
            }
            log.append("   Generated: ").append(generated).append("/").append(TIER_BUILDERS.length).append(" tiers\n");
            log.append("   Source tree: ./").append(SRC_DIR).append("\n");

            // STEP 4: COMPILE (only if tools available)
            if (hasGcc && hasNasm && hasLd) {
                log.append("\n[2/7] COMPILING BOOTLOADER (NASM)...\n");
                runCommand(log, "nasm", "-f", "elf32", SRC_DIR + "/boot.asm", "-o", BIN_DIR + "/boot.o");

                log.append("\n[3/7] COMPILING KERNEL MODULES (GCC)...\n");
                String[] cFiles = {"kernel", "vga", "gui", "net", "paging", "doom", "shell", "compiler"};
                for (String mod : cFiles) {
                    File src = new File(SRC_DIR + "/" + mod + ".c");
                    if (src.exists()) {
                        runCommand(log, "gcc", "-m32", "-ffreestanding", "-c",
                                src.getPath(), "-o", BIN_DIR + "/" + mod + ".o");
                    }
                }

                log.append("\n[4/7] LINKING BINARY (LD)...\n");
                File linkerScript = new File(SRC_DIR + "/linker.ld");
                if (linkerScript.exists()) {
                    // Collect all .o files
                    File binDir = new File(BIN_DIR);
                    File[] oFiles = binDir.listFiles((d, n) -> n.endsWith(".o"));
                    if (oFiles != null && oFiles.length > 0) {
                        String[] linkCmd = new String[6 + oFiles.length];
                        linkCmd[0] = "ld";
                        linkCmd[1] = "-m";
                        linkCmd[2] = "elf_i386";
                        linkCmd[3] = "-T";
                        linkCmd[4] = linkerScript.getPath();
                        linkCmd[5] = "-o";
                        // Wait, need to restructure
                        String[] linkCmd2 = buildLinkCommand(linkerScript.getPath(), oFiles);
                        runCommand(log, linkCmd2);
                    }
                } else {
                    log.append("   ⚠️ linker.ld not found. Skipping link step.\n");
                }

                // STEP 5: PACKAGE ISO
                if (hasGrub) {
                    log.append("\n[5/7] BUILDING ISO IMAGE...\n");
                    File isoBoot = new File(ISO_DIR);
                    isoBoot.mkdirs();

                    File kernelBin = new File(BIN_DIR + "/kernel.bin");
                    if (kernelBin.exists()) {
                        Files.copy(kernelBin.toPath(),
                                Path.of("fraynix_iso/boot/kernel.bin"),
                                StandardCopyOption.REPLACE_EXISTING);
                    }

                    File systemImg = new File(SRC_DIR + "/system.img");
                    if (systemImg.exists()) {
                        Files.copy(systemImg.toPath(),
                                Path.of("fraynix_iso/boot/system.img"),
                                StandardCopyOption.REPLACE_EXISTING);
                    }

                    createGrubConfig();
                    runCommand(log, "grub-mkrescue", "-o", "fraynix.iso", "fraynix_iso");

                    File iso = new File("fraynix.iso");
                    if (iso.exists()) {
                        log.append("\n✅ BUILD COMPLETE: fraynix.iso\n");
                        log.append("   SIZE: ").append(iso.length()).append(" bytes\n");
                        log.append("   NEXT: qemu-system-i386 -cdrom fraynix.iso\n");
                    }
                } else {
                    log.append("\n⚠️ grub-mkrescue not found. Skipping ISO creation.\n");
                    log.append("   Source + objects are in ./").append(SRC_DIR).append(" and ./").append(BIN_DIR).append("\n");
                }
            } else {
                log.append("\n[SKIP] Compilation skipped (missing toolchain).\n");
                log.append("   Source code generated in ./").append(SRC_DIR).append("\n");
                log.append("   To compile manually:\n");
                log.append("     nasm -f elf32 ").append(SRC_DIR).append("/boot.asm -o ").append(BIN_DIR).append("/boot.o\n");
                log.append("     gcc -m32 -ffreestanding -c ").append(SRC_DIR).append("/kernel.c -o ").append(BIN_DIR).append("/kernel.o\n");
                log.append("     ld -m elf_i386 -T ").append(SRC_DIR).append("/linker.ld -o ").append(BIN_DIR).append("/kernel.bin ").append(BIN_DIR).append("/*.o\n");
            }

        } catch (Exception e) {
            log.append("\n❌ CRITICAL ERROR: ").append(e.getMessage()).append("\n");
        }

        log.append("\n════════════════════════════════════════════════════════════\n");
        return log.toString();
    }

    /**
     * Generate source tree only (no compilation).
     * Safe to run on any platform.
     */
    public static String generateOnly() {
        StringBuilder log = new StringBuilder();
        log.append("🏭 NEXUS: Generating Fraynix source tree...\n");

        new File(SRC_DIR).mkdirs();

        int generated = 0;
        for (String builderClass : TIER_BUILDERS) {
            try {
                Class<?> clazz = Class.forName(builderClass);
                java.lang.reflect.Method main = clazz.getMethod("main", String[].class);
                main.invoke(null, (Object) new String[]{});
                log.append("   ✅ ").append(builderClass).append("\n");
                generated++;
            } catch (ClassNotFoundException e) {
                log.append("   ⚠️ ").append(builderClass).append(" (not on classpath)\n");
            } catch (Exception e) {
                log.append("   ❌ ").append(builderClass).append(": ").append(e.getMessage()).append("\n");
            }
        }

        log.append("Generated: ").append(generated).append("/").append(TIER_BUILDERS.length).append(" tiers\n");
        log.append("Output: ./").append(SRC_DIR).append("\n");
        return log.toString();
    }

    /**
     * List the 7-Tier stack status.
     */
    public static String listTiers() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌─────────────────────────────────────────────────┐\n");
        sb.append("│  FRAYMUS NEXUS: 7-TIER MILITARY STACK           │\n");
        sb.append("├─────────────────────────────────────────────────┤\n");
        sb.append("│  Tier 1: FraymusNexus      [THE FACTORY]        │\n");
        sb.append("│  Tier 2: FraynixBuilder    [THE KERNEL]         │\n");
        sb.append("│  Tier 3: FrayMemBuilder    [THE MEMORY]         │\n");
        sb.append("│  Tier 4: FrayFSBuilder     [THE STORAGE]        │\n");
        sb.append("│  Tier 5: FrayVGABuilder    [THE VISION]         │\n");
        sb.append("│  Tier 6: FrayCompilerBuilder [THE COMPILER]     │\n");
        sb.append("│  Tier 7: FrayDoomBuilder   [THE SIMULATION]     │\n");
        sb.append("├─────────────────────────────────────────────────┤\n");

        int available = 0;
        for (String cls : TIER_BUILDERS) {
            try {
                Class.forName(cls);
                available++;
            } catch (ClassNotFoundException ignored) {}
        }
        sb.append(String.format("│  Available: %d/%d builders on classpath       │%n", available, TIER_BUILDERS.length));

        File srcDir = new File(SRC_DIR);
        if (srcDir.exists()) {
            File[] files = srcDir.listFiles();
            sb.append(String.format("│  Source files: %d in ./%s              │%n",
                    files != null ? files.length : 0, SRC_DIR));
        } else {
            sb.append("│  Source: Not yet generated                      │\n");
        }

        File iso = new File("fraynix.iso");
        if (iso.exists()) {
            sb.append(String.format("│  ISO: fraynix.iso (%d bytes)           │%n", iso.length()));
        } else {
            sb.append("│  ISO: Not yet built                             │\n");
        }

        sb.append("└─────────────────────────────────────────────────┘\n");
        return sb.toString();
    }

    // --- STANDALONE ENTRY POINT ---

    public static void main(String[] args) {
        System.out.println(build());
    }

    // --- UTILITIES ---

    private static boolean checkTool(String tool, StringBuilder log) {
        try {
            String cmd = System.getProperty("os.name").toLowerCase().contains("win") ? "where" : "which";
            Process p = new ProcessBuilder(cmd, tool).redirectErrorStream(true).start();
            boolean found = p.waitFor(5, java.util.concurrent.TimeUnit.SECONDS) && p.exitValue() == 0;
            log.append("   ").append(found ? "✅" : "❌").append(" ").append(tool).append("\n");
            return found;
        } catch (Exception e) {
            log.append("   ❌ ").append(tool).append(" (check failed)\n");
            return false;
        }
    }

    private static void runCommand(StringBuilder log, String... command) {
        try {
            log.append("   EXEC: ").append(String.join(" ", command)).append("\n");
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Capture errors only
                    if (line.toLowerCase().contains("error")) {
                        log.append("      ").append(line).append("\n");
                    }
                }
            }

            if (!p.waitFor(60, java.util.concurrent.TimeUnit.SECONDS)) {
                p.destroyForcibly();
                log.append("      ⚠️ TIMEOUT\n");
            } else if (p.exitValue() != 0) {
                log.append("      ⚠️ Exit code: ").append(p.exitValue()).append("\n");
            }
        } catch (Exception e) {
            log.append("      ❌ ").append(e.getMessage()).append("\n");
        }
    }

    private static String[] buildLinkCommand(String linkerScript, File[] oFiles) {
        // ld -m elf_i386 -T linker.ld -o bin/kernel.bin obj1.o obj2.o ...
        String[] cmd = new String[6 + oFiles.length];
        cmd[0] = "ld";
        cmd[1] = "-m";
        cmd[2] = "elf_i386";
        cmd[3] = "-T";
        cmd[4] = linkerScript;
        cmd[5] = "-o";
        // Need one more slot for output
        String[] cmd2 = new String[7 + oFiles.length];
        System.arraycopy(cmd, 0, cmd2, 0, 6);
        cmd2[6] = BIN_DIR + "/kernel.bin";
        for (int i = 0; i < oFiles.length; i++) {
            cmd2[7 + i] = oFiles[i].getPath();
        }
        return cmd2;
    }

    private static void createGrubConfig() throws IOException {
        String grubCfg =
            "menuentry \"Fraynix OS v3.0 (Military Grade)\" {\n" +
            "   multiboot /boot/kernel.bin\n" +
            "   module /boot/system.img\n" +
            "   boot\n" +
            "}";
        try (FileWriter fw = new FileWriter(ISO_DIR + "/grub.cfg")) {
            fw.write(grubCfg);
        }
    }
}
