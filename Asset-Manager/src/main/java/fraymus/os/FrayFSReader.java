package fraymus.os;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * 💾 FRAY-FS READER - Gen 141
 * "Reads the memories stored in the disk."
 * 
 * Parses a FrayFS disk image (system.img) and extracts files.
 * Can list contents, extract individual files, or dump entire filesystem.
 * 
 * "The disk speaks. We listen."
 */
public class FrayFSReader {

    private static final byte[] MAGIC = "FRAY".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] SUPERBLOCK_MAGIC = "FRAYFS01".getBytes(StandardCharsets.US_ASCII);
    private static final int HEADER_SIZE = 64;
    private static final int SUPERBLOCK_SIZE = 512;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: FrayFSReader <image.img> [list|extract|dump <output_dir>]");
            System.out.println("  list    - List all files");
            System.out.println("  extract - Extract all files to output_dir");
            System.out.println("  dump    - Hex dump of filesystem structure");
            return;
        }

        String imagePath = args[0];
        String command = args.length > 1 ? args[1] : "list";
        String outputDir = args.length > 2 ? args[2] : "extracted";

        FrayFSReader reader = new FrayFSReader();
        
        switch (command) {
            case "list" -> reader.listFiles(imagePath);
            case "extract" -> reader.extractAll(imagePath, outputDir);
            case "dump" -> reader.dumpStructure(imagePath);
            default -> System.out.println("Unknown command: " + command);
        }
    }

    public List<FileEntry> listFiles(String imagePath) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  💾 FRAY-FS READER - Gen 141                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        List<FileEntry> entries = new ArrayList<>();
        
        try (RandomAccessFile raf = new RandomAccessFile(imagePath, "r")) {
            // Read superblock
            byte[] superblock = new byte[SUPERBLOCK_SIZE];
            raf.readFully(superblock);
            
            if (!checkMagic(superblock, 0, SUPERBLOCK_MAGIC)) {
                System.err.println("⚠️ Invalid FrayFS superblock");
                return entries;
            }
            
            String volumeName = readString(superblock, 26, 32);
            long created = readLong(superblock, 18);
            
            System.out.println("📀 Volume: " + volumeName);
            System.out.println("📅 Created: " + new Date(created));
            System.out.println();
            System.out.println("Files:");
            System.out.println("─────────────────────────────────────────────────────────────────");
            
            // Read file entries
            byte[] header = new byte[HEADER_SIZE];
            int fileNum = 0;
            
            while (raf.read(header) == HEADER_SIZE) {
                if (!checkMagic(header, 0, MAGIC)) {
                    break;  // End of filesystem
                }
                
                FileEntry entry = parseHeader(header);
                entries.add(entry);
                fileNum++;
                
                System.out.printf("  %3d. %-32s %10s  %s%n", 
                    fileNum, entry.name, formatSize(entry.size), 
                    formatFlags(entry.flags));
                
                // Skip file data
                raf.skipBytes(entry.size);
            }
            
            System.out.println("─────────────────────────────────────────────────────────────────");
            System.out.println("Total: " + entries.size() + " files");
            
        } catch (IOException e) {
            System.err.println("❌ Read failed: " + e.getMessage());
        }
        
        return entries;
    }

    public void extractAll(String imagePath, String outputDir) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  💾 FRAY-FS EXTRACTOR - Gen 141                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("⚡ Image:  " + imagePath);
        System.out.println("⚡ Output: " + outputDir);
        System.out.println();
        
        try (RandomAccessFile raf = new RandomAccessFile(imagePath, "r")) {
            // Skip superblock
            raf.seek(SUPERBLOCK_SIZE);
            
            byte[] header = new byte[HEADER_SIZE];
            int extracted = 0;
            
            while (raf.read(header) == HEADER_SIZE) {
                if (!checkMagic(header, 0, MAGIC)) {
                    break;
                }
                
                FileEntry entry = parseHeader(header);
                
                // Read file data
                byte[] data = new byte[entry.size];
                raf.readFully(data);
                
                // Verify checksum
                int computed = computeChecksum(data);
                if (computed != entry.checksum) {
                    System.out.println("   ⚠️ " + entry.name + " (checksum mismatch!)");
                } else {
                    System.out.println("   📄 " + entry.name);
                }
                
                // Write to output
                Path outPath = Paths.get(outputDir, entry.name);
                Files.createDirectories(outPath.getParent() != null ? outPath.getParent() : Paths.get(outputDir));
                Files.write(outPath, data);
                
                extracted++;
            }
            
            System.out.println();
            System.out.println("✅ Extracted " + extracted + " files to " + outputDir);
            
        } catch (IOException e) {
            System.err.println("❌ Extract failed: " + e.getMessage());
        }
    }

    public void dumpStructure(String imagePath) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  💾 FRAY-FS DUMP - Gen 141                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try (RandomAccessFile raf = new RandomAccessFile(imagePath, "r")) {
            long pos = 0;
            
            // Dump superblock
            byte[] superblock = new byte[SUPERBLOCK_SIZE];
            raf.readFully(superblock);
            System.out.println("=== SUPERBLOCK (offset 0x0000) ===");
            hexDump(superblock, 0, 64);
            pos += SUPERBLOCK_SIZE;
            
            // Dump file headers
            byte[] header = new byte[HEADER_SIZE];
            int fileNum = 0;
            
            while (raf.read(header) == HEADER_SIZE) {
                if (!checkMagic(header, 0, MAGIC)) {
                    System.out.printf("%n=== END MARKER (offset 0x%04X) ===%n", pos);
                    break;
                }
                
                FileEntry entry = parseHeader(header);
                fileNum++;
                
                System.out.printf("%n=== FILE %d: %s (offset 0x%04X) ===%n", fileNum, entry.name, pos);
                hexDump(header, 0, HEADER_SIZE);
                
                pos += HEADER_SIZE + entry.size;
                raf.skipBytes(entry.size);
            }
            
        } catch (IOException e) {
            System.err.println("❌ Dump failed: " + e.getMessage());
        }
    }

    private FileEntry parseHeader(byte[] header) {
        FileEntry entry = new FileEntry();
        entry.name = readString(header, 4, 32);
        entry.size = readInt(header, 36);
        entry.checksum = readInt(header, 40);
        entry.flags = readInt(header, 44);
        entry.timestamp = readLong(header, 48);
        return entry;
    }

    private boolean checkMagic(byte[] buf, int offset, byte[] magic) {
        for (int i = 0; i < magic.length; i++) {
            if (buf[offset + i] != magic[i]) return false;
        }
        return true;
    }

    private String readString(byte[] buf, int offset, int maxLen) {
        int end = offset;
        while (end < offset + maxLen && buf[end] != 0) end++;
        return new String(buf, offset, end - offset, StandardCharsets.UTF_8);
    }

    private int readInt(byte[] buf, int offset) {
        return ((buf[offset] & 0xFF) << 24) |
               ((buf[offset + 1] & 0xFF) << 16) |
               ((buf[offset + 2] & 0xFF) << 8) |
               (buf[offset + 3] & 0xFF);
    }

    private long readLong(byte[] buf, int offset) {
        return ((long)(buf[offset] & 0xFF) << 56) |
               ((long)(buf[offset + 1] & 0xFF) << 48) |
               ((long)(buf[offset + 2] & 0xFF) << 40) |
               ((long)(buf[offset + 3] & 0xFF) << 32) |
               ((long)(buf[offset + 4] & 0xFF) << 24) |
               ((long)(buf[offset + 5] & 0xFF) << 16) |
               ((long)(buf[offset + 6] & 0xFF) << 8) |
               (buf[offset + 7] & 0xFF);
    }

    private int computeChecksum(byte[] data) {
        int sum = 0;
        for (byte b : data) {
            sum = (sum + (b & 0xFF)) & 0xFFFFFFFF;
        }
        return sum;
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

    private String formatFlags(int flags) {
        StringBuilder sb = new StringBuilder();
        if ((flags & 0x01) != 0) sb.append("R");
        if ((flags & 0x02) != 0) sb.append("H");
        if ((flags & 0x04) != 0) sb.append("S");
        if ((flags & 0x08) != 0) sb.append("X");
        return sb.length() > 0 ? "[" + sb + "]" : "";
    }

    private void hexDump(byte[] data, int offset, int length) {
        for (int i = 0; i < length; i += 16) {
            System.out.printf("%04X: ", offset + i);
            
            // Hex
            for (int j = 0; j < 16; j++) {
                if (i + j < length) {
                    System.out.printf("%02X ", data[offset + i + j] & 0xFF);
                } else {
                    System.out.print("   ");
                }
            }
            
            System.out.print(" |");
            
            // ASCII
            for (int j = 0; j < 16 && i + j < length; j++) {
                byte b = data[offset + i + j];
                if (b >= 32 && b < 127) {
                    System.out.print((char) b);
                } else {
                    System.out.print(".");
                }
            }
            
            System.out.println("|");
        }
    }

    public static class FileEntry {
        public String name;
        public int size;
        public int checksum;
        public int flags;
        public long timestamp;
    }
}
