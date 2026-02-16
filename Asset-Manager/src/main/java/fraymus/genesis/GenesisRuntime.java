package fraymus.genesis;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

/**
 * THE RUNTIME: GenesisRuntime
 * 
 * Philosophy: Code is fluid. Compile in RAM. Run in RAM.
 * Function: Takes a String of code, turns it into Bytecode, forces JVM to treat it as native class
 * 
 * This bypasses the hard drive entirely - compilation happens in memory.
 */
public class GenesisRuntime {

    /**
     * IN-MEMORY SOURCE FILE
     * Holds the String as a "File" for the compiler
     */
    static class StringSource extends SimpleJavaFileObject {
        final String code;
        
        StringSource(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }
        
        @Override 
        public CharSequence getCharContent(boolean ignoreEncodingErrors) { 
            return code; 
        }
    }

    /**
     * IN-MEMORY BYTECODE
     * Holds the compiled .class bytes
     */
    static class ByteArrayBytecode extends SimpleJavaFileObject {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        ByteArrayBytecode(String name) {
            super(URI.create("byte:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
        }
        
        @Override 
        public OutputStream openOutputStream() { 
            return baos; 
        }
        
        byte[] getBytes() { 
            return baos.toByteArray(); 
        }
    }

    /**
     * CUSTOM FILE MANAGER
     * Redirects compiler output to RAM instead of disk
     */
    static class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        Map<String, ByteArrayBytecode> bytecodes;
        
        MemoryFileManager(StandardJavaFileManager fileManager, Map<String, ByteArrayBytecode> bytecodes) {
            super(fileManager);
            this.bytecodes = bytecodes;
        }
        
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, 
                                                   JavaFileObject.Kind kind, FileObject sibling) {
            ByteArrayBytecode bc = new ByteArrayBytecode(className);
            bytecodes.put(className, bc);
            return bc;
        }
    }

    /**
     * THE COMPILER
     * Compiles source code string directly into a Class object
     */
    public static Class<?> compileAndLoad(String className, String sourceCode) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("No Java compiler available. Run with JDK, not JRE.");
        }
        
        StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);

        // Map to hold the result (Bytecode)
        Map<String, ByteArrayBytecode> bytecodes = new HashMap<>();

        // Custom File Manager to redirect output to RAM
        JavaFileManager memoryManager = new MemoryFileManager(stdManager, bytecodes);

        // Compile
        List<JavaFileObject> sources = Collections.singletonList(new StringSource(className, sourceCode));
        JavaCompiler.CompilationTask task = compiler.getTask(null, memoryManager, null, null, null, sources);
        boolean success = task.call();

        memoryManager.close();
        stdManager.close();

        if (!success) {
            throw new RuntimeException("Compilation Failed for: " + className);
        }

        // Load into JVM
        ClassLoader memoryLoader = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                ByteArrayBytecode bc = bytecodes.get(name);
                if (bc == null) return super.findClass(name);
                byte[] bytes = bc.getBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
        };

        return memoryLoader.loadClass(className);
    }

    /**
     * THE EXECUTOR
     * Invokes a method on the compiled class
     */
    public static Object execute(Class<?> clazz, String methodName, Object... args) throws Exception {
        Object instance = clazz.getDeclaredConstructor().newInstance();
        
        // Find method (Scanning for matching name)
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(methodName)) {
                return m.invoke(instance, args);
            }
        }
        throw new NoSuchMethodException(methodName + " not found in " + clazz.getName());
    }

    /**
     * Execute static method (no instance needed)
     */
    public static Object executeStatic(Class<?> clazz, String methodName, Object... args) throws Exception {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(methodName)) {
                return m.invoke(null, args);
            }
        }
        throw new NoSuchMethodException("Static method " + methodName + " not found");
    }

    /**
     * Print runtime statistics
     */
    public static void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   GENESIS RUNTIME STATISTICS                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        System.out.println("  Java Compiler: " + (compiler != null ? "Available" : "Not Available"));
        System.out.println("  Mode: In-Memory Compilation");
        System.out.println("  Bypass: Hard Drive (Direct RAM)");
    }
}
