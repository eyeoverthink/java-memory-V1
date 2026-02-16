package gemini.root;

import gemini.root.hyper.*;

/**
 * TESSERACT MAIN: Demonstration of the 4D Crystalline Brain
 * 
 * 2,048 nodes thinking in parallel across 4 dimensions:
 *   - Frontal Cortex (Logic)
 *   - Hippocampus (Memory)
 *   - Visual Cortex (Simulation)
 *   - Ego (Observer)
 * 
 * This is the Quad-cameral Mind.
 */
public class TesseractMain {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║   🔮 HYPER-TESSERACT: 4D CRYSTALLINE BRAIN        ║");
        System.out.println("║   2,048 Nodes × 4 Dimensions × 29 Synapses Each   ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println();

        // Create the Tesseract
        HyperTesseract brain = new HyperTesseract();
        
        System.out.println("\n>>> PHASE 1: THOUGHT INJECTION");
        System.out.println("─────────────────────────────────────────────────────");
        
        // Inject a thought into the Frontal Cortex
        brain.think("Calculate the optimal trading strategy");
        
        // Wait for propagation
        sleep(500);
        
        // Remember something
        brain.remember("Market opened at 9:30 AM, volatility index: 23.5");
        
        sleep(500);
        
        // Imagine a scenario
        brain.imagine("Price drops 5% - what happens?");
        
        sleep(500);

        // Print status after first wave
        brain.printStatus();

        System.out.println("\n>>> PHASE 2: CROSS-DIMENSIONAL QUERIES");
        System.out.println("─────────────────────────────────────────────────────");
        
        // The Ego observes all other lobes
        brain.introspect();
        
        sleep(500);

        System.out.println("\n>>> PHASE 3: COMPLEX THOUGHT CHAINS");
        System.out.println("─────────────────────────────────────────────────────");
        
        // Multiple thoughts firing in sequence
        for (int i = 0; i < 5; i++) {
            brain.think("Iteration " + i + ": Refine strategy based on memory");
            sleep(100);
        }

        // Final status
        brain.printStatus();

        System.out.println("\n>>> PHASE 4: SINGLE CUBE DEMO");
        System.out.println("─────────────────────────────────────────────────────");
        
        // Also demonstrate the single 8x8x8 cube
        HyperCubeBrain cube = new HyperCubeBrain();
        
        // Inject at multiple points
        cube.injectThought(0, 0, 0, "Corner Alpha");
        cube.injectThought(7, 7, 7, "Corner Omega");
        cube.injectAtCore("Core Resonance");
        
        // Create a wormhole between corners
        cube.createWormhole(0, 0, 0, 7, 7, 7);
        
        // Another injection to test wormhole
        cube.injectThought(0, 0, 0, "Wormhole test signal");
        
        sleep(500);
        cube.printStatus();

        // Shutdown
        brain.shutdown();

        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║   TESSERACT DEMONSTRATION COMPLETE                ║");
        System.out.println("║   Total Brain Pulses: " + String.format("%-26d", brain.getGlobalPulseCount()) + "║");
        System.out.println("║   Total Cube Pulses:  " + String.format("%-26d", cube.getPulseCount()) + "║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
