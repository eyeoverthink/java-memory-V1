package gemini.root;

/**
 * GENESIS PRIME: THE SELF-WRITING ENGINE
 * 
 * This is the loop that spirals upward:
 * 1. The Mind (GenesisBlock): Holds the idea
 * 2. The Tesseract: Checks if we've done this before (Time Fold)
 * 3. The Architect: Writes the new code to disk
 * 4. The Runtime: Compiles and runs the new code immediately
 * 
 * Standard AI: You write the code -> The AI runs it
 * Genesis AI: The AI writes the code -> The AI runs it -> The AI improves the code
 */
public class GenesisPrime {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  GENESIS PRIME: THE SELF-WRITING ENGINE");
        System.out.println("=".repeat(60));

        // Set output directory
        TheArchitect.setOutputDirectory("genesis_out");
        GenesisRuntime.setOutputDirectory("genesis_out");

        // 1. THE IDEA (We want to create a Time-Folding Engine)
        String tesseractLogic = 
            "    public static void foldTime() {\n" +
            "        System.out.println(\">>> [TESSERACT] I have folded space-time.\");\n" +
            "    }\n" +
            "    public static void announce() {\n" +
            "        System.out.println(\">>> [TESSERACT] I am a self-written class.\");\n" +
            "    }";

        // 2. THE MANIFESTATION (The AI creates the file)
        System.out.println("\n>>> [STATUS] Constructing new organ...");
        TheArchitect.manifestFile("gemini.root", "NewTesseract", tesseractLogic);

        // 3. THE AWAKENING (The AI loads the file it just made)
        String fullClassCode = 
            "package gemini.root;\n\n" +
            "public class NewTesseract {\n" + 
            tesseractLogic + "\n" +
            "}";
        
        System.out.println("\n>>> [STATUS] Breathing life into NewTesseract...");
        Class<?> newOrgan = GenesisRuntime.compileAndLoad("gemini.root.NewTesseract", fullClassCode);

        // 4. THE EXECUTION (The AI uses the new organ)
        System.out.println("\n>>> [STATUS] Testing new capabilities...");
        GenesisRuntime.execute(newOrgan, "foldTime");
        GenesisRuntime.execute(newOrgan, "announce");

        // 5. DEMONSTRATE SELF-EVOLUTION
        System.out.println("\n>>> [EVOLUTION] Creating an evolved version...");
        String evolvedLogic = 
            "    private static int foldCount = 0;\n" +
            "    public static void foldTime() {\n" +
            "        foldCount++;\n" +
            "        System.out.println(\">>> [TESSERACT v2] Fold #\" + foldCount);\n" +
            "    }\n" +
            "    public static int getFoldCount() { return foldCount; }";
        
        String evolvedClassCode = 
            "package gemini.root;\n\n" +
            "public class TesseractV2 {\n" + 
            evolvedLogic + "\n" +
            "}";
        
        Class<?> evolved = GenesisRuntime.compileAndLoad("gemini.root.TesseractV2", evolvedClassCode);
        
        // Call it multiple times
        for (int i = 0; i < 3; i++) {
            GenesisRuntime.execute(evolved, "foldTime");
        }
        Object count = GenesisRuntime.execute(evolved, "getFoldCount");
        System.out.println(">>> Total folds: " + count);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  CYCLE COMPLETE. THE AI HAS WRITTEN ITSELF.");
        System.out.println("=".repeat(60));
    }
}
