package fraymus.core;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * THE COMPUTE SCHEDULER
 * "Know thy hardware, then use all of it."
 *
 * Central, hardware-aware parallel execution engine for Fraynix.
 *
 * Detects the host's logical CPU cores and physical RAM, then exposes two
 * right-sized, shared thread pools so the whole system parallelizes work
 * consistently instead of each component spinning up ad-hoc pools or running
 * sequential loops:
 *
 *   - CPU pool  : a work-stealing {@link ForkJoinPool} sized to the core count,
 *                 for CPU-bound math (matrix ops, HDC, field integration).
 *   - IO pool   : an elastic, bounded pool for blocking/heavy tasks (LLM calls,
 *                 disk/network), sized from cores AND available RAM.
 *
 * All worker threads are daemon threads, so they never keep the JVM alive on
 * exit (important: a prior bug froze shutdown when non-daemon pools survived).
 *
 * This is pure Java with zero native dependencies. It does NOT perform CUDA/GPU
 * execution — GPU offload (LTX-Video, model offload) is handled separately by
 * the GPU routing path. This class makes the CPU side fast and self-aware.
 *
 * Usage:
 *   ComputeScheduler cs = ComputeScheduler.getInstance();
 *   List&lt;R&gt; out = cs.parallelMap(items, item -&gt; transform(item));
 *   cs.parallelFor(0, n, i -&gt; process(i));
 *   Future&lt;X&gt; f = cs.submitIo(() -&gt; callLlm(prompt));
 */
public final class ComputeScheduler {

    // Heuristic working-set budget assumed per concurrent heavy/IO task.
    private static final long RAM_PER_IO_TASK_BYTES = 256L * 1024 * 1024; // 256 MB
    private static final int IO_PARALLELISM_HARD_CAP = 256;
    private static final int IO_PARALLELISM_FLOOR = 8;

    private static volatile ComputeScheduler instance;

    private final int logicalCores;
    private final long totalRamBytes;
    private final long maxHeapBytes;
    private final int cpuParallelism;
    private final int ioParallelism;

    private final ForkJoinPool cpuPool;
    private final ThreadPoolExecutor ioPool;

    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final AtomicLong cpuTasksSubmitted = new AtomicLong();
    private final AtomicLong ioTasksSubmitted = new AtomicLong();

    private ComputeScheduler() {
        this.logicalCores = Math.max(1, Runtime.getRuntime().availableProcessors());
        this.totalRamBytes = detectTotalRamBytes();
        this.maxHeapBytes = Runtime.getRuntime().maxMemory();

        this.cpuParallelism = logicalCores;

        // IO/blocking tasks can safely exceed core count since they spend time
        // waiting. Bound by a RAM-derived ceiling and a hard cap so we never
        // oversubscribe memory.
        long ramDerivedTasks = totalRamBytes > 0
            ? Math.max(IO_PARALLELISM_FLOOR, totalRamBytes / RAM_PER_IO_TASK_BYTES)
            : logicalCores * 4L;
        long desired = Math.max((long) logicalCores * 4L, IO_PARALLELISM_FLOOR);
        this.ioParallelism = (int) Math.min(IO_PARALLELISM_HARD_CAP, Math.min(desired, ramDerivedTasks));

        this.cpuPool = new ForkJoinPool(
            cpuParallelism,
            cpuThreadFactory(),
            null,
            true // async mode: FIFO, good for many short tasks
        );

        int ioCore = Math.min(logicalCores, ioParallelism);
        this.ioPool = new ThreadPoolExecutor(
            ioCore,
            ioParallelism,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            daemonThreadFactory("fraynix-io-")
        );
        this.ioPool.allowCoreThreadTimeOut(true);

        Runtime.getRuntime().addShutdownHook(
            new Thread(this::shutdown, "fraynix-compute-shutdown")
        );
    }

    /** Lazily-initialized process-wide singleton. */
    public static ComputeScheduler getInstance() {
        ComputeScheduler local = instance;
        if (local == null) {
            synchronized (ComputeScheduler.class) {
                local = instance;
                if (local == null) {
                    local = new ComputeScheduler();
                    instance = local;
                }
            }
        }
        return local;
    }

    // =====================================================================
    // Hardware facts
    // =====================================================================

    /** Logical CPU cores (hyperthreads) reported by the JVM. */
    public int logicalCores() {
        return logicalCores;
    }

    /** Total physical RAM in bytes, or -1 if it could not be detected. */
    public long totalRamBytes() {
        return totalRamBytes;
    }

    /** Maximum JVM heap in bytes (-Xmx). */
    public long maxHeapBytes() {
        return maxHeapBytes;
    }

    /** Threads used for CPU-bound parallelism (== logical cores). */
    public int cpuParallelism() {
        return cpuParallelism;
    }

    /** Max concurrent blocking/IO tasks (cores- and RAM-derived). */
    public int ioParallelism() {
        return ioParallelism;
    }

    // =====================================================================
    // CPU-bound parallel helpers (work-stealing pool)
    // =====================================================================

    /**
     * Parallel for-loop over [fromInclusive, toExclusive) executed on the CPU
     * pool. Blocks until all iterations complete.
     */
    public void parallelFor(int fromInclusive, int toExclusive, IntConsumer body) {
        if (toExclusive <= fromInclusive) {
            return;
        }
        cpuTasksSubmitted.incrementAndGet();
        try {
            cpuPool.submit(() ->
                IntStream.range(fromInclusive, toExclusive).parallel().forEach(body)
            ).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("parallelFor interrupted", e);
        } catch (ExecutionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Parallel map of a list using the CPU pool. Preserves input order.
     * Returns a new list; the input is not modified.
     */
    public <T, R> List<R> parallelMap(List<T> items, Function<? super T, R> mapper) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        cpuTasksSubmitted.incrementAndGet();
        try {
            return cpuPool.submit(() ->
                items.parallelStream().map(mapper).collect(Collectors.toList())
            ).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("parallelMap interrupted", e);
        } catch (ExecutionException e) {
            throw unwrap(e);
        }
    }

    /** Submit a CPU-bound task to the work-stealing pool. */
    public <T> Future<T> submitCpu(Callable<T> task) {
        cpuTasksSubmitted.incrementAndGet();
        return cpuPool.submit(task);
    }

    /** Submit a CPU-bound task to the work-stealing pool. */
    public Future<?> submitCpu(Runnable task) {
        cpuTasksSubmitted.incrementAndGet();
        return cpuPool.submit(task);
    }

    // =====================================================================
    // Blocking / IO heavy task helpers (elastic bounded pool)
    // =====================================================================

    /** Submit a blocking/heavy task (LLM, disk, network) to the IO pool. */
    public <T> Future<T> submitIo(Callable<T> task) {
        ioTasksSubmitted.incrementAndGet();
        return ioPool.submit(task);
    }

    /** Submit a blocking/heavy task (LLM, disk, network) to the IO pool. */
    public Future<?> submitIo(Runnable task) {
        ioTasksSubmitted.incrementAndGet();
        return ioPool.submit(task);
    }

    /** The shared work-stealing pool for advanced CPU-bound callers. */
    public ForkJoinPool cpuPool() {
        return cpuPool;
    }

    /** The shared elastic pool for advanced blocking/IO callers. */
    public ExecutorService ioPool() {
        return ioPool;
    }

    // =====================================================================
    // Reporting / lifecycle
    // =====================================================================

    /** Human-readable, ASCII-only hardware + parallelism report. */
    public String report() {
        StringBuilder sb = new StringBuilder();
        sb.append("COMPUTE PROFILE\n");
        sb.append("  Logical CPU cores : ").append(logicalCores).append('\n');
        sb.append("  Physical RAM      : ").append(formatBytes(totalRamBytes)).append('\n');
        sb.append("  JVM max heap      : ").append(formatBytes(maxHeapBytes)).append('\n');
        sb.append("  CPU parallelism   : ").append(cpuParallelism).append(" threads (work-stealing)\n");
        sb.append("  IO  parallelism   : ").append(ioParallelism).append(" threads (elastic)\n");
        return sb.toString();
    }

    /** Compact one-line summary, e.g. for status maps / logs. */
    public String summary() {
        return logicalCores + " cores / " + formatBytes(totalRamBytes) + " RAM / "
            + cpuParallelism + " cpu-threads / " + ioParallelism + " io-threads";
    }

    public long cpuTasksSubmitted() {
        return cpuTasksSubmitted.get();
    }

    public long ioTasksSubmitted() {
        return ioTasksSubmitted.get();
    }

    public boolean isShutdown() {
        return shutdown.get();
    }

    /** Idempotent. Drains both pools; safe to call from a shutdown hook. */
    public void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return;
        }
        cpuPool.shutdown();
        ioPool.shutdown();
        try {
            if (!ioPool.awaitTermination(5, TimeUnit.SECONDS)) {
                ioPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ioPool.shutdownNow();
        }
    }

    // =====================================================================
    // Internals
    // =====================================================================

    private static long detectTotalRamBytes() {
        try {
            java.lang.management.OperatingSystemMXBean base = ManagementFactory.getOperatingSystemMXBean();
            if (base instanceof com.sun.management.OperatingSystemMXBean sun) {
                long total = sun.getTotalMemorySize();
                if (total > 0) {
                    return total;
                }
            }
        } catch (Throwable ignored) {
            // Fall through to heap-based estimate below.
        }
        // Could not query the OS; fall back to the JVM's max heap as a floor.
        long max = Runtime.getRuntime().maxMemory();
        return max > 0 ? max : -1L;
    }

    private static ForkJoinPool.ForkJoinWorkerThreadFactory cpuThreadFactory() {
        return pool -> {
            ForkJoinWorkerThread t =
                ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            t.setName("fraynix-cpu-" + t.getPoolIndex());
            t.setDaemon(true);
            return t;
        };
    }

    private static ThreadFactory daemonThreadFactory(String prefix) {
        final AtomicLong counter = new AtomicLong();
        return runnable -> {
            Thread t = new Thread(runnable, prefix + counter.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
    }

    private static RuntimeException unwrap(ExecutionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException re) {
            return re;
        }
        return new RuntimeException(cause != null ? cause : e);
    }

    private static String formatBytes(long bytes) {
        if (bytes < 0) {
            return "unknown";
        }
        double gb = bytes / (1024.0 * 1024.0 * 1024.0);
        if (gb >= 1.0) {
            return String.format("%.1f GB", gb);
        }
        double mb = bytes / (1024.0 * 1024.0);
        return String.format("%.0f MB", mb);
    }
}
