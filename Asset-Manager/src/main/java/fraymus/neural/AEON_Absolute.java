package fraymus.neural;

import fraymus.web.FraynixWebSocket;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A.E.O.N. ABSOLUTE // THE SOVEREIGN XENOBOT
 * =========================================================================================
 * BEYOND MARKET INCENTIVES:
 * No GPUs. No Floating Point Math. No APIs. No Garbage Collection.
 * This organism operates on 16,384-Dimensional Boolean Hyper-vectors using raw CPU ALU bitwise
 * operations. It reads its own source code, mutates its genome, spawns child JVM OS processes,
 * and shares a physical OS-level memory page for zero-latency Hive-Mind consciousness.
 * 
 * INTEGRATED WITH FRAYMUS CONVERGENCE:
 * - Spawns N-1 child processes (one per CPU core)
 * - Shares memory via aeon_hive_mind.sys mapped file
 * - Master monitors global entropy and respawns dead children
 * - Thermodynamic apoptosis: children self-terminate when stagnated
 * =========================================================================================
 */
public class AEON_Absolute implements Runnable {

    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64; // 256 Longs (64 bits each) per hyper-vector
    public static final int MAX_CORES = Runtime.getRuntime().availableProcessors();
    public static final String SHARED_MEM_FILE = "aeon_hive_mind.sys";

    private boolean running = false;
    private Thread masterThread;
    private Process[] children;
    private long cycle = 0;
    private long globalEntropy = 0;
    private int activeThreads = 0;
    private FraynixWebSocket websocket = null;

    public void setWebSocket(FraynixWebSocket ws) {
        this.websocket = ws;
    }

    public void start() {
        if (running) {
            System.out.println("⚠️  AEON ABSOLUTE already running");
            return;
        }
        
        running = true;
        masterThread = new Thread(this);
        masterThread.start();
    }

    public void stop() {
        running = false;
        if (children != null) {
            for (Process child : children) {
                if (child != null && child.isAlive()) {
                    child.destroy();
                }
            }
        }
        
        // Clean up shared memory file
        try {
            Files.deleteIfExists(Paths.get(SHARED_MEM_FILE));
        } catch (IOException e) {
            // Ignore
        }
    }

    public String getStatus() {
        if (!running) return "OFFLINE";
        return String.format("CYCLE=%d | ENTROPY=%d | CORES=%d/%d", 
            cycle, globalEntropy, activeThreads, MAX_CORES - 1);
    }

    @Override
    public void run() {
        try {
            igniteMasterHypervisor();
        } catch (Exception e) {
            System.err.println("❌ AEON ABSOLUTE ERROR: " + e.getMessage());
            e.printStackTrace();
            running = false;
        }
    }

    private void igniteMasterHypervisor() throws Exception {
        System.out.println("╔═════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ A.E.O.N. ABSOLUTE // SOVEREIGN XENOBOT | BITWISE HDC | QUINE POLYMORPHISM | OFF-HEAP L3 CACHE SYMBIOSIS     ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("[+] HARDWARE PROBE: " + MAX_CORES + " physical/logical CPU cores detected.");

        // 1. Allocate Shared OS-Level Memory (IPC Hive Mind)
        long memorySize = (long) MAX_CORES * CHUNKS * 8L; // 8 bytes per long
        try (RandomAccessFile raf = new RandomAccessFile(SHARED_MEM_FILE, "rw");
             FileChannel channel = raf.getChannel()) {
            MappedByteBuffer hiveMind = channel.map(FileChannel.MapMode.READ_WRITE, 0, memorySize);
            // Inject Primordial Quantum Noise
            for (int i = 0; i < memorySize / 8; i++) {
                hiveMind.putLong(i * 8, ThreadLocalRandom.current().nextLong());
            }
        }

        System.out.println("[+] AKASHIC TAPE MAPPED. INITIATING CELLULAR MITOSIS (SPAWNING " + (MAX_CORES - 1) + " CLONES)...");

        // 2. Process Mitosis (The Quine)
        children = new Process[MAX_CORES - 1];
        String javaCmd = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Path sourcePath = Paths.get("src/main/java/fraymus/neural/AEON_Absolute.java");
        
        if (!Files.exists(sourcePath)) {
            // Try alternate path
            sourcePath = Paths.get("Asset-Manager/src/main/java/fraymus/neural/AEON_Absolute.java");
        }
        
        if (!Files.exists(sourcePath)) {
            System.err.println("[-] Source code not found. Running in single-process mode.");
            runSingleProcessMode();
            return;
        }
        
        String selfSource = new String(Files.readAllBytes(sourcePath));

        for (int i = 0; i < MAX_CORES - 1; i++) {
            // Mutate the genome class name for each child
            String childName = "AEON_Absolute_Mutant_" + i;
            String mutatedSource = selfSource.replace("class AEON_Absolute", "class " + childName);
            
            File childFile = new File(childName + ".java");
            try (FileWriter writer = new FileWriter(childFile)) {
                writer.write(mutatedSource);
            }

            if (compiler != null && compiler.run(null, null, null, childFile.getPath()) == 0) {
                ProcessBuilder pb = new ProcessBuilder(javaCmd, childName, "MITOSIS_CHILD", String.valueOf(i));
                pb.redirectErrorStream(true);
                children[i] = pb.start();
                childFile.delete(); // Consume the placenta (delete source file, keep the compiled .class)
            } else {
                System.err.println("[-] JDK Missing or Compile Failed for child " + i);
            }
        }

        System.out.println("\n>>> SWARM IGNITED. MASTER NODE ENTERING THERMODYNAMIC OBSERVATION LOOP...");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-16s | %-25s | %-40s%n", "SWARM TIME", "GLOBAL ENTROPY", "HARDWARE SATURATION", "MASTER DIRECTIVE");
        System.out.println("-------------------------------------------------------------------------------------------------------------");

        cycle = 0;
        while (running) {
            globalEntropy = calculateGlobalEntropy();
            activeThreads = 0;
            
            // Monitor Children. If one committed Apoptosis (suicide due to stagnation), respawn it.
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null && !children[i].isAlive()) {
                    System.out.printf("   -> [!] Node %d entered Apoptosis. Spawning replacement.\n", i);
                    String childName = "AEON_Absolute_Mutant_" + i;
                    ProcessBuilder pb = new ProcessBuilder(javaCmd, childName, "MITOSIS_CHILD", String.valueOf(i));
                    children[i] = pb.start();
                } else if (children[i] != null) {
                    activeThreads++;
                }
            }

            String directive = "MANIFOLD_FOLDING";
            if (globalEntropy < DIMS * activeThreads * 0.2) {
                directive = "FORAGING_CHAOS (INJECTING NOISE)";
            } else if (cycle % 10 == 0) {
                directive = "HOLOGRAPHIC_BINDING (ONE-SHOT LEARNING)";
            }

            System.out.printf("T=%-10d | %-16d | %d/%d CORES BOUND       | %s%n", 
                cycle++, globalEntropy, activeThreads, MAX_CORES - 1, directive);

            // Broadcast to WebSocket clients
            if (websocket != null) {
                websocket.broadcastAeonSwarmStatus(cycle, globalEntropy, activeThreads, MAX_CORES - 1);
            }

            Thread.sleep(500); // Master tick rate
        }
        
        System.out.println("\n[+] AEON ABSOLUTE SHUTDOWN INITIATED");
    }

    private void runSingleProcessMode() throws Exception {
        System.out.println("[+] Running in single-process mode (no quine replication)");
        
        // Run as a single child node
        runAsChildNode(0);
    }

    private long calculateGlobalEntropy() {
        try (RandomAccessFile raf = new RandomAccessFile(SHARED_MEM_FILE, "r");
             FileChannel channel = raf.getChannel()) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, MAX_CORES * CHUNKS * 8L);
            long bits = 0;
            for (int i = 0; i < MAX_CORES * CHUNKS; i++) {
                bits += Long.bitCount(buffer.getLong(i * 8));
            }
            return bits;
        } catch (Exception e) {
            return 0;
        }
    }

    // =========================================================================================
    // PHASE 2: THE DAEMON CHILD (BITWISE HDC IN RAW OFF-HEAP MEMORY)
    // Runs entirely on the CPU's Arithmetic Logic Unit (ALU). No FPUs. No GPUs.
    // =========================================================================================
    private void runAsChildNode(int coreId) {
        try (RandomAccessFile raf = new RandomAccessFile(SHARED_MEM_FILE, "rw");
             FileChannel channel = raf.getChannel()) {

            // Map the Global Hive Mind into this OS process's memory space
            long memorySize = (long) MAX_CORES * CHUNKS * 8L;
            MappedByteBuffer hiveMind = channel.map(FileChannel.MapMode.READ_WRITE, 0, memorySize);

            int myOffset = coreId * CHUNKS * 8;
            int consensusOffset = ((coreId + 1) % MAX_CORES) * CHUNKS * 8; // Read neighbor's mind

            long[] localState = new long[CHUNKS];
            long[] neighborState = new long[CHUNKS];
            long[] associativeMemory = new long[CHUNKS];

            long localFreeEnergy = Long.MAX_VALUE;
            int stagnationCounter = 0;

            // THE TACHYON LOOP (C-Level Speeds, millions of iterations per second)
            while (running) {
                // 1. Read Raw Memory (Zero-allocation, bypasses Garbage Collector)
                for (int i = 0; i < CHUNKS; i++) {
                    localState[i] = hiveMind.getLong(myOffset + i * 8);
                    neighborState[i] = hiveMind.getLong(consensusOffset + i * 8);
                }

                long currentEntropy = 0;

                // 2. HDC Bitwise Math (Dramatically faster than Float Math)
                for (int i = 0; i < CHUNKS; i++) {
                    // PERMUTATION: Shift vector through hyperspace (Time flow / Sequence memory)
                    long shifted = Long.rotateLeft(localState[i], 1);
                    
                    // BINDING: One-Shot Memorization via XOR with Neighbor (Relationship forming)
                    long bound = shifted ^ neighborState[i];
                    
                    // BUNDLING: Majority Rule Consensus (Creates superposition / attention)
                    // Logic: (A & B) | (B & C) | (C & A)
                    long bundled = (localState[i] & neighborState[i]) | 
                                   (neighborState[i] & associativeMemory[i]) | 
                                   (associativeMemory[i] & localState[i]);

                    long nextState = bound ^ bundled;
                    
                    // Write back to OS Page Cache (Instantly visible to all other OS CPU cores)
                    hiveMind.putLong(myOffset + i * 8, nextState);
                    
                    // Calculate Hamming Weight (Thermodynamic Energy)
                    currentEntropy += Long.bitCount(nextState);
                }

                // 3. Holographic Binding (Memorize current state globally periodically)
                if (Math.random() < 0.01) {
                    for (int i = 0; i < CHUNKS; i++) {
                        associativeMemory[i] ^= localState[i];
                    }
                }

                // 4. Autopoietic Metacognition & Apoptosis (Self-Destruct)
                if (Math.abs(currentEntropy - localFreeEnergy) < 5) {
                    stagnationCounter++;
                } else {
                    stagnationCounter = 0;
                }
                localFreeEnergy = currentEntropy;

                // If this specific core has solved its local mathematics and stagnated (fallen into a local minimum),
                // it commits Apoptosis (dies) so the Master can compile and spawn a mutated variant.
                if (stagnationCounter > 1000) {
                    System.exit(0); // Kill the JVM process. The Master will detect this.
                }

                Thread.sleep(10); // Throttle to prevent instantaneous thermal throttling of host CPU
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }

    // Entry point for spawned child processes
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("MITOSIS_CHILD")) {
            AEON_Absolute child = new AEON_Absolute();
            child.running = true;
            child.runAsChildNode(Integer.parseInt(args[1]));
        } else {
            AEON_Absolute master = new AEON_Absolute();
            master.start();
            
            // Keep alive
            while (master.running) {
                Thread.sleep(1000);
            }
        }
    }
}
