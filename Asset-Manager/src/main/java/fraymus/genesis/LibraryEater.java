package fraymus.genesis;

import java.io.*;
import java.nio.file.*;

/**
 * THE DIGESTER: LibraryEater
 * 
 * Philosophy: Don't import the library. Eat the library.
 * Function: Reads raw code, finds the math, saves it to Genesis.
 * 
 * Zero Dependencies - Only java.io and java.nio
 */
public class LibraryEater {

    // The Stomach (Where we store absorbed logic)
    private GenesisBlock knowledgeBase;
    private int methodsAbsorbed = 0;
    private int classesAbsorbed = 0;

    public LibraryEater(GenesisBlock kb) {
        this.knowledgeBase = kb;
    }

    /**
     * THE ABSORPTION PROTOCOL
     * Reads a file, finds "public" methods, and stores them as Text Logic
     */
    public void consumeSourceCode(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath));
            
            System.out.println("[DIGESTER] Consuming: " + filePath);
            
            // 1. ABSTRACT: Strip Imports (We don't bow to dependencies)
            String pureCode = content.replaceAll("import .*?;", ""); 
            
            // 2. Extract class name
            String className = extractClassName(pureCode);
            if (className != null) {
                classesAbsorbed++;
                System.out.println("  [CLASS] " + className);
            }
            
            // 3. IDENTIFY: Find the Core Logic (The Method Bodies)
            // Simple regex to find "public void something() { ... }"
            String[] tokens = pureCode.split("public");
            
            for (String token : tokens) {
                if (token.contains("{") && token.contains("}")) {
                    // We found a behavior!
                    String methodSignature = extractMethodSignature(token);
                    String methodLogic = extractMethodBody(token);
                    
                    if (methodSignature != null && methodLogic != null) {
                        // 4. ABSORB: Save it to our Internal Database
                        String key = (className != null ? className + "." : "") + methodSignature;
                        knowledgeBase.absorb(key, methodLogic);
                        methodsAbsorbed++;
                        System.out.println("  [METHOD] " + methodSignature);
                    }
                }
            }
            
            System.out.println("  ✓ Absorption complete");
            
        } catch (IOException e) {
            System.err.println("[DIGESTER] Failed to eat: " + e.getMessage());
        }
    }

    /**
     * Consume entire directory recursively
     */
    public void consumeDirectory(String dirPath) {
        try {
            Files.walk(Path.of(dirPath))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> consumeSourceCode(p.toString()));
        } catch (IOException e) {
            System.err.println("[DIGESTER] Failed to walk directory: " + e.getMessage());
        }
    }

    /**
     * Extract class name from source
     */
    private String extractClassName(String code) {
        // Find "class ClassName"
        int classIdx = code.indexOf("class ");
        if (classIdx == -1) return null;
        
        int start = classIdx + 6;
        int end = start;
        while (end < code.length() && Character.isJavaIdentifierPart(code.charAt(end))) {
            end++;
        }
        
        return code.substring(start, end).trim();
    }

    /**
     * Extract method signature
     */
    private String extractMethodSignature(String token) {
        try {
            int parenIdx = token.indexOf("(");
            if (parenIdx == -1) return null;
            
            // Get everything before the opening paren
            String beforeParen = token.substring(0, parenIdx).trim();
            
            // Get the method name (last word)
            String[] parts = beforeParen.split("\\s+");
            if (parts.length == 0) return null;
            
            String methodName = parts[parts.length - 1];
            
            // Get parameters
            int closeParen = token.indexOf(")", parenIdx);
            if (closeParen == -1) return null;
            
            String params = token.substring(parenIdx, closeParen + 1);
            
            return methodName + params;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract method body
     */
    private String extractMethodBody(String token) {
        try {
            int openBrace = token.indexOf("{");
            int closeBrace = token.lastIndexOf("}");
            
            if (openBrace == -1 || closeBrace == -1 || closeBrace <= openBrace) {
                return null;
            }
            
            return token.substring(openBrace, closeBrace + 1);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get absorption statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   LIBRARY EATER STATISTICS                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Classes Absorbed: " + classesAbsorbed);
        System.out.println("  Methods Absorbed: " + methodsAbsorbed);
        System.out.println("  Knowledge Base Nodes: " + knowledgeBase.countNodes());
    }

    /**
     * Get knowledge base
     */
    public GenesisBlock getKnowledgeBase() {
        return knowledgeBase;
    }
}
