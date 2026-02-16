package fraymus.evolution;

import fraymus.limbs.ClawIO;
import fraymus.limbs.ClawConnector;
import fraymus.core.GravityEngine;
import fraymus.core.PhiSuit;

/**
 * FRAYNIX EVOLVER - The Ouroboros Loop
 * 
 * 1. Read Own Code
 * 2. Fuse with Optimization Strategy
 * 3. Rewrite Self
 * 4. Recompile
 * 
 * "A system that reads its own DNA and edits it."
 */
public class FraynixEvolver {

    private final ClawIO surgeon;
    private final ClawConnector nerve;
    private final GravityEngine brain;

    public FraynixEvolver(GravityEngine brain) {
        this.brain = brain;
        this.surgeon = new ClawIO();
        this.nerve = new ClawConnector();
    }

    /**
     * TRIGGER: Analyze a specific class for weakness.
     */
    public void analyzeSelf(String className) {
        System.out.println("🔍 INTROSPECTION: Analyzing " + className + "...");

        // 1. READ (Ingest self)
        String sourceCode = surgeon.readSource(className);
        if (sourceCode.startsWith("ERROR")) {
            System.err.println(sourceCode);
            return;
        }

        // 2. PHYSICS SIMULATION (Find Friction)
        // We represent the Code Complexity as "Entropy"
        double entropy = calculateEntropy(sourceCode);
        System.out.println("   > Current Entropy: " + entropy);

        if (entropy > 50.0) {
            // 3. FUSION EVENT (Generate Improvement)
            System.out.println("⚡ HIGH ENTROPY DETECTED. Initiating Fusion...");
            
            // We fuse the "Source Code" particle with the "Optimization" particle
            PhiSuit<String> codeParticle = new PhiSuit<>(sourceCode, 0, 0, 0);
            PhiSuit<String> optimParticle = new PhiSuit<>("OPTIMIZE_ALGORITHM", 10, 10, 10);
            
            // In a real system, the FusionReactor would produce the new string.
            // Here, we simulate the LLM call via OpenClaw to "Refactor this".
            initiateSurgery(className, sourceCode);
        } else {
            System.out.println("   > Code is stable (Harmonic). No action.");
        }
    }

    private void initiateSurgery(String className, String oldCode) {
        // Dispatch to OpenClaw (Local LLM)
        String prompt = "You are a Self-Improving Compiler. Refactor this Java class to be O(1) or O(log n). " +
                        "Return ONLY the raw Java code. Class: " + className + "\n\nCode:\n" + oldCode;
        
        // This sends the prompt to your local OpenClaw agent
        String evolvedCode = nerve.dispatch(prompt, "CONTEXT: SELF_IMPROVEMENT");
        
        // 4. VERIFICATION (The Immune System)
        if (evolvedCode.contains("public class " + className)) {
             System.out.println("🧬 EVOLUTION CANDIDATE RECEIVED.");
             
             // 5. REWRITE (Apply Mutation)
             String result = surgeon.writeSource(className, evolvedCode);
             System.out.println("   > " + result);
             
             // 6. BUILD CHECK (Did we kill the host?)
             runBuildCheck();
        }
    }

    private void runBuildCheck() {
        System.out.println("🛡️ IMMUNE SYSTEM: Running './gradlew build'...");
        // Call OpenClaw to run the build command locally
        nerve.dispatch("./gradlew compileJava", "Verify System Integrity");
    }

    // A simple heuristic for "Bad Code" (e.g., nested loops)
    private double calculateEntropy(String code) {
        double entropy = 0;
        if (code.contains("for (")) entropy += 10;
        if (code.contains("while (")) entropy += 10;
        if (code.contains("Thread.sleep")) entropy += 50; // Inefficiency
        
        // Count nested loops
        int forCount = 0;
        for (char c : code.toCharArray()) {
            if (code.indexOf("for (", forCount) != -1) {
                forCount++;
            }
        }
        if (forCount > 2) entropy += 30; // Nested loops
        
        return entropy;
    }
}
