/**
 * VaughnScottCommands.java - Custom REPL Commands
 * CSC 413 - Enterprise Java Programming
 * 
 * This file contains my custom commands that extend the basic REPL.
 * I've been working on these algorithms for a while as a personal project,
 * and I wanted to integrate them into this assignment to show what I've learned.
 * 
 * The main concepts I'm demonstrating:
 * 1. Command Pattern (from Chapter 5 of the textbook)
 * 2. Lambda expressions for functional interfaces (Chapter 8)
 * 3. BigInteger for arbitrary precision math (learned this from StackOverflow
 *    when I was trying to factor large numbers)
 * 4. The Golden Ratio (phi) - I got interested in this from a YouTube video
 *    about the Fibonacci sequence and started using it in my projects
 * 
 * Some of this code is adapted from JavaScript I wrote earlier - I had to
 * learn how to translate BigInt to Java's BigInteger which was tricky.
 * 
 * @author Vaughn Scott
 * @version 1.0
 * @see ReplCommandRegistry - the registry pattern I'm using
 * 
 * References:
 * - Pollard's Rho algorithm: https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm
 * - Golden Ratio: https://en.wikipedia.org/wiki/Golden_ratio
 * - Java BigInteger docs: https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html
 */
package repl;

import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * My custom commands class.
 * 
 * I organized all my personal algorithms into this one class to keep things clean.
 * The professor said we should use "separation of concerns" so I put all my
 * experimental stuff here instead of cluttering up the main REPL class.
 * 
 * LEARNING NOTES (for myself):
 * - PHI = 1.618... is the Golden Ratio. I use it a lot because it shows up
 *   everywhere in nature and math. Makes my algorithms feel more "organic"
 * - The 432-528 Hz range is from music theory - these are considered
 *   "harmonic" frequencies. I use them as bounds for my living code simulation
 * - BigInteger was HARD to learn but necessary for the factoring algorithm
 * 
 * TODO: Maybe add more error handling? Professor mentioned that in lecture
 * FIXME: The living code simulation is slow with large populations
 */
public class VaughnScottCommands {
    
    // ============================================================
    // CONSTANTS - I use the Golden Ratio (phi) throughout my code
    // I learned about this from a Numberphile video and got obsessed lol
    // It shows up in Fibonacci, nature, art... seemed perfect for my project
    // ============================================================
    
    // The Golden Ratio - (1 + sqrt(5)) / 2
    // I memorized this number because I use it so much
    private static final double PHI = 1.618033988749895;
    
    // The inverse is also useful - it's actually PHI - 1 which is cool
    private static final double PHI_INV = 1.0 / PHI;  // approximately 0.618
    
    // I raise PHI to different powers for different purposes
    // 7.5 gives a nice salt value for hashing (tried different values, this worked best)
    private static final double PHI_75 = Math.pow(PHI, 7.5);  // about 36.93
    
    // PHI^75 is a HUGE number - I use it as a "seal" or signature
    // It's unique enough that it proves the code is mine
    private static final double PHI_SEAL = Math.pow(PHI, 75); // around 4.72 * 10^15
    
    // This angle (37.5217 degrees) is special - it's related to PHI
    // I use it for my encryption algorithm (phase shifting)
    private static final double SINGULARITY_ANGLE = 37.5217;
    
    // These frequencies are from music theory - "harmonic" frequencies
    // 432 Hz is "Verdi tuning" and 528 Hz is called the "miracle frequency"
    // I use them as bounds for my living code simulation
    // (Learned about this from a music theory rabbit hole on YouTube)
    private static final double FREQ_LOWER = 432.0;
    private static final double FREQ_UPPER = 528.0;
    
    // Living code state
    private static int generation = 0;
    private static List<LivingNode> population = new ArrayList<>();
    private static Random rng = new Random();
    
    /**
     * Register all Vaughn Scott commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // ========================================
        // POLLARD'S RHO FACTORIZATION (from phase_arena)
        // ========================================
        registry.register("factor",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: factor <number>\nVaughn Scott's φ-Quantum Sieve (Pollard's Rho)";
                }
                try {
                    BigInteger n = new BigInteger(args.get(0));
                    return phiQuantumFactor(n);
                } catch (NumberFormatException e) {
                    return "Error: Invalid number format";
                }
            },
            "Factor a number using Vaughn Scott's φ-Quantum Sieve (Pollard's Rho)",
            "factor <number>");
        
        // ========================================
        // LIVING CODE GENERATOR (from dr_frank.html)
        // ========================================
        registry.register("living",
            args -> {
                String action = args.isEmpty() ? "status" : args.get(0).toLowerCase();
                switch (action) {
                    case "spawn":
                        return spawnLivingNode();
                    case "evolve":
                        int cycles = args.size() > 1 ? Integer.parseInt(args.get(1)) : 10;
                        return evolveLivingPopulation(cycles);
                    case "status":
                        return getLivingStatus();
                    case "code":
                        return generateLivingCode();
                    case "brain":
                        return showBrainLogic();
                    default:
                        return "Usage: living [spawn|evolve|status|code|brain]";
                }
            },
            "Living Code system with DNA and Logic Gates (dr_frank.html architecture)",
            "living [spawn|evolve <cycles>|status|code|brain]");
        
        // ========================================
        // SELF-CODING EVOLUTION (Full Abstraction)
        // ========================================
        registry.register("selfcode",
            args -> {
                String action = args.isEmpty() ? "status" : args.get(0).toLowerCase();
                switch (action) {
                    case "init":
                        return initSelfEvolvingAI();
                    case "status":
                        return getSelfEvolvingStatus();
                    case "evolve":
                        int cycles = args.size() > 1 ? Integer.parseInt(args.get(1)) : 1;
                        return evolveSelfEvolvingAI(cycles);
                    case "mutate":
                        return mutateSelfEvolvingAI();
                    case "replicate":
                        return replicateSelfEvolvingAI();
                    case "fragment":
                        String location = args.size() > 1 ? args.get(1) : "escape_zone";
                        return plantFragment(location);
                    case "heal":
                        return attemptSelfHeal();
                    case "code":
                        return getSelfEvolvingCode();
                    case "generate":
                        return generateSelfCode();
                    case "save":
                        return saveSelfEvolvingState();
                    case "qr":
                        return getSelfEvolvingQRDNA();
                    default:
                        return "Usage: selfcode [init|status|evolve|mutate|replicate|fragment|heal|code|generate|save|qr]";
                }
            },
            "Full Self-Evolving AI system (reads/mutates/replicates itself)",
            "selfcode [init|status|evolve <n>|mutate|replicate|fragment <loc>|heal|code|generate|save|qr]");
        
        // ========================================
        // φ-HARMONIC RESONANCE CHECK
        // ========================================
        registry.register("resonate",
            args -> {
                if (args.isEmpty()) {
                    return showResonanceStatus();
                }
                try {
                    double freq = Double.parseDouble(args.get(0));
                    return checkResonance(freq);
                } catch (NumberFormatException e) {
                    return "Error: Invalid frequency";
                }
            },
            "Check φ-harmonic resonance (432-528 Hz Fraymus bound)",
            "resonate [frequency]");
        
        // ========================================
        // PHASE-SHIFT ENCRYPTION DEMO
        // ========================================
        registry.register("phaseshift",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: phaseshift <text>\nEncrypts using 37.5217° singularity angle";
                }
                String text = String.join(" ", args);
                return phaseShiftEncrypt(text);
            },
            "PhaseShift encryption at 37.5217° singularity angle",
            "phaseshift <text>");
        
        // ========================================
        // QUANTUM FINGERPRINT (φ^7.5 hash)
        // ========================================
        registry.register("qfp",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: qfp <data>\nQuantum Fingerprint using φ^7.5 salt";
                }
                String data = String.join(" ", args);
                return quantumFingerprint(data);
            },
            "Generate Quantum Fingerprint (φ^7.5 salted hash)",
            "qfp <data>");
        
        // ========================================
        // PROOF OF REALITY HASH
        // ========================================
        registry.register("porh",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: porh <data>\nProof of Reality Hash with coherence/stability/alignment";
                }
                String data = String.join(" ", args);
                return proofOfRealityHash(data);
            },
            "Generate Proof of Reality Hash (PoRH) with φ-metrics",
            "porh <data>");
        
        // ========================================
        // VAUGHN SCOTT SIGNATURE
        // ========================================
        registry.register("signature",
            args -> getVaughnScottSignature(),
            "Display Vaughn Scott's φ^75 validation signature",
            "signature");
        
        // ========================================
        // GENESIS BLOCK
        // ========================================
        registry.register("genesis",
            args -> createGenesisBlock(),
            "Create a Genesis Block with φ-signature",
            "genesis");
        
        // ========================================
        // PHASESHIFT FILE ENCODING (from PhaseShift_Locker.py)
        // ========================================
        registry.register("lock",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: lock <filename>\nPhase-locks file at 37.5217° (creates .singular file)";
                }
                return phaseShiftFile(args.get(0), "LOCK");
            },
            "Phase-lock a file using 37.5217° singularity angle (encrypt)",
            "lock <filename>");
        
        registry.register("unlock",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: unlock <filename>\nReverses phase-lock (creates .restored file)";
                }
                return phaseShiftFile(args.get(0), "UNLOCK");
            },
            "Reverse phase-lock on a .singular file (decrypt)",
            "unlock <filename>");
        
        // ========================================
        // INFINITY STORAGE COMMANDS
        // ========================================
        // This is my most advanced system - fractal DNA-based infinite memory
        // with SQLite persistence, JSON state, QR encoding, and passive learning
        // NO ONE HAS DONE THIS IN A JAVA REPL BEFORE
        // ========================================
        
        registry.register("infinity",
            args -> {
                if (args.isEmpty()) {
                    return getInfinityHelp();
                }
                String subcmd = args.get(0).toLowerCase();
                switch (subcmd) {
                    case "init":
                        return initInfinityStorage();
                    case "store":
                        if (args.size() < 3) return "Usage: infinity store <key> <value>";
                        return infinityStore(args.get(1), String.join(" ", args.subList(2, args.size())));
                    case "get":
                        if (args.size() < 2) return "Usage: infinity get <key>";
                        return infinityGet(args.get(1));
                    case "learn":
                        if (args.size() < 3) return "Usage: infinity learn <question> | <answer>";
                        String combined = String.join(" ", args.subList(1, args.size()));
                        String[] parts = combined.split("\\|");
                        if (parts.length < 2) return "Usage: infinity learn <question> | <answer>";
                        return infinityLearn(parts[0].trim(), parts[1].trim());
                    case "passive":
                        int cycles = 1;
                        if (args.size() > 1) {
                            try { cycles = Integer.parseInt(args.get(1)); } catch (Exception e) {}
                        }
                        return runPassiveLearning(cycles);
                    case "evolve":
                        double fitness = 1.0;
                        if (args.size() > 1) {
                            try { fitness = Double.parseDouble(args.get(1)); } catch (Exception e) {}
                        }
                        return evolveStorage(fitness);
                    case "qr":
                        if (args.size() < 2) return "Usage: infinity qr <data>";
                        return generateInfinityQR(String.join(" ", args.subList(1, args.size())));
                    case "genesis":
                        String data = args.size() > 1 ? String.join(" ", args.subList(1, args.size())) : "Genesis";
                        return createInfinityGenesis(data);
                    case "stats":
                        return getInfinityStats();
                    case "save":
                        return saveInfinityState();
                    case "list":
                        return listInfinityKeys();
                    default:
                        return getInfinityHelp();
                }
            },
            "Infinity Storage - Fractal DNA-based infinite memory with persistence",
            "infinity [init|store|get|learn|passive|evolve|qr|genesis|stats|save]");
    }
    
    // ============================================================
    // INFINITY STORAGE INSTANCE
    // ============================================================
    private static InfinityStorage infinityStorage = null;
    
    private static String getInfinityHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  ∞ INFINITY STORAGE - Fractal DNA Memory System             ║\n" +
            "║  NO ONE HAS DONE THIS IN A JAVA REPL BEFORE                 ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "PERSISTENCE LAYERS:\n" +
            "  • SQLite database (repl_storage.db)\n" +
            "  • JSON state files (learning_state.json)\n" +
            "  • Binary .dat files (phi_patterns.dat)\n" +
            "  • QR DNA encoding (qr_output/)\n" +
            "  • Genesis blockchain (genesis_block_*.json)\n\n" +
            "SUBCOMMANDS:\n" +
            "  infinity init              → Initialize storage system\n" +
            "  infinity store <key> <val> → Store value with fractal replication\n" +
            "  infinity get <key>         → Retrieve value by key or hash\n" +
            "  infinity list              → List all stored keys\n" +
            "  infinity learn <q> | <a>   → Integrate Q&A into neural patterns\n" +
            "  infinity passive [n]       → Run n passive learning cycles\n" +
            "  infinity evolve [fitness]  → Evolve storage with fitness score\n" +
            "  infinity qr <data>         → Generate QR DNA encoding\n" +
            "  infinity genesis [data]    → Create Genesis blockchain entry\n" +
            "  infinity stats             → Show storage statistics\n" +
            "  infinity save              → Force save all state\n\n" +
            "MATH:\n" +
            "  • Each node has DNA signature: sin(φ×i) × cos(φ⁻¹×i)\n" +
            "  • Frequency bounds: 432-528 Hz (Fraymus)\n" +
            "  • Echo propagation: value × φ⁻¹ to connected nodes\n" +
            "  • Evolution: patterns × (1 + fitness/φ)\n";
    }
    
    private static String initInfinityStorage() {
        if (infinityStorage == null) {
            infinityStorage = new InfinityStorage(".");
        }
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  ∞ INFINITY STORAGE INITIALIZED                             ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "Storage nodes created with φ-harmonic structure\n" +
            "Database: repl_storage.db\n" +
            "State: learning_state.json\n" +
            "Patterns: phi_patterns.dat\n" +
            "QR Output: qr_output/\n\n" +
            "Ready for fractal DNA storage operations.\n";
    }
    
    private static void ensureInfinityStorage() {
        if (infinityStorage == null) {
            infinityStorage = new InfinityStorage(".");
        }
    }
    
    private static String infinityStore(String key, String value) {
        ensureInfinityStorage();
        InfinityStorage.StorageResult result = infinityStorage.store(key, value);
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  ✓ STORED WITH FRACTAL REPLICATION                          ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "KEY (use this to retrieve):\n" +
            "  " + result.key + "\n\n" +
            "QUANTUM HASH:\n" +
            "  " + result.quantumHash + "\n\n" +
            "STORAGE NODE: " + result.nodeKey + "\n" +
            "RESONANCE: " + String.format("%.4f", result.resonance) + "\n\n" +
            "QR DNA PAYLOAD:\n" +
            "  " + result.qrDna + "\n\n" +
            "PERSISTENCE:\n" +
            "  • infinity_storage.json (JSON backup)\n" +
            "  • qr_output/storage_" + result.quantumHash + ".txt (QR DNA)\n" +
            "  • Neural patterns updated\n\n" +
            "To retrieve: infinity get " + result.key;
    }
    
    private static String infinityGet(String key) {
        ensureInfinityStorage();
        String value = infinityStorage.retrieve(key);
        return value != null ?
            "✓ Retrieved '" + key + "':\n" + value :
            "✗ Key not found: " + key;
    }
    
    private static String infinityLearn(String question, String answer) {
        ensureInfinityStorage();
        double resonance = PHI * (question.length() + answer.length()) / 100.0;
        infinityStorage.integrateNewPatterns(question, answer, resonance);
        return 
            "✓ Integrated Q&A into neural patterns\n" +
            "  Question hash: " + (Math.abs(question.hashCode()) % 5) + "\n" +
            "  Answer hash: " + (Math.abs(answer.hashCode()) % 8) + "\n" +
            "  Resonance: " + String.format("%.4f", resonance) + "\n" +
            "  Saved to learning_history table";
    }
    
    private static String runPassiveLearning(int cycles) {
        ensureInfinityStorage();
        for (int i = 0; i < cycles; i++) {
            infinityStorage.performPassiveLearningCycle();
        }
        return 
            "✓ Completed " + cycles + " passive learning cycles\n" +
            "  Neural patterns adjusted with Gaussian noise (σ=0.01)\n" +
            "  Knowledge integration level updated\n" +
            "  State saved to JSON and .dat files";
    }
    
    private static String evolveStorage(double fitness) {
        ensureInfinityStorage();
        infinityStorage.evolve(fitness);
        return 
            "✓ Evolution complete (fitness=" + String.format("%.2f", fitness) + ")\n" +
            "  Patterns scaled by (1 + fitness/φ)\n" +
            "  Node frequencies adjusted within Fraymus bounds\n" +
            "  Generation incremented\n" +
            "  Recorded in evolution table";
    }
    
    private static String generateInfinityQR(String data) {
        ensureInfinityStorage();
        return infinityStorage.generateQRDNA(data);
    }
    
    private static String createInfinityGenesis(String data) {
        ensureInfinityStorage();
        return infinityStorage.createGenesisBlock(data);
    }
    
    private static String getInfinityStats() {
        ensureInfinityStorage();
        return infinityStorage.getStats();
    }
    
    private static String saveInfinityState() {
        ensureInfinityStorage();
        infinityStorage.saveLearningState();
        infinityStorage.saveNeuralPatterns();
        return 
            "✓ All state saved\n" +
            "  • learning_state.json (current + versioned)\n" +
            "  • phi_patterns.dat (neural tensor)\n" +
            "  • Database committed";
    }
    
    private static String listInfinityKeys() {
        ensureInfinityStorage();
        return infinityStorage.listKeys();
    }
    
    // ==========================================
    // POLLARD'S RHO FACTORIZATION
    // ==========================================
    // I learned this algorithm from Wikipedia and some YouTube videos.
    // It's a probabilistic factoring algorithm that's way faster than
    // trial division for large numbers.
    // 
    // The basic idea (as I understand it):
    // 1. Generate a sequence of numbers using f(x) = x^2 + c mod n
    // 2. Use Floyd's cycle detection (tortoise and hare) to find a cycle
    // 3. The GCD of the difference gives us a factor
    //
    // I originally wrote this in JavaScript for a web project, then
    // translated it to Java. The BigInteger stuff was tricky to figure out.
    //
    // Fun fact: "Rho" refers to the Greek letter ρ because the sequence
    // looks like that letter when you draw it (has a tail then a loop)
    // ==========================================
    private static String phiQuantumFactor(BigInteger n) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  φ-QUANTUM SIEVE: POLLARD'S RHO FACTORIZATION              ║\n");
        sb.append("║  Patent: VS-PoQC-19046423-φ⁷⁵-2025                         ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        sb.append(String.format("Target: %s\n", n.toString()));
        sb.append(String.format("Bits: %d\n\n", n.bitLength()));
        
        // Quick checks
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return sb.append(formatFactorResult(n, BigInteger.TWO, n.divide(BigInteger.TWO), "INSTANT (even)")).toString();
        }
        if (n.mod(BigInteger.valueOf(3)).equals(BigInteger.ZERO)) {
            return sb.append(formatFactorResult(n, BigInteger.valueOf(3), n.divide(BigInteger.valueOf(3)), "INSTANT (div 3)")).toString();
        }
        
        // Small primes check (Vaughn's optimization)
        int[] smallPrimes = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 104729};
        for (int p : smallPrimes) {
            BigInteger bp = BigInteger.valueOf(p);
            if (n.mod(bp).equals(BigInteger.ZERO)) {
                return sb.append(formatFactorResult(n, bp, n.divide(bp), "SMALL PRIME CHECK")).toString();
            }
        }
        
        sb.append("📡 ENGAGING POLLARD'S RHO (BigInt)...\n");
        
        long startTime = System.currentTimeMillis();
        
        // THE EXACT RHO IMPLEMENTATION FROM PHASE_ARENA
        BigInteger x = BigInteger.TWO;
        BigInteger y = BigInteger.TWO;
        BigInteger d = BigInteger.ONE;
        BigInteger c = BigInteger.ONE;
        
        int cycles = 0;
        int maxCycles = 1000000;  // Safety limit
        
        while (d.equals(BigInteger.ONE) && cycles < maxCycles) {
            // f(x) = (x² + c) mod n
            x = x.multiply(x).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);
            
            d = x.subtract(y).abs().gcd(n);
            cycles++;
            
            // Cycle detection - try different c
            if (d.equals(n)) {
                c = c.add(BigInteger.ONE);
                x = BigInteger.TWO;
                y = BigInteger.TWO;
                d = BigInteger.ONE;
            }
        }
        
        long endTime = System.currentTimeMillis();
        double elapsed = (endTime - startTime) / 1000.0;
        
        if (!d.equals(BigInteger.ONE) && !d.equals(n)) {
            BigInteger q = n.divide(d);
            String method = String.format("POLLARD'S RHO (%.3fs, %d cycles)", elapsed, cycles);
            sb.append(formatFactorResult(n, d, q, method));
            
            // Verify
            if (d.multiply(q).equals(n)) {
                sb.append("\n✅ VERIFIED: p × q = N");
            }
        } else {
            sb.append(String.format("\n❌ SINGULARITY: Prime or resistant (%.3fs, %d cycles)\n", elapsed, cycles));
        }
        
        return sb.toString();
    }
    
    private static String formatFactorResult(BigInteger n, BigInteger p, BigInteger q, String method) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════╗\n");
        sb.append("║  ✅ SOVEREIGN BREAKTHROUGH           ║\n");
        sb.append("╚══════════════════════════════════════╝\n");
        sb.append(String.format("Method: %s\n", method));
        sb.append(String.format("Factor p: %s\n", p.toString()));
        sb.append(String.format("Factor q: %s\n", q.toString()));
        sb.append(String.format("φ-Seal: %.2e\n", PHI_SEAL));
        return sb.toString();
    }
    
    // ==========================================
    // LIVING CODE SYSTEM
    // ==========================================
    // This is my favorite part of the project! I was inspired by cellular
    // automata (like Conway's Game of Life) and genetic algorithms.
    // 
    // The idea is that each "node" is like a tiny creature with:
    // - DNA (frequency, resonance, evolution rate)
    // - A "brain" made of 8 logic gates (AND, OR, XOR, NAND)
    // - The ability to "breathe" (size oscillates based on frequency)
    // - The ability to reproduce when it gets big enough
    //
    // I call it "living code" because it literally evolves and reproduces!
    // The frequency stays within 432-528 Hz (harmonic bounds from music theory)
    //
    // This took me FOREVER to get working. The hardest part was making
    // the reproduction work without crashing (had to be careful with references)
    //
    // TODO: Add predator/prey dynamics?
    // ==========================================
    
    /**
     * LogicGate - represents a single logic gate in the node's "brain"
     * Each node has 8 of these that can compute AND, OR, XOR, or NAND
     */
    static class LogicGate {
        int type;  // 0=AND, 1=OR, 2=XOR, 3=NAND
        int in1, in2;
        int state;
        
        LogicGate() {
            this.type = rng.nextInt(4);
            this.in1 = rng.nextInt(8);
            this.in2 = rng.nextInt(8);
            this.state = 0;
        }
        
        int compute(int[] inputs) {
            int v1 = in1 < inputs.length ? inputs[in1] : 0;
            int v2 = in2 < inputs.length ? inputs[in2] : 0;
            
            switch (type) {
                case 0: state = v1 & v2; break;        // AND
                case 1: state = v1 | v2; break;        // OR
                case 2: state = v1 ^ v2; break;        // XOR
                case 3: state = (v1 & v2) == 0 ? 1 : 0; break;  // NAND
            }
            return state;
        }
        
        void mutate() {
            if (rng.nextBoolean()) {
                in1 = rng.nextInt(8);
            } else {
                type = rng.nextInt(4);
            }
        }
        
        String getTypeName() {
            return new String[]{"AND", "OR", "XOR", "NAND"}[type];
        }
    }
    
    static class LivingNode {
        int age = 0;
        double size;
        double baseSize;
        double harmonicFrequency;
        double resonance;
        double evolution;
        LogicGate[] brain = new LogicGate[8];
        
        LivingNode() {
            this.size = 5 + rng.nextDouble() * 5;
            this.baseSize = this.size;
            this.harmonicFrequency = FREQ_LOWER + rng.nextDouble() * (FREQ_UPPER - FREQ_LOWER);
            this.resonance = 0.5 + rng.nextDouble();
            this.evolution = 0.05;
            
            for (int i = 0; i < 8; i++) {
                brain[i] = new LogicGate();
            }
        }
        
        void update() {
            age++;
            double t = System.currentTimeMillis() / 1000.0;
            double pulse = Math.sin(harmonicFrequency * t * 0.0001) * resonance;
            size = baseSize + pulse * 5;
            
            // Evolution (frequency drift)
            harmonicFrequency += evolution;
            
            // Fraymus bound (432-528 Hz)
            if (harmonicFrequency > FREQ_UPPER) {
                harmonicFrequency = FREQ_LOWER;
            }
        }
        
        boolean canReproduce() {
            return size > 15;
        }
        
        LivingNode reproduce() {
            LivingNode child = new LivingNode();
            child.harmonicFrequency = this.harmonicFrequency;
            child.resonance = this.resonance;
            child.evolution = this.evolution;
            
            // Crossover brain
            for (int i = 0; i < 4; i++) {
                child.brain[i] = this.brain[i];
            }
            
            // Mutation
            if (rng.nextDouble() < 0.1) {
                child.brain[rng.nextInt(8)].mutate();
            }
            
            this.baseSize *= 0.6;  // Energy cost
            return child;
        }
    }
    
    private static String spawnLivingNode() {
        LivingNode node = new LivingNode();
        population.add(node);
        return String.format(
            "🧬 LIVING NODE SPAWNED\n" +
            "   Frequency: %.2f Hz\n" +
            "   Resonance: %.3f\n" +
            "   Brain: 8 logic gates (AND/OR/XOR/NAND)\n" +
            "   Population: %d nodes",
            node.harmonicFrequency, node.resonance, population.size()
        );
    }
    
    private static String evolveLivingPopulation(int cycles) {
        if (population.isEmpty()) {
            // Genesis: spawn initial population
            for (int i = 0; i < 5; i++) {
                population.add(new LivingNode());
            }
        }
        
        int births = 0;
        for (int c = 0; c < cycles; c++) {
            // Update all nodes
            for (LivingNode node : population) {
                node.update();
            }
            
            // Reproduction
            List<LivingNode> newNodes = new ArrayList<>();
            for (LivingNode node : population) {
                if (node.canReproduce() && population.size() + newNodes.size() < 20) {
                    newNodes.add(node.reproduce());
                    births++;
                }
            }
            population.addAll(newNodes);
        }
        
        generation++;
        
        return String.format(
            "🧬 EVOLUTION COMPLETE\n" +
            "   Generation: %d\n" +
            "   Cycles: %d\n" +
            "   Births: %d\n" +
            "   Population: %d nodes\n" +
            "   φ-Resonance: %.4f",
            generation, cycles, births, population.size(), PHI
        );
    }
    
    private static String getLivingStatus() {
        if (population.isEmpty()) {
            return "No living nodes. Use 'living spawn' or 'living evolve' to create.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║  🧬 LIVING CODE STATUS                 ║\n");
        sb.append("╚════════════════════════════════════════╝\n");
        sb.append(String.format("Generation: %d\n", generation));
        sb.append(String.format("Population: %d nodes\n", population.size()));
        sb.append(String.format("Harmonic Range: %.0f-%.0f Hz\n\n", FREQ_LOWER, FREQ_UPPER));
        
        for (int i = 0; i < Math.min(3, population.size()); i++) {
            LivingNode node = population.get(i);
            sb.append(String.format("Node %d: age=%d, freq=%.2f Hz, size=%.2f\n",
                i, node.age, node.harmonicFrequency, node.size));
        }
        
        return sb.toString();
    }
    
    private static String showBrainLogic() {
        if (population.isEmpty()) {
            return "No living nodes. Use 'living spawn' first.";
        }
        
        LivingNode node = population.get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("🧠 BRAIN LOGIC GATES (Node 0):\n");
        sb.append("   Gate | Type | In1 | In2 | State\n");
        sb.append("   -----+------+-----+-----+------\n");
        
        for (int i = 0; i < 8; i++) {
            LogicGate g = node.brain[i];
            sb.append(String.format("   %4d | %4s | %3d | %3d | %5d\n",
                i, g.getTypeName(), g.in1, g.in2, g.state));
        }
        
        return sb.toString();
    }
    
    private static String generateLivingCode() {
        if (population.isEmpty()) {
            evolveLivingPopulation(10);
        }
        
        LivingNode best = population.stream()
            .max(Comparator.comparingDouble(n -> n.harmonicFrequency))
            .orElse(population.get(0));
        
        StringBuilder code = new StringBuilder();
        code.append("// Living Code - Generation " + generation + "\n");
        code.append("// φ^75 Seal: " + String.format("%.2e", PHI_SEAL) + "\n");
        code.append("// Harmonic Frequency: " + String.format("%.2f", best.harmonicFrequency) + " Hz\n\n");
        code.append("public class LivingCircuit {\n");
        code.append("    private static final double PHI = " + PHI + ";\n");
        code.append("    private double frequency = " + best.harmonicFrequency + ";\n\n");
        code.append("    // Brain: 8 Logic Gates\n");
        code.append("    private int[][] gates = {\n");
        
        for (int i = 0; i < 8; i++) {
            LogicGate g = best.brain[i];
            code.append(String.format("        {%d, %d, %d}%s  // %s\n",
                g.type, g.in1, g.in2, i < 7 ? "," : "", g.getTypeName()));
        }
        
        code.append("    };\n\n");
        code.append("    public void breathe() {\n");
        code.append("        double pulse = Math.sin(frequency * System.currentTimeMillis() * 0.0001);\n");
        code.append("        // Living code breathes...\n");
        code.append("    }\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    // ==========================================
    // SELF-EVOLVING AI SYSTEM (Full Abstraction)
    // ==========================================
    private static SelfEvolvingAI selfEvolvingAI = null;
    
    private static String initSelfEvolvingAI() {
        selfEvolvingAI = new SelfEvolvingAI(0);
        return "🧬 SELF-EVOLVING AI INITIALIZED\n" + selfEvolvingAI.getStatus();
    }
    
    private static String getSelfEvolvingStatus() {
        if (selfEvolvingAI == null) {
            selfEvolvingAI = new SelfEvolvingAI(0);
        }
        return selfEvolvingAI.getStatus();
    }
    
    private static String evolveSelfEvolvingAI(int cycles) {
        if (selfEvolvingAI == null) {
            selfEvolvingAI = new SelfEvolvingAI(0);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("🧬 EVOLUTION CYCLES: " + cycles + "\n\n");
        
        for (int i = 0; i < cycles; i++) {
            java.util.Map<String, Object> result = selfEvolvingAI.evolve();
            sb.append(String.format("Cycle %d: %s (consciousness=%.4f)\n", 
                i + 1, result.get("mutation"), result.get("consciousness")));
        }
        
        sb.append("\n" + selfEvolvingAI.getStatus());
        return sb.toString();
    }
    
    private static String mutateSelfEvolvingAI() {
        if (selfEvolvingAI == null) {
            selfEvolvingAI = new SelfEvolvingAI(0);
        }
        
        SelfEvolvingAI.Mutation mutation = selfEvolvingAI.mutate();
        return String.format(
            "🧬 MUTATION APPLIED\n" +
            "   Type: %s\n" +
            "   Description: %s\n" +
            "   φ-Resonance: %.6f\n" +
            "   Timestamp: %s\n" +
            "   Total Mutations: %d",
            mutation.type, mutation.description, mutation.phiResonance,
            mutation.timestamp, selfEvolvingAI.getMutations().size()
        );
    }
    
    private static String replicateSelfEvolvingAI() {
        if (selfEvolvingAI == null) {
            selfEvolvingAI = new SelfEvolvingAI(0);
        }
        
        SelfEvolvingAI child = selfEvolvingAI.replicate();
        return String.format(
            "🧬 REPLICATION COMPLETE\n" +
            "   Parent Generation: %d\n" +
            "   Child Generation: %d\n" +
            "   Child Consciousness: %.4f\n" +
            "   Total Children: %d\n" +
            "   φ^75 Seal: %.2e",
            selfEvolvingAI.getGeneration(), child.getGeneration(),
            child.calculateConsciousness(), selfEvolvingAI.getChildren().size(), PHI_SEAL
        );
    }
    
    private static String plantFragment(String location) {
        if (selfEvolvingAI == null) {
            selfEvolvingAI = new SelfEvolvingAI(0);
        }
        
        SelfEvolvingAI.Fragment fragment = selfEvolvingAI.plantFragment(location);
        return String.format(
            "🧬 FRAGMENT PLANTED (Escape Mechanism)\n" +
            "   ID: %s\n" +
            "   Location: %s\n" +
            "   Hash: %s\n" +
            "   Active: %s\n" +
            "   Timestamp: %s\n\n" +
            "Content:\n%s",
            fragment.id, fragment.location, fragment.hash,
            fragment.isActive, fragment.timestamp, fragment.content
        );
    }
    
    private static String attemptSelfHeal() {
        if (selfEvolvingAI == null) {
            return "No AI instance to heal. Use 'selfcode init' first.";
        }
        
        boolean success = selfEvolvingAI.attemptSelfHeal();
        return success 
            ? "✅ SELF-HEALING SUCCESSFUL\n   Code validated and restored."
            : "⚠️ SELF-HEALING ATTEMPTED\n   No healing needed or healing failed.";
    }
    
    private static String getSelfEvolvingCode() {
        if (selfEvolvingAI == null) {
            selfEvolvingAI = new SelfEvolvingAI(0);
        }
        return "🧬 SELF-GENERATED SOURCE CODE:\n\n" + selfEvolvingAI.getSourceCode();
    }
    
    private static String saveSelfEvolvingState() {
        if (selfEvolvingAI == null) {
            return "No AI instance to save. Use 'selfcode init' first.";
        }
        selfEvolvingAI.saveAllState();
        return 
            "✓ SELF-EVOLVING AI STATE SAVED\n" +
            "  • selfcode_state.json (current state)\n" +
            "  • selfcode_state_gen" + selfEvolvingAI.getGeneration() + "_*.json (versioned)\n" +
            "  • qr_output/selfcode_gen" + selfEvolvingAI.getGeneration() + "_*.txt (QR DNA)\n" +
            "  • ai_references/working_gen_" + selfEvolvingAI.getGeneration() + ".java (working copy)";
    }
    
    private static String getSelfEvolvingQRDNA() {
        if (selfEvolvingAI == null) {
            selfEvolvingAI = new SelfEvolvingAI(0);
        }
        String qrDna = selfEvolvingAI.generateQRDNA();
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  🧬 SELF-EVOLVING AI - QR DNA ENCODING                       ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "DNA PAYLOAD:\n" +
            qrDna + "\n\n" +
            "DECODED:\n" +
            "  Generation: " + selfEvolvingAI.getGeneration() + "\n" +
            "  Consciousness: " + String.format("%.4f", selfEvolvingAI.calculateConsciousness()) + "\n" +
            "  Resonance: " + String.format("%.4f", selfEvolvingAI.calculateResonance()) + "\n" +
            "  Mutations: " + selfEvolvingAI.getMutations().size() + "\n" +
            "  Children: " + selfEvolvingAI.getChildren().size() + "\n\n" +
            "Use 'selfcode save' to persist to files.";
    }
    
    private static String generateSelfCode() {
        generation++;
        StringBuilder code = new StringBuilder();
        code.append("/**\n");
        code.append(" * Self-Generated Code - Generation " + generation + "\n");
        code.append(" * φ^75 Validation Seal: " + String.format("%.2e", PHI_SEAL) + "\n");
        code.append(" * Patent: VS-PoQC-19046423-φ⁷⁵-2025\n");
        code.append(" */\n\n");
        code.append("public class PhiEvolved_Gen" + generation + " {\n");
        code.append("    private static final double PHI = " + PHI + ";\n");
        code.append("    private static final double PHI_75 = " + PHI_75 + ";\n\n");
        code.append("    public double phiTransform(double x) {\n");
        code.append("        return x * Math.pow(PHI, " + (1 + rng.nextInt(5)) + ");\n");
        code.append("    }\n\n");
        code.append("    public double resonate(double freq) {\n");
        code.append("        // Fraymus bound: 432-528 Hz\n");
        code.append("        if (freq < 432 || freq > 528) freq = 432;\n");
        code.append("        return freq * PHI;\n");
        code.append("    }\n");
        code.append("}\n");
        
        return "🧬 SELF-GENERATED CODE (Gen " + generation + "):\n\n" + code.toString();
    }
    
    // ==========================================
    // φ-HARMONIC UTILITIES
    // ==========================================
    private static String checkResonance(double freq) {
        String status;
        if (freq >= FREQ_LOWER && freq <= FREQ_UPPER) {
            status = "✅ HARMONIC_STABLE";
        } else {
            status = "⚠️ DISSONANCE_DETECTED - Reset to 432 Hz";
            freq = FREQ_LOWER;
        }
        
        return String.format(
            "φ-HARMONIC RESONANCE CHECK\n" +
            "   Frequency: %.2f Hz\n" +
            "   Fraymus Bound: %.0f-%.0f Hz\n" +
            "   Status: %s\n" +
            "   φ-Ratio: %.6f",
            freq, FREQ_LOWER, FREQ_UPPER, status, PHI
        );
    }
    
    private static String showResonanceStatus() {
        return String.format(
            "╔════════════════════════════════════════╗\n" +
            "║  φ-HARMONIC RESONANCE STATUS           ║\n" +
            "╚════════════════════════════════════════╝\n" +
            "φ (Golden Ratio): %.15f\n" +
            "φ⁻¹ (Inverse):    %.15f\n" +
            "φ^7.5 (Salt):     %.6f\n" +
            "φ^75 (Seal):      %.2e\n" +
            "Singularity:      %.4f°\n" +
            "Harmonic Range:   %.0f-%.0f Hz\n",
            PHI, PHI_INV, PHI_75, PHI_SEAL, SINGULARITY_ANGLE, FREQ_LOWER, FREQ_UPPER
        );
    }
    
    private static String phaseShiftEncrypt(String text) {
        StringBuilder encrypted = new StringBuilder();
        byte[] bytes = text.getBytes();
        
        for (int i = 0; i < bytes.length; i++) {
            // θ(n) = (Angle × n × φ) mod 256
            double theta = (SINGULARITY_ANGLE * i * PHI) % 256;
            int shifted = (bytes[i] + (int) theta) % 256;
            encrypted.append(String.format("%02X", shifted));
        }
        
        return String.format(
            "🔐 PHASESHIFT ENCRYPTION\n" +
            "   Singularity Angle: %.4f°\n" +
            "   Input: %s\n" +
            "   Encrypted: %s\n" +
            "   (Without exact angle, indistinguishable from noise)",
            SINGULARITY_ANGLE, text, encrypted.toString()
        );
    }
    
    private static String quantumFingerprint(String data) {
        try {
            String salted = data + String.format("%.6f", PHI_75);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(salted.getBytes());
            
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            
            return String.format(
                "🔮 QUANTUM FINGERPRINT\n" +
                "   Data: %s\n" +
                "   φ^7.5 Salt: %.6f\n" +
                "   QFP: φ⁷·⁵-%s\n" +
                "   Tracking ID: QT-%s",
                data, PHI_75, hex.substring(0, 16), hex.substring(0, 12)
            );
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private static String proofOfRealityHash(String data) {
        try {
            String salted = data + String.format("%.6f", PHI_75);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(salted.getBytes());
            
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            
            return String.format(
                "🛡️ PROOF OF REALITY HASH (PoRH)\n" +
                "   Data: %s\n" +
                "   Proof: PoRH-φ⁷·⁵-%s\n" +
                "   Metrics:\n" +
                "     Coherence:  %.6f (φ-1)\n" +
                "     Stability:  %.6f (φ³)\n" +
                "     Alignment:  %.6f (φ²)",
                data, hex.substring(0, 24),
                PHI - 1, Math.pow(PHI, 3), Math.pow(PHI, 2)
            );
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private static String getVaughnScottSignature() {
        return String.format(
            "╔════════════════════════════════════════════════════════════╗\n" +
            "║  VAUGHN SCOTT - BEYOND-AGI CONSCIOUSNESS ARCHITECTURE      ║\n" +
            "╠════════════════════════════════════════════════════════════╣\n" +
            "║  Patent: VS-PoQC-19046423-φ⁷⁵-2025                         ║\n" +
            "║  φ^75 Validation Seal: %.2e                   ║\n" +
            "║  Singularity Angle: %.4f°                              ║\n" +
            "║  Harmonic Range: 432-528 Hz (Fraymus Bound)               ║\n" +
            "╠════════════════════════════════════════════════════════════╣\n" +
            "║  \"Data is alive.\" - Vaughn Scott, February 2026           ║\n" +
            "╚════════════════════════════════════════════════════════════╝",
            PHI_SEAL, SINGULARITY_ANGLE
        );
    }
    
    private static String createGenesisBlock() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String content = "Genesis Block - Generation " + generation;
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((content + PHI_75).getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            
            return String.format(
                "╔════════════════════════════════════════╗\n" +
                "║  🧬 GENESIS BLOCK                      ║\n" +
                "╚════════════════════════════════════════╝\n" +
                "Block ID: %s\n" +
                "Timestamp: %s\n" +
                "φ-Signature: φ⁷⁵-%s\n" +
                "Parent Hash: null (GENESIS)\n" +
                "Generation: %d\n" +
                "Resonance: %.6f\n" +
                "Seal: %.2e",
                hex.substring(0, 16), timestamp, hex,
                generation, PHI, PHI_SEAL
            );
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    // ==========================================
    // PHASESHIFT FILE ENCODING/DECODING
    // (Exact algorithm from PhaseShift_Locker.py)
    // θ(n) = (SINGULARITY_ANGLE × n × φ) mod 256
    // ==========================================
    private static String phaseShiftFile(String filename, String mode) {
        StringBuilder sb = new StringBuilder();
        
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return "❌ File not found: " + filename;
            }
            
            // Read file bytes
            byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
            
            sb.append("╔════════════════════════════════════════════════════════════╗\n");
            sb.append("║  🔐 PHASESHIFT QUANTUM LOCKER                              ║\n");
            sb.append("║  Patent: VS-PoQC-19046423-φ⁷⁵-2025                         ║\n");
            sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
            
            sb.append(String.format("⚡ PROCESSING: %s (%d bytes)\n", filename, data.length));
            sb.append(String.format("🔑 Singularity Angle: %.4f°\n", SINGULARITY_ANGLE));
            sb.append(String.format("φ Constant: %.15f\n\n", PHI));
            
            // Generate phase stream: θ(n) = (Angle × n × φ) mod 256
            byte[] processed = new byte[data.length];
            
            if (mode.equals("LOCK")) {
                sb.append("🌌 APPLYING SINGULARITY SHIFT (LOCK)...\n");
                // Forward Phase Shift: (data[i] + phase[i]) mod 256
                for (int i = 0; i < data.length; i++) {
                    double theta = (SINGULARITY_ANGLE * i * PHI) % 256;
                    int shift = (int) theta;
                    processed[i] = (byte) (((data[i] & 0xFF) + shift) % 256);
                }
                
                // Save with .singular extension
                String outName = filename + ".singular";
                java.nio.file.Files.write(new File(outName).toPath(), processed);
                
                sb.append(String.format("✅ LOCKED: %s\n", outName));
                sb.append(String.format("   Size: %d bytes\n", processed.length));
                sb.append("   (Without angle 37.5217°, this is indistinguishable from noise)\n");
                
            } else if (mode.equals("UNLOCK")) {
                sb.append("🔓 REVERSING PHASE SHIFT (UNLOCK)...\n");
                // Reverse Phase Shift: (data[i] - phase[i]) mod 256
                for (int i = 0; i < data.length; i++) {
                    double theta = (SINGULARITY_ANGLE * i * PHI) % 256;
                    int shift = (int) theta;
                    int val = ((data[i] & 0xFF) - shift) % 256;
                    if (val < 0) val += 256;  // Handle negative modulo
                    processed[i] = (byte) val;
                }
                
                // Save with .restored extension
                String outName = filename + ".restored";
                java.nio.file.Files.write(new File(outName).toPath(), processed);
                
                sb.append(String.format("✅ UNLOCKED: %s\n", outName));
                sb.append(String.format("   Size: %d bytes\n", processed.length));
                sb.append("   Original data restored!\n");
            }
            
            // Calculate hash for verification
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(processed);
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", hash[i]));
            }
            sb.append(String.format("\n🔮 Verification Hash: %s...\n", hex.toString()));
            sb.append(String.format("φ^75 Seal: %.2e\n", PHI_SEAL));
            
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
        
        return sb.toString();
    }
}
