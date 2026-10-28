package fraymus.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * GPU PROFILE
 * "Know whether you have CUDA before you reach for it."
 *
 * Detects NVIDIA GPU(s) and the CUDA toolchain by shelling out to the standard
 * NVIDIA tools that ship with the driver / toolkit:
 *
 *   - {@code nvidia-smi}            -> GPU name, total/free VRAM, driver + CUDA runtime
 *   - {@code nvcc --version}        -> CUDA Toolkit (compiler) version, if installed
 *
 * This is detection only. It does NOT execute CUDA kernels in the JVM — it lets
 * the rest of the system decide, at runtime, whether GPU-bound work (LTX-Video,
 * model offload) can be routed to the GPU or must fall back to CPU.
 *
 * Detection is performed once and cached. All probes are wrapped in short
 * timeouts and broad try/catch so a missing tool simply reports "no GPU".
 */
public final class GpuProfile {

    private static final long PROBE_TIMEOUT_SECONDS = 5L;

    private static volatile GpuProfile instance;

    private final boolean cudaGpuPresent;
    private final List<Gpu> gpus;
    private final String driverVersion;   // e.g. "552.22"
    private final String cudaRuntime;     // CUDA version reported by nvidia-smi, e.g. "12.4"
    private final String cudaToolkit;     // nvcc release, e.g. "12.4" (empty if toolkit absent)

    /** A single detected GPU. */
    public static final class Gpu {
        public final String name;
        public final long totalVramBytes;
        public final long freeVramBytes;

        Gpu(String name, long totalVramBytes, long freeVramBytes) {
            this.name = name;
            this.totalVramBytes = totalVramBytes;
            this.freeVramBytes = freeVramBytes;
        }
    }

    private GpuProfile() {
        List<Gpu> detected = new ArrayList<>();
        String driver = "";
        String runtime = "";

        List<String> smiLines = runCommand(
            "nvidia-smi",
            "--query-gpu=name,memory.total,memory.free,driver_version",
            "--format=csv,noheader,nounits"
        );
        for (String line : smiLines) {
            // Format: "NVIDIA GeForce RTX 4090, 24564, 23010, 552.22"
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String name = parts[0].trim();
                long totalMb = parseLongSafe(parts[1].trim());
                long freeMb = parseLongSafe(parts[2].trim());
                detected.add(new Gpu(name, totalMb * 1024L * 1024L, freeMb * 1024L * 1024L));
                if (parts.length >= 4 && driver.isEmpty()) {
                    driver = parts[3].trim();
                }
            }
        }

        // CUDA runtime version as seen by the driver (top-right of `nvidia-smi`).
        if (!detected.isEmpty()) {
            for (String line : runCommand("nvidia-smi")) {
                int idx = line.indexOf("CUDA Version:");
                if (idx >= 0) {
                    runtime = line.substring(idx + "CUDA Version:".length()).trim();
                    int sp = runtime.indexOf(' ');
                    if (sp > 0) {
                        runtime = runtime.substring(0, sp);
                    }
                    // Trim trailing box-drawing/pipe characters from nvidia-smi tables.
                    runtime = runtime.replaceAll("[^0-9.].*$", "");
                    break;
                }
            }
        }

        // CUDA Toolkit (compiler) — only present if the toolkit is installed.
        String toolkit = "";
        for (String line : runCommand("nvcc", "--version")) {
            int idx = line.indexOf("release ");
            if (idx >= 0) {
                String rest = line.substring(idx + "release ".length()).trim();
                int comma = rest.indexOf(',');
                toolkit = (comma > 0 ? rest.substring(0, comma) : rest).trim();
                break;
            }
        }

        this.gpus = detected;
        this.cudaGpuPresent = !detected.isEmpty();
        this.driverVersion = driver;
        this.cudaRuntime = runtime;
        this.cudaToolkit = toolkit;
    }

    /** Lazily-initialized process-wide singleton (detection runs once). */
    public static GpuProfile getInstance() {
        GpuProfile local = instance;
        if (local == null) {
            synchronized (GpuProfile.class) {
                local = instance;
                if (local == null) {
                    local = new GpuProfile();
                    instance = local;
                }
            }
        }
        return local;
    }

    // =====================================================================
    // Facts
    // =====================================================================

    /** True if at least one NVIDIA GPU was detected via nvidia-smi. */
    public boolean hasCudaGpu() {
        return cudaGpuPresent;
    }

    /** True if the CUDA Toolkit (nvcc) is installed — required to compile kernels. */
    public boolean hasCudaToolkit() {
        return !cudaToolkit.isEmpty();
    }

    public List<Gpu> gpus() {
        return new ArrayList<>(gpus);
    }

    public int gpuCount() {
        return gpus.size();
    }

    /** Total VRAM across all detected GPUs, in bytes (0 if none). */
    public long totalVramBytes() {
        long total = 0L;
        for (Gpu g : gpus) {
            total += g.totalVramBytes;
        }
        return total;
    }

    /** Free VRAM across all detected GPUs at detection time, in bytes (0 if none). */
    public long freeVramBytes() {
        long free = 0L;
        for (Gpu g : gpus) {
            free += g.freeVramBytes;
        }
        return free;
    }

    public String driverVersion() {
        return driverVersion;
    }

    public String cudaRuntimeVersion() {
        return cudaRuntime;
    }

    public String cudaToolkitVersion() {
        return cudaToolkit;
    }

    // =====================================================================
    // Reporting
    // =====================================================================

    /** Human-readable, ASCII-only GPU report. */
    public String report() {
        StringBuilder sb = new StringBuilder();
        sb.append("GPU PROFILE\n");
        if (!cudaGpuPresent) {
            sb.append("  CUDA GPU          : none detected (CPU-only)\n");
            return sb.toString();
        }
        sb.append("  CUDA GPU          : ").append(gpus.size()).append(" device(s)\n");
        for (int i = 0; i < gpus.size(); i++) {
            Gpu g = gpus.get(i);
            sb.append("    [").append(i).append("] ").append(g.name)
              .append(" | VRAM ").append(formatBytes(g.freeVramBytes))
              .append(" free / ").append(formatBytes(g.totalVramBytes)).append(" total\n");
        }
        sb.append("  Driver            : ").append(driverVersion.isEmpty() ? "unknown" : driverVersion).append('\n');
        sb.append("  CUDA runtime      : ").append(cudaRuntime.isEmpty() ? "unknown" : cudaRuntime).append('\n');
        sb.append("  CUDA toolkit      : ").append(cudaToolkit.isEmpty() ? "not installed (nvcc)" : cudaToolkit).append('\n');
        return sb.toString();
    }

    /** Compact one-line summary for status maps / logs. */
    public String summary() {
        if (!cudaGpuPresent) {
            return "no CUDA GPU (CPU-only)";
        }
        String first = gpus.get(0).name;
        return gpus.size() + "x GPU (" + first + ") / " + formatBytes(totalVramBytes())
            + " VRAM / CUDA " + (cudaRuntime.isEmpty() ? "?" : cudaRuntime);
    }

    // =====================================================================
    // Internals
    // =====================================================================

    private static List<String> runCommand(String... command) {
        List<String> lines = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            if (!process.waitFor(PROBE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return new ArrayList<>();
            }
        } catch (Exception ignored) {
            // Tool not on PATH or failed -> treat as "not available".
            return new ArrayList<>();
        }
        return lines;
    }

    private static long parseLongSafe(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes <= 0) {
            return "0 MB";
        }
        double gb = bytes / (1024.0 * 1024.0 * 1024.0);
        if (gb >= 1.0) {
            return String.format("%.1f GB", gb);
        }
        double mb = bytes / (1024.0 * 1024.0);
        return String.format("%.0f MB", mb);
    }
}
