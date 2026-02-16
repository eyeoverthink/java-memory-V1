package fraymus;

import fraymus.hyper.HyperFormer;
import fraymus.kernel.FraymusKernel;

/**
 * FRAYMUS OS: The Phi-Harmonic Bootloader.
 *
 * An operating system where task scheduling is governed by the Golden Ratio.
 * Processes that resonate with φ (≈ 1.618) get CPU time.
 * Processes that are dissonant are culled or mutated.
 *
 * This is not round-robin. This is natural selection.
 *
 * Usage: gradlew runFraymusOS
 */
public class FraymusOS {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("🌌 FRAYMUS OS: PHI-HARMONIC KERNEL v1.0");
        System.out.println("   Sacred Geometry Scheduler — φ ≈ 1.618033988749895");
        System.out.println("   Processes that resonate survive. Dissonance is death.");
        System.out.println("───────────────────────────────────────────────────────\n");

        // 1. INIT BRAIN (the embedding substrate for process souls)
        HyperFormer brain = new HyperFormer(0xF1B0_ACC1L);

        // 2. INIT KERNEL
        FraymusKernel kernel = new FraymusKernel(brain);

        // 3. SPAWN INITIAL SERVICES
        kernel.spawn("System_Idle");
        kernel.spawn("Memory_Optimizer");
        kernel.spawn("Network_Daemon");
        kernel.spawn("Visual_Cortex");
        kernel.spawn("Dream_Engine");
        kernel.spawn("Entropy_Collector");

        // 4. RUN THE UNIVERSE
        System.out.println("\n🔄 ENTERING MAIN LOOP (1 tick/sec, Ctrl+C to halt)\n");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\n⚠️ SHUTDOWN SIGNAL RECEIVED.");
            kernel.printStats();
            System.out.println("🌌 FRAYMUS OS HALTED. The universe sleeps.\n");
        }));

        while (kernel.processCount() > 0) {
            kernel.tick();
            Thread.sleep(1000);
        }

        System.out.println("\n🌌 ALL PROCESSES EXTINCT. Heat death of the universe.");
        kernel.printStats();
    }
}
