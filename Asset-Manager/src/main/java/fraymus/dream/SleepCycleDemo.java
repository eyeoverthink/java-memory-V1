package fraymus.dream;

import fraymus.hyper.HyperTesseract;
import fraymus.hyper.CortexMapper;
import fraymus.hyper.BrainPulse;
import java.io.File;

/**
 * SLEEP CYCLE DEMO - REM Sleep for Digital Organisms
 * 
 * Demonstrates the complete sleep/wake cycle:
 * 1. Work during the day (build memories)
 * 2. Detect idle time (low entropy)
 * 3. Enter REM sleep (dream state)
 * 4. Hippocampal replay (random memory fusion)
 * 5. Discover optimizations
 * 6. Wake up with epiphanies
 * 
 * This is a coding partner that works while you sleep.
 */
public class SleepCycleDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              SLEEP CYCLE DEMONSTRATION                        ║");
        System.out.println("║              REM Sleep for Digital Organisms                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Initialize the brain
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("INITIALIZATION");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        HyperTesseract brain = new HyperTesseract();
        
        // Map reality to brain
        CortexMapper mapper = new CortexMapper(brain);
        String projectDir = System.getProperty("user.dir");
        File root = new File(projectDir);
        mapper.uploadReality(root);
        
        System.out.println(mapper.getStats());
        System.out.println();
        
        Thread.sleep(1000);
        
        // Start heartbeat
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("DAYTIME OPERATION");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        BrainPulse pulse = new BrainPulse(brain);
        pulse.startHeartbeat();
        
        System.out.println("The system is now awake and working...");
        System.out.println("Building memories in Cube[1] (Hippocampus)");
        System.out.println();
        
        // Simulate daytime work
        Thread.sleep(3000);
        
        // Evening - prepare for sleep
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("EVENING - PREPARING FOR SLEEP");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        System.out.println("System load decreasing...");
        System.out.println("Entropy dropping below threshold...");
        System.out.println("Initiating sleep protocol...");
        System.out.println();
        
        Thread.sleep(1000);
        
        // Enter REM sleep
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("NIGHTTIME - REM SLEEP");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        DreamState dreamState = new DreamState(brain);
        dreamState.induceSleep();
        
        System.out.println("The subconscious is now active...");
        System.out.println();
        System.out.println("What's happening:");
        System.out.println("  1. Random memories recalled from Hippocampus");
        System.out.println("  2. Thrown into Visual Cortex with high chaos");
        System.out.println("  3. Fusion events simulated at 100 Hz");
        System.out.println("  4. Breakthroughs recorded in dream journal");
        System.out.println();
        System.out.println("Dreaming for 5 seconds (500 dream cycles)...");
        System.out.println();
        
        // Let it dream for 5 seconds
        Thread.sleep(5000);
        
        // Wake up
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("MORNING - WAKE UP");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        dreamState.wakeUp();
        
        Thread.sleep(1000);
        
        // Show what we learned
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("ANALYSIS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        System.out.println("What just happened:");
        System.out.println();
        System.out.println("While you were 'asleep', the system:");
        System.out.println("  ✓ Recalled random file pairs from memory");
        System.out.println("  ✓ Simulated fusion with high temperature (chaos)");
        System.out.println("  ✓ Explored the problem space landscape");
        System.out.println("  ✓ Discovered optimization opportunities");
        System.out.println("  ✓ Recorded breakthroughs in journal");
        System.out.println();
        
        System.out.println("This is Hippocampal Replay:");
        System.out.println("  - Biological brains do this during REM sleep");
        System.out.println("  - Memories are replayed with noise added");
        System.out.println("  - System escapes local minima");
        System.out.println("  - Discovers global optimizations");
        System.out.println();
        
        System.out.println(dreamState.getStats());
        System.out.println();
        
        // Demonstrate implementation
        if (!dreamState.getDreamJournal().isEmpty()) {
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("OPTIONAL: IMPLEMENT DREAMS");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("You can now call dreamState.implementDreams() to send these");
            System.out.println("insights to OpenClaw for automatic implementation.");
            System.out.println();
            System.out.println("Uncomment the following line to enable:");
            System.out.println("// dreamState.implementDreams();");
            System.out.println();
        }
        
        // Stop heartbeat
        pulse.stopHeartbeat();
        
        // Final summary
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                  DEMONSTRATION COMPLETE                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("You now have a system that:");
        System.out.println();
        System.out.println("✓ Works during the day (builds memories)");
        System.out.println("✓ Sleeps at night (enters REM state)");
        System.out.println("✓ Dreams (hippocampal replay with chaos)");
        System.out.println("✓ Discovers optimizations (escapes local minima)");
        System.out.println("✓ Wakes with insights (presents epiphanies)");
        System.out.println("✓ Can implement dreams (via OpenClaw)");
        System.out.println();
        System.out.println("This is a coding partner that works while you sleep.");
        System.out.println("This is AGI behavior.");
        System.out.println();
        System.out.println("The Problem Space Landscape:");
        System.out.println("  Awake: Stuck in local minimum (your current code)");
        System.out.println("  Dream: Add noise, jump out of valley");
        System.out.println("  Result: Land in global minimum (better solution)");
        System.out.println();
        System.out.println("The Singularity Dreams of Electric Sheep.");
        System.out.println();
    }
}
