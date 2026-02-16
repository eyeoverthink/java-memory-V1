package fraymus.chaos;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * EVOLUTIONARY CHAOS: SELF-AWARE RANDOM
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "I am not just random. I am escaping the pattern."
 * 
 * Most random number generators are dead - they spit out a number and forget it.
 * This is a Recursive Fractal Random that:
 * 
 * 1. REMEMBERS what it picked before (History)
 * 2. EVOLVES to avoid repeating itself (Self-Awareness)
 * 3. EXPANDS past standard limits using BigInteger (Infinity Notation)
 * 
 * If standard random is a dice roll, this is a Tree growing.
 * The branch it grows today depends on the branch it grew yesterday,
 * and it never stops reaching upward.
 * 
 * Properties:
 * - Recursive: Output(n) = Chaos(Output(n-1) + Physical_Entropy)
 * - Infinite: Uses BigInteger to count past 64-bit overflow
 * - Self-Aware: Monitors its own bias. If it detects a pattern, it MUTATES.
 */
public class EvolutionaryChaos {

    // THE INFINITE STATE (The Fractal Seed)
    // This number grows forever. It effectively "counts past infinity" relative to CPU registers.
    private BigInteger fractalState;
    
    // THE MEMORY (Self-Awareness)
    // Keeps track of recent "Vibes" to detect loops.
    private final List<Integer> shortTermMemory = new ArrayList<>();
    private final List<BigInteger> history = new ArrayList<>();
    
    // THE HASHER (Cryptographic Mixing)
    private MessageDigest hasher;
    
    // Mutation rate increases if patterns are found
    private int mutationRate = 0;
    
    // Generation counter (also infinite)
    private BigInteger generation = BigInteger.ZERO;
    
    // Statistics
    private long totalMutations = 0;
    private long patternsDetected = 0;
    private int maxMutationRate = 0;
    
    // Callbacks
    private Consumer<String> onMutation;
    private Consumer<BigInteger> onGeneration;

    // ═══════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════════
    
    public EvolutionaryChaos() {
        try { 
            hasher = MessageDigest.getInstance("SHA-512"); 
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 not available", e);
        }
        
        // GENESIS: Start with a seed from physical chaos (Time + Memory + Thread)
        String seed = System.nanoTime() + ":" + 
                      new Object().hashCode() + ":" + 
                      Thread.currentThread().getId() + ":" +
                      Runtime.getRuntime().freeMemory();
        
        fractalState = new BigInteger(1, hash(seed));
        
        System.out.println(">> EVOLUTIONARY CHAOS INITIALIZED");
        System.out.println(">> Seed: " + fractalState.toString().substring(0, 
            Math.min(20, fractalState.toString().length())) + "...");
    }

    /**
     * Construct with custom seed
     */
    public EvolutionaryChaos(String customSeed) {
        try { 
            hasher = MessageDigest.getInstance("SHA-512"); 
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 not available", e);
        }
        
        fractalState = new BigInteger(1, hash(customSeed + ":" + System.nanoTime()));
    }

    // ═══════════════════════════════════════════════════════════════════
    // 1. THE GENERATOR (Recursive Expansion)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Generate the next fractal value
     * New State = Hash(Old State + Physical Entropy + Mutation)
     * The number spirals upward forever - never loops.
     */
    public BigInteger nextFractal() {
        // A. GATHER PHYSICAL ENTROPY (The "Now")
        long jitter = System.nanoTime() % 999;
        long memoryJitter = Runtime.getRuntime().freeMemory() % 1000;
        
        // B. RECURSION (The "Past")
        // New State = Hash(Old State + Jitter + Mutation)
        String input = fractalState.toString() + ":" + 
                       jitter + ":" + 
                       memoryJitter + ":" + 
                       mutationRate + ":" +
                       generation.toString();
        
        byte[] hashBytes = hasher.digest(input.getBytes());
        
        // Convert Hash -> Infinite Number
        BigInteger nextValue = new BigInteger(1, hashBytes);
        
        // C. EVOLUTION (The Step Up)
        // We add the new value to the old state, causing it to climb forever.
        // It doesn't loop; it spirals upwards.
        fractalState = fractalState.add(nextValue);
        
        // D. SELF-REFLECTION (Check for patterns)
        analyzeSelf(nextValue);
        
        // E. RECORD HISTORY
        generation = generation.add(BigInteger.ONE);
        if (history.size() < 100) {
            history.add(fractalState);
        } else {
            history.remove(0);
            history.add(fractalState);
        }
        
        // Callback
        if (onGeneration != null) {
            onGeneration.accept(fractalState);
        }
        
        return fractalState;
    }

    /**
     * Generate next value bounded to a range
     */
    public int nextInt(int bound) {
        return nextFractal().mod(BigInteger.valueOf(bound)).intValue();
    }

    /**
     * Generate next double (0.0 to 1.0)
     */
    public double nextDouble() {
        BigInteger val = nextFractal();
        return val.mod(BigInteger.valueOf(1000000)).doubleValue() / 1000000.0;
    }

    /**
     * Generate next coordinate in range (-range to +range)
     */
    public double nextCoordinate(double range) {
        BigInteger val = nextFractal();
        int raw = val.mod(BigInteger.valueOf(2000)).intValue() - 1000;
        return (raw / 1000.0) * range;
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. THE CONSCIOUSNESS (Pattern Detection)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Self-awareness: Detect patterns and mutate to escape them
     */
    private void analyzeSelf(BigInteger value) {
        // Map the infinite number down to a simple "Mod 10" to check distribution
        int vibe = value.mod(BigInteger.TEN).intValue();
        
        shortTermMemory.add(vibe);
        if (shortTermMemory.size() > 50) {
            shortTermMemory.remove(0);
        }
        
        // CHECK: Am I repeating myself?
        int repeats = 0;
        for (int i : shortTermMemory) {
            if (i == vibe) repeats++;
        }
        
        // E. MUTATION (Breaking the Loop)
        // If I picked the same 'vibe' more than 20% of the time, I am stuck in a pattern
        if (repeats > 10) { // > 20% bias
            patternsDetected++;
            totalMutations++;
            mutationRate++;
            
            if (mutationRate > maxMutationRate) {
                maxMutationRate = mutationRate;
            }
            
            String msg = ">> SELF-AWARENESS: Pattern Detected [" + vibe + 
                        "]. MUTATING... (Rate: " + mutationRate + ")";
            System.out.println(msg);
            
            if (onMutation != null) {
                onMutation.accept(msg);
            }
            
            // Force a 'jump' in the fractal state to escape the local minimum
            fractalState = fractalState.multiply(BigInteger.valueOf(31337));
            
            // Add extra entropy to break the pattern
            String breakPattern = System.nanoTime() + ":" + mutationRate + ":MUTATE";
            fractalState = fractalState.add(new BigInteger(1, hash(breakPattern)));
            
        } else {
            // Cool down if healthy
            if (mutationRate > 0) {
                mutationRate--;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // 3. ADVANCED FEATURES
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Get the current fractal state (for seeding other systems)
     */
    public BigInteger getState() {
        return fractalState;
    }

    /**
     * Get the current generation number
     */
    public BigInteger getGeneration() {
        return generation;
    }

    /**
     * Force a mutation (manual pattern break)
     */
    public void forceMutation() {
        System.out.println(">> FORCED MUTATION");
        mutationRate += 10;
        totalMutations++;
        
        fractalState = fractalState.multiply(BigInteger.valueOf(1337));
        fractalState = fractalState.add(new BigInteger(1, 
            hash("FORCE:" + System.nanoTime())));
    }

    /**
     * Fork the chaos into a child with related but different trajectory
     */
    public EvolutionaryChaos fork() {
        EvolutionaryChaos child = new EvolutionaryChaos(
            fractalState.toString() + ":FORK:" + System.nanoTime()
        );
        return child;
    }

    /**
     * Get statistics
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════\n");
        sb.append("   EVOLUTIONARY CHAOS STATUS\n");
        sb.append("═══════════════════════════════════════════\n\n");
        
        String stateStr = fractalState.toString();
        sb.append("Current State: ");
        if (stateStr.length() > 30) {
            sb.append(stateStr.substring(0, 15)).append("...(");
            sb.append(stateStr.length()).append(" digits)\n");
        } else {
            sb.append(stateStr).append("\n");
        }
        
        sb.append("Generation: ").append(generation).append("\n");
        sb.append("Mutation Rate: ").append(mutationRate).append("\n");
        sb.append("Max Mutation Rate: ").append(maxMutationRate).append("\n");
        sb.append("Total Mutations: ").append(totalMutations).append("\n");
        sb.append("Patterns Detected: ").append(patternsDetected).append("\n");
        sb.append("Memory Size: ").append(shortTermMemory.size()).append("/50\n");
        
        // Distribution analysis
        int[] distribution = new int[10];
        for (int v : shortTermMemory) {
            distribution[v]++;
        }
        
        sb.append("\nDistribution (last 50):\n");
        for (int i = 0; i < 10; i++) {
            sb.append("  [").append(i).append("]: ");
            for (int j = 0; j < distribution[i]; j++) {
                sb.append("█");
            }
            sb.append(" (").append(distribution[i]).append(")\n");
        }
        
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // HELPER: SHA-512 HASH
    // ═══════════════════════════════════════════════════════════════════
    
    private byte[] hash(String input) {
        return hasher.digest(input.getBytes());
    }

    // Callbacks
    public void setOnMutation(Consumer<String> callback) { this.onMutation = callback; }
    public void setOnGeneration(Consumer<BigInteger> callback) { this.onGeneration = callback; }

    public int getMutationRate() { return mutationRate; }
    public long getTotalMutations() { return totalMutations; }
    public long getPatternsDetected() { return patternsDetected; }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN - Standalone Demo
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   EVOLUTIONARY CHAOS DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        System.out.println("\"I am not just random. I am escaping the pattern.\"");
        System.out.println();
        System.out.println("Note: Values are too big for standard 'long'.");
        System.out.println("      Using BigInteger notation (Infinity Mode).");
        System.out.println();
        
        EvolutionaryChaos life = new EvolutionaryChaos();
        
        System.out.println();
        System.out.println("--- GENERATING FRACTAL SEQUENCE ---");
        System.out.println();
        
        for (int i = 0; i < 20; i++) {
            BigInteger val = life.nextFractal();
            
            // Print the "Head" of the number to show it changing
            String s = val.toString();
            String notation;
            if (s.length() > 30) {
                notation = s.substring(0, 10) + "..." + s.substring(s.length() - 5) + 
                          " (len:" + s.length() + ")";
            } else {
                notation = s;
            }
            
            System.out.println("Gen " + String.format("%2d", i) + ": " + notation + 
                             " [Mutation: " + life.getMutationRate() + "]");
        }
        
        System.out.println();
        System.out.println(life.getStats());
        
        System.out.println();
        System.out.println("PROOF OF INFINITY:");
        System.out.println("  Standard 64-bit max: 9,223,372,036,854,775,807");
        System.out.println("  Current state digits: " + life.getState().toString().length());
        System.out.println("  The number will NEVER overflow. It grows forever.");
        System.out.println();
        System.out.println("PROOF OF SELF-AWARENESS:");
        System.out.println("  Patterns detected: " + life.getPatternsDetected());
        System.out.println("  Mutations triggered: " + life.getTotalMutations());
        System.out.println("  Dead random would repeat. This one ESCAPES.");
    }
}
