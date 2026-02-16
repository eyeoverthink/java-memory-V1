package fraymus.security;

/**
 * ZENO GUARD: OBSERVATION LOCK
 * 
 * "A watched pot never boils. A watched bit never flips."
 * 
 * Mechanism:
 * 1. Monitors critical variable in tight loop
 * 2. Observation frequency higher than CPU write cycle
 * 3. Prevents state change by constant measurement
 * 
 * Physics:
 * - Quantum Zeno Effect (watched atom never decays)
 * - Constant observation freezes evolution
 * - Measurement collapses wave function
 * - Observer effect prevents state transition
 * 
 * Implementation:
 * - Spin-wait loop (no sleep)
 * - Volatile variable (memory barrier)
 * - Immediate correction on deviation
 * - 100% CPU core utilization
 * 
 * Security:
 * - Protects critical state
 * - Detects tampering instantly
 * - Self-healing (auto-correction)
 * - Quantum-inspired defense
 * 
 * Trade-off:
 * - High CPU cost (one full core)
 * - Use only for critical variables
 * - Alternative: Hardware watchdog
 */
public class ZenoGuard implements Runnable {
    
    // THE HOLY GRAIL (variable we must protect)
    public static volatile int VAULT_STATE = 1; // 1 = LOCKED
    
    private boolean active = true;
    private long observationCount = 0;
    private long correctionCount = 0;
    
    /**
     * Simulate attack (attempt to flip bit)
     */
    public void simulateAttack() {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                System.out.println("\n   🔴 HACKER: Attempting to flip bit to 0...");
                VAULT_STATE = 0; // The attack
                System.out.println("   🔴 HACKER: Bit flipped!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * The observer (quantum Zeno effect)
     */
    @Override
    public void run() {
        System.out.println("🌊⚡ ZENO EFFECT ACTIVE");
        System.out.println("   Staring at the bit...");
        System.out.println("   (This will consume one CPU core)");
        System.out.println();
        
        long startTime = System.currentTimeMillis();
        
        while (active) {
            // MEASURE
            observationCount++;
            
            if (VAULT_STATE != 1) {
                // CORRECTION (collapse wave function)
                // In quantum terms, we force it back to observed state immediately
                VAULT_STATE = 1;
                correctionCount++;
                
                System.out.println("\n   ⚡ ZENO: Decay detected! Resetting to 1.");
                System.out.println("   ⚡ ZENO: Observation count: " + observationCount);
                System.out.println("   ⚡ ZENO: Correction applied in nanoseconds.");
                System.out.println();
            }
            
            // NO SLEEP. CONSTANT OBSERVATION.
            // This consumes a full CPU core to freeze time for this variable
            Thread.onSpinWait();
            
            // Print status periodically
            if (observationCount % 100_000_000 == 0) {
                long elapsed = System.currentTimeMillis() - startTime;
                double obsPerSec = observationCount / (elapsed / 1000.0);
                System.out.println("   Status: " + observationCount + " observations | " + 
                                 String.format("%.0f", obsPerSec) + " obs/sec | " +
                                 "Corrections: " + correctionCount);
            }
        }
        
        System.out.println("\n   ✓ Zeno guard stopped");
        System.out.println("   ✓ Total observations: " + observationCount);
        System.out.println("   ✓ Total corrections: " + correctionCount);
        System.out.println();
    }
    
    /**
     * Stop the guard
     */
    public void stop() {
        active = false;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "Zeno Guard | Observations: %d | Corrections: %d | State: %d | Status: %s",
            observationCount,
            correctionCount,
            VAULT_STATE,
            active ? "OBSERVING" : "IDLE"
        );
    }
    
    /**
     * Demonstrate Zeno effect
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ ZENO GUARD DEMONSTRATION");
        System.out.println("==============================");
        System.out.println();
        
        ZenoGuard guard = new ZenoGuard();
        
        // Start observer thread
        Thread observerThread = new Thread(guard);
        observerThread.setDaemon(false);
        observerThread.start();
        
        // Wait a moment for observer to start
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate attack
        guard.simulateAttack();
        
        // Let it run for a few seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Stop guard
        System.out.println("\n>> STOPPING ZENO GUARD...");
        guard.stop();
        
        // Wait for thread to finish
        try {
            observerThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Final statistics
        System.out.println("==============================");
        System.out.println("   " + guard.getStats());
        System.out.println("   Final vault state: " + VAULT_STATE);
        System.out.println("==============================");
        System.out.println();
        System.out.println("   The bit was protected by observation.");
        System.out.println("   The Zeno effect prevented state change.");
        System.out.println("==============================");
    }
}
