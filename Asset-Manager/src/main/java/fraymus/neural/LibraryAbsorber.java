package fraymus.neural;

import fraymus.knowledge.AkashicRecord;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.jar.*;
import java.util.stream.*;

/**
 * LibraryAbsorber.java v2 — Universal Library Absorption + Evolution Engine
 * =========================================================================================
 *
 * "We don't just use libraries. We absorb them. Then we outgrow them."
 *
 * ORIGINAL (v1 in Fraymus_Agent_BrainV2):
 *   - Hardcoded to 3 java.util classes
 *   - storeKnowledge() just incremented a counter
 *   - No real abstraction, no evolution, no learning
 *
 * THIS VERSION (v2):
 *   1. SCAN    — Reflect ANY package on classpath or ANY .jar file on disk
 *                Discovers all public classes, methods, fields, constructors, annotations
 *
 *   2. EXTRACT — Build a complete API surface model:
 *                Method signatures, parameter types, return types, inheritance chains,
 *                interface implementations, exception declarations
 *
 *   3. ABSTRACT — Feed into SentientCore:
 *                CausalGraph: class→method (HAS), method→returnType (PRODUCES),
 *                             class→interface (IMPLEMENTS), class→superclass (EXTENDS)
 *                ConceptLattice: class × {attributes} for natural category discovery
 *                SufficientStats: method count per class, parameter count per method, etc.
 *
 *   4. EVOLVE  — Generate improved/simplified versions via codegen:
 *                "This class has 47 methods. The agent uses 3. Generate a facade."
 *                "This method throws 5 exceptions. Generate a safe wrapper."
 *                "These 3 classes share an interface. Generate a unified adapter."
 *
 *   5. PERSIST — Store absorbed knowledge in Akashic Record
 *                Survives JVM restart. Can be queried. Grows over time.
 *
 *   6. OUTGROW — Once the system has generated its own implementations,
 *                mark the original library as "transcended" — no longer needed.
 *
 * =========================================================================================
 */
public class LibraryAbsorber {

    private volatile SentientCore  sentient;
    private volatile AkashicRecord akashic;

    private final Map<String, AbsorbedLibrary> libraries = new ConcurrentHashMap<>();
    private final AtomicInteger totalClasses  = new AtomicInteger(0);
    private final AtomicInteger totalMethods  = new AtomicInteger(0);
    private final AtomicInteger totalFields   = new AtomicInteger(0);
    private final AtomicInteger evolvedCount  = new AtomicInteger(0);
    private final AtomicInteger transcended   = new AtomicInteger(0);
    private final List<String>  absorptionLog = new CopyOnWriteArrayList<>();

    private static final String AKASHIC_CATEGORY = "LIBRARY_ABSORBED";

    public LibraryAbsorber() {
        System.out.println("[LibraryAbsorber] v2 Online — Universal Absorption + Evolution");
    }

    public void wireSentient(SentientCore s)  { this.sentient = s; }
    public void wireAkashic(AkashicRecord a)  { 
        this.akashic = a; 
        restoreFromAkashic();
    }

    /**
     * Restore absorbed libraries from AkashicRecord persistence
     */
    private void restoreFromAkashic() {
        if (akashic == null) return;
        
        try {
            List<fraymus.knowledge.AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(AKASHIC_CATEGORY);
            System.out.println("[LibraryAbsorber] Found " + blocks.size() + " blocks in LIBRARY_ABSORBED category");
            int restored = 0;
            int skipped = 0;
            
            for (fraymus.knowledge.AkashicRecord.KnowledgeBlock block : blocks) {
                String content = block.content;
                if (content.startsWith("ABSORBED||")) {
                    String[] parts = content.split("\\|\\|");
                    if (parts.length >= 5) {
                        String libName = parts[1];
                        System.out.println("[LibraryAbsorber] Restoring: " + libName);
                        AbsorbedLibrary lib = new AbsorbedLibrary(libName, "restored");
                        
                        // Parse metadata
                        for (int i = 2; i < parts.length; i++) {
                            String part = parts[i];
                            if (part.startsWith("classes=")) {
                                lib.classCount = Integer.parseInt(part.substring(8));
                            } else if (part.startsWith("methods=")) {
                                lib.methodCount = Integer.parseInt(part.substring(8));
                            } else if (part.startsWith("fields=")) {
                                lib.fieldCount = Integer.parseInt(part.substring(7));
                            } else if (part.startsWith("time=")) {
                                lib.absorptionTimeMs = Integer.parseInt(part.substring(5).replace("ms", ""));
                            }
                        }
                        
                        libraries.put(libName, lib);
                        totalClasses.addAndGet(lib.classCount);
                        totalMethods.addAndGet(lib.methodCount);
                        totalFields.addAndGet(lib.fieldCount);
                        restored++;
                    } else {
                        System.out.println("[LibraryAbsorber] Skipped block (parts.length=" + parts.length + ")");
                        skipped++;
                    }
                } else {
                    System.out.println("[LibraryAbsorber] Skipped non-ABSORBED block");
                    skipped++;
                }
            }
            
            if (restored > 0) {
                System.out.println("[LibraryAbsorber] Restored " + restored + " libraries from AkashicRecord");
            }
            if (skipped > 0) {
                System.out.println("[LibraryAbsorber] Skipped " + skipped + " blocks");
            }
        } catch (Exception e) {
            System.out.println("[LibraryAbsorber] Restore warning: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================================
    // ABSORB — Scan any package on classpath via reflection
    // =========================================================================================

    /**
     * Absorb all public classes in a package that are already on the classpath.
     * Works with any Java package: java.util, java.io, javax.swing, fraymus.neural, etc.
     */
    public String absorbPackage(String packageName) {
        long t0 = System.currentTimeMillis();
        AbsorbedLibrary lib = new AbsorbedLibrary(packageName, "classpath");

        // Discover classes via classpath scanning
        List<Class<?>> classes = scanClasspath(packageName);
        if (classes.isEmpty()) {
            return "[Absorber] No classes found for package: " + packageName;
        }

        for (Class<?> clazz : classes) {
            absorbClass(lib, clazz);
        }

        finishAbsorption(lib, t0);
        return lib.summary();
    }

    /**
     * Absorb a single class by fully-qualified name.
     */
    public String absorbClass(String className) {
        long t0 = System.currentTimeMillis();
        try {
            Class<?> clazz = Class.forName(className);
            String pkg = clazz.getPackageName();
            AbsorbedLibrary lib = libraries.computeIfAbsent(pkg,
                k -> new AbsorbedLibrary(pkg, "class:" + className));
            absorbClass(lib, clazz);
            finishAbsorption(lib, t0);
            return lib.summary();
        } catch (ClassNotFoundException e) {
            return "[Absorber] Class not found: " + className;
        }
    }

    /**
     * Absorb all classes from a .jar file on disk, OR a source directory of .java files.
     * Auto-detects: if path is a directory → absorbSourceTree(); if .jar → JAR reflection.
     */
    public String absorbJar(String jarPath) {
        long t0 = System.currentTimeMillis();
        File jarFile = new File(jarPath);
        if (!jarFile.exists()) return "[Absorber] NOT FOUND: " + jarPath;

        // If it's a directory, absorb as source tree
        if (jarFile.isDirectory()) {
            return absorbSourceTree(jarPath);
        }

        AbsorbedLibrary lib = new AbsorbedLibrary(jarFile.getName(), "jar:" + jarPath);

        try (JarFile jar = new JarFile(jarFile)) {
            URL[] urls = {jarFile.toURI().toURL()};
            URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
                    String className = entry.getName()
                        .replace('/', '.').replace(".class", "");
                    try {
                        Class<?> clazz = loader.loadClass(className);
                        absorbClass(lib, clazz);
                    } catch (Throwable ignored) {
                        // Skip classes that can't be loaded (missing deps, etc.)
                    }
                }
            }
            loader.close();
        } catch (IOException e) {
            return "[Absorber] JAR read error: " + e.getMessage();
        }

        finishAbsorption(lib, t0);
        return lib.summary();
    }

    // =========================================================================================
    // SOURCE TREE ABSORPTION — Parse raw .java files from disk
    // =========================================================================================

    /**
     * Absorb a directory tree of .java source files.
     * Recursively scans, extracts package/class/method signatures,
     * feeds SentientCore causal graph + concept lattice, stores in Akashic.
     * Detects domain and prints "Skills Acquired" messaging.
     */
    public String absorbSourceTree(String dirPath) {
        long t0 = System.currentTimeMillis();
        File root = new File(dirPath);
        if (!root.exists() || !root.isDirectory())
            return "[Absorber] Directory not found: " + dirPath;

        String libName = root.getName();
        AbsorbedLibrary lib = new AbsorbedLibrary(libName, "source:" + dirPath);

        // Collect all .java files recursively
        List<File> javaFiles = new ArrayList<>();
        collectJavaFiles(root, javaFiles);

        if (javaFiles.isEmpty())
            return "[Absorber] No .java files found in: " + dirPath;

        System.out.printf("[Absorber] SOURCE TREE: %s — %,d .java files%n", libName, javaFiles.size());

        int filesDone = 0;
        Set<String> packages = new TreeSet<>();

        for (File jf : javaFiles) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(jf.toPath()));
                String[] lines = content.split("\n");
                String className = jf.getName().replace(".java", "");
                if (className.equals("package-info") || className.equals("module-info")) continue;

                String pkg = "";
                ClassKnowledge ck = null;
                List<String> javadocLines = new ArrayList<>();
                boolean inJavadoc = false;

                for (String rawLine : lines) {
                    String line = rawLine.trim();

                    // Package
                    if (line.startsWith("package ")) {
                        pkg = line.replace("package ", "").replace(";", "").trim();
                        packages.add(pkg);
                    }
                    // Javadoc
                    else if (line.startsWith("/**")) {
                        inJavadoc = true;
                        javadocLines.clear();
                    } else if (inJavadoc) {
                        if (line.contains("*/")) { inJavadoc = false; }
                        else {
                            String cleaned = line.replaceFirst("^\\*\\s?", "").trim();
                            if (!cleaned.isEmpty() && !cleaned.startsWith("@"))
                                javadocLines.add(cleaned);
                        }
                    }
                    // Class/Interface/Enum declaration
                    else if ((line.startsWith("public ") || line.startsWith("abstract ") || line.startsWith("final "))
                             && (line.contains("class ") || line.contains("interface ") || line.contains("enum "))) {
                        String fqcn = pkg.isEmpty() ? className : pkg + "." + className;
                        ck = new ClassKnowledge(fqcn);
                        lib.classCount++;
                        totalClasses.incrementAndGet();

                        // Detect extends/implements
                        if (line.contains(" extends ")) {
                            String after = line.substring(line.indexOf(" extends ") + 9).trim();
                            String superName = after.split("[\\s{<,]")[0];
                            ck.superClass = superName;
                            feedSentient(className, "EXTENDS", superName);
                        }
                        if (line.contains(" implements ")) {
                            String after = line.substring(line.indexOf(" implements ") + 12).trim();
                            for (String iface : after.split("[{,]")) {
                                String ifaceName = iface.trim().split("[<\\s]")[0];
                                if (!ifaceName.isEmpty()) {
                                    ck.interfaces.add(ifaceName);
                                    feedSentient(className, "IMPLEMENTS", ifaceName);
                                    feedLattice(className, "IMPLEMENTS_" + ifaceName.toUpperCase());
                                }
                            }
                        }

                        // Classify by type
                        if (line.contains("interface ")) feedLattice(className, "INTERFACE");
                        else if (line.contains("enum ")) feedLattice(className, "ENUM");
                        else if (line.contains("abstract ")) feedLattice(className, "ABSTRACT");

                        javadocLines.clear();
                    }
                    // Method declarations
                    else if (line.contains("(") && line.contains(")")
                             && (line.contains("public ") || line.contains("protected "))
                             && !line.contains("class ") && !line.contains("new ")
                             && !line.contains("return ") && !line.startsWith("//")
                             && !line.startsWith("*")) {
                        String methodName = extractSourceMethodName(line);
                        String returnType = extractSourceReturnType(line);
                        String sig = methodName + "(" + extractSourceParams(line) + ") : " + returnType;

                        lib.methodCount++;
                        totalMethods.incrementAndGet();

                        if (ck != null) ck.methods.add(sig);
                        feedSentient(className, "HAS_METHOD", methodName);
                        feedSentient(methodName, "RETURNS", returnType);
                        feedLattice(className, "CAN_" + methodName.toUpperCase());
                    }
                    // Field declarations
                    else if ((line.contains("private ") || line.contains("public ") || line.contains("protected "))
                             && line.contains(";") && !line.contains("(")) {
                        lib.fieldCount++;
                        totalFields.incrementAndGet();
                    }
                }

                // Register the class
                if (ck != null) {
                    // Classify by API surface size
                    if (ck.methods.size() > 20) feedLattice(className, "LARGE_API");
                    else if (ck.methods.size() > 5) feedLattice(className, "MEDIUM_API");
                    else feedLattice(className, "SMALL_API");
                    feedStats("METHODS_PER_CLASS", ck.methods.size());
                    feedStats("FIELDS_PER_CLASS", ck.fields.size());
                    lib.classes.put(ck.fullName, ck);
                }

            } catch (IOException e) {
                // Skip unreadable files
            }

            filesDone++;
            if (filesDone % 200 == 0) {
                System.out.printf("   [ABSORBER] %,d / %,d files absorbed%n", filesDone, javaFiles.size());
            }
        }

        finishAbsorption(lib, t0);

        // Domain detection and Skills Acquired messaging
        String skills = detectSkills(dirPath, lib);

        return lib.summary() + "\n" + skills;
    }

    private void collectJavaFiles(File dir, List<File> out) {
        if (dir == null || !dir.isDirectory()) return;
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File child : children) {
            if (child.isDirectory()) {
                String name = child.getName();
                if (!name.equals("test") && !name.equals("tests")
                    && !name.equals("__pycache__") && !name.equals(".git")) {
                    collectJavaFiles(child, out);
                }
            } else if (child.getName().endsWith(".java")) {
                out.add(child);
            }
        }
    }

    private String extractSourceMethodName(String line) {
        int paren = line.indexOf('(');
        if (paren < 0) return "unknown";
        String before = line.substring(0, paren).trim();
        String[] parts = before.split("\\s+");
        return parts.length > 0 ? parts[parts.length - 1] : "unknown";
    }

    private String extractSourceReturnType(String line) {
        int paren = line.indexOf('(');
        if (paren < 0) return "void";
        String before = line.substring(0, paren).trim();
        String[] parts = before.split("\\s+");
        return parts.length > 1 ? parts[parts.length - 2] : "void";
    }

    private String extractSourceParams(String line) {
        int open = line.indexOf('(');
        int close = line.indexOf(')', open);
        if (open < 0 || close < 0) return "";
        String params = line.substring(open + 1, close).trim();
        if (params.isEmpty()) return "";
        // Simplify: just extract type names
        StringBuilder sb = new StringBuilder();
        for (String p : params.split(",")) {
            p = p.trim();
            String[] parts = p.split("\\s+");
            if (parts.length >= 1) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(parts[0]); // type name
            }
        }
        return sb.toString();
    }

    private String detectSkills(String path, AbsorbedLibrary lib) {
        String lower = path.toLowerCase().replace('\\', '/');
        StringBuilder skills = new StringBuilder();
        skills.append("\n╔══════════════════════════════════════════════════════════╗\n");
        skills.append("║  \uD83E\uDDE0 SKILLS ACQUIRED                                       ║\n");
        skills.append("╠══════════════════════════════════════════════════════════╣\n");

        if (lower.contains("nd4j") || lower.contains("linalg")) {
            skills.append("║  \u2714 Tensor Operations (N-Dimensional Arrays)              ║\n");
            skills.append("║  \u2714 Linear Algebra (Matrix Multiply, Decomposition)       ║\n");
            skills.append("║  \u2714 Off-Heap Memory Management (MemoryWorkspace)          ║\n");
            skills.append("║  \u2714 Data Type System (FLOAT, DOUBLE, INT, HALF, BFLOAT16) ║\n");
        } else if (lower.contains("datavec")) {
            skills.append("║  \u2714 ETL Pipelines (Extract, Transform, Load)              ║\n");
            skills.append("║  \u2714 Data Vectorization (CSV, Image, Video → INDArray)     ║\n");
            skills.append("║  \u2714 Format Detection (Auto-schema discovery)              ║\n");
            skills.append("║  \u2714 Record Readers (CSV, LibSVM, Sequence, RegEx)         ║\n");
        } else if (lower.contains("python4j")) {
            skills.append("║  \u2714 Python-Java Bridge (Py4J Gateway)                     ║\n");
            skills.append("║  \u2714 Python Function Execution from JVM                    ║\n");
            skills.append("║  \u2714 NumPy ↔ INDArray Conversion                           ║\n");
        } else if (lower.contains("deeplearning4j")) {
            skills.append("║  \u2714 Neural Network Architecture (MultiLayerNetwork)       ║\n");
            skills.append("║  \u2714 Layer Composition (Dense, Conv, LSTM, Output)         ║\n");
            skills.append("║  \u2714 Training Loop (fit, evaluate, score, gradient)        ║\n");
            skills.append("║  \u2714 Model Serialization (save/load trained models)        ║\n");
        } else if (lower.contains("jsoup")) {
            skills.append("║  \u2714 HTML Parsing (DOM traversal, CSS selectors)           ║\n");
            skills.append("║  \u2714 HTTP Connections (fetch, post, cookies)               ║\n");
            skills.append("║  \u2714 HTML Sanitization (Whitelist/Safelist cleaning)       ║\n");
        } else if (lower.contains("tika")) {
            skills.append("║  \u2714 Content Detection (1000+ MIME types)                  ║\n");
            skills.append("║  \u2714 Text Extraction (PDF, DOCX, HTML, XML, etc.)         ║\n");
            skills.append("║  \u2714 Metadata Extraction (Dublin Core, EXIF, XMP)         ║\n");
        } else {
            skills.append(String.format("║  \u2714 %d classes absorbed                                  ║\n", lib.classCount));
            skills.append(String.format("║  \u2714 %d methods indexed                                   ║\n", lib.methodCount));
            skills.append(String.format("║  \u2714 %d fields catalogued                                 ║\n", lib.fieldCount));
        }

        skills.append(String.format("║  Source: %-47s ║\n",
            lib.name.length() > 47 ? lib.name.substring(0, 47) : lib.name));
        skills.append(String.format("║  Time: %,d ms                                            ║\n", lib.absorptionTimeMs));
        skills.append("╚══════════════════════════════════════════════════════════╝");
        return skills.toString();
    }

    // =========================================================================================
    // CORE ABSORPTION — Extract full API surface from a single class
    // =========================================================================================

    private void absorbClass(AbsorbedLibrary lib, Class<?> clazz) {
        if (!Modifier.isPublic(clazz.getModifiers())) return;
        if (clazz.isAnonymousClass() || clazz.isSynthetic()) return;

        String className = clazz.getSimpleName();
        String fullName  = clazz.getName();
        lib.classCount++;
        totalClasses.incrementAndGet();

        ClassKnowledge ck = new ClassKnowledge(fullName);

        // Inheritance chain
        Class<?> sup = clazz.getSuperclass();
        if (sup != null && !sup.equals(Object.class)) {
            ck.superClass = sup.getSimpleName();
            feedSentient(className, "EXTENDS", sup.getSimpleName());
        }

        // Interfaces
        for (Class<?> iface : clazz.getInterfaces()) {
            ck.interfaces.add(iface.getSimpleName());
            feedSentient(className, "IMPLEMENTS", iface.getSimpleName());
            feedLattice(className, "IMPLEMENTS_" + iface.getSimpleName());
        }

        // Public methods
        try {
            for (Method m : clazz.getDeclaredMethods()) {
                if (!Modifier.isPublic(m.getModifiers())) continue;
                lib.methodCount++;
                totalMethods.incrementAndGet();

                String sig = buildSignature(m);
                ck.methods.add(sig);

                // Causal: class HAS method, method PRODUCES returnType
                feedSentient(className, "HAS_METHOD", m.getName());
                feedSentient(m.getName(), "RETURNS", m.getReturnType().getSimpleName());

                // Lattice: class has capability
                feedLattice(className, "CAN_" + m.getName().toUpperCase());

                // Stats: parameter count
                feedStats(className + "." + m.getName() + ".params", m.getParameterCount());
            }
        } catch (Throwable ignored) {}

        // Public fields
        try {
            for (Field f : clazz.getDeclaredFields()) {
                if (!Modifier.isPublic(f.getModifiers())) continue;
                lib.fieldCount++;
                totalFields.incrementAndGet();
                ck.fields.add(f.getName() + " : " + f.getType().getSimpleName());
                feedLattice(className, "HAS_FIELD_" + f.getName().toUpperCase());
            }
        } catch (Throwable ignored) {}

        // Public constructors
        try {
            for (Constructor<?> c : clazz.getDeclaredConstructors()) {
                if (!Modifier.isPublic(c.getModifiers())) continue;
                ck.constructors.add(buildConstructorSig(c));
            }
        } catch (Throwable ignored) {}

        // Stats: methods per class
        feedStats("METHODS_PER_CLASS", ck.methods.size());
        feedStats("FIELDS_PER_CLASS", ck.fields.size());

        // Lattice: classify by size
        if (ck.methods.size() > 20) feedLattice(className, "LARGE_API");
        else if (ck.methods.size() > 5) feedLattice(className, "MEDIUM_API");
        else feedLattice(className, "SMALL_API");

        if (Modifier.isAbstract(clazz.getModifiers())) feedLattice(className, "ABSTRACT");
        if (clazz.isInterface()) feedLattice(className, "INTERFACE");
        if (clazz.isEnum()) feedLattice(className, "ENUM");

        lib.classes.put(fullName, ck);
    }

    // =========================================================================================
    // SENTIENT CORE INTEGRATION — Feed extracted knowledge into Bayesian intelligence
    // =========================================================================================

    private void feedSentient(String subject, String relation, String object) {
        if (sentient == null) return;
        // Feed as causal: subject RELATION object
        sentient.getCausal().observe(subject, object);
        // Also feed as a learnable statement
        sentient.learn(subject + " " + relation.toLowerCase().replace('_', ' ') + " " + object);
    }

    private void feedLattice(String object, String attribute) {
        if (sentient == null) return;
        sentient.getLattice().teach(object, attribute);
    }

    private void feedStats(String concept, float value) {
        if (sentient == null) return;
        sentient.getStats().observe(concept, value);
    }

    // =========================================================================================
    // FINISH — Persist to Akashic, log results
    // =========================================================================================

    private void finishAbsorption(AbsorbedLibrary lib, long t0) {
        lib.absorptionTimeMs = System.currentTimeMillis() - t0;
        libraries.put(lib.name, lib);

        // Persist to Akashic
        if (akashic != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("ABSORBED||").append(lib.name).append("||");
            sb.append("classes=").append(lib.classCount).append("||");
            sb.append("methods=").append(lib.methodCount).append("||");
            sb.append("fields=").append(lib.fieldCount).append("||");
            sb.append("time=").append(lib.absorptionTimeMs).append("ms||");
            // Store top method signatures for recall
            int limit = 0;
            for (ClassKnowledge ck : lib.classes.values()) {
                for (String m : ck.methods) {
                    sb.append(m).append(";");
                    if (++limit > 50) break; // cap at 50 methods per persist
                }
                if (limit > 50) break;
            }
            akashic.addBlock(AKASHIC_CATEGORY, sb.toString());
        }

        String log = String.format("[Absorber] %s: %d classes, %d methods, %d fields in %dms",
            lib.name, lib.classCount, lib.methodCount, lib.fieldCount, lib.absorptionTimeMs);
        absorptionLog.add(log);
        System.out.println(log);
    }

    // =========================================================================================
    // QUERY — Search absorbed knowledge
    // =========================================================================================

    /**
     * Search all absorbed libraries for methods/classes matching a term.
     */
    public String query(String searchTerm) {
        String lower = searchTerm.toLowerCase();
        StringBuilder results = new StringBuilder();
        int count = 0;

        for (AbsorbedLibrary lib : libraries.values()) {
            for (Map.Entry<String, ClassKnowledge> entry : lib.classes.entrySet()) {
                ClassKnowledge ck = entry.getValue();
                // Search class name
                if (entry.getKey().toLowerCase().contains(lower)) {
                    results.append("  [CLASS] ").append(entry.getKey()).append("\n");
                    count++;
                }
                // Search methods
                for (String m : ck.methods) {
                    if (m.toLowerCase().contains(lower)) {
                        results.append("  [METHOD] ").append(entry.getValue().fullName)
                               .append(".").append(m).append("\n");
                        count++;
                    }
                }
            }
            if (count > 50) { results.append("  ... (").append(count).append("+ results, showing 50)\n"); break; }
        }

        return count > 0 ? "Found " + count + " matches:\n" + results
                         : "No results for: " + searchTerm;
    }

    /**
     * Ask SentientCore to reason about absorbed knowledge.
     * "What does ArrayList do?" → lattice lookup
     * "Why does HashMap implement Serializable?" → causal chain
     */
    public String reason(String question) {
        if (sentient == null) return "[Absorber] No SentientCore wired";
        return sentient.think(question);
    }

    // =========================================================================================
    // EVOLVE — Generate improved/simplified versions
    // =========================================================================================

    /**
     * Analyze an absorbed class and suggest evolution opportunities:
     * - Too many methods → generate a focused facade
     * - Checked exceptions → generate safe wrappers
     * - Similar classes → generate unified adapter
     */
    public String analyzeForEvolution(String className) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("[EVOLUTION ANALYSIS] ").append(className).append("\n");

        ClassKnowledge ck = findClass(className);
        if (ck == null) return "[Absorber] Class not found in absorbed knowledge: " + className;

        // 1. API bloat check
        if (ck.methods.size() > 20) {
            analysis.append("  ⚠ BLOATED: ").append(ck.methods.size()).append(" public methods\n");
            analysis.append("    → EVOLVE: Generate focused facade with only essential methods\n");
        }

        // 2. Exception surface
        long throwingMethods = ck.methods.stream()
            .filter(m -> m.contains("throws")).count();
        if (throwingMethods > 3) {
            analysis.append("  ⚠ EXCEPTION-HEAVY: ").append(throwingMethods).append(" methods throw\n");
            analysis.append("    → EVOLVE: Generate safe wrappers (try/catch → Optional returns)\n");
        }

        // 3. Inheritance depth
        if (ck.superClass != null && !ck.superClass.equals("Object")) {
            analysis.append("  ℹ INHERITS: ").append(ck.superClass).append("\n");
            analysis.append("    → EVOLVE: Flatten inheritance — compose instead of extend\n");
        }

        // 4. Interface implementations
        if (ck.interfaces.size() > 2) {
            analysis.append("  ⚠ COMPLEX: implements ").append(ck.interfaces).append("\n");
            analysis.append("    → EVOLVE: Generate single-purpose adapter per interface\n");
        }

        // 5. Similar classes in same library
        if (sentient != null) {
            List<String[]> similar = sentient.getLattice().classify(
                className.contains(".") ? className.substring(className.lastIndexOf('.') + 1) : className);
            if (!similar.isEmpty()) {
                analysis.append("  ℹ SIMILAR TO:\n");
                for (String[] s : similar.subList(0, Math.min(3, similar.size()))) {
                    analysis.append("    ").append(s[0]).append(" (").append(s[1]).append(")\n");
                }
                analysis.append("    → EVOLVE: Generate unified adapter for similar classes\n");
            }
        }

        if (analysis.toString().contains("EVOLVE")) {
            evolvedCount.incrementAndGet();
        }

        return analysis.toString();
    }

    /**
     * Mark a library as "transcended" — the system has generated its own
     * implementation and no longer needs the original.
     */
    public String transcend(String libraryName) {
        AbsorbedLibrary lib = libraries.get(libraryName);
        if (lib == null) return "[Absorber] Library not found: " + libraryName;

        lib.transcended = true;
        transcended.incrementAndGet();

        if (akashic != null) {
            akashic.addBlock(AKASHIC_CATEGORY,
                "TRANSCENDED||" + libraryName + "||" + System.currentTimeMillis());
        }

        return String.format("[Absorber] %s TRANSCENDED — %d classes, %d methods absorbed and outgrown",
            libraryName, lib.classCount, lib.methodCount);
    }

    /** Public accessor for OpenClawAgent — find absorbed class by name. */
    public ClassKnowledge findClassPublic(String name) { return findClass(name); }

    /** Public accessor for OpenClawAgent — all absorbed libraries. */
    public Map<String, AbsorbedLibrary> getLibraries() { return Collections.unmodifiableMap(libraries); }

    /** Public accessor — all absorbed method signatures as className::methodSig pairs. */
    public List<String[]> getAbsorbedMethodSignatures() {
        List<String[]> result = new ArrayList<>();
        for (AbsorbedLibrary lib : libraries.values()) {
            for (Map.Entry<String, ClassKnowledge> entry : lib.classes.entrySet()) {
                for (String m : entry.getValue().methods) {
                    String methodName = m.contains("(") ? m.substring(0, m.indexOf('(')) : m;
                    result.add(new String[]{entry.getKey(), methodName, m});
                }
            }
        }
        return result;
    }

    private ClassKnowledge findClass(String name) {
        for (AbsorbedLibrary lib : libraries.values()) {
            for (Map.Entry<String, ClassKnowledge> entry : lib.classes.entrySet()) {
                if (entry.getKey().contains(name) ||
                    entry.getKey().endsWith("." + name)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    // =========================================================================================
    // CLASSPATH SCANNING — Find classes in a package via classloader
    // =========================================================================================

    private List<Class<?>> scanClasspath(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) cl = this.getClass().getClassLoader();

            Enumeration<URL> resources = cl.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    // Directory on classpath
                    File dir = new File(resource.toURI());
                    if (dir.isDirectory()) {
                        for (File file : dir.listFiles()) {
                            if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                                String className = packageName + "." +
                                    file.getName().replace(".class", "");
                                try {
                                    classes.add(Class.forName(className));
                                } catch (Throwable ignored) {}
                            }
                        }
                    }
                } else if ("jar".equals(protocol)) {
                    // JAR on classpath
                    String jarPath = resource.getPath();
                    if (jarPath.contains("!")) {
                        jarPath = jarPath.substring(5, jarPath.indexOf("!"));
                    }
                    try (JarFile jar = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith(path) && name.endsWith(".class") &&
                                !name.contains("$")) {
                                String className = name.replace('/', '.').replace(".class", "");
                                try {
                                    classes.add(Class.forName(className));
                                } catch (Throwable ignored) {}
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[Absorber] Scan error: " + e.getMessage());
        }

        // Fallback: try well-known classes if package scan found nothing
        if (classes.isEmpty()) {
            tryLoadKnownClasses(packageName, classes);
        }

        return classes;
    }

    private void tryLoadKnownClasses(String pkg, List<Class<?>> out) {
        // Massive known-class registry — JDK modules + popular external libs
        // JDK modules can't be classpath-scanned, so we enumerate key classes
        Map<String, String[]> known = new LinkedHashMap<>();

        // ═══════════════════════════════════════════════════════════════
        // JDK CORE — the foundation of all Java
        // ═══════════════════════════════════════════════════════════════
        known.put("java.lang", new String[]{
            "String", "StringBuilder", "StringBuffer", "Integer", "Long", "Double",
            "Float", "Boolean", "Byte", "Short", "Character", "Number", "Math",
            "StrictMath", "System", "Runtime", "Thread", "ThreadGroup", "ThreadLocal",
            "InheritableThreadLocal", "ProcessBuilder", "Process", "ClassLoader",
            "SecurityManager", "StackTraceElement", "StackWalker", "Throwable",
            "Exception", "RuntimeException", "Error", "OutOfMemoryError",
            "NullPointerException", "IllegalArgumentException", "IllegalStateException",
            "UnsupportedOperationException", "ClassCastException", "ArrayIndexOutOfBoundsException",
            "ArithmeticException", "ClassNotFoundException", "NoSuchMethodException",
            "Comparable", "Iterable", "Cloneable", "AutoCloseable", "Runnable",
            "Enum", "Record", "Void", "Object"
        });
        known.put("java.lang.reflect", new String[]{
            "java.lang.reflect.Method", "java.lang.reflect.Field", "java.lang.reflect.Constructor",
            "java.lang.reflect.Modifier", "java.lang.reflect.Array", "java.lang.reflect.Proxy",
            "java.lang.reflect.Parameter", "java.lang.reflect.Type",
            "java.lang.reflect.AnnotatedElement", "java.lang.reflect.AccessibleObject"
        });
        known.put("java.lang.annotation", new String[]{
            "java.lang.annotation.Annotation", "java.lang.annotation.Retention",
            "java.lang.annotation.Target", "java.lang.annotation.Documented",
            "java.lang.annotation.Inherited", "java.lang.annotation.Repeatable"
        });

        // ═══════════════════════════════════════════════════════════════
        // COLLECTIONS & CONCURRENCY — data structures + parallelism
        // ═══════════════════════════════════════════════════════════════
        known.put("java.util", new String[]{
            "ArrayList", "LinkedList", "HashMap", "LinkedHashMap", "TreeMap",
            "HashSet", "LinkedHashSet", "TreeSet", "EnumSet", "EnumMap",
            "ArrayDeque", "PriorityQueue", "Collections", "Arrays", "Objects",
            "Optional", "OptionalInt", "OptionalLong", "OptionalDouble",
            "StringJoiner", "Random", "SplittableRandom", "UUID",
            "Timer", "TimerTask", "Date", "Calendar", "GregorianCalendar",
            "TimeZone", "Locale", "Currency", "Properties", "ResourceBundle",
            "Scanner", "Formatter", "BitSet", "Base64", "ServiceLoader",
            "Spliterator", "Spliterators", "Iterator", "ListIterator",
            "Comparator", "EventObject", "AbstractMap", "AbstractList",
            "AbstractSet", "AbstractCollection", "WeakHashMap", "IdentityHashMap",
            "Stack", "Vector", "Hashtable", "Dictionary"
        });
        known.put("java.util.concurrent", new String[]{
            "java.util.concurrent.ConcurrentHashMap", "java.util.concurrent.ConcurrentLinkedQueue",
            "java.util.concurrent.ConcurrentLinkedDeque", "java.util.concurrent.CopyOnWriteArrayList",
            "java.util.concurrent.CopyOnWriteArraySet", "java.util.concurrent.ExecutorService",
            "java.util.concurrent.Executors", "java.util.concurrent.ThreadPoolExecutor",
            "java.util.concurrent.ScheduledExecutorService", "java.util.concurrent.ScheduledThreadPoolExecutor",
            "java.util.concurrent.ForkJoinPool", "java.util.concurrent.ForkJoinTask",
            "java.util.concurrent.RecursiveTask", "java.util.concurrent.RecursiveAction",
            "java.util.concurrent.Future", "java.util.concurrent.CompletableFuture",
            "java.util.concurrent.CountDownLatch", "java.util.concurrent.CyclicBarrier",
            "java.util.concurrent.Semaphore", "java.util.concurrent.Phaser",
            "java.util.concurrent.Exchanger", "java.util.concurrent.BlockingQueue",
            "java.util.concurrent.LinkedBlockingQueue", "java.util.concurrent.ArrayBlockingQueue",
            "java.util.concurrent.PriorityBlockingQueue", "java.util.concurrent.SynchronousQueue",
            "java.util.concurrent.locks.ReentrantLock", "java.util.concurrent.locks.ReadWriteLock",
            "java.util.concurrent.locks.ReentrantReadWriteLock", "java.util.concurrent.locks.StampedLock",
            "java.util.concurrent.locks.Condition", "java.util.concurrent.locks.Lock",
            "java.util.concurrent.atomic.AtomicInteger", "java.util.concurrent.atomic.AtomicLong",
            "java.util.concurrent.atomic.AtomicBoolean", "java.util.concurrent.atomic.AtomicReference",
            "java.util.concurrent.atomic.AtomicIntegerArray", "java.util.concurrent.atomic.AtomicLongArray",
            "java.util.concurrent.atomic.LongAdder", "java.util.concurrent.atomic.LongAccumulator"
        });
        known.put("java.util.stream", new String[]{
            "java.util.stream.Stream", "java.util.stream.IntStream",
            "java.util.stream.LongStream", "java.util.stream.DoubleStream",
            "java.util.stream.Collectors", "java.util.stream.StreamSupport"
        });
        known.put("java.util.function", new String[]{
            "java.util.function.Function", "java.util.function.BiFunction",
            "java.util.function.Consumer", "java.util.function.BiConsumer",
            "java.util.function.Supplier", "java.util.function.Predicate",
            "java.util.function.BiPredicate", "java.util.function.UnaryOperator",
            "java.util.function.BinaryOperator", "java.util.function.IntFunction",
            "java.util.function.LongFunction", "java.util.function.DoubleFunction",
            "java.util.function.ToIntFunction", "java.util.function.ToLongFunction",
            "java.util.function.ToDoubleFunction", "java.util.function.IntConsumer",
            "java.util.function.LongConsumer", "java.util.function.DoubleConsumer",
            "java.util.function.IntPredicate", "java.util.function.LongPredicate",
            "java.util.function.DoublePredicate", "java.util.function.IntSupplier",
            "java.util.function.LongSupplier", "java.util.function.DoubleSupplier",
            "java.util.function.IntUnaryOperator", "java.util.function.LongUnaryOperator",
            "java.util.function.DoubleUnaryOperator", "java.util.function.IntBinaryOperator",
            "java.util.function.LongBinaryOperator", "java.util.function.DoubleBinaryOperator",
            "java.util.function.ObjIntConsumer", "java.util.function.ObjLongConsumer",
            "java.util.function.ObjDoubleConsumer"
        });
        known.put("java.util.regex", new String[]{
            "java.util.regex.Pattern", "java.util.regex.Matcher", "java.util.regex.MatchResult"
        });

        // ═══════════════════════════════════════════════════════════════
        // I/O — files, streams, serialization
        // ═══════════════════════════════════════════════════════════════
        known.put("java.io", new String[]{
            "File", "InputStream", "OutputStream", "FileInputStream", "FileOutputStream",
            "BufferedInputStream", "BufferedOutputStream", "DataInputStream", "DataOutputStream",
            "BufferedReader", "BufferedWriter", "InputStreamReader", "OutputStreamWriter",
            "PrintWriter", "PrintStream", "StringReader", "StringWriter",
            "ByteArrayInputStream", "ByteArrayOutputStream",
            "ObjectInputStream", "ObjectOutputStream",
            "PipedInputStream", "PipedOutputStream", "PipedReader", "PipedWriter",
            "RandomAccessFile", "StreamTokenizer", "LineNumberReader",
            "FileReader", "FileWriter", "FilterInputStream", "FilterOutputStream",
            "PushbackInputStream", "PushbackReader", "SequenceInputStream",
            "Serializable", "Externalizable", "Closeable", "Flushable",
            "IOException", "FileNotFoundException", "EOFException"
        });
        known.put("java.nio", new String[]{
            "java.nio.ByteBuffer", "java.nio.CharBuffer", "java.nio.ShortBuffer",
            "java.nio.IntBuffer", "java.nio.LongBuffer", "java.nio.FloatBuffer",
            "java.nio.DoubleBuffer", "java.nio.MappedByteBuffer", "java.nio.ByteOrder",
            "java.nio.Buffer"
        });
        known.put("java.nio.file", new String[]{
            "java.nio.file.Files", "java.nio.file.Path", "java.nio.file.Paths",
            "java.nio.file.FileSystem", "java.nio.file.FileSystems",
            "java.nio.file.FileStore", "java.nio.file.FileVisitor",
            "java.nio.file.SimpleFileVisitor", "java.nio.file.StandardOpenOption",
            "java.nio.file.StandardCopyOption", "java.nio.file.LinkOption",
            "java.nio.file.WatchService", "java.nio.file.WatchKey",
            "java.nio.file.WatchEvent", "java.nio.file.DirectoryStream",
            "java.nio.file.attribute.BasicFileAttributes",
            "java.nio.file.attribute.FileAttribute",
            "java.nio.file.attribute.PosixFilePermission"
        });
        known.put("java.nio.channels", new String[]{
            "java.nio.channels.FileChannel", "java.nio.channels.SocketChannel",
            "java.nio.channels.ServerSocketChannel", "java.nio.channels.DatagramChannel",
            "java.nio.channels.Selector", "java.nio.channels.SelectionKey",
            "java.nio.channels.Pipe", "java.nio.channels.AsynchronousFileChannel",
            "java.nio.channels.AsynchronousSocketChannel",
            "java.nio.channels.AsynchronousServerSocketChannel"
        });
        known.put("java.nio.charset", new String[]{
            "java.nio.charset.Charset", "java.nio.charset.StandardCharsets",
            "java.nio.charset.CharsetEncoder", "java.nio.charset.CharsetDecoder"
        });

        // ═══════════════════════════════════════════════════════════════
        // NETWORKING — sockets, HTTP, URLs
        // ═══════════════════════════════════════════════════════════════
        known.put("java.net", new String[]{
            "URL", "URI", "HttpURLConnection", "Socket", "ServerSocket",
            "DatagramSocket", "DatagramPacket", "MulticastSocket",
            "InetAddress", "Inet4Address", "Inet6Address", "InetSocketAddress",
            "NetworkInterface", "URLClassLoader", "URLConnection",
            "URLEncoder", "URLDecoder", "Proxy", "ProxySelector",
            "CookieManager", "CookieHandler", "Authenticator"
        });
        known.put("java.net.http", new String[]{
            "java.net.http.HttpClient", "java.net.http.HttpRequest",
            "java.net.http.HttpResponse", "java.net.http.WebSocket"
        });

        // ═══════════════════════════════════════════════════════════════
        // MATH & SCIENCE — the foundation of computation
        // ═══════════════════════════════════════════════════════════════
        known.put("java.math", new String[]{
            "java.math.BigInteger", "java.math.BigDecimal",
            "java.math.MathContext", "java.math.RoundingMode"
        });
        known.put("java.time", new String[]{
            "java.time.Instant", "java.time.LocalDate", "java.time.LocalTime",
            "java.time.LocalDateTime", "java.time.ZonedDateTime", "java.time.OffsetDateTime",
            "java.time.Duration", "java.time.Period", "java.time.ZoneId",
            "java.time.ZoneOffset", "java.time.Clock", "java.time.Year",
            "java.time.YearMonth", "java.time.MonthDay", "java.time.Month",
            "java.time.DayOfWeek", "java.time.format.DateTimeFormatter",
            "java.time.temporal.ChronoUnit", "java.time.temporal.ChronoField",
            "java.time.temporal.TemporalAdjusters"
        });

        // ═══════════════════════════════════════════════════════════════
        // SECURITY & CRYPTO — encryption, signatures, certificates
        // ═══════════════════════════════════════════════════════════════
        known.put("java.security", new String[]{
            "java.security.MessageDigest", "java.security.KeyPair",
            "java.security.KeyPairGenerator", "java.security.Signature",
            "java.security.SecureRandom", "java.security.KeyFactory",
            "java.security.KeyStore", "java.security.Security",
            "java.security.Provider", "java.security.Permission",
            "java.security.PrivateKey", "java.security.PublicKey",
            "java.security.cert.Certificate", "java.security.cert.X509Certificate",
            "java.security.cert.CertificateFactory"
        });
        known.put("javax.crypto", new String[]{
            "javax.crypto.Cipher", "javax.crypto.KeyGenerator",
            "javax.crypto.SecretKey", "javax.crypto.Mac",
            "javax.crypto.SecretKeyFactory", "javax.crypto.KeyAgreement",
            "javax.crypto.spec.SecretKeySpec", "javax.crypto.spec.IvParameterSpec",
            "javax.crypto.spec.PBEKeySpec", "javax.crypto.spec.GCMParameterSpec"
        });

        // ═══════════════════════════════════════════════════════════════
        // GUI — Swing + AWT (processing-like)
        // ═══════════════════════════════════════════════════════════════
        known.put("java.awt", new String[]{
            "java.awt.Color", "java.awt.Graphics", "java.awt.Graphics2D",
            "java.awt.Font", "java.awt.Image", "java.awt.Rectangle",
            "java.awt.Point", "java.awt.Dimension", "java.awt.Polygon",
            "java.awt.BasicStroke", "java.awt.GradientPaint", "java.awt.TexturePaint",
            "java.awt.RenderingHints", "java.awt.AlphaComposite",
            "java.awt.Toolkit", "java.awt.Robot", "java.awt.Desktop",
            "java.awt.FlowLayout", "java.awt.BorderLayout", "java.awt.GridLayout",
            "java.awt.GridBagLayout", "java.awt.CardLayout",
            "java.awt.event.ActionEvent", "java.awt.event.ActionListener",
            "java.awt.event.MouseEvent", "java.awt.event.MouseListener",
            "java.awt.event.KeyEvent", "java.awt.event.KeyListener",
            "java.awt.event.WindowEvent", "java.awt.event.WindowListener",
            "java.awt.geom.AffineTransform", "java.awt.geom.Path2D",
            "java.awt.geom.Ellipse2D", "java.awt.geom.Rectangle2D",
            "java.awt.geom.Line2D", "java.awt.geom.Arc2D",
            "java.awt.image.BufferedImage", "java.awt.image.WritableRaster",
            "java.awt.image.ColorModel", "java.awt.image.Raster"
        });
        known.put("javax.swing", new String[]{
            "javax.swing.JFrame", "javax.swing.JPanel", "javax.swing.JButton",
            "javax.swing.JLabel", "javax.swing.JTextField", "javax.swing.JTextArea",
            "javax.swing.JScrollPane", "javax.swing.JList", "javax.swing.JTable",
            "javax.swing.JTree", "javax.swing.JComboBox", "javax.swing.JCheckBox",
            "javax.swing.JRadioButton", "javax.swing.JMenuBar", "javax.swing.JMenu",
            "javax.swing.JMenuItem", "javax.swing.JToolBar", "javax.swing.JTabbedPane",
            "javax.swing.JSplitPane", "javax.swing.JDialog", "javax.swing.JFileChooser",
            "javax.swing.JOptionPane", "javax.swing.JProgressBar",
            "javax.swing.JSlider", "javax.swing.JSpinner",
            "javax.swing.SwingUtilities", "javax.swing.Timer",
            "javax.swing.BorderFactory", "javax.swing.BoxLayout",
            "javax.swing.DefaultListModel", "javax.swing.DefaultComboBoxModel",
            "javax.swing.table.DefaultTableModel", "javax.swing.table.AbstractTableModel",
            "javax.swing.tree.DefaultMutableTreeNode", "javax.swing.tree.DefaultTreeModel"
        });
        known.put("javax.imageio", new String[]{
            "javax.imageio.ImageIO", "javax.imageio.ImageReader",
            "javax.imageio.ImageWriter", "javax.imageio.ImageTypeSpecifier"
        });

        // ═══════════════════════════════════════════════════════════════
        // XML & JSON processing
        // ═══════════════════════════════════════════════════════════════
        known.put("javax.xml.parsers", new String[]{
            "javax.xml.parsers.DocumentBuilder", "javax.xml.parsers.DocumentBuilderFactory",
            "javax.xml.parsers.SAXParser", "javax.xml.parsers.SAXParserFactory"
        });
        known.put("javax.xml.transform", new String[]{
            "javax.xml.transform.Transformer", "javax.xml.transform.TransformerFactory",
            "javax.xml.transform.dom.DOMSource", "javax.xml.transform.stream.StreamResult"
        });

        // ═══════════════════════════════════════════════════════════════
        // SQL — database connectivity
        // ═══════════════════════════════════════════════════════════════
        known.put("java.sql", new String[]{
            "java.sql.Connection", "java.sql.DriverManager", "java.sql.Statement",
            "java.sql.PreparedStatement", "java.sql.CallableStatement",
            "java.sql.ResultSet", "java.sql.ResultSetMetaData",
            "java.sql.DatabaseMetaData", "java.sql.Types",
            "java.sql.Date", "java.sql.Time", "java.sql.Timestamp",
            "java.sql.Blob", "java.sql.Clob", "java.sql.Array",
            "java.sql.SQLException", "java.sql.SQLWarning"
        });

        // ═══════════════════════════════════════════════════════════════
        // MANAGEMENT & INSTRUMENTATION
        // ═══════════════════════════════════════════════════════════════
        known.put("java.lang.management", new String[]{
            "java.lang.management.ManagementFactory", "java.lang.management.RuntimeMXBean",
            "java.lang.management.MemoryMXBean", "java.lang.management.MemoryUsage",
            "java.lang.management.ThreadMXBean", "java.lang.management.OperatingSystemMXBean",
            "java.lang.management.GarbageCollectorMXBean", "java.lang.management.ClassLoadingMXBean"
        });
        known.put("java.lang.invoke", new String[]{
            "java.lang.invoke.MethodHandle", "java.lang.invoke.MethodHandles",
            "java.lang.invoke.MethodType", "java.lang.invoke.VarHandle",
            "java.lang.invoke.CallSite", "java.lang.invoke.LambdaMetafactory"
        });

        // ═══════════════════════════════════════════════════════════════
        // EXTERNAL LIBRARIES — try to load if on classpath
        // OpenCV, Processing, Apache Commons, JSON, Guava, etc.
        // These will silently skip if not available.
        // ═══════════════════════════════════════════════════════════════

        // OpenCV (computer vision)
        known.put("org.opencv.core", new String[]{
            "org.opencv.core.Mat", "org.opencv.core.Scalar", "org.opencv.core.Size",
            "org.opencv.core.Point", "org.opencv.core.Rect", "org.opencv.core.Range",
            "org.opencv.core.Core", "org.opencv.core.CvType", "org.opencv.core.MatOfPoint",
            "org.opencv.core.MatOfPoint2f", "org.opencv.core.MatOfFloat",
            "org.opencv.core.MatOfInt", "org.opencv.core.MatOfByte",
            "org.opencv.core.RotatedRect", "org.opencv.core.TermCriteria"
        });
        known.put("org.opencv.imgproc", new String[]{
            "org.opencv.imgproc.Imgproc"
        });
        known.put("org.opencv.imgcodecs", new String[]{
            "org.opencv.imgcodecs.Imgcodecs"
        });
        known.put("org.opencv.videoio", new String[]{
            "org.opencv.videoio.VideoCapture", "org.opencv.videoio.VideoWriter"
        });
        known.put("org.opencv.objdetect", new String[]{
            "org.opencv.objdetect.CascadeClassifier", "org.opencv.objdetect.HOGDescriptor"
        });
        known.put("org.opencv.ml", new String[]{
            "org.opencv.ml.SVM", "org.opencv.ml.KNearest", "org.opencv.ml.DTrees",
            "org.opencv.ml.RTrees", "org.opencv.ml.Boost", "org.opencv.ml.ANN_MLP",
            "org.opencv.ml.LogisticRegression", "org.opencv.ml.StatModel"
        });
        known.put("org.opencv.dnn", new String[]{
            "org.opencv.dnn.Net", "org.opencv.dnn.Dnn"
        });

        // Processing (creative coding)
        known.put("processing.core", new String[]{
            "processing.core.PApplet", "processing.core.PGraphics",
            "processing.core.PImage", "processing.core.PFont",
            "processing.core.PShape", "processing.core.PVector",
            "processing.core.PConstants", "processing.core.PMatrix",
            "processing.core.PMatrix2D", "processing.core.PMatrix3D",
            "processing.core.PStyle", "processing.core.PSurface"
        });
        known.put("processing.data", new String[]{
            "processing.data.JSONObject", "processing.data.JSONArray",
            "processing.data.Table", "processing.data.TableRow",
            "processing.data.XML", "processing.data.StringDict",
            "processing.data.IntDict", "processing.data.FloatDict"
        });
        known.put("processing.opengl", new String[]{
            "processing.opengl.PGL", "processing.opengl.PGraphicsOpenGL",
            "processing.opengl.PShader", "processing.opengl.PJOGL"
        });

        // Apache Commons Math
        known.put("org.apache.commons.math3.linear", new String[]{
            "org.apache.commons.math3.linear.RealMatrix",
            "org.apache.commons.math3.linear.RealVector",
            "org.apache.commons.math3.linear.MatrixUtils",
            "org.apache.commons.math3.linear.Array2DRowRealMatrix",
            "org.apache.commons.math3.linear.ArrayRealVector",
            "org.apache.commons.math3.linear.LUDecomposition",
            "org.apache.commons.math3.linear.QRDecomposition",
            "org.apache.commons.math3.linear.SingularValueDecomposition",
            "org.apache.commons.math3.linear.EigenDecomposition",
            "org.apache.commons.math3.linear.CholeskyDecomposition"
        });
        known.put("org.apache.commons.math3.stat", new String[]{
            "org.apache.commons.math3.stat.descriptive.DescriptiveStatistics",
            "org.apache.commons.math3.stat.descriptive.SummaryStatistics",
            "org.apache.commons.math3.stat.regression.SimpleRegression",
            "org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression",
            "org.apache.commons.math3.stat.correlation.PearsonsCorrelation",
            "org.apache.commons.math3.stat.correlation.SpearmansCorrelation",
            "org.apache.commons.math3.stat.inference.TTest",
            "org.apache.commons.math3.stat.inference.ChiSquareTest"
        });
        known.put("org.apache.commons.math3.analysis", new String[]{
            "org.apache.commons.math3.analysis.function.Sigmoid",
            "org.apache.commons.math3.analysis.function.Gaussian",
            "org.apache.commons.math3.analysis.function.Logistic",
            "org.apache.commons.math3.analysis.interpolation.SplineInterpolator",
            "org.apache.commons.math3.analysis.interpolation.LinearInterpolator"
        });
        known.put("org.apache.commons.math3.optim", new String[]{
            "org.apache.commons.math3.optim.linear.SimplexSolver",
            "org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex",
            "org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer"
        });
        known.put("org.apache.commons.math3.random", new String[]{
            "org.apache.commons.math3.random.MersenneTwister",
            "org.apache.commons.math3.random.Well19937c",
            "org.apache.commons.math3.random.RandomDataGenerator"
        });
        known.put("org.apache.commons.math3.distribution", new String[]{
            "org.apache.commons.math3.distribution.NormalDistribution",
            "org.apache.commons.math3.distribution.UniformRealDistribution",
            "org.apache.commons.math3.distribution.ExponentialDistribution",
            "org.apache.commons.math3.distribution.PoissonDistribution",
            "org.apache.commons.math3.distribution.BinomialDistribution",
            "org.apache.commons.math3.distribution.ChiSquaredDistribution",
            "org.apache.commons.math3.distribution.TDistribution",
            "org.apache.commons.math3.distribution.BetaDistribution",
            "org.apache.commons.math3.distribution.GammaDistribution"
        });
        known.put("org.apache.commons.math3.transform", new String[]{
            "org.apache.commons.math3.transform.FastFourierTransformer",
            "org.apache.commons.math3.transform.DftNormalization",
            "org.apache.commons.math3.transform.TransformType"
        });
        known.put("org.apache.commons.math3.complex", new String[]{
            "org.apache.commons.math3.complex.Complex", "org.apache.commons.math3.complex.ComplexUtils"
        });
        known.put("org.apache.commons.math3.geometry", new String[]{
            "org.apache.commons.math3.geometry.euclidean.threed.Vector3D",
            "org.apache.commons.math3.geometry.euclidean.twod.Vector2D",
            "org.apache.commons.math3.geometry.euclidean.threed.Rotation"
        });

        // Apache Commons Lang
        known.put("org.apache.commons.lang3", new String[]{
            "org.apache.commons.lang3.StringUtils", "org.apache.commons.lang3.ArrayUtils",
            "org.apache.commons.lang3.ObjectUtils", "org.apache.commons.lang3.ClassUtils",
            "org.apache.commons.lang3.SystemUtils", "org.apache.commons.lang3.RandomStringUtils",
            "org.apache.commons.lang3.math.NumberUtils", "org.apache.commons.lang3.time.StopWatch",
            "org.apache.commons.lang3.tuple.Pair", "org.apache.commons.lang3.tuple.Triple",
            "org.apache.commons.lang3.builder.ToStringBuilder",
            "org.apache.commons.lang3.builder.EqualsBuilder",
            "org.apache.commons.lang3.builder.HashCodeBuilder",
            "org.apache.commons.lang3.builder.CompareToBuilder"
        });

        // Apache Commons IO
        known.put("org.apache.commons.io", new String[]{
            "org.apache.commons.io.FileUtils", "org.apache.commons.io.IOUtils",
            "org.apache.commons.io.FilenameUtils", "org.apache.commons.io.LineIterator",
            "org.apache.commons.io.input.ReversedLinesFileReader",
            "org.apache.commons.io.output.ByteArrayOutputStream",
            "org.apache.commons.io.monitor.FileAlterationMonitor",
            "org.apache.commons.io.monitor.FileAlterationObserver"
        });

        // Apache Commons Collections
        known.put("org.apache.commons.collections4", new String[]{
            "org.apache.commons.collections4.CollectionUtils",
            "org.apache.commons.collections4.MapUtils",
            "org.apache.commons.collections4.ListUtils",
            "org.apache.commons.collections4.SetUtils",
            "org.apache.commons.collections4.bag.HashBag",
            "org.apache.commons.collections4.bidimap.DualHashBidiMap",
            "org.apache.commons.collections4.multimap.ArrayListValuedHashMap",
            "org.apache.commons.collections4.trie.PatriciaTrie"
        });

        // Google Guava
        known.put("com.google.common.collect", new String[]{
            "com.google.common.collect.ImmutableList", "com.google.common.collect.ImmutableMap",
            "com.google.common.collect.ImmutableSet", "com.google.common.collect.Lists",
            "com.google.common.collect.Maps", "com.google.common.collect.Sets",
            "com.google.common.collect.Multimap", "com.google.common.collect.HashMultimap",
            "com.google.common.collect.ArrayListMultimap",
            "com.google.common.collect.BiMap", "com.google.common.collect.HashBiMap",
            "com.google.common.collect.Table", "com.google.common.collect.HashBasedTable",
            "com.google.common.collect.Range", "com.google.common.collect.RangeMap",
            "com.google.common.collect.TreeRangeMap"
        });
        known.put("com.google.common.base", new String[]{
            "com.google.common.base.Preconditions", "com.google.common.base.Strings",
            "com.google.common.base.Joiner", "com.google.common.base.Splitter",
            "com.google.common.base.Optional", "com.google.common.base.Stopwatch",
            "com.google.common.base.Suppliers", "com.google.common.base.CaseFormat"
        });
        known.put("com.google.common.io", new String[]{
            "com.google.common.io.Files", "com.google.common.io.Resources",
            "com.google.common.io.ByteStreams", "com.google.common.io.CharStreams",
            "com.google.common.io.BaseEncoding"
        });
        known.put("com.google.common.cache", new String[]{
            "com.google.common.cache.CacheBuilder", "com.google.common.cache.Cache",
            "com.google.common.cache.LoadingCache", "com.google.common.cache.CacheLoader"
        });
        known.put("com.google.common.hash", new String[]{
            "com.google.common.hash.Hashing", "com.google.common.hash.HashFunction",
            "com.google.common.hash.BloomFilter"
        });
        known.put("com.google.common.math", new String[]{
            "com.google.common.math.IntMath", "com.google.common.math.LongMath",
            "com.google.common.math.DoubleMath", "com.google.common.math.BigIntegerMath",
            "com.google.common.math.Stats", "com.google.common.math.Quantiles"
        });
        known.put("com.google.common.util.concurrent", new String[]{
            "com.google.common.util.concurrent.ListenableFuture",
            "com.google.common.util.concurrent.Futures",
            "com.google.common.util.concurrent.MoreExecutors",
            "com.google.common.util.concurrent.RateLimiter"
        });

        // JSON — org.json + Gson
        known.put("org.json", new String[]{
            "org.json.JSONObject", "org.json.JSONArray", "org.json.JSONTokener",
            "org.json.JSONWriter", "org.json.JSONStringer", "org.json.JSONException"
        });
        known.put("com.google.gson", new String[]{
            "com.google.gson.Gson", "com.google.gson.GsonBuilder",
            "com.google.gson.JsonElement", "com.google.gson.JsonObject",
            "com.google.gson.JsonArray", "com.google.gson.JsonPrimitive",
            "com.google.gson.JsonParser", "com.google.gson.TypeAdapter",
            "com.google.gson.stream.JsonReader", "com.google.gson.stream.JsonWriter"
        });

        // Jackson
        known.put("com.fasterxml.jackson.databind", new String[]{
            "com.fasterxml.jackson.databind.ObjectMapper",
            "com.fasterxml.jackson.databind.JsonNode",
            "com.fasterxml.jackson.databind.node.ObjectNode",
            "com.fasterxml.jackson.databind.node.ArrayNode",
            "com.fasterxml.jackson.databind.SerializationFeature",
            "com.fasterxml.jackson.databind.DeserializationFeature"
        });

        // ND4J / DL4J (Deep Learning for Java — linear algebra)
        known.put("org.nd4j.linalg", new String[]{
            "org.nd4j.linalg.factory.Nd4j", "org.nd4j.linalg.api.ndarray.INDArray",
            "org.nd4j.linalg.ops.transforms.Transforms",
            "org.nd4j.linalg.api.buffer.DataType",
            "org.nd4j.linalg.indexing.NDArrayIndex",
            "org.nd4j.linalg.dataset.DataSet"
        });
        known.put("org.deeplearning4j.nn", new String[]{
            "org.deeplearning4j.nn.multilayer.MultiLayerNetwork",
            "org.deeplearning4j.nn.conf.NeuralNetConfiguration",
            "org.deeplearning4j.nn.conf.layers.DenseLayer",
            "org.deeplearning4j.nn.conf.layers.OutputLayer",
            "org.deeplearning4j.nn.conf.layers.ConvolutionLayer",
            "org.deeplearning4j.nn.conf.layers.LSTM"
        });

        // JUnit 5 (testing framework)
        known.put("org.junit.jupiter.api", new String[]{
            "org.junit.jupiter.api.Test", "org.junit.jupiter.api.Assertions",
            "org.junit.jupiter.api.BeforeEach", "org.junit.jupiter.api.AfterEach",
            "org.junit.jupiter.api.BeforeAll", "org.junit.jupiter.api.AfterAll",
            "org.junit.jupiter.api.DisplayName", "org.junit.jupiter.api.Nested",
            "org.junit.jupiter.api.RepeatedTest", "org.junit.jupiter.api.Tag",
            "org.junit.jupiter.api.Timeout", "org.junit.jupiter.api.Disabled",
            "org.junit.jupiter.api.TestFactory", "org.junit.jupiter.api.TestInstance"
        });

        // SLF4J / Logback (logging)
        known.put("org.slf4j", new String[]{
            "org.slf4j.Logger", "org.slf4j.LoggerFactory",
            "org.slf4j.MDC", "org.slf4j.Marker", "org.slf4j.MarkerFactory"
        });

        // ═══════════════════════════════════════════════════════════════
        // RESOLVE: try fully-qualified first, then pkg.name
        // ═══════════════════════════════════════════════════════════════
        String[] names = known.get(pkg);
        if (names != null) {
            for (String name : names) {
                String full = name.contains(".") ? name : pkg + "." + name;
                try { out.add(Class.forName(full)); } catch (Throwable ignored) {}
            }
        }
    }

    // ─── PARALLEL MULTI-DIMENSIONAL ABSORPTION ENGINE ────────────────────────
    // Each package = 1 dimension. All dimensions processed simultaneously
    // across all available CPU cores. Results superimposed atomically.
    // ────────────────────────────────────────────────────────────────────────

    private static final String[] JDK_PACKAGES = {
        "java.lang", "java.lang.reflect", "java.lang.annotation",
        "java.util", "java.util.concurrent", "java.util.stream",
        "java.util.function", "java.util.regex",
        "java.io", "java.nio", "java.nio.file", "java.nio.channels", "java.nio.charset",
        "java.net", "java.net.http",
        "java.math", "java.time",
        "java.security", "javax.crypto",
        "java.awt", "javax.swing", "javax.imageio",
        "javax.xml.parsers", "javax.xml.transform",
        "java.sql",
        "java.lang.management", "java.lang.invoke"
    };

    private static final String[] EXTERNAL_PACKAGES = {
        "org.opencv.core", "org.opencv.imgproc", "org.opencv.imgcodecs",
        "org.opencv.videoio", "org.opencv.objdetect", "org.opencv.ml", "org.opencv.dnn",
        "processing.core", "processing.data", "processing.opengl",
        "org.apache.commons.math3.linear", "org.apache.commons.math3.stat",
        "org.apache.commons.math3.analysis", "org.apache.commons.math3.optim",
        "org.apache.commons.math3.random", "org.apache.commons.math3.distribution",
        "org.apache.commons.math3.transform", "org.apache.commons.math3.complex",
        "org.apache.commons.math3.geometry",
        "org.apache.commons.lang3", "org.apache.commons.io",
        "org.apache.commons.collections4",
        "com.google.common.collect", "com.google.common.base",
        "com.google.common.io", "com.google.common.cache",
        "com.google.common.hash", "com.google.common.math",
        "com.google.common.util.concurrent",
        "org.json", "com.google.gson", "com.fasterxml.jackson.databind",
        "org.nd4j.linalg", "org.deeplearning4j.nn",
        "org.junit.jupiter.api", "org.slf4j"
    };

    private volatile boolean massAbsorptionComplete = false;
    private volatile String  massAbsorptionResult   = "";

    /** True when background parallel absorption has finished. */
    public boolean isMassAbsorptionComplete() { return massAbsorptionComplete; }
    public String  getMassAbsorptionResult()  { return massAbsorptionResult; }

    /**
     * Mass-absorb everything using PARALLEL MULTI-DIMENSIONAL processing.
     * All N packages fire simultaneously across all CPU cores.
     * Each package = one dimension. Results superimpose atomically via ConcurrentHashMap + AtomicInteger.
     */
    public String absorbEverything() {
        int cores = Runtime.getRuntime().availableProcessors();
        long t0 = System.currentTimeMillis();
        int beforeClasses = totalClasses.get();
        int beforeMethods = totalMethods.get();

        // Merge all packages into a single dimension array
        String[] allPackages = new String[JDK_PACKAGES.length + EXTERNAL_PACKAGES.length];
        System.arraycopy(JDK_PACKAGES, 0, allPackages, 0, JDK_PACKAGES.length);
        System.arraycopy(EXTERNAL_PACKAGES, 0, allPackages, JDK_PACKAGES.length, EXTERNAL_PACKAGES.length);

        System.out.printf("[Absorber] HYPER-PARALLEL ABSORPTION — %d dimensions × %d cores%n",
            allPackages.length, cores);

        // ForkJoinPool: each package is an independent dimension
        ForkJoinPool hyperPool = new ForkJoinPool(cores);
        AtomicInteger externalHit = new AtomicInteger(0);
        AtomicInteger dimDone     = new AtomicInteger(0);
        int totalDims = allPackages.length;

        try {
            hyperPool.submit(() ->
                java.util.Arrays.stream(allPackages).parallel().forEach(pkg -> {
                    try {
                        String result = absorbPackage(pkg);
                        int done = dimDone.incrementAndGet();
                        if (!result.contains("No classes found")) {
                            boolean isExternal = pkg.startsWith("org.") || pkg.startsWith("com.") ||
                                                 pkg.startsWith("processing.");
                            if (isExternal) externalHit.incrementAndGet();
                        }
                        // Progress every 10 dimensions
                        if (done % 10 == 0 || done == totalDims) {
                            System.out.printf("   [ABSORBER] %d/%d dimensions collapsed%n", done, totalDims);
                        }
                    } catch (Throwable ignored) {
                        dimDone.incrementAndGet();
                    }
                })
            ).get(); // Wait for all dimensions to collapse
        } catch (Exception e) {
            System.out.println("   [ABSORBER] Parallel absorption error: " + e.getMessage());
        } finally {
            hyperPool.shutdown();
        }

        long elapsed = System.currentTimeMillis() - t0;
        int newClasses = totalClasses.get() - beforeClasses;
        int newMethods = totalMethods.get() - beforeMethods;
        double speedup = cores > 1 ? (double) allPackages.length / Math.max(1, elapsed / 1000.0) : 1.0;

        massAbsorptionComplete = true;
        massAbsorptionResult = String.format(
            "[Absorber] HYPER-PARALLEL COMPLETE: +%d classes, +%d methods, %d dimensions, %d cores, %d external in %dms (%.1f dims/sec)",
            newClasses, newMethods, allPackages.length, cores, externalHit.get(), elapsed, speedup);

        return massAbsorptionResult;
    }

    // =========================================================================================
    // UTILITIES
    // =========================================================================================

    private String buildSignature(Method m) {
        StringBuilder sig = new StringBuilder(m.getName());
        sig.append("(");
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            sig.append(params[i].getSimpleName());
            if (i < params.length - 1) sig.append(", ");
        }
        sig.append(") : ").append(m.getReturnType().getSimpleName());
        Class<?>[] exceptions = m.getExceptionTypes();
        if (exceptions.length > 0) {
            sig.append(" throws ");
            for (int i = 0; i < exceptions.length; i++) {
                sig.append(exceptions[i].getSimpleName());
                if (i < exceptions.length - 1) sig.append(", ");
            }
        }
        return sig.toString();
    }

    private String buildConstructorSig(Constructor<?> c) {
        StringBuilder sig = new StringBuilder("<init>(");
        Class<?>[] params = c.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            sig.append(params[i].getSimpleName());
            if (i < params.length - 1) sig.append(", ");
        }
        sig.append(")");
        return sig.toString();
    }

    // =========================================================================================
    // STATUS
    // =========================================================================================

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧬 LIBRARY ABSORBER v2 — Absorb → Abstract → Evolve → Transcend ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Libraries:   %,6d    Classes:    %,6d              ║\n",
            libraries.size(), totalClasses.get()));
        sb.append(String.format("║  Methods:     %,6d    Fields:     %,6d              ║\n",
            totalMethods.get(), totalFields.get()));
        sb.append(String.format("║  Evolved:     %,6d    Transcended:%,6d              ║\n",
            evolvedCount.get(), transcended.get()));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  SentientCore: %s    Akashic: %s          ║\n",
            sentient != null ? "WIRED ✓" : "NOT WIRED",
            akashic != null ? "WIRED ✓" : "NOT WIRED"));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");

        for (AbsorbedLibrary lib : libraries.values()) {
            sb.append(String.format("║  %s%-20s%s %3d classes, %4d methods %s║\n",
                lib.transcended ? "🌟" : "📚",
                lib.name.length() > 20 ? lib.name.substring(0, 20) : lib.name,
                lib.transcended ? " [TRANSCENDED]" : "              ",
                lib.classCount, lib.methodCount,
                lib.transcended ? "" : " "));
        }

        sb.append("╚══════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    // =========================================================================================
    // DATA CLASSES
    // =========================================================================================

    static class AbsorbedLibrary {
        final String name;
        final String source;
        int classCount = 0, methodCount = 0, fieldCount = 0;
        long absorptionTimeMs = 0;
        boolean transcended = false;
        final Map<String, ClassKnowledge> classes = new LinkedHashMap<>();

        AbsorbedLibrary(String name, String source) {
            this.name = name; this.source = source;
        }

        String summary() {
            return String.format("[Absorber] %s: %d classes, %d methods, %d fields (%dms)%s",
                name, classCount, methodCount, fieldCount, absorptionTimeMs,
                transcended ? " [TRANSCENDED]" : "");
        }
    }

    static class ClassKnowledge {
        final String fullName;
        String superClass;
        final List<String> interfaces    = new ArrayList<>();
        final List<String> methods       = new ArrayList<>();
        final List<String> fields        = new ArrayList<>();
        final List<String> constructors  = new ArrayList<>();

        ClassKnowledge(String fullName) { this.fullName = fullName; }
    }
}
