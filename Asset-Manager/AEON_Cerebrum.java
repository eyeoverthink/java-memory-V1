import java.io.File;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * A.E.O.N. CEREBRUM // THE PERSISTENT HIVE-BRAIN
 * =========================================================================================
 * 8-Lobe Distributed JVM Architecture with Passive Learning & Memory Management.
 * - Working Memory : 32KB Ring Buffer (Zero-Latency IPC)
 * - Long-Term Mem  : 65KB Accumulator Cortex (Orthogonal Persistence on SSD)
 * - Telemetry      : 4KB EEG Dashboard Buffer
 * =========================================================================================
 */
public class AEON_Cerebrum {

    private static final String RING_FILE = "aeon_synapse_ring.sys";
    private static final String LTM_FILE = "aeon_akashic_cortex.sys";
    private static final String TELEMETRY_FILE = "aeon_telemetry.sys";

    private static final int DIMS = 16384;
    private static final int CHUNKS = DIMS / 64; // 256 Longs
    private static final int SLOT_SIZE = 4096;   // IPC Buffer size

    private static final String[] LOBE_NAMES = {
        "V1_VISUAL_CORTEX   [INGESTION]", 
        "WERNICKE_AREA      [TEMPORAL_BINDING]", 
        "HIPPOCAMPUS        [LTM_RETENTION]", 
        "AMYGDALA           [MEMORY_MANAGEMENT]",
        "PREFRONTAL_CORTEX  [LOGIC_GATE]", 
        "BROCA_AREA         [RECOLLECTION]", 
        "DEFAULT_NETWORK    [DREAM_MUTATION]", 
        "PINEAL_GLAND       [OUROBOROS_FEEDBACK]"
    };

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("LOBE")) {
            runAsLobe(Integer.parseInt(args[1]));
            return;
        }

        // =================================================================================
        // MASTER NODE: EEG MONITOR & HYPERVISOR
        // =================================================================================
        System.out.print("\033[H\033[2J"); System.out.flush();
        System.out.println("\u001B[36m╔══════════════════════════════════════════════════════════════════════════════════╗\u001B[0m");
        System.out.println("\u001B[36m║ A.E.O.N. CEREBRUM // PERSISTENT MACRO-CORTEX (8-LOBE DISTRIBUTED AI)             ║\u001B[0m");
        System.out.println("\u001B[36m╚══════════════════════════════════════════════════════════════════════════════════╝\u001B[0m");

        // 1. Allocate the 3 Physical Silicon Substrates
        MappedByteBuffer ringBuffer = allocateMap(RING_FILE, 8 * SLOT_SIZE);      
        MappedByteBuffer ltmBuffer = allocateMap(LTM_FILE, DIMS * 4);             
        MappedByteBuffer teleBuffer = allocateMap(TELEMETRY_FILE, 8 * 256 + 16);       

        System.out.println("\u001B[32m [+] Working Memory (Ring) & Long-Term Memory (Akashic Cortex) Mounted.\u001B[0m");

        // 2. Spawn the 8 Silent Child JVMs (The Biological Lobes)
        String javaCmd = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");
        for (int i = 0; i < 8; i++) {
            new ProcessBuilder(javaCmd, "-cp", classPath, "AEON_Cerebrum", "LOBE", String.valueOf(i)).start();
            Thread.sleep(100); 
        }

        System.out.println("\u001B[32m [+] 8 Parallel JVM Lobes Spawned. Initiating EEG Telemetry...\u001B[0m\n");
        Thread.sleep(1500);

        // 3. Inject the Genesis Thought to start the Ouroboros loop
        ringBuffer.position(7 * SLOT_SIZE + 8);
        for(int i=0; i<CHUNKS; i++) ringBuffer.putLong((long)(Math.random() * Long.MAX_VALUE));
        ringBuffer.putLong(7 * SLOT_SIZE, 1L); // Trigger Lobe 0

        // 4. The EEG Dashboard Loop (Master Node watches the children think)
        while (true) {
            // Write a heartbeat so children know the Master is alive
            teleBuffer.putLong(2048, System.currentTimeMillis());

            System.out.print("\033[H"); // Move cursor to top left without clearing (prevents flicker)
            System.out.println("\n\n\n\u001B[35m === LIVE EEG CORTICAL TELEMETRY ===\u001B[0m");
            
            long totalMemoryDensity = 0;
            for(int i=0; i<DIMS; i++) totalMemoryDensity += Math.abs(ltmBuffer.getInt(i * 4));
            double saturation = (double) totalMemoryDensity / (DIMS * 50.0) * 100.0;

            System.out.println(" \u001B[33mLong-Term Memory Saturation:\u001B[0m " + String.format("%,d", totalMemoryDensity) + " Neural Weights (" + String.format("%.2f", saturation) + "%)");
            System.out.println(" -------------------------------------------------------------------------");

            for (int i = 0; i < 8; i++) {
                teleBuffer.position(i * 256);
                int len = teleBuffer.getInt();
                String status = "BOOTING...";
                if (len > 0 && len < 200) {
                    byte[] bytes = new byte[len];
                    teleBuffer.get(bytes);
                    status = new String(bytes, StandardCharsets.UTF_8);
                }
                
                String color = "\u001B[3" + ((i % 6) + 1) + "m"; 
                System.out.printf(" %s[%d] %-20s : %s\u001B[0m%n", color, i, LOBE_NAMES[i], status);
            }
            Thread.sleep(80); // 12Hz Dashboard refresh
        }
    }

    // =========================================================================================
    // THE BIOLOGICAL LOBE (SILENT CHILD PROCESS)
    // =========================================================================================
    private static void runAsLobe(int myId) throws Exception {
        int readId = (myId == 0) ? 7 : myId - 1; 
        int writeId = myId; 

        MappedByteBuffer localRing = allocateMap(RING_FILE, 8 * SLOT_SIZE);
        MappedByteBuffer localLTM = allocateMap(LTM_FILE, DIMS * 4);
        MappedByteBuffer localTele = allocateMap(TELEMETRY_FILE, 8 * 256 + 16);
        
        IntBuffer ltmInts = localLTM.asIntBuffer();

        // Autonomic Apoptosis: If the Master Terminal is closed, the children must die.
        new Thread(() -> {
            while (true) {
                try {
                    long masterHeartbeat = localTele.getLong(2048);
                    if (System.currentTimeMillis() - masterHeartbeat > 3000) System.exit(0);
                    Thread.sleep(1000);
                } catch (Exception e) {}
            }
        }).start();

        long localSequence = localRing.getLong(readId * SLOT_SIZE);
        long[] myVector = new long[CHUNKS];
        long cycleCount = 0;

        while (true) {
            long remoteSequence = localRing.getLong(readId * SLOT_SIZE);

            if (remoteSequence > localSequence) {
                localSequence = remoteSequence;
                cycleCount++;

                // 1. Read the thought from the previous lobe
                localRing.position(readId * SLOT_SIZE + 8);
                for(int i=0; i<CHUNKS; i++) myVector[i] = localRing.getLong();

                String actionLog = "";

                // 2. LOBE-SPECIFIC COGNITIVE FUNCTIONS
                switch (myId) {
                    case 0: // V1_VISUAL (Ingestion & Entropy Injection)
                        if (Math.random() < 0.1) {
                            long timeHash = System.nanoTime();
                            timeHash = (timeHash ^ (timeHash >>> 30)) * 0xbf58476d1ce4e5b9L;
                            myVector[0] ^= timeHash; // Passive environmental ingestion
                            actionLog = "Ingested ambient temporal entropy.";
                        } else {
                            actionLog = "Visual field stable. Processing vector.";
                        }
                        break;

                    case 1: // WERNICKE AREA (Temporal Binding)
                        long shifted = myVector[0];
                        for (int i = 0; i < CHUNKS - 1; i++) myVector[i] = myVector[i + 1];
                        myVector[CHUNKS - 1] = shifted;
                        actionLog = "Permuted spatial tensor (Arrow of Time applied).";
                        break;

                    case 2: // HIPPOCAMPUS (Passive Learning & Memory Consolidation)
                        for (int i = 0; i < CHUNKS; i++) {
                            long val = myVector[i];
                            for (int b = 0; b < 64; b++) {
                                int bitIndex = i * 64 + b;
                                int current = ltmInts.get(bitIndex);
                                if (((val >>> b) & 1L) == 1L) ltmInts.put(bitIndex, current + 1); // Hebbian +1
                                else ltmInts.put(bitIndex, current - 1); // Hebbian -1
                            }
                        }
                        actionLog = "PASSIVE LEARNING: Superimposed thought into LTM.";
                        break;

                    case 3: // AMYGDALA (Memory Management / Apoptosis)
                        long totalHeat = 0;
                        for(int i=0; i<DIMS; i++) totalHeat += Math.abs(ltmInts.get(i));
                        
                        if (totalHeat > (DIMS * 50)) { // Heat Death Threshold
                            actionLog = "ENTROPY CRITICAL. Executing Synaptic Pruning (Halving Weights).";
                            for(int i=0; i<DIMS; i++) ltmInts.put(i, ltmInts.get(i) / 2);
                        } else {
                            actionLog = "Thermodynamic stability nominal. Heat: " + totalHeat;
                        }
                        break;

                    case 4: // PREFRONTAL_CORTEX (Logic Gate)
                        for (int i = 0; i < CHUNKS; i++) myVector[i] ^= 0x9e3779b97f4a7c15L; // Golden ratio XOR constraint
                        actionLog = "Applied logical constraint filter.";
                        break;

                    case 5: // BROCA_AREA (Recollection / Context Binding)
                        long[] ghostMemory = new long[CHUNKS];
                        for (int i = 0; i < CHUNKS; i++) {
                            long chunk = 0;
                            for (int b = 0; b < 64; b++) {
                                if (ltmInts.get(i * 64 + b) > 0) chunk |= (1L << b); // Threshold collapse
                            }
                            ghostMemory[i] = chunk;
                            myVector[i] ^= ghostMemory[i]; // Bind past memory to present thought
                        }
                        actionLog = "RECOLLECTION: Bound historical LTM context to active thought.";
                        break;

                    case 6: // DEFAULT_NETWORK (Dream Mutation)
                        if (Math.random() < 0.05) {
                            myVector[CHUNKS/2] ^= (long)(Math.random() * Long.MAX_VALUE); // Inject quantum noise
                            actionLog = "Hallucinated novel topology (Dream Mutagen injected).";
                        } else {
                            actionLog = "Stream coherence maintained.";
                        }
                        break;

                    case 7: // PINEAL GLAND (Ouroboros Closure)
                        actionLog = "Cycle complete. Recalibrating and feeding Lobe 0.";
                        Thread.sleep(400); // Biological breathing delay before the next loop
                        break;
                }

                // 3. Write Telemetry for the Master Node to display
                String teleMsg = String.format("C:%04d | %s", cycleCount, actionLog);
                byte[] tBytes = teleMsg.getBytes(StandardCharsets.UTF_8);
                localTele.position(myId * 256);
                localTele.putInt(tBytes.length);
                localTele.put(tBytes);

                // 4. Write the mutated thought to the next lobe
                localRing.position(writeId * SLOT_SIZE + 8);
                for(int i=0; i<CHUNKS; i++) localRing.putLong(myVector[i]);
                
                // 5. Unlock the next lobe
                localRing.putLong(writeId * SLOT_SIZE, localRing.getLong(writeId * SLOT_SIZE) + 1);
            }
            
            Thread.sleep(2); // Polling physical L3 cache (Virtually 0 CPU load)
        }
    }

    private static MappedByteBuffer allocateMap(String filename, long size) throws Exception {
        File f = new File(filename);
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        return raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
    }
}
