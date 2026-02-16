package fraynix.fs;

import fraynix.brain.HyperTesseract;
import fraynix.core.*;
import fraynix.observe.EventLogger;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * FRAYFS V1: Virtual FS + Real Adapter
 * 
 * Structure:
 *   - FrayFS (logical interface)
 *   - VFileStore (in-memory / hash store)
 *   - PhysicalFSAdapter (reads/writes disk)
 *   - IntegrityService (checksums, versions, rollbacks)
 * 
 * File-to-brain mapping:
 *   - Uses consistent hashing to select a node
 *   - Stores collision bucket in the node
 *   - Logs collision_count per node
 */
public class FrayFS implements KernelService {

    private final Map<String, VFile> files = new ConcurrentHashMap<>();
    private final Map<String, List<VFile>> versions = new ConcurrentHashMap<>();
    private final HyperTesseract brain;
    private final EventLogger logger;
    
    private volatile boolean running = false;
    private long startTime;
    
    // Stats
    private long reads = 0;
    private long writes = 0;
    private long deletes = 0;
    private final Map<String, Integer> nodeCollisions = new ConcurrentHashMap<>();

    private final Set<String> watches = ConcurrentHashMap.newKeySet();

    public FrayFS(HyperTesseract brain, EventLogger logger) {
        this.brain = brain;
        this.logger = logger;
        System.out.println("📁 FrayFS initialized (virtual + physical adapter)");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // VIRTUAL FS OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    public VFile read(String path) {
        reads++;
        VFile file = files.get(normalizePath(path));
        
        if (file != null) {
            // Stimulate brain at file location
            int[] coords = file.getBrainCoordinates();
            brain.stimulateRegion(coords[0], coords[1], coords[2], coords[3], 0.2, 1);
        }
        
        return file;
    }

    public VFile write(String path, byte[] content) {
        return write(path, content, Map.of());
    }

    public VFile write(String path, byte[] content, Map<String, String> metadata) {
        writes++;
        path = normalizePath(path);
        
        VFile existing = files.get(path);
        VFile newFile;
        
        if (existing != null) {
            newFile = existing.withContent(content);
            // Keep version history
            versions.computeIfAbsent(path, k -> new ArrayList<>()).add(existing);
        } else {
            newFile = new VFile(path, content, 1, java.time.Instant.now(), metadata);
        }
        
        files.put(path, newFile);
        
        // Map to brain
        int[] coords = newFile.getBrainCoordinates();
        String nodeKey = coords[0] + "," + coords[1] + "," + coords[2] + "," + coords[3];
        int collisionCount = nodeCollisions.merge(nodeKey, 1, Integer::sum);

        var node = brain.getNode(coords[0], coords[1], coords[2], coords[3]);
        if (node != null) {
            node.remember("file:" + path, newFile.getHash());

            @SuppressWarnings("unchecked")
            List<String> bucket = (List<String>) node.recall("collision_bucket");
            if (bucket == null) {
                bucket = Collections.synchronizedList(new ArrayList<>());
                node.remember("collision_bucket", bucket);
            }
            if (!bucket.contains(path)) {
                bucket.add(path);
            }
            node.remember("collision_count", collisionCount);
        }
        brain.stimulateRegion(coords[0], coords[1], coords[2], coords[3], 0.3, 1);
        
        logger.logEvent("fs_write", Map.of(
            "path", path,
            "size", content.length,
            "version", newFile.getVersion(),
            "hash", newFile.getHash().substring(0, 8)
        ));
        
        return newFile;
    }

    public void addWatch(String path) {
        watches.add(normalizePath(path));
        logger.logEvent("fs_watch_added", Map.of("path", normalizePath(path), "watchCount", watches.size()));
    }

    public void removeWatch(String path) {
        watches.remove(normalizePath(path));
        logger.logEvent("fs_watch_removed", Map.of("path", normalizePath(path), "watchCount", watches.size()));
    }

    public Set<String> getWatches() {
        return Collections.unmodifiableSet(watches);
    }

    public boolean delete(String path) {
        deletes++;
        path = normalizePath(path);
        
        VFile removed = files.remove(path);
        if (removed != null) {
            // Keep in version history
            versions.computeIfAbsent(path, k -> new ArrayList<>()).add(removed);
            
            logger.logEvent("fs_delete", Map.of(
                "path", path,
                "lastVersion", removed.getVersion()
            ));
            return true;
        }
        return false;
    }

    public boolean exists(String path) {
        return files.containsKey(normalizePath(path));
    }

    public List<VFile> list(String directory) {
        String dir = normalizePath(directory);
        if (!dir.endsWith("/")) dir += "/";
        
        final String prefix = dir;
        return files.values().stream()
            .filter(f -> f.getPath().startsWith(prefix) && 
                        !f.getPath().equals(prefix) &&
                        !f.getPath().substring(prefix.length()).contains("/"))
            .collect(Collectors.toList());
    }

    public List<VFile> listRecursive(String directory) {
        String dir = normalizePath(directory);
        if (!dir.endsWith("/")) dir += "/";
        
        final String prefix = dir;
        return files.values().stream()
            .filter(f -> f.getPath().startsWith(prefix))
            .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // VERSION & INTEGRITY
    // ═══════════════════════════════════════════════════════════════════════════

    public List<VFile> getVersions(String path) {
        path = normalizePath(path);
        List<VFile> hist = versions.get(path);
        if (hist == null) return List.of();
        
        List<VFile> result = new ArrayList<>(hist);
        VFile current = files.get(path);
        if (current != null) result.add(current);
        return result;
    }

    public VFile rollback(String path, long targetVersion) {
        path = normalizePath(path);
        
        List<VFile> hist = versions.get(path);
        if (hist == null) return null;
        
        for (VFile v : hist) {
            if (v.getVersion() == targetVersion) {
                // Restore this version
                VFile restored = VFile.builder()
                    .path(path)
                    .content(v.getContent())
                    .version(files.containsKey(path) ? files.get(path).getVersion() + 1 : 1)
                    .metadata(v.getMetadata())
                    .build();
                
                files.put(path, restored);
                
                logger.logEvent("fs_rollback", Map.of(
                    "path", path,
                    "targetVersion", targetVersion,
                    "newVersion", restored.getVersion()
                ));
                
                return restored;
            }
        }
        return null;
    }

    public boolean verifyIntegrity(String path) {
        VFile file = files.get(normalizePath(path));
        if (file == null) return false;
        return file.verifyIntegrity();
    }

    public List<String> checkAllIntegrity() {
        List<String> corrupted = new ArrayList<>();
        for (VFile file : files.values()) {
            if (!file.verifyIntegrity()) {
                corrupted.add(file.getPath());
                logger.logEvent("fs_corruption_detected", Map.of(
                    "path", file.getPath(),
                    "expectedHash", file.getHash()
                ));
            }
        }
        return corrupted;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PHYSICAL FS ADAPTER
    // ═══════════════════════════════════════════════════════════════════════════

    public void syncFromDisk(Path root) throws IOException {
        Files.walk(root)
            .filter(Files::isRegularFile)
            .forEach(path -> {
                try {
                    String vpath = "/" + root.relativize(path).toString().replace('\\', '/');
                    byte[] content = Files.readAllBytes(path);
                    write(vpath, content, Map.of("source", "disk", "realPath", path.toString()));
                } catch (IOException e) {
                    System.err.println("Failed to sync: " + path + " - " + e.getMessage());
                }
            });
        
        logger.logEvent("fs_sync_from_disk", Map.of(
            "root", root.toString(),
            "fileCount", files.size()
        ));
    }

    public void syncToDisk(Path root) throws IOException {
        for (VFile file : files.values()) {
            Path target = root.resolve(file.getPath().substring(1));
            Files.createDirectories(target.getParent());
            Files.write(target, file.getContent());
        }
        
        logger.logEvent("fs_sync_to_disk", Map.of(
            "root", root.toString(),
            "fileCount", files.size()
        ));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STATS
    // ═══════════════════════════════════════════════════════════════════════════

    public Map<String, Object> getStats() {
        int maxCollisions = nodeCollisions.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        double avgCollisions = nodeCollisions.isEmpty() ? 0 : 
            nodeCollisions.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        
        return Map.of(
            "fileCount", files.size(),
            "totalSize", files.values().stream().mapToLong(VFile::getSize).sum(),
            "reads", reads,
            "writes", writes,
            "deletes", deletes,
            "versionedFiles", versions.size(),
            "brainNodes", nodeCollisions.size(),
            "maxCollisions", maxCollisions,
            "avgCollisions", avgCollisions
        );
    }

    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) return "/";
        if (!path.startsWith("/")) path = "/" + path;
        return path.replaceAll("/+", "/");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // KERNEL SERVICE
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    public String getName() { return "FrayFS"; }
    
    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public void start() {
        running = true;
        startTime = System.currentTimeMillis();
        System.out.println("📁 FrayFS started");
    }

    @Override
    public void stop() {
        running = false;
        System.out.println("📁 FrayFS stopped (" + files.size() + " files in memory)");
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public ServiceStatus getStatus() {
        return running ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
    }

    @Override
    public HealthReport getHealth() {
        long uptime = running ? System.currentTimeMillis() - startTime : 0;
        
        // Check for corruption
        List<String> corrupted = checkAllIntegrity();
        
        if (!corrupted.isEmpty()) {
            return HealthReport.degraded(
                corrupted.size() + " files corrupted",
                uptime,
                Map.of("corrupted", corrupted)
            );
        }
        
        return HealthReport.healthy(
            running ? ServiceStatus.RUNNING : ServiceStatus.STOPPED,
            uptime
        );
    }

    @Override
    public ServiceMetrics getMetrics() {
        return new ServiceMetrics(
            reads + writes + deletes,
            reads + writes,
            0,
            0,
            0,
            0.0,
            files.values().stream().mapToLong(VFile::getSize).sum(),
            Map.of("files", (long) files.size(), "versions", (long) versions.size())
        );
    }
}
