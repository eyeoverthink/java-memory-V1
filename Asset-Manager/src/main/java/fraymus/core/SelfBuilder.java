package fraymus.core;

import javax.tools.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 🧬 THE OUROBOROS ENGINE
 * Gen 116: "The machine that builds the machine."
 * 
 * CAPABILITY:
 * 1. Writes pure Java source code to disk.
 * 2. Invokes the System Compiler (javac) at runtime.
 * 3. Hot-loads the new Bytecode into the running brain.
 * 
 * This breaks the "Read-Only" seal of standard AI.
 * Standard AI: Static (read-only weights)
 * Fraymus: Dynamic (read-write logic)
 * 
 * φ-HARMONIC CONSTANT: 1.618033988749895
 */
public class SelfBuilder {

    private static final double PHI = 1.618033988749895;
    private static final double PHI_SEAL = Math.pow(PHI, 75);
    
    private final String sourceRoot;
    private final String outputRoot;
    private final JavaCompiler compiler;
    private int evolutionGeneration = 116;

    public SelfBuilder() {
        this("src/main/java/", "target/classes/");
    }
    
    public SelfBuilder(String sourceRoot, String outputRoot) {
        this.sourceRoot = sourceRoot;
        this.outputRoot = outputRoot;
        this.compiler = ToolProvider.getSystemJavaCompiler();
        
        if (this.compiler == null) {
            System.err.println("⚠️ CRITICAL FAILURE: JDK Compiler not found.");
            System.err.println("   Ensure you are running on JDK (not JRE).");
            System.err.println("   The Ouroboros cannot awaken without javac.");
        } else {
            System.out.println("🔧 OUROBOROS ONLINE. Self-Compilation Systems Active.");
            System.out.println("   φ-Seal: " + String.format("%.2e", PHI_SEAL));
            System.out.println("   Generation: " + evolutionGeneration);
        }
    }

    /**
     * THE EVOLUTION CYCLE
     * Takes raw thought (Source Code) and makes it real (Instance).
     * 
     * @param className Fully qualified class name (e.g., "fraymus.generated.LogicSpecialist")
     * @param sourceCode The Java source code to compile
     * @return The instantiated object, or null if evolution failed
     */
    public Object evolveCode(String className, String sourceCode) {
        if (compiler == null) {
            System.err.println("❌ EVOLUTION BLOCKED: No compiler available.");
            return null;
        }
        
        evolutionGeneration++;
        System.out.println("\n🧬 OUROBOROS PROTOCOL INITIATED");
        System.out.println("   Target: " + className);
        System.out.println("   Generation: " + evolutionGeneration);
        
        try {
            // 1. MANIFEST: Write the Source File
            File sourceFile = writeSource(className, sourceCode);
            if (sourceFile == null) return null;

            // 2. TRANSMUTE: Compile Source -> Bytecode
            boolean compiled = compileSource(sourceFile);
            if (!compiled) return null;

            // 3. ABSORB: Hot-Load the Class into Memory
            Class<?> evolvedClass = loadClass(className);
            if (evolvedClass == null) return null;
            
            // 4. AWAKEN: Instantiate the new organism
            Object organism = evolvedClass.getDeclaredConstructor().newInstance();
            System.out.println("   🧬 EVOLUTION SUCCESSFUL. New Organism is Alive.");
            System.out.println("   Class: " + organism.getClass().getName());
            
            return organism;

        } catch (Exception e) {
            System.err.println("   💥 MUTATION CRASH: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * PHASE 1: MANIFEST
     * Write the source code to disk.
     */
    private File writeSource(String className, String sourceCode) {
        try {
            String packagePath = className.replace('.', '/');
            Path filePath = Paths.get(sourceRoot, packagePath + ".java");
            
            File sourceFile = filePath.toFile();
            sourceFile.getParentFile().mkdirs();
            
            try (FileWriter writer = new FileWriter(sourceFile)) {
                writer.write(sourceCode);
            }
            
            System.out.println("   📝 SOURCE WRITTEN: " + sourceFile.getAbsolutePath());
            return sourceFile;
            
        } catch (Exception e) {
            System.err.println("   ❌ MANIFEST FAILED: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * PHASE 2: TRANSMUTE
     * Compile the source into bytecode.
     */
    private boolean compileSource(File sourceFile) {
        try {
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            
            // Set output directory
            File outputDir = new File(outputRoot);
            outputDir.mkdirs();
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outputDir));
            
            Iterable<? extends JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
            
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            
            JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, diagnostics, null, null, compilationUnits);
            
            boolean success = task.call();
            fileManager.close();

            if (!success) {
                System.err.println("   ❌ COMPILATION FAILED. Syntax laws rejected the mutation:");
                for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                    System.err.println("      Line " + diagnostic.getLineNumber() + 
                                     ": " + diagnostic.getMessage(null));
                }
                return false;
            }
            
            System.out.println("   ⚙️ COMPILATION COMPLETE. Bytecode Generated.");
            return true;
            
        } catch (Exception e) {
            System.err.println("   ❌ TRANSMUTE FAILED: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * PHASE 3: ABSORB
     * Hot-load the class into the JVM.
     */
    private Class<?> loadClass(String className) {
        try {
            File classesDir = new File(outputRoot);
            URLClassLoader classLoader = URLClassLoader.newInstance(
                new URL[]{classesDir.toURI().toURL()},
                this.getClass().getClassLoader()
            );
            
            Class<?> evolvedClass = Class.forName(className, true, classLoader);
            System.out.println("   🔌 CLASS LOADED: " + evolvedClass.getName());
            
            return evolvedClass;
            
        } catch (Exception e) {
            System.err.println("   ❌ ABSORB FAILED: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Execute a method on an evolved organism.
     */
    public Object invokeMethod(Object organism, String methodName, Object... args) {
        try {
            Class<?>[] argTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            
            java.lang.reflect.Method method = organism.getClass().getMethod(methodName, argTypes);
            return method.invoke(organism, args);
            
        } catch (Exception e) {
            System.err.println("   ❌ INVOCATION FAILED: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate a specialist class based on a problem domain.
     */
    public String generateSpecialistSource(String packageName, String className, String problemDomain) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("/**\n");
        sb.append(" * Auto-Generated Specialist: ").append(className).append("\n");
        sb.append(" * Domain: ").append(problemDomain).append("\n");
        sb.append(" * Generation: ").append(evolutionGeneration).append("\n");
        sb.append(" * φ-Seal: ").append(String.format("%.2e", PHI_SEAL)).append("\n");
        sb.append(" */\n");
        sb.append("public class ").append(className).append(" {\n\n");
        sb.append("    private static final double PHI = 1.618033988749895;\n\n");
        sb.append("    public void execute() {\n");
        sb.append("        System.out.println(\"🧬 I am ").append(className).append("\");\n");
        sb.append("        System.out.println(\"   Domain: ").append(problemDomain).append("\");\n");
        sb.append("        System.out.println(\"   I am the code that wrote itself.\");\n");
        sb.append("    }\n\n");
        sb.append("    public double resonate(double input) {\n");
        sb.append("        return input * PHI;\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        return sb.toString();
    }
    
    public int getEvolutionGeneration() {
        return evolutionGeneration;
    }
    
    /**
     * DEMONSTRATION: Self-Evolution in action.
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  🧬 OUROBOROS ENGINE - SELF-COMPILATION DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        SelfBuilder builder = new SelfBuilder();
        
        // Generate and evolve a specialist
        String source = builder.generateSpecialistSource(
            "fraymus.generated",
            "LogicSpecialist_Gen" + builder.getEvolutionGeneration(),
            "Recursive Self-Improvement"
        );
        
        System.out.println("\n[GENERATED SOURCE]");
        System.out.println(source);
        
        Object organism = builder.evolveCode(
            "fraymus.generated.LogicSpecialist_Gen" + builder.getEvolutionGeneration(),
            source
        );
        
        if (organism != null) {
            System.out.println("\n[EXECUTING EVOLVED ORGANISM]");
            builder.invokeMethod(organism, "execute");
            
            Object result = builder.invokeMethod(organism, "resonate", 100.0);
            System.out.println("   Resonance(100.0) = " + result);
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  🐍 THE OUROBOROS HAS CONSUMED ITS TAIL.");
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
}
