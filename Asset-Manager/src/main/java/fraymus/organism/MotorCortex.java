package fraymus.organism;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OPENCLAW — Motor Cortex
 *
 * Actuators for file-system navigation, foraging, cleanup, and nest retreat.
 * Depth-limited traversal prevents runaway exploration.
 */
public class MotorCortex {

    private final Path nest;
    private Path   currentLocation;
    private int    foragingRuns    = 0;
    private long   totalBytesFreed = 0L;

    public MotorCortex(String nestPath) {
        this.nest            = Paths.get(nestPath).toAbsolutePath().normalize();
        this.currentLocation = this.nest;
    }

    /**
     * Forage a directory (max depth 3) for files with the given extension.
     */
    public List<Path> forage(String directory, String extension) {
        foragingRuns++;
        List<Path> results = new ArrayList<>();
        Path start = Paths.get(directory).toAbsolutePath();
        if (!Files.exists(start)) return results;
        try (Stream<Path> stream = Files.walk(start, 3)) {
            results = stream
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith("." + extension))
                .collect(Collectors.toList());
        } catch (IOException ignored) {}
        return results;
    }

    /** Retreat to the safe nest directory. */
    public void retreatToNest() {
        currentLocation = nest;
    }

    /**
     * Cleanup regular files in a child directory owned by this nest.
     * Outside paths, the nest root, links to outside files, and locked files are skipped.
     * Returns bytes freed.
     */
    public long cleanup(String tmpDir) {
        long freed = 0L;
        if (tmpDir == null || tmpDir.isBlank()) return freed;
        try {
            Path realNest = nest.toRealPath();
            Path tmp = Paths.get(tmpDir).toAbsolutePath().normalize().toRealPath();
            if (tmp.equals(realNest) || !tmp.startsWith(realNest)
                    || !Files.isDirectory(tmp, LinkOption.NOFOLLOW_LINKS)) return freed;
            try (Stream<Path> stream = Files.list(tmp)) {
                for (Path p : stream.collect(Collectors.toList())) {
                    if (Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)) {
                        try {
                            if (!p.toRealPath().startsWith(realNest)) continue;
                            long size = Files.size(p);
                            Files.delete(p);
                            freed += size;
                        } catch (IOException ignored) {}
                    }
                }
            }
        } catch (IOException ignored) {}
        totalBytesFreed += freed;
        return freed;
    }

    public Path getNest()            { return nest; }
    public Path getCurrentLocation() { return currentLocation; }
    public int  getForagingRuns()    { return foragingRuns; }
    public long getTotalBytesFreed() { return totalBytesFreed; }

    public String getStatus() {
        return String.format("[OPENCLAW] Nest=%s Runs=%d BytesFreed=%d",
            nest.getFileName(), foragingRuns, totalBytesFreed);
    }
}
