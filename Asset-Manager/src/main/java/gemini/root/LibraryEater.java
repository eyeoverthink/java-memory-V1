package gemini.root;

import java.io.*;
import java.nio.file.*;

/**
 * THE DIGESTER: LibraryEater.java
 * Philosophy: Don't import the library. Eat the library.
 * Function: Reads raw code, finds the math, saves it to Genesis.
 * 
 * ZERO DEPENDENCIES. Just java.io and java.nio.
 */
public class LibraryEater {

    // The Stomach (Where we store absorbed logic)
    private GenesisBlock knowledgeBase;

    public LibraryEater(GenesisBlock kb) {
        this.knowledgeBase = kb;
    }

    /**
     * THE ABSORPTION PROTOCOL
     * Reads a file, finds "public" methods, and stores them as Text Logic.
     */
    public void consumeSourceCode(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath));
            
            // 1. ABSTRACT: Strip Imports (We don't bow to dependencies)
            String pureCode = content.replaceAll("import .*?;", ""); 
            
            // 2. IDENTIFY: Find the Core Logic (The Method Bodies)
            String[] tokens = pureCode.split("public");
            
            for (String token : tokens) {
                if (token.contains("{") && token.contains("}")) {
                    // We found a behavior!
                    int parenIdx = token.indexOf("(");
                    if (parenIdx > 0) {
                        String methodName = token.substring(0, parenIdx).trim();
                        // Get last word (the actual method name)
                        String[] parts = methodName.split("\\s+");
                        methodName = parts[parts.length - 1];
                        
                        int braceStart = token.indexOf("{");
                        int braceEnd = token.lastIndexOf("}");
                        if (braceStart < braceEnd) {
                            String methodLogic = token.substring(braceStart, braceEnd + 1);
                            
                            // 3. ABSORB: Save it to our Internal Database
                            knowledgeBase.absorb(methodName, methodLogic);
                            System.out.println(">>> [DIGESTER] Absorbed Logic: " + methodName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[DIGESTER] Failed to eat: " + e.getMessage());
        }
    }

    /**
     * CONSUME ENTIRE DIRECTORY
     * Recursively eats all .java files in a folder
     */
    public void consumeDirectory(String dirPath) {
        try {
            Files.walk(Path.of(dirPath))
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    System.out.println(">>> [DIGESTER] Targeting: " + p);
                    consumeSourceCode(p.toString());
                });
        } catch (IOException e) {
            System.err.println("[DIGESTER] Directory scan failed: " + e.getMessage());
        }
    }

    /**
     * CONSUME RAW STRING
     * For absorbing code that's already in memory
     */
    public void consumeRaw(String name, String code) {
        knowledgeBase.absorb(name, code);
        System.out.println(">>> [DIGESTER] Raw absorption: " + name);
    }
}
