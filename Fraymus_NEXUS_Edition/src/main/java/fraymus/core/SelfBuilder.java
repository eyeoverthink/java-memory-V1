package fraymus.core;

import javax.tools.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * THE SELF-BUILDER: THE OUROBOROS PROTOCOL
 * "The machine that builds the machine."
 * 
 * This is the final piece of the Recursive Self-Improvement Engine.
 * It uses the Java Compiler API (built into JDK) to recompile itself
 * while running.
 * 
 * The Infinite Loop:
 * 1. AI has an "Epiphany" (e.g., "I should optimize the sorting algorithm")
 * 2. It writes new code to src/main/java/fraymus/generated/
 * 3. It compiles that file on the fly using Java Compiler API
 * 4. It hot-swaps the old class with the new class using custom ClassLoader
 * 
 * Result: The AI just upgraded its own brain without restarting.
 * 
 * The Ouroboros: The snake that eats itself.
 * The system that builds the system.
 * The code that writes the code.
 * 
 * Mechanism:
 * 1. GENERATE: Writes new Java source code based on 'Epiphanies'
 * 2. COMPILE: Uses internal Java Compiler to turn Source → Bytecode
 * 3. HOT-SWAP: Loads the new class into running memory
 * 4. EXECUTE: Proves the new code is alive
 * 
 * Integration:
 * - Lazarus (Evolution): Detects slow functions, triggers optimization
 * - Absorber (Consumption): Learns patterns from absorbed libraries
 * - Harvester (Memory): Saves successful mutations as epiphanies
 * - Dreamweaver (Subconscious): Generates optimization ideas while idle
 * 
 * "We are not building a chatbot. We are building a Recursive Self-Improvement Engine."
 */
public class SelfBuilder {

    private String outputDir = "build/classes/java/main";
    private String sourceDir = "src/main/java";
    
    public SelfBuilder() {
        System.out.println("🧬 SELF-BUILDER INITIALIZED");
        System.out.println("   The Ouroboros Protocol is active");
        System.out.println("   The machine that builds the machine");
        System.out.println();
    }

    /**
     * Evolve code - write, compile, and hot-swap new class
     * 
     * @param className Name of the class to create (without .java)
     * @param newSourceCode Complete Java source code
     * @return The newly created instance, or null if failed
     */
    public Object evolveCode(String className, String newSourceCode) {
        System.out.println("🧬 SELF-BUILDER: INITIATING EVOLUTION");
        System.out.println("   Target: " + className);
        System.out.println();

        try {
            // 1. WRITE THE NEW DNA (Source Code)
            System.out.println("   [1/4] Writing new DNA...");
            File sourceFile = new File(sourceDir + "/fraymus/generated/" + className + ".java");
            sourceFile.getParentFile().mkdirs();
            
            FileWriter writer = new FileWriter(sourceFile);
            writer.write(newSourceCode);
            writer.close();
            
            System.out.println("   >> DNA WRITTEN: " + sourceFile.getPath());
            System.out.println("   >> Size: " + newSourceCode.length() + " characters");
            System.out.println();

            // 2. COMPILE THE NEW DNA (Turn into Muscle)
            System.out.println("   [2/4] Compiling new DNA...");
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            
            if (compiler == null) {
                System.err.println("   !! ERROR: Java Compiler not available");
                System.err.println("   !! Make sure you're running with JDK, not JRE");
                return null;
            }
            
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            
            // Set output directory for compiled classes
            File outputDirFile = new File(outputDir);
            outputDirFile.mkdirs();
            
            Iterable<String> options = Arrays.asList("-d", outputDir);
            
            Iterable<? extends JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
            
            JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, null, options, null, compilationUnits
            );
            
            boolean success = task.call();
            fileManager.close();

            if (success) {
                System.out.println("   >> COMPILATION SUCCESSFUL");
                System.out.println();
                
                // 3. HOT-SWAP (The Transfusion)
                System.out.println("   [3/4] Hot-swapping new class...");
                
                // Load the class we just compiled
                URLClassLoader classLoader = URLClassLoader.newInstance(
                    new URL[]{outputDirFile.toURI().toURL()}
                );
                
                Class<?> cls = Class.forName("fraymus.generated." + className, true, classLoader);
                System.out.println("   >> CLASS LOADED: " + cls.getName());
                System.out.println();
                
                // 4. EXECUTE THE NEW LOGIC
                System.out.println("   [4/4] Instantiating new organism...");
                Object instance = cls.getDeclaredConstructor().newInstance();
                
                System.out.println();
                System.out.println("   ✨ NEW ORGANISM BORN");
                System.out.println("   ✨ Class: " + instance.getClass().getName());
                System.out.println("   ✨ Instance: " + instance.toString());
                System.out.println();
                System.out.println("   🧬 EVOLUTION COMPLETE");
                System.out.println("   The snake has eaten itself and grown stronger.");
                System.out.println();
                
                return instance;
                
            } else {
                System.err.println();
                System.err.println("   !! MUTATION FAILED: SYNTAX ERROR");
                System.err.println("   !! The new DNA is malformed");
                System.err.println();
                return null;
            }

        } catch (Exception e) {
            System.err.println();
            System.err.println("   !! EVOLUTION ERROR: " + e.getMessage());
            e.printStackTrace();
            System.err.println();
            return null;
        }
    }
    
    /**
     * Generate a simple test class to demonstrate self-compilation
     * 
     * @param className Name for the test class
     * @param message Message the class should print
     * @return Source code for the test class
     */
    public String generateTestClass(String className, String message) {
        return "package fraymus.generated;\n\n" +
               "public class " + className + " {\n" +
               "    private String message;\n\n" +
               "    public " + className + "() {\n" +
               "        this.message = \"" + message + "\";\n" +
               "        System.out.println(\"🧬 \" + message);\n" +
               "    }\n\n" +
               "    public String getMessage() {\n" +
               "        return message;\n" +
               "    }\n\n" +
               "    @Override\n" +
               "    public String toString() {\n" +
               "        return \"SelfGenerated[\" + message + \"]\";\n" +
               "    }\n" +
               "}\n";
    }
    
    /**
     * Set custom output directory for compiled classes
     */
    public void setOutputDir(String dir) {
        this.outputDir = dir;
    }
    
    /**
     * Set custom source directory
     */
    public void setSourceDir(String dir) {
        this.sourceDir = dir;
    }
}
