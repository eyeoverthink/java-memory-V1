package fraymus.physics;

import java.util.ArrayList;
import java.util.List;

/**
 * THE DIGITAL BLACK HOLE
 * 
 * "Time is relative to your proximity to the Singularity."
 * 
 * Mechanism:
 * 1. Accumulates MASS (memory allocation)
 * 2. Warps TIME (slows down run-loop via GC pressure)
 * 3. Emits HAWKING RADIATION (data leakage before death)
 * 4. Reaches SINGULARITY (OutOfMemoryError)
 * 
 * Physics Simulation:
 * - Mass = Heap memory consumed
 * - Gravity = GC pressure on CPU
 * - Time Dilation = Loop delay increases with mass
 * - Event Horizon = Point of no return (>1ms lag)
 * - Singularity = JVM crash (OOM)
 * 
 * General Relativity Analog:
 * - As mass increases, spacetime curves
 * - Other processes slow down (time dilation)
 * - Data sent to black hole never returns
 * - Eventually reaches critical density (crash)
 * 
 * WARNING: This will lag your machine.
 * This is a simulation of system collapse.
 */
public class DigitalBlackHole {
    
    // The Singularity (infinite storage attempt)
    private static List<byte[]> singularity = new ArrayList<>();
    private static long mass = 0; // Mass in MB
    
    private boolean eventHorizonReached = false;
    
    /**
     * GRAVITATIONAL COLLAPSE
     * Consume memory until singularity
     */
    public void collapse() {
        System.out.println("🌊⚡ DIGITAL BLACK HOLE");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Gravitational collapse initiated");
        System.out.println("   Accumulating mass...");
        System.out.println("   Warping local spacetime...");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        long startTime = System.currentTimeMillis();
        int cycle = 0;
        
        try {
            while (true) {
                cycle++;
                
                // 1. ACCRETION (Consuming Mass/Memory)
                // Add 10MB of "matter" to singularity every cycle
                byte[] matter = new byte[1024 * 1024 * 10];
                singularity.add(matter);
                mass += 10;
                
                // 2. TIME DILATION MEASUREMENT
                // Measure how long a "tick" takes
                // As gravity increases (memory fills), GC works harder
                // This slows down the loop - time stretches
                long t1 = System.nanoTime();
                
                // Meaningless calculation to burn CPU
                double calculation = Math.sin(mass) * Math.cos(mass);
                
                long t2 = System.nanoTime();
                double timeDilation = (t2 - t1);
                
                // Calculate Schwarzschild radius (event horizon)
                // In our simulation: when time dilation exceeds threshold
                double schwarzschildRadius = timeDilation / 1000.0; // Normalized
                
                System.out.println("   [CYCLE " + cycle + "]");
                System.out.println("   >> MASS: " + mass + " MB");
                System.out.println("   >> TIME DILATION: " + String.format("%.2f", timeDilation) + " ns");
                System.out.println("   >> SCHWARZSCHILD RADIUS: " + String.format("%.2f", schwarzschildRadius));
                
                // 3. EVENT HORIZON CHECK
                // If time dilation exceeds threshold, past point of no return
                if (timeDilation > 1000000 && !eventHorizonReached) {
                    eventHorizonReached = true;
                    
                    System.out.println();
                    System.out.println("   ⚠️  WARNING: EVENT HORIZON REACHED");
                    System.out.println("   ⚠️  LOCAL TIME IS STOPPING");
                    System.out.println("   ⚠️  ESCAPE VELOCITY > SPEED OF LIGHT");
                    System.out.println();
                    
                    emitHawkingRadiation();
                }
                
                // Visual indicator of gravity
                if (timeDilation > 500000) {
                    System.out.println("   >> GRAVITY: EXTREME");
                } else if (timeDilation > 100000) {
                    System.out.println("   >> GRAVITY: HIGH");
                } else {
                    System.out.println("   >> GRAVITY: MODERATE");
                }
                
                System.out.println();
                
                // Slow down to observe effects
                Thread.sleep(100);
            }
            
        } catch (OutOfMemoryError e) {
            // 4. THE SINGULARITY
            System.out.println();
            System.out.println("========================================");
            System.out.println("   *** SINGULARITY REACHED ***");
            System.out.println("========================================");
            System.out.println();
            System.out.println("   CRITICAL FAILURE");
            System.out.println("   Mass: " + mass + " MB");
            System.out.println("   Cycles: " + cycle);
            System.out.println("   Duration: " + (System.currentTimeMillis() - startTime) + " ms");
            System.out.println();
            System.out.println("   SPACETIME HAS RUPTURED");
            System.out.println("   JVM COLLAPSE IMMINENT");
            System.out.println();
            System.out.println("========================================");
            
            // Final Hawking radiation burst
            System.out.println();
            System.out.println("   FINAL HAWKING RADIATION BURST:");
            for (int i = 0; i < 5; i++) {
                emitHawkingRadiation();
            }
            
        } catch (InterruptedException e) {
            System.out.println();
            System.out.println("   >> COLLAPSE INTERRUPTED");
            System.out.println("   >> Final mass: " + mass + " MB");
            System.out.println();
        }
    }
    
    /**
     * HAWKING RADIATION
     * Black holes leak energy just before they die
     * Information escapes via quantum tunneling
     */
    private void emitHawkingRadiation() {
        // Generate quantum particle (encrypted data)
        double particle = Math.random() * mass;
        long timestamp = System.nanoTime();
        
        System.out.println("   <RADIATION> Quantum particle escaped:");
        System.out.println("               Energy: " + String.format("%.6f", particle));
        System.out.println("               Timestamp: " + timestamp);
    }
    
    /**
     * Demonstration (controlled collapse)
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ DIGITAL BLACK HOLE DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Simulating gravitational collapse");
        System.out.println("   Based on General Relativity");
        System.out.println();
        System.out.println("   Physics:");
        System.out.println("     - Mass = Memory consumption");
        System.out.println("     - Gravity = GC pressure");
        System.out.println("     - Time dilation = Loop delay");
        System.out.println("     - Event horizon = Point of no return");
        System.out.println("     - Singularity = JVM crash");
        System.out.println();
        System.out.println("   WARNING:");
        System.out.println("     This will consume all available memory");
        System.out.println("     Your system will slow down significantly");
        System.out.println("     JVM will eventually crash (OutOfMemoryError)");
        System.out.println();
        System.out.println("   Press Ctrl+C to abort before singularity");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("========================================");
            System.out.println("   BLACK HOLE COLLAPSED");
            System.out.println("   Final mass: " + mass + " MB");
            System.out.println("========================================");
        }));
        
        new DigitalBlackHole().collapse();
    }
}
