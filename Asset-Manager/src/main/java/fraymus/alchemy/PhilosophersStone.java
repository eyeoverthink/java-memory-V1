package fraymus.alchemy;

import fraymus.core.OllamaBridge;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * 💎 THE PHILOSOPHER'S STONE - Gen 129
 * "Transmutes any base element (Python, C++, Rust, JS) into Gold (Java)."
 * 
 * PROTOCOL:
 * 1. INGEST: Read alien source code
 * 2. TRANSMUTE: Use Neural Core (Ollama) to draft Java
 * 3. PURIFY: Compile using in-memory compiler
 * 4. RECURSE: If compilation fails, feed errors back to Neural Core until valid
 * 
 * The Stone doesn't just "ask" the AI - it forces valid code by beating it
 * with compiler errors until it submits.
 */
public class PhilosophersStone {

    private static final double PHI = 1.6180339887;
    
    private final OllamaBridge brain;
    private final int maxRetries;
    private final boolean verbose;
    
    // Statistics
    private int totalAttempts = 0;
    private int successCount = 0;
    private int failCount = 0;
    private List<TransmutationResult> history = new ArrayList<>();

    public PhilosophersStone() {
        this("eyeoverthink/Fraymus", 5, true);
    }
    
    public PhilosophersStone(String model, int maxRetries, boolean verbose) {
        this.brain = new OllamaBridge(model);
        this.maxRetries = maxRetries;
        this.verbose = verbose;
        
        if (verbose) {
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║  💎 PHILOSOPHER'S STONE ACTIVE                                ║");
            System.out.println("║  Universal Code Transmuter - Gen 129                          ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println("   Model: " + model);
            System.out.println("   Max retries: " + maxRetries);
            System.out.println("   Ready to assimilate.\n");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // RUNTIME INTENT COMPILER (ENGLISH → EXECUTABLE JAVA)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * TRANSMUTATE INTENT
     * Takes raw English intent, generates Java code, compiles it, and executes it.
     * Self-heals by feeding compiler errors back to LLM until code compiles.
     */
    public void transmutate(String intent) {
        System.out.println("💎 TRANSMUTATION INITIATED: " + intent);
        
        // 1. GENERATE INITIAL SOURCE (THE LEAD)
        String className = "Construct_" + System.currentTimeMillis();
        String prompt = buildIntentPrompt(intent, className);
        
        String sourceCode = brain.speak(null, prompt);
        sourceCode = extractJavaCode(sourceCode);
        
        // 2. THE REFINEMENT LOOP (ALCHEMICAL FIRE)
        boolean compiled = false;
        int attempts = 0;

        while (!compiled && attempts < maxRetries) {
            System.out.println("   🔥 HEATING CRUCIBLE (Attempt " + (attempts + 1) + ")...");
            
            CompilationResult compResult = compileRunnable(className, sourceCode);
            compiled = compResult.success;

            if (compiled) {
                System.out.println("   ✨ TRANSMUTATION COMPLETE. GOLD CREATED.");
                loadAndExecute(className, compResult.compiledClass);
                successCount++;
                return;
            } else {
                // 3. FEED THE FAILURE BACK TO THE ORACLE (SELF-HEALING)
                System.out.println("   ⚠️ IMPURITY DETECTED:");
                System.out.println(compResult.errors);
                
                // Ask LLM to fix the specific compiler errors
                String fixPrompt = buildIntentFixPrompt(intent, sourceCode, compResult.errors, className);
                sourceCode = brain.speak(null, fixPrompt);
                sourceCode = extractJavaCode(sourceCode);
                attempts++;
            }
        }
        
        if (!compiled) {
            System.out.println("   ❌ TRANSMUTATION FAILED. LEAD REMAINED LEAD.");
            failCount++;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN TRANSMUTATION API
    // ═══════════════════════════════════════════════════════════════════

    /**
     * ASSIMILATE FILE
     * Reads source file, transmutes to Java, attempts compilation.
     */
    public TransmutationResult assimilate(File sourceFile) {
        try {
            String alienCode = Files.readString(sourceFile.toPath());
            String fileName = sourceFile.getName();
            String lang = detectLanguage(fileName);
            String className = generateClassName(fileName);
            
            return assimilate(alienCode, lang, className, fileName);
        } catch (Exception e) {
            TransmutationResult result = new TransmutationResult();
            result.success = false;
            result.error = e.getMessage();
            result.sourceFile = sourceFile.getAbsolutePath();
            return result;
        }
    }
    
    /**
     * ASSIMILATE CODE
     * Transmutes raw source code string to Java.
     */
    public TransmutationResult assimilate(String alienCode, String language, String className, String sourceName) {
        TransmutationResult result = new TransmutationResult();
        result.sourceLanguage = language;
        result.sourceFile = sourceName;
        result.targetClassName = className;
        result.originalCode = alienCode;
        
        totalAttempts++;
        
        if (verbose) {
            System.out.println("🌀 ASSIMILATING: [" + language + "] " + sourceName);
            System.out.println("   Target class: " + className);
        }
        
        try {
            // Phase 1: Initial Transmutation
            String prompt = buildTransmutationPrompt(alienCode, language, className);
            String javaSource = brain.speak(null, prompt);
            javaSource = extractJavaCode(javaSource);
            
            result.javaCode = javaSource;
            result.attempts = 1;
            
            // Phase 2: Purification Loop (Compile → Error → Fix)
            int attempts = 0;
            CompilationResult compResult = null;
            
            while (attempts < maxRetries) {
                if (verbose) {
                    System.out.println("   ⚗️ PURIFICATION CYCLE " + (attempts + 1) + "...");
                }
                
                compResult = compile(className, javaSource);
                
                if (compResult.success) {
                    result.success = true;
                    result.compiledClass = compResult.compiledClass;
                    result.javaCode = javaSource;
                    result.attempts = attempts + 1;
                    successCount++;
                    
                    if (verbose) {
                        System.out.println("   ✨ TRANSMUTATION COMPLETE!");
                        System.out.println("   Class " + className + " is now part of the JVM.\n");
                    }
                    
                    history.add(result);
                    return result;
                }
                
                // Compilation failed - ask LLM to fix
                if (verbose) {
                    System.out.println("   ⚠️ Compilation error. Feeding back to Neural Core...");
                }
                
                String fixPrompt = buildFixPrompt(javaSource, compResult.errors, className);
                javaSource = brain.speak(null, fixPrompt);
                javaSource = extractJavaCode(javaSource);
                
                attempts++;
                result.attempts = attempts + 1;
            }
            
            // Max retries exceeded
            result.success = false;
            result.error = "Max retries exceeded. Last errors:\n" + 
                (compResult != null ? compResult.errors : "Unknown");
            result.javaCode = javaSource;
            failCount++;
            
            if (verbose) {
                System.out.println("   ☠️ ASSIMILATION FAILED after " + attempts + " attempts.");
                System.out.println("   The alien DNA was too complex.\n");
            }
            
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
            failCount++;
        }
        
        history.add(result);
        return result;
    }

    // ═══════════════════════════════════════════════════════════════════
    // PROMPT ENGINEERING
    // ═══════════════════════════════════════════════════════════════════

    private String buildTransmutationPrompt(String code, String language, String className) {
        return String.format("""
            You are a Senior Java Engineer performing code transmutation.
            
            TASK: Convert the following %s code into a valid Java 21 class.
            
            REQUIREMENTS:
            1. The class MUST be named exactly: %s
            2. The package MUST be: fraymus.evolved
            3. Use ONLY standard Java 21 libraries (java.*, javax.*)
            4. Preserve the original logic and functionality
            5. Add appropriate error handling
            6. Include a main() method if the original had an entry point
            
            OUTPUT FORMAT:
            - Output ONLY the Java code
            - No markdown, no explanations, no comments about the conversion
            - Start with 'package fraymus.evolved;'
            
            SOURCE CODE (%s):
            ```
            %s
            ```
            
            OUTPUT THE JAVA CLASS NOW:
            """, language, className, language, code);
    }
    
    private String buildIntentPrompt(String intent, String className) {
        return String.format("""
            You are a Senior Java Engineer writing executable code from English intent.
            
            INTENT: %s
            
            REQUIREMENTS:
            1. Create a Java class named: %s
            2. The class MUST implement Runnable
            3. Put ALL logic in the run() method
            4. Use ONLY standard Java 21 libraries (java.*, javax.*)
            5. Add proper imports
            6. Handle all exceptions
            7. Print results to System.out
            
            OUTPUT FORMAT:
            - Output ONLY raw Java code
            - No markdown, no explanations
            - No package declaration (default package)
            - Start with imports, then class definition
            
            EXAMPLE:
            ```
            import java.math.BigInteger;
            
            public class %s implements Runnable {
                @Override
                public void run() {
                    // Your implementation here
                }
            }
            ```
            
            OUTPUT THE COMPLETE RUNNABLE CLASS NOW:
            """, intent, className, className);
    }
    
    private String buildIntentFixPrompt(String intent, String brokenCode, String errors, String className) {
        return String.format("""
            The following Java code failed to compile. Fix ALL errors.
            
            ORIGINAL INTENT: %s
            CLASS NAME: %s
            
            COMPILER ERRORS:
            %s
            
            BROKEN CODE:
            ```java
            %s
            ```
            
            REQUIREMENTS:
            1. Fix ALL syntax errors
            2. Add ALL missing imports
            3. Fix ALL type mismatches
            4. The class MUST implement Runnable
            5. Keep the same class name
            6. No package declaration
            
            OUTPUT ONLY THE FIXED JAVA CODE. No explanations.
            """, intent, className, errors, brokenCode);
    }
    
    private String buildFixPrompt(String brokenCode, String errors, String className) {
        return String.format("""
            The following Java code failed to compile. Fix ALL errors.
            
            CLASS NAME: %s
            PACKAGE: fraymus.evolved
            
            COMPILER ERRORS:
            %s
            
            BROKEN CODE:
            ```java
            %s
            ```
            
            REQUIREMENTS:
            1. Fix ALL syntax errors
            2. Add ALL missing imports
            3. Fix ALL type mismatches
            4. Ensure the class compiles with Java 21
            5. Keep the same class name and package
            
            OUTPUT ONLY THE FIXED JAVA CODE. No explanations.
            """, className, errors, brokenCode);
    }

    // ═══════════════════════════════════════════════════════════════════
    // RUNTIME COMPILATION & EXECUTION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Compile a Runnable class (no package, for runtime execution)
     */
    private CompilationResult compileRunnable(String className, String source) {
        CompilationResult result = new CompilationResult();
        
        try {
            javax.tools.JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                result.success = false;
                result.errors = "No Java compiler available. Run with JDK, not JRE.";
                return result;
            }
            
            javax.tools.DiagnosticCollector<javax.tools.JavaFileObject> diagnostics = 
                new javax.tools.DiagnosticCollector<>();
            
            javax.tools.StandardJavaFileManager fileManager = 
                compiler.getStandardFileManager(diagnostics, null, null);
            
            // Write to temp file
            Path tempDir = Files.createTempDirectory("alchemy_runtime_");
            Path sourceFile = tempDir.resolve(className + ".java");
            Files.writeString(sourceFile, source);
            
            // Compile
            Iterable<? extends javax.tools.JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjects(sourceFile.toFile());
            
            List<String> options = Arrays.asList(
                "-d", tempDir.toString()
            );
            
            javax.tools.JavaCompiler.CompilationTask task = 
                compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
            
            boolean success = task.call();
            fileManager.close();
            
            if (success) {
                result.success = true;
                result.outputPath = tempDir.resolve(className + ".class").toString();
                
                // Load the class
                java.net.URL[] urls = { tempDir.toUri().toURL() };
                java.net.URLClassLoader loader = new java.net.URLClassLoader(urls);
                result.compiledClass = loader.loadClass(className);
            } else {
                result.success = false;
                StringBuilder errors = new StringBuilder();
                for (javax.tools.Diagnostic<?> d : diagnostics.getDiagnostics()) {
                    errors.append(String.format("Line %d: %s%n", 
                        d.getLineNumber(), d.getMessage(null)));
                }
                result.errors = errors.toString();
            }
            
        } catch (Exception e) {
            result.success = false;
            result.errors = e.getClass().getSimpleName() + ": " + e.getMessage();
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * Load and execute a compiled Runnable class
     */
    private void loadAndExecute(String className, Class<?> cls) {
        try {
            if (cls == null) {
                System.out.println("   ❌ Class is null, cannot execute.");
                return;
            }
            
            // Verify it implements Runnable
            if (!Runnable.class.isAssignableFrom(cls)) {
                System.out.println("   ⚠️ Class does not implement Runnable. Attempting to invoke main()...");
                
                // Try to find and invoke main method
                try {
                    java.lang.reflect.Method mainMethod = cls.getMethod("main", String[].class);
                    mainMethod.invoke(null, (Object) new String[0]);
                    return;
                } catch (NoSuchMethodException e) {
                    System.out.println("   ❌ No main() method found either.");
                    return;
                }
            }
            
            Runnable construct = (Runnable) cls.getDeclaredConstructor().newInstance();
            
            System.out.println("   ⚡ EXECUTING CONSTRUCT...");
            System.out.println("   " + "═".repeat(60));
            
            // Execute in new thread
            Thread executor = new Thread(construct, "AlchemyExecutor-" + className);
            executor.start();
            
            // Wait for completion (with timeout)
            executor.join(30000); // 30 second timeout
            
            if (executor.isAlive()) {
                System.out.println("   ⚠️ Execution timeout. Interrupting...");
                executor.interrupt();
            }
            
            System.out.println("   " + "═".repeat(60));
            System.out.println("   ✓ EXECUTION COMPLETE\n");
            
        } catch (Exception e) {
            System.out.println("   ❌ EXECUTION FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // COMPILATION
    // ═══════════════════════════════════════════════════════════════════

    private CompilationResult compile(String className, String source) {
        CompilationResult result = new CompilationResult();
        
        try {
            // Use Java Compiler API
            javax.tools.JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                result.success = false;
                result.errors = "No Java compiler available. Run with JDK, not JRE.";
                return result;
            }
            
            // Create in-memory file
            javax.tools.DiagnosticCollector<javax.tools.JavaFileObject> diagnostics = 
                new javax.tools.DiagnosticCollector<>();
            
            javax.tools.StandardJavaFileManager fileManager = 
                compiler.getStandardFileManager(diagnostics, null, null);
            
            // Write to temp file
            Path tempDir = Files.createTempDirectory("alchemy_");
            Path packageDir = tempDir.resolve("fraymus/evolved");
            Files.createDirectories(packageDir);
            Path sourceFile = packageDir.resolve(className + ".java");
            Files.writeString(sourceFile, source);
            
            // Compile
            Iterable<? extends javax.tools.JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjects(sourceFile.toFile());
            
            List<String> options = Arrays.asList(
                "-d", tempDir.toString(),
                "-source", "21",
                "-target", "21"
            );
            
            javax.tools.JavaCompiler.CompilationTask task = 
                compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
            
            boolean success = task.call();
            fileManager.close();
            
            if (success) {
                result.success = true;
                result.outputPath = tempDir.resolve("fraymus/evolved/" + className + ".class").toString();
                
                // Load the class
                java.net.URL[] urls = { tempDir.toUri().toURL() };
                try (java.net.URLClassLoader loader = new java.net.URLClassLoader(urls)) {
                    result.compiledClass = loader.loadClass("fraymus.evolved." + className);
                }
            } else {
                result.success = false;
                StringBuilder errors = new StringBuilder();
                for (javax.tools.Diagnostic<?> d : diagnostics.getDiagnostics()) {
                    errors.append(String.format("Line %d: %s%n", 
                        d.getLineNumber(), d.getMessage(null)));
                }
                result.errors = errors.toString();
            }
            
        } catch (Exception e) {
            result.success = false;
            result.errors = e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        
        return result;
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════

    private String detectLanguage(String fileName) {
        if (fileName.endsWith(".py")) return "Python";
        if (fileName.endsWith(".cpp") || fileName.endsWith(".cc") || fileName.endsWith(".cxx")) return "C++";
        if (fileName.endsWith(".c")) return "C";
        if (fileName.endsWith(".h") || fileName.endsWith(".hpp")) return "C/C++ Header";
        if (fileName.endsWith(".rs")) return "Rust";
        if (fileName.endsWith(".go")) return "Go";
        if (fileName.endsWith(".js")) return "JavaScript";
        if (fileName.endsWith(".ts")) return "TypeScript";
        if (fileName.endsWith(".rb")) return "Ruby";
        if (fileName.endsWith(".swift")) return "Swift";
        if (fileName.endsWith(".kt")) return "Kotlin";
        if (fileName.endsWith(".scala")) return "Scala";
        if (fileName.endsWith(".cs")) return "C#";
        if (fileName.endsWith(".php")) return "PHP";
        if (fileName.endsWith(".lua")) return "Lua";
        if (fileName.endsWith(".zig")) return "Zig";
        if (fileName.endsWith(".nim")) return "Nim";
        if (fileName.endsWith(".ex") || fileName.endsWith(".exs")) return "Elixir";
        if (fileName.endsWith(".erl")) return "Erlang";
        if (fileName.endsWith(".hs")) return "Haskell";
        if (fileName.endsWith(".ml") || fileName.endsWith(".mli")) return "OCaml";
        if (fileName.endsWith(".clj")) return "Clojure";
        if (fileName.endsWith(".lisp") || fileName.endsWith(".cl")) return "Common Lisp";
        if (fileName.endsWith(".scm")) return "Scheme";
        if (fileName.endsWith(".pl")) return "Perl";
        if (fileName.endsWith(".r") || fileName.endsWith(".R")) return "R";
        if (fileName.endsWith(".jl")) return "Julia";
        if (fileName.endsWith(".dart")) return "Dart";
        if (fileName.endsWith(".v")) return "V";
        if (fileName.endsWith(".asm") || fileName.endsWith(".s")) return "Assembly";
        return "Unknown";
    }
    
    private String generateClassName(String fileName) {
        // Remove extension and sanitize
        String base = fileName;
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            base = fileName.substring(0, dot);
        }
        
        // Capitalize first letter, remove invalid chars
        base = base.replaceAll("[^a-zA-Z0-9_]", "_");
        if (base.isEmpty() || Character.isDigit(base.charAt(0))) {
            base = "Evolved_" + base;
        } else {
            base = Character.toUpperCase(base.charAt(0)) + base.substring(1);
        }
        
        return base;
    }
    
    private String extractJavaCode(String response) {
        // Remove markdown code blocks if present
        response = response.trim();
        
        // Remove ```java ... ```
        Pattern codeBlock = Pattern.compile("```(?:java)?\\s*\\n([\\s\\S]*?)\\n```", Pattern.MULTILINE);
        Matcher m = codeBlock.matcher(response);
        if (m.find()) {
            return m.group(1).trim();
        }
        
        // Remove leading/trailing ``` if unmatched
        if (response.startsWith("```")) {
            response = response.substring(response.indexOf('\n') + 1);
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.lastIndexOf("```"));
        }
        
        return response.trim();
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS & STATUS
    // ═══════════════════════════════════════════════════════════════════

    public String status() {
        double successRate = totalAttempts > 0 ? 
            (double) successCount / totalAttempts * 100 : 0;
        
        return String.format("""
            💎 PHILOSOPHER'S STONE STATUS
               Total attempts: %d
               Successful: %d (%.1f%%)
               Failed: %d
               φ-Efficiency: %.6f
            """, 
            totalAttempts, successCount, successRate, failCount,
            successRate * PHI / 100);
    }
    
    public List<TransmutationResult> getHistory() {
        return new ArrayList<>(history);
    }
    
    public void clearHistory() {
        history.clear();
    }

    // ═══════════════════════════════════════════════════════════════════
    // INNER CLASSES
    // ═══════════════════════════════════════════════════════════════════

    public static class TransmutationResult {
        public boolean success;
        public String sourceLanguage;
        public String sourceFile;
        public String targetClassName;
        public String originalCode;
        public String javaCode;
        public String error;
        public int attempts;
        public Class<?> compiledClass;
        
        @Override
        public String toString() {
            return String.format("[%s] %s → %s (%s, %d attempts)",
                success ? "✓" : "✗",
                sourceFile,
                targetClassName,
                sourceLanguage,
                attempts);
        }
    }
    
    private static class CompilationResult {
        boolean success;
        String errors;
        String outputPath;
        Class<?> compiledClass;
    }
}
