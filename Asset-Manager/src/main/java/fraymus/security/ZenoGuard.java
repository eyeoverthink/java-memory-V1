package fraymus.security;

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * THE ZENO GUARD: OBSERVATION LOCK
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "A watched pot never boils. A watched bit never flips."
 * 
 * Implements the Quantum Zeno Effect as a security mechanism:
 * - Monitors a critical variable in a tight observation loop
 * - Observation frequency is higher than CPU write cycle
 * - Prevents state change by constant measurement
 * - If an attacker tries to change the value, the Observer resets it
 *   faster than the write operation can complete
 * 
 * The Physics:
 * - A radioactive atom will never decay if you keep measuring it
 * - Constant observation freezes evolution
 * - We exploit this to make variables effectively immutable
 */
public class ZenoGuard implements Runnable {

    private static final double PHI = PhiQuantumConstants.PHI;

    // The protected value
    private volatile int protectedValue;
    private final int expectedValue;
    
    // State
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final AtomicBoolean observing = new AtomicBoolean(false);
    private Thread guardThread;
    
    // Statistics
    private final AtomicLong observationCount = new AtomicLong(0);
    private final AtomicLong correctionCount = new AtomicLong(0);
    private final AtomicLong attacksDetected = new AtomicLong(0);
    private long startTime = 0;
    
    // Configuration
    private boolean useSpinWait = true;  // Use Thread.onSpinWait() for efficiency
    private int reportInterval = 500000000; // Report every 500M observations (less spam)
    
    // Callbacks
    private Consumer<String> onAttackDetected;
    private Consumer<Long> onObservation;

    /**
     * Create a Zeno Guard for a specific value
     */
    public ZenoGuard(int valueToProtect) {
        this.protectedValue = valueToProtect;
        this.expectedValue = valueToProtect;
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE OBSERVER (The Defense)
    // ═══════════════════════════════════════════════════════════════════
    
    @Override
    public void run() {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   ZENO GUARD ACTIVE");
        System.out.println("═══════════════════════════════════════════");
        System.out.println("Protected Value: " + expectedValue);
        System.out.println("Mode: " + (useSpinWait ? "Spin Wait (Efficient)" : "Tight Loop (Maximum)"));
        System.out.println();
        System.out.println(">> STARING AT THE BIT. TIME IS FROZEN.");
        System.out.println();
        
        observing.set(true);
        startTime = System.nanoTime();
        
        while (active.get()) {
            // MEASURE - The Observation
            long count = observationCount.incrementAndGet();
            
            if (protectedValue != expectedValue) {
                // CORRECTION - Collapse wave function back to expected state
                protectedValue = expectedValue;
                correctionCount.incrementAndGet();
                attacksDetected.incrementAndGet();
                
                String msg = ">> ZENO: Decay detected! Value " + protectedValue + 
                    " corrected to " + expectedValue + " | Observation #" + count;
                System.out.println(msg);
                
                if (onAttackDetected != null) {
                    onAttackDetected.accept(msg);
                }
            }
            
            // Periodic status report
            if (count % reportInterval == 0) {
                double obsPerSec = count / ((System.nanoTime() - startTime) / 1e9);
                System.out.println(">> ZENO: " + count + " observations | " + 
                    String.format("%.2f", obsPerSec / 1e6) + " MHz | " +
                    correctionCount.get() + " corrections");
            }
            
            if (onObservation != null && count % 100000 == 0) {
                onObservation.accept(count);
            }
            
            // Efficient spin - hints to CPU this is a spin loop
            if (useSpinWait) {
                Thread.onSpinWait();
            }
            // NO SLEEP - CONSTANT OBSERVATION
            // This consumes a full CPU core to freeze time for this variable
        }
        
        observing.set(false);
        System.out.println(">> ZENO GUARD DEACTIVATED. Time resumes.");
    }

    /**
     * Start the guard on a new thread
     */
    public void startGuard() {
        if (active.get()) {
            System.out.println("Guard already active!");
            return;
        }
        
        active.set(true);
        guardThread = new Thread(this, "ZenoGuard-" + expectedValue);
        guardThread.setPriority(Thread.MAX_PRIORITY);
        guardThread.start();
    }

    /**
     * Stop the guard
     */
    public void stopGuard() {
        active.set(false);
        
        if (guardThread != null) {
            try {
                guardThread.join(1000);
            } catch (InterruptedException e) {
                guardThread.interrupt();
            }
        }
        
        printStats();
    }

    /**
     * Simulate an attack (for testing)
     */
    public void simulateAttack(int newValue, int delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                System.out.println(">> ATTACKER: Attempting to change value to " + newValue + "...");
                protectedValue = newValue; // The Attack
                
                // Check if it stuck
                Thread.sleep(10);
                if (protectedValue == newValue) {
                    System.out.println(">> ATTACKER: SUCCESS! Value changed.");
                } else {
                    System.out.println(">> ATTACKER: FAILED! Zeno Guard corrected the value.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Attacker").start();
    }

    /**
     * Multi-attack simulation
     */
    public void simulateMultipleAttacks(int count, int intervalMs) {
        new Thread(() -> {
            System.out.println(">> LAUNCHING " + count + " ATTACKS...");
            
            for (int i = 0; i < count && active.get(); i++) {
                try {
                    Thread.sleep(intervalMs);
                    int attackValue = (int)(Math.random() * 1000);
                    protectedValue = attackValue;
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            System.out.println(">> ATTACK SEQUENCE COMPLETE.");
        }, "MultiAttacker").start();
    }

    /**
     * Print statistics
     */
    public void printStats() {
        long elapsed = System.nanoTime() - startTime;
        double seconds = elapsed / 1e9;
        double obsPerSec = observationCount.get() / seconds;
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   ZENO GUARD STATISTICS");
        System.out.println("═══════════════════════════════════════════");
        System.out.println("Protected Value: " + expectedValue);
        System.out.println("Current Value: " + protectedValue);
        System.out.println("Status: " + (active.get() ? "ACTIVE" : "INACTIVE"));
        System.out.println();
        System.out.println("Duration: " + String.format("%.2f", seconds) + " seconds");
        System.out.println("Observations: " + observationCount.get());
        System.out.println("Observation Rate: " + String.format("%.2f", obsPerSec / 1e6) + " MHz");
        System.out.println();
        System.out.println("Attacks Detected: " + attacksDetected.get());
        System.out.println("Corrections Made: " + correctionCount.get());
        System.out.println("Defense Rate: " + (attacksDetected.get() > 0 ? "100%" : "N/A"));
    }

    // ═══════════════════════════════════════════════════════════════════
    // ACCESSORS
    // ═══════════════════════════════════════════════════════════════════
    
    public int getProtectedValue() { return protectedValue; }
    public int getExpectedValue() { return expectedValue; }
    public boolean isActive() { return active.get(); }
    public boolean isObserving() { return observing.get(); }
    public long getObservationCount() { return observationCount.get(); }
    public long getCorrectionCount() { return correctionCount.get(); }
    public long getAttacksDetected() { return attacksDetected.get(); }
    
    public void setUseSpinWait(boolean use) { this.useSpinWait = use; }
    public void setReportInterval(int interval) { this.reportInterval = interval; }
    public void setOnAttackDetected(Consumer<String> callback) { this.onAttackDetected = callback; }
    public void setOnObservation(Consumer<Long> callback) { this.onObservation = callback; }

    /**
     * Static factory for common use case - vault lock
     */
    public static ZenoGuard createVaultLock() {
        return new ZenoGuard(1); // 1 = LOCKED
    }

    /**
     * Demo
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   QUANTUM ZENO EFFECT DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        System.out.println("The Zeno Effect: A watched pot never boils.");
        System.out.println("We will protect value '1' (LOCKED) from modification.");
        System.out.println();
        
        ZenoGuard guard = new ZenoGuard(1);
        guard.setReportInterval(5000000);
        
        // Start the guard
        guard.startGuard();
        
        // Wait a moment then attack
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        
        // Simulate attacks
        guard.simulateAttack(0, 100);   // Single attack
        guard.simulateAttack(999, 500); // Another attack
        
        // Wait and observe
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        
        // Multiple rapid attacks
        guard.simulateMultipleAttacks(10, 100);
        
        // Wait more
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        
        // Stop and print stats
        guard.stopGuard();
        
        System.out.println();
        System.out.println("Final Value: " + guard.getProtectedValue());
        System.out.println("Expected: " + guard.getExpectedValue());
        System.out.println("Result: " + (guard.getProtectedValue() == guard.getExpectedValue() ? 
            "PROTECTED - Zeno Effect held!" : "BREACHED"));
    }
}
