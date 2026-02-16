package gemini.root;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

/**
 * THE RUNTIME: GenesisRuntime.java
 * Function: Compiles and loads code dynamically at runtime.
 * Philosophy: The AI doesn't wait for humans to compile. It compiles itself.
 * 
 * Uses javax.tools (part of JDK, not external dependency).
 */
public class GenesisRuntime {

    private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private static String outputDir = "genesis_out";

    static {
        new File(outputDir).mkdirs();
    }

    /**
     * COMPILE AND LOAD: Takes source code string, compiles it, loads the class.
     * @param className Full class name (e.g., "gemini.root.Tesseract")
     * @param sourceCode The Java source code
     * @return The loaded Class object
     */
    public static Class<?> compileAndLoad(String className, String sourceCode) throws Exception {
        if (compiler == null) {
            throw new RuntimeException("No Java compiler available. Run with JDK, not JRE.");
        }

        // 1. Write source to temp file
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        String packagePath = className.substring(0, className.lastIndexOf('.')).replace('.', File.separatorChar);
        File packageDir = new File(outputDir + File.separator + packagePath);
        packageDir.mkdirs();
        
        File sourceFile = new File(packageDir, simpleClassName + ".java");
        try (FileWriter writer = new FileWriter(sourceFile)) {
            writer.write(sourceCode);
        }

        // 2. Compile
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        
        Iterable<? extends JavaFileObject> compilationUnits = 
            fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
        
        List<String> options = Arrays.asList("-d", outputDir);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        
        boolean success = task.call();
        fileManager.close();

        if (!success) {
            StringBuilder errors = new StringBuilder("Compilation failed:\n");
            for (Diagnostic<?> d : diagnostics.getDiagnostics()) {
                errors.append(d.toString()).append("\n");
            }
            throw new RuntimeException(errors.toString());
        }

        System.out.println(">>> [RUNTIME] Compiled: " + className);

        // 3. Load the class
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(outputDir).toURI().toURL()});
        Class<?> loadedClass = classLoader.loadClass(className);
        
        System.out.println(">>> [RUNTIME] Loaded: " + className);
        return loadedClass;
    }

    /**
     * EXECUTE: Run a static method on a dynamically loaded class
     */
    public static Object execute(Class<?> clazz, String methodName, Object... args) throws Exception {
        // Find the method
        Method method = null;
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                method = m;
                break;
            }
        }
        
        if (method == null) {
            throw new RuntimeException("Method not found: " + methodName);
        }

        method.setAccessible(true);
        
        // Invoke (static methods use null as instance)
        Object result = method.invoke(null, args);
        System.out.println(">>> [RUNTIME] Executed: " + methodName);
        return result;
    }

    /**
     * QUICK EVAL: Compile and execute in one step
     */
    public static Object eval(String className, String sourceCode, String methodName, Object... args) throws Exception {
        Class<?> clazz = compileAndLoad(className, sourceCode);
        return execute(clazz, methodName, args);
    }

    /**
     * SET OUTPUT DIRECTORY
     */
    public static void setOutputDirectory(String dir) {
        outputDir = dir;
        new File(dir).mkdirs();
    }
}
