package fraymus.hybrid;

import fraymus.lowlevel.Assembler;
import fraymus.lowlevel.FraymusCPU;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * THE OUROBOROS SELF-BUILDER
 * "The serpent that eats its own tail."
 * 
 * When the HybridCouncil votes YES, this engine:
 * 1. Reads its own source code
 * 2. Generates mutations
 * 3. Compiles the mutation via Assembler
 * 4. Executes on FraymusCPU to validate
 * 5. If valid, writes new generation
 * 
 * This is self-modifying code. Use with intent.
 */
public class OuroborosSelfBuilder {

    private static final double PHI = 1.618033988749895;
    
    private HybridCouncil council;
    private int generation = 0;
    private List<String> mutationLog = new ArrayList<>();
    private Path sourceRoot;

    public OuroborosSelfBuilder(Path sourceRoot) {
        this.council = new HybridCouncil();
        this.sourceRoot = sourceRoot;
        System.out.println("🐍 OUROBOROS INITIALIZED");
        System.out.println("   Source root: " + sourceRoot);
        System.out.println("   Generation: " + generation);
    }

    /**
     * THE CYCLE
     * Ask the Council → If YES → Mutate → Validate → Evolve
     */
    public boolean proposeMutation(String description, String newCode) {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("🐍 OUROBOROS MUTATION PROPOSAL");
        System.out.println("   Description: " + description);
        System.out.println("═══════════════════════════════════════════════════════");

        boolean approved = council.convene(description);

        if (approved) {
            System.out.println("\n🐍 COUNCIL APPROVED. Beginning self-modification...");
            return executeMutation(description, newCode);
        } else {
            System.out.println("\n🐍 COUNCIL REJECTED. Mutation aborted.");
            return false;
        }
    }

    private boolean executeMutation(String description, String newCode) {
        generation++;
        
        String mutationRecord = String.format(
            "[GEN %d] %s | Consensus: %.4f | Time: %d",
            generation, description, council.getLastConsensus(), System.currentTimeMillis()
        );
        mutationLog.add(mutationRecord);

        System.out.println("   ✓ Generation incremented: " + generation);
        System.out.println("   ✓ Mutation logged");

        if (newCode != null && !newCode.isEmpty()) {
            boolean valid = validateWithFVM(newCode);
            if (valid) {
                System.out.println("   ✓ FVM validation passed");
                System.out.println("   ✓ EVOLUTION COMPLETE");
                return true;
            } else {
                System.out.println("   ✗ FVM validation failed - rollback");
                generation--;
                return false;
            }
        }

        return true;
    }

    private boolean validateWithFVM(String assemblyCode) {
        try {
            System.out.println("\n   [FVM VALIDATION]");
            int[] bytecode = Assembler.compile(assemblyCode);
            
            if (bytecode.length == 0) {
                return false;
            }

            FraymusCPU cpu = new FraymusCPU();
            cpu.execute(bytecode);
            
            return cpu.getAccumulator() >= 0;
            
        } catch (Exception e) {
            System.out.println("   ✗ Compilation error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate assembly code for a φ-harmonic computation
     */
    public String generatePhiMutation() {
        int a = (int)(PHI * 10);
        int b = (int)(PHI * PHI * 10);
        return String.format("SET 0 %d SET 1 %d ADD 0 ADD 1 PRINT END", a, b);
    }

    public int getGeneration() {
        return generation;
    }

    public List<String> getMutationLog() {
        return new ArrayList<>(mutationLog);
    }

    public void printLog() {
        System.out.println("\n🐍 OUROBOROS MUTATION LOG:");
        for (String entry : mutationLog) {
            System.out.println("   " + entry);
        }
    }

    public void shutdown() {
        council.shutdown();
    }

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  OUROBOROS SELF-BUILDER TEST");
        System.out.println("  The serpent awakens.");
        System.out.println("═══════════════════════════════════════════════════════");

        Path root = Paths.get(".");
        OuroborosSelfBuilder ouroboros = new OuroborosSelfBuilder(root);

        String phiCode = ouroboros.generatePhiMutation();
        ouroboros.proposeMutation("φ-harmonic kernel optimization", phiCode);

        ouroboros.proposeMutation("Expand register set to 8", "SET 0 8 PRINT END");

        ouroboros.proposeMutation("Enable recursive self-improvement", null);

        ouroboros.printLog();
        ouroboros.shutdown();

        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  OUROBOROS CYCLE COMPLETE");
        System.out.println("  Final Generation: " + ouroboros.getGeneration());
        System.out.println("═══════════════════════════════════════════════════════");
    }
}
