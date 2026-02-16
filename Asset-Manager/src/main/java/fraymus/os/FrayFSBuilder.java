package fraymus.os;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * 💾 FRAY-FS BUILDER - Gen 141
 * "Formats raw energy (bytes) into organized memory (Files)."
 * 
 * Creates a virtual disk image (system.img) from a folder of files.
 * The Fraynix kernel can mount and read this filesystem.
 * 
 * STRUCTURE:
 *   [ SUPERBLOCK (512 bytes) ]
 *   [ FILE HEADER (64 bytes) ] [ DATA ] 
 *   [ FILE HEADER (64 bytes) ] [ DATA ]
 *   ...
 *   [ END MARKER (64 bytes of zeros) ]
 * 
 * HEADER FORMAT (64 bytes):
 *   - Magic     (4 bytes):  "FRAY"
 *   - Name      (32 bytes): Filename (null-padded)
 *   - Size      (4 bytes):  File size (big-endian)
 *   - Checksum  (4 bytes):  CRC32 of data
 *   - Flags     (4 bytes):  File attributes
 *   - Timestamp (8 bytes):  Creation time (millis)
 *   - Reserved  (8 bytes):  Future use
 * 
 * "The disk remembers what the RAM forgets."
 */
public class FrayFSBuilder {

    private static final byte[] MAGIC = "FRAY".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] SUPERBLOCK_MAGIC = "FRAYFS01".getBytes(StandardCharsets.US_ASCII);
    private static final int HEADER_SIZE = 64;
    private static final int SUPERBLOCK_SIZE = 512;
    
    // File flags
    public static final int FLAG_READONLY = 0x01;
    public static final int FLAG_HIDDEN = 0x02;
    public static final int FLAG_SYSTEM = 0x04;
    public static final int FLAG_EXECUTABLE = 0x08;
    
    private int fileCount = 0;
    private long totalBytes = 0;

    public static void main(String[] args) {
        String inputDir = args.length > 0 ? args[0] : "fray_memories";
        String outputFile = args.length > 1 ? args[1] : "system.img";

        FrayFSBuilder builder = new FrayFSBuilder();
        builder.build(inputDir, outputFile);
    }

    public void build(String inputDir, String outputFile) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  💾 FRAY-FS BUILDER - Gen 141                                 ║");
        System.out.println("║  Virtual Disk Formatter                                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("⚡ INPUT:  " + inputDir);
        System.out.println("⚡ OUTPUT: " + outputFile);
        System.out.println();

        File folder = new File(inputDir);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("📁 Created input directory: " + inputDir);
            System.out.println("   Place files here and run again.");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            // 1. Write Superblock
            writeSuperblock(bos, folder);
            
            // 2. Write all files recursively
            writeDirectory(bos, folder, "");
            
            // 3. Write End-of-Disk marker
            bos.write(new byte[HEADER_SIZE]);
            
            bos.flush();
            
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println(String.format("║  ✅ FILESYSTEM BUILT                                          ║"));
            System.out.println(String.format("║     Files:      %4d                                          ║", fileCount));
            System.out.println(String.format("║     Total size: %s                                     ║", formatSize(totalBytes)));
            System.out.println(String.format("║     Output:     %s", padRight(outputFile, 40) + "║"));
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        } catch (IOException e) {
            System.err.println("❌ FORMAT FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeSuperblock(OutputStream os, File rootDir) throws IOException {
        byte[] superblock = new byte[SUPERBLOCK_SIZE];
        
        // Offset 0: Magic "FRAYFS01"
        System.arraycopy(SUPERBLOCK_MAGIC, 0, superblock, 0, SUPERBLOCK_MAGIC.length);
        
        // Offset 8: Version (1.0)
        superblock[8] = 1;
        superblock[9] = 0;
        
        // Offset 10: Block size (64 for headers)
        writeInt(superblock, 10, HEADER_SIZE);
        
        // Offset 14: Superblock size
        writeInt(superblock, 14, SUPERBLOCK_SIZE);
        
        // Offset 18: Creation timestamp
        writeLong(superblock, 18, System.currentTimeMillis());
        
        // Offset 26: Volume name "FRAYNIX"
        byte[] volumeName = "FRAYNIX".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(volumeName, 0, superblock, 26, volumeName.length);
        
        // Offset 58: φ signature
        writeLong(superblock, 58, Double.doubleToLongBits(1.6180339887));
        
        os.write(superblock);
        System.out.println("📀 SUPERBLOCK written (512 bytes)");
    }

    private void writeDirectory(OutputStream os, File dir, String prefix) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        // Sort for deterministic output
        Arrays.sort(files, Comparator.comparing(File::getName));
        
        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively process subdirectories
                String newPrefix = prefix.isEmpty() ? file.getName() : prefix + "/" + file.getName();
                writeDirectory(os, file, newPrefix);
            } else if (file.isFile()) {
                String fullName = prefix.isEmpty() ? file.getName() : prefix + "/" + file.getName();
                writeFileEntry(os, file, fullName);
            }
        }
    }

    private void writeFileEntry(OutputStream os, File file, String name) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        
        System.out.println("   💾 " + name + " [" + formatSize(data.length) + "]");
        fileCount++;
        totalBytes += data.length;
        
        // 1. PREPARE HEADER (64 Bytes)
        byte[] header = new byte[HEADER_SIZE];
        
        // Offset 0-3: Magic "FRAY"
        System.arraycopy(MAGIC, 0, header, 0, MAGIC.length);

        // Offset 4-35: Filename (truncated to 32 chars, null-padded)
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        int nameLen = Math.min(nameBytes.length, 32);
        System.arraycopy(nameBytes, 0, header, 4, nameLen);

        // Offset 36-39: File Size (big-endian int)
        writeInt(header, 36, data.length);

        // Offset 40-43: Checksum (simple sum)
        int checksum = computeChecksum(data);
        writeInt(header, 40, checksum);

        // Offset 44-47: Flags
        int flags = 0;
        if (!file.canWrite()) flags |= FLAG_READONLY;
        if (file.isHidden()) flags |= FLAG_HIDDEN;
        if (file.getName().endsWith(".bin") || file.getName().endsWith(".exe")) flags |= FLAG_EXECUTABLE;
        writeInt(header, 44, flags);

        // Offset 48-55: Timestamp
        writeLong(header, 48, file.lastModified());

        // Offset 56-63: Reserved
        
        // 2. WRITE HEADER
        os.write(header);

        // 3. WRITE DATA BODY
        os.write(data);
    }

    private int computeChecksum(byte[] data) {
        int sum = 0;
        for (byte b : data) {
            sum = (sum + (b & 0xFF)) & 0xFFFFFFFF;
        }
        return sum;
    }

    private void writeInt(byte[] buf, int offset, int value) {
        buf[offset] = (byte) (value >> 24);
        buf[offset + 1] = (byte) (value >> 16);
        buf[offset + 2] = (byte) (value >> 8);
        buf[offset + 3] = (byte) value;
    }

    private void writeLong(byte[] buf, int offset, long value) {
        buf[offset] = (byte) (value >> 56);
        buf[offset + 1] = (byte) (value >> 48);
        buf[offset + 2] = (byte) (value >> 40);
        buf[offset + 3] = (byte) (value >> 32);
        buf[offset + 4] = (byte) (value >> 24);
        buf[offset + 5] = (byte) (value >> 16);
        buf[offset + 6] = (byte) (value >> 8);
        buf[offset + 7] = (byte) value;
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
