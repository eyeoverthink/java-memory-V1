package fraymus.evolution;

import fraymus.hyper.HyperFormer;
import fraymus.hyper.HyperVector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 🪞 CODE REFLECTOR - Source → Vector Digestion
 * "The system reads its own source code"
 * 
 * Traditional AI:
 * - Trained on external data (internet, books)
 * - Cannot introspect its own implementation
 * - Static after training
 * 
 * Mirror Protocol:
 * - Reads its own .java files
 * - Converts source code to 10,000D vectors
 * - Learns from its own structure
 * - Can generate improved versions
 * 
 * This is Recursive Self-Improvement.
 */
public class CodeReflector {

    private final HyperFormer brain;

    public CodeReflector(HyperFormer brain) {
        this.brain = brain;
    }

    /**
     * DIGEST: Reads a Java file and turns it into a Mental Hologram
     * 
     * Process:
     * 1. Read source file
     * 2. Tokenize (simple split for demo, could use AST parser)
     * 3. Embed each token into HyperVector
     * 4. Learn transitions (code patterns)
     * 5. Bundle into file hologram
     * 
     * @param filePath Path to .java file
     * @return HyperVector representing the entire file
     */
    public HyperVector digestFile(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath));
            
            // 1. TOKENIZE
            // Simple split for demo - in production, use JavaParser for AST
            // This regex splits on common Java delimiters while preserving them
            String[] tokens = content.split("(?=[,.{}\\(\\);\\s])|(?<=[,.{}\\(\\);\\s])");
            
            List<String> cleanTokens = new ArrayList<>();
            for (String t : tokens) {
                if (!t.isBlank()) {
                    cleanTokens.add(t.trim());
                }
            }

            // 2. EMBED & LEARN
            // The brain "reads" the code and updates its transition memory
            // This learns patterns like: "public" → "class", "class" → "Name", etc.
            brain.learn(cleanTokens.toArray(String[]::new));
            
            // 3. CREATE HOLOGRAM
            // Bundle all tokens into single vector representing the file's "essence"
            HyperVector fileVector = new HyperVector();
            for (String t : cleanTokens) {
                fileVector = fileVector.bundle(brain.embed(t));
            }
            
            System.out.println("   [REFLECTOR] Digested " + filePath);
            System.out.println("               Tokens: " + cleanTokens.size());
            System.out.println("               Density: " + String.format("%.4f", fileVector.density()));
            
            return fileVector;

        } catch (Exception e) {
            System.err.println("❌ BROKEN MIRROR: " + e.getMessage());
            return new HyperVector();
        }
    }

    /**
     * DIGEST DIRECTORY: Read all .java files in a directory
     * 
     * This allows the system to ingest its entire codebase.
     */
    public List<HyperVector> digestDirectory(String dirPath) {
        List<HyperVector> vectors = new ArrayList<>();
        
        try {
            Files.walk(Path.of(dirPath))
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    HyperVector v = digestFile(p.toString());
                    vectors.add(v);
                });
        } catch (Exception e) {
            System.err.println("❌ Directory scan failed: " + e.getMessage());
        }
        
        return vectors;
    }
}
