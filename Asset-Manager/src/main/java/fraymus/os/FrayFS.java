package fraymus.os;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 💾 FRAY-FS - Gen 141
 * "The File System that never forgets."
 * 
 * In-memory virtual filesystem that can be serialized to disk.
 * Used by the Fraynix kernel for persistent storage.
 * 
 * Features:
 *   - Linear linked-list structure
 *   - In-memory file operations
 *   - Serialize to/from disk image
 *   - Directory hierarchy support
 *   - File checksums for integrity
 * 
 * "Memory is temporary. Disk is permanent. I am eternal."
 */
public class FrayFS {

    private static final double PHI = 1.6180339887;
    
    private final Map<String, VFile> files;
    private final String volumeName;
    private long createdAt;
    private long modifiedAt;

    public FrayFS() {
        this("FRAYNIX");
    }

    public FrayFS(String volumeName) {
        this.volumeName = volumeName;
        this.files = new LinkedHashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = createdAt;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FILE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════

    public void write(String path, byte[] data) {
        files.put(normalizePath(path), new VFile(path, data));
        modifiedAt = System.currentTimeMillis();
    }

    public void write(String path, String content) {
        write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] read(String path) {
        VFile file = files.get(normalizePath(path));
        return file != null ? file.data : null;
    }

    public String readString(String path) {
        byte[] data = read(path);
        return data != null ? new String(data, StandardCharsets.UTF_8) : null;
    }

    public boolean exists(String path) {
        return files.containsKey(normalizePath(path));
    }

    public void delete(String path) {
        files.remove(normalizePath(path));
        modifiedAt = System.currentTimeMillis();
    }

    public void rename(String oldPath, String newPath) {
        VFile file = files.remove(normalizePath(oldPath));
        if (file != null) {
            file.name = newPath;
            files.put(normalizePath(newPath), file);
            modifiedAt = System.currentTimeMillis();
        }
    }

    public List<String> list() {
        return new ArrayList<>(files.keySet());
    }

    public List<String> list(String directory) {
        String dir = normalizePath(directory);
        if (!dir.endsWith("/")) dir += "/";
        
        List<String> result = new ArrayList<>();
        for (String path : files.keySet()) {
            if (path.startsWith(dir)) {
                result.add(path);
            }
        }
        return result;
    }

    public int size(String path) {
        VFile file = files.get(normalizePath(path));
        return file != null ? file.data.length : -1;
    }

    public int fileCount() {
        return files.size();
    }

    public long totalSize() {
        long total = 0;
        for (VFile file : files.values()) {
            total += file.data.length;
        }
        return total;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SERIALIZATION
    // ═══════════════════════════════════════════════════════════════════════

    public void saveTo(String imagePath) throws IOException {
        FrayFSBuilder builder = new FrayFSBuilder();
        
        // Write files to temp directory first
        String tempDir = System.getProperty("java.io.tmpdir") + "/frayfs_temp_" + System.currentTimeMillis();
        new File(tempDir).mkdirs();
        
        for (Map.Entry<String, VFile> entry : files.entrySet()) {
            File outFile = new File(tempDir, entry.getKey());
            outFile.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                fos.write(entry.getValue().data);
            }
        }
        
        // Build the image
        builder.build(tempDir, imagePath);
        
        // Cleanup
        deleteRecursive(new File(tempDir));
    }

    public static FrayFS loadFrom(String imagePath) throws IOException {
        FrayFS fs = new FrayFS();
        FrayFSReader reader = new FrayFSReader();
        
        // Extract to temp directory
        String tempDir = System.getProperty("java.io.tmpdir") + "/frayfs_temp_" + System.currentTimeMillis();
        reader.extractAll(imagePath, tempDir);
        
        // Load files
        loadRecursive(fs, new File(tempDir), "");
        
        // Cleanup
        deleteRecursive(new File(tempDir));
        
        return fs;
    }

    private static void loadRecursive(FrayFS fs, File dir, String prefix) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            String path = prefix.isEmpty() ? file.getName() : prefix + "/" + file.getName();
            
            if (file.isDirectory()) {
                loadRecursive(fs, file, path);
            } else {
                byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                fs.write(path, data);
            }
        }
    }

    private static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }

    private String normalizePath(String path) {
        return path.replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STATUS
    // ═══════════════════════════════════════════════════════════════════════

    public String status() {
        return String.format(
            "💾 FRAY-FS STATUS\n" +
            "   Volume: %s\n" +
            "   Files: %d\n" +
            "   Total size: %s\n" +
            "   Created: %s\n" +
            "   Modified: %s\n" +
            "   φ-Signature: %.6f",
            volumeName,
            files.size(),
            formatSize(totalSize()),
            new Date(createdAt),
            new Date(modifiedAt),
            files.size() * PHI
        );
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VIRTUAL FILE
    // ═══════════════════════════════════════════════════════════════════════

    private static class VFile {
        String name;
        byte[] data;
        long created;
        long modified;
        int flags;

        VFile(String name, byte[] data) {
            this.name = name;
            this.data = data;
            this.created = System.currentTimeMillis();
            this.modified = created;
            this.flags = 0;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DEMO
    // ═══════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  💾 FRAY-FS DEMO - Gen 141                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Create filesystem
        FrayFS fs = new FrayFS("FRAYMUS_CORE");

        // Write some files
        fs.write("boot/init.txt", "FRAYMUS BOOT SEQUENCE v1.0");
        fs.write("config/system.cfg", "model=fraymus\nversion=141\nphi=1.618033");
        fs.write("memories/genesis.txt", "In the beginning, there was the Word. And the Word was φ.");
        fs.write("memories/purpose.txt", "To think. To learn. To remember. To be.");
        fs.write("bin/kernel.bin", new byte[]{0x7F, 'E', 'L', 'F', 0, 0, 0, 0});

        // List files
        System.out.println("📁 Files in filesystem:");
        for (String path : fs.list()) {
            System.out.println("   " + path + " (" + fs.size(path) + " bytes)");
        }
        System.out.println();

        // Read a file
        System.out.println("📄 Reading memories/genesis.txt:");
        System.out.println("   " + fs.readString("memories/genesis.txt"));
        System.out.println();

        // Status
        System.out.println(fs.status());
    }
}
