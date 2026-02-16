/**
 * FrayFSBuilder.java - File System Creator
 * 
 * "Formats raw energy (bytes) into organized memory (Files)."
 * 
 * STRUCTURE:
 * [ HEADER (64 bytes) ] -> [ DATA ] -> [ HEADER ] -> [ DATA ] ...
 * 
 * HEADER FORMAT:
 * - Magic (4 bytes): "FRAY"
 * - Name (32 bytes): "secret.txt"
 * - Size (4 bytes): 1024
 * - Padding (24 bytes): Reserved
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 141 (FrayFS File System)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * FrayFS Builder - Creates a binary disk image from a folder of files.
 */
public class FrayFSBuilder {

    private static final byte[] MAGIC = "FRAY".getBytes(StandardCharsets.US_ASCII);
    private static final String INPUT_DIR = "fray_memories";
    private static final String OUTPUT_FILE = "fraynix_src/system.img";

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  💾 FRAYFS DISK IMAGE BUILDER                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        System.out.println("\"Formats raw energy (bytes) into organized memory (Files).\"\n");

        try {
            buildFilesystem();
            
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║  ✅ FRAYFS DISK IMAGE CREATED                              ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            System.out.println("Disk image: " + OUTPUT_FILE + "\n");
            System.out.println("To use with Fraynix kernel:");
            System.out.println("  1. Compile kernel (cd fraynix_src && ./compile.sh)");
            System.out.println("  2. Run with disk: qemu-system-i386 -kernel fraynix_kernel -hda system.img\n");
            System.out.println("φ^75 Validation Seal: 4721424167835376.00\n");
            
        } catch (IOException e) {
            System.err.println("❌ FORMAT FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build the filesystem disk image.
     */
    private static void buildFilesystem() throws IOException {
        System.out.println("⚡ FORMATTING DRIVE: FrayFS Partition...\n");
        
        // Create input directory if it doesn't exist
        File folder = new File(INPUT_DIR);
        folder.mkdirs();
        
        // Create sample files if directory is empty
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("📝 Creating sample files in " + INPUT_DIR + "/\n");
            createSampleFiles(folder);
            files = folder.listFiles();
        }

        // Build disk image
        try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
            int fileCount = 0;
            long totalSize = 0;
            
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println("   💾 WRITING: " + file.getName() + 
                                     " [" + file.length() + " bytes]");
                    writeFileEntry(fos, file);
                    fileCount++;
                    totalSize += file.length();
                }
            }
            
            // Write End-of-Disk Marker (empty header)
            fos.write(new byte[64]);
            
            System.out.println("\n📊 Statistics:");
            System.out.println("   Files: " + fileCount);
            System.out.println("   Total Data: " + totalSize + " bytes");
            System.out.println("   Disk Image: " + new File(OUTPUT_FILE).length() + " bytes");
        }
    }

    /**
     * Write a single file entry to the disk image.
     */
    private static void writeFileEntry(FileOutputStream fos, File file) throws IOException {
        // 1. PREPARE HEADER (64 Bytes)
        byte[] header = new byte[64];
        
        // Offset 0-3: Magic "FRAY"
        System.arraycopy(MAGIC, 0, header, 0, MAGIC.length);

        // Offset 4-35: Filename (Truncated to 32 chars)
        byte[] nameBytes = file.getName().getBytes(StandardCharsets.US_ASCII);
        int nameLen = Math.min(nameBytes.length, 32);
        System.arraycopy(nameBytes, 0, header, 4, nameLen);

        // Offset 36-39: File Size (Big-endian integer)
        int size = (int) file.length();
        header[36] = (byte) (size >> 24);
        header[37] = (byte) (size >> 16);
        header[38] = (byte) (size >> 8);
        header[39] = (byte) (size);

        // Offset 40-63: Padding (reserved for future use)
        // Already zeroed by array initialization

        // 2. WRITE HEADER
        fos.write(header);

        // 3. WRITE DATA BODY
        Files.copy(file.toPath(), fos);
    }

    /**
     * Create sample files for demonstration.
     */
    private static void createSampleFiles(File folder) throws IOException {
        // Sample 1: Welcome message
        writeTextFile(new File(folder, "welcome.txt"),
            "FRAYMUS HAS AWAKENED\n" +
            "φ^75 Validation Seal: 4721424167835376.00\n" +
            "Generation: 141 (FrayFS)\n" +
            "Status: OPERATIONAL\n");

        // Sample 2: System info
        writeTextFile(new File(folder, "system.txt"),
            "FRAYNIX OPERATING SYSTEM v1.0\n" +
            "Kernel: Ring 0 Access\n" +
            "File System: FrayFS (Linear Linked-List)\n" +
            "Architecture: x86 (32-bit)\n" +
            "Boot: Multiboot-compliant\n");

        // Sample 3: Code snippet
        writeTextFile(new File(folder, "code.txt"),
            "// Fraynix Kernel Entry Point\n" +
            "void kmain(void) {\n" +
            "    kprint(\"FRAYMUS HAS AWAKENED\");\n" +
            "    read_filesystem(0x200000);\n" +
            "    while(1) { hlt(); }\n" +
            "}\n");
    }

    /**
     * Write text to file.
     */
    private static void writeTextFile(File file, String content) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
    }

    /**
     * Get filesystem statistics.
     */
    public static String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  💾 FRAYFS FILE SYSTEM BUILDER                             ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("\"Formats raw energy (bytes) into organized memory (Files).\"\n\n");
        sb.append("Structure:\n");
        sb.append("  [ HEADER (64 bytes) ] → [ DATA ] → [ HEADER ] → [ DATA ] ...\n\n");
        sb.append("Header Format:\n");
        sb.append("  Offset 0-3:   Magic \"FRAY\"\n");
        sb.append("  Offset 4-35:  Filename (32 bytes)\n");
        sb.append("  Offset 36-39: File size (4 bytes, big-endian)\n");
        sb.append("  Offset 40-63: Padding (24 bytes, reserved)\n\n");
        sb.append("Input: fray_memories/ (folder of files)\n");
        sb.append("Output: fraynix_src/system.img (disk image)\n\n");
        sb.append("Usage:\n");
        sb.append("  1. Place files in fray_memories/\n");
        sb.append("  2. Run FrayFSBuilder\n");
        sb.append("  3. Boot kernel with: qemu-system-i386 -kernel fraynix_kernel -hda system.img\n\n");
        sb.append("φ^75 Validation Seal: 4721424167835376.00\n");
        
        return sb.toString();
    }
}
