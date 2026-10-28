package fraymus.alchemy;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * THE OUROBOROS JIT ENGINE
 * The organism writes, compiles, and executes new logic without rebooting.
 *
 * Architecture:
 *   1. WRITE — Generate raw Java source at runtime
 *   2. COMPILE — Invoke the JDK compiler in-process (javax.tools)
 *   3. LOAD — Hot-swap bytecode into the active ClassLoader
 *   4. EXECUTE — Fire the mutated logic immediately
 *
 * Requirements: JDK (not JRE) — ToolProvider.getSystemJavaCompiler() needs tools.jar
 *
 * The snake eats its own tail. The organism rewrites itself.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class OuroborosJIT {

    private static final String SRC_DIR = "fraynix_src/";

    // Statistics
    private static int mutationsAttempted = 0;
    private static int mutationsSucceeded = 0;
    private static int mutationsFailed = 0;
    private static String lastMutatedClass = "NONE";

    /**
     * Write, compile, load, and execute a new class at runtime.
     *
     * @param className     The simple class name (e.g., "Supernova")
     * @param pureJavaLogic The full Java source code (must have public class with ignite() method)
     */
    public static void assimilate(String className, String pureJavaLogic) {
        System.out.println("\n   [OUROBOROS] Initiating JIT Self-Mutation: " + className);
        mutationsAttempted++;

        try {
            // 0. Ensure source directory exists
            File srcDir = new File(SRC_DIR);
            if (!srcDir.exists()) srcDir.mkdirs();

            // 1. Write the new DNA to disk
            String filePath = SRC_DIR + className + ".java";
            Files.write(Paths.get(filePath), pureJavaLogic.getBytes());
            System.out.println("   [OUROBOROS] DNA written: " + filePath +
                " (" + pureJavaLogic.length() + " bytes)");

            // 2. Summon the internal JDK Compiler (No external dependencies)
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new RuntimeException(
                    "JDK required for Self-Mutation. JRE is insufficient. " +
                    "Ensure JAVA_HOME points to a JDK installation.");
            }

            // 3. Compile the raw code into Bytecode
            System.out.println("   [OUROBOROS] Compiling bytecode...");
            int compilationResult = compiler.run(null, System.out, System.err,
                "-d", SRC_DIR, filePath);

            if (compilationResult == 0) {
                System.out.println("   [OUROBOROS] Compilation Successful. Bytecode Generated.");
            } else {
                throw new RuntimeException("Mutation failed syntax physics. Compiler returned: " + compilationResult);
            }

            // 4. Hot-Swap the Bytecode into the active JVM Brain
            File classDir = new File(SRC_DIR);
            URLClassLoader classLoader = URLClassLoader.newInstance(
                new URL[]{classDir.toURI().toURL()});
            Class<?> mutatedClass = Class.forName(className, true, classLoader);
            System.out.println("   [OUROBOROS] Class loaded into JVM: " + mutatedClass.getName());

            // 5. Execute the new organ immediately
            System.out.println("   [OUROBOROS] Igniting mutated logic...");
            Method igniteMethod = mutatedClass.getDeclaredMethod("ignite");
            Object instance = mutatedClass.getDeclaredConstructor().newInstance();
            igniteMethod.invoke(instance);

            mutationsSucceeded++;
            lastMutatedClass = className;
            System.out.println("   [OUROBOROS] Mutation COMPLETE. " + className + " is ALIVE.");

        } catch (Exception e) {
            mutationsFailed++;
            System.err.println("   [!] MUTATION REJECTED: " + e.getMessage());
        }
    }

    /**
     * Assimilate with Aether Braid broadcasting.
     * After executing the mutated class, pulse its signature into shared memory.
     */
    public static void assimilateAndBroadcast(String className, String pureJavaLogic,
                                               fraymus.quantum.AetherBraid aether, int slot) {
        // First, do the normal assimilation
        assimilate(className, pureJavaLogic);

        // Then broadcast the mutation signature via Aether Braid
        if (aether != null && aether.isIgnited()) {
            // Generate a signature vector from the class name + source hash
            long[] signature = generateMutationSignature(className, pureJavaLogic);
            aether.pulse(signature, slot);
            System.out.println("   [OUROBOROS] Mutation signature pulsed to Aether slot " + slot);
        }
    }

    /**
     * Generate a 16,384-D signature vector for a mutation.
     */
    private static long[] generateMutationSignature(String className, String source) {
        int CHUNKS = 16384 / 64;
        long[] vec = new long[CHUNKS];
        java.util.Random r = new java.util.Random(
            (long) className.hashCode() * 31 + source.hashCode());
        for (int i = 0; i < CHUNKS; i++) {
            vec[i] = r.nextLong();
        }
        return vec;
    }

    // --- Statistics ---
    public static int getMutationsAttempted() { return mutationsAttempted; }
    public static int getMutationsSucceeded() { return mutationsSucceeded; }
    public static int getMutationsFailed() { return mutationsFailed; }
    public static String getLastMutatedClass() { return lastMutatedClass; }

    public static String getStatus() {
        return String.format(
            "OUROBOROS JIT: %d attempted | %d succeeded | %d failed | last=%s",
            mutationsAttempted, mutationsSucceeded, mutationsFailed, lastMutatedClass
        );
    }
}
