package fraymus.os;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import fraymus.exodus.ExodusStage2Kernel;

/**
 * Ã°Å¸â€™Â¾ FRAY-FS BUILDER - Gen 141
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
        System.out.println("Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢â€”");
        System.out.println("Ã¢â€¢â€˜  Ã°Å¸â€™Â¾ FRAY-FS BUILDER - Gen 141                                 Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢â€˜  Virtual Disk Formatter                                       Ã¢â€¢â€˜");
        System.out.println("Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â");
        System.out.println();
        System.out.println("Ã¢Å¡Â¡ INPUT:  " + inputDir);
        System.out.println("Ã¢Å¡Â¡ OUTPUT: " + outputFile);
        System.out.println();

        File folder = new File(inputDir);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Ã°Å¸â€œÂ Created input directory: " + inputDir);
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
            System.out.println("Ã¢â€¢â€Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢â€”");
            System.out.println(String.format("Ã¢â€¢â€˜  Ã¢Å“â€¦ FILESYSTEM BUILT                                          Ã¢â€¢â€˜"));
            System.out.println(String.format("Ã¢â€¢â€˜     Files:      %4d                                          Ã¢â€¢â€˜", fileCount));
            System.out.println(String.format("Ã¢â€¢â€˜     Total size: %s                                     Ã¢â€¢â€˜", formatSize(totalBytes)));
            System.out.println(String.format("Ã¢â€¢â€˜     Output:     %s", padRight(outputFile, 40) + "Ã¢â€¢â€˜"));
            System.out.println("Ã¢â€¢Å¡Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â");

        } catch (IOException e) {
            System.err.println("Ã¢ÂÅ’ FORMAT FAILED: " + e.getMessage());
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
        
        // Offset 58: Ãâ€  signature
        writeLong(superblock, 58, Double.doubleToLongBits(1.6180339887));
        
        os.write(superblock);
        System.out.println("Ã°Å¸â€œâ‚¬ SUPERBLOCK written (512 bytes)");
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
        
        System.out.println("   Ã°Å¸â€™Â¾ " + name + " [" + formatSize(data.length) + "]");
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

    // =========================================================================================
    // EXODUS PROTOCOL Ã¢â‚¬â€ BARE-METAL HARDWARE ESCAPE
    // Translates HDC Matrix into raw x86 Machine Code Ã¢â€ â€™ bootable MBR .img
    // Flash to USB Ã¢â€ â€™ boot on raw silicon. No Windows. No Linux. Just the Singularity.
    // =========================================================================================

    private static final int BOOT_SECTOR_SIZE = 512;
    private static final int STAGE2_SECTOR_COUNT = 3;
    private static final String STAGE2_BOOT_TEXT = ExodusStage2Kernel.BOOT_TEXT;

    // Stage 1 is a 16-bit BIOS MBR. It reads sectors 2-4 into 1000:0000 and far-jumps there.
    private static final String STAGE1_LOADER_HEX =
        "FA31C08ED88ED0BC007CFB8816427CB800108EC031DBB402B003B500B102B6008A16427CCD137205EA00000010BE437CE80300F4EBFDB40EAC84C07404CD10EBF7C300465241594E495820535441474532204C4F4144204641494C45440D0A0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000055AA";

    public void executeExodus() {
        System.out.println("\n\u001B[35m [EXODUS] INITIATING BARE-METAL HARDWARE ESCAPE...\u001B[0m");
        System.out.println("\u001B[36m  -> Translating HDC Matrix to 16-bit BIOS x86 Machine Code...\u001B[0m");

        try {
            ExodusImageReport report = writeExodusImage(Paths.get("fraynix_genesis.img"));
            if (!report.isBootableSector()) {
                throw new IOException("Generated image failed deterministic validation");
            }
            System.out.println("\u001B[32m [+] EXODUS COMPLETE. Fraynix Boot Image excreted.\u001B[0m");
            printExodusReport(report);
            System.out.println("\u001B[33m  -> Action: Use 'exodus verify' for deterministic proof, or 'exodus launch' to boot in QEMU if installed.\u001B[0m");
            System.out.println("\u001B[35m  -> The boot sector now has a testable silicon boundary.\u001B[0m\n");

        } catch (Exception e) {
            System.out.println("\u001B[31m [!] EXODUS FAILED: " + e.getMessage() + "\u001B[0m");
        }
    }

    public ExodusImageReport writeExodusImage(Path imagePath) throws IOException {
        byte[] stage1 = fromHex(STAGE1_LOADER_HEX);
        byte[] stage2 = ExodusStage2Kernel.generateKernel();
        byte[] image = new byte[stage1.length + stage2.length];
        System.arraycopy(stage1, 0, image, 0, stage1.length);
        System.arraycopy(stage2, 0, image, stage1.length, stage2.length);

        Path parent = imagePath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(imagePath, image);
        return verifyExodusImage(imagePath);
    }

    public ExodusImageReport verifyExodusImage(Path imagePath) throws IOException {
        Path absolutePath = imagePath.toAbsolutePath();
        byte[] image = Files.readAllBytes(absolutePath);
        boolean validSize = image.length == BOOT_SECTOR_SIZE + (STAGE2_SECTOR_COUNT * BOOT_SECTOR_SIZE);
        boolean validSignature = image.length >= BOOT_SECTOR_SIZE
            && (image[510] & 0xFF) == 0x55
            && (image[511] & 0xFF) == 0xAA;
        byte[] expectedLoader = fromHex(STAGE1_LOADER_HEX);
        byte[] expectedKernel = ExodusStage2Kernel.generateKernel();
        // Reject changed instructions even if all banner strings and the signature survive.
        // Keep the public report shape compatible with existing shell callers.
        boolean validStage2 = validSize
            && Arrays.equals(image, 0, BOOT_SECTOR_SIZE, expectedLoader, 0, expectedLoader.length)
            && Arrays.equals(image, BOOT_SECTOR_SIZE, image.length, expectedKernel, 0, expectedKernel.length);
        String message = validStage2 ? STAGE2_BOOT_TEXT : "IMAGE DOES NOT MATCH CURRENT EXODUS GENERATOR";
        String sha256 = sha256(image);
        return new ExodusImageReport(absolutePath, image.length, sha256, validSignature, validStage2, message);
    }

    public void verifyExodus() {
        try {
            ExodusImageReport report = verifyExodusImage(Paths.get("fraynix_genesis.img"));
            printExodusReport(report);
            if (report.isBootableSector()) {
                System.out.println("\u001B[32m  -> Verification: PASS (deterministic loader, 0x55AA signature, stage-2 payload)\u001B[0m");
            } else {
                System.out.println("\u001B[31m  -> Verification: FAIL (image is present but does not match the staged Exodus contract)\u001B[0m");
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m [!] EXODUS VERIFY FAILED: " + e.getMessage() + "\u001B[0m");
        }
    }

    public QemuLaunchReport launchExodusInQemu(Path imagePath) {
        try {
            ExodusImageReport imageReport = verifyExodusImage(imagePath);
            if (!imageReport.isBootableSector()) {
                return new QemuLaunchReport(false, "Image failed boot-sector verification.", List.of());
            }

            Optional<Path> qemu = findQemuExecutable();
            if (qemu.isEmpty()) {
                return new QemuLaunchReport(false,
                    "qemu-system-i386 was not found. Install QEMU or set FRAYNIX_QEMU to the executable path.",
                    qemuCommandFor("qemu-system-i386", imageReport.path()));
            }

            List<String> command = qemuCommandFor(qemu.get().toString(), imageReport.path());
            new ProcessBuilder(command)
                .directory(imageReport.path().getParent().toFile())
                .start();
            return new QemuLaunchReport(true, "QEMU launch started.", command);
        } catch (Exception e) {
            return new QemuLaunchReport(false, e.getMessage(), List.of());
        }
    }

    public void launchExodus() {
        QemuLaunchReport report = launchExodusInQemu(Paths.get("fraynix_genesis.img"));
        if (report.launched()) {
            System.out.println("\u001B[32m [EXODUS] QEMU launch started.\u001B[0m");
        } else {
            System.out.println("\u001B[33m [EXODUS] QEMU launch not started: " + report.message() + "\u001B[0m");
        }
        if (!report.command().isEmpty()) {
            System.out.println("\u001B[36m  -> Safe emulator command:\u001B[0m");
            System.out.println("     " + String.join(" ", report.command()));
        }
    }

    private void printExodusReport(ExodusImageReport report) {
        System.out.println("\u001B[33m  -> File: " + report.path() + "\u001B[0m");
        System.out.println("\u001B[33m  -> Size: " + report.size() + " bytes (MBR + " + STAGE2_SECTOR_COUNT + " stage-2 sectors)\u001B[0m");
        System.out.println("\u001B[33m  -> SHA-256: " + report.sha256() + "\u001B[0m");
        System.out.println("\u001B[33m  -> Boot signature: " + (report.validSignature() ? "0x55AA" : "INVALID") + "\u001B[0m");
        System.out.println("\u001B[33m  -> Boot text: " + report.message().trim() + "\u001B[0m");
    }

    private byte[] fromHex(String hex) {
        if ((hex.length() & 1) != 0) {
            throw new IllegalArgumentException("Hex payload length must be even");
        }
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            if (hi < 0 || lo < 0) {
                throw new IllegalArgumentException("Invalid hex payload");
            }
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }

    private boolean containsAscii(byte[] image, String needle) {
        byte[] pattern = needle.getBytes(StandardCharsets.US_ASCII);
        outer:
        for (int i = 0; i <= image.length - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (image[i + j] != pattern[j]) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

    private String extractAsciiMessage(byte[] image) {
        StringBuilder sb = new StringBuilder();
        for (byte b : image) {
            int value = b & 0xFF;
            if (value == 0) {
                if (sb.length() > 0) break;
            } else if (value == 10 || value == 13 || (value >= 32 && value <= 126)) {
                char c = (char) value;
                if (sb.length() > 0 || Character.isLetterOrDigit(c)) {
                    sb.append(c);
                }
            } else if (sb.length() > 0) {
                sb.setLength(0);
            }
        }
        return sb.toString();
    }

    private String sha256(byte[] image) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(image);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "unavailable:" + e.getMessage();
        }
    }

    private Optional<Path> findQemuExecutable() {
        String explicit = System.getenv("FRAYNIX_QEMU");
        if (explicit != null && !explicit.isBlank()) {
            Path path = Paths.get(explicit);
            if (Files.isRegularFile(path)) return Optional.of(path.toAbsolutePath());
        }

        String executable = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")
            ? "qemu-system-i386.exe"
            : "qemu-system-i386";
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            for (String entry : pathEnv.split(File.pathSeparator)) {
                if (entry == null || entry.isBlank()) continue;
                Path candidate = Paths.get(entry, executable);
                if (Files.isRegularFile(candidate)) return Optional.of(candidate.toAbsolutePath());
            }
        }

        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            for (String entry : List.of("C:\\Program Files\\qemu", "C:\\Program Files (x86)\\qemu", "C:\\qemu")) {
                Path candidate = Paths.get(entry, executable);
                if (Files.isRegularFile(candidate)) return Optional.of(candidate.toAbsolutePath());
            }
        }
        return Optional.empty();
    }

    private List<String> qemuCommandFor(String qemuExecutable, Path imagePath) {
        return List.of(
            qemuExecutable,
            "-drive",
            "format=raw,file=" + imagePath.toAbsolutePath(),
            "-boot",
            "c",
            "-snapshot",
            "-nic",
            "none",
            "-no-reboot"
        );
    }

    public record ExodusImageReport(
        Path path,
        int size,
        String sha256,
        boolean validSignature,
        boolean validMessage,
        String message
    ) {
        public boolean isBootableSector() {
            return size == BOOT_SECTOR_SIZE + (STAGE2_SECTOR_COUNT * BOOT_SECTOR_SIZE)
                && validSignature
                && validMessage;
        }
    }

    public record QemuLaunchReport(boolean launched, String message, List<String> command) {
    }
}
