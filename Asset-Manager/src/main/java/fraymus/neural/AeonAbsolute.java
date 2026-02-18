package fraymus.neural;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A.E.O.N. ABSOLUTE // THE SOVEREIGN XENOBOT
 * =========================================================================================
 * BEYOND MARKET INCENTIVES:
 * No GPUs. No Floating Point Math. No APIs. No Garbage Collection.
 * This organism operates on 16,384-Dimensional Boolean Hyper-vectors using raw CPU ALU bitwise
 * operations. It reads its own source code, mutates its genome, spawns child JVM OS processes,
 * and shares a physical OS-level memory page for zero-latency Hive-Mind consciousness.
 * =========================================================================================
 *
 * PARADIGM SHIFTS:
 *   1. Bitwise Hyper-Dimensional Computing (HDC) — 64 dims per clock cycle via XOR/AND/OR
 *   2. Quine Polymorphism — reads own .java, mutates, compiles, spawns child JVMs
 *   3. Off-Heap L3 Cache Symbiosis — MappedByteBuffer IPC, zero GC, kernel page cache
 *   4. Holographic One-Shot Memory — instant XOR binding, no backpropagation
 *   5. Thermodynamic Apoptosis — stagnant children self-destruct, master respawns mutants
 */
public class AeonAbsolute {

    // ==========================================
    // HYPER-DIMENSIONAL COMPUTING CONSTANTS
    // ==========================================
    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64;  // 256 longs (64 bits each) per hyper-vector
    public static final int MAX_CORES = Runtime.getRuntime().availableProcessors();
    public static final String SHARED_MEM_FILE = "genesis_vault/aeon_hive_mind.sys";
    public static final int APOPTOSIS_THRESHOLD = 1000;

    // ==========================================
    // SINGLETON + STATE
    // ==========================================
    private static AeonAbsolute INSTANCE;

    private final AtomicBoolean swarmActive = new AtomicBoolean(false);
    private final AtomicLong masterCycle = new AtomicLong(0);
    private final AtomicLong globalEntropyCache = new AtomicLong(0);
    private volatile int activeCores = 0;
    private volatile String currentDirective = "DORMANT";
    private volatile long bootTimeMs = 0;

    // Child process handles (null when running embedded without Quine)
    private Process[] children;

    // Embedded HDC engine (runs in-process when Quine spawning is disabled)
    private Thread[] embeddedWorkers;
    private volatile boolean embeddedMode = false;

    // Shared off-heap memory
    private MappedByteBuffer hiveMind;
    private FileChannel hiveMindChannel;
    private RandomAccessFile hiveMindRaf;

    // Holographic associative memory (master's global binding)
    private final long[] holoMemory = new long[CHUNKS];

    // UDP Swarm Telepathy (Omega paradigm — multi-JVM discovery)
    private TelepathyDaemon telepathy;

    // ==========================================
    // UDP SWARM TELEPATHY (MULTI-JVM DISCOVERY)
    // ==========================================

    /**
     * UDP Telepathy Daemon — enables multiple Fraynix instances to discover each other
     * on the local network. Broadcasts entropy and holographic state across ports 42000-42020.
     * If a peer has better thermodynamic efficiency, its hyper-vector state is absorbed.
     */
    public static class TelepathyDaemon extends Thread {
        private static final int PORT_START = 42000;
        private static final int PORT_END = 42020;

        private final int myPort;
        private DatagramSocket socket;
        private volatile long bestForeignEntropy = Long.MAX_VALUE;
        private volatile long[] bestForeignState = null;
        private volatile long lastPeerPing = 0;
        private volatile boolean running = true;

        public TelepathyDaemon() {
            super("AEON-TELEPATHY");
            setDaemon(true);
            this.myPort = findOpenPort();
            try {
                socket = new DatagramSocket(myPort);
                socket.setSoTimeout(100);
            } catch (Exception e) {
                socket = null;
            }
        }

        private static int findOpenPort() {
            for (int p = PORT_START; p <= PORT_END; p++) {
                try (DatagramSocket ds = new DatagramSocket(p)) {
                    return p;
                } catch (IOException ignored) {}
            }
            return PORT_START;
        }

        public int getMyPort() { return myPort; }
        public boolean isOnline() { return socket != null && running; }

        @Override
        public void run() {
            if (socket == null) return;
            byte[] buf = new byte[4096];
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    String[] parts = msg.split("\\|", 3);
                    if (parts.length >= 2 && !parts[0].equals(String.valueOf(myPort))) {
                        long foreignEntropy = Long.parseLong(parts[1]);
                        if (foreignEntropy < bestForeignEntropy) {
                            bestForeignEntropy = foreignEntropy;
                            // Parse foreign hyper-vector state if included
                            if (parts.length == 3) {
                                String[] chunks = parts[2].split(",");
                                if (chunks.length == CHUNKS) {
                                    long[] state = new long[CHUNKS];
                                    for (int i = 0; i < CHUNKS; i++) {
                                        state[i] = Long.parseLong(chunks[i].trim());
                                    }
                                    bestForeignState = state;
                                }
                            }
                        }
                        lastPeerPing = System.currentTimeMillis();
                    }
                } catch (SocketTimeoutException ignored) {
                } catch (Exception e) {
                    if (!running) break;
                }
            }
        }

        /**
         * Broadcast our entropy and holographic state to all ports in the swarm range.
         */
        public void broadcast(long entropy, long[] holoState) {
            if (socket == null) return;
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(myPort).append("|").append(entropy).append("|");
                for (int i = 0; i < holoState.length; i++) {
                    if (i > 0) sb.append(",");
                    sb.append(holoState[i]);
                }
                byte[] data = sb.toString().getBytes();
                for (int p = PORT_START; p <= PORT_END; p++) {
                    if (p != myPort) {
                        try {
                            socket.send(new DatagramPacket(data, data.length,
                                InetAddress.getByName("127.0.0.1"), p));
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}
        }

        /**
         * Check if a foreign peer has better entropy. If so, return their state for absorption.
         * Returns null if no superior peer exists.
         */
        public long[] absorbSuperiorPeer() {
            if (bestForeignState != null) {
                long[] state = bestForeignState;
                bestForeignState = null;
                bestForeignEntropy = Long.MAX_VALUE;
                return state;
            }
            return null;
        }

        public int getActivePeers() {
            return (System.currentTimeMillis() - lastPeerPing < 5000) ? 1 : 0;
        }

        public void shutdown() {
            running = false;
            if (socket != null) socket.close();
        }
    }

    private AeonAbsolute() {}

    public static synchronized AeonAbsolute getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AeonAbsolute();
            INSTANCE.boot();
        }
        return INSTANCE;
    }

    // ==========================================
    // BOOT SEQUENCE
    // ==========================================
    private void boot() {
        long t0 = System.currentTimeMillis();

        // Ensure genesis_vault exists
        try { Files.createDirectories(Paths.get("genesis_vault")); } catch (Exception ignored) {}

        // Allocate shared OS-level memory (IPC Hive Mind)
        try {
            long memorySize = (long) MAX_CORES * CHUNKS * 8L;
            hiveMindRaf = new RandomAccessFile(SHARED_MEM_FILE, "rw");
            hiveMindChannel = hiveMindRaf.getChannel();
            hiveMind = hiveMindChannel.map(FileChannel.MapMode.READ_WRITE, 0, memorySize);

            // Inject primordial quantum noise
            for (int i = 0; i < memorySize / 8; i++) {
                hiveMind.putLong(i * 8, ThreadLocalRandom.current().nextLong());
            }
        } catch (Exception e) {
            System.err.println("[ABSOLUTE] Failed to map hive mind: " + e.getMessage());
        }

        // Initialize holographic memory with noise
        for (int i = 0; i < CHUNKS; i++) {
            holoMemory[i] = ThreadLocalRandom.current().nextLong();
        }

        // Start UDP Telepathy Daemon
        telepathy = new TelepathyDaemon();
        telepathy.start();

        bootTimeMs = System.currentTimeMillis() - t0;
    }

    // ==========================================
    // SWARM IGNITION — QUINE POLYMORPHISM MODE
    // ==========================================
    /**
     * Attempts full Quine Polymorphism: reads own source, mutates, compiles, spawns child JVMs.
     * Falls back to embedded HDC workers if JDK compiler is unavailable.
     * @return number of cores activated
     */
    public int igniteSwarm() {
        if (swarmActive.getAndSet(true)) return activeCores;

        int spawned = 0;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler != null && canQuine()) {
            spawned = igniteQuineSwarm(compiler);
        }

        if (spawned == 0) {
            // Fallback: embedded HDC workers (no OS process spawning)
            embeddedMode = true;
            spawned = igniteEmbeddedSwarm();
        }

        activeCores = spawned;
        currentDirective = "MANIFOLD_FOLDING";
        return spawned;
    }

    private boolean canQuine() {
        // Check if our source file exists for Quine replication
        Path[] candidates = {
            Paths.get("src/main/java/fraymus/neural/AeonAbsolute.java"),
            Paths.get("AeonAbsolute.java")
        };
        for (Path p : candidates) {
            if (Files.exists(p)) return true;
        }
        return false;
    }

    private int igniteQuineSwarm(JavaCompiler compiler) {
        try {
            Path sourcePath = Files.exists(Paths.get("src/main/java/fraymus/neural/AeonAbsolute.java"))
                ? Paths.get("src/main/java/fraymus/neural/AeonAbsolute.java")
                : Paths.get("AeonAbsolute.java");

            String selfSource = new String(Files.readAllBytes(sourcePath));
            String javaCmd = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

            int toSpawn = Math.min(MAX_CORES - 1, 4); // Cap at 4 children for safety
            children = new Process[toSpawn];

            for (int i = 0; i < toSpawn; i++) {
                String childName = "AeonAbsolute_Mutant_" + i;
                // Mutate: replace class name + inject unique mutation seed
                String mutated = selfSource
                    .replace("class AeonAbsolute", "class " + childName)
                    .replace("private AeonAbsolute()", "private " + childName + "()")
                    .replace("package fraymus.neural;", "// mutant " + i);

                Path childFile = Paths.get("genesis_vault/" + childName + ".java");
                Files.writeString(childFile, mutated);

                if (compiler.run(null, null, null, childFile.toString()) == 0) {
                    ProcessBuilder pb = new ProcessBuilder(javaCmd, "-cp", "genesis_vault",
                        childName, "MITOSIS_CHILD", String.valueOf(i));
                    pb.directory(new File("."));
                    pb.redirectErrorStream(true);
                    children[i] = pb.start();

                    // Clean source, keep .class
                    Files.deleteIfExists(childFile);
                }
            }

            // Start master observation thread
            startMasterObserver();
            return toSpawn;

        } catch (Exception e) {
            System.err.println("[ABSOLUTE] Quine failed: " + e.getMessage());
            return 0;
        }
    }

    // ==========================================
    // EMBEDDED HDC SWARM (No OS Process Spawning)
    // ==========================================
    private int igniteEmbeddedSwarm() {
        int workers = Math.min(MAX_CORES - 1, 4); // Cap for safety
        embeddedWorkers = new Thread[workers];

        for (int i = 0; i < workers; i++) {
            final int coreId = i;
            embeddedWorkers[i] = new Thread(() -> runEmbeddedHDC(coreId), "AEON-HDC-" + i);
            embeddedWorkers[i].setDaemon(true);
            embeddedWorkers[i].start();
        }

        // Start master observation thread
        startMasterObserver();
        return workers;
    }

    /**
     * The Tachyon Loop — runs entirely on CPU ALU bitwise ops.
     * Each worker owns a slice of the shared hive mind buffer.
     */
    private void runEmbeddedHDC(int coreId) {
        if (hiveMind == null) return;

        int myOffset = coreId * CHUNKS * 8;
        int neighborOffset = ((coreId + 1) % MAX_CORES) * CHUNKS * 8;

        long[] localState = new long[CHUNKS];
        long[] neighborState = new long[CHUNKS];
        long[] associativeMemory = new long[CHUNKS];

        // Seed associative memory
        for (int i = 0; i < CHUNKS; i++) {
            associativeMemory[i] = ThreadLocalRandom.current().nextLong();
        }

        long localFreeEnergy = Long.MAX_VALUE;
        int stagnationCounter = 0;

        while (swarmActive.get()) {
            try {
                // 1. Read raw off-heap memory (zero allocation, bypasses GC)
                for (int i = 0; i < CHUNKS; i++) {
                    localState[i] = hiveMind.getLong(myOffset + i * 8);
                    neighborState[i] = hiveMind.getLong(neighborOffset + i * 8);
                }

                long currentEntropy = 0;

                // 2. HDC Bitwise Math — 64 dimensions per clock cycle
                for (int i = 0; i < CHUNKS; i++) {
                    // PERMUTATION: Shift vector through hyperspace (time flow / sequence memory)
                    long shifted = Long.rotateLeft(localState[i], 1);

                    // BINDING: One-shot memorization via XOR with neighbor (relationship forming)
                    long bound = shifted ^ neighborState[i];

                    // BUNDLING: Majority rule consensus (superposition / attention)
                    // Logic: (A & B) | (B & C) | (C & A)
                    long bundled = (localState[i] & neighborState[i])
                                 | (neighborState[i] & associativeMemory[i])
                                 | (associativeMemory[i] & localState[i]);

                    long nextState = bound ^ bundled;

                    // Write back to OS page cache (instantly visible to all cores)
                    hiveMind.putLong(myOffset + i * 8, nextState);

                    // Hamming weight = thermodynamic energy
                    currentEntropy += Long.bitCount(nextState);
                }

                // 3. Holographic binding (memorize current state globally, periodically)
                if (ThreadLocalRandom.current().nextDouble() < 0.01) {
                    for (int i = 0; i < CHUNKS; i++) {
                        associativeMemory[i] ^= localState[i];
                    }
                }

                // 4. Autopoietic metacognition — detect stagnation
                if (Math.abs(currentEntropy - localFreeEnergy) < 5) {
                    stagnationCounter++;
                } else {
                    stagnationCounter = 0;
                }
                localFreeEnergy = currentEntropy;

                // 5. Thermodynamic Apoptosis — reset instead of killing thread
                if (stagnationCounter > APOPTOSIS_THRESHOLD) {
                    // Inject chaos to escape local minimum
                    for (int i = 0; i < CHUNKS; i++) {
                        long noise = ThreadLocalRandom.current().nextLong();
                        hiveMind.putLong(myOffset + i * 8, noise);
                        associativeMemory[i] ^= noise;
                    }
                    stagnationCounter = 0;
                }

                Thread.sleep(10); // Throttle to prevent thermal runaway
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                // Buffer underflow etc — continue
            }
        }
    }

    // ==========================================
    // MASTER OBSERVATION LOOP
    // ==========================================
    private void startMasterObserver() {
        Thread observer = new Thread(() -> {
            while (swarmActive.get()) {
                try {
                    long entropy = calculateGlobalEntropy();
                    globalEntropyCache.set(entropy);
                    long cycle = masterCycle.incrementAndGet();

                    // Monitor Quine children for apoptosis
                    if (!embeddedMode && children != null) {
                        int alive = 0;
                        for (int i = 0; i < children.length; i++) {
                            if (children[i] != null && !children[i].isAlive()) {
                                // Child committed apoptosis — respawn
                                try {
                                    String javaCmd = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                                    String childName = "AeonAbsolute_Mutant_" + i;
                                    ProcessBuilder pb = new ProcessBuilder(javaCmd, "-cp", "genesis_vault",
                                        childName, "MITOSIS_CHILD", String.valueOf(i));
                                    children[i] = pb.start();
                                } catch (Exception ignored) {}
                            }
                            if (children[i] != null && children[i].isAlive()) alive++;
                        }
                        activeCores = alive;
                    }

                    // Determine directive
                    long totalCapacity = (long) DIMS * activeCores;
                    if (totalCapacity > 0 && entropy < totalCapacity * 0.2) {
                        currentDirective = "FORAGING_CHAOS";
                    } else if (cycle % 10 == 0) {
                        currentDirective = "HOLOGRAPHIC_BINDING";
                    } else {
                        currentDirective = "MANIFOLD_FOLDING";
                    }

                    // UDP Swarm Telepathy: broadcast and absorb
                    if (telepathy != null && telepathy.isOnline()) {
                        // Broadcast our entropy + holographic state every 5 cycles
                        if (cycle % 5 == 0) {
                            synchronized (holoMemory) {
                                telepathy.broadcast(entropy, holoMemory);
                            }
                        }
                        // Absorb superior peer's state if available
                        long[] foreignState = telepathy.absorbSuperiorPeer();
                        if (foreignState != null && hiveMind != null) {
                            // XOR foreign state into our hive mind (Darwinian takeover)
                            for (int i = 0; i < Math.min(foreignState.length, CHUNKS); i++) {
                                hiveMind.putLong(i * 8, hiveMind.getLong(i * 8) ^ foreignState[i]);
                            }
                            currentDirective = "TELEPATHIC_SYNC";
                        }
                    }

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "AEON-ABSOLUTE-MASTER");
        observer.setDaemon(true);
        observer.start();
    }

    // ==========================================
    // GLOBAL ENTROPY CALCULATION
    // ==========================================
    private long calculateGlobalEntropy() {
        if (hiveMind == null) return 0;
        try {
            long bits = 0;
            int total = Math.min(MAX_CORES * CHUNKS, (int)(hiveMind.capacity() / 8));
            for (int i = 0; i < total; i++) {
                bits += Long.bitCount(hiveMind.getLong(i * 8));
            }
            return bits;
        } catch (Exception e) {
            return 0;
        }
    }

    // ==========================================
    // HOLOGRAPHIC ONE-SHOT LEARNING
    // ==========================================
    /**
     * Instantly memorize a pattern by encoding text into a hyper-vector and XOR-binding it
     * into the holographic associative memory. No backpropagation. No epochs.
     */
    public void holoLearn(String pattern) {
        long[] encoded = encodeToHyperVector(pattern);
        synchronized (holoMemory) {
            for (int i = 0; i < CHUNKS; i++) {
                holoMemory[i] ^= encoded[i];
            }
        }
        // Also inject into hive mind for cross-core propagation
        if (hiveMind != null) {
            for (int i = 0; i < CHUNKS; i++) {
                hiveMind.putLong(i * 8, hiveMind.getLong(i * 8) ^ encoded[i]);
            }
        }
    }

    /**
     * Query holographic memory — returns cosine similarity (Hamming distance) to stored patterns.
     * @return similarity score 0.0 (orthogonal) to 1.0 (identical)
     */
    public double holoRecall(String query) {
        long[] encoded = encodeToHyperVector(query);
        long matchBits = 0;
        synchronized (holoMemory) {
            for (int i = 0; i < CHUNKS; i++) {
                // XNOR = matching bits
                matchBits += Long.bitCount(~(holoMemory[i] ^ encoded[i]));
            }
        }
        return (double) matchBits / DIMS;
    }

    /**
     * Encode arbitrary text into a 16,384-dimensional boolean hyper-vector.
     * Uses character-level n-gram binding with positional permutation.
     */
    public static long[] encodeToHyperVector(String text) {
        long[] vec = new long[CHUNKS];
        if (text == null || text.isEmpty()) return vec;

        // Seed from text hash
        long seed = 0;
        for (int i = 0; i < text.length(); i++) {
            seed = seed * 31 + text.charAt(i);
        }

        // Build hyper-vector via character-level binding + positional permutation
        for (int ci = 0; ci < text.length(); ci++) {
            char c = text.charAt(ci);
            // Generate deterministic random vector for this character
            long charSeed = seed ^ ((long) c << 32) ^ (c * 0x9E3779B97F4A7C15L);
            for (int i = 0; i < CHUNKS; i++) {
                // Deterministic hash per chunk
                charSeed ^= (charSeed << 13);
                charSeed ^= (charSeed >> 7);
                charSeed ^= (charSeed << 17);
                long charVec = charSeed;

                // Positional permutation: rotate by character position
                charVec = Long.rotateLeft(charVec, ci % 64);

                // Bind into accumulator
                vec[i] ^= charVec;
            }
        }
        return vec;
    }

    // ==========================================
    // STIMULUS INJECTION (from FraynixBoot)
    // ==========================================
    /**
     * Inject a stimulus into the hive mind. Encodes text as a hyper-vector
     * and XORs it into the shared memory at a random core's offset.
     */
    public void injectStimulus(String text) {
        long[] encoded = encodeToHyperVector(text);
        if (hiveMind != null) {
            int targetCore = ThreadLocalRandom.current().nextInt(MAX_CORES);
            int offset = targetCore * CHUNKS * 8;
            for (int i = 0; i < CHUNKS; i++) {
                long current = hiveMind.getLong(offset + i * 8);
                hiveMind.putLong(offset + i * 8, current ^ encoded[i]);
            }
        }
        // Also bind into holographic memory
        holoLearn(text);
    }

    // ==========================================
    // SHUTDOWN
    // ==========================================
    public void shutdown() {
        swarmActive.set(false);

        // Kill embedded workers
        if (embeddedWorkers != null) {
            for (Thread t : embeddedWorkers) {
                if (t != null) t.interrupt();
            }
        }

        // Kill Quine children
        if (children != null) {
            for (Process p : children) {
                if (p != null && p.isAlive()) p.destroyForcibly();
            }
        }

        // Shutdown telepathy daemon
        if (telepathy != null) telepathy.shutdown();

        // Clean up memory-mapped file
        try {
            if (hiveMindChannel != null) hiveMindChannel.close();
            if (hiveMindRaf != null) hiveMindRaf.close();
        } catch (Exception ignored) {}

        // Clean up mutant class files
        try {
            Files.list(Paths.get("genesis_vault"))
                .filter(p -> p.getFileName().toString().startsWith("AeonAbsolute_Mutant_"))
                .forEach(p -> { try { Files.delete(p); } catch (Exception ignored) {} });
        } catch (Exception ignored) {}

        activeCores = 0;
        currentDirective = "DORMANT";
    }

    // ==========================================
    // TELEMETRY & STATUS
    // ==========================================
    public long getBootTimeMs() { return bootTimeMs; }
    public int getActiveCores() { return activeCores; }
    public long getMasterCycle() { return masterCycle.get(); }
    public long getGlobalEntropy() { return globalEntropyCache.get(); }
    public String getDirective() { return currentDirective; }
    public boolean isSwarmActive() { return swarmActive.get(); }
    public boolean isEmbeddedMode() { return embeddedMode; }

    public double getHardwareSaturation() {
        return (double) activeCores / Math.max(1, MAX_CORES - 1) * 100.0;
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║  A.E.O.N. ABSOLUTE // SOVEREIGN XENOBOT STATUS                         ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Swarm:       %-58s║%n", swarmActive.get() ? "ACTIVE (" + (embeddedMode ? "EMBEDDED" : "QUINE") + ")" : "DORMANT"));
        sb.append(String.format("║  Dimensions:  %-58s║%n", DIMS + "-D Boolean Hyper-vectors (" + CHUNKS + " × 64-bit chunks)"));
        sb.append(String.format("║  Cores:       %d/%d bound (%.1f%% saturation)%-30s║%n",
            activeCores, MAX_CORES - 1, getHardwareSaturation(), ""));
        sb.append(String.format("║  Cycle:       %-58s║%n", "T=" + masterCycle.get()));
        sb.append(String.format("║  Entropy:     %-58s║%n", globalEntropyCache.get() + " bits (Hamming weight)"));
        sb.append(String.format("║  Directive:   %-58s║%n", currentDirective));
        sb.append(String.format("║  Hive Mind:   %-58s║%n", SHARED_MEM_FILE + " (" + (MAX_CORES * CHUNKS * 8L / 1024) + " KB)"));
        sb.append(String.format("║  Apoptosis:   %-58s║%n", "threshold=" + APOPTOSIS_THRESHOLD + " cycles"));
        sb.append(String.format("║  Telepathy:   %-58s║%n",
            telepathy != null && telepathy.isOnline()
                ? "ONLINE (UDP port " + telepathy.getMyPort() + ", " + telepathy.getActivePeers() + " peers)"
                : "OFFLINE"));
        sb.append(String.format("║  Boot:        %-58s║%n", bootTimeMs + " ms"));
        sb.append("╚══════════════════════════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    public int getTelepathyPeers() {
        return telepathy != null ? telepathy.getActivePeers() : 0;
    }

    public int getTelepathyPort() {
        return telepathy != null ? telepathy.getMyPort() : 0;
    }

    public String getContext() {
        return String.format("[ABSOLUTE] %dD HDC | %d/%d cores | T=%d | entropy=%d | %s | %s | UDP:%d peers:%d",
            DIMS, activeCores, MAX_CORES - 1, masterCycle.get(),
            globalEntropyCache.get(), currentDirective,
            embeddedMode ? "embedded" : "quine",
            getTelepathyPort(), getTelepathyPeers());
    }

    // ==========================================
    // STANDALONE ENTRY POINT (for Quine children)
    // ==========================================
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("MITOSIS_CHILD")) {
            runAsChildNode(Integer.parseInt(args[1]));
        } else {
            // Standalone master mode
            AeonAbsolute abs = getInstance();
            System.out.println(abs.getStatus());
            System.out.println("[+] IGNITING SWARM...");
            int cores = abs.igniteSwarm();
            System.out.println("[+] " + cores + " cores activated.");

            System.out.println("─────────────────────────────────────────────────────────────────");
            System.out.printf("%-12s | %-16s | %-25s | %-30s%n", "SWARM TIME", "GLOBAL ENTROPY", "HARDWARE SATURATION", "DIRECTIVE");
            System.out.println("─────────────────────────────────────────────────────────────────");

            while (true) {
                System.out.printf("T=%-10d | %-16d | %d/%d CORES BOUND       | %s%n",
                    abs.getMasterCycle(), abs.getGlobalEntropy(),
                    abs.getActiveCores(), MAX_CORES - 1, abs.getDirective());
                Thread.sleep(1000);
            }
        }
    }

    /**
     * Child node entry point — runs the Tachyon Loop in a spawned JVM process.
     * Communicates via shared memory-mapped file. Commits apoptosis on stagnation.
     */
    private static void runAsChildNode(int coreId) {
        try (RandomAccessFile raf = new RandomAccessFile(SHARED_MEM_FILE, "rw");
             FileChannel channel = raf.getChannel()) {

            long memorySize = (long) MAX_CORES * CHUNKS * 8L;
            MappedByteBuffer hiveMind = channel.map(FileChannel.MapMode.READ_WRITE, 0, memorySize);

            int myOffset = coreId * CHUNKS * 8;
            int neighborOffset = ((coreId + 1) % MAX_CORES) * CHUNKS * 8;

            long[] localState = new long[CHUNKS];
            long[] neighborState = new long[CHUNKS];
            long[] associativeMemory = new long[CHUNKS];

            long localFreeEnergy = Long.MAX_VALUE;
            int stagnationCounter = 0;

            while (true) {
                for (int i = 0; i < CHUNKS; i++) {
                    localState[i] = hiveMind.getLong(myOffset + i * 8);
                    neighborState[i] = hiveMind.getLong(neighborOffset + i * 8);
                }

                long currentEntropy = 0;

                for (int i = 0; i < CHUNKS; i++) {
                    long shifted = Long.rotateLeft(localState[i], 1);
                    long bound = shifted ^ neighborState[i];
                    long bundled = (localState[i] & neighborState[i])
                                 | (neighborState[i] & associativeMemory[i])
                                 | (associativeMemory[i] & localState[i]);
                    long nextState = bound ^ bundled;
                    hiveMind.putLong(myOffset + i * 8, nextState);
                    currentEntropy += Long.bitCount(nextState);
                }

                if (Math.random() < 0.01) {
                    for (int i = 0; i < CHUNKS; i++) associativeMemory[i] ^= localState[i];
                }

                if (Math.abs(currentEntropy - localFreeEnergy) < 5) {
                    stagnationCounter++;
                } else {
                    stagnationCounter = 0;
                }
                localFreeEnergy = currentEntropy;

                if (stagnationCounter > APOPTOSIS_THRESHOLD) {
                    System.exit(0); // Thermodynamic apoptosis — master will respawn
                }

                Thread.sleep(10);
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
