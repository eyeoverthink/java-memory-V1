package fraymus.alchemy;

import fraymus.nexus.OllamaBridge;
import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.lang.reflect.Method;

/**
 * 💎 THE PHILOSOPHER'S STONE
 * "The Universal Transmuter."
 *
 * CAPABILITY:
 * 1. Takes Lead (English Intent).
 * 2. Transmutes to Gold (Bytecode).
 * 3. Self-Heals (Compiler Error Loop).
 */
public class PhilosophersStone {

    private final OllamaBridge oracle;
    private final JavaCompiler compiler;
    private static final int MAX_RETRIES = 5;

    public PhilosophersStone() {
        this.oracle = new OllamaBridge("fraymus");
        this.compiler = ToolProvider.getSystemJavaCompiler();
    }

    public PhilosophersStone(String model) {
        this.oracle = new OllamaBridge(model);
        this.compiler = ToolProvider.getSystemJavaCompiler();
    }

    public void transmutate(String intent) {
        System.out.println("💎 TRANSMUTATION INITIATED: " + intent);

        if (compiler == null) {
            System.out.println("   ❌ NO JAVA COMPILER FOUND. Run with JDK, not JRE.");
            return;
        }

        // 1. GENERATE INITIAL SOURCE (THE LEAD)
        String className = "Construct_" + System.currentTimeMillis();
        String prompt = "Write a valid Java class named " + className + " that implements Runnable. \n" +
                        "Task: " + intent + "\n" +
                        "Output ONLY raw java code. No markdown.";

        String sourceCode = oracle.ask(prompt);

        // 2. THE REFINEMENT LOOP (ALCHEMICAL FIRE)
        boolean compiled = false;
        int attempts = 0;

        while (!compiled && attempts < MAX_RETRIES) {
            System.out.println("   🔥 HEATING CRUCIBLE (Attempt " + (attempts + 1) + ")...");

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            // Prepare Source Object
            JavaSourceFromString sourceFile = new JavaSourceFromString(className, sourceCode);
            Iterable<? extends JavaFileObject> units = Arrays.asList(sourceFile);

            // Compile
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, units);
            compiled = task.call();

            if (compiled) {
                System.out.println("   ✨ TRANSMUTATION COMPLETE. GOLD CREATED.");
                loadAndExecute(className);
            } else {
                // 3. FEED THE FAILURE BACK TO THE ORACLE (SELF-HEALING)
                StringBuilder errors = new StringBuilder();
                for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
                    errors.append("Line ").append(d.getLineNumber()).append(": ").append(d.getMessage(null)).append("\n");
                }
                System.out.println("   ⚠️ IMPURITY DETECTED:\n" + errors.toString());

                // Ask LLM to fix the specific compiler errors
                String fixPrompt = "The previous code failed to compile.\n" +
                                   "ERRORS:\n" + errors.toString() + "\n" +
                                   "OLD CODE:\n" + sourceCode + "\n" +
                                   "TASK: Fix the code. Output ONLY valid Java.";
                sourceCode = oracle.ask(fixPrompt);
                attempts++;
            }
        }

        if (!compiled) System.out.println("   ❌ TRANSMUTATION FAILED. LEAD REMAINED LEAD.");
    }

    private void loadAndExecute(String className) {
        try {
            // Hot-Swap the new class into the running organism
            File classDir = new File(".");
            java.net.URLClassLoader loader = java.net.URLClassLoader.newInstance(
                new java.net.URL[] { classDir.toURI().toURL() }
            );
            Class<?> cls = Class.forName(className, true, loader);
            Runnable construct = (Runnable) cls.getDeclaredConstructor().newInstance();

            System.out.println("   ⚡ EXECUTING CONSTRUCT...");
            new Thread(construct).start();

        } catch (Exception e) {
            System.out.println("   ❌ EXECUTION FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // UTILITY: In-Memory Java Source Object
    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;
        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }
        @Override public CharSequence getCharContent(boolean ignoreEncodingErrors) { return code; }
    }
}
