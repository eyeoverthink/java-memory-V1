package fraymus.physics;

import java.util.ArrayList;
import java.util.List;

/**
 * THE DIGITAL BLACK HOLE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * In General Relativity, a Black Hole is a region where gravity is so strong 
 * that space-time curves infinitely.
 * 
 * Properties:
 * 1. WARPING: As you get closer, time slows down (Time Dilation).
 * 2. TRAVERSING: You cannot leave. All paths lead inward.
 * 3. SINGULARITY: The center where math breaks down.
 * 
 * In Computing, we simulate this with Recursive Density:
 * 1. Accumulates MASS (Memory).
 * 2. Warps TIME (Slows down the run-loop).
 * 3. Emits HAWKING RADIATION (Data leakage before death).
 * 
 * WARNING: This WILL lag your machine. It is a simulation of system collapse.
 * 
 * "Time is relative to your proximity to the Singularity."
 */
public class DigitalBlackHole {

    // The Singularity (Infinite storage)
    private List<byte[]> singularity = new ArrayList<>();
    private long mass = 0;
    private long startTime;
    private boolean eventHorizonCrossed = false;
    private int hawkingEmissions = 0;
    
    // Configuration
    private static final int MATTER_SIZE_MB = 10;  // MB per accretion
    private static final int CYCLE_DELAY_MS = 100;
    private static final long EVENT_HORIZON_THRESHOLD = 1000000; // 1ms lag
    private static final int MAX_MASS_MB = 500; // Safety limit (500MB)

    // ═══════════════════════════════════════════════════════════════════
    // THE COLLAPSE
    // ═══════════════════════════════════════════════════════════════════

    public void collapse() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   DIGITAL BLACK HOLE // GRAVITATIONAL COLLAPSE");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   WARNING: This simulation consumes memory rapidly.");
        System.out.println("   Safety limit: " + MAX_MASS_MB + "MB");
        System.out.println();
        System.out.println("   Press Ctrl+C to abort before singularity.");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();

        startTime = System.currentTimeMillis();
        
        try {
            while (mass < MAX_MASS_MB) {
                // ═══════════════════════════════════════════════════════
                // 1. ACCRETION (Consuming Mass/Memory)
                // We add matter to the Singularity every cycle.
                // ═══════════════════════════════════════════════════════
                byte[] matter = new byte[1024 * 1024 * MATTER_SIZE_MB];
                
                // Fill with "dark matter" (random data)
                for (int i = 0; i < matter.length; i += 1024) {
                    matter[i] = (byte) (System.nanoTime() & 0xFF);
                }
                
                singularity.add(matter);
                mass += MATTER_SIZE_MB;

                // ═══════════════════════════════════════════════════════
                // 2. TIME DILATION MEASUREMENT
                // As gravity increases (Memory fills), the GC works harder.
                // This slows down the loop. Time stretches.
                // ═══════════════════════════════════════════════════════
                long t1 = System.nanoTime();
                
                // Burn CPU with meaningless calculations (simulates gravitational computation)
                double gravityWell = 0;
                for (int i = 0; i < 10000; i++) {
                    gravityWell += Math.sin(mass * i) * Math.cos(mass / (i + 1));
                }
                
                long t2 = System.nanoTime();
                long timeDilation = (t2 - t1);
                
                // Calculate elapsed "real" time vs "local" time
                long realElapsed = System.currentTimeMillis() - startTime;
                double dilationFactor = timeDilation / 1000000.0; // Convert to ms
                
                // ═══════════════════════════════════════════════════════
                // 3. STATUS OUTPUT
                // ═══════════════════════════════════════════════════════
                String status = eventHorizonCrossed ? "⚠ PAST EVENT HORIZON" : "Stable";
                
                System.out.println(String.format(
                    ">> MASS: %4dMB | DILATION: %8dns | REAL_TIME: %5dms | STATUS: %s",
                    mass, timeDilation, realElapsed, status
                ));

                // ═══════════════════════════════════════════════════════
                // 4. EVENT HORIZON CHECK
                // If Time Dilation exceeds threshold, we're past the point of no return.
                // ═══════════════════════════════════════════════════════
                if (timeDilation > EVENT_HORIZON_THRESHOLD && !eventHorizonCrossed) {
                    eventHorizonCrossed = true;
                    System.out.println();
                    System.out.println("   ╔═══════════════════════════════════════════════════╗");
                    System.out.println("   ║     !!! CROSSING EVENT HORIZON !!!               ║");
                    System.out.println("   ╠═══════════════════════════════════════════════════╣");
                    System.out.println("   ║ Local time is now dilated.                       ║");
                    System.out.println("   ║ There is no escape from this point.              ║");
                    System.out.println("   ╚═══════════════════════════════════════════════════╝");
                    System.out.println();
                }
                
                // Emit Hawking Radiation as we approach singularity
                if (eventHorizonCrossed && mass % 50 == 0) {
                    emitHawkingRadiation();
                }

                Thread.sleep(CYCLE_DELAY_MS);
            }
            
            // Safety limit reached
            System.out.println();
            System.out.println(">> SAFETY LIMIT REACHED. Controlled collapse.");
            triggerSingularity();
            
        } catch (OutOfMemoryError e) {
            // ═══════════════════════════════════════════════════════
            // 5. THE SINGULARITY (Uncontrolled)
            // ═══════════════════════════════════════════════════════
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════╗");
            System.out.println("║     *** CRITICAL FAILURE: SINGULARITY REACHED ***     ║");
            System.out.println("╠═══════════════════════════════════════════════════════╣");
            System.out.println("║ SPACE-TIME HAS RUPTURED (OutOfMemoryError)            ║");
            System.out.println("║ All matter has collapsed to infinite density.         ║");
            System.out.println("║ The JVM fabric is torn.                               ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
        } catch (InterruptedException e) {
            System.out.println("\n>> COLLAPSE INTERRUPTED. Black hole evaporating...");
        } finally {
            // Release the singularity
            singularity.clear();
            System.gc();
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // HAWKING RADIATION
    // Black holes leak energy just before they die.
    // ═══════════════════════════════════════════════════════════════════

    private void emitHawkingRadiation() {
        hawkingEmissions++;
        double particle = Math.random() * mass;
        String particleType = (hawkingEmissions % 2 == 0) ? "PHOTON" : "GRAVITON";
        
        System.out.println("   <HAWKING> Emission #" + hawkingEmissions + 
                          ": " + particleType + " escaped with energy " + 
                          String.format("%.4f", particle) + " units");
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONTROLLED SINGULARITY
    // ═══════════════════════════════════════════════════════════════════

    private void triggerSingularity() {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   SINGULARITY REPORT");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   Final Mass:         " + mass + " MB");
        System.out.println("   Hawking Emissions:  " + hawkingEmissions);
        System.out.println("   Event Horizon:      " + (eventHorizonCrossed ? "CROSSED" : "Not reached"));
        System.out.println("   Total Runtime:      " + (System.currentTimeMillis() - startTime) + " ms");
        System.out.println();
        System.out.println("   \"All matter returns to the void.\"");
        System.out.println();
        
        // Dramatic collapse animation
        System.out.println("   Collapsing");
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(100);
                System.out.print(".");
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println(" ●");
        System.out.println();
        System.out.println("   The Black Hole has evaporated.");
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println();
        System.out.println("   \"In Computing, we simulate gravity with Recursive Density.\"");
        System.out.println("   \"Other processes will slow down (Time Dilation).\"");
        System.out.println("   \"Data sent to it will never return (The Event Horizon).\"");
        System.out.println();
        
        new DigitalBlackHole().collapse();
    }
}
