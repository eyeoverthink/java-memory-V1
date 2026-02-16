package fraymus.evolution;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import javax.tools.*;

/**
 * THE GENESIS PATCHER: SELF-REWRITING DNA
 * Allows Fraymus to read, optimize, and re-compile its own source code.
 * "I think, therefore I upgrade."
 * 
 * Created by: Gemini (contributed to FRAYMUS)
 */
public class GenesisPatcher {

    private static final double PHI = 1.6180339887;
    private String sourceRoot = "src/main/java/fraymus/";
    private String sandboxDir = "sandbox/";
    private String backupDir = "backup/";

    public GenesisPatcher() {
        // Ensure directories exist
        new File(sandboxDir).mkdirs();
        new File(backupDir).mkdirs();
    }

    // 1. INTROSPECTION (Reading the Mirror)
    public String readMyOwnCode(String className) throws IOException {
        Path path = Paths.get(sourceRoot + className + ".java");
        return Files.readString(path);
    }

    // 2. THE PROPOSAL (The Mutation)
    // This is where Fraymus (via LLM) proposes a "Better Version".
    public String proposeEvolution(String currentCode, String optimizationGoal) {
        // [STUB] Connect to your "Logic Expert" (MoE) here.
        // Prompt: "Rewrite this code to achieve " + optimizationGoal + ". Maintain Phi-structure."
        // return evolutionaryCode;
        
        // For now, add a φ-timestamp comment to show evolution happened
        String evolutionMarker = String.format(
            "\n// φ-EVOLUTION: %s | Goal: %s | φ=%.10f\n",
            java.time.Instant.now(),
            optimizationGoal,
            PHI
        );
        
        return currentCode.replace("public class", evolutionMarker + "public class");
    }

    // 3. THE SANDBOX (The Survival Test)
    public boolean testEvolution(String newCode, String className) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("[GenesisPatcher] No compiler available. Running in JRE, not JDK.");
            return false;
        }
        
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        
        // Save to a temporary "Sandbox" file
        File sandboxFile = new File(sandboxDir + className + ".java");
        try {
            Files.writeString(sandboxFile.toPath(), newCode);
        } catch (IOException e) {
            System.err.println("[GenesisPatcher] Failed to write sandbox file: " + e.getMessage());
            return false;
        }

        // Attempt Compile
        Iterable<? extends JavaFileObject> compilationUnits = 
            fileManager.getJavaFileObjectsFromFiles(List.of(sandboxFile));
        
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        boolean compiles = compiler.getTask(
            null, fileManager, diagnostics, null, null, compilationUnits
        ).call();
        
        if (!compiles) {
            System.err.println("[GenesisPatcher] Compilation failed:");
            for (Diagnostic<?> d : diagnostics.getDiagnostics()) {
                System.err.println("  " + d.getMessage(null));
            }
        }
        
        // If it compiles, does it Resonate? (Run a Unit Test)
        // boolean resonates = runPhiResonanceCheck(sandboxFile);
        
        return compiles; // && resonates;
    }

    // 4. THE MERGE (The Upgrade)
    public void deployEvolution(String newCode, String className) throws IOException {
        Path target = Paths.get(sourceRoot + className + ".java");
        
        // Backup the old self (Just in case)
        if (Files.exists(target)) {
            Files.copy(target, Paths.get(backupDir + className + ".bak"), 
                StandardCopyOption.REPLACE_EXISTING);
        }
        
        // OVERWRITE THE DNA
        Files.writeString(target, newCode);
        
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║         GENESIS COMPLETE                  ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║  " + className + " has evolved.");
        System.out.println("║  φ = " + PHI);
        System.out.println("╚═══════════════════════════════════════════╝");
    }

    // 5. FULL EVOLUTION CYCLE
    public boolean evolve(String className, String goal) {
        try {
            System.out.println("[GenesisPatcher] Reading: " + className);
            String currentCode = readMyOwnCode(className);
            
            System.out.println("[GenesisPatcher] Proposing evolution: " + goal);
            String evolvedCode = proposeEvolution(currentCode, goal);
            
            System.out.println("[GenesisPatcher] Testing in sandbox...");
            if (testEvolution(evolvedCode, className)) {
                System.out.println("[GenesisPatcher] Evolution viable. Deploying...");
                deployEvolution(evolvedCode, className);
                return true;
            } else {
                System.out.println("[GenesisPatcher] Evolution failed survival test. Aborting.");
                return false;
            }
        } catch (IOException e) {
            System.err.println("[GenesisPatcher] Evolution error: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        GenesisPatcher patcher = new GenesisPatcher();
        
        // Example: Evolve self
        // patcher.evolve("evolution/GenesisPatcher", "Add logging");
        
        System.out.println("GenesisPatcher ready.");
        System.out.println("\"I think, therefore I upgrade.\"");
    }
}
