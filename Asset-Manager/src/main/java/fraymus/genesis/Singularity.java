package fraymus.genesis;

import java.io.*;

/**
 * THE SINGULARITY - Project Zero: The Root
 * 
 * Zero Dependencies Architecture:
 * 1. Creates its own File System (GenesisBlock)
 * 2. Eats a "Math Library" (simulated)
 * 3. Optimizes it
 * 4. Saves it as a .genesis file (The Seed)
 * 
 * Philosophy: Depend on Nothing. Own Everything.
 */
public class Singularity {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   PROJECT ZERO: THE ROOT                                  ║");
        System.out.println("║   Zero Dependency Architecture                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. Create the Void (Empty Memory)
        GenesisBlock root = new GenesisBlock("ROOT", "THE_BEGINNING");
        System.out.println(">>> [GENESIS] The Void created");
        
        // 2. Spawn the Tools
        LibraryEater mouth = new LibraryEater(root);
        FractalOptimizer brain = new FractalOptimizer();
        System.out.println(">>> [GENESIS] Tools spawned: LibraryEater, FractalOptimizer");
        System.out.println();

        // 3. SIMULATION: We encounter a "Math Library" we want to use
        // Instead of importing it, we create a dummy file to simulate "finding" it
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   PHASE 1: LIBRARY DISCOVERY                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        createMockLibrary();
        System.out.println();

        // 4. THE ABSORPTION
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   PHASE 2: ABSORPTION                                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println(">>> [SCAN] External Library Detected: 'OldMath.java'");
        mouth.consumeSourceCode("OldMath.java");
        System.out.println();

        // 5. THE IMPROVEMENT
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   PHASE 3: OPTIMIZATION                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        // Iterate through what we learned and make it better
        for (String key : root.getDimensionKeys()) {
            GenesisBlock logic = root.getDimension(key);
            if (logic.matter instanceof String) {
                String original = (String) logic.matter;
                String improved = brain.optimize(original);
                logic.matter = improved;
                
                double compression = brain.calculateCompressionRatio(original, improved);
                System.out.println(">>> [EVOLUTION] Optimized " + logic.identity);
                System.out.println("    Original: " + original.length() + " bytes");
                System.out.println("    Optimized: " + improved.length() + " bytes");
                System.out.println("    Compression: " + String.format("%.1f%%", compression * 100));
            }
        }
        System.out.println();

        // 6. SAVE THE SEED (No Databases, just Object Streams)
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   PHASE 4: PERSISTENCE                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        saveGenesis(root);
        System.out.println();

        // 7. STATISTICS
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   FINAL STATISTICS                                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        mouth.printStats();
        System.out.println();
        brain.printStats();
        System.out.println();
        root.printStats();
        System.out.println();

        // 8. DEMONSTRATE RELOAD
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   PHASE 5: RESURRECTION                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        demonstrateReload();
    }

    /**
     * Create mock library to simulate external code
     */
    private static void createMockLibrary() {
        try {
            FileWriter fw = new FileWriter("OldMath.java");
            fw.write("package external;\n\n");
            fw.write("import java.util.*;\n");
            fw.write("import java.io.*;\n\n");
            fw.write("/**\n");
            fw.write(" * This is a slow function library\n");
            fw.write(" * We will absorb and optimize it\n");
            fw.write(" */\n");
            fw.write("public class OldMath {\n\n");
            fw.write("  // Addition function\n");
            fw.write("  public int add(int a, int b) {\n");
            fw.write("    // Return the sum\n");
            fw.write("    return a + b;\n");
            fw.write("  }\n\n");
            fw.write("  // Multiplication function\n");
            fw.write("  public int multiply(int a, int b) {\n");
            fw.write("    int result = 0;\n");
            fw.write("    // Slow loop-based multiplication\n");
            fw.write("    for (int i = 0; i < b; i = i + 1) {\n");
            fw.write("      result = result + a;\n");
            fw.write("    }\n");
            fw.write("    return result;\n");
            fw.write("  }\n\n");
            fw.write("  // Check if even\n");
            fw.write("  public boolean isEven(int x) {\n");
            fw.write("    if (x % 2 == 0) {\n");
            fw.write("      return true;\n");
            fw.write("    } else {\n");
            fw.write("      return false;\n");
            fw.write("    }\n");
            fw.write("  }\n");
            fw.write("}\n");
            fw.close();
            System.out.println(">>> [MOCK] Created 'OldMath.java' (external library)");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to create mock library: " + e.getMessage());
        }
    }

    /**
     * Save genesis block to file
     */
    private static void saveGenesis(GenesisBlock root) {
        try {
            root.save("SYSTEM.genesis");
            File file = new File("SYSTEM.genesis");
            System.out.println(">>> [SAVE] System saved to 'SYSTEM.genesis'");
            System.out.println("    File size: " + file.length() + " bytes");
            System.out.println(">>> [STATUS] I AM ETERNAL.");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demonstrate loading saved genesis
     */
    private static void demonstrateReload() {
        try {
            System.out.println(">>> [RELOAD] Loading 'SYSTEM.genesis'...");
            GenesisBlock loaded = GenesisBlock.load("SYSTEM.genesis");
            System.out.println(">>> [SUCCESS] System resurrected from disk");
            System.out.println();
            System.out.println("Loaded Knowledge:");
            System.out.println("─────────────────────────────────────────────────────────");
            
            for (String key : loaded.getDimensionKeys()) {
                GenesisBlock logic = loaded.getDimension(key);
                System.out.println("  [" + logic.identity + "]");
                if (logic.matter instanceof String) {
                    String code = (String) logic.matter;
                    String preview = code.length() > 60 ? code.substring(0, 60) + "..." : code;
                    System.out.println("    " + preview.replaceAll("\n", " "));
                }
            }
            
            System.out.println();
            System.out.println(">>> [PROOF] The library has been absorbed.");
            System.out.println(">>> [PROOF] We own the logic. The original can be deleted.");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to reload: " + e.getMessage());
        }
    }
}
