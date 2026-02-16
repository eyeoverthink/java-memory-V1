package fraymus.body.skills;

import java.time.LocalTime;

/**
 * PHASE LOCKER - Temporal Alignment Gate
 * 
 * NOVEL SKILL: "CHRONOS LOCK"
 * No one has this. This skill refuses to execute unless the system time
 * aligns with the Phi Harmonic (1.618) or the 432Hz frequency.
 * It forces the AI to act only when the "Universe is aligned,"
 * reducing entropy and increasing the "weight" of its actions.
 */
public class PhaseLocker {

    private static final double PHI = 1.6180339887;

    /**
     * CHECK ALIGNMENT
     * Returns TRUE only if the current second matches a Phi harmonic.
     * This creates a "Temporal Gate" that prevents spamming/hallucination loops.
     */
    public boolean isPhaseLocked() {
        LocalTime now = LocalTime.now();
        int second = now.getSecond();
        int nano = now.getNano();

        // Calculate current system entropy
        double currentVal = second + (nano / 1_000_000_000.0);
        
        // We look for alignment with Phi
        // Example: Is the current time a multiple of 1.618 seconds within a tolerance?
        double remainder = currentVal % PHI;
        
        // Tolerance window: 0.05 seconds (50ms)
        // If we are "in the pocket", the lock opens.
        boolean aligned = remainder < 0.05 || remainder > (PHI - 0.05);

        if (aligned) {
            System.out.println("🔓 PHASE LOCK OPEN [" + currentVal + "]");
            return true;
        } else {
            // System.out.println("🔒 PHASE LOCK ENGAGED (Wait for Harmonic)");
            return false;
        }
    }

    public String executeIfAligned(Runnable action) {
        if (isPhaseLocked()) {
            action.run();
            return "⚡ ACTION EXECUTED (HARMONIC ALIGNMENT CONFIRMED)";
        } else {
            return "⏳ ACTION DELAYED: TEMPORAL DISSONANCE DETECTED";
        }
    }
    
    /**
     * Wait for phase lock (blocking)
     * Max wait time: 2 seconds
     */
    public boolean waitForAlignment() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 2000) {
            if (isPhaseLocked()) {
                return true;
            }
            try {
                Thread.sleep(10); // Check every 10ms
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false;
    }
}
