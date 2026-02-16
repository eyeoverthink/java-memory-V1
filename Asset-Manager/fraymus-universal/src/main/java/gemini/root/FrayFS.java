package gemini.root;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 💾 FRAY-FS - Gen 141
 * "The File System that never forgets."
 * 
 * In-memory virtual filesystem.
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

    public List<String> list() {
        return new ArrayList<>(files.keySet());
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

    private String normalizePath(String path) {
        return path.replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
    }

    public String status() {
        return String.format(
            "💾 FRAY-FS STATUS\n" +
            "   Volume: %s\n" +
            "   Files: %d\n" +
            "   Total size: %s\n" +
            "   φ-Signature: %.6f",
            volumeName,
            files.size(),
            formatSize(totalSize()),
            files.size() * PHI
        );
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

    private static class VFile {
        String name;
        byte[] data;
        long created;

        VFile(String name, byte[] data) {
            this.name = name;
            this.data = data;
            this.created = System.currentTimeMillis();
        }
    }
}
