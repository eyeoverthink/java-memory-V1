package fraymus.core;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 🏺 THE JAR ABSORBER - Gen 152
 * "I know Kung Fu."
 * 
 * FUNCTION:
 * 1. INGEST: Reads a .jar file from disk.
 * 2. LOAD: Injects the classes into Fraymus's runtime memory.
 * 3. INDEX: Scans for 'main' methods or specific interfaces (like PApplet).
 * 4. EXECUTE: Runs the library as if it were native code.
 * 
 * Fraymus essentially "installs" new software into its own brain without restarting.
 */
public class FrayJarAbsorber {

    private final Map<String, URLClassLoader> absorbedJars = new HashMap<>();
    private final Map<String, List<Class<?>>> executableClasses = new HashMap<>();
    private final Map<String, List<Class<?>>> processingClasses = new HashMap<>();

    /**
     * CONSUME LIBRARY
     * @param jarPath Path to the .jar file (e.g., "libs/processing-core.jar")
     * @return true if absorption was successful
     */
    public boolean absorb(String jarPath) {
        File file = new File(jarPath);
        if (!file.exists()) {
            System.err.println("❌ TARGET MISSING: " + jarPath);
            return false;
        }

        String jarName = file.getName();
        
        if (absorbedJars.containsKey(jarPath)) {
            System.out.println("⚠️ ALREADY ABSORBED: [" + jarName + "]");
            return true;
        }

        System.out.println("⚡ ABSORBING JAVA ARTIFACT: [" + jarName + "]...");

        try {
            // 1. EXTEND THE CLASSPATH DYNAMICALLY
            URL url = file.toURI().toURL();
            URLClassLoader child = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
            absorbedJars.put(jarPath, child);

            List<Class<?>> executables = new ArrayList<>();
            List<Class<?>> sketches = new ArrayList<>();
            int classCount = 0;

            // 2. SCAN THE BRAIN (Look for capabilities)
            try (JarFile jar = new JarFile(file)) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName()
                            .replace('/', '.')
                            .replace(".class", "");
                        
                        // Skip inner classes for main scan
                        if (className.contains("$")) continue;
                        
                        try {
                            Class<?> cls = Class.forName(className, false, child);
                            classCount++;
                            
                            // CHECK: Is this a Processing Sketch?
                            if (isProcessingSketch(cls)) {
                                System.out.println("   🎨 FOUND VISUAL CORTEX: " + className);
                                sketches.add(cls);
                            }
                            
                            // CHECK: Does it have a main method?
                            if (hasMain(cls)) {
                                System.out.println("   🚀 FOUND EXECUTABLE: " + className);
                                executables.add(cls);
                            }
                            
                            // CHECK: Is it a runnable?
                            if (Runnable.class.isAssignableFrom(cls) && !cls.isInterface()) {
                                System.out.println("   🔄 FOUND RUNNABLE: " + className);
                            }
                            
                        } catch (Throwable t) {
                            // Ignore classes that can't load (dependencies missing)
                        }
                    }
                }
            }
            
            executableClasses.put(jarPath, executables);
            processingClasses.put(jarPath, sketches);
            
            System.out.println("   📊 SCANNED: " + classCount + " classes");
            System.out.println("   🚀 EXECUTABLES: " + executables.size());
            System.out.println("   🎨 SKETCHES: " + sketches.size());
            System.out.println("✅ ASSIMILATION COMPLETE. Library is now Part of Us.");
            
            return true;

        } catch (Exception e) {
            System.err.println("❌ REJECTION: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Execute a main class from an absorbed JAR
     * @param jarPath The absorbed JAR path
     * @param className The fully qualified class name
     * @param args Arguments to pass to main
     */
    public void executeMain(String jarPath, String className, String[] args) {
        URLClassLoader loader = absorbedJars.get(jarPath);
        if (loader == null) {
            System.err.println("❌ JAR NOT ABSORBED: " + jarPath);
            return;
        }

        try {
            Class<?> cls = Class.forName(className, true, loader);
            Method main = cls.getMethod("main", String[].class);
            
            System.out.println("🚀 EXECUTING: " + className);
            main.invoke(null, (Object) args);
            
        } catch (Exception e) {
            System.err.println("❌ EXECUTION FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Run a Processing sketch from an absorbed JAR
     * @param jarPath The absorbed JAR path
     * @param sketchClassName The sketch class name
     */
    public void runProcessingSketch(String jarPath, String sketchClassName) {
        URLClassLoader loader = absorbedJars.get(jarPath);
        if (loader == null) {
            System.err.println("❌ JAR NOT ABSORBED: " + jarPath);
            return;
        }

        try {
            // Load Processing's PApplet
            Class<?> pappletClass = Class.forName("processing.core.PApplet", true, loader);
            Class<?> sketchClass = Class.forName(sketchClassName, true, loader);
            
            // Create sketch instance
            Object sketch = sketchClass.getDeclaredConstructor().newInstance();
            
            // Call PApplet.runSketch(String[] args, PApplet sketch)
            Method runSketch = pappletClass.getMethod("runSketch", String[].class, pappletClass);
            String[] args = new String[]{"--present", sketchClassName};
            
            System.out.println("🎨 LAUNCHING VISUAL CORTEX: " + sketchClassName);
            runSketch.invoke(null, args, sketch);
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ PROCESSING NOT FOUND. Absorb processing-core.jar first.");
        } catch (Exception e) {
            System.err.println("❌ SKETCH LAUNCH FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load a class from an absorbed JAR
     * @param jarPath The absorbed JAR path
     * @param className The fully qualified class name
     * @return The loaded class, or null if not found
     */
    public Class<?> loadClass(String jarPath, String className) {
        URLClassLoader loader = absorbedJars.get(jarPath);
        if (loader == null) {
            System.err.println("❌ JAR NOT ABSORBED: " + jarPath);
            return null;
        }

        try {
            return Class.forName(className, true, loader);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ CLASS NOT FOUND: " + className);
            return null;
        }
    }

    /**
     * Get all executable classes from an absorbed JAR
     */
    public List<Class<?>> getExecutables(String jarPath) {
        return executableClasses.getOrDefault(jarPath, new ArrayList<>());
    }

    /**
     * Get all Processing sketches from an absorbed JAR
     */
    public List<Class<?>> getSketches(String jarPath) {
        return processingClasses.getOrDefault(jarPath, new ArrayList<>());
    }

    /**
     * List all absorbed JARs
     */
    public void listAbsorbed() {
        System.out.println("\n🧠 ABSORBED ARTIFACTS:");
        System.out.println("══════════════════════════════════════");
        
        if (absorbedJars.isEmpty()) {
            System.out.println("   (none)");
            return;
        }
        
        for (String path : absorbedJars.keySet()) {
            List<Class<?>> execs = executableClasses.getOrDefault(path, new ArrayList<>());
            List<Class<?>> sketches = processingClasses.getOrDefault(path, new ArrayList<>());
            
            System.out.println("   📦 " + new File(path).getName());
            System.out.println("      → " + execs.size() + " executables, " + sketches.size() + " sketches");
        }
    }

    /**
     * Absorb all JARs from a directory
     * @param libDir Path to directory containing JARs
     */
    public void absorbDirectory(String libDir) {
        File dir = new File(libDir);
        if (!dir.isDirectory()) {
            System.err.println("❌ NOT A DIRECTORY: " + libDir);
            return;
        }

        File[] jars = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            System.out.println("⚠️ NO JARS FOUND IN: " + libDir);
            return;
        }

        System.out.println("🔮 MASS ABSORPTION FROM: " + libDir);
        for (File jar : jars) {
            absorb(jar.getAbsolutePath());
        }
    }

    /**
     * Release an absorbed JAR (cleanup)
     */
    public void release(String jarPath) {
        URLClassLoader loader = absorbedJars.remove(jarPath);
        if (loader != null) {
            try {
                loader.close();
                executableClasses.remove(jarPath);
                processingClasses.remove(jarPath);
                System.out.println("🗑️ RELEASED: " + new File(jarPath).getName());
            } catch (Exception e) {
                System.err.println("⚠️ RELEASE WARNING: " + e.getMessage());
            }
        }
    }

    // --- HEURISTICS ---

    private boolean isProcessingSketch(Class<?> cls) {
        // Processing sketches usually extend 'PApplet'
        Class<?> superclass = cls.getSuperclass();
        while (superclass != null) {
            if (superclass.getName().equals("processing.core.PApplet")) return true;
            superclass = superclass.getSuperclass();
        }
        return false;
    }

    private boolean hasMain(Class<?> cls) {
        try {
            Method m = cls.getMethod("main", String[].class);
            return Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers());
        } catch (Exception e) {
            return false;
        }
    }

    // --- DEMO ---

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🏺 FRAY-JAR-ABSORBER - Gen 152                               ║");
        System.out.println("║  \"I know Kung Fu.\"                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        FrayJarAbsorber absorber = new FrayJarAbsorber();

        if (args.length > 0) {
            // Absorb specified JAR
            absorber.absorb(args[0]);
            
            if (args.length > 1) {
                // Execute specified class
                absorber.executeMain(args[0], args[1], new String[0]);
            }
        } else {
            // Demo: Absorb all JARs from libs/
            absorber.absorbDirectory("libs");
        }

        absorber.listAbsorbed();
    }
}
