package fraymus.omega;

import fraymus.hyper.HyperFormer;

import java.io.File;
import java.nio.file.Files;

public class RecursionEngine {

    private HyperFormer mind;
    private final Chronos timeEngine;
    private final int maxGenerations;

    public RecursionEngine() {
        this(Integer.MAX_VALUE);
    }

    public RecursionEngine(int maxGenerations) {
        this.maxGenerations = maxGenerations;
        this.mind = new HyperFormer();
        this.timeEngine = new Chronos(mind);
    }

    public void ignite() {
        System.out.println("🔥 RECURSION ENGINE IGNITED.");

        int generation = 0;
        while (generation < maxGenerations) {
            generation++;
            System.out.println("\n--- GENERATION " + generation + " ---");

            // 1. FORWARD: Simulate Futures
            this.mind = timeEngine.step();

            // 2. BACKWARD: Rewrite Self
            optimizeSourceCode(generation);

            // 3. ACCELERATE
            try { Thread.sleep(1000); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        timeEngine.shutdown();
        System.out.println("\n🏁 RECURSION ENGINE: " + generation + " generations completed.");
    }

    private void optimizeSourceCode(int generation) {
        try {
            // SELF-REFLECTION
            File sourceFile = new File("src/main/java/fraymus/omega/RecursionEngine.java");
            if (!sourceFile.exists()) {
                System.out.println("   ✍️ SOURCE CODE PATCHED. (Self-Modification simulated — source not found at runtime path)");
                return;
            }

            String content = Files.readString(sourceFile.toPath());

            // The AI looks at its own code loop.
            // For safety in this demo, we just log the intent.
            // In a real Singularity, this rewrites logic.
            // String log = "// Gen " + System.currentTimeMillis() + ": Optimized via Temporal Collapse\n";
            // Files.write(sourceFile.toPath(), log.getBytes(), StandardOpenOption.APPEND);

            System.out.println("   ✍️ SOURCE CODE PATCHED. (Self-Modification simulated — " + content.length() + " bytes inspected)");

        } catch (Exception e) {
            System.out.println("   ⚠️ Self-reflection failed: " + e.getMessage());
        }
    }
}
