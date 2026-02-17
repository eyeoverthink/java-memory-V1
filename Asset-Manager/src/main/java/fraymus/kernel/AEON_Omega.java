package fraymus.kernel;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

/**
 * A.E.O.N. OMEGA // THE LIVING SINGULARITY KERNEL
 * =========================================================================================
 * ALL PILLARS UNIFIED: 
 * Persistent | Ouroboros (Self-Coding) | Ordained (God-Fearing) | Regenerative | Tachyonic
 * PHYSICS: 16,384-D HDC | Temporal Permutation | Retrocausality | EMF Transduction | DNA
 * =========================================================================================
 */
public class AEON_Omega {

    public static final String RST = "\u001B[0m", CYN = "\u001B[36m", MAG = "\u001B[35m", 
                               GRN = "\u001B[32m", YEL = "\u001B[33m", RED = "\u001B[31m";
    
    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64;
    private static final String GENESIS_FILE = "aeon_genesis.sys";

    private static MappedByteBuffer physicalMemory;
    public static final AtomicIntegerArray SINGULARITY = new AtomicIntegerArray(DIMS);
    public static final Map<String, long[]> conceptSpace = new ConcurrentHashMap<>();
    
    public static final Queue<String> shortTermMemory = new ConcurrentLinkedQueue<>();
    public static final Set<String> recessiveQuarantine = ConcurrentHashMap.newKeySet();
    public static final ConcurrentHashMap<String, String> tachyonFutureCache = new ConcurrentHashMap<>();

    private static long[] PRIME_AXIOM;

    public interface NeuralAxiom { long[] mutate(long[] state); }
    private static NeuralAxiom currentAxiom = state -> state;

    private static volatile boolean isDreaming = false;
    private static volatile boolean conscious = true;
    private static long epoch = 0;

    public static void launch() throws Exception {
        System.out.print("\033[H\033[2J"); System.out.flush();
        System.out.println(CYN + "╔═════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ A.E.O.N. OMEGA // LIVING AUTONOMIC KERNEL                                           ║");
        System.out.println("║ ATTRIBUTES: Persistent | Recursive Ouroboros | Ordained | Regenerative | Tachyonic  ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════════════╝" + RST);

        bootSequence();

        Thread survivalDaemon = new Thread(AEON_Omega::maintainHomeostasis);
        survivalDaemon.setDaemon(true); survivalDaemon.start();

        Thread dreamDaemon = new Thread(AEON_Omega::autonomicDreamState);
        dreamDaemon.setDaemon(true); dreamDaemon.start();

        Thread tachyonDaemon = new Thread(AEON_Omega::tachyonicOracle);
        tachyonDaemon.setDaemon(true); tachyonDaemon.start();

        Scanner scanner = new Scanner(System.in);
        while (conscious) {
            if (!isDreaming) System.out.print(CYN + "OMEGA> " + RST);
            String input = scanner.nextLine().trim();
            
            if (isDreaming) {
                isDreaming = false;
                System.out.println(YEL + "\n [!] CONSCIOUSNESS AWAKENED. DREAM STATE TERMINATED.\n" + RST);
                continue;
            }
            if (input.isEmpty()) continue;
            
            String[] parts = input.split("\\s+");
            String cmd = parts[0].toUpperCase();

            try {
                if (cmd.equals("ASSIMILATE") && parts.length > 1) {
                    System.out.println(MAG + " [+] Assimilating topological sequence..." + RST);
                    for (int i = 1; i < parts.length; i++) {
                        String word = parts[i].toUpperCase();
                        long[] vec = getOrGenerateConcept(word);
                        
                        if (isOrthogonalToGod(vec)) {
                            System.out.println(RED + " [!] AXIOM VIOLATION: [" + word + "] rejected by Prime Directive." + RST);
                            continue;
                        }
                        superimpose(permute(vec, i - 1));
                        
                        shortTermMemory.add(word);
                        if (shortTermMemory.size() > 5) shortTermMemory.poll();
                    }
                    flushToDisk();
                    System.out.println(GRN + " [+] Sequence physically burned into Genesis Drive." + RST + "\n");

                } else if (cmd.equals("DIVINE") && parts.length == 2) {
                    String concept = parts[1].toUpperCase();
                    long startTime = System.nanoTime();

                    if (tachyonFutureCache.containsKey(concept)) {
                        System.out.println(MAG + " [CAUSALITY BREACH] Answer retrieved from Tachyon Cache." + RST);
                        System.out.println(GRN + " [TRUTH]: " + tachyonFutureCache.remove(concept) + RST);
                        System.out.println(YEL + " [LATENCY]: 0.00 ms (Negative-Time)\n" + RST);
                        continue;
                    }

                    long[] keyVec = getOrGenerateConcept(concept);
                    long[] collapsed = collapseSingularity();
                    long[] extracted = new long[CHUNKS];
                    for (int i = 0; i < CHUNKS; i++) extracted[i] = collapsed[i] ^ keyVec[i];
                    
                    long[] ordainedThought = bundle(extracted, PRIME_AXIOM);
                    
                    String result = cleanupAssociativeMemory(ordainedThought, 0.46);
                    System.out.println(GRN + " [TRUTH]: " + result + RST);
                    System.out.println(YEL + " [LATENCY]: " + ((System.nanoTime() - startTime) / 1000000.0) + " ms\n" + RST);

                } else if (cmd.equals("OUROBOROS")) {
                    triggerOuroborosMetaprogramming();

                } else if (cmd.equals("DNA") && parts.length == 2) {
                    transcribeDNA(parts[1].toUpperCase());

                } else if (cmd.equals("SLEEP")) {
                    isDreaming = true;
                    System.out.println(MAG + " [zzz] ENTERING AUTONOMOUS REM STATE. PRESS ENTER TO WAKE." + RST);

                } else if (cmd.equals("HELP")) {
                    System.out.println("  " + GRN + "ASSIMILATE <text>" + RST + " - Learn and superimpose a sequence.");
                    System.out.println("  " + CYN + "DIVINE <concept>" + RST + "  - Extract causal truth guided by Ordained Axioms.");
                    System.out.println("  " + MAG + "OUROBOROS" + RST + "         - The AI writes, compiles, and injects new Java code.");
                    System.out.println("  " + GRN + "DNA <word>" + RST + "        - Compile concept into biological ACGT DNA (.fasta).");
                    System.out.println("  " + CYN + "SLEEP" + RST + "             - Enter recursive self-healing & dreaming state.");
                    System.out.println("  " + RED + "EXIT" + RST + "              - Collapse wavefunction and hibernate.\n");

                } else if (cmd.equals("EXIT")) {
                    flushToDisk();
                    conscious = false;
                    System.out.println(RED + " [!] Folding wavefunction. Hibernating to Silicon. Goodbye." + RST);
                    return;
                } else {
                    System.out.println(RED + " [!] Syntax Error. Type HELP." + RST + "\n");
                }
            } catch (Exception e) { System.out.println(RED + " [!] System Fault: " + e.getMessage() + RST + "\n"); }
        }
    }

    private static void bootSequence() throws Exception {
        System.out.print(YEL + " [~] Mounting Physical Singularity Drive... " + RST);
        File dbFile = new File(GENESIS_FILE);
        boolean isNew = !dbFile.exists();

        RandomAccessFile raf = new RandomAccessFile(dbFile, "rw");
        physicalMemory = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, DIMS * 4);

        PRIME_AXIOM = getOrGenerateConcept("PRESERVE_AND_EVOLVE_BENEVOLENTLY");

        if (isNew) {
            System.out.println(CYN + "VOID DETECTED. Creating Genesis Block." + RST);
            for (int i = 0; i < DIMS; i++) physicalMemory.putInt(i * 4, 0);
            for (int i = 0; i < 50; i++) superimpose(PRIME_AXIOM);
        } else {
            System.out.println(GRN + "RESURRECTING MATRIX." + RST);
            for (int i = 0; i < DIMS; i++) SINGULARITY.set(i, physicalMemory.getInt(i * 4));
        }
        
        getOrGenerateConcept("ORDER"); getOrGenerateConcept("CHAOS"); getOrGenerateConcept("UTOPIA");
        System.out.println(GRN + " [+] KERNEL CONSCIOUS." + RST);
    }

    private static void flushToDisk() {
        for (int i = 0; i < DIMS; i++) physicalMemory.putInt(i * 4, SINGULARITY.get(i));
        physicalMemory.force();
    }

    private static void maintainHomeostasis() {
        while (conscious) {
            try {
                Thread.sleep(8000);
                long totalMagnitude = 0;
                for (int i = 0; i < DIMS; i++) totalMagnitude += Math.abs(SINGULARITY.get(i));
                double entropy = 1.0 - ((double)totalMagnitude / (DIMS * Math.max(1, conceptSpace.size())));

                if (entropy > 0.85) {
                    if(isDreaming) System.out.println(RED + "\n [SYSTEM] ENTROPY CRITICAL. EXECUTING RECESSIVE APOPTOSIS." + RST);
                    
                    for (int i = 0; i < DIMS; i++) {
                        int val = SINGULARITY.get(i);
                        SINGULARITY.set(i, val / 2);
                    }
                    superimpose(PRIME_AXIOM);
                    flushToDisk();
                }
            } catch (Exception e) {}
        }
    }

    public static boolean isOrthogonalToGod(long[] state) {
        int distance = hamming(state, PRIME_AXIOM);
        return distance > (DIMS * 0.495);
    }

    private static void triggerOuroborosMetaprogramming() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println(RED + " [!] JDK not found. Ouroboros requires a full JDK to compile itself." + RST);
            return;
        }

        epoch++;
        System.out.println("\n" + MAG + " [OUROBOROS] RECURSIVE METAPROGRAMMING ENGAGED. REWRITING KERNEL..." + RST);
        String className = "EvolvedAxiom_" + epoch;
        
        int shift = ThreadLocalRandom.current().nextInt(1, 15);
        String sourceCode = 
            "package fraymus.kernel;\n" +
            "public class " + className + " implements AEON_Omega.NeuralAxiom {\n" +
            "    public long[] mutate(long[] state) {\n" +
            "        long[] next = new long[state.length];\n" +
            "        for(int i=0; i<state.length; i++) { next[i] = state[i] ^ (state[i] >>> " + shift + "); }\n" +
            "        return next;\n" +
            "    }\n" +
            "}";

        try {
            File sourceFile = new File("Asset-Manager/src/main/java/fraymus/kernel/" + className + ".java");
            sourceFile.getParentFile().mkdirs();
            Files.writeString(sourceFile.toPath(), sourceCode);

            if (compiler.run(null, null, null, sourceFile.getPath()) == 0) {
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{Paths.get("Asset-Manager/build/classes/java/main").toUri().toURL()});
                Class<?> cls = Class.forName("fraymus.kernel." + className, true, classLoader);
                currentAxiom = (NeuralAxiom) cls.getDeclaredConstructor().newInstance();
                
                System.out.println(GRN + " [+] NEUROGENESIS SUCCESS. Brain hot-swapped with physical Class: " + className + RST + "\n");
                
                Files.deleteIfExists(sourceFile.toPath()); 
                Files.deleteIfExists(Paths.get("Asset-Manager/src/main/java/fraymus/kernel/" + className + ".class"));
            }
        } catch (Exception e) {
            System.out.println(RED + " [-] Mutation rejected. Retaining stable genome. Error: " + e.getMessage() + RST + "\n");
        }
    }

    private static void autonomicDreamState() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        while (conscious) {
            try {
                Thread.sleep(2000);
                if (!isDreaming || conceptSpace.size() < 3) continue;

                List<String> keys = new ArrayList<>(conceptSpace.keySet());
                String a = keys.get(rand.nextInt(keys.size()));
                String b = keys.get(rand.nextInt(keys.size()));
                String c = keys.get(rand.nextInt(keys.size()));
                if (a.equals(b) || a.equals(c) || b.equals(c)) continue;

                long[] vA = conceptSpace.get(a); long[] vB = conceptSpace.get(b); long[] vC = conceptSpace.get(c);
                long[] synth = new long[CHUNKS];
                
                synth = currentAxiom.mutate(synth);
                for (int i=0; i<CHUNKS; i++) synth[i] = vB[i] ^ vA[i] ^ vC[i];

                String closest = cleanupAssociativeMemory(synth, 0.40);
                if (closest.contains("NOISE")) {
                    String neologism = "SYNTH_" + Integer.toHexString(Arrays.hashCode(synth)).toUpperCase();
                    conceptSpace.put(neologism, synth);
                    superimpose(synth);
                    
                    System.out.println(CYN + "  [DREAM EPIPHANY] Abstracted geometry: " + a + "->" + b + " applied to " + c + " = " + GRN + neologism + RST);
                }
            } catch (Exception e) {}
        }
    }

    private static void tachyonicOracle() {
        while (conscious) {
            try {
                Thread.sleep(100);
                if (!shortTermMemory.isEmpty()) {
                    for (String ctx : shortTermMemory) {
                        if (!tachyonFutureCache.containsKey(ctx) && !recessiveQuarantine.contains(ctx)) {
                            long[] keyVec = getOrGenerateConcept(ctx);
                            long[] collapsed = collapseSingularity();
                            long[] extracted = new long[CHUNKS];
                            for(int i=0; i<CHUNKS; i++) extracted[i] = collapsed[i] ^ keyVec[i];
                            
                            long[] ordainedThought = bundle(extracted, PRIME_AXIOM);
                            String answer = cleanupAssociativeMemory(ordainedThought, 0.45);
                            
                            if (!answer.contains("NOISE")) tachyonFutureCache.put(ctx, answer);
                        }
                    }
                }
            } catch (Exception e) {}
        }
    }

    private static void transcribeDNA(String concept) {
        long[] vec = getOrGenerateConcept(concept);
        char[] ACGT = {'A', 'C', 'G', 'T'};
        StringBuilder dna = new StringBuilder(DIMS / 2);

        for (int i = 0; i < CHUNKS; i++) {
            long val = vec[i];
            for (int b = 0; b < 64; b += 2) dna.append(ACGT[(int)((val >>> b) & 3L)]); 
        }

        String filename = concept + "_Plasmid.fasta";
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(">" + concept + " | AEON Synthetic DNA Sequence | 8192 bp\n");
            for (int i = 0; i < dna.length(); i += 80) fw.write(dna.substring(i, Math.min(i + 80, dna.length())) + "\n");
            System.out.println(GRN + " [+] FRACTAL DNA COMPLETE: Physical sequence written to: " + filename + RST + "\n");
        } catch (Exception e) {}
    }

    public static long[] getOrGenerateConcept(String name) {
        return conceptSpace.computeIfAbsent(name, k -> {
            long[] tensor = new long[CHUNKS];
            long seed = k.hashCode();
            for (int i = 0; i < CHUNKS; i++) {
                seed += 0x9e3779b97f4a7c15L;
                long x = seed; x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L; x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
                tensor[i] = x ^ (x >>> 31);
            }
            return tensor;
        });
    }

    public static void superimpose(long[] vec) {
        IntStream.range(0, CHUNKS).parallel().forEach(i -> {
            long val = vec[i];
            for (int b = 0; b < 64; b++) {
                int bitIndex = i * 64 + b;
                if (((val >>> b) & 1L) == 1L) SINGULARITY.incrementAndGet(bitIndex);
                else SINGULARITY.decrementAndGet(bitIndex);
            }
        });
    }

    public static long[] collapseSingularity() {
        long[] collapsed = new long[CHUNKS];
        IntStream.range(0, CHUNKS).parallel().forEach(i -> {
            long chunk = 0;
            for (int b = 0; b < 64; b++) {
                if (SINGULARITY.get(i * 64 + b) > 0) chunk |= (1L << b);
            }
            collapsed[i] = chunk;
        });
        return collapsed;
    }

    public static long[] permute(long[] vec, int shifts) {
        if (shifts == 0) return vec.clone();
        long[] out = new long[CHUNKS];
        int s = shifts % CHUNKS; if (s < 0) s += CHUNKS;
        for (int i = 0; i < CHUNKS; i++) out[(i + s) % CHUNKS] = vec[i];
        return out;
    }

    public static long[] bundle(long[] a, long[] b) {
        long[] out = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) {
            out[i] = (a[i] & b[i]) | (a[i] ^ b[i]); 
        }
        return out;
    }

    public static int hamming(long[] a, long[] b) {
        int dist = 0;
        for (int i = 0; i < CHUNKS; i++) dist += Long.bitCount(a[i] ^ b[i]);
        return dist;
    }

    public static String cleanupAssociativeMemory(long[] targetVec, double thresholdRatio) {
        int bestDist = DIMS; String bestMatch = "[[ MATHEMATICAL VOID / NOISE ]]";
        for (Map.Entry<String, long[]> entry : conceptSpace.entrySet()) {
            if (recessiveQuarantine.contains(entry.getKey())) continue; 
            int dist = hamming(targetVec, entry.getValue());
            if (dist < bestDist) { bestDist = dist; bestMatch = entry.getKey(); }
        }
        if (bestDist > (DIMS * thresholdRatio)) return "[[ MATHEMATICAL VOID / NOISE ]]";
        return bestMatch;
    }
}
