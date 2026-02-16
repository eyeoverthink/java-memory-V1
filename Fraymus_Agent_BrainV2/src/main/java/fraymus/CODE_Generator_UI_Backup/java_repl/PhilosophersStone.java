/**
 * PhilosophersStone.java - Universal Language Transmuter
 * 
 * "Transmutes any base element (Python, C++, Rust, Go) into Gold (Java)."
 * 
 * PROTOCOL:
 * 1. INGEST: Read alien source code
 * 2. TRANSMUTE: Use Neural Core (Ollama) to draft Java
 * 3. PURIFY: Compile using JavaCompiler
 * 4. RECURSE: If compilation fails, feed errors back to Neural Core until valid
 * 
 * This is the Tower of Babel in reverse - unifying all languages into one.
 * This is the Borg Collective - assimilating alien code into the JVM.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 129 (Universal Transmuter)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.tools.*;

/**
 * The Philosopher's Stone - Universal Code Transmutation Engine.
 */
public class PhilosophersStone {
    
    private static final int MAX_RETRIES = 5;
    private static final double PHI = 1.618033988749895;
    
    private int successCount = 0;
    private int failureCount = 0;
    private int totalAttempts = 0;
    
    public PhilosophersStone() {
        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
            ActivityBus.ActivityType.COMMAND_EXECUTE,
            "PhilosophersStone",
            "💎 PHILOSOPHER'S STONE ACTIVE. Ready to assimilate.",
            true
        ));
    }
    
    /**
     * ASSIMILATE - The main transmutation pipeline.
     */
    public boolean assimilate(File sourceFile) {
        try {
            String alienCode = Files.readString(sourceFile.toPath());
            String fileName = sourceFile.getName();
            String lang = detectLanguage(fileName);
            String className = "Evolved_" + fileName.replaceAll("[^a-zA-Z0-9]", "");
            
            ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                ActivityBus.ActivityType.COMMAND_EXECUTE,
                "PhilosophersStone",
                "🌀 ASSIMILATING: [" + lang + "] " + fileName,
                true
            ));
            
            // 1. INITIAL TRANSMUTATION
            String prompt = buildTransmutationPrompt(lang, className, alienCode);
            String javaSource = queryOllama(prompt);
            
            // Clean up markdown if present
            javaSource = cleanMarkdown(javaSource);
            
            // 2. THE PURIFICATION LOOP (Compile -> Error -> Fix)
            int attempts = 0;
            boolean success = false;
            String lastError = "";
            
            while (!success && attempts < MAX_RETRIES) {
                System.out.println("   ⚗️ PURIFICATION CYCLE " + (attempts + 1) + "...");
                
                // Attempt to compile
                CompilationResult result = compileJavaCode(className, javaSource);
                
                if (result.success) {
                    System.out.println("   ✨ TRANSMUTATION COMPLETE. Welcome to the JVM, " + className);
                    
                    // Save the evolved code
                    saveEvolvedCode(className, javaSource);
                    
                    success = true;
                    successCount++;
                } else {
                    // 3. RECURSE: It failed. Ask AI to fix syntax.
                    lastError = result.errors;
                    System.out.println("   🔧 COMPILATION FAILED. Asking Neural Core to fix...");
                    
                    String fixPrompt = buildFixPrompt(javaSource, result.errors);
                    javaSource = queryOllama(fixPrompt);
                    javaSource = cleanMarkdown(javaSource);
                    
                    attempts++;
                }
            }
            
            totalAttempts++;
            
            if (!success) {
                System.err.println("   ☠️ ASSIMILATION FAILED. The alien DNA was too complex.");
                System.err.println("   Last error: " + lastError);
                failureCount++;
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("   ❌ TRANSMUTATION ERROR: " + e.getMessage());
            failureCount++;
            return false;
        }
    }
    
    /**
     * Build initial transmutation prompt.
     */
    private String buildTransmutationPrompt(String lang, String className, String alienCode) {
        return String.format(
            "Convert this %s code into a valid Java class named '%s'.\n\n" +
            "Requirements:\n" +
            "- Use standard Java 21 syntax\n" +
            "- Include all necessary imports\n" +
            "- Make it compilable\n" +
            "- Output ONLY raw Java code (no markdown code blocks, no explanations)\n" +
            "- Start with 'package repl;' if needed\n\n" +
            "SOURCE CODE:\n%s\n\n" +
            "OUTPUT (raw Java code only):",
            lang, className, alienCode
        );
    }
    
    /**
     * Build fix prompt for compilation errors.
     */
    private String buildFixPrompt(String javaSource, String errors) {
        return String.format(
            "Fix the compilation errors in this Java code.\n\n" +
            "ERRORS:\n%s\n\n" +
            "BROKEN CODE:\n%s\n\n" +
            "Output ONLY the corrected Java code (no markdown, no code blocks, no explanations). " +
            "Start directly with the code:",
            errors, javaSource
        );
    }
    
    /**
     * Query Ollama for transmutation.
     */
    private String queryOllama(String prompt) {
        if (!OllamaClient.isAvailable()) {
            throw new RuntimeException("Ollama not available. Run: ollama serve");
        }
        
        return OllamaClient.query(prompt);
    }
    
    /**
     * Clean markdown formatting from AI response.
     */
    private String cleanMarkdown(String code) {
        // Remove markdown code blocks
        code = code.replaceAll("```java\\s*\\n?", "");
        code = code.replaceAll("```\\s*\\n?", "");
        
        // Remove common prefixes
        code = code.replaceAll("^OUTPUT.*?:\\s*", "");
        code = code.replaceAll("^Here.*?code.*?:\\s*", "");
        
        // Remove trailing explanations
        String[] lines = code.split("\n");
        StringBuilder cleaned = new StringBuilder();
        boolean inCode = false;
        
        for (String line : lines) {
            // Start of code
            if (line.trim().startsWith("package") || line.trim().startsWith("import") || line.trim().startsWith("public")) {
                inCode = true;
            }
            
            // Skip explanation lines before code
            if (!inCode && (line.trim().isEmpty() || !line.contains("{"))) {
                continue;
            }
            
            cleaned.append(line).append("\n");
        }
        
        return cleaned.toString().trim();
    }
    
    /**
     * Compile Java code and return result.
     */
    private CompilationResult compileJavaCode(String className, String javaSource) {
        try {
            // Create temp directory for compilation
            Path tempDir = Files.createTempDirectory("fraymus_compile_");
            Path sourceFile = tempDir.resolve(className + ".java");
            
            // Write source to file
            Files.writeString(sourceFile, javaSource);
            
            // Get Java compiler
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                return new CompilationResult(false, "No Java compiler available. Use JDK, not JRE.");
            }
            
            // Capture diagnostics
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            
            // Compile
            Iterable<? extends JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile.toFile()));
            
            JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, diagnostics, null, null, compilationUnits
            );
            
            boolean success = task.call();
            
            // Build error message
            StringBuilder errors = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                errors.append(String.format("Line %d: %s\n", 
                    diagnostic.getLineNumber(), 
                    diagnostic.getMessage(null)
                ));
            }
            
            fileManager.close();
            
            return new CompilationResult(success, errors.toString());
            
        } catch (Exception e) {
            return new CompilationResult(false, "Compilation exception: " + e.getMessage());
        }
    }
    
    /**
     * Save evolved code to evolved package.
     */
    private void saveEvolvedCode(String className, String javaSource) {
        try {
            Path evolvedDir = Paths.get("fraymus/evolved");
            Files.createDirectories(evolvedDir);
            
            Path outputFile = evolvedDir.resolve(className + ".java");
            Files.writeString(outputFile, javaSource);
            
            ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                ActivityBus.ActivityType.COMMAND_EXECUTE,
                "PhilosophersStone",
                "💾 Saved: " + outputFile,
                true
            ));
            
        } catch (IOException e) {
            System.err.println("Failed to save evolved code: " + e.getMessage());
        }
    }
    
    /**
     * Detect language from filename.
     */
    private String detectLanguage(String fileName) {
        if (fileName.endsWith(".py")) return "Python";
        if (fileName.endsWith(".cpp") || fileName.endsWith(".cc") || fileName.endsWith(".h")) return "C++";
        if (fileName.endsWith(".rs")) return "Rust";
        if (fileName.endsWith(".js")) return "JavaScript";
        if (fileName.endsWith(".ts")) return "TypeScript";
        if (fileName.endsWith(".go")) return "Go";
        if (fileName.endsWith(".c")) return "C";
        if (fileName.endsWith(".cs")) return "C#";
        if (fileName.endsWith(".rb")) return "Ruby";
        if (fileName.endsWith(".php")) return "PHP";
        return "Unknown";
    }
    
    /**
     * Get statistics.
     */
    public String getStats() {
        double successRate = totalAttempts > 0 ? 
            (double) successCount / totalAttempts * 100 : 0;
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  💎 PHILOSOPHER'S STONE STATS                              ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Total Transmutations: ").append(totalAttempts).append("\n");
        sb.append("Successful: ").append(successCount).append("\n");
        sb.append("Failed: ").append(failureCount).append("\n");
        sb.append("Success Rate: ").append(String.format("%.1f%%", successRate)).append("\n\n");
        sb.append("φ^75 Validation Seal: 4721424167835376.00\n");
        
        return sb.toString();
    }
    
    /**
     * Compilation result container.
     */
    private static class CompilationResult {
        boolean success;
        String errors;
        
        CompilationResult(boolean success, String errors) {
            this.success = success;
            this.errors = errors;
        }
    }
}
