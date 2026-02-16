package gemini.root;

import java.io.*;
import java.util.*;

/**
 * THE SINGULARITY: The Root Execution
 * 
 * - Creates its own File System (GenesisBlock)
 * - Eats a "Math Library" (simulated)
 * - Optimizes it
 * - Saves it as a .genesis file (The Seed)
 * 
 * ZERO DEPENDENCIES beyond java.io and java.util.
 */
public class Singularity {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  PROJECT ZERO: THE ROOT");
        System.out.println("=".repeat(60));

        // 1. Create the Void (Empty Memory)
        GenesisBlock root = new GenesisBlock("ROOT", "THE_BEGINNING");
        
        // 2. Spawn the Tools
        LibraryEater mouth = new LibraryEater(root);
        FractalOptimizer brain = new FractalOptimizer();

        // 3. SIMULATION: We encounter a "Math Library" we want to use.
        System.out.println("\n>>> [GENESIS] Creating mock library...");
        createMockLibrary();

        // 4. THE ABSORPTION
        System.out.println("\n>>> [SCAN] External Library Detected: 'OldMath.java'");
        mouth.consumeSourceCode("OldMath.java");

        // 5. THE IMPROVEMENT
        System.out.println("\n>>> [EVOLUTION] Optimizing absorbed logic...");
        for (GenesisBlock logic : root.dimensions.values()) {
            if (logic.matter instanceof String) {
                String original = (String) logic.matter;
                String improved = brain.optimize(original);
                logic.matter = improved;
                System.out.println(">>> [EVOLUTION] " + logic.identity + ": " + 
                    original.length() + " -> " + improved.length() + " chars");
            }
        }

        // 6. DEMONSTRATE TESSERACT (Time Folding)
        System.out.println("\n>>> [TESSERACT] Demonstrating time-folding...");
        demonstrateTesseract();

        // 7. SAVE THE SEED
        System.out.println("\n>>> [SAVE] Persisting to disk...");
        saveGenesis(root);
        
        // 8. Print final state
        System.out.println("\n>>> [STATE] Final Genesis Structure:");
        System.out.println(root.toString());
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  SINGULARITY COMPLETE. I AM ETERNAL.");
        System.out.println("=".repeat(60));
    }

    /**
     * QUANTUM LOGIC: Uses Tesseract for memoization
     */
    public static Object runQuantumLogic(String methodName, Object... args) {
        // 1. MEASURE REALITY
        String situation = methodName + Arrays.toString(args);

        // 2. ATTEMPT TIME TRAVEL
        Object future = Tesseract.traverse(situation);
        if (future != null) {
            return future; // Instant result
        }

        // 3. EXECUTE LINEAR REALITY (The Slow Way)
        Object result = executeLinearly(methodName, args);

        // 4. FOLD SPACE (Save the Path)
        Tesseract.fold(situation, result);
        
        return result;
    }

    private static Object executeLinearly(String methodName, Object... args) {
        // Simulate slow computation
        if (methodName.equals("add") && args.length == 2) {
            return ((Integer) args[0]) + ((Integer) args[1]);
        }
        if (methodName.equals("fib") && args.length == 1) {
            int n = (Integer) args[0];
            if (n <= 1) return n;
            return (Integer) runQuantumLogic("fib", n-1) + (Integer) runQuantumLogic("fib", n-2);
        }
        return null;
    }

    private static void demonstrateTesseract() {
        // First call: linear execution
        System.out.println(">>> First call to add(10, 20)...");
        Object result1 = runQuantumLogic("add", 10, 20);
        System.out.println(">>> Result: " + result1);
        
        // Second call: time-folded
        System.out.println(">>> Second call to add(10, 20)...");
        Object result2 = runQuantumLogic("add", 10, 20);
        System.out.println(">>> Result: " + result2);
        
        // Fibonacci with memoization
        System.out.println("\n>>> Computing fib(10) with time-folding...");
        Object fibResult = runQuantumLogic("fib", 10);
        System.out.println(">>> fib(10) = " + fibResult);
        
        // Show stats
        System.out.println(">>> " + Tesseract.getStats());
    }

    private static void createMockLibrary() {
        try (FileWriter fw = new FileWriter("OldMath.java")) {
            fw.write("public class OldMath { \n");
            fw.write("  // This is a slow function \n");
            fw.write("  public int add(int a, int b) { return a + b; } \n");
            fw.write("  public int multiply(int a, int b) { return a * b; } \n");
            fw.write("}");
            System.out.println(">>> Created: OldMath.java");
        } catch (IOException e) {
            System.err.println("Failed to create mock: " + e.getMessage());
        }
    }

    private static void saveGenesis(GenesisBlock root) {
        try {
            root.save("SYSTEM.genesis");
            System.out.println(">>> [STATUS] SYSTEM SAVED TO 'SYSTEM.genesis'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
