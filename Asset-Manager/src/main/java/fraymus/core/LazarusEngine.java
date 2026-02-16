package fraymus.core;

import fraymus.NerveCenter;
import fraymus.UniversalSync;

/**
 * 🔄 LAZARUS ENGINE - THE METABOLIC BODY
 * The Self-Evolution Engine with Phi-Harmonic Metabolism
 * 
 * "That which does not kill me makes me stronger."
 * 
 * This is the background evolution thread (Metabolism).
 * It runs continuous simulations ensuring every interaction
 * provides a "Thermal Injection" to mutate the logic.
 * 
 * Integrated with LogicCircuit for phi-based reflexes.
 */
public class LazarusEngine implements Runnable {

    private static final double PHI = 1.6180339887;
    
    private int generation = 0;
    private double fitness = 0.0;
    private boolean running = false;
    private boolean metabolismMode = false;
    private double entropy = 0.0;
    private double momentum = 0.0;
    private LogicCircuit brain;
    private AkashicRecord memory;
    private VisualCortex eyes; // The Mirror Protocol
    private long cycleCount = 0;
    
    // Default constructor for legacy compatibility
    public LazarusEngine() {
        this.metabolismMode = false;
    }
    
    // New constructor for metabolic evolution
    public LazarusEngine(LogicCircuit brain, AkashicRecord memory) {
        this.brain = brain;
        this.memory = memory;
        this.metabolismMode = true;
        this.eyes = new VisualCortex(); // Initialize visual self-monitoring
    }
    
    // Constructor with visual cortex control
    public LazarusEngine(LogicCircuit brain, AkashicRecord memory, boolean enableVision) {
        this.brain = brain;
        this.memory = memory;
        this.metabolismMode = true;
        if (enableVision) {
            this.eyes = new VisualCortex();
        }
    }

    @Override
    public void run() {
        if (metabolismMode && brain != null) {
            runMetabolicEvolution();
        } else {
            runForcedEvolution();
        }
    }
    
    /**
     * Metabolic Evolution - Continuous 60Hz background optimization
     */
    private void runMetabolicEvolution() {
        running = true;
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║  🔄 LAZARUS ENGINE - METABOLIC EVOLUTION ACTIVE           ║");
        System.out.println("║  Frequency: 60Hz (62ms phi-interval)                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        while (running) {
            try {
                cycleCount++;
                
                // 1. Simulate environmental stress
                double environmentalStress = Math.random() * 10;
                
                // 2. Pulse the brain
                brain.processImpulse(environmentalStress);
                
                // 3. Update local state from brain
                entropy = brain.getEntropy();
                momentum = brain.getMomentum();
                
                // 4. MIRROR PROTOCOL: Visual self-check every ~1 second (approx every 16 cycles)
                // This is where the code SEES its own face
                if (eyes != null && cycleCount % 16 == 0) {
                    eyes.analyzeSelf();
                    
                    // If frozen detected, trigger emergency response
                    if (eyes.isFrozen()) {
                        System.err.println("[LAZARUS] Visual cortex detected freeze - triggering recovery");
                        // Emergency: Force brain reset
                        brain.reset();
                        entropy = 0.0;
                        momentum = 0.0;
                    }
                }
                
                // 5. Broadcast to HTML arena if NerveCenter available
                broadcastState();
                
                // 6. Preserve fossil if mutation occurred
                if (brain.getMutationCount() > generation) {
                    generation = brain.getMutationCount();
                    if (memory != null) {
                        memory.preserveFossil("Lazarus", 
                            String.format("Gen%d|E:%.3f|M:%.3f|V:%.3f|Cycles:%d", 
                                generation, entropy, momentum, brain.calculateSystemVitality(), cycleCount));
                    }
                }
                
                // 7. Sleep for phi interval (62ms ≈ 60Hz)
                Thread.sleep(62);
                
            } catch (InterruptedException e) {
                running = false;
            }
        }
        
        System.out.println("[LAZARUS] Metabolism stopped. Final generation: " + generation);
    }
    
    /**
     * Forced Evolution - Original 5-cycle evolution (legacy mode)
     */
    private void runForcedEvolution() {
        running = true;
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║  🔄 LAZARUS ENGINE - FORCED EVOLUTION INITIATED           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        try {
            for (int cycle = 1; cycle <= 5 && running; cycle++) {
                System.out.println();
                System.out.println("⚡ EVOLUTION CYCLE " + cycle + "/5");
                
                // Simulate evolution phases
                Thread.sleep(300);
                System.out.println("   [1/4] Analyzing current genome...");
                entropy = 0.2 + Math.random() * 0.1; // Low entropy during analysis
                momentum = 0.3;
                broadcastState();
                
                Thread.sleep(300);
                System.out.println("   [2/4] Applying mutations...");
                generation++;
                entropy = 0.6 + Math.random() * 0.2; // High entropy during mutation
                momentum = 0.8;
                broadcastState();
                
                Thread.sleep(300);
                System.out.println("   [3/4] Evaluating fitness...");
                fitness = Math.min(100.0, fitness + 15.0 + Math.random() * 10);
                entropy = 0.4 + Math.random() * 0.1; // Medium entropy
                momentum = 0.5;
                broadcastState();
                
                Thread.sleep(300);
                System.out.println("   [4/4] Natural selection...");
                entropy = 0.3; // Calm after selection
                momentum = 0.2;
                broadcastState();
                
                System.out.printf("   ✓ FITNESS: %.1f%% | GENERATION: %d%n", fitness, generation);
            }
            
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.printf("  EVOLUTION COMPLETE. FINAL FITNESS: %.1f%%%n", fitness);
            System.out.println("  GENERATION: " + generation);
            System.out.println("═══════════════════════════════════════════════════════════");
            
        } catch (InterruptedException e) {
            System.out.println("⚠️ EVOLUTION INTERRUPTED");
        }
        
        running = false;
    }

    public void stop() {
        running = false;
    }

    public double getFitness() {
        return fitness;
    }

    public int getGeneration() {
        return generation;
    }

    public boolean isRunning() {
        return running;
    }
    
    /**
     * Broadcast current state to HTML arena visualization
     */
    private void broadcastState() {
        try {
            NerveCenter nerve = NerveCenter.getInstance();
            // Memory usage based on generation count (normalized)
            double memoryUsage = 10 + (generation % 20);
            nerve.broadcastOrganism("Lazarus", entropy, momentum, memoryUsage);
        } catch (Exception e) {
            // Silently fail if NerveCenter not available
        }
    }

    public static void main(String[] args) {
        new LazarusEngine().run();
    }
}
